package net.minecraft.client.multiplayer;

import net.minecraft.block.Block;
import net.minecraft.block.IBlockWithPartner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet102WindowClick;
import net.minecraft.network.packet.Packet107CreativeSetSlot;
import net.minecraft.network.packet.Packet108EnchantItem;
import net.minecraft.network.packet.Packet16BlockItemSwitch;
import net.minecraft.network.packet.Packet55BlockDestroy;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.util.Coords;
import net.minecraft.util.Curse;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumItemInUseAction;
import net.minecraft.util.EnumSignal;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.World;

public class PlayerControllerMP {
   private final Minecraft mc;
   public final NetClientHandler netClientHandler;
   private int currentBlockX = -1;
   private int currentBlockY = -1;
   private int currentblockZ = -1;
   private ItemStack field_85183_f;
   public float curBlockDamageMP;
   private Coords last_block_destruction_coords;
   private int last_block_destruction_stage = -1;
   private float stepSoundTickCounter;
   private int blockHitDelay;
   public boolean isHittingBlock;
   private EnumGameType currentGameType;
   private int currentPlayerItem;
   private long use_button_unlock_ms;
   private long use_button_unlock_ms_override;
   private long ingestion_unlock_ms;
   public Item last_used_item;
   public int last_used_item_subtype;
   private long last_used_item_reset_ms;
   public boolean item_switch_or_restock_pending;
   private boolean run_toggled_on;
   public boolean listening_for_auto_harvest_mode_click;
   public Block auto_harvest_block;
   private int auto_harvest_block_metadata;
   public boolean cancel_auto_harvest_on_next_click;
   public long last_auto_harvest_ms;
   public boolean listening_for_auto_use_mode_click;
   public Item auto_use_mode_item;
   public boolean cancel_auto_use_mode_on_next_click;
   private static final Block[] blocks_for_which_metadata_must_match_for_automatic_harvest_mode;
   public boolean cancel_swing;

   public PlayerControllerMP(Minecraft par1Minecraft, NetClientHandler par2NetClientHandler) {
      this.currentGameType = EnumGameType.SURVIVAL;
      this.mc = par1Minecraft;
      this.netClientHandler = par2NetClientHandler;
   }

   public static void clickBlockCreative(Minecraft par0Minecraft, PlayerControllerMP par1PlayerControllerMP, int x, int y, int z, EnumFace face) {
      if (!par0Minecraft.theWorld.extinguishFire(par0Minecraft.thePlayer, x, y, z, face)) {
         par1PlayerControllerMP.onPlayerDestroyBlock(x, y, z, face);
      }

   }

   public void setPlayerCapabilities(EntityPlayer par1EntityPlayer) {
      this.currentGameType.configurePlayerCapabilities(par1EntityPlayer.capabilities);
   }

   public boolean enableEverythingIsScrewedUpMode() {
      return false;
   }

   public void setGameType(EnumGameType par1EnumGameType) {
      this.currentGameType = par1EnumGameType;
      this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
   }

   public void flipPlayer(EntityPlayer par1EntityPlayer) {
      par1EntityPlayer.rotationYaw = -180.0F;
   }

   public boolean shouldDrawHUD() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   private boolean onPlayerDestroyBlock(int par1, int par2, int par3, EnumFace face) {

      ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
      if (stack != null && stack.getItem() != null && stack.getItem().onBlockStartBreak(stack, par1, par2, par3, mc.thePlayer))
      {
         return false;
      }


      if (this.currentGameType.isAdventure() && !this.mc.thePlayer.isCurrentToolAdventureModeExempt(par1, par2, par3)) {
         return false;
      } else if (this.currentGameType.isCreative() && this.mc.thePlayer.getHeldItemStack() != null && this.mc.thePlayer.getHeldItemStack().getItem() instanceof ItemSword) {
         return false;
      } else {
         WorldClient var5 = this.mc.theWorld;
         Block var6 = Block.blocksList[var5.getBlockId(par1, par2, par3)];
         if (var6 == null) {
            return false;
         } else {
            int metadata = var5.getBlockMetadata(par1, par2, par3);
            int data = var6.blockID + (metadata << 12);
            if (this.mc.thePlayer.canSilkHarvestBlock(var6, metadata)) {
               data |= RenderGlobal.SFX_2001_WAS_SILK_HARVESTED;
            }

            var5.playAuxSFX(2001, par1, par2, par3, data);
            boolean is_partner_present = var6 instanceof IBlockWithPartner && var6.isPartnerPresent(var5, par1, par2, par3);
            boolean var8 = var6.removeBlockByPlayer(var5, mc.thePlayer, par1, par2, par3);
            if (var8 && is_partner_present) {
               int x = var6.getPartnerX(par1, metadata);
               int y = var6.getPartnerY(par2, metadata);
               int z = var6.getPartnerZ(par3, metadata);
               Block partner_block = var5.getBlock(x, y, z);
               if (partner_block instanceof IBlockWithPartner && ((IBlockWithPartner)partner_block).requiresPartner(var5.getBlockMetadata(x, y, z))) {
                  var5.setBlockToAir(x, y, z, 2);
               }
            }

            this.currentBlockY = -1;
            if (!this.currentGameType.isCreative()) {
            }

            return var8;
         }
      }
   }

