<div align="center">
  
  <img src="./docs/arbor-icon.png" alt="Arbor" width="160">
  
  # Arbor
  
  *专为生存和无政府服务器设计，提供优化、可配置的原版特性和丰富的 API 支持*
  
  Arbor 社区交流群：[881217223](https://qun.qq.com/universal-share/share?ac=1&authKey=DvHj9OFa9j88C5Nb3ttq0HXip8J%2F%2FLZqRLgQxSWT3ZxxkVprovvwrFpGUPDH0A8H&busi_data=eyJncm91cENvZGUiOiI4ODEyMTcyMjMiLCJ0b2tlbiI6InBsZDJobWRrQVR6ZExBR2h2ZWxETGdKU0ZjSUtWUmtWZkpXZVVaV2s2VjZvU1J0RXhYSUJIaWM1bjRIcXFGb2siLCJ1aW4iOiIxMjIyMjE5NTUifQ%3D%3D&data=5tqbb8NuVDi9Tez1u31lKO6HE3itb4jy7W1kZh7zBV656KoPog0q5eI9Wlxp-sgBGSK1pipJ4z7KjRfLlkfxCQ&svctype=4&tempid=h5_group_info)
  
  ![Created At](https://img.shields.io/github/created-at/LittleOvO233/Arbor?style=for-the-badge&color=blue)
  [![License](https://img.shields.io/github/license/LittleOvO233/Arbor?style=for-the-badge&color=green)](LICENSE.md)
  [![Issues](https://img.shields.io/github/issues/LittleOvO233/Arbor?style=for-the-badge&color=orange)](https://github.com/LittleOvO233/Arbor/issues)
  
  ![Commit Activity](https://img.shields.io/github/commit-activity/w/LittleOvO233/Arbor?style=for-the-badge&color=purple)
  ![GitHub all releases](https://img.shields.io/github/downloads/LittleOvO233/Arbor/total?style=for-the-badge&color=red)
  
  ![Repo contributors](https://img.shields.io/github/contributors/LittleOvO233/Arbor?style=for-the-badge&color=brightgreen)
  
  [English](./README_EN.md) | **中文**
</div>

---

## 📌 关于本分支

**Arbor** 是 [**Luminol**](https://github.com/LuminolMC/Luminol) 的一个社区分支（fork）。

- **原始项目**：Luminol，由原作者 **EarthMe** 创建与维护。
- **分支说明**：原作者 EarthMe 欢迎社区自行分支并独立维护，**前提是必须标明原作者与许可证**。本项目遵循这一要求。
- **当前维护者**：**Little**（本分支 Arbor 的维护者）。
- **许可证**：本项目继承上游许可证，以 [GNU 通用公共许可证第 3 版（GPLv3）](LICENSE.md) 授权发布，该许可证由 Luminol → Folia → Paper / Spigot / Bukkit / CraftBukkit 逐级继承而来。详见 [LICENSE.md](LICENSE.md)。

> 感谢 EarthMe 及 Luminol 全体贡献者的工作。Arbor 在其成果之上继续开发，所有原始版权与署名均予保留。

> [!CAUTION]
> 特别说明！</br>上游项目Luminol与CanvasMC组织存在侵权许可证事件，现已无法访问（404状态），具体原因请查看：</br>https://www.nexusmc.cn/forum/post/luminolmc-canvasmc-gpl-3-0</br>https://www.nexusmc.cn/forum/post/luminolmc-canvasmc-earthme

---

## ✨ 核心特性

- 🔧 **可配置的原版特性** - 灵活调整游戏机制以适应不同服务器需求
- 📊 **Tpsbar 支持** - 实时监控服务器性能状态
- 🐛 **Folia Bug 修复** - 针对 Folia 已知问题的专项修复
- 💾 **多存档格式支持** - 支持 linear 和 b_linear（linear 重新实现）存档格式
- 🔌 **扩展 API 支持** - 为插件开发者提供更多 API 接口（持续开发中）

## 📥 下载

### 稳定版本
所有发布版本都可以在 [Releases](https://github.com/LittleOvO233/Arbor/releases) 页面找到。

### 开发版本
如果您想体验最新功能，可以通过以下步骤自行构建。

### 构建步骤

```bash
# 克隆项目
git clone https://github.com/LittleOvO233/Arbor.git
cd Arbor

# 应用补丁并构建 Paperclip JAR
chmod 755 ./scripts/setup_and_build.sh && ./scripts/setup_and_build.sh
```

构建完成后，您可以在 `arbor-server/build/libs` 目录中找到生成的 JAR 文件。

## 🔌 API 使用

### Gradle 配置

```kotlin
repositories {
    maven {
        url = "https://repo.littleovo.cn/releases"
    }
}

dependencies {
    compileOnly("zone.little.arbor:arbor-api:26.1.2.build.+")
}
```

### Maven 配置

```xml
<repositories>
    <repository>
        <id>repository</id>
        <url>https://repo.littleovo.cn/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>zone.little.arbor</groupId>
        <artifactId>arbor-api</artifactId>
        <version>[26.1.2.build,)</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

## 💬 支持

### 获取帮助

- 📋 [提交 Issue](https://github.com/LittleOvO233/Arbor/issues)
- 💬 [GitHub Discussions](https://github.com/LittleOvO233/Arbor/discussions)
- 📖 [项目文档](./docs/)

## 🐛 问题反馈

当您遇到任何问题时，请向我们提问，我们将尽力解决。请记得：

- 📝 **清楚描述问题** - 详细说明问题的具体表现
- 📋 **提供完整日志** - 包含错误日志和相关配置信息
- 🔍 **环境信息** - 说明服务器版本、插件列表等环境详情
- 🔄 **复现步骤** - 如果可能，请提供问题复现的具体步骤

## 🤝 贡献代码

我们欢迎社区贡献！详细的贡献指南请查看：

- 📖 [贡献指南 (中文)](./docs/CONTRIBUTING.md)
- 📖 [Contributing Guide (English)](./docs/CONTRIBUTING_EN.md) 
