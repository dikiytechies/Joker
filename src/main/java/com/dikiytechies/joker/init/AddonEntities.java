package com.dikiytechies.joker.init;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.entity.mob.JokerIggyEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AddonEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, AddonMain.MOD_ID);

    public static final RegistryObject<EntityType<JokerIggyEntity>> JOKER_IGGY = ENTITIES.register("joker_iggy",
            () -> EntityType.Builder.<JokerIggyEntity>of(JokerIggyEntity::new, EntityClassification.MISC)
                    .sized(0.65f, 0.7f)
                    .fireImmune()
                    .build(new ResourceLocation(AddonMain.MOD_ID, "joker_iggy").toString()));
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(JOKER_IGGY.get(), JokerIggyEntity.createAttributes().build());
    }
}
