package net.minecraft.world.biome;

import net.minecraft.util.WeightedRandomItem;

public class SpawnListEntry extends WeightedRandomItem {
   public Class entityClass;
   public int minGroupCount;
   public int maxGroupCount;

   public SpawnListEntry(Class var1, int var2, int var3, int var4) {
      super(var2);
      this.entityClass = var1;
      this.minGroupCount = var3;
      this.maxGroupCount = var4;
   }

   public String toString() {
      return this.entityClass.getSimpleName() + "*(" + this.minGroupCount + "-" + this.maxGroupCount + "):" + this.itemWeight;
   }
}
