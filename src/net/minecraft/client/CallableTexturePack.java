package net.minecraft.client;

import java.util.concurrent.Callable;

class CallableTexturePack implements Callable {
   // $FF: synthetic field
   final Minecraft theMinecraft;

   CallableTexturePack(Minecraft var1) {
      this.theMinecraft = var1;
   }

   public String callTexturePack() {
      return this.theMinecraft.gameSettings.skin;
   }

   // $FF: synthetic method
   public Object call() {
      return this.callTexturePack();
   }
}
