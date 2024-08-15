package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ItemHatchet extends ItemAxe {
   protected ItemHatchet(int par1, Material material) {
      super(par1, material);
      this.setReachBonus(0.5F);
   }

   public String getToolType() {
      return "hatchet";
   }

   public float getBaseHarvestEfficiency(Block block) {
      return super.getBaseHarvestEfficiency(block) * 0.5F;
   }

   public float getBaseDamageVsEntity() {
      return super.getBaseDamageVsEntity() - 1.0F;
   }

   public boolean canBlock() {
      return false;
   }

   public int getNumComponentsForDurability() {
      return 1;
   }

   public float getBaseDecayRateForBreakingBlock(Block block) {
      return super.getBaseDecayRateForBreakingBlock(block) * 4.0F / 3.0F;
   }

   public float getBaseDecayRateForAttackingEntity(ItemStack item_stack) {
      return super.getBaseDecayRateForAttackingEntity(item_stack) * 4.0F / 3.0F;
   }

   public Class[] getSimilarClasses() {
      return new Class[]{ItemAxe.class, ItemBattleAxe.class};
   }
}
