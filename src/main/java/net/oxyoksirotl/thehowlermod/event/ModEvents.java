package net.oxyoksirotl.thehowlermod.event;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.oxyoksirotl.thehowlermod.TheHowlerMod;
import net.oxyoksirotl.thehowlermod.controller.HowlerAggressivenessProvider;
import net.oxyoksirotl.thehowlermod.util.ModUtils;

@Mod.EventBusSubscriber(modid = TheHowlerMod.MOD_ID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer (AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player) {
            if(!event.getObject().getCapability(HowlerAggressivenessProvider.PLAYER_HOWL_AGGR).isPresent()) {
                event.addCapability(new ResourceLocation(TheHowlerMod.MOD_ID, "properties"), new HowlerAggressivenessProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if(event.isWasDeath()) {
            event.getOriginal().getCapability(HowlerAggressivenessProvider.PLAYER_HOWL_AGGR).ifPresent(oldStore -> {
                event.getOriginal().getCapability(HowlerAggressivenessProvider.PLAYER_HOWL_AGGR).ifPresent(newStore -> {
                    newStore.copyForm(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if(event.side == LogicalSide.SERVER) {

            event.player.getCapability(HowlerAggressivenessProvider.PLAYER_HOWL_AGGR).ifPresent( howlerAggressiveness -> {

                // On average, every 2.7 minutes, the Howler's aggressive toward a player will increase by 1.
                // TODO - Make the number configurable.

                if(howlerAggressiveness.getHowlerAggressiveness() < 25 && event.player.getRandom().nextFloat() < 0.00027F) {
                    howlerAggressiveness.addHowlerAggressiveness(1);
                    event.player.sendSystemMessage(Component.literal("Increased Aggressiveness"));
                }

                // Play random ambient sound depends on Aggression.
                // Spawn the Howler depends on Aggression.

                switch ((int) Math.ceil((double) howlerAggressiveness.getHowlerAggressiveness() / 3)) {

                    case 0 -> {
                    }
                    case 1, 2 -> {

                        // Wolf bark.
                        if(event.player.getRandom().nextFloat() < 0.0005F) playAmbientSounds(event.player,
                                SoundType.WOLF_AMBIENCE, 8, 15);

                    }

                    case 3, 4, 5 -> {

                        // Wolf bark.
                        if(event.player.getRandom().nextFloat() < 0.00015F) playAmbientSounds(event.player,
                                SoundType.WOLF_AMBIENCE, 5, 10);

                        // Wolf Hostile
                        if(event.player.getRandom().nextFloat() < 0.00015F) playAmbientSounds(event.player,
                                SoundType.WOLF_ANGRY, 8, 15);

                        // Cave noises
                        if(event.player.getRandom().nextFloat() < 0.00001F) playAmbientSounds(event.player,
                                SoundType.VANILLA_AMBIENCE, 8, 15);

                        // Spawn Howler
                        if(event.player.getRandom().nextFloat() < 0.00001F) ModUtils.tryHowlerSpawning(
                                event.player, 100
                        );
                    }

                    case 6, 7 -> {

                        // Wolf Hostile
                        if(event.player.getRandom().nextFloat() < 0.00015F) playAmbientSounds(event.player,
                                SoundType.WOLF_ANGRY, 5, 10);

                        // Cave noises
                        if(event.player.getRandom().nextFloat() < 0.00005F) playAmbientSounds(event.player,
                                SoundType.VANILLA_AMBIENCE, 8, 15);

                        // Modded ambience
                        if(event.player.getRandom().nextFloat() < 0.00001F) playAmbientSounds(event.player,
                                SoundType.MOD_AMBIENCE, 8, 15);

                    }

                    case 8 -> {

                    }

                }

            });

        }

    }

    @SubscribeEvent
    public static void onPlayerSleep(PlayerWakeUpEvent event) {

        // Everytime a player sleep, there's a 25& chance that the Howler's aggressive will increase by 1.

        if (!event.getEntity().level().isClientSide) {

            event.getEntity().getCapability(HowlerAggressivenessProvider.PLAYER_HOWL_AGGR).ifPresent(
                    howlerAggressiveness -> {
                        if (howlerAggressiveness.getHowlerAggressiveness() < 25 && event.getEntity().getRandom().nextFloat() < 0.01F) {
                            howlerAggressiveness.addHowlerAggressiveness(3);
                            event.getEntity().sendSystemMessage(Component.literal("Increased Howler Aggressiveness by 3."));
                        } else if (howlerAggressiveness.getHowlerAggressiveness() < 25 && event.getEntity().getRandom().nextFloat() < 0.14F) {
                            howlerAggressiveness.addHowlerAggressiveness(2);
                            event.getEntity().sendSystemMessage(Component.literal("Increased Howler Aggressiveness by 2."));
                        } else if (howlerAggressiveness.getHowlerAggressiveness() < 25 && event.getEntity().getRandom().nextFloat() < 0.35F) {
                            howlerAggressiveness.addHowlerAggressiveness(1);
                            event.getEntity().sendSystemMessage(Component.literal("Increased Howler Aggressiveness by 1."));
                        }
                    }
            );

        }
    }

    public static void playAmbientSounds(Player player, SoundType soundType, int distanceStart, int distanceEnd) {
        switch (soundType) {

            case WOLF_AMBIENCE -> player.level().playSound(
                    null,
                    new BlockPos(
                            (int) player.getX() + player.getRandom().nextInt(distanceStart,distanceEnd),
                            (int) player.getY(),
                            (int) player.getZ() + player.getRandom().nextInt(distanceStart,distanceEnd)
                    ),
                    SoundEvents.WOLF_AMBIENT,
                    SoundSource.NEUTRAL
            );

            case WOLF_ANGRY -> player.level().playSound(
                    null,
                    new BlockPos(
                            (int) player.getX() + player.getRandom().nextInt(distanceStart,distanceEnd),
                            (int) player.getY(),
                            (int) player.getZ() + player.getRandom().nextInt(distanceStart,distanceEnd)
                    ),
                    SoundEvents.WOLF_GROWL,
                    SoundSource.HOSTILE
            );

            case FOOTSTEP -> player.level().playSound(
                    null,
                    new BlockPos(
                            (int) player.getX() + player.getRandom().nextInt(distanceStart,distanceEnd),
                            (int) player.getY(),
                            (int) player.getZ() + player.getRandom().nextInt(distanceStart,distanceEnd)
                    ),
                    SoundEvents.WOLF_AMBIENT,
                    SoundSource.NEUTRAL
            );

            case HOWL -> player.level().playSound(
                    null,
                    new BlockPos(
                            (int) player.getX() + player.getRandom().nextInt(distanceStart,distanceEnd),
                            (int) player.getY(),
                            (int) player.getZ() + player.getRandom().nextInt(distanceStart,distanceEnd)
                    ),
                    SoundEvents.WOLF_HOWL,
                    SoundSource.NEUTRAL
            );

            case VANILLA_AMBIENCE -> player.level().playSound(
                    null,
                    new BlockPos(
                            (int) player.getX() + player.getRandom().nextInt(distanceStart,distanceEnd),
                            (int) player.getY(),
                            (int) player.getZ() + player.getRandom().nextInt(distanceStart,distanceEnd)
                    ),
                    SoundEvents.AMBIENT_CAVE.get(),
                    SoundSource.AMBIENT
            );

            case MOD_AMBIENCE -> player.level().playSound(
                    null,
                    new BlockPos(
                            (int) player.getX() + player.getRandom().nextInt(distanceStart,distanceEnd),
                            (int) player.getY(),
                            (int) player.getZ() + player.getRandom().nextInt(distanceStart,distanceEnd)
                    ),
                    SoundEvents.AMBIENT_CAVE.get(),
                    SoundSource.AMBIENT
            );


        }
    }

    private enum SoundType {
        WOLF_AMBIENCE,
        WOLF_ANGRY,
        FOOTSTEP,
        HOWL,
        VANILLA_AMBIENCE,
        MOD_AMBIENCE
    }

}
