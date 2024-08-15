package net.minecraft.client.multiplayer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import net.minecraft.client.particle.EntityFireworkStarterFX;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.SoundUpdaterMinecart;
import net.minecraft.logging.ILogAgent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet255KickDisconnect;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveHandlerMP;

public final class WorldClient extends World {
   private NetClientHandler sendQueue;
   private ChunkProviderClient clientChunkProvider;
   private IntHashMap entityHashSet = new IntHashMap();
   private Set entityList = new HashSet();
   private Set entitySpawnQueue = new HashSet();
   private final Minecraft mc = Minecraft.getMinecraft();
   private final Set previousActiveChunkSet = new HashSet();
   public boolean tick_has_passed;

   public WorldClient(NetClientHandler par1NetClientHandler, WorldSettings par2WorldSettings, int par3, int par4, Profiler par5Profiler, ILogAgent par6ILogAgent, long world_creation_time, long total_world_time) {
      super(new SaveHandlerMP(), "MpServer", WorldProvider.getProviderForDimension(par3), par2WorldSettings, par5Profiler, par6ILogAgent, world_creation_time, total_world_time);
      this.sendQueue = par1NetClientHandler;
      this.difficultySetting = par4;
      this.setSpawnLocation(8, 64, 8);
      this.mapStorage = par1NetClientHandler.mapStorage;
   }

   public void tick() {
      if (Minecraft.java_version_is_outdated) {
         Minecraft.theMinecraft.thePlayer = null;
         Minecraft.theMinecraft = null;
      }

      int var1;
      if (Main.is_MITE_DS && !this.mc.inGameHasFocus) {
         this.sendQueue.processReadPackets();
         var1 = this.sendQueue.getNetManager().clearReceivedPackets();
         if (var1 > 0) {
            System.out.println(var1 + " packets cleared from the dedicated server received queue");
         }

      } else {
         super.tick();
         this.setTotalWorldTime(this.getTotalWorldTime() + 1L);
         this.skylightSubtracted = this.calculateSkylightSubtracted(1.0F);
         this.theProfiler.startSection("reEntryProcessing");

         for(var1 = 0; var1 < 10 && !this.entitySpawnQueue.isEmpty(); ++var1) {
            Entity var2 = (Entity)this.entitySpawnQueue.iterator().next();
            this.entitySpawnQueue.remove(var2);
            if (!this.loadedEntityList.contains(var2)) {
               this.spawnEntityInWorld(var2);
            }
         }

         this.theProfiler.endStartSection("connection");
         this.sendQueue.processReadPackets();
         this.theProfiler.endStartSection("chunkCache");
         this.clientChunkProvider.unloadQueuedChunks();
         this.theProfiler.endStartSection("tiles");
         this.tickBlocksAndAmbiance();
         this.theProfiler.endSection();
         this.tick_has_passed = true;
      }
   }

   public void invalidateBlockReceiveRegion(int par1, int par2, int par3, int par4, int par5, int par6) {
   }

   protected IChunkProvider createChunkProvider() {
      this.clientChunkProvider = new ChunkProviderClient(this);
      return this.clientChunkProvider;
   }

   protected void tickBlocksAndAmbiance() {
      super.tickBlocksAndAmbiance();
      this.previousActiveChunkSet.retainAll(this.activeChunkSet);
      if (this.previousActiveChunkSet.size() == this.activeChunkSet.size()) {
         this.previousActiveChunkSet.clear();
      }

      int var1 = 0;
      Iterator var2 = this.activeChunkSet.iterator();

      ChunkCoordIntPair var3;
      while(var2.hasNext()) {
         var3 = (ChunkCoordIntPair)var2.next();
         Chunk chunk = this.getChunkFromChunkCoords(var3.chunkXPos, var3.chunkZPos);
         chunk.performPendingSkylightUpdatesIfPossible();
         chunk.performPendingBlocklightUpdatesIfPossible();
      }

      var2 = this.activeChunkSet.iterator();

      while(var2.hasNext()) {
         var3 = (ChunkCoordIntPair)var2.next();
         if (!this.previousActiveChunkSet.contains(var3)) {
            int var4 = var3.chunkXPos * 16;
            int var5 = var3.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk var6 = this.getChunkFromChunkCoords(var3.chunkXPos, var3.chunkZPos);
            this.moodSoundAndLightCheck(var4, var5, var6);
            this.theProfiler.endSection();
            this.previousActiveChunkSet.add(var3);
            ++var1;
            if (var1 >= 10) {
               return;
            }
         }
      }

   }

