package com.ballz.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class BallzClient implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("ballz-client", "main"));

    public static KeyMapping zoomKey;

    @Override
    public void onInitializeClient() {
        zoomKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.ballz.zoom",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                CATEGORY));

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("ballz-client", "hud"),
                BallzHud::render);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("ballz")
                        .then(ClientCommandManager.literal("skin")
                                .then(ClientCommandManager.argument("name", StringArgumentType.word())
                                        .executes(ctx -> {
                                            SkinLookup.lookup(StringArgumentType.getString(ctx, "name"));
                                            return 1;
                                        })))));
    }
}
