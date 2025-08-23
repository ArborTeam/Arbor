package me.earthme.luminol.config.modules.misc;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import dev.kaiijumc.kaiiju.KaiijuEntityLimits;
import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClass;

@ConfigClass
public class KaiijuEntityLimiterConfig implements IConfigModule {
    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.MISC;
    }

    @Override
    public String getBaseName() {
        return "kaiiju_entity_limiter";
    }

    @Override
    public void onLoaded(CommentedFileConfig configInstance) {
        KaiijuEntityLimits.init();
    }
}