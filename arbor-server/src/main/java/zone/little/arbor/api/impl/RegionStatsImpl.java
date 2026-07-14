package zone.little.arbor.api.impl;

import io.papermc.paper.threadedregions.TickRegions;
import zone.little.arbor.api.RegionStats;

public class RegionStatsImpl implements RegionStats {
    private final TickRegions.RegionStats internal;

    public RegionStatsImpl(TickRegions.RegionStats internal) {
        this.internal = internal;
    }

    @Override
    public int getEntityCount() {
        return this.internal.getEntityCount();
    }

    @Override
    public int getPlayerCount() {
        return this.internal.getPlayerCount();
    }

    @Override
    public int getChunkCount() {
        return this.internal.getChunkCount();
    }
}