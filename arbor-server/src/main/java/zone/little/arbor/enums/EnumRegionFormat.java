package zone.little.arbor.enums;

import zone.little.arbor.config.modules.function.RegionFormatConfig;
import zone.little.arbor.data.BufferedLinearRegionFile;
import zone.little.arbor.utils.IRegionCreateFunction;
import net.minecraft.world.level.chunk.storage.RegionFile;

public enum EnumRegionFormat {
    MCA("mca", (info) -> new RegionFile(info.info(), info.filePath(), info.folder(), info.sync())),
    B_LINEAR("b_linear", (info) -> new BufferedLinearRegionFile(info.filePath(), RegionFormatConfig.linearCompressionLevel, RegionFormatConfig.blinearFlusher));

    private final String argument;
    private final IRegionCreateFunction creator;

    EnumRegionFormat(String argument, IRegionCreateFunction creator) {
        this.argument = argument;
        this.creator = creator;
    }

    public IRegionCreateFunction getCreator() {
        return this.creator;
    }

    public String getArgument() {
        return this.argument;
    }
}