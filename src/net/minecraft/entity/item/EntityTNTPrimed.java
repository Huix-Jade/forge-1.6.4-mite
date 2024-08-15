package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public class EntityTNTPrimed extends Entity {
   public int fuse;
   private EntityLivingBase tntPlacedBy;

   public EntityTNTPrimed(World par1World) {
      super(par1World);
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.98F);
      this.yOffset = this.height / 2.0F;
   }

   public EntityTNTPrimed(World par1World, double par2, double par4, double par6, EntityLivingBase par8EntityLivingBase) {
      this(par1World);
      this.setPosition(par2, par4, par6);
      float var9 = (float)(Math.random() * Math.PI * 2.0);
      this.motionX = (double)(-((float)Math.sin((double)var9)) * 0.02F);
      this.motionY = 0.20000000298023224;
      this.motionZ = (double)(-((float)Math.cos((double)var9)) * 0.02F);
      this.motionX = this.motionZ = 0.0;
      this.fuse = 80;
      this.prevPosX = par2;
      this.prevPosY = par4;
      this.prevPosZ = par6;
      this.tntPlacedBy = par8EntityLivingBase;
   }

   protected void entityInit() {
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= 0.03999999910593033;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9800000190734863;
      this.motionY *= 0.9800000190734863;
      this.motionZ *= 0.9800000190734863;
      if (this.onGround) {
         this.motionX *= 0.699999988079071;
         this.motionZ *= 0.699999988079071;
         this.motionY *= -0.5;
      }

      if (this.fuse-- <= 0) {
         this.setDead();
         if (!this.worldObj.isRemote) {
            this.explode();
         }
      } else {
         this.worldObj.spawnParticle(EnumParticle.smoke, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
      }

   }

   private void explode() {
      float var1 = 4.0F;
      var1 = 2.0F;
      this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, var1, var1, true);
      this.entityFX(EnumEntityFX.frags);
   }

   protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setByte("Fuse", (byte)this.fuse);
   }

   protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      this.fuse = par1NBTTagCompound.getByte("Fuse");
   }

   public float getShadowSize() {
      return 0.0F;
   }

   public EntityLivingBase getTntPlacedBy() {
      return this.tntPlacedBy;
   }

   public int getFragParticle() {
      return Block.tnt.blockID;
   }

   public boolean canCatchFire() {
      return true;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }
}
