package firenh.eleanorsvillagers.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import firenh.eleanorsvillagers.util.EVTags;
import firenh.eleanorsvillagers.util.LibrarianUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

@Mixin(TradeOffers.EnchantBookFactory.class)
public class TradeOffers_EnchantBookFactoryMixin {
    @Mutable @Shadow
    private TagKey<Enchantment> possibleEnchantments;

    @Mutable @Shadow
    private int minLevel;
    
    @Mutable @Shadow
    private int maxLevel;

    @Inject(at = @At("HEAD"), method = "create")
    private void create_atHead(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> info) {
        if (entity instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) entity;
        
            this.maxLevel = LibrarianUtil.getVillagerEnchantmentLevel(villager);

            int villagerLevel = villager.getVillagerData().getLevel();
            this.possibleEnchantments = EVTags.Enchantments.getTradedEnchantments(villagerLevel);
        }
    }

    @Inject(at = @At("RETURN"), method = "create", cancellable = true)
    private void create_atReturn(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> info) {
        TradeOffer oldOffer = info.getReturnValue();
        ItemStack oldSellItem = oldOffer.getSellItem();

        Optional<ItemStack> sellItem = Optional.empty();

        if (oldSellItem.isOf(Items.ENCHANTED_BOOK) && entity instanceof VillagerEntity) {
            sellItem = LibrarianUtil.finalEnchantedBookTradeOfferModification((VillagerEntity) entity, oldOffer, random, minLevel, maxLevel, possibleEnchantments);
        }

        if (sellItem.isPresent()) {
            info.setReturnValue(TradeOfferAccessor.newTradeOffer(
                oldOffer.getFirstBuyItem(),
                oldOffer.getSecondBuyItem(),
                sellItem.get(),
                oldOffer.getUses(),
                oldOffer.getMaxUses(),
                oldOffer.shouldRewardPlayerExperience(),
                oldOffer.getSpecialPrice(),
                oldOffer.getDemandBonus(),
                oldOffer.getPriceMultiplier(),
                oldOffer.getMerchantExperience()
            ));
        }
    }
}
