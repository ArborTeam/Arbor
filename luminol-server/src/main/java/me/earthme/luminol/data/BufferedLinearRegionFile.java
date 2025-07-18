package me.earthme.luminol.data;

import abomination.IRegionFile;
import ca.spottedleaf.concurrentutil.util.ConcurrentUtil;
import ca.spottedleaf.moonrise.patches.chunk_system.io.MoonriseRegionFileIO;
import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import me.earthme.luminol.utils.BufferedLinearRegionFileFlusher;
import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BufferedLinearRegionFile implements IRegionFile {
    private static final double SWAP_FILE_AUTO_COMPACT_PERCENT = 3.0 / 5.0; // 60 %
    private static final long SWAP_FILE_AUTO_COMPACT_SIZE = 1024 * 1024; // 1 MiB

    private static final long SWAP_FILE_SUPER_BLOCK = 0x1145141919810L;
    private static final int SWAP_FILE_HASH_SEED = 0x0721; // ～(∠・ω< )⌒★
    private static final byte SWAP_FILE_VERSION = 0x02; // ver 2.0

    private static final long MASTER_FILE_SUPER_BLOCK = -0x200812250269L;
    private static final byte MASTER_FILE_VERSION = 0x02; // ver 2.0

    private static final long LINEAR_FILE_SUPER_BLOCK = 0xc3ff13183cca9d9aL;

    private static final StandardOpenOption[] SWAP_FILE_CHANNEL_OPTIONS = new StandardOpenOption[]{
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.READ,
            StandardOpenOption.DELETE_ON_CLOSE
    };
    private static final StandardOpenOption[] TMP_FILE_CHANNEL_OPTIONS = new StandardOpenOption[]{
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE
    };

    private final Path masterFilePath;
    private final Path swapFilePath;

    private final ReadWriteLock regionObjectLock = new ReentrantReadWriteLock();
    private final XXHash32 xxHash32 = XXHashFactory.fastestInstance().hash32();
    private final Sector[] sectors = new Sector[1024];
    private long currentAcquiredIndex = this.headerSize();
    private int xxHash32Seed = SWAP_FILE_HASH_SEED;
    private FileChannel swapFileChannel;

    private final byte compressionLevel;
    private final LinearMasterFileFrameParser frameParser = new LinearMasterFileFrameParser();

    private boolean closed = false;

    private boolean beingSynced = false;
    private boolean synced = false;
    private long lastWritten = System.nanoTime();

    private static final VarHandle SYNCED_HANDLE = ConcurrentUtil.getVarHandle(BufferedLinearRegionFile.class, "synced", boolean.class);
    private static final VarHandle BEING_SYNCED_HANDLE = ConcurrentUtil.getVarHandle(BufferedLinearRegionFile.class, "beingSynced", boolean.class);
    private static final VarHandle LAST_WRITTEN_HANDLE = ConcurrentUtil.getVarHandle(BufferedLinearRegionFile.class, "lastWritten", long.class);

    private final BufferedLinearRegionFileFlusher flusher;

    public BufferedLinearRegionFile(Path masterFilePath, int compressionLevel, @NotNull BufferedLinearRegionFileFlusher flusher) throws IOException {
        this.masterFilePath = masterFilePath;
        this.swapFilePath = Path.of(this.masterFilePath.toString() + ".swp");

        this.compressionLevel = (byte) compressionLevel;

        this.initSwapFile();
        this.loadSwapDataFromMasterFile();

        this.flusher = flusher;

        this.flusher.addFile(this);
    }

    public boolean markAsBeingSynced() {
        return BEING_SYNCED_HANDLE.compareAndSet(this, false, true);
    }


    public long getLastWritten() {
        return (long) LAST_WRITTEN_HANDLE.get(this);
    }

    public boolean shouldSync() {
        return !((boolean) SYNCED_HANDLE.get(this));
    }

    public boolean softReadLock() {
        // not done close logic yet
        return this.regionObjectLock.readLock().tryLock();
    }

    public void releaseReadLock() {
        this.regionObjectLock.readLock().unlock();
    }

    public boolean isClosedRaw() {
        return this.closed;
    }

    public boolean isClosed() {
        this.regionObjectLock.readLock().lock();
        try {
            return this.closed;
        } finally {
            this.regionObjectLock.readLock().unlock();
        }
    }

    public void syncIfNeeded() throws IOException {
        // the sync operation is just coping the data from swap file to the master file
        this.regionObjectLock.readLock().lock(); // so we could acquire read lock simply so that we won't block any other read operations
        try {
            // skip if closed already
            if (this.closed) {
                return;
            }

            this.syncToMasterFile();

            BEING_SYNCED_HANDLE.set(this, false); // mark as not being synced
        } finally {
            this.regionObjectLock.readLock().unlock();
        }
    }

    private void syncToMasterFile() throws IOException {
        // prevent multiple syncs in the same time
        if (!SYNCED_HANDLE.compareAndSet(this, false, true)) {
            return;
        }

        this.frameParser.writeMainFile(this.masterFilePath);
    }

    private void loadSwapDataFromMasterFile() throws IOException {
        this.frameParser.parseMainFile(this.masterFilePath);
    }

    private void initSwapFile() throws IOException {
        this.swapFileChannel = FileChannel.open(
                this.swapFilePath,
                SWAP_FILE_CHANNEL_OPTIONS
        );

        // fill default sectors
        for (int i = 0; i < 1024; i++) {
            this.sectors[i] = new Sector(i, this.headerSize(), 0);
        }

        // load sectors
        this.readSwapFileHeaders();
    }

    private void readSwapFileHeaders() throws IOException {
        if (this.swapFileChannel.size() < this.headerSize()) {
            return;
        }

        final ByteBuffer buffer = ByteBuffer.allocate(this.headerSize());
        this.swapFileChannel.read(buffer, 0);
        buffer.flip();

        if (buffer.getLong() != SWAP_FILE_SUPER_BLOCK || buffer.get() != SWAP_FILE_VERSION) {
            throw new IOException("Invalid file format or version mismatch");
        }

        this.xxHash32Seed = buffer.getInt(); // XXHash32 seed
        this.currentAcquiredIndex = buffer.getLong(); // Acquired index

        for (Sector sector : this.sectors) {
            sector.restoreFrom(buffer);
            if (sector.hasData()) {
                // recompute if acquired index is corrupted
                this.currentAcquiredIndex = Math.max(this.currentAcquiredIndex, sector.offset + sector.length);
            }
        }
    }

    private void writeSwapFileHeaders() throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(this.headerSize());

        buffer.putLong(SWAP_FILE_SUPER_BLOCK); // Magic
        buffer.put(SWAP_FILE_VERSION); // Version
        buffer.putInt(this.xxHash32Seed); // XXHash32 seed
        buffer.putLong(this.currentAcquiredIndex); // Acquired index

        for (Sector sector : this.sectors) {
            // encode each sector
            buffer.put(sector.getEncoded());
        }

        buffer.flip();

        long offset = 0;
        while (buffer.hasRemaining()) {
            offset += this.swapFileChannel.write(buffer, offset);
        }
    }

    private int sectorSize() {
        return this.sectors.length * Sector.sizeOfSingle();
    }

    private int headerSize() {
        int result = 0;

        result += Long.BYTES; // Magic
        result += Byte.BYTES; // Version
        result += Integer.BYTES; // XXHash32 seed
        result += Long.BYTES; // Acquired index
        result += this.sectorSize(); // Sectors

        return result;
    }

    private void flushInternal() throws IOException {
        if (this.closed) {
            return;
        }

        // save headers
        this.writeSwapFileHeaders();

        long spareSize = this.swapFileChannel.size();

        spareSize -= this.headerSize();
        for (Sector sector : this.sectors) {
            spareSize -= sector.length;
        }

        long sectorSize = 0;
        for (Sector sector : this.sectors) {
            sectorSize += sector.length;
        }

        // try auto compact to clean the garbage area
        if (spareSize > SWAP_FILE_AUTO_COMPACT_SIZE && (double) spareSize > ((double) sectorSize) * SWAP_FILE_AUTO_COMPACT_PERCENT) {
            this.compactSwapFile();
        }

        if (!Files.exists(this.masterFilePath)) {
            this.syncToMasterFile();
        }
    }

    private void closeInternal() throws IOException {
        this.closed = true;
        this.flusher.removeFile(this);
        this.writeSwapFileHeaders();
        this.swapFileChannel.force(true);
        this.syncToMasterFile();
        this.swapFileChannel.close();
    }

    private void compactSwapFile() throws IOException {
        this.writeSwapFileHeaders(); // save headers for compact
        this.swapFileChannel.force(true);
        try (FileChannel tempChannel = FileChannel.open(
                new File(this.swapFilePath.toString() + ".tmp").toPath(),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.READ
        )) {
            // get the latest head in file
            final ByteBuffer headerBuffer = ByteBuffer.allocate(this.headerSize());
            this.swapFileChannel.read(headerBuffer, 0);
            headerBuffer.flip();

            long offset = 0;
            while (headerBuffer.hasRemaining()) {
                offset += tempChannel.write(headerBuffer, offset);
            }

            long offsetPointer = this.headerSize();
            tempChannel.position(offsetPointer);

            for (Sector sector : this.sectors) {
                // skip cleared or no data-contained sectors
                if (!sector.hasData()) {
                    continue;
                }

                // transfer to target
                sector.transferTo(this.swapFileChannel, tempChannel);

                // recalculate the offset and length
                final Sector newRecalculated = new Sector(sector.index, offsetPointer, sector.length);
                newRecalculated.hasData = true;

                offsetPointer += sector.length;
                this.sectors[sector.index] = newRecalculated; // update sector infos
            }

            tempChannel.force(true);
            this.currentAcquiredIndex = offsetPointer;
        }

        this.swapFileChannel.close();

        Files.move(
                new File(this.swapFilePath + ".tmp").toPath(),
                this.swapFilePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        this.reopenSwapFileChannel();
        this.writeSwapFileHeaders();
    }

    private void reopenSwapFileChannel() throws IOException {
        if (this.swapFileChannel.isOpen()) {
            this.swapFileChannel.close();
        }

        this.swapFileChannel = FileChannel.open(
                this.swapFilePath,
                SWAP_FILE_CHANNEL_OPTIONS
        );
    }

    private void writeChunkDataRaw(int chunkOrdinal, ByteBuffer chunkData) throws IOException {
        final Sector sector = this.sectors[chunkOrdinal];

        sector.store(chunkData, this.swapFileChannel);

        SYNCED_HANDLE.set(this, false); // mark as unsynced
        LAST_WRITTEN_HANDLE.set(this, System.nanoTime()); // update last written time
    }

    private @Nullable ByteBuffer readChunkDataRaw(int chunkOrdinal) throws IOException {
        final Sector sector = this.sectors[chunkOrdinal];

        if (!sector.hasData()) {
            return null;
        }

        return sector.read(this.swapFileChannel);
    }

    private void clearChunkData(int chunkOrdinal) throws IOException {
        final Sector sector = this.sectors[chunkOrdinal];

        sector.clear();

        this.writeSwapFileHeaders();

        SYNCED_HANDLE.set(this, false); // mark as unsynced
        LAST_WRITTEN_HANDLE.set(this, System.nanoTime()); // update last written time
    }

    private static int getChunkIndex(int x, int z) {
        return (x & 31) + ((z & 31) << 5);
    }

    private boolean hasData(int chunkOriginal) {
        return this.sectors[chunkOriginal].hasData();
    }

    private void writeChunk(int x, int z, @NotNull ByteBuffer data) throws IOException {
        final int chunkIndex = getChunkIndex(x, z);

        final int oldPositionOfData = data.position();
        final int xxHash32OfData = this.xxHash32.hash(data, this.xxHash32Seed);
        data.position(oldPositionOfData);

        // uncompressed length + timestamp + xxhash32
        final ByteBuffer chunkSectionBuilder = ByteBuffer.allocate(data.remaining() + 4 + 8 + 4);

        chunkSectionBuilder.putInt(data.remaining()); // Length
        chunkSectionBuilder.putLong(System.currentTimeMillis()); // Timestamp
        chunkSectionBuilder.putInt(xxHash32OfData); // xxHash32 of the original data
        chunkSectionBuilder.put(data); // Data
        chunkSectionBuilder.flip();

        this.writeChunkDataRaw(chunkIndex, chunkSectionBuilder);
    }

    private @Nullable ByteBuffer readChunk(int x, int z) throws IOException {
        final ByteBuffer data = this.readChunkDataRaw(getChunkIndex(x, z));

        if (data == null) {
            return null;
        }

        final int length = data.getInt(); // compressed length
        final long timestamp = data.getLong(); // TODO use this timestamp for something?
        final int dataXXHash32 = data.getInt(); // XXHash32 for validation

        final IOException xxHash32CheckFailedEx = this.checkXXHash32(dataXXHash32, data);
        if (xxHash32CheckFailedEx != null) {
            throw xxHash32CheckFailedEx; // prevent from loading
        }

        return data;
    }

    private @Nullable IOException checkXXHash32(long originalXXHash32, @NotNull ByteBuffer input) {
        final int oldPositionOfInput = input.position();
        final int currentXXHash32 = this.xxHash32.hash(input, this.xxHash32Seed);
        input.position(oldPositionOfInput);

        if (originalXXHash32 != currentXXHash32) {
            return new IOException("XXHash32 check failed ! Expected: " + originalXXHash32 + ",but got: " + currentXXHash32);
        }

        return null;
    }

    @Override
    public Path getPath() {
        return this.masterFilePath;
    }

    @Override
    public DataInputStream getChunkDataInputStream(@NotNull ChunkPos pos) throws IOException {
        this.regionObjectLock.readLock().lock();
        try {
            final ByteBuffer data = this.readChunk(pos.x, pos.z);

            if (data == null) {
                return null;
            }

            final byte[] baked = new byte[data.remaining()];
            data.get(baked);

            return new DataInputStream(new ByteArrayInputStream(baked));
        } finally {
            this.regionObjectLock.readLock().unlock();
        }
    }

    @Override
    public boolean doesChunkExist(@NotNull ChunkPos pos) {
        this.regionObjectLock.readLock().lock();
        try {
            return this.hasData(getChunkIndex(pos.x, pos.z));
        } finally {
            this.regionObjectLock.readLock().unlock();
        }
    }

    @Override
    public DataOutputStream getChunkDataOutputStream(ChunkPos pos) {
        return new DataOutputStream(new ChunkBufferHelper(pos));
    }

    @Override
    public void clear(@NotNull ChunkPos pos) throws IOException {
        this.regionObjectLock.writeLock().lock();
        try {
            this.clearChunkData(getChunkIndex(pos.x, pos.z));
        } finally {
            this.regionObjectLock.writeLock().unlock();
        }
    }

    @Override
    public boolean hasChunk(@NotNull ChunkPos pos) {
        this.regionObjectLock.readLock().lock();
        try {
            return this.hasData(getChunkIndex(pos.x, pos.z));
        } finally {
            this.regionObjectLock.readLock().unlock();
        }
    }

    @Override
    public void write(@NotNull ChunkPos pos, ByteBuffer buf) throws IOException {
        this.regionObjectLock.writeLock().lock();
        try {
            this.writeChunk(pos.x, pos.z, buf);
        } finally {
            this.regionObjectLock.writeLock().unlock();
        }
    }

    // MCC 的玩意,这东西也用不上给Linear了()
    @Override
    public CompoundTag getOversizedData(int x, int z) {
        return null;
    }

    @Override
    public boolean isOversized(int x, int z) {
        return false;
    }

    @Override
    public boolean recalculateHeader() {
        return false;
    }

    @Override
    public void setOversized(int x, int z, boolean oversized) {

    }
    // MCC end

    @Override
    public MoonriseRegionFileIO.RegionDataController.WriteData moonrise$startWrite(CompoundTag data, ChunkPos pos) {
        final DataOutputStream out = this.getChunkDataOutputStream(pos);

        return new MoonriseRegionFileIO.RegionDataController.WriteData(
                data, MoonriseRegionFileIO.RegionDataController.WriteData.WriteResult.WRITE,
                out, regionFile -> out.close()
        );
    }

    @Override
    public void flush() throws IOException {
        this.regionObjectLock.writeLock().lock();
        try {
            this.flushInternal();
        } finally {
            this.regionObjectLock.writeLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        this.regionObjectLock.writeLock().lock();
        try {
            this.closeInternal();
        } finally {
            this.regionObjectLock.writeLock().unlock();
        }
    }

    public class Sector {
        private final int index;
        private long offset;
        private long length;
        private boolean hasData = false;

        private Sector(int index, long offset, long length) {
            this.index = index;
            this.offset = offset;
            this.length = length;
        }

        public void transferTo(@NotNull FileChannel source, @NotNull FileChannel target) throws IOException {
            long transferred = 0;
            while (transferred < this.length) {
                transferred += source.transferTo(
                        this.offset + transferred,
                        this.length - transferred,
                        target);
            }
        }

        public @NotNull ByteBuffer read(@NotNull FileChannel channel) throws IOException {
            final ByteBuffer result = ByteBuffer.allocate((int) this.length);

            channel.read(result, this.offset);
            result.flip();

            return result;
        }

        public void store(@NotNull ByteBuffer newData, @NotNull FileChannel channel) throws IOException {
            this.hasData = true;
            this.length = newData.remaining();
            this.offset = currentAcquiredIndex;

            BufferedLinearRegionFile.this.currentAcquiredIndex += this.length;

            long offset = this.offset;
            while (newData.hasRemaining()) {
                offset += channel.write(newData, offset);
            }
        }

        private @NotNull ByteBuffer getEncoded() {
            final ByteBuffer buffer = ByteBuffer.allocate(sizeOfSingle());

            buffer.putLong(this.offset);
            buffer.putLong(this.length);
            buffer.put((byte) (this.hasData ? 1 : 0));
            buffer.flip();

            return buffer;
        }

        public void restoreFrom(@NotNull ByteBuffer buffer) {
            this.offset = buffer.getLong();
            this.length = buffer.getLong();
            this.hasData = buffer.get() == 1;

            if (this.length < 0 || this.offset < 0) {
                throw new IllegalStateException("Invalid sector data: " + this);
            }
        }

        public void clear() {
            this.hasData = false;
        }

        public boolean hasData() {
            return this.hasData;
        }

        static int sizeOfSingle() {
            //     offset + length  hasData
            return Long.BYTES * 2 + 1;
        }
    }

    private class ChunkBufferHelper extends ByteArrayOutputStream {
        private final ChunkPos pos;

        private ChunkBufferHelper(ChunkPos pos) {
            this.pos = pos;
        }

        @Override
        public void close() throws IOException {
            BufferedLinearRegionFile.this.regionObjectLock.writeLock().lock();
            try {
                ByteBuffer bytebuffer = ByteBuffer.wrap(this.buf, 0, this.count);

                BufferedLinearRegionFile.this.writeChunk(this.pos.x, this.pos.z, bytebuffer);
            } finally {
                BufferedLinearRegionFile.this.regionObjectLock.writeLock().unlock();
            }
        }
    }

    private class LinearMasterFileFrameParser {
        private void parseBufferedLinear(@NotNull DataInputStream ioStream, Path file) throws IOException {
            final byte version = ioStream.readByte();
            if (version != MASTER_FILE_VERSION)
                throw new RuntimeException("Invalid version: " + version + " in " + file);

            // Skip newestTimestamp (Long) + Compression level (Byte): Unused.
            ioStream.skipBytes(9);

            try (ZstdInputStream decompressStream = new ZstdInputStream(ioStream);
                    DataInputStream dataStream = new DataInputStream(decompressStream)) {
                for (int index = 0; index < 1024; index++) {
                    int size = dataStream.readInt(); // len

                    if (size > 0) {
                        byte[] sectorData = new byte[size];
                        dataStream.readFully(sectorData, 0, size); // data

                        final ByteBuffer sectorDataNioBuffer = ByteBuffer.wrap(sectorData);

                        BufferedLinearRegionFile.this.writeChunkDataRaw(index, sectorDataNioBuffer);
                    }
                }
            }
        }

        @Contract(value = "_ -> new", pure = true)
        public static int @NotNull [] coordinatesFromOrdinal(int chunkIndex) {
            int x = chunkIndex & 31;
            int z = (chunkIndex >> 5) & 31;
            return new int[]{x, z};
        }

        private void parseLinear(@NotNull DataInputStream ioStream, Path file) throws IOException {
            final byte version = ioStream.readByte();

            if (version != 1 && version != 2) {
                throw new IOException("Unsupported version for linear format : " + version);
            }

            // Skip newestTimestamp (Long) + Compression level (Byte) + Chunk count (Short): Unused.
            ioStream.skipBytes(11);
            // Skip chunk data len(Int)(Unused).
            ioStream.skipBytes(4);
            // Skip data hash (Long): Unused.
            ioStream.skipBytes(8);

            try (ZstdInputStream decompressedStream = new ZstdInputStream(ioStream);
                 DataInputStream bufferHelper = new DataInputStream(decompressedStream)) {
                final int[] chunkStarts = new int[1024];
                for (int i = 0; i < 1024; i++) {
                    chunkStarts[i] = bufferHelper.readInt();
                    bufferHelper.skipBytes(4); // Skip timestamps (Int): Unused.
                }

                for (int i = 0; i < 1024; i++) {
                    if (chunkStarts[i] > 0) {
                        int size = chunkStarts[i];
                        byte[] chunkData = new byte[size];
                        bufferHelper.read(chunkData);

                        final ByteBuffer chunkDataNioBuffer = ByteBuffer.wrap(chunkData);

                        final int[] posByAxis = coordinatesFromOrdinal(i);

                        final int x = posByAxis[0];
                        final int z = posByAxis[1];

                        BufferedLinearRegionFile.this.writeChunk(x, z, chunkDataNioBuffer);
                    }
                }
            }
        }

        public void parseMainFile(@NotNull Path mainFilePath) throws IOException {
            final File file = mainFilePath.toFile();

            if (!file.exists() || !file.canRead()) {
                return;
            }

            try (FileInputStream fileStream = new FileInputStream(file);
                 DataInputStream rawDataStream = new DataInputStream(fileStream)) {

                final long superBlock = rawDataStream.readLong();

                if (superBlock == MASTER_FILE_SUPER_BLOCK) {
                    parseBufferedLinear(rawDataStream, mainFilePath);
                    return;
                }

                if (superBlock == LINEAR_FILE_SUPER_BLOCK) {
                    parseLinear(rawDataStream, mainFilePath);
                    return;
                }

                throw new IOException("Unknown or unsupported super block : " + superBlock);
            }
        }

        public void writeMainFile(@NotNull Path mainFile) throws IOException {
            final Path tmpFilePath = Path.of(mainFile + ".tmp");

            long timestamp = System.currentTimeMillis();

            File tempFile = tmpFilePath.toFile();

            try (OutputStream fileStream = Files.newOutputStream(tmpFilePath, TMP_FILE_CHANNEL_OPTIONS);
                 DataOutputStream dataStream = new DataOutputStream(fileStream);
                 ZstdOutputStream zstdStream = new ZstdOutputStream(fileStream, BufferedLinearRegionFile.this.compressionLevel);
                 DataOutputStream zstdDataStream = new DataOutputStream(zstdStream)
            ) {

                dataStream.writeLong(MASTER_FILE_SUPER_BLOCK); // super block
                dataStream.writeByte(MASTER_FILE_VERSION); // version
                dataStream.writeLong(timestamp); // timestamp
                dataStream.write(BufferedLinearRegionFile.this.compressionLevel); // compression level
                dataStream.flush();

                for (int i = 0; i < 1024; i++) {
                    // read from swap file
                    final ByteBuffer chunkData = BufferedLinearRegionFile.this.readChunkDataRaw(i);

                    // not found
                    if (chunkData == null) {
                        zstdDataStream.writeInt(0);
                        continue;
                    }
                    
                    final byte[] buffer = chunkData.array();
                    // store
                    zstdDataStream.writeInt(buffer.length); // len
                    zstdDataStream.write(buffer); // data
                }

                zstdDataStream.flush();
            }

            Files.move(tempFile.toPath(), masterFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}