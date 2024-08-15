package net.minecraft.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.RandomItemListEntry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.raycast.RaycastPolicies;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityBoneLord extends EntitySkeleton {
   int num_troops_summoned;

   public EntityBoneLord(World world) {
      super(world);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.followRange, 40.0);
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.25999999046325684);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 5.0);
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, 20.0);
   }

   public void addRandomWeapon() {
      List items = new ArrayList();
      items.add(new RandomItemListEntry(Item.swordRustedIron, 2));
      if (this.worldObj.getDayOfWorld() >= 10 && !Minecraft.isInTournamentMode()) {
         items.add(new RandomItemListEntry(Item.battleAxeRustedIron, 1));
      }

      if (this.worldObj.getDayOfWorld() >= 20 && !Minecraft.isInTournamentMode()) {
         items.add(new RandomItemListEntry(Item.warHammerRustedIron, 1));
      }

      RandomItemListEntry entry = (RandomItemListEntry)WeightedRandom.getRandomItem(this.rand, (Collection)items);
      this.setHeldItemStack((new ItemStack(entry.item)).randomizeForMob(this, true));
   }

   protected void addRandomEquipment() {
      this.addRandomWeapon();
      this.setBoots((new ItemStack(Item.bootsRustedIron)).randomizeForMob(this, true));
      this.setLeggings((new ItemStack(Item.legsRustedIron)).randomizeForMob(this, true));
      this.setCuirass((new ItemStack(Item.plateRustedIron)).randomizeForMob(this, true));
      this.setHelmet((new ItemStack(Item.helmetRustedIron)).randomizeForMob(this, true));
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.num_troops_summoned = par1NBTTagCompound.getByte("num_troops_summoned");
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      if (this.num_troops_summoned > 0) {
         par1NBTTagCompound.setByte("num_troops_summoned", (byte)this.num_troops_summoned);
      }

   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 3;
   }

   public int getMaxSpawnedInChunk() {
      return 1;
   }

   public Class getTroopClass() {
      return this.isAncientBoneLord() ? EntityLongdead.class : EntitySkeleton.class;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onServer()) {
         if (!this.isNoDespawnRequired() && this.getTarget() instanceof EntityPlayer) {
            this.func_110163_bv();
         }

         if (this.getTicksExistedWithOffset() % 20 == 0) {
            EntityLivingBase target = this.getTarget();
            if (target != null) {
               if (target.isDead || target.getHealth() <= 0.0F || (double)this.getDistanceToEntity(target) > 16.0 || !this.canSeeEntity(target)) {
                  target = null;
               }

               if (this.num_troops_summoned < 6 && target instanceof EntityPlayer && this.rand.nextInt(8) < 7 - this.num_troops_summoned) {
                  this.num_troops_summoned += this.trySummonTroop(target);
                  if (this.num_troops_summoned < 6 && this.rand.nextBoolean()) {
                     this.num_troops_summoned += this.trySummonTroop(target);
                  }
               }
            }

            List nearby_skeletons = this.worldObj.getEntitiesWithinAABB(EntitySkeleton.class, this.boundingBox.expand(16.0, 8.0, 16.0));
            Class troop_class = this.getTroopClass();
            Iterator i = nearby_skeletons.iterator();

            while(true) {
               EntitySkeleton entity_skeleton;
               do {
                  do {
                     do {
                        do {
                           do {
                              if (!i.hasNext()) {
                                 return;
                              }

                              entity_skeleton = (EntitySkeleton)i.next();
                           } while(entity_skeleton != this && !entity_skeleton.canSeeEntity(this, true));

                           if (entity_skeleton.getHealth() < entity_skeleton.getMaxHealth()) {
                              entity_skeleton.heal(1.0F);
                           }
                        } while(entity_skeleton.isBoneLord());

                        entity_skeleton.setFrenziedByBoneLord(target);
                     } while(this.num_troops_summoned <= 0);
                  } while(entity_skeleton.getClass() != troop_class);
               } while((entity_skeleton.getAttackTarget() != null || !(entity_skeleton.getHealthFraction() >= 1.0F) || !(this.rand.nextFloat() < 0.05F)) && entity_skeleton.despawn_counter < 0);

               int despawn_counter = entity_skeleton.despawn_counter;
               entity_skeleton.despawn_counter = Integer.MAX_VALUE;
               if (entity_skeleton.canDespawn()) {
                  entity_skeleton.tryDespawnEntity();
                  if (entity_skeleton.isDead) {
                     --this.num_troops_summoned;
                  } else {
                     entity_skeleton.despawn_counter = despawn_counter;
                  }
               }
            }
         }
      }

   }

   private int trySummonTroop(EntityLivingBase target) {
      int bone_lord_x = this.getBlockPosX();
      int bone_lord_y = this.getFootBlockPosY();
      int bone_lord_z = this.getBlockPosZ();
      int target_x = target.getBlockPosX();
      int target_y = target.getFootBlockPosY();
      int target_z = target.getBlockPosZ();
      Vec3 bone_lord_leg_pos = this.getFootPosPlusFractionOfHeight(0.25F);
      Vec3 bone_lord_head_pos = this.getFootPosPlusFractionOfHeight(0.75F);
      Class troop_class = this.getTroopClass();
      int max_num_attempts = 48 - this.num_troops_summoned * 8;
      int attempts = 0;

      EntitySkeleton skeleton;
      while(true) {
         if (attempts >= max_num_attempts) {
            return 0;
         }

         skeleton = (EntitySkeleton)((WorldServer)this.worldObj).tryCreateNewLivingEntityCloseTo(bone_lord_x, bone_lord_y, bone_lord_z, 2, 12, troop_class, EnumCreatureType.monster);
         if (skeleton != null) {
            int skeleton_type = skeleton.getRandomSkeletonType(this.worldObj);
            skeleton.forced_skeleton_type = skeleton_type;
            if (this.worldObj.getClosestVulnerablePlayer(skeleton, 4.0, false) == null && (skeleton_type != 0 || skeleton.canSeeEntity(target, true))) {
               double distance_sq_to_summoning_bone_lord = skeleton.getDistanceSqToEntity(this);
               Iterator i = this.worldObj.getAsWorldServer().playerEntities.iterator();

               while(i.hasNext()) {
                  EntityPlayer entity_player = (EntityPlayer)i.next();
                  if (!(entity_player.getHealth() <= 0.0F) && skeleton.getDistanceSqToEntity(entity_player) < distance_sq_to_summoning_bone_lord) {
                     skeleton = null;
                     break;
                  }
               }

               if (skeleton != null) {
                  label98: {
                     PathNavigate navigator = skeleton.getNavigator();
                     Vec3 skeleton_eye_pos = skeleton.getEyePos();
                     boolean can_raycast_to_summoner = skeleton_eye_pos.squareDistanceTo(bone_lord_leg_pos) < 256.0 && this.worldObj.checkForNoBlockCollision(skeleton_eye_pos, bone_lord_leg_pos, RaycastPolicies.for_piercing_projectile);
                     World var10000;
                     PathEntity path;
                     PathPoint final_point;
                     if (!can_raycast_to_summoner) {
                        can_raycast_to_summoner = skeleton_eye_pos.squareDistanceTo(bone_lord_head_pos) < 256.0 && this.worldObj.checkForNoBlockCollision(skeleton_eye_pos, bone_lord_head_pos, RaycastPolicies.for_piercing_projectile);
                        if (!can_raycast_to_summoner) {
                           path = this.worldObj.getEntityPathToXYZ(skeleton, bone_lord_x, bone_lord_y, bone_lord_z, 16.0F, navigator.canPassOpenWoodenDoors, false, navigator.avoidsWater, navigator.canSwim);
                           if (path == null) {
                              break label98;
                           }

                           final_point = path.getFinalPathPoint();
                           var10000 = this.worldObj;
                           if (World.getDistanceSqFromDeltas((float)(final_point.xCoord - bone_lord_x), (float)(final_point.yCoord - bone_lord_y), (float)(final_point.zCoord - bone_lord_z)) > 2.0) {
                              break label98;
                           }
                        }
                     }

                     if (skeleton_type == 0 || skeleton_type != 2) {
                        break;
                     }

                     path = this.worldObj.getEntityPathToXYZ(skeleton, target_x, target_y, target_z, 16.0F, navigator.canPassOpenWoodenDoors, false, navigator.avoidsWater, navigator.canSwim);
                     if (path != null) {
                        final_point = path.getFinalPathPoint();
                        var10000 = this.worldObj;
                        if (!(World.getDistanceSqFromDeltas((float)(final_point.xCoord - target_x), (float)(final_point.yCoord - target_y), (float)(final_point.zCoord - target_z)) > 2.0)) {
                           break;
                        }
                     }
                  }
               }
            }
         }

         ++attempts;
      }

      skeleton.refreshDespawnCounter(-9600);
      this.worldObj.spawnEntityInWorld(skeleton);
      skeleton.onSpawnWithEgg((EntityLivingData)null);
      skeleton.setAttackTarget(target);
      skeleton.entityFX(EnumEntityFX.summoned);
      return 1;
   }
}
