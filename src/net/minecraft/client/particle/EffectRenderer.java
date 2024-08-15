package net.minecraft.client.particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BitHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFace;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public final class EffectRenderer {
   private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");
   protected World worldObj;
   private List[] fxLayers = new List[4];
   private TextureManager renderer;
   private Random rand = new Random();

   public EffectRenderer(World par1World, TextureManager par2TextureManager) {
      if (par1World != null) {
         this.worldObj = par1World;
      }

      this.renderer = par2TextureManager;

      for(int var3 = 0; var3 < 4; ++var3) {
         this.fxLayers[var3] = new ArrayList();
      }

   }

   public void addEffect(EntityFX par1EntityFX) {
      int var2 = par1EntityFX.getFXLayer();
      if (this.fxLayers[var2].size() >= 4000) {
         this.fxLayers[var2].remove(0);
      }

      this.fxLayers[var2].add(par1EntityFX);
   }

   public void updateEffects() {
      for(int var1 = 0; var1 < 4; ++var1) {
         for(int var2 = 0; var2 < this.fxLayers[var1].size(); ++var2) {
            EntityFX var3 = (EntityFX)this.fxLayers[var1].get(var2);
            var3.onUpdate();
            if (var3.isDead) {
               this.fxLayers[var1].remove(var2--);
            }
         }
      }

   }

   public void renderParticles(Entity par1Entity, float par2) {
      float var3 = ActiveRenderInfo.rotationX;
      float var4 = ActiveRenderInfo.rotationZ;
      float var5 = ActiveRenderInfo.rotationYZ;
      float var6 = ActiveRenderInfo.rotationXY;
      float var7 = ActiveRenderInfo.rotationXZ;
      EntityFX.interpPosX = par1Entity.lastTickPosX + (par1Entity.posX - par1Entity.lastTickPosX) * (double)par2;
      EntityFX.interpPosY = par1Entity.lastTickPosY + (par1Entity.posY - par1Entity.lastTickPosY) * (double)par2;
      EntityFX.interpPosZ = par1Entity.lastTickPosZ + (par1Entity.posZ - par1Entity.lastTickPosZ) * (double)par2;

      for(int var8 = 0; var8 < 3; ++var8) {
         if (!this.fxLayers[var8].isEmpty()) {
            switch (var8) {
               case 0:
               default:
                  this.renderer.bindTexture(particleTextures);
                  break;
               case 1:
                  this.renderer.bindTexture(TextureMap.locationBlocksTexture);
                  break;
               case 2:
                  this.renderer.bindTexture(TextureMap.locationItemsTexture);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDepthMask(true);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glAlphaFunc(516, 0.003921569F);
            Tessellator var9 = Tessellator.instance;
            var9.startDrawingQuads();

            for(int var10 = 0; var10 < this.fxLayers[var8].size(); ++var10) {
               EntityFX var11 = (EntityFX)this.fxLayers[var8].get(var10);
               var9.setBrightness(var11.getBrightnessForRender(par2));
               var11.renderParticle(var9, par2, var3, var7, var4, var5, var6);
            }

            var9.draw();
            GL11.glDisable(3042);
            GL11.glDepthMask(true);
            GL11.glAlphaFunc(516, 0.1F);
         }
      }

   }

   public void renderLitParticles(Entity par1Entity, float par2) {
      float var3 = 0.017453292F;
      float var4 = MathHelper.cos(par1Entity.rotationYaw * 0.017453292F);
      float var5 = MathHelper.sin(par1Entity.rotationYaw * 0.017453292F);
      float var6 = -var5 * MathHelper.sin(par1Entity.rotationPitch * 0.017453292F);
      float var7 = var4 * MathHelper.sin(par1Entity.rotationPitch * 0.017453292F);
      float var8 = MathHelper.cos(par1Entity.rotationPitch * 0.017453292F);
      byte var9 = 3;
      List var10 = this.fxLayers[var9];
      if (!var10.isEmpty()) {
         Tessellator var11 = Tessellator.instance;

         for(int var12 = 0; var12 < var10.size(); ++var12) {
            EntityFX var13 = (EntityFX)var10.get(var12);
            var11.setBrightness(var13.getBrightnessForRender(par2));
            var13.renderParticle(var11, par2, var4, var8, var5, var6, var7);
         }
      }

   }

   public void clearEffects(World par1World) {
      this.worldObj = par1World;

      for(int var2 = 0; var2 < 4; ++var2) {
         this.fxLayers[var2].clear();
      }

   }

   public void addBlockDestroyEffectsForSnow(int par1, int par2, int par3, int par4, int par5) {
      if (par4 != 0) {
         Block var6 = Block.blocksList[par4];
         byte var7 = 4;

         for(int var8 = 0; var8 < var7; ++var8) {
            for(int var9 = 0; var9 < var7; ++var9) {
               for(int var10 = 0; var10 < var7; ++var10) {
                  double var11 = (double)par1 + ((double)var8 + 0.5) / (double)var7;
                  double var10000 = (double)par2 + ((double)var9 + 0.5) / (double)var7;
                  double var15 = (double)par3 + ((double)var10 + 0.5) / (double)var7;
                  float max_offset_y = (float)(par5 + 1) * 0.125F;

                  float offset_y;
                  do {
                     offset_y = this.rand.nextFloat();
                  } while(!(offset_y <= max_offset_y));

                  double var13 = (double)((float)par2 + offset_y);
                  EntityDiggingFX fx = new EntityDiggingFX(this.worldObj, var11, var13, var15, var11 - (double)par1 - 0.5, var13 - (double)par2 - 0.5, var15 - (double)par3 - 0.5, var6, par5);
                  fx.motionX *= 0.4000000059604645;
                  fx.motionY = 0.0;
                  fx.motionZ *= 0.4000000059604645;
                  fx.particleScale *= 0.8F;
                  fx.visible_on_tick = Minecraft.theMinecraft.theWorld.getTotalWorldTime() + 1L;
                  this.addEffect(fx.applyColourMultiplier(par1, par2, par3));
               }
            }
         }
      }

   }

   public void addBlockDestroyEffectsForPortalDamage(int par1, int par2, int par3, int par4, int par5) {
      if (par4 != 0) {
         Block var6 = this.worldObj.getBlock(par1, par2, par3);
         if (var6 == null || !PathFinder.isWoodenPortal(var6.blockID)) {
            return;
         }

         var6.setBlockBoundsBasedOnStateAndNeighbors(this.worldObj, par1, par2, par3);
         int index = Minecraft.getThreadIndex();

         for(int i = 0; i < 4; ++i) {
            double var11 = (double)par1 + var6.minX[index] + (double)this.rand.nextFloat() * (var6.maxX[index] - var6.minX[index]);
            double var13 = (double)par2 + var6.minY[index] + (double)this.rand.nextFloat() * (var6.maxY[index] - var6.minY[index]);
            double var15 = (double)par3 + var6.minZ[index] + (double)this.rand.nextFloat() * (var6.maxZ[index] - var6.minZ[index]);
            EntityDiggingFX fx = new EntityDiggingFX(this.worldObj, var11, var13, var15, var11 - (double)par1 - 0.5, var13 - (double)par2 - 0.5, var15 - (double)par3 - 0.5, var6, par5);
            fx.motionX *= 0.4000000059604645;
            fx.motionY = 0.0;
            fx.motionZ *= 0.4000000059604645;
            fx.particleScale *= 0.5F;
            this.addEffect(fx.applyColourMultiplier(par1, par2, par3));
         }
      }

   }

   public void addBlockDestroyEffects(int x, int y, int z, int block_id, int metadata, int aux_data) {
      this.addBlockDestroyEffects(x, y, z, block_id, metadata, aux_data, (AxisAlignedBB)null);
   }

   public void addBlockDestroyEffects(int x, int y, int z, int block_id, int metadata, int aux_data, AxisAlignedBB bounds_of_exclusion) {
      if (block_id != 0) {
         Block block = Block.getBlock(block_id);
         long visible_on_tick = this.worldObj.getTotalWorldTime() + 1L;
         boolean was_not_legal = BitHelper.isBitSet(aux_data, RenderGlobal.SFX_2001_WAS_NOT_LEGAL);
         block.setBlockBoundsBasedOnStateAndNeighbors(this.worldObj, x, y, z);
         int index = Minecraft.getThreadIndex();
         double min_x = MathHelper.clamp_double(block.getBlockBoundsMinX(index), 0.125, 0.875);
         double max_x = MathHelper.clamp_double(block.getBlockBoundsMaxX(index), 0.125, 0.875);
         double min_y = MathHelper.clamp_double(block.getBlockBoundsMinY(index), 0.125, 0.875);
         double max_y = MathHelper.clamp_double(block.getBlockBoundsMaxY(index), 0.125, 0.875);
         double min_z = MathHelper.clamp_double(block.getBlockBoundsMinZ(index), 0.125, 0.875);
         double max_z = MathHelper.clamp_double(block.getBlockBoundsMaxZ(index), 0.125, 0.875);
         double range_x = max_x - min_x;
         double range_y = max_y - min_y;
         double range_z = max_z - min_z;
         int num_divisions_x = 2 + MathHelper.ceiling_double_int(range_x * 2.0);
         int num_divisions_y = 2 + MathHelper.ceiling_double_int(range_y * 2.0);
         int num_divisions_z = 2 + MathHelper.ceiling_double_int(range_z * 2.0);

         for(int var8 = 0; var8 < num_divisions_x; ++var8) {
            for(int var9 = 0; var9 < num_divisions_y; ++var9) {
               for(int var10 = 0; var10 < num_divisions_z; ++var10) {
                  double var11 = (double)x + min_x + range_x * (double)var8 / (double)(num_divisions_x - 1);
                  double var13 = (double)y + min_y + range_y * (double)var9 / (double)(num_divisions_y - 1);
                  double var15 = (double)z + min_z + range_z * (double)var10 / (double)(num_divisions_z - 1);
                  if (bounds_of_exclusion == null || !(var11 >= bounds_of_exclusion.minX) || !(var11 < bounds_of_exclusion.maxX) || !(var13 >= bounds_of_exclusion.minY) || !(var13 < bounds_of_exclusion.maxY) || !(var15 >= bounds_of_exclusion.minZ) || !(var15 < bounds_of_exclusion.maxZ)) {
                     double motion_x = var11 - (double)x - (max_x + min_x) * 0.5;
                     double motion_y = var13 - (double)y - (max_y + min_y) * 0.5;
                     double motion_z = var15 - (double)z - (max_z + min_z) * 0.5;
                     double scaler_x = 1.0 / MathHelper.clamp_double(block.getBlockBoundsMaxX(index) - block.getBlockBoundsMinX(index), 0.8, 1.0);
                     double scaler_z = 1.0 / MathHelper.clamp_double(block.getBlockBoundsMaxZ(index) - block.getBlockBoundsMinZ(index), 0.8, 1.0);
                     EntityFX fx = (new EntityDiggingFX(this.worldObj, var11, var13, var15, motion_x, motion_y, motion_z, block, metadata)).applyColourMultiplier(x, y, z).setVisibleOnTick(visible_on_tick);
                     if (was_not_legal) {
                        fx.motionX *= 0.699999988079071;
                        fx.motionZ *= 0.699999988079071;
                        fx.motionY *= 0.30000001192092896;
                     }

                     fx.motionX *= scaler_x;
                     fx.motionZ *= scaler_z;
                     this.addEffect(fx);
                  }
               }
            }
         }

      }
   }

   public void addBlockDestroyEffectsForReplace(int x, int y, int z, int block_id, int metadata, int successor_block_id, int successor_metadata) {
      if (block_id != 0) {
         Block successor_block = Block.getBlock(successor_block_id);
         if (!successor_block.isSolid(successor_metadata)) {
            this.addBlockDestroyEffects(x, y, z, block_id, metadata, 0);
         } else {
            int original_metadata = this.worldObj.getBlockMetadata(x, y, z);
            this.worldObj.setBlockMetadataWithNotify(x, y, z, successor_metadata, 0);
            AxisAlignedBB bb = successor_block.getCollisionBoundsCombined(this.worldObj, x, y, z, (Entity)null, true);
            this.worldObj.setBlockMetadataWithNotify(x, y, z, original_metadata, 0);
            this.addBlockDestroyEffects(x, y, z, block_id, metadata, 0, bb);
         }
      }
   }

   public void addBlockHitEffects(int par1, int par2, int par3, EnumFace face_hit) {
      int var5 = this.worldObj.getBlockId(par1, par2, par3);
      if (var5 != 0) {
         Block var6 = Block.blocksList[var5];
         float var7 = 0.1F;
         int index = Minecraft.getThreadIndex();
         double var8 = (double)par1 + this.rand.nextDouble() * (var6.getBlockBoundsMaxX(index) - var6.getBlockBoundsMinX(index) - (double)(var7 * 2.0F)) + (double)var7 + var6.getBlockBoundsMinX(index);
         double var10 = (double)par2 + this.rand.nextDouble() * (var6.getBlockBoundsMaxY(index) - var6.getBlockBoundsMinY(index) - (double)(var7 * 2.0F)) + (double)var7 + var6.getBlockBoundsMinY(index);
         double var12 = (double)par3 + this.rand.nextDouble() * (var6.getBlockBoundsMaxZ(index) - var6.getBlockBoundsMinZ(index) - (double)(var7 * 2.0F)) + (double)var7 + var6.getBlockBoundsMinZ(index);
         if (face_hit == EnumFace.BOTTOM) {
            var10 = (double)par2 + var6.getBlockBoundsMinY(index) - (double)var7;
         }

         if (face_hit == EnumFace.TOP) {
            var10 = (double)par2 + var6.getBlockBoundsMaxY(index) + (double)var7;
         }

         if (face_hit == EnumFace.NORTH) {
            var12 = (double)par3 + var6.getBlockBoundsMinZ(index) - (double)var7;
         }

         if (face_hit == EnumFace.SOUTH) {
            var12 = (double)par3 + var6.getBlockBoundsMaxZ(index) + (double)var7;
         }

         if (face_hit == EnumFace.WEST) {
            var8 = (double)par1 + var6.getBlockBoundsMinX(index) - (double)var7;
         }

         if (face_hit == EnumFace.EAST) {
            var8 = (double)par1 + var6.getBlockBoundsMaxX(index) + (double)var7;
         }

         this.addEffect((new EntityDiggingFX(this.worldObj, var8, var10, var12, 0.0, 0.0, 0.0, var6, this.worldObj.getBlockMetadata(par1, par2, par3))).applyColourMultiplier(par1, par2, par3).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
      }

   }

   public String getStatistics() {
      return "" + (this.fxLayers[0].size() + this.fxLayers[1].size() + this.fxLayers[2].size());
   }

   public String getStatsString() {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < this.fxLayers.length; ++i) {
         int size = this.fxLayers[i].size();
         if (i == 0 || size != 0) {
            sb.append("[" + i + "]: " + size + " ");
         }
      }

      return StringHelper.stripTrailing(" ", sb.toString());
   }
}
