package firenh.eleanorsvillagers.util;

import firenh.eleanorsvillagers.mixin.VillagerEntityAccessor;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerProfession;

public class VillagerHappinessUtil {
    public static final double MIN_HAPPINESS = -1;
    public static final double MAX_HAPPINESS = 1;

    public static final long DAY_LENGTH = 24000;
    public static final long SOCIAL_BATTERY = DAY_LENGTH / 6;

    public static final double TALK_WITH_VILLAGER_HAPPINESS_MODIFICATION = 0.1;
    public static final double DAY_SINCE_LAST_SOCIALIZED_HAPPINESS_MODIFICATION = -0.05;
    public static final double CANNOT_SLEEP_TICKING_HAPPINESS_MODIFICATION = -0.5 / DAY_LENGTH;
    public static final double CANNOT_SLEEP_HAPPINESS_MODIFICATION_VALUE_ALTERATION_WHEN_HAPPY = 0.5;
    public static final double TWO_DAYS_SINCE_LAST_SOCIALIZED_TICKING_HAPPINESS_MODIFICATION = 10 * DAY_SINCE_LAST_SOCIALIZED_HAPPINESS_MODIFICATION / DAY_LENGTH;
    public static final double UNHAPPINESS_THRESHOLD_TO_MODIFY_PRICES = 0.25;
    public static final double UNHAPPINESS_PRICE_INCREASE = 5;

    public static final boolean SHOULD_NITWITIFY = false;

    public static void tickHappiness(VillagerEntity villager, EleanorsVillagersData eleanorsVillagersData) {
        if (villager.getWorld().isNight()) modifyHappinessWhenAttemptingToSleep(villager);
        if (villager.getWorld().getTime() - eleanorsVillagersData.getLastSocialized() > DAY_LENGTH * 2) modifyHappinessWhenDaysSinceSocialized(villager);

        if (eleanorsVillagersData.getHappiness() <= 0) {
            villager.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 5, 3));
            villager.getWorld().addParticle(ParticleTypes.SMOKE, villager.getX(), villager.getEyeY(), villager.getZ(), 0, 0, 0);


            if (0.005 > Math.random()) {
                ((VillagerEntityAccessor) villager).invokeSayNo();
            }

            LookControl lookControl = villager.getLookControl();
            lookControl.lookAt(new Vec3d(lookControl.getLookX(), 100, lookControl.getLookZ()));

            if (SHOULD_NITWITIFY && eleanorsVillagersData.getHappiness() <= MIN_HAPPINESS) {
                villager.setVillagerData(villager.getVillagerData()
                    .withProfession(VillagerProfession.NITWIT)
                );
            }
        }
    }

    public static void modifyHappinessWhileTalkingWithVillager(ServerWorld world, VillagerEntity thisVillager, VillagerEntity otherVillager, long time) {
        if (thisVillager.getWorld().getTime() - ((VillagerEntityAccessor)thisVillager).getEleanorsVillagersData().getLastSocialized() > SOCIAL_BATTERY) {
            if (((VillagerEntityAccessor) thisVillager).getEleanorsVillagersData().getHappiness() <= ((VillagerEntityAccessor)otherVillager).getEleanorsVillagersData().getHappiness()) {
                ((VillagerEntityAccessor)thisVillager).getEleanorsVillagersData().modifyHappiness(TALK_WITH_VILLAGER_HAPPINESS_MODIFICATION);
            }
            ((VillagerEntityAccessor)thisVillager).getEleanorsVillagersData().setLastSocialized(time);
        }
    }

    public static void modifyHappinessDuringWork(VillagerEntity villager) {
        // EleanorsVillagers.LOGGER.info("Working!");        

        EleanorsVillagersData data = ((VillagerEntityAccessor) villager).getEleanorsVillagersData();
        long timeSinceSocialized = villager.getWorld().getTime() - data.getLastSocialized();
        // EleanorsVillagers.LOGGER.info("time since socialized:" + timeSinceSocialized + "; world time: " + villager.getWorld().getTime());

        if (timeSinceSocialized > DAY_LENGTH) {
            data.modifyHappiness(DAY_SINCE_LAST_SOCIALIZED_HAPPINESS_MODIFICATION * (timeSinceSocialized / DAY_LENGTH));
        }

        ((VillagerEntityAccessor) villager).setEleanorsVillagersData(data);
        // EleanorsVillagers.LOGGER.info("New Happiness: " + ((VillagerEntityAccessor) villager).getEleanorsVillagersData().getHappiness());
    }

    public static void modifyHappinessWhenDaysSinceSocialized(VillagerEntity villager) {
        // EleanorsVillagers.LOGGER.info("Two days since last socialized!"); 
        ((VillagerEntityAccessor) villager).getEleanorsVillagersData().modifyHappiness(TWO_DAYS_SINCE_LAST_SOCIALIZED_TICKING_HAPPINESS_MODIFICATION);
    }

    public static void modifyHappinessWhenAttemptingToSleep(VillagerEntity villager) {
        if (!villager.isSleeping()) {

            // EleanorsVillagers.LOGGER.info("Cant sleep!"); 
            ((VillagerEntityAccessor) villager).getEleanorsVillagersData().modifyHappiness(CANNOT_SLEEP_TICKING_HAPPINESS_MODIFICATION);
        } else if (((VillagerEntityAccessor)villager).getEleanorsVillagersData().getHappiness() > 0) {
            // EleanorsVillagers.LOGGER.info("Sleeping!");
            ((VillagerEntityAccessor) villager).getEleanorsVillagersData().modifyHappiness(- CANNOT_SLEEP_TICKING_HAPPINESS_MODIFICATION * CANNOT_SLEEP_HAPPINESS_MODIFICATION_VALUE_ALTERATION_WHEN_HAPPY);
        }
    }

    // public static void modifySpecialPricesWhenUnhappy(VillagerEntity villager, EleanorsVillagersData eleanorsVillagersData, PlayerEntity customer) {
    //     if (eleanorsVillagersData.getHappiness() < UNHAPPINESS_THRESHOLD_TO_MODIFY_PRICES) {
    //         for (TradeOffer tradeOffer : villager.getOffers()) {
	// 			tradeOffer.increaseSpecialPrice((int) (Math.abs(eleanorsVillagersData.getHappiness() - UNHAPPINESS_THRESHOLD_TO_MODIFY_PRICES) * UNHAPPINESS_PRICE_INCREASE));
	// 		}
    //     } else {
    //         for (TradeOffer tradeOffer : villager.getOffers()) {
    //             tradeOffer.clearSpecialPrice();
    //         }
    //     }
    // }
}
