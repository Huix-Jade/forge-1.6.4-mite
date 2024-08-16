package net.minecraft.entity.item;

import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockInfo;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemDamageResult;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemIngot;
import net.minecraft.item.ItemMeat;
import net.minecraft.item.ItemNugget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemVessel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastPolicies;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockOperation;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumQuality;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.EnumTournamentType;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class EntityItem extends Entity {
   public int age;
   public int delayBeforeCanPickup;
   private int health;
   public float hoverStart;
   public boolean dropped_by_player;
   private float cooking_progress;

   /**
    * The maximum age of this EntityItem.  The item is expired once this is reached.
    */
   public int lifespan = 6000;

   public EntityItem(World par1World, double par2, double par4, double par6) {
      super(par1World);
      this.health = 5;
      this.hoverStart = (float)(Math.random() * Math.PI * 2.0);
      this.setSize(0.25F, 0.25F);
      this.yOffset = this.height / 2.0F;
      this.setPosition(par2, par4, par6);
      if (!this.worldObj.isRemote) {
         this.rotationYaw = (float)(Math.random() * 360.0);
         this.motionX = (double)((float)(Math.random() * 0.20000000298023224 - 0.10000000149011612));
         this.motionY = 0.20000000298023224;
         this.motionZ = (double)((float)(Math.random() * 0.20000000298023224 - 0.10000000149011612));
      }


   }

   public EntityItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack) {
      this(par1World, par2, par4, par6);
      this.setEntityItemStack(par8ItemStack);
      if (par8ItemStack.itemID == Item.manure.itemID) {
         this.motionX = this.motionY = this.motionZ = 0.0;
      }
      this.lifespan = (par8ItemStack.getItem() == null ? 6000 : par8ItemStack.getItem().getEntityLifespan(par8ItemStack, par1World));

   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public EntityItem(World par1World) {
      super(par1World);
      this.health = 5;
      this.hoverStart = (float)(Math.random() * Math.PI * 2.0);
      this.setSize(0.25F, 0.25F);
      this.yOffset = this.height / 2.0F;
   }

   protected void entityInit() {
      this.getDataWatcher().addObjectByDataType(10, 5);
   }

   public boolean handleLavaMovement() {
      return this.worldObj.getBlockMaterial(this.getBlockPosX(), this.getBlockPosY(), this.getBlockPosZ()) == Material.lava;
   }

   public void onUpdate() {
      ItemStack stack = this.getDataWatcher().getWatchableObjectItemStack(10);
      if (stack != null && stack.getItem() != null)
      {
         if (stack.getItem().onEntityItemUpdate(this))
         {
            return;
         }
      }

      super.onUpdate();
      if (this.delayBeforeCanPickup > 0) {
         --this.delayBeforeCanPickup;
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= 0.03999999910593033;
      if (this.getEntityItem().getItem() == Item.feather) {
         if (this.motionY < -0.10000000149011612) {
            this.motionY = -0.10000000149011612;
         }

         this.motionX *= 0.949999988079071;
         this.motionZ *= 0.949999988079071;
      }

      if ((this.isInsideOfMaterial(Material.water, this.height) || this.isInsideOfMaterial(Material.lava, this.height)) && this.motionY < 0.0) {
         this.motionY *= this.getEntityItem().getItem() == Item.feather ? 0.4000000059604645 : 0.699999988079071;
         this.motionX *= 0.8999999761581421;
         this.motionZ *= 0.8999999761581421;
      }

      this.pushOutOfBlocks();
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      boolean var10000;
      if ((int)this.prevPosX == (int)this.posX && (int)this.prevPosY == (int)this.posY && (int)this.prevPosZ == (int)this.posZ) {
         var10000 = false;
      } else {
         var10000 = true;
      }

      float var2 = 0.98F;
      if (this.onGround) {
         var2 = 0.58800006F;
         BlockInfo block_info = this.getBlockRestingOn(0.1F);
         int var3 = block_info == null ? 0 : block_info.block.blockID;
         if (block_info != null && this.worldObj.getBlock(block_info.x, block_info.y + 1, block_info.z) == Block.snow && BlockSnow.getDepth(this.worldObj.getBlockMetadata(block_info.x, block_info.y + 1, block_info.z)) == 1) {
            var3 = Block.snow.blockID;
         }

         if (var3 > 0) {
            var2 = Block.blocksList[var3].slipperiness * 0.98F;
         }
      }

      this.motionX *= (double)var2;
      this.motionY *= 0.9800000190734863;
      this.motionZ *= (double)var2;
      if (this.onGround) {
         this.motionY *= -0.5;
      }

      ++this.age;
      ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);

      if (!this.worldObj.isRemote && this.age >= lifespan)
      {
         if (item != null)
         {
            ItemExpireEvent event = new ItemExpireEvent(this, (item.getItem() == null ? 6000 : item.getItem().getEntityLifespan(item, worldObj)));
            if (MinecraftForge.EVENT_BUS.post(event))
            {
               lifespan += event.extraLife;
            }
            else
            {
               this.setDead();
            }
         }
         else
         {
            this.setDead();
         }
      }

      if (item != null && item.stackSize <= 0){
         if (!this.dropped_by_player || DedicatedServer.tournament_type != EnumTournamentType.score && !this.isArtifact()) {
            this.setDead();
            this.tryRemoveFromWorldUniques();
         }
      }


      if (!this.isDead && this.onServer()) {
         float chance_of_snow_items_melting = Item.getChanceOfSnowAndIceItemsMelting(this.getBiome().temperature);
         if (chance_of_snow_items_melting > 0.0F) {
            ItemStack item_stack = this.getEntityItem();
            if ((item_stack.hasMaterial(Material.snow, true) || item_stack.hasMaterial(Material.craftedSnow, true) || item_stack.hasMaterial(Material.ice, true)) && item_stack.subjectToChanceOfDisappearing(chance_of_snow_items_melting, this.rand).stackSize < 1) {
               this.entityFX(EnumEntityFX.item_vanish);
               this.setDead();
            }
         }
      }

   }

   public void tryRemoveFromWorldUniques() {
      if (this.onClient()) {
         Minecraft.setErrorMessage("tryRemoveFromWorldUniques: called on client");
      } else if (!this.isDead) {
         Minecraft.setErrorMessage("tryRemoveFromWorldUniques: not marked dead " + this);
      } else {
         ItemStack item_stack = this.getEntityItem();
         if (item_stack.hasSignature()) {
            this.worldObj.worldInfo.removeSignature(item_stack.getSignature());
         }

      }
   }

   private void searchForOtherItemsNearby() {
      Iterator var1 = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.5, 0.0, 0.5)).iterator();

      while(var1.hasNext()) {
         EntityItem var2 = (EntityItem)var1.next();
         this.combineItems(var2);
      }

   }

   public boolean combineItems(EntityItem par1EntityItem) {
      if (par1EntityItem == this) {
         return false;
      } else if (par1EntityItem.isEntityAlive() && this.isEntityAlive()) {
         ItemStack var2 = this.getEntityItem();
         ItemStack var3 = par1EntityItem.getEntityItem();
         if (var3.getItem() != var2.getItem()) {
            return false;
         } else if (var3.hasTagCompound() ^ var2.hasTagCompound()) {
            return false;
         } else if (var3.hasTagCompound() && !var3.getTagCompound().equals(var2.getTagCompound())) {
            return false;
         } else if (var3.getItem().getHasSubtypes() && var3.getItemSubtype() != var2.getItemSubtype()) {
            return false;
         } else if (var3.stackSize < var2.stackSize) {
            return par1EntityItem.combineItems(this);
         } else if (var3.stackSize + var2.stackSize > var3.getMaxStackSize()) {
            return false;
         } else {
            var3.stackSize += var2.stackSize;
            par1EntityItem.delayBeforeCanPickup = Math.max(par1EntityItem.delayBeforeCanPickup, this.delayBeforeCanPickup);
            par1EntityItem.age = Math.min(par1EntityItem.age, this.age);
            par1EntityItem.setEntityItemStack(var3);
            this.setDead();
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean canBePickedUpBy(EntityLivingBase entity_living_base) {
      if (entity_living_base instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity_living_base;
         if (player.getAsEntityPlayerMP().portal_grace_ticks > 0) {
            return false;
         }

         if (!player.hasHeldItem() && System.currentTimeMillis() < player.getAsEntityPlayerMP().prevent_item_pickup_due_to_held_item_breaking_until) {
            return false;
         }
      }

      if (this.delayBeforeCanPickup > 0) {
         return false;
      } else if (this.isBurning() && entity_living_base.isHarmedByFire()) {
         return false;
      } else {
         if (this.getEntityItem().isBlock() && !this.getEntityItem().getItemAsBlock().getBlock().canBeCarried()) {
            Minecraft.setErrorMessage("canBePickedUpBy: block is not carriable " + this.getEntityItem());
            if (!entity_living_base.isPlayerInCreative()) {
               return false;
            }
         }

         return this.canRaycastToEntity(entity_living_base);
      }
   }

   public void setAgeToCreativeDespawnTime() {
      this.age = 0;
   }

   public boolean handleWaterMovement() {
      if (this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this)) {
         if (!this.inWater && !this.firstUpdate && this.motionY < -0.10000000149011612 && !this.worldObj.isRemote) {
            this.entityFX(EnumEntityFX.splash);
         }

         this.inWater = true;
         return true;
      } else {
         return false;
      }
   }

   private int getStackSize() {
      return this.getEntityItem().stackSize;
   }

   public void convertItem(Item item) {
      if (this.worldObj.isRemote) {
         Minecraft.setErrorMessage("convertItem: not meant to be called on client");
      } else {
         this.setEntityItemStack(new ItemStack(item, this.getStackSize()));

         ItemStack var10000;
         int stack_size;
         for(int max_stack_size = item.getItemStackLimit(0, 0); this.getStackSize() > max_stack_size; var10000.stackSize -= stack_size) {
            stack_size = Math.min(this.getStackSize() - max_stack_size, max_stack_size);
            EntityItem entity_item = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(item, stack_size));
            entity_item.motionX = this.motionX;
            entity_item.motionY = this.motionY;
            entity_item.motionZ = this.motionZ;
            entity_item.age = this.age;
            entity_item.delayBeforeCanPickup = this.delayBeforeCanPickup;
            this.worldObj.spawnEntityInWorld(entity_item);
            var10000 = this.getEntityItem();
         }

      }
   }

   public void spentTickInWater() {
      Item item = this.getEntityItem().getItem();
      if (item instanceof ItemVessel) {
         ItemVessel vessel = (ItemVessel)item;
         if (vessel.contains(Material.lava)) {
            if (!this.worldObj.isRemote) {
               this.entityFX(EnumEntityFX.steam_with_hiss);
               this.convertItem(vessel.getPeerForContents(Material.stone));
            }

            return;
         }

         if (!this.worldObj.isRemote && !vessel.contains(Material.stone)) {
            this.convertItem(vessel.getPeerForContents(Material.water));
         }
      } else if (this.onServer() && item.hasMaterial(Material.water, true)) {
         if (!this.isDead) {
            this.setDead();
         }
      } else if (this.onServer() && item.isDissolvedByWater() && !this.isDead && this.ticksExisted % 20 == 0) {
         this.attackEntityFrom(new Damage(DamageSource.melt, 1.0F));
         if (this.isDead) {
            this.entityFX(EnumEntityFX.item_vanish);
         }
      }

      super.spentTickInWater();
   }

   public void spentTickInLava() {
      if (!this.isDead) {
         Item item = this.getEntityItem().getItem();
         if (!(item instanceof ItemBucket) && !(item instanceof ItemBucketMilk)) {
            if (item instanceof ItemBlock) {
               ItemBlock item_block = (ItemBlock)item;
               Block block = item_block.getBlock();
               if ((block == Block.blockSnow || block == Block.ice) && !this.worldObj.isRemote) {
                  this.worldObj.tryConvertLavaToCobblestoneOrObsidian(this.getBlockPosX(), this.getBlockPosY(), this.getBlockPosZ());
               }
            }
         } else {
            ItemVessel vessel = (ItemVessel)item;
            if (vessel.canContentsDouseFire()) {
               if (!this.worldObj.isRemote && this.worldObj.tryConvertLavaToCobblestoneOrObsidian(this.getBlockPosX(), this.getBlockPosY(), this.getBlockPosZ())) {
                  this.convertItem(vessel.getEmptyVessel());
               }

               return;
            }

            if (!this.worldObj.isRemote) {
               this.convertItem(vessel.getPeerForContents(Material.lava));
            }
         }
      }

      super.spentTickInLava();
   }

   private boolean destroyItem(DamageSource damage_source) {
      if (this.worldObj.isRemote) {
         Minecraft.setErrorMessage("destroyItem: called on client?");
      }

      ItemStack item_stack = this.getEntityItem().getItem().getItemProducedWhenDestroyed(this.getEntityItem(), damage_source);
      if (item_stack == null) {
         this.setDead();
      } else {
         if (!this.worldObj.isRemote) {
            if (damage_source.isFireDamage()) {
               if (!this.worldObj.isRemote) {
                  this.worldObj.douseFire(this.getBlockPosX(), this.getBlockPosY(), this.getBlockPosZ(), this);
               }
            } else if (damage_source.isLavaDamage()) {
               if (this.canDouseFire()) {
                  this.causeQuenchEffect();
               }

               this.entityFX(EnumEntityFX.burned_up_in_lava);
            }
         }

         this.setEntityItemStack(item_stack);
      }

      if (this.isDead) {
         this.tryRemoveFromWorldUniques();
      }

      return this.isDead;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result != null && !result.entityWasDestroyed()) {
         ItemStack item_stack = this.getEntityItem();
         if (item_stack == null) {
            Minecraft.setErrorMessage("attackEntityFrom: EntityItem had null item_stack");
            return null;
         } else {
            Item item = item_stack.getItem();
            if (item == null) {
               Minecraft.setErrorMessage("attackEntityFrom: EntityItem had null item");
               return null;
            } else if (item == Item.netherStar && damage.isExplosion()) {
               return null;
            } else if (damage.isLavaDamage() && this.isHarmedByLava()) {
               return this.destroyItem(damage.getSource()) ? result.setEntityWasDestroyed() : result.setEntityWasAffected();
            } else if (damage.isFireDamage() && this.getEntityItem().canDouseFire()) {
               return this.destroyItem(damage.getSource()) ? result.setEntityWasDestroyed() : result.setEntityWasAffected();
            } else if (damage.getSource() == DamageSource.pepsin && !this.isHarmedByPepsin()) {
               return null;
            } else if (damage.getSource() == DamageSource.acid && !this.isHarmedByAcid()) {
               return null;
            } else {
               this.setBeenAttacked();
               if (item_stack.isItemStackDamageable()) {
                  float scaled_damage = damage.getAmount() * 20.0F * 5.0F;
                  if (item instanceof ItemArmor) {
                     scaled_damage *= (float)Item.plateIron.getMaxDamage(EnumQuality.average) / (float)Item.swordIron.getMaxDamage(EnumQuality.average);
                  } else if (!(item instanceof ItemTool)) {
                     scaled_damage = damage.getAmount();
                  }

                  if (scaled_damage < 1.0F) {
                     scaled_damage = 1.0F;
                  }

                  result.startTrackingHealth((float)item_stack.getRemainingDurability());
                  ItemDamageResult idr = item_stack.tryDamageItem(this.worldObj, Math.round(scaled_damage), false);
                  result.finishTrackingHealth((float)item_stack.getRemainingDurability());
                  if (idr != null && idr.itemWasDestroyed()) {
                     this.health = 0;
                  } else {
                     this.health = 5 * item_stack.getItemDamage() / item_stack.getMaxDamage();
                     if (this.health < 1) {
                        this.health = 1;
                     }
                  }
               } else {
                  if (damage.isFireDamage() && item instanceof ItemFood) {
                     ItemFood item_food = (ItemFood)item;
                     if (item_food.getCookedItem() != null || item_food.getUncookedItem() != null) {
                        int xp_reward;
                        int xp_share;
                        if (item_food.getCookedItem() != null) {
                           int x = this.getBlockPosX();
                           xp_reward = this.getBlockPosY();
                           xp_share = this.getBlockPosZ();

                           for(int dx = -1; dx <= 1; ++dx) {
                              for(int dz = -1; dz <= 1; ++dz) {
                                 Block block = this.worldObj.getBlock(x + dx, xp_reward, xp_share + dz);
                                 if (block == Block.fire) {
                                    this.worldObj.getAsWorldServer().addScheduledBlockOperation(EnumBlockOperation.try_extinguish_by_items, x + dx, xp_reward, xp_share + dz, (this.worldObj.getTotalWorldTime() / 10L + 1L) * 10L, false);
                                 }
                              }
                           }
                        }

                        this.cooking_progress += damage.getAmount() * 3.0F;
                        if (this.cooking_progress >= 100.0F) {
                           ItemStack cooked_item_stack = item.getItemProducedWhenDestroyed(item_stack, damage.getSource());
                           if (cooked_item_stack == null) {
                              this.setDead();
                              return result.setEntityWasDestroyed();
                           }

                           if (item instanceof ItemMeat) {
                              this.playSound("imported.random.sizzle", 1.0F, 1.0F);
                           }

                           this.setEntityItemStack(cooked_item_stack);
                           xp_reward = cooked_item_stack.getExperienceReward();

                           while(xp_reward > 0) {
                              xp_share = EntityXPOrb.getXPSplit(xp_reward);
                              xp_reward -= xp_share;
                              this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY + 0.5, this.posZ + 0.5, xp_share));
                           }
                        }

                        return result.setEntityWasAffected();
                     }
                  }

                  result.startTrackingHealth((float)this.health);
                  this.health = (int)((float)this.health - damage.getAmount());
                  result.finishTrackingHealth((float)this.health);
               }

               if (result.entityWasNegativelyAffected() && (damage.isPepsinDamage() || damage.isAcidDamage())) {
                  if (this.health <= 0) {
                     this.entityFX(damage.isAcidDamage() ? EnumEntityFX.smoke_and_steam_with_hiss : EnumEntityFX.steam_with_hiss);
                  } else {
                     this.entityFX(EnumEntityFX.item_vanish);
                  }
               }

               if (this.health <= 0) {
                  if (damage.isFireDamage()) {
                     this.entityFX(EnumEntityFX.smoke);
                  }

                  if (!this.getEntityItem().hasSignature() && this.getEntityItem().getItem().hasContainerItem()) {
                     Item container = this.getEntityItem().getItem().getContainerItem();
                     if (!container.isHarmedBy(damage.getSource())) {
                        this.convertItem(container);
                        return result;
                     }
                  }

                  this.setDead();
                  if (item_stack.hasSignatureThatHasBeenAddedToWorld(this.worldObj)) {
                     this.tryRemoveFromWorldUniques();
                  }

                  result.setEntityWasDestroyed();
               }

               return result;
            }
         }
      } else {
         return result;
      }
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setShort("Health", (short)((byte)this.health));
      par1NBTTagCompound.setShort("Age", (short)this.age);
      this.age = par1NBTTagCompound.getShort("Age");
      par1NBTTagCompound.setInteger("Lifespan", lifespan);
      if (this.getEntityItem() != null) {
         par1NBTTagCompound.setCompoundTag("Item", this.getEntityItem().writeToNBT(new NBTTagCompound()));
      }

      par1NBTTagCompound.setBoolean("dropped_by_player", this.dropped_by_player);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      this.health = par1NBTTagCompound.getShort("Health") & 255;


      ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);

      if (item == null || item.stackSize <= 0)
      {
         this.setDead();
      }

      if (par1NBTTagCompound.hasKey("Lifespan"))
      {
         lifespan = par1NBTTagCompound.getInteger("Lifespan");
      }
      NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("Item");
      this.setEntityItemStack(ItemStack.loadItemStackFromNBT(var2));
      if (this.getEntityItem() == null) {
         this.setDead();
      }

      this.dropped_by_player = par1NBTTagCompound.getBoolean("dropped_by_player");
   }

   public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
      if (!par1EntityPlayer.isGhost() && !par1EntityPlayer.isZevimrgvInTournament()) {
         if (!(par1EntityPlayer.ridingEntity instanceof EntityHorse) || !(this.posY - par1EntityPlayer.getFootPosY() < -0.5)) {
            if (!this.worldObj.isRemote) {
               if (this.delayBeforeCanPickup > 0)
               {
                  return;
               }

               EntityItemPickupEvent event = new EntityItemPickupEvent(par1EntityPlayer, this);

               if (MinecraftForge.EVENT_BUS.post(event))
               {
                  return;
               }


               boolean was_empty_handed_before = !par1EntityPlayer.hasHeldItem();
               ItemStack var2 = this.getEntityItem();
               int var3 = var2.stackSize;
               if (this.delayBeforeCanPickup <= 0 && (event.getResult() == Result.ALLOW || var3 <= 0 || par1EntityPlayer.inventory.addItemStackToInventory(var2)))
               {
                  if (var2.itemID == Block.wood.blockID) {
                     par1EntityPlayer.triggerAchievement(AchievementList.mineWood);
                  }

                  if (var2.itemID == Item.leather.itemID) {
                     par1EntityPlayer.triggerAchievement(AchievementList.killCow);
                  }

                  if (var2.itemID == Item.diamond.itemID) {
                     par1EntityPlayer.triggerAchievement(AchievementList.diamonds);
                  }

                  if (var2.itemID == Item.emerald.itemID) {
                     par1EntityPlayer.triggerAchievement(AchievementList.emeralds);
                  }

                  if (var2.itemID == Item.blazeRod.itemID) {
                     par1EntityPlayer.triggerAchievement(AchievementList.blazeRod);
                  }

                  if (var2.itemID == Item.seeds.itemID || var2.itemID == Item.blueberries.itemID || var2.itemID == Item.wormRaw.itemID) {
                     par1EntityPlayer.triggerAchievement(AchievementList.seeds);
                  }

                  if (var2.itemID == Item.stick.itemID) {
                     par1EntityPlayer.triggerAchievement(AchievementList.stickPicker);
                  }

                  if (var2.itemID == Item.copperNugget.itemID || var2.itemID == Item.silverNugget.itemID || var2.itemID == Item.goldNugget.itemID || var2.itemID == Item.ironNugget.itemID || var2.itemID == Item.mithrilNugget.itemID || var2.itemID == Item.adamantiumNugget.itemID) {
                     par1EntityPlayer.triggerAchievement(AchievementList.nuggets);
                  }

                  if (var2.itemID == Item.wheat.itemID) {
                     this.worldObj.worldInfo.fullfillVillageCondition(1, (WorldServer)this.worldObj);
                  }

                  if (var2.itemID == Item.carrot.itemID) {
                     this.worldObj.worldInfo.fullfillVillageCondition(2, (WorldServer)this.worldObj);
                  }

                  if (var2.itemID == Item.potato.itemID) {
                     this.worldObj.worldInfo.fullfillVillageCondition(4, (WorldServer)this.worldObj);
                  }

                  if (var2.itemID == Item.onion.itemID) {
                     this.worldObj.worldInfo.fullfillVillageCondition(8, (WorldServer)this.worldObj);
                  }

                  this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                  par1EntityPlayer.onItemPickup(this, var3);
                  if (var2.stackSize <= 0) {
                     this.setDead();
                  }

                  if (was_empty_handed_before && par1EntityPlayer.hasHeldItem()) {
                     par1EntityPlayer.sendPacket(new Packet85SimpleSignal(EnumSignal.picked_up_held_item));
                  }
               }
            }

         }
      }
   }

   public String getEntityName() {
      return "EntityItem: " + this.getEntityItem().getDisplayName() + " x " + this.getStackSize();
   }

   public boolean canAttackWithItem() {
      return false;
   }

   public ItemStack getEntityItem() {
      ItemStack var1 = this.getDataWatcher().getWatchableObjectItemStack(10);
      if (var1 == null) {
         if (this.worldObj != null) {
            this.worldObj.getWorldLogAgent().logSevere("Item entity " + this.entityId + " has no item?!");
         }

         return new ItemStack(Block.stone);
      } else {
         return var1;
      }
   }

   public void setEntityItemStack(ItemStack par1ItemStack) {
      if (par1ItemStack != null && par1ItemStack.isBlock() && !par1ItemStack.getItemAsBlock().getBlock().canBeCarried()) {
         Minecraft.setErrorMessage("setEntityItemStack: the block " + par1ItemStack + " is not carriable");
      }

      this.getDataWatcher().updateObject(10, par1ItemStack);
      this.getDataWatcher().setObjectWatched(10);
      this.health = 5;
      this.cooking_progress = 0.0F;
   }

   public boolean isImmuneToExplosion() {
      ItemStack item_stack = this.getEntityItem();
      if (item_stack != null && item_stack.itemID >= 256 && !item_stack.isItemStackDamageable()) {
         Item item = item_stack.getItem();
         if (item == null) {
            return false;
         } else if (item.isCompletelyMetal()) {
            return true;
         } else {
            return item instanceof ItemNugget || item instanceof ItemIngot || item == Item.redstone;
         }
      } else {
         return false;
      }
   }

   public boolean handleExplosion(Explosion explosion) {
      this.applyExplosionMotion(explosion);
      if (this.isImmuneToExplosion()) {
         return true;
      } else {
         ItemStack item_stack = this.getEntityItem();
         if (item_stack == null) {
            return false;
         } else {
            Item item = item_stack.getItem();
            if (item == null) {
               return false;
            } else {
               double dx = this.posX - explosion.explosionX;
               double dy = this.posY - explosion.explosionY;
               double dz = this.posZ - explosion.explosionZ;
               World var10002 = this.worldObj;
               float explosion_force = this.calcExplosionForce(explosion.explosion_size_vs_blocks, World.getDistanceSqFromDeltas(dx, dy * 0.5, dz));
               if (item.itemID < 256) {
                  Block block = Block.getBlock(item.itemID);
                  if (block == null) {
                     return false;
                  }

                  World var10000 = this.worldObj;
                  double distance_sq = World.getDistanceSqFromDeltas(this.posX - explosion.explosionX, this.posY - explosion.explosionY, this.posZ - explosion.explosionZ);
                  if (distance_sq < 1.0) {
                     distance_sq = 1.0;
                  }

                  if (explosion_force * 2.0F < block.getExplosionResistance(explosion)) {
                     return true;
                  }

                  int metadata = item_stack.getItemSubtype();
                  int i;
                  if (!block.isValidMetadata(metadata)) {
                     for(i = 0; i < 16; ++i) {
                        if (block.isValidMetadata(i)) {
                           metadata = i;
                           break;
                        }
                     }
                  }

                  for(i = 0; i < item_stack.stackSize; ++i) {
                     block.dropBlockAsEntityItem((new BlockBreakInfo(this.worldObj, this.getBlockPosX(), this.getBlockPosY(), this.getBlockPosZ())).setBlock(block, metadata).setExploded(explosion));
                  }
               } else {
                  if (item_stack.isItemStackDamageable()) {
                     this.attackEntityFrom(new Damage(DamageSource.setExplosionSource(explosion), explosion_force * 64.0F));
                     return true;
                  }

                  if (explosion_force < 0.2F) {
                     return true;
                  }

                  Block block = null;
                  if (item == Item.bed) {
                     block = Block.bed;
                  } else if (item == Item.doorWood) {
                     block = Block.doorWood;
                  }

                  if (block != null) {
                     ((Block)block).dropBlockAsEntityItem((new BlockBreakInfo(this.worldObj, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))).setBlock(Block.bed, 0).setExploded(explosion));
                     this.setDead();
                     this.tryRemoveFromWorldUniques();
                     return true;
                  }

                  if (item == Item.flint) {
                     item = Item.chipFlint;
                  } else if (item == Item.emerald) {
                     item = Item.shardEmerald;
                  } else if (item == Item.diamond) {
                     item = Item.shardDiamond;
                  } else {
                     item = null;
                  }

                  if (item != null && !this.worldObj.isRemote) {
                     for(int i = 0; i < item_stack.stackSize; ++i) {
                        EntityItem entity_item = (new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(item))).applyExplosionMotion(explosion);
                        this.worldObj.spawnEntityInWorld(entity_item);
                     }
                  }
               }

               this.setDead();
               this.tryRemoveFromWorldUniques();
               return true;
            }
         }
      }
   }

   public float calcExplosionForce(float explosion_size, double distance_sq) {
      return (float)((double)explosion_size / Math.pow(distance_sq, 0.75));
   }

   public EntityItem applyExplosionMotion(Explosion explosion) {
      float size = explosion.explosion_size_vs_blocks;
      double dx = this.posX - explosion.explosionX;
      double dy = this.posY - explosion.explosionY;
      double dz = this.posZ - explosion.explosionZ;
      World var10000 = this.worldObj;
      double distance_sq = World.getDistanceSqFromDeltas(dx, dy * 0.5, dz);
      if (distance_sq < 1.0) {
         distance_sq = 1.0;
      }

      float force = this.calcExplosionForce(explosion.explosion_size_vs_blocks, distance_sq);
      this.motionX = dx * (double)force * 0.17499999701976776;
      this.motionY = 0.4000000059604645 * Math.sqrt((double)size / Math.pow(distance_sq, 0.75));
      this.motionZ = dz * (double)force * 0.17499999701976776;
      this.motionX *= (double)(0.8F + this.rand.nextFloat() * 0.4F);
      this.motionY *= (double)(0.8F + this.rand.nextFloat() * 0.4F);
      this.motionZ *= (double)(0.8F + this.rand.nextFloat() * 0.4F);
      this.send_position_update_immediately = true;
      return this;
   }

   public boolean canDouseFire() {
      return this.getEntityItem().canDouseFire();
   }

   public boolean canCatchFire() {
      return this.getEntityItem().canCatchFire();
   }

   public boolean isHarmedByFire() {
      return this.getEntityItem().isHarmedByFire();
   }

   public boolean isHarmedByLava() {
      return this.getEntityItem().isHarmedByLava();
   }

   public boolean canRaycastToEntity(EntityLivingBase elb) {
      Raycast raycast = (new Raycast(this.worldObj, this.getCenterPoint())).setPolicies(RaycastPolicies.for_entity_item_pickup);
      if (raycast.checkForNoBlockCollision(elb.getFootPosPlusFractionOfHeight(0.25F))) {
         return true;
      } else {
         return raycast.checkForNoBlockCollision(elb.getFootPosPlusFractionOfHeight(0.5F)) ? true : raycast.checkForNoBlockCollision(elb.getFootPosPlusFractionOfHeight(0.75F));
      }
   }

   public boolean isArtifact() {
      return this.getEntityItem() != null && this.getEntityItem().isArtifact();
   }

   public boolean isEntityInvulnerable() {
      return this.isArtifact() || super.isEntityInvulnerable();
   }

   public boolean isHarmedByPepsin() {
      return this.getEntityItem().isHarmedByPepsin();
   }

   public boolean isHarmedByAcid() {
      return this.getEntityItem().isHarmedByAcid();
   }

   public boolean isHarmedBy(DamageSource damage_source) {
      return this.getEntityItem().isHarmedBy(damage_source);
   }

   public boolean isVessel() {
      return this.getEntityItem().getItem() instanceof ItemVessel;
   }

   public int getHealth() {
      return this.health;
   }
}
