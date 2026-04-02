package me.earthme.luminol.functions.bars;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.earthme.luminol.config.modules.function.MembarConfig;
import me.earthme.luminol.enums.EnumStatusBarDisplay;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.UUID;

public class GlobalServerMemoryBar extends AbstractGlobalServerBar {
    public void init() {
        init(MembarConfig.updateInterval);
    }

    public boolean isPlayerVisible(Player player) {
        return ((CraftPlayer) player).getHandle().isMemBarVisible && super.isPlayerVisible(player);
    }

    public void setVisibilityForPlayer(Player target, boolean canSee) {
        ((CraftPlayer) target).getHandle().isMemBarVisible = canSee;
    }

    @Override
    public boolean enabled() {
        return MembarConfig.memoryBarEnabled;
    }

    public ScheduledTask createBossBarForPlayer(@NotNull Player apiPlayer) {
        final UUID playerUUID = apiPlayer.getUniqueId();
        return apiPlayer.getScheduler().runAtFixedRate(NULL_PLUGIN, (unused) -> {
            if (checkAndRemove(apiPlayer)) return;

            MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

            long used = heap.getUsed();
            long xmx = heap.getMax();

            BossBar targetBossbar = null;

            if (MembarConfig.display == EnumStatusBarDisplay.BOSS_BAR) {
                targetBossbar = uuid2Bossbars.computeIfAbsent(
                        playerUUID,
                        (unused1) -> BossBar.bossBar(Component.text(""), 0.0F, MembarConfig.memColors.get(3), BossBar.Overlay.NOTCHED_20)
                );

                apiPlayer.showBossBar(targetBossbar);
            }

            updateMembar(apiPlayer, targetBossbar, used, xmx);
        }, () -> {
            final BossBar removed = uuid2Bossbars.remove(playerUUID);

            if (removed != null) {
                apiPlayer.hideBossBar(removed);
            }
        }, 1, MembarConfig.updateInterval);
    }

    private void updateMembar(Player player, @NotNull BossBar bar, long used, long xmx) {
        double percent = Math.clamp((float) used / xmx, 0.0F, 1.0F);
        final Component message = MiniMessage.miniMessage().deserialize(
                MembarConfig.memBarFormat,
                Placeholder.component("used", getMemoryComponent(used, xmx)),
                Placeholder.component("available", getMaxMemComponent(xmx))
        );

        switch (MembarConfig.display) {
            case BOSS_BAR -> bar.name(message).color(barColorFromMemory(percent)).progress((float) percent);

            case ACTION_BAR -> player.sendActionBar(message);

            case TAB_LIST -> player.sendPlayerListFooter(message);

            default -> throw new IllegalStateException();
        }
    }

    private @NotNull Component getMaxMemComponent(double max) {
        final BossBar.Color colorBukkit = BossBar.Color.GREEN;
        final String colorString = colorBukkit.name();

        final String content = "<%s><text></%s>";
        final String replaced = String.format(content, colorString, colorString);

        return MiniMessage.miniMessage().deserialize(replaced, Placeholder.parsed("text", String.format("%.2f", max / (1024 * 1024))));
    }

    private @NotNull Component getMemoryComponent(long used, long max) {
        final BossBar.Color colorBukkit = barColorFromMemory(Math.clamp((float) used / max, 0.0F, 1.0F));
        final String colorString = colorBukkit.name();

        final String content = "<%s><text></%s>";
        final String replaced = String.format(content, colorString, colorString);

        return MiniMessage.miniMessage().deserialize(replaced, Placeholder.parsed("text", String.format("%.2f", (double) used / (1024 * 1024))));
    }

    private BossBar.Color barColorFromMemory(double memPercent) {
        if (memPercent == -1) {
            return MembarConfig.memColors.get(3);
        }

        if (memPercent <= 50) {
            return MembarConfig.memColors.get(0);
        }

        if (memPercent <= 70) {
            return MembarConfig.memColors.get(1);
        }

        return MembarConfig.memColors.get(2);
    }
}