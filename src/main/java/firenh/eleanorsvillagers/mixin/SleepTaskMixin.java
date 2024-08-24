// package firenh.eleanorsvillagers.mixin;

// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// import firenh.eleanorsvillagers.util.VillagerHappinessUtil;
// import net.minecraft.entity.LivingEntity;
// import net.minecraft.entity.ai.brain.task.SleepTask;
// import net.minecraft.entity.passive.VillagerEntity;
// import net.minecraft.server.world.ServerWorld;

// @Mixin(SleepTask.class)
// public class SleepTaskMixin {
//     @Inject(at = @At ("RETURN"), method = "shouldRun") 
//     private void shouldRun(ServerWorld world, LivingEntity entity, CallbackInfoReturnable<Boolean> info) {
//         if (entity instanceof VillagerEntity) {
//             VillagerHappinessUtil.modifyHappinessWhenAttemptingToSleep((VillagerEntity)entity, info.getReturnValueZ());
//         }
//     }
// }
