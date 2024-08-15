//package net.minecraft.world.storage;
//
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.world.EnumGameType;
//import net.minecraft.world.GameRules;
//import net.minecraft.world.WorldType;
//
//public class DerivedWorldInfo extends WorldInfo {
//   private final WorldInfo theWorldInfo;
//
//   public DerivedWorldInfo(WorldInfo var1) {
//      this.theWorldInfo = var1;
//   }
//
//   public NBTTagCompound getNBTTagCompound() {
//      return this.theWorldInfo.getNBTTagCompound();
//   }
//
//   public NBTTagCompound cloneNBTCompound(NBTTagCompound var1) {
//      return this.theWorldInfo.cloneNBTCompound(var1);
//   }
//
//   public long getSeed() {
//      return this.theWorldInfo.getSeed();
//   }
//
//   public int getSpawnX() {
//      return this.theWorldInfo.getSpawnX();
//   }
//
//   public int getSpawnY() {
//      return this.theWorldInfo.getSpawnY();
//   }
//
//   public int getSpawnZ() {
//      return this.theWorldInfo.getSpawnZ();
//   }
//
//   public long getWorldTotalTime() {
//      return this.theWorldInfo.f();
//   }
//
//   public long getWorldTime() {
//      return this.theWorldInfo.g();
//   }
//
//   public long getSizeOnDisk() {
//      return this.theWorldInfo.getSizeOnDisk();
//   }
//
//   public NBTTagCompound getPlayerNBTTagCompound() {
//      return this.theWorldInfo.getPlayerNBTTagCompound();
//   }
//
//   public int getVanillaDimension() {
//      return this.theWorldInfo.getVanillaDimension();
//   }
//
//   public String getWorldName() {
//      return this.theWorldInfo.getWorldName();
//   }
//
//   public int getSaveVersion() {
//      return this.theWorldInfo.getSaveVersion();
//   }
//
//   public long getLastTimePlayed() {
//      return this.theWorldInfo.getLastTimePlayed();
//   }
//
//   public boolean isThundering() {
//      return this.theWorldInfo.n();
//   }
//
//   public int getThunderTime() {
//      return this.theWorldInfo.o();
//   }
//
//   public boolean isRaining() {
//      return this.theWorldInfo.p();
//   }
//
//   public int getRainTime() {
//      return this.theWorldInfo.q();
//   }
//
//   public EnumGameType getGameType() {
//      return this.theWorldInfo.getGameType();
//   }
//
//   public void setSpawnX(int var1) {
//   }
//
//   public void setSpawnY(int var1) {
//   }
//
//   public void setSpawnZ(int var1) {
//   }
//
//   public void incrementTotalWorldTime(long var1) {
//   }
//
//   public void setWorldTime(long var1) {
//   }
//
//   public void setSpawnPosition(int var1, int var2, int var3) {
//   }
//
//   public void setWorldName(String var1) {
//   }
//
//   public void setSaveVersion(int var1) {
//   }
//
//   public void setThundering(boolean var1) {
//   }
//
//   public void setThunderTime(int var1) {
//   }
//
//   public void setRaining(boolean var1) {
//   }
//
//   public void setRainTime(int var1) {
//   }
//
//   public boolean isMapFeaturesEnabled() {
//      return this.theWorldInfo.isMapFeaturesEnabled();
//   }
//
//   public boolean isHardcoreModeEnabled() {
//      return this.theWorldInfo.isHardcoreModeEnabled();
//   }
//
//   public WorldType getTerrainType() {
//      return this.theWorldInfo.getTerrainType();
//   }
//
//   public void setTerrainType(WorldType var1) {
//   }
//
//   public boolean areCommandsAllowed() {
//      return this.theWorldInfo.areCommandsAllowed();
//   }
//
//   public boolean isInitialized() {
//      return this.theWorldInfo.isInitialized();
//   }
//
//   public void setServerInitialized(boolean var1) {
//   }
//
//   public GameRules getGameRulesInstance() {
//      return this.theWorldInfo.getGameRulesInstance();
//   }
//}
