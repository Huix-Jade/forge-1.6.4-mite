package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;

public class ComponentNetherBridgeStartPiece extends ComponentNetherBridgeCrossing3 {
   public StructureNetherBridgePieceWeight theNetherBridgePieceWeight;
   public List primaryWeights;
   public List secondaryWeights;
   public ArrayList field_74967_d = new ArrayList();

   public ComponentNetherBridgeStartPiece() {
   }

   public ComponentNetherBridgeStartPiece(Random var1, int var2, int var3) {
      super(var1, var2, var3);
      this.primaryWeights = new ArrayList();
      StructureNetherBridgePieceWeight[] var4 = StructureNetherBridgePieces.getPrimaryComponents();
      int var5 = var4.length;

      int var6;
      StructureNetherBridgePieceWeight var7;
      for(var6 = 0; var6 < var5; ++var6) {
         var7 = var4[var6];
         var7.field_78827_c = 0;
         this.primaryWeights.add(var7);
      }

      this.secondaryWeights = new ArrayList();
      var4 = StructureNetherBridgePieces.getSecondaryComponents();
      var5 = var4.length;

      for(var6 = 0; var6 < var5; ++var6) {
         var7 = var4[var6];
         var7.field_78827_c = 0;
         this.secondaryWeights.add(var7);
      }

   }

   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
   }

   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
   }
}
