package net.minecraft.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntityCommandBlock;
import org.lwjgl.input.Keyboard;

public class GuiCommandBlock extends GuiScreen {
   private GuiTextField commandTextField;
   private final TileEntityCommandBlock commandBlock;
   private GuiButton doneBtn;
   private GuiButton cancelBtn;

   public GuiCommandBlock(TileEntityCommandBlock var1) {
      this.commandBlock = var1;
   }

   public void updateScreen() {
      this.commandTextField.updateCursorCounter();
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.getString("gui.done")));
      this.buttonList.add(this.cancelBtn = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.getString("gui.cancel")));
      this.commandTextField = new GuiTextField(this.fontRenderer, this.width / 2 - 150, 60, 300, 20);
      this.commandTextField.setMaxStringLength(32767);
      this.commandTextField.setFocused(true);
      this.commandTextField.setText(this.commandBlock.getCommand());
      this.doneBtn.enabled = this.commandTextField.getText().trim().length() > 0;
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id == 1) {
            this.mc.displayGuiScreen((GuiScreen)null);
         } else if (var1.id == 0) {
            String var2 = "MC|AdvCdm";
            ByteArrayOutputStream var3 = new ByteArrayOutputStream();
            DataOutputStream var4 = new DataOutputStream(var3);

            try {
               var4.writeInt(this.commandBlock.xCoord);
               var4.writeInt(this.commandBlock.yCoord);
               var4.writeInt(this.commandBlock.zCoord);
               Packet.writeString(this.commandTextField.getText(), var4);
               this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload(var2, var3.toByteArray()));
            } catch (Exception var6) {
               var6.printStackTrace();
            }

            this.mc.displayGuiScreen((GuiScreen)null);
         }

      }
   }

   protected void keyTyped(char var1, int var2) {
      this.commandTextField.textboxKeyTyped(var1, var2);
      this.doneBtn.enabled = this.commandTextField.getText().trim().length() > 0;
      if (var2 != 28 && var2 != 156) {
         if (var2 == 1) {
            this.actionPerformed(this.cancelBtn);
         }
      } else {
         this.actionPerformed(this.doneBtn);
      }

   }

   protected void mouseClicked(int var1, int var2, int var3) {
      super.mouseClicked(var1, var2, var3);
      this.commandTextField.mouseClicked(var1, var2, var3);
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.getString("advMode.setCommand"), this.width / 2, 20, 16777215);
      this.drawString(this.fontRenderer, I18n.getString("advMode.command"), this.width / 2 - 150, 47, 10526880);
      this.drawString(this.fontRenderer, I18n.getString("advMode.nearestPlayer"), this.width / 2 - 150, 97, 10526880);
      this.drawString(this.fontRenderer, I18n.getString("advMode.randomPlayer"), this.width / 2 - 150, 108, 10526880);
      this.drawString(this.fontRenderer, I18n.getString("advMode.allPlayers"), this.width / 2 - 150, 119, 10526880);
      this.commandTextField.drawTextBox();
      super.drawScreen(var1, var2, var3);
   }
}
