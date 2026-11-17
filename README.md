# GregMek

> 格雷科技现代版（GTCEu）× 通用机械（Mekanism）矿石处理联动模组 —— **GTCEu 1.4.4 兼容分支**
>
> An ore-processing bridge between GregTech CEu Modern and Mekanism, ported to **GTCEu 1.4.4**.

---

## 简介 / About

本仓库基于 [MeowmelMuku/GregMek](https://github.com/MeowmelMuku/GregMek) 修改而来，把模组从 GTCEu 7.0.0 适配到 **格雷科技现代版 1.4.4**，并顺带修复了上游数据生成不完整的缺陷。

模组在 GTCEu 的材料体系里新增了 4 种材料形态和 2 种浆液流体，并把 GTCEu 的矿物处理线与 Mekanism 的压射仓、提纯仓、富集仓、粉碎机打通，形成一条从原矿到纯净粉的完整链路。

This repo is a fork of GregMek targeting GTCEu 1.4.4 on Minecraft 1.20.1 / Forge 47.x. It adds 4 material forms and 2 slurry fluids to GTCEu's material system, and links GTCEu's ore processing chain with Mekanism's Injecting / Purifying / Enriching / Crushing machines.

## 环境要求 / Requirements

| 组件 | 版本 |
| --- | --- |
| Minecraft | 1.20.1 |
| Forge | 47.4.16 或更高 |
| 格雷科技现代版（GTCEu） | **1.4.4**（限定 1.4.x 线） |
| LDLib | 1.0.28.a 或更高 |
| 通用机械（Mekanism） | **10.4.16.80**（1.20.1 最新稳定版） |

> 上面这些版本已写入 `mods.toml` 的依赖声明，版本不匹配时 Forge 会在启动阶段直接拦下，避免出现难以排查的崩溃。

## 新增内容 / What it adds

**材料形态（TagPrefix）**

| 形态 | ID 模式 | 说明 |
| --- | --- | --- |
| 污浊粉 | `dirty_%s_dust` | 碎块粉碎产物，可离心 |
| 碎块 | `%s_clump` | 提纯仓产物 |
| 碎片 | `%s_shard` | 压射仓产物 |
| 晶体 | `%s_crystal` | 浆液结晶产物 |

**流体（FluidStorageKey）**

- `gtceu:pure_slurry_<material>` —— 纯净浆液
- `gtceu:dirty_slurry_<material>` —— 污浊浆液

两者都会为**全部 110 种含矿物属性的材料**自动注册（含桶装形式）。

**处理链**

```
原矿/粗矿/矿石 ──洗矿(硫酸)──> 污浊浆液 ──化学浴(水)──> 纯净浆液 ──高压釜──> 晶体
                                                                    │
粗矿/矿石 ──压射(盐酸)──> 碎片 ──提纯(氧气)──> 碎块 ──粉碎──> 污浊粉 ──富集──> 纯净粉
```

共生成 **2971 个配方文件**，全部随仓库提交在 `src/generated/resources` 下。

## 相对上游的改动 / Changes from upstream

1. **依赖锁定**：GTCEu `7.0.0 → 1.4.4`、LDLib `1.0.40.b → 1.0.28.a`、Mekanism `10.4.9.61 → 10.4.16.80`、Forge `47.4.6 → 47.4.16`
2. **`FluidStorageKey` 签名适配**：GTCEu 1.4.x 的第二个参数是 `String tagKey`，7.x 才是 `TagKey<Fluid>`
3. **配方流体类型适配**：GTCEu 1.4.x 的 `GTRecipeBuilder.inputFluids/outputFluids` 接收 LDLib 的 `FluidStack`，尚未迁移到 Forge 的 `FluidStack`
4. **修复配方数据丢失**：`GregMekRecipeProvider` 丢弃了 `DataProvider.saveStable(...)` 返回的 future，而落盘是异步的，数据生成一结束 JVM 退出就会丢掉排队中的写入（实测 2971 个配方只有约 200 个真正落盘）。改为 `.join()` 同步等待后全部正常输出
5. **修复污浊浆液注册名**：`"dirty_slurry" + name` 会得到 `dirty_slurryiron`，修正为 `dirty_slurry_iron`
6. **单材料容错**：某个材料生成配方出错时只记录日志并跳过，不再中断整体数据生成
7. **补充依赖声明**：`mods.toml` 增加 `gtceu` / `ldlib` / `mekanism` / `forge` 的版本约束

## 构建 / Building

需要 JDK 17。

```bash
./gradlew build
```

产物：`build/libs/gregmek-1.0.1.jar`

仓库已提交 `src/generated/resources`，所以直接 `build` 就能得到包含全部配方的 jar。如果修改了材料或配方生成逻辑，重新跑一次数据生成即可：

```bash
./gradlew runData
```

> 数据生成会把结果写入 `src/generated/resources`，同时清理掉过期文件。

## 安装 / Installation

1. 安装 Minecraft 1.20.1 + Forge 47.4.16
2. 把 GTCEu 1.4.4、LDLib 1.0.28.a、Mekanism 10.4.16.80 放进 `mods/`
3. 把本模组的 jar 放进 `mods/`

## 许可 / License

本项目沿用上游的 **GNU LGPL 3.0**，详见 [LICENSE](LICENSE)。原始模组的著作权归 [MeowmelMuku](https://github.com/MeowmelMuku) 所有。

## 致谢 / Credits

- 原作者：[MeowmelMuku](https://github.com/MeowmelMuku)
- 格雷科技现代版：[GregTechCEu](https://github.com/GregTechCEu/GregTech-Modern)
- 通用机械：[mekanism/Mekanism](https://github.com/mekanism/Mekanism)
- GTCEu 1.4.4 兼容适配：[HK5538](https://github.com/HK5538)
