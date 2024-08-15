package net.minecraft.mite;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StringHelper;

public class TournamentStanding {
   public boolean caught_a_fish;
   public boolean killed_a_skeleton;
   public boolean killed_a_zombie;
   public boolean killed_a_spider;
   public boolean killed_a_wood_spider;
   public boolean killed_a_creeper;
   public boolean killed_a_large_slime;
   public boolean killed_a_ghoul;
   public boolean killed_a_wight;
   public boolean killed_an_invisible_stalker;
   public boolean killed_a_witch;
   public boolean killed_a_shadow;
   public boolean killed_a_hellhound;
   public boolean killed_a_demon_spider;
   public int copper_nuggets_harvested;
   public int silver_nuggets_harvested;
   public int gold_nuggets_harvested;
   public int mithril_nuggets_harvested;
   public int adamantium_nuggets_harvested;
   public int copper_ore_harvested;
   public int silver_ore_harvested;
   public int gold_ore_harvested;
   public int iron_ore_harvested;
   public int mithril_ore_harvested;
   public int adamantium_ore_harvested;
   public int experience;

   public TournamentStanding readFromNBT(NBTTagCompound par1NBTTagCompound) {
      this.caught_a_fish = par1NBTTagCompound.getBoolean("caught_a_fish");
      this.killed_a_skeleton = par1NBTTagCompound.getBoolean("killed_a_skeleton");
      this.killed_a_zombie = par1NBTTagCompound.getBoolean("killed_a_zombie");
      this.killed_a_spider = par1NBTTagCompound.getBoolean("killed_a_spider");
      this.killed_a_wood_spider = par1NBTTagCompound.getBoolean("killed_a_wood_spider");
      this.killed_a_creeper = par1NBTTagCompound.getBoolean("killed_a_creeper");
      this.killed_a_large_slime = par1NBTTagCompound.getBoolean("killed_a_large_slime");
      this.killed_a_ghoul = par1NBTTagCompound.getBoolean("killed_a_ghoul");
      this.killed_a_wight = par1NBTTagCompound.getBoolean("killed_a_wight");
      this.killed_an_invisible_stalker = par1NBTTagCompound.getBoolean("killed_an_invisible_stalker");
      this.killed_a_witch = par1NBTTagCompound.getBoolean("killed_a_witch");
      this.killed_a_shadow = par1NBTTagCompound.getBoolean("killed_a_shadow");
      this.killed_a_hellhound = par1NBTTagCompound.getBoolean("killed_a_hellhound");
      this.killed_a_demon_spider = par1NBTTagCompound.getBoolean("killed_a_demon_spider");
      this.copper_nuggets_harvested = par1NBTTagCompound.getInteger("copper_nuggets_harvested");
      this.silver_nuggets_harvested = par1NBTTagCompound.getInteger("silver_nuggets_harvested");
      this.gold_nuggets_harvested = par1NBTTagCompound.getInteger("gold_nuggets_harvested");
      this.mithril_nuggets_harvested = par1NBTTagCompound.getInteger("mithril_nuggets_harvested");
      this.adamantium_nuggets_harvested = par1NBTTagCompound.getInteger("adamantium_nuggets_harvested");
      this.copper_ore_harvested = par1NBTTagCompound.getInteger("copper_ore_smelted");
      this.silver_ore_harvested = par1NBTTagCompound.getInteger("silver_ore_smelted");
      this.gold_ore_harvested = par1NBTTagCompound.getInteger("gold_ore_smelted");
      this.iron_ore_harvested = par1NBTTagCompound.getInteger("iron_ore_smelted");
      this.mithril_ore_harvested = par1NBTTagCompound.getInteger("mithril_ore_harvested");
      this.adamantium_ore_harvested = par1NBTTagCompound.getInteger("adamantium_ore_harvested");
      return this;
   }

