# RapidBow — Fabric Mod (Minecraft 26.2)

Hold **right-click** with a bow equipped and it fires as fast as possible
instead of needing a full draw-and-release each shot. Press **`NUM_PAD_1`**
anytime to toggle rapid-fire on/off — a chat message confirms
the state. When off, the bow behaves 100% vanilla.

Built for **Minecraft 26.2**, using **Fabric Loader** + **Fabric API**,
with Java 25 and Minecraft's official (unobfuscated) mappings — no Yarn
mappings needed since Mojang dropped obfuscation as of 26.1.

## How it actually works

Vanilla Minecraft re-starts the bow draw every client tick as long as
you're still holding right-click and the bow isn't already "in use."
Normally you have to release the mouse button to fire. This mod force-
releases (fires) the bow after a few ticks of draw, and since your mouse
button is still physically down, vanilla automatically starts a new draw
the very next tick. Repeat several times a second = rapid fire. It's
entirely client-side — no mixins, no packet spoofing, no server-side
changes needed.

Because it's client-side only, servers running anti-cheat may flag or
reject the unnaturally fast fire rate. This is meant for singleplayer or
servers you control / have permission to use it on.

## Tuning fire speed

```java
private static final int FIRE_AFTER_TICKS = 4;
```

- `1`–`2`: fastest, but often unreliable — shots may not register at all
- `3`–`4`: good balance. **`4` is the current default**
- `5`+: slower but very reliable if you're still seeing missed shots at `4`

## Toggle key

NUM_PAD_1 — swap `GLFW.GLFW_KEY_KP_1` for another key if wanted.
