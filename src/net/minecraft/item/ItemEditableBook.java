package net.minecraft.item;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringHelper;

public class ItemEditableBook extends Item {
   public ItemEditableBook(int par1) {
      super(par1, "book_written");
      this.setMaterial(new Material[]{Material.paper, Material.leather});
      this.setMaxStackSize(1);
   }

   public static boolean validBookTagContents(NBTTagCompound par0NBTTagCompound) {
      if (!ItemWritableBook.validBookTagPages(par0NBTTagCompound)) {
         return false;
      } else if (!par0NBTTagCompound.hasKey("title")) {
         return false;
      } else {
         String var1 = par0NBTTagCompound.getString("title");
         return var1 != null && var1.length() <= 16 ? par0NBTTagCompound.hasKey("author") : false;
      }
   }

   public String getItemDisplayName(ItemStack par1ItemStack) {
      if (par1ItemStack != null && par1ItemStack.hasTagCompound()) {
         NBTTagCompound var2 = par1ItemStack.getTagCompound();
         NBTTagString var3 = (NBTTagString)var2.getTag("title");
         if (var3 != null) {
            String title = var3.toString().trim();
            if (!(this instanceof ItemReferencedBook) && !title.isEmpty()) {
               title = StringHelper.stripLeading("\"", StringHelper.stripTrailing("\"", title)).trim();
               if (!title.startsWith("\"")) {
                  title = "\"" + title;
               }

               if (!title.endsWith("\"")) {
                  title = title + "\"";
               }

               return title;
            }

            return title;
         }
      }

      return super.getItemDisplayName(par1ItemStack);
   }

   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4, Slot slot) {
      if (par1ItemStack.hasTagCompound()) {
         NBTTagCompound var5 = par1ItemStack.getTagCompound();
         NBTTagString var6 = (NBTTagString)var5.getTag("author");
         if (var6 != null) {
            par3List.add(EnumChatFormatting.GRAY + String.format(StatCollector.translateToLocalFormatted("book.byAuthor", var6.data)));
         }
      }

   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      ItemStack held_item_stack = player.getHeldItemStack();
      player.displayGUIBook(held_item_stack);
      return true;
   }

   public boolean getShareTag() {
      return true;
   }

   public boolean hasEffect(ItemStack par1ItemStack) {
      return true;
   }

   public boolean canBeRenamed() {
      return false;
   }
}
