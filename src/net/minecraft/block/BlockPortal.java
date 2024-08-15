package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityVampireBat;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockPortal extends BlockBreakable {
   private Icon runegate_icon;
   private Icon nether_portal_icon;
   public static final int DESTINATION_OVERWORLD = 0;
   public static final int DESTINATION_UNDERWORLD = 1;
   public static final int DESTINATION_NETHER = 2;
   public static final int IS_RUNEGATE = 8;

   public BlockPortal(int par1) {
      super(par1, "portal", Material.portal, false, (new BlockConstants()).setUsesAlphaBlending());
      this.setTickRandomly(true);
   }

   public String getMetadataNotes() {
      return "0=To Overworld, 1=To Underworld, 2=To Nether, bit 8 set if Runegate";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 3 || metadata >= 8 && metadata < 11;
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      this.updateType(world, x, y, z);
      Class entity_class = null;
      if (world.isOverworld()) {
         if (this.isPortalToUnderworld(world.getBlockMetadata(x, y, z), false) && random.nextInt(100) == 0) {
            entity_class = random.nextBoolean() ? EntityCaveSpider.class : EntityVampireBat.class;
         }
      } else if (world.isUnderworld() && this.isPortalToNether(world.getBlockMetadata(x, y, z), false) && random.nextInt(100) == 0) {
         entity_class = EntityPigZombie.class;
      }

      boolean flag = true;
      if (flag) {
         entity_class = null;
      }

      if (entity_class != null) {
         List players = world.getAsWorldServer().playerEntities;
         Iterator i = players.iterator();

         while(i.hasNext()) {
            EntityPlayerMP player = (EntityPlayerMP)i.next();
            if (player.ticks_since_portal_teleport < 1000) {
               entity_class = null;
               break;
            }
         }

         if (entity_class != null && world.getEntitiesWithinAABB(entity_class, world.getBoundingBoxFromPool(x, y, z).scale(16.0)).size() > 3) {
            entity_class = null;
         }
      }

      if (entity_class != null) {
         int entity_id = EntityList.getEntityID(entity_class);
         if (entity_class == EntityVampireBat.class) {
            Entity var7 = ItemMonsterPlacer.spawnCreature(world, entity_id, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, false, EnumFace.TOP);
            var7.timeUntilPortal = var7.getPortalCooldown();
            var7.refreshDespawnCounter(-9600);
         } else {
            int var6;
            for(var6 = y; !world.isBlockTopFlatAndSolid(x, var6, z) && var6 > 0; --var6) {
            }

            if (var6 > 0 && !world.isBlockNormalCube(x, var6 + 1, z)) {
               Entity var7 = ItemMonsterPlacer.spawnCreature(world, entity_id, (double)x + 0.5, (double)var6 + 1.1, (double)z + 0.5, false, EnumFace.TOP);
               if (var7 != null) {
                  var7.timeUntilPortal = var7.getPortalCooldown();
                  var7.refreshDespawnCounter(-9600);
               }
            }
         }
      }

      return false;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      float var5;
      float var6;
      if (par1IBlockAccess.getBlockId(par2 - 1, par3, par4) != this.blockID && par1IBlockAccess.getBlockId(par2 + 1, par3, par4) != this.blockID) {
         var5 = 0.125F;
         var6 = 0.5F;
         this.setBlockBoundsForCurrentThread((double)(0.5F - var5), 0.0, (double)(0.5F - var6), (double)(0.5F + var5), 1.0, (double)(0.5F + var6));
      } else {
         var5 = 0.5F;
         var6 = 0.125F;
         this.setBlockBoundsForCurrentThread((double)(0.5F - var5), 0.0, (double)(0.5F - var6), (double)(0.5F + var5), 1.0, (double)(0.5F + var6));
      }

   }

   public boolean tryToCreatePortal(World par1World, int par2, int par3, int par4) {
      if (par1World.isTheEnd()) {
         return false;
      } else {
         byte var5 = 0;
         byte var6 = 0;
         if (par1World.getBlockId(par2 - 1, par3, par4) == Block.obsidian.blockID || par1World.getBlockId(par2 + 1, par3, par4) == Block.obsidian.blockID) {
            var5 = 1;
         }

         if (par1World.getBlockId(par2, par3, par4 - 1) == Block.obsidian.blockID || par1World.getBlockId(par2, par3, par4 + 1) == Block.obsidian.blockID) {
            var6 = 1;
         }

         if (var5 == var6) {
            return false;
         } else {
            if (par1World.getBlockId(par2 - var5, par3, par4 - var6) == 0) {
               par2 -= var5;
               par4 -= var6;
            }

            int var7;
            int var8;
            for(var7 = -1; var7 <= 2; ++var7) {
               for(var8 = -1; var8 <= 3; ++var8) {
                  boolean var9 = var7 == -1 || var7 == 2 || var8 == -1 || var8 == 3;
                  if (var7 != -1 && var7 != 2 || var8 != -1 && var8 != 3) {
                     int var10 = par1World.getBlockId(par2 + var5 * var7, par3 + var8, par4 + var6 * var7);
                     if (var9) {
                        if (var10 != Block.obsidian.blockID) {
                           return false;
                        }
                     } else if (var10 != 0 && var10 != Block.fire.blockID && var10 != Block.spark.blockID) {
                        return false;
                     }
                  }
               }
            }

            for(var7 = 0; var7 < 2; ++var7) {
               for(var8 = 0; var8 < 3; ++var8) {
                  par1World.setBlock(par2 + var5 * var7, par3 + var8, par4 + var6 * var7, Block.portal.blockID, 0, 2);
               }
            }

            int metadata = this.getPortalTypeBasedOnLocation(par1World, par2, par3, par4, true);

            for(var7 = 0; var7 < 2; ++var7) {
               for(var8 = 0; var8 < 3; ++var8) {
                  par1World.setBlock(par2 + var5 * var7, par3 + var8, par4 + var6 * var7, Block.portal.blockID, metadata, 2);
               }
            }

            return true;
         }
      }
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      byte var6 = 0;
      byte var7 = 1;
      if (world.getBlockId(x - 1, y, z) == this.blockID || world.getBlockId(x + 1, y, z) == this.blockID) {
         var6 = 1;
         var7 = 0;
      }

      int var8;
      for(var8 = y; world.getBlockId(x, var8 - 1, z) == this.blockID; --var8) {
      }

      if (world.getBlockId(x, var8 - 1, z) != Block.obsidian.blockID) {
         return world.setBlockToAir(x, y, z);
      } else {
         int var9;
         for(var9 = 1; var9 < 4 && world.getBlockId(x, var8 + var9, z) == this.blockID; ++var9) {
         }

         if (var9 == 3 && world.getBlockId(x, var8 + var9, z) == Block.obsidian.blockID) {
            boolean var10 = world.getBlockId(x - 1, y, z) == this.blockID || world.getBlockId(x + 1, y, z) == this.blockID;
            boolean var11 = world.getBlockId(x, y, z - 1) == this.blockID || world.getBlockId(x, y, z + 1) == this.blockID;
            if (var10 && var11) {
               return world.setBlockToAir(x, y, z);
            } else {
               return world.getBlockId(x + var6, y, z + var7) == Block.obsidian.blockID && world.getBlockId(x - var6, y, z - var7) == this.blockID || world.getBlockId(x - var6, y, z - var7) == Block.obsidian.blockID && world.getBlockId(x + var6, y, z + var7) == this.blockID ? this.updateType(world, x, y, z) : world.setBlockToAir(x, y, z);
            }
         } else {
            return world.setBlockToAir(x, y, z);
         }
      }
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      if (par1IBlockAccess.getBlockId(par2, par3, par4) == this.blockID) {
         return false;
      } else {
         boolean var6 = par1IBlockAccess.getBlockId(par2 - 1, par3, par4) == this.blockID && par1IBlockAccess.getBlockId(par2 - 2, par3, par4) != this.blockID;
         boolean var7 = par1IBlockAccess.getBlockId(par2 + 1, par3, par4) == this.blockID && par1IBlockAccess.getBlockId(par2 + 2, par3, par4) != this.blockID;
         boolean var8 = par1IBlockAccess.getBlockId(par2, par3, par4 - 1) == this.blockID && par1IBlockAccess.getBlockId(par2, par3, par4 - 2) != this.blockID;
         boolean var9 = par1IBlockAccess.getBlockId(par2, par3, par4 + 1) == this.blockID && par1IBlockAccess.getBlockId(par2, par3, par4 + 2) != this.blockID;
         boolean var10 = var6 || var7;
         boolean var11 = var8 || var9;
         return var10 && par5 == 4 ? true : (var10 && par5 == 5 ? true : (var11 && par5 == 2 ? true : var11 && par5 == 3));
      }
   }

   public int getRenderBlockPass() {
      return 1;
   }

   private int getFrameMinX(World world, int x, int y, int z) {
      boolean x_aligned = world.getBlockId(x + 1, y, z) == this.blockID;

      int min_x;
      for(min_x = x; world.getBlockId(min_x - 1, y, z) == this.blockID; x_aligned = true) {
         --min_x;
      }

      return x_aligned ? min_x - 1 : min_x;
   }

   private int getFrameMaxX(World world, int x, int y, int z) {
      boolean x_aligned = world.getBlockId(x - 1, y, z) == this.blockID;

      int max_x;
      for(max_x = x; world.getBlockId(max_x + 1, y, z) == this.blockID; x_aligned = true) {
         ++max_x;
      }

      return x_aligned ? max_x + 1 : max_x;
   }

   private int getFrameMinY(World world, int x, int y, int z) {
      int min_y;
      for(min_y = y; world.getBlockId(x, min_y - 1, z) == this.blockID; --min_y) {
      }

      return min_y - 1;
   }

   private int getFrameMaxY(World world, int x, int y, int z) {
      int max_y;
      for(max_y = y; world.getBlockId(x, max_y + 1, z) == this.blockID; ++max_y) {
      }

      return max_y + 1;
   }

   private int getFrameMinZ(World world, int x, int y, int z) {
      boolean z_aligned = world.getBlockId(x, y, z + 1) == this.blockID;

      int min_z;
      for(min_z = z; world.getBlockId(x, y, min_z - 1) == this.blockID; z_aligned = true) {
         --min_z;
      }

      return z_aligned ? min_z - 1 : min_z;
   }

   private int getFrameMaxZ(World world, int x, int y, int z) {
      boolean z_aligned = world.getBlockId(x, y, z - 1) == this.blockID;

      int max_z;
      for(max_z = z; world.getBlockId(x, y, max_z + 1) == this.blockID; z_aligned = true) {
         ++max_z;
      }

      return z_aligned ? max_z + 1 : max_z;
   }

   public static int getDestinationBit(int metadata) {
      return BitHelper.clearBit(metadata, 8);
   }

   public int getMetadataForDestination(int destination, boolean is_runegate) {
      return destination | (is_runegate ? 8 : 0);
   }

   public boolean isPortalToOverworld(int metadata, boolean include_runegates) {
      return (include_runegates ? getDestinationBit(metadata) : metadata) == 0;
   }

   public boolean isPortalToUnderworld(int metadata, boolean include_runegates) {
      return (include_runegates ? getDestinationBit(metadata) : metadata) == 1;
   }

   public boolean isPortalToNether(int metadata, boolean include_runegates) {
      return (include_runegates ? getDestinationBit(metadata) : metadata) == 2;
   }

   public boolean isRunegate(int metadata) {
      return BitHelper.isBitSet(metadata, 8);
   }

   public boolean isPortalToOverworldSpawn(World world, int metadata) {
      return world.isOverworld() && metadata == 0;
   }

   public BlockRunestone getRunegateType(World world, int x, int y, int z) {
      int frame_min_x = this.getFrameMinX(world, x, y, z);
      int frame_max_x = this.getFrameMaxX(world, x, y, z);
      int frame_min_y = this.getFrameMinY(world, x, y, z);
      int frame_max_y = this.getFrameMaxY(world, x, y, z);
      int frame_min_z = this.getFrameMinZ(world, x, y, z);
      int frame_max_z = this.getFrameMaxZ(world, x, y, z);
      if (frame_max_x - frame_min_x > frame_max_z - frame_min_z) {
         if (world.getBlock(frame_min_x, frame_min_y, z) == runestoneMithril && world.getBlock(frame_max_x, frame_min_y, z) == runestoneMithril && world.getBlock(frame_min_x, frame_max_y, z) == runestoneMithril && world.getBlock(frame_max_x, frame_max_y, z) == runestoneMithril) {
            return runestoneMithril;
         } else {
            return world.getBlock(frame_min_x, frame_min_y, z) == runestoneAdamantium && world.getBlock(frame_max_x, frame_min_y, z) == runestoneAdamantium && world.getBlock(frame_min_x, frame_max_y, z) == runestoneAdamantium && world.getBlock(frame_max_x, frame_max_y, z) == runestoneAdamantium ? runestoneAdamantium : null;
         }
      } else if (world.getBlock(x, frame_min_y, frame_min_z) == runestoneMithril && world.getBlock(x, frame_min_y, frame_max_z) == runestoneMithril && world.getBlock(x, frame_max_y, frame_min_z) == runestoneMithril && world.getBlock(x, frame_max_y, frame_max_z) == runestoneMithril) {
         return runestoneMithril;
      } else {
         return world.getBlock(x, frame_min_y, frame_min_z) == runestoneAdamantium && world.getBlock(x, frame_min_y, frame_max_z) == runestoneAdamantium && world.getBlock(x, frame_max_y, frame_min_z) == runestoneAdamantium && world.getBlock(x, frame_max_y, frame_max_z) == runestoneAdamantium ? runestoneAdamantium : null;
      }
   }

   public boolean isRunegate(World world, int x, int y, int z, boolean intensive_check) {
      if (!intensive_check) {
         return this.isRunegate(world.getBlockMetadata(x, y, z));
      } else {
         return this.getRunegateType(world, x, y, z) != null;
      }
   }

   private int getRunegateSeed(World world, int x, int y, int z) {
      int frame_min_x = this.getFrameMinX(world, x, y, z);
      int frame_max_x = this.getFrameMaxX(world, x, y, z);
      int frame_min_y = this.getFrameMinY(world, x, y, z);
      int frame_max_y = this.getFrameMaxY(world, x, y, z);
      int frame_min_z = this.getFrameMinZ(world, x, y, z);
      int frame_max_z = this.getFrameMaxZ(world, x, y, z);
      return frame_max_x - frame_min_x > frame_max_z - frame_min_z ? world.getBlockMetadata(frame_min_x, frame_min_y, z) + (world.getBlockMetadata(frame_max_x, frame_min_y, z) << 4) + (world.getBlockMetadata(frame_min_x, frame_max_y, z) << 8) + (world.getBlockMetadata(frame_max_x, frame_max_y, z) << 12) : world.getBlockMetadata(x, frame_min_y, frame_min_z) + (world.getBlockMetadata(x, frame_min_y, frame_max_z) << 4) + (world.getBlockMetadata(x, frame_max_y, frame_min_z) << 8) + (world.getBlockMetadata(x, frame_max_y, frame_max_z) << 12);
   }

   public boolean isTouchingBottomBedrock(World world, int x, int y, int z) {
      int frame_min_y = this.getFrameMinY(world, x, y, z);
      if (frame_min_y > 8) {
         return false;
      } else {
         int frame_min_x = this.getFrameMinX(world, x, y, z);
         int frame_max_x = this.getFrameMaxX(world, x, y, z);
         int frame_min_z = this.getFrameMinZ(world, x, y, z);
         int frame_max_z = this.getFrameMaxZ(world, x, y, z);

         for(int frame_x = frame_min_x; frame_x <= frame_max_x; ++frame_x) {
            for(int frame_z = frame_min_z; frame_z <= frame_max_z; ++frame_z) {
               if (world.isBottomBlock(frame_x, frame_min_y - 1, frame_z)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static int getDestinationBitForDimensionId(int destination_dimension_id) {
      if (destination_dimension_id == 0) {
         return 0;
      } else if (destination_dimension_id == -2) {
         return 1;
      } else if (destination_dimension_id == -1) {
         return 2;
      } else {
         Minecraft.setErrorMessage("getDestinationBitForDimensionId: destination_dimension_id not handled " + destination_dimension_id);
         return 0;
      }
   }

   public static int getDestinationBit(World world) {
      return getDestinationBitForDimensionId(world.getDimensionId());
   }

   public int getPortalTypeBasedOnLocation(World world, int x, int y, int z, boolean test_for_runegate) {
      if (test_for_runegate && this.isRunegate(world, x, y, z, true)) {
         return 8 | getDestinationBit(world);
      } else if (world.isTheNether()) {
         return 1;
      } else if (this.isTouchingBottomBedrock(world, x, y, z)) {
         return world.isOverworld() ? 1 : 2;
      } else {
         return 0;
      }
   }

   public static int getDestinationDimensionIdForMetadata(int metadata) {
      int destination_bit = getDestinationBit(metadata);
      if (destination_bit == 0) {
         return 0;
      } else if (destination_bit == 1) {
         return -2;
      } else if (destination_bit == 2) {
         return -1;
      } else {
         Minecraft.setErrorMessage("getDestinationDimensionIdForMetadata: no handler for destination_bit " + destination_bit);
         return 0;
      }
   }

   public int getDestinationDimensionID(World world, int x, int y, int z) {
      return getDestinationDimensionIdForMetadata(world.getBlockMetadata(x, y, z));
   }

   public static boolean isGoodSpotForPlayerToAppearAt(World world, int x, int y, int z) {
      return world.isAirOrPassableBlock(x, y, z, false) && world.isAirOrPassableBlock(x, y + 1, z, false) && !world.isAirOrPassableBlock(x, y - 1, z, false) && !world.isLavaBlock(x, y - 1, z) && !world.isCeilingBedrock(x, y - 1, z);
   }

   public int[] getRunegateDestinationCoords(WorldServer world, int x, int y, int z) {
      int seed = this.getRunegateSeed(world, x, y, z);
      BlockRunestone block_runestone = this.getRunegateType(world, x, y, z);
      int chunk_z;
      int runegate_domain_radius;
      if (seed == 0) {
         x = 0;
         z = 0;
      } else {
         Random random = new Random((long)seed);

         for(chunk_z = 0; chunk_z < 4; ++chunk_z) {
            runegate_domain_radius = world.getRunegateDomainRadius(block_runestone == Block.runestoneAdamantium ? Material.adamantium : Material.mithril);
            x = random.nextInt(runegate_domain_radius * 2) - runegate_domain_radius;

            for(z = random.nextInt(runegate_domain_radius * 2) - runegate_domain_radius; block_runestone == Block.runestoneAdamantium && WorldServer.getDistanceFromDeltas((double)x, (double)z) < (double)(runegate_domain_radius / 2); z = random.nextInt(runegate_domain_radius * 2) - runegate_domain_radius) {
               x = random.nextInt(runegate_domain_radius * 2) - runegate_domain_radius;
            }

            if (world.getBiomeGenForCoords(x, z) != BiomeGenBase.ocean) {
               break;
            }
         }
      }

      int chunk_x = x >> 4;
      chunk_z = z >> 4;

      for(runegate_domain_radius = -1; runegate_domain_radius <= 1; ++runegate_domain_radius) {
         for(int dz = -1; dz <= 1; ++dz) {
            world.chunkProvider.provideChunk(chunk_x + runegate_domain_radius, chunk_z + dz);
         }
      }

      if (world.isTheNether()) {
         y = 0;

         while(true) {
            ++y;
            if (y >= 254) {
               break;
            }

            if (world.isAirOrPassableBlock(x, y, z, false)) {
               ++y;
               if (world.isAirOrPassableBlock(x, y, z, false) && !world.isAirOrPassableBlock(x, y - 2, z, false) && !world.isLavaBlock(x, y - 2, z) && !world.isLavaBlock(x, y - 1, z)) {
                  return new int[]{x, y - 1, z};
               }
            }
         }
      } else if (world.isUnderworld()) {
         y = 254;

         while(true) {
            --y;
            if (y <= 0) {
               break;
            }

            if (isGoodSpotForPlayerToAppearAt(world, x, y, z)) {
               return new int[]{x, y, z};
            }
         }
      } else {
         y = 256;

         while(true) {
            --y;
            if (y <= 0) {
               break;
            }

            if (world.isAirOrPassableBlock(x, y, z, false)) {
               --y;
               if (world.isAirOrPassableBlock(x, y, z, false)) {
                  while(y > 0 && world.isAirOrPassableBlock(x, y - 1, z, false)) {
                     --y;
                  }

                  if (y == 0) {
                     y = 64;
                  }

                  return new int[]{x, y, z};
               }
            }
         }
      }

      if (!world.isAirOrPassableBlock(x, 64, z, true)) {
         world.setBlockToAir(x, 64, z);
      }

      if (!world.isAirOrPassableBlock(x, 65, z, true)) {
         world.setBlockToAir(x, 65, z);
      }

      return new int[]{x, 64, z};
   }

   private boolean updateType(World world, int x, int y, int z) {
      int metadata = world.getBlockMetadata(x, y, z);
      int new_metadata = this.getPortalTypeBasedOnLocation(world, x, y, z, true);
      return new_metadata != metadata && this.isRunegate(new_metadata) != this.isRunegate(metadata) ? world.setBlockMetadataWithNotify(x, y, z, new_metadata, 3) : false;
   }

   private void initiateRunegateTeleport(WorldServer world, int x, int y, int z, EntityPlayerMP player, boolean is_portal_to_world_spawn) {
      player.is_runegate_teleporting = true;
      player.runegate_destination_coords = is_portal_to_world_spawn ? new int[]{world.getSpawnX(), world.getTopSolidOrLiquidBlockMITE(world.getSpawnX(), world.getSpawnZ(), false) + 1, world.getSpawnZ()} : this.getRunegateDestinationCoords(world, x, y, z);
      player.playerNetServerHandler.sendPacketToPlayer(new Packet85SimpleSignal(EnumSignal.runegate_start));
      player.prevent_runegate_achievement = is_portal_to_world_spawn;
   }

   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      EntityPlayer player = par5Entity instanceof EntityPlayer ? (EntityPlayer)par5Entity : null;
      if (player == null || !player.is_runegate_teleporting) {
         int metadata = par1World.getBlockMetadata(par2, par3, par4);
         boolean is_runegate = this.isRunegate(metadata);
         boolean is_portal_to_world_spawn = this.isPortalToOverworldSpawn(par1World, metadata);
         if (!is_runegate && !is_portal_to_world_spawn) {
            if (par5Entity.ridingEntity == null && par5Entity.riddenByEntity == null) {
               par5Entity.setInPortal(getDestinationDimensionIdForMetadata(metadata));
            }

         } else if (!par1World.isRemote && player != null) {
            if (player.ridingEntity == null && player.riddenByEntity == null) {
               this.initiateRunegateTeleport((WorldServer)par1World, par2, par3, par4, (EntityPlayerMP)player, is_portal_to_world_spawn);
            }
         }
      }
   }

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      int metadata = par1World.getBlockMetadata(par2, par3, par4);
      boolean is_runegate = this.isRunegate(metadata);
      boolean is_portal_to_overworld = this.isPortalToOverworld(metadata, true);
      this.isPortalToUnderworld(metadata, true);
      boolean is_portal_to_nether = this.isPortalToNether(metadata, true);
      if (par5Random.nextInt(100) == 0) {
         par1World.playSound((double)par2 + 0.5, (double)par3 + 0.5, (double)par4 + 0.5, (!is_runegate || !is_portal_to_overworld) && !this.isPortalToOverworldSpawn(par1World, metadata) ? "portal.portal" : "imported.portal.runegate", 0.5F, par5Random.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int var6 = 0; var6 < 4; ++var6) {
         double var7 = (double)((float)par2 + par5Random.nextFloat());
         double var9 = (double)((float)par3 + par5Random.nextFloat());
         double var11 = (double)((float)par4 + par5Random.nextFloat());
         double var13 = 0.0;
         double var15 = 0.0;
         double var17 = 0.0;
         int var19 = par5Random.nextInt(2) * 2 - 1;
         var13 = ((double)par5Random.nextFloat() - 0.5) * 0.5;
         var15 = ((double)par5Random.nextFloat() - 0.5) * 0.5;
         var17 = ((double)par5Random.nextFloat() - 0.5) * 0.5;
         if (par1World.getBlockId(par2 - 1, par3, par4) != this.blockID && par1World.getBlockId(par2 + 1, par3, par4) != this.blockID) {
            var7 = (double)par2 + 0.5 + 0.25 * (double)var19;
            var13 = (double)(par5Random.nextFloat() * 2.0F * (float)var19);
         } else {
            var11 = (double)par4 + 0.5 + 0.25 * (double)var19;
            var17 = (double)(par5Random.nextFloat() * 2.0F * (float)var19);
         }

         par1World.spawnParticle(is_portal_to_overworld ? EnumParticle.runegate : (is_portal_to_nether ? EnumParticle.portal_nether : EnumParticle.portal_underworld), var7, var9, var11, var13, var15, var17);
      }

   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return 0;
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return 0;
   }

   public Icon getIcon(int side, int metadata) {
      int destination_bit = getDestinationBit(metadata);
      return destination_bit == 0 ? this.runegate_icon : (destination_bit == 1 ? this.blockIcon : this.nether_portal_icon);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      super.registerIcons(par1IconRegister);
      this.runegate_icon = par1IconRegister.registerIcon("runegate");
      this.nether_portal_icon = par1IconRegister.registerIcon("portal_nether");
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return true;
   }
}
