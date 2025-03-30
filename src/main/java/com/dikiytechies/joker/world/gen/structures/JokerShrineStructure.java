package com.dikiytechies.joker.world.gen.structures;

import net.minecraft.block.BlockState;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class JokerShrineStructure extends Structure<NoFeatureConfig> {
    public JokerShrineStructure() {
        super(NoFeatureConfig.CODEC);
    }
    @Override
    public GenerationStage.Decoration step() { return GenerationStage.Decoration.SURFACE_STRUCTURES; }
    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed,
                                     SharedSeedRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig) {
        int x = (chunkX << 4) + 7;
        int z = (chunkZ << 4) + 7;
        BlockPos centerOfChunk = new BlockPos(x, 0, z);
        int landHeight = chunkGenerator.getFirstOccupiedHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG);

        IBlockReader columnOfBlocks = chunkGenerator.getBaseColumn(centerOfChunk.getX(), centerOfChunk.getZ());
        BlockState topBlock = columnOfBlocks.getBlockState(centerOfChunk.above(landHeight));

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
            int minY = chunkGenerator.getBaseHeight(centerX, centerZ, Heightmap.Type.WORLD_SURFACE_WG);
            BlockPos blockPos = new BlockPos(centerX, minY, centerZ);
            JokerShrinePieces.start(templateManager, blockPos, pieces, random);
            calculateBoundingBox();
        }
    }
}
