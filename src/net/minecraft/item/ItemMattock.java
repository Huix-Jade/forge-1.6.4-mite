package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;

public class ItemMattock extends ItemShovel {
   protected ItemMattock(int par1, Material material) {
      super(par1, material);
   }

   public String getToolType() {
      return "mattock";
   }

   public float getBaseHarvestEfficiency(Block block) {
      return super.getBaseHarvestEfficiency(block) * 0.75F;
   }

   public boolean canBlock() {
      return false;
   }

   public int getNumComponentsForDurability() {
      return 4;
   }

   public float getBaseDecayRateForBreakingBlock(Block block) {
      return super.getBaseDecayRateForBreakingBlock(block) * 0.8F;
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, true);
      if (rc != null && rc.isBlock()) {
         return rc.face_hit.isTop() ? ItemHoe.tryTillSoil(rc.world, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, rc.face_hit, player, player.getHeldItemStack()) : false;
      } else {
         return false;
      }
   }

   public boolean onBlockDestroyed(BlockBreakInfo info) {
      if (!info.world.isRemote) {
         Block block = Block.getBlock(info.block_id);
         if (block instanceof BlockCrops && this.isEffectiveAgainstBlock(block, info.getMetadata())) {
            BlockCrops crops = (BlockCrops)block;
            if (!crops.isDead() && crops.isMature(info.getMetadata()) && Math.random() < (double)EnchantmentHelper.getEnchantmentLevelFraction(Enchantment.fertility, info.getHarvesterItemStack())) {
               BlockFarmland.setFertilized(info.world, info.x, info.y - 1, info.z, true);
            }
         }
      }

      return super.onBlockDestroyed(info);
   }

   public Class[] getSimilarClasses() {
      return new Class[]{ItemHoe.class, ItemShovel.class};
   }
}
