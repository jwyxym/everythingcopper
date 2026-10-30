# Minecraft 26.2 迁移

目标环境：Minecraft 26.2、NeoForge 26.2.0.93、Java 25。

- 铜剑、铜铲、铜镐、铜斧、铜锄、四件铜盔甲、铜马铠和铜粒改用 `minecraft:` ID。
- 铜栏杆、铜锁链、铜灯笼及其氧化、涂蜡变种，以及铜傀儡，改用原版内容。
- 旧 `everythingcopper:` 注册名通过 NeoForge 注册表别名指向原版注册名。装备的 `everythingcopper:oxidation_state` 和 `everythingcopper:waxed` 组件继续保留。
- 手持或穿戴的上述铜装备会在服务端自然氧化。氧化台同样支持原版铜装备。
- 四阶段为 `unaffected`、`exposed`、`weathered`、`oxidized`。涂蜡暂停氧化；除锈配方先除蜡，再逐级还原。
- 装备转换保留耐久、附魔、自定义名称及其他组件。未氧化时使用原版模型；氧化模型保留盔甲纹饰分支。
- 铜傀儡使用原版行为和渲染，不再替换雕刻南瓜的发射器行为。
- 模组独有的铜剪刀、矿车、铁砧、漏斗、梯子、按钮、压力板、铁轨、灵魂灯笼和氧化台继续保留。

构建配置已改为 ModDevGradle，并移除旧 Parchment 映射与 1.21.1 的 JEI/Jade 固定依赖。JEI 集成默认参与构建，API 固定为 `30.48.0.244`，JEI 本体不会被打包进模组，也不是游戏运行的必装依赖。开发环境默认加入 JEI 运行时及其 Maven 传递依赖，可用 `-Pjei_runtime=false` 关闭开发运行时。

使用构建后的模组时，在客户端安装 Minecraft 26.2 的 NeoForge 版 JEI `30.48.0.244` 及其 MezzConfig 依赖。在 JEI 中对物品使用默认按键 `R` 查询配方、`U` 查询用途。氧化台作为配方催化剂，配方页显示输入、输出及每次消耗的 100 mB 水。工作台分类另外展示装备涂蜡、除蜡和逐级除锈；这里生成的 JEI 布局不改变实际的无序合成规则。

JEI 根据氧化和涂蜡组件区分铜装备，忽略与这些配方无关的耐久和自定义名称。四阶段及涂蜡变种会加入侧栏。生成的配方展示 ID 包含原物品的命名空间、路径及氧化阶段，避免不同物品或阶段之间冲突。此处展示的是模组默认转换规则；数据包若移除或改写涂蜡、除锈配方，还需同步调整 JEI 展示。

本次仅静态检查 Java 类签名、JSON、模型、注册引用及 JEI 官方 API 源码，未运行 Gradle、编译或启动游戏。跨版本旧存档的实际加载、Mixin 注入和 JEI 游戏内展示仍需在游戏中验证。

版本与 API 依据：[NeoForge 26.1 迁移说明](https://docs.neoforged.net/primer/docs/26.1/)、[26.2 迁移说明](https://docs.neoforged.net/primer/docs/26.2/)、[26.2 官方开发模板](https://github.com/NeoForgeMDKs/MDK-26.2-ModDevGradle)、Mojang 官方 26.2 客户端类签名与资源。
