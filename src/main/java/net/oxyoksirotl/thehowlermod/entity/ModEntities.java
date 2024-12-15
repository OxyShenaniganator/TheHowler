package net.oxyoksirotl.thehowlermod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.oxyoksirotl.thehowlermod.TheHowlerMod;
import net.oxyoksirotl.thehowlermod.entity.custom.TheHowlerEntity;
import org.apache.http.client.entity.EntityBuilder;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TheHowlerMod.MOD_ID);

    public static final RegistryObject<EntityType<TheHowlerEntity>> THE_HOWLER =
            ENTITY_TYPES.register("the_howler", () -> EntityType.Builder.of(TheHowlerEntity::new, MobCategory.MISC)
                    .sized(1.5f, 3f).build("the_howler"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
