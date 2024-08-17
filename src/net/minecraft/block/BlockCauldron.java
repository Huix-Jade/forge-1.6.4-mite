package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemVessel;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCauldron extends Block {
   private Icon field_94378_a;
   private Icon cauldronTopIcon;
   private Icon cauldronBottomIcon;
   private static final AxisAlignedBB[] multiple_bounds = getMultipleBounds();
   private static final AxisAlignedBB[] multiple_bounds_for_player_selection = new AxisAlignedBB[]{new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)};

   public BlockCauldron(int par1) {
      super(par1, Material.iron, new BlockConstants());
      this.setMaxStackSize(4);
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used for water height";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 4;
   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 1 ? this.cauldronTopIcon : (par1 == 0 ? this.cauldronBottomIcon : this.blockIcon);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.field_94378_a = par1IconRegister.registerIcon(this.getTextureName() + "_" + "inner");
      this.cauldronTopIcon = par1IconRegister.registerIcon(this.getTextureName() + "_top");
      this.cauldronBottomIcon = par1IconRegister.registerIcon(this.getTextureName() + "_" + "bottom");
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
   }

   public static Icon getCauldronIcon(String par0Str) {
      return par0Str.equals("inner") ? Block.cauldron.field_94378_a : (par0Str.equals("bottom") ? Block.cauldron.cauldronBottomIcon : null);
   }

   private static AxisAlignedBB[] getMultipleBounds() {
      float var8 = 0.125F;
      return new AxisAlignedBB[]{new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, (double)var8, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, (double)var8), new AxisAlignedBB((double)(1.0F - var8), 0.0, 0.0, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, (double)(1.0F - var8), 1.0, 1.0, 1.0)};
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      return multiple_bounds;
   }

   public RaycastCollision tryRaycastVsBlock(Raycast raycast, int x, int y, int z, Vec3 origin, Vec3 limit) {
      return raycast.isForPlayerSelection() ? this.tryRaycastVsStandardFormBounds(raycast, x, y, z, origin, limit) : super.tryRaycastVsBlock(raycast, x, y, z, origin, limit);
   }

   public void setBlockBoundsForItemRender(int item_damage) {
      this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   }

   public int getRenderType() {
      return 24;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (world.isBlockFaceFlatAndSolid(x, y + 1, z, EnumFace.BOTTOM)) {
         return false;
      } else {
         ItemStack held_item = player.getHeldItemStack();
         if (held_item == null) {
            return false;
         } else {
            int volume_in_cauldron = func_111045_h_(world.getBlockMetadata(x, y, z));
            int volume_in_cauldron_before = volume_in_cauldron;
            boolean action_performed = false;
            Item item = held_item.getItem();
            if (item instanceof ItemVessel) {
               ItemVessel vessel = (ItemVessel)item;
               int vessel_volume = vessel.getStandardVolume();
               if (vessel_volume > 3) {
                  vessel_volume = 3;
               }

               if (vessel.isEmpty()) {
                  if (volume_in_cauldron >= vessel_volume) {
                     if (player.onClient()) {
                        return true;
                     }

                     if (!player.inCreativeMode()) {
                        player.inventory.convertOneOfCurrentItem(new ItemStack(vessel.getPeerForContents(Material.water)));
                     }

                     volume_in_cauldron -= vessel_volume;
                     action_performed = true;
                  }
               } else if (vessel.contains(Material.water) && volume_in_cauldron < 3) {
                  if (player.onClient()) {
                     return true;
                  }

                  if (!player.inCreativeMode()) {
                     player.inventory.convertOneOfCurrentItem(new ItemStack(vessel.getEmptyVessel()));
                  }

                  volume_in_cauldron = MathHelper.clamp_int(volume_in_cauldron + vessel_volume, 0, 3);
                  action_performed = true;
               }
            } else if (item == Item.glassBottle) {
               if (volume_in_cauldron > 0) {
                  if (player.onClient()) {
                     return true;
                  }

                  if (!player.inCreativeMode()) {
                     player.inventory.convertOneOfCurrentItem(new ItemStack(Item.potion, 1, 0));
                  }

                  --volume_in_cauldron;
                  action_performed = true;
               }
            } else if (item == Item.potion && held_item.getItemSubtype() == 0) {
               if (volume_in_cauldron < 3) {
                  if (player.onClient()) {
                     return true;
                  }

                  if (!player.inCreativeMode()) {
                     player.inventory.convertOneOfCurrentItem(new ItemStack(Item.glassBottle));
                  }

                  ++volume_in_cauldron;
                  action_performed = true;
               }
            } else if (item instanceof ItemArmor) {
               ItemArmor armor = (ItemArmor)item;
               if (armor.hasColor(held_item) && volume_in_cauldron > 0) {
                  if (player.onClient()) {
                     return true;
                  }

                  armor.removeColor(held_item);
                  --volume_in_cauldron;
                  action_performed = true;
               }
            }

            if (player.onServer() && volume_in_cauldron != volume_in_cauldron_before) {
               world.setBlockMetadataWithNotify(x, y, z, volume_in_cauldron, 2);
               world.func_96440_m(x, y, z, this.blockID);
            }

            return action_performed;
         }
      }
   }

   public void fillWithRain(World par1World, int par2, int par3, int par4) {
      if (par1World.rand.nextInt(20) == 1) {
         int var5 = par1World.getBlockMetadata(par2, par3, par4);
         if (var5 < 3) {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var5 + 1, 2);
         }
      }

   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Item.cauldron.itemID;
   }

   public boolean hasComparatorInputOverride() {
      return true;
   }

   public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
      int var6 = par1World.getBlockMetadata(par2, par3, par4);
      return func_111045_h_(var6);
   }

   public static int func_111045_h_(int par0) {
      return par0;
   }

   public boolean isPortable(World world, EntityLivingBase entity_living_base, int x, int y, int z) {
      return true;
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, Item.cauldron);
   }

   public boolean playerSwingsOnBlockActivated(boolean empty_handed) {
      return true;
   }

   public boolean canBePlacedOnBlock(int metadata, Block block_below, int block_below_metadata, double block_below_bounds_max_y) {
      return (block_below.isBlockTopFacingSurfaceSolid(block_below_metadata) || block_below == tilledField) && super.canBePlacedOnBlock(metadata, block_below, block_below_metadata, block_below_bounds_max_y);
   }

   public boolean hidesAdjacentSide(IBlockAccess block_access, int x, int y, int z, Block neighbor, int side) {
      return side == 0;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
