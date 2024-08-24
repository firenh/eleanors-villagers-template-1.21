package firenh.eleanorsvillagers.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import firenh.eleanorsvillagers.EleanorsVillagers;
import firenh.eleanorsvillagers.util.EleanorsVillagersData;
import firenh.eleanorsvillagers.util.TradeOfferUtil;
import firenh.eleanorsvillagers.util.VillagerHappinessUtil;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {
    private EleanorsVillagersData eleanorsVillagersData;

    public double modifyHappiness(double happiness) {
        return eleanorsVillagersData.modifyHappiness(happiness).getHappiness();
    }

    @Inject(at = @At("HEAD"), method = "mobTick")
	private void mobTick(CallbackInfo info) {
        if (Objects.isNull(eleanorsVillagersData)) {
            EleanorsVillagers.LOGGER.info("EleanorsVillagersData is null, creating it lol");
            this.eleanorsVillagersData = new EleanorsVillagersData(((VillagerEntity)(Object)this).getWorld().getTime(), 0.5f);
        }

        VillagerHappinessUtil.tickHappiness((VillagerEntity)(Object)this, eleanorsVillagersData);
    }

    @Inject(at = @At("HEAD"), method = "beginTradeWith", cancellable = true)
    private void beginTradeWith(PlayerEntity customer, CallbackInfo info) {
        if (eleanorsVillagersData.getHappiness() < -0.33333333333333) {
            // EleanorsVillagers.LOGGER.info("Happiness too low, cancelling trade");
            ((VillagerEntityAccessor)this).invokeSayNo();
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "prepareOffersFor")
    private void prepareOffersFor(PlayerEntity customer, CallbackInfo info) {
        // VillagerHappinessUtil.modifySpecialPricesWhenUnhappy((VillagerEntity)(Object)this, eleanorsVillagersData, customer);
    }

    @Inject(at = @At("HEAD"), method = "shouldRestock")
	private void shouldRestock(CallbackInfoReturnable<Boolean> info) {
        // VillagerHappinessUtil.modifyHappinessDuringWork(((VillagerEntity)(Object)this));
    }

    @Inject(at = @At("HEAD"), method = "restock()V")
	private void restock(CallbackInfo info) {
        // EleanorsVillagers.LOGGER.info("Restocking!!!");

        TradeOfferUtil.modifyTradeOffers(( (VillagerEntity) (Object) this));
    }

    @Inject(at = @At("HEAD"), method = "restockAndUpdateDemandBonus()V")
	private void restockAndUpdateDemandBonus(CallbackInfo info) {
        // EleanorsVillagers.LOGGER.info("Restocking!!!");

        TradeOfferUtil.modifyTradeOffers(( (VillagerEntity) (Object) this));
    }

    @Inject(at = @At("HEAD"), method = "talkWithVillager")
    private void talkWithVillager(ServerWorld world, VillagerEntity otherVillager, long time, CallbackInfo info) {
        VillagerHappinessUtil.modifyHappinessWhileTalkingWithVillager(world, ((VillagerEntity)(Object)this), otherVillager, time);
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        // nbt.putLong("EleanorsVillagers_LastSocialized", this.lastSocialized);

        EleanorsVillagersData.CODEC
			.encodeStart(NbtOps.INSTANCE, eleanorsVillagersData)
			.resultOrPartial(EleanorsVillagers.LOGGER::error)
			.ifPresent(
                nbtElement -> nbt.put("EleanorsVillagersData", nbtElement)
                // () -> {}
            );
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    private void readCusomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        // if (nbt.contains("EleanorsVillagersData", NbtElement.COMPOUND_TYPE)) {
        EleanorsVillagersData.CODEC
            .parse(NbtOps.INSTANCE, nbt.get("EleanorsVillagersData"))
            .resultOrPartial(EleanorsVillagers.LOGGER::error)
            .ifPresentOrElse(
                eleanorsVillagersData -> { this.eleanorsVillagersData = eleanorsVillagersData; },
                () -> this.eleanorsVillagersData = new EleanorsVillagersData(((VillagerEntity)(Object)this).getWorld().getTime(), 0.5)
            );
        // }
    }
}
