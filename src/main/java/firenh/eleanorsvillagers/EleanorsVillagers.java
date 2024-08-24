package firenh.eleanorsvillagers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class EleanorsVillagers implements ModInitializer {
	public static final String MODID = "eleanorsvillagers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final String VERSION = "0.0.1";

	// public static EVConfig CONFIG;

	@Override
	public void onInitialize() {
		// loadConfigFromFile();

		// CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {EleanorsVillagersCommand.register(dispatcher, registryAccess, environment);});
	}

	// public static void loadConfigFromFile() {
    //     CONFIG = EVConfig.init();
    // }

	public static Identifier id(String id) {
		return Identifier.of(MODID, id);
	}
}