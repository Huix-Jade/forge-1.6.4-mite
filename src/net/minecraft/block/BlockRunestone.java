package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.Icon;
import net.minecraft.util.StringHelper;
import net.minecraft.world.World;

public class BlockRunestone extends BlockObsidian {
   private static String[] magic_names = new String[]{"Nul", "Quas", "Por", "An", "Nox", "Flam", "Vas", "Des", "Ort", "Tym", "Corp", "Lor", "Mani", "Jux", "Ylem", "Sanct"};
   protected Icon[] iconArray = new Icon[16];
   public Material rune_metal;

   public BlockRunestone(int id, Material rune_metal) {
      super(id);
      this.rune_metal = rune_metal;
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public Icon getIcon(int side, int metadata) {
      return side != 0 && side != 1 ? this.iconArray[metadata] : this.blockIcon;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      super.registerIcons(par1IconRegister);

      for(int i = 0; i < this.iconArray.length; ++i) {
         this.iconArray[i] = par1IconRegister.registerIcon("runestones/" + this.rune_metal.name + "/" + i);
      }

   }

   public void scheduleUpdatesForNearbyPortalBlocks(World world, int x, int y, int z) {
      int check_x;
      int var10001 = check_x = x - 1;
      int check_y;
      int var10002 = check_y = y + 1;
      int check_z = z;
      if (world.getBlock(var10001, var10002, z) == portal) {
         world.scheduleBlockUpdate(check_x, check_y, check_z, portal.blockID, 1);
      }

      var10001 = check_x = x + 1;
      var10002 = check_y = y + 1;
      check_z = z;
      if (world.getBlock(var10001, var10002, z) == portal) {
         world.scheduleBlockUpdate(check_x, check_y, check_z, portal.blockID, 1);
      }

      var10001 = check_x = x - 1;
      var10002 = check_y = y - 1;
      check_z = z;
      if (world.getBlock(var10001, var10002, z) == portal) {
         world.scheduleBlockUpdate(check_x, check_y, check_z, portal.blockID, 1);
      }

      var10001 = check_x = x + 1;
      var10002 = check_y = y - 1;
      check_z = z;
      if (world.getBlock(var10001, var10002, z) == portal) {
         world.scheduleBlockUpdate(check_x, check_y, check_z, portal.blockID, 1);
      }

      check_x = x;
      if (world.getBlock(x, check_y = y + 1, check_z = z - 1) == portal) {
         world.scheduleBlockUpdate(check_x, check_y, check_z, portal.blockID, 1);
      }

      check_x = x;
      if (world.getBlock(x, check_y = y + 1, check_z = z + 1) == portal) {
         world.scheduleBlockUpdate(check_x, check_y, check_z, portal.blockID, 1);
      }

      check_x = x;
      if (world.getBlock(x, check_y = y - 1, check_z = z - 1) == portal) {
         world.scheduleBlockUpdate(check_x, check_y, check_z, portal.blockID, 1);
      }

      check_x = x;
      if (world.getBlock(x, check_y = y - 1, check_z = z + 1) == portal) {
         world.scheduleBlockUpdate(check_x, check_y, check_z, portal.blockID, 1);
      }

   }

   public void onBlockAdded(World world, int x, int y, int z) {
      this.scheduleUpdatesForNearbyPortalBlocks(world, x, y, z);
   }

   public void onBlockPreDestroy(World world, int x, int y, int z, int metadata) {
      this.scheduleUpdatesForNearbyPortalBlocks(world, x, y, z);
   }

   public static String getMagicName(int metadata) {
      return magic_names[metadata];
   }

   public String getMetadataNotes() {
      String[] array = new String[this.iconArray.length];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + "=\"" + getMagicName(i) + "\"";
      }

      return StringHelper.implode(array, ", ", true, true);
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata;
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{this.blockMaterial, this.rune_metal});
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this.rune_metal.name;
   }
}
