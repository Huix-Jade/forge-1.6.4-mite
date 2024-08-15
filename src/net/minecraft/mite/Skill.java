package net.minecraft.mite;

import net.minecraft.block.BitHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringHelper;

public class Skill {
   public static final int ANY = -1;
   public static final int ALL = -1;
   public static final int NONE = 0;
   public static Skill[] list = new Skill[32];
   private static int num_skills;
   public static Skill FIGHTING = new Skill("fighting");
   public static Skill ARCHERY = new Skill("archery");
   public static Skill MINING = new Skill("mining");
   public static Skill FOOD_PREPARATION = new Skill("foodPreparation");
   public static Skill MASONRY = new Skill("masonry");
   public static Skill FARMING = new Skill("farming");
   public static Skill TINKERING = new Skill("tinkering");
   public static Skill CARPENTRY = new Skill("carpentry");
   public static Skill BLACKSMITHING = new Skill("blacksmithing");
   public static Skill BREWING = new Skill("brewing");
   public static Skill FINE_ARTS = new Skill("fineArts");
   public static Skill ENCHANTING = new Skill("enchanting");
   public static Skill FISHING = new Skill("fishing");
   public final int id;
   final String unlocalized_name;

   public Skill(String unlocalized_name) {
      this.id = 1 << num_skills;
      list[num_skills++] = this;
      this.unlocalized_name = "skill." + unlocalized_name;
   }

   static Skill getById(int id) {
      for(int i = 0; i < num_skills; ++i) {
         if ((list[i].id & id) != 0) {
            return list[i];
         }
      }

      return null;
   }

   static Skill[] getSkillsByIds(int ids) {
      int num_skills_present = getNumSkills(ids);
      if (num_skills_present == 0) {
         return null;
      } else {
         Skill[] skills = new Skill[num_skills_present];
         int j = 0;

         for(int i = 0; i < num_skills; ++i) {
            if (BitHelper.isBitSet(ids, list[i].id)) {
               skills[j++] = list[i];
            }
         }

         return j == 0 ? null : skills;
      }
   }

   public static Skill getByLocalizedName(String localized_name, boolean profession_name) {
      for(int i = 0; i < num_skills; ++i) {
         if (list[i].getLocalizedName(profession_name).equalsIgnoreCase(localized_name)) {
            return list[i];
         }
      }

      return null;
   }

   public static String getSkillsString(int ids, boolean profession_names, String delimiter) {
      StringBuffer sb = new StringBuffer();

      for(int i = 0; i < num_skills; ++i) {
         Skill skill = list[i];
         if (BitHelper.isBitSet(ids, skill.id)) {
            sb.append(skill.getLocalizedName(profession_names) + delimiter);
         }
      }

      String s = sb.toString();
      return s.isEmpty() ? null : StringHelper.left(s, -delimiter.length());
   }

   public static String getSkillsetsString(int[] skillsets, boolean profession_names) {
      StringBuffer sb = new StringBuffer();
      String delimiter = " or ";

      for(int i = 0; i < skillsets.length; ++i) {
         sb.append(getSkillsString(skillsets[i], profession_names, profession_names ? " / " : " + ") + delimiter);
      }

      String s = sb.toString();
      return s.isEmpty() ? null : StringHelper.left(s, -delimiter.length());
   }

   public static int getNumSkills() {
      return num_skills;
   }

   public static int getNumSkills(int ids) {
      int num = 0;

      for(int i = 0; i < num_skills; ++i) {
         if (BitHelper.isBitSet(ids, list[i].id)) {
            ++num;
         }
      }

      return num;
   }

   public static boolean skillExistsIn(Skill skill, int ids) {
      return BitHelper.isBitSet(ids, skill.id);
   }

   public String toString() {
      return this.getLocalizedName(false);
   }

   public String getLocalizedName(boolean profession_name) {
      return StatCollector.translateToLocal(profession_name ? this.unlocalized_name + ".profession" : this.unlocalized_name);
   }

   public String getLocalizedDescription() {
      return StatCollector.translateToLocal(this.unlocalized_name + ".description");
   }
}
