package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;

public class ItemScythe extends ItemTool {
   protected ItemScythe(int par1, Material material) {
      super(par1, material);
      this.addBlocksEffectiveAgainst(new Block[]{Block.tallGrass, Block.crops});
      this.setReachBonus(1.0F);
   }

   public String getToolType() {
      return "scythe";
   }

   public float getBaseDamageVsEntity() {
      return 1.0F;
   }

   public int getNumComponentsForDurability() {
      return 2;
   }

   public float getBaseDecayRateForBreakingBlock(Block block) {
      return block != Block.tallGrass && block != Block.crops ? 2.0F : 0.5F;
   }

   public float getBaseDecayRateForAttackingEntity(ItemStack item_stack) {
      if (EnchantmentHelper.hasEnchantment(item_stack, Enchantment.vampiric)) {
         return 1.0F;
      } else {
         return EnchantmentHelper.hasEnchantment(item_stack, Enchantment.sharpness) ? 2.0F : 4.0F;
      }
   }

   public boolean onBlockDestroyed(BlockBreakInfo info) {
      if (!info.world.isRemote) {
         Block block = Block.getBlock(info.block_id);
         if (block.getClass() == BlockCrops.class) {
            BlockCrops crops = (BlockCrops)block;
            if (crops.isMature(info.getMetadata()) && Math.random() < (double)EnchantmentHelper.getEnchantmentLevelFraction(Enchantment.fertility, info.getHarvesterItemStack())) {
               BlockFarmland.setFertilized(info.world, info.x, info.y - 1, info.z, true);
            }
         }
      }

      return super.onBlockDestroyed(info);
   }

   public Class[] getSimilarClasses() {
      return new Class[]{ItemSword.class, ItemDagger.class, ItemKnife.class};
   }
}
