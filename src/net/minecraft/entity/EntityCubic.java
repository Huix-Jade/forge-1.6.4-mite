package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet24MobSpawn;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public abstract class EntityCubic extends EntityLiving implements IMob {
   public float squishAmount;
   public float squishFactor;
   public float prevSquishFactor;
   private int slimeJumpDelay;
   private EntityItem last_targetted_item;
   private EntityLivingBase last_targetted_non_player;

   public EntityCubic(World par1World) {
      super(par1World);
      this.getNavigator().setAvoidsWater(true);
      int var2 = 1 << this.rand.nextInt(3);
      this.yOffset = 0.0F;
      this.slimeJumpDelay = this.getJumpDelay((Entity)null);
      this.setSize(var2);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)1));
   }

   public void onSendToClient(Packet24MobSpawn packet) {
      if (this.onClient()) {
         this.updateSize();
      }

   }

   public void setSize(int par1) {
      this.dataWatcher.updateObject(16, new Byte((byte)par1));
      this.updateSize();
      this.setPosition(this.posX, this.posY, this.posZ);
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((double)(par1 * par1));
      this.setHealth(this.getMaxHealth());
   }

   private void updateSize() {
      this.setSize(0.5F * (float)this.getSize(), 0.5F * (float)this.getSize());
   }

   public int getSize() {
      return this.dataWatcher.getWatchableObjectByte(16);
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("Size", this.getSize() - 1);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.setSize(par1NBTTagCompound.getInteger("Size") + 1);
   }

   public abstract EnumParticle getSquishParticle();

   public String getJumpSound() {
      return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
   }

   public void onUpdate() {
      if (!this.worldObj.isRemote && this.worldObj.difficultySetting == 0 && this.getSize() > 0) {
         this.setDead();
      }

      this.prevSquishFactor = this.squishFactor;
      this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
      boolean var1 = this.onGround;
      super.onUpdate();
      if (this.isOoze() && var1) {
         this.squishAmount = 0.0F + (float)Math.sin((double)((float)this.getTicksExistedWithOffset() / 3.0F)) * 0.1F;
      } else if (this.isGelatinousCube() && this.getAsEntityGelatinousCube().isFeeding()) {
         this.squishAmount = 0.0F + (float)Math.sin((double)((float)this.getTicksExistedWithOffset() / 5.0F)) * 0.1F;
      } else {
         int var2;
         if (this.onGround && !var1) {
            var2 = this.getSize();

            for(int var3 = 0; var3 < var2 * 8; ++var3) {
               float var4 = this.rand.nextFloat() * 3.1415927F * 2.0F;
               float var5 = this.rand.nextFloat() * 0.5F + 0.5F;
               float var6 = MathHelper.sin(var4) * (float)var2 * 0.5F * var5;
               float var7 = MathHelper.cos(var4) * (float)var2 * 0.5F * var5;
               this.worldObj.spawnParticle(this.getSquishParticle(), this.posX + (double)var6, this.boundingBox.minY, this.posZ + (double)var7, 0.0, 0.0, 0.0);
            }

            if (this.makesSoundOnLand()) {
               this.playSound(this.getJumpSound(), this.getSoundVolume(this.getJumpSound()), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            }

            this.squishAmount = -0.5F;
         } else if (!this.onGround && var1) {
            this.squishAmount = 1.0F;
         }

         this.alterSquishAmount();
         if (this.worldObj.isRemote) {
            var2 = this.getSize();
            this.updateSize();
         }

      }
   }

   public abstract boolean attacksAnimals();

   public abstract boolean attacksVillagers();

   public boolean seeksItems() {
      return this.isGelatinousCube();
   }

   public final boolean preysUpon(Entity entity) {
      return this.attacksAnimals() && entity.isTrueAnimal() || this.attacksVillagers() && entity instanceof EntityVillager;
   }

   public boolean triesToDamageOnContact(Entity entity) {
      return entity instanceof EntityPlayer || this.preysUpon(entity);
   }

   public EntityItem getClosestDissolvableEntityItem(double max_distance, boolean requires_line_of_sight, boolean requires_path) {
      List items = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(max_distance, max_distance, max_distance));
      if (items.isEmpty()) {
         return null;
      } else {
         EntityItem closest_item = null;
         float closest_distance = 0.0F;
         Iterator i = items.iterator();

         while(true) {
            EntityItem entity_item;
            float distance;
            do {
               do {
                  int x;
                  int y;
                  int z;
                  Block block;
                  do {
                     do {
                        do {
                           do {
                              do {
                                 if (!i.hasNext()) {
                                    return closest_item;
                                 }

                                 entity_item = (EntityItem)i.next();
                              } while(entity_item.isDead);
                           } while(entity_item.getHealth() <= 0);
                        } while(!entity_item.getEntityItem().isHarmedBy(this.getAsEntityGelatinousCube().getDamageTypeVsItems()));

                        x = entity_item.getBlockPosX();
                        y = entity_item.getBlockPosY();
                        z = entity_item.getBlockPosZ();
                        block = this.worldObj.getBlock(x, y, z);
                     } while(block != null && block.isSolid(this.worldObj.getBlockMetadata(x, y, z)) && this.getAsEntityGelatinousCube().getDissolvePeriod(block, x, y, z) == -1);
                  } while(requires_line_of_sight && !this.getEntitySenses().canSee(entity_item));
               } while(requires_path && !this.canPathTo(entity_item.getBlockPosX(), entity_item.getBlockPosY(), entity_item.getBlockPosZ(), (int)max_distance));

               distance = this.getDistanceToEntity(entity_item);
            } while(closest_item != null && !(distance < closest_distance));

            closest_item = entity_item;
            closest_distance = distance;
         }
      }
   }

   protected void updateEntityActionState() {
      this.tryDespawnEntity();
      Entity var1 = this.findPlayerToAttack(this.isOoze() ? 32.0F : 16.0F);
      if (var1 == null) {
         var1 = this.worldObj.getClosestVulnerablePlayer(this, 8.0, false);
      }

      if (var1 == null && this.isGelatinousCube()) {
         EntityGelatinousCube gelatinous_cube = this.getAsEntityGelatinousCube();
         if (!gelatinous_cube.isBlockFeedingCountdownAboveZero()) {
            if (this.seeksItems()) {
               if (this.getTicksExistedWithOffset() % 20 == 0) {
                  this.last_targetted_item = this.getClosestDissolvableEntityItem(8.0, true, false);
               }

               if (this.last_targetted_item != null) {
                  if (!this.last_targetted_item.isDead && !((float)this.last_targetted_item.getHealth() <= 0.0F)) {
                     var1 = this.last_targetted_item;
                  } else {
                     this.last_targetted_item = null;
                  }
               }
            }

            if (var1 == null && !gelatinous_cube.isFeeding()) {
               if (this.getTicksExistedWithOffset() % 20 == 0) {
                  this.last_targetted_non_player = this.worldObj.getClosestPrey(this, 8.0, false, false);
               }

               if (this.last_targetted_non_player != null) {
                  if (!this.last_targetted_non_player.isDead && !(this.last_targetted_non_player.getHealth() <= 0.0F)) {
                     var1 = this.last_targetted_non_player;
                  } else {
                     this.last_targetted_non_player = null;
                  }
               }
            }
         }
      }

      boolean move_like_an_ooze = this.isOoze() || this.isGelatinousCube() && var1 instanceof EntityItem && this.boundingBox.intersectsWith(((Entity)var1).boundingBox.copy().scaleXZ(4.0));
      boolean move_slowly = false;
      if (!move_like_an_ooze && this.isGelatinousCube() && this.worldObj.doesBBIntersectWithBlockCollisionBounds(this.boundingBox.translateCopy(0.0, 0.25, 0.0))) {
         move_like_an_ooze = true;
         move_slowly = true;
      }

      if (var1 != null) {
         this.faceEntity((Entity)var1, 10.0F, 20.0F);
      }

      if (this.isGelatinousCube()) {
         if (var1 instanceof EntityPlayer) {
            this.getAsEntityGelatinousCube().setBlockFeedingCountdown(0);
            this.getAsEntityGelatinousCube().setItemFeedingCountdown(0);
         } else {
            boolean is_touching_target_item = var1 instanceof EntityItem && this.boundingBox.intersectsWith(((Entity)var1).boundingBox);
            if (is_touching_target_item || this.getAsEntityGelatinousCube().isFeeding()) {
               if (this.onGround) {
                  this.moveStrafing = 0.0F;
                  if (!is_touching_target_item || this.isCollidedHorizontally || this.boundingBox.copy().scaleXZ(0.5).intersectsWith(((Entity)var1).boundingBox)) {
                     this.moveForward = 0.0F;
                  }
               }

               return;
            }
         }
      }

      if (var1 != null && this.slimeJumpDelay > this.getJumpDelay((Entity)var1)) {
         this.slimeJumpDelay = this.getJumpDelay((Entity)var1);
      }

      if (move_like_an_ooze) {
         this.moveStrafing = 0.0F;
         this.moveForward = var1 == null ? 0.15F : 0.25F;
         if (move_slowly) {
            this.moveForward *= 0.65F;
         }
      } else if (this.onGround && this.slimeJumpDelay-- <= 0) {
         this.slimeJumpDelay = this.getJumpDelay((Entity)var1);
         this.isJumping = true;
         if (this.makesSoundOnJump()) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(this.getJumpSound()), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
         }

         this.moveStrafing = 1.0F - this.rand.nextFloat() * 2.0F;
         this.moveForward = (float)(1 * this.getSize());
      } else {
         this.isJumping = false;
         if (this.onGround) {
            this.moveStrafing = this.moveForward = 0.0F;
         }
      }

      if (this.onGround && (this.isAcidic() || this.isMagmaCube())) {
         BlockInfo[] infos = this.getBlocksBelow();

         for(int i = 0; i < infos.length; ++i) {
            BlockInfo info = infos[i];
            if (info != null) {
               info.block.setBlockBoundsBasedOnStateAndNeighbors(this.worldObj, info.x, info.y, info.z);
               if (!(this.boundingBox.minY - (double)info.y - info.block.getBlockBoundsMaxY(Minecraft.getThreadIndex()) > 0.01) && this.worldObj.getBlock(info.x, info.y + 1, info.z) != Block.snow) {
                  info.block.onContactWithAcid(this.worldObj, info.x, info.y, info.z, EnumFace.TOP, false);
               }
            }
         }
      }

   }

   protected void alterSquishAmount() {
      this.squishAmount *= 0.6F;
   }

   public abstract int getJumpDelay(Entity var1);

   public abstract EntityCubic createInstance();

   public void setDead() {
      if (this.isBurning()) {
         super.setDead();
      } else {
         int var1 = this.getSize();
         if (!this.worldObj.isRemote && var1 > 1 && this.getHealth() <= 0.0F) {
            int var2 = 2 + this.rand.nextInt(3);

            for(int var3 = 0; var3 < var2; ++var3) {
               float var4 = ((float)(var3 % 2) - 0.5F) * (float)var1 / 4.0F;
               float var5 = ((float)(var3 / 2) - 0.5F) * (float)var1 / 4.0F;
               EntityCubic var6 = this.createInstance();
               var6.setSize(var1 / 2);
               var6.setLocationAndAngles(this.posX + (double)var4, this.posY + 0.5, this.posZ + (double)var5, this.rand.nextFloat() * 360.0F, 0.0F);
               this.worldObj.spawnEntityInWorld(var6);
            }
         }

         super.setDead();
      }
   }

   public boolean isRepelledByCollisionWithPlayer() {
      return false;
   }

   private float getReachSquared() {
      int size = this.getSize();
      float reach = (float)size * 0.6F;
      if (size == 1) {
         reach *= 1.1F;
      } else if (size == 2) {
         reach *= 0.9F;
      } else if (size >= 3) {
         reach *= 0.7F;
      }

      return reach * reach;
   }

   public void onCollideWithPlayer(EntityPlayer player) {
      if (this.getDistanceSqToEntity(player) <= (double)this.getReachSquared() && this.canSeeEntity(player)) {
         if (this.slowsPlayerOnContact()) {
            player.collided_with_gelatinous_cube = true;
         }

         if (this.onServer()) {
            this.attackEntityAsMob(player);
         }
      }

   }

   protected void collideWithEntity(Entity entity) {
      if (this.onServer() && this.triesToDamageOnContact(entity) && !entity.isEntityPlayer() && this.getDistanceSqToEntity(entity) <= (double)this.getReachSquared() && this.canSeeEntity(entity)) {
         this.attackEntityAsMob(entity);
      }

      super.collideWithEntity(entity);
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      if (!this.triesToDamageOnContact(target)) {
         return null;
      } else {
         EntityDamageResult result = target.attackEntityFrom(new Damage(DamageSource.causeMobDamage(this), (float)this.getAttackStrength()));
         if (result != null && !result.entityWasDestroyed()) {
            if (result.entityWasNegativelyAffectedButNotDestroyed()) {
               if (this.isGelatinousCube()) {
                  if (target.isEntityPlayer()) {
                     target.getAsPlayer().dealDamageToInventory(this.getAsEntityGelatinousCube().getDamageTypeVsItems(), 0.05F * (float)this.getSize(), (float)this.getAttackStrength(), true);
                  }

                  if (this.isBlob() && result.entityLostHealth()) {
                     target.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 50, 5));
                  }
               }

               if (this.isMagmaCube() && this.rand.nextInt(10) < this.getSize() * 2) {
                  target.setFire(this.getSize() * 3);
               }
            }

            return result;
         } else {
            return result;
         }
      }
   }

   public boolean slowsPlayerOnContact() {
      return true;
   }

   public abstract int getAttackStrengthMultiplierForType();

   protected final int getAttackStrength() {
      return this.getSize() * this.getAttackStrengthMultiplierForType();
   }

   protected String getHurtSound() {
      return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
   }

   protected String getDeathSound() {
      return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
   }

   protected int getDropItemId() {
      return 0;
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      if (this.isGelatinousCube() && !this.isSlime()) {
         if (this.isAcidic() && this.getBlockBelow() != Block.stone) {
            return false;
         } else {
            return (!perform_light_check || this.isValidLightLevel()) && super.getCanSpawnHere(perform_light_check);
         }
      } else {
         Chunk var1 = this.worldObj.getChunkFromBlockCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
         if (this.worldObj.getWorldInfo().getTerrainType() == WorldType.FLAT && this.rand.nextInt(4) != 1) {
            return false;
         } else {
            if (this.getSize() == 1 || this.worldObj.difficultySetting > 0) {
               BiomeGenBase var2 = this.worldObj.getBiomeGenForCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
               if (var2 == BiomeGenBase.swampland && this.posY > 50.0 && this.posY < 70.0 && this.rand.nextFloat() < 0.5F && this.rand.nextFloat() < this.worldObj.getCurrentMoonPhaseFactor() && this.worldObj.getBlockLightValue(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) <= this.rand.nextInt(8)) {
                  return super.getCanSpawnHere(perform_light_check);
               }

               if (this.rand.nextInt(10) == 0 && var1.getRandomWithSeed(987234911L).nextInt(10) == 0 && this.posY < 40.0) {
                  return super.getCanSpawnHere(perform_light_check);
               }
            }

            return false;
         }
      }
   }

   public float getSoundVolume(String sound) {
      return 0.2F * (float)this.getSize();
   }

   public int getVerticalFaceSpeed() {
      return 0;
   }

   protected boolean makesSoundOnJump() {
      return this.getSize() > 0;
   }

   protected boolean makesSoundOnLand() {
      return this.getSize() > 2;
   }

   public boolean breathesAir() {
      return false;
   }

   public abstract int getExperienceValue();

   public boolean canSpawnInShallowWater() {
      return true;
   }

   public boolean canTakeDamageFromPlayerThrownSnowballs() {
      return true;
   }

   public boolean canBeDamagedByCacti() {
      return false;
   }

   public final boolean isGelatinousCube() {
      return this instanceof EntityGelatinousCube;
   }

   public EntityGelatinousCube getAsEntityGelatinousCube() {
      return (EntityGelatinousCube)this;
   }

   public final boolean isSlime() {
      return this instanceof EntitySlime;
   }

   public final boolean isJelly() {
      return this instanceof EntityJelly;
   }

   public final boolean isBlob() {
      return this instanceof EntityBlob;
   }

   public final boolean isOoze() {
      return this instanceof EntityOoze;
   }

   public final boolean isPudding() {
      return this instanceof EntityPudding;
   }

   public final boolean isMagmaCube() {
      return this instanceof EntityMagmaCube;
   }

   public final boolean hasPepsin() {
      return this.isSlime() || this.isJelly() || this.isBlob();
   }

   public final boolean isAcidic() {
      return this.isOoze() || this.isPudding();
   }

   public boolean isOnLadder() {
      return false;
   }

   public float getRenderSizeModifier() {
      return (float)this.getSize();
   }

   public final boolean isHarmedByPepsin() {
      return false;
   }

   public final boolean isHarmedByAcid() {
      return !this.isPudding() && !this.isMagmaCube() ? super.isHarmedByAcid() : false;
   }
}
