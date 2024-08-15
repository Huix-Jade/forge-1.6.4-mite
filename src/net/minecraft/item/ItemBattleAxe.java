package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class ItemBattleAxe extends ItemAxe {
   protected ItemBattleAxe(int par1, Material material) {
      super(par1, material);
      this.setCreativeTab(CreativeTabs.tabCombat);
   }

   public String getToolType() {
      return "battle_axe";
   }

   public float getBaseHarvestEfficiency(Block block) {
      return super.getBaseHarvestEfficiency(block) * 0.75F;
   }

   public float getBaseDamageVsEntity() {
      return super.getBaseDamageVsEntity() + 1.0F;
   }

   public int getNumComponentsForDurability() {
      return 4;
   }

   public float getBaseDecayRateForBreakingBlock(Block block) {
      return super.getBaseDecayRateForBreakingBlock(block) * 1.25F;
   }

   public float getBaseDecayRateForAttackingEntity(ItemStack item_stack) {
      return super.getBaseDecayRateForAttackingEntity(item_stack) * 0.75F;
   }

   public Class[] getSimilarClasses() {
      return this.spliceClassArrays(ItemTool.weapon_classes, new Class[]{ItemAxe.class, ItemHatchet.class});
   }
}
