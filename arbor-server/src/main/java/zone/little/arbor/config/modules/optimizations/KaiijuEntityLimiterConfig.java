package zone.little.arbor.config.modules.optimizations;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import dev.kaiijumc.kaiiju.KaiijuEntityLimits;
import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.enums.EnumConfigCategory;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@ConfigClassInfo(category = EnumConfigCategory.OPTIMIZATIONS, name = "kaiiju_entity_limiter")
public class KaiijuEntityLimiterConfig implements IConfigModule {
    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> e) {
        KaiijuEntityLimits.init();
    }
}