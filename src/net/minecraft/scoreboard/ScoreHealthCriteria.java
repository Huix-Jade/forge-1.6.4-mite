package net.minecraft.scoreboard;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class ScoreHealthCriteria extends ScoreDummyCriteria {
   public ScoreHealthCriteria(String var1) {
      super(var1);
   }

   public int func_96635_a(List var1) {
      float var2 = 0.0F;

      EntityPlayer var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var2 += var4.getHealth() + var4.getAbsorptionAmount()) {
         var4 = (EntityPlayer)var3.next();
      }

      if (var1.size() > 0) {
         var2 /= (float)var1.size();
      }

      return MathHelper.ceiling_float_int(var2);
   }

   public boolean isReadOnly() {
      return true;
   }
}
