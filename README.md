# GTNH Server Localization

> Mod ID: `serverlocalization-gtnh` · Package: `site.gtnhserverlocalization`

GTNH 服务端语言本地化模组 — 让服务端也显示中文（或其他语言）。该模组为**服务端模组**，客户端无需安装。

## 核心原理

### Minecraft 的翻译系统

Minecraft 使用 `net.minecraft.util.StringTranslate` 管理所有翻译。该类内部维护一个 `Map<String, String>`（翻译键 → 翻译文本），渲染时通过键名查找对应文本。

**关键问题**：该 Map 由客户端在启动时加载语言文件填充，服务端不执行此操作，因此服务端消息（死亡提示、命令反馈等）始终为英文。

### 本模组的解决方案

通过 **Java 反射** 直接获取 `StringTranslate` 单例的内部翻译表 Map，将目标语言的键值对写入其中。

## 生命週期

| 阶段 | 事件 | 行为 |
|------|------|------|
| PreInit | `FMLPreInitializationEvent` | `Config.load()` — 读取语言配置 |
| ServerStarting | `FMLServerStartingEvent` | 依次执行 TxLoader / GregTech 翻译加载并注入 |

## 两种翻译加载方式

### 1. TxLoaderTranslator

扫描 TxLoader 目录结构，以 Java `Properties` 格式解析 `.lang` 文件：

```
config/txloader/load/{modName}/lang/{lang}.lang
config/txloader/forceload/{modName}/lang/{lang}.lang
```

例如：`config/txloader/forceload/GregTech[tectech]/lang/zh_CN.lang`

### 2. GregTechTranslator

在根目录查找 `GregTech_{lang}.lang`，该文件为 **Forge Configuration 格式**。直接通过 `net.minecraftforge.common.config.Configuration` 读取 `languagefile` category 下的所有条目并注入。

示例文件内容：
```
languagefile {
    S:"death.attack.hot"=被熔岩烧死了
    S:"gt.metaitem.01.1100.name"=锡锭
    S:gt.blockmachines.123.name=工业熔炉
}
```

## 文件结构

| 文件 | 职责 |
|------|------|
| `GTNHServerLocalization.java` | `@Mod` 入口，注册 PreInit / ServerStarting 事件 |
| `Config.java` | 通过 Forge `Configuration` API 读取语言设置，自动生成默认配置 |
| `TranslationInjector.java` | 反射获取 `StringTranslate` 内部 `Map`，提供 `inject(key, value)` |
| `TxLoaderTranslator.java` | 遍历 `config/txloader/`，以 `Properties` 解析 `.lang` 文件并注入 |
| `GregTechTranslator.java` | 以 Forge `Configuration` 解析 `GregTech_{lang}.lang` 的 `languagefile` category |

### TranslationInjector 反射路径

| 目标 | 类 | MCP 混淆名 |
|------|-----|-----------|
| 单例实例 | `net.minecraft.util.StringTranslate` | `field_74817_a` |
| 翻译表 Map | `net.minecraft.util.StringTranslate` | `field_74816_c` |

### Config 配置格式

配置文件路径由 `FMLPreInitializationEvent.getSuggestedConfigurationFile()` 确定，不存在时自动生成：

```
general {
    S:lang=zh_CN
}
```