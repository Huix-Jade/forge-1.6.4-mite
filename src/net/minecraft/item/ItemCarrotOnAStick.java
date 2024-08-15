package net.minecraft.item;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.DamageSource;

public class ItemCarrotOnAStick extends Item implements IDamageableItem {
   protected Material hook_material;

   public ItemCarrotOnAStick(int par1, Material hook_material) {
      super(par1, Material.wood, "carrot_on_a_stick");
      this.addMaterial(new Material[]{this.hook_material = hook_material});
      this.setCreativeTab(CreativeTabs.tabTransport);
      this.setMaxStackSize(1);
      this.setMaxDamage(25);
   }

   public boolean isFull3D() {
      return true;
   }

   public boolean shouldRotateAroundWhenRendering() {
      return true;
   }

   public ItemFishingRod getFishingRodItem() {
      if (this.hook_material == Material.flint) {
         return Item.fishingRodFlint;
      } else if (this.hook_material == Material.obsidian) {
         return Item.fishingRodObsidian;
      } else if (this.hook_material == Material.copper) {
         return Item.fishingRodCopper;
      } else if (this.hook_material == Material.silver) {
         return Item.fishingRodSilver;
      } else if (this.hook_material == Material.gold) {
         return Item.fishingRodGold;
      } else if (this.hook_material == Material.iron) {
         return Item.fishingRodIron;
      } else if (this.hook_material == Material.mithril) {
         return Item.fishingRodMithril;
      } else if (this.hook_material == Material.adamantium) {
         return Item.fishingRodAdamantium;
      } else {
         return this.hook_material == Material.ancient_metal ? Item.fishingRodAncientMetal : null;
      }
   }

   public int getNumComponentsForDurability() {
      return 1;
   }

   public int getRepairCost() {
      return 0;
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      if (player.isRiding() && player.ridingEntity instanceof EntityPig) {
         EntityPig pig = (EntityPig)player.ridingEntity;
         ItemStack item_stack = player.getHeldItemStack();
         if (item_stack.getMaxDamage() - item_stack.getItemDamage() >= 7) {
            if (player.onServer() && pig.getAIControlledByPlayer().isControlledByPlayer()) {
               pig.getAIControlledByPlayer().boostSpeed();
               item_stack.tryDamageItem(DamageSource.pig_nibble, 7, player);
            }

            return true;
         }
      }

      return false;
   }

   public void addInformationBeforeEnchantments(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4, Slot slot) {
      ItemFishingRod item_fishing_rod = ((ItemCarrotOnAStick)par1ItemStack.getItem()).getFishingRodItem();
      item_fishing_rod.addInformationBeforeEnchantments(new ItemStack(item_fishing_rod), par2EntityPlayer, par3List, par4, slot);
   }

   public ItemStack getItemProducedWhenDestroyed(ItemStack item_stack, DamageSource damage_source) {
      return damage_source == DamageSource.pig_nibble ? (new ItemStack(this.getFishingRodItem())).setTagCompound(item_stack.stackTagCompound) : null;
   }

   public String getNameDisambiguationForReferenceFile(int subtype) {
      return this.hook_material.name;
   }
}
