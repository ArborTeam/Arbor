# Changes

This file satisfies Section 5(a) of the [GNU General Public License v3.0](LICENSE.md):
carrying notices that state the work has been modified, with the relevant dates and changes.

> 中文版本：[CHANGES.md](CHANGES.md)

---

## Provenance

- **Original project**: [Luminol](https://github.com/LuminolMC/Luminol), created and maintained by **EarthMe**.
- **This fork**: **Arbor**, maintained by **Little**.
- **License**: GPLv3, inherited from Luminol → Folia → Paper / Spigot / Bukkit / CraftBukkit.

---

## Modification Log

### 2026-07-14 — Modified by Little

Applied two patches provided by the original author EarthMe:

- **0085**: Fix teleport missing original rotation and velocity.
- **0086**: Replace the packet processing queue with mt-queue.

Released the first Arbor build based on Luminol 26.1.2.

### 2026-07-14 — Modified by Little

Forked from Luminol 26.1.2 as Arbor, with the following changes:

- **Rename**: Renamed the `luminol-api` / `luminol-server` modules and related patches
  and files to `arbor-api` / `arbor-server` / `arbor-*`.
- **Localization**: Translated configuration annotation comments into Simplified Chinese.
- **Documentation**: Updated `README.md` / `README_EN.md` / `LICENSE.md` with fork and
  attribution notices.
- **Repo config**: Updated `.gitignore`.
- **Icons**: Removed icon files inherited from Luminol (Arbor-specific icons to come later).
---
