package net.minecraft.client.gui.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepairINNER2;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCoin;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingResult;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipesArmorDyes;
import net.minecraft.mite.MITEContainerCrafting;
import net.minecraft.mite.Skill;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Translator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public abstract class GuiContainer extends GuiScreen {
   protected static final ResourceLocation field_110408_a = new ResourceLocation("textures/gui/container/inventory.png");
   protected static RenderItem itemRenderer = new RenderItem();
   protected int xSize = 176;
   protected int ySize = 166;
   public Container inventorySlots;
   protected int guiLeft;
   protected int guiTop;
   private Slot theSlot;
   private Slot clickedSlot;
   private boolean isRightMouseClick;
   private ItemStack draggedStack;
   private int field_85049_r;
   private int field_85048_s;
   private Slot returningStackDestSlot;
   private long returningStackTime;
   private ItemStack returningStack;
   private Slot field_92033_y;
   private long field_92032_z;
   protected final Set field_94077_p = new HashSet();
   protected boolean field_94076_q;
   private int field_94071_C;
   private int field_94067_D;
   private boolean field_94068_E;
   private int field_94069_F;
   private long field_94070_G;
   private Slot field_94072_H;
   private int field_94073_I;
   private boolean field_94074_J;
   private ItemStack field_94075_K;

   public GuiContainer(Container par1Container) {
      this.inventorySlots = par1Container;
      this.field_94068_E = true;
   }

   public void initGui() {
      super.initGui();
      this.mc.thePlayer.openContainer = this.inventorySlots;
      this.guiLeft = (this.width - this.xSize) / 2;
      this.guiTop = (this.height - this.ySize) / 2;
   }

   public void drawScreen(int par1, int par2, float par3) {
      this.drawDefaultBackground();
      int var4 = this.guiLeft;
      int var5 = this.guiTop;
      this.drawGuiContainerBackgroundLayer(par3, par1, par2);
      GL11.glDisable(32826);
      RenderHelper.disableStandardItemLighting();
      GL11.glDisable(2896);
      GL11.glDisable(2929);
      super.drawScreen(par1, par2, par3);
      RenderHelper.enableGUIStandardItemLighting();
      GL11.glPushMatrix();
      GL11.glTranslatef((float)var4, (float)var5, 0.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(32826);
      this.theSlot = null;
      short var6 = 240;
      short var7 = 240;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var6 / 1.0F, (float)var7 / 1.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

      int var9;
      for(int var13 = 0; var13 < this.inventorySlots.inventorySlots.size(); ++var13) {
         Slot var14 = (Slot)this.inventorySlots.inventorySlots.get(var13);
         this.drawSlotInventory(var14);
         if (this.isMouseOverSlot(var14, par1, par2) && var14.func_111238_b()) {
            this.theSlot = var14;
            GL11.glDisable(2896);
            GL11.glDisable(2929);
            int var8 = var14.xDisplayPosition;
            var9 = var14.yDisplayPosition;
            this.drawGradientRect(var8, var9, var8 + 16, var9 + 16, -2130706433, -2130706433);
            GL11.glEnable(2896);
            GL11.glEnable(2929);
         }
      }

      boolean lighting_enabled = GL11.glGetBoolean(2896);
      if (lighting_enabled) {
         GL11.glDisable(2896);
      }
      //Forge: Force lighting to be disabled as there are some issue where lighting would
      //incorrectly be applied based on items that are in the inventory.
      GL11.glDisable(GL11.GL_LIGHTING);
      this.drawGuiContainerForegroundLayer(par1, par2);
      GL11.glEnable(GL11.GL_LIGHTING);
      if (lighting_enabled) {
         GL11.glEnable(2896);
      }

      InventoryPlayer var15 = this.mc.thePlayer.inventory;
      ItemStack var16 = this.draggedStack == null ? var15.getItemStack() : this.draggedStack;
      if (var16 != null) {
         byte var18 = 8;
         var9 = this.draggedStack == null ? 8 : 16;
         String var10 = null;
         if (this.draggedStack != null && this.isRightMouseClick) {
            var16 = var16.copy();
            var16.stackSize = MathHelper.ceiling_float_int((float)var16.stackSize / 2.0F);
         } else if (this.field_94076_q && this.field_94077_p.size() > 1) {
            var16 = var16.copy();
            var16.stackSize = this.field_94069_F;
            if (var16.stackSize == 0) {
               var10 = "" + EnumChatFormatting.YELLOW + "0";
            }
         }

         this.drawItemStack(var16, par1 - var4 - var18, par2 - var5 - var9, var10);
      }

      if (this.returningStack != null) {
         float var17 = (float)(Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;
         if (var17 >= 1.0F) {
            var17 = 1.0F;
            this.returningStack = null;
         }

         var9 = this.returningStackDestSlot.xDisplayPosition - this.field_85049_r;
         int var20 = this.returningStackDestSlot.yDisplayPosition - this.field_85048_s;
         int var11 = this.field_85049_r + (int)((float)var9 * var17);
         int var12 = this.field_85048_s + (int)((float)var20 * var17);
         this.drawItemStack(this.returningStack, var11, var12, (String)null);
      }

      GL11.glPopMatrix();
      if (this.theSlot != null && this.theSlot.getHasStack() && (var15.getItemStack() == null || this.theSlot instanceof SlotCrafting)) {
         ItemStack var19 = this.theSlot.getStack();
         this.drawItemStackTooltip(var19, par1, par2, this.theSlot);
      }

      GL11.glEnable(2896);
      GL11.glEnable(2929);
      RenderHelper.enableStandardItemLighting();
   }

   private void drawItemStack(ItemStack par1ItemStack, int par2, int par3, String par4Str) {
      GL11.glTranslatef(0.0F, 0.0F, 32.0F);
      this.zLevel = 200.0F;
      FontRenderer font = null;
      if (par1ItemStack != null) font = par1ItemStack.getItem().getFontRenderer(par1ItemStack);
      if (font == null) font = fontRenderer;
      itemRenderer.renderItemAndEffectIntoGUI(font, this.mc.getTextureManager(), par1ItemStack, par2, par3);
      itemRenderer.renderItemOverlayIntoGUI(font, this.mc.getTextureManager(), par1ItemStack, par2, par3 - (this.draggedStack == null ? 0 : 8), par4Str);
      this.zLevel = 0.0F;
      itemRenderer.zLevel = 0.0F;
   }

   protected void drawItemStackTooltip(ItemStack par1ItemStack, int par2, int par3) {
      this.drawItemStackTooltip(par1ItemStack, par2, par3, (Slot)null);
   }

   protected void drawItemStackTooltip(ItemStack par1ItemStack, int par2, int par3, Slot slot) {
      List var4 = par1ItemStack.getTooltip(this.mc.thePlayer, GuiScreen.isShiftKeyDown(), slot);

      for(int var5 = 0; var5 < var4.size(); ++var5) {
         if (var5 == 0) {
            var4.set(var5, "§" + Integer.toHexString(par1ItemStack.getRarity().rarityColor) + (String)var4.get(var5));
         } else {
            var4.set(var5, EnumChatFormatting.GRAY + (String)var4.get(var5));
         }
      }

      if (slot instanceof SlotCrafting) {
         this.inventorySlots.crafting_result_shown_but_prevented = false;
         Item item = par1ItemStack.getItem();
         IRecipe recipe = ((MITEContainerCrafting)this.inventorySlots).getRecipe();
         Material material_to_check_tool_bench_hardness_against = recipe == null ? item.getHardestMetalMaterial() : recipe.getMaterialToCheckToolBenchHardnessAgainst();
         boolean upper_body_in_web = this.mc.thePlayer.isUpperBodyInWeb();
         if (material_to_check_tool_bench_hardness_against != null || upper_body_in_web) {
            List tooltips = new ArrayList();
            if (upper_body_in_web) {
               tooltips.add(EnumChatFormatting.GOLD + Translator.get("container.crafting.prevented"));
               this.inventorySlots.crafting_result_shown_but_prevented = true;
            } else if (this.inventorySlots instanceof ContainerWorkbench) {
               ContainerWorkbench container_workbench = (ContainerWorkbench)this.inventorySlots;
               Material tool_material = BlockWorkbench.getToolMaterial(container_workbench.getBlockMetadata());
               if (tool_material == null) {
                  tooltips.add(EnumChatFormatting.GOLD + Translator.get("container.crafting.needsTools"));
                  this.inventorySlots.crafting_result_shown_but_prevented = true;
               } else if (material_to_check_tool_bench_hardness_against.durability > tool_material.durability) {
                  tooltips.add(EnumChatFormatting.GOLD + Translator.get("container.crafting.needsBetterTools"));
                  this.inventorySlots.crafting_result_shown_but_prevented = true;
               }
            } else {
               tooltips.add(EnumChatFormatting.GOLD + Translator.get("container.crafting.needsToolBench"));
               this.inventorySlots.crafting_result_shown_but_prevented = true;
            }

            if (tooltips.size() > 0) {
               if (var4.size() > 1) {
                  var4.add("");
               }

               var4.addAll(tooltips);
               FontRenderer font = par1ItemStack.getItem().getFontRenderer(par1ItemStack);
               drawHoveringText(var4, par2, par3, true, (font == null ? fontRenderer : font));
               return;
            }
         }

         CraftingResult crafting_result = ((MITEContainerCrafting)slot.getContainer()).current_crafting_result;
         if (this.mc.theWorld.areSkillsEnabled() && !this.mc.thePlayer.hasSkillsForCraftingResult(crafting_result)) {
            if (var4.size() > 2) {
               var4.add("");
            }

            if (item.hasQuality() && !(crafting_result.recipe instanceof RecipesArmorDyes)) {
               var4.add(EnumChatFormatting.DARK_GRAY + Skill.getSkillsetsString(crafting_result.applicable_skillsets, false));
            } else {
               var4.add(EnumChatFormatting.DARK_GRAY + "Requires " + Skill.getSkillsetsString(crafting_result.applicable_skillsets, true));
            }
         } else if (item instanceof ItemCoin && this.mc.thePlayer.experience < ((ItemCoin)item).getExperienceValue() * par1ItemStack.stackSize) {
            var4.add(EnumChatFormatting.GOLD + Translator.get("container.crafting.requiresExperience"));
            this.inventorySlots.crafting_result_shown_but_prevented = true;
         } else if (this.inventorySlots instanceof MITEContainerCrafting && ((MITEContainerCrafting)this.inventorySlots).craft_matrix.hasDamagedItem() && !((MITEContainerCrafting)this.inventorySlots).current_crafting_result.isRepair()) {
            var4.add(EnumChatFormatting.GOLD + Translator.get("container.crafting.damagedComponent"));
            this.inventorySlots.crafting_result_shown_but_prevented = true;
         }
      }

      if (slot instanceof SlotCrafting || slot instanceof ContainerRepairINNER2) {
         if (this.inventorySlots.repair_fail_condition == 1) {
            var4.add("");
            var4.add(EnumChatFormatting.GOLD + Translator.get("container.repair.insufficientSkill"));
         } else if (this.inventorySlots.repair_fail_condition == 2) {
            var4.add("");
            var4.add(EnumChatFormatting.GOLD + Translator.get("container.repair.needsHarderAnvil"));
         }
      }

      this.func_102021_a(var4, par2, par3);
   }

   protected void drawCreativeTabHoveringText(String par1Str, int par2, int par3) {
      this.func_102021_a(Arrays.asList(par1Str), par2, par3);
   }


   protected void func_102021_a(List par1List, int par2, int par3)
   {
      drawHoveringText(par1List, par2, par3, true,fontRenderer);
   }

   protected void drawHoveringText(List par1List, int par2, int par3, boolean has_title, FontRenderer font) {
      if (!par1List.isEmpty()) {
         GL11.glDisable(32826);
         RenderHelper.disableStandardItemLighting();
         GL11.glDisable(2896);
         GL11.glDisable(2929);
         int var4 = 0;
         Iterator var5 = par1List.iterator();

         int var15;
         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            var15 = this.fontRenderer.getStringWidth(var6);
            if (var15 > var4) {
               var4 = var15;
            }
         }

         int var14 = par2 + 12;
         var15 = par3 - 12;
         int var8 = 8;
         if (par1List.size() > 1) {
            var8 += 2 + (par1List.size() - 1) * 10;
         }

         if (!has_title) {
            var8 -= 2;
         }

         if (var14 + var4 > this.width) {
            var14 -= 28 + var4;
         }

         if (var15 + var8 + 6 > this.height) {
            var15 = this.height - var8 - 6;
         }

         this.zLevel = 300.0F;
         itemRenderer.zLevel = 300.0F;
         int var9 = -267386864;
         var9 = var9 & 16777215 | -369098752;
         this.drawGradientRect(var14 - 3, var15 - 4, var14 + var4 + 3, var15 - 3, var9, var9);
         this.drawGradientRect(var14 - 3, var15 + var8 + 3, var14 + var4 + 3, var15 + var8 + 4, var9, var9);
         this.drawGradientRect(var14 - 3, var15 - 3, var14 + var4 + 3, var15 + var8 + 3, var9, var9);
         this.drawGradientRect(var14 - 4, var15 - 3, var14 - 3, var15 + var8 + 3, var9, var9);
         this.drawGradientRect(var14 + var4 + 3, var15 - 3, var14 + var4 + 4, var15 + var8 + 3, var9, var9);
         int var10 = 1347420415;
         int var11 = (var10 & 16711422) >> 1 | var10 & -16777216;
         this.drawGradientRect(var14 - 3, var15 - 3 + 1, var14 - 3 + 1, var15 + var8 + 3 - 1, var10, var11);
         this.drawGradientRect(var14 + var4 + 2, var15 - 3 + 1, var14 + var4 + 3, var15 + var8 + 3 - 1, var10, var11);
         this.drawGradientRect(var14 - 3, var15 - 3, var14 + var4 + 3, var15 - 3 + 1, var10, var10);
         this.drawGradientRect(var14 - 3, var15 + var8 + 2, var14 + var4 + 3, var15 + var8 + 3, var11, var11);

         for(int var12 = 0; var12 < par1List.size(); ++var12) {
            String var13 = (String)par1List.get(var12);
            this.fontRenderer.drawStringWithShadow(var13, var14, var15, -1);
            if (var12 == 0 && has_title) {
               var15 += 2;
            }

            var15 += 10;
         }

         this.zLevel = 0.0F;
         itemRenderer.zLevel = 0.0F;
         GL11.glEnable(2896);
         GL11.glEnable(2929);
         RenderHelper.enableStandardItemLighting();
         GL11.glEnable(32826);
      }

   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
   }

   protected abstract void drawGuiContainerBackgroundLayer(float var1, int var2, int var3);

   private void drawSlotInventory(Slot par1Slot) {
      int var2 = par1Slot.xDisplayPosition;
      int var3 = par1Slot.yDisplayPosition;
      ItemStack var4 = par1Slot.getStack();
      boolean var5 = false;
      boolean var6 = par1Slot == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
      ItemStack var7 = this.mc.thePlayer.inventory.getItemStack();
      String var8 = null;
      if (par1Slot == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && var4 != null) {
         var4 = var4.copy();
         var4.stackSize /= 2;
      } else if (this.field_94076_q && this.field_94077_p.contains(par1Slot) && var7 != null) {
         if (this.field_94077_p.size() == 1) {
            return;
         }

         if (Container.func_94527_a(par1Slot, var7, true) && this.inventorySlots.canDragIntoSlot(par1Slot)) {
            var4 = var7.copy();
            var5 = true;
            Container.func_94525_a(this.field_94077_p, this.field_94071_C, var4, par1Slot.getStack() == null ? 0 : par1Slot.getStack().stackSize);
            if (var4.stackSize > var4.getMaxStackSize()) {
               var8 = EnumChatFormatting.YELLOW + "" + var4.getMaxStackSize();
               var4.stackSize = var4.getMaxStackSize();
            }

            if (var4.stackSize > par1Slot.getSlotStackLimit()) {
               var8 = EnumChatFormatting.YELLOW + "" + par1Slot.getSlotStackLimit();
               var4.stackSize = par1Slot.getSlotStackLimit();
            }
         } else {
            this.field_94077_p.remove(par1Slot);
            this.func_94066_g();
         }
      }

      this.zLevel = 100.0F;
      itemRenderer.zLevel = 100.0F;
      if (var4 == null) {
         Icon var9 = par1Slot.getBackgroundIconIndex();
         if (var9 != null) {
            GL11.glDisable(2896);
            this.mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
            this.drawTexturedModelRectFromIcon(var2, var3, var9, 16, 16);
            GL11.glEnable(2896);
            var6 = true;
         }
      }

      if (!var6) {
         if (var5) {
            drawRect(var2, var3, var2 + 16, var3 + 16, -2130706433);
         }

         GL11.glEnable(2929);
         itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.getTextureManager(), var4, var2, var3);
         itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.getTextureManager(), var4, var2, var3, var8);
      }

      itemRenderer.zLevel = 0.0F;
      this.zLevel = 0.0F;
   }

   private void func_94066_g() {
      ItemStack var1 = this.mc.thePlayer.inventory.getItemStack();
      if (var1 != null && this.field_94076_q) {
         this.field_94069_F = var1.stackSize;

         ItemStack var4;
         int var5;
         for(Iterator var2 = this.field_94077_p.iterator(); var2.hasNext(); this.field_94069_F -= var4.stackSize - var5) {
            Slot var3 = (Slot)var2.next();
            var4 = var1.copy();
            var5 = var3.getStack() == null ? 0 : var3.getStack().stackSize;
            Container.func_94525_a(this.field_94077_p, this.field_94071_C, var4, var5);
            if (var4.stackSize > var4.getMaxStackSize()) {
               var4.stackSize = var4.getMaxStackSize();
            }

            if (var4.stackSize > var3.getSlotStackLimit()) {
               var4.stackSize = var3.getSlotStackLimit();
            }
         }
      }

   }

   private Slot getSlotAtPosition(int par1, int par2) {
      for(int var3 = 0; var3 < this.inventorySlots.inventorySlots.size(); ++var3) {
         Slot var4 = (Slot)this.inventorySlots.inventorySlots.get(var3);
         if (this.isMouseOverSlot(var4, par1, par2)) {
            return var4;
         }
      }

      return null;
   }

   protected void mouseClicked(int par1, int par2, int par3) {
      super.mouseClicked(par1, par2, par3);
      boolean var4 = par3 == this.mc.gameSettings.keyBindPickBlock.keyCode + 100;
      Slot var5 = this.getSlotAtPosition(par1, par2);
      long var6 = Minecraft.getSystemTime();
      this.field_94074_J = this.field_94072_H == var5 && var6 - this.field_94070_G < 250L && this.field_94073_I == par3;
      this.field_94068_E = false;
      if (par3 == 0 || par3 == 1 || var4) {
         int var8 = this.guiLeft;
         int var9 = this.guiTop;
         boolean var10 = par1 < var8 || par2 < var9 || par1 >= var8 + this.xSize || par2 >= var9 + this.ySize;
         int var11 = -1;
         if (var5 != null) {
            var11 = var5.slotNumber;
         }

         if (var10) {
            var11 = -999;
         }

         if (this.mc.gameSettings.touchscreen && var10 && this.mc.thePlayer.inventory.getItemStack() == null) {
            this.mc.displayGuiScreen((GuiScreen)null);
            return;
         }

         if (var11 != -1) {
            if (this.mc.gameSettings.touchscreen) {
               if (var5 != null && var5.getHasStack()) {
                  this.clickedSlot = var5;
                  this.draggedStack = null;
                  this.isRightMouseClick = par3 == 1;
               } else {
                  this.clickedSlot = null;
               }
            } else if (!this.field_94076_q) {
               if (this.mc.thePlayer.inventory.getItemStack() == null) {
                  if (par3 == this.mc.gameSettings.keyBindPickBlock.keyCode + 100) {
                     this.handleMouseClick(var5, var11, par3, 3);
                  } else {
                     boolean var12 = var11 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                     byte var13 = 0;
                     if (var12) {
                        this.field_94075_K = var5 != null && var5.getHasStack() ? var5.getStack() : null;
                        var13 = 1;
                     } else if (var11 == -999) {
                        var13 = 4;
                     }

                     this.handleMouseClick(var5, var11, par3, var13);
                  }

                  this.field_94068_E = true;
               } else {
                  this.field_94076_q = true;
                  this.field_94067_D = par3;
                  this.field_94077_p.clear();
                  if (par3 == 0) {
                     this.field_94071_C = 0;
                  } else if (par3 == 1) {
                     this.field_94071_C = 1;
                  }
               }
            }
         }
      }

      this.field_94072_H = var5;
      this.field_94070_G = var6;
      this.field_94073_I = par3;
   }

   protected void mouseClickMove(int par1, int par2, int par3, long par4) {
      Slot var6 = this.getSlotAtPosition(par1, par2);
      ItemStack var7 = this.mc.thePlayer.inventory.getItemStack();
      if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
         if (par3 == 0 || par3 == 1) {
            if (this.draggedStack == null) {
               if (var6 != this.clickedSlot) {
                  this.draggedStack = this.clickedSlot.getStack().copy();
               }
            } else if (this.draggedStack.stackSize > 1 && var6 != null && Container.func_94527_a(var6, this.draggedStack, false)) {
               long var8 = Minecraft.getSystemTime();
               if (this.field_92033_y == var6) {
                  if (var8 - this.field_92032_z > 500L) {
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                     this.handleMouseClick(var6, var6.slotNumber, 1, 0);
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                     this.field_92032_z = var8 + 750L;
                     --this.draggedStack.stackSize;
                  }
               } else {
                  this.field_92033_y = var6;
                  this.field_92032_z = var8;
               }
            }
         }
      } else if (this.field_94076_q && var6 != null && var7 != null && var7.stackSize > this.field_94077_p.size() && Container.func_94527_a(var6, var7, true) && var6.isItemValid(var7) && this.inventorySlots.canDragIntoSlot(var6)) {
         this.field_94077_p.add(var6);
         this.func_94066_g();
      }

   }

   protected void mouseMovedOrUp(int par1, int par2, int par3) {
      Slot var4 = this.getSlotAtPosition(par1, par2);
      int var5 = this.guiLeft;
      int var6 = this.guiTop;
      boolean var7 = par1 < var5 || par2 < var6 || par1 >= var5 + this.xSize || par2 >= var6 + this.ySize;
      int var8 = -1;
      if (var4 != null) {
         var8 = var4.slotNumber;
      }

      if (var7) {
         var8 = -999;
      }

      Slot var10;
      Iterator var11;
      if (this.field_94074_J && var4 != null && par3 == 0 && this.inventorySlots.func_94530_a((ItemStack)null, var4)) {
         if (isShiftKeyDown()) {
            if (var4 != null && var4.inventory != null && this.field_94075_K != null) {
               var11 = this.inventorySlots.inventorySlots.iterator();

               while(var11.hasNext()) {
                  var10 = (Slot)var11.next();
                  if (var10 != null && var10.canTakeStack(this.mc.thePlayer) && var10.getHasStack() && var10.inventory == var4.inventory && Container.func_94527_a(var10, this.field_94075_K, true)) {
                     this.handleMouseClick(var10, var10.slotNumber, par3, 1);
                  }
               }
            }
         } else {
            this.handleMouseClick(var4, var8, par3, 6);
         }

         this.field_94074_J = false;
         this.field_94070_G = 0L;
      } else {
         if (this.field_94076_q && this.field_94067_D != par3) {
            this.field_94076_q = false;
            this.field_94077_p.clear();
            this.field_94068_E = true;
            return;
         }

         if (this.field_94068_E) {
            this.field_94068_E = false;
            return;
         }

         boolean var9;
         if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
            if (par3 == 0 || par3 == 1) {
               if (this.draggedStack == null && var4 != this.clickedSlot) {
                  this.draggedStack = this.clickedSlot.getStack();
               }

               var9 = Container.func_94527_a(var4, this.draggedStack, false);
               if (var8 != -1 && this.draggedStack != null && var9) {
                  this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, par3, 0);
                  this.handleMouseClick(var4, var8, 0, 0);
                  if (this.mc.thePlayer.inventory.getItemStack() != null) {
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, par3, 0);
                     this.field_85049_r = par1 - var5;
                     this.field_85048_s = par2 - var6;
                     this.returningStackDestSlot = this.clickedSlot;
                     this.returningStack = this.draggedStack;
                     this.returningStackTime = Minecraft.getSystemTime();
                  } else {
                     this.returningStack = null;
                  }
               } else if (this.draggedStack != null) {
                  this.field_85049_r = par1 - var5;
                  this.field_85048_s = par2 - var6;
                  this.returningStackDestSlot = this.clickedSlot;
                  this.returningStack = this.draggedStack;
                  this.returningStackTime = Minecraft.getSystemTime();
               }

               this.draggedStack = null;
               this.clickedSlot = null;
            }
         } else if (this.field_94076_q && !this.field_94077_p.isEmpty()) {
            this.handleMouseClick((Slot)null, -999, Container.func_94534_d(0, this.field_94071_C), 5);
            var11 = this.field_94077_p.iterator();

            while(var11.hasNext()) {
               var10 = (Slot)var11.next();
               this.handleMouseClick(var10, var10.slotNumber, Container.func_94534_d(1, this.field_94071_C), 5);
            }

            this.handleMouseClick((Slot)null, -999, Container.func_94534_d(2, this.field_94071_C), 5);
         } else if (this.mc.thePlayer.inventory.getItemStack() != null) {
            if (par3 == this.mc.gameSettings.keyBindPickBlock.keyCode + 100) {
               this.handleMouseClick(var4, var8, par3, 3);
            } else {
               var9 = var8 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
               if (var9) {
                  this.field_94075_K = var4 != null && var4.getHasStack() ? var4.getStack() : null;
               }

               this.handleMouseClick(var4, var8, par3, var9 ? 1 : 0);
            }
         }
      }

      if (this.mc.thePlayer.inventory.getItemStack() == null) {
         this.field_94070_G = 0L;
      }

      this.field_94076_q = false;
   }

   public boolean isMouseOverSlot(Slot par1Slot, int par2, int par3) {
      return this.isPointInRegion(par1Slot.xDisplayPosition, par1Slot.yDisplayPosition, 16, 16, par2, par3);
   }

   protected boolean isPointInRegion(int par1, int par2, int par3, int par4, int par5, int par6) {
      int var7 = this.guiLeft;
      int var8 = this.guiTop;
      par5 -= var7;
      par6 -= var8;
      return par5 >= par1 - 1 && par5 < par1 + par3 + 1 && par6 >= par2 - 1 && par6 < par2 + par4 + 1;
   }

   protected void handleMouseClick(Slot par1Slot, int par2, int par3, int par4) {
      if (par1Slot != null) {
         par2 = par1Slot.slotNumber;
      }

      this.mc.playerController.windowClick(this.inventorySlots.windowId, par2, par3, par4, this.mc.thePlayer);
   }

   protected void keyTyped(char par1, int par2) {
      if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.keyCode) {
         this.mc.thePlayer.closeScreen();
      }

      this.checkHotbarKeys(par2);
      if (this.theSlot != null && this.theSlot.getHasStack()) {
         if (par2 == this.mc.gameSettings.keyBindPickBlock.keyCode) {
            this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, 3);
         } else if (par2 == this.mc.gameSettings.keyBindDrop.keyCode) {
            this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, 4);
         }
      }

   }

   protected boolean checkHotbarKeys(int par1) {
      if (this.mc.thePlayer.inventory.getItemStack() == null && this.theSlot != null) {
         for(int var2 = 0; var2 < 9; ++var2) {
            if (par1 == 2 + var2) {
               this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, var2, 2);
               return true;
            }
         }
      }

      return false;
   }

   public void onGuiClosed() {
      if (this.mc.thePlayer != null) {
         this.inventorySlots.onContainerClosed(this.mc.thePlayer);
         if (this instanceof GuiMerchant || this instanceof GuiScreenHorseInventory) {
            Minecraft.disable_clicks_until = System.currentTimeMillis() + 1000L;
         }
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void updateScreen() {
      super.updateScreen();
      if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead) {
         this.mc.thePlayer.closeScreen();
      }

   }

   public Slot getSlotThatMouseIsOver(int mouse_x, int mouse_y) {
      for(int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i) {
         if (this.isMouseOverSlot(this.inventorySlots.getSlot(i), mouse_x, mouse_y)) {
            return this.inventorySlots.getSlot(i);
         }
      }

      return null;
   }
}
