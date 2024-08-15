package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class ItemSword extends ItemTool {
   protected ItemSword(int par1, Material material) {
      super(par1, material);
      this.addMaterialsEffectiveAgainst(new Material[]{Material.materialCarpet, Material.cloth, Material.tree_leaves, Material.plants, Material.pumpkin, Material.vine, Material.web});
      this.setCreativeTab(CreativeTabs.tabCombat);
   }

   public String getToolType() {
      return "sword";
   }

   public float getBaseHarvestEfficiency(Block block) {
      return 2.0F;
   }

   public float getBaseDamageVsEntity() {
      return 4.0F;
   }

   public int getNumComponentsForDurability() {
      return 2;
   }

   public float getBaseDecayRateForBreakingBlock(Block block) {
      return 2.0F;
   }

   public float getBaseDecayRateForAttackingEntity(ItemStack item_stack) {
      return 0.5F;
   }

   public Class[] getSimilarClasses() {
      return ItemTool.weapon_classes;
   }
}
