package net.minecraft.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityPhaseSpider;
import net.minecraft.entity.EntityStatsDump;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet0KeepAlive;
import net.minecraft.network.packet.Packet101CloseWindow;
import net.minecraft.network.packet.Packet102WindowClick;
import net.minecraft.network.packet.Packet103SetSlot;
import net.minecraft.network.packet.Packet106Transaction;
import net.minecraft.network.packet.Packet107CreativeSetSlot;
import net.minecraft.network.packet.Packet108EnchantItem;
import net.minecraft.network.packet.Packet10Flying;
import net.minecraft.network.packet.Packet11PlayerPosition;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraft.network.packet.Packet13PlayerLookMove;
import net.minecraft.network.packet.Packet15Place;
import net.minecraft.network.packet.Packet16BlockItemSwitch;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet19EntityAction;
import net.minecraft.network.packet.Packet202PlayerAbilities;
import net.minecraft.network.packet.Packet203AutoComplete;
import net.minecraft.network.packet.Packet204ClientInfo;
import net.minecraft.network.packet.Packet205ClientCommand;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet255KickDisconnect;
import net.minecraft.network.packet.Packet27PlayerInput;
import net.minecraft.network.packet.Packet34EntityTeleport;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet51MapChunk;
import net.minecraft.network.packet.Packet53BlockChange;
import net.minecraft.network.packet.Packet55BlockDestroy;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.network.packet.Packet81RightClick;
import net.minecraft.network.packet.Packet82AddHunger;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.network.packet.Packet87SetDespawnCounters;
import net.minecraft.network.packet.Packet89PlaySoundOnServerAtEntity;
import net.minecraft.network.packet.Packet90BroadcastToAssociatedPlayers;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.BanEntry;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumConsciousState;
import net.minecraft.util.EnumEntityReachContext;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.lang3.StringUtils;

public class NetServerHandler extends NetHandler {
   public final INetworkManager netManager;
   private final MinecraftServer mcServer;
   public boolean connectionClosed;
   public EntityPlayerMP playerEntity;
   private int currentTicks;
   private int ticksForFloatKick;
   private boolean field_72584_h;
   private int keepAliveRandomID;
   private long keepAliveTimeSent;
   private static Random randomGenerator = new Random();
   private long ticksOfLastKeepAlive;
   private int chatSpamThresholdCount;
   private int creativeItemCreationSpamThresholdTally;
   private double lastPosX;
   private double lastPosY;
   private double lastPosZ;
   private boolean hasMoved = true;
   private IntHashMap field_72586_s = new IntHashMap();

   public NetServerHandler(MinecraftServer par1MinecraftServer, INetworkManager par2INetworkManager, EntityPlayerMP par3EntityPlayerMP) {
      this.mcServer = par1MinecraftServer;
      this.netManager = par2INetworkManager;
      par2INetworkManager.setNetHandler(this);
      this.playerEntity = par3EntityPlayerMP;
      par3EntityPlayerMP.playerNetServerHandler = this;
   }

   public void networkTick() {
      this.field_72584_h = false;
      ++this.currentTicks;
      this.mcServer.theProfiler.startSection("packetflow");
      this.netManager.processReadPackets();
      this.mcServer.theProfiler.endStartSection("keepAlive");
      if ((long)this.currentTicks - this.ticksOfLastKeepAlive > 20L) {
         this.ticksOfLastKeepAlive = (long)this.currentTicks;
         this.keepAliveTimeSent = System.nanoTime() / 1000000L;
         this.keepAliveRandomID = randomGenerator.nextInt();
         this.sendPacketToPlayer(new Packet0KeepAlive(this.keepAliveRandomID));
      }

      if (this.chatSpamThresholdCount > 0) {
         --this.chatSpamThresholdCount;
      }

      if (this.creativeItemCreationSpamThresholdTally > 0) {
         --this.creativeItemCreationSpamThresholdTally;
      }

      this.mcServer.theProfiler.endStartSection("playerTick");
      this.mcServer.theProfiler.endSection();
   }

