# RapidBow — Fabric Mod

Hold **right-click** with a bow equipped and it fires as fast as possible
instead of needing a full draw-and-release each shot. Press **Enter**
anytime to toggle rapid-fire on/off (an action-bar message confirms the
state) — when off, the bow behaves 100% vanilla.

Targets **Minecraft 1.20.1** with **Fabric Loader** + **Fabric API**.

## How it actually works (no exploits/hacks, no server-side changes needed)

Vanilla Minecraft re-starts the bow draw every client tick as long as you're
still holding right-click and the bow isn't already "in use." Normally you
have to release the mouse button to fire. This mod just force-releases
(fires) the bow after 1 tick of draw, and since your mouse button is still
physically down, vanilla automatically starts a new draw the very next tick.
Repeat ~20 times a second = rapid fire. It's entirely client-side and just
piggybacks on Fabric API's tick event — no mixins, no packet spoofing.

Because it's client-side only, on a server with anti-cheat it may get
flagged/rejected — this is meant for singleplayer or servers you control.

## Project layout

```
rapidbowmod/
├── build.gradle
├── gradle.properties
├── settings.gradle
├── README.md
└── src/main/
    ├── java/com/example/rapidbow/RapidBowClient.java   <- all the logic lives here
    └── resources/fabric.mod.json
```

## Setup & build

You'll need a JDK 17 and internet access to Fabric's Maven (this sandbox
doesn't have that, so the project hasn't been build-tested here — but it's
a completely standard, minimal Fabric client mod).

1. Install [Fabric Loom prerequisites]: JDK 17.
2. From this folder, generate the Gradle wrapper (or just open the folder
   in IntelliJ IDEA with the Fabric/Loom setup, which will do it for you):
   ```
   gradle wrapper
   ```
3. Build:
   ```
   ./gradlew build
   ```
   The compiled jar will show up in `build/libs/rapidbow-1.0.0.jar`.
4. Drop that jar into your `.minecraft/mods` folder, alongside the matching
   **Fabric API** jar for 1.20.1 (download from Modrinth or CurseForge if
   you don't already have it), and launch with the Fabric Loader profile.

If you'd rather not deal with Gradle at all, the easiest path is:
- Clone the official Fabric example mod template (search "fabric-example-mod"
  on GitHub) as your project scaffold — it already has a working Gradle
  wrapper and toolchain.
- Copy `RapidBowClient.java` into its `src/main/java/...` package and copy
  the `entrypoints.client` line + mod id/name into its `fabric.mod.json`.
- Build with `./gradlew build` from there.

## Tuning

In `RapidBowClient.java`:

```java
private static final int FIRE_AFTER_TICKS = 1;
```

- `4` = fastest possible (fires ~20x/sec, arrows have minimal power since
  there's almost no draw).
- Raise it (e.g. `3`–`5`) if you want a bit more arrow power/damage per
  shot at the cost of fire rate — it's a straight speed-vs-power tradeoff,
  same as a normal bow draw.

## Toggle key

Enter (main or numpad) toggles the whole effect on/off. Want a different
key? Swap `GLFW.GLFW_KEY_ENTER` / `GLFW_KEY_KP_ENTER` in
`handleToggleKey()` for any other [GLFW key constant].
