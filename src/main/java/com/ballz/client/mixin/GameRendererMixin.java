package com.ballz.client.mixin;

import com.ballz.client.BallzClient;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    // Hold the zoom key to divide the field of view by 4
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void ballz_zoomFov(Camera camera, float partialTick, boolean changingFov,
                               CallbackInfoReturnable<Double> cir) {
        if (BallzClient.zoomKey != null && BallzClient.zoomKey.isDown()) {
            cir.setReturnValue(cir.getReturnValue() / 4.0);
        }
    }
}
