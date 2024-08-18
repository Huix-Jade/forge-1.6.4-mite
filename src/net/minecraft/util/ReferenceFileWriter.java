package net.minecraft.util;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityCubic;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityListEntry;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemCoal;
import net.minecraft.item.ItemCudgel;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemHatchet;
import net.minecraft.item.ItemKnife;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.CraftingResult;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.mite.MITEConstant;
import net.minecraft.mite.Skill;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import org.apache.commons.io.FileUtils;

public class ReferenceFileWriter {
   private static String newline = new String(System.getProperty("line.separator").getBytes());
   public static boolean running;

   private static String formatFloat(float f) {
      return StringHelper.formatFloat(f);
   }

   private static String getBlockMaterialString(Block block, ItemStack item_stack, boolean as_subtype) {
      StringBuilder sb = new StringBuilder();
      sb.append("Block[" + block.blockID + "]");
      sb.append(as_subtype ? "[" + item_stack.getItemSubtype() + "] " : " ");
      sb.append(item_stack.getNameForReferenceFile() + ": " + block.blockMaterial.name);
      sb.append(newline);
      return sb.toString();
   }

   private static void writeBlockMaterialFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/block_material.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Block Material" + newline);
      sb.append("--------------" + newline);

      for(int i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            List list = block.getItemStacks();
            ItemStack item_stack = (ItemStack)list.get(0);
            if (list.size() == 1 && item_stack.getItemSubtype() == 0) {
               sb.append(getBlockMaterialString(block, item_stack, false));
            } else {
               Iterator iterator = list.iterator();

               while(iterator.hasNext()) {
                  item_stack = (ItemStack)iterator.next();
                  sb.append(getBlockMaterialString(block, item_stack, true));
               }
            }
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static String getBlockConstantsString(Block block) {
      StringBuilder sb = new StringBuilder();
      sb.append("Block[" + block.blockID + "] ");
      sb.append(block.getNameForReferenceFile(0, true) + ": ");
      String[] tags = new String[16];
      if (block.is_always_opaque_standard_form_cube) {
         StringHelper.addToStringArray("ALWAYS_OPAQUE_STANDARD_FORM_CUBE", tags);
      } else if (block.is_always_standard_form_cube) {
         StringHelper.addToStringArray("ALWAYS_STANDARD_FORM_CUBE", tags);
      } else if (block.is_never_standard_form_cube) {
         StringHelper.addToStringArray("NEVER_STANDARD_FORM_CUBE", tags);
      } else {
         StringHelper.addToStringArray("NOT_ALWAYS_STANDARD_FORM_CUBE", tags);
      }

      if (block.is_always_solid) {
         StringHelper.addToStringArray("ALWAYS_SOLID", tags);
      } else if (block.is_never_solid) {
         StringHelper.addToStringArray("NEVER_SOLID", tags);
      } else {
         StringHelper.addToStringArray("SOMETIMES_SOLID", tags);
      }

      if (block.never_hides_adjacent_faces) {
         StringHelper.addToStringArray("NEVER_HIDES_ADJACENT_FACES", tags);
      }

      if (MITEConstant.useNewPrecipitationHeightDetermination()) {
         if (block.always_blocks_precipitation) {
            StringHelper.addToStringArray("ALWAYS_BLOCKS_PRECIPITATION", tags);
         } else if (block.never_blocks_precipitation) {
            StringHelper.addToStringArray("NEVER_BLOCKS_PRECIPITATION", tags);
         } else {
            StringHelper.addToStringArray("SOMETIMES_BLOCKS_PRECIPITATION", tags);
         }
      }

      if (block.always_blocks_fluids) {
         StringHelper.addToStringArray("ALWAYS_BLOCKS_FLUIDS", tags);
      } else if (block.never_blocks_fluids) {
         StringHelper.addToStringArray("NEVER_BLOCKS_FLUIDS", tags);
      } else {
         StringHelper.addToStringArray("SOMETIMES_BLOCKS_FLUIDS", tags);
      }

      if (block.connects_with_fence) {
         StringHelper.addToStringArray("ALWAYS_CONNECTS_WITH_FENCE", tags);
      } else {
         StringHelper.addToStringArray("NEVER_CONNECTS_WITH_FENCE", tags);
      }

      if (block.is_always_legal) {
         StringHelper.addToStringArray("ALWAYS_LEGAL", tags);
      } else {
         StringHelper.addToStringArray("NOT_ALWAYS_LEGAL", tags);
      }

      if (block.is_always_immutable) {
         StringHelper.addToStringArray("ALWAYS_IMMUTABLE", tags);
      }

      if (!block.canBeCarried()) {
         StringHelper.addToStringArray("NEVER_CARRIED", tags);
      }

      sb.append(StringHelper.implode(tags, ", ", false, false));
      sb.append(newline);
      return sb.toString();
   }

   private static void writeBlockConstantsFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/block_constants.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Block Constants" + newline);
      sb.append("---------------" + newline);

      for(int i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            sb.append(getBlockConstantsString(block));
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static String getBlockHardnessString(Block block, ItemStack item_stack, boolean as_subtype) {
      StringBuilder sb = new StringBuilder();
      int subtype = item_stack.getItemSubtype();
      sb.append("Block[" + block.blockID + "]");
      sb.append(as_subtype ? "[" + subtype + "] " : " ");
      float hardness = block.getBlockHardness(block instanceof BlockCrops ? 1 : subtype);
      sb.append(item_stack.getNameForReferenceFile() + ": " + (hardness == -1.0F ? "Infinite" : (int)(hardness * 100.0F)));
      if (block.isPortable((World)null, (EntityLivingBase)null, 0, 0, 0)) {
         sb.append(" {Portable}");
      }

      sb.append(newline);
      return sb.toString();
   }

   private static void writeBlockHardnessFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/block_hardness.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Block Hardness" + newline);
      sb.append("--------------" + newline);

      for(int i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            List list = block.getItemStacks();
            ItemStack item_stack = (ItemStack)list.get(0);
            if (list.size() == 1 && item_stack.getItemSubtype() == 0) {
               sb.append(getBlockHardnessString(block, item_stack, false));
            } else {
               Iterator iterator = list.iterator();

               while(iterator.hasNext()) {
                  item_stack = (ItemStack)iterator.next();
                  sb.append(getBlockHardnessString(block, item_stack, true));
               }
            }
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static String getBlockMetadataString(Block block) {
      StringBuilder sb = new StringBuilder();
      sb.append("Block[" + block.blockID + "] ");
      sb.append(block.getNameForReferenceFile(0, true) + ": ");
      boolean previous_was_valid = false;
      int first_valid = -1;
      int last_valid = -1;

      for(int i = 0; i < 16; ++i) {
         if (block.isValidMetadata(i)) {
            if (previous_was_valid) {
               last_valid = i;
            } else {
               last_valid = i;
               first_valid = i;
               previous_was_valid = true;
            }

            if (i == 15) {
               sb.append("[" + (last_valid == first_valid ? first_valid : first_valid + "-" + last_valid) + "]");
            }
         } else if (previous_was_valid) {
            sb.append("[" + (last_valid == first_valid ? first_valid : first_valid + "-" + last_valid) + "]");
            previous_was_valid = false;
         }
      }

      String notes = block.getMetadataNotes();
      if (notes != null) {
         sb.append(" {" + notes + "}");
      }

      sb.append(newline);
      return sb.toString();
   }

   private static void writeBlockMetadataFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/block_metadata.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Block Metadata" + newline);
      sb.append("--------------" + newline);

      for(int i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            sb.append(getBlockMetadataString(block));
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static String getBlockDissolveTimeString(Block block, ItemStack item_stack, boolean as_subtype) {
      StringBuilder sb = new StringBuilder();
      int subtype = item_stack.getItemSubtype();
      sb.append("Block[" + block.blockID + "]");
      sb.append(as_subtype ? "[" + subtype + "] " : " ");
      int ticks_for_pepsin = block.getDissolvePeriod(subtype, DamageSource.pepsin);
      int ticks_for_acid = block.getDissolvePeriod(subtype, DamageSource.acid);
      sb.append(item_stack.getNameForReferenceFile() + ": " + (ticks_for_pepsin < 0 ? "never" : (ticks_for_pepsin == 0 ? "instant" : ticks_for_pepsin)));
      sb.append(", " + (ticks_for_acid < 0 ? "never" : (ticks_for_acid == 0 ? "instant" : ticks_for_acid)));
      sb.append(newline);
      return sb.toString();
   }

   private static void writeBlockDissolveTimeFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/block_dissolve_time.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Number of ticks for a small gelatinous cube to dissolve the block (pepsin, acid)." + newline + newline);
      sb.append("Block Dissolve Time" + newline);
      sb.append("-------------------" + newline);

      for(int i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            List list = block.getItemStacks();
            ItemStack item_stack = (ItemStack)list.get(0);
            if (list.size() == 1 && item_stack.getItemSubtype() == 0) {
               sb.append(getBlockDissolveTimeString(block, item_stack, false));
            } else {
               Iterator iterator = list.iterator();

               while(iterator.hasNext()) {
                  item_stack = (ItemStack)iterator.next();
                  sb.append(getBlockDissolveTimeString(block, item_stack, true));
               }
            }
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static String getSilkHarvestString(Block block, ItemStack item_stack, boolean as_subtype) {
      StringBuilder sb = new StringBuilder();
      int subtype = item_stack.getItemSubtype();
      sb.append("Block[" + block.blockID + "]");
      sb.append(as_subtype ? "[" + subtype + "] " : " ");
      sb.append(item_stack.getNameForReferenceFile() + ": " + StringHelper.yesOrNo(block.canSilkHarvest(subtype)));
      sb.append(newline);
      return sb.toString();
   }

   private static void writeSilkHarvestFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/silk_harvest.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Can Silk Harvest" + newline);
      sb.append("----------------" + newline);

      for(int i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            List list = block.getItemStacks();
            ItemStack item_stack = (ItemStack)list.get(0);
            if (list.size() == 1 && item_stack.getItemSubtype() == 0) {
               sb.append(getSilkHarvestString(block, item_stack, false));
            } else {
               Iterator iterator = list.iterator();

               while(iterator.hasNext()) {
                  item_stack = (ItemStack)iterator.next();
                  sb.append(getSilkHarvestString(block, item_stack, true));
               }
            }
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static String getBlockHarvestLevelString(Block block, ItemStack item_stack, boolean as_subtype) {
      int subtype = item_stack.getItemSubtype();
      int harvest_level = block.getMinHarvestLevel(subtype);
      if (harvest_level == 0) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append("Block[" + block.blockID + "]");
         sb.append(as_subtype ? "[" + subtype + "] " : " ");
         sb.append(item_stack.getNameForReferenceFile() + ": " + harvest_level);
         sb.append(newline);
         return sb.toString();
      }
   }

   private static String getToolHarvestLevelString(ItemTool item, ItemStack item_stack, boolean as_subtype) {
      int subtype = item_stack.getItemSubtype();
      int harvest_level = item.getMaterialHarvestLevel();
      if (harvest_level == 0) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append("Item[" + item_stack.itemID + "]");
         sb.append(as_subtype ? "[" + subtype + "] " : " ");
         sb.append(item_stack.getNameForReferenceFile());
         sb.append(": " + harvest_level);
         sb.append(newline);
         return sb.toString();
      }
   }

   private static void writeHarvestLevelFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/harvest_level.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Only blocks and tools with a harvest level greater than 0 are listed." + newline + newline);
      sb.append("Block Harvest Level" + newline);
      sb.append("-------------------" + newline);

      int i;
      for(i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            List list = block.getItemStacks();
            ItemStack item_stack = (ItemStack)list.get(0);
            if (list.size() == 1 && item_stack.getItemSubtype() == 0) {
               sb.append(getBlockHarvestLevelString(block, item_stack, false));
            } else {
               Iterator iterator = list.iterator();

               while(iterator.hasNext()) {
                  item_stack = (ItemStack)iterator.next();
                  sb.append(getBlockHarvestLevelString(block, item_stack, true));
               }
            }
         }
      }

      sb.append(newline);
      sb.append("Tool Harvest Level" + newline);
      sb.append("------------------" + newline);

      for(i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item instanceof ItemTool) {
            ItemTool tool = item.getAsTool();
            List sub_items = item.getSubItems();
            ItemStack item_stack = (ItemStack)sub_items.get(0);
            if (sub_items.size() == 1 && item_stack.getItemSubtype() == 0) {
               sb.append(getToolHarvestLevelString(tool, item_stack, false));
            } else {
               Iterator iterator = sub_items.iterator();

               while(iterator.hasNext()) {
                  item_stack = (ItemStack)iterator.next();
                  sb.append(getToolHarvestLevelString(tool, item_stack, true));
               }
            }
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static String getComponentString(ItemStack item_stack) {
      Item item = item_stack.getItem();
      StringBuffer sb = new StringBuffer();
      sb.append(item_stack.getNameForReferenceFile() + ": difficulty = ");
      sb.append(item_stack.getCraftingDifficultyAsComponent() < 0.0F ? "Not set!" : formatFloat(item_stack.getCraftingDifficultyAsComponent()));
      List products = item.getCraftingProductsThisIsComponentOf(item_stack.getItemSubtype());
      if (products.size() > 0) {
         sb.append(", {");

         for(int i = 0; i < products.size(); ++i) {
            ItemStack output = (ItemStack)products.get(i);
            sb.append(output.getNameForReferenceFile());
            if (i < products.size() - 1) {
               sb.append(", ");
            }
         }

         sb.append("}");
      }

      return sb.toString();
   }

   private static void writeRecipeComponentsFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/recipe_components.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("The difficulty of a crafting recipe is equal to the sum of the difficulty of its components. Therefore each recipe has its own difficulty (see item_recipes.txt for more information)." + newline + newline);
      sb.append("Recipe Components" + newline);
      sb.append("-----------------" + newline);

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item != null) {
            if (item.getNumSubtypes() == 0 && item.isCraftingComponent(0)) {
               sb.append("Item[" + i + "] ");
               sb.append(getComponentString(new ItemStack(item)));
               sb.append(newline);
            } else {
               List sub_items = item.getSubItems();
               Iterator iterator = sub_items.iterator();

               while(iterator.hasNext()) {
                  ItemStack item_stack = (ItemStack)iterator.next();
                  if (item.isCraftingComponent(item_stack.getItemSubtype())) {
                     sb.append("Item[" + i + "][" + (item instanceof ItemMap ? "*" : item_stack.getItemSubtype()) + "] ");
                     sb.append(getComponentString(item_stack));
                     sb.append(newline);
                  }
               }
            }
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeItemSubtypesFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/item_subtypes.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Item Subtypes" + newline);
      sb.append("-------------" + newline);

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item != null && item.getHasSubtypes()) {
            sb.append("Item[" + i + "] ");
            sb.append(item.getNameForReferenceFile());
            if (item instanceof ItemBlock) {
               ItemBlock item_block = (ItemBlock)item;
               if (item_block.getBlock().getNumSubBlocks() != item.getNumSubtypes()) {
                  System.out.println("Number of subtypes discrepency for " + item + ", " + item_block.getBlock().getNumSubBlocks() + " vs " + item.getNumSubtypes());
               }
            }

            if (item instanceof ItemMap) {
               sb.append(": * {");
            } else {
               sb.append(": " + item.getNumSubtypes() + " {");
            }

            List list = new ArrayList();
            item.getSubItems(item.itemID, (CreativeTabs)null, list);
            Iterator iterator = list.iterator();

            while(iterator.hasNext()) {
               ItemStack item_stack = (ItemStack)iterator.next();
               sb.append(item_stack.getNameForReferenceFile());
               if (iterator.hasNext()) {
                  sb.append(", ");
               }
            }

            sb.append("}" + newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static String getItemMaterialsString(ItemStack item_stack, boolean as_subtype) {
      StringBuilder sb = new StringBuilder();
      sb.append("Item[" + item_stack.itemID + "]");
      sb.append(as_subtype ? "[" + item_stack.getItemSubtype() + "] " : " ");
      sb.append(item_stack.getNameForReferenceFile());
      Item item = item_stack.getItem();
      sb.append(": " + (item.materials.size() == 0 ? "null" : StringHelper.getCommaSeparatedList(Material.getMaterialNames(item.materials))));
      sb.append(newline);
      return sb.toString();
   }

   private static void writeItemMaterialFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/item_material.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Some items have more than one material." + newline + newline);
      sb.append("Item Material" + newline);
      sb.append("-------------" + newline);

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item != null) {
            List sub_items = item.getSubItems();
            ItemStack item_stack = (ItemStack)sub_items.get(0);
            if (sub_items.size() == 1 && item_stack.getItemSubtype() == 0) {
               sb.append(getItemMaterialsString(item_stack, false));
            } else {
               Iterator iterator = sub_items.iterator();

               while(iterator.hasNext()) {
                  item_stack = (ItemStack)iterator.next();
                  sb.append(getItemMaterialsString(item_stack, true));
               }
            }
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeItemDurabilityFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/item_durability.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Tools have their own specific decay rates vs blocks and entities. See tool decay rate files for more information." + newline + newline);
      sb.append("Item Durability" + newline);
      sb.append("---------------" + newline);

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item != null && item.isDamageable()) {
            sb.append("Item[" + i + "] ");
            sb.append(item.getNameForReferenceFile());
            if (item.hasQuality()) {
               sb.append(" (" + item.getDefaultQuality().getUnlocalizedName() + ")" + ": " + item.getMaxDamage(item.getDefaultQuality()));
            } else {
               sb.append(": " + item.getMaxDamage(EnumQuality.average));
            }

            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeItemBurnTimeFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/item_burn_time.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Only items that can be burned in a furnace are listed." + newline + newline);
      sb.append("Item Burn Time" + newline);
      sb.append("--------------" + newline);

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item != null && item.getBurnTime((ItemStack)null) > 0) {
            int num_subtypes = item.getNumSubtypes();
            if (item instanceof ItemMap) {
               ItemStack item_stack = new ItemStack(item, 1, 0);
               sb.append("Item[" + i + "][*] ");
               sb.append(item_stack.getNameForReferenceFile());
               sb.append(": " + item.getBurnTime(item_stack) + " ticks @ heat level " + item.getHeatLevel(item_stack));
               sb.append(newline);
            } else if (num_subtypes == 0) {
               sb.append("Item[" + i + "] ");
               sb.append(item.getNameForReferenceFile());
               sb.append(": " + item.getBurnTime((ItemStack)null) + " ticks @ heat level " + item.getHeatLevel((ItemStack)null));
               sb.append(newline);
            } else {
               for(int subtype = 0; subtype < num_subtypes; ++subtype) {
                  ItemStack item_stack = new ItemStack(item, 1, subtype);
                  sb.append("Item[" + i + "][" + subtype + "] ");
                  sb.append(item_stack.getNameForReferenceFile());
                  sb.append(": " + item.getBurnTime(item_stack) + " ticks @ heat level " + item.getHeatLevel(item_stack));
                  sb.append(newline);
               }
            }
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static String getItemCompostingLine(ItemStack item_stack) {
      String line = new String();
      line = line + "Item[" + item_stack.itemID + "]";
      line = line + (item_stack.getItem() instanceof ItemMap ? "[*] " : (item_stack.getHasSubtypes() ? "[" + item_stack.getItemSubtype() + "] " : " "));
      line = line + StringHelper.repeat(" ", 16 - line.length());
      line = line + item_stack.getNameForReferenceFile();
      line = line + StringHelper.repeat(" ", 42 - line.length());
      line = line + StringHelper.formatFloat(item_stack.getItem().getCompostingValue(), 1, 1);
      Item item = item_stack.getItem().getCompostingRemains(item_stack);
      if (item != null) {
         line = line + " + " + item.getNameForReferenceFile();
      }

      if (item_stack.itemID == Block.pumpkinLantern.blockID) {
         line = line + " + " + Item.getItem(Block.torchWood).getNameForReferenceFile();
      }

      line = line + newline;
      return line;
   }

   private static void writeItemCompostingFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/item_composting.txt");
      StringBuilder sb = new StringBuilder();
      sb.append("Only items that can be composted by worms are listed." + newline + newline);
      sb.append("Item ID         Description               Composting Value" + newline);
      sb.append("-------         -----------               ----------------" + newline);

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item != null) {
            List sub_items = item.getSubItems();
            ItemStack item_stack;
            if (item instanceof ItemMap) {
               item_stack = new ItemStack(item, 1, 0);
               if (item_stack.canBeCompostedByWorms()) {
                  sb.append(getItemCompostingLine(item_stack).toString());
               }
            } else if (sub_items.size() == 0) {
               item_stack = new ItemStack(item);
               if (item_stack.canBeCompostedByWorms()) {
                  sb.append(getItemCompostingLine(item_stack).toString());
               }
            } else {
               Iterator iterator = sub_items.iterator();

               while(iterator.hasNext()) {
                  ItemStack next = (ItemStack)iterator.next();
                  if (next.canBeCompostedByWorms()) {
                     sb.append(getItemCompostingLine(next).toString());
                  }
               }
            }
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static String getRecipeString(Item item, int recipe_index) {
      IRecipe recipe = item.recipes[recipe_index];
      if (recipe == null) {
         return "null";
      } else {
         StringBuffer sb = new StringBuffer();
         ItemStack output = recipe.getRecipeOutput();
         sb.append(output.getNameForReferenceFile() + (output.stackSize == 1 ? "" : " x" + output.stackSize) + ": {");
         ItemStack[] components = recipe.getComponents();

         for(int i = 0; i < components.length; ++i) {
            if (components[i] != null) {
               if (!components[i].getItem().doesSubtypeMatterForProduct(output) && !(components[i].getItem() instanceof ItemCoal)) {
                  sb.append(components[i].getItem().getNameForReferenceFile());
               } else {
                  sb.append(components[i].getNameForReferenceFile());
               }

               sb.append(", ");
            }
         }

         String s = sb.toString();
         sb = new StringBuffer(s.substring(0, s.length() - 2) + "}, difficulty = " + formatFloat(recipe.getUnmodifiedDifficulty()));
         if (output.stackSize > 1) {
            sb.append(" (" + formatFloat(recipe.getUnmodifiedDifficulty() / (float)output.stackSize) + " per unit)");
         }

         if (recipe.getSkillsets() != null) {
            sb.append(" (" + Skill.getSkillsetsString(recipe.getSkillsets(), false) + (output.getItem().hasQuality() ? " for average quality or better" : "") + ")");
         }

         return sb.toString();
      }
   }

   private static void writeItemRecipesFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/item_recipes.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Crafting difficulty is the number of ticks taken to craft an item from start to finish." + newline + newline);
      sb.append("If crafting difficulty is higher than 100 then ticks required is fitted to the curve:" + newline + newline);
      sb.append("   crafting_ticks = ((crafting_difficulty - 100) ^ 0.8) + 100" + newline + newline);
      sb.append("Twenty ticks are performed each second." + newline + newline);
      sb.append("Item Recipes" + newline);
      sb.append("------------" + newline);

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item != null && item.isCraftingProduct()) {
            sb.append("Item[" + i + "] ");
            if (item.num_recipes == 1) {
               sb.append(getRecipeString(item, 0));
            } else {
               sb.append("Has " + item.num_recipes + " recipes:");

               for(int recipe_index = 0; recipe_index < item.num_recipes; ++recipe_index) {
                  sb.append(newline + "  Recipe[" + recipe_index + "] " + getRecipeString(item, recipe_index));
                  IRecipe recipe = item.recipes[recipe_index];
                  float var7 = recipe.getUnmodifiedDifficulty() / (float)recipe.getRecipeOutput().stackSize;
               }

               if (item.isRepairable()) {
                  sb.append(newline + "  Difficulty used for repairs = " + formatFloat(item.getLowestCraftingDifficultyToProduce()));
               }
            }

            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeFurnaceRecipesFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/furnace_recipes.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Furnace Recipes" + newline);
      sb.append("---------------" + newline);
      boolean sandstone_done = false;

      for(int i = 0; i < Item.itemsList.length; ++i) {
         ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(new ItemStack(i), -1);
         if (i == Block.sand.blockID) {
            result = FurnaceRecipes.smelting().getSmeltingResult(new ItemStack(i, sandstone_done ? 4 : 4), sandstone_done ? 2 : 1);
         }

         if (result != null) {
            Item item = Item.getItem(i);
            sb.append(item);
            if (i == Block.sand.blockID && !sandstone_done) {
               sb.append(" x4");
            }

            if (i == Block.sand.blockID && sandstone_done) {
               sb.append(" x4");
            }

            sb.append(" @ ");
            sb.append(TileEntityFurnace.getHeatLevelRequired(i) + (i == Block.sand.blockID && sandstone_done ? 1 : 0));
            sb.append(" = ");
            sb.append(result.getNameForReferenceFile());
            if (result.stackSize != 1) {
               sb.append(" x" + result.stackSize);
            }

            int[] skillsets = TileEntityFurnace.getSkillsetsThatCanSmelt(item);
            if (skillsets != null) {
               sb.append(" (" + Skill.getSkillsetsString(skillsets, false) + ")");
            }

            sb.append(newline);
            if (i == Block.sand.blockID && !sandstone_done) {
               sandstone_done = true;
               --i;
            }
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static String getXpCostForQualityString(Item item) {
      StringBuffer sb = new StringBuffer();

      for(int i = EnumQuality.fine.ordinal(); i <= item.getMaxQuality().ordinal(); ++i) {
         EnumQuality quality = EnumQuality.get(i);
         sb.append(StringHelper.capitalize(quality.getUnlocalizedName()) + "=");
         int cost;
         if (quality.isAverageOrLower()) {
            cost = 0;
         } else {
            float quality_adjusted_crafting_difficulty = CraftingResult.getQualityAdjustedDifficulty(item.getLowestCraftingDifficultyToProduce(), quality);
            cost = Math.round(quality_adjusted_crafting_difficulty / 5.0F);
         }

         sb.append(cost);
         sb.append(", ");
      }

      String s = sb.toString();
      return s.isEmpty() ? s : StringHelper.left(s, -2);
   }

   private static void writeItemXpReqsFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/item_xp_reqs.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Experience Requirements for Item Crafting & Repairing" + newline);
      sb.append("-----------------------------------------------------" + newline);

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item != null && item.isCraftingProduct() && item.hasQuality() && item.getMaxQuality().isHigherThan(EnumQuality.average)) {
            sb.append("Item[" + i + "] ");
            sb.append(item.getNameForReferenceFile() + ": ");
            sb.append(getXpCostForQualityString(item));
            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeFoodValueFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/food_value.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Food Value (satiation, nutrition, protein, phytonutrients, IR=Insulin Response)" + newline);
      sb.append("----------" + newline);

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item != null && item.isIngestable(0)) {
            sb.append("Item[" + i + "] ");
            sb.append(item.getNameForReferenceFile() + ": " + item.getSatiation((EntityPlayer)null) + ", " + item.getNutrition());
            sb.append(", " + item.getProtein() + ", " + item.getPhytonutrients());
            int insulin_response = item.getInsulinResponse();
            if (insulin_response > 0) {
               sb.append(" IR=" + insulin_response);
            }

            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

	private static void writeItemReachFile(File dir) throws Exception
	{
		FileWriter fw = new FileWriter(dir.getPath() + "/item_reach.txt");
		StringBuilder sb = new StringBuilder();
		sb.append("The player has a base reach of ").append(StringHelper.formatFloat(2.75F, 1, 2)).append(" vs blocks and ")
				.append(StringHelper.formatFloat(1.5F, 1, 2))
				.append(" vs entities.").append(newline).append(newline);
		sb.append("Only items that have a reach bonus are listed.").append(newline).append(newline);
		sb.append("Reach Bonus").append(newline);
		sb.append("-----------").append(newline);
		for (int i = 0; i < Item.itemsList.length; i++) {
			Item item = Item.getItem(i);
			if (item != null) {
				String name = item.getNameForReferenceFile();
				if (item instanceof ItemTool) {
                   ItemTool reach_bonus = (ItemTool) item;
                    if (reach_bonus.getToolMaterial() != Material.iron && !(reach_bonus instanceof ItemCudgel))
						continue;
					name = (reach_bonus.getToolMaterial()).name;
				}
				float var7 = item.getReachBonus();
				if (var7 > 0.0F)
				{
					sb.append("Item[").append(i).append("] ");
					sb.append(name).append(": +").append(StringHelper.formatFloat(var7, 1, 3));
					sb.append(newline);
				}
			}
		}
		fw.write(sb.toString());
		fw.close();
	}

   private static void writeDamageVsEntityFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/damage_vs_entity.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Damage vs Entity" + newline);
      sb.append("----------------" + newline);

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item instanceof ItemTool) {
            ItemTool item_tool = (ItemTool)item;
            sb.append("Item[" + i + "] ");
            sb.append(item.getNameForReferenceFile() + ": +" + (int)item_tool.getCombinedDamageVsEntity());
            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeArmorProtectionFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/armor_protection.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("A total armor score of 20 is considered complete protection. Note that non-zero damage always results in at least half a heart of damage." + newline + newline);
      sb.append("Armor begins to lose its protection value after falling below 50% durability." + newline + newline);
      sb.append("Protection Values" + newline);
      sb.append("-----------------" + newline);

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)item;
            sb.append("Item[" + i + "] ");
            sb.append(item.getNameForReferenceFile() + ": +" + StringHelper.formatFloat(armor.getMultipliedProtection((ItemStack)null), 2, 2));
            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeItemEnchantmentsFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/item_enchantments.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Item Enchantments" + newline);
      sb.append("-----------------" + newline);

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item != null && item.getItemEnchantability() > 0) {
            String item_name = item.getNameForReferenceFile();
            if (item instanceof ItemTool) {
               ItemTool tool = (ItemTool)item;
               if (tool instanceof ItemCudgel) {
                  item_name = StringHelper.capitalizeEachWord(tool.getToolType().replaceAll("_", " "));
               } else {
                  if (tool.getToolMaterial() != Material.iron) {
                     continue;
                  }

                  if (!(tool instanceof ItemKnife) && !(tool instanceof ItemHatchet) && !(tool instanceof ItemAxe)) {
                     item_name = StringHelper.capitalizeEachWord(tool.getToolType().replaceAll("_", " "));
                  } else {
                     item_name = StringHelper.capitalizeEachWord((tool.getToolType() + " (metal)").replaceAll("_", " "));
                  }
               }
            } else if (item instanceof ItemArmor) {
               ItemArmor armor = (ItemArmor)item;
               Material material = armor.getArmorMaterial();
               if (material != Material.leather && material != Material.iron) {
                  continue;
               }

               String disambiguation = material == Material.leather ? "leather" : (armor.isChainMail() ? "chain" : "metal");
               item_name = StringHelper.capitalizeEachWord((armor.getArmorType() + " (" + disambiguation + ")").replaceAll("_", " "));
            }

            if (item instanceof ItemBow) {
               if (item != Item.bowMithril) {
                  continue;
               }

               item_name = StringHelper.capitalizeEachWord("Bow");
            }

            if (item instanceof ItemFishingRod) {
               ItemFishingRod rod = (ItemFishingRod)item;
               if (rod.getHookMaterial() != Material.iron) {
                  continue;
               }

               String disambiguation = "metal";
               item_name = StringHelper.capitalizeEachWord(("Fishing Rod (" + disambiguation + ")").replaceAll("_", " "));
            }

            List possible_enchantments = new ArrayList();

            for(int enchantment_index = 0; enchantment_index < Enchantment.enchantmentsList.length; ++enchantment_index) {
               Enchantment enchantment = Enchantment.get(enchantment_index);
               if (enchantment != null && enchantment.canEnchantItem(item)) {
                  possible_enchantments.add(enchantment);
               }
            }

            if (possible_enchantments.size() > 0) {
               sb.append("Item[" + i + "] ");
               String[] names = new String[possible_enchantments.size()];

               for(int enchantment_index = 0; enchantment_index < possible_enchantments.size(); ++enchantment_index) {
                  names[enchantment_index] = ((Enchantment)possible_enchantments.get(enchantment_index)).getTranslatedName(item);
               }

               sb.append(item_name + ": " + StringHelper.getCommaSeparatedList(names));
               sb.append(newline);
            }
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeEnchantmentsFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/enchantments.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Enchantments" + newline);
      sb.append("------------" + newline);

      for(int i = 0; i < Enchantment.enchantmentsList.length; ++i) {
         Enchantment enchantment = Enchantment.get(i);
         if (enchantment != null) {
            sb.append("Enchantment[" + i + "] ");
            sb.append(enchantment + ": " + enchantment.rarity + ", difficulty = " + enchantment.difficulty);
            if (enchantment.hasLevels()) {
               sb.append(", " + enchantment.getNumLevels() + " levels");
            }

            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static String getStackLimitString(ItemStack item_stack, boolean as_subtype) {
      StringBuilder sb = new StringBuilder();
      sb.append("Item[" + item_stack.itemID + "]");
      sb.append(as_subtype ? "[" + item_stack.getItemSubtype() + "] " : " ");
      sb.append(item_stack.getNameForReferenceFile());
      sb.append(": " + item_stack.getMaxStackSize());
      sb.append(newline);
      return sb.toString();
   }

   private static void writeStackLimitsFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/stack_limits.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Stack Limits" + newline);
      sb.append("------------" + newline);

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item != null) {
            boolean allowed_exception = item == Item.map || item == Item.writtenBook || item == Item.firework || item == Item.enchantedBook;
            if (item.getCreativeTab() != null || allowed_exception) {
               List sub_items = item.getSubItems();
               ItemStack item_stack = (ItemStack)sub_items.get(0);
               if (sub_items.size() == 1 && item_stack.getItemSubtype() == 0) {
                  sb.append(getStackLimitString(item_stack, false));
               } else {
                  Iterator iterator = sub_items.iterator();

                  while(iterator.hasNext()) {
                     item_stack = (ItemStack)iterator.next();
                     sb.append(getStackLimitString(item_stack, true));
                  }
               }
            }
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeMaterialDurabilityFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/material_durability.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Material Durability" + newline);
      sb.append("-------------------" + newline);

      for(int i = 0; i < Material.num_materials; ++i) {
         Material material = Material.materials[i];
         if (material != null && material.durability > 0.0F) {
            sb.append("Material[" + i + "] ");
            sb.append(material.getCapitalizedName() + ": " + material.durability);
            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeMaterialMaxQualityFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/material_max_quality.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Material Max Quality" + newline);
      sb.append("--------------------" + newline);

      for(int i = 0; i < Material.num_materials; ++i) {
         Material material = Material.materials[i];
         if (material != null && (material.getMaxQuality().isLowerThan(EnumQuality.legendary) || material == Material.mithril || material == Material.adamantium)) {
            sb.append("Material[" + i + "] ");
            sb.append(material.getCapitalizedName() + ": " + StringHelper.getFirstWord(material.getMaxQuality().getDescriptor()));
            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeMaterialEnchantabilityFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/material_enchantability.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Materials not listed here are not enchantable." + newline + newline);
      sb.append("Material Enchantability" + newline);
      sb.append("-----------------------" + newline);

      for(int i = 0; i < Material.num_materials; ++i) {
         Material material = Material.materials[i];
         if (material != null && material.enchantability > 0) {
            sb.append("Material[" + i + "] ");
            sb.append(material.getCapitalizedName() + ": " + material.enchantability);
            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static String getToolDecayRateString(ItemTool item_tool, ItemStack item_stack, boolean as_subtype) {
      Block block = item_stack.getItem().getAsItemBlock().getBlock();
      StringBuilder sb = new StringBuilder();
      int subtype = item_stack.getItemSubtype();
      sb.append("Block[" + block.blockID + "]");
      sb.append(as_subtype ? "[" + subtype + "] " : " ");
      sb.append(item_stack.getNameForReferenceFile());
      sb.append(": " + item_tool.getToolDecayFromBreakingBlock(new BlockBreakInfo(block.blockID, block instanceof BlockCrops ? 1 : subtype)));
      sb.append(newline);
      return sb.toString();
   }

   private static void writeToolDecayRateFiles(File dir) throws Exception {
      dir = new File(dir, "tool_decay_rates");
      if (!dir.exists()) {
         dir.mkdir();
      }

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item instanceof ItemTool) {
            ItemTool item_tool = (ItemTool)item;
            if (item_tool.getToolMaterial() == Material.adamantium) {
               FileWriter fw = new FileWriter(dir.getPath() + "/" + item_tool.getToolType() + ".txt");
               StringBuffer sb = new StringBuffer();
               sb.append("Only the blocks that this tool is effective against are listed." + newline + newline);
               sb.append("Decay Rate vs Entity" + newline);
               sb.append("--------------------" + newline);
               sb.append("General Factor: x" + formatFloat(item_tool.getBaseDecayRateForAttackingEntity((ItemStack)null)) + newline);
               sb.append("All: " + item_tool.getToolDecayFromAttackingEntity((ItemStack)null, (EntityLivingBase)null));
               sb.append(newline + newline);
               sb.append("Decay Rate vs Block" + newline);
               sb.append("-------------------" + newline);
               sb.append("General Factor: x" + formatFloat(item_tool.getBaseDecayRateForBreakingBlock((Block)null)) + newline);

               for(int block_index = 0; block_index < 256; ++block_index) {
                  Block block = Block.blocksList[block_index];
                  if (block != null && item_tool.isEffectiveAgainstBlock(block, 0)) {
                     List list = block.getItemStacks();
                     ItemStack item_stack = (ItemStack)list.get(0);
                     if (list.size() == 1 && item_stack.getItemSubtype() == 0) {
                        sb.append(getToolDecayRateString(item_tool, item_stack, false));
                     } else {
                        Iterator iterator = list.iterator();

                        while(iterator.hasNext()) {
                           item_stack = (ItemStack)iterator.next();
                           sb.append(getToolDecayRateString(item_tool, item_stack, true));
                        }
                     }
                  }
               }

               fw.write(sb.toString());
               fw.close();
            }
         }
      }

   }

   private static String getToolHarvestEfficiencyString(ItemTool item_tool, ItemStack item_stack, boolean as_subtype) {
      Block block = item_stack.getItem().getAsItemBlock().getBlock();
      StringBuilder sb = new StringBuilder();
      sb.append("Block[" + block.blockID + "]");
      sb.append(as_subtype ? "[" + item_stack.getItemSubtype() + "] " : " ");
      sb.append(item_stack.getNameForReferenceFile());
      sb.append(": " + item_tool.getBaseHarvestEfficiency(block));
      sb.append(newline);
      return sb.toString();
   }

   private static void writeToolHarvestEfficiencyFiles(File dir) throws Exception {
      dir = new File(dir, "tool_harvest_efficiency");
      if (!dir.exists()) {
         dir.mkdir();
      }

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item instanceof ItemTool) {
            ItemTool item_tool = (ItemTool)item;
            if (item_tool.getToolMaterial() == Material.adamantium) {
               FileWriter fw = new FileWriter(dir.getPath() + "/" + item_tool.getToolType() + ".txt");
               StringBuffer sb = new StringBuffer();
               sb.append("Only the blocks that this tool is effective against are listed." + newline + newline);
               sb.append("Harvest Efficiency" + newline);
               sb.append("------------------" + newline);

               for(int block_index = 0; block_index < 256; ++block_index) {
                  Block block = Block.blocksList[block_index];
                  if (block != null && item_tool.isEffectiveAgainstBlock(block, 0)) {
                     List list = block.getItemStacks();
                     ItemStack item_stack = (ItemStack)list.get(0);
                     if (list.size() == 1 && item_stack.getItemSubtype() == 0) {
                        sb.append(getToolHarvestEfficiencyString(item_tool, item_stack, false));
                     } else {
                        Iterator iterator = list.iterator();

                        while(iterator.hasNext()) {
                           item_stack = (ItemStack)iterator.next();
                           sb.append(getToolHarvestEfficiencyString(item_tool, item_stack, true));
                        }
                     }
                  }
               }

               fw.write(sb.toString());
               fw.close();
            }
         }
      }

   }

   private static void writePlayerLevelsFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/player_levels.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("The modifier shown in the last column applies to harvesting speed, melee damage, and crafting time reduction." + newline + newline);
      sb.append("Level          Experience          Stats          Harvesting          Crafting          Melee Dmg" + newline);
      sb.append("-----          ----------          -----          ----------          --------          ---------" + newline);

      for(int level = -40; level <= EntityPlayer.getHighestPossibleLevel(); ++level) {
         String line = new String();
         line = line + level;
         line = line + StringHelper.repeat(" ", 15 - line.length());
         line = line + EntityPlayer.getExperienceRequired(level);
         line = line + StringHelper.repeat(" ", 35 - line.length());
         line = line + EntityPlayer.getHealthLimit(level);
         line = line + StringHelper.repeat(" ", 50 - line.length());
         int level_modifier = Math.round(EntityPlayer.getLevelModifier(level, EnumLevelBonus.HARVESTING) * 100.0F);
         if (level_modifier > 0) {
            line = line + "+";
         }

         line = line + level_modifier + "%";
         line = line + StringHelper.repeat(" ", 70 - line.length());
         level_modifier = Math.round(EntityPlayer.getLevelModifier(level, EnumLevelBonus.CRAFTING) * 100.0F);
         if (level_modifier > 0) {
            line = line + "+";
         }

         line = line + level_modifier + "%";
         line = line + StringHelper.repeat(" ", 88 - line.length());
         level_modifier = Math.round(EntityPlayer.getLevelModifier(level, EnumLevelBonus.MELEE_DAMAGE) * 100.0F);
         if (level_modifier > 0) {
            line = line + "+";
         }

         line = line + level_modifier + "%";
         sb.append(line);
         sb.append(newline);
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeProfessionsFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/professions.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Player professions are only relevant in worlds that have professions enabled." + newline + newline);
      sb.append("Profession     Skill                Description" + newline);
      sb.append("----------     -----                -----------" + newline);

      for(int i = 0; i < Skill.getNumSkills(); ++i) {
         Skill skill = Skill.list[i];
         String line = new String();
         line = line + skill.getLocalizedName(true);
         line = line + StringHelper.repeat(" ", 15 - line.length());
         line = line + skill.getLocalizedName(false);
         line = line + StringHelper.repeat(" ", 36 - line.length());
         line = line + skill.getLocalizedDescription() + ".";
         sb.append(line);
         sb.append(newline);
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeMobExperienceFile(File dir) throws Exception
   {
      FileWriter fw = new FileWriter(dir.getPath() + "/mob_experience.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Only mobs that drop experience are listed here." + newline + newline);

       for (Object o : EntityList.entries) {
           EntityListEntry entry = (EntityListEntry) o;

           if (EntityLiving.class.isAssignableFrom(entry._class) && !Modifier.isAbstract(entry._class.getModifiers())) {
               EntityLiving entity_living = (EntityLiving) entry._class.getConstructor(World.class).newInstance(new Object[]{null});

               if (entity_living instanceof EntityCubic) {
                   ((EntityCubic) entity_living).setSize(4);
               }

               int experience_value = entity_living.getExperienceValue();

               if (experience_value > 0) {
                   sb.append("Entity[" + entry.id + "] " + entity_living.getEntityName() + ": " + experience_value);
                   sb.append(newline);
               }
           }
       }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeCommandsFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/commands.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("These commands are available to all players." + newline + newline);

      for(int i = 0; i < EnumCommand.values().length; ++i) {
         EnumCommand command = EnumCommand.values()[i];
         sb.append("/" + command.text + StringHelper.repeat(" ", 20 - command.text.length()) + command.description + newline);
      }

      fw.write(sb.toString());
      fw.close();
   }

   public static void write() {
      running = true;

      try {
         FileUtils.deleteDirectory(new File("reference"));
         FileUtils.deleteDirectory(new File("MITE/reference"));
      } catch (Exception var1) {
      }

      try {
         File dir = new File("MITE/reference");
         if (!dir.exists()) {
            dir.mkdir();
         }

         writeBlockMaterialFile(dir);
         writeBlockConstantsFile(dir);
         writeBlockHardnessFile(dir);
         writeBlockMetadataFile(dir);
         writeBlockDissolveTimeFile(dir);
         writeSilkHarvestFile(dir);
         writeHarvestLevelFile(dir);
         writeRecipeComponentsFile(dir);
         writeItemSubtypesFile(dir);
         writeItemMaterialFile(dir);
         writeItemDurabilityFile(dir);
         writeItemBurnTimeFile(dir);
         writeItemCompostingFile(dir);
         writeItemRecipesFile(dir);
         writeFurnaceRecipesFile(dir);
         writeItemXpReqsFile(dir);
         writeFoodValueFile(dir);
         writeItemReachFile(dir);
         writeDamageVsEntityFile(dir);
         writeArmorProtectionFile(dir);
         writeEnchantmentsFile(dir);
         writeItemEnchantmentsFile(dir);
         writeStackLimitsFile(dir);
         writeMaterialDurabilityFile(dir);
         writeMaterialMaxQualityFile(dir);
         writeMaterialEnchantabilityFile(dir);
         writeToolDecayRateFiles(dir);
         writeToolHarvestEfficiencyFiles(dir);
         writePlayerLevelsFile(dir);
         writeProfessionsFile(dir);
         writeMobExperienceFile(dir);
         writeCommandsFile(dir);
         if (Minecraft.inDevMode()) {
            writeBlockOpacityFile(dir);
            writeIsOpaqueStandardFormCubeFile(dir);
            writeNormalCubeFile(dir);
            writeBlockMetadataToSubtypeFile(dir);
            writeAllowsGrassBeneathFile(dir);
            writeUseNeighborBrightnessFile(dir);
            writeBlockRenderTypeFile(dir);
         }

         System.out.println("Writing reference files... [ok]\n");
      } catch (Exception var2) {
         Exception e = var2;
         System.out.println("Writing reference files... [failed]\n");
         if (Minecraft.inDevMode()) {
            e.printStackTrace();
         }
      }

      running = false;
   }

   private static void writeBlockOpacityFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/block_opacity.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Block Opacity" + newline);
      sb.append("-------------" + newline);

      for(int i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            sb.append("Block[" + block.blockID + "] ");
            sb.append(block.getNameForReferenceFile(0, true) + ": " + Block.lightOpacity[block.blockID]);
            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeIsOpaqueStandardFormCubeFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/is_opaque_standard_form_cube.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Is Opaque Standard Form Cube" + newline);
      sb.append("----------------------------" + newline);

      for(int i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            sb.append("Block[" + block.blockID + "] ");
            sb.append(block.getNameForReferenceFile(0, true) + ": " + block.isAlwaysOpaqueStandardFormCube());
            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeNormalCubeFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/normal_cube.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Normal Cube" + newline);
      sb.append("-----------" + newline);

      for(int i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            sb.append("Block[" + block.blockID + "] ");
            sb.append(block.getNameForReferenceFile(0, true) + ": " + block.is_normal_cube);
            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeBlockMetadataToSubtypeFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/block_metadata_to_subtype.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Metadata to Subtype" + newline);
      sb.append("-------------------" + newline);

      for(int i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            for(int metadata = 0; metadata < 16; ++metadata) {
               if (block.isValidMetadata(metadata)) {
                  sb.append("Block[" + block.blockID + "][" + metadata + "] ");
                  int block_subtype = block.getBlockSubtype(metadata);
                  int item_subtype = block.getItemSubtype(metadata);
                  if (block_subtype == item_subtype) {
                     sb.append(block.getNameForReferenceFile(0, true) + ": " + block_subtype);
                  } else {
                     sb.append(block.getNameForReferenceFile(0, true) + ": " + block_subtype + " vs " + item_subtype);
                  }

                  sb.append(newline);
               }
            }
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeAllowsGrassBeneathFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/allows_grass_beneath.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Allows Grass Beneath" + newline);
      sb.append("--------------------" + newline);

      for(int i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            sb.append("Block[" + block.blockID + "] ");
            sb.append(block.getNameForReferenceFile(0, true) + ": " + Block.canHaveLightValue[block.blockID]);
            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeUseNeighborBrightnessFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/use_neighbor_brightness.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Use Neighbor Brightness" + newline);
      sb.append("-----------------------" + newline);

      for(int i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            sb.append("Block[" + block.blockID + "] ");
            sb.append(block.getNameForReferenceFile(0, true) + ": " + Block.useNeighborBrightness[block.blockID]);
            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }

   private static void writeBlockRenderTypeFile(File dir) throws Exception {
      FileWriter fw = new FileWriter(dir.getPath() + "/block_render_type.txt");
      StringBuffer sb = new StringBuffer();
      sb.append("Block Render Type" + newline);
      sb.append("-----------------" + newline);

      for(int i = 0; i < 256; ++i) {
         Block block = Block.blocksList[i];
         if (block != null) {
            sb.append("Block[" + block.blockID + "] ");
            sb.append(block.getNameForReferenceFile(0, true) + ": " + block.getRenderType());
            if (block.renderAsNormalBlock()) {
               sb.append(" as Normal Block");
            }

            sb.append(", Pass " + block.getRenderBlockPass());
            sb.append(newline);
         }
      }

      fw.write(sb.toString());
      fw.close();
   }
}
