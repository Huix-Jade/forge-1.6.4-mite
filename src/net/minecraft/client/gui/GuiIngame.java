package net.minecraft.client.gui;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumTournamentType;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Translator;
import net.minecraft.world.WeatherEvent;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeHooks;
import org.lwjgl.opengl.GL11;

public class GuiIngame extends Gui {
   private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
   private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
   private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
   private static final RenderItem itemRenderer = new RenderItem();
   private final Random rand = new Random();
   private final Minecraft mc;
   private final GuiNewChat persistantChatGUI;
   private int updateCounter;
   private String recordPlaying = "";
   private int recordPlayingUpFor;
   private boolean recordIsPlaying;
   public float prevVignetteBrightness = 1.0F;
   private int remainingHighlightTicks;
   private ItemStack highlightingItemStack;
   public static final ResourceLocation MITE_icons = new ResourceLocation("textures/gui/MITE_icons.png");
   public static long display_overburdened_cpu_icon_until_ms;
   public int curse_notification_counter;
   private String last_highlighting_item_stack_text;
   public static int allotted_time = -1;
   public static int server_load = -1;

   public GuiIngame(Minecraft par1Minecraft) {
      this.mc = par1Minecraft;
      this.persistantChatGUI = new GuiNewChat(par1Minecraft);
   }

