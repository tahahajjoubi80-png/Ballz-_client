package com.ballz.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;

public final class BallzHud {
    private BallzHud() {}

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        int fps = mc.getFps();
        PlayerInfo info = mc.getConnection() == null ? null
                : mc.getConnection().getPlayerInfo(mc.player.getUUID());
        String ping = info == null ? "--" : info.getLatency() + " ms";
        BlockPos pos = mc.player.blockPosition();

        String[] lines = {
                "FPS: " + fps,
                "Ping: " + ping,
                "XYZ: " + pos.getX() + " " + pos.getY() + " " + pos.getZ()
        };

        int y = 4;
        for (String line : lines) {
            graphics.text(mc.font, line, 4, y, 0xFFFFFFFF, true);
            y += 10;
        }
    }
}
