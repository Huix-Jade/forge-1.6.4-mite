package net.minecraft.item;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.inventory.Slot;
import net.minecraft.mite.Skill;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.util.Translator;
import net.minecraft.world.WorldServer;

public class ItemFishingRod extends Item implements IDamageableItem {
   private Icon castIcon;
   private Icon[] uncastIcons = new Icon[9];
   private Material hook_material;

   public ItemFishingRod(int par1, Material hook_material) {
      super(par1, Material.wood, "fishing_rod");
      this.addMaterial(new Material[]{this.hook_material = hook_material});
      this.setMaxDamage((int)(2.0F * hook_material.durability + (float)(hook_material == Material.flint ? 1 : 0)));
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabTools);
      this.setSkillsetThatCanRepairThis(Skill.FISHING.id);
   }

   public boolean isFull3D() {
      return true;
   }

   public boolean shouldRotateAroundWhenRendering() {
      return true;
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      if (player.fishEntity == null && !player.canCastFishingRod()) {
         return false;
      } else {
         if (player.onClient()) {
            player.swingArm();
         } else if (player.fishEntity != null) {
            player.getHeldItemStack().tryDamageItem(DamageSource.generic, player.fishEntity.catchFish(), player);
         } else {
            WorldServer world = player.getWorldServer();
            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            world.spawnEntityInWorld(new EntityFishHook(world, player));
         }

         return true;
      }
   }

   private int getMaterialOrdinal() {
      if (this.hook_material == Material.flint) {
         return 0;
      } else if (this.hook_material == Material.obsidian) {
         return 1;
      } else if (this.hook_material == Material.copper) {
         return 2;
      } else if (this.hook_material == Material.silver) {
         return 3;
      } else if (this.hook_material == Material.gold) {
         return 4;
      } else if (this.hook_material == Material.iron) {
         return 5;
      } else if (this.hook_material == Material.mithril) {
         return 6;
      } else if (this.hook_material == Material.adamantium) {
         return 7;
      } else {
         return this.hook_material == Material.ancient_metal ? 8 : -1;
      }
   }

   private Material getMaterialByOrdinal(int ordinal) {
      if (ordinal == 0) {
         return Material.flint;
      } else if (ordinal == 1) {
         return Material.obsidian;
      } else if (ordinal == 2) {
         return Material.copper;
      } else if (ordinal == 3) {
         return Material.silver;
      } else if (ordinal == 4) {
         return Material.gold;
      } else if (ordinal == 5) {
         return Material.iron;
      } else if (ordinal == 6) {
         return Material.mithril;
      } else if (ordinal == 7) {
         return Material.adamantium;
      } else {
         return ordinal == 8 ? Material.ancient_metal : null;
      }
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.castIcon = par1IconRegister.registerIcon(this.getIconString() + "_cast");

      for(int i = 0; i < this.uncastIcons.length; ++i) {
         this.uncastIcons[i] = par1IconRegister.registerIcon("fishing_rods/" + this.getMaterialByOrdinal(i).name + "_uncast");
      }

   }

   public Icon func_94597_g() {
      return this.castIcon;
   }

   public Icon getIconFromSubtype(int par1) {
      return this.uncastIcons[this.getMaterialOrdinal()];
   }

   public int getItemEnchantability() {
      return this.getMaterialForEnchantment().enchantability;
   }

   public Material getMaterialForDurability() {
      return this.getHookMaterial();
   }

   public Material getMaterialForEnchantment() {
      return this.getHookMaterial();
   }

   public Material getHookMaterial() {
      return this.hook_material;
   }

   public int getNumComponentsForDurability() {
      return 1;
   }

   public int getRepairCost() {
      return 1;
   }

   public void addInformationBeforeEnchantments(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4, Slot slot) {
      par3List.add(Translator.getFormatted("item.tooltip.fishingRodHook", ((ItemFishingRod)par1ItemStack.getItem()).getHookMaterial().getLocalizedName()));
   }

   public String getNameDisambiguationForReferenceFile(int subtype) {
      return this.hook_material.name;
   }
}
