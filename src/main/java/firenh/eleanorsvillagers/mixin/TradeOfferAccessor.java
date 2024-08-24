package firenh.eleanorsvillagers.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;

@Mixin(TradeOffer.class)
public interface TradeOfferAccessor {
    @Invoker("<init>")
    public static TradeOffer newTradeOffer(
		TradedItem firstBuyItem,
		Optional<TradedItem> secondBuyItem,
		ItemStack sellItem,
		int uses,
		int maxUses,
		boolean rewardingPlayerExperience,
		int specialPrice,
		int demandBonus,
		float priceMultiplier,
		int merchantExperience
	) {
        throw new AssertionError();
    }
}
