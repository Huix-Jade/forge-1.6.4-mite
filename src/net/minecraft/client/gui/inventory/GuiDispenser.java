package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiDispenser extends GuiContainer {
   private static final ResourceLocation dispenserGuiTextures = new ResourceLocation("textures/gui/container/dispenser.png");
   public TileEntityDispenser theDispenser;

   public GuiDispenser(EntityPlayer player, TileEntityDispenser par2TileEntityDispenser) {
      super(new ContainerDispenser(player, par2TileEntityDispenser));
      this.theDispenser = par2TileEntityDispenser;
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      String var3 = this.theDispenser.hasCustomName() ? this.theDispenser.getCustomNameOrUnlocalized() : I18n.getString(this.theDispenser.getCustomNameOrUnlocalized());
      this.fontRenderer.drawString(var3, this.xSize / 2 - this.fontRenderer.getStringWidth(var3) / 2, 6, 2631720);
      this.fontRenderer.drawString(I18n.getString("container.inventory"), 7, this.ySize - 96 + 3, 2631720);
   }

   protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(dispenserGuiTextures);
      int var4 = (this.width - this.xSize) / 2;
      int var5 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
   }
}
