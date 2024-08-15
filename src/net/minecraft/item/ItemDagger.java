package net.minecraft.item;

import net.minecraft.block.material.Material;

public class ItemDagger extends ItemSword {
   protected ItemDagger(int par1, Material material) {
      super(par1, material);
      this.setReachBonus(0.5F);
   }

   public String getToolType() {
      return "dagger";
   }

   public float getBaseDamageVsEntity() {
      return super.getBaseDamageVsEntity() - 2.0F;
   }

   public boolean canBlock() {
      return false;
   }

   public int getNumComponentsForDurability() {
      return 1;
   }
}
