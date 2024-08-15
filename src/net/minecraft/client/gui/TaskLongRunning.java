package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public abstract class TaskLongRunning implements Runnable {
   protected GuiScreenLongRunningTask taskGUI;

   public void setGUI(GuiScreenLongRunningTask var1) {
      this.taskGUI = var1;
   }

   public void setFailedMessage(String var1) {
      this.taskGUI.setFailedMessage(var1);
   }

   public void setMessage(String var1) {
      this.taskGUI.setMessage(var1);
   }

   public Minecraft getMinecraft() {
      return this.taskGUI.func_96208_g();
   }

   public boolean wasScreenClosed() {
      return this.taskGUI.wasScreenClosed();
   }

   public void updateScreen() {
   }

   public void buttonClicked(GuiButton var1) {
   }

   public void initGUI() {
   }
}
