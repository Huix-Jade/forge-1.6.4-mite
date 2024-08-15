package net.minecraft.client.audio;

import paulscode.sound.SoundSystem;

class SoundManagerINNER1 implements Runnable {
   // $FF: synthetic field
   final SoundManager theSoundManager;

   SoundManagerINNER1(SoundManager var1) {
      this.theSoundManager = var1;
   }

   public void run() {
      SoundManager.func_130080_a(this.theSoundManager, new SoundSystem());
      SoundManager.func_130082_a(this.theSoundManager, true);
   }
}
