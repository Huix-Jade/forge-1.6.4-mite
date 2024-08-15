package net.minecraft.client.gui.inventory;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Translator;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class GuiCrafting extends InventoryEffectRenderer {
   private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("textures/gui/container/crafting_table.png");

   public GuiCrafting(EntityPlayer player, World par2World, int par3, int par4, int par5) {
      super(new ContainerWorkbench(player, par3, par4, par5));
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      ContainerWorkbench container_workbench = (ContainerWorkbench)this.inventorySlots;
      String var3 = Translator.get("tile.toolbench." + BlockWorkbench.getToolMaterial(container_workbench.getBlockMetadata()) + ".name");
      this.fontRenderer.drawString(var3, 29, 6, 4210752);
      this.fontRenderer.drawString(I18n.getString("container.inventory"), 7, this.ySize - 96 + 3, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(craftingTableGuiTextures);
      int var4 = this.guiLeft;
      int var5 = this.guiTop;
      this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
      EntityClientPlayerMP player = this.mc.thePlayer;
      if (player.crafting_ticks > 0) {
         this.drawTexturedModalRect(var4 + 90, var5 + 34, 176, 0, player.crafting_ticks * 23 / player.crafting_period, 16);
      }

      SlotCrafting slot_crafting = (SlotCrafting)this.inventorySlots.getSlot(0);
      if (slot_crafting.getNumCraftingResults(player) > 1) {
         this.mc.getTextureManager().bindTexture(GuiIngame.MITE_icons);
         float grey = 0.54509807F;
         GL11.glColor4f(grey, grey, grey, 1.0F);
         this.drawTexturedModalRect(var4 + 147, var5 + 31, 16, 0, 3, 3);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

   }
}
