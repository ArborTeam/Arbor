package me.earthme.luminol.config.modules.removed;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(configAttribution = EnumConfigCategory.REMOVED, mainName = "removed_config")
public class RemovedConfig implements IConfigModule {
    @TransformedConfig(name = "enabled", category = {"experiment", "enable_tick_command"}, transform = false)
    @TransformedConfig(name = "barrel_rows", category = {"misc", "container_expansion"}, transform = false)
    @TransformedConfig(name = "enderchest_rows", category = {"misc", "container_expansion"}, transform = false)
    @TransformedConfig(name = "disable_end_crystal_check", category = {"misc", "end_crystal"}, transform = false)
    @TransformedConfig(name = "enabled", category = {"experiment", "entity_damage_source_trace"}, transform = false)
    @TransformedConfig(name = "allow_bad_omen_trigger_raid", category = {"misc", "revert_raid_changes"}, transform = false)
    @TransformedConfig(name = "give_bad_omen_when_kill_patrol_leader", category = {"misc", "revert_raid_changes"}, transform = false)
    @TransformedConfig(name = "bad_omen_infinite", category = {"misc", "revert_raid_changes"}, transform = false)
    @TransformedConfig(name = "skip_height_check", category = {"misc", "revert_raid_changes"}, transform = false)
    @TransformedConfig(name = "skip_self_raid_check", category = {"misc", "revert_raid_changes"}, transform = false)
    @TransformedConfig(name = "revert_274911", category = {"misc", "revert_raid_changes"}, transform = false)
    @TransformedConfig(name = "enabled", category = {"experiment", "ray_tracking_entity_tracker"}, transform = false)
    @TransformedConfig(name = "skip_marker_armor_stands", category = {"experiment", "ray_tracking_entity_tracker"}, transform = false)
    @TransformedConfig(name = "check_interval_ms", category = {"experiment", "ray_tracking_entity_tracker"}, transform = false)
    @TransformedConfig(name = "tracing_distance", category = {"experiment", "ray_tracking_entity_tracker"}, transform = false)
    @TransformedConfig(name = "hitbox_limit", category = {"experiment", "ray_tracking_entity_tracker"}, transform = false)
    @ConfigInfo(baseName = "removed", comments =
            """
                    RemovedConfig redirect to here, no any function.""")
    public static boolean enabled = true;
}