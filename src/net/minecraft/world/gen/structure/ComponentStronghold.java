package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

abstract class ComponentStronghold extends StructureComponent {
   protected EnumDoor field_143013_d;

   public ComponentStronghold() {
      this.field_143013_d = EnumDoor.OPENING;
   }

   protected ComponentStronghold(int var1) {
      super(var1);
      this.field_143013_d = EnumDoor.OPENING;
   }

   protected void func_143012_a(NBTTagCompound var1) {
      var1.setString("EntryDoor", this.field_143013_d.name());
   }

   protected void func_143011_b(NBTTagCompound var1) {
      this.field_143013_d = EnumDoor.valueOf(var1.getString("EntryDoor"));
   }

   protected void placeDoor(World var1, Random var2, StructureBoundingBox var3, EnumDoor var4, int var5, int var6, int var7) {
      // $FF: Couldn't be decompiled
   }

   protected EnumDoor getRandomDoor(Random var1) {
      int var2 = var1.nextInt(5);
      switch (var2) {
         case 0:
         case 1:
         default:
            return EnumDoor.OPENING;
         case 2:
            return EnumDoor.WOOD_DOOR;
         case 3:
            return EnumDoor.GRATES;
         case 4:
            return EnumDoor.IRON_DOOR;
      }
   }

   protected StructureComponent getNextComponentNormal(ComponentStrongholdStairs2 var1, List var2, Random var3, int var4, int var5) {
      switch (this.coordBaseMode) {
         case 0:
            return StructureStrongholdPieces.getNextValidComponentAccess(var1, var2, var3, this.boundingBox.minX + var4, this.boundingBox.minY + var5, this.boundingBox.maxZ + 1, this.coordBaseMode, this.getComponentType());
         case 1:
            return StructureStrongholdPieces.getNextValidComponentAccess(var1, var2, var3, this.boundingBox.minX - 1, this.boundingBox.minY + var5, this.boundingBox.minZ + var4, this.coordBaseMode, this.getComponentType());
         case 2:
            return StructureStrongholdPieces.getNextValidComponentAccess(var1, var2, var3, this.boundingBox.minX + var4, this.boundingBox.minY + var5, this.boundingBox.minZ - 1, this.coordBaseMode, this.getComponentType());
         case 3:
            return StructureStrongholdPieces.getNextValidComponentAccess(var1, var2, var3, this.boundingBox.maxX + 1, this.boundingBox.minY + var5, this.boundingBox.minZ + var4, this.coordBaseMode, this.getComponentType());
         default:
            return null;
      }
   }

   protected StructureComponent getNextComponentX(ComponentStrongholdStairs2 var1, List var2, Random var3, int var4, int var5) {
      switch (this.coordBaseMode) {
         case 0:
            return StructureStrongholdPieces.getNextValidComponentAccess(var1, var2, var3, this.boundingBox.minX - 1, this.boundingBox.minY + var4, this.boundingBox.minZ + var5, 1, this.getComponentType());
         case 1:
            return StructureStrongholdPieces.getNextValidComponentAccess(var1, var2, var3, this.boundingBox.minX + var5, this.boundingBox.minY + var4, this.boundingBox.minZ - 1, 2, this.getComponentType());
         case 2:
            return StructureStrongholdPieces.getNextValidComponentAccess(var1, var2, var3, this.boundingBox.minX - 1, this.boundingBox.minY + var4, this.boundingBox.minZ + var5, 1, this.getComponentType());
         case 3:
            return StructureStrongholdPieces.getNextValidComponentAccess(var1, var2, var3, this.boundingBox.minX + var5, this.boundingBox.minY + var4, this.boundingBox.minZ - 1, 2, this.getComponentType());
         default:
            return null;
      }
   }

   protected StructureComponent getNextComponentZ(ComponentStrongholdStairs2 var1, List var2, Random var3, int var4, int var5) {
      switch (this.coordBaseMode) {
         case 0:
            return StructureStrongholdPieces.getNextValidComponentAccess(var1, var2, var3, this.boundingBox.maxX + 1, this.boundingBox.minY + var4, this.boundingBox.minZ + var5, 3, this.getComponentType());
         case 1:
            return StructureStrongholdPieces.getNextValidComponentAccess(var1, var2, var3, this.boundingBox.minX + var5, this.boundingBox.minY + var4, this.boundingBox.maxZ + 1, 0, this.getComponentType());
         case 2:
            return StructureStrongholdPieces.getNextValidComponentAccess(var1, var2, var3, this.boundingBox.maxX + 1, this.boundingBox.minY + var4, this.boundingBox.minZ + var5, 3, this.getComponentType());
         case 3:
            return StructureStrongholdPieces.getNextValidComponentAccess(var1, var2, var3, this.boundingBox.minX + var5, this.boundingBox.minY + var4, this.boundingBox.maxZ + 1, 0, this.getComponentType());
         default:
            return null;
      }
   }

   protected static boolean canStrongholdGoDeeper(StructureBoundingBox var0) {
      return var0 != null && var0.minY > 10;
   }
}
