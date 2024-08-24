package firenh.eleanorsvillagers.brain;

import java.util.Optional;

import com.mojang.serialization.Codec;

import firenh.eleanorsvillagers.EleanorsVillagers;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EVMemoryModuleTypes {
    public static final MemoryModuleType<Long> LAST_SOCIALIZED = register("last_socialized", Codec.LONG);
    public static final MemoryModuleType<Double> HAPPINESS = register("last_socialized", Codec.DOUBLE);

    private static <U> MemoryModuleType<U> register(String id, Codec<U> codec) {
		return Registry.register(Registries.MEMORY_MODULE_TYPE, EleanorsVillagers.id(id), new MemoryModuleType<>(Optional.of(codec)));
	}
}
