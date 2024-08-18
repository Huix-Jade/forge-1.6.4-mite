package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.ArrayList;
import java.util.Random;

public class ItemShears extends ItemTool {
   public ItemShears(int par1, Material material) {
      super(par1, material);
      this.addMaterialsEffectiveAgainst(new Material[]{Material.cloth, Material.tree_leaves, Material.plants, Material.vine, Material.web});
      this.addBlocksEffectiveAgainst(new Block[]{Block.tripWire});
      this.setReachBonus(0.5F);
   }

   public String getToolType() {
      return "shears";
   }

   public float getBaseHarvestEfficiency(Block block) {
      return 4.0F;
   }

   public float getBaseDamageVsEntity() {
      return 0.0F;
   }

   public boolean canBlock() {
      return false;
   }

   public int getNumComponentsForDurability() {
      return 2;
   }

   public float getBaseDecayRateForBreakingBlock(Block block) {
      return 1.0F;
   }

   public float getBaseDecayRateForAttackingEntity(ItemStack item_stack) {
      return 2.0F;
   }

//   public boolean tryEntityInteraction(Entity entity, EntityPlayer player, ItemStack item_stack) {
//      if (entity instanceof EntitySheep) {
//         EntitySheep sheep = (EntitySheep)entity;
//         if (!sheep.getSheared() && !sheep.isChild()) {
//            if (player.onServer()) {
//               sheep.setSheared(true);
//               int num_drops = 1 + sheep.rand.nextInt(3);
//
//               for(int i = 0; i < num_drops; ++i) {
//                  EntityItem entity_item = sheep.dropItemStack(new ItemStack(Block.cloth.blockID, 1, sheep.getFleeceColor()), 1.0F);
//                  entity_item.motionY += (double)(sheep.rand.nextFloat() * 0.05F);
//                  entity_item.motionX += (double)((sheep.rand.nextFloat() - sheep.rand.nextFloat()) * 0.1F);
//                  entity_item.motionZ += (double)((sheep.rand.nextFloat() - sheep.rand.nextFloat()) * 0.1F);
//               }
//
//               player.tryDamageHeldItem(DamageSource.generic, 50);
//               sheep.playSound("mob.sheep.shear", 1.0F, 1.0F);
//            }
//
//            return true;
//         }
//      }
//
//      return false;
//   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, false);
      if (rc != null && rc.isBlock() && rc.canPlayerEditBlockHit(player, player.getHeldItemStack())) {
         Block block = rc.getBlockHit();
         if (block.canSilkHarvest(rc.block_hit_metadata) && this.isEffectiveAgainstBlock(block, rc.block_hit_metadata)) {
            if (player.onClient()) {
               player.swingArm();
            } else {
               World world = player.getWorld();
               int x = rc.block_hit_x;
               int y = rc.block_hit_y;
               int z = rc.block_hit_z;
               BlockBreakInfo info = (new BlockBreakInfo(world, x, y, z)).setHarvestedBy(player);
               info.dropBlockAsItself(true);
               world.playSoundAtBlock(x, y, z, "mob.sheep.shear", 1.0F, 1.0F);
               player.tryDamageHeldItem(DamageSource.generic, this.getToolDecayFromBreakingBlock(info));
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean hasWoodenHandle() {
      return false;
   }

   @Override
   public boolean tryEntityInteraction(Entity entity, EntityPlayer player, ItemStack item_stack)
   {
      if (entity.worldObj.isRemote)
      {
         return false;
      }
      if (entity instanceof IShearable)
      {
         IShearable target = (IShearable) entity;
         ItemStack itemstack = player.itemInUse;
         if (target.isShearable(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ))
         {
            ArrayList<ItemStack> drops = target.onSheared(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ,
                    EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));

            Random rand = new Random();
            for(ItemStack stack : drops)
            {
               EntityItem ent = entity.dropItemStack(stack, 1.0F);
               ent.motionY += rand.nextFloat() * 0.05F;
               ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
               ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
            }
            itemstack.tryDamageItem(DamageSource.generic, 50, player);
         }
         return true;
      }
      return false;
   }

   @Override
   public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player)
   {
      if (player.worldObj.isRemote)
      {
         return false;
      }
      int id = player.worldObj.getBlockId(x, y, z);
      if (Block.blocksList[id] instanceof IShearable)
      {
         IShearable target = (IShearable) Block.blocksList[id];
          if (target.isShearable(itemstack, player.worldObj, x, y, z))
         {
            ArrayList<ItemStack> drops = target.onSheared(itemstack, player.worldObj, x, y, z,
                    EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
            Random rand = new Random();

            for(ItemStack stack : drops)
            {
               float f = 0.7F;
               double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
               double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
               double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
               EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, stack);
               entityitem.delayBeforeCanPickup = 10;
               player.worldObj.spawnEntityInWorld(entityitem);
            }

            itemstack.tryDamageItem(DamageSource.generic, 50, player);
            player.addStat(StatList.mineBlockStatArray[id], 1);
         }
      }
      return false;
   }
}
