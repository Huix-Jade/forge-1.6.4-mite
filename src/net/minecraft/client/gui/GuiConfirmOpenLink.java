package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumSpecialSplash;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class GuiConfirmOpenLink extends GuiYesNoMITE {
   private String openLinkWarning;
   private String copyLinkButtonText;
   private String field_92028_p;
   private boolean field_92027_q;
   private EnumSpecialSplash enum_special_splash;

   public GuiConfirmOpenLink(GuiScreen par1GuiScreen, String par2Str, int par3, boolean par4) {
      this(par1GuiScreen, par2Str, par3, par4, (EnumSpecialSplash)null);
   }

   public GuiConfirmOpenLink(GuiScreen par1GuiScreen, String par2Str, int par3, boolean par4, EnumSpecialSplash enum_special_splash) {
      super(par1GuiScreen, enum_special_splash == null ? I18n.getString(par4 ? "chat.link.confirmTrusted" : "chat.link.confirm") : enum_special_splash.getMessageText(), par2Str, par3);
      this.field_92027_q = true;
      this.buttonText1 = I18n.getString(par4 ? "chat.link.open" : "gui.yes");
      this.buttonText2 = I18n.getString(par4 ? "gui.cancel" : "gui.no");
      this.copyLinkButtonText = I18n.getString("chat.copy");
      this.openLinkWarning = I18n.getString("chat.link.warning");
      this.field_92028_p = par2Str;
      this.enum_special_splash = enum_special_splash;
   }

   public GuiConfirmOpenLink(GuiScreen gui_screen, EnumSpecialSplash enum_special_splash) {
      this(gui_screen, enum_special_splash.getURL(), 15 + enum_special_splash.ordinal(), true, enum_special_splash);
   }

   public void initGui() {
      if (this.enum_special_splash != null) {
         this.buttonList.add(new GuiButton(0, this.width / 2 - 154 + 0, this.height / 2 + 66, 100, 20, this.buttonText1));
         this.buttonList.add(new GuiButton(2, this.width / 2 - 154 + 105, this.height / 2 + 66, 100, 20, this.copyLinkButtonText));
         this.buttonList.add(new GuiButton(1, this.width / 2 - 154 + 210, this.height / 2 + 66, 100, 20, this.buttonText2));
      } else {
         this.buttonList.add(new GuiButton(0, this.width / 2 - 154 + 0, this.height / 2 + 16, 100, 20, this.buttonText1));
         this.buttonList.add(new GuiButton(2, this.width / 2 - 154 + 105, this.height / 2 + 16, 100, 20, this.copyLinkButtonText));
         this.buttonList.add(new GuiButton(1, this.width / 2 - 154 + 210, this.height / 2 + 16, 100, 20, this.buttonText2));
      }

   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton != null) {
         if (par1GuiButton.id == 2) {
            this.copyLinkToClipboard();
         }

         this.parentScreen.confirmClicked(par1GuiButton.id == 0, this.worldNumber);
      }
   }

   protected void keyTyped(char par1, int par2) {
      if (par2 == 1) {
         this.mc.displayGuiScreen(this.parentScreen);
      } else {
         super.keyTyped(par1, par2);
      }
   }

   public void copyLinkToClipboard() {
      setClipboardString(this.field_92028_p);
   }

   public void drawScreen(int par1, int par2, float par3) {
      super.drawScreen(par1, par2, par3);
      if (this.enum_special_splash != null && this.enum_special_splash.hasLinkPageTexture()) {
         GL11.glEnable(3042);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(this.enum_special_splash.getLinkPageTexture());
         int image_width = MathHelper.floor_double((double)((float)this.enum_special_splash.getWidth() * this.enum_special_splash.getScale()));
         int image_height = MathHelper.floor_double((double)((float)this.enum_special_splash.getHeight() * this.enum_special_splash.getScale()));
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         this.drawTexturedModalRect2(this.width / 2 - image_width / 2, this.height / 2 - image_height / 2 + 10 - 10, image_width, image_height);
         GL11.glTexParameteri(3553, 10241, 9728);
         GL11.glTexParameteri(3553, 10240, 9728);
      }

   }

   public void func_92026_h() {
      this.field_92027_q = false;
   }

   protected int getMessage1YPos() {
      return this.enum_special_splash == null ? super.getMessage1YPos() : this.height / 2 - this.enum_special_splash.getMessageHeight();
   }

   protected boolean showMessage2() {
      return this.enum_special_splash == null && super.showMessage2();
   }
}
