package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityAnvil;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAnvil extends BlockFalling implements ITileEntityProvider {
   public static final String[] statuses = new String[]{"intact", "slightlyDamaged", "veryDamaged"};
   private static final String[] anvilIconNames = new String[]{"top_damaged_0", "top_damaged_1", "top_damaged_2"};
   public int field_82521_b;
   private Icon[] iconArray;
   public Material metal_type;
   private final int[] minimum_damage_for_stage = new int[3];

   protected BlockAnvil(int par1, Material metal_type) {
      super(par1, Material.anvil, (new BlockConstants()).setNeverHidesAdjacentFaces());
      this.setLightOpacity(0);
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.metal_type = metal_type;
      this.setHardnessRelativeToWood(BlockHardness.log);
      this.setMaxStackSize(1);

      for(int i = 0; i < this.minimum_damage_for_stage.length; ++i) {
         this.minimum_damage_for_stage[i] = this.getMinimumDamageForStage(i, false);
      }

   }

   public Icon getIcon(int par1, int par2) {
      if (this.field_82521_b == 3 && par1 == 1) {
         int var3 = (par2 >> 2) % this.iconArray.length;
         return this.iconArray[var3];
      } else {
         return this.blockIcon;
      }
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("anvil/" + this.metal_type.name + "/base");
      this.iconArray = new Icon[anvilIconNames.length];

      for(int var2 = 0; var2 < this.iconArray.length; ++var2) {
         this.iconArray[var2] = par1IconRegister.registerIcon("anvil/" + this.metal_type.name + "/" + anvilIconNames[var2]);
      }

   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      EnumDirection direction = entity.getDirectionFromYaw();
      int subtype = item_stack.getItemSubtype();
      int metadata = subtype == 1 ? 4 : (subtype == 2 ? 8 : 0);
      return metadata | (direction.isWest() ? 0 : (direction.isNorth() ? 1 : (direction.isEast() ? 2 : 3)));
   }

   public EnumDirection getDirectionFacing(int metadata) {
      int orientation = metadata & 3;
      return orientation == 0 ? EnumDirection.EAST : (orientation == 1 ? EnumDirection.SOUTH : (orientation == 2 ? EnumDirection.WEST : EnumDirection.NORTH));
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      return metadata & -4 | (direction.isEast() ? 0 : (direction.isSouth() ? 1 : (direction.isWest() ? 2 : (direction.isNorth() ? 3 : -1))));
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (!world.isAirOrPassableBlock(x, y + 1, z, false)) {
         return false;
      } else {
         if (player.onServer()) {
            TileEntityAnvil tile_entity = (TileEntityAnvil)world.getBlockTileEntity(x, y, z);
            if (tile_entity != null) {
               player.displayGUIAnvil(x, y, z);
            }
         }

         return true;
      }
   }

   public int getRenderType() {
      return 35;
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used for orientation, bit 4 set if slightly worn, and bit 8 set if badly worn";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 12;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata >> 2;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 3;
      if (var5 != 3 && var5 != 1) {
         this.setBlockBoundsForCurrentThread(0.125, 0.0, 0.0, 0.875, 1.0, 1.0);
      } else {
         this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.125, 1.0, 1.0, 0.875);
      }

   }

   public void getItemStacks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
      par3List.add(new ItemStack(par1, 1, 0));
      par3List.add((new ItemStack(par1, 1, 1)).setItemDamage(this.getMinimumDamageForStage(1)));
      par3List.add((new ItemStack(par1, 1, 2)).setItemDamage(this.getMinimumDamageForStage(2)));
   }

   protected void onStartFalling(World world, int x, int y, int z, EntityFallingSand entity_falling_sand) {
      entity_falling_sand.setIsAnvil(true);
      TileEntityAnvil tile_entity = (TileEntityAnvil)world.getBlockTileEntity(x, y, z);
      entity_falling_sand.item_damage = tile_entity.damage;
      entity_falling_sand.custom_name = tile_entity.getCustomInvName();
      world.removeBlockTileEntity(x, y, z);
   }

   public void onFinishFalling(World par1World, int par2, int par3, int par4, int par5, EntityFallingSand entity_falling_sand) {
      par1World.playAuxSFX(1022, par2, par3, par4, 0);
      TileEntityAnvil tile_entity_anvil = (TileEntityAnvil)par1World.getBlockTileEntity(par2, par3, par4);
      tile_entity_anvil.damage = entity_falling_sand.item_damage;
      tile_entity_anvil.setCustomInvName(entity_falling_sand.custom_name);
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return true;
   }

   public boolean playerSwingsOnBlockActivated(boolean empty_handed) {
      return false;
   }

   public boolean isPortable(World world, EntityLivingBase entity_living_base, int x, int y, int z) {
      return true;
   }

   public boolean getIsRepairable(ItemStack item_to_repair, ItemStack repair_item) {
      return item_to_repair != null && repair_item != null && item_to_repair.hasRepairCost() && item_to_repair.getRepairItem() == repair_item.getItem();
   }

   public float getCraftingDifficultyAsComponent(int metadata) {
      return -1.0F;
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{this.metal_type});
   }

   public boolean canReplaceBlock(int metadata, Block existing_block, int existing_block_metadata) {
      if (super.canReplaceBlock(metadata, existing_block, existing_block_metadata)) {
         return true;
      } else {
         return existing_block != null && existing_block.blockMaterial == Material.circuits;
      }
   }

   public TileEntity createNewTileEntity(World world) {
      return new TileEntityAnvil();
   }

   public void breakBlock(World world, int x, int y, int z, int block_id, int metadata) {
      super.breakBlock(world, x, y, z, block_id, metadata);
      world.removeBlockTileEntity(x, y, z);
   }

   public boolean onBlockEventReceived(World world, int x, int y, int z, int block_id, int event_id) {
      super.onBlockEventReceived(world, x, y, z, block_id, event_id);
      TileEntity tile_entity = world.getBlockTileEntity(x, y, z);
      return tile_entity != null ? tile_entity.receiveClientEvent(block_id, event_id) : false;
   }

   public int getBaseDurabilityPerIngot() {
      return 1600;
   }

   public int getDurability() {
      return (int)((float)(this.getBaseDurabilityPerIngot() * 31) * this.metal_type.durability);
   }

   public int getDamageStage(int damage) {
      float damage_factor = (float)damage / (float)this.getDurability();
      if (damage_factor >= 1.0F) {
         return 3;
      } else if (damage_factor >= 0.8F) {
         return 2;
      } else {
         return damage_factor >= 0.5F ? 1 : 0;
      }
   }

   public int getMinimumDamageForStage(int stage) {
      return this.getMinimumDamageForStage(stage, true);
   }

   public int getMinimumDamageForStage(int stage, boolean use_table_value) {
      if (use_table_value) {
         return this.minimum_damage_for_stage[stage];
      } else {
         int damage;
         for(damage = 0; this.getDamageStage(damage) < stage; ++damage) {
         }

         return damage;
      }
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      TileEntityAnvil tile_entity_anvil = (TileEntityAnvil)info.tile_entity;
      return super.dropBlockAsEntityItem(info.setDamage(tile_entity_anvil.damage));
   }

   public boolean tryPlaceFromHeldItem(int x, int y, int z, EnumFace face, ItemStack item_stack, EntityPlayer player, float offset_x, float offset_y, float offset_z, boolean perform_placement_check, boolean drop_existing_block, boolean test_only) {
      if (super.tryPlaceFromHeldItem(x, y, z, face, item_stack, player, offset_x, offset_y, offset_z, perform_placement_check, drop_existing_block, test_only)) {
         if (!test_only && player.onServer()) {
            TileEntityAnvil tile_entity_anvil = (TileEntityAnvil)player.worldObj.getBlockTileEntity(x, y, z);
            tile_entity_anvil.addDamage(player.worldObj, x, y, z, item_stack.getItemDamage());
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }

   public Material getMetalType() {
      return this.metal_type;
   }
}
