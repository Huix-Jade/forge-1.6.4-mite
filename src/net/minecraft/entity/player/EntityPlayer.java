package net.minecraft.entity.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.block.BitHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.main.Main;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityOwnable;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.*;
import net.minecraft.item.crafting.CraftingResult;
import net.minecraft.mite.Skill;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.MemoryConnection;
import net.minecraft.network.RightClickFilter;
import net.minecraft.network.SignalData;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.network.packet.Packet90BroadcastToAssociatedPlayers;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.raycast.RaycastPolicies;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Curse;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumConsciousState;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumEntityReachContext;
import net.minecraft.util.EnumInsulinResistanceLevel;
import net.minecraft.util.EnumItemInUseAction;
import net.minecraft.util.EnumLevelBonus;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumQuality;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.EnumTournamentType;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringHelper;
import net.minecraft.util.TentativeBoundingBox;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public abstract class EntityPlayer extends EntityLivingBase implements ICommandSender {

   public static final String PERSISTED_NBT_TAG = "PlayerPersisted";
   private HashMap<Integer, ChunkCoordinates> spawnChunkMap = new HashMap<Integer, ChunkCoordinates>();
   private HashMap<Integer, Boolean> spawnForcedMap = new HashMap<Integer, Boolean>();


   public InventoryPlayer inventory = new InventoryPlayer(this);
   private InventoryEnderChest theInventoryEnderChest = new InventoryEnderChest();
   public Container inventoryContainer;
   public Container openContainer;
   public FoodStats foodStats;
   protected int flyToggleTimer;
   public float prevCameraYaw;
   public float cameraYaw;
   public final String username;
   public int xpCooldown;
   public double field_71091_bM;
   public double field_71096_bN;
   public double field_71097_bO;
   public double field_71094_bP;
   public double field_71095_bQ;
   public double field_71085_bR;
   public ChunkCoordinates bed_location;
   public int bed_direction;
   public EnumConsciousState conscious_state;
   public float field_71079_bU;
   public float field_71082_cx;
   public float field_71089_bV;
   private ChunkCoordinates spawnChunk;
   private boolean spawnForced;
   private ChunkCoordinates startMinecartRidingCoordinate;
   public PlayerCapabilities capabilities;
   public int experience;
   public ItemStack itemInUse;
   public int itemInUseCount;
   protected float speedOnGround;
   protected float speedInAir;
   private int field_82249_h;
   public EntityFishHook fishEntity;
   public float vision_dimming;
   public int countdown_to_mark_all_nearby_chunks_for_render_update;
   public static float y_offset_on_client_and_eye_height_on_server = 1.62F;
   private boolean has_dedicated_server_ghost_check_been_made;
   private boolean is_dedicated_server_ghost;
   public boolean is_tournament_winner;
   public ItemArrow nocked_arrow;
   public boolean is_runegate_teleporting;
   public boolean prevent_runegate_achievement;
   public boolean is_cursed;
   public int curse_id;
   public boolean curse_effect_known;
   public boolean collided_with_gelatinous_cube;
   public boolean in_test_mode;
   private int times_renderUpdateMethod2_called;
   public boolean suppress_next_arm_swing;
   public boolean suppress_next_stat_increment;
   private boolean cancel_right_click;
   private boolean zevimrgv_check_made;
   private boolean is_zevimrgv;
   public List tentative_bounding_boxes;
   public double pos_x_before_bed;
   public double pos_y_before_bed;
   public double pos_z_before_bed;
   public int data_object_id_skills;
   public HashMap stats;
   public long block_placement_tick;
   public double block_placement_pos_x;
   public double block_placement_pos_y;
   public double block_placement_pos_z;
   public World block_placement_world;
   private int insulin_resistance;
   public EnumInsulinResistanceLevel insulin_resistance_level;
   private static final int[] experience_for_level = new int[getHighestPossibleLevel() + 1];

   public EntityPlayer(World par1World, String par2Str) {
      super(par1World);
      this.conscious_state = EnumConsciousState.fully_awake;
      this.capabilities = new PlayerCapabilities(this);
      this.speedOnGround = 0.1F;
      this.speedInAir = 0.02F;
      this.countdown_to_mark_all_nearby_chunks_for_render_update = 20;
      this.tentative_bounding_boxes = new ArrayList();
      this.stats = new HashMap();
      this.block_placement_tick = -1L;
      this.username = par2Str;
      this.inventoryContainer = new ContainerPlayer(this);
      this.openContainer = this.inventoryContainer;
      this.yOffset = y_offset_on_client_and_eye_height_on_server;
      ChunkCoordinates var3 = par1World.getSpawnPoint();
      this.setLocationAndAngles((double)var3.posX + 0.5, (double)(var3.posY + 1), (double)var3.posZ + 0.5, 0.0F, 0.0F);
      this.foodStats = new FoodStats(this);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 1.0);
   }

   public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {
      FMLNetworkHandler.openGui(this, mod, modGuiId, world, x, y, z);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
      this.dataWatcher.addObject(17, 0.0F);
      this.dataWatcher.addObject(18, 0);
      this.data_object_id_skills = this.dataWatcher.addObject(this.dataWatcher.getNextAvailableId(), 0);
   }

   public void setSkills(int skills) {
      this.dataWatcher.updateObject(this.data_object_id_skills, skills);
   }

   public void addSkill(Skill skill) {
      this.setSkills(this.getSkills() | skill.id);
   }

   public void removeSkill(Skill skill) {
      this.setSkills(BitHelper.clearBit(this.getSkills(), skill.id));
   }

   public int getSkills() {
      return this.dataWatcher.getWatchableObjectInt(this.data_object_id_skills);
   }

   public boolean hasSkill(Skill skill) {
      if (!this.worldObj.areSkillsEnabled()) {
         Minecraft.setErrorMessage("hasSkill: skills aren't enabled");
         (new Exception()).printStackTrace();
      }

      return Skill.skillExistsIn(skill, this.getSkills());
   }

   public boolean hasAnyOfTheseSkills(int skills) {
      if (!this.worldObj.areSkillsEnabled()) {
         Minecraft.setErrorMessage("hasAnyOfTheseSkills: skills aren't enabled");
         (new Exception()).printStackTrace();
      }

      return BitHelper.isAnyBitSet(this.getSkills(), skills);
   }

   public boolean hasAllOfTheseSkills(int skills) {
      if (!this.worldObj.areSkillsEnabled()) {
         Minecraft.setErrorMessage("hasAllOfTheseSkills: skills aren't enabled");
         (new Exception()).printStackTrace();
      }

      return BitHelper.isBitSet(this.getSkills(), skills);
   }

   public boolean hasAnyOfTheseSkillsets(int[] skillsets) {
      if (!this.worldObj.areSkillsEnabled()) {
         Minecraft.setErrorMessage("hasAnyOfTheseSkillsets: skills aren't enabled");
         (new Exception()).printStackTrace();
      }

      if (skillsets == null) {
         return true;
      } else {
         for(int i = 0; i < skillsets.length; ++i) {
            if (this.hasAllOfTheseSkills(skillsets[i])) {
               return true;
            }
         }

         return false;
      }
   }

   public ItemStack getItemInUse() {
      return this.itemInUse;
   }

   public int getItemInUseCount() {
      return this.itemInUseCount;
   }

   public boolean isUsingItem() {
      return this.itemInUse != null;
   }

   public int getItemInUseDuration() {
      return this.isUsingItem() ? this.itemInUse.getMaxItemUseDuration() - this.itemInUseCount : 0;
   }

   public void stopUsingItem() {
      this.stopUsingItem(this.onClient());
   }

   public void stopUsingItem(boolean inform_server) {
      if (this.itemInUse != null) {
         this.itemInUse.onPlayerStoppedUsing(this.worldObj, this, this.itemInUseCount);
      }

      this.clearItemInUse();
   }

   public void clearItemInUse() {
      this.itemInUse = null;
      this.itemInUseCount = 0;
      this.setEating(false);
      if (this.onServer()) {
         this.sendPacketToAssociatedPlayers((new Packet85SimpleSignal(EnumSignal.item_in_use)).setInteger(0).setEntityID(this), false);
      }

   }

   public boolean isBlocking() {
      return this.isUsingItem() && Item.itemsList[this.itemInUse.itemID].getItemInUseAction(this.itemInUse, this) == EnumItemInUseAction.BLOCK;
   }

   private void checkForArmorAchievements() {
      boolean wearing_leather = false;
      boolean wearing_full_suit_plate = true;
      boolean wearing_full_suit_adamantium_plate = true;

      for(int i = 0; i < 4; ++i) {
         if (this.inventory.armorInventory[i] != null && this.inventory.armorInventory[i].getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)this.inventory.armorInventory[i].getItem();
            Material material = armor.getArmorMaterial();
            if (material == Material.leather) {
               wearing_leather = true;
            }

            if (material != Material.copper && material != Material.silver && material != Material.gold && material != Material.iron && material != Material.mithril && material != Material.adamantium && material != Material.ancient_metal) {
               wearing_full_suit_plate = false;
            }

            if (material != Material.adamantium) {
               wearing_full_suit_adamantium_plate = false;
            }
         } else {
            wearing_full_suit_plate = false;
            wearing_full_suit_adamantium_plate = false;
         }
      }

      if (wearing_leather) {
         this.triggerAchievement(AchievementList.wearLeather);
      }

      if (wearing_full_suit_plate) {
         this.triggerAchievement(AchievementList.wearAllPlateArmor);
      }

      if (wearing_full_suit_adamantium_plate) {
         this.triggerAchievement(AchievementList.wearAllAdamantiumPlateArmor);
      }

   }

   public void markAllNearbyChunksForRenderUpdate(boolean report_issues) {
      int radius = 5;
      int chunk_currently_in_section_index = this.getChunkCurrentlyInSectionIndex();
      int chunk_currently_in_pos_x = this.getChunkPosX();
      int chunk_currently_in_pos_z = this.getChunkPosZ();

      for(int chunk_dy = -radius; chunk_dy <= radius; ++chunk_dy) {
         int chunk_y = chunk_currently_in_section_index + chunk_dy;
         int y = chunk_y * 16;
         if (y >= 0 || y <= 255) {
            for(int chunk_dx = -radius; chunk_dx <= radius; ++chunk_dx) {
               int chunk_x = chunk_currently_in_pos_x + chunk_dx;
               int x = chunk_x * 16;

               for(int chunk_dz = -radius; chunk_dz <= radius; ++chunk_dz) {
                  int chunk_z = chunk_currently_in_pos_z + chunk_dz;
                  int z = chunk_z * 16;
                  if (report_issues && !this.worldObj.chunkExists(chunk_x, chunk_z)) {
                     Minecraft.theMinecraft.thePlayer.receiveChatMessage("Chunk does not exist on client at " + x + ", " + z);
                  }

                  this.worldObj.markBlockForRenderUpdate(x, y, z);
               }
            }
         }
      }

   }

   private void renderUpdateMethod2() {
      if (!Main.disable_render_update_method_2 && !this.isGhost()) {
         int shell_radius = 6;
         int shell_size = shell_radius * 2 + 1;
         int shell_posts = shell_size * 4 - 4;
         int post_index = this.times_renderUpdateMethod2_called++ % shell_posts;
         int chunk_dx;
         int chunk_dz;
         if (post_index < shell_size) {
            chunk_dx = post_index - shell_radius;
            chunk_dz = -shell_radius;
         } else {
            post_index -= shell_size;
            if (post_index < shell_size) {
               chunk_dx = post_index - shell_radius;
               chunk_dz = shell_radius;
            } else {
               post_index -= shell_size;
               if (post_index < shell_size - 2) {
                  chunk_dx = -shell_radius;
                  chunk_dz = -shell_radius + 1 + post_index;
               } else {
                  post_index -= shell_size - 2;
                  chunk_dx = shell_radius;
                  chunk_dz = -shell_radius + 1 + post_index;
               }
            }
         }

         int chunk_x = this.getChunkPosX() + chunk_dx;
         int chunk_z = this.getChunkPosZ() + chunk_dz;
         int x = chunk_x * 16;
         int z = chunk_z * 16;

         for(int chunk_y = this.chunk_added_to_section_index - 2; chunk_y <= this.chunk_added_to_section_index + 2; ++chunk_y) {
            int y = chunk_y * 16;
            if (y >= 0 && y <= 255) {
               this.worldObj.markBlockForRenderUpdate(x, y, z);
            }
         }

      }
   }

   public void checkBoundingBoxAgainstSolidBlocks() {
      if (!(this instanceof EntityOtherPlayerMP)) {
         List collisions = this.getCollidingBlockBounds();
         if (!collisions.isEmpty()) {
            Minecraft.setErrorMessage("Player BB inside of solid block on " + (this.onClient() ? "client" : "server"));
         }

      }
   }

   public void onUpdate() {
      if (this.onClient() && Minecraft.java_version_is_outdated) {
         Minecraft.theMinecraft.thePlayer = null;
         Minecraft.theMinecraft = null;
      }

      if (!(this instanceof EntityOtherPlayerMP)) {
         this.openContainer.onUpdate();
      }

      int x = this.getBlockPosX();
      int y = this.getFootBlockPosY();
      int z = this.getBlockPosZ();
      if (Minecraft.inDevMode()) {
         this.checkBoundingBoxAgainstSolidBlocks();
      }

      if (!this.worldObj.isRemote) {
         this.checkForArmorAchievements();
         this.worldObj.checkPendingEntitySpawns();
         if (Minecraft.inDevMode()) {
            BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(this.getBlockPosX(), this.getBlockPosZ());
            Debug.biome_info = biome.biomeName + ", temp=" + biome.temperature + ", high humidity=" + biome.isHighHumidity();
         }
      } else if (this.countdown_to_mark_all_nearby_chunks_for_render_update > 0 && --this.countdown_to_mark_all_nearby_chunks_for_render_update == 0) {
         this.markAllNearbyChunksForRenderUpdate(false);
      }

      if (this.tentative_bounding_boxes.size() > 0) {
         Iterator i = this.tentative_bounding_boxes.iterator();

         while(i.hasNext()) {
            TentativeBoundingBox tbb = (TentativeBoundingBox)i.next();
            if (--tbb.countdown_for_clearing <= 0) {
               i.remove();
            }
         }
      }

      if (this.itemInUse != null) {
         ItemStack var1 = this.inventory.getCurrentItemStack();
         if (var1 == this.itemInUse) {
            if (this.itemInUseCount <= 25 && this.itemInUseCount % 4 == 0) {
               itemInUse.getItem().onUsingItemTick(itemInUse, this, itemInUseCount);
               this.updateItemUse(var1, 5);
            }

            if (--this.itemInUseCount == 0) {
               this.onItemUseFinish();
            } else if (this.isLocalClient()) {
               if (var1.getMaxItemUseDuration() - this.itemInUseCount == 1) {
                  this.sendPacket(new Packet85SimpleSignal(EnumSignal.confirm_or_cancel_item_in_use));
               }

               if ((var1.getItemInUseAction(this) == EnumItemInUseAction.EAT || var1.getItemInUseAction(this) == EnumItemInUseAction.DRINK) && !this.canIngest(var1)) {
                  this.stopUsingItem();
               }
            }
         } else {
            this.clearItemInUse();
         }
      }

      if (this.xpCooldown > 0) {
         --this.xpCooldown;
      }

      if (!this.worldObj.isRemote && this.bed_location != null) {
         if (!this.inBed()) {
            this.wakeUpPlayer(true, (Entity)null);
         } else if (this.getHostileEntityNearBed() != null) {
            if (this.isSleeping()) {
               this.addChatMessage("tile.bed.wakeMobs");
            } else {
               this.addChatMessage("tile.bed.notSafe");
            }

            this.wakeUpPlayer(true, this.getHostileEntityNearBed());
         } else if (this.isHostileEntityDiggingNearBed(this.bed_location.posX, this.bed_location.posY, this.bed_location.posZ)) {
            if (this.isSleeping()) {
               this.addChatMessage("tile.bed.wakeMobs");
            } else {
               this.addChatMessage("tile.bed.mobsDigging");
            }

            this.wakeUpPlayer(true, (Entity)null);
         } else if (this.isStarving()) {
            if (this.isSleeping()) {
               this.addChatMessage("tile.bed.wakeHungry");
            } else {
               this.addChatMessage("tile.bed.tooHungry");
            }

            this.wakeUpPlayer(true, (Entity)null);
         }
      }

      super.onUpdate();
      if (!this.worldObj.isRemote && this.openContainer != null && !ForgeHooks.canInteractWith(this, this.openContainer))
      {
         this.closeScreen();
         this.openContainer = this.inventoryContainer;
      }

      if (this.isBurning() && this.capabilities.disableDamage) {
         this.extinguish();
      }

      this.field_71091_bM = this.field_71094_bP;
      this.field_71096_bN = this.field_71095_bQ;
      this.field_71097_bO = this.field_71085_bR;
      double var9 = this.posX - this.field_71094_bP;
      double var3 = this.posY - this.field_71095_bQ;
      double var5 = this.posZ - this.field_71085_bR;
      double var7 = 10.0;
      if (var9 > var7) {
         this.field_71091_bM = this.field_71094_bP = this.posX;
      }

      if (var5 > var7) {
         this.field_71097_bO = this.field_71085_bR = this.posZ;
      }

      if (var3 > var7) {
         this.field_71096_bN = this.field_71095_bQ = this.posY;
      }

      if (var9 < -var7) {
         this.field_71091_bM = this.field_71094_bP = this.posX;
      }

      if (var5 < -var7) {
         this.field_71097_bO = this.field_71085_bR = this.posZ;
      }

      if (var3 < -var7) {
         this.field_71096_bN = this.field_71095_bQ = this.posY;
      }

      this.field_71094_bP += var9 * 0.25;
      this.field_71085_bR += var5 * 0.25;
      this.field_71095_bQ += var3 * 0.25;
      this.addStat(StatList.minutesPlayedStat, 1);
      if (this.ridingEntity == null) {
         this.startMinecartRidingCoordinate = null;
      }

   }

   public int getMaxInPortalTime() {
      return this.capabilities.disableDamage ? 0 : 80;
   }

   public int getPortalCooldown() {
      return 10;
   }

   public void playSound(String par1Str, float par2, float par3) {
      if (!this.isZevimrgvInTournament()) {
         this.worldObj.playSoundToNearExcept(this, par1Str, par2, par3);
      }
   }

   protected void updateItemUse(ItemStack par1ItemStack, int par2) {
      if (par1ItemStack.getItemInUseAction(this) == EnumItemInUseAction.DRINK) {
         this.playSound("random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (par1ItemStack.getItemInUseAction(this) == EnumItemInUseAction.EAT) {
         for(int var3 = 0; var3 < par2; ++var3) {
            Vec3 var4 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            var4.rotateAroundX(-this.rotationPitch * 3.1415927F / 180.0F);
            var4.rotateAroundY(-this.rotationYaw * 3.1415927F / 180.0F);
            Vec3 var5 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5) * 0.3, (double)(-this.rand.nextFloat()) * 0.6 - 0.3, 0.6);
            var5.rotateAroundX(-this.rotationPitch * 3.1415927F / 180.0F);
            var5.rotateAroundY(-this.rotationYaw * 3.1415927F / 180.0F);
            var5 = var5.addVector(this.posX, this.getEyePosY(), this.posZ);
            double var10008 = var4.yCoord + 0.05;
            this.worldObj.spawnParticleEx(EnumParticle.iconcrack, par1ItemStack.getItem().itemID, 0, var5.xCoord, var5.yCoord, var5.zCoord, var4.xCoord, var10008, var4.zCoord);
         }

         this.playSound("random.eat", 0.5F + 0.5F * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
      }

   }

   protected void onItemUseFinish() {
      if (this.itemInUse != null) {
         this.updateItemUse(this.itemInUse, 16);
         this.itemInUse.onItemUseFinish(this.worldObj, this);
         this.addStat(StatList.objectUseStats[this.itemInUse.itemID], 1);
         this.clearItemInUse();
      }

   }

   protected boolean isMovementBlocked() {
      return this.getHealth() <= 0.0F || this.inBed();
   }

   protected void closeScreen() {
      this.openContainer = this.inventoryContainer;
   }

   public void mountEntity(Entity par1Entity) {
      if (this.ridingEntity != null && par1Entity == null) {
         if (!this.worldObj.isRemote) {
            this.dismountEntity(this.ridingEntity);
         }

         if (this.ridingEntity != null) {
            this.ridingEntity.riddenByEntity = null;
         }

         this.ridingEntity = null;
      } else {
         super.mountEntity(par1Entity);
      }

   }

   public void updateRidden() {
      if (!this.worldObj.isRemote && this.isSneaking()) {
         this.mountEntity((Entity)null);
         this.setSneaking(false);
      } else {
         double var1 = this.posX;
         double var3 = this.posY;
         double var5 = this.posZ;
         float var7 = this.rotationYaw;
         float var8 = this.rotationPitch;
         super.updateRidden();
         this.prevCameraYaw = this.cameraYaw;
         this.cameraYaw = 0.0F;
         this.addMountedMovementStat(this.posX - var1, this.posY - var3, this.posZ - var5);
         if (this.ridingEntity instanceof EntityLivingBase && ((EntityLivingBase)ridingEntity).shouldRiderFaceForward(this))
         {
            this.rotationPitch = var8;
            this.rotationYaw = var7;
            this.renderYawOffset = ((EntityLivingBase)this.ridingEntity).renderYawOffset;
         }
      }

   }

   public static final int getHighestPossibleLevel() {
      return 200;
   }

   public static final int getExperienceRequired(int level) {
      return level < 0 ? level * 20 : (level > getHighestPossibleLevel() ? Integer.MAX_VALUE : experience_for_level[level]);
   }

   public final int getExperienceLevel(int experience) {
      if (experience < 0 && this.inCreativeMode()) {
         return 0;
      } else if (experience < 0) {
         return Math.max(-((-experience - 1) / 20 + 1), -40);
      } else {
         int level = 0;

         do {
            ++level;
         } while(getExperienceRequired(level) <= experience);

         return level - 1;
      }
   }

   public final int getExperienceLevel() {
      return this.getExperienceLevel(this.experience);
   }

   public final float getLevelProgress() {
      int bar_xp = this.experience - getExperienceRequired(this.getExperienceLevel());
      return (float)bar_xp / (float)(getExperienceRequired(this.getExperienceLevel() + 1) - getExperienceRequired(this.getExperienceLevel()));
   }

   public static int getHealthLimit(int level) {
      return Math.max(Math.min(6 + level / 5 * 2, 20), 6);
   }

   public float getHealthLimit() {
      return (float)getHealthLimit(this.getExperienceLevel());
   }

   public static float getLevelModifier(int level, EnumLevelBonus kind) {
      return level > 0 && kind == EnumLevelBonus.MELEE_DAMAGE ? (float)level * 0.005F : (float)level * 0.02F;
   }

   public float getLevelModifier(EnumLevelBonus kind) {
      return getLevelModifier(this.getExperienceLevel(), kind);
   }

   public void preparePlayerToSpawn() {
      this.yOffset = y_offset_on_client_and_eye_height_on_server;
      this.setSizeNormal();
      super.preparePlayerToSpawn();
      this.setHealth(this.getMaxHealth());
      this.deathTime = 0;
   }

   public void setSizeNormal() {
      this.setSize(0.6F, 1.8F);
   }

   public void setSizeProne() {
      this.setSize(0.2F, 0.2F);
   }

   protected void updateEntityActionState() {
      super.updateEntityActionState();
      this.updateArmSwingProgress();
   }

   public void onLivingUpdate() {
      FMLCommonHandler.instance().onPlayerPreTick(this);
      if (this.worldObj.isRemote) {
         if (this.vision_dimming < 0.01F) {
            this.vision_dimming = 0.0F;
         } else if (this.vision_dimming > 2.0F) {
            this.vision_dimming = 2.0F;
         } else {
            this.vision_dimming -= 0.01F;
         }
      }

      if (this.flyToggleTimer > 0) {
         --this.flyToggleTimer;
      }

      if (this.worldObj.difficultySetting == 0 && this.getHealth() < this.getMaxHealth() && this.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && this.ticksExisted % 20 * 12 == 0) {
         this.heal(1.0F);
      }

      this.inventory.decrementAnimations();
      this.prevCameraYaw = this.cameraYaw;
      super.onLivingUpdate();
      AttributeInstance var1 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      if (!this.worldObj.isRemote) {
         var1.setAttribute((double)this.capabilities.getWalkSpeed());
      }

      this.jumpMovementFactor = this.speedInAir;
      if (this.isSprinting()) {
         this.jumpMovementFactor = (float)((double)this.jumpMovementFactor + (double)this.speedInAir * 0.3);
      }

      if (!this.hasFoodEnergy()) {
         this.jumpMovementFactor *= 0.75F;
      }

      this.jumpMovementFactor *= this.getSpeedBoostOrSlowDownFactor();
      this.setAIMoveSpeed((float)var1.getAttributeValue());
      float var2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      float var3 = (float)Math.atan(-this.motionY * 0.20000000298023224) * 15.0F;
      if (var2 > 0.1F) {
         var2 = 0.1F;
      }

      if (!this.onGround || this.getHealth() <= 0.0F) {
         var2 = 0.0F;
      }

      if (this.onGround || this.getHealth() <= 0.0F) {
         var3 = 0.0F;
      }

      this.cameraYaw += (var2 - this.cameraYaw) * 0.4F;
      this.cameraPitch += (var3 - this.cameraPitch) * 0.8F;
      if (this.getHealth() > 0.0F) {
         AxisAlignedBB var4 = null;
         if (this.ridingEntity != null && !this.ridingEntity.isDead) {
            var4 = this.boundingBox.func_111270_a(this.ridingEntity.boundingBox).expand(1.0, 0.0, 1.0);
         } else {
            var4 = this.boundingBox.expand(1.0, 0.5, 1.0);
         }

         this.collided_with_gelatinous_cube = false;
         List var5 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, var4);
         if (var5 != null) {
            for(int var6 = 0; var6 < var5.size(); ++var6) {
               Entity var7 = (Entity)var5.get(var6);
               if (!var7.isDead) {
                  this.collideWithPlayer(var7);
               }
            }
         }
      }

      FMLCommonHandler.instance().onPlayerPostTick(this);
   }

   private void collideWithPlayer(Entity par1Entity) {
      par1Entity.onCollideWithPlayer(this);
   }

   public int getScore() {
      return this.dataWatcher.getWatchableObjectInt(18);
   }

   public void setScore(int par1) {
      this.dataWatcher.updateObject(18, par1);
   }

   public void addScore(int par1) {
      int var2 = this.getScore();
      this.dataWatcher.updateObject(18, var2 + par1);
   }

   public void causeBreakingItemEffect(Item item, int hotbar_slot_index) {
      if (item.hasBreakingEffect()) {
         this.entityFX(EnumEntityFX.item_breaking, (new SignalData()).setByte(hotbar_slot_index).setShort(item.itemID));
      }

   }

   public void onDeath(DamageSource par1DamageSource) {
      if (ForgeHooks.onLivingDeath(this, par1DamageSource)) return;
      super.onDeath(par1DamageSource);
      this.setSizeProne();
      this.setPosition(this.posX, this.posY, this.posZ);
      this.motionY = 0.10000000149011612;

      captureDrops = true;
      capturedDrops.clear();

      if (this.username.equals("Notch")) {
         this.dropPlayerItemWithRandomChoice(new ItemStack(Item.appleRed, 1), true);
      }

      if (!this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
         this.inventory.dropAllItems();
      }

      captureDrops = false;

      if (!worldObj.isRemote)
      {
         PlayerDropsEvent event = new PlayerDropsEvent(this, par1DamageSource, capturedDrops, recentlyHit > 0);
         if (!MinecraftForge.EVENT_BUS.post(event))
         {
            for (EntityItem item : capturedDrops)
            {
               joinEntityItemWithWorld(item);
            }
         }
      }

      if (par1DamageSource != null) {
         this.motionX = (double)(-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * 3.1415927F / 180.0F) * 0.1F);
         this.motionZ = (double)(-MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * 3.1415927F / 180.0F) * 0.1F);
      } else {
         this.motionX = this.motionZ = 0.0;
      }

      this.yOffset = 0.1F;
      this.addStat(StatList.deathsStat, 1);
   }

   public void addToPlayerScore(Entity par1Entity, int par2) {
      this.addScore(par2);
      Collection var3 = this.getWorldScoreboard().func_96520_a(ScoreObjectiveCriteria.totalKillCount);
      if (par1Entity instanceof EntityPlayer) {
         this.addStat(StatList.playerKillsStat, 1);
         var3.addAll(this.getWorldScoreboard().func_96520_a(ScoreObjectiveCriteria.playerKillCount));
      } else {
         this.addStat(StatList.mobKillsStat, 1);
      }

      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         ScoreObjective var5 = (ScoreObjective)var4.next();
         Score var6 = this.getWorldScoreboard().func_96529_a(this.getEntityName(), var5);
         var6.func_96648_a();
      }

   }

   public EntityItem dropOneItem(boolean par1) {
      ItemStack stack = inventory.getItemStack();

      if (stack == null) {
         return null;
      }

      if (stack.getItem().onDroppedByPlayer(stack, this)) {
         int count = par1 && this.inventory.getItemStack() != null ? this.inventory.getItemStack().stackSize : 1;
         return ForgeHooks.onPlayerTossEvent(this, inventory.decrStackSize(inventory.currentItem, count));
      }

      return null;
   }

   public EntityItem dropPlayerItem(ItemStack par1ItemStack) {
      return ForgeHooks.onPlayerTossEvent(this, par1ItemStack);
   }

   public EntityItem dropPlayerItemWithRandomChoice(ItemStack par1ItemStack, boolean par2) {
      if (par1ItemStack == null) {
         return null;
      } else if (par1ItemStack.stackSize == 0) {
         return null;
      } else {
         EntityItem var3 = new EntityItem(this.worldObj, this.posX, this.getEyePosY() - 0.30000001192092896, this.posZ, par1ItemStack);
         var3.delayBeforeCanPickup = 40;
         float var4 = 0.1F;
         float var5;
         if (par2) {
            var5 = this.rand.nextFloat() * 0.5F;
            float var6 = this.rand.nextFloat() * 3.1415927F * 2.0F;
            var3.motionX = (double)(-MathHelper.sin(var6) * var5);
            var3.motionZ = (double)(MathHelper.cos(var6) * var5);
            var3.motionY = 0.20000000298023224;
         } else {
            var4 = 0.3F;
            var3.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F) * var4);
            var3.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F) * var4);
            var3.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * 3.1415927F) * var4 + 0.1F);
            var4 = 0.02F;
            var5 = this.rand.nextFloat() * 3.1415927F * 2.0F;
            var4 *= this.rand.nextFloat();
            var3.motionX += Math.cos((double)var5) * (double)var4;
            var3.motionY += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
            var3.motionZ += Math.sin((double)var5) * (double)var4;
         }

         var3.age = -18000;
         var3.dropped_by_player = true;
         this.joinEntityItemWithWorld(var3);
         this.addStat(StatList.dropStat, 1);
         return var3;
      }
   }

   public EntityItem dropPlayerItemWithNoTrajectory(ItemStack par1ItemStack) {
      if (par1ItemStack == null) {
         return null;
      } else if (par1ItemStack.stackSize == 0) {
         return null;
      } else {
         EntityItem var3 = new EntityItem(this.worldObj, this.posX, this.getEyePosY() - 0.30000001192092896, this.posZ, par1ItemStack);
         var3.delayBeforeCanPickup = 40;
         var3.motionX = var3.motionY = var3.motionZ = 0.0;
         var3.age = -18000;
         var3.dropped_by_player = true;
         this.joinEntityItemWithWorld(var3);
         this.addStat(StatList.dropStat, 1);
         return var3;
      }
   }

   public void joinEntityItemWithWorld(EntityItem par1EntityItem) {
      if (captureDrops)
      {
         capturedDrops.add(par1EntityItem);
         return;
      }
      this.worldObj.spawnEntityInWorld(par1EntityItem);
   }

   public float getCurrentPlayerStrVsBlock(int x, int y, int z, boolean apply_held_item) {
      Block block = Block.blocksList[this.worldObj.getBlockId(x, y, z)];
      if (block == null) {
         return 0.0F;
      } else {
         float block_hardness = this.worldObj.getBlockHardness(x, y, z);
         if (block_hardness == 0.0F) {
            return 1.0F;
         } else {
            int metadata = 0;
            float min_str_vs_block = -3.4028235E38F;
            Item held_item = this.getHeldItem();
            float str_vs_block;
            if (block.isPortable(this.worldObj, this, x, y, z)) {
               str_vs_block = min_str_vs_block = 4.0F * block_hardness;
            } else {
               if (apply_held_item && held_item != null) {
                  metadata = this.worldObj.getBlockMetadata(x, y, z);
                  str_vs_block = held_item.getStrVsBlock(block, metadata);
                  if (str_vs_block < 1.0F) {
                     return this.getCurrentPlayerStrVsBlock(x, y, z, false);
                  }

                  int var4 = EnchantmentHelper.getEfficiencyModifier(this);
                  if (var4 > 0) {
                     float var6 = (float)(var4 * var4 + 1);
                     str_vs_block += var6;
                  }
               } else {
                  metadata = this.worldObj.getBlockMetadata(x, y, z);
                  if (block.blockMaterial.requiresTool(block, metadata)) {
                     str_vs_block = 0.0F;
                  } else {
                     str_vs_block = 1.0F;
                  }
               }
            }

            if (block == Block.web) {
               boolean decrease_strength = true;
               if (apply_held_item && held_item != null && held_item.isTool() && held_item.getAsTool().isEffectiveAgainstBlock(block, 0)) {
                  decrease_strength = false;
               }

               if (decrease_strength) {
                  str_vs_block *= 0.2F;
               }
            }

            if (this.isPotionActive(Potion.digSpeed)) {
               str_vs_block *= 1.0F + (float)(this.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
            }

            if (this.isPotionActive(Potion.digSlowdown)) {
               str_vs_block *= 1.0F - (float)(this.getActivePotionEffect(Potion.digSlowdown).getAmplifier() + 1) * 0.2F;
            }

            if (this.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(this)) {
               str_vs_block /= 5.0F;
            }

            if (!this.onGround) {
               str_vs_block /= 5.0F;
            }

            if (!this.hasFoodEnergy()) {
               str_vs_block /= 5.0F;
            }

            str_vs_block *= 1.0F + this.getLevelModifier(EnumLevelBonus.HARVESTING);
            float speed = Math.max(str_vs_block, min_str_vs_block);
            speed = ForgeEventFactory.getBreakSpeed(this, block, metadata, speed);
            return speed;
         }
      }
   }

   public float getRelativeBlockHardness(int x, int y, int z, boolean apply_held_item) {
      Block block = Block.blocksList[this.worldObj.getBlockId(x, y, z)];
      if (block == null) {
         return -1.0F;
      } else {
         float block_hardness = this.worldObj.getBlockHardness(x, y, z);
         if (block_hardness == 0.0F) {
            return 0.0F;
         } else if (!(block_hardness < 0.0F) && !(this.getCurrentPlayerStrVsBlock(x, y, z, apply_held_item) <= 0.0F)) {
            float relative_hardness = block_hardness / this.getCurrentPlayerStrVsBlock(x, y, z, apply_held_item);
            return relative_hardness < 0.0029296875F ? 0.0F : relative_hardness;
         } else {
            return -1.0F;
         }
      }
   }

   public float getRelativeStrVsBlock(int x, int y, int z, boolean apply_held_item) {
      float relative_hardness = this.getRelativeBlockHardness(x, y, z, apply_held_item);
      if (relative_hardness < 0.0F) {
         return 0.0F;
      } else {
         return relative_hardness == 0.0F ? 512.0F : 1.0F / relative_hardness;
      }
   }

   public float getDamageVsBlock(int x, int y, int z, boolean apply_held_item) {
      return this.getRelativeStrVsBlock(x, y, z, apply_held_item) / 512.0F;
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      this.experience = par1NBTTagCompound.getInteger("experience");
      if (par1NBTTagCompound.hasKey("XpTotal")) {
         this.experience = par1NBTTagCompound.getInteger("XpTotal");
      }

      super.readEntityFromNBT(par1NBTTagCompound);
      NBTTagList var2 = par1NBTTagCompound.getTagList("Inventory");
      this.inventory.readFromNBT(var2);
      this.inventory.currentItem = par1NBTTagCompound.getInteger("SelectedItemSlot");
      this.setScore(par1NBTTagCompound.getInteger("Score"));
      if (this.inBed()) {
         this.bed_location = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
         this.wakeUpPlayer(true, (Entity)null);
      }

      if (par1NBTTagCompound.hasKey("SpawnX") && par1NBTTagCompound.hasKey("SpawnY") && par1NBTTagCompound.hasKey("SpawnZ")) {
         this.spawnChunk = new ChunkCoordinates(par1NBTTagCompound.getInteger("SpawnX"), par1NBTTagCompound.getInteger("SpawnY"), par1NBTTagCompound.getInteger("SpawnZ"));
         this.spawnForced = par1NBTTagCompound.getBoolean("SpawnForced");
      }

      NBTTagList spawnlist = null;
      spawnlist = par1NBTTagCompound.getTagList("Spawns");
      for (int i = 0; i < spawnlist.tagCount(); ++i) {
         NBTTagCompound spawndata = (NBTTagCompound)spawnlist.tagAt(i);
         int spawndim = spawndata.getInteger("Dim");
         this.spawnChunkMap.put(spawndim, new ChunkCoordinates(spawndata.getInteger("SpawnX"), spawndata.getInteger("SpawnY"), spawndata.getInteger("SpawnZ")));
         this.spawnForcedMap.put(spawndim, spawndata.getBoolean("SpawnForced"));
      }

      this.foodStats.readNBT(par1NBTTagCompound);
      this.capabilities.readCapabilitiesFromNBT(par1NBTTagCompound);
      if (par1NBTTagCompound.hasKey("EnderItems")) {
         NBTTagList var3 = par1NBTTagCompound.getTagList("EnderItems");
         this.theInventoryEnderChest.loadInventoryFromNBT(var3);
      }

      this.vision_dimming = par1NBTTagCompound.getFloat("vision_dimming");
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setInteger("experience", this.experience);
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
      par1NBTTagCompound.setInteger("SelectedItemSlot", this.inventory.currentItem);
      par1NBTTagCompound.setInteger("Score", this.getScore());
      if (this.spawnChunk != null) {
         par1NBTTagCompound.setInteger("SpawnX", this.spawnChunk.posX);
         par1NBTTagCompound.setInteger("SpawnY", this.spawnChunk.posY);
         par1NBTTagCompound.setInteger("SpawnZ", this.spawnChunk.posZ);
         par1NBTTagCompound.setBoolean("SpawnForced", this.spawnForced);
      }

      NBTTagList spawnlist = new NBTTagList();
      for (Entry<Integer, ChunkCoordinates> entry : this.spawnChunkMap.entrySet()) {
         NBTTagCompound spawndata = new NBTTagCompound();
         ChunkCoordinates spawn = entry.getValue();
         if (spawn == null) continue;
         Boolean forced = spawnForcedMap.get(entry.getKey());
         if (forced == null) forced = false;
         spawndata.setInteger("Dim", entry.getKey());
         spawndata.setInteger("SpawnX", spawn.posX);
         spawndata.setInteger("SpawnY", spawn.posY);
         spawndata.setInteger("SpawnZ", spawn.posZ);
         spawndata.setBoolean("SpawnForced", forced);
         spawnlist.appendTag(spawndata);
      }
      par1NBTTagCompound.setTag("Spawns", spawnlist);

      this.foodStats.writeNBT(par1NBTTagCompound);
      this.capabilities.writeCapabilitiesToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setTag("EnderItems", this.theInventoryEnderChest.saveInventoryToNBT());
      par1NBTTagCompound.setFloat("vision_dimming", this.vision_dimming);
   }

   public void displayGUIChestForMinecart(IInventory par1IInventory) {
   }

   public void displayGUIChest(int x, int y, int z, IInventory par1IInventory) {
   }

   public void displayGUIHopper(TileEntityHopper par1TileEntityHopper) {
   }

   public void displayGUIHopperMinecart(EntityMinecartHopper par1EntityMinecartHopper) {
   }

   public void displayGUIHorse(EntityHorse par1EntityHorse, IInventory par2IInventory) {
   }

   public void displayGUIEnchantment(int par1, int par2, int par3, String par4Str) {
   }

   public void displayGUIAnvil(int x, int y, int z) {
   }

   public void displayGUIWorkbench(int par1, int par2, int par3) {
   }

   public float getEyeHeight() {
      return this.isSneaking() ? -0.1185F : 0.12F;
   }

   public double getFootPosY() {
      double foot_pos_y = this.posY - (double)y_offset_on_client_and_eye_height_on_server;
      int foot_pos_y_int = (int)foot_pos_y;
      if (foot_pos_y < (double)foot_pos_y_int && (double)foot_pos_y_int - foot_pos_y < 9.999999747378752E-5) {
         foot_pos_y = (double)foot_pos_y_int;
      }

      return foot_pos_y;
   }

   public double getEyePosY() {
      return this.isSneaking() ? this.posY + -0.23849999904632568 : this.posY;
   }

   protected void resetHeight() {
      this.yOffset = y_offset_on_client_and_eye_height_on_server;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      if (ForgeHooks.onLivingAttack(this, damage.getSource(), damage.getAmount())) return null;
      if (this.ticksExisted < 1000 && Damage.wasCausedByPlayer(damage) && this.isWithinTournamentSafeZone()) {
         return null;
      } else if (this.capabilities.disableDamage && !damage.canHarmInCreative()) {
         return null;
      } else {
         if (this.inBed()) {
            this.wakeUpPlayer(true, damage.getResponsibleEntity());
         }

         if (damage.isExplosion()) {
            damage.scaleAmount(1.5F);
         }

         EntityDamageResult result = super.attackEntityFrom(damage);
         if (result != null) {
         }

         return result;
      }
   }

   public boolean canAttackPlayer(EntityPlayer par1EntityPlayer) {
      Team var2 = this.getTeam();
      Team var3 = par1EntityPlayer.getTeam();
      return var2 == null ? true : (!var2.isSameTeam(var3) ? true : var2.getAllowFriendlyFire());
   }

   public void tryDamageArmor(DamageSource damage_source, float amount, EntityDamageResult result) {
      this.inventory.tryDamageArmor(damage_source, amount, result);
   }

   public float getArmorVisibility() {
      int var1 = 0;
      ItemStack[] var2 = this.inventory.armorInventory;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ItemStack var5 = var2[var4];
         if (var5 != null) {
            ++var1;
         }
      }

      return (float)var1 / (float)this.inventory.armorInventory.length;
   }

   public void displayGUIFurnace(TileEntityFurnace par1TileEntityFurnace) {
   }

   public void displayGUIDispenser(TileEntityDispenser par1TileEntityDispenser) {
   }

   public void displayGUIEditSign(TileEntity par1TileEntity) {
   }

   public void displayGUIBrewingStand(TileEntityBrewingStand par1TileEntityBrewingStand) {
   }

   public void displayGUIBeacon(TileEntityBeacon par1TileEntityBeacon) {
   }

   public void displayGUIMerchant(IMerchant par1IMerchant, String par2Str) {
   }

   public void displayGUIBook(ItemStack par1ItemStack) {
   }

   public double getYOffset() {
      return (double)(this.yOffset - 0.5F);
   }

   public boolean willDeliverCriticalStrike() {
      return this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(Potion.blindness) && this.ridingEntity == null;
   }

   public float calcRawMeleeDamageVs(Entity target) {
      return this.calcRawMeleeDamageVs(target, this.willDeliverCriticalStrike(), this.isSuspendedInLiquid());
   }

   public float calcRawMeleeDamageVs(Entity target, boolean critical, boolean suspended_in_liquid) {
      float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
      if (damage <= 0.0F) {
         return 0.0F;
      } else {
         float enchantment_modifiers = 0.0F;
         if (target == null || target instanceof EntityLivingBase) {
            enchantment_modifiers = EnchantmentDamage.getDamageModifiers(this.getHeldItemStack(), (EntityLivingBase)target);
         }

         if (damage > 0.0F || enchantment_modifiers > 0.0F) {
            if (critical && damage > 0.0F) {
               damage *= 1.5F;
            }

            if (this.getLevelModifier(EnumLevelBonus.MELEE_DAMAGE) < 0.0F || !this.worldObj.areSkillsEnabled() || this.hasSkill(Skill.FIGHTING)) {
               damage *= 1.0F + this.getLevelModifier(EnumLevelBonus.MELEE_DAMAGE);
            }

            Item held_item = this.getHeldItemStack() == null ? null : this.getHeldItemStack().getItem();
            if (held_item != null) {
               if (target instanceof EntityLivingBase && ((EntityLivingBase)target).isEntityUndead() && held_item.hasMaterial(Material.silver)) {
                  damage *= 1.25F;
               }

               if (target instanceof EntitySkeleton && (held_item instanceof ItemCudgel || held_item instanceof ItemWarHammer)) {
                  damage *= 2.0F;
               }
            }

            damage += enchantment_modifiers;
            if (suspended_in_liquid && damage > 1.0F) {
               damage = 1.0F + (damage - 1.0F) * 0.5F;
            }
         }

         return damage;
      }
   }

   public void attackTargetEntityWithCurrentItem(Entity target) {
      if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(this, target)))
      {
         return;
      }
      ItemStack stack = getHeldItemStack();
      if (stack != null && stack.getItem().onLeftClickEntity(stack, this, target))
      {
         return;
      }

      if (!this.isImmuneByGrace()) {
         if (target.canAttackWithItem()) {
            boolean critical = this.willDeliverCriticalStrike();
            float damage = this.calcRawMeleeDamageVs(target, critical, this.isSuspendedInLiquid());
            if (damage <= 0.0F) {
               return;
            }

            int knockback = 0;
            if (target instanceof EntityLivingBase) {
               knockback += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)target);
            }

            if (this.isSprinting()) {
               ++knockback;
            }

            boolean was_set_on_fire = false;
            int fire_aspect = EnchantmentHelper.getFireAspectModifier(this);
            if (target instanceof EntityLivingBase && fire_aspect > 0 && !target.isBurning()) {
               was_set_on_fire = true;
               target.setFire(1);
            }

            if (this.onServer() && target instanceof EntityLivingBase) {
               EntityLivingBase entity_living_base = (EntityLivingBase)target;
               ItemStack item_stack_to_drop = entity_living_base.getHeldItemStack();
               if (item_stack_to_drop != null && this.rand.nextFloat() < EnchantmentHelper.getEnchantmentLevelFraction(Enchantment.disarming, this.getHeldItemStack()) && entity_living_base instanceof EntityLiving) {
                  EntityLiving entity_living = (EntityLiving)entity_living_base;
                  entity_living.dropItemStack(item_stack_to_drop, entity_living.height / 2.0F);
                  entity_living.clearMatchingEquipmentSlot(item_stack_to_drop);
                  entity_living.ticks_disarmed = 40;
               }
            }

            EntityDamageResult result = target.attackEntityFrom(new Damage(DamageSource.causePlayerDamage(this).setFireAspect(fire_aspect > 0), damage));
            boolean target_was_harmed = result != null && result.entityWasNegativelyAffected();
            target.onMeleeAttacked(this, result);
            if (target_was_harmed) {
               if (target instanceof EntityLivingBase) {
                  int stunning = EnchantmentHelper.getStunModifier(this, (EntityLivingBase)target);
                  if ((double)stunning > Math.random() * 10.0) {
                     ((EntityLivingBase)target).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, stunning * 50, stunning * 5));
                  }

                  this.heal((float)EnchantmentHelper.getVampiricTransfer(this, (EntityLivingBase)target, damage), EnumEntityFX.vampiric_gain);
               }

               if (knockback > 0) {
                  target.addVelocity((double)(-MathHelper.sin(this.rotationYaw * 3.1415927F / 180.0F) * (float)knockback * 0.5F), 0.1, (double)(MathHelper.cos(this.rotationYaw * 3.1415927F / 180.0F) * (float)knockback * 0.5F));
                  this.motionX *= 0.6;
                  this.motionZ *= 0.6;
                  this.setSprinting(false);
               }

               if (critical) {
                  this.onCriticalHit(target);
               }

               if (target instanceof EntityLivingBase && EnchantmentDamage.getDamageModifiers(this.getHeldItemStack(), (EntityLivingBase)target) > 0.0F) {
                  this.onEnchantmentCritical(target);
               }

               if (damage >= 18.0F) {
                  this.triggerAchievement(AchievementList.overkill);
               }

               this.setLastAttackTarget(target);
               if (target instanceof EntityLivingBase) {
                  if (this.worldObj.isRemote) {
                     System.out.println("EntityPlayer.attackTargetEntityWithCurrentItem() is calling EnchantmentThorns.func_92096_a() on client");
                     Minecraft.temp_debug = "player";
                  }

                  EnchantmentThorns.func_92096_a(this, (EntityLivingBase)target, this.rand);
               }
            }

            ItemStack held_item_stack = this.getHeldItemStack();
            Object var10 = target;
            if (target instanceof EntityDragonPart) {
               IEntityMultiPart var11 = ((EntityDragonPart)target).entityDragonObj;
               if (var11 != null && var11 instanceof EntityLivingBase) {
                  var10 = (EntityLivingBase)var11;
               }
            }

            if (target_was_harmed && held_item_stack != null && var10 instanceof EntityLivingBase) {
               held_item_stack.hitEntity((EntityLivingBase)var10, this);
            }

            if (target instanceof EntityLivingBase) {
               this.addStat(StatList.damageDealtStat, Math.round(damage * 10.0F));
               if (fire_aspect > 0 && target_was_harmed) {
                  target.setFire(fire_aspect * 4);
               } else if (was_set_on_fire) {
                  target.extinguish();
               }
            }

            if (this.onServer()) {
               this.addHungerServerSide(0.3F * EnchantmentHelper.getEnduranceModifier(this));
            }
         }

      }
   }

   public void onCriticalHit(Entity par1Entity) {
   }

   public void onEnchantmentCritical(Entity par1Entity) {
   }

   public void respawnPlayer() {
   }

   public void setDead() {
      super.setDead();
      this.inventoryContainer.onContainerClosed(this);
      if (this.openContainer != null) {
         this.openContainer.onContainerClosed(this);
      }

   }

   public boolean isEntityInsideOpaqueBlock() {
      return !this.inBed() && super.isEntityInsideOpaqueBlock();
   }

   public EntityLiving getHostileEntityNearBed() {
      return this.getHostileEntityNearBed(this.bed_location.posX, this.bed_location.posY, this.bed_location.posZ);
   }

   public EntityLiving getHostileEntityNearBed(int x, int y, int z) {
      double var4 = 8.0;
      double var6 = 5.0;
      List var8 = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB((double)x - var4, (double)y - var6, (double)z - var4, (double)x + var4, (double)y + var6, (double)z + var4));
      if (var8.size() == 0) {
         return null;
      } else {
         EntityLiving nearest_pathable_hostile_entity_living = null;
         double distanc_sq_to_nearest_pathable_hostile_entity_living = 1000.0;
         Iterator i = var8.iterator();

         while(true) {
            EntityLiving entity_living;
            double distance_sq_to_entity_living;
            do {
               boolean is_considered_hostile;
               do {
                  if (!i.hasNext()) {
                     return nearest_pathable_hostile_entity_living;
                  }

                  entity_living = (EntityLiving)i.next();
                  is_considered_hostile = entity_living instanceof IMob;
               } while(!is_considered_hostile);

               World var10000 = this.worldObj;
               distance_sq_to_entity_living = World.getDistanceSqFromDeltas((float)(MathHelper.floor_double(entity_living.posX) - x), (float)(MathHelper.floor_double(entity_living.posY) - y), (float)(MathHelper.floor_double(entity_living.posZ) - z));
            } while(nearest_pathable_hostile_entity_living != null && !(distance_sq_to_entity_living < distanc_sq_to_nearest_pathable_hostile_entity_living));

            PathEntity path = entity_living.getNavigator().getPathToXYZ(x, y, z, 24);
            if (path != null) {
               PathPoint final_point = path.getFinalPathPoint();
               int abs_dx = Math.abs(final_point.xCoord - x);
               int abs_dy = Math.abs(final_point.yCoord - y);
               int abs_dz = Math.abs(final_point.zCoord - z);
               if (abs_dx <= 1 && abs_dy <= 1 && abs_dz <= 1) {
                  nearest_pathable_hostile_entity_living = entity_living;
                  distanc_sq_to_nearest_pathable_hostile_entity_living = distance_sq_to_entity_living;
               }
            }
         }
      }
   }

   public boolean isHostileEntityDiggingNearBed(int x, int y, int z) {
      double var4 = 8.0;
      double var6 = 5.0;
      List var8 = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB((double)x - var4, (double)y - var6, (double)z - var4, (double)x + var4, (double)y + var6, (double)z + var4));
      if (var8.size() == 0) {
         return false;
      } else {
         Iterator i = var8.iterator();

         while(i.hasNext()) {
            EntityLiving entity_living = (EntityLiving)i.next();
            if (entity_living instanceof EntityZombie) {
               EntityZombie entity_zombie = (EntityZombie)entity_living;
               if (entity_zombie.is_destroying_block) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static float getYawForDirection(int direction) {
      return (float)direction * 90.0F - 180.0F;
   }

   public void setPositionAndRotationInBed() {
      float offset_x;
      float offset_z;
      switch (this.bed_direction) {
         case 0:
            offset_x = 0.5F;
            offset_z = 0.9F;
            break;
         case 1:
            offset_x = 0.1F;
            offset_z = 0.5F;
            break;
         case 2:
            offset_x = 0.5F;
            offset_z = 0.1F;
            break;
         default:
            offset_x = 0.9F;
            offset_z = 0.5F;
      }

      float x = (float)this.bed_location.posX + offset_x;
      float y = (float)this.bed_location.posY + 0.7626F;
      float z = (float)this.bed_location.posZ + offset_z;
      this.setPosition((double)x, (double)y, (double)z, true);
      if (this.onServer()) {
         this.getAsEntityPlayerMP().set_position_in_bed_next_tick = true;
      }

      this.lastTickPosX = (double)x;
      this.lastTickPosY = (double)y;
      this.lastTickPosZ = (double)z;
      this.rotationYaw = getYawForDirection(this.bed_direction);
      this.rotationPitch = 0.0F;
      this.rotationYawHead = this.rotationYaw;
      this.motionX = this.motionZ = this.motionY = 0.0;
   }

   public void getIntoBed(int x, int y, int z, int direction) {
      if (this.isRiding()) {
         this.mountEntity((Entity)null);
      }

      if (this.onServer()) {
         this.pos_x_before_bed = this.posX;
         this.pos_y_before_bed = this.posY;
         this.pos_z_before_bed = this.posZ;
      }

      this.setSizeProne();
      this.yOffset = 0.2F;
      this.func_71013_b(direction);
      this.bed_location = new ChunkCoordinates(x, y, z);
      this.bed_direction = direction;
      this.setPositionAndRotationInBed();
      if (!this.worldObj.isRemote) {
         BlockBed.setBedOccupied(this.worldObj, x, y, z, true);
      }

      this.setSpawnChunk(this.bed_location, false);
   }

   public void tryToSleepInBedAt(int x, int y, int z) {
      PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(this, x, y, z);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.result != null)
      {
         return;
      }
   }

   private void func_71013_b(int par1) {
      this.field_71079_bU = 0.0F;
      this.field_71089_bV = 0.0F;
      switch (par1) {
         case 0:
            this.field_71089_bV = -1.8F;
            break;
         case 1:
            this.field_71079_bU = 1.8F;
            break;
         case 2:
            this.field_71089_bV = 1.8F;
            break;
         case 3:
            this.field_71079_bU = -1.8F;
      }

   }

   public void getOutOfBed(Entity entity_to_look_at) {
      this.setSizeNormal();
      this.resetHeight();
      if (this.bed_location != null) {
         int x = this.bed_location.posX;
         int y = this.bed_location.posY;
         int z = this.bed_location.posZ;
         ChunkCoordinates new_location;
         if (this.worldObj.getBlockId(x, y, z) == Block.bed.blockID) {
            BlockBed.setBedOccupied(this.worldObj, x, y, z, false);
            new_location = BlockBed.getNearestEmptyChunkCoordinates(this.worldObj, x, y, z, 0, this.worldObj.getVec3(this.pos_x_before_bed, this.pos_y_before_bed, this.pos_z_before_bed));
            if (new_location == null) {
               new_location = new ChunkCoordinates(x, y + 1, z);
            }
         } else {
            new_location = new ChunkCoordinates(x, y, z);
         }

         if (new_location.posX == MathHelper.floor_double(this.pos_x_before_bed) && new_location.posY == MathHelper.floor_double(this.pos_y_before_bed + 0.949999988079071) && new_location.posZ == MathHelper.floor_double(this.pos_z_before_bed)) {
            this.setPosition(this.pos_x_before_bed, this.pos_y_before_bed + (double)this.yOffset, this.pos_z_before_bed, true);
         } else {
            this.setPosition((double)((float)new_location.posX + 0.5F), (double)((float)new_location.posY + this.yOffset), (double)((float)new_location.posZ + 0.5F), true);
         }
      }

      if (entity_to_look_at == null) {
         if (this.bed_location != null) {
            this.setRotationForLookingAt(this.worldObj.getBlockCenterPos(this.bed_location.posX, this.bed_location.posY, this.bed_location.posZ));
         }
      } else {
         this.setRotationForLookingAt(entity_to_look_at instanceof EntityLivingBase ? entity_to_look_at.getAsEntityLivingBase().getFootPosPlusFractionOfHeight(0.75F) : entity_to_look_at.getCenterPoint());
      }

      this.bed_location = null;
      this.conscious_state = EnumConsciousState.fully_awake;
      if (this.worldObj.isRemote) {
         this.lastTickPosX = this.posX;
         this.lastTickPosY = this.posY;
         this.lastTickPosZ = this.posZ;
      } else {
         this.getAsEntityPlayerMP().try_push_out_of_blocks = true;
      }

   }

   public void wakeUpPlayer(boolean get_out_of_bed, Entity entity_to_look_at) {
      if (get_out_of_bed) {
         this.getOutOfBed(entity_to_look_at);
      } else {
         this.conscious_state = EnumConsciousState.waking_up;
      }

   }

   public boolean inBed() {
      return this.bed_location != null && (this.worldObj.isRemote || this.worldObj.getBlockId(this.bed_location.posX, this.bed_location.posY, this.bed_location.posZ) == Block.bed.blockID);
   }

   public static ChunkCoordinates verifyRespawnCoordinates(World par0World, ChunkCoordinates par1ChunkCoordinates, boolean par2) {
      IChunkProvider var3 = par0World.getChunkProvider();
      var3.loadChunk(par1ChunkCoordinates.posX - 3 >> 4, par1ChunkCoordinates.posZ - 3 >> 4);
      var3.loadChunk(par1ChunkCoordinates.posX + 3 >> 4, par1ChunkCoordinates.posZ - 3 >> 4);
      var3.loadChunk(par1ChunkCoordinates.posX - 3 >> 4, par1ChunkCoordinates.posZ + 3 >> 4);
      var3.loadChunk(par1ChunkCoordinates.posX + 3 >> 4, par1ChunkCoordinates.posZ + 3 >> 4);
      if (par0World.getBlockId(par1ChunkCoordinates.posX, par1ChunkCoordinates.posY, par1ChunkCoordinates.posZ) == Block.bed.blockID) {
         ChunkCoordinates var8 = BlockBed.getNearestEmptyChunkCoordinates(par0World, par1ChunkCoordinates.posX, par1ChunkCoordinates.posY, par1ChunkCoordinates.posZ, 0, (Vec3)null);
         return var8;
      } else {
         Material var4 = par0World.getBlockMaterial(par1ChunkCoordinates.posX, par1ChunkCoordinates.posY, par1ChunkCoordinates.posZ);
         Material var5 = par0World.getBlockMaterial(par1ChunkCoordinates.posX, par1ChunkCoordinates.posY + 1, par1ChunkCoordinates.posZ);
         boolean var6 = !var4.isSolid() && !var4.isLiquid();
         boolean var7 = !var5.isSolid() && !var5.isLiquid();
         return par2 && var6 && var7 ? par1ChunkCoordinates : null;
      }
   }

   public float getBedOrientationInDegrees() {
      if (this.bed_location != null) {
         int var1 = this.worldObj.getBlockMetadata(this.bed_location.posX, this.bed_location.posY, this.bed_location.posZ);
         int var2 = BlockBed.j(var1);
         switch (var2) {
            case 0:
               return 90.0F;
            case 1:
               return 0.0F;
            case 2:
               return 270.0F;
            case 3:
               return 180.0F;
         }
      }

      return 0.0F;
   }

   public boolean isSleeping() {
      return false;
   }

   protected boolean getHideCape(int par1) {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1 << par1) != 0;
   }

   protected void setHideCape(int par1, boolean par2) {
      byte var3 = this.dataWatcher.getWatchableObjectByte(16);
      if (par2) {
         this.dataWatcher.updateObject(16, (byte)(var3 | 1 << par1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(var3 & ~(1 << par1)));
      }

   }

   public void addChatMessage(String par1Str) {
   }

   public ChunkCoordinates getBedLocation() {
      return getBedLocation(this.dimension);
   }

   public boolean isSpawnForced() {
      return isSpawnForced(this.dimension);
   }

   /**
    * A dimension aware version of getBedLocation.
    * @param dimension The dimension to get the bed spawn for
    * @return The player specific spawn location for the dimension.  May be null.
    */
   public ChunkCoordinates getBedLocation(int dimension) {
      if (dimension == 0) return this.spawnChunk;
      return this.spawnChunkMap.get(dimension);
   }

   /**
    * A dimension aware version of isSpawnForced.
    * Noramally isSpawnForced is used to determine if the respawn system should check for a bed or not.
    * This just extends that to be dimension aware.
    * @param dimension The dimension to get whether to check for a bed before spawning for
    * @return The player specific spawn location for the dimension.  May be null.
    */
   public boolean isSpawnForced(int dimension) {
      if (dimension == 0) return this.spawnForced;
      Boolean forced = this.spawnForcedMap.get(dimension);
      if (forced == null) return false;
      return forced;
   }

//   /**
//
//    */
//   public void setSpawnChunk(ChunkCoordinates par1ChunkCoordinates, boolean par2)
//   {
//      if (this.dimension != 0)
//      {
//         setSpawnChunk(par1ChunkCoordinates, par2, this.dimension);
//         return;
//      }
//      if (par1ChunkCoordinates != null)
//      {
//         this.spawnChunk = new ChunkCoordinates(par1ChunkCoordinates);
//
//         this.spawnForced = false;
//      }
//   }

   /**
    * A dimension aware version of setSpawnChunk.
    * This functions identically, but allows you to specify which dimension to affect, rather than affecting the player's current dimension.
    * @param chunkCoordinates The spawn point to set as the player-specific spawn point for the dimension
    * @param forced Whether or not the respawn code should check for a bed at this location (true means it won't check for a bed)
    * @param dimension Which dimension to apply the player-specific respawn point to
    */
   public void setSpawnChunk(ChunkCoordinates chunkCoordinates, boolean forced, int dimension) {
      if (dimension == 0)
      {
         if (chunkCoordinates != null)
         {
            this.spawnChunk = new ChunkCoordinates(chunkCoordinates);
            this.spawnForced = forced;
         }
         else
         {
            this.spawnChunk = null;
            this.spawnForced = false;
         }
         return;
      }
      if (chunkCoordinates != null)
      {
         this.spawnChunkMap.put(dimension, new ChunkCoordinates(chunkCoordinates));
         this.spawnForcedMap.put(dimension, forced);
      }
      else
      {
         this.spawnChunkMap.remove(dimension);
         this.spawnForcedMap.remove(dimension);
      }
   }

   public void setSpawnChunk(ChunkCoordinates par1ChunkCoordinates, boolean par2) {
      if (par1ChunkCoordinates != null) {
         this.spawnChunk = new ChunkCoordinates(par1ChunkCoordinates);
         this.spawnForced = par2;
      } else {
         this.spawnChunk = null;
         this.spawnForced = false;
      }

   }

   public void triggerAchievement(StatBase par1StatBase) {
      this.addStat(par1StatBase, 1);
   }

   public void addStat(StatBase par1StatBase, int par2) {
   }

   protected void jump() {
      super.jump();
      this.addStat(StatList.jumpStat, 1);
      if (this instanceof EntityClientPlayerMP) {
         this.getAsEntityClientPlayerMP().sendPacket((new Packet85SimpleSignal(EnumSignal.increment_stat_for_this_world_only)).setInteger(StatList.jumpStat.statId));
      }

   }

   public void moveEntityWithHeading(float par1, float par2) {
      double var3 = this.posX;
      double var5 = this.posY;
      double var7 = this.posZ;
      if (this.capabilities.isFlying && this.ridingEntity == null) {
         double var9 = this.motionY;
         float var11 = this.jumpMovementFactor;
         this.jumpMovementFactor = this.capabilities.getFlySpeed();
         super.moveEntityWithHeading(par1, par2);
         this.motionY = var9 * 0.6;
         this.jumpMovementFactor = var11;
      } else {
         if (this.collided_with_gelatinous_cube) {
            par1 *= 0.4F;
            par2 *= 0.4F;
         }

         super.moveEntityWithHeading(par1, par2);
      }

      this.addMovementStat(this.posX - var3, this.posY - var5, this.posZ - var7);
   }

   public float getAIMoveSpeed() {
      return (float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
   }

   public void addMovementStat(double par1, double par3, double par5) {
      if (this.ridingEntity == null) {
         int var7;
         if (this.isInsideOfMaterial(Material.water)) {
            var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5) * 100.0F);
            if (var7 > 0) {
               this.addStat(StatList.distanceDoveStat, var7);
               if (this.onServer()) {
                  this.addHungerServerSide(0.015F * (float)var7 * 0.01F);
               }
            }
         } else if (this.isInWater()) {
            var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0F);
            if (var7 > 0) {
               this.addStat(StatList.distanceSwumStat, var7);
               if (this.onServer()) {
                  this.addHungerServerSide(0.015F * (float)var7 * 0.01F);
               }
            }
         } else if (this.isOnLadder()) {
            if (par3 > 0.0) {
               this.addStat(StatList.distanceClimbedStat, (int)Math.round(par3 * 100.0));
               if (this.onServer()) {
                  this.addHungerServerSide((float)par3 / 10.0F);
               }
            }
         } else if (this.onGround) {
            var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0F);
            if (this.inBed()) {
               var7 = 0;
            }

            if (var7 > 0) {
               this.addStat(StatList.distanceWalkedStat, var7);
               if (this.isSprinting()) {
                  if (this.onServer()) {
                     this.addHungerServerSide(0.05F * (float)var7 * 0.01F);
                  }
               } else if (this.onServer()) {
                  this.addHungerServerSide(0.01F * (float)var7 * 0.01F);
               }
            }
         } else {
            var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0F);
            if (var7 > 25) {
               this.addStat(StatList.distanceFlownStat, var7);
            }
         }
      } else if (this.ridingEntity instanceof EntityBoat && this.onClient() && this.moveForward != 0.0F) {
         this.addHungerClientSide(Math.abs(this.moveForward) * 0.01F);
      }

   }

   private void addMountedMovementStat(double par1, double par3, double par5) {
      if (this.ridingEntity != null) {
         int var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5) * 100.0F);
         if (var7 > 0) {
            if (this.ridingEntity instanceof EntityMinecart) {
               this.addStat(StatList.distanceByMinecartStat, var7);
               if (this.startMinecartRidingCoordinate == null) {
                  this.startMinecartRidingCoordinate = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
               } else if ((double)this.startMinecartRidingCoordinate.getDistanceSquared(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) >= 1000000.0) {
                  this.addStat(AchievementList.onARail, 1);
               }
            } else if (this.ridingEntity instanceof EntityBoat) {
               this.addStat(StatList.distanceByBoatStat, var7);
            } else if (this.ridingEntity instanceof EntityPig) {
               this.addStat(StatList.distanceByPigStat, var7);
            }
         }
      }

   }

   protected void fall(float par1) {
      if (!this.capabilities.allowFlying) {
         if (par1 >= 2.0F) {
            this.addStat(StatList.distanceFallenStat, (int)Math.round((double)par1 * 100.0));
         }

         super.fall(par1);
      }
      else
      {
         MinecraftForge.EVENT_BUS.post(new PlayerFlyableFallEvent(this, par1));
      }

   }

   public void onKillEntity(EntityLivingBase par1EntityLivingBase) {
      if (par1EntityLivingBase instanceof IMob) {
         this.triggerAchievement(AchievementList.killEnemy);
      }

   }

   public void setInWeb() {
      if (!this.capabilities.isFlying) {
         super.setInWeb();
      }

   }

   public Icon getItemIcon(ItemStack par1ItemStack, int par2) {
      Icon var3 = super.getItemIcon(par1ItemStack, par2);
      if (par1ItemStack.getItem() instanceof ItemFishingRod && this.fishEntity != null) {
         var3 = ((ItemFishingRod)par1ItemStack.getItem()).func_94597_g();
      } else {
         if (par1ItemStack.getItem().requiresMultipleRenderPasses()) {
            return par1ItemStack.getItem().getIcon(par1ItemStack, par2);
         }

         if (this.itemInUse != null && par1ItemStack.getItem() instanceof ItemBow) {
            ItemBow item_bow = (ItemBow) par1ItemStack.getItem();
            float fraction_pulled = ItemBow.getFractionPulled(par1ItemStack, this.itemInUseCount);
             if (fraction_pulled >= 0.9F) {
               return item_bow.getItemIconForUseDuration(2, this);
            }

            if (fraction_pulled >= 0.65F) {
               return item_bow.getItemIconForUseDuration(1, this);
            }

            if (fraction_pulled > 0.0F) {
               return item_bow.getItemIconForUseDuration(0, this);
            }
         }

         var3 = par1ItemStack.getItem().getIcon(par1ItemStack, par2, this, itemInUse, itemInUseCount);
      }

      return var3;
   }

   public ItemStack getCurrentArmor(int par1) {
      return this.inventory.armorItemInSlot(par1);
   }

   public void addExperience(int amount) {
      this.addExperience(amount, false, false);
   }

   public void addExperience(int amount, boolean suppress_healing) {
      this.addExperience(amount, suppress_healing, false);
   }

   public void addExperience(int amount, boolean suppress_healing, boolean suppress_sound) {
      suppress_healing = true;
      if (amount < 0) {
         if (!suppress_sound) {
            this.worldObj.playSoundAtEntity(this, "imported.random.level_drain");
         }
      } else if (amount > 0) {
         this.addScore(amount);
         if (!suppress_sound) {
            this.worldObj.playSoundAtEntity(this, "random.orb", 0.1F, 0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
         }
      }

      float health_limit_before = this.getHealthLimit();
      int level_before = this.getExperienceLevel();
      this.experience += amount;
      if (this.experience < getExperienceRequired(-40)) {
         this.experience = getExperienceRequired(-40);
      }

      int level_after = this.getExperienceLevel();
      int level_change = level_after - level_before;
      if (level_change < 0) {
         this.setHealth(this.getHealth());
         this.foodStats.setSatiation(this.foodStats.getSatiation(), true);
         this.foodStats.setNutrition(this.foodStats.getNutrition(), true);
      } else if (level_change > 0) {
         if (this.getHealthLimit() > health_limit_before && (float)this.field_82249_h < (float)this.ticksExisted - 100.0F) {
            float volume = level_after > 30 ? 1.0F : (float)level_after / 30.0F;
            if (!suppress_sound) {
               this.worldObj.playSoundAtEntity(this, "random.levelup", volume * 0.75F, 1.0F);
            }

            this.field_82249_h = this.ticksExisted;
         }

         if (!suppress_healing) {
            this.setHealth(this.getHealth() + this.getHealthLimit() - health_limit_before);
         }
      }

      if (level_change != 0 && !this.worldObj.isRemote) {
         MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).sendPlayerInfoToAllPlayers(true);
      }

      if (this instanceof EntityPlayerMP && DedicatedServer.tournament_type == EnumTournamentType.score) {
         DedicatedServer.getOrCreateTournamentStanding(this).experience = this.experience;
         DedicatedServer.updateTournamentScoreOnClient(this, true);
      }

   }

   public void addHungerClientSide(float hunger) {
      if (!this.capabilities.isCreativeMode && !this.capabilities.disableDamage) {
         this.foodStats.addHungerClientSide(hunger);
      }
   }

   public void addHungerServerSide(float hunger) {
      if (!this.capabilities.isCreativeMode && !this.capabilities.disableDamage) {
         this.foodStats.addHungerServerSide(hunger);
      }
   }

   public FoodStats getFoodStats() {
      return this.foodStats;
   }

   public boolean canEatAndDrink() {
      if (this.isInsideOfMaterial(Material.water) && !this.canBreatheUnderwater()) {
         return false;
      } else {
         return !this.isPotionActive(Potion.confusion);
      }
   }

   private final boolean isIngestionForbiddenByCurse(Item ingestable_item, int item_subtype) {
      if (ingestable_item.isAnimalProduct() && this.hasCurse(Curse.cannot_eat_animals, true)) {
         return true;
      } else if (ingestable_item.isPlantProduct() && this.hasCurse(Curse.cannot_eat_plants, true)) {
         return true;
      } else {
         return ingestable_item.isDrinkable(item_subtype) && this.hasCurse(Curse.cannot_drink, true) && ingestable_item != Item.bottleOfDisenchanting;
      }
   }

   public int getSatiation() {
      return this.foodStats.getSatiation();
   }

   public int getNutrition() {
      return this.foodStats.getNutrition();
   }

   public int getSatiationLimit() {
      return this.foodStats.getSatiationLimit();
   }

   public int getNutritionLimit() {
      return this.foodStats.getNutritionLimit();
   }

   public boolean isHungry() {
      return this.getSatiation() == 0;
   }

   public boolean isStarving() {
      return this.getNutrition() == 0;
   }

   public boolean hasFoodEnergy() {
      return this.getSatiation() + this.getNutrition() != 0;
   }

   public boolean hasMaxFoodEnergy() {
      return this.getSatiation() >= this.getSatiationLimit() && this.getNutrition() >= this.getNutritionLimit();
   }

   public final boolean canIngest(ItemStack item_stack) {
      return this.canIngest(item_stack.getItem(), item_stack.getItemSubtype());
   }

   public final boolean canIngest(Item item, int item_subtype) {
      if (!this.canEatAndDrink()) {
         return false;
      } else if (item != null && item.isIngestable(item_subtype)) {
         if (item.isAlwaysEdible()) {
            return !this.isIngestionForbiddenByCurse(item, item_subtype);
         } else if (!this.canIngest(item.getSatiation(this), item.getNutrition(), item.getSugarContent(), item.getProtein(), item.getPhytonutrients())) {
            return false;
         } else {
            return !this.isIngestionForbiddenByCurse(item, item_subtype);
         }
      } else {
         return false;
      }
   }

   public boolean canIngest(int satiation, int nutrition, int sugar_content, int protein, int phytonutrients) {
      if (!this.canEatAndDrink()) {
         return false;
      } else if (satiation == 0 && nutrition == 0 && sugar_content == 0) {
         return true;
      } else {
         if (this instanceof EntityClientPlayerMP) {
            if (this.getAsEntityClientPlayerMP().is_malnourished_in_protein && protein > 0) {
               return true;
            }

            if (this.getAsEntityClientPlayerMP().is_malnourished_in_phytonutrients && phytonutrients > 0) {
               return true;
            }
         } else if (this instanceof EntityPlayerMP) {
            if (this.getAsEntityPlayerMP().getProtein() < 1 && protein > 0) {
               return true;
            }

            if (this.getAsEntityPlayerMP().getPhytonutrients() < 1 && phytonutrients > 0) {
               return true;
            }
         }

         if (this.getSatiation() < this.getSatiationLimit()) {
            if (satiation > 0) {
               return true;
            }

            if (this.getNutrition() >= this.getNutritionLimit()) {
               return false;
            }
         } else if (satiation > 0 && this.getNutrition() > 0) {
            return false;
         }

         return nutrition > 0 && this.getNutrition() < this.getNutritionLimit();
      }
   }

   public void addFoodValue(Item item) {
      this.foodStats.addFoodValue(item);
   }

   public boolean shouldHeal() {
      return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
   }

   public boolean isCurrentToolAdventureModeExempt(int par1, int par2, int par3) {
      if (this.capabilities.allowEdit) {
         return true;
      } else {
         int var4 = this.worldObj.getBlockId(par1, par2, par3);
         if (var4 > 0) {
            Block var5 = Block.blocksList[var4];
            if (var5.blockMaterial.isAdventureModeExempt()) {
               return true;
            }

            if (this.getHeldItemStack() != null) {
               ItemStack var6 = this.getHeldItemStack();
               int metadata = this.worldObj.getBlockMetadata(par1, par2, par3);
               if (var6.getStrVsBlock(var5, metadata) > 0.0F) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public final boolean canPlayerEdit(int par1, int par2, int par3, ItemStack par5ItemStack) {
      return this.capabilities.allowEdit ? true : (par5ItemStack != null ? par5ItemStack.canEditBlocks() : false);
   }

   public int getExperienceValue() {
      return this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory") ? 0 : this.experience / 3;
   }

   public final boolean isEntityPlayer() {
      return true;
   }

   public String getEntityName() {
      return this.username;
   }

   public boolean getAlwaysRenderNameTagForRender() {
      return true;
   }

   public void clonePlayer(EntityPlayer par1EntityPlayer, boolean par2) {
      if (par2) {
         this.inventory.copyInventory(par1EntityPlayer.inventory);
         this.experience = par1EntityPlayer.experience;
         this.setHealth(par1EntityPlayer.getHealth());
         this.foodStats = par1EntityPlayer.foodStats;
         this.setScore(par1EntityPlayer.getScore());
         this.teleportDirection = par1EntityPlayer.teleportDirection;
      } else if (this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
         this.inventory.copyInventory(par1EntityPlayer.inventory);
         this.experience = par1EntityPlayer.experience;
         this.setScore(par1EntityPlayer.getScore());
      }
      this.spawnChunkMap = par1EntityPlayer.spawnChunkMap;
      this.spawnForcedMap = par1EntityPlayer.spawnForcedMap;
      this.theInventoryEnderChest = par1EntityPlayer.theInventoryEnderChest;

      //Copy over a section of the Entity Data from the old player.
      //Allows mods to specify data that persists after players respawn.
      NBTTagCompound old = par1EntityPlayer.getEntityData();
      if (old.hasKey(PERSISTED_NBT_TAG))
      {
         getEntityData().setCompoundTag(PERSISTED_NBT_TAG, old.getCompoundTag(PERSISTED_NBT_TAG));
      }

   }

   protected boolean canTriggerWalking() {
      return !this.capabilities.isFlying;
   }

   public void sendPlayerAbilities() {
   }

   public void setGameType(EnumGameType par1EnumGameType) {
   }

   public String getCommandSenderName() {
      return this.username;
   }

   public World getEntityWorld() {
      return this.worldObj;
   }

   public InventoryEnderChest getInventoryEnderChest() {
      return this.theInventoryEnderChest;
   }

   public ItemStack getCurrentItemOrArmor(int par1) {
      return par1 == 0 ? this.inventory.getCurrentItemStack() : this.inventory.armorInventory[par1 - 1];
   }

   public ItemStack getHeldItemStack() {
      return this.inventory.getCurrentItemStack();
   }

   public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack) {
      if (par1 == 0)
      {
         this.inventory.mainInventory[this.inventory.currentItem] = par2ItemStack;
      }
      else
      {
         this.inventory.armorInventory[par1] = par2ItemStack;
      }

   }

   public boolean isInvisibleToPlayer(EntityPlayer par1EntityPlayer) {
      if (!this.isInvisible()) {
         return false;
      } else {
         Team var2 = this.getTeam();
         return var2 == null || par1EntityPlayer == null || par1EntityPlayer.getTeam() != var2 || !var2.func_98297_h();
      }
   }

   public ItemStack[] getLastActiveItems() {
      return this.inventory.armorInventory;
   }

   public boolean getHideCape() {
      return this.getHideCape(1);
   }

   public boolean isPushedByWater() {
      return !this.capabilities.isFlying;
   }

   public Scoreboard getWorldScoreboard() {
      return this.worldObj.getScoreboard();
   }

   public Team getTeam() {
      return this.getWorldScoreboard().getPlayersTeam(this.username);
   }

   public String getTranslatedEntityName() {
      return ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getDisplayName());
   }

   public void setAbsorptionAmount(float par1) {
      if (par1 < 0.0F) {
         par1 = 0.0F;
      }

      this.getDataWatcher().updateObject(17, par1);
   }

   public float getAbsorptionAmount() {
      return this.getDataWatcher().getWatchableObjectFloat(17);
   }

   public void clearCrafting() {
   }

   public void resetCraftingProgress() {
   }

   public void sendPacket(Packet packet) {
   }

   public boolean hasExtendedReach() {
      return this.capabilities.isCreativeMode;
   }

   public final float getReach(Block block, int metadata) {
      if (this.hasExtendedReach()) {
         return 5.0F;
      } else {
         float block_reach = 2.75F;
         ItemStack item_stack = this.getHeldItemStack();
         return item_stack == null ? block_reach : block_reach + item_stack.getItem().getReachBonus(block, metadata);
      }
   }

   public float getReach(EnumEntityReachContext context, Entity entity) {
      if (this.hasExtendedReach()) {
         return 5.0F;
      } else {
         float elevation_difference = (float)(this.posY - (double)this.yOffset - (entity.posY - (double)entity.yOffset));
         float height_advantage;
         if (elevation_difference < -0.5F) {
            height_advantage = (elevation_difference + 0.5F) * 0.5F;
            if (height_advantage < -1.0F) {
               height_advantage = -1.0F;
            }
         } else if (elevation_difference > 0.5F) {
            height_advantage = (elevation_difference - 0.5F) * 0.5F;
            if (height_advantage > 1.0F) {
               height_advantage = 1.0F;
            }
         } else {
            height_advantage = 0.0F;
         }

         ItemStack item_stack = this.getHeldItemStack();
         if (context == EnumEntityReachContext.FOR_MELEE_ATTACK) {
            return entity.adjustPlayerReachForAttacking(this, 1.5F + height_advantage + (item_stack == null ? 0.0F : item_stack.getItem().getReachBonus()));
         } else if (context == EnumEntityReachContext.FOR_INTERACTION) {
            return entity.adjustPlayerReachForInteraction(this, 2.5F + height_advantage + (item_stack == null ? 0.0F : item_stack.getItem().getReachBonus(entity)));
         } else {
            Minecraft.setErrorMessage("getReach: invalid context");
            return 0.0F;
         }
      }
   }

   public Vec3[] getBlockReachFromPoints(float partial_tick_time) {
      Vec3 camera_position = this.getEyePosition(partial_tick_time);
      Vec3 player_center = camera_position.addVector(0.0, -0.25, 0.0);
      Vec3 player_center_lower = player_center.addVector(0.0, -0.375, 0.0);
      Vec3 player_center_upper = player_center.addVector(0.0, 0.0, 0.0);
      return new Vec3[]{player_center_lower, player_center_upper};
   }

   public final RaycastCollision getSelectedObject(float partial_tick, boolean hit_liquids) {
      return this.getSelectedObject(partial_tick, hit_liquids, false, (EnumEntityReachContext)null);
   }

   public final RaycastCollision getSelectedObject(float partial_tick, boolean hit_liquids, boolean include_non_collidable_entities, EnumEntityReachContext entity_reach_context) {
      if (!this.inBed() && !this.isGhost() && !this.isZevimrgvInTournament()) {
         double reach_distance_limit = 16.0;
         RaycastPolicies policies = RaycastPolicies.for_selection(hit_liquids);
         if (include_non_collidable_entities) {
            policies = policies.getMutableCopy().setIncludeNonCollidableEntities(include_non_collidable_entities);
         }

         Raycast raycast = (new Raycast(this, partial_tick, 16.0)).setPolicies(policies).setForPlayerSelection().performVsBlocksAndEntities();
         return raycast.getNearestCollisionReachableByObserver(entity_reach_context, partial_tick);
      } else {
         return null;
      }
   }

   public boolean canReachEntity(RaycastCollision rc, EnumEntityReachContext context) {
      return rc.getDistanceFromOriginToCollisionPoint() <= (double)this.getReach(context, rc.getEntityHit());
   }

   public void dismountEntity(Entity par1Entity) {
      if (!this.worldObj.isRemote) {
         for(int i = 0; i < 2; ++i) {
            RaycastCollision rc = this.getSelectedObject(1.0F, i > 0, false, (EnumEntityReachContext)null);
            if (rc != null && rc.isBlock()) {
               int x;
               int y;
               int z;
               if ((double)((float)rc.block_hit_y - 0.5F) < this.posY && this.worldObj.isAirOrPassableBlock(rc.block_hit_x, rc.block_hit_y + 1, rc.block_hit_z, true)) {
                  x = rc.block_hit_x;
                  y = rc.block_hit_y + 1;
                  z = rc.block_hit_z;
               } else if (this.worldObj.isAirOrPassableBlock(rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, true)) {
                  x = rc.block_hit_x;
                  y = rc.block_hit_y;
                  z = rc.block_hit_z;
               } else {
                  x = rc.neighbor_block_x;
                  y = rc.neighbor_block_y;
                  z = rc.neighbor_block_z;
               }

               if ((x != par1Entity.getBlockPosX() || y != par1Entity.getBlockPosY() || z != par1Entity.getBlockPosZ()) && this.worldObj.isAirOrPassableBlock(x, y, z, true)) {
                  this.setPositionAndUpdate((double)((float)x + 0.5F), (double)((float)y + 0.2F), (double)((float)z + 0.5F));
                  return;
               }
            }
         }
      }

      super.dismountEntity(par1Entity);
   }

   public boolean isEntityInvulnerable() {
      return !this.isGhost() && !this.isZevimrgvInTournament() ? super.isEntityInvulnerable() : true;
   }

   public final boolean isGhost() {
      if (!this.has_dedicated_server_ghost_check_been_made) {
         this.is_dedicated_server_ghost = this.username.equals("Dedicated_Server");
         this.has_dedicated_server_ghost_check_been_made = true;
      }

      return this.is_dedicated_server_ghost;
   }

   public final void applyEntityCollision(Entity par1Entity) {
      if (!this.isGhost() && !this.isZevimrgvInTournament()) {
         super.applyEntityCollision(par1Entity);
      }
   }

   public ItemStack[] getWornItems() {
      return this.inventory.armorInventory;
   }

   public boolean setWornItem(int slot_index, ItemStack item_stack) {
      return this.inventory.setWornItem(slot_index, item_stack);
   }

   public boolean isLockedInFirstPersonView() {
      return this.isGhost() || this.isWearingPumpkinHelmet();
   }

   public boolean isLocalClient() {
      return this == Minecraft.getClientPlayer();
   }

   public boolean drawBackFaces() {
      return !this.isGhost() || this.isWearingItems(true);
   }

   public boolean hasItems() {
      int i;
      for(i = 0; i < this.inventory.mainInventory.length; ++i) {
         if (this.inventory.mainInventory[i] != null) {
            return true;
         }
      }

      for(i = 0; i < this.inventory.armorInventory.length; ++i) {
         if (this.inventory.armorInventory[i] != null) {
            return true;
         }
      }

      return false;
   }

   public Curse getCurse() {
      return this.worldObj.isRemote ? Curse.cursesList[this.curse_id] : ((WorldServer)this.worldObj).getCurseForPlayer(this);
   }

   public boolean hasCurse(Curse curse, boolean learn_effect_if_so) {
      boolean has_curse = this.is_cursed && this.curse_id == curse.id;
      if (has_curse && learn_effect_if_so && !this.curse_effect_known) {
         this.learnCurseEffect();
      }

      return has_curse;
   }

   public boolean hasCurse(Curse curse) {
      return this.hasCurse(curse, false);
   }

   public void learnCurseEffect() {
      if (this.is_cursed && this.curse_id != 0) {
         if (this.worldObj.isRemote) {
            this.curse_effect_known = true;
            this.sendPacket(new Packet85SimpleSignal(EnumSignal.curse_effect_learned));
         } else {
            this.getCurse().effect_known = true;
         }

      } else {
         Minecraft.setErrorMessage("learnCurseEffect: player is not cursed!");
      }
   }

   public void onCurseRealized(int curse_id) {
      if (curse_id == Curse.cannot_wear_armor.id && !this.worldObj.isRemote && this.inventory.dropAllArmor()) {
         this.learnCurseEffect();
      }

   }

   public void setAir(int par1) {
      if (this.hasCurse(Curse.cannot_hold_breath) && par1 > 90) {
         par1 = 90;
      }

      super.setAir(par1);
   }

   public boolean inCreativeMode() {
      return this.capabilities != null && this.capabilities.isCreativeMode;
   }

   public EnumQuality getMinCraftingQuality(Item item, int[] applicable_skillsets) {
      if (!this.worldObj.areSkillsEnabled()) {
         applicable_skillsets = null;
      }

      int effective_experience_level = this.getExperienceLevel();
      if (this.hasCurse(Curse.clumsiness, true)) {
         effective_experience_level -= 20;
      }

      EnumQuality quality = EnumQuality.get(MathHelper.clamp_int(EnumQuality.average.ordinal() + effective_experience_level / 10, 0, EnumQuality.average.ordinal()));
      return applicable_skillsets != null && !this.hasAnyOfTheseSkillsets(applicable_skillsets) ? EnumQuality.getLowest(quality, EnumQuality.poor) : quality;
   }

   public EnumQuality getMaxCraftingQuality(float unadjusted_crafting_difficulty_to_produce, Item item, int[] applicable_skillsets) {
      if (!this.worldObj.areSkillsEnabled()) {
         applicable_skillsets = null;
      }

      if (this.experience <= 0) {
         return this.getMinCraftingQuality(item, applicable_skillsets);
      } else if (applicable_skillsets != null && !this.hasAnyOfTheseSkillsets(applicable_skillsets)) {
         return this.getMinCraftingQuality(item, applicable_skillsets);
      } else {
         if (item.getLowestCraftingDifficultyToProduce() == Float.MAX_VALUE) {
            Minecraft.setErrorMessage("getMaxCraftingQuality: item has no recipes! " + item.getItemDisplayName((ItemStack)null));
         }

         for(int i = item.getMaxQuality().ordinal(); i > EnumQuality.average.ordinal(); --i) {
            if (this.getCraftingExperienceCost(CraftingResult.getQualityAdjustedDifficulty(unadjusted_crafting_difficulty_to_produce, EnumQuality.values()[i])) <= this.experience) {
               return EnumQuality.values()[i];
            }
         }

         return this.getMinCraftingQuality(item, applicable_skillsets);
      }
   }

   public int getCraftingExperienceCost(float quality_adjusted_crafting_difficulty) {
      int cost = Math.round(quality_adjusted_crafting_difficulty / 5.0F);
      if (this.hasCurse(Curse.clumsiness, true)) {
         cost *= 2;
      }

      return cost;
   }

   public boolean isImmuneByGrace() {
      return false;
   }

   public int getFireResistance() {
      return 10;
   }

   public void sendPacketToAssociatedPlayers(Packet packet, boolean include_sender) {
      if (this.worldObj.isRemote) {
         this.sendPacket(new Packet90BroadcastToAssociatedPlayers(packet, include_sender));
      } else {
         WorldServer world_server = (WorldServer)this.worldObj;
         EntityTracker entity_tracker = world_server.getEntityTracker();
         if (include_sender) {
            entity_tracker.sendPacketToAllAssociatedPlayers(this, packet);
         } else {
            entity_tracker.sendPacketToAllPlayersTrackingEntity(this, packet);
         }
      }

   }

   private boolean checkForBlockActivation(RaycastCollision rc) {
      if (rc != null && rc.isBlock()) {
         World world = this.worldObj;
         if (this.isSneaking() && this.hasHeldItem()) {
            return false;
         } else {
            Block block = rc.getBlockHit();
            int x = rc.block_hit_x;
            int y = rc.block_hit_y;
            int z = rc.block_hit_z;
            if (block.onBlockActivated(world, x, y, z, this, rc.face_hit, rc.block_hit_offset_x, rc.block_hit_offset_y, rc.block_hit_offset_z)) {
               if (world.isRemote && !this.rightClickCancelled()) {
                  if (block.playerSwingsOnBlockActivated(!this.hasHeldItem())) {
                     this.swingArm();
                  }

                  Minecraft.theMinecraft.right_click_counter = 10;
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean isHoldingAnEffectiveTool(Block block, int metadata) {
      return ForgeEventFactory.doPlayerHarvestCheck(this, block, super.isHoldingAnEffectiveTool(block, metadata));
   }

   private boolean checkForEntityInteraction(RaycastCollision rc) {
      if (rc != null && rc.isEntity() && rc.getEntityHit().isEntityAlive()) {
         if (!this.canReachEntity(rc, EnumEntityReachContext.FOR_INTERACTION)) {
            return false;
         } else {
            ItemStack item_stack = this.getHeldItemStack();
            if (item_stack != null && item_stack.tryEntityInteraction(rc.getEntityHit(), this)) {
               if (this.onClient() && !this.rightClickCancelled()) {
                  this.swingArm();
               }

               return true;
            } else {
               return rc.getEntityHit().onEntityRightClicked(this, item_stack);
            }
         }
      } else {
         return false;
      }
   }
   @Override
   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if (MinecraftForge.EVENT_BUS.post(new EntityInteractEvent(this, player))) return false;
      return true;
   }

   private boolean checkForIngestion() {
      ItemStack held_item_stack = this.getHeldItemStack();
      if (held_item_stack == null) {
         return false;
      } else {
         EnumItemInUseAction action = held_item_stack.getItemInUseAction(this);
         if (action != null && action.isIngestion()) {
            if (this.canIngest(held_item_stack)) {
               if (this.isLocalClient() && !Minecraft.theMinecraft.playerController.ingestionEnabled()) {
                  this.cancelRightClick();
                  return true;
               } else {
                  this.setHeldItemInUse();
                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private final RightClickFilter onPlayerRightClick(RaycastCollision rc, RightClickFilter filter, float partial_tick, boolean ctrl_is_down) {
      World world = this.worldObj;
      if (this.isLocalClient()) {
         Minecraft.theMinecraft.playerController.syncCurrentPlayItem();
      }

      if (filter.allowsBlockActivation() && this.checkForBlockActivation(rc)) {
         return filter.setExclusive(1);
      } else if (filter.allowsEntityInteraction() && this.checkForEntityInteraction(rc)) {
         return filter.setExclusive(2);
      } else {
         ItemStack held_item_stack = this.getHeldItemStack();
         if (rc == null && held_item_stack == null) {
            return filter.setNoActionAllowed();
         } else {
            if (held_item_stack != null) {
               if (held_item_stack.stackSize < 1) {
                  Minecraft.setErrorMessage("onPlayerRightClick: stack size is " + held_item_stack.stackSize);
                  return filter.setNoActionAllowed();
               }

               if (filter.allowsIngestion() && held_item_stack.hasIngestionPriority(ctrl_is_down) && this.checkForIngestion()) {
                  return filter.setExclusive(4);
               }

               if (filter.allowsOnItemRightClick() && held_item_stack.getItem().onItemRightClick(this, partial_tick, ctrl_is_down)) {
                  return filter.setExclusive(8);
               }

               if (filter.allowsIngestion() && !held_item_stack.hasIngestionPriority(ctrl_is_down) && this.checkForIngestion()) {
                  return filter.setExclusive(4);
               }
            }

            return filter.setNoActionAllowed();
         }
      }
   }

   public void bobItem() {
      if (this.isLocalClient()) {
         Minecraft.theMinecraft.entityRenderer.itemRenderer.resetEquippedProgress();
      } else {
         Minecraft.setErrorMessage("bobItem: only meant to be called on client");
      }

   }

   public final RightClickFilter onPlayerRightClickChecked(RaycastCollision rc, RightClickFilter filter, float partial_tick, boolean ctrl_is_down) {
      if (!this.isEntityAlive()) {
         return filter.setNoActionAllowed();
      } else {
         ItemStack held_item_stack_before = this.getHeldItemStack();
         ItemStack held_item_stack_before_copy = held_item_stack_before == null ? null : held_item_stack_before.copy();
         ItemStack item_in_use_before = this.itemInUse;
         filter = this.onPlayerRightClick(rc, filter, partial_tick, ctrl_is_down);
         if (this.rightClickCancelled()) {
            filter.setNoActionAllowed();
         }

         ItemStack held_item_stack_after = this.getHeldItemStack();
         ItemStack item_in_use_after = this.itemInUse;
         boolean held_item_stack_changed = held_item_stack_after != held_item_stack_before || !ItemStack.areItemStacksEqual(held_item_stack_after, held_item_stack_before_copy);
         if ((held_item_stack_changed || item_in_use_after != item_in_use_before) && filter.allowsNoActions()) {
            Minecraft.setErrorMessage("onPlayerRightClickChecked: onPlayerRightClick returned no action but stack was modified or item was set in use");
         }

         if (this.onClient()) {
            if (held_item_stack_changed) {
               Minecraft.setErrorMessage("onPlayerRightClickChecked: why did held item stack change on client?");
            }
         } else {
            if (held_item_stack_after != held_item_stack_before || held_item_stack_after != null && held_item_stack_after.stackSize < 1) {
               this.setHeldItemStack(held_item_stack_after != null && held_item_stack_after.stackSize >= 1 ? held_item_stack_after : null);
            }

            if (held_item_stack_before_copy != null && held_item_stack_changed && !this.inCreativeMode() && !this.suppress_next_stat_increment) {
               this.addStat(StatList.objectUseStats[held_item_stack_before_copy.itemID], 1);
            } else {
               this.suppress_next_stat_increment = false;
            }
         }

         return filter;
      }
   }

   public void setHeldItemStack(ItemStack item_stack) {
      this.inventory.setInventorySlotContents(this.inventory.currentItem, item_stack);
   }

   public void convertOneOfHeldItem(ItemStack created_item_stack) {
      this.inventory.convertOneOfCurrentItem(created_item_stack);
   }

   public boolean tryPlaceHeldItemAsBlock(RaycastCollision rc, Block block) {
      block.is_being_placed = true;
      boolean result = this.getHeldItem().tryPlaceAsBlock(rc, block, this, this.getHeldItemStack());
      block.is_being_placed = false;
      if (!result) {
         block = block.getAlternativeBlockForPlacement();
         if (block != null) {
            block.is_being_placed = true;
            result = this.getHeldItem().tryPlaceAsBlock(rc, block, this, this.getHeldItemStack());
            block.is_being_placed = false;
         }
      }

      if (result) {
         if (this.onClient()) {
            this.swingArm();
            Minecraft.theMinecraft.playerController.setUseButtonDelayOverride(250);
         } else if (!this.inCreativeMode()) {
            ItemStack stack = new ItemStack(this.getHeldItem());
            this.addHungerServerSide(Math.min(block.getBlockHardness(0), 20.0F) * EnchantmentHelper.getEnduranceModifier(this));
            this.convertOneOfHeldItem((ItemStack)null);

            if (this.getHeldItem() == null) {
               MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(this, stack));
            }
         }
      }

      return result;
   }

   public final boolean setHeldItemInUse() {
      ItemStack item_stack = this.getHeldItemStack();
      if (item_stack == null) {
         Minecraft.setErrorMessage("setHeldItemInUse: no item held");
      } else if (item_stack.getItemInUseAction(this) == null) {
         Minecraft.setErrorMessage("setHeldItemInUse: item has no inUseAction");
      } else if (item_stack != this.itemInUse) {
         this.itemInUse = item_stack;
         this.itemInUseCount = item_stack.getMaxItemUseDuration();
         if (this.onServer() && this.itemInUse.getItemInUseAction(this) == EnumItemInUseAction.EAT) {
            this.setEating(true);
            this.getWorldServer().getEntityTracker().sendPacketToAllAssociatedPlayers(this, new Packet18Animation(this, 5));
         }

         if (this.onServer()) {
            this.sendPacketToAssociatedPlayers((new Packet85SimpleSignal(EnumSignal.item_in_use)).setInteger(this.itemInUseCount).setEntityID(this), false);
         }

         return true;
      }

      return false;
   }

   public PlayerControllerMP getController() {
      return this.isLocalClient() ? Minecraft.theMinecraft.playerController : null;
   }

   public void cancelRightClick() {
      this.cancel_right_click = true;
   }

   public boolean rightClickCancelled() {
      return this.cancel_right_click;
   }

   public void clearRightClickCancelled() {
      this.cancel_right_click = false;
   }

   public boolean canMineAndEditBlock(int x, int y, int z) {
      return this.worldObj.canMineBlock(this, x, y, z) && this.canPlayerEdit(x, y, z, this.getHeldItemStack());
   }

   public final ItemDamageResult tryDamageHeldItem(DamageSource damage_source, int amount) {
      if (this.onClient()) {
         Minecraft.setErrorMessage("tryDamageHeldItem: why calling this on client?");
      }

      if (this.inCreativeMode()) {
         return null;
      } else {
         ItemStack item_stack = this.getHeldItemStack();
         return item_stack == null ? null : item_stack.tryDamageItem(damage_source, amount, this);
      }
   }

   public EntityClientPlayerMP getAsEntityClientPlayerMP() {
      return (EntityClientPlayerMP)this;
   }

   public boolean ownsEntity(EntityOwnable entity_ownable) {
      return this.getCommandSenderName().equalsIgnoreCase(entity_ownable.getOwnerName());
   }

   public void suppressNextArmSwing() {
      if (this.onServer()) {
         Minecraft.setErrorMessage("suppressNextArmSwing: not meant to be called on server");
      }

      this.suppress_next_arm_swing = true;
   }

   public void suppressNextStatIncrement() {
      if (this.onClient()) {
         Minecraft.setErrorMessage("suppressNextStatIncrement: not meant to be called on client");
      }

      this.suppress_next_stat_increment = true;
   }

   public PlayerControllerMP getPlayerController() {
      return this.isLocalClient() ? Minecraft.theMinecraft.playerController : null;
   }

   public boolean isMITEmigo() {
      if (this.username == null) {
         return false;
      } else {
         return this.username.equals("Roninpawn") || this.username.equals("ShadowKnight1234") || this.username.equals("xXLeGoldFishXx");
      }
   }

   public boolean hasUsername(String username) {
      if (username == null) {
         return this.username == null;
      } else {
         return username.equals(this.username);
      }
   }

   public final boolean isZevimrgv() {
      if (!this.zevimrgv_check_made) {
         this.is_zevimrgv = "zevimrgv".equals(StringHelper.mirrorString(this.username));
         this.zevimrgv_check_made = true;
      }

      return this.is_zevimrgv;
   }

   public final boolean isZevimrgvInTournament() {
      return DedicatedServer.isTournament() && this.isZevimrgv();
   }

   public static boolean isZevimrgv(String username) {
      return "zevimrgv".equals(StringHelper.mirrorString(username));
   }

   public void setTentativeBoundingBoxCountdownForClearing(int x, int y, int z, int countdown_for_clearing) {
      Iterator i = this.tentative_bounding_boxes.iterator();

      while(i.hasNext()) {
         TentativeBoundingBox tbb = (TentativeBoundingBox)i.next();
         if (tbb.matches(x, y, z)) {
            tbb.countdown_for_clearing = countdown_for_clearing;
         }
      }

   }

   public boolean canOnlyPerformWeakStrike() {
      return !this.isHoldingItemThatPreventsHandDamage() && (this.getHealth() < 2.0F || !this.hasFoodEnergy() || this.getEntityAttributeValue(SharedMonsterAttributes.attackDamage) < 1.0);
   }

   public String getSkillsString(boolean profession_names) {
      return Skill.getSkillsString(this.getSkills(), profession_names, profession_names ? " / " : ", ");
   }

   public int getNumSkills() {
      return Skill.getNumSkills(this.getSkills());
   }

   public boolean hasSkills() {
      return this.getSkills() != 0;
   }

   public boolean hasSkillsForCraftingResult(CraftingResult crafting_result) {
      if (!this.worldObj.areSkillsEnabled()) {
         Minecraft.setErrorMessage("hasSkillsForCraftingResult: skills aren't enabled");
         return true;
      } else {
         return this.hasAnyOfTheseSkillsets(crafting_result.applicable_skillsets);
      }
   }

   public boolean isWearing(ItemStack item_stack) {
      return this.inventory.isWearing(item_stack);
   }

   public final void incrementStatForThisWorldFromClient(StatBase stat) {
      if (this instanceof EntityClientPlayerMP) {
         this.sendPacket((new Packet85SimpleSignal(EnumSignal.increment_stat_for_this_world_only)).setInteger(stat.statId));
      } else if (this instanceof EntityPlayerMP) {
         Minecraft.setErrorMessage("incrementStatForThisWorldFromClient: not meant to be called on server - use incrementStatForThisWorldOnServer instead");
      }

   }

   public final void incrementStatForThisWorldOnServer(StatBase stat) {
      if (this instanceof EntityPlayerMP) {
         this.getAsEntityPlayerMP().addStatForThisWorldOnly(stat, 1);
      } else {
         Minecraft.setErrorMessage("incrementStatForThisWorldOnServer: not meant to be called on client - use incrementStatForThisWorldFromClient instead");
      }

   }

   public final void incrementStatForThisWorldOnServer(int id) {
      this.incrementStatForThisWorldOnServer(StatList.getStat(id));
   }

   public boolean haveAchievementsBeenUnlockedByOtherPlayers() {
      return this.worldObj.worldInfo.haveAchievementsBeenUnlockedByOtherPlayers(this);
   }

   public int getNumItems(Item item) {
      return this.inventory.getNumItems(item);
   }

   public final boolean isPlayerInCreative() {
      return this.inCreativeMode();
   }

   public abstract INetworkManager getNetManager();

   public final boolean isUsingMemoryConnection() {
      return this.getNetManager() instanceof MemoryConnection;
   }

   public boolean canCastFishingRod() {
      if (this.fishEntity != null) {
         return false;
      } else if (this.ridingEntity instanceof EntityBoat) {
         return true;
      } else if (this.ridingEntity instanceof EntityHorse) {
         return true;
      } else if (this.onGround && !this.isSuspendedInLiquid()) {
         return !this.worldObj.getBlockMaterial(this.getBlockPosX(), this.getHeadBlockPosY(), this.getBlockPosZ()).isLiquid();
      } else {
         return false;
      }
   }

   public void setInsulinResistance(int insulin_resistance) {
      this.insulin_resistance = insulin_resistance;
   }

   public int getInsulinResistance() {
      return this.insulin_resistance;
   }

   public EnumInsulinResistanceLevel getInsulinResistanceLevel() {
      return this.insulin_resistance_level;
   }

   public boolean isInsulinResistant() {
      return this.insulin_resistance_level != null;
   }

   public boolean canMetabolizeFoodSugars() {
      return this.insulin_resistance_level == null || this.insulin_resistance_level.canMetabolizeFoodSugars();
   }

   public boolean dealDamageToInventory(DamageSource damage_source, float chance_per_item, float amount, boolean include_worn_items) {
      return this.inventory.takeDamage(damage_source, chance_per_item, amount, include_worn_items);
   }

   public boolean isUpperBodyInWeb() {
      return this.worldObj.isMaterialInBB(this.boundingBox.copy().setMinY(this.getFootPosY() + 1.0), Material.web);
   }

   static {
      int xp = 0;
      int increase = 20;

      for(int level = 1; level < experience_for_level.length; ++level) {
         xp += increase;
         increase += 10;
         experience_for_level[level] = xp;
      }

   }

   /* ===================================== FORGE START =====================================*/

   private String displayname;


   /**
    * Get the currently computed display name, cached for efficiency.
    * @return the current display name
    */
   public String getDisplayName()
   {
      if(this.displayname == null)
      {
         this.displayname = ForgeEventFactory.getPlayerDisplayName(this, this.username);
      }
      return this.displayname;
   }

   /**
    * Force the displayed name to refresh
    */
   public void refreshDisplayName()
   {
      this.displayname = ForgeEventFactory.getPlayerDisplayName(this, this.username);
   }
}
