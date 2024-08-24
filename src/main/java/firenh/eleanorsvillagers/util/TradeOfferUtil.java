package firenh.eleanorsvillagers.util;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

public class TradeOfferUtil {
    public static void modifyTradeOffers(VillagerEntity villager) {
        TradeOfferList tradeOfferList = new TradeOfferList();

        for (TradeOffer oldOffer : villager.getOffers()) {
            TradeOffer newOffer = oldOffer.copy();

            if (
                oldOffer.getSellItem().isOf(Items.ENCHANTED_BOOK) 
                || (oldOffer.getSecondBuyItem().isPresent() && oldOffer.getSecondBuyItem().get().matches(Items.BOOK.getDefaultStack()) && oldOffer.getSellItem().isOf(LibrarianUtil.FALLBACK_ITEM_IF_ENCHANTED_BOOK_UNAVAILABLE))
            ) {
                newOffer = LibrarianUtil.newEnchantedBookTradeOffer(villager, oldOffer);
            }

            tradeOfferList.add(newOffer);
        }

        // for (TradeOffer t : tradeOfferList) {
        //     EleanorsVillagers.LOGGER.info("sellItem" + t.getSellItem().getItem());
        // }

        villager.setOffers(tradeOfferList);
    }

    
}
