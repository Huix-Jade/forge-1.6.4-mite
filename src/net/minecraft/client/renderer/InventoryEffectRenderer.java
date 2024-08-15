package net.minecraft.client.renderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Curse;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumInsulinResistanceLevel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Translator;
import org.lwjgl.opengl.GL11;

public abstract class InventoryEffectRenderer extends GuiContainer {
   private boolean field_74222_o;
   private int initial_tick;
   private static final ResourceLocation sugar_icon = new ResourceLocation("textures/items/sugar.png");

   public InventoryEffectRenderer(Container par1Container) {
      super(par1Container);
   }

   public void initGui() {
      super.initGui();
      if (!this.mc.thePlayer.getActivePotionEffects().isEmpty() || this.mc.thePlayer.isMalnourished() || this.mc.thePlayer.isInsulinResistant() || this.mc.thePlayer.is_cursed) {
         this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
         this.field_74222_o = true;
      }

      this.initial_tick = (int)this.mc.theWorld.getTotalWorldTime();
   }

   private void drawMalnourishedBoxTooltip(int mouse_x, int mouse_y) {
      int malnourished_box_left = this.guiLeft - 128;
      int malnourished_box_top = this.guiTop;
      int malnourished_box_right = malnourished_box_left + 123;
      int malnourished_box_bottom = malnourished_box_top + 31;
      if (this.mc.thePlayer.isMalnourished() && mouse_x >= malnourished_box_left && mouse_x <= malnourished_box_right && mouse_y >= malnourished_box_top && mouse_y <= malnourished_box_bottom) {
         List list = new ArrayList();
         Translator.addToList(EnumChatFormatting.GRAY, "effect.malnourished.general", list);
         list.add(EnumChatFormatting.GRAY + "");
         Translator.addToList(EnumChatFormatting.GRAY, "effect.malnourished." + (this.mc.thePlayer.is_malnourished_in_protein ? "protein" : "phytonutrients"), list);
         this.func_102021_a(list, mouse_x, mouse_y, false);
      }

   }

   private void drawInsulinResistantBoxTooltip(int mouse_x, int mouse_y) {
      int box_left = this.guiLeft - 128;
      int box_top = this.guiTop;
      int box_right = box_left + 123;
      int box_bottom = box_top + 31;
      if (this.mc.thePlayer.isMalnourished()) {
         box_top += 33;
         box_bottom += 33;
      }

      if (this.mc.thePlayer.isInsulinResistant() && mouse_x >= box_left && mouse_x <= box_right && mouse_y >= box_top && mouse_y <= box_bottom) {
         List list = new ArrayList();
         EnumInsulinResistanceLevel insulin_resistance_level = this.mc.thePlayer.getInsulinResistanceLevel();
         Translator.addToList(EnumChatFormatting.GRAY, "effect.insulinResistance." + insulin_resistance_level.getUnlocalizedName(), list);
         this.func_102021_a(list, mouse_x, mouse_y, false);
      }

   }

