package net.minecraft.util;

public enum EnumCommand {
   chunks("chunks", "Shows the number of chunks loaded"),
   commands("commands", "Lists all MITE commands"),
   day("day", "Shows the age of the world, in days"),
   ground("ground", "Rescues player from bouncing between floor and ceiling bug"),
   load("load", "Shows the current processing load on the server"),
   mem("mem", "Shows the amount of memory the server is currently using as well as the total amount that has been allocated to the JVM"),
   rendering("rendering", "Shows the current rendering scheme"),
   skills("skills", "Lists all available skills if professions are enabled"),
   stats("stats", "Writes character stats to plain text file"),
   syncpos("syncpos", "Syncs player's position on client exactly with position on server"),
   tournament("tournament", "Shows the tournament objective, if applicable"),
   version("version", "Shows the release number of MITE you are playing"),
   versions("versions", "Shows the range of releases the current world has been played with"),
   villages("villages", "Shows prerequisites for unlocking villages"),
   xp("xp", "Shows how many experience points you have");

   public String text;
   public String description;

   private EnumCommand(String text, String description) {
      this.text = text;
      this.description = description;
   }

   public String toString() {
      return this.text;
   }

   public boolean matches(String text) {
      return text != null && text.equalsIgnoreCase(this.text);
   }

   public static EnumCommand get(String text) {
      if (text == null) {
         return null;
      } else {
         if (text.startsWith("/")) {
            text = text.substring(1);
         }

         for(int i = 0; i < values().length; ++i) {
            if (values()[i].matches(text)) {
               return values()[i];
            }
         }

         return null;
      }
   }

   public static EnumCommand get(int ordinal) {
      return values()[ordinal];
   }
}
