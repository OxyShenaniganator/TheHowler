package net.oxyoksirotl.thehowlermod.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.oxyoksirotl.thehowlermod.TheHowlerMod;
import net.oxyoksirotl.thehowlermod.entity.custom.TheHowlerEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;


public class TheHowlerModel extends GeoModel<TheHowlerEntity> {

    @Override
    public ResourceLocation getModelResource(TheHowlerEntity theHowlerEntity) {
        return new ResourceLocation(TheHowlerMod.MOD_ID, "geo/howler.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TheHowlerEntity theHowlerEntity) {
        return new ResourceLocation(TheHowlerMod.MOD_ID, "texture/entity/howlertexture2.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TheHowlerEntity theHowlerEntity) {
        return new ResourceLocation(TheHowlerMod.MOD_ID, "animations/howler.animation.json");
    }

    @Override
    public void setCustomAnimations(TheHowlerEntity animatable, long instanceId, AnimationState<TheHowlerEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(head.getRotX() + entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(head.getRotY() + entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);

    }
}
