# 修改记录

本文件用于满足 [GNU 通用公共许可证第 3 版（GPLv3）](LICENSE.md) 第 5 条 a 款的要求：
携带醒目声明，说明本作品已被修改，并给出相关日期与改动内容。

> English version: [CHANGES_EN.md](CHANGES_EN.md)

---

## 项目溯源

- **原始项目**：[Luminol](https://github.com/LuminolMC/Luminol)，由 **EarthMe** 创建与维护。
- **本分支**：**Arbor**，由 **Little** 维护。
- **许可证**：GPLv3，继承自 Luminol → Folia → Paper / Spigot / Bukkit / CraftBukkit。

---

## 修改记录

### 2026-07-27 — 修改者：LittleOvO233

修复珊瑚扇被移动中的活塞销毁的问题（对应 `arbor-server/arbor-patches/0002`）。

粘性活塞收回时，目标格会先被设为 `MOVING_PISTON`，此时其方块实体尚未挂载，
碰撞形状为空，导致上方珊瑚扇的支撑检测失败而被销毁；随后重新读取源格只能拿到空气，
珊瑚扇彻底消失。改为在写入目标格之前先清空源格，避免误判。

同类依赖下方支撑的方块（海泡菜、紫水晶簇等）的相同问题一并修复。

### 2026-07-16 — 修改者：Little

本次更新了/memoryleak指令 用于监测内存泄漏
详情见arbor_global_config.toml
写出的堆转储 .hprof文件默认目录为arbor-heap-dumps下

### 2026-07-14 — 修改者：Little

应用两个由原作者 EarthMe 提供的补丁：

- **0085**：修复传送时丢失原始朝向与速度的问题。
- **0086**：将数据包处理队列替换为 mt-queue。

发布基于 Luminol 26.1.2 的首个 Arbor 版本。

### 2026-07-14 — 修改者：Little

基于 Luminol 26.1.2 创建 Arbor 分支，改动如下：

- **重命名**：将 `luminol-api` / `luminol-server` 模块及相关补丁、
  文件重命名为 `arbor-api` / `arbor-server` / `arbor-*`。
- **本地化**：将配置系统的注解注释翻译为简体中文。
- **文档**：更新 `README.md` / `README_EN.md` / `LICENSE.md`，
  加入分支说明与署名声明。
- **仓库配置**：更新 `.gitignore`。
- **图标**：删除沿用自 Luminol 的图标文件（后续将制作 Arbor 专属图标）。
---
