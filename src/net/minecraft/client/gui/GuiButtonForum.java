package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class GuiButtonForum extends GuiButton {
   public GuiButtonForum(int par1, int par2, int par3) {
      super(par1, par2, par3, 20, 20, "");
   }

   public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
      if (this.drawButton) {
         par1Minecraft.getTextureManager().bindTexture(GuiButton.buttonTextures);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         boolean var4 = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
         int var5 = 106;
         if (var4) {
            var5 += this.height;
         }

         this.drawTexturedModalRect(this.xPosition, this.yPosition, 0 + this.width, var5, this.width, this.height);
      }

   }
}
