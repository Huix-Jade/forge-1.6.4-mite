package net.minecraft.item;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityBrick;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Translator;
import net.minecraft.world.WorldServer;

public class ItemBrick extends Item {
   public ItemBrick(int id, Material material, String texture) {
      super(id, material, texture);
      this.setMaxStackSize(8);
      this.setCraftingDifficultyAsComponent(100.0F);
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      if (player.onServer()) {
         if (!player.inCreativeMode()) {
            player.convertOneOfHeldItem((ItemStack)null);
            player.addHungerServerSide(0.25F * EnchantmentHelper.getEnduranceModifier(player));
         }

         WorldServer world = player.getWorldServer();
         world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         world.spawnEntityInWorld(new EntityBrick(world, player, this));
      } else {
         player.bobItem();
      }

      return true;
   }

   public void addInformation(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
      if (extended_info) {
         info.add("");
         info.add(EnumChatFormatting.BLUE + Translator.getFormatted("item.tooltip.missileDamage", 1));
      }

   }
}
