package net.oxyoksirotl.thehowlermod.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class TheHowlerEntity extends Monster implements GeoEntity {

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private HowlerStatus status;

    public boolean isChasing;
    public boolean isStaring;
    private int tickTimer;
    private int stareTimer;

    public TheHowlerEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {

        super(pEntityType, pLevel);
        this.status = HowlerStatus.IDLE;

        // Add ability to walk fast on water
        ItemStack enchantedBoots = new ItemStack(Items.CHAINMAIL_BOOTS);
        enchantedBoots.enchant(Enchantments.DEPTH_STRIDER, 3);

        this.setItemSlot(EquipmentSlot.FEET, enchantedBoots);

        // Fire resistance
        this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 999999,
                100, true, false));
        this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 999999,
                255, true, false));
    }



    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.isChasing = nbt.getBoolean("isChasing");
        this.isStaring = nbt.getBoolean("isStaring");
        this.tickTimer = nbt.getInt("timer");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("isChasing", this.isChasing);
        nbt.putBoolean("isStaring", this.isStaring);
        nbt.putInt("timer", this.tickTimer);
    }

    public static AttributeSupplier.Builder createAttributes () {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 400)
                .add(Attributes.MOVEMENT_SPEED, 0.2F)
                .add(Attributes.ARMOR_TOUGHNESS, 0.1f)
                .add(Attributes.ATTACK_DAMAGE, 6)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5f)
                .add(Attributes.FOLLOW_RANGE, 80.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // this.goalSelector.addGoal(1, new TheHowlerEntityGoal.StalkPlayerGoal(this, 1.0F, 60, 150));
        // this.goalSelector.addGoal(2, new ChasePlayerGoal(this, Player.class));
        // this.goalSelector.addGoal(2, new TheHowlerEntityGoal.StaredBehaviorGoal(this, 1.0F, 0, 2));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 160f));


        // this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        // this.targetSelector.addGoal(1, new HowlerHuntTargetGoal(this, this::is));
    }

    public boolean isBeingStaredAt (Player player, double distance) {
        if (!(player == null || player.isCreative() || player.isSpectator() || player.isInvisible())) {
            Vec3 playerEyePos = player.getEyePosition();
            Vec3 playerLookVec = player.getViewVector(1.0F).normalize();
            Vec3 entityPos = this.position();

            Vec3 directionToEntity = entityPos.subtract(playerEyePos).normalize();
            double distanceToEntity = playerEyePos.distanceTo(entityPos);

            if (distanceToEntity > distance) return false;

            double doProduct = directionToEntity.dot(playerLookVec);
            double angleThreshold = Math.cos(Math.toRadians(15));

            return doProduct >= angleThreshold;

        }

        return false;
    }

    public boolean isMoving() {
        return !(this.xOld == this.getX() && this.yOld == this.getY() && this.zOld == this.getZ());
    }

    private boolean isBlockTransparent(BlockState blockState) {
        return blockState.is(Tags.Blocks.GLASS) || blockState.is(Blocks.WATER);
    }

    @Override
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) { return (2.55F); }

    @Override
    protected boolean canRide(Entity pVehicle) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        this.updateHowlerStatus();

    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        } else this.noActionTime = 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 4, this::predicate));
    }

    private PlayState predicate(AnimationState<TheHowlerEntity> theHowlerEntityAnimationState) {

        String currentAnimation = "";

        switch (this.status) {
            case WALKING -> currentAnimation = "animation.howler.walk";
            case CHASING -> currentAnimation = "animation.howler.chase";
            case IDLE -> currentAnimation = "animation.howler.idle";
            default -> currentAnimation = "animation.howler.idle";
        }

        theHowlerEntityAnimationState.getController().setAnimation(
                RawAnimation.begin().then(currentAnimation, Animation.LoopType.LOOP)
        );

        return PlayState.CONTINUE;

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    // Howler status handling
    private enum HowlerStatus {
        IDLE,
        WALKING,
        CHASING,
        STARING
    }

    private void updateHowlerStatus() {

        // Check if the parameters: isMoving, isChasing
        if (this.isMoving()) {
            this.status = isChasing ? HowlerStatus.CHASING: HowlerStatus.WALKING;
        } else {
            this.status = isStaring ? HowlerStatus.STARING: HowlerStatus.IDLE;
        }

    }

    public HowlerStatus getHowlerStatus() {
        return this.status;
    }

    public int getStareTimer() {
        return this.stareTimer;
    }

    public void increaseStareTimer(int increaseAmount) {
        this.stareTimer+=increaseAmount;
    }

    public void resetStareTimer() {
        this.stareTimer=0;
    }
}