   private void sendDiggingPacket(EnumSignal kind, int x, int y, int z, EnumFace face) {
      if (kind == EnumSignal.digging_block_start) {
         this.sendPacket((new Packet85SimpleSignal(kind)).setBlockCoords(x, y, z).setByte(face.ordinal()));
      } else {
         this.sendPacket((new Packet85SimpleSignal(kind)).setBlockCoords(x, y, z));
      }

   }

   private void updateBlockDestruction(boolean client_only) {
      if (client_only) {
         this.mc.theWorld.destroyBlockInWorldPartially(this.mc.thePlayer.entityId, this.currentBlockX, this.currentBlockY, this.currentblockZ, Block.getStageOfBlockDestruction(this.curBlockDamageMP));
      } else {
         int stage = Block.getStageOfBlockDestruction(this.curBlockDamageMP);
         if (this.last_block_destruction_stage != stage || this.last_block_destruction_coords == null || !this.last_block_destruction_coords.equals(this.currentBlockX, this.currentBlockY, this.currentblockZ)) {
            this.mc.theWorld.destroyBlockInWorldPartially(this.mc.thePlayer.entityId, this.currentBlockX, this.currentBlockY, this.currentblockZ, Block.getStageOfBlockDestruction(this.curBlockDamageMP));
            this.sendPacket(new Packet55BlockDestroy(this.mc.thePlayer.entityId, this.currentBlockX, this.currentBlockY, this.currentblockZ, stage));
            this.last_block_destruction_stage = stage;
            if (this.last_block_destruction_coords == null) {
               this.last_block_destruction_coords = new Coords(this.currentBlockX, this.currentBlockY, this.currentblockZ);
            } else {
               this.last_block_destruction_coords.set(this.currentBlockX, this.currentBlockY, this.currentblockZ);
            }
         }

      }
   }

   public void clickBlock(int par1, int par2, int par3, EnumFace face) {
      if (!this.currentGameType.isAdventure() || this.mc.thePlayer.isCurrentToolAdventureModeExempt(par1, par2, par3)) {
         if (this.currentGameType.isCreative()) {
            this.sendDiggingPacket(EnumSignal.digging_block_start, par1, par2, par3, face);
            clickBlockCreative(this.mc, this, par1, par2, par3, face);
            this.blockHitDelay = 5;
         } else if (!this.isHittingBlock || !this.sameToolAndBlock(par1, par2, par3)) {
            if (this.isHittingBlock) {
               this.sendDiggingPacket(EnumSignal.digging_block_cancel, this.currentBlockX, this.currentBlockY, this.currentblockZ, (EnumFace)null);
            }

            int var5 = this.mc.theWorld.getBlockId(par1, par2, par3);
            float damage_vs_block = this.mc.thePlayer.getDamageVsBlock(par1, par2, par3, true);
            if (damage_vs_block > 0.0F) {
               this.sendDiggingPacket(EnumSignal.digging_block_start, par1, par2, par3, face);
            } else if (this.mc.theWorld.getNeighborBlock(par1, par2, par3, face) == Block.fire) {
               this.mc.thePlayer.sendPacket(new Packet85SimpleSignal(EnumSignal.put_out_fire));
            }

            if (var5 > 0 && this.curBlockDamageMP == 0.0F) {
               Block.blocksList[var5].onBlockClicked(this.mc.theWorld, par1, par2, par3, this.mc.thePlayer);
            }

            if (var5 > 0 && damage_vs_block >= 1.0F) {
               this.onPlayerDestroyBlock(par1, par2, par3, face);
               this.mc.thePlayer.addHungerClientSide(0.01F * EnchantmentHelper.getEnduranceModifier(this.mc.thePlayer));
            } else {
               this.isHittingBlock = true;
               this.currentBlockX = par1;
               this.currentBlockY = par2;
               this.currentblockZ = par3;
               this.field_85183_f = this.mc.thePlayer.getHeldItemStack();
               this.curBlockDamageMP = 0.0F;
               this.stepSoundTickCounter = 0.0F;
            }
         }
      }

   }

