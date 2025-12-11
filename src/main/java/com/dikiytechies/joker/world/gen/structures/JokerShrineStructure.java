package com.dikiytechies.joker.world.gen.structures;

import com.dikiytechies.joker.AddonMain;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.Level;

public class JokerShrineStructure extends Structure<NoFeatureConfig> {
    public JokerShrineStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }
    @Override
    public GenerationStage.Decoration step() { return GenerationStage.Decoration.SURFACE_STRUCTURES; }
    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed, SharedSeedRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig) {
        BlockPos chunkCenter = new BlockPos(chunkX << 4 + 7, 0, chunkZ << 4 + 7);

        int landHeight = chunkGenerator.getFirstOccupiedHeight(chunkCenter.getX(), chunkCenter.getZ(), Heightmap.Type.WORLD_SURFACE_WG) + 1; // don't forget to move the pointer above ground
        IBlockReader columnOfBlocks = chunkGenerator.getBaseColumn(chunkCenter.getX(), chunkCenter.getZ());
        BlockState topBlock = columnOfBlocks.getBlockState(chunkCenter.above(landHeight));

        return topBlock.getFluidState().isEmpty();
    }
    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return Start::new;
    }

    public static class Start extends StructureStart<NoFeatureConfig> {
        public Start(Structure<NoFeatureConfig> structureIn, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int referenceIn, long seedIn) {
            super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
        }

        @Override
        public void generatePieces(DynamicRegistries dynamicRegistryManager, ChunkGenerator chunkGenerator,
                                   TemplateManager templateManager, int chunkX, int chunkZ, Biome biome, NoFeatureConfig config) {
            int centerX = (chunkX << 4) + 7;
            int centerZ = (chunkZ << 4) + 7;
            BlockPos centerPos = new BlockPos(centerX, 0, centerZ);
            JigsawManager.addPieces(dynamicRegistryManager,
                    new VillageConfig(() -> dynamicRegistryManager.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
                            .get(new ResourceLocation(AddonMain.MOD_ID, "shrine/start_pool")),
                            10),
                    AbstractVillagePiece::new,
                    chunkGenerator,
                    templateManager,
                    centerPos,
                    this.pieces,
                    this.random,
                    false,
                    true);
            Vector3i structureCenter = this.pieces.get(0).getBoundingBox().getCenter();
            int xOffset = centerPos.getX() - structureCenter.getX();
            int zOffset = centerPos.getZ() - structureCenter.getZ();
            for (StructurePiece piece: this.pieces)
                piece.move(xOffset, 0, zOffset);
            this.calculateBoundingBox();
        }
    }
}
