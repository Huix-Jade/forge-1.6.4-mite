package net.minecraft.entity.ai;

import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityInfernalCreeper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.World;

public class EntityAICreeperSwell extends EntityAIBase {
   EntityCreeper swellingCreeper;
   EntityLivingBase creeperAttackTarget;

   public EntityAICreeperSwell(EntityCreeper par1EntityCreeper) {
      this.swellingCreeper = par1EntityCreeper;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (this.swellingCreeper.getCreeperState() > 0) {
         return true;
      } else if (this.swellingCreeper.recently_took_damage_from_conspicuous_cactus > 0 && this.swellingCreeper.isNearToBlock(Block.cactus, 1, 1)) {
         return true;
      } else {
         EntityLivingBase target = this.swellingCreeper.getAttackTarget();
         if (target == null) {
            return false;
         } else {
            float trigger_distance_sq = this.swellingCreeper.getNavigator().noPath() ? 16.0F : (this.swellingCreeper.getHealthFraction() < 1.0F ? 9.0F : 4.5F);
            if (this.swellingCreeper instanceof EntityInfernalCreeper) {
               trigger_distance_sq *= 2.0F;
            }

            return this.swellingCreeper.getDistanceSqToEntity(target) < (double)trigger_distance_sq;
         }
      }
   }

   public void startExecuting() {
      this.swellingCreeper.getNavigator().clearPathEntity();
      this.creeperAttackTarget = this.swellingCreeper.getAttackTarget();
   }

   public void resetTask() {
      this.creeperAttackTarget = null;
   }

   public void updateTask() {
      if (this.swellingCreeper.recently_took_damage_from_conspicuous_cactus > 0 && this.swellingCreeper.isNearToBlock(Block.cactus, 1, 1)) {
         this.swellingCreeper.setCreeperState(1);
      } else if (this.creeperAttackTarget == null) {
         this.swellingCreeper.setCreeperState(-1);
      } else {
         float health_fraction_for_swelling = MathHelper.clamp_float(this.swellingCreeper.getHealthFraction(), 0.4F, 1.0F);
         float distance_limit_sq = (this.swellingCreeper instanceof EntityInfernalCreeper ? 36.0F : 16.0F) / health_fraction_for_swelling;
         World world = this.swellingCreeper.worldObj;
         Vec3Pool vec3_pool = world.getWorldVec3Pool();
         Iterator i = world.playerEntities.iterator();

         EntityPlayerMP player;
         double creeper_knee_pos_y;
         double creeper_eye_pos_y;
         do {
            do {
               do {
                  if (!i.hasNext()) {
                     this.swellingCreeper.setCreeperState(-1);
                     return;
                  }

                  player = (EntityPlayerMP)i.next();
               } while(!(player.getHealth() > 0.0F));
            } while(!(this.swellingCreeper.getDistanceSqToEntity(player) <= (double)distance_limit_sq));

            creeper_knee_pos_y = this.swellingCreeper.getFootPosY() + (double)(this.swellingCreeper.height / 4.0F);
            creeper_eye_pos_y = this.swellingCreeper.getEyePosY();
         } while(!player.canEntityBeSeenFrom(this.swellingCreeper.posX, creeper_knee_pos_y, this.swellingCreeper.posZ, (double)distance_limit_sq) && !player.canEntityBeSeenFrom(this.swellingCreeper.posX, creeper_eye_pos_y, this.swellingCreeper.posZ, (double)distance_limit_sq));

         this.swellingCreeper.setCreeperState(1);
      }
   }
}