   public void drawChatForMITEDS() {
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 190.0F, 0.0F);
      this.persistantChatGUI.drawChat(this.updateCounter);
      GL11.glPopMatrix();
   }

   public void renderGameOverlay(float par1, boolean par2, int par3, int par4) {
      ScaledResolution var5 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
      int var6 = var5.getScaledWidth();
      int var7 = var5.getScaledHeight();
      FontRenderer fontRenderer = this.mc.fontRenderer;
      this.mc.entityRenderer.setupOverlayRendering();
      if (!this.mc.thePlayer.isGhost()) {
         GL11.glEnable(3042);
         int skylight_subtracted = this.mc.theWorld.skylightSubtracted;
         int skylight_subtracted_ignoring_rain_and_thunder = this.mc.theWorld.skylight_subtracted_ignoring_rain_and_thunder;
         this.mc.theWorld.skylightSubtracted = 0;
         this.mc.theWorld.skylight_subtracted_ignoring_rain_and_thunder = 0;
         float vignette_brightness = this.mc.thePlayer.getBrightness(par1);
         this.mc.theWorld.skylightSubtracted = skylight_subtracted;
         this.mc.theWorld.skylight_subtracted_ignoring_rain_and_thunder = skylight_subtracted_ignoring_rain_and_thunder;
         if (this.mc.thePlayer.is_cursed && vignette_brightness > 0.0F) {
            vignette_brightness = 0.0F;
         }

         if ((Minecraft.isFancyGraphicsEnabled() || this.mc.thePlayer.is_cursed) && this.mc.gameSettings.gui_mode != 2) {
            this.renderVignette(vignette_brightness, var6, var7);
         } else {
            GL11.glBlendFunc(770, 771);
         }

         ItemStack itemstack = this.mc.thePlayer.inventory.armorItemInSlot(3);
         if (this.mc.gameSettings.thirdPersonView == 0 && itemstack != null && itemstack.getItem() != null) {
            if (itemstack.itemID == Block.pumpkin.blockID)
            {
               this.renderPumpkinBlur(var6, var7);
            }
            else
            {
               itemstack.getItem().renderHelmetOverlay(itemstack, mc.thePlayer, scaledresolution, par1, par2, par3, par4);
            }
         }

         if (!this.mc.thePlayer.isPotionActive(Potion.confusion)) {
            float var10000 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * par1;
         }

         float interpolated_vision_dimming = this.mc.thePlayer.vision_dimming - par1 * 0.01F;
         if (interpolated_vision_dimming > 0.01F) {
            this.renderVisionDim(var6, var7, Math.min(interpolated_vision_dimming, 0.9F));
         }

         if (this.mc.thePlayer.runegate_counter > 0) {
            this.renderRunegateEffect(var6, var7);
         }

         int eye_block_x = this.mc.thePlayer.getBlockPosX();
         int eye_block_y = MathHelper.floor_double(this.mc.thePlayer.getEyePosY() - 0.05000000074505806);
         int eye_block_z = this.mc.thePlayer.getBlockPosZ();
         Block block = this.mc.theWorld.getBlock(eye_block_x, eye_block_y, eye_block_z);
         boolean prevent_xray_vision = this.mc.theWorld.isBlockSolid(block, eye_block_x, eye_block_y, eye_block_z) && block != Block.glass;
         if (prevent_xray_vision && !block.isOpaqueStandardFormCube(this.mc.theWorld, eye_block_x, eye_block_y, eye_block_z)) {
            prevent_xray_vision = false;
         }

         if (prevent_xray_vision) {
            GL11.glDisable(2929);
            GL11.glDisable(3008);
            drawRect(0, 0, var6, var7, -16777216);
            GL11.glEnable(3008);
            GL11.glEnable(2929);
         }

         int sleep_counter = this.mc.thePlayer.falling_asleep_counter;
         int var11;
         if (sleep_counter > 0) {
            var11 = Math.min(255 * sleep_counter / 100, 255);
            this.mc.mcProfiler.startSection("sleep");
            GL11.glDisable(2929);
            GL11.glDisable(3008);
            drawRect(0, 0, var6, var7, var11 << 24);
            GL11.glEnable(3008);
            GL11.glEnable(2929);
            this.mc.mcProfiler.endSection();
         }

         int var13;
         if (!this.mc.playerController.enableEverythingIsScrewedUpMode() && this.mc.gameSettings.gui_mode == 0) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (display_overburdened_cpu_icon_until_ms >= System.currentTimeMillis()) {
               this.mc.getTextureManager().bindTexture(MITE_icons);
               this.drawTexturedModalRect(var6 - 17, 2, 0, 0, 16, 16);
            }

            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            InventoryPlayer var31 = this.mc.thePlayer.inventory;
            this.zLevel = -90.0F;
            this.drawTexturedModalRect(var6 / 2 - 91, var7 - 22, 0, 0, 182, 22);
            if (this.mc.playerController.auto_use_mode_item != null) {
               GL11.glColor4f(0.8F, 1.0F, 0.7F, 1.0F);
            }

            this.drawTexturedModalRect(var6 / 2 - 91 - 1 + var31.currentItem * 20, var7 - 22 - 1, 0, 22, 24, 22);
            if (this.mc.playerController.auto_use_mode_item != null) {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            this.mc.getTextureManager().bindTexture(icons);
            GL11.glEnable(3042);
            GL11.glBlendFunc(775, 769);
            this.drawTexturedModalRect(var6 / 2 - 7, var7 / 2 - 7, 0, 0, 16, 16);
            GL11.glDisable(3042);
            this.mc.mcProfiler.startSection("bossHealth");
            this.renderBossHealth();
            this.mc.mcProfiler.endSection();
            if (this.mc.playerController.shouldDrawHUD()) {
               this.func_110327_a(var6, var7);
            }

            GL11.glDisable(3042);
            this.mc.mcProfiler.startSection("actionBar");
            GL11.glEnable(32826);
            RenderHelper.enableGUIStandardItemLighting();

            for(var11 = 0; var11 < 9; ++var11) {
               int var12 = var6 / 2 - 90 + var11 * 20 + 2;
               var13 = var7 - 16 - 3;
               this.renderInventorySlot(var11, var12, var13, par1);
            }

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(32826);
            this.mc.mcProfiler.endSection();
         }

         int var32 = 16777215;
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         var11 = var6 / 2 - 91;
         int var14;
         int var15;
         int var17;
         int var16;
         float var33;
         short var37;
         if (this.mc.thePlayer.isRidingHorse() && this.mc.gameSettings.gui_mode == 0) {
            this.mc.mcProfiler.startSection("jumpBar");
            this.mc.getTextureManager().bindTexture(Gui.icons);
            var33 = this.mc.thePlayer.getHorseJumpPower();
            var37 = 182;
            var14 = (int)(var33 * (float)(var37 + 1));
            var15 = var7 - 32 + 3;
            this.drawTexturedModalRect(var11, var15, 0, 84, var37, 5);
            if (var14 > 0) {
               this.drawTexturedModalRect(var11, var15, 0, 89, var14, 5);
            }

            this.mc.mcProfiler.endSection();
         } else if (this.mc.playerController.func_78763_f() && this.mc.gameSettings.gui_mode == 0 && !(this.mc.currentScreen instanceof GuiScreenBook)) {
            this.mc.mcProfiler.startSection("expBar");
            this.mc.getTextureManager().bindTexture(Gui.icons);
            var37 = 182;
            var14 = (int)(this.mc.thePlayer.getLevelProgress() * (float)(var37 + 1));
            var15 = var7 - 32 + 3;
            this.drawTexturedModalRect(var11, var15, 0, 64, var37, 5);
            if (var14 > 0) {
               this.drawTexturedModalRect(var11, var15, 0, 69, var14, 5);
            }

            this.mc.mcProfiler.endSection();
            if (this.mc.thePlayer.getExperienceLevel() != 0 && !(this.mc.currentScreen instanceof GuiScreenBook)) {
               this.mc.mcProfiler.startSection("expLevel");
               boolean var35 = false;
               var14 = var35 ? 16777215 : 8453920;
               if (this.mc.thePlayer.getExperienceLevel() < 0) {
                  var14 = 16716563;
               }

               String var42 = "" + this.mc.thePlayer.getExperienceLevel();
               var16 = (var6 - fontRenderer.getStringWidth(var42)) / 2;
               var17 = var7 - 31 - 4;
               boolean var18 = false;
               fontRenderer.drawString(var42, var16 + 1, var17, 0);
               fontRenderer.drawString(var42, var16 - 1, var17, 0);
               fontRenderer.drawString(var42, var16, var17 + 1, 0);
               fontRenderer.drawString(var42, var16, var17 - 1, 0);
               fontRenderer.drawString(var42, var16, var17, var14);
               this.mc.mcProfiler.endSection();
            }
         }

         int var22;
         if (this.curse_notification_counter > 0 && this.mc.thePlayer.is_cursed) {
            FontRenderer fr = this.mc.fontRenderer;
            ScaledResolution sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
            String text = Translator.get(this.mc.thePlayer.is_cursed ? "curse.notify" : "curse.lifted");
            var14 = var7 - 59;
            if (!this.mc.playerController.shouldDrawHUD()) {
               var14 += 14;
            }

            var22 = (int)((float)this.curse_notification_counter * 256.0F / 10.0F);
            if (var22 > 255) {
               var22 = 255;
            }

            if (var22 > 0) {
               GL11.glEnable(3042);
               GL11.glBlendFunc(770, 771);
               fr.drawStringWithShadow(EnumChatFormatting.DARK_PURPLE + text, (sr.getScaledWidth() - fr.getStringWidth(text)) / 2, var14, 16777215 + (var22 << 24));
               GL11.glDisable(3042);
            }

            this.remainingHighlightTicks = 0;
         }

         String var36;
         if (this.mc.gameSettings.heldItemTooltips && this.mc.gameSettings.gui_mode == 0) {
            this.mc.mcProfiler.startSection("toolHighlight");
            if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
               var36 = this.highlightingItemStack.getMITEStyleDisplayName();
               var13 = (var6 - fontRenderer.getStringWidth(var36)) / 2;
               var14 = var7 - 59;
               if (!this.mc.playerController.shouldDrawHUD()) {
                  var14 += 14;
               }

               var15 = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);
               if (var15 > 255) {
                  var15 = 255;
               }

               if (var15 > 0) {
                  GL11.glPushMatrix();
                  GL11.glEnable(3042);
                  GL11.glBlendFunc(770, 771);
                  fontRenderer.drawStringWithShadow(var36, var13, var14, 16777215 + (var15 << 24));
                  FontRenderer font = highlightingItemStack.getItem().getFontRenderer(highlightingItemStack);
                  if (font != null)
                  {
                     var14 = (var13 - font.getStringWidth(var36)) / 2;
                     font.drawStringWithShadow(var36, var13, var14, 16777215 + (var15 << 24));
                  }
                  else
                  {
                     fontRenderer.drawStringWithShadow(var36, var13, var14, 16777215 + (var15 << 24));
                  }

                  GL11.glDisable(3042);
                  GL11.glPopMatrix();
               }

               this.last_highlighting_item_stack_text = var36;
            }

            if (this.highlightingItemStack == null) {
               this.last_highlighting_item_stack_text = null;
            }

            this.mc.mcProfiler.endSection();
         }

         if (this.mc.isDemo()) {
            this.mc.mcProfiler.startSection("demo");
            var36 = "";
            if (this.mc.theWorld.getTotalWorldTime() >= 120500L) {
               var36 = I18n.getString("demo.demoExpired");
            } else {
               var36 = I18n.getStringParams("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - this.mc.theWorld.getTotalWorldTime())));
            }

            var13 = fontRenderer.getStringWidth(var36);
            fontRenderer.drawStringWithShadow(var36, var6 - var13 - 10, 5, 16777215);
            this.mc.mcProfiler.endSection();
         }

         if (DedicatedServer.tournament_type == EnumTournamentType.score) {
            EntityClientPlayerMP var67 = this.mc.thePlayer;
            var67.delta_tournament_score_opacity -= 2;
            if (this.mc.thePlayer.delta_tournament_score_opacity < 0) {
               this.mc.thePlayer.delta_tournament_score = 0;
               this.mc.thePlayer.delta_tournament_score_opacity = 0;
            }

            this.mc.last_known_delta_tournament_score = this.mc.thePlayer.delta_tournament_score;
            this.mc.last_known_delta_tournament_score_opacity = this.mc.thePlayer.delta_tournament_score_opacity;
            this.mc.last_known_tournament_score = this.mc.thePlayer.tournament_score;
         }

         int row = 0;
         if (Minecraft.getErrorMessage() != null) {
            this.drawString(fontRenderer, Minecraft.getErrorMessage(), 2, 2 + 10 * row++, 16716563);
            this.drawString(fontRenderer, "Press [c] to clear error message.", 2, 2 + 10 * row++, 16716563);
         }

         if (this.mc.gameSettings.showDebugInfo && this.mc.gameSettings.gui_mode == 0) {
            if (DedicatedServer.tournament_type == EnumTournamentType.score) {
               this.drawTournamentScore(row++, 2, fontRenderer);
            }

            if (allotted_time >= 0) {
               this.drawAllottedTime(row++, 2, fontRenderer);
            }
         }

         Minecraft var10003;
         StringBuilder var68;
         if (Minecraft.inDevMode() && this.mc.gameSettings.gui_mode == 0) {
            if (server_load >= 0) {
               ScaledResolution sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
               String text = server_load + "%";
               this.drawString(fontRenderer, text, sr.getScaledWidth() - fontRenderer.getStringWidth(text) - 2, 2, 14737632);
            }

            var68 = (new StringBuilder()).append("Legs (").append(MathHelper.floor_double(this.mc.thePlayer.posX)).append(", ").append(MathHelper.floor_double(this.mc.thePlayer.posY - (double)this.mc.thePlayer.yOffset)).append(", ").append(MathHelper.floor_double(this.mc.thePlayer.posZ)).append(")  yaw=").append(StringHelper.formatFloat(this.mc.thePlayer.rotationYaw, 1, 1)).append("  ").append(this.mc.thePlayer.getDirectionFromYaw()).append(" pitch=").append(StringHelper.formatFloat(this.mc.thePlayer.rotationPitch, 1, 1)).append("   FPS=");
            var10003 = this.mc;
            var68 = var68.append(Minecraft.last_fps).append(" (");
            var10003 = this.mc;
            this.drawString(fontRenderer, var68.append(Minecraft.last_fp10s).append(")").toString(), 2, 2 + 10 * row++, 14737632);
            if (Debug.flag) {
               this.drawString(fontRenderer, "FLAG", 320, 2, 16716563);
            }
         }

         if (Debug.is_active && this.mc.gameSettings.gui_mode == 0) {
            this.drawString(fontRenderer, "Counter: " + Debug.general_counter, 2, 2 + 10 * row++, 14737632);
            if (Debug.biome_info != null) {
               this.drawString(fontRenderer, Debug.biome_info, 2, 2 + 10 * row++, 14737632);
            }

            if (Debug.selected_object_info != null) {
               this.drawString(fontRenderer, Debug.selected_object_info, 2, 2 + 10 * row++, 14737632);
            }

            if (Debug.equipped_item_info != null) {
               this.drawString(fontRenderer, Debug.equipped_item_info, 2, 2 + 10 * row++, 14737632);
            }

            if (Debug.general_info != null) {
               this.drawString(fontRenderer, Debug.general_info, 2, 2 + 10 * row++, 14737632);
            }

            if (Debug.general_info_client != null) {
               this.drawString(fontRenderer, "[Client] " + Debug.general_info_client, 2, 2 + 10 * row++, 14737632);
            }

            if (Debug.general_info_server != null) {
               this.drawString(fontRenderer, "[Server] " + Debug.general_info_server, 2, 2 + 10 * row++, 14737632);
            }

            row += 2;
            this.drawString(fontRenderer, "Player entityId: " + this.mc.thePlayer.entityId + ", username: " + this.mc.thePlayer.username, 2, 2 + 10 * row++, 14737632);
            this.drawString(fontRenderer, "O:" + this.mc.theWorld.worldInfo.getWorldTotalTime(0) + " U:" + this.mc.theWorld.worldInfo.getWorldTotalTime(-2) + " N:" + this.mc.theWorld.worldInfo.getWorldTotalTime(-1) + " E:" + this.mc.theWorld.worldInfo.getWorldTotalTime(1), 2, 2 + 10 * row++, 14737632);
            WeatherEvent event = this.mc.theWorld.getCurrentWeatherEvent();
            String s;
            if (event != null) {
               s = "Current rain: " + event.start + " to " + event.end;
            } else {
               event = this.mc.theWorld.getNextWeatherEvent(false);
               if (event != null) {
                  s = "Next rain: " + event.start + " to " + event.end;
               } else {
                  event = this.mc.theWorld.getPreviousWeatherEvent(false);
                  s = event == null ? "No rain today" : "Previous rain: " + event.start + " to " + event.end;
               }
            }

            this.drawString(fontRenderer, s, 2, 2 + 10 * row++, 14737632);
            event = this.mc.theWorld.getCurrentWeatherEvent(true, false);
            if (event != null) {
               s = "Current storm: " + event.start_of_storm + " to " + event.end_of_storm;
            } else {
               event = this.mc.theWorld.getNextWeatherEvent(true);
               if (event != null) {
                  s = "Next storm: " + event.start_of_storm + " to " + event.end_of_storm;
               } else {
                  event = this.mc.theWorld.getPreviousWeatherEvent(true);
                  s = event == null ? "No storm today" : "Previous storm: " + event.start_of_storm + " to " + event.end_of_storm;
               }
            }

            this.drawString(fontRenderer, s, 2, 2 + 10 * row++, 14737632);
            this.drawString(fontRenderer, "Client Pools: " + AxisAlignedBB.getAABBPool().getlistAABBsize() + " | " + this.mc.theWorld.getWorldVec3Pool().getPoolSize(), 2, 2 + 10 * row++, 14737632);
            this.drawString(fontRenderer, "Server Pools: " + Minecraft.server_pools_string, 2, 2 + 10 * row++, 14737632);
            ++row;
            this.drawString(fontRenderer, "Atk: " + StringHelper.formatFloat(this.mc.thePlayer.calcRawMeleeDamageVs((Entity)null), 1, 1) + "  Prt:" + StringHelper.formatFloat(this.mc.thePlayer.getTotalProtection((DamageSource)null), 1, 1), 2, 2 + 10 * row++, 14737632);
            this.drawString(fontRenderer, "Look: " + MathHelper.getNormalizedVector(this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch, this.mc.theWorld.getWorldVec3Pool()).toStringCompact(), 2, 2 + 10 * row++, 14737632);
            this.drawString(fontRenderer, "fxLayers" + this.mc.effectRenderer.getStatsString(), 2, 2 + 10 * row++, 14737632);
            Chunk chunk = this.mc.thePlayer.getChunkFromPosition();
            this.drawString(fontRenderer, "Chunk: " + chunk.xPosition + "," + chunk.zPosition + " [" + (this.mc.thePlayer.getFootBlockPosY() >> 4) + "] FPP=" + StringHelper.formatDouble((double)EntityRenderer.getProximityToNearestFogPost(this.mc.thePlayer), 3, 3), 2, 2 + 10 * row++, 14737632);
            MinecraftServer mc_server = MinecraftServer.getServer();
            if (mc_server != null) {
               WorldServer world_server = mc_server.worldServerForDimension(this.mc.thePlayer.dimension);
               this.drawString(fontRenderer, "Mobs high: " + world_server.countMobs(false, true) + " / " + world_server.last_mob_spawn_limit_at_60_or_higher, 2, 2 + 10 * row++, 14737632);
               this.drawString(fontRenderer, "Mobs low:  " + world_server.countMobs(true, false) + " / " + world_server.last_mob_spawn_limit_under_60, 2, 2 + 10 * row++, 14737632);
            }

            this.drawString(fontRenderer, "Mem: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L + " / " + Runtime.getRuntime().totalMemory() / 1024L / 1024L, 2, 2 + 10 * row++, 14737632);
         } else if (this.mc.gameSettings.showDebugInfo && this.mc.gameSettings.gui_mode == 0) {
            this.mc.mcProfiler.startSection("debug");
            this.mc.mcProfiler.endSection();
            if (DedicatedServer.tournament_type != EnumTournamentType.score && allotted_time < 0) {
               var68 = (new StringBuilder()).append("");
               var10003 = this.mc;
               this.drawString(fontRenderer, var68.append(Minecraft.last_fps).toString(), 2, 2 + 10 * row++, 14737632);
            }
         }

         if (this.recordPlayingUpFor > 0) {
            this.mc.mcProfiler.startSection("overlayMessage");
            var33 = (float)this.recordPlayingUpFor - par1;
            var13 = (int)(var33 * 255.0F / 20.0F);
            if (var13 > 255) {
               var13 = 255;
            }

            if (var13 > 8) {
               GL11.glPushMatrix();
               GL11.glTranslatef((float)(var6 / 2), (float)(var7 - 68), 0.0F);
               GL11.glEnable(3042);
               GL11.glBlendFunc(770, 771);
               var14 = 16777215;
               if (this.recordIsPlaying) {
                  var14 = Color.HSBtoRGB(var33 / 50.0F, 0.7F, 0.6F) & 16777215;
               }

               fontRenderer.drawString(this.recordPlaying, -fontRenderer.getStringWidth(this.recordPlaying) / 2, -4, var14 + (var13 << 24 & -16777216));
               GL11.glDisable(3042);
               GL11.glPopMatrix();
            }

            this.mc.mcProfiler.endSection();
         }

         ScoreObjective var43 = this.mc.theWorld.getScoreboard().func_96539_a(1);
         if (var43 != null) {
            this.func_96136_a(var43, var7, var6, fontRenderer);
         }

         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glDisable(3008);
         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, (float)(var7 - 48), 0.0F);
         this.mc.mcProfiler.startSection("chat");
         this.persistantChatGUI.drawChat(this.updateCounter);
         this.mc.mcProfiler.endSection();
         GL11.glPopMatrix();
         var43 = this.mc.theWorld.getScoreboard().func_96539_a(0);
         if (this.mc.gameSettings.keyBindPlayerList.pressed && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.playerInfoList.size() > 1 || var43 != null)) {
            this.mc.mcProfiler.startSection("playerList");
            NetClientHandler var41 = this.mc.thePlayer.sendQueue;
            List var44 = var41.playerInfoList;
            var15 = var41.currentServerMaxPlayers;
            var16 = var15;

            for(var17 = 1; var16 > 20; var16 = (var15 + var17 - 1) / var17) {
               ++var17;
            }

            int var45 = 300 / var17;
            if (var45 > 150) {
               var45 = 150;
            }

            int var19 = (var6 - var17 * var45) / 2;
            byte var47 = 10;
            drawRect(var19 - 1, var47 - 1, var19 + var45 * var17, var47 + 9 * var16, Integer.MIN_VALUE);
            int players_skipped = 0;

            for(int var21 = 0; var21 < var15; ++var21) {
               var22 = var19 + var21 % var17 * var45;
               int var23 = var47 + var21 / var17 * 9;
               int var23_alt = var47 + (var21 - players_skipped) / var17 * 9;
               drawRect(var22, var23, var22 + var45 - 1, var23 + 8, 553648127);
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               GL11.glEnable(3008);
               if (var21 < var44.size()) {
                  GuiPlayerInfo var49 = (GuiPlayerInfo)var44.get(var21);
                  ScorePlayerTeam var48 = this.mc.theWorld.getScoreboard().getPlayersTeam(var49.name);
                  String var52 = ScorePlayerTeam.formatPlayerName(var48, var49.name);
                  if ("avernite".equals(var52) && DedicatedServer.isTournament()) {
                     ++players_skipped;
                  } else {
                     fontRenderer.drawStringWithShadow(var52, var22, var23_alt, 16777215);
                     int var27;
                     int var28;
                     if (var43 != null) {
                        var27 = var22 + fontRenderer.getStringWidth(var52) + 5;
                        var28 = var22 + var45 - 12 - 5;
                        if (var28 - var27 > 5) {
                           Score var29 = var43.getScoreboard().func_96529_a(var49.name, var43);
                           String var30 = EnumChatFormatting.YELLOW + "" + var29.getScorePoints();
                           fontRenderer.drawStringWithShadow(var30, var28 - fontRenderer.getStringWidth(var30), var23_alt, 16777215);
                        }
                     } else {
                        var27 = var22 + fontRenderer.getStringWidth(var52) + 5;
                        var28 = var22 + var45 - 12 - 5;
                        if (var28 - var27 > 5) {
                           String level;
                           if (var49.level < 0) {
                              level = EnumChatFormatting.RED + "" + var49.level;
                           } else if (var49.level == 0) {
                              level = EnumChatFormatting.GRAY + "" + var49.level;
                           } else {
                              level = EnumChatFormatting.GREEN + "+" + var49.level;
                           }

                           fontRenderer.drawStringWithShadow(level, var28 - fontRenderer.getStringWidth(level), var23_alt, 16777215);
                        }
                     }

                     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                     this.mc.getTextureManager().bindTexture(icons);
                     byte var53 = 0;
                     boolean var51 = false;
                     byte var50;
                     if (var49.responseTime < 0) {
                        var50 = 5;
                     } else if (var49.responseTime < 150) {
                        var50 = 0;
                     } else if (var49.responseTime < 300) {
                        var50 = 1;
                     } else if (var49.responseTime < 600) {
                        var50 = 2;
                     } else if (var49.responseTime < 1000) {
                        var50 = 3;
                     } else {
                        var50 = 4;
                     }

                     this.zLevel += 100.0F;
                     this.drawTexturedModalRect(var22 + var45 - 12, var23_alt, 0 + var53 * 10, 176 + var50 * 8, 10, 8);
                     this.zLevel -= 100.0F;
                  }
               }
            }
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glDisable(2896);
         GL11.glEnable(3008);
      }
   }

   private void func_96136_a(ScoreObjective par1ScoreObjective, int par2, int par3, FontRenderer par4FontRenderer) {
      Scoreboard var5 = par1ScoreObjective.getScoreboard();
      Collection var6 = var5.func_96534_i(par1ScoreObjective);
      if (var6.size() <= 15) {
         int var7 = par4FontRenderer.getStringWidth(par1ScoreObjective.getDisplayName());

         String var11;
         for(Iterator var8 = var6.iterator(); var8.hasNext(); var7 = Math.max(var7, par4FontRenderer.getStringWidth(var11))) {
            Score var9 = (Score)var8.next();
            ScorePlayerTeam var10 = var5.getPlayersTeam(var9.getPlayerName());
            var11 = ScorePlayerTeam.formatPlayerName(var10, var9.getPlayerName()) + ": " + EnumChatFormatting.RED + var9.getScorePoints();
         }

         int var22 = var6.size() * par4FontRenderer.FONT_HEIGHT;
         int var23 = par2 / 2 + var22 / 3;
         byte var25 = 3;
         int var24 = par3 - var7 - var25;
         int var12 = 0;
         Iterator var13 = var6.iterator();

         while(var13.hasNext()) {
            Score var14 = (Score)var13.next();
            ++var12;
            ScorePlayerTeam var15 = var5.getPlayersTeam(var14.getPlayerName());
            String var16 = ScorePlayerTeam.formatPlayerName(var15, var14.getPlayerName());
            String var17 = EnumChatFormatting.RED + "" + var14.getScorePoints();
            int var19 = var23 - var12 * par4FontRenderer.FONT_HEIGHT;
            int var20 = par3 - var25 + 2;
            drawRect(var24 - 2, var19, var20, var19 + par4FontRenderer.FONT_HEIGHT, 1342177280);
            par4FontRenderer.drawString(var16, var24, var19, 553648127);
            par4FontRenderer.drawString(var17, var20 - par4FontRenderer.getStringWidth(var17), var19, 553648127);
            if (var12 == var6.size()) {
               String var21 = par1ScoreObjective.getDisplayName();
               drawRect(var24 - 2, var19 - par4FontRenderer.FONT_HEIGHT - 1, var20, var19 - 1, 1610612736);
               drawRect(var24 - 2, var19 - 1, var20, var19, 1342177280);
               par4FontRenderer.drawString(var21, var24 + var7 / 2 - par4FontRenderer.getStringWidth(var21) / 2, var19 - par4FontRenderer.FONT_HEIGHT, 553648127);
            }
         }
      }

   }

   private void func_110327_a(int par1, int par2) {
      boolean var3 = this.mc.thePlayer.hurtResistantTime / 3 % 2 == 1;
      if (this.mc.thePlayer.hurtResistantTime < 10) {
         var3 = false;
      }

      int var4 = MathHelper.ceiling_float_int(this.mc.thePlayer.getHealth());
      int var5 = MathHelper.ceiling_float_int(this.mc.thePlayer.prevHealth);
      this.rand.setSeed((long)(this.updateCounter * 312871));
      FoodStats var7 = this.mc.thePlayer.getFoodStats();
      int var8 = var7.getNutrition();
      AttributeInstance var10 = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
      int var11 = par1 / 2 - 91;
      int var12 = par1 / 2 + 91;
      int var13 = par2 - 39;
      float var14 = (float)var10.getAttributeValue();
      float var15 = this.mc.thePlayer.getAbsorptionAmount();
      int var16 = MathHelper.ceiling_float_int((var14 + var15) / 2.0F / 10.0F);
      int var17 = Math.max(10 - (var16 - 2), 3);
      int var18 = var13 - (var16 - 1) * var17 - 10;
      float var19 = var15;
      float total_protection = ForgeHooks.getTotalArmorValue(mc.thePlayer);
      int var20 = MathHelper.ceiling_float_int(total_protection);
      int var21 = -1;
      if (this.mc.thePlayer.isPotionActive(Potion.regeneration)) {
         var21 = this.updateCounter % MathHelper.ceiling_float_int(var14 + 5.0F);
      }

      this.mc.mcProfiler.startSection("armor");

      int var23;
      int var22;
      for(var22 = 0; var22 < 10; ++var22) {
         if (total_protection > 0.0F || this.mc.thePlayer.isWearingArmor()) {
            var23 = var11 + var22 * 8;
            if (var22 * 2 + 1 < var20) {
               this.drawTexturedModalRect(var23, var18, 34, 9, 9, 9);
            }

            if (var22 * 2 + 1 == var20) {
               this.drawTexturedModalRect(var23, var18, 25, 9, 9, 9);
            }

            if (var22 * 2 + 1 > var20) {
               this.drawTexturedModalRect(var23, var18, 16, 9, 9, 9);
            }
         }
      }

      this.mc.mcProfiler.endStartSection("health");

      int var25;
      int var27;
      int var26;
      int var28;
      for(var22 = MathHelper.ceiling_float_int((var14 + var15) / 2.0F) - 1; var22 >= 0; --var22) {
         var23 = 16;
         if (this.mc.thePlayer.isPotionActive(Potion.poison)) {
            var23 += 36;
         } else if (this.mc.thePlayer.isPotionActive(Potion.wither)) {
            var23 += 72;
         }

         byte var24 = 0;
         if (var3) {
            var24 = 1;
         }

         var25 = MathHelper.ceiling_float_int((float)(var22 + 1) / 10.0F) - 1;
         var26 = var11 + var22 % 10 * 8;
         var27 = var13 - var25 * var17;
         if (var4 <= 4) {
            var27 += this.rand.nextInt(2);
         }

         if (var22 == var21) {
            var27 -= 2;
         }

         var28 = 0;
         if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
            var28 = 5;
         }

         if ((float)var22 < this.mc.thePlayer.getMaxHealth() / 2.0F) {
            this.drawTexturedModalRect(var26, var27, 16 + var24 * 9, 9 * var28, 9, 9);
         }

         if (var3) {
            if (var22 * 2 + 1 < var5) {
               this.drawTexturedModalRect(var26, var27, var23 + 54, 9 * var28, 9, 9);
            }

            if (var22 * 2 + 1 == var5) {
               this.drawTexturedModalRect(var26, var27, var23 + 63, 9 * var28, 9, 9);
            }
         }

         if (var19 > 0.0F) {
            if (var19 == var15 && var15 % 2.0F == 1.0F) {
               this.drawTexturedModalRect(var26, var27, var23 + 153, 9 * var28, 9, 9);
            } else {
               this.drawTexturedModalRect(var26, var27, var23 + 144, 9 * var28, 9, 9);
            }

            var19 -= 2.0F;
         } else {
            if (var22 * 2 + 1 < var4) {
               this.drawTexturedModalRect(var26, var27, var23 + 36, 9 * var28, 9, 9);
            }

            if (var22 * 2 + 1 == var4) {
               this.drawTexturedModalRect(var26, var27, var23 + 45, 9 * var28, 9, 9);
            }
         }
      }

      Entity var34 = this.mc.thePlayer.ridingEntity;
      if (var34 != null && !(var34 instanceof EntityBoat)) {
         if (var34 instanceof EntityLivingBase) {
            this.mc.mcProfiler.endStartSection("mountHealth");
            EntityLivingBase var38 = (EntityLivingBase)var34;
            var28 = (int)Math.ceil((double)var38.getHealth());
            float var37 = var38.getMaxHealth();
            var26 = (int)(var37 + 0.5F) / 2;
            if (var26 > 30) {
               var26 = 30;
            }

            var27 = var13;

            for(int var39 = 0; var26 > 0; var39 += 20) {
               int var29 = Math.min(var26, 10);
               var26 -= var29;

               for(int var30 = 0; var30 < var29; ++var30) {
                  byte var31 = 52;
                  byte var32 = 0;
                  int var33 = var12 - var30 * 8 - 9;
                  this.drawTexturedModalRect(var33, var27, var31 + var32 * 9, 9, 9, 9);
                  if (var30 * 2 + 1 + var39 < var28) {
                     this.drawTexturedModalRect(var33, var27, var31 + 36, 9, 9, 9);
                  }

                  if (var30 * 2 + 1 + var39 == var28) {
                     this.drawTexturedModalRect(var33, var27, var31 + 45, 9, 9, 9);
                  }
               }

               var27 -= 10;
            }
         }
      } else {
         this.mc.mcProfiler.endStartSection("food");

         for(var23 = 0; var23 < 10; ++var23) {
            var28 = var13;
            var25 = 16;
            byte var36 = 0;
            if (this.mc.thePlayer.isPotionActive(Potion.hunger)) {
               var25 += 36;
               var36 = 13;
            }

            if (this.mc.thePlayer.isHungry() && this.updateCounter % (var8 * 3 + 1) == 0) {
               var28 = var13 + (this.rand.nextInt(3) - 1);
            }

            var27 = var12 - var23 * 8 - 9;
            if (var23 < this.mc.thePlayer.foodStats.getNutritionLimit() / 2) {
               this.drawTexturedModalRect(var27, var28, 16 + var36 * 9, 27, 9, 9);
            }

            if (var23 * 2 + 1 < var8) {
               this.drawTexturedModalRect(var27, var28, var25 + 36, 27, 9, 9);
            }

            if (var23 * 2 + 1 == var8) {
               this.drawTexturedModalRect(var27, var28, var25 + 45, 27, 9, 9);
            }
         }
      }

      this.mc.mcProfiler.endStartSection("air");
      if (this.mc.thePlayer.isInsideOfMaterial(Material.water)) {
         var23 = this.mc.thePlayer.getAir();
         var28 = MathHelper.ceiling_double_int((double)(var23 - 2) * 10.0 / 300.0);
         var25 = MathHelper.ceiling_double_int((double)var23 * 10.0 / 300.0) - var28;

         for(var26 = 0; var26 < var28 + var25; ++var26) {
            if (var26 < var28) {
               this.drawTexturedModalRect(var12 - var26 * 8 - 9, var18, 16, 18, 9, 9);
            } else {
               this.drawTexturedModalRect(var12 - var26 * 8 - 9, var18, 25, 18, 9, 9);
            }
         }
      }

      this.mc.mcProfiler.endSection();
   }

   private void renderBossHealth() {
      if (BossStatus.bossName != null && BossStatus.statusBarLength > 0) {
         --BossStatus.statusBarLength;
         FontRenderer var1 = this.mc.fontRenderer;
         ScaledResolution var2 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
         int var3 = var2.getScaledWidth();
         short var4 = 182;
         int var5 = var3 / 2 - var4 / 2;
         int var6 = (int)(BossStatus.healthScale * (float)(var4 + 1));
         byte var7 = 12;
         this.drawTexturedModalRect(var5, var7, 0, 74, var4, 5);
         this.drawTexturedModalRect(var5, var7, 0, 74, var4, 5);
         if (var6 > 0) {
            this.drawTexturedModalRect(var5, var7, 0, 79, var6, 5);
         }

         String var8 = BossStatus.bossName;
         var1.drawStringWithShadow(var8, var3 / 2 - var1.getStringWidth(var8) / 2, var7 - 10, 16777215);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(icons);
      }

   }

   private void renderPumpkinBlur(int par1, int par2) {
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      GL11.glBlendFunc(770, 771);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(3008);
      this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
      Tessellator var3 = Tessellator.instance;
      var3.startDrawingQuads();
      var3.addVertexWithUV(0.0, (double)par2, -90.0, 0.0, 1.0);
      var3.addVertexWithUV((double)par1, (double)par2, -90.0, 1.0, 1.0);
      var3.addVertexWithUV((double)par1, 0.0, -90.0, 1.0, 0.0);
      var3.addVertexWithUV(0.0, 0.0, -90.0, 0.0, 0.0);
      var3.draw();
      GL11.glDepthMask(true);
      GL11.glEnable(2929);
      GL11.glEnable(3008);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderVisionDim(int par1, int par2, float vision_dimming) {
      boolean gl_texture_2d = GL11.glGetBoolean(3553);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      GL11.glBlendFunc(770, 771);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, Math.min(vision_dimming, 1.0F));
      GL11.glDisable(3008);
      Tessellator var3 = Tessellator.instance;
      var3.startDrawingQuads();
      var3.addVertexWithUV(0.0, (double)par2, -90.0, 0.0, 1.0);
      var3.addVertexWithUV((double)par1, (double)par2, -90.0, 1.0, 1.0);
      var3.addVertexWithUV((double)par1, 0.0, -90.0, 1.0, 0.0);
      var3.addVertexWithUV(0.0, 0.0, -90.0, 0.0, 0.0);
      var3.draw();
      GL11.glDepthMask(true);
      GL11.glEnable(2929);
      GL11.glEnable(3008);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (gl_texture_2d) {
         GL11.glEnable(3553);
      }

   }

   private void renderRunegateEffect(int par1, int par2) {
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      GL11.glBlendFunc(770, 771);
      short r;
      short g;
      short b;
      if (this.mc.theWorld.isOverworld()) {
         r = 53;
         g = 159;
         b = 255;
      } else if (this.mc.theWorld.isUnderworld()) {
         r = 68;
         g = 1;
         b = 180;
      } else if (this.mc.theWorld.isTheNether()) {
         r = 228;
         g = 123;
         b = 78;
      } else {
         r = 255;
         g = 255;
         b = 255;
      }

      GL11.glColor4f((float)r / 255.0F, (float)g / 255.0F, (float)b / 255.0F, Math.min((float)this.mc.thePlayer.runegate_counter / 20.0F, 1.0F));
      GL11.glDisable(3008);
      GL11.glDisable(3553);
      Tessellator var3 = Tessellator.instance;
      var3.startDrawingQuads();
      var3.addVertexWithUV(0.0, (double)par2, -90.0, 0.0, 1.0);
      var3.addVertexWithUV((double)par1, (double)par2, -90.0, 1.0, 1.0);
      var3.addVertexWithUV((double)par1, 0.0, -90.0, 1.0, 0.0);
      var3.addVertexWithUV(0.0, 0.0, -90.0, 0.0, 0.0);
      var3.draw();
      GL11.glDepthMask(true);
      GL11.glEnable(2929);
      GL11.glEnable(3008);
      GL11.glEnable(3553);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderVignette(float par1, int par2, int par3) {
      par1 = 1.0F - par1;
      if (par1 < 0.0F) {
         par1 = 0.0F;
      }

      if (par1 > 1.0F) {
         par1 = 1.0F;
      }

      this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(par1 - this.prevVignetteBrightness) * 0.01);
      if (this.mc.theWorld.provider.drawGuiVignette()) {
         GL11.glDisable(2929);
         GL11.glDepthMask(false);
         GL11.glBlendFunc(0, 769);
         GL11.glColor4f(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
         this.mc.getTextureManager().bindTexture(vignetteTexPath);
         Tessellator var4 = Tessellator.instance;
         var4.startDrawingQuads();
         var4.addVertexWithUV(0.0, (double)par3, -90.0, 0.0, 1.0);
         var4.addVertexWithUV((double)par2, (double)par3, -90.0, 1.0, 1.0);
         var4.addVertexWithUV((double)par2, 0.0, -90.0, 1.0, 0.0);
         var4.addVertexWithUV(0.0, 0.0, -90.0, 0.0, 0.0);
         var4.draw();
      } else {
         this.prevVignetteBrightness = 1.0F;
      }

      GL11.glDepthMask(true);
      GL11.glEnable(2929);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBlendFunc(770, 771);
   }

   private void func_130015_b(float par1, int par2, int par3) {
      if (par1 < 1.0F) {
         par1 *= par1;
         par1 *= par1;
         par1 = par1 * 0.8F + 0.2F;
      }

      GL11.glDisable(3008);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      GL11.glBlendFunc(770, 771);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, par1);
      Icon var4 = Block.portal.getBlockTextureFromSide(1);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      float var5 = var4.getMinU();
      float var6 = var4.getMinV();
      float var7 = var4.getMaxU();
      float var8 = var4.getMaxV();
      Tessellator var9 = Tessellator.instance;
      var9.startDrawingQuads();
      var9.addVertexWithUV(0.0, (double)par3, -90.0, (double)var5, (double)var8);
      var9.addVertexWithUV((double)par2, (double)par3, -90.0, (double)var7, (double)var8);
      var9.addVertexWithUV((double)par2, 0.0, -90.0, (double)var7, (double)var6);
      var9.addVertexWithUV(0.0, 0.0, -90.0, (double)var5, (double)var6);
      var9.draw();
      GL11.glDepthMask(true);
      GL11.glEnable(2929);
      GL11.glEnable(3008);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderInventorySlot(int par1, int par2, int par3, float par4) {
      ItemStack var5 = this.mc.thePlayer.inventory.mainInventory[par1];
      if (var5 != null) {
         float var6 = (float)var5.animationsToGo - par4;
         if (var6 > 0.0F) {
            GL11.glPushMatrix();
            float var7 = 1.0F + var6 / 5.0F;
            GL11.glTranslatef((float)(par2 + 8), (float)(par3 + 12), 0.0F);
            GL11.glScalef(1.0F / var7, (var7 + 1.0F) / 2.0F, 1.0F);
            GL11.glTranslatef((float)(-(par2 + 8)), (float)(-(par3 + 12)), 0.0F);
         }

         itemRenderer.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), var5, par2, par3);
         if (var6 > 0.0F) {
            GL11.glPopMatrix();
         }

         itemRenderer.renderItemOverlayIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), var5, par2, par3);
      }

   }

   public void updateTick() {
      if (this.recordPlayingUpFor > 0) {
         --this.recordPlayingUpFor;
      }

      ++this.updateCounter;
      if (this.mc.thePlayer != null) {
         ItemStack var1 = this.mc.thePlayer.inventory.getCurrentItemStack();
         if (var1 == null) {
            this.remainingHighlightTicks = 0;
         } else if (var1.getMITEStyleDisplayName().equals(this.last_highlighting_item_stack_text)) {
            if (this.remainingHighlightTicks > 0) {
               --this.remainingHighlightTicks;
            }
         } else {
            this.remainingHighlightTicks = 40;
         }

         this.highlightingItemStack = var1;
         if (this.curse_notification_counter > 0 && this.mc.currentScreen == null) {
            --this.curse_notification_counter;
         }
      }

   }

   public void setRecordPlayingMessage(String par1Str) {
      this.func_110326_a("Now playing: " + par1Str, true);
   }

   public void func_110326_a(String par1Str, boolean par2) {
      this.recordPlaying = par1Str;
      this.recordPlayingUpFor = 60;
      this.recordIsPlaying = par2;
   }

   public GuiNewChat getChatGUI() {
      return this.persistantChatGUI;
   }

   public int getUpdateCounter() {
      return this.updateCounter;
   }

   private void drawTournamentScore(int row, int col, FontRenderer var8) {
      String tournament_score = "" + this.mc.thePlayer.tournament_score;
      this.drawString(var8, tournament_score, col, 2 + 10 * row++, this.mc.thePlayer.tournament_score < 0 ? 16716563 : 8453920);
      if (this.mc.thePlayer.delta_tournament_score != 0 && this.mc.thePlayer.delta_tournament_score_opacity > 4) {
         int effective_opacity = MathHelper.clamp_int(this.mc.thePlayer.delta_tournament_score_opacity, 0, 255) * 256 * 256 * 256;
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         String delta_tournament_score = (this.mc.thePlayer.delta_tournament_score < 1 ? "" : "+") + this.mc.thePlayer.delta_tournament_score;
         --row;
         col += var8.getStringWidth(tournament_score);
         this.drawString(var8, " (", col, 2 + 10 * row, 12632256 + effective_opacity);
         col += var8.getStringWidth(" (");
         this.drawString(var8, delta_tournament_score, col, 2 + 10 * row, this.mc.thePlayer.delta_tournament_score < 0 ? 16716563 + effective_opacity : 8453920 + effective_opacity);
         col += var8.getStringWidth(delta_tournament_score);
         this.drawString(var8, ")", col, 2 + 10 * row, 12632256 + effective_opacity);
         ++row;
         GL11.glDisable(3042);
      }

   }

   private void drawAllottedTime(int row, int col, FontRenderer var8) {
      int seconds = allotted_time / 20;
      int hours = seconds / 3600;
      seconds -= hours * 3600;
      int minutes = seconds / 60;
      seconds -= minutes * 60;
      this.drawString(var8, hours + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds), col, 2 + 10 * row++, this.mc.thePlayer.tournament_score < 0 ? 16716563 : 8453920);
   }
}
