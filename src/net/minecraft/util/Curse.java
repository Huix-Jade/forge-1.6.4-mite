package net.minecraft.util;

import java.util.Random;
import java.util.UUID;
import net.minecraft.client.Minecraft;

public class Curse {
   public static Curse[] cursesList = new Curse[64];
   public static final Curse equipment_decays_faster = new Curse(1, "equipmentDecay");
   public static final Curse cannot_hold_breath = new Curse(2, "cantHoldBreath");
   public static final Curse cannot_run = new Curse(3, "cantRun");
   public static final Curse cannot_eat_animals = new Curse(4, "cantEatAnimals");
   public static final Curse cannot_eat_plants = new Curse(5, "cantEatPlants");
   public static final Curse cannot_drink = new Curse(6, "cantDrink");
   public static final Curse endermen_aggro = new Curse(7, "endermenEnemy");
   public static final Curse clumsiness = new Curse(8, "clumsiness");
   public static final Curse entanglement = new Curse(9, "entanglement");
   public static final Curse cannot_wear_armor = new Curse(10, "cantWearArmor");
   public static final Curse cannot_open_chests = new Curse(11, "cantOpenChests");
   public static final Curse cannot_sleep = new Curse(12, "cantSleep");
   public static final Curse fear_of_spiders = new Curse(13, "fearOfSpiders");
   public static final Curse fear_of_wolves = new Curse(14, "fearOfWolves");
   public static final Curse fear_of_creepers = new Curse(15, "fearOfCreepers");
   public static final Curse fear_of_undead = new Curse(16, "fearOfUndead");
   public String cursed_player_username;
   public UUID cursing_entity_uuid;
   public long time_of_realization;
   public boolean has_been_realized;
   public boolean effect_known;
   public boolean effect_has_already_been_learned;
   public int id;
   public String key;

   public Curse(int id, String key) {
      this.id = id;
      this.key = key;
      if (cursesList[id] != null) {
         Minecraft.setErrorMessage("Curse id=" + id + " already taken!");
      } else {
         cursesList[id] = this;
      }

   }

   public Curse(String cursed_player_username, UUID cursing_entity_uuid, Curse curse, long time_of_realization, boolean has_been_realized, boolean effect_known) {
      this.cursed_player_username = cursed_player_username;
      this.cursing_entity_uuid = cursing_entity_uuid;
      this.id = curse.id;
      this.time_of_realization = time_of_realization;
      this.has_been_realized = has_been_realized;
      this.effect_known = effect_known;
   }

   public static Curse getRandomCurse(Random rand) {
      if (Minecraft.inDevMode()) {
         return cannot_drink;
      } else {
         int index;
         do {
            index = rand.nextInt(cursesList.length);
         } while(cursesList[index] == null);

         return cursesList[index];
      }
   }

   public String getTitle() {
      return Translator.get("curse." + this.key + ".name");
   }

   public String[] getTooltip() {
      return StringHelper.explode(Translator.get("curse." + this.key + ".desc"), "\\|");
   }
}
