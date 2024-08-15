package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

abstract class ComponentScatteredFeature extends StructureComponent {
   protected int scatteredFeatureSizeX;
   protected int scatteredFeatureSizeY;
   protected int scatteredFeatureSizeZ;
   protected int field_74936_d = -1;

   public ComponentScatteredFeature() {
   }

   protected ComponentScatteredFeature(Random var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      super(0);
      this.scatteredFeatureSizeX = var5;
      this.scatteredFeatureSizeY = var6;
      this.scatteredFeatureSizeZ = var7;
      this.coordBaseMode = var1.nextInt(4);
      switch (this.coordBaseMode) {
         case 0:
         case 2:
            this.boundingBox = new StructureBoundingBox(var2, var3, var4, var2 + var5 - 1, var3 + var6 - 1, var4 + var7 - 1);
            break;
         default:
            this.boundingBox = new StructureBoundingBox(var2, var3, var4, var2 + var7 - 1, var3 + var6 - 1, var4 + var5 - 1);
      }

   }

   protected void func_143012_a(NBTTagCompound var1) {
      var1.setInteger("Width", this.scatteredFeatureSizeX);
      var1.setInteger("Height", this.scatteredFeatureSizeY);
      var1.setInteger("Depth", this.scatteredFeatureSizeZ);
      var1.setInteger("HPos", this.field_74936_d);
   }

   protected void func_143011_b(NBTTagCompound var1) {
      this.scatteredFeatureSizeX = var1.getInteger("Width");
      this.scatteredFeatureSizeY = var1.getInteger("Height");
      this.scatteredFeatureSizeZ = var1.getInteger("Depth");
      this.field_74936_d = var1.getInteger("HPos");
   }

   protected boolean func_74935_a(World var1, StructureBoundingBox var2, int var3) {
      if (this.field_74936_d >= 0) {
         return true;
      } else {
         int var4 = 0;
         int var5 = 0;

         for(int var6 = this.boundingBox.minZ; var6 <= this.boundingBox.maxZ; ++var6) {
            for(int var7 = this.boundingBox.minX; var7 <= this.boundingBox.maxX; ++var7) {
               if (var2.isVecInside(var7, 64, var6)) {
                  var4 += Math.max(var1.getTopSolidOrLiquidBlock(var7, var6), var1.provider.getAverageGroundLevel());
                  ++var5;
               }
            }
         }

         if (var5 == 0) {
            return false;
         } else {
            this.field_74936_d = var4 / var5;
            this.boundingBox.offset(0, this.field_74936_d - this.boundingBox.minY + var3, 0);
            return true;
         }
      }
   }
}