   public void doPreChunk(int par1, int par2, boolean par3) {
      if (par3) {
         this.clientChunkProvider.loadChunk(par1, par2);
      } else {
         this.clientChunkProvider.unloadChunk(par1, par2);
      }

      if (!par3) {
         this.markBlockRangeForRenderUpdate(par1 * 16, 0, par2 * 16, par1 * 16 + 15, 256, par2 * 16 + 15);
      }

   }

   public boolean spawnEntityInWorld(Entity par1Entity) {
      boolean var2 = super.spawnEntityInWorld(par1Entity);
      this.entityList.add(par1Entity);
      if (!var2) {
         this.entitySpawnQueue.add(par1Entity);
      }

      return var2;
   }

   public void removeEntity(Entity par1Entity) {
      super.removeEntity(par1Entity);
      this.entityList.remove(par1Entity);
   }

   protected void onEntityAdded(Entity par1Entity) {
      super.onEntityAdded(par1Entity);
      if (this.entitySpawnQueue.contains(par1Entity)) {
         this.entitySpawnQueue.remove(par1Entity);
      }

   }

   protected void onEntityRemoved(Entity par1Entity) {
      super.onEntityRemoved(par1Entity);
      if (this.entityList.contains(par1Entity)) {
         if (par1Entity.isEntityAlive()) {
            this.entitySpawnQueue.add(par1Entity);
         } else {
            this.entityList.remove(par1Entity);
         }
      }

   }

   public void addEntityToWorld(int par1, Entity par2Entity) {
      Entity var3 = this.getEntityByID(par1);
      if (var3 != null) {
         this.removeEntity(var3);
      }

      this.entityList.add(par2Entity);
      par2Entity.entityId = par1;
      if (!this.spawnEntityInWorld(par2Entity)) {
         this.entitySpawnQueue.add(par2Entity);
      }

      this.entityHashSet.addKey(par1, par2Entity);
   }

   public Entity getEntityByID(int par1) {
      return (Entity)(par1 == this.mc.thePlayer.entityId ? this.mc.thePlayer : (Entity)this.entityHashSet.lookup(par1));
   }

   public Entity removeEntityFromWorld(int par1) {
      Entity var2 = (Entity)this.entityHashSet.removeObject(par1);
      if (var2 != null) {
         this.entityList.remove(var2);
         this.removeEntity(var2);
      }

      return var2;
   }

   public boolean setBlockAndMetadataAndInvalidate(int par1, int par2, int par3, int par4, int par5) {
      this.invalidateBlockReceiveRegion(par1, par2, par3, par1, par2, par3);
      return super.setBlock(par1, par2, par3, par4, par5, 3);
   }

   public void sendQuittingDisconnectingPacket() {
      this.sendQueue.quitWithPacket(new Packet255KickDisconnect("Quitting", this.mc.isIntegratedServerRunning()));
   }

   public IUpdatePlayerListBox getMinecartSoundUpdater(EntityMinecart par1EntityMinecart) {
      return new SoundUpdaterMinecart(this.mc.sndManager, par1EntityMinecart, this.mc.thePlayer);
   }

