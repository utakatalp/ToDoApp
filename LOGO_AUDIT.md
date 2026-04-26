# Logo audit — ToDoApp / DoneBot

## What you have today

| File | Format | Density | Purpose |
|------|--------|---------|---------|
| `app/src/main/res/mipmap-mdpi/ic_launcher.webp` and `…/ic_launcher_round.webp` | WebP | 48×48 dp | Pre-API-26 launcher icon |
| `app/src/main/res/mipmap-hdpi/ic_launcher*.webp` | WebP | 72×72 dp | Pre-API-26 launcher (1.5×) |
| `app/src/main/res/mipmap-xhdpi/ic_launcher*.webp` | WebP | 96×96 dp | Pre-API-26 launcher (2×) |
| `app/src/main/res/mipmap-xxhdpi/ic_launcher*.webp` | WebP | 144×144 dp | Pre-API-26 launcher (3×) |
| `app/src/main/res/mipmap-xxxhdpi/ic_launcher*.webp` | WebP | 192×192 dp | Pre-API-26 launcher (4×) |
| `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` | Adaptive XML | — | API 26+ launcher (foreground + background layers) |
| `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml` | Adaptive XML | — | API 26+ round launcher |
| `app/src/main/res/mipmap-*/ic_app_logo*.webp` | WebP | all 5 buckets | Project's brand logo (separate from launcher) |
| `app/src/main/ic_app_logo-playstore.png` | PNG | — | **512 × 512** (correct Play Store size!) |

**`AndroidManifest.xml`** sets `android:icon=@mipmap/ic_launcher` and `android:roundIcon=@mipmap/ic_launcher_round`. Adaptive XML resolves on API 26+; WebP fallback kicks in on older devices.

## What's correct already

- ✅ All 5 density buckets exist for legacy launcher icons.
- ✅ Adaptive icon XML present for API 26+.
- ✅ Play Store icon is exactly **512 × 512 PNG** — that's the spec Google demands.
- ✅ Round-icon variant present (required for some launchers).
- ✅ WebP format (smaller than PNG, no quality loss for icons).

## What's missing

- ❌ **Themed icon (Android 13+).** Modern devices apply system theme tint to monochrome icons. You don't have a `<monochrome>` layer in the adaptive XML. On stock Android 13+ launchers, your icon doesn't theme with the system colour scheme — it always shows full-colour.
- ❌ **No master 1024 × 1024 source PNG checked in.** All density buckets were derived from something, but the source doesn't live in the repo. If you ever need to regenerate (e.g. brand refresh, new density), there's no canonical original.
- ❌ **Foreground safe area unverified.** Adaptive icons reserve a 66 dp safe area inside a 108 dp logical canvas (the outer 21 dp can get masked by circle/squircle/rounded-square shapes). If your `ic_launcher_foreground.webp` artwork extends beyond the safe area, parts get clipped on devices that use aggressive masking. Without the master, can't verify.

## Optimal source spec

**One master file, 1024 × 1024 PNG, sRGB, transparent background, all visible artwork inside the central 832 × 832 square** (this is the safe area when scaled into the 108 dp adaptive canvas). That single file is enough for Android Studio's *Image Asset Studio* to derive every other variant:

| Output | Spec | Tool |
|--------|------|------|
| Adaptive icon foreground (per density) | scaled WebP | Image Asset Studio |
| Pre-API-26 launcher (per density) | 48/72/96/144/192 px WebP | Image Asset Studio |
| Round launcher (per density) | same dims, circle-masked | Image Asset Studio |
| Themed (monochrome) icon | white-on-transparent vector | recolour the source manually, save as `drawable/ic_launcher_monochrome.xml` |
| Play Store listing icon | **512 × 512 PNG, no transparency, sRGB** | export from your design tool with a solid background |
| Notification small icon | **vector** or 24 × 24 white-on-transparent PNG | already covered by `ic_notification.xml` |

You already have the 512 × 512 Play Store icon ✅, so only the master 1024 + monochrome variant are missing.

## Action items, prioritized

1. **Add a `<monochrome>` layer to the adaptive XMLs** — recolour your existing foreground to pure white-on-transparent, save as `drawable/ic_launcher_monochrome.xml` (vector) or `mipmap-*/ic_launcher_monochrome.webp` (raster). Reference it in `mipmap-anydpi-v26/ic_launcher.xml`:
   ```xml
   <adaptive-icon>
       <background android:drawable="@drawable/ic_launcher_background" />
       <foreground android:drawable="@drawable/ic_launcher_foreground" />
       <monochrome android:drawable="@drawable/ic_launcher_monochrome" />
   </adaptive-icon>
   ```

2. **Commit a master `marketing/master-1024.png`** outside `app/src/main/res/` so future regenerations have a canonical source. 1024 × 1024, transparent background.

3. **Re-run Image Asset Studio** if/when you produce a new master (Android Studio → New → Image Asset → Launcher Icons (Adaptive and Legacy)). It rewrites every mipmap in one pass.

4. **Audit safe area** on a Pixel-style aggressive masking launcher (Pixel Launcher itself, or any AOSP) — if any letters / mascot edges get cut off, the foreground extends beyond the 832 × 832 safe area and needs to be re-padded.

5. **Don't change the Play Store icon now** — yours is already correct.

## Bottom line

You're not broken. Existing icons render correctly across all Android versions. The two real gaps are:

- **Themed icon** for Android 13+ users (cosmetic — no functional impact).
- **No checked-in 1024 × 1024 master** (process gap — hits you only when you need to regenerate).

If shipping to Play Store next week, neither blocks. Address them before a brand refresh, not before launch.
