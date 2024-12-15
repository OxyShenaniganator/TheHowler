package net.oxyoksirotl.thehowlermod.controller;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public class HowlerAggressiveness {

    private int howlerAggressiveness;
    private final int MAX_AGGR = 25;
    private final int MIN_AGGR = 1;

    public int getHowlerAggressiveness() {
        return howlerAggressiveness;
    }

    public void addHowlerAggressiveness(int add) {
        this.howlerAggressiveness = Math.min(howlerAggressiveness + add, MAX_AGGR);
    }

    public void subHowlerAggressiveness(int sub) {
        this.howlerAggressiveness = Math.max(howlerAggressiveness - sub, MIN_AGGR);
    }

    public void copyForm(HowlerAggressiveness source) {
        this.howlerAggressiveness = source.howlerAggressiveness;
    }

    public void saveNBTData (CompoundTag nbt) {
        nbt.putInt("howler_aggressiveness", howlerAggressiveness);
    }

    public void loadNBTData (CompoundTag nbt) {
        howlerAggressiveness = nbt.getInt("howler_aggressiveness");
    }

}
