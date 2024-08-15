package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityAIBeg extends EntityAIBase {
   private EntityWolf theWolf;
   private EntityPlayer thePlayer;
   private World worldObject;
   private float minPlayerDistance;
   private int field_75384_e;

   public EntityAIBeg(EntityWolf par1EntityWolf, float par2) {
      this.theWolf = par1EntityWolf;
      this.worldObject = par1EntityWolf.worldObj;
      this.minPlayerDistance = par2;
      this.setMutexBits(2);
   }

   public boolean shouldExecute() {
      if (!this.theWolf.isAttacking() && !this.theWolf.isHostileToPlayers()) {
         this.thePlayer = this.worldObject.getClosestPlayerToEntity(this.theWolf, (double)this.minPlayerDistance, true);
         return this.thePlayer == null ? false : this.hasPlayerGotBoneInHand(this.thePlayer);
      } else {
         return false;
      }
   }

   public boolean continueExecuting() {
      if (!this.theWolf.isAttacking() && !this.theWolf.isHostileToPlayers()) {
         return !this.thePlayer.isEntityAlive() ? false : (this.theWolf.getDistanceSqToEntity(this.thePlayer) > (double)(this.minPlayerDistance * this.minPlayerDistance) ? false : this.field_75384_e > 0 && this.hasPlayerGotBoneInHand(this.thePlayer));
      } else {
         return false;
      }
   }

   public void startExecuting() {
      this.theWolf.func_70918_i(true);
      this.field_75384_e = 40 + this.theWolf.getRNG().nextInt(40);
   }

   public void resetTask() {
      this.theWolf.func_70918_i(false);
      this.thePlayer = null;
   }

   public void updateTask() {
      this.theWolf.getLookHelper().setLookPosition(this.thePlayer.posX, this.thePlayer.posY + (double)this.thePlayer.getEyeHeight(), this.thePlayer.posZ, 10.0F, (float)this.theWolf.getVerticalFaceSpeed());
      --this.field_75384_e;
   }

   private boolean hasPlayerGotBoneInHand(EntityPlayer par1EntityPlayer) {
      ItemStack var2 = par1EntityPlayer.inventory.getCurrentItemStack();
      return var2 == null ? false : (!this.theWolf.isTamed() && var2.itemID == Item.bone.itemID ? true : this.theWolf.isFoodItem(var2));
   }
}
