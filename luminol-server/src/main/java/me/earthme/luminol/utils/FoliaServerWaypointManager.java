package me.earthme.luminol.utils;

import ca.spottedleaf.moonrise.common.util.TickThread;
import com.google.common.collect.Sets;
import me.earthme.luminol.config.modules.experiment.CommandConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.waypoints.WaypointManager;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FoliaServerWaypointManager implements WaypointManager<@NotNull WaypointTransmitter> {
    private final Set<WaypointTransmitter> waypoints = ConcurrentHashMap.newKeySet();
    private final Set<ServerPlayer> trackingPlayers = ConcurrentHashMap.newKeySet();
    private final Map<ServerPlayer, Map<WaypointTransmitter, WaypointTransmitter.Connection>> connections = new ConcurrentHashMap<>();

    public void breakAllConnections() {
        throw new UnsupportedOperationException("Unused");
    }

    public void remakeConnections(WaypointTransmitter waypoint) {
        throw new UnsupportedOperationException("Unused");

    }

    public Set<WaypointTransmitter> transmitters() {
        return this.waypoints;
    }

    @Override
    public void trackWaypoint(WaypointTransmitter waypoint) {
        if (!CommandConfig.waypointsAndWaypointCommand) {
            return;
        }

        this.waypoints.add(waypoint);

        for (ServerPlayer toCreateFor : this.trackingPlayers) {
            this.createConnection(toCreateFor, waypoint);
        }
    }

    @Override
    public void updateWaypoint(WaypointTransmitter waypoint) {
        if (!CommandConfig.waypointsAndWaypointCommand) {
            return;
        }

        if (this.waypoints.contains(waypoint)) {
            for (ServerPlayer player : this.trackingPlayers) {
                Map<WaypointTransmitter, WaypointTransmitter.Connection> connectionsOfThisPlayer = this.connections.get(player);

                if (connectionsOfThisPlayer != null) {
                    WaypointTransmitter.Connection connection = connectionsOfThisPlayer.get(waypoint);
                    if (connection != null) {
                        this.updateConnection(player, waypoint, connection);
                    } else {
                        this.createConnection(player, waypoint);
                    }
                } else {
                    this.createConnection(player, waypoint);
                }
            }
        }
    }

    @Override
    public void untrackWaypoint(WaypointTransmitter waypoint) {
        if (!CommandConfig.waypointsAndWaypointCommand) {
            return;
        }

        for (Map.Entry<ServerPlayer, Map<WaypointTransmitter, WaypointTransmitter.Connection>> connectionMapEntry : this.connections.entrySet()) {
            final Map<WaypointTransmitter, WaypointTransmitter.Connection> connectionsOfCurr = connectionMapEntry.getValue();
            final ServerPlayer ownerOfCurr = connectionMapEntry.getKey();

            final WaypointTransmitter.Connection connectionOfCurr = connectionsOfCurr.remove(waypoint);
            if (connectionOfCurr != null) {
                scheduleIfOffTarget(((Entity) ownerOfCurr), connectionOfCurr::disconnect);
            }
        }

        this.waypoints.remove(waypoint);
    }

    public void addPlayer(ServerPlayer player) {
        this.trackingPlayers.add(player);

        for (WaypointTransmitter waypointTransmitter : this.waypoints) {
            this.createConnection(player, waypointTransmitter);
        }

        scheduleIfOffTarget(((Entity) player), () -> {
            if (player.isTransmittingWaypoint()) {
                this.trackWaypoint(player);
            }
        });
    }

    public void updatePlayer(ServerPlayer player) {
        Map<WaypointTransmitter, WaypointTransmitter.Connection> connectionsOfCurr = this.connections.get(player);

        if (connectionsOfCurr == null) {
            return;
        }

        Sets.SetView<WaypointTransmitter> set = Sets.difference(this.waypoints, connectionsOfCurr.keySet());

        for (Map.Entry<WaypointTransmitter, WaypointTransmitter.Connection> entry : connectionsOfCurr.entrySet()) {
            this.updateConnection(player, entry.getKey(), entry.getValue());
        }

        for (WaypointTransmitter waypointTransmitter : set) {
            this.createConnection(player, waypointTransmitter);
        }
    }

    public void removePlayer(ServerPlayer player) {
        final Map<WaypointTransmitter, WaypointTransmitter.Connection> removedConnections = this.connections.remove(player);

        if (removedConnections != null) {
            for (WaypointTransmitter.Connection connection : removedConnections.values()) {
                connection.disconnect();
            }
        }

        this.untrackWaypoint(player);
        this.trackingPlayers.remove(player);
    }

    private static boolean isLocatorBarEnabledFor(@NotNull ServerPlayer player) {
        return player.level().getGameRules().get(GameRules.LOCATOR_BAR);
    }

    private static void scheduleIfOffTarget(WaypointTransmitter transmitter, Runnable action) {
        if (transmitter instanceof LivingEntity ent && !TickThread.isTickThreadFor(ent)) {
            ent.getBukkitEntity().taskScheduler.schedule(unused -> action.run(), null, 1L);
            return;
        }

        action.run();
    }

    private static void scheduleIfOffTarget(Entity ent, Runnable action) {
        if (!TickThread.isTickThreadFor(ent)) {
            ent.getBukkitEntity().taskScheduler.schedule(unused -> action.run(), null, 1L);
            return;
        }

        action.run();
    }

    private void createConnection(ServerPlayer player, WaypointTransmitter waypoint) {
        if (player != waypoint) {
            if (isLocatorBarEnabledFor(player)) {
                // -> waypoint
                scheduleIfOffTarget(waypoint, () -> waypoint.makeWaypointConnectionWith(player).ifPresentOrElse(connection -> {
                    final Map<WaypointTransmitter, WaypointTransmitter.Connection> connectionsOfThisPlayer = this.connections.computeIfAbsent(player, o -> new ConcurrentHashMap<>());

                    connectionsOfThisPlayer.put(waypoint, connection);

                    scheduleIfOffTarget((Entity) player, connection::connect);
                }, () -> {
                    final Map<WaypointTransmitter, WaypointTransmitter.Connection> connectionsOfThisPlayer = this.connections.get(player);

                    if (connectionsOfThisPlayer != null) {
                        WaypointTransmitter.Connection removedConnection = connectionsOfThisPlayer.remove(waypoint);
                        if (removedConnection != null) {
                            scheduleIfOffTarget((Entity) player, removedConnection::disconnect);
                        }
                    }
                }));
            }
        }
    }

    private void updateConnection(ServerPlayer player, WaypointTransmitter waypoint, WaypointTransmitter.Connection connection) {
        if (player != waypoint) {
            if (isLocatorBarEnabledFor(player)) {
                scheduleIfOffTarget((Entity) player, () -> {
                    if (!connection.isBroken()) {
                        connection.update();
                    } else {
                        scheduleIfOffTarget(waypoint, () -> {
                            var ref = new Object() {
                                boolean connectionOrDisconnect = true;
                                WaypointTransmitter.Connection target;
                            };

                            waypoint.makeWaypointConnectionWith(player).ifPresentOrElse(connection1 -> {
                                final Map<WaypointTransmitter, WaypointTransmitter.Connection> connectionsOfThisPlayer = this.connections.computeIfAbsent(player, o -> new ConcurrentHashMap<>());

                                connectionsOfThisPlayer.put(waypoint, connection1);


                                ref.target = connection1;
                                ref.connectionOrDisconnect = true;
                            }, () -> {
                                final Map<WaypointTransmitter, WaypointTransmitter.Connection> connectionsOfThisPlayer = this.connections.get(player);

                                if (connectionsOfThisPlayer != null) {
                                    connectionsOfThisPlayer.remove(waypoint);
                                }

                                ref.target = connection;
                                ref.connectionOrDisconnect = false;
                            });

                            scheduleIfOffTarget((Entity) player, () -> {
                                if (ref.connectionOrDisconnect) {
                                    ref.target.connect();
                                    return;
                                }

                                ref.target.disconnect();
                            });
                        });
                    }
                });
            }
        }
    }
}
