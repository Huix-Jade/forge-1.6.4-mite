package net.minecraft.client.gui.inventory;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;

public class CreativeCrafting implements ICrafting {
   private final Minecraft mc;

   public CreativeCrafting(Minecraft var1) {
      this.mc = var1;
   }

   public void sendContainerAndContentsToPlayer(Container var1, List var2) {
   }

   public void sendSlotContents(Container var1, int var2, ItemStack var3) {
      this.mc.playerController.sendSlotPacket(var3, var2);
   }

   public void sendProgressBarUpdate(Container var1, int var2, int var3) {
   }
}
