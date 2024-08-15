package net.minecraft.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.dedicated.DedicatedServer;

public enum EnumTournamentType {
   open(true, false, false, true, 384, 0),
   pickaxe(true, false, true, true, 384, 0),
   diamond(true, false, true, true, 384, 0),
   nether(true, false, true, true, 384, 0),
   score(true, false, true, true, 384, 12),
   wonder(false, true, false, true, 2048, 432);

   public final boolean has_safe_zone;
   public final boolean uses_allotted_times;
   public final boolean allows_animal_spawning;
   public final boolean prevents_time_forwarding;
   public final int arena_radius;
   public final int time_limit_in_days;

   private EnumTournamentType(boolean has_safe_zone, boolean uses_allotted_times, boolean allows_animal_spawning, boolean prevents_time_forwarding, int arena_radius, int time_limit_in_days) {
      this.has_safe_zone = has_safe_zone;
      this.uses_allotted_times = uses_allotted_times;
      this.allows_animal_spawning = allows_animal_spawning;
      this.prevents_time_forwarding = prevents_time_forwarding;
      this.arena_radius = arena_radius;
      this.time_limit_in_days = time_limit_in_days;
   }

   public static EnumTournamentType get(int ordinal) {
      return values()[ordinal];
   }

   public static EnumTournamentType getTournamentType(String tournament_type) {
      if (tournament_type == null) {
         return null;
      } else if (tournament_type.equalsIgnoreCase("open")) {
         return open;
      } else if (tournament_type.equalsIgnoreCase("pickaxe")) {
         return pickaxe;
      } else if (tournament_type.equalsIgnoreCase("diamond")) {
         return diamond;
      } else if (tournament_type.equalsIgnoreCase("nether")) {
         return nether;
      } else if (tournament_type.equalsIgnoreCase("score")) {
         return score;
      } else {
         return tournament_type.equalsIgnoreCase("wonder") ? wonder : null;
      }
   }

   public static String getTournamentObjective(EnumTournamentType tournament_type) {
      String objective;
      if (tournament_type == pickaxe) {
         objective = "craft a pickaxe";
      } else if (tournament_type == diamond) {
         objective = "collect a diamond";
      } else {
         if (tournament_type != nether) {
            if (tournament_type == score) {
               return "The player that accumulates the highest score wins! Increased your score by killing one of each mob, obtaining nuggets from gravel, harvesting metal ores, catching a fish, and gaining experience. The tournament will run until the end of day " + (DedicatedServer.tournament_type == null ? 0 : DedicatedServer.tournament_type.time_limit_in_days) + ".";
            }

            if (tournament_type == wonder) {
               return "The server that builds a sacred pyramid in the least number of world ticks wins! Combine sandstone with gold nuggets to produce sacred sandstone. The pyramid must be " + DedicatedServer.getRequiredPyramidHeight() + " blocks tall and the outside made of sacred sandstone.";
            }

            return null;
         }

         objective = "enter the netherworld";
      }

      return "The first player to " + objective + " wins!";
   }

   public static String getTournamentVictoryMessage(EntityPlayer player, EnumTournamentType tournament_type) {
      String objective;
      if (tournament_type == pickaxe) {
         objective = "crafted a pickaxe";
      } else if (tournament_type == diamond) {
         objective = "collected a diamond";
      } else {
         if (tournament_type != nether) {
            if (tournament_type == score) {
               return "Notice: The tournament has concluded and the winner will be announced shortly.";
            }

            if (tournament_type == wonder) {
               return "Notice: The sacred pyramid has been completed! It took " + DedicatedServer.getTickOfWorld() + " world ticks! Congratulations!";
            }

            return null;
         }

         objective = "entered the netherworld";
      }

      return "Notice: " + player.username + " has " + objective + " and wins the tournament! Congratulations!";
   }
}
