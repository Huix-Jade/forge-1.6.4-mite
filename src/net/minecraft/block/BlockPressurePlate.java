package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockPressurePlate extends BlockBasePressurePlate {
   private EnumMobType triggerMobType;

   protected BlockPressurePlate(int par1, String par2Str, Material par3Material, EnumMobType par4EnumMobType) {
      super(par1, par2Str, par3Material);
      this.triggerMobType = par4EnumMobType;
   }

   public String getMetadataNotes() {
      return "Bit 1 set if triggered";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 2;
   }

   protected int getMetaFromWeight(int par1) {
      return par1 > 0 ? 1 : 0;
   }

   protected int getPowerSupply(int par1) {
      return par1 == 1 ? 15 : 0;
   }

   protected int getPlateState(World par1World, int par2, int par3, int par4) {
      List var5 = null;
      if (this.triggerMobType == EnumMobType.everything) {
         var5 = par1World.getEntitiesWithinAABBExcludingEntity((Entity)null, this.getSensitiveAABB(par2, par3, par4));
      }

      if (this.triggerMobType == EnumMobType.mobs) {
         var5 = par1World.getEntitiesWithinAABB(EntityLivingBase.class, this.getSensitiveAABB(par2, par3, par4));
      }

      if (this.triggerMobType == EnumMobType.players) {
         var5 = par1World.getEntitiesWithinAABB(EntityPlayer.class, this.getSensitiveAABB(par2, par3, par4));
      }

      if (var5 != null && !var5.isEmpty()) {
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            Entity var7 = (Entity)var6.next();
            if (!var7.doesEntityNotTriggerPressurePlate()) {
               return 15;
            }
         }
      }

      return 0;
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this.blockMaterial.name;
   }
}
