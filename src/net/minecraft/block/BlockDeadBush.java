package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockDeadBush extends BlockFlower {
   public static final String[] types = new String[]{"deadbush", "witherwood"};
   private Icon[] icons;

   protected BlockDeadBush(int par1) {
      super(par1, Material.vine);
      this.icons = new Icon[types.length];
      float var2 = 0.4F;
      this.setBlockBoundsForAllThreads((double)(0.5F - var2), 0.0, (double)(0.5F - var2), (double)(0.5F + var2), 0.800000011920929, (double)(0.5F + var2));
      this.setCushioning(0.2F);
   }

   public boolean canOccurAt(World world, int x, int y, int z, int metadata) {
      return !this.isWitherwood(metadata) && !world.canBlockSeeTheSky(x, y, z) ? false : super.canOccurAt(world, x, y, z, metadata);
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return this.isWitherwood(metadata) ? BlockGravel.isNetherGravel(block_below, block_below_metadata) : block_below == sand;
   }

   public int getMinAllowedLightValue() {
      return 0;
   }

   public boolean isWitherwood(int metadata) {
      return isWitherwood(this, metadata);
   }

   public static boolean isWitherwood(Block block, int metadata) {
      return block == deadBush && block.getBlockSubtype(metadata) == 1;
   }

   public String getMetadataNotes() {
      return "0=Regular, 1=Witherwood";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 2;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata & 1;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      for(int i = 0; i < types.length; ++i) {
         this.icons[i] = par1IconRegister.registerIcon(types[i]);
      }

   }

   public Icon getIcon(int side, int metadata) {
      return this.icons[this.getBlockSubtype(metadata)];
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      float chance_of_stick;
      if (this.isWitherwood(info.getMetadata())) {
         if (info.wasNotLegal() || info.wasSelfDropped()) {
            return super.dropBlockAsEntityItem(info);
         }

         chance_of_stick = 0.5F;
      } else {
         if (info.wasNotLegal()) {
            info.world.destroyBlock(info, false);
         }

         chance_of_stick = 0.05F;
      }

      return this.dropBlockAsEntityItem(info, Item.stick.itemID, 0, 1, chance_of_stick);
   }

   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      if (par1World.isWorldServer() && par5Entity instanceof EntityLivingBase) {
         int metadata = par1World.getBlockMetadata(par2, par3, par4);
         if (this.isWitherwood(metadata)) {
            PotionEffect potion_effect = par5Entity.getAsEntityLivingBase().getActivePotionEffect(Potion.wither);
            if (potion_effect == null) {
               par5Entity.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.wither.id, 200, 0));
            } else if (potion_effect.getAmplifier() < 1) {
               if (potion_effect.getDuration() + potion_effect.getEffectInterval() <= 200) {
                  potion_effect.setDuration(potion_effect.getDuration() + potion_effect.getEffectInterval());
               }

               potion_effect.setAmplifier(Math.max(potion_effect.getAmplifier(), 0));
               if (par5Entity.isEntityPlayer()) {
                  par5Entity.getAsPlayer().sendPacket((new Packet85SimpleSignal(EnumSignal.update_potion_effect)).setByte(potion_effect.getPotionID()).setShort(potion_effect.getAmplifier()).setInteger(potion_effect.getDuration()));
               }
            }
         }
      }

   }

   public static void addWitherEffect(EntityLivingBase entity_living_base) {
      entity_living_base.addPotionEffect(new PotionEffect(Potion.wither.id, 200, 0));
   }

   public void randomDisplayTick(World world, int x, int y, int z, Random random) {
      if (this.isWitherwood(world.getBlockMetadata(x, y, z))) {
         spawnWitherwoodParticles(world, x, y, z, random);
      }
   }

   public static void spawnWitherwoodParticles(World world, int x, int y, int z, Random random) {
      Random var5 = world.rand;
      int num = var5.nextInt(3) + 1;

      for(int i = 0; i < num; ++i) {
         world.spawnParticle(EnumParticle.sacred, (double)((float)x + var5.nextFloat()), (double)((float)y + var5.nextFloat()), (double)((float)z + var5.nextFloat()), 0.0, 0.0, 0.0);
      }

   }
}
