package net.minecraft.block;

import java.util.Iterator;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {
   private final int maxItemsWeighted;

   protected BlockPressurePlateWeighted(int par1, String par2Str, Material par3Material, int par4) {
      super(par1, par2Str, par3Material);
      this.maxItemsWeighted = par4;
   }

   public String getMetadataNotes() {
      return "All bits used for output signal strength";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   protected int getPlateState(World par1World, int par2, int par3, int par4) {
      int var5 = 0;
      Iterator var6 = par1World.getEntitiesWithinAABB(EntityItem.class, this.getSensitiveAABB(par2, par3, par4)).iterator();

      while(var6.hasNext()) {
         EntityItem var7 = (EntityItem)var6.next();
         var5 += var7.getEntityItem().stackSize;
         if (var5 >= this.maxItemsWeighted) {
            break;
         }
      }

      if (var5 <= 0) {
         return 0;
      } else {
         float var8 = (float)Math.min(this.maxItemsWeighted, var5) / (float)this.maxItemsWeighted;
         return MathHelper.ceiling_float_int(var8 * 15.0F);
      }
   }

   protected int getPowerSupply(int par1) {
      return par1;
   }

   protected int getMetaFromWeight(int par1) {
      return par1;
   }

   public int tickRate(World par1World) {
      return 10;
   }
}
