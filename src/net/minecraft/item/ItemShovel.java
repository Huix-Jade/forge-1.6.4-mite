package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ItemShovel extends ItemTool {
   protected ItemShovel(int par1, Material material) {
      super(par1, material);
      this.addMaterialsEffectiveAgainst(new Material[]{Material.cake, Material.clay, Material.craftedSnow, Material.grass, Material.dirt, Material.sand, Material.snow});
      this.addBlocksEffectiveAgainst(new Block[]{Block.carrot, Block.onions, Block.potato, Block.thinGlass});
      if (material.isMetal()) {
         this.addBlocksEffectiveAgainst(new Block[]{Block.glass});
      }

   }

   public String getToolType() {
      return "shovel";
   }

   public float getBaseDamageVsEntity() {
      return 1.0F;
   }

   public int getNumComponentsForDurability() {
      return 1;
   }

   public float getBaseDecayRateForBreakingBlock(Block block) {
      return 0.5F;
   }

   public float getBaseDecayRateForAttackingEntity(ItemStack item_stack) {
      return 1.0F;
   }

   public Class[] getSimilarClasses() {
      return new Class[]{ItemMattock.class, ItemHoe.class};
   }
}
