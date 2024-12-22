package net.oxyoksirotl.thehowlermod.entity.custom;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.oxyoksirotl.thehowlermod.sound.ModSounds;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TheHowlerStaringGoal extends Goal {

    private TheHowlerEntity mob;
    private HashMap<Player,Integer> staringPlayers;
    private Player staringPlayer;
    private final double MAX_STARE_THRESHOLD;
    private double currentStaringTick;

    public TheHowlerStaringGoal(TheHowlerEntity mob, double staringThreshold) {

        this.mob = mob;
        this.MAX_STARE_THRESHOLD = staringThreshold;

    }

    @Override
    public boolean canUse() {

        this.staringPlayers = this.mob.getTrackingTargets();

        // Just in case if the staring plaer map is empty.
        if (this.staringPlayers == null || this.staringPlayers.isEmpty()) return false;

        // Choose a player with the largest random int.
        this.staringPlayer = Collections.max(staringPlayers.entrySet(), Map.Entry.comparingByValue()).getKey();

        // Allow this Goal to be used when the Howler is staring, but not chasing.
        return this.mob.isStaring() && !(this.mob.isChasing());

    }

    @Override
    public void start() {
        this.currentStaringTick = 0;
    }

    @Override
    public boolean canContinueToUse() {

        // Quit when staring phase is over or entering chase mode.
        return this.mob.isStaring() && !(this.mob.isChasing());
    }

    @Override
    public void tick() {

        // If the staring player didn't set up properly, break.
        if (this.staringPlayer == null && !this.mob.level().isClientSide) this.mob.setStaring(false);

        // Update ticking timer.
        this.currentStaringTick++;

        this.mob.getLookControl().setLookAt(this.staringPlayer.getX(),
                this.staringPlayer.getY(),
                this.staringPlayer.getZ());

        // Choose a random response after staring timer reached the threshold.
        // Depends on the random int, choose a behaviors.
        // TODO - Make the player's current aggression affect the Howler's behavior as well.
        // 0 - Despawn
        // 1 - Scare
        // 2 - MockChase
        // 3 - Chase

        if (!this.mob.level().isClientSide && this.currentStaringTick >= this.MAX_STARE_THRESHOLD) {

            int randomBehaviorInt = this.mob.getRandom().nextInt(0,50);

            if (randomBehaviorInt >= 25) this.scareTargetPlayer();
            else this.mob.discard();

        }

    }

    private void scareTargetPlayer() {

        // Safe check - if the target player is null, return.
        if (this.staringPlayer == null) return;

        this.staringPlayer.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100));
        this.staringPlayer.level().playSeededSound(this.staringPlayer,
                this.staringPlayer.getX(), this.staringPlayer.getY(), this.staringPlayer.getZ(),
                ModSounds.HOWLER_SPOTTED.get(), SoundSource.AMBIENT, 1, 1, 0);

        this.mob.discard();

    }

}
