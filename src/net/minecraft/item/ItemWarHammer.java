package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class ItemWarHammer extends ItemPickaxe {
   protected ItemWarHammer(int par1, Material material) {
      super(par1, material);
      this.addMaterialsEffectiveAgainst(new Material[]{Material.cake, Material.pumpkin});
      this.setCreativeTab(CreativeTabs.tabCombat);
   }

   public String getToolType() {
      return "war_hammer";
   }

   public float getBaseHarvestEfficiency(Block block) {
      return super.getBaseHarvestEfficiency(block) * 0.75F;
   }

   public int getNumComponentsForDurability() {
      return 5;
   }

   public float getBaseDecayRateForBreakingBlock(Block block) {
      return super.getBaseDecayRateForBreakingBlock(block) * 2.0F / 3.0F;
   }

   public float getBaseDecayRateForAttackingEntity(ItemStack item_stack) {
      return super.getBaseDecayRateForAttackingEntity(item_stack) * 2.0F / 3.0F;
   }

   public Class[] getSimilarClasses() {
      return this.spliceClassArrays(new Class[]{ItemPickaxe.class}, ItemTool.weapon_classes);
   }
}