   public void resetBlockRemoving() {
      if (this.isHittingBlock) {
         this.sendDiggingPacket(EnumSignal.digging_block_cancel, this.currentBlockX, this.currentBlockY, this.currentblockZ, (EnumFace)null);
      }

      this.isHittingBlock = false;
      this.curBlockDamageMP = 0.0F;
      this.updateBlockDestruction(true);
   }

   public void onPlayerDamageBlock(int par1, int par2, int par3, EnumFace face_hit) {
      this.syncCurrentPlayItem();
      if (this.blockHitDelay > 0) {
         --this.blockHitDelay;
      } else if (this.currentGameType.isCreative()) {
         this.blockHitDelay = 5;
         this.sendDiggingPacket(EnumSignal.digging_block_start, par1, par2, par3, face_hit);
         clickBlockCreative(this.mc, this, par1, par2, par3, face_hit);
      } else if (this.sameToolAndBlock(par1, par2, par3)) {
         int var5 = this.mc.theWorld.getBlockId(par1, par2, par3);
         if (var5 == 0) {
            this.isHittingBlock = false;
            return;
         }

         Block var6 = Block.blocksList[var5];
         this.curBlockDamageMP += this.mc.thePlayer.getDamageVsBlock(par1, par2, par3, true);
         this.mc.thePlayer.addHungerClientSide(0.01F * EnchantmentHelper.getEnduranceModifier(this.mc.thePlayer));
         if (this.stepSoundTickCounter % 4.0F == 0.0F && var6 != null) {
            this.mc.sndManager.playSound(var6.stepSound.getStepSound(), (float)par1 + 0.5F, (float)par2 + 0.5F, (float)par3 + 0.5F, (var6.stepSound.getVolume() + 1.0F) / 8.0F, var6.stepSound.getPitch() * 0.5F);
            this.mc.thePlayer.sendPacket((new Packet85SimpleSignal(EnumSignal.block_hit_sound)).setBlockCoords(par1, par2, par3));
         }

         ++this.stepSoundTickCounter;
         if (this.curBlockDamageMP >= 1.0F) {
            this.isHittingBlock = false;
            this.sendDiggingPacket(EnumSignal.digging_block_complete, par1, par2, par3, (EnumFace)null);
            this.onPlayerDestroyBlock(par1, par2, par3, face_hit);
            this.curBlockDamageMP = 0.0F;
            this.stepSoundTickCounter = 0.0F;
            this.blockHitDelay = 5;
            this.updateBlockDestruction(true);
         } else {
            this.updateBlockDestruction(false);
         }
      } else {
         this.clickBlock(par1, par2, par3, face_hit);
      }

   }

   public void updateController() {
      this.syncCurrentPlayItem();
      if (!this.mc.theWorld.isTheEnd()) {
         this.mc.sndManager.playRandomMusicIfReady();
      }

   }

   private boolean sameBlock(int x, int y, int z) {
      return x == this.currentBlockX && y == this.currentBlockY && z == this.currentblockZ;
   }

   public boolean sameToolAndBlock(int x, int y, int z) {
      if (!this.sameBlock(x, y, z)) {
         return false;
      } else {
         ItemStack initial_item_stack = this.field_85183_f;
         ItemStack current_item_stack = this.mc.thePlayer.getHeldItemStack();
         if (current_item_stack == initial_item_stack) {
            return true;
         } else if (ItemStack.areItemStacksEqual(current_item_stack, initial_item_stack, true, false, true, false)) {
            return true;
         } else {
            Item previous_item = initial_item_stack == null ? null : initial_item_stack.getItem();
            Item current_item = current_item_stack == null ? null : current_item_stack.getItem();
            Block block = this.mc.theWorld.getBlock(x, y, z);
            int metadata = this.mc.theWorld.getBlockMetadata(x, y, z);
            if (previous_item != null && previous_item.isEffectiveAgainstBlock(block, metadata)) {
               return false;
            } else {
               return current_item == null || !current_item.isEffectiveAgainstBlock(block, metadata);
            }
         }
      }
   }