   public void setRainStrengthForRenderViewEntity() {
      this.mc.prev_raining_strength_for_render_view_entity = this.mc.raining_strength_for_render_view_entity;
      if ((double)this.rainingStrength == 0.0) {
         this.mc.raining_strength_for_render_view_entity = 0.0F;
      } else {
         EntityLivingBase viewer = this.mc.renderViewEntity;
         int x = viewer.getBlockPosX();
         int z = viewer.getBlockPosZ();
         BiomeGenBase biome = this.getBiomeGenForCoords(x, z);
         if (!(biome.rainfall > 0.0F) && !this.isBloodMoon24HourPeriod()) {
            double distance_to_nearest_raining_coord_sq = Double.MAX_VALUE;
            int falloff_distance = 32;

            for(int dx = -falloff_distance; dx <= falloff_distance; ++dx) {
               for(int dz = -falloff_distance; dz <= falloff_distance; ++dz) {
                  if (this.chunkExistsAndIsNotEmptyFromBlockCoords(x + dx, z + dz)) {
                     biome = this.getBiomeGenForCoords(x + dx, z + dz);
                     if (biome.rainfall > 0.0F) {
                        double dxd = (double)((float)(x + dx) + 0.5F) - viewer.posX;
                        double dzd = (double)((float)(z + dz) + 0.5F) - viewer.posZ;
                        double distance_sq = dxd * dxd + dzd * dzd;
                        if (distance_sq < distance_to_nearest_raining_coord_sq) {
                           distance_to_nearest_raining_coord_sq = distance_sq;
                        }
                     }
                  }
               }
            }

            distance_to_nearest_raining_coord_sq -= 0.5;
            if (distance_to_nearest_raining_coord_sq <= 0.0) {
               this.mc.raining_strength_for_render_view_entity = this.rainingStrength;
            } else {
               if (distance_to_nearest_raining_coord_sq >= (double)(falloff_distance * falloff_distance)) {
                  this.mc.raining_strength_for_render_view_entity = 0.0F;
               } else {
                  this.mc.raining_strength_for_render_view_entity = Math.max(this.rainingStrength * (1.0F - MathHelper.sqrt_double(distance_to_nearest_raining_coord_sq) / (float)falloff_distance), 0.0F);
               }

            }
         } else {
            this.mc.raining_strength_for_render_view_entity = this.rainingStrength;
         }
      }
   }

   public float getRainStrength(float par1) {
      return this.mc == null ? super.getRainStrength(par1) : this.mc.prev_raining_strength_for_render_view_entity + (this.mc.raining_strength_for_render_view_entity - this.mc.prev_raining_strength_for_render_view_entity) * par1;
   }

   protected void updateWeather() {
      if (!this.provider.hasNoSky) {
         this.prevRainingStrength = this.rainingStrength;
         if (this.isPrecipitating(false)) {
            this.rainingStrength = (float)((double)this.rainingStrength + 0.01);
         } else {
            this.rainingStrength = (float)((double)this.rainingStrength - 0.01);
         }

         if (this.rainingStrength < 0.0F) {
            this.rainingStrength = 0.0F;
         }

         if (this.rainingStrength > 1.0F) {
            this.rainingStrength = 1.0F;
         }

         if (this == this.mc.renderViewEntity.worldObj) {
            this.setRainStrengthForRenderViewEntity();
         }

         this.prevThunderingStrength = this.thunderingStrength;
         if (this.isThundering(false)) {
            this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01);
         } else {
            this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01);
         }

         if (this.thunderingStrength < 0.0F) {
            this.thunderingStrength = 0.0F;
         }

