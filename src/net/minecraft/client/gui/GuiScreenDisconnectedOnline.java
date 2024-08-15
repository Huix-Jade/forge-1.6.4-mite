package net.minecraft.client.gui;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.resources.I18n;

public class GuiScreenDisconnectedOnline extends GuiScreen {
   private String field_98113_a;
   private String field_98111_b;
   private Object[] field_98112_c;
   private List field_98110_d;
   private final GuiScreen field_98114_n;

   public GuiScreenDisconnectedOnline(GuiScreen var1, String var2, String var3, Object... var4) {
      this.field_98114_n = var1;
      this.field_98113_a = I18n.getString(var2);
      this.field_98111_b = var3;
      this.field_98112_c = var4;
   }

   protected void keyTyped(char var1, int var2) {
   }

   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.getString("gui.back")));
      if (this.field_98112_c != null) {
         this.field_98110_d = this.fontRenderer.listFormattedStringToWidth(I18n.getStringParams(this.field_98111_b, this.field_98112_c), this.width - 50);
      } else {
         this.field_98110_d = this.fontRenderer.listFormattedStringToWidth(I18n.getString(this.field_98111_b), this.width - 50);
      }

   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.id == 0) {
         this.mc.displayGuiScreen(this.field_98114_n);
      }

   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.field_98113_a, this.width / 2, this.height / 2 - 50, 11184810);
      int var4 = this.height / 2 - 30;
      if (this.field_98110_d != null) {
         for(Iterator var5 = this.field_98110_d.iterator(); var5.hasNext(); var4 += this.fontRenderer.FONT_HEIGHT) {
            String var6 = (String)var5.next();
            this.drawCenteredString(this.fontRenderer, var6, this.width / 2, var4, 16777215);
         }
      }

      super.drawScreen(var1, var2, var3);
   }
}
