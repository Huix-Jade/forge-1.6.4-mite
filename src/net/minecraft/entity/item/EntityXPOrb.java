package net.minecraft.entity.item;

import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastPolicies;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityXPOrb extends Entity {
   public int xpColor;
   public int xpOrbAge;
   public int field_70532_c;
   private int xpOrbHealth = 5;
   private int xpValue;
   private EntityPlayer closestPlayer;
   private int xpTargetColor;
   public String player_this_belongs_to;
   public boolean created_by_bottle_of_enchanting;

   public EntityXPOrb(World par1World, double par2, double par4, double par6, int par8) {
      super(par1World);
      this.setSize(0.25F, 0.25F);
      this.yOffset = this.height / 2.0F;
      this.setPosition(par2, par4, par6);
      this.xpValue = par8;
      if (!this.worldObj.isRemote) {
         this.motionX = (double)((float)(Math.random() * 0.20000000298023224 - 0.10000000149011612) * 2.0F);
         this.motionY = (double)((float)(Math.random() * 0.2) * 2.0F);
         this.motionZ = (double)((float)(Math.random() * 0.20000000298023224 - 0.10000000149011612) * 2.0F);
      }

   }

   public void setPlayerThisBelongsTo(String player_this_belongs_to) {
      this.player_this_belongs_to = "".equals(player_this_belongs_to) ? null : player_this_belongs_to;
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public EntityXPOrb(World par1World) {
      super(par1World);
      this.setSize(0.25F, 0.25F);
      this.yOffset = this.height / 2.0F;
   }

   protected void entityInit() {
   }

   public int getBrightnessForRender(float par1) {
      float var2 = 0.5F;
      if (var2 < 0.0F) {
         var2 = 0.0F;
      }

      if (var2 > 1.0F) {
         var2 = 1.0F;
      }

      int var3 = super.getBrightnessForRender(par1);
      int var4 = var3 & 255;
      int var5 = var3 >> 16 & 255;
      var4 += (int)(var2 * 15.0F * 16.0F);
      if (var4 > 240) {
         var4 = 240;
      }

      return var4 | var5 << 16;
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.field_70532_c > 0) {
         --this.field_70532_c;
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= 0.029999999329447746;
      if (this.worldObj.isFullLavaBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), true)) {
         this.motionY = 0.0;
         this.motionX *= 0.949999988079071;
         this.motionZ *= 0.949999988079071;
      }

      this.pushOutOfBlocks();
      double var1 = 8.0;
      if (this.xpTargetColor < this.xpColor - 20 + this.entityId % 100) {
         if (this.player_this_belongs_to != null) {
            Iterator i = this.worldObj.playerEntities.iterator();

            while(i.hasNext()) {
               EntityPlayer player = (EntityPlayer)i.next();
               if (this.player_this_belongs_to.equals(player.username)) {
                  this.closestPlayer = player;
                  break;
               }
            }
         } else if (this.closestPlayer == null || this.closestPlayer.getDistanceSqToEntity(this) > var1 * var1) {
            this.closestPlayer = this.worldObj.getClosestPlayerToEntity(this, var1, true);
         }

         this.xpTargetColor = this.xpColor;
      }

      if (this.closestPlayer != null) {
         double var3 = (this.closestPlayer.posX - this.posX) / var1;
         double var5 = (this.closestPlayer.posY + (double)this.closestPlayer.getEyeHeight() - this.posY) / var1;
         double var7 = (this.closestPlayer.posZ - this.posZ) / var1;
         double var9 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
         double var11 = 1.0 - var9;
         if (var11 > 0.0) {
            var11 *= var11;
            this.motionX += var3 / var9 * var11 * 0.1;
            this.motionY += var5 / var9 * var11 * 0.1;
            this.motionZ += var7 / var9 * var11 * 0.1;
         }
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      float var13 = 0.98F;
      if (this.onGround) {
         var13 = 0.58800006F;
         int var4 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
         if (var4 > 0) {
            var13 = Block.blocksList[var4].slipperiness * 0.98F;
         }
      }

      this.motionX *= (double)var13;
      this.motionY *= 0.9800000190734863;
      this.motionZ *= (double)var13;
      if (this.onGround) {
         this.motionY *= -0.8999999761581421;
      }

      ++this.xpColor;
      ++this.xpOrbAge;
      if (this.xpOrbAge >= 6000) {
         this.setDead();
      }

   }

   public boolean handleWaterMovement() {
      return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
   }

   protected void dealFireDamage(int par1) {
      this.attackEntityFrom(new Damage(DamageSource.inFire, (float)par1));
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result != null && !result.entityWasDestroyed()) {
         this.setBeenAttacked();
         result.startTrackingHealth((float)this.xpOrbHealth);
         this.xpOrbHealth = (int)((float)this.xpOrbHealth - damage.getAmount());
         result.finishTrackingHealth((float)this.xpOrbHealth);
         if (this.xpOrbHealth <= 0) {
            this.setDead();
            result.setEntityWasDestroyed();
         }

         return result;
      } else {
         return result;
      }
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setShort("Health", (short)((byte)this.xpOrbHealth));
      par1NBTTagCompound.setShort("Age", (short)this.xpOrbAge);
      par1NBTTagCompound.setShort("Value", (short)this.xpValue);
      if (this.player_this_belongs_to != null) {
         par1NBTTagCompound.setString("player_this_belongs_to", this.player_this_belongs_to);
      }

      if (this.created_by_bottle_of_enchanting) {
         par1NBTTagCompound.setBoolean("created_by_bottle_of_enchanting", true);
      }

   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      this.xpOrbHealth = par1NBTTagCompound.getShort("Health") & 255;
      this.xpOrbAge = par1NBTTagCompound.getShort("Age");
      this.xpValue = par1NBTTagCompound.getShort("Value");
      if (par1NBTTagCompound.hasKey("player_this_belongs_to")) {
         this.player_this_belongs_to = par1NBTTagCompound.getString("player_this_belongs_to");
      }

      this.created_by_bottle_of_enchanting = par1NBTTagCompound.getBoolean("created_by_bottle_of_enchanting");
   }

   public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
      if (!this.worldObj.isRemote) {
         if (!this.canRaycastToEntity(par1EntityPlayer)) {
            return;
         }

         if (this.field_70532_c == 0 && par1EntityPlayer.xpCooldown == 0) {
            if (this.player_this_belongs_to != null && !this.player_this_belongs_to.equals(par1EntityPlayer.username)) {
               return;
            }

            par1EntityPlayer.xpCooldown = 2;
            par1EntityPlayer.onItemPickup(this, 1);
            par1EntityPlayer.addExperience(this.xpValue, this.created_by_bottle_of_enchanting);
            this.setDead();
         }
      }

   }

   public int getXpValue() {
      return this.xpValue;
   }

   public int getTextureByXP() {
      return this.xpValue >= 2477 ? 10 : (this.xpValue >= 1237 ? 9 : (this.xpValue >= 617 ? 8 : (this.xpValue >= 307 ? 7 : (this.xpValue >= 149 ? 6 : (this.xpValue >= 73 ? 5 : (this.xpValue >= 37 ? 4 : (this.xpValue >= 17 ? 3 : (this.xpValue >= 7 ? 2 : (this.xpValue >= 3 ? 1 : 0)))))))));
   }

   public static int getXPSplit(int par0) {
      return par0 >= 2477 ? 2477 : (par0 >= 1237 ? 1237 : (par0 >= 617 ? 617 : (par0 >= 307 ? 307 : (par0 >= 149 ? 149 : (par0 >= 73 ? 73 : (par0 >= 37 ? 37 : (par0 >= 17 ? 17 : (par0 >= 7 ? 7 : (par0 >= 3 ? 3 : 1)))))))));
   }

   public boolean canAttackWithItem() {
      return false;
   }

   public boolean canCatchFire() {
      return false;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }

   public EntityXPOrb setCreatedByBottleOfEnchanting() {
      this.created_by_bottle_of_enchanting = true;
      return this;
   }

   public boolean canRaycastToEntity(EntityLivingBase elb) {
      Raycast raycast = (new Raycast(this.worldObj, this.getCenterPoint())).setPolicies(RaycastPolicies.for_physical_reach_narrow);
      if (raycast.checkForNoBlockCollision(elb.getFootPosPlusFractionOfHeight(0.25F))) {
         return true;
      } else {
         return raycast.checkForNoBlockCollision(elb.getFootPosPlusFractionOfHeight(0.5F)) ? true : raycast.checkForNoBlockCollision(elb.getFootPosPlusFractionOfHeight(0.75F));
      }
   }
}
