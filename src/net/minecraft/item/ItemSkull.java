package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;

public class ItemSkull extends Item {
   private static final String[] skullTypes = new String[]{"skeleton", "wither", "zombie", "char", "creeper"};
   public static final String[] field_94587_a = new String[]{"skeleton", "wither", "zombie", "steve", "creeper"};
   private Icon[] field_94586_c;

   public ItemSkull(int par1) {
      super(par1, Material.bone, "skull");
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, true);
      if (rc != null && rc.isBlock()) {
         return rc.face_hit.isBottom() ? false : player.tryPlaceHeldItemAsBlock(rc, Block.skull);
      } else {
         return false;
      }
   }

   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
      for(int var4 = 0; var4 < skullTypes.length; ++var4) {
         par3List.add(new ItemStack(par1, 1, var4));
      }

   }

   public Icon getIconFromSubtype(int par1) {
      if (par1 < 0 || par1 >= skullTypes.length) {
         par1 = 0;
      }

      return this.field_94586_c[par1];
   }

   public int getMetadata(int par1) {
      return par1;
   }

   public String getUnlocalizedName(ItemStack par1ItemStack) {
      int var2 = par1ItemStack.getItemSubtype();
      if (var2 < 0 || var2 >= skullTypes.length) {
         var2 = 0;
      }

      return super.getUnlocalizedName() + "." + skullTypes[var2];
   }

   public String getItemDisplayName(ItemStack par1ItemStack) {
      if (par1ItemStack == null) {
         return "Skull";
      } else {
         return par1ItemStack.getItemSubtype() == 3 && par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("SkullOwner") ? StatCollector.translateToLocalFormatted("item.skull.player.name", par1ItemStack.getTagCompound().getString("SkullOwner")) : super.getItemDisplayName(par1ItemStack);
      }
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.field_94586_c = new Icon[field_94587_a.length];

      for(int var2 = 0; var2 < field_94587_a.length; ++var2) {
         this.field_94586_c[var2] = par1IconRegister.registerIcon(this.getIconString() + "_" + field_94587_a[var2]);
      }

   }
}