   public void syncCurrentPlayItem() {
      int var1 = this.mc.thePlayer.inventory.currentItem;
      if (var1 != this.currentPlayerItem) {
         this.currentPlayerItem = var1;
         this.netClientHandler.addToSendQueue(new Packet16BlockItemSwitch(this.currentPlayerItem));
      }

   }

   public EntityClientPlayerMP func_78754_a(World par1World) {
      return new EntityClientPlayerMP(this.mc, par1World, this.mc.getSession(), this.netClientHandler);
   }

   public void setLastUsedItem(Item item, int item_subtype) {
      this.last_used_item = item;
      this.last_used_item_subtype = item_subtype;
      if (item != null) {
         this.last_used_item_reset_ms = System.currentTimeMillis() + 3000L;
      }

   }

   public void leftClickEntity(Entity target) {
      this.syncCurrentPlayItem();
      this.sendPacket((new Packet85SimpleSignal(EnumSignal.left_click_entity)).setEntityID(target));
   }

   public ItemStack windowClick(int par1, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
      if (par2 >= 0 && par2 < par5EntityPlayer.openContainer.inventorySlots.size() && par5EntityPlayer.openContainer.getSlot(par2).locked) {
         return null;
      } else {
         short var6 = par5EntityPlayer.openContainer.getNextTransactionID(par5EntityPlayer.inventory);
         ItemStack var7 = par5EntityPlayer.openContainer.slotClick(par2, par3, par4, GuiScreen.isShiftKeyDown(), par5EntityPlayer);
         this.netClientHandler.addToSendQueue((new Packet102WindowClick(par1, par2, par3, par4, var7, var6)).setHoldingShift(GuiScreen.isShiftKeyDown()));
         return var7;
      }
   }

   public void sendEnchantPacket(int par1, int par2) {
      this.netClientHandler.addToSendQueue(new Packet108EnchantItem(par1, par2));
   }

   public void sendSlotPacket(ItemStack par1ItemStack, int par2) {
      if (this.currentGameType.isCreative()) {
         this.netClientHandler.addToSendQueue(new Packet107CreativeSetSlot(par2, par1ItemStack));
      }

   }

   public void func_78752_a(ItemStack par1ItemStack) {
      if (this.currentGameType.isCreative() && par1ItemStack != null) {
         this.netClientHandler.addToSendQueue(new Packet107CreativeSetSlot(-1, par1ItemStack));
      }

   }

   public boolean func_78763_f() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   public boolean isNotCreative() {
      return !this.currentGameType.isCreative();
   }

   public boolean isInCreativeMode() {
      return this.currentGameType.isCreative();
   }

   public boolean func_110738_j() {
      return this.mc.thePlayer.isRiding() && this.mc.thePlayer.ridingEntity instanceof EntityHorse;
   }

   public void sendPacket(Packet packet) {
      this.netClientHandler.addToSendQueue(packet);
   }

   public void setUseButtonDelay() {
      if (System.currentTimeMillis() + 500L > this.use_button_unlock_ms) {
         this.use_button_unlock_ms = System.currentTimeMillis() + 500L;
      }

   }

   public void setUseButtonDelayOverride(int milliseconds) {
      this.use_button_unlock_ms_override = System.currentTimeMillis() + (long)milliseconds;
   }

   public boolean useButtonEnabled() {
      if (this.use_button_unlock_ms_override > 0L) {
         if (System.currentTimeMillis() >= this.use_button_unlock_ms_override) {
            this.use_button_unlock_ms_override = 0L;
            this.use_button_unlock_ms = 0L;
            return true;
         } else {
            return false;
         }
      } else {
         return System.currentTimeMillis() >= this.use_button_unlock_ms;
      }
   }

   public void setIngestionDelay() {
      this.ingestion_unlock_ms = System.currentTimeMillis() + 1000L;
   }

   public boolean ingestionEnabled() {
      return System.currentTimeMillis() >= this.ingestion_unlock_ms;
   }

   public boolean autoStockEnabled() {
      long milliseconds_remaining = this.last_used_item_reset_ms - System.currentTimeMillis();
      long restock_delay_ms = 250L;
      return milliseconds_remaining > 0L && milliseconds_remaining <= 3000L - restock_delay_ms;
   }

