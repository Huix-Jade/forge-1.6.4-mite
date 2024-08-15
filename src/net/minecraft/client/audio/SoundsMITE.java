package net.minecraft.client.audio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePack;
import net.minecraft.util.ResourceLocation;

public class SoundsMITE {
   private SoundManager sndManager;
   private List sounds = new ArrayList();
   public boolean loaded = false;

   public SoundsMITE(SoundManager sndManager) {
      this.sndManager = sndManager;
      this.add("sound/imported/liquid/block_splash.ogg");
      this.add("sound/imported/random/sizzle.ogg");
      this.add("sound/imported/random/boil.ogg");
      this.add("sound/imported/random/level_drain.ogg");
      this.add("sound/imported/random/camera.ogg");
      this.add("sound/imported/random/chest_locked.ogg");
      this.add("sound/imported/random/book_open.ogg");
      this.add("sound/imported/random/book_page.ogg");
      this.add("sound/imported/random/book_close.ogg");
      this.add("sound/imported/random/cow_alarm1.ogg");
      this.add("sound/imported/random/cow_alarm2.ogg");
      this.add("sound/imported/random/gunshot.ogg");
      this.add("sound/imported/mob/wolf/howl1.ogg");
      this.add("sound/imported/mob/wolf/howl2.ogg");
      this.add("sound/imported/mob/wolf/howl3.ogg");
      this.add("sound/imported/mob/demonspider/death.ogg");
      this.add("sound/imported/mob/demonspider/hurt1.ogg");
      this.add("sound/imported/mob/demonspider/hurt2.ogg");
      this.add("sound/imported/mob/demonspider/say1.ogg");
      this.add("sound/imported/mob/demonspider/say2.ogg");
      this.add("sound/imported/mob/demonspider/say3.ogg");
      this.add("sound/imported/mob/ghoul/death.ogg");
      this.add("sound/imported/mob/ghoul/hurt1.ogg");
      this.add("sound/imported/mob/ghoul/hurt2.ogg");
      this.add("sound/imported/mob/ghoul/say1.ogg");
      this.add("sound/imported/mob/ghoul/say2.ogg");
      this.add("sound/imported/mob/hellhound/death.ogg");
      this.add("sound/imported/mob/hellhound/hurt1.ogg");
      this.add("sound/imported/mob/hellhound/hurt2.ogg");
      this.add("sound/imported/mob/hellhound/say1.ogg");
      this.add("sound/imported/mob/hellhound/say2.ogg");
      this.add("sound/imported/mob/hellhound/say3.ogg");
      this.add("sound/imported/mob/hellhound/breath.ogg");
      this.add("sound/imported/mob/invisiblestalker/death.ogg");
      this.add("sound/imported/mob/invisiblestalker/hurt1.ogg");
      this.add("sound/imported/mob/invisiblestalker/hurt2.ogg");
      this.add("sound/imported/mob/invisiblestalker/say1.ogg");
      this.add("sound/imported/mob/invisiblestalker/say2.ogg");
      this.add("sound/imported/mob/invisiblestalker/say3.ogg");
      this.add("sound/imported/mob/wight/death.ogg");
      this.add("sound/imported/mob/wight/hurt1.ogg");
      this.add("sound/imported/mob/wight/hurt2.ogg");
      this.add("sound/imported/mob/wight/say1.ogg");
      this.add("sound/imported/mob/wight/say2.ogg");
      this.add("sound/imported/mob/shadow/death1.ogg");
      this.add("sound/imported/mob/shadow/death2.ogg");
      this.add("sound/imported/mob/shadow/hurt1.ogg");
      this.add("sound/imported/mob/shadow/hurt2.ogg");
      this.add("sound/imported/mob/shadow/say1.ogg");
      this.add("sound/imported/mob/shadow/say2.ogg");
      this.add("sound/imported/mob/shadow/say3.ogg");
      this.add("sound/imported/mob/witch/death.ogg");
      this.add("sound/imported/mob/witch/hurt.ogg");
      this.add("sound/imported/mob/witch/cackle1.ogg");
      this.add("sound/imported/mob/witch/cackle2.ogg");
      this.add("sound/imported/mob/witch/cackle3.ogg");
      this.add("sound/imported/portal/runegate.ogg");
      this.add("records/imported/underworld.ogg");
      this.add("records/imported/descent.ogg");
      this.add("records/imported/wanderer.ogg");
      this.add("records/imported/legends.ogg");
   }

   private boolean add(String path) {
      return this.sounds.add(path);
   }

   public boolean load() {
      if (this.loaded) {
         System.err.println();
         System.err.println("SoundsMITE: sounds have already been loaded!");
         return true;
      } else {
         System.out.println();
         System.out.print("SoundsMITE: Loading sounds...");
         boolean errors = false;
         ResourcePack MITE_resource_pack = Minecraft.MITE_resource_pack;
         if (MITE_resource_pack == null) {
            Minecraft.setErrorMessage("\nSoundsMITE: MITE Resource Pack 1.6.4 needs to be loaded!");
            errors = true;
         } else {
            Iterator i = this.sounds.iterator();

            while(i.hasNext()) {
               String sound = (String)i.next();
               if (MITE_resource_pack.resourceExists(new ResourceLocation(sound))) {
                  this.loadMITESound(sound);
               } else {
                  if (!errors) {
                     System.err.println();
                  }

                  Minecraft.setErrorMessage("SoundsMITE: sound " + sound + " not found in MITE Resource Pack " + "1.6.4" + "!");
                  errors = true;
               }
            }
         }

         if (errors) {
            System.out.println("SoundsMITE: Finished loading sounds with errors.");
         } else {
            System.out.println(" [ok]");
         }

         return this.loaded;
      }
   }

   private void loadMITESound(String path) {
      int var3 = path.indexOf("/");
      if (var3 != -1) {
         String var4 = path.substring(0, var3);
         path = path.substring(var3 + 1);
         if ("sound".equalsIgnoreCase(var4)) {
            this.sndManager.addSound(path);
         } else if ("records".equalsIgnoreCase(var4)) {
            this.sndManager.addStreaming(path);
         } else if ("music".equalsIgnoreCase(var4)) {
            this.sndManager.addMusic(path);
         }
      }

      this.loaded = true;
   }
}
