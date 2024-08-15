package net.minecraft.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet93WorldAchievement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.Curse;
import net.minecraft.util.Translator;

public class WorldInfoShared {
   public long randomSeed;
   public long random_seed_hashed;
   public WorldType terrainType;
   public String generatorOptions;
   public int spawnX;
   public int spawnY;
   public int spawnZ;
   private long[] totalTime = new long[4];
   public long lastTimePlayed;
   public long sizeOnDisk;
   public NBTTagCompound playerTag;
   public int dimension;
   public String levelName;
   public int saveVersion;
   public EnumGameType theGameType;
   public boolean mapFeaturesEnabled;
   public boolean hardcore;
   public boolean allowCommands;
   public boolean initialized;
   public GameRules theGameRules;
   public byte village_conditions;
   public int earliest_MITE_release_run_in = 196;
   public int latest_MITE_release_run_in = 196;
   public int earliest_allowable_MITE_release;
   public boolean is_valid_MITE_world = true;
   public String is_not_valid_reason;
   public int sacred_stones_placed;
   public List curses = new ArrayList();
   public long world_creation_time;
   public long nanotime;
   public boolean the_underworld_has_been_visited;
   public boolean the_nether_has_been_visited;
   public boolean are_skills_enabled;
   private List uniques = new ArrayList();
   private HashMap achievements = new HashMap();

   public WorldInfoShared() {
      this.terrainType = WorldType.DEFAULT;
      this.generatorOptions = "";
      this.theGameRules = new GameRules();
   }

   private long getRandomSeedHashed(long random_seed) {
      Random random = new Random(random_seed);
      random.nextInt();
      return random.nextLong();
   }

