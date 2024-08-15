package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

class GuiButtonMerchant extends GuiButton {
   private final boolean mirrored;

   public GuiButtonMerchant(int var1, int var2, int var3, boolean var4) {
      super(var1, var2, var3, 12, 19, "");
      this.mirrored = var4;
   }

   public void drawButton(Minecraft var1, int var2, int var3) {
      if (this.drawButton) {
         var1.getTextureManager().bindTexture(GuiMerchant.func_110417_h());
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         boolean var4 = var2 >= this.xPosition && var3 >= this.yPosition && var2 < this.xPosition + this.width && var3 < this.yPosition + this.height;
         int var5 = 0;
         int var6 = 176;
         if (!this.enabled) {
            var6 += this.width * 2;
         } else if (var4) {
            var6 += this.width;
         }

         if (!this.mirrored) {
            var5 += this.height;
         }

         this.drawTexturedModalRect(this.xPosition, this.yPosition, var6, var5, this.width, this.height);
      }
   }
}
