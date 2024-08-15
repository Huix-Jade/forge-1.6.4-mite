package net.minecraft.scoreboard;

import java.util.Comparator;

final class ScoreComparator implements Comparator {
   public int func_96659_a(Score var1, Score var2) {
      if (var1.getScorePoints() > var2.getScorePoints()) {
         return 1;
      } else {
         return var1.getScorePoints() < var2.getScorePoints() ? -1 : 0;
      }
   }

   // $FF: synthetic method
   public int compare(Object var1, Object var2) {
      return this.func_96659_a((Score)var1, (Score)var2);
   }
}
