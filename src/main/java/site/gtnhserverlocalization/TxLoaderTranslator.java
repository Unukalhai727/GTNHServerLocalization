package site.gtnhserverlocalization;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * TxLoader 方式的翻译加载器。
 * <p>
 * 扫描以下两个目录中的 .lang 文件，以 Properties 格式解析并注入翻译表：
 * <ul>
 * <li>{@code config/txloader/load/{modName}/lang/{lang}.lang}</li>
 * <li>{@code config/txloader/forceload/{modName}/lang/{lang}.lang}</li>
 * </ul>
 */
public final class TxLoaderTranslator {

    private static final String TXLOADER_BASE = "config" + File.separator + "txloader";

    private TxLoaderTranslator() {}

    /**
     * 加载 TxLoader 翻译文件并注入翻译表。
     *
     * @param lang 目标语言代码，如 {@code zh_CN}
     */
    public static void load(final String lang) {
        final File txLoaderDir = new File(TXLOADER_BASE);

        if (!txLoaderDir.exists() || !txLoaderDir.isDirectory()) {
            return;
        }

        // 遍历 load 和 forceload 目录
        processCategory(new File(txLoaderDir, "load"), lang);
        processCategory(new File(txLoaderDir, "forceload"), lang);
    }

    /**
     * 处理指定类别目录（load / forceload）下的所有模组翻译文件。
     */
    private static void processCategory(final File categoryDir, final String lang) {
        if (!categoryDir.exists() || !categoryDir.isDirectory()) {
            return;
        }

        final File[] modDirs = categoryDir.listFiles(File::isDirectory);
        if (modDirs == null) {
            return;
        }

        for (final File modDir : modDirs) {
            final File langDir = new File(modDir, "lang");
            if (!langDir.exists() || !langDir.isDirectory()) {
                continue;
            }

            final File langFile = new File(langDir, lang + ".lang");
            if (langFile.exists() && langFile.isFile()) {
                loadLangFile(langFile);
            }
        }
    }

    /**
     * 以 UTF-8 + Properties 格式读取 .lang 文件并注入翻译表。
     */
    private static void loadLangFile(final File file) {
        final Properties props = new Properties();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            props.load(reader);
        } catch (final IOException e) {
            e.printStackTrace();
            return;
        }

        for (final String key : props.stringPropertyNames()) {
            TranslationInjector.inject(key, props.getProperty(key));
        }
    }
}
