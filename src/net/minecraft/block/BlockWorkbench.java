package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.util.StringHelper;
import net.minecraft.world.World;

public class BlockWorkbench extends Block {
   private Icon workbenchIconTop;
   private Icon icon_flint_top;
   private Icon icon_obsidian_top;
   protected Icon[] front_icons = new Icon[15];
   protected Icon[] side_icons = new Icon[15];
   public static final Material[] tool_materials;

   protected BlockWorkbench(int par1) {
      super(par1, Material.wood, new BlockConstants());
      this.setHardness(BlockHardness.workbench);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public Icon getIcon(int side, int metadata) {
      if (metadata < 4) {
         return side == 1 ? this.icon_flint_top : Block.wood.getIcon(side, metadata);
      } else if (metadata > 10) {
         return side == 1 ? this.icon_obsidian_top : Block.wood.getIcon(side, metadata - 11);
      } else if (side == 0) {
         return Block.planks.getBlockTextureFromSide(side);
      } else if (side == 1) {
         return this.workbenchIconTop;
      } else {
         return side != 2 && side != 3 ? this.side_icons[metadata] : this.front_icons[metadata];
      }
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.icon_flint_top = par1IconRegister.registerIcon("crafting_table/flint/top");
      this.icon_obsidian_top = par1IconRegister.registerIcon("crafting_table/obsidian/top");
      this.workbenchIconTop = par1IconRegister.registerIcon("crafting_table_top");

      for(int i = 4; i < this.front_icons.length - 4; ++i) {
         this.front_icons[i] = par1IconRegister.registerIcon("crafting_table/" + getToolMaterial(i).name + "/front");
         this.side_icons[i] = par1IconRegister.registerIcon("crafting_table/" + getToolMaterial(i).name + "/side");
      }

   }

   public String getMetadataNotes() {
      String[] array = new String[this.getNumSubBlocks()];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + "=" + getToolMaterial(i).getCapitalizedName() + " Tools";
      }

      return StringHelper.implode(array, ", ", true, true);
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 15;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata;
   }

   public static Material getToolMaterial(int metadata) {
      if (metadata > 10) {
         return tool_materials[8];
      } else {
         return metadata < 4 ? tool_materials[0] : tool_materials[metadata - 3];
      }
   }

   public static ItemStack getBlockComponent(int metadata) {
      Material tool_material = getToolMaterial(metadata);
      if (tool_material == Material.flint) {
         return new ItemStack(Block.wood, 1, metadata);
      } else {
         return tool_material == Material.obsidian ? new ItemStack(Block.wood, 1, metadata - 11) : null;
      }
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (player.onServer() && world.isAirOrPassableBlock(x, y + 1, z, false)) {
         Block block_above = world.getBlock(x, y + 1, z);
         if (block_above == null || !block_above.hidesAdjacentSide(world, x, y + 1, z, this, 1)) {
            player.displayGUIWorkbench(x, y, z);
         }
      }

      return true;
   }

   public boolean playerSwingsOnBlockActivated(boolean empty_handed) {
      return false;
   }

   public boolean isPortable(World world, EntityLivingBase entity_living_base, int x, int y, int z) {
      return true;
   }

   static {
      tool_materials = new Material[]{Material.flint, Material.copper, Material.silver, Material.gold, Material.iron, Material.ancient_metal, Material.mithril, Material.adamantium, Material.obsidian};
   }
}
