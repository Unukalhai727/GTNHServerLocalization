package site.gtnhserverlocalization;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 * GregTech 专用的翻译加载器。
 * <p>
 * 在根目录查找 {@code GregTech_{lang}.lang}，该文件为 Forge 配置格式。
 */
public final class GregTechTranslator {

    private static final String CATEGORY_LANGUAGE_FILE = "languagefile";

    private GregTechTranslator() {}

    /**
     * 加载 GregTech 翻译文件并注入翻译表。
     *
     * @param lang 目标语言代码，如 {@code zh_CN}
     */
    public static void load(final String lang) {
        final String fileName = "GregTech_" + lang + ".lang";
        final File file = new File(fileName);

        if (!file.exists() || !file.isFile()) {
            return;
        }

        final Configuration config = new Configuration(file);
        config.load();

        for (final String key : config.getCategory(CATEGORY_LANGUAGE_FILE)
            .keySet()) {
            final String value = config.getString(key, CATEGORY_LANGUAGE_FILE, "", "");
            if (!value.isEmpty()) {
                TranslationInjector.inject(key, value);
            }
        }
    }
}
