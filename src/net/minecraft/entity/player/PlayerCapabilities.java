package net.minecraft.entity.player;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerCapabilities {
   public boolean disableDamage;
   public boolean isFlying;
   public boolean allowFlying;
   public boolean isCreativeMode;
   public boolean allowEdit = true;
   private float flySpeed = 0.05F;
   private float walkSpeed = 0.1F;
   public EntityPlayer player;

   public PlayerCapabilities(EntityPlayer player) {
      this.player = player;
   }

   public void writeCapabilitiesToNBT(NBTTagCompound par1NBTTagCompound) {
      NBTTagCompound var2 = new NBTTagCompound();
      var2.setBoolean("invulnerable", this.disableDamage);
      var2.setBoolean("flying", this.isFlying);
      var2.setBoolean("mayfly", this.allowFlying);
      var2.setBoolean("instabuild", this.isCreativeMode);
      var2.setBoolean("mayBuild", this.allowEdit);
      var2.setFloat("flySpeed", this.flySpeed);
      var2.setFloat("walkSpeed", this.walkSpeed);
      par1NBTTagCompound.setTag("abilities", var2);
   }

   public void readCapabilitiesFromNBT(NBTTagCompound par1NBTTagCompound) {
      if (par1NBTTagCompound.hasKey("abilities")) {
         NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("abilities");
         this.disableDamage = var2.getBoolean("invulnerable");
         this.isFlying = var2.getBoolean("flying");
         this.allowFlying = var2.getBoolean("mayfly");
         this.isCreativeMode = var2.getBoolean("instabuild");
         if (var2.hasKey("flySpeed")) {
            this.flySpeed = var2.getFloat("flySpeed");
            this.walkSpeed = var2.getFloat("walkSpeed");
         }

         if (var2.hasKey("mayBuild")) {
            this.allowEdit = var2.getBoolean("mayBuild");
         }
      }

   }

   public float getFlySpeed() {
      return this.flySpeed;
   }

   public void setFlySpeed(float par1) {
      this.flySpeed = par1;
   }

   public float getWalkSpeed() {
      float speed_boost_or_slow_down_factor = this.player.getSpeedBoostOrSlowDownFactor();
      return (this.player.hasFoodEnergy() ? this.walkSpeed : this.walkSpeed * 0.75F) * EnchantmentHelper.getSpeedModifier(this.player) * speed_boost_or_slow_down_factor;
   }

   public void setPlayerWalkSpeed(float par1) {
      this.walkSpeed = par1;
   }
}
