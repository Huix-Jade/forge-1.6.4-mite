package net.minecraft.client.renderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

public final class WorldRenderer {
   public World worldObj;
   private int glRenderList = -1;
   public static int chunksUpdated;
   private int posX;
   private int posY;
   private int posZ;
   public int posXMinus;
   public int posYMinus;
   public int posZMinus;
   public int posXClip;
   public int posYClip;
   public int posZClip;
   public boolean isInFrustum;
   public boolean[] skipRenderPass = new boolean[2];
   public int posXPlus;
   public int posYPlus;
   public int posZPlus;
   public boolean needsUpdate;
   public AxisAlignedBB rendererBoundingBox;
   public int chunkIndex;
   public boolean isVisible = true;
   public boolean isWaitingOnOcclusionQuery;
   public int glOcclusionQuery;
   public boolean isChunkLit;
   public boolean isInitialized;
   public List tileEntityRenderers = new ArrayList();
   private List tileEntities;
   private int bytesDrawn;

   public WorldRenderer(World par1World, List par2List, int par3, int par4, int par5, int par6) {
      this.worldObj = par1World;
      this.tileEntities = par2List;
      this.glRenderList = par6;
      this.posX = -999;
      this.setPosition(par3, par4, par5);
      this.needsUpdate = false;
   }

   public void setPosition(int par1, int par2, int par3) {
      if (par1 != this.posX || par2 != this.posY || par3 != this.posZ) {
         this.setDontDraw();
         this.posX = par1;
         this.posY = par2;
         this.posZ = par3;
         this.posXPlus = par1 + 8;
         this.posYPlus = par2 + 8;
         this.posZPlus = par3 + 8;
         this.posXClip = par1 & 1023;
         this.posYClip = par2;
         this.posZClip = par3 & 1023;
         this.posXMinus = par1 - this.posXClip;
         this.posYMinus = par2 - this.posYClip;
         this.posZMinus = par3 - this.posZClip;
         float var4 = 6.0F;
         this.rendererBoundingBox = AxisAlignedBB.getBoundingBox((double)((float)par1 - var4), (double)((float)par2 - var4), (double)((float)par3 - var4), (double)((float)(par1 + 16) + var4), (double)((float)(par2 + 16) + var4), (double)((float)(par3 + 16) + var4));
         GL11.glNewList(this.glRenderList + 2, 4864);
         RenderItem.renderAABB(AxisAlignedBB.getAABBPool().getAABB((double)((float)this.posXClip - var4), (double)((float)this.posYClip - var4), (double)((float)this.posZClip - var4), (double)((float)(this.posXClip + 16) + var4), (double)((float)(this.posYClip + 16) + var4), (double)((float)(this.posZClip + 16) + var4)));
         GL11.glEndList();
         this.markDirty();
      }

   }

   private void setupGLTranslation() {
      GL11.glTranslatef((float)this.posXClip, (float)this.posYClip, (float)this.posZClip);
   }

