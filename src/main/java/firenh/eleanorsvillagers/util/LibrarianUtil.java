package firenh.eleanorsvillagers.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import firenh.eleanorsvillagers.mixin.TradeOfferAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.minecraft.world.World;

public class LibrarianUtil {
    public static final double CHANCE_TO_PULL_FROM_CHISELED_BOOKSHELF = 0.25;
    public static final Item FALLBACK_ITEM_IF_ENCHANTED_BOOK_UNAVAILABLE = Items.WRITABLE_BOOK;

    public static boolean enchantingBooksRequireEnchantingTable() {
        return true;
    }

    public static boolean doUnenchantedBooksAffectLevel() {
        return false;
    }

    public static TradeOffer newEnchantedBookTradeOffer(VillagerEntity villager, TradeOffer oldOffer) {
        TradeOffer virtualTradeOffer = new TradeOffers.EnchantBookFactory(villager.getVillagerData().getLevel(), EnchantmentTags.TRADEABLE).create(villager, villager.getRandom());
        int price = virtualTradeOffer.getFirstBuyItem().count();
        TradedItem firstBuyItem = oldOffer.getFirstBuyItem();
        ItemStack sellItem = virtualTradeOffer.getSellItem();

        return TradeOfferAccessor.newTradeOffer(
                new TradedItem(firstBuyItem.item(), price, firstBuyItem.components()),
                oldOffer.getSecondBuyItem(),
                sellItem,
                oldOffer.getUses(),
                oldOffer.getMaxUses(),
                oldOffer.shouldRewardPlayerExperience(),
                oldOffer.getSpecialPrice(),
                oldOffer.getDemandBonus(),
                oldOffer.getPriceMultiplier(),
                oldOffer.getMerchantExperience());
    }

    public static Optional<ItemStack> finalEnchantedBookTradeOfferModification(VillagerEntity villager, TradeOffer oldOffer, Random random, int minLevel, int maxLevel, TagKey<Enchantment> possibleEnchantments) {
        ItemStack newSellItem = oldOffer.copySellItem();
        
        if (shouldReplaceEnchantedBookSellItemWithUnenchantedBook(villager)) {
            return Optional.of(FALLBACK_ITEM_IF_ENCHANTED_BOOK_UNAVAILABLE.getDefaultStack());
        }

        Optional<ChiseledBookshelfBlockEntity> chiseledBookshelf = getChiseledBookshelf(villager);
        if (chiseledBookshelf.isEmpty()) return Optional.empty();
        ChiseledBookshelfVillagerInfo chiseledBookshelfInfo = ChiseledBookshelfVillagerInfo.fromBlockEntity(chiseledBookshelf.get());
        double chance = (villager.getRandom().nextDouble()) * Math.log(8 - chiseledBookshelfInfo.enchantedBooks());

        if (chiseledBookshelfInfo.enchantedBooks() > 0 && chance < (CHANCE_TO_PULL_FROM_CHISELED_BOOKSHELF * Math.log(chiseledBookshelfInfo.enchantedBooks()))) {
            List<RegistryEntry<Enchantment>> chiseledBookshelfEnchantments = chiseledBookshelf.isPresent() ? chiseledBookshelfInfo.enchantments() : List.of();
            List<RegistryEntry<Enchantment>> possibleNewEnchantments = new ArrayList<>();

            for (RegistryEntry<Enchantment> e : chiseledBookshelfEnchantments) {
                if (e.isIn(possibleEnchantments)) {
                    boolean shouldAdd = true;
                    if (shouldAdd) possibleNewEnchantments.add(e);
                    // EleanorsVillagers.LOGGER.info("Enchantment: " + e.getIdAsString());
                }
            }

            if (possibleNewEnchantments.size() > 0) {
                RegistryEntry<Enchantment> enchantment = possibleNewEnchantments.get(random.nextInt(possibleNewEnchantments.size()));
                int maxEnchantmentLevel = enchantment.value().getMaxLevel();
                // EleanorsVillagers.LOGGER.info("minLevel: " + minLevel + " maxLevel: " + maxLevel);
                int level = MathHelper.nextInt(random, 1, maxLevel > maxEnchantmentLevel ? maxEnchantmentLevel : maxLevel);
                // EleanorsVillagers.LOGGER.info("Enchantment: " + enchantment.getIdAsString() + "; Level: " + level);
                
                EnchantmentLevelEntry enchantmentLevelEntry = new EnchantmentLevelEntry(enchantment, level);
                newSellItem = EnchantedBookItem.forEnchantment(enchantmentLevelEntry);
                return Optional.of(newSellItem);
            }
        }

        return Optional.empty();
    }

