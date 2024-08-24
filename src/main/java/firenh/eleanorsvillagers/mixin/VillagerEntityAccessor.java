package firenh.eleanorsvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import firenh.eleanorsvillagers.util.EleanorsVillagersData;
import net.minecraft.entity.passive.VillagerEntity;

@Mixin(VillagerEntity.class)
public interface VillagerEntityAccessor {
    @Accessor
    public EleanorsVillagersData getEleanorsVillagersData();

    @Accessor("eleanorsVillagersData")
    public void setEleanorsVillagersData(EleanorsVillagersData time);

    @Invoker("sayNo")
    public void invokeSayNo();

    @Invoker("hasRecentlySlept")
    public boolean invokeHasRecentlySlept(long worldTime);
}