   public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setBoolean("caught_a_fish", this.caught_a_fish);
      par1NBTTagCompound.setBoolean("killed_a_skeleton", this.killed_a_skeleton);
      par1NBTTagCompound.setBoolean("killed_a_zombie", this.killed_a_zombie);
      par1NBTTagCompound.setBoolean("killed_a_spider", this.killed_a_spider);
      par1NBTTagCompound.setBoolean("killed_a_wood_spider", this.killed_a_wood_spider);
      par1NBTTagCompound.setBoolean("killed_a_creeper", this.killed_a_creeper);
      par1NBTTagCompound.setBoolean("killed_a_large_slime", this.killed_a_large_slime);
      par1NBTTagCompound.setBoolean("killed_a_ghoul", this.killed_a_ghoul);
      par1NBTTagCompound.setBoolean("killed_a_wight", this.killed_a_wight);
      par1NBTTagCompound.setBoolean("killed_an_invisible_stalker", this.killed_an_invisible_stalker);
      par1NBTTagCompound.setBoolean("killed_a_witch", this.killed_a_witch);
      par1NBTTagCompound.setBoolean("killed_a_shadow", this.killed_a_shadow);
      par1NBTTagCompound.setBoolean("killed_a_hellhound", this.killed_a_hellhound);
      par1NBTTagCompound.setBoolean("killed_a_demon_spider", this.killed_a_demon_spider);
      par1NBTTagCompound.setInteger("copper_nuggets_harvested", this.copper_nuggets_harvested);
      par1NBTTagCompound.setInteger("silver_nuggets_harvested", this.silver_nuggets_harvested);
      par1NBTTagCompound.setInteger("gold_nuggets_harvested", this.gold_nuggets_harvested);
      par1NBTTagCompound.setInteger("mithril_nuggets_harvested", this.mithril_nuggets_harvested);
      par1NBTTagCompound.setInteger("adamantium_nuggets_harvested", this.adamantium_nuggets_harvested);
      par1NBTTagCompound.setInteger("copper_ore_smelted", this.copper_ore_harvested);
      par1NBTTagCompound.setInteger("silver_ore_smelted", this.silver_ore_harvested);
      par1NBTTagCompound.setInteger("gold_ore_smelted", this.gold_ore_harvested);
      par1NBTTagCompound.setInteger("iron_ore_smelted", this.iron_ore_harvested);
      par1NBTTagCompound.setInteger("mithril_ore_harvested", this.mithril_ore_harvested);
      par1NBTTagCompound.setInteger("adamantium_ore_harvested", this.adamantium_ore_harvested);
   }

   public int calcScore() {
      int score = 0;
      score += this.caught_a_fish ? 300 : 0;
      score += this.killed_a_skeleton ? 100 : 0;
      score += this.killed_a_zombie ? 200 : 0;
      score += this.killed_a_spider ? 200 : 0;
      score += this.killed_a_wood_spider ? 200 : 0;
      score += this.killed_a_creeper ? 200 : 0;
      score += this.killed_a_large_slime ? 300 : 0;
      score += this.killed_a_ghoul ? 300 : 0;
      score += this.killed_a_wight ? 400 : 0;
      score += this.killed_an_invisible_stalker ? 300 : 0;
      score += this.killed_a_witch ? 500 : 0;
      score += this.killed_a_shadow ? 500 : 0;
      score += this.killed_a_hellhound ? 700 : 0;
      score += this.killed_a_demon_spider ? 1000 : 0;
      score += this.copper_nuggets_harvested * 100;
      score += this.silver_nuggets_harvested * 200;
      score += this.gold_nuggets_harvested * 300;
      score += this.mithril_nuggets_harvested * 500;
      score += this.adamantium_nuggets_harvested * 900;
      score += this.copper_ore_harvested * 900;
      score += this.silver_ore_harvested * 1800;
      score += this.gold_ore_harvested * 2700;
      score += this.iron_ore_harvested * 2700;
      score += this.mithril_ore_harvested * 4500;
      score += this.adamantium_ore_harvested * 8100;
      score += this.experience;
      return score;
   }

   private String getKilledMobString(String mob_name, boolean killed) {
      if (!killed) {
         return "";
      } else {
         StringBuffer sb = new StringBuffer();
         sb.append(StringHelper.startsWithVowel(mob_name) ? "an" : "a");
         sb.append(" ");
         sb.append(mob_name);
         sb.append(", ");
         return sb.toString();
      }
   }

   private String getHarvestedItemString(Item item, int number_harvested) {
      if (number_harvested == 0) {
         return "";
      } else {
         StringBuffer sb = new StringBuffer();
         sb.append(number_harvested);
         sb.append(" ");
         sb.append(item.getItemDisplayName().toLowerCase());
         if (number_harvested > 1) {
            sb.append("s");
         }

         sb.append(", ");
         return sb.toString();
      }
   }

   public String toString(String username) {
      StringBuffer line = new StringBuffer();
      StringBuffer sb = new StringBuffer();
      line.append(this.calcScore() + ": " + username + " has killed ");
      sb.append(this.getKilledMobString("skeleton", this.killed_a_skeleton));
      sb.append(this.getKilledMobString("zombie", this.killed_a_zombie));
      sb.append(this.getKilledMobString("spider", this.killed_a_spider));
      sb.append(this.getKilledMobString("wood_spider", this.killed_a_wood_spider));
      sb.append(this.getKilledMobString("creeper", this.killed_a_creeper));
      sb.append(this.getKilledMobString("large slime", this.killed_a_large_slime));
      sb.append(this.getKilledMobString("ghoul", this.killed_a_ghoul));
      sb.append(this.getKilledMobString("wight", this.killed_a_wight));
      sb.append(this.getKilledMobString("invisible stalker", this.killed_an_invisible_stalker));
      sb.append(this.getKilledMobString("witch", this.killed_a_witch));
      sb.append(this.getKilledMobString("shadow", this.killed_a_shadow));
      sb.append(this.getKilledMobString("hellhound", this.killed_a_hellhound));
      sb.append(this.getKilledMobString("demon spider", this.killed_a_demon_spider));
      if (sb.length() == 0) {
         line.append("no mobs, ");
      } else {
         line.append(sb.toString());
      }

      line.append("has harvested ");
      sb.setLength(0);
      sb.append(this.getHarvestedItemString(Item.copperNugget, this.copper_nuggets_harvested));
      sb.append(this.getHarvestedItemString(Item.silverNugget, this.silver_nuggets_harvested));
      sb.append(this.getHarvestedItemString(Item.goldNugget, this.gold_nuggets_harvested));
      sb.append(this.getHarvestedItemString(Item.mithrilNugget, this.mithril_nuggets_harvested));
      sb.append(this.getHarvestedItemString(Item.adamantiumNugget, this.adamantium_nuggets_harvested));
      sb.append(this.getHarvestedItemString(Item.getItem(Block.oreCopper), this.copper_ore_harvested));
      sb.append(this.getHarvestedItemString(Item.getItem(Block.oreSilver), this.silver_ore_harvested));
      sb.append(this.getHarvestedItemString(Item.getItem(Block.oreGold), this.gold_ore_harvested));
      sb.append(this.getHarvestedItemString(Item.getItem(Block.oreIron), this.iron_ore_harvested));
      sb.append(this.getHarvestedItemString(Item.getItem(Block.oreMithril), this.mithril_ore_harvested));
      sb.append(this.getHarvestedItemString(Item.getItem(Block.oreAdamantium), this.adamantium_ore_harvested));
      if (sb.length() == 0) {
         line.append("no scoring items, ");
      } else {
         line.append(sb.toString());
      }

      if (this.caught_a_fish) {
         line.append("has caught a fish, ");
      }

      line.append("and has " + this.experience + " experience.");
      return line.toString();
   }
}
