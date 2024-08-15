package net.minecraft.client.gui;

import net.minecraft.client.mco.GuiScreenConfirmationType;
import net.minecraft.client.resources.I18n;

public class GuiScreenConfirmation extends GuiScreen {
   private final GuiScreenConfirmationType field_140045_e;
   private final String field_140049_p;
   private final String field_96288_n;
   protected final GuiScreen field_140048_a;
   protected final String field_140046_b;
   protected final String field_140047_c;
   protected final int field_140044_d;

   public GuiScreenConfirmation(GuiScreen var1, GuiScreenConfirmationType var2, String var3, String var4, int var5) {
      this.field_140048_a = var1;
      this.field_140044_d = var5;
      this.field_140045_e = var2;
      this.field_140049_p = var3;
      this.field_96288_n = var4;
      this.field_140046_b = I18n.getString("gui.yes");
      this.field_140047_c = I18n.getString("gui.no");
   }

   public void initGui() {
      this.buttonList.add(new GuiSmallButton(0, this.width / 2 - 155, this.height / 6 + 112, this.field_140046_b));
      this.buttonList.add(new GuiSmallButton(1, this.width / 2 - 155 + 160, this.height / 6 + 112, this.field_140047_c));
   }

   protected void actionPerformed(GuiButton var1) {
      this.field_140048_a.confirmClicked(var1.id == 0, this.field_140044_d);
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.field_140045_e.field_140072_d, this.width / 2, 70, this.field_140045_e.field_140075_c);
      this.drawCenteredString(this.fontRenderer, this.field_140049_p, this.width / 2, 90, 16777215);
      this.drawCenteredString(this.fontRenderer, this.field_96288_n, this.width / 2, 110, 16777215);
      super.drawScreen(var1, var2, var3);
   }
}
