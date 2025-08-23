package me.earthme.luminol.config.modules.fixes;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClass;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClass
public class ForceCleanupEntityBrainMemoryConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled_for_entity", comments = "When enabled, the entity's brain will clean the memory which is typed of entity and not belong to current tickregion")
    public static boolean enabledForEntity = false;
    @ConfigInfo(baseName = "enabled_for_block_pos", comments = "When enabled, the entity's brain will clean the memory which is typed of block_pos and not belong to current tickregion")
    public static boolean enabledForBlockPos = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.FIXES;
    }

    @Override
    public String getBaseName() {
        return "force_cleanup_drop_non_owned_entity_memory_module";
    }
}