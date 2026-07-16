package com.example.rapidbow;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

/**
 * RapidBow
 * --------
 * While enabled, holding right-click with a bow equipped fires arrows as
 * fast as possible instead of requiring a full draw + release each time.
 *
 * How it works: vanilla Minecraft re-initiates the bow draw every client
 * tick as long as the "use item" key (right-click) is held and the item
 * is not currently "in use". Normally the item stays "in use" until you
 * release the mouse button, which is what fires the arrow. Here, we just
 * force that release (which fires the arrow) after a very short number of
 * ticks. Since the mouse button is still physically held down, vanilla
 * automatically starts drawing again next tick -> repeat -> rapid fire.
 *
 * Press BACKSLASH ( \ ) at any time to toggle rapid-fire on/off.
 * When off, the bow behaves completely normally.
 *
 * Written for Minecraft 26.2 (Fabric, unobfuscated official mappings).
 */
public class RapidBowClient implements ClientModInitializer {

    /**
     * Number of ticks to hold the draw before force-firing.
     * Minecraft ticks run at 20/sec, so:
     *   1 tick  = fires ~20 times/sec (fastest, lowest arrow power)
     *   2 ticks = fires ~10 times/sec
     *   4 ticks = fires ~5 times/sec
     */
    private static final int FIRE_AFTER_TICKS = 1;

    private boolean rapidFireEnabled = true;
    private boolean lastToggleKeyState = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(Minecraft client) {
        if (client.player == null || client.level == null || client.gameMode == null) {
            return;
        }

        handleToggleKey(client);

        if (!rapidFireEnabled) {
            return;
        }

        if (client.player.isUsingItem()) {
            ItemStack activeStack = client.player.getUseItem();
            if (activeStack.getItem() instanceof BowItem) {
                int useDuration = activeStack.getUseDuration(client.player);
                int useTicks = useDuration - client.player.getUseItemRemainingTicks();
                if (useTicks >= FIRE_AFTER_TICKS) {
                    // Force the release/fire now. If right-click is still held,
                    // vanilla will automatically start a new draw next tick.
                    client.gameMode.releaseUsingItem(client.player);
                }
            }
        }
    }

    private void handleToggleKey(Minecraft client) {
        long windowHandle = client.getWindow().getWindow();
        boolean toggleKeyDown = GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_BACKSLASH) == GLFW.GLFW_PRESS;

        // Edge-triggered so holding the key doesn't rapidly flip the toggle.
        if (toggleKeyDown && !lastToggleKeyState) {
            rapidFireEnabled = !rapidFireEnabled;
            if (client.player != null) {
                client.player.displayClientMessage(
                        Component.literal("RapidBow: " + (rapidFireEnabled ? "ON" : "OFF")),
                        true // action bar message
                );
            }
        }
        lastToggleKeyState = toggleKeyDown;
    }
}
