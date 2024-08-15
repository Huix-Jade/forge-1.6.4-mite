package net.minecraft.scoreboard;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ScorePlayerTeam extends Team {
   private final Scoreboard theScoreboard;
   private final String field_96675_b;
   private final Set membershipSet = new HashSet();
   private String field_96673_d;
   private String field_96674_e = "";
   private String colorSuffix = "";
   private boolean allowFriendlyFire = true;
   private boolean field_98301_h = true;

   public ScorePlayerTeam(Scoreboard var1, String var2) {
      this.theScoreboard = var1;
      this.field_96675_b = var2;
      this.field_96673_d = var2;
   }

   public String func_96661_b() {
      return this.field_96675_b;
   }

   public String func_96669_c() {
      return this.field_96673_d;
   }

   public void setTeamName(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Name cannot be null");
      } else {
         this.field_96673_d = var1;
         this.theScoreboard.func_96538_b(this);
      }
   }

   public Collection getMembershipCollection() {
      return this.membershipSet;
   }

   public String getColorPrefix() {
      return this.field_96674_e;
   }

   public void setNamePrefix(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Prefix cannot be null");
      } else {
         this.field_96674_e = var1;
         this.theScoreboard.func_96538_b(this);
      }
   }

   public String getColorSuffix() {
      return this.colorSuffix;
   }

   public void setNameSuffix(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Suffix cannot be null");
      } else {
         this.colorSuffix = var1;
         this.theScoreboard.func_96538_b(this);
      }
   }

   public String func_142053_d(String var1) {
      return this.getColorPrefix() + var1 + this.getColorSuffix();
   }

   public static String formatPlayerName(Team var0, String var1) {
      return var0 == null ? var1 : var0.func_142053_d(var1);
   }

   public boolean getAllowFriendlyFire() {
      return this.allowFriendlyFire;
   }

   public void setAllowFriendlyFire(boolean var1) {
      this.allowFriendlyFire = var1;
      this.theScoreboard.func_96538_b(this);
   }

   public boolean func_98297_h() {
      return this.field_98301_h;
   }

   public void setSeeFriendlyInvisiblesEnabled(boolean var1) {
      this.field_98301_h = var1;
      this.theScoreboard.func_96538_b(this);
   }

   public int func_98299_i() {
      int var1 = 0;
      if (this.getAllowFriendlyFire()) {
         var1 |= 1;
      }

      if (this.func_98297_h()) {
         var1 |= 2;
      }

      return var1;
   }

   public void func_98298_a(int var1) {
      this.setAllowFriendlyFire((var1 & 1) > 0);
      this.setSeeFriendlyInvisiblesEnabled((var1 & 2) > 0);
   }
}
