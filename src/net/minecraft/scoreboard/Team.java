package net.minecraft.scoreboard;

public abstract class Team {
   public boolean isSameTeam(Team var1) {
      if (var1 == null) {
         return false;
      } else {
         return this == var1;
      }
   }

   public abstract String func_96661_b();

   public abstract String func_142053_d(String var1);

   public abstract boolean func_98297_h();

   public abstract boolean getAllowFriendlyFire();
}