   public void updateRenderer() {
      if (this.needsUpdate) {
         Chunk chunk = this.worldObj.getChunkFromBlockCoords(this.posX, this.posZ);
         this.needsUpdate = false;
         int var1 = this.posX;
         int var2 = this.posY;
         int var3 = this.posZ;
         int var4 = this.posX + 15;
         int var5 = this.posY + 15;
         int var6 = this.posZ + 15;

         for(int var7 = 0; var7 < 2; ++var7) {
            this.skipRenderPass[var7] = true;
         }

         Chunk.isLit = false;
         HashSet var21 = new HashSet();
         var21.addAll(this.tileEntityRenderers);
         this.tileEntityRenderers.clear();
         byte var8 = 1;
         ChunkCache var9 = new ChunkCache(this.worldObj, var1 - var8, var2 - var8, var3 - var8, var4 + var8, var5 + var8, var6 + var8, var8);
         Tessellator tessellator = Tessellator.instance;
         if (!var9.extendedLevelsInChunkCache()) {
            ++chunksUpdated;
            RenderBlocks var10 = new RenderBlocks(var9);
            this.bytesDrawn = 0;

            for(int var11 = 0; var11 < 2; ++var11) {
               boolean var12 = false;
               boolean var13 = false;
               boolean var14 = false;

               for(int var15 = var2; var15 <= var5; ++var15) {
                  for(int var16 = var3; var16 <= var6; ++var16) {
                     for(int var17 = var1; var17 <= var4; ++var17) {
                        int var18 = chunk.getBlockID(var17 & 15, var15, var16 & 15);
                        if (var18 > 0) {
                           if (!var14) {
                              var14 = true;
                              GL11.glNewList(this.glRenderList + var11, 4864);
                              GL11.glPushMatrix();
                              this.setupGLTranslation();
                              float var19 = 1.00001F;
                              GL11.glTranslatef(-8.0F, -8.0F, -8.0F);
                              GL11.glScalef(var19, var19, var19);
                              GL11.glTranslatef(8.0F, 8.0F, 8.0F);
                              //ForgeHooksClient.beforeRenderPass(l1); Noop fo now, TODO: Event if anyone needs
                              Tessellator.instance.startDrawingQuads();
                              Tessellator.instance.setTranslation((double)(-this.posX), (double)(-this.posY), (double)(-this.posZ));
                           }

                           Block var23 = Block.blocksList[var18];
                           if (var23 != null) {
                              if (var11 == 0 && var23.hasTileEntity(chunk.getBlockMetadata(var17, var15, var16))) {
                                 TileEntity var20 = var9.getBlockTileEntity(var17, var15, var16);
                                 if (TileEntityRenderer.instance.hasSpecialRenderer(var20)) {
                                    this.tileEntityRenderers.add(var20);
                                 }
                              }

                              int var24 = var23.getRenderBlockPass();
                              if (var24 > var11) {
                                 var12 = true;
                              }
                              if (!var23.canRenderInPass(var11)) {
                                 continue;
                              }
                              var13 |= var10.renderBlockByRenderType(var23, var17, var15, var16);
                           }
                        }
                     }
                  }
               }

               if (var14) {
                  this.bytesDrawn += Tessellator.instance.draw();
                  GL11.glPopMatrix();
                  GL11.glEndList();
                  Tessellator.instance.setTranslation(0.0D, 0.0D, 0.0D);
               } else {
                  var13 = false;
               }

               if (var13) {
                  this.skipRenderPass[var11] = false;
               }

               if (!var12) {
                  break;
               }
            }
         }

         HashSet var22 = new HashSet();
         var22.addAll(this.tileEntityRenderers);
         var22.removeAll(var21);
         this.tileEntities.addAll(var22);
         var21.removeAll(this.tileEntityRenderers);
         this.tileEntities.removeAll(var21);
         this.isChunkLit = Chunk.isLit;
         this.isInitialized = true;
      }

   }

   public float distanceToEntitySquared(Entity par1Entity) {
      float var2 = (float)(par1Entity.posX - (double)this.posXPlus);
      float var3 = (float)(par1Entity.posY - (double)this.posYPlus);
      float var4 = (float)(par1Entity.posZ - (double)this.posZPlus);
      return var2 * var2 + var3 * var3 + var4 * var4;
   }

   public void setDontDraw() {
      for(int var1 = 0; var1 < 2; ++var1) {
         this.skipRenderPass[var1] = true;
      }

      this.isInFrustum = false;
      this.isInitialized = false;
   }

   public void stopRendering() {
      this.setDontDraw();
      this.worldObj = null;
   }

   public int getGLCallListForPass(int par1) {
      return !this.isInFrustum ? -1 : (!this.skipRenderPass[par1] ? this.glRenderList + par1 : -1);
   }

   public void updateInFrustum(ICamera par1ICamera) {
      this.isInFrustum = par1ICamera.isBoundingBoxInFrustum(this.rendererBoundingBox);
   }

   public void callOcclusionQueryList() {
      GL11.glCallList(this.glRenderList + 2);
   }

   public boolean skipAllRenderPasses() {
      return !this.isInitialized ? false : this.skipRenderPass[0] && this.skipRenderPass[1];
   }

   public void markDirty() {
      this.needsUpdate = true;
   }

   public boolean isRenderingCoords(int x, int y, int z) {
      return x >= this.posX && x < this.posX + 16 && y >= this.posY && y < this.posY + 16 && z >= this.posZ && z < this.posZ + 16;
   }
}
