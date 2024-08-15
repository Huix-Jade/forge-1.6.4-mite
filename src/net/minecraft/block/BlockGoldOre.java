package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockGoldOre extends BlockOre implements IBlockWithSubtypes {
   private BlockSubtypes subtypes = new BlockSubtypes(new String[]{"gold_ore", "gold_ore_netherrack"});

   public BlockGoldOre(int par1, Material vein_material, int min_harvest_level) {
      super(par1, vein_material, min_harvest_level);
   }

   public String getMetadataNotes() {
      return "0=Gold Ore Stone, 2=Gold Ore Netherrack, bit 1 set if placed by entity";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 4;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return BitHelper.isBitSet(metadata, 2) ? 1 : 0;
   }

   public int getItemSubtype(int metadata) {
      return this.getBlockSubtype(metadata) == 1 ? 2 : 0;
   }

   public boolean isGoldOreNetherrack(int metadata) {
      return isGoldOreNetherrack(this, metadata);
   }

   public static boolean isGoldOreNetherrack(Block block, int metadata) {
      return block == oreGold && block.getBlockSubtype(metadata) == 1;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.subtypes.setIcons(this.registerIcons(par1IconRegister, this.getTextures()));
   }

   public Icon getIcon(int side, int metadata) {
      return this.subtypes.getIcon(this.getBlockSubtype(metadata));
   }

   public String[] getTextures() {
      return this.subtypes.getTextures();
   }

   public String[] getNames() {
      return this.subtypes.getNames();
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return item_stack.getItemSubtype() | 1;
   }
}
