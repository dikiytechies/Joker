package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.init.AddonStructures;
import com.dikiytechies.joker.init.Sounds;
import com.github.standobyte.jojo.capability.entity.PlayerUtilCapProvider;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.network.PacketManager;
import com.github.standobyte.jojo.network.packets.fromserver.TrEntitySpecialEffectPacket;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.github.standobyte.jojo.util.mc.CustomVillagerTrades;
import com.github.standobyte.jojo.util.mc.reflection.CommonReflection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.inventory.MerchantInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.OptionalInt;

import static com.github.standobyte.jojo.util.mc.CustomVillagerTrades.MapTrades.biomeHasOtherStructure;
import static com.github.standobyte.jojo.util.mc.CustomVillagerTrades.MapTrades.canGiveMap;

@Mixin(value = CustomVillagerTrades.MapTrades.MapTrade.class, remap = false)
public class CustomVillagerTradesMixin {
    @Shadow @Final
    public SoundEvent onFirstBuyFlavorSound;
    @Shadow @Final
    public String name;
        private static final CustomVillagerTrades.MapTrades.MapTrade SHRINE_MAP = new CustomVillagerTrades.MapTrades.MapTrade(VillagerType.DESERT, "shrine_map",
            new CustomVillagerTrades.MapTrades.EmeraldForMapTrade(16, AddonStructures.SHRINE,
                    new ResourceLocation(AddonMain.MOD_ID, "textures/map/shrine.png"), OptionalInt.of(0x520411), 1, 15),
            new TranslationTextComponent("filled_map.joker:shrine"), Sounds.MAP_BOUGHT_DESERT.get(), null) { // :shrug:
        @Override
        public double getMapChance(@Nullable StandType<?> standType, @Nullable NonStandPowerType<?> powerType, VillagerData villager) {
            double shrineMapChance;


            VillagerType biome = villager.getType();
            if (biome == VillagerType.DESERT)                     shrineMapChance = 1;
            else if (biomeHasOtherStructure(biome))             shrineMapChance = 0;
            else if (canGiveMap(biome, VillagerType.DESERT))      shrineMapChance = 0.8;
            else                                                shrineMapChance = 0.2;

            if (powerType == ModPowers.HAMON.get())        shrineMapChance *= 0.0625;
            if (powerType == null)                                  shrineMapChance *= 0.5;

            return shrineMapChance;
        }
    };

    @Inject(method = "onTrade", at = @At("HEAD"))
    private void playBuySound(PlayerEntity player, ItemStack stack, MerchantInventory slots, MerchantOffer offer, CallbackInfo ci) {
        if (onFirstBuyFlavorSound != null && name.equals("shrine_map")) {
            IMerchant merchant = CommonReflection.getMerchant(slots);
            if (merchant instanceof Entity) {
                Entity merchantEntity = (Entity) merchant;
                player.getCapability(PlayerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    cap.doWhen(
                            () -> PacketManager.sendToClientsTrackingAndSelf(new TrEntitySpecialEffectPacket(
                                    merchantEntity.getId(), onFirstBuyFlavorSound, player.getId()), merchantEntity),
                            () -> player.containerMenu == player.inventoryMenu);
                });
            }
        }
    }
}