   public void kickPlayerFromServer(String par1Str) {
      if (!this.connectionClosed) {
         this.playerEntity.is_disconnecting_while_in_bed = this.playerEntity.inBed();
         this.playerEntity.mountEntityAndWakeUp();
         MinecraftServer var10004 = this.mcServer;
         this.sendPacketToPlayer(new Packet255KickDisconnect(par1Str, MinecraftServer.isPlayerHostingGame(this.playerEntity)));
         this.netManager.serverShutdown();
         if (!this.playerEntity.isZevimrgvInTournament()) {
            this.mcServer.getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromTranslationWithSubstitutions("multiplayer.player.left", this.playerEntity.getTranslatedEntityName()).setColor(EnumChatFormatting.YELLOW));
         }

         this.mcServer.getConfigurationManager().playerLoggedOut(this.playerEntity);
         this.connectionClosed = true;
      }

   }

   public void func_110774_a(Packet27PlayerInput par1Packet27PlayerInput) {
      this.playerEntity.setEntityActionState(par1Packet27PlayerInput.getMoveStrafing(), par1Packet27PlayerInput.getMoveForward(), par1Packet27PlayerInput.getJumping(), par1Packet27PlayerInput.getSneaking());
   }

   public void handleFlying(Packet10Flying par1Packet10Flying) {
      WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
      this.field_72584_h = true;
      if (this.playerEntity.block_placement_world == var2 && this.playerEntity.block_placement_tick == var2.getTotalWorldTime()) {
         this.playerEntity.setPosition(this.playerEntity.block_placement_pos_x, this.playerEntity.block_placement_pos_y, this.playerEntity.block_placement_pos_z);
      }

      if (this.playerEntity.block_placement_world != var2 || var2.getTotalWorldTime() > this.playerEntity.block_placement_tick) {
         this.playerEntity.block_placement_world = null;
      }

      if (par1Packet10Flying instanceof Packet13PlayerLookMove && par1Packet10Flying.yPosition == -999.0 && par1Packet10Flying.stance == -999.0) {
         this.playerEntity.last_received_motion_x = par1Packet10Flying.xPosition;
         this.playerEntity.last_received_motion_z = par1Packet10Flying.zPosition;
      } else if (!(par1Packet10Flying instanceof Packet11PlayerPosition) && !(par1Packet10Flying instanceof Packet13PlayerLookMove)) {
         this.playerEntity.last_received_motion_x = 0.0;
         this.playerEntity.last_received_motion_z = 0.0;
      } else {
         this.playerEntity.last_received_motion_x = par1Packet10Flying.xPosition - this.playerEntity.posX;
         this.playerEntity.last_received_motion_z = par1Packet10Flying.zPosition - this.playerEntity.posZ;
      }

      if (!this.playerEntity.playerConqueredTheEnd) {
         double var3;
         if (!this.hasMoved) {
            var3 = par1Packet10Flying.yPosition - this.lastPosY;
            if (par1Packet10Flying.xPosition == this.lastPosX && var3 * var3 < 0.01 && par1Packet10Flying.zPosition == this.lastPosZ) {
               this.hasMoved = true;
            }
         }

         if (this.hasMoved) {
            double var5;
            double var7;
            double var9;
            float var11;
            float var12;
            if (this.playerEntity.ridingEntity != null) {
               var11 = this.playerEntity.rotationYaw;
               var12 = this.playerEntity.rotationPitch;
               this.playerEntity.ridingEntity.updateRiderPosition();
               var5 = this.playerEntity.posX;
               var7 = this.playerEntity.posY;
               var9 = this.playerEntity.posZ;
               if (par1Packet10Flying.rotating) {
                  var11 = par1Packet10Flying.yaw;
                  var12 = par1Packet10Flying.pitch;
               }

               this.playerEntity.onGround = par1Packet10Flying.onGround;
               this.playerEntity.onUpdateEntity();
               this.playerEntity.ySize = 0.0F;
               this.playerEntity.setPositionAndRotation(var5, var7, var9, var11, var12);
               if (this.playerEntity.ridingEntity != null) {
                  this.playerEntity.ridingEntity.updateRiderPosition();
               }

               this.mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
               if (this.hasMoved) {
                  this.lastPosX = this.playerEntity.posX;
                  this.lastPosY = this.playerEntity.posY;
                  this.lastPosZ = this.playerEntity.posZ;
               }

               var2.updateEntity(this.playerEntity);
               return;
            }

            if (!this.hasMoved) //Fixes teleportation kick while riding entities
            {
               return;
            }

            if (this.playerEntity.inBed()) {
               this.playerEntity.onUpdateEntity();
               this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
               var2.updateEntity(this.playerEntity);
               return;
            }

            var3 = this.playerEntity.posY;
            this.lastPosX = this.playerEntity.posX;
            this.lastPosY = this.playerEntity.posY;
            this.lastPosZ = this.playerEntity.posZ;
            var5 = this.playerEntity.posX;
            var7 = this.playerEntity.posY;
            var9 = this.playerEntity.posZ;
            var11 = this.playerEntity.rotationYaw;
            var12 = this.playerEntity.rotationPitch;
            if (par1Packet10Flying.moving && par1Packet10Flying.yPosition == -999.0 && par1Packet10Flying.stance == -999.0) {
               par1Packet10Flying.moving = false;
            }

            double var13;
            if (par1Packet10Flying.moving) {
               label213: {
                  var5 = par1Packet10Flying.xPosition;
                  var7 = par1Packet10Flying.yPosition;
                  var9 = par1Packet10Flying.zPosition;
                  var13 = par1Packet10Flying.stance - par1Packet10Flying.yPosition;
                  if (this.playerEntity.inBed() || !(var13 > 1.65) && !(var13 < 0.1)) {
                     if (!(Math.abs(par1Packet10Flying.xPosition) > 3.2E7) && !(Math.abs(par1Packet10Flying.zPosition) > 3.2E7)) {
                        break label213;
                     }

                     this.kickPlayerFromServer("Illegal position");
                     return;
                  }

                  this.kickPlayerFromServer("Illegal stance");
                  this.mcServer.getLogAgent().logWarning(this.playerEntity.getCommandSenderName() + " had an illegal stance: " + var13);
                  return;
               }
            }

            if (par1Packet10Flying.rotating) {
               var11 = par1Packet10Flying.yaw;
               var12 = par1Packet10Flying.pitch;
            }

            this.playerEntity.onUpdateEntity();
            this.playerEntity.ySize = 0.0F;
            this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, var11, var12);
            if (!this.hasMoved) {
               return;
            }

            var13 = var5 - this.playerEntity.posX;
            double var15 = var7 - this.playerEntity.posY;
            double var17 = var9 - this.playerEntity.posZ;
            double var19 = Math.max(Math.abs(var13), Math.abs(this.playerEntity.motionX));
            double var21 = Math.max(Math.abs(var15), Math.abs(this.playerEntity.motionY));
            double var23 = Math.max(Math.abs(var17), Math.abs(this.playerEntity.motionZ));
            double var25 = var19 * var19 + var21 * var21 + var23 * var23;
            if (var25 > 100.0 && (!this.mcServer.isSinglePlayer() || !this.mcServer.getServerOwner().equals(this.playerEntity.getCommandSenderName()))) {
               this.mcServer.getLogAgent().logWarning(this.playerEntity.getCommandSenderName() + " moved too quickly! " + var13 + "," + var15 + "," + var17 + " (" + var19 + ", " + var21 + ", " + var23 + ")");
               this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
               return;
            }

            float var27 = 0.0625F;
            boolean var28 = var2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract((double)var27, (double)var27, (double)var27)).isEmpty();
            if (this.playerEntity.onGround && !par1Packet10Flying.onGround && var15 > 0.0) {
               this.playerEntity.addHungerServerSide(this.playerEntity.isSprinting() ? 0.8F : 0.2F);
            }

            if (!this.hasMoved) //Fixes "Moved Too Fast" kick when being teleported while moving
            {
               return;
            }

            this.playerEntity.moveEntity(var13, var15, var17);
            this.playerEntity.onGround = par1Packet10Flying.onGround;
            this.playerEntity.addMovementStat(var13, var15, var17);
            double var29 = var15;
            var13 = var5 - this.playerEntity.posX;
            var15 = var7 - this.playerEntity.posY;
            if (var15 > -0.5 || var15 < 0.5) {
               var15 = 0.0;
            }

            var17 = var9 - this.playerEntity.posZ;
            var25 = var13 * var13 + var15 * var15 + var17 * var17;
            boolean var31 = false;
            if (var25 > 0.0625 && !this.playerEntity.inBed() && !this.playerEntity.theItemInWorldManager.isCreative()) {
               var31 = true;
               this.mcServer.getLogAgent().logWarning(this.playerEntity.getCommandSenderName() + " moved wrongly!");
            }

            if (!this.hasMoved) //Fixes "Moved Too Fast" kick when being teleported while moving
            {
               return;
            }

            this.playerEntity.setPositionAndRotation(var5, var7, var9, var11, var12);
            boolean var32 = var2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract((double)var27, (double)var27, (double)var27)).isEmpty();
            if (var28 && (var31 || !var32) && !this.playerEntity.inBed() && !this.playerEntity.noClip) {
               this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, var11, var12);
               return;
            }

            AxisAlignedBB var33 = this.playerEntity.boundingBox.copy().expand((double)var27, (double)var27, (double)var27).addCoord(0.0, -0.55, 0.0);
            if (!this.mcServer.isFlightAllowed() && !this.playerEntity.theItemInWorldManager.isCreative() && !this.playerEntity.isZevimrgvInTournament()
                    && !var2.checkBlockCollision(var33) && !this.playerEntity.capabilities.allowFlying) {
               if (var29 >= -0.03125) {
                  ++this.ticksForFloatKick;
                  if (this.ticksForFloatKick > 80) {
                     this.mcServer.getLogAgent().logWarning(this.playerEntity.getCommandSenderName() + " was kicked for floating too long!");
                     this.kickPlayerFromServer("Flying is not enabled on this server");
                     return;
                  }
               }
            } else {
               this.ticksForFloatKick = 0;
            }

            if (!this.hasMoved) //Fixes "Moved Too Fast" kick when being teleported while moving
            {
               return;
            }


            this.playerEntity.onGround = par1Packet10Flying.onGround;
            this.mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
            this.playerEntity.updateFlyingState(this.playerEntity.posY - var3, par1Packet10Flying.onGround);

         } else if (this.currentTicks % 20 == 0) {
            this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
         }
      }

   }

   public void setPlayerLocation(double par1, double par3, double par5, float par7, float par8) {
      this.hasMoved = false;
      this.lastPosX = par1;
      this.lastPosY = par3;
      this.lastPosZ = par5;
      this.playerEntity.setPositionAndRotation(par1, par3, par5, par7, par8);
      this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet13PlayerLookMove(par1, par3 + 1.6200000047683716, par3, par5, par7, par8, false));
   }

   private void handleDiggingPacket(Packet85SimpleSignal packet) {
      WorldServer world = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
      this.playerEntity.func_143004_u();
      int x = packet.getBlockX();
      int y = packet.getBlockY();
      int z = packet.getBlockZ();
      if (y < this.mcServer.getBuildLimit()) {
         EnumSignal signal_type = packet.signal_type;
         if (signal_type != EnumSignal.digging_block_cancel) {
            double dx = this.playerEntity.posX - ((double)x + 0.5);
            double dy = this.playerEntity.posY + 1.5 - ((double)y + 0.5);
            double dz = this.playerEntity.posZ - ((double)z + 0.5);
            double distance_sq = dx * dx + dy * dy + dz * dz;
            if (distance_sq > 256.0) {
               Minecraft.setErrorMessage("handleDiggingPacket: player is too far from target block (" + signal_type + ")");
            }
         }

         if (signal_type == EnumSignal.digging_block_start) {
            if (!this.mcServer.isBlockProtected(world, x, y, z, this.playerEntity)) {
               Block block = world.getBlock(x, y, z);
               if (block == Block.tnt && (Enchantment.fireAspect.getLevel(this.playerEntity.getHeldItemStack()) > 0 || !this.playerEntity.hasHeldItem() && this.playerEntity.isBurning())) {
                  BlockTNT var10000 = Block.tnt;
                  BlockTNT.primeTnt(world, x, y, z, 1, this.playerEntity);
                  world.setBlockToAir(x, y, z);
               }

               this.playerEntity.theItemInWorldManager.onBlockClicked(x, y, z, EnumFace.get(packet.getByte()));
            } else {
               this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(x, y, z, world));
            }
         } else if (signal_type == EnumSignal.digging_block_complete) {
            world.destroyBlockInWorldPartially(this.playerEntity.entityId, x, y, z, -1);
            if (world.getBlock(x, y, z) != null) {
               this.playerEntity.theItemInWorldManager.tryHarvestBlock(x, y, z);
            }

            if (world.getBlockId(x, y, z) != 0) {
               this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(x, y, z, world));
            }
         } else if (signal_type == EnumSignal.digging_block_cancel) {
            world.destroyBlockInWorldPartially(this.playerEntity.entityId, x, y, z, -1);
            if (world.getBlockId(x, y, z) != 0) {
               this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(x, y, z, world));
            }
         }

      }
   }

   public void XXXhandlePlace(Packet15Place par1Packet15Place) {
      WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
      ItemStack var3 = this.playerEntity.inventory.getCurrentItemStack();
      boolean var4 = false;
      int var5 = par1Packet15Place.getXPosition();
      int var6 = par1Packet15Place.getYPosition();
      int var7 = par1Packet15Place.getZPosition();
      EnumFace face = par1Packet15Place.getFace();
      this.playerEntity.func_143004_u();
      if (face == null) {
         Minecraft.setErrorMessage("handlePlace: face is null");
      } else {
         if (par1Packet15Place.getYPosition() < this.mcServer.getBuildLimit() - 1 || !face.isTop() && par1Packet15Place.getYPosition() < this.mcServer.getBuildLimit()) {
            if (this.hasMoved && this.playerEntity.getDistanceSq((double)var5 + 0.5, (double)var6 + 0.5, (double)var7 + 0.5) < 64.0 && !this.mcServer.isBlockProtected(var2, var5, var6, var7, this.playerEntity)) {
            }

            var4 = true;
         } else {
            this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet3Chat(ChatMessageComponent.createFromTranslationWithSubstitutions("build.tooHigh", this.mcServer.getBuildLimit()).setColor(EnumChatFormatting.RED)));
            var4 = true;
         }

         if (var4) {
            this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var5, var6, var7, var2));
            var5 = face.getNeighborX(var5);
            var6 = face.getNeighborY(var6);
            var7 = face.getNeighborZ(var7);
            this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var5, var6, var7, var2));
         }

         var3 = this.playerEntity.inventory.getCurrentItemStack();
         if (var3 != null && var3.stackSize == 0) {
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
            var3 = null;
         }

         if (var3 == null || var3.getMaxItemUseDuration() == 0) {
            this.playerEntity.playerInventoryBeingManipulated = true;
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyItemStack(this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]);
            Slot var9 = this.playerEntity.openContainer.getSlotFromInventory(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
            this.playerEntity.openContainer.detectAndSendChanges();
            this.playerEntity.playerInventoryBeingManipulated = false;
            if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItemStack(), par1Packet15Place.getItemStack())) {
               this.sendPacketToPlayer(new Packet103SetSlot(this.playerEntity.openContainer.windowId, var9.slotNumber, this.playerEntity.inventory.getCurrentItemStack()));
            }
         }

      }
   }

   public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj) {
      this.mcServer.getLogAgent().logInfo(this.playerEntity.getCommandSenderName() + " lost connection: " + par1Str);
      if (!this.playerEntity.isZevimrgvInTournament()) {
         this.mcServer.getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromTranslationWithSubstitutions("multiplayer.player.left", this.playerEntity.getTranslatedEntityName()).setColor(EnumChatFormatting.YELLOW));
      }

      this.playerEntity.is_disconnecting_while_in_bed = this.playerEntity.inBed();
      this.mcServer.getConfigurationManager().playerLoggedOut(this.playerEntity);
      this.connectionClosed = true;
      if (this.mcServer.isSinglePlayer() && this.playerEntity.getCommandSenderName().equals(this.mcServer.getServerOwner())) {
         this.mcServer.getLogAgent().logInfo("Stopping singleplayer server as player logged out");
         this.mcServer.initiateShutdown();
      }

   }

   public void unexpectedPacket(Packet par1Packet) {
      this.mcServer.getLogAgent().logWarning(this.getClass() + " wasn't prepared to deal with a " + par1Packet.getClass());
      this.kickPlayerFromServer("Protocol error, unexpected packet");
   }

   public void sendPacketToPlayer(Packet par1Packet) {
      if (par1Packet instanceof Packet3Chat) {
         Packet3Chat var2 = (Packet3Chat)par1Packet;
         int var3 = this.playerEntity.getChatVisibility();
         if (var3 == 2) {
            return;
         }

         if (var3 == 1 && !var2.getIsServer()) {
            return;
         }
      }

      try {
         this.netManager.addToSendQueue(par1Packet);
      } catch (Throwable var5) {
         CrashReport var6 = CrashReport.makeCrashReport(var5, "Sending packet");
         CrashReportCategory var4 = var6.makeCategory("Packet being sent");
         var4.addCrashSectionCallable("Packet ID", new CallablePacketID(this, par1Packet));
         var4.addCrashSectionCallable("Packet class", new CallablePacketClass(this, par1Packet));
         throw new ReportedException(var6);
      }
   }

   public void handleBlockItemSwitch(Packet16BlockItemSwitch par1Packet16BlockItemSwitch) {
      if (par1Packet16BlockItemSwitch.id >= 0 && par1Packet16BlockItemSwitch.id < InventoryPlayer.getHotbarSize()) {
         this.playerEntity.inventory.currentItem = par1Packet16BlockItemSwitch.id;
         this.playerEntity.func_143004_u();
      } else {
         this.mcServer.getLogAgent().logWarning(this.playerEntity.getCommandSenderName() + " tried to set an invalid carried item");
      }

   }

   public void handleChat(Packet3Chat par1Packet3Chat) {
      if (this.playerEntity.getChatVisibility() == 2) {
         this.sendPacketToPlayer(new Packet3Chat(ChatMessageComponent.createFromTranslationKey("chat.cannotSend").setColor(EnumChatFormatting.RED)));
      } else {
         this.playerEntity.func_143004_u();
         String var2 = par1Packet3Chat.message;
         if (var2.length() > 100) {
            this.kickPlayerFromServer("Chat message too long");
         } else {
            var2 = StringUtils.normalizeSpace(var2);

            for(int var3 = 0; var3 < var2.length(); ++var3) {
               if (!ChatAllowedCharacters.isAllowedCharacter(var2.charAt(var3))) {
                  this.kickPlayerFromServer("Illegal characters in chat");
                  return;
               }
            }

            if (var2.startsWith("/")) {
               this.handleSlashCommand(var2, par1Packet3Chat.permission_override);
            } else {
               if (this.playerEntity.getChatVisibility() == 1) {
                  this.sendPacketToPlayer(new Packet3Chat(ChatMessageComponent.createFromTranslationKey("chat.cannotSend").setColor(EnumChatFormatting.RED)));
                  return;
               }

               ChatMessageComponent var4 = ChatMessageComponent.createFromTranslationWithSubstitutions("chat.type.text", this.playerEntity.getTranslatedEntityName(), var2);
               var4 = ForgeHooks.onServerChatEvent(this, var2, var4);
               if (var4 == null) return;
               this.mcServer.getConfigurationManager().func_110459_a(var4, false);
            }

            this.chatSpamThresholdCount += 20;
            if (this.chatSpamThresholdCount > 200 && !this.mcServer.getConfigurationManager().isPlayerOpped(this.playerEntity.getCommandSenderName()) && MinecraftServer.getServer() instanceof DedicatedServer) {
               this.kickPlayerFromServer("disconnect.spam");
            }
         }
      }

   }

   public void handlePlayerInventory(Packet5PlayerInventory packet) {
      this.playerEntity.inventory.setInventorySlotContents(packet.slot, packet.getItemSlot());
   }

   private void handleSlashCommand(String par1Str, boolean permission_override) {
      this.mcServer.getCommandManager().executeCommand(this.playerEntity, par1Str, permission_override);
   }

   public void handleAnimation(Packet18Animation par1Packet18Animation) {
      this.playerEntity.func_143004_u();
      if (par1Packet18Animation.animate == 1) {
         this.playerEntity.swingArm();
      }

   }

   public void handleEntityAction(Packet19EntityAction par1Packet19EntityAction) {
      this.playerEntity.func_143004_u();
      if (par1Packet19EntityAction.action == 1) {
         this.playerEntity.setSneaking(true);
      } else if (par1Packet19EntityAction.action == 2) {
         this.playerEntity.setSneaking(false);
      } else if (par1Packet19EntityAction.action == 4) {
         this.playerEntity.setSprinting(true);
      } else if (par1Packet19EntityAction.action == 5) {
         this.playerEntity.setSprinting(false);
      } else if (par1Packet19EntityAction.action == 3) {
         this.playerEntity.wakeUpPlayer(true, (Entity)null);
         this.hasMoved = false;
      } else if (par1Packet19EntityAction.action == 6) {
         if (this.playerEntity.ridingEntity != null && this.playerEntity.ridingEntity instanceof EntityHorse) {
            ((EntityHorse)this.playerEntity.ridingEntity).setJumpPower(par1Packet19EntityAction.auxData);
         }
      } else if (par1Packet19EntityAction.action == 7 && this.playerEntity.ridingEntity != null && this.playerEntity.ridingEntity instanceof EntityHorse) {
         ((EntityHorse)this.playerEntity.ridingEntity).tryOpenGUI(this.playerEntity);
      }

   }

   public void handleKickDisconnect(Packet255KickDisconnect par1Packet255KickDisconnect) {
      this.netManager.networkShutdown("disconnect.quitting");
   }

   public int packetSize() {
      return this.netManager.packetSize();
   }

   public void handleBlockDestroy(Packet55BlockDestroy par1Packet55BlockDestroy) {
      int x = par1Packet55BlockDestroy.getPosX();
      int y = par1Packet55BlockDestroy.getPosY();
      int z = par1Packet55BlockDestroy.getPosZ();
      Block block = this.playerEntity.worldObj.getBlock(x, y, z);
      if (block != null) {
         block.onBlockDamageStageChange(x, y, z, this.playerEntity.worldObj.getEntityByID(par1Packet55BlockDestroy.getEntityId()), par1Packet55BlockDestroy.getDestroyedStage());
      }

      this.playerEntity.worldObj.destroyBlockInWorldPartially(par1Packet55BlockDestroy.getEntityId(), par1Packet55BlockDestroy.getPosX(), par1Packet55BlockDestroy.getPosY(), par1Packet55BlockDestroy.getPosZ(), par1Packet55BlockDestroy.getDestroyedStage());
   }

   public void handleClientCommand(Packet205ClientCommand par1Packet205ClientCommand) {
      this.playerEntity.func_143004_u();
      if (par1Packet205ClientCommand.forceRespawn == 1) {
         if (this.playerEntity.playerConqueredTheEnd) {
            this.playerEntity = this.mcServer.getConfigurationManager().respawnPlayer(this.playerEntity, 0, true);
         } else if (this.playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled()) {
            if (this.mcServer.isSinglePlayer() && this.playerEntity.getCommandSenderName().equals(this.mcServer.getServerOwner())) {
               this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it's game over!");
               this.mcServer.deleteWorldAndStopServer();
            } else {
               BanEntry var2 = new BanEntry(this.playerEntity.getCommandSenderName());
               var2.setBanReason("Death in Hardcore");
               this.mcServer.getConfigurationManager().getBannedPlayers().put(var2);
               this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it's game over!");
            }
         } else {
            if (this.playerEntity.getHealth() > 0.0F) {
               return;
            }

            this.playerEntity = this.mcServer.getConfigurationManager().respawnPlayer(this.playerEntity,  playerEntity.dimension, false);
         }
      }

   }

   public boolean canProcessPacketsAsync() {
      return true;
   }

   public void handleRespawn(Packet9Respawn par1Packet9Respawn) {
   }

   public void handleCloseWindow(Packet101CloseWindow par1Packet101CloseWindow) {
      this.playerEntity.closeContainer();
   }

   public void handleWindowClick(Packet102WindowClick par1Packet102WindowClick) {
      this.playerEntity.func_143004_u();
      if (this.playerEntity.openContainer.windowId == par1Packet102WindowClick.window_Id && this.playerEntity.openContainer.isPlayerNotUsingContainer(this.playerEntity)) {
         ItemStack var2 = this.playerEntity.openContainer.slotClick(par1Packet102WindowClick.inventorySlot, par1Packet102WindowClick.mouseClick, par1Packet102WindowClick.holdingShift, par1Packet102WindowClick.holding_shift, this.playerEntity);
         if (ItemStack.areItemStacksEqual(par1Packet102WindowClick.itemStack, var2)) {
            this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet106Transaction(par1Packet102WindowClick.window_Id, par1Packet102WindowClick.action, true));
            this.playerEntity.playerInventoryBeingManipulated = true;
            this.playerEntity.openContainer.detectAndSendChanges();
            this.playerEntity.updateHeldItem();
            this.playerEntity.playerInventoryBeingManipulated = false;
         } else {
            this.field_72586_s.addKey(this.playerEntity.openContainer.windowId, par1Packet102WindowClick.action);
            this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet106Transaction(par1Packet102WindowClick.window_Id, par1Packet102WindowClick.action, false));
            this.playerEntity.openContainer.setPlayerIsPresent(this.playerEntity, false);
            ArrayList var3 = new ArrayList();

            for(int var4 = 0; var4 < this.playerEntity.openContainer.inventorySlots.size(); ++var4) {
               var3.add(((Slot)this.playerEntity.openContainer.inventorySlots.get(var4)).getStack());
            }

            this.playerEntity.sendContainerAndContentsToPlayer(this.playerEntity.openContainer, var3);
         }
      }

   }

   public void handleEnchantItem(Packet108EnchantItem par1Packet108EnchantItem) {
      this.playerEntity.func_143004_u();
      if (this.playerEntity.openContainer.windowId == par1Packet108EnchantItem.windowId && this.playerEntity.openContainer.isPlayerNotUsingContainer(this.playerEntity)) {
         this.playerEntity.openContainer.enchantItem(this.playerEntity, par1Packet108EnchantItem.enchantment);
         this.playerEntity.openContainer.detectAndSendChanges();
      }

   }

   public void handleCreativeSetSlot(Packet107CreativeSetSlot par1Packet107CreativeSetSlot) {
      if (this.playerEntity.theItemInWorldManager.isCreative()) {
         boolean var2 = par1Packet107CreativeSetSlot.slot < 0;
         ItemStack var3 = par1Packet107CreativeSetSlot.itemStack;
         boolean var4 = par1Packet107CreativeSetSlot.slot >= 1 && par1Packet107CreativeSetSlot.slot < 36 + InventoryPlayer.getHotbarSize();
         boolean var5 = var3 == null || var3.itemID < Item.itemsList.length && var3.itemID >= 0 && Item.itemsList[var3.itemID] != null;
         boolean var6 = var3 == null || var3.getItemSubtype() >= 0 && var3.getItemDamage() >= 0 && var3.stackSize <= 64 && var3.stackSize > 0;
         if (var4 && var5 && var6) {
            if (var3 == null) {
               this.playerEntity.inventoryContainer.putStackInSlot(par1Packet107CreativeSetSlot.slot, (ItemStack)null);
            } else {
               this.playerEntity.inventoryContainer.putStackInSlot(par1Packet107CreativeSetSlot.slot, var3);
            }

            this.playerEntity.inventoryContainer.setPlayerIsPresent(this.playerEntity, true);
         } else if (var2 && var5 && var6 && this.creativeItemCreationSpamThresholdTally < 200) {
            this.creativeItemCreationSpamThresholdTally += 20;
            EntityItem var7 = this.playerEntity.dropPlayerItem(var3);
            if (var7 != null) {
               var7.setAgeToCreativeDespawnTime();
            }
         }
      }

   }

   public void handleTransaction(Packet106Transaction par1Packet106Transaction) {
      Short var2 = (Short)this.field_72586_s.lookup(this.playerEntity.openContainer.windowId);
      if (var2 != null && par1Packet106Transaction.shortWindowId == var2 && this.playerEntity.openContainer.windowId == par1Packet106Transaction.windowId && !this.playerEntity.openContainer.isPlayerNotUsingContainer(this.playerEntity)) {
         this.playerEntity.openContainer.setPlayerIsPresent(this.playerEntity, true);
      }

   }

   public void handleUpdateSign(Packet130UpdateSign par1Packet130UpdateSign) {
      this.playerEntity.func_143004_u();
      WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
      if (var2.blockExists(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition)) {
         TileEntity var3 = var2.getBlockTileEntity(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition);
         if (var3 instanceof TileEntitySign) {
            TileEntitySign var4 = (TileEntitySign)var3;
            if (!var4.isEditable() || var4.func_142009_b() != this.playerEntity) {
               this.mcServer.logWarning("Player " + this.playerEntity.getCommandSenderName() + " just tried to change non-editable sign");
               return;
            }
         }

         int var8;
         int var6;
         for(var8 = 0; var8 < 4; ++var8) {
            boolean var5 = true;
            if (par1Packet130UpdateSign.signLines[var8].length() > 15) {
               var5 = false;
            } else {
               for(var6 = 0; var6 < par1Packet130UpdateSign.signLines[var8].length(); ++var6) {
                  if (ChatAllowedCharacters.allowedCharacters.indexOf(par1Packet130UpdateSign.signLines[var8].charAt(var6)) < 0) {
                     var5 = false;
                  }
               }
            }

            if (!var5) {
               par1Packet130UpdateSign.signLines[var8] = "!?";
            }
         }

         if (var3 instanceof TileEntitySign) {
            var8 = par1Packet130UpdateSign.xPosition;
            int var9 = par1Packet130UpdateSign.yPosition;
            var6 = par1Packet130UpdateSign.zPosition;
            TileEntitySign var7 = (TileEntitySign)var3;
            System.arraycopy(par1Packet130UpdateSign.signLines, 0, var7.signText, 0, 4);
            var7.onInventoryChanged();
            var2.markBlockForUpdate(var8, var9, var6);
         }
      }

   }

   public void handleKeepAlive(Packet0KeepAlive par1Packet0KeepAlive) {
      if (par1Packet0KeepAlive.randomId == this.keepAliveRandomID) {
         int var2 = (int)(System.nanoTime() / 1000000L - this.keepAliveTimeSent);
         this.playerEntity.ping = (this.playerEntity.ping * 3 + var2) / 4;
      }

   }

   public boolean isServerHandler() {
      return true;
   }

   public void handlePlayerAbilities(Packet202PlayerAbilities par1Packet202PlayerAbilities) {
      this.playerEntity.capabilities.isFlying = par1Packet202PlayerAbilities.getFlying() && this.playerEntity.capabilities.allowFlying;
   }

   public void handleAutoComplete(Packet203AutoComplete par1Packet203AutoComplete) {
      StringBuilder var2 = new StringBuilder();

      String var4;
      for(Iterator var3 = this.mcServer.getPossibleCompletions(this.playerEntity, par1Packet203AutoComplete.getText()).iterator(); var3.hasNext(); var2.append(var4)) {
         var4 = (String)var3.next();
         if (var2.length() > 0) {
            var2.append("\u0000");
         }
      }

      this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet203AutoComplete(var2.toString()));
   }

   public void handleClientInfo(Packet204ClientInfo par1Packet204ClientInfo) {
      this.playerEntity.updateClientInfo(par1Packet204ClientInfo);
   }

   public void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload) {
      DataInputStream var2;
      ItemStack var3;
      ItemStack var4;
      Exception var11;
      if ("MC|BEdit".equals(par1Packet250CustomPayload.channel)) {
         try {
            var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
            var3 = Packet.readItemStack(var2);
            if (!ItemWritableBook.validBookTagPages(var3.getTagCompound())) {
               throw new IOException("Invalid book tag!");
            }

            var4 = this.playerEntity.inventory.getCurrentItemStack();
            if (var3 != null && var3.itemID == Item.writableBook.itemID && var3.itemID == var4.itemID) {
               var4.setTagInfo("pages", var3.getTagCompound().getTagList("pages"));
            }
         } catch (Exception var14) {
            var11 = var14;
            var11.printStackTrace();
         }
      } else if ("MC|BSign".equals(par1Packet250CustomPayload.channel)) {
         try {
            var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
            var3 = Packet.readItemStack(var2);
            if (!ItemEditableBook.validBookTagContents(var3.getTagCompound())) {
               throw new IOException("Invalid book tag!");
            }

            var4 = this.playerEntity.inventory.getCurrentItemStack();
            if (var3 != null && var3.itemID == Item.writtenBook.itemID && var4.itemID == Item.writableBook.itemID) {
               var4.setTagInfo("author", new NBTTagString("author", this.playerEntity.getCommandSenderName()));
               var4.setTagInfo("title", new NBTTagString("title", var3.getTagCompound().getString("title")));
               var4.setTagInfo("pages", var3.getTagCompound().getTagList("pages"));
               var4.itemID = Item.writtenBook.itemID;
            }
         } catch (Exception var13) {
            var11 = var13;
            var11.printStackTrace();
         }
      } else {
         int var14;
         if ("MC|TrSel".equals(par1Packet250CustomPayload.channel)) {
            try {
               var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
               var14 = var2.readInt();
               Container var16 = this.playerEntity.openContainer;
               if (var16 instanceof ContainerMerchant) {
                  ((ContainerMerchant)var16).setCurrentRecipeIndex(var14);
               }
            } catch (Exception var12) {
               Exception var10 = var12;
               var10.printStackTrace();
            }
         } else {
            Exception var9;
            String var15;
            int var18;
            if ("MC|AdvCdm".equals(par1Packet250CustomPayload.channel)) {
               if (!this.mcServer.isCommandBlockEnabled()) {
                  this.playerEntity.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.notEnabled"));
               } else if (this.playerEntity.canCommandSenderUseCommand(2, "") && this.playerEntity.capabilities.isCreativeMode) {
                  try {
                     var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
                     var14 = var2.readInt();
                     var18 = var2.readInt();
                     int var5 = var2.readInt();
                     var15 = Packet.readString(var2, 256);
                     TileEntity var7 = this.playerEntity.worldObj.getBlockTileEntity(var14, var18, var5);
                     if (var7 != null && var7 instanceof TileEntityCommandBlock) {
                        ((TileEntityCommandBlock)var7).setCommand(var15);
                        this.playerEntity.worldObj.markBlockForUpdate(var14, var18, var5);
                        this.playerEntity.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("advMode.setCommand.success", var15));
                     }
                  } catch (Exception exception) {
                     exception.printStackTrace();
                  }
               } else {
                  this.playerEntity.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.notAllowed"));
               }
            } else if ("MC|Beacon".equals(par1Packet250CustomPayload.channel)) {
               if (this.playerEntity.openContainer instanceof ContainerBeacon) {
                  try {
                     var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
                     var14 = var2.readInt();
                     var18 = var2.readInt();
                     ContainerBeacon var17 = (ContainerBeacon)this.playerEntity.openContainer;
                     Slot var19 = var17.getSlot(0);
                     if (var19.getHasStack()) {
                        var19.decrStackSize(1);
                        TileEntityBeacon var20 = var17.getBeacon();
                        var20.setPrimaryEffect(var14);
                        var20.setSecondaryEffect(var18);
                        var20.onInventoryChanged();
                     }
                  } catch (Exception var10) {
                     var9 = var10;
                     var9.printStackTrace();
                  }
               }
            } else if ("MC|ItemName".equals(par1Packet250CustomPayload.channel) && this.playerEntity.openContainer instanceof ContainerRepair) {
               ContainerRepair var13 = (ContainerRepair)this.playerEntity.openContainer;
               if (par1Packet250CustomPayload.data != null && par1Packet250CustomPayload.data.length >= 1) {
                  var15 = ChatAllowedCharacters.filerAllowedCharacters(new String(par1Packet250CustomPayload.data));
                  if (var15.length() <= 30) {
                     var13.updateItemName(var15);
                  }
               } else {
                  var13.updateItemName("");
               }
            }
         }
      }

   }

   public void handleEntityTeleport(Packet34EntityTeleport par1Packet34EntityTeleport) {
      WorldServer world_server = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
      Entity var2 = world_server.getEntityByID(par1Packet34EntityTeleport.entity_id);
      if (var2 != null) {
         par1Packet34EntityTeleport.applyToEntity(var2);
         if (var2 instanceof EntityPlayer) {
            this.sendPacketToPlayer(new Packet51MapChunk(var2.getChunkFromPosition(), false, 15));
         }
      }

   }

   public void handleRightClick(Packet81RightClick packet) {
      if (packet.slot_index == this.playerEntity.inventory.currentItem && packet.item_id == this.playerEntity.getHeldItemID()) {
         RightClickFilter filter;
         if (packet.requiresRaycasting()) {
            double pos_x = this.playerEntity.posX;
            double pos_y = this.playerEntity.posY;
            double pos_z = this.playerEntity.posZ;
            float rotation_yaw = this.playerEntity.rotationYaw;
            float rotation_pitch = this.playerEntity.rotationPitch;
            double prev_pos_x = this.playerEntity.prevPosX;
            double prev_pos_y = this.playerEntity.prevPosY;
            double prev_pos_z = this.playerEntity.prevPosZ;
            float prev_rotation_yaw = this.playerEntity.prevRotationYaw;
            float prev_rotation_pitch = this.playerEntity.prevRotationPitch;
            float y_size = this.playerEntity.ySize;
            AxisAlignedBB bb = this.playerEntity.boundingBox.copy();
            this.playerEntity.posX = packet.pos_x;
            this.playerEntity.posY = packet.pos_y;
            this.playerEntity.posZ = packet.pos_z;
            this.playerEntity.rotationYaw = packet.rotation_yaw;
            this.playerEntity.rotationPitch = packet.rotation_pitch;
            this.playerEntity.prevPosX = packet.prev_pos_x;
            this.playerEntity.prevPosY = packet.prev_pos_y;
            this.playerEntity.prevPosZ = packet.prev_pos_z;
            this.playerEntity.prevRotationYaw = packet.prev_rotation_yaw;
            this.playerEntity.prevRotationPitch = packet.prev_rotation_pitch;
            this.playerEntity.ySize = packet.y_size;
            this.playerEntity.boundingBox.setBB(packet.bb);
            filter = this.playerEntity.onPlayerRightClickChecked(this.playerEntity.getSelectedObject(packet.partial_tick, false, false, (EnumEntityReachContext)null), packet.filter, packet.partial_tick, packet.ctrl_is_down);
            this.playerEntity.posX = pos_x;
            this.playerEntity.posY = pos_y;
            this.playerEntity.posZ = pos_z;
            this.playerEntity.rotationYaw = rotation_yaw;
            this.playerEntity.rotationPitch = rotation_pitch;
            this.playerEntity.prevPosX = prev_pos_x;
            this.playerEntity.prevPosY = prev_pos_y;
            this.playerEntity.prevPosZ = prev_pos_z;
            this.playerEntity.prevRotationYaw = prev_rotation_yaw;
            this.playerEntity.prevRotationPitch = prev_rotation_pitch;
            this.playerEntity.ySize = y_size;
            this.playerEntity.boundingBox.setBB(bb);
         } else if (packet.filter.allowsEntityInteractionOnly()) {
            Entity entity = this.playerEntity.worldObj.getEntityByID(packet.entity_id);
            if (entity == null || entity.isDead) {
               return;
            }

            filter = this.playerEntity.onPlayerRightClickChecked(new RaycastCollision(entity), packet.filter, 1.0F, packet.ctrl_is_down);
         } else {
            filter = this.playerEntity.onPlayerRightClickChecked((RaycastCollision)null, packet.filter, 1.0F, packet.ctrl_is_down);
         }

         if (filter.getAllowedActions() != packet.filter.getAllowedActions()) {
            Minecraft.setErrorMessage("handleRightClick: filter discrepency");
         }

      }
   }

   public void handleAddHunger(Packet82AddHunger packet) {
      this.playerEntity.addHungerServerSide(packet.hunger);
   }

   public void handleSimpleSignal(Packet85SimpleSignal packet) {
      EnumSignal signal_type = packet.signal_type;
      WorldServer world = (WorldServer)this.playerEntity.worldObj;
      if (signal_type == EnumSignal.achievement_unlocked) {
         if (this.mcServer.isDedicatedServer()) {
            DedicatedServer.logAchievement(this.playerEntity, StatList.getOneShotStat(packet.getInteger()));
         }
      } else if (signal_type == EnumSignal.increment_stat_for_this_world_only) {
         this.playerEntity.incrementStatForThisWorldOnServer(packet.getInteger());
      } else if (signal_type == EnumSignal.crafting_completed) {
         Slot slot = this.playerEntity.openContainer.getSlot(0);
         if (slot instanceof SlotCrafting) {
            ItemStack item_stack = slot.getStack();
            slot.onPickupFromSlot(this.playerEntity, item_stack);
            this.playerEntity.addExperience(-packet.getInteger());
         }
      } else if (signal_type == EnumSignal.sleeping) {
         this.playerEntity.conscious_state = EnumConsciousState.sleeping;
      } else if (signal_type == EnumSignal.fully_awake) {
         this.playerEntity.conscious_state = EnumConsciousState.fully_awake;
      } else {
         int z;
         int dx;
         int dz;
         int block_id;
         if (signal_type == EnumSignal.send_nearby_chunk_report) {
            PlayerManager player_manager = this.playerEntity.getServerForPlayer().getPlayerManager();
            int radius = 5;

            for(z = -radius; z <= radius; ++z) {
               dx = this.playerEntity.getChunkCurrentlyInSectionIndex() + z;
               dz = dx * 16;
               if (dz >= 0 || dz <= 255) {
                  for(block_id = -radius; block_id <= radius; ++block_id) {
                     int chunk_x = this.playerEntity.getChunkPosX() + block_id;
                     int x = chunk_x * 16;

                     for(int chunk_dz = -radius; chunk_dz <= radius; ++chunk_dz) {
                        int chunk_z = this.playerEntity.getChunkPosZ() + chunk_dz;
                        int i = chunk_z * 16;
                        ChunkCoordIntPair chunk_coords = new ChunkCoordIntPair(chunk_x, chunk_z);
                        if (!player_manager.isPlayerWatchingChunk(this.playerEntity, chunk_x, chunk_z)) {
                           this.sendChatToPlayer("[Server] Player is not watching chunk @ " + x + ", " + i + " (cond #1=" + this.playerEntity.loadedChunks.contains(chunk_coords) + ")");
                        } else if (!this.playerEntity.worldObj.chunkExists(chunk_x, chunk_z)) {
                           this.sendChatToPlayer("[Server] chunk does not exist @ " + x + ", " + i);
                        }

                        Chunk chunk = this.playerEntity.getServerForPlayer().getChunkFromChunkCoords(chunk_x, chunk_z);
                        if (chunk != null) {
                           chunk.setChunkModified();
                        }

                        player_manager.filterChunkLoadQueue(this.playerEntity);
                     }
                  }
               }
            }
         } else {
            int master_hash;
            if (signal_type == EnumSignal.terraform) {
               master_hash = this.playerEntity.getBlockPosX();
               int y = this.playerEntity.getBlockPosY();
               z = this.playerEntity.getBlockPosZ();

               for(dx = -128; dx <= 128; ++dx) {
                  for(dz = 0; dz <= 64; ++dz) {
                     for(block_id = -128; block_id <= 128; ++block_id) {
                        if (world.blockExists(master_hash + dx, y + dz, z + block_id)) {
                           world.getBlock(master_hash + dx, y + dz, z + block_id);
                           world.setBlockToAir(master_hash + dx, y + dz, z + block_id);
                        }
                     }
                  }
               }

               for(dx = -32; dx <= 32; ++dx) {
                  for(dz = -2; dz < 0; ++dz) {
                     for(block_id = -32; block_id <= 32; ++block_id) {
                        world.setBlock(master_hash + dx, y + dz, z + block_id, Block.dirt.blockID);
                     }
                  }
               }

               for(dx = -12; dx <= 18; dx += 6) {
                  for(dz = -4; dz < 4; ++dz) {
                     world.setBlock(master_hash + dx - 1, y - 1, z + dz, Block.tilledField.blockID, 7, 3);
                     world.setBlockToAir(master_hash + dx, y - 1, z + dz);
                     world.setBlock(master_hash + dx + 1, y - 1, z + dz, Block.tilledField.blockID, 7, 3);
                     if (dx == -12) {
                        block_id = Block.crops.blockID;
                     } else if (dx == -6) {
                        block_id = Block.carrot.blockID;
                     } else if (dx == 0) {
                        block_id = Block.potato.blockID;
                     } else if (dx == 6) {
                        block_id = Block.onions.blockID;
                     } else if (dx == 12) {
                        block_id = Block.pumpkinStem.blockID;
                     } else {
                        block_id = Block.melonStem.blockID;
                     }

                     world.setBlock(master_hash + dx - 1, y, z + dz, block_id);
                     world.setBlock(master_hash + dx + 1, y, z + dz, block_id);
                  }

                  world.setBlock(master_hash + dx, y - 1, z - 4, Block.waterMoving.blockID);
                  world.setBlock(master_hash + dx, y, z - 5, Block.torchWood.blockID);
               }

               for(dx = -12; dx <= 18; dx += 6) {
                  for(dz = -4; dz < 4; ++dz) {
                     world.setBlock(master_hash + dx - 1, y - 1, z + dz + 12, Block.tilledField.blockID, 7, 3);
                     world.setBlockToAir(master_hash + dx, y - 1, z + dz + 12);
                     world.setBlock(master_hash + dx + 1, y - 1, z + dz + 12, Block.tilledField.blockID, 7, 3);
                     if (dx == -12) {
                        block_id = Block.crops.blockID;
                     } else if (dx == -6) {
                        block_id = Block.carrot.blockID;
                     } else if (dx == 0) {
                        block_id = Block.potato.blockID;
                     } else if (dx == 6) {
                        block_id = Block.onions.blockID;
                     } else if (dx == 12) {
                        block_id = Block.pumpkinStem.blockID;
                     } else {
                        block_id = Block.melonStem.blockID;
                     }

                     world.setBlock(master_hash + dx - 1, y, z + dz + 12, block_id);
                     world.setBlock(master_hash + dx + 1, y, z + dz + 12, block_id);
                  }

                  world.setBlock(master_hash + dx, y - 1, z - 4 + 12, Block.waterMoving.blockID);
                  world.setBlock(master_hash + dx, y, z - 5 + 12, Block.torchWood.blockID);
               }

               List entity_items = world.getEntitiesWithinAABB(EntityItem.class, this.playerEntity.boundingBox.expand(128.0, 16.0, 128.0));
               Iterator i = entity_items.iterator();

               while(i.hasNext()) {
                  ((EntityItem)i.next()).setDead();
               }
            } else if (signal_type == EnumSignal.save_world_maps) {
               this.mcServer.saveWorldMaps();
            } else if (signal_type == EnumSignal.runegate_execute) {
               World.getDistanceFromDeltas((double)((float)this.playerEntity.runegate_destination_coords[0] + 0.5F) - this.playerEntity.posX, (double)((float)this.playerEntity.runegate_destination_coords[2] + 0.5F) - this.playerEntity.posZ);
               this.playerEntity.travelInsideDimension((double)((float)this.playerEntity.runegate_destination_coords[0] + 0.5F), (double)((float)this.playerEntity.runegate_destination_coords[1] + 0.1F), (double)((float)this.playerEntity.runegate_destination_coords[2] + 0.5F));
               this.playerEntity.is_runegate_teleporting = false;
               if (this.playerEntity.prevent_runegate_achievement) {
                  this.playerEntity.prevent_runegate_achievement = false;
               } else {
                  this.playerEntity.triggerAchievement(AchievementList.runegate);
               }

               this.sendPacketToPlayer(new Packet85SimpleSignal(EnumSignal.runegate_finished));
            } else if (signal_type == EnumSignal.curse_effect_learned) {
               this.playerEntity.getCurse().effect_known = true;
            } else if (signal_type == EnumSignal.transfered_to_world) {
               this.playerEntity.portal_grace_ticks = Math.min(this.playerEntity.portal_grace_ticks, 60);
            } else if (signal_type == EnumSignal.change_world_time) {
               if (packet.getBoolean()) {
                  this.mcServer.addTotalTimeForAllWorlds(packet.getInteger());
               } else {
                  world.addTotalWorldTime(packet.getInteger(), true);
               }
            } else if (signal_type == EnumSignal.slot_locked) {
               this.sendPacketToPlayer(new Packet85SimpleSignal(EnumSignal.unlock_slots));
            } else if (signal_type == EnumSignal.drop_one_item) {
               this.playerEntity.dropOneItem(packet.getBoolean());
            } else if (signal_type == EnumSignal.stopped_using_item) {
               this.playerEntity.stopUsingItem();
            } else if (signal_type != EnumSignal.digging_block_start && signal_type != EnumSignal.digging_block_cancel && signal_type != EnumSignal.digging_block_complete) {
               if (signal_type == EnumSignal.update_minecart_fuel) {
                  this.handleMinecartFuelUpdate(packet);
               } else if (signal_type == EnumSignal.confirm_or_cancel_item_in_use) {
                  if (this.playerEntity.itemInUse == null) {
                     this.playerEntity.sendPacket(new Packet85SimpleSignal(EnumSignal.confirm_or_cancel_item_in_use));
                  }
               } else {
                  Entity entity;
                  if (signal_type == EnumSignal.left_click_entity) {
                     entity = world.getEntityByID(packet.getEntityID());
                     this.playerEntity.func_143004_u();
                     if (entity != null && this.playerEntity.getDistanceSqToEntity(entity) < 256.0) {
                        if (entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityArrow || entity == this.playerEntity) {
                           this.kickPlayerFromServer("Attempting to attack an invalid entity");
                           this.mcServer.logWarning("Player " + this.playerEntity.getCommandSenderName() + " tried to attack an invalid entity");
                           return;
                        }

                        this.playerEntity.attackTargetEntityWithCurrentItem(entity);
                     }
                  } else {
                     RaycastCollision rc;
                     if (signal_type == EnumSignal.put_out_fire) {
                        rc = this.playerEntity.getSelectedObject(1.0F, true);
                        if (rc != null && rc.isBlock() && rc.getNeighborOfBlockHit() == Block.fire) {
                           world.extinguishFire((EntityPlayer)null, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, rc.face_hit);
                        }
                     } else if (signal_type == EnumSignal.mh) {
                        master_hash = NetClientHandler.getMasterHash(this.playerEntity.worldObj.getSeed());
                        this.playerEntity.master_hash_received = true;
                        this.playerEntity.master_hash_validated = master_hash == packet.getInteger();
                        if (!this.playerEntity.master_hash_validated) {
                           this.mcServer.getLogAgent().logWarning(this.playerEntity.username + " sent a master hash that did not validate!");
                        } else if (Minecraft.inDevMode()) {
                           System.out.println(this.playerEntity.username + " sent a master hash that validated");
                        }
                     } else if (signal_type == EnumSignal.block_hit_sound) {
                        Block block = world.getBlock(packet.getBlockX(), packet.getBlockY(), packet.getBlockZ());
                        if (block != null && block.stepSound != null) {
                           world.playSoundToNearExcept(this.playerEntity, block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 8.0F, block.stepSound.getPitch() * 0.5F);
                        }
                     } else if (signal_type == EnumSignal.tag_entity) {
                        entity = world.getEntityByID(packet.getEntityID());
                        if (entity == null) {
                           this.sendChatToPlayer("Was not able to " + (packet.getBoolean() ? "" : "un-") + "tag entity on server", EnumChatFormatting.RED);
                        } else {
                           entity.tagged = packet.getBoolean();
                           this.sendChatToPlayer(entity.getEntityName() + " is now " + (entity.tagged ? "" : "un-") + "tagged");
                        }
                     } else if (signal_type == EnumSignal.respawn_screen) {
                        this.playerEntity.sendPacket((new Packet85SimpleSignal(EnumSignal.respawn_screen)).setShort(this.playerEntity.respawn_countdown));
                     } else if (signal_type == EnumSignal.vision_dimming_to_server) {
                        this.playerEntity.vision_dimming_on_client = packet.getFloat() < 0.2F ? 0.0F : packet.getFloat();
                     } else if (signal_type == EnumSignal.entity_stats_dump) {
                        entity = this.playerEntity.worldObj.getEntityByID(packet.getEntityID());
                        if (entity instanceof EntityLivingBase) {
                           this.playerEntity.sendPacket(EntityStatsDump.generatePacketFor(entity.getAsEntityLivingBase()));
                        }
                     } else if (signal_type == EnumSignal.delete_selection) {
                        rc = this.playerEntity.getSelectedObject(1.0F, false);
                        if (rc != null) {
                           if (rc.isBlock()) {
                              world.destroyBlockWithoutDroppingItem(rc.block_hit_x, rc.block_hit_y, rc.block_hit_z);
                           } else if (rc.isEntity()) {
                              rc.getEntityHit().setDead();
                           }
                        }
                     } else if (signal_type == EnumSignal.teleport_away) {
                        entity = this.playerEntity.worldObj.getEntityByID(packet.getEntityID());
                        if (entity instanceof EntityPhaseSpider) {
                           ((EntityPhaseSpider)entity).tryTeleportAwayFrom(this.playerEntity, 3.0);
                        }
                     } else {
                        Minecraft.setErrorMessage("handleSimpleSignal: unhandled signal (" + packet.signal_type + ")");
                     }
                  }
               }
            } else {
               this.handleDiggingPacket(packet);
            }
         }
      }

   }

   public void handleSetDespawnCounters(Packet87SetDespawnCounters packet) {
      WorldServer world_server = this.mcServer.worldServerForDimension(this.playerEntity.dimension);

      for(int i = 0; i < packet.entries; ++i) {
         Entity entity = world_server.getEntityByID(packet.entity_id[i]);
         if (entity != null) {
            entity.refreshDespawnCounter(packet.despawn_counter[i]);
         }
      }

   }

   public void handlePlaySoundOnServerAtEntity(Packet89PlaySoundOnServerAtEntity packet) {
      Entity entity = this.playerEntity.worldObj.getEntityByID(packet.entity_id);
      if (entity != null) {
         entity.handlePacket89(packet);
      }
   }

   public void handleBroadcastToAssociatedPlayers(Packet90BroadcastToAssociatedPlayers packet) {
      WorldServer world_server = (WorldServer)this.playerEntity.worldObj;
      EntityTracker entity_tracker = world_server.getEntityTracker();
      if (packet.include_sender) {
         entity_tracker.sendPacketToAllAssociatedPlayers(this.playerEntity, packet.packet);
      } else {
         entity_tracker.sendPacketToAllPlayersTrackingEntity(this.playerEntity, packet.packet);
      }

   }

   private void handleMinecartFuelUpdate(Packet85SimpleSignal packet) {
      int i = -packet.getEntityID() - 100;
      if (i >= 0 && i < EntityMinecart.c.length) {
         if (!this.playerEntity.Sr[i]) {
            if (packet.getInteger() != EntityMinecart.S[i]) {
               EntityMinecart.updateFuel(this.playerEntity, packet, i);
            }

            this.playerEntity.Sr[i] = true;
         }
      } else {
         System.out.println("handleMinecartFuelUpdate: invalid index!");
      }

   }

   public boolean isConnectionClosed() {
      return this.connectionClosed;
   }

   public void sendChatToPlayer(String message) {
      this.sendChatToPlayer(message, EnumChatFormatting.YELLOW);
   }

   public void sendChatToPlayer(String message, EnumChatFormatting color) {
      this.sendPacketToPlayer(new Packet3Chat(ChatMessageComponent.createFromText(message).setColor(color)));
   }

   public INetworkManager getNetManager() {
      return this.netManager;
   }
}
