package net.minecraft.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet20NamedEntitySpawn;
import net.minecraft.network.packet.Packet23VehicleSpawn;
import net.minecraft.network.packet.Packet24MobSpawn;
import net.minecraft.network.packet.Packet25EntityPainting;
import net.minecraft.network.packet.Packet26EntityExpOrb;
import net.minecraft.network.packet.Packet28EntityVelocity;
import net.minecraft.network.packet.Packet32EntityLook;
import net.minecraft.network.packet.Packet34EntityTeleport;
import net.minecraft.network.packet.Packet35EntityHeadRotation;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.network.packet.Packet40EntityMetadata;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet44UpdateAttributes;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.network.packet.Packet83EntityTeleportCompact;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.SpatialScaler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;

public class EntityTrackerEntry {
   public Entity myEntity;
   public int blocksDistanceThreshold;
   public int updateFrequency;
   public int lastScaledXPosition;
   public int lastScaledYPosition;
   public int lastScaledZPosition;
   private double last_pos_x;
   private double last_pos_y;
   private double last_pos_z;
   public int lastYaw;
   public int lastPitch;
   public int lastHeadMotion;
   public double motionX;
   public double motionY;
   public double motionZ;
   public int ticks;
   private double posX;
   private double posY;
   private double posZ;
   private boolean isDataInitialized;
   private boolean sendVelocityUpdates;
   private Entity field_85178_v;
   private boolean ridingEntity;
   public boolean playerEntitiesUpdated;
   public Set trackingPlayers = new HashSet();

   public EntityTrackerEntry(Entity par1Entity, int par2, int par3, boolean par4) {
      this.myEntity = par1Entity;
      this.blocksDistanceThreshold = par2;
      this.updateFrequency = par3;
      this.sendVelocityUpdates = par4;
      this.lastScaledXPosition = SpatialScaler.getScaledPosX(par1Entity);
      this.lastScaledYPosition = SpatialScaler.getScaledPosY(par1Entity);
      this.lastScaledZPosition = SpatialScaler.getScaledPosZ(par1Entity);
      this.last_pos_x = par1Entity.posX;
      this.last_pos_y = par1Entity.posY;
      this.last_pos_z = par1Entity.posZ;
      this.lastYaw = MathHelper.floor_float(par1Entity.rotationYaw * 256.0F / 360.0F);
      this.lastPitch = MathHelper.floor_float(par1Entity.rotationPitch * 256.0F / 360.0F);
      this.lastHeadMotion = MathHelper.floor_float(par1Entity.getRotationYawHead() * 256.0F / 360.0F);
   }

   public boolean equals(Object par1Obj) {
      return par1Obj instanceof EntityTrackerEntry ? ((EntityTrackerEntry)par1Obj).myEntity.entityId == this.myEntity.entityId : false;
   }

   public int hashCode() {
      return this.myEntity.entityId;
   }

