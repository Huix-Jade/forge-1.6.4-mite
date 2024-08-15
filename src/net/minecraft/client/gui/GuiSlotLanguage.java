package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;

class GuiSlotLanguage extends GuiSlot {
   private final List field_77251_g;
   private final Map field_77253_h;
   // $FF: synthetic field
   final GuiLanguage languageGui;

   public GuiSlotLanguage(GuiLanguage var1) {
      super(var1.mc, var1.width, var1.height, 32, var1.height - 65 + 4, 18);
      this.languageGui = var1;
      this.field_77251_g = Lists.newArrayList();
      this.field_77253_h = Maps.newHashMap();
      Iterator var2 = GuiLanguage.func_135011_a(var1).getLanguages().iterator();

      while(var2.hasNext()) {
         Language var3 = (Language)var2.next();
         this.field_77253_h.put(var3.getLanguageCode(), var3);
         this.field_77251_g.add(var3.getLanguageCode());
      }

   }

   protected int getSize() {
      return this.field_77251_g.size();
   }

   protected void elementClicked(int var1, boolean var2) {
      Language var3 = (Language)this.field_77253_h.get(this.field_77251_g.get(var1));
      GuiLanguage.func_135011_a(this.languageGui).setCurrentLanguage(var3);
      GuiLanguage.getGameSettings(this.languageGui).language = var3.getLanguageCode();
      this.languageGui.mc.refreshResources();
      this.languageGui.fontRenderer.setUnicodeFlag(GuiLanguage.func_135011_a(this.languageGui).isCurrentLocaleUnicode());
      this.languageGui.fontRenderer.setBidiFlag(GuiLanguage.func_135011_a(this.languageGui).isCurrentLanguageBidirectional());
      GuiLanguage.getDoneButton(this.languageGui).displayString = I18n.getString("gui.done");
      GuiLanguage.getGameSettings(this.languageGui).saveOptions();
   }

   protected boolean isSelected(int var1) {
      return ((String)this.field_77251_g.get(var1)).equals(GuiLanguage.func_135011_a(this.languageGui).getCurrentLanguage().getLanguageCode());
   }

   protected int getContentHeight() {
      return this.getSize() * 18;
   }

   protected void drawBackground() {
      this.languageGui.drawDefaultBackground();
   }

   protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
      this.languageGui.fontRenderer.setBidiFlag(true);
      this.languageGui.drawCenteredString(this.languageGui.fontRenderer, ((Language)this.field_77253_h.get(this.field_77251_g.get(var1))).toString(), this.languageGui.width / 2, var3 + 1, 16777215);
      this.languageGui.fontRenderer.setBidiFlag(GuiLanguage.func_135011_a(this.languageGui).getCurrentLanguage().isBidirectional());
   }
}
