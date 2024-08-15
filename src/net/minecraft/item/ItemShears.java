package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

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

   public boolean tryEntityInteraction(Entity entity, EntityPlayer player, ItemStack item_stack) {
      if (entity instanceof EntitySheep) {
         EntitySheep sheep = (EntitySheep)entity;
         if (!sheep.getSheared() && !sheep.isChild()) {
            if (player.onServer()) {
               sheep.setSheared(true);
               int num_drops = 1 + sheep.rand.nextInt(3);

               for(int i = 0; i < num_drops; ++i) {
                  EntityItem entity_item = sheep.dropItemStack(new ItemStack(Block.cloth.blockID, 1, sheep.getFleeceColor()), 1.0F);
                  entity_item.motionY += (double)(sheep.rand.nextFloat() * 0.05F);
                  entity_item.motionX += (double)((sheep.rand.nextFloat() - sheep.rand.nextFloat()) * 0.1F);
                  entity_item.motionZ += (double)((sheep.rand.nextFloat() - sheep.rand.nextFloat()) * 0.1F);
               }

               player.tryDamageHeldItem(DamageSource.generic, 50);
               sheep.playSound("mob.sheep.shear", 1.0F, 1.0F);
            }

            return true;
         }
      }

      return false;
   }

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
}
