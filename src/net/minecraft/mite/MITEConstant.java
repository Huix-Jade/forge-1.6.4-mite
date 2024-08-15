package net.minecraft.mite;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public final class MITEConstant {
   public static final int SQUARE_OF_16 = 256;
   public static final int SQUARE_OF_24 = 576;
   public static final int SQUARE_OF_32 = 1024;
   public static final int SQUARE_OF_48 = 2304;
   public static final int SQUARE_OF_64 = 4096;
   public static final int overworld_block_domain_radius = 524288;
   public static final int runegate_adamantium_to_mithril_distance_ratio = 8;
   public static final int runegate_domain_radius_limit = 40000;
   public static final float freezing_point = 0.15F;
   public static final float silver_vs_undead = 1.25F;
   public static final float speed_boost_per_potion_level = 0.2F;
   public static final float slow_down_per_potion_level = -0.2F;
   public static final boolean sneak_delta_y_enabled = true;
   public static final float sneak_delta_y = -0.2385F;
   public static final float vision_dimming_attenuation_per_tick = 0.01F;
   public static final float frenzied_speed_boost = 1.2F;
   public static boolean sync_client_potion_attribute_modifiers_with_server = true;
   private static final boolean use_new_precipitation_height_determination = true;
   public static final float player_block_reach = 2.75F;
   public static final float player_attack_reach = 1.5F;
   public static final float player_entity_interaction_reach = 2.5F;
   public static final int burn_time_for_planks = 400;
   public static final int burn_time_for_stick = 100;
   public static final int sand_to_sandstone_ratio_in_furnace = 4;
   public static final int sand_to_glass_ratio_in_furnace = 4;
   public static final float min_temperature_for_reed_growth = 0.2F;
   public static final int ticks_per_sec_IRL = 20;
   public static final int ticks_per_1_min_IRL = 1200;
   public static final int ticks_per_hour_IRL = 72000;
   public static final int ticks_per_day_IRL = 1728000;
   public static final int ticks_per_year_IRL = 630720000;
   public static final int ticks_per_hour_IG = 1000;
   public static final int ticks_per_day_IG = 24000;
   public static final int nutrient_limit = 160000;
   public static final float sugar_content_to_insulin_response = 4.8F;
   public static final int insulin_resistance_limit = 192000;
   public static final int seconds_to_auto_respawn = 120;
   public static final int strict_disconnection_penalty_window = 1200;
   public static final int repair_cost_multiplier = 2;
   public static final int hostile_mob_spawning_max_density = 50;
   public static final float hostile_mob_spawning_rate_below_60 = 0.1F;
   public static final float hostile_mob_spawning_rate_at_60_or_higher = 0.17F;
   public static final int hostile_mob_spawning_max_mobs_near_player = 8;
   public static final int decreased_hostile_mob_spawning_period = 4000;
   public static final int increased_hostile_mob_spawning_period = 2000;
   public static boolean egg_has_essential_fats = false;
   public static final double distance_sq_from_world_spawn_to_trigger_explorer_achievement = 1.0E8;
   public static final double distance_sq_from_world_origin_to_trigger_runegate_achievement = 1.0E10;
   public static String newline = new String(System.getProperty("line.separator").getBytes());
   public static final int min_day_of_world_for_artifacts = 40;
   public static final float chance_of_phoonzang_scripture_per_stronghold_library_chest = 0.5F;
   public static final boolean phoonzang_scriptures_require_bookcase_achievement = true;
   public static final int artifact_type_phoonzang_scripture = 0;
   public static final int maxDistanceBetweenScatteredFeatures = 40;
   public static final int minDistanceBetweenScatteredFeatures = 20;
   public static final int min_day_for_village_generation = 60;
   public static final double min_distance_from_world_spawn_for_major_scattered_features = 2000.0;
   public static final double min_distance_from_world_origin_for_cave_networks = 1000.0;
   public static final double min_distance_from_world_origin_for_mushroom_caves = 1500.0;
   public static final BiomeGenBase forced_biome_for_scattered_feature_testing = null;
   public static final int SEED_DESERT = 1;
   public static final int SEED_SWAMP = 2;
   public static final int SEED_PLAINS = 3;
   public static final int SEED_ISLAND = 4;
   public static final int SEED_FOREST = 6;
   public static final int SEED_TAIGA = 8;
   public static final int SEED_ICE_PLAINS = 10;
   public static final int SEED_EXTREME_HILLS = 16;
   public static final int SEED_JUNGLE = 17;
   public static final int compression_level_for_chunk_packet_data = -1;

   public static boolean useNewPrecipitationHeightDetermination() {
      return true;
   }

   public static boolean includeEmptyChunksForLighting() {
      return false;
   }

   public static boolean usePacket51ForLargePacket52s() {
      return false;
   }

   public static boolean usePacket97MultiBlockChange() {
      return true;
   }

   public static boolean preventLightingArtifacts() {
      return true;
   }

   public static boolean considerNeighboringChunksInLightingArtifactPrevention() {
      return preventLightingArtifacts();
   }

   public static int maxRandomRaycastsPerTickForCorrectiveLightingUpdates(World world) {
      return world.hasSkylight() ? 16 : 0;
   }

   static {
      if (usePacket51ForLargePacket52s() && usePacket97MultiBlockChange()) {
         Minecraft.setErrorMessage("MITEConstant: usePacket51ForLargePacket52s() is overriding usePacket97MultiBlockChange()");
      }

   }
}
