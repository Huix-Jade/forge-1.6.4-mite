package net.minecraft.world;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class GameRules {
   private TreeMap theGameRules = new TreeMap();

   public GameRules() {
      this.addGameRule("doFireTick", "true");
      this.addGameRule("mobGriefing", "true");
      this.addGameRule("keepInventory", "false");
      this.addGameRule("doMobSpawning", "true");
      this.addGameRule("doMobLoot", "true");
      this.addGameRule("doTileDrops", "true");
      this.addGameRule("commandBlockOutput", "true");
      this.addGameRule("naturalRegeneration", "true");
      this.addGameRule("doDaylightCycle", "true");
   }

   public void addGameRule(String par1Str, String par2Str) {
      this.theGameRules.put(par1Str, new GameRuleValue(par2Str));
   }

   public void setOrCreateGameRule(String par1Str, String par2Str) {
      GameRuleValue var3 = (GameRuleValue)this.theGameRules.get(par1Str);
      if (var3 != null) {
         var3.setValue(par2Str);
      } else {
         this.addGameRule(par1Str, par2Str);
      }

   }

   public String getGameRuleStringValue(String par1Str) {
      GameRuleValue var2 = (GameRuleValue)this.theGameRules.get(par1Str);
      return var2 != null ? var2.getGameRuleStringValue() : "";
   }

   public boolean getGameRuleBooleanValue(String par1Str) {
      return this.getGameRuleOverrideBooleanValue(par1Str);
   }

   public NBTTagCompound writeGameRulesToNBT() {
      NBTTagCompound var1 = new NBTTagCompound("GameRules");
      Iterator var2 = this.theGameRules.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         GameRuleValue var4 = (GameRuleValue)this.theGameRules.get(var3);
         var1.setString(var3, var4.getGameRuleStringValue());
      }

      return var1;
   }

   public void readGameRulesFromNBT(NBTTagCompound par1NBTTagCompound) {
      Collection var2 = par1NBTTagCompound.getTags();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         NBTBase var4 = (NBTBase)var3.next();
         String var5 = var4.getName();
         String var6 = par1NBTTagCompound.getString(var4.getName());
         this.setOrCreateGameRule(var5, var6);
      }

   }

   public String[] getRules() {
      return (String[])((String[])this.theGameRules.keySet().toArray(new String[0]));
   }

   public boolean hasRule(String par1Str) {
      return this.theGameRules.containsKey(par1Str);
   }

   private boolean getGameRuleOverrideBooleanValue(String rule_name) {
      if (rule_name.equals("doFireTick")) {
         return true;
      } else if (rule_name.equals("mobGriefing")) {
         return true;
      } else if (rule_name.equals("keepInventory")) {
         return false;
      } else if (rule_name.equals("doMobSpawning")) {
         return true;
      } else if (rule_name.equals("doMobLoot")) {
         return true;
      } else if (rule_name.equals("doTileDrops")) {
         return true;
      } else if (rule_name.equals("commandBlockOutput")) {
         return true;
      } else if (rule_name.equals("naturalRegeneration")) {
         return true;
      } else if (rule_name.equals("doDaylightCycle")) {
         return true;
      } else {
         Minecraft.setErrorMessage("getGameRuleOverride: unhandled rule " + rule_name);
         return true;
      }
   }
}
