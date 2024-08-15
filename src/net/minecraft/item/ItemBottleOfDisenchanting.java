package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemBottleOfDisenchanting extends Item {
   public ItemBottleOfDisenchanting(int id) {
      super(id, Material.glass, "bottle_of_disenchanting");
      this.setMaxStackSize(1);
      this.setCraftingDifficultyAsComponent(25.0F);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public void onItemUseFinish(ItemStack item_stack, World world, EntityPlayer player) {
      if (player.onServer()) {
         player.clearActivePotions();
         ((WorldServer)world).removeCursesFromPlayer((EntityPlayerMP)player);
      }

      super.onItemUseFinish(item_stack, world, player);
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 32;
   }

   public boolean isDrinkable(int item_subtype) {
      return true;
   }

   public Item getItemProducedOnItemUseFinish() {
      return glassBottle;
   }
}
