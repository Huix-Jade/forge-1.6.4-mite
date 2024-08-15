package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.mite.Skill;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Translator;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemBlock extends Item {
   private int blockID;
   private Icon field_94588_b;

   public ItemBlock(Block block) {
      super(block.blockID - 256, (String)null, block.getNumSubBlocks());
      this.blockID = block.blockID;
      this.getBlock().addItemBlockMaterials(this);
   }

   public int getBlockID() {
      return this.blockID;
   }

   public int getSpriteNumber() {
      return Block.blocksList[this.blockID].getItemIconName() != null ? 1 : 0;
   }

   public Icon getIconFromSubtype(int par1) {
      return this.field_94588_b != null ? this.field_94588_b : Block.blocksList[this.blockID].getBlockTextureFromSide(1);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      if (player.inCreativeMode() && ctrl_is_down && this.itemID == Block.web.blockID) {
         if (player.onServer()) {
            if (!player.inCreativeMode()) {
               player.convertOneOfHeldItem((ItemStack)null);
               player.addHungerServerSide(0.25F * EnchantmentHelper.getEnduranceModifier(player));
            }

            WorldServer world = player.getWorldServer();
            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            world.spawnEntityInWorld(new EntityWeb(world, player));
         } else {
            player.bobItem();
         }

         return true;
      } else {
         RaycastCollision rc = player.getSelectedObject(partial_tick, false);
         if (rc != null && rc.isBlock()) {
            if (player.worldObj.areSkillsEnabled() && this.getBlock() instanceof BlockMushroom && !player.hasSkill(Skill.FARMING)) {
               return false;
            } else {
               return player.onClient() && System.currentTimeMillis() < player.getAsEntityClientPlayerMP().prevent_block_placement_due_to_picking_up_held_item_until ? false : player.tryPlaceHeldItemAsBlock(rc, Block.getBlock(this.blockID));
            }
         } else {
            return false;
         }
      }
   }

   public String getUnlocalizedName(ItemStack par1ItemStack) {
      return Block.blocksList[this.blockID].getUnlocalizedName();
   }

   public String getUnlocalizedName() {
      return Block.blocksList[this.blockID].getUnlocalizedName();
   }

   public CreativeTabs getCreativeTab() {
      return Block.blocksList[this.blockID].getCreativeTabToDisplayOn();
   }

   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
      Block.blocksList[this.blockID].getItemStacks(par1, par2CreativeTabs, par3List);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      String var2 = Block.blocksList[this.blockID].getItemIconName();
      if (var2 != null) {
         this.field_94588_b = par1IconRegister.registerIcon(var2);
      }

   }

   public Block getBlock() {
      return Block.blocksList[this.getBlockID()];
   }

   public void onItemUseFinish(ItemStack item_stack, World world, EntityPlayer player) {
      Block block = this.getBlock();
      if (player.onServer() && this.isIngestable(item_stack)) {
         player.addFoodValue(this);
         if (this.isEatable(item_stack)) {
            world.playSoundAtEntity(player, "random.burp", 0.5F, player.rand.nextFloat() * 0.1F + 0.9F);
         }

         this.onEaten(item_stack, world, player);
      }

      super.onItemUseFinish(item_stack, world, player);
   }

   protected void onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer player) {
      if (player.onServer() && this.getBlock() == Block.mushroomRed) {
         player.addPotionEffect(new PotionEffect(Potion.poison.id, 200, 0));
         player.addPotionEffect(new PotionEffect(Potion.confusion.id, 1200, 4));
      }

   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 32;
   }

   public boolean isEatable(int item_subtype) {
      return this.getBlock() instanceof BlockMushroom || this.getBlock() instanceof BlockCake;
   }

   public int getSimilarityToItem(Item item) {
      return this.getBlock() == Block.gravel && item instanceof ItemShovel ? 1 : 0;
   }

   public String getNameDisambiguationForReferenceFile(int subtype) {
      return this.getBlock().getNameDisambiguationForReferenceFile(subtype);
   }

   public int getBurnTime(ItemStack item_stack) {
      if (!this.canBurnAsFuelSource()) {
         return 0;
      } else {
         Block block = this.getBlock();
         if (block == Block.wood) {
            return 1600;
         } else if (block != Block.planks && block != Block.woodDoubleSlab && block != Block.woodenButton) {
            if (block != Block.woodSingleSlab && block != Block.sapling && block != Block.deadBush) {
               if (block != Block.torchWood && !(block instanceof BlockRedstoneTorch)) {
                  if (block.blockMaterial == Material.wood) {
                     return 400;
                  } else {
                     return block == Block.coalBlock ? 16000 : 0;
                  }
               } else {
                  return 800;
               }
            } else {
               return 200;
            }
         } else {
            return 400;
         }
      }
   }

   public int getHeatLevel(ItemStack item_stack) {
      Block block = this.getBlock();
      return block == Block.coalBlock ? 2 : super.getHeatLevel(item_stack);
   }

   public boolean canCatchFire() {
      Block block = this.getBlock();
      return block instanceof BlockTorch ? true : block.blockMaterial.canCatchFire();
   }

   public boolean canBurnAsFuelSource() {
      Block block = this.getBlock();
      if (!(block instanceof BlockTorch) && !(block instanceof BlockSapling)) {
         return block != Block.woodenButton && block != Block.deadBush ? block.blockMaterial.canBurnAsFuelSource() : true;
      } else {
         return true;
      }
   }

   public boolean isHarmedByFire() {
      Block block = this.getBlock();
      return block instanceof BlockTorch ? true : block.blockMaterial.isHarmedByFire();
   }

   public boolean isHarmedByLava() {
      Block block = this.getBlock();
      if (block instanceof BlockTorch) {
         return true;
      } else {
         return block == Block.oreAdamantium ? false : block.blockMaterial.isHarmedByLava();
      }
   }

   public boolean hasIngestionPriority(ItemStack item_stack, boolean ctrl_is_down) {
      Block block = this.getBlock();
      return !(block instanceof BlockMushroom);
   }

   public float getCraftingDifficultyAsComponent(ItemStack item_stack) {
      if (item_stack == null) {
         return super.getCraftingDifficultyAsComponent((ItemStack)null);
      } else {
         float difficulty = this.getBlock().getCraftingDifficultyAsComponent(item_stack.getItemSubtype());
         return difficulty < 0.0F ? super.getCraftingDifficultyAsComponent(item_stack) : difficulty;
      }
   }

   public void addInformation(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
      if (player.inCreativeMode() && extended_info) {
         Block block = this.getBlock();
         int metadata = item_stack.getItemSubtype();
         int min_harvest_level = block.getMinHarvestLevel(metadata);
         info.add(EnumChatFormatting.GRAY + "Hardness: " + StringHelper.formatFloat(block.getBlockHardness(metadata), 1, 2) + (min_harvest_level == 0 ? "" : " (" + min_harvest_level + ")"));
         info.add(EnumChatFormatting.GRAY + "Material: " + block.blockMaterial.getCapitalizedName());
      }

      super.addInformation(item_stack, player, info, extended_info, slot);
   }

   public ItemStack getItemStackForStatsIcon() {
      Block block = this.getBlock();
      int id = block.blockID;
      if (block == Block.flowerPot || block == Block.flowerPotMulti) {
         id = Item.flowerPot.itemID;
      }

      int subtype = 0;
      if (block == Block.tallGrass) {
         subtype = 1;
      }

      return new ItemStack(id, 1, subtype);
   }

   public String getItemDisplayName(ItemStack item_stack) {
      return item_stack != null && this.getBlock() == Block.workbench ? Translator.get("tile.toolbench." + BlockWorkbench.getToolMaterial(item_stack.getItemSubtype()).name + ".name") : super.getItemDisplayName(item_stack);
   }

   public float getCompostingValue() {
      Block block = this.getBlock();
      if (block == Block.hay) {
         return Item.wheat.getCompostingValue() * 9.0F;
      } else if (block != Block.leaves && block != Block.vine) {
         if (block != Block.melon && !(block instanceof BlockPumpkin)) {
            if (!(block instanceof BlockMushroom) && block != Block.tallGrass && block != Block.waterlily) {
               if (block instanceof BlockFlower) {
                  return 0.25F;
               } else {
                  return block == Block.cake ? 2.4F : super.getCompostingValue();
               }
            } else {
               return 0.5F;
            }
         } else {
            return 2.0F;
         }
      } else {
         return 1.0F;
      }
   }

   public Item getCompostingRemains(ItemStack item_stack) {
      Block block = this.getBlock();
      return block == Block.melon ? Item.melonSeeds : (block != Block.pumpkin && block != Block.pumpkinLantern ? null : Item.pumpkinSeeds);
   }
}