   public void sendLocationToAllClients(List par1List) {
      this.playerEntitiesUpdated = false;
      boolean velocity_update_sent_to_every_tracking_player = false;
      if (!this.isDataInitialized || this.myEntity.getDistanceSq(this.posX, this.posY, this.posZ) > 16.0) {
         this.posX = this.myEntity.posX;
         this.posY = this.myEntity.posY;
         this.posZ = this.myEntity.posZ;
         this.isDataInitialized = true;
         this.playerEntitiesUpdated = true;
         this.sendEventsToPlayers(par1List);
      }

      if (this.myEntity.velocityChanged) {
         this.sendPacketToAllAssociatedPlayers(new Packet28EntityVelocity(this));
         this.myEntity.velocityChanged = false;
         velocity_update_sent_to_every_tracking_player = true;
      }

      if (this.field_85178_v != this.myEntity.ridingEntity || this.myEntity.ridingEntity != null && this.ticks % 60 == 0) {
         this.field_85178_v = this.myEntity.ridingEntity;
         this.sendPacketToAllTrackingPlayers(new Packet39AttachEntity(0, this.myEntity, this.myEntity.ridingEntity));
      }

      if (this.myEntity instanceof EntityPlayer) {
         this.myEntity.send_position_update_immediately = true;
      }

      boolean prevent_update = false;
      if (this.myEntity instanceof EntityArrow) {
         prevent_update = true;
      }

      if (prevent_update) {
         if (this.myEntity.getDataWatcher().hasChanges()) {
            this.func_111190_b();
         }
      } else if (this.myEntity instanceof EntityItemFrame && this.ticks % 10 == 0) {
         EntityItemFrame var23 = (EntityItemFrame)this.myEntity;
         ItemStack var24 = var23.getDisplayedItem();
         if (var24 != null && var24.getItem() instanceof ItemMap) {
            MapData var26 = Item.map.getMapData(var24, this.myEntity.worldObj);
            Iterator var27 = par1List.iterator();

            while(var27.hasNext()) {
               EntityPlayer var28 = (EntityPlayer)var27.next();
               EntityPlayerMP var29 = (EntityPlayerMP)var28;
               var26.updateVisiblePlayers(var29, var24);
               if (var29.playerNetServerHandler.packetSize() <= 5) {
                  Packet var30 = Item.map.createMapDataPacket(var24, this.myEntity.worldObj, var29);
                  if (var30 != null) {
                     var29.playerNetServerHandler.sendPacketToPlayer(var30);
                  }
               }
            }
         }

         this.func_111190_b();
      } else if (this.myEntity.send_position_update_immediately || this.ticks % this.updateFrequency == 0 || this.myEntity.isAirBorne || this.myEntity.getDataWatcher().hasChanges()) {
         this.myEntity.send_position_update_immediately = false;
         int scaled_yaw = SpatialScaler.getScaledRotation(this.myEntity.rotationYaw);
         int scaled_pitch = SpatialScaler.getScaledRotation(this.myEntity.rotationPitch);
         int scaled_pos_x;
         if (this.myEntity.ridingEntity == null) {
            if (this.ticks > 0 || this.myEntity instanceof EntityArrow) {
               scaled_pos_x = SpatialScaler.getScaledPosX(this.myEntity);
               int scaled_pos_y = SpatialScaler.getScaledPosY(this.myEntity);
               int scaled_pos_z = SpatialScaler.getScaledPosZ(this.myEntity);
               Packet var10 = null;
               if (!(this.myEntity instanceof EntityPlayer) && scaled_pos_x == this.lastScaledXPosition && scaled_pos_y == this.lastScaledYPosition && scaled_pos_z == this.lastScaledZPosition) {
                  if (scaled_yaw != this.lastYaw || scaled_pitch != this.lastPitch) {
                     var10 = new Packet32EntityLook(this.myEntity);
                  }
               } else if (scaled_pos_x >= -32000 && scaled_pos_x <= 32000 && scaled_pos_z >= -32000 && scaled_pos_z <= 32000 && !this.myEntity.sync_last_tick_pos_on_next_update) {
                  var10 = new Packet83EntityTeleportCompact(this.myEntity);
               } else {
                  var10 = new Packet34EntityTeleport(this.myEntity);
               }

               if (var10 != null) {
                  this.sendPacketToAllTrackingPlayers((Packet)var10);
               }

               this.lastScaledXPosition = scaled_pos_x;
               this.lastScaledYPosition = scaled_pos_y;
               this.lastScaledZPosition = scaled_pos_z;
               this.lastYaw = scaled_yaw;
               this.lastPitch = scaled_pitch;
               this.last_pos_x = this.myEntity.posX;
               this.last_pos_y = this.myEntity.posY;
               this.last_pos_z = this.myEntity.posZ;
            }

            if (!velocity_update_sent_to_every_tracking_player && this.sendVelocityUpdates) {
               double var13 = this.myEntity.motionX - this.motionX;
               double var15 = this.myEntity.motionY - this.motionY;
               double var17 = this.myEntity.motionZ - this.motionZ;
               double var19 = 0.02;
               double var21 = var13 * var13 + var15 * var15 + var17 * var17;
               if (var21 > var19 * var19 || var21 > 0.0 && this.myEntity.motionX == 0.0 && this.myEntity.motionY == 0.0 && this.myEntity.motionZ == 0.0) {
                  this.sendPacketToAllTrackingPlayers(new Packet28EntityVelocity(this));
               }
            }

            this.func_111190_b();
            this.ridingEntity = false;
         } else {
            if (Math.abs(scaled_yaw - this.lastYaw) >= 2 || Math.abs(scaled_pitch - this.lastPitch) >= 2) {
               this.sendPacketToAllTrackingPlayers(new Packet32EntityLook(this.myEntity));
               this.lastYaw = scaled_yaw;
               this.lastPitch = scaled_pitch;
            }

            this.lastScaledXPosition = SpatialScaler.getScaledPosX(this.myEntity);
            this.lastScaledYPosition = SpatialScaler.getScaledPosY(this.myEntity);
            this.lastScaledZPosition = SpatialScaler.getScaledPosZ(this.myEntity);
            this.last_pos_x = this.myEntity.posX;
            this.last_pos_y = this.myEntity.posY;
            this.last_pos_z = this.myEntity.posZ;
            this.func_111190_b();
            this.ridingEntity = true;
         }

         scaled_pos_x = SpatialScaler.getScaledRotation(this.myEntity.getRotationYawHead());
         if (Math.abs(scaled_pos_x - this.lastHeadMotion) >= 2) {
            this.sendPacketToAllTrackingPlayers(new Packet35EntityHeadRotation(this.myEntity.entityId, (byte)scaled_pos_x));
            this.lastHeadMotion = scaled_pos_x;
         }

         this.myEntity.isAirBorne = false;
      }

      ++this.ticks;
   }

