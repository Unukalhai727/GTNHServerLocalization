package site.gtnhserverlocalization;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * GTNH 服务端语言本地化模组 — 让服务端也显示中文（或其他语言）。
 * <p>
 * 该模组为服务端模组，客户端无需安装。通过反射机制直接向
 * {@link net.minecraft.util.StringTranslate} 的内部翻译表注入翻译键值对。
 * <p>
 * 生命週期：
 * <ol>
 * <li>预初始化阶段：加载配置文件，确定目标语言</li>
 * <li>服务端启动阶段：依次执行 TxLoader 翻译加载与 GregTech 翻译加载，
 * 将所有翻译注入 StringTranslate</li>
 * </ol>
 */
@Mod(
    modid = GTNHServerLocalization.MODID,
    name = GTNHServerLocalization.MOD_NAME,
    version = GTNHServerLocalization.VERSION,
    acceptableRemoteVersions = "*")
public class GTNHServerLocalization {

    public static final String MODID = "serverlocalization-gtnh";
    public static final String MOD_NAME = "GTNH Server Localization";
    public static final String VERSION = Tags.VERSION;

    /**
     * 预初始化：加载配置文件，获取目标语言设置。
     */
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        Config.load(event.getSuggestedConfigurationFile());
    }

    /**
     * 服务端启动：加载并注入所有翻译。
     */
    @Mod.EventHandler
    public void serverStarting(final FMLServerStartingEvent event) {
        final String lang = Config.lang;

        // TxLoader 方式翻译
        TxLoaderTranslator.load(lang);

        // GregTech 专用翻译配置文件
        GregTechTranslator.load(lang);
    }
}
