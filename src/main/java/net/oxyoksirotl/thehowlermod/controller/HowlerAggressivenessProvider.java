package net.oxyoksirotl.thehowlermod.controller;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HowlerAggressivenessProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<HowlerAggressiveness> PLAYER_HOWL_AGGR = CapabilityManager.get(new CapabilityToken<HowlerAggressiveness>() {});

    private HowlerAggressiveness playerHowlerAggr = null;
    private final LazyOptional<HowlerAggressiveness> optional = LazyOptional.of(this::createHowlerAggressiveness);

    private @NotNull HowlerAggressiveness createHowlerAggressiveness() {
        if(this.playerHowlerAggr == null) {
            this.playerHowlerAggr = new HowlerAggressiveness();
        }

        return this.playerHowlerAggr;

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {

        if(capability == PLAYER_HOWL_AGGR) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {

        CompoundTag nbt = new CompoundTag();
        createHowlerAggressiveness().saveNBTData(nbt);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

        createHowlerAggressiveness().loadNBTData(nbt);

    }
}
