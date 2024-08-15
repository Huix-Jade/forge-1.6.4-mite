package net.minecraft.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockInfo;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemVessel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityGelatinousCube extends EntityCubic {
   private int block_feeding_countdown;
   private int item_feeding_countdown;
   private int data_object_id_is_feeding;
   private static final int OFFSET_BLOCK_ID = 0;
   private static final int OFFSET_BLOCK_DIMENSION = 1;
   private static final int OFFSET_BLOCK_X = 2;
   private static final int OFFSET_BLOCK_Y = 3;
   private static final int OFFSET_BLOCK_Z = 4;
   private static final int OFFSET_BLOCK_PROGRESS = 5;
   private static final int DISSOLVING_BLOCK_FIELDS = 6;
   private static final int MAX_NUM_DISSOLVING_BLOCKS = 64;
   private int[] dissolving_blocks_info = new int[384];
   private List extended_dissolving_blocks_info = new ArrayList();
   private int ticks_until_next_fizz_sound;

   public EntityGelatinousCube(World world) {
      super(world);
   }

   protected void entityInit() {
      super.entityInit();
      this.data_object_id_is_feeding = this.dataWatcher.addObject(this.dataWatcher.getNextAvailableId(), new Byte((byte)0));
   }

   protected boolean setIsFeeding(boolean is_feeding) {
      this.dataWatcher.updateObject(this.data_object_id_is_feeding, (byte)(is_feeding ? -1 : 0));
      return is_feeding;
   }

   public boolean isFeeding() {
      return this.dataWatcher.getWatchableObjectByte(this.data_object_id_is_feeding) != 0;
   }

   private void updateIsFeedingFlag() {
      this.setIsFeeding(this.block_feeding_countdown > 0 || this.item_feeding_countdown > 0);
   }

   public boolean isBlockFeedingCountdownAboveZero() {
      return this.block_feeding_countdown > 0;
   }

   public void setBlockFeedingCountdown(int block_feeding_countdown) {
      this.block_feeding_countdown = MathHelper.clamp_int(block_feeding_countdown, 0, 20);
      this.updateIsFeedingFlag();
   }

   public void setItemFeedingCountdown(int item_feeding_countdown) {
      this.item_feeding_countdown = MathHelper.clamp_int(item_feeding_countdown, 0, 20);
      this.updateIsFeedingFlag();
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setIntArray("dissolving_blocks_info", this.dissolving_blocks_info);
      if (!this.extended_dissolving_blocks_info.isEmpty()) {
         int[] spliced = new int[this.extended_dissolving_blocks_info.size() * 6];
         int index = -1;
         Iterator i = this.extended_dissolving_blocks_info.iterator();

         while(i.hasNext()) {
            int[] info = (int[])((int[])i.next());

            for(int offset = 0; offset < 6; ++offset) {
               ++index;
               spliced[index] = info[offset];
            }
         }

         par1NBTTagCompound.setIntArray("extended_dissolving_blocks_info", spliced);
      }

   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      int[] spliced;
      int i;
      if (par1NBTTagCompound.hasKey("dissolving_blocks_info")) {
         this.dissolving_blocks_info = par1NBTTagCompound.getIntArray("dissolving_blocks_info");
         if (this.dissolving_blocks_info.length < 384) {
            spliced = this.dissolving_blocks_info;
            this.dissolving_blocks_info = new int[384];

            for(i = 0; i < spliced.length; ++i) {
               this.dissolving_blocks_info[i] = spliced[i];
            }
         }
      }

      if (par1NBTTagCompound.hasKey("extended_dissolving_blocks_info")) {
         spliced = par1NBTTagCompound.getIntArray("extended_dissolving_blocks_info");
         i = spliced.length / 6;

         for(int k = 0; k < k; ++k) {
            int base_offset = k * 6;
            int[] info = new int[]{spliced[base_offset + 0], spliced[base_offset + 1], spliced[base_offset + 2], spliced[base_offset + 3], spliced[base_offset + 4], spliced[base_offset + 5]};
            this.extended_dissolving_blocks_info.add(info);
         }
      }

   }

   private BlockInfo[] getBlocksOccupiedWithStandardExpansion() {
      return this.getBlocksOccupied(0.01F, 0.01F, 0.01F, 0.01F, true);
   }

   public int getJumpDelay(Entity target) {
      return target == null ? this.rand.nextInt(81) + 40 : 10;
   }

   protected void updateEntityActionState() {
      BlockInfo[] infos = this.getBlocksOccupiedWithStandardExpansion();

      for(int i = 0; i < infos.length; ++i) {
         BlockInfo info = infos[i];
         if (info != null) {
            Block block = info.block;
            int x = info.x;
            int y = info.y;
            int z = info.z;
            int dissolve_period = this.getDissolvePeriod(info.block, x, y, z);
            if (dissolve_period != 0 && !(block instanceof BlockTorch) && !(block instanceof BlockCrops)) {
               if (dissolve_period > 0) {
                  this.setBlockFeedingCountdown(20);
                  break;
               }
            } else if (this.worldObj.isBlockSolid(x, y, z)) {
               this.worldObj.destroyBlockWithoutDroppingItem(x, y, z, this.isAcidic() ? EnumBlockFX.smoke_and_steam : EnumBlockFX.steam);
            } else {
               this.onCollidedWithBlock(this.worldObj, block, x, y, z);
            }
         }
      }

      super.updateEntityActionState();
   }

   public void onCollidedWithBlock(World world, Block block, int x, int y, int z) {
      if (this.onServer() && block.doRenderBoundsIntersectWith(world, x, y, z, this.boundingBox)) {
         if (block instanceof BlockTorch) {
            world.destroyBlockWithoutDroppingItem(x, y, z, EnumBlockFX.smoke_and_steam);
            this.attackEntityFrom(new Damage(DamageSource.inFire, 1.0F));
            return;
         }

         if (this.dissolvesBlockInstantly(x, y, z)) {
            world.destroyBlockWithoutDroppingItem(x, y, z, this.isAcidic() ? EnumBlockFX.smoke_and_steam : EnumBlockFX.steam);
            return;
         }

         if (block instanceof BlockCrops) {
            BlockCrops crops = (BlockCrops)block;
            crops.setBlighted(world, x, y, z, true);
         }
      }

      super.onCollidedWithBlock(world, block, x, y, z);
   }

   private void setDissolvingBlock(Block block, int x, int y, int z, int dissolving_progress) {
      for(int i = 0; i < 64; ++i) {
         int base_offset = i * 6;
         if (this.dissolving_blocks_info[base_offset + 0] == 0) {
            this.dissolving_blocks_info[base_offset + 0] = block.blockID;
            this.dissolving_blocks_info[base_offset + 1] = this.worldObj.getDimensionId();
            this.dissolving_blocks_info[base_offset + 2] = x;
            this.dissolving_blocks_info[base_offset + 3] = y;
            this.dissolving_blocks_info[base_offset + 4] = z;
            this.dissolving_blocks_info[base_offset + 5] = dissolving_progress;
            return;
         }
      }

      Debug.setErrorMessage("setDissolvingBlock: wasn't able to add another block");
   }

   private int isDissolvingBlock(Block block, int x, int y, int z) {
      for(int i = 0; i < 64; ++i) {
         int base_offset = i * 6;
         if (this.dissolving_blocks_info[base_offset + 0] == block.blockID && this.dissolving_blocks_info[base_offset + 1] == this.worldObj.getDimensionId() && this.dissolving_blocks_info[base_offset + 2] == x && this.dissolving_blocks_info[base_offset + 3] == y && this.dissolving_blocks_info[base_offset + 4] == z) {
            return i;
         }
      }

      return -1;
   }

   public int isDissolvingBlock(BlockInfo info) {
      return this.isDissolvingBlock(info.block, info.x, info.y, info.z);
   }

   public int getDissolvePeriod(Block block, int x, int y, int z) {
      int ticks = block.getDissolvePeriod(this.worldObj, x, y, z, this.getDamageTypeVsItems());
      return ticks >= 0 ? ticks / 20 : -1;
   }

   public boolean canDissolveBlockGradually(int x, int y, int z) {
      Block block = this.worldObj.getBlock(x, y, z);
      return block != null && this.getDissolvePeriod(block, x, y, z) > 0;
   }

   public boolean canDissolveBlockGradually(BlockInfo info) {
      return this.canDissolveBlockGradually(info.x, info.y, info.z);
   }

   public boolean dissolvesBlockInstantly(int x, int y, int z) {
      Block block = this.worldObj.getBlock(x, y, z);
      return block != null && this.getDissolvePeriod(block, x, y, z) == 0;
   }

   private boolean isSameBlock(int index, BlockInfo info) {
      if (info == null) {
         return false;
      } else {
         int base_offset = index * 6;
         if (this.dissolving_blocks_info[base_offset + 0] != info.block.blockID) {
            return false;
         } else if (this.dissolving_blocks_info[base_offset + 1] != this.worldObj.getDimensionId()) {
            return false;
         } else if (this.dissolving_blocks_info[base_offset + 2] != info.x) {
            return false;
         } else if (this.dissolving_blocks_info[base_offset + 3] != info.y) {
            return false;
         } else {
            return this.dissolving_blocks_info[base_offset + 4] == info.z;
         }
      }
   }

   private boolean isBlockFoundInBlockInfos(int index, BlockInfo[] infos) {
      for(int i = 0; i < infos.length; ++i) {
         if (this.isSameBlock(index, infos[i])) {
            return true;
         }
      }

      return false;
   }

   private void storeExtendedDissolvingBlockInfo(int base_offset) {
      Iterator i = this.extended_dissolving_blocks_info.iterator();

      int[] info;
      boolean matches;
      do {
         if (!i.hasNext()) {
            info = new int[6];

            for(int offset = 0; offset < 6; ++offset) {
               info[offset] = this.dissolving_blocks_info[base_offset + offset];
            }

            this.extended_dissolving_blocks_info.add(info);
            return;
         }

         info = (int[])((int[])i.next());
         matches = true;

         for(int offset = 0; offset < 5; ++offset) {
            if (info[offset] != this.dissolving_blocks_info[base_offset + offset]) {
               matches = false;
               break;
            }
         }
      } while(!matches);

      info[5] = this.dissolving_blocks_info[base_offset + 5];
   }

   private int getProgressFromExtendedDissolvingBlockInfo(Block block, int block_dimension, int x, int y, int z) {
      Iterator i = this.extended_dissolving_blocks_info.iterator();

      int[] info;
      do {
         if (!i.hasNext()) {
            return 0;
         }

         info = (int[])((int[])i.next());
      } while(info[0] != block.blockID || info[1] != block_dimension || info[2] != x || info[3] != y || info[4] != z);

      return info[5];
   }

   private void pruneExtendedDissolvingBlockInfo() {
      Iterator i = this.extended_dissolving_blocks_info.iterator();

      while(true) {
         int[] info;
         int x;
         int y;
         int z;
         do {
            if (!i.hasNext()) {
               return;
            }

            info = (int[])((int[])i.next());
            x = info[2];
            y = info[3];
            z = info[4];
         } while(this.worldObj.getDimensionId() == info[1] && this.getDistanceSqToBlock(x, y, z) < 256.0 && this.worldObj.getBlock(x, y, z) == Block.getBlock(info[0]));

         i.remove();
      }
   }

   public void spentTickInLava() {
      super.spentTickInLava();
      if (!this.isDead) {
         if (this.onClient()) {
            this.spawnSteamParticles(5);
            this.spawnLargeSmokeParticles(5);
         } else if (--this.ticks_until_next_fizz_sound <= 0) {
            this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            this.ticks_until_next_fizz_sound = this.rand.nextInt(7) + 2;
         }
      }

   }

   public void onLivingUpdate() {
      if (this.onServer()) {
         if (this.block_feeding_countdown > 0) {
            --this.block_feeding_countdown;
            this.updateIsFeedingFlag();
         }

         if (this.item_feeding_countdown > 0) {
            --this.item_feeding_countdown;
            this.updateIsFeedingFlag();
         }

         if (this.getTicksExistedWithOffset() % 20 == 0) {
            this.pruneExtendedDissolvingBlockInfo();
            BlockInfo[] infos = this.getBlocksOccupiedWithStandardExpansion();

            int i;
            for(int k = 0; k < 64; ++k) {
               k = k * 6;
               if (this.dissolving_blocks_info[k + 0] != 0 && !this.isBlockFoundInBlockInfos(k, infos)) {
                  this.storeExtendedDissolvingBlockInfo(k);
                  this.dissolving_blocks_info[k + 0] = 0;
               }
            }

            boolean clear_block_feeding_counter = true;

            for(i = 0; i < infos.length; ++i) {
               BlockInfo info = infos[i];
               if (info != null) {
                  Block block = info.block;
                  int x = info.x;
                  int y = info.y;
                  int z = info.z;
                  int dissolve_period;
                  if (this.worldObj.getBlock(x, y, z) != block) {
                     dissolve_period = this.isDissolvingBlock(info);
                     if (dissolve_period >= 0) {
                        this.dissolving_blocks_info[dissolve_period * 6 + 0] = 0;
                     }
                  } else {
                     dissolve_period = this.getDissolvePeriod(block, x, y, z);
                     if (dissolve_period >= 1) {
                        int index = this.isDissolvingBlock(info);
                        int base_offset;
                        if (index < 0) {
                           base_offset = this.getProgressFromExtendedDissolvingBlockInfo(block, this.worldObj.getDimensionId(), x, y, z);
                           this.setDissolvingBlock(block, x, y, z, base_offset);
                           index = this.isDissolvingBlock(info);
                        }

                        base_offset = index * 6;
                        int[] var10000 = this.dissolving_blocks_info;
                        var10000[base_offset + 5] += this.getSize();
                        if (this.dissolving_blocks_info[base_offset + 5] >= dissolve_period) {
                           this.worldObj.destroyBlockWithoutDroppingItem(x, y, z, this.isAcidic() ? EnumBlockFX.smoke_and_steam : EnumBlockFX.steam);
                           this.dissolving_blocks_info[base_offset + 0] = 0;
                        } else {
                           this.worldObj.blockFX(EnumBlockFX.steam_particles_only, x, y, z);
                           clear_block_feeding_counter = false;
                        }
                     }
                  }
               }
            }

            this.setBlockFeedingCountdown(clear_block_feeding_counter ? 0 : 20);
            List entity_items = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox);
            boolean refresh_counter = false;
            if (entity_items != null) {
               for(int k = 0; k < entity_items.size(); ++k) {
                  EntityItem entity_item = (EntityItem)entity_items.get(k);
                  if (!entity_item.isDead) {
                     if (entity_item.delayBeforeCanPickup < 60) {
                        entity_item.delayBeforeCanPickup = 60;
                     }

                     if (entity_item.isVessel()) {
                        ItemVessel item_vessel = (ItemVessel)entity_item.getEntityItem().getItem();
                        if (!item_vessel.isEmpty()) {
                           Material contents = item_vessel.getContents();
                           if (contents == Material.lava) {
                              this.attackEntityFrom(new Damage(DamageSource.lava, 5.0F));
                              this.worldObj.blockFX(EnumBlockFX.smoke_and_steam, entity_item.getBlockPosX(), entity_item.getBlockPosY(), entity_item.getBlockPosZ());
                              entity_item.convertItem(item_vessel.getPeerForContents(Material.stone));
                           } else if (contents == Material.water) {
                              entity_item.convertItem(item_vessel.getEmptyVessel());
                           }
                        }
                     }

                     EntityDamageResult result = entity_item.attackEntityFrom(new Damage(this.getDamageTypeVsItems(), 1.0F));
                     if (result != null && result.entityWasNegativelyAffectedButNotDestroyed()) {
                        refresh_counter = true;
                     }
                  }
               }
            }

            this.setItemFeedingCountdown(refresh_counter ? 20 : 0);
         }
      }

      super.onLivingUpdate();
   }

   public abstract DamageSource getDamageTypeVsItems();

   public void onMeleeAttacked(EntityLivingBase attacker, EntityDamageResult result) {
      super.onMeleeAttacked(attacker, result);
      if (attacker.isEntityPlayer() && attacker.hasHeldItem()) {
         EntityPlayer player = attacker.getAsPlayer();
         if (player.inventory.takeDamage(player.getHeldItemStack(), this.getDamageTypeVsItems(), (float)this.getAttackStrengthMultiplierForType())) {
            player.entityFX(EnumEntityFX.steam_with_hiss);
         }
      }

   }

   public void tryAddArrowToContainedItems(EntityArrow entity_arrow) {
      if (this.isAcidic()) {
         ItemArrow item_arrow = entity_arrow.item_arrow;
         if (item_arrow.isHarmedByAcid()) {
            entity_arrow.entityFX(EnumEntityFX.steam_with_hiss);
            return;
         }
      }

      super.tryAddArrowToContainedItems(entity_arrow);
   }

   protected final int getDropItemId() {
      return this.getSize() == 1 ? Item.slimeBall.itemID : 0;
   }

   public float getClimbingSpeed() {
      return 0.2F;
   }

   public int getExperienceValue() {
      return this.getSize() * (this.getAttackStrengthMultiplierForType() + (this.isAcidic() ? 1 : 0));
   }

   public boolean canDouseFire() {
      return true;
   }

   public boolean canCatchFire() {
      return false;
   }

   public final boolean attacksAnimals() {
      return true;
   }

   public final boolean attacksVillagers() {
      return true;
   }
}
