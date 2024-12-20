package net.oxyoksirotl.thehowlermod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.oxyoksirotl.thehowlermod.TheHowlerMod;
import net.oxyoksirotl.thehowlermod.entity.custom.TheHowlerEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;


// Code shamelessly yoinked from SiverDX/Cadentem's Cave Dweller Evolved, because there's no tutorial on
// how to fix Geckolib's broken glowing layer. :)
// Shoutout to Cadentem. You're my savior.
public class TheHowlerEyesLayer extends GeoRenderLayer<TheHowlerEntity> {

    public ResourceLocation TEXTURE = new ResourceLocation(TheHowlerMod.MOD_ID,
            "textures/entity/howlertexture2_glowmask.png");

    public TheHowlerEyesLayer(GeoRenderer<TheHowlerEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, TheHowlerEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        packedLight = 15728880;
        RenderType eyesRenderType = RenderType.entityCutoutNoCull(TEXTURE);

        this.getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, eyesRenderType, bufferSource.getBuffer(eyesRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
