package net.minecraft.world;

import net.minecraft.client.Minecraft;
import net.minecraft.world.storage.WorldInfo;

public final class WorldSettings {
   private final long seed;
   private final EnumGameType theGameType;
   private final boolean mapFeaturesEnabled;
   private final boolean hardcoreEnabled;
   private final WorldType terrainType;
   private boolean commandsAllowed;
   private boolean bonusChestEnabled;
   private String field_82751_h;
   private boolean are_skills_enabled;

   public WorldSettings(long par1, EnumGameType par3EnumGameType, boolean par4, boolean par5, WorldType par6WorldType, boolean are_skills_enabled) {
      this.field_82751_h = "";
      this.seed = par1;
      this.theGameType = par3EnumGameType;
      this.mapFeaturesEnabled = par4;
      this.hardcoreEnabled = par5;
      this.terrainType = par6WorldType;
      this.are_skills_enabled = are_skills_enabled;
   }

   public WorldSettings(WorldInfo par1WorldInfo) {
      this(par1WorldInfo.getSeed(), par1WorldInfo.getGameType(), par1WorldInfo.isMapFeaturesEnabled(), par1WorldInfo.isHardcoreModeEnabled(), par1WorldInfo.getTerrainType(), par1WorldInfo.areSkillsEnabled());
   }

   public WorldSettings enableBonusChest() {
      this.bonusChestEnabled = false;
      return this;
   }

   public WorldSettings enableCommands() {
      return this;
   }

   public WorldSettings func_82750_a(String par1Str) {
      this.field_82751_h = par1Str;
      return this;
   }

   public boolean isBonusChestEnabled() {
      return this.bonusChestEnabled;
   }

   public long getSeed() {
      return this.seed;
   }

   public EnumGameType getGameType() {
      return this.theGameType;
   }

   public boolean getHardcoreEnabled() {
      return this.hardcoreEnabled;
   }

   public boolean isMapFeaturesEnabled() {
      return this.mapFeaturesEnabled;
   }

   public WorldType getTerrainType() {
      return this.terrainType;
   }

   public boolean areCommandsAllowed() {
      return Minecraft.inDevMode();
   }

   public static EnumGameType getGameTypeById(int par0) {
      return EnumGameType.getByID(par0);
   }

   public String func_82749_j() {
      return this.field_82751_h;
   }

   public boolean areSkillsEnabled() {
      return this.are_skills_enabled;
   }
}
