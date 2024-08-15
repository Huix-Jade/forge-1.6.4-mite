package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntityAnvil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiRepair extends GuiContainer implements ICrafting {
   private static final ResourceLocation anvilGuiTextures = new ResourceLocation("textures/gui/container/anvil.png");
   private ContainerRepair repairContainer;
   private GuiTextField itemNameField;
   private InventoryPlayer field_82325_q;
   private TileEntityAnvil tile_entity_anvil;
   private int x;
   private int y;
   private int z;

   public GuiRepair(EntityPlayer player, int par3, int par4, int par5) {
      super(new ContainerRepair(player, par3, par4, par5));
      this.field_82325_q = player.inventory;
      this.repairContainer = (ContainerRepair)this.inventorySlots;
      this.x = par3;
      this.y = par4;
      this.z = par5;
      this.tile_entity_anvil = (TileEntityAnvil)player.worldObj.getBlockTileEntity(this.x, this.y, this.z);
   }

   public void initGui() {
      super.initGui();
      Keyboard.enableRepeatEvents(true);
      int var1 = (this.width - this.xSize) / 2;
      int var2 = (this.height - this.ySize) / 2;
      this.itemNameField = new GuiTextField(this.fontRenderer, var1 + 62, var2 + 24, 103, 12);
      this.itemNameField.setTextColor(-1);
      this.itemNameField.setDisabledTextColour(-1);
      this.itemNameField.setEnableBackgroundDrawing(false);
      this.itemNameField.setMaxStringLength(40);
      this.inventorySlots.removeCraftingFromCrafters(this);
      this.inventorySlots.addCraftingToCrafters(this);
   }

   public void onGuiClosed() {
      super.onGuiClosed();
      Keyboard.enableRepeatEvents(false);
      this.inventorySlots.removeCraftingFromCrafters(this);
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
      GL11.glDisable(2896);
      this.fontRenderer.drawString(this.tile_entity_anvil.getCustomInvNameOrTranslated(), 60, 6, 4210752);
      GL11.glEnable(2896);
   }

   protected void keyTyped(char par1, int par2) {
      if (par1 != '"' && this.itemNameField.textboxKeyTyped(par1, par2)) {
         this.func_135015_g();
      } else {
         super.keyTyped(par1, par2);
      }

   }

   private boolean getItemStackHasCustomName() {
      Slot slot = this.repairContainer.getSlot(0);
      return slot != null && slot.getHasStack() && slot.getStack().hasDisplayName();
   }

   private String getItemStackDisplayName() {
      Slot slot = this.repairContainer.getSlot(0);
      return slot != null && slot.getHasStack() ? slot.getStack().getDisplayName() : null;
   }

   private void func_135015_g() {
      String var1 = this.itemNameField.getText();
      Slot var2 = this.repairContainer.getSlot(0);
      if (var2 != null && var2.getHasStack() && !var2.getStack().hasDisplayName() && var1.equals(var2.getStack().getDisplayName())) {
         var1 = "";
      }

      this.repairContainer.updateItemName(var1);
      this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("MC|ItemName", var1.getBytes()));
   }

   protected void mouseClicked(int par1, int par2, int par3) {
      boolean is_focused = this.itemNameField.isFocused();
      super.mouseClicked(par1, par2, par3);
      this.itemNameField.mouseClicked(par1, par2, par3);
      if (this.itemNameField.isFocused() && !is_focused && !this.getItemStackHasCustomName() && this.itemNameField.getText().equals(this.getItemStackDisplayName())) {
         this.itemNameField.setText("");
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      super.drawScreen(par1, par2, par3);
      GL11.glDisable(2896);
      this.itemNameField.drawTextBox();
   }

   private boolean canBeRenamed(ItemStack item_stack) {
      return item_stack != null && item_stack.canBeRenamed();
   }

   protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(anvilGuiTextures);
      int var4 = (this.width - this.xSize) / 2;
      int var5 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
      this.drawTexturedModalRect(var4 + 59, var5 + 20, 0, this.ySize + (this.canBeRenamed(this.repairContainer.getSlot(0).getStack()) ? 0 : 16), 110, 16);
      if ((this.repairContainer.getSlot(0).getHasStack() || this.repairContainer.getSlot(1).getHasStack()) && !this.repairContainer.getSlot(2).getHasStack()) {
         this.drawTexturedModalRect(var4 + 99, var5 + 45, this.xSize, 0, 28, 21);
      }

   }

   public void sendContainerAndContentsToPlayer(Container par1Container, List par2List) {
      this.sendSlotContents(par1Container, 0, par1Container.getSlot(0).getStack());
   }

   public void sendSlotContents(Container par1Container, int par2, ItemStack par3ItemStack) {
      if (par2 == 0) {
         this.itemNameField.setText(par3ItemStack == null ? "" : par3ItemStack.getDisplayName());
         this.itemNameField.setEnabled(this.canBeRenamed(par3ItemStack));
         if (par3ItemStack != null) {
            this.func_135015_g();
         }
      }

   }

   public void sendProgressBarUpdate(Container par1Container, int par2, int par3) {
   }

   public boolean allowsImposedChat() {
      return !this.itemNameField.isFocused();
   }
}
