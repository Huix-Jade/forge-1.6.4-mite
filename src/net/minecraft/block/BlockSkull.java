package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockSkull extends BlockMountedWithTileEntity {
   protected BlockSkull(int par1) {
      super(par1, Material.circuits, TileEntitySkull.class, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.setBlockBoundsForAllThreads(0.25, 0.0, 0.25, 0.75, 0.5, 0.75);
   }

   public String getMetadataNotes() {
      return "1 if lying on ground (in which case the tile entity determines orientation), otherwise 2-5 if wall-mounted";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata > 0 && metadata < 6;
   }

   public final int getRenderType() {
      return -1;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 7;
      switch (var5) {
         case 1:
         default:
            this.setBlockBoundsForCurrentThread(0.25, 0.0, 0.25, 0.75, 0.5, 0.75);
            break;
         case 2:
            this.setBlockBoundsForCurrentThread(0.25, 0.25, 0.5, 0.75, 0.75, 1.0);
            break;
         case 3:
            this.setBlockBoundsForCurrentThread(0.25, 0.25, 0.0, 0.75, 0.75, 0.5);
            break;
         case 4:
            this.setBlockBoundsForCurrentThread(0.5, 0.25, 0.25, 1.0, 0.75, 0.75);
            break;
         case 5:
            this.setBlockBoundsForCurrentThread(0.0, 0.25, 0.25, 0.5, 0.75, 0.75);
      }

   }

   public EnumFace getFaceMountedTo(int metadata) {
      return EnumFace.get(metadata);
   }

   public final int getDefaultMetadataForFaceMountedTo(EnumFace face) {
      return face.ordinal();
   }

   public boolean canMountToBlock(int metadata, Block neighbor_block, int neighbor_block_metadata, EnumFace face) {
      if (face.isTop()) {
         if (neighbor_block == fence || neighbor_block == Block.netherFence) {
            return true;
         }

         if (neighbor_block instanceof BlockWall) {
            return true;
         }

         if (neighbor_block != leaves && neighbor_block.isFaceFlatAndSolid(neighbor_block_metadata, face)) {
            return true;
         }
      }

      return neighbor_block == cloth || super.canMountToBlock(metadata, neighbor_block, neighbor_block_metadata, face);
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntitySkull();
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Item.skull.itemID;
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (info.wasExploded()) {
         return 0;
      } else if (info.wasCrushed()) {
         return this.dropBlockAsEntityItem(info, new ItemStack(Item.dyePowder, 1, 15));
      } else {
         if (info.isResponsiblePlayerInCreativeMode()) {
            info.setMetadata(info.getMetadata() | 8);
         }

         if ((info.getMetadata() & 8) == 0) {
            TileEntitySkull tile_entity_skull = info.tile_entity instanceof TileEntitySkull ? (TileEntitySkull)info.tile_entity : null;
            if (tile_entity_skull == null) {
               return 0;
            } else {
               ItemStack item_stack = new ItemStack(Item.skull.itemID, 1, tile_entity_skull.getSkullType());
               String extra_type = tile_entity_skull.getExtraType();
               if (tile_entity_skull.getSkullType() == 3 && extra_type != null && extra_type.length() > 0) {
                  item_stack.setTagCompound(new NBTTagCompound());
                  item_stack.getTagCompound().setString("SkullOwner", extra_type);
               }

               return this.dropBlockAsEntityItem(info.setOther(), item_stack);
            }
         } else {
            return 0;
         }
      }
   }

   @Override
   public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
      ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
      BlockBreakInfo info = new BlockBreakInfo(world, x, y, z);
      if (info.wasExploded()) {
         return drops;
      } else if (info.wasCrushed()) {
         drops.add(new ItemStack(Item.dyePowder, 1, 15));
         return drops;
      } else {

         if ((metadata & 8) == 0)
         {
            ItemStack itemstack = new ItemStack(Item.skull.itemID, 1, info.damage);
            TileEntitySkull tileentityskull = (TileEntitySkull)world.getBlockTileEntity(x, y, z);

            if (tileentityskull == null) {
               return drops;
            }
            if (tileentityskull.getSkullType() == 3 && tileentityskull.getExtraType() != null && tileentityskull.getExtraType().length() > 0)
            {
               itemstack.setTagCompound(new NBTTagCompound());
               itemstack.getTagCompound().setString("SkullOwner", tileentityskull.getExtraType());
            }
            drops.add(itemstack);
         }
      }

      return drops;
   }


   public void breakBlock(World world, int x, int y, int z, int old_block_id, int old_block_metadata) {
      super.breakBlock(world, x, y, z, old_block_id, old_block_metadata);
   }

   public void makeWither(World par1World, int par2, int par3, int par4, TileEntitySkull par5TileEntitySkull) {
      if (par5TileEntitySkull.getSkullType() == 1 && par3 >= 2 && par1World.difficultySetting > 0 && !par1World.isRemote) {
         int var6 = Block.slowSand.blockID;

         int var7;
         EntityWither var8;
         int var9;
         for(var7 = -2; var7 <= 0; ++var7) {
            if (par1World.getBlockId(par2, par3 - 1, par4 + var7) == var6 && par1World.getBlockId(par2, par3 - 1, par4 + var7 + 1) == var6 && par1World.getBlockId(par2, par3 - 2, par4 + var7 + 1) == var6 && par1World.getBlockId(par2, par3 - 1, par4 + var7 + 2) == var6 && this.func_82528_d(par1World, par2, par3, par4 + var7, 1) && this.func_82528_d(par1World, par2, par3, par4 + var7 + 1, 1) && this.func_82528_d(par1World, par2, par3, par4 + var7 + 2, 1)) {
               par1World.setBlockMetadataWithNotify(par2, par3, par4 + var7, 8, 2);
               par1World.setBlockMetadataWithNotify(par2, par3, par4 + var7 + 1, 8, 2);
               par1World.setBlockMetadataWithNotify(par2, par3, par4 + var7 + 2, 8, 2);
               par1World.setBlock(par2, par3, par4 + var7, 0, 0, 2);
               par1World.setBlock(par2, par3, par4 + var7 + 1, 0, 0, 2);
               par1World.setBlock(par2, par3, par4 + var7 + 2, 0, 0, 2);
               par1World.setBlock(par2, par3 - 1, par4 + var7, 0, 0, 2);
               par1World.setBlock(par2, par3 - 1, par4 + var7 + 1, 0, 0, 2);
               par1World.setBlock(par2, par3 - 1, par4 + var7 + 2, 0, 0, 2);
               par1World.setBlock(par2, par3 - 2, par4 + var7 + 1, 0, 0, 2);
               if (!par1World.isRemote) {
                  var8 = new EntityWither(par1World);
                  var8.setLocationAndAngles((double)par2 + 0.5, (double)par3 - 1.45, (double)(par4 + var7) + 1.5, 90.0F, 0.0F);
                  var8.renderYawOffset = 90.0F;
                  var8.func_82206_m();
                  par1World.spawnEntityInWorld(var8);
               }

               for(var9 = 0; var9 < 120; ++var9) {
                  par1World.spawnParticle(EnumParticle.snowballpoof, (double)par2 + par1World.rand.nextDouble(), (double)(par3 - 2) + par1World.rand.nextDouble() * 3.9, (double)(par4 + var7 + 1) + par1World.rand.nextDouble(), 0.0, 0.0, 0.0);
               }

               par1World.notifyBlockChange(par2, par3, par4 + var7, 0);
               par1World.notifyBlockChange(par2, par3, par4 + var7 + 1, 0);
               par1World.notifyBlockChange(par2, par3, par4 + var7 + 2, 0);
               par1World.notifyBlockChange(par2, par3 - 1, par4 + var7, 0);
               par1World.notifyBlockChange(par2, par3 - 1, par4 + var7 + 1, 0);
               par1World.notifyBlockChange(par2, par3 - 1, par4 + var7 + 2, 0);
               par1World.notifyBlockChange(par2, par3 - 2, par4 + var7 + 1, 0);
               return;
            }
         }

         for(var7 = -2; var7 <= 0; ++var7) {
            if (par1World.getBlockId(par2 + var7, par3 - 1, par4) == var6 && par1World.getBlockId(par2 + var7 + 1, par3 - 1, par4) == var6 && par1World.getBlockId(par2 + var7 + 1, par3 - 2, par4) == var6 && par1World.getBlockId(par2 + var7 + 2, par3 - 1, par4) == var6 && this.func_82528_d(par1World, par2 + var7, par3, par4, 1) && this.func_82528_d(par1World, par2 + var7 + 1, par3, par4, 1) && this.func_82528_d(par1World, par2 + var7 + 2, par3, par4, 1)) {
               par1World.setBlockMetadataWithNotify(par2 + var7, par3, par4, 8, 2);
               par1World.setBlockMetadataWithNotify(par2 + var7 + 1, par3, par4, 8, 2);
               par1World.setBlockMetadataWithNotify(par2 + var7 + 2, par3, par4, 8, 2);
               par1World.setBlock(par2 + var7, par3, par4, 0, 0, 2);
               par1World.setBlock(par2 + var7 + 1, par3, par4, 0, 0, 2);
               par1World.setBlock(par2 + var7 + 2, par3, par4, 0, 0, 2);
               par1World.setBlock(par2 + var7, par3 - 1, par4, 0, 0, 2);
               par1World.setBlock(par2 + var7 + 1, par3 - 1, par4, 0, 0, 2);
               par1World.setBlock(par2 + var7 + 2, par3 - 1, par4, 0, 0, 2);
               par1World.setBlock(par2 + var7 + 1, par3 - 2, par4, 0, 0, 2);
               if (!par1World.isRemote) {
                  var8 = new EntityWither(par1World);
                  var8.setLocationAndAngles((double)(par2 + var7) + 1.5, (double)par3 - 1.45, (double)par4 + 0.5, 0.0F, 0.0F);
                  var8.func_82206_m();
                  par1World.spawnEntityInWorld(var8);
               }

               for(var9 = 0; var9 < 120; ++var9) {
                  par1World.spawnParticle(EnumParticle.snowballpoof, (double)(par2 + var7 + 1) + par1World.rand.nextDouble(), (double)(par3 - 2) + par1World.rand.nextDouble() * 3.9, (double)par4 + par1World.rand.nextDouble(), 0.0, 0.0, 0.0);
               }

               par1World.notifyBlockChange(par2 + var7, par3, par4, 0);
               par1World.notifyBlockChange(par2 + var7 + 1, par3, par4, 0);
               par1World.notifyBlockChange(par2 + var7 + 2, par3, par4, 0);
               par1World.notifyBlockChange(par2 + var7, par3 - 1, par4, 0);
               par1World.notifyBlockChange(par2 + var7 + 1, par3 - 1, par4, 0);
               par1World.notifyBlockChange(par2 + var7 + 2, par3 - 1, par4, 0);
               par1World.notifyBlockChange(par2 + var7 + 1, par3 - 2, par4, 0);
               return;
            }
         }
      }

   }

   private boolean func_82528_d(World par1World, int par2, int par3, int par4, int par5) {
      if (par1World.getBlockId(par2, par3, par4) != this.blockID) {
         return false;
      } else {
         TileEntity var6 = par1World.getBlockTileEntity(par2, par3, par4);
         return var6 != null && var6 instanceof TileEntitySkull ? ((TileEntitySkull)var6).getSkullType() == par5 : false;
      }
   }

   public void registerIcons(IconRegister par1IconRegister) {
   }

   public Icon getIcon(int par1, int par2) {
      return Block.slowSand.getBlockTextureFromSide(par1);
   }

   public String getItemIconName() {
      return this.getTextureName() + "_" + ItemSkull.field_94587_a[0];
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      if (!test_only && placer instanceof EntityPlayer) {
         EntityPlayer player = placer.getAsPlayer();
         TileEntity tile_entity = world.getBlockTileEntity(x, y, z);
         if (tile_entity instanceof TileEntitySkull) {
            String var13 = "";
            ItemStack item_stack = player.getHeldItemStack();
            if (item_stack.hasTagCompound() && item_stack.getTagCompound().hasKey("SkullOwner")) {
               var13 = item_stack.getTagCompound().getString("SkullOwner");
            }

            ((TileEntitySkull)tile_entity).setSkullType(item_stack.getItemSubtype(), var13);
            ((TileEntitySkull)tile_entity).setSkullRotation(this.getFaceMountedTo(metadata).isTop() ? player.getRotationYawAsSixteenths() : 0);
            ((BlockSkull)Block.skull).makeWither(world, x, y, z, (TileEntitySkull)tile_entity);
         }
      }

      return true;
   }

   public boolean isDislodgedOrCrushedByFallingBlock(int metadata, Block falling_block, int falling_block_metadata) {
      return true;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (!this.getFaceMountedTo(world.getBlockMetadata(x, y, z)).isTop()) {
         return false;
      } else {
         TileEntity tile_entity = world.getBlockTileEntity(x, y, z);
         if (tile_entity instanceof TileEntitySkull) {
            ((TileEntitySkull)tile_entity).setSkullRotation(player.getRotationYawAsSixteenths());
         }

         return true;
      }
   }
}
