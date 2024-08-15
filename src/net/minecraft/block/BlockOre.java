package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.EnumFace;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BlockOre extends Block {
   public Material vein_material;

   public BlockOre(int par1, Material vein_material, int min_harvest_level) {
      super(par1, Material.stone, new BlockConstants());
      this.vein_material = vein_material;
      this.setCreativeTab(CreativeTabs.tabBlock);
      this.setMinHarvestLevel(min_harvest_level);
   }

   public String getMetadataNotes() {
      return "0=Natural, 1=Placed";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 2;
   }

   public void onBlockAboutToBeBroken(BlockBreakInfo info) {
      if (this == oreCopper && (info.wasHarvested() || info.wasExploded())) {
         Chunk chunk = info.getChunkIfItExists();
         if (chunk != null && chunk.doAllNeighborsExist(1, false, false)) {
            World world = info.world;

            for(int dx = -3; dx <= 3; ++dx) {
               for(int dy = -3; dy <= 3; ++dy) {
                  for(int dz = -3; dz <= 3; ++dz) {
                     int x = info.x + dx;
                     int y = info.y + dy;
                     int z = info.z + dz;
                     Block block = world.getBlock(x, y, z);
                     if (block == silverfish) {
                        BlockSilverfish.updateSilverfishType(world, x, y, z);
                     }
                  }
               }
            }
         }
      }

   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      int metadata_dropped = -1;
      int quantity_dropped = 1;
      int id_dropped;
      if (info.wasExploded()) {
         if (this == oreEmerald) {
            id_dropped = Item.shardEmerald.itemID;
         } else if (this == oreDiamond) {
            id_dropped = Item.shardDiamond.itemID;
         } else if (this == oreLapis) {
            id_dropped = Item.dyePowder.itemID;
            metadata_dropped = 4;
            quantity_dropped = 3 + info.world.rand.nextInt(3);
         } else if (this == oreNetherQuartz) {
            id_dropped = Item.shardNetherQuartz.itemID;
         } else if (this == oreCoal) {
            id_dropped = -1;
         } else {
            id_dropped = this.blockID;
         }
      } else {
         if (info.wasHarvestedByPlayer() && info.getResponsiblePlayer().worldObj.areSkillsEnabled() && !info.getResponsiblePlayer().hasSkill(Skill.MINING)) {
            return super.dropBlockAsEntityItem(info);
         }

         if (this == oreCoal) {
            id_dropped = Item.coal.itemID;
         } else if (this == oreDiamond) {
            id_dropped = Item.diamond.itemID;
         } else if (this == oreLapis) {
            id_dropped = Item.dyePowder.itemID;
            metadata_dropped = 4;
            quantity_dropped = 3 + info.world.rand.nextInt(3);
         } else if (this == oreEmerald) {
            id_dropped = Item.emerald.itemID;
         } else if (this == oreNetherQuartz) {
            id_dropped = Item.netherQuartz.itemID;
         } else {
            id_dropped = this.blockID;
         }
      }

      if (metadata_dropped == -1) {
         metadata_dropped = id_dropped == this.blockID ? this.getItemSubtype(info.getMetadata()) : 0;
      }

      boolean suppress_fortune = id_dropped == this.blockID && BitHelper.isBitSet(info.getMetadata(), 1);
      if (id_dropped != -1 && info.getMetadata() == 0) {
         DedicatedServer.incrementTournamentScoringCounter(info.getResponsiblePlayer(), Item.getItem(id_dropped));
      }

      float chance = suppress_fortune ? 1.0F : 1.0F + (float)info.getHarvesterFortune() * 0.1F;
      return super.dropBlockAsEntityItem(info, id_dropped, metadata_dropped, quantity_dropped, chance);
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{this.blockMaterial, this.vein_material});
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return 1;
   }
}
