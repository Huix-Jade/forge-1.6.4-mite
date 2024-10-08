package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiScreenHorseInventory extends GuiContainer {
   private static final ResourceLocation horseGuiTextures = new ResourceLocation("textures/gui/container/horse.png");
   private IInventory field_110413_u;
   private IInventory field_110412_v;
   private EntityHorse field_110411_w;
   private float field_110416_x;
   private float field_110415_y;

   public GuiScreenHorseInventory(EntityPlayer player, IInventory par2IInventory, EntityHorse par3EntityHorse) {
      super(new ContainerHorseInventory(player, par2IInventory, par3EntityHorse));
      this.field_110413_u = player.inventory;
      this.field_110412_v = par2IInventory;
      this.field_110411_w = par3EntityHorse;
      this.allowUserInput = false;
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      this.fontRenderer.drawString(this.field_110412_v.hasCustomName() ? this.field_110412_v.getCustomNameOrUnlocalized() : I18n.getString(this.field_110412_v.getCustomNameOrUnlocalized()), 8, 6, 4210752);
      this.fontRenderer.drawString(this.field_110413_u.hasCustomName() ? this.field_110413_u.getCustomNameOrUnlocalized() : I18n.getString(this.field_110413_u.getCustomNameOrUnlocalized()), 8, this.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(horseGuiTextures);
      int var4 = (this.width - this.xSize) / 2;
      int var5 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
      if (this.field_110411_w.isChested()) {
         this.drawTexturedModalRect(var4 + 79, var5 + 17, 0, this.ySize, 90, 54);
      }

      if (this.field_110411_w.isNormalHorse()) {
         this.drawTexturedModalRect(var4 + 7, var5 + 35, 0, this.ySize + 54, 18, 18);
      }

      GuiInventory.func_110423_a(var4 + 51, var5 + 60, 17, (float)(var4 + 51) - this.field_110416_x, (float)(var5 + 75 - 50) - this.field_110415_y, this.field_110411_w);
   }

   public void drawScreen(int par1, int par2, float par3) {
      this.field_110416_x = (float)par1;
      this.field_110415_y = (float)par2;
      super.drawScreen(par1, par2, par3);
   }
}