   public WorldInfoShared(NBTTagCompound par1NBTTagCompound) {
      this.terrainType = WorldType.DEFAULT;
      this.generatorOptions = "";
      this.theGameRules = new GameRules();
      this.randomSeed = par1NBTTagCompound.getLong("RandomSeed");
      this.random_seed_hashed = this.getRandomSeedHashed(this.randomSeed);
      int i;
      if (par1NBTTagCompound.hasKey("generatorName")) {
         String var2 = par1NBTTagCompound.getString("generatorName");
         this.terrainType = WorldType.parseWorldType(var2);
         if (this.terrainType == null) {
            this.terrainType = WorldType.DEFAULT;
         } else if (this.terrainType.isVersioned()) {
            i = 0;
            if (par1NBTTagCompound.hasKey("generatorVersion")) {
               i = par1NBTTagCompound.getInteger("generatorVersion");
            }

            this.terrainType = this.terrainType.getWorldTypeForGeneratorVersion(i);
         }

         if (par1NBTTagCompound.hasKey("generatorOptions")) {
            this.generatorOptions = par1NBTTagCompound.getString("generatorOptions");
         }
      }

      this.theGameType = EnumGameType.getByID(par1NBTTagCompound.getInteger("GameType"));
      if (par1NBTTagCompound.hasKey("MapFeatures")) {
         this.mapFeaturesEnabled = par1NBTTagCompound.getBoolean("MapFeatures");
      } else {
         this.mapFeaturesEnabled = true;
      }

      this.spawnX = par1NBTTagCompound.getInteger("SpawnX");
      this.spawnY = par1NBTTagCompound.getInteger("SpawnY");
      this.spawnZ = par1NBTTagCompound.getInteger("SpawnZ");

      for(int j = 0; j < this.totalTime.length; ++j) {
         this.totalTime[j] = par1NBTTagCompound.getLong("Time" + j);
      }

      if (par1NBTTagCompound.hasKey("Time") || par1NBTTagCompound.hasKey("DayTime")) {
         this.is_valid_MITE_world = false;
         this.is_not_valid_reason = Translator.get("invalidWorld.outdated");
      }

      this.lastTimePlayed = par1NBTTagCompound.getLong("LastPlayed");
      this.sizeOnDisk = par1NBTTagCompound.getLong("SizeOnDisk");
      this.levelName = par1NBTTagCompound.getString("LevelName");
      this.saveVersion = par1NBTTagCompound.getInteger("version");
      this.hardcore = par1NBTTagCompound.getBoolean("hardcore");
      if (par1NBTTagCompound.hasKey("initialized")) {
         this.initialized = par1NBTTagCompound.getBoolean("initialized");
      } else {
         this.initialized = true;
      }

      if (par1NBTTagCompound.hasKey("allowCommands")) {
         this.allowCommands = par1NBTTagCompound.getBoolean("allowCommands");
      } else {
         this.allowCommands = this.theGameType == EnumGameType.CREATIVE;
      }

      if ((!par1NBTTagCompound.hasKey("last_run_on_MITE_DS") || Main.is_MITE_DS) && (par1NBTTagCompound.hasKey("last_run_on_MITE_DS") || !Main.is_MITE_DS) && par1NBTTagCompound.hasKey("Player")) {
         this.playerTag = par1NBTTagCompound.getCompoundTag("Player");
         this.dimension = this.playerTag.getInteger("Dimension");
      }

      if (par1NBTTagCompound.hasKey("GameRules")) {
         this.theGameRules.readGameRulesFromNBT(par1NBTTagCompound.getCompoundTag("GameRules"));
      }

      this.village_conditions = par1NBTTagCompound.getByte("village_conditions");
      if (par1NBTTagCompound.hasKey("earliest_MITE_release_run_in")) {
         this.earliest_MITE_release_run_in = par1NBTTagCompound.getInteger("earliest_MITE_release_run_in");
      } else {
         this.earliest_MITE_release_run_in = 0;
         this.is_valid_MITE_world = false;
      }

      if (par1NBTTagCompound.hasKey("latest_MITE_release_run_in")) {
         this.latest_MITE_release_run_in = par1NBTTagCompound.getInteger("latest_MITE_release_run_in");
      } else {
         this.is_valid_MITE_world = false;
      }

      int[] uniques;
      if (this.earliest_MITE_release_run_in < 172) {
         this.is_valid_MITE_world = false;
         this.is_not_valid_reason = Translator.get("invalidWorld.outdated");
      } else {
         uniques = Minecraft.incompatible_releases;

         for(i = 0; i < uniques.length; ++i) {
            if (this.earliest_MITE_release_run_in == uniques[i] || this.latest_MITE_release_run_in == uniques[i]) {
               this.is_valid_MITE_world = false;
               this.is_not_valid_reason = Translator.get("invalidWorld.incompatible");
               break;
            }
         }
      }

      if (this.latest_MITE_release_run_in < 196) {
         this.latest_MITE_release_run_in = 196;
      } else if (this.latest_MITE_release_run_in > 196) {
         this.is_valid_MITE_world = false;
         this.is_not_valid_reason = Translator.getFormatted("invalidWorld.needsRelease", this.latest_MITE_release_run_in);
      }

      if (par1NBTTagCompound.hasKey("earliest_allowable_MITE_release")) {
         this.earliest_allowable_MITE_release = par1NBTTagCompound.getInteger("earliest_allowable_MITE_release");
      }

      if (this.earliest_allowable_MITE_release > 196) {
         this.is_valid_MITE_world = false;
         this.is_not_valid_reason = Translator.getFormatted("invalidWorld.needsRelease", this.earliest_allowable_MITE_release);
      }

      if (par1NBTTagCompound.hasKey("curses")) {
         String[] curses = par1NBTTagCompound.getString("curses").split("\\|");

         for(i = 0; i < curses.length; ++i) {
            String[] curse_fields = curses[i].split(":");
            Curse curse = new Curse(curse_fields[0], UUID.fromString(curse_fields[1]), Curse.cursesList[Integer.valueOf(curse_fields[2])], Long.valueOf(curse_fields[3]), Boolean.valueOf(curse_fields[4]), Boolean.valueOf(curse_fields[5]));
            curse.effect_has_already_been_learned = curse.effect_known;
            this.curses.add(curse);
         }
      }

      if (par1NBTTagCompound.hasKey("sacred_stones_placed")) {
         this.sacred_stones_placed = par1NBTTagCompound.getInteger("sacred_stones_placed");
      }

      this.the_underworld_has_been_visited = par1NBTTagCompound.getBoolean("the_underworld_has_been_visited");
      this.the_nether_has_been_visited = par1NBTTagCompound.getBoolean("the_nether_has_been_visited");
      this.are_skills_enabled = par1NBTTagCompound.getBoolean("are_skills_enabled");
      if (this.initialized) {
         if (par1NBTTagCompound.hasKey("world_creation_time")) {
            this.world_creation_time = par1NBTTagCompound.getLong("world_creation_time");
         } else {
            this.is_valid_MITE_world = false;
         }

         if (par1NBTTagCompound.hasKey("nanotime")) {
            this.nanotime = par1NBTTagCompound.getLong("nanotime");
         } else {
            this.is_valid_MITE_world = false;
         }

         if (this.nanotime != (long)this.calcChecksum()) {
            this.is_valid_MITE_world = false;
         }
      }

      if (!this.is_valid_MITE_world && MinecraftServer.getServer() instanceof DedicatedServer) {
         System.out.println(this.is_not_valid_reason == null ? "Invalid world" : this.is_not_valid_reason);
         System.exit(0);
      }

      if (par1NBTTagCompound.hasKey("uniques")) {
         uniques = par1NBTTagCompound.getIntArray("uniques");
         this.uniques.clear();

         for(i = 0; i < uniques.length; ++i) {
            this.uniques.add(uniques[i]);
         }
      }

      if (par1NBTTagCompound.hasKey("achievements")) {
         NBTTagList achievements = par1NBTTagCompound.getTagList("achievements");
         this.achievements.clear();

         for(i = 0; i < achievements.tagCount(); ++i) {
            WorldAchievement wa = new WorldAchievement((NBTTagCompound)achievements.tagAt(i));
            this.achievements.put(wa.achievement, wa);
         }
      }

   }

