package com.dikiytechies.joker.world.gen.structures;

import com.dikiytechies.joker.AddonMain;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

public class JokerShrinePieces {
    private static IStructurePieceType SHRINE_PIECES;
    private static final ResourceLocation SHRINE = new ResourceLocation(AddonMain.MOD_ID, "shrine");
    public static void start(TemplateManager templateManager, BlockPos pos, List<StructurePiece> pieceList, Random random) {
        pieceList.add(new JokerShrinePieces.Piece(templateManager, SHRINE, pos));
    }
    private static class Piece extends TemplateStructurePiece {
        private final ResourceLocation piece;

        public Piece(TemplateManager templateManager, ResourceLocation piece, BlockPos blockPos) {
            super(SHRINE_PIECES, 0);
            this.piece = piece;
            this.templatePosition = blockPos;
            this.setupPiece(templateManager);
        }
        //todo fix structure no id exception
        public Piece(TemplateManager templateManager, CompoundNBT cnbt) {
            super(SHRINE_PIECES, cnbt);
            this.piece = new ResourceLocation(cnbt.getString("Template"));
            this.setupPiece(templateManager);
        }

        private void setupPiece(TemplateManager templateManager) {
            Template template = templateManager.getOrCreate(piece);
            setup(template, templatePosition, new PlacementSettings().setMirror(Mirror.NONE));
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT cnbt) {
            super.addAdditionalSaveData(cnbt);
            cnbt.putString("Template", piece.toString());
        }

        @Override
        protected void handleDataMarker(String function, BlockPos pos, IServerWorld world, Random rand, MutableBoundingBox sbb) {}
    }
}
