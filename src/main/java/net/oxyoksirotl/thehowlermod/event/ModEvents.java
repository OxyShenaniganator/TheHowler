package net.oxyoksirotl.thehowlermod.event;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.oxyoksirotl.thehowlermod.TheHowlerMod;
import net.oxyoksirotl.thehowlermod.entity.ModEntities;
import net.oxyoksirotl.thehowlermod.entity.custom.TheHowlerEntity;

@Mod.EventBusSubscriber(modid = TheHowlerMod.MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {

        event.put(ModEntities.THE_HOWLER.get(), TheHowlerEntity.createAttributes().build());

    }

}
