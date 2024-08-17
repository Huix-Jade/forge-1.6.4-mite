package net.minecraft.world.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RNG;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.mite.MITEConstant;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Debug;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class Chunk
{
	/**
	 * Determines if the chunk is lit or not at a light value greater than 0.
	 */
	public static boolean isLit;

	/**
	 * Used to store block IDs, block MSBs, Sky-light maps, Block-light maps, and metadata. Each entry corresponds to a
	 * logical segment of 16x16x16 blocks, stacked vertically.
	 */
	public ExtendedBlockStorage[] storageArrays;

	/**
	 * Contains a 16x16 mapping on the X/Z plane of the biome ID to which each colum belongs.
	 */
	private byte[] blockBiomeArray;

	/**
	 * A map, similar to heightMap, that tracks how far down precipitation can fall.
	 */
	public int[] precipitationHeightMap;

	/** Which columns need their skylightMaps updated. */
	public boolean[] updateSkylightColumns;

	/** Whether or not this Chunk is currently loaded into the World */
	public boolean isChunkLoaded;

	/** Reference to the World object. */
	public World worldObj;
	public int[] heightMap;

	/** The x coordinate of the chunk. */
	public final int xPosition;

	/** The z coordinate of the chunk. */
	public final int zPosition;
	public boolean isGapLightingUpdated;

	/** A Map of ChunkPositions to TileEntities in this chunk */
	public Map chunkTileEntityMap;
	public static final int num_entity_lists = 16;

	/**
	 * Array of Lists containing the entities in this Chunk. Each List represents a 16 block subchunk.
	 */
    public List[] entityLists;

	/** Boolean value indicating if the terrain is populated. */
	public boolean isTerrainPopulated;

	/**
	 * Set to true if the chunk has been modified and needs to be updated internally.
	 */
	public boolean isModified;

	/** The time according to World.worldTime when this chunk was last saved */
	public long lastSaveTime;

	/**
	 * Updates to this chunk will not be sent to clients if this is false. This field is set to true the first time the
	 * chunk is sent to a client, and never set to false.
	 */
	public boolean sendUpdates;
	public final int[] skylight_bottom;
	public static final int skylight_bottom_initial_value = World.getMaxBlockY() + 1;

	/** the cumulative number of ticks players have been in this chunk */
	public long inhabitedTime;

	/**
	 * Contains the current round-robin relight check index, and is implied as the relight check location as well.
	 */
	private int queuedLightChecks;
	public long last_total_world_time;
	public int animals_spawned;
	public boolean should_be_saved_once_time_forwarding_is_completed;
	private final boolean is_empty;
	public boolean invalidate_checksum;
	public boolean has_had_lighting_checked;
	private boolean has_initial_heightmap_been_generated;
	private boolean has_initial_skymap_been_generated;
	public byte[] pending_skylight_update_coords;
	public int max_num_pending_skylight_updates;
	public int num_pending_skylight_updates;
	public final boolean[] pending_skylight_updates;
	private static final boolean[] skylight_propagation_from_neighbor_to_nw = generateSkylightPropagationForNeighbor(-1, -1);
	private static final boolean[] skylight_propagation_from_neighbor_to_ne = generateSkylightPropagationForNeighbor(1, -1);
	private static final boolean[] skylight_propagation_from_neighbor_to_se = generateSkylightPropagationForNeighbor(1, 1);
	private static final boolean[] skylight_propagation_from_neighbor_to_sw = generateSkylightPropagationForNeighbor(-1, 1);
	public byte[] pending_blocklight_update_coords;
	public int max_num_pending_blocklight_updates;
	public int num_pending_blocklight_updates;
	public final boolean[] pending_blocklight_updates;
	public final boolean has_skylight;
	private final boolean is_within_block_domain;
	private boolean had_naturally_occurring_mycelium;
	public HashMap pending_sand_falls;
	private static final byte sand_block_id = (byte) Block.sand.blockID;
	private static final byte water_still_block_id = (byte)Block.waterStill.blockID;

	public Chunk(World par1World, int par2, int par3)
	{
		this.max_num_pending_skylight_updates = 256;
		this.max_num_pending_blocklight_updates = 256;
		this.is_empty = this instanceof EmptyChunk;
		this.is_within_block_domain = par1World.isChunkWithinBlockDomain(par2, par3);
		this.has_skylight = par1World.hasSkylight();
		this.storageArrays = this.is_empty ? null : new ExtendedBlockStorage[16];
		this.blockBiomeArray = new byte[256];
		this.precipitationHeightMap = new int[256];
		this.updateSkylightColumns = new boolean[256];
		this.chunkTileEntityMap = new HashMap();
		this.queuedLightChecks = 4096;
		this.entityLists = new List[16];
		this.worldObj = par1World;
		this.xPosition = par2;
		this.zPosition = par3;
		this.heightMap = new int[256];

		for (int var4 = 0; var4 < this.entityLists.length; ++var4)
		{
			this.entityLists[var4] = new ArrayList();
		}

		Arrays.fill(this.precipitationHeightMap, -999);
		Arrays.fill(this.blockBiomeArray, (byte) - 1);

		if (this.is_empty)
		{
			this.skylight_bottom = null;
			this.pending_skylight_update_coords = null;
			this.pending_skylight_updates = null;
			this.pending_blocklight_update_coords = null;
			this.pending_blocklight_updates = null;
		}
		else
		{
			this.skylight_bottom = new int[this.heightMap.length];
			Arrays.fill(this.skylight_bottom, skylight_bottom_initial_value);
			this.pending_skylight_update_coords = par1World.hasSkylight() ? new byte[this.max_num_pending_skylight_updates * 2] : null;
			this.pending_skylight_updates = par1World.hasSkylight() ? new boolean[65536] : null;
			this.pending_blocklight_update_coords = new byte[this.max_num_pending_blocklight_updates * 2];
			this.pending_blocklight_updates = new boolean[65536];
		}
	}

	private double getMax(double a, double b)
	{
		return a > b ? a : b;
	}

	public Chunk(World par1World, byte[] par2ArrayOfByte, int par3, int par4)
	{
		this(par1World, par3, par4);
		int var5 = par2ArrayOfByte.length / 256;
		int var6;
		int var7;
		int var9;
		int var10;
		int var12;
		int var13;

		if (par1World.provider.isHellWorld)
		{
			var6 = this.xPosition * 16;
			var7 = this.zPosition * 16;

			for (int var8 = 0; var8 < 16; ++var8)
			{
				for (var9 = 0; var9 < 16; ++var9)
				{
					for (var10 = 0; var10 < var5; ++var10)
					{
						/* FORGE: The following change, a cast from unsigned byte to int,
						 * fixes a vanilla bug when generating new chunks that contain a block ID > 127 */
						int var11 = par2ArrayOfByte[var8 << 11 | var9 << 7 | var10] & 0xFF;

						if (var11 != 0)
						{
							if (var11 == Block.lavaStill.blockID)
							{
								if (var10 == 31 && par2ArrayOfByte[var8 << 11 | var9 << 7 | var10 + 1] == 0)
								{
									this.addPendingBlocklightUpdate(var6 + var8, var10, var7 + var9);
								}

								if (var10 > 31)
								{
									var12 = var6 + var8;
									var13 = var7 + var9;
									par1World.scheduleBlockChange(var12, var10, var13, var11, var11 - 1, 0, 10);
								}
							}

							var12 = var10 >> 4;

							if (this.storageArrays[var12] == null)
							{
								this.storageArrays[var12] = new ExtendedBlockStorage(var12 << 4, !par1World.provider.hasNoSky);
							}

							this.storageArrays[var12].setExtBlockID(var8, var10 & 15, var9, var11);

							if (var11 == Block.gravel.blockID)
							{
								this.storageArrays[var12].setExtBlockMetadata(var8, var10 & 15, var9, 2);
							}
							else if (var11 == Block.mantleOrCore.blockID)
							{
								this.addPendingBlocklightUpdate(var6 + var8, var10, var7 + var9);

								if (var10 < 1)
								{
									this.storageArrays[var12].setExtBlockMetadata(var8, var10 & 15, var9, 1);
								}
							}
						}
					}
				}
			}
		}
		else
		{
			int var14;
			int var15;
			int var17;

			if (par1World.isUnderworld())
			{
				var6 = this.xPosition * 16;
				var7 = this.zPosition * 16;
				Random var30 = new Random(par1World.getHashedSeed() * (long)getChunkCoordsHash(this.xPosition, this.zPosition));
				var9 = par1World.underworld_y_offset;
				double var33 = 0.015625D;
				double var35 = 0.03125D;
				ChunkProviderUnderworld.bedrock_strata_1a_noise = ChunkProviderUnderworld.noise_gen_bedrock_strata_1a.generateNoiseOctaves(ChunkProviderUnderworld.bedrock_strata_1a_noise, var6, 0, var7, 16, 1, 16, var33 * 2.0D, var35 * 2.0D, var33 * 2.0D);
				ChunkProviderUnderworld.bedrock_strata_1b_noise = ChunkProviderUnderworld.noise_gen_bedrock_strata_1b.generateNoiseOctaves(ChunkProviderUnderworld.bedrock_strata_1b_noise, var6, 0, var7, 16, 1, 16, var33 * 2.0D, var35 * 2.0D, var33 * 2.0D);
				ChunkProviderUnderworld.bedrock_strata_2_noise = ChunkProviderUnderworld.noise_gen_bedrock_strata_2.generateNoiseOctaves(ChunkProviderUnderworld.bedrock_strata_2_noise, var6, 0, var7, 16, 1, 16, var33 * 4.0D, var35 * 2.0D, var33 * 4.0D);
				ChunkProviderUnderworld.bedrock_strata_3_noise = ChunkProviderUnderworld.noise_gen_bedrock_strata_3.generateNoiseOctaves(ChunkProviderUnderworld.bedrock_strata_3_noise, var6, 0, var7, 16, 1, 16, var33 * 4.0D, var35 * 2.0D, var33 * 4.0D);
				ChunkProviderUnderworld.bedrock_strata_4_noise = ChunkProviderUnderworld.noise_gen_bedrock_strata_4.generateNoiseOctaves(ChunkProviderUnderworld.bedrock_strata_4_noise, var6, 0, var7, 16, 1, 16, var33 * 4.0D, var35 * 2.0D, var33 * 4.0D);
				var33 = 0.25D;
				ChunkProviderUnderworld.bedrock_strata_1a_bump_noise = ChunkProviderUnderworld.noise_gen_bedrock_strata_1a_bump.generateNoiseOctaves(ChunkProviderUnderworld.bedrock_strata_1a_bump_noise, var6, 0, var7, 16, 1, 16, var33 * 0.5D, var35 * 2.0D, var33 * 0.5D);
				ChunkProviderUnderworld.bedrock_strata_1b_bump_noise = ChunkProviderUnderworld.noise_gen_bedrock_strata_1b_bump.generateNoiseOctaves(ChunkProviderUnderworld.bedrock_strata_1b_bump_noise, var6, 0, var7, 16, 1, 16, var33 * 1.0D, var35 * 2.0D, var33 * 1.0D);
				ChunkProviderUnderworld.bedrock_strata_1c_bump_noise = ChunkProviderUnderworld.noise_gen_bedrock_strata_1c_bump.generateNoiseOctaves(ChunkProviderUnderworld.bedrock_strata_1c_bump_noise, var6, 0, var7, 16, 1, 16, var33 * 2.0D, var35 * 2.0D, var33 * 2.0D);
				ChunkProviderUnderworld.bedrock_strata_2_bump_noise = ChunkProviderUnderworld.noise_gen_bedrock_strata_2_bump.generateNoiseOctaves(ChunkProviderUnderworld.bedrock_strata_2_bump_noise, var6, 0, var7, 16, 1, 16, var33 * 4.0D, var35 * 2.0D, var33 * 4.0D);
				ChunkProviderUnderworld.bedrock_strata_3_bump_noise = ChunkProviderUnderworld.noise_gen_bedrock_strata_3_bump.generateNoiseOctaves(ChunkProviderUnderworld.bedrock_strata_3_bump_noise, var6, 0, var7, 16, 1, 16, var33 * 4.0D, var35 * 2.0D, var33 * 4.0D);
				ChunkProviderUnderworld.bedrock_strata_4_bump_noise = ChunkProviderUnderworld.noise_gen_bedrock_strata_4_bump.generateNoiseOctaves(ChunkProviderUnderworld.bedrock_strata_4_bump_noise, var6, 0, var7, 16, 1, 16, var33 * 4.0D, var35 * 2.0D, var33 * 4.0D);
				var14 = this.xPosition * 2653 + this.zPosition * 6714631;

				for (var15 = 0; var15 < 16; ++var15)
				{
					for (int var16 = 0; var16 < 16; ++var16)
					{
						var17 = var9 == 0 ? 0 : var30.nextInt(3) + 1;

						for (int var18 = -var9; var18 < (var9 == 0 ? var5 : 256 - var9); ++var18)
						{
							int var19;
							byte var20;

							if (var18 >= 0 && var18 <= 127)
							{
								var19 = var15 << 11 | var16 << 7 | var18;
								var20 = var19 >= 0 && var19 < par2ArrayOfByte.length ? par2ArrayOfByte[var19] : (byte)Block.stone.blockID;
								var18 += var9;
							}
							else
							{
								var19 = -1;
								var18 += var9;

								if (var18 < var17)
								{
									var20 = (byte)Block.mantleOrCore.blockID;
								}
								else if (var18 > 255 - var17)
								{
									var20 = (byte)Block.bedrock.blockID;
								}
								else
								{
									var20 = (byte)Block.stone.blockID;
									byte var21 = (byte)Block.bedrock.blockID;
									int var22 = var16 + var15 * 16;
									double var23 = this.getMax(ChunkProviderUnderworld.bedrock_strata_1a_noise[var22], ChunkProviderUnderworld.bedrock_strata_1b_noise[var22]);
									int var25 = var18 - 3;
									double var26 = ChunkProviderUnderworld.bedrock_strata_1a_bump_noise[var22];

									if (var26 > 0.0D)
									{
										var23 += var26 * 0.25D;
									}

									var26 = ChunkProviderUnderworld.bedrock_strata_1b_bump_noise[var22];

									if (var26 > 0.0D)
									{
										var23 += var26 * 0.125D;
									}

									var26 = ChunkProviderUnderworld.bedrock_strata_1c_bump_noise[var22];

									if (var26 > 0.0D)
									{
										var23 += var26 * 0.125D;
									}

									var26 = ChunkProviderUnderworld.bedrock_strata_4_bump_noise[var22];

									if (var26 > 0.0D)
									{
										var23 += var26 * 0.09375D + 0.125D;
									}

									if (var23 > 0.0D && (double)var25 <= var23 * 7.0D)
									{
										var20 = (byte)Block.bedrock.blockID;
									}

									if (var20 != var21)
									{
										var25 = var18 - 32;
										var23 = ChunkProviderUnderworld.bedrock_strata_2_noise[var22] - var23 * 1.5D;

										if (var23 > 0.0D)
										{
											if (var25 > 0)
											{
												var26 = ChunkProviderUnderworld.bedrock_strata_2_bump_noise[var22];

												if (var26 > 0.0D)
												{
													var23 += var26 * 0.25D + 0.25D;
												}
											}

											if (var25 < 0)
											{
												++var14;

												if (RNG.chance_in_2[var14 & 32767])
												{
													++var25;
												}

												var25 = -var25;
											}

											if ((double)var25 <= var23 * 2.0D)
											{
												var20 = (byte)Block.bedrock.blockID;
											}
										}
									}

									if (var20 != var21)
									{
										var25 = var18 - 72;
										var23 = ChunkProviderUnderworld.bedrock_strata_3_noise[var22] - ChunkProviderUnderworld.bedrock_strata_4_noise[var22] * 0.375D;
										var23 += 0.5D;

										if (var23 > 0.0D)
										{
											if (var25 > 0)
											{
												var26 = ChunkProviderUnderworld.bedrock_strata_3_bump_noise[var22];

												if (var26 > 0.0D)
												{
													var23 += var26 * 0.25D + 0.25D;
												}
											}

											if (var25 < 0)
											{
												++var14;

												if (RNG.chance_in_2[var14 & 32767])
												{
													++var25;
												}

												var25 = -var25;
											}

											if ((double)var25 <= var23 * 2.0D)
											{
												var20 = (byte)Block.bedrock.blockID;
											}
										}
									}

									if (var20 != var21)
									{
										var25 = var18 - 96;
										var23 = ChunkProviderUnderworld.bedrock_strata_4_noise[var22] - ChunkProviderUnderworld.bedrock_strata_3_noise[var22] * 0.375D;
										var23 += 0.5D;

										if (var23 > 0.0D)
										{
											if (var25 > 0)
											{
												var26 = ChunkProviderUnderworld.bedrock_strata_4_bump_noise[var22];

												if (var26 > 0.0D)
												{
													var23 += var26 * 0.25D + 0.25D;
												}
											}

											if (var25 < 0)
											{
												++var14;

												if (RNG.chance_in_2[var14 & 32767])
												{
													++var25;
												}

												var25 = -var25;
											}

											if ((double)var25 <= var23 * 2.0D)
											{
												var20 = (byte)Block.bedrock.blockID;
											}
										}
									}
								}
							}

							if (var20 != 0)
							{
								int var37 = var18 >> 4;

								if (this.storageArrays[var37] == null)
								{
									this.storageArrays[var37] = new ExtendedBlockStorage(var37 << 4, !par1World.provider.hasNoSky);
								}

								this.storageArrays[var37].setExtBlockID(var15, var18 & 15, var16, var20);

								if (Block.lightValue[var20] > 0)
								{
									this.storageArrays[var37].setExtBlocklightValue(var15, var18 & 15, var16, Block.lightValue[var20]);

									if (par2ArrayOfByte[var19 + 1] == 0)
									{
										this.addPendingBlocklightUpdate(var6 + var15, var18, var7 + var16);
									}
								}
							}

							var18 -= var9;
						}
					}
				}
			}
			else
			{
				Chunk var28 = this.getNeighboringChunkIfItExists(-1, 0);
				Chunk var29 = this.getNeighboringChunkIfItExists(1, 0);
				Chunk var31 = this.getNeighboringChunkIfItExists(0, -1);
				Chunk var32 = this.getNeighboringChunkIfItExists(0, 1);
				var10 = this.xPosition * 16;
				int var34 = this.zPosition * 16;

				for (var12 = 0; var12 < 16; ++var12)
				{
					for (var13 = 0; var13 < 16; ++var13)
					{
						for (var14 = 0; var14 < var5; ++var14)
						{
							var15 = var12 << 11 | var13 << 7 | var14;
							byte var36 = par2ArrayOfByte[var15];

							if (var36 != 0)
							{
								var17 = var14 >> 4;

								if (this.storageArrays[var17] == null)
								{
									this.storageArrays[var17] = new ExtendedBlockStorage(var17 << 4, !par1World.provider.hasNoSky);
								}

								this.storageArrays[var17].setExtBlockID(var12, var14 & 15, var13, var36);

								if (Block.lightValue[var36] > 0)
								{
									this.storageArrays[var17].setExtBlocklightValue(var12, var14 & 15, var13, Block.lightValue[var36]);

									if (par2ArrayOfByte[var12 << 11 | var13 << 7 | var14 + 1] == 0)
									{
										this.addPendingBlocklightUpdate(var10 + var12, var14, var34 + var13);
									}
								}
								else if (var36 == Block.waterStill.blockID)
								{
									if (var14 > 0 && par2ArrayOfByte[var15 - 1] == 0)
									{
										par1World.scheduleBlockChange(var10 + var12, var14, var34 + var13, var36, var36 - 1, 0, 10);
									}
									else if (var14 == 62)
									{
										if (var12 == 0)
										{
											if (var28 != null && var28.getBlockIDOptimized(15 | var13 << 4, var14) == 0)
											{
												par1World.scheduleBlockChange(var10 + var12, var14, var34 + var13, var36, var36 - 1, 0, 10);
												continue;
											}
										}
										else if (par2ArrayOfByte[var15 - 2048] == 0)
										{
											par1World.scheduleBlockChange(var10 + var12, var14, var34 + var13, var36, var36 - 1, 0, 10);
											continue;
										}

										if (var12 == 15)
										{
											if (var29 != null && var29.getBlockIDOptimized(0 | var13 << 4, var14) == 0)
											{
												par1World.scheduleBlockChange(var10 + var12, var14, var34 + var13, var36, var36 - 1, 0, 10);
												continue;
											}
										}
										else if (par2ArrayOfByte[var15 + 2048] == 0)
										{
											par1World.scheduleBlockChange(var10 + var12, var14, var34 + var13, var36, var36 - 1, 0, 10);
											continue;
										}

										if (var13 == 0)
										{
											if (var31 != null && var31.getBlockIDOptimized(var12 | 240, var14) == 0)
											{
												par1World.scheduleBlockChange(var10 + var12, var14, var34 + var13, var36, var36 - 1, 0, 10);
												continue;
											}
										}
										else if (par2ArrayOfByte[var15 - 128] == 0)
										{
											par1World.scheduleBlockChange(var10 + var12, var14, var34 + var13, var36, var36 - 1, 0, 10);
											continue;
										}

										if (var13 == 15)
										{
											if (var32 != null && var32.getBlockIDOptimized(var12 | 0, var14) == 0)
											{
												par1World.scheduleBlockChange(var10 + var12, var14, var34 + var13, var36, var36 - 1, 0, 10);
											}
										}
										else if (par2ArrayOfByte[var15 + 128] == 0)
										{
											par1World.scheduleBlockChange(var10 + var12, var14, var34 + var13, var36, var36 - 1, 0, 10);
										}
									}
								}
							}
							else if (var14 == 62)
							{
								if (var12 == 0 && var28 != null && var28.getBlockIDOptimized(15 | var13 << 4, var14) == water_still_block_id)
								{
									par1World.scheduleBlockChange(var10 + var12 - 1, var14, var34 + var13, water_still_block_id, water_still_block_id - 1, 0, 10);
								}
								else if (var12 == 15 && var29 != null && var29.getBlockIDOptimized(0 | var13 << 4, var14) == water_still_block_id)
								{
									par1World.scheduleBlockChange(var10 + var12 + 1, var14, var34 + var13, water_still_block_id, water_still_block_id - 1, 0, 10);
								}
								else if (var13 == 0 && var31 != null && var31.getBlockIDOptimized(var12 | 240, var14) == water_still_block_id)
								{
									par1World.scheduleBlockChange(var10 + var12, var14, var34 + var13 - 1, water_still_block_id, water_still_block_id - 1, 0, 10);
								}
								else if (var13 == 15 && var32 != null && var32.getBlockIDOptimized(var12 | 0, var14) == water_still_block_id)
								{
									par1World.scheduleBlockChange(var10 + var12, var14, var34 + var13 + 1, water_still_block_id, water_still_block_id - 1, 0, 10);
								}
							}
						}
					}
				}
			}
		}
	}
	/**
	 * Metadata sensitive Chunk constructor for use in new ChunkProviders that
	 * use metadata sensitive blocks during generation.
	 *
	 * @param world The world this chunk belongs to
	 * @param ids A ByteArray containing all the BlockID's to set this chunk to
	 * @param metadata A ByteArray containing all the metadata to set this chunk to
	 * @param chunkX The chunk's X position
	 * @param chunkZ The Chunk's Z position
	 */
	public Chunk(World world, byte[] ids, byte[] metadata, int chunkX, int chunkZ)
	{
		this(world, chunkX, chunkZ);
		int k = ids.length / 256;

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				for (int y = 0; y < k; ++y)
				{
					int idx = x << 11 | z << 7 | y;
					int id = ids[idx] & 0xFF;
					int meta = metadata[idx];

					if (id != 0)
					{
						int l = y >> 4;

						if (this.storageArrays[l] == null)
						{
							this.storageArrays[l] = new ExtendedBlockStorage(l << 4, !world.provider.hasNoSky);
						}

						this.storageArrays[l].setExtBlockID(x, y & 15, z, id);
						this.storageArrays[l].setExtBlockMetadata(x, y & 15, z, meta);
					}
				}
			}
		}
	}

	/**
	 * A Chunk Constructor which handles shorts to allow block ids > 256 (full 4096 range)
	 * Meta data sensitive
	 * NOTE: The x,y,z order of the array is different from the native Chunk constructor to allow for generation > y127
	 * NOTE: This is possibly more efficient than the standard constructor due to less memory skipping
	 *
	 * @param world The world this chunk belongs to
	 * @param ids A ShortArray containing all the BlockID's to set this chunk to (x is low order, z is mid, y is high)
	 * @param metadata A ByteArray containing all the metadata to set this chunk to
	 * @param chunkX The chunk's X position
	 * @param chunkZ The Chunk's Z position
	 */
	public Chunk(World world, short[] ids, byte[] metadata, int chunkX, int chunkZ)
	{
		this(world, chunkX, chunkZ);
		int max = ids.length / 256;

		for (int y = 0; y < max; ++y)
		{
			for (int z = 0; z < 16; ++z)
			{
				for (int x = 0; x < 16; ++x)
				{
					int idx = y << 8 | z << 4 | x;
					int id = ids[idx] & 0xFFFFFF;
					int meta = metadata[idx];

					if (id != 0)
					{
						int storageBlock = y >> 4;

						if (this.storageArrays[storageBlock] == null)
						{
							this.storageArrays[storageBlock] = new ExtendedBlockStorage(storageBlock << 4, !world.provider.hasNoSky);
						}

						this.storageArrays[storageBlock].setExtBlockID(x, y & 15, z, id);
						this.storageArrays[storageBlock].setExtBlockMetadata(x, y & 15, z, meta);
					}
				}
			}
		}
	}


	/**
	 * Checks whether the chunk is at the X/Z location specified
	 */
	public final boolean isAtLocation(int par1, int par2)
	{
		return par1 == this.xPosition && par2 == this.zPosition;
	}

	/**
	 * Returns the value in the height map at this x, z coordinate in the chunk
	 */
	public final int getHeightValue(int par1, int par2)
	{
		return this.is_empty ? 0 : this.heightMap[par2 << 4 | par1];
	}

	/**
	 * Returns the topmost ExtendedBlockStorage instance for this Chunk that actually contains a block.
	 */
	public final int getTopFilledSegment()
	{
		if (this.is_empty)
		{
			return 0;
		}
		else
		{
			for (int var1 = this.storageArrays.length - 1; var1 >= 0; --var1)
			{
				if (this.storageArrays[var1] != null)
				{
					return this.storageArrays[var1].getYLocation();
				}
			}

			return 0;
		}
	}

	/**
	 * Returns the ExtendedBlockStorage array for this Chunk.
	 */
	public final ExtendedBlockStorage[] getBlockStorageArray()
	{
		return this.storageArrays;
	}

	public final int calcHeightMapValue(int local_x, int top_filled_segment_plus_15, int local_z)
	{
		int index = local_x + local_z * 16;

		for (int y = top_filled_segment_plus_15; y >= 0; --y)
		{
			int block_id = this.getBlockIDOptimized(index, y);

			if (block_id > 0 && Block.lightOpacity[block_id] > 0)
			{
				return y + 1;
			}
		}

		return 0;
	}

	public final void generateHeightMap(boolean allow_overwrite)
	{
		if (this.is_empty)
		{
			Minecraft.setErrorMessage("generateHeightMapMITE: called for empty chunk on " + this.worldObj.getClientOrServerString());
		}
		else if (this.has_initial_heightmap_been_generated && !allow_overwrite)
		{
			Minecraft.setErrorMessage("generateHeightMapMITE: initial heightmap has already been generated for " + this);
			Debug.printStackTrace();
		}
		else
		{
			this.has_initial_heightmap_been_generated = true;
			int top_filled_segment_plus_15 = this.getTopFilledSegment() + 15;

			for (int local_x = 0; local_x < 16; ++local_x)
			{
				for (int local_z = 0; local_z < 16; ++local_z)
				{
					int index = local_x + local_z * 16;
					this.precipitationHeightMap[index] = -999;
					this.setHeightMap(index, this.calcHeightMapValue(local_x, top_filled_segment_plus_15, local_z));
				}
			}

			this.isModified = true;
		}
	}

	public final void generateSkylightColumn(int local_x, int local_z, int top_segment_plus_16, int new_heightmap_value, boolean mark_blocks_for_render_update)
	{
		this.propagateSkylightOcclusion(local_x, local_z);
		int index = local_x + local_z * 16;
		this.skylight_bottom[index] = skylight_bottom_initial_value;
		int brightness;

		for (brightness = new_heightmap_value; brightness < top_segment_plus_16; ++brightness)
		{
			ExtendedBlockStorage y = this.storageArrays[brightness >> 4];

			if (y != null)
			{
				y.setExtSkylightValue(index, brightness, 15, this);
			}
		}

		brightness = 15;

		for (int var12 = new_heightmap_value - 1; var12 >= 0 && brightness > 0; --var12)
		{
			int opacity = Block.lightOpacity[this.getBlockID(local_x, var12, local_z)];

			if (opacity > 0 && new_heightmap_value - 1 > var12)
			{
				++opacity;
			}

			if (opacity == 0)
			{
				opacity = 1;
			}

			if (brightness > 0 && brightness - opacity <= 0)
			{
				this.skylight_bottom[index] = var12 + 1;
			}

			brightness -= opacity;

			if (brightness < 0)
			{
				brightness = 0;
			}

			int ebs_index = var12 >> 4;
			ExtendedBlockStorage var10 = this.storageArrays[ebs_index];

			if (var10 == null)
			{
				var10 = new ExtendedBlockStorage(var12 >> 4 << 4, !this.worldObj.provider.hasNoSky);
				this.storageArrays[ebs_index] = var10;
			}

			var10.setExtSkylightValue(index, var12, brightness, this);
		}
	}

	public final void generateSkylightMap(boolean generate_heightmap)
	{
		if (this.is_empty)
		{
			Minecraft.setErrorMessage("generateSkylightMapMITE: called for empty chunk on " + this.worldObj.getClientOrServerString());
		}
		else if (this.worldObj.isRemote)
		{
			Minecraft.setErrorMessage("generateSkylightMapMITE: called on client?");
		}
		else
		{
			if (!this.hasSkylight())
			{
				Debug.setErrorMessage("generateSkylightMapMITE: called for world without skylight?");
			}

			if (this.has_initial_skymap_been_generated)
			{
				Minecraft.setErrorMessage("generateSkylightMapMITE: already called for " + this);
				Debug.printStackTrace();
			}

			this.has_initial_skymap_been_generated = true;

			if (generate_heightmap)
			{
				this.generateHeightMap(false);
			}

			int top_filled_segment_plus_16 = this.getTopFilledSegment() + 16;

			if (this.hasSkylight())
			{
				for (int local_x = 0; local_x < 16; ++local_x)
				{
					for (int local_z = 0; local_z < 16; ++local_z)
					{
						int index = local_x + local_z * 16;
						this.generateSkylightColumn(local_x, local_z, top_filled_segment_plus_16, this.heightMap[index], false);
					}
				}
			}
		}
	}

	/**
	 * Propagates a given sky-visible block's light value downward and upward to neighboring blocks as necessary.
	 */
	private final void propagateSkylightOcclusion(int par1, int par2)
	{
		this.updateSkylightColumns[par1 + par2 * 16] = true;
		this.isGapLightingUpdated = true;
	}

	public void propagateSkylightOcclusion()
	{
		Arrays.fill(this.updateSkylightColumns, true);
		this.isGapLightingUpdated = true;
	}

	private static boolean[] generateSkylightPropagationForNeighbor(int neighbor_dx, int neighbor_dz)
	{
		boolean[] update_skylight_columns = new boolean[256];
		int x;
		int z;

		if (neighbor_dx == -1 && neighbor_dz == -1)
		{
			for (x = 1; x < 16; ++x)
			{
				for (z = 16 - x; z < 16; ++z)
				{
					update_skylight_columns[x + z * 16] = true;
				}
			}
		}
		else if (neighbor_dx == 1 && neighbor_dz == -1)
		{
			for (x = 0; x < 15; ++x)
			{
				for (z = x + 1; z < 16; ++z)
				{
					update_skylight_columns[x + z * 16] = true;
				}
			}
		}
		else if (neighbor_dx == 1 && neighbor_dz == 1)
		{
			for (x = 0; x < 15; ++x)
			{
				for (z = 0; z < 15 - x; ++z)
				{
					update_skylight_columns[x + z * 16] = true;
				}
			}
		}
		else if (neighbor_dx == -1 && neighbor_dz == 1)
		{
			for (x = 1; x < 16; ++x)
			{
				for (z = 0; z < x; ++z)
				{
					update_skylight_columns[x + z * 16] = true;
				}
			}
		}
		else
		{
			Minecraft.setErrorMessage("generateSkylightPropagationForNeighbor: unhandled case");
		}

		return update_skylight_columns;
	}

	private static boolean[] getSkylightPropagationForNeighbor(int neighbor_dx, int neighbor_dz)
	{
		if (neighbor_dx == -1 && neighbor_dz == -1)
		{
			return skylight_propagation_from_neighbor_to_nw;
		}
		else if (neighbor_dx == 1 && neighbor_dz == -1)
		{
			return skylight_propagation_from_neighbor_to_ne;
		}
		else if (neighbor_dx == 1 && neighbor_dz == 1)
		{
			return skylight_propagation_from_neighbor_to_se;
		}
		else if (neighbor_dx == -1 && neighbor_dz == 1)
		{
			return skylight_propagation_from_neighbor_to_sw;
		}
		else
		{
			Minecraft.setErrorMessage("getSkylightPropagationForNeighbor: unhandled neighbor");
			return null;
		}
	}

	public void propagateSkylightFromNeighbor(int neighbor_dx, int neighbor_dz)
	{
		boolean[] update_skylight_columns = getSkylightPropagationForNeighbor(neighbor_dx, neighbor_dz);

		for (int i = 0; i < update_skylight_columns.length; ++i)
		{
			if (update_skylight_columns[i])
			{
				this.updateSkylightColumns[i] = true;
			}
		}

		this.isGapLightingUpdated = true;
		this.updateSkylight(true);
	}

	private final int getSkylightBottom(int local_x, int local_z)
	{
		return this.skylight_bottom[local_x + local_z * 16];
	}

	private int getLowestSkylightBottom(int var1, int var2)
	{
		int min_y = this.getSkylightBottom(var1, var2);

		if (var1 == 0)
		{
			min_y = Math.min(min_y, this.worldObj.getChunkFromChunkCoords(this.xPosition - 1, this.zPosition).getSkylightBottom(15, var2));
		}
		else
		{
			min_y = Math.min(min_y, this.getSkylightBottom(var1 - 1, var2));
		}

		if (var1 == 15)
		{
			min_y = Math.min(min_y, this.worldObj.getChunkFromChunkCoords(this.xPosition + 1, this.zPosition).getSkylightBottom(0, var2));
		}
		else
		{
			min_y = Math.min(min_y, this.getSkylightBottom(var1 + 1, var2));
		}

		if (var2 == 0)
		{
			min_y = Math.min(min_y, this.worldObj.getChunkFromChunkCoords(this.xPosition, this.zPosition - 1).getSkylightBottom(var1, 15));
		}
		else
		{
			min_y = Math.min(min_y, this.getSkylightBottom(var1, var2 - 1));
		}

		if (var2 == 15)
		{
			min_y = Math.min(min_y, this.worldObj.getChunkFromChunkCoords(this.xPosition, this.zPosition + 1).getSkylightBottom(var1, 0));
		}
		else
		{
			min_y = Math.min(min_y, this.getSkylightBottom(var1, var2 + 1));
		}

		return min_y;
	}

	private final boolean updateSkylight_do(boolean trusted_xz_for_this_chunk_and_immediate_neighbors)
	{
		this.worldObj.theProfiler.startSection("recheckGaps");
		boolean update_occured;

		if (!trusted_xz_for_this_chunk_and_immediate_neighbors && !this.worldObj.canUpdateLightByType(this.xPosition * 16, this.zPosition * 16))
		{
			update_occured = false;
			Debug.setErrorMessage("updateSkylight_do: wasn\'t able to update");
		}
		else
		{
			for (int var1 = 0; var1 < 16; ++var1)
			{
				for (int var2 = 0; var2 < 16; ++var2)
				{
					if (this.updateSkylightColumns[var1 + var2 * 16])
					{
						this.updateSkylightColumns[var1 + var2 * 16] = false;
						int var3 = this.getHeightValue(var1, var2);
						int var4 = this.xPosition * 16 + var1;
						int var5 = this.zPosition * 16 + var2;
						int min_y = this.getLowestSkylightBottom(var1, var2);
						checkSkylightNeighborHeight(var4 - 1, var5, var3, var1 > 0 ? this : this.worldObj.getChunkFromChunkCoords(this.xPosition - 1, this.zPosition), trusted_xz_for_this_chunk_and_immediate_neighbors);
						checkSkylightNeighborHeight(var4 + 1, var5, var3, var1 < 15 ? this : this.worldObj.getChunkFromChunkCoords(this.xPosition + 1, this.zPosition), trusted_xz_for_this_chunk_and_immediate_neighbors);
						checkSkylightNeighborHeight(var4, var5 - 1, var3, var2 > 0 ? this : this.worldObj.getChunkFromChunkCoords(this.xPosition, this.zPosition - 1), trusted_xz_for_this_chunk_and_immediate_neighbors);
						checkSkylightNeighborHeight(var4, var5 + 1, var3, var2 < 15 ? this : this.worldObj.getChunkFromChunkCoords(this.xPosition, this.zPosition + 1), trusted_xz_for_this_chunk_and_immediate_neighbors);
						checkSkylightNeighborHeight(var4, var5, min_y, this, true);
					}
				}
			}

			this.isGapLightingUpdated = false;
			update_occured = true;
		}

		this.worldObj.theProfiler.endSection();
		return update_occured;
	}

	private static final void checkSkylightNeighborHeight(int par1, int par2, int par3, Chunk chunk, boolean trusted_xz)
	{
		int var4 = chunk.heightMap[(par1 & 15) + (par2 & 15) * 16];

		if (var4 > par3)
		{
			updateSkylightNeighborHeight(par1, par2, par3, var4, chunk, trusted_xz);
		}
		else if (var4 < par3)
		{
			updateSkylightNeighborHeight(par1, par2, var4, par3, chunk, trusted_xz);
		}
	}

	private static final void updateSkylightNeighborHeight(int par1, int par2, int par3, int par4, Chunk chunk, boolean trusted_xz)
	{
		if (!chunk.isEmpty())
		{
			if (par4 >= par3)
			{
				int var5;

				if (!trusted_xz && !chunk.worldObj.canUpdateLightByType(par1, par2))
				{
					for (var5 = par3; var5 <= par4; ++var5)
					{
						chunk.addPendingSkylightUpdate(par1, var5, par2);
					}
				}
				else
				{
					for (var5 = par3; var5 <= par4; ++var5)
					{
						chunk.worldObj.updateLightByType(EnumSkyBlock.Sky, par1, var5, par2, true, chunk);
					}
				}

				chunk.isModified = true;
			}
			else
			{
				Debug.setErrorMessage("updateSkylightNeighborHeight: min_y was higher than max_y");
				Debug.printStackTrace();
			}
		}
	}

	private int getResultingHeightmapValue(int local_x, int y, int local_z, int opacity, int previous_heightmap_value, boolean update_skylight_above)
	{
		int previous_y = previous_heightmap_value - 1;

		if (y < previous_y)
		{
			return previous_heightmap_value;
		}
		else if (opacity == 0)
		{
			if (y != previous_y)
			{
				return previous_heightmap_value;
			}
			else
			{
				int index = local_x + local_z * 16;

				if (update_skylight_above)
				{
					this.storageArrays[y >> 4].setExtSkylightValue(index, y, 15, this);

					while (true)
					{
						--y;

						if (y < 0 || Block.lightOpacity[this.getBlockIDOptimized(index, y)] != 0)
						{
							break;
						}

						ExtendedBlockStorage ebs = this.storageArrays[y >> 4];

						if (ebs == null)
						{
							ebs = this.storageArrays[y >> 4] = new ExtendedBlockStorage(y >> 4 << 4, this.hasSkylight());
						}

						ebs.setExtSkylightValue(index, y, 15, this);
					}
				}
				else
				{
					do
					{
						--y;
					}
					while (y >= 0 && Block.lightOpacity[this.getBlockIDOptimized(index, y)] == 0);
				}

				return y + 1;
			}
		}
		else
		{
			return y + 1;
		}
	}

	private final void relightBlock(int local_x, int y, int local_z, int previous_opacity, int new_opacity)
	{
		int original_y = y;
		int x = this.getNonLocalX(local_x);
		int z = this.getNonLocalZ(local_z);
		int index = local_x + local_z * 16;
		int previous_heightmap_value = this.heightMap[index];
		int previous_skylight_bottom = this.skylight_bottom[index];
		int new_heightmap_value = this.getResultingHeightmapValue(local_x, y, local_z, new_opacity, previous_heightmap_value, this.hasSkylight());
		this.setHeightMap(index, new_heightmap_value);

		if (this.hasSkylight())
		{
			++y;
			int brightness;

			if (y >= new_heightmap_value)
			{
				brightness = 15;
			}
			else
			{
				ExtendedBlockStorage opacity = this.storageArrays[y >> 4];
				brightness = opacity == null ? 15 : opacity.getExtSkylightValue(index, y & 15);
			}

			do
			{
				--y;
				int var17 = Block.lightOpacity[this.getBlockIDOptimized(index, y)];

				if (var17 > 0)
				{
					if (new_heightmap_value - 1 > y)
					{
						++var17;
					}
				}
				else
				{
					var17 = 1;
				}

				brightness -= var17;

				if (brightness < 0)
				{
					brightness = 0;
				}

				int ebs_index = y >> 4;
				ExtendedBlockStorage var10 = this.storageArrays[ebs_index];

				if (var10 == null)
				{
					this.storageArrays[ebs_index] = var10 = new ExtendedBlockStorage(ebs_index << 4, !this.worldObj.provider.hasNoSky);
					var10.fillSkylightValues(15);
				}

				var10.setExtSkylightValue(index, y, brightness, this);
			}
			while (y > 0 && (y > previous_skylight_bottom || brightness > 0));

			if (new_opacity < previous_opacity && new_heightmap_value < y)
			{
				y = new_heightmap_value;
			}
			else if (y < original_y && this.getBlockLightOpacity(local_x, y, local_z) > 14)
			{
				++y;
			}

			this.worldObj.markBlockRangeForRenderUpdate(x, y, z, x, original_y, z);
			updateSkylightNeighborHeight(x - 1, z, y, original_y, local_x > 0 ? this : this.worldObj.getChunkFromChunkCoords(this.xPosition - 1, this.zPosition), false);
			updateSkylightNeighborHeight(x + 1, z, y, original_y, local_x < 15 ? this : this.worldObj.getChunkFromChunkCoords(this.xPosition + 1, this.zPosition), false);
			updateSkylightNeighborHeight(x, z - 1, y, original_y, local_z > 0 ? this : this.worldObj.getChunkFromChunkCoords(this.xPosition, this.zPosition - 1), false);
			updateSkylightNeighborHeight(x, z + 1, y, original_y, local_z < 15 ? this : this.worldObj.getChunkFromChunkCoords(this.xPosition, this.zPosition + 1), false);
			updateSkylightNeighborHeight(x, z, y, original_y, this, false);
		}

		this.isModified = true;
	}

	public final int getBlockLightOpacity(int par1, int par2, int par3)
	{
		int x = (xPosition << 4) + par1;
		int z = (zPosition << 4) + par3;
		Block block = Block.blocksList[getBlockID(par1, par2, par3)];
		return (block == null ? 0 : block.getLightOpacity(worldObj, x, par2, z));
	}

	/**
	 * Return the ID of a block in the chunk.
	 */
	public final int getBlockID(int par1, int par2, int par3)
	{
		if (this.is_empty)
		{
			return 0;
		}
		else
		{
			int var4 = par2 >> 4;

			if (var4 < this.storageArrays.length)
			{
				ExtendedBlockStorage var5 = this.storageArrays[var4];

				if (var5 != null)
				{
					int var6 = par2 & 15;
					int var7 = var5.blockLSBArray[var6 << 8 | par3 << 4 | par1] & 255;

					if (var5.blockMSBArray != null)
					{
						int var8 = var6 << 8 | par3 << 4 | par1;
						int var9 = var8 >> 1;
						return (var8 & 1) == 0 ? var5.blockMSBArray.data[var9] & 15 : var5.blockMSBArray.data[var9] >> 4 & 15;
					}

					return var7;
				}
			}

			return 0;
		}
	}

	public final int getBlockIDOptimized(int xz_index, int y)
	{
		ExtendedBlockStorage ebs = this.storageArrays[y >> 4];
		return ebs == null ? 0 : ebs.blockLSBArray[(y & 15) << 8 | xz_index] & 255;
	}

	/**
	 * Return the metadata corresponding to the given coordinates inside a chunk.
	 */
	public final int getBlockMetadata(int par1, int par2, int par3)
	{
		if (this.is_empty)
		{
			return 0;
		}
		else if (par2 >> 4 >= this.storageArrays.length)
		{
			return 0;
		}
		else
		{
			ExtendedBlockStorage var4 = this.storageArrays[par2 >> 4];
			return var4 != null ? var4.getExtBlockMetadata(par1, par2 & 15, par3) : 0;
		}
	}

	private void setHeightMap(int index, int value)
	{
		this.heightMap[index] = value;
	}

	public final boolean setBlockIDWithMetadata(int par1, int par2, int par3, int par4, int par5, int block_id_before)
	{
		if (!this.is_empty && this.is_within_block_domain)
		{
			int var6 = par3 << 4 | par1;

			if (par2 >= this.precipitationHeightMap[var6] - 1)
			{
				this.precipitationHeightMap[var6] = -999;
			}

			int var7 = this.heightMap[var6];
			int var9 = this.getBlockMetadata(par1, par2, par3);

			if (block_id_before == par4 && var9 == par5)
			{
				return false;
			}
			else
			{
				ExtendedBlockStorage var10 = this.storageArrays[par2 >> 4];
				boolean var11 = false;

				if (var10 == null)
				{
					if (par4 == 0)
					{
						return false;
					}

					var10 = this.storageArrays[par2 >> 4] = new ExtendedBlockStorage(par2 >> 4 << 4, !this.worldObj.provider.hasNoSky);
					var11 = par2 >= var7;
				}

				int var12 = this.xPosition * 16 + par1;
				int var13 = this.zPosition * 16 + par3;

				if (block_id_before != 0 && !this.worldObj.isRemote)
				{
					Block.blocksList[block_id_before].onBlockPreDestroy(this.worldObj, var12, par2, var13, var9);
				}

				var10.setExtBlockID(par1, par2 & 15, par3, par4);

				if (block_id_before != 0)
				{
					if (!this.worldObj.isRemote)
					{
						Block.blocksList[block_id_before].breakBlock(this.worldObj, var12, par2, var13, block_id_before, var9);
					}
					else if (Block.blocksList[block_id_before] != null && Block.blocksList[block_id_before].hasTileEntity(var9))
					{
						TileEntity te = getChunkBlockTileEntityUnsafe(var12 & 0xf, par2, var13 & 0xf);
						if (te != null && te.shouldRefresh(block_id_before, par4, var9, par5, worldObj, var12, par2, var13))
						{
							this.worldObj.removeBlockTileEntity(var12, par2, var13);
						}
					}
				}

				if (var10.getExtBlockID(par1, par2 & 15, par3) != par4)
				{
					return false;
				}
				else
				{
					var10.setExtBlockMetadata(par1, par2 & 15, par3, par5);

					if (var11 && this.hasSkylight())
					{
						var10.fillSkylightValues(15);
					}

					int previous_opacity = Block.lightOpacity[block_id_before];
					int new_opacity = Block.lightOpacity[par4];

					if (new_opacity != previous_opacity && (new_opacity > 0 || par2 < var7))
					{
						this.relightBlock(par1, par2, par3, previous_opacity, new_opacity);
					}

					TileEntity var14;

					if (par4 != 0)
					{
						if (!this.worldObj.isRemote || par4 == Block.runestoneMithril.blockID || par4 == Block.runestoneAdamantium.blockID)
						{
							Block.blocksList[par4].onBlockAdded(this.worldObj, var12, par2, var13);
						}

						if (Block.blocksList[par4] != null && Block.blocksList[par4].hasTileEntity(par5))
						{
							var14 = this.getChunkBlockTileEntity(par1, par2, par3);

							if (var14 == null)
							{
								var14 = Block.blocksList[par4].createTileEntity(this.worldObj, par5);
								this.worldObj.setBlockTileEntity(var12, par2, var13, var14);
							}

							if (var14 != null)
							{
								var14.updateContainingBlockInfo();
							}
						}
					}
//					else if (block_id_before > 0 && Block.blocksList[block_id_before] instanceof ITileEntityProvider)
//					{
//						var14 = this.getChunkBlockTileEntity(par1, par2, par3);
//
//						if (var14 != null)
//						{
//							var14.updateContainingBlockInfo();
//						}
//					}

					this.isModified = true;
					this.worldObj.markWorldMapPixelDirty(var12, var13);
					return true;
				}
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * Set the metadata of a block in the chunk
	 */
	public final boolean setBlockMetadata(int par1, int par2, int par3, int par4)
	{
		if (this.is_empty)
		{
			return false;
		}
		else
		{
			if (!this.worldObj.isRemote)
			{
				int var5 = par3 << 4 | par1;

				if (par2 >= this.precipitationHeightMap[var5] - 1)
				{
					this.precipitationHeightMap[var5] = -999;
				}
			}

			ExtendedBlockStorage var9 = this.storageArrays[par2 >> 4];

			if (var9 == null)
			{
				return false;
			}
			else
			{
				int var6 = var9.getExtBlockMetadata(par1, par2 & 15, par3);

				if (var6 == par4)
				{
					return false;
				}
				else
				{
					this.isModified = true;
					var9.setExtBlockMetadata(par1, par2 & 15, par3, par4);
					int var7 = var9.getExtBlockID(par1, par2 & 15, par3);

					if (var7 > 0 && Block.blocksList[var7] != null && Block.blocksList[var7].hasTileEntity(par4))
					{
						TileEntity var8 = this.getChunkBlockTileEntity(par1, par2, par3);

						if (var8 != null)
						{
							var8.updateContainingBlockInfo();
							var8.blockMetadata = par4;
						}
					}

					return true;
				}
			}
		}
	}

	/**
	 * Gets the amount of light saved in this block (doesn't adjust for daylight)
	 */
	public final int getSavedLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
	{
		if (this.is_empty)
		{
			return 0;
		}
		else
		{
			ExtendedBlockStorage var5 = this.storageArrays[par3 >> 4];
			return var5 == null ? (this.canBlockSeeTheSky(par2, par3, par4) ? par1EnumSkyBlock.defaultLightValue : 0) : (par1EnumSkyBlock == EnumSkyBlock.Sky ? (this.worldObj.provider.hasNoSky ? 0 : var5.getExtSkylightValue(par2, par3 & 15, par4)) : (par1EnumSkyBlock == EnumSkyBlock.Block ? var5.getExtBlocklightValue(par2, par3 & 15, par4) : par1EnumSkyBlock.defaultLightValue));
		}
	}

	public final int getSavedSkylightValue(int par2, int par3, int par4)
	{
		if (this.is_empty)
		{
			return 0;
		}
		else
		{
			ExtendedBlockStorage var5 = this.storageArrays[par3 >> 4];
			return var5 == null ? (this.canBlockSeeTheSkyForNonEmptyChunk(par2, par3, par4) ? 15 : 0) : var5.getExtSkylightValue(par2, par3 & 15, par4);
		}
	}

	public final int getSavedBlocklightValue(int par2, int par3, int par4)
	{
		if (this.is_empty)
		{
			return 0;
		}
		else
		{
			ExtendedBlockStorage var5 = this.storageArrays[par3 >> 4];
			return var5 == null ? 0 : var5.getExtBlocklightValue(par2, par3 & 15, par4);
		}
	}

	public final int getSavedLightValueForNonEmptyChunk(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
	{
		ExtendedBlockStorage var5 = this.storageArrays[par3 >> 4];
		return var5 == null ? (this.canBlockSeeTheSkyForNonEmptyChunk(par2, par3, par4) ? par1EnumSkyBlock.defaultLightValue : 0) : (par1EnumSkyBlock == EnumSkyBlock.Sky ? (this.worldObj.provider.hasNoSky ? 0 : var5.getExtSkylightValue(par2, par3 & 15, par4)) : (par1EnumSkyBlock == EnumSkyBlock.Block ? var5.getExtBlocklightValue(par2, par3 & 15, par4) : par1EnumSkyBlock.defaultLightValue));
	}

	public final int getSavedSkylightValueForNonEmptyChunk(int par2, int par3, int par4)
	{
		ExtendedBlockStorage var5 = this.storageArrays[par3 >> 4];
		return var5 == null ? (this.canBlockSeeTheSkyForNonEmptyChunk(par2, par3, par4) ? 15 : 0) : var5.getExtSkylightValue(par2, par3 & 15, par4);
	}

	public final int getSavedBlocklightValueForNonEmptyChunk(int par2, int par3, int par4)
	{
		ExtendedBlockStorage var5 = this.storageArrays[par3 >> 4];
		return var5 == null ? 0 : var5.getExtBlocklightValue(par2, par3 & 15, par4);
	}

	/**
	 * Sets the light value at the coordinate. If enumskyblock is set to sky it sets it in the skylightmap and if its a
	 * block then into the blocklightmap. Args enumSkyBlock, x, y, z, lightValue
	 */
	public final void setLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4, int par5)
	{
		if (!this.is_empty)
		{
			ExtendedBlockStorage var6 = this.storageArrays[par3 >> 4];

			if (var6 == null)
			{
				var6 = this.storageArrays[par3 >> 4] = new ExtendedBlockStorage(par3 >> 4 << 4, !this.worldObj.provider.hasNoSky);

				if (this.hasSkylight())
				{
					var6.fillSkylightValues(EnumSkyBlock.Sky.defaultLightValue);
				}
			}

			this.isModified = true;

			if (par1EnumSkyBlock == EnumSkyBlock.Sky)
			{
				if (!this.worldObj.provider.hasNoSky)
				{
					var6.setExtSkylightValue(par2 + par4 * 16, par3, par5, this);
				}
			}
			else
			{
				var6.setExtBlocklightValue(par2, par3 & 15, par4, par5);
			}
		}
	}

	public final void setSkylightValue(int par2, int par3, int par4, int par5)
	{
		if (!this.is_empty)
		{
			if (!this.hasSkylight())
			{
				Debug.setErrorMessage("setSkylightValue: chunk does not have skylight");
			}

			ExtendedBlockStorage var6 = this.storageArrays[par3 >> 4];

			if (var6 == null)
			{
				var6 = this.storageArrays[par3 >> 4] = new ExtendedBlockStorage(par3 >> 4 << 4, true);
				var6.fillSkylightValues(15);
			}

			var6.setExtSkylightValue(par2 + par4 * 16, par3, par5, this);
			this.isModified = true;
		}
	}

	public final void setBlocklightValue(int par2, int par3, int par4, int par5)
	{
		if (!this.is_empty)
		{
			ExtendedBlockStorage var6 = this.storageArrays[par3 >> 4];

			if (var6 == null)
			{
				if (this.hasSkylight())
				{
					var6 = this.storageArrays[par3 >> 4] = new ExtendedBlockStorage(par3 >> 4 << 4, true);
					var6.fillSkylightValues(15);
				}
				else
				{
					var6 = this.storageArrays[par3 >> 4] = new ExtendedBlockStorage(par3 >> 4 << 4, false);
				}
			}

			var6.setExtBlocklightValue(par2, par3 & 15, par4, par5);
			this.isModified = true;
		}
	}

	public final int getNonLocalX(int local_x)
	{
		return (this.xPosition << 4) + local_x;
	}

	public final int getNonLocalZ(int local_z)
	{
		return (this.zPosition << 4) + local_z;
	}

	/**
	 * Gets the amount of light on a block taking into account sunlight
	 */
	public final int getBlockLightValue(int par1, int par2, int par3, int par4)
	{
		if (this.is_empty)
		{
			return 0;
		}
		else
		{
			ExtendedBlockStorage var5 = this.storageArrays[par2 >> 4];

			if (var5 != null)
			{
				int var6 = this.worldObj.provider.hasNoSky ? 0 : var5.getExtSkylightValue(par1, par2 & 15, par3);

				if (var6 > 0)
				{
					isLit = true;
				}

				var6 -= par4;
				int var7 = var5.getExtBlocklightValue(par1, par2 & 15, par3);

				if (var7 > var6)
				{
					var6 = var7;
				}

				return var6;
			}
			else
			{
				return !this.worldObj.provider.hasNoSky && par4 < EnumSkyBlock.Sky.defaultLightValue ? EnumSkyBlock.Sky.defaultLightValue - par4 : 0;
			}
		}
	}

	/**
	 * Adds an entity to the chunk. Args: entity
	 */
	public final void addEntity(Entity par1Entity)
	{
		if (!this.is_empty)
		{
			if (!par1Entity.isDead)
			{
				if (par1Entity.isAddedToAChunk())
				{
					Minecraft.setErrorMessage("addEntity: " + par1Entity.getEntityName() + " already belongs to a different chunk!");
					(new Exception()).printStackTrace();
				}

				if (Minecraft.inDevMode())
				{
					if (this.doesEntityObjectExistInEntityLists(par1Entity))
					{
						System.out.println("addEntity: " + par1Entity.getEntityName() + " is already in the entityLists!");
						(new Exception()).printStackTrace();
					}
					else if (par1Entity.isExpectedToHaveUUID() && this.doesEntityWithMatchingClassAndUUIDExistInEntityLists(par1Entity))
					{
						System.out.println("addEntity: " + par1Entity.getEntityName() + " is already in the entityLists! (UUID match: " + par1Entity.getUniqueID() + " in " + this.worldObj.getDimensionName() + ")");
						(new Exception()).printStackTrace();
					}
				}

				int var2 = MathHelper.floor_double(par1Entity.posX / 16.0D);
				int var3 = MathHelper.floor_double(par1Entity.posZ / 16.0D);

				if (var2 != this.xPosition || var3 != this.zPosition)
				{
					this.worldObj.getWorldLogAgent().logSevere("Wrong location! " + par1Entity);
					Minecraft.setErrorMessage("addEntity: chunk position is " + this.xPosition + "," + this.zPosition + " but entity\'s chunk position is " + var2 + "," + var3 + ", block pos is " + par1Entity.posX + "," + par1Entity.posZ);
					Thread.dumpStack();
				}

				int var4 = par1Entity.getChunkCurrentlyInSectionIndex();
				par1Entity.setChunkAddedToUnchecked(this, var4);

				if (!this.entityLists[var4].add(par1Entity))
				{
					Minecraft.setErrorMessage("addEntity: was not able to add " + par1Entity.getEntityName() + " to entityLists!");
				}

				this.setChunkModified();
			}
		}
	}

	/**
	 * removes entity using its y chunk coordinate as its index
	 */
	public final void removeEntity(Entity par1Entity)
	{
		if (!this.is_empty)
		{
			this.removeEntityThoroughly(par1Entity);
		}
	}

	public final boolean removeEntityThoroughly(Entity entity)
	{
		short RL = 196;

		if (Minecraft.inDevMode() && 196 > RL)
		{
			System.out.println("Reminder: removeEntityThoroughly is still being used");
		}

		if (this.is_empty)
		{
			return false;
		}
		else
		{
			Chunk chunk = entity.getChunkAddedTo();

			if (chunk == null)
			{
				Minecraft.setErrorMessage("removeEntityThoroughly: entity\'s chunk_added_to was null");
			}
			else if (chunk.xPosition != this.xPosition || chunk.zPosition != this.zPosition)
			{
				Minecraft.setErrorMessage("removeEntityThoroughly: entity\'s chunk_added_to was not this one");
			}

			int num_removals = 0;

			for (int was_removed = 0; was_removed < 16; ++was_removed)
			{
				List entity_list = this.entityLists[was_removed];
				Iterator iterator = entity_list.iterator();

				while (iterator.hasNext())
				{
					Entity entity_in_list = (Entity)iterator.next();

					if (entity_in_list == entity)
					{
						iterator.remove();
						++num_removals;
					}
					else if (entity.isExpectedToHaveUUID() && entity_in_list.isExpectedToHaveUUID() && entity_in_list.getClass() == entity.getClass() && entity_in_list.getUniqueID().equals(entity.getUniqueID()))
					{
						iterator.remove();
						++num_removals;
					}
				}
			}

			if (num_removals > 1)
			{
				System.out.println("removeEntityThoroughly: " + entity.getEntityName() + " was removed " + num_removals + " times from chunk\'s entityLists (UUID=" + entity.getUniqueID() + " in " + this.worldObj.getDimensionName() + ")");
			}

			boolean var9 = num_removals > 0;

			if (var9)
			{
				entity.setChunkAddedToUnchecked((Chunk)null, -1);
				this.setChunkModified();
			}
			else
			{
				System.out.println("removeEntityThoroughly: was not able to remove " + entity.getEntityName() + " from chunk\'s entityLists");
				(new Exception()).printStackTrace();
			}

			return var9;
		}
	}

	private final boolean removeEntityAtIndex(Entity par1Entity, int par2)
	{
		if (this.is_empty)
		{
			return false;
		}
		else
		{
			if (par2 < 0)
			{
				par2 = 0;
			}

			if (par2 >= this.entityLists.length)
			{
				par2 = this.entityLists.length - 1;
			}
			MinecraftForge.EVENT_BUS.post(new EntityEvent.EnteringChunk(par1Entity, this.xPosition, this.zPosition,
					par1Entity.getChunkPosX(), par1Entity.getChunkPosZ()));
			Chunk chunk = par1Entity.getChunkAddedTo();

			if (chunk == null)
			{
				Minecraft.setErrorMessage("removeEntityAtIndex: entity\'s chunk_added_to was null");
			}
			else if (chunk.xPosition == this.xPosition && chunk.zPosition == this.zPosition)
			{
				if (par1Entity.chunk_added_to_section_index != par2)
				{
					Minecraft.setErrorMessage("removeEntityAtIndex: entity\'s chunk_added_to_section_index was different from the param received");
				}
			}
			else
			{
				Minecraft.setErrorMessage("removeEntityAtIndex: entity\'s chunk_added_to was not this one");
			}

			boolean was_removed = this.entityLists[par2].remove(par1Entity);

			if (was_removed)
			{
				par1Entity.setChunkAddedToUnchecked((Chunk)null, -1);
				this.setChunkModified();
			}
			else
			{
				System.out.println("removeEntityAtIndex: was not able to remove " + par1Entity.getEntityName() + " from chunk\'s entityLists");
				(new Exception()).printStackTrace();
			}

			return was_removed;
		}
	}

	/**
	 * Returns whether is not a block above this one blocking sight to the sky (done via checking against the heightmap)
	 */
	public final boolean canBlockSeeTheSky(int par1, int par2, int par3)
	{
		return this.is_empty ? false : par2 >= this.heightMap[par3 << 4 | par1];
	}

	public final boolean canBlockSeeTheSkyForNonEmptyChunk(int par1, int par2, int par3)
	{
		return par2 >= this.heightMap[par3 << 4 | par1];
	}

	/**
	 * Gets the TileEntity for a given block in this chunk
	 */
	public TileEntity getChunkBlockTileEntity(int par1, int par2, int par3)
	{
		ChunkPosition var4 = new ChunkPosition(par1, par2, par3);
		TileEntity var5 = (TileEntity)this.chunkTileEntityMap.get(var4);

		if (var5 != null && var5.isInvalid())
		{
			chunkTileEntityMap.remove(var4);
			var5 = null;
		}

		if (var5 == null)
		{
			int var6 = this.getBlockID(par1, par2, par3);

			int meta = this.getBlockMetadata(par1, par2, par3);

			if (var6 <= 0 || !Block.blocksList[var6].hasTileEntity(meta))
			{
				return null;
			}

			if (var5 == null)
			{
				var5 = Block.blocksList[var6].createTileEntity(this.worldObj, meta);
				this.worldObj.setBlockTileEntity(this.xPosition * 16 + par1, par2, this.zPosition * 16 + par3, var5);
			}

			var5 = (TileEntity)this.chunkTileEntityMap.get(var4);
		}

		return var5;

	}

	/**
	 * Adds a TileEntity to a chunk
	 */
	public void addTileEntity(TileEntity par1TileEntity)
	{
		int var2 = par1TileEntity.xCoord - this.xPosition * 16;
		int var3 = par1TileEntity.yCoord;
		int var4 = par1TileEntity.zCoord - this.zPosition * 16;
		this.setChunkBlockTileEntity(var2, var3, var4, par1TileEntity);

		if (this.isChunkLoaded)
		{
			this.worldObj.addTileEntity(par1TileEntity);
		}
	}

	/**
	 * Sets the TileEntity for a given block in this chunk
	 */
	public void setChunkBlockTileEntity(int par1, int par2, int par3, TileEntity par4TileEntity)
	{
		ChunkPosition var5 = new ChunkPosition(par1, par2, par3);
		par4TileEntity.setWorldObj(this.worldObj);
		par4TileEntity.xCoord = this.xPosition * 16 + par1;
		par4TileEntity.yCoord = par2;
		par4TileEntity.zCoord = this.zPosition * 16 + par3;

		Block block = Block.blocksList[getBlockID(par1, par2, par3)];
		if (block != null && block.hasTileEntity(getBlockMetadata(par1, par2, par3)))
		{
			if (this.chunkTileEntityMap.containsKey(var5))
			{
				((TileEntity)this.chunkTileEntityMap.get(var5)).invalidate();
			}

			par4TileEntity.validate();
			this.chunkTileEntityMap.put(var5, par4TileEntity);
		}
	}

	/**
	 * Removes the TileEntity for a given block in this chunk
	 */
	public void removeChunkBlockTileEntity(int par1, int par2, int par3)
	{
		ChunkPosition var4 = new ChunkPosition(par1, par2, par3);

		if (this.isChunkLoaded)
		{
			TileEntity var5 = (TileEntity)this.chunkTileEntityMap.remove(var4);

			if (var5 != null)
			{
				var5.invalidate();
			}
		}
	}

	/**
	 * Called when this Chunk is loaded by the ChunkProvider
	 */
	public void onChunkLoad()
	{
		this.isChunkLoaded = true;
		this.worldObj.addTileEntity(this.chunkTileEntityMap.values());
		ArrayList var1 = new ArrayList();

		for (int var2 = 0; var2 < this.entityLists.length; ++var2)
		{
			Iterator var3 = this.entityLists[var2].iterator();

			while (var3.hasNext())
			{
				Entity var4 = (Entity)var3.next();

				if (Minecraft.inDevMode())
				{
					Entity var5 = World.getEntityWithSameClassAndUUIDInEntityList("onChunkLoad", var4, var1, true);

					if (var5 != null)
					{
						Minecraft.setErrorMessage("onChunkLoad: A duplicate of " + var4.getEntityName() + " is already being loaded from the chunk. Skipping.");
						continue;
					}

					var5 = this.worldObj.getEntityWithSameClassAndUUIDInLoadedEntityList(var4, true);

					if (var5 != null && !this.worldObj.isEntityObjectInUnloadedEntityList(var5))
					{
						Minecraft.setErrorMessage("onChunkLoad: A duplicate of " + var4.getEntityName() + " already exists in the world. Skipping.");
						continue;
					}
				}

				var1.add(var4);
				var4.onChunkLoad();
			}
		}

		this.worldObj.addLoadedEntities(var1);
		MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(this));
	}

	/**
	 * Called when this Chunk is unloaded by the ChunkProvider
	 */
	public void onChunkUnload()
	{
		this.isChunkLoaded = false;
		Iterator var1 = this.chunkTileEntityMap.values().iterator();

		while (var1.hasNext())
		{
			TileEntity var2 = (TileEntity)var1.next();
			this.worldObj.markTileEntityForDespawn(var2);
		}

		for (int var3 = 0; var3 < this.entityLists.length; ++var3)
		{
			this.worldObj.unloadEntities(this.entityLists[var3]);
		}
		MinecraftForge.EVENT_BUS.post(new ChunkEvent.Unload(this));
	}


	/**
	 * Sets the isModified flag for this Chunk
	 */
	public final void setChunkModified()
	{
		if (!this.is_empty)
		{
			this.isModified = true;
		}
	}

	/**
	 * Fills the given list of all entities that intersect within the given bounding box that aren't the passed entity
	 * Args: entity, aabb, listToFill
	 */
	public void getEntitiesWithinAABBForEntity(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB, List par3List, IEntitySelector par4IEntitySelector)
	{
		int var5 = MathHelper.floor_double((par2AxisAlignedBB.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
		int var6 = MathHelper.floor_double((par2AxisAlignedBB.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);

		if (var5 < 0)
		{
			var5 = 0;
			var6 = Math.max(var5, var6);
		}

		if (var6 >= this.entityLists.length)
		{
			var6 = this.entityLists.length - 1;
			var5 = Math.min(var5, var6);
		}

		for (int var7 = var5; var7 <= var6; ++var7)
		{
			List var8 = this.entityLists[var7];

			for (int var9 = 0; var9 < var8.size(); ++var9)
			{
				Entity var10 = (Entity)var8.get(var9);

				if (var10 != par1Entity && var10.boundingBox.intersectsWith(par2AxisAlignedBB) && (par4IEntitySelector == null || par4IEntitySelector.isEntityApplicable(var10)))
				{
					par3List.add(var10);
					Entity[] var11 = var10.getParts();

					if (var11 != null)
					{
						for (int var12 = 0; var12 < var11.length; ++var12)
						{
							var10 = var11[var12];

							if (var10 != par1Entity && var10.boundingBox.intersectsWith(par2AxisAlignedBB) && (par4IEntitySelector == null || par4IEntitySelector.isEntityApplicable(var10)))
							{
								par3List.add(var10);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Gets all entities that can be assigned to the specified class. Args: entityClass, aabb, listToFill
	 */
	public void getEntitiesOfTypeWithinAAAB(Class par1Class, AxisAlignedBB par2AxisAlignedBB, List par3List, IEntitySelector par4IEntitySelector)
	{
		int var5 = MathHelper.floor_double((par2AxisAlignedBB.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
		int var6 = MathHelper.floor_double((par2AxisAlignedBB.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);

		if (var5 < 0)
		{
			var5 = 0;
		}
		else if (var5 >= this.entityLists.length)
		{
			var5 = this.entityLists.length - 1;
		}

		if (var6 >= this.entityLists.length)
		{
			var6 = this.entityLists.length - 1;
		}
		else if (var6 < 0)
		{
			var6 = 0;
		}

		for (int var7 = var5; var7 <= var6; ++var7)
		{
			List var8 = this.entityLists[var7];

			for (int var9 = 0; var9 < var8.size(); ++var9)
			{
				Entity var10 = (Entity)var8.get(var9);

				if (par1Class.isAssignableFrom(var10.getClass()) && var10.boundingBox.intersectsWith(par2AxisAlignedBB) && (par4IEntitySelector == null || par4IEntitySelector.isEntityApplicable(var10)))
				{
					par3List.add(var10);
				}
			}
		}
	}

	/**
	 * Returns true if this Chunk needs to be saved
	 */
	public final boolean needsSaving(boolean par1)
	{
		if (this.is_empty)
		{
			return false;
		}
		else if (this.isModified)
		{
			return true;
		}
		else if (this.should_be_saved_once_time_forwarding_is_completed && this.worldObj.getTotalWorldTime() - this.last_total_world_time < 100L)
		{
			return true;
		}
		else
		{
			if (par1)
			{
				if (this.worldObj.getTotalWorldTime() != this.lastSaveTime && this.hasEntitiesForWritingToNBT())
				{
					return true;
				}
			}
			else if (this.worldObj.getTotalWorldTime() >= this.lastSaveTime + 600L && this.hasEntitiesForWritingToNBT())
			{
				return true;
			}

			return this.isModified;
		}
	}

	public Random getRandomWithSeed(long par1)
	{
		return new Random(this.worldObj.getSeed() + (long)(this.xPosition * this.xPosition * 4987142) + (long)(this.xPosition * 5947611) + (long)(this.zPosition * this.zPosition) * 4392871L + (long)(this.zPosition * 389711) ^ par1);
	}

	public final boolean isEmpty()
	{
		return this.is_empty;
	}

	public void populateChunk(IChunkProvider par1IChunkProvider, IChunkProvider par2IChunkProvider, int par3, int par4)
	{
		if (!this.isTerrainPopulated && par1IChunkProvider.chunkExists(par3 + 1, par4 + 1) && par1IChunkProvider.chunkExists(par3, par4 + 1) && par1IChunkProvider.chunkExists(par3 + 1, par4))
		{
			par1IChunkProvider.populate(par2IChunkProvider, par3, par4);
		}

		if (par1IChunkProvider.chunkExists(par3 - 1, par4) && !par1IChunkProvider.provideChunk(par3 - 1, par4).isTerrainPopulated && par1IChunkProvider.chunkExists(par3 - 1, par4 + 1) && par1IChunkProvider.chunkExists(par3, par4 + 1) && par1IChunkProvider.chunkExists(par3 - 1, par4 + 1))
		{
			par1IChunkProvider.populate(par2IChunkProvider, par3 - 1, par4);
		}

		if (par1IChunkProvider.chunkExists(par3, par4 - 1) && !par1IChunkProvider.provideChunk(par3, par4 - 1).isTerrainPopulated && par1IChunkProvider.chunkExists(par3 + 1, par4 - 1) && par1IChunkProvider.chunkExists(par3 + 1, par4 - 1) && par1IChunkProvider.chunkExists(par3 + 1, par4))
		{
			par1IChunkProvider.populate(par2IChunkProvider, par3, par4 - 1);
		}

		if (par1IChunkProvider.chunkExists(par3 - 1, par4 - 1) && !par1IChunkProvider.provideChunk(par3 - 1, par4 - 1).isTerrainPopulated && par1IChunkProvider.chunkExists(par3, par4 - 1) && par1IChunkProvider.chunkExists(par3 - 1, par4))
		{
			par1IChunkProvider.populate(par2IChunkProvider, par3 - 1, par4 - 1);
		}
	}

	public final int getPrecipitationHeightNew(int par1, int par2)
	{
		if (this.is_empty)
		{
			return 0;
		}
		else
		{
			int var3 = par1 | par2 << 4;
			int var4 = this.precipitationHeightMap[var3];

			if (var4 == -999)
			{
				int var5 = this.getTopFilledSegment() + 15;
				var4 = -1;

				while (var5 > 0)
				{
					int block_id = this.getBlockIDOptimized(var3, var5);

					if (block_id == 0)
					{
						--var5;
					}
					else
					{
						Block block = Block.getBlock(block_id);

						if (block.always_blocks_precipitation)
						{
							var4 = var5 + 1;
							break;
						}

						if (block.never_blocks_precipitation)
						{
							--var5;
						}
						else
						{
							if (block.blocksPrecipitation(this.getBlockMetadata(par1, var5, par2)))
							{
								var4 = var5 + 1;
								break;
							}

							--var5;
						}
					}
				}

				this.precipitationHeightMap[var3] = var4;
			}

			return var4;
		}
	}

	/**
	 * Gets the height to which rain/snow will fall. Calculates it if not already stored.
	 */
	public final int getPrecipitationHeight(int par1, int par2)
	{
		if (MITEConstant.useNewPrecipitationHeightDetermination())
		{
			return this.getPrecipitationHeightNew(par1, par2);
		}
		else
		{
			int var3 = par1 | par2 << 4;
			int var4 = this.precipitationHeightMap[var3];

			if (var4 == -999)
			{
				int var5 = this.getTopFilledSegment() + 15;
				var4 = -1;

				while (var5 > 0 && var4 == -1)
				{
					int var6 = this.getBlockID(par1, var5, par2);
					boolean var7;

					if (var6 == 0)
					{
						var7 = false;
					}
					else
					{
						Block var8 = Block.getBlock(var6);

						if (var8.isLiquid())
						{
							var7 = true;
						}
						else
						{
							var7 = var8.isAlwaysSolid() ? true : (var8.isNeverSolid() ? false : var8.isSolid(this.getBlockMetadata(par1, var5, par2)));
						}
					}

					if (!var7)
					{
						--var5;
					}
					else
					{
						var4 = var5 + 1;
					}
				}

				this.precipitationHeightMap[var3] = var4;
			}

			return var4;
		}
	}

	public final boolean updateSkylight(boolean trusted_xz_for_this_chunk_and_immediate_neighbors)
	{
		return this.isGapLightingUpdated && !this.worldObj.provider.hasNoSky ? this.updateSkylight_do(trusted_xz_for_this_chunk_and_immediate_neighbors) : false;
	}

	/**
	 * Gets a ChunkCoordIntPair representing the Chunk's position.
	 */
	public final ChunkCoordIntPair getChunkCoordIntPair()
	{
		return new ChunkCoordIntPair(this.xPosition, this.zPosition);
	}

	/**
	 * Returns whether the ExtendedBlockStorages containing levels (in blocks) from arg 1 to arg 2 are fully empty
	 * (true) or not (false).
	 */
	public final boolean getAreLevelsEmpty(int par1, int par2)
	{
		if (this.is_empty)
		{
			return true;
		}
		else
		{
			if (par1 < 0)
			{
				par1 = 0;
			}

			if (par2 >= 256)
			{
				par2 = 255;
			}

			for (int var3 = par1; var3 <= par2; var3 += 16)
			{
				ExtendedBlockStorage var4 = this.storageArrays[var3 >> 4];

				if (var4 != null && !var4.isEmpty())
				{
					return false;
				}
			}

			return true;
		}
	}

	public void setStorageArrays(ExtendedBlockStorage[] par1ArrayOfExtendedBlockStorage)
	{
		this.storageArrays = par1ArrayOfExtendedBlockStorage;
	}

	/**
	 * Initialise this chunk with new binary data
	 */
	public void fillChunk(byte[] par1ArrayOfByte, int par2, int par3, boolean par4)
	{
		Iterator iterator = chunkTileEntityMap.values().iterator();
		while(iterator.hasNext())
		{
			TileEntity tileEntity = (TileEntity)iterator.next();
			tileEntity.updateContainingBlockInfo();
			tileEntity.getBlockMetadata();
			tileEntity.getBlockType();
		}

		int var5 = 0;
		boolean var6 = !this.worldObj.provider.hasNoSky;
		int var7;

		for (var7 = 0; var7 < this.storageArrays.length; ++var7)
		{
			if ((par2 & 1 << var7) != 0)
			{
				if (this.storageArrays[var7] == null)
				{
					this.storageArrays[var7] = new ExtendedBlockStorage(var7 << 4, var6);
				}

				byte[] var8 = this.storageArrays[var7].getBlockLSBArray();
				System.arraycopy(par1ArrayOfByte, var5, var8, 0, var8.length);
				var5 += var8.length;
			}
			else if (par4 && this.storageArrays[var7] != null)
			{
				this.storageArrays[var7] = null;
			}
		}

		NibbleArray var11;

		for (var7 = 0; var7 < this.storageArrays.length; ++var7)
		{
			if ((par2 & 1 << var7) != 0 && this.storageArrays[var7] != null)
			{
				var11 = this.storageArrays[var7].getMetadataArray();
				System.arraycopy(par1ArrayOfByte, var5, var11.data, 0, var11.data.length);
				var5 += var11.data.length;
			}
		}

		for (var7 = 0; var7 < this.storageArrays.length; ++var7)
		{
			if ((par2 & 1 << var7) != 0 && this.storageArrays[var7] != null)
			{
				var11 = this.storageArrays[var7].getBlocklightArray();
				System.arraycopy(par1ArrayOfByte, var5, var11.data, 0, var11.data.length);
				var5 += var11.data.length;
			}
		}

		if (var6)
		{
			for (var7 = 0; var7 < this.storageArrays.length; ++var7)
			{
				if ((par2 & 1 << var7) != 0 && this.storageArrays[var7] != null)
				{
					var11 = this.storageArrays[var7].getSkylightArray();
					System.arraycopy(par1ArrayOfByte, var5, var11.data, 0, var11.data.length);
					var5 += var11.data.length;
				}
			}
		}

		for (var7 = 0; var7 < this.storageArrays.length; ++var7)
		{
			if ((par3 & 1 << var7) != 0)
			{
				if (this.storageArrays[var7] == null)
				{
					var5 += 2048;
				}
				else
				{
					var11 = this.storageArrays[var7].getBlockMSBArray();

					if (var11 == null)
					{
						var11 = this.storageArrays[var7].createBlockMSBArray();
					}

					System.arraycopy(par1ArrayOfByte, var5, var11.data, 0, var11.data.length);
					var5 += var11.data.length;
				}
			}
			else if (par4 && this.storageArrays[var7] != null && this.storageArrays[var7].getBlockMSBArray() != null)
			{
				this.storageArrays[var7].clearMSBArray();
			}
		}

		if (par4)
		{
			System.arraycopy(par1ArrayOfByte, var5, this.blockBiomeArray, 0, this.blockBiomeArray.length);
			var5 += this.blockBiomeArray.length;
		}

		if (this.hasSkylight())
		{
			for (int var9 = 0; var9 < this.skylight_bottom.length; ++var9)
			{
				this.skylight_bottom[var9] = par1ArrayOfByte[var5 + var9] & 255;
			}
		}

		for (var7 = 0; var7 < this.storageArrays.length; ++var7)
		{
			if (this.storageArrays[var7] != null && (par2 & 1 << var7) != 0)
			{
				this.storageArrays[var7].removeInvalidBlocks();
			}
		}

		this.generateHeightMap(true);

		List<TileEntity> invalidList = new ArrayList<TileEntity>();
		iterator = chunkTileEntityMap.values().iterator();

		while (iterator.hasNext())
		{
			TileEntity tileEntity = (TileEntity)iterator.next();
			int x = tileEntity.xCoord & 15;
			int y = tileEntity.yCoord;
			int z = tileEntity.zCoord & 15;
			Block block = tileEntity.getBlockType();
			if (block == null || block.blockID != getBlockID(x, y, z) || tileEntity.getBlockMetadata() != getBlockMetadata(x, y, z))
			{
				invalidList.add(tileEntity);
			}
			tileEntity.updateContainingBlockInfo();
		}

		for (TileEntity tileEntity : invalidList)
		{
			tileEntity.invalidate();
		}
	}

	/** FORGE: Used to remove only invalid TileEntities */
	public void cleanChunkBlockTileEntity(int x, int y, int z)
	{
		ChunkPosition position = new ChunkPosition(x, y, z);
		if (isChunkLoaded)
		{
			TileEntity entity = (TileEntity)chunkTileEntityMap.get(position);
			if (entity != null && entity.isInvalid())
			{
				chunkTileEntityMap.remove(position);
			}
		}
	}

	/** FORGE: backport TE false creation fix */
	/**
	 *
	 * Retrieves the tile entity, WITHOUT creating it. Good for checking if it
	 * exists.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return The tile entity at the specified location, if it exists and is
	 *         valid.
	 */
	public TileEntity getChunkBlockTileEntityUnsafe(int x, int y, int z)
	{
		ChunkPosition chunkposition = new ChunkPosition(x, y, z);
		TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(chunkposition);

		if (tileentity != null && tileentity.isInvalid())
		{
			chunkTileEntityMap.remove(chunkposition);
			tileentity = null;
		}

		return tileentity;
	}

	/**
	 * This method retrieves the biome at a set of coordinates
	 */
	public final BiomeGenBase getBiomeGenForWorldCoords(int par1, int par2, WorldChunkManager par3WorldChunkManager)
	{
		int var4 = this.blockBiomeArray[par2 << 4 | par1] & 255;

		if (var4 == 255)
		{
			BiomeGenBase var5 = par3WorldChunkManager.getBiomeGenAt((this.xPosition << 4) + par1, (this.zPosition << 4) + par2);
			var4 = var5.biomeID;
			this.blockBiomeArray[par2 << 4 | par1] = (byte)(var4 & 255);
		}

		return BiomeGenBase.biomeList[var4] == null ? BiomeGenBase.plains : BiomeGenBase.biomeList[var4];
	}

	/**
	 * Returns an array containing a 16x16 mapping on the X/Z of block positions in this Chunk to biome IDs.
	 */
	public final byte[] getBiomeArray()
	{
		return this.blockBiomeArray;
	}

	/**
	 * Accepts a 256-entry array that contains a 16x16 mapping on the X/Z plane of block positions in this Chunk to
	 * biome IDs.
	 */
	public final void setBiomeArray(byte[] par1ArrayOfByte)
	{
		this.blockBiomeArray = par1ArrayOfByte;
	}

	/**
	 * Resets the relight check index to 0 for this Chunk.
	 */
	public void resetRelightChecks()
	{
		this.queuedLightChecks = 0;
	}

	public boolean hasEntitiesForWritingToNBT()
	{
		for (int i = 0; i < 16; ++i)
		{
			List entity_list = this.entityLists[i];

			for (int j = 0; j < entity_list.size(); ++j)
			{
				Entity entity = (Entity)entity_list.get(j);

				if (entity.isWrittenToChunkNBT())
				{
					return true;
				}
			}
		}

		return false;
	}

	public boolean checkForEntityDuplicates(Entity var21)
	{
		int num_object_matches = 0;
		int num_UUID_matches = 0;
		boolean[] list_occupied = new boolean[16];
		int num_matches;
		int num_lists_occupied;

		for (num_matches = 0; num_matches < this.entityLists.length; ++num_matches)
		{
			for (num_lists_occupied = 0; num_lists_occupied < this.entityLists[num_matches].size(); ++num_lists_occupied)
			{
				Entity msg = (Entity)this.entityLists[num_matches].get(num_lists_occupied);

				if (msg == var21)
				{
					++num_object_matches;
					list_occupied[num_matches] = true;
				}
				else if (msg.getClass() == var21.getClass() && msg.getUniqueID().equals(var21.getUniqueID()))
				{
					++num_UUID_matches;
					list_occupied[num_matches] = true;
				}
			}
		}

		num_matches = num_object_matches + num_UUID_matches;

		if (num_object_matches == 1 && num_UUID_matches == 0)
		{
			return false;
		}
		else
		{
			num_lists_occupied = 0;

			for (int var8 = 0; var8 < 16; ++var8)
			{
				if (list_occupied[var8])
				{
					++num_lists_occupied;
				}
			}

			String var9 = "writeChunkToNBT: " + var21.getEntityName() + " duplicated in entityLists " + (num_matches - 1) + " time(s), " + num_object_matches + " object match(es) and " + num_UUID_matches + " UUID match(es), lists occupied:" + num_lists_occupied;

			if (Minecraft.inDevMode())
			{
				Minecraft.setErrorMessage(var9);
			}
			else
			{
				System.out.println(var9);
			}

			return true;
		}
	}

	private boolean doesEntityObjectExistInEntityList(Entity entity, int index)
	{
		List entity_list = this.entityLists[index];

		for (int i = 0; i < entity_list.size(); ++i)
		{
			if (entity_list.get(i) == entity)
			{
				return true;
			}
		}

		return false;
	}

	public boolean doesEntityObjectExistInEntityLists(Entity entity)
	{
		for (int i = 0; i < 16; ++i)
		{
			if (this.doesEntityObjectExistInEntityList(entity, i))
			{
				return true;
			}
		}

		return false;
	}

	private boolean doesEntityWithMatchingClassAndUUIDExistInEntityList(Entity entity, int index)
	{
		if (this.worldObj.isRemote)
		{
			Minecraft.setErrorMessage("doesEntityWithMatchingClassAndUUIDExistInEntityList: Why calling this on client?");
		}

		if (!entity.isExpectedToHaveUUID())
		{
			Minecraft.setErrorMessage("doesEntityWithMatchingClassAndUUIDExistInEntityList: entity is not expected to have UUID " + entity);
		}

		List entity_list = this.entityLists[index];

		for (int i = 0; i < entity_list.size(); ++i)
		{
			Entity entity_in_list = (Entity)entity_list.get(i);

			if (entity_in_list == entity)
			{
				return true;
			}

			if (entity_in_list.isExpectedToHaveUUID() && entity_in_list.getClass() == entity.getClass() && entity_in_list.getUniqueID().equals(entity.getUniqueID()))
			{
				return true;
			}
		}

		return false;
	}

	public boolean doesEntityWithMatchingClassAndUUIDExistInEntityLists(Entity entity)
	{
		for (int i = 0; i < 16; ++i)
		{
			if (this.doesEntityWithMatchingClassAndUUIDExistInEntityList(entity, i))
			{
				return true;
			}
		}

		return false;
	}

	public List[] getEntityListsForReadingOnly()
	{
		return this.entityLists;
	}

	public int getMinBlockX()
	{
		return this.xPosition * 16;
	}

	public int getMinBlockZ()
	{
		return this.zPosition * 16;
	}

	public int getMaxBlockX()
	{
		return this.getMinBlockX() + 15;
	}

	public int getMaxBlockZ()
	{
		return this.getMinBlockZ() + 15;
	}

	public static int getChunkCoordFromBlockCoord(int block_coord)
	{
		return block_coord >> 4;
	}

	public static int getChunkCoordFromDouble(double pos)
	{
		return MathHelper.floor_double(pos) >> 4;
	}

	public final boolean doAllNeighborsExist(int range, boolean check_this_chunk, boolean include_empty_chunks)
	{
		for (int dx = -range; dx <= range; ++dx)
		{
			for (int dz = -range; dz <= range; ++dz)
			{
				if (check_this_chunk || dx != 0 || dz != 0)
				{
					int chunk_x = this.xPosition + dx;
					int chunk_z = this.zPosition + dz;

					if (!this.worldObj.chunkExists(chunk_x, chunk_z))
					{
						return false;
					}

					if (!include_empty_chunks && this.worldObj.getChunkFromChunkCoords(chunk_x, chunk_z).isEmpty())
					{
						return false;
					}
				}
			}
		}

		return true;
	}

	public void addPendingSkylightUpdate(int x, int y, int z)
	{
		if (this.num_pending_skylight_updates == this.max_num_pending_skylight_updates)
		{
			this.max_num_pending_skylight_updates *= 2;
			byte[] local_x = new byte[this.max_num_pending_skylight_updates * 2];
			System.arraycopy(this.pending_skylight_update_coords, 0, local_x, 0, this.pending_skylight_update_coords.length);
			this.pending_skylight_update_coords = local_x;
		}

		int local_x1 = x & 15;
		int local_z = z & 15;
		int index = local_x1 + local_z * 16 + y * 256;

		if (!this.pending_skylight_updates[index])
		{
			this.pending_skylight_updates[index] = true;
			int offset = this.num_pending_skylight_updates * 2;
			this.pending_skylight_update_coords[offset] = (byte)(local_x1 | local_z << 4);
			this.pending_skylight_update_coords[offset + 1] = (byte)y;
			++this.num_pending_skylight_updates;
		}
	}

	public boolean performPendingSkylightUpdatesIfPossible()
	{
		if (this.num_pending_skylight_updates >= 1 && this.worldObj.canUpdateLightByType(this.xPosition * 16, this.zPosition * 16))
		{
			int num_pending_skylight_updates_to_perform = this.worldObj.isRemote ? Math.min(this.num_pending_skylight_updates, 64) : this.num_pending_skylight_updates;
			int num_updates_performed = 0;
			int num_bytes_processed;
			int i;

			for (this.num_pending_skylight_updates = -this.num_pending_skylight_updates; num_updates_performed < num_pending_skylight_updates_to_perform; ++num_updates_performed)
			{
				num_bytes_processed = num_updates_performed * 2;
				byte num_bytes_remaining = this.pending_skylight_update_coords[num_bytes_processed];
				i = num_bytes_remaining & 15;
				int local_z = num_bytes_remaining >> 4 & 15;
				int y = this.pending_skylight_update_coords[num_bytes_processed + 1] & 255;
				int index = i + local_z * 16 + y * 256;
				this.worldObj.updateLightByType(EnumSkyBlock.Sky, this.getNonLocalX(i), y, this.getNonLocalZ(local_z), true, this);
				this.pending_skylight_updates[index] = false;
			}

			if (num_updates_performed > 63)
			{
				this.setChunkModified();
			}

			this.num_pending_skylight_updates = -this.num_pending_skylight_updates - num_updates_performed;

			if (this.num_pending_skylight_updates == 0)
			{
				if (this.max_num_pending_skylight_updates > 256)
				{
					this.max_num_pending_skylight_updates = 256;
					this.pending_skylight_update_coords = new byte[this.max_num_pending_skylight_updates * 2];
				}
			}
			else
			{
				num_bytes_processed = num_updates_performed * 2;
				int var9 = this.num_pending_skylight_updates * 2;

				for (i = 0; i < var9; ++i)
				{
					this.pending_skylight_update_coords[i] = this.pending_skylight_update_coords[i + num_bytes_processed];
				}
			}

			return true;
		}
		else
		{
			return false;
		}
	}

	public void addPendingBlocklightUpdate(int x, int y, int z)
	{
		if (this.num_pending_blocklight_updates == this.max_num_pending_blocklight_updates)
		{
			this.max_num_pending_blocklight_updates *= 2;
			byte[] local_x = new byte[this.max_num_pending_blocklight_updates * 2];
			System.arraycopy(this.pending_blocklight_update_coords, 0, local_x, 0, this.pending_blocklight_update_coords.length);
			this.pending_blocklight_update_coords = local_x;
		}

		int local_x1 = x & 15;
		int local_z = z & 15;
		int index = local_x1 + local_z * 16 + y * 256;

		if (!this.pending_blocklight_updates[index])
		{
			this.pending_blocklight_updates[index] = true;
			int offset = this.num_pending_blocklight_updates * 2;
			this.pending_blocklight_update_coords[offset] = (byte)(local_x1 | local_z << 4);
			this.pending_blocklight_update_coords[offset + 1] = (byte)y;
			++this.num_pending_blocklight_updates;
		}
	}

	public boolean performPendingBlocklightUpdatesIfPossible()
	{
		if (this.num_pending_blocklight_updates >= 1 && this.worldObj.canUpdateLightByType(this.xPosition * 16, this.zPosition * 16))
		{
			int num_pending_blocklight_updates_to_perform = this.worldObj.isRemote ? Math.min(this.num_pending_blocklight_updates, 64) : this.num_pending_blocklight_updates;
			int num_updates_performed = 0;
			int num_bytes_processed;
			int i;

			for (this.num_pending_blocklight_updates = -this.num_pending_blocklight_updates; num_updates_performed < num_pending_blocklight_updates_to_perform; ++num_updates_performed)
			{
				num_bytes_processed = num_updates_performed * 2;
				byte num_bytes_remaining = this.pending_blocklight_update_coords[num_bytes_processed];
				i = num_bytes_remaining & 15;
				int local_z = num_bytes_remaining >> 4 & 15;
				int y = this.pending_blocklight_update_coords[num_bytes_processed + 1] & 255;
				int index = i + local_z * 16 + y * 256;
				this.worldObj.updateLightByType(EnumSkyBlock.Block, this.getNonLocalX(i), y, this.getNonLocalZ(local_z), true, this);
				this.pending_blocklight_updates[index] = false;
			}

			if (num_updates_performed > 63)
			{
				this.setChunkModified();
			}

			this.num_pending_blocklight_updates = -this.num_pending_blocklight_updates - num_updates_performed;

			if (this.num_pending_blocklight_updates == 0)
			{
				if (this.max_num_pending_blocklight_updates > 256)
				{
					this.max_num_pending_blocklight_updates = 256;
					this.pending_blocklight_update_coords = new byte[this.max_num_pending_blocklight_updates * 2];
				}
			}
			else
			{
				num_bytes_processed = num_updates_performed * 2;
				int var9 = this.num_pending_blocklight_updates * 2;

				for (i = 0; i < var9; ++i)
				{
					this.pending_blocklight_update_coords[i] = this.pending_blocklight_update_coords[i + num_bytes_processed];
				}
			}

			return true;
		}
		else
		{
			return false;
		}
	}

	public void loadNeighboringChunks(int radius)
	{
		for (int dx = -radius; dx <= radius; ++dx)
		{
			for (int dz = -radius; dz <= radius; ++dz)
			{
				if (dx != 0 || dz != 0)
				{
					Chunk neighbor = this.worldObj.getChunkFromChunkCoords(this.xPosition + dx, this.zPosition + dz);

					if (neighbor.isEmpty())
					{
						Debug.setErrorMessage("checkLighting: chunk is empty?");
					}
				}
			}
		}
	}

	public Chunk getNeighboringChunk(int dx, int dz)
	{
		return dx == 0 && dz == 0 ? this : this.worldObj.getChunkFromChunkCoords(this.xPosition + dx, this.zPosition + dz);
	}

	public Chunk getNeighboringChunkIfItExists(int dx, int dz)
	{
		return dx == 0 && dz == 0 ? this : this.worldObj.getChunkIfItExists(this.xPosition + dx, this.zPosition + dz);
	}

	public int recalculateSkylightBottom(int index, int lowest_y)
	{
		int ebs_index = lowest_y >> 4;
		int y;

		for (y = ebs_index; y < this.storageArrays.length; ++y)
		{
			if (this.storageArrays[y] != null)
			{
				lowest_y = y * 16 + (lowest_y & 15);
				break;
			}

			if (y == this.storageArrays.length - 1)
			{
				return lowest_y;
			}
		}

		for (y = lowest_y; y < skylight_bottom_initial_value; ++y)
		{
			ExtendedBlockStorage ebs = this.storageArrays[y >> 4];

			if (ebs != null && ebs.getExtSkylightValue(index, y & 15) > 0)
			{
				return y;
			}
		}

		return skylight_bottom_initial_value;
	}

	public final void recalculateSkylightBottoms()
	{
		if (!this.isEmpty())
		{
			for (int local_x = 0; local_x < 16; ++local_x)
			{
				for (int local_z = 0; local_z < 16; ++local_z)
				{
					int index = local_x + local_z * 16;
					this.skylight_bottom[index] = this.recalculateSkylightBottom(index, 0);
				}
			}
		}
	}

	public final boolean hasSkylight()
	{
		return this.has_skylight;
	}

	public final boolean hasCoords(int chunk_x, int chunk_z)
	{
		return chunk_x == this.xPosition && chunk_z == this.zPosition;
	}

	public final boolean isWithinBlockDomain()
	{
		if (this.is_empty)
		{
			Minecraft.setErrorMessage("isWithinBlockDomain: chunk is empty so it will always return true");
		}

		return this.is_within_block_domain;
	}

	public static int getChunkCoordsHash(int chunk_x, int chunk_z)
	{
		byte hash = 17;
		int hash1 = hash * 31 + chunk_x;
		hash1 = hash1 * 31 + chunk_z;
		return hash1;
	}

	public void setHadNaturallyOccurringMycelium()
	{
		this.had_naturally_occurring_mycelium = true;
	}

	public boolean getHadNaturallyOccurringMycelium()
	{
		return this.had_naturally_occurring_mycelium;
	}

	public boolean performPendingSandFallsIfPossible()
	{
		if (this.pending_sand_falls != null && this.doAllNeighborsExist(1, false, false))
		{
			Iterator i = this.pending_sand_falls.entrySet().iterator();

			while (i.hasNext())
			{
				Map.Entry entry = (Map.Entry)i.next();
				int xz_index = ((Integer)entry.getKey()).intValue();
				int y = ((Integer)entry.getValue()).intValue();

				if (this.getBlockIDOptimized(xz_index, y) == sand_block_id)
				{
					int local_x = xz_index % 16;
					int local_z = xz_index / 16;
					int x = this.getNonLocalX(local_x);
					int z = this.getNonLocalZ(local_z);
					int num_sand_blocks;

					for (num_sand_blocks = 1; this.getBlockIDOptimized(xz_index, y + num_sand_blocks) == sand_block_id; ++num_sand_blocks)
					{
						;
					}

					int max_y = y + num_sand_blocks - 1;
					int above_y = max_y;
					boolean dead_bush = false;
					int dead_bush_y;

					while (true)
					{
						++above_y;
						dead_bush_y = this.getBlockIDOptimized(xz_index, above_y);

						if (dead_bush_y == 0)
						{
							break;
						}

						Block block = Block.getBlock(dead_bush_y);

						if (block.isAlwaysLegal() || block.isLegalOn(this.getBlockMetadata(local_x, above_y, local_z), (Block)null, 0))
						{
							break;
						}

						this.setBlockIDWithMetadata(local_x, above_y, local_z, 0, 0, dead_bush_y);
						this.worldObj.markBlockForUpdate(x, above_y, z);

						if (block == Block.deadBush)
						{
							dead_bush = true;
							break;
						}
					}

					while (this.getBlockIDOptimized(xz_index, y - 1) == 0)
					{
						--y;
					}

					dead_bush_y = dead_bush ? y + num_sand_blocks : 0;
					--y;

					while (true)
					{
						++y;

						if (y > max_y)
						{
							if (dead_bush)
							{
								this.worldObj.setBlock(x, dead_bush_y, z, Block.deadBush.blockID);
							}

							break;
						}

						--num_sand_blocks;
						this.worldObj.setBlock(x, y, z, num_sand_blocks < 0 ? 0 : sand_block_id);
					}
				}
			}

			this.pending_sand_falls = null;
			return true;
		}
		else
		{
			return false;
		}
	}

	public String toString()
	{
		return "Chunk [" + this.xPosition + "," + this.zPosition + "] (block range " + this.getMinBlockX() + "," + this.getMinBlockZ() + " to " + this.getMaxBlockX() + "," + this.getMaxBlockZ() + ") dimension " + this.worldObj.getDimensionId() + " on " + this.worldObj.getClientOrServerString();
	}
}
