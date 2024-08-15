package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Axis;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public abstract class BlockRotatedPillar extends Block {
   protected Icon field_111051_a;

   protected BlockRotatedPillar(int par1, Material par2Material) {
      super(par1, par2Material, new BlockConstants());
   }

   public int getRenderType() {
      return 31;
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      int metadata = super.getMetadataForPlacement(world, x, y, z, item_stack, entity, face, offset_x, offset_y, offset_z);
      if (face.isEastOrWest()) {
         metadata |= 4;
      } else if (face.isNorthOrSouth()) {
         metadata |= 8;
      }

      return metadata;
   }

   public Icon getIcon(int par1, int par2) {
      int var3 = par2 & 12;
      int var4 = par2 & 3;
      return var3 == 0 && (par1 == 1 || par1 == 0) ? this.getEndIcon(var4) : (var3 != 4 || par1 != 5 && par1 != 4 ? (var3 != 8 || par1 != 2 && par1 != 3 ? this.getSideIcon(var4) : this.getEndIcon(var4)) : this.getEndIcon(var4));
   }

   protected abstract Icon getSideIcon(int var1);

   protected Icon getEndIcon(int par1) {
      return this.field_111051_a;
   }

   public EnumDirection getDirectionFacing(int metadata) {
      return (metadata & 4) != 0 ? EnumDirection.WEST : ((metadata & 8) != 0 ? EnumDirection.NORTH : EnumDirection.DOWN);
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      return this.getItemSubtype(metadata) | (direction.isUpOrDown() ? 0 : (direction.isEastOrWest() ? 4 : (direction.isNorthOrSouth() ? 8 : -1)));
   }

   public Axis getAxis(int metadata) {
      return BitHelper.isBitSet(metadata, 4) ? Axis.EAST_WEST : (BitHelper.isBitSet(metadata, 8) ? Axis.NORTH_SOUTH : Axis.UP_DOWN);
   }
}
