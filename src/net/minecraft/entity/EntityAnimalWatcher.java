package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockUnderminable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIWatchAnimal;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCudgel;
import net.minecraft.item.ItemScythe;
import net.minecraft.item.ItemShovel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.World;

public class EntityAnimalWatcher extends EntityMob {
   public boolean is_destroying_block;
   public int destroy_block_x;
   public int destroy_block_y;
   public int destroy_block_z;
   protected int destroy_block_progress;
   public int destroy_block_cooloff = 40;
   public int destroy_pause_ticks;

   public EntityAnimalWatcher(World world) {
      super(world);
      this.tasks.addTask(1, new EntityAIWatchAnimal(this));
   }

   public boolean isHoldingItemThatPreventsDigging() {
      Item held_item = this.getHeldItem();
      return held_item instanceof ItemSword || held_item instanceof ItemCudgel || held_item instanceof ItemScythe;
   }

   public boolean isDiggingEnabled() {
      return !this.isHoldingItemThatPreventsDigging();
   }

   public boolean blockWillFall(int x, int y, int z) {
      Block block = this.worldObj.getBlock(x, y, z);
      return block instanceof BlockFalling || block == Block.cactus || block instanceof BlockTorch || block == Block.snow;
   }

   public void partiallyDestroyBlock() {
      int x = this.destroy_block_x;
      int y = this.destroy_block_y;
      int z = this.destroy_block_z;
      if (!this.canDestroyBlock(x, y, z, true)) {
         this.cancelBlockDestruction();
      } else {
         this.refreshDespawnCounter(-400);
         World world = this.worldObj;
         Block block = world.getBlock(x, y, z);
         if (block == Block.cactus && !this.isHoldingItemThatPreventsHandDamage()) {
            this.attackEntityFrom(new Damage(DamageSource.cactus, 1.0F));
         }

         if (++this.destroy_block_progress < 10) {
            this.is_destroying_block = true;
         } else {
            this.destroy_block_progress = -1;
            if (block.blockMaterial == Material.glass) {
               world.playAuxSFX(2001, x, y, z, block.blockID);
            }

            BlockBreakInfo info = (new BlockBreakInfo(world, x, y, z)).setHarvestedBy(this);
            block.dropBlockAsEntityItem(info);
            world.setBlockToAir(x, y, z);
            if (this.blockWillFall(x, y + 1, z)) {
               List entities = world.selectEntitiesWithinAABB(EntityLiving.class, this.boundingBox.expand(3.0, 1.0, 3.0), new EntitySelectorEntityLiving(true, true));
               Iterator i = entities.iterator();

               while(i.hasNext()) {
                  EntityLiving entity_living = (EntityLiving)i.next();
                  EntityAIAttackOnCollide ai = (EntityAIAttackOnCollide)entity_living.getEntityAITask(EntityAIAttackOnCollide.class);
                  if (ai != null) {
                     if (ai.ticks_suppressed < 10) {
                        ai.ticks_suppressed = 10;
                     }

                     if (ai.attackTick < 10) {
                        ai.attackTick = 10;
                     }
                  }
               }
            }

            ItemStack item_stack = this.getHeldItemStack();
            if (item_stack != null) {
               item_stack.getItem().onBlockDestroyed(info);
            }

            this.is_destroying_block = false;
            Block block_above = world.getBlock(x, y + 1, z);
            if (block_above instanceof BlockFalling) {
               this.is_destroying_block = true;
               this.destroy_pause_ticks = 10;
            } else if (block_above != null && !this.blockWillFall(x, y + 1, z)) {
               if (y == this.getFootBlockPosY() && this.canDestroyBlock(x, y + 1, z, true)) {
                  ++this.destroy_block_y;
               } else {
                  --this.destroy_block_y;
               }

               this.is_destroying_block = true;
               this.destroy_pause_ticks = 10;
            } else if (y == this.getFootBlockPosY() + 1 && !world.isAirOrPassableBlock(this.getBlockPosX(), this.getBlockPosY() + 2, this.getBlockPosZ(), false) && this.canDestroyBlock(x, y - 1, z, true)) {
               this.is_destroying_block = true;
               this.destroy_pause_ticks = 10;
               --this.destroy_block_y;
            }

            if (block_above instanceof BlockUnderminable) {
               ((BlockUnderminable)block_above).tryToFall(world, x, y + 1, z);
            }
         }

         world.watchAnimal(this.entityId, x, y, z, this.destroy_block_progress);
         if (block.blockMaterial == Material.glass) {
            world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), Block.glass.stepSound.getPlaceSound(), Block.glass.stepSound.getVolume() + 2.0F, Block.glass.stepSound.getPitch() * 1.0F);
         } else {
            world.playAuxSFX(2001, x, y, z, block.blockID + (world.getBlockMetadata(x, y, z) << 12));
         }

      }
   }

   protected double getCenterPosYForBlockDestroying() {
      return this.posY + (double)(this.height * 0.5F);
   }

   public Vec3 getEyePosForBlockDestroying() {
      return this.getPrimaryPointOfAttack();
   }

   public Vec3 getAttackerLegPosForBlockDestroying() {
      Vec3Pool vec3_pool = this.worldObj.getWorldVec3Pool();
      return vec3_pool.getVecFromPool(this.posX, this.posY + (double)(this.height * 0.25F), this.posZ);
   }

   public Vec3 getTargetEntityCenterPosForBlockDestroying(EntityLivingBase entity_living_base) {
      Vec3Pool vec3_pool = entity_living_base.worldObj.getWorldVec3Pool();
      return vec3_pool.getVecFromPool(entity_living_base.posX, entity_living_base.posY + (double)(entity_living_base.height / 2.0F), entity_living_base.posZ);
   }

   private boolean hasDownwardsDiggingTool() {
      ItemStack held_item = this.getHeldItemStack();
      return held_item != null && held_item.getItem() instanceof ItemShovel;
   }

   private boolean isBlockClaimedByAnother(int x, int y, int z) {
      AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(this.posX - 4.0, this.posY - 4.0, this.posZ - 4.0, this.posX + 4.0, this.posY + 4.0, this.posZ + 4.0);
      List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, bb);
      Iterator i = entities.iterator();

      while(i.hasNext()) {
         Entity entity = (Entity)i.next();
         if (entity instanceof EntityAnimalWatcher) {
            EntityAnimalWatcher digger = (EntityAnimalWatcher)entity;
            if (digger.is_destroying_block && digger.destroy_block_x == x && digger.destroy_block_y == y && digger.destroy_block_z == z) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean canDestroyBlock(int x, int y, int z, boolean check_clipping) {
      if (this.isHoldingItemThatPreventsDigging()) {
         return false;
      } else {
         int foot_y = this.getFootBlockPosY();
         if (y < foot_y && !this.hasDownwardsDiggingTool()) {
            return false;
         } else if (y > foot_y + 1) {
            return false;
         } else {
            World world = this.worldObj;
            if (World.getDistanceSqFromDeltas(this.posX - (double)((float)x + 0.5F), this.getCenterPosYForBlockDestroying() - (double)((float)y + 0.5F), this.posZ - (double)((float)z + 0.5F)) > 3.25) {
               return false;
            } else {
               if (check_clipping) {
                  RaycastCollision rc = world.getBlockCollisionForPhysicalReach(this.getEyePosForBlockDestroying(), world.getBlockCenterPos(x, y, z));
                  if (rc != null && (rc.isEntity() || rc.isBlock() && (rc.block_hit_x != x || rc.block_hit_y != y || rc.block_hit_z != z))) {
                     rc = world.getBlockCollisionForPhysicalReach(this.getAttackerLegPosForBlockDestroying(), world.getBlockCenterPos(x, y, z));
                     if (rc != null && (rc.isEntity() || rc.isBlock() && (rc.block_hit_x != x || rc.block_hit_y != y || rc.block_hit_z != z))) {
                        return false;
                     }
                  }
               }

               Block block = Block.blocksList[world.getBlockId(x, y, z)];
               if (block == null) {
                  return false;
               } else if (block.blockMaterial.isLiquid()) {
                  return false;
               } else {
                  int metadata = world.getBlockMetadata(x, y, z);
                  if (this instanceof EntityEarthElemental) {
                     EntityEarthElemental entity_earth_elemental = (EntityEarthElemental)this;
                     if (block.getMinHarvestLevel(metadata) <= entity_earth_elemental.getBlockHarvestLevel()) {
                        return true;
                     }
                  }

                  Item held_item = this.getHeldItemStack() == null ? null : this.getHeldItemStack().getItem();
                  boolean has_effective_tool = held_item instanceof ItemTool && ((ItemTool)held_item).getStrVsBlock(block, metadata) > 0.0F;
                  if (block.blockMaterial.requiresTool(block, metadata) && (!this.isFrenzied() || block.getMinHarvestLevel(metadata) >= 2) && !has_effective_tool && block != Block.sand && block != Block.dirt && block != Block.grass && block != Block.gravel && block != Block.blockSnow && block != Block.tilledField && block != Block.blockClay && block != Block.leaves && block != Block.cloth && (block != Block.cactus || !this.isEntityInvulnerable() && !this.isEntityUndead()) && block != Block.sponge && !(block instanceof BlockPumpkin) && !(block instanceof BlockMelon) && block != Block.mycelium && block != Block.hay && block != Block.thinGlass) {
                     return false;
                  } else {
                     return !this.isBlockClaimedByAnother(x, y, z);
                  }
               }
            }
         }
      }
   }

   public boolean setBlockToDig(int x, int y, int z, boolean check_clipping) {
      if (!this.canDestroyBlock(x, y, z, check_clipping)) {
         return false;
      } else {
         this.is_destroying_block = true;
         if (x == this.destroy_block_x && y == this.destroy_block_y && z == this.destroy_block_z) {
            return true;
         } else {
            if (y == this.getFootBlockPosY() + 1 && this.worldObj.getBlock(x, y, z) == Block.cactus && this.canDestroyBlock(x, y - 1, z, check_clipping)) {
               --y;
            }

            this.destroy_block_progress = -1;
            this.destroy_block_x = x;
            this.destroy_block_y = y;
            this.destroy_block_z = z;
            return true;
         }
      }
   }

   public void cancelBlockDestruction() {
      if (this.is_destroying_block) {
         this.worldObj.watchAnimal(this.entityId, this.destroy_block_x, this.destroy_block_y, this.destroy_block_z, -1);
         this.is_destroying_block = false;
         this.destroy_block_progress = -1;
         this.destroy_block_cooloff = 40;
      }
   }

   public int getCooloffForBlock() {
      Block block = Block.blocksList[this.worldObj.getBlockId(this.destroy_block_x, this.destroy_block_y, this.destroy_block_z)];
      if (block == null) {
         return 40;
      } else {
         int cooloff = (int)(300.0F * this.worldObj.getBlockHardness(this.destroy_block_x, this.destroy_block_y, this.destroy_block_z));
         if (this.isFrenzied()) {
            cooloff /= 2;
         }

         if (this instanceof EntityEarthElemental) {
            EntityEarthElemental elemental = (EntityEarthElemental)this;
            if (elemental.isNormalClay()) {
               cooloff /= 4;
            } else if (elemental.isHardenedClay()) {
               cooloff /= 6;
            } else {
               cooloff /= 8;
            }
         }

         if (this.getHeldItemStack() == null) {
            return cooloff;
         } else {
            Item held_item = this.getHeldItemStack().getItem();
            if (held_item instanceof ItemTool) {
               ItemTool item_tool = (ItemTool)held_item;
               cooloff = (int)((float)cooloff / (1.0F + item_tool.getStrVsBlock(block, this.worldObj.getBlockMetadata(this.destroy_block_x, this.destroy_block_y, this.destroy_block_z)) * 0.5F));
            }

            return cooloff;
         }
      }
   }

   public void onUpdate() {
      if (this.is_destroying_block) {
         if (this.destroy_pause_ticks == 0) {
            this.getLookHelper().setLookPosition((double)((float)this.destroy_block_x + 0.5F), (double)((float)this.destroy_block_y + 0.5F), (double)((float)this.destroy_block_z + 0.5F), 10.0F, (float)this.getVerticalFaceSpeed());
            if (!this.canDestroyBlock(this.destroy_block_x, this.destroy_block_y, this.destroy_block_z, true)) {
               this.cancelBlockDestruction();
            }
         }
      } else {
         this.destroy_block_cooloff = 40;
         this.destroy_block_progress = -1;
      }

      super.onUpdate();
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      if (this.is_destroying_block) {
         par1NBTTagCompound.setBoolean("is_destroying_block", this.is_destroying_block);
         par1NBTTagCompound.setInteger("destroy_block_x", this.destroy_block_x);
         par1NBTTagCompound.setInteger("destroy_block_y", this.destroy_block_y);
         par1NBTTagCompound.setInteger("destroy_block_z", this.destroy_block_z);
         par1NBTTagCompound.setInteger("destroy_block_progress", this.destroy_block_progress);
         par1NBTTagCompound.setInteger("destroy_block_cooloff", this.destroy_block_cooloff);
      }

   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      if (par1NBTTagCompound.hasKey("is_destroying_block")) {
         this.is_destroying_block = par1NBTTagCompound.getBoolean("is_destroying_block");
         this.destroy_block_x = par1NBTTagCompound.getInteger("destroy_block_x");
         this.destroy_block_y = par1NBTTagCompound.getInteger("destroy_block_y");
         this.destroy_block_z = par1NBTTagCompound.getInteger("destroy_block_z");
         this.destroy_block_progress = par1NBTTagCompound.getInteger("destroy_block_progress");
         this.destroy_block_cooloff = par1NBTTagCompound.getInteger("destroy_block_cooloff");
      }

   }

   public void onDeath(DamageSource par1DamageSource) {
      this.cancelBlockDestruction();
      super.onDeath(par1DamageSource);
   }
}
