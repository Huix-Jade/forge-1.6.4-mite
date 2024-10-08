package net.minecraft.world.gen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

public final class ChunkProviderServer implements IChunkProvider
{
	/**
	 * used by unload100OldestChunks to iterate the loadedChunkHashMap for unload (underlying assumption, first in,
	 * first out)
	 */
	private Set chunksToUnload = new HashSet();
	public Chunk defaultEmptyChunk;
	private IChunkProvider currentChunkProvider;
	public IChunkLoader currentChunkLoader;

	/**
	 * if this is false, the defaultEmptyChunk will be returned by the provider
	 */
	public boolean loadChunkOnProvideRequest = true;
	public LongHashMap loadedChunkHashMap = new LongHashMap();
	private List loadedChunks = new ArrayList();
	public WorldServer worldObj;

	public ChunkProviderServer(WorldServer par1WorldServer, IChunkLoader par2IChunkLoader, IChunkProvider par3IChunkProvider)
	{
		this.defaultEmptyChunk = new EmptyChunk(par1WorldServer, 0, 0);
		this.worldObj = par1WorldServer;
		this.currentChunkLoader = par2IChunkLoader;
		this.currentChunkProvider = par3IChunkProvider;
	}

	/**
	 * Checks to see if a chunk exists at x, y
	 */
	public boolean chunkExists(int par1, int par2)
	{
		return this.loadedChunkHashMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(par1, par2));
	}

	public Chunk getChunkIfItExists(int chunk_x, int chunk_z)
	{
		return (Chunk)this.loadedChunkHashMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(chunk_x, chunk_z));
	}

	/**
	 * marks chunk for unload by "unload100OldestChunks"  if there is no spawn point, or if the center of the chunk is
	 * outside 200 blocks (x or z) of the spawn
	 */
	public void unloadChunksIfNotNearSpawn(int par1, int par2)
	{
		if (this.worldObj.provider.canRespawnHere() && DimensionManager.shouldLoadSpawn(this.worldObj.provider.dimensionId))
		{
			ChunkCoordinates var3 = this.worldObj.getSpawnPoint();
			int var4 = par1 * 16 + 8 - var3.posX;
			int var5 = par2 * 16 + 8 - var3.posZ;
			short var6 = 128;

			if (var4 < -var6 || var4 > var6 || var5 < -var6 || var5 > var6)
			{
				this.chunksToUnload.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(par1, par2)));
			}
		}
		else
		{
			this.chunksToUnload.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(par1, par2)));
		}
	}

	/**
	 * marks all chunks for unload, ignoring those near the spawn
	 */
	public void unloadAllChunks()
	{
		Iterator var1 = this.loadedChunks.iterator();

		while (var1.hasNext())
		{
			Chunk var2 = (Chunk)var1.next();
			this.unloadChunksIfNotNearSpawn(var2.xPosition, var2.zPosition);
		}
	}

	public void finalCleanup()
	{
		this.loadedChunks.clear();
		this.chunksToUnload.clear();
		this.loadedChunkHashMap.hashArray = null;
	}

	/**
	 * loads or generates the chunk at the chunk location specified
	 */
	public Chunk loadChunk(int par1, int par2)
	{
		long var3 = ChunkCoordIntPair.chunkXZ2Int(par1, par2);
		this.chunksToUnload.remove(Long.valueOf(var3));
		Chunk var5 = (Chunk)this.loadedChunkHashMap.getValueByKey(var3);

		if (var5 == null)
		{

			var5 = ForgeChunkManager.fetchDormantChunk(var3, this.worldObj);
			if (var5 == null)
			{
				var5 = this.safeLoadChunk(par1, par2);
			}

			if (var5 == null)
			{
				if (this.currentChunkProvider == null)
				{
					var5 = this.defaultEmptyChunk;
				}
				else
				{
					try
					{
						var5 = this.currentChunkProvider.provideChunk(par1, par2);
					}
					catch (Throwable var9)
					{
						CrashReport var7 = CrashReport.makeCrashReport(var9, "Exception generating new chunk");
						CrashReportCategory var8 = var7.makeCategory("Chunk to be generated");
						var8.addCrashSection("Location", String.format("%d,%d", new Object[] {Integer.valueOf(par1), Integer.valueOf(par2)}));
						var8.addCrashSection("Position hash", Long.valueOf(var3));
						var8.addCrashSection("Generator", this.currentChunkProvider.makeString());
						throw new ReportedException(var7);
					}
				}
			}

			this.loadedChunkHashMap.add(var3, var5);
			this.loadedChunks.add(var5);

			if (var5 != null)
			{
				var5.onChunkLoad();
			}

			var5.populateChunk(this, this, par1, par2);
		}

		return var5;
	}

	/**
	 * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
	 * specified chunk from the map seed and chunk seed
	 */
	public Chunk provideChunk(int par1, int par2)
	{
		Chunk var3 = (Chunk)this.loadedChunkHashMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(par1, par2));
		return var3 == null ? (!this.worldObj.findingSpawnPoint && !this.loadChunkOnProvideRequest ? this.defaultEmptyChunk : this.loadChunk(par1, par2)) : var3;
	}

	/**
	 * used by loadChunk, but catches any exceptions if the load fails.
	 */
	private Chunk safeLoadChunk(int par1, int par2)
	{
		if (this.currentChunkLoader == null)
		{
			return null;
		}
		else
		{
			try
			{
				Chunk var3 = this.currentChunkLoader.loadChunk(this.worldObj, par1, par2);

				if (var3 != null)
				{
					var3.lastSaveTime = this.worldObj.getTotalWorldTime();

					if (this.currentChunkProvider != null)
					{
						this.currentChunkProvider.recreateStructures(par1, par2);
					}
				}

				return var3;
			}
			catch (Exception var4)
			{
				var4.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * used by saveChunks, but catches any exceptions if the save fails.
	 */
	private void safeSaveExtraChunkData(Chunk par1Chunk)
	{
		if (this.currentChunkLoader != null)
		{
			try
			{
				this.currentChunkLoader.saveExtraChunkData(this.worldObj, par1Chunk);
			}
			catch (Exception var3)
			{
				var3.printStackTrace();
			}
		}
	}

	/**
	 * used by saveChunks, but catches any exceptions if the save fails.
	 */
	private void safeSaveChunk(Chunk par1Chunk)
	{
		if (this.currentChunkLoader != null)
		{
			try
			{
				par1Chunk.lastSaveTime = this.worldObj.getTotalWorldTime();
				this.currentChunkLoader.saveChunk(this.worldObj, par1Chunk);
			}
			catch (IOException var3)
			{
				var3.printStackTrace();
			}
			catch (MinecraftException var4)
			{
				var4.printStackTrace();
			}
		}
	}

	/**
	 * Populates chunk with ores etc etc
	 */
	public void populate(IChunkProvider par1IChunkProvider, int par2, int par3)
	{
		Chunk var4 = this.provideChunk(par2, par3);
		if (!var4.isTerrainPopulated) {
			var4.isTerrainPopulated = true;
			if (this.currentChunkProvider != null) {
				this.currentChunkProvider.populate(par1IChunkProvider, par2, par3);
				GameRegistry.generateWorld(par2, par3, worldObj, currentChunkProvider, par1IChunkProvider);
				var4.setChunkModified();
			}
		}
	}

	/**
	 * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
	 * Return true if all chunks have been saved.
	 */
	public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate)
	{
		int var3 = 0;

		for (int var4 = 0; var4 < this.loadedChunks.size(); ++var4)
		{
			Chunk var5 = (Chunk)this.loadedChunks.get(var4);

			if (par1)
			{
				this.safeSaveExtraChunkData(var5);
			}

			if (var5.needsSaving(par1))
			{
				this.safeSaveChunk(var5);
				var5.isModified = false;
				var5.should_be_saved_once_time_forwarding_is_completed = false;
				++var3;

				if (var3 == 24 && !par1)
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
	 * unimplemented.
	 */
	public void saveExtraData()
	{
		if (this.currentChunkLoader != null)
		{
			this.currentChunkLoader.saveExtraData();
		}
	}

	/**
	 * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
	 */
	public boolean unloadQueuedChunks()
	{
		if (!this.worldObj.canNotSave)
		{
			for (ChunkCoordIntPair forced : this.worldObj.getPersistentChunks().keySet())
			{
				this.chunksToUnload.remove(ChunkCoordIntPair.chunkXZ2Int(forced.chunkXPos, forced.chunkZPos));
			}

			for (int var1 = 0; var1 < 100; ++var1)
			{
				if (!this.chunksToUnload.isEmpty())
				{
					Long var2 = (Long)this.chunksToUnload.iterator().next();
					Chunk var3 = (Chunk)this.loadedChunkHashMap.getValueByKey(var2.longValue());
					var3.onChunkUnload();

					if (!MinecraftServer.treachery_detected)
					{
						this.safeSaveChunk(var3);
						this.safeSaveExtraChunkData(var3);
					}

					this.chunksToUnload.remove(var2);
					this.loadedChunkHashMap.remove(var2.longValue());
					this.loadedChunks.remove(var3);
					ForgeChunkManager.putDormantChunk(ChunkCoordIntPair.chunkXZ2Int(var3.xPosition, var3.zPosition), var3);
					if(loadedChunks.size() == 0 && ForgeChunkManager.getPersistentChunksFor(this.worldObj).size() == 0 && !DimensionManager.shouldLoadSpawn(this.worldObj.provider.dimensionId)) {
						DimensionManager.unloadWorld(this.worldObj.provider.dimensionId);
						return currentChunkProvider.unloadQueuedChunks();
					}
				}
			}

			if (this.currentChunkLoader != null)
			{
				this.currentChunkLoader.chunkTick();
			}
		}

		return this.currentChunkProvider.unloadQueuedChunks();
	}

	/**
	 * Returns if the IChunkProvider supports saving.
	 */
	public boolean canSave()
	{
		return !this.worldObj.canNotSave;
	}

	/**
	 * Converts the instance data to a readable string.
	 */
	public String makeString()
	{
		return "ServerChunkCache: " + this.loadedChunkHashMap.getNumHashElements() + " Drop: " + this.chunksToUnload.size();
	}

	/**
	 * Returns a list of creatures of the specified type that can spawn at the given location.
	 */
	public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
	{
		return this.currentChunkProvider.getPossibleCreatures(par1EnumCreatureType, par2, par3, par4);
	}

	/**
	 * Returns the location of the closest structure of the specified type. If not found returns null.
	 */
	public ChunkPosition findClosestStructure(World par1World, String par2Str, int par3, int par4, int par5)
	{
		return this.currentChunkProvider.findClosestStructure(par1World, par2Str, par3, par4, par5);
	}

	public int getLoadedChunkCount()
	{
		return this.loadedChunkHashMap.getNumHashElements();
	}

	public void recreateStructures(int par1, int par2) {}

	public IChunkProvider getChunkProvider()
	{
		return this.currentChunkProvider;
	}
}
