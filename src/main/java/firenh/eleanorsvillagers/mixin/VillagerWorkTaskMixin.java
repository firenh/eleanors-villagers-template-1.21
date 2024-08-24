package firenh.eleanorsvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import firenh.eleanorsvillagers.EleanorsVillagers;
import firenh.eleanorsvillagers.util.VillagerHappinessUtil;
import net.minecraft.entity.ai.brain.task.VillagerWorkTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(VillagerWorkTask.class)
public class VillagerWorkTaskMixin {
    @Inject(at = @At("HEAD"), method = "shouldRun", cancellable = true)
	private void shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity, CallbackInfoReturnable<Boolean> info) {
        if (((VillagerEntityAccessor) villagerEntity).getEleanorsVillagersData().getHappiness() <= -1.0) {
            // EleanorsVillagers.LOGGER.info("Too Unhappy to work");
            info.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "run")
    private void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long time, CallbackInfo info) {
        VillagerHappinessUtil.modifyHappinessDuringWork(villagerEntity);
    }
}
