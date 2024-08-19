package net.minecraft.client.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiScreen extends Gui {
   protected Minecraft mc;
   public int width;
   public int height;
   protected List buttonList = new ArrayList();
   public boolean allowUserInput;
   protected FontRenderer fontRenderer;
   private GuiButton selectedButton;
   private int eventButton;
   private long lastMouseEvent;
   private int field_92018_d;

   public void drawScreen(int par1, int par2, float par3) {
       for (Object o : this.buttonList) {
           GuiButton var5 = (GuiButton) o;
           var5.drawButton(this.mc, par1, par2);
       }

   }

   protected void keyTyped(char par1, int par2) {
      if (par2 == 1) {
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
      }

   }

   public static String getClipboardString() {
      try {
         Transferable var0 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object)null);
         if (var0 != null && var0.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return (String)var0.getTransferData(DataFlavor.stringFlavor);
         }
      } catch (Exception var1) {
      }

      return "";
   }

   public static void setClipboardString(String par0Str) {
      try {
         StringSelection var1 = new StringSelection(par0Str);
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(var1, (ClipboardOwner)null);
      } catch (Exception var2) {
      }

   }

   protected void mouseClicked(int par1, int par2, int par3) {
      if (par3 == 0) {
         for(int var4 = 0; var4 < this.buttonList.size(); ++var4) {
            GuiButton var5 = (GuiButton)this.buttonList.get(var4);
            if (var5.mousePressed(this.mc, par1, par2)) {
               this.selectedButton = var5;
               var5.playClickedSound(this.mc.sndManager);
               this.actionPerformed(var5);
            }
         }
      }

   }

   protected void mouseMovedOrUp(int par1, int par2, int par3) {
      if (this.selectedButton != null && par3 == 0) {
         this.selectedButton.mouseReleased(par1, par2);
         this.selectedButton = null;
      }

   }

   protected void mouseClickMove(int par1, int par2, int par3, long par4) {
   }

   protected void actionPerformed(GuiButton par1GuiButton) {
   }

   public void setWorldAndResolution(Minecraft par1Minecraft, int par2, int par3) {
      this.mc = par1Minecraft;
      this.fontRenderer = par1Minecraft.fontRenderer;
      this.width = par2;
      this.height = par3;
      this.buttonList.clear();
      this.initGui();
   }

   public void initGui() {
   }

   public boolean handlesMouseInput() {
      return true;
   }

   public void handleInput() {
      while(this.mc.isGuiOpen(true) && Mouse.next()) {
         this.handleMouseInput();
      }

      while(Keyboard.next()) {
         this.handleKeyboardInput();
      }

   }

   public void handleMouseInput() {
      int var1 = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int var2 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      int var3 = Mouse.getEventButton();
      if (Minecraft.isRunningOnMac && var3 == 0 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157))) {
         var3 = 1;
      }

      if (Mouse.getEventButtonState()) {
         if (this.mc.gameSettings.touchscreen && this.field_92018_d++ > 0) {
            return;
         }

         this.eventButton = var3;
         this.lastMouseEvent = Minecraft.getSystemTime();
         this.mouseClicked(var1, var2, this.eventButton);
      } else if (var3 != -1) {
         if (this.mc.gameSettings.touchscreen && --this.field_92018_d > 0) {
            return;
         }

         this.eventButton = -1;
         this.mouseMovedOrUp(var1, var2, var3);
      } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
         long var4 = Minecraft.getSystemTime() - this.lastMouseEvent;
         this.mouseClickMove(var1, var2, this.eventButton, var4);
      }

   }

   public void handleKeyboardInput() {
      int key = Keyboard.getEventKey();
      char character = Keyboard.getEventCharacter();
      if (Keyboard.getEventKeyState() || (key == 0 && Character.isDefined(character))) {
         if (key == 87) {
            if (!Main.is_MITE_DS) {
               this.mc.toggleFullscreen();
            }

            return;
         }

         this.keyTyped(character, key);
      }

   }

   public void updateScreen() {
   }

   public void onGuiClosed() {
   }

   public void drawDefaultBackground() {
      this.drawWorldBackground(0);
   }

   public void drawWorldBackground(int par1) {
      if (this.mc.theWorld != null) {
         this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
      } else {
         this.drawBackground(par1);
      }

   }

   public void drawBackground(int par1) {
      GL11.glDisable(2896);
      GL11.glDisable(2912);
      Tessellator var2 = Tessellator.instance;
      this.mc.getTextureManager().bindTexture(optionsBackground);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var3 = 32.0F;
      var2.startDrawingQuads();
      var2.setColorOpaque_I(4210752);
      var2.addVertexWithUV(0.0, (double)this.height, 0.0, 0.0, (double)((float)this.height / var3 + (float)par1));
      var2.addVertexWithUV((double)this.width, (double)this.height, 0.0, (double)((float)this.width / var3), (double)((float)this.height / var3 + (float)par1));
      var2.addVertexWithUV((double)this.width, 0.0, 0.0, (double)((float)this.width / var3), (double)par1);
      var2.addVertexWithUV(0.0, 0.0, 0.0, 0.0, (double)par1);
      var2.draw();
   }

   public boolean doesGuiPauseGame() {
      return true;
   }

   public void confirmClicked(boolean par1, int par2) {
   }

   public static boolean isCtrlKeyDown() {
      return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
   }

   public static boolean isShiftKeyDown() {
      return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
   }

   public boolean allowsImposedChat() {
      return !this.doesGuiPauseGame();
   }
}
