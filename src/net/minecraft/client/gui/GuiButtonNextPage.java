package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

class GuiButtonNextPage extends GuiButton {
   private final boolean nextPage;

   public GuiButtonNextPage(int par1, int par2, int par3, boolean par4) {
      super(par1, par2, par3, 23, 13, "");
      this.nextPage = par4;
      this.setClickedSound((String)null, 0.5F, 1.0F);
   }

   public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
      if (this.drawButton) {
         boolean var4 = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         par1Minecraft.getTextureManager().bindTexture(GuiScreenBook.func_110404_g());
         int var5 = 0;
         int var6 = 192;
         if (var4) {
            var5 += 23;
         }

         if (!this.nextPage) {
            var6 += 13;
         }

         this.drawTexturedModalRect(this.xPosition, this.yPosition, var5, var6, 23, 13);
      }

   }
}
