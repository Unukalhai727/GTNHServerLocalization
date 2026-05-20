package site.gtnhserverlocalization;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.util.StringTranslate;

/**
 * 翻译注入器 — 通过 Java 反射机制直接访问 {@link StringTranslate} 内部的翻译表 Map，
 * 将键值对写入其中，使服务端也能返回翻译后的文本。
 * <p>
 * 反射路径：
 * <ul>
 * <li>StringTranslate 类：{@code net.minecraft.util.StringTranslate}</li>
 * <li>单例实例字段（MCP 混淆名）：{@code field_74817_a}</li>
 * <li>翻译表 Map 字段（MCP 混淆名）：{@code field_74816_c}</li>
 * </ul>
 */
@SuppressWarnings("unchecked")
public final class TranslationInjector {

    /** 翻译表 Map&lt;String, String&gt; */
    private static Map<String, String> translationMap;

    private TranslationInjector() {}

    /**
     * 初始化反射，获取 StringTranslate 实例及其内部翻译表。
     *
     * @return 是否初始化成功
     */
    private static boolean init() {
        if (translationMap != null) {
            return true;
        }

        try {
            // 获取 StringTranslate 单例实例（MCP: field_74817_a）
            final Field instanceField = StringTranslate.class.getDeclaredField("field_74817_a");
            instanceField.setAccessible(true);
            final StringTranslate instance = (StringTranslate) instanceField.get(null);

            if (instance == null) {
                return false;
            }

            // 获取内部翻译表 Map（MCP: field_74816_c）
            final Field mapField = StringTranslate.class.getDeclaredField("field_74816_c");
            mapField.setAccessible(true);
            translationMap = (Map<String, String>) mapField.get(instance);

            return translationMap != null;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 注入单个翻译键值对。
     *
     * @param key   翻译键
     * @param value 翻译文本
     */
    public static void inject(final String key, final String value) {
        if (key == null || value == null) {
            return;
        }
        if (!init()) {
            return;
        }
        translationMap.put(key, value);
    }

}
