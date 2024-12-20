package net.oxyoksirotl.thehowlermod.entity.custom;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.stream.Collectors;

public class TheHowlerEntityGoal {

    // Custom goals - Observing, Stalking, Chasing, FakeChasing, Scaring and StaredBehaviorProcessing

    public class ObservingGoal extends Goal {

        private TheHowlerEntity theHowler;
        private HashMap<Integer, Player> potentialTargetPlayer;
        private Player targetPlayer;
        private double detectDistance;
        private final double MAX_TRACKING_TICK;
        private double currentTick;

        public ObservingGoal(TheHowlerEntity theHowler, double detectDistance, double maxTrackingTick) {

            this.theHowler = theHowler;
            this.detectDistance = detectDistance;
            this.MAX_TRACKING_TICK = maxTrackingTick;

        }

        @Override
        public boolean canUse() {

            // First, update the tracking list.
            this.updateTrackingList();

            // If the tracking list is not empty:
            // 1. Check if there's anyone in the tracking list that's staring at it,
            //    if true, enter stare processing and return false;
            // 2. When no nearby player is staring at it, set the tracking player and return true;
            if (!potentialTargetPlayer.isEmpty()) {

                if (potentialTargetPlayer.entrySet().stream().filter(
                        e -> this.theHowler.isBeingStaredAt(e.getValue(), detectDistance))
                        .map(Map.Entry::getValue).collect(Collectors.toSet()).isEmpty()) {

                    this.theHowler.isStaring = true;
                    return false;
                }

                this.targetPlayer = this.potentialTargetPlayer.get(Collections.max(this.potentialTargetPlayer.keySet()));
                return true;
            }

            return false;

        }

        @Override
        public boolean canContinueToUse() {

            // If there's no targeting player AND there are no longer any player left in the HashMap to follow,
            // or the Howler is being stared at, quit the Observe state.
            return !((this.targetPlayer == null && this.potentialTargetPlayer.isEmpty()) || this.theHowler.isStaring);
        }

        @Override
        public void start() {
            // Set the current tick to 0.
            this.currentTick = 0;

        }

        @Override
        public void stop() {
            this.currentTick = 0;
            this.potentialTargetPlayer = null;
            this.targetPlayer = null;
            super.stop();
        }

        @Override
        public void tick() {

            // Update tick
            this.currentTick++;

            // If the timer is up, despawn.
            if (this.currentTick >= this.MAX_TRACKING_TICK) {
                this.theHowler.discard();
                return;
            }

            // Every 100 ticks (5 seconds), update the tracking list and tracking player.
            if (this.currentTick % 100 == 0) {
                updateTrackingList();

                // Change the tracking player if the player left tracking distance (no longer on tracking list)
                // Set the tracking player to null.
                // If updated tracking list is empty, return, else choose the new tracking player.
                if(!(this.potentialTargetPlayer.containsValue(this.targetPlayer))) {

                    this.targetPlayer = null;
                    if (!(this.potentialTargetPlayer.isEmpty())) return;

                    this.targetPlayer = this.potentialTargetPlayer.get(Collections.max(this.potentialTargetPlayer.keySet()));
                }

            }

            // Stare.
            this.theHowler.getLookControl().setLookAt(targetPlayer);

        }

        private void updateTrackingList() {

            // Get a list of surrounding player within detection distance
            List<Player> surroundingPlayer = this.theHowler.level().getEntitiesOfClass(
                    Player.class, new AABB (this.theHowler.blockPosition()).inflate(detectDistance),
                    (p) -> !(p.isCreative() || p.isSpectator() || p.isAlive() || p.isInvisible())
            );

            // If there's no player within the list, break.
            if (surroundingPlayer.isEmpty()) {
                return;
            }

            // Remove players that's no longer within detection Distance
            this.potentialTargetPlayer.entrySet().removeIf(e -> !(surroundingPlayer.contains(e)));

            // For each player in the list, append into HashMap if not already exist
            for (Player p : surroundingPlayer) {
                if (this.potentialTargetPlayer.containsValue(p)) this.potentialTargetPlayer.put(
                        p.getRandom().nextInt(), p);

            }

        }
    }
}
