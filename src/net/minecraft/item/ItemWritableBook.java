package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class ItemWritableBook extends Item {
   public ItemWritableBook(int par1) {
      super(par1, "book_writable");
      this.setMaterial(new Material[]{Material.paper, Material.leather});
      this.setMaxStackSize(1);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      player.displayGUIBook(player.getHeldItemStack());
      return true;
   }

   public boolean getShareTag() {
      return true;
   }

   public static boolean validBookTagPages(NBTTagCompound par0NBTTagCompound) {
      if (par0NBTTagCompound == null) {
         return false;
      } else if (!par0NBTTagCompound.hasKey("pages")) {
         return false;
      } else {
         NBTTagList var1 = (NBTTagList)par0NBTTagCompound.getTag("pages");

         for(int var2 = 0; var2 < var1.tagCount(); ++var2) {
            NBTTagString var3 = (NBTTagString)var1.tagAt(var2);
            if (var3.data == null) {
               return false;
            }

            if (var3.data.length() > 256) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean canBeRenamed() {
      return false;
   }
}
