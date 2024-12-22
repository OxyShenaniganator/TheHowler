package net.oxyoksirotl.thehowlermod.util;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.oxyoksirotl.thehowlermod.entity.ModEntities;
import net.oxyoksirotl.thehowlermod.entity.custom.TheHowlerEntity;

public class ModUtils {

    public static void tryHowlerSpawning(Player player, double distance) {

        AABB playerSurrounding = new AABB(player.blockPosition()).inflate(distance);

        // If the Howler already exist, don't spawn.
        if (player.level().getEntitiesOfClass(TheHowlerEntity.class, playerSurrounding).isEmpty()) return;

        // Else, spawn the Howler.
        if (!player.level().isClientSide) {

            double targetX = player.getX() + distance + player.getRandom().nextInt(-25, 25);
            double targetZ = player.getZ() + distance + player.getRandom().nextInt(-25, 25);

            TheHowlerEntity howlerEntity = new TheHowlerEntity(ModEntities.THE_HOWLER.get(), player.level());
            howlerEntity.moveTo(targetX,
                    player.level().getHeight(Heightmap.Types.WORLD_SURFACE, (int) targetX, (int) targetZ),
                    targetZ);

            player.level().addFreshEntity(howlerEntity);

        }


    }

}
