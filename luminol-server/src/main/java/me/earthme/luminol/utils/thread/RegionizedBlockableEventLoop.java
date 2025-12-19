package me.earthme.luminol.utils.thread;

import ca.spottedleaf.moonrise.common.util.TickThread;
import io.papermc.paper.threadedregions.RegionizedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

/**
 * We use this to simplify scheduling tasks to the correct regionized thread for folia
 *
 * @param level the level it is owned by
 */
public record RegionizedBlockableEventLoop(Level level) {

    /**
     * Check if we are on the target region thread
     *
     * @param chunkX chunkX
     * @param chunkZ chunkZ
     * @return true if we are on the target region thread
     */
    public boolean isSameThread(int chunkX, int chunkZ) {
        return TickThread.isTickThreadFor(this.level, chunkX, chunkZ);
    }

    /**
     * Check if we are on the target region thread
     *
     * @param pos the block position
     * @return true if we are on the target region thread
     */
    public boolean isSameThread(@NotNull BlockPos pos) {
        return this.isSameThread(pos.getX() >> 4, pos.getZ() >> 4);
    }

    /**
     * Create an executor for task executing on the target region thread
     *
     * @param chunkX                   chunkX
     * @param chunkZ                   chunkZ
     * @param chunkTask                if true, the tasks off-main will go to chunk task queue, otherwise tick task queue
     * @param executeImmediateIfOnMain if false, tasks will always be scheduled, even if we are on the target thread
     * @return the executor
     */
    @Contract(pure = true)
    public @NotNull Executor asExecutor(int chunkX, int chunkZ, boolean chunkTask, boolean executeImmediateIfOnMain) {
        if (executeImmediateIfOnMain) {
            return this.asExecutor(chunkX, chunkZ, chunkTask);
        }

        return task -> this.schedule(task, chunkX, chunkZ, chunkTask);
    }

    /**
     * Create an executor for task executing on the target region thread
     *
     * @param pos                      the block position
     * @param chunkTask                if true, the tasks off-main will go to chunk task queue, otherwise tick task queue
     * @param executeImmediateIfOnMain if false, tasks will always be scheduled, even if we are on the target thread
     * @return the executor
     */
    @Contract(pure = true)
    public @NotNull Executor asExecutor(@NotNull BlockPos pos, boolean chunkTask, boolean executeImmediateIfOnMain) {
        if (executeImmediateIfOnMain) {
            return this.asExecutor(pos, chunkTask);
        }

        return task -> this.schedule(task, pos, chunkTask);
    }

    /**
     * Create an executor for task executing on the target region thread which is almost acting like the vanilla BlockableEventLoop
     *
     * @param pos       the block position
     * @param chunkTask if true, the tasks off-main will go to chunk task queue, otherwise tick task queue
     * @return the executor
     */
    @Contract(pure = true)
    public @NotNull Executor asExecutor(@NotNull BlockPos pos, boolean chunkTask) {
        return task -> this.execute(task, pos, chunkTask);
    }

    /**
     * Create an executor for task executing on the target region thread which is almost acting like the vanilla BlockableEventLoop
     *
     * @param chunkX    chunkX
     * @param chunkZ    chunkZ
     * @param chunkTask if true, the tasks off-main will go to chunk task queue, otherwise tick task queue
     * @return the executor
     */
    @Contract(pure = true)
    public @NotNull Executor asExecutor(int chunkX, int chunkZ, boolean chunkTask) {
        return task -> this.execute(task, chunkX, chunkZ, chunkTask);
    }

    public void execute(Runnable task, @NotNull BlockPos pos, boolean chunkTask) {
        this.execute(task, pos.getX() >> 4, pos.getZ() >> 4, chunkTask);
    }

    public void execute(Runnable task, int chunkX, int chunkZ, boolean chunkTask) {
        if (this.isSameThread(chunkX, chunkZ)) {
            task.run();
            return;
        }

        this.schedule(task, chunkX, chunkZ, chunkTask);
    }

    public void schedule(Runnable task, @NotNull BlockPos pos, boolean chunkTask) {
        this.schedule(task, pos.getX() >> 4, pos.getZ() >> 4, chunkTask);
    }

    public void schedule(Runnable task, int chunkX, int chunkZ, boolean chunkTask) {
        if (chunkTask) {
            RegionizedServer.getInstance().taskQueue.queueChunkTask((ServerLevel) this.level, chunkX, chunkZ, task);
            return;
        }

        RegionizedServer.getInstance().taskQueue.queueTickTaskQueue((ServerLevel) this.level, chunkX, chunkZ, task);
    }
}
