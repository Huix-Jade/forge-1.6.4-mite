package net.minecraft.item;

import net.minecraft.block.material.Material;

public class ItemClub extends ItemCudgel {
   protected ItemClub(int par1, Material material) {
      super(par1, material);
      this.setReachBonus(0.5F);
   }

   public String getToolType() {
      return "club";
   }

   public float getBaseDamageVsEntity() {
      return super.getBaseDamageVsEntity() + 1.0F;
   }

   public boolean canBlock() {
      return true;
   }

   public int getNumComponentsForDurability() {
      return 2;
   }
}
