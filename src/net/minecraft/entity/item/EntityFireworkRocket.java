package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkRocket extends Entity {
   private int fireworkAge;
   private int lifetime;

   public EntityFireworkRocket(World par1World) {
      super(par1World);
      this.setSize(0.25F, 0.25F);
   }

   protected void entityInit() {
      this.dataWatcher.addObjectByDataType(8, 5);
   }

   public boolean isInRangeToRenderDist(double par1) {
      return par1 < 4096.0;
   }

   public EntityFireworkRocket(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack) {
      super(par1World);
      this.fireworkAge = 0;
      this.setSize(0.25F, 0.25F);
      this.setPosition(par2, par4, par6);
      this.yOffset = 0.0F;
      int var9 = 1;
      if (par8ItemStack != null && par8ItemStack.hasTagCompound()) {
         this.dataWatcher.updateObject(8, par8ItemStack);
         NBTTagCompound var10 = par8ItemStack.getTagCompound();
         NBTTagCompound var11 = var10.getCompoundTag("Fireworks");
         if (var11 != null) {
            var9 += var11.getByte("Flight");
         }
      }

      this.motionX = this.rand.nextGaussian() * 0.001;
      this.motionZ = this.rand.nextGaussian() * 0.001;
      this.motionY = 0.05;
      this.lifetime = 10 * var9 + this.rand.nextInt(6) + this.rand.nextInt(7);
   }

   public void setVelocity(double par1, double par3, double par5) {
      this.motionX = par1;
      this.motionY = par3;
      this.motionZ = par5;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float var7 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
         this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0 / Math.PI);
         this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(par3, (double)var7) * 180.0 / Math.PI);
      }

   }

   public void onUpdate() {
      this.lastTickPosX = this.posX;
      this.lastTickPosY = this.posY;
      this.lastTickPosZ = this.posZ;
      super.onUpdate();
      this.motionX *= 1.15;
      this.motionZ *= 1.15;
      this.motionY += 0.04;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      float var1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);

      for(this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var1) * 180.0 / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
      }

      while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
      this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
      if (this.fireworkAge == 0) {
         this.worldObj.playSoundAtEntity(this, "fireworks.launch", 3.0F, 1.0F);
      }

      ++this.fireworkAge;
      if (this.worldObj.isRemote && this.fireworkAge % 2 < 2) {
         this.worldObj.spawnParticle(EnumParticle.fireworkSpark, this.posX, this.posY - 0.3, this.posZ, this.rand.nextGaussian() * 0.05, -this.motionY * 0.5, this.rand.nextGaussian() * 0.05);
      }

      if (!this.worldObj.isRemote && this.fireworkAge > this.lifetime) {
         this.worldObj.setEntityState(this, EnumEntityState.firework_ended);
         this.setDead();
      }

   }

   public void handleHealthUpdate(EnumEntityState par1) {
      if (par1 == EnumEntityState.firework_ended && this.worldObj.isRemote) {
         ItemStack var2 = this.dataWatcher.getWatchableObjectItemStack(8);
         NBTTagCompound var3 = null;
         if (var2 != null && var2.hasTagCompound()) {
            var3 = var2.getTagCompound().getCompoundTag("Fireworks");
         }

         this.worldObj.func_92088_a(this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ, var3);
      }

      super.handleHealthUpdate(par1);
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setInteger("Life", this.fireworkAge);
      par1NBTTagCompound.setInteger("LifeTime", this.lifetime);
      ItemStack var2 = this.dataWatcher.getWatchableObjectItemStack(8);
      if (var2 != null) {
         NBTTagCompound var3 = new NBTTagCompound();
         var2.writeToNBT(var3);
         par1NBTTagCompound.setCompoundTag("FireworksItem", var3);
      }

   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      this.fireworkAge = par1NBTTagCompound.getInteger("Life");
      this.lifetime = par1NBTTagCompound.getInteger("LifeTime");
      NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("FireworksItem");
      if (var2 != null) {
         ItemStack var3 = ItemStack.loadItemStackFromNBT(var2);
         if (var3 != null) {
            this.dataWatcher.updateObject(8, var3);
         }
      }

   }

   public float getShadowSize() {
      return 0.0F;
   }

   public float getBrightness(float par1) {
      return super.getBrightness(par1);
   }

   public int getBrightnessForRender(float par1) {
      return super.getBrightnessForRender(par1);
   }

   public boolean canAttackWithItem() {
      return false;
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
