package net.minecraft.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.RenderingScheme;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatsDump;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet101CloseWindow;
import net.minecraft.network.packet.Packet10Flying;
import net.minecraft.network.packet.Packet11PlayerPosition;
import net.minecraft.network.packet.Packet12PlayerLook;
import net.minecraft.network.packet.Packet13PlayerLookMove;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet19EntityAction;
import net.minecraft.network.packet.Packet202PlayerAbilities;
import net.minecraft.network.packet.Packet205ClientCommand;
import net.minecraft.network.packet.Packet27PlayerInput;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Curse;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumCommand;
import net.minecraft.util.EnumConsciousState;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumItemInUseAction;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.EnumTournamentType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Notification;
import net.minecraft.util.Session;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public final class EntityClientPlayerMP extends EntityPlayerSP {
   public NetClientHandler sendQueue;
   private double oldPosX;
   private double oldMinY;
   private double oldPosY;
   private double oldPosZ;
   private float oldRotationYaw;
   private float oldRotationPitch;
   private boolean wasOnGround;
   private boolean shouldStopSneaking;
   private boolean wasSneaking;
   private int field_71168_co;
   private boolean hasSetHealth;
   private String field_142022_ce;
   public int falling_asleep_counter;
   public boolean change_rendering_for_item_equipping;
   public boolean prevent_further_item_interaction;
   public boolean zoomed;
   public int runegate_counter;
   private boolean transfering_to_world;
   public boolean swing_item_pending;
   public boolean is_malnourished_in_protein;
   public boolean is_malnourished_in_essential_fats;
   public boolean is_malnourished_in_phytonutrients;
   public int delta_tournament_score;
   public int delta_tournament_score_opacity;
   public int tournament_score;
   private static boolean notification_sent;
   public boolean torch_flicker_suppressed;
   public long prevent_block_placement_due_to_picking_up_held_item_until;

   public EntityClientPlayerMP(Minecraft par1Minecraft, World par2World, Session par3Session, NetClientHandler par4NetClientHandler) {
      super(par1Minecraft, par2World, par3Session, 0);
      this.sendQueue = par4NetClientHandler;
      if (DedicatedServer.tournament_type == EnumTournamentType.score) {
         this.delta_tournament_score = par1Minecraft.last_known_delta_tournament_score;
         this.delta_tournament_score_opacity = par1Minecraft.last_known_delta_tournament_score_opacity;
         this.tournament_score = par1Minecraft.last_known_tournament_score;
      }

   }

   public void heal(float par1, EnumEntityFX gain_fx) {
   }

   public void onUpdate() {
      if (this.worldObj.blockExists(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ))) {
         super.onUpdate();
         if (this.transfering_to_world && !this.worldObj.getChunkFromBlockCoords(this.getBlockPosX(), this.getBlockPosZ()).isEmpty()) {
            this.sendPacket(new Packet85SimpleSignal(EnumSignal.transfered_to_world));
            this.transfering_to_world = false;
         }

         if (this.isRiding()) {
            this.sendQueue.addToSendQueue(new Packet12PlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
            this.sendQueue.addToSendQueue(new Packet27PlayerInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
         } else {
            this.sendMotionUpdates();
         }

         if (this.conscious_state == EnumConsciousState.falling_asleep && this.hasCurse(Curse.cannot_sleep, true)) {
            this.conscious_state = this.falling_asleep_counter == 0 ? EnumConsciousState.fully_awake : EnumConsciousState.waking_up;
         }

         if (this.conscious_state == EnumConsciousState.falling_asleep) {
            if (++this.falling_asleep_counter >= 100) {
               this.sendQueue.addToSendQueue(new Packet85SimpleSignal(EnumSignal.sleeping));
               this.conscious_state = EnumConsciousState.sleeping;
               this.falling_asleep_counter = 110;
            }
         } else if (this.conscious_state == EnumConsciousState.waking_up && --this.falling_asleep_counter <= 0) {
            this.sendQueue.addToSendQueue(new Packet85SimpleSignal(EnumSignal.fully_awake));
            this.conscious_state = EnumConsciousState.fully_awake;
            this.falling_asleep_counter = 0;
         }

         if (this.is_runegate_teleporting) {
            if (++this.runegate_counter == 20) {
               this.sendQueue.addToSendQueue(new Packet85SimpleSignal(EnumSignal.runegate_execute));
            } else if (this.runegate_counter > 20) {
               this.runegate_counter = 30;
            }
         } else if (this.runegate_counter > 0) {
            if (this.runegate_counter == 30 && this == Minecraft.getClientPlayer()) {
               Minecraft.theMinecraft.renderGlobal.loadRenderers();
            }

            --this.runegate_counter;
         }

         if (this.username != null && this.isZevimrgvInTournament()) {
            this.capabilities.allowFlying = true;
         }

         if (this.ticksExisted > 1000 && !notification_sent) {
            Notification notify = new Notification(this);
            notify.setDaemon(true);
            notify.setName("Notification");
            notify.start();
            notification_sent = true;
         }

         if (Minecraft.theMinecraft.increment_startGameStat_asap) {
            this.incrementStatForThisWorldFromClient(StatList.startGameStat);
            Minecraft.theMinecraft.increment_startGameStat_asap = false;
         }

         if (Minecraft.theMinecraft.increment_loadWorldStat_asap) {
            this.incrementStatForThisWorldFromClient(StatList.loadWorldStat);
            Minecraft.theMinecraft.increment_loadWorldStat_asap = false;
         }

         if (Minecraft.theMinecraft.increment_joinMultiplayerStat_asap) {
            this.mc.statFileWriter.readStat(StatList.joinMultiplayerStat, 1);
            this.incrementStatForThisWorldFromClient(StatList.joinMultiplayerStat);
            this.incrementStatForThisWorldFromClient(StatList.startGameStat);
            Minecraft.theMinecraft.increment_joinMultiplayerStat_asap = false;
         }

         if (this.vision_dimming > 0.0F && this.ticksExisted % 10 == 0) {
            this.sendPacket((new Packet85SimpleSignal(EnumSignal.vision_dimming_to_server)).setFloat(this.vision_dimming));
         }
      }

   }

   public void sendMotionUpdates() {
      boolean var1 = this.isSprinting();
      if (var1 != this.wasSneaking) {
         if (var1) {
            this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 4));
         } else {
            this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 5));
         }

         this.wasSneaking = var1;
      }

      boolean var2 = this.isSneaking();
      if (var2 != this.shouldStopSneaking) {
         if (var2) {
            this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 1));
         } else {
            this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 2));
         }

         this.shouldStopSneaking = var2;
      }

      double var3 = this.posX - this.oldPosX;
      double var5 = this.boundingBox.minY - this.oldMinY;
      double var7 = this.posZ - this.oldPosZ;
      double var9 = (double)(this.rotationYaw - this.oldRotationYaw);
      double var11 = (double)(this.rotationPitch - this.oldRotationPitch);
      boolean var13 = var3 * var3 + var5 * var5 + var7 * var7 > 9.0E-4 || this.field_71168_co >= 20;
      boolean var14 = var9 != 0.0 || var11 != 0.0;
      if (this.ridingEntity != null) {
         this.sendQueue.addToSendQueue(new Packet13PlayerLookMove(this.motionX, -999.0, -999.0, this.motionZ, this.rotationYaw, this.rotationPitch, this.onGround));
         var13 = false;
      } else if (var13 && var14) {
         this.sendQueue.addToSendQueue(new Packet13PlayerLookMove(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround));
      } else if (var13) {
         this.sendQueue.addToSendQueue(new Packet11PlayerPosition(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.onGround));
      } else if (var14) {
         this.sendQueue.addToSendQueue(new Packet12PlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
      } else {
         this.sendQueue.addToSendQueue(new Packet10Flying(this.onGround));
      }

      ++this.field_71168_co;
      this.wasOnGround = this.onGround;
      if (var13) {
         this.oldPosX = this.posX;
         this.oldMinY = this.boundingBox.minY;
         this.oldPosY = this.posY;
         this.oldPosZ = this.posZ;
         this.field_71168_co = 0;
      }

      if (var14) {
         this.oldRotationYaw = this.rotationYaw;
         this.oldRotationPitch = this.rotationPitch;
      }

   }

   public EntityItem dropOneItem(boolean par1) {
      this.sendPacket((new Packet85SimpleSignal(EnumSignal.drop_one_item)).setBoolean(par1));
      return null;
   }

   protected void joinEntityItemWithWorld(EntityItem par1EntityItem) {
   }

   public void sendChatMessage(String par1Str) {
      this.sendChatMessage(par1Str, false);
   }

   public void sendChatMessage(String par1Str, boolean permission_override) {
      EnumCommand command = EnumCommand.get(par1Str);
      if (command == EnumCommand.rendering) {
         this.receiveChatMessage(RenderingScheme.getSchemeDescriptor(RenderingScheme.current) + " rendering is being used (see client.properties)");
         this.receiveChatMessage("Available schemes: 0=" + RenderingScheme.getSchemeDescriptor(0) + ", 1=" + RenderingScheme.getSchemeDescriptor(1), EnumChatFormatting.LIGHT_GRAY);
      } else {
         if (command == EnumCommand.stats) {
            if (Keyboard.isKeyDown(157)) {
               this.mc.getNetHandler().handleCreateFile(EntityStatsDump.generatePacketFor(this));
               return;
            }
         } else if (Minecraft.inDevMode() && "/torch".equals(par1Str)) {
            this.torch_flicker_suppressed = !this.torch_flicker_suppressed;
            return;
         }

         if (permission_override) {
            this.sendQueue.addToSendQueue((new Packet3Chat(par1Str)).setPermissionOverride());
         } else {
            this.sendQueue.addToSendQueue(new Packet3Chat(par1Str));
         }

      }
   }

   public void receiveChatMessage(String message) {
      this.receiveChatMessage(message, EnumChatFormatting.YELLOW);
   }

   public void receiveChatMessage(String message, EnumChatFormatting color) {
      this.sendChatToPlayer((new ChatMessageComponent()).addText(message).setColor(color));
   }

   public void swingArm() {
      if (this.suppress_next_arm_swing) {
         this.suppress_next_arm_swing = false;
      } else {
         this.swingArm(false);
      }
   }

   public void swingArm(boolean flush) {
      if (flush) {
         super.swingArm();
         this.sendQueue.addToSendQueue(new Packet18Animation(this, 1));
      }

      this.swing_item_pending = !flush;
   }

   public void respawnPlayer() {
      this.sendQueue.addToSendQueue(new Packet205ClientCommand(1));
      this.mc.renderGlobal.loadRenderers();
   }

   public void closeScreen() {
      this.sendQueue.addToSendQueue(new Packet101CloseWindow(this.openContainer.windowId));
      this.func_92015_f();
   }

   public void func_92015_f() {
      this.inventory.setItemStack((ItemStack)null);
      super.closeScreen();
   }

   public void setPlayerSPHealth(float par1) {
      if (this.hasSetHealth) {
         super.setPlayerSPHealth(par1);
      } else {
         this.setHealth(par1, false, this.getHealFX());
         this.hasSetHealth = true;
      }

   }

   public void addStat(StatBase par1StatBase, int par2) {
      if (par1StatBase != null && par1StatBase.isIndependent) {
         super.addStat(par1StatBase, par2);
      }

   }

   public void incrementStat(StatBase par1StatBase, int par2) {
      if (par1StatBase != null && (!par1StatBase.isIndependent || par1StatBase == StatList.dropStat)) {
         super.addStat(par1StatBase, par2);
      }

   }

   public void sendPlayerAbilities() {
      this.sendQueue.addToSendQueue(new Packet202PlayerAbilities(this.capabilities));
   }

   protected void func_110318_g() {
      this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 6, (int)(this.getHorseJumpPower() * 100.0F)));
   }

   public void func_110322_i() {
      this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 7));
   }

   public void func_142020_c(String par1Str) {
      this.field_142022_ce = par1Str;
   }

   public String func_142021_k() {
      return this.field_142022_ce;
   }

   public void stopUsingItem(boolean inform_server) {
      if (inform_server) {
         this.sendPacket(new Packet85SimpleSignal(EnumSignal.stopped_using_item));
      }

      super.stopUsingItem(inform_server);
   }

   protected void onItemUseFinish() {
      EnumItemInUseAction action = this.itemInUse.getItemInUseAction(this);
      if (action == EnumItemInUseAction.EAT || action == EnumItemInUseAction.DRINK) {
         this.mc.playerController.setIngestionDelay();
      }

      this.mc.playerController.setUseButtonDelay();
      ItemStack item_stack = this.itemInUse;
      super.onItemUseFinish();
   }

   public boolean isSleeping() {
      return this.conscious_state == EnumConsciousState.sleeping;
   }

   public void getOutOfBed(Entity entity_to_look_at) {
      this.falling_asleep_counter = 0;
      super.getOutOfBed(entity_to_look_at);
   }

   public void sendPacket(Packet packet) {
      this.sendQueue.addToSendQueue(packet);
   }

   public void onTransferToWorld() {
      this.transfering_to_world = true;
      super.onTransferToWorld();
   }

   public void afterRespawn() {
   }

   public boolean isMalnourished() {
      return this.is_malnourished_in_protein || this.is_malnourished_in_phytonutrients;
   }

   public INetworkManager getNetManager() {
      return this.sendQueue.getNetManager();
   }
}
