package net.oxyoksirotl.thehowlermod.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.HashMap;


public class TheHowlerEntity extends Monster implements GeoEntity {

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private HowlerStatus status;
    private HashMap<Player, Integer> trackingTargets;

//    public boolean isChasing;
//    public boolean isStaring;

    // TODO : These two needs to be synchronized too.
    private int tickTimer;
    private int stareTimer;
    private int randomAnimationNum;


    // Synchronized data & Networking bullshits.
    private static final EntityDataAccessor<Boolean> IS_BEING_STARED_AT = SynchedEntityData.defineId(
            TheHowlerEntity.class, EntityDataSerializers.BOOLEAN
    );
    public boolean isStaring() {
      return this.entityData.get(IS_BEING_STARED_AT);
    };
    public void setStaring(boolean isStaring) {
        this.entityData.set(IS_BEING_STARED_AT, isStaring);
    }

    private static final EntityDataAccessor<Boolean> IS_CURRENTLY_CHASING = SynchedEntityData.defineId(
            TheHowlerEntity.class, EntityDataSerializers.BOOLEAN
    );
    public boolean isChasing() {
        return this.entityData.get(IS_CURRENTLY_CHASING);
    }
    public void setChasing(boolean isChasing) {
        this.entityData.set(IS_CURRENTLY_CHASING, isChasing);
    }

    public static final EntityDataAccessor<Boolean> IS_CURRENTLY_STALKING = SynchedEntityData.defineId(
            TheHowlerEntity.class, EntityDataSerializers.BOOLEAN
    );
    public boolean isStalking() { return this.entityData.get(IS_CURRENTLY_STALKING); }
    public void setStalking(boolean isStalking) {
        this.entityData.set(IS_CURRENTLY_STALKING, isStalking);
    }

    private static final EntityDataAccessor<Integer> RANDOM_ANIM_NUM = SynchedEntityData.defineId(
            TheHowlerEntity.class, EntityDataSerializers.INT
    );
    public int getRandomAnimNum() {
        return this.entityData.get(RANDOM_ANIM_NUM);
    }
    public void setRandomAnimNum(int randomAnimationNum) {
        this.entityData.set(RANDOM_ANIM_NUM, randomAnimationNum);
    }

    public TheHowlerEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {

        super(pEntityType, pLevel);
        this.status = HowlerStatus.IDLE;
        this.trackingTargets = new HashMap<>();

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
        this.setChasing(nbt.getBoolean("isChasing"));
        this.setStaring(nbt.getBoolean("isStaring"));
        this.setStalking(nbt.getBoolean("isStalking"));
        this.tickTimer = nbt.getInt("timer");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("isChasing", this.isChasing());
        nbt.putBoolean("isStaring", this.isStaring());
        nbt.putBoolean("isStalking", this.isStalking());
        nbt.putInt("timer", this.tickTimer);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CURRENTLY_CHASING, false);
        this.entityData.define(IS_BEING_STARED_AT, false);
        this.entityData.define(IS_CURRENTLY_STALKING, false);
        this.entityData.define(RANDOM_ANIM_NUM, 1);
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
        this.goalSelector.addGoal(1, new TheHowlerStaringGoal(this, 100));
        this.goalSelector.addGoal(2, new TheHowlerObservingGoal(this, 100, 10));

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
            case CHASING -> currentAnimation = "animation.howler.chase";
            case WALKING -> currentAnimation = "animation.howler.walk";
            case STARING -> currentAnimation = this.staringAnimation();
            case IDLE -> currentAnimation = "animation.howler.idle";
            default -> currentAnimation = "animation.howler.idle";
        }

        theHowlerEntityAnimationState.getController().setAnimation(
                RawAnimation.begin().then(currentAnimation, Animation.LoopType.LOOP)
        );

        return PlayState.CONTINUE;

    }

    private String staringAnimation() {

        return "animation.howler.stared" + Integer.toString(this.getRandomAnimNum());
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
            this.status = this.isChasing() ? HowlerStatus.CHASING: HowlerStatus.WALKING;
        } else {
            this.status = this.isStaring() ? HowlerStatus.STARING: HowlerStatus.IDLE;
        }

    }

    public HowlerStatus getHowlerStatus() {
        return this.status;
    }

    // Tracking target management

    public HashMap<Player, Integer> getTrackingTargets() {
        return this.trackingTargets;
    }

    public void setTrackingTargets(HashMap<Player, Integer> trackingTargets) {
        this.trackingTargets = trackingTargets;
    }

}