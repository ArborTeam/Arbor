package me.earthme.luminol.functions.bars;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.earthme.luminol.utils.NullPlugin;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;

public abstract class AbstractGlobalServerBar {
    protected final NullPlugin NULL_PLUGIN = new NullPlugin();
    protected final Map<UUID, BossBar> uuid2Bossbars = Maps.newConcurrentMap();
    protected final Map<UUID, ScheduledTask> scheduledTasks = new HashMap<>();
    protected final Logger logger = LogUtils.getLogger();
    protected volatile ScheduledTask scannerTask = null;
    protected boolean disabled = true;

    public abstract void init();

    public void init(int i) {
        disabled = false;
        cancelBarUpdateTask();

        scannerTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(NULL_PLUGIN, unused -> {
            try {
                update();
                cleanUp();
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }
        }, 1, i);
    }

    public void cancelBarUpdateTask() {
        if (scannerTask == null || scannerTask.isCancelled()) {
            return;
        }

        scannerTask.cancel();

        for (ScheduledTask task : scheduledTasks.values()) {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
    }

    public void runUnloadTask() {
        this.disabled = true;
        for (Player player : Bukkit.getOnlinePlayers()) {
            checkAndRemove(player);
        }
    }

    public boolean checkAndRemove(Player player) {
        return checkAndRemove(player, player.getUniqueId());
    }

    public boolean checkAndRemove(Player player, UUID uuid) {
        if (!isPlayerVisible(player)) {
            final BossBar removed = uuid2Bossbars.remove(uuid);

            if (removed != null) {
                player.hideBossBar(removed);
            }
            return true;
        }
        return false;
    }

    public boolean isPlayerVisible(Player player) {
        return !disabled;
    }

    public abstract void setVisibilityForPlayer(Player target, boolean canSee);

    public abstract boolean enabled();

    private void update() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            scheduledTasks.computeIfAbsent(player.getUniqueId(), unused -> createBossBarForPlayer(player));
        }
    }

    private void cleanUp() {
        final List<UUID> toCleanUp = new ArrayList<>();

        for (Map.Entry<UUID, ScheduledTask> toCheck : scheduledTasks.entrySet()) {
            if (toCheck.getValue().isCancelled()) {
                toCleanUp.add(toCheck.getKey());
            }
        }

        for (UUID uuid : toCleanUp) {
            scheduledTasks.remove(uuid);
        }
    }

    public abstract ScheduledTask createBossBarForPlayer(@NotNull Player apiPlayer);
}