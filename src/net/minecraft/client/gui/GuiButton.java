package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiButton extends Gui {
   protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
   protected int width;
   protected int height;
   public int xPosition;
   public int yPosition;
   public String displayString;
   public int id;
   public boolean enabled;
   public boolean drawButton;
   protected boolean field_82253_i;
   private String clicked_sound;
   private float clicked_sound_volume;
   private float clicked_sound_pitch;
   public boolean is_disconnect_button;

   public GuiButton(int par1, int par2, int par3, String par4Str) {
      this(par1, par2, par3, 200, 20, par4Str);
   }

   public GuiButton(int par1, int par2, int par3, int par4, int par5, String par6Str) {
      this.clicked_sound = "random.click";
      this.clicked_sound_volume = 1.0F;
      this.clicked_sound_pitch = 1.0F;
      this.width = 200;
      this.height = 20;
      this.enabled = true;
      this.drawButton = true;
      this.id = par1;
      this.xPosition = par2;
      this.yPosition = par3;
      this.width = par4;
      this.height = par5;
      this.displayString = par6Str;
   }

   protected int getHoverState(boolean par1) {
      byte var2 = 1;
      if (!this.enabled) {
         var2 = 0;
      } else if (par1) {
         var2 = 2;
      }

      return var2;
   }

   public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
      if (this.drawButton) {
         FontRenderer var4 = par1Minecraft.fontRenderer;
         par1Minecraft.getTextureManager().bindTexture(buttonTextures);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_82253_i = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
         int var5 = this.getHoverState(this.field_82253_i);
         this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + var5 * 20, this.width / 2, this.height);
         this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - (this.width - this.width / 2), 46 + var5 * 20, this.width - this.width / 2, this.height);
         this.mouseDragged(par1Minecraft, par2, par3);
         int var6 = 14737632;
         if (!this.enabled) {
            var6 = -6250336;
         } else if (this.field_82253_i) {
            var6 = 16777120;
         }

         this.drawCenteredString(var4, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, var6);
      }

   }

   protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
   }

   public void mouseReleased(int par1, int par2) {
   }

   public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
      return this.enabled && this.drawButton && par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
   }

   public boolean func_82252_a() {
      return this.field_82253_i;
   }

   public void func_82251_b(int par1, int par2) {
   }

   public boolean isClickable() {
      return this.drawButton && this.enabled;
   }

   public GuiButton setClickedSound(String clicked_sound, float volume, float pitch) {
      this.clicked_sound = clicked_sound;
      this.clicked_sound_volume = volume;
      this.clicked_sound_pitch = pitch;
      return this;
   }

   public void playClickedSound(SoundManager sound_manager) {
      if (this.clicked_sound != null) {
         sound_manager.playSoundFX(this.clicked_sound, this.clicked_sound_volume, this.clicked_sound_pitch);
      }

   }
}
