package firenh.eleanorsvillagers.util;

import firenh.eleanorsvillagers.EleanorsVillagers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class EVTags {
    public static class Items {
        public static final TagKey<Item> UNENCHANTED_BOOK = of("unenchanted_book");
        
        private static TagKey<Item> of(String id) {
            return TagKey.of(RegistryKeys.ITEM, EleanorsVillagers.id(id));
        }
    }

    public static class Enchantments {
        public static final TagKey<Enchantment> TRADED_APPRENTICE = of("traded/apprentice");
        public static final TagKey<Enchantment> TRADED_NOVICE = of("traded/novice");
        public static final TagKey<Enchantment> TRADED_JOURNEYMAN = of("traded/journeyman");
        public static final TagKey<Enchantment> TRADED_EXPERT = of("traded/expert");
        public static final TagKey<Enchantment> TRADED_MASTER = of("traded/master");

        private static TagKey<Enchantment> of(String id) {
            return TagKey.of(RegistryKeys.ENCHANTMENT, EleanorsVillagers.id(id));
        }

        public static TagKey<Enchantment> getTradedEnchantments(int villagerLevel) {
            switch (villagerLevel) {
                case 1 -> { return TRADED_NOVICE; }
                case 2 -> { return TRADED_APPRENTICE; }
                case 3 -> { return TRADED_JOURNEYMAN; }
                case 4 -> { return TRADED_EXPERT; }
                case 5 -> { return TRADED_MASTER; }

                default -> { return TRADED_NOVICE; }
            }
        }
    }
}