   public WorldInfoShared(WorldSettings world_settings, String level_name) {
      this.terrainType = WorldType.DEFAULT;
      this.generatorOptions = "";
      this.theGameRules = new GameRules();
      this.randomSeed = world_settings.getSeed();
      this.random_seed_hashed = this.getRandomSeedHashed(this.randomSeed);
      this.theGameType = world_settings.getGameType();
      this.mapFeaturesEnabled = world_settings.isMapFeaturesEnabled();
      this.levelName = level_name;
      this.hardcore = world_settings.getHardcoreEnabled();
      this.terrainType = world_settings.getTerrainType();
      this.generatorOptions = world_settings.func_82749_j();
      this.allowCommands = world_settings.areCommandsAllowed();
      this.initialized = false;
      this.are_skills_enabled = world_settings.areSkillsEnabled();
   }

   public void updateTagCompound(NBTTagCompound par1NBTTagCompound, NBTTagCompound par2NBTTagCompound) {
      par1NBTTagCompound.setLong("RandomSeed", this.randomSeed);
      par1NBTTagCompound.setString("generatorName", this.terrainType.getWorldTypeName());
      par1NBTTagCompound.setInteger("generatorVersion", this.terrainType.getGeneratorVersion());
      par1NBTTagCompound.setString("generatorOptions", this.generatorOptions);
      par1NBTTagCompound.setInteger("GameType", this.theGameType.getID());
      par1NBTTagCompound.setBoolean("MapFeatures", this.mapFeaturesEnabled);
      par1NBTTagCompound.setInteger("SpawnX", this.spawnX);
      par1NBTTagCompound.setInteger("SpawnY", this.spawnY);
      par1NBTTagCompound.setInteger("SpawnZ", this.spawnZ);

      for(int i = 0; i < this.totalTime.length; ++i) {
         par1NBTTagCompound.setLong("Time" + i, this.totalTime[i]);
      }

      par1NBTTagCompound.setLong("SizeOnDisk", this.sizeOnDisk);
      par1NBTTagCompound.setLong("LastPlayed", MinecraftServer.getSystemTimeMillis());
      par1NBTTagCompound.setString("LevelName", this.levelName);
      par1NBTTagCompound.setInteger("version", this.saveVersion);
      par1NBTTagCompound.setBoolean("hardcore", this.hardcore);
      par1NBTTagCompound.setBoolean("allowCommands", this.allowCommands);
      par1NBTTagCompound.setBoolean("initialized", this.initialized);
      par1NBTTagCompound.setCompoundTag("GameRules", this.theGameRules.writeGameRulesToNBT());
      par1NBTTagCompound.setByte("village_conditions", this.village_conditions);
      par1NBTTagCompound.setInteger("earliest_MITE_release_run_in", this.earliest_MITE_release_run_in);
      par1NBTTagCompound.setInteger("latest_MITE_release_run_in", this.latest_MITE_release_run_in);
      par1NBTTagCompound.setInteger("earliest_allowable_MITE_release", this.earliest_allowable_MITE_release);
      if (Main.is_MITE_DS) {
         par1NBTTagCompound.setBoolean("last_run_on_MITE_DS", true);
      }

      if (par2NBTTagCompound != null) {
         par1NBTTagCompound.setCompoundTag("Player", par2NBTTagCompound);
      }

      Iterator i;
      if (!this.curses.isEmpty()) {
         StringBuffer sb = new StringBuffer();
         i = this.curses.iterator();

         while(i.hasNext()) {
            Curse curse = (Curse)i.next();
            sb.append('|');
            sb.append(curse.cursed_player_username);
            sb.append(':');
            sb.append(curse.cursing_entity_uuid.toString());
            sb.append(':');
            sb.append(curse.id);
            sb.append(':');
            sb.append(curse.time_of_realization);
            sb.append(':');
            sb.append(curse.has_been_realized);
            sb.append(':');
            sb.append(curse.effect_known);
         }

         par1NBTTagCompound.setString("curses", sb.substring(1));
      }

      par1NBTTagCompound.setInteger("sacred_stones_placed", this.sacred_stones_placed);
      par1NBTTagCompound.setBoolean("the_underworld_has_been_visited", this.the_underworld_has_been_visited);
      par1NBTTagCompound.setBoolean("the_nether_has_been_visited", this.the_nether_has_been_visited);
      if (this.are_skills_enabled) {
         par1NBTTagCompound.setBoolean("are_skills_enabled", this.are_skills_enabled);
      }

      if (this.world_creation_time != 0L) {
         par1NBTTagCompound.setLong("world_creation_time", this.world_creation_time);
         par1NBTTagCompound.setLong("nanotime", this.nanotime);
      }

      if (this.uniques.size() > 0) {
         int[] uniques = new int[this.uniques.size()];

         for(int j = 0; j < uniques.length; ++j) {
            uniques[j] = (Integer)this.uniques.get(j);
         }

         par1NBTTagCompound.setIntArray("uniques", uniques);
      }

      if (this.achievements.size() > 0) {
         NBTTagList achievements = new NBTTagList();
         i = this.achievements.entrySet().iterator();

         while(i.hasNext()) {
            Map.Entry entry = (Map.Entry)i.next();
            achievements.appendTag(((WorldAchievement)entry.getValue()).getAsNBTTagCompound());
         }

         par1NBTTagCompound.setTag("achievements", achievements);
      }

   }

