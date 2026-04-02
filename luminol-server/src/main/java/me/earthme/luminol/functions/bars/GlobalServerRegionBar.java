package me.earthme.luminol.functions.bars;

import ca.spottedleaf.moonrise.common.time.TickData;
import io.papermc.paper.threadedregions.ThreadedRegionizer;
import io.papermc.paper.threadedregions.TickRegionScheduler;
import io.papermc.paper.threadedregions.TickRegions;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.earthme.luminol.config.modules.function.RegionBarConfig;
import me.earthme.luminol.enums.EnumStatusBarDisplay;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.UUID;

public class GlobalServerRegionBar extends AbstractGlobalServerBar {
    private final ThreadLocal<DecimalFormat> ONE_DECIMAL_PLACES = ThreadLocal.withInitial(() -> new DecimalFormat("#,##0.0"));

    public void init() {
        init(RegionBarConfig.updateInterval);
    }

    public boolean isPlayerVisible(Player player) {
        return ((CraftPlayer) player).getHandle().isRegionBarVisible && super.isPlayerVisible(player);
    }

    public void setVisibilityForPlayer(Player target, boolean canSee) {
        ((CraftPlayer) target).getHandle().isRegionBarVisible = canSee;
    }

    @Override
    public boolean enabled() {
        return RegionBarConfig.regionbarEnabled;
    }

    public ScheduledTask createBossBarForPlayer(@NotNull Player apiPlayer) {
        final UUID playerUUID = apiPlayer.getUniqueId();

        return apiPlayer.getScheduler().runAtFixedRate(NULL_PLUGIN, (n) -> {
            if (checkAndRemove(apiPlayer, playerUUID)) return;

            final ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData> region = TickRegionScheduler.getCurrentRegion();
            final TickData.TickReportData reportData = region.getData().getRegionSchedulingHandle().getTickReport5s(System.nanoTime());
            final TickRegions.RegionStats regionStats = region.getData().getRegionStats();

            BossBar targetBossbar = null;
            if (RegionBarConfig.display == EnumStatusBarDisplay.BOSS_BAR) {
                targetBossbar = uuid2Bossbars.computeIfAbsent(
                        playerUUID,
                        unused -> BossBar.bossBar(Component.text(""), 0.0F, BossBar.Color.GREEN, BossBar.Overlay.NOTCHED_20)
                );

                apiPlayer.showBossBar(targetBossbar);
            }

            if (reportData != null) {
                final double utilisation = reportData.utilisation();
                final int chunkCount = regionStats.getChunkCount();
                final int playerCount = regionStats.getPlayerCount();
                final int entityCount = regionStats.getEntityCount();

                updateRegionBar(utilisation, chunkCount, playerCount, entityCount, targetBossbar, apiPlayer);
            }
        }, () -> {
            final BossBar removed = uuid2Bossbars.remove(playerUUID); // Auto clean up it

            if (removed != null) {
                apiPlayer.hideBossBar(removed);
            }
        }, 1, RegionBarConfig.updateInterval);
    }

    private void updateRegionBar(double utilisation, int chunks, int players, int entities, @NotNull BossBar bar, Player player) {
        final double utilisationPercent = utilisation * 100.0;
        final String formattedUtil = ONE_DECIMAL_PLACES.get().format(utilisationPercent);
        final Component message = MiniMessage.miniMessage().deserialize(
                RegionBarConfig.regionBarFormat,
                Placeholder.component("util", getUtilComponent(formattedUtil)),
                Placeholder.component("chunks", getChunksComponent(chunks)),
                Placeholder.component("players", getPlayersComponent(players)),
                Placeholder.component("entities", getEntitiesComponent(entities))
        );

        switch (RegionBarConfig.display) {
            case ACTION_BAR -> player.sendActionBar(message);

            case BOSS_BAR ->
                    bar.name(message).color(barColorFromUtil(utilisationPercent)).progress((float) Math.clamp(utilisation, 0, 1.0));

            case TAB_LIST -> player.sendPlayerListFooter(message);

            default -> throw new IllegalStateException();
        }
    }

    private @NotNull Component getEntitiesComponent(int entities) {
        final String content = "<text>";
        return MiniMessage.miniMessage().deserialize(content, Placeholder.parsed("text", String.valueOf(entities)));
    }

    private @NotNull Component getPlayersComponent(int players) {
        final String content = "<text>";
        return MiniMessage.miniMessage().deserialize(content, Placeholder.parsed("text", String.valueOf(players)));
    }

    private @NotNull Component getChunksComponent(int chunks) {
        final String content = "<text>";
        return MiniMessage.miniMessage().deserialize(content, Placeholder.parsed("text", String.valueOf(chunks)));
    }

    private @NotNull Component getUtilComponent(String formattedUtil) {
        final BossBar.Color colorBukkit = barColorFromUtil(Double.parseDouble(formattedUtil));
        final String colorString = colorBukkit.name();

        final String content = "<%s><text></%s>";
        final String replaced = String.format(content, colorString, colorString);

        return MiniMessage.miniMessage().deserialize(replaced, Placeholder.parsed("text", formattedUtil + "%"));
    }

    private BossBar.Color barColorFromUtil(double util) {
        if (util > 100) {
            return RegionBarConfig.utilColors.get(3);
        }

        if (util >= 70) {
            return RegionBarConfig.utilColors.get(2);
        }

        if (util >= 50) {
            return RegionBarConfig.utilColors.get(1);
        }

        return RegionBarConfig.utilColors.get(0);
    }
}