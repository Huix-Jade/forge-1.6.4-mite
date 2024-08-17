package net.minecraft.world;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroomCap;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGiantVampireBat;
import net.minecraft.entity.EntityHellhound;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.EntityLongdead;
import net.minecraft.entity.EntityLongdeadGuardian;
import net.minecraft.entity.EntityPhaseSpider;
import net.minecraft.entity.EntityVampireBat;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumTournamentType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeEventFactory;

public final class SpawnerAnimals {
   private HashMap eligibleChunksForSpawning = new HashMap();
   private Random random = new Random();

   protected static ChunkPosition getRandomSpawningPointInChunk(World par0World, int par1, int par2) {
      Chunk var3 = par0World.getChunkFromChunkCoords(par1, par2);
      int var4 = par1 * 16 + par0World.rand.nextInt(16);
      int var5 = par2 * 16 + par0World.rand.nextInt(16);
      int var6 = par0World.rand.nextInt(var3 == null ? par0World.getActualHeight() : var3.getTopFilledSegment() + 16 - 0);
      return new ChunkPosition(var4, var6, var5);
   }

   private void setEligibleChunksForSpawning(WorldServer world, boolean for_hostile_mobs) {
      this.eligibleChunksForSpawning.clear();
      boolean is_daytime = world.isDaytime();
      boolean is_full_moon = world.isFullMoon();
      boolean is_blood_moon = world.isBloodMoon(false);

      for(int i = 0; i < world.playerEntities.size(); ++i) {
         EntityPlayer player = (EntityPlayer)world.playerEntities.get(i);
         if (!player.isGhost() && !player.isZevimrgvInTournament() && !player.isDead && !(player.getHealth() <= 0.0F)) {
            int density_limit;
            int minimum_density_limit;
            if (for_hostile_mobs) {
               if (world.provider.dimensionId == 0) {
                  density_limit = (int)(8.0F * (1.0F + (float)(64 - player.getFootBlockPosY()) / 32.0F));
                  minimum_density_limit = 8;
                  if (!is_daytime) {
                     if (is_blood_moon) {
                        minimum_density_limit = minimum_density_limit * 3 / 2;
                     } else if (is_full_moon) {
                        minimum_density_limit = minimum_density_limit * 5 / 4;
                     }
                  }

                  if (density_limit < minimum_density_limit) {
                     density_limit = minimum_density_limit;
                  }
               } else {
                  density_limit = 8;
               }

               density_limit = (int)((float)density_limit + (float)density_limit * world.getStrongholdProximity(player.getBlockPosX(), player.getBlockPosZ()));
               if (world.getEntitiesWithinAABB(IMob.class, player.boundingBox.expand(32.0, 8.0, 32.0)).size() > density_limit) {
                  continue;
               }
            }

            density_limit = player.getChunkPosX();
            minimum_density_limit = player.getChunkPosZ();

            for(int delta_chunk_x = -8; delta_chunk_x <= 8; ++delta_chunk_x) {
               for(int delta_chunk_z = -8; delta_chunk_z <= 8; ++delta_chunk_z) {
                  boolean is_at_edge = delta_chunk_x == -8 || delta_chunk_x == 8 || delta_chunk_z == -8 || delta_chunk_z == 8;
                  ChunkCoordIntPair chunk_coord = new ChunkCoordIntPair(density_limit + delta_chunk_x, minimum_density_limit + delta_chunk_z);
                  if (!is_at_edge) {
                     this.eligibleChunksForSpawning.put(chunk_coord, false);
                  } else if (!this.eligibleChunksForSpawning.containsKey(chunk_coord)) {
                     this.eligibleChunksForSpawning.put(chunk_coord, true);
                  }
               }
            }
         }
      }

   }

   private Class getSubstituteClassToSpawn(World world, int y, Class suitable_creature_class) {
      if (suitable_creature_class == EntityVampireBat.class && world.isUnderworld() && world.rand.nextInt(6) == 0) {
         return EntityGiantVampireBat.class;
      } else {
         return suitable_creature_class == EntityLongdead.class && world.rand.nextInt(6) == 0 ? EntityLongdeadGuardian.class : suitable_creature_class;
      }
   }

