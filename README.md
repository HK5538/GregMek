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
矿石 ──化学反应釜(硫酸100mB)──> 污浊浆液 1000mB ──搅拌机(水500mB/100mB)──> 纯净浆液 ──高压釜(200mB)──> 晶体
                                                                                    │
矿石/粗矿 ──化学反应釜(盐酸)──> 碎片 ──化学反应釜(氧气)──> 碎块 ──粉碎──> 污浊粉 ──富集──> 纯净粉
```

> 机器选择是按 GTCEu 各机器的槽位上限定的：洗矿厂流体输出槽为 0、化学浸洗机流体输入槽为 1，都装不下「矿+酸→浆液」「浆液+水→浆液」这两道工序，因此溶解放在化学反应釜（3 流体进 / 2 流体出），洗涤放在搅拌机（2 流体进 / 1 流体出）。

**倍矿比例**

四条路线对齐 Mekanism 原版的阶梯，全部 110 种材料一致：

| 路线 | 机器 | 每 1 个矿石产量 |
| --- | --- | --- |
| 富集 | 富集仓 | 2 份粉尘（2x） |
| 提纯 | 提纯仓 | 3 碎块 → 3 份粉尘（3x） |
| 压射 | 压射仓 | 4 碎片 → 4 份粉尘（4x） |
| 化学链 | 化学反应釜 → 搅拌机 → 高压釜 | 5 晶体 → 5 份粉尘（5x） |

作为参照：GTCEu 原版矿石处理（粉碎机→洗矿→离心）约 2x，Mekanism 原版化学链为 5x。本模组的化学链完全复刻后者的 1000mB / 1:5 / 200mB 比例，因此倍率与原版 Mekanism 一致。

共生成 **3190 个配方文件**（110 种材料 × 29 条，其中化学反应釜的 8 条会自动复制一份到大化反），全部随仓库提交在 `src/generated/resources` 下。

## 更新日志 / Changelog

各版本的改动、修复与已知问题请见 [Releases](https://github.com/HK5538/GregMek/releases) 页面，发行说明中随附可直接下载的 jar。

## 构建 / Building

需要 JDK 17。

```bash
./gradlew build
```

产物：`build/libs/gregmek-1.0.3.jar`

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
