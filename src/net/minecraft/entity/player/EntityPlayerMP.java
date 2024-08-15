package net.minecraft.entity.player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityDemonSpider;
import net.minecraft.entity.EntityGhoul;
import net.minecraft.entity.EntityHellhound;
import net.minecraft.entity.EntityInvisibleStalker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityShadow;
import net.minecraft.entity.EntityWight;
import net.minecraft.entity.EntityWoodSpider;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemReferencedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemVessel;
import net.minecraft.mite.MITEConstant;
import net.minecraft.mite.TournamentStanding;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.SoonestReconnectionTime;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.network.packet.Packet101CloseWindow;
import net.minecraft.network.packet.Packet103SetSlot;
import net.minecraft.network.packet.Packet104WindowItems;
import net.minecraft.network.packet.Packet105UpdateProgressbar;
import net.minecraft.network.packet.Packet133TileEditorOpen;
import net.minecraft.network.packet.Packet17Sleep;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet200Statistic;
import net.minecraft.network.packet.Packet202PlayerAbilities;
import net.minecraft.network.packet.Packet204ClientInfo;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet29DestroyEntity;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet42RemoveEntityEffect;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet56MapChunks;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.network.packet.Packet8UpdateHealth;
import net.minecraft.network.packet.Packet91PlayerStat;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScoreObjectiveCriteria;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Debug;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumBlockOperation;
import net.minecraft.util.EnumConsciousState;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumInsulinResistanceLevel;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.EnumTournamentType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public final class EntityPlayerMP extends EntityPlayer implements ICrafting {
   private String translator = "en_US";
   public NetServerHandler playerNetServerHandler;
   public MinecraftServer mcServer;
   public ItemInWorldManager theItemInWorldManager;
   public double managedPosX;
   public double managedPosZ;
   public final List loadedChunks = new LinkedList();
   public final List destroyedItemsNetCache = new LinkedList();
   private float field_130068_bO = Float.MIN_VALUE;
   private float lastHealth = -1.0E8F;
   private int last_satiation = -99999999;
   private int last_nutrition = -99999999;
   private int protein;
   private int essential_fats;
   private int phytonutrients;
   private int last_experience = -99999999;
   private int initialInvulnerability = 0;
   private int renderDistance;
   private int chatVisibility;
   private boolean chatColours = true;
   private long field_143005_bX = 0L;
   private int currentWindowId;
   public boolean playerInventoryBeingManipulated;
   public int ping;
   public boolean playerConqueredTheEnd;
   public boolean[] Sr = new boolean[64];
   public boolean raS;
   public int respawn_experience = 0;
   private double pos_x_at_last_world_map_update;
   private double pos_z_at_last_world_map_update;
   public int ticks_logged_in;
   public int[] runegate_destination_coords;
   public boolean sync_client_player_on_next_tick = true;
   public int portal_grace_ticks;
   private boolean is_not_on_hit_list;
   private boolean initial_on_update = true;
   public boolean master_hash_received;
   public boolean master_hash_validated;
   public int sacred_stones_placed;
   public int allotted_time = -1;
   public boolean is_disconnecting_while_in_bed;
   public boolean try_push_out_of_blocks;
   public int last_skill_learned_on_day;
   public short respawn_countdown = -1;
   public List referenced_books_read = new ArrayList();
   public float vision_dimming_on_client;
   public long prevent_item_pickup_due_to_held_item_breaking_until;
   public double last_received_motion_x;
   public double last_received_motion_z;
   public boolean set_position_in_bed_next_tick;

   public EntityPlayerMP(MinecraftServer par1MinecraftServer, World par2World, String par3Str, ItemInWorldManager par4ItemInWorldManager) {
      super(par2World, par3Str);
      par4ItemInWorldManager.thisPlayerMP = this;
      this.theItemInWorldManager = par4ItemInWorldManager;
      this.renderDistance = par1MinecraftServer.getConfigurationManager().getViewDistance();
      ChunkCoordinates var5 = par2World.getSpawnPoint();
      int var6 = var5.posX;
      int var7 = var5.posZ;
      int var8 = var5.posY;
      if (!par2World.provider.hasNoSky && par2World.getWorldInfo().getGameType() != EnumGameType.ADVENTURE) {
         int var9 = Math.max(5, par1MinecraftServer.getSpawnProtectionSize() - 6);
         var6 += this.rand.nextInt(var9 * 2) - var9;
         var7 += this.rand.nextInt(var9 * 2) - var9;
         var8 = par2World.getTopSolidOrLiquidBlockMITE(var6, var7, true);
      }

      this.mcServer = par1MinecraftServer;
      this.stepHeight = 0.0F;
      this.yOffset = 0.0F;
      this.setLocationAndAngles((double)var6 + 0.5, (double)var8, (double)var7 + 0.5, 0.0F, 0.0F);

      while(!par2World.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()) {
         this.setPosition(this.posX, this.posY + 1.0, this.posZ);
      }

      this.protein = this.essential_fats = this.phytonutrients = 160000;
      if (DedicatedServer.tournament_type == EnumTournamentType.score) {
         DedicatedServer.getOrCreateTournamentStanding(this);
      }

      if (DedicatedServer.isTournamentThatUsesAllottedTimes()) {
         this.allotted_time = DedicatedServer.allotted_time;
      }

   }

   private int calcChecksum(int for_release_number) {
      int checksum = 0;
      checksum += this.inventory.calcChecksum(for_release_number) * 2;
      checksum = (int)((float)checksum + this.getHealth() * 3.0F);
      checksum += this.dimension * 5;
      checksum += this.experience * 7;
      checksum += this.getSatiation() * 11;
      checksum += this.getNutrition() * 13;
      checksum = (int)((double)checksum + this.posX * 17.0);
      checksum = (int)((double)checksum + this.posY * 19.0);
      checksum = (int)((double)checksum + this.posZ * 23.0);
      checksum += this.respawn_experience * 29;
      checksum += this.theItemInWorldManager.getGameType().getID() * 31;
      checksum *= 657;
      checksum = Integer.MAX_VALUE - Math.abs(checksum);
      return Math.abs(checksum);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.respawn_experience = par1NBTTagCompound.getInteger("respawn_experience");
      if (par1NBTTagCompound.hasKey("playerGameType")) {
         if (MinecraftServer.getServer().getForceGamemode()) {
            this.theItemInWorldManager.setGameType(MinecraftServer.getServer().getGameType());
         } else {
            this.theItemInWorldManager.setGameType(EnumGameType.getByID(par1NBTTagCompound.getInteger("playerGameType")));
         }
      }

      int i;
      if (par1NBTTagCompound.hasKey("LMRN")) {
         int LMRN = par1NBTTagCompound.getInteger("LMRN");
         if (LMRN < 81) {
            MinecraftServer.setTreacheryDetected();
         }

         i = par1NBTTagCompound.getInteger(this.obf("mzmlgrnv"));
         if (Minecraft.inDevMode()) {
            System.out.println(this.obf("Kozbvi MYG xsvxphfn ezorwzgrlm, xlnkzirmt ") + i + " vs " + this.calcChecksum(LMRN) + " (R" + LMRN + " used)");
         }

         if (i != this.calcChecksum(LMRN)) {
            MinecraftServer.setTreacheryDetected();
         }
      } else if (Minecraft.inDevMode()) {
         System.out.println(this.obf("Hprkkrmt kozbvi xsvxphfn ezorwzgrlm (ONIM mlg ulfmw)"));
      } else {
         MinecraftServer.setTreacheryDetected();
      }

      if (par1NBTTagCompound.hasKey("protein")) {
         this.protein = par1NBTTagCompound.getInteger("protein");
      } else {
         this.protein = 160000;
      }

      if (par1NBTTagCompound.hasKey("essential_fats")) {
         this.essential_fats = par1NBTTagCompound.getInteger("essential_fats");
      } else {
         this.essential_fats = 160000;
      }

      if (par1NBTTagCompound.hasKey("phytonutrients")) {
         this.phytonutrients = par1NBTTagCompound.getInteger("phytonutrients");
      } else {
         this.phytonutrients = 160000;
      }

      this.setInsulinResistance(par1NBTTagCompound.getInteger("insulin_resistance"));
      this.insulin_resistance_level = EnumInsulinResistanceLevel.getByTransmittedOrdinal(par1NBTTagCompound.getByte("insulin_resistance_level"));
      if (DedicatedServer.tournament_type == EnumTournamentType.score) {
         DedicatedServer.getOrCreateTournamentStanding(this).readFromNBT(par1NBTTagCompound).experience = this.experience;
      }

      this.sacred_stones_placed = par1NBTTagCompound.getInteger("sacred_stones_placed");
      if (DedicatedServer.isTournamentThatUsesAllottedTimes()) {
         this.allotted_time = par1NBTTagCompound.getInteger("allotted_time");
         this.allotted_time = (int)((long)this.allotted_time + (this.worldObj.getTotalWorldTime() - par1NBTTagCompound.getLong("last_total_world_time")) / 3L);
         if (this.allotted_time > DedicatedServer.allotted_time) {
            this.allotted_time = DedicatedServer.allotted_time;
         }
      }

      if (par1NBTTagCompound.getBoolean("disconnected_while_in_bed")) {
         this.pos_x_before_bed = par1NBTTagCompound.getDouble("pos_x_before_bed");
         this.pos_y_before_bed = par1NBTTagCompound.getDouble("pos_y_before_bed");
         this.pos_z_before_bed = par1NBTTagCompound.getDouble("pos_z_before_bed");
         this.setPosition(this.pos_x_before_bed, this.pos_y_before_bed, this.pos_z_before_bed);
         this.try_push_out_of_blocks = true;
      }

      this.setSkills(par1NBTTagCompound.getInteger("skills"));
      this.last_skill_learned_on_day = par1NBTTagCompound.getInteger("last_skill_learned_on_day");
      if (par1NBTTagCompound.hasKey("respawn_countdown")) {
         this.respawn_countdown = par1NBTTagCompound.getShort("respawn_countdown");
      } else {
         this.respawn_countdown = 120;
      }

      if (par1NBTTagCompound.hasKey("stats")) {
         this.readStatsFromNBT(par1NBTTagCompound.getCompoundTag("stats"));
      }

      if (par1NBTTagCompound.hasKey("referenced_books_read")) {
         int[] rbr = par1NBTTagCompound.getIntArray("referenced_books_read");
         this.referenced_books_read.clear();

         for(i = 0; i < rbr.length; ++i) {
            this.referenced_books_read.add(rbr[i]);
         }
      }

      if (par1NBTTagCompound.hasKey("vision_dimming_on_client")) {
         this.vision_dimming = par1NBTTagCompound.getFloat("vision_dimming_on_client");
      }

      if (par1NBTTagCompound.hasKey("ticks_since_portal_teleport")) {
         this.ticks_since_portal_teleport = par1NBTTagCompound.getShort("ticks_since_portal_teleport");
      }

   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("respawn_experience", this.respawn_experience);
      par1NBTTagCompound.setInteger("playerGameType", this.theItemInWorldManager.getGameType().getID());
      par1NBTTagCompound.setInteger("LMRN", 196);
      par1NBTTagCompound.setInteger("nanotime", this.calcChecksum(196));
      par1NBTTagCompound.setInteger("protein", this.protein);
      par1NBTTagCompound.setInteger("essential_fats", this.essential_fats);
      par1NBTTagCompound.setInteger("phytonutrients", this.phytonutrients);
      par1NBTTagCompound.setInteger("insulin_resistance", this.getInsulinResistance());
      if (this.isInsulinResistant()) {
         par1NBTTagCompound.setByte("insulin_resistance_level", (byte)this.getInsulinResistanceLevel().getOrdinalForTransmission());
      }

      if (DedicatedServer.tournament_type == EnumTournamentType.score) {
         DedicatedServer.getOrCreateTournamentStanding(this).writeToNBT(par1NBTTagCompound);
      }

      par1NBTTagCompound.setInteger("sacred_stones_placed", this.sacred_stones_placed);
      par1NBTTagCompound.setInteger("allotted_time", this.allotted_time);
      par1NBTTagCompound.setLong("last_total_world_time", this.worldObj.getTotalWorldTime());
      par1NBTTagCompound.setBoolean("disconnected_while_in_bed", this.is_disconnecting_while_in_bed);
      if (this.is_disconnecting_while_in_bed) {
         par1NBTTagCompound.setDouble("pos_x_before_bed", this.pos_x_before_bed);
         par1NBTTagCompound.setDouble("pos_y_before_bed", this.pos_y_before_bed);
         par1NBTTagCompound.setDouble("pos_z_before_bed", this.pos_z_before_bed);
      }

      if (this.hasSkills()) {
         par1NBTTagCompound.setInteger("skills", this.getSkills());
      }

      if (this.last_skill_learned_on_day != 0) {
         par1NBTTagCompound.setInteger("last_skill_learned_on_day", this.last_skill_learned_on_day);
      }

      par1NBTTagCompound.setShort("respawn_countdown", this.respawn_countdown);
      if (!par1NBTTagCompound.hasKey("stats")) {
         par1NBTTagCompound.setTag("stats", new NBTTagCompound());
      }

      this.writeStatsToNBT(par1NBTTagCompound.getCompoundTag("stats"));
      if (this.referenced_books_read.size() > 0) {
         int[] rbr = new int[this.referenced_books_read.size()];

         for(int i = 0; i < rbr.length; ++i) {
            rbr[i] = (Integer)this.referenced_books_read.get(i);
         }

         par1NBTTagCompound.setIntArray("referenced_books_read", rbr);
      }

      if (this.vision_dimming_on_client >= 0.2F) {
         par1NBTTagCompound.setFloat("vision_dimming_on_client", this.vision_dimming_on_client);
      }

      if (this.ticks_since_portal_teleport < 24000) {
         par1NBTTagCompound.setShort("ticks_since_portal_teleport", (short)this.ticks_since_portal_teleport);
      }

   }

   public boolean hasReadReferencedBook(int index) {
      return this.referenced_books_read.contains(index);
   }

   public void addToReferencedBooksRead(ItemStack item_stack) {
      int index = ItemReferencedBook.getReferenceIndex(item_stack);
      if (!this.hasReadReferencedBook(index)) {
         this.referenced_books_read.add(index);
         int xp_reward = ItemReferencedBook.getXPReward(item_stack);
         if (xp_reward != 0) {
            this.addExperience(xp_reward);
         }

         boolean has_read_the_collected_works_of_father_phoonzang = true;

         for(int i = 1; i <= 9; ++i) {
            if (!this.hasReadReferencedBook(i)) {
               has_read_the_collected_works_of_father_phoonzang = false;
               break;
            }
         }

         if (has_read_the_collected_works_of_father_phoonzang) {
            this.triggerAchievement(AchievementList.enlightenment);
         }
      }

   }

   public void addSelfToInternalCraftingInventory() {
      this.openContainer.addCraftingToCrafters(this);
   }

   protected void resetHeight() {
      this.yOffset = 0.0F;
   }

   public float getEyeHeight() {
      return this.isSneaking() ? 1.3815F : 1.62F;
   }

   public double getFootPosY() {
      return this.posY;
   }

   public double getEyePosY() {
      return this.posY + (double)this.getEyeHeight();
   }

   public void decrementNutrients() {
      if (!this.inCreativeMode()) {
         if (this.protein > 0) {
            --this.protein;
         }

         if (this.essential_fats > 0) {
            --this.essential_fats;
         }

         if (this.phytonutrients > 0) {
            --this.phytonutrients;
         }

      }
   }

   public void decrementInsulinResistance() {
      if (!this.inCreativeMode()) {
         int insulin_resistance = this.getInsulinResistance();
         if (insulin_resistance > 0) {
            --insulin_resistance;
            this.setInsulinResistance(insulin_resistance);
         }

      }
   }

   public void setProtein(int protein) {
      this.protein = MathHelper.clamp_int(protein, 0, 160000);
   }

   public void setEssentialFats(int essential_fats) {
      this.essential_fats = MathHelper.clamp_int(essential_fats, 0, 160000);
   }

   public void setPhytonutrients(int phytonutrients) {
      this.phytonutrients = MathHelper.clamp_int(phytonutrients, 0, 160000);
   }

   public void setInsulinResistance(int insulin_resistance) {
      insulin_resistance = MathHelper.clamp_int(insulin_resistance, 0, 192000);
      super.setInsulinResistance(insulin_resistance);
      if (insulin_resistance == 0) {
         this.insulin_resistance_level = null;
      } else {
         EnumInsulinResistanceLevel insulin_resistance_level = EnumInsulinResistanceLevel.getInsulinResistanceLevel(insulin_resistance);
         if (insulin_resistance_level == null) {
            if (this.insulin_resistance_level != null) {
               this.insulin_resistance_level = EnumInsulinResistanceLevel.mild;
            }
         } else if (this.insulin_resistance_level == null) {
            this.insulin_resistance_level = insulin_resistance_level;
         } else if (this.insulin_resistance_level.isLessSevereThan(insulin_resistance_level)) {
            this.insulin_resistance_level = insulin_resistance_level;
         } else if (this.insulin_resistance_level.isMoreSevereThan(insulin_resistance_level)) {
            this.insulin_resistance_level = insulin_resistance_level.getNext();
         }
      }

   }

   public int getProtein() {
      return this.protein;
   }

   public int getEssentialFats() {
      return this.essential_fats;
   }

   public int getPhytonutrients() {
      return this.phytonutrients;
   }

   public void addNutrients(Item item) {
      if (item == Item.seeds) {
         this.addEssentialFats(2000);
      }

      this.addProtein(item.getProtein());
      this.addEssentialFats(item.getEssentialFats());
      this.addPhytonutrients(item.getPhytonutrients());
   }

   public void addProtein(int protein) {
      this.setProtein(this.protein + protein);
   }

   public void addEssentialFats(int essential_fats) {
      this.setEssentialFats(this.essential_fats + essential_fats);
   }

   public void addPhytonutrients(int phytonutrients) {
      this.setPhytonutrients(this.phytonutrients + phytonutrients);
   }

   public void addInsulinResistance(int insulin_resistance) {
      this.setInsulinResistance(this.getInsulinResistance() + insulin_resistance);
      if (insulin_resistance > 0 && this.isInsulinResistant()) {
         this.addPotionEffect(new PotionEffect(Potion.confusion.id, 400, this.getInsulinResistanceLevel().ordinal()));
         if (this.getInsulinResistanceLevel().isSevere()) {
            this.addPotionEffect(new PotionEffect(Potion.poison.id, Math.max((int)((float)insulin_resistance / 48.0F), 100), 0));
         }
      }

   }

   protected int pushOutOfBlocks() {
      int result = super.pushOutOfBlocks();
      if (result == 1) {
         this.setPositionAndUpdate(this.posX, this.posY, this.posZ);
      }

      return result;
   }

   public void updateRespawnCountdown() {
      if (this.respawn_countdown > 0 && this.worldObj.getTotalWorldTime() % 20L == 0L) {
         --this.respawn_countdown;
      }

   }

   public void sendAllStatsToClient() {
      Iterator i = this.stats.entrySet().iterator();

      while(i.hasNext()) {
         Map.Entry entry = (Map.Entry)i.next();
         int id = (Integer)entry.getKey();
         StatBase stat = StatList.getStat(id);
         if (StatList.isEitherZeroOrOne(stat)) {
            this.sendPacket(new Packet91PlayerStat(stat, (long)(Byte)entry.getValue()));
         } else if (StatList.hasLongValue(stat)) {
            this.sendPacket(new Packet91PlayerStat(stat, (Long)entry.getValue()));
         } else {
            this.sendPacket(new Packet91PlayerStat(stat, (long)(Integer)entry.getValue()));
         }
      }

   }

   private void tickPlayerInventory() {
      boolean players_eyes_inside_water = this.isInsideOfMaterial(Material.water);
      boolean steam_and_hiss = false;

      for(int i = 0; i < this.inventory.mainInventory.length; ++i) {
         ItemStack item_stack = this.inventory.getInventorySlotContents(i);
         if (item_stack != null) {
            Item item = item_stack.getItem();
            if (players_eyes_inside_water && item instanceof ItemVessel && item instanceof ItemBucket) {
               ItemBucket bucket = (ItemBucket)item;
               if (bucket.contains(Material.lava)) {
                  this.inventory.convertAllItemsInSlot(i, bucket.getPeerForContents(Material.stone));
                  steam_and_hiss = true;
               }
            }
         }
      }

      if (steam_and_hiss) {
         this.entityFX(EnumEntityFX.steam_with_hiss);
      }

   }

   private boolean isUnderworldBottomBedrockVisible() {
      if (this.worldObj.isUnderworld() && this.getFootBlockPosY() <= 20) {
         int raycast_seed_offset = this.raycast_seed_offset;
         this.raycast_seed_offset = this.worldObj.getTimeOfDay();
         boolean result = false;

         for(int i = 0; i < 1; ++i) {
            float rotationYaw = this.rotationYaw;
            float rotationPitch = this.rotationPitch;
            this.rotationYaw = (float)((double)this.rotationYaw + (Math.random() * 181.0 - 90.0));
            this.rotationPitch = (float)((double)this.rotationPitch + (Math.random() * 181.0 - 90.0));
            Raycast raycast = new Raycast(this, 1.0F, 16.0);
            raycast.setForVision(false);
            RaycastCollision rc = raycast.performVsBlocksSingle().getBlockCollision();
            this.rotationYaw = rotationYaw;
            this.rotationPitch = rotationPitch;
            if (rc != null && rc.isBlock() && rc.getBlockHit() == Block.mantleOrCore && rc.block_hit_y < 5) {
               result = true;
               break;
            }
         }

         this.raycast_seed_offset = raycast_seed_offset;
         return result;
      } else {
         return false;
      }
   }

   public void onUpdate() {
      if (this.in_test_mode) {
         if (this.ticksExisted % 20 == 0) {
            this.worldObj.sendPacketToAllAssociatedPlayers(this, (new Packet85SimpleSignal(EnumSignal.boolean_test)).setBoolean(true));
            this.worldObj.sendPacketToAllAssociatedPlayers(this, (new Packet85SimpleSignal(EnumSignal.byte_test)).setByte(3));
            this.worldObj.sendPacketToAllAssociatedPlayers(this, (new Packet85SimpleSignal(EnumSignal.short_test)).setShort(42));
            this.worldObj.sendPacketToAllAssociatedPlayers(this, (new Packet85SimpleSignal(EnumSignal.integer_test)).setInteger(101));
            this.worldObj.sendPacketToAllAssociatedPlayers(this, (new Packet85SimpleSignal(EnumSignal.float_test)).setFloat(0.2F));
            this.worldObj.sendPacketToAllAssociatedPlayers(this, (new Packet85SimpleSignal(EnumSignal.complex_test)).setByte(3).setShort(42).setInteger(101).setFloat(0.2F));
         }

         if (this.ticksExisted % 20 == 0) {
            this.worldObj.sendPacketToAllAssociatedPlayers(this, (new Packet85SimpleSignal(EnumSignal.approx_pos_test)).setApproxPosition(this.posX, this.posY + 2.0, this.posZ));
         }

         if (this.ticksExisted % 20 == 10) {
            this.worldObj.sendPacketToAllAssociatedPlayers(this, (new Packet85SimpleSignal(EnumSignal.exact_pos_test)).setExactPosition(this.posX, this.posY + 2.0, this.posZ));
         }

         if (this.ticksExisted % 60 == 0) {
            this.worldObj.blockFX(EnumBlockFX.lava_mixing_with_water, this.getBlockPosX(), this.getFootBlockPosY() - 1, this.getBlockPosZ());
         }

         if (this.ticksExisted % 20 == 0) {
            this.entityFX(EnumEntityFX.curse_effect_learned);
         }

         if (this.ticksExisted % 200 == 0) {
            this.causeBreakingItemEffect(Item.ingotCopper, -1);
         }
      }

      if (this.set_position_in_bed_next_tick) {
         if (this.inBed()) {
            this.setSizeProne();
            this.setPositionAndRotationInBed();
         }

         this.set_position_in_bed_next_tick = false;
      }

      if (this.try_push_out_of_blocks && this.ticksExisted > 1) {
         if (this.pushOutOfBlocks() == -1) {
         }

         this.try_push_out_of_blocks = false;
      }

      if (this.username != null && this.isZevimrgvInTournament()) {
         this.capabilities.allowFlying = true;
      }

      if (Minecraft.inDevMode() || this.isZevimrgvInTournament()) {
         int load = (int)(this.mcServer.getLoadOnServer() * 100.0F);
         this.sendPacket((new Packet85SimpleSignal(EnumSignal.server_load)).setShort(load < 1000 ? load : 1000));
      }

      if (this.initial_on_update) {
         if (DedicatedServer.tournament_type == EnumTournamentType.score) {
            DedicatedServer.generatePrizeKeyFile(this);
            DedicatedServer.updateTournamentScoreOnClient(this, false);
         } else if (DedicatedServer.isTournament()) {
            DedicatedServer.generatePrizeKeyFile(this);
         }

         this.initial_on_update = false;
      }

      if (this.ticksExisted % 24 == 0) {
         SoonestReconnectionTime srt = DedicatedServer.getSoonestReconnectionTime(this.username);
         if (srt != null && srt.ticks_disconnected > 0L) {
            --srt.ticks_disconnected;
         }
      }

      if (!this.master_hash_received && this.ticks_logged_in >= 20 && this.ticks_logged_in % 20 == 0) {
         if (this.ticks_logged_in < 1000) {
            this.sendPacket((new Packet85SimpleSignal(EnumSignal.mh)).setInteger((int)this.worldObj.getSeed()));
         } else {
            this.mcServer.getLogAgent().logWarning(this.username + " never sent a master hash!");
            this.master_hash_received = true;
         }
      }

      this.sendPacket((new Packet85SimpleSignal(EnumSignal.malnourished)).setInteger((this.protein == 0 ? 1 : 0) | (this.phytonutrients == 0 ? 4 : 0) | EnumInsulinResistanceLevel.getOrdinalForTransmission(this.insulin_resistance_level) << 3 | this.getInsulinResistance() << 8));
      if (this.sync_client_player_on_next_tick) {
         this.syncClientPlayer();
         this.sync_client_player_on_next_tick = false;
      }

      this.updateMinecartFuelAmounts();
      ++this.ticks_logged_in;
      if (this.portal_grace_ticks > 0) {
         --this.portal_grace_ticks;
      }

      if (this.isUnderworldBottomBedrockVisible()) {
         this.triggerAchievement(AchievementList.portalToNether);
      }

      if (this.itemInUse != null && this.itemInUse.getItem() instanceof ItemBow) {
         this.addHungerServerSide(0.01F * EnchantmentHelper.getEnduranceModifier(this));
      }

      WorldServer world = (WorldServer)this.worldObj;
      this.foodStats.onUpdate(this);
      float chance_of_snow_items_melting = Item.getChanceOfSnowAndIceItemsMelting(this.isBurning() ? 20.0F : this.getBiome().temperature);
      int var1;
      if (chance_of_snow_items_melting > 0.0F) {
         for(var1 = 0; var1 < this.inventory.mainInventory.length; ++var1) {
            ItemStack item_stack = this.inventory.getInventorySlotContents(var1);
            if (item_stack != null && (item_stack.hasMaterial(Material.snow, true) || item_stack.hasMaterial(Material.craftedSnow, true) || item_stack.hasMaterial(Material.ice, true)) && item_stack.subjectToChanceOfDisappearing(chance_of_snow_items_melting, this.rand).stackSize < 1) {
               this.inventory.setInventorySlotContents(var1, (ItemStack)null);
            }
         }
      }

      this.tickPlayerInventory();
      int x;
      int y;
      int z;
      int dy;
      int dx;
      int dz;
      ArrayList var6;
      if (this.ticksExisted % 100 == 0) {
         if (Math.random() < 0.009999999776482582 && this.isOnHitList() && world.isThundering(true) && world.canLightningStrikeAt(this.getBlockPosX(), this.getBlockPosY(), this.getBlockPosZ())) {
            world.addWeatherEffect(new EntityLightningBolt(world, this.posX, this.posY, this.posZ));
            this.tryDamageArmor(DamageSource.generic, 20.0F, (EntityDamageResult)null);
            this.attackEntityFrom(new Damage(DamageSource.divine_lightning, 20.0F));
         }

         if (this.ticksExisted % 1000 == 0) {
            var6 = null;
            int blockPosX = this.getBlockPosX();
            blockPosX = this.getBlockPosY();
            y = this.getBlockPosZ();
            z = 5;
            int ranged_squared = z * z;

            for(dy = -z; dy <= z; ++dy) {
               for(dx = -z; dx <= z; ++dx) {
                  for(dz = -z; dz <= z; ++dz) {
                     int block_x = blockPosX + dy;
                     int block_y = blockPosX + dz;
                     int block_z = y + dx;
                     if (world.isAirOrPassableBlock(block_x - 1, block_y, block_z, false) || world.isAirOrPassableBlock(block_x + 1, block_y, block_z, false) || world.isAirOrPassableBlock(block_x, block_y, block_z - 1, false) || world.isAirOrPassableBlock(block_x, block_y, block_z + 1, false)) {
                        float block_center_x = (float)block_x + 0.5F;
                        float block_center_y = (float)block_y + 0.5F;
                        float block_center_z = (float)block_z + 0.5F;
                        if (this.canEntityBeSeenFrom((double)(block_center_x - 1.0F), (double)block_center_y, (double)block_center_z, (double)ranged_squared) || this.canEntityBeSeenFrom((double)(block_center_x + 1.0F), (double)block_center_y, (double)block_center_z, (double)ranged_squared) || this.canEntityBeSeenFrom((double)block_center_x, (double)(block_center_y - 1.0F), (double)block_center_z, (double)ranged_squared) || this.canEntityBeSeenFrom((double)block_center_x, (double)(block_center_y + 1.0F), (double)block_center_z, (double)ranged_squared) || this.canEntityBeSeenFrom((double)block_center_x, (double)block_center_y, (double)(block_center_z - 1.0F), (double)ranged_squared) || this.canEntityBeSeenFrom((double)block_center_x, (double)block_center_y, (double)(block_center_z + 1.0F), (double)ranged_squared)) {
                           Block block = this.worldObj.getBlock(block_x, block_y, block_z);
                           if (block instanceof BlockSilverfish && (!this.worldObj.isOverworld() || this.worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, block_x - 1, block_y, block_z) <= 7 && this.worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, block_x + 1, block_y, block_z) <= 7 && this.worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, block_x, block_y + 1, block_z) <= 7 && this.worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, block_x, block_y, block_z - 1) <= 7 && this.worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, block_x, block_y, block_z + 1) <= 7)) {
                              if (var6 == null) {
                                 var6 = new ArrayList();
                              }

                              var6.add(new ChunkPosition(block_x, block_y, block_z));
                           }
                        }
                     }
                  }
               }
            }

            if (var6 != null) {
               Iterator i = var6.iterator();

               while(i.hasNext()) {
                  ChunkPosition block_pos = (ChunkPosition)i.next();
                  if (!this.worldObj.getAsWorldServer().doesQueuedBlockOperationExist(block_pos.x, block_pos.y, block_pos.z, EnumBlockOperation.spawn_silverfish)) {
                     dz = Entity.obtainNextEntityID();
                     world.watchAnimal(dz, block_pos.x, block_pos.y, block_pos.z, 4);
                     world.playAuxSFX(2001, block_pos.x, block_pos.y, block_pos.z, Block.silverfish.blockID + (world.getBlockMetadata(block_pos.x, block_pos.y, block_pos.z) << 12));
                     this.worldObj.getAsWorldServer().addScheduledBlockOperation(EnumBlockOperation.spawn_silverfish, block_pos.x, block_pos.y, block_pos.z, this.worldObj.getTotalWorldTime() + 20L, false, this);
                  }
               }
            }
         }
      }

      if (this.ticksExisted >= 60) {
         label352: {
            if (this.ticksExisted != 60) {
               World var10000 = this.worldObj;
               if (!(World.getDistanceSqFromDeltas(this.posX - this.pos_x_at_last_world_map_update, 0.0, this.posZ - this.pos_z_at_last_world_map_update) > 16.0)) {
                  break label352;
               }
            }

            if (!this.isZevimrgvInTournament()) {
               this.pos_x_at_last_world_map_update = this.posX;
               this.pos_z_at_last_world_map_update = this.posZ;
               world.addWorldMapSurvey(this.getBlockPosX(), this.getBlockPosZ(), 16, false);
            }
         }
      }

      if (this.initialInvulnerability > 0) {
         --this.initialInvulnerability;
      }

      this.openContainer.detectAndSendChanges();
      if (!this.worldObj.isRemote && !this.openContainer.canInteractWith(this)) {
         this.closeScreen();
         this.openContainer = this.inventoryContainer;
      }

      while(!this.destroyedItemsNetCache.isEmpty()) {
         var1 = Math.min(this.destroyedItemsNetCache.size(), 127);
         int[] var2 = new int[var1];
         Iterator var3 = this.destroyedItemsNetCache.iterator();
         y = 0;

         while(var3.hasNext() && y < var1) {
            var2[y++] = (Integer)var3.next();
            var3.remove();
         }

         this.playerNetServerHandler.sendPacketToPlayer(new Packet29DestroyEntity(var2));
      }

      if (!this.loadedChunks.isEmpty()) {
         if (this.loadedChunks.size() > 500) {
            Debug.setErrorMessage("EntityPlayerMP: loadedChunks.size() > 500");
         }

         var6 = new ArrayList();
         Iterator var7 = this.loadedChunks.iterator();
         ArrayList var8 = new ArrayList();
         long ms = System.currentTimeMillis();

         Chunk var10;
         while(var7.hasNext() && var6.size() < 5) {
            ChunkCoordIntPair var9 = (ChunkCoordIntPair)var7.next();
            if (this.worldObj.doesChunkAndAllNeighborsExist(var9.chunkXPos, var9.chunkZPos, MITEConstant.considerNeighboringChunksInLightingArtifactPrevention() ? 2 : 1, false)) {
               var7.remove();
               var10 = this.worldObj.getChunkFromChunkCoords(var9.chunkXPos, var9.chunkZPos);
               if (var10.isWithinBlockDomain()) {
                  if (MITEConstant.preventLightingArtifacts()) {
                     Packet56MapChunks.checkLighting(var10);
                  }

                  var6.add(var10);
                  var8.addAll(((WorldServer)this.worldObj).getAllTileEntityInBox(var9.chunkXPos * 16, 0, var9.chunkZPos * 16, var9.chunkXPos * 16 + 16, 256, var9.chunkZPos * 16 + 16));
                  if (System.currentTimeMillis() - ms > 10L || MinecraftServer.getServer().getLoadOnServer() > 0.8F) {
                     break;
                  }
               }
            }
         }

         if (!var6.isEmpty()) {
            this.playerNetServerHandler.sendPacketToPlayer(new Packet56MapChunks(var6));
            Iterator var11 = var8.iterator();

            while(var11.hasNext()) {
               TileEntity var5 = (TileEntity)var11.next();
               this.sendTileEntityToPlayer(var5);
            }

            var11 = var6.iterator();

            while(var11.hasNext()) {
               var10 = (Chunk)var11.next();
               this.getServerForPlayer().getEntityTracker().func_85172_a(this, var10);
            }
         }
      }

      if (this.field_143005_bX > 0L && this.mcServer.func_143007_ar() > 0 && MinecraftServer.getSystemTimeMillis() - this.field_143005_bX > (long)(this.mcServer.func_143007_ar() * 1000 * 60)) {
         this.playerNetServerHandler.kickPlayerFromServer("You have been idle for too long!");
      }

      if (DedicatedServer.isTournamentThatUsesAllottedTimes()) {
         if (this.allotted_time > 0) {
            --this.allotted_time;
         }

         if (this.allotted_time == 0 && this.isZevimrgvInTournament()) {
            this.allotted_time = 20;
         }

         if (this.allotted_time == 0) {
            DedicatedServer.players_kicked_for_depleted_time_shares.put(this.username, this.worldObj.getTotalWorldTime());
            this.playerNetServerHandler.kickPlayerFromServer("Time share depleted");
         } else if (this.allotted_time % 20 == 0) {
            this.sendPacket((new Packet85SimpleSignal(EnumSignal.allotted_time)).setInteger(this.allotted_time));
         }
      }

      double distance_sq_to_world_spawn = this.getDistanceSqToWorldSpawnPoint(false);
      if (this.worldObj.isOverworld() && distance_sq_to_world_spawn >= 1.0E8 && distance_sq_to_world_spawn < 4.0E8) {
         this.triggerAchievement(AchievementList.explorer);
      }

      if (this.ticksExisted % 10 == 0 && this.ridingEntity instanceof EntityBoat) {
         x = this.getBlockPosX();
         y = this.getFootBlockPosY();
         z = this.getBlockPosZ();
         boolean eligible = true;

         for(dy = -4; eligible && dy <= -1; ++dy) {
            for(dx = -8; eligible && dx <= 8; ++dx) {
               for(dz = -8; eligible && dz <= 8; ++dz) {
                  if (this.worldObj.getBlockMaterial(x + dx, y + dy, z + dz) != Material.water) {
                     eligible = false;
                     break;
                  }
               }
            }
         }

         if (eligible) {
            this.triggerAchievement(AchievementList.seaworthy);
         }
      }

   }

   public void onUpdateEntity() {
      try {
         super.onUpdate();

         for(int var1 = 0; var1 < this.inventory.getSizeInventory(); ++var1) {
            ItemStack var6 = this.inventory.getStackInSlot(var1);
            if (var6 != null && Item.itemsList[var6.itemID].isMap() && this.playerNetServerHandler.packetSize() <= 5) {
               Packet var8 = ((ItemMapBase)Item.itemsList[var6.itemID]).createMapDataPacket(var6, this.worldObj, this);
               if (var8 != null) {
                  this.playerNetServerHandler.sendPacketToPlayer(var8);
               }
            }
         }

         float health = this.getHealth();
         int satiation = this.getSatiation();
         int nutrition = this.getNutrition();
         float hunger = this.foodStats.getHunger();
         if (health != this.lastHealth || satiation != this.last_satiation || nutrition != this.last_nutrition || this.vision_dimming > 0.0F) {
            this.playerNetServerHandler.sendPacketToPlayer(new Packet8UpdateHealth(health, satiation, nutrition, this.vision_dimming));
            this.lastHealth = health;
            this.last_satiation = satiation;
            this.last_nutrition = nutrition;
            this.vision_dimming = 0.0F;
         }

         if (this.getHealth() + this.getAbsorptionAmount() != this.field_130068_bO) {
            this.field_130068_bO = this.getHealth() + this.getAbsorptionAmount();
            Collection var5 = this.getWorldScoreboard().func_96520_a(ScoreObjectiveCriteria.health);
            Iterator var7 = var5.iterator();

            while(var7.hasNext()) {
               ScoreObjective var9 = (ScoreObjective)var7.next();
               this.getWorldScoreboard().func_96529_a(this.getEntityName(), var9).func_96651_a(Arrays.asList(this));
            }
         }

         if (this.experience != this.last_experience) {
            this.last_experience = this.experience;
            this.playerNetServerHandler.sendPacketToPlayer(new Packet43Experience(this.experience));
         }

      } catch (Throwable var8) {
         Throwable var4 = var8;
         CrashReport var2 = CrashReport.makeCrashReport(var4, "Ticking player");
         CrashReportCategory var3 = var2.makeCategory("Player being ticked");
         this.addEntityCrashInfo(var3);
         throw new ReportedException(var2);
      }
   }

   public void onDeath(DamageSource par1DamageSource) {
      this.mcServer.getConfigurationManager().sendChatMsg(this.func_110142_aN().func_94546_b());
      if (!this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
         this.inventory.dropAllItems();
      }

      Collection var2 = this.worldObj.getScoreboard().func_96520_a(ScoreObjectiveCriteria.deathCount);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         ScoreObjective var4 = (ScoreObjective)var3.next();
         Score var5 = this.getWorldScoreboard().func_96529_a(this.getEntityName(), var4);
         var5.func_96648_a();
      }

      EntityLivingBase var6 = this.func_94060_bK();
      if (var6 != null) {
         var6.addToPlayerScore(this, this.scoreValue);
      }

      this.addStat(StatList.deathsStat, 1);
      if (this.experience < getExperienceRequired(1)) {
         this.respawn_experience = this.experience - getExperienceRequired(1);
         if (this.respawn_experience < getExperienceRequired(-40)) {
            this.respawn_experience = getExperienceRequired(-40);
         }

         if (DedicatedServer.tournament_type == EnumTournamentType.score) {
            DedicatedServer.getOrCreateTournamentStanding(this).experience = this.respawn_experience;
            DedicatedServer.updateTournamentScoreOnClient(this, true);
         }
      }

      this.respawn_countdown = 120;
      if (this.mcServer instanceof IntegratedServer) {
         ((IntegratedServer)this.mcServer).saveAllPlayersAndWorlds();
      }

   }

   public void afterRespawn() {
      MinecraftServer.getServerConfigurationManager(this.mcServer).sendPlayerInfoToAllPlayers(true);
      this.sendPacket(new Packet85SimpleSignal(EnumSignal.after_respawn));
      this.initialInvulnerability = 60;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      boolean var3 = this.mcServer.isDedicatedServer() && this.mcServer.isPVPEnabled() && "fall".equals(damage.getSource().damageType);
      if (!var3 && this.initialInvulnerability > 0 && damage.getSource() != DamageSource.outOfWorld) {
         return null;
      } else {
         if (damage.getSource() instanceof EntityDamageSource) {
            Entity var4 = damage.getResponsibleEntity();
            if (var4 instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)var4)) {
               return null;
            }

            if (var4 instanceof EntityArrow) {
               EntityArrow arrow = (EntityArrow)var4;
               if (arrow.shootingEntity instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)arrow.shootingEntity)) {
                  return null;
               }
            }
         }

         return super.attackEntityFrom(damage);
      }
   }

   public boolean canAttackPlayer(EntityPlayer par1EntityPlayer) {
      return !this.mcServer.isPVPEnabled() ? false : super.canAttackPlayer(par1EntityPlayer);
   }

   public void onEntityDamaged(DamageSource damage_source, float amount) {
      this.addHungerServerSide(damage_source.getHungerDamage());
      this.sendPacket((new Packet85SimpleSignal(EnumSignal.damage_taken)).setShort(Math.min(Math.round(amount * 10.0F), 32767)));
      this.addStat(StatList.damageTakenStat, Math.round(amount * 10.0F));
      super.onEntityDamaged(damage_source, amount);
   }

   public void travelInsideDimension(double x, double y, double z) {
      this.last_experience = -1;
      this.lastHealth = -1.0F;
      this.last_nutrition = -1;
      this.mcServer.getConfigurationManager().teleportPlayerInsideDimension(this, x, y, z, true);
   }

   public void travelToDimension(int par1) {
      if (this.dimension == 1 && par1 == 1) {
         this.triggerAchievement(AchievementList.theEnd2);
         this.worldObj.removeEntity(this);
         this.playerConqueredTheEnd = true;
         this.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(4, 0));
      } else {
         WorldServer destination_world = this.mcServer.worldServerForDimension(par1);
         if (destination_world.isUnderworld()) {
            this.worldObj.worldInfo.setUnderworldVisited();
         }

         if (destination_world.isTheNether()) {
            this.worldObj.worldInfo.setNetherVisited();
         }

         if (destination_world.isTheEnd() && destination_world.playerEntities.size() == 0) {
            ((WorldProviderEnd)this.mcServer.worldServerForDimension(par1).provider).heal_ender_dragon = true;
         }

         if (this.dimension == 0 && par1 == 1) {
            this.triggerAchievement(AchievementList.theEnd);
            ChunkCoordinates var2 = this.mcServer.worldServerForDimension(par1).getEntrancePortalLocation();
            if (var2 != null) {
               this.playerNetServerHandler.setPlayerLocation((double)var2.posX, (double)var2.posY, (double)var2.posZ, 0.0F, 0.0F);
            }

            par1 = 1;
         } else {
            this.triggerAchievement(AchievementList.portal);
         }

         this.mcServer.getConfigurationManager().transferPlayerToDimension(this, par1);
         this.last_experience = -1;
         this.lastHealth = -1.0F;
         this.last_nutrition = -1;
      }

   }

   private void sendTileEntityToPlayer(TileEntity par1TileEntity) {
      if (par1TileEntity != null) {
         Packet var2 = par1TileEntity.getDescriptionPacket();
         if (var2 != null) {
            this.playerNetServerHandler.sendPacketToPlayer(var2);
         }
      }

   }

   public void onItemPickup(Entity par1Entity, int par2) {
      super.onItemPickup(par1Entity, par2);
      this.openContainer.detectAndSendChanges();
   }

   public void tryToSleepInBedAt(int x, int y, int z) {
      if (!this.inBed() && this.isEntityAlive() && this.worldObj.provider.isSurfaceWorld()) {
         if (this.getHostileEntityNearBed(x, y, z) != null) {
            this.addChatMessage("tile.bed.notSafe");
         } else if (this.worldObj.isOutdoors(x, y, z)) {
            this.addChatMessage("tile.bed.notSheltered");
         } else if (this.isStarving()) {
            this.addChatMessage("tile.bed.tooHungry");
         } else if (!this.worldObj.isAirOrPassableBlock(x, y + 1, z, false)) {
            this.addChatMessage("tile.bed.obstructed");
         } else if (this.isPotionActive(Potion.poison)) {
            this.addChatMessage("tile.bed.poisoned");
         } else if (this.isHostileEntityDiggingNearBed(x, y, z)) {
            this.addChatMessage("tile.bed.mobsDigging");
         } else {
            int direction = BlockBed.j(this.worldObj.getBlockMetadata(x, y, z));
            this.getIntoBed(x, y, z, direction);
            Packet17Sleep var5 = new Packet17Sleep(this, 0, x, y, z, direction);
            this.getServerForPlayer().getEntityTracker().sendPacketToAllPlayersTrackingEntity(this, var5);
            this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.playerNetServerHandler.sendPacketToPlayer(var5);
         }

      }
   }

   public void wakeUpPlayer(boolean get_out_of_bed, Entity entity_to_look_at) {
      if (this.conscious_state == EnumConsciousState.falling_asleep || this.conscious_state == EnumConsciousState.sleeping || get_out_of_bed) {
         if (this.conscious_state == EnumConsciousState.falling_asleep || this.conscious_state == EnumConsciousState.sleeping) {
            this.mcServer.sendWorldAgesToClient(this);
            this.playerNetServerHandler.sendPacketToPlayer(new Packet85SimpleSignal(EnumSignal.start_waking_up));
         }

         if (this.bed_location == null) {
            this.conscious_state = EnumConsciousState.fully_awake;
         } else {
            if (get_out_of_bed) {
               this.getServerForPlayer().getEntityTracker().sendPacketToAllAssociatedPlayers(this, new Packet18Animation(this, 3, entity_to_look_at));
            }

            super.wakeUpPlayer(get_out_of_bed, entity_to_look_at);
            if (get_out_of_bed && this.playerNetServerHandler != null) {
               this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            }

         }
      }
   }

   public void mountEntity(Entity par1Entity) {
      super.mountEntity(par1Entity);
      this.playerNetServerHandler.sendPacketToPlayer(new Packet39AttachEntity(0, this, this.ridingEntity));
      this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
   }

   protected void updateFallState(double par1, boolean par3) {
   }

   public void updateFlyingState(double par1, boolean par3) {
      super.updateFallState(par1, par3);
   }

   public void displayGUIEditSign(TileEntity par1TileEntity) {
      if (par1TileEntity instanceof TileEntitySign) {
         ((TileEntitySign)par1TileEntity).func_142010_a(this);
         this.playerNetServerHandler.sendPacketToPlayer(new Packet133TileEditorOpen(0, par1TileEntity.xCoord, par1TileEntity.yCoord, par1TileEntity.zCoord));
      }

   }

   private void incrementWindowID() {
      this.currentWindowId = this.currentWindowId % 100 + 1;
   }

   public void displayGUIWorkbench(int par1, int par2, int par3) {
      this.incrementWindowID();
      this.playerNetServerHandler.sendPacketToPlayer((new Packet100OpenWindow(this.currentWindowId, 1, "Crafting", 9, true)).setCoords(par1, par2, par3));
      this.openContainer = new ContainerWorkbench(this, par1, par2, par3);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addCraftingToCrafters(this);
   }

   public void displayGUIEnchantment(int par1, int par2, int par3, String par4Str) {
      this.incrementWindowID();
      this.playerNetServerHandler.sendPacketToPlayer((new Packet100OpenWindow(this.currentWindowId, 4, par4Str == null ? "" : par4Str, 9, par4Str != null)).setCoords(par1, par2, par3));
      this.openContainer = new ContainerEnchantment(this, this.worldObj, par1, par2, par3);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addCraftingToCrafters(this);
   }

   public void displayGUIAnvil(int x, int y, int z) {
      this.incrementWindowID();
      TileEntity tile_entity = this.worldObj.getBlockTileEntity(x, y, z);
      this.playerNetServerHandler.sendPacketToPlayer((new Packet100OpenWindow(this.currentWindowId, 8, tile_entity.getCustomInvName(), 9, tile_entity.hasCustomName())).setCoords(x, y, z));
      this.openContainer = new ContainerRepair(this, x, y, z);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addCraftingToCrafters(this);
   }

   public void displayGUIChestForMinecart(IInventory par1IInventory) {
      if (this.openContainer != this.inventoryContainer) {
         this.closeScreen();
      }

      this.incrementWindowID();
      this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 12, par1IInventory.getCustomNameOrUnlocalized(), par1IInventory.getSizeInventory(), par1IInventory.hasCustomName()));
      this.openContainer = new ContainerChest(this, par1IInventory);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addCraftingToCrafters(this);
   }

   public void displayGUIChest(int x, int y, int z, IInventory par1IInventory) {
      if (this.openContainer != this.inventoryContainer) {
         this.closeScreen();
      }

      this.incrementWindowID();
      this.playerNetServerHandler.sendPacketToPlayer((new Packet100OpenWindow(this.currentWindowId, 0, par1IInventory.getCustomNameOrUnlocalized(), par1IInventory.getSizeInventory(), par1IInventory.hasCustomName())).setCoords(x, y, z));
      this.openContainer = new ContainerChest(this, par1IInventory);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addCraftingToCrafters(this);
   }

   public void displayGUIHopper(TileEntityHopper par1TileEntityHopper) {
      this.incrementWindowID();
      this.playerNetServerHandler.sendPacketToPlayer((new Packet100OpenWindow(this.currentWindowId, 9, par1TileEntityHopper.getCustomNameOrUnlocalized(), par1TileEntityHopper.getSizeInventory(), par1TileEntityHopper.hasCustomName())).setCoords(par1TileEntityHopper));
      this.openContainer = new ContainerHopper(this, par1TileEntityHopper);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addCraftingToCrafters(this);
   }

   public void displayGUIHopperMinecart(EntityMinecartHopper par1EntityMinecartHopper) {
      this.incrementWindowID();
      this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 13, par1EntityMinecartHopper.getCustomNameOrUnlocalized(), par1EntityMinecartHopper.getSizeInventory(), par1EntityMinecartHopper.hasCustomName()));
      this.openContainer = new ContainerHopper(this, par1EntityMinecartHopper);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addCraftingToCrafters(this);
   }

   public void displayGUIFurnace(TileEntityFurnace par1TileEntityFurnace) {
      this.incrementWindowID();
      this.playerNetServerHandler.sendPacketToPlayer((new Packet100OpenWindow(this.currentWindowId, 2, par1TileEntityFurnace.getCustomNameOrUnlocalized(), par1TileEntityFurnace.getSizeInventory(), par1TileEntityFurnace.hasCustomName())).setCoords(par1TileEntityFurnace));
      this.openContainer = new ContainerFurnace(this, par1TileEntityFurnace);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addCraftingToCrafters(this);
   }

   public void displayGUIDispenser(TileEntityDispenser par1TileEntityDispenser) {
      this.incrementWindowID();
      this.playerNetServerHandler.sendPacketToPlayer((new Packet100OpenWindow(this.currentWindowId, par1TileEntityDispenser instanceof TileEntityDropper ? 10 : 3, par1TileEntityDispenser.getCustomNameOrUnlocalized(), par1TileEntityDispenser.getSizeInventory(), par1TileEntityDispenser.hasCustomName())).setCoords(par1TileEntityDispenser));
      this.openContainer = new ContainerDispenser(this, par1TileEntityDispenser);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addCraftingToCrafters(this);
   }

   public void displayGUIBrewingStand(TileEntityBrewingStand par1TileEntityBrewingStand) {
      this.incrementWindowID();
      this.playerNetServerHandler.sendPacketToPlayer((new Packet100OpenWindow(this.currentWindowId, 5, par1TileEntityBrewingStand.getCustomNameOrUnlocalized(), par1TileEntityBrewingStand.getSizeInventory(), par1TileEntityBrewingStand.hasCustomName())).setCoords(par1TileEntityBrewingStand));
      this.openContainer = new ContainerBrewingStand(this, par1TileEntityBrewingStand);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addCraftingToCrafters(this);
   }

   public void displayGUIBeacon(TileEntityBeacon par1TileEntityBeacon) {
      this.incrementWindowID();
      this.playerNetServerHandler.sendPacketToPlayer((new Packet100OpenWindow(this.currentWindowId, 7, par1TileEntityBeacon.getCustomNameOrUnlocalized(), par1TileEntityBeacon.getSizeInventory(), par1TileEntityBeacon.hasCustomName())).setCoords(par1TileEntityBeacon));
      this.openContainer = new ContainerBeacon(this, par1TileEntityBeacon);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addCraftingToCrafters(this);
   }

   public void displayGUIMerchant(IMerchant par1IMerchant, String par2Str) {
      this.incrementWindowID();
      this.openContainer = new ContainerMerchant(this, par1IMerchant);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addCraftingToCrafters(this);
      InventoryMerchant var3 = ((ContainerMerchant)this.openContainer).getMerchantInventory();
      this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 6, par2Str == null ? "" : par2Str, var3.getSizeInventory(), par2Str != null));
      MerchantRecipeList var4 = par1IMerchant.getRecipes(this);
      if (var4 != null) {
         try {
            ByteArrayOutputStream var5 = new ByteArrayOutputStream();
            DataOutputStream var6 = new DataOutputStream(var5);
            var6.writeInt(this.currentWindowId);
            var4.writeRecipiesToStream(var6);
            this.playerNetServerHandler.sendPacketToPlayer(new Packet250CustomPayload("MC|TrList", var5.toByteArray()));
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      }

   }

   public void displayGUIHorse(EntityHorse par1EntityHorse, IInventory par2IInventory) {
      if (this.openContainer != this.inventoryContainer) {
         this.closeScreen();
      }

      this.incrementWindowID();
      this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 11, par2IInventory.getCustomNameOrUnlocalized(), par2IInventory.getSizeInventory(), par2IInventory.hasCustomName(), par1EntityHorse.entityId));
      this.openContainer = new ContainerHorseInventory(this, par2IInventory, par1EntityHorse);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.addCraftingToCrafters(this);
   }

   public void sendSlotContents(Container par1Container, int par2, ItemStack par3ItemStack) {
      if (!(par1Container.getSlot(par2) instanceof SlotCrafting) && !this.playerInventoryBeingManipulated) {
         this.playerNetServerHandler.sendPacketToPlayer(new Packet103SetSlot(par1Container.windowId, par2, par3ItemStack));
      }

   }

   public void sendContainerToPlayer(Container par1Container) {
      this.sendContainerAndContentsToPlayer(par1Container, par1Container.getInventory());
   }

   public void sendContainerAndContentsToPlayer(Container par1Container, List par2List) {
      this.playerNetServerHandler.sendPacketToPlayer(new Packet104WindowItems(par1Container.windowId, par2List));
      this.playerNetServerHandler.sendPacketToPlayer(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
   }

   public void sendProgressBarUpdate(Container par1Container, int par2, int par3) {
      this.playerNetServerHandler.sendPacketToPlayer(new Packet105UpdateProgressbar(par1Container.windowId, par2, par3));
   }

   public void closeScreen() {
      this.playerNetServerHandler.sendPacketToPlayer(new Packet101CloseWindow(this.openContainer.windowId));
      this.closeContainer();
   }

   public void updateHeldItem() {
      if (!this.playerInventoryBeingManipulated) {
         this.playerNetServerHandler.sendPacketToPlayer(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
      }

   }

   public void closeContainer() {
      this.openContainer.onContainerClosed(this);
      this.openContainer = this.inventoryContainer;
   }

   public void setEntityActionState(float par1, float par2, boolean par3, boolean par4) {
      if (this.ridingEntity != null) {
         if (par1 >= -1.0F && par1 <= 1.0F) {
            this.moveStrafing = par1;
         }

         if (par2 >= -1.0F && par2 <= 1.0F) {
            this.moveForward = par2;
         }

         this.isJumping = par3;
         this.setSneaking(par4);
      }

   }

   public void addStatForThisWorldOnly(StatBase par1StatBase, int par2) {
      int id = par1StatBase.statId;
      if (StatList.isEitherZeroOrOne(par1StatBase)) {
         int previous_value = this.stats.containsKey(id) ? (Byte)this.stats.get(id) : 0;
         if (previous_value == 1) {
            return;
         }

         if (previous_value != 0) {
            Minecraft.setErrorMessage("addStatForThisWorldOnly: invalid stat value for " + par1StatBase);
         }

         if (par1StatBase.isAchievement()) {
            this.sendPacket((new Packet85SimpleSignal(EnumSignal.achievement_unlocked)).setInteger(id));
            this.worldObj.worldInfo.unlockAchievement((Achievement)par1StatBase, this);
         }

         this.stats.put(id, (byte)1);
         this.sendPacket(new Packet91PlayerStat(par1StatBase, 1L));
      } else if (StatList.hasLongValue(par1StatBase)) {
         long value;
         if (this.stats.containsKey(id)) {
            value = (long)par2 + (Long)this.stats.get(id);
         } else {
            value = (long)par2;
         }

         this.stats.put(id, value);
         this.sendPacket(new Packet91PlayerStat(par1StatBase, value));
      } else {
         if (this.stats.containsKey(id)) {
            par2 += (Integer)this.stats.get(id);
         }

         this.stats.put(id, par2);
         this.sendPacket(new Packet91PlayerStat(par1StatBase, (long)par2));
      }

   }

   public void addStat(StatBase par1StatBase, int par2) {
      if (par1StatBase != null) {
         if (!par1StatBase.isIndependent || par1StatBase == StatList.dropStat) {
            this.playerNetServerHandler.sendPacketToPlayer(new Packet200Statistic(par1StatBase.statId, par2));
         }

         if (par2 != 0) {
            this.addStatForThisWorldOnly(par1StatBase, par2);
         }
      }

   }

   public void writeStatsToNBT(NBTTagCompound par1NBTTagCompound) {
      Iterator i = this.stats.entrySet().iterator();

      while(i.hasNext()) {
         Map.Entry entry = (Map.Entry)i.next();
         int id = (Integer)entry.getKey();
         String key = "" + id;
         StatBase stat = StatList.getStat(id);
         if (StatList.isEitherZeroOrOne(stat)) {
            par1NBTTagCompound.setByte(key, (Byte)entry.getValue());
         } else if (StatList.hasLongValue(stat)) {
            par1NBTTagCompound.setLong(key, (Long)entry.getValue());
         } else {
            par1NBTTagCompound.setInteger(key, (Integer)entry.getValue());
         }
      }

   }

   public void readStatsFromNBT(NBTTagCompound par1NBTTagCompound) {
      Collection tags = par1NBTTagCompound.getTags();
      Iterator i = tags.iterator();

      while(i.hasNext()) {
         NBTBase tag = (NBTBase)i.next();
         int id = Integer.valueOf(tag.getName());
         StatBase stat = StatList.getStat(id);
         if (StatList.isEitherZeroOrOne(stat)) {
            this.stats.put(id, par1NBTTagCompound.getByte(tag.getName()));
         } else if (StatList.hasLongValue(stat)) {
            this.stats.put(id, par1NBTTagCompound.getLong(tag.getName()));
         } else {
            this.stats.put(id, par1NBTTagCompound.getInteger(tag.getName()));
         }
      }

   }

   public void mountEntityAndWakeUp() {
      if (this.riddenByEntity != null) {
         this.riddenByEntity.mountEntity(this);
      }

      if (this.inBed()) {
         this.wakeUpPlayer(true, (Entity)null);
      }

   }

   public void setPlayerHealthUpdated() {
      this.lastHealth = -1.0E8F;
   }

   public void addChatMessage(String par1Str) {
      this.playerNetServerHandler.sendPacketToPlayer(new Packet3Chat(ChatMessageComponent.createFromTranslationKey(par1Str)));
   }

   protected void onItemUseFinish() {
      super.onItemUseFinish();
   }

   public void clonePlayer(EntityPlayer par1EntityPlayer, boolean par2) {
      super.clonePlayer(par1EntityPlayer, par2);
      this.last_experience = -1;
      this.lastHealth = -1.0F;
      this.last_nutrition = -1;
      this.destroyedItemsNetCache.addAll(((EntityPlayerMP)par1EntityPlayer).destroyedItemsNetCache);
   }

   public boolean isMalnourished() {
      return this.protein == 0 || this.phytonutrients == 0;
   }

   public boolean isDoubleMalnourished() {
      return this.protein == 0 && this.phytonutrients == 0;
   }

   public void syncClientPlayer() {
      if (this.is_cursed) {
         this.sendPacket((new Packet85SimpleSignal(EnumSignal.cursed)).setByte(this.curse_id));
         if (this.curse_effect_known) {
            this.sendPacket(new Packet85SimpleSignal(EnumSignal.curse_effect_learned));
         }
      }

      if (this.hasSkills()) {
         this.sendPacket((new Packet85SimpleSignal(EnumSignal.skillset)).setInteger(this.getSkills()));
      }

      this.sendPacket(new Packet70GameEvent(7, this.mcServer.getVillageConditions()));
      this.sendAllStatsToClient();
   }

   protected void onNewPotionEffect(PotionEffect par1PotionEffect) {
      super.onNewPotionEffect(par1PotionEffect);
      this.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(this.entityId, par1PotionEffect));
   }

   protected void onChangedPotionEffect(PotionEffect par1PotionEffect, boolean par2) {
      super.onChangedPotionEffect(par1PotionEffect, par2);
      this.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(this.entityId, par1PotionEffect));
   }

   protected void onFinishedPotionEffect(PotionEffect par1PotionEffect) {
      super.onFinishedPotionEffect(par1PotionEffect);
      this.playerNetServerHandler.sendPacketToPlayer(new Packet42RemoveEntityEffect(this.entityId, par1PotionEffect));
   }

   public void setPositionAndUpdate(double par1, double par3, double par5) {
      this.playerNetServerHandler.setPlayerLocation(par1, par3, par5, this.rotationYaw, this.rotationPitch);
   }

   public void onCriticalHit(Entity par1Entity) {
      this.getServerForPlayer().getEntityTracker().sendPacketToAllAssociatedPlayers(this, new Packet18Animation(par1Entity, 6));
   }

   public void onEnchantmentCritical(Entity par1Entity) {
      this.getServerForPlayer().getEntityTracker().sendPacketToAllAssociatedPlayers(this, new Packet18Animation(par1Entity, 7));
   }

   public void sendPlayerAbilities() {
      if (this.playerNetServerHandler != null) {
         this.playerNetServerHandler.sendPacketToPlayer(new Packet202PlayerAbilities(this.capabilities));
      }

   }

   public WorldServer getServerForPlayer() {
      return (WorldServer)this.worldObj;
   }

   public void setGameType(EnumGameType par1EnumGameType) {
      this.theItemInWorldManager.setGameType(par1EnumGameType);
      this.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(3, par1EnumGameType.getID()));
   }

   public void sendChatToPlayer(ChatMessageComponent par1ChatMessageComponent) {
      this.playerNetServerHandler.sendPacketToPlayer(new Packet3Chat(par1ChatMessageComponent));
   }

   public boolean canCommandSenderUseCommand(int par1, String par2Str) {
      if (Minecraft.inDevMode()) {
         return true;
      } else {
         return "seed".equals(par2Str) && !this.mcServer.isDedicatedServer() ? true : (!"tell".equals(par2Str) && !"help".equals(par2Str) && !"me".equals(par2Str) ? (this.mcServer.getConfigurationManager().isPlayerOpped(this.username) ? this.mcServer.func_110455_j() >= par1 : false) : true);
      }
   }

   public String getPlayerIP() {
      String var1 = this.playerNetServerHandler.netManager.getSocketAddress().toString();
      var1 = var1.substring(var1.indexOf("/") + 1);
      var1 = var1.substring(0, var1.indexOf(":"));
      return var1;
   }

   public void updateClientInfo(Packet204ClientInfo par1Packet204ClientInfo) {
      this.translator = par1Packet204ClientInfo.getLanguage();
      int var2 = 256 >> par1Packet204ClientInfo.getRenderDistance();
      if (var2 > 3 && var2 < 15) {
         this.renderDistance = var2;
      }

      this.chatVisibility = par1Packet204ClientInfo.getChatVisibility();
      this.chatColours = par1Packet204ClientInfo.getChatColours();
      if (this.mcServer.isSinglePlayer() && this.mcServer.getServerOwner().equals(this.username)) {
         this.mcServer.setDifficultyForAllWorlds(par1Packet204ClientInfo.getDifficulty());
      }

      this.setHideCape(1, !par1Packet204ClientInfo.getShowCape());
   }

   public int getChatVisibility() {
      return this.chatVisibility;
   }

   public void requestTexturePackLoad(String par1Str, int par2) {
      String var3 = par1Str + "\u0000" + par2;
      this.playerNetServerHandler.sendPacketToPlayer(new Packet250CustomPayload("MC|TPack", var3.getBytes()));
   }

   public ChunkCoordinates getPlayerCoordinates() {
      return new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 0.5), MathHelper.floor_double(this.posZ));
   }

   public void func_143004_u() {
      this.field_143005_bX = MinecraftServer.getSystemTimeMillis();
   }

   private void updateMinecartFuelAmounts() {
      if (!this.raS && this.ticks_logged_in >= 20 && this.ticks_logged_in % 20 == 0) {
         this.raS = true;

         for(int i = 0; i < EntityMinecart.c.length; ++i) {
            if (!this.Sr[i]) {
               this.raS = false;
               break;
            }
         }

         if (!this.raS) {
            if (this.ticks_logged_in < 1000) {
               this.sendPacket((new Packet85SimpleSignal(EnumSignal.update_minecart_fuel)).setInteger(0).setEntityID(-100));
            } else {
               EntityMinecart.notify(this);
               this.raS = true;
            }
         }
      }

   }

   public boolean isSleeping() {
      return this.conscious_state == EnumConsciousState.sleeping;
   }

   public void sendPacket(Packet packet) {
      this.playerNetServerHandler.netManager.addToSendQueue(packet);
   }

   public boolean hasCursePending() {
      if (this.worldObj.isRemote) {
         Minecraft.setErrorMessage("hasCursePending: not meant to be called on client");
         return false;
      } else {
         return ((WorldServer)this.worldObj).playerHasCursePending(this);
      }
   }

   public boolean isImmuneByGrace() {
      return this.portal_grace_ticks > 0;
   }

   public void onTransferToWorld() {
      this.portal_grace_ticks = 600;
      this.sendPacket(new Packet85SimpleSignal(EnumSignal.transfered_to_world));
      super.onTransferToWorld();
   }

   public boolean isOnHitList() {
      if (this.is_not_on_hit_list) {
         return false;
      } else if (this.username == null) {
         return false;
      } else {
         String[] hit_list = Minecraft.hit_list;

         for(int i = 0; i < hit_list.length; ++i) {
            if (this.username.equalsIgnoreCase(hit_list[i])) {
               return true;
            }
         }

         this.is_not_on_hit_list = true;
         return false;
      }
   }

   public void sendTryAutoSwitchOrRestockPacket(ItemStack item_stack) {
      int item_subtype = item_stack.getHasSubtypes() ? item_stack.getItemSubtype() : 0;
      if (item_subtype <= 127) {
         this.sendPacket((new Packet85SimpleSignal(EnumSignal.try_auto_switch_or_restock)).setByte(item_subtype).setShort(item_stack.itemID));
      } else {
         this.sendPacket((new Packet85SimpleSignal(EnumSignal.try_auto_switch_or_restock_large_subtype)).setInteger(item_subtype).setShort(item_stack.itemID));
      }

   }

   public void onKillEntity(EntityLivingBase victim) {
      if (DedicatedServer.tournament_type == EnumTournamentType.score) {
         TournamentStanding ts = DedicatedServer.getOrCreateTournamentStanding(this);
         if (victim instanceof EntitySkeleton) {
            ts.killed_a_skeleton = true;
         } else if (victim instanceof EntityZombie) {
            ts.killed_a_zombie = true;
         } else if (victim instanceof EntitySpider) {
            ts.killed_a_spider = true;
         } else if (victim instanceof EntityWoodSpider) {
            ts.killed_a_wood_spider = true;
         } else if (victim instanceof EntityCreeper) {
            ts.killed_a_creeper = true;
         } else if (!(victim instanceof EntitySlime)) {
            if (victim instanceof EntityGhoul) {
               ts.killed_a_ghoul = true;
            } else if (victim instanceof EntityWight) {
               ts.killed_a_wight = true;
            } else if (victim instanceof EntityInvisibleStalker) {
               ts.killed_an_invisible_stalker = true;
            } else if (victim instanceof EntityWitch) {
               ts.killed_a_witch = true;
            } else if (victim instanceof EntityShadow) {
               ts.killed_a_shadow = true;
            } else if (victim instanceof EntityHellhound) {
               ts.killed_a_hellhound = true;
            } else if (victim instanceof EntityDemonSpider) {
               ts.killed_a_demon_spider = true;
            }
         } else {
            ts.killed_a_large_slime = ts.killed_a_large_slime || ((EntitySlime)victim).getSize() == 4;
         }

         DedicatedServer.updateTournamentScoreOnClient(this, true);
      }

      super.onKillEntity(victim);
   }

   public String obf(String s) {
      char[] chars = s.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         int c = chars[i];
         if (c >= 65 && c <= 90) {
            c = 90 - (c - 65);
         } else if (c >= 97 && c <= 122) {
            c = 122 - (c - 97);
         } else if (c >= 48 && c <= 57) {
            c = 57 - (c - 48);
         }

         chars[i] = (char)c;
      }

      return new String(chars);
   }

   public float getWetnessAndMalnourishmentHungerMultiplier() {
      int x = this.getBlockPosX();
      int y = this.getFootBlockPosY();
      int z = this.getBlockPosZ();
      float rain_factor = this.isInRain() ? (this.worldObj.isThundering(true) ? 0.5F : 0.25F) : 0.0F;
      float immersion_factor = this.worldObj.getBlockMaterial(x, y + 1, z) == Material.water ? 0.5F : (this.worldObj.getBlockMaterial(x, y, z) == Material.water ? 0.25F : 0.0F);
      float wetness_factor = Math.max(rain_factor, immersion_factor);
      if (this.isInRain() && !this.worldObj.isThundering(true) && immersion_factor == 0.25F) {
         wetness_factor += 0.125F;
      }

      if (this.worldObj.isBiomeFreezing(x, z)) {
         wetness_factor *= 2.0F;
      } else if (this.worldObj.getBiomeGenForCoords(x, z).temperature >= BiomeGenBase.desertRiver.temperature) {
         wetness_factor = 0.0F;
      }

      float malnourishment_factor = this.isMalnourished() ? 0.5F : 0.0F;
      return 1.0F + wetness_factor + malnourishment_factor;
   }

   public void sendWorldAgesToClient() {
      this.mcServer.sendWorldAgesToClient(this);
   }

   public INetworkManager getNetManager() {
      return this.playerNetServerHandler.getNetManager();
   }
}
