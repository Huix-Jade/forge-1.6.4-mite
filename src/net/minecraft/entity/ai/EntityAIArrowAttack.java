package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIArrowAttack extends EntityAIBase {
   private final EntityLiving entityHost;
   private final IRangedAttackMob rangedAttackEntityHost;
   private EntityLivingBase attackTarget;
   private int rangedAttackTime;
   private double entityMoveSpeed;
   private int field_75318_f;
   private int field_96561_g;
   private int maxRangedAttackTime;
   private float field_96562_i;
   private float field_82642_h;

   public EntityAIArrowAttack(IRangedAttackMob par1IRangedAttackMob, double par2, int par4, float par5) {
      this(par1IRangedAttackMob, par2, par4, par4, par5);
   }

   public EntityAIArrowAttack(IRangedAttackMob par1IRangedAttackMob, double par2, int par4, int par5, float par6) {
      this.rangedAttackTime = -1;
      if (!(par1IRangedAttackMob instanceof EntityLivingBase)) {
         throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
      } else {
         this.rangedAttackEntityHost = par1IRangedAttackMob;
         this.entityHost = (EntityLiving)par1IRangedAttackMob;
         this.entityMoveSpeed = par2;
         this.field_96561_g = par4;
         this.maxRangedAttackTime = par5;
         this.field_96562_i = par6;
         this.field_82642_h = par6 * par6;
         this.field_82642_h *= 4.0F;
         this.setMutexBits(3);
      }
   }

   public boolean shouldExecute() {
      EntityLivingBase var1 = this.entityHost.getAttackTarget();
      if (var1 == null) {
         return false;
      } else {
         this.attackTarget = var1;
         return true;
      }
   }

   public boolean continueExecuting() {
      return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
   }

   public void resetTask() {
      this.attackTarget = null;
      this.field_75318_f = 0;
      this.rangedAttackTime = -1;
   }

   public void updateTask() {
      double var1 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
      World world = this.entityHost.worldObj;
      Raycast raycast = (new Raycast(world, this.entityHost.getEyePos(), this.attackTarget.getEyePos())).setForPiercingProjectile((Entity)null).performVsBlocks();
      if (raycast.hasBlockCollision()) {
         raycast.setLimit(this.attackTarget.getFootPosPlusFractionOfHeight(0.25F));
         raycast.performVsBlocks();
      }

      RaycastCollision rc = raycast.getBlockCollision();
      boolean var3 = rc == null;
      if (var3) {
         ++this.field_75318_f;
      } else {
         this.field_75318_f = 0;
      }

      if (var1 <= (double)this.field_82642_h && this.field_75318_f >= 20) {
         this.entityHost.getNavigator().clearPathEntity();
      } else {
         this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
      }

      this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
      if (--this.rangedAttackTime == 0) {
         if (var1 > (double)this.field_82642_h || !var3) {
            return;
         }

         float var4 = MathHelper.sqrt_double(var1) / this.field_96562_i;
         float var5 = var4;
         if (var4 < 0.1F) {
            var5 = 0.1F;
         }

         if (var5 > 1.0F) {
            var5 = 1.0F;
         }

         if (this.entityHost.worldObj.isAirOrPassableBlock(this.entityHost.getBlockPosX(), this.entityHost.getEyeBlockPosY(), this.entityHost.getBlockPosZ(), true)) {
            this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, var5);
         }

         this.rangedAttackTime = this.maxRangedAttackTime;
         if (this.entityHost.isFrenzied()) {
            this.rangedAttackTime = (int)((float)this.rangedAttackTime * 0.67F);
         }
      } else if (this.rangedAttackTime < 0) {
         this.rangedAttackTime = this.maxRangedAttackTime;
         if (this.entityHost.isFrenzied()) {
            this.rangedAttackTime = (int)((float)this.rangedAttackTime * 0.67F);
         }
      }

   }
}
