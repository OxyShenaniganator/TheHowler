package net.oxyoksirotl.thehowlermod.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.oxyoksirotl.thehowlermod.TheHowlerMod;
import net.oxyoksirotl.thehowlermod.controller.HowlerAggressivenessProvider;

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
                if(howlerAggressiveness.getHowlerAggressiveness() < 25 && event.player.getRandom().nextFloat() < 0.005F) {
                    howlerAggressiveness.addHowlerAggressiveness(1);
                    event.player.sendSystemMessage(Component.literal("Increased Aggressiveness"));
                }
            });
        }
    }

}
