package me.earthme.luminol.config.modules.fixes;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(configAttribution = EnumConfigCategory.OPTIMIZATIONS, mainName = "force_cleanup_drop_non_owned_entity_memory_module")
public class ForceCleanupEntityBrainMemoryConfig implements IConfigModule {
    @TransformedConfig(name = "enabled_for_entity", category = {"optimizations", "enabled_for_entity"})
    @TransformedConfig(name = "enabled_for_entity", category = {"experiment", "enabled_for_entity"})
    @ConfigInfo(baseName = "enabled_for_entity", comments = "When enabled, the entity's brain will clean the memory which is typed of entity and not belong to current tickregion")
    public static boolean enabledForEntity = false;
    @TransformedConfig(name = "enabled_for_block_pos", category = {"optimizations", "enabled_for_block_pos"})
    @TransformedConfig(name = "enabled_for_block_pos", category = {"experiment", "enabled_for_block_pos"})
    @ConfigInfo(baseName = "enabled_for_block_pos", comments = "When enabled, the entity's brain will clean the memory which is typed of block_pos and not belong to current tickregion")
    public static boolean enabledForBlockPos = false;
}