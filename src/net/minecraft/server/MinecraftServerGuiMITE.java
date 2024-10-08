package net.minecraft.server;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import net.minecraft.mite.PlayerListComponentMITE;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.stats.StatsComponentMITE;

public class MinecraftServerGuiMITE extends JComponent {
   private static boolean field_120022_a;
   private DedicatedServer field_120021_b;

   public static void func_120016_a(DedicatedServer par0DedicatedServer) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception var3) {
      }

      MinecraftServerGuiMITE var1 = new MinecraftServerGuiMITE(par0DedicatedServer);
      field_120022_a = true;
      JFrame var2 = new JFrame("Minecraft server");
      var2.add(var1);
      var2.pack();
      var2.setLocationRelativeTo((Component)null);
      var2.setVisible(true);
      var2.addWindowListener(new MinecraftServerGuiINNER1MITE(par0DedicatedServer));
   }

   public MinecraftServerGuiMITE(DedicatedServer par1DedicatedServer) {
      this.field_120021_b = par1DedicatedServer;
      this.setPreferredSize(new Dimension(854, 480));
      this.setLayout(new BorderLayout());

      try {
         this.add(this.func_120018_d(), "Center");
         this.add(this.func_120019_b(), "West");
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   private JComponent func_120019_b() {
      JPanel var1 = new JPanel(new BorderLayout());
      var1.add(new StatsComponentMITE(this.field_120021_b), "North");
      var1.add(this.func_120020_c(), "Center");
      var1.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
      return var1;
   }

   private JComponent func_120020_c() {
      PlayerListComponentMITE var1 = new PlayerListComponentMITE(this.field_120021_b);
      JScrollPane var2 = new JScrollPane(var1, 22, 30);
      var2.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
      return var2;
   }

   private JComponent func_120018_d() {
      JPanel var1 = new JPanel(new BorderLayout());
      JTextArea var2 = new JTextArea();
      this.field_120021_b.getLogAgent().func_120013_a().addHandler(new TextAreaLogHandlerMITE(var2));
      JScrollPane var3 = new JScrollPane(var2, 22, 30);
      var2.setEditable(false);
      JTextField var4 = new JTextField();
      var4.addActionListener(new MinecraftServerGuiINNER2MITE(this, var4));
      var2.addFocusListener(new MinecraftServerGuiINNER3MITE(this));
      var1.add(var3, "Center");
      var1.add(var4, "South");
      var1.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
      return var1;
   }

   static DedicatedServer func_120017_a(MinecraftServerGuiMITE par0MinecraftServerGui) {
      return par0MinecraftServerGui.field_120021_b;
   }
}
