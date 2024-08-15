package net.minecraft.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;

class MinecraftServerGuiINNER2MITE implements ActionListener {
   final JTextField field_120025_a;
   final MinecraftServerGuiMITE field_120024_b;

   MinecraftServerGuiINNER2MITE(MinecraftServerGuiMITE par1MinecraftServerGui, JTextField par2JTextField) {
      this.field_120024_b = par1MinecraftServerGui;
      this.field_120025_a = par2JTextField;
   }

   public void actionPerformed(ActionEvent par1ActionEvent) {
      String var2 = this.field_120025_a.getText().trim();
      if (var2.length() > 0) {
         MinecraftServerGuiMITE.func_120017_a(this.field_120024_b).addPendingCommand(var2, MinecraftServer.getServer(), false);
      }

      this.field_120025_a.setText("");
   }
}
