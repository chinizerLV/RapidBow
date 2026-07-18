package com.example.rapidbow;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class RapidBowClient implements ClientModInitializer {

    private static final int FIRE_AFTER_TICKS = 4;

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
                    client.gameMode.releaseUsingItem(client.player);
                }
            }
        }
    }

    private void handleToggleKey(Minecraft client) {
        long windowHandle = GLFW.glfwGetCurrentContext();
        boolean toggleKeyDown = GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_KP_1) == GLFW.GLFW_PRESS;

        if (toggleKeyDown && !lastToggleKeyState) {
            rapidFireEnabled = !rapidFireEnabled;
            if (client.player != null) {
                client.player.sendSystemMessage(
                        Component.literal("RapidBow: " + (rapidFireEnabled ? "ON" : "OFF"))
                );
            }
        }
        lastToggleKeyState = toggleKeyDown;
    }
}
