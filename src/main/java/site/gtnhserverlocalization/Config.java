package site.gtnhserverlocalization;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 * 配置管理类，通过 Forge {@link Configuration} 读取目标语言设置。
 * 若配置文件不存在，自动生成默认配置。
 */
public final class Config {

    /** 默认语言 */
    public static final String DEFAULT_LANG = "zh_CN";

    /** 当前目标语言 */
    public static String lang = DEFAULT_LANG;

    private Config() {}

    /**
     * 从 Forge 约定的配置文件加载语言设置。
     *
     * @param configFile Forge 建议的配置文件路径
     */
    public static void load(final File configFile) {
        final Configuration config = new Configuration(configFile);
        config.load();
        lang = config.getString(
            "lang",
            Configuration.CATEGORY_GENERAL,
            DEFAULT_LANG,
            "Target language code (e.g. zh_CN, en_US)");
        if (config.hasChanged()) {
            config.save();
        }
    }
}
