package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.Icon;

public final class BlockGravel extends BlockFalling implements IBlockWithSubtypes {
   private BlockSubtypes subtypes = new BlockSubtypes(new String[]{"gravel", "nether_gravel"});

   public BlockGravel(int par1) {
      super(par1, Material.sand, (new BlockConstants()).setUseNewSandPhysics());
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (info.getMetadata() == 1) {
         return super.dropBlockAsEntityItem(info);
      } else if (!info.wasExploded() && info.wasHarvestedByPlayer()) {
         int fortune = info.getHarvesterFortune();
         if (fortune > 3) {
            fortune = 3;
         }

         Random rand = info.world.rand;
         if (rand.nextInt(12 - fortune * 2) > 2) {
            return super.dropBlockAsEntityItem(info);
         } else {
            int id_dropped;
            if (rand.nextInt(3) > 0) {
               if (rand.nextInt(16) == 0) {
                  id_dropped = info.wasExploded() ? Item.chipFlint.itemID : Item.flint.itemID;
               } else {
                  if (info.wasExploded()) {
                     return super.dropBlockAsEntityItem(info);
                  }

                  id_dropped = Item.chipFlint.itemID;
               }
            } else if (rand.nextInt(3) > 0) {
               id_dropped = Item.copperNugget.itemID;
            } else if (rand.nextInt(3) > 0) {
               id_dropped = Item.silverNugget.itemID;
            } else if (rand.nextInt(3) > 0) {
               id_dropped = Item.goldNugget.itemID;
            } else if (rand.nextInt(3) > 0) {
               id_dropped = info.wasExploded() ? -1 : Item.shardObsidian.itemID;
            } else if (rand.nextInt(3) > 0) {
               id_dropped = info.wasExploded() ? -1 : Item.shardEmerald.itemID;
            } else if (rand.nextInt(3) > 0) {
               id_dropped = info.wasExploded() ? -1 : Item.shardDiamond.itemID;
            } else if (rand.nextInt(3) > 0) {
               id_dropped = Item.mithrilNugget.itemID;
            } else {
               id_dropped = Item.adamantiumNugget.itemID;
            }

            if (this.isNetherGravel(info.getMetadata())) {
               if (id_dropped != Item.copperNugget.itemID && id_dropped != Item.silverNugget.itemID && id_dropped != Item.mithrilNugget.itemID && id_dropped != Item.adamantiumNugget.itemID) {
                  if (id_dropped == Item.shardObsidian.itemID || id_dropped == Item.shardEmerald.itemID || id_dropped == Item.shardDiamond.itemID) {
                     id_dropped = Item.shardNetherQuartz.itemID;
                  }
               } else {
                  id_dropped = Item.goldNugget.itemID;
               }
            }

            if (id_dropped != -1) {
               DedicatedServer.incrementTournamentScoringCounter(info.getResponsiblePlayer(), Item.getItem(id_dropped));
            }

            if (info.wasHarvestedByPlayer() && (id_dropped == Item.chipFlint.itemID || id_dropped == Item.flint.itemID)) {
               info.getResponsiblePlayer().triggerAchievement(AchievementList.flintFinder);
            }

            return this.dropBlockAsEntityItem(info, id_dropped);
         }
      } else {
         return super.dropBlockAsEntityItem(info);
      }
   }

   public String getMetadataNotes() {
      return "0=Gravel, 1=Village Road, 2=Nether Gravel";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 3;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata == 2 ? 1 : 0;
   }

   public int getItemSubtype(int metadata) {
      return this.getBlockSubtype(metadata) == 1 ? 2 : 0;
   }

   public boolean isNetherGravel(int metadata) {
      return isNetherGravel(this, metadata);
   }

   public static boolean isNetherGravel(Block block, int metadata) {
      return block == gravel && block.getBlockSubtype(metadata) == 1;
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
}
