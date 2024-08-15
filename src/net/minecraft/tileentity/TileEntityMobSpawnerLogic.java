package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumEntityState;
import net.minecraft.world.World;

class TileEntityMobSpawnerLogic extends MobSpawnerBaseLogic {
   final TileEntityMobSpawner mobSpawnerEntity;

   TileEntityMobSpawnerLogic(TileEntityMobSpawner par1TileEntityMobSpawner) {
      this.mobSpawnerEntity = par1TileEntityMobSpawner;
   }

   public void func_98267_a(EnumEntityState par1) {
      if (par1 == EnumEntityState.mob_spawn) {
         this.mobSpawnerEntity.worldObj.addBlockEvent(this.mobSpawnerEntity.xCoord, this.mobSpawnerEntity.yCoord, this.mobSpawnerEntity.zCoord, Block.mobSpawner.blockID, 1, 0);
      } else {
         Minecraft.setErrorMessage("func_98267_a: unhandled case, EnumEntityState ordinal=" + par1.ordinal());
      }

   }

   public World getSpawnerWorld() {
      return this.mobSpawnerEntity.worldObj;
   }

   public int getSpawnerX() {
      return this.mobSpawnerEntity.xCoord;
   }

   public int getSpawnerY() {
      return this.mobSpawnerEntity.yCoord;
   }

   public int getSpawnerZ() {
      return this.mobSpawnerEntity.zCoord;
   }

   public void setRandomMinecart(WeightedRandomMinecart par1WeightedRandomMinecart) {
      super.setRandomMinecart(par1WeightedRandomMinecart);
      if (this.getSpawnerWorld() != null) {
         this.getSpawnerWorld().markBlockForUpdate(this.mobSpawnerEntity.xCoord, this.mobSpawnerEntity.yCoord, this.mobSpawnerEntity.zCoord);
      }

   }

   public boolean canRun() {
      return this.getSpawnerWorld().getBlockMetadata(this.getSpawnerX(), this.getSpawnerY(), this.getSpawnerZ()) == 15 ? false : super.canRun();
   }
}
