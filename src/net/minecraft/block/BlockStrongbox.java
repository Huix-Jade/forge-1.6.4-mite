package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityStrongbox;
import net.minecraft.util.EnumChestType;
import net.minecraft.util.EnumFace;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStrongbox extends BlockChest {
   protected BlockStrongbox(int id, Material material) {
      super(id, EnumChestType.strongbox, material);
      this.modifyMinHarvestLevel(1);
      this.setHardnessRelativeToWood(BlockHardness.log);
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      this.setBlockBoundsForCurrentThread(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      if (!test_only && placer instanceof EntityPlayer) {
         ((TileEntityStrongbox)world.getBlockTileEntity(x, y, z)).setOwner(placer.getAsPlayer());
      }

      return super.onBlockPlacedMITE(world, x, y, z, metadata, placer, test_only);
   }

   public TileEntity createNewTileEntity(World world) {
      return new TileEntityStrongbox(EnumChestType.strongbox, this);
   }

   public int[] getUnifiedNeighborCoordinates(World world, int x, int y, int z) {
      return null;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (player.onServer()) {
         TileEntityStrongbox tile_strongbox = (TileEntityStrongbox)world.getBlockTileEntity(x, y, z);
         if (!player.inCreativeMode() && !tile_strongbox.isOwner(player)) {
            if (tile_strongbox.lidAngle == 0.0F) {
               world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "imported.random.chest_locked", 0.2F);
            }

            return true;
         } else {
            return super.onBlockActivated(world, x, y, z, player, face, offset_x, offset_y, offset_z);
         }
      } else {
         return super.onBlockActivated(world, x, y, z, player, face, offset_x, offset_y, offset_z);
      }
   }

   public void registerIcons(IconRegister par1IconRegister) {
      if (this.blockMaterial == Material.copper) {
         this.blockIcon = par1IconRegister.registerIcon("copper_block");
      } else if (this.blockMaterial == Material.silver) {
         this.blockIcon = par1IconRegister.registerIcon("silver_block");
      } else if (this.blockMaterial == Material.gold) {
         this.blockIcon = par1IconRegister.registerIcon("gold_block");
      } else if (this.blockMaterial == Material.iron) {
         this.blockIcon = par1IconRegister.registerIcon("iron_block");
      } else if (this.blockMaterial == Material.mithril) {
         this.blockIcon = par1IconRegister.registerIcon("mithril_block");
      } else if (this.blockMaterial == Material.adamantium) {
         this.blockIcon = par1IconRegister.registerIcon("adamantium_block");
      } else if (this.blockMaterial == Material.ancient_metal) {
         this.blockIcon = par1IconRegister.registerIcon("ancient_metal_block");
      }

   }

   public boolean onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
      return false;
   }

   public boolean isPortable(World world, EntityLivingBase entity_living_base, int x, int y, int z) {
      if (!(entity_living_base instanceof EntityPlayer)) {
         return false;
      } else {
         TileEntity tile_entity = world.getBlockTileEntity(x, y, z);
         return tile_entity instanceof TileEntityStrongbox && ((TileEntityStrongbox)tile_entity).isOwner((EntityPlayer)entity_living_base);
      }
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      EntityPlayer player = info.getResponsiblePlayer();
      return player != null && !player.inCreativeMode() && ((TileEntityStrongbox)info.tile_entity).isOwner(player) ? super.dropBlockAsEntityItem(info) : 0;
   }

   public float getCraftingDifficultyAsComponent(int metadata) {
      return -1.0F;
   }
}
