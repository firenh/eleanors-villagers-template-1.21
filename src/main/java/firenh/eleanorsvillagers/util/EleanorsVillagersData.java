package firenh.eleanorsvillagers.util;

import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import firenh.eleanorsvillagers.EleanorsVillagers;

public class EleanorsVillagersData {
    public static final Codec<EleanorsVillagersData> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
            Codec.LONG.fieldOf("LastSocialized").orElse(0L).forGetter(data -> {
                if (!Objects.isNull(data.lastSocialized)) {
                    return data.lastSocialized;
                }

                data.lastSocialized = 0L;
                return 0L;
            }),
            Codec.DOUBLE.fieldOf("Happiness").orElse(0.5).forGetter(data -> data.happiness)
    ).apply(instance, EleanorsVillagersData::new));

    private long lastSocialized;
    private double happiness;

    public EleanorsVillagersData(long lastSocialized, double happiness) {
        this.lastSocialized = lastSocialized;
        this.happiness = happiness;
    }

    public long getLastSocialized() {
        if (!Objects.isNull(lastSocialized)) {
            return lastSocialized;
        }

        this.lastSocialized = 0L;
        return lastSocialized;
    }

    public void setLastSocialized(long lastSocialized) {
        this.lastSocialized = lastSocialized;
    }

    public double getHappiness() {
        if (!Objects.isNull(happiness)) {
            return happiness;
        }

        this.happiness = 0.5;
        return happiness;
    }

    public void setHappiness(double happiness) {
        this.happiness = happiness;
    }

    public EleanorsVillagersData modifyHappiness(double happinessModify) {
        EleanorsVillagers.LOGGER.info("Modified Happiness! Old Happiness: " + happiness + "; New Happiness" + (happinessModify + happiness));
        setHappiness(happiness + happinessModify);

        if (happiness > VillagerHappinessUtil.MAX_HAPPINESS) happiness = VillagerHappinessUtil.MAX_HAPPINESS;
        if (happiness < VillagerHappinessUtil.MIN_HAPPINESS) happiness = VillagerHappinessUtil.MIN_HAPPINESS;

        return this;
    }

    

    
}
