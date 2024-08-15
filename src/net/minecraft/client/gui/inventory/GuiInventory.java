package net.minecraft.client.gui.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.stats.AchievementList;
import org.lwjgl.opengl.GL11;

public class GuiInventory extends InventoryEffectRenderer {
   private float xSize_lo;
   private float ySize_lo;

   public GuiInventory(EntityPlayer par1EntityPlayer) {
      super(par1EntityPlayer.inventoryContainer);
      this.allowUserInput = true;
      par1EntityPlayer.addStat(AchievementList.openInventory, 1);
      par1EntityPlayer.incrementStatForThisWorldFromClient(AchievementList.openInventory);
   }

   public void updateScreen() {
      if (this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
      }

   }

   public void initGui() {
      this.buttonList.clear();
      if (this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
      } else {
         super.initGui();
      }

   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      this.fontRenderer.drawString(I18n.getString("container.crafting"), 87, 15, 4210752);
   }

   public void drawScreen(int par1, int par2, float par3) {
      this.xSize_lo = (float)par1;
      this.ySize_lo = (float)par2;
      super.drawScreen(par1, par2, par3);
      if (GuiScreen.isShiftKeyDown()) {
         this.drawProfessionsTooltip(par1, par2);
      }

   }

   protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(field_110408_a);
      int var4 = this.guiLeft;
      int var5 = this.guiTop;
      this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
      if (this.mc.thePlayer.crafting_ticks > 0) {
         this.drawTexturedModalRect(var4 + 125, var5 + 36, 176, 0, this.mc.thePlayer.crafting_ticks * 17 / this.mc.thePlayer.crafting_period, 14);
      }

      func_110423_a(var4 + 51, var5 + 75, 30, (float)(var4 + 51) - this.xSize_lo, (float)(var5 + 75 - 50) - this.ySize_lo, this.mc.thePlayer);
      SlotCrafting slot_crafting = (SlotCrafting)this.inventorySlots.getSlot(0);
      if (slot_crafting.getNumCraftingResults(this.mc.thePlayer) > 1) {
         this.mc.getTextureManager().bindTexture(GuiIngame.MITE_icons);
         float grey = 0.54509807F;
         GL11.glColor4f(grey, grey, grey, 1.0F);
         this.drawTexturedModalRect(var4 + 163, var5 + 36, 16, 0, 3, 3);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

   }

   public static void func_110423_a(int par0, int par1, int par2, float par3, float par4, EntityLivingBase par5EntityLivingBase) {
      GL11.glEnable(2903);
      GL11.glPushMatrix();
      GL11.glTranslatef((float)par0, (float)par1, 50.0F);
      GL11.glScalef((float)(-par2), (float)par2, (float)par2);
      GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
      float var6 = par5EntityLivingBase.renderYawOffset;
      float var7 = par5EntityLivingBase.rotationYaw;
      float var8 = par5EntityLivingBase.rotationPitch;
      float var9 = par5EntityLivingBase.prevRotationYawHead;
      float var10 = par5EntityLivingBase.rotationYawHead;
      GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-((float)Math.atan((double)(par4 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
      par5EntityLivingBase.renderYawOffset = (float)Math.atan((double)(par3 / 40.0F)) * 20.0F;
      par5EntityLivingBase.rotationYaw = (float)Math.atan((double)(par3 / 40.0F)) * 40.0F;
      par5EntityLivingBase.rotationPitch = -((float)Math.atan((double)(par4 / 40.0F))) * 20.0F;
      par5EntityLivingBase.rotationYawHead = par5EntityLivingBase.rotationYaw;
      par5EntityLivingBase.prevRotationYawHead = par5EntityLivingBase.rotationYaw;
      GL11.glTranslatef(0.0F, par5EntityLivingBase.yOffset, 0.0F);
      RenderManager.instance.playerViewY = 180.0F;
      par5EntityLivingBase.disable_shadow = true;
      RenderManager.instance.renderEntityWithPosYaw(par5EntityLivingBase, 0.0, 0.0, 0.0, 0.0F, 1.0F);
      par5EntityLivingBase.disable_shadow = false;
      par5EntityLivingBase.renderYawOffset = var6;
      par5EntityLivingBase.rotationYaw = var7;
      par5EntityLivingBase.rotationPitch = var8;
      par5EntityLivingBase.prevRotationYawHead = var9;
      par5EntityLivingBase.rotationYawHead = var10;
      GL11.glPopMatrix();
      RenderHelper.disableStandardItemLighting();
      GL11.glDisable(32826);
      OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GL11.glDisable(3553);
      OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.id == 0) {
         this.mc.displayGuiScreen(new GuiAchievements(this.mc.statFileWriter));
      }

      if (par1GuiButton.id == 1) {
         this.mc.displayGuiScreen(new GuiStats(this, this.mc.statFileWriter));
      }

   }

   private void drawProfessionsTooltip(int mouse_x, int mouse_y) {
      if (this.mc.theWorld.areSkillsEnabled()) {
         int cursed_box_left = this.guiLeft + 26;
         int cursed_box_top = this.guiTop + 8;
         int cursed_box_right = cursed_box_left + 51;
         int cursed_box_bottom = cursed_box_top + 69;
         if (mouse_x >= cursed_box_left && mouse_x <= cursed_box_right && mouse_y >= cursed_box_top && mouse_y <= cursed_box_bottom) {
            String professions = this.mc.thePlayer.getSkillsString(true);
            if (professions == null) {
               return;
            }

            List list = new ArrayList();
            list.add(professions);
            this.func_102021_a(list, mouse_x, mouse_y);
         }

      }
   }
}
