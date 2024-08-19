package net.minecraft.client.gui.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiContainerCreative extends InventoryEffectRenderer {
   private static final ResourceLocation field_110424_t = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private static InventoryBasic inventory = new InventoryBasic("tmp", true, 45);
   private static int selectedTabIndex;
   private float currentScroll;
   private boolean isScrolling;
   private boolean wasClicking;
   private GuiTextField searchField;
   private List backupContainerSlots;
   private Slot field_74235_v;
   private boolean field_74234_w;
   private CreativeCrafting field_82324_x;

   private static int tabPage = 0;
   private int maxPages = 0;

   public GuiContainerCreative(EntityPlayer par1EntityPlayer) {
      super(new ContainerCreative(par1EntityPlayer));
      par1EntityPlayer.openContainer = this.inventorySlots;
      this.allowUserInput = true;
      par1EntityPlayer.addStat(AchievementList.openInventory, 1);
      par1EntityPlayer.incrementStatForThisWorldFromClient(AchievementList.openInventory);
      this.ySize = 136;
      this.xSize = 195;
   }

   public void updateScreen() {
      if (!this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
      }

   }

   protected void handleMouseClick(Slot par1Slot, int par2, int par3, int par4) {
      this.field_74234_w = true;
      boolean var5 = par4 == 1;
      par4 = par2 == -999 && par4 == 0 ? 4 : par4;
      ItemStack itemstack;
      InventoryPlayer var11;
      if (par1Slot == null && selectedTabIndex != CreativeTabs.tabInventory.getTabIndex() && par4 != 5) {
         var11 = this.mc.thePlayer.inventory;
         if (var11.getItemStack() != null) {
            if (par3 == 0) {
               this.mc.thePlayer.dropPlayerItem(var11.getItemStack());
               this.mc.playerController.func_78752_a(var11.getItemStack());
               var11.setItemStack((ItemStack)null);
            }

            if (par3 == 1) {
               itemstack = var11.getItemStack().splitStack(1);
               this.mc.thePlayer.dropPlayerItem(itemstack);
               this.mc.playerController.func_78752_a(itemstack);
               if (var11.getItemStack().stackSize == 0) {
                  var11.setItemStack((ItemStack)null);
               }
            }
         }
      } else {
         int var10;
         if (par1Slot == this.field_74235_v && var5) {
            for(var10 = 0; var10 < this.mc.thePlayer.inventoryContainer.getInventory().size(); ++var10) {
               this.mc.playerController.sendSlotPacket((ItemStack)null, var10);
            }
         } else {
            ItemStack var6;
            if (selectedTabIndex == CreativeTabs.tabInventory.getTabIndex()) {
               if (par1Slot == this.field_74235_v) {
                  this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
               } else if (par4 == 4 && par1Slot != null && par1Slot.getHasStack()) {
                  var6 = par1Slot.decrStackSize(par3 == 0 ? 1 : par1Slot.getStack().getMaxStackSize());
                  this.mc.thePlayer.dropPlayerItem(var6);
                  this.mc.playerController.func_78752_a(var6);
               } else if (par4 == 4 && this.mc.thePlayer.inventory.getItemStack() != null) {
                  this.mc.thePlayer.dropPlayerItem(this.mc.thePlayer.inventory.getItemStack());
                  this.mc.playerController.func_78752_a(this.mc.thePlayer.inventory.getItemStack());
                  this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
               } else {
                  this.mc.thePlayer.inventoryContainer.slotClick(par1Slot == null ? par2 : SlotCreativeInventory.func_75240_a((SlotCreativeInventory)par1Slot).slotNumber, par3, par4, GuiScreen.isShiftKeyDown(), this.mc.thePlayer);
                  this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
               }
            } else if (par4 != 5 && par1Slot.inventory == inventory) {
               var11 = this.mc.thePlayer.inventory;
               itemstack = var11.getItemStack();
               ItemStack itemstack2 = par1Slot.getStack();
               ItemStack var9;
               if (par4 == 2) {
                  if (itemstack2 != null && par3 >= 0 && par3 < 9) {
                     var9 = itemstack2.copy();
                     var9.stackSize = var9.getMaxStackSize();
                     this.mc.thePlayer.inventory.setInventorySlotContents(par3, var9);
                     this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
                  }

                  return;
               }

               if (par4 == 3) {
                  if (var11.getItemStack() == null && par1Slot.getHasStack()) {
                     var9 = par1Slot.getStack().copy();
                     var9.stackSize = var9.getMaxStackSize();
                     var11.setItemStack(var9);
                  }

                  return;
               }

               if (par4 == 4) {
                  if (itemstack2 != null) {
                     var9 = itemstack2.copy();
                     var9.stackSize = par3 == 0 ? 1 : var9.getMaxStackSize();
                     this.mc.thePlayer.dropPlayerItem(var9);
                     this.mc.playerController.func_78752_a(var9);
                  }

                  return;
               }

               if (itemstack != null && itemstack2 != null
                       && itemstack.isItemStackEqual(itemstack2, true, false, false, true)
                       && ItemStack.areItemStackTagsEqual(itemstack, itemstack2)) {
                  //Forge: Bugfix, Compare NBT data, allow for deletion of enchanted books, MC-12770
                  if (par3 == 0) {
                     if (var5) {
                        itemstack.stackSize = itemstack.getMaxStackSize();
                     } else if (itemstack.stackSize < itemstack.getMaxStackSize()) {
                        ++itemstack.stackSize;
                     }
                  } else if (itemstack.stackSize <= 1) {
                     var11.setItemStack((ItemStack)null);
                  } else {
                     --itemstack.stackSize;
                  }
               } else if (itemstack2 != null && itemstack == null) {
                  var11.setItemStack(ItemStack.copyItemStack(itemstack2));
                  itemstack = var11.getItemStack();
                  if (var5) {
                     itemstack.stackSize = itemstack.getMaxStackSize();
                  }
               } else {
                  var11.setItemStack((ItemStack)null);
               }
            } else {
               this.inventorySlots.slotClick(par1Slot == null ? par2 : par1Slot.slotNumber, par3, par4, GuiScreen.isShiftKeyDown(), this.mc.thePlayer);
               if (Container.func_94532_c(par3) == 2) {
                  for(var10 = 0; var10 < 9; ++var10) {
                     this.mc.playerController.sendSlotPacket(this.inventorySlots.getSlot(45 + var10).getStack(), 36 + var10);
                  }
               } else if (par1Slot != null) {
                  var6 = this.inventorySlots.getSlot(par1Slot.slotNumber).getStack();
                  this.mc.playerController.sendSlotPacket(var6, par1Slot.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
               }
            }
         }
      }

   }

   public void initGui() {
      if (this.mc.playerController.isInCreativeMode()) {
         super.initGui();
         this.buttonList.clear();
         Keyboard.enableRepeatEvents(true);
         this.searchField = new GuiTextField(this.fontRenderer, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRenderer.FONT_HEIGHT);
         this.searchField.setMaxStringLength(15);
         this.searchField.setEnableBackgroundDrawing(false);
         this.searchField.setVisible(false);
         this.searchField.setTextColor(16777215);
         int var1 = selectedTabIndex;
         selectedTabIndex = -1;
         this.setCurrentCreativeTab(CreativeTabs.creativeTabArray[var1]);
         this.field_82324_x = new CreativeCrafting(this.mc);
         this.mc.thePlayer.inventoryContainer.addCraftingToCrafters(this.field_82324_x);
         int tabCount = CreativeTabs.creativeTabArray.length;
         if (tabCount > 12) {
            buttonList.add(new GuiButton(101, guiLeft,              guiTop - 50, 20, 20, "<"));
            buttonList.add(new GuiButton(102, guiLeft + xSize - 20, guiTop - 50, 20, 20, ">"));
            maxPages = ((tabCount - 12) / 10) + 1;
         }

      } else {
         this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
      }

   }

   public void onGuiClosed() {
      super.onGuiClosed();
      if (this.mc.thePlayer != null && this.mc.thePlayer.inventory != null) {
         this.mc.thePlayer.inventoryContainer.removeCraftingFromCrafters(this.field_82324_x);
      }

      Keyboard.enableRepeatEvents(false);
   }

   protected void keyTyped(char par1, int par2) {
      if (!CreativeTabs.creativeTabArray[selectedTabIndex].hasSearchBar()) {
         if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat)) {
            this.setCurrentCreativeTab(CreativeTabs.tabAllSearch);
         } else {
            super.keyTyped(par1, par2);
         }
      } else {
         if (this.field_74234_w) {
            this.field_74234_w = false;
            this.searchField.setText("");
         }

         if (!this.checkHotbarKeys(par2)) {
            if (this.searchField.textboxKeyTyped(par1, par2)) {
               this.updateCreativeSearch();
            } else {
               super.keyTyped(par1, par2);
            }
         }
      }

   }

   private void updateCreativeSearch() {
      ContainerCreative containercreative = (ContainerCreative)this.inventorySlots;
      containercreative.itemList.clear();
      CreativeTabs tab = CreativeTabs.creativeTabArray[selectedTabIndex];
      if (tab.hasSearchBar() && tab != CreativeTabs.tabAllSearch) {
          tab.displayAllReleventItems(containercreative.itemList);
          updateFilteredItems(containercreative);
          return;
      }

      Item[] var2 = Item.itemsList;
      int var3 = var2.length;

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         Item var5 = var2[var4];
         if (var5 != null && var5.getCreativeTab() != null) {
            var5.getSubItems(var5.itemID, CreativeTabs.tabAllSearch, containercreative.itemList);
         }
      }

      Enchantment[] var8 = Enchantment.enchantmentsList;
      var3 = var8.length;

      for(var4 = 0; var4 < var3; ++var4) {
         Enchantment var12 = var8[var4];
         if (var12 != null) {
            Item.enchantedBook.func_92113_a(var12, containercreative.itemList);
         }
      }
      updateFilteredItems(containercreative);

   }

    //split from above for custom search tabs
    private void updateFilteredItems(ContainerCreative containercreative) {
       Iterator var9 = containercreative.itemList.iterator();
       String var10 = this.searchField.getText().toLowerCase();

       while(var9.hasNext()) {
          ItemStack var11 = (ItemStack)var9.next();
          boolean var13 = false;

           for (Object o : var11.getTooltip(this.mc.thePlayer, true, null)) {
               String var7 = (String) o;
               if (var7.toLowerCase().contains(var10)) {
                   var13 = true;
                   break;
               }
           }

          if (!var13) {
             var9.remove();
          }
       }

       this.currentScroll = 0.0F;
       containercreative.scrollTo(0.0F);
    }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      CreativeTabs var3 = CreativeTabs.creativeTabArray[selectedTabIndex];
      if (var3 != null && var3.drawInForegroundOfTab()) {
         this.fontRenderer.drawString(I18n.getString(var3.getTranslatedTabLabel()), 8, 6, 4210752);
      }

   }

   protected void mouseClicked(int par1, int par2, int par3) {
      if (par3 == 0) {
         int var4 = par1 - this.guiLeft;
         int var5 = par2 - this.guiTop;
         CreativeTabs[] var6 = CreativeTabs.creativeTabArray;
         int var7 = var6.length;

          for (CreativeTabs var9 : var6) {
              if (var9 != null && func_74232_a(var9, var4, var5)) {
                  return;
              }
          }
      }

      super.mouseClicked(par1, par2, par3);
   }

   protected void mouseMovedOrUp(int par1, int par2, int par3) {
      if (par3 == 0) {
         int var4 = par1 - this.guiLeft;
         int var5 = par2 - this.guiTop;
         CreativeTabs[] var6 = CreativeTabs.creativeTabArray;

          for (CreativeTabs var9 : var6) {
              if (this.func_74232_a(var9, var4, var5)) {
                  this.setCurrentCreativeTab(var9);
                  return;
              }
          }
      }

      super.mouseMovedOrUp(par1, par2, par3);
   }

   private boolean needsScrollBars() {
      if (CreativeTabs.creativeTabArray[selectedTabIndex] == null) return false;
      return selectedTabIndex != CreativeTabs.tabInventory.getTabIndex() && CreativeTabs.creativeTabArray[selectedTabIndex].shouldHidePlayerInventory() && ((ContainerCreative)this.inventorySlots).hasMoreThan1PageOfItemsInList();
   }

   private void setCurrentCreativeTab(CreativeTabs par1CreativeTabs) {
       if (par1CreativeTabs == null) {
            return;
       }
      int var2 = selectedTabIndex;
      selectedTabIndex = par1CreativeTabs.getTabIndex();
      ContainerCreative var3 = (ContainerCreative)this.inventorySlots;
      this.field_94077_p.clear();
      var3.itemList.clear();
      par1CreativeTabs.displayAllReleventItems(var3.itemList);
      if (par1CreativeTabs == CreativeTabs.tabInventory) {
         Container var4 = this.mc.thePlayer.inventoryContainer;
         if (this.backupContainerSlots == null) {
            this.backupContainerSlots = var3.inventorySlots;
         }

         var3.inventorySlots = new ArrayList();

         for(int var5 = 0; var5 < var4.inventorySlots.size(); ++var5) {
            SlotCreativeInventory var6 = new SlotCreativeInventory(this, (Slot)var4.inventorySlots.get(var5), var5);
            var3.inventorySlots.add(var6);
            int var7;
            int var8;
            int var9;
            if (var5 >= 5 && var5 < 9) {
               var7 = var5 - 5;
               var8 = var7 / 2;
               var9 = var7 % 2;
               var6.xDisplayPosition = 9 + var8 * 54;
               var6.yDisplayPosition = 6 + var9 * 27;
            } else if (var5 >= 0 && var5 < 5) {
               var6.yDisplayPosition = -2000;
               var6.xDisplayPosition = -2000;
            } else if (var5 < var4.inventorySlots.size()) {
               var7 = var5 - 9;
               var8 = var7 % 9;
               var9 = var7 / 9;
               var6.xDisplayPosition = 9 + var8 * 18;
               if (var5 >= 36) {
                  var6.yDisplayPosition = 112;
               } else {
                  var6.yDisplayPosition = 54 + var9 * 18;
               }
            }
         }

         this.field_74235_v = new Slot(inventory, 0, 173, 112);
         var3.inventorySlots.add(this.field_74235_v);
      } else if (var2 == CreativeTabs.tabInventory.getTabIndex()) {
         var3.inventorySlots = this.backupContainerSlots;
         this.backupContainerSlots = null;
      }

      if (this.searchField != null) {
         if (par1CreativeTabs.hasSearchBar()) {
            this.searchField.setVisible(true);
            this.searchField.setCanLoseFocus(false);
            this.searchField.setFocused(true);
            this.searchField.setText("");
            this.updateCreativeSearch();
         } else {
            this.searchField.setVisible(false);
            this.searchField.setCanLoseFocus(true);
            this.searchField.setFocused(false);
         }
      }

      this.currentScroll = 0.0F;
      var3.scrollTo(0.0F);
   }

   public void handleMouseInput() {
      super.handleMouseInput();
      int var1 = Mouse.getEventDWheel();
      if (var1 != 0 && this.needsScrollBars()) {
         int var2 = ((ContainerCreative)this.inventorySlots).itemList.size() / 9 - 5 + 1;
         if (var1 > 0) {
            var1 = 1;
         }

         if (var1 < 0) {
            var1 = -1;
         }

         this.currentScroll = (float)((double)this.currentScroll - (double)var1 / (double)var2);
         if (this.currentScroll < 0.0F) {
            this.currentScroll = 0.0F;
         }

         if (this.currentScroll > 1.0F) {
            this.currentScroll = 1.0F;
         }

         ((ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      boolean var4 = Mouse.isButtonDown(0);
      int var5 = this.guiLeft;
      int var6 = this.guiTop;
      int var7 = var5 + 175;
      int var8 = var6 + 18;
      int var9 = var7 + 14;
      int var10 = var8 + 112;
      if (!this.wasClicking && var4 && par1 >= var7 && par2 >= var8 && par1 < var9 && par2 < var10) {
         this.isScrolling = this.needsScrollBars();
      }

      if (!var4) {
         this.isScrolling = false;
      }

      this.wasClicking = var4;
      if (this.isScrolling) {
         this.currentScroll = ((float)(par2 - var8) - 7.5F) / ((float)(var10 - var8) - 15.0F);
         if (this.currentScroll < 0.0F) {
            this.currentScroll = 0.0F;
         }

         if (this.currentScroll > 1.0F) {
            this.currentScroll = 1.0F;
         }

         ((ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
      }

      super.drawScreen(par1, par2, par3);
      CreativeTabs[] var11 = CreativeTabs.creativeTabArray;

      int start = tabPage * 10;
      int i2 = Math.min(var11.length, ((tabPage + 1) * 10) + 2);
      if (tabPage != 0) start += 2;
      boolean rendered = false;
      for (int j2 = start; j2 < i2; ++j2) {
         CreativeTabs var14 = var11[j2];
         if (var14 != null && this.renderCreativeInventoryHoveringText(var14, par1, par2)) {
            rendered = true;
            break;
         }
      }

      if (!rendered && !renderCreativeInventoryHoveringText(CreativeTabs.tabAllSearch, par1, par2)) {
         renderCreativeInventoryHoveringText(CreativeTabs.tabInventory, par1, par2);
      }

      if (this.field_74235_v != null && selectedTabIndex == CreativeTabs.tabInventory.getTabIndex() && this.isPointInRegion(this.field_74235_v.xDisplayPosition, this.field_74235_v.yDisplayPosition, 16, 16, par1, par2)) {
         this.drawCreativeTabHoveringText(I18n.getString("inventory.binSlot"), par1, par2);
      }

      if (maxPages != 0) {
         String page = String.format("%d / %d", tabPage + 1, maxPages + 1);
         int width = fontRenderer.getStringWidth(page);
         GL11.glDisable(GL11.GL_LIGHTING);
         this.zLevel = 300.0F;
         itemRenderer.zLevel = 300.0F;
         fontRenderer.drawString(page, guiLeft + (xSize / 2) - (width / 2), guiTop - 44, -1);
          this.zLevel = 0.0F;
         itemRenderer.zLevel = 0.0F;
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(2896);
   }

   protected void drawItemStackTooltip(ItemStack par1ItemStack, int par2, int par3) {
      if (selectedTabIndex == CreativeTabs.tabAllSearch.getTabIndex()) {
         List var4 = par1ItemStack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips, (Slot)null);
         CreativeTabs var5 = par1ItemStack.getItem().getCreativeTab();
         if (var5 == null && par1ItemStack.itemID == Item.enchantedBook.itemID) {
            Map var6 = EnchantmentHelper.getStoredEnchantmentsMap(par1ItemStack);
            if (var6.size() == 1) {
               Enchantment var7 = Enchantment.enchantmentsList[(Integer)var6.keySet().iterator().next()];
               CreativeTabs[] var8 = CreativeTabs.creativeTabArray;
               int var9 = var8.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  CreativeTabs var11 = var8[var10];
                  if (var7.isOnCreativeTab(var11)) {
                     var5 = var11;
                     break;
                  }
               }
            }
         }

         if (var5 != null) {
            var4.add(1, "" + EnumChatFormatting.BOLD + EnumChatFormatting.BLUE + I18n.getString(var5.getTranslatedTabLabel()));
         }

         for(int var12 = 0; var12 < var4.size(); ++var12) {
            if (var12 == 0) {
               var4.set(var12, "§" + Integer.toHexString(par1ItemStack.getRarity().rarityColor) + (String)var4.get(var12));
            } else {
               var4.set(var12, EnumChatFormatting.GRAY + (String)var4.get(var12));
            }
         }

         this.func_102021_a(var4, par2, par3);
      } else {
         super.drawItemStackTooltip(par1ItemStack, par2, par3);
      }

   }

   protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderHelper.enableGUIStandardItemLighting();
      CreativeTabs var4 = CreativeTabs.creativeTabArray[selectedTabIndex];
      CreativeTabs[] var5 = CreativeTabs.creativeTabArray;

      int start = tabPage * 10;
      int var6 = Math.min(var5.length, ((tabPage + 1) * 10 + 2));
      if (tabPage != 0) start += 2;

      int var7;
      for(var7 = start; var7 < var6; ++var7) {
         CreativeTabs var8 = var5[var7];
         this.mc.getTextureManager().bindTexture(field_110424_t);
         if (var8 != null && var8.getTabIndex() != selectedTabIndex) {
            this.renderCreativeTab(var8);
         }
      }

      if (tabPage != 0)
      {
         if (var4 != CreativeTabs.tabAllSearch)
         {
            this.mc.getTextureManager().bindTexture(field_110424_t);
            renderCreativeTab(CreativeTabs.tabAllSearch);
         }
         if (var4 != CreativeTabs.tabInventory)
         {
            this.mc.getTextureManager().bindTexture(field_110424_t);
            renderCreativeTab(CreativeTabs.tabInventory);
         }
      }

      this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + var4.getBackgroundImageName()));
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      this.searchField.drawTextBox();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int var9 = this.guiLeft + 175;
      var6 = this.guiTop + 18;
      var7 = var6 + 112;
      this.mc.getTextureManager().bindTexture(field_110424_t);
      if (var4.shouldHidePlayerInventory()) {
         this.drawTexturedModalRect(var9, var6 + (int)((float)(var7 - var6 - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
      }

      this.renderCreativeTab(var4);
      if (var4 == CreativeTabs.tabInventory) {
         GuiInventory.func_110423_a(this.guiLeft + 43, this.guiTop + 45, 20, (float)(this.guiLeft + 43 - par2), (float)(this.guiTop + 45 - 30 - par3), this.mc.thePlayer);
      }

   }

   protected boolean func_74232_a(CreativeTabs par1CreativeTabs, int par2, int par3) {
      int var4 = par1CreativeTabs.getTabColumn();
      int var5 = 28 * var4;
      byte var6 = 0;
      if (var4 == 5) {
         var5 = this.xSize - 28 + 2;
      } else if (var4 > 0) {
         var5 += var4;
      }

      int var7;
      if (par1CreativeTabs.isTabInFirstRow()) {
         var7 = var6 - 32;
      } else {
         var7 = var6 + this.ySize;
      }

      return par2 >= var5 && par2 <= var5 + 28 && par3 >= var7 && par3 <= var7 + 32;
   }

   protected boolean renderCreativeInventoryHoveringText(CreativeTabs par1CreativeTabs, int par2, int par3) {
      int var4 = par1CreativeTabs.getTabColumn();
      int var5 = 28 * var4;
      byte var6 = 0;
      if (var4 == 5) {
         var5 = this.xSize - 28 + 2;
      } else if (var4 > 0) {
         var5 += var4;
      }

      int var7;
      if (par1CreativeTabs.isTabInFirstRow()) {
         var7 = var6 - 32;
      } else {
         var7 = var6 + this.ySize;
      }

      if (this.isPointInRegion(var5 + 3, var7 + 3, 23, 27, par2, par3)) {
         this.drawCreativeTabHoveringText(I18n.getString(par1CreativeTabs.getTranslatedTabLabel()), par2, par3);
         return true;
      } else {
         return false;
      }
   }

   protected void renderCreativeTab(CreativeTabs par1CreativeTabs) {
      boolean var2 = par1CreativeTabs.getTabIndex() == selectedTabIndex;
      boolean var3 = par1CreativeTabs.isTabInFirstRow();
      int var4 = par1CreativeTabs.getTabColumn();
      int var5 = var4 * 28;
      int var6 = 0;
      int var7 = this.guiLeft + 28 * var4;
      int var8 = this.guiTop;
      byte var9 = 32;
      if (var2) {
         var6 += 32;
      }

      if (var4 == 5) {
         var7 = this.guiLeft + this.xSize - 28;
      } else if (var4 > 0) {
         var7 += var4;
      }

      if (var3) {
         var8 -= 28;
      } else {
         var6 += 64;
         var8 += this.ySize - 4;
      }

      GL11.glDisable(2896);
      this.drawTexturedModalRect(var7, var8, var5, var6, 28, var9);
      this.zLevel = 100.0F;
      itemRenderer.zLevel = 100.0F;
      var7 += 6;
      var8 += 8 + (var3 ? 1 : -1);
      GL11.glEnable(2896);
      GL11.glEnable(32826);
      ItemStack var10 = new ItemStack(par1CreativeTabs.getTabIconItem());
      itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.getTextureManager(), var10, var7, var8);
      itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.getTextureManager(), var10, var7, var8);
      GL11.glDisable(2896);
      itemRenderer.zLevel = 0.0F;
      this.zLevel = 0.0F;
   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.id == 0) {
         this.mc.displayGuiScreen(new GuiAchievements(this.mc.statFileWriter));
      }

      if (par1GuiButton.id == 1) {
         this.mc.displayGuiScreen(new GuiStats(this, this.mc.statFileWriter));
      }

   }

   public int getCurrentTabIndex() {
      return selectedTabIndex;
   }

   static InventoryBasic getInventory() {
      return inventory;
   }

   public boolean allowsImposedChat() {
      return false;
   }

   static {
      selectedTabIndex = CreativeTabs.tabBlock.getTabIndex();
   }
}
