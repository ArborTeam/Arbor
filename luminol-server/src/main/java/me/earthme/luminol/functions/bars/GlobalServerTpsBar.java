package me.earthme.luminol.functions.bars;

import ca.spottedleaf.moonrise.common.time.TickData;
import io.papermc.paper.threadedregions.ThreadedRegionizer;
import io.papermc.paper.threadedregions.TickRegionScheduler;
import io.papermc.paper.threadedregions.TickRegions;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.earthme.luminol.config.modules.function.TpsBarConfig;
import me.earthme.luminol.enums.EnumStatusBarDisplay;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GlobalServerTpsBar extends AbstractGlobalServerBar {
    public void init() {
        init(TpsBarConfig.updateInterval);
    }

    public boolean isPlayerVisible(Player player) {
        return ((CraftPlayer) player).getHandle().isTpsBarVisible && super.isPlayerVisible(player);
    }

    public void setVisibilityForPlayer(Player target, boolean canSee) {
        ((CraftPlayer) target).getHandle().isTpsBarVisible = canSee;
    }

    @Override
    public boolean enabled() {
        return TpsBarConfig.tpsbarEnabled;
    }

    public ScheduledTask createBossBarForPlayer(@NotNull Player apiPlayer) {
        final UUID playerUUID = apiPlayer.getUniqueId();

        return apiPlayer.getScheduler().runAtFixedRate(NULL_PLUGIN, (n) -> {
            if (checkAndRemove(apiPlayer, playerUUID)) return;

            final ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData> region = TickRegionScheduler.getCurrentRegion();
            final TickData.TickReportData reportData = region.getData().getRegionSchedulingHandle().getTickReport5s(System.nanoTime());


            BossBar targetBossbar = null;

            if (TpsBarConfig.display == EnumStatusBarDisplay.BOSS_BAR) {
                targetBossbar = uuid2Bossbars.computeIfAbsent(
                        playerUUID,
                        unused -> BossBar.bossBar(Component.text(""), 0.0F, TpsBarConfig.tpsColors.get(3), BossBar.Overlay.NOTCHED_20)
                );

                apiPlayer.showBossBar(targetBossbar);
            }

            if (reportData != null) {
                final TickData.SegmentData tpsData = reportData.tpsData().segmentAll();
                final double mspt = reportData.timePerTickData().segmentAll().average() / 1.0E6;

                updateTpsBar(tpsData.average(), mspt, targetBossbar, apiPlayer);
            }
        }, () -> {
            final BossBar removed = uuid2Bossbars.remove(playerUUID); // Auto clean up it

            if (removed != null) {
                apiPlayer.hideBossBar(removed);
            }
        }, 1, TpsBarConfig.updateInterval);
    }

    private void updateTpsBar(double tps, double mspt, @NotNull BossBar bar, @NotNull Player player) {
        final Component message = MiniMessage.miniMessage().deserialize(
                TpsBarConfig.tpsBarFormat,
                Placeholder.component("tps", getTpsComponent(tps)),
                Placeholder.component("mspt", getMsptComponent(mspt)),
                Placeholder.component("ping", getPingComponent(player.getPing())),
                Placeholder.component("chunkhot", getChunkHotComponent(player.getNearbyChunkHot()))
        );

        switch (TpsBarConfig.display) {
            case ACTION_BAR -> player.sendActionBar(message);

            case BOSS_BAR ->
                    bar.name(message).color(barColorFromTps(tps)).progress((float) Math.clamp(mspt / 50, 0, (float) 1));

            case TAB_LIST -> player.sendPlayerListFooter(message);

            default -> throw new IllegalStateException();
        }
    }

    private @NotNull Component getPingComponent(int ping) {
        final BossBar.Color colorBukkit = barColorFromPing(ping);
        final String colorString = colorBukkit.name();

        final String content = "<%s><text></%s>";
        final String replaced = String.format(content, colorString, colorString);

        return MiniMessage.miniMessage().deserialize(replaced, Placeholder.parsed("text", String.valueOf(ping)));
    }

    private BossBar.Color barColorFromPing(int ping) {
        if (ping == -1) {
            return TpsBarConfig.pingColors.get(3);
        }

        if (ping <= 80) {
            return TpsBarConfig.pingColors.get(0);
        }

        if (ping <= 160) {
            return TpsBarConfig.pingColors.get(1);
        }

        return TpsBarConfig.pingColors.get(2);
    }

    private @NotNull Component getMsptComponent(double mspt) {
        final BossBar.Color colorBukkit = barColorFromMspt(mspt);
        final String colorString = colorBukkit.name();

        final String content = "<%s><text></%s>";
        final String replaced = String.format(content, colorString, colorString);

        return MiniMessage.miniMessage().deserialize(replaced, Placeholder.parsed("text", String.format("%." + TpsBarConfig.precisionOfMSPT + "f", mspt)));
    }

    private @NotNull Component getChunkHotComponent(long chunkHot) {
        final BossBar.Color colorBukkit = barColorFromChunkHot(chunkHot);
        final String colorString = colorBukkit.name();

        final String content = "<%s><text></%s>";
        final String replaced = String.format(content, colorString, colorString);

        return MiniMessage.miniMessage().deserialize(replaced, Placeholder.parsed("text", String.valueOf(chunkHot)));
    }

    private BossBar.Color barColorFromChunkHot(long chunkHot) {
        if (chunkHot == -1) {
            return TpsBarConfig.chunkHotColors.get(3);
        }

        if (chunkHot <= 300000L) {
            return TpsBarConfig.chunkHotColors.get(0);
        }

        if (chunkHot <= 500000L) {
            return TpsBarConfig.chunkHotColors.get(1);
        }

        return TpsBarConfig.chunkHotColors.get(2);
    }

    private BossBar.Color barColorFromMspt(double mspt) {
        if (mspt == -1) {
            return TpsBarConfig.tpsColors.get(3);
        }

        if (mspt <= 25) {
            return TpsBarConfig.tpsColors.get(0);
        }

        if (mspt <= 50) {
            return TpsBarConfig.tpsColors.get(1);
        }

        return TpsBarConfig.tpsColors.get(2);
    }

    private @NotNull Component getTpsComponent(double tps) {
        final BossBar.Color colorBukkit = barColorFromTps(tps);
        final String colorString = colorBukkit.name();

        final String content = "<%s><text></%s>";
        final String replaced = String.format(content, colorString, colorString);

        return MiniMessage.miniMessage().deserialize(replaced, Placeholder.parsed("text", String.format("%." + TpsBarConfig.precisionOfTPS + "f", tps)));
    }

    private BossBar.Color barColorFromTps(double tps) {
        if (tps == -1) {
            return TpsBarConfig.tpsColors.get(3);
        }

        if (tps >= 18) {
            return TpsBarConfig.tpsColors.get(0);
        }

        if (tps >= 15) {
            return TpsBarConfig.tpsColors.get(1);
        }

        return TpsBarConfig.tpsColors.get(2);
    }
}