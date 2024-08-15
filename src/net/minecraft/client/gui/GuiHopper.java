package net.minecraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiHopper extends GuiContainer {
   private static final ResourceLocation hopperGuiTextures = new ResourceLocation("textures/gui/container/hopper.png");
   private IInventory field_94081_r;
   private IInventory field_94080_s;

   public GuiHopper(EntityPlayer player, IInventory par2IInventory) {
      super(new ContainerHopper(player, par2IInventory));
      this.field_94081_r = player.inventory;
      this.field_94080_s = par2IInventory;
      this.allowUserInput = false;
      this.ySize = 133;
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      String text = this.field_94080_s.hasCustomName() ? this.field_94080_s.getCustomNameOrUnlocalized() : I18n.getString(this.field_94080_s.getCustomNameOrUnlocalized());
      this.fontRenderer.drawString(text, this.xSize / 2 - this.fontRenderer.getStringWidth(text) / 2, 9, 2631720);
      this.fontRenderer.drawString(this.field_94081_r.hasCustomName() ? this.field_94081_r.getCustomNameOrUnlocalized() : I18n.getString(this.field_94081_r.getCustomNameOrUnlocalized()), 7, this.ySize - 96 + 3, 2631720);
   }

   protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(hopperGuiTextures);
      int var4 = (this.width - this.xSize) / 2;
      int var5 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
   }
}
