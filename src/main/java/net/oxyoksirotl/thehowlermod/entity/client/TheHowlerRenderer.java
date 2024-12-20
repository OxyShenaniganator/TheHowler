package net.oxyoksirotl.thehowlermod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.oxyoksirotl.thehowlermod.TheHowlerMod;
import net.oxyoksirotl.thehowlermod.entity.custom.TheHowlerEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class TheHowlerRenderer extends GeoEntityRenderer<TheHowlerEntity> {
    public TheHowlerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TheHowlerModel());

        // For some reason, this only works for some shaders.
        //addRenderLayer(new AutoGlowingGeoLayer<>(this));

        // Use custom layer instead. (Credit to @SiverDX)
        addRenderLayer(new TheHowlerEyesLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(TheHowlerEntity animatable) {
        return new ResourceLocation(TheHowlerMod.MOD_ID, "textures/entity/howlertexture2.png");
    }

    @Override
    public void render(TheHowlerEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