    public static boolean shouldReplaceEnchantedBookSellItemWithUnenchantedBook(VillagerEntity villager) {
        if (!enchantingBooksRequireEnchantingTable()) return false;

        Optional<GlobalPos> optPos = villager.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE);
        World world = villager.getWorld();
        
        if (optPos.isEmpty()) return true;
        GlobalPos pos = optPos.get();
        if (! (pos.dimension().equals(world.getRegistryKey()))) return true;

        return !locateNextToLectern(pos.pos(), Blocks.ENCHANTING_TABLE, world).isPresent();
    }

    public static int getVillagerEnchantmentLevel(VillagerEntity villager) {
        if (!doUnenchantedBooksAffectLevel()) return Integer.MAX_VALUE;

        Optional<ChiseledBookshelfBlockEntity> chiseledBookshelf = getChiseledBookshelf(villager);
        int books = chiseledBookshelf.isPresent() ? ChiseledBookshelfVillagerInfo.fromBlockEntity(getChiseledBookshelf(villager).get()).unenchantedBooks() : 1;
        return (int) (books * 2 + 1);
    }

    public static Optional<ChiseledBookshelfBlockEntity> getChiseledBookshelf(VillagerEntity villager) {
        Optional<GlobalPos> optPos = villager.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE);
        World world = villager.getWorld();
        
        if (optPos.isEmpty()) return Optional.empty();
        GlobalPos pos = optPos.get();
        if (! (pos.dimension().equals(world.getRegistryKey()))) return Optional.empty();

        Optional<BlockPos> optionalChiseledBookshelf = locateNextToLectern(pos.pos(), Blocks.CHISELED_BOOKSHELF, world);
        if (optionalChiseledBookshelf.isEmpty()) return Optional.empty();

        BlockEntity chiseledBookshelf = world.getBlockEntity(optionalChiseledBookshelf.get());
        if (chiseledBookshelf instanceof ChiseledBookshelfBlockEntity) return Optional.of((ChiseledBookshelfBlockEntity) chiseledBookshelf);

        return Optional.empty();
    }

    public static Optional<BlockPos> locateNextToLectern(BlockPos origin, Block block, World world) {
        BlockState state = world.getBlockState(origin);
        if (!state.contains(Properties.HORIZONTAL_FACING)) return Optional.empty();

        Direction lecternDirection = state.get(Properties.HORIZONTAL_FACING);
        Axis lecternPerpendicular = lecternDirection.getAxis().equals(Axis.X) ? Axis.Z : Axis.X;
        BlockPos checkPos = origin;

        for (int i = -1; i < 2; i += 2) {
            checkPos = origin.offset(lecternPerpendicular, i);
            if (world.getBlockState(checkPos).isOf(block)) return Optional.of(checkPos);
        }

        if (world.getBlockState(origin.offset(lecternDirection.getOpposite(), 1)).isOf(block)) return Optional.of(checkPos);
        if (world.getBlockState(origin.down()).isOf(block)) return Optional.of(checkPos);

        return Optional.empty();
    }

    public static record ChiseledBookshelfVillagerInfo(int unenchantedBooks, int enchantedBooks, List<RegistryEntry<Enchantment>> enchantments) {
        public static ChiseledBookshelfVillagerInfo fromBlockEntity(ChiseledBookshelfBlockEntity chiseledBookshelf) {
            int unenchantedBooks = 0;
            int enchantedBooks = 0;
            List<RegistryEntry<Enchantment>> enchantments = new ArrayList<>();
            
            for (int i = 0; i < ChiseledBookshelfBlockEntity.MAX_BOOKS; i += 1) {
                ItemStack stack = chiseledBookshelf.getStack(i);

                if (stack.isIn(EVTags.Items.UNENCHANTED_BOOK)) {
                    unenchantedBooks += 1;
                    continue;
                }

                if (stack.isOf(Items.ENCHANTED_BOOK)) {
                    enchantedBooks += 1;
                    stack.get(DataComponentTypes.STORED_ENCHANTMENTS).getEnchantments().forEach(e -> {enchantments.add(e); });
                }
            }

            return new ChiseledBookshelfVillagerInfo(unenchantedBooks, enchantedBooks, enchantments);
        }
    }
}
