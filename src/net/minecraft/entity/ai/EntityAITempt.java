package net.minecraft.entity.ai;

import java.util.Iterator;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivestock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityAITempt extends EntityAIBase {
   private EntityCreature temptedEntity;
   private double field_75282_b;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double field_75278_f;
   private double field_75279_g;
   private EntityPlayer temptingPlayer;
   private int delayTemptCounter;
   private boolean isRunning;
   private int breedingFood;
   private boolean scaredByPlayerMovement;
   private boolean field_75286_m;

   public EntityAITempt(EntityCreature par1EntityCreature, double par2, int par4, boolean par5) {
      this.temptedEntity = par1EntityCreature;
      this.field_75282_b = par2;
      this.breedingFood = par4;
      this.scaredByPlayerMovement = par5;
      this.setMutexBits(3);
   }

   public boolean shouldExecute() {
      if (this.delayTemptCounter > 0) {
         --this.delayTemptCounter;
         return false;
      } else {
         if (this.temptedEntity instanceof EntityLivestock && this.temptedEntity.hasFullHealth()) {
            EntityLivestock livestock = (EntityLivestock)this.temptedEntity;
            if (!livestock.isWell() && !livestock.isHungry()) {
               return false;
            }
         }

         EntityPlayer closest_seen_player_holding_tempt_item = null;
         double distance_to_closest_seen_player_holding_tempt_item = 0.0;
         Iterator i = this.temptedEntity.worldObj.getAsWorldServer().playerEntities.iterator();

         while(true) {
            EntityPlayer player;
            double distance;
            do {
               ItemStack held_item_stack;
               do {
                  do {
                     while(true) {
                        do {
                           if (!i.hasNext()) {
                              this.temptingPlayer = closest_seen_player_holding_tempt_item;
                              return this.temptingPlayer != null;
                           }

                           player = (EntityPlayer)i.next();
                           distance = (double)this.temptedEntity.getDistanceToEntity(player);
                        } while(distance > 10.0);

                        if (this.temptedEntity.canSeeEntity(player, true)) {
                           held_item_stack = player.getHeldItemStack();
                           break;
                        }

                        if (player == this.temptingPlayer && this.temptedEntity.canPathTo(player.getBlockPosX(), player.getFootBlockPosY(), player.getBlockPosZ(), 16)) {
                           distance += 4.0;
                           if (closest_seen_player_holding_tempt_item == null || distance < distance_to_closest_seen_player_holding_tempt_item) {
                              closest_seen_player_holding_tempt_item = player;
                              distance_to_closest_seen_player_holding_tempt_item = distance;
                           }
                        }
                     }
                  } while(held_item_stack == null);
               } while(held_item_stack.itemID != this.breedingFood);

               if (player == this.temptingPlayer) {
                  distance -= 4.0;
               }
            } while(closest_seen_player_holding_tempt_item != null && !(distance < distance_to_closest_seen_player_holding_tempt_item));

            closest_seen_player_holding_tempt_item = player;
            distance_to_closest_seen_player_holding_tempt_item = distance;
         }
      }
   }

   public boolean continueExecuting() {
      if (this.scaredByPlayerMovement) {
         if (this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 36.0) {
            if (this.temptingPlayer.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002) {
               return false;
            }

            if (Math.abs((double)this.temptingPlayer.rotationPitch - this.field_75278_f) > 5.0 || Math.abs((double)this.temptingPlayer.rotationYaw - this.field_75279_g) > 5.0) {
               return false;
            }
         } else {
            this.targetX = this.temptingPlayer.posX;
            this.targetY = this.temptingPlayer.posY;
            this.targetZ = this.temptingPlayer.posZ;
         }

         this.field_75278_f = (double)this.temptingPlayer.rotationPitch;
         this.field_75279_g = (double)this.temptingPlayer.rotationYaw;
      }

      return this.shouldExecute();
   }

   public void startExecuting() {
      this.targetX = this.temptingPlayer.posX;
      this.targetY = this.temptingPlayer.posY;
      this.targetZ = this.temptingPlayer.posZ;
      this.isRunning = true;
      this.field_75286_m = this.temptedEntity.getNavigator().getAvoidsWater();
      this.temptedEntity.getNavigator().setAvoidsWater(false);
   }

   public void resetTask() {
      this.temptingPlayer = null;
      this.temptedEntity.getNavigator().clearPathEntity();
      this.delayTemptCounter = 100;
      this.isRunning = false;
      this.temptedEntity.getNavigator().setAvoidsWater(this.field_75286_m);
   }

   public void updateTask() {
      this.temptedEntity.getLookHelper().setLookPositionWithEntity(this.temptingPlayer, 30.0F, (float)this.temptedEntity.getVerticalFaceSpeed());
      if (this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 6.25) {
         this.temptedEntity.getNavigator().clearPathEntity();
      } else {
         this.temptedEntity.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.field_75282_b);
      }

   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
