package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockComparator;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPlant;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRedstoneLogic;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.modlib.FMLRenderAccessLibrary;
import org.lwjgl.opengl.GL11;
import static net.minecraftforge.common.ForgeDirection.*;

public final class RenderBlocks {
   public IBlockAccess blockAccess;
   private Icon overrideBlockTexture;
   public boolean flipTexture;
   public boolean renderAllFaces;
   public static boolean fancyGrass = true;
   public boolean useInventoryTint = true;
   public double renderMinX;
   public double renderMaxX;
   public double renderMinY;
   public double renderMaxY;
   public double renderMinZ;
   public double renderMaxZ;
   public boolean lockBlockBounds;
   public boolean partialRenderBounds;
   public final Minecraft minecraftRB;
   private int uvRotateEast;
   private int uvRotateWest;
   private int uvRotateSouth;
   private int uvRotateNorth;
   private int uvRotateTop;
   private int uvRotateBottom;
   private boolean enableAO;
   private float aoLightValueScratchXYZNNN;
   private float aoLightValueScratchXYNN;
   private float aoLightValueScratchXYZNNP;
   private float aoLightValueScratchYZNN;
   private float aoLightValueScratchYZNP;
   private float aoLightValueScratchXYZPNN;
   private float aoLightValueScratchXYPN;
   private float aoLightValueScratchXYZPNP;
   private float aoLightValueScratchXYZNPN;
   private float aoLightValueScratchXYNP;
   private float aoLightValueScratchXYZNPP;
   private float aoLightValueScratchYZPN;
   private float aoLightValueScratchXYZPPN;
   private float aoLightValueScratchXYPP;
   private float aoLightValueScratchYZPP;
   private float aoLightValueScratchXYZPPP;
   private float aoLightValueScratchXZNN;
   private float aoLightValueScratchXZPN;
   private float aoLightValueScratchXZNP;
   private float aoLightValueScratchXZPP;
   private int aoBrightnessXYZNNN;
   private int aoBrightnessXYNN;
   private int aoBrightnessXYZNNP;
   private int aoBrightnessYZNN;
   private int aoBrightnessYZNP;
   private int aoBrightnessXYZPNN;
   private int aoBrightnessXYPN;
   private int aoBrightnessXYZPNP;
   private int aoBrightnessXYZNPN;
   private int aoBrightnessXYNP;
   private int aoBrightnessXYZNPP;
   private int aoBrightnessYZPN;
   private int aoBrightnessXYZPPN;
   private int aoBrightnessXYPP;
   private int aoBrightnessYZPP;
   private int aoBrightnessXYZPPP;
   private int aoBrightnessXZNN;
   private int aoBrightnessXZPN;
   private int aoBrightnessXZNP;
   private int aoBrightnessXZPP;
   private int brightnessTopLeft;
   private int brightnessBottomLeft;
   private int brightnessBottomRight;
   private int brightnessTopRight;
   private float colorRedTopLeft;
   private float colorRedBottomLeft;
   private float colorRedBottomRight;
   private float colorRedTopRight;
   private float colorGreenTopLeft;
   private float colorGreenBottomLeft;
   private float colorGreenBottomRight;
   private float colorGreenTopRight;
   private float colorBlueTopLeft;
   private float colorBlueBottomLeft;
   private float colorBlueBottomRight;
   private float colorBlueTopRight;
   double[] x = new double[4];
   double[] y = new double[4];
   double[] z = new double[4];
   double[] u = new double[4];
   double[] v = new double[4];
   float[] r = new float[4];
   float[] g = new float[4];
   float[] b = new float[4];
   int[] brightness = new int[4];
   public final int thread_index;

   public RenderBlocks(IBlockAccess par1IBlockAccess) {
      this.blockAccess = par1IBlockAccess;
      this.minecraftRB = Minecraft.getMinecraft();
      this.thread_index = Minecraft.getThreadIndex();
   }

   public RenderBlocks() {
      this.minecraftRB = Minecraft.getMinecraft();
      this.thread_index = Minecraft.getThreadIndex();
   }

   public boolean hasOverrideBlockTexture() {
      return this.overrideBlockTexture != null;
   }

   public void setOverrideBlockTexture(Icon par1Icon) {
      this.overrideBlockTexture = par1Icon;
   }

   public void clearOverrideBlockTexture() {
      this.overrideBlockTexture = null;
   }

   public void setRenderBounds(double par1, double par3, double par5, double par7, double par9, double par11) {
      if (!this.lockBlockBounds) {
         this.renderMinX = par1;
         this.renderMaxX = par7;
         this.renderMinY = par3;
         this.renderMaxY = par9;
         this.renderMinZ = par5;
         this.renderMaxZ = par11;
         this.partialRenderBounds = this.minecraftRB.gameSettings.ambientOcclusion >= 2 && (this.renderMinX > 0.0 || this.renderMaxX < 1.0 || this.renderMinY > 0.0 || this.renderMaxY < 1.0 || this.renderMinZ > 0.0 || this.renderMaxZ < 1.0);
      }

   }

   public void setRenderBoundsForStandardFormBlock() {
      if (!this.lockBlockBounds) {
         this.renderMinX = 0.0;
         this.renderMaxX = 1.0;
         this.renderMinY = 0.0;
         this.renderMaxY = 1.0;
         this.renderMinZ = 0.0;
         this.renderMaxZ = 1.0;
         this.partialRenderBounds = false;
      }
   }

   public void setRenderBoundsForNonStandardFormBlock(Block block) {
      if (!this.lockBlockBounds) {
         this.renderMinX = block.minX[this.thread_index];
         this.renderMaxX = block.maxX[this.thread_index];
         this.renderMinY = block.minY[this.thread_index];
         this.renderMaxY = block.maxY[this.thread_index];
         this.renderMinZ = block.minZ[this.thread_index];
         this.renderMaxZ = block.maxZ[this.thread_index];
         this.partialRenderBounds = this.minecraftRB.gameSettings.ambientOcclusion >= 2 && (this.renderMinX > 0.0 || this.renderMaxX < 1.0 || this.renderMinY > 0.0 || this.renderMaxY < 1.0 || this.renderMinZ > 0.0 || this.renderMaxZ < 1.0);
      }
   }

   public void XXXsetRenderBoundsFromBlock(Block par1Block) {
      if (!this.lockBlockBounds) {
         if (par1Block.isAlwaysStandardFormCube()) {
            this.renderMinX = 0.0;
            this.renderMaxX = 1.0;
            this.renderMinY = 0.0;
            this.renderMaxY = 1.0;
            this.renderMinZ = 0.0;
            this.renderMaxZ = 1.0;
            this.partialRenderBounds = false;
         } else {
            this.renderMinX = par1Block.minX[this.thread_index];
            this.renderMaxX = par1Block.maxX[this.thread_index];
            this.renderMinY = par1Block.minY[this.thread_index];
            this.renderMaxY = par1Block.maxY[this.thread_index];
            this.renderMinZ = par1Block.minZ[this.thread_index];
            this.renderMaxZ = par1Block.maxZ[this.thread_index];
            this.partialRenderBounds = this.minecraftRB.gameSettings.ambientOcclusion >= 2 && (this.renderMinX > 0.0 || this.renderMaxX < 1.0 || this.renderMinY > 0.0 || this.renderMaxY < 1.0 || this.renderMinZ > 0.0 || this.renderMaxZ < 1.0);
         }
      }

   }

   public void overrideBlockBounds(double par1, double par3, double par5, double par7, double par9, double par11) {
      this.renderMinX = par1;
      this.renderMaxX = par7;
      this.renderMinY = par3;
      this.renderMaxY = par9;
      this.renderMinZ = par5;
      this.renderMaxZ = par11;
      this.lockBlockBounds = true;
      this.partialRenderBounds = this.minecraftRB.gameSettings.ambientOcclusion >= 2 && (this.renderMinX > 0.0 || this.renderMaxX < 1.0 || this.renderMinY > 0.0 || this.renderMaxY < 1.0 || this.renderMinZ > 0.0 || this.renderMaxZ < 1.0);
   }

   public void unlockBlockBounds() {
      this.lockBlockBounds = false;
   }

   public void renderBlockUsingTexture(Block par1Block, int par2, int par3, int par4, Icon par5Icon) {
      this.setOverrideBlockTexture(par5Icon);
      this.renderBlockByRenderType(par1Block, par2, par3, par4);
      this.clearOverrideBlockTexture();
   }

   public void renderBlockAllFaces(Block par1Block, int par2, int par3, int par4) {
      this.renderAllFaces = true;
      this.renderBlockByRenderType(par1Block, par2, par3, par4);
      this.renderAllFaces = false;
   }

   public boolean renderBlockByRenderType(Block par1Block, int par2, int par3, int par4) {
      int var5 = par1Block.getRenderType();
      if (var5 == -1) {
         return false;
      } else {
         if (this.overrideBlockTexture != null && var5 == 22) {
            var5 = 0;
         }

         if (par1Block.isAlwaysStandardFormCube()) {
            this.setRenderBoundsForStandardFormBlock();
         } else {
            par1Block.setBlockBoundsBasedOnStateAndNeighbors(this.blockAccess, par2, par3, par4);
            this.setRenderBoundsForNonStandardFormBlock(par1Block);
         }
         int[] ints = new int[]{0, 4, 31, 1, 2, 20 ,11, 39, 5, 13, 9, 19, 23, 6, 3, 8, 7, 10, 27, 12, 30, 15, 37, 17, 21, 44, 25, 28, 38,
                 32, 29, 14, 36, 16, 18, 24, 35, 26, 34};

         for (int anInt : ints) {
            int renderType = par1Block.getRenderType();
            if (renderType != anInt) {
               return FMLRenderAccessLibrary.renderWorldBlock(this, blockAccess, par2, par3, par4, par1Block, renderType);
            }
         }

         return var5 == 0 ? this.renderStandardBlock(par1Block, par2, par3, par4) : (var5 == 4 ? this.renderBlockFluids(par1Block, par2, par3, par4) : (var5 == 31 ? this.renderBlockLog(par1Block, par2, par3, par4) : (var5 == 1 ? this.renderCrossedSquares(par1Block, par2, par3, par4) : (var5 == 2 ? this.renderBlockTorch(par1Block, par2, par3, par4) : (var5 == 20 ? this.renderBlockVine(par1Block, par2, par3, par4) : (var5 == 11 ? this.renderBlockFence((BlockFence)par1Block, par2, par3, par4) : (var5 == 39 ? this.renderBlockQuartz(par1Block, par2, par3, par4) : (var5 == 5 ? this.renderBlockRedstoneWire(par1Block, par2, par3, par4) : (var5 == 13 ? this.renderBlockCactus(par1Block, par2, par3, par4) : (var5 == 9 ? this.renderBlockMinecartTrack((BlockRailBase)par1Block, par2, par3, par4) : (var5 == 19 ? this.renderBlockStem(par1Block, par2, par3, par4) : (var5 == 23 ? this.renderBlockLilyPad(par1Block, par2, par3, par4) : (var5 == 6 ? this.renderBlockCrops(par1Block, par2, par3, par4) : (var5 == 3 ? this.renderBlockFire((BlockFire)par1Block, par2, par3, par4) : (var5 == 8 ? this.renderBlockLadder(par1Block, par2, par3, par4) : (var5 == 7 ? this.renderBlockDoor(par1Block, par2, par3, par4) : (var5 == 10 ? this.renderBlockStairs((BlockStairs)par1Block, par2, par3, par4) : (var5 == 27 ? this.renderBlockDragonEgg((BlockDragonEgg)par1Block, par2, par3, par4) : (var5 == 32 ? this.renderBlockWall((BlockWall)par1Block, par2, par3, par4) : (var5 == 12 ? this.renderBlockLever(par1Block, par2, par3, par4) : (var5 == 29 ? this.renderBlockTripWireSource(par1Block, par2, par3, par4) : (var5 == 30 ? this.renderBlockTripWire(par1Block, par2, par3, par4) : (var5 == 14 ? this.renderBlockBed(par1Block, par2, par3, par4) : (var5 == 15 ? this.renderBlockRepeater((BlockRedstoneRepeater)par1Block, par2, par3, par4) : (var5 == 36 ? this.renderBlockRedstoneLogic((BlockRedstoneLogic)par1Block, par2, par3, par4) : (var5 == 37 ? this.renderBlockComparator((BlockComparator)par1Block, par2, par3, par4) : (var5 == 16 ? this.renderPistonBase(par1Block, par2, par3, par4, false) : (var5 == 17 ? this.renderPistonExtension(par1Block, par2, par3, par4, true) : (var5 == 18 ? this.renderBlockPane((BlockPane)par1Block, par2, par3, par4) : (var5 == 21 ? this.renderBlockFenceGate((BlockFenceGate)par1Block, par2, par3, par4) : (var5 == 24 ? this.renderBlockCauldron((BlockCauldron)par1Block, par2, par3, par4) : (var5 == 33 ? this.renderBlockFlowerpot((BlockFlowerPot)par1Block, par2, par3, par4) : (var5 == 35 ? this.renderBlockAnvil((BlockAnvil)par1Block, par2, par3, par4) : (var5 == 25 ? this.renderBlockBrewingStand((BlockBrewingStand)par1Block, par2, par3, par4) : (var5 == 26 ? this.renderBlockEndPortalFrame((BlockEndPortalFrame)par1Block, par2, par3, par4) : (var5 == 28 ? this.renderBlockCocoa((BlockCocoa)par1Block, par2, par3, par4) : (var5 == 34 ? this.renderBlockBeacon((BlockBeacon)par1Block, par2, par3, par4) : (var5 == 38 ? this.renderBlockHopper((BlockHopper)par1Block, par2, par3, par4) : false))))))))))))))))))))))))))))))))))))));
      }
   }

   private boolean renderBlockEndPortalFrame(BlockEndPortalFrame par1BlockEndPortalFrame, int par2, int par3, int par4) {
      int var5 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      int var6 = var5 & 3;
      if (var6 == 0) {
         this.uvRotateTop = 3;
      } else if (var6 == 3) {
         this.uvRotateTop = 1;
      } else if (var6 == 1) {
         this.uvRotateTop = 2;
      }

      if (!BlockEndPortalFrame.isEnderEyeInserted(var5)) {
         this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0);
         this.renderStandardBlock(par1BlockEndPortalFrame, par2, par3, par4);
         this.uvRotateTop = 0;
         return true;
      } else {
         this.renderAllFaces = true;
         this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0);
         this.renderStandardBlock(par1BlockEndPortalFrame, par2, par3, par4);
         this.setOverrideBlockTexture(par1BlockEndPortalFrame.func_94398_p());
         this.setRenderBounds(0.25, 0.8125, 0.25, 0.75, 1.0, 0.75);
         this.renderStandardBlock(par1BlockEndPortalFrame, par2, par3, par4);
         this.renderAllFaces = false;
         this.clearOverrideBlockTexture();
         this.uvRotateTop = 0;
         return true;
      }
   }

   private boolean renderBlockBed(Block par1Block, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      int var6 = this.blockAccess.getBlockMetadata(par2, par3, par4);

      int var7 = par1Block.getBedDirection(blockAccess, par2, par3, par4);
      boolean var8 = par1Block.isBedFoot(blockAccess, par2, par3, par4);

      float var9 = 0.5F;
      float var10 = 1.0F;
      float var11 = 0.8F;
      float var12 = 0.6F;
      int var25 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);
      var5.setBrightness(var25);
      var5.setColorOpaque_F(var9, var9, var9);
      Icon var27 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 0);
      if (hasOverrideBlockTexture()) var27 = overrideBlockTexture; //BugFix Proper breaking texture on underside
      double var28 = (double)var27.getMinU();
      double var30 = (double)var27.getMaxU();
      double var32 = (double)var27.getMinV();
      double var34 = (double)var27.getMaxV();
      double var36 = (double)par2 + this.renderMinX;
      double var38 = (double)par2 + this.renderMaxX;
      double var40 = (double)par3 + this.renderMinY + 0.1875;
      double var42 = (double)par4 + this.renderMinZ;
      double var44 = (double)par4 + this.renderMaxZ;
      var5.addVertexWithUV(var36, var40, var44, var28, var34);
      var5.addVertexWithUV(var36, var40, var42, var28, var32);
      var5.addVertexWithUV(var38, var40, var42, var30, var32);
      var5.addVertexWithUV(var38, var40, var44, var30, var34);
      var5.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4));
      var5.setColorOpaque_F(var10, var10, var10);
      var27 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 1);
      if (hasOverrideBlockTexture()) var27 = overrideBlockTexture; //BugFix Proper breaking texture on underside
      var28 = (double)var27.getMinU();
      var30 = (double)var27.getMaxU();
      var32 = (double)var27.getMinV();
      var34 = (double)var27.getMaxV();
      var36 = var28;
      var38 = var30;
      var40 = var32;
      var42 = var32;
      var44 = var28;
      double var46 = var30;
      double var48 = var34;
      double var50 = var34;
      if (var7 == 0) {
         var38 = var28;
         var40 = var34;
         var44 = var30;
         var50 = var32;
      } else if (var7 == 2) {
         var36 = var30;
         var42 = var34;
         var46 = var28;
         var48 = var32;
      } else if (var7 == 3) {
         var36 = var30;
         var42 = var34;
         var46 = var28;
         var48 = var32;
         var38 = var28;
         var40 = var34;
         var44 = var30;
         var50 = var32;
      }

      double var52 = (double)par2 + this.renderMinX;
      double var54 = (double)par2 + this.renderMaxX;
      double var56 = (double)par3 + this.renderMaxY;
      double var58 = (double)par4 + this.renderMinZ;
      double var60 = (double)par4 + this.renderMaxZ;
      var5.addVertexWithUV(var54, var56, var60, var44, var48);
      var5.addVertexWithUV(var54, var56, var58, var36, var40);
      var5.addVertexWithUV(var52, var56, var58, var38, var42);
      var5.addVertexWithUV(var52, var56, var60, var46, var50);
      int var62 = Direction.directionToFacing[var7];
      if (var8) {
         var62 = Direction.directionToFacing[Direction.rotateOpposite[var7]];
      }

      byte var63 = 4;
      switch (var7) {
         case 0:
            var63 = 5;
            break;
         case 1:
            var63 = 3;
         case 2:
         default:
            break;
         case 3:
            var63 = 2;
      }

      if (var62 != 2 && (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 - 1, 2))) {
         var5.setBrightness(this.renderMinZ > 0.0 ? var25 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1));
         var5.setColorOpaque_F(var11, var11, var11);
         this.flipTexture = var63 == 2;
         this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 2));
      }

      if (var62 != 3 && (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 + 1, 3))) {
         var5.setBrightness(this.renderMaxZ < 1.0 ? var25 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1));
         var5.setColorOpaque_F(var11, var11, var11);
         this.flipTexture = var63 == 3;
         this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3));
      }

      if (var62 != 4 && (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 - 1, par3, par4, 4))) {
         var5.setBrightness(this.renderMinZ > 0.0 ? var25 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4));
         var5.setColorOpaque_F(var12, var12, var12);
         this.flipTexture = var63 == 4;
         this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 4));
      }

      if (var62 != 5 && (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 + 1, par3, par4, 5))) {
         var5.setBrightness(this.renderMaxZ < 1.0 ? var25 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4));
         var5.setColorOpaque_F(var12, var12, var12);
         this.flipTexture = var63 == 5;
         this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 5));
      }

      this.flipTexture = false;
      return true;
   }

   private boolean renderBlockBrewingStand(BlockBrewingStand par1BlockBrewingStand, int par2, int par3, int par4) {
      this.setRenderBounds(0.4375, 0.0, 0.4375, 0.5625, 0.875, 0.5625);
      this.renderStandardBlock(par1BlockBrewingStand, par2, par3, par4);
      this.setOverrideBlockTexture(par1BlockBrewingStand.getBrewingStandIcon());
      this.renderAllFaces = true;
      this.setRenderBounds(0.5625, 0.0, 0.3125, 0.9375, 0.125, 0.6875);
      this.renderStandardBlock(par1BlockBrewingStand, par2, par3, par4);
      this.setRenderBounds(0.125, 0.0, 0.0625, 0.5, 0.125, 0.4375);
      this.renderStandardBlock(par1BlockBrewingStand, par2, par3, par4);
      this.setRenderBounds(0.125, 0.0, 0.5625, 0.5, 0.125, 0.9375);
      this.renderStandardBlock(par1BlockBrewingStand, par2, par3, par4);
      this.renderAllFaces = false;
      this.clearOverrideBlockTexture();
      Tessellator var5 = Tessellator.instance;
      var5.setBrightness(par1BlockBrewingStand.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var6 = 1.0F;
      int var7 = par1BlockBrewingStand.colorMultiplier(this.blockAccess, par2, par3, par4);
      float var8 = (float)(var7 >> 16 & 255) / 255.0F;
      float var9 = (float)(var7 >> 8 & 255) / 255.0F;
      float var10 = (float)(var7 & 255) / 255.0F;
      if (EntityRenderer.anaglyphEnable) {
         float var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
         float var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
         float var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
         var8 = var11;
         var9 = var12;
         var10 = var13;
      }

      var5.setColorOpaque_F(var6 * var8, var6 * var9, var6 * var10);
      Icon var32 = this.overrideBlockTexture == null ? this.getBlockIconFromSideAndMetadata(par1BlockBrewingStand, 0, 0) : this.overrideBlockTexture;
      double var33 = (double)var32.getMinV();
      double var14 = (double)var32.getMaxV();
      int var16 = this.blockAccess.getBlockMetadata(par2, par3, par4);

      for(int var17 = 0; var17 < 3; ++var17) {
         double var18 = (double)var17 * Math.PI * 2.0 / 3.0 + 1.5707963267948966;
         double var20 = (double)var32.getInterpolatedU(8.0);
         double var22 = (double)var32.getMaxU();
         if ((var16 & 1 << var17) != 0) {
            var22 = (double)var32.getMinU();
         }

         double var24 = (double)par2 + 0.5;
         double var26 = (double)par2 + 0.5 + Math.sin(var18) * 8.0 / 16.0;
         double var28 = (double)par4 + 0.5;
         double var30 = (double)par4 + 0.5 + Math.cos(var18) * 8.0 / 16.0;
         var5.addVertexWithUV(var24, (double)(par3 + 1), var28, var20, var33);
         var5.addVertexWithUV(var24, (double)(par3 + 0), var28, var20, var14);
         var5.addVertexWithUV(var26, (double)(par3 + 0), var30, var22, var14);
         var5.addVertexWithUV(var26, (double)(par3 + 1), var30, var22, var33);
         var5.addVertexWithUV(var26, (double)(par3 + 1), var30, var22, var33);
         var5.addVertexWithUV(var26, (double)(par3 + 0), var30, var22, var14);
         var5.addVertexWithUV(var24, (double)(par3 + 0), var28, var20, var14);
         var5.addVertexWithUV(var24, (double)(par3 + 1), var28, var20, var33);
      }

      par1BlockBrewingStand.setBlockBoundsForItemRender(0);
      return true;
   }

   private boolean renderBlockCauldron(BlockCauldron par1BlockCauldron, int par2, int par3, int par4) {
      this.renderStandardBlock(par1BlockCauldron, par2, par3, par4);
      Tessellator var5 = Tessellator.instance;
      var5.setBrightness(par1BlockCauldron.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var6 = 1.0F;
      int var7 = par1BlockCauldron.colorMultiplier(this.blockAccess, par2, par3, par4);
      float var8 = (float)(var7 >> 16 & 255) / 255.0F;
      float var9 = (float)(var7 >> 8 & 255) / 255.0F;
      float var10 = (float)(var7 & 255) / 255.0F;
      float var12;
      if (EntityRenderer.anaglyphEnable) {
         float var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
         var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
         float var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
         var8 = var11;
         var9 = var12;
         var10 = var13;
      }

      var5.setColorOpaque_F(var6 * var8, var6 * var9, var6 * var10);
      Icon var16 = par1BlockCauldron.getBlockTextureFromSide(2);
      var12 = 0.125F;
      this.renderFaceXPos(par1BlockCauldron, (double)((float)par2 - 1.0F + var12), (double)par3, (double)par4, var16);
      this.renderFaceXNeg(par1BlockCauldron, (double)((float)par2 + 1.0F - var12), (double)par3, (double)par4, var16);
      this.renderFaceZPos(par1BlockCauldron, (double)par2, (double)par3, (double)((float)par4 - 1.0F + var12), var16);
      this.renderFaceZNeg(par1BlockCauldron, (double)par2, (double)par3, (double)((float)par4 + 1.0F - var12), var16);
      Icon var17 = BlockCauldron.getCauldronIcon("inner");
      this.renderFaceYPos(par1BlockCauldron, (double)par2, (double)((float)par3 - 1.0F + 0.25F), (double)par4, var17);
      this.renderFaceYNeg(par1BlockCauldron, (double)par2, (double)((float)par3 + 1.0F - 0.75F), (double)par4, var17);
      int var14 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      if (var14 > 0) {
         Icon var15 = BlockFluid.getFluidIcon("water_still");
         if (var14 > 3) {
            var14 = 3;
         }

         this.renderFaceYPos(par1BlockCauldron, (double)par2, (double)((float)par3 - 1.0F + (6.0F + (float)var14 * 3.0F) / 16.0F), (double)par4, var15);
      }

      return true;
   }

   private boolean renderBlockFlowerpot(BlockFlowerPot par1BlockFlowerPot, int par2, int par3, int par4) {
      this.renderStandardBlock(par1BlockFlowerPot, par2, par3, par4);
      Tessellator var5 = Tessellator.instance;
      var5.setBrightness(par1BlockFlowerPot.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var6 = 1.0F;
      int var7 = par1BlockFlowerPot.colorMultiplier(this.blockAccess, par2, par3, par4);
      Icon var8 = this.getBlockIconFromSide(par1BlockFlowerPot, 0);
      float var9 = (float)(var7 >> 16 & 255) / 255.0F;
      float var10 = (float)(var7 >> 8 & 255) / 255.0F;
      float var11 = (float)(var7 & 255) / 255.0F;
      float var12;
      float var14;
      if (EntityRenderer.anaglyphEnable) {
         var12 = (var9 * 30.0F + var10 * 59.0F + var11 * 11.0F) / 100.0F;
         float var13 = (var9 * 30.0F + var10 * 70.0F) / 100.0F;
         var14 = (var9 * 30.0F + var11 * 70.0F) / 100.0F;
         var9 = var12;
         var10 = var13;
         var11 = var14;
      }

      var5.setColorOpaque_F(var6 * var9, var6 * var10, var6 * var11);
      var12 = 0.1865F;
      this.renderFaceXPos(par1BlockFlowerPot, (double)((float)par2 - 0.5F + var12), (double)par3, (double)par4, var8);
      this.renderFaceXNeg(par1BlockFlowerPot, (double)((float)par2 + 0.5F - var12), (double)par3, (double)par4, var8);
      this.renderFaceZPos(par1BlockFlowerPot, (double)par2, (double)par3, (double)((float)par4 - 0.5F + var12), var8);
      this.renderFaceZNeg(par1BlockFlowerPot, (double)par2, (double)par3, (double)((float)par4 + 0.5F - var12), var8);
      this.renderFaceYPos(par1BlockFlowerPot, (double)par2, (double)((float)par3 - 0.5F + var12 + 0.1875F), (double)par4, this.getBlockIcon(Block.dirt));
      int var19 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      float var15;
      float var16;
      if (this.blockAccess.getBlockId(par2, par3, par4) == Block.flowerPotMulti.blockID) {
         if (var19 == 0) {
            return true;
         }

         var14 = 0.0F;
         var15 = 4.0F;
         var16 = 0.0F;
         var5.addTranslation(var14 / 16.0F, var15 / 16.0F, var16 / 16.0F);
         this.renderBlockByRenderType(Block.plantRed, par2, par3, par4);
         var5.addTranslation(-var14 / 16.0F, -var15 / 16.0F, -var16 / 16.0F);
      } else if (var19 != 0) {
         var14 = 0.0F;
         var15 = 4.0F;
         var16 = 0.0F;
         BlockPlant var17 = null;
         switch (var19) {
            case 1:
               var17 = Block.plantRed;
               break;
            case 2:
               var17 = Block.plantYellow;
            case 3:
            case 4:
            case 5:
            case 6:
            default:
               break;
            case 7:
               var17 = Block.mushroomRed;
               break;
            case 8:
               var17 = Block.mushroomBrown;
         }

         var5.addTranslation(var14 / 16.0F, var15 / 16.0F, var16 / 16.0F);
         if (var17 != null) {
            this.blockAccess.getWorld().setBlockMetadataWithNotify(par2, par3, par4, 0, 4);
            this.renderBlockByRenderType((Block)var17, par2, par3, par4);
            this.blockAccess.getWorld().setBlockMetadataWithNotify(par2, par3, par4, var19, 4);
         } else if (var19 == 9) {
            this.renderAllFaces = true;
            float var18 = 0.125F;
            this.setRenderBounds((double)(0.5F - var18), 0.0, (double)(0.5F - var18), (double)(0.5F + var18), 0.25, (double)(0.5F + var18));
            this.renderStandardBlock(Block.cactus, par2, par3, par4);
            this.setRenderBounds((double)(0.5F - var18), 0.25, (double)(0.5F - var18), (double)(0.5F + var18), 0.5, (double)(0.5F + var18));
            this.renderStandardBlock(Block.cactus, par2, par3, par4);
            this.setRenderBounds((double)(0.5F - var18), 0.5, (double)(0.5F - var18), (double)(0.5F + var18), 0.75, (double)(0.5F + var18));
            this.renderStandardBlock(Block.cactus, par2, par3, par4);
            this.renderAllFaces = false;
            this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         } else if (var19 == 3) {
            this.drawCrossedSquares(Block.sapling, 0, (double)par2, (double)par3, (double)par4, 0.75F);
         } else if (var19 == 5) {
            this.drawCrossedSquares(Block.sapling, 2, (double)par2, (double)par3, (double)par4, 0.75F);
         } else if (var19 == 4) {
            this.drawCrossedSquares(Block.sapling, 1, (double)par2, (double)par3, (double)par4, 0.75F);
         } else if (var19 == 6) {
            this.drawCrossedSquares(Block.sapling, 3, (double)par2, (double)par3, (double)par4, 0.75F);
         } else if (var19 == 11) {
            var7 = Block.tallGrass.colorMultiplier(this.blockAccess, par2, par3, par4);
            var9 = (float)(var7 >> 16 & 255) / 255.0F;
            var10 = (float)(var7 >> 8 & 255) / 255.0F;
            var11 = (float)(var7 & 255) / 255.0F;
            var5.setColorOpaque_F(var6 * var9, var6 * var10, var6 * var11);
            this.drawCrossedSquares(Block.tallGrass, 2, (double)par2, (double)par3, (double)par4, 0.75F);
         } else if (var19 == 10 || var19 == 12) {
            this.drawCrossedSquares(Block.deadBush, var19 == 12 ? 1 : 0, (double)par2, (double)par3, (double)par4, 0.75F);
         }

         var5.addTranslation(-var14 / 16.0F, -var15 / 16.0F, -var16 / 16.0F);
      }

      return true;
   }

   private boolean renderBlockAnvil(BlockAnvil par1BlockAnvil, int par2, int par3, int par4) {
      return this.renderBlockAnvilMetadata(par1BlockAnvil, par2, par3, par4, this.blockAccess.getBlockMetadata(par2, par3, par4));
   }

   public boolean renderBlockAnvilMetadata(BlockAnvil par1BlockAnvil, int par2, int par3, int par4, int par5) {
      Tessellator var6 = Tessellator.instance;
      var6.setBrightness(par1BlockAnvil.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var7 = 1.0F;
      int var8 = par1BlockAnvil.colorMultiplier(this.blockAccess, par2, par3, par4);
      float var9 = (float)(var8 >> 16 & 255) / 255.0F;
      float var10 = (float)(var8 >> 8 & 255) / 255.0F;
      float var11 = (float)(var8 & 255) / 255.0F;
      if (EntityRenderer.anaglyphEnable) {
         float var12 = (var9 * 30.0F + var10 * 59.0F + var11 * 11.0F) / 100.0F;
         float var13 = (var9 * 30.0F + var10 * 70.0F) / 100.0F;
         float var14 = (var9 * 30.0F + var11 * 70.0F) / 100.0F;
         var9 = var12;
         var10 = var13;
         var11 = var14;
      }

      var6.setColorOpaque_F(var7 * var9, var7 * var10, var7 * var11);
      return this.renderBlockAnvilOrient(par1BlockAnvil, par2, par3, par4, par5, false);
   }

   private boolean renderBlockAnvilOrient(BlockAnvil par1BlockAnvil, int par2, int par3, int par4, int par5, boolean par6) {
      int var7 = par6 ? 0 : par5 & 3;
      boolean var8 = false;
      float var9 = 0.0F;
      switch (var7) {
         case 0:
            this.uvRotateSouth = 2;
            this.uvRotateNorth = 1;
            this.uvRotateTop = 3;
            this.uvRotateBottom = 3;
            break;
         case 1:
            this.uvRotateEast = 1;
            this.uvRotateWest = 2;
            this.uvRotateTop = 2;
            this.uvRotateBottom = 1;
            var8 = true;
            break;
         case 2:
            this.uvRotateSouth = 1;
            this.uvRotateNorth = 2;
            break;
         case 3:
            this.uvRotateEast = 2;
            this.uvRotateWest = 1;
            this.uvRotateTop = 1;
            this.uvRotateBottom = 2;
            var8 = true;
      }

      var9 = this.renderBlockAnvilRotate(par1BlockAnvil, par2, par3, par4, 0, var9, 0.75F, 0.25F, 0.75F, var8, par6, par5);
      var9 = this.renderBlockAnvilRotate(par1BlockAnvil, par2, par3, par4, 1, var9, 0.5F, 0.0625F, 0.625F, var8, par6, par5);
      var9 = this.renderBlockAnvilRotate(par1BlockAnvil, par2, par3, par4, 2, var9, 0.25F, 0.3125F, 0.5F, var8, par6, par5);
      this.renderBlockAnvilRotate(par1BlockAnvil, par2, par3, par4, 3, var9, 0.625F, 0.375F, 1.0F, var8, par6, par5);
      this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      this.uvRotateEast = 0;
      this.uvRotateWest = 0;
      this.uvRotateSouth = 0;
      this.uvRotateNorth = 0;
      this.uvRotateTop = 0;
      this.uvRotateBottom = 0;
      return true;
   }

   private float renderBlockAnvilRotate(BlockAnvil par1BlockAnvil, int par2, int par3, int par4, int par5, float par6, float par7, float par8, float par9, boolean par10, boolean par11, int par12) {
      if (par10) {
         float var13 = par7;
         par7 = par9;
         par9 = var13;
      }

      par7 /= 2.0F;
      par9 /= 2.0F;
      par1BlockAnvil.field_82521_b = par5;
      this.setRenderBounds((double)(0.5F - par7), (double)par6, (double)(0.5F - par9), (double)(0.5F + par7), (double)(par6 + par8), (double)(0.5F + par9));
      if (par11) {
         Tessellator var14 = Tessellator.instance;
         var14.startDrawingQuads();
         var14.setNormal(0.0F, -1.0F, 0.0F);
         this.renderFaceYNeg(par1BlockAnvil, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1BlockAnvil, 0, par12));
         var14.draw();
         var14.startDrawingQuads();
         var14.setNormal(0.0F, 1.0F, 0.0F);
         this.renderFaceYPos(par1BlockAnvil, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1BlockAnvil, 1, par12));
         var14.draw();
         var14.startDrawingQuads();
         var14.setNormal(0.0F, 0.0F, -1.0F);
         this.renderFaceZNeg(par1BlockAnvil, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1BlockAnvil, 2, par12));
         var14.draw();
         var14.startDrawingQuads();
         var14.setNormal(0.0F, 0.0F, 1.0F);
         this.renderFaceZPos(par1BlockAnvil, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1BlockAnvil, 3, par12));
         var14.draw();
         var14.startDrawingQuads();
         var14.setNormal(-1.0F, 0.0F, 0.0F);
         this.renderFaceXNeg(par1BlockAnvil, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1BlockAnvil, 4, par12));
         var14.draw();
         var14.startDrawingQuads();
         var14.setNormal(1.0F, 0.0F, 0.0F);
         this.renderFaceXPos(par1BlockAnvil, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1BlockAnvil, 5, par12));
         var14.draw();
      } else {
         this.renderStandardBlock(par1BlockAnvil, par2, par3, par4);
      }

      return par6 + par8;
   }

   public boolean renderBlockTorch(Block par1Block, int par2, int par3, int par4) {
      int var5 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      Tessellator var6 = Tessellator.instance;
      var6.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      var6.setColorOpaque_F(1.0F, 1.0F, 1.0F);
      double var7 = 0.4000000059604645;
      double var9 = 0.5 - var7;
      double var11 = 0.20000000298023224;
      if (var5 == 1) {
         this.renderTorchAtAngle(par1Block, (double)par2 - var9, (double)par3 + var11, (double)par4, -var7, 0.0, 0);
      } else if (var5 == 2) {
         this.renderTorchAtAngle(par1Block, (double)par2 + var9, (double)par3 + var11, (double)par4, var7, 0.0, 0);
      } else if (var5 == 3) {
         this.renderTorchAtAngle(par1Block, (double)par2, (double)par3 + var11, (double)par4 - var9, 0.0, -var7, 0);
      } else if (var5 == 4) {
         this.renderTorchAtAngle(par1Block, (double)par2, (double)par3 + var11, (double)par4 + var9, 0.0, var7, 0);
      } else {
         this.renderTorchAtAngle(par1Block, (double)par2, (double)par3, (double)par4, 0.0, 0.0, 0);
      }

      return true;
   }

   private boolean renderBlockRepeater(BlockRedstoneRepeater par1BlockRedstoneRepeater, int par2, int par3, int par4) {
      int var5 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      int var6 = var5 & 3;
      int var7 = (var5 & 12) >> 2;
      Tessellator var8 = Tessellator.instance;
      var8.setBrightness(par1BlockRedstoneRepeater.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      var8.setColorOpaque_F(1.0F, 1.0F, 1.0F);
      double var9 = -0.1875;
      boolean var11 = par1BlockRedstoneRepeater.func_94476_e(this.blockAccess, par2, par3, par4, var5);
      double var12 = 0.0;
      double var14 = 0.0;
      double var16 = 0.0;
      double var18 = 0.0;
      switch (var6) {
         case 0:
            var18 = -0.3125;
            var14 = BlockRedstoneRepeater.repeaterTorchOffset[var7];
            break;
         case 1:
            var16 = 0.3125;
            var12 = -BlockRedstoneRepeater.repeaterTorchOffset[var7];
            break;
         case 2:
            var18 = 0.3125;
            var14 = -BlockRedstoneRepeater.repeaterTorchOffset[var7];
            break;
         case 3:
            var16 = -0.3125;
            var12 = BlockRedstoneRepeater.repeaterTorchOffset[var7];
      }

      if (!var11) {
         this.renderTorchAtAngle(par1BlockRedstoneRepeater, (double)par2 + var12, (double)par3 + var9, (double)par4 + var14, 0.0, 0.0, 0);
      } else {
         Icon var20 = this.getBlockIcon(Block.bedrock);
         this.setOverrideBlockTexture(var20);
         float var21 = 2.0F;
         float var22 = 14.0F;
         float var23 = 7.0F;
         float var24 = 9.0F;
         switch (var6) {
            case 1:
            case 3:
               var21 = 7.0F;
               var22 = 9.0F;
               var23 = 2.0F;
               var24 = 14.0F;
            case 0:
            case 2:
            default:
               this.setRenderBounds((double)(var21 / 16.0F + (float)var12), 0.125, (double)(var23 / 16.0F + (float)var14), (double)(var22 / 16.0F + (float)var12), 0.25, (double)(var24 / 16.0F + (float)var14));
               double var25 = (double)var20.getInterpolatedU((double)var21);
               double var27 = (double)var20.getInterpolatedV((double)var23);
               double var29 = (double)var20.getInterpolatedU((double)var22);
               double var31 = (double)var20.getInterpolatedV((double)var24);
               var8.addVertexWithUV((double)((float)par2 + var21 / 16.0F) + var12, (double)((float)par3 + 0.25F), (double)((float)par4 + var23 / 16.0F) + var14, var25, var27);
               var8.addVertexWithUV((double)((float)par2 + var21 / 16.0F) + var12, (double)((float)par3 + 0.25F), (double)((float)par4 + var24 / 16.0F) + var14, var25, var31);
               var8.addVertexWithUV((double)((float)par2 + var22 / 16.0F) + var12, (double)((float)par3 + 0.25F), (double)((float)par4 + var24 / 16.0F) + var14, var29, var31);
               var8.addVertexWithUV((double)((float)par2 + var22 / 16.0F) + var12, (double)((float)par3 + 0.25F), (double)((float)par4 + var23 / 16.0F) + var14, var29, var27);
               this.renderStandardBlock(par1BlockRedstoneRepeater, par2, par3, par4);
               this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
               this.clearOverrideBlockTexture();
         }
      }

      var8.setBrightness(par1BlockRedstoneRepeater.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      var8.setColorOpaque_F(1.0F, 1.0F, 1.0F);
      this.renderTorchAtAngle(par1BlockRedstoneRepeater, (double)par2 + var16, (double)par3 + var9, (double)par4 + var18, 0.0, 0.0, 0);
      this.renderBlockRedstoneLogic(par1BlockRedstoneRepeater, par2, par3, par4);
      return true;
   }

   private boolean renderBlockComparator(BlockComparator par1BlockComparator, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      var5.setBrightness(par1BlockComparator.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      var5.setColorOpaque_F(1.0F, 1.0F, 1.0F);
      int var6 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      int var7 = var6 & 3;
      double var8 = 0.0;
      double var10 = -0.1875;
      double var12 = 0.0;
      double var14 = 0.0;
      double var16 = 0.0;
      Icon var18;
      if (par1BlockComparator.func_94490_c(var6)) {
         var18 = Block.torchRedstoneActive.getBlockTextureFromSide(0);
      } else {
         var10 -= 0.1875;
         var18 = Block.torchRedstoneIdle.getBlockTextureFromSide(0);
      }

      switch (var7) {
         case 0:
            var12 = -0.3125;
            var16 = 1.0;
            break;
         case 1:
            var8 = 0.3125;
            var14 = -1.0;
            break;
         case 2:
            var12 = 0.3125;
            var16 = -1.0;
            break;
         case 3:
            var8 = -0.3125;
            var14 = 1.0;
      }

      this.renderTorchAtAngle(par1BlockComparator, (double)par2 + 0.25 * var14 + 0.1875 * var16, (double)((float)par3 - 0.1875F), (double)par4 + 0.25 * var16 + 0.1875 * var14, 0.0, 0.0, var6);
      this.renderTorchAtAngle(par1BlockComparator, (double)par2 + 0.25 * var14 + -0.1875 * var16, (double)((float)par3 - 0.1875F), (double)par4 + 0.25 * var16 + -0.1875 * var14, 0.0, 0.0, var6);
      this.setOverrideBlockTexture(var18);
      this.renderTorchAtAngle(par1BlockComparator, (double)par2 + var8, (double)par3 + var10, (double)par4 + var12, 0.0, 0.0, var6);
      this.clearOverrideBlockTexture();
      this.renderBlockRedstoneLogicMetadata(par1BlockComparator, par2, par3, par4, var7);
      return true;
   }

   private boolean renderBlockRedstoneLogic(BlockRedstoneLogic par1BlockRedstoneLogic, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      this.renderBlockRedstoneLogicMetadata(par1BlockRedstoneLogic, par2, par3, par4, this.blockAccess.getBlockMetadata(par2, par3, par4) & 3);
      return true;
   }

   private void renderBlockRedstoneLogicMetadata(BlockRedstoneLogic par1BlockRedstoneLogic, int par2, int par3, int par4, int par5) {
      this.renderStandardBlock(par1BlockRedstoneLogic, par2, par3, par4);
      Tessellator var6 = Tessellator.instance;
      var6.setBrightness(par1BlockRedstoneLogic.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      var6.setColorOpaque_F(1.0F, 1.0F, 1.0F);
      int var7 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      Icon var8 = this.getBlockIconFromSideAndMetadata(par1BlockRedstoneLogic, 1, var7);
      double var9 = (double)var8.getMinU();
      double var11 = (double)var8.getMaxU();
      double var13 = (double)var8.getMinV();
      double var15 = (double)var8.getMaxV();
      double var17 = 0.125;
      double var19 = (double)(par2 + 1);
      double var21 = (double)(par2 + 1);
      double var23 = (double)(par2 + 0);
      double var25 = (double)(par2 + 0);
      double var27 = (double)(par4 + 0);
      double var29 = (double)(par4 + 1);
      double var31 = (double)(par4 + 1);
      double var33 = (double)(par4 + 0);
      double var35 = (double)par3 + var17;
      if (par5 == 2) {
         var19 = var21 = (double)(par2 + 0);
         var23 = var25 = (double)(par2 + 1);
         var27 = var33 = (double)(par4 + 1);
         var29 = var31 = (double)(par4 + 0);
      } else if (par5 == 3) {
         var19 = var25 = (double)(par2 + 0);
         var21 = var23 = (double)(par2 + 1);
         var27 = var29 = (double)(par4 + 0);
         var31 = var33 = (double)(par4 + 1);
      } else if (par5 == 1) {
         var19 = var25 = (double)(par2 + 1);
         var21 = var23 = (double)(par2 + 0);
         var27 = var29 = (double)(par4 + 1);
         var31 = var33 = (double)(par4 + 0);
      }

      var6.addVertexWithUV(var25, var35, var33, var9, var13);
      var6.addVertexWithUV(var23, var35, var31, var9, var15);
      var6.addVertexWithUV(var21, var35, var29, var11, var15);
      var6.addVertexWithUV(var19, var35, var27, var11, var13);
   }

   public void renderPistonBaseAllFaces(Block par1Block, int par2, int par3, int par4) {
      this.renderAllFaces = true;
      this.renderPistonBase(par1Block, par2, par3, par4, true);
      this.renderAllFaces = false;
   }

   private boolean renderPistonBase(Block par1Block, int par2, int par3, int par4, boolean par5) {
      int var6 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      boolean var7 = par5 || (var6 & 8) != 0;
      int var8 = BlockPistonBase.getOrientation(var6);
      float var9 = 0.25F;
      if (var7) {
         switch (var8) {
            case 0:
               this.uvRotateEast = 3;
               this.uvRotateWest = 3;
               this.uvRotateSouth = 3;
               this.uvRotateNorth = 3;
               this.setRenderBounds(0.0, 0.25, 0.0, 1.0, 1.0, 1.0);
               break;
            case 1:
               this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
               break;
            case 2:
               this.uvRotateSouth = 1;
               this.uvRotateNorth = 2;
               this.setRenderBounds(0.0, 0.0, 0.25, 1.0, 1.0, 1.0);
               break;
            case 3:
               this.uvRotateSouth = 2;
               this.uvRotateNorth = 1;
               this.uvRotateTop = 3;
               this.uvRotateBottom = 3;
               this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.75);
               break;
            case 4:
               this.uvRotateEast = 1;
               this.uvRotateWest = 2;
               this.uvRotateTop = 2;
               this.uvRotateBottom = 1;
               this.setRenderBounds(0.25, 0.0, 0.0, 1.0, 1.0, 1.0);
               break;
            case 5:
               this.uvRotateEast = 2;
               this.uvRotateWest = 1;
               this.uvRotateTop = 1;
               this.uvRotateBottom = 2;
               this.setRenderBounds(0.0, 0.0, 0.0, 0.75, 1.0, 1.0);
         }

         ((BlockPistonBase)par1Block).func_96479_b((float)this.renderMinX, (float)this.renderMinY, (float)this.renderMinZ, (float)this.renderMaxX, (float)this.renderMaxY, (float)this.renderMaxZ);
         this.renderStandardBlock(par1Block, par2, par3, par4);
         this.uvRotateEast = 0;
         this.uvRotateWest = 0;
         this.uvRotateSouth = 0;
         this.uvRotateNorth = 0;
         this.uvRotateTop = 0;
         this.uvRotateBottom = 0;
         this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         ((BlockPistonBase)par1Block).func_96479_b((float)this.renderMinX, (float)this.renderMinY, (float)this.renderMinZ, (float)this.renderMaxX, (float)this.renderMaxY, (float)this.renderMaxZ);
      } else {
         switch (var8) {
            case 0:
               this.uvRotateEast = 3;
               this.uvRotateWest = 3;
               this.uvRotateSouth = 3;
               this.uvRotateNorth = 3;
            case 1:
            default:
               break;
            case 2:
               this.uvRotateSouth = 1;
               this.uvRotateNorth = 2;
               break;
            case 3:
               this.uvRotateSouth = 2;
               this.uvRotateNorth = 1;
               this.uvRotateTop = 3;
               this.uvRotateBottom = 3;
               break;
            case 4:
               this.uvRotateEast = 1;
               this.uvRotateWest = 2;
               this.uvRotateTop = 2;
               this.uvRotateBottom = 1;
               break;
            case 5:
               this.uvRotateEast = 2;
               this.uvRotateWest = 1;
               this.uvRotateTop = 1;
               this.uvRotateBottom = 2;
         }

         this.renderStandardBlock(par1Block, par2, par3, par4);
         this.uvRotateEast = 0;
         this.uvRotateWest = 0;
         this.uvRotateSouth = 0;
         this.uvRotateNorth = 0;
         this.uvRotateTop = 0;
         this.uvRotateBottom = 0;
      }

      return true;
   }

   private void renderPistonRodUD(double par1, double par3, double par5, double par7, double par9, double par11, float par13, double par14) {
      Icon var16 = this.overrideBlockTexture == null ? BlockPistonBase.getPistonBaseIcon("piston_side") : this.overrideBlockTexture;
      Tessellator var17 = Tessellator.instance;
      double var18 = (double)var16.getMinU();
      double var20 = (double)var16.getMinV();
      double var22 = (double)var16.getInterpolatedU(par14);
      double var24 = (double)var16.getInterpolatedV(4.0);
      var17.setColorOpaque_F(par13, par13, par13);
      var17.addVertexWithUV(par1, par7, par9, var22, var20);
      var17.addVertexWithUV(par1, par5, par9, var18, var20);
      var17.addVertexWithUV(par3, par5, par11, var18, var24);
      var17.addVertexWithUV(par3, par7, par11, var22, var24);
   }

   private void renderPistonRodSN(double par1, double par3, double par5, double par7, double par9, double par11, float par13, double par14) {
      Icon var16 = this.overrideBlockTexture == null ? BlockPistonBase.getPistonBaseIcon("piston_side") : this.overrideBlockTexture;
      Tessellator var17 = Tessellator.instance;
      double var18 = (double)var16.getMinU();
      double var20 = (double)var16.getMinV();
      double var22 = (double)var16.getInterpolatedU(par14);
      double var24 = (double)var16.getInterpolatedV(4.0);
      var17.setColorOpaque_F(par13, par13, par13);
      var17.addVertexWithUV(par1, par5, par11, var22, var20);
      var17.addVertexWithUV(par1, par5, par9, var18, var20);
      var17.addVertexWithUV(par3, par7, par9, var18, var24);
      var17.addVertexWithUV(par3, par7, par11, var22, var24);
   }

   private void renderPistonRodEW(double par1, double par3, double par5, double par7, double par9, double par11, float par13, double par14) {
      Icon var16 = this.overrideBlockTexture == null ? BlockPistonBase.getPistonBaseIcon("piston_side") : this.overrideBlockTexture;
      Tessellator var17 = Tessellator.instance;
      double var18 = (double)var16.getMinU();
      double var20 = (double)var16.getMinV();
      double var22 = (double)var16.getInterpolatedU(par14);
      double var24 = (double)var16.getInterpolatedV(4.0);
      var17.setColorOpaque_F(par13, par13, par13);
      var17.addVertexWithUV(par3, par5, par9, var22, var20);
      var17.addVertexWithUV(par1, par5, par9, var18, var20);
      var17.addVertexWithUV(par1, par7, par11, var18, var24);
      var17.addVertexWithUV(par3, par7, par11, var22, var24);
   }

   public void renderPistonExtensionAllFaces(Block par1Block, int par2, int par3, int par4, boolean par5) {
      this.renderAllFaces = true;
      this.renderPistonExtension(par1Block, par2, par3, par4, par5);
      this.renderAllFaces = false;
   }

   private boolean renderPistonExtension(Block par1Block, int par2, int par3, int par4, boolean par5) {
      int var6 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      int var7 = BlockPistonExtension.getDirectionMeta(var6);
      float var8 = 0.25F;
      float var9 = 0.375F;
      float var10 = 0.625F;
      float var11 = par1Block.getBlockBrightness(this.blockAccess, par2, par3, par4);
      float var12 = par5 ? 1.0F : 0.5F;
      double var13 = par5 ? 16.0 : 8.0;
      Tessellator tessellator = Tessellator.instance;
      switch (var7) {
         case 0:
            this.uvRotateEast = 3;
            this.uvRotateWest = 3;
            this.uvRotateSouth = 3;
            this.uvRotateNorth = 3;
            this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.25, 1.0);
            this.renderStandardBlock(par1Block, par2, par3, par4);
            tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
            this.renderPistonRodUD((double)((float)par2 + 0.375F), (double)((float)par2 + 0.625F), (double)((float)par3 + 0.25F), (double)((float)par3 + 0.25F + var12), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.625F), var11 * 0.8F, var13);
            this.renderPistonRodUD((double)((float)par2 + 0.625F), (double)((float)par2 + 0.375F), (double)((float)par3 + 0.25F), (double)((float)par3 + 0.25F + var12), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.375F), var11 * 0.8F, var13);
            this.renderPistonRodUD((double)((float)par2 + 0.375F), (double)((float)par2 + 0.375F), (double)((float)par3 + 0.25F), (double)((float)par3 + 0.25F + var12), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.625F), var11 * 0.6F, var13);
            this.renderPistonRodUD((double)((float)par2 + 0.625F), (double)((float)par2 + 0.625F), (double)((float)par3 + 0.25F), (double)((float)par3 + 0.25F + var12), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.375F), var11 * 0.6F, var13);
            break;
         case 1:
            this.setRenderBounds(0.0, 0.75, 0.0, 1.0, 1.0, 1.0);
            this.renderStandardBlock(par1Block, par2, par3, par4);
            tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
            this.renderPistonRodUD((double)((float)par2 + 0.375F), (double)((float)par2 + 0.625F), (double)((float)par3 - 0.25F + 1.0F - var12), (double)((float)par3 - 0.25F + 1.0F), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.625F), var11 * 0.8F, var13);
            this.renderPistonRodUD((double)((float)par2 + 0.625F), (double)((float)par2 + 0.375F), (double)((float)par3 - 0.25F + 1.0F - var12), (double)((float)par3 - 0.25F + 1.0F), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.375F), var11 * 0.8F, var13);
            this.renderPistonRodUD((double)((float)par2 + 0.375F), (double)((float)par2 + 0.375F), (double)((float)par3 - 0.25F + 1.0F - var12), (double)((float)par3 - 0.25F + 1.0F), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.625F), var11 * 0.6F, var13);
            this.renderPistonRodUD((double)((float)par2 + 0.625F), (double)((float)par2 + 0.625F), (double)((float)par3 - 0.25F + 1.0F - var12), (double)((float)par3 - 0.25F + 1.0F), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.375F), var11 * 0.6F, var13);
            break;
         case 2:
            this.uvRotateSouth = 1;
            this.uvRotateNorth = 2;
            this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.25);
            this.renderStandardBlock(par1Block, par2, par3, par4);
            tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
            this.renderPistonRodSN((double)((float)par2 + 0.375F), (double)((float)par2 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par4 + 0.25F), (double)((float)par4 + 0.25F + var12), var11 * 0.6F, var13);
            this.renderPistonRodSN((double)((float)par2 + 0.625F), (double)((float)par2 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par4 + 0.25F), (double)((float)par4 + 0.25F + var12), var11 * 0.6F, var13);
            this.renderPistonRodSN((double)((float)par2 + 0.375F), (double)((float)par2 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.375F), (double)((float)par4 + 0.25F), (double)((float)par4 + 0.25F + var12), var11 * 0.5F, var13);
            this.renderPistonRodSN((double)((float)par2 + 0.625F), (double)((float)par2 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.625F), (double)((float)par4 + 0.25F), (double)((float)par4 + 0.25F + var12), var11, var13);
            break;
         case 3:
            this.uvRotateSouth = 2;
            this.uvRotateNorth = 1;
            this.uvRotateTop = 3;
            this.uvRotateBottom = 3;
            this.setRenderBounds(0.0, 0.0, 0.75, 1.0, 1.0, 1.0);
            this.renderStandardBlock(par1Block, par2, par3, par4);
            tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
            this.renderPistonRodSN((double)((float)par2 + 0.375F), (double)((float)par2 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par4 - 0.25F + 1.0F - var12), (double)((float)par4 - 0.25F + 1.0F), var11 * 0.6F, var13);
            this.renderPistonRodSN((double)((float)par2 + 0.625F), (double)((float)par2 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par4 - 0.25F + 1.0F - var12), (double)((float)par4 - 0.25F + 1.0F), var11 * 0.6F, var13);
            this.renderPistonRodSN((double)((float)par2 + 0.375F), (double)((float)par2 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.375F), (double)((float)par4 - 0.25F + 1.0F - var12), (double)((float)par4 - 0.25F + 1.0F), var11 * 0.5F, var13);
            this.renderPistonRodSN((double)((float)par2 + 0.625F), (double)((float)par2 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.625F), (double)((float)par4 - 0.25F + 1.0F - var12), (double)((float)par4 - 0.25F + 1.0F), var11, var13);
            break;
         case 4:
            this.uvRotateEast = 1;
            this.uvRotateWest = 2;
            this.uvRotateTop = 2;
            this.uvRotateBottom = 1;
            this.setRenderBounds(0.0, 0.0, 0.0, 0.25, 1.0, 1.0);
            this.renderStandardBlock(par1Block, par2, par3, par4);
            tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
            this.renderPistonRodEW((double)((float)par2 + 0.25F), (double)((float)par2 + 0.25F + var12), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.375F), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.375F), var11 * 0.5F, var13);
            this.renderPistonRodEW((double)((float)par2 + 0.25F), (double)((float)par2 + 0.25F + var12), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.625F), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.625F), var11, var13);
            this.renderPistonRodEW((double)((float)par2 + 0.25F), (double)((float)par2 + 0.25F + var12), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.375F), var11 * 0.6F, var13);
            this.renderPistonRodEW((double)((float)par2 + 0.25F), (double)((float)par2 + 0.25F + var12), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.625F), var11 * 0.6F, var13);
            break;
         case 5:
            this.uvRotateEast = 2;
            this.uvRotateWest = 1;
            this.uvRotateTop = 1;
            this.uvRotateBottom = 2;
            this.setRenderBounds(0.75, 0.0, 0.0, 1.0, 1.0, 1.0);
            this.renderStandardBlock(par1Block, par2, par3, par4);
            tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
            this.renderPistonRodEW((double)((float)par2 - 0.25F + 1.0F - var12), (double)((float)par2 - 0.25F + 1.0F), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.375F), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.375F), var11 * 0.5F, var13);
            this.renderPistonRodEW((double)((float)par2 - 0.25F + 1.0F - var12), (double)((float)par2 - 0.25F + 1.0F), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.625F), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.625F), var11, var13);
            this.renderPistonRodEW((double)((float)par2 - 0.25F + 1.0F - var12), (double)((float)par2 - 0.25F + 1.0F), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.375F), var11 * 0.6F, var13);
            this.renderPistonRodEW((double)((float)par2 - 0.25F + 1.0F - var12), (double)((float)par2 - 0.25F + 1.0F), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.625F), var11 * 0.6F, var13);
      }

      this.uvRotateEast = 0;
      this.uvRotateWest = 0;
      this.uvRotateSouth = 0;
      this.uvRotateNorth = 0;
      this.uvRotateTop = 0;
      this.uvRotateBottom = 0;
      this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      return true;
   }

   public boolean renderBlockLever(Block par1Block, int par2, int par3, int par4) {
      int var5 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      int var6 = var5 & 7;
      boolean var7 = (var5 & 8) > 0;
      Tessellator var8 = Tessellator.instance;
      boolean var9 = this.overrideBlockTexture != null;
      if (!var9) {
         this.setOverrideBlockTexture(this.getBlockIcon(Block.cobblestone));
      }

      float var10 = 0.25F;
      float var11 = 0.1875F;
      float var12 = 0.1875F;
      if (var6 == 5) {
         this.setRenderBounds((double)(0.5F - var11), 0.0, (double)(0.5F - var10), (double)(0.5F + var11), (double)var12, (double)(0.5F + var10));
      } else if (var6 == 6) {
         this.setRenderBounds((double)(0.5F - var10), 0.0, (double)(0.5F - var11), (double)(0.5F + var10), (double)var12, (double)(0.5F + var11));
      } else if (var6 == 4) {
         this.setRenderBounds((double)(0.5F - var11), (double)(0.5F - var10), (double)(1.0F - var12), (double)(0.5F + var11), (double)(0.5F + var10), 1.0);
      } else if (var6 == 3) {
         this.setRenderBounds((double)(0.5F - var11), (double)(0.5F - var10), 0.0, (double)(0.5F + var11), (double)(0.5F + var10), (double)var12);
      } else if (var6 == 2) {
         this.setRenderBounds((double)(1.0F - var12), (double)(0.5F - var10), (double)(0.5F - var11), 1.0, (double)(0.5F + var10), (double)(0.5F + var11));
      } else if (var6 == 1) {
         this.setRenderBounds(0.0, (double)(0.5F - var10), (double)(0.5F - var11), (double)var12, (double)(0.5F + var10), (double)(0.5F + var11));
      } else if (var6 == 0) {
         this.setRenderBounds((double)(0.5F - var10), (double)(1.0F - var12), (double)(0.5F - var11), (double)(0.5F + var10), 1.0, (double)(0.5F + var11));
      } else if (var6 == 7) {
         this.setRenderBounds((double)(0.5F - var11), (double)(1.0F - var12), (double)(0.5F - var10), (double)(0.5F + var11), 1.0, (double)(0.5F + var10));
      }

      this.renderStandardBlock(par1Block, par2, par3, par4);
      if (!var9) {
         this.clearOverrideBlockTexture();
      }

      var8.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var13 = 1.0F;
      if (Block.lightValue[par1Block.blockID] > 0) {
         var13 = 1.0F;
      }

      var8.setColorOpaque_F(var13, var13, var13);
      Icon var14 = this.overrideBlockTexture == null ? this.getBlockIconFromSide(par1Block, 0) : this.overrideBlockTexture;
      double var15 = (double)var14.getMinU();
      double var17 = (double)var14.getMinV();
      double var19 = (double)var14.getMaxU();
      double var21 = (double)var14.getMaxV();
      Vec3[] var23 = new Vec3[8];
      float var24 = 0.0625F;
      float var25 = 0.0625F;
      float var26 = 0.625F;
      var23[0] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-var24), 0.0, (double)(-var25));
      var23[1] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)var24, 0.0, (double)(-var25));
      var23[2] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)var24, 0.0, (double)var25);
      var23[3] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-var24), 0.0, (double)var25);
      var23[4] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-var24), (double)var26, (double)(-var25));
      var23[5] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)var24, (double)var26, (double)(-var25));
      var23[6] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)var24, (double)var26, (double)var25);
      var23[7] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-var24), (double)var26, (double)var25);

      for(int var27 = 0; var27 < 8; ++var27) {
         if (var7) {
            var23[var27].zCoord -= 0.0625;
            var23[var27].rotateAroundX(0.69813174F);
         } else {
            var23[var27].zCoord += 0.0625;
            var23[var27].rotateAroundX(-0.69813174F);
         }

         if (var6 == 0 || var6 == 7) {
            var23[var27].rotateAroundZ(3.1415927F);
         }

         if (var6 == 6 || var6 == 0) {
            var23[var27].rotateAroundY(1.5707964F);
         }

         if (var6 > 0 && var6 < 5) {
            var23[var27].yCoord -= 0.375;
            var23[var27].rotateAroundX(1.5707964F);
            if (var6 == 4) {
               var23[var27].rotateAroundY(0.0F);
            }

            if (var6 == 3) {
               var23[var27].rotateAroundY(3.1415927F);
            }

            if (var6 == 2) {
               var23[var27].rotateAroundY(1.5707964F);
            }

            if (var6 == 1) {
               var23[var27].rotateAroundY(-1.5707964F);
            }

            var23[var27].xCoord += (double)par2 + 0.5;
            var23[var27].yCoord += (double)((float)par3 + 0.5F);
            var23[var27].zCoord += (double)par4 + 0.5;
         } else if (var6 != 0 && var6 != 7) {
            var23[var27].xCoord += (double)par2 + 0.5;
            var23[var27].yCoord += (double)((float)par3 + 0.125F);
            var23[var27].zCoord += (double)par4 + 0.5;
         } else {
            var23[var27].xCoord += (double)par2 + 0.5;
            var23[var27].yCoord += (double)((float)par3 + 0.875F);
            var23[var27].zCoord += (double)par4 + 0.5;
         }
      }

      Vec3 var32 = null;
      Vec3 var28 = null;
      Vec3 var29 = null;
      Vec3 var30 = null;

      for(int var31 = 0; var31 < 6; ++var31) {
         if (var31 == 0) {
            var15 = (double)var14.getInterpolatedU(7.0);
            var17 = (double)var14.getInterpolatedV(6.0);
            var19 = (double)var14.getInterpolatedU(9.0);
            var21 = (double)var14.getInterpolatedV(8.0);
         } else if (var31 == 2) {
            var15 = (double)var14.getInterpolatedU(7.0);
            var17 = (double)var14.getInterpolatedV(6.0);
            var19 = (double)var14.getInterpolatedU(9.0);
            var21 = (double)var14.getMaxV();
         }

         if (var31 == 0) {
            var32 = var23[0];
            var28 = var23[1];
            var29 = var23[2];
            var30 = var23[3];
         } else if (var31 == 1) {
            var32 = var23[7];
            var28 = var23[6];
            var29 = var23[5];
            var30 = var23[4];
         } else if (var31 == 2) {
            var32 = var23[1];
            var28 = var23[0];
            var29 = var23[4];
            var30 = var23[5];
         } else if (var31 == 3) {
            var32 = var23[2];
            var28 = var23[1];
            var29 = var23[5];
            var30 = var23[6];
         } else if (var31 == 4) {
            var32 = var23[3];
            var28 = var23[2];
            var29 = var23[6];
            var30 = var23[7];
         } else if (var31 == 5) {
            var32 = var23[0];
            var28 = var23[3];
            var29 = var23[7];
            var30 = var23[4];
         }

         var8.addVertexWithUV(var32.xCoord, var32.yCoord, var32.zCoord, var15, var21);
         var8.addVertexWithUV(var28.xCoord, var28.yCoord, var28.zCoord, var19, var21);
         var8.addVertexWithUV(var29.xCoord, var29.yCoord, var29.zCoord, var19, var17);
         var8.addVertexWithUV(var30.xCoord, var30.yCoord, var30.zCoord, var15, var17);
      }

      return true;
   }

   public boolean renderBlockTripWireSource(Block par1Block, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      int var6 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      int var7 = var6 & 3;
      boolean var8 = (var6 & 4) == 4;
      boolean var9 = (var6 & 8) == 8;
      boolean var10 = !this.blockAccess.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4);
      boolean var11 = this.overrideBlockTexture != null;
      if (!var11) {
         this.setOverrideBlockTexture(this.getBlockIcon(Block.planks));
      }

      float var12 = 0.25F;
      float var13 = 0.125F;
      float var14 = 0.125F;
      float var15 = 0.3F - var12;
      float var16 = 0.3F + var12;
      if (var7 == 2) {
         this.setRenderBounds((double)(0.5F - var13), (double)var15, (double)(1.0F - var14), (double)(0.5F + var13), (double)var16, 1.0);
      } else if (var7 == 0) {
         this.setRenderBounds((double)(0.5F - var13), (double)var15, 0.0, (double)(0.5F + var13), (double)var16, (double)var14);
      } else if (var7 == 1) {
         this.setRenderBounds((double)(1.0F - var14), (double)var15, (double)(0.5F - var13), 1.0, (double)var16, (double)(0.5F + var13));
      } else if (var7 == 3) {
         this.setRenderBounds(0.0, (double)var15, (double)(0.5F - var13), (double)var14, (double)var16, (double)(0.5F + var13));
      }

      this.renderStandardBlock(par1Block, par2, par3, par4);
      if (!var11) {
         this.clearOverrideBlockTexture();
      }

      var5.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var17 = 1.0F;
      if (Block.lightValue[par1Block.blockID] > 0) {
         var17 = 1.0F;
      }

      var5.setColorOpaque_F(var17, var17, var17);
      Icon var18 = this.overrideBlockTexture == null ? this.getBlockIconFromSide(par1Block, 0) : this.overrideBlockTexture;
      double var19 = (double)var18.getMinU();
      double var21 = (double)var18.getMinV();
      double var23 = (double)var18.getMaxU();
      double var25 = (double)var18.getMaxV();
      Vec3[] var27 = new Vec3[8];
      float var28 = 0.046875F;
      float var29 = 0.046875F;
      float var30 = 0.3125F;
      var27[0] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-var28), 0.0, (double)(-var29));
      var27[1] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)var28, 0.0, (double)(-var29));
      var27[2] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)var28, 0.0, (double)var29);
      var27[3] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-var28), 0.0, (double)var29);
      var27[4] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-var28), (double)var30, (double)(-var29));
      var27[5] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)var28, (double)var30, (double)(-var29));
      var27[6] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)var28, (double)var30, (double)var29);
      var27[7] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-var28), (double)var30, (double)var29);

      for(int var31 = 0; var31 < 8; ++var31) {
         var27[var31].zCoord += 0.0625;
         if (var9) {
            var27[var31].rotateAroundX(0.5235988F);
            var27[var31].yCoord -= 0.4375;
         } else if (var8) {
            var27[var31].rotateAroundX(0.08726647F);
            var27[var31].yCoord -= 0.4375;
         } else {
            var27[var31].rotateAroundX(-0.69813174F);
            var27[var31].yCoord -= 0.375;
         }

         var27[var31].rotateAroundX(1.5707964F);
         if (var7 == 2) {
            var27[var31].rotateAroundY(0.0F);
         }

         if (var7 == 0) {
            var27[var31].rotateAroundY(3.1415927F);
         }

         if (var7 == 1) {
            var27[var31].rotateAroundY(1.5707964F);
         }

         if (var7 == 3) {
            var27[var31].rotateAroundY(-1.5707964F);
         }

         var27[var31].xCoord += (double)par2 + 0.5;
         var27[var31].yCoord += (double)((float)par3 + 0.3125F);
         var27[var31].zCoord += (double)par4 + 0.5;
      }

      Vec3 var62 = null;
      Vec3 var32 = null;
      Vec3 var33 = null;
      Vec3 var34 = null;
      byte var35 = 7;
      byte var36 = 9;
      byte var37 = 9;
      byte var38 = 16;

      for(int var39 = 0; var39 < 6; ++var39) {
         if (var39 == 0) {
            var62 = var27[0];
            var32 = var27[1];
            var33 = var27[2];
            var34 = var27[3];
            var19 = (double)var18.getInterpolatedU((double)var35);
            var21 = (double)var18.getInterpolatedV((double)var37);
            var23 = (double)var18.getInterpolatedU((double)var36);
            var25 = (double)var18.getInterpolatedV((double)(var37 + 2));
         } else if (var39 == 1) {
            var62 = var27[7];
            var32 = var27[6];
            var33 = var27[5];
            var34 = var27[4];
         } else if (var39 == 2) {
            var62 = var27[1];
            var32 = var27[0];
            var33 = var27[4];
            var34 = var27[5];
            var19 = (double)var18.getInterpolatedU((double)var35);
            var21 = (double)var18.getInterpolatedV((double)var37);
            var23 = (double)var18.getInterpolatedU((double)var36);
            var25 = (double)var18.getInterpolatedV((double)var38);
         } else if (var39 == 3) {
            var62 = var27[2];
            var32 = var27[1];
            var33 = var27[5];
            var34 = var27[6];
         } else if (var39 == 4) {
            var62 = var27[3];
            var32 = var27[2];
            var33 = var27[6];
            var34 = var27[7];
         } else if (var39 == 5) {
            var62 = var27[0];
            var32 = var27[3];
            var33 = var27[7];
            var34 = var27[4];
         }

         var5.addVertexWithUV(var62.xCoord, var62.yCoord, var62.zCoord, var19, var25);
         var5.addVertexWithUV(var32.xCoord, var32.yCoord, var32.zCoord, var23, var25);
         var5.addVertexWithUV(var33.xCoord, var33.yCoord, var33.zCoord, var23, var21);
         var5.addVertexWithUV(var34.xCoord, var34.yCoord, var34.zCoord, var19, var21);
      }

      float var63 = 0.09375F;
      float var40 = 0.09375F;
      float var41 = 0.03125F;
      var27[0] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-var63), 0.0, (double)(-var40));
      var27[1] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)var63, 0.0, (double)(-var40));
      var27[2] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)var63, 0.0, (double)var40);
      var27[3] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-var63), 0.0, (double)var40);
      var27[4] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-var63), (double)var41, (double)(-var40));
      var27[5] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)var63, (double)var41, (double)(-var40));
      var27[6] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)var63, (double)var41, (double)var40);
      var27[7] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-var63), (double)var41, (double)var40);

      for(int var42 = 0; var42 < 8; ++var42) {
         var27[var42].zCoord += 0.21875;
         if (var9) {
            var27[var42].yCoord -= 0.09375;
            var27[var42].zCoord -= 0.1625;
            var27[var42].rotateAroundX(0.0F);
         } else if (var8) {
            var27[var42].yCoord += 0.015625;
            var27[var42].zCoord -= 0.171875;
            var27[var42].rotateAroundX(0.17453294F);
         } else {
            var27[var42].rotateAroundX(0.87266463F);
         }

         if (var7 == 2) {
            var27[var42].rotateAroundY(0.0F);
         }

         if (var7 == 0) {
            var27[var42].rotateAroundY(3.1415927F);
         }

         if (var7 == 1) {
            var27[var42].rotateAroundY(1.5707964F);
         }

         if (var7 == 3) {
            var27[var42].rotateAroundY(-1.5707964F);
         }

         var27[var42].xCoord += (double)par2 + 0.5;
         var27[var42].yCoord += (double)((float)par3 + 0.3125F);
         var27[var42].zCoord += (double)par4 + 0.5;
      }

      byte var65 = 5;
      byte var43 = 11;
      byte var44 = 3;
      byte var45 = 9;

      for(int var46 = 0; var46 < 6; ++var46) {
         if (var46 == 0) {
            var62 = var27[0];
            var32 = var27[1];
            var33 = var27[2];
            var34 = var27[3];
            var19 = (double)var18.getInterpolatedU((double)var65);
            var21 = (double)var18.getInterpolatedV((double)var44);
            var23 = (double)var18.getInterpolatedU((double)var43);
            var25 = (double)var18.getInterpolatedV((double)var45);
         } else if (var46 == 1) {
            var62 = var27[7];
            var32 = var27[6];
            var33 = var27[5];
            var34 = var27[4];
         } else if (var46 == 2) {
            var62 = var27[1];
            var32 = var27[0];
            var33 = var27[4];
            var34 = var27[5];
            var19 = (double)var18.getInterpolatedU((double)var65);
            var21 = (double)var18.getInterpolatedV((double)var44);
            var23 = (double)var18.getInterpolatedU((double)var43);
            var25 = (double)var18.getInterpolatedV((double)(var44 + 2));
         } else if (var46 == 3) {
            var62 = var27[2];
            var32 = var27[1];
            var33 = var27[5];
            var34 = var27[6];
         } else if (var46 == 4) {
            var62 = var27[3];
            var32 = var27[2];
            var33 = var27[6];
            var34 = var27[7];
         } else if (var46 == 5) {
            var62 = var27[0];
            var32 = var27[3];
            var33 = var27[7];
            var34 = var27[4];
         }

         var5.addVertexWithUV(var62.xCoord, var62.yCoord, var62.zCoord, var19, var25);
         var5.addVertexWithUV(var32.xCoord, var32.yCoord, var32.zCoord, var23, var25);
         var5.addVertexWithUV(var33.xCoord, var33.yCoord, var33.zCoord, var23, var21);
         var5.addVertexWithUV(var34.xCoord, var34.yCoord, var34.zCoord, var19, var21);
      }

      if (var8) {
         double var64 = var27[0].yCoord;
         float var48 = 0.03125F;
         float var49 = 0.5F - var48 / 2.0F;
         float var50 = var49 + var48;
         Icon var51 = this.getBlockIcon(Block.tripWire);
         double var52 = (double)var18.getMinU();
         double var54 = (double)var18.getInterpolatedV(var8 ? 2.0 : 0.0);
         double var56 = (double)var18.getMaxU();
         double var58 = (double)var18.getInterpolatedV(var8 ? 4.0 : 2.0);
         double var60 = (double)(var10 ? 3.5F : 1.5F) / 16.0;
         var17 = par1Block.getBlockBrightness(this.blockAccess, par2, par3, par4) * 0.75F;
         var5.setColorOpaque_F(var17, var17, var17);
         if (var7 == 2) {
            var5.addVertexWithUV((double)((float)par2 + var49), (double)par3 + var60, (double)par4 + 0.25, var52, var54);
            var5.addVertexWithUV((double)((float)par2 + var50), (double)par3 + var60, (double)par4 + 0.25, var52, var58);
            var5.addVertexWithUV((double)((float)par2 + var50), (double)par3 + var60, (double)par4, var56, var58);
            var5.addVertexWithUV((double)((float)par2 + var49), (double)par3 + var60, (double)par4, var56, var54);
            var5.addVertexWithUV((double)((float)par2 + var49), var64, (double)par4 + 0.5, var52, var54);
            var5.addVertexWithUV((double)((float)par2 + var50), var64, (double)par4 + 0.5, var52, var58);
            var5.addVertexWithUV((double)((float)par2 + var50), (double)par3 + var60, (double)par4 + 0.25, var56, var58);
            var5.addVertexWithUV((double)((float)par2 + var49), (double)par3 + var60, (double)par4 + 0.25, var56, var54);
         } else if (var7 == 0) {
            var5.addVertexWithUV((double)((float)par2 + var49), (double)par3 + var60, (double)par4 + 0.75, var52, var54);
            var5.addVertexWithUV((double)((float)par2 + var50), (double)par3 + var60, (double)par4 + 0.75, var52, var58);
            var5.addVertexWithUV((double)((float)par2 + var50), var64, (double)par4 + 0.5, var56, var58);
            var5.addVertexWithUV((double)((float)par2 + var49), var64, (double)par4 + 0.5, var56, var54);
            var5.addVertexWithUV((double)((float)par2 + var49), (double)par3 + var60, (double)(par4 + 1), var52, var54);
            var5.addVertexWithUV((double)((float)par2 + var50), (double)par3 + var60, (double)(par4 + 1), var52, var58);
            var5.addVertexWithUV((double)((float)par2 + var50), (double)par3 + var60, (double)par4 + 0.75, var56, var58);
            var5.addVertexWithUV((double)((float)par2 + var49), (double)par3 + var60, (double)par4 + 0.75, var56, var54);
         } else if (var7 == 1) {
            var5.addVertexWithUV((double)par2, (double)par3 + var60, (double)((float)par4 + var50), var52, var58);
            var5.addVertexWithUV((double)par2 + 0.25, (double)par3 + var60, (double)((float)par4 + var50), var56, var58);
            var5.addVertexWithUV((double)par2 + 0.25, (double)par3 + var60, (double)((float)par4 + var49), var56, var54);
            var5.addVertexWithUV((double)par2, (double)par3 + var60, (double)((float)par4 + var49), var52, var54);
            var5.addVertexWithUV((double)par2 + 0.25, (double)par3 + var60, (double)((float)par4 + var50), var52, var58);
            var5.addVertexWithUV((double)par2 + 0.5, var64, (double)((float)par4 + var50), var56, var58);
            var5.addVertexWithUV((double)par2 + 0.5, var64, (double)((float)par4 + var49), var56, var54);
            var5.addVertexWithUV((double)par2 + 0.25, (double)par3 + var60, (double)((float)par4 + var49), var52, var54);
         } else {
            var5.addVertexWithUV((double)par2 + 0.5, var64, (double)((float)par4 + var50), var52, var58);
            var5.addVertexWithUV((double)par2 + 0.75, (double)par3 + var60, (double)((float)par4 + var50), var56, var58);
            var5.addVertexWithUV((double)par2 + 0.75, (double)par3 + var60, (double)((float)par4 + var49), var56, var54);
            var5.addVertexWithUV((double)par2 + 0.5, var64, (double)((float)par4 + var49), var52, var54);
            var5.addVertexWithUV((double)par2 + 0.75, (double)par3 + var60, (double)((float)par4 + var50), var52, var58);
            var5.addVertexWithUV((double)(par2 + 1), (double)par3 + var60, (double)((float)par4 + var50), var56, var58);
            var5.addVertexWithUV((double)(par2 + 1), (double)par3 + var60, (double)((float)par4 + var49), var56, var54);
            var5.addVertexWithUV((double)par2 + 0.75, (double)par3 + var60, (double)((float)par4 + var49), var52, var54);
         }
      }

      return true;
   }

   public boolean renderBlockTripWire(Block par1Block, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      int var7 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      boolean var8 = (var7 & 4) == 4;
      boolean var9 = (var7 & 2) == 2;
      Icon var6 = this.overrideBlockTexture == null ? this.getBlockIconFromSide(par1Block, 0) : this.overrideBlockTexture;
      var5.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var10 = par1Block.getBlockBrightness(this.blockAccess, par2, par3, par4) * 0.75F;
      var5.setColorOpaque_F(var10, var10, var10);
      double var11 = (double)var6.getMinU();
      double var13 = (double)var6.getInterpolatedV(var8 ? 2.0 : 0.0);
      double var15 = (double)var6.getMaxU();
      double var17 = (double)var6.getInterpolatedV(var8 ? 4.0 : 2.0);
      double var19 = (double)(var9 ? 3.5F : 1.5F) / 16.0;
      boolean var21 = BlockTripWire.func_72148_a(this.blockAccess, par2, par3, par4, var7, 1);
      boolean var22 = BlockTripWire.func_72148_a(this.blockAccess, par2, par3, par4, var7, 3);
      boolean var23 = BlockTripWire.func_72148_a(this.blockAccess, par2, par3, par4, var7, 2);
      boolean var24 = BlockTripWire.func_72148_a(this.blockAccess, par2, par3, par4, var7, 0);
      float var25 = 0.03125F;
      float var26 = 0.5F - var25 / 2.0F;
      float var27 = var26 + var25;
      if (!var23 && !var22 && !var24 && !var21) {
         var23 = true;
         var24 = true;
      }

      if (var23) {
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4 + 0.25, var11, var13);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4 + 0.25, var11, var17);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4, var15, var17);
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4, var15, var13);
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4, var15, var13);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4, var15, var17);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4 + 0.25, var11, var17);
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4 + 0.25, var11, var13);
      }

      if (var23 || var24 && !var22 && !var21) {
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4 + 0.5, var11, var13);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4 + 0.5, var11, var17);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4 + 0.25, var15, var17);
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4 + 0.25, var15, var13);
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4 + 0.25, var15, var13);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4 + 0.25, var15, var17);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4 + 0.5, var11, var17);
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4 + 0.5, var11, var13);
      }

      if (var24 || var23 && !var22 && !var21) {
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4 + 0.75, var11, var13);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4 + 0.75, var11, var17);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4 + 0.5, var15, var17);
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4 + 0.5, var15, var13);
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4 + 0.5, var15, var13);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4 + 0.5, var15, var17);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4 + 0.75, var11, var17);
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4 + 0.75, var11, var13);
      }

      if (var24) {
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)(par4 + 1), var11, var13);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)(par4 + 1), var11, var17);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4 + 0.75, var15, var17);
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4 + 0.75, var15, var13);
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)par4 + 0.75, var15, var13);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)par4 + 0.75, var15, var17);
         var5.addVertexWithUV((double)((float)par2 + var27), (double)par3 + var19, (double)(par4 + 1), var11, var17);
         var5.addVertexWithUV((double)((float)par2 + var26), (double)par3 + var19, (double)(par4 + 1), var11, var13);
      }

      if (var21) {
         var5.addVertexWithUV((double)par2, (double)par3 + var19, (double)((float)par4 + var27), var11, var17);
         var5.addVertexWithUV((double)par2 + 0.25, (double)par3 + var19, (double)((float)par4 + var27), var15, var17);
         var5.addVertexWithUV((double)par2 + 0.25, (double)par3 + var19, (double)((float)par4 + var26), var15, var13);
         var5.addVertexWithUV((double)par2, (double)par3 + var19, (double)((float)par4 + var26), var11, var13);
         var5.addVertexWithUV((double)par2, (double)par3 + var19, (double)((float)par4 + var26), var11, var13);
         var5.addVertexWithUV((double)par2 + 0.25, (double)par3 + var19, (double)((float)par4 + var26), var15, var13);
         var5.addVertexWithUV((double)par2 + 0.25, (double)par3 + var19, (double)((float)par4 + var27), var15, var17);
         var5.addVertexWithUV((double)par2, (double)par3 + var19, (double)((float)par4 + var27), var11, var17);
      }

      if (var21 || var22 && !var23 && !var24) {
         var5.addVertexWithUV((double)par2 + 0.25, (double)par3 + var19, (double)((float)par4 + var27), var11, var17);
         var5.addVertexWithUV((double)par2 + 0.5, (double)par3 + var19, (double)((float)par4 + var27), var15, var17);
         var5.addVertexWithUV((double)par2 + 0.5, (double)par3 + var19, (double)((float)par4 + var26), var15, var13);
         var5.addVertexWithUV((double)par2 + 0.25, (double)par3 + var19, (double)((float)par4 + var26), var11, var13);
         var5.addVertexWithUV((double)par2 + 0.25, (double)par3 + var19, (double)((float)par4 + var26), var11, var13);
         var5.addVertexWithUV((double)par2 + 0.5, (double)par3 + var19, (double)((float)par4 + var26), var15, var13);
         var5.addVertexWithUV((double)par2 + 0.5, (double)par3 + var19, (double)((float)par4 + var27), var15, var17);
         var5.addVertexWithUV((double)par2 + 0.25, (double)par3 + var19, (double)((float)par4 + var27), var11, var17);
      }

      if (var22 || var21 && !var23 && !var24) {
         var5.addVertexWithUV((double)par2 + 0.5, (double)par3 + var19, (double)((float)par4 + var27), var11, var17);
         var5.addVertexWithUV((double)par2 + 0.75, (double)par3 + var19, (double)((float)par4 + var27), var15, var17);
         var5.addVertexWithUV((double)par2 + 0.75, (double)par3 + var19, (double)((float)par4 + var26), var15, var13);
         var5.addVertexWithUV((double)par2 + 0.5, (double)par3 + var19, (double)((float)par4 + var26), var11, var13);
         var5.addVertexWithUV((double)par2 + 0.5, (double)par3 + var19, (double)((float)par4 + var26), var11, var13);
         var5.addVertexWithUV((double)par2 + 0.75, (double)par3 + var19, (double)((float)par4 + var26), var15, var13);
         var5.addVertexWithUV((double)par2 + 0.75, (double)par3 + var19, (double)((float)par4 + var27), var15, var17);
         var5.addVertexWithUV((double)par2 + 0.5, (double)par3 + var19, (double)((float)par4 + var27), var11, var17);
      }

      if (var22) {
         var5.addVertexWithUV((double)par2 + 0.75, (double)par3 + var19, (double)((float)par4 + var27), var11, var17);
         var5.addVertexWithUV((double)(par2 + 1), (double)par3 + var19, (double)((float)par4 + var27), var15, var17);
         var5.addVertexWithUV((double)(par2 + 1), (double)par3 + var19, (double)((float)par4 + var26), var15, var13);
         var5.addVertexWithUV((double)par2 + 0.75, (double)par3 + var19, (double)((float)par4 + var26), var11, var13);
         var5.addVertexWithUV((double)par2 + 0.75, (double)par3 + var19, (double)((float)par4 + var26), var11, var13);
         var5.addVertexWithUV((double)(par2 + 1), (double)par3 + var19, (double)((float)par4 + var26), var15, var13);
         var5.addVertexWithUV((double)(par2 + 1), (double)par3 + var19, (double)((float)par4 + var27), var15, var17);
         var5.addVertexWithUV((double)par2 + 0.75, (double)par3 + var19, (double)((float)par4 + var27), var11, var17);
      }

      return true;
   }

   public boolean renderBlockFire(BlockFire par1BlockFire, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      Icon var6 = par1BlockFire.getFireIcon(0);
      Icon var7 = par1BlockFire.getFireIcon(1);
      Icon var8 = this.overrideBlockTexture == null ? var6 : this.overrideBlockTexture;
      var5.setColorOpaque_F(1.0F, 1.0F, 1.0F);
      var5.setBrightness(par1BlockFire.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      double var9 = (double)var8.getMinU();
      double var11 = (double)var8.getMinV();
      double var13 = (double)var8.getMaxU();
      double var15 = (double)var8.getMaxV();
      float var17 = 1.4F;
      double var32;
      double var20;
      double var22;
      double var24;
      double var26;
      double var28;
      double var30;
      if (!this.blockAccess.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) && !Block.fire.canBlockCatchFire(this.blockAccess, par2, par3 - 1, par4, UP)) {
         float var36 = 0.2F;
         float var19 = 0.0625F;
         if ((par2 + par3 + par4 & 1) == 1) {
            var9 = (double)var7.getMinU();
            var11 = (double)var7.getMinV();
            var13 = (double)var7.getMaxU();
            var15 = (double)var7.getMaxV();
         }

         if ((par2 / 2 + par3 / 2 + par4 / 2 & 1) == 1) {
            var20 = var13;
            var13 = var9;
            var9 = var20;
         }

         if (Block.fire.canBlockCatchFire(this.blockAccess, par2 - 1, par3, par4, EAST)) {
            var5.addVertexWithUV((double)((float)par2 + var36), (double)((float)par3 + var17 + var19), (double)(par4 + 1), var13, var11);
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 1), var13, var15);
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var9, var15);
            var5.addVertexWithUV((double)((float)par2 + var36), (double)((float)par3 + var17 + var19), (double)(par4 + 0), var9, var11);
            var5.addVertexWithUV((double)((float)par2 + var36), (double)((float)par3 + var17 + var19), (double)(par4 + 0), var9, var11);
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var9, var15);
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 1), var13, var15);
            var5.addVertexWithUV((double)((float)par2 + var36), (double)((float)par3 + var17 + var19), (double)(par4 + 1), var13, var11);
         }

         if (Block.fire.canBlockCatchFire(this.blockAccess, par2, par3, par4 - 1, SOUTH)) {
            var5.addVertexWithUV((double)((float)(par2 + 1) - var36), (double)((float)par3 + var17 + var19), (double)(par4 + 0), var9, var11);
            var5.addVertexWithUV((double)(par2 + 1 - 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var9, var15);
            var5.addVertexWithUV((double)(par2 + 1 - 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 1), var13, var15);
            var5.addVertexWithUV((double)((float)(par2 + 1) - var36), (double)((float)par3 + var17 + var19), (double)(par4 + 1), var13, var11);
            var5.addVertexWithUV((double)((float)(par2 + 1) - var36), (double)((float)par3 + var17 + var19), (double)(par4 + 1), var13, var11);
            var5.addVertexWithUV((double)(par2 + 1 - 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 1), var13, var15);
            var5.addVertexWithUV((double)(par2 + 1 - 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var9, var15);
            var5.addVertexWithUV((double)((float)(par2 + 1) - var36), (double)((float)par3 + var17 + var19), (double)(par4 + 0), var9, var11);
         }

         if (Block.fire.canBlockCatchFire(this.blockAccess, par2, par3, par4 + 1, NORTH)) {
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)par3 + var17 + var19), (double)((float)par4 + var36), var13, var11);
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var13, var15);
            var5.addVertexWithUV((double)(par2 + 1), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var9, var15);
            var5.addVertexWithUV((double)(par2 + 1), (double)((float)par3 + var17 + var19), (double)((float)par4 + var36), var9, var11);
            var5.addVertexWithUV((double)(par2 + 1), (double)((float)par3 + var17 + var19), (double)((float)par4 + var36), var9, var11);
            var5.addVertexWithUV((double)(par2 + 1), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var9, var15);
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var13, var15);
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)par3 + var17 + var19), (double)((float)par4 + var36), var13, var11);
         }

         if (Block.fire.canBlockCatchFire(this.blockAccess, par2, par3 + 1, par4, DOWN)) {
            var5.addVertexWithUV((double)(par2 + 1), (double)((float)par3 + var17 + var19), (double)((float)(par4 + 1) - var36), var9, var11);
            var5.addVertexWithUV((double)(par2 + 1), (double)((float)(par3 + 0) + var19), (double)(par4 + 1 - 0), var9, var15);
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 1 - 0), var13, var15);
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)par3 + var17 + var19), (double)((float)(par4 + 1) - var36), var13, var11);
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)par3 + var17 + var19), (double)((float)(par4 + 1) - var36), var13, var11);
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 1 - 0), var13, var15);
            var5.addVertexWithUV((double)(par2 + 1), (double)((float)(par3 + 0) + var19), (double)(par4 + 1 - 0), var9, var15);
            var5.addVertexWithUV((double)(par2 + 1), (double)((float)par3 + var17 + var19), (double)((float)(par4 + 1) - var36), var9, var11);
         }

         if (Block.fire.canBlockCatchFire(this.blockAccess, par2, par3 + 1, par4)) {
            var20 = (double)par2 + 0.5 + 0.5;
            var22 = (double)par2 + 0.5 - 0.5;
            var24 = (double)par4 + 0.5 + 0.5;
            var26 = (double)par4 + 0.5 - 0.5;
            var28 = (double)par2 + 0.5 - 0.5;
            var30 = (double)par2 + 0.5 + 0.5;
            var32 = (double)par4 + 0.5 - 0.5;
            double var34 = (double)par4 + 0.5 + 0.5;
            var9 = (double)var6.getMinU();
            var11 = (double)var6.getMinV();
            var13 = (double)var6.getMaxU();
            var15 = (double)var6.getMaxV();
            ++par3;
            var17 = -0.2F;
            if ((par2 + par3 + par4 & 1) == 0) {
               var5.addVertexWithUV(var28, (double)((float)par3 + var17), (double)(par4 + 0), var13, var11);
               var5.addVertexWithUV(var20, (double)(par3 + 0), (double)(par4 + 0), var13, var15);
               var5.addVertexWithUV(var20, (double)(par3 + 0), (double)(par4 + 1), var9, var15);
               var5.addVertexWithUV(var28, (double)((float)par3 + var17), (double)(par4 + 1), var9, var11);
               var9 = (double)var7.getMinU();
               var11 = (double)var7.getMinV();
               var13 = (double)var7.getMaxU();
               var15 = (double)var7.getMaxV();
               var5.addVertexWithUV(var30, (double)((float)par3 + var17), (double)(par4 + 1), var13, var11);
               var5.addVertexWithUV(var22, (double)(par3 + 0), (double)(par4 + 1), var13, var15);
               var5.addVertexWithUV(var22, (double)(par3 + 0), (double)(par4 + 0), var9, var15);
               var5.addVertexWithUV(var30, (double)((float)par3 + var17), (double)(par4 + 0), var9, var11);
            } else {
               var5.addVertexWithUV((double)(par2 + 0), (double)((float)par3 + var17), var34, var13, var11);
               var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), var26, var13, var15);
               var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), var26, var9, var15);
               var5.addVertexWithUV((double)(par2 + 1), (double)((float)par3 + var17), var34, var9, var11);
               var9 = (double)var7.getMinU();
               var11 = (double)var7.getMinV();
               var13 = (double)var7.getMaxU();
               var15 = (double)var7.getMaxV();
               var5.addVertexWithUV((double)(par2 + 1), (double)((float)par3 + var17), var32, var13, var11);
               var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), var24, var13, var15);
               var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), var24, var9, var15);
               var5.addVertexWithUV((double)(par2 + 0), (double)((float)par3 + var17), var32, var9, var11);
            }
         }
      } else {
         double var18 = (double)par2 + 0.5 + 0.2;
         var20 = (double)par2 + 0.5 - 0.2;
         var22 = (double)par4 + 0.5 + 0.2;
         var24 = (double)par4 + 0.5 - 0.2;
         var26 = (double)par2 + 0.5 - 0.3;
         var28 = (double)par2 + 0.5 + 0.3;
         var30 = (double)par4 + 0.5 - 0.3;
         var32 = (double)par4 + 0.5 + 0.3;
         var5.addVertexWithUV(var26, (double)((float)par3 + var17), (double)(par4 + 1), var13, var11);
         var5.addVertexWithUV(var18, (double)(par3 + 0), (double)(par4 + 1), var13, var15);
         var5.addVertexWithUV(var18, (double)(par3 + 0), (double)(par4 + 0), var9, var15);
         var5.addVertexWithUV(var26, (double)((float)par3 + var17), (double)(par4 + 0), var9, var11);
         var5.addVertexWithUV(var28, (double)((float)par3 + var17), (double)(par4 + 0), var13, var11);
         var5.addVertexWithUV(var20, (double)(par3 + 0), (double)(par4 + 0), var13, var15);
         var5.addVertexWithUV(var20, (double)(par3 + 0), (double)(par4 + 1), var9, var15);
         var5.addVertexWithUV(var28, (double)((float)par3 + var17), (double)(par4 + 1), var9, var11);
         var9 = (double)var7.getMinU();
         var11 = (double)var7.getMinV();
         var13 = (double)var7.getMaxU();
         var15 = (double)var7.getMaxV();
         var5.addVertexWithUV((double)(par2 + 1), (double)((float)par3 + var17), var32, var13, var11);
         var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), var24, var13, var15);
         var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), var24, var9, var15);
         var5.addVertexWithUV((double)(par2 + 0), (double)((float)par3 + var17), var32, var9, var11);
         var5.addVertexWithUV((double)(par2 + 0), (double)((float)par3 + var17), var30, var13, var11);
         var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), var22, var13, var15);
         var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), var22, var9, var15);
         var5.addVertexWithUV((double)(par2 + 1), (double)((float)par3 + var17), var30, var9, var11);
         var18 = (double)par2 + 0.5 - 0.5;
         var20 = (double)par2 + 0.5 + 0.5;
         var22 = (double)par4 + 0.5 - 0.5;
         var24 = (double)par4 + 0.5 + 0.5;
         var26 = (double)par2 + 0.5 - 0.4;
         var28 = (double)par2 + 0.5 + 0.4;
         var30 = (double)par4 + 0.5 - 0.4;
         var32 = (double)par4 + 0.5 + 0.4;
         var5.addVertexWithUV(var26, (double)((float)par3 + var17), (double)(par4 + 0), var9, var11);
         var5.addVertexWithUV(var18, (double)(par3 + 0), (double)(par4 + 0), var9, var15);
         var5.addVertexWithUV(var18, (double)(par3 + 0), (double)(par4 + 1), var13, var15);
         var5.addVertexWithUV(var26, (double)((float)par3 + var17), (double)(par4 + 1), var13, var11);
         var5.addVertexWithUV(var28, (double)((float)par3 + var17), (double)(par4 + 1), var9, var11);
         var5.addVertexWithUV(var20, (double)(par3 + 0), (double)(par4 + 1), var9, var15);
         var5.addVertexWithUV(var20, (double)(par3 + 0), (double)(par4 + 0), var13, var15);
         var5.addVertexWithUV(var28, (double)((float)par3 + var17), (double)(par4 + 0), var13, var11);
         var9 = (double)var6.getMinU();
         var11 = (double)var6.getMinV();
         var13 = (double)var6.getMaxU();
         var15 = (double)var6.getMaxV();
         var5.addVertexWithUV((double)(par2 + 0), (double)((float)par3 + var17), var32, var9, var11);
         var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), var24, var9, var15);
         var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), var24, var13, var15);
         var5.addVertexWithUV((double)(par2 + 1), (double)((float)par3 + var17), var32, var13, var11);
         var5.addVertexWithUV((double)(par2 + 1), (double)((float)par3 + var17), var30, var9, var11);
         var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), var22, var9, var15);
         var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), var22, var13, var15);
         var5.addVertexWithUV((double)(par2 + 0), (double)((float)par3 + var17), var30, var13, var11);
      }

      return true;
   }

   public boolean renderBlockRedstoneWire(Block par1Block, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      int var6 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      Icon var7 = BlockRedstoneWire.getRedstoneWireIcon("cross");
      Icon var8 = BlockRedstoneWire.getRedstoneWireIcon("line");
      Icon var9 = BlockRedstoneWire.getRedstoneWireIcon("cross_overlay");
      Icon var10 = BlockRedstoneWire.getRedstoneWireIcon("line_overlay");
      var5.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var11 = 1.0F;
      float var12 = (float)var6 / 15.0F;
      float var13 = var12 * 0.6F + 0.4F;
      if (var6 == 0) {
         var13 = 0.3F;
      }

      float var14 = var12 * var12 * 0.7F - 0.5F;
      float var15 = var12 * var12 * 0.6F - 0.7F;
      if (var14 < 0.0F) {
         var14 = 0.0F;
      }

      if (var15 < 0.0F) {
         var15 = 0.0F;
      }

      var5.setColorOpaque_F(var13, var14, var15);
      double var16 = 0.015625;
      double var18 = 0.015625;
      boolean var20 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2 - 1, par3, par4, 1) || !this.blockAccess.isBlockNormalCube(par2 - 1, par3, par4) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2 - 1, par3 - 1, par4, -1);
      boolean var21 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2 + 1, par3, par4, 3) || !this.blockAccess.isBlockNormalCube(par2 + 1, par3, par4) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2 + 1, par3 - 1, par4, -1);
      boolean var22 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2, par3, par4 - 1, 2) || !this.blockAccess.isBlockNormalCube(par2, par3, par4 - 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2, par3 - 1, par4 - 1, -1);
      boolean var23 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2, par3, par4 + 1, 0) || !this.blockAccess.isBlockNormalCube(par2, par3, par4 + 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2, par3 - 1, par4 + 1, -1);
      if (!this.blockAccess.isBlockNormalCube(par2, par3 + 1, par4)) {
         if (this.blockAccess.isBlockNormalCube(par2 - 1, par3, par4) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2 - 1, par3 + 1, par4, -1)) {
            var20 = true;
         }

         if (this.blockAccess.isBlockNormalCube(par2 + 1, par3, par4) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2 + 1, par3 + 1, par4, -1)) {
            var21 = true;
         }

         if (this.blockAccess.isBlockNormalCube(par2, par3, par4 - 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2, par3 + 1, par4 - 1, -1)) {
            var22 = true;
         }

         if (this.blockAccess.isBlockNormalCube(par2, par3, par4 + 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2, par3 + 1, par4 + 1, -1)) {
            var23 = true;
         }
      }

      float var24 = (float)(par2 + 0);
      float var25 = (float)(par2 + 1);
      float var26 = (float)(par4 + 0);
      float var27 = (float)(par4 + 1);
      int var28 = 0;
      if ((var20 || var21) && !var22 && !var23) {
         var28 = 1;
      }

      if ((var22 || var23) && !var21 && !var20) {
         var28 = 2;
      }

      if (var28 == 0) {
         int var29 = 0;
         int var30 = 0;
         int var31 = 16;
         int var32 = 16;
         boolean var33 = true;
         if (!var20) {
            var24 += 0.3125F;
         }

         if (!var20) {
            var29 += 5;
         }

         if (!var21) {
            var25 -= 0.3125F;
         }

         if (!var21) {
            var31 -= 5;
         }

         if (!var22) {
            var26 += 0.3125F;
         }

         if (!var22) {
            var30 += 5;
         }

         if (!var23) {
            var27 -= 0.3125F;
         }

         if (!var23) {
            var32 -= 5;
         }

         var5.addVertexWithUV((double)var25, (double)par3 + 0.015625, (double)var27, (double)var7.getInterpolatedU((double)var31), (double)var7.getInterpolatedV((double)var32));
         var5.addVertexWithUV((double)var25, (double)par3 + 0.015625, (double)var26, (double)var7.getInterpolatedU((double)var31), (double)var7.getInterpolatedV((double)var30));
         var5.addVertexWithUV((double)var24, (double)par3 + 0.015625, (double)var26, (double)var7.getInterpolatedU((double)var29), (double)var7.getInterpolatedV((double)var30));
         var5.addVertexWithUV((double)var24, (double)par3 + 0.015625, (double)var27, (double)var7.getInterpolatedU((double)var29), (double)var7.getInterpolatedV((double)var32));
         var5.setColorOpaque_F(var11, var11, var11);
         var5.addVertexWithUV((double)var25, (double)par3 + 0.015625, (double)var27, (double)var9.getInterpolatedU((double)var31), (double)var9.getInterpolatedV((double)var32));
         var5.addVertexWithUV((double)var25, (double)par3 + 0.015625, (double)var26, (double)var9.getInterpolatedU((double)var31), (double)var9.getInterpolatedV((double)var30));
         var5.addVertexWithUV((double)var24, (double)par3 + 0.015625, (double)var26, (double)var9.getInterpolatedU((double)var29), (double)var9.getInterpolatedV((double)var30));
         var5.addVertexWithUV((double)var24, (double)par3 + 0.015625, (double)var27, (double)var9.getInterpolatedU((double)var29), (double)var9.getInterpolatedV((double)var32));
      } else if (var28 == 1) {
         var5.addVertexWithUV((double)var25, (double)par3 + 0.015625, (double)var27, (double)var8.getMaxU(), (double)var8.getMaxV());
         var5.addVertexWithUV((double)var25, (double)par3 + 0.015625, (double)var26, (double)var8.getMaxU(), (double)var8.getMinV());
         var5.addVertexWithUV((double)var24, (double)par3 + 0.015625, (double)var26, (double)var8.getMinU(), (double)var8.getMinV());
         var5.addVertexWithUV((double)var24, (double)par3 + 0.015625, (double)var27, (double)var8.getMinU(), (double)var8.getMaxV());
         var5.setColorOpaque_F(var11, var11, var11);
         var5.addVertexWithUV((double)var25, (double)par3 + 0.015625, (double)var27, (double)var10.getMaxU(), (double)var10.getMaxV());
         var5.addVertexWithUV((double)var25, (double)par3 + 0.015625, (double)var26, (double)var10.getMaxU(), (double)var10.getMinV());
         var5.addVertexWithUV((double)var24, (double)par3 + 0.015625, (double)var26, (double)var10.getMinU(), (double)var10.getMinV());
         var5.addVertexWithUV((double)var24, (double)par3 + 0.015625, (double)var27, (double)var10.getMinU(), (double)var10.getMaxV());
      } else {
         var5.addVertexWithUV((double)var25, (double)par3 + 0.015625, (double)var27, (double)var8.getMaxU(), (double)var8.getMaxV());
         var5.addVertexWithUV((double)var25, (double)par3 + 0.015625, (double)var26, (double)var8.getMinU(), (double)var8.getMaxV());
         var5.addVertexWithUV((double)var24, (double)par3 + 0.015625, (double)var26, (double)var8.getMinU(), (double)var8.getMinV());
         var5.addVertexWithUV((double)var24, (double)par3 + 0.015625, (double)var27, (double)var8.getMaxU(), (double)var8.getMinV());
         var5.setColorOpaque_F(var11, var11, var11);
         var5.addVertexWithUV((double)var25, (double)par3 + 0.015625, (double)var27, (double)var10.getMaxU(), (double)var10.getMaxV());
         var5.addVertexWithUV((double)var25, (double)par3 + 0.015625, (double)var26, (double)var10.getMinU(), (double)var10.getMaxV());
         var5.addVertexWithUV((double)var24, (double)par3 + 0.015625, (double)var26, (double)var10.getMinU(), (double)var10.getMinV());
         var5.addVertexWithUV((double)var24, (double)par3 + 0.015625, (double)var27, (double)var10.getMaxU(), (double)var10.getMinV());
      }

      if (!this.blockAccess.isBlockNormalCube(par2, par3 + 1, par4)) {
         float var34 = 0.021875F;
         if (this.blockAccess.isBlockNormalCube(par2 - 1, par3, par4) && this.blockAccess.getBlockId(par2 - 1, par3 + 1, par4) == Block.redstoneWire.blockID) {
            var5.setColorOpaque_F(var11 * var13, var11 * var14, var11 * var15);
            var5.addVertexWithUV((double)par2 + 0.015625, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1), (double)var8.getMaxU(), (double)var8.getMinV());
            var5.addVertexWithUV((double)par2 + 0.015625, (double)(par3 + 0), (double)(par4 + 1), (double)var8.getMinU(), (double)var8.getMinV());
            var5.addVertexWithUV((double)par2 + 0.015625, (double)(par3 + 0), (double)(par4 + 0), (double)var8.getMinU(), (double)var8.getMaxV());
            var5.addVertexWithUV((double)par2 + 0.015625, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 0), (double)var8.getMaxU(), (double)var8.getMaxV());
            var5.setColorOpaque_F(var11, var11, var11);
            var5.addVertexWithUV((double)par2 + 0.015625, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1), (double)var10.getMaxU(), (double)var10.getMinV());
            var5.addVertexWithUV((double)par2 + 0.015625, (double)(par3 + 0), (double)(par4 + 1), (double)var10.getMinU(), (double)var10.getMinV());
            var5.addVertexWithUV((double)par2 + 0.015625, (double)(par3 + 0), (double)(par4 + 0), (double)var10.getMinU(), (double)var10.getMaxV());
            var5.addVertexWithUV((double)par2 + 0.015625, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 0), (double)var10.getMaxU(), (double)var10.getMaxV());
         }

         if (this.blockAccess.isBlockNormalCube(par2 + 1, par3, par4) && this.blockAccess.getBlockId(par2 + 1, par3 + 1, par4) == Block.redstoneWire.blockID) {
            var5.setColorOpaque_F(var11 * var13, var11 * var14, var11 * var15);
            var5.addVertexWithUV((double)(par2 + 1) - 0.015625, (double)(par3 + 0), (double)(par4 + 1), (double)var8.getMinU(), (double)var8.getMaxV());
            var5.addVertexWithUV((double)(par2 + 1) - 0.015625, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1), (double)var8.getMaxU(), (double)var8.getMaxV());
            var5.addVertexWithUV((double)(par2 + 1) - 0.015625, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 0), (double)var8.getMaxU(), (double)var8.getMinV());
            var5.addVertexWithUV((double)(par2 + 1) - 0.015625, (double)(par3 + 0), (double)(par4 + 0), (double)var8.getMinU(), (double)var8.getMinV());
            var5.setColorOpaque_F(var11, var11, var11);
            var5.addVertexWithUV((double)(par2 + 1) - 0.015625, (double)(par3 + 0), (double)(par4 + 1), (double)var10.getMinU(), (double)var10.getMaxV());
            var5.addVertexWithUV((double)(par2 + 1) - 0.015625, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1), (double)var10.getMaxU(), (double)var10.getMaxV());
            var5.addVertexWithUV((double)(par2 + 1) - 0.015625, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 0), (double)var10.getMaxU(), (double)var10.getMinV());
            var5.addVertexWithUV((double)(par2 + 1) - 0.015625, (double)(par3 + 0), (double)(par4 + 0), (double)var10.getMinU(), (double)var10.getMinV());
         }

         if (this.blockAccess.isBlockNormalCube(par2, par3, par4 - 1) && this.blockAccess.getBlockId(par2, par3 + 1, par4 - 1) == Block.redstoneWire.blockID) {
            var5.setColorOpaque_F(var11 * var13, var11 * var14, var11 * var15);
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)par4 + 0.015625, (double)var8.getMinU(), (double)var8.getMaxV());
            var5.addVertexWithUV((double)(par2 + 1), (double)((float)(par3 + 1) + 0.021875F), (double)par4 + 0.015625, (double)var8.getMaxU(), (double)var8.getMaxV());
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 1) + 0.021875F), (double)par4 + 0.015625, (double)var8.getMaxU(), (double)var8.getMinV());
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)par4 + 0.015625, (double)var8.getMinU(), (double)var8.getMinV());
            var5.setColorOpaque_F(var11, var11, var11);
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)par4 + 0.015625, (double)var10.getMinU(), (double)var10.getMaxV());
            var5.addVertexWithUV((double)(par2 + 1), (double)((float)(par3 + 1) + 0.021875F), (double)par4 + 0.015625, (double)var10.getMaxU(), (double)var10.getMaxV());
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 1) + 0.021875F), (double)par4 + 0.015625, (double)var10.getMaxU(), (double)var10.getMinV());
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)par4 + 0.015625, (double)var10.getMinU(), (double)var10.getMinV());
         }

         if (this.blockAccess.isBlockNormalCube(par2, par3, par4 + 1) && this.blockAccess.getBlockId(par2, par3 + 1, par4 + 1) == Block.redstoneWire.blockID) {
            var5.setColorOpaque_F(var11 * var13, var11 * var14, var11 * var15);
            var5.addVertexWithUV((double)(par2 + 1), (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1) - 0.015625, (double)var8.getMaxU(), (double)var8.getMinV());
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)(par4 + 1) - 0.015625, (double)var8.getMinU(), (double)var8.getMinV());
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)(par4 + 1) - 0.015625, (double)var8.getMinU(), (double)var8.getMaxV());
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1) - 0.015625, (double)var8.getMaxU(), (double)var8.getMaxV());
            var5.setColorOpaque_F(var11, var11, var11);
            var5.addVertexWithUV((double)(par2 + 1), (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1) - 0.015625, (double)var10.getMaxU(), (double)var10.getMinV());
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)(par4 + 1) - 0.015625, (double)var10.getMinU(), (double)var10.getMinV());
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)(par4 + 1) - 0.015625, (double)var10.getMinU(), (double)var10.getMaxV());
            var5.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1) - 0.015625, (double)var10.getMaxU(), (double)var10.getMaxV());
         }
      }

      return true;
   }

   public boolean renderBlockMinecartTrack(BlockRailBase par1BlockRailBase, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      int var6 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      Icon var7 = this.overrideBlockTexture == null ? this.getBlockIconFromSideAndMetadata(par1BlockRailBase, 0, var6) : this.overrideBlockTexture;
      if (par1BlockRailBase.isPowered()) {
         var6 &= 7;
      }

      var5.setBrightness(par1BlockRailBase.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      var5.setColorOpaque_F(1.0F, 1.0F, 1.0F);
      double var8 = (double)var7.getMinU();
      double var10 = (double)var7.getMinV();
      double var12 = (double)var7.getMaxU();
      double var14 = (double)var7.getMaxV();
      double var16 = 0.0625;
      double var18 = (double)(par2 + 1);
      double var20 = (double)(par2 + 1);
      double var22 = (double)(par2 + 0);
      double var24 = (double)(par2 + 0);
      double var26 = (double)(par4 + 0);
      double var28 = (double)(par4 + 1);
      double var30 = (double)(par4 + 1);
      double var32 = (double)(par4 + 0);
      double var34 = (double)par3 + var16;
      double var36 = (double)par3 + var16;
      double var38 = (double)par3 + var16;
      double var40 = (double)par3 + var16;
      if (var6 != 1 && var6 != 2 && var6 != 3 && var6 != 7) {
         if (var6 == 8) {
            var18 = var20 = (double)(par2 + 0);
            var22 = var24 = (double)(par2 + 1);
            var26 = var32 = (double)(par4 + 1);
            var28 = var30 = (double)(par4 + 0);
         } else if (var6 == 9) {
            var18 = var24 = (double)(par2 + 0);
            var20 = var22 = (double)(par2 + 1);
            var26 = var28 = (double)(par4 + 0);
            var30 = var32 = (double)(par4 + 1);
         }
      } else {
         var18 = var24 = (double)(par2 + 1);
         var20 = var22 = (double)(par2 + 0);
         var26 = var28 = (double)(par4 + 1);
         var30 = var32 = (double)(par4 + 0);
      }

      if (var6 != 2 && var6 != 4) {
         if (var6 == 3 || var6 == 5) {
            ++var36;
            ++var38;
         }
      } else {
         ++var34;
         ++var40;
      }

      var5.addVertexWithUV(var18, var34, var26, var12, var10);
      var5.addVertexWithUV(var20, var36, var28, var12, var14);
      var5.addVertexWithUV(var22, var38, var30, var8, var14);
      var5.addVertexWithUV(var24, var40, var32, var8, var10);
      var5.addVertexWithUV(var24, var40, var32, var8, var10);
      var5.addVertexWithUV(var22, var38, var30, var8, var14);
      var5.addVertexWithUV(var20, var36, var28, var12, var14);
      var5.addVertexWithUV(var18, var34, var26, var12, var10);
      return true;
   }

   public boolean renderBlockLadder(Block par1Block, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      Icon var6 = this.overrideBlockTexture == null ? this.getBlockIconFromSide(par1Block, 0) : this.overrideBlockTexture;
      var5.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var7 = 1.0F;
      var5.setColorOpaque_F(var7, var7, var7);
      double var20 = (double)var6.getMinU();
      double var9 = (double)var6.getMinV();
      double var11 = (double)var6.getMaxU();
      double var13 = (double)var6.getMaxV();
      int var15 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      double var16 = 0.0;
      double var18 = 0.05000000074505806;
      if (var15 == 5) {
         var5.addVertexWithUV((double)par2 + var18, (double)(par3 + 1) + var16, (double)(par4 + 1) + var16, var20, var9);
         var5.addVertexWithUV((double)par2 + var18, (double)(par3 + 0) - var16, (double)(par4 + 1) + var16, var20, var13);
         var5.addVertexWithUV((double)par2 + var18, (double)(par3 + 0) - var16, (double)(par4 + 0) - var16, var11, var13);
         var5.addVertexWithUV((double)par2 + var18, (double)(par3 + 1) + var16, (double)(par4 + 0) - var16, var11, var9);
      }

      if (var15 == 4) {
         var5.addVertexWithUV((double)(par2 + 1) - var18, (double)(par3 + 0) - var16, (double)(par4 + 1) + var16, var11, var13);
         var5.addVertexWithUV((double)(par2 + 1) - var18, (double)(par3 + 1) + var16, (double)(par4 + 1) + var16, var11, var9);
         var5.addVertexWithUV((double)(par2 + 1) - var18, (double)(par3 + 1) + var16, (double)(par4 + 0) - var16, var20, var9);
         var5.addVertexWithUV((double)(par2 + 1) - var18, (double)(par3 + 0) - var16, (double)(par4 + 0) - var16, var20, var13);
      }

      if (var15 == 3) {
         var5.addVertexWithUV((double)(par2 + 1) + var16, (double)(par3 + 0) - var16, (double)par4 + var18, var11, var13);
         var5.addVertexWithUV((double)(par2 + 1) + var16, (double)(par3 + 1) + var16, (double)par4 + var18, var11, var9);
         var5.addVertexWithUV((double)(par2 + 0) - var16, (double)(par3 + 1) + var16, (double)par4 + var18, var20, var9);
         var5.addVertexWithUV((double)(par2 + 0) - var16, (double)(par3 + 0) - var16, (double)par4 + var18, var20, var13);
      }

      if (var15 == 2) {
         var5.addVertexWithUV((double)(par2 + 1) + var16, (double)(par3 + 1) + var16, (double)(par4 + 1) - var18, var20, var9);
         var5.addVertexWithUV((double)(par2 + 1) + var16, (double)(par3 + 0) - var16, (double)(par4 + 1) - var18, var20, var13);
         var5.addVertexWithUV((double)(par2 + 0) - var16, (double)(par3 + 0) - var16, (double)(par4 + 1) - var18, var11, var13);
         var5.addVertexWithUV((double)(par2 + 0) - var16, (double)(par3 + 1) + var16, (double)(par4 + 1) - var18, var11, var9);
      }

      return true;
   }

   public boolean renderBlockVine(Block par1Block, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      Icon var6 = this.overrideBlockTexture == null ? this.getBlockIconFromSide(par1Block, 0) : this.overrideBlockTexture;
      float var7 = 1.0F;
      var5.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      int var8 = par1Block.colorMultiplier(this.blockAccess, par2, par3, par4);
      float var9 = (float)(var8 >> 16 & 255) / 255.0F;
      float var10 = (float)(var8 >> 8 & 255) / 255.0F;
      float var11 = (float)(var8 & 255) / 255.0F;
      var5.setColorOpaque_F(var7 * var9, var7 * var10, var7 * var11);
      double var19 = (double)var6.getMinU();
      double var20 = (double)var6.getMinV();
      double var12 = (double)var6.getMaxU();
      double var14 = (double)var6.getMaxV();
      double var16 = 0.05000000074505806;
      int var18 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      if ((var18 & 2) != 0) {
         if (RenderingScheme.current == 0) {
            var5.addVertexWithUV((double)par2 + var16, (double)(par3 + 1), (double)(par4 + 1), var19, var20);
            var5.addVertexWithUV((double)par2 + var16, (double)(par3 + 0), (double)(par4 + 1), var19, var14);
            var5.addVertexWithUV((double)par2 + var16, (double)(par3 + 0), (double)(par4 + 0), var12, var14);
            var5.addVertexWithUV((double)par2 + var16, (double)(par3 + 1), (double)(par4 + 0), var12, var20);
            var5.addVertexWithUV((double)par2 + var16, (double)(par3 + 1), (double)(par4 + 0), var12, var20);
            var5.addVertexWithUV((double)par2 + var16, (double)(par3 + 0), (double)(par4 + 0), var12, var14);
            var5.addVertexWithUV((double)par2 + var16, (double)(par3 + 0), (double)(par4 + 1), var19, var14);
            var5.addVertexWithUV((double)par2 + var16, (double)(par3 + 1), (double)(par4 + 1), var19, var20);
         } else {
            this.x[0] = (double)par2 + var16;
            this.y[0] = (double)(par3 + 1);
            this.z[0] = (double)(par4 + 1);
            this.u[0] = var19;
            this.v[0] = var20;
            this.x[1] = (double)par2 + var16;
            this.y[1] = (double)par3;
            this.z[1] = (double)(par4 + 1);
            this.u[1] = var19;
            this.v[1] = var14;
            this.x[2] = (double)par2 + var16;
            this.y[2] = (double)par3;
            this.z[2] = (double)par4;
            this.u[2] = var12;
            this.v[2] = var14;
            this.x[3] = (double)par2 + var16;
            this.y[3] = (double)(par3 + 1);
            this.z[3] = (double)par4;
            this.u[3] = var12;
            this.v[3] = var20;
            var5.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
            this.x[0] = (double)par2 + var16;
            this.y[0] = (double)(par3 + 1);
            this.z[0] = (double)par4;
            this.u[0] = var12;
            this.v[0] = var20;
            this.x[1] = (double)par2 + var16;
            this.y[1] = (double)par3;
            this.z[1] = (double)par4;
            this.u[1] = var12;
            this.v[1] = var14;
            this.x[2] = (double)par2 + var16;
            this.y[2] = (double)par3;
            this.z[2] = (double)(par4 + 1);
            this.u[2] = var19;
            this.v[2] = var14;
            this.x[3] = (double)par2 + var16;
            this.y[3] = (double)(par3 + 1);
            this.z[3] = (double)(par4 + 1);
            this.u[3] = var19;
            this.v[3] = var20;
            var5.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
         }
      }

      if ((var18 & 8) != 0) {
         if (RenderingScheme.current == 0) {
            var5.addVertexWithUV((double)(par2 + 1) - var16, (double)(par3 + 0), (double)(par4 + 1), var12, var14);
            var5.addVertexWithUV((double)(par2 + 1) - var16, (double)(par3 + 1), (double)(par4 + 1), var12, var20);
            var5.addVertexWithUV((double)(par2 + 1) - var16, (double)(par3 + 1), (double)(par4 + 0), var19, var20);
            var5.addVertexWithUV((double)(par2 + 1) - var16, (double)(par3 + 0), (double)(par4 + 0), var19, var14);
            var5.addVertexWithUV((double)(par2 + 1) - var16, (double)(par3 + 0), (double)(par4 + 0), var19, var14);
            var5.addVertexWithUV((double)(par2 + 1) - var16, (double)(par3 + 1), (double)(par4 + 0), var19, var20);
            var5.addVertexWithUV((double)(par2 + 1) - var16, (double)(par3 + 1), (double)(par4 + 1), var12, var20);
            var5.addVertexWithUV((double)(par2 + 1) - var16, (double)(par3 + 0), (double)(par4 + 1), var12, var14);
         } else {
            this.x[0] = (double)(par2 + 1) - var16;
            this.y[0] = (double)par3;
            this.z[0] = (double)(par4 + 1);
            this.u[0] = var12;
            this.v[0] = var14;
            this.x[1] = (double)(par2 + 1) - var16;
            this.y[1] = (double)(par3 + 1);
            this.z[1] = (double)(par4 + 1);
            this.u[1] = var12;
            this.v[1] = var20;
            this.x[2] = (double)(par2 + 1) - var16;
            this.y[2] = (double)(par3 + 1);
            this.z[2] = (double)par4;
            this.u[2] = var19;
            this.v[2] = var20;
            this.x[3] = (double)(par2 + 1) - var16;
            this.y[3] = (double)par3;
            this.z[3] = (double)par4;
            this.u[3] = var19;
            this.v[3] = var14;
            var5.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
            this.x[0] = (double)(par2 + 1) - var16;
            this.y[0] = (double)par3;
            this.z[0] = (double)par4;
            this.u[0] = var19;
            this.v[0] = var14;
            this.x[1] = (double)(par2 + 1) - var16;
            this.y[1] = (double)(par3 + 1);
            this.z[1] = (double)par4;
            this.u[1] = var19;
            this.v[1] = var20;
            this.x[2] = (double)(par2 + 1) - var16;
            this.y[2] = (double)(par3 + 1);
            this.z[2] = (double)(par4 + 1);
            this.u[2] = var12;
            this.v[2] = var20;
            this.x[3] = (double)(par2 + 1) - var16;
            this.y[3] = (double)par3;
            this.z[3] = (double)(par4 + 1);
            this.u[3] = var12;
            this.v[3] = var14;
            var5.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
         }
      }

      if ((var18 & 4) != 0) {
         if (RenderingScheme.current == 0) {
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)par4 + var16, var12, var14);
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 1), (double)par4 + var16, var12, var20);
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 1), (double)par4 + var16, var19, var20);
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)par4 + var16, var19, var14);
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)par4 + var16, var19, var14);
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 1), (double)par4 + var16, var19, var20);
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 1), (double)par4 + var16, var12, var20);
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)par4 + var16, var12, var14);
         } else {
            this.x[0] = (double)(par2 + 1);
            this.y[0] = (double)par3;
            this.z[0] = (double)par4 + var16;
            this.u[0] = var12;
            this.v[0] = var14;
            this.x[1] = (double)(par2 + 1);
            this.y[1] = (double)(par3 + 1);
            this.z[1] = (double)par4 + var16;
            this.u[1] = var12;
            this.v[1] = var20;
            this.x[2] = (double)par2;
            this.y[2] = (double)(par3 + 1);
            this.z[2] = (double)par4 + var16;
            this.u[2] = var19;
            this.v[2] = var20;
            this.x[3] = (double)par2;
            this.y[3] = (double)par3;
            this.z[3] = (double)par4 + var16;
            this.u[3] = var19;
            this.v[3] = var14;
            var5.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
            this.x[0] = (double)par2;
            this.y[0] = (double)par3;
            this.z[0] = (double)par4 + var16;
            this.u[0] = var19;
            this.v[0] = var14;
            this.x[1] = (double)par2;
            this.y[1] = (double)(par3 + 1);
            this.z[1] = (double)par4 + var16;
            this.u[1] = var19;
            this.v[1] = var20;
            this.x[2] = (double)(par2 + 1);
            this.y[2] = (double)(par3 + 1);
            this.z[2] = (double)par4 + var16;
            this.u[2] = var12;
            this.v[2] = var20;
            this.x[3] = (double)(par2 + 1);
            this.y[3] = (double)par3;
            this.z[3] = (double)par4 + var16;
            this.u[3] = var12;
            this.v[3] = var14;
            var5.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
         }
      }

      if ((var18 & 1) != 0) {
         if (RenderingScheme.current == 0) {
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 1), (double)(par4 + 1) - var16, var19, var20);
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)(par4 + 1) - var16, var19, var14);
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)(par4 + 1) - var16, var12, var14);
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 1), (double)(par4 + 1) - var16, var12, var20);
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 1), (double)(par4 + 1) - var16, var12, var20);
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)(par4 + 1) - var16, var12, var14);
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)(par4 + 1) - var16, var19, var14);
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 1), (double)(par4 + 1) - var16, var19, var20);
         } else {
            this.x[0] = (double)(par2 + 1);
            this.y[0] = (double)(par3 + 1);
            this.z[0] = (double)(par4 + 1) - var16;
            this.u[0] = var19;
            this.v[0] = var20;
            this.x[1] = (double)(par2 + 1);
            this.y[1] = (double)par3;
            this.z[1] = (double)(par4 + 1) - var16;
            this.u[1] = var19;
            this.v[1] = var14;
            this.x[2] = (double)par2;
            this.y[2] = (double)par3;
            this.z[2] = (double)(par4 + 1) - var16;
            this.u[2] = var12;
            this.v[2] = var14;
            this.x[3] = (double)par2;
            this.y[3] = (double)(par3 + 1);
            this.z[3] = (double)(par4 + 1) - var16;
            this.u[3] = var12;
            this.v[3] = var20;
            var5.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
            this.x[0] = (double)par2;
            this.y[0] = (double)(par3 + 1);
            this.z[0] = (double)(par4 + 1) - var16;
            this.u[0] = var12;
            this.v[0] = var20;
            this.x[1] = (double)par2;
            this.y[1] = (double)par3;
            this.z[1] = (double)(par4 + 1) - var16;
            this.u[1] = var12;
            this.v[1] = var14;
            this.x[2] = (double)(par2 + 1);
            this.y[2] = (double)par3;
            this.z[2] = (double)(par4 + 1) - var16;
            this.u[2] = var19;
            this.v[2] = var14;
            this.x[3] = (double)(par2 + 1);
            this.y[3] = (double)(par3 + 1);
            this.z[3] = (double)(par4 + 1) - var16;
            this.u[3] = var19;
            this.v[3] = var20;
            var5.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
         }
      }

      if (this.blockAccess.isBlockNormalCube(par2, par3 + 1, par4)) {
         if (RenderingScheme.current == 0) {
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 1) - var16, (double)(par4 + 0), var19, var20);
            var5.addVertexWithUV((double)(par2 + 1), (double)(par3 + 1) - var16, (double)(par4 + 1), var19, var14);
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 1) - var16, (double)(par4 + 1), var12, var14);
            var5.addVertexWithUV((double)(par2 + 0), (double)(par3 + 1) - var16, (double)(par4 + 0), var12, var20);
         } else {
            this.x[0] = (double)(par2 + 1);
            this.y[0] = (double)(par3 + 1) - var16;
            this.z[0] = (double)par4;
            this.u[0] = var19;
            this.v[0] = var20;
            this.x[1] = (double)(par2 + 1);
            this.y[1] = (double)(par3 + 1) - var16;
            this.z[1] = (double)(par4 + 1);
            this.u[1] = var19;
            this.v[1] = var14;
            this.x[2] = (double)par2;
            this.y[2] = (double)(par3 + 1) - var16;
            this.z[2] = (double)(par4 + 1);
            this.u[2] = var12;
            this.v[2] = var14;
            this.x[3] = (double)par2;
            this.y[3] = (double)(par3 + 1) - var16;
            this.z[3] = (double)par4;
            this.u[3] = var12;
            this.v[3] = var20;
            var5.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
         }
      }

      return true;
   }

   public boolean renderBlockPane(BlockPane par1BlockPane, int par2, int par3, int par4) {
      int var5 = this.blockAccess.getHeight();
      Tessellator var6 = Tessellator.instance;
      var6.setBrightness(par1BlockPane.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var7 = 1.0F;
      int var8 = par1BlockPane.colorMultiplier(this.blockAccess, par2, par3, par4);
      float var9 = (float)(var8 >> 16 & 255) / 255.0F;
      float var10 = (float)(var8 >> 8 & 255) / 255.0F;
      float var11 = (float)(var8 & 255) / 255.0F;
      if (EntityRenderer.anaglyphEnable) {
         float var12 = (var9 * 30.0F + var10 * 59.0F + var11 * 11.0F) / 100.0F;
         float var13 = (var9 * 30.0F + var10 * 70.0F) / 100.0F;
         float var14 = (var9 * 30.0F + var11 * 70.0F) / 100.0F;
         var9 = var12;
         var10 = var13;
         var11 = var14;
      }

      var6.setColorOpaque_F(var7 * var9, var7 * var10, var7 * var11);
      Icon var64;
      Icon var65;
      if (this.overrideBlockTexture != null) {
         var64 = this.overrideBlockTexture;
         var65 = this.overrideBlockTexture;
      } else {
         int var66 = this.blockAccess.getBlockMetadata(par2, par3, par4);
         var64 = this.getBlockIconFromSideAndMetadata(par1BlockPane, 0, var66);
         var65 = par1BlockPane.getSideTextureIndex();
      }

      double var67 = (double)var64.getMinU();
      double var16 = (double)var64.getInterpolatedU(8.0);
      double var18 = (double)var64.getMaxU();
      double var20 = (double)var64.getMinV();
      double var22 = (double)var64.getMaxV();
      double var24 = (double)var65.getInterpolatedU(7.0);
      double var26 = (double)var65.getInterpolatedU(9.0);
      double var28 = (double)var65.getMinV();
      double var30 = (double)var65.getInterpolatedV(8.0);
      double var32 = (double)var65.getMaxV();
      double var34 = (double)par2;
      double var36 = (double)par2 + 0.5;
      double var38 = (double)(par2 + 1);
      double var40 = (double)par4;
      double var42 = (double)par4 + 0.5;
      double var44 = (double)(par4 + 1);
      double var46 = (double)par2 + 0.5 - 0.0625;
      double var48 = (double)par2 + 0.5 + 0.0625;
      double var50 = (double)par4 + 0.5 - 0.0625;
      double var52 = (double)par4 + 0.5 + 0.0625;
      boolean var54 = par1BlockPane.canPaneConnectTo(this.blockAccess,par2, par3, par4, NORTH);
      boolean var55 = par1BlockPane.canPaneConnectTo(this.blockAccess,par2, par3, par4, SOUTH);
      boolean var56 = par1BlockPane.canPaneConnectTo(this.blockAccess,par2, par3, par4, WEST);
      boolean var57 = par1BlockPane.canPaneConnectTo(this.blockAccess,par2, par3, par4, EAST);

      boolean var58 = par1BlockPane.shouldSideBeRendered(this.blockAccess, par2, par3 + 1, par4, 1);
      boolean var59 = par1BlockPane.shouldSideBeRendered(this.blockAccess, par2, par3 - 1, par4, 0);
      double var60 = 0.01;
      double var62 = 0.005;
      if ((!var56 || !var57) && (var56 || var57 || var54 || var55)) {
         if (var56 && !var57) {
            var6.addVertexWithUV(var34, (double)(par3 + 1), var42, var67, var20);
            var6.addVertexWithUV(var34, (double)(par3 + 0), var42, var67, var22);
            var6.addVertexWithUV(var36, (double)(par3 + 0), var42, var16, var22);
            var6.addVertexWithUV(var36, (double)(par3 + 1), var42, var16, var20);
            var6.addVertexWithUV(var36, (double)(par3 + 1), var42, var67, var20);
            var6.addVertexWithUV(var36, (double)(par3 + 0), var42, var67, var22);
            var6.addVertexWithUV(var34, (double)(par3 + 0), var42, var16, var22);
            var6.addVertexWithUV(var34, (double)(par3 + 1), var42, var16, var20);
            if (!var55 && !var54) {
               var6.addVertexWithUV(var36, (double)(par3 + 1), var52, var24, var28);
               var6.addVertexWithUV(var36, (double)(par3 + 0), var52, var24, var32);
               var6.addVertexWithUV(var36, (double)(par3 + 0), var50, var26, var32);
               var6.addVertexWithUV(var36, (double)(par3 + 1), var50, var26, var28);
               var6.addVertexWithUV(var36, (double)(par3 + 1), var50, var24, var28);
               var6.addVertexWithUV(var36, (double)(par3 + 0), var50, var24, var32);
               var6.addVertexWithUV(var36, (double)(par3 + 0), var52, var26, var32);
               var6.addVertexWithUV(var36, (double)(par3 + 1), var52, var26, var28);
            }

            if (var58 || par3 < var5 - 1 && this.blockAccess.isAirBlock(par2 - 1, par3 + 1, par4)) {
               var6.addVertexWithUV(var34, (double)(par3 + 1) + 0.01, var52, var26, var30);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var52, var26, var32);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var50, var24, var32);
               var6.addVertexWithUV(var34, (double)(par3 + 1) + 0.01, var50, var24, var30);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var52, var26, var30);
               var6.addVertexWithUV(var34, (double)(par3 + 1) + 0.01, var52, var26, var32);
               var6.addVertexWithUV(var34, (double)(par3 + 1) + 0.01, var50, var24, var32);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var50, var24, var30);
            }

            if (var59 || par3 > 1 && this.blockAccess.isAirBlock(par2 - 1, par3 - 1, par4)) {
               var6.addVertexWithUV(var34, (double)par3 - 0.01, var52, var26, var30);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var52, var26, var32);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var50, var24, var32);
               var6.addVertexWithUV(var34, (double)par3 - 0.01, var50, var24, var30);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var52, var26, var30);
               var6.addVertexWithUV(var34, (double)par3 - 0.01, var52, var26, var32);
               var6.addVertexWithUV(var34, (double)par3 - 0.01, var50, var24, var32);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var50, var24, var30);
            }
         } else if (!var56 && var57) {
            var6.addVertexWithUV(var36, (double)(par3 + 1), var42, var16, var20);
            var6.addVertexWithUV(var36, (double)(par3 + 0), var42, var16, var22);
            var6.addVertexWithUV(var38, (double)(par3 + 0), var42, var18, var22);
            var6.addVertexWithUV(var38, (double)(par3 + 1), var42, var18, var20);
            var6.addVertexWithUV(var38, (double)(par3 + 1), var42, var16, var20);
            var6.addVertexWithUV(var38, (double)(par3 + 0), var42, var16, var22);
            var6.addVertexWithUV(var36, (double)(par3 + 0), var42, var18, var22);
            var6.addVertexWithUV(var36, (double)(par3 + 1), var42, var18, var20);
            if (!var55 && !var54) {
               var6.addVertexWithUV(var36, (double)(par3 + 1), var50, var24, var28);
               var6.addVertexWithUV(var36, (double)(par3 + 0), var50, var24, var32);
               var6.addVertexWithUV(var36, (double)(par3 + 0), var52, var26, var32);
               var6.addVertexWithUV(var36, (double)(par3 + 1), var52, var26, var28);
               var6.addVertexWithUV(var36, (double)(par3 + 1), var52, var24, var28);
               var6.addVertexWithUV(var36, (double)(par3 + 0), var52, var24, var32);
               var6.addVertexWithUV(var36, (double)(par3 + 0), var50, var26, var32);
               var6.addVertexWithUV(var36, (double)(par3 + 1), var50, var26, var28);
            }

            if (var58 || par3 < var5 - 1 && this.blockAccess.isAirBlock(par2 + 1, par3 + 1, par4)) {
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var52, var26, var28);
               var6.addVertexWithUV(var38, (double)(par3 + 1) + 0.01, var52, var26, var30);
               var6.addVertexWithUV(var38, (double)(par3 + 1) + 0.01, var50, var24, var30);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var50, var24, var28);
               var6.addVertexWithUV(var38, (double)(par3 + 1) + 0.01, var52, var26, var28);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var52, var26, var30);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var50, var24, var30);
               var6.addVertexWithUV(var38, (double)(par3 + 1) + 0.01, var50, var24, var28);
            }

            if (var59 || par3 > 1 && this.blockAccess.isAirBlock(par2 + 1, par3 - 1, par4)) {
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var52, var26, var28);
               var6.addVertexWithUV(var38, (double)par3 - 0.01, var52, var26, var30);
               var6.addVertexWithUV(var38, (double)par3 - 0.01, var50, var24, var30);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var50, var24, var28);
               var6.addVertexWithUV(var38, (double)par3 - 0.01, var52, var26, var28);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var52, var26, var30);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var50, var24, var30);
               var6.addVertexWithUV(var38, (double)par3 - 0.01, var50, var24, var28);
            }
         }
      } else {
         var6.addVertexWithUV(var34, (double)(par3 + 1), var42, var67, var20);
         var6.addVertexWithUV(var34, (double)(par3 + 0), var42, var67, var22);
         var6.addVertexWithUV(var38, (double)(par3 + 0), var42, var18, var22);
         var6.addVertexWithUV(var38, (double)(par3 + 1), var42, var18, var20);
         var6.addVertexWithUV(var38, (double)(par3 + 1), var42, var67, var20);
         var6.addVertexWithUV(var38, (double)(par3 + 0), var42, var67, var22);
         var6.addVertexWithUV(var34, (double)(par3 + 0), var42, var18, var22);
         var6.addVertexWithUV(var34, (double)(par3 + 1), var42, var18, var20);
         if (var58) {
            var6.addVertexWithUV(var34, (double)(par3 + 1) + 0.01, var52, var26, var32);
            var6.addVertexWithUV(var38, (double)(par3 + 1) + 0.01, var52, var26, var28);
            var6.addVertexWithUV(var38, (double)(par3 + 1) + 0.01, var50, var24, var28);
            var6.addVertexWithUV(var34, (double)(par3 + 1) + 0.01, var50, var24, var32);
            var6.addVertexWithUV(var38, (double)(par3 + 1) + 0.01, var52, var26, var32);
            var6.addVertexWithUV(var34, (double)(par3 + 1) + 0.01, var52, var26, var28);
            var6.addVertexWithUV(var34, (double)(par3 + 1) + 0.01, var50, var24, var28);
            var6.addVertexWithUV(var38, (double)(par3 + 1) + 0.01, var50, var24, var32);
         } else {
            if (par3 < var5 - 1 && this.blockAccess.isAirBlock(par2 - 1, par3 + 1, par4)) {
               var6.addVertexWithUV(var34, (double)(par3 + 1) + 0.01, var52, var26, var30);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var52, var26, var32);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var50, var24, var32);
               var6.addVertexWithUV(var34, (double)(par3 + 1) + 0.01, var50, var24, var30);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var52, var26, var30);
               var6.addVertexWithUV(var34, (double)(par3 + 1) + 0.01, var52, var26, var32);
               var6.addVertexWithUV(var34, (double)(par3 + 1) + 0.01, var50, var24, var32);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var50, var24, var30);
            }

            if (par3 < var5 - 1 && this.blockAccess.isAirBlock(par2 + 1, par3 + 1, par4)) {
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var52, var26, var28);
               var6.addVertexWithUV(var38, (double)(par3 + 1) + 0.01, var52, var26, var30);
               var6.addVertexWithUV(var38, (double)(par3 + 1) + 0.01, var50, var24, var30);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var50, var24, var28);
               var6.addVertexWithUV(var38, (double)(par3 + 1) + 0.01, var52, var26, var28);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var52, var26, var30);
               var6.addVertexWithUV(var36, (double)(par3 + 1) + 0.01, var50, var24, var30);
               var6.addVertexWithUV(var38, (double)(par3 + 1) + 0.01, var50, var24, var28);
            }
         }

         if (var59) {
            var6.addVertexWithUV(var34, (double)par3 - 0.01, var52, var26, var32);
            var6.addVertexWithUV(var38, (double)par3 - 0.01, var52, var26, var28);
            var6.addVertexWithUV(var38, (double)par3 - 0.01, var50, var24, var28);
            var6.addVertexWithUV(var34, (double)par3 - 0.01, var50, var24, var32);
            var6.addVertexWithUV(var38, (double)par3 - 0.01, var52, var26, var32);
            var6.addVertexWithUV(var34, (double)par3 - 0.01, var52, var26, var28);
            var6.addVertexWithUV(var34, (double)par3 - 0.01, var50, var24, var28);
            var6.addVertexWithUV(var38, (double)par3 - 0.01, var50, var24, var32);
         } else {
            if (par3 > 1 && this.blockAccess.isAirBlock(par2 - 1, par3 - 1, par4)) {
               var6.addVertexWithUV(var34, (double)par3 - 0.01, var52, var26, var30);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var52, var26, var32);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var50, var24, var32);
               var6.addVertexWithUV(var34, (double)par3 - 0.01, var50, var24, var30);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var52, var26, var30);
               var6.addVertexWithUV(var34, (double)par3 - 0.01, var52, var26, var32);
               var6.addVertexWithUV(var34, (double)par3 - 0.01, var50, var24, var32);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var50, var24, var30);
            }

            if (par3 > 1 && this.blockAccess.isAirBlock(par2 + 1, par3 - 1, par4)) {
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var52, var26, var28);
               var6.addVertexWithUV(var38, (double)par3 - 0.01, var52, var26, var30);
               var6.addVertexWithUV(var38, (double)par3 - 0.01, var50, var24, var30);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var50, var24, var28);
               var6.addVertexWithUV(var38, (double)par3 - 0.01, var52, var26, var28);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var52, var26, var30);
               var6.addVertexWithUV(var36, (double)par3 - 0.01, var50, var24, var30);
               var6.addVertexWithUV(var38, (double)par3 - 0.01, var50, var24, var28);
            }
         }
      }

      if ((!var54 || !var55) && (var56 || var57 || var54 || var55)) {
         if (var54 && !var55) {
            var6.addVertexWithUV(var36, (double)(par3 + 1), var40, var67, var20);
            var6.addVertexWithUV(var36, (double)(par3 + 0), var40, var67, var22);
            var6.addVertexWithUV(var36, (double)(par3 + 0), var42, var16, var22);
            var6.addVertexWithUV(var36, (double)(par3 + 1), var42, var16, var20);
            var6.addVertexWithUV(var36, (double)(par3 + 1), var42, var67, var20);
            var6.addVertexWithUV(var36, (double)(par3 + 0), var42, var67, var22);
            var6.addVertexWithUV(var36, (double)(par3 + 0), var40, var16, var22);
            var6.addVertexWithUV(var36, (double)(par3 + 1), var40, var16, var20);
            if (!var57 && !var56) {
               var6.addVertexWithUV(var46, (double)(par3 + 1), var42, var24, var28);
               var6.addVertexWithUV(var46, (double)(par3 + 0), var42, var24, var32);
               var6.addVertexWithUV(var48, (double)(par3 + 0), var42, var26, var32);
               var6.addVertexWithUV(var48, (double)(par3 + 1), var42, var26, var28);
               var6.addVertexWithUV(var48, (double)(par3 + 1), var42, var24, var28);
               var6.addVertexWithUV(var48, (double)(par3 + 0), var42, var24, var32);
               var6.addVertexWithUV(var46, (double)(par3 + 0), var42, var26, var32);
               var6.addVertexWithUV(var46, (double)(par3 + 1), var42, var26, var28);
            }

            if (var58 || par3 < var5 - 1 && this.blockAccess.isAirBlock(par2, par3 + 1, par4 - 1)) {
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var40, var26, var28);
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var42, var26, var30);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var42, var24, var30);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var40, var24, var28);
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var42, var26, var28);
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var40, var26, var30);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var40, var24, var30);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var42, var24, var28);
            }

            if (var59 || par3 > 1 && this.blockAccess.isAirBlock(par2, par3 - 1, par4 - 1)) {
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var40, var26, var28);
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var42, var26, var30);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var42, var24, var30);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var40, var24, var28);
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var42, var26, var28);
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var40, var26, var30);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var40, var24, var30);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var42, var24, var28);
            }
         } else if (!var54 && var55) {
            var6.addVertexWithUV(var36, (double)(par3 + 1), var42, var16, var20);
            var6.addVertexWithUV(var36, (double)(par3 + 0), var42, var16, var22);
            var6.addVertexWithUV(var36, (double)(par3 + 0), var44, var18, var22);
            var6.addVertexWithUV(var36, (double)(par3 + 1), var44, var18, var20);
            var6.addVertexWithUV(var36, (double)(par3 + 1), var44, var16, var20);
            var6.addVertexWithUV(var36, (double)(par3 + 0), var44, var16, var22);
            var6.addVertexWithUV(var36, (double)(par3 + 0), var42, var18, var22);
            var6.addVertexWithUV(var36, (double)(par3 + 1), var42, var18, var20);
            if (!var57 && !var56) {
               var6.addVertexWithUV(var48, (double)(par3 + 1), var42, var24, var28);
               var6.addVertexWithUV(var48, (double)(par3 + 0), var42, var24, var32);
               var6.addVertexWithUV(var46, (double)(par3 + 0), var42, var26, var32);
               var6.addVertexWithUV(var46, (double)(par3 + 1), var42, var26, var28);
               var6.addVertexWithUV(var46, (double)(par3 + 1), var42, var24, var28);
               var6.addVertexWithUV(var46, (double)(par3 + 0), var42, var24, var32);
               var6.addVertexWithUV(var48, (double)(par3 + 0), var42, var26, var32);
               var6.addVertexWithUV(var48, (double)(par3 + 1), var42, var26, var28);
            }

            if (var58 || par3 < var5 - 1 && this.blockAccess.isAirBlock(par2, par3 + 1, par4 + 1)) {
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var42, var24, var30);
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var44, var24, var32);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var44, var26, var32);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var42, var26, var30);
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var44, var24, var30);
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var42, var24, var32);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var42, var26, var32);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var44, var26, var30);
            }

            if (var59 || par3 > 1 && this.blockAccess.isAirBlock(par2, par3 - 1, par4 + 1)) {
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var42, var24, var30);
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var44, var24, var32);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var44, var26, var32);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var42, var26, var30);
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var44, var24, var30);
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var42, var24, var32);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var42, var26, var32);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var44, var26, var30);
            }
         }
      } else {
         var6.addVertexWithUV(var36, (double)(par3 + 1), var44, var67, var20);
         var6.addVertexWithUV(var36, (double)(par3 + 0), var44, var67, var22);
         var6.addVertexWithUV(var36, (double)(par3 + 0), var40, var18, var22);
         var6.addVertexWithUV(var36, (double)(par3 + 1), var40, var18, var20);
         var6.addVertexWithUV(var36, (double)(par3 + 1), var40, var67, var20);
         var6.addVertexWithUV(var36, (double)(par3 + 0), var40, var67, var22);
         var6.addVertexWithUV(var36, (double)(par3 + 0), var44, var18, var22);
         var6.addVertexWithUV(var36, (double)(par3 + 1), var44, var18, var20);
         if (var58) {
            var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var44, var26, var32);
            var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var40, var26, var28);
            var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var40, var24, var28);
            var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var44, var24, var32);
            var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var40, var26, var32);
            var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var44, var26, var28);
            var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var44, var24, var28);
            var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var40, var24, var32);
         } else {
            if (par3 < var5 - 1 && this.blockAccess.isAirBlock(par2, par3 + 1, par4 - 1)) {
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var40, var26, var28);
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var42, var26, var30);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var42, var24, var30);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var40, var24, var28);
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var42, var26, var28);
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var40, var26, var30);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var40, var24, var30);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var42, var24, var28);
            }

            if (par3 < var5 - 1 && this.blockAccess.isAirBlock(par2, par3 + 1, par4 + 1)) {
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var42, var24, var30);
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var44, var24, var32);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var44, var26, var32);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var42, var26, var30);
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var44, var24, var30);
               var6.addVertexWithUV(var46, (double)(par3 + 1) + 0.005, var42, var24, var32);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var42, var26, var32);
               var6.addVertexWithUV(var48, (double)(par3 + 1) + 0.005, var44, var26, var30);
            }
         }

         if (var59) {
            var6.addVertexWithUV(var48, (double)par3 - 0.005, var44, var26, var32);
            var6.addVertexWithUV(var48, (double)par3 - 0.005, var40, var26, var28);
            var6.addVertexWithUV(var46, (double)par3 - 0.005, var40, var24, var28);
            var6.addVertexWithUV(var46, (double)par3 - 0.005, var44, var24, var32);
            var6.addVertexWithUV(var48, (double)par3 - 0.005, var40, var26, var32);
            var6.addVertexWithUV(var48, (double)par3 - 0.005, var44, var26, var28);
            var6.addVertexWithUV(var46, (double)par3 - 0.005, var44, var24, var28);
            var6.addVertexWithUV(var46, (double)par3 - 0.005, var40, var24, var32);
         } else {
            if (par3 > 1 && this.blockAccess.isAirBlock(par2, par3 - 1, par4 - 1)) {
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var40, var26, var28);
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var42, var26, var30);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var42, var24, var30);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var40, var24, var28);
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var42, var26, var28);
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var40, var26, var30);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var40, var24, var30);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var42, var24, var28);
            }

            if (par3 > 1 && this.blockAccess.isAirBlock(par2, par3 - 1, par4 + 1)) {
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var42, var24, var30);
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var44, var24, var32);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var44, var26, var32);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var42, var26, var30);
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var44, var24, var30);
               var6.addVertexWithUV(var46, (double)par3 - 0.005, var42, var24, var32);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var42, var26, var32);
               var6.addVertexWithUV(var48, (double)par3 - 0.005, var44, var26, var30);
            }
         }
      }

      return true;
   }

   public boolean renderCrossedSquares(Block par1Block, int par2, int par3, int par4) {
      Tessellator.instance.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var6 = 1.0F;
      int var7 = par1Block.colorMultiplier(this.blockAccess, par2, par3, par4);
      float var8 = (float)(var7 >> 16 & 255) / 255.0F;
      float var9 = (float)(var7 >> 8 & 255) / 255.0F;
      float var10 = (float)(var7 & 255) / 255.0F;
      if (EntityRenderer.anaglyphEnable) {
         float var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
         float var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
         float var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
         var8 = var11;
         var9 = var12;
         var10 = var13;
      }

      Tessellator.instance.setColorOpaque_F(var6 * var8, var6 * var9, var6 * var10);
      double var19 = (double)par2;
      double var20 = (double)par3;
      double var15 = (double)par4;
      if (par1Block == Block.tallGrass) {
         long var17 = (long)(par2 * 3129871) ^ (long)par4 * 116129781L ^ (long)par3;
         var17 = var17 * var17 * 42317861L + var17 * 11L;
         var19 += ((double)((float)(var17 >> 16 & 15L) / 15.0F) - 0.5) * 0.5;
         var20 += ((double)((float)(var17 >> 20 & 15L) / 15.0F) - 1.0) * 0.2;
         var15 += ((double)((float)(var17 >> 24 & 15L) / 15.0F) - 0.5) * 0.5;
      }

      this.drawCrossedSquares(par1Block, this.blockAccess.getBlockMetadata(par2, par3, par4), var19, var20, var15, 1.0F);
      return true;
   }

   public boolean renderBlockStem(Block par1Block, int par2, int par3, int par4) {
      BlockStem var5 = (BlockStem)par1Block;
      Tessellator var6 = Tessellator.instance;
      var6.setBrightness(var5.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var7 = 1.0F;
      int var8 = var5.colorMultiplier(this.blockAccess, par2, par3, par4);
      float var9 = (float)(var8 >> 16 & 255) / 255.0F;
      float var10 = (float)(var8 >> 8 & 255) / 255.0F;
      float var11 = (float)(var8 & 255) / 255.0F;
      if (EntityRenderer.anaglyphEnable) {
         float var12 = (var9 * 30.0F + var10 * 59.0F + var11 * 11.0F) / 100.0F;
         float var13 = (var9 * 30.0F + var10 * 70.0F) / 100.0F;
         float var14 = (var9 * 30.0F + var11 * 70.0F) / 100.0F;
         var9 = var12;
         var10 = var13;
         var11 = var14;
      }

      var6.setColorOpaque_F(var7 * var9, var7 * var10, var7 * var11);
      var5.setBlockBoundsBasedOnStateAndNeighbors(this.blockAccess, par2, par3, par4);
      int var15 = var5.getState(this.blockAccess, par2, par3, par4);
      if (var15 < 0) {
         this.renderBlockStemSmall(var5, this.blockAccess.getBlockMetadata(par2, par3, par4), this.renderMaxY, (double)par2, (double)((float)par3 - 0.0625F), (double)par4);
      } else {
         this.renderBlockStemSmall(var5, this.blockAccess.getBlockMetadata(par2, par3, par4), 0.5, (double)par2, (double)((float)par3 - 0.0625F), (double)par4);
         this.renderBlockStemBig(var5, this.blockAccess.getBlockMetadata(par2, par3, par4), var15, 1.0, (double)par2, (double)((float)par3 - 0.0625F), (double)par4);
      }

      return true;
   }

   public boolean renderBlockCrops(Block par1Block, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      var5.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      var5.setColorOpaque_F(1.0F, 1.0F, 1.0F);
      this.renderBlockCropsImpl(par1Block, this.blockAccess.getBlockMetadata(par2, par3, par4), (double)par2, (double)((float)par3 - 0.0625F), (double)par4);
      return true;
   }

   public void renderTorchAtAngle(Block par1Block, double par2, double par4, double par6, double par8, double par10, int par12) {
      Tessellator var13 = Tessellator.instance;
      Icon var14 = this.overrideBlockTexture == null ? this.getBlockIconFromSideAndMetadata(par1Block, 0, par12) : this.overrideBlockTexture;
      double var15 = (double)var14.getMinU();
      double var17 = (double)var14.getMinV();
      double var19 = (double)var14.getMaxU();
      double var21 = (double)var14.getMaxV();
      double var23 = (double)var14.getInterpolatedU(7.0);
      double var25 = (double)var14.getInterpolatedV(6.0);
      double var27 = (double)var14.getInterpolatedU(9.0);
      double var29 = (double)var14.getInterpolatedV(8.0);
      double var31 = (double)var14.getInterpolatedU(7.0);
      double var33 = (double)var14.getInterpolatedV(13.0);
      double var35 = (double)var14.getInterpolatedU(9.0);
      double var37 = (double)var14.getInterpolatedV(15.0);
      par2 += 0.5;
      par6 += 0.5;
      double var39 = par2 - 0.5;
      double var41 = par2 + 0.5;
      double var43 = par6 - 0.5;
      double var45 = par6 + 0.5;
      double var47 = 0.0625;
      double var49 = 0.625;
      var13.addVertexWithUV(par2 + par8 * (1.0 - var49) - var47, par4 + var49, par6 + par10 * (1.0 - var49) - var47, var23, var25);
      var13.addVertexWithUV(par2 + par8 * (1.0 - var49) - var47, par4 + var49, par6 + par10 * (1.0 - var49) + var47, var23, var29);
      var13.addVertexWithUV(par2 + par8 * (1.0 - var49) + var47, par4 + var49, par6 + par10 * (1.0 - var49) + var47, var27, var29);
      var13.addVertexWithUV(par2 + par8 * (1.0 - var49) + var47, par4 + var49, par6 + par10 * (1.0 - var49) - var47, var27, var25);
      var13.addVertexWithUV(par2 + var47 + par8, par4, par6 - var47 + par10, var35, var33);
      var13.addVertexWithUV(par2 + var47 + par8, par4, par6 + var47 + par10, var35, var37);
      var13.addVertexWithUV(par2 - var47 + par8, par4, par6 + var47 + par10, var31, var37);
      var13.addVertexWithUV(par2 - var47 + par8, par4, par6 - var47 + par10, var31, var33);
      var13.addVertexWithUV(par2 - var47, par4 + 1.0, var43, var15, var17);
      var13.addVertexWithUV(par2 - var47 + par8, par4 + 0.0, var43 + par10, var15, var21);
      var13.addVertexWithUV(par2 - var47 + par8, par4 + 0.0, var45 + par10, var19, var21);
      var13.addVertexWithUV(par2 - var47, par4 + 1.0, var45, var19, var17);
      var13.addVertexWithUV(par2 + var47, par4 + 1.0, var45, var15, var17);
      var13.addVertexWithUV(par2 + par8 + var47, par4 + 0.0, var45 + par10, var15, var21);
      var13.addVertexWithUV(par2 + par8 + var47, par4 + 0.0, var43 + par10, var19, var21);
      var13.addVertexWithUV(par2 + var47, par4 + 1.0, var43, var19, var17);
      var13.addVertexWithUV(var39, par4 + 1.0, par6 + var47, var15, var17);
      var13.addVertexWithUV(var39 + par8, par4 + 0.0, par6 + var47 + par10, var15, var21);
      var13.addVertexWithUV(var41 + par8, par4 + 0.0, par6 + var47 + par10, var19, var21);
      var13.addVertexWithUV(var41, par4 + 1.0, par6 + var47, var19, var17);
      var13.addVertexWithUV(var41, par4 + 1.0, par6 - var47, var15, var17);
      var13.addVertexWithUV(var41 + par8, par4 + 0.0, par6 - var47 + par10, var15, var21);
      var13.addVertexWithUV(var39 + par8, par4 + 0.0, par6 - var47 + par10, var19, var21);
      var13.addVertexWithUV(var39, par4 + 1.0, par6 - var47, var19, var17);
   }

   public void drawCrossedSquares(Block par1Block, int par2, double par3, double par5, double par7, float par9) {
      Tessellator var10 = Tessellator.instance;
      Icon var11 = this.overrideBlockTexture == null ? this.getBlockIconFromSideAndMetadata(par1Block, 0, par2) : this.overrideBlockTexture;
      double var12 = (double)var11.getMinU();
      double var14 = (double)var11.getMinV();
      double var16 = (double)var11.getMaxU();
      double var18 = (double)var11.getMaxV();
      double var20 = 0.45 * (double)par9;
      double var22 = par3 + 0.5 - var20;
      double var24 = par3 + 0.5 + var20;
      double var26 = par7 + 0.5 - var20;
      double var28 = par7 + 0.5 + var20;
      if (RenderingScheme.current == 0) {
         var10.addVertexWithUV(var22, par5 + (double)par9, var26, var12, var14);
         var10.addVertexWithUV(var22, par5 + 0.0, var26, var12, var18);
         var10.addVertexWithUV(var24, par5 + 0.0, var28, var16, var18);
         var10.addVertexWithUV(var24, par5 + (double)par9, var28, var16, var14);
         var10.addVertexWithUV(var24, par5 + (double)par9, var28, var12, var14);
         var10.addVertexWithUV(var24, par5 + 0.0, var28, var12, var18);
         var10.addVertexWithUV(var22, par5 + 0.0, var26, var16, var18);
         var10.addVertexWithUV(var22, par5 + (double)par9, var26, var16, var14);
         var10.addVertexWithUV(var22, par5 + (double)par9, var28, var12, var14);
         var10.addVertexWithUV(var22, par5 + 0.0, var28, var12, var18);
         var10.addVertexWithUV(var24, par5 + 0.0, var26, var16, var18);
         var10.addVertexWithUV(var24, par5 + (double)par9, var26, var16, var14);
         var10.addVertexWithUV(var24, par5 + (double)par9, var26, var12, var14);
         var10.addVertexWithUV(var24, par5 + 0.0, var26, var12, var18);
         var10.addVertexWithUV(var22, par5 + 0.0, var28, var16, var18);
         var10.addVertexWithUV(var22, par5 + (double)par9, var28, var16, var14);
      } else {
         this.x[0] = var22;
         this.y[0] = par5 + (double)par9;
         this.z[0] = var26;
         this.u[0] = var12;
         this.v[0] = var14;
         this.x[1] = var22;
         this.y[1] = par5;
         this.z[1] = var26;
         this.u[1] = var12;
         this.v[1] = var18;
         this.x[2] = var24;
         this.y[2] = par5;
         this.z[2] = var28;
         this.u[2] = var16;
         this.v[2] = var18;
         this.x[3] = var24;
         this.y[3] = par5 + (double)par9;
         this.z[3] = var28;
         this.u[3] = var16;
         this.v[3] = var14;
         var10.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
         this.x[0] = var24;
         this.y[0] = par5 + (double)par9;
         this.z[0] = var28;
         this.u[0] = var12;
         this.v[0] = var14;
         this.x[1] = var24;
         this.y[1] = par5;
         this.z[1] = var28;
         this.u[1] = var12;
         this.v[1] = var18;
         this.x[2] = var22;
         this.y[2] = par5;
         this.z[2] = var26;
         this.u[2] = var16;
         this.v[2] = var18;
         this.x[3] = var22;
         this.y[3] = par5 + (double)par9;
         this.z[3] = var26;
         this.u[3] = var16;
         this.v[3] = var14;
         var10.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
         this.x[0] = var22;
         this.y[0] = par5 + (double)par9;
         this.z[0] = var28;
         this.u[0] = var12;
         this.v[0] = var14;
         this.x[1] = var22;
         this.y[1] = par5;
         this.z[1] = var28;
         this.u[1] = var12;
         this.v[1] = var18;
         this.x[2] = var24;
         this.y[2] = par5;
         this.z[2] = var26;
         this.u[2] = var16;
         this.v[2] = var18;
         this.x[3] = var24;
         this.y[3] = par5 + (double)par9;
         this.z[3] = var26;
         this.u[3] = var16;
         this.v[3] = var14;
         var10.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
         this.x[0] = var24;
         this.y[0] = par5 + (double)par9;
         this.z[0] = var26;
         this.u[0] = var12;
         this.v[0] = var14;
         this.x[1] = var24;
         this.y[1] = par5;
         this.z[1] = var26;
         this.u[1] = var12;
         this.v[1] = var18;
         this.x[2] = var22;
         this.y[2] = par5;
         this.z[2] = var28;
         this.u[2] = var16;
         this.v[2] = var18;
         this.x[3] = var22;
         this.y[3] = par5 + (double)par9;
         this.z[3] = var28;
         this.u[3] = var16;
         this.v[3] = var14;
         var10.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
      }

   }

   public void renderBlockStemSmall(Block par1Block, int par2, double par3, double par5, double par7, double par9) {
      Tessellator var11 = Tessellator.instance;
      Icon var12 = this.overrideBlockTexture == null ? this.getBlockIconFromSideAndMetadata(par1Block, 0, par2) : this.overrideBlockTexture;
      double var13 = (double)var12.getMinU();
      double var15 = (double)var12.getMinV();
      double var17 = (double)var12.getMaxU();
      double var19 = (double)var12.getInterpolatedV(par3 * 16.0);
      double var21 = par5 + 0.5 - 0.44999998807907104;
      double var23 = par5 + 0.5 + 0.44999998807907104;
      double var25 = par9 + 0.5 - 0.44999998807907104;
      double var27 = par9 + 0.5 + 0.44999998807907104;
      var11.addVertexWithUV(var21, par7 + par3, var25, var13, var15);
      var11.addVertexWithUV(var21, par7 + 0.0, var25, var13, var19);
      var11.addVertexWithUV(var23, par7 + 0.0, var27, var17, var19);
      var11.addVertexWithUV(var23, par7 + par3, var27, var17, var15);
      var11.addVertexWithUV(var23, par7 + par3, var27, var13, var15);
      var11.addVertexWithUV(var23, par7 + 0.0, var27, var13, var19);
      var11.addVertexWithUV(var21, par7 + 0.0, var25, var17, var19);
      var11.addVertexWithUV(var21, par7 + par3, var25, var17, var15);
      var11.addVertexWithUV(var21, par7 + par3, var27, var13, var15);
      var11.addVertexWithUV(var21, par7 + 0.0, var27, var13, var19);
      var11.addVertexWithUV(var23, par7 + 0.0, var25, var17, var19);
      var11.addVertexWithUV(var23, par7 + par3, var25, var17, var15);
      var11.addVertexWithUV(var23, par7 + par3, var25, var13, var15);
      var11.addVertexWithUV(var23, par7 + 0.0, var25, var13, var19);
      var11.addVertexWithUV(var21, par7 + 0.0, var27, var17, var19);
      var11.addVertexWithUV(var21, par7 + par3, var27, var17, var15);
   }

   public boolean renderBlockLilyPad(Block par1Block, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      Icon var6 = this.overrideBlockTexture == null ? this.getBlockIconFromSide(par1Block, 1) : this.overrideBlockTexture;
      float var7 = 0.015625F;
      double var8 = (double)var6.getMinU();
      double var10 = (double)var6.getMinV();
      double var12 = (double)var6.getMaxU();
      double var14 = (double)var6.getMaxV();
      long var16 = (long)(par2 * 3129871) ^ (long)par4 * 116129781L ^ (long)par3;
      var16 = var16 * var16 * 42317861L + var16 * 11L;
      int var18 = (int)(var16 >> 16 & 3L);
      var5.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var19 = (float)par2 + 0.5F;
      float var20 = (float)par4 + 0.5F;
      float var21 = (float)(var18 & 1) * 0.5F * (float)(1 - var18 / 2 % 2 * 2);
      float var22 = (float)(var18 + 1 & 1) * 0.5F * (float)(1 - (var18 + 1) / 2 % 2 * 2);
      var5.setColorOpaque_I(par1Block.getBlockColor());
      var5.addVertexWithUV((double)(var19 + var21 - var22), (double)((float)par3 + var7), (double)(var20 + var21 + var22), var8, var10);
      var5.addVertexWithUV((double)(var19 + var21 + var22), (double)((float)par3 + var7), (double)(var20 - var21 + var22), var12, var10);
      var5.addVertexWithUV((double)(var19 - var21 + var22), (double)((float)par3 + var7), (double)(var20 - var21 - var22), var12, var14);
      var5.addVertexWithUV((double)(var19 - var21 - var22), (double)((float)par3 + var7), (double)(var20 + var21 - var22), var8, var14);
      var5.setColorOpaque_I((par1Block.getBlockColor() & 16711422) >> 1);
      var5.addVertexWithUV((double)(var19 - var21 - var22), (double)((float)par3 + var7), (double)(var20 + var21 - var22), var8, var14);
      var5.addVertexWithUV((double)(var19 - var21 + var22), (double)((float)par3 + var7), (double)(var20 - var21 - var22), var12, var14);
      var5.addVertexWithUV((double)(var19 + var21 + var22), (double)((float)par3 + var7), (double)(var20 - var21 + var22), var12, var10);
      var5.addVertexWithUV((double)(var19 + var21 - var22), (double)((float)par3 + var7), (double)(var20 + var21 + var22), var8, var10);
      return true;
   }

   public void renderBlockStemBig(BlockStem par1BlockStem, int par2, int par3, double par4, double par6, double par8, double par10) {
      Tessellator var12 = Tessellator.instance;
      Icon var13 = this.overrideBlockTexture == null ? par1BlockStem.getStemIcon() : this.overrideBlockTexture;
      double var14 = (double)var13.getMinU();
      double var16 = (double)var13.getMinV();
      double var18 = (double)var13.getMaxU();
      double var20 = (double)var13.getMaxV();
      double var22 = par6 + 0.5 - 0.5;
      double var24 = par6 + 0.5 + 0.5;
      double var26 = par10 + 0.5 - 0.5;
      double var28 = par10 + 0.5 + 0.5;
      double var30 = par6 + 0.5;
      double var32 = par10 + 0.5;
      if ((par3 + 1) / 2 % 2 == 1) {
         double var34 = var18;
         var18 = var14;
         var14 = var34;
      }

      if (par3 < 2) {
         var12.addVertexWithUV(var22, par8 + par4, var32, var14, var16);
         var12.addVertexWithUV(var22, par8 + 0.0, var32, var14, var20);
         var12.addVertexWithUV(var24, par8 + 0.0, var32, var18, var20);
         var12.addVertexWithUV(var24, par8 + par4, var32, var18, var16);
         var12.addVertexWithUV(var24, par8 + par4, var32, var18, var16);
         var12.addVertexWithUV(var24, par8 + 0.0, var32, var18, var20);
         var12.addVertexWithUV(var22, par8 + 0.0, var32, var14, var20);
         var12.addVertexWithUV(var22, par8 + par4, var32, var14, var16);
      } else {
         var12.addVertexWithUV(var30, par8 + par4, var28, var14, var16);
         var12.addVertexWithUV(var30, par8 + 0.0, var28, var14, var20);
         var12.addVertexWithUV(var30, par8 + 0.0, var26, var18, var20);
         var12.addVertexWithUV(var30, par8 + par4, var26, var18, var16);
         var12.addVertexWithUV(var30, par8 + par4, var26, var18, var16);
         var12.addVertexWithUV(var30, par8 + 0.0, var26, var18, var20);
         var12.addVertexWithUV(var30, par8 + 0.0, var28, var14, var20);
         var12.addVertexWithUV(var30, par8 + par4, var28, var14, var16);
      }

   }

   public void renderBlockCropsImpl(Block par1Block, int par2, double par3, double par5, double par7) {
      Tessellator var9 = Tessellator.instance;
      Icon var10 = this.overrideBlockTexture == null ? this.getBlockIconFromSideAndMetadata(par1Block, 0, par2) : this.overrideBlockTexture;
      double var11 = (double)var10.getMinU();
      double var13 = (double)var10.getMinV();
      double var15 = (double)var10.getMaxU();
      double var17 = (double)var10.getMaxV();
      double var19 = par3 + 0.5 - 0.25;
      double var21 = par3 + 0.5 + 0.25;
      double var23 = par7 + 0.5 - 0.5;
      double var25 = par7 + 0.5 + 0.5;
      var9.addVertexWithUV(var19, par5 + 1.0, var23, var11, var13);
      var9.addVertexWithUV(var19, par5 + 0.0, var23, var11, var17);
      var9.addVertexWithUV(var19, par5 + 0.0, var25, var15, var17);
      var9.addVertexWithUV(var19, par5 + 1.0, var25, var15, var13);
      var9.addVertexWithUV(var19, par5 + 1.0, var25, var11, var13);
      var9.addVertexWithUV(var19, par5 + 0.0, var25, var11, var17);
      var9.addVertexWithUV(var19, par5 + 0.0, var23, var15, var17);
      var9.addVertexWithUV(var19, par5 + 1.0, var23, var15, var13);
      var9.addVertexWithUV(var21, par5 + 1.0, var25, var11, var13);
      var9.addVertexWithUV(var21, par5 + 0.0, var25, var11, var17);
      var9.addVertexWithUV(var21, par5 + 0.0, var23, var15, var17);
      var9.addVertexWithUV(var21, par5 + 1.0, var23, var15, var13);
      var9.addVertexWithUV(var21, par5 + 1.0, var23, var11, var13);
      var9.addVertexWithUV(var21, par5 + 0.0, var23, var11, var17);
      var9.addVertexWithUV(var21, par5 + 0.0, var25, var15, var17);
      var9.addVertexWithUV(var21, par5 + 1.0, var25, var15, var13);
      var19 = par3 + 0.5 - 0.5;
      var21 = par3 + 0.5 + 0.5;
      var23 = par7 + 0.5 - 0.25;
      var25 = par7 + 0.5 + 0.25;
      var9.addVertexWithUV(var19, par5 + 1.0, var23, var11, var13);
      var9.addVertexWithUV(var19, par5 + 0.0, var23, var11, var17);
      var9.addVertexWithUV(var21, par5 + 0.0, var23, var15, var17);
      var9.addVertexWithUV(var21, par5 + 1.0, var23, var15, var13);
      var9.addVertexWithUV(var21, par5 + 1.0, var23, var11, var13);
      var9.addVertexWithUV(var21, par5 + 0.0, var23, var11, var17);
      var9.addVertexWithUV(var19, par5 + 0.0, var23, var15, var17);
      var9.addVertexWithUV(var19, par5 + 1.0, var23, var15, var13);
      var9.addVertexWithUV(var21, par5 + 1.0, var25, var11, var13);
      var9.addVertexWithUV(var21, par5 + 0.0, var25, var11, var17);
      var9.addVertexWithUV(var19, par5 + 0.0, var25, var15, var17);
      var9.addVertexWithUV(var19, par5 + 1.0, var25, var15, var13);
      var9.addVertexWithUV(var19, par5 + 1.0, var25, var11, var13);
      var9.addVertexWithUV(var19, par5 + 0.0, var25, var11, var17);
      var9.addVertexWithUV(var21, par5 + 0.0, var25, var15, var17);
      var9.addVertexWithUV(var21, par5 + 1.0, var25, var15, var13);
   }

   public boolean renderBlockFluids(Block par1Block, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      int var6 = par1Block.colorMultiplier(this.blockAccess, par2, par3, par4);
      float var7 = (float)(var6 >> 16 & 255) / 255.0F;
      float var8 = (float)(var6 >> 8 & 255) / 255.0F;
      float var9 = (float)(var6 & 255) / 255.0F;
      boolean var10 = par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 + 1, par4, 1);
      boolean var11 = par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 - 1, par4, 0);
      boolean[] var12 = new boolean[]{par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 - 1, 2), par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 + 1, 3), par1Block.shouldSideBeRendered(this.blockAccess, par2 - 1, par3, par4, 4), par1Block.shouldSideBeRendered(this.blockAccess, par2 + 1, par3, par4, 5)};
      if (!var10 && !var11 && !var12[0] && !var12[1] && !var12[2] && !var12[3]) {
         return false;
      } else {
         boolean var13 = false;
         float var14 = 0.5F;
         float var15 = 1.0F;
         float var16 = 0.8F;
         float var17 = 0.6F;
         double var18 = 0.0;
         double var20 = 1.0;
         Material var22 = par1Block.blockMaterial;
         int var23 = this.blockAccess.getBlockMetadata(par2, par3, par4);
         double var24 = (double)this.getFluidHeight(par2, par3, par4, var22);
         double var26 = (double)this.getFluidHeight(par2, par3, par4 + 1, var22);
         double var28 = (double)this.getFluidHeight(par2 + 1, par3, par4 + 1, var22);
         double var30 = (double)this.getFluidHeight(par2 + 1, par3, par4, var22);
         double var32 = 0.0010000000474974513;
         float var54;
         float var53;
         float var52;
         double var39;
         double var43;
         double var41;
         double var47;
         double var45;
         double var49;
         if (this.renderAllFaces || var10) {
            var13 = true;
            Icon var34 = this.getBlockIconFromSideAndMetadata(par1Block, 1, var23);
            float var35 = (float)BlockFluid.getFlowDirection(this.blockAccess, par2, par3, par4, var22);
            if (var35 > -999.0F) {
               var34 = this.getBlockIconFromSideAndMetadata(par1Block, 2, var23);
            }

            var24 -= var32;
            var26 -= var32;
            var28 -= var32;
            var30 -= var32;
            double var38;
            double var48;
            if (var35 < -999.0F) {
               var39 = (double)var34.getInterpolatedU(0.0);
               var45 = (double)var34.getInterpolatedV(0.0);
               var38 = var39;
               var47 = (double)var34.getInterpolatedV(16.0);
               var41 = (double)var34.getInterpolatedU(16.0);
               var48 = var47;
               var43 = var41;
               var49 = var45;
            } else {
               var52 = MathHelper.sin(var35) * 0.25F;
               var53 = MathHelper.cos(var35) * 0.25F;
               var54 = 8.0F;
               var39 = (double)var34.getInterpolatedU((double)(8.0F + (-var53 - var52) * 16.0F));
               var45 = (double)var34.getInterpolatedV((double)(8.0F + (-var53 + var52) * 16.0F));
               var38 = (double)var34.getInterpolatedU((double)(8.0F + (-var53 + var52) * 16.0F));
               var47 = (double)var34.getInterpolatedV((double)(8.0F + (var53 + var52) * 16.0F));
               var41 = (double)var34.getInterpolatedU((double)(8.0F + (var53 + var52) * 16.0F));
               var48 = (double)var34.getInterpolatedV((double)(8.0F + (var53 - var52) * 16.0F));
               var43 = (double)var34.getInterpolatedU((double)(8.0F + (var53 - var52) * 16.0F));
               var49 = (double)var34.getInterpolatedV((double)(8.0F + (-var53 - var52) * 16.0F));
            }

            var5.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
            var52 = 1.0F;
            var5.setColorOpaque_F(var15 * var52 * var7, var15 * var52 * var8, var15 * var52 * var9);
            if (RenderingScheme.current == 0) {
               var5.addVertexWithUV((double)(par2 + 0), (double)par3 + var24, (double)(par4 + 0), var39, var45);
               var5.addVertexWithUV((double)(par2 + 0), (double)par3 + var26, (double)(par4 + 1), var38, var47);
               var5.addVertexWithUV((double)(par2 + 1), (double)par3 + var28, (double)(par4 + 1), var41, var48);
               var5.addVertexWithUV((double)(par2 + 1), (double)par3 + var30, (double)(par4 + 0), var43, var49);
            } else {
               this.x[0] = (double)par2;
               this.y[0] = (double)par3 + var24;
               this.z[0] = (double)par4;
               this.u[0] = var39;
               this.v[0] = var45;
               this.x[1] = (double)par2;
               this.y[1] = (double)par3 + var26;
               this.z[1] = (double)(par4 + 1);
               this.u[1] = var38;
               this.v[1] = var47;
               this.x[2] = (double)(par2 + 1);
               this.y[2] = (double)par3 + var28;
               this.z[2] = (double)(par4 + 1);
               this.u[2] = var41;
               this.v[2] = var48;
               this.x[3] = (double)(par2 + 1);
               this.y[3] = (double)par3 + var30;
               this.z[3] = (double)par4;
               this.u[3] = var43;
               this.v[3] = var49;
               var5.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
            }
         }

         if (this.renderAllFaces || var11) {
            var5.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4));
            float var58 = 1.0F;
            var5.setColorOpaque_F(var14 * var58, var14 * var58, var14 * var58);
            this.renderFaceYNeg(par1Block, (double)par2, (double)par3 + var32, (double)par4, this.getBlockIconFromSide(par1Block, 0));
            var13 = true;
         }

         for(int var57 = 0; var57 < 4; ++var57) {
            int var59 = par2;
            int var37 = par4;
            if (var57 == 0) {
               var37 = par4 - 1;
            }

            if (var57 == 1) {
               ++var37;
            }

            if (var57 == 2) {
               var59 = par2 - 1;
            }

            if (var57 == 3) {
               ++var59;
            }

            Icon var60 = this.getBlockIconFromSideAndMetadata(par1Block, var57 + 2, var23);
            if (this.renderAllFaces || var12[var57]) {
               if (var57 == 0) {
                  var39 = var24;
                  var41 = var30;
                  var43 = (double)par2;
                  var47 = (double)(par2 + 1);
                  var45 = (double)par4 + var32;
                  var49 = (double)par4 + var32;
               } else if (var57 == 1) {
                  var39 = var28;
                  var41 = var26;
                  var43 = (double)(par2 + 1);
                  var47 = (double)par2;
                  var45 = (double)(par4 + 1) - var32;
                  var49 = (double)(par4 + 1) - var32;
               } else if (var57 == 2) {
                  var39 = var26;
                  var41 = var24;
                  var43 = (double)par2 + var32;
                  var47 = (double)par2 + var32;
                  var45 = (double)(par4 + 1);
                  var49 = (double)par4;
               } else {
                  var39 = var30;
                  var41 = var28;
                  var43 = (double)(par2 + 1) - var32;
                  var47 = (double)(par2 + 1) - var32;
                  var45 = (double)par4;
                  var49 = (double)(par4 + 1);
               }

               var13 = true;
               float var51 = var60.getInterpolatedU(0.0);
               var52 = var60.getInterpolatedU(8.0);
               var53 = var60.getInterpolatedV((1.0 - var39) * 16.0 * 0.5);
               var54 = var60.getInterpolatedV((1.0 - var41) * 16.0 * 0.5);
               float var55 = var60.getInterpolatedV(8.0);
               var5.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, var59, par3, var37));
               float var56 = 1.0F;
               if (var57 < 2) {
                  var56 *= var16;
               } else {
                  var56 *= var17;
               }

               var5.setColorOpaque_F(var15 * var56 * var7, var15 * var56 * var8, var15 * var56 * var9);
               if (RenderingScheme.current == 0) {
                  var5.addVertexWithUV(var43, (double)par3 + var39, var45, (double)var51, (double)var53);
                  var5.addVertexWithUV(var47, (double)par3 + var41, var49, (double)var52, (double)var54);
                  var5.addVertexWithUV(var47, (double)(par3 + 0), var49, (double)var52, (double)var55);
                  var5.addVertexWithUV(var43, (double)(par3 + 0), var45, (double)var51, (double)var55);
               } else {
                  this.x[0] = var43;
                  this.y[0] = (double)par3 + var39;
                  this.z[0] = var45;
                  this.u[0] = (double)var51;
                  this.v[0] = (double)var53;
                  this.x[1] = var47;
                  this.y[1] = (double)par3 + var41;
                  this.z[1] = var49;
                  this.u[1] = (double)var52;
                  this.v[1] = (double)var54;
                  this.x[2] = var47;
                  this.y[2] = (double)par3;
                  this.z[2] = var49;
                  this.u[2] = (double)var52;
                  this.v[2] = (double)var55;
                  this.x[3] = var43;
                  this.y[3] = (double)par3;
                  this.z[3] = var45;
                  this.u[3] = (double)var51;
                  this.v[3] = (double)var55;
                  var5.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
               }
            }
         }

         this.renderMinY = var18;
         this.renderMaxY = var20;
         return var13;
      }
   }

   private float getFluidHeight(int par1, int par2, int par3, Material par4Material) {
      int var5 = 0;
      float var6 = 0.0F;

      for(int var7 = 0; var7 < 4; ++var7) {
         int var8 = par1 - (var7 & 1);
         int var10 = par3 - (var7 >> 1 & 1);
         if (this.blockAccess.getBlockMaterial(var8, par2 + 1, var10) == par4Material) {
            return 1.0F;
         }

         Material var11 = this.blockAccess.getBlockMaterial(var8, par2, var10);
         if (var11 != par4Material) {
            if (!var11.isSolid()) {
               ++var6;
               ++var5;
            }
         } else {
            int var12 = this.blockAccess.getBlockMetadata(var8, par2, var10);
            if (var12 >= 8 || var12 == 0) {
               var6 += BlockFluid.getFluidHeightPercent(var12) * 10.0F;
               var5 += 10;
            }

            var6 += BlockFluid.getFluidHeightPercent(var12);
            ++var5;
         }
      }

      return 1.0F - var6 / (float)var5;
   }

   public void renderBlockSandFalling(Block par1Block, World par2World, int par3, int par4, int par5, int par6) {
      float var7 = 0.5F;
      float var8 = 1.0F;
      float var9 = 0.8F;
      float var10 = 0.6F;
      Tessellator var11 = Tessellator.instance;
      var11.startDrawingQuads();
      var11.setBrightness(par1Block.getMixedBrightnessForBlock(par2World, par3, par4, par5));
      float var12 = 1.0F;
      float var13 = 1.0F;
      if (var13 < var12) {
         var13 = var12;
      }

      var11.setColorOpaque_F(var7 * var13, var7 * var13, var7 * var13);
      this.renderFaceYNeg(par1Block, -0.5, -0.5, -0.5, this.getBlockIconFromSideAndMetadata(par1Block, 0, par6));
      var13 = 1.0F;
      if (var13 < var12) {
         var13 = var12;
      }

      var11.setColorOpaque_F(var8 * var13, var8 * var13, var8 * var13);
      this.renderFaceYPos(par1Block, -0.5, -0.5, -0.5, this.getBlockIconFromSideAndMetadata(par1Block, 1, par6));
      var13 = 1.0F;
      if (var13 < var12) {
         var13 = var12;
      }

      var11.setColorOpaque_F(var9 * var13, var9 * var13, var9 * var13);
      this.renderFaceZNeg(par1Block, -0.5, -0.5, -0.5, this.getBlockIconFromSideAndMetadata(par1Block, 2, par6));
      var13 = 1.0F;
      if (var13 < var12) {
         var13 = var12;
      }

      var11.setColorOpaque_F(var9 * var13, var9 * var13, var9 * var13);
      this.renderFaceZPos(par1Block, -0.5, -0.5, -0.5, this.getBlockIconFromSideAndMetadata(par1Block, 3, par6));
      var13 = 1.0F;
      if (var13 < var12) {
         var13 = var12;
      }

      var11.setColorOpaque_F(var10 * var13, var10 * var13, var10 * var13);
      this.renderFaceXNeg(par1Block, -0.5, -0.5, -0.5, this.getBlockIconFromSideAndMetadata(par1Block, 4, par6));
      var13 = 1.0F;
      if (var13 < var12) {
         var13 = var12;
      }

      var11.setColorOpaque_F(var10 * var13, var10 * var13, var10 * var13);
      this.renderFaceXPos(par1Block, -0.5, -0.5, -0.5, this.getBlockIconFromSideAndMetadata(par1Block, 5, par6));
      var11.draw();
   }

   public boolean renderStandardBlock(Block par1Block, int par2, int par3, int par4) {
      int var5 = par1Block.colorMultiplier(this.blockAccess, par2, par3, par4);
      float var6 = (float)(var5 >> 16 & 255) / 255.0F;
      float var7 = (float)(var5 >> 8 & 255) / 255.0F;
      float var8 = (float)(var5 & 255) / 255.0F;
      if (EntityRenderer.anaglyphEnable) {
         float var9 = (var6 * 30.0F + var7 * 59.0F + var8 * 11.0F) / 100.0F;
         float var10 = (var6 * 30.0F + var7 * 70.0F) / 100.0F;
         float var11 = (var6 * 30.0F + var8 * 70.0F) / 100.0F;
         var6 = var9;
         var7 = var10;
         var8 = var11;
      }

      return Minecraft.isAmbientOcclusionEnabled() && Block.lightValue[par1Block.blockID] == 0 ? (this.partialRenderBounds ? this.renderStandardBlockWithAmbientOcclusionPartial(par1Block, par2, par3, par4, var6, var7, var8) : this.renderStandardBlockWithAmbientOcclusion(par1Block, par2, par3, par4, var6, var7, var8)) : this.renderStandardBlockWithColorMultiplier(par1Block, par2, par3, par4, var6, var7, var8);
   }

   public boolean renderBlockLog(Block par1Block, int par2, int par3, int par4) {
      int var5 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      int var6 = var5 & 12;
      if (var6 == 4) {
         this.uvRotateEast = 1;
         this.uvRotateWest = 1;
         this.uvRotateTop = 1;
         this.uvRotateBottom = 1;
      } else if (var6 == 8) {
         this.uvRotateSouth = 1;
         this.uvRotateNorth = 1;
      }

      boolean var7 = this.renderStandardBlock(par1Block, par2, par3, par4);
      this.uvRotateSouth = 0;
      this.uvRotateEast = 0;
      this.uvRotateWest = 0;
      this.uvRotateNorth = 0;
      this.uvRotateTop = 0;
      this.uvRotateBottom = 0;
      return var7;
   }

   public boolean renderBlockQuartz(Block par1Block, int par2, int par3, int par4) {
      int var5 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      if (var5 == 3) {
         this.uvRotateEast = 1;
         this.uvRotateWest = 1;
         this.uvRotateTop = 1;
         this.uvRotateBottom = 1;
      } else if (var5 == 4) {
         this.uvRotateSouth = 1;
         this.uvRotateNorth = 1;
      }

      boolean var6 = this.renderStandardBlock(par1Block, par2, par3, par4);
      this.uvRotateSouth = 0;
      this.uvRotateEast = 0;
      this.uvRotateWest = 0;
      this.uvRotateNorth = 0;
      this.uvRotateTop = 0;
      this.uvRotateBottom = 0;
      return var6;
   }

   public boolean renderStandardBlockWithAmbientOcclusion(Block par1Block, int par2, int par3, int par4, float par5, float par6, float par7) {
      int x = par2;
      int y = par3;
      int z = par4;
      this.enableAO = true;
      boolean var8 = false;
      float var9 = 0.0F;
      float var10 = 0.0F;
      float var11 = 0.0F;
      float var12 = 0.0F;
      int var14 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);
      Tessellator var15 = Tessellator.instance;
      var15.setBrightness(983055);
      boolean var13 = !par1Block.has_grass_top_icon && this.overrideBlockTexture == null;
      boolean var17;
      boolean var16;
      boolean var19;
      boolean var18;
      float var21;
      int var20;
      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 - 1, par4, 0)) {
         if (this.renderMinY <= 0.0) {
            --par3;
         }

         this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
         this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
         this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
         this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
         this.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4);
         this.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 - 1);
         this.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 + 1);
         this.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4);
         var16 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 + 1, y - 1, par4)];
         var17 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 - 1, y - 1, par4)];
         var18 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, y - 1, par4 + 1)];
         var19 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, y - 1, par4 - 1)];
         if (!var19 && !var17) {
            this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXYNN;
            this.aoBrightnessXYZNNN = this.aoBrightnessXYNN;
         } else {
            this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4 - 1);
            this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4 - 1);
         }

         if (!var18 && !var17) {
            this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXYNN;
            this.aoBrightnessXYZNNP = this.aoBrightnessXYNN;
         } else {
            this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4 + 1);
            this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4 + 1);
         }

         if (!var19 && !var16) {
            this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXYPN;
            this.aoBrightnessXYZPNN = this.aoBrightnessXYPN;
         } else {
            this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4 - 1);
            this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4 - 1);
         }

         if (!var18 && !var16) {
            this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXYPN;
            this.aoBrightnessXYZPNP = this.aoBrightnessXYPN;
         } else {
            this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4 + 1);
            this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4 + 1);
         }

         if (this.renderMinY <= 0.0) {
            ++par3;
         }

         var20 = var14;
         if (this.renderMinY <= 0.0 || !this.blockAccess.isBlockStandardFormOpaqueCube(par2, par3 - 1, par4)) {
            var20 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
         }

         var21 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4);
         var9 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXYNN + this.aoLightValueScratchYZNP + var21) / 4.0F;
         var12 = (this.aoLightValueScratchYZNP + var21 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXYPN) / 4.0F;
         var11 = (var21 + this.aoLightValueScratchYZNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNN) / 4.0F;
         var10 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNN + var21 + this.aoLightValueScratchYZNN) / 4.0F;
         this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXYNN, this.aoBrightnessYZNP, var20);
         this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXYPN, var20);
         this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYPN, this.aoBrightnessXYZPNN, var20);
         this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNN, this.aoBrightnessYZNN, var20);
         if (var13) {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.5F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.5F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.5F;
         } else {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.5F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.5F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.5F;
         }

         this.colorRedTopLeft *= var9;
         this.colorGreenTopLeft *= var9;
         this.colorBlueTopLeft *= var9;
         this.colorRedBottomLeft *= var10;
         this.colorGreenBottomLeft *= var10;
         this.colorBlueBottomLeft *= var10;
         this.colorRedBottomRight *= var11;
         this.colorGreenBottomRight *= var11;
         this.colorBlueBottomRight *= var11;
         this.colorRedTopRight *= var12;
         this.colorGreenTopRight *= var12;
         this.colorBlueTopRight *= var12;
         this.renderFaceYNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 0));
         var8 = true;
      }

      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 + 1, par4, 1)) {
         if (this.renderMaxY >= 1.0) {
            ++par3;
         }

         this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
         this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
         this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
         this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
         this.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4);
         this.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4);
         this.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 - 1);
         this.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 + 1);
         var16 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 + 1, y + 1, par4)];
         var17 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 - 1, y + 1, par4)];
         var18 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, y + 1, par4 + 1)];
         var19 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, y + 1, par4 - 1)];
         if (!var19 && !var17) {
            this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXYNP;
            this.aoBrightnessXYZNPN = this.aoBrightnessXYNP;
         } else {
            this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4 - 1);
            this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4 - 1);
         }

         if (!var19 && !var16) {
            this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXYPP;
            this.aoBrightnessXYZPPN = this.aoBrightnessXYPP;
         } else {
            this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4 - 1);
            this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4 - 1);
         }

         if (!var18 && !var17) {
            this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXYNP;
            this.aoBrightnessXYZNPP = this.aoBrightnessXYNP;
         } else {
            this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4 + 1);
            this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4 + 1);
         }

         if (!var18 && !var16) {
            this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXYPP;
            this.aoBrightnessXYZPPP = this.aoBrightnessXYPP;
         } else {
            this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4 + 1);
            this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4 + 1);
         }

         if (this.renderMaxY >= 1.0) {
            --par3;
         }

         var20 = var14;
         if (this.renderMaxY >= 1.0 || !this.blockAccess.isBlockStandardFormOpaqueCube(par2, par3 + 1, par4)) {
            var20 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
         }

         var21 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4);
         var12 = (this.aoLightValueScratchXYZNPP + this.aoLightValueScratchXYNP + this.aoLightValueScratchYZPP + var21) / 4.0F;
         var9 = (this.aoLightValueScratchYZPP + var21 + this.aoLightValueScratchXYZPPP + this.aoLightValueScratchXYPP) / 4.0F;
         var10 = (var21 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPN) / 4.0F;
         var11 = (this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPN + var21 + this.aoLightValueScratchYZPN) / 4.0F;
         this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNPP, this.aoBrightnessXYNP, this.aoBrightnessYZPP, var20);
         this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXYZPPP, this.aoBrightnessXYPP, var20);
         this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXYPP, this.aoBrightnessXYZPPN, var20);
         this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYNP, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, var20);
         this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5;
         this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6;
         this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7;
         this.colorRedTopLeft *= var9;
         this.colorGreenTopLeft *= var9;
         this.colorBlueTopLeft *= var9;
         this.colorRedBottomLeft *= var10;
         this.colorGreenBottomLeft *= var10;
         this.colorBlueBottomLeft *= var10;
         this.colorRedBottomRight *= var11;
         this.colorGreenBottomRight *= var11;
         this.colorBlueBottomRight *= var11;
         this.colorRedTopRight *= var12;
         this.colorGreenTopRight *= var12;
         this.colorBlueTopRight *= var12;
         this.renderFaceYPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 1));
         var8 = true;
      }

      Icon var22;
      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 - 1, 2)) {
         if (this.renderMinZ <= 0.0) {
            --par4;
         }

         this.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4);
         this.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4);
         this.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4);
         this.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4);
         this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
         this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
         this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
         this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
         var16 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 + 1, par3, z - 1)];
         var17 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 - 1, par3, z - 1)];
         var18 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, par3 + 1, z - 1)];
         var19 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, par3 - 1, z - 1)];
         if (!var17 && !var19) {
            this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
            this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
         } else {
            this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3 - 1, par4);
            this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3 - 1, par4);
         }

         if (!var17 && !var18) {
            this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
            this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
         } else {
            this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3 + 1, par4);
            this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3 + 1, par4);
         }

         if (!var16 && !var19) {
            this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
            this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
         } else {
            this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3 - 1, par4);
            this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3 - 1, par4);
         }

         if (!var16 && !var18) {
            this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
            this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
         } else {
            this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3 + 1, par4);
            this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3 + 1, par4);
         }

         if (this.renderMinZ <= 0.0) {
            ++par4;
         }

         var20 = var14;
         if (this.renderMinZ <= 0.0 || !this.blockAccess.isBlockStandardFormOpaqueCube(par2, par3, par4 - 1)) {
            var20 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
         }

         var21 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 - 1);
         var9 = (this.aoLightValueScratchXZNN + this.aoLightValueScratchXYZNPN + var21 + this.aoLightValueScratchYZPN) / 4.0F;
         var10 = (var21 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXZPN + this.aoLightValueScratchXYZPPN) / 4.0F;
         var11 = (this.aoLightValueScratchYZNN + var21 + this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXZPN) / 4.0F;
         var12 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXZNN + this.aoLightValueScratchYZNN + var21) / 4.0F;
         this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, var20);
         this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, var20);
         this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYZPNN, this.aoBrightnessXZPN, var20);
         this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXZNN, this.aoBrightnessYZNN, var20);
         if (var13) {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.8F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.8F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.8F;
         } else {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
         }

         this.colorRedTopLeft *= var9;
         this.colorGreenTopLeft *= var9;
         this.colorBlueTopLeft *= var9;
         this.colorRedBottomLeft *= var10;
         this.colorGreenBottomLeft *= var10;
         this.colorBlueBottomLeft *= var10;
         this.colorRedBottomRight *= var11;
         this.colorGreenBottomRight *= var11;
         this.colorBlueBottomRight *= var11;
         this.colorRedTopRight *= var12;
         this.colorGreenTopRight *= var12;
         this.colorBlueTopRight *= var12;
         var22 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 2);
         this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, var22);
         if (var22.isGreenGrassSide() && this.overrideBlockTexture == null && fancyGrass) {
            this.colorRedTopLeft *= par5;
            this.colorRedBottomLeft *= par5;
            this.colorRedBottomRight *= par5;
            this.colorRedTopRight *= par5;
            this.colorGreenTopLeft *= par6;
            this.colorGreenBottomLeft *= par6;
            this.colorGreenBottomRight *= par6;
            this.colorGreenTopRight *= par6;
            this.colorBlueTopLeft *= par7;
            this.colorBlueBottomLeft *= par7;
            this.colorBlueBottomRight *= par7;
            this.colorBlueTopRight *= par7;
            this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
         }

         var8 = true;
      }

      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 + 1, 3)) {
         if (this.renderMaxZ >= 1.0) {
            ++par4;
         }

         this.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4);
         this.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4);
         this.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4);
         this.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4);
         this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
         this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
         this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
         this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
         var16 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 + 1, par3, z + 1)];
         var17 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 - 1, par3, z + 1)];
         var18 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, par3 + 1, z + 1)];
         var19 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, par3 - 1, z + 1)];
         if (!var17 && !var19) {
            this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
            this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
         } else {
            this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3 - 1, par4);
            this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3 - 1, par4);
         }

         if (!var17 && !var18) {
            this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
            this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
         } else {
            this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3 + 1, par4);
            this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3 + 1, par4);
         }

         if (!var16 && !var19) {
            this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
            this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
         } else {
            this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3 - 1, par4);
            this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3 - 1, par4);
         }

         if (!var16 && !var18) {
            this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
            this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
         } else {
            this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3 + 1, par4);
            this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3 + 1, par4);
         }

         if (this.renderMaxZ >= 1.0) {
            --par4;
         }

         var20 = var14;
         if (this.renderMaxZ >= 1.0 || !this.blockAccess.isBlockStandardFormOpaqueCube(par2, par3, par4 + 1)) {
            var20 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
         }

         var21 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 + 1);
         var9 = (this.aoLightValueScratchXZNP + this.aoLightValueScratchXYZNPP + var21 + this.aoLightValueScratchYZPP) / 4.0F;
         var12 = (var21 + this.aoLightValueScratchYZPP + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYZPPP) / 4.0F;
         var11 = (this.aoLightValueScratchYZNP + var21 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXZPP) / 4.0F;
         var10 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXZNP + this.aoLightValueScratchYZNP + var21) / 4.0F;
         this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYZNPP, this.aoBrightnessYZPP, var20);
         this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXZPP, this.aoBrightnessXYZPPP, var20);
         this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, var20);
         this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, this.aoBrightnessYZNP, var20);
         if (var13) {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.8F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.8F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.8F;
         } else {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
         }

         this.colorRedTopLeft *= var9;
         this.colorGreenTopLeft *= var9;
         this.colorBlueTopLeft *= var9;
         this.colorRedBottomLeft *= var10;
         this.colorGreenBottomLeft *= var10;
         this.colorBlueBottomLeft *= var10;
         this.colorRedBottomRight *= var11;
         this.colorGreenBottomRight *= var11;
         this.colorBlueBottomRight *= var11;
         this.colorRedTopRight *= var12;
         this.colorGreenTopRight *= var12;
         this.colorBlueTopRight *= var12;
         var22 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3);
         this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3));
         if (var22.isGreenGrassSide() && this.overrideBlockTexture == null && fancyGrass) {
            this.colorRedTopLeft *= par5;
            this.colorRedBottomLeft *= par5;
            this.colorRedBottomRight *= par5;
            this.colorRedTopRight *= par5;
            this.colorGreenTopLeft *= par6;
            this.colorGreenBottomLeft *= par6;
            this.colorGreenBottomRight *= par6;
            this.colorGreenTopRight *= par6;
            this.colorBlueTopLeft *= par7;
            this.colorBlueBottomLeft *= par7;
            this.colorBlueBottomRight *= par7;
            this.colorBlueTopRight *= par7;
            this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
         }

         var8 = true;
      }

      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 - 1, par3, par4, 4)) {
         if (this.renderMinX <= 0.0) {
            --par2;
         }

         this.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4);
         this.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 - 1);
         this.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 + 1);
         this.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4);
         this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
         this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
         this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
         this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
         var16 = Block.canHaveLightValue[this.blockAccess.getBlockId(x - 1, par3 + 1, par4)];
         var17 = Block.canHaveLightValue[this.blockAccess.getBlockId(x - 1, par3 - 1, par4)];
         var18 = Block.canHaveLightValue[this.blockAccess.getBlockId(x - 1, par3, par4 - 1)];
         var19 = Block.canHaveLightValue[this.blockAccess.getBlockId(x - 1, par3, par4 + 1)];
         if (!var18 && !var17) {
            this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
            this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
         } else {
            this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4 - 1);
            this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4 - 1);
         }

         if (!var19 && !var17) {
            this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
            this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
         } else {
            this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4 + 1);
            this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4 + 1);
         }

         if (!var18 && !var16) {
            this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
            this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
         } else {
            this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4 - 1);
            this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4 - 1);
         }

         if (!var19 && !var16) {
            this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
            this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
         } else {
            this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4 + 1);
            this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4 + 1);
         }

         if (this.renderMinX <= 0.0) {
            ++par2;
         }

         var20 = var14;
         if (this.renderMinX <= 0.0 || !this.blockAccess.isBlockStandardFormOpaqueCube(par2 - 1, par3, par4)) {
            var20 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
         }

         var21 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4);
         var12 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNP + var21 + this.aoLightValueScratchXZNP) / 4.0F;
         var9 = (var21 + this.aoLightValueScratchXZNP + this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPP) / 4.0F;
         var10 = (this.aoLightValueScratchXZNN + var21 + this.aoLightValueScratchXYZNPN + this.aoLightValueScratchXYNP) / 4.0F;
         var11 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXYNN + this.aoLightValueScratchXZNN + var21) / 4.0F;
         this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, var20);
         this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYNP, this.aoBrightnessXYZNPP, var20);
         this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessXYNP, var20);
         this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXYNN, this.aoBrightnessXZNN, var20);
         if (var13) {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.6F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.6F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.6F;
         } else {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
         }

         this.colorRedTopLeft *= var9;
         this.colorGreenTopLeft *= var9;
         this.colorBlueTopLeft *= var9;
         this.colorRedBottomLeft *= var10;
         this.colorGreenBottomLeft *= var10;
         this.colorBlueBottomLeft *= var10;
         this.colorRedBottomRight *= var11;
         this.colorGreenBottomRight *= var11;
         this.colorBlueBottomRight *= var11;
         this.colorRedTopRight *= var12;
         this.colorGreenTopRight *= var12;
         this.colorBlueTopRight *= var12;
         var22 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 4);
         this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, var22);
         if (var22.isGreenGrassSide() && this.overrideBlockTexture == null && fancyGrass) {
            this.colorRedTopLeft *= par5;
            this.colorRedBottomLeft *= par5;
            this.colorRedBottomRight *= par5;
            this.colorRedTopRight *= par5;
            this.colorGreenTopLeft *= par6;
            this.colorGreenBottomLeft *= par6;
            this.colorGreenBottomRight *= par6;
            this.colorGreenTopRight *= par6;
            this.colorBlueTopLeft *= par7;
            this.colorBlueBottomLeft *= par7;
            this.colorBlueBottomRight *= par7;
            this.colorBlueTopRight *= par7;
            this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
         }

         var8 = true;
      }

      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 + 1, par3, par4, 5)) {
         if (this.renderMaxX >= 1.0) {
            ++par2;
         }

         this.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4);
         this.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 - 1);
         this.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 + 1);
         this.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4);
         this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
         this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
         this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
         this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
         var16 = Block.canHaveLightValue[this.blockAccess.getBlockId(x + 1, par3 + 1, par4)];
         var17 = Block.canHaveLightValue[this.blockAccess.getBlockId(x + 1, par3 - 1, par4)];
         var18 = Block.canHaveLightValue[this.blockAccess.getBlockId(x + 1, par3, par4 + 1)];
         var19 = Block.canHaveLightValue[this.blockAccess.getBlockId(x + 1, par3, par4 - 1)];
         if (!var17 && !var19) {
            this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
            this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
         } else {
            this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4 - 1);
            this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4 - 1);
         }

         if (!var17 && !var18) {
            this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
            this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
         } else {
            this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4 + 1);
            this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4 + 1);
         }

         if (!var16 && !var19) {
            this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
            this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
         } else {
            this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4 - 1);
            this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4 - 1);
         }

         if (!var16 && !var18) {
            this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
            this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
         } else {
            this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4 + 1);
            this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4 + 1);
         }

         if (this.renderMaxX >= 1.0) {
            --par2;
         }

         var20 = var14;
         if (this.renderMaxX >= 1.0 || !this.blockAccess.isBlockStandardFormOpaqueCube(par2 + 1, par3, par4)) {
            var20 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
         }

         var21 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4);
         var9 = (this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNP + var21 + this.aoLightValueScratchXZPP) / 4.0F;
         var10 = (this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXZPN + var21) / 4.0F;
         var11 = (this.aoLightValueScratchXZPN + var21 + this.aoLightValueScratchXYZPPN + this.aoLightValueScratchXYPP) / 4.0F;
         var12 = (var21 + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPP) / 4.0F;
         this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYPN, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, var20);
         this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXZPP, this.aoBrightnessXYPP, this.aoBrightnessXYZPPP, var20);
         this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, this.aoBrightnessXYPP, var20);
         this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZPNN, this.aoBrightnessXYPN, this.aoBrightnessXZPN, var20);
         if (var13) {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.6F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.6F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.6F;
         } else {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
         }

         this.colorRedTopLeft *= var9;
         this.colorGreenTopLeft *= var9;
         this.colorBlueTopLeft *= var9;
         this.colorRedBottomLeft *= var10;
         this.colorGreenBottomLeft *= var10;
         this.colorBlueBottomLeft *= var10;
         this.colorRedBottomRight *= var11;
         this.colorGreenBottomRight *= var11;
         this.colorBlueBottomRight *= var11;
         this.colorRedTopRight *= var12;
         this.colorGreenTopRight *= var12;
         this.colorBlueTopRight *= var12;
         var22 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 5);
         this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, var22);
         if (var22.isGreenGrassSide() && this.overrideBlockTexture == null && fancyGrass) {
            this.colorRedTopLeft *= par5;
            this.colorRedBottomLeft *= par5;
            this.colorRedBottomRight *= par5;
            this.colorRedTopRight *= par5;
            this.colorGreenTopLeft *= par6;
            this.colorGreenBottomLeft *= par6;
            this.colorGreenBottomRight *= par6;
            this.colorGreenTopRight *= par6;
            this.colorBlueTopLeft *= par7;
            this.colorBlueBottomLeft *= par7;
            this.colorBlueBottomRight *= par7;
            this.colorBlueTopRight *= par7;
            this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
         }

         var8 = true;
      }

      this.enableAO = false;
      return var8;
   }

   public boolean renderStandardBlockWithAmbientOcclusionPartial(Block par1Block, int par2, int par3, int par4, float par5, float par6, float par7) {
      int x = par2;
      int y = par3;
      int z = par4;
      this.enableAO = true;
      boolean var8 = false;
      float var9 = 0.0F;
      float var10 = 0.0F;
      float var11 = 0.0F;
      float var12 = 0.0F;
      int var14 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);
      Tessellator var15 = Tessellator.instance;
      var15.setBrightness(983055);
      boolean var13 = !par1Block.has_grass_top_icon && this.overrideBlockTexture == null;
      boolean var17;
      boolean var16;
      boolean var19;
      boolean var18;
      float var21;
      int var20;
      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 - 1, par4, 0)) {
         if (this.renderMinY <= 0.0) {
            --par3;
         }

         this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
         this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
         this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
         this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
         this.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4);
         this.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 - 1);
         this.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 + 1);
         this.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4);
         var16 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 + 1, y - 1, par4)];
         var17 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 - 1, y - 1, par4)];
         var18 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, y - 1, par4 + 1)];
         var19 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, y - 1, par4 - 1)];
         if (!var19 && !var17) {
            this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXYNN;
            this.aoBrightnessXYZNNN = this.aoBrightnessXYNN;
         } else {
            this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4 - 1);
            this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4 - 1);
         }

         if (!var18 && !var17) {
            this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXYNN;
            this.aoBrightnessXYZNNP = this.aoBrightnessXYNN;
         } else {
            this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4 + 1);
            this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4 + 1);
         }

         if (!var19 && !var16) {
            this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXYPN;
            this.aoBrightnessXYZPNN = this.aoBrightnessXYPN;
         } else {
            this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4 - 1);
            this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4 - 1);
         }

         if (!var18 && !var16) {
            this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXYPN;
            this.aoBrightnessXYZPNP = this.aoBrightnessXYPN;
         } else {
            this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4 + 1);
            this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4 + 1);
         }

         if (this.renderMinY <= 0.0) {
            ++par3;
         }

         var20 = var14;
         if (this.renderMinY <= 0.0 || !this.blockAccess.isBlockStandardFormOpaqueCube(par2, par3 - 1, par4)) {
            var20 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
         }

         var21 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4);
         var9 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXYNN + this.aoLightValueScratchYZNP + var21) / 4.0F;
         var12 = (this.aoLightValueScratchYZNP + var21 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXYPN) / 4.0F;
         var11 = (var21 + this.aoLightValueScratchYZNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNN) / 4.0F;
         var10 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNN + var21 + this.aoLightValueScratchYZNN) / 4.0F;
         this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXYNN, this.aoBrightnessYZNP, var20);
         this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXYPN, var20);
         this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYPN, this.aoBrightnessXYZPNN, var20);
         this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNN, this.aoBrightnessYZNN, var20);
         if (var13) {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.5F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.5F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.5F;
         } else {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.5F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.5F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.5F;
         }

         this.colorRedTopLeft *= var9;
         this.colorGreenTopLeft *= var9;
         this.colorBlueTopLeft *= var9;
         this.colorRedBottomLeft *= var10;
         this.colorGreenBottomLeft *= var10;
         this.colorBlueBottomLeft *= var10;
         this.colorRedBottomRight *= var11;
         this.colorGreenBottomRight *= var11;
         this.colorBlueBottomRight *= var11;
         this.colorRedTopRight *= var12;
         this.colorGreenTopRight *= var12;
         this.colorBlueTopRight *= var12;
         this.renderFaceYNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 0));
         var8 = true;
      }

      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 + 1, par4, 1)) {
         if (this.renderMaxY >= 1.0) {
            ++par3;
         }

         this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
         this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
         this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
         this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
         this.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4);
         this.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4);
         this.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 - 1);
         this.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 + 1);
         var16 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 + 1, y + 1, par4)];
         var17 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 - 1, y + 1, par4)];
         var18 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, y + 1, par4 + 1)];
         var19 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, y + 1, par4 - 1)];
         if (!var19 && !var17) {
            this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXYNP;
            this.aoBrightnessXYZNPN = this.aoBrightnessXYNP;
         } else {
            this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4 - 1);
            this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4 - 1);
         }

         if (!var19 && !var16) {
            this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXYPP;
            this.aoBrightnessXYZPPN = this.aoBrightnessXYPP;
         } else {
            this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4 - 1);
            this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4 - 1);
         }

         if (!var18 && !var17) {
            this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXYNP;
            this.aoBrightnessXYZNPP = this.aoBrightnessXYNP;
         } else {
            this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4 + 1);
            this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4 + 1);
         }

         if (!var18 && !var16) {
            this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXYPP;
            this.aoBrightnessXYZPPP = this.aoBrightnessXYPP;
         } else {
            this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4 + 1);
            this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4 + 1);
         }

         if (this.renderMaxY >= 1.0) {
            --par3;
         }

         var20 = var14;
         if (this.renderMaxY >= 1.0 || !this.blockAccess.isBlockStandardFormOpaqueCube(par2, par3 + 1, par4)) {
            var20 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
         }

         var21 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4);
         var12 = (this.aoLightValueScratchXYZNPP + this.aoLightValueScratchXYNP + this.aoLightValueScratchYZPP + var21) / 4.0F;
         var9 = (this.aoLightValueScratchYZPP + var21 + this.aoLightValueScratchXYZPPP + this.aoLightValueScratchXYPP) / 4.0F;
         var10 = (var21 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPN) / 4.0F;
         var11 = (this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPN + var21 + this.aoLightValueScratchYZPN) / 4.0F;
         this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNPP, this.aoBrightnessXYNP, this.aoBrightnessYZPP, var20);
         this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXYZPPP, this.aoBrightnessXYPP, var20);
         this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXYPP, this.aoBrightnessXYZPPN, var20);
         this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYNP, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, var20);
         this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5;
         this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6;
         this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7;
         this.colorRedTopLeft *= var9;
         this.colorGreenTopLeft *= var9;
         this.colorBlueTopLeft *= var9;
         this.colorRedBottomLeft *= var10;
         this.colorGreenBottomLeft *= var10;
         this.colorBlueBottomLeft *= var10;
         this.colorRedBottomRight *= var11;
         this.colorGreenBottomRight *= var11;
         this.colorBlueBottomRight *= var11;
         this.colorRedTopRight *= var12;
         this.colorGreenTopRight *= var12;
         this.colorBlueTopRight *= var12;
         this.renderFaceYPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 1));
         var8 = true;
      }

      float var23;
      float var22;
      float var25;
      float var24;
      int var27;
      int var26;
      int var29;
      int var28;
      Icon var30;
      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 - 1, 2)) {
         if (this.renderMinZ <= 0.0) {
            --par4;
         }

         this.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4);
         this.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4);
         this.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4);
         this.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4);
         this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
         this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
         this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
         this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
         var16 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 + 1, par3, z - 1)];
         var17 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 - 1, par3, z - 1)];
         var18 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, par3 + 1, z - 1)];
         var19 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, par3 - 1, z - 1)];
         if (!var17 && !var19) {
            this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
            this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
         } else {
            this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3 - 1, par4);
            this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3 - 1, par4);
         }

         if (!var17 && !var18) {
            this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
            this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
         } else {
            this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3 + 1, par4);
            this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3 + 1, par4);
         }

         if (!var16 && !var19) {
            this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
            this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
         } else {
            this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3 - 1, par4);
            this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3 - 1, par4);
         }

         if (!var16 && !var18) {
            this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
            this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
         } else {
            this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3 + 1, par4);
            this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3 + 1, par4);
         }

         if (this.renderMinZ <= 0.0) {
            ++par4;
         }

         var20 = var14;
         if (this.renderMinZ <= 0.0 || !this.blockAccess.isBlockStandardFormOpaqueCube(par2, par3, par4 - 1)) {
            var20 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
         }

         var21 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 - 1);
         var22 = (this.aoLightValueScratchXZNN + this.aoLightValueScratchXYZNPN + var21 + this.aoLightValueScratchYZPN) / 4.0F;
         var23 = (var21 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXZPN + this.aoLightValueScratchXYZPPN) / 4.0F;
         var24 = (this.aoLightValueScratchYZNN + var21 + this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXZPN) / 4.0F;
         var25 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXZNN + this.aoLightValueScratchYZNN + var21) / 4.0F;
         var9 = (float)((double)var22 * this.renderMaxY * (1.0 - this.renderMinX) + (double)var23 * this.renderMaxY * this.renderMinX + (double)var24 * (1.0 - this.renderMaxY) * this.renderMinX + (double)var25 * (1.0 - this.renderMaxY) * (1.0 - this.renderMinX));
         var10 = (float)((double)var22 * this.renderMaxY * (1.0 - this.renderMaxX) + (double)var23 * this.renderMaxY * this.renderMaxX + (double)var24 * (1.0 - this.renderMaxY) * this.renderMaxX + (double)var25 * (1.0 - this.renderMaxY) * (1.0 - this.renderMaxX));
         var11 = (float)((double)var22 * this.renderMinY * (1.0 - this.renderMaxX) + (double)var23 * this.renderMinY * this.renderMaxX + (double)var24 * (1.0 - this.renderMinY) * this.renderMaxX + (double)var25 * (1.0 - this.renderMinY) * (1.0 - this.renderMaxX));
         var12 = (float)((double)var22 * this.renderMinY * (1.0 - this.renderMinX) + (double)var23 * this.renderMinY * this.renderMinX + (double)var24 * (1.0 - this.renderMinY) * this.renderMinX + (double)var25 * (1.0 - this.renderMinY) * (1.0 - this.renderMinX));
         var26 = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, var20);
         var27 = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, var20);
         var28 = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYZPNN, this.aoBrightnessXZPN, var20);
         var29 = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXZNN, this.aoBrightnessYZNN, var20);
         this.brightnessTopLeft = this.mixAoBrightness(var26, var27, var28, var29, this.renderMaxY * (1.0 - this.renderMinX), this.renderMaxY * this.renderMinX, (1.0 - this.renderMaxY) * this.renderMinX, (1.0 - this.renderMaxY) * (1.0 - this.renderMinX));
         this.brightnessBottomLeft = this.mixAoBrightness(var26, var27, var28, var29, this.renderMaxY * (1.0 - this.renderMaxX), this.renderMaxY * this.renderMaxX, (1.0 - this.renderMaxY) * this.renderMaxX, (1.0 - this.renderMaxY) * (1.0 - this.renderMaxX));
         this.brightnessBottomRight = this.mixAoBrightness(var26, var27, var28, var29, this.renderMinY * (1.0 - this.renderMaxX), this.renderMinY * this.renderMaxX, (1.0 - this.renderMinY) * this.renderMaxX, (1.0 - this.renderMinY) * (1.0 - this.renderMaxX));
         this.brightnessTopRight = this.mixAoBrightness(var26, var27, var28, var29, this.renderMinY * (1.0 - this.renderMinX), this.renderMinY * this.renderMinX, (1.0 - this.renderMinY) * this.renderMinX, (1.0 - this.renderMinY) * (1.0 - this.renderMinX));
         if (var13) {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.8F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.8F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.8F;
         } else {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
         }

         this.colorRedTopLeft *= var9;
         this.colorGreenTopLeft *= var9;
         this.colorBlueTopLeft *= var9;
         this.colorRedBottomLeft *= var10;
         this.colorGreenBottomLeft *= var10;
         this.colorBlueBottomLeft *= var10;
         this.colorRedBottomRight *= var11;
         this.colorGreenBottomRight *= var11;
         this.colorBlueBottomRight *= var11;
         this.colorRedTopRight *= var12;
         this.colorGreenTopRight *= var12;
         this.colorBlueTopRight *= var12;
         var30 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 2);
         this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, var30);
         if (var30.isGreenGrassSide() && this.overrideBlockTexture == null && fancyGrass) {
            this.colorRedTopLeft *= par5;
            this.colorRedBottomLeft *= par5;
            this.colorRedBottomRight *= par5;
            this.colorRedTopRight *= par5;
            this.colorGreenTopLeft *= par6;
            this.colorGreenBottomLeft *= par6;
            this.colorGreenBottomRight *= par6;
            this.colorGreenTopRight *= par6;
            this.colorBlueTopLeft *= par7;
            this.colorBlueBottomLeft *= par7;
            this.colorBlueBottomRight *= par7;
            this.colorBlueTopRight *= par7;
            this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
         }

         var8 = true;
      }

      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 + 1, 3)) {
         if (this.renderMaxZ >= 1.0) {
            ++par4;
         }

         this.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4);
         this.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4);
         this.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4);
         this.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4);
         this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
         this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
         this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
         this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
         var16 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 + 1, par3, z + 1)];
         var17 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2 - 1, par3, z + 1)];
         var18 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, par3 + 1, z + 1)];
         var19 = Block.canHaveLightValue[this.blockAccess.getBlockId(par2, par3 - 1, z + 1)];
         if (!var17 && !var19) {
            this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
            this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
         } else {
            this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3 - 1, par4);
            this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3 - 1, par4);
         }

         if (!var17 && !var18) {
            this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
            this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
         } else {
            this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3 + 1, par4);
            this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3 + 1, par4);
         }

         if (!var16 && !var19) {
            this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
            this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
         } else {
            this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3 - 1, par4);
            this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3 - 1, par4);
         }

         if (!var16 && !var18) {
            this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
            this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
         } else {
            this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3 + 1, par4);
            this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3 + 1, par4);
         }

         if (this.renderMaxZ >= 1.0) {
            --par4;
         }

         var20 = var14;
         if (this.renderMaxZ >= 1.0 || !this.blockAccess.isBlockStandardFormOpaqueCube(par2, par3, par4 + 1)) {
            var20 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
         }

         var21 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 + 1);
         var22 = (this.aoLightValueScratchXZNP + this.aoLightValueScratchXYZNPP + var21 + this.aoLightValueScratchYZPP) / 4.0F;
         var23 = (var21 + this.aoLightValueScratchYZPP + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYZPPP) / 4.0F;
         var24 = (this.aoLightValueScratchYZNP + var21 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXZPP) / 4.0F;
         var25 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXZNP + this.aoLightValueScratchYZNP + var21) / 4.0F;
         var9 = (float)((double)var22 * this.renderMaxY * (1.0 - this.renderMinX) + (double)var23 * this.renderMaxY * this.renderMinX + (double)var24 * (1.0 - this.renderMaxY) * this.renderMinX + (double)var25 * (1.0 - this.renderMaxY) * (1.0 - this.renderMinX));
         var10 = (float)((double)var22 * this.renderMinY * (1.0 - this.renderMinX) + (double)var23 * this.renderMinY * this.renderMinX + (double)var24 * (1.0 - this.renderMinY) * this.renderMinX + (double)var25 * (1.0 - this.renderMinY) * (1.0 - this.renderMinX));
         var11 = (float)((double)var22 * this.renderMinY * (1.0 - this.renderMaxX) + (double)var23 * this.renderMinY * this.renderMaxX + (double)var24 * (1.0 - this.renderMinY) * this.renderMaxX + (double)var25 * (1.0 - this.renderMinY) * (1.0 - this.renderMaxX));
         var12 = (float)((double)var22 * this.renderMaxY * (1.0 - this.renderMaxX) + (double)var23 * this.renderMaxY * this.renderMaxX + (double)var24 * (1.0 - this.renderMaxY) * this.renderMaxX + (double)var25 * (1.0 - this.renderMaxY) * (1.0 - this.renderMaxX));
         var26 = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYZNPP, this.aoBrightnessYZPP, var20);
         var27 = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXZPP, this.aoBrightnessXYZPPP, var20);
         var28 = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, var20);
         var29 = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, this.aoBrightnessYZNP, var20);
         this.brightnessTopLeft = this.mixAoBrightness(var26, var29, var28, var27, this.renderMaxY * (1.0 - this.renderMinX), (1.0 - this.renderMaxY) * (1.0 - this.renderMinX), (1.0 - this.renderMaxY) * this.renderMinX, this.renderMaxY * this.renderMinX);
         this.brightnessBottomLeft = this.mixAoBrightness(var26, var29, var28, var27, this.renderMinY * (1.0 - this.renderMinX), (1.0 - this.renderMinY) * (1.0 - this.renderMinX), (1.0 - this.renderMinY) * this.renderMinX, this.renderMinY * this.renderMinX);
         this.brightnessBottomRight = this.mixAoBrightness(var26, var29, var28, var27, this.renderMinY * (1.0 - this.renderMaxX), (1.0 - this.renderMinY) * (1.0 - this.renderMaxX), (1.0 - this.renderMinY) * this.renderMaxX, this.renderMinY * this.renderMaxX);
         this.brightnessTopRight = this.mixAoBrightness(var26, var29, var28, var27, this.renderMaxY * (1.0 - this.renderMaxX), (1.0 - this.renderMaxY) * (1.0 - this.renderMaxX), (1.0 - this.renderMaxY) * this.renderMaxX, this.renderMaxY * this.renderMaxX);
         if (var13) {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.8F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.8F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.8F;
         } else {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
         }

         this.colorRedTopLeft *= var9;
         this.colorGreenTopLeft *= var9;
         this.colorBlueTopLeft *= var9;
         this.colorRedBottomLeft *= var10;
         this.colorGreenBottomLeft *= var10;
         this.colorBlueBottomLeft *= var10;
         this.colorRedBottomRight *= var11;
         this.colorGreenBottomRight *= var11;
         this.colorBlueBottomRight *= var11;
         this.colorRedTopRight *= var12;
         this.colorGreenTopRight *= var12;
         this.colorBlueTopRight *= var12;
         var30 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3);
         this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3));
         if (var30.isGreenGrassSide() && this.overrideBlockTexture == null && fancyGrass) {
            this.colorRedTopLeft *= par5;
            this.colorRedBottomLeft *= par5;
            this.colorRedBottomRight *= par5;
            this.colorRedTopRight *= par5;
            this.colorGreenTopLeft *= par6;
            this.colorGreenBottomLeft *= par6;
            this.colorGreenBottomRight *= par6;
            this.colorGreenTopRight *= par6;
            this.colorBlueTopLeft *= par7;
            this.colorBlueBottomLeft *= par7;
            this.colorBlueBottomRight *= par7;
            this.colorBlueTopRight *= par7;
            this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
         }

         var8 = true;
      }

      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 - 1, par3, par4, 4)) {
         if (this.renderMinX <= 0.0) {
            --par2;
         }

         this.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4);
         this.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 - 1);
         this.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 + 1);
         this.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4);
         this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
         this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
         this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
         this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
         var16 = Block.canHaveLightValue[this.blockAccess.getBlockId(x - 1, par3 + 1, par4)];
         var17 = Block.canHaveLightValue[this.blockAccess.getBlockId(x - 1, par3 - 1, par4)];
         var18 = Block.canHaveLightValue[this.blockAccess.getBlockId(x - 1, par3, par4 - 1)];
         var19 = Block.canHaveLightValue[this.blockAccess.getBlockId(x - 1, par3, par4 + 1)];
         if (!var18 && !var17) {
            this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
            this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
         } else {
            this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4 - 1);
            this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4 - 1);
         }

         if (!var19 && !var17) {
            this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
            this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
         } else {
            this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4 + 1);
            this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4 + 1);
         }

         if (!var18 && !var16) {
            this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
            this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
         } else {
            this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4 - 1);
            this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4 - 1);
         }

         if (!var19 && !var16) {
            this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
            this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
         } else {
            this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4 + 1);
            this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4 + 1);
         }

         if (this.renderMinX <= 0.0) {
            ++par2;
         }

         var20 = var14;
         if (this.renderMinX <= 0.0 || !this.blockAccess.isBlockStandardFormOpaqueCube(par2 - 1, par3, par4)) {
            var20 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
         }

         var21 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 - 1, par3, par4);
         var22 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNP + var21 + this.aoLightValueScratchXZNP) / 4.0F;
         var23 = (var21 + this.aoLightValueScratchXZNP + this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPP) / 4.0F;
         var24 = (this.aoLightValueScratchXZNN + var21 + this.aoLightValueScratchXYZNPN + this.aoLightValueScratchXYNP) / 4.0F;
         var25 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXYNN + this.aoLightValueScratchXZNN + var21) / 4.0F;
         var9 = (float)((double)var23 * this.renderMaxY * this.renderMaxZ + (double)var24 * this.renderMaxY * (1.0 - this.renderMaxZ) + (double)var25 * (1.0 - this.renderMaxY) * (1.0 - this.renderMaxZ) + (double)var22 * (1.0 - this.renderMaxY) * this.renderMaxZ);
         var10 = (float)((double)var23 * this.renderMaxY * this.renderMinZ + (double)var24 * this.renderMaxY * (1.0 - this.renderMinZ) + (double)var25 * (1.0 - this.renderMaxY) * (1.0 - this.renderMinZ) + (double)var22 * (1.0 - this.renderMaxY) * this.renderMinZ);
         var11 = (float)((double)var23 * this.renderMinY * this.renderMinZ + (double)var24 * this.renderMinY * (1.0 - this.renderMinZ) + (double)var25 * (1.0 - this.renderMinY) * (1.0 - this.renderMinZ) + (double)var22 * (1.0 - this.renderMinY) * this.renderMinZ);
         var12 = (float)((double)var23 * this.renderMinY * this.renderMaxZ + (double)var24 * this.renderMinY * (1.0 - this.renderMaxZ) + (double)var25 * (1.0 - this.renderMinY) * (1.0 - this.renderMaxZ) + (double)var22 * (1.0 - this.renderMinY) * this.renderMaxZ);
         var26 = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, var20);
         var27 = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYNP, this.aoBrightnessXYZNPP, var20);
         var28 = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessXYNP, var20);
         var29 = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXYNN, this.aoBrightnessXZNN, var20);
         this.brightnessTopLeft = this.mixAoBrightness(var27, var28, var29, var26, this.renderMaxY * this.renderMaxZ, this.renderMaxY * (1.0 - this.renderMaxZ), (1.0 - this.renderMaxY) * (1.0 - this.renderMaxZ), (1.0 - this.renderMaxY) * this.renderMaxZ);
         this.brightnessBottomLeft = this.mixAoBrightness(var27, var28, var29, var26, this.renderMaxY * this.renderMinZ, this.renderMaxY * (1.0 - this.renderMinZ), (1.0 - this.renderMaxY) * (1.0 - this.renderMinZ), (1.0 - this.renderMaxY) * this.renderMinZ);
         this.brightnessBottomRight = this.mixAoBrightness(var27, var28, var29, var26, this.renderMinY * this.renderMinZ, this.renderMinY * (1.0 - this.renderMinZ), (1.0 - this.renderMinY) * (1.0 - this.renderMinZ), (1.0 - this.renderMinY) * this.renderMinZ);
         this.brightnessTopRight = this.mixAoBrightness(var27, var28, var29, var26, this.renderMinY * this.renderMaxZ, this.renderMinY * (1.0 - this.renderMaxZ), (1.0 - this.renderMinY) * (1.0 - this.renderMaxZ), (1.0 - this.renderMinY) * this.renderMaxZ);
         if (var13) {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.6F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.6F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.6F;
         } else {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
         }

         this.colorRedTopLeft *= var9;
         this.colorGreenTopLeft *= var9;
         this.colorBlueTopLeft *= var9;
         this.colorRedBottomLeft *= var10;
         this.colorGreenBottomLeft *= var10;
         this.colorBlueBottomLeft *= var10;
         this.colorRedBottomRight *= var11;
         this.colorGreenBottomRight *= var11;
         this.colorBlueBottomRight *= var11;
         this.colorRedTopRight *= var12;
         this.colorGreenTopRight *= var12;
         this.colorBlueTopRight *= var12;
         var30 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 4);
         this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, var30);
         if (var30.isGreenGrassSide() && this.overrideBlockTexture == null && fancyGrass) {
            this.colorRedTopLeft *= par5;
            this.colorRedBottomLeft *= par5;
            this.colorRedBottomRight *= par5;
            this.colorRedTopRight *= par5;
            this.colorGreenTopLeft *= par6;
            this.colorGreenBottomLeft *= par6;
            this.colorGreenBottomRight *= par6;
            this.colorGreenTopRight *= par6;
            this.colorBlueTopLeft *= par7;
            this.colorBlueBottomLeft *= par7;
            this.colorBlueBottomRight *= par7;
            this.colorBlueTopRight *= par7;
            this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
         }

         var8 = true;
      }

      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 + 1, par3, par4, 5)) {
         if (this.renderMaxX >= 1.0) {
            ++par2;
         }

         this.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4);
         this.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 - 1);
         this.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3, par4 + 1);
         this.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4);
         this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
         this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
         this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
         this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
         var16 = Block.canHaveLightValue[this.blockAccess.getBlockId(x + 1, par3 + 1, par4)];
         var17 = Block.canHaveLightValue[this.blockAccess.getBlockId(x + 1, par3 - 1, par4)];
         var18 = Block.canHaveLightValue[this.blockAccess.getBlockId(x + 1, par3, par4 + 1)];
         var19 = Block.canHaveLightValue[this.blockAccess.getBlockId(x + 1, par3, par4 - 1)];
         if (!var17 && !var19) {
            this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
            this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
         } else {
            this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4 - 1);
            this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4 - 1);
         }

         if (!var17 && !var18) {
            this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
            this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
         } else {
            this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 - 1, par4 + 1);
            this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4 + 1);
         }

         if (!var16 && !var19) {
            this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
            this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
         } else {
            this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4 - 1);
            this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4 - 1);
         }

         if (!var16 && !var18) {
            this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
            this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
         } else {
            this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2, par3 + 1, par4 + 1);
            this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4 + 1);
         }

         if (this.renderMaxX >= 1.0) {
            --par2;
         }

         var20 = var14;
         if (this.renderMaxX >= 1.0 || !this.blockAccess.isBlockStandardFormOpaqueCube(par2 + 1, par3, par4)) {
            var20 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
         }

         var21 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par2 + 1, par3, par4);
         var22 = (this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNP + var21 + this.aoLightValueScratchXZPP) / 4.0F;
         var23 = (this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXZPN + var21) / 4.0F;
         var24 = (this.aoLightValueScratchXZPN + var21 + this.aoLightValueScratchXYZPPN + this.aoLightValueScratchXYPP) / 4.0F;
         var25 = (var21 + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPP) / 4.0F;
         var9 = (float)((double)var22 * (1.0 - this.renderMinY) * this.renderMaxZ + (double)var23 * (1.0 - this.renderMinY) * (1.0 - this.renderMaxZ) + (double)var24 * this.renderMinY * (1.0 - this.renderMaxZ) + (double)var25 * this.renderMinY * this.renderMaxZ);
         var10 = (float)((double)var22 * (1.0 - this.renderMinY) * this.renderMinZ + (double)var23 * (1.0 - this.renderMinY) * (1.0 - this.renderMinZ) + (double)var24 * this.renderMinY * (1.0 - this.renderMinZ) + (double)var25 * this.renderMinY * this.renderMinZ);
         var11 = (float)((double)var22 * (1.0 - this.renderMaxY) * this.renderMinZ + (double)var23 * (1.0 - this.renderMaxY) * (1.0 - this.renderMinZ) + (double)var24 * this.renderMaxY * (1.0 - this.renderMinZ) + (double)var25 * this.renderMaxY * this.renderMinZ);
         var12 = (float)((double)var22 * (1.0 - this.renderMaxY) * this.renderMaxZ + (double)var23 * (1.0 - this.renderMaxY) * (1.0 - this.renderMaxZ) + (double)var24 * this.renderMaxY * (1.0 - this.renderMaxZ) + (double)var25 * this.renderMaxY * this.renderMaxZ);
         var26 = this.getAoBrightness(this.aoBrightnessXYPN, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, var20);
         var27 = this.getAoBrightness(this.aoBrightnessXZPP, this.aoBrightnessXYPP, this.aoBrightnessXYZPPP, var20);
         var28 = this.getAoBrightness(this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, this.aoBrightnessXYPP, var20);
         var29 = this.getAoBrightness(this.aoBrightnessXYZPNN, this.aoBrightnessXYPN, this.aoBrightnessXZPN, var20);
         this.brightnessTopLeft = this.mixAoBrightness(var26, var29, var28, var27, (1.0 - this.renderMinY) * this.renderMaxZ, (1.0 - this.renderMinY) * (1.0 - this.renderMaxZ), this.renderMinY * (1.0 - this.renderMaxZ), this.renderMinY * this.renderMaxZ);
         this.brightnessBottomLeft = this.mixAoBrightness(var26, var29, var28, var27, (1.0 - this.renderMinY) * this.renderMinZ, (1.0 - this.renderMinY) * (1.0 - this.renderMinZ), this.renderMinY * (1.0 - this.renderMinZ), this.renderMinY * this.renderMinZ);
         this.brightnessBottomRight = this.mixAoBrightness(var26, var29, var28, var27, (1.0 - this.renderMaxY) * this.renderMinZ, (1.0 - this.renderMaxY) * (1.0 - this.renderMinZ), this.renderMaxY * (1.0 - this.renderMinZ), this.renderMaxY * this.renderMinZ);
         this.brightnessTopRight = this.mixAoBrightness(var26, var29, var28, var27, (1.0 - this.renderMaxY) * this.renderMaxZ, (1.0 - this.renderMaxY) * (1.0 - this.renderMaxZ), this.renderMaxY * (1.0 - this.renderMaxZ), this.renderMaxY * this.renderMaxZ);
         if (var13) {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.6F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.6F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.6F;
         } else {
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
         }

         this.colorRedTopLeft *= var9;
         this.colorGreenTopLeft *= var9;
         this.colorBlueTopLeft *= var9;
         this.colorRedBottomLeft *= var10;
         this.colorGreenBottomLeft *= var10;
         this.colorBlueBottomLeft *= var10;
         this.colorRedBottomRight *= var11;
         this.colorGreenBottomRight *= var11;
         this.colorBlueBottomRight *= var11;
         this.colorRedTopRight *= var12;
         this.colorGreenTopRight *= var12;
         this.colorBlueTopRight *= var12;
         var30 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 5);
         this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, var30);
         if (var30.isGreenGrassSide() && this.overrideBlockTexture == null && fancyGrass) {
            this.colorRedTopLeft *= par5;
            this.colorRedBottomLeft *= par5;
            this.colorRedBottomRight *= par5;
            this.colorRedTopRight *= par5;
            this.colorGreenTopLeft *= par6;
            this.colorGreenBottomLeft *= par6;
            this.colorGreenBottomRight *= par6;
            this.colorGreenTopRight *= par6;
            this.colorBlueTopLeft *= par7;
            this.colorBlueBottomLeft *= par7;
            this.colorBlueBottomRight *= par7;
            this.colorBlueTopRight *= par7;
            this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
         }

         var8 = true;
      }

      this.enableAO = false;
      return var8;
   }

   private int getBrightnessOfSkylight(int mixed_brightness) {
      return (mixed_brightness & 255) / 16;
   }

   private int getBrightnessOfBlocklight(int mixed_brightness) {
      return mixed_brightness >> 20;
   }

   private int getAoBrightness(int par1, int par2, int par3, int par4) {
      if (par1 == 0) {
         par1 = par4;
      }

      if (par2 == 0) {
         par2 = par4;
      }

      if (par3 == 0) {
         par3 = par4;
      }

      return par1 + par2 + par3 + par4 >> 2 & 16711935;
   }

   private int mixAoBrightness(int par1, int par2, int par3, int par4, double par5, double par7, double par9, double par11) {
      int var13 = (int)((double)(par1 >> 16 & 255) * par5 + (double)(par2 >> 16 & 255) * par7 + (double)(par3 >> 16 & 255) * par9 + (double)(par4 >> 16 & 255) * par11) & 255;
      int var14 = (int)((double)(par1 & 255) * par5 + (double)(par2 & 255) * par7 + (double)(par3 & 255) * par9 + (double)(par4 & 255) * par11) & 255;
      return var13 << 16 | var14;
   }

   public boolean renderStandardBlockWithColorMultiplier(Block par1Block, int par2, int par3, int par4, float par5, float par6, float par7) {
      this.enableAO = false;
      Tessellator var8 = Tessellator.instance;
      boolean var9 = false;
      float var10 = 0.5F;
      float var11 = 1.0F;
      float var12 = 0.8F;
      float var13 = 0.6F;
      float var14 = var11 * par5;
      float var15 = var11 * par6;
      float var16 = var11 * par7;
      float var17 = var10;
      float var18 = var12;
      float var19 = var13;
      float var20 = var10;
      float var21 = var12;
      float var22 = var13;
      float var23 = var10;
      float var24 = var12;
      float var25 = var13;
      if (par1Block != Block.grass) {
         var17 = var10 * par5;
         var18 = var12 * par5;
         var19 = var13 * par5;
         var20 = var10 * par6;
         var21 = var12 * par6;
         var22 = var13 * par6;
         var23 = var10 * par7;
         var24 = var12 * par7;
         var25 = var13 * par7;
      }

      int var26 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);
      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 - 1, par4, 0)) {
         var8.setBrightness(this.renderMinY > 0.0 ? var26 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4));
         var8.setColorOpaque_F(var17, var20, var23);
         this.renderFaceYNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 0));
         var9 = true;
      }

      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 + 1, par4, 1)) {
         var8.setBrightness(this.renderMaxY < 1.0 ? var26 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4));
         var8.setColorOpaque_F(var14, var15, var16);
         this.renderFaceYPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 1));
         var9 = true;
      }

      Icon var28;
      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 - 1, 2)) {
         var8.setBrightness(this.renderMinZ > 0.0 ? var26 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1));
         var8.setColorOpaque_F(var18, var21, var24);
         var28 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 2);
         this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, var28);
         if (var28.isGreenGrassSide() && this.overrideBlockTexture == null && fancyGrass) {
            var8.setColorOpaque_F(var18 * par5, var21 * par6, var24 * par7);
            this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
         }

         var9 = true;
      }

      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 + 1, 3)) {
         var8.setBrightness(this.renderMaxZ < 1.0 ? var26 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1));
         var8.setColorOpaque_F(var18, var21, var24);
         var28 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3);
         this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, var28);
         if (var28.isGreenGrassSide() && this.overrideBlockTexture == null && fancyGrass) {
            var8.setColorOpaque_F(var18 * par5, var21 * par6, var24 * par7);
            this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
         }

         var9 = true;
      }

      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 - 1, par3, par4, 4)) {
         var8.setBrightness(this.renderMinX > 0.0 ? var26 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4));
         var8.setColorOpaque_F(var19, var22, var25);
         var28 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 4);
         this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, var28);
         if (var28.isGreenGrassSide() && this.overrideBlockTexture == null && fancyGrass) {
            var8.setColorOpaque_F(var19 * par5, var22 * par6, var25 * par7);
            this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
         }

         var9 = true;
      }

      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 + 1, par3, par4, 5)) {
         var8.setBrightness(this.renderMaxX < 1.0 ? var26 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4));
         var8.setColorOpaque_F(var19, var22, var25);
         var28 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 5);
         this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, var28);
         if (var28.isGreenGrassSide() && this.overrideBlockTexture == null && fancyGrass) {
            var8.setColorOpaque_F(var19 * par5, var22 * par6, var25 * par7);
            this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
         }

         var9 = true;
      }

      return var9;
   }

   private boolean renderBlockCocoa(BlockCocoa par1BlockCocoa, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      var5.setBrightness(par1BlockCocoa.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      var5.setColorOpaque_F(1.0F, 1.0F, 1.0F);
      int var6 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      int var7 = BlockCocoa.getDirection(var6);
      int var8 = BlockCocoa.func_72219_c(var6);
      Icon var9 = par1BlockCocoa.getCocoaIcon(var8);
      int var10 = 4 + var8 * 2;
      int var11 = 5 + var8 * 2;
      double var12 = 15.0 - (double)var10;
      double var14 = 15.0;
      double var16 = 4.0;
      double var18 = 4.0 + (double)var11;
      double var20 = (double)var9.getInterpolatedU(var12);
      double var22 = (double)var9.getInterpolatedU(var14);
      double var24 = (double)var9.getInterpolatedV(var16);
      double var26 = (double)var9.getInterpolatedV(var18);
      double var28 = 0.0;
      double var30 = 0.0;
      switch (var7) {
         case 0:
            var28 = 8.0 - (double)(var10 / 2);
            var30 = 15.0 - (double)var10;
            break;
         case 1:
            var28 = 1.0;
            var30 = 8.0 - (double)(var10 / 2);
            break;
         case 2:
            var28 = 8.0 - (double)(var10 / 2);
            var30 = 1.0;
            break;
         case 3:
            var28 = 15.0 - (double)var10;
            var30 = 8.0 - (double)(var10 / 2);
      }

      double var32 = (double)par2 + var28 / 16.0;
      double var34 = (double)par2 + (var28 + (double)var10) / 16.0;
      double var36 = (double)par3 + (12.0 - (double)var11) / 16.0;
      double var38 = (double)par3 + 0.75;
      double var40 = (double)par4 + var30 / 16.0;
      double var42 = (double)par4 + (var30 + (double)var10) / 16.0;
      var5.addVertexWithUV(var32, var36, var40, var20, var26);
      var5.addVertexWithUV(var32, var36, var42, var22, var26);
      var5.addVertexWithUV(var32, var38, var42, var22, var24);
      var5.addVertexWithUV(var32, var38, var40, var20, var24);
      var5.addVertexWithUV(var34, var36, var42, var20, var26);
      var5.addVertexWithUV(var34, var36, var40, var22, var26);
      var5.addVertexWithUV(var34, var38, var40, var22, var24);
      var5.addVertexWithUV(var34, var38, var42, var20, var24);
      var5.addVertexWithUV(var34, var36, var40, var20, var26);
      var5.addVertexWithUV(var32, var36, var40, var22, var26);
      var5.addVertexWithUV(var32, var38, var40, var22, var24);
      var5.addVertexWithUV(var34, var38, var40, var20, var24);
      var5.addVertexWithUV(var32, var36, var42, var20, var26);
      var5.addVertexWithUV(var34, var36, var42, var22, var26);
      var5.addVertexWithUV(var34, var38, var42, var22, var24);
      var5.addVertexWithUV(var32, var38, var42, var20, var24);
      int var44 = var10;
      if (var8 >= 2) {
         var44 = var10 - 1;
      }

      var20 = (double)var9.getMinU();
      var22 = (double)var9.getInterpolatedU((double)var44);
      var24 = (double)var9.getMinV();
      var26 = (double)var9.getInterpolatedV((double)var44);
      var5.addVertexWithUV(var32, var38, var42, var20, var26);
      var5.addVertexWithUV(var34, var38, var42, var22, var26);
      var5.addVertexWithUV(var34, var38, var40, var22, var24);
      var5.addVertexWithUV(var32, var38, var40, var20, var24);
      var5.addVertexWithUV(var32, var36, var40, var20, var24);
      var5.addVertexWithUV(var34, var36, var40, var22, var24);
      var5.addVertexWithUV(var34, var36, var42, var22, var26);
      var5.addVertexWithUV(var32, var36, var42, var20, var26);
      var20 = (double)var9.getInterpolatedU(12.0);
      var22 = (double)var9.getMaxU();
      var24 = (double)var9.getMinV();
      var26 = (double)var9.getInterpolatedV(4.0);
      var28 = 8.0;
      var30 = 0.0;
      double var45;
      switch (var7) {
         case 0:
            var28 = 8.0;
            var30 = 12.0;
            var45 = var20;
            var20 = var22;
            var22 = var45;
            break;
         case 1:
            var28 = 0.0;
            var30 = 8.0;
            break;
         case 2:
            var28 = 8.0;
            var30 = 0.0;
            break;
         case 3:
            var28 = 12.0;
            var30 = 8.0;
            var45 = var20;
            var20 = var22;
            var22 = var45;
      }

      var32 = (double)par2 + var28 / 16.0;
      var34 = (double)par2 + (var28 + 4.0) / 16.0;
      var36 = (double)par3 + 0.75;
      var38 = (double)par3 + 1.0;
      var40 = (double)par4 + var30 / 16.0;
      var42 = (double)par4 + (var30 + 4.0) / 16.0;
      if (var7 != 2 && var7 != 0) {
         if (var7 == 1 || var7 == 3) {
            var5.addVertexWithUV(var34, var36, var40, var20, var26);
            var5.addVertexWithUV(var32, var36, var40, var22, var26);
            var5.addVertexWithUV(var32, var38, var40, var22, var24);
            var5.addVertexWithUV(var34, var38, var40, var20, var24);
            var5.addVertexWithUV(var32, var36, var40, var22, var26);
            var5.addVertexWithUV(var34, var36, var40, var20, var26);
            var5.addVertexWithUV(var34, var38, var40, var20, var24);
            var5.addVertexWithUV(var32, var38, var40, var22, var24);
         }
      } else {
         var5.addVertexWithUV(var32, var36, var40, var22, var26);
         var5.addVertexWithUV(var32, var36, var42, var20, var26);
         var5.addVertexWithUV(var32, var38, var42, var20, var24);
         var5.addVertexWithUV(var32, var38, var40, var22, var24);
         var5.addVertexWithUV(var32, var36, var42, var20, var26);
         var5.addVertexWithUV(var32, var36, var40, var22, var26);
         var5.addVertexWithUV(var32, var38, var40, var22, var24);
         var5.addVertexWithUV(var32, var38, var42, var20, var24);
      }

      return true;
   }

   private boolean renderBlockBeacon(BlockBeacon par1BlockBeacon, int par2, int par3, int par4) {
      float var5 = 0.1875F;
      this.setOverrideBlockTexture(this.getBlockIcon(Block.glass));
      this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      this.renderStandardBlock(par1BlockBeacon, par2, par3, par4);
      this.renderAllFaces = true;
      this.setOverrideBlockTexture(this.getBlockIcon(Block.obsidian));
      this.setRenderBounds(0.125, 0.0062500000931322575, 0.125, 0.875, (double)var5, 0.875);
      this.renderStandardBlock(par1BlockBeacon, par2, par3, par4);
      this.setOverrideBlockTexture(this.getBlockIcon(Block.beacon));
      this.setRenderBounds(0.1875, (double)var5, 0.1875, 0.8125, 0.875, 0.8125);
      this.renderStandardBlock(par1BlockBeacon, par2, par3, par4);
      this.renderAllFaces = false;
      this.clearOverrideBlockTexture();
      return true;
   }

   public boolean renderBlockCactus(Block par1Block, int par2, int par3, int par4) {
      int var5 = par1Block.colorMultiplier(this.blockAccess, par2, par3, par4);
      float var6 = (float)(var5 >> 16 & 255) / 255.0F;
      float var7 = (float)(var5 >> 8 & 255) / 255.0F;
      float var8 = (float)(var5 & 255) / 255.0F;
      if (EntityRenderer.anaglyphEnable) {
         float var9 = (var6 * 30.0F + var7 * 59.0F + var8 * 11.0F) / 100.0F;
         float var10 = (var6 * 30.0F + var7 * 70.0F) / 100.0F;
         float var11 = (var6 * 30.0F + var8 * 70.0F) / 100.0F;
         var6 = var9;
         var7 = var10;
         var8 = var11;
      }

      return this.renderBlockCactusImpl(par1Block, par2, par3, par4, var6, var7, var8);
   }

   public boolean renderBlockCactusImpl(Block par1Block, int par2, int par3, int par4, float par5, float par6, float par7) {
      Tessellator var8 = Tessellator.instance;
      boolean var9 = false;
      float var10 = 0.5F;
      float var11 = 1.0F;
      float var12 = 0.8F;
      float var13 = 0.6F;
      float var14 = var10 * par5;
      float var15 = var11 * par5;
      float var16 = var12 * par5;
      float var17 = var13 * par5;
      float var18 = var10 * par6;
      float var19 = var11 * par6;
      float var20 = var12 * par6;
      float var21 = var13 * par6;
      float var22 = var10 * par7;
      float var23 = var11 * par7;
      float var24 = var12 * par7;
      float var25 = var13 * par7;
      float var26 = 0.0625F;
      int var27 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);
      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 - 1, par4, 0)) {
         var8.setBrightness(this.renderMinY > 0.0 ? var27 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4));
         var8.setColorOpaque_F(var14, var18, var22);
         this.renderFaceYNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 0));
      }

      if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 + 1, par4, 1)) {
         var8.setBrightness(this.renderMaxY < 1.0 ? var27 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4));
         var8.setColorOpaque_F(var15, var19, var23);
         this.renderFaceYPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 1));
      }

      var8.setBrightness(var27);
      var8.setColorOpaque_F(var16, var20, var24);
      var8.addTranslation(0.0F, 0.0F, var26);
      this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 2));
      var8.addTranslation(0.0F, 0.0F, -var26);
      var8.addTranslation(0.0F, 0.0F, -var26);
      this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3));
      var8.addTranslation(0.0F, 0.0F, var26);
      var8.setColorOpaque_F(var17, var21, var25);
      var8.addTranslation(var26, 0.0F, 0.0F);
      this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 4));
      var8.addTranslation(-var26, 0.0F, 0.0F);
      var8.addTranslation(-var26, 0.0F, 0.0F);
      this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 5));
      var8.addTranslation(var26, 0.0F, 0.0F);
      return true;
   }

   public boolean renderBlockFence(BlockFence par1BlockFence, int par2, int par3, int par4) {
      boolean var5 = false;
      float var6 = 0.375F;
      float var7 = 0.625F;
      this.setRenderBounds((double)var6, 0.0, (double)var6, (double)var7, 1.0, (double)var7);
      this.renderStandardBlock(par1BlockFence, par2, par3, par4);
      var5 = true;
      boolean var8 = false;
      boolean var9 = false;
      if (par1BlockFence.canConnectFenceTo(this.blockAccess, par2 - 1, par3, par4) || par1BlockFence.canConnectFenceTo(this.blockAccess, par2 + 1, par3, par4)) {
         var8 = true;
      }

      if (par1BlockFence.canConnectFenceTo(this.blockAccess, par2, par3, par4 - 1) || par1BlockFence.canConnectFenceTo(this.blockAccess, par2, par3, par4 + 1)) {
         var9 = true;
      }

      boolean var10 = par1BlockFence.canConnectFenceTo(this.blockAccess, par2 - 1, par3, par4);
      boolean var11 = par1BlockFence.canConnectFenceTo(this.blockAccess, par2 + 1, par3, par4);
      boolean var12 = par1BlockFence.canConnectFenceTo(this.blockAccess, par2, par3, par4 - 1);
      boolean var13 = par1BlockFence.canConnectFenceTo(this.blockAccess, par2, par3, par4 + 1);
      if (!var8 && !var9) {
         var8 = true;
      }

      var6 = 0.4375F;
      var7 = 0.5625F;
      float var14 = 0.75F;
      float var15 = 0.9375F;
      float var16 = var10 ? 0.0F : var6;
      float var17 = var11 ? 1.0F : var7;
      float var18 = var12 ? 0.0F : var6;
      float var19 = var13 ? 1.0F : var7;
      if (var8) {
         this.setRenderBounds((double)var16, (double)var14, (double)var6, (double)var17, (double)var15, (double)var7);
         this.renderStandardBlock(par1BlockFence, par2, par3, par4);
         var5 = true;
      }

      if (var9) {
         this.setRenderBounds((double)var6, (double)var14, (double)var18, (double)var7, (double)var15, (double)var19);
         this.renderStandardBlock(par1BlockFence, par2, par3, par4);
         var5 = true;
      }

      var14 = 0.375F;
      var15 = 0.5625F;
      if (var8) {
         this.setRenderBounds((double)var16, (double)var14, (double)var6, (double)var17, (double)var15, (double)var7);
         this.renderStandardBlock(par1BlockFence, par2, par3, par4);
         var5 = true;
      }

      if (var9) {
         this.setRenderBounds((double)var6, (double)var14, (double)var18, (double)var7, (double)var15, (double)var19);
         this.renderStandardBlock(par1BlockFence, par2, par3, par4);
         var5 = true;
      }

      par1BlockFence.setBlockBoundsBasedOnStateAndNeighbors(this.blockAccess, par2, par3, par4);
      return var5;
   }

   public boolean renderBlockWall(BlockWall par1BlockWall, int par2, int par3, int par4) {
      boolean var5 = par1BlockWall.canConnectWallTo(this.blockAccess, par2 - 1, par3, par4);
      boolean var6 = par1BlockWall.canConnectWallTo(this.blockAccess, par2 + 1, par3, par4);
      boolean var7 = par1BlockWall.canConnectWallTo(this.blockAccess, par2, par3, par4 - 1);
      boolean var8 = par1BlockWall.canConnectWallTo(this.blockAccess, par2, par3, par4 + 1);
      boolean var9 = var7 && var8 && !var5 && !var6;
      boolean var10 = !var7 && !var8 && var5 && var6;
      boolean var11 = this.blockAccess.isAirBlock(par2, par3 + 1, par4);
      if ((var9 || var10) && var11) {
         if (var9) {
            this.setRenderBounds(0.3125, 0.0, 0.0, 0.6875, 0.8125, 1.0);
            this.renderStandardBlock(par1BlockWall, par2, par3, par4);
         } else {
            this.setRenderBounds(0.0, 0.0, 0.3125, 1.0, 0.8125, 0.6875);
            this.renderStandardBlock(par1BlockWall, par2, par3, par4);
         }
      } else {
         this.setRenderBounds(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
         this.renderStandardBlock(par1BlockWall, par2, par3, par4);
         if (var5) {
            this.setRenderBounds(0.0, 0.0, 0.3125, 0.25, 0.8125, 0.6875);
            this.renderStandardBlock(par1BlockWall, par2, par3, par4);
         }

         if (var6) {
            this.setRenderBounds(0.75, 0.0, 0.3125, 1.0, 0.8125, 0.6875);
            this.renderStandardBlock(par1BlockWall, par2, par3, par4);
         }

         if (var7) {
            this.setRenderBounds(0.3125, 0.0, 0.0, 0.6875, 0.8125, 0.25);
            this.renderStandardBlock(par1BlockWall, par2, par3, par4);
         }

         if (var8) {
            this.setRenderBounds(0.3125, 0.0, 0.75, 0.6875, 0.8125, 1.0);
            this.renderStandardBlock(par1BlockWall, par2, par3, par4);
         }
      }

      par1BlockWall.setBlockBoundsBasedOnStateAndNeighbors(this.blockAccess, par2, par3, par4);
      return true;
   }

   public boolean renderBlockDragonEgg(BlockDragonEgg par1BlockDragonEgg, int par2, int par3, int par4) {
      boolean var5 = false;
      int var6 = 0;

      for(int var7 = 0; var7 < 8; ++var7) {
         byte var8 = 0;
         byte var9 = 1;
         if (var7 == 0) {
            var8 = 2;
         }

         if (var7 == 1) {
            var8 = 3;
         }

         if (var7 == 2) {
            var8 = 4;
         }

         if (var7 == 3) {
            var8 = 5;
            var9 = 2;
         }

         if (var7 == 4) {
            var8 = 6;
            var9 = 3;
         }

         if (var7 == 5) {
            var8 = 7;
            var9 = 5;
         }

         if (var7 == 6) {
            var8 = 6;
            var9 = 2;
         }

         if (var7 == 7) {
            var8 = 3;
         }

         float var10 = (float)var8 / 16.0F;
         float var11 = 1.0F - (float)var6 / 16.0F;
         float var12 = 1.0F - (float)(var6 + var9) / 16.0F;
         var6 += var9;
         this.setRenderBounds((double)(0.5F - var10), (double)var12, (double)(0.5F - var10), (double)(0.5F + var10), (double)var11, (double)(0.5F + var10));
         this.renderStandardBlock(par1BlockDragonEgg, par2, par3, par4);
      }

      var5 = true;
      this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      return var5;
   }

   public boolean renderBlockFenceGate(BlockFenceGate par1BlockFenceGate, int par2, int par3, int par4) {
      boolean var5 = true;
      int var6 = this.blockAccess.getBlockMetadata(par2, par3, par4);
      boolean var7 = BlockFenceGate.isFenceGateOpen(var6);
      int var8 = BlockFenceGate.j(var6);
      float var9 = 0.375F;
      float var10 = 0.5625F;
      float var11 = 0.75F;
      float var12 = 0.9375F;
      float var13 = 0.3125F;
      float var14 = 1.0F;
      if ((var8 == 2 || var8 == 0) && this.blockAccess.getBlockId(par2 - 1, par3, par4) == Block.cobblestoneWall.blockID && this.blockAccess.getBlockId(par2 + 1, par3, par4) == Block.cobblestoneWall.blockID || (var8 == 3 || var8 == 1) && this.blockAccess.getBlockId(par2, par3, par4 - 1) == Block.cobblestoneWall.blockID && this.blockAccess.getBlockId(par2, par3, par4 + 1) == Block.cobblestoneWall.blockID) {
         var9 -= 0.1875F;
         var10 -= 0.1875F;
         var11 -= 0.1875F;
         var12 -= 0.1875F;
         var13 -= 0.1875F;
         var14 -= 0.1875F;
      }

      this.renderAllFaces = true;
      float var15;
      float var17;
      float var16;
      float var18;
      if (var8 != 3 && var8 != 1) {
         var15 = 0.0F;
         var16 = 0.125F;
         var17 = 0.4375F;
         var18 = 0.5625F;
         this.setRenderBounds((double)var15, (double)var13, (double)var17, (double)var16, (double)var14, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         var15 = 0.875F;
         var16 = 1.0F;
         this.setRenderBounds((double)var15, (double)var13, (double)var17, (double)var16, (double)var14, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
      } else {
         this.uvRotateTop = 1;
         var15 = 0.4375F;
         var16 = 0.5625F;
         var17 = 0.0F;
         var18 = 0.125F;
         this.setRenderBounds((double)var15, (double)var13, (double)var17, (double)var16, (double)var14, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         var17 = 0.875F;
         var18 = 1.0F;
         this.setRenderBounds((double)var15, (double)var13, (double)var17, (double)var16, (double)var14, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         this.uvRotateTop = 0;
      }

      if (var7) {
         if (var8 == 2 || var8 == 0) {
            this.uvRotateTop = 1;
         }

         float var19;
         float var21;
         float var20;
         if (var8 == 3) {
            var15 = 0.0F;
            var16 = 0.125F;
            var17 = 0.875F;
            var18 = 1.0F;
            var19 = 0.5625F;
            var20 = 0.8125F;
            var21 = 0.9375F;
            this.setRenderBounds(0.8125, (double)var9, 0.0, 0.9375, (double)var12, 0.125);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.8125, (double)var9, 0.875, 0.9375, (double)var12, 1.0);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.5625, (double)var9, 0.0, 0.8125, (double)var10, 0.125);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.5625, (double)var9, 0.875, 0.8125, (double)var10, 1.0);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.5625, (double)var11, 0.0, 0.8125, (double)var12, 0.125);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.5625, (double)var11, 0.875, 0.8125, (double)var12, 1.0);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         } else if (var8 == 1) {
            var15 = 0.0F;
            var16 = 0.125F;
            var17 = 0.875F;
            var18 = 1.0F;
            var19 = 0.0625F;
            var20 = 0.1875F;
            var21 = 0.4375F;
            this.setRenderBounds(0.0625, (double)var9, 0.0, 0.1875, (double)var12, 0.125);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.0625, (double)var9, 0.875, 0.1875, (double)var12, 1.0);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.1875, (double)var9, 0.0, 0.4375, (double)var10, 0.125);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.1875, (double)var9, 0.875, 0.4375, (double)var10, 1.0);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.1875, (double)var11, 0.0, 0.4375, (double)var12, 0.125);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.1875, (double)var11, 0.875, 0.4375, (double)var12, 1.0);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         } else if (var8 == 0) {
            var15 = 0.0F;
            var16 = 0.125F;
            var17 = 0.875F;
            var18 = 1.0F;
            var19 = 0.5625F;
            var20 = 0.8125F;
            var21 = 0.9375F;
            this.setRenderBounds(0.0, (double)var9, 0.8125, 0.125, (double)var12, 0.9375);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.875, (double)var9, 0.8125, 1.0, (double)var12, 0.9375);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.0, (double)var9, 0.5625, 0.125, (double)var10, 0.8125);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.875, (double)var9, 0.5625, 1.0, (double)var10, 0.8125);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.0, (double)var11, 0.5625, 0.125, (double)var12, 0.8125);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.875, (double)var11, 0.5625, 1.0, (double)var12, 0.8125);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         } else if (var8 == 2) {
            var15 = 0.0F;
            var16 = 0.125F;
            var17 = 0.875F;
            var18 = 1.0F;
            var19 = 0.0625F;
            var20 = 0.1875F;
            var21 = 0.4375F;
            this.setRenderBounds(0.0, (double)var9, 0.0625, 0.125, (double)var12, 0.1875);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.875, (double)var9, 0.0625, 1.0, (double)var12, 0.1875);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.0, (double)var9, 0.1875, 0.125, (double)var10, 0.4375);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.875, (double)var9, 0.1875, 1.0, (double)var10, 0.4375);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.0, (double)var11, 0.1875, 0.125, (double)var12, 0.4375);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds(0.875, (double)var11, 0.1875, 1.0, (double)var12, 0.4375);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         }
      } else if (var8 != 3 && var8 != 1) {
         var15 = 0.375F;
         var16 = 0.5F;
         var17 = 0.4375F;
         var18 = 0.5625F;
         this.setRenderBounds((double)var15, (double)var9, (double)var17, (double)var16, (double)var12, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         var15 = 0.5F;
         var16 = 0.625F;
         this.setRenderBounds((double)var15, (double)var9, (double)var17, (double)var16, (double)var12, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         var15 = 0.625F;
         var16 = 0.875F;
         this.setRenderBounds((double)var15, (double)var9, (double)var17, (double)var16, (double)var10, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         this.setRenderBounds((double)var15, (double)var11, (double)var17, (double)var16, (double)var12, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         var15 = 0.125F;
         var16 = 0.375F;
         this.setRenderBounds((double)var15, (double)var9, (double)var17, (double)var16, (double)var10, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         this.setRenderBounds((double)var15, (double)var11, (double)var17, (double)var16, (double)var12, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
      } else {
         this.uvRotateTop = 1;
         var15 = 0.4375F;
         var16 = 0.5625F;
         var17 = 0.375F;
         var18 = 0.5F;
         this.setRenderBounds((double)var15, (double)var9, (double)var17, (double)var16, (double)var12, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         var17 = 0.5F;
         var18 = 0.625F;
         this.setRenderBounds((double)var15, (double)var9, (double)var17, (double)var16, (double)var12, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         var17 = 0.625F;
         var18 = 0.875F;
         this.setRenderBounds((double)var15, (double)var9, (double)var17, (double)var16, (double)var10, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         this.setRenderBounds((double)var15, (double)var11, (double)var17, (double)var16, (double)var12, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         var17 = 0.125F;
         var18 = 0.375F;
         this.setRenderBounds((double)var15, (double)var9, (double)var17, (double)var16, (double)var10, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
         this.setRenderBounds((double)var15, (double)var11, (double)var17, (double)var16, (double)var12, (double)var18);
         this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
      }

      this.renderAllFaces = false;
      this.uvRotateTop = 0;
      this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      return var5;
   }

   private boolean renderBlockHopper(BlockHopper par1BlockHopper, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      var5.setBrightness(par1BlockHopper.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
      float var6 = 1.0F;
      int var7 = par1BlockHopper.colorMultiplier(this.blockAccess, par2, par3, par4);
      float var8 = (float)(var7 >> 16 & 255) / 255.0F;
      float var9 = (float)(var7 >> 8 & 255) / 255.0F;
      float var10 = (float)(var7 & 255) / 255.0F;
      if (EntityRenderer.anaglyphEnable) {
         float var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
         float var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
         float var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
         var8 = var11;
         var9 = var12;
         var10 = var13;
      }

      var5.setColorOpaque_F(var6 * var8, var6 * var9, var6 * var10);
      return this.renderBlockHopperMetadata(par1BlockHopper, par2, par3, par4, this.blockAccess.getBlockMetadata(par2, par3, par4), false);
   }

   private boolean renderBlockHopperMetadata(BlockHopper par1BlockHopper, int par2, int par3, int par4, int par5, boolean par6) {
      Tessellator var7 = Tessellator.instance;
      int var8 = BlockHopper.getDirectionFromMetadata(par5);
      double var9 = 0.625;
      this.setRenderBounds(0.0, var9, 0.0, 1.0, 1.0, 1.0);
      if (par6) {
         var7.startDrawingQuads();
         var7.setNormal(0.0F, -1.0F, 0.0F);
         this.renderFaceYNeg(par1BlockHopper, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1BlockHopper, 0, par5));
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(0.0F, 1.0F, 0.0F);
         this.renderFaceYPos(par1BlockHopper, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1BlockHopper, 1, par5));
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(0.0F, 0.0F, -1.0F);
         this.renderFaceZNeg(par1BlockHopper, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1BlockHopper, 2, par5));
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(0.0F, 0.0F, 1.0F);
         this.renderFaceZPos(par1BlockHopper, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1BlockHopper, 3, par5));
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(-1.0F, 0.0F, 0.0F);
         this.renderFaceXNeg(par1BlockHopper, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1BlockHopper, 4, par5));
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(1.0F, 0.0F, 0.0F);
         this.renderFaceXPos(par1BlockHopper, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1BlockHopper, 5, par5));
         var7.draw();
      } else {
         this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
      }

      float var13;
      if (!par6) {
         var7.setBrightness(par1BlockHopper.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
         float var11 = 1.0F;
         int var12 = par1BlockHopper.colorMultiplier(this.blockAccess, par2, par3, par4);
         var13 = (float)(var12 >> 16 & 255) / 255.0F;
         float var14 = (float)(var12 >> 8 & 255) / 255.0F;
         float var15 = (float)(var12 & 255) / 255.0F;
         if (EntityRenderer.anaglyphEnable) {
            float var16 = (var13 * 30.0F + var14 * 59.0F + var15 * 11.0F) / 100.0F;
            float var17 = (var13 * 30.0F + var14 * 70.0F) / 100.0F;
            float var18 = (var13 * 30.0F + var15 * 70.0F) / 100.0F;
            var13 = var16;
            var14 = var17;
            var15 = var18;
         }

         var7.setColorOpaque_F(var11 * var13, var11 * var14, var11 * var15);
      }

      Icon var24 = BlockHopper.getHopperIcon("hopper_outside");
      Icon var25 = BlockHopper.getHopperIcon("hopper_inside");
      var13 = 0.125F;
      if (par6) {
         var7.startDrawingQuads();
         var7.setNormal(1.0F, 0.0F, 0.0F);
         this.renderFaceXPos(par1BlockHopper, (double)(-1.0F + var13), 0.0, 0.0, var24);
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(-1.0F, 0.0F, 0.0F);
         this.renderFaceXNeg(par1BlockHopper, (double)(1.0F - var13), 0.0, 0.0, var24);
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(0.0F, 0.0F, 1.0F);
         this.renderFaceZPos(par1BlockHopper, 0.0, 0.0, (double)(-1.0F + var13), var24);
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(0.0F, 0.0F, -1.0F);
         this.renderFaceZNeg(par1BlockHopper, 0.0, 0.0, (double)(1.0F - var13), var24);
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(0.0F, 1.0F, 0.0F);
         this.renderFaceYPos(par1BlockHopper, 0.0, -1.0 + var9, 0.0, var25);
         var7.draw();
      } else {
         this.renderFaceXPos(par1BlockHopper, (double)((float)par2 - 1.0F + var13), (double)par3, (double)par4, var24);
         this.renderFaceXNeg(par1BlockHopper, (double)((float)par2 + 1.0F - var13), (double)par3, (double)par4, var24);
         this.renderFaceZPos(par1BlockHopper, (double)par2, (double)par3, (double)((float)par4 - 1.0F + var13), var24);
         this.renderFaceZNeg(par1BlockHopper, (double)par2, (double)par3, (double)((float)par4 + 1.0F - var13), var24);
         this.renderFaceYPos(par1BlockHopper, (double)par2, (double)((float)par3 - 1.0F) + var9, (double)par4, var25);
      }

      this.setOverrideBlockTexture(var24);
      double var26 = 0.25;
      double var27 = 0.25;
      this.setRenderBounds(var26, var27, var26, 1.0 - var26, var9 - 0.002, 1.0 - var26);
      if (par6) {
         var7.startDrawingQuads();
         var7.setNormal(1.0F, 0.0F, 0.0F);
         this.renderFaceXPos(par1BlockHopper, 0.0, 0.0, 0.0, var24);
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(-1.0F, 0.0F, 0.0F);
         this.renderFaceXNeg(par1BlockHopper, 0.0, 0.0, 0.0, var24);
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(0.0F, 0.0F, 1.0F);
         this.renderFaceZPos(par1BlockHopper, 0.0, 0.0, 0.0, var24);
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(0.0F, 0.0F, -1.0F);
         this.renderFaceZNeg(par1BlockHopper, 0.0, 0.0, 0.0, var24);
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(0.0F, 1.0F, 0.0F);
         this.renderFaceYPos(par1BlockHopper, 0.0, 0.0, 0.0, var24);
         var7.draw();
         var7.startDrawingQuads();
         var7.setNormal(0.0F, -1.0F, 0.0F);
         this.renderFaceYNeg(par1BlockHopper, 0.0, 0.0, 0.0, var24);
         var7.draw();
      } else {
         this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
      }

      if (!par6) {
         double var20 = 0.375;
         double var22 = 0.25;
         this.setOverrideBlockTexture(var24);
         if (var8 == 0) {
            this.setRenderBounds(var20, 0.0, var20, 1.0 - var20, 0.25, 1.0 - var20);
            this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
         }

         if (var8 == 2) {
            this.setRenderBounds(var20, var27, 0.0, 1.0 - var20, var27 + var22, var26);
            this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
         }

         if (var8 == 3) {
            this.setRenderBounds(var20, var27, 1.0 - var26, 1.0 - var20, var27 + var22, 1.0);
            this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
         }

         if (var8 == 4) {
            this.setRenderBounds(0.0, var27, var20, var26, var27 + var22, 1.0 - var20);
            this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
         }

         if (var8 == 5) {
            this.setRenderBounds(1.0 - var26, var27, var20, 1.0, var27 + var22, 1.0 - var20);
            this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
         }
      }

      this.clearOverrideBlockTexture();
      return true;
   }

   public boolean renderBlockStairs(BlockStairs par1BlockStairs, int par2, int par3, int par4) {
      par1BlockStairs.func_82541_d(this.blockAccess, par2, par3, par4);
      this.setRenderBoundsForNonStandardFormBlock(par1BlockStairs);
      this.renderStandardBlock(par1BlockStairs, par2, par3, par4);
      boolean var5 = par1BlockStairs.func_82542_g(this.blockAccess, par2, par3, par4);
      this.setRenderBoundsForNonStandardFormBlock(par1BlockStairs);
      this.renderStandardBlock(par1BlockStairs, par2, par3, par4);
      if (var5 && par1BlockStairs.func_82544_h(this.blockAccess, par2, par3, par4)) {
         this.setRenderBoundsForNonStandardFormBlock(par1BlockStairs);
         this.renderStandardBlock(par1BlockStairs, par2, par3, par4);
      }

      return true;
   }

   public boolean renderBlockDoor(Block par1Block, int par2, int par3, int par4) {
      Tessellator var5 = Tessellator.instance;
      this.blockAccess.getBlockMetadata(par2, par3, par4);
      boolean var7 = false;
      float var8 = 0.5F;
      float var9 = 1.0F;
      float var10 = 0.8F;
      float var11 = 0.6F;
      int var12 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);
      var5.setBrightness(this.renderMinY > 0.0 ? var12 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4));
      var5.setColorOpaque_F(var8, var8, var8);
      this.renderFaceYNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 0));
      var7 = true;
      var5.setBrightness(this.renderMaxY < 1.0 ? var12 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4));
      var5.setColorOpaque_F(var9, var9, var9);
      this.renderFaceYPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 1));
      var7 = true;
      var5.setBrightness(this.renderMinZ > 0.0 ? var12 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1));
      var5.setColorOpaque_F(var10, var10, var10);
      Icon var14 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 2);
      this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, var14);
      var7 = true;
      this.flipTexture = false;
      var5.setBrightness(this.renderMaxZ < 1.0 ? var12 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1));
      var5.setColorOpaque_F(var10, var10, var10);
      var14 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3);
      this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, var14);
      var7 = true;
      this.flipTexture = false;
      var5.setBrightness(this.renderMinX > 0.0 ? var12 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4));
      var5.setColorOpaque_F(var11, var11, var11);
      var14 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 4);
      this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, var14);
      var7 = true;
      this.flipTexture = false;
      var5.setBrightness(this.renderMaxX < 1.0 ? var12 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4));
      var5.setColorOpaque_F(var11, var11, var11);
      var14 = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 5);
      this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, var14);
      var7 = true;
      this.flipTexture = false;
      return var7;
   }

   public void renderFaceYNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      Tessellator var9 = Tessellator.instance;
      if (this.overrideBlockTexture != null) {
         par8Icon = this.overrideBlockTexture;
      }

      double var10 = (double)par8Icon.getInterpolatedU(this.renderMinX * 16.0);
      double var12 = (double)par8Icon.getInterpolatedU(this.renderMaxX * 16.0);
      double var14 = (double)par8Icon.getInterpolatedV(this.renderMinZ * 16.0);
      double var16 = (double)par8Icon.getInterpolatedV(this.renderMaxZ * 16.0);
      if (this.renderMinX < 0.0 || this.renderMaxX > 1.0) {
         var10 = (double)par8Icon.getMinU();
         var12 = (double)par8Icon.getMaxU();
      }

      if (this.renderMinZ < 0.0 || this.renderMaxZ > 1.0) {
         var14 = (double)par8Icon.getMinV();
         var16 = (double)par8Icon.getMaxV();
      }

      double var18 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;
      if (this.uvRotateBottom == 2) {
         var10 = (double)par8Icon.getInterpolatedU(this.renderMinZ * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMaxX * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(this.renderMaxZ * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMinX * 16.0);
         var22 = var14;
         var24 = var16;
         var18 = var10;
         var20 = var12;
         var14 = var16;
         var16 = var22;
      } else if (this.uvRotateBottom == 1) {
         var10 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMaxZ * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(this.renderMinX * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMinZ * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(this.renderMaxX * 16.0);
         var18 = var12;
         var20 = var10;
         var10 = var12;
         var12 = var20;
         var22 = var16;
         var24 = var14;
      } else if (this.uvRotateBottom == 3) {
         var10 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMinX * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMaxX * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMinZ * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMaxZ * 16.0);
         var18 = var12;
         var20 = var10;
         var22 = var14;
         var24 = var16;
      }

      double var26 = par2 + this.renderMinX;
      double var28 = par2 + this.renderMaxX;
      double var30 = par4 + this.renderMinY;
      double var32 = par6 + this.renderMinZ;
      double var34 = par6 + this.renderMaxZ;
      if (this.enableAO) {
         if (RenderingScheme.current == 0) {
            var9.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            var9.setBrightness(this.brightnessTopLeft);
            var9.addVertexWithUV(var26, var30, var34, var20, var24);
            var9.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            var9.setBrightness(this.brightnessBottomLeft);
            var9.addVertexWithUV(var26, var30, var32, var10, var14);
            var9.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            var9.setBrightness(this.brightnessBottomRight);
            var9.addVertexWithUV(var28, var30, var32, var18, var22);
            var9.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            var9.setBrightness(this.brightnessTopRight);
            var9.addVertexWithUV(var28, var30, var34, var12, var16);
         } else {
            this.x[0] = var26;
            this.y[0] = var30;
            this.z[0] = var34;
            this.u[0] = var20;
            this.v[0] = var24;
            this.r[0] = this.colorRedTopLeft;
            this.g[0] = this.colorGreenTopLeft;
            this.b[0] = this.colorBlueTopLeft;
            this.brightness[0] = this.brightnessTopLeft;
            this.x[1] = var26;
            this.y[1] = var30;
            this.z[1] = var32;
            this.u[1] = var10;
            this.v[1] = var14;
            this.r[1] = this.colorRedBottomLeft;
            this.g[1] = this.colorGreenBottomLeft;
            this.b[1] = this.colorBlueBottomLeft;
            this.brightness[1] = this.brightnessBottomLeft;
            this.x[2] = var28;
            this.y[2] = var30;
            this.z[2] = var32;
            this.u[2] = var18;
            this.v[2] = var22;
            this.r[2] = this.colorRedBottomRight;
            this.g[2] = this.colorGreenBottomRight;
            this.b[2] = this.colorBlueBottomRight;
            this.brightness[2] = this.brightnessBottomRight;
            this.x[3] = var28;
            this.y[3] = var30;
            this.z[3] = var34;
            this.u[3] = var12;
            this.v[3] = var16;
            this.r[3] = this.colorRedTopRight;
            this.g[3] = this.colorGreenTopRight;
            this.b[3] = this.colorBlueTopRight;
            this.brightness[3] = this.brightnessTopRight;
            var9.add4VerticesWithUVandAO(this.x, this.y, this.z, this.u, this.v, this.r, this.g, this.b, this.brightness);
         }
      } else if (RenderingScheme.current == 0) {
         var9.addVertexWithUV(var26, var30, var34, var20, var24);
         var9.addVertexWithUV(var26, var30, var32, var10, var14);
         var9.addVertexWithUV(var28, var30, var32, var18, var22);
         var9.addVertexWithUV(var28, var30, var34, var12, var16);
      } else {
         this.x[0] = var26;
         this.y[0] = var30;
         this.z[0] = var34;
         this.u[0] = var20;
         this.v[0] = var24;
         this.x[1] = var26;
         this.y[1] = var30;
         this.z[1] = var32;
         this.u[1] = var10;
         this.v[1] = var14;
         this.x[2] = var28;
         this.y[2] = var30;
         this.z[2] = var32;
         this.u[2] = var18;
         this.v[2] = var22;
         this.x[3] = var28;
         this.y[3] = var30;
         this.z[3] = var34;
         this.u[3] = var12;
         this.v[3] = var16;
         var9.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
      }

   }

   public void renderFaceYPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      if (!Minecraft.see_through_block_tops) {
         Tessellator var9 = Tessellator.instance;
         if (this.overrideBlockTexture != null) {
            par8Icon = this.overrideBlockTexture;
         }

         double var10 = (double)par8Icon.getInterpolatedU(this.renderMinX * 16.0);
         double var12 = (double)par8Icon.getInterpolatedU(this.renderMaxX * 16.0);
         double var14 = (double)par8Icon.getInterpolatedV(this.renderMinZ * 16.0);
         double var16 = (double)par8Icon.getInterpolatedV(this.renderMaxZ * 16.0);
         if (this.renderMinX < 0.0 || this.renderMaxX > 1.0) {
            var10 = (double)par8Icon.getMinU();
            var12 = (double)par8Icon.getMaxU();
         }

         if (this.renderMinZ < 0.0 || this.renderMaxZ > 1.0) {
            var14 = (double)par8Icon.getMinV();
            var16 = (double)par8Icon.getMaxV();
         }

         double var18 = var12;
         double var20 = var10;
         double var22 = var14;
         double var24 = var16;
         if (this.uvRotateTop == 1) {
            var10 = (double)par8Icon.getInterpolatedU(this.renderMinZ * 16.0);
            var14 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMaxX * 16.0);
            var12 = (double)par8Icon.getInterpolatedU(this.renderMaxZ * 16.0);
            var16 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMinX * 16.0);
            var22 = var14;
            var24 = var16;
            var18 = var10;
            var20 = var12;
            var14 = var16;
            var16 = var22;
         } else if (this.uvRotateTop == 2) {
            var10 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMaxZ * 16.0);
            var14 = (double)par8Icon.getInterpolatedV(this.renderMinX * 16.0);
            var12 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMinZ * 16.0);
            var16 = (double)par8Icon.getInterpolatedV(this.renderMaxX * 16.0);
            var18 = var12;
            var20 = var10;
            var10 = var12;
            var12 = var20;
            var22 = var16;
            var24 = var14;
         } else if (this.uvRotateTop == 3) {
            var10 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMinX * 16.0);
            var12 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMaxX * 16.0);
            var14 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMinZ * 16.0);
            var16 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMaxZ * 16.0);
            var18 = var12;
            var20 = var10;
            var22 = var14;
            var24 = var16;
         }

         double var26 = par2 + this.renderMinX;
         double var28 = par2 + this.renderMaxX;
         double var30 = par4 + this.renderMaxY;
         double var32 = par6 + this.renderMinZ;
         double var34 = par6 + this.renderMaxZ;
         if (this.enableAO) {
            if (RenderingScheme.current == 0) {
               var9.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
               var9.setBrightness(this.brightnessTopLeft);
               var9.addVertexWithUV(var28, var30, var34, var12, var16);
               var9.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
               var9.setBrightness(this.brightnessBottomLeft);
               var9.addVertexWithUV(var28, var30, var32, var18, var22);
               var9.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
               var9.setBrightness(this.brightnessBottomRight);
               var9.addVertexWithUV(var26, var30, var32, var10, var14);
               var9.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
               var9.setBrightness(this.brightnessTopRight);
               var9.addVertexWithUV(var26, var30, var34, var20, var24);
            } else {
               this.x[0] = var28;
               this.y[0] = var30;
               this.z[0] = var34;
               this.u[0] = var12;
               this.v[0] = var16;
               this.r[0] = this.colorRedTopLeft;
               this.g[0] = this.colorGreenTopLeft;
               this.b[0] = this.colorBlueTopLeft;
               this.brightness[0] = this.brightnessTopLeft;
               this.x[1] = var28;
               this.y[1] = var30;
               this.z[1] = var32;
               this.u[1] = var18;
               this.v[1] = var22;
               this.r[1] = this.colorRedBottomLeft;
               this.g[1] = this.colorGreenBottomLeft;
               this.b[1] = this.colorBlueBottomLeft;
               this.brightness[1] = this.brightnessBottomLeft;
               this.x[2] = var26;
               this.y[2] = var30;
               this.z[2] = var32;
               this.u[2] = var10;
               this.v[2] = var14;
               this.r[2] = this.colorRedBottomRight;
               this.g[2] = this.colorGreenBottomRight;
               this.b[2] = this.colorBlueBottomRight;
               this.brightness[2] = this.brightnessBottomRight;
               this.x[3] = var26;
               this.y[3] = var30;
               this.z[3] = var34;
               this.u[3] = var20;
               this.v[3] = var24;
               this.r[3] = this.colorRedTopRight;
               this.g[3] = this.colorGreenTopRight;
               this.b[3] = this.colorBlueTopRight;
               this.brightness[3] = this.brightnessTopRight;
               var9.add4VerticesWithUVandAO(this.x, this.y, this.z, this.u, this.v, this.r, this.g, this.b, this.brightness);
            }
         } else if (RenderingScheme.current == 0) {
            var9.addVertexWithUV(var28, var30, var34, var12, var16);
            var9.addVertexWithUV(var28, var30, var32, var18, var22);
            var9.addVertexWithUV(var26, var30, var32, var10, var14);
            var9.addVertexWithUV(var26, var30, var34, var20, var24);
         } else {
            this.x[0] = var28;
            this.y[0] = var30;
            this.z[0] = var34;
            this.u[0] = var12;
            this.v[0] = var16;
            this.x[1] = var28;
            this.y[1] = var30;
            this.z[1] = var32;
            this.u[1] = var18;
            this.v[1] = var22;
            this.x[2] = var26;
            this.y[2] = var30;
            this.z[2] = var32;
            this.u[2] = var10;
            this.v[2] = var14;
            this.x[3] = var26;
            this.y[3] = var30;
            this.z[3] = var34;
            this.u[3] = var20;
            this.v[3] = var24;
            var9.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
         }

      }
   }

   public void renderFaceZNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      Tessellator var9 = Tessellator.instance;
      if (this.overrideBlockTexture != null) {
         par8Icon = this.overrideBlockTexture;
      }

      double var10 = (double)par8Icon.getInterpolatedU(this.renderMinX * 16.0);
      double var12 = (double)par8Icon.getInterpolatedU(this.renderMaxX * 16.0);
      double var14 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMaxY * 16.0);
      double var16 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMinY * 16.0);
      double var18;
      if (this.flipTexture) {
         var18 = var10;
         var10 = var12;
         var12 = var18;
      }

      if (this.renderMinX < 0.0 || this.renderMaxX > 1.0) {
         var10 = (double)par8Icon.getMinU();
         var12 = (double)par8Icon.getMaxU();
      }

      if (this.renderMinY < 0.0 || this.renderMaxY > 1.0) {
         var14 = (double)par8Icon.getMinV();
         var16 = (double)par8Icon.getMaxV();
      }

      var18 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;
      if (this.uvRotateEast == 2) {
         var10 = (double)par8Icon.getInterpolatedU(this.renderMinY * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMinX * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(this.renderMaxY * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMaxX * 16.0);
         var22 = var14;
         var24 = var16;
         var18 = var10;
         var20 = var12;
         var14 = var16;
         var16 = var22;
      } else if (this.uvRotateEast == 1) {
         var10 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMaxY * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(this.renderMaxX * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMinY * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(this.renderMinX * 16.0);
         var18 = var12;
         var20 = var10;
         var10 = var12;
         var12 = var20;
         var22 = var16;
         var24 = var14;
      } else if (this.uvRotateEast == 3) {
         var10 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMinX * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMaxX * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(this.renderMaxY * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(this.renderMinY * 16.0);
         var18 = var12;
         var20 = var10;
         var22 = var14;
         var24 = var16;
      }

      double var26 = par2 + this.renderMinX;
      double var28 = par2 + this.renderMaxX;
      double var30 = par4 + this.renderMinY;
      double var32 = par4 + this.renderMaxY;
      double var34 = par6 + this.renderMinZ;
      if (this.enableAO) {
         if (RenderingScheme.current == 0) {
            var9.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            var9.setBrightness(this.brightnessTopLeft);
            var9.addVertexWithUV(var26, var32, var34, var18, var22);
            var9.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            var9.setBrightness(this.brightnessBottomLeft);
            var9.addVertexWithUV(var28, var32, var34, var10, var14);
            var9.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            var9.setBrightness(this.brightnessBottomRight);
            var9.addVertexWithUV(var28, var30, var34, var20, var24);
            var9.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            var9.setBrightness(this.brightnessTopRight);
            var9.addVertexWithUV(var26, var30, var34, var12, var16);
         } else {
            this.x[0] = var26;
            this.y[0] = var32;
            this.z[0] = var34;
            this.u[0] = var18;
            this.v[0] = var22;
            this.r[0] = this.colorRedTopLeft;
            this.g[0] = this.colorGreenTopLeft;
            this.b[0] = this.colorBlueTopLeft;
            this.brightness[0] = this.brightnessTopLeft;
            this.x[1] = var28;
            this.y[1] = var32;
            this.z[1] = var34;
            this.u[1] = var10;
            this.v[1] = var14;
            this.r[1] = this.colorRedBottomLeft;
            this.g[1] = this.colorGreenBottomLeft;
            this.b[1] = this.colorBlueBottomLeft;
            this.brightness[1] = this.brightnessBottomLeft;
            this.x[2] = var28;
            this.y[2] = var30;
            this.z[2] = var34;
            this.u[2] = var20;
            this.v[2] = var24;
            this.r[2] = this.colorRedBottomRight;
            this.g[2] = this.colorGreenBottomRight;
            this.b[2] = this.colorBlueBottomRight;
            this.brightness[2] = this.brightnessBottomRight;
            this.x[3] = var26;
            this.y[3] = var30;
            this.z[3] = var34;
            this.u[3] = var12;
            this.v[3] = var16;
            this.r[3] = this.colorRedTopRight;
            this.g[3] = this.colorGreenTopRight;
            this.b[3] = this.colorBlueTopRight;
            this.brightness[3] = this.brightnessTopRight;
            var9.add4VerticesWithUVandAO(this.x, this.y, this.z, this.u, this.v, this.r, this.g, this.b, this.brightness);
         }
      } else if (RenderingScheme.current == 0) {
         var9.addVertexWithUV(var26, var32, var34, var18, var22);
         var9.addVertexWithUV(var28, var32, var34, var10, var14);
         var9.addVertexWithUV(var28, var30, var34, var20, var24);
         var9.addVertexWithUV(var26, var30, var34, var12, var16);
      } else {
         this.x[0] = var26;
         this.y[0] = var32;
         this.z[0] = var34;
         this.u[0] = var18;
         this.v[0] = var22;
         this.x[1] = var28;
         this.y[1] = var32;
         this.z[1] = var34;
         this.u[1] = var10;
         this.v[1] = var14;
         this.x[2] = var28;
         this.y[2] = var30;
         this.z[2] = var34;
         this.u[2] = var20;
         this.v[2] = var24;
         this.x[3] = var26;
         this.y[3] = var30;
         this.z[3] = var34;
         this.u[3] = var12;
         this.v[3] = var16;
         var9.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
      }

   }

   public void renderFaceZPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      Tessellator var9 = Tessellator.instance;
      if (this.overrideBlockTexture != null) {
         par8Icon = this.overrideBlockTexture;
      }

      double var10 = (double)par8Icon.getInterpolatedU(this.renderMinX * 16.0);
      double var12 = (double)par8Icon.getInterpolatedU(this.renderMaxX * 16.0);
      double var14 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMaxY * 16.0);
      double var16 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMinY * 16.0);
      double var18;
      if (this.flipTexture) {
         var18 = var10;
         var10 = var12;
         var12 = var18;
      }

      if (this.renderMinX < 0.0 || this.renderMaxX > 1.0) {
         var10 = (double)par8Icon.getMinU();
         var12 = (double)par8Icon.getMaxU();
      }

      if (this.renderMinY < 0.0 || this.renderMaxY > 1.0) {
         var14 = (double)par8Icon.getMinV();
         var16 = (double)par8Icon.getMaxV();
      }

      var18 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;
      if (this.uvRotateWest == 1) {
         var10 = (double)par8Icon.getInterpolatedU(this.renderMinY * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMinX * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(this.renderMaxY * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMaxX * 16.0);
         var22 = var14;
         var24 = var16;
         var18 = var10;
         var20 = var12;
         var14 = var16;
         var16 = var22;
      } else if (this.uvRotateWest == 2) {
         var10 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMaxY * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(this.renderMinX * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMinY * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(this.renderMaxX * 16.0);
         var18 = var12;
         var20 = var10;
         var10 = var12;
         var12 = var20;
         var22 = var16;
         var24 = var14;
      } else if (this.uvRotateWest == 3) {
         var10 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMinX * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMaxX * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(this.renderMaxY * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(this.renderMinY * 16.0);
         var18 = var12;
         var20 = var10;
         var22 = var14;
         var24 = var16;
      }

      double var26 = par2 + this.renderMinX;
      double var28 = par2 + this.renderMaxX;
      double var30 = par4 + this.renderMinY;
      double var32 = par4 + this.renderMaxY;
      double var34 = par6 + this.renderMaxZ;
      if (this.enableAO) {
         if (RenderingScheme.current == 0) {
            var9.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            var9.setBrightness(this.brightnessTopLeft);
            var9.addVertexWithUV(var26, var32, var34, var10, var14);
            var9.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            var9.setBrightness(this.brightnessBottomLeft);
            var9.addVertexWithUV(var26, var30, var34, var20, var24);
            var9.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            var9.setBrightness(this.brightnessBottomRight);
            var9.addVertexWithUV(var28, var30, var34, var12, var16);
            var9.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            var9.setBrightness(this.brightnessTopRight);
            var9.addVertexWithUV(var28, var32, var34, var18, var22);
         } else {
            this.x[0] = var26;
            this.y[0] = var32;
            this.z[0] = var34;
            this.u[0] = var10;
            this.v[0] = var14;
            this.r[0] = this.colorRedTopLeft;
            this.g[0] = this.colorGreenTopLeft;
            this.b[0] = this.colorBlueTopLeft;
            this.brightness[0] = this.brightnessTopLeft;
            this.x[1] = var26;
            this.y[1] = var30;
            this.z[1] = var34;
            this.u[1] = var20;
            this.v[1] = var24;
            this.r[1] = this.colorRedBottomLeft;
            this.g[1] = this.colorGreenBottomLeft;
            this.b[1] = this.colorBlueBottomLeft;
            this.brightness[1] = this.brightnessBottomLeft;
            this.x[2] = var28;
            this.y[2] = var30;
            this.z[2] = var34;
            this.u[2] = var12;
            this.v[2] = var16;
            this.r[2] = this.colorRedBottomRight;
            this.g[2] = this.colorGreenBottomRight;
            this.b[2] = this.colorBlueBottomRight;
            this.brightness[2] = this.brightnessBottomRight;
            this.x[3] = var28;
            this.y[3] = var32;
            this.z[3] = var34;
            this.u[3] = var18;
            this.v[3] = var22;
            this.r[3] = this.colorRedTopRight;
            this.g[3] = this.colorGreenTopRight;
            this.b[3] = this.colorBlueTopRight;
            this.brightness[3] = this.brightnessTopRight;
            var9.add4VerticesWithUVandAO(this.x, this.y, this.z, this.u, this.v, this.r, this.g, this.b, this.brightness);
         }
      } else if (RenderingScheme.current == 0) {
         var9.addVertexWithUV(var26, var32, var34, var10, var14);
         var9.addVertexWithUV(var26, var30, var34, var20, var24);
         var9.addVertexWithUV(var28, var30, var34, var12, var16);
         var9.addVertexWithUV(var28, var32, var34, var18, var22);
      } else {
         this.x[0] = var26;
         this.y[0] = var32;
         this.z[0] = var34;
         this.u[0] = var10;
         this.v[0] = var14;
         this.x[1] = var26;
         this.y[1] = var30;
         this.z[1] = var34;
         this.u[1] = var20;
         this.v[1] = var24;
         this.x[2] = var28;
         this.y[2] = var30;
         this.z[2] = var34;
         this.u[2] = var12;
         this.v[2] = var16;
         this.x[3] = var28;
         this.y[3] = var32;
         this.z[3] = var34;
         this.u[3] = var18;
         this.v[3] = var22;
         var9.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
      }

   }

   public void renderFaceXNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      Tessellator var9 = Tessellator.instance;
      if (this.overrideBlockTexture != null) {
         par8Icon = this.overrideBlockTexture;
      }

      double var10 = (double)par8Icon.getInterpolatedU(this.renderMinZ * 16.0);
      double var12 = (double)par8Icon.getInterpolatedU(this.renderMaxZ * 16.0);
      double var14 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMaxY * 16.0);
      double var16 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMinY * 16.0);
      double var18;
      if (this.flipTexture) {
         var18 = var10;
         var10 = var12;
         var12 = var18;
      }

      if (this.renderMinZ < 0.0 || this.renderMaxZ > 1.0) {
         var10 = (double)par8Icon.getMinU();
         var12 = (double)par8Icon.getMaxU();
      }

      if (this.renderMinY < 0.0 || this.renderMaxY > 1.0) {
         var14 = (double)par8Icon.getMinV();
         var16 = (double)par8Icon.getMaxV();
      }

      var18 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;
      if (this.uvRotateNorth == 1) {
         var10 = (double)par8Icon.getInterpolatedU(this.renderMinY * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMaxZ * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(this.renderMaxY * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMinZ * 16.0);
         var22 = var14;
         var24 = var16;
         var18 = var10;
         var20 = var12;
         var14 = var16;
         var16 = var22;
      } else if (this.uvRotateNorth == 2) {
         var10 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMaxY * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(this.renderMinZ * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMinY * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(this.renderMaxZ * 16.0);
         var18 = var12;
         var20 = var10;
         var10 = var12;
         var12 = var20;
         var22 = var16;
         var24 = var14;
      } else if (this.uvRotateNorth == 3) {
         var10 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMinZ * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMaxZ * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(this.renderMaxY * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(this.renderMinY * 16.0);
         var18 = var12;
         var20 = var10;
         var22 = var14;
         var24 = var16;
      }

      double var26 = par2 + this.renderMinX;
      double var28 = par4 + this.renderMinY;
      double var30 = par4 + this.renderMaxY;
      double var32 = par6 + this.renderMinZ;
      double var34 = par6 + this.renderMaxZ;
      if (this.enableAO) {
         if (RenderingScheme.current == 0) {
            var9.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            var9.setBrightness(this.brightnessTopLeft);
            var9.addVertexWithUV(var26, var30, var34, var18, var22);
            var9.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            var9.setBrightness(this.brightnessBottomLeft);
            var9.addVertexWithUV(var26, var30, var32, var10, var14);
            var9.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            var9.setBrightness(this.brightnessBottomRight);
            var9.addVertexWithUV(var26, var28, var32, var20, var24);
            var9.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            var9.setBrightness(this.brightnessTopRight);
            var9.addVertexWithUV(var26, var28, var34, var12, var16);
         } else {
            this.x[0] = var26;
            this.y[0] = var30;
            this.z[0] = var34;
            this.u[0] = var18;
            this.v[0] = var22;
            this.r[0] = this.colorRedTopLeft;
            this.g[0] = this.colorGreenTopLeft;
            this.b[0] = this.colorBlueTopLeft;
            this.brightness[0] = this.brightnessTopLeft;
            this.x[1] = var26;
            this.y[1] = var30;
            this.z[1] = var32;
            this.u[1] = var10;
            this.v[1] = var14;
            this.r[1] = this.colorRedBottomLeft;
            this.g[1] = this.colorGreenBottomLeft;
            this.b[1] = this.colorBlueBottomLeft;
            this.brightness[1] = this.brightnessBottomLeft;
            this.x[2] = var26;
            this.y[2] = var28;
            this.z[2] = var32;
            this.u[2] = var20;
            this.v[2] = var24;
            this.r[2] = this.colorRedBottomRight;
            this.g[2] = this.colorGreenBottomRight;
            this.b[2] = this.colorBlueBottomRight;
            this.brightness[2] = this.brightnessBottomRight;
            this.x[3] = var26;
            this.y[3] = var28;
            this.z[3] = var34;
            this.u[3] = var12;
            this.v[3] = var16;
            this.r[3] = this.colorRedTopRight;
            this.g[3] = this.colorGreenTopRight;
            this.b[3] = this.colorBlueTopRight;
            this.brightness[3] = this.brightnessTopRight;
            var9.add4VerticesWithUVandAO(this.x, this.y, this.z, this.u, this.v, this.r, this.g, this.b, this.brightness);
         }
      } else if (RenderingScheme.current == 0) {
         var9.addVertexWithUV(var26, var30, var34, var18, var22);
         var9.addVertexWithUV(var26, var30, var32, var10, var14);
         var9.addVertexWithUV(var26, var28, var32, var20, var24);
         var9.addVertexWithUV(var26, var28, var34, var12, var16);
      } else {
         this.x[0] = var26;
         this.y[0] = var30;
         this.z[0] = var34;
         this.u[0] = var18;
         this.v[0] = var22;
         this.x[1] = var26;
         this.y[1] = var30;
         this.z[1] = var32;
         this.u[1] = var10;
         this.v[1] = var14;
         this.x[2] = var26;
         this.y[2] = var28;
         this.z[2] = var32;
         this.u[2] = var20;
         this.v[2] = var24;
         this.x[3] = var26;
         this.y[3] = var28;
         this.z[3] = var34;
         this.u[3] = var12;
         this.v[3] = var16;
         var9.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
      }

   }

   public void renderFaceXPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      Tessellator var9 = Tessellator.instance;
      if (this.overrideBlockTexture != null) {
         par8Icon = this.overrideBlockTexture;
      }

      double var10 = (double)par8Icon.getInterpolatedU(this.renderMinZ * 16.0);
      double var12 = (double)par8Icon.getInterpolatedU(this.renderMaxZ * 16.0);
      double var14 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMaxY * 16.0);
      double var16 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMinY * 16.0);
      double var18;
      if (this.flipTexture) {
         var18 = var10;
         var10 = var12;
         var12 = var18;
      }

      if (this.renderMinZ < 0.0 || this.renderMaxZ > 1.0) {
         var10 = (double)par8Icon.getMinU();
         var12 = (double)par8Icon.getMaxU();
      }

      if (this.renderMinY < 0.0 || this.renderMaxY > 1.0) {
         var14 = (double)par8Icon.getMinV();
         var16 = (double)par8Icon.getMaxV();
      }

      var18 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;
      if (this.uvRotateSouth == 2) {
         var10 = (double)par8Icon.getInterpolatedU(this.renderMinY * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMinZ * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(this.renderMaxY * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(16.0 - this.renderMaxZ * 16.0);
         var22 = var14;
         var24 = var16;
         var18 = var10;
         var20 = var12;
         var14 = var16;
         var16 = var22;
      } else if (this.uvRotateSouth == 1) {
         var10 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMaxY * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(this.renderMaxZ * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMinY * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(this.renderMinZ * 16.0);
         var18 = var12;
         var20 = var10;
         var10 = var12;
         var12 = var20;
         var22 = var16;
         var24 = var14;
      } else if (this.uvRotateSouth == 3) {
         var10 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMinZ * 16.0);
         var12 = (double)par8Icon.getInterpolatedU(16.0 - this.renderMaxZ * 16.0);
         var14 = (double)par8Icon.getInterpolatedV(this.renderMaxY * 16.0);
         var16 = (double)par8Icon.getInterpolatedV(this.renderMinY * 16.0);
         var18 = var12;
         var20 = var10;
         var22 = var14;
         var24 = var16;
      }

      double var26 = par2 + this.renderMaxX;
      double var28 = par4 + this.renderMinY;
      double var30 = par4 + this.renderMaxY;
      double var32 = par6 + this.renderMinZ;
      double var34 = par6 + this.renderMaxZ;
      if (this.enableAO) {
         if (RenderingScheme.current == 0) {
            var9.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            var9.setBrightness(this.brightnessTopLeft);
            var9.addVertexWithUV(var26, var28, var34, var20, var24);
            var9.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            var9.setBrightness(this.brightnessBottomLeft);
            var9.addVertexWithUV(var26, var28, var32, var12, var16);
            var9.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            var9.setBrightness(this.brightnessBottomRight);
            var9.addVertexWithUV(var26, var30, var32, var18, var22);
            var9.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            var9.setBrightness(this.brightnessTopRight);
            var9.addVertexWithUV(var26, var30, var34, var10, var14);
         } else {
            this.x[0] = var26;
            this.y[0] = var28;
            this.z[0] = var34;
            this.u[0] = var20;
            this.v[0] = var24;
            this.r[0] = this.colorRedTopLeft;
            this.g[0] = this.colorGreenTopLeft;
            this.b[0] = this.colorBlueTopLeft;
            this.brightness[0] = this.brightnessTopLeft;
            this.x[1] = var26;
            this.y[1] = var28;
            this.z[1] = var32;
            this.u[1] = var12;
            this.v[1] = var16;
            this.r[1] = this.colorRedBottomLeft;
            this.g[1] = this.colorGreenBottomLeft;
            this.b[1] = this.colorBlueBottomLeft;
            this.brightness[1] = this.brightnessBottomLeft;
            this.x[2] = var26;
            this.y[2] = var30;
            this.z[2] = var32;
            this.u[2] = var18;
            this.v[2] = var22;
            this.r[2] = this.colorRedBottomRight;
            this.g[2] = this.colorGreenBottomRight;
            this.b[2] = this.colorBlueBottomRight;
            this.brightness[2] = this.brightnessBottomRight;
            this.x[3] = var26;
            this.y[3] = var30;
            this.z[3] = var34;
            this.u[3] = var10;
            this.v[3] = var14;
            this.r[3] = this.colorRedTopRight;
            this.g[3] = this.colorGreenTopRight;
            this.b[3] = this.colorBlueTopRight;
            this.brightness[3] = this.brightnessTopRight;
            var9.add4VerticesWithUVandAO(this.x, this.y, this.z, this.u, this.v, this.r, this.g, this.b, this.brightness);
         }
      } else if (RenderingScheme.current == 0) {
         var9.addVertexWithUV(var26, var28, var34, var20, var24);
         var9.addVertexWithUV(var26, var28, var32, var12, var16);
         var9.addVertexWithUV(var26, var30, var32, var18, var22);
         var9.addVertexWithUV(var26, var30, var34, var10, var14);
      } else {
         this.x[0] = var26;
         this.y[0] = var28;
         this.z[0] = var34;
         this.u[0] = var20;
         this.v[0] = var24;
         this.x[1] = var26;
         this.y[1] = var28;
         this.z[1] = var32;
         this.u[1] = var12;
         this.v[1] = var16;
         this.x[2] = var26;
         this.y[2] = var30;
         this.z[2] = var32;
         this.u[2] = var18;
         this.v[2] = var22;
         this.x[3] = var26;
         this.y[3] = var30;
         this.z[3] = var34;
         this.u[3] = var10;
         this.v[3] = var14;
         var9.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
      }

   }

   public void renderBlockAsItem(Block par1Block, int par2, float par3) {
      Tessellator var4 = Tessellator.instance;
      boolean var5 = par1Block.blockID == Block.grass.blockID;
      if (par1Block == Block.dispenser || par1Block == Block.dropper || par1Block instanceof BlockFurnace) {
         par2 = 3;
      }

      int var6;
      float var7;
      float var8;
      float var9;
      if (this.useInventoryTint) {
         var6 = par1Block.getRenderColor(par2);
         if (var5) {
            var6 = 16777215;
         }

         var7 = (float)(var6 >> 16 & 255) / 255.0F;
         var8 = (float)(var6 >> 8 & 255) / 255.0F;
         var9 = (float)(var6 & 255) / 255.0F;
         GL11.glColor4f(var7 * par3, var8 * par3, var9 * par3, 1.0F);
      }

      var6 = par1Block.getRenderType();
      if (par1Block.isAlwaysStandardFormCube()) {
         this.setRenderBoundsForStandardFormBlock();
      } else {
         this.setRenderBoundsForNonStandardFormBlock(par1Block);
      }

      int var14;
      if (var6 != 0 && var6 != 31 && var6 != 39 && var6 != 16 && var6 != 26) {
         if (var6 == 1) {
            var4.startDrawingQuads();
            var4.setNormal(0.0F, -1.0F, 0.0F);
            this.drawCrossedSquares(par1Block, par2, -0.5, -0.5, -0.5, 1.0F);
            var4.draw();
         } else if (var6 == 19) {
            var4.startDrawingQuads();
            var4.setNormal(0.0F, -1.0F, 0.0F);
            par1Block.setBlockBoundsForItemRender(par2);
            this.renderBlockStemSmall(par1Block, par2, this.renderMaxY, -0.5, -0.5, -0.5);
            var4.draw();
         } else if (var6 == 23) {
            var4.startDrawingQuads();
            var4.setNormal(0.0F, -1.0F, 0.0F);
            par1Block.setBlockBoundsForItemRender(par2);
            var4.draw();
         } else if (var6 == 13) {
            par1Block.setBlockBoundsForItemRender(par2);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            var7 = 0.0625F;
            var4.startDrawingQuads();
            var4.setNormal(0.0F, -1.0F, 0.0F);
            this.renderFaceYNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 0));
            var4.draw();
            var4.startDrawingQuads();
            var4.setNormal(0.0F, 1.0F, 0.0F);
            this.renderFaceYPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 1));
            var4.draw();
            var4.startDrawingQuads();
            var4.setNormal(0.0F, 0.0F, -1.0F);
            var4.addTranslation(0.0F, 0.0F, var7);
            this.renderFaceZNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 2));
            var4.addTranslation(0.0F, 0.0F, -var7);
            var4.draw();
            var4.startDrawingQuads();
            var4.setNormal(0.0F, 0.0F, 1.0F);
            var4.addTranslation(0.0F, 0.0F, -var7);
            this.renderFaceZPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 3));
            var4.addTranslation(0.0F, 0.0F, var7);
            var4.draw();
            var4.startDrawingQuads();
            var4.setNormal(-1.0F, 0.0F, 0.0F);
            var4.addTranslation(var7, 0.0F, 0.0F);
            this.renderFaceXNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 4));
            var4.addTranslation(-var7, 0.0F, 0.0F);
            var4.draw();
            var4.startDrawingQuads();
            var4.setNormal(1.0F, 0.0F, 0.0F);
            var4.addTranslation(-var7, 0.0F, 0.0F);
            this.renderFaceXPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 5));
            var4.addTranslation(var7, 0.0F, 0.0F);
            var4.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
         } else if (var6 == 22) {
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            ChestItemRenderHelper.instance.renderChest(par1Block, par2, par3);
            GL11.glEnable(32826);
         } else if (var6 == 6) {
            var4.startDrawingQuads();
            var4.setNormal(0.0F, -1.0F, 0.0F);
            this.renderBlockCropsImpl(par1Block, par2, -0.5, -0.5, -0.5);
            var4.draw();
         } else if (var6 == 2) {
            var4.startDrawingQuads();
            var4.setNormal(0.0F, -1.0F, 0.0F);
            this.renderTorchAtAngle(par1Block, -0.5, -0.5, -0.5, 0.0, 0.0, 0);
            var4.draw();
         } else if (var6 == 10) {
            for(var14 = 0; var14 < 2; ++var14) {
               if (var14 == 0) {
                  this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.5);
               }

               if (var14 == 1) {
                  this.setRenderBounds(0.0, 0.0, 0.5, 1.0, 0.5, 1.0);
               }

               GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
               var4.startDrawingQuads();
               var4.setNormal(0.0F, -1.0F, 0.0F);
               this.renderFaceYNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 0));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 1.0F, 0.0F);
               this.renderFaceYPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 1));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 0.0F, -1.0F);
               this.renderFaceZNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 2));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 0.0F, 1.0F);
               this.renderFaceZPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 3));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(-1.0F, 0.0F, 0.0F);
               this.renderFaceXNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 4));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(1.0F, 0.0F, 0.0F);
               this.renderFaceXPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 5));
               var4.draw();
               GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
         } else if (var6 == 27) {
            var14 = 0;
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            var4.startDrawingQuads();

            for(int var15 = 0; var15 < 8; ++var15) {
               byte var16 = 0;
               byte var17 = 1;
               if (var15 == 0) {
                  var16 = 2;
               }

               if (var15 == 1) {
                  var16 = 3;
               }

               if (var15 == 2) {
                  var16 = 4;
               }

               if (var15 == 3) {
                  var16 = 5;
                  var17 = 2;
               }

               if (var15 == 4) {
                  var16 = 6;
                  var17 = 3;
               }

               if (var15 == 5) {
                  var16 = 7;
                  var17 = 5;
               }

               if (var15 == 6) {
                  var16 = 6;
                  var17 = 2;
               }

               if (var15 == 7) {
                  var16 = 3;
               }

               float var11 = (float)var16 / 16.0F;
               float var12 = 1.0F - (float)var14 / 16.0F;
               float var13 = 1.0F - (float)(var14 + var17) / 16.0F;
               var14 += var17;
               this.setRenderBounds((double)(0.5F - var11), (double)var13, (double)(0.5F - var11), (double)(0.5F + var11), (double)var12, (double)(0.5F + var11));
               var4.setNormal(0.0F, -1.0F, 0.0F);
               this.renderFaceYNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 0));
               var4.setNormal(0.0F, 1.0F, 0.0F);
               this.renderFaceYPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 1));
               var4.setNormal(0.0F, 0.0F, -1.0F);
               this.renderFaceZNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 2));
               var4.setNormal(0.0F, 0.0F, 1.0F);
               this.renderFaceZPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 3));
               var4.setNormal(-1.0F, 0.0F, 0.0F);
               this.renderFaceXNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 4));
               var4.setNormal(1.0F, 0.0F, 0.0F);
               this.renderFaceXPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 5));
            }

            var4.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         } else if (var6 == 11) {
            for(var14 = 0; var14 < 4; ++var14) {
               var8 = 0.125F;
               if (var14 == 0) {
                  this.setRenderBounds((double)(0.5F - var8), 0.0, 0.0, (double)(0.5F + var8), 1.0, (double)(var8 * 2.0F));
               }

               if (var14 == 1) {
                  this.setRenderBounds((double)(0.5F - var8), 0.0, (double)(1.0F - var8 * 2.0F), (double)(0.5F + var8), 1.0, 1.0);
               }

               var8 = 0.0625F;
               if (var14 == 2) {
                  this.setRenderBounds((double)(0.5F - var8), (double)(1.0F - var8 * 3.0F), (double)(-var8 * 2.0F), (double)(0.5F + var8), (double)(1.0F - var8), (double)(1.0F + var8 * 2.0F));
               }

               if (var14 == 3) {
                  this.setRenderBounds((double)(0.5F - var8), (double)(0.5F - var8 * 3.0F), (double)(-var8 * 2.0F), (double)(0.5F + var8), (double)(0.5F - var8), (double)(1.0F + var8 * 2.0F));
               }

               GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
               var4.startDrawingQuads();
               var4.setNormal(0.0F, -1.0F, 0.0F);
               this.renderFaceYNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 0));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 1.0F, 0.0F);
               this.renderFaceYPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 1));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 0.0F, -1.0F);
               this.renderFaceZNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 2));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 0.0F, 1.0F);
               this.renderFaceZPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 3));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(-1.0F, 0.0F, 0.0F);
               this.renderFaceXNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 4));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(1.0F, 0.0F, 0.0F);
               this.renderFaceXPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 5));
               var4.draw();
               GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }

            this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         } else if (var6 == 21) {
            for(var14 = 0; var14 < 3; ++var14) {
               var8 = 0.0625F;
               if (var14 == 0) {
                  this.setRenderBounds((double)(0.5F - var8), 0.30000001192092896, 0.0, (double)(0.5F + var8), 1.0, (double)(var8 * 2.0F));
               }

               if (var14 == 1) {
                  this.setRenderBounds((double)(0.5F - var8), 0.30000001192092896, (double)(1.0F - var8 * 2.0F), (double)(0.5F + var8), 1.0, 1.0);
               }

               var8 = 0.0625F;
               if (var14 == 2) {
                  this.setRenderBounds((double)(0.5F - var8), 0.5, 0.0, (double)(0.5F + var8), (double)(1.0F - var8), 1.0);
               }

               GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
               var4.startDrawingQuads();
               var4.setNormal(0.0F, -1.0F, 0.0F);
               this.renderFaceYNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 0));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 1.0F, 0.0F);
               this.renderFaceYPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 1));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 0.0F, -1.0F);
               this.renderFaceZNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 2));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 0.0F, 1.0F);
               this.renderFaceZPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 3));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(-1.0F, 0.0F, 0.0F);
               this.renderFaceXNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 4));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(1.0F, 0.0F, 0.0F);
               this.renderFaceXPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(par1Block, 5));
               var4.draw();
               GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
         } else if (var6 == 32) {
            for(var14 = 0; var14 < 2; ++var14) {
               if (var14 == 0) {
                  this.setRenderBounds(0.0, 0.0, 0.3125, 1.0, 0.8125, 0.6875);
               }

               if (var14 == 1) {
                  this.setRenderBounds(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
               }

               GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
               var4.startDrawingQuads();
               var4.setNormal(0.0F, -1.0F, 0.0F);
               this.renderFaceYNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 0, par2));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 1.0F, 0.0F);
               this.renderFaceYPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 1, par2));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 0.0F, -1.0F);
               this.renderFaceZNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 2, par2));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 0.0F, 1.0F);
               this.renderFaceZPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 3, par2));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(-1.0F, 0.0F, 0.0F);
               this.renderFaceXNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 4, par2));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(1.0F, 0.0F, 0.0F);
               this.renderFaceXPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 5, par2));
               var4.draw();
               GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }

            this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         } else if (var6 == 35) {
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            this.renderBlockAnvilOrient((BlockAnvil)par1Block, 0, 0, 0, par2 << 2, true);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
         } else if (var6 == 34) {
            for(var14 = 0; var14 < 3; ++var14) {
               if (var14 == 0) {
                  this.setRenderBounds(0.125, 0.0, 0.125, 0.875, 0.1875, 0.875);
                  this.setOverrideBlockTexture(this.getBlockIcon(Block.obsidian));
               } else if (var14 == 1) {
                  this.setRenderBounds(0.1875, 0.1875, 0.1875, 0.8125, 0.875, 0.8125);
                  this.setOverrideBlockTexture(this.getBlockIcon(Block.beacon));
               } else if (var14 == 2) {
                  this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
                  this.setOverrideBlockTexture(this.getBlockIcon(Block.glass));
               }

               GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
               var4.startDrawingQuads();
               var4.setNormal(0.0F, -1.0F, 0.0F);
               this.renderFaceYNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 0, par2));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 1.0F, 0.0F);
               this.renderFaceYPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 1, par2));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 0.0F, -1.0F);
               this.renderFaceZNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 2, par2));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(0.0F, 0.0F, 1.0F);
               this.renderFaceZPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 3, par2));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(-1.0F, 0.0F, 0.0F);
               this.renderFaceXNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 4, par2));
               var4.draw();
               var4.startDrawingQuads();
               var4.setNormal(1.0F, 0.0F, 0.0F);
               this.renderFaceXPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 5, par2));
               var4.draw();
               GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }

            this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
            this.clearOverrideBlockTexture();
         } else if (var6 == 38) {
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            this.renderBlockHopperMetadata((BlockHopper)par1Block, 0, 0, 0, 0, true);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
         } else
         {
            FMLRenderAccessLibrary.renderInventoryBlock(this, par1Block, par2, var6);
         }
      } else {
         if (var6 == 16) {
            par2 = 1;
         }

         par1Block.setBlockBoundsForItemRender(par2);
         if (par1Block.isAlwaysStandardFormCube()) {
            this.setRenderBoundsForStandardFormBlock();
         } else {
            this.setRenderBoundsForNonStandardFormBlock(par1Block);
         }

         GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
         GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
         var4.startDrawingQuads();
         var4.setNormal(0.0F, -1.0F, 0.0F);
         this.renderFaceYNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 0, par2));
         var4.draw();
         if (var5 && this.useInventoryTint) {
            var14 = par1Block.getRenderColor(par2);
            var8 = (float)(var14 >> 16 & 255) / 255.0F;
            var9 = (float)(var14 >> 8 & 255) / 255.0F;
            float var10 = (float)(var14 & 255) / 255.0F;
            GL11.glColor4f(var8 * par3, var9 * par3, var10 * par3, 1.0F);
         }

         var4.startDrawingQuads();
         var4.setNormal(0.0F, 1.0F, 0.0F);
         this.renderFaceYPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 1, par2));
         var4.draw();
         if (var5 && this.useInventoryTint) {
            GL11.glColor4f(par3, par3, par3, 1.0F);
         }

         var4.startDrawingQuads();
         var4.setNormal(0.0F, 0.0F, -1.0F);
         this.renderFaceZNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 2, par2));
         var4.draw();
         var4.startDrawingQuads();
         var4.setNormal(0.0F, 0.0F, 1.0F);
         this.renderFaceZPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 3, par2));
         var4.draw();
         var4.startDrawingQuads();
         var4.setNormal(-1.0F, 0.0F, 0.0F);
         this.renderFaceXNeg(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 4, par2));
         var4.draw();
         var4.startDrawingQuads();
         var4.setNormal(1.0F, 0.0F, 0.0F);
         this.renderFaceXPos(par1Block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(par1Block, 5, par2));
         var4.draw();
         GL11.glTranslatef(0.5F, 0.5F, 0.5F);
      }

   }

   public static boolean renderItemIn3d(int par0) {
      switch (par0)
      {
         case 0: return true;
         case 31: return true ;
         case 39: return true ;
         case 13: return true ;
         case 10: return true ;
         case 11: return true ;
         case 27: return true ;
         case 22: return true ;
         case 21: return true ;
         case 16: return true ;
         case 26: return true ;
         case 32: return true ;
         case 34: return true ;
         case 35: return true ;
         default: return FMLRenderAccessLibrary.renderItemAsFull3DBlock(par0);
      }
   }

   public Icon getBlockIcon(Block par1Block, IBlockAccess par2IBlockAccess, int par3, int par4, int par5, int par6) {
      Icon par1Icon = par1Block.getBlockTexture(par2IBlockAccess, par3, par4, par5, par6);
      return (Icon)(par1Icon != null ? par1Icon : ((TextureMap)Minecraft.theMinecraft.getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno"));
   }

   public Icon getBlockIconFromSideAndMetadata(Block par1Block, int par2, int par3) {
      Icon par1Icon = par1Block.getIcon(par2, par3);
      return (Icon)(par1Icon != null ? par1Icon : ((TextureMap)Minecraft.theMinecraft.getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno"));
   }

   public Icon getBlockIconFromSide(Block par1Block, int par2) {
      Icon par1Icon = par1Block.getBlockTextureFromSide(par2);
      return (Icon)(par1Icon != null ? par1Icon : ((TextureMap)Minecraft.theMinecraft.getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno"));
   }

   public Icon getBlockIcon(Block par1Block) {
      return this.getIconSafe(par1Block.getBlockTextureFromSide(1));
   }

   public Icon getIconSafe(Icon par1Icon) {
      return (Icon)(par1Icon != null ? par1Icon : ((TextureMap)Minecraft.theMinecraft.getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno"));
   }
}
