package net.minecraft.client.gui.achievement;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSmallButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.mite.PlayerStatsHelper;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldAchievement;
import net.minecraftforge.common.AchievementPage;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiAchievements extends GuiScreen {
   private static final int guiMapTop;
   private static final int guiMapLeft;
   private static final int guiMapBottom;
   private static final int guiMapRight;
   private static final ResourceLocation achievementTextures;
   protected int achievementsPaneWidth = 256;
   protected int achievementsPaneHeight = 202;
   protected int mouseX;
   protected int mouseY;
   protected double field_74117_m;
   protected double field_74115_n;
   protected double guiMapX;
   protected double guiMapY;
   protected double field_74124_q;
   protected double field_74123_r;
   private int isMouseButtonDown;
   private StatFileWriter statFileWriter;
   public static final int view_mode_player = 0;
   public static final int view_mode_this_world = 1;
   public static final int view_mode_all_worlds = 2;
   public static int view_mode;
   GuiButton button_cycle_view_mode;

   private int currentPage = -1;
   private GuiSmallButton button;
   private LinkedList<Achievement> minecraftAchievements = new LinkedList<Achievement>();

   public GuiAchievements(StatFileWriter par1StatFileWriter) {
      this.statFileWriter = par1StatFileWriter;
      short var2 = 141;
      short var3 = 141;
      this.field_74117_m = this.guiMapX = this.field_74124_q = (double)(AchievementList.openInventory.displayColumn * 24 - var2 / 2 - 12);
      this.field_74115_n = this.guiMapY = this.field_74123_r = (double)(AchievementList.openInventory.displayRow * 24 - var3 / 2);
      minecraftAchievements.clear();
      for (Object achievement : AchievementList.achievementList)
      {
         if (!AchievementPage.isAchievementInPages((Achievement)achievement))
         {
            minecraftAchievements.add((Achievement)achievement);
         }
      }
   }

   public void updateViewModeButtonState() {
      if (Minecraft.theMinecraft.thePlayer.haveAchievementsBeenUnlockedByOtherPlayers()) {
         this.button_cycle_view_mode.drawButton = true;
      } else {
         this.button_cycle_view_mode.drawButton = false;
         view_mode = 0;
      }

      this.button_cycle_view_mode.displayString = I18n.getString(view_mode == 0 ? "gui.stats.thisWorld" : (view_mode == 1 ? (Minecraft.inDevMode() ? "gui.stats.allWorlds" : "gui.stats.player") : "gui.stats.player"));
   }

   public void initGui() {
      view_mode = 0;
      this.buttonList.clear();
      this.buttonList.add(new GuiSmallButton(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, I18n.getString("gui.done")));
      this.buttonList.add(button = new GuiSmallButton(3, (width - achievementsPaneWidth) / 2 + 24, height / 2 + 74, 125, 20, AchievementPage.getTitle(currentPage)));
      this.buttonList.add(this.button_cycle_view_mode = new GuiSmallButton(2, this.width / 2 - 24 - 80, this.height / 2 + 74, 80, 20, ""));
      this.updateViewModeButtonState();
   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.id == 1) {
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
      } else if (par1GuiButton.id == 2) {
         if (++view_mode > (Minecraft.inDevMode() ? 2 : 1)) {
            view_mode = 0;
         }

         this.updateViewModeButtonState();
      } else if (par1GuiButton.id == 3) {
         currentPage++;
         if (currentPage >= AchievementPage.getAchievementPages().size())
         {
            currentPage = -1;
         }
         button.displayString = AchievementPage.getTitle(currentPage);

      }

      super.actionPerformed(par1GuiButton);
   }

   protected void keyTyped(char par1, int par2) {
      if (par2 == this.mc.gameSettings.keyBindInventory.keyCode) {
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
      } else {
         super.keyTyped(par1, par2);
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      this.updateViewModeButtonState();
      if (Mouse.isButtonDown(0)) {
         int var4 = (this.width - this.achievementsPaneWidth) / 2;
         int var5 = (this.height - this.achievementsPaneHeight) / 2;
         int var6 = var4 + 8;
         int var7 = var5 + 17;
         if ((this.isMouseButtonDown == 0 || this.isMouseButtonDown == 1) && par1 >= var6 && par1 < var6 + 224 && par2 >= var7 && par2 < var7 + 155) {
            if (this.isMouseButtonDown == 0) {
               this.isMouseButtonDown = 1;
            } else {
               this.guiMapX -= (double)(par1 - this.mouseX);
               this.guiMapY -= (double)(par2 - this.mouseY);
               this.field_74124_q = this.field_74117_m = this.guiMapX;
               this.field_74123_r = this.field_74115_n = this.guiMapY;
            }

            this.mouseX = par1;
            this.mouseY = par2;
         }

         if (this.field_74124_q < (double)guiMapTop) {
            this.field_74124_q = (double)guiMapTop;
         }

         if (this.field_74123_r < (double)guiMapLeft) {
            this.field_74123_r = (double)guiMapLeft;
         }

         if (this.field_74124_q >= (double)guiMapBottom) {
            this.field_74124_q = (double)(guiMapBottom - 1);
         }

         if (this.field_74123_r >= (double)guiMapRight) {
            this.field_74123_r = (double)(guiMapRight - 1);
         }
      } else {
         this.isMouseButtonDown = 0;
      }

      this.drawDefaultBackground();
      this.genAchievementBackground(par1, par2, par3);
      GL11.glDisable(2896);
      GL11.glDisable(2929);
      this.drawTitle();
      GL11.glEnable(2896);
      GL11.glEnable(2929);
   }

   public void updateScreen() {
      this.field_74117_m = this.guiMapX;
      this.field_74115_n = this.guiMapY;
      double var1 = this.field_74124_q - this.guiMapX;
      double var3 = this.field_74123_r - this.guiMapY;
      if (var1 * var1 + var3 * var3 < 4.0) {
         this.guiMapX += var1;
         this.guiMapY += var3;
      } else {
         this.guiMapX += var1 * 0.85;
         this.guiMapY += var3 * 0.85;
      }

   }

   protected void drawTitle() {
      int var1 = (this.width - this.achievementsPaneWidth) / 2;
      int var2 = (this.height - this.achievementsPaneHeight) / 2;
      this.fontRenderer.drawString("Achievements", var1 + 15, var2 + 5, 4210752);
   }

   private boolean hasAchievementUnlocked(Achievement achievement) {
      if (view_mode == 0) {
         return PlayerStatsHelper.hasAchievementUnlocked(achievement);
      } else {
         return view_mode == 1 ? this.mc.theWorld.worldInfo.hasAchievementUnlocked(achievement) : this.statFileWriter.hasAchievementUnlocked(achievement);
      }
   }

   private boolean canUnlockAchievement(Achievement achievement) {
      return this.haveAllParentAchievementsBeenUnlocked(achievement, true);
   }

   private boolean haveAllParentAchievementsBeenUnlocked(Achievement achievement, boolean include_second_parents) {
      if (include_second_parents && achievement.hasSecondParent() && this.hasAchievementUnlocked(achievement.getSecondParent()) && this.haveAllParentAchievementsBeenUnlocked(achievement.getSecondParent(), true)) {
         return true;
      } else {
         do {
            if (achievement.parentAchievement == null) {
               return true;
            }

            achievement = achievement.parentAchievement;
            if (!this.hasAchievementUnlocked(achievement)) {
               return false;
            }
         } while(!include_second_parents || !achievement.hasSecondParent() || !this.hasAchievementUnlocked(achievement.getSecondParent()) || !this.haveAllParentAchievementsBeenUnlocked(achievement.getSecondParent(), true));

         return true;
      }
   }

   protected void genAchievementBackground(int par1, int par2, float par3) {
      int var4 = MathHelper.floor_double(this.field_74117_m + (this.guiMapX - this.field_74117_m) * (double)par3);
      int var5 = MathHelper.floor_double(this.field_74115_n + (this.guiMapY - this.field_74115_n) * (double)par3);
      if (var4 < guiMapTop) {
         var4 = guiMapTop;
      }

      if (var5 < guiMapLeft) {
         var5 = guiMapLeft;
      }

      if (var4 >= guiMapBottom) {
         var4 = guiMapBottom - 1;
      }

      if (var5 >= guiMapRight) {
         var5 = guiMapRight - 1;
      }

      int var6 = (this.width - this.achievementsPaneWidth) / 2;
      int var7 = (this.height - this.achievementsPaneHeight) / 2;
      int var8 = var6 + 16;
      int var9 = var7 + 17;
      this.zLevel = 0.0F;
      GL11.glDepthFunc(518);
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 0.0F, -200.0F);
      GL11.glEnable(3553);
      GL11.glDisable(2896);
      GL11.glEnable(32826);
      GL11.glEnable(2903);
      int var10 = var4 + 288 >> 4;
      int var11 = var5 + 288 >> 4;
      int var12 = (var4 + 288) % 16;
      int var13 = (var5 + 288) % 16;
      boolean var14 = true;
      boolean var15 = true;
      boolean var16 = true;
      boolean var17 = true;
      boolean var18 = true;
      Random var19 = new Random();

      int var20;
      int var23;
      int var22;
      for(var20 = 0; var20 * 16 - var13 < 155; ++var20) {
         float var21 = 0.6F - (float)(var11 + var20) / 25.0F * 0.3F;
         GL11.glColor4f(var21, var21, var21, 1.0F);

         for(var22 = 0; var22 * 16 - var12 < 224; ++var22) {
            var19.setSeed((long)(1234 + var10 + var22));
            var19.nextInt();
            var23 = var19.nextInt(1 + var11 + var20) + (var11 + var20) / 2;
            Icon var24 = Block.sand.getIcon(0, 0);
            if (var23 <= 37 && var11 + var20 != 35) {
               if (var23 == 22) {
                  if (var19.nextInt(2) == 0) {
                     var24 = Block.oreDiamond.getIcon(0, 0);
                  } else {
                     var24 = Block.oreRedstone.getIcon(0, 0);
                  }
               } else if (var23 == 10) {
                  var24 = Block.oreIron.getIcon(0, 0);
               } else if (var23 == 8) {
                  var24 = Block.oreCoal.getIcon(0, 0);
               } else if (var23 > 4) {
                  var24 = Block.stone.getIcon(0, 0);
               } else if (var23 > 0) {
                  var24 = Block.dirt.getIcon(0, 0);
               }
            } else {
               var24 = Block.bedrock.getIcon(0, 0);
            }

            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.drawTexturedModelRectFromIcon(var8 + var22 * 16 - var12, var9 + var20 * 16 - var13, var24, 16, 16);
         }
      }

      GL11.glEnable(2929);
      GL11.glDepthFunc(515);
      GL11.glDisable(3553);

      int var41;
      int var25;
      for(int pass = 0; pass < 2; ++pass) {
         List<Achievement> achievementList = (currentPage == -1 ? minecraftAchievements : AchievementPage.getAchievementPage(currentPage).getAchievements());
         for (var20 = 0; var20 < achievementList.size(); ++var20) {
            Achievement var31 = achievementList.get(var20);
            if (var31.parentAchievement != null && achievementList.contains(var31.parentAchievement)) {
               var22 = var31.displayColumn * 24 - var4 + 11 + var8;
               var23 = var31.displayRow * 24 - var5 + 11 + var9;
               var41 = var31.parentAchievement.displayColumn * 24 - var4 + 11 + var8;
               var25 = var31.parentAchievement.displayRow * 24 - var5 + 11 + var9;
               if (var31.isFlipped()) {
                  var22 = var31.parentAchievement.displayColumn * 24 - var4 + 11 + var8;
                  var23 = var31.parentAchievement.displayRow * 24 - var5 + 11 + var9;
                  var41 = var31.displayColumn * 24 - var4 + 11 + var8;
                  var25 = var31.displayRow * 24 - var5 + 11 + var9;
               }

               boolean var26 = this.hasAchievementUnlocked(var31);
               boolean var27 = this.canUnlockAchievement(var31);
               if (!this.haveAllParentAchievementsBeenUnlocked(var31, false)) {
                  if (!this.hasAchievementUnlocked(var31.parentAchievement)) {
                     var26 = false;
                  }

                  var27 = false;
               }

               int var29 = -16777216;
               if (var26) {
                  if (pass == 0) {
                     continue;
                  }

                  var29 = -9408400;
               } else if (var27) {
                  var29 = Math.sin((double) (Minecraft.getSystemTime() % 600L) / 600.0 * Math.PI * 2.0) > 0.6 ? -16728064 : -16744448;
               }

               if (pass == 1 && !var26) {
                  continue;
               }

               if (var31.hasSecondParent() && this.hasAchievementUnlocked(var31.parentAchievement)) {
                  this.drawLines(var4, var5, var8, var9, var31, var31.getSecondParent());
               }

               this.drawHorizontalLine(var22, var41, var23, var29);
               this.drawVerticalLine(var41, var23, var25, var29);
         }

            if (var31.hasSecondParent() && !this.hasAchievementUnlocked(var31.parentAchievement)) {
               this.drawLines(var4, var5, var8, var9, var31, var31.getSecondParent());
            }
         }
      }

      Achievement var30 = null;
      RenderItem var32 = new RenderItem();
      RenderHelper.enableGUIStandardItemLighting();
      GL11.glDisable(2896);
      GL11.glEnable(32826);
      GL11.glEnable(2903);

      int var39;
      int var40;
      for (var22 = 0; var22 < AchievementList.achievementList.size(); ++var22) {
         Achievement var34 = (Achievement)AchievementList.achievementList.get(var22);
         var41 = var34.displayColumn * 24 - var4;
         var25 = var34.displayRow * 24 - var5;
         if (var41 >= -24 && var25 >= -24 && var41 <= 224 && var25 <= 155) {
            float var38;
            if (this.hasAchievementUnlocked(var34)) {
               var38 = 1.0F;
               GL11.glColor4f(var38, var38, var38, 1.0F);
            } else if (this.canUnlockAchievement(var34)) {
               var38 = Math.sin((double)(Minecraft.getSystemTime() % 600L) / 600.0 * Math.PI * 2.0) < 0.6 ? 0.6F : 0.8F;
               GL11.glColor4f(var38, var38, var38, 1.0F);
            } else {
               var38 = 0.3F;
               GL11.glColor4f(var38, var38, var38, 1.0F);
            }

            this.mc.getTextureManager().bindTexture(achievementTextures);
            var40 = var8 + var41;
            var39 = var9 + var25;
            if (var34.getSpecial()) {
               this.drawTexturedModalRect(var40 - 2, var39 - 2, 26, 202, 26, 26);
            } else {
               this.drawTexturedModalRect(var40 - 2, var39 - 2, 0, 202, 26, 26);
            }

            if (!this.canUnlockAchievement(var34) && !this.hasAchievementUnlocked(var34)) {
               float var37 = 0.1F;
               GL11.glColor4f(var37, var37, var37, 1.0F);
               var32.renderWithColor = false;
            }

            GL11.glEnable(2896);
            GL11.glEnable(2884);
            var32.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), var34.theItemStack, var40 + 3, var39 + 3);
            GL11.glDisable(2896);
            if (!this.canUnlockAchievement(var34)) {
               var32.renderWithColor = true;
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (par1 >= var8 && par2 >= var9 && par1 < var8 + 224 && par2 < var9 + 155 && par1 >= var40 && par1 <= var40 + 22 && par2 >= var39 && par2 <= var39 + 22) {
               var30 = var34;
            }
         }
      }

      GL11.glDisable(2929);
      GL11.glEnable(3042);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(achievementTextures);
      this.drawTexturedModalRect(var6, var7, 0, 0, this.achievementsPaneWidth, this.achievementsPaneHeight);
      GL11.glPopMatrix();
      this.zLevel = 0.0F;
      GL11.glDepthFunc(515);
      GL11.glDisable(2929);
      GL11.glEnable(3553);
      super.drawScreen(par1, par2, par3);
      if (var30 != null) {
         String var33 = I18n.getString(var30.getName());
         if (Minecraft.inDevMode()) {
            var33 = var33 + " (" + (var30.statId - 5242880) + ")";
         }

         String var35 = var30.getDescription();
         var41 = par1 + 12;
         var25 = par2 - 4;
         if (!this.canUnlockAchievement(var30) && !this.hasAchievementUnlocked(var30)) {
            var40 = Math.max(this.fontRenderer.getStringWidth(var33), 120);

            Achievement refer_to;
            for(refer_to = var30; !this.haveAllParentAchievementsBeenUnlocked(refer_to, true); refer_to = refer_to.parentAchievement) {
            }

            String var36 = I18n.getStringParams("gui.achievement.see", I18n.getString(refer_to.getName()));
            int var28 = this.fontRenderer.splitStringWidth(var36, var40);
            this.drawGradientRect(var41 - 3, var25 - 3, var41 + var40 + 3, var25 + var28 + 12 + 3, -1073741824, -1073741824);
            this.fontRenderer.drawSplitString(var36, var41, var25 + 12, var40, -9416624);
         } else {
            var40 = Math.max(this.fontRenderer.getStringWidth(var33), var30.getTooltipWidth());
            if (this.hasAchievementUnlocked(var30) && view_mode == 1) {
               var40 = Math.max(var40, this.fontRenderer.getStringWidth(this.getTakenByText(var30)));
            }

            var39 = this.fontRenderer.splitStringWidth(var35, var40);
            if (this.hasAchievementUnlocked(var30)) {
               var39 += 12;
            }

            if (var30 == AchievementList.buildBetterPickaxe) {
               var39 += 12;
            }

            this.drawGradientRect(var41 - 3, var25 - 3, var41 + var40 + 3, var25 + var39 + 3 + 12, -1073741824, -1073741824);
            this.fontRenderer.drawSplitString(var35, var41, var25 + 12, var40, !this.canUnlockAchievement(var30) && !this.hasAchievementUnlocked(var30) ? 5263440 : -6250336);
            if (var30 == AchievementList.buildBetterPickaxe) {
               this.fontRenderer.drawStringWithShadow("Unlocks villages", var41, var25 + var39 + 4 - (this.hasAchievementUnlocked(var30) ? 12 : 0), 12619872);
            }

            if (this.hasAchievementUnlocked(var30)) {
               this.fontRenderer.drawStringWithShadow(view_mode == 1 ? this.getTakenByText(var30) : I18n.getString("achievement.taken"), var41, var25 + var39 + 4, -7302913);
            }
         }

         this.fontRenderer.drawStringWithShadow(var33, var41, var25, !this.canUnlockAchievement(var30) && !this.hasAchievementUnlocked(var30) ? (var30.getSpecial() ? -8355776 : -8355712) : (var30.getSpecial() ? -128 : -1));
      }

      GL11.glEnable(2929);
      GL11.glEnable(2896);
      RenderHelper.disableStandardItemLighting();
   }

   private void drawLines(int var4, int var5, int var8, int var9, Achievement child, Achievement parent) {
      for(int pass = 0; pass < 2; ++pass) {
         Achievement var31 = child;
         if (parent != null) {
            int var22 = var31.displayColumn * 24 - var4 + 11 + var8;
            int var23 = var31.displayRow * 24 - var5 + 11 + var9;
            int var41 = parent.displayColumn * 24 - var4 + 11 + var8;
            int var25 = parent.displayRow * 24 - var5 + 11 + var9;
            if (var31.isFlipped()) {
               var22 = parent.displayColumn * 24 - var4 + 11 + var8;
               var23 = parent.displayRow * 24 - var5 + 11 + var9;
               var41 = var31.displayColumn * 24 - var4 + 11 + var8;
               var25 = var31.displayRow * 24 - var5 + 11 + var9;
            }

            boolean var26 = this.hasAchievementUnlocked(var31);
            boolean var27 = this.canUnlockAchievement(var31);
            if (!this.hasAchievementUnlocked(var31.getSecondParent()) || !this.haveAllParentAchievementsBeenUnlocked(var31.getSecondParent(), true)) {
               if (!this.hasAchievementUnlocked(parent)) {
                  var26 = false;
               }

               var27 = false;
            }

            int var29 = -16777216;
            if (var26) {
               if (pass == 0) {
                  continue;
               }

               var29 = -9408400;
            } else if (var27) {
               var29 = Math.sin((double)(Minecraft.getSystemTime() % 600L) / 600.0 * Math.PI * 2.0) > 0.6 ? -16728064 : -16744448;
            }

            if (pass != 1 || var26) {
               this.drawHorizontalLine(var22, var41, var23, var29);
               this.drawVerticalLine(var41, var23, var25, var29);
            }
         }
      }

   }

   public String getTakenByText(Achievement achievement) {
      WorldAchievement wa = this.mc.theWorld.worldInfo.getWorldAchievement(achievement);
      return I18n.getStringParams("gui.achievement.takenBy", wa.username);
   }

   public boolean doesGuiPauseGame() {
      return true;
   }

   static {
      guiMapTop = AchievementList.minDisplayColumn * 24 - 112;
      guiMapLeft = AchievementList.minDisplayRow * 24 - 112;
      guiMapBottom = AchievementList.maxDisplayColumn * 24 - 77;
      guiMapRight = AchievementList.maxDisplayRow * 24 - 77;
      achievementTextures = new ResourceLocation("textures/gui/achievement/achievement_background.png");
      view_mode = 0;
   }
}
