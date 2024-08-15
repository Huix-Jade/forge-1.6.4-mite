package net.minecraft.world.gen.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class MapGenStructureData extends WorldSavedData {
   private NBTTagCompound field_143044_a = new NBTTagCompound("Features");

   public MapGenStructureData(String var1) {
      super(var1);
   }

   public void readFromNBT(NBTTagCompound var1) {
      this.field_143044_a = var1.getCompoundTag("Features");
   }

   public void writeToNBT(NBTTagCompound var1) {
      var1.setTag("Features", this.field_143044_a);
   }

   public void func_143043_a(NBTTagCompound var1, int var2, int var3) {
      String var4 = this.func_143042_b(var2, var3);
      var1.setName(var4);
      this.field_143044_a.setTag(var4, var1);
   }

   public String func_143042_b(int var1, int var2) {
      return "[" + var1 + "," + var2 + "]";
   }

   public NBTTagCompound func_143041_a() {
      return this.field_143044_a;
   }
}
