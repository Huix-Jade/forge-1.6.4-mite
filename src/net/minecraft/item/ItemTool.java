package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.mite.Skill;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumItemInUseAction;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class ItemTool extends Item implements IDamageableItem {
   private Material effective_material;
   protected List materials_effective_against = new ArrayList();
   protected List blocks_effective_against = new ArrayList();
   protected float damageVsEntity;
   protected static Class[] weapon_classes = new Class[]{ItemSword.class, ItemBattleAxe.class, ItemWarHammer.class, ItemDagger.class, ItemClub.class, ItemPickaxe.class, ItemCudgel.class, ItemKnife.class};

   protected ItemTool(int par1, Material material) {
      super(par1, (Material)material, (String)null);
      if (this.hasWoodenHandle() && !this.hasMaterial(Material.wood)) {
         this.addMaterial(new Material[]{Material.wood});
      }

      this.effective_material = material;
      this.setTextureName("tools/" + material.name + "_" + this.getToolType());
      this.setMaxStackSize(1);
      this.setMaxDamage(this.getMultipliedDurability());
      this.damageVsEntity = this.getCombinedDamageVsEntity();
      this.setCreativeTab(CreativeTabs.tabTools);
      this.setReachBonus(0.75F);
      this.setSkillsetThatCanRepairThis(this.hasMaterial(Material.wood, true) ? Skill.CARPENTRY.id : (this.containsRockyMineral() ? Skill.MASONRY.id : (this.containsMetal() ? Skill.BLACKSMITHING.id : -1)));
   }

   public final float getBaseDurability() {
      return 4.0F;
   }

   public final int getMultipliedDurability() {
      return (int)(this.getBaseDurability() * (float)this.getNumComponentsForDurability() * this.effective_material.durability * 100.0F);
   }

   public float getBaseHarvestEfficiency(Block block) {
      return 4.0F;
   }

   public abstract float getBaseDamageVsEntity();

   public float getCombinedDamageVsEntity() {
      return this.getBaseDamageVsEntity() + this.getMaterialDamageVsEntity();
   }

   public boolean canBlock() {
      return true;
   }

   public final float getMultipliedHarvestEfficiency(Block block) {
      return this.getBaseHarvestEfficiency(block) * this.getMaterialHarvestEfficiency();
   }

   public int getMaterialHarvestLevel() {
      return this.effective_material.min_harvest_level == 0 ? 0 : (this.effective_material.isMetal() ? this.effective_material.min_harvest_level : this.effective_material.min_harvest_level - 1);
   }

   public float getMaterialHarvestEfficiency() {
      if (this.effective_material == Material.wood) {
         return 1.0F;
      } else if (this.effective_material == Material.flint) {
         return 1.25F;
      } else if (this.effective_material == Material.obsidian) {
         return 1.5F;
      } else if (this.effective_material == Material.rusted_iron) {
         return 1.25F;
      } else if (this.effective_material == Material.copper) {
         return 1.75F;
      } else if (this.effective_material == Material.silver) {
         return 1.75F;
      } else if (this.effective_material == Material.gold) {
         return 1.75F;
      } else if (this.effective_material == Material.iron) {
         return 2.0F;
      } else if (this.effective_material == Material.mithril) {
         return 2.5F;
      } else if (this.effective_material == Material.adamantium) {
         return 3.0F;
      } else if (this.effective_material == Material.diamond) {
         return 2.5F;
      } else if (this.effective_material == Material.ancient_metal) {
         return 2.0F;
      } else {
         Minecraft.setErrorMessage("getMaterialHarvestEfficiency: tool material not handled");
         return 0.0F;
      }
   }

   public float getMaterialDamageVsEntity() {
      return this.effective_material.getDamageVsEntity();
   }

   public void addMaterialsEffectiveAgainst(Material[] materials_effective_against) {
      for(int i = 0; i < materials_effective_against.length; ++i) {
         if (!this.materials_effective_against.contains(materials_effective_against[i])) {
            this.materials_effective_against.add(materials_effective_against[i]);
         }
      }

   }

   public void addBlocksEffectiveAgainst(Block[] blocks_effective_against) {
      for(int i = 0; i < blocks_effective_against.length; ++i) {
         if (!this.blocks_effective_against.contains(blocks_effective_against[i])) {
            this.blocks_effective_against.add(blocks_effective_against[i]);
         }
      }

   }

   public boolean isEffectiveAgainstBlock(Block block, int metadata) {
      if (block instanceof BlockSlab) {
         Block model_block = ((BlockSlab)block).getModelBlock(metadata);
         if (model_block == Block.sandStone && this instanceof ItemAxe) {
            return true;
         }
      }

      return (this.materials_effective_against.contains(block.blockMaterial) || this.blocks_effective_against.contains(block)) && this.getMaterialHarvestLevel() >= block.getMinHarvestLevel(metadata);
   }

   public final float getStrVsBlock(Block block, int metadata) {
      return this.isEffectiveAgainstBlock(block, metadata) ? this.getMultipliedHarvestEfficiency(block) : super.getStrVsBlock(block, metadata);
   }

   public abstract float getBaseDecayRateForBreakingBlock(Block var1);

   public abstract float getBaseDecayRateForAttackingEntity(ItemStack var1);

   public final int getToolDecayFromBreakingBlock(BlockBreakInfo info) {
      float block_hardness = info.getBlockHardness();
      if (block_hardness == 0.0F) {
         return 0;
      } else {
         float decay = 100.0F * this.getBaseDecayRateForBreakingBlock(info.block);
         return Math.max(Math.max((int)(block_hardness * decay), (int)(decay / 20.0F)), 1);
      }
   }

   public final int getToolDecayFromAttackingEntity(ItemStack item_stack, EntityLivingBase entity_living_base) {
      return Math.max((int)(100.0F * this.getBaseDecayRateForAttackingEntity(item_stack)), 1);
   }

   public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase) {
      if (par2EntityLivingBase.onClient()) {
         Minecraft.setErrorMessage("ItemTool.hitEntity: called on client?");
      }

      if (par3EntityLivingBase instanceof EntityPlayer && ((EntityPlayer)par3EntityLivingBase).capabilities.isCreativeMode) {
         return false;
      } else {
         par1ItemStack.tryDamageItem(DamageSource.generic, this.getToolDecayFromAttackingEntity(par1ItemStack, par2EntityLivingBase), par3EntityLivingBase);
         return true;
      }
   }

   public boolean onBlockDestroyed(BlockBreakInfo info) {
      if (info.world.isRemote) {
         Minecraft.setErrorMessage("ItemTool.onBlockDestroyed: called on client?");
      }

      Block block = info.block;
      ItemStack item_stack = info.getHarvesterItemStack();
      if (item_stack.isItemStackDamageable() && !block.isPortable(info.world, info.getHarvester(), info.x, info.y, info.z) && !info.isResponsiblePlayerInCreativeMode() && !(info.getBlockHardness() <= 0.0F) && !(this.getStrVsBlock(block, info.getMetadata()) <= 1.0F)) {
         info.getHarvesterItemStack().tryDamageItem(DamageSource.generic, this.getToolDecayFromBreakingBlock(info), info.getHarvester());
         return true;
      } else {
         return false;
      }
   }

   public boolean isFull3D() {
      return true;
   }

   public EnumItemInUseAction getItemInUseAction(ItemStack par1ItemStack, EntityPlayer player) {
      return EnumItemInUseAction.BLOCK;
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 72000;
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      if (this.canBlock()) {
         player.setHeldItemInUse();
         return true;
      } else {
         return false;
      }
   }

   public int getItemEnchantability() {
      return this.getMaterialForEnchantment().enchantability;
   }

   public String getToolMaterialName() {
      return this.getToolMaterial().name;
   }

   public Multimap getItemAttributeModifiers() {
      Multimap var1 = super.getItemAttributeModifiers();
      var1.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Tool modifier", (double)this.damageVsEntity, 0));
      return var1;
   }

   public Material getToolMaterial() {
      return this.effective_material;
   }

   public Material getMaterialForEnchantment() {
      return this.getToolMaterial();
   }

   public boolean similarToItemsOfSameClass() {
      return true;
   }

   public abstract String getToolType();

   public boolean hasQuality() {
      return true;
   }

   public boolean hasWoodenHandle() {
      return true;
   }

   public int getBurnTime(ItemStack item_stack) {
      return this.effective_material == Material.wood ? 200 : (this.hasMaterial(Material.wood) ? 100 : 0);
   }

   public Material getMaterialForDurability() {
      return this.getToolMaterial();
   }

   public final int getRepairCost() {
      return this.getNumComponentsForDurability() * 2;
   }

   public float getMeleeDamageBonus() {
      return this.getCombinedDamageVsEntity();
   }

   /** FORGE: Overridden to allow custom tool effectiveness */
   @Override
   public float getStrVsBlock(ItemStack stack, Block block, int meta)
   {
      if (ForgeHooks.isToolEffective(stack, block, meta))
      {
         return getMultipliedHarvestEfficiency(block);
      }
      return getStrVsBlock(block, meta);
   }
}
