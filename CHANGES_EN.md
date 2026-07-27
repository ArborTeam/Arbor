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

### 2026-07-27 — Modified by Little

Fixed coral fans being destroyed by moving pistons.
The same issue affecting other blocks that require support below (sea pickles, amethyst clusters, etc.) is fixed as well.

### 2026-07-21 — Modified by Little

Adapted Arbor from Folia 26.1.2 to 26.2:

- Version: bumped mcVersion / apiVersion to 26.2, pointed foliaRef at Folia ver/26.2.x.
- Build: in the arbor-server build patch, bumped mache to 26.2 and the fill plugin to 1.0.12, replaced the concurrentutil dependency with leafpile, and reordered repositories to prefer mirrors.
- Patches: re-resolved all server and API patch conflicts against 26.2 and adapted to upstream API changes.

### 2026-07-16 — Modified by Little

Added the /memoryleak command for monitoring memory leaks.
See arbor_global_config.toml for details.
Heap dump (.hprof) files are written to the arbor-heap-dumps directory by default.

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
