package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ItemAxe extends ItemTool {
   protected ItemAxe(int par1, Material material) {
      super(par1, material);
      this.addMaterialsEffectiveAgainst(new Material[]{Material.cactus, Material.clay, Material.glass, Material.hardened_clay, Material.ice, Material.pumpkin, Material.wood});
      this.addBlocksEffectiveAgainst(new Block[]{Block.ladder, Block.reed, Block.sandStone});
   }

   public String getToolType() {
      return "axe";
   }

   public float getBaseHarvestEfficiency(Block block) {
      return block == Block.sandStone ? super.getBaseHarvestEfficiency(block) * 0.5F : super.getBaseHarvestEfficiency(block);
   }

   public float getBaseDamageVsEntity() {
      return 3.0F;
   }

   public int getNumComponentsForDurability() {
      return 3;
   }

   public float getBaseDecayRateForBreakingBlock(Block block) {
      return block == Block.sandStone ? 1.875F : 1.0F;
   }

   public float getBaseDecayRateForAttackingEntity(ItemStack item_stack) {
      return 1.0F;
   }

   public Class[] getSimilarClasses() {
      return this.spliceClassArrays(new Class[]{ItemBattleAxe.class, ItemHatchet.class}, ItemTool.weapon_classes);
   }
}
