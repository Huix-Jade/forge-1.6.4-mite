package net.minecraft.item;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.Slot;
import net.minecraft.mite.Skill;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumItemInUseAction;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.Icon;
import net.minecraft.util.Translator;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class ItemBow extends Item implements IDamageableItem {
   private static final Material[] possible_arrow_materials;
   public static final String[] bow_pull_icon_name_array;
   private Icon[] iconArray;
   private Material reinforcement_material;

   public ItemBow(int id, Material reinforcement_material) {
      super(id, Material.wood, "bows/" + reinforcement_material.name + "/");
      if (reinforcement_material != null && reinforcement_material != Material.wood) {
         this.addMaterial(new Material[]{reinforcement_material});
      }

      this.reinforcement_material = reinforcement_material;
      this.setMaxStackSize(1);
      this.setMaxDamage(reinforcement_material == Material.mithril ? 128 : (reinforcement_material == Material.ancient_metal ? 64 : 32));
      this.setCreativeTab(CreativeTabs.tabCombat);
      this.setSkillsetThatCanRepairThis(reinforcement_material.isMetal() ? Skill.ARCHERY.id + Skill.BLACKSMITHING.id : Skill.ARCHERY.id);
   }

   public static int getTicksForMaxPull(ItemStack item_stack) {
      return 20 - EnchantmentHelper.getEnchantmentLevelFractionOfInteger(Enchantment.quickness, item_stack, 10);
   }

   public static int getTicksPulled(ItemStack item_stack, int item_in_use_count) {
      return item_stack.getMaxItemUseDuration() - item_in_use_count;
   }

   public static float getFractionPulled(ItemStack item_stack, int item_in_use_count) {
      return Math.min((float)getTicksPulled(item_stack, item_in_use_count) / (float)getTicksForMaxPull(item_stack), 1.0F);
   }

   public void onPlayerStoppedUsing(ItemStack item_stack, World world, EntityPlayer player, int item_in_use_count) {
      int j = this.getMaxItemUseDuration(item_stack) - item_in_use_count;

      ArrowLooseEvent event = new ArrowLooseEvent(player, item_stack, j);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled())
      {
         return;
      }
      j = event.charge;


      if (!world.isRemote) {
         ItemArrow arrow = player.inventory.getReadiedArrow();
         if (arrow == null) {
            if (!player.inCreativeMode()) {
               return;
            }

            arrow = player.nocked_arrow;
         }

         float fraction_pulled = getFractionPulled(item_stack, item_in_use_count);
         fraction_pulled = (fraction_pulled * fraction_pulled + fraction_pulled * 2.0F) / 3.0F;
         if (!(fraction_pulled < 0.1F)) {
            if (fraction_pulled > 1.0F) {
               fraction_pulled = 1.0F;
            }

            EntityArrow entity_arrow = new EntityArrow(world, player, fraction_pulled * 2.0F, arrow, item_stack.isItemEnchanted());
            player.nocked_arrow = null;
            if (fraction_pulled == 1.0F) {
               entity_arrow.setIsCritical(true);
            }

            int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, item_stack);
            if (power > 0) {
               entity_arrow.setDamage(entity_arrow.getDamage() + (double)((float)power * 0.5F) + 0.5);
            }

            int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, item_stack);
            if (punch > 0) {
               entity_arrow.setKnockbackStrength(punch);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, item_stack) > 0) {
               entity_arrow.setFire(100);
            }

            player.tryDamageHeldItem(DamageSource.generic, 1);
            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + fraction_pulled * 0.5F);
            if (player.inCreativeMode()) {
               entity_arrow.canBePickedUp = 2;
            } else {
               player.inventory.consumeArrow();
            }

            if (!world.isRemote) {
               world.spawnEntityInWorld(entity_arrow);
            }

         }
      }
   }

   public void onItemUseFinish(ItemStack item_stack, World world, EntityPlayer player) {
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 72000;
   }

   public EnumItemInUseAction getItemInUseAction(ItemStack par1ItemStack, EntityPlayer player) {
      return EnumItemInUseAction.BOW;
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      ArrowNockEvent event = new ArrowNockEvent(player, player.itemInUse);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled())
      {
         return false;
      }

      if (!player.inCreativeMode() && player.inventory.getReadiedArrow() == null) {
         return false;
      } else {
         player.nocked_arrow = player.inventory.getReadiedArrow();
         if (player.nocked_arrow == null && player.inCreativeMode()) {
            player.nocked_arrow = Item.arrowFlint;
         }

         if (player.onServer()) {
            player.sendPacketToAssociatedPlayers((new Packet85SimpleSignal(EnumSignal.nocked_arrow)).setShort(player.nocked_arrow.itemID).setEntityID(player), false);
         }

         player.setHeldItemInUse();
         return true;
      }
   }

   public int getItemEnchantability() {
      return this.getMaterialForEnchantment().enchantability;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.itemIcon = par1IconRegister.registerIcon(this.getIconString() + "standby");
      this.iconArray = new Icon[bow_pull_icon_name_array.length];

      for(int i = 0; i < this.iconArray.length; ++i) {
         this.iconArray[i] = par1IconRegister.registerIcon(this.getIconString() + bow_pull_icon_name_array[i]);
      }

   }

   public Icon getItemIconForUseDuration(int par1, EntityPlayer player) {
      if (player.nocked_arrow == null) {
         Minecraft.setErrorMessage("getItemIconForUseDuration: nocked_arrow was null!");
         return this.iconArray[par1];
      } else {
         return this.iconArray[par1 + player.nocked_arrow.getArrowIndex() * 3];
      }
   }

   public Material getMaterialForDurability() {
      return Material.wood;
   }

   public Material getMaterialForRepairs() {
      return this.reinforcement_material == null ? Material.wood : this.reinforcement_material;
   }

   public int getNumComponentsForDurability() {
      return this.reinforcement_material.isMetal() ? 1 : 0;
   }

   public int getRepairCost() {
      return this.getNumComponentsForDurability() * 2;
   }

   public void addInformation(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
      if (extended_info && this.reinforcement_material.isMetal()) {
         int bonus = this.reinforcement_material == Material.mithril ? 25 : 10;
         info.add("");
         info.add(EnumChatFormatting.BLUE + Translator.getFormatted("item.tooltip.velocityBonus", bonus));
      }

      super.addInformation(item_stack, player, info, extended_info, slot);
   }

   public boolean hasQuality() {
      return true;
   }

   public boolean similarToItemsOfSameClass() {
      return true;
   }

   static {
      possible_arrow_materials = new Material[]{Material.flint, Material.obsidian, Material.copper, Material.silver, Material.rusted_iron, Material.gold, Material.iron, Material.mithril, Material.adamantium, Material.ancient_metal};
      bow_pull_icon_name_array = new String[possible_arrow_materials.length * 3];

      for(int arrow_index = 0; arrow_index < possible_arrow_materials.length; ++arrow_index) {
         Material material = possible_arrow_materials[arrow_index];

         for(int icon_index = 0; icon_index < 3; ++icon_index) {
            bow_pull_icon_name_array[arrow_index * 3 + icon_index] = material.name + "_arrow_" + icon_index;
         }
      }

   }
}
