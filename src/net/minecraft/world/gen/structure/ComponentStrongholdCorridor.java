package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ComponentStrongholdCorridor extends ComponentStronghold {
   private int field_74993_a;

   public ComponentStrongholdCorridor() {
   }

   public ComponentStrongholdCorridor(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.coordBaseMode = var4;
      this.boundingBox = var3;
      this.field_74993_a = var4 != 2 && var4 != 0 ? var3.getXSize() : var3.getZSize();
   }

   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.setInteger("Steps", this.field_74993_a);
   }

   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_74993_a = var1.getInteger("Steps");
   }

   public static StructureBoundingBox func_74992_a(List var0, Random var1, int var2, int var3, int var4, int var5) {
      boolean var6 = true;
      StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -1, -1, 0, 5, 5, 4, var5);
      StructureComponent var8 = StructureComponent.findIntersecting(var0, var7);
      if (var8 == null) {
         return null;
      } else {
         if (var8.getBoundingBox().minY == var7.minY) {
            for(int var9 = 3; var9 >= 1; --var9) {
               var7 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -1, -1, 0, 5, 5, var9 - 1, var5);
               if (!var8.getBoundingBox().intersectsWith(var7)) {
                  return StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -1, -1, 0, 5, 5, var9, var5);
               }
            }
         }

         return null;
      }
   }

   public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
      if (this.isLiquidInStructureBoundingBox(var1, var3)) {
         return false;
      } else {
         for(int var4 = 0; var4 < this.field_74993_a; ++var4) {
            this.placeBlockAtCurrentPosition(var1, Block.stoneBrick.blockID, 0, 0, 0, var4, var3);
            this.placeBlockAtCurrentPosition(var1, Block.stoneBrick.blockID, 0, 1, 0, var4, var3);
            this.placeBlockAtCurrentPosition(var1, Block.stoneBrick.blockID, 0, 2, 0, var4, var3);
            this.placeBlockAtCurrentPosition(var1, Block.stoneBrick.blockID, 0, 3, 0, var4, var3);
            this.placeBlockAtCurrentPosition(var1, Block.stoneBrick.blockID, 0, 4, 0, var4, var3);

            for(int var5 = 1; var5 <= 3; ++var5) {
               this.placeBlockAtCurrentPosition(var1, Block.stoneBrick.blockID, 0, 0, var5, var4, var3);
               this.placeBlockAtCurrentPosition(var1, 0, 0, 1, var5, var4, var3);
               this.placeBlockAtCurrentPosition(var1, 0, 0, 2, var5, var4, var3);
               this.placeBlockAtCurrentPosition(var1, 0, 0, 3, var5, var4, var3);
               this.placeBlockAtCurrentPosition(var1, Block.stoneBrick.blockID, 0, 4, var5, var4, var3);
            }

            this.placeBlockAtCurrentPosition(var1, Block.stoneBrick.blockID, 0, 0, 4, var4, var3);
            this.placeBlockAtCurrentPosition(var1, Block.stoneBrick.blockID, 0, 1, 4, var4, var3);
            this.placeBlockAtCurrentPosition(var1, Block.stoneBrick.blockID, 0, 2, 4, var4, var3);
            this.placeBlockAtCurrentPosition(var1, Block.stoneBrick.blockID, 0, 3, 4, var4, var3);
            this.placeBlockAtCurrentPosition(var1, Block.stoneBrick.blockID, 0, 4, 4, var4, var3);
         }

         return true;
      }
   }
}
