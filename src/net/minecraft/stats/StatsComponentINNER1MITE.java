package net.minecraft.stats;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class StatsComponentINNER1MITE implements ActionListener {
   final StatsComponentMITE field_120030_a;

   StatsComponentINNER1MITE(StatsComponentMITE par1StatsComponent) {
      this.field_120030_a = par1StatsComponent;
   }

   public void actionPerformed(ActionEvent par1ActionEvent) {
      StatsComponentMITE.func_120033_a(this.field_120030_a);
   }
}
