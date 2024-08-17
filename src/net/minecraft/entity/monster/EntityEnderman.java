package net.minecraft.entity.monster;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockUnderminable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.SignalData;
import net.minecraft.util.Curse;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntityEnderman extends EntityMob {
   private static final UUID attackingSpeedBoostModifierUUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier attackingSpeedBoostModifier;
   public static boolean[] carriableBlocks;
   private int teleportDelay;
   private int stareTimer;
   private Entity lastEntityToAttack;
   private boolean isAggressive;

   public EntityEnderman(World par1World) {
      super(par1World);
      this.setSize(0.6F, 2.9F);
      this.stepHeight = 1.0F;
      this.getNavigator().setAvoidsWater(true);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(40.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.30000001192092896);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(10.0);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)0));
      this.dataWatcher.addObject(17, new Byte((byte)0));
      this.dataWatcher.addObject(18, new Byte((byte)0));
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setShort("carried", (short)this.getCarried());
      par1NBTTagCompound.setShort("carriedData", (short)this.getCarryingData());
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.setCarried(par1NBTTagCompound.getShort("carried"));
      this.setCarryingData(par1NBTTagCompound.getShort("carriedData"));
   }

   protected EntityPlayer findPlayerToAttack(float max_distance) {
      EntityPlayer var1 = super.findPlayerToAttack(64.0F);
      if (var1 != null) {
         if (this.shouldAttackPlayer(var1)) {
            this.isAggressive = true;
            if (this.stareTimer == 0) {
               this.worldObj.playSoundAtEntity(var1, "mob.endermen.stare", 1.0F, 1.0F);
            }

            if (this.stareTimer++ == 5) {
               this.stareTimer = 0;
               this.setScreaming(true);
               return var1;
            }
         } else {
            this.stareTimer = 0;
         }
      }

      return null;
   }

   private boolean shouldAttackPlayer(EntityPlayer par1EntityPlayer) {
      if (this.isDecoy()) {
         return false;
      } else if (this.rand.nextInt(3) == 0 && par1EntityPlayer.hasCurse(Curse.endermen_aggro, true)) {
         return true;
      } else if (isEnderPearlOrEye(par1EntityPlayer.getHeldItemStack())) {
         this.worldObj.playSoundAtEntity(par1EntityPlayer, "mob.endermen.stare", 1.0F, 1.0F);
         this.stareTimer = 5;
         return true;
      } else {
         int num_ender_items = par1EntityPlayer.getNumItems(Item.enderPearl) + par1EntityPlayer.getNumItems(Item.eyeOfEnder);
         if (num_ender_items > 0 && this.rand.nextInt(2000) < num_ender_items) {
            this.worldObj.playSoundAtEntity(par1EntityPlayer, "mob.endermen.stare", 1.0F, 1.0F);
            this.stareTimer = 5;
            return true;
         } else {
            ItemStack var2 = par1EntityPlayer.inventory.armorInventory[3];
            if (var2 != null && var2.itemID == Block.pumpkin.blockID) {
               return false;
            } else {
               Vec3 var3 = par1EntityPlayer.getLook(1.0F).normalize();
               Vec3 var4 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX - par1EntityPlayer.posX, this.boundingBox.minY + (double)(this.height / 2.0F) - (par1EntityPlayer.posY + (double)par1EntityPlayer.getEyeHeight()), this.posZ - par1EntityPlayer.posZ);
               double var5 = var4.lengthVector();
               var4 = var4.normalize();
               double var7 = var3.dotProduct(var4);
               return var7 > 1.0 - 0.025 / var5 ? par1EntityPlayer.canSeeEntity(this) : false;
            }
         }
      }
   }

   public EntityItem findTargetEntityItem(float max_distance) {
      Iterator i = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand((double)max_distance, (double)(max_distance * 0.25F), (double)max_distance)).iterator();

      EntityItem entity_item;
      do {
         do {
            do {
               if (!i.hasNext()) {
                  return null;
               }

               entity_item = (EntityItem)i.next();
            } while(entity_item.isWet());
         } while(entity_item.isBurning() && this.isHarmedByFire());
      } while(!this.willPickupAsValuable(entity_item.getEntityItem()));

      return entity_item;
   }

   public static boolean isEnderPearlOrEye(ItemStack item_stack) {
      if (item_stack == null) {
         return false;
      } else {
         return item_stack.getItem() == Item.enderPearl || item_stack.getItem() == Item.eyeOfEnder;
      }
   }

   public boolean willPickupAsValuable(ItemStack item_stack) {
      return item_stack.getItemSubtype() == 0 && item_stack.getItemDamage() == 0 && isEnderPearlOrEye(item_stack);
   }

   public void onLivingUpdate() {
      if (this.onServer() && this.isWet()) {
         this.attackEntityFrom(new Damage(DamageSource.water, 1.0F));
      }

      if (this.lastEntityToAttack != this.entityToAttack) {
         AttributeInstance var1 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
         var1.removeModifier(attackingSpeedBoostModifier);
         if (this.entityToAttack != null) {
            var1.applyModifier(attackingSpeedBoostModifier);
         }
      }

      this.lastEntityToAttack = this.entityToAttack;
      int var6;
      if (!this.worldObj.isRemote && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
         int var2;
         int var3;
         int var4;
         if (this.getCarried() == 0) {
            if (this.rand.nextInt(20) == 0) {
               var6 = MathHelper.floor_double(this.posX - 2.0 + this.rand.nextDouble() * 4.0);
               var2 = MathHelper.floor_double(this.posY + this.rand.nextDouble() * 3.0);
               var3 = MathHelper.floor_double(this.posZ - 2.0 + this.rand.nextDouble() * 4.0);
               var4 = this.worldObj.getBlockId(var6, var2, var3);
               if (carriableBlocks[var4]) {
                  this.setCarried(this.worldObj.getBlockId(var6, var2, var3));
                  this.setCarryingData(this.worldObj.getBlockMetadata(var6, var2, var3));
                  this.worldObj.setBlock(var6, var2, var3, 0);
                  Block block_above = this.worldObj.getBlock(var6, var2 + 1, var3);
                  if (block_above instanceof BlockUnderminable) {
                     ((BlockUnderminable)block_above).tryToFall(this.worldObj, var6, var2 + 1, var3);
                  }
               }
            }
         } else if (this.rand.nextInt(2000) == 0) {
            var6 = MathHelper.floor_double(this.posX - 1.0 + this.rand.nextDouble() * 2.0);
            var2 = MathHelper.floor_double(this.posY + this.rand.nextDouble() * 2.0);
            var3 = MathHelper.floor_double(this.posZ - 1.0 + this.rand.nextDouble() * 2.0);
            var4 = this.worldObj.getBlockId(var6, var2, var3);
            int var5 = this.worldObj.getBlockId(var6, var2 - 1, var3);
            if (var4 == 0 && var5 > 0 && Block.blocksList[var5].renderAsNormalBlock()) {
               this.worldObj.setBlock(var6, var2, var3, this.getCarried(), this.getCarryingData(), 3);
               this.setCarried(0);
            }
         }
      }

      for(var6 = 0; var6 < 2; ++var6) {
         this.worldObj.spawnParticle(EnumParticle.portal_underworld, this.posX + (this.rand.nextDouble() - 0.5) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - 0.25, this.posZ + (this.rand.nextDouble() - 0.5) * (double)this.width, (this.rand.nextDouble() - 0.5) * 2.0, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5) * 2.0);
      }

      boolean has_teleported = false;
      if (this.entityToAttack == null && this.worldObj.isDaytime() && !this.worldObj.isRemote && this.rand.nextInt(4) == 0) {
         float var7 = this.getBrightness(1.0F);
         if (var7 > 0.5F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) && this.rand.nextFloat() * 30.0F < (var7 - 0.4F) * 2.0F) {
            this.entityToAttack = null;
            this.setScreaming(false);
            this.isAggressive = false;
            if (this.teleportRandomly()) {
               has_teleported = true;
            }
         }
      }

      if (this.onServer() && !has_teleported && this.getTicksExistedWithOffset() % 20 == 0 && this.rand.nextInt(10) == 0 && this.tryTeleportToValuableItem()) {
         has_teleported = true;
      }

      if (this.onServer() && (this.isWet() || this.isBurning())) {
         this.entityToAttack = null;
         this.setScreaming(false);
         this.isAggressive = false;
         this.teleportRandomly();
      }

      if (this.isScreaming() && !this.isAggressive && this.rand.nextInt(100) == 0) {
         this.setScreaming(false);
      }

      this.isJumping = false;
      if (this.entityToAttack != null) {
         this.faceEntity(this.entityToAttack, 100.0F, 100.0F);
      }

      if (!this.worldObj.isRemote && this.isEntityAlive()) {
         if (this.entityToAttack != null) {
            if (++this.teleportDelay > 30) {
               if (this.rand.nextInt(2) == 0) {
                  if (this.rand.nextInt(3) == 0) {
                     this.teleportRandomly();
                  } else {
                     this.teleportToEntity(this.entityToAttack);
                  }
               }

               this.teleportDelay = 0;
            }
         } else {
            this.setScreaming(false);
            this.teleportDelay = 0;
         }
      }

      super.onLivingUpdate();
   }

   private EntityItem getNearestObtainableValuableItem() {
      EntityItem nearest_obtainable_valuable_item = null;
      double distance_sq_to_nearest_obtainable_valuable_item = 0.0;
      List items = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(16.0, 8.0, 16.0));
      Iterator i = items.iterator();

      while(true) {
         EntityItem entity_item;
         double distance_sq;
         do {
            int x;
            int y;
            int z;
            do {
               do {
                  ItemStack item_stack;
                  do {
                     do {
                        do {
                           do {
                              if (!i.hasNext()) {
                                 return nearest_obtainable_valuable_item;
                              }

                              entity_item = (EntityItem)i.next();
                           } while(entity_item.isDead);
                        } while(entity_item.isWet());
                     } while(entity_item.isBurning() && this.isHarmedByFire());

                     item_stack = entity_item.getEntityItem();
                  } while(!this.willPickupAsValuable(item_stack));

                  x = entity_item.getBlockPosX();
                  y = entity_item.getBlockPosY();
                  z = entity_item.getBlockPosZ();
               } while(!this.worldObj.isAirOrPassableBlock(x, y + 1, z, false));
            } while(!this.worldObj.isAirOrPassableBlock(x, y + 2, z, false));

            distance_sq = this.getDistanceSqToEntity(entity_item);
         } while(nearest_obtainable_valuable_item != null && !(distance_sq < distance_sq_to_nearest_obtainable_valuable_item));

         nearest_obtainable_valuable_item = entity_item;
         distance_sq_to_nearest_obtainable_valuable_item = distance_sq;
      }
   }

   private boolean tryTeleportToValuableItem() {
      if (this.onClient()) {
         Minecraft.setErrorMessage("tryTeleportToValuableItem: called on client");
      }

      if (!this.isWet() && !this.isBurning()) {
         EntityItem entity_item = this.getNearestObtainableValuableItem();
         if (entity_item == null) {
            return false;
         } else {
            int x = entity_item.getBlockPosX();
            int y = entity_item.getBlockPosY();
            int z = entity_item.getBlockPosZ();
            return this.teleportTo((double)x + 0.5, this.worldObj.getBlockCollisionTopY(x, y, z, this), (double)z + 0.5);
         }
      } else {
         return false;
      }
   }

   protected boolean teleportRandomly() {
      if (this.onClient()) {
         Minecraft.setErrorMessage("teleportRandomly: called on client");
      }

      if (this.isDecoy()) {
         return false;
      } else if (this.tryTeleportToValuableItem()) {
         return true;
      } else {
         double var1 = this.posX + (this.rand.nextDouble() - 0.5) * 64.0;
         double var3 = this.posY + (double)(this.rand.nextInt(64) - 32);
         double var5 = this.posZ + (this.rand.nextDouble() - 0.5) * 64.0;
         return this.teleportTo(var1, var3, var5);
      }
   }

   protected boolean teleportToEntity(Entity par1Entity) {
      int x = par1Entity.getBlockPosX() + this.rand.nextInt(7) - 3;
      int y = par1Entity.getBlockPosY() + 3;
      int z = par1Entity.getBlockPosZ() + this.rand.nextInt(7) - 3;

      for(int dy = 0; dy >= -6 && this.worldObj.isAirOrPassableBlock(x, y - 1, z, false); --dy) {
         --y;
      }

      return this.teleportTo((double)((float)x + 0.5F), (double)((float)y + 0.1F), (double)((float)z + 0.5F));
   }

   protected boolean teleportTo(double par1, double par3, double par5) {
      EnderTeleportEvent event = new EnderTeleportEvent(this, par1, par3, par5, 0);
      if (MinecraftForge.EVENT_BUS.post(event)){
         return false;
      }

      double var7 = this.posX;
      double var9 = this.posY;
      double var11 = this.posZ;
      this.posX = event.targetX;
      this.posY = event.targetY;
      this.posZ = event.targetZ;
      boolean var13 = false;
      int var14 = MathHelper.floor_double(this.posX);
      int var15 = MathHelper.floor_double(this.posY);
      int var16 = MathHelper.floor_double(this.posZ);
      if (this.worldObj.blockExists(var14, var15, var16)) {
         boolean var17 = false;

         while(!var17 && var15 > 0) {
            if (this.worldObj.isBlockSolid(var14, var15 - 1, var16)) {
               var17 = true;
            } else {
               --this.posY;
               --var15;
            }
         }

         if (var17) {
            this.setPosition(this.posX, this.posY, this.posZ);
            if (this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox)) {
               var13 = true;
            }
         }
      }

      if (!var13) {
         this.setPosition(var7, var9, var11);
         return false;
      } else {
         int x = MathHelper.floor_double(this.posX);
         int y = MathHelper.floor_double(this.posY);
         int z = MathHelper.floor_double(this.posZ);
         World var10000 = this.worldObj;
         double distance = (double)World.getDistanceFromDeltas(this.posX - var7, this.posY - var9, this.posZ - var11);
         this.worldObj.blockFX(EnumBlockFX.particle_trail, x, y, z, (new SignalData()).setByte(EnumParticle.portal_underworld.ordinal()).setShort((int)(8.0 * distance)).setApproxPosition((double)MathHelper.floor_double(var7), (double)MathHelper.floor_double(var9), (double)MathHelper.floor_double(var11)));
         this.worldObj.blockFX(EnumBlockFX.particle_trail, x, y + 1, z, (new SignalData()).setByte(EnumParticle.portal_underworld.ordinal()).setShort((int)(8.0 * distance)).setApproxPosition((double)MathHelper.floor_double(var7), (double)MathHelper.floor_double(var9 + 1.0), (double)MathHelper.floor_double(var11)));
         this.worldObj.playSoundEffect(var7, var9, var11, "mob.endermen.portal", 1.0F, 1.0F);
         return true;
      }
   }

   protected String getLivingSound() {
      return this.isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
   }

   protected String getHurtSound() {
      return "mob.endermen.hit";
   }

   protected String getDeathSound() {
      return "mob.endermen.death";
   }

   protected int getDropItemId() {
      return Item.enderPearl.itemID;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      int item_id = this.getDropItemId();
      if (item_id > 0) {
         int num_drops = this.rand.nextInt(2 + damage_source.getLootingModifier());

         for(int i = 0; i < num_drops; ++i) {
            this.dropItem(item_id, 1);
         }
      }

   }

   public void setCarried(int par1) {
      this.dataWatcher.updateObject(16, (byte)(par1 & 255));
   }

   public int getCarried() {
      return this.dataWatcher.getWatchableObjectByte(16);
   }

   public void setCarryingData(int par1) {
      this.dataWatcher.updateObject(17, (byte)(par1 & 255));
   }

   public int getCarryingData() {
      return this.dataWatcher.getWatchableObjectByte(17);
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result != null && result.entityWasNegativelyAffected()) {
         this.setScreaming(true);
         if (damage.getResponsibleEntity() instanceof EntityPlayer) {
            this.isAggressive = true;
         }

         if (damage.isIndirect()) {
            this.isAggressive = false;

            for(int var3 = 0; var3 < 64; ++var3) {
               if (this.teleportRandomly()) {
                  return result.setEntityWasAffected();
               }
            }
         }
      }

      return result;
   }

   public boolean isScreaming() {
      return this.dataWatcher.getWatchableObjectByte(18) > 0;
   }

   public void setScreaming(boolean par1) {
      this.dataWatcher.updateObject(18, (byte)(par1 ? 1 : 0));
   }

   public boolean isEntityBiologicallyAlive() {
      return false;
   }

   public boolean canSpawnInShallowWater() {
      return false;
   }

   public void tryAddArrowToContainedItems(EntityArrow entity_arrow) {
   }

   public boolean isFrenzied() {
      return false;
   }

   static {
      attackingSpeedBoostModifier = (new AttributeModifier(attackingSpeedBoostModifierUUID, "Attacking speed boost", 6.199999809265137, 0)).setSaved(false);
      carriableBlocks = new boolean[256];
      carriableBlocks[Block.grass.blockID] = true;
      carriableBlocks[Block.dirt.blockID] = true;
      carriableBlocks[Block.sand.blockID] = true;
      carriableBlocks[Block.gravel.blockID] = true;
      carriableBlocks[Block.plantYellow.blockID] = true;
      carriableBlocks[Block.plantRed.blockID] = true;
      carriableBlocks[Block.mushroomBrown.blockID] = true;
      carriableBlocks[Block.mushroomRed.blockID] = true;
      carriableBlocks[Block.tnt.blockID] = true;
      carriableBlocks[Block.cactus.blockID] = true;
      carriableBlocks[Block.blockClay.blockID] = true;
      carriableBlocks[Block.pumpkin.blockID] = true;
      carriableBlocks[Block.melon.blockID] = true;
      carriableBlocks[Block.mycelium.blockID] = true;
   }
}
