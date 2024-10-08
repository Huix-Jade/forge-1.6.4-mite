package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public class GuiEnchantment extends GuiContainer {
   private static final ResourceLocation enchantingTableGuiTextures = new ResourceLocation("textures/gui/container/enchanting_table.png");
   private static final ResourceLocation enchantingTableBookTextures = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private static final ModelBook bookModel = new ModelBook();
   private Random rand = new Random();
   private ContainerEnchantment containerEnchantment;
   public int field_74214_o;
   public float field_74213_p;
   public float field_74212_q;
   public float field_74211_r;
   public float field_74210_s;
   public float field_74209_t;
   public float field_74208_u;
   ItemStack theItemStack;
   private String field_94079_C;

   public GuiEnchantment(EntityPlayer player, World par2World, int par3, int par4, int par5, String par6Str) {
      super(new ContainerEnchantment(player, par2World, par3, par4, par5));
      this.containerEnchantment = (ContainerEnchantment)this.inventorySlots;
      this.field_94079_C = par6Str;
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      this.fontRenderer.drawString(this.field_94079_C == null ? I18n.getString("container.enchant") : this.field_94079_C, 12, 6, 2631720);
      this.fontRenderer.drawString(I18n.getString("container.inventory"), 7, this.ySize - 96 + 3, 2631720);
   }

   public void updateScreen() {
      super.updateScreen();
      this.func_74205_h();
   }

   protected void mouseClicked(int par1, int par2, int par3) {
      super.mouseClicked(par1, par2, par3);
      if (!this.mc.theWorld.areSkillsEnabled() || this.mc.thePlayer.hasSkill(Skill.ENCHANTING)) {
         int var4 = (this.width - this.xSize) / 2;
         int var5 = (this.height - this.ySize) / 2;

         for(int var6 = 0; var6 < 3; ++var6) {
            int var7 = par1 - (var4 + 60);
            int var8 = par2 - (var5 + 14 + 19 * var6);
            if (var7 >= 0 && var8 >= 0 && var7 < 108 && var8 < 19 && this.containerEnchantment.enchantItem(this.mc.thePlayer, var6)) {
               this.mc.playerController.sendEnchantPacket(this.containerEnchantment.windowId, var6);
            }
         }

      }
   }

   protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(enchantingTableGuiTextures);
      int var4 = (this.width - this.xSize) / 2;
      int var5 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
      GL11.glPushMatrix();
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      ScaledResolution var6 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
      GL11.glViewport((var6.getScaledWidth() - 320) / 2 * var6.getScaleFactor(), (var6.getScaledHeight() - 240) / 2 * var6.getScaleFactor(), 320 * var6.getScaleFactor(), 240 * var6.getScaleFactor());
      GL11.glTranslatef(-0.34F, 0.23F, 0.0F);
      Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
      float var7 = 1.0F;
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      RenderHelper.enableStandardItemLighting();
      GL11.glTranslatef(0.0F, 3.3F, -16.0F);
      GL11.glScalef(var7, var7, var7);
      float var8 = 5.0F;
      GL11.glScalef(var8, var8, var8);
      GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(enchantingTableBookTextures);
      GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
      float var9 = this.field_74208_u + (this.field_74209_t - this.field_74208_u) * par1;
      GL11.glTranslatef((1.0F - var9) * 0.2F, (1.0F - var9) * 0.1F, (1.0F - var9) * 0.25F);
      GL11.glRotatef(-(1.0F - var9) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      float var10 = this.field_74212_q + (this.field_74213_p - this.field_74212_q) * par1 + 0.25F;
      float var11 = this.field_74212_q + (this.field_74213_p - this.field_74212_q) * par1 + 0.75F;
      var10 = (var10 - (float)MathHelper.truncateDoubleToInt((double)var10)) * 1.6F - 0.3F;
      var11 = (var11 - (float)MathHelper.truncateDoubleToInt((double)var11)) * 1.6F - 0.3F;
      if (var10 < 0.0F) {
         var10 = 0.0F;
      }

      if (var11 < 0.0F) {
         var11 = 0.0F;
      }

      if (var10 > 1.0F) {
         var10 = 1.0F;
      }

      if (var11 > 1.0F) {
         var11 = 1.0F;
      }

      GL11.glEnable(32826);
      bookModel.render((Entity)null, 0.0F, var10, var11, var9, 0.0F, 0.0625F);
      GL11.glDisable(32826);
      RenderHelper.disableStandardItemLighting();
      GL11.glMatrixMode(5889);
      GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      RenderHelper.disableStandardItemLighting();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNameParts.instance.setRandSeed(this.containerEnchantment.nameSeed);
      List tooltips = null;

      for(int var12 = 0; var12 < 3; ++var12) {
         String var13 = EnchantmentNameParts.instance.generateRandomEnchantName();
         this.zLevel = 0.0F;
         this.mc.getTextureManager().bindTexture(enchantingTableGuiTextures);
         int var14 = this.containerEnchantment.enchantLevels[var12];
         int experience_cost = Enchantment.getExperienceCost(var14);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (var14 == 0) {
            this.drawTexturedModalRect(var4 + 60, var5 + 14 + 19 * var12, 0, 185, 108, 19);
         } else {
            String var15 = "" + experience_cost;
            FontRenderer var16 = this.mc.standardGalacticFontRenderer;
            int var17 = 6839882;
            if (this.mc.thePlayer.experience < experience_cost && !this.mc.thePlayer.capabilities.isCreativeMode) {
               this.drawTexturedModalRect(var4 + 60, var5 + 14 + 19 * var12, 0, 185, 108, 19);
               var16.drawSplitString(var13, var4 + 62, var5 + 16 + 19 * var12, 104, (var17 & 16711422) >> 1);
               var16 = this.mc.fontRenderer;
               var17 = 4226832;
               var16.drawStringWithShadow(var15, var4 + 62 + 104 - var16.getStringWidth(var15), var5 + 16 + 19 * var12 + 7, var17);
            } else {
               int var18 = par2 - (var4 + 60);
               int var19 = par3 - (var5 + 14 + 19 * var12);
               if (var18 >= 0 && var19 >= 0 && var18 < 108 && var19 < 19) {
                  this.drawTexturedModalRect(var4 + 60, var5 + 14 + 19 * var12, 0, 204, 108, 19);
                  var17 = 16777088;
                  if (this.mc.theWorld.areSkillsEnabled() && !this.mc.thePlayer.hasSkill(Skill.ENCHANTING)) {
                     tooltips = new ArrayList();
                     tooltips.add(EnumChatFormatting.GRAY + "Requires " + Skill.ENCHANTING.getLocalizedName(true));
                  }
               } else {
                  this.drawTexturedModalRect(var4 + 60, var5 + 14 + 19 * var12, 0, 166, 108, 19);
               }

               var16.drawSplitString(var13, var4 + 62, var5 + 16 + 19 * var12, 104, var17);
               var16 = this.mc.fontRenderer;
               var17 = 8453920;
               var16.drawStringWithShadow(var15, var4 + 62 + 104 - var16.getStringWidth(var15), var5 + 16 + 19 * var12 + 7, var17);
            }
         }
      }

      if (tooltips != null) {
         this.func_102021_a(tooltips, par2, par3);
      }

   }

   public void func_74205_h() {
      ItemStack var1 = this.inventorySlots.getSlot(0).getStack();
      if (!ItemStack.areItemStacksEqual(var1, this.theItemStack)) {
         this.theItemStack = var1;

         do {
            this.field_74211_r += (float)(this.rand.nextInt(4) - this.rand.nextInt(4));
         } while(this.field_74213_p <= this.field_74211_r + 1.0F && this.field_74213_p >= this.field_74211_r - 1.0F);
      }

      ++this.field_74214_o;
      this.field_74212_q = this.field_74213_p;
      this.field_74208_u = this.field_74209_t;
      boolean var2 = false;

      for(int var3 = 0; var3 < 3; ++var3) {
         if (this.containerEnchantment.enchantLevels[var3] != 0) {
            var2 = true;
         }
      }

      if (var2) {
         this.field_74209_t += 0.2F;
      } else {
         this.field_74209_t -= 0.2F;
      }

      if (this.field_74209_t < 0.0F) {
         this.field_74209_t = 0.0F;
      }

      if (this.field_74209_t > 1.0F) {
         this.field_74209_t = 1.0F;
      }

      float var5 = (this.field_74211_r - this.field_74213_p) * 0.4F;
      float var4 = 0.2F;
      if (var5 < -var4) {
         var5 = -var4;
      }

      if (var5 > var4) {
         var5 = var4;
      }

      this.field_74210_s += (var5 - this.field_74210_s) * 0.9F;
      this.field_74213_p += this.field_74210_s;
   }
}
