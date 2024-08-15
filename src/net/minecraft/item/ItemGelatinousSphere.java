package net.minecraft.item;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGelatinousSphere;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Translator;
import net.minecraft.world.WorldServer;

public class ItemGelatinousSphere extends Item {
   public static final String[] names = new String[]{"green", "ochre", "crimson", "gray", "black"};
   private Icon[] icons;
   public static final int GREEN = 0;
   public static final int OCHRE = 1;
   public static final int CRIMSON = 2;
   public static final int GRAY = 3;
   public static final int BLACK = 4;

   public ItemGelatinousSphere(int par1) {
      super(par1, Material.slime, "gelatinous_sphere");
      this.setCreativeTab(CreativeTabs.tabMisc);
      this.setUnlocalizedName("gelatinousSphere");
      this.setCraftingDifficultyAsComponent(100.0F);
   }

   public Icon getIconFromSubtype(int par1) {
      int var2 = MathHelper.clamp_int(par1, 0, names.length - 1);
      return this.icons[var2];
   }

   public String getUnlocalizedName(ItemStack par1ItemStack) {
      if (par1ItemStack == null) {
         return super.getUnlocalizedName();
      } else {
         int var2 = MathHelper.clamp_int(par1ItemStack.getItemSubtype(), 0, names.length - 1);
         return super.getUnlocalizedName() + "." + names[var2];
      }
   }

   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
      for(int var4 = 0; var4 < names.length; ++var4) {
         par3List.add(new ItemStack(par1, 1, var4));
      }

   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.icons = new Icon[names.length];

      for(int var2 = 0; var2 < names.length; ++var2) {
         this.icons[var2] = par1IconRegister.registerIcon(this.getIconString() + "/" + names[var2]);
      }

   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      if (player.onServer()) {
         int subtype = player.getHeldItemStack().getItemSubtype();
         if (!player.inCreativeMode()) {
            player.convertOneOfHeldItem((ItemStack)null);
            player.addHungerServerSide(0.25F * EnchantmentHelper.getEnduranceModifier(player));
         }

         WorldServer world = player.getWorldServer();
         world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         world.spawnEntityInWorld(new EntityGelatinousSphere(world, player, this, subtype));
      } else {
         player.bobItem();
      }

      return true;
   }

   public int getAttackDamage(int subtype) {
      return subtype < 3 ? subtype + 1 : subtype;
   }

   public void addInformation(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
      if (extended_info) {
         info.add("");
         info.add(EnumChatFormatting.BLUE + Translator.getFormatted("item.tooltip.missileDamage", this.getAttackDamage(item_stack.getItemSubtype())));
      }

   }

   public DamageSource getDamageType(int subtype) {
      return subtype != 3 && subtype != 4 ? DamageSource.pepsin : DamageSource.acid;
   }
}
