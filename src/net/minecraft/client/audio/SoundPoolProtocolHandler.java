package net.minecraft.client.audio;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

class SoundPoolProtocolHandler extends URLStreamHandler {
   // $FF: synthetic field
   final SoundPool theSoundPool;

   SoundPoolProtocolHandler(SoundPool var1) {
      this.theSoundPool = var1;
   }

   protected URLConnection openConnection(URL var1) {
      return new SoundPoolURLConnection(this.theSoundPool, var1, (SoundPoolProtocolHandler)null);
   }
}
