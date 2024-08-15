package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.BlockInfo;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockUnderminable;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RNG;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.SignalData;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.network.packet.Packet89PlaySoundOnServerAtEntity;
import net.minecraft.raycast.RaycastPolicies;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Curse;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Debug;
import net.minecraft.util.DebugAttack;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public abstract class Entity {
   private static int nextEntityID;
   public int entityId;
   public double renderDistanceWeight;
   public boolean preventEntitySpawning;
   public Entity riddenByEntity;
   public Entity ridingEntity;
   public boolean forceSpawn;
   public World worldObj;
   public double prevPosX;
   public double prevPosY;
   public double prevPosZ;
   public double posX;
   public double posY;
   public double posZ;
   public double motionX;
   public double motionY;
   public double motionZ;
   public float rotationYaw;
   public float rotationPitch;
   public float prevRotationYaw;
   public float prevRotationPitch;
   public final AxisAlignedBB boundingBox;
   public boolean onGround;
   public boolean isCollidedHorizontally;
   public boolean isCollidedVertically;
   public boolean isCollided;
   public boolean velocityChanged;
   public boolean send_position_update_immediately;
   protected boolean isInWeb;
   public boolean field_70135_K;
   public boolean isDead;
   public boolean is_unwanted_duplicate;
   public float yOffset;
   public float width;
   public float height;
   public float prevDistanceWalkedModified;
   public float distanceWalkedModified;
   public float distanceWalkedOnStepModified;
   public float fallDistance;
   private int nextStepDistance;
   public double lastTickPosX;
   public double lastTickPosY;
   public double lastTickPosZ;
   public float ySize;
   public float stepHeight;
   public boolean noClip;
   public float entityCollisionReduction;
   public Random rand;
   public int ticksExisted;
   private int fire;
   public boolean inWater;
   public int hurtResistantTime;
   public boolean firstUpdate;
   protected DataWatcher dataWatcher;
   private double entityRiderPitchDelta;
   private double entityRiderYawDelta;
   private Chunk chunk_added_to;
   public int chunk_added_to_section_index;
   public Chunk last_chunk_saved_to;
   public int last_chunk_saved_to_entity_list_index;
   public Chunk last_chunk_loaded_from;
   public int last_chunk_loaded_from_entity_list_index;
   public boolean ignoreFrustumCheck;
   public boolean isAirBorne;
   public int timeUntilPortal;
   public int ticks_since_portal_teleport = 24000;
   protected boolean inPortal;
   protected int portal_destination_dimension_id;
   protected int portalCounter;
   public int dimension;
   protected int teleportDirection;
   private boolean invulnerable;
   private UUID entityUniqueID;
   public EnumEntitySize myEntitySize;
   public int spawn_x;
   public int spawn_y;
   public int spawn_z;
   public int despawn_counter = 0;
   public boolean sync_last_tick_pos_on_next_update;
   public boolean disable_shadow;
   private int ticks_on_ground;
   public int seen_by_bat_countdown;
   public int index_of_last_applicable_world_renderer;
   int ticks_since_last_wet;
   public int raycast_seed_offset = 0;
   public static Class entity_look_helper_class = EntityLookHelper.getTheClass();
   public boolean tagged;
   public static boolean apply_MITE_bb_limits_checking = true;

   public Entity(World par1World) {
      this.entityId = nextEntityID++;
      this.renderDistanceWeight = 1.0;
      this.boundingBox = AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
      this.field_70135_K = true;
      this.width = 0.6F;
      this.height = 1.8F;
      this.nextStepDistance = 1;
      this.rand = new Random();
      this.firstUpdate = true;
      this.dataWatcher = new DataWatcher();
      this.myEntitySize = EnumEntitySize.SIZE_2;
      this.worldObj = par1World;
      this.entityUniqueID = this.isExpectedToHaveUUID() ? UUID.randomUUID() : null;
      this.setPosition(0.0, 0.0, 0.0);
      if (par1World != null) {
         this.dimension = par1World.provider.dimensionId;
      }

      this.dataWatcher.addObject(0, (byte)0);
      this.dataWatcher.addObject(1, (short)300);
      this.entityInit();
   }

   protected abstract void entityInit();

   public static void resetEntityIds() {
      if (nextEntityID < 0) {
         nextEntityID = 0;
      }

   }

   public static int peekNextEntityID() {
      return nextEntityID;
   }

   public static int obtainNextEntityID() {
      return nextEntityID++;
   }

   public void onSpawned() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
   }

   public DataWatcher getDataWatcher() {
      return this.dataWatcher;
   }

   public boolean equals(Object par1Obj) {
      return par1Obj instanceof Entity ? ((Entity)par1Obj).entityId == this.entityId : false;
   }

   public int hashCode() {
      return this.entityId;
   }

   protected void preparePlayerToSpawn() {
      if (this.worldObj != null) {
         while(true) {
            if (this.posY > 0.0) {
               this.setPosition(this.posX, this.posY, this.posZ);
               if (!this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()) {
                  ++this.posY;
                  continue;
               }
            }

            this.motionX = this.motionY = this.motionZ = 0.0;
            this.rotationPitch = 0.0F;
            break;
         }
      }

   }

   public void setDead() {
      if (!this.isDead && this instanceof EntityItem) {
         Chunk chunk = this.getChunkAddedTo();
         if (chunk != null && chunk.doesEntityObjectExistInEntityLists(this)) {
            chunk.setChunkModified();
         }
      }

      this.isDead = true;
   }

   protected void setSize(float par1, float par2) {
      float var3;
      if (par1 != this.width || par2 != this.height) {
         boolean was_on_ground = this.onGround;
         var3 = this.width;
         this.width = par1;
         this.height = par2;
         this.boundingBox.maxX = this.boundingBox.minX + (double)this.width;
         this.boundingBox.maxZ = this.boundingBox.minZ + (double)this.width;
         this.boundingBox.maxY = this.boundingBox.minY + (double)this.height;
         if (this.width > var3 && !this.firstUpdate && !this.worldObj.isRemote) {
            this.moveEntity((double)(var3 - this.width), 0.0, (double)(var3 - this.width));
            if (was_on_ground) {
               this.onGround = true;
            }
         }
      }

      var3 = par1 % 2.0F;
      if ((double)var3 < 0.375) {
         this.myEntitySize = EnumEntitySize.SIZE_1;
      } else if ((double)var3 < 0.75) {
         this.myEntitySize = EnumEntitySize.SIZE_2;
      } else if ((double)var3 < 1.0) {
         this.myEntitySize = EnumEntitySize.SIZE_3;
      } else if ((double)var3 < 1.375) {
         this.myEntitySize = EnumEntitySize.SIZE_4;
      } else if ((double)var3 < 1.75) {
         this.myEntitySize = EnumEntitySize.SIZE_5;
      } else {
         this.myEntitySize = EnumEntitySize.SIZE_6;
      }

   }

   public void setRotation(float par1, float par2) {
      this.rotationYaw = par1 % 360.0F;
      this.rotationPitch = par2 % 360.0F;
   }

   public void setPosition(double par1, double par3, double par5) {
      this.setPosition(par1, par3, par5, false);
   }

   public void setPosition(double par1, double par3, double par5, boolean bypass_in_bed_check) {
      if (!bypass_in_bed_check && this instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)this;
         if (player.inBed()) {
            return;
         }
      }

      this.posX = par1;
      this.posY = par3;
      this.posZ = par5;
      float var7 = this.width / 2.0F;
      float var8 = this.height;
      this.boundingBox.setBounds(par1 - (double)var7, par3 - (double)this.yOffset + (double)this.ySize, par5 - (double)var7, par1 + (double)var7, par3 - (double)this.yOffset + (double)this.ySize + (double)var8, par5 + (double)var7);
   }

   public void setAngles(float par1, float par2) {
      float var3 = this.rotationPitch;
      float var4 = this.rotationYaw;
      this.rotationYaw = (float)((double)this.rotationYaw + (double)par1 * 0.15);
      this.rotationPitch = (float)((double)this.rotationPitch - (double)par2 * 0.15);
      if (this.rotationPitch < -90.0F) {
         this.rotationPitch = -90.0F;
      }

      if (this.rotationPitch > 90.0F) {
         this.rotationPitch = 90.0F;
      }

      this.prevRotationPitch += this.rotationPitch - var3;
      this.prevRotationYaw += this.rotationYaw - var4;
   }

   public void spentTickInWater() {
   }

   public void spentTickInFire() {
      if (!this.isDead) {
         this.dealFireDamage(1);
         if (this.onServer() && this.canDouseFire() && (!(this instanceof EntityItem) || this.isDead) && this.worldObj.extinguishAllFireBlocksInBoundingBox(this.boundingBox) && !this.isDead) {
            this.causeQuenchEffect();
         }

         if (!this.isWet() && this.canCatchFire() && ++this.fire == 0) {
            this.setFire(8);
         }

      }
   }

   public void spentTickInLava() {
      if (!this.isDead) {
         this.setOnFireFromLava();
         if (this.isDead && !this.worldObj.isRemote) {
            if (this.canDouseFire()) {
               this.causeQuenchEffect();
            }

            this.entityFX(EnumEntityFX.burned_up_in_lava);
         }

         this.fallDistance *= 0.5F;
      }
   }

   public void checkForContactWithFireAndLava() {
      if (this.handleLavaMovement()) {
         this.spentTickInLava();
      } else if (!this.worldObj.isRemote) {
         if (this.isInFire()) {
            this.spentTickInFire();
         } else if (this.fire <= 0) {
            this.fire = -this.getFireResistance();
         }

         if (this.isWet() && this.fire > 0) {
            this.fire = -this.getFireResistance();
            this.causeQuenchEffect();
         }
      }

   }

   public final int getTicksExistedWithOffset() {
      return this.ticksExisted + this.entityId * 47;
   }

   public void detectAndRemoveDuplicateEntities() {
      WorldServer world = this.worldObj.getAsWorldServer();
      int num_times_in_list = 0;

      for(int i = 0; i < world.loadedEntityList.size(); ++i) {
         Entity entity = (Entity)world.loadedEntityList.get(i);
         if (entity.getClass() == this.getClass()) {
            if (entity == this) {
               ++num_times_in_list;
            } else if (entity.getUniqueID().equals(this.getUniqueID())) {
               if (!world.isEntityObjectInUnloadedEntityList(entity) && !entity.isDead) {
                  String msg = "Duplicate " + this.getEntityName() + " detected, removing (" + entity.getBlockPosX() + "," + entity.getBlockPosY() + "," + entity.getBlockPosZ() + " vs " + this.getBlockPosX() + "," + this.getBlockPosY() + "," + this.getBlockPosZ() + ") ticksExisted: " + entity.ticksExisted + " vs " + this.ticksExisted;
                  if (Minecraft.inDevMode()) {
                     Minecraft.setErrorMessage(msg);
                  } else {
                     System.out.println(msg);
                  }

                  System.out.println("  Chunk loaded from: " + entity.last_chunk_loaded_from + " ELI:" + entity.last_chunk_loaded_from_entity_list_index + " vs " + this.last_chunk_loaded_from + " ELI:" + this.last_chunk_loaded_from_entity_list_index);
                  entity.setAsUnwantedDuplicate();
                  (new Exception()).printStackTrace();
               } else {
                  if (!entity.isDead) {
                  }

                  entity.setAsUnwantedDuplicate();
               }
            }
         }
      }

      if (num_times_in_list > 1) {
         Minecraft.setErrorMessage("detectAndRemoveDuplicateEntities: " + this.getEntityName() + " in loadedEntityList " + num_times_in_list + " times!");
      }

   }

   public void setAsUnwantedDuplicate() {
      this.is_unwanted_duplicate = true;
      this.setDead();
   }

   public void onUpdate() {
      if (this.onServer() && this.getTicksExistedWithOffset() % 200 == 0) {
         this.detectAndRemoveDuplicateEntities();
      }

      if (this.onServer() && this instanceof IProjectile) {
         boolean in_flight = true;
         if (this instanceof EntityArrow) {
            EntityArrow entity_arrow = (EntityArrow)this;
            if (entity_arrow.isInGround()) {
               in_flight = false;
            }
         }

         if (in_flight) {
            List nearby_entities = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, this.boundingBox.expand(4.0, 4.0, 4.0));
            Iterator i = nearby_entities.iterator();

            while(i.hasNext()) {
               EntityLiving entity_living = (EntityLiving)i.next();
               if (entity_living instanceof EntityBat) {
                  EntityBat entity_bat = (EntityBat)entity_living;
                  if (entity_bat.getIsBatHanging() && entity_bat.canSeeEntity(this, true)) {
                     entity_bat.hurtResistantTime = 2;
                  }
               }
            }
         }
      }

      this.onEntityUpdate();
   }

   private boolean doesBlockShowSprintingParticles(Block block) {
      Material material = block.blockMaterial;
      return material != Material.anvil && material != Material.diamond && material != Material.emerald && material != Material.hardened_clay && material != Material.glass && material != Material.ice && material != Material.netherrack && material != Material.obsidian && material != Material.stone && material != Material.wood && !material.isMetal();
   }

   public void onEntityUpdate() {
      if (this.isExpectedToHaveUUID()) {
         if (this.entityUniqueID == null) {
            Minecraft.setErrorMessage("onEntityUpdate: UUID null for " + this);
         }
      } else if (this.entityUniqueID != null) {
         Minecraft.setErrorMessage("onEntityUpdate: UUID not null for " + this);
      }

      if (this.ticks_since_portal_teleport < 24000) {
         ++this.ticks_since_portal_teleport;
      }

      if (this.onGround) {
         ++this.ticks_on_ground;
      } else {
         this.ticks_on_ground = 0;
      }

      if (this.seen_by_bat_countdown > 0) {
         --this.seen_by_bat_countdown;
      }

      this.worldObj.theProfiler.startSection("entityBaseTick");
      if (this.ridingEntity != null && this.ridingEntity.isDead) {
         this.ridingEntity = null;
      }

      this.prevDistanceWalkedModified = this.distanceWalkedModified;
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
      int var2;
      int var5;
      if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
         this.worldObj.theProfiler.startSection("portal");
         MinecraftServer var1 = ((WorldServer)this.worldObj).getMinecraftServer();
         var2 = this.getMaxInPortalTime();
         if (this.inPortal) {
            if (var1.getAllowNether()) {
               if (this.ridingEntity == null && this.portalCounter++ >= var2) {
                  this.portalCounter = var2;
                  this.timeUntilPortal = this.getPortalCooldown();
                  var5 = (byte)this.portal_destination_dimension_id;
                  this.travelToDimension(var5);
               }

               this.inPortal = false;
            }
         } else {
            if (this.portalCounter > 0) {
               this.portalCounter -= 4;
            }

            if (this.portalCounter < 0) {
               this.portalCounter = 0;
            }
         }

         if (this.timeUntilPortal > 0) {
            --this.timeUntilPortal;
         }

         this.worldObj.theProfiler.endSection();
      }

      boolean render_sprinting_particles = false;
      if (this.onGround && !this.isInWater() && !this.isZevimrgvInTournament() && this.isSprinting()) {
         render_sprinting_particles = true;
      }

      int x;
      int y;
      if (render_sprinting_particles) {
         var5 = MathHelper.floor_double(this.posX);
         var2 = MathHelper.floor_double(this.posY - 0.20000000298023224 - (double)this.yOffset);
         x = MathHelper.floor_double(this.posZ);
         y = this.worldObj.getBlockId(var5, var2, x);
         if (y > 0) {
            if (this.worldObj.getBlock(var5, var2 + 1, x) == Block.snow) {
               y = Block.snow.blockID;
               ++var2;
            }

            if (this.doesBlockShowSprintingParticles(Block.getBlock(y))) {
               this.worldObj.spawnParticleEx(EnumParticle.tilecrack, y, this.worldObj.getBlockMetadata(var5, var2, x), this.posX + ((double)this.rand.nextFloat() - 0.5) * (double)this.width, this.boundingBox.minY + 0.1, this.posZ + ((double)this.rand.nextFloat() - 0.5) * (double)this.width, -this.motionX * 4.0, 1.5, -this.motionZ * 4.0);
            }
         }
      }

      if (this.handleWaterMovement()) {
         this.spentTickInWater();
      }

      if (this.isWet()) {
         this.ticks_since_last_wet = 0;
      } else {
         ++this.ticks_since_last_wet;
      }

      if (this.worldObj.isRemote) {
         this.fire = 0;
      } else if (this.fire > 0) {
         if (this.canCatchFire() && this.isHarmedByFire()) {
            if (this.fire % 20 == 0) {
               this.attackEntityFrom(new Damage(DamageSource.onFire, 1.0F));
            }

            --this.fire;
         } else {
            this.fire -= 4;
            if (this.fire < 0) {
               this.fire = 0;
            }
         }
      }

      this.checkForContactWithFireAndLava();
      if (this.posY < -64.0) {
         this.kill();
      }

      if (!this.worldObj.isRemote) {
         this.setFlag(0, this.fire > 0);
      }

      if (!this.worldObj.isRemote) {
         float chance_to_set_surroundings_on_fire;
         if (this instanceof EntityFireElemental) {
            chance_to_set_surroundings_on_fire = 0.05F;
         } else if (this instanceof EntityEarthElemental && ((EntityEarthElemental)this).isMagma()) {
            chance_to_set_surroundings_on_fire = 0.04F;
         } else if (this instanceof EntityMagmaCube) {
            chance_to_set_surroundings_on_fire = 0.04F;
         } else if (!this.isBurning()) {
            chance_to_set_surroundings_on_fire = 0.0F;
         } else {
            chance_to_set_surroundings_on_fire = this instanceof EntityLiving && this.getAsEntityLiving().increased_chance_of_spreading_fire_countdown > 0 ? 0.01F : 0.00125F;
         }

         if (chance_to_set_surroundings_on_fire > 0.0F && this.rand.nextFloat() < chance_to_set_surroundings_on_fire) {
            x = MathHelper.floor_double(this.posX);
            y = MathHelper.floor_double(this.posY);
            int z = MathHelper.floor_double(this.posZ);
            int fire_height = (int)this.height + 1;

            for(int dy = 0; dy < fire_height; ++dy) {
               if (this.worldObj.isAirBlock(x, y + dy, z)) {
                  if (this.rand.nextInt(10) == 0 && Block.fire.canNeighborBurn(this.worldObj, x, y + dy, z)) {
                     this.worldObj.setBlock(x, y + dy, z, Block.fire.blockID);
                  } else if (dy == 0) {
                     BlockInfo info = this.getBlockRestingOn(0.1F);
                     if (info != null) {
                        this.worldObj.tryToMeltBlock(info.x, info.y, info.z);
                     }
                  }
               } else if (!Block.fire.tryToCatchBlockOnFire(this.worldObj, x, y + dy, z, 100, this.rand, 1)) {
                  this.worldObj.tryToMeltBlock(x, y + dy, z);
               }
            }
         }
      }

      this.firstUpdate = false;
      this.worldObj.theProfiler.endSection();
   }

   public int getMaxInPortalTime() {
      return 0;
   }

   protected void setOnFireFromLava() {
      if (!this.worldObj.isRemote) {
         if (this.isHarmedByLava()) {
            this.attackEntityFrom(new Damage(DamageSource.lava, 4.0F));
         }

         if (this.canCatchFire()) {
            this.setFire(15);
         }
      }

   }

   public void setFire(int par1) {
      if (par1 >= 1 && this.canCatchFire()) {
         int var2 = par1 * 20;
         var2 += 10;
         var2 = EnchantmentProtection.getFireTimeForEntity(this, var2);
         if (this.fire < var2) {
            this.fire = var2;
         }

      }
   }

   public void extinguish(boolean do_quench_effect_even_if_not_wet) {
      if (!this.worldObj.isRemote && this.fire > 0 && (do_quench_effect_even_if_not_wet || this.isWet())) {
         this.causeQuenchEffect();
      }

      this.fire = -this.getFireResistance();
   }

   public void extinguish() {
      this.extinguish(false);
   }

   protected void kill() {
      this.setDead();
      if (this.onServer() && this instanceof EntityItem) {
         ((EntityItem)this).tryRemoveFromWorldUniques();
      }

   }

   public boolean isOffsetPositionInLiquid(double par1, double par3, double par5) {
      AxisAlignedBB var7 = this.boundingBox.getOffsetBoundingBox(par1, par3, par5);
      List var8 = this.worldObj.getCollidingBoundingBoxes(this, var7);
      return !var8.isEmpty() ? false : !this.worldObj.isAnyLiquid(var7);
   }

   public void spawnParticles(EnumParticle kind, int num_particles, float random_motion) {
      if (!this.worldObj.isRemote) {
         Minecraft.setErrorMessage("spawnParticles: only valid on client");
      } else {
         double effective_pos_y;
         if (this instanceof EntityLivingBase) {
            effective_pos_y = ((EntityLivingBase)this).getFootPosY();
         } else {
            effective_pos_y = this.posY;
         }

         double dx = 0.0;
         double dy = 0.0;
         double dz = 0.0;

         for(int i = 0; i < num_particles; ++i) {
            if (random_motion > 0.0F) {
               dx = this.rand.nextGaussian() * (double)random_motion;
               dy = this.rand.nextGaussian() * (double)random_motion;
               dz = this.rand.nextGaussian() * (double)random_motion;
            }

            this.worldObj.spawnParticle(kind, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, effective_pos_y + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, dx, dy, dz);
         }

      }
   }

   public void spawnParticles(EnumParticle kind, int num_particles) {
      this.spawnParticles(kind, num_particles, 0.0F);
   }

   public void spawnParticle(EnumParticle kind, float random_motion) {
      if (!this.worldObj.isRemote) {
         Minecraft.setErrorMessage("spawnParticles: only valid on client");
      } else {
         double effective_pos_y;
         if (this instanceof EntityLivingBase) {
            effective_pos_y = ((EntityLivingBase)this).getFootPosY();
         } else {
            effective_pos_y = this.posY;
         }

         double dx = 0.0;
         double dy = 0.0;
         double dz = 0.0;
         if (random_motion > 0.0F) {
            dx = this.rand.nextGaussian() * (double)random_motion;
            dy = this.rand.nextGaussian() * (double)random_motion;
            dz = this.rand.nextGaussian() * (double)random_motion;
         }

         this.worldObj.spawnParticle(kind, this.posX, effective_pos_y + (double)this.height + (double)(this.rand.nextFloat() * 0.2F), this.posZ, dx, dy, dz);
      }
   }

   public void spawnSteamParticles(int num_particles) {
      this.spawnParticles(EnumParticle.explode, num_particles, 0.02F);
   }

   public void spawnSmokeParticles(int num_particles) {
      this.spawnParticles(EnumParticle.smoke, num_particles, 0.02F);
   }

   public void spawnLargeSmokeParticles(int num_particles) {
      this.spawnParticles(EnumParticle.largesmoke, num_particles, 0.02F);
   }

   public void spawnCurseEffectLearnedParticles(int num_particles) {
      this.spawnParticles(EnumParticle.mobSpell, num_particles);
   }

   public void spawnRandomlyLocatedParticle(EnumParticle kind, double vel_x_or_red, double vel_y_or_green, double vel_z_or_blue) {
      double foot_pos_y = this.posY;
      if (this instanceof EntityLivingBase) {
         EntityLivingBase entity_living_base = (EntityLivingBase)this;
         foot_pos_y = entity_living_base.getFootPosY();
      }

      this.worldObj.spawnParticle(kind, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, foot_pos_y + 0.10000000149011612 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, vel_x_or_red, vel_y_or_green, vel_z_or_blue);
   }

   public void entityFX(EnumEntityFX kind, SignalData data) {
      if (this.worldObj.isRemote) {
         Minecraft.setErrorMessage("entityFX: only valid on server (" + kind + ")");
      } else if (!this.isEntityPlayer() || this.getAsPlayer().username != null && !this.isZevimrgvInTournament()) {
         Packet85SimpleSignal packet = (new Packet85SimpleSignal(EnumSignal.entity_fx, kind)).setEntityID(this.entityId);
         if (data != null) {
            packet.addData(data);
         }

         ((WorldServer)this.worldObj).sendPacketToAllAssociatedPlayers(this, packet);
      }
   }

   public void entityFX(EnumEntityFX kind) {
      this.entityFX(kind, (SignalData)null);
   }

   public void sendPacketToAllAssociatedPlayers(Packet packet) {
      if (this.worldObj.isRemote) {
         Minecraft.setErrorMessage("sendPacketToAllAssociatedPlayers: cannot be called on client");
      } else {
         ((WorldServer)this.worldObj).sendPacketToAllAssociatedPlayers(this, packet);
      }
   }

   public void sendPacketToAllPlayersTrackingEntity(Packet packet) {
      if (this.onServer()) {
         this.getWorldServer().sendPacketToAllPlayersTrackingEntity(this, packet);
      } else {
         Minecraft.setErrorMessage("sendPacketToAllPlayersTrackingEntity: cannot be called on client");
      }

   }

   public boolean isDecoy() {
      return false;
   }

   public boolean isEntityPlayer() {
      return false;
   }

   public boolean isEntityFX() {
      return this instanceof EntityFX;
   }

   public boolean isInFire() {
      return (this.worldObj.isUnderworld() && this.boundingBox.minY <= 3.0 || this.worldObj.isTheNether() && (this.boundingBox.minY <= 1.0 || this.boundingBox.maxY >= 123.0)) && this.worldObj.doesBoundingBoxContainBlock(this.boundingBox.expand(0.001, 0.001, 0.001), Block.mantleOrCore.blockID, -1) ? true : this.worldObj.isBoundingBoxBurning(this.boundingBox.contract(0.001, 0.001, 0.001), false);
   }

   public String getClientOrServerString() {
      return this.onClient() ? "client" : (this.onServer() ? "server" : "unknown");
   }

   private final boolean applyLimitsForX(double[] limits) {
      if (!apply_MITE_bb_limits_checking) {
         return false;
      } else if (Double.isNaN(limits[0]) && Double.isNaN(limits[1])) {
         return false;
      } else {
         if (!Double.isNaN(limits[0])) {
            limits[2] = limits[0] - this.boundingBox.maxX;
            this.boundingBox.maxX = limits[0];
            this.boundingBox.minX = limits[0] - (double)this.width;
         } else {
            limits[2] = limits[1] - this.boundingBox.minX;
            this.boundingBox.minX = limits[1];
            this.boundingBox.maxX = limits[1] + (double)this.width;
         }

         return true;
      }
   }

   private final boolean applyLimitsForY(double[] limits) {
      if (!apply_MITE_bb_limits_checking) {
         return false;
      } else if (Double.isNaN(limits[0]) && Double.isNaN(limits[1])) {
         return false;
      } else {
         if (!Double.isNaN(limits[0])) {
            limits[2] = limits[0] - this.boundingBox.maxY;
            this.boundingBox.maxY = limits[0];
            this.boundingBox.minY = limits[0] - (double)this.height;
         } else {
            limits[2] = limits[1] - this.boundingBox.minY;
            this.boundingBox.minY = limits[1];
            this.boundingBox.maxY = limits[1] + (double)this.height;
         }

         return true;
      }
   }

   private final boolean applyLimitsForZ(double[] limits) {
      if (!apply_MITE_bb_limits_checking) {
         return false;
      } else if (Double.isNaN(limits[0]) && Double.isNaN(limits[1])) {
         return false;
      } else {
         if (!Double.isNaN(limits[0])) {
            limits[2] = limits[0] - this.boundingBox.maxZ;
            this.boundingBox.maxZ = limits[0];
            this.boundingBox.minZ = limits[0] - (double)this.width;
         } else {
            limits[2] = limits[1] - this.boundingBox.minZ;
            this.boundingBox.minZ = limits[1];
            this.boundingBox.maxZ = limits[1] + (double)this.width;
         }

         return true;
      }
   }

   public void moveEntity(double par1, double par3, double par5) {
      if (!this.isDecoy()) {
         if (this.isEntityFX() || !this.worldObj.getChunkFromBlockCoords(this.getBlockPosX(), this.getBlockPosZ()).isEmpty()) {
            if (this.posX + par1 < this.worldObj.min_entity_pos_xz) {
               par1 = this.worldObj.min_entity_pos_xz - this.posX;
            } else if (this.posX + par1 > this.worldObj.max_entity_pos_xz) {
               par1 = this.worldObj.max_entity_pos_xz - this.posX;
            }

            if (this.posY + par3 < -255.0) {
               par3 = -255.0 - this.posY;
            } else if (this.posY + par3 > 255.0) {
               par3 = 255.0 - this.posY;
            }

            if (this.posZ + par5 < this.worldObj.min_entity_pos_xz) {
               par5 = this.worldObj.min_entity_pos_xz - this.posZ;
            } else if (this.posZ + par5 > this.worldObj.max_entity_pos_xz) {
               par5 = this.worldObj.max_entity_pos_xz - this.posZ;
            }

            if (DedicatedServer.getTournamentArenaRadius() > 0 && this instanceof EntityPlayer) {
               int domain_radius = DedicatedServer.getTournamentArenaRadius();
               int spawn_x = this.worldObj.worldInfo.getSpawnX();
               int spawn_z = this.worldObj.worldInfo.getSpawnZ();
               float min_x = (float)(spawn_x - domain_radius);
               float max_x = (float)(spawn_x + domain_radius);
               float min_z = (float)(spawn_z - domain_radius);
               float max_z = (float)(spawn_z + domain_radius);
               if (this.posX + par1 < (double)min_x) {
                  par1 = (double)min_x - this.posX;
               } else if (this.posX + par1 > (double)max_x) {
                  par1 = (double)max_x - this.posX;
               }

               if (this.posZ + par5 < (double)min_z) {
                  par5 = (double)min_z - this.posZ;
               } else if (this.posZ + par5 > (double)max_z) {
                  par5 = (double)max_z - this.posZ;
               }
            }

            if (this.noClip) {
               this.boundingBox.offset(par1, par3, par5);
               this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0;
               this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
               this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0;
            } else {
               this.worldObj.theProfiler.startSection("move");
               this.ySize *= 0.4F;
               double var7 = this.posX;
               double var9 = this.posY;
               double var11 = this.posZ;
               if (this.isInWeb && !(this instanceof EntityFallingSand)) {
                  this.isInWeb = false;
                  par1 *= 0.25;
                  par3 *= 0.05000000074505806;
                  par5 *= 0.25;
                  this.motionX = 0.0;
                  this.motionY = 0.0;
                  this.motionZ = 0.0;
               }

               double var13 = par1;
               double var15 = par3;
               double var17 = par5;
               AxisAlignedBB var19 = this.boundingBox.copy();
               boolean var20 = this.onGround && this.isSneaking() && this instanceof EntityPlayer;
               if (var20) {
                  double var21;
                  for(var21 = 0.05; par1 != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(par1, -1.0, 0.0)).isEmpty(); var13 = par1) {
                     if (par1 < var21 && par1 >= -var21) {
                        par1 = 0.0;
                     } else if (par1 > 0.0) {
                        par1 -= var21;
                     } else {
                        par1 += var21;
                     }
                  }

                  for(; par5 != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(0.0, -1.0, par5)).isEmpty(); var17 = par5) {
                     if (par5 < var21 && par5 >= -var21) {
                        par5 = 0.0;
                     } else if (par5 > 0.0) {
                        par5 -= var21;
                     } else {
                        par5 += var21;
                     }
                  }

                  while(par1 != 0.0 && par5 != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(par1, -1.0, par5)).isEmpty()) {
                     if (par1 < var21 && par1 >= -var21) {
                        par1 = 0.0;
                     } else if (par1 > 0.0) {
                        par1 -= var21;
                     } else {
                        par1 += var21;
                     }

                     if (par5 < var21 && par5 >= -var21) {
                        par5 = 0.0;
                     } else if (par5 > 0.0) {
                        par5 -= var21;
                     } else {
                        par5 += var21;
                     }

                     var13 = par1;
                     var17 = par5;
                  }
               }

               List var36 = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(par1, par3, par5));
               double[] limits = new double[]{Double.NaN, Double.NaN, 0.0};

               for(int var22 = 0; var22 < var36.size(); ++var22) {
                  par3 = ((AxisAlignedBB)var36.get(var22)).calculateYOffset(this.boundingBox, par3, limits);
               }

               if (!this.applyLimitsForY(limits)) {
                  this.boundingBox.offset(0.0, par3, 0.0);
               } else if (Minecraft.inDevMode() && limits[2] != par3) {
                  Minecraft.setErrorMessage("moveEntity: par3=" + par3 + " vs limits[2]=" + limits[2] + " (" + this.getEntityName() + ")");
               }

               if (!this.field_70135_K && var15 != par3) {
                  par5 = 0.0;
                  par3 = 0.0;
                  par1 = 0.0;
               }

               boolean var35 = this.onGround || var15 != par3 && var15 < 0.0;
               limits[0] = Double.NaN;
               limits[1] = Double.NaN;

               int var23;
               for(var23 = 0; var23 < var36.size(); ++var23) {
                  par1 = ((AxisAlignedBB)var36.get(var23)).calculateXOffset(this.boundingBox, par1, limits);
               }

               if (!this.applyLimitsForX(limits)) {
                  this.boundingBox.offset(par1, 0.0, 0.0);
               } else if (Minecraft.inDevMode() && limits[2] != par1) {
                  Minecraft.setErrorMessage("moveEntity: par1=" + par1 + " vs limits[2]=" + limits[2] + " (" + this.getEntityName() + ")");
               }

               if (!this.field_70135_K && var13 != par1) {
                  par5 = 0.0;
                  par3 = 0.0;
                  par1 = 0.0;
               }

               limits[0] = Double.NaN;
               limits[1] = Double.NaN;

               for(var23 = 0; var23 < var36.size(); ++var23) {
                  par5 = ((AxisAlignedBB)var36.get(var23)).calculateZOffset(this.boundingBox, par5, limits);
               }

               if (!this.applyLimitsForZ(limits)) {
                  this.boundingBox.offset(0.0, 0.0, par5);
               } else if (Minecraft.inDevMode() && limits[2] != par5) {
                  Minecraft.setErrorMessage("moveEntity: par5=" + par5 + " vs limits[2]=" + limits[2] + " (" + this.getEntityName() + ")");
               }

               if (!this.field_70135_K && var17 != par5) {
                  par5 = 0.0;
                  par3 = 0.0;
                  par1 = 0.0;
               }

               double var25;
               double var27;
               int var30;
               double var37;
               if (this.stepHeight > 0.0F && var35 && (var20 || this.ySize < 0.05F) && (var13 != par1 || var17 != par5)) {
                  var37 = par1;
                  var25 = par3;
                  var27 = par5;
                  par1 = var13;
                  par3 = (double)this.stepHeight;
                  par5 = var17;
                  AxisAlignedBB var29 = this.boundingBox.copy();
                  this.boundingBox.setBB(var19);
                  var36 = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(var13, par3, var17));
                  limits[0] = Double.NaN;
                  limits[1] = Double.NaN;

                  for(var30 = 0; var30 < var36.size(); ++var30) {
                     par3 = ((AxisAlignedBB)var36.get(var30)).calculateYOffset(this.boundingBox, par3, limits);
                  }

                  if (!this.applyLimitsForY(limits)) {
                     this.boundingBox.offset(0.0, par3, 0.0);
                  } else if (Minecraft.inDevMode() && limits[2] != par3) {
                     Minecraft.setErrorMessage("moveEntity: par3=" + par3 + " vs limits[2]=" + limits[2] + " (" + this.getEntityName() + ")");
                  }

                  if (!this.field_70135_K && var15 != par3) {
                     par5 = 0.0;
                     par3 = 0.0;
                     par1 = 0.0;
                  }

                  limits[0] = Double.NaN;
                  limits[1] = Double.NaN;

                  for(var30 = 0; var30 < var36.size(); ++var30) {
                     par1 = ((AxisAlignedBB)var36.get(var30)).calculateXOffset(this.boundingBox, par1, limits);
                  }

                  if (!this.applyLimitsForX(limits)) {
                     this.boundingBox.offset(par1, 0.0, 0.0);
                  } else if (Minecraft.inDevMode() && limits[2] != par1) {
                     Minecraft.setErrorMessage("moveEntity: par1=" + par1 + " vs limits[2]=" + limits[2] + " (" + this.getEntityName() + ")");
                  }

                  if (!this.field_70135_K && var13 != par1) {
                     par5 = 0.0;
                     par3 = 0.0;
                     par1 = 0.0;
                  }

                  limits[0] = Double.NaN;
                  limits[1] = Double.NaN;

                  for(var30 = 0; var30 < var36.size(); ++var30) {
                     par5 = ((AxisAlignedBB)var36.get(var30)).calculateZOffset(this.boundingBox, par5, limits);
                  }

                  if (!this.applyLimitsForZ(limits)) {
                     this.boundingBox.offset(0.0, 0.0, par5);
                  } else if (Minecraft.inDevMode() && limits[2] != par5) {
                     Minecraft.setErrorMessage("moveEntity: par5=" + par5 + " vs limits[2]=" + limits[2] + " (" + this.getEntityName() + ")");
                  }

                  if (!this.field_70135_K && var17 != par5) {
                     par5 = 0.0;
                     par3 = 0.0;
                     par1 = 0.0;
                  }

                  if (!this.field_70135_K && var15 != par3) {
                     par5 = 0.0;
                     par3 = 0.0;
                     par1 = 0.0;
                  } else {
                     par3 = (double)(-this.stepHeight);
                     var36 = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(par1, par3, par5));
                     limits[0] = Double.NaN;
                     limits[1] = Double.NaN;

                     for(var30 = 0; var30 < var36.size(); ++var30) {
                        par3 = ((AxisAlignedBB)var36.get(var30)).calculateYOffset(this.boundingBox, par3, limits);
                     }

                     if (!this.applyLimitsForY(limits)) {
                        this.boundingBox.offset(0.0, par3, 0.0);
                     } else if (Minecraft.inDevMode() && limits[2] != par3) {
                        Minecraft.setErrorMessage("moveEntity: par3=" + par3 + " vs limits[2]=" + limits[2] + " (" + this.getEntityName() + ")");
                     }
                  }

                  if (var37 * var37 + var27 * var27 >= par1 * par1 + par5 * par5) {
                     par1 = var37;
                     par3 = var25;
                     par5 = var27;
                     this.boundingBox.setBB(var29);
                  }
               }

               this.worldObj.theProfiler.endSection();
               this.worldObj.theProfiler.startSection("rest");
               this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0;
               this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
               this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0;
               this.isCollidedHorizontally = var13 != par1 || var17 != par5;
               this.isCollidedVertically = var15 != par3;
               this.onGround = var15 != par3 && var15 < 0.0;
               this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
               this.updateFallState(par3, this.onGround);
               if (var13 != par1) {
                  this.motionX = 0.0;
               }

               if (var15 != par3) {
                  this.motionY = 0.0;
               }

               if (var17 != par5) {
                  this.motionZ = 0.0;
               }

               var37 = this.posX - var7;
               var25 = this.posY - var9;
               var27 = this.posZ - var11;
               if (this.canTriggerWalking() && !var20 && this.ridingEntity == null) {
                  int var39 = MathHelper.floor_double(this.posX);
                  var30 = MathHelper.floor_double(this.posY - 0.20000000298023224 - (double)this.yOffset);
                  int var31 = MathHelper.floor_double(this.posZ);
                  int var32 = this.worldObj.getBlockId(var39, var30, var31);
                  if (var32 == 0) {
                     int var33 = this.worldObj.blockGetRenderType(var39, var30 - 1, var31);
                     if (var33 == 11 || var33 == 32 || var33 == 21) {
                        var32 = this.worldObj.getBlockId(var39, var30 - 1, var31);
                     }
                  }

                  if (var32 != Block.ladder.blockID) {
                     var25 = 0.0;
                  }

                  this.distanceWalkedModified = (float)((double)this.distanceWalkedModified + (double)MathHelper.sqrt_double(var37 * var37 + var27 * var27) * 0.6);
                  this.distanceWalkedOnStepModified = (float)((double)this.distanceWalkedOnStepModified + (double)MathHelper.sqrt_double(var37 * var37 + var25 * var25 + var27 * var27) * 0.6);
                  if (this.distanceWalkedOnStepModified > (float)this.nextStepDistance && var32 > 0) {
                     this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;
                     if (!this.isZevimrgvInTournament()) {
                        if (this.isInWater()) {
                           float var42 = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224 + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224) * 0.35F;
                           if (var42 > 1.0F) {
                              var42 = 1.0F;
                           }

                           this.playSound("liquid.swim", var42, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                        } else if ((!(this instanceof EntityPlayer) || !((EntityPlayer)this).inBed()) && this.ticks_on_ground > 5 && !(this instanceof EntityGelatinousCube)) {
                           this.playStepSound(var39, var30, var31, var32);
                        }
                     }

                     Block.blocksList[var32].onEntityWalking(this.worldObj, var39, var30, var31, this);
                  } else {
                     Block block = Block.blocksList[var32];
                     if (block instanceof BlockUnderminable) {
                        block.onEntityWalking(this.worldObj, var39, var30, var31, this);
                     } else {
                        block = Block.blocksList[this.worldObj.getBlockId(var39, var30 + 1, var31)];
                        if (block == Block.snow) {
                           block.onEntityWalking(this.worldObj, var39, var30 + 1, var31, this);
                        }
                     }
                  }
               }

               try {
                  this.doBlockCollisions();
               } catch (Throwable throwable) {
                  CrashReport var41 = CrashReport.makeCrashReport(throwable, "Checking entity tile collision");
                  CrashReportCategory var38 = var41.makeCategory("Entity being checked for collision");
                  this.addEntityCrashInfo(var38);
                  throw new ReportedException(var41);
               }

               this.worldObj.theProfiler.endSection();
            }

         }
      }
   }

   public void causeQuenchEffect() {
      this.entityFX(EnumEntityFX.steam_with_hiss);
   }

   public void spawnSingleSteamParticle(boolean with_hiss_sound) {
      if (with_hiss_sound) {
         this.entityFX(EnumEntityFX.single_steam_particle_with_hiss);
      } else {
         Minecraft.setErrorMessage("spawnSingleSmokeParticle: signal without hiss not handled yet");
      }

   }

   protected final void doBlockCollisions() {
      int var1 = MathHelper.floor_double(this.boundingBox.minX + 0.001);
      int var2 = MathHelper.floor_double(this.boundingBox.minY + 0.001);
      int var3 = MathHelper.floor_double(this.boundingBox.minZ + 0.001);
      int var4 = MathHelper.floor_double(this.boundingBox.maxX - 0.001);
      int var5 = MathHelper.floor_double(this.boundingBox.maxY - 0.001);
      int var6 = MathHelper.floor_double(this.boundingBox.maxZ - 0.001);
      if (var2 <= 255 && var5 >= 0) {
         for(int var7 = var1; var7 <= var4; ++var7) {
            for(int var8 = var2; var8 <= var5; ++var8) {
               for(int var9 = var3; var9 <= var6; ++var9) {
                  int var10 = this.worldObj.getBlockId(var7, var8, var9);
                  if (var10 > 0) {
                     try {
                        Block.blocksList[var10].onEntityCollidedWithBlock(this.worldObj, var7, var8, var9, this);
                        this.onCollidedWithBlock(this.worldObj, Block.blocksList[var10], var7, var8, var9);
                     } catch (Throwable var14) {
                        CrashReport var12 = CrashReport.makeCrashReport(var14, "Colliding entity with tile");
                        CrashReportCategory var13 = var12.makeCategory("Tile being collided with");
                        CrashReportCategory.addBlockCrashInfo(var13, var7, var8, var9, var10, this.worldObj.getBlockMetadata(var7, var8, var9));
                        throw new ReportedException(var12);
                     }
                  }
               }
            }
         }

      }
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      if (!this.isZevimrgvInTournament()) {
         StepSound var5 = Block.blocksList[par4].stepSound;
         if (this.worldObj.getBlockId(par1, par2 + 1, par3) == Block.snow.blockID) {
            var5 = Block.snow.stepSound;
            this.playSound(var5.getStepSound(), var5.getVolume() * 0.15F, var5.getPitch());
         } else if (!Block.blocksList[par4].blockMaterial.isLiquid()) {
            this.playSound(var5.getStepSound(), var5.getVolume() * 0.15F, var5.getPitch());
         }

      }
   }

   public void playSound(String par1Str, float par2, float par3) {
      if (!this.isZevimrgvInTournament()) {
         this.worldObj.playSoundAtEntity(this, par1Str, par2, par3);
      }
   }

   public void playLongDistanceSound(String par1Str, float par2, float par3) {
      this.worldObj.playLongDistanceSoundAtEntity(this, par1Str, par2, par3);
   }

   protected boolean canTriggerWalking() {
      return true;
   }

   protected void updateFallState(double par1, boolean par3) {
      if (par3) {
         if (this.fallDistance > 0.0F) {
            this.fall(this.fallDistance);
            this.fallDistance = 0.0F;
         }
      } else if (par1 < 0.0) {
         this.fallDistance = (float)((double)this.fallDistance - par1);
      }

   }

   public AxisAlignedBB getBoundingBox() {
      return null;
   }

   protected void dealFireDamage(int par1) {
      if (this.isHarmedByFire() && (this instanceof EntityLivingBase || this.ticksExisted % 10 == 0)) {
         this.attackEntityFrom(new Damage(DamageSource.inFire, (float)par1));
      }

   }

   public boolean canDouseFire() {
      return this.hasModelItem() ? this.getModelItem().canDouseFire() : false;
   }

   public boolean canCatchFire() {
      if (this.hasModelItem()) {
         return this.getModelItem().canCatchFire();
      } else {
         Minecraft.setErrorMessage("canCatchFire: entity must override this function or have a model item (" + this.getEntityName() + ")");
         return false;
      }
   }

   public boolean isHarmedByFire() {
      if (this.hasModelItem()) {
         return this.getModelItem().isHarmedByFire();
      } else {
         Minecraft.setErrorMessage("isHarmedByFire: entity must override this function or have a model item (" + this.getEntityName() + ")");
         return false;
      }
   }

   public boolean isHarmedByLava() {
      if (this.hasModelItem()) {
         return this.getModelItem().isHarmedByLava();
      } else {
         Minecraft.setErrorMessage("isHarmedByLava: entity must override this function or have a model item (" + this.getEntityName() + ")");
         return false;
      }
   }

   public boolean isHarmedByPepsin() {
      return false;
   }

   public boolean isHarmedByAcid() {
      return true;
   }

   protected void fall(float par1) {
      if (this.riddenByEntity != null) {
         this.riddenByEntity.fall(par1);
      }

   }

   public boolean isWet() {
      return this.inWater || this.worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) || this.worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + (double)this.height), MathHelper.floor_double(this.posZ));
   }

   public boolean isInWater() {
      return this.inWater;
   }

   public void spawnSplashParticles() {
      float var2 = (float)MathHelper.floor_double(this.boundingBox.minY);

      int var3;
      float var4;
      float var5;
      for(var3 = 0; (float)var3 < 1.0F + this.width * 20.0F; ++var3) {
         var4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         var5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.worldObj.spawnParticle(EnumParticle.bubble, this.posX + (double)var4, (double)(var2 + 1.0F), this.posZ + (double)var5, this.motionX, this.motionY - (double)(this.rand.nextFloat() * 0.2F), this.motionZ);
      }

      for(var3 = 0; (float)var3 < 1.0F + this.width * 20.0F; ++var3) {
         var4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         var5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.worldObj.spawnParticle(EnumParticle.splash, this.posX + (double)var4, (double)(var2 + 1.0F), this.posZ + (double)var5, this.motionX, this.motionY, this.motionZ);
      }

   }

   public boolean handleWaterMovement() {
      double expansion_y = -0.4;
      if (this.boundingBox.maxY - this.boundingBox.minY < 0.4) {
         expansion_y = (this.boundingBox.maxY - this.boundingBox.minY) * -0.5;
      }

      if (this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0, expansion_y, 0.0).contract(0.001, 0.001, 0.001), Material.water, this)) {
         if (!this.inWater && !this.firstUpdate && !this.worldObj.isRemote) {
            this.entityFX(EnumEntityFX.splash);
         }

         this.fallDistance = 0.0F;
         this.inWater = true;
         this.extinguish();
         this.ticks_since_last_wet = 0;
      } else {
         this.inWater = false;
      }

      return this.inWater;
   }

   public boolean isInsideOfMaterial(Material par1Material) {
      double var2 = this.posY + (double)this.getEyeHeight();
      int var4 = MathHelper.floor_double(this.posX);
      int var5 = MathHelper.floor_float((float)MathHelper.floor_double(var2));
      int var6 = MathHelper.floor_double(this.posZ);
      int var7 = this.worldObj.getBlockId(var4, var5, var6);
      if (var7 != 0 && Block.blocksList[var7].blockMaterial == par1Material) {
         float var8 = BlockFluid.getFluidHeightPercent(this.worldObj.getBlockMetadata(var4, var5, var6)) - 0.11111111F;
         float var9 = (float)(var5 + 1) - var8;
         return var2 < (double)var9;
      } else {
         return false;
      }
   }

   public boolean isInsideOfMaterial(Material par1Material, float offset_y) {
      double var2 = this.posY + (double)this.getEyeHeight() + (double)offset_y;
      int var4 = MathHelper.floor_double(this.posX);
      int var5 = MathHelper.floor_float((float)MathHelper.floor_double(var2));
      int var6 = MathHelper.floor_double(this.posZ);
      int var7 = this.worldObj.getBlockId(var4, var5, var6);
      if (var7 != 0 && Block.blocksList[var7].blockMaterial == par1Material) {
         float var8 = BlockFluid.getFluidHeightPercent(this.worldObj.getBlockMetadata(var4, var5, var6)) - 0.11111111F;
         float var9 = (float)(var5 + 1) - var8;
         return var2 < (double)var9;
      } else {
         return false;
      }
   }

   public float getEyeHeight() {
      return 0.0F;
   }

   public boolean handleLavaMovement() {
      return this.worldObj.isMaterialInBB(this.boundingBox.expand(-0.10000000149011612, -0.4000000059604645, -0.10000000149011612), Material.lava);
   }

   public void moveFlying(float par1, float par2, float par3) {
      float var4 = par1 * par1 + par2 * par2;
      if (var4 >= 1.0E-4F) {
         var4 = MathHelper.sqrt_float(var4);
         if (var4 < 1.0F) {
            var4 = 1.0F;
         }

         var4 = par3 / var4;
         par1 *= var4;
         par2 *= var4;
         float var5 = MathHelper.sin(this.rotationYaw * 3.1415927F / 180.0F);
         float var6 = MathHelper.cos(this.rotationYaw * 3.1415927F / 180.0F);
         this.motionX += (double)(par1 * var6 - par2 * var5);
         this.motionZ += (double)(par2 * var6 + par1 * var5);
      }

   }

   public int getBrightnessForRender(float par1) {
      int var2 = MathHelper.floor_double(this.posX);
      int var3 = MathHelper.floor_double(this.posZ);
      if (this.worldObj.blockExists(var2, 0, var3)) {
         double var4 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66;
         int var6 = MathHelper.floor_double(this.posY - (double)this.yOffset + var4);
         return this.worldObj.getLightBrightnessForSkyBlocks(var2, var6, var3, 0);
      } else {
         return 0;
      }
   }

   public float getBrightness(float par1) {
      int var2 = MathHelper.floor_double(this.posX);
      int var3 = MathHelper.floor_double(this.posZ);
      if (this.worldObj.blockExists(var2, 0, var3)) {
         double var4 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66;
         int var6 = MathHelper.floor_double(this.posY - (double)this.yOffset + var4);
         if (!this.worldObj.isRemote && this.worldObj.getBiomeGenForCoords(var2, var3).rainfall == 0.0F && !this.worldObj.isBloodMoon24HourPeriod()) {
            this.worldObj.ignore_rain_and_thunder_for_next_BLV = true;
         }

         return this.worldObj.getLightBrightness(var2, var6, var3);
      } else {
         return 0.0F;
      }
   }

   public void setWorld(World par1World) {
      this.worldObj = par1World;
   }

   public void setPositionAndRotation(double par1, double par3, double par5, float par7, float par8) {
      this.prevPosX = this.posX = par1;
      this.prevPosY = this.posY = par3;
      this.prevPosZ = this.posZ = par5;
      this.prevRotationYaw = this.rotationYaw = par7;
      this.prevRotationPitch = this.rotationPitch = par8;
      this.ySize = 0.0F;
      double var9 = (double)(this.prevRotationYaw - par7);
      if (var9 < -180.0) {
         this.prevRotationYaw += 360.0F;
      }

      if (var9 >= 180.0) {
         this.prevRotationYaw -= 360.0F;
      }

      this.setPosition(this.posX, this.posY, this.posZ);
      this.setRotation(par7, par8);
   }

   public void setLocationAndAngles(double par1, double par3, double par5, float par7, float par8) {
      this.lastTickPosX = this.prevPosX = this.posX = par1;
      this.lastTickPosY = this.prevPosY = this.posY = par3 + (double)this.yOffset;
      this.lastTickPosZ = this.prevPosZ = this.posZ = par5;
      this.rotationYaw = par7;
      this.rotationPitch = par8;
      this.setPosition(this.posX, this.posY, this.posZ);
   }

   public float getDistanceToEntity(Entity par1Entity) {
      float var2 = (float)(this.posX - par1Entity.posX);
      float var3 = (float)(this.posY - par1Entity.posY);
      float var4 = (float)(this.posZ - par1Entity.posZ);
      return MathHelper.sqrt_float(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public double getDistanceSq(double par1, double par3, double par5) {
      double var7 = this.posX - par1;
      double var9 = this.posY - par3;
      double var11 = this.posZ - par5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double getDistance(double par1, double par3, double par5) {
      double var7 = this.posX - par1;
      double var9 = this.posY - par3;
      double var11 = this.posZ - par5;
      return (double)MathHelper.sqrt_double(var7 * var7 + var9 * var9 + var11 * var11);
   }

   public double getDistanceSqToBlock(int x, int y, int z) {
      double dx = this.posX - (double)x;
      double dy = this.posY - (double)y;
      double dz = this.posZ - (double)z;
      return dx * dx + dy * dy + dz * dz;
   }

   public double getDistanceSqToBlock(int x, int z) {
      double dx = this.posX - (double)x;
      double dz = this.posZ - (double)z;
      return dx * dx + dz * dz;
   }

   public double getDistanceSqToEntity(Entity par1Entity) {
      double var2;
      double var4;
      double var6;
      if (this.worldObj.isRemote) {
         var2 = (this instanceof EntityLivingBase ? ((EntityLivingBase)this).getFootPosY() : this.posY) - (par1Entity instanceof EntityLivingBase ? ((EntityLivingBase)par1Entity).getFootPosY() : par1Entity.posY);
         var4 = this.posX - par1Entity.posX;
         var6 = this.posZ - par1Entity.posZ;
         return var4 * var4 + var2 * var2 + var6 * var6;
      } else {
         var2 = this.posX - par1Entity.posX;
         var4 = this.posY - par1Entity.posY;
         var6 = this.posZ - par1Entity.posZ;
         return var2 * var2 + var4 * var4 + var6 * var6;
      }
   }

   public boolean isRepelledByCollisionWithPlayer() {
      return true;
   }

   public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
   }

   public void applyEntityCollision(Entity par1Entity) {
      if (this instanceof EntityPlayer || par1Entity instanceof EntityPlayer) {
         label92: {
            if (this.isEntityPlayer() && (((EntityPlayer)this).isGhost() || this.isZevimrgvInTournament())) {
               return;
            }

            if (!(par1Entity instanceof EntityPlayer) || !((EntityPlayer)par1Entity).isGhost() && !par1Entity.isZevimrgvInTournament()) {
               if (this.isRepelledByCollisionWithPlayer() && par1Entity.isRepelledByCollisionWithPlayer()) {
                  if (this instanceof EntityWoodSpider && this.isInsideOfMaterial(Material.tree_leaves)) {
                     return;
                  }

                  if (par1Entity instanceof EntityWoodSpider && par1Entity.isInsideOfMaterial(Material.tree_leaves)) {
                     return;
                  }
                  break label92;
               }

               return;
            }

            return;
         }
      }

      if (!(this instanceof EntityCubic) || !((EntityCubic)this).triesToDamageOnContact(par1Entity) || this.isRepelledByCollisionWithPlayer()) {
         if (!(par1Entity instanceof EntityCubic) || !((EntityCubic)par1Entity).triesToDamageOnContact(this) || par1Entity.isRepelledByCollisionWithPlayer()) {
            if (par1Entity.riddenByEntity != this && par1Entity.ridingEntity != this) {
               double var2 = par1Entity.posX - this.posX;
               double var4 = par1Entity.posZ - this.posZ;
               double var6 = MathHelper.abs_max(var2, var4);
               if (var6 >= 0.009999999776482582) {
                  var6 = (double)MathHelper.sqrt_double(var6);
                  var2 /= var6;
                  var4 /= var6;
                  double var8 = 1.0 / var6;
                  if (var8 > 1.0) {
                     var8 = 1.0;
                  }

                  var2 *= var8;
                  var4 *= var8;
                  var2 *= 0.05000000074505806;
                  var4 *= 0.05000000074505806;
                  var2 *= (double)(1.0F - this.entityCollisionReduction);
                  var4 *= (double)(1.0F - this.entityCollisionReduction);
                  this.addVelocity(-var2, 0.0, -var4);
                  par1Entity.addVelocity(var2, 0.0, var4);
               }
            }

         }
      }
   }

   public void addVelocity(double par1, double par3, double par5) {
      this.motionX += par1;
      this.motionY += par3;
      this.motionZ += par5;
      this.isAirBorne = true;
   }

   protected void setBeenAttacked() {
      this.velocityChanged = true;
   }

   public boolean isImmuneTo(DamageSource damage_source) {
      if (!this.isEntityInvulnerable() && !this.isDead) {
         if (damage_source.isGelatinousSphereDamage()) {
            EntityGelatinousSphere sphere = (EntityGelatinousSphere)damage_source.getImmediateEntity();
            if (this.isImmuneTo(sphere.getDamageType())) {
               return true;
            }
         }

         if (damage_source.isFireDamage() && !this.isHarmedByFire()) {
            return true;
         } else if (damage_source.isLavaDamage() && !this.isHarmedByLava()) {
            return true;
         } else if (damage_source.isPepsinDamage() && !this.isHarmedByPepsin()) {
            return true;
         } else if (damage_source.isAcidDamage() && !this.isHarmedByAcid()) {
            return true;
         } else if (damage_source.isExplosion() && this.isImmuneToExplosion()) {
            return true;
         } else if (damage_source.isDrowning() && !this.isHarmedByDrowning()) {
            return true;
         } else if (damage_source.isCactus() && !this.canBeDamagedByCacti()) {
            return true;
         } else if ((damage_source.isAnvil() || damage_source.isFallingBlock()) && !(this instanceof EntityLivingBase)) {
            return true;
         } else {
            return damage_source == DamageSource.wither && (!this.isEntityLivingBase() || !this.getAsEntityLivingBase().isEntityBiologicallyAlive());
         }
      } else {
         return true;
      }
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      if (this.onClient()) {
         Minecraft.setErrorMessage("attackEntityFrom: not meant to be called on client (" + damage + " vs " + this.getEntityName() + ")");
         return new EntityDamageResult(this);
      } else {
         EntityDamageResult result = new EntityDamageResult(this);
         if (damage.isNil()) {
            return null;
         } else if (!this.isEntityInvulnerable() && !this.isDead) {
            if (!damage.isAbsolute() && !damage.ignoreSpecificImmunities() && this.isImmuneTo(damage.getSource())) {
               return null;
            } else {
               if (!this.canBeKnockedBack()) {
                  if (damage.isKnockbackOnly()) {
                     return null;
                  }

                  if (damage.isPlayerThrownSnowball() && !this.canTakeDamageFromPlayerThrownSnowballs()) {
                     return null;
                  }
               }

               DebugAttack.start(this, damage);
               return result;
            }
         } else {
            return null;
         }
      }
   }

   public void onEntityDamaged(DamageSource damage_source, float amount) {
   }

   public final boolean isCorporeal() {
      return !(this instanceof EntityFX) && !(this instanceof EntityWeatherEffect);
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean canBePushed() {
      return false;
   }

   public void addToPlayerScore(Entity par1Entity, int par2) {
   }

   public boolean isInRangeToRenderVec3D(Vec3 par1Vec3) {
      double var2 = this.posX - par1Vec3.xCoord;
      double var4 = this.posY - par1Vec3.yCoord;
      double var6 = this.posZ - par1Vec3.zCoord;
      double var8 = var2 * var2 + var4 * var4 + var6 * var6;
      return this.isInRangeToRenderDist(var8);
   }

   public boolean isInRangeToRenderDist(double par1) {
      int render_distance = Minecraft.theMinecraft == null ? 0 : Minecraft.theMinecraft.gameSettings.getRenderDistance();
      float threshold_distance_sq = render_distance == 0 ? 2048.0F : (render_distance == 1 ? 1024.0F : 512.0F);
      boolean longer_distance_candidate = this instanceof EntityLivingBase;
      if (longer_distance_candidate && par1 < (double)threshold_distance_sq) {
         return true;
      } else if ((this instanceof EntityItem || this instanceof EntityXPOrb) && par1 < (double)(threshold_distance_sq / 2.0F)) {
         return true;
      } else {
         double var3 = this.boundingBox.getAverageEdgeLength();
         var3 *= 64.0 * this.renderDistanceWeight;
         if (longer_distance_candidate) {
            var3 *= 1.25;
         }

         return par1 < var3 * var3;
      }
   }

   public boolean writeMountToNBT(NBTTagCompound par1NBTTagCompound) {
      String var2 = this.getEntityString();
      if (!this.isDead && var2 != null) {
         par1NBTTagCompound.setString("id", var2);
         par1NBTTagCompound.setInteger("despawn_counter", this.despawn_counter);
         this.writeToNBT(par1NBTTagCompound);
         return true;
      } else {
         return false;
      }
   }

   public boolean isWrittenToChunkNBT() {
      return !this.isDead && this.getEntityString() != null && this.riddenByEntity == null;
   }

   public final boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound) {
      if (this.isWrittenToChunkNBT()) {
         String var2 = this.getEntityString();
         par1NBTTagCompound.setString("id", var2);
         this.writeToNBT(par1NBTTagCompound);
         return true;
      } else {
         return false;
      }
   }

   public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
      try {
         par1NBTTagCompound.setTag("Pos", this.newDoubleNBTList(this.posX, this.posY + (double)this.ySize, this.posZ));
         par1NBTTagCompound.setTag("Motion", this.newDoubleNBTList(this.motionX, this.motionY, this.motionZ));
         par1NBTTagCompound.setTag("Rotation", this.newFloatNBTList(this.rotationYaw, this.rotationPitch));
         par1NBTTagCompound.setFloat("FallDistance", this.fallDistance);
         par1NBTTagCompound.setShort("Fire", (short)this.fire);
         par1NBTTagCompound.setShort("Air", (short)this.getAir());
         par1NBTTagCompound.setBoolean("OnGround", this.onGround);
         par1NBTTagCompound.setInteger("Dimension", this.dimension);
         par1NBTTagCompound.setBoolean("Invulnerable", this.invulnerable);
         par1NBTTagCompound.setInteger("PortalCooldown", this.timeUntilPortal);
         par1NBTTagCompound.setLong("UUIDMost", this.entityUniqueID.getMostSignificantBits());
         par1NBTTagCompound.setLong("UUIDLeast", this.entityUniqueID.getLeastSignificantBits());
         this.writeEntityToNBT(par1NBTTagCompound);
         if (this.ridingEntity != null) {
            NBTTagCompound var2 = new NBTTagCompound("Riding");
            if (this.ridingEntity.writeMountToNBT(var2)) {
               par1NBTTagCompound.setTag("Riding", var2);
            }
         }

         par1NBTTagCompound.setInteger("spawn_x", this.spawn_x);
         par1NBTTagCompound.setInteger("spawn_y", this.spawn_y);
         par1NBTTagCompound.setInteger("spawn_z", this.spawn_z);
         par1NBTTagCompound.setInteger("despawn_counter", this.despawn_counter);
      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.makeCrashReport(var5, "Saving entity NBT");
         CrashReportCategory var4 = var3.makeCategory("Entity being saved");
         this.addEntityCrashInfo(var4);
         throw new ReportedException(var3);
      }
   }

   public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
      try {
         NBTTagList var2 = par1NBTTagCompound.getTagList("Pos");
         NBTTagList var6 = par1NBTTagCompound.getTagList("Motion");
         NBTTagList var7 = par1NBTTagCompound.getTagList("Rotation");
         this.motionX = ((NBTTagDouble)var6.tagAt(0)).data;
         this.motionY = ((NBTTagDouble)var6.tagAt(1)).data;
         this.motionZ = ((NBTTagDouble)var6.tagAt(2)).data;
         if (Math.abs(this.motionX) > 10.0) {
            this.motionX = 0.0;
         }

         if (Math.abs(this.motionY) > 10.0) {
            this.motionY = 0.0;
         }

         if (Math.abs(this.motionZ) > 10.0) {
            this.motionZ = 0.0;
         }

         this.prevPosX = this.lastTickPosX = this.posX = ((NBTTagDouble)var2.tagAt(0)).data;
         this.prevPosY = this.lastTickPosY = this.posY = ((NBTTagDouble)var2.tagAt(1)).data;
         this.prevPosZ = this.lastTickPosZ = this.posZ = ((NBTTagDouble)var2.tagAt(2)).data;
         this.prevRotationYaw = this.rotationYaw = ((NBTTagFloat)var7.tagAt(0)).data;
         this.prevRotationPitch = this.rotationPitch = ((NBTTagFloat)var7.tagAt(1)).data;
         this.fallDistance = par1NBTTagCompound.getFloat("FallDistance");
         this.fire = par1NBTTagCompound.getShort("Fire");
         this.setAir(par1NBTTagCompound.getShort("Air"));
         this.onGround = par1NBTTagCompound.getBoolean("OnGround");
         this.dimension = par1NBTTagCompound.getInteger("Dimension");
         this.invulnerable = par1NBTTagCompound.getBoolean("Invulnerable");
         this.timeUntilPortal = par1NBTTagCompound.getInteger("PortalCooldown");
         if (par1NBTTagCompound.hasKey("UUIDMost") && par1NBTTagCompound.hasKey("UUIDLeast")) {
            this.entityUniqueID = new UUID(par1NBTTagCompound.getLong("UUIDMost"), par1NBTTagCompound.getLong("UUIDLeast"));
         }

         this.setPosition(this.posX, this.posY, this.posZ);
         this.setRotation(this.rotationYaw, this.rotationPitch);
         this.spawn_x = par1NBTTagCompound.getInteger("spawn_x");
         this.spawn_y = par1NBTTagCompound.getInteger("spawn_y");
         this.spawn_z = par1NBTTagCompound.getInteger("spawn_z");
         this.despawn_counter = par1NBTTagCompound.getInteger("despawn_counter");
         this.readEntityFromNBT(par1NBTTagCompound);
         if (this.shouldSetPosAfterLoading()) {
            this.setPosition(this.posX, this.posY, this.posZ);
         }

      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.makeCrashReport(var5, "Loading entity NBT");
         CrashReportCategory var4 = var3.makeCategory("Entity being loaded");
         this.addEntityCrashInfo(var4);
         throw new ReportedException(var3);
      }
   }

   protected boolean shouldSetPosAfterLoading() {
      return true;
   }

   protected final String getEntityString() {
      return EntityList.getEntityString(this);
   }

   protected abstract void readEntityFromNBT(NBTTagCompound var1);

   protected abstract void writeEntityToNBT(NBTTagCompound var1);

   public void onChunkLoad() {
   }

   protected NBTTagList newDoubleNBTList(double... par1ArrayOfDouble) {
      NBTTagList var2 = new NBTTagList();
      double[] var3 = par1ArrayOfDouble;
      int var4 = par1ArrayOfDouble.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         double var6 = var3[var5];
         var2.appendTag(new NBTTagDouble((String)null, var6));
      }

      return var2;
   }

   protected NBTTagList newFloatNBTList(float... par1ArrayOfFloat) {
      NBTTagList var2 = new NBTTagList();
      float[] var3 = par1ArrayOfFloat;
      int var4 = par1ArrayOfFloat.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         float var6 = var3[var5];
         var2.appendTag(new NBTTagFloat((String)null, var6));
      }

      return var2;
   }

   public float getShadowSize() {
      return this.height / 2.0F;
   }

   public EntityItem dropItem(Item item) {
      return this.dropItem(item, 1);
   }

   public EntityItem dropItem(Item item, int quantity) {
      return item != null && quantity >= 1 ? this.dropItem(item.itemID, quantity) : null;
   }

   public EntityItem dropItem(int item_id, int quantity) {
      return item_id <= 0 ? null : this.dropItem(item_id, quantity, this.height * 0.5F);
   }

   public EntityItem dropItem(int item_id, int quantity, float y_offset) {
      return item_id > 0 && quantity >= 1 ? this.dropItemStack(new ItemStack(item_id, quantity, 0), y_offset) : null;
   }

   public EntityItem dropItemStack(ItemStack item_stack) {
      return this.dropItemStack(item_stack, this.height * 0.5F);
   }

   public EntityItem dropItemStack(ItemStack item_stack, float y_offset) {
      if (item_stack != null && item_stack.stackSize != 0) {
         EntityItem entity_item = new EntityItem(this.worldObj, this.posX, this.posY + (double)y_offset, this.posZ, item_stack);
         entity_item.delayBeforeCanPickup = 10;
         if (this.isEntityPlayer()) {
            entity_item.age = -18000;
            entity_item.dropped_by_player = true;
         }

         this.worldObj.spawnEntityInWorld(entity_item);
         if (this.isBurning()) {
            entity_item.setFire(this.rand.nextInt(7) + 2);
         }

         return entity_item;
      } else {
         return null;
      }
   }

   public boolean isEntityAlive() {
      return !this.isDead;
   }

   public boolean isEntityInsideOpaqueBlock() {
      for(int var1 = 0; var1 < 8; ++var1) {
         float var2 = ((float)((var1 >> 0) % 2) - 0.5F) * this.width * 0.8F;
         float var3 = ((float)((var1 >> 1) % 2) - 0.5F) * 0.1F;
         float var4 = ((float)((var1 >> 2) % 2) - 0.5F) * this.width * 0.8F;
         int var5 = MathHelper.floor_double(this.posX + (double)var2);
         int var6 = MathHelper.floor_double(this.posY + (double)this.getEyeHeight() + (double)var3);
         int var7 = MathHelper.floor_double(this.posZ + (double)var4);
         if (this.worldObj.isBlockNormalCube(var5, var6, var7)) {
            return true;
         }
      }

      return false;
   }

   public boolean isSilverfishInsideDamagingOpaqueBlock() {
      for(int var1 = 0; var1 < 8; ++var1) {
         float var2 = ((float)((var1 >> 0) % 2) - 0.5F) * this.width * 0.8F;
         float var3 = ((float)((var1 >> 1) % 2) - 0.5F) * 0.1F;
         float var4 = ((float)((var1 >> 2) % 2) - 0.5F) * this.width * 0.8F;
         int var5 = MathHelper.floor_double(this.posX + (double)var2);
         int var6 = MathHelper.floor_double(this.posY + (double)this.getEyeHeight() + (double)var3);
         int var7 = MathHelper.floor_double(this.posZ + (double)var4);
         Block block = this.worldObj.getBlock(var5, var6, var7);
         if (Block.isNormalCube(block) && block != Block.sand && block != Block.dirt && block != Block.gravel && block != Block.slowSand) {
            return true;
         }
      }

      return false;
   }

   public AxisAlignedBB getCollisionBox(Entity par1Entity) {
      return null;
   }

   public void updateRidden() {
      if (this.ridingEntity.isDead) {
         this.ridingEntity = null;
      } else {
         this.motionX = 0.0;
         this.motionY = 0.0;
         this.motionZ = 0.0;
         this.onUpdate();
         if (this.ridingEntity != null) {
            this.ridingEntity.updateRiderPosition();
            this.entityRiderYawDelta += (double)(this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);

            for(this.entityRiderPitchDelta += (double)(this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch); this.entityRiderYawDelta >= 180.0; this.entityRiderYawDelta -= 360.0) {
            }

            while(this.entityRiderYawDelta < -180.0) {
               this.entityRiderYawDelta += 360.0;
            }

            while(this.entityRiderPitchDelta >= 180.0) {
               this.entityRiderPitchDelta -= 360.0;
            }

            while(this.entityRiderPitchDelta < -180.0) {
               this.entityRiderPitchDelta += 360.0;
            }

            double var1 = this.entityRiderYawDelta * 0.5;
            double var3 = this.entityRiderPitchDelta * 0.5;
            float var5 = 10.0F;
            if (var1 > (double)var5) {
               var1 = (double)var5;
            }

            if (var1 < (double)(-var5)) {
               var1 = (double)(-var5);
            }

            if (var3 > (double)var5) {
               var3 = (double)var5;
            }

            if (var3 < (double)(-var5)) {
               var3 = (double)(-var5);
            }

            this.entityRiderYawDelta -= var1;
            this.entityRiderPitchDelta -= var3;
         }
      }

   }

   public void updateRiderPosition() {
      if (this.riddenByEntity != null) {
         this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
      }

   }

   public double getYOffset() {
      return (double)this.yOffset;
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.75;
   }

   public void mountEntity(Entity par1Entity) {
      this.entityRiderPitchDelta = 0.0;
      this.entityRiderYawDelta = 0.0;
      if (par1Entity == null) {
         if (this.ridingEntity != null) {
            this.setLocationAndAngles(this.ridingEntity.posX, this.ridingEntity.boundingBox.minY + (double)this.ridingEntity.height, this.ridingEntity.posZ, this.rotationYaw, this.rotationPitch);
            this.ridingEntity.riddenByEntity = null;
         }

         this.ridingEntity = null;
      } else {
         if (this.ridingEntity != null) {
            this.ridingEntity.riddenByEntity = null;
         }

         this.ridingEntity = par1Entity;
         par1Entity.riddenByEntity = this;
      }

   }

   public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
      this.setPosition(par1, par3, par5);
      this.setRotation(par7, par8);
      List var10 = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.contract(0.03125, 0.0, 0.03125));
      if (!var10.isEmpty()) {
         double var11 = 0.0;

         for(int var13 = 0; var13 < var10.size(); ++var13) {
            AxisAlignedBB var14 = (AxisAlignedBB)var10.get(var13);
            if (var14.maxY > var11) {
               var11 = var14.maxY;
            }
         }

         par3 += var11 - this.boundingBox.minY;
         this.setPosition(par1, par3, par5);
      }

   }

   public float getCollisionBorderSize(Entity for_raycast_from_this_entity) {
      return !(for_raycast_from_this_entity instanceof IProjectile) && !(for_raycast_from_this_entity instanceof EntityFireball) && !(for_raycast_from_this_entity instanceof EntityFishHook) ? 0.1F : 0.3F;
   }

   public Vec3 getLookVec() {
      return null;
   }

   public void setInPortal(int destination_dimension_id) {
      if (this.timeUntilPortal > 0) {
         this.timeUntilPortal = this.getPortalCooldown();
      } else {
         double var1 = this.prevPosX - this.posX;
         double var3 = this.prevPosZ - this.posZ;
         if (!this.worldObj.isRemote && !this.inPortal) {
            this.teleportDirection = Direction.getMovementDirection(var1, var3);
         }

         this.inPortal = true;
         this.portal_destination_dimension_id = destination_dimension_id;
      }

   }

   public int getPortalCooldown() {
      return 900;
   }

   public void setVelocity(double par1, double par3, double par5) {
      this.motionX = par1;
      this.motionY = par3;
      this.motionZ = par5;
   }

   public void handleHealthUpdate(EnumEntityState par1) {
   }

   public void performHurtAnimation() {
   }

   public ItemStack[] getLastActiveItems() {
      return null;
   }

   public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack) {
   }

   public boolean isBurning() {
      return this.canCatchFire() && (this.fire > 0 || this.getFlag(0));
   }

   public boolean isRiding() {
      return this.ridingEntity != null;
   }

   public boolean isSneaking() {
      return this.getFlag(1);
   }

   public void setSneaking(boolean par1) {
      this.setFlag(1, par1);
   }

   public boolean isSprinting() {
      return this.getFlag(3);
   }

   public void setSprinting(boolean par1) {
      this.setFlag(3, par1);
   }

   public boolean isInvisible() {
      return this.getFlag(5);
   }

   public boolean isInvisibleToPlayer(EntityPlayer par1EntityPlayer) {
      return this.isInvisible();
   }

   public void setInvisible(boolean par1) {
      this.setFlag(5, par1);
   }

   public boolean isEating() {
      return this.getFlag(4);
   }

   public void setEating(boolean par1) {
      this.setFlag(4, par1);
   }

   protected boolean getFlag(int par1) {
      return (this.dataWatcher.getWatchableObjectByte(0) & 1 << par1) != 0;
   }

   protected void setFlag(int par1, boolean par2) {
      byte var3 = this.dataWatcher.getWatchableObjectByte(0);
      if (par2) {
         this.dataWatcher.updateObject(0, (byte)(var3 | 1 << par1));
      } else {
         this.dataWatcher.updateObject(0, (byte)(var3 & ~(1 << par1)));
      }

   }

   public int getAir() {
      return this.dataWatcher.getWatchableObjectShort(1);
   }

   public void setAir(int par1) {
      this.dataWatcher.updateObject(1, (short)par1);
   }

   public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt) {
      this.dealFireDamage(5);
      ++this.fire;
      if (this.fire == 0) {
         this.setFire(8);
      }

   }

   public void onKillEntity(EntityLivingBase par1EntityLivingBase) {
   }

   protected int pushOutOfBlocks() {
      if (!this.worldObj.isRemote && this.ticksExisted >= 2) {
         double original_center_y = (this.boundingBox.minY + this.boundingBox.maxY) / 2.0;
         double center_x = this.posX;
         double center_y = original_center_y;
         double center_z = this.posZ;
         int x = center_x < 0.0 ? (int)center_x - 1 : (int)center_x;
         int y = center_y < 0.0 ? (int)center_y - 1 : (int)center_y;
         int z = center_z < 0.0 ? (int)center_z - 1 : (int)center_z;
         List collisions;
         if (!this.worldObj.isBlockFullSolidCube(x, y, z) || this.worldObj.getBlock(x, y, z) == Block.cauldron) {
            collisions = this.worldObj.getCollidingBlockBounds(this.boundingBox, this);
            if (collisions.isEmpty()) {
               return 0;
            }
         }

         if (this instanceof EntityItem && this.worldObj.getBlock(x, y, z) instanceof BlockLeaves) {
            collisions = this.worldObj.getCollidingBlockBounds(this.boundingBox, this);
            if (collisions.isEmpty()) {
               return 0;
            }
         }

         int max_escape_range = this instanceof EntityXPOrb ? 2 : 1;
         if (this instanceof EntityLivestock || this.isEntityPlayer()) {
            max_escape_range = 3;
         }

         int matrix_size = max_escape_range * 2 + 1;
         int matrix_size_sq = matrix_size * matrix_size;
         boolean can_escape = false;
         boolean[] is_candidate_block = new boolean[matrix_size * matrix_size * matrix_size];

         int dy;
         for(int dx = -max_escape_range; dx <= max_escape_range; ++dx) {
            for(dy = -max_escape_range; dy <= max_escape_range; ++dy) {
               for(int dz = -max_escape_range; dz <= max_escape_range; ++dz) {
                  if (this.worldObj.blockExists(x + dx, y + dy, z + dz) && !this.worldObj.isBlockFullSolidCube(x + dx, y + dy, z + dz)) {
                     can_escape = true;
                     is_candidate_block[dx + max_escape_range + (dy + max_escape_range) * matrix_size + (dz + max_escape_range) * matrix_size_sq] = true;
                  }
               }
            }
         }

         if (!can_escape) {
            return -1;
         } else {
            AxisAlignedBB trial_bounding_box = this.boundingBox.copy();
            dy = (int)(Math.random() * 2.147483647E9);
            float range = 0.1F;

            while((range += 0.001F) < (float)(max_escape_range + 1)) {
               ++dy;
               double dPosX = RNG.double_1[dy & 32767] * (double)range * 2.0 - (double)range;
               ++dy;
               double dPosY = RNG.double_1[dy & 32767] * (double)range * 2.0 - (double)range;
               ++dy;
               double dPosZ = RNG.double_1[dy & 32767] * (double)range * 2.0 - (double)range;
               center_x = this.posX + dPosX;
               center_y = original_center_y + dPosY;
               center_z = this.posZ + dPosZ;
               int trial_x = center_x < 0.0 ? (int)center_x - 1 : (int)center_x;
               int trial_y = center_y < 0.0 ? (int)center_y - 1 : (int)center_y;
               int trial_z = center_z < 0.0 ? (int)center_z - 1 : (int)center_z;
               int dx = trial_x - x;
               int dy_ = trial_y - y;
               int dz = trial_z - z;
               if (dx >= -max_escape_range && dx <= max_escape_range && dy_ >= -max_escape_range && dy_ <= max_escape_range && dz >= -max_escape_range && dz <= max_escape_range && is_candidate_block[dx + max_escape_range + (dy_ + max_escape_range) * matrix_size + (dz + max_escape_range) * matrix_size_sq]) {
                  trial_bounding_box.setBounds(this.boundingBox.minX + dPosX, this.boundingBox.minY + dPosY, this.boundingBox.minZ + dPosZ, this.boundingBox.maxX + dPosX, this.boundingBox.maxY + dPosY, this.boundingBox.maxZ + dPosZ);
                  List collisions_ = this.worldObj.getCollidingBlockBounds(trial_bounding_box, this);
                  if (collisions_.isEmpty()) {
                     this.setPosition(this.posX + dPosX, this.posY + dPosY, this.posZ + dPosZ);
                     this.send_position_update_immediately = true;
                     this.motionX = this.motionY = this.motionZ = 0.0;
                     this.sync_last_tick_pos_on_next_update = true;
                     return 1;
                  }
               }
            }

            return -1;
         }
      } else {
         return 0;
      }
   }

   public void setInWeb() {
      this.isInWeb = true;
      this.fallDistance = 0.0F;
   }

   public String getEntityName() {
      String var1 = EntityList.getEntityString(this);
      if (var1 == null) {
         var1 = "generic";
      }

      return StatCollector.translateToLocal("entity." + var1 + ".name");
   }

   public Entity[] getParts() {
      return null;
   }

   public boolean isEntityEqual(Entity par1Entity) {
      return this == par1Entity;
   }

   public float getRotationYawHead() {
      return 0.0F;
   }

   public void setRotationYawHead(float par1) {
   }

   public boolean canAttackWithItem() {
      return true;
   }

   public boolean isEntityUndead() {
      return false;
   }

   public String toString() {
      return String.format("%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getEntityName(), this.entityId, this.worldObj == null ? "~NULL~" : this.worldObj.getWorldInfo().getWorldName(), this.posX, this.posY, this.posZ);
   }

   public boolean isEntityInvulnerable() {
      return this.invulnerable;
   }

   public void setEntityInvulnerable(boolean invulnerable) {
      this.invulnerable = invulnerable;
   }

   public void copyLocationAndAnglesFrom(Entity par1Entity) {
      this.setLocationAndAngles(par1Entity.posX, par1Entity.posY, par1Entity.posZ, par1Entity.rotationYaw, par1Entity.rotationPitch);
   }

   public void copyDataFrom(Entity par1Entity, boolean par2) {
      NBTTagCompound var3 = new NBTTagCompound();
      par1Entity.writeToNBT(var3);
      this.readFromNBT(var3);
      this.timeUntilPortal = par1Entity.timeUntilPortal;
      this.teleportDirection = par1Entity.teleportDirection;
   }

   public void travelToDimension(int par1) {
      if (!this.worldObj.isRemote && !this.isDead) {
         this.worldObj.theProfiler.startSection("changeDimension");
         MinecraftServer var2 = MinecraftServer.getServer();
         int var3 = this.dimension;
         WorldServer var4 = var2.worldServerForDimension(var3);
         WorldServer var5 = var2.worldServerForDimension(par1);
         this.dimension = par1;
         if (var3 == 1 && par1 == 1) {
            var5 = var2.worldServerForDimension(0);
            this.dimension = 0;
         }

         this.worldObj.removeEntity(this);
         this.isDead = false;
         this.worldObj.theProfiler.startSection("reposition");
         var2.getConfigurationManager().transferEntityToWorld(this, var3, var4, var5);
         this.worldObj.theProfiler.endStartSection("reloading");
         Entity var6 = EntityList.createEntityByName(EntityList.getEntityString(this), var5);
         this.setDead();
         if (this.chunk_added_to != null) {
            if (Minecraft.inDevMode()) {
               System.out.println("travelToDimension: Removed " + this.getEntityName() + " (UUID=" + this.getUniqueID() + ") from chunk in " + this.chunk_added_to.worldObj.getDimensionName());
            }

            this.removeFromChunk();
         }

         if (var6 != null) {
            var6.copyDataFrom(this, true);
            if (var3 == 1 && par1 == 1) {
               ChunkCoordinates var7 = var5.getSpawnPoint();
               var7.posY = this.worldObj.getTopSolidOrLiquidBlock(var7.posX, var7.posZ);
               var6.setLocationAndAngles((double)var7.posX, (double)var7.posY, (double)var7.posZ, var6.rotationYaw, var6.rotationPitch);
            }

            if (Minecraft.inDevMode()) {
               System.out.println("travelToDimension: Spawning " + var6.getEntityName() + " in " + var5.getDimensionName() + " (UUID=" + var6.getUniqueID() + ")");
            }

            var5.spawnEntityInWorld(var6);
         }

         this.worldObj.theProfiler.endSection();
         var4.resetUpdateEntityTick();
         var5.resetUpdateEntityTick();
         this.worldObj.theProfiler.endSection();
      }

   }

   public boolean shouldExplodeBlock(Explosion par1Explosion, World par2World, int par3, int par4, int par5, int par6, float par7) {
      return true;
   }

   public int getMaxSafePointTries() {
      return 3;
   }

   public int getTeleportDirection() {
      return this.teleportDirection;
   }

   public boolean doesEntityNotTriggerPressurePlate() {
      return false;
   }

   public void addEntityCrashInfo(CrashReportCategory par1CrashReportCategory) {
      par1CrashReportCategory.addCrashSectionCallable("Entity Type", new CallableEntityType(this));
      par1CrashReportCategory.addCrashSection("Entity ID", this.entityId);
      par1CrashReportCategory.addCrashSectionCallable("Entity Name", new CallableEntityName(this));
      par1CrashReportCategory.addCrashSection("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.posX, this.posY, this.posZ));
      par1CrashReportCategory.addCrashSection("Entity's Block location", CrashReportCategory.getLocationInfo(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)));
      par1CrashReportCategory.addCrashSection("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.motionX, this.motionY, this.motionZ));
   }

   public boolean canRenderOnFire() {
      return this.isBurning();
   }

   public boolean isExpectedToHaveUUID() {
      if (this.worldObj == null) {
         boolean is_server_thread = Minecraft.isServerThread();
         if (is_server_thread) {
            Debug.setErrorMessage("isExpectedToHaveUUID: worldObj was null for " + this.getClass() + " on server thread");
            Debug.printStackTrace();
         }

         return is_server_thread;
      } else {
         return this.onServer();
      }
   }

   public UUID getUniqueID() {
      if (!this.isExpectedToHaveUUID()) {
         Minecraft.setErrorMessage("getUniqueID: entity not expected to have a UUID " + this);
         (new Exception()).printStackTrace();
      }

      if (this.entityUniqueID == null) {
         Minecraft.setErrorMessage("getUniqueID: was null for " + this);
         (new Exception()).printStackTrace();
      }

      return this.entityUniqueID;
   }

   public UUID getUniqueIDSilent() {
      return this.entityUniqueID;
   }

   public boolean isPushedByWater() {
      return true;
   }

   public String getTranslatedEntityName() {
      return this.getEntityName();
   }

   public boolean hasSkyAbove() {
      return this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
   }

   public boolean isOutdoors() {
      return this.worldObj.isOutdoors(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
   }

   public boolean isInSunlight() {
      return this.worldObj.isInSunlight(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
   }

   public boolean isInRain() {
      return this.worldObj.isInRain(this.getBlockPosX(), this.getBlockPosY(), this.getBlockPosZ());
   }

   public boolean isInPrecipitation() {
      return this.worldObj.isPrecipitatingAt(this.getBlockPosX(), this.getBlockPosY(), this.getBlockPosZ());
   }

   public double getDistanceSqToSpawnPoint() {
      World var10000 = this.worldObj;
      return World.getDistanceSqFromDeltas(this.posX - (double)this.spawn_x, this.posY - (double)this.spawn_y, this.posZ - (double)this.spawn_z);
   }

   public final double getDistanceSqToWorldSpawnPoint(boolean include_delta_y) {
      World var10000 = this.worldObj;
      return World.getDistanceSqFromDeltas(this.posX - (double)this.worldObj.getSpawnX(), include_delta_y ? this.posY - (double)this.worldObj.getSpawnY() : 0.0, this.posZ - (double)this.worldObj.getSpawnZ());
   }

   public final double getDistanceToWorldSpawnPoint(boolean include_delta_y) {
      return (double)MathHelper.sqrt_double(this.getDistanceSqToWorldSpawnPoint(include_delta_y));
   }

   public boolean isNearToBlock(Block block, int max_horizontal_distance, int max_vertical_distance) {
      return this.worldObj.blockTypeIsNearTo(block.blockID, this.posX, this.posY, this.posZ, max_horizontal_distance, max_vertical_distance);
   }

   public Entity getNearbyEntityByUniqueID(String unique_id_string) {
      if (unique_id_string != null && !unique_id_string.isEmpty()) {
         List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(32.0, 8.0, 32.0));
         Iterator i = entities.iterator();

         Entity entity;
         do {
            if (!i.hasNext()) {
               return null;
            }

            entity = (Entity)i.next();
         } while(!entity.getUniqueID().toString().equals(unique_id_string));

         return entity;
      } else {
         return null;
      }
   }

   public List getNearbyEntities(float max_horizontal_distance, float max_vertical_distance) {
      return this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand((double)max_horizontal_distance, (double)max_vertical_distance, (double)max_horizontal_distance));
   }

   public final int getBlockPosX() {
      return MathHelper.floor_double(this.posX);
   }

   public int getBlockPosY() {
      return MathHelper.floor_double(this.posY);
   }

   public final int getBlockPosZ() {
      return MathHelper.floor_double(this.posZ);
   }

   public final int getChunkPosX() {
      return this.getBlockPosX() >> 4;
   }

   public final int getChunkPosZ() {
      return this.getBlockPosZ() >> 4;
   }

   public int getChunkCurrentlyInSectionIndex() {
      double effective_pos_y;
      if (this instanceof EntityLivingBase) {
         effective_pos_y = this.getAsEntityLivingBase().getFootPosY();
      } else {
         effective_pos_y = this.posY;
      }

      int index = MathHelper.floor_double(effective_pos_y / 16.0);
      if (index < 0) {
         index = 0;
      } else if (index >= 16) {
         index = 15;
      }

      return index;
   }

   private boolean isRestingOnBlock(int x, int y, int z, double test_y, int[] dy) {
      dy[0] = 0;
      if (!this.worldObj.isAirOrPassableBlock(x, y, z, true)) {
         return true;
      } else {
         Block block = this.worldObj.getBlock(x, y, z);
         if (block == Block.snow) {
            block.setBlockBoundsBasedOnStateAndNeighbors(this.worldObj, x, y, z);
            if (test_y < (double)y + block.maxY[Minecraft.getThreadIndex()]) {
               return true;
            }
         }

         block = this.worldObj.getBlock(x, y - 1, z);
         if (block != null) {
            block.setBlockBoundsBasedOnStateAndNeighbors(this.worldObj, x, y - 1, z);
            double maxY = block.maxY[Minecraft.getThreadIndex()];
            if (block instanceof BlockFence) {
               maxY = 1.5;
            }

            if (test_y < (double)(y - 1) + maxY && !this.worldObj.isAirOrPassableBlock(x, y - 1, z, true)) {
               dy[0] = -1;
               return true;
            }
         }

         return false;
      }
   }

   public BlockInfo getBlockRestingOn(float y_allowance) {
      return this.getBlockRestingOn3();
   }

   public final BlockInfo getBlockRestingOn3() {
      if (this.onGround && this.ridingEntity == null) {
         BlockInfo info = null;
         double highest_max_y = 0.0;
         double shortest_distance_sq = 0.0;
         int min_x = this.boundingBox.getBlockCoordForMinX();
         int max_x = this.boundingBox.getBlockCoordForMaxX();
         int min_y = this.boundingBox.getBlockCoordForMinY();
         int max_y = this.boundingBox.getBlockCoordForMaxY();
         int min_z = this.boundingBox.getBlockCoordForMinZ();
         int max_z = this.boundingBox.getBlockCoordForMaxZ();

         for(int x = min_x; x <= max_x; ++x) {
            for(int z = min_z; z <= max_z; ++z) {
               for(int y = min_y - 1; y <= max_y; ++y) {
                  Block block = this.worldObj.getBlock(x, y, z);
                  if (block != null) {
                     AxisAlignedBB bb = block.getCollisionBoundsCombined(this.worldObj, x, y, z, this, true);
                     if (bb != null && bb.maxY == this.boundingBox.minY) {
                        double dx;
                        double dz;
                        if (info != null && !(bb.maxY > highest_max_y)) {
                           if (bb.maxY == highest_max_y) {
                              dx = (double)x + 0.5 - this.posX;
                              dz = (double)z + 0.5 - this.posZ;
                              double distance_sq = dx * dx + dz * dz;
                              if (distance_sq < shortest_distance_sq) {
                                 info = new BlockInfo(block, x, y, z, this.worldObj.getBlockMetadata(x, y, z));
                                 shortest_distance_sq = distance_sq;
                              }
                           }
                        } else {
                           info = new BlockInfo(block, x, y, z, this.worldObj.getBlockMetadata(x, y, z));
                           highest_max_y = bb.maxY;
                           dx = (double)x + 0.5 - this.posX;
                           dz = (double)z + 0.5 - this.posZ;
                           shortest_distance_sq = dx * dx + dz * dz;
                        }
                     }
                  }
               }
            }
         }

         return info;
      } else {
         return null;
      }
   }

   public boolean isSuspendedInLiquid() {
      if (this.ridingEntity == null && !this.onGround) {
         if (!this.isInWater() && !this.handleLavaMovement()) {
            return false;
         } else {
            return this.getBlockRestingOn(0.2F) == null;
         }
      } else {
         return false;
      }
   }

   public boolean isImmuneToExplosion() {
      return false;
   }

   public boolean handleExplosion(Explosion explosion) {
      return false;
   }

   public void handlePacket89(Packet89PlaySoundOnServerAtEntity packet) {
   }

   public int getFragParticle() {
      return -1;
   }

   public int getNumFragParticles() {
      return 40;
   }

   public boolean spawnFragParticles() {
      if (!this.worldObj.isRemote) {
         return false;
      } else {
         int frag_item_id = this.getFragParticle();
         if (frag_item_id < 0) {
            return false;
         } else {
            int frag_particles = this.getNumFragParticles();

            for(int i = 0; i < frag_particles; ++i) {
               float vel_x = this.rand.nextFloat() * 0.2F - 0.1F;
               float vel_y = this.rand.nextFloat() * 0.2F - 0.1F;

               float vel_z;
               for(vel_z = this.rand.nextFloat() * 0.2F - 0.1F; vel_x * vel_x + vel_y * vel_y + vel_z * vel_z < 0.25F; vel_z *= 2.0F) {
                  vel_x *= 2.0F;
                  vel_y *= 2.0F;
               }

               while(vel_x * vel_x + vel_y * vel_y + vel_z * vel_z < 1.0F) {
                  vel_x *= 1.1F;
                  vel_y *= 1.1F;
                  vel_z *= 1.1F;
               }

               this.worldObj.spawnParticleEx(EnumParticle.iconcrack, frag_item_id, 0, this.posX + (double)(this.rand.nextFloat() * this.width) - (double)(this.width / 2.0F), this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width) - (double)(this.width / 2.0F), (double)vel_x, (double)vel_y, (double)vel_z);
            }

            return frag_particles > 0;
         }
      }
   }

   public boolean drawBackFaces() {
      return true;
   }

   public boolean canBeDamagedByCacti() {
      return false;
   }

   public final boolean isEntityLivingBase() {
      return this instanceof EntityLivingBase;
   }

   public final boolean isEntityLiving() {
      return this instanceof EntityLiving;
   }

   public boolean canBeAttackedBy(EntityLivingBase attacker) {
      if (!this.worldObj.isRemote) {
         Minecraft.setErrorMessage("canBeAttackedBy: to be called on client only");
      }

      return !this.isEntityUndead() || this.rand.nextInt(4) <= 0 || !attacker.hasCurse(Curse.fear_of_undead, true);
   }

   public void onTransferToWorld() {
   }

   public final boolean hasModelItem() {
      return this.getModelItem() != null;
   }

   public Item getModelItem() {
      return null;
   }

   public int getFireResistance() {
      return 1;
   }

   public void refreshDespawnCounter(int value) {
      if (this.despawn_counter > value) {
         this.despawn_counter = value;
      }

   }

   public final EnumDirection getDirectionFromYaw() {
      return EnumDirection.getDirectionFromYaw(this.rotationYaw);
   }

   public final EnumDirection getDirectionFromPitch() {
      return EnumDirection.getDirectionFromPitch(this.rotationPitch);
   }

   public final boolean onClient() {
      return this.worldObj.isRemote;
   }

   public final boolean onServer() {
      return !this.worldObj.isRemote;
   }

   public final WorldClient getWorldClient() {
      return (WorldClient)this.worldObj;
   }

   public final WorldServer getWorldServer() {
      return (WorldServer)this.worldObj;
   }

   public final int getRotationYawAsSixteenths() {
      return Math.round(this.rotationYaw / 22.5F) & 15;
   }

   public final World getWorld() {
      return this.worldObj;
   }

   public final EntityPlayer getAsPlayer() {
      return (EntityPlayer)this;
   }

   public final EntityPlayerMP getAsEntityPlayerMP() {
      return (EntityPlayerMP)this;
   }

   public final EntityLivingBase getAsEntityLivingBase() {
      return (EntityLivingBase)this;
   }

   public final EntityLiving getAsEntityLiving() {
      return (EntityLiving)this;
   }

   public final EntityAnimal getAsEntityAnimal() {
      return (EntityAnimal)this;
   }

   public final EntityTameable getAsEntityTameable() {
      return (EntityTameable)this;
   }

   public float adjustPlayerReachForAttacking(EntityPlayer player, float reach) {
      return reach;
   }

   public float adjustPlayerReachForInteraction(EntityPlayer player, float reach) {
      return reach;
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      return false;
   }

   public final boolean doesYawHaveNorthComponent() {
      return EnumDirection.doesYawHaveNorthComponent(this.rotationYaw);
   }

   public final boolean doesYawHaveSouthComponent() {
      return EnumDirection.doesYawHaveSouthComponent(this.rotationYaw);
   }

   public final boolean doesYawHaveWestComponent() {
      return EnumDirection.doesYawHaveWestComponent(this.rotationYaw);
   }

   public final boolean doesYawHaveEastComponent() {
      return EnumDirection.doesYawHaveEastComponent(this.rotationYaw);
   }

   public String getPosString() {
      return this.posX + "," + this.posY + "," + this.posZ;
   }

   public String getBlockPosString() {
      return this.getBlockPosX() + "," + this.getBlockPosY() + "," + this.getBlockPosZ();
   }

   public boolean isWithinTournamentSafeZone() {
      return this.worldObj.isWithinTournamentSafeZone(this.getBlockPosX(), this instanceof EntityLivingBase ? this.getAsEntityLivingBase().getFootBlockPosY() : this.getBlockPosY(), this.getBlockPosZ());
   }

   public boolean isZevimrgvInTournament() {
      return false;
   }

   public final boolean isArrow() {
      return this instanceof EntityArrow;
   }

   public boolean isHarmedByDrowning() {
      return false;
   }

   public boolean canBeKnockedBack() {
      return false;
   }

   public boolean canTakeDamageFromPlayerThrownSnowballs() {
      return false;
   }

   public void transferToChunk(Chunk chunk) {
      if (chunk == null && this.chunk_added_to == null) {
         Minecraft.setErrorMessage("transferToChunk: from null to null?");
         (new Exception()).printStackTrace();
      }

      if (this.chunk_added_to != null) {
         this.chunk_added_to.removeEntity(this);
      }

      if (chunk != null) {
         chunk.addEntity(this);
      }

   }

   public void removeFromChunk() {
      if (this.chunk_added_to == null) {
         Minecraft.setErrorMessage("removeFromChunk: " + this.getEntityName() + " hasn't been added to a chunk");
      } else {
         this.transferToChunk((Chunk)null);
      }

   }

   public Chunk getChunkAddedTo() {
      return this.chunk_added_to;
   }

   public boolean isAddedToAChunk() {
      return this.chunk_added_to != null;
   }

   public void setChunkAddedToUnchecked(Chunk chunk, int chunk_added_to_section_index) {
      this.chunk_added_to = chunk;
      if (chunk == null && chunk_added_to_section_index != -1) {
         Minecraft.setErrorMessage("setChunkAddedToUnchecked: setting to null but section index!=-1");
      }

      this.chunk_added_to_section_index = chunk_added_to_section_index;
   }

   public void transferToChunkCurrentlyIn() {
      Chunk chunk = this.getChunkFromPosition();
      if (chunk == null) {
         Minecraft.setErrorMessage("transferToChunkCurrentlyIn: chunk currently in does not exist");
      } else {
         this.transferToChunk(chunk);
      }

   }

   public List getTargetPoints() {
      return null;
   }

   public Vec3 getCenterPoint() {
      return this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY + (double)(this.height * 0.5F), this.posZ);
   }

   public void onMeleeAttacked(EntityLivingBase attacker, EntityDamageResult result) {
   }

   public boolean cannotRaycastCollideWith(Entity entity) {
      return entity == this;
   }

   public void modifyEffectiveCollisionBoxForRaycastFromEntity(AxisAlignedBB effective_collision_box, Entity entity) {
      if (entity == this.riddenByEntity) {
         effective_collision_box.translate(0.0, -0.5, 0.0);
      }

   }

   public final boolean canEntityBeSeenFrom(double x, double y, double z, double max_range_sq) {
      return this.canEntityBeSeenFrom(x, y, z, max_range_sq, false);
   }

   public boolean canEntityBeSeenFrom(double x, double y, double z, double max_range_sq, boolean ignore_leaves) {
      Vec3 origin = this.worldObj.getVec3(x, y, z);
      Vec3 limit = this.getCenterPoint();
      return origin.squareDistanceTo(limit) > max_range_sq ? false : this.worldObj.checkForNoBlockCollision(origin, limit, RaycastPolicies.for_vision(ignore_leaves));
   }

   public final boolean isUnderOpenSky() {
      return this.worldObj.isOverworld() && this.worldObj.canBlockSeeTheSky(this.getBlockPosX(), this.getBlockPosY(), this.getBlockPosZ());
   }

   public BiomeGenBase getBiome() {
      return this.worldObj.getBiomeGenForCoords(this.getBlockPosX(), this.getBlockPosZ());
   }

   public final List getCollidingBlockBounds() {
      return this.worldObj.getCollidingBlockBounds(this.boundingBox, this);
   }

   public boolean isPlayerInCreative() {
      return false;
   }

   public final Chunk getChunkFromPosition() {
      return this.worldObj.getChunkFromPosition(this.posX, this.posZ);
   }

   public final boolean isInUnderworld() {
      return this.worldObj.isUnderworld();
   }

   public final boolean isInNether() {
      return this.worldObj.isTheNether();
   }

   public void onCollidedWithBlock(World world, Block block, int x, int y, int z) {
   }

   public BlockInfo[] getBlocksBelow() {
      int min_x = this.boundingBox.getBlockCoordForMinX();
      int max_x = this.boundingBox.getBlockCoordForMaxX();
      int min_z = this.boundingBox.getBlockCoordForMinZ();
      int max_z = this.boundingBox.getBlockCoordForMaxZ();
      int num_blocks_x = max_x - min_x + 1;
      int num_blocks_z = max_z - min_z + 1;
      int num_blocks = num_blocks_x * num_blocks_z;
      BlockInfo[] infos = new BlockInfo[num_blocks];
      int y = (this.isEntityLivingBase() ? this.getAsEntityLivingBase().getFootBlockPosY() : this.getBlockPosY()) - 1;
      int index = -1;

      for(int x = min_x; x <= max_x; ++x) {
         for(int z = min_z; z <= max_z; ++z) {
            ++index;
            Block block = this.worldObj.getBlock(x, y, z);
            if (block != null) {
               infos[index] = new BlockInfo(this.worldObj, block, x, y, z);
            }
         }
      }

      return infos;
   }

   public BlockInfo[] getBlocksOccupied(float expansion_x, float expansion_y_down, float expansion_y_up, float expansion_z, boolean must_intersect) {
      AxisAlignedBB bb = this.boundingBox.copy();
      bb.minX -= (double)expansion_x;
      bb.maxX += (double)expansion_x;
      bb.minY -= (double)expansion_y_down;
      bb.maxY += (double)expansion_y_up;
      bb.minZ -= (double)expansion_z;
      bb.maxZ += (double)expansion_z;
      int min_x = bb.getBlockCoordForMinX();
      int max_x = bb.getBlockCoordForMaxX();
      int min_y = bb.getBlockCoordForMinY();
      int max_y = bb.getBlockCoordForMaxY();
      int min_z = bb.getBlockCoordForMinZ();
      int max_z = bb.getBlockCoordForMaxZ();
      int num_blocks_x = max_x - min_x + 1;
      int num_blocks_y = max_y - min_y + 1;
      int num_blocks_z = max_z - min_z + 1;
      int num_blocks = num_blocks_x * num_blocks_y * num_blocks_z;
      BlockInfo[] infos = new BlockInfo[num_blocks];
      int index = -1;

      for(int x = min_x; x <= max_x; ++x) {
         for(int z = min_z; z <= max_z; ++z) {
            for(int y = min_y; y <= max_y; ++y) {
               ++index;
               Block block = this.worldObj.getBlock(x, y, z);
               if (block != null && (!must_intersect || block.doRenderBoundsIntersectWith(this.worldObj, x, y, z, bb))) {
                  infos[index] = new BlockInfo(this.worldObj, block, x, y, z);
               }
            }
         }
      }

      return infos;
   }

   public final boolean isTrueAnimal() {
      if (this instanceof EntityAnimal) {
         return !(this instanceof EntityHellhound);
      } else {
         return false;
      }
   }

   public final double getPredictedPosX(float lead) {
      if (this instanceof EntityPlayerMP) {
         double last_received_motion_x = this.getAsEntityPlayerMP().last_received_motion_x;
         if (Math.abs(last_received_motion_x) <= 1.0) {
            return this.posX + last_received_motion_x * (double)lead;
         }
      }

      return this.posX + this.motionX * (double)lead;
   }

   public final double getPredictedPosZ(float lead) {
      if (this instanceof EntityPlayerMP) {
         double last_received_motion_z = this.getAsEntityPlayerMP().last_received_motion_z;
         if (Math.abs(last_received_motion_z) <= 1.0) {
            return this.posZ + last_received_motion_z * (double)lead;
         }
      }

      return this.posZ + this.motionZ * (double)lead;
   }

   public static final boolean isClass(Entity entity, Class _class) {
      return entity != null && entity.getClass() == _class;
   }

   public final boolean isAtCoordsInQuestion() {
      return this.getBlockPosX() == -605 && this.getBlockPosY() == 5 && this.getBlockPosZ() == 198;
   }
}
