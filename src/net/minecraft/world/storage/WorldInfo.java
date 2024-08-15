package net.minecraft.world.storage;

import java.util.HashMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.stats.Achievement;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAchievement;
import net.minecraft.world.WorldInfoShared;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public final class WorldInfo {
   public static final int village_condition_wheat = 1;
   public static final int village_condition_carrot = 2;
   public static final int village_condition_potato = 4;
   public static final int village_condition_onion = 8;
   public static final int village_condition_iron_pickaxe_or_warhammer = 16;
   private WorldInfoShared shared;
   private int dimension_id;

   protected WorldInfo() {
      this.shared = new WorldInfoShared();
   }

   public WorldInfo(NBTTagCompound par1NBTTagCompound) {
      NBTBase.loading_world_info = true;
      this.shared = new WorldInfoShared(par1NBTTagCompound);
      NBTBase.loading_world_info = false;
   }

   public WorldInfo(WorldSettings par1WorldSettings, String par2Str) {
      this.shared = new WorldInfoShared(par1WorldSettings, par2Str);
   }

   public WorldInfo(WorldInfo world_info, int dimension_id) {
      this.shared = world_info.shared;
      this.dimension_id = dimension_id;
   }

   public NBTTagCompound getNBTTagCompound() {
      NBTTagCompound var1 = new NBTTagCompound();
      this.updateTagCompound(var1, this.shared.playerTag);
      return var1;
   }

   public NBTTagCompound cloneNBTCompound(NBTTagCompound par1NBTTagCompound) {
      NBTTagCompound var2 = new NBTTagCompound();
      this.updateTagCompound(var2, par1NBTTagCompound);
      return var2;
   }

   private void updateTagCompound(NBTTagCompound par1NBTTagCompound, NBTTagCompound par2NBTTagCompound) {
      this.shared.updateTagCompound(par1NBTTagCompound, par2NBTTagCompound);
   }

   public long getSeed() {
      return this.shared.randomSeed;
   }

   public long getHashedSeed() {
      return this.shared.random_seed_hashed;
   }

   public int getSpawnX() {
      return this.shared.spawnX;
   }

   public int getSpawnY() {
      return this.shared.spawnY;
   }

   public int getSpawnZ() {
      return this.shared.spawnZ;
   }

   public long getWorldTotalTime(int dimension_id) {
      return this.shared.getTotalWorldTime(dimension_id);
   }

   public int getWorldTimeOfDay(int dimension_id) {
      return (int)(this.getWorldTotalTime(dimension_id) % 24000L);
   }

   public long getSizeOnDisk() {
      return this.shared.sizeOnDisk;
   }

   public NBTTagCompound getPlayerNBTTagCompound() {
      return this.shared.playerTag;
   }

   public int getVanillaDimension() {
      return this.shared.dimension;
   }

   public void setSpawnX(int par1) {
      if (this.dimension_id == 0) {
         this.shared.spawnX = par1;
      }

   }

   public void setSpawnY(int par1) {
      if (this.dimension_id == 0) {
         this.shared.spawnY = par1;
      }

   }

   public void setSpawnZ(int par1) {
      if (this.dimension_id == 0) {
         this.shared.spawnZ = par1;
      }

   }

   public void setTotalWorldTime(long total_world_time, World world) {
      if (Minecraft.inDevMode() && world.getDimensionId() != this.dimension_id) {
         Minecraft.setErrorMessage("setTotalWorldTime: dimension id discrepency: " + world.getDimensionId() + " vs " + this.dimension_id);
         (new Exception()).printStackTrace();
      }

      this.shared.setTotalWorldTime(world, total_world_time);
   }

   public void setTotalWorldTimes(long[] total_world_times, WorldClient world) {
      this.shared.setTotalWorldTimes(total_world_times, world);
   }

   public void setSpawnPosition(int par1, int par2, int par3) {
      if (this.dimension_id == 0) {
         this.shared.spawnX = par1;
         this.shared.spawnY = par2;
         this.shared.spawnZ = par3;
      }

   }

   public String getWorldName() {
      return this.shared.levelName;
   }

   public void setWorldName(String par1Str) {
      if (this.dimension_id == 0) {
         this.shared.levelName = par1Str;
      }

   }

   public int getSaveVersion() {
      return this.shared.saveVersion;
   }

   public void setSaveVersion(int par1) {
      if (this.dimension_id == 0) {
         this.shared.saveVersion = par1;
      }

   }

   public long getLastTimePlayed() {
      return this.shared.lastTimePlayed;
   }

   public EnumGameType getGameType() {
      if (!Minecraft.inDevMode()) {
         this.shared.theGameType = EnumGameType.SURVIVAL;
      }

      return this.shared.theGameType;
   }

   public boolean isMapFeaturesEnabled() {
      return this.shared.mapFeaturesEnabled;
   }

   public void setGameType(EnumGameType par1EnumGameType) {
      if (!Minecraft.inDevMode()) {
         par1EnumGameType = EnumGameType.SURVIVAL;
      }

      this.shared.theGameType = par1EnumGameType;
   }

   public boolean isHardcoreModeEnabled() {
      return this.shared.hardcore;
   }

   public WorldType getTerrainType() {
      return this.shared.terrainType;
   }

   public void setTerrainType(WorldType par1WorldType) {
      if (this.dimension_id == 0) {
         this.shared.terrainType = par1WorldType;
      }

   }

   public String getGeneratorOptions() {
      return this.shared.generatorOptions;
   }

   public boolean areCommandsAllowed() {
      return Minecraft.inDevMode();
   }

   public boolean isInitialized() {
      return this.shared.initialized;
   }

   public void setServerInitialized(boolean par1) {
      if (this.dimension_id == 0) {
         this.shared.initialized = par1;
      }

   }

   public GameRules getGameRulesInstance() {
      return this.shared.theGameRules;
   }

   public void addToCrashReport(CrashReportCategory par1CrashReportCategory) {
      par1CrashReportCategory.addCrashSectionCallable("Level seed", new CallableLevelSeed(this));
      par1CrashReportCategory.addCrashSectionCallable("Level generator", new CallableLevelGenerator(this));
      par1CrashReportCategory.addCrashSectionCallable("Level generator options", new CallableLevelGeneratorOptions(this));
      par1CrashReportCategory.addCrashSectionCallable("Level spawn location", new CallableLevelSpawnLocation(this));
      par1CrashReportCategory.addCrashSectionCallable("Level time", new CallableLevelTime(this));
      par1CrashReportCategory.addCrashSectionCallable("Level dimension", new CallableLevelDimension(this));
      par1CrashReportCategory.addCrashSectionCallable("Level storage version", new CallableLevelStorageVersion(this));
      par1CrashReportCategory.addCrashSectionCallable("Level weather", new CallableLevelWeather(this));
      par1CrashReportCategory.addCrashSectionCallable("Level game mode", new CallableLevelGamemode(this));
   }

   static WorldType getTerrainTypeOfWorld(WorldInfo par0WorldInfo) {
      return par0WorldInfo.shared.terrainType;
   }

   static boolean getMapFeaturesEnabled(WorldInfo par0WorldInfo) {
      return par0WorldInfo.shared.mapFeaturesEnabled;
   }

   static String getWorldGeneratorOptions(WorldInfo par0WorldInfo) {
      return par0WorldInfo.shared.generatorOptions;
   }

   static int getSpawnXCoordinate(WorldInfo par0WorldInfo) {
      return par0WorldInfo.shared.spawnX;
   }

   static int getSpawnYCoordinate(WorldInfo par0WorldInfo) {
      return par0WorldInfo.shared.spawnY;
   }

   static int getSpawnZCoordinate(WorldInfo par0WorldInfo) {
      return par0WorldInfo.shared.spawnZ;
   }

   static long func_85126_g(WorldInfo par0WorldInfo) {
      return par0WorldInfo.shared.getTotalWorldTime(0);
   }

   static final long getWorldTimeOfDay(WorldInfo par0WorldInfo, int dimension_id) {
      return (long)par0WorldInfo.getWorldTimeOfDay(dimension_id);
   }

   static int func_85122_i(WorldInfo par0WorldInfo) {
      return par0WorldInfo.shared.dimension;
   }

   static int getSaveVersion(WorldInfo par0WorldInfo) {
      return par0WorldInfo.shared.saveVersion;
   }

   static EnumGameType getGameType(WorldInfo par0WorldInfo) {
      return par0WorldInfo.shared.theGameType;
   }

   static boolean func_85117_p(WorldInfo par0WorldInfo) {
      return par0WorldInfo.shared.hardcore;
   }

   static boolean func_85131_q(WorldInfo par0WorldInfo) {
      return par0WorldInfo.shared.allowCommands;
   }

   public void fullfillVillageCondition(int condition, WorldServer world_server) {
      WorldInfoShared var10000 = this.shared;
      var10000.village_conditions = (byte)(var10000.village_conditions | condition);
      world_server.sendPacketToAllPlayersInThisDimension(new Packet70GameEvent(7, this.shared.village_conditions));
   }

   public int calcChecksum() {
      return this.shared.calcChecksum();
   }

   public void setUnderworldVisited() {
      this.shared.the_underworld_has_been_visited = true;
   }

   public boolean getUnderworldHasBeenVisited() {
      return this.shared.the_underworld_has_been_visited;
   }

   public void setNetherVisited() {
      this.shared.the_nether_has_been_visited = true;
   }

   public boolean getNetherHasBeenVisited() {
      return this.shared.the_nether_has_been_visited;
   }

   public void setSkillsEnabled(boolean are_skills_enabled) {
      this.shared.are_skills_enabled = are_skills_enabled;
   }

   public boolean areSkillsEnabled() {
      return this.shared.are_skills_enabled;
   }

   public boolean areCoinsEnabled() {
      return true;
   }

   public void setEarliestMITEReleaseRunIn(int earliest_MITE_release_run_in) {
      this.shared.earliest_MITE_release_run_in = earliest_MITE_release_run_in;
   }

   public void setLatestMITEReleaseRunIn(int latest_MITE_release_run_in) {
      this.shared.latest_MITE_release_run_in = latest_MITE_release_run_in;
   }

   public int getEarliestMITEReleaseRunIn() {
      return this.shared.earliest_MITE_release_run_in;
   }

   public int getLatestMITEReleaseRunIn() {
      return this.shared.latest_MITE_release_run_in;
   }

   public List getCurses() {
      return this.shared.curses;
   }

   public boolean isValidMITEWorld() {
      return this.shared.is_valid_MITE_world;
   }

   public void setVillageConditions(byte village_conditions) {
      this.shared.village_conditions = village_conditions;
   }

   public byte getVillageConditions() {
      return this.shared.village_conditions;
   }

   public static byte getVillagePrerequisites() {
      return 16;
   }

   public void setWorldCreationTime(long world_creation_time) {
      this.shared.world_creation_time = world_creation_time;
   }

   public long getWorldCreationTime() {
      return this.shared.world_creation_time;
   }

   public void setNanotime(long nanotime) {
      this.shared.nanotime = nanotime;
   }

   public long getNanotime() {
      return this.shared.nanotime;
   }

   public String getIsNotValidReason() {
      return this.shared.is_not_valid_reason;
   }

   public void incrementSacredSandstonesPlaced() {
      ++this.shared.sacred_stones_placed;
   }

   public void setDimensionId(int dimension_id) {
      this.dimension_id = dimension_id;
   }

   public int getDimensionId() {
      return this.dimension_id;
   }

   public boolean hasSignatureBeenAdded(int id) {
      return this.shared.hasSignatureBeenAdded(id);
   }

   public void addSignature(int id) {
      this.shared.addSignature(id);
   }

   public boolean removeSignature(int id) {
      return this.shared.removeSignature(id);
   }

   public int getNumSignatures() {
      return this.shared.getNumSignatures();
   }

   public boolean hasAchievementUnlocked(Achievement achievement) {
      return this.shared.hasAchievementUnlocked(achievement);
   }

   public boolean hasAchievementUnlockedOrIsNull(Achievement achievement) {
      return achievement == null || this.hasAchievementUnlocked(achievement);
   }

   public void unlockAchievement(Achievement achievement, String username, int day, boolean update_clients) {
      this.shared.unlockAchievement(achievement, username, day, update_clients);
   }

   public void unlockAchievement(Achievement achievement, EntityPlayer player) {
      this.shared.unlockAchievement(achievement, player);
   }

   public void setAchievements(HashMap achievements) {
      this.shared.setAchievements(achievements);
   }

   public HashMap getAchievements() {
      return this.shared.getAchievements();
   }

   public WorldAchievement getWorldAchievement(Achievement achievement) {
      return this.shared.getWorldAchievement(achievement);
   }

   public boolean haveAchievementsBeenUnlockedByOtherPlayers(EntityPlayer player) {
      return this.shared.haveAchievementsBeenUnlockedByOtherPlayers(player);
   }

   public void setEarliestAllowableMITERelease(int earliest_allowable_MITE_release) {
      if (this.shared.earliest_allowable_MITE_release < earliest_allowable_MITE_release) {
         this.shared.earliest_allowable_MITE_release = earliest_allowable_MITE_release;
      }

   }
}
