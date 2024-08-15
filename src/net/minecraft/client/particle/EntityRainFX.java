package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityRainFX extends EntityFX {
   public EntityRainFX(World par1World, double par2, double par4, double par6) {
      super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
      this.motionX *= 0.30000001192092896;
      this.motionY = (double)((float)Math.random() * 0.2F + 0.1F);
      this.motionZ *= 0.30000001192092896;
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.setParticleTextureIndex(19 + this.rand.nextInt(4));
      this.setSize(0.01F, 0.01F);
      this.particleGravity = 0.06F;
      this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= (double)this.particleGravity;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9800000190734863;
      this.motionY *= 0.9800000190734863;
      this.motionZ *= 0.9800000190734863;
      if (this.particleMaxAge-- <= 0) {
         this.setDead();
      }

      if (this.onGround) {
         if (Math.random() < 0.5) {
            this.setDead();
         }

         this.motionX *= 0.699999988079071;
         this.motionZ *= 0.699999988079071;
      }

      if (!(this.motionY > 0.0)) {
         int x = this.getBlockPosX();
         int y = this.getBlockPosY();
         int z = this.getBlockPosZ();
         int precipitation_height = this.worldObj.getPrecipitationHeight(x, z);
         if (MathHelper.floor_double(this.posY + this.motionY) < precipitation_height - 1) {
            this.setDead();
         } else {
            int block_id = this.worldObj.getBlockId(x, y, z);
            if (block_id > 0) {
               Block block = Block.getBlock(block_id);
               if (block.blockMaterial.isSolid()) {
                  if (block.isAlwaysStandardFormCube()) {
                     this.setDead();
                  } else {
                     block.setBlockBoundsBasedOnStateAndNeighbors(this.worldObj, x, y, z);
                     if (this.posY < block.maxY[Minecraft.getThreadIndex()]) {
                        this.setDead();
                     }
                  }
               } else if (block.blockMaterial.isLiquid()) {
                  double var2 = (double)((float)(y + 1) - BlockFluid.getFluidHeightPercent(this.worldObj.getBlockMetadata(x, y, z)));
                  if (this.posY + this.motionY < var2) {
                     this.setDead();
                  }
               }
            }

         }
      }
   }
}
