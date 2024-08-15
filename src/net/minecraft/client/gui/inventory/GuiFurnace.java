package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Translator;
import org.lwjgl.opengl.GL11;

public class GuiFurnace extends GuiContainer {
   private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");
   private TileEntityFurnace furnaceInventory;

   public GuiFurnace(EntityPlayer player, TileEntityFurnace par2TileEntityFurnace) {
      super(new ContainerFurnace(player, par2TileEntityFurnace));
      this.furnaceInventory = par2TileEntityFurnace;
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      String var3 = this.furnaceInventory.hasCustomName() ? this.furnaceInventory.getCustomNameOrUnlocalized() : I18n.getString(this.furnaceInventory.getCustomNameOrUnlocalized());
      this.fontRenderer.drawString(var3, this.xSize / 2 - this.fontRenderer.getStringWidth(var3) / 2, 6, 4210752);
      this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
      int var4 = (this.width - this.xSize) / 2;
      int var5 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
      int var6;
      if (this.furnaceInventory.isBurning()) {
         var6 = this.furnaceInventory.getBurnTimeRemainingScaled(12);
         this.drawTexturedModalRect(var4 + 56, var5 + 36 + 12 - var6, 176, 12 - var6, 14, var6 + 2);
      }

      var6 = this.furnaceInventory.getCookProgressScaled(24);
      this.drawTexturedModalRect(var4 + 79, var5 + 34, 176, 14, var6 + 1, 16);
   }

   public void drawScreen(int mouse_x, int mouse_y, float par3) {
      super.drawScreen(mouse_x, mouse_y, par3);
      if (this.inventorySlots.slot_that_did_not_accept_item != null) {
         ItemStack mouse_item_stack = this.mc.thePlayer.inventory.getItemStack();
         if (mouse_item_stack != null) {
            Slot slot = this.getSlotThatMouseIsOver(mouse_x, mouse_y);
            if (slot == null) {
               this.inventorySlots.slot_that_did_not_accept_item = null;
            } else if (slot != null && !slot.isItemValid(mouse_item_stack)) {
               if (!slot.accepts_large_items && Slot.isLargeItem(mouse_item_stack.getItem())) {
                  this.drawCreativeTabHoveringText(EnumChatFormatting.GOLD + Translator.get("container.furnace.wontFit"), mouse_x, mouse_y);
               } else {
                  if (slot == this.inventorySlots.getSlot(0)) {
                     if (!FurnaceRecipes.smelting().doesSmeltingRecipeExistFor(mouse_item_stack)) {
                        this.drawCreativeTabHoveringText(EnumChatFormatting.GOLD + Translator.get("container.furnace.cantSmelt"), mouse_x, mouse_y);
                        return;
                     }
                  } else if (slot == this.inventorySlots.getSlot(1)) {
                     if (this.furnaceInventory.getItemHeatLevel(mouse_item_stack) < 1) {
                        this.drawCreativeTabHoveringText(EnumChatFormatting.GOLD + Translator.get("container.furnace.notFuel"), mouse_x, mouse_y);
                        return;
                     }

                     if (this.furnaceInventory.getItemHeatLevel(mouse_item_stack) > this.furnaceInventory.getMaxHeatLevel()) {
                        this.drawCreativeTabHoveringText(EnumChatFormatting.GOLD + Translator.get("container.furnace.tooHot"), mouse_x, mouse_y);
                     }
                  }

               }
            }
         }
      }
   }
}
