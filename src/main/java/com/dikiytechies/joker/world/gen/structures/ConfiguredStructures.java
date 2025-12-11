package com.dikiytechies.joker.world.gen.structures;

import com.dikiytechies.joker.util.ForgeBusEventSubscriber;
import com.github.standobyte.jojo.util.mc.reflection.CommonReflection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import javax.annotation.Nullable;
import java.util.function.Predicate;

import static com.dikiytechies.joker.init.AddonStructures.SHRINE;

public class ConfiguredStructures {
    public static final StructureFeature<?, ?> CONFIGURED_SHRINE = SHRINE.get().configured(IFeatureConfig.NONE);

    public static void registerConfiguredStructure(Registry<StructureFeature<?, ?>> registry, StructureFeature<?, ?> configured,
                                                    ResourceLocation resLoc, Structure<?> structure, @Nullable Predicate<BiomeLoadingEvent> structureBiome) {
        Registry.register(registry, resLoc, configured);
        CommonReflection.flatGenSettingsStructures().put(structure, configured);
        if (structureBiome != null) {
            ForgeBusEventSubscriber.structureBiomes.put(() -> configured, structureBiome);
        }
    }
}
