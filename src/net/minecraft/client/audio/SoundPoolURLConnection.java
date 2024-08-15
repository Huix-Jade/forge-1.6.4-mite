package net.minecraft.client.audio;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import net.minecraft.util.ResourceLocation;

class SoundPoolURLConnection extends URLConnection {
   private final ResourceLocation field_110659_b;
   // $FF: synthetic field
   final SoundPool theSoundPool;

   private SoundPoolURLConnection(SoundPool var1, URL var2) {
      super(var2);
      this.theSoundPool = var1;
      this.field_110659_b = new ResourceLocation(var2.getPath());
   }

   public void connect() {
   }

   public InputStream getInputStream() {
      try {
         return SoundPool.func_110655_a(this.theSoundPool).getResource(this.field_110659_b).getInputStream();
      }
      catch (Exception ex) {
         return null;
      }
   }

   // $FF: synthetic method
   SoundPoolURLConnection(SoundPool var1, URL var2, SoundPoolProtocolHandler var3) {
      this(var1, var2);
   }
}
