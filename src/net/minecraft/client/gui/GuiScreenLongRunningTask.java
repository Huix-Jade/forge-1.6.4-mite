package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.client.Minecraft;

public class GuiScreenLongRunningTask extends GuiScreen {
   private final int field_96213_b = 666;
   private final GuiScreen previousScreen;
   private final Thread taskThread;
   private volatile String message = "";
   private volatile boolean taskFailed;
   private volatile String failedMessage;
   private volatile boolean screenWasClosed;
   private int progressCounter;
   private TaskLongRunning task;
   public static final String[] PROGRESS_TEXT = new String[]{"â–ƒ â–„ â–… â–† â–‡ â–ˆ â–‡ â–† â–… â–„ â–ƒ", "_ â–ƒ â–„ â–… â–† â–‡ â–ˆ â–‡ â–† â–… â–„", "_ _ â–ƒ â–„ â–… â–† â–‡ â–ˆ â–‡ â–† â–…", "_ _ _ â–ƒ â–„ â–… â–† â–‡ â–ˆ â–‡ â–†", "_ _ _ _ â–ƒ â–„ â–… â–† â–‡ â–ˆ â–‡", "_ _ _ _ _ â–ƒ â–„ â–… â–† â–‡ â–ˆ", "_ _ _ _ â–ƒ â–„ â–… â–† â–‡ â–ˆ â–‡", "_ _ _ â–ƒ â–„ â–… â–† â–‡ â–ˆ â–‡ â–†", "_ _ â–ƒ â–„ â–… â–† â–‡ â–ˆ â–‡ â–† â–…", "_ â–ƒ â–„ â–… â–† â–‡ â–ˆ â–‡ â–† â–… â–„", "â–ƒ â–„ â–… â–† â–‡ â–ˆ â–‡ â–† â–… â–„ â–ƒ", "â–„ â–… â–† â–‡ â–ˆ â–‡ â–† â–… â–„ â–ƒ _", "â–… â–† â–‡ â–ˆ â–‡ â–† â–… â–„ â–ƒ _ _", "â–† â–‡ â–ˆ â–‡ â–† â–… â–„ â–ƒ _ _ _", "â–‡ â–ˆ â–‡ â–† â–… â–„ â–ƒ _ _ _ _", "â–ˆ â–‡ â–† â–… â–„ â–ƒ _ _ _ _ _", "â–‡ â–ˆ â–‡ â–† â–… â–„ â–ƒ _ _ _ _", "â–† â–‡ â–ˆ â–‡ â–† â–… â–„ â–ƒ _ _ _", "â–… â–† â–‡ â–ˆ â–‡ â–† â–… â–„ â–ƒ _ _", "â–„ â–… â–† â–‡ â–ˆ â–‡ â–† â–… â–„ â–ƒ _"};

   public GuiScreenLongRunningTask(Minecraft var1, GuiScreen var2, TaskLongRunning var3) {
      super.buttonList = Collections.synchronizedList(new ArrayList());
      this.mc = var1;
      this.previousScreen = var2;
      this.task = var3;
      var3.setGUI(this);
      this.taskThread = new Thread(var3);
   }

   public void func_98117_g() {
      this.taskThread.start();
   }

   public void updateScreen() {
      super.updateScreen();
      ++this.progressCounter;
      this.task.updateScreen();
   }

   protected void keyTyped(char var1, int var2) {
   }

   public void initGui() {
      this.task.initGUI();
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.id == 666) {
         this.screenWasClosed = true;
         this.mc.displayGuiScreen(this.previousScreen);
      }

      this.task.buttonClicked(var1);
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.message, this.width / 2, this.height / 2 - 50, 16777215);
      this.drawCenteredString(this.fontRenderer, "", this.width / 2, this.height / 2 - 10, 16777215);
      if (!this.taskFailed) {
         this.drawCenteredString(this.fontRenderer, PROGRESS_TEXT[this.progressCounter % PROGRESS_TEXT.length], this.width / 2, this.height / 2 + 15, 8421504);
      }

      if (this.taskFailed) {
         this.drawCenteredString(this.fontRenderer, this.failedMessage, this.width / 2, this.height / 2 + 15, 16711680);
      }

      super.drawScreen(var1, var2, var3);
   }

   public void setFailedMessage(String var1) {
      this.taskFailed = true;
      this.failedMessage = var1;
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(666, this.width / 2 - 100, this.height / 4 + 120 + 12, "Back"));
   }

   public Minecraft func_96208_g() {
      return this.mc;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   public boolean wasScreenClosed() {
      return this.screenWasClosed;
   }
}
