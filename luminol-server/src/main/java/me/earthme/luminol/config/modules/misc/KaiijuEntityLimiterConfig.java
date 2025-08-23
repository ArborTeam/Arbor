package me.earthme.luminol.config.modules.misc;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import dev.kaiijumc.kaiiju.KaiijuEntityLimits;
import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;

@ConfigClassInfo(configAttribution = EnumConfigCategory.MISC, mainName = "kaiiju_entity_limiter")
public class KaiijuEntityLimiterConfig implements IConfigModule {
    @Override
    public void onLoaded(CommentedFileConfig configInstance) {
        KaiijuEntityLimits.init();
    }
}