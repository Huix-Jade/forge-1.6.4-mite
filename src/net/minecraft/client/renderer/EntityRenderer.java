package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.raycast.RaycastPolicies;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumEntityReachContext;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public final class EntityRenderer {
   private static final ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
   private static final ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
   public static boolean anaglyphEnable;
   public static int anaglyphField;
   private Minecraft mc;
   private float farPlaneDistance;
   public ItemRenderer itemRenderer;
   private int rendererUpdateCount;
   private Entity pointedEntity;
   private MouseFilter mouseFilterXAxis = new MouseFilter();
   private MouseFilter mouseFilterYAxis = new MouseFilter();
   private MouseFilter mouseFilterDummy1 = new MouseFilter();
   private MouseFilter mouseFilterDummy2 = new MouseFilter();
   private MouseFilter mouseFilterDummy3 = new MouseFilter();
   private MouseFilter mouseFilterDummy4 = new MouseFilter();
   private float thirdPersonDistance = 4.0F;
   private float thirdPersonDistanceTemp = 4.0F;
   private float debugCamYaw;
   private float prevDebugCamYaw;
   private float debugCamPitch;
   private float prevDebugCamPitch;
   private float smoothCamYaw;
   private float smoothCamPitch;
   private float smoothCamFilterX;
   private float smoothCamFilterY;
   private float smoothCamPartialTicks;
   private float debugCamFOV;
   private float prevDebugCamFOV;
   private float camRoll;
   private float prevCamRoll;
   private final DynamicTexture lightmapTexture;
   private final int[] lightmapColors;
   private final ResourceLocation locationLightMap;
   private float fovModifierHand;
   private float fovModifierHandPrev;
   private float fovMultiplierTemp;
   private float field_82831_U;
   private float field_82832_V;
   private boolean cloudFog;
   private double cameraZoom = 1.0;
   private double cameraYaw;
   private double cameraPitch;
   private long prevFrameTime = Minecraft.getSystemTime();
   private long renderEndNanoTime;
   private boolean lightmapUpdateNeeded;
   float torchFlickerX;
   float torchFlickerDX;
   float torchFlickerY;
   float torchFlickerDY;
   private Random random = new Random();
   private int rainSoundCounter;
   float[] rainXCoords;
   float[] rainYCoords;
   FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
   float fogColorRed;
   float fogColorGreen;
   float fogColorBlue;
   private float fogColor2;
   private float fogColor1;
   public int debugViewDirection;
   private double[] x = new double[4];
   private double[] y = new double[4];
   private double[] z = new double[4];
   private double[] u = new double[4];
   private double[] v = new double[4];
   private float[] r = new float[4];
   private float[] g = new float[4];
   private float[] b = new float[4];
   private int[] brightness = new int[4];
   private static final boolean capability_gl_nv_fog_distance;
   private long last_vsync_nanotime = -1L;
   private long fps_start_time = -1L;
   private int fps_counter;
   private long fp10s_start_time = -1L;
   private int fp10s_counter;
   private int last_precipitation_type;
   private float skylight_brightness_used_for_fog;
   private static final int fog_post_cutoff_distance = 1024;
   private static final int fog_post_field_chunk_range = 65;
   private static final int fog_post_field_size = 131;
   private static final int fog_post_field_num_chunks = 17161;
   private static List fog_post_list;
   private static World last_fog_post_field_generation_viewer_world;
   private static int last_fog_post_field_generation_viewer_chunk_x;
   private static int last_fog_post_field_generation_viewer_chunk_z;
   public static boolean disable_fog;
   private static Random random_for_fog_events;
   private static double distance_from_biome_that_can_be_foggy;
   private static long distance_from_biome_that_can_be_foggy_tick;
   private static double distance_from_biome_that_can_be_foggy_last_viewer_pos_x;
   private static double distance_from_biome_that_can_be_foggy_last_viewer_pos_z;
   private static int distance_from_biome_that_can_be_foggy_last_viewer_x;
   private static int distance_from_biome_that_can_be_foggy_last_viewer_z;
   private static World distance_from_biome_that_can_be_foggy_last_viewer_world;
   private static boolean[] is_fog_supporting_biome;
   private static boolean is_fog_supporting_biome_contains_at_least_one;

   public static void clearWorldSessionClientData() {
      distance_from_biome_that_can_be_foggy_tick = -1L;
      distance_from_biome_that_can_be_foggy_last_viewer_world = null;
   }

   public EntityRenderer(Minecraft par1Minecraft) {
      this.mc = par1Minecraft;
      this.itemRenderer = new ItemRenderer(par1Minecraft);
      this.lightmapTexture = new DynamicTexture(16, 16);
      this.locationLightMap = par1Minecraft.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
      this.lightmapColors = this.lightmapTexture.getTextureData();
   }

   public void updateRenderer() {
      this.updateFovModifierHand();
      this.updateTorchFlicker();
      this.fogColor2 = this.fogColor1;
      this.thirdPersonDistanceTemp = this.thirdPersonDistance;
      this.prevDebugCamYaw = this.debugCamYaw;
      this.prevDebugCamPitch = this.debugCamPitch;
      this.prevDebugCamFOV = this.debugCamFOV;
      this.prevCamRoll = this.camRoll;
      float var1;
      float var2;
      if (this.mc.gameSettings.smoothCamera) {
         var1 = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
         var2 = var1 * var1 * var1 * 8.0F;
         this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * var2);
         this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * var2);
         this.smoothCamPartialTicks = 0.0F;
         this.smoothCamYaw = 0.0F;
         this.smoothCamPitch = 0.0F;
      }

      if (this.mc.renderViewEntity == null) {
         this.mc.renderViewEntity = this.mc.thePlayer;
      }

      var1 = this.mc.theWorld.getLightBrightness(MathHelper.floor_double(this.mc.renderViewEntity.posX), MathHelper.floor_double(this.mc.renderViewEntity.posY), MathHelper.floor_double(this.mc.renderViewEntity.posZ));
      var2 = (float)(3 - this.mc.gameSettings.getRenderDistance()) / 3.0F;
      float var3 = var1 * (1.0F - var2) + var2;
      this.fogColor1 += (var3 - this.fogColor1) * 0.1F;
      ++this.rendererUpdateCount;
      this.itemRenderer.updateEquippedItem();
      this.addRainParticles();
      this.field_82832_V = this.field_82831_U;
      if (BossStatus.field_82825_d) {
         this.field_82831_U += 0.05F;
         if (this.field_82831_U > 1.0F) {
            this.field_82831_U = 1.0F;
         }

         BossStatus.field_82825_d = false;
      } else if (this.field_82831_U > 0.0F) {
         this.field_82831_U -= 0.0125F;
      }

   }

   public static void setDebugInfoForSelectedObject(RaycastCollision rc, EntityPlayer player) {
      if (rc != null) {
         if (rc.isEntity()) {
            Entity entity = rc.getEntityHit();
            Debug.selected_object_info = "Entity Selected: " + entity.getEntityName() + ", id=" + entity.entityId;
         } else if (rc.isBlock()) {
            Block block = Block.blocksList[player.worldObj.getBlockId(rc.block_hit_x, rc.block_hit_y, rc.block_hit_z)];
            float block_hardness = player.worldObj.getBlockHardness(rc.block_hit_x, rc.block_hit_y, rc.block_hit_z);
            if (Minecraft.inDevMode()) {
               int metadata = player.worldObj.getBlockMetadata(rc.block_hit_x, rc.block_hit_y, rc.block_hit_z);
               int min_harvest_level = block.getMinHarvestLevel(metadata);
               if (min_harvest_level == 0) {
                  Debug.selected_object_info = block.getLocalizedName() + " (" + block.blockID + ":" + metadata + "), hardness=" + block_hardness + ", strVs=" + player.getCurrentPlayerStrVsBlock(rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, true);
               } else {
                  Debug.selected_object_info = block.getLocalizedName() + " (" + block.blockID + ":" + metadata + "), hardness=" + block_hardness + ", MinHL=" + min_harvest_level + ", strVs=" + player.getCurrentPlayerStrVsBlock(rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, true);
               }

               if (!rc.isNeighborAirBlock()) {
                  Debug.selected_object_info = Debug.selected_object_info + ", neighbor (" + rc.getNeighborOfBlockHitID() + ":" + rc.getNeighborOfBlockHitMetadata() + ")";
               }
            }
         }
      } else {
         Debug.selected_object_info = "No object selected";
      }

   }

   public void getMouseOver(float partial_tick) {
      if (this.mc.renderViewEntity != null && this.mc.theWorld != null) {
         if (this.mc.renderViewEntity instanceof EntityPlayer && this.mc.theWorld != null) {
            EntityPlayer player = (EntityPlayer) this.mc.renderViewEntity;
            RaycastCollision rc = player.getSelectedObject(partial_tick, false);
            this.mc.objectMouseOver = rc;
            this.pointedEntity = null;
            this.mc.pointedEntityLiving = null;
            if (rc != null && rc.isEntity()) {
               this.pointedEntity = rc.getEntityHit();
               if (this.pointedEntity instanceof EntityLivingBase) {
                  this.mc.pointedEntityLiving = (EntityLivingBase)this.pointedEntity;
               }
            }

            if (Minecraft.inDevMode()) {
               setDebugInfoForSelectedObject(player.getSelectedObject(partial_tick, false, true, (EnumEntityReachContext)null), player);
            }
         } else {
            Minecraft.setErrorMessage("getMouseOver: cannot handle non EntityPlayer entities");
            this.mc.objectMouseOver = null;
            this.pointedEntity = null;
            this.mc.pointedEntityLiving = null;
         }

      }
   }

   private void updateFovModifierHand() {
      if (mc.renderViewEntity instanceof EntityPlayerSP)
      {
         EntityPlayerSP entityplayersp = (EntityPlayerSP)this.mc.renderViewEntity;
         this.fovMultiplierTemp = entityplayersp.getFOVMultiplier();
      }
      else
      {
         this.fovMultiplierTemp = mc.thePlayer.getFOVMultiplier();
      }
      this.fovModifierHandPrev = this.fovModifierHand;
      this.fovModifierHand += (this.fovMultiplierTemp - this.fovModifierHand) * 0.5F;
      if (this.fovModifierHand > 1.5F) {
         this.fovModifierHand = 1.5F;
      }

      if (this.fovModifierHand < 0.1F) {
         this.fovModifierHand = 0.1F;
      }

   }

   private float getFOVModifier(float par1, boolean par2) {
      if (this.debugViewDirection > 0) {
         return 90.0F;
      } else {
         EntityLivingBase var3 = (EntityLivingBase)this.mc.renderViewEntity;
         float var4 = 70.0F;
         if (par2) {
            var4 += this.mc.gameSettings.fovSetting * 40.0F;
            var4 *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * par1;
         }

         if (var3.getHealth() <= 0.0F) {
            float var5 = (float)var3.deathTime + par1;
            var4 /= (1.0F - 500.0F / (var5 + 500.0F)) * 2.0F + 1.0F;
         }

         int var6 = ActiveRenderInfo.getBlockIdAtEntityViewpoint(this.mc.theWorld, var3, par1);
         if (var6 != 0 && Block.blocksList[var6].blockMaterial == Material.water) {
            var4 = var4 * 60.0F / 70.0F;
         }

         return var4 + this.prevDebugCamFOV + (this.debugCamFOV - this.prevDebugCamFOV) * par1;
      }
   }

   private void hurtCameraEffect(float par1) {
      EntityLivingBase var2 = this.mc.renderViewEntity;
      float var3 = (float)var2.hurtTime - par1;
      float var4;
      if (var2.getHealth() <= 0.0F) {
         var4 = (float)var2.deathTime + par1;
         GL11.glRotatef(40.0F - 8000.0F / (var4 + 200.0F), 0.0F, 0.0F, 1.0F);
      }

      if (var3 >= 0.0F) {
         var3 /= (float)var2.maxHurtTime;
         var3 = MathHelper.sin(var3 * var3 * var3 * var3 * 3.1415927F);
         var4 = var2.attackedAtYaw;
         GL11.glRotatef(-var4, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(-var3 * 14.0F, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(var4, 0.0F, 1.0F, 0.0F);
      }

   }

   private void setupViewBobbing(float par1) {
      if (this.mc.renderViewEntity instanceof EntityPlayer) {
         EntityPlayer var2 = (EntityPlayer) this.mc.renderViewEntity;

         float var3 = var2.distanceWalkedModified - var2.prevDistanceWalkedModified;
         float var4 = -(var2.distanceWalkedModified + var3 * par1);
         float var5 = var2.prevCameraYaw + (var2.cameraYaw - var2.prevCameraYaw) * par1;
         float var6 = var2.prevCameraPitch + (var2.cameraPitch - var2.prevCameraPitch) * par1;
         GL11.glTranslatef(MathHelper.sin(var4 * 3.1415927F) * var5 * 0.5F, -Math.abs(MathHelper.cos(var4 * 3.1415927F) * var5), 0.0F);
         GL11.glRotatef(MathHelper.sin(var4 * 3.1415927F) * var5 * 3.0F, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(Math.abs(MathHelper.cos(var4 * 3.1415927F - 0.2F) * var5) * 5.0F, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(var6, 1.0F, 0.0F, 0.0F);
      }

   }

   private void orientCamera(float par1) {
      EntityLivingBase var2 = this.mc.renderViewEntity;
      float var3 = var2.yOffset - 1.62F;
      if (this.mc.thePlayer.isSneaking()) {
         var3 -= -0.2385F;
      }

      double var4 = var2.prevPosX + (var2.posX - var2.prevPosX) * (double)par1;
      double var6 = var2.prevPosY + (var2.posY - var2.prevPosY) * (double)par1 - (double)var3;
      double var8 = var2.prevPosZ + (var2.posZ - var2.prevPosZ) * (double)par1;
      GL11.glRotatef(this.prevCamRoll + (this.camRoll - this.prevCamRoll) * par1, 0.0F, 0.0F, 1.0F);
      if (var2.inBed()) {
         var3 = (float)((double)var3 + 1.0);
         GL11.glTranslatef(0.0F, 0.3F, 0.0F);
         if (!this.mc.gameSettings.debugCamEnable) {
            ForgeHooksClient.orientBedCamera(mc, var2);

            GL11.glRotatef(var2.prevRotationYaw + (var2.rotationYaw - var2.prevRotationYaw) * par1 + 180.0F, 0.0F, -1.0F, 0.0F);
            GL11.glRotatef(var2.prevRotationPitch + (var2.rotationPitch - var2.prevRotationPitch) * par1, -1.0F, 0.0F, 0.0F);
         }
      } else if (this.mc.gameSettings.thirdPersonView > 0) {
         double var27 = (double)(this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * par1);
         float var28;
         float var13;
         if (this.mc.gameSettings.debugCamEnable) {
            var28 = this.prevDebugCamYaw + (this.debugCamYaw - this.prevDebugCamYaw) * par1;
            var13 = this.prevDebugCamPitch + (this.debugCamPitch - this.prevDebugCamPitch) * par1;
            GL11.glTranslatef(0.0F, 0.0F, (float)(-var27));
            GL11.glRotatef(var13, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(var28, 0.0F, 1.0F, 0.0F);
         } else {
            var28 = var2.rotationYaw;
            var13 = var2.rotationPitch;
            if (this.mc.gameSettings.thirdPersonView == 2) {
               var13 += 180.0F;
            }

            double var14 = (double)(-MathHelper.sin(var28 / 180.0F * 3.1415927F) * MathHelper.cos(var13 / 180.0F * 3.1415927F)) * var27;
            double var16 = (double)(MathHelper.cos(var28 / 180.0F * 3.1415927F) * MathHelper.cos(var13 / 180.0F * 3.1415927F)) * var27;
            double var18 = (double)(-MathHelper.sin(var13 / 180.0F * 3.1415927F)) * var27;

            for(int var20 = 0; var20 < 8; ++var20) {
               float var21 = (float)((var20 & 1) * 2 - 1);
               float var22 = (float)((var20 >> 1 & 1) * 2 - 1);
               float var23 = (float)((var20 >> 2 & 1) * 2 - 1);
               var21 *= 0.1F;
               var22 *= 0.1F;
               var23 *= 0.1F;
               RaycastCollision var24 = this.mc.theWorld.getBlockCollisionForPolicies(this.mc.theWorld.getVec3(var4 + (double)var21, var6 + (double)var22, var8 + (double)var23), this.mc.theWorld.getVec3(var4 - var14 + (double)var21 + (double)var23, var6 - var18 + (double)var22, var8 - var16 + (double)var23), RaycastPolicies.for_third_person_view);
               if (var24 != null) {
                  double var25 = var24.position_hit.distanceTo(this.mc.theWorld.getWorldVec3Pool().getVecFromPool(var4, var6, var8));
                  if (var25 < var27) {
                     var27 = var25;
                  }
               }
            }

            if (this.mc.gameSettings.thirdPersonView == 2) {
               GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GL11.glRotatef(var2.rotationPitch - var13, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(var2.rotationYaw - var28, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, 0.0F, (float)(-var27));
            GL11.glRotatef(var28 - var2.rotationYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(var13 - var2.rotationPitch, 1.0F, 0.0F, 0.0F);
         }
      } else {
         GL11.glTranslatef(0.0F, 0.0F, -0.1F);
      }

      if (!this.mc.gameSettings.debugCamEnable) {
         GL11.glRotatef(var2.prevRotationPitch + (var2.rotationPitch - var2.prevRotationPitch) * par1, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(var2.prevRotationYaw + (var2.rotationYaw - var2.prevRotationYaw) * par1 + 180.0F, 0.0F, 1.0F, 0.0F);
      }

      GL11.glTranslatef(0.0F, var3, 0.0F);
      var4 = var2.prevPosX + (var2.posX - var2.prevPosX) * (double)par1;
      var6 = var2.prevPosY + (var2.posY - var2.prevPosY) * (double)par1 - (double)var3;
      var8 = var2.prevPosZ + (var2.posZ - var2.prevPosZ) * (double)par1;
      this.cloudFog = this.mc.renderGlobal.hasCloudFog(var4, var6, var8, par1);
   }

   public void setupCameraTransform(float par1, int par2, boolean extend_far_clipping_plane) {
      this.farPlaneDistance = (float)(extend_far_clipping_plane ? 384 : 256 >> this.mc.gameSettings.getRenderDistance());
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      float var3 = 0.07F;
      if (this.mc.gameSettings.anaglyph) {
         GL11.glTranslatef((float)(-(par2 * 2 - 1)) * var3, 0.0F, 0.0F);
      }

      if (this.cameraZoom != 1.0) {
         GL11.glTranslatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
         GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0);
      }

      Project.gluPerspective(this.getFOVModifier(par1, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
      float var4;
      if (this.mc.playerController.enableEverythingIsScrewedUpMode()) {
         var4 = 0.6666667F;
         GL11.glScalef(1.0F, var4, 1.0F);
      }

      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      if (this.mc.gameSettings.anaglyph) {
         GL11.glTranslatef((float)(par2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
      }

      this.hurtCameraEffect(par1);
      if (this.mc.gameSettings.viewBobbing) {
         this.setupViewBobbing(par1);
      }

      var4 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * par1;
      int var7;
      if (var4 > 0.0F) {
         var7 = 20;
         if (this.mc.thePlayer.isPotionActive(Potion.confusion) && this.mc.thePlayer.getActivePotionEffect(Potion.confusion).getAmplifier() > 0) {
            var7 = 7;
            var4 *= 0.25F * (float)this.mc.thePlayer.getActivePotionEffect(Potion.confusion).getAmplifier();
         }

         float var6 = 5.0F / (var4 * var4 + 5.0F) - var4 * 0.04F;
         var6 *= var6;
         GL11.glRotatef(((float)this.rendererUpdateCount + par1) * (float)var7, 0.0F, 1.0F, 1.0F);
         GL11.glScalef(1.0F / var6, 1.0F, 1.0F);
         GL11.glRotatef(-((float)this.rendererUpdateCount + par1) * (float)var7, 0.0F, 1.0F, 1.0F);
      }

      this.orientCamera(par1);
      if (this.debugViewDirection > 0) {
         var7 = this.debugViewDirection - 1;
         if (var7 == 1) {
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
         }

         if (var7 == 2) {
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
         }

         if (var7 == 3) {
            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
         }

         if (var7 == 4) {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
         }

         if (var7 == 5) {
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
         }
      }

   }

   private void renderHand(float par1, int par2) {
      if (this.debugViewDirection <= 0) {
         GL11.glMatrixMode(5889);
         GL11.glLoadIdentity();
         float var3 = 0.07F;
         if (this.mc.gameSettings.anaglyph) {
            GL11.glTranslatef((float)(-(par2 * 2 - 1)) * var3, 0.0F, 0.0F);
         }

         if (this.cameraZoom != 1.0) {
            GL11.glTranslatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
            GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0);
         }

         Project.gluPerspective(this.getFOVModifier(par1, false), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
         if (this.mc.playerController.enableEverythingIsScrewedUpMode()) {
            float var4 = 0.6666667F;
            GL11.glScalef(1.0F, var4, 1.0F);
         }

         GL11.glMatrixMode(5888);
         GL11.glLoadIdentity();
         if (this.mc.gameSettings.anaglyph) {
            GL11.glTranslatef((float)(par2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
         }

         GL11.glPushMatrix();
         this.hurtCameraEffect(par1);
         if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(par1);
         }

         if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.inBed() && this.mc.gameSettings.gui_mode != 2 && !this.mc.playerController.enableEverythingIsScrewedUpMode()) {
            this.enableLightmap((double)par1);
            this.itemRenderer.renderItemInFirstPerson(par1);
            this.disableLightmap((double)par1);
         }

         GL11.glPopMatrix();
         if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.inBed()) {
            this.itemRenderer.renderOverlays(par1);
            this.hurtCameraEffect(par1);
         }

         if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(par1);
         }
      }

   }

   public void disableLightmap(double par1) {
      OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GL11.glDisable(3553);
      OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   public void enableLightmap(double par1) {
      OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GL11.glMatrixMode(5890);
      GL11.glLoadIdentity();
      float var3 = 0.00390625F;
      GL11.glScalef(var3, var3, var3);
      GL11.glTranslatef(8.0F, 8.0F, 8.0F);
      GL11.glMatrixMode(5888);
      this.mc.getTextureManager().bindTexture(this.locationLightMap);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10242, 10496);
      GL11.glTexParameteri(3553, 10243, 10496);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(3553);
      OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   private void updateTorchFlicker() {
      if (this.mc.thePlayer.torch_flicker_suppressed) {
         this.lightmapUpdateNeeded = true;
      } else {
         this.torchFlickerDX = (float)((double)this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
         this.torchFlickerDY = (float)((double)this.torchFlickerDY + (Math.random() - Math.random()) * Math.random() * Math.random());
         this.torchFlickerDX = (float)((double)this.torchFlickerDX * 0.9);
         this.torchFlickerDY = (float)((double)this.torchFlickerDY * 0.9);
         this.torchFlickerX += (this.torchFlickerDX - this.torchFlickerX) * 1.0F;
         this.torchFlickerY += (this.torchFlickerDY - this.torchFlickerY) * 1.0F;
         this.lightmapUpdateNeeded = true;
      }
   }

   private void updateLightmap(float par1) {
      WorldClient var2 = this.mc.theWorld;
      if (var2 != null) {
         for(int var3 = 0; var3 < 256; ++var3) {
            float skylight_brightness = (float)(var3 / 16) / 15.0F;
            float blocklight_brightness = (float)(var3 % 16) / 15.0F;
            float var4 = var2.getSunBrightness(1.0F) * 0.95F + 0.05F;
            float var5 = var2.provider.lightBrightnessTable[var3 / 16] * var4;
            float var6 = var2.provider.lightBrightnessTable[var3 % 16] * (this.torchFlickerX * 0.1F + 1.5F);
            float var7;
            if (var2.isOverworld() && var2.getMoonAscensionFactor() > 0.0F) {
               var7 = MathHelper.clamp_float(var2.getMoonAscensionFactor(), 0.0F, 0.2F) * 5.0F;
               var5 = var5 * (1.0F - var7) + var5 * var7 * var2.getMoonBrightness(par1, true);
            }

            if (var2.lastLightningBolt > 0) {
               var7 = (float)Math.pow((double)this.mc.raining_strength_for_render_view_entity, 4.0);
               var5 = var2.provider.lightBrightnessTable[var3 / 16] * var7 + var5 * (1.0F - var7);
            }

            var7 = var5 * (var2.getSunBrightness(1.0F) * 0.65F + 0.35F);
            float var8 = var5 * (var2.getSunBrightness(1.0F) * 0.65F + 0.35F);
            float var11 = var6 * ((var6 * 0.6F + 0.4F) * 0.6F + 0.4F);
            float var12 = var6 * (var6 * var6 * 0.6F + 0.4F);
            float var13 = var7 + var6;
            float var14 = var8 + var11;
            float var15 = var5 + var12;
            var13 = var13 * 0.96F + 0.03F;
            var14 = var14 * 0.96F + 0.03F;
            var15 = var15 * 0.96F + 0.03F;
            float var16;
            if (this.field_82831_U > 0.0F) {
               var16 = this.field_82832_V + (this.field_82831_U - this.field_82832_V) * par1;
               var13 = var13 * (1.0F - var16) + var13 * 0.7F * var16;
               var14 = var14 * (1.0F - var16) + var14 * 0.6F * var16;
               var15 = var15 * (1.0F - var16) + var15 * 0.6F * var16;
            }

            if (var2.provider.dimensionId == 1) {
               var13 = 0.22F + var6 * 0.75F;
               var14 = 0.28F + var11 * 0.75F;
               var15 = 0.25F + var12 * 0.75F;
            }

            float var17;
            if (this.mc.thePlayer.isPotionActive(Potion.nightVision) || Minecraft.night_vision_override) {
               var16 = Minecraft.night_vision_override ? 1.0F : this.getNightVisionBrightness(this.mc.thePlayer, par1);
               var17 = 1.0F / var13;
               if (var17 > 1.0F / var14) {
                  var17 = 1.0F / var14;
               }

               if (var17 > 1.0F / var15) {
                  var17 = 1.0F / var15;
               }

               var13 = var13 * (1.0F - var16) + var13 * var17 * var16;
               var14 = var14 * (1.0F - var16) + var14 * var17 * var16;
               var15 = var15 * (1.0F - var16) + var15 * var17 * var16;
            }

            if (var13 > 1.0F) {
               var13 = 1.0F;
            }

            if (var14 > 1.0F) {
               var14 = 1.0F;
            }

            if (var15 > 1.0F) {
               var15 = 1.0F;
            }

            var16 = this.mc.gameSettings.gammaSetting;
            var17 = 1.0F - var13;
            float var18 = 1.0F - var14;
            float var19 = 1.0F - var15;
            var17 = 1.0F - var17 * var17 * var17 * var17;
            var18 = 1.0F - var18 * var18 * var18 * var18;
            var19 = 1.0F - var19 * var19 * var19 * var19;
            var13 = var13 * (1.0F - var16) + var17 * var16;
            var14 = var14 * (1.0F - var16) + var18 * var16;
            var15 = var15 * (1.0F - var16) + var19 * var16;
            var13 = var13 * 0.96F + 0.03F;
            var14 = var14 * 0.96F + 0.03F;
            var15 = var15 * 0.96F + 0.03F;
            float min;
            if (var2.isBloodMoonNight()) {
               min = MathHelper.clamp_float(1.0F - blocklight_brightness * 2.0F, 0.0F, 1.0F);
               var13 *= 1.0F + 1.0F * MathHelper.clamp_float(var2.getMoonAscensionFactor(), 0.0F, 0.2F) * 5.0F * skylight_brightness * min;
               var14 *= 1.0F + 0.4F * MathHelper.clamp_float(var2.getMoonAscensionFactor(), 0.0F, 0.2F) * 5.0F * skylight_brightness * min;
            } else if (var2.isHarvestMoonNight()) {
               min = MathHelper.clamp_float(1.0F - blocklight_brightness * 2.0F, 0.0F, 1.0F);
               var13 *= 1.0F + 0.6F * MathHelper.clamp_float(var2.getMoonAscensionFactor(), 0.0F, 0.2F) * 4.0F * skylight_brightness * min;
               var14 *= 1.0F + 0.4F * MathHelper.clamp_float(var2.getMoonAscensionFactor(), 0.0F, 0.2F) * 4.0F * skylight_brightness * min;
               var15 *= 1.0F - 0.2F * MathHelper.clamp_float(var2.getMoonAscensionFactor(), 0.0F, 0.2F) * 4.0F * skylight_brightness * min;
            } else if (var2.isBlueMoonNight()) {
               min = MathHelper.clamp_float(1.0F - blocklight_brightness * 2.0F, 0.0F, 1.0F);
               var15 *= 1.0F + 0.5F * MathHelper.clamp_float(var2.getMoonAscensionFactor(), 0.0F, 0.2F) * 2.5F * skylight_brightness * min;
               var14 *= 1.0F + 0.2F * MathHelper.clamp_float(var2.getMoonAscensionFactor(), 0.0F, 0.2F) * 2.5F * skylight_brightness * min;
            }

            if (var13 > 1.0F) {
               var13 = 1.0F;
            }

            if (var14 > 1.0F) {
               var14 = 1.0F;
            }

            if (var15 > 1.0F) {
               var15 = 1.0F;
            }

            if (var13 < 0.0F) {
               var13 = 0.0F;
            }

            if (var14 < 0.0F) {
               var14 = 0.0F;
            }

            if (var15 < 0.0F) {
               var15 = 0.0F;
            }

            if (var3 == 0) {
               min = 0.062F;
               if (var13 < min) {
                  var13 = min;
               }

               if (var14 < min) {
                  var14 = min;
               }

               if (var15 < min) {
                  var15 = min;
               }
            }

            short var20 = 255;
            int var21 = (int)(var13 * 255.0F);
            int var22 = (int)(var14 * 255.0F);
            int var23 = (int)(var15 * 255.0F);
            this.lightmapColors[var3] = var20 << 24 | var21 << 16 | var22 << 8 | var23;
         }

         this.lightmapTexture.updateDynamicTexture();
         this.lightmapUpdateNeeded = false;
      }

   }

   private float getNightVisionBrightness(EntityPlayer par1EntityPlayer, float par2) {
      int var3 = par1EntityPlayer.getActivePotionEffect(Potion.nightVision).getDuration();
      return var3 > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)var3 - par2) * 3.1415927F * 0.2F) * 0.3F;
   }

   public void updateCameraAndRender(float par1) {
      this.mc.mcProfiler.startSection("lightTex");
      if (this.lightmapUpdateNeeded) {
         this.updateLightmap(par1);
      }

      this.mc.mcProfiler.endSection();
      boolean var2 = Display.isActive();
      if (var2 || !this.mc.gameSettings.pauseOnLostFocus || this.mc.gameSettings.touchscreen && Mouse.isButtonDown(1)) {
         this.prevFrameTime = Minecraft.getSystemTime();
      } else if (Minecraft.getSystemTime() - this.prevFrameTime > 500L) {
         this.mc.displayInGameMenu();
      }

      this.mc.mcProfiler.startSection("mouse");
      int var18;
      if (this.mc.inGameHasFocus && var2) {
         this.mc.mouseHelper.mouseXYChange();
         float var3 = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
         float var4 = var3 * var3 * var3 * 8.0F;
         if (this.mc.thePlayer.zoomed) {
            var4 /= 4.0F;
         }

         if (this.mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
         }

         float overall_speed_modifier = this.mc.thePlayer.getSpeedBoostVsSlowDown();
         if (overall_speed_modifier < 0.0F) {
            if (overall_speed_modifier < -0.8F) {
               overall_speed_modifier = -0.8F;
            }

            var4 /= 1.0F - overall_speed_modifier * 15.0F;
         }

         float var5 = (float)this.mc.mouseHelper.deltaX * var4;
         float var6 = (float)this.mc.mouseHelper.deltaY * var4;
         var18 = 1;
         if (this.mc.gameSettings.invertMouse) {
            var18 = -1;
         }

         if (this.mc.gameSettings.smoothCamera) {
            this.smoothCamYaw += var5;
            this.smoothCamPitch += var6;
            float var8 = par1 - this.smoothCamPartialTicks;
            this.smoothCamPartialTicks = par1;
            var5 = this.smoothCamFilterX * var8;
            var6 = this.smoothCamFilterY * var8;
            this.mc.thePlayer.setAngles(var5, var6 * (float)var18);
         } else {
            this.mc.thePlayer.setAngles(var5, var6 * (float)var18);
         }
      }

      this.mc.mcProfiler.endSection();
      if (!this.mc.skipRenderWorld) {
         anaglyphEnable = this.mc.gameSettings.anaglyph;
         ScaledResolution var13 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
         int var14 = var13.getScaledWidth();
         int var15 = var13.getScaledHeight();
         int var16 = Mouse.getX() * var14 / this.mc.displayWidth;
         int var17 = var15 - Mouse.getY() * var15 / this.mc.displayHeight - 1;
         var18 = performanceToFps(this.mc.gameSettings.limitFramerate);
         if (this.mc.theWorld != null && this.mc.theWorld.tick_has_passed) {
            this.mc.mcProfiler.startSection("level");
            if (this.mc.inGameHasFocus || !this.mc.thePlayer.isGhost()) {
               if (this.mc.gameSettings.limitFramerate == 0) {
                  this.renderWorld(par1, 0L);
               } else if (this.mc.gameSettings.limitFramerate == 3) {
                  this.renderWorld(par1, this.renderEndNanoTime + 16666666L);
               } else {
                  this.renderWorld(par1, this.renderEndNanoTime + (long)(1000000000 / var18));
               }
            }

            this.renderEndNanoTime = System.nanoTime();
            this.mc.mcProfiler.endStartSection("gui");
            this.mc.ingameGUI.renderGameOverlay(par1, this.mc.isGuiOpen(), var16, var17);
            this.mc.mcProfiler.endSection();
         } else {
            GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            this.setupOverlayRendering();
            this.renderEndNanoTime = System.nanoTime();
         }

         if (this.mc.isGuiOpen()) {
            GL11.glClear(256);

            try {
               if (this.mc.currentScreen != null) {
                  this.mc.currentScreen.drawScreen(var16, var17, par1);
               }

               if (this.mc.isChatImposed()) {
                  this.mc.imposed_gui_chat.drawScreen(var16, var17, par1);
               }
            } catch (Throwable var12) {
               CrashReport var10 = CrashReport.makeCrashReport(var12, "Rendering screen");
               CrashReportCategory var11 = var10.makeCategory("Screen render details");
               var11.addCrashSectionCallable("Screen name", new CallableScreenName(this));
               var11.addCrashSectionCallable("Mouse location", new CallableMouseLocation(this, var16, var17));
               var11.addCrashSectionCallable("Screen size", new CallableScreenSize(this, var13));
               throw new ReportedException(var10);
            }
         }
      }

   }

   public void renderWorld(float par1, long par2) {
      this.mc.mcProfiler.startSection("lightTex");
      if (this.lightmapUpdateNeeded) {
         this.updateLightmap(par1);
      }

      GL11.glEnable(2884);
      GL11.glEnable(2929);
      if (this.mc.renderViewEntity == null) {
         this.mc.renderViewEntity = this.mc.thePlayer;
      }

      this.mc.mcProfiler.endStartSection("pick");
      this.getMouseOver(par1);
      EntityLivingBase var4 = this.mc.renderViewEntity;
      if (var4 instanceof EntityPlayer) {
         EntityPlayer entity_player = (EntityPlayer)var4;
         if (entity_player.inBed()) {
            entity_player.setPositionAndRotationInBed();
         }
      }

      RenderGlobal var5 = this.mc.renderGlobal;
      EffectRenderer var6 = this.mc.effectRenderer;
      double var7 = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * (double)par1;
      double var9 = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * (double)par1;
      double var11 = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * (double)par1;
      this.mc.mcProfiler.endStartSection("center");

      for(int var13 = 0; var13 < 2; ++var13) {
         if (this.mc.gameSettings.anaglyph) {
            anaglyphField = var13;
            if (anaglyphField == 0) {
               GL11.glColorMask(false, true, true, false);
            } else {
               GL11.glColorMask(true, false, false, false);
            }
         }

         this.mc.mcProfiler.endStartSection("clear");
         GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
         this.updateFogColor(par1);
         GL11.glClear(16640);
         GL11.glEnable(2884);
         this.mc.mcProfiler.endStartSection("camera");
         this.setupCameraTransform(par1, var13, false);
         if (this.last_vsync_nanotime != -1L) {
            long milliseconds_since_last_vsync = (System.nanoTime() - this.last_vsync_nanotime) / 1000000L;
            this.mc.downtimeProcessing(16L - milliseconds_since_last_vsync);
         }

         ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
         this.last_vsync_nanotime = System.nanoTime();
         Minecraft var10000;
         if (this.last_vsync_nanotime > this.fps_start_time + 1000000000L) {
            this.fps_start_time = this.last_vsync_nanotime;
            var10000 = this.mc;
            Minecraft.last_fps = this.fps_counter;
            this.fps_counter = 0;
         } else {
            ++this.fps_counter;
         }

         if (this.last_vsync_nanotime > this.fp10s_start_time + 10000000000L) {
            this.fp10s_start_time = this.last_vsync_nanotime;
            var10000 = this.mc;
            Minecraft.last_fp10s = this.fp10s_counter;
            this.fp10s_counter = 0;
         } else {
            ++this.fp10s_counter;
         }

         this.mc.mcProfiler.endStartSection("frustrum");
         ClippingHelperImpl.getInstance();
         if (this.mc.gameSettings.getRenderDistance() < 2) {
            this.setupFog(-1, par1);
            this.mc.mcProfiler.endStartSection("sky");
            var5.renderSky(par1);
         }

         GL11.glEnable(2912);
         this.setupFog(1, par1);
         if (this.mc.gameSettings.ambientOcclusion != 0) {
            GL11.glShadeModel(7425);
         }

         this.mc.mcProfiler.endStartSection("culling");
         Frustrum var14 = new Frustrum();
         var14.setPosition(var7, var9, var11);
         this.mc.renderGlobal.clipRenderersByFrustum(var14, par1);
         if (var13 == 0) {
            this.mc.mcProfiler.endStartSection("updatechunks");

            while(!this.mc.renderGlobal.updateRenderers(var4, false) && par2 != 0L) {
               long var15 = par2 - System.nanoTime();
               if (this.mc.gameSettings.limitFramerate == 3) {
                  if (var15 < 1000000L || var15 > 1000000000L) {
                     break;
                  }
               } else if (var15 < 0L || var15 > 1000000000L) {
                  break;
               }
            }
         }

         if (var4.posY < 128.0) {
            this.renderCloudsCheck(var5, par1);
         }

         this.mc.mcProfiler.endStartSection("prepareterrain");
         this.setupFog(0, par1);
         GL11.glEnable(2912);
         this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         RenderHelper.disableStandardItemLighting();
         this.mc.mcProfiler.endStartSection("terrain");
         var5.sortAndRender(var4, 0, (double)par1);
         GL11.glShadeModel(7424);
         EntityPlayer var17;
         if (this.debugViewDirection == 0) {
            RenderHelper.enableStandardItemLighting();
            ForgeHooksClient.setRenderPass(0);
            this.mc.mcProfiler.endStartSection("entities");
            ForgeHooksClient.setRenderPass(0);
                /* Forge: Moved down
            var5.renderEntities(var4.getPosition(par1), var14, par1);
            this.enableLightmap((double)par1);
            this.mc.mcProfiler.endStartSection("litParticles");
            var6.renderLitParticles(var4, par1);
            RenderHelper.disableStandardItemLighting();
            this.setupFog(0, par1);
            this.mc.mcProfiler.endStartSection("particles");
            var6.renderParticles(var4, par1);
            this.disableLightmap((double)par1);

                 */
            if (this.mc.objectMouseOver != null && var4.isInsideOfMaterial(Material.water) && var4 instanceof EntityPlayer && this.mc.gameSettings.gui_mode == 0) {
               var17 = (EntityPlayer)var4;
               GL11.glDisable(3008);
               this.mc.mcProfiler.endStartSection("outline");
               var5.drawSelectionBox(var17, this.mc.objectMouseOver, 0, par1);

               if (!ForgeHooksClient.onDrawBlockHighlight(var5, var17, mc.objectMouseOver, 0, var17.inventory.getCurrentItemStack(), par1))
               {
                  var5.drawSelectionBox(var17, this.mc.objectMouseOver, 0, par1);
               }

               GL11.glEnable(3008);
            }
         }

         GL11.glDisable(3042);
         GL11.glEnable(2884);
         GL11.glBlendFunc(770, 771);
         GL11.glDepthMask(true);
         this.setupFog(0, par1);
         GL11.glEnable(3042);
         GL11.glDisable(2884);
         this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         if (this.mc.gameSettings.isFancyGraphicsEnabled()) {
            this.mc.mcProfiler.endStartSection("water");
            if (this.mc.gameSettings.ambientOcclusion != 0) {
               GL11.glShadeModel(7425);
            }

            GL11.glColorMask(false, false, false, false);
            int var18 = var5.sortAndRender(var4, 1, (double)par1);
            if (this.mc.gameSettings.anaglyph) {
               if (anaglyphField == 0) {
                  GL11.glColorMask(false, true, true, true);
               } else {
                  GL11.glColorMask(true, false, false, true);
               }
            } else {
               GL11.glColorMask(true, true, true, true);
            }

            if (var18 > 0) {
               var5.renderAllRenderLists(1, (double)par1);
            }

            GL11.glShadeModel(7424);
         } else {
            this.mc.mcProfiler.endStartSection("water");
            var5.sortAndRender(var4, 1, (double)par1);
         }

         if (this.debugViewDirection == 0) //Only render if render pass 0 happens as well.
         {
            RenderHelper.enableStandardItemLighting();
            this.mc.mcProfiler.endStartSection("entities");
            ForgeHooksClient.setRenderPass(1);
            var5.renderEntities(var4.getPosition(par1), var14, par1);
            ForgeHooksClient.setRenderPass(-1);
            RenderHelper.disableStandardItemLighting();
         }


         GL11.glDepthMask(true);
         GL11.glEnable(2884);
         GL11.glDisable(3042);
         if (this.cameraZoom == 1.0 && var4 instanceof EntityPlayer && this.mc.gameSettings.gui_mode == 0 && this.mc.objectMouseOver != null && !var4.isInsideOfMaterial(Material.water)) {
            var17 = (EntityPlayer)var4;
            GL11.glDisable(3008);
            this.mc.mcProfiler.endStartSection("outline");

            if (!ForgeHooksClient.onDrawBlockHighlight(var5, var17, mc.objectMouseOver, 0, var17.inventory.getCurrentItemStack(), par1))
            {
               var5.drawSelectionBox(var17, this.mc.objectMouseOver, 0, par1);
            }

            var5.drawSelectionBox(var17, this.mc.objectMouseOver, 0, par1);
            GL11.glEnable(3008);
         }

         this.mc.mcProfiler.endStartSection("destroyProgress");
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 1);

         var5.drawBlockDamageTexture(Tessellator.instance, var4, par1);


         GL11.glDisable(3042);
         this.mc.mcProfiler.endStartSection("weather");
         this.renderRainSnow(par1);
         GL11.glDisable(2912);
         if (var4.posY >= 128.0) {
            this.renderCloudsCheck(var5, par1);
         }


         //Forge: Moved section from above, now particles are the last thing to render.
         this.enableLightmap((double)par1);
         this.mc.mcProfiler.endStartSection("litParticles");
         var6.renderLitParticles(var4, par1);
         RenderHelper.disableStandardItemLighting();
         this.setupFog(0, par1);
         this.mc.mcProfiler.endStartSection("particles");
         var6.renderParticles(var4, par1);
         this.disableLightmap((double)par1);
         //Forge: End Move

         this.mc.mcProfiler.endStartSection("FRenderLast");
         ForgeHooksClient.dispatchRenderLast(var5, par1);


         this.mc.mcProfiler.endStartSection("hand");
         if (this.cameraZoom == 1.0) {
            GL11.glClear(256);
            this.renderHand(par1, var13);
         }

         if (!this.mc.gameSettings.anaglyph) {
            this.mc.mcProfiler.endSection();
            return;
         }
      }

      GL11.glColorMask(true, true, true, false);
      this.mc.mcProfiler.endSection();
   }

   private void renderCloudsCheck(RenderGlobal par1RenderGlobal, float par2) {
      if (this.mc.gameSettings.shouldRenderClouds()) {
         this.mc.mcProfiler.endStartSection("clouds");
         GL11.glPushMatrix();
         this.setupFog(0, par2);
         GL11.glEnable(2912);
         par1RenderGlobal.renderClouds(par2);
         GL11.glDisable(2912);
         this.setupFog(1, par2);
         GL11.glPopMatrix();
      }

   }

   private void addRainParticles() {
      float var1 = this.mc.theWorld.getRainStrength(1.0F);
      boolean is_blood_moon = this.mc.theWorld.isBloodMoon24HourPeriod();
      if (!this.mc.gameSettings.isFancyGraphicsEnabled()) {
         var1 /= 2.0F;
      }

      if (var1 != 0.0F) {
         this.random.setSeed((long)this.rendererUpdateCount * 312987231L);
         EntityLivingBase var2 = this.mc.renderViewEntity;
         WorldClient var3 = this.mc.theWorld;
         int var4 = MathHelper.floor_double(var2.posX);
         int var5 = MathHelper.floor_double(var2.posY);
         int var6 = MathHelper.floor_double(var2.posZ);
         byte var7 = 10;
         double var8 = 0.0;
         double var10 = 0.0;
         double var12 = 0.0;
         int var14 = 0;
         int var15 = (int)(100.0F * var1 * var1);
         if (this.mc.gameSettings.particleSetting == 1) {
            var15 >>= 1;
         } else if (this.mc.gameSettings.particleSetting == 2) {
            var15 = 0;
         }

         int index = Minecraft.getThreadIndex();

         for(int var16 = 0; var16 < var15; ++var16) {
            int var17 = var4 + this.random.nextInt(var7) - this.random.nextInt(var7);
            int var18 = var6 + this.random.nextInt(var7) - this.random.nextInt(var7);
            int var19 = var3.getPrecipitationHeight(var17, var18);
            int var20 = var3.getBlockId(var17, var19 - 1, var18);
            BiomeGenBase var21 = var3.getBiomeGenForCoords(var17, var18);
            if (var19 <= var5 + var7 && var19 >= var5 - var7 && var21.canSpawnLightningBolt(is_blood_moon) && var21.getFloatTemperature() >= 0.2F && var20 > 0) {
               float var22 = this.random.nextFloat();
               float var23 = this.random.nextFloat();
               Block block = Block.getBlock(var20);
               double pos_y;
               if (block.blockMaterial == Material.lava) {
                  pos_y = (double)((float)var19 - BlockFluid.getFluidHeightPercent(this.mc.theWorld.getBlockMetadata(var17, var19 - 1, var18)) + 0.1F + 0.125F);
                  this.mc.effectRenderer.addEffect(new EntitySmokeFX(var3, (double)((float)var17 + var22), pos_y, (double)((float)var18 + var23), 0.0, 0.0, 0.0));
               } else {
                  if (block.blockMaterial.isLiquid()) {
                     pos_y = (double)((float)var19 - BlockFluid.getFluidHeightPercent(this.mc.theWorld.getBlockMetadata(var17, var19 - 1, var18)) + 0.1F + 0.125F);
                  } else if (block.isAlwaysStandardFormCube()) {
                     pos_y = (double)((float)var19 + 0.1F);
                  } else {
                     if (block instanceof BlockTrapDoor && BlockTrapDoor.isTrapdoorOpen(this.mc.theWorld.getBlockMetadata(var17, var19 - 1, var18))) {
                        continue;
                     }

                     block.setBlockBoundsBasedOnStateAndNeighbors(this.mc.theWorld, var17, var19 - 1, var18);
                     pos_y = (double)(var19 - 1) + block.getBlockBoundsMaxY(index) + 0.10000000149011612;
                  }

                  ++var14;
                  if (this.random.nextInt(var14) == 0) {
                     var8 = (double)((float)var17 + var22);
                     var10 = (double)((float)var19 + 0.1F) - Block.blocksList[var20].getBlockBoundsMinY(index);
                     var12 = (double)((float)var18 + var23);
                  }

                  this.mc.effectRenderer.addEffect(new EntityRainFX(var3, (double)((float)var17 + var22), pos_y, (double)((float)var18 + var23)));
               }
            }
         }

         boolean player_is_outdoors = this.mc.thePlayer.isOutdoors();
         float sleep_factor = 1.0F - (float)this.mc.thePlayer.falling_asleep_counter / 50.0F;
         float distance_from_rain_factor = (float)Math.pow((double)this.mc.raining_strength_for_render_view_entity, 4.0);
         if (sleep_factor < 0.0F) {
            sleep_factor = 0.0F;
         }

         if (var14 > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
            this.rainSoundCounter = 0;
            if (var10 > var2.posY + 1.0 && var3.getPrecipitationHeight(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posZ)) > MathHelper.floor_double(var2.posY)) {
               if (player_is_outdoors) {
                  this.mc.theWorld.playSound(var8, var10, var12, "ambient.weather.rain", 0.1F * sleep_factor * distance_from_rain_factor, 0.5F, false);
               } else {
                  this.mc.theWorld.playSound(var8, var10, var12, "ambient.weather.rain", 0.025F * sleep_factor * distance_from_rain_factor, 0.125F, false);
               }
            } else if (player_is_outdoors) {
               this.mc.theWorld.playSound(var8, var10, var12, "ambient.weather.rain", 0.2F * sleep_factor * distance_from_rain_factor, 1.0F, false);
            } else {
               this.mc.theWorld.playSound(var8, var10, var12, "ambient.weather.rain", 0.05F * sleep_factor * distance_from_rain_factor, 0.25F, false);
            }
         }
      }

   }

   protected void renderRainSnow(float par1) {
      if (this.mc.renderViewEntity.ticksExisted >= 1) {
         float var2 = this.mc.theWorld.getRainStrength(par1);
         if (var2 > 0.0F) {
            boolean is_blood_moon = this.mc.theWorld.isBloodMoon24HourPeriod();
            this.enableLightmap((double)par1);
            if (this.rainXCoords == null) {
               this.rainXCoords = new float[1024];
               this.rainYCoords = new float[1024];

               for(int var3 = 0; var3 < 32; ++var3) {
                  for(int var4 = 0; var4 < 32; ++var4) {
                     float var5 = (float)(var4 - 16);
                     float var6 = (float)(var3 - 16);
                     float var7 = MathHelper.sqrt_float(var5 * var5 + var6 * var6);
                     this.rainXCoords[var3 << 5 | var4] = -var6 / var7;
                     this.rainYCoords[var3 << 5 | var4] = var5 / var7;
                  }
               }
            }

            EntityLivingBase var41 = this.mc.renderViewEntity;
            WorldClient var42 = this.mc.theWorld;
            int var43 = MathHelper.floor_double(var41.posX);
            int var44 = MathHelper.floor_double(var41.posY);
            int var45 = MathHelper.floor_double(var41.posZ);
            Tessellator var8 = Tessellator.instance;
            GL11.glDisable(2884);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glAlphaFunc(516, 0.01F);
            double var9 = var41.lastTickPosX + (var41.posX - var41.lastTickPosX) * (double)par1;
            double var11 = var41.lastTickPosY + (var41.posY - var41.lastTickPosY) * (double)par1;
            double var13 = var41.lastTickPosZ + (var41.posZ - var41.lastTickPosZ) * (double)par1;
            int var15 = MathHelper.floor_double(var11);
            byte var16 = 5;
            if (this.mc.gameSettings.isFancyGraphicsEnabled()) {
               var16 = 10;
            }

            boolean var17 = false;
            byte var18 = -1;
            float var19 = (float)this.rendererUpdateCount + par1;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            var17 = false;
            int type = this.mc.theWorld.getPrecipitationType(this.last_precipitation_type);
            this.last_precipitation_type = type;

            for(int var20 = var45 - var16; var20 <= var45 + var16; ++var20) {
               for(int var21 = var43 - var16; var21 <= var43 + var16; ++var21) {
                  if ((var21 != var43 || var20 != var45) && var42.chunkExistsAndIsNotEmptyFromBlockCoords(var21, var20)) {
                     int dx = var21 - var43;
                     int dz = var20 - var45;
                     int index_wrapped = MathHelper.getWrappedIndex(var20 * 32 + var21, 32768);
                     int var22 = (var20 - var45 + 16) * 32 + var21 - var43 + 16;
                     float var23 = this.rainXCoords[var22] * 0.5F;
                     float var24 = this.rainYCoords[var22] * 0.5F;
                     BiomeGenBase var25 = var42.getBiomeGenForCoords(var21, var20);
                     if (var25.canSpawnLightningBolt(is_blood_moon) || var25.getEnableSnow()) {
                        int var26 = var42.getPrecipitationHeight(var21, var20);
                        if (var26 < 0) {
                           var26 = 0;
                        }

                        int var27 = var44 - var16;
                        int var28 = var44 + var16;
                        if (var27 < var26) {
                           var27 = var26;
                        }

                        if (var28 < var26) {
                           var28 = var26;
                        }

                        float var29 = 1.0F;
                        int var30 = var26;
                        if (var26 < var15) {
                           var30 = var15;
                        }

                        if (var27 != var28) {
                           double var27_adjusted = var42.getBlockRenderTopY(var21, var27 - 1, var20);
                           this.random.setSeed((long)(var21 * var21 * 3121 + var21 * 45238971 ^ var20 * var20 * 418711 + var20 * 13761));
                           float var31 = var25.getFloatTemperature();
                           double var35;
                           float var32;
                           float var46;
                           float var34;
                           if (var42.getWorldChunkManager().getTemperatureAtHeight(var31, var26) >= 0.15F) {
                              if (var18 != 0) {
                                 if (var18 >= 0) {
                                    var8.draw();
                                 }

                                 var18 = 0;
                                 this.mc.getTextureManager().bindTexture(locationRainPng);
                                 var8.startDrawingQuads();
                              }

                              var32 = ((float)(this.rendererUpdateCount + var21 * var21 * 3121 + var21 * 45238971 + var20 * var20 * 418711 + var20 * 13761 & 31) + par1) / 32.0F * (3.0F + this.random.nextFloat());
                              double var33 = (double)((float)var21 + 0.5F) - var41.posX;
                              var35 = (double)((float)var20 + 0.5F) - var41.posZ;
                              var46 = MathHelper.sqrt_double(var33 * var33 + var35 * var35) / (float)var16;
                              var34 = 1.0F;
                              var8.setBrightness(var42.getLightBrightnessForSkyBlocks(var21, var30, var20, 0));
                              var8.setColorRGBA_F(var34, var34, var34, ((1.0F - var46 * var46) * 0.5F + 0.5F) * var2);
                              var8.setTranslation(-var9 * 1.0, -var11 * 1.0, -var13 * 1.0);
                              if (RenderingScheme.current == 0) {
                                 var8.addVertexWithUV((double)((float)var21 - var23) + 0.5, (double)var27, (double)((float)var20 - var24) + 0.5, (double)(0.0F * var29), (double)((float)var27 * var29 / 4.0F + var32 * var29));
                                 var8.addVertexWithUV((double)((float)var21 + var23) + 0.5, (double)var27, (double)((float)var20 + var24) + 0.5, (double)(1.0F * var29), (double)((float)var27 * var29 / 4.0F + var32 * var29));
                                 var8.addVertexWithUV((double)((float)var21 + var23) + 0.5, (double)var28, (double)((float)var20 + var24) + 0.5, (double)(1.0F * var29), (double)((float)var28 * var29 / 4.0F + var32 * var29));
                                 var8.addVertexWithUV((double)((float)var21 - var23) + 0.5, (double)var28, (double)((float)var20 - var24) + 0.5, (double)(0.0F * var29), (double)((float)var28 * var29 / 4.0F + var32 * var29));
                              } else {
                                 this.x[0] = (double)((float)var21 - var23 + 0.5F);
                                 this.y[0] = var27_adjusted;
                                 this.z[0] = (double)((float)var20 - var24 + 0.5F);
                                 this.u[0] = 0.0;
                                 this.v[0] = var27_adjusted * (double)var29 / 4.0 + (double)(var32 * var29);
                                 this.x[1] = (double)((float)var21 + var23 + 0.5F);
                                 this.y[1] = var27_adjusted;
                                 this.z[1] = (double)((float)var20 + var24 + 0.5F);
                                 this.u[1] = (double)var29;
                                 this.v[1] = this.v[0];
                                 this.x[2] = this.x[1];
                                 this.y[2] = (double)var28;
                                 this.z[2] = this.z[1];
                                 this.u[2] = (double)var29;
                                 this.v[2] = (double)((float)var28 * var29 / 4.0F + var32 * var29);
                                 this.x[3] = this.x[0];
                                 this.y[3] = (double)var28;
                                 this.z[3] = this.z[0];
                                 this.u[3] = 0.0;
                                 this.v[3] = this.v[2];
                                 var8.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
                              }

                              var8.setTranslation(0.0, 0.0, 0.0);
                           } else {
                              if (var18 != 1) {
                                 if (var18 >= 0) {
                                    var8.draw();
                                 }

                                 var18 = 1;
                                 this.mc.getTextureManager().bindTexture(locationSnowPng);
                                 var8.startDrawingQuads();
                              }

                              var32 = ((float)(this.rendererUpdateCount & 511) + par1) / 512.0F;
                              float vertical_offset;
                              float horizontal_offset;
                              if (type == 0) {
                                 horizontal_offset = (float)RNG.int_32[index_wrapped] / 32.0F;
                                 vertical_offset = var19 * 0.004F;
                              } else if (type == 1) {
                                 horizontal_offset = 1.0F + var19 * RNG.float_1_minus_float_1[index_wrapped] * 0.004F;
                                 vertical_offset = var19 * 0.004F;
                              } else if (type == 2) {
                                 horizontal_offset = 1.0F + var19 * RNG.float_1_minus_float_1[index_wrapped] * 0.008F;
                                 vertical_offset = var19 * 0.004F;
                              } else if (type == 3) {
                                 horizontal_offset = 1.0F + var19 * RNG.float_1_minus_float_1[index_wrapped] * 0.012F;
                                 vertical_offset = var19 * 0.003F;
                              } else {
                                 horizontal_offset = this.random.nextFloat() + var19 * 0.01F * (float)this.random.nextGaussian();
                                 vertical_offset = this.random.nextFloat() + var19 * (float)this.random.nextGaussian() * 0.001F;
                              }

                              var46 = horizontal_offset;
                              var34 = vertical_offset;
                              var34 += RNG.float_1[index_wrapped];
                              var35 = (double)((float)var21 + 0.5F) - var41.posX;
                              double var47 = (double)((float)var20 + 0.5F) - var41.posZ;
                              float var39 = MathHelper.sqrt_double(var35 * var35 + var47 * var47) / (float)var16;
                              float var40 = 1.0F;
                              var8.setBrightness((var42.getLightBrightnessForSkyBlocks(var21, var30, var20, 0) * 3 + 15728880) / 4);
                              var8.setColorRGBA_F(var40, var40, var40, MathHelper.clamp_float(0.85F - var39 * 0.35F, 0.0F, 1.0F) * var2);
                              var8.setTranslation(-var9 * 1.0, -var11 * 1.0, -var13 * 1.0);
                              if (RenderingScheme.current == 0) {
                                 var8.addVertexWithUV((double)((float)var21 - var23) + 0.5, (double)var27, (double)((float)var20 - var24) + 0.5, (double)(0.0F * var29 + var46), (double)((float)var27 * var29 / 4.0F + var32 * var29 + var34));
                                 var8.addVertexWithUV((double)((float)var21 + var23) + 0.5, (double)var27, (double)((float)var20 + var24) + 0.5, (double)(1.0F * var29 + var46), (double)((float)var27 * var29 / 4.0F + var32 * var29 + var34));
                                 var8.addVertexWithUV((double)((float)var21 + var23) + 0.5, (double)var28, (double)((float)var20 + var24) + 0.5, (double)(1.0F * var29 + var46), (double)((float)var28 * var29 / 4.0F + var32 * var29 + var34));
                                 var8.addVertexWithUV((double)((float)var21 - var23) + 0.5, (double)var28, (double)((float)var20 - var24) + 0.5, (double)(0.0F * var29 + var46), (double)((float)var28 * var29 / 4.0F + var32 * var29 + var34));
                              } else {
                                 this.x[0] = (double)((float)var21 - var23 + 0.5F);
                                 this.y[0] = var27_adjusted;
                                 this.z[0] = (double)((float)var20 - var24 + 0.5F);
                                 this.u[0] = (double)var46;
                                 this.v[0] = var27_adjusted * (double)var29 / 4.0 + (double)(var32 * var29) + (double)var34;
                                 this.x[1] = (double)((float)var21 + var23 + 0.5F);
                                 this.y[1] = var27_adjusted;
                                 this.z[1] = (double)((float)var20 + var24 + 0.5F);
                                 this.u[1] = (double)(var29 + var46);
                                 this.v[1] = this.v[0];
                                 this.x[2] = this.x[1];
                                 this.y[2] = (double)var28;
                                 this.z[2] = this.z[1];
                                 this.u[2] = this.u[1];
                                 this.v[2] = (double)((float)var28 * var29 / 4.0F + var32 * var29 + var34);
                                 this.x[3] = this.x[0];
                                 this.y[3] = (double)var28;
                                 this.z[3] = this.z[0];
                                 this.u[3] = (double)var46;
                                 this.v[3] = this.v[2];
                                 if (dx * dx + dz * dz <= 9) {
                                    float yaw = (float)MathHelper.getYawInDegrees((double)var21 + 0.5, (double)var20 + 0.5, var9, var13);
                                    Vec3 right_side = MathHelper.getNormalizedVector(yaw + 90.0F, 0.0F, var42.getWorldVec3Pool());
                                    this.x[0] = (double)((float)var21 + 0.5F) + right_side.xCoord / 2.0;
                                    this.x[1] = (double)((float)var21 + 0.5F) - right_side.xCoord / 2.0;
                                    this.x[2] = this.x[1];
                                    this.x[3] = this.x[0];
                                    this.z[0] = (double)((float)var20 + 0.5F) + right_side.zCoord / 2.0;
                                    this.z[1] = (double)((float)var20 + 0.5F) - right_side.zCoord / 2.0;
                                    this.z[2] = this.z[1];
                                    this.z[3] = this.z[0];
                                 }

                                 var8.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
                              }

                              var8.setTranslation(0.0, 0.0, 0.0);
                           }
                        }
                     }
                  }
               }
            }

            if (var18 >= 0) {
               var8.draw();
            }

            GL11.glEnable(2884);
            GL11.glDisable(3042);
            GL11.glAlphaFunc(516, 0.1F);
            this.disableLightmap((double)par1);
         }

      }
   }

   public void setupOverlayRendering() {
      ScaledResolution var1 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
      GL11.glClear(256);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0, var1.getScaledWidth_double(), var1.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
   }

   private double getInterpolatedPosYForVoidFog(EntityLivingBase viewer, float partial_tick) {
      return Math.max(MathHelper.getInterpolatedValue(viewer.lastTickPosY, viewer.posY, partial_tick), 0.0);
   }

   private void updateFogColor(float par1) {
      WorldClient var2 = this.mc.theWorld;
      EntityLivingBase var3 = this.mc.renderViewEntity;
      float var4 = 1.0F / (float)(4 - this.mc.gameSettings.getRenderDistance());
      var4 = 1.0F - (float)Math.pow((double)var4, 0.25);
      Vec3 var5 = var2.getSkyColor(this.mc.renderViewEntity, par1);
      float var6 = (float)var5.xCoord;
      float var7 = (float)var5.yCoord;
      float var8 = (float)var5.zCoord;
      Vec3 var9 = var2.getFogColor(par1, var3);
      this.fogColorRed = (float)var9.xCoord;
      this.fogColorGreen = (float)var9.yCoord;
      this.fogColorBlue = (float)var9.zCoord;
      float var11;
      if (this.mc.gameSettings.getRenderDistance() < 2) {
         Vec3 var10 = MathHelper.sin(var2.getCelestialAngleRadians(par1)) > 0.0F ? var2.getWorldVec3Pool().getVecFromPool(-1.0, 0.0, 0.0) : var2.getWorldVec3Pool().getVecFromPool(1.0, 0.0, 0.0);
         var11 = (float)var3.getLook(par1).dotProduct(var10);
         if (var11 < 0.0F) {
            var11 = 0.0F;
         }

         if (var11 > 0.0F) {
            float[] var12 = var2.provider.calcSunriseSunsetColors(var2.getCelestialAngle(par1), par1);
            if (var12 != null) {
               var11 *= var12[3];
               this.fogColorRed = this.fogColorRed * (1.0F - var11) + var12[0] * var11;
               this.fogColorGreen = this.fogColorGreen * (1.0F - var11) + var12[1] * var11;
               this.fogColorBlue = this.fogColorBlue * (1.0F - var11) + var12[2] * var11;
            }
         }
      }

      this.fogColorRed += (var6 - this.fogColorRed) * var4;
      this.fogColorGreen += (var7 - this.fogColorGreen) * var4;
      this.fogColorBlue += (var8 - this.fogColorBlue) * var4;
      float var19 = var2.getRainStrength(par1);
      float var20;
      if (var19 > 0.0F) {
         var11 = 1.0F - var19 * 0.5F;
         var20 = 1.0F - var19 * 0.4F;
         this.fogColorRed *= var11;
         this.fogColorGreen *= var11;
         this.fogColorBlue *= var20;
      }

      var11 = var2.getWeightedThunderStrength(par1);
      if (var11 > 0.0F) {
         var20 = 1.0F - var11 * 0.5F;
         this.fogColorRed *= var20;
         this.fogColorGreen *= var20;
         this.fogColorBlue *= var20;
      }

      int var21 = ActiveRenderInfo.getBlockIdAtEntityViewpoint(this.mc.theWorld, var3, par1);
      if (this.cloudFog) {
         Vec3 var13 = var2.getCloudColour(par1);
         this.fogColorRed = (float)var13.xCoord;
         this.fogColorGreen = (float)var13.yCoord;
         this.fogColorBlue = (float)var13.zCoord;
      } else if (var21 != 0 && Block.blocksList[var21].blockMaterial == Material.water) {
         float var22 = (float)EnchantmentHelper.getRespiration(var3) * 0.2F;
         this.fogColorRed = 0.02F + var22;
         this.fogColorGreen = 0.02F + var22;
         this.fogColorBlue = 0.2F + var22;
      } else if (var21 != 0 && Block.blocksList[var21].blockMaterial == Material.lava) {
         this.fogColorRed = 0.6F;
         this.fogColorGreen = 0.1F;
         this.fogColorBlue = 0.0F;
      }

      double var14 = this.mc.theWorld.hasSkylight() ? this.getInterpolatedPosYForVoidFog(var3, par1) * var2.provider.getVoidFogYFactor() : 1.0;
      if (var3.isPotionActive(Potion.blindness)) {
         int var16 = var3.getActivePotionEffect(Potion.blindness).getDuration();
         if (var16 < 20) {
            var14 *= (double)(1.0F - (float)var16 / 20.0F);
         } else {
            var14 = 0.0;
         }
      }

      if (var14 < 1.0) {
         if (var14 < 0.0) {
            var14 = 0.0;
         }

         var14 *= var14;
         this.fogColorRed = (float)((double)this.fogColorRed * var14);
         this.fogColorGreen = (float)((double)this.fogColorGreen * var14);
         this.fogColorBlue = (float)((double)this.fogColorBlue * var14);
      }

      float var23;
      if (this.field_82831_U > 0.0F) {
         var23 = this.field_82832_V + (this.field_82831_U - this.field_82832_V) * par1;
         this.fogColorRed = this.fogColorRed * (1.0F - var23) + this.fogColorRed * 0.7F * var23;
         this.fogColorGreen = this.fogColorGreen * (1.0F - var23) + this.fogColorGreen * 0.6F * var23;
         this.fogColorBlue = this.fogColorBlue * (1.0F - var23) + this.fogColorBlue * 0.6F * var23;
      }

      if (this.mc.gameSettings.anaglyph) {
         var23 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
         float var17 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
         float var18 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
         this.fogColorRed = var23;
         this.fogColorGreen = var17;
         this.fogColorBlue = var18;
      }

      GL11.glClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
   }

   private static boolean doesFogPostFieldRequireRegeneration(EntityLivingBase viewer) {
      return fog_post_list == null || last_fog_post_field_generation_viewer_world != viewer.worldObj || last_fog_post_field_generation_viewer_chunk_x != viewer.getChunkPosX() || last_fog_post_field_generation_viewer_chunk_z != viewer.getChunkPosZ();
   }

   private static void generateFogPostFieldIfRequired(EntityLivingBase viewer) {
      if (doesFogPostFieldRequireRegeneration(viewer)) {
         last_fog_post_field_generation_viewer_world = viewer.worldObj;
         last_fog_post_field_generation_viewer_chunk_x = viewer.getChunkPosX();
         last_fog_post_field_generation_viewer_chunk_z = viewer.getChunkPosZ();
         if (fog_post_list == null) {
            fog_post_list = new ArrayList();
         } else {
            fog_post_list.clear();
         }

         Random random = new Random();
         random.setSeed(0L);
         random.nextInt();
         long hashed_type = random.nextLong();

         for(int chunk_dx = -65; chunk_dx <= 65; ++chunk_dx) {
            for(int chunk_dz = -65; chunk_dz <= 65; ++chunk_dz) {
               int chunk_x = viewer.getChunkPosX() + chunk_dx;
               int chunk_z = viewer.getChunkPosZ() + chunk_dz;
               random.setSeed((long)Chunk.getChunkCoordsHash(chunk_x, chunk_z) * hashed_type * viewer.worldObj.getHashedSeed());
               random.nextInt();
               if (random.nextInt(4828) == 0) {
                  fog_post_list.add(new ChunkCoordIntPair(chunk_x, chunk_z));
               }
            }
         }

         Debug.println("generateFogPostFieldIfRequired: regenerated fog post field");
      }
   }

   public static float getProximityToNearestFogPost(EntityLivingBase viewer) {
      generateFogPostFieldIfRequired(viewer);
      float distance_to_nearest_fog_post = 1024.0F;
      Iterator i = fog_post_list.iterator();

      while(i.hasNext()) {
         ChunkCoordIntPair chunk_coords = (ChunkCoordIntPair)i.next();
         int chunk_x = chunk_coords.chunkXPos;
         int chunk_z = chunk_coords.chunkZPos;
         World var10000 = viewer.worldObj;
         float distance_to_fog_post = World.getDistanceFromDeltas(viewer.posX - (double)(chunk_x * 16), 0.0, viewer.posZ - (double)(chunk_z * 16));
         if (distance_to_fog_post < distance_to_nearest_fog_post) {
            distance_to_nearest_fog_post = distance_to_fog_post;
         }
      }

      return distance_to_nearest_fog_post >= 1024.0F ? 0.0F : 1.0F - distance_to_nearest_fog_post / 1024.0F;
   }

   public double getDistanceToNearestBiomeThatCanBeFoggy(double pos_x, double pos_z) {
      World world = this.mc.theWorld;
      int x = MathHelper.floor_double(pos_x);
      int z = MathHelper.floor_double(pos_z);
      BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
      if (biome.rainfall > 0.0F && !biome.isFreezing()) {
         distance_from_biome_that_can_be_foggy_last_viewer_world = world;
         return Math.max(World.getDistanceFromDeltas((double)x + 0.5 - pos_x, (double)z + 0.5 - pos_z) - 0.7071, 0.0);
      } else {
         boolean foggy_biome_found = false;
         double distance_to_nearest_raining_coord_sq = 0.0;
         int falloff_distance = 32;
         int index = -1;
         int dx;
         int dz;
         double dxd;
         double dzd;
         double distance_sq;
         if (distance_from_biome_that_can_be_foggy_last_viewer_world == world && distance_from_biome_that_can_be_foggy_last_viewer_x == x && distance_from_biome_that_can_be_foggy_last_viewer_z == z) {
            if (!is_fog_supporting_biome_contains_at_least_one) {
               return 10000.0;
            } else {
               for(dx = -falloff_distance; dx <= falloff_distance; ++dx) {
                  for(dz = -falloff_distance; dz <= falloff_distance; ++dz) {
                     ++index;
                     if (is_fog_supporting_biome[index]) {
                        dxd = (double)((float)(x + dx) + 0.5F) - pos_x;
                        dzd = (double)((float)(z + dz) + 0.5F) - pos_z;
                        distance_sq = dxd * dxd + dzd * dzd;
                        if (!foggy_biome_found || distance_sq < distance_to_nearest_raining_coord_sq) {
                           foggy_biome_found = true;
                           distance_to_nearest_raining_coord_sq = distance_sq;
                        }
                     }
                  }
               }

               return foggy_biome_found ? Math.max((double)MathHelper.sqrt_double(distance_to_nearest_raining_coord_sq) - 0.7071, 0.0) : 10000.0;
            }
         } else {
            distance_from_biome_that_can_be_foggy_last_viewer_x = x;
            distance_from_biome_that_can_be_foggy_last_viewer_z = z;
            distance_from_biome_that_can_be_foggy_last_viewer_world = world;

            for(dx = -falloff_distance; dx <= falloff_distance; ++dx) {
               for(dz = -falloff_distance; dz <= falloff_distance; ++dz) {
                  ++index;
                  if (!world.chunkExistsAndIsNotEmptyFromBlockCoords(x + dx, z + dz)) {
                     is_fog_supporting_biome[index] = false;
                  } else {
                     biome = world.getBiomeGenForCoords(x + dx, z + dz);
                     if (biome.rainfall > 0.0F && !biome.isFreezing()) {
                        dxd = (double)((float)(x + dx) + 0.5F) - pos_x;
                        dzd = (double)((float)(z + dz) + 0.5F) - pos_z;
                        distance_sq = dxd * dxd + dzd * dzd;
                        if (!foggy_biome_found || distance_sq < distance_to_nearest_raining_coord_sq) {
                           foggy_biome_found = true;
                           distance_to_nearest_raining_coord_sq = distance_sq;
                        }

                        is_fog_supporting_biome[index] = true;
                     } else {
                        is_fog_supporting_biome[index] = false;
                     }
                  }
               }
            }

            is_fog_supporting_biome_contains_at_least_one = foggy_biome_found;
            return foggy_biome_found ? Math.max((double)MathHelper.sqrt_double(distance_to_nearest_raining_coord_sq) - 0.7071, 0.0) : 10000.0;
         }
      }
   }

   private void setupFog(int par1, float par2) {
      EntityLivingBase var3 = this.mc.renderViewEntity;
      boolean var4 = false;
      if (disable_fog) {
         var4 = true;
      }

      if (par1 == 999) {
         GL11.glFog(2918, this.setFogColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
         GL11.glFogi(2917, 9729);
         GL11.glFogf(2915, 0.0F);
         GL11.glFogf(2916, 8.0F);
         if (capability_gl_nv_fog_distance) {
            GL11.glFogi(34138, 34139);
         }

         GL11.glFogf(2915, 0.0F);
      } else {
         GL11.glFog(2918, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
         GL11.glNormal3f(0.0F, -1.0F, 0.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         int var5 = ActiveRenderInfo.getBlockIdAtEntityViewpoint(this.mc.theWorld, var3, par2);
         float var6;
         if (var3.isPotionActive(Potion.blindness)) {
            var6 = 5.0F;
            int var7 = var3.getActivePotionEffect(Potion.blindness).getDuration();
            if (var7 < 20) {
               var6 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float)var7 / 20.0F);
            }

            GL11.glFogi(2917, 9729);
            if (par1 < 0) {
               GL11.glFogf(2915, 0.0F);
               GL11.glFogf(2916, var6 * 0.8F);
            } else {
               GL11.glFogf(2915, var6 * 0.25F);
               GL11.glFogf(2916, var6);
            }

            if (capability_gl_nv_fog_distance) {
               GL11.glFogi(34138, 34139);
            }
         } else if (this.cloudFog) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 0.1F);
         } else if (var5 > 0 && Block.blocksList[var5].blockMaterial == Material.water) {
            GL11.glFogi(2917, 2048);
            if (var3.isPotionActive(Potion.waterBreathing)) {
               GL11.glFogf(2914, 0.05F);
            } else {
               GL11.glFogf(2914, 0.1F - (float)EnchantmentHelper.getRespiration(var3) * 0.03F);
            }
         } else if (var5 > 0 && Block.blocksList[var5].blockMaterial == Material.lava) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 2.0F);
         } else {
            var6 = this.farPlaneDistance;
            if (!var4) {
               if (this.mc.theWorld.provider.getWorldHasVoidFog()) {
                  Vec3 eye_pos = var3.getEyePos();
                  int skylight_brightness_at_eye_pos = this.mc.theWorld.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, eye_pos.getBlockX(), eye_pos.getBlockY(), eye_pos.getBlockZ());
                  Minecraft var10000 = this.mc;
                  int var28;
                  if (Minecraft.last_fps < 1) {
                     var28 = 1;
                  } else {
                     var10000 = this.mc;
                     var28 = Minecraft.last_fps;
                  }

                  int effective_fps = var28;
                  float delta;
                  float max_change;
                  if (this.skylight_brightness_used_for_fog < (float)skylight_brightness_at_eye_pos) {
                     delta = (float)skylight_brightness_at_eye_pos - this.skylight_brightness_used_for_fog;
                     max_change = !(delta < 12.0F) && eye_pos.getBlockY() >= 0 ? delta : 0.6F / (float)effective_fps;
                     this.skylight_brightness_used_for_fog += Math.min(delta, max_change);
                  } else if (this.skylight_brightness_used_for_fog > (float)skylight_brightness_at_eye_pos) {
                     delta = this.skylight_brightness_used_for_fog - (float)skylight_brightness_at_eye_pos;
                     max_change = !(delta < 12.0F) && eye_pos.getBlockY() >= 0 ? delta : 0.6F / (float)effective_fps;
                     this.skylight_brightness_used_for_fog -= Math.min(delta, max_change);
                  }

                  double interpolated_pos_y = MathHelper.getInterpolatedValue(var3.lastTickPosY, var3.posY, par2);
                  double var10 = (double)this.skylight_brightness_used_for_fog / 16.0 + (interpolated_pos_y + 4.0) / 20.0;
                  if (var10 < 0.0) {
                     var10 = 0.0;
                  }

                  double power = 1.0 + (16.0 - interpolated_pos_y) / 2.0;
                  if (power > 1.0) {
                     var10 = Math.pow(var10, power);
                  }

                  float var9 = 100.0F * (float)var10;
                  if (var9 < 5.0F) {
                     var9 = 5.0F;
                  }

                  if (var6 > var9) {
                     var6 = var9;
                  }

                  if (var6 < this.farPlaneDistance) {
                     var6 = (float)((double)var6 / Math.max(Math.sqrt((double)getProximityToNearestFogPost(var3)), 0.009999999776482582));
                  }

                  if (var6 < 24.0F) {
                     var6 = 24.0F;
                  }

                  if (var6 > 96.0F && this.mc.theWorld.isOverworld()) {
                     long shifted_total_world_time = this.mc.theWorld.getTotalWorldTime() - 12000L;
                     int shifted_day_of_world = World.getDayOfWorld(shifted_total_world_time);
                     if (shifted_day_of_world > 7) {
                        random_for_fog_events.setSeed((long)(shifted_day_of_world * 365024131) * this.mc.theWorld.getWorldCreationTime() * 672784657L);
                        random_for_fog_events.nextInt();
                        if (random_for_fog_events.nextInt(7) == 0) {
                           float fog_max_strength = 96.0F + random_for_fog_events.nextFloat() * (var6 - 96.0F) * 0.75F;
                           long ticks_from_midnight = (long)this.mc.theWorld.getAdjustedTimeOfDay();
                           boolean is_dusk = false;
                           if (ticks_from_midnight > 12000L) {
                              ticks_from_midnight = 24000L - ticks_from_midnight;
                              is_dusk = true;
                           }

                           float day_cycle_factor = MathHelper.clamp_float((float)(8000L - ticks_from_midnight) / (is_dusk ? 4000.0F : 2000.0F), 0.0F, 1.0F);
                           if (day_cycle_factor > 0.0F) {
                              if (distance_from_biome_that_can_be_foggy_last_viewer_world != this.mc.theWorld || distance_from_biome_that_can_be_foggy_tick != this.mc.theWorld.getTotalWorldTime()) {
                                 boolean player_moved = distance_from_biome_that_can_be_foggy_last_viewer_world != this.mc.theWorld || distance_from_biome_that_can_be_foggy_last_viewer_pos_x != var3.posX || distance_from_biome_that_can_be_foggy_last_viewer_pos_z != var3.posZ;
                                 if (player_moved) {
                                    distance_from_biome_that_can_be_foggy = this.getDistanceToNearestBiomeThatCanBeFoggy(var3.posX, var3.posZ);
                                    distance_from_biome_that_can_be_foggy_last_viewer_pos_x = var3.posX;
                                    distance_from_biome_that_can_be_foggy_last_viewer_pos_z = var3.posZ;
                                 }

                                 distance_from_biome_that_can_be_foggy_tick = this.mc.theWorld.getTotalWorldTime();
                              }

                              float distance_from_biome_that_can_be_foggy_factor = Math.max(1.0F - (float)(distance_from_biome_that_can_be_foggy / 32.0), 0.0F);
                              float final_factor = day_cycle_factor * distance_from_biome_that_can_be_foggy_factor;
                              if (final_factor > 0.0F) {
                                 float fog_strength = fog_max_strength * final_factor + var6 * (1.0F - final_factor);
                                 if (var6 > fog_strength) {
                                    var6 = fog_strength;
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (var6 > this.farPlaneDistance) {
                     var6 = this.farPlaneDistance;
                  }
               } else if (this.mc.theWorld.isUnderworld()) {
                  var6 = 128.0F;
               }
            }

            GL11.glFogi(2917, 9729);
            if (par1 < 0) {
               GL11.glFogf(2915, 0.0F);
               GL11.glFogf(2916, var6 * 0.8F);
            } else {
               GL11.glFogf(2915, var6 * 0.25F);
               GL11.glFogf(2916, var6);
            }

            if (capability_gl_nv_fog_distance) {
               GL11.glFogi(34138, 34139);
            }

            if (this.mc.theWorld.provider.doesXZShowFog(var3.getBlockPosX(), var3.getEyeBlockPosY(), var3.getBlockPosZ())) {
               GL11.glFogf(2915, var6 * 0.05F);
               GL11.glFogf(2916, Math.min(var6, 192.0F) * 0.5F);
            }
         }

         GL11.glEnable(2903);
         GL11.glColorMaterial(1028, 4608);
      }

   }

   private FloatBuffer setFogColorBuffer(float par1, float par2, float par3, float par4) {
      this.fogColorBuffer.clear();
      this.fogColorBuffer.put(par1).put(par2).put(par3).put(par4);
      this.fogColorBuffer.flip();
      return this.fogColorBuffer;
   }

   public static int performanceToFps(int par0) {
      if (par0 == 3) {
         return 120;
      } else {
         return par0 == 1 ? 120 : (par0 == 2 ? 35 : 200);
      }
   }

   static Minecraft getRendererMinecraft(EntityRenderer par0EntityRenderer) {
      return par0EntityRenderer.mc;
   }

   static {
      capability_gl_nv_fog_distance = GLContext.getCapabilities().GL_NV_fog_distance;
      random_for_fog_events = new Random();
      is_fog_supporting_biome = new boolean[4225];
   }
}
