package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class ItemKnife extends ItemDagger {
   protected ItemKnife(int par1, Material material) {
      super(par1, material);
      this.setReachBonus(0.25F);
      this.setCreativeTab(CreativeTabs.tabTools);
   }

   public String getToolType() {
      return "knife";
   }

   public float getBaseDamageVsEntity() {
      return super.getBaseDamageVsEntity() - 1.0F;
   }

   public float getBaseDecayRateForBreakingBlock(Block block) {
      return block == null || block.blockMaterial != Material.cloth && block.blockMaterial != Material.plants && block.blockMaterial != Material.vine ? super.getBaseDecayRateForBreakingBlock(block) / 2.0F : super.getBaseDecayRateForBreakingBlock(block) / 4.0F;
   }
}
