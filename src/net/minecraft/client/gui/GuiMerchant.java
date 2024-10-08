package net.minecraft.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import org.lwjgl.opengl.GL11;

public class GuiMerchant extends GuiContainer {
   private static final ResourceLocation merchantGuiTextures = new ResourceLocation("textures/gui/container/villager.png");
   private IMerchant theIMerchant;
   private GuiButtonMerchant nextRecipeButtonIndex;
   private GuiButtonMerchant previousRecipeButtonIndex;
   private int currentRecipeIndex;
   private String field_94082_v;

   public GuiMerchant(EntityPlayer player, IMerchant par2IMerchant, String par4Str) {
      super(new ContainerMerchant(player, par2IMerchant));
      this.theIMerchant = par2IMerchant;
      this.field_94082_v = par4Str != null && par4Str.length() >= 1 ? par4Str : I18n.getString("entity.Villager.name");
   }

   public void initGui() {
      super.initGui();
      int var1 = (this.width - this.xSize) / 2;
      int var2 = (this.height - this.ySize) / 2;
      this.buttonList.add(this.nextRecipeButtonIndex = new GuiButtonMerchant(1, var1 + 120 + 27, var2 + 24 - 1, true));
      this.buttonList.add(this.previousRecipeButtonIndex = new GuiButtonMerchant(2, var1 + 36 - 19, var2 + 24 - 1, false));
      this.nextRecipeButtonIndex.enabled = false;
      this.previousRecipeButtonIndex.enabled = false;
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      this.fontRenderer.drawString(this.field_94082_v, this.xSize / 2 - this.fontRenderer.getStringWidth(this.field_94082_v) / 2, 6, 4210752);
      this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
   }

   public void updateScreen() {
      super.updateScreen();
      MerchantRecipeList var1 = this.theIMerchant.getRecipes(this.mc.thePlayer);
      if (var1 != null) {
         this.nextRecipeButtonIndex.enabled = this.currentRecipeIndex < var1.size() - 1;
         this.previousRecipeButtonIndex.enabled = this.currentRecipeIndex > 0;
      }

   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      boolean var2 = false;
      if (par1GuiButton == this.nextRecipeButtonIndex) {
         ++this.currentRecipeIndex;
         var2 = true;
      } else if (par1GuiButton == this.previousRecipeButtonIndex) {
         --this.currentRecipeIndex;
         var2 = true;
      }

      if (var2) {
         ((ContainerMerchant)this.inventorySlots).setCurrentRecipeIndex(this.currentRecipeIndex);
         ByteArrayOutputStream var3 = new ByteArrayOutputStream();
         DataOutputStream var4 = new DataOutputStream(var3);

         try {
            var4.writeInt(this.currentRecipeIndex);
            this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("MC|TrSel", var3.toByteArray()));
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

   }

   protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(merchantGuiTextures);
      int var4 = (this.width - this.xSize) / 2;
      int var5 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
      MerchantRecipeList var6 = this.theIMerchant.getRecipes(this.mc.thePlayer);
      if (var6 != null && !var6.isEmpty()) {
         int var7 = this.currentRecipeIndex;
         MerchantRecipe var8 = (MerchantRecipe)var6.get(var7);
         if (var8.func_82784_g()) {
            this.mc.getTextureManager().bindTexture(merchantGuiTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(2896);
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
         }
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      super.drawScreen(par1, par2, par3);
      MerchantRecipeList var4 = this.theIMerchant.getRecipes(this.mc.thePlayer);
      if (var4 != null && !var4.isEmpty()) {
         int var5 = (this.width - this.xSize) / 2;
         int var6 = (this.height - this.ySize) / 2;
         int var7 = this.currentRecipeIndex;
         MerchantRecipe var8 = (MerchantRecipe)var4.get(var7);
         GL11.glPushMatrix();
         ItemStack var9 = var8.getItemToBuy();
         ItemStack var10 = var8.getSecondItemToBuy();
         ItemStack var11 = var8.getItemToSell();
         RenderHelper.enableGUIStandardItemLighting();
         GL11.glDisable(2896);
         GL11.glEnable(32826);
         GL11.glEnable(2903);
         GL11.glEnable(2896);
         itemRenderer.zLevel = 100.0F;
         itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.getTextureManager(), var9, var5 + 36, var6 + 24);
         itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.getTextureManager(), var9, var5 + 36, var6 + 24);
         if (var10 != null) {
            itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.getTextureManager(), var10, var5 + 62, var6 + 24);
            itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.getTextureManager(), var10, var5 + 62, var6 + 24);
         }

         itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.getTextureManager(), var11, var5 + 120, var6 + 24);
         itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.getTextureManager(), var11, var5 + 120, var6 + 24);
         itemRenderer.zLevel = 0.0F;
         GL11.glDisable(2896);
         if (this.isPointInRegion(36, 24, 16, 16, par1, par2)) {
            this.drawItemStackTooltip(var9, par1, par2);
         } else if (var10 != null && this.isPointInRegion(62, 24, 16, 16, par1, par2)) {
            this.drawItemStackTooltip(var10, par1, par2);
         } else if (this.isPointInRegion(120, 24, 16, 16, par1, par2)) {
            this.drawItemStackTooltip(var11, par1, par2);
         }

         GL11.glPopMatrix();
         GL11.glEnable(2896);
         GL11.glEnable(2929);
         RenderHelper.enableStandardItemLighting();
      }

   }

   public IMerchant getIMerchant() {
      return this.theIMerchant;
   }

   static ResourceLocation func_110417_h() {
      return merchantGuiTextures;
   }
}
