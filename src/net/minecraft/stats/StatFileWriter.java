package net.minecraft.stats;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import argo.saj.InvalidSyntaxException;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.asm.ReobfuscationMarker;
import net.minecraft.client.stats.StatPlaceholder;
import net.minecraft.util.MD5String;
import net.minecraft.util.Session;

@ReobfuscationMarker
public class StatFileWriter {
   private Map field_77457_a = new HashMap();
   private Map field_77455_b = new HashMap();
   private boolean field_77456_c;
   private StatsSyncher statsSyncher;

   public StatFileWriter(Session par1Session, File par2File) {
      File var3 = new File(par2File, "stats");
      if (!var3.exists()) {
         var3.mkdir();
      }

      File[] var4 = par2File.listFiles();

       for (File var7 : var4) {
           if (var7.getName().startsWith("stats_") && var7.getName().endsWith(".dat")) {
               File var8 = new File(var3, var7.getName());
               if (!var8.exists()) {
                   System.out.println("Relocating " + var7.getName());
                   var7.renameTo(var8);
               }
           }
       }

      this.statsSyncher = new StatsSyncher(par1Session, this, var3);
   }

   public void readStat(StatBase par1StatBase, int par2) {
      this.writeStatToMap(this.field_77455_b, par1StatBase, par2);
      this.writeStatToMap(this.field_77457_a, par1StatBase, par2);
      this.field_77456_c = true;
   }

   private void writeStatToMap(Map par1Map, StatBase par2StatBase, int par3) {
      Integer var4 = (Integer)par1Map.get(par2StatBase);
      int var5 = var4 == null ? 0 : var4;
      par1Map.put(par2StatBase, var5 + par3);
   }

   public Map func_77445_b() {
      return new HashMap(this.field_77455_b);
   }

   public void writeStats(Map par1Map) {
      if (par1Map != null) {
         this.field_77456_c = true;

          for (Object o : par1Map.keySet()) {
              StatBase var3 = (StatBase) o;
              this.writeStatToMap(this.field_77455_b, var3, (Integer) par1Map.get(var3));
              this.writeStatToMap(this.field_77457_a, var3, (Integer) par1Map.get(var3));
          }
      }

   }

   public void func_77452_b(Map par1Map) {
      if (par1Map != null) {

          for (Object o : par1Map.keySet()) {
              StatBase var3 = (StatBase) o;
              Integer var4 = (Integer) this.field_77455_b.get(var3);
              int var5 = var4 == null ? 0 : var4;
              this.field_77457_a.put(var3, (Integer) par1Map.get(var3) + var5);
          }
      }

   }

   public void func_77448_c(Map par1Map) {
      if (par1Map != null) {
         this.field_77456_c = true;

          for (Object o : par1Map.keySet()) {
              StatBase var3 = (StatBase) o;
              this.writeStatToMap(this.field_77455_b, var3, (Integer) par1Map.get(var3));
          }
      }

   }

   public static Map func_77453_b(String par0Str) {
      HashMap var1 = new HashMap();

      try {
         String var2 = "local";
         StringBuilder var3 = new StringBuilder();
         JsonRootNode var4 = (new JdomParser()).parse(par0Str);
         List var5 = var4.getArrayNode(new Object[]{"stats-change"});

          for (Object o : var5) {
              JsonNode var7 = (JsonNode) o;
              Map var8 = var7.getFields();
              Map.Entry var9 = (Map.Entry) var8.entrySet().iterator().next();
              int var10 = Integer.parseInt(((JsonStringNode) var9.getKey()).getText());
              int var11 = Integer.parseInt(((JsonNode) var9.getValue()).getText());
              boolean var12 = true;
              StatBase var13 = StatList.getOneShotStat(var10);
              if (var13 == null) {
                  var12 = false;
                  var13 = (new StatPlaceholder(var10)).registerStat();
              }

              var3.append(StatList.getOneShotStat(var10).statGuid).append(",");
              var3.append(var11).append(",");
              if (var12) {
                  var1.put(var13, var11);
              }
          }

         MD5String var15 = new MD5String(var2);
         String var16 = var15.getMD5String(var3.toString());
         if (!var16.equals(var4.getStringValue(new Object[]{"checksum"}))) {
            System.out.println("CHECKSUM MISMATCH");
            return null;
         }
      } catch (InvalidSyntaxException var14) {
         var14.printStackTrace();
      }

      return var1;
   }

   public static String func_77441_a(String par0Str, String par1Str, Map par2Map) {
      StringBuilder var3 = new StringBuilder();
      StringBuilder var4 = new StringBuilder();
      boolean var5 = true;
      var3.append("{\r\n");
      if (par0Str != null && par1Str != null) {
         var3.append("  \"user\":{\r\n");
         var3.append("    \"name\":\"").append(par0Str).append("\",\r\n");
         var3.append("    \"sessionid\":\"").append(par1Str).append("\"\r\n");
         var3.append("  },\r\n");
      }

      var3.append("  \"stats-change\":[");

       for (Object o : par2Map.keySet()) {
           StatBase var7 = (StatBase) o;
           if (var5) {
               var5 = false;
           } else {
               var3.append("},");
           }

           var3.append("\r\n    {\"").append(var7.statId).append("\":").append(par2Map.get(var7));
           var4.append(var7.statGuid).append(",");
           var4.append(par2Map.get(var7)).append(",");
       }

      if (!var5) {
         var3.append("}");
      }

      MD5String var8 = new MD5String(par1Str);
      var3.append("\r\n  ],\r\n");
      var3.append("  \"checksum\":\"").append(var8.getMD5String(var4.toString())).append("\"\r\n");
      var3.append("}");
      return var3.toString();
   }

   public boolean hasAchievementUnlocked(Achievement par1Achievement) {
      return this.field_77457_a.containsKey(par1Achievement);
   }

   public void clearAchievement(Achievement achievement) {
      this.field_77457_a.remove(achievement);
   }

   public boolean canUnlockAchievement(Achievement par1Achievement) {
      return this.haveAllParentAchievementsBeenUnlocked(par1Achievement);
   }

   public int writeStat(StatBase par1StatBase) {
      Integer var2 = (Integer)this.field_77457_a.get(par1StatBase);
      return var2 == null ? 0 : var2;
   }

   public void syncStats() {
      this.statsSyncher.syncStatsFileWithMap(this.func_77445_b());
   }

   public void func_77449_e() {
      if (this.field_77456_c && this.statsSyncher.func_77425_c()) {
         this.statsSyncher.beginSendStats(this.func_77445_b());
      }

      this.statsSyncher.func_77422_e();
   }

   public boolean haveAllParentAchievementsBeenUnlocked(Achievement achievement) {
      while(true) {
         if (achievement.parentAchievement != null) {
            achievement = achievement.parentAchievement;
            if (this.hasAchievementUnlocked(achievement)) {
               continue;
            }

            return false;
         }

         return true;
      }
   }
}
