package net.minecraft.item;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;

public class ItemCoal extends Item {
   private Icon field_111220_a;

   public ItemCoal(int par1) {
      super(par1, Material.coal, "coal");
      this.setMaxStackSize(16);
      this.setCraftingDifficultyAsComponent(25.0F);
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }

   public String getUnlocalizedName(ItemStack par1ItemStack) {
      return par1ItemStack != null && par1ItemStack.getItemSubtype() == 1 ? "item.charcoal" : "item.coal";
   }

   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
      par3List.add(new ItemStack(par1, 1, 0));
      par3List.add(new ItemStack(par1, 1, 1));
   }

   public Icon getIconFromSubtype(int par1) {
      return par1 == 1 ? this.field_111220_a : super.getIconFromSubtype(par1);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      super.registerIcons(par1IconRegister);
      this.field_111220_a = par1IconRegister.registerIcon("charcoal");
   }

   public int getBurnTime(ItemStack item_stack) {
      return 1600;
   }

   public int getHeatLevel(ItemStack item_stack) {
      return item_stack.getItemSubtype() == 1 ? 1 : 2;
   }
}
