package zone.little.arbor.config.modules.function;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.config.flags.HotReloadUnsupported;
import zone.little.arbor.enums.EnumConfigCategory;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ConfigClassInfo(name = "portal_rate_limit", category = EnumConfigCategory.FUNCTION)
public class PortalRateLimiterConfig implements IConfigModule {
    @ConfigInfo(name = "enable", comments = "是否在实体进入传送门时限制传送门的传送速率")
    @HotReloadUnsupported
    public static boolean enabled = false;

    @ConfigInfo(name = "maximum_portal_teleports_per_tick", comments = """
            决定在单个 tick 区域内，一个 tick 中应处理多少次传送门传送，超出后
            传送门传送将被推迟到下一个 tick

            注意：设为 -1 以使用自定义表达式""")
    @HotReloadUnsupported
    public static int maxPortalTeleportsPerTick = 200;

    @ConfigInfo(name = "maximum_portal_teleports_per_tick_expression", comments = """
            如果固定限制不够用，你可以定义自己的表达式来动态限制
            传送门速率。

            可用变量（均为当前 tick 区域的值）：e (ticking_entity_count)
                                 c (ticking_chunk_count)
                                 p (player_count)
            示例：50 * (1 + sqrt(x/1000) + c/200 + p/5)
            """)
    @HotReloadUnsupported
    public static String maxPortalTeleportsExpression = "50 * (1 + sqrt(e/1000) + c/200 + p/5)";

    // use this to prevent reallocation
    private static final String VARIABLE_TICKING_ENTITY_CONT = "e";
    private static final String VARIABLE_TICKING_CHUNK_CONT = "c";
    private static final String VARIABLE_PLAYER_CONT = "p";

    @Nullable
    public static Expression getExpressionIfConfigured() {
        if (maxPortalTeleportsPerTick != -1) {
            return null;
        }

        return new ExpressionBuilder(maxPortalTeleportsExpression)
                .variables(
                        VARIABLE_PLAYER_CONT,
                        VARIABLE_TICKING_CHUNK_CONT,
                        VARIABLE_TICKING_ENTITY_CONT
                )
                .build();
    }

    public static int computeExpression(@NotNull Expression expression, int entityCount, int chunkCount, int playerCount) {
        expression.setVariable(VARIABLE_TICKING_ENTITY_CONT, entityCount);
        expression.setVariable(VARIABLE_TICKING_CHUNK_CONT, chunkCount);
        expression.setVariable(VARIABLE_PLAYER_CONT, playerCount);

        return (int) expression.evaluate();
    }
}
