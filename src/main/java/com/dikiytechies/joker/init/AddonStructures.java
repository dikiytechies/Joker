package com.dikiytechies.joker.init;

import com.dikiytechies.joker.AddonConfig;
import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.util.ForgeBusEventSubscriber;
import com.dikiytechies.joker.world.gen.structures.JokerShrineStructure;
import com.github.standobyte.jojo.util.mc.reflection.CommonReflection;
import com.github.standobyte.jojo.world.gen.ConfiguredStructureSupplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AddonStructures {
    public static final DeferredRegister<Structure<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, AddonMain.MOD_ID);
    public static final Predicate<BiomeLoadingEvent> SHRINE_BIOMES = biome -> biome.getCategory() == Biome.Category.MESA;
    public static final RegistryObject<Structure<NoFeatureConfig>> SHRINE = STRUCTURES.register("shrine", () -> (new JokerShrineStructure(NoFeatureConfig.CODEC)));
    public static final ConfiguredStructureSupplier<?, ?> CONFIGURED_SHRINE = new ConfiguredStructureSupplier<>(SHRINE, IFeatureConfig.NONE);

    @SubscribeEvent(priority = EventPriority.LOW)
    public static final void afterStructuresRegister(RegistryEvent.Register<Structure<?>> event) {
        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;

        setupMapSpacingAndLand(SHRINE.get(), new StructureSeparationSettings(40, 20, 228001337), true);

        registerConfiguredStructure(registry, CONFIGURED_SHRINE.get(),
                new ResourceLocation(AddonMain.MOD_ID, "configured_shrine"), SHRINE.get(),
                SHRINE_BIOMES.and(b -> AddonConfig.getCommonConfigInstance(false).enableShrineGeneration.get()));
    }
    private static <F extends Structure<?>> void setupMapSpacingAndLand(
            F structure,
            StructureSeparationSettings structureSeparationSettings,
            boolean transformSurroundingLand) {
        Structure.STRUCTURES_REGISTRY.put(structure.getRegistryName().toString(), structure);

        if (transformSurroundingLand) {
            Structure.NOISE_AFFECTING_FEATURES = ImmutableList.<Structure<?>>builder()
                    .addAll(Structure.NOISE_AFFECTING_FEATURES)
                    .add(structure)
                    .build();
        }

        setMapSpacing(structure, structureSeparationSettings);
    }
    public static <F extends Structure<?>> void setMapSpacing(F structure, StructureSeparationSettings structureSeparationSettings) {
        DimensionStructuresSettings.DEFAULTS = ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
                .putAll(DimensionStructuresSettings.DEFAULTS)
                .put(structure, structureSeparationSettings)
                .build();

        WorldGenRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings -> {
            Map<Structure<?>, StructureSeparationSettings> structureMap = settings.getValue().structureSettings().structureConfig();

            if (structureMap instanceof ImmutableMap) {
                Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(structureMap);
                tempMap.put(structure, structureSeparationSettings);
                settings.getValue().structureSettings().structureConfig = tempMap;
            }
            else{
                structureMap.put(structure, structureSeparationSettings);
            }
        });
    }
    private static void registerConfiguredStructure(Registry<StructureFeature<?, ?>> registry, StructureFeature<?, ?> configured,
                                                    ResourceLocation resLoc, Structure<?> structure, @Nullable Predicate<BiomeLoadingEvent> structureBiome) {
        Registry.register(registry, resLoc, configured);
        CommonReflection.flatGenSettingsStructures().put(structure, configured);
        if (structureBiome != null) {
            ForgeBusEventSubscriber.structureBiomes.put(() -> configured, structureBiome);
        }
    }
}