   public int calcChecksum() {
      int checksum = 0;
      checksum += this.earliest_MITE_release_run_in * 67;
      checksum += (int)this.randomSeed * 83;
      checksum = (int)((long)checksum + this.world_creation_time * 561L);
      return checksum;
   }

   public boolean hasSignatureBeenAdded(int id) {
      return this.uniques.contains(id);
   }

   public void addSignature(int id) {
      if (this.hasSignatureBeenAdded(id)) {
         Minecraft.setErrorMessage("addSignature: signature already exists in list " + id);
      } else {
         if (Minecraft.inDevMode()) {
            System.out.println("Unique added to world, signature=" + id);
         }

         this.uniques.add(id);
      }
   }

   public boolean removeSignature(int id) {
      if (Minecraft.inDevMode()) {
         System.out.println("removeSignature: trying to remove " + id);
      }

      return this.hasSignatureBeenAdded(id) && this.uniques.remove(new Integer(id));
   }

   public int getNumSignatures() {
      return this.uniques.size();
   }

   public boolean hasAchievementUnlocked(Achievement achievement) {
      return this.achievements.containsKey(achievement);
   }

   public void unlockAchievement(Achievement achievement, String username, int day, boolean update_clients) {
      if (!this.hasAchievementUnlocked(achievement)) {
         this.achievements.put(achievement, new WorldAchievement(achievement, username, day));
         if (update_clients) {
            MinecraftServer.getServer();
            MinecraftServer.sendPacketToAllPlayersOnServer(new Packet93WorldAchievement(achievement, username, day));
         }
      }

   }

   public void unlockAchievement(Achievement achievement, EntityPlayer player) {
      this.unlockAchievement(achievement, player.username, player.worldObj.getDayOfWorld(), player instanceof EntityPlayerMP);
   }

   public void setAchievements(HashMap achievements) {
      this.achievements = achievements;
   }

   public HashMap getAchievements() {
      return this.achievements;
   }

   public WorldAchievement getWorldAchievement(Achievement achievement) {
      return (WorldAchievement)this.achievements.get(achievement);
   }

   public boolean haveAchievementsBeenUnlockedByOtherPlayers(EntityPlayer player) {
      Iterator i = this.achievements.entrySet().iterator();

      WorldAchievement wa;
      do {
         if (!i.hasNext()) {
            return false;
         }

         Map.Entry entry = (Map.Entry)i.next();
         wa = (WorldAchievement)entry.getValue();
      } while(player.username.equals(wa.username));

      return true;
   }

   private static int getWorldIndexForDimensionId(int dimension_id) {
      return MinecraftServer.getWorldIndexForDimensionId(dimension_id);
   }

   public void setTotalWorldTime(World world, long total_world_time) {
      this.totalTime[getWorldIndexForDimensionId(world.getDimensionId())] = total_world_time;
      world.updateTickFlags();
   }

   public void setTotalWorldTimes(long[] total_world_times, WorldClient world) {
      for(int i = 0; i < 4; ++i) {
         this.totalTime[i] = total_world_times[i];
      }

      world.updateTickFlags();
   }

   public long getTotalWorldTime(int dimension_id) {
      return this.totalTime[getWorldIndexForDimensionId(dimension_id)];
   }
}
