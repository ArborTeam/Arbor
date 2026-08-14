<div align="center">
  
  <img src="./docs/arbor-icon.png" alt="Arbor" width="160">
  
  # Arbor
  
  *Designed for survival and anarchy servers, providing optimizations, configurable vanilla features, and rich API support*
  
  Arbor community QQ group: [881217223](https://qun.qq.com/universal-share/share?ac=1&authKey=DvHj9OFa9j88C5Nb3ttq0HXip8J%2F%2FLZqRLgQxSWT3ZxxkVprovvwrFpGUPDH0A8H&busi_data=eyJncm91cENvZGUiOiI4ODEyMTcyMjMiLCJ0b2tlbiI6InBsZDJobWRrQVR6ZExBR2h2ZWxETGdKU0ZjSUtWUmtWZkpXZVVaV2s2VjZvU1J0RXhYSUJIaWM1bjRIcXFGb2siLCJ1aW4iOiIxMjIyMjE5NTUifQ%3D%3D&data=5tqbb8NuVDi9Tez1u31lKO6HE3itb4jy7W1kZh7zBV656KoPog0q5eI9Wlxp-sgBGSK1pipJ4z7KjRfLlkfxCQ&svctype=4&tempid=h5_group_info)
  
  ![Created At](https://img.shields.io/github/created-at/LittleOvO233/Arbor?style=for-the-badge&color=blue)
  [![License](https://img.shields.io/github/license/LittleOvO233/Arbor?style=for-the-badge&color=green)](LICENSE.md)
  [![Issues](https://img.shields.io/github/issues/LittleOvO233/Arbor?style=for-the-badge&color=orange)](https://github.com/LittleOvO233/Arbor/issues)
  
  ![Commit Activity](https://img.shields.io/github/commit-activity/w/LittleOvO233/Arbor?style=for-the-badge&color=purple)
  ![GitHub all releases](https://img.shields.io/github/downloads/LittleOvO233/Arbor/total?style=for-the-badge&color=red)
  
  ![Repo contributors](https://img.shields.io/github/contributors/LittleOvO233/Arbor?style=for-the-badge&color=brightgreen)
  
  **English** | [中文](./README.md)
</div>

---

## 📌 About This Fork

**Arbor** is a community fork of [**Luminol**](https://github.com/LuminolMC/Luminol).

- **Original Project**: Luminol, created and maintained by its original author **EarthMe** (me.earthme.luminol).
- **Fork Notice**: The original author EarthMe welcomes the community to fork and maintain the project independently, **provided that the original author and license are clearly credited**. This project honors that requirement.
- **Current Maintainer**: **Little** (maintainer of this Arbor fork).
- **License**: This project inherits its upstream license and is released under the [GNU General Public License version 3 (GPLv3)](LICENSE.md), inherited through Luminol → Folia → Paper / Spigot / Bukkit / CraftBukkit. See [LICENSE.md](LICENSE.md) for details.

> Thanks to EarthMe and all Luminol contributors for their work. Arbor continues development on top of their efforts, preserving all original copyright and attribution.

---

## ✨ Core Features

- 🔧 **Configurable Vanilla Features** - Flexibly adjust game mechanics to suit different server needs
- 📊 **Tpsbar Support** - Real-time server performance monitoring
- 🐛 **Folia Bug Fixes** - Targeted fixes for known Folia issues
- 💾 **Multiple World Format Support** - Support for b_linear (linear reimplementation) world formats
- 🔌 **Extended API Support** - More API interfaces for plugin developers (ongoing development)
- ⚡ **Performance Optimizations** - Optimized specifically for high-load survival and anarchy servers

## 📥 Download

### Stable Releases
All release versions can be found on the [Releases](https://github.com/LittleOvO233/Arbor/releases) page.

### Development Builds
If you want to experience the latest features, you can build it yourself following the steps below.

### Build Steps

```bash
# Clone the project
git clone https://github.com/LittleOvO233/Arbor.git
cd Arbor

# Apply patches and build Paperclip JAR
chmod 755 ./scripts/setup_and_build.sh && ./scripts/setup_and_build.sh
```

After building, you can find the generated JAR file in the `build/libs` directory.

## 🔌 API Usage

### Gradle Configuration

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

### Maven Configuration

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

## 💬 Support

### Get Help

- 📋 [Submit Issues](https://github.com/LittleOvO233/Arbor/issues)
- 💬 [GitHub Discussions](https://github.com/LittleOvO233/Arbor/discussions)
- 📖 [Project Documentation](./docs/)

## 🐛 Bug Reports

When you encounter any issues, please ask us and we'll do our best to resolve them. Please remember to:

- 📝 **Describe the problem clearly** - Provide detailed information about the specific issue
- 📋 **Provide complete logs** - Include error logs and relevant configuration information
- 🔍 **Environment details** - Specify server version, plugin list, and other environment details
- 🔄 **Reproduction steps** - If possible, provide specific steps to reproduce the issue

## 🤝 Contributing

We welcome community contributions! For detailed contribution guidelines, please see:

- 📖 [Contributing Guide (English)](./docs/CONTRIBUTING_EN.md)
- 📖 [贡献指南 (中文)](./docs/CONTRIBUTING.md)

## 📊 Project Statistics

### BStats Data

![bStats](https://bstats.org/signatures/server-implementation/Arbor.svg "bStats")