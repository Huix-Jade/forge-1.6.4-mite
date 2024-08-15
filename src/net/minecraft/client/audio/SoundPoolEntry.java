package net.minecraft.client.audio;

import java.net.URL;

public class SoundPoolEntry {
   private final String soundName;
   private final URL soundUrl;

   public SoundPoolEntry(String var1, URL var2) {
      this.soundName = var1;
      this.soundUrl = var2;
   }

   public String getSoundName() {
      return this.soundName;
   }

   public URL getSoundUrl() {
      return this.soundUrl;
   }
}