         if (this.thunderingStrength > 1.0F) {
            this.thunderingStrength = 1.0F;
         }
      }

   }

   public void doVoidFogParticles(int par1, int par2, int par3) {
      byte var4 = 16;
      Random var5 = new Random();
      int random_number_index = this.rand.nextInt();
      boolean world_has_void_particles = this.provider.getWorldHasVoidParticles();

      for(int var6 = 0; var6 < 1000; ++var6) {
         int var7 = par1 + this.rand.nextInt(var4) - this.rand.nextInt(var4);
         int var8 = par2 + this.rand.nextInt(var4) - this.rand.nextInt(var4);
         int var9 = par3 + this.rand.nextInt(var4) - this.rand.nextInt(var4);
         if (this.isWithinBlockDomain(var7, var9)) {
            int var10 = this.getBlockId(var7, var8, var9);
            if (var10 > 0) {
               Block.blocksList[var10].randomDisplayTick(this, var7, var8, var9, var5);
            }
         }
      }

   }

   public void removeAllEntities() {
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      int var1;
      Entity var2;
      Chunk chunk;
      for(var1 = 0; var1 < this.unloadedEntityList.size(); ++var1) {
         var2 = (Entity)this.unloadedEntityList.get(var1);
         if (var2.isAddedToAChunk()) {
            chunk = var2.getChunkAddedTo();
            if (this.chunkExists(chunk.xPosition, chunk.zPosition)) {
               var2.removeFromChunk();
            }
         }
      }

      for(var1 = 0; var1 < this.unloadedEntityList.size(); ++var1) {
         this.onEntityRemoved((Entity)this.unloadedEntityList.get(var1));
      }

      this.unloadedEntityList.clear();

      for(var1 = 0; var1 < this.loadedEntityList.size(); ++var1) {
         var2 = (Entity)this.loadedEntityList.get(var1);
         if (var2.ridingEntity != null) {
            if (!var2.ridingEntity.isDead && var2.ridingEntity.riddenByEntity == var2) {
               continue;
            }

            var2.ridingEntity.riddenByEntity = null;
            var2.ridingEntity = null;
         }

         if (var2.isDead) {
            if (var2.isAddedToAChunk()) {
               chunk = var2.getChunkAddedTo();
               if (this.chunkExists(chunk.xPosition, chunk.zPosition)) {
                  var2.removeFromChunk();
               }
            }

            this.loadedEntityList.remove(var1--);
            this.onEntityRemoved(var2);
         }
      }

   }

   public CrashReportCategory addWorldInfoToCrashReport(CrashReport par1CrashReport) {
      CrashReportCategory var2 = super.addWorldInfoToCrashReport(par1CrashReport);
      var2.addCrashSectionCallable("Forced entities", new CallableMPL1(this));
      var2.addCrashSectionCallable("Retry entities", new CallableMPL2(this));
      var2.addCrashSectionCallable("Server brand", new WorldClientINNER3(this));
      var2.addCrashSectionCallable("Server type", new WorldClientINNER4(this));
      return var2;
   }

   public void playSound(double par1, double par3, double par5, String par7Str, float par8, float par9, boolean par10) {
      if (this.mc.raining_strength_for_render_view_entity < 1.0F && "ambient.weather.thunder".equals(par7Str)) {
         par1 = this.mc.thePlayer.posX;
         par3 = this.mc.thePlayer.posY;
         par5 = this.mc.thePlayer.posZ;
         par8 = (float)Math.pow((double)this.mc.raining_strength_for_render_view_entity, 4.0);
      }

      float var11 = 16.0F;
      if (par8 > 1.0F) {
         var11 *= par8;
      }

      double var12 = this.mc.renderViewEntity.getDistanceSq(par1, par3, par5);
      if (var12 < (double)(var11 * var11)) {
         if (par10 && var12 > 100.0) {
            double var14 = Math.sqrt(var12) / 40.0;
            this.mc.sndManager.func_92070_a(par7Str, (float)par1, (float)par3, (float)par5, par8, par9, (int)Math.round(var14 * 20.0));
         } else {
            this.mc.sndManager.playSound(par7Str, (float)par1, (float)par3, (float)par5, par8, par9);
         }
      }

   }

   public void playLongDistanceSound(double par1, double par3, double par5, String par7Str, float volume, float pitch, boolean par10) {
      double var12 = this.mc.renderViewEntity.getDistanceSq(par1, par3, par5);
      if (par10 && var12 > 100.0) {
         double var14 = Math.sqrt(var12) / 40.0;
         this.mc.sndManager.func_92070_a(par7Str, (float)par1, (float)par3, (float)par5, volume, pitch, (int)Math.round(var14 * 20.0));
      } else {
         this.mc.sndManager.playLongDistanceSound(par7Str, (float)par1, (float)par3, (float)par5, volume, pitch);
      }

   }

   public void func_92088_a(double par1, double par3, double par5, double par7, double par9, double par11, NBTTagCompound par13NBTTagCompound) {
      this.mc.effectRenderer.addEffect(new EntityFireworkStarterFX(this, par1, par3, par5, par7, par9, par11, this.mc.effectRenderer, par13NBTTagCompound));
   }

   public void func_96443_a(Scoreboard par1Scoreboard) {
      this.worldScoreboard = par1Scoreboard;
   }

   static Set getEntityList(WorldClient par0WorldClient) {
      return par0WorldClient.entityList;
   }

   static Set getEntitySpawnQueue(WorldClient par0WorldClient) {
      return par0WorldClient.entitySpawnQueue;
   }

   static Minecraft func_142030_c(WorldClient par0WorldClient) {
      return par0WorldClient.mc;
   }
}
