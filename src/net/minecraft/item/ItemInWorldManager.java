package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityOoze;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet53BlockChange;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemInWorldManager {
   public World theWorld;
   public EntityPlayerMP thisPlayerMP;
   private EnumGameType gameType;
   private boolean tree_felling_in_progress;

   public ItemInWorldManager(World par1World) {
      this.gameType = EnumGameType.NOT_SET;
      this.theWorld = par1World;
   }

   public void setGameType(EnumGameType par1EnumGameType) {
      if (!Minecraft.inDevMode()) {
         par1EnumGameType = EnumGameType.SURVIVAL;
      }

      this.gameType = par1EnumGameType;
      par1EnumGameType.configurePlayerCapabilities(this.thisPlayerMP.capabilities);
      this.thisPlayerMP.sendPlayerAbilities();
   }

   public EnumGameType getGameType() {
      if (!Minecraft.inDevMode()) {
         this.gameType = EnumGameType.SURVIVAL;
      }

      return this.gameType;
   }

   public boolean isCreative() {
      return !Minecraft.inDevMode() ? false : this.gameType.isCreative();
   }

   public void initializeGameType(EnumGameType par1EnumGameType) {
      if (!Minecraft.inDevMode()) {
         par1EnumGameType = EnumGameType.SURVIVAL;
      }

      if (this.gameType == EnumGameType.NOT_SET) {
         this.gameType = par1EnumGameType;
      }

      this.setGameType(this.gameType);
   }

   public void onBlockClicked(int par1, int par2, int par3, EnumFace face) {
      if (!this.gameType.isAdventure() || this.thisPlayerMP.isCurrentToolAdventureModeExempt(par1, par2, par3)) {
         if (this.isCreative()) {
            if (!this.theWorld.extinguishFire((EntityPlayer)null, par1, par2, par3, face)) {
               this.tryHarvestBlock(par1, par2, par3);
            }
         } else {
            this.theWorld.extinguishFire((EntityPlayer)null, par1, par2, par3, face);
            float var5 = 1.0F;
            int var6 = this.theWorld.getBlockId(par1, par2, par3);
            if (var6 > 0) {
               Block.blocksList[var6].onBlockClicked(this.theWorld, par1, par2, par3, this.thisPlayerMP);
               var5 = this.thisPlayerMP.getDamageVsBlock(par1, par2, par3, true);
            }

            if (var6 > 0 && var5 >= 1.0F) {
               this.tryHarvestBlock(par1, par2, par3);
            }
         }
      }

   }

   private boolean removeBlock(int par1, int par2, int par3) {
      Block var4 = Block.blocksList[this.theWorld.getBlockId(par1, par2, par3)];
      int var5 = this.theWorld.getBlockMetadata(par1, par2, par3);
      if (var4 != null) {
      }

      boolean var6 = this.theWorld.setBlockToAir(par1, par2, par3);
      if (var4 != null && var6 && var4.isAlwaysOpaqueStandardFormCube() && var4.blockMaterial.requiresTool(var4, var5) && this.theWorld.rand.nextInt(100) == 0) {
         int ran = this.theWorld.rand.nextInt(6);
         int dx = ran == 0 ? -1 : (ran == 1 ? 1 : 0);
         int dy = ran == 2 ? -1 : (ran == 3 ? 1 : 0);
         int dz = ran == 4 ? -1 : (ran == 5 ? 1 : 0);
         int x = par1 + dx;
         int y = par2 + dy;
         int z = par3 + dz;
         if (this.theWorld.getBlock(x, y, z) == Block.stone) {
            int num_non_stone_blocks = 0;

            for(int i = 0; i < 6; ++i) {
               dx = i == 0 ? -1 : (i == 1 ? 1 : 0);
               dy = i == 2 ? -1 : (i == 3 ? 1 : 0);
               dz = i == 4 ? -1 : (i == 5 ? 1 : 0);
               if (this.theWorld.getBlock(x + dx, y + dy, z + dz) != Block.stone) {
                  ++num_non_stone_blocks;
                  if (num_non_stone_blocks > 1) {
                     break;
                  }
               }
            }

            if (num_non_stone_blocks == 1) {
               this.theWorld.setBlockToAir(x, y, z);
               EntityOoze ooze = new EntityOoze(this.theWorld);
               ooze.setSize(1);
               ooze.setLocationAndAngles((double)x + 0.5, (double)y + 0.25, (double)z + 0.5, this.theWorld.rand.nextFloat() * 360.0F, 0.0F);
               this.theWorld.spawnEntityInWorld(ooze);
               ooze.playSound(ooze.getJumpSound(), ooze.getSoundVolume(ooze.getJumpSound()), ((ooze.rand.nextFloat() - ooze.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
            }
         }
      }

      return var6;
   }

   public boolean tryHarvestBlock(int x, int y, int z) {
      if (this.theWorld.isRemote) {
         Minecraft.setErrorMessage("tryHarvestBlock: called on client?");
      }

      if (this.gameType.isAdventure() && !this.thisPlayerMP.isCurrentToolAdventureModeExempt(x, y, z)) {
         return false;
      } else if (this.gameType.isCreative() && this.thisPlayerMP.getHeldItemStack() != null && this.thisPlayerMP.getHeldItemStack().getItem() instanceof ItemSword) {
         return false;
      } else {
         Block block = this.theWorld.getBlock(x, y, z);
         if (block == null) {
            return false;
         } else {
            block.onBlockAboutToBeBroken((new BlockBreakInfo(this.theWorld, x, y, z)).setHarvestedBy(this.thisPlayerMP));
            if (this.theWorld.getBlock(x, y, z) == null) {
               return false;
            } else {
               BlockBreakInfo block_break_info = (new BlockBreakInfo(this.theWorld, x, y, z)).setHarvestedBy(this.thisPlayerMP);
               block = block_break_info.block;
               if (block == null) {
                  return false;
               } else {
                  boolean player_can_damage_block = this.thisPlayerMP.getCurrentPlayerStrVsBlock(x, y, z, true) > 0.0F;
                  int data = block_break_info.block_id + (block_break_info.getMetadata() << 12);
                  if (block_break_info.wasSilkHarvested()) {
                     data |= RenderGlobal.SFX_2001_WAS_SILK_HARVESTED;
                  }

                  this.theWorld.playAuxSFXAtEntity(this.thisPlayerMP, 2001, x, y, z, data);
                  boolean block_was_removed = this.removeBlock(x, y, z);
                  if (this.isCreative()) {
                     this.thisPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(x, y, z, this.theWorld));
                  } else {
                     ItemStack held_item_stack = this.thisPlayerMP.getHeldItemStack();
                     if (held_item_stack != null) {
                        Item item = held_item_stack.getItem();
                        if (item.onBlockDestroyed(block_break_info)) {
                           this.thisPlayerMP.addStat(StatList.objectUseStats[item.itemID], 1);
                        }
                     }

                     if (block_was_removed && player_can_damage_block) {
                        this.thisPlayerMP.addStat(StatList.mineBlockStatArray[block.blockID], 1);
                        if (block_break_info.wasSilkHarvested()) {
                           block.dropBlockAsItself(block_break_info);
                        } else {
                           block.dropBlockAsEntityItem(block_break_info);
                        }

                        if (block == Block.wood && !this.tree_felling_in_progress) {
                           int felling = EnchantmentHelper.getTreeFellingModifier(this.thisPlayerMP);
                           this.tree_felling_in_progress = true;

                           for(int dy = 1; dy <= felling && this.theWorld.getBlockId(x, y + dy, z) == Block.wood.blockID; ++dy) {
                              this.tryHarvestBlock(x, y + dy, z);
                           }

                           this.tree_felling_in_progress = false;
                        }
                     }
                  }

                  int i;
                  if (block_was_removed && !(block instanceof BlockTorch)) {
                     i = this.theWorld.getBlockId(x, y + 1, z);
                     if (Block.blocksList[i] != null) {
                        Block.blocksList[i].onUnderminedByPlayer(this.theWorld, this.thisPlayerMP, x, y + 1, z);
                     }

                     int[] dx = new int[]{0, 1, 0, -1};
                     int[] dz = new int[]{-1, 0, 1, 0};

                     for(int k = 0; k < dx.length; ++k) {
                        int block_id2 = this.theWorld.getBlockId(x + dx[k], y, z + dz[k]);
                        if (Block.blocksList[block_id2] != null) {
                           Block.blocksList[block_id2].onUnderminedByPlayer(this.theWorld, this.thisPlayerMP, x + dx[k], y, z + dz[k]);
                        }
                     }
                  }

                  if (block_was_removed && this.theWorld.isUnderworld() && y < 6) {
                     for(i = 0; i < EnumDirection.values().length; ++i) {
                        EnumDirection direction = EnumDirection.get(i);
                        Block neighbor = this.theWorld.getNeighborBlock(x, y, z, direction);
                        if (neighbor == Block.mantleOrCore) {
                           this.thisPlayerMP.triggerAchievement(AchievementList.portalToNether);
                           break;
                        }
                     }
                  }

                  return block_was_removed;
               }
            }
         }
      }
   }

   public boolean XactivateBlockOrUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack, int par4, int par5, int par6, EnumFace face, float par8, float par9, float par10) {
      Minecraft.setErrorMessage("activateBlockOrUseItem: this function shouldn't be in use anymore");
      return false;
   }

   public void setWorld(WorldServer par1WorldServer) {
      this.theWorld = par1WorldServer;
   }
}
