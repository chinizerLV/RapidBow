package com.example.rapidbow;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * RapidBow
 * --------
 * While enabled, holding right-click with a bow equipped will fire arrows
 * as fast as possible instead of requiring a full draw + release each time.
 *
 * How it works: vanilla Minecraft re-initiates the bow draw every client
 * tick as long as the "use item" key (right-click) is held and the item
 * is not currently "in use". Normally the item stays "in use" until you
 * release the mouse button, which is what fires the arrow. Here, we just
 * force that release (which fires the arrow) after a very short number of
 * ticks. Since the mouse button is still physically held down, vanilla
 * automatically starts drawing again next tick -> repeat -> rapid fire.
 *
 * Press ENTER (or numpad Enter) at any time to toggle rapid-fire on/off.
 * When off, the bow behaves completely normally.
 */
public class RapidBowClient implements ClientModInitializer {

    /**
     * Number of ticks to hold the draw before force-firing.
     * Minecraft ticks run at 20/sec, so:
     *   1 tick  = fires ~20 times/sec (fastest, lowest arrow power)
     *   2 ticks = fires ~10 times/sec
     *   4 ticks = fires ~5 times/sec
     * Tweak this to taste. 1 is the absolute fastest possible.
     */
    private static final int FIRE_AFTER_TICKS = 1;

    private boolean rapidFireEnabled = true;
    private boolean lastEnterState = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.interactionManager == null) {
            return;
        }

        handleToggleKey(client);

        if (!rapidFireEnabled) {
            return;
        }

        if (client.player.isUsingItem()) {
            ItemStack activeStack = client.player.getActiveItem();
            if (activeStack.getItem() instanceof BowItem) {
                int useTicks = activeStack.getMaxUseTime() - client.player.getItemUseTimeLeft();
                if (useTicks >= FIRE_AFTER_TICKS) {
                    // Force the release/fire now. If right-click is still held,
                    // vanilla will automatically start a new draw next tick.
                    client.interactionManager.stopUsingItem(client.player);
                }
            }
        }
    }

    private void handleToggleKey(MinecraftClient client) {
        long windowHandle = client.getWindow().getHandle();
        // Toggle key: backslash ( \ ). Change GLFW.GLFW_KEY_BACKSLASH below
        // to any other GLFW_KEY_* constant if you'd like a different key.
        boolean toggleKeyDown = InputUtil.isKeyPressed(windowHandle, GLFW.GLFW_KEY_BACKSLASH);

        // Edge-triggered so holding the key doesn't rapidly flip the toggle.
        if (toggleKeyDown && !lastEnterState) {
            rapidFireEnabled = !rapidFireEnabled;
            if (client.player != null) {
                client.player.sendMessage(
                        Text.literal("RapidBow: " + (rapidFireEnabled ? "ON" : "OFF")),
                        true // action bar message
                );
            }
        }
        lastEnterState = toggleKeyDown;
    }
}
