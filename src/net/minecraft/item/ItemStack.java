package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.Curse;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumItemInUseAction;
import net.minecraft.util.EnumQuality;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Translator;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public final class ItemStack {
   public static final DecimalFormat field_111284_a = new DecimalFormat("#.###");
   public int stackSize;
   public int animationsToGo;
   public int itemID;
   public NBTTagCompound stackTagCompound;
   private int subtype;
   private int damage;
   private EntityItemFrame itemFrame;
   private EnumQuality quality;
   private boolean is_artifact;

   public ItemStack(Block par1Block) {
      this((Block)par1Block, 1);
   }

   public ItemStack(int item_id) {
      this(item_id, 1, 0);
   }

   public ItemStack(int item_id, int stack_size) {
      this(item_id, stack_size, 0);
   }

   public ItemStack(Block block, int stack_size) {
      this(block.blockID, stack_size, 0);
   }

   public ItemStack(Block block, int stack_size, int subtype) {
      this(block.blockID, stack_size, subtype);
   }

   public ItemStack(Item item) {
      this(item.itemID, 1, 0);
   }

   public ItemStack(Item item, int stack_size) {
      this(item.itemID, stack_size, 0);
   }

   public ItemStack(Item item, int stack_size, int subtype) {
      this(item.itemID, stack_size, subtype);
   }

   public ItemStack(int id, int stack_size, int subtype) {
      this.itemID = id;
      this.stackSize = stack_size;
      this.setItemSubtype(subtype);
   }

   public static ItemStack loadItemStackFromNBT(NBTTagCompound par0NBTTagCompound) {
      if (!par0NBTTagCompound.hasKey("id")) {
         return null;
      } else {
         ItemStack var1 = new ItemStack();
         var1.readFromNBT(par0NBTTagCompound);
         return var1.getItem() != null ? var1 : null;
      }
   }

   private ItemStack() {
   }

   public ItemStack splitStack(int par1) {
      ItemStack var2 = (new ItemStack(this.itemID, par1, this.subtype)).setItemDamage(this.damage);
      var2.quality = this.quality;
      var2.is_artifact = this.is_artifact;
      if (this.stackTagCompound != null) {
         var2.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
      }

      this.stackSize -= par1;
      return var2;
   }

   public Item getItem() {
      return Item.itemsList[this.itemID];
   }

   public Icon getIconIndex() {
      return this.getItem().getIconIndex(this);
   }

   public int getItemSpriteNumber() {
      return this.getItem().getSpriteNumber();
   }

   public float getStrVsBlock(Block block, int metadata) {
      return this.getItem().getStrVsBlock(block, metadata);
   }

   public void onItemUseFinish(World par1World, EntityPlayer par2EntityPlayer) {
      this.getItem().onItemUseFinish(this, par1World, par2EntityPlayer);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setShort("id", (short)this.itemID);
      par1NBTTagCompound.setByte("Count", (byte)this.stackSize);
      par1NBTTagCompound.setInteger("damage", this.damage);
      par1NBTTagCompound.setShort("subtype", (short)this.subtype);
      if (this.stackTagCompound != null) {
         NBTTagCompound effective_stackTagCompound = this.stackTagCompound;
         if (ItemReferencedBook.isReferencedBook(this)) {
            effective_stackTagCompound = new NBTTagCompound();
            effective_stackTagCompound.setInteger("reference_index", ItemReferencedBook.getReferenceIndex(this));
         }

         par1NBTTagCompound.setTag("tag", effective_stackTagCompound);
      }

      if (this.getItem().hasQuality()) {
         par1NBTTagCompound.setByte("quality", (byte)this.getQuality().ordinal());
      }

      if (this.is_artifact) {
         par1NBTTagCompound.setBoolean("is_artifact", this.is_artifact);
      }

      return par1NBTTagCompound;
   }

   public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
      this.itemID = par1NBTTagCompound.getShort("id");
      this.stackSize = par1NBTTagCompound.getByte("Count");
      if (this.itemID <= 0) {
         (new Exception()).printStackTrace();
      }

      Item item;
      if (par1NBTTagCompound.hasKey("subtype")) {
         this.setItemSubtype(par1NBTTagCompound.getShort("subtype"));
         this.setItemDamage(par1NBTTagCompound.getInteger("damage"));
      } else {
         if (Minecraft.inDevMode()) {
            System.out.println("Importing item stack " + this.getItem() + ", id=" + this.itemID);
         }

         if (this.isItemStackDamageable() && this.getHasSubtypes()) {
            item = this.getItem();
            if (item instanceof ItemAnvilBlock) {
               this.setItemSubtype(par1NBTTagCompound.getShort("Damage"));
            } else {
               Minecraft.setErrorMessage("Unhandled item import, setting damage for: " + this);
               this.setItemDamage(par1NBTTagCompound.getShort("Damage"));
            }
         } else if (this.isItemStackDamageable()) {
            this.setItemDamage(par1NBTTagCompound.getShort("Damage"));
         } else {
            this.setItemSubtype(par1NBTTagCompound.getShort("Damage"));
         }
      }

      if (par1NBTTagCompound.hasKey("tag")) {
         this.stackTagCompound = par1NBTTagCompound.getCompoundTag("tag");
         if (ItemReferencedBook.isReferencedBook(this)) {
            this.setTagCompound(ItemReferencedBook.generateBookContents(ItemReferencedBook.getReferenceIndex(this)));
         }
      }

      item = this.getItem();
      if (item == null) {
         this.quality = null;
      } else {
         if (par1NBTTagCompound.hasKey("quality")) {
            this.setQuality(EnumQuality.values()[par1NBTTagCompound.getByte("quality")]);
         } else {
            this.setQuality((EnumQuality)null);
         }

         if (this.isItemStackDamageable() && this.damage >= this.getMaxDamage()) {
            this.setItemDamage(this.getMaxDamage() - 1);
         }
      }

      this.is_artifact = par1NBTTagCompound.getBoolean("is_artifact");
   }

   public int getMaxStackSize() {
      return this.getItem().getItemStackLimit(this);
   }

   public boolean isStackable() {
      return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
   }

   public boolean isItemStackDamageable() {
      Item item = this.getItem();
      return item != null && item.isDamageable();
   }

   public boolean getHasSubtypes() {
      return Item.itemsList[this.itemID].getHasSubtypes();
   }

   public boolean isItemDamaged() {
      if (this.damage < 0) {
         Minecraft.setErrorMessage("isItemDamaged: Why is damage less than zero? " + this);
      }

      if (!this.isItemStackDamageable() && this.damage != 0) {
         Minecraft.setErrorMessage("isItemDamaged: Why does non-damageable item have non-zero damage? " + this);
      }

      return this.isItemStackDamageable() && this.damage != 0;
   }

   public int getItemDamageForDisplay() {
      if (getItem() != null)
      {
         return getItem().getDisplayDamage(this);
      }
      return this.damage;
   }

   public int getItemSubtype() {
      return this.subtype;
   }

   public int getItemDamage() {
      if (getItem() != null && getItem().getDamage(this) != 0)
      {
         return getItem().getDamage(this);
      }

      return this.damage;
   }

   public ItemStack setItemSubtype(int subtype) {
      if (subtype < 0) {
         Minecraft.setErrorMessage("setItemSubtype: setting subtype to " + subtype);
         subtype = 0;
      }

      this.subtype = subtype;
      return this;
   }

   public ItemStack setItemDamage(int damage) {
      if (damage < 0) {
         Minecraft.setErrorMessage("setDamage: less than 0");
         damage = 0;
      }

      if (damage != 0 && !this.isItemStackDamageable()) {
         Minecraft.setErrorMessage("setItemDamage: setting non-zero damage for non-damageable ItemStack " + this);
         return this;
      } else {
         this.damage = damage;
         if (this.getItem() instanceof ItemAnvilBlock) {
            ((ItemAnvilBlock)this.getItem()).updateSubtypeForDamage(this);
         }

         return this;
      }
   }

   public int getRemainingDurability() {
      if (!this.isItemStackDamageable()) {
         Minecraft.setErrorMessage("getRemainingDurability: item stack is not damageable");
         return 0;
      } else {
         return this.getMaxDamage() - this.getItemDamage();
      }
   }

   public int getMaxDamage() {
      return this.getItem().getMaxDamage(this);
   }

   public int damageItem(int itemDamage, EntityLivingBase entityLiving) {
      this.tryDamageItem(DamageSource.generic, itemDamage, entityLiving);
      return itemDamage;
   }

   public ItemDamageResult tryDamageItem(World world, int damage, boolean prevent_destruction) {
      if (this.isItemStackDamageable() && damage >= 1) {
         float fraction_of_unbreaking = this.getEnchantmentLevelFraction(Enchantment.unbreaking);
         if (fraction_of_unbreaking > 0.0F) {
            Random random = new Random();
            int points_negated = 0;
            float chance_of_negation_per_point = fraction_of_unbreaking * 0.75F;

            for(int i = 0; i < damage; ++i) {
               if (random.nextFloat() < chance_of_negation_per_point) {
                  ++points_negated;
               }
            }

            damage -= points_negated;
         }

         if (prevent_destruction && this.damage + damage >= this.getMaxDamage()) {
            damage = this.getMaxDamage() - this.damage - 1;
         }

         if (damage <= 0) {
            return null;
         } else {
            ItemDamageResult result = (new ItemDamageResult()).setItemLostDurability();
            this.setItemDamage(this.damage + damage);
            return this.damage >= this.getMaxDamage() ? result.setItemWasDestroyed(world, this) : result;
         }
      } else {
         return null;
      }
   }

   public ItemDamageResult tryDamageItem(DamageSource damage_source, int damage, EntityLivingBase owner) {
      if (this.isItemStackDamageable() && this.isHarmedBy(damage_source)) {
         World world = owner.worldObj;
         if (world.isRemote) {
            Minecraft.setErrorMessage("damageItem: not meant to be called on client");
         }

         boolean was_held_item = owner != null && owner.getHeldItemStack() == this;
         boolean is_pepsin_or_acid_that_can_destroy_item = (damage_source.isPepsinDamage() || damage_source.isAcidDamage()) && this.isHarmedBy(damage_source);
         boolean prevent_destruction = owner.isWearing(this) && !is_pepsin_or_acid_that_can_destroy_item;
         if (prevent_destruction) {
            if (damage_source.isFireDamage()) {
               if (this.hasMaterial(Material.leather)) {
                  prevent_destruction = false;
               }
            } else if (damage_source.isLavaDamage()) {
               prevent_destruction = false;
               if (this.getItem().containsMetal()) {
                  damage *= 10;
               }
            }
         }

         ItemDamageResult result;
         if (owner instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)owner;
            if (player.inCreativeMode()) {
               return null;
            }

            if (player.hasCurse(Curse.equipment_decays_faster, true)) {
               damage *= 2;
            }

            result = this.tryDamageItem(world, damage, prevent_destruction);
            if (result == null || !result.itemWasDestroyed()) {
               return result;
            }

            if (player.onServer()) {
               player.addStat(StatList.objectBreakStats[this.itemID], 1);
               if (was_held_item) {
                  ItemStack item_stack = this.getItem().getItemProducedWhenDestroyed(this, damage_source);
                  if (item_stack == null) {
                     player.causeBreakingItemEffect(this.getItem(), player.getHeldItemStack() == this ? player.inventory.currentItem : -1);
                  }

                  player.convertOneOfHeldItem(item_stack);
                  if (!player.hasHeldItem()) {
                     player.getAsEntityPlayerMP().prevent_item_pickup_due_to_held_item_breaking_until = System.currentTimeMillis() + 1500L;
                  }
               } else if (--this.stackSize == 0) {
                  player.causeBreakingItemEffect(this.getItem(), player.getHeldItemStack() == this ? player.inventory.currentItem : -1);
                  player.inventory.destroyInventoryItemStack(this);
               }
            }
         } else {
            if (!(owner instanceof EntityLiving)) {
               Minecraft.setErrorMessage("tryDamageItem: no handler for " + owner);
               return null;
            }

            EntityLiving entity_living = (EntityLiving)owner;
            result = this.tryDamageItem(world, damage, prevent_destruction);
            if (result == null || !result.itemWasDestroyed()) {
               return result;
            }

            if (owner.onServer()) {
               entity_living.causeBreakingItemEffect(this.getItem());
               if (--this.stackSize == 0) {
                  entity_living.clearMatchingEquipmentSlot(this);
               }
            }
         }

         if (this.stackSize < 0) {
            this.stackSize = 0;
         }

         this.setItemDamage(0);
         return result;
      } else {
         return null;
      }
   }

   public void hitEntity(EntityLivingBase par1EntityLivingBase, EntityPlayer par2EntityPlayer) {
      boolean var3 = Item.itemsList[this.itemID].hitEntity(this, par1EntityLivingBase, par2EntityPlayer);
      if (var3) {
         par2EntityPlayer.addStat(StatList.objectUseStats[this.itemID], 1);
      }

   }

   public ItemStack copy() {
      ItemStack var1 = (new ItemStack(this.itemID, this.stackSize, this.subtype)).setItemDamage(this.damage);
      var1.setQuality(this.getQuality());
      var1.is_artifact = this.is_artifact;
      if (this.stackTagCompound != null) {
         var1.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
      }

      return var1;
   }

   public ItemStack copyOver(ItemStack item_stack) {
      item_stack.itemID = this.itemID;
      item_stack.subtype = this.subtype;
      item_stack.setItemDamage(this.damage);
      item_stack.quality = this.quality;
      item_stack.is_artifact = this.is_artifact;
      item_stack.stackSize = this.stackSize;
      item_stack.stackTagCompound = this.stackTagCompound == null ? null : (NBTTagCompound)this.stackTagCompound.copy();
      if (!areItemStackTagsEqual(item_stack, this)) {
         Minecraft.setErrorMessage("copyOver: item_stacks are different after copy");
      }

      return item_stack;
   }

   public static boolean areItemStackTagsEqual(ItemStack par0ItemStack, ItemStack par1ItemStack) {
      if (par0ItemStack == par1ItemStack) {
         return true;
      } else if (par0ItemStack != null && par1ItemStack != null) {
         if (par0ItemStack.stackTagCompound == par1ItemStack.stackTagCompound) {
            return true;
         } else {
            return par0ItemStack.stackTagCompound != null && par1ItemStack.stackTagCompound != null ? par0ItemStack.stackTagCompound.equals(par1ItemStack.stackTagCompound) : false;
         }
      } else {
         return false;
      }
   }

   public static boolean areItemStacksEqual(ItemStack par0ItemStack, ItemStack par1ItemStack) {
      return areItemStacksEqual(par0ItemStack, par1ItemStack, false, false, false, false);
   }

   public static boolean areItemStacksEqual(ItemStack par0ItemStack, ItemStack par1ItemStack, boolean ignore_stack_size) {
      return areItemStacksEqual(par0ItemStack, par1ItemStack, ignore_stack_size, false, false, false);
   }

   public static boolean areItemStacksEqual(ItemStack par0ItemStack, ItemStack par1ItemStack, boolean ignore_stack_size, boolean ignore_quality) {
      return areItemStacksEqual(par0ItemStack, par1ItemStack, ignore_stack_size, ignore_quality, false, false);
   }

   public static boolean areItemStacksEqual(ItemStack par0ItemStack, ItemStack par1ItemStack, boolean ignore_stack_size, boolean ignore_quality, boolean ignore_damage_but_not_subtype) {
      return areItemStacksEqual(par0ItemStack, par1ItemStack, ignore_stack_size, ignore_quality, ignore_damage_but_not_subtype, false);
   }

   public static boolean areItemStacksEqual(ItemStack par0ItemStack, ItemStack par1ItemStack, boolean ignore_stack_size, boolean ignore_quality, boolean ignore_damage_but_not_subtype, boolean ignore_tag_compound) {
      if (par0ItemStack == par1ItemStack) {
         return true;
      } else {
         return par0ItemStack != null && par1ItemStack != null ? par0ItemStack.isItemStackEqual(par1ItemStack, ignore_stack_size, ignore_quality, ignore_damage_but_not_subtype, ignore_tag_compound) : false;
      }
   }

   public boolean isItemEqual(ItemStack par1ItemStack) {
      return this.isItemStackEqual(par1ItemStack, true, true, true, true);
   }

   public boolean isItemStackEqual(ItemStack par1ItemStack, boolean ignore_stack_size, boolean ignore_quality, boolean ignore_damage_but_not_subtype, boolean ignore_tag_compound) {
      if (par1ItemStack == this) {
         return true;
      } else if (this.itemID != par1ItemStack.itemID) {
         return false;
      } else if (this.getItemSubtype() != par1ItemStack.getItemSubtype()) {
         if (!this.getHasSubtypes()) {
            Minecraft.setErrorMessage("isItemStackEqual: subtypes are different but item does not have subtypes");
         }

         return false;
      } else {
         if (this.getItemDamage() != par1ItemStack.getItemDamage()) {
            if (!this.isItemStackDamageable()) {
               Minecraft.setErrorMessage("isItemStackEqual: damages are different but item is not damageable");
            }

            if (!ignore_damage_but_not_subtype) {
               return false;
            }
         }

         if (!ignore_quality && par1ItemStack.getQuality() != this.getQuality()) {
            return false;
         } else if (par1ItemStack.is_artifact != this.is_artifact) {
            return false;
         } else if (!ignore_stack_size && this.stackSize != par1ItemStack.stackSize) {
            return false;
         } else {
            return ignore_tag_compound || areItemStackTagsEqual(this, par1ItemStack);
         }
      }
   }

   public String getUnlocalizedName() {
      return Item.itemsList[this.itemID].getUnlocalizedName(this);
   }

   public static ItemStack copyItemStack(ItemStack par0ItemStack) {
      return par0ItemStack == null ? null : par0ItemStack.copy();
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.stackSize + "x" + Item.itemsList[this.itemID].getUnlocalizedName() + "[" + this.itemID + ":" + this.subtype + "]");
      if (this.isItemStackDamageable()) {
         sb.append("@" + this.damage + "/" + this.getMaxDamage());
      }

      return sb.toString();
   }

   public void updateAnimation(World par1World, Entity par2Entity, int par3, boolean par4) {
      if (this.animationsToGo > 0) {
         --this.animationsToGo;
      }

      Item.itemsList[this.itemID].onUpdate(this, par1World, par2Entity, par3, par4);
   }

   public void onCrafting(World par1World, EntityPlayer par2EntityPlayer, int par3) {
      par2EntityPlayer.addStat(StatList.objectCraftStats[this.itemID], par3);
      Item.itemsList[this.itemID].onCreated(this, par1World, par2EntityPlayer);
   }

   public int getMaxItemUseDuration() {
      return this.getItem().getMaxItemUseDuration(this);
   }

   public EnumItemInUseAction getItemInUseAction(EntityPlayer player) {
      return this.getItem().getItemInUseAction(this, player);
   }

   public void onPlayerStoppedUsing(World par1World, EntityPlayer par2EntityPlayer, int par3) {
      this.getItem().onPlayerStoppedUsing(this, par1World, par2EntityPlayer, par3);
   }

   public boolean hasTagCompound() {
      return this.stackTagCompound != null;
   }

   public NBTTagCompound getTagCompound() {
      return this.stackTagCompound;
   }

   public NBTTagList getEnchantmentTagList() {
      return this.stackTagCompound == null ? null : (NBTTagList)this.stackTagCompound.getTag("ench");
   }

   public NBTTagList getStoredEnchantmentTagList() {
      if (this.getItem() != Item.enchantedBook) {
         Minecraft.setErrorMessage("getStoredEnchantmentTagList: called for an item that isn't an enchanted book");
         return null;
      } else {
         return this.stackTagCompound != null && this.stackTagCompound.hasKey("StoredEnchantments") ? (NBTTagList)this.stackTagCompound.getTag("StoredEnchantments") : null;
      }
   }

   public boolean hasStoredEnchantments() {
      NBTTagList stored_enchantments = this.getStoredEnchantmentTagList();
      return stored_enchantments != null && stored_enchantments.tagCount() > 0;
   }

   public boolean hasEnchantment(Enchantment enchantment, boolean exclusive) {
      NBTTagList enchantments = this.getEnchantmentTagList();
      if (enchantments == null || exclusive && enchantments.tagCount() != 1) {
         return false;
      } else {
         for(int i = 0; i < enchantments.tagCount(); ++i) {
            if (((NBTTagCompound)enchantments.tagAt(i)).getShort("id") == enchantment.effectId) {
               return true;
            }
         }

         return false;
      }
   }

   public void clearEnchantTagList() {
      if (this.stackTagCompound != null) {
         this.stackTagCompound.removeTag("ench");
      }
   }

   public ItemStack setTagCompound(NBTTagCompound par1NBTTagCompound) {
      this.stackTagCompound = par1NBTTagCompound;
      return this;
   }

   public String getDisplayName() {
      String var1 = this.getItem().getItemDisplayName(this);
      if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display")) {
         NBTTagCompound var2 = this.stackTagCompound.getCompoundTag("display");
         if (var2.hasKey("Name")) {
            var1 = var2.getString("Name");
         }
      }

      return var1;
   }

   public String getNameForReferenceFile() {
      return this.getItem().getNameForReferenceFile(this);
   }

   public String getMITEStyleDisplayName() {
      String standard_name = this.getItem().getItemDisplayName(this);
      return this.hasDisplayName() ? standard_name + " \"" + this.getDisplayName() + "\"" : standard_name;
   }

   public boolean canBeRenamed() {
      return this.getItem().canBeRenamed();
   }

   public void setItemName(String par1Str) {
      if (!this.canBeRenamed()) {
         Minecraft.setErrorMessage("setItemName: This item cannot be renamed " + this.getItem());
      } else {
         if (par1Str != null) {
            par1Str = par1Str.trim();
         }

         if (this.stackTagCompound == null) {
            this.stackTagCompound = new NBTTagCompound("tag");
         }

         if (!this.stackTagCompound.hasKey("display")) {
            this.stackTagCompound.setCompoundTag("display", new NBTTagCompound());
         }

         this.stackTagCompound.getCompoundTag("display").setString("Name", par1Str);
      }
   }

   public void func_135074_t() {
      if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display")) {
         NBTTagCompound var1 = this.stackTagCompound.getCompoundTag("display");
         var1.removeTag("Name");
         if (var1.hasNoTags()) {
            this.stackTagCompound.removeTag("display");
            if (this.stackTagCompound.hasNoTags()) {
               this.setTagCompound((NBTTagCompound)null);
            }
         }
      }

   }

   public boolean hasDisplayName() {
      return this.stackTagCompound == null ? false : (!this.stackTagCompound.hasKey("display") ? false : this.stackTagCompound.getCompoundTag("display").hasKey("Name"));
   }

   public static void addTooltipsToList(EnumChatFormatting enum_chat_formatting, String[] lines, List list) {
      if (lines != null) {
         for(int i = 0; i < lines.length; ++i) {
            list.add(enum_chat_formatting == null ? lines[i] : enum_chat_formatting + lines[i]);
         }

      }
   }

   public List getTooltip(EntityPlayer par1EntityPlayer, boolean par2, Slot slot) {
      ArrayList var3 = new ArrayList();
      Item var4 = Item.itemsList[this.itemID];
      String var5 = EnumChatFormatting.WHITE + this.getMITEStyleDisplayName();
      boolean is_map = this.itemID == Item.map.itemID;
      if (par2 && par1EntityPlayer.inCreativeMode() && !is_map) {
         String var6 = "";
         if (var5.length() > 0) {
            var5 = var5 + " (";
            var6 = ")";
         }

         if (this.getHasSubtypes()) {
            var5 = var5 + String.format("#%04d/%d%s", this.itemID, this.subtype, var6);
         } else {
            var5 = var5 + String.format("#%04d%s", this.itemID, var6);
         }

         if (this.hasSignature()) {
            var5 = var5 + " [" + this.getSignature() + "]";
         }
      } else if (!this.hasDisplayName() && is_map) {
         if (ItemMap.isBeingExtended(this)) {
            var5 = "Extended Map";
         } else {
            var5 = var5 + " #" + this.subtype;
         }
      }

      var3.add(var5);
      if (var4.hasQuality()) {
         var3.add(EnumChatFormatting.GRAY + this.getQuality().getDescriptor());
      }

      var4.addInformationBeforeEnchantments(this, par1EntityPlayer, var3, par2, slot);
      int experience_cost;
      int required_heat_level;
      int hypothetical_level;
      if (this.hasTagCompound()) {
         NBTTagList var14 = this.getEnchantmentTagList();
         if (var14 != null) {
            if (var14.tagCount() > 0) {
               var3.add("");
            }

            for(experience_cost = 0; experience_cost < var14.tagCount(); ++experience_cost) {
               required_heat_level = ((NBTTagCompound)var14.tagAt(experience_cost)).getShort("id");
               hypothetical_level = ((NBTTagCompound)var14.tagAt(experience_cost)).getShort("lvl");
               if (Enchantment.enchantmentsList[required_heat_level] != null) {
                  var3.add(EnumChatFormatting.AQUA + Enchantment.enchantmentsList[required_heat_level].getTranslatedName(hypothetical_level, this));
               }
            }
         }
      }

      var4.addInformation(this, par1EntityPlayer, var3, par2, slot);
      if (this.hasTagCompound() && this.stackTagCompound.hasKey("display")) {
         NBTTagCompound var17 = this.stackTagCompound.getCompoundTag("display");
         if (var17.hasKey("color") && par2) {
            var3.add("");
            var3.add("Dyed Color: #" + Integer.toHexString(var17.getInteger("color")).toUpperCase());
         }

         if (var17.hasKey("Lore")) {
            NBTTagList var19 = var17.getTagList("Lore");
            if (var19.tagCount() > 0) {
               for(required_heat_level = 0; required_heat_level < var19.tagCount(); ++required_heat_level) {
                  var3.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + ((NBTTagString)var19.tagAt(required_heat_level)).data);
               }
            }
         }
      }

      Multimap var16 = this.getAttributeModifiers();
      if (par2 && !var16.isEmpty()) {
         var3.add("");
         Iterator var15 = var16.entries().iterator();

         while(var15.hasNext()) {
            Map.Entry var18 = (Map.Entry)var15.next();
            AttributeModifier var21 = (AttributeModifier)var18.getValue();
            double var10 = var21.getAmount();
            double var12;
            if (var21.getOperation() != 1 && var21.getOperation() != 2) {
               var12 = var21.getAmount();
            } else {
               var12 = var21.getAmount() * 100.0;
            }

            if (var10 > 0.0) {
               var3.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + var21.getOperation(), field_111284_a.format(var12), StatCollector.translateToLocal("attribute.name." + (String)var18.getKey())));
            } else if (var10 < 0.0) {
               var12 *= -1.0;
               var3.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + var21.getOperation(), field_111284_a.format(var12), StatCollector.translateToLocal("attribute.name." + (String)var18.getKey())));
            }
         }
      }

      if (par2 && var4 instanceof ItemTool) {
         ItemTool tool = (ItemTool)var4;
         if (tool.getToolMaterial() == Material.silver) {
            var3.add(EnumChatFormatting.WHITE + Translator.get("item.tooltip.bonusVsUndead"));
         }
      }

      if (par2 && this.getQuality() != null) {
         float modifier = this.getQuality().getDurabilityModifier();
         if (modifier < 1.0F) {
            var3.add(EnumChatFormatting.RED + Translator.getFormatted("item.tooltip.durabilityPenalty", (int)((1.0F - modifier) * 100.0F)));
         } else if (modifier > 1.0F) {
            var3.add(EnumChatFormatting.BLUE + Translator.getFormatted("item.tooltip.durabilityBonus", (int)((modifier - 1.0F) * 100.0F)));
         }
      }

      ForgeEventFactory.onItemTooltip(this, par1EntityPlayer, var3, par2);

      if (this.isArtifact()) {
         var3.add("");
         var3.add(EnumChatFormatting.AQUA + "Artifact");
      }

      if (this.hasTagCompound() && par2 && this.stackTagCompound.hasKey("flavor_text")) {
         String text = this.stackTagCompound.getString("flavor_text");
         List text_lines = Minecraft.theMinecraft.fontRenderer.listFormattedStringToWidth(text, 120);
         var3.add("");

         for(hypothetical_level = 0; hypothetical_level < text_lines.size(); ++hypothetical_level) {
            var3.add(EnumChatFormatting.DARK_GRAY + "" + EnumChatFormatting.ITALIC + (String)text_lines.get(hypothetical_level));
         }
      }

      if (par2 && (Minecraft.theMinecraft.gameSettings.advancedItemTooltips || par1EntityPlayer.inCreativeMode()) && this.isItemStackDamageable()) {
         var3.add("");
         if (this.isItemDamaged()) {
            var3.add(Translator.get("item.tooltip.durability") + " " + (this.getMaxDamage() - this.getItemDamageForDisplay()) + " / " + this.getMaxDamage());
         } else {
            var3.add(Translator.get("item.tooltip.durability") + " " + this.getMaxDamage());
         }
      }

      if (slot instanceof SlotCrafting) {
         experience_cost = ((EntityClientPlayerMP)par1EntityPlayer).crafting_experience_cost;
         if (experience_cost == 0 && par1EntityPlayer.getAsEntityClientPlayerMP().crafting_experience_cost_tentative > 0) {
            experience_cost = par1EntityPlayer.getAsEntityClientPlayerMP().crafting_experience_cost_tentative;
         }

         SlotCrafting slot_crafting = (SlotCrafting)slot;
         if (experience_cost == 0 && slot_crafting.getNumCraftingResults(par1EntityPlayer) > 1) {
            var3.add("");
            Item item = this.getItem();
            if (item.hasQuality()) {
               Translator.addToList(EnumChatFormatting.YELLOW, "container.crafting.differentQuality", var3);
            } else if (item instanceof ItemRunestone) {
               Translator.addToList(EnumChatFormatting.YELLOW, "container.crafting.differentRunestone", var3);
            }
         } else if (experience_cost > 0) {
            hypothetical_level = par1EntityPlayer.getExperienceLevel(par1EntityPlayer.experience - experience_cost);
            int level_cost = par1EntityPlayer.getExperienceLevel() - hypothetical_level;
            var3.add("");
            if (level_cost == 0) {
               Translator.addToList(EnumChatFormatting.YELLOW, "container.crafting.qualityCostLessThanOneLevel", var3);
            } else if (level_cost == 1) {
               Translator.addToList(EnumChatFormatting.YELLOW, "container.crafting.qualityCostOneLevel", var3);
            } else {
               Translator.addToListFormatted(EnumChatFormatting.YELLOW, "container.crafting.qualityCostMoreThanOneLevel", var3, level_cost);
            }
         }
      } else if (slot != null && slot.inventory instanceof TileEntityFurnace) {
         TileEntityFurnace tile_entity_furnace = (TileEntityFurnace)slot.inventory;
         if (tile_entity_furnace.getStackInSlot(0) == this) {
            required_heat_level = TileEntityFurnace.getHeatLevelRequired(this.itemID);
            hypothetical_level = tile_entity_furnace.heat_level > 0 ? tile_entity_furnace.heat_level : tile_entity_furnace.getFuelHeatLevel();
            if (hypothetical_level > 0 && hypothetical_level < required_heat_level) {
               var3.add(EnumChatFormatting.GOLD + Translator.get("container.furnace.needsMoreHeat"));
            }
         }
      }

      return var3;
   }

   public boolean hasEffect() {
      return this.hasEffect(0);
   }

   public boolean hasEffect(int pass)
   {
      return this.getItem().hasEffect(this, pass);
   }

   public EnumRarity getRarity() {
      return this.getItem().getRarity(this);
   }

   public boolean isEnchantable() {
      if (this.getItem() == Item.book) {
         return true;
      } else if (!ItemPotion.isBottleOfWater(this) && !ItemAppleGold.isUnenchantedGoldenApple(this)) {
         if (this.getMaxStackSize() != 1) {
            return false;
         } else if (!this.isItemStackDamageable()) {
            return false;
         } else {
            return this.getItem().getItemEnchantability() > 0 && !this.isItemEnchanted();
         }
      } else {
         return true;
      }
   }

   public void addEnchantment(Enchantment par1Enchantment, int par2) {
      if (this.stackTagCompound == null) {
         this.setTagCompound(new NBTTagCompound());
      }

      if (!this.stackTagCompound.hasKey("ench")) {
         this.stackTagCompound.setTag("ench", new NBTTagList("ench"));
      }

      NBTTagList var3 = (NBTTagList)this.stackTagCompound.getTag("ench");
      NBTTagCompound var4 = new NBTTagCompound();
      var4.setShort("id", (short)par1Enchantment.effectId);
      var4.setShort("lvl", (short)((byte)par2));
      var3.appendTag(var4);
   }

   public boolean isItemEnchanted() {
      return this.stackTagCompound != null && this.stackTagCompound.hasKey("ench");
   }

   public void setTagInfo(String par1Str, NBTBase par2NBTBase) {
      if (this.stackTagCompound == null) {
         this.setTagCompound(new NBTTagCompound());
      }

      this.stackTagCompound.setTag(par1Str, par2NBTBase);
   }

   public boolean canEditBlocks() {
      return this.getItem().canItemEditBlocks();
   }

   public boolean isOnItemFrame() {
      return this.itemFrame != null;
   }

   public void setItemFrame(EntityItemFrame par1EntityItemFrame) {
      this.itemFrame = par1EntityItemFrame;
   }

   public EntityItemFrame getItemFrame() {
      return this.itemFrame;
   }

   public int getRepairCost() {
      return this.getItem().getRepairCost();
   }

   public boolean hasRepairCost() {
      return this.getItem().hasRepairCost();
   }

   public Multimap getAttributeModifiers() {
      Object var1;
      if (this.hasTagCompound() && this.stackTagCompound.hasKey("AttributeModifiers")) {
         var1 = HashMultimap.create();
         NBTTagList var2 = this.stackTagCompound.getTagList("AttributeModifiers");

         for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            AttributeModifier var5 = SharedMonsterAttributes.func_111259_a(var4);
            if (var5.getID().getLeastSignificantBits() != 0L && var5.getID().getMostSignificantBits() != 0L) {
               ((Multimap)var1).put(var4.getString("AttributeName"), var5);
            }
         }
      } else {
         var1 = this.getItem().getItemAttributeModifiers();
      }

      return (Multimap)var1;
   }

   public ItemStack setStackSize(int stack_size) {
      this.stackSize = stack_size;
      return this;
   }

   public static void decrementStackSize(ItemStack item_stack) {
      if (item_stack != null && item_stack.stackSize > 0) {
         --item_stack.stackSize;
      }

   }

   private ItemStack applyRandomItemStackDamageForMob(EntityLiving owner) {
      if (this.isItemStackDamageable()) {
         Item item = this.getItem();
         if (item.hasQuality() && this.getQuality().isAverageOrHigher() && !this.isItemEnchanted()) {
            Material material = null;
            if (item instanceof ItemTool) {
               material = ((ItemTool)item).getToolMaterial();
            } else if (item instanceof ItemArmor) {
               material = ((ItemArmor)item).getArmorMaterial();
            } else if (item.materials.size() == 1) {
               material = item.getExclusiveMaterial();
            }

            if (material == Material.wood || material == Material.leather) {
               this.setQuality(this.getQuality().getNextLower());
            }
         }

         float fraction_damaged = (this.isItemEnchanted() ? 0.5F : 0.7F) + 0.3F * owner.rand.nextFloat();
         int damage = (int)((float)this.getMaxDamage() * fraction_damaged);
         if (damage >= this.getMaxDamage()) {
            damage = this.getMaxDamage() - 1;
         }

         this.setItemDamage(damage);
      }

      return this;
   }

   public ItemStack applyRandomItemStackDamageForChest() {
      if (this.isItemStackDamageable()) {
         int fraction_damaged = this.hasMaterial(Material.rusted_iron) ? (int)(3.0 + Math.random() * 7.0) : (int)(Math.random() * 7.0);
         int damage = this.getMaxDamage() * fraction_damaged / 10;
         if (damage >= this.getMaxDamage()) {
            damage = this.getMaxDamage() - 1;
         }

         this.setItemDamage(damage);
      }

      return this;
   }

   public ItemStack randomizeForMob(EntityLiving owner, boolean may_be_enchanted) {
      if (may_be_enchanted) {
         owner.enchantEquipment(this);
      }

      this.applyRandomItemStackDamageForMob(owner);
      return this;
   }

   public boolean isRepairItem() {
      return this.getItem() instanceof ItemNugget;
   }

   public Item getRepairItem() {
      return this.getItem().getRepairItem();
   }

   public boolean hasMaterial(Material material) {
      return this.hasMaterial(material, false);
   }

   public boolean hasMaterial(Material material, boolean exclusively) {
      return this.getItem().hasMaterial(material, exclusively);
   }

   public int getExperienceReward(int quantity) {
      return this.getItem().getExperienceReward(this.getItemSubtype()) * quantity;
   }

   public int getExperienceReward() {
      return this.getExperienceReward(this.stackSize);
   }

   public ItemStack setQuality(EnumQuality quality) {
      Item item = this.getItem();
      if (item == null) {
         Minecraft.setErrorMessage("setQuality: item_stack.getItem()==null");
         return this;
      } else {
         if (item.hasQuality()) {
            if (quality == null) {
               quality = item.getDefaultQuality();
            } else if (quality.isHigherThan(item.getMaxQuality())) {
               Minecraft.setErrorMessage("setQuality: quality is higher than item's max quality (" + this.getDisplayName() + ")");
               quality = item.getMaxQuality();
            }

            this.quality = quality;
         } else if (quality != null) {
            Minecraft.setErrorMessage("setQuality: item \"" + this.getItem().getItemDisplayName(this) + "\" does not have quality");
            return this;
         }

         return this;
      }
   }

   public EnumQuality getQuality() {
      if (!this.getItem().hasQuality()) {
         this.quality = null;
      } else if (this.quality == null) {
         this.quality = this.getItem().getDefaultQuality();
      }

      return this.quality;
   }

   public int calcChecksum(int for_release_number) {
      int checksum = this.itemID * this.stackSize;
      if (for_release_number > 80) {
         checksum += this.getQuality() == null ? 0 : this.getQuality().ordinal() * 17;
      }

      if (for_release_number > 120) {
         checksum += this.is_artifact ? 531 : 0;
      }

      checksum *= 3;
      return checksum;
   }

   public void setAsComponentOfCraftingProduct(ItemStack crafting_product) {
      this.getItem().setAsComponentOfCraftingProduct(this.getHasSubtypes() ? this.getItemSubtype() : 0, crafting_product);
   }

   public Material getMaterialForRepairs() {
      return this.getItem().getMaterialForRepairs();
   }

   public int getEnchantmentLevel(Enchantment enchantment) {
      return EnchantmentHelper.getEnchantmentLevel(enchantment.effectId, this);
   }

   public float getEnchantmentLevelFraction(Enchantment enchantment) {
      return EnchantmentHelper.getEnchantmentLevelFraction(enchantment, this);
   }

   public int getEnchantmentLevelFractionOfInteger(Enchantment enchantment, int integer) {
      return EnchantmentHelper.getEnchantmentLevelFractionOfInteger(enchantment, this, integer);
   }

   public boolean canDouseFire() {
      return this.getItem().canDouseFire();
   }

   public boolean canCatchFire() {
      return this.getItem().canCatchFire();
   }

   public boolean canBurnAsFuelSource() {
      return this.getItem().canBurnAsFuelSource();
   }

   public boolean isHarmedByFire() {
      return this.getItem().isHarmedByFire();
   }

   public boolean isHarmedByLava() {
      return this.getItem().isHarmedByLava();
   }

   public boolean tryPlaceAsBlock(RaycastCollision rc, Block block, EntityPlayer player) {
      return this.getItem().tryPlaceAsBlock(rc, block, player, this);
   }

   public boolean hasIngestionPriority(boolean ctrl_is_down) {
      return this.getItem().hasIngestionPriority(this, ctrl_is_down);
   }

   public boolean tryEntityInteraction(Entity entity, EntityPlayer player) {
      return this.getItem().tryEntityInteraction(entity, player, this);
   }

   public boolean isIngestable() {
      return this.getItem().isIngestable(this);
   }

   public boolean isAlwaysEdible() {
      return this.getItem().isAlwaysEdible();
   }

   public int getSatiation(EntityPlayer player) {
      return this.getItem().getSatiation(player);
   }

   public int getNutrition() {
      return this.getItem().getNutrition();
   }

   public ItemStack subjectToChanceOfDisappearing(float chance, Random random) {
      if (chance > 0.0F) {
         int num_disappeared = 0;

         for(int i = 0; i < this.stackSize; ++i) {
            if (random.nextFloat() < chance) {
               ++num_disappeared;
            }
         }

         this.stackSize -= num_disappeared;
      }

      return this;
   }

   public boolean isDyed() {
      Item item = this.getItem();
      if (item instanceof ItemBlock) {
         ItemBlock item_block = (ItemBlock)item;
         return item_block.getBlock() instanceof BlockColored;
      } else if (item instanceof ItemArmor) {
         ItemArmor item_armor = (ItemArmor)item;
         return item_armor.hasColor(this);
      } else {
         return false;
      }
   }

   public int getDyedColor() {
      Item item = this.getItem();
      if (item instanceof ItemBlock) {
         ItemBlock item_block = (ItemBlock)item;
         if (item_block.getBlock() instanceof BlockColored) {
            return this.getItemSubtype();
         }
      } else if (item instanceof ItemArmor) {
         ItemArmor item_armor = (ItemArmor)item;
         return item_armor.getColor(this);
      }

      Minecraft.setErrorMessage("getDyedColor: don't know how to handle " + this);
      return -1;
   }

   public ItemStack copyDyedColor(ItemStack item_stack) {
      if (item_stack.getItem() != this.getItem()) {
         Minecraft.setErrorMessage("copyDyedColor: items aren't the same");
         return this;
      } else {
         Item item = this.getItem();
         if (item instanceof ItemBlock) {
            ItemBlock item_block = (ItemBlock)item;
            if (item_block.getBlock() instanceof BlockColored) {
               this.setItemSubtype(item_stack.getItemSubtype());
               return this;
            }
         } else if (item instanceof ItemArmor) {
            ItemArmor item_armor = (ItemArmor)item;
            item_armor.func_82813_b(this, item_armor.getColor(item_stack));
            return this;
         }

         Minecraft.setErrorMessage("copyDyedColor: don't know how to handle " + this);
         return this;
      }
   }

   public boolean isBlock() {
      return this.getItem().isBlock();
   }

   public ItemBlock getItemAsBlock() {
      return this.getItem().getAsItemBlock();
   }

   public boolean isTool() {
      return this.getItem().isTool();
   }

   public ItemTool getItemAsTool() {
      return this.getItem().getAsTool();
   }

   public boolean isArmor() {
      return this.getItem().isArmor();
   }

   public boolean isChainMail() {
      return this.getItem().isChainMail();
   }

   public ItemArmor getItemAsArmor() {
      return this.getItem().getAsArmor();
   }

   public ItemStack setAsArtifact() {
      this.is_artifact = true;
      return this;
   }

   public boolean isArtifact() {
      return this.is_artifact;
   }

   private int checkSignature(int signature, int min, int max) {
      if ((signature < min || signature > max) && signature != 0) {
         Minecraft.setErrorMessage("getSignature: invalid signature for " + this + " (" + signature + "), min=" + min + ", max=" + max);
      }

      return signature;
   }

   public int getSignature(boolean suppress_errors) {
      int signature = 0;
      if (ItemReferencedBook.isReferencedBook(this)) {
         signature = this.checkSignature(ItemReferencedBook.getSignature(this), 1, 100);
      } else if (ItemRecord.isUniqueRecord(this)) {
         signature = this.checkSignature(ItemRecord.getSignature(this), 101, 200);
      }

      if (signature == 0 && !suppress_errors) {
         Minecraft.setErrorMessage("getSignature: unhandled case " + this);
      }

      return signature;
   }

   public int getSignature() {
      return this.getSignature(false);
   }

   public boolean hasSignature() {
      return this.getSignature(true) > 0;
   }

   public boolean hasSignatureThatHasBeenAddedToWorld(World world) {
      return this.hasSignature() && world.worldInfo.hasSignatureBeenAdded(this.getSignature());
   }

   public void addSignatureToTheWorld(World world) {
      world.worldInfo.addSignature(this.getSignature());
   }

   public float getCraftingDifficultyAsComponent() {
      return this.getItem().getCraftingDifficultyAsComponent(this);
   }

   public boolean isHarmedByPepsin() {
      return this.getItem().isHarmedByPepsin();
   }

   public boolean isHarmedByAcid() {
      return this.getItem() == Item.dyePowder && this.getItemSubtype() == 4 ? false : this.getItem().isHarmedByAcid();
   }

   public boolean isHarmedBy(DamageSource damage_source) {
      return damage_source.isAcidDamage() && !this.isHarmedByAcid() ? false : this.getItem().isHarmedBy(damage_source);
   }

   public int getScaledDamage(float damage) {
      return this.getItem().getScaledDamage(damage);
   }

   public float getMeleeDamageBonus() {
      return this.getItem().getMeleeDamageBonus();
   }

   public boolean canBeCompostedByWorms() {
      return this.getItem() != null && this.stackSize > 0 && this.getItem().canBeCompostedByWorms(this);
   }
}
