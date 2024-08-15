package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class ComponentVillageStartPiece extends ComponentVillageWell {
   public WorldChunkManager worldChunkMngr;
   public boolean inDesert;
   public int terrainType;
   public StructureVillagePieceWeight structVillagePieceWeight;
   public List structureVillageWeightedPieceList;
   public List field_74932_i = new ArrayList();
   public List field_74930_j = new ArrayList();

   public ComponentVillageStartPiece() {
   }

   public ComponentVillageStartPiece(WorldChunkManager var1, int var2, Random var3, int var4, int var5, List var6, int var7) {
      super((ComponentVillageStartPiece)null, 0, var3, var4, var5);
      this.worldChunkMngr = var1;
      this.structureVillageWeightedPieceList = var6;
      this.terrainType = var7;
      BiomeGenBase var8 = var1.getBiomeGenAt(var4, var5);
      this.inDesert = var8 == BiomeGenBase.desert || var8 == BiomeGenBase.desertHills;
   }

   public WorldChunkManager getWorldChunkManager() {
      return this.worldChunkMngr;
   }
}
