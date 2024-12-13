package net.oxyoksirotl.thehowlermod.entity.custom;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class TheHowlerEntity extends Monster implements GeoEntity {

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public TheHowlerEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes () {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 400)
                .add(Attributes.MOVEMENT_SPEED, 1D)
                .add(Attributes.ARMOR_TOUGHNESS, 0.1f)
                .add(Attributes.ATTACK_DAMAGE, 16)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5f);
    }

    @Override
    protected boolean canRide(Entity pVehicle) {
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 32f));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 4, this::predicate));
    }

    private PlayState predicate(AnimationState<TheHowlerEntity> theHowlerEntityAnimationState) {
        if(theHowlerEntityAnimationState.isMoving()) {
            theHowlerEntityAnimationState.getController().setAnimation(
                    RawAnimation.begin().then("animation.howler.walk", Animation.LoopType.LOOP));
        }

        theHowlerEntityAnimationState.getController().setAnimation(
                RawAnimation.begin().then("animation.howler.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
