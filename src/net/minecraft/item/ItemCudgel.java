package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class ItemCudgel extends ItemTool {
   protected ItemCudgel(int par1, Material material) {
      super(par1, material);
      this.addMaterialsEffectiveAgainst(new Material[]{Material.cake, Material.coral, Material.glass, Material.ice, Material.pumpkin});
      this.setReachBonus(0.25F);
      this.setCreativeTab(CreativeTabs.tabCombat);
   }

   public String getToolType() {
      return "cudgel";
   }

   public float getBaseHarvestEfficiency(Block block) {
      return 2.0F;
   }

   public float getBaseDamageVsEntity() {
      return 1.0F;
   }

   public boolean canBlock() {
      return false;
   }

   public int getNumComponentsForDurability() {
      return 1;
   }

   public float getBaseDecayRateForBreakingBlock(Block block) {
      return 0.25F;
   }

   public float getBaseDecayRateForAttackingEntity(ItemStack item_stack) {
      return 0.25F;
   }

   public int getSimilarityToItem(Item item) {
      return item != Item.stick && item != Item.bone ? super.getSimilarityToItem(item) : 1;
   }
}
