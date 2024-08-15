package net.minecraft.client.renderer;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BitHelper;
import net.minecraft.block.Block;
import net.minecraft.block.IBlockWithPartner;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.client.ClientProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityCloudFX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.client.particle.EntityEnchantmentTableParticleFX;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFireworkSparkFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityFootStepFX;
import net.minecraft.client.particle.EntityHeartFX;
import net.minecraft.client.particle.EntityHugeExplodeFX;
import net.minecraft.client.particle.EntityLargeExplodeFX;
import net.minecraft.client.particle.EntityLavaFX;
import net.minecraft.client.particle.EntityNoteFX;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySnowShovelFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.particle.EntitySplashFX;
import net.minecraft.client.particle.EntitySuspendFX;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDepthSuspendParticle;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityRepairFX;
import net.minecraft.entity.EntitySacredFX;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.network.packet.Packet87SetDespawnCounters;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.IWorldAccess;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;

public final class RenderGlobal implements IWorldAccess {
   private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
   private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
   private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation moon_halo_256 = new ResourceLocation("textures/environment/halo_256.png");
   private static final ResourceLocation moon_ring_128 = new ResourceLocation("textures/environment/ring_128.png");
   public List tileEntities = new ArrayList();
   private WorldClient theWorld;
   private final TextureManager renderEngine;
   private List worldRenderersToUpdate = new ArrayList();
   private WorldRenderer[] sortedWorldRenderers;
   public WorldRenderer[] worldRenderers;
   private int renderChunksWide;
   private int renderChunksTall;
   private int renderChunksDeep;
   private int glRenderListBase;
   private Minecraft mc;
   private RenderBlocks globalRenderBlocks;
   private IntBuffer glOcclusionQueryBase;
   private boolean occlusionEnabled;
   private int cloudTickCounter;
   private int starGLCallList;
   private int glSkyList;
   private int glSkyList2;
   private int minBlockX;
   private int minBlockY;
   private int minBlockZ;
   private int maxBlockX;
   private int maxBlockY;
   private int maxBlockZ;
   private Map damagedBlocks = new HashMap();
   private Icon[] destroyBlockIcons;
   private int renderDistance = -1;
   private int renderEntitiesStartupCounter = 2;
   private int countEntitiesTotal;
   private int countEntitiesRendered;
   private int countEntitiesHidden;
   IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);
   private int renderersLoaded;
   private int renderersBeingClipped;
   private int renderersBeingOccluded;
   private int renderersBeingRendered;
   private int renderersSkippingRenderPass;
   private int dummyRenderInt;
   private int worldRenderersCheckIndex;
   private List glRenderLists = new ArrayList();
   private RenderList[] allRenderLists = new RenderList[]{new RenderList(), new RenderList(), new RenderList(), new RenderList()};
   double prevSortX = -9999.0;
   double prevSortY = -9999.0;
   double prevSortZ = -9999.0;
   int frustumCheckOffset;
   private long time_to_send_next_packet87 = System.currentTimeMillis();
   private double[] x = new double[4];
   private double[] y = new double[4];
   private double[] z = new double[4];
   private double[] u = new double[4];
   private double[] v = new double[4];
   private float[] r = new float[4];
   private float[] g = new float[4];
   private float[] b = new float[4];
   private int[] brightness = new int[4];
   public double last_cloud_compile_x;
   public double last_cloud_compile_y;
   public double last_cloud_compile_z;
   private boolean last_cloud_compile_has_cloud_tops;
   private boolean last_cloud_compile_has_cloud_bottoms;
   private int clouds_recompiling_step = 0;
   private int[] clouds_display_list = new int[5];
   private boolean[] clouds_display_list_initialized = new boolean[5];
   public static final int SFX_2001_WAS_SILK_HARVESTED = BitHelper.getBitValue(16);
   public static final int SFX_2001_SUPPRESS_SOUND = BitHelper.getBitValue(17);
   public static final int SFX_2001_WAS_NOT_LEGAL = BitHelper.getBitValue(18);
   public static final int SFX_2001_FOR_AI_BREAK_DOOR = BitHelper.getBitValue(19);
   public static final int SFX_2001_FOR_PARTNER_BLOCK = BitHelper.getBitValue(20);
   public boolean include_all_world_renderers_in_next_sorting;

   public RenderGlobal(Minecraft par1Minecraft) {
      this.mc = par1Minecraft;
      this.renderEngine = par1Minecraft.getTextureManager();
      byte var2 = 34;
      byte var3 = 32;
      this.glRenderListBase = GLAllocation.generateDisplayLists(var2 * var2 * var3 * 3);
      this.occlusionEnabled = OpenGlCapsChecker.checkARBOcclusion();
      if (this.occlusionEnabled) {
         this.occlusionResult.clear();
         this.glOcclusionQueryBase = GLAllocation.createDirectIntBuffer(var2 * var2 * var3);
         this.glOcclusionQueryBase.clear();
         this.glOcclusionQueryBase.position(0);
         this.glOcclusionQueryBase.limit(var2 * var2 * var3);
         ARBOcclusionQuery.glGenQueriesARB(this.glOcclusionQueryBase);
      }

      this.starGLCallList = GLAllocation.generateDisplayLists(3);
      GL11.glPushMatrix();
      GL11.glNewList(this.starGLCallList, 4864);
      this.renderStars();
      GL11.glEndList();
      GL11.glPopMatrix();
      Tessellator var4 = Tessellator.instance;
      this.glSkyList = this.starGLCallList + 1;
      GL11.glNewList(this.glSkyList, 4864);
      byte var6 = 64;
      int var7 = 256 / var6 + 2;
      float var5 = 16.0F;

      int var8;
      int var9;
      for(var8 = -var6 * var7; var8 <= var6 * var7; var8 += var6) {
         for(var9 = -var6 * var7; var9 <= var6 * var7; var9 += var6) {
            var4.startDrawingQuads();
            var4.addVertex((double)(var8 + 0), (double)var5, (double)(var9 + 0));
            var4.addVertex((double)(var8 + var6), (double)var5, (double)(var9 + 0));
            var4.addVertex((double)(var8 + var6), (double)var5, (double)(var9 + var6));
            var4.addVertex((double)(var8 + 0), (double)var5, (double)(var9 + var6));
            var4.draw();
         }
      }

      GL11.glEndList();
      this.glSkyList2 = this.starGLCallList + 2;
      GL11.glNewList(this.glSkyList2, 4864);
      var5 = -16.0F;
      var4.startDrawingQuads();

      for(var8 = -var6 * var7; var8 <= var6 * var7; var8 += var6) {
         for(var9 = -var6 * var7; var9 <= var6 * var7; var9 += var6) {
            var4.addVertex((double)(var8 + var6), (double)var5, (double)(var9 + 0));
            var4.addVertex((double)(var8 + 0), (double)var5, (double)(var9 + 0));
            var4.addVertex((double)(var8 + 0), (double)var5, (double)(var9 + var6));
            var4.addVertex((double)(var8 + var6), (double)var5, (double)(var9 + var6));
         }
      }

      var4.draw();
      GL11.glEndList();
   }

   private void renderStars() {
      Random var1 = new Random(10842L);
      Tessellator var2 = Tessellator.instance;
      var2.startDrawingQuads();

      for(int var3 = 0; var3 < 1500; ++var3) {
         double var4 = (double)(var1.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(var1.nextFloat() * 2.0F - 1.0F);
         double var8 = (double)(var1.nextFloat() * 2.0F - 1.0F);
         double var10 = (double)(0.15F + var1.nextFloat() * 0.1F);
         double var12 = var4 * var4 + var6 * var6 + var8 * var8;
         if (var12 < 1.0 && var12 > 0.01) {
            var12 = 1.0 / Math.sqrt(var12);
            var4 *= var12;
            var6 *= var12;
            var8 *= var12;
            double var14 = var4 * 100.0;
            double var16 = var6 * 100.0;
            double var18 = var8 * 100.0;
            double var20 = Math.atan2(var4, var8);
            double var22 = Math.sin(var20);
            double var24 = Math.cos(var20);
            double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
            double var28 = Math.sin(var26);
            double var30 = Math.cos(var26);
            double var32 = var1.nextDouble() * Math.PI * 2.0;
            double var34 = Math.sin(var32);
            double var36 = Math.cos(var32);

            for(int var38 = 0; var38 < 4; ++var38) {
               double var39 = 0.0;
               double var41 = (double)((var38 & 2) - 1) * var10;
               double var43 = (double)((var38 + 1 & 2) - 1) * var10;
               double var47 = var41 * var36 - var43 * var34;
               double var49 = var43 * var36 + var41 * var34;
               double var53 = var47 * var28 + var39 * var30;
               double var55 = var39 * var28 - var47 * var30;
               double var57 = var55 * var22 - var49 * var24;
               double var61 = var49 * var22 + var55 * var24;
               var2.addVertex(var14 + var57, var16 + var53, var18 + var61);
            }
         }
      }

      var2.draw();
   }

   public void setWorldAndLoadRenderers(WorldClient par1WorldClient) {
      if (this.theWorld != null) {
         this.theWorld.removeWorldAccess(this);
      }

      this.prevSortX = -9999.0;
      this.prevSortY = -9999.0;
      this.prevSortZ = -9999.0;
      RenderManager.instance.set(par1WorldClient);
      this.theWorld = par1WorldClient;
      this.globalRenderBlocks = new RenderBlocks(par1WorldClient);
      if (par1WorldClient != null) {
         par1WorldClient.addWorldAccess(this);
         this.loadRenderers();
      }

   }

   public void markAllRenderersDirty() {
      for(int i = 0; i < this.worldRenderers.length; ++i) {
         this.worldRenderers[i].needsUpdate = true;
      }

   }

   public void markAllRenderersUninitialized() {
      for(int i = 0; i < this.worldRenderers.length; ++i) {
         WorldRenderer world_renderer = this.worldRenderers[i];
         world_renderer.isInitialized = false;
         world_renderer.needsUpdate = true;
         if (!this.worldRenderersToUpdate.contains(world_renderer)) {
            this.worldRenderersToUpdate.add(world_renderer);
         }
      }

   }

   public WorldRenderer getWorldRendererFor(int x, int y, int z, Entity entity_for_caching_index) {
      for(int i = 0; i < this.worldRenderers.length; ++i) {
         if (this.worldRenderers[i].isRenderingCoords(x, y, z)) {
            if (entity_for_caching_index != null) {
               entity_for_caching_index.index_of_last_applicable_world_renderer = i;
            }

            return this.worldRenderers[i];
         }
      }

      return null;
   }

   public WorldRenderer getWorldRendererFor(int x, int y, int z) {
      return this.getWorldRendererFor(x, y, z, (Entity)null);
   }

   public void loadRenderers() {
      if (this.theWorld != null) {
         Block.leaves.setGraphicsLevel(this.mc.gameSettings.isFancyGraphicsEnabled());
         this.renderDistance = this.mc.gameSettings.getRenderDistance();
         int var1;
         if (this.worldRenderers != null) {
            for(var1 = 0; var1 < this.worldRenderers.length; ++var1) {
               this.worldRenderers[var1].stopRendering();
            }
         }

         var1 = 64 << 3 - this.renderDistance;
         if (var1 > 400) {
            var1 = 400;
         }

         this.renderChunksWide = var1 / 16 + 1;
         this.renderChunksTall = 16;
         this.renderChunksDeep = var1 / 16 + 1;
         this.worldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
         this.sortedWorldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
         int var2 = 0;
         int var3 = 0;
         this.minBlockX = 0;
         this.minBlockY = 0;
         this.minBlockZ = 0;
         this.maxBlockX = this.renderChunksWide;
         this.maxBlockY = this.renderChunksTall;
         this.maxBlockZ = this.renderChunksDeep;

         int var4;
         for(var4 = 0; var4 < this.worldRenderersToUpdate.size(); ++var4) {
            ((WorldRenderer)this.worldRenderersToUpdate.get(var4)).needsUpdate = false;
         }

         this.worldRenderersToUpdate.clear();
         this.tileEntities.clear();

         for(var4 = 0; var4 < this.renderChunksWide; ++var4) {
            for(int var5 = 0; var5 < this.renderChunksTall; ++var5) {
               for(int var6 = 0; var6 < this.renderChunksDeep; ++var6) {
                  this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4] = new WorldRenderer(this.theWorld, this.tileEntities, var4 * 16, var5 * 16, var6 * 16, this.glRenderListBase + var2);
                  if (this.occlusionEnabled) {
                     this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].glOcclusionQuery = this.glOcclusionQueryBase.get(var3);
                  }

                  this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isWaitingOnOcclusionQuery = false;
                  this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isVisible = true;
                  this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isInFrustum = true;
                  this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].chunkIndex = var3++;
                  this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].markDirty();
                  this.sortedWorldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4] = this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4];
                  this.worldRenderersToUpdate.add(this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4]);
                  var2 += 3;
               }
            }
         }

         if (this.theWorld != null) {
            EntityLivingBase var7 = this.mc.renderViewEntity;
            if (var7 != null) {
               this.markRenderersForNewPosition(MathHelper.floor_double(var7.posX), MathHelper.floor_double(var7.posY), MathHelper.floor_double(var7.posZ));
               Arrays.sort(this.sortedWorldRenderers, new EntitySorter(var7));
            }
         }

         this.renderEntitiesStartupCounter = 2;
      }

   }

   public void renderEntities(Vec3 par1Vec3, ICamera par2ICamera, float par3) {
      if (this.renderEntitiesStartupCounter > 0) {
         --this.renderEntitiesStartupCounter;
      } else {
         this.theWorld.theProfiler.startSection("prepare");
         TileEntityRenderer.instance.cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRenderer, this.mc.renderViewEntity, par3);
         RaycastCollision rc = ((EntityPlayer)this.mc.renderViewEntity).getSelectedObject(par3, false);
         EntityLiving pointed_entity_living = rc == null ? null : (rc.getEntityHit() instanceof EntityLiving ? (EntityLiving)rc.getEntityHit() : null);
         RenderManager.instance.cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRenderer, this.mc.renderViewEntity, pointed_entity_living, this.mc.gameSettings, par3);
         this.countEntitiesTotal = 0;
         this.countEntitiesRendered = 0;
         this.countEntitiesHidden = 0;
         EntityLivingBase var4 = this.mc.renderViewEntity;
         RenderManager.renderPosX = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * (double)par3;
         RenderManager.renderPosY = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * (double)par3;
         RenderManager.renderPosZ = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * (double)par3;
         TileEntityRenderer.staticPlayerX = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * (double)par3;
         TileEntityRenderer.staticPlayerY = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * (double)par3;
         TileEntityRenderer.staticPlayerZ = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * (double)par3;
         this.mc.entityRenderer.enableLightmap((double)par3);
         this.theWorld.theProfiler.endStartSection("global");
         List var5 = this.theWorld.getLoadedEntityList();
         this.countEntitiesTotal = var5.size();

         int var6;
         Entity var7;
         for(var6 = 0; var6 < this.theWorld.weatherEffects.size(); ++var6) {
            var7 = (Entity)this.theWorld.weatherEffects.get(var6);
            ++this.countEntitiesRendered;
            if (var7.isInRangeToRenderVec3D(par1Vec3)) {
               RenderManager.instance.renderEntity(var7, par3);
            }
         }

         this.theWorld.theProfiler.endStartSection("entities");
         Packet87SetDespawnCounters packet87 = System.currentTimeMillis() >= this.time_to_send_next_packet87 ? new Packet87SetDespawnCounters() : null;

         for(var6 = 0; var6 < var5.size(); ++var6) {
            var7 = (Entity)var5.get(var6);
            boolean render_override = var7 != this.mc.thePlayer && var7.getDistanceSqToEntity(this.mc.thePlayer) < 16.0;
            if (var7 instanceof EntityDragon || var7 instanceof EntityDragonPart) {
               render_override = true;
            }

            boolean var8 = render_override ? true : var7.isInRangeToRenderVec3D(par1Vec3) && (var7.ignoreFrustumCheck || par2ICamera.isBoundingBoxInFrustum(var7.boundingBox) || var7.riddenByEntity == this.mc.thePlayer || var7 instanceof EntityPlayer && var7 != this.mc.thePlayer);
            if (!var8 && var7 instanceof EntityLiving) {
               EntityLiving var9 = (EntityLiving)var7;
               if (var9.getLeashed() && var9.getLeashedToEntity() != null) {
                  Entity var10 = var9.getLeashedToEntity();
                  var8 = par2ICamera.isBoundingBoxInFrustum(var10.boundingBox);
               }
            }

            if (var8 && (var7 != this.mc.renderViewEntity || this.mc.gameSettings.thirdPersonView != 0 || this.mc.renderViewEntity.inBed()) && (this.theWorld.blockExists(MathHelper.floor_double(var7.posX), 0, MathHelper.floor_double(var7.posZ)) || render_override)) {
               ++this.countEntitiesRendered;
               RenderManager.instance.renderEntity(var7, par3);
               if (packet87 != null && var7 instanceof EntityLiving && var7.getAsEntityLiving().isConsideredInViewOfPlayerForDespawningPurposes(Minecraft.getClientPlayer())) {
                  packet87.add(var7.entityId, (short)(var7 instanceof EntityWaterMob ? -9600 : -400));
               }
            }
         }

         if (packet87 != null) {
            this.time_to_send_next_packet87 = System.currentTimeMillis() + 1000L;
            if (packet87.entries > 0) {
               Minecraft.getClientPlayer().sendQueue.addToSendQueue(packet87);
            }
         }

         this.theWorld.theProfiler.endStartSection("tileentities");
         RenderHelper.enableStandardItemLighting();

         for(var6 = 0; var6 < this.tileEntities.size(); ++var6) {
            TileEntityRenderer.instance.renderTileEntity((TileEntity)this.tileEntities.get(var6), par3);
         }

         this.mc.entityRenderer.disableLightmap((double)par3);
         this.theWorld.theProfiler.endSection();
      }

   }

   public String getDebugInfoRenders() {
      return "C: " + this.renderersBeingRendered + "/" + this.renderersLoaded + ". F: " + this.renderersBeingClipped + ", O: " + this.renderersBeingOccluded + ", E: " + this.renderersSkippingRenderPass;
   }

   public String getDebugInfoEntities() {
      return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ". B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered);
   }

   private void markRenderersForNewPosition(int par1, int par2, int par3) {
      par1 -= 8;
      par2 -= 8;
      par3 -= 8;
      this.minBlockX = Integer.MAX_VALUE;
      this.minBlockY = Integer.MAX_VALUE;
      this.minBlockZ = Integer.MAX_VALUE;
      this.maxBlockX = Integer.MIN_VALUE;
      this.maxBlockY = Integer.MIN_VALUE;
      this.maxBlockZ = Integer.MIN_VALUE;
      int var4 = this.renderChunksWide * 16;
      int var5 = var4 / 2;

      for(int var6 = 0; var6 < this.renderChunksWide; ++var6) {
         int var7 = var6 * 16;
         int var8 = var7 + var5 - par1;
         if (var8 < 0) {
            var8 -= var4 - 1;
         }

         var8 /= var4;
         var7 -= var8 * var4;
         if (var7 < this.minBlockX) {
            this.minBlockX = var7;
         }

         if (var7 > this.maxBlockX) {
            this.maxBlockX = var7;
         }

         for(int var9 = 0; var9 < this.renderChunksDeep; ++var9) {
            int var10 = var9 * 16;
            int var11 = var10 + var5 - par3;
            if (var11 < 0) {
               var11 -= var4 - 1;
            }

            var11 /= var4;
            var10 -= var11 * var4;
            if (var10 < this.minBlockZ) {
               this.minBlockZ = var10;
            }

            if (var10 > this.maxBlockZ) {
               this.maxBlockZ = var10;
            }

            for(int var12 = 0; var12 < this.renderChunksTall; ++var12) {
               int var13 = var12 * 16;
               if (var13 < this.minBlockY) {
                  this.minBlockY = var13;
               }

               if (var13 > this.maxBlockY) {
                  this.maxBlockY = var13;
               }

               WorldRenderer var14 = this.worldRenderers[(var9 * this.renderChunksTall + var12) * this.renderChunksWide + var6];
               boolean var15 = var14.needsUpdate;
               var14.setPosition(var7, var13, var10);
               if (!var15 && var14.needsUpdate) {
                  this.worldRenderersToUpdate.add(var14);
               }
            }
         }
      }

   }

   public int sortAndRender(EntityLivingBase par1EntityLivingBase, int par2, double par3) {
      this.theWorld.theProfiler.startSection("sortchunks");

      for(int var5 = 0; var5 < (this.include_all_world_renderers_in_next_sorting ? this.worldRenderers.length : 10); ++var5) {
         this.worldRenderersCheckIndex = (this.worldRenderersCheckIndex + 1) % this.worldRenderers.length;
         WorldRenderer var6 = this.worldRenderers[this.worldRenderersCheckIndex];
         if (var6.needsUpdate && !this.worldRenderersToUpdate.contains(var6)) {
            this.worldRenderersToUpdate.add(var6);
         }
      }

      if (this.include_all_world_renderers_in_next_sorting) {
         this.include_all_world_renderers_in_next_sorting = false;
      }

      if (this.mc.gameSettings.getRenderDistance() != this.renderDistance) {
         this.loadRenderers();
      }

      if (par2 == 0) {
         this.renderersLoaded = 0;
         this.dummyRenderInt = 0;
         this.renderersBeingClipped = 0;
         this.renderersBeingOccluded = 0;
         this.renderersBeingRendered = 0;
         this.renderersSkippingRenderPass = 0;
      }

      double var33 = par1EntityLivingBase.lastTickPosX + (par1EntityLivingBase.posX - par1EntityLivingBase.lastTickPosX) * par3;
      double var7 = par1EntityLivingBase.lastTickPosY + (par1EntityLivingBase.posY - par1EntityLivingBase.lastTickPosY) * par3;
      double var9 = par1EntityLivingBase.lastTickPosZ + (par1EntityLivingBase.posZ - par1EntityLivingBase.lastTickPosZ) * par3;
      double var11 = par1EntityLivingBase.posX - this.prevSortX;
      double var13 = par1EntityLivingBase.posY - this.prevSortY;
      double var15 = par1EntityLivingBase.posZ - this.prevSortZ;
      if (var11 * var11 + var13 * var13 + var15 * var15 > 16.0) {
         this.prevSortX = par1EntityLivingBase.posX;
         this.prevSortY = par1EntityLivingBase.posY;
         this.prevSortZ = par1EntityLivingBase.posZ;
         this.markRenderersForNewPosition(MathHelper.floor_double(par1EntityLivingBase.posX), MathHelper.floor_double(par1EntityLivingBase.posY), MathHelper.floor_double(par1EntityLivingBase.posZ));
         Arrays.sort(this.sortedWorldRenderers, new EntitySorter(par1EntityLivingBase));
      }

      RenderHelper.disableStandardItemLighting();
      byte var17 = 0;
      int var34;
      if (this.occlusionEnabled && this.mc.gameSettings.advancedOpengl && !this.mc.gameSettings.anaglyph && par2 == 0) {
         byte var18 = 0;
         int var19 = 16;
         this.checkOcclusionQueryResult(var18, var19);

         int var35;
         for(var35 = var18; var35 < var19; ++var35) {
            this.sortedWorldRenderers[var35].isVisible = true;
         }

         this.theWorld.theProfiler.endStartSection("render");
         var34 = var17 + this.renderSortedRenderers(var18, var19, par2, par3);

         do {
            this.theWorld.theProfiler.endStartSection("occ");
            var35 = var19;
            var19 *= 2;
            if (var19 > this.sortedWorldRenderers.length) {
               var19 = this.sortedWorldRenderers.length;
            }

            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glDisable(3008);
            GL11.glDisable(2912);
            GL11.glColorMask(false, false, false, false);
            GL11.glDepthMask(false);
            this.theWorld.theProfiler.startSection("check");
            this.checkOcclusionQueryResult(var35, var19);
            this.theWorld.theProfiler.endSection();
            GL11.glPushMatrix();
            float var36 = 0.0F;
            float var21 = 0.0F;
            float var22 = 0.0F;

            for(int var23 = var35; var23 < var19; ++var23) {
               if (this.sortedWorldRenderers[var23].skipAllRenderPasses()) {
                  this.sortedWorldRenderers[var23].isInFrustum = false;
               } else {
                  if (!this.sortedWorldRenderers[var23].isInFrustum) {
                     this.sortedWorldRenderers[var23].isVisible = true;
                  }

                  if (this.sortedWorldRenderers[var23].isInFrustum && !this.sortedWorldRenderers[var23].isWaitingOnOcclusionQuery) {
                     float var24 = MathHelper.sqrt_float(this.sortedWorldRenderers[var23].distanceToEntitySquared(par1EntityLivingBase));
                     int var25 = (int)(1.0F + var24 / 128.0F);
                     if (this.cloudTickCounter % var25 == var23 % var25) {
                        WorldRenderer var26 = this.sortedWorldRenderers[var23];
                        float var27 = (float)((double)var26.posXMinus - var33);
                        float var28 = (float)((double)var26.posYMinus - var7);
                        float var29 = (float)((double)var26.posZMinus - var9);
                        float var30 = var27 - var36;
                        float var31 = var28 - var21;
                        float var32 = var29 - var22;
                        if (var30 != 0.0F || var31 != 0.0F || var32 != 0.0F) {
                           GL11.glTranslatef(var30, var31, var32);
                           var36 += var30;
                           var21 += var31;
                           var22 += var32;
                        }

                        this.theWorld.theProfiler.startSection("bb");
                        ARBOcclusionQuery.glBeginQueryARB(35092, this.sortedWorldRenderers[var23].glOcclusionQuery);
                        this.sortedWorldRenderers[var23].callOcclusionQueryList();
                        ARBOcclusionQuery.glEndQueryARB(35092);
                        this.theWorld.theProfiler.endSection();
                        this.sortedWorldRenderers[var23].isWaitingOnOcclusionQuery = true;
                     }
                  }
               }
            }

            GL11.glPopMatrix();
            if (this.mc.gameSettings.anaglyph) {
               if (EntityRenderer.anaglyphField == 0) {
                  GL11.glColorMask(false, true, true, true);
               } else {
                  GL11.glColorMask(true, false, false, true);
               }
            } else {
               GL11.glColorMask(true, true, true, true);
            }

            GL11.glDepthMask(true);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glEnable(2912);
            this.theWorld.theProfiler.endStartSection("render");
            var34 += this.renderSortedRenderers(var35, var19, par2, par3);
         } while(var19 < this.sortedWorldRenderers.length);
      } else {
         this.theWorld.theProfiler.endStartSection("render");
         var34 = var17 + this.renderSortedRenderers(0, this.sortedWorldRenderers.length, par2, par3);
      }

      this.theWorld.theProfiler.endSection();
      return var34;
   }

   private void checkOcclusionQueryResult(int par1, int par2) {
      for(int var3 = par1; var3 < par2; ++var3) {
         if (this.sortedWorldRenderers[var3].isWaitingOnOcclusionQuery) {
            this.occlusionResult.clear();
            ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[var3].glOcclusionQuery, 34919, this.occlusionResult);
            if (this.occlusionResult.get(0) != 0) {
               this.sortedWorldRenderers[var3].isWaitingOnOcclusionQuery = false;
               this.occlusionResult.clear();
               ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[var3].glOcclusionQuery, 34918, this.occlusionResult);
               this.sortedWorldRenderers[var3].isVisible = this.occlusionResult.get(0) != 0;
            }
         }
      }

   }

   private int renderSortedRenderers(int par1, int par2, int par3, double par4) {
      this.glRenderLists.clear();
      int var6 = 0;

      for(int var7 = par1; var7 < par2; ++var7) {
         if (par3 == 0) {
            ++this.renderersLoaded;
            if (this.sortedWorldRenderers[var7].skipRenderPass[par3]) {
               ++this.renderersSkippingRenderPass;
            } else if (!this.sortedWorldRenderers[var7].isInFrustum) {
               ++this.renderersBeingClipped;
            } else if (this.occlusionEnabled && !this.sortedWorldRenderers[var7].isVisible) {
               ++this.renderersBeingOccluded;
            } else {
               ++this.renderersBeingRendered;
            }
         }

         if (!this.sortedWorldRenderers[var7].skipRenderPass[par3] && this.sortedWorldRenderers[var7].isInFrustum && (!this.occlusionEnabled || this.sortedWorldRenderers[var7].isVisible)) {
            int var8 = this.sortedWorldRenderers[var7].getGLCallListForPass(par3);
            if (var8 >= 0) {
               this.glRenderLists.add(this.sortedWorldRenderers[var7]);
               ++var6;
            }
         }
      }

      EntityLivingBase var19 = this.mc.renderViewEntity;
      double var20 = var19.lastTickPosX + (var19.posX - var19.lastTickPosX) * par4;
      double var10 = var19.lastTickPosY + (var19.posY - var19.lastTickPosY) * par4;
      double var12 = var19.lastTickPosZ + (var19.posZ - var19.lastTickPosZ) * par4;
      int var14 = 0;

      int var15;
      for(var15 = 0; var15 < this.allRenderLists.length; ++var15) {
         this.allRenderLists[var15].func_78421_b();
      }

      for(var15 = 0; var15 < this.glRenderLists.size(); ++var15) {
         WorldRenderer var16 = (WorldRenderer)this.glRenderLists.get(var15);
         int var17 = -1;

         for(int var18 = 0; var18 < var14; ++var18) {
            if (this.allRenderLists[var18].func_78418_a(var16.posXMinus, var16.posYMinus, var16.posZMinus)) {
               var17 = var18;
            }
         }

         if (var17 < 0) {
            var17 = var14++;
            this.allRenderLists[var17].func_78422_a(var16.posXMinus, var16.posYMinus, var16.posZMinus, var20, var10, var12);
         }

         this.allRenderLists[var17].func_78420_a(var16.getGLCallListForPass(par3));
      }

      this.renderAllRenderLists(par3, par4);
      return var6;
   }

   public void renderAllRenderLists(int par1, double par2) {
      this.mc.entityRenderer.enableLightmap(par2);

      for(int var4 = 0; var4 < this.allRenderLists.length; ++var4) {
         this.allRenderLists[var4].func_78419_a();
      }

      this.mc.entityRenderer.disableLightmap(par2);
   }

   public void updateClouds() {
      ++this.cloudTickCounter;
      if (this.cloudTickCounter % 20 == 0) {
         Iterator var1 = this.damagedBlocks.values().iterator();

         while(var1.hasNext()) {
            DestroyBlockProgress var2 = (DestroyBlockProgress)var1.next();
            int var3 = var2.getCreationCloudUpdateTick();
            if (this.cloudTickCounter - var3 > 400) {
               var1.remove();
            }
         }
      }

   }

   public void renderSky(float par1) {
      GL11.glDisable(2896);
      if (this.mc.theWorld.provider.dimensionId == 1) {
         GL11.glDisable(2912);
         GL11.glDisable(3008);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         RenderHelper.disableStandardItemLighting();
         GL11.glDepthMask(false);
         this.renderEngine.bindTexture(locationEndSkyPng);
         Tessellator var21 = Tessellator.instance;

         for(int var22 = 0; var22 < 6; ++var22) {
            GL11.glPushMatrix();
            if (var22 == 1) {
               GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var22 == 2) {
               GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var22 == 3) {
               GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var22 == 4) {
               GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (var22 == 5) {
               GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            var21.startDrawingQuads();
            var21.setColorOpaque_I(2631720);
            var21.addVertexWithUV(-100.0, -100.0, -100.0, 0.0, 0.0);
            var21.addVertexWithUV(-100.0, -100.0, 100.0, 0.0, 16.0);
            var21.addVertexWithUV(100.0, -100.0, 100.0, 16.0, 16.0);
            var21.addVertexWithUV(100.0, -100.0, -100.0, 16.0, 0.0);
            var21.draw();
            GL11.glPopMatrix();
         }

         GL11.glDepthMask(true);
         GL11.glEnable(3553);
         GL11.glEnable(3008);
      } else if (this.mc.theWorld.provider.isSurfaceWorld()) {
         GL11.glDisable(3553);
         Vec3 var2 = this.theWorld.getSkyColor(this.mc.renderViewEntity, par1);
         float var3 = (float)var2.xCoord;
         float var4 = (float)var2.yCoord;
         float var5 = (float)var2.zCoord;
         float var8;
         if (this.mc.gameSettings.anaglyph) {
            float var6 = (var3 * 30.0F + var4 * 59.0F + var5 * 11.0F) / 100.0F;
            float var7 = (var3 * 30.0F + var4 * 70.0F) / 100.0F;
            var8 = (var3 * 30.0F + var5 * 70.0F) / 100.0F;
            var3 = var6;
            var4 = var7;
            var5 = var8;
         }

         GL11.glColor3f(var3, var4, var5);
         Tessellator var23 = Tessellator.instance;
         GL11.glDepthMask(false);
         GL11.glEnable(2912);
         GL11.glColor3f(var3, var4, var5);
         GL11.glCallList(this.glSkyList);
         GL11.glDisable(2912);
         GL11.glDisable(3008);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         RenderHelper.disableStandardItemLighting();
         float[] var24 = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(par1), par1);
         float var9;
         float var10;
         float var11;
         float var12;
         int var26;
         int var27;
         float var14;
         float var15;
         if (var24 != null) {
            GL11.glDisable(3553);
            GL11.glShadeModel(7425);
            GL11.glPushMatrix();
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(MathHelper.sin(this.theWorld.getCelestialAngleRadians(par1)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            var8 = var24[0];
            var9 = var24[1];
            var10 = var24[2];
            float var13;
            if (this.mc.gameSettings.anaglyph) {
               var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
               var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
               var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
               var8 = var11;
               var9 = var12;
               var10 = var13;
            }

            var23.startDrawing(6);
            var23.setColorRGBA_F(var8, var9, var10, var24[3]);
            var23.addVertex(0.0, 100.0, 0.0);
            var26 = 16;
            var23.setColorRGBA_F(var24[0], var24[1], var24[2], 0.0F);

            for(var27 = 0; var27 <= var26; ++var27) {
               var13 = (float)var27 * 3.1415927F * 2.0F / (float)var26;
               var14 = MathHelper.sin(var13);
               var15 = MathHelper.cos(var13);
               var23.addVertex((double)(var14 * 120.0F), (double)(var15 * 120.0F), (double)(-var15 * 40.0F * var24[3]));
            }

            var23.draw();
            GL11.glPopMatrix();
            GL11.glShadeModel(7424);
         }

         GL11.glEnable(3553);
         GL11.glBlendFunc(770, 1);
         GL11.glPushMatrix();
         var8 = 1.0F - this.theWorld.getRainStrength(par1);
         var9 = 0.0F;
         var10 = 0.0F;
         var11 = 0.0F;
         GL11.glColor4f(1.0F, 1.0F, 1.0F, var8);
         GL11.glTranslatef(var9, var10, var11);
         GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(this.theWorld.getCelestialAngle(par1) * 360.0F, 1.0F, 0.0F, 0.0F);
         var12 = 30.0F;
         this.renderEngine.bindTexture(locationSunPng);
         var23.startDrawingQuads();
         var23.addVertexWithUV((double)(-var12), 100.0, (double)(-var12), 0.0, 0.0);
         var23.addVertexWithUV((double)var12, 100.0, (double)(-var12), 1.0, 0.0);
         var23.addVertexWithUV((double)var12, 100.0, (double)var12, 1.0, 1.0);
         var23.addVertexWithUV((double)(-var12), 100.0, (double)var12, 0.0, 1.0);
         var23.draw();
         if (this.theWorld.isBloodMoon(false)) {
            GL11.glColor4f(0.6F, 0.2F, 0.1F, var8);
         } else if (this.theWorld.isHarvestMoon24HourPeriod()) {
            GL11.glColor4f(1.0F, 0.8F, 0.45F, var8);
         } else if (this.theWorld.isBlueMoon(false)) {
            GL11.glColor4f(0.66F, 0.74F, 1.0F, var8);
         } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, var8);
         }

         var12 = 20.0F;
         this.renderEngine.bindTexture(locationMoonPhasesPng);
         int var28 = this.theWorld.getMoonPhase();
         var26 = var28 % 4;
         var27 = var28 / 4 % 2;
         var14 = (float)(var26 + 0) / 4.0F;
         var15 = (float)(var27 + 0) / 2.0F;
         float var18 = (float)(var26 + 1) / 4.0F;
         float var19 = (float)(var27 + 1) / 2.0F;
         var23.startDrawingQuads();
         var23.addVertexWithUV((double)(-var12), -100.0, (double)var12, (double)var18, (double)var19);
         var23.addVertexWithUV((double)var12, -100.0, (double)var12, (double)var14, (double)var19);
         var23.addVertexWithUV((double)var12, -100.0, (double)(-var12), (double)var14, (double)var15);
         var23.addVertexWithUV((double)(-var12), -100.0, (double)(-var12), (double)var18, (double)var15);
         var23.draw();
         float alpha_factor;
         float start_fading_in_at;
         float stop_fading_in_at;
         float start_fading_out_at;
         if (!this.theWorld.isBloodMoon24HourPeriod() && !this.theWorld.isBlueMoon24HourPeriod()) {
            if (this.theWorld.isMoonDog24HourPeriod()) {
               alpha_factor = (1.0F - this.theWorld.getRainStrength(par1)) * 0.5F;
               start_fading_in_at = 0.18F;
               stop_fading_in_at = 0.28F;
               start_fading_out_at = 0.6F;
               float stop_fading_out_at = 0.785F;
               float celestial_angle = this.theWorld.getCelestialAngle(par1);
               if (!(celestial_angle < start_fading_in_at) && !(celestial_angle > stop_fading_out_at)) {
                  if (celestial_angle < stop_fading_in_at) {
                     alpha_factor *= (celestial_angle - start_fading_in_at) / (stop_fading_in_at - start_fading_in_at);
                  } else if (celestial_angle > start_fading_out_at) {
                     alpha_factor *= 1.0F - (celestial_angle - start_fading_out_at) / (stop_fading_out_at - start_fading_out_at);
                  }
               } else {
                  alpha_factor = 0.0F;
               }

               if (alpha_factor > 0.0F) {
                  GL11.glColor4f(0.4975F, 0.6275F, 0.85F, alpha_factor);
                  this.renderEngine.bindTexture(moon_ring_128);
                  float ring_size = 80.0F;
                  var23.startDrawingQuads();
                  var23.addVertexWithUV((double)(-ring_size), -100.0, (double)ring_size, 0.0, 0.0);
                  var23.addVertexWithUV((double)ring_size, -100.0, (double)ring_size, 1.0, 0.0);
                  var23.addVertexWithUV((double)ring_size, -100.0, (double)(-ring_size), 1.0, 1.0);
                  var23.addVertexWithUV((double)(-ring_size), -100.0, (double)(-ring_size), 0.0, 1.0);
                  var23.draw();
               }
            }
         } else {
            alpha_factor = 0.25F;
            start_fading_in_at = this.theWorld.getCelestialAngle(par1);
            stop_fading_in_at = 0.77F;
            if (start_fading_in_at > stop_fading_in_at) {
               alpha_factor *= 1.0F - (start_fading_in_at - stop_fading_in_at) / (0.785F - stop_fading_in_at);
            }

            if (this.theWorld.isBloodMoon24HourPeriod()) {
               GL11.glColor4f(0.6F, 0.2F, 0.1F, var8 * alpha_factor);
            } else {
               GL11.glColor4f(0.33F, 0.37F, 0.8F, var8 * alpha_factor);
            }

            this.renderEngine.bindTexture(moon_halo_256);
            start_fading_out_at = 160.0F;
            var23.startDrawingQuads();
            var23.addVertexWithUV((double)(-start_fading_out_at), -100.0, (double)start_fading_out_at, 0.0, 0.0);
            var23.addVertexWithUV((double)start_fading_out_at, -100.0, (double)start_fading_out_at, 1.0, 0.0);
            var23.addVertexWithUV((double)start_fading_out_at, -100.0, (double)(-start_fading_out_at), 1.0, 1.0);
            var23.addVertexWithUV((double)(-start_fading_out_at), -100.0, (double)(-start_fading_out_at), 0.0, 1.0);
            var23.draw();
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, var8);
         GL11.glDisable(3553);
         alpha_factor = this.theWorld.getStarBrightness(par1) * var8;
         if (alpha_factor > 0.0F) {
            GL11.glColor4f(alpha_factor, alpha_factor, alpha_factor, alpha_factor);
            GL11.glCallList(this.starGLCallList);
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glDisable(3042);
         GL11.glEnable(3008);
         GL11.glEnable(2912);
         GL11.glPopMatrix();
         GL11.glDisable(3553);
         GL11.glColor3f(0.0F, 0.0F, 0.0F);
         double var25 = this.mc.thePlayer.getPosition(par1).yCoord - this.theWorld.getHorizon();
         if (var25 < 0.0) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 12.0F, 0.0F);
            GL11.glCallList(this.glSkyList2);
            GL11.glPopMatrix();
            var10 = 1.0F;
            var11 = -((float)(var25 + 65.0));
            var12 = -var10;
            var23.startDrawingQuads();
            var23.setColorRGBA_I(0, 255);
            var23.addVertex((double)(-var10), (double)var11, (double)var10);
            var23.addVertex((double)var10, (double)var11, (double)var10);
            var23.addVertex((double)var10, (double)var12, (double)var10);
            var23.addVertex((double)(-var10), (double)var12, (double)var10);
            var23.addVertex((double)(-var10), (double)var12, (double)(-var10));
            var23.addVertex((double)var10, (double)var12, (double)(-var10));
            var23.addVertex((double)var10, (double)var11, (double)(-var10));
            var23.addVertex((double)(-var10), (double)var11, (double)(-var10));
            var23.addVertex((double)var10, (double)var12, (double)(-var10));
            var23.addVertex((double)var10, (double)var12, (double)var10);
            var23.addVertex((double)var10, (double)var11, (double)var10);
            var23.addVertex((double)var10, (double)var11, (double)(-var10));
            var23.addVertex((double)(-var10), (double)var11, (double)(-var10));
            var23.addVertex((double)(-var10), (double)var11, (double)var10);
            var23.addVertex((double)(-var10), (double)var12, (double)var10);
            var23.addVertex((double)(-var10), (double)var12, (double)(-var10));
            var23.addVertex((double)(-var10), (double)var12, (double)(-var10));
            var23.addVertex((double)(-var10), (double)var12, (double)var10);
            var23.addVertex((double)var10, (double)var12, (double)var10);
            var23.addVertex((double)var10, (double)var12, (double)(-var10));
            var23.draw();
         }

         if (this.theWorld.provider.isSkyColored()) {
            GL11.glColor3f(var3 * 0.2F + 0.04F, var4 * 0.2F + 0.04F, var5 * 0.6F + 0.1F);
         } else {
            GL11.glColor3f(var3, var4, var5);
         }

         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, -((float)(var25 - 16.0)), 0.0F);
         GL11.glCallList(this.glSkyList2);
         GL11.glPopMatrix();
         GL11.glEnable(3553);
         GL11.glDepthMask(true);
      }

   }

   public void compileCloudsFancy(float par1, double var8, double var2, double var10) {
      GL11.glEnable(2884);
      GL11.glDisable(2912);
      GL11.glDisable(3008);
      GL11.glDisable(3553);
      boolean player_can_see_cloud_tops = this.canViewerSeeCloudTopsFancy(var2);
      boolean player_can_see_cloud_bottoms = this.canViewerSeeCloudBottomsFancy(var2);
      this.last_cloud_compile_x = var8;
      this.last_cloud_compile_y = var2;
      this.last_cloud_compile_z = var10;
      this.last_cloud_compile_has_cloud_tops = player_can_see_cloud_tops;
      this.last_cloud_compile_has_cloud_bottoms = player_can_see_cloud_bottoms;

      for(this.clouds_recompiling_step = 0; this.clouds_recompiling_step < this.clouds_display_list.length; ++this.clouds_recompiling_step) {
         if ((this.clouds_recompiling_step != 1 || player_can_see_cloud_tops) && (this.clouds_recompiling_step != 2 || player_can_see_cloud_bottoms)) {
            if (this.clouds_display_list_initialized[this.clouds_recompiling_step]) {
               GL11.glDeleteLists(this.clouds_display_list[this.clouds_recompiling_step], 1);
            }

            this.clouds_display_list[this.clouds_recompiling_step] = GL11.glGenLists(1);
            GL11.glNewList(this.clouds_display_list[this.clouds_recompiling_step], 4864);
            this.renderCloudSide(par1, this.clouds_recompiling_step - 1, this.last_cloud_compile_x, this.last_cloud_compile_z, player_can_see_cloud_tops, player_can_see_cloud_bottoms);
            GL11.glEndList();
            this.clouds_display_list_initialized[this.clouds_recompiling_step] = true;
         }
      }

      GL11.glEnable(2912);
      GL11.glEnable(3008);
      GL11.glEnable(3553);
   }

   public static double[] getViewerPos(float par1, float horizontal_scaling) {
      Minecraft mc = Minecraft.theMinecraft;
      int cloud_tick_counter = mc.renderGlobal.cloudTickCounter;
      double x = (mc.renderViewEntity.prevPosX + (mc.renderViewEntity.posX - mc.renderViewEntity.prevPosX) * (double)par1 + (double)((float)cloud_tick_counter + par1) * 0.03) / (double)horizontal_scaling;
      double y = mc.renderViewEntity.lastTickPosY + (mc.renderViewEntity.posY - mc.renderViewEntity.lastTickPosY) * (double)par1;
      double z = (mc.renderViewEntity.prevPosZ + (mc.renderViewEntity.posZ - mc.renderViewEntity.prevPosZ) * (double)par1) / (double)horizontal_scaling + 0.33;
      return new double[]{x, y, z};
   }

   private boolean canViewerSeeCloudTopsFancy(double viewer_pos_y) {
      return (double)this.theWorld.provider.getCloudHeight() - viewer_pos_y + 0.33000001311302185 <= 0.5;
   }

   private boolean canViewerSeeCloudBottomsFancy(double viewer_pos_y) {
      return (double)this.theWorld.provider.getCloudHeight() - viewer_pos_y + 0.33000001311302185 > -4.5;
   }

   public void renderCloudsFancy_MITE(float par1) {
      GL11.glShadeModel(7424);
      Vec3 rgb = this.theWorld.getCloudColour(par1);
      float r = (float)rgb.xCoord;
      float g = (float)rgb.yCoord;
      float b = (float)rgb.zCoord;
      float var4 = 12.0F;
      double var10000 = (double)((float)this.cloudTickCounter + par1);
      double[] viewer_pos = getViewerPos(par1, var4);
      double var8 = viewer_pos[0];
      float var2 = (float)viewer_pos[1];
      double var10 = viewer_pos[2];
      float var22 = (float)(var8 - (double)MathHelper.floor_double(var8));
      float var23 = (float)(var10 - (double)MathHelper.floor_double(var10));
      float var12 = this.theWorld.provider.getCloudHeight() - var2 + 0.33F;
      boolean player_can_see_cloud_tops = this.canViewerSeeCloudTopsFancy((double)var2);
      boolean player_can_see_cloud_bottoms = this.canViewerSeeCloudBottomsFancy((double)var2);
      if (this.clouds_recompiling_step == 0) {
         this.compileCloudsFancy(par1, var8, (double)var2, var10);
         this.clouds_recompiling_step = -1;
      } else if (Math.abs(this.last_cloud_compile_x - var8) > 2.0 || Math.abs(this.last_cloud_compile_z - var10) > 2.0 || this.last_cloud_compile_has_cloud_tops != player_can_see_cloud_tops || this.last_cloud_compile_has_cloud_bottoms != player_can_see_cloud_bottoms) {
         this.clouds_recompiling_step = 0;
      }

      if (player_can_see_cloud_tops && player_can_see_cloud_bottoms) {
         GL11.glDisable(2884);
      } else {
         GL11.glEnable(2884);
      }

      GL11.glFogf(2915, -16.0F);
      GL11.glFogf(2916, 384.0F);
      this.mc.entityRenderer.setupCameraTransform(par1, 0, true);
      GL11.glPushMatrix();
      GL11.glScalef(var4, 1.0F, var4);
      GL11.glTranslated(-(var8 - this.last_cloud_compile_x), (double)(-var2), -(var10 - this.last_cloud_compile_z));
      GL11.glCallList(this.clouds_display_list[0]);
      if (this.last_cloud_compile_has_cloud_tops) {
         GL11.glColor4f(r, g, b, 0.8F);
         GL11.glCallList(this.clouds_display_list[1]);
      }

      if (this.last_cloud_compile_has_cloud_bottoms) {
         GL11.glColor4f(r * 0.7F, g * 0.7F, b * 0.7F, 0.8F);
         GL11.glCallList(this.clouds_display_list[2]);
      }

      GL11.glColor4f(r * 0.9F, g * 0.9F, b * 0.9F, 0.8F);
      GL11.glCallList(this.clouds_display_list[3]);
      GL11.glColor4f(r * 0.8F, g * 0.8F, b * 0.8F, 0.8F);
      GL11.glCallList(this.clouds_display_list[4]);
      GL11.glPopMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.entityRenderer.setupCameraTransform(par1, 0, false);
      GL11.glFogf(2915, 64.0F);
      GL11.glFogf(2916, 256.0F);
      GL11.glEnable(2884);
      GL11.glShadeModel(7425);
   }

   public void renderClouds(float par1) {
      if (this.mc.theWorld.provider.isSurfaceWorld()) {
         boolean force_fancy_clouds = true;
         if (!force_fancy_clouds && !this.mc.gameSettings.isFancyGraphicsEnabled()) {
            GL11.glEnable(2884);
            float var2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)par1);
            byte var3 = 32;
            int var4 = 256 / var3;
            Tessellator var5 = Tessellator.instance;
            this.renderEngine.bindTexture(locationCloudsPng);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            Vec3 var6 = this.theWorld.getCloudColour(par1);
            float var7 = (float)var6.xCoord;
            float var8 = (float)var6.yCoord;
            float var9 = (float)var6.zCoord;
            float var10;
            if (this.mc.gameSettings.anaglyph) {
               var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
               float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
               float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
               var7 = var10;
               var8 = var11;
               var9 = var12;
            }

            var10 = 4.8828125E-4F;
            double var24 = (double)((float)this.cloudTickCounter + par1);
            double var13 = this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)par1 + var24 * 0.029999999329447746;
            double var15 = this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)par1;
            int var17 = MathHelper.floor_double(var13 / 2048.0);
            int var18 = MathHelper.floor_double(var15 / 2048.0);
            var13 -= (double)(var17 * 2048);
            var15 -= (double)(var18 * 2048);
            float var19 = this.theWorld.provider.getCloudHeight() - var2 + 0.33F;
            float var20 = (float)(var13 * (double)var10);
            float var21 = (float)(var15 * (double)var10);
            boolean player_can_see_cloud_bottoms = var19 > -0.0F;
            GL11.glCullFace(player_can_see_cloud_bottoms ? 1028 : 1029);
            var5.startDrawingQuads();
            GL11.glColor4f(var7, var8, var9, 0.8F);
            var5.hasTexture = true;
            int[] rawBuffer = var5.rawBuffer;
            int y0 = Float.floatToRawIntBits(var19);

            for(int var22 = -var3 * var4; var22 < var3 * var4; var22 += var3) {
               int u0 = Float.floatToRawIntBits((float)var22 * var10 + var20);
               int u1 = Float.floatToRawIntBits((float)(var22 + var3) * var10 + var20);
               int x0 = Float.floatToRawIntBits((float)var22);
               int x1 = Float.floatToRawIntBits((float)(var22 + var3));

               for(int var23 = -var3 * var4; var23 < var3 * var4; var23 += var3) {
                  if (RenderingScheme.current == 0) {
                     var5.addVertexWithUV((double)(var22 + 0), (double)var19, (double)(var23 + var3), (double)((float)(var22 + 0) * var10 + var20), (double)((float)(var23 + var3) * var10 + var21));
                     var5.addVertexWithUV((double)(var22 + var3), (double)var19, (double)(var23 + var3), (double)((float)(var22 + var3) * var10 + var20), (double)((float)(var23 + var3) * var10 + var21));
                     var5.addVertexWithUV((double)(var22 + var3), (double)var19, (double)(var23 + 0), (double)((float)(var22 + var3) * var10 + var20), (double)((float)(var23 + 0) * var10 + var21));
                     var5.addVertexWithUV((double)(var22 + 0), (double)var19, (double)(var23 + 0), (double)((float)(var22 + 0) * var10 + var20), (double)((float)(var23 + 0) * var10 + var21));
                  } else {
                     int v0 = Float.floatToRawIntBits((float)(var23 + var3) * var10 + var21);
                     int v2 = Float.floatToRawIntBits((float)var23 * var10 + var21);
                     int z0 = Float.floatToRawIntBits((float)(var23 + var3));
                     int z1 = Float.floatToRawIntBits((float)var23);
                     rawBuffer[var5.rawBufferIndex + 3] = u0;
                     rawBuffer[var5.rawBufferIndex + 11] = u1;
                     rawBuffer[var5.rawBufferIndex + 19] = u1;
                     rawBuffer[var5.rawBufferIndex + 27] = u0;
                     rawBuffer[var5.rawBufferIndex + 4] = v0;
                     rawBuffer[var5.rawBufferIndex + 12] = v0;
                     rawBuffer[var5.rawBufferIndex + 20] = v2;
                     rawBuffer[var5.rawBufferIndex + 28] = v2;
                     rawBuffer[var5.rawBufferIndex + 0] = x0;
                     rawBuffer[var5.rawBufferIndex + 8] = x1;
                     rawBuffer[var5.rawBufferIndex + 16] = x1;
                     rawBuffer[var5.rawBufferIndex + 24] = x0;
                     rawBuffer[var5.rawBufferIndex + 1] = y0;
                     rawBuffer[var5.rawBufferIndex + 9] = y0;
                     rawBuffer[var5.rawBufferIndex + 17] = y0;
                     rawBuffer[var5.rawBufferIndex + 25] = y0;
                     rawBuffer[var5.rawBufferIndex + 2] = z0;
                     rawBuffer[var5.rawBufferIndex + 10] = z0;
                     rawBuffer[var5.rawBufferIndex + 18] = z1;
                     rawBuffer[var5.rawBufferIndex + 26] = z1;
                     var5.rawBufferIndex += 32;
                     var5.addedVertices += 4;
                     var5.vertexCount += 4;
                     if (var5.rawBufferIndex >= 2097120) {
                        var5.draw();
                        var5.isDrawing = true;
                     }
                  }
               }
            }

            var5.draw();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            GL11.glCullFace(1029);
         } else if (this.mc.gameSettings.anaglyph) {
            this.renderCloudsFancy(par1);
         } else {
            this.renderCloudsFancy_MITE(par1);
         }
      }

   }

   public boolean hasCloudFog(double par1, double par3, double par5, float par7) {
      return false;
   }

   public void renderCloudsFancy(float par1) {
      GL11.glDisable(2884);
      float var2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)par1);
      Tessellator var3 = Tessellator.instance;
      float var4 = 12.0F;
      float var5 = 4.0F;
      double var6 = (double)((float)this.cloudTickCounter + par1);
      double var8 = (this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)par1 + var6 * 0.029999999329447746) / (double)var4;
      double var10 = (this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)par1) / (double)var4 + 0.33000001311302185;
      float var12 = this.theWorld.provider.getCloudHeight() - var2 + 0.33F;
      int var13 = MathHelper.floor_double(var8 / 2048.0);
      int var14 = MathHelper.floor_double(var10 / 2048.0);
      var8 -= (double)(var13 * 2048);
      var10 -= (double)(var14 * 2048);
      this.renderEngine.bindTexture(locationCloudsPng);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      Vec3 var15 = this.theWorld.getCloudColour(par1);
      float var16 = (float)var15.xCoord;
      float var17 = (float)var15.yCoord;
      float var18 = (float)var15.zCoord;
      float var19;
      float var21;
      float var20;
      if (this.mc.gameSettings.anaglyph) {
         var19 = (var16 * 30.0F + var17 * 59.0F + var18 * 11.0F) / 100.0F;
         var20 = (var16 * 30.0F + var17 * 70.0F) / 100.0F;
         var21 = (var16 * 30.0F + var18 * 70.0F) / 100.0F;
         var16 = var19;
         var17 = var20;
         var18 = var21;
      }

      var19 = (float)(var8 * 0.0);
      var20 = (float)(var10 * 0.0);
      var21 = 0.00390625F;
      var19 = (float)MathHelper.floor_double(var8) * var21;
      var20 = (float)MathHelper.floor_double(var10) * var21;
      float var22 = (float)(var8 - (double)MathHelper.floor_double(var8));
      float var23 = (float)(var10 - (double)MathHelper.floor_double(var10));
      byte var24 = 8;
      byte var25 = 4;
      float var26 = 9.765625E-4F;
      GL11.glScalef(var4, 1.0F, var4);

      for(int var27 = 0; var27 < 2; ++var27) {
         if (var27 == 0) {
            GL11.glColorMask(false, false, false, false);
         } else if (this.mc.gameSettings.anaglyph) {
            if (EntityRenderer.anaglyphField == 0) {
               GL11.glColorMask(false, true, true, true);
            } else {
               GL11.glColorMask(true, false, false, true);
            }
         } else {
            GL11.glColorMask(true, true, true, true);
         }

         for(int var28 = -var25 + 1; var28 <= var25; ++var28) {
            for(int var29 = -var25 + 1; var29 <= var25; ++var29) {
               var3.startDrawingQuads();
               float var30 = (float)(var28 * var24);
               float var31 = (float)(var29 * var24);
               float var32 = var30 - var22;
               float var33 = var31 - var23;
               if (var12 > -var5 - 1.0F) {
                  var3.setColorRGBA_F(var16 * 0.7F, var17 * 0.7F, var18 * 0.7F, 0.8F);
                  var3.setNormal(0.0F, -1.0F, 0.0F);
                  var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                  var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                  var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                  var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
               }

               if (var12 <= var5 + 1.0F) {
                  var3.setColorRGBA_F(var16, var17, var18, 0.8F);
                  var3.setNormal(0.0F, 1.0F, 0.0F);
                  var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5 - var26), (double)(var33 + (float)var24), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                  var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5 - var26), (double)(var33 + (float)var24), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                  var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5 - var26), (double)(var33 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                  var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5 - var26), (double)(var33 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
               }

               var3.setColorRGBA_F(var16 * 0.9F, var17 * 0.9F, var18 * 0.9F, 0.8F);
               int var34;
               if (var28 > -1) {
                  var3.setNormal(-1.0F, 0.0F, 0.0F);

                  for(var34 = 0; var34 < var24; ++var34) {
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + var5), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                  }
               }

               if (var28 <= 1) {
                  var3.setNormal(1.0F, 0.0F, 0.0F);

                  for(var34 = 0; var34 < var24; ++var34) {
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + var5), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + var5), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                  }
               }

               var3.setColorRGBA_F(var16 * 0.8F, var17 * 0.8F, var18 * 0.8F, 0.8F);
               if (var29 > -1) {
                  var3.setNormal(0.0F, 0.0F, -1.0F);

                  for(var34 = 0; var34 < var24; ++var34) {
                     var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                     var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                  }
               }

               if (var29 <= 1) {
                  var3.setNormal(0.0F, 0.0F, 1.0F);

                  for(var34 = 0; var34 < var24; ++var34) {
                     var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                     var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                  }
               }

               var3.draw();
            }
         }
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(3042);
      GL11.glEnable(2884);
   }

   public void renderCloudSide(float par1, int side, double x, double z, boolean player_can_see_cloud_tops, boolean player_can_see_cloud_bottoms) {
      float var2 = 0.0F;
      Tessellator var3 = Tessellator.instance;
      float var5 = 4.0F;
      double var10000 = (double)((float)this.cloudTickCounter + par1);
      double var8 = x;
      double var10 = z;
      float var12 = this.theWorld.provider.getCloudHeight() - 0.0F + 0.33F;
      int var13 = MathHelper.floor_double(var8 / 2048.0);
      int var14 = MathHelper.floor_double(var10 / 2048.0);
      var8 -= (double)(var13 * 2048);
      var10 -= (double)(var14 * 2048);
      this.renderEngine.bindTexture(locationCloudsPng);
      Vec3 var15 = this.theWorld.getCloudColour(par1);
      float var16 = (float)var15.xCoord;
      float var17 = (float)var15.yCoord;
      float var18 = (float)var15.zCoord;
      float var21 = 0.00390625F;
      float var19 = (float)MathHelper.floor_double(var8) * 0.00390625F;
      float var20 = (float)MathHelper.floor_double(var10) * 0.00390625F;
      float var22 = (float)(var8 - (double)MathHelper.floor_double(var8));
      float var23 = (float)(var10 - (double)MathHelper.floor_double(var10));
      float var26 = 9.765625E-4F;
      boolean first_pass = side == -1;
      if (first_pass) {
         GL11.glDisable(3042);
         GL11.glColorMask(false, false, false, false);
      } else {
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glColorMask(true, true, true, true);
      }

      var3.draw_in_groups = true;
      int[] rawBuffer = var3.rawBuffer;
      int cloud_top_y = Float.floatToRawIntBits(var12 + 4.0F);
      int cloud_bottom_y = Float.floatToRawIntBits(var12);
      int cloud_bottom_y_spaced = player_can_see_cloud_bottoms && player_can_see_cloud_tops ? cloud_bottom_y : Float.floatToRawIntBits(var12 - 0.01F);
      int x0;
      int x1;
      int z0;
      int z2;
      int var28;
      float var30;
      int var29;
      float var31;
      float var32;
      float var33;
      int u0;
      int u1;
      int v0;
      int v2;
      if (first_pass || side == 0) {
         var3.startDrawingQuads();
         var3.hasTexture = true;
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         int y1 = Float.floatToRawIntBits(var12 + 4.0F - 9.765625E-4F);

         for(var28 = -5; var28 <= 5; ++var28) {
            var30 = (float)(var28 * 8);
            u0 = Float.floatToRawIntBits(var30 * 0.00390625F + var19);
            u1 = Float.floatToRawIntBits((var30 + 8.0F) * 0.00390625F + var19);

            for(var29 = -5; var29 <= 5; ++var29) {
               var31 = (float)(var29 * 8);
               var32 = var30 - var22;
               var33 = var31 - var23;
               v0 = Float.floatToRawIntBits((var31 + 8.0F) * 0.00390625F + var20);
               v2 = Float.floatToRawIntBits(var31 * 0.00390625F + var20);
               x0 = Float.floatToRawIntBits(var32);
               x1 = Float.floatToRawIntBits(var32 + 8.0F);
               z0 = Float.floatToRawIntBits(var33 + 8.0F);
               z2 = Float.floatToRawIntBits(var33);
               if (RenderingScheme.current == 0) {
                  var3.setColorRGBA_F(var16, var17, var18, 0.8F);
                  var3.setNormal(0.0F, 1.0F, 0.0F);
                  var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 4.0F - 9.765625E-4F), (double)(var33 + 8.0F), (double)((var30 + 0.0F) * 0.00390625F + var19), (double)((var31 + 8.0F) * 0.00390625F + var20));
                  var3.addVertexWithUV((double)(var32 + 8.0F), (double)(var12 + 4.0F - 9.765625E-4F), (double)(var33 + 8.0F), (double)((var30 + 8.0F) * 0.00390625F + var19), (double)((var31 + 8.0F) * 0.00390625F + var20));
                  var3.addVertexWithUV((double)(var32 + 8.0F), (double)(var12 + 4.0F - 9.765625E-4F), (double)(var33 + 0.0F), (double)((var30 + 8.0F) * 0.00390625F + var19), (double)((var31 + 0.0F) * 0.00390625F + var20));
                  var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 4.0F - 9.765625E-4F), (double)(var33 + 0.0F), (double)((var30 + 0.0F) * 0.00390625F + var19), (double)((var31 + 0.0F) * 0.00390625F + var20));
               } else {
                  rawBuffer[var3.rawBufferIndex + 3] = u0;
                  rawBuffer[var3.rawBufferIndex + 11] = u1;
                  rawBuffer[var3.rawBufferIndex + 19] = u1;
                  rawBuffer[var3.rawBufferIndex + 27] = u0;
                  rawBuffer[var3.rawBufferIndex + 4] = v0;
                  rawBuffer[var3.rawBufferIndex + 12] = v0;
                  rawBuffer[var3.rawBufferIndex + 20] = v2;
                  rawBuffer[var3.rawBufferIndex + 28] = v2;
                  rawBuffer[var3.rawBufferIndex + 0] = x0;
                  rawBuffer[var3.rawBufferIndex + 8] = x1;
                  rawBuffer[var3.rawBufferIndex + 16] = x1;
                  rawBuffer[var3.rawBufferIndex + 24] = x0;
                  rawBuffer[var3.rawBufferIndex + 1] = y1;
                  rawBuffer[var3.rawBufferIndex + 9] = y1;
                  rawBuffer[var3.rawBufferIndex + 17] = y1;
                  rawBuffer[var3.rawBufferIndex + 25] = y1;
                  rawBuffer[var3.rawBufferIndex + 2] = z0;
                  rawBuffer[var3.rawBufferIndex + 10] = z0;
                  rawBuffer[var3.rawBufferIndex + 18] = z2;
                  rawBuffer[var3.rawBufferIndex + 26] = z2;
                  var3.rawBufferIndex += 32;
                  var3.addedVertices += 4;
                  var3.vertexCount += 4;
                  if (var3.rawBufferIndex >= 2097120) {
                     var3.draw();
                     var3.isDrawing = true;
                  }
               }
            }
         }

         var3.draw();
      }

      if (first_pass || side == 1) {
         var3.startDrawingQuads();
         var3.hasTexture = true;
         GL11.glNormal3f(0.0F, -1.0F, 0.0F);

         for(var28 = -5; var28 <= 5; ++var28) {
            var30 = (float)(var28 * 8);
            u0 = Float.floatToRawIntBits(var30 * 0.00390625F + var19);
            u1 = Float.floatToRawIntBits((var30 + 8.0F) * 0.00390625F + var19);

            for(var29 = -5; var29 <= 5; ++var29) {
               boolean tight_bottom = var28 <= -3 || var28 >= 3 || var29 <= -3 || var29 >= 3;
               int y0;
               if (tight_bottom) {
                  y0 = cloud_bottom_y;
               } else {
                  y0 = cloud_bottom_y_spaced;
               }

               var32 = (float)(var29 * 8);
               var33 = var30 - var22;
               float v1 = var32 - var23;
               v0 = Float.floatToRawIntBits((var32 + 8.0F) * 0.00390625F + var20);
               v2 = Float.floatToRawIntBits(var32 * 0.00390625F + var20);
               x0 = Float.floatToRawIntBits(v1);
               x1 = Float.floatToRawIntBits(v1 + 8.0F);
               z0 = Float.floatToRawIntBits(v1 + 8.0F);
               z2 = Float.floatToRawIntBits(v1);
               if (RenderingScheme.current == 0) {
                  var3.setColorRGBA_F(var16 * 0.7F, var17 * 0.7F, var18 * 0.7F, 0.8F);
                  var3.setNormal(0.0F, -1.0F, 0.0F);
                  var3.addVertexWithUV((double)(v1 + 0.0F), (double)(var12 + 0.0F), (double)(v1 + 0.0F), (double)((var30 + 0.0F) * 0.00390625F + var19), (double)((var32 + 0.0F) * 0.00390625F + var20));
                  var3.addVertexWithUV((double)(v1 + 8.0F), (double)(var12 + 0.0F), (double)(v1 + 0.0F), (double)((var30 + 8.0F) * 0.00390625F + var19), (double)((var32 + 0.0F) * 0.00390625F + var20));
                  var3.addVertexWithUV((double)(v1 + 8.0F), (double)(var12 + 0.0F), (double)(v1 + 8.0F), (double)((var30 + 8.0F) * 0.00390625F + var19), (double)((var32 + 8.0F) * 0.00390625F + var20));
                  var3.addVertexWithUV((double)(v1 + 0.0F), (double)(var12 + 0.0F), (double)(v1 + 8.0F), (double)((var30 + 0.0F) * 0.00390625F + var19), (double)((var32 + 8.0F) * 0.00390625F + var20));
               } else {
                  rawBuffer[var3.rawBufferIndex + 3] = u0;
                  rawBuffer[var3.rawBufferIndex + 11] = u0;
                  rawBuffer[var3.rawBufferIndex + 19] = u1;
                  rawBuffer[var3.rawBufferIndex + 27] = u1;
                  rawBuffer[var3.rawBufferIndex + 4] = v0;
                  rawBuffer[var3.rawBufferIndex + 12] = v2;
                  rawBuffer[var3.rawBufferIndex + 20] = v2;
                  rawBuffer[var3.rawBufferIndex + 28] = v0;
                  rawBuffer[var3.rawBufferIndex + 0] = x0;
                  rawBuffer[var3.rawBufferIndex + 8] = x0;
                  rawBuffer[var3.rawBufferIndex + 16] = x1;
                  rawBuffer[var3.rawBufferIndex + 24] = x1;
                  rawBuffer[var3.rawBufferIndex + 1] = y0;
                  rawBuffer[var3.rawBufferIndex + 9] = y0;
                  rawBuffer[var3.rawBufferIndex + 17] = y0;
                  rawBuffer[var3.rawBufferIndex + 25] = y0;
                  rawBuffer[var3.rawBufferIndex + 2] = z0;
                  rawBuffer[var3.rawBufferIndex + 10] = z2;
                  rawBuffer[var3.rawBufferIndex + 18] = z2;
                  rawBuffer[var3.rawBufferIndex + 26] = z0;
                  var3.rawBufferIndex += 32;
                  var3.addedVertices += 4;
                  var3.vertexCount += 4;
                  if (var3.rawBufferIndex >= 2097120) {
                     var3.draw();
                     var3.isDrawing = true;
                  }
               }
            }
         }

         var3.draw();
      }

      float z_plane;
      int var34;
      if (first_pass || side == 2) {
         var3.startDrawingQuads();
         var3.hasTexture = true;
         GL11.glNormal3f(-1.0F, 0.0F, 0.0F);

         for(var28 = -1; var28 <= 5; ++var28) {
            var30 = (float)(var28 * 8);

            for(var29 = -5; var29 <= 5; ++var29) {
               var31 = (float)(var29 * 8);
               var32 = var30 - var22;
               var33 = var31 - var23;
               v0 = Float.floatToRawIntBits((var31 + 8.0F) * 0.00390625F + var20);
               v2 = Float.floatToRawIntBits(var31 * 0.00390625F + var20);
               z0 = Float.floatToRawIntBits(var33 + 8.0F);
               z2 = Float.floatToRawIntBits(var33);

               for(var34 = 0; var34 < 8; ++var34) {
                  z_plane = var32 + (float)var34;
                  u0 = Float.floatToRawIntBits((var30 + (float)var34 + 0.5F) * 0.00390625F + var19);
                  x0 = Float.floatToRawIntBits(z_plane);
                  if (RenderingScheme.current == 0) {
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + 8.0F), (double)((var30 + (float)var34 + 0.5F) * 0.00390625F + var19), (double)((var31 + 8.0F) * 0.00390625F + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 4.0F), (double)(var33 + 8.0F), (double)((var30 + (float)var34 + 0.5F) * 0.00390625F + var19), (double)((var31 + 8.0F) * 0.00390625F + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 4.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * 0.00390625F + var19), (double)((var31 + 0.0F) * 0.00390625F + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * 0.00390625F + var19), (double)((var31 + 0.0F) * 0.00390625F + var20));
                  } else {
                     rawBuffer[var3.rawBufferIndex + 3] = u0;
                     rawBuffer[var3.rawBufferIndex + 11] = u0;
                     rawBuffer[var3.rawBufferIndex + 19] = u0;
                     rawBuffer[var3.rawBufferIndex + 27] = u0;
                     rawBuffer[var3.rawBufferIndex + 4] = v0;
                     rawBuffer[var3.rawBufferIndex + 12] = v0;
                     rawBuffer[var3.rawBufferIndex + 20] = v2;
                     rawBuffer[var3.rawBufferIndex + 28] = v2;
                     rawBuffer[var3.rawBufferIndex + 0] = x0;
                     rawBuffer[var3.rawBufferIndex + 8] = x0;
                     rawBuffer[var3.rawBufferIndex + 16] = x0;
                     rawBuffer[var3.rawBufferIndex + 24] = x0;
                     rawBuffer[var3.rawBufferIndex + 1] = cloud_bottom_y;
                     rawBuffer[var3.rawBufferIndex + 9] = cloud_top_y;
                     rawBuffer[var3.rawBufferIndex + 17] = cloud_top_y;
                     rawBuffer[var3.rawBufferIndex + 25] = cloud_bottom_y;
                     rawBuffer[var3.rawBufferIndex + 2] = z0;
                     rawBuffer[var3.rawBufferIndex + 10] = z0;
                     rawBuffer[var3.rawBufferIndex + 18] = z2;
                     rawBuffer[var3.rawBufferIndex + 26] = z2;
                     var3.rawBufferIndex += 32;
                     var3.addedVertices += 4;
                     var3.vertexCount += 4;
                     if (var3.rawBufferIndex >= 2097120) {
                        var3.draw();
                        var3.isDrawing = true;
                     }
                  }
               }
            }
         }

         var3.draw();
      }

      if (first_pass || side == 2) {
         var3.startDrawingQuads();
         var3.hasTexture = true;
         GL11.glNormal3f(1.0F, 0.0F, 0.0F);

         for(var28 = -5; var28 < 1; ++var28) {
            var30 = (float)(var28 * 8);

            for(var29 = -5; var29 <= 5; ++var29) {
               var31 = (float)(var29 * 8);
               var32 = var30 - var22;
               var33 = var31 - var23;
               v0 = Float.floatToRawIntBits((var31 + 8.0F) * 0.00390625F + var20);
               v2 = Float.floatToRawIntBits(var31 * 0.00390625F + var20);
               z0 = Float.floatToRawIntBits(var33 + 8.0F);
               z2 = Float.floatToRawIntBits(var33);

               for(var34 = 0; var34 < 8; ++var34) {
                  z_plane = var32 + (float)var34 + 1.0F - 9.765625E-4F;
                  u0 = Float.floatToRawIntBits((var30 + (float)var34 + 0.5F) * 0.00390625F + var19);
                  x0 = Float.floatToRawIntBits(z_plane);
                  if (RenderingScheme.current == 0) {
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - 9.765625E-4F), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * 0.00390625F + var19), (double)((var31 + 0.0F) * 0.00390625F + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - 9.765625E-4F), (double)(var12 + 4.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * 0.00390625F + var19), (double)((var31 + 0.0F) * 0.00390625F + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - 9.765625E-4F), (double)(var12 + 4.0F), (double)(var33 + 8.0F), (double)((var30 + (float)var34 + 0.5F) * 0.00390625F + var19), (double)((var31 + 8.0F) * 0.00390625F + var20));
                     var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - 9.765625E-4F), (double)(var12 + 0.0F), (double)(var33 + 8.0F), (double)((var30 + (float)var34 + 0.5F) * 0.00390625F + var19), (double)((var31 + 8.0F) * 0.00390625F + var20));
                  } else {
                     rawBuffer[var3.rawBufferIndex + 3] = u0;
                     rawBuffer[var3.rawBufferIndex + 11] = u0;
                     rawBuffer[var3.rawBufferIndex + 19] = u0;
                     rawBuffer[var3.rawBufferIndex + 27] = u0;
                     rawBuffer[var3.rawBufferIndex + 4] = v0;
                     rawBuffer[var3.rawBufferIndex + 12] = v2;
                     rawBuffer[var3.rawBufferIndex + 20] = v2;
                     rawBuffer[var3.rawBufferIndex + 28] = v0;
                     rawBuffer[var3.rawBufferIndex + 0] = x0;
                     rawBuffer[var3.rawBufferIndex + 8] = x0;
                     rawBuffer[var3.rawBufferIndex + 16] = x0;
                     rawBuffer[var3.rawBufferIndex + 24] = x0;
                     rawBuffer[var3.rawBufferIndex + 1] = cloud_bottom_y;
                     rawBuffer[var3.rawBufferIndex + 9] = cloud_bottom_y;
                     rawBuffer[var3.rawBufferIndex + 17] = cloud_top_y;
                     rawBuffer[var3.rawBufferIndex + 25] = cloud_top_y;
                     rawBuffer[var3.rawBufferIndex + 2] = z0;
                     rawBuffer[var3.rawBufferIndex + 10] = z2;
                     rawBuffer[var3.rawBufferIndex + 18] = z2;
                     rawBuffer[var3.rawBufferIndex + 26] = z0;
                     var3.rawBufferIndex += 32;
                     var3.addedVertices += 4;
                     var3.vertexCount += 4;
                     if (var3.rawBufferIndex >= 2097120) {
                        var3.draw();
                        var3.isDrawing = true;
                     }
                  }
               }
            }
         }

         var3.draw();
      }

      if (first_pass || side == 3) {
         var3.startDrawingQuads();
         var3.hasTexture = true;
         GL11.glNormal3f(0.0F, 0.0F, -1.0F);

         for(var28 = -5; var28 <= 5; ++var28) {
            var30 = (float)(var28 * 8);
            u0 = Float.floatToRawIntBits(var30 * 0.00390625F + var19);
            u1 = Float.floatToRawIntBits((var30 + 8.0F) * 0.00390625F + var19);

            for(var29 = -1; var29 <= 5; ++var29) {
               var31 = (float)(var29 * 8);
               var32 = var30 - var22;
               var33 = var31 - var23;
               x0 = Float.floatToRawIntBits(var32);
               x1 = Float.floatToRawIntBits(var32 + 8.0F);

               for(var34 = 0; var34 < 8; ++var34) {
                  z_plane = var33 + (float)var34;
                  v0 = Float.floatToRawIntBits((var31 + (float)var34 + 0.5F) * 0.00390625F + var20);
                  z0 = Float.floatToRawIntBits(z_plane);
                  if (RenderingScheme.current == 0) {
                     var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 4.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 0.0F) * 0.00390625F + var19), (double)((var31 + (float)var34 + 0.5F) * 0.00390625F + var20));
                     var3.addVertexWithUV((double)(var32 + 8.0F), (double)(var12 + 4.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 8.0F) * 0.00390625F + var19), (double)((var31 + (float)var34 + 0.5F) * 0.00390625F + var20));
                     var3.addVertexWithUV((double)(var32 + 8.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 8.0F) * 0.00390625F + var19), (double)((var31 + (float)var34 + 0.5F) * 0.00390625F + var20));
                     var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 0.0F) * 0.00390625F + var19), (double)((var31 + (float)var34 + 0.5F) * 0.00390625F + var20));
                  } else {
                     rawBuffer[var3.rawBufferIndex + 3] = u0;
                     rawBuffer[var3.rawBufferIndex + 11] = u1;
                     rawBuffer[var3.rawBufferIndex + 19] = u1;
                     rawBuffer[var3.rawBufferIndex + 27] = u0;
                     rawBuffer[var3.rawBufferIndex + 4] = v0;
                     rawBuffer[var3.rawBufferIndex + 12] = v0;
                     rawBuffer[var3.rawBufferIndex + 20] = v0;
                     rawBuffer[var3.rawBufferIndex + 28] = v0;
                     rawBuffer[var3.rawBufferIndex + 0] = x0;
                     rawBuffer[var3.rawBufferIndex + 8] = x1;
                     rawBuffer[var3.rawBufferIndex + 16] = x1;
                     rawBuffer[var3.rawBufferIndex + 24] = x0;
                     rawBuffer[var3.rawBufferIndex + 1] = cloud_top_y;
                     rawBuffer[var3.rawBufferIndex + 9] = cloud_top_y;
                     rawBuffer[var3.rawBufferIndex + 17] = cloud_bottom_y;
                     rawBuffer[var3.rawBufferIndex + 25] = cloud_bottom_y;
                     rawBuffer[var3.rawBufferIndex + 2] = z0;
                     rawBuffer[var3.rawBufferIndex + 10] = z0;
                     rawBuffer[var3.rawBufferIndex + 18] = z0;
                     rawBuffer[var3.rawBufferIndex + 26] = z0;
                     var3.rawBufferIndex += 32;
                     var3.addedVertices += 4;
                     var3.vertexCount += 4;
                     if (var3.rawBufferIndex >= 2097120) {
                        var3.draw();
                        var3.isDrawing = true;
                     }
                  }
               }
            }
         }

         var3.draw();
      }

      if (first_pass || side == 3) {
         var3.startDrawingQuads();
         var3.hasTexture = true;
         GL11.glNormal3f(0.0F, 0.0F, 1.0F);

         for(var28 = -5; var28 <= 5; ++var28) {
            var30 = (float)(var28 * 8);
            u0 = Float.floatToRawIntBits(var30 * 0.00390625F + var19);
            u1 = Float.floatToRawIntBits((var30 + 8.0F) * 0.00390625F + var19);

            for(var29 = -5; var29 < 1; ++var29) {
               var31 = (float)(var29 * 8);
               var32 = var30 - var22;
               var33 = var31 - var23;
               x0 = Float.floatToRawIntBits(var32);
               x1 = Float.floatToRawIntBits(var32 + 8.0F);

               for(var34 = 0; var34 < 8; ++var34) {
                  z_plane = var33 + (float)var34 + 1.0F - 9.765625E-4F;
                  v0 = Float.floatToRawIntBits((var31 + (float)var34 + 0.5F) * 0.00390625F + var20);
                  z0 = Float.floatToRawIntBits(z_plane);
                  if (RenderingScheme.current == 0) {
                     var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 1.0F - 9.765625E-4F), (double)((var30 + 0.0F) * 0.00390625F + var19), (double)((var31 + (float)var34 + 0.5F) * 0.00390625F + var20));
                     var3.addVertexWithUV((double)(var32 + 8.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 1.0F - 9.765625E-4F), (double)((var30 + 8.0F) * 0.00390625F + var19), (double)((var31 + (float)var34 + 0.5F) * 0.00390625F + var20));
                     var3.addVertexWithUV((double)(var32 + 8.0F), (double)(var12 + 4.0F), (double)(var33 + (float)var34 + 1.0F - 9.765625E-4F), (double)((var30 + 8.0F) * 0.00390625F + var19), (double)((var31 + (float)var34 + 0.5F) * 0.00390625F + var20));
                     var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 4.0F), (double)(var33 + (float)var34 + 1.0F - 9.765625E-4F), (double)((var30 + 0.0F) * 0.00390625F + var19), (double)((var31 + (float)var34 + 0.5F) * 0.00390625F + var20));
                  } else {
                     rawBuffer[var3.rawBufferIndex + 3] = u0;
                     rawBuffer[var3.rawBufferIndex + 11] = u0;
                     rawBuffer[var3.rawBufferIndex + 19] = u1;
                     rawBuffer[var3.rawBufferIndex + 27] = u1;
                     rawBuffer[var3.rawBufferIndex + 4] = v0;
                     rawBuffer[var3.rawBufferIndex + 12] = v0;
                     rawBuffer[var3.rawBufferIndex + 20] = v0;
                     rawBuffer[var3.rawBufferIndex + 28] = v0;
                     rawBuffer[var3.rawBufferIndex + 0] = x0;
                     rawBuffer[var3.rawBufferIndex + 8] = x0;
                     rawBuffer[var3.rawBufferIndex + 16] = x1;
                     rawBuffer[var3.rawBufferIndex + 24] = x1;
                     rawBuffer[var3.rawBufferIndex + 1] = cloud_top_y;
                     rawBuffer[var3.rawBufferIndex + 9] = cloud_bottom_y;
                     rawBuffer[var3.rawBufferIndex + 17] = cloud_bottom_y;
                     rawBuffer[var3.rawBufferIndex + 25] = cloud_top_y;
                     rawBuffer[var3.rawBufferIndex + 2] = z0;
                     rawBuffer[var3.rawBufferIndex + 10] = z0;
                     rawBuffer[var3.rawBufferIndex + 18] = z0;
                     rawBuffer[var3.rawBufferIndex + 26] = z0;
                     var3.rawBufferIndex += 32;
                     var3.addedVertices += 4;
                     var3.vertexCount += 4;
                     if (var3.rawBufferIndex >= 2097120) {
                        var3.draw();
                        var3.isDrawing = true;
                     }
                  }
               }
            }
         }

         var3.draw();
      }

      var3.draw_in_groups = true;
      GL11.glDisable(3042);
   }

   public boolean updateRenderers(EntityLivingBase par1EntityLivingBase, boolean par2) {
      if (this.mc.force_rendering_for_screenshot) {
         par2 = true;
         this.mc.force_rendering_for_screenshot = false;
      }

      byte var3 = 2;
      RenderSorter var4 = new RenderSorter(par1EntityLivingBase);
      WorldRenderer[] var5 = new WorldRenderer[var3];
      ArrayList var6 = null;
      int var7 = this.worldRenderersToUpdate.size();
      int var8 = 0;
      this.theWorld.theProfiler.startSection("nearChunksSearch");

      int var9;
      WorldRenderer var10;
      int var11;
      int var12;
      label142:
      for(var9 = 0; var9 < var7; ++var9) {
         var10 = (WorldRenderer)this.worldRenderersToUpdate.get(var9);
         if (var10 != null) {
            if (!par2) {
               if (var10.distanceToEntitySquared(par1EntityLivingBase) > 256.0F) {
                  for(var11 = 0; var11 < var3 && (var5[var11] == null || var4.doCompare(var5[var11], var10) <= 0); ++var11) {
                  }

                  --var11;
                  if (var11 <= 0) {
                     continue;
                  }

                  var12 = var11;

                  while(true) {
                     --var12;
                     if (var12 == 0) {
                        var5[var11] = var10;
                        continue label142;
                     }

                     var5[var12 - 1] = var5[var12];
                  }
               }
            } else if (!var10.isInFrustum) {
               continue;
            }

            if (var6 == null) {
               var6 = new ArrayList();
            }

            ++var8;
            var6.add(var10);
            this.worldRenderersToUpdate.set(var9, (Object)null);
         }
      }

      this.theWorld.theProfiler.endSection();
      this.theWorld.theProfiler.startSection("sort");
      if (var6 != null) {
         if (var6.size() > 1) {
            Collections.sort(var6, var4);
         }

         for(var9 = var6.size() - 1; var9 >= 0; --var9) {
            var10 = (WorldRenderer)var6.get(var9);
            var10.updateRenderer();
         }
      }

      this.theWorld.theProfiler.endSection();
      var9 = 0;
      this.theWorld.theProfiler.startSection("rebuild");

      int var16;
      WorldRenderer var13;
      for(var16 = var3 - 1; var16 >= 0; --var16) {
         var13 = var5[var16];
         if (var13 != null) {
            if (!var13.isInFrustum && var16 != var3 - 1) {
               var5[var16] = null;
               var5[0] = null;
               break;
            }

            var5[var16].updateRenderer();
            ++var9;
         }
      }

      this.theWorld.theProfiler.endSection();
      this.theWorld.theProfiler.startSection("cleanup");
      var16 = 0;
      var11 = 0;

      for(var12 = this.worldRenderersToUpdate.size(); var16 != var12; ++var16) {
         var13 = (WorldRenderer)this.worldRenderersToUpdate.get(var16);
         if (var13 != null) {
            boolean var14 = false;

            for(int var15 = 0; var15 < var3 && !var14; ++var15) {
               if (var13 == var5[var15]) {
                  var14 = true;
               }
            }

            if (!var14) {
               if (var11 != var16) {
                  this.worldRenderersToUpdate.set(var11, var13);
               }

               ++var11;
            }
         }
      }

      this.theWorld.theProfiler.endSection();
      this.theWorld.theProfiler.startSection("trim");

      while(true) {
         --var16;
         if (var16 < var11) {
            this.theWorld.theProfiler.endSection();
            return var7 == var8 + var9;
         }

         this.worldRenderersToUpdate.remove(var16);
      }
   }

   public boolean XXXupdateRenderersMITE(EntityLivingBase par1EntityLivingBase, boolean par2, int iteration) {
      byte var3 = 2;
      RenderSorter var4 = new RenderSorter(par1EntityLivingBase);
      WorldRenderer[] var5 = new WorldRenderer[var3];
      ArrayList var6 = null;
      int var7 = this.worldRenderersToUpdate.size();
      int var8 = 0;
      this.theWorld.theProfiler.startSection("nearChunksSearch");
      boolean use_forced_chunk_rendering = ClientProperties.getForcedChunkRenderingDistance() > 1 && this.mc.thePlayer.ticksExisted >= 20 && iteration == 1;
      float forced_chunk_rendering_distance_sq;
      if (use_forced_chunk_rendering) {
         forced_chunk_rendering_distance_sq = (float)(ClientProperties.getForcedChunkRenderingDistance() * 16 * ClientProperties.getForcedChunkRenderingDistance() * 16);
      } else {
         forced_chunk_rendering_distance_sq = 0.0F;
      }

      int var9;
      WorldRenderer var10;
      int var11;
      int var12;
      label159:
      for(var9 = 0; var9 < var7; ++var9) {
         var10 = (WorldRenderer)this.worldRenderersToUpdate.get(var9);
         if (var10 != null) {
            if (!par2) {
               float distance_sq_from_viewer = var10.distanceToEntitySquared(par1EntityLivingBase);
               if ((!use_forced_chunk_rendering || !var10.isInFrustum || !(distance_sq_from_viewer <= forced_chunk_rendering_distance_sq)) && !(distance_sq_from_viewer <= 256.0F)) {
                  for(var11 = 0; var11 < var3 && (var5[var11] == null || var4.doCompare(var5[var11], var10) <= 0); ++var11) {
                  }

                  --var11;
                  if (var11 <= 0) {
                     continue;
                  }

                  var12 = var11;

                  while(true) {
                     --var12;
                     if (var12 == 0) {
                        var5[var11] = var10;
                        continue label159;
                     }

                     var5[var12 - 1] = var5[var12];
                  }
               }
            } else if (!var10.isInFrustum) {
               continue;
            }

            if (var6 == null) {
               var6 = new ArrayList();
            }

            ++var8;
            var6.add(var10);
            this.worldRenderersToUpdate.set(var9, (Object)null);
         }
      }

      this.theWorld.theProfiler.endSection();
      this.theWorld.theProfiler.startSection("sort");
      if (var6 != null) {
         if (var6.size() > 1) {
            Collections.sort(var6, var4);
         }

         for(var9 = var6.size() - 1; var9 >= 0; --var9) {
            var10 = (WorldRenderer)var6.get(var9);
            var10.updateRenderer();
         }
      }

      this.theWorld.theProfiler.endSection();
      var9 = 0;
      this.theWorld.theProfiler.startSection("rebuild");

      WorldRenderer var13;
      int var16;
      for(var16 = var3 - 1; var16 >= 0; --var16) {
         var13 = var5[var16];
         if (var13 != null) {
            if (!var13.isInFrustum && var16 != var3 - 1) {
               var5[var16] = null;
               var5[0] = null;
               break;
            }

            var5[var16].updateRenderer();
            ++var9;
         }
      }

      this.theWorld.theProfiler.endSection();
      this.theWorld.theProfiler.startSection("cleanup");
      var16 = 0;
      var11 = 0;

      for(var12 = this.worldRenderersToUpdate.size(); var16 != var12; ++var16) {
         var13 = (WorldRenderer)this.worldRenderersToUpdate.get(var16);
         if (var13 != null) {
            boolean var14 = false;

            for(int var15 = 0; var15 < var3 && !var14; ++var15) {
               if (var13 == var5[var15]) {
                  var14 = true;
               }
            }

            if (!var14) {
               if (var11 != var16) {
                  this.worldRenderersToUpdate.set(var11, var13);
               }

               ++var11;
            }
         }
      }

      this.theWorld.theProfiler.endSection();
      this.theWorld.theProfiler.startSection("trim");

      while(true) {
         --var16;
         if (var16 < var11) {
            this.theWorld.theProfiler.endSection();
            return var7 == var8 + var9;
         }

         this.worldRenderersToUpdate.remove(var16);
      }
   }

   public void drawBlockDamageTexture(Tessellator par1Tessellator, EntityPlayer par2EntityPlayer, float par3) {
      double var4 = par2EntityPlayer.lastTickPosX + (par2EntityPlayer.posX - par2EntityPlayer.lastTickPosX) * (double)par3;
      double var6 = par2EntityPlayer.lastTickPosY + (par2EntityPlayer.posY - par2EntityPlayer.lastTickPosY) * (double)par3;
      double var8 = par2EntityPlayer.lastTickPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.lastTickPosZ) * (double)par3;
      if (!this.damagedBlocks.isEmpty()) {
         GL11.glBlendFunc(774, 768);
         this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
         GL11.glPushMatrix();
         GL11.glDisable(3008);
         GL11.glPolygonOffset(-3.0F, -3.0F);
         GL11.glEnable(32823);
         GL11.glEnable(3008);
         par1Tessellator.startDrawingQuads();
         par1Tessellator.setTranslation(-var4, -var6, -var8);
         GL11.glScalef(1.0F, 0.9999F, 1.0F);
         par1Tessellator.disableColor();
         Iterator var10 = this.damagedBlocks.values().iterator();

         while(var10.hasNext()) {
            DestroyBlockProgress var11 = (DestroyBlockProgress)var10.next();
            double var12 = (double)var11.getPartialBlockX() - var4;
            double var14 = (double)var11.getPartialBlockY() - var6;
            double var16 = (double)var11.getPartialBlockZ() - var8;
            if (var12 * var12 + var14 * var14 + var16 * var16 > 1024.0) {
               var10.remove();
            } else {
               int var18 = this.theWorld.getBlockId(var11.getPartialBlockX(), var11.getPartialBlockY(), var11.getPartialBlockZ());
               Block var19 = var18 > 0 ? Block.blocksList[var18] : null;
               if (var19 == null) {
                  var19 = Block.stone;
               }

               this.globalRenderBlocks.renderBlockUsingTexture(var19, var11.getPartialBlockX(), var11.getPartialBlockY(), var11.getPartialBlockZ(), this.destroyBlockIcons[var11.getPartialBlockDamage()]);
            }
         }

         par1Tessellator.draw();
         par1Tessellator.setTranslation(0.0, 0.0, 0.0);
         GL11.glDisable(3008);
         GL11.glPolygonOffset(0.0F, 0.0F);
         GL11.glDisable(32823);
         GL11.glEnable(3008);
         GL11.glDepthMask(true);
         GL11.glPopMatrix();
      }

   }

   public void drawSelectionBox(EntityPlayer par1EntityPlayer, RaycastCollision rc, int par3, float par4) {
      if (par3 == 0 && rc.isBlock()) {
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
         GL11.glLineWidth(2.0F);
         GL11.glDisable(3553);
         GL11.glDepthMask(false);
         float var5 = 0.002F;
         Block block = rc.getBlockHit();
         double var7 = par1EntityPlayer.lastTickPosX + (par1EntityPlayer.posX - par1EntityPlayer.lastTickPosX) * (double)par4;
         double var9 = par1EntityPlayer.lastTickPosY + (par1EntityPlayer.posY - par1EntityPlayer.lastTickPosY) * (double)par4;
         double var11 = par1EntityPlayer.lastTickPosZ + (par1EntityPlayer.posZ - par1EntityPlayer.lastTickPosZ) * (double)par4;
         this.drawOutlinedBoundingBox(block.getSelectedBoundingBoxFromPool(this.theWorld, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z).expand((double)var5, (double)var5, (double)var5).getOffsetBoundingBox(-var7, -var9, -var11));
         GL11.glDepthMask(true);
         GL11.glEnable(3553);
         GL11.glDisable(3042);
      }

   }

   private void drawOutlinedBoundingBox(AxisAlignedBB par1AxisAlignedBB) {
      Tessellator var2 = Tessellator.instance;
      var2.startDrawing(3);
      var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
      var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
      var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
      var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
      var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
      var2.draw();
      var2.startDrawing(3);
      var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
      var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
      var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
      var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
      var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
      var2.draw();
      var2.startDrawing(1);
      var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
      var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
      var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
      var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
      var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
      var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
      var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
      var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
      var2.draw();
   }

   public void markBlocksForUpdate(int par1, int par2, int par3, int par4, int par5, int par6) {
      int var7 = MathHelper.bucketInt(par1, 16);
      int var8 = MathHelper.bucketInt(par2, 16);
      int var9 = MathHelper.bucketInt(par3, 16);
      int var10 = MathHelper.bucketInt(par4, 16);
      int var11 = MathHelper.bucketInt(par5, 16);
      int var12 = MathHelper.bucketInt(par6, 16);

      for(int var13 = var7; var13 <= var10; ++var13) {
         int var14 = var13 % this.renderChunksWide;
         if (var14 < 0) {
            var14 += this.renderChunksWide;
         }

         for(int var15 = var8; var15 <= var11; ++var15) {
            int var16 = var15 % this.renderChunksTall;
            if (var16 < 0) {
               var16 += this.renderChunksTall;
            }

            for(int var17 = var9; var17 <= var12; ++var17) {
               int var18 = var17 % this.renderChunksDeep;
               if (var18 < 0) {
                  var18 += this.renderChunksDeep;
               }

               int var19 = (var18 * this.renderChunksTall + var16) * this.renderChunksWide + var14;
               WorldRenderer var20 = this.worldRenderers[var19];
               if (var20 != null && !var20.needsUpdate) {
                  this.worldRenderersToUpdate.add(var20);
                  var20.markDirty();
               }
            }
         }
      }

   }

   public void markBlockForUpdate(int par1, int par2, int par3) {
      this.markBlocksForUpdate(par1 - 1, par2 - 1, par3 - 1, par1 + 1, par2 + 1, par3 + 1);
   }

   public void markBlockForRenderUpdate(int par1, int par2, int par3) {
      this.markBlocksForUpdate(par1 - 1, par2 - 1, par3 - 1, par1 + 1, par2 + 1, par3 + 1);
   }

   public void markBlockRangeForRenderUpdate(int par1, int par2, int par3, int par4, int par5, int par6) {
      this.markBlocksForUpdate(par1 - 1, par2 - 1, par3 - 1, par4 + 1, par5 + 1, par6 + 1);
   }

   public void clipRenderersByFrustum(ICamera par1ICamera, float par2) {
      for(int var3 = 0; var3 < this.worldRenderers.length; ++var3) {
         if (!this.worldRenderers[var3].skipAllRenderPasses() && (!this.worldRenderers[var3].isInFrustum || (var3 + this.frustumCheckOffset & 15) == 0)) {
            this.worldRenderers[var3].updateInFrustum(par1ICamera);
         }
      }

      ++this.frustumCheckOffset;
   }

   public void playRecord(String par1Str, int par2, int par3, int par4) {
      ItemRecord var5 = ItemRecord.getRecord(par1Str);
      if (par1Str != null && var5 != null) {
         this.mc.ingameGUI.setRecordPlayingMessage(var5.getRecordTitle());
      }

      this.mc.sndManager.playStreaming(par1Str, (float)par2, (float)par3, (float)par4);
   }

   public void playSound(String par1Str, double par2, double par4, double par6, float par8, float par9) {
   }

   public void playLongDistanceSound(String par1Str, double par2, double par4, double par6, float par8, float par9) {
   }

   public void playSoundToNearExcept(EntityPlayer par1EntityPlayer, String par2Str, double par3, double par5, double par7, float par9, float par10) {
   }

   public void spawnParticle(EnumParticle enum_particle, double posX, double posY, double posZ, double velX_or_red, double velY_or_green, double velZ_or_blue) {
      try {
         this.doSpawnParticle(enum_particle, 0, 0, posX, posY, posZ, velX_or_red, velY_or_green, velZ_or_blue);
      } catch (Throwable var17) {
         CrashReport var15 = CrashReport.makeCrashReport(var17, "Exception while adding particle");
         CrashReportCategory var16 = var15.makeCategory("Particle being added");
         var16.addCrashSection("Ordinal", enum_particle.ordinal());
         var16.addCrashSectionCallable("Position", new CallableParticlePositionInfo(this, posX, posY, posZ));
         throw new ReportedException(var15);
      }
   }

   public void spawnParticleEx(EnumParticle enum_particle, int index, int data, double par2, double par4, double par6, double par8, double par10, double par12) {
      try {
         this.doSpawnParticle(enum_particle, index, data, par2, par4, par6, par8, par10, par12);
      } catch (Throwable var19) {
         Throwable var17 = var19;
         CrashReport var15 = CrashReport.makeCrashReport(var17, "Exception while adding particle");
         CrashReportCategory var16 = var15.makeCategory("Particle being added");
         var16.addCrashSection("Ordinal", enum_particle.ordinal());
         var16.addCrashSectionCallable("Position", new CallableParticlePositionInfo(this, par2, par4, par6));
         throw new ReportedException(var15);
      }
   }

   public EntityFX doSpawnParticle(EnumParticle enum_particle, int index, int metadata, double posX, double posY, double posZ, double par8, double par10, double par12) {
      if (this.mc != null && this.mc.renderViewEntity != null && this.mc.effectRenderer != null) {
         int var14 = this.mc.gameSettings.particleSetting;
         if (var14 == 1 && this.theWorld.rand.nextInt(3) == 0) {
            var14 = 2;
         }

         double var15 = this.mc.renderViewEntity.posX - posX;
         double var17 = this.mc.renderViewEntity.posY - posY;
         double var19 = this.mc.renderViewEntity.posZ - posZ;
         EntityFX var21 = null;
         if (enum_particle == EnumParticle.hugeexplosion) {
            this.mc.effectRenderer.addEffect((EntityFX)(var21 = new EntityHugeExplodeFX(this.theWorld, posX, posY, posZ, par8, par10, par12)));
         } else if (enum_particle == EnumParticle.largeexplode) {
            this.mc.effectRenderer.addEffect((EntityFX)(var21 = new EntityLargeExplodeFX(this.renderEngine, this.theWorld, posX, posY, posZ, par8, par10, par12)));
         } else if (enum_particle == EnumParticle.fireworkSpark) {
            this.mc.effectRenderer.addEffect((EntityFX)(var21 = new EntityFireworkSparkFX(this.theWorld, posX, posY, posZ, par8, par10, par12, this.mc.effectRenderer)));
         }

         if (var21 != null) {
            return (EntityFX)var21;
         } else {
            double var22 = 24.0;
            if (var15 * var15 + var17 * var17 + var19 * var19 > var22 * var22) {
               return null;
            } else if (var14 > 1) {
               return null;
            } else {
               if (enum_particle == EnumParticle.bubble) {
                  var21 = new EntityBubbleFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.suspended) {
                  var21 = new EntitySuspendFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
                  ((EntityFX)var21).motionY = 5.000000237487257E-4 + Math.random() * 0.0010000000474974513;
               } else if (enum_particle == EnumParticle.depthsuspend) {
                  var21 = EntityDepthSuspendParticle.getCachedOrCreate(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.townaura) {
                  var21 = new EntityAuraFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.crit) {
                  var21 = new EntityCritFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.magicCrit) {
                  var21 = new EntityCritFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
                  ((EntityFX)var21).setRBGColorF(((EntityFX)var21).getRedColorF() * 0.3F, ((EntityFX)var21).getGreenColorF() * 0.8F, ((EntityFX)var21).getBlueColorF());
                  ((EntityFX)var21).nextTextureIndexX();
               } else if (enum_particle == EnumParticle.smoke) {
                  var21 = new EntitySmokeFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.mobSpell) {
                  var21 = new EntitySpellParticleFX(this.theWorld, posX, posY, posZ, 0.0, 0.0, 0.0);
                  ((EntityFX)var21).setRBGColorF((float)par8, (float)par10, (float)par12);
               } else if (enum_particle == EnumParticle.mobSpellAmbient) {
                  var21 = new EntitySpellParticleFX(this.theWorld, posX, posY, posZ, 0.0, 0.0, 0.0);
                  ((EntityFX)var21).setAlphaF(0.15F);
                  ((EntityFX)var21).setRBGColorF((float)par8, (float)par10, (float)par12);
               } else if (enum_particle == EnumParticle.spell) {
                  var21 = new EntitySpellParticleFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.instantSpell) {
                  var21 = new EntitySpellParticleFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
                  ((EntitySpellParticleFX)var21).setBaseSpellTextureIndex(144);
               } else if (enum_particle == EnumParticle.witchMagic) {
                  var21 = new EntitySpellParticleFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
                  ((EntitySpellParticleFX)var21).setBaseSpellTextureIndex(144);
                  float var24 = this.theWorld.rand.nextFloat() * 0.5F + 0.35F;
                  ((EntityFX)var21).setRBGColorF(1.0F * var24, 0.0F * var24, 1.0F * var24);
               } else if (enum_particle == EnumParticle.note) {
                  var21 = new EntityNoteFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.portal_underworld) {
                  var21 = new EntityPortalFX(this.theWorld, posX, posY, posZ, par8, par10, par12, 0);
               } else if (enum_particle == EnumParticle.portal_nether) {
                  var21 = new EntityPortalFX(this.theWorld, posX, posY, posZ, par8, par10, par12, 2);
               } else if (enum_particle == EnumParticle.runegate) {
                  var21 = new EntityPortalFX(this.theWorld, posX, posY, posZ, par8, par10, par12, 1);
               } else if (enum_particle == EnumParticle.enchantmenttable) {
                  var21 = new EntityEnchantmentTableParticleFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.explode) {
                  var21 = new EntityExplodeFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.flame) {
                  var21 = new EntityFlameFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.lava) {
                  var21 = new EntityLavaFX(this.theWorld, posX, posY, posZ);
               } else if (enum_particle == EnumParticle.footstep) {
                  var21 = new EntityFootStepFX(this.renderEngine, this.theWorld, posX, posY, posZ);
               } else if (enum_particle == EnumParticle.splash) {
                  var21 = new EntitySplashFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.largesmoke) {
                  var21 = new EntitySmokeFX(this.theWorld, posX, posY, posZ, par8, par10, par12, 2.5F);
               } else if (enum_particle == EnumParticle.cloud) {
                  var21 = new EntityCloudFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.reddust) {
                  var21 = new EntityReddustFX(this.theWorld, posX, posY, posZ, (float)par8, (float)par10, (float)par12);
               } else if (enum_particle == EnumParticle.snowballpoof) {
                  var21 = new EntityBreakingFX(this.theWorld, posX, posY, posZ, Item.snowball);
               } else if (enum_particle == EnumParticle.brickpoof) {
                  var21 = new EntityBreakingFX(this.theWorld, posX, posY, posZ, Item.brick);
               } else if (enum_particle == EnumParticle.brickpoof_netherrack) {
                  var21 = new EntityBreakingFX(this.theWorld, posX, posY, posZ, Item.netherrackBrick);
               } else if (enum_particle == EnumParticle.dripWater) {
                  var21 = new EntityDropParticleFX(this.theWorld, posX, posY, posZ, Material.water);
               } else if (enum_particle == EnumParticle.dripLava) {
                  var21 = new EntityDropParticleFX(this.theWorld, posX, posY, posZ, Material.lava);
               } else if (enum_particle == EnumParticle.snowshovel) {
                  var21 = new EntitySnowShovelFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.slime) {
                  var21 = new EntityBreakingFX(this.theWorld, posX, posY, posZ, Item.slimeBall);
               } else if (enum_particle == EnumParticle.ochre_jelly) {
                  var21 = new EntityBreakingFX(this.theWorld, posX, posY, posZ, Item.slimeBall, 1);
               } else if (enum_particle == EnumParticle.crimson_blob) {
                  var21 = new EntityBreakingFX(this.theWorld, posX, posY, posZ, Item.slimeBall, 2);
               } else if (enum_particle == EnumParticle.gray_ooze) {
                  var21 = new EntityBreakingFX(this.theWorld, posX, posY, posZ, Item.slimeBall, 3);
               } else if (enum_particle == EnumParticle.black_pudding) {
                  var21 = new EntityBreakingFX(this.theWorld, posX, posY, posZ, Item.slimeBall, 4);
               } else if (enum_particle == EnumParticle.heart) {
                  var21 = new EntityHeartFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
               } else if (enum_particle == EnumParticle.vampiric_gain) {
                  var21 = new EntityHeartFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
                  ((EntityFX)var21).setParticleTextureIndex(8);
               } else if (enum_particle == EnumParticle.angryVillager) {
                  var21 = new EntityHeartFX(this.theWorld, posX, posY + 0.5, posZ, par8, par10, par12);
                  ((EntityFX)var21).setParticleTextureIndex(81);
                  ((EntityFX)var21).setRBGColorF(1.0F, 1.0F, 1.0F);
               } else if (enum_particle == EnumParticle.happyVillager) {
                  var21 = new EntityAuraFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
                  ((EntityFX)var21).setParticleTextureIndex(82);
                  ((EntityFX)var21).setRBGColorF(1.0F, 1.0F, 1.0F);
               } else if (enum_particle == EnumParticle.manure) {
                  var21 = new EntityAuraFX(this.theWorld, posX, posY, posZ, par8, par10, par12);
                  ((EntityFX)var21).setParticleTextureIndex(98);
                  ((EntityFX)var21).setRBGColorF(0.4F, 0.3F, 0.2F);
               } else if (enum_particle == EnumParticle.repair) {
                  var21 = new EntityRepairFX(this.theWorld, posX, posY, posZ, 0.0, 0.0024999999441206455, 0.0);
                  ((EntityFX)var21).setParticleTextureIndex(99);
                  ((EntityFX)var21).setRBGColorF((float)par8, (float)par10, (float)par12);
               } else if (enum_particle == EnumParticle.sacred) {
                  var21 = new EntitySacredFX(this.theWorld, posX, posY, posZ);
               } else if (enum_particle == EnumParticle.iconcrack) {
                  var21 = new EntityBreakingFX(this.theWorld, posX, posY, posZ, par8, par10, par12, Item.itemsList[index], metadata);
               } else if (enum_particle == EnumParticle.tilecrack) {
                  var21 = (new EntityDiggingFX(this.theWorld, posX, posY, posZ, par8, par10, par12, Block.blocksList[index], metadata)).applyRenderColor(metadata);
               } else if (enum_particle == EnumParticle.crafting) {
                  var21 = new EntityBreakingFX(this.theWorld, posX, posY, posZ, par8, par10, par12, Item.itemsList[index], metadata);

                  for(((EntityFX)var21).particleMaxAge += 20; ((EntityFX)var21).particleScale > 0.7F; ((EntityFX)var21).particleScale = (RNG.float_1[++RNG.random_number_index & 32767] + 1.0F) * 0.5F) {
                  }
               }

               if (var21 != null) {
                  this.mc.effectRenderer.addEffect((EntityFX)var21);
               }

               return (EntityFX)var21;
            }
         }
      } else {
         return null;
      }
   }

   public void onEntityCreate(Entity par1Entity) {
   }

   public void onEntityDestroy(Entity par1Entity) {
   }

   public void deleteAllDisplayLists() {
      GLAllocation.deleteDisplayLists(this.glRenderListBase);
   }

   public void broadcastSound(int par1, int par2, int par3, int par4, int par5) {
      Random var6 = this.theWorld.rand;
      switch (par1) {
         case 1013:
         case 1018:
            if (this.mc.renderViewEntity != null) {
               double var7 = (double)par2 - this.mc.renderViewEntity.posX;
               double var9 = (double)par3 - this.mc.renderViewEntity.posY;
               double var11 = (double)par4 - this.mc.renderViewEntity.posZ;
               double var13 = Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
               double var15 = this.mc.renderViewEntity.posX;
               double var17 = this.mc.renderViewEntity.posY;
               double var19 = this.mc.renderViewEntity.posZ;
               if (var13 > 0.0) {
                  var15 += var7 / var13 * 2.0;
                  var17 += var9 / var13 * 2.0;
                  var19 += var11 / var13 * 2.0;
               }

               if (par1 == 1013) {
                  this.theWorld.playSound(var15, var17, var19, "mob.wither.spawn", 1.0F, 1.0F, false);
               } else if (par1 == 1018) {
                  this.theWorld.playSound(var15, var17, var19, "mob.enderdragon.end", 5.0F, 1.0F, false);
               }
            }
         default:
      }
   }

   public void playAuxSFX(EntityPlayer player, int id, int x, int y, int z, int data) {
      Random var7 = this.theWorld.rand;
      double var8;
      double var10;
      double var12;
      int var15;
      double var23;
      double var25;
      double var27;
      double var29;
      double var39;
      switch (id) {
         case 1000:
            this.theWorld.playSound((double)x, (double)y, (double)z, "random.click", 1.0F, 1.0F, false);
            break;
         case 1001:
            this.theWorld.playSound((double)x, (double)y, (double)z, "random.click", 1.0F, 1.2F, false);
            break;
         case 1002:
            this.theWorld.playSound((double)x, (double)y, (double)z, "random.bow", 1.0F, 1.2F, false);
            break;
         case 1003:
            if (Math.random() < 0.5) {
               this.theWorld.playSound((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.door_open", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            } else {
               this.theWorld.playSound((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.door_close", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            }
            break;
         case 1004:
            this.theWorld.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.5F, 2.6F + (var7.nextFloat() - var7.nextFloat()) * 0.8F, false);
            break;
         case 1005:
            if (Item.itemsList[data] instanceof ItemRecord) {
               this.theWorld.playRecord(((ItemRecord)Item.itemsList[data]).recordName, x, y, z);
            } else {
               this.theWorld.playRecord((String)null, x, y, z);
            }
            break;
         case 1007:
            this.theWorld.playSound((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "mob.ghast.charge", 10.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1008:
            this.theWorld.playSound((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "mob.ghast.fireball", 10.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1009:
            this.theWorld.playSound((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "mob.ghast.fireball", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1010:
            this.theWorld.playSound((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "mob.zombie.wood", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1011:
            this.theWorld.playSound((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "mob.zombie.metal", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1012:
            this.theWorld.playSound((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "mob.zombie.woodbreak", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1014:
            this.theWorld.playSound((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "mob.wither.shoot", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1015:
            this.theWorld.playSound((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "mob.bat.takeoff", 0.05F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1016:
            this.theWorld.playSound((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "mob.zombie.infect", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1017:
            this.theWorld.playSound((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "mob.zombie.unfect", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1020:
            this.theWorld.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.anvil_break", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1021:
            this.theWorld.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.anvil_use", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1022:
            this.theWorld.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.anvil_land", 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2000:
            int var33 = data % 3 - 1;
            int var9 = data / 3 % 3 - 1;
            var10 = (double)x + (double)var33 * 0.6 + 0.5;
            var12 = (double)y + 0.5;
            double var34 = (double)z + (double)var9 * 0.6 + 0.5;

            for(int var35 = 0; var35 < 10; ++var35) {
               double var37 = var7.nextDouble() * 0.2 + 0.01;
               double var38 = var10 + (double)var33 * 0.01 + (var7.nextDouble() - 0.5) * (double)var9 * 0.5;
               var39 = var12 + (var7.nextDouble() - 0.5) * 0.5;
               var23 = var34 + (double)var9 * 0.01 + (var7.nextDouble() - 0.5) * (double)var33 * 0.5;
               var25 = (double)var33 * var37 + var7.nextGaussian() * 0.01;
               var27 = -0.03 + var7.nextGaussian() * 0.01;
               var29 = (double)var9 * var37 + var7.nextGaussian() * 0.01;
               this.spawnParticle(EnumParticle.smoke, var38, var39, var23, var25, var27, var29);
            }

            return;
         case 2001:
            Block block = Block.getBlock(data & 4095);
            if (block != null) {
               int original_data = data;
               boolean was_silk_harvested = BitHelper.isBitSet(data, SFX_2001_WAS_SILK_HARVESTED);
               data = BitHelper.clearBit(data, SFX_2001_WAS_SILK_HARVESTED);
               boolean suppress_sound = BitHelper.isBitSet(data, SFX_2001_SUPPRESS_SOUND);
               data = BitHelper.clearBit(data, SFX_2001_SUPPRESS_SOUND);
               boolean was_not_legal = BitHelper.isBitSet(data, SFX_2001_WAS_NOT_LEGAL);
               data = BitHelper.clearBit(data, SFX_2001_WAS_NOT_LEGAL);
               boolean use_special_snow_particles = block == Block.snow && was_not_legal;
               boolean use_special_portal_damage_particles = BitHelper.isBitSet(data, SFX_2001_FOR_AI_BREAK_DOOR);
               data = BitHelper.clearBit(data, SFX_2001_FOR_AI_BREAK_DOOR);
               StepSound step_sound = block.stepSound;
               String break_sound = step_sound == null ? null : step_sound.getBreakSound();
               if (was_silk_harvested && "random.glass".equals(break_sound)) {
                  step_sound = Block.stone.stepSound;
                  break_sound = step_sound.getBreakSound();
               }

               if (!suppress_sound) {
                  this.mc.sndManager.playSound(break_sound, (float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, (step_sound.getVolume() + 1.0F) / 2.0F, step_sound.getPitch() * 0.8F);
               }

               if (!was_silk_harvested || block.blockMaterial != Material.ice && block.blockMaterial != Material.glass) {
                  int metadata = data >> 12 & 15;
                  if (use_special_snow_particles) {
                     this.mc.effectRenderer.addBlockDestroyEffectsForSnow(x, y, z, block.blockID, metadata);
                  } else if (use_special_portal_damage_particles) {
                     this.mc.effectRenderer.addBlockDestroyEffectsForPortalDamage(x, y, z, block.blockID, metadata);
                  } else {
                     this.mc.effectRenderer.addBlockDestroyEffects(x, y, z, block.blockID, metadata, original_data);
                  }

                  if (block instanceof IBlockWithPartner && !BitHelper.isBitSet(data, SFX_2001_FOR_PARTNER_BLOCK) && block.isPartnerPresent(this.theWorld, x, y, z)) {
                     x = block.getPartnerX(x, metadata);
                     y = block.getPartnerY(y, metadata);
                     z = block.getPartnerZ(z, metadata);
                     Block partner_block = this.theWorld.getBlock(x, y, z);
                     if (partner_block instanceof IBlockWithPartner) {
                        int partner_block_metadata = this.theWorld.getBlockMetadata(x, y, z);
                        if (((IBlockWithPartner)partner_block).requiresPartner(partner_block_metadata)) {
                           data = partner_block.blockID + (partner_block_metadata << 12) | BitHelper.clearBit(original_data, BitHelper.getBitValue(16) - 1);
                           this.playAuxSFX(player, id, x, y, z, data | SFX_2001_SUPPRESS_SOUND | SFX_2001_FOR_PARTNER_BLOCK);
                        }
                     }
                  }
               }
            }
            break;
         case 2002:
            var8 = (double)x;
            var10 = (double)y;
            var12 = (double)z;

            for(var15 = 0; var15 < 8; ++var15) {
               this.spawnParticleEx(EnumParticle.iconcrack, Item.potion.itemID, data, var8, var10, var12, var7.nextGaussian() * 0.15, var7.nextDouble() * 0.2, var7.nextGaussian() * 0.15);
            }

            var15 = Item.potion.getColorFromDamage(data);
            float var16 = (float)(var15 >> 16 & 255) / 255.0F;
            float var17 = (float)(var15 >> 8 & 255) / 255.0F;
            float var18 = (float)(var15 >> 0 & 255) / 255.0F;
            EnumParticle enum_particle = Item.potion.isEffectInstant(data) ? EnumParticle.instantSpell : EnumParticle.spell;

            for(int var20 = 0; var20 < 100; ++var20) {
               var39 = var7.nextDouble() * 4.0;
               var23 = var7.nextDouble() * Math.PI * 2.0;
               var25 = Math.cos(var23) * var39;
               var27 = 0.01 + var7.nextDouble() * 0.5;
               var29 = Math.sin(var23) * var39;
               EntityFX var31 = this.doSpawnParticle(enum_particle, 0, 0, var8 + var25 * 0.1, var10 + 0.3, var12 + var29 * 0.1, var25, var27, var29);
               if (var31 != null) {
                  float var32 = 0.75F + var7.nextFloat() * 0.25F;
                  var31.setRBGColorF(var16 * var32, var17 * var32, var18 * var32);
                  var31.multiplyVelocity((float)var39);
               }
            }

            this.theWorld.playSound((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.glass", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2003:
            var8 = (double)x + 0.5;
            var10 = (double)y;
            var12 = (double)z + 0.5;

            for(var15 = 0; var15 < 8; ++var15) {
               this.spawnParticleEx(EnumParticle.iconcrack, Item.eyeOfEnder.itemID, 0, var8, var10, var12, var7.nextGaussian() * 0.15, var7.nextDouble() * 0.2, var7.nextGaussian() * 0.15);
            }

            for(double var36 = 0.0; var36 < 6.283185307179586; var36 += 0.15707963267948966) {
               this.spawnParticle(EnumParticle.portal_underworld, var8 + Math.cos(var36) * 5.0, var10 - 0.4, var12 + Math.sin(var36) * 5.0, Math.cos(var36) * -5.0, 0.0, Math.sin(var36) * -5.0);
               this.spawnParticle(EnumParticle.portal_underworld, var8 + Math.cos(var36) * 5.0, var10 - 0.4, var12 + Math.sin(var36) * 5.0, Math.cos(var36) * -7.0, 0.0, Math.sin(var36) * -7.0);
            }

            return;
         case 2004:
            for(int var21 = 0; var21 < 20; ++var21) {
               double var22 = (double)x + 0.5 + ((double)this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               double var24 = (double)y + 0.5 + ((double)this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               double var26 = (double)z + 0.5 + ((double)this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               this.theWorld.spawnParticle(EnumParticle.smoke, var22, var24, var26, 0.0, 0.0, 0.0);
               this.theWorld.spawnParticle(EnumParticle.flame, var22, var24, var26, 0.0, 0.0, 0.0);
            }

            return;
         case 2005:
            ItemDye.func_96603_a(this.theWorld, x, y, z, data);
            return;
         default:
            Minecraft.setErrorMessage("playAuxSFX: id " + id + " not handled");
      }

   }

   public void destroyBlockPartially(int destroyer_entity_id, int x, int y, int z, int tenths_destroyed) {
      if (tenths_destroyed == -2) {
         Set set = this.damagedBlocks.entrySet();
         Iterator i = set.iterator();

         while(i.hasNext()) {
            Map.Entry entry = (Map.Entry)i.next();
            DestroyBlockProgress dbp = (DestroyBlockProgress)entry.getValue();
            if (dbp.getPartialBlockX() == x && dbp.getPartialBlockY() == y && dbp.getPartialBlockZ() == z) {
               i.remove();
            }
         }

      } else {
         if (tenths_destroyed >= 0 && tenths_destroyed < 10) {
            DestroyBlockProgress destroy_block_progress = (DestroyBlockProgress)this.damagedBlocks.get(destroyer_entity_id);
            if (destroy_block_progress == null || destroy_block_progress.getPartialBlockX() != x || destroy_block_progress.getPartialBlockY() != y || destroy_block_progress.getPartialBlockZ() != z) {
               destroy_block_progress = new DestroyBlockProgress(destroyer_entity_id, x, y, z);
               this.damagedBlocks.put(destroyer_entity_id, destroy_block_progress);
            }

            destroy_block_progress.setPartialBlockDamage(tenths_destroyed);
            destroy_block_progress.setCloudUpdateTick(this.cloudTickCounter);
         } else {
            this.damagedBlocks.remove(destroyer_entity_id);
         }

      }
   }

   public void clearPartialBlockDamage() {
      this.damagedBlocks.clear();
   }

   public void registerDestroyBlockIcons(IconRegister par1IconRegister) {
      this.destroyBlockIcons = new Icon[10];

      for(int var2 = 0; var2 < this.destroyBlockIcons.length; ++var2) {
         this.destroyBlockIcons[var2] = par1IconRegister.registerIcon("destroy_stage_" + var2);
      }

   }
}
