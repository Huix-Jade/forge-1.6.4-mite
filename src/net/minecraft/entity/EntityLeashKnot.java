package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemLeash;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityLeashKnot extends EntityHanging {
   public EntityLeashKnot(World par1World) {
      super(par1World);
   }

   public EntityLeashKnot(World par1World, int par2, int par3, int par4) {
      super(par1World, par2, par3, par4, 0);
      this.setPosition((double)par2 + 0.5, (double)par3 + 0.5, (double)par4 + 0.5);
   }

   protected void entityInit() {
      super.entityInit();
   }

   public void setDirection(int par1) {
   }

   public int getWidthPixels() {
      return 9;
   }

   public int getHeightPixels() {
      return 9;
   }

   public boolean isInRangeToRenderDist(double par1) {
      return par1 < 1024.0;
   }

   public void onBroken(Entity par1Entity) {
   }

   public boolean isWrittenToChunkNBT() {
      return false;
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if (player.onClient()) {
         player.swingArm();
      }

      if (!ItemLeash.transferLeashedEntitiesToAnotherEntity(player, this, true)) {
         ItemLeash.unleashEntitiesThatAreLeashedToEntity(this, !player.inCreativeMode(), true);
         this.setDead();
      }

      return true;
   }

   public boolean onValidSurface() {
      int var1 = this.worldObj.getBlockId(this.xPosition, this.yPosition, this.zPosition);
      return Block.blocksList[var1] != null && Block.blocksList[var1].getRenderType() == 11;
   }

   public static EntityLeashKnot func_110129_a(World par0World, int par1, int par2, int par3) {
      EntityLeashKnot var4 = new EntityLeashKnot(par0World, par1, par2, par3);
      var4.forceSpawn = true;
      par0World.spawnEntityInWorld(var4);
      return var4;
   }

   public static EntityLeashKnot getKnotForBlock(World par0World, int par1, int par2, int par3) {
      List var4 = par0World.getEntitiesWithinAABB(EntityLeashKnot.class, AxisAlignedBB.getAABBPool().getAABB((double)par1 - 1.0, (double)par2 - 1.0, (double)par3 - 1.0, (double)par1 + 1.0, (double)par2 + 1.0, (double)par3 + 1.0));
      Object var5 = null;
      if (var4 != null) {
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            EntityLeashKnot var7 = (EntityLeashKnot)var6.next();
            if (var7.xPosition == par1 && var7.yPosition == par2 && var7.zPosition == par3) {
               return var7;
            }
         }
      }

      return null;
   }

   public Item getModelItem() {
      return Item.leash;
   }
}