   private float tryHangBatFromCeiling(World world, EntityBat bat, int x, int y, int z, float pos_x, float pos_y, float pos_z) {
      int dy = 0;

      while(true) {
         ++dy;
         if (dy >= 16) {
            break;
         }

         if (!world.isAirBlock(x, y + dy, z)) {
            if (bat.canHangFromBlock(x, y + dy, z)) {
               bat.posX = (double)pos_x;
               bat.posY = (double)((float)(y + dy) - bat.height - 0.01F);
               bat.posZ = (double)pos_z;
               bat.setBlockHangingFromY(y + dy);
               bat.setIsBatHanging(true);
               bat.setInitialHangAttempted();
               pos_y = (float)bat.posY;
            }
            break;
         }
      }

      return pos_y;
   }

   public int trySpawningHostileMobs(WorldServer world, boolean deep_only) {
      EnumCreatureType creature_type = EnumCreatureType.monster;
      boolean is_overworld = world.isOverworld();
      boolean is_new_moon = world.isNewMoon();
      boolean is_full_moon = world.isFullMoon();
      boolean is_blood_moon = world.isBloodMoon(false);
      boolean is_blue_moon = world.isBlueMoon(false);
      float min_distance_from_players = 24.0F;
      float min_distance_from_spawn_sq = 576.0F;
      boolean is_daytime = world.isDaytime();
      int creature_limit = creature_type.getMaxNumberOfCreature() * this.eligibleChunksForSpawning.size() / 256;
      if (deep_only) {
         creature_limit *= 2;
      }

      if (deep_only) {
         world.last_mob_spawn_limit_under_60 = creature_limit;
      } else {
         world.last_mob_spawn_limit_at_60_or_higher = creature_limit;
      }

      if (world.countMobs(deep_only, !deep_only) >= creature_limit) {
         return 0;
      } else {
         boolean try_to_hang_bats_from_ceiling = world.rand.nextBoolean();
         int total_spawned = 0;
         ChunkCoordinates spawn_point = world.getSpawnPoint();
         Iterator eligible_chunk_iterator = this.eligibleChunksForSpawning.keySet().iterator();

         label251:
         while(true) {
            int x;
            int y;
            int z;
            do {
               while(true) {
                  do {
                     do {
                        ChunkCoordIntPair chunk_coord;
                        do {
                           if (!eligible_chunk_iterator.hasNext()) {
                              return total_spawned;
                           }

                           chunk_coord = (ChunkCoordIntPair)eligible_chunk_iterator.next();
                        } while((Boolean)this.eligibleChunksForSpawning.get(chunk_coord));

                        ChunkPosition chunk_pos = getRandomSpawningPointInChunk(world, chunk_coord.chunkXPos, chunk_coord.chunkZPos);
                        if (deep_only && chunk_pos.y >= 60) {
                           chunk_pos = getRandomSpawningPointInChunk(world, chunk_coord.chunkXPos, chunk_coord.chunkZPos);
                        }

                        x = chunk_pos.x;
                        y = chunk_pos.y;
                        z = chunk_pos.z;
                     } while(world.isOverworld() && y == 63 && world.rand.nextInt(4) > 0 && world.getBlock(x, y - 1, z) == Block.ice);
                  } while(world.getClosestPlayer((double)x, (double)y, (double)z, 48.0, true) == null && world.rand.nextInt(2) == 0);

                  if (deep_only) {
                     if (y < 60 && world.countMobs(true, false) < creature_limit) {
                        break;
                     }
                  } else if (y >= 60 && world.countMobs(false, true) < creature_limit) {
                     break;
                  }
               }
            } while(!canCreatureTypeSpawnAtLocation(creature_type, world, x, y, z, false, (double[])null));

            int num_spawned_below_60 = 0;
            int num_spawned_at_60_or_higher = 0;

            label249:
            for(int var18 = 0; var18 < 3; ++var18) {
               int x_with_random_offset = x;
               int z_with_random_offset = z;
               byte random_offset_range = 6;
               Class suitable_creature_class = null;
               EntityLivingData entity_living_data = null;
               int var25 = 0;
               int max_spawn_attempts = 4;

               while(true) {
                  while(true) {
                     if (var25 >= max_spawn_attempts) {
                        continue label249;
                     }

                     x_with_random_offset += world.rand.nextInt(random_offset_range) - world.rand.nextInt(random_offset_range);
                     z_with_random_offset += world.rand.nextInt(random_offset_range) - world.rand.nextInt(random_offset_range);
                     double[] resulting_y_pos = new double[1];
                     if (!canCreatureTypeSpawnAtLocation(creature_type, world, x_with_random_offset, y, z_with_random_offset, false, resulting_y_pos)) {
                        ++var25;
                     } else {
                        float pos_x = (float)x_with_random_offset + 0.5F;
                        float pos_y = (float)y;
                        float pos_z = (float)z_with_random_offset + 0.5F;
                        pos_y = (float)resulting_y_pos[0];
                        boolean can_spawn_close_to_player = (world.isOverworld() || world.isUnderworld()) && world.getClosestPlayer((double)pos_x, (double)pos_y, (double)pos_z, 24.0, true) != null && world.getBlockLightValue(x_with_random_offset, MathHelper.floor_float(pos_y), z_with_random_offset) == 0 && world.getBlockLightValue(x_with_random_offset, MathHelper.floor_float(pos_y) + 1, z_with_random_offset) == 0;
                        if (can_spawn_close_to_player) {
                           if (world.getClosestPlayer((double)pos_x, (double)pos_y, (double)pos_z, 8.0, false) != null) {
                              ++var25;
                              continue;
                           }
                        } else if (world.getClosestPlayer((double)pos_x, (double)pos_y, (double)pos_z, 24.0, false) != null) {
                           ++var25;
                           continue;
                        }

                        float delta_x = pos_x - (float)spawn_point.posX;
                        float delta_y = pos_y - (float)spawn_point.posY;
                        float delta_z = pos_z - (float)spawn_point.posZ;
                        float distance_from_spawn_point_sq = delta_x * delta_x + delta_y * delta_y + delta_z * delta_z;
                        if (distance_from_spawn_point_sq < 576.0F) {
                           ++var25;
                        } else {
                           if (suitable_creature_class == null) {
                              suitable_creature_class = world.getSuitableCreature(creature_type, x_with_random_offset, y, z_with_random_offset);
                              if (suitable_creature_class == null) {
                                 continue label249;
                              }
                           }

                           if (suitable_creature_class == EntityGhast.class && world.getClosestPlayer((double)pos_x, (double)pos_y, (double)pos_z, 48.0, false) != null) {
                              ++var25;
                           } else {
                              EntityLiving entity_living;
                              try {
                                 entity_living = (EntityLiving)this.getSubstituteClassToSpawn(world, y, suitable_creature_class).getConstructor(World.class).newInstance(world);
                              } catch (Exception var43) {
                                 Exception e = var43;
                                 e.printStackTrace();
                                 return total_spawned;
                              }

                              if (entity_living instanceof EntityBat && try_to_hang_bats_from_ceiling) {
                                 pos_y = this.tryHangBatFromCeiling(world, (EntityBat)entity_living, x_with_random_offset, y, z_with_random_offset, pos_x, pos_y, pos_z);
                              }

                              if (is_overworld && Entity.isClass(entity_living, EntityPhaseSpider.class)) {
                                 max_spawn_attempts = 64;
                              } else {
                                 max_spawn_attempts = 4;
                              }

                              entity_living.setLocationAndAngles((double)pos_x, (double)pos_y, (double)pos_z, world.rand.nextFloat() * 360.0F, 0.0F);
                              if (world.isOverworld() && world.isBlueMoonNight() && world.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, entity_living.getBlockPosX(), entity_living.getEyeBlockPosY(), entity_living.getBlockPosZ()) > 0) {
                                 ++var25;
                              } else {
                                 if (entity_living.width < 1.0F) {
                                    if (!entity_living.getCanSpawnHere(true)) {
                                       ++var25;
                                       continue;
                                    }
                                 } else {
                                    boolean can_spawn_here = entity_living.getCanSpawnHere(true);
                                    if (!can_spawn_here) {
                                       entity_living.setLocationAndAngles((double)(pos_x - 0.5F), (double)pos_y, (double)pos_z, world.rand.nextFloat() * 360.0F, 0.0F);
                                       can_spawn_here = entity_living.getCanSpawnHere(true);
                                    }

                                    if (!can_spawn_here) {
                                       entity_living.setLocationAndAngles((double)(pos_x + 0.5F), (double)pos_y, (double)pos_z, world.rand.nextFloat() * 360.0F, 0.0F);
                                       can_spawn_here = entity_living.getCanSpawnHere(true);
                                    }

                                    if (!can_spawn_here) {
                                       entity_living.setLocationAndAngles((double)pos_x, (double)pos_y, (double)(pos_z - 0.5F), world.rand.nextFloat() * 360.0F, 0.0F);
                                       can_spawn_here = entity_living.getCanSpawnHere(true);
                                    }

                                    if (!can_spawn_here) {
                                       entity_living.setLocationAndAngles((double)pos_x, (double)pos_y, (double)(pos_z + 0.5F), world.rand.nextFloat() * 360.0F, 0.0F);
                                       can_spawn_here = entity_living.getCanSpawnHere(true);
                                    }

                                    if (!can_spawn_here) {
                                       ++var25;
                                       continue;
                                    }
                                 }

                                 if (is_daytime) {
                                    if (entity_living.isEntityUndead() && world.isOutdoors(MathHelper.floor_double((double)pos_x), MathHelper.floor_double((double)pos_y), MathHelper.floor_double((double)pos_z))) {
                                       continue label251;
                                    }
                                 } else {
                                    int chance_of_skipping = is_blue_moon ? 54 : (is_blood_moon ? 2 : (is_full_moon ? 3 : (is_new_moon ? 6 : 4)));
                                    if (world.rand.nextInt(chance_of_skipping) != 0 && world.isOutdoors(MathHelper.floor_double((double)pos_x), MathHelper.floor_double((double)pos_y), MathHelper.floor_double((double)pos_z))) {
                                       continue label251;
                                    }
                                 }

                                 if (y < 60) {
                                    ++num_spawned_below_60;
                                 } else {
                                    ++num_spawned_at_60_or_higher;
                                 }

                                 entity_living_data = entity_living.onSpawnWithEgg(entity_living_data);
                                 world.spawnEntityInWorld(entity_living);
                                 ++total_spawned;
                                 if (y < 60) {
                                    if (num_spawned_below_60 >= entity_living.getMaxSpawnedInChunk()) {
                                       continue label251;
                                    }
                                 } else if (num_spawned_at_60_or_higher >= entity_living.getMaxSpawnedInChunk()) {
                                    continue label251;
                                 }

                                 ++var25;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public int trySpawningPeacefulMobs(WorldServer world, EnumCreatureType creature_type) {
      boolean is_blue_moon_animal_spawning_period = this.isBlueMoonAnimalSpawningPeriod(world);
      float min_distance_from_players = 24.0F;
      float min_distance_from_spawn_sq = 576.0F;
      int creature_limit = creature_type.getMaxNumberOfCreature() * this.eligibleChunksForSpawning.size() / 256;
      if (creature_type == EnumCreatureType.animal && DedicatedServer.isTournamentThatAllowsAnimalSpawning()) {
         creature_limit *= 2;
      }

      if (world.countEntities(creature_type.getCreatureClass()) >= creature_limit) {
         return 0;
      } else {
         boolean try_to_hang_bats_from_ceiling = world.rand.nextBoolean();
         int total_spawned = 0;
         ChunkCoordinates spawn_point = world.getSpawnPoint();
         Iterator eligible_chunk_iterator = this.eligibleChunksForSpawning.keySet().iterator();
         ArrayList<ChunkCoordIntPair> tmp = new ArrayList(eligibleChunksForSpawning.keySet());
         Collections.shuffle(tmp);
         eligible_chunk_iterator = tmp.iterator();

         label118:
         while(true) {
            int x;
            int y;
            int z;
            do {
               do {
                  ChunkCoordIntPair chunk_coord;
                  do {
                     if (!eligible_chunk_iterator.hasNext()) {
                        return total_spawned;
                     }

                     chunk_coord = (ChunkCoordIntPair)eligible_chunk_iterator.next();
                  } while((Boolean)this.eligibleChunksForSpawning.get(chunk_coord));

                  ChunkPosition chunk_pos = getRandomSpawningPointInChunk(world, chunk_coord.chunkXPos, chunk_coord.chunkZPos);
                  x = chunk_pos.x;
                  y = chunk_pos.y;
                  z = chunk_pos.z;
               } while(creature_type == EnumCreatureType.animal && DedicatedServer.tournament_type != null && !world.isWithinTournamentArena(x, z));
            } while(!canCreatureTypeSpawnAtLocation(creature_type, world, x, y, z, false, (double[])null));

            int num_spawned = 0;

            label116:
            for(int var18 = 0; var18 < 3; ++var18) {
               int x_with_random_offset = x;
               int z_with_random_offset = z;
               byte random_offset_range = 6;
               Class suitable_creature_class = null;
               EntityLivingData entity_living_data = null;
               int var25 = 0;
               int max_num_spawned = 4;

               while(true) {
                  while(true) {
                     if (var25 >= max_num_spawned) {
                        continue label116;
                     }

                     x_with_random_offset += world.rand.nextInt(random_offset_range) - world.rand.nextInt(random_offset_range);
                     z_with_random_offset += world.rand.nextInt(random_offset_range) - world.rand.nextInt(random_offset_range);
                     double[] resulting_y_pos = new double[1];
                     if (!canCreatureTypeSpawnAtLocation(creature_type, world, x_with_random_offset, y, z_with_random_offset, false, resulting_y_pos)) {
                        ++var25;
                     } else {
                        float pos_x = (float)x_with_random_offset + 0.5F;
                        float pos_y = (float)y;
                        float pos_z = (float)z_with_random_offset + 0.5F;
                        pos_y = (float)resulting_y_pos[0];
                        if (world.getClosestPlayer((double)pos_x, (double)pos_y, (double)pos_z, 24.0, false) != null) {
                           ++var25;
                        } else {
                           float delta_x = pos_x - (float)spawn_point.posX;
                           float delta_y = pos_y - (float)spawn_point.posY;
                           float delta_z = pos_z - (float)spawn_point.posZ;
                           float distance_from_spawn_point_sq = delta_x * delta_x + delta_y * delta_y + delta_z * delta_z;
                           if (distance_from_spawn_point_sq < 576.0F) {
                              ++var25;
                           } else {
                              if (suitable_creature_class == null) {
                                 suitable_creature_class = world.getSuitableCreature(creature_type, x_with_random_offset, y, z_with_random_offset);
                                 if (suitable_creature_class == null) {
                                    continue label116;
                                 }

                                 if (suitable_creature_class == EntityVampireBat.class && world.rand.nextInt(4) == 0) {
                                    max_num_spawned = 4 + world.rand.nextInt(5);
                                 }
                              }

                              EntityLiving entity_living;
                              try {
                                 entity_living = (EntityLiving)this.getSubstituteClassToSpawn(world, y, suitable_creature_class).getConstructor(World.class).newInstance(world);
                              } catch (Exception e) {
                                  e.printStackTrace();
                                 return total_spawned;
                              }

                              if (entity_living instanceof EntityBat && try_to_hang_bats_from_ceiling) {
                                 pos_y = this.tryHangBatFromCeiling(world, (EntityBat)entity_living, x_with_random_offset, y, z_with_random_offset, pos_x, pos_y, pos_z);
                              }

                              entity_living.setLocationAndAngles(pos_x, pos_y, pos_z, world.rand.nextFloat() * 360.0F, 0.0F);
                              Result canSpawn = ForgeEventFactory.canEntitySpawn(entity_living, world, pos_x, pos_y, pos_z);
                              if (canSpawn == Result.ALLOW || (canSpawn == Result.DEFAULT && entity_living.getCanSpawnHere(creature_type != EnumCreatureType.animal || !is_blue_moon_animal_spawning_period)))
                              {
                                 ++var25;
                              } else {
                                 ++num_spawned;
                                 ++total_spawned;

                                 if (!ForgeEventFactory.doSpecialSpawn(entity_living, world, pos_x, pos_y, pos_z))
                                 {
                                    entity_living_data = entity_living.onSpawnWithEgg(entity_living_data);
                                 }

                                 if (num_spawned >= ForgeEventFactory.getMaxSpawnPackSize(entity_living)) {
                                    continue label118;
                                 }

                                 ++var25;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isBlueMoonAnimalSpawningPeriod(World world) {
      return world.isBlueMoon(true);
   }

   public float calcEffectiveHostileMobSpawningRateModifier(WorldServer world) {
      if (world.provider.dimensionId != 0) {
         return 0.25F;
      } else {
         float hostile_mob_spawning_rate_modifier = 1.0F;
         if (world.decreased_hostile_mob_spawning_counter > 0) {
            --world.decreased_hostile_mob_spawning_counter;
            hostile_mob_spawning_rate_modifier *= 0.5F;
         } else if (this.random.nextInt(24000) == 0) {
            world.decreased_hostile_mob_spawning_counter = this.random.nextInt(4000) + 1;
         }

         if (world.increased_hostile_mob_spawning_counter > 0) {
            --world.increased_hostile_mob_spawning_counter;
            hostile_mob_spawning_rate_modifier *= 2.0F;
         } else if (this.random.nextInt(24000) == 0) {
            world.increased_hostile_mob_spawning_counter = this.random.nextInt(2000);
         }

         if (world.no_hostile_mob_spawning_counter > 0) {
            --world.no_hostile_mob_spawning_counter;
            hostile_mob_spawning_rate_modifier = 0.0F;
         } else if (this.random.nextInt(24000) == 0) {
            world.no_hostile_mob_spawning_counter = this.random.nextInt(2000) + this.random.nextInt(2000);
         }

         if (hostile_mob_spawning_rate_modifier < 1.0F && (world.isBloodMoon(false) || world.isThundering(true))) {
            hostile_mob_spawning_rate_modifier = 1.0F;
         }

         return hostile_mob_spawning_rate_modifier;
      }
   }

   public void performRandomLivingEntitySpawning(WorldServer world) {
      if (!CommandHandler.spawning_disabled) {
         if (world.playerEntities.size() != 0) {
            float hostile_mob_spawning_rate_modifier = this.calcEffectiveHostileMobSpawningRateModifier(world);
            float effective_hostile_mob_spawning_rate_below_60 = 0.1F * hostile_mob_spawning_rate_modifier;
            float effective_hostile_mob_spawning_rate_at_60_or_higher = 0.17F * hostile_mob_spawning_rate_modifier;
            boolean spawn_hostile_mobs_below_60 = world.spawnHostileMobs && Math.random() < (double)effective_hostile_mob_spawning_rate_below_60;
            boolean spawn_hostile_mobs_at_60_or_higher = world.spawnHostileMobs && Math.random() < (double)effective_hostile_mob_spawning_rate_at_60_or_higher;
            boolean spawn_peaceful_mobs = world.spawnPeacefulMobs && world.getTimeOfDay() % 400 == 0;
            boolean spawn_animals = DedicatedServer.tournament_type == EnumTournamentType.score && world.getTimeOfDay() % 400 == 0;
            if (!spawn_animals) {
               spawn_animals = this.isBlueMoonAnimalSpawningPeriod(world) && world.getTimeOfDay() % 400 == 0;
            }

            if (spawn_hostile_mobs_below_60 || spawn_hostile_mobs_at_60_or_higher || spawn_peaceful_mobs || spawn_animals) {
               this.setEligibleChunksForSpawning(world, true);
               if (spawn_hostile_mobs_below_60) {
                  this.trySpawningHostileMobs(world, true);
               }

               if (spawn_hostile_mobs_at_60_or_higher) {
                  this.trySpawningHostileMobs(world, false);
               }

               this.setEligibleChunksForSpawning(world, false);
               if (spawn_peaceful_mobs) {
                  this.trySpawningPeacefulMobs(world, EnumCreatureType.ambient);
                  this.trySpawningPeacefulMobs(world, EnumCreatureType.aquatic);
               }

               if (spawn_animals) {
                  this.trySpawningPeacefulMobs(world, EnumCreatureType.animal);
               }

            }
         }
      }
   }

   public static final boolean canCreatureTypeSpawnOn(World world, EnumCreatureType creature_type, Block block, int x, int y, int z
           , int metadata, boolean initial_spawn) {
      if (creature_type == EnumCreatureType.ambient) {
         return true;
      } else if (block == null) {
         return false;
      } else {
         boolean spawnBlock = block != null && block.canCreatureSpawn(creature_type, world, x, y, z);
         Material creature_spawn_material = creature_type.getCreatureMaterial();
         if (creature_spawn_material == Material.water) {
            return block.blockMaterial == Material.water;
         } else if (!spawnBlock && block != Block.bedrock && block != Block.mantleOrCore) {
            if (block != Block.mycelium && !(block instanceof BlockMushroomCap)) {
               if (creature_type == EnumCreatureType.animal) {
                  return block == Block.grass;
               } else {
                  return block == Block.snow || block == Block.waterlily || block instanceof BlockSlab || block instanceof BlockStairs || block.isBlockTopFacingSurfaceSolid(metadata);
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static final boolean canCreatureTypeSpawnAtLocation(EnumCreatureType creature_type, World world, int x, int y, int z, boolean initial_spawn, double[] resulting_y_pos) {
      if (resulting_y_pos != null) {
         resulting_y_pos[0] = (double)y;
      }

      if (y < 1) {
         return false;
      } else {
         Block block = world.getBlock(x, y, z);
         if (Block.isAlwaysSolidStandardFormCube(block)) {
            return false;
         } else {
            Material block_material = block == null ? Material.air : block.blockMaterial;
            if (block_material == Material.lava) {
               return false;
            } else if (creature_type.getCreatureMaterial() == Material.water) {
               if (block_material != Material.water) {
                  return false;
               } else {
                  return resulting_y_pos == null || world.getBlockMaterial(x, y - 1, z) == Material.water;
               }
            } else if (creature_type == EnumCreatureType.ambient) {
               return block_material == Material.air;
            } else if (!world.isBlockSolid(block, x, y, z) && block != Block.snow && block != Block.waterlily) {
               Block block_below = world.getBlock(x, y - 1, z);
               if (block_below == null || !world.isBlockSolid(block_below, x, y - 1, z) && block_below != Block.snow && block_below != Block.waterlily) {
                  return false;
               } else if (resulting_y_pos == null) {
                  return true;
               } else {
                  block_below.setBlockBoundsBasedOnStateAndNeighbors(world, x, y - 1, z);
                  resulting_y_pos[0] = (double)(y - 1) + block_below.maxY[Minecraft.getThreadIndex()] + 0.005;
                  return canCreatureTypeSpawnOn(world, creature_type, block_below, x, y - 1, z,  world.getBlockMetadata(x, y - 1, z), initial_spawn);
               }
            } else if (resulting_y_pos == null) {
               return true;
            } else {
               block.setBlockBoundsBasedOnStateAndNeighbors(world, x, y, z);
               resulting_y_pos[0] = (double)y + block.maxY[Minecraft.getThreadIndex()] + 0.005;
               return block == Block.snow ? canCreatureTypeSpawnOn(world, creature_type, world.getBlock(x, y - 1, z), x, y - 1, z, world.getBlockMetadata(x, y - 1, z), initial_spawn) : canCreatureTypeSpawnOn(world, creature_type, block, x, y, z, world.getBlockMetadata(x, y, z), initial_spawn);
            }
         }
      }
   }

   public static void performWorldGenSpawning(World world, BiomeGenBase biome, EnumCreatureType creature_type, int min_x, int min_z, int range_x, int range_z, Random random) {
      List creatures = biome.getSpawnableList(creature_type);
      if (!creatures.isEmpty()) {
         while(true) {
            SpawnListEntry entry;
            while(true) {
               if (!(random.nextFloat() < biome.getSpawningChance())) {
                  return;
               }

               entry = (SpawnListEntry)WeightedRandom.getRandomItem(world.rand, (Collection)creatures);
               if (entry.entityClass == EntityHorse.class) {
                  if (random.nextFloat() < 0.8F) {
                     continue;
                  }
                  break;
               } else if (entry.entityClass != EntityWolf.class || !(random.nextFloat() < 0.5F)) {
                  break;
               }
            }

            EntityLivingData entity_living_data = null;
            int group_size = entry.minGroupCount + random.nextInt(1 + entry.maxGroupCount - entry.minGroupCount);
            int x = min_x + random.nextInt(range_x);
            int z = min_z + random.nextInt(range_z);
            int initial_x = x;
            int initial_z = z;
            boolean is_animal = false;
            if (entry.entityClass != EntityHellhound.class) {
               for(Class super_class = entry.entityClass.getSuperclass(); super_class != null; super_class = super_class.getSuperclass()) {
                  if (super_class == EntityAnimal.class) {
                     is_animal = true;
                     break;
                  }
               }
            }

            int group_size_spawned = 0;

            for(int i = 0; i < group_size; ++i) {
               boolean spawn_successful = false;

               for(int attempt = 0; !spawn_successful && attempt < 4; ++attempt) {
                  int y = world.getTopSolidOrLiquidBlock(x, z);
                  if (creature_type == EnumCreatureType.aquatic && world.getBlockMaterial(x, y, z) == Material.water) {
                     int highest_water_y = world.getTopSolidOrLiquidBlockMITE(x, z, true);
                     if (highest_water_y - y > 3) {
                        y = random.nextInt(highest_water_y - y - 3) + y + 2;
                     }
                  }

                  double[] resulting_y_pos = new double[1];
                  if (canCreatureTypeSpawnAtLocation(creature_type, world, x, y, z, true, resulting_y_pos)) {
                     Chunk chunk = world.getChunkFromBlockCoords(x, z);
                     if (is_animal) {
                        if (chunk.animals_spawned >= 4) {
                           continue;
                        }

                        ++chunk.animals_spawned;
                     }

                     float pos_x = (float)x + 0.5F;
                     float pos_y = (float)y;
                     float pos_z = (float)z + 0.5F;
                     pos_y = (float)resulting_y_pos[0];

                     EntityLiving var22;
                     try {
                        var22 = (EntityLiving)entry.entityClass.getConstructor(World.class).newInstance(world);
                     } catch (Exception var31) {
                        Exception var24 = var31;
                        var24.printStackTrace();
                        continue;
                     }

                     int blocks_high = (int)var22.height + 1;
                     boolean placement_prevented = false;

                     for(int dy = 1; dy < blocks_high; ++dy) {
                        if (world.isBlockSolidStandardFormCube(x, y + dy, z)) {
                           placement_prevented = true;
                           break;
                        }
                     }

                     if (placement_prevented) {
                        if (Minecraft.inDevMode()) {
                           System.out.println("Prevented placement of " + var22 + " at " + StringHelper.getCoordsAsString(x, y, z));
                        }
                     } else {
                        var22.setLocationAndAngles((double)pos_x, (double)pos_y, (double)pos_z, random.nextFloat() * 360.0F, 0.0F);
                        entity_living_data = var22.onSpawnWithEgg(entity_living_data);
                        world.spawnEntityInWorld(var22);
                        spawn_successful = true;
                        ++group_size_spawned;
                        if (var22 instanceof EntityWaterMob) {
                           var22.refreshDespawnCounter(-9600);
                        }
                     }
                  }

                  x += random.nextInt(5) - random.nextInt(5);

                  for(z += random.nextInt(5) - random.nextInt(5); x < min_x || x >= min_x + range_x || z < min_z || z >= min_z + range_x; z = initial_z + random.nextInt(5) - random.nextInt(5)) {
                     x = initial_x + random.nextInt(5) - random.nextInt(5);
                  }
               }
            }
         }
      }
   }
}
