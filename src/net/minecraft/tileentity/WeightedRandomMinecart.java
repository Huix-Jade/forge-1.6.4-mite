package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomItem;

public class WeightedRandomMinecart extends WeightedRandomItem {
   public final NBTTagCompound field_98222_b;
   public final String minecartName;
   // $FF: synthetic field
   final MobSpawnerBaseLogic field_98221_d;

   public WeightedRandomMinecart(MobSpawnerBaseLogic var1, NBTTagCompound var2) {
      super(var2.getInteger("Weight"));
      this.field_98221_d = var1;
      NBTTagCompound var3 = var2.getCompoundTag("Properties");
      String var4 = var2.getString("Type");
      if (var4.equals("Minecart")) {
         if (var3 != null) {
            switch (var3.getInteger("Type")) {
               case 0:
                  var4 = "MinecartRideable";
                  break;
               case 1:
                  var4 = "MinecartChest";
                  break;
               case 2:
                  var4 = "MinecartFurnace";
            }
         } else {
            var4 = "MinecartRideable";
         }
      }

      this.field_98222_b = var3;
      this.minecartName = var4;
   }

   public WeightedRandomMinecart(MobSpawnerBaseLogic var1, NBTTagCompound var2, String var3) {
      super(1);
      this.field_98221_d = var1;
      if (var3.equals("Minecart")) {
         if (var2 != null) {
            switch (var2.getInteger("Type")) {
               case 0:
                  var3 = "MinecartRideable";
                  break;
               case 1:
                  var3 = "MinecartChest";
                  break;
               case 2:
                  var3 = "MinecartFurnace";
            }
         } else {
            var3 = "MinecartRideable";
         }
      }

      this.field_98222_b = var2;
      this.minecartName = var3;
   }

   public NBTTagCompound func_98220_a() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.setCompoundTag("Properties", this.field_98222_b);
      var1.setString("Type", this.minecartName);
      var1.setInteger("Weight", this.itemWeight);
      return var1;
   }
}
