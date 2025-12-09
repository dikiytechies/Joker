package com.dikiytechies.joker.world.gen.structures;

import com.dikiytechies.joker.AddonMain;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
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
    private static IStructurePieceType SHRINE_PIECE = IStructurePieceType.setPieceId(JokerShrinePieces.Piece::new, AddonMain.MOD_ID + ":Shrine");;
    private static final ResourceLocation SHRINE = new ResourceLocation(AddonMain.MOD_ID, "shrine");
    public static void start(TemplateManager templateManager, BlockPos pos, List<StructurePiece> pieceList, Random random) {
        pieceList.add(new JokerShrinePieces.Piece(templateManager, SHRINE, pos, Rotation.NONE));
    }
    private static class Piece extends TemplateStructurePiece {
        private final ResourceLocation piece;
        private final Rotation rotation;

        public Piece(TemplateManager templateManager, ResourceLocation piece, BlockPos blockPos, Rotation rotation) {
            super(SHRINE_PIECE, 0);
            this.piece = piece;
            this.templatePosition = blockPos;
            this.rotation = rotation;
            this.setupPiece(templateManager);
        }
        public Piece(TemplateManager templateManager, CompoundNBT cnbt) {
            super(SHRINE_PIECE, cnbt);
            this.piece = new ResourceLocation(cnbt.getString("Template"));
            this.rotation = Rotation.valueOf(cnbt.getString("Rotation"));
            this.setupPiece(templateManager);
        }

        private void setupPiece(TemplateManager templateManager) {
            Template template = templateManager.getOrCreate(piece);
            PlacementSettings placementsettings = new PlacementSettings().setRotation(rotation).setMirror(Mirror.NONE);
            setup(template, templatePosition, placementsettings);
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT cnbt) {
            super.addAdditionalSaveData(cnbt);
            cnbt.putString("Template", piece.toString());
            cnbt.putString("Rotation", rotation.name());
        }

        @Override
        protected void handleDataMarker(String function, BlockPos pos, IServerWorld world, Random rand, MutableBoundingBox sbb) {}
    }
}