   public void setListeningForAutoHarvestMode() {
      this.listening_for_auto_harvest_mode_click = true;
      this.listening_for_auto_use_mode_click = false;
      this.cancel_auto_harvest_on_next_click = false;
   }

   public void setAutoHarvestMode(int x, int y, int z) {
      this.auto_harvest_block = this.mc.theWorld.getBlock(x, y, z);
      if (this.auto_harvest_block == null) {
         this.clearAutoHarvestMode();
      } else {
         this.auto_harvest_block_metadata = this.mc.theWorld.getBlockMetadata(x, y, z);
         this.cancel_auto_harvest_on_next_click = false;
         this.last_auto_harvest_ms = System.currentTimeMillis();
         this.clearAutoUseMode();
      }
   }

   public void clearAutoHarvestMode() {
      this.auto_harvest_block = null;
      this.auto_harvest_block_metadata = 0;
      this.cancel_auto_harvest_on_next_click = false;
      this.last_auto_harvest_ms = 0L;
      if (!this.mc.gameSettings.keyBindAttack.pressed) {
         this.resetBlockRemoving();
      }

   }

   public boolean autoHarvestModeHasExpired() {
      return System.currentTimeMillis() - this.last_auto_harvest_ms > 5000L;
   }

   public boolean matchesAutoHarvestBlock(int x, int y, int z) {
      if (this.auto_harvest_block == null) {
         return false;
      } else if (this.mc.thePlayer.hasFoodEnergy() && !this.mc.thePlayer.isDead && !this.mc.thePlayer.inBed() && !this.autoHarvestModeHasExpired()) {
         Block block = this.mc.theWorld.getBlock(x, y, z);
         if ((this.auto_harvest_block == Block.dirt || this.auto_harvest_block == Block.grass) && (block == Block.dirt || block == Block.grass)) {
            return true;
         } else if (this.auto_harvest_block == Block.oreRedstoneGlowing && block == Block.oreRedstone) {
            return true;
         } else if (block != this.auto_harvest_block) {
            return false;
         } else {
            int metadata = this.mc.theWorld.getBlockMetadata(x, y, z);
            if (metadata == this.auto_harvest_block_metadata) {
               return true;
            } else {
               for(int i = 0; i < blocks_for_which_metadata_must_match_for_automatic_harvest_mode.length; ++i) {
                  if (blocks_for_which_metadata_must_match_for_automatic_harvest_mode[i] == block) {
                     return false;
                  }
               }

               return true;
            }
         }
      } else {
         this.clearAutoHarvestMode();
         return false;
      }
   }

   public void setListeningForAutoUseMode() {
      this.listening_for_auto_use_mode_click = true;
      this.listening_for_auto_harvest_mode_click = false;
      this.cancel_auto_use_mode_on_next_click = false;
   }

   public boolean isItemStackEligibleForAUM(ItemStack item_stack) {
      EnumItemInUseAction enum_item_in_use_action = item_stack.getItemInUseAction(this.mc.thePlayer);
      return enum_item_in_use_action != null && enum_item_in_use_action.isIngestion();
   }

   public boolean setAutoUseMode(ItemStack item_stack) {
      this.auto_use_mode_item = this.isItemStackEligibleForAUM(item_stack) ? item_stack.getItem() : null;
      this.listening_for_auto_use_mode_click = false;
      this.cancel_auto_use_mode_on_next_click = false;
      this.resetBlockRemoving();
      this.clearAutoHarvestMode();
      return this.auto_use_mode_item != null;
   }

   public boolean inAutoUseMode() {
      return this.auto_use_mode_item != null;
   }

   public void clearAutoUseMode() {
      this.auto_use_mode_item = null;
      this.cancel_auto_use_mode_on_next_click = false;
   }

   public void toggleRun(EntityPlayer player) {
      if (this.run_toggled_on) {
         this.mc.thePlayer.setSprinting(false);
      }

      this.run_toggled_on = !this.run_toggled_on;
      if (this.run_toggled_on && player.hasCurse(Curse.cannot_run, true)) {
         this.run_toggled_on = false;
      }

   }

   public boolean isRunToggledOn(EntityPlayer player) {
      if (this.run_toggled_on && player.hasCurse(Curse.cannot_run, true)) {
         this.run_toggled_on = false;
      }

      return this.run_toggled_on;
   }

   static {
      blocks_for_which_metadata_must_match_for_automatic_harvest_mode = new Block[]{Block.tallGrass};
   }
}
