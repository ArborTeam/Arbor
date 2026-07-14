package zone.little.arbor.config.modules.function;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.config.flags.HotReloadUnsupported;
import zone.little.arbor.enums.EnumConfigCategory;

import java.security.SecureRandom;
import java.util.Base64;

@ConfigClassInfo(category = EnumConfigCategory.FUNCTION, name = "secure_seed")
public class SecureSeedConfig implements IConfigModule {
    @ConfigInfo(name = "enabled", comments = """
                     一旦启用安全种子，所有矿物和结构都会使用 1024 位种子生成，
                     而不是原版中使用的 64 位种子，从而使传统的种子破解无法实现。
            注意：如果使用 V1，它将容易受到地形高程攻击。
                     ***** 警告：如果你的旧世界也使用了安全种子，你需要保持其启用！否则会毁掉你的存档 *****""")
    @HotReloadUnsupported
    public static boolean enabled = false;

    @ConfigInfo(name = "version", comments = """
            版本 1：Blake2b（不安全，在有足够熵的情况下可在数分钟内被 GPU/ASIC 集群逆向破解）
            版本 2：Blake3 加盐值密钥派生（推荐，不可逆）
            ***** 警告：切换版本会导致区块错误！ *****""")
    @HotReloadUnsupported
    public static int version = 1;

    @ConfigInfo(name = "salt", comments = """
            用于 V2 加密操作的自动生成的 256 位盐值。
            首次启动时生成一次 - 请勿分享或修改（修改此项会导致区块错误）！
            与 Blake3 密钥哈希一起使用，使种子不可逆。""")
    @HotReloadUnsupported
    public static String salt = generateSalt();

    private static String generateSalt() {
        byte[] saltBytes = new byte[32];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }
}
