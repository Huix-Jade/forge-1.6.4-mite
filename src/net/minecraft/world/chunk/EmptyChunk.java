package net.minecraft.world.chunk;

import java.util.List;
import java.util.Random;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public final class EmptyChunk extends Chunk {
   public EmptyChunk(World par1World, int par2, int par3) {
      super(par1World, par2, par3);
   }

   public TileEntity getChunkBlockTileEntity(int par1, int par2, int par3) {
      return null;
   }

   public void addTileEntity(TileEntity par1TileEntity) {
   }

   public void setChunkBlockTileEntity(int par1, int par2, int par3, TileEntity par4TileEntity) {
   }

   public void removeChunkBlockTileEntity(int par1, int par2, int par3) {
   }

   public void onChunkLoad() {
   }

   public void onChunkUnload() {
   }

   public void getEntitiesWithinAABBForEntity(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB, List par3List, IEntitySelector par4IEntitySelector) {
   }

   public void getEntitiesOfTypeWithinAAAB(Class par1Class, AxisAlignedBB par2AxisAlignedBB, List par3List, IEntitySelector par4IEntitySelector) {
   }

   public Random getRandomWithSeed(long par1) {
      return new Random(this.worldObj.getSeed() + (long)(this.xPosition * this.xPosition * 4987142) + (long)(this.xPosition * 5947611) + (long)(this.zPosition * this.zPosition) * 4392871L + (long)(this.zPosition * 389711) ^ par1);
   }
}