   private void func_111190_b() {
      DataWatcher var1 = this.myEntity.getDataWatcher();
      if (var1.hasChanges()) {
         this.sendPacketToAllAssociatedPlayers(new Packet40EntityMetadata(this.myEntity.entityId, var1, false));
      }

      if (this.myEntity instanceof EntityLivingBase) {
         ServersideAttributeMap var2 = (ServersideAttributeMap)((EntityLivingBase)this.myEntity).getAttributeMap();
         Set var3 = var2.func_111161_b();
         if (!var3.isEmpty()) {
            this.sendPacketToAllAssociatedPlayers(new Packet44UpdateAttributes(this.myEntity.entityId, var3));
         }

         var3.clear();
      }

   }

   public void sendPacketToAllTrackingPlayers(Packet par1Packet) {
      Iterator var2 = this.trackingPlayers.iterator();

      while(var2.hasNext()) {
         EntityPlayerMP var3 = (EntityPlayerMP)var2.next();
         var3.playerNetServerHandler.sendPacketToPlayer(par1Packet);
      }

   }

   public void sendPacketToAllAssociatedPlayers(Packet par1Packet) {
      this.sendPacketToAllTrackingPlayers(par1Packet);
      if (this.myEntity instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.myEntity).playerNetServerHandler.sendPacketToPlayer(par1Packet);
      }

   }

   public void informAllAssociatedPlayersOfItemDestruction() {
      Iterator var1 = this.trackingPlayers.iterator();

      while(var1.hasNext()) {
         EntityPlayerMP var2 = (EntityPlayerMP)var1.next();
         var2.destroyedItemsNetCache.add(this.myEntity.entityId);
      }

   }

   public void removeFromWatchingList(EntityPlayerMP par1EntityPlayerMP) {
      if (this.trackingPlayers.contains(par1EntityPlayerMP)) {
         par1EntityPlayerMP.destroyedItemsNetCache.add(this.myEntity.entityId);
         this.trackingPlayers.remove(par1EntityPlayerMP);
      }

   }

   public void tryStartWachingThis(EntityPlayerMP par1EntityPlayerMP) {
      if (par1EntityPlayerMP != this.myEntity) {
         double var2 = par1EntityPlayerMP.posX - this.last_pos_x;
         double var4 = par1EntityPlayerMP.posZ - this.last_pos_z;
         if (var2 >= (double)(-this.blocksDistanceThreshold) && var2 <= (double)this.blocksDistanceThreshold && var4 >= (double)(-this.blocksDistanceThreshold) && var4 <= (double)this.blocksDistanceThreshold) {
            if (!this.trackingPlayers.contains(par1EntityPlayerMP) && (this.isPlayerWatchingThisChunk(par1EntityPlayerMP) || this.myEntity.forceSpawn)) {
               boolean velocity_update_sent_to_this_player = false;
               this.trackingPlayers.add(par1EntityPlayerMP);
               Packet var6 = this.getPacketForThisEntity();
               par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(var6);
               if (var6 instanceof Packet23VehicleSpawn) {
                  Packet23VehicleSpawn packet23 = (Packet23VehicleSpawn)var6;
                  if (packet23.throwerEntityId > 0) {
                     velocity_update_sent_to_this_player = true;
                  }
               } else if (var6 instanceof Packet26EntityExpOrb) {
                  velocity_update_sent_to_this_player = true;
               }

               if (velocity_update_sent_to_this_player) {
                  this.motionX = this.myEntity.motionX;
                  this.motionY = this.myEntity.motionY;
                  this.motionZ = this.myEntity.motionZ;
               }

               if (!this.myEntity.getDataWatcher().getIsBlank()) {
                  par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet40EntityMetadata(this.myEntity.entityId, this.myEntity.getDataWatcher(), true));
               }

               if (this.myEntity instanceof EntityLivingBase) {
                  ServersideAttributeMap var7 = (ServersideAttributeMap)((EntityLivingBase)this.myEntity).getAttributeMap();
                  Collection var8 = var7.func_111160_c();
                  if (!var8.isEmpty()) {
                     par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet44UpdateAttributes(this.myEntity.entityId, var8));
                  }
               }

               if (!velocity_update_sent_to_this_player && this.sendVelocityUpdates && !(var6 instanceof Packet24MobSpawn)) {
                  par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet28EntityVelocity(this));
                  velocity_update_sent_to_this_player = true;
               }

               if (this.myEntity.ridingEntity != null) {
                  par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet39AttachEntity(0, this.myEntity, this.myEntity.ridingEntity));
               }

               if (this.myEntity instanceof EntityLiving && ((EntityLiving)this.myEntity).getLeashedToEntity() != null) {
                  par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet39AttachEntity(1, this.myEntity, ((EntityLiving)this.myEntity).getLeashedToEntity()));
               }

               if (this.myEntity instanceof EntityLivingBase) {
                  for(int var10 = 0; var10 < 5; ++var10) {
                     ItemStack var13 = ((EntityLivingBase)this.myEntity).getCurrentItemOrArmor(var10);
                     if (var13 != null) {
                        par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet5PlayerInventory(this.myEntity.entityId, var10, var13));
                     }
                  }
               }

               if (this.myEntity instanceof EntityLivingBase) {
                  EntityLivingBase var14 = (EntityLivingBase)this.myEntity;
                  Iterator var12 = var14.getActivePotionEffects().iterator();

                  while(var12.hasNext()) {
                     PotionEffect var9 = (PotionEffect)var12.next();
                     par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(this.myEntity.entityId, var9));
                  }
               }
            }
         } else if (this.trackingPlayers.contains(par1EntityPlayerMP)) {
            this.trackingPlayers.remove(par1EntityPlayerMP);
            par1EntityPlayerMP.destroyedItemsNetCache.add(this.myEntity.entityId);
         }
      }

   }

   private boolean isPlayerWatchingThisChunk(EntityPlayerMP par1EntityPlayerMP) {
      if (!this.myEntity.isAddedToAChunk()) {
         return false;
      } else {
         Chunk chunk = this.myEntity.getChunkAddedTo();
         return par1EntityPlayerMP.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(par1EntityPlayerMP, chunk.xPosition, chunk.zPosition);
      }
   }

   public void sendEventsToPlayers(List par1List) {
      for(int var2 = 0; var2 < par1List.size(); ++var2) {
         this.tryStartWachingThis((EntityPlayerMP)par1List.get(var2));
      }

   }

   private Packet getPacketForThisEntity() {
      if (this.myEntity.isDead) {
         this.myEntity.worldObj.getWorldLogAgent().logWarning("Fetching addPacket for removed entity");
      }

      if (this.myEntity instanceof EntityItem) {
         return new Packet23VehicleSpawn(this.myEntity, 2, 1);
      } else if (this.myEntity instanceof EntityPlayerMP) {
         return new Packet20NamedEntitySpawn((EntityPlayer)this.myEntity);
      } else if (this.myEntity instanceof EntityMinecart) {
         EntityMinecart var9 = (EntityMinecart)this.myEntity;
         return new Packet23VehicleSpawn(this.myEntity, 10, var9.getMinecartType());
      } else if (this.myEntity instanceof EntityBoat) {
         return new Packet23VehicleSpawn(this.myEntity, 1);
      } else if (!(this.myEntity instanceof IAnimals) && !(this.myEntity instanceof EntityDragon)) {
         if (this.myEntity instanceof EntityFishHook) {
            EntityPlayer var8 = ((EntityFishHook)this.myEntity).angler;
            return new Packet23VehicleSpawn(this.myEntity, 90, var8 != null ? var8.entityId : this.myEntity.entityId);
         } else if (this.myEntity instanceof EntityArrow) {
            Entity var7 = ((EntityArrow)this.myEntity).shootingEntity;
            Packet23VehicleSpawn packet = new Packet23VehicleSpawn(this.myEntity, 60, var7 != null ? var7.entityId : this.myEntity.entityId);
            packet.arrow_item_id = ((EntityArrow)this.myEntity).item_arrow.itemID;
            packet.launcher_was_enchanted = ((EntityArrow)this.myEntity).launcher_was_enchanted;
            packet.arrow_stuck_in_block = ((EntityArrow)this.myEntity).isInGround();
            return packet;
         } else if (this.myEntity instanceof EntitySnowball) {
            return new Packet23VehicleSpawn(this.myEntity, 61);
         } else if (this.myEntity instanceof EntityPotion) {
            return new Packet23VehicleSpawn(this.myEntity, 73, ((EntityPotion)this.myEntity).getPotionType());
         } else if (this.myEntity instanceof EntityExpBottle) {
            return new Packet23VehicleSpawn(this.myEntity, 75);
         } else if (this.myEntity instanceof EntityEnderPearl) {
            return new Packet23VehicleSpawn(this.myEntity, 65);
         } else if (this.myEntity instanceof EntityEnderEye) {
            return new Packet23VehicleSpawn(this.myEntity, 72);
         } else if (this.myEntity instanceof EntityFireworkRocket) {
            return new Packet23VehicleSpawn(this.myEntity, 76);
         } else {
            Packet23VehicleSpawn var2;
            if (this.myEntity instanceof EntityFireball) {
               EntityFireball var6 = (EntityFireball)this.myEntity;
               var2 = null;
               byte var3 = 63;
               if (this.myEntity instanceof EntitySmallFireball) {
                  var3 = 64;
               } else if (this.myEntity instanceof EntityWitherSkull) {
                  var3 = 66;
               }

               if (var6.shootingEntity != null) {
                  var2 = new Packet23VehicleSpawn(this.myEntity, var3, ((EntityFireball)this.myEntity).shootingEntity.entityId);
               } else {
                  var2 = new Packet23VehicleSpawn(this.myEntity, var3, 0);
               }

               var2.approx_motion_x = (float)var6.accelerationX;
               var2.approx_motion_y = (float)var6.accelerationY;
               var2.approx_motion_z = (float)var6.accelerationZ;
               return var2;
            } else if (this.myEntity instanceof EntityEgg) {
               return new Packet23VehicleSpawn(this.myEntity, 62);
            } else if (this.myEntity instanceof EntityBrick) {
               return new Packet23VehicleSpawn(this.myEntity, ((EntityBrick)this.myEntity).getModelItem() == Item.netherrackBrick ? 501 : 500);
            } else if (this.myEntity instanceof EntityGelatinousSphere) {
               EntityGelatinousSphere sphere = (EntityGelatinousSphere)this.myEntity;
               return new Packet23VehicleSpawn(this.myEntity, 600 + sphere.getModelSubtype());
            } else if (this.myEntity instanceof EntityWeb) {
               return new Packet23VehicleSpawn(this.myEntity, 700);
            } else if (this.myEntity instanceof EntityTNTPrimed) {
               return new Packet23VehicleSpawn(this.myEntity, 50);
            } else if (this.myEntity instanceof EntityEnderCrystal) {
               return new Packet23VehicleSpawn(this.myEntity, 51);
            } else if (this.myEntity instanceof EntityFallingSand) {
               EntityFallingSand var5 = (EntityFallingSand)this.myEntity;
               return new Packet23VehicleSpawn(this.myEntity, 70, var5.blockID | var5.metadata << 16);
            } else if (this.myEntity instanceof EntityPainting) {
               return new Packet25EntityPainting((EntityPainting)this.myEntity);
            } else if (this.myEntity instanceof EntityItemFrame) {
               EntityItemFrame var4 = (EntityItemFrame)this.myEntity;
               var2 = new Packet23VehicleSpawn(this.myEntity, 71, var4.hangingDirection);
               var2.setUnscaledPositionWithIntegers(var4.xPosition, var4.yPosition, var4.zPosition);
               return var2;
            } else if (this.myEntity instanceof EntityLeashKnot) {
               EntityLeashKnot var1 = (EntityLeashKnot)this.myEntity;
               var2 = new Packet23VehicleSpawn(this.myEntity, 77);
               var2.setUnscaledPositionWithIntegers(var1.xPosition, var1.yPosition, var1.zPosition);
               return var2;
            } else if (this.myEntity instanceof EntityXPOrb) {
               return new Packet26EntityExpOrb((EntityXPOrb)this.myEntity);
            } else {
               throw new IllegalArgumentException("Don't know how to add " + this.myEntity.getClass() + "!");
            }
         }
      } else if (this.myEntity instanceof EntityLiving) {
         this.lastHeadMotion = MathHelper.floor_float(this.myEntity.getRotationYawHead() * 256.0F / 360.0F);
         return new Packet24MobSpawn((EntityLiving)this.myEntity);
      } else {
         Minecraft.setErrorMessage("getPacketForThisEntity: entity not handled: " + this.myEntity);
         throw new IllegalArgumentException("Don't know how to add " + this.myEntity.getClass() + "!");
      }
   }

   public void removePlayerFromTracker(EntityPlayerMP par1EntityPlayerMP) {
      if (this.trackingPlayers.contains(par1EntityPlayerMP)) {
         this.trackingPlayers.remove(par1EntityPlayerMP);
         par1EntityPlayerMP.destroyedItemsNetCache.add(this.myEntity.entityId);
      }

   }
}