   private void drawCurseBoxTooltip(int mouse_x, int mouse_y) {
      int cursed_box_left = this.guiLeft - 128;
      int cursed_box_top = this.guiTop;
      int cursed_box_right = cursed_box_left + 123;
      int cursed_box_bottom = cursed_box_top + 31;
      if (this.mc.thePlayer.isMalnourished()) {
         cursed_box_top += 33;
         cursed_box_bottom += 33;
      }

      if (this.mc.thePlayer.isInsulinResistant()) {
         cursed_box_top += 33;
         cursed_box_bottom += 33;
      }

      if (this.mc.thePlayer.is_cursed && mouse_x >= cursed_box_left && mouse_x <= cursed_box_right && mouse_y >= cursed_box_top && mouse_y <= cursed_box_bottom) {
         Curse curse = Curse.cursesList[this.mc.thePlayer.curse_id];
         if (this.mc.thePlayer.curse_effect_known) {
            List list = new ArrayList();
            ItemStack.addTooltipsToList(EnumChatFormatting.GRAY, curse.getTooltip(), list);
            this.func_102021_a(list, mouse_x, mouse_y, false);
         }
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      super.drawScreen(par1, par2, par3);
      if (this.field_74222_o) {
         this.displayDebuffEffects();
      }

      this.drawMalnourishedBoxTooltip(par1, par2);
      this.drawInsulinResistantBoxTooltip(par1, par2);
      this.drawCurseBoxTooltip(par1, par2);
   }

   private void displayDebuffEffects() {
      int var1 = this.guiLeft - 128;
      int var2 = this.guiTop;
      boolean var3 = true;
      Collection var4 = this.mc.thePlayer.getActivePotionEffects();
      int num_effects = var4.size();
      if (this.mc.thePlayer.isMalnourished()) {
         ++num_effects;
      }

      if (this.mc.thePlayer.isInsulinResistant()) {
         ++num_effects;
      }

      if (this.mc.thePlayer.is_cursed) {
         ++num_effects;
      }

      if (num_effects > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glDisable(2896);
         int var5 = 33;
         if (num_effects > 5) {
            var5 = 132 / (num_effects - 1);
         }

         String var11;
         String var10;
         TextureManager var10000;
         GuiIngame var10001;
         if (this.mc.thePlayer.isMalnourished()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(field_110408_a);
            this.drawTexturedModalRect(var1, var2, 0, 166, 140, 32);
            var10000 = this.mc.getTextureManager();
            var10001 = this.mc.ingameGUI;
            var10000.bindTexture(GuiIngame.MITE_icons);
            this.drawTexturedModalRect(var1 + 6, var2 + 7, 18, 198, 18, 18);
            var11 = I18n.getString("effect.malnourished");
            this.fontRenderer.drawStringWithShadow(var11, var1 + 10 + 18 - 1, var2 + 6 + 1, 16777215);
            var10 = ((int)this.mc.theWorld.getTotalWorldTime() - this.initial_tick) / 100 % 2 == 0 ? I18n.getString("effect.malnourished.slowHealing") : I18n.getString("effect.malnourished.plus50PercentHunger");
            this.fontRenderer.drawStringWithShadow(var10, var1 + 10 + 18 - 1, var2 + 6 + 10 + 1, 8355711);
            var2 += var5;
         }

         if (this.mc.thePlayer.isInsulinResistant()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(field_110408_a);
            this.drawTexturedModalRect(var1, var2, 0, 166, 140, 32);
            this.mc.getTextureManager().bindTexture(sugar_icon);
            this.drawTexturedModalRect2(var1 + 7, var2 + 8, 16, 16);
            EnumInsulinResistanceLevel insulin_resistance_level = this.mc.thePlayer.getInsulinResistanceLevel();
            GL11.glColor4f(insulin_resistance_level.getRedAsFloat(), insulin_resistance_level.getGreenAsFloat(), insulin_resistance_level.getBlueAsFloat(), 1.0F);
            var10000 = this.mc.getTextureManager();
            var10001 = this.mc.ingameGUI;
            var10000.bindTexture(GuiIngame.MITE_icons);
            this.drawTexturedModalRect(var1 + 6, var2 + 7, 54, 198, 18, 18);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            var10 = I18n.getString("effect.insulinResistance");
            this.fontRenderer.drawStringWithShadow(var10, var1 + 10 + 18 - 1, var2 + 6 + 1, 16777215);
            String s = StringUtils.ticksToElapsedTime(this.mc.thePlayer.getInsulinResistance());
            this.fontRenderer.drawStringWithShadow(s, var1 + 10 + 18 - 1, var2 + 6 + 10 + 1, 8355711);
            var2 += var5;
         }

         if (this.mc.thePlayer.is_cursed) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(field_110408_a);
            this.drawTexturedModalRect(var1, var2, 0, 166, 140, 32);
            var10000 = this.mc.getTextureManager();
            var10001 = this.mc.ingameGUI;
            var10000.bindTexture(GuiIngame.MITE_icons);
            this.drawTexturedModalRect(var1 + 6, var2 + 7, 0, 198, 18, 18);
            var11 = I18n.getString("effect.cursed");
            this.fontRenderer.drawStringWithShadow(var11, var1 + 10 + 18 - 1, var2 + 6 + 1, 16777215);
            var10 = this.mc.thePlayer.curse_effect_known ? EnumChatFormatting.DARK_PURPLE + this.mc.thePlayer.getCurse().getTitle() : Translator.get("curse.unknown");
            this.fontRenderer.drawStringWithShadow(var10, var1 + 10 + 18 - 1, var2 + 6 + 10 + 1, 8355711);
            var2 += var5;
         }

         for(Iterator var6 = this.mc.thePlayer.getActivePotionEffects().iterator(); var6.hasNext(); var2 += var5) {
            PotionEffect var7 = (PotionEffect)var6.next();
            Potion var8 = Potion.potionTypes[var7.getPotionID()];
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(field_110408_a);
            this.drawTexturedModalRect(var1, var2, 0, 166, 140, 32);
            if (var8.hasStatusIcon()) {
               int var9 = var8.getStatusIconIndex();
               this.drawTexturedModalRect(var1 + 6, var2 + 7, 0 + var9 % 8 * 18, 198 + var9 / 8 * 18, 18, 18);
            }

            String string = I18n.getString(var8.getName());
            if (var7.getAmplifier() == 1) {
               string = string + " II";
            } else if (var7.getAmplifier() == 2) {
               string = string + " III";
            } else if (var7.getAmplifier() == 3) {
               string = string + " IV";
            }

            this.fontRenderer.drawStringWithShadow(string, var1 + 10 + 18 - 1, var2 + 6 + 1, 16777215);
            String durationString = Potion.getDurationString(var7);
            this.fontRenderer.drawStringWithShadow(durationString, var1 + 10 + 18 - 1, var2 + 6 + 10 + 1, 8355711);
         }
      }

   }
}
