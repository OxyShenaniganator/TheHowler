package net.oxyoksirotl.thehowlermod.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.oxyoksirotl.thehowlermod.TheHowlerMod;
import net.oxyoksirotl.thehowlermod.controller.HowlerAggressiveness;
import net.oxyoksirotl.thehowlermod.controller.HowlerAggressivenessProvider;
import net.oxyoksirotl.thehowlermod.entity.ModEntities;
import net.oxyoksirotl.thehowlermod.entity.custom.TheHowlerEntity;

@Mod.EventBusSubscriber(modid = TheHowlerMod.MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {

        event.put(ModEntities.THE_HOWLER.get(), TheHowlerEntity.createAttributes().build());

    }

}
