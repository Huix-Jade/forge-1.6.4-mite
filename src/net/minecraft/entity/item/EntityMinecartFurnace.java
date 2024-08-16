package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

public class EntityMinecartFurnace extends EntityMinecart {
   private int fuel;
   public double pushX;
   public double pushZ;

   public EntityMinecartFurnace(World par1World) {
      super(par1World);
   }

   public EntityMinecartFurnace(World par1World, double par2, double par4, double par6) {
      super(par1World, par2, par4, par6);
   }

   public int getMinecartType() {
      return 2;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.fuel > 0) {
         --this.fuel;
         if (this.onServer() && (this.fuel == 0 || this.ticksExisted == 20 || this.ticksExisted % 200 == 0)) {
            this.sendPacketToAllPlayersTrackingEntity((new Packet85SimpleSignal(EnumSignal.update_minecart_fuel)).setInteger(this.fuel).setEntityID(this));
         }
      }

      if (this.fuel <= 0) {
         this.pushX = this.pushZ = 0.0;
      }

      this.setMinecartPowered(this.fuel > 0);
      if (this.isMinecartPowered() && this.rand.nextInt(4) == 0) {
         this.worldObj.spawnParticle(EnumParticle.largesmoke, this.posX, this.posY + 0.8, this.posZ, 0.0, 0.0, 0.0);
      }

   }

   public void killMinecart(DamageSource par1DamageSource) {
      super.killMinecart(par1DamageSource);
      if (!par1DamageSource.isExplosion()) {
         this.dropItemStack(new ItemStack(Block.furnaceIdle, 1), 0.0F);
      }

   }

   protected void updateOnTrack(int par1, int par2, int par3, double par4, double par6, int par8, int par9) {
      super.updateOnTrack(par1, par2, par3, par4, par6, par8, par9);
      double var10 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (var10 > 1.0E-4 && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001) {
         var10 = (double)MathHelper.sqrt_double(var10);
         this.pushX /= var10;
         this.pushZ /= var10;
         if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0) {
            this.pushX = 0.0;
            this.pushZ = 0.0;
         } else {
            this.pushX = this.motionX;
            this.pushZ = this.motionZ;
         }
      }

   }

   protected void applyDrag() {
      double var1 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (var1 > 1.0E-4) {
         var1 = (double)MathHelper.sqrt_double(var1);
         this.pushX /= var1;
         this.pushZ /= var1;
         double var3 = 0.05;
         this.motionX *= 0.800000011920929;
         this.motionY *= 0.0;
         this.motionZ *= 0.800000011920929;
         this.motionX += this.pushX * var3;
         this.motionZ += this.pushZ * var3;
      } else {
         this.motionX *= 0.9800000190734863;
         this.motionY *= 0.0;
         this.motionZ *= 0.9800000190734863;
      }

      super.applyDrag();
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if(MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player)))
      {
         return true;
      }

      if (player.getHeldItem() == Item.coal) {
         this.fuel += 3600;
         if (player.onServer() && !player.inCreativeMode()) {
            player.convertOneOfHeldItem((ItemStack)null);
         }
      }

      this.pushX = this.posX - player.posX;
      this.pushZ = this.posZ - player.posZ;
      return true;
   }

   protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setDouble("PushX", this.pushX);
      par1NBTTagCompound.setDouble("PushZ", this.pushZ);
      par1NBTTagCompound.setShort("Fuel", (short)this.fuel);
   }

   protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.pushX = par1NBTTagCompound.getDouble("PushX");
      this.pushZ = par1NBTTagCompound.getDouble("PushZ");
      this.fuel = par1NBTTagCompound.getShort("Fuel");
   }

   protected boolean isMinecartPowered() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   protected void setMinecartPowered(boolean par1) {
      if (par1) {
         this.dataWatcher.updateObject(16, (byte)(this.dataWatcher.getWatchableObjectByte(16) | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(this.dataWatcher.getWatchableObjectByte(16) & -2));
      }

   }

   public Block getDefaultDisplayTile() {
      return Block.furnaceBurning;
   }

   public int getDefaultDisplayTileData() {
      return 2;
   }

   public Item getModelItem() {
      return Item.minecartPowered;
   }

   public void setFuel(int fuel) {
      this.fuel = fuel;
   }
}
