package net.oxyoksirotl.thehowlermod.entity.custom;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TheHowlerObservingGoal extends Goal {

    private final double MAX_TRACKING_DISTANCE;
    private final double TRACKING_REFRESH_TICK;

    private TheHowlerEntity mob;
    private double currentTick;

    private HashMap<Player, Integer> potentialTargetPlayer;
    private HashMap<Player, Integer> staringPlayers;
    private Player trackingPlayer;

    public TheHowlerObservingGoal(TheHowlerEntity mob, double trackingDistance, double trackingRefreshRate) {

        this.mob = mob;
        this.MAX_TRACKING_DISTANCE = trackingDistance;
        this.TRACKING_REFRESH_TICK = trackingRefreshRate;

        this.potentialTargetPlayer = new HashMap<>();
        this.staringPlayers = new HashMap<>();
    }

    @Override
    public boolean canUse() {

        if (this.mob.isStaring() || this.mob.isChasing()) return false;

        // Update the list first.
        this.updateTrackingList();

        // Check if the updated tracking list is empty.
        if (this.potentialTargetPlayer.isEmpty()) return false;

        // Assign tracking player.
        this.assignNewTrackingPlayer();

        return this.trackingPlayer != null;

    }

    @Override
    public boolean canContinueToUse() {
        // Check if the entity is in staring mode.
        return !(this.mob.isStaring());
    }

    @Override
    public void tick() {

        // For every tick, update the tracking list.
        this.updateTrackingList();

        //Check if the tracking list is empty.
        if (this.potentialTargetPlayer.isEmpty()) return;

        // Check if the player is still in the list.
        // If not, change the tracking player.
        if (!(this.potentialTargetPlayer.containsKey(this.trackingPlayer))) {
            this.assignNewTrackingPlayer();
        }

        // If for some reason, tracking player is null, return.
        if (this.trackingPlayer == null) return;

        // Stare.
        this.mob.getLookControl().setLookAt(
                this.trackingPlayer.getX(),
                this.trackingPlayer.getY(),
                this.trackingPlayer.getZ()
        );

        // Check if anyone in the potential tracking list is staring at the entity.
        this.staringPlayers = new HashMap<>(this.potentialTargetPlayer.entrySet().stream()
                .filter(e -> this.mob.isBeingStaredAt(e.getKey(), this.MAX_TRACKING_DISTANCE))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        if (!(this.staringPlayers.isEmpty())) {

            this.mob.setTrackingTargets(this.staringPlayers);

            if (!(this.mob.level().isClientSide)) {

                this.mob.setRandomAnimNum(this.mob.getRandom().nextInt(1, 3));
                this.mob.setStaring(true);

            }
        }

    }

    private void updateTrackingList(){

        // First, get a list of player nearby within tracking distance.
        // Must not be in creative mode, being spectator or is invisible.
        List<Player> nearbyPlayers = this.mob.level().getEntitiesOfClass(
                Player.class, new AABB(this.mob.blockPosition()).inflate(this.MAX_TRACKING_DISTANCE),
                p -> !(p.isCreative() || p.isSpectator() || p.isInvisible()) && p.isAlive()
        );

        // For players within the list that's not in the HashMap, put it into the HashMap
        for (Player p : nearbyPlayers) {

            this.potentialTargetPlayer.putIfAbsent(p, p.getRandom().nextInt());
        }

        // Remove players that are no longer in the nearbyPlayers list.
        this.potentialTargetPlayer.keySet().retainAll(nearbyPlayers);


    }

    private void assignNewTrackingPlayer() {
        this.trackingPlayer = Collections.max(
                this.potentialTargetPlayer.entrySet(),
                Map.Entry.comparingByValue()
        ).getKey();
    }

}