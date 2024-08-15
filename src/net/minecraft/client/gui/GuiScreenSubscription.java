package net.minecraft.client.gui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.mco.ValueObjectSubscription;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiScreenSubscription extends GuiScreen {
   private final GuiScreen field_98067_a;
   private final McoServer field_98065_b;
   private final int field_98066_c = 0;
   private final int field_98064_d = 1;
   private int field_98068_n;
   private String field_98069_o;

   public GuiScreenSubscription(GuiScreen var1, McoServer var2) {
      this.field_98067_a = var1;
      this.field_98065_b = var2;
   }

   public void updateScreen() {
   }

   public void initGui() {
      this.func_98063_a(this.field_98065_b.field_96408_a);
      Keyboard.enableRepeatEvents(true);
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.getString("gui.cancel")));
   }

   private void func_98063_a(long var1) {
      McoClient var3 = new McoClient(this.mc.getSession());

      try {
         ValueObjectSubscription var4 = var3.func_98177_f(var1);
         this.field_98068_n = var4.field_98170_b;
         this.field_98069_o = this.func_98062_b(var4.field_98171_a);
      } catch (ExceptionMcoService var5) {
         Minecraft.getMinecraft().getLogAgent().logSevere(var5.toString());
      } catch (IOException var6) {
         Minecraft.getMinecraft().getLogAgent().logWarning("Realms: could not parse response");
      }

   }

   private String func_98062_b(long var1) {
      GregorianCalendar var3 = new GregorianCalendar(TimeZone.getDefault());
      var3.setTimeInMillis(var1);
      return SimpleDateFormat.getDateTimeInstance().format(var3.getTime());
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id == 0) {
            this.mc.displayGuiScreen(this.field_98067_a);
         } else if (var1.id == 1) {
         }

      }
   }

   protected void keyTyped(char var1, int var2) {
   }

   protected void mouseClicked(int var1, int var2, int var3) {
      super.mouseClicked(var1, var2, var3);
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.getString("mco.configure.world.subscription.title"), this.width / 2, 17, 16777215);
      this.drawString(this.fontRenderer, I18n.getString("mco.configure.world.subscription.start"), this.width / 2 - 100, 53, 10526880);
      this.drawString(this.fontRenderer, this.field_98069_o, this.width / 2 - 100, 66, 16777215);
      this.drawString(this.fontRenderer, I18n.getString("mco.configure.world.subscription.daysleft"), this.width / 2 - 100, 85, 10526880);
      this.drawString(this.fontRenderer, String.valueOf(this.field_98068_n), this.width / 2 - 100, 98, 16777215);
      super.drawScreen(var1, var2, var3);
   }
}
