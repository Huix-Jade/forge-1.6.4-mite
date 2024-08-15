package net.minecraft.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Scoreboard {
   private final Map scoreObjectives = new HashMap();
   private final Map field_96543_b = new HashMap();
   private final Map field_96544_c = new HashMap();
   private final ScoreObjective[] field_96541_d = new ScoreObjective[3];
   private final Map field_96542_e = new HashMap();
   private final Map teamMemberships = new HashMap();

   public ScoreObjective getObjective(String var1) {
      return (ScoreObjective)this.scoreObjectives.get(var1);
   }

   public ScoreObjective func_96535_a(String var1, ScoreObjectiveCriteria var2) {
      ScoreObjective var3 = this.getObjective(var1);
      if (var3 != null) {
         throw new IllegalArgumentException("An objective with the name '" + var1 + "' already exists!");
      } else {
         var3 = new ScoreObjective(this, var1, var2);
         Object var4 = (List)this.field_96543_b.get(var2);
         if (var4 == null) {
            var4 = new ArrayList();
            this.field_96543_b.put(var2, var4);
         }

         ((List)var4).add(var3);
         this.scoreObjectives.put(var1, var3);
         this.func_96522_a(var3);
         return var3;
      }
   }

   public Collection func_96520_a(ScoreObjectiveCriteria var1) {
      Collection var2 = (Collection)this.field_96543_b.get(var1);
      return var2 == null ? new ArrayList() : new ArrayList(var2);
   }

   public Score func_96529_a(String var1, ScoreObjective var2) {
      Object var3 = (Map)this.field_96544_c.get(var1);
      if (var3 == null) {
         var3 = new HashMap();
         this.field_96544_c.put(var1, var3);
      }

      Score var4 = (Score)((Map)var3).get(var2);
      if (var4 == null) {
         var4 = new Score(this, var2, var1);
         ((Map)var3).put(var2, var4);
      }

      return var4;
   }

   public Collection func_96534_i(ScoreObjective var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.field_96544_c.values().iterator();

      while(var3.hasNext()) {
         Map var4 = (Map)var3.next();
         Score var5 = (Score)var4.get(var1);
         if (var5 != null) {
            var2.add(var5);
         }
      }

      Collections.sort(var2, Score.field_96658_a);
      return var2;
   }

   public Collection getScoreObjectives() {
      return this.scoreObjectives.values();
   }

   public Collection getObjectiveNames() {
      return this.field_96544_c.keySet();
   }

   public void func_96515_c(String var1) {
      Map var2 = (Map)this.field_96544_c.remove(var1);
      if (var2 != null) {
         this.func_96516_a(var1);
      }

   }

   public Collection func_96528_e() {
      Collection var1 = this.field_96544_c.values();
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Map var4 = (Map)var3.next();
         var2.addAll(var4.values());
      }

      return var2;
   }

   public Map func_96510_d(String var1) {
      Object var2 = (Map)this.field_96544_c.get(var1);
      if (var2 == null) {
         var2 = new HashMap();
      }

      return (Map)var2;
   }

   public void func_96519_k(ScoreObjective var1) {
      this.scoreObjectives.remove(var1.getName());

      for(int var2 = 0; var2 < 3; ++var2) {
         if (this.func_96539_a(var2) == var1) {
            this.func_96530_a(var2, (ScoreObjective)null);
         }
      }

      List var5 = (List)this.field_96543_b.get(var1.getCriteria());
      if (var5 != null) {
         var5.remove(var1);
      }

      Iterator var3 = this.field_96544_c.values().iterator();

      while(var3.hasNext()) {
         Map var4 = (Map)var3.next();
         var4.remove(var1);
      }

      this.func_96533_c(var1);
   }

   public void func_96530_a(int var1, ScoreObjective var2) {
      this.field_96541_d[var1] = var2;
   }

   public ScoreObjective func_96539_a(int var1) {
      return this.field_96541_d[var1];
   }

   public ScorePlayerTeam func_96508_e(String var1) {
      return (ScorePlayerTeam)this.field_96542_e.get(var1);
   }

   public ScorePlayerTeam createTeam(String var1) {
      ScorePlayerTeam var2 = this.func_96508_e(var1);
      if (var2 != null) {
         throw new IllegalArgumentException("An objective with the name '" + var1 + "' already exists!");
      } else {
         var2 = new ScorePlayerTeam(this, var1);
         this.field_96542_e.put(var1, var2);
         this.func_96523_a(var2);
         return var2;
      }
   }

   public void func_96511_d(ScorePlayerTeam var1) {
      this.field_96542_e.remove(var1.func_96661_b());
      Iterator var2 = var1.getMembershipCollection().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         this.teamMemberships.remove(var3);
      }

      this.func_96513_c(var1);
   }

   public void addPlayerToTeam(String var1, ScorePlayerTeam var2) {
      if (this.getPlayersTeam(var1) != null) {
         this.removePlayerFromTeams(var1);
      }

      this.teamMemberships.put(var1, var2);
      var2.getMembershipCollection().add(var1);
   }

   public boolean removePlayerFromTeams(String var1) {
      ScorePlayerTeam var2 = this.getPlayersTeam(var1);
      if (var2 != null) {
         this.removePlayerFromTeam(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   public void removePlayerFromTeam(String var1, ScorePlayerTeam var2) {
      if (this.getPlayersTeam(var1) != var2) {
         throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + var2.func_96661_b() + "'.");
      } else {
         this.teamMemberships.remove(var1);
         var2.getMembershipCollection().remove(var1);
      }
   }

   public Collection func_96531_f() {
      return this.field_96542_e.keySet();
   }

   public Collection func_96525_g() {
      return this.field_96542_e.values();
   }

   public ScorePlayerTeam getPlayersTeam(String var1) {
      return (ScorePlayerTeam)this.teamMemberships.get(var1);
   }

   public void func_96522_a(ScoreObjective var1) {
   }

   public void func_96532_b(ScoreObjective var1) {
   }

   public void func_96533_c(ScoreObjective var1) {
   }

   public void func_96536_a(Score var1) {
   }

   public void func_96516_a(String var1) {
   }

   public void func_96523_a(ScorePlayerTeam var1) {
   }

   public void func_96538_b(ScorePlayerTeam var1) {
   }

   public void func_96513_c(ScorePlayerTeam var1) {
   }

   public static String getObjectiveDisplaySlot(int var0) {
      switch (var0) {
         case 0:
            return "list";
         case 1:
            return "sidebar";
         case 2:
            return "belowName";
         default:
            return null;
      }
   }

   public static int getObjectiveDisplaySlotNumber(String var0) {
      if (var0.equalsIgnoreCase("list")) {
         return 0;
      } else if (var0.equalsIgnoreCase("sidebar")) {
         return 1;
      } else {
         return var0.equalsIgnoreCase("belowName") ? 2 : -1;
      }
   }
}
