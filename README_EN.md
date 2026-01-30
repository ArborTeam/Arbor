<div align="center">
  <img src="./public/image/Luminol_5.png" alt="Luminol Logo" width="300">
  
  # Luminol
  
  *Designed for survival and anarchy servers, providing optimizations, configurable vanilla features, and rich API support*
  
  ![Created At](https://img.shields.io/github/created-at/LuminolMC/Luminol?style=for-the-badge&color=blue)
  [![License](https://img.shields.io/github/license/LuminolMC/Luminol?style=for-the-badge&color=green)](LICENSE.md)
  [![Issues](https://img.shields.io/github/issues/LuminolMC/Luminol?style=for-the-badge&color=orange)](https://github.com/LuminolMC/Luminol/issues)
  
  ![Commit Activity](https://img.shields.io/github/commit-activity/w/LuminolMC/Luminol?style=for-the-badge&color=purple)
  ![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/LuminolMC/Luminol?style=for-the-badge&color=yellow)
  ![GitHub all releases](https://img.shields.io/github/downloads/LuminolMC/Luminol/total?style=for-the-badge&color=red)
  
  ![Repo contributors](https://img.shields.io/github/contributors/LuminolMC/Luminol?style=for-the-badge&color=brightgreen)
  [![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/LuminolMC/Luminol)
  
  **English** | [中文](./README.md)
</div>

---

## ✨ Core Features

- 🔧 **Configurable Vanilla Features** - Flexibly adjust game mechanics to suit different server needs
- 📊 **Tpsbar Support** - Real-time server performance monitoring
- 🐛 **Folia Bug Fixes** - Targeted fixes for known Folia issues
- 💾 **Multiple World Format Support** - Support for linear and b_linear (linear reimplementation) world formats
- 🔌 **Extended API Support** - More API interfaces for plugin developers (ongoing development)
- ⚡ **Performance Optimizations** - Optimized specifically for high-load survival and anarchy servers

## 📥 Download

### Stable Releases
All release versions can be found on the [Releases](https://github.com/LuminolMC/Luminol/releases) page.

### Development Builds
If you want to experience the latest features, you can build it yourself following the steps below.

### Build Steps

```bash
# Clone the project
git clone https://github.com/LuminolMC/Luminol.git
cd Luminol

# Apply patches and build Paperclip JAR
chmod 755 ./scripts/setup_and_build.sh && ./scripts/setup_and_build.sh
```

After building, you can find the generated JAR file in the `build/libs` directory.

## 🔌 API Usage

### Gradle Configuration

```kotlin
repositories {
    maven {
        url = "https://repo.menthamc.org/repository/maven-public/"
    }
}

dependencies {
    compileOnly("me.earthme.luminol:luminol-api:$VERSION")
}
```

### Maven Configuration

```xml
<repositories>
    <repository>
        <id>menthamc</id>
        <url>https://repo.menthamc.org/repository/maven-public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>me.earthme.luminol</groupId>
        <artifactId>luminol-api</artifactId>
        <version>$VERSION</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

## 💬 Community & Support

> If you're interested in this project or have any questions, feel free to ask us.

### Join Our Community

- **QQ Group**: [1015048616](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=QML5kIVsniPi1PlZvnjHQT_02EHsZ5Jc&authKey=%2FTCJsZC7JFQ9sxAroPCKuYnlV57Z5fyqp36ewXZk3Sn4iJ9p4MB1JKdc%2FFcX3HOM&noverify=0&group_code=1015048616)
- **QQ Channel**: [Join Here](https://pd.qq.com/s/eq9krf9j)
- **Telegram**: [Join Here](https://t.me/LuminolMinecraft)
- **Discord**: [Join Here](https://discord.gg/Qd7m3V6eDx)

### Get Help

- 📋 [Submit Issues](https://github.com/LuminolMC/Luminol/issues)
- 💬 [GitHub Discussions](https://github.com/LuminolMC/Luminol/discussions)
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

## 🎉 Special Thanks

### Project Sponsors

<div align="center">
  <b>Thanks to <a href="https://github.com/LegacyLands">LegacyLands</a> for sponsoring this project</b>
  <br>
  <i>If you want to develop cross-Folia/non-Folia platform plugins, <a href="https://github.com/LegacyLands/legacy-lands-library/">legacy-lands-library</a> would be a great choice</i>
  <br><br>
  <img src="public/image/legacy-lands-logo.png" alt="LegacyLands Logo" width="200">
</div>

---

<div align="center">
  <img src="public/image/rain-yun.png" alt="RainYun" width="200">
  <br>
  <b>International multi-route selection with cloud storage support</b>
  <br>
  7-day satisfaction guarantee with refund option, powerful technical support team and high-availability customer service
  <br>
  RainYun Cloud Servers - stability and cost-effectiveness to help you get to the cloud quickly
  <br>
  <a href="https://www.rainyun.com/aiyuyun_">Visit RainYun</a>
</div>

---

<div align="center">
  <img src="https://www.ej-technologies.com/images/product_banners/jprofiler_large.png" alt="Jprofiler Logo" width="200">
  <br>
    <i>If you need a Java profiler for analyzing performance issues, <a href="https://www.ej-technologies.com/jprofiler">Jprofiler</a> will be a great choice</i>
  <br><br>
</div>

---

## ⭐ Give Us a Star!

> Every free ⭐Star you give is the motivation for our every step forward.

### Star History

<a href="https://star-history.com/#LuminolMC/Luminol&LuminolMC/LightingLuminol&LuminolMC/Lophine&Date">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=LuminolMC/Luminol%2CLuminolMC/LightingLuminol%2CLuminolMC/Lophine&type=Date&theme=dark" />
    <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=LuminolMC/Luminol%2CLuminolMC/LightingLuminol%2CLuminolMC/Lophine&type=Date" />
    <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=LuminolMC/Luminol%2CLuminolMC/LightingLuminol%2CLuminolMC/Lophine&type=Date" />
  </picture>
</a>

<div align="center">
  <b>If this project helps you, please don't forget to give us a ⭐Star!</b>
</div>