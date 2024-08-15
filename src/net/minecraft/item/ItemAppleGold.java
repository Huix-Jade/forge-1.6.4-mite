package net.minecraft.item;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemAppleGold extends ItemFood {
   public ItemAppleGold(int id, int satiation, int nutrition, String texture) {
      super(id, Material.fruit, satiation, nutrition, 1000, false, false, true, texture);
      this.addMaterial(new Material[]{Material.gold});
      this.setPlantProduct();
   }

   public boolean hasEffect(ItemStack par1ItemStack) {
      return par1ItemStack.getItemSubtype() > 0;
   }

   public EnumRarity getRarity(ItemStack par1ItemStack) {
      return par1ItemStack.getItemSubtype() == 0 ? EnumRarity.rare : EnumRarity.epic;
   }

   protected void onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
      if (!par2World.isRemote) {
      }

      if (par1ItemStack.getItemSubtype() > 0) {
         if (!par2World.isRemote) {
            par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.regeneration.id, 1200, 1));
            par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 1200, 0));
            par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 1200, 0));
         }
      } else {
         super.onEaten(par1ItemStack, par2World, par3EntityPlayer);
      }

   }

   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
      par3List.add(new ItemStack(par1, 1, 0));
      par3List.add(new ItemStack(par1, 1, 1));
   }

   public boolean tryEntityInteraction(Entity entity, EntityPlayer player, ItemStack item_stack) {
      if (entity instanceof EntityZombie) {
         EntityZombie zombie = (EntityZombie)entity;
         if (player.onClient()) {
            return zombie.isVillager() && !zombie.isConverting();
         }

         if (zombie.isVillager() && !zombie.isConverting() && zombie.isPotionActive(Potion.weakness)) {
            if (player.onServer()) {
               zombie.startConversion(item_stack.getItemSubtype() == 1 ? 1 : zombie.rand.nextInt(2401) + 3600);
               if (!player.inCreativeMode()) {
                  player.convertOneOfHeldItem((ItemStack)null);
               }
            }

            return true;
         }
      }

      return false;
   }

   public String getUnlocalizedName(ItemStack item_stack) {
      return isEnchantedGoldenApple(item_stack) ? "item.appleGold.enchanted" : super.getUnlocalizedName(item_stack);
   }

   public static boolean isGoldenApple(ItemStack item_stack) {
      return item_stack != null && item_stack.itemID == appleGold.itemID;
   }

   public static boolean isUnenchantedGoldenApple(ItemStack item_stack) {
      return isGoldenApple(item_stack) && item_stack.getItemSubtype() == 0;
   }

   public static boolean isEnchantedGoldenApple(ItemStack item_stack) {
      return isGoldenApple(item_stack) && item_stack.getItemSubtype() > 0;
   }
}
