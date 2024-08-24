package firenh.eleanorsvillagers.config;

import java.util.HashMap;

import com.google.common.collect.Maps;

import net.minecraft.util.Identifier;

public class EVEnchantedBookConfig {
    public double chance_to_reset_when_traded_with = 1.0;
    public double chance_to_reset_when_not_traded_with = 1.0 / 3.0;
    public HashMap<Identifier, EVEnchantmentProperties> enchantment_properties = createDefaultEnchantmentProperties();

    public static HashMap<Identifier, EVEnchantmentProperties> createDefaultEnchantmentProperties() {
        HashMap<Identifier, EVEnchantmentProperties> properties = Maps.newHashMap();
        properties.put(Identifier.ofVanilla("protection"), new EVEnchantmentProperties(10, 1, 2, 4));
        properties.put(Identifier.ofVanilla("efficiency"), new EVEnchantmentProperties(10, 1, 60, 64));
        properties.put(Identifier.ofVanilla("mending"), new EVEnchantmentProperties(1, 5, 48, 64));
        return properties;
    }

    public static record EVEnchantmentProperties(
        int weight,
        int minVillagerLevel,
        int minPrice,
        int maxPrice
    ) {}
}
