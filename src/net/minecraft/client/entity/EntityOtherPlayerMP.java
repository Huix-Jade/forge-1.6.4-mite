package net.minecraft.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityOtherPlayerMP extends AbstractClientPlayer {
   public boolean isItemInUse;
   private int otherPlayerMPPosRotationIncrements;
   private double otherPlayerMPX;
   private double otherPlayerMPY;
   private double otherPlayerMPZ;
   private double otherPlayerMPYaw;
   private double otherPlayerMPPitch;
   private boolean initial_position_sync = true;

   public EntityOtherPlayerMP(World par1World, String par2Str) {
      super(par1World, par2Str);
      this.yOffset = 0.0F;
      this.stepHeight = 0.0F;
      this.noClip = true;
      this.field_71082_cx = 0.25F;
      this.renderDistanceWeight = 10.0;
   }

   protected void resetHeight() {
      this.yOffset = 0.0F;
   }

   public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
      this.otherPlayerMPX = par1;
      this.otherPlayerMPY = par3;
      this.otherPlayerMPZ = par5;
      this.otherPlayerMPYaw = (double)par7;
      this.otherPlayerMPPitch = (double)par8;
      this.otherPlayerMPPosRotationIncrements = par9;
   }

   public void onUpdate() {
      this.field_71082_cx = 0.0F;
      super.onUpdate();
      this.prevLimbSwingAmount = this.limbSwingAmount;
      double var1 = this.posX - this.prevPosX;
      double var3 = this.posZ - this.prevPosZ;
      float var5 = MathHelper.sqrt_double(var1 * var1 + var3 * var3) * 4.0F;
      if (var5 > 1.0F) {
         var5 = 1.0F;
      }

      this.limbSwingAmount += (var5 - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
      if (this.itemInUseCount > 0) {
         --this.itemInUseCount;
      }

   }

   public float getShadowSize() {
      return 0.0F;
   }

   public void onLivingUpdate() {
      super.updateEntityActionState();
      if (this.otherPlayerMPPosRotationIncrements > 0) {
         if (this.initial_position_sync) {
            this.initial_position_sync = false;
            this.otherPlayerMPPosRotationIncrements = 1;
            this.prevRotationYaw = (float)this.otherPlayerMPYaw;
            this.prevRotationYawHead = (float)this.otherPlayerMPYaw;
            this.prevRotationPitch = (float)this.otherPlayerMPPitch;
         }

         double var1 = this.posX + (this.otherPlayerMPX - this.posX) / (double)this.otherPlayerMPPosRotationIncrements;
         double var3 = this.posY + (this.otherPlayerMPY - this.posY) / (double)this.otherPlayerMPPosRotationIncrements;
         double var5 = this.posZ + (this.otherPlayerMPZ - this.posZ) / (double)this.otherPlayerMPPosRotationIncrements;

         double var7;
         for(var7 = this.otherPlayerMPYaw - (double)this.rotationYaw; var7 < -180.0; var7 += 360.0) {
         }

         while(var7 >= 180.0) {
            var7 -= 360.0;
         }

         this.rotationYaw = (float)((double)this.rotationYaw + var7 / (double)this.otherPlayerMPPosRotationIncrements);
         this.rotationPitch = (float)((double)this.rotationPitch + (this.otherPlayerMPPitch - (double)this.rotationPitch) / (double)this.otherPlayerMPPosRotationIncrements);
         --this.otherPlayerMPPosRotationIncrements;
         this.setPosition(var1, var3, var5);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      }

      this.prevCameraYaw = this.cameraYaw;
      float var9 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      float var2 = (float)Math.atan(-this.motionY * 0.20000000298023224) * 15.0F;
      if (var9 > 0.1F) {
         var9 = 0.1F;
      }

      if (!this.onGround || this.getHealth() <= 0.0F) {
         var9 = 0.0F;
      }

      if (this.onGround || this.getHealth() <= 0.0F) {
         var2 = 0.0F;
      }

      this.cameraYaw += (var9 - this.cameraYaw) * 0.4F;
      this.cameraPitch += (var2 - this.cameraPitch) * 0.8F;
   }

   public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack) {
      if (par1 == 0) {
         this.inventory.mainInventory[this.inventory.currentItem] = par2ItemStack;
      } else {
         this.inventory.armorInventory[par1 - 1] = par2ItemStack;
      }

   }

   @Override
   public float getDefaultEyeHeight()
   {
      return 1.82F;
   }
//   public float getEyeHeight() {
//      return 1.82F;
//   }

   public double getFootPosY() {
      double foot_pos_y = this.posY;
      int foot_pos_y_int = (int)foot_pos_y;
      if (foot_pos_y < (double)foot_pos_y_int && (double)foot_pos_y_int - foot_pos_y < 9.999999747378752E-5) {
         foot_pos_y = (double)foot_pos_y_int;
      }

      return foot_pos_y;
   }

   public double getEyePosY() {
      return this.getFootPosY() + (double)EntityPlayer.y_offset_on_client_and_eye_height_on_server;
   }

   public void sendChatToPlayer(ChatMessageComponent par1ChatMessageComponent) {
      Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(par1ChatMessageComponent.toStringWithFormatting(true));
   }

   public boolean canCommandSenderUseCommand(int par1, String par2Str) {
      return false;
   }

   public ChunkCoordinates getPlayerCoordinates() {
      return new ChunkCoordinates(MathHelper.floor_double(this.posX + 0.5), MathHelper.floor_double(this.posY + 0.5), MathHelper.floor_double(this.posZ + 0.5));
   }

   public INetworkManager getNetManager() {
      return null;
   }


}
