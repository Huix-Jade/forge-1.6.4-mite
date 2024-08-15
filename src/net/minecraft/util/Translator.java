package net.minecraft.util;

import java.util.List;

public final class Translator {
   public static String get(String key) {
      return StatCollector.translateToLocal(key);
   }

   public static String getFormatted(String key, Object... par1ArrayOfObj) {
      return StatCollector.translateToLocalFormatted(key, par1ArrayOfObj);
   }

   public static void addToList(EnumChatFormatting enum_chat_formatting, String key, List list) {
      String s = get(key);
      if (s.contains("|")) {
         String[] arr = StringHelper.explode(s, "\\|");

         for(int i = 0; i < arr.length; ++i) {
            list.add(enum_chat_formatting == null ? arr[i] : enum_chat_formatting + arr[i]);
         }
      } else {
         list.add(enum_chat_formatting == null ? s : enum_chat_formatting + s);
      }

   }

   public static void addToListFormatted(EnumChatFormatting enum_chat_formatting, String key, List list, Object... objects) {
      String s = getFormatted(key, objects);
      if (s.contains("|")) {
         String[] arr = StringHelper.explode(s, "\\|");

         for(int i = 0; i < arr.length; ++i) {
            list.add(enum_chat_formatting == null ? arr[i] : enum_chat_formatting + arr[i]);
         }
      } else {
         list.add(enum_chat_formatting == null ? s : enum_chat_formatting + s);
      }

   }
}
