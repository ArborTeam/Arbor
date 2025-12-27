package me.earthme.luminol.config.modules.removed;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.REMOVED, name = "removed_config")
public class RemovedConfig implements IConfigModule {
    @TransformedConfig(name = "vanilla_fluid_pushing", directory = {"fixes", "collision_behavior"}, transform = false)
    @TransformedConfig(name = "vanilla_fluid_pushing", directory = {"misc", "vanilla_fluid_pushing"}, transform = false)
    @TransformedConfig(name = "useAlternateKeepAlive", directory = {"optimizations", "alternative_keepalive_handling"}, transform = false)
    @TransformedConfig(name = "enabled", directory = {"experiment", "enable_tick_command"}, transform = false)
    @TransformedConfig(name = "barrel_rows", directory = {"misc", "container_expansion"}, transform = false)
    @TransformedConfig(name = "enderchest_rows", directory = {"misc", "container_expansion"}, transform = false)
    @TransformedConfig(name = "disable_end_crystal_check", directory = {"misc", "end_crystal"}, transform = false)
    @TransformedConfig(name = "enabled", directory = {"experiment", "entity_damage_source_trace"}, transform = false)
    @TransformedConfig(name = "allow_bad_omen_trigger_raid", directory = {"misc", "revert_raid_changes"}, transform = false)
    @TransformedConfig(name = "give_bad_omen_when_kill_patrol_leader", directory = {"misc", "revert_raid_changes"}, transform = false)
    @TransformedConfig(name = "bad_omen_infinite", directory = {"misc", "revert_raid_changes"}, transform = false)
    @TransformedConfig(name = "skip_height_check", directory = {"misc", "revert_raid_changes"}, transform = false)
    @TransformedConfig(name = "skip_self_raid_check", directory = {"misc", "revert_raid_changes"}, transform = false)
    @TransformedConfig(name = "revert_274911", directory = {"misc", "revert_raid_changes"}, transform = false)
    @TransformedConfig(name = "enabled", directory = {"experiment", "ray_tracking_entity_tracker"}, transform = false)
    @TransformedConfig(name = "skip_marker_armor_stands", directory = {"experiment", "ray_tracking_entity_tracker"}, transform = false)
    @TransformedConfig(name = "check_interval_ms", directory = {"experiment", "ray_tracking_entity_tracker"}, transform = false)
    @TransformedConfig(name = "tracing_distance", directory = {"experiment", "ray_tracking_entity_tracker"}, transform = false)
    @TransformedConfig(name = "hitbox_limit", directory = {"experiment", "ray_tracking_entity_tracker"}, transform = false)
    @ConfigInfo(name = "removed", comments =
            """
                    RemovedConfig redirect to here, no any function.""")
    public static boolean enabled = true;
}