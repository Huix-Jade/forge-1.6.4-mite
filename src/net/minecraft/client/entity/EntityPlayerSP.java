package net.minecraft.client.entity;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.mite.PlayerStatsHelper;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.potion.Potion;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Curse;
import net.minecraft.util.EnumLevelBonus;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Session;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

public abstract class EntityPlayerSP extends AbstractClientPlayer {
   public MovementInput movementInput;
   protected Minecraft mc;
   protected int sprintToggleTimer;
   public int sprintingTicksLeft;
   public float renderArmYaw;
   public float renderArmPitch;
   public float prevRenderArmYaw;
   public float prevRenderArmPitch;
   private int horseJumpPowerCounter;
   private float horseJumpPower;
   private MouseFilter field_71162_ch = new MouseFilter();
   private MouseFilter field_71160_ci = new MouseFilter();
   private MouseFilter field_71161_cj = new MouseFilter();
   public float timeInPortal;
   public float prevTimeInPortal;
   public Item crafting_item;
   public int crafting_period;
   public boolean crafting_proceed;
   public int crafting_ticks;
   public int crafting_experience_cost;
   public int crafting_experience_cost_tentative;
   public int open_inventory_suppressed_countdown;

   public EntityPlayerSP(Minecraft par1Minecraft, World par2World, Session par3Session, int par4) {
      super(par2World, par3Session.getUsername());
      this.mc = par1Minecraft;
      this.dimension = par4;
   }

   public void updateEntityActionState() {
      super.updateEntityActionState();
      this.moveStrafing = this.movementInput.moveStrafe;
      this.moveForward = this.movementInput.moveForward;
      this.isJumping = this.movementInput.jump;
      this.prevRenderArmYaw = this.renderArmYaw;
      this.prevRenderArmPitch = this.renderArmPitch;
      this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5);
      this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5);
   }

   public void onLivingUpdate() {
      if (this.sprintingTicksLeft > 0) {
         --this.sprintingTicksLeft;
         if (this.sprintingTicksLeft == 0) {
            this.setSprinting(false);
         }
      }

      if (this.sprintToggleTimer > 0) {
         --this.sprintToggleTimer;
      }

      if (this.mc.playerController.enableEverythingIsScrewedUpMode()) {
         this.posX = this.posZ = 0.5;
         this.posX = 0.0;
         this.posZ = 0.0;
         this.rotationYaw = (float)this.ticksExisted / 12.0F;
         this.rotationPitch = 10.0F;
         this.posY = 68.5;
      } else {
         if (this.open_inventory_suppressed_countdown == 0 && !PlayerStatsHelper.hasAchievementUnlocked(AchievementList.openInventory)) {
            this.mc.guiAchievement.queueAchievementInformation(AchievementList.openInventory);
         }

         if (this.open_inventory_suppressed_countdown > 0) {
            --this.open_inventory_suppressed_countdown;
         }

         this.prevTimeInPortal = this.timeInPortal;
         if (this.inPortal) {
            if (this.mc.currentScreen != null) {
               this.mc.displayGuiScreen((GuiScreen)null);
            }

            if (this.mc.imposed_gui_chat != null) {
               this.mc.closeImposedChat();
            }

            if (this.timeInPortal == 0.0F) {
               this.mc.sndManager.playSoundFX("portal.trigger", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
            }

            this.timeInPortal += 0.0125F;
            if (this.timeInPortal >= 1.0F) {
               this.timeInPortal = 1.0F;
            }

            this.inPortal = false;
         } else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getAmplifier() > 0 && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
            this.timeInPortal += 0.006666667F;
            if (this.timeInPortal > 1.0F) {
               this.timeInPortal = 1.0F;
            }
         } else {
            if (this.timeInPortal > 0.0F) {
               this.timeInPortal -= 0.05F;
            }

            if (this.timeInPortal < 0.0F) {
               this.timeInPortal = 0.0F;
            }
         }

         if (this.timeUntilPortal > 0) {
            --this.timeUntilPortal;
         }

         boolean var1 = this.movementInput.jump;
         float var2 = 0.8F;
         boolean var3 = this.movementInput.moveForward >= var2;
         this.movementInput.updatePlayerMoveState();
         if (this.isUsingItem() && !this.isRiding()) {
            MovementInput var10000 = this.movementInput;
            var10000.moveStrafe *= 0.2F;
            var10000 = this.movementInput;
            var10000.moveForward *= 0.2F;
            this.sprintToggleTimer = 0;
         }

         this.pushOutOfBlocks(this.posX - (double)this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ + (double)this.width * 0.35);
         this.pushOutOfBlocks(this.posX - (double)this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ - (double)this.width * 0.35);
         this.pushOutOfBlocks(this.posX + (double)this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ - (double)this.width * 0.35);
         this.pushOutOfBlocks(this.posX + (double)this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ + (double)this.width * 0.35);
         boolean var4 = this.hasFoodEnergy() || this.capabilities.allowFlying;
         if (this.onGround && (Minecraft.theMinecraft.playerController.isRunToggledOn(this) || !var3) && this.movementInput.moveForward >= var2 && !this.isSprinting() && var4 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness)) {
            if (this.sprintToggleTimer == 0) {
               this.sprintToggleTimer = 7;
            } else {
               this.setSprinting(true);
               this.sprintToggleTimer = 0;
            }
         }

         if (this.isSneaking()) {
            this.sprintToggleTimer = 0;
         }

         if (this.isSprinting() && (this.movementInput.moveForward < var2 || this.isCollidedHorizontally || !var4)) {
            this.setSprinting(false);
         }

         if (this.capabilities.allowFlying && !var1 && this.movementInput.jump) {
            if (this.flyToggleTimer == 0) {
               this.flyToggleTimer = 7;
            } else {
               this.capabilities.isFlying = !this.capabilities.isFlying;
               this.sendPlayerAbilities();
               this.flyToggleTimer = 0;
            }
         }

         if (this.capabilities.isFlying) {
            if (this.movementInput.sneak) {
               this.motionY -= 0.15;
            }

            if (this.movementInput.jump) {
               this.motionY += 0.15;
            }
         }

         if (this.isRidingHorse()) {
            if (this.horseJumpPowerCounter < 0) {
               ++this.horseJumpPowerCounter;
               if (this.horseJumpPowerCounter == 0) {
                  this.horseJumpPower = 0.0F;
               }
            }

            if (var1 && !this.movementInput.jump) {
               this.horseJumpPowerCounter = -10;
               this.func_110318_g();
            } else if (!var1 && this.movementInput.jump) {
               this.horseJumpPowerCounter = 0;
               this.horseJumpPower = 0.0F;
            } else if (var1) {
               ++this.horseJumpPowerCounter;
               if (this.horseJumpPowerCounter < 10) {
                  this.horseJumpPower = (float)this.horseJumpPowerCounter * 0.1F;
               } else {
                  this.horseJumpPower = 0.8F + 2.0F / (float)(this.horseJumpPowerCounter - 9) * 0.1F;
               }
            }
         } else {
            this.horseJumpPower = 0.0F;
         }

         super.onLivingUpdate();
         if (this.onGround && this.capabilities.isFlying) {
            this.capabilities.isFlying = false;
            this.sendPlayerAbilities();
         }
      }

   }

   public float getFOVMultiplier() {
      float var1 = 1.0F;
      if (this.capabilities.isFlying) {
         var1 *= 1.1F;
      }

      AttributeInstance var2 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      var1 = (float)((double)var1 * ((var2.getAttributeValue() / (double)this.capabilities.getWalkSpeed() + 1.0) / 2.0));
      var1 = Math.max(var1, 1.0F);
      var1 = 1.0F;
      boolean sneak_delta_y_enabled = true;
      if (!this.isSprinting() && !this.mc.playerController.isRunToggledOn(this)) {
         if (this.isSneaking() && !sneak_delta_y_enabled) {
            var1 *= 0.88F;
         }
      } else {
         var1 *= 1.22F;
      }

      if (this.mc.thePlayer.zoomed) {
         var1 /= 4.0F;
      }

      if (this.isUsingItem() && this.getItemInUse().getItem() instanceof ItemBow) {
         int var3 = this.getItemInUseDuration();
         float var4 = (float)var3 / (float)ItemBow.getTicksForMaxPull(this.getItemInUse());
         if (var4 > 1.0F) {
            var4 = 1.0F;
         } else {
            var4 *= var4;
         }

         var1 *= 1.0F - var4 * 0.15F;
      }

      return ForgeHooksClient.getOffsetFOV(this, var1);
   }

   public void closeScreen() {
      super.closeScreen();
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   public void displayGUIEditSign(TileEntity par1TileEntity) {
      if (par1TileEntity instanceof TileEntitySign) {
         this.mc.displayGuiScreen(new GuiEditSign((TileEntitySign)par1TileEntity));
      } else if (par1TileEntity instanceof TileEntityCommandBlock) {
         this.mc.displayGuiScreen(new GuiCommandBlock((TileEntityCommandBlock)par1TileEntity));
      }

   }

   public void displayGUIBook(ItemStack par1ItemStack) {
      Item var2 = par1ItemStack.getItem();
      if (var2 instanceof ItemEditableBook) {
         this.mc.displayGuiScreen(new GuiScreenBook(this, par1ItemStack, false));
      } else if (var2 == Item.writableBook) {
         this.mc.displayGuiScreen(new GuiScreenBook(this, par1ItemStack, true));
      }

   }

   public void displayGUIChestForMinecart(IInventory par1IInventory) {
      this.mc.displayGuiScreen(new GuiChest(this, par1IInventory));
   }

   public void displayGUIChest(int x, int y, int z, IInventory par1IInventory) {
      this.mc.displayGuiScreen(new GuiChest(this, par1IInventory));
   }

   public void displayGUIHopper(TileEntityHopper par1TileEntityHopper) {
      this.mc.displayGuiScreen(new GuiHopper(this, par1TileEntityHopper));
   }

   public void displayGUIHopperMinecart(EntityMinecartHopper par1EntityMinecartHopper) {
      this.mc.displayGuiScreen(new GuiHopper(this, par1EntityMinecartHopper));
   }

   public void displayGUIHorse(EntityHorse par1EntityHorse, IInventory par2IInventory) {
      this.mc.displayGuiScreen(new GuiScreenHorseInventory(this, par2IInventory, par1EntityHorse));
   }

   public void displayGUIWorkbench(int par1, int par2, int par3) {
      this.mc.displayGuiScreen(new GuiCrafting(this, this.worldObj, par1, par2, par3));
   }

   public void displayGUIEnchantment(int par1, int par2, int par3, String par4Str) {
      this.mc.displayGuiScreen(new GuiEnchantment(this, this.worldObj, par1, par2, par3, par4Str));
   }

   public void displayGUIAnvil(int x, int y, int z) {
      this.mc.displayGuiScreen(new GuiRepair(this, x, y, z));
   }

   public void displayGUIFurnace(TileEntityFurnace par1TileEntityFurnace) {
      this.mc.displayGuiScreen(new GuiFurnace(this, par1TileEntityFurnace));
   }

   public void displayGUIBrewingStand(TileEntityBrewingStand par1TileEntityBrewingStand) {
      this.mc.displayGuiScreen(new GuiBrewingStand(this, par1TileEntityBrewingStand));
   }

   public void displayGUIBeacon(TileEntityBeacon par1TileEntityBeacon) {
      this.mc.displayGuiScreen(new GuiBeacon(this, par1TileEntityBeacon));
   }

   public void displayGUIDispenser(TileEntityDispenser par1TileEntityDispenser) {
      this.mc.displayGuiScreen(new GuiDispenser(this, par1TileEntityDispenser));
   }

   public void displayGUIMerchant(IMerchant par1IMerchant, String par2Str) {
      this.mc.displayGuiScreen(new GuiMerchant(this, par1IMerchant, par2Str));
   }

   public void onCriticalHit(Entity par1Entity) {
      this.mc.effectRenderer.addEffect(new EntityCrit2FX(this.mc.theWorld, par1Entity));
   }

   public void onEnchantmentCritical(Entity par1Entity) {
      EntityCrit2FX var2 = new EntityCrit2FX(this.mc.theWorld, par1Entity, EnumParticle.magicCrit);
      this.mc.effectRenderer.addEffect(var2);
   }

   public void onItemPickup(Entity par1Entity, int par2) {
      this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, par1Entity, this, -0.5F));
   }

   public boolean isSneaking() {
      return this.movementInput.sneak && !this.inBed() && !this.capabilities.isFlying;
   }

   public void setPlayerSPHealth(float par1) {
      float var2 = this.getHealth() - par1;
      if (var2 <= 0.0F) {
         this.setHealth(par1);
         if (var2 < 0.0F) {
            this.hurtResistantTime = this.maxHurtResistantTime / 2;
         }
      } else {
         this.lastDamage = var2;
         this.hurtResistantTime = this.maxHurtResistantTime;
         this.hurtTime = this.maxHurtTime = 10;
         this.setHealth(par1);
      }

      if (this.getHealth() <= 0.0F && this.mc.currentScreen != null) {
         this.mc.displayGuiScreen((GuiScreen)null);
      }

   }

   public void addChatMessage(String par1Str) {
      this.mc.ingameGUI.getChatGUI().addTranslatedMessage(par1Str);
   }

   public void addStat(StatBase par1StatBase, int par2) {
      if (par1StatBase != null) {
         if (par1StatBase.isAchievement()) {
            Achievement var3 = (Achievement)par1StatBase;
            if (!this.mc.statFileWriter.hasAchievementUnlocked(var3)) {
               this.mc.thePlayer.sendQueue.addToSendQueue((new Packet85SimpleSignal(EnumSignal.achievement_unlocked)).setInteger(var3.statId));
            }

            this.mc.statFileWriter.readStat(par1StatBase, par2);
         } else {
            this.mc.statFileWriter.readStat(par1StatBase, par2);
         }
      }

   }

   private boolean isBlockTranslucent(int par1, int par2, int par3) {
      return this.worldObj.isBlockNormalCube(par1, par2, par3);
   }

   protected boolean pushOutOfBlocks(double par1, double par3, double par5) {
      if (this.noClip)
      {
         return false;
      }
      int var7 = MathHelper.floor_double(par1);
      int var8 = MathHelper.floor_double(par3);
      int var9 = MathHelper.floor_double(par5);
      double var10 = par1 - (double)var7;
      double var12 = par5 - (double)var9;
      int entHeight = Math.max(Math.round(this.height), 1);

      boolean inTranslucentBlock = true;

      for (int i1 = 0; i1 < entHeight; i1++) {
         if (!this.isBlockTranslucent(var7, var7 + i1, var9)) {
            inTranslucentBlock = false;
         }
      }

      if (inTranslucentBlock) {
         boolean var14 = true;
         boolean var15 = true;
         boolean var16 = true;
         boolean var17 = true;
         for (int i1 = 0; i1 < entHeight; i1++)
         {
            if(this.isBlockTranslucent(var7 - 1, var8 + i1, var9))
            {
               var14 = false;
               break;
            }
         }
         for (int i1 = 0; i1 < entHeight; i1++)
         {
            if(this.isBlockTranslucent(var7 + 1, var8 + i1, var9))
            {
               var15 = false;
               break;
            }
         }
         for (int i1 = 0; i1 < entHeight; i1++)
         {
            if(this.isBlockTranslucent(var7, var8 + i1, var9 - 1))
            {
               var16 = false;
               break;
            }
         }
         for (int i1 = 0; i1 < entHeight; i1++) {
            if(this.isBlockTranslucent(var7, var8 + i1, var9 + 1)) {
               var17 = false;
               break;
            }
         }

         byte var18 = -1;
         double var19 = 9999.0;
         if (var14 && var10 < var19) {
            var19 = var10;
            var18 = 0;
         }

         if (var15 && 1.0 - var10 < var19) {
            var19 = 1.0 - var10;
            var18 = 1;
         }

         if (var16 && var12 < var19) {
            var19 = var12;
            var18 = 4;
         }

         if (var17 && 1.0 - var12 < var19) {
            var19 = 1.0 - var12;
            var18 = 5;
         }

         float var21 = 0.1F;
         if (var18 == 0) {
            this.motionX = (double)(-var21);
         }

         if (var18 == 1) {
            this.motionX = (double)var21;
         }

         if (var18 == 4) {
            this.motionZ = (double)(-var21);
         }

         if (var18 == 5) {
            this.motionZ = (double)var21;
         }
      }

      return false;
   }

   public void setSprinting(boolean par1) {
      if (par1 && this.hasCurse(Curse.cannot_run, true)) {
         par1 = false;
      }

      super.setSprinting(par1);
      this.sprintingTicksLeft = par1 ? 600 : 0;
   }

   public void setXPStats(int experience) {
      this.experience = experience;
   }

   public void sendChatToPlayer(ChatMessageComponent par1ChatMessageComponent) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(par1ChatMessageComponent.toStringWithFormatting(true));
   }

   public boolean canCommandSenderUseCommand(int par1, String par2Str) {
      return par1 <= 0;
   }

   public ChunkCoordinates getPlayerCoordinates() {
      return new ChunkCoordinates(MathHelper.floor_double(this.posX + 0.5), MathHelper.floor_double(this.posY + 0.5), MathHelper.floor_double(this.posZ + 0.5));
   }

   public void playSound(String par1Str, float par2, float par3) {
      PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(this, par1Str, par2, par3);
      if (MinecraftForge.EVENT_BUS.post(event))
      {
         return;
      }
      par1Str = event.name;
      this.worldObj.playSound(this.posX, this.posY - (double)this.yOffset, this.posZ, par1Str, par2, par3, false);
   }

   public boolean isRidingHorse() {
      return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse;
   }

   public float getHorseJumpPower() {
      return this.horseJumpPower;
   }

   protected void func_110318_g() {
   }

   public static int calcUnmodifiedCraftingPeriod(float quality_adjusted_crafting_difficulty) {
      if (quality_adjusted_crafting_difficulty < 25.0F) {
         return 25;
      } else {
         return quality_adjusted_crafting_difficulty > 100.0F ? (int)Math.round(Math.pow((double)(quality_adjusted_crafting_difficulty - 100.0F), 0.800000011920929)) + 100 : Math.round(quality_adjusted_crafting_difficulty);
      }
   }

   private float getBenchAndToolsModifier(Container container) {
      if (!(container instanceof ContainerWorkbench)) {
         return 0.0F;
      } else {
         ContainerWorkbench container_workbench = (ContainerWorkbench)container;
         SlotCrafting slot_crafting = (SlotCrafting)container_workbench.getSlot(0);
         ItemStack item_stack = slot_crafting.getStack();
         Item item = item_stack == null ? null : item_stack.getItem();
         IRecipe recipe = container_workbench.getRecipe();
         Material material_to_check_tool_bench_hardness_against = recipe == null ? item.getHardestMetalMaterial() : recipe.getMaterialToCheckToolBenchHardnessAgainst();
         if (material_to_check_tool_bench_hardness_against == null) {
            return 0.2F;
         } else {
            Material tool_material = BlockWorkbench.getToolMaterial(container_workbench.getBlockMetadata());
            if (tool_material != Material.flint && tool_material != Material.obsidian) {
               if (tool_material != Material.copper && tool_material != Material.silver && tool_material != Material.gold) {
                  if (tool_material == Material.iron) {
                     return 0.4F;
                  } else if (tool_material == Material.ancient_metal) {
                     return 0.5F;
                  } else if (tool_material == Material.mithril) {
                     return 0.6F;
                  } else if (tool_material == Material.adamantium) {
                     return 0.7F;
                  } else {
                     Minecraft.setErrorMessage("getBenchAndToolsModifier: unrecognized tool material " + tool_material);
                     return 0.0F;
                  }
               } else {
                  return 0.3F;
               }
            } else {
               return 0.2F;
            }
         }
      }
   }

   public int getCraftingPeriod(float quality_adjusted_crafting_difficulty) {
      int period = calcUnmodifiedCraftingPeriod(quality_adjusted_crafting_difficulty);
      if (this.hasCurse(Curse.clumsiness)) {
         period *= 2;
      }

      float bench_and_tools_modifier = this.getBenchAndToolsModifier(this.openContainer);
      return (int)Math.max((float)period / (1.0F + this.getLevelModifier(EnumLevelBonus.CRAFTING) + bench_and_tools_modifier), 25.0F);
   }

   public void clearCrafting() {
      this.crafting_item = null;
      this.crafting_period = 0;
      this.crafting_proceed = false;
      this.crafting_ticks = 0;
      this.crafting_experience_cost = 0;
   }

   public void resetCraftingProgress() {
      this.crafting_ticks = 0;
   }
}
