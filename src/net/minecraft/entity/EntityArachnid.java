package net.minecraft.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Curse;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityArachnid extends EntityMob {
   int num_webs;

   public EntityArachnid(World par1World, float scaling) {
      super(par1World);
      this.setSize(1.4F * scaling, 0.9F * scaling);
      if (par1World != null && !par1World.isRemote && !(this instanceof EntityPhaseSpider)) {
         this.num_webs = this.rand.nextInt(4);
         if (this.num_webs > 0 && !(this instanceof EntityCaveSpider) && !(this instanceof EntityDemonSpider)) {
            --this.num_webs;
         }
      }

   }

   public boolean canClimbWalls() {
      return true;
   }

   protected void entityInit() {
      super.entityInit();
      if (this.canClimbWalls()) {
         this.dataWatcher.addObject(16, new Byte((byte)0));
      }

   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setByte("num_webs", (byte)this.num_webs);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.num_webs = par1NBTTagCompound.getByte("num_webs");
   }

   public void checkSwitchingToPeaceful() {
      if (this.worldObj.isRemote) {
         Minecraft.setErrorMessage("checkSwitchingToPeacful: only meant to be called on server");
      } else {
         if (this.getEntityToAttack() instanceof EntityPlayer && this.peacefulDuringDay() && this.getBrightness(1.0F) > 0.5F && !(this.getLastHarmingEntity() instanceof EntityPlayer) && this.rand.nextInt(100) == 0 && this.isOutdoors()) {
            this.setEntityToAttack((Entity)null);
         }

      }
   }

   public int getTicksBetweenWebThrows() {
      return !(this instanceof EntityCaveSpider) && !(this instanceof EntityDemonSpider) ? 500 : 200;
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote && this.canClimbWalls()) {
         this.setBesideClimbableBlock(this.isCollidedHorizontally);
      }

      if (!this.worldObj.isRemote) {
         this.checkSwitchingToPeaceful();
         if (this.num_webs > 0 && this.getTicksExistedWithOffset() % this.getTicksBetweenWebThrows() == 0) {
            Entity target = this.getEntityToAttack();
            if (target instanceof EntityLivingBase) {
               double distance = (double)this.getDistanceToEntity(target);
               if (distance <= 8.0) {
                  EntityLivingBase elb_target = (EntityLivingBase)target;
                  Raycast raycast = (new Raycast(this.worldObj, this.getEyePos(), elb_target.getEyePos())).setForThrownWeb((Entity)null).performVsBlocks();
                  if (raycast.hasBlockCollision()) {
                     raycast.setLimit(elb_target.getFootPosPlusFractionOfHeight(0.25F));
                     raycast.performVsBlocks();
                  }

                  RaycastCollision rc = raycast.getBlockCollision();
                  if (rc == null) {
                     raycast.setOriginator(this).performVsEntities();
                     rc = raycast.getNearestEntityCollision();
                     if (rc.getEntityHit() == target) {
                        this.attackEntityWithRangedAttack((EntityLivingBase)target, 1.0F);
                     }
                  }
               }
            }
         }
      }

   }

   public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2) {
      EntityWeb var3 = new EntityWeb(this.worldObj, this);
      int lead = 10;
      double var4 = par1EntityLivingBase.getPredictedPosX((float)lead) - this.posX;
      double var6 = par1EntityLivingBase.posY + (double)par1EntityLivingBase.getEyeHeight() - 1.100000023841858 - var3.posY;
      double var8 = par1EntityLivingBase.getPredictedPosZ((float)lead) - this.posZ;
      float var10 = MathHelper.sqrt_double(var4 * var4 + var8 * var8) * 0.2F;
      var3.setThrowableHeading(var4, var6 + (double)var10, var8, 0.8F, 0.0F);
      this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.worldObj.spawnEntityInWorld(var3);
      if (this instanceof EntityDemonSpider || this.isBurning()) {
         var3.setFire(10);
      }

      --this.num_webs;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, 12.0);
      this.setEntityAttribute(SharedMonsterAttributes.followRange, 28.0);
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 1.0);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 4.0);
   }

   protected String getLivingSound() {
      return "mob.spider.say";
   }

   protected String getHurtSound() {
      return "mob.spider.say";
   }

   protected String getDeathSound() {
      return "mob.spider.death";
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      this.makeSound("mob.spider.step", 0.15F, 1.0F);
   }

   public boolean peacefulDuringDay() {
      return true;
   }

   protected EntityPlayer findPlayerToAttack(float max_distance) {
      return this.peacefulDuringDay() && this.getBrightness(1.0F) > 0.5F && this.isOutdoors() ? null : super.findPlayerToAttack(max_distance);
   }

   protected Entity findNonPlayerToAttack(float max_distance) {
      if (this.peacefulDuringDay() && this.getBrightness(1.0F) > 0.5F && this.isOutdoors()) {
         return null;
      } else {
         Entity target = this.worldObj.findNearestSeenEntityWithinAABB(EntityChicken.class, this.boundingBox.expand((double)max_distance, (double)(max_distance / 4.0F), (double)max_distance), this, this.getEntitySenses());
         return target;
      }
   }

   public boolean preysUpon(Entity entity) {
      return entity instanceof EntityChicken;
   }

   public boolean canJump() {
      return true;
   }

   protected void attackEntity(Entity par1Entity, float par2) {
      if (par2 > 2.0F && par2 < 6.0F && this.canJump() && this.rand.nextInt(10) == 0) {
         if (this.onGround) {
            double var4 = par1Entity.posX - this.posX;
            double var6 = par1Entity.posZ - this.posZ;
            float var8 = MathHelper.sqrt_double(var4 * var4 + var6 * var6);
            this.motionX = var4 / (double)var8 * 0.5 * 0.800000011920929 + this.motionX * 0.20000000298023224;
            this.motionZ = var6 / (double)var8 * 0.5 * 0.800000011920929 + this.motionZ * 0.20000000298023224;
            this.motionY = 0.4000000059604645;
            this.rotationYaw = (float)MathHelper.getYawInDegrees(this.posX, this.posZ, par1Entity.posX, par1Entity.posZ);
            PathEntity path = this.worldObj.getPathEntityToEntity(this, par1Entity, 8.0F, true, false, this.avoidsPathingThroughWater(), true);
            if (path != null) {
               this.setPathToEntity(path);
            }
         }
      } else {
         super.attackEntity(par1Entity, par2);
      }

   }

   protected int getDropItemId() {
      return Item.silk.itemID;
   }

   protected final void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      if (recently_hit_by_player) {
         while(this.num_webs-- > 0) {
            this.dropItemStack(new ItemStack(Item.silk.itemID));
         }
      }

      if (recently_hit_by_player && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + damage_source.getButcheringModifier()) > 0)) {
         this.dropItem(Item.spiderEye.itemID, 1);
      }

   }

   public boolean isOnLadder() {
      return this.isBesideClimbableBlock();
   }

   public void setInWeb() {
   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.ARTHROPOD;
   }

   public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
      return par1PotionEffect.getPotionID() == Potion.poison.id ? false : super.isPotionApplicable(par1PotionEffect);
   }

   public boolean isBesideClimbableBlock() {
      return this.canClimbWalls() && (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean par1) {
      if (this.canClimbWalls()) {
         byte var2 = this.dataWatcher.getWatchableObjectByte(16);
         if (par1) {
            var2 = (byte)(var2 | 1);
         } else {
            var2 &= -2;
         }

         this.dataWatcher.updateObject(16, var2);
      }
   }

   public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData) {
      return super.onSpawnWithEgg(par1EntityLivingData);
   }

   public boolean drawBackFaces() {
      return false;
   }

   public boolean canBeAttackedBy(EntityLivingBase attacker) {
      return this.rand.nextInt(4) > 0 && attacker.hasCurse(Curse.fear_of_spiders, true) ? false : super.canBeAttackedBy(attacker);
   }

   public boolean requiresLineOfSightToTargets() {
      return false;
   }

   public final boolean isBlackWidowSpider() {
      return this.getClass() == EntityBlackWidowSpider.class;
   }
}
