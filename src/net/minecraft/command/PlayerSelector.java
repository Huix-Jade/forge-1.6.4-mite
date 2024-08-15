package net.minecraft.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.World;

public class PlayerSelector {
   private static final Pattern tokenPattern = Pattern.compile("^@([parf])(?:\\[([\\w=,!-]*)\\])?$");
   private static final Pattern intListPattern = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
   private static final Pattern keyValueListPattern = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");

   public static EntityPlayerMP matchOnePlayer(ICommandSender var0, String var1) {
      EntityPlayerMP[] var2 = matchPlayers(var0, var1);
      return var2 != null && var2.length == 1 ? var2[0] : null;
   }

   public static String matchPlayersAsString(ICommandSender var0, String var1) {
      EntityPlayerMP[] var2 = matchPlayers(var0, var1);
      if (var2 != null && var2.length != 0) {
         String[] var3 = new String[var2.length];

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] = var2[var4].getTranslatedEntityName();
         }

         return CommandBase.joinNiceString(var3);
      } else {
         return null;
      }
   }

   public static EntityPlayerMP[] matchPlayers(ICommandSender var0, String var1) {
      Matcher var2 = tokenPattern.matcher(var1);
      if (!var2.matches()) {
         return null;
      } else {
         Map var3 = getArgumentMap(var2.group(2));
         String var4 = var2.group(1);
         int var5 = getDefaultMinimumRange(var4);
         int var6 = getDefaultMaximumRange(var4);
         int var7 = getDefaultMinimumLevel(var4);
         int var8 = getDefaultMaximumLevel(var4);
         int var9 = getDefaultCount(var4);
         int var10 = EnumGameType.NOT_SET.getID();
         ChunkCoordinates var11 = var0.getPlayerCoordinates();
         Map var12 = func_96560_a(var3);
         String var13 = null;
         String var14 = null;
         boolean var15 = false;
         if (var3.containsKey("rm")) {
            var5 = MathHelper.parseIntWithDefault((String)var3.get("rm"), var5);
            var15 = true;
         }

         if (var3.containsKey("r")) {
            var6 = MathHelper.parseIntWithDefault((String)var3.get("r"), var6);
            var15 = true;
         }

         if (var3.containsKey("lm")) {
            var7 = MathHelper.parseIntWithDefault((String)var3.get("lm"), var7);
         }

         if (var3.containsKey("l")) {
            var8 = MathHelper.parseIntWithDefault((String)var3.get("l"), var8);
         }

         if (var3.containsKey("x")) {
            var11.posX = MathHelper.parseIntWithDefault((String)var3.get("x"), var11.posX);
            var15 = true;
         }

         if (var3.containsKey("y")) {
            var11.posY = MathHelper.parseIntWithDefault((String)var3.get("y"), var11.posY);
            var15 = true;
         }

         if (var3.containsKey("z")) {
            var11.posZ = MathHelper.parseIntWithDefault((String)var3.get("z"), var11.posZ);
            var15 = true;
         }

         if (var3.containsKey("m")) {
            var10 = MathHelper.parseIntWithDefault((String)var3.get("m"), var10);
         }

         if (var3.containsKey("c")) {
            var9 = MathHelper.parseIntWithDefault((String)var3.get("c"), var9);
         }

         if (var3.containsKey("team")) {
            var14 = (String)var3.get("team");
         }

         if (var3.containsKey("name")) {
            var13 = (String)var3.get("name");
         }

         World var16 = var15 ? var0.getEntityWorld() : null;
         List var17;
         if (!var4.equals("p") && !var4.equals("a")) {
            if (!var4.equals("r")) {
               return null;
            } else {
               var17 = MinecraftServer.getServer().getConfigurationManager().findPlayers(var11, var5, var6, 0, var10, var7, var8, var12, var13, var14, var16);
               Collections.shuffle(var17);
               var17 = var17.subList(0, Math.min(var9, var17.size()));
               return var17 != null && !var17.isEmpty() ? (EntityPlayerMP[])var17.toArray(new EntityPlayerMP[0]) : new EntityPlayerMP[0];
            }
         } else {
            var17 = MinecraftServer.getServer().getConfigurationManager().findPlayers(var11, var5, var6, var9, var10, var7, var8, var12, var13, var14, var16);
            return var17 != null && !var17.isEmpty() ? (EntityPlayerMP[])var17.toArray(new EntityPlayerMP[0]) : new EntityPlayerMP[0];
         }
      }
   }

   public static Map func_96560_a(Map var0) {
      HashMap var1 = new HashMap();
      Iterator var2 = var0.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (var3.startsWith("score_") && var3.length() > "score_".length()) {
            String var4 = var3.substring("score_".length());
            var1.put(var4, MathHelper.parseIntWithDefault((String)var0.get(var3), 1));
         }
      }

      return var1;
   }

   public static boolean matchesMultiplePlayers(String var0) {
      Matcher var1 = tokenPattern.matcher(var0);
      if (var1.matches()) {
         Map var2 = getArgumentMap(var1.group(2));
         String var3 = var1.group(1);
         int var4 = getDefaultCount(var3);
         if (var2.containsKey("c")) {
            var4 = MathHelper.parseIntWithDefault((String)var2.get("c"), var4);
         }

         return var4 != 1;
      } else {
         return false;
      }
   }

   public static boolean hasTheseArguments(String var0, String var1) {
      Matcher var2 = tokenPattern.matcher(var0);
      if (var2.matches()) {
         String var3 = var2.group(1);
         return var1 == null || var1.equals(var3);
      } else {
         return false;
      }
   }

   public static boolean hasArguments(String var0) {
      return hasTheseArguments(var0, (String)null);
   }

   private static final int getDefaultMinimumRange(String var0) {
      return 0;
   }

   private static final int getDefaultMaximumRange(String var0) {
      return 0;
   }

   private static final int getDefaultMaximumLevel(String var0) {
      return Integer.MAX_VALUE;
   }

   private static final int getDefaultMinimumLevel(String var0) {
      return 0;
   }

   private static final int getDefaultCount(String var0) {
      return var0.equals("a") ? 0 : 1;
   }

   private static Map getArgumentMap(String var0) {
      HashMap var1 = new HashMap();
      if (var0 == null) {
         return var1;
      } else {
         Matcher var2 = intListPattern.matcher(var0);
         int var3 = 0;

         int var4;
         for(var4 = -1; var2.find(); var4 = var2.end()) {
            String var5 = null;
            switch (var3++) {
               case 0:
                  var5 = "x";
                  break;
               case 1:
                  var5 = "y";
                  break;
               case 2:
                  var5 = "z";
                  break;
               case 3:
                  var5 = "r";
            }

            if (var5 != null && var2.group(1).length() > 0) {
               var1.put(var5, var2.group(1));
            }
         }

         if (var4 < var0.length()) {
            var2 = keyValueListPattern.matcher(var4 == -1 ? var0 : var0.substring(var4));

            while(var2.find()) {
               var1.put(var2.group(1), var2.group(2));
            }
         }

         return var1;
      }
   }
}
