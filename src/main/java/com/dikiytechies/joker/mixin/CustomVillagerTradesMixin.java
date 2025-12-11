package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.init.AddonStructures;
import com.github.standobyte.jojo.JojoMod;
import com.github.standobyte.jojo.capability.entity.PlayerUtilCap;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.init.ModStructures;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.github.standobyte.jojo.util.mc.CustomVillagerTrades;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import static com.github.standobyte.jojo.util.mc.CustomVillagerTrades.MapTrades.biomeHasOtherStructure;
import static com.github.standobyte.jojo.util.mc.CustomVillagerTrades.MapTrades.canGiveMap;

@Mixin(value = CustomVillagerTrades.MapTrades.MapTrade.class, remap = false)
public class CustomVillagerTradesMixin {
        private static final CustomVillagerTrades.MapTrades.MapTrade SHRINE_MAP = new CustomVillagerTrades.MapTrades.MapTrade(VillagerType.DESERT, "shrine_map",
                new CustomVillagerTrades.MapTrades.EmeraldForMapTrade(16, AddonStructures.SHRINE,
                        new ResourceLocation(AddonMain.MOD_ID, "textures/map/shrine.png"), OptionalInt.of(0x520411), 1, 15),
                new TranslationTextComponent("filled_map.joker:shrine"), ModSounds.MAP_BOUGHT_METEORITE.get(), PlayerUtilCap.OneTimeNotification.BOUGHT_METEORITE_MAP) {
            @Override
            public double getMapChance(@Nullable StandType<?> standType, @Nullable NonStandPowerType<?> powerType, VillagerData villager) {
                double shrineMapChance;

                if (powerType == null)                                  shrineMapChance = 0;
                else {
                    VillagerType biome = villager.getType();
                    if (biome == VillagerType.DESERT)                     shrineMapChance = 1;
                    else if (biomeHasOtherStructure(biome))             shrineMapChance = 0;
                    else if (canGiveMap(biome, VillagerType.DESERT))      shrineMapChance = 0.8;
                    else                                                shrineMapChance = 0.2;

                    if (powerType == ModPowers.HAMON.get())        shrineMapChance *= 0.0625;
                }

                return shrineMapChance;
            }
        };
}
