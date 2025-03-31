package com.dikiytechies.joker.init;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.item.AddonArmorMaterials;
import com.dikiytechies.joker.item.MaskItem;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AddonItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AddonMain.MOD_ID);

    public static final RegistryObject<MaskItem> MASK = ITEMS.register("mask",
            () -> new MaskItem(AddonArmorMaterials.JOKER, EquipmentSlotType.HEAD, new Item.Properties()));
}
