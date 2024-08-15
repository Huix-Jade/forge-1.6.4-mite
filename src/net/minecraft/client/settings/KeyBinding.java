package net.minecraft.client.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.IntHashMap;

public class KeyBinding {
   public static List keybindArray = new ArrayList();
   public static IntHashMap hash = new IntHashMap();
   public String keyDescription;
   public int keyCode;
   public boolean pressed;
   public int pressTime;

   public static void onTick(int var0) {
      KeyBinding var1 = (KeyBinding)hash.lookup(var0);
      if (var1 != null) {
         ++var1.pressTime;
      }

   }

   public static void setKeyBindState(int var0, boolean var1) {
      KeyBinding var2 = (KeyBinding)hash.lookup(var0);
      if (var2 != null) {
         var2.pressed = var1;
      }

   }

   public static void unPressAllKeys() {
      Iterator var0 = keybindArray.iterator();

      while(var0.hasNext()) {
         KeyBinding var1 = (KeyBinding)var0.next();
         var1.unpressKey();
      }

   }

   public static void resetKeyBindingArrayAndHash() {
      hash.clearMap();
      Iterator var0 = keybindArray.iterator();

      while(var0.hasNext()) {
         KeyBinding var1 = (KeyBinding)var0.next();
         hash.addKey(var1.keyCode, var1);
      }

   }

   public KeyBinding(String var1, int var2) {
      this.keyDescription = var1;
      this.keyCode = var2;
      keybindArray.add(this);
      hash.addKey(var2, this);
   }

   public boolean isPressed() {
      if (this.pressTime == 0) {
         return false;
      } else {
         --this.pressTime;
         return true;
      }
   }

   private void unpressKey() {
      this.pressTime = 0;
      this.pressed = false;
   }
}
