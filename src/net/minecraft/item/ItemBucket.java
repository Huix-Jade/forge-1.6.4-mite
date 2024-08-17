package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.DispenserBehaviorEmptyBucket;
import net.minecraft.dispenser.DispenserBehaviorFilledBucket;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumItemInUseAction;
import net.minecraft.util.Translator;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class ItemBucket extends ItemVessel {
   public ItemBucket(int id, Material material, Material contents) {
      super(id, material, contents, 4, 8, 1, "buckets/" + material.name + "/" + (contents == null ? "empty" : (contents == Material.water ? "water" : (contents == Material.lava ? "lava" : "stone"))));
      this.setCraftingDifficultyAsComponent(25.0F);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, true);
      if (rc != null && rc.isBlock()) {
         int x;
         int y;
         int z;
         if (this.isEmpty()) {
            Material material;
            if (rc.getBlockHitMaterial().isLiquid()) {
               x = rc.block_hit_x;
               y = rc.block_hit_y;
               z = rc.block_hit_z;
               material = rc.getBlockHitMaterial();
            } else {
               x = rc.neighbor_block_x;
               y = rc.neighbor_block_y;
               z = rc.neighbor_block_z;
               material = rc.getNeighborOfBlockHitMaterial();
            }

            if (material != null && material.isLiquid()) {
               if (player.inCreativeMode() && !player.canMineAndEditBlock(x, y, z)) {
                  return false;
               } else {
                  if (player.onServer()) {
                     if (player.inCreativeMode() || ctrl_is_down) {
                        rc.world.setBlockToAir(x, y, z);
                     }

                     if (!player.inCreativeMode()) {
                        if (material == Material.lava && rc.world.rand.nextFloat() < this.getChanceOfMeltingWhenFilledWithLava()) {
                           player.addStat(StatList.objectBreakStats[this.itemID], 1);
                           ItemStack held_item_stack = player.getHeldItemStack();
                           ItemStack item_stack = this.getItemProducedWhenDestroyed(held_item_stack, DamageSource.lava);
                           if (item_stack == null) {
                              rc.world.blockFX(EnumBlockFX.item_consumed_by_lava, x, y, z);
                           }

                           player.convertOneOfHeldItem(item_stack);
                           if (!player.hasHeldItem()) {
                              player.getAsEntityPlayerMP().prevent_item_pickup_due_to_held_item_breaking_until = System.currentTimeMillis() + 1500L;
                           }
                        } else {

                           FillBucketEvent event = new FillBucketEvent(player, player.itemInUse, player.worldObj, rc);
                           if (MinecraftForge.EVENT_BUS.post(event))
                           {
                              return true;
                           }

                           if (event.getResult() == Event.Result.ALLOW)
                           {
                              if (player.capabilities.isCreativeMode)
                              {
                                 return false;
                              }

                              if (--player.itemInUse.stackSize <= 0)
                              {
                                 return false;
                              }

                              if (!player.inventory.addItemStackToInventory(event.result))
                              {
                                 player.dropPlayerItem(event.result);
                              }

                              return false;
                           }

                           player.convertOneOfHeldItem(new ItemStack(this.getPeerForContents(material)));
                        }
                     }
                  }

                  return true;
               }
            } else {
               return false;
            }
         } else if (this.contains(Material.stone)) {
            return false;
         } else {
            ItemStack item_stack = player.getHeldItemStack();
            if (this.contains(Material.water)) {
               Block block = rc.getBlockHit();
               x = rc.block_hit_x;
               y = rc.block_hit_y;
               z = rc.block_hit_z;
               EnumFace face_hit = rc.face_hit;
               if (rc.world.getBlock(x, y - 1, z) == Block.tilledField) {
                  --y;
                  block = rc.world.getBlock(x, y, z);
                  face_hit = EnumFace.TOP;
               }

               if (block == Block.tilledField && face_hit == EnumFace.TOP) {
                  if (BlockFarmland.fertilize(rc.world, x, y, z, player.getHeldItemStack(), player)) {
                     if (player.onServer() && !player.inCreativeMode()) {
                        player.convertOneOfHeldItem(new ItemStack(this.getEmptyVessel()));
                     }

                     return true;
                  }

                  return false;
               }
            }

            if (player.inCreativeMode() || rc.getBlockHitMaterial() != this.getContents() && rc.getNeighborOfBlockHitMaterial() != this.getContents()) {
               if (!rc.getBlockHit().isLiquid() && !rc.isBlockHitReplaceableBy(this.getBlockForContents(), 0)) {
                  x = rc.neighbor_block_x;
                  y = rc.neighbor_block_y;
                  z = rc.neighbor_block_z;
               } else {
                  x = rc.block_hit_x;
                  y = rc.block_hit_y;
                  z = rc.block_hit_z;
               }

               if (!player.canPlayerEdit(x, y, z, item_stack)) {
                  return false;
               } else if (this.tryPlaceContainedLiquid(rc.world, player, x, y, z, shouldContainedLiquidBePlacedAsSourceBlock(player, ctrl_is_down))) {
                  if (player.onServer() && !player.inCreativeMode()) {
                     player.convertOneOfHeldItem(new ItemStack(this.getEmptyVessel()));
                  }

                  return true;
               } else {
                  return false;
               }
            } else {
               if (player.onServer()) {
                  player.convertOneOfHeldItem(new ItemStack(this.getEmptyVessel()));
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static boolean shouldContainedLiquidBePlacedAsSourceBlock(EntityPlayer player, boolean ctrl_is_down) {
      if (player == null) {
         return false;
      } else if (player.inCreativeMode()) {
         return true;
      } else {
         return ctrl_is_down && player.experience >= 100;
      }
   }

   public Block getBlockForContents() {
      if (this.contains(Material.water)) {
         return Block.waterMoving;
      } else if (this.contains(Material.lava)) {
         return Block.lavaMoving;
      } else {
         Minecraft.setErrorMessage("getBlockForContents: no handler for contents " + this.getContents());
         return null;
      }
   }

   public boolean tryPlaceContainedLiquid(World world, EntityPlayer player, int x, int y, int z, boolean allow_placement_of_source_block) {
      if (this.isEmpty()) {
         Minecraft.setErrorMessage("tryPlaceContainedLiquid: bucket is empty");
         return false;
      } else {
         Material material_in_bucket = this.getContents();
         if (material_in_bucket == null) {
            Minecraft.setErrorMessage("tryPlaceContainedLiquid: material in bucket is null");
            return false;
         } else {
            Material target_block_material = world.getBlockMaterial(x, y, z);
            if (target_block_material.isSolid()) {
               return false;
            } else {
               boolean placement_prevented = false;
               if (material_in_bucket.canDouseFire() && world.getBlock(x, y, z) == Block.fire) {
                  if (!world.isRemote) {
                     world.douseFire(x, y, z, (Entity)null);
                  }

                  placement_prevented = true;
               } else if (material_in_bucket == Material.water && world.provider.isHellWorld) {
                  if (!world.isRemote) {
                     world.blockFX(EnumBlockFX.steam, x, y, z);
                  }

                  placement_prevented = true;
               }

               if (!placement_prevented) {
                  if (player != null && !player.inCreativeMode() && material_in_bucket == target_block_material) {
                     return true;
                  }

                  if (!world.isRemote) {
                     WorldServer world_server = (WorldServer)world;
                     if (!target_block_material.isSolid() && !target_block_material.isLiquid() && !world.isAirBlock(x, y, z)) {
                        world.destroyBlock((new BlockBreakInfo(world, x, y, z)).setFlooded((BlockFluid)this.getBlockForContents()), true);
                     }

                     if (material_in_bucket == Material.water && world.getBlockMaterial(x, y, z) == Material.lava) {
                        world.tryConvertLavaToCobblestoneOrObsidian(x, y, z);
                     } else {
                        if (material_in_bucket == Material.water && world.getBlock(x, y - 1, z) == Block.mantleOrCore) {
                           world.blockFX(EnumBlockFX.steam, x, y, z);
                           return true;
                        }

                        if (material_in_bucket == Material.lava && world.getBlockMaterial(x, y, z) == Material.water) {
                           world.tryConvertWaterToCobblestone(x, y, z);
                        } else {
                           if (player == null || !player.inCreativeMode()) {
                              if (material_in_bucket == Material.water) {
                                 if (!allow_placement_of_source_block) {
                                    world.scheduleBlockChange(x, y, z, Block.waterStill.blockID, this.getBlockForContents().blockID, 1, 16);
                                 } else if (!player.inCreativeMode()) {
                                    player.addExperience(-100);
                                 }
                              } else if (material_in_bucket == Material.lava) {
                                 if (!allow_placement_of_source_block) {
                                    world.scheduleBlockChange(x, y, z, Block.lavaMoving.blockID, this.getBlockForContents().blockID, 1, 48);
                                 } else if (!player.inCreativeMode()) {
                                    player.addExperience(-100);
                                 }
                              }
                           }

                           world.setBlock(x, y, z, this.getBlockForContents().blockID, 0, 3);
                        }
                     }
                  }
               }

               return true;
            }
         }
      }
   }

   public int getSimilarityToItem(Item item) {
      if (item instanceof ItemBucket) {
         ItemBucket item_bucket = (ItemBucket)item;
         if (item_bucket.getContents() == this.getContents()) {
            return 99;
         }

         if (item_bucket.isEmpty() || this.isEmpty()) {
            return 100 - (this.getVesselMaterial() == item_bucket.getVesselMaterial() ? 2 : 3);
         }
      }

      return super.getSimilarityToItem(item);
   }

   public EnumItemInUseAction getItemInUseAction(ItemStack par1ItemStack, EntityPlayer player) {
      return null;
   }

   public int getBurnTime(ItemStack item_stack) {
      return this.contains(Material.lava) ? 3200 : 0;
   }

   public int getHeatLevel(ItemStack item_stack) {
      return this.contains(Material.lava) ? 3 : 0;
   }

   public static ItemVessel getPeer(Material vessel_material, Material contents) {
      if (contents == null) {
         if (vessel_material == Material.copper) {
            return Item.bucketCopperEmpty;
         } else if (vessel_material == Material.silver) {
            return Item.bucketSilverEmpty;
         } else if (vessel_material == Material.gold) {
            return Item.bucketGoldEmpty;
         } else if (vessel_material == Material.iron) {
            return Item.bucketEmpty;
         } else if (vessel_material == Material.mithril) {
            return Item.bucketMithrilEmpty;
         } else if (vessel_material == Material.adamantium) {
            return Item.bucketAdamantiumEmpty;
         } else {
            return vessel_material == Material.ancient_metal ? Item.bucketAncientMetalEmpty : null;
         }
      } else if (contents == Material.water) {
         if (vessel_material == Material.copper) {
            return Item.bucketCopperWater;
         } else if (vessel_material == Material.silver) {
            return Item.bucketSilverWater;
         } else if (vessel_material == Material.gold) {
            return Item.bucketGoldWater;
         } else if (vessel_material == Material.iron) {
            return Item.bucketWater;
         } else if (vessel_material == Material.mithril) {
            return Item.bucketMithrilWater;
         } else if (vessel_material == Material.adamantium) {
            return Item.bucketAdamantiumWater;
         } else {
            return vessel_material == Material.ancient_metal ? Item.bucketAncientMetalWater : null;
         }
      } else if (contents == Material.lava) {
         if (vessel_material == Material.copper) {
            return Item.bucketCopperLava;
         } else if (vessel_material == Material.silver) {
            return Item.bucketSilverLava;
         } else if (vessel_material == Material.gold) {
            return Item.bucketGoldLava;
         } else if (vessel_material == Material.iron) {
            return Item.bucketLava;
         } else if (vessel_material == Material.mithril) {
            return Item.bucketMithrilLava;
         } else if (vessel_material == Material.adamantium) {
            return Item.bucketAdamantiumLava;
         } else {
            return vessel_material == Material.ancient_metal ? Item.bucketAncientMetalLava : null;
         }
      } else if (contents == Material.milk) {
         if (vessel_material == Material.copper) {
            return Item.bucketCopperMilk;
         } else if (vessel_material == Material.silver) {
            return Item.bucketSilverMilk;
         } else if (vessel_material == Material.gold) {
            return Item.bucketGoldMilk;
         } else if (vessel_material == Material.iron) {
            return Item.bucketIronMilk;
         } else if (vessel_material == Material.mithril) {
            return Item.bucketMithrilMilk;
         } else if (vessel_material == Material.adamantium) {
            return Item.bucketAdamantiumMilk;
         } else {
            return vessel_material == Material.ancient_metal ? Item.bucketAncientMetalMilk : null;
         }
      } else if (contents == Material.stone) {
         if (vessel_material == Material.copper) {
            return Item.bucketCopperStone;
         } else if (vessel_material == Material.silver) {
            return Item.bucketSilverStone;
         } else if (vessel_material == Material.gold) {
            return Item.bucketGoldStone;
         } else if (vessel_material == Material.iron) {
            return Item.bucketIronStone;
         } else if (vessel_material == Material.mithril) {
            return Item.bucketMithrilStone;
         } else if (vessel_material == Material.adamantium) {
            return Item.bucketAdamantiumStone;
         } else {
            return vessel_material == Material.ancient_metal ? Item.bucketAncientMetalStone : null;
         }
      } else {
         return null;
      }
   }

   public ItemVessel getPeerForContents(Material contents) {
      return getPeer(this.getVesselMaterial(), contents);
   }

   public ItemVessel getPeerForVesselMaterial(Material vessel_material) {
      return getPeer(vessel_material, this.getContents());
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 32;
   }

   public IBehaviorDispenseItem getDispenserBehavior() {
      return (IBehaviorDispenseItem)(this.isEmpty() ? new DispenserBehaviorEmptyBucket(this) : (this.getContents() != Material.water && this.getContents() != Material.lava ? null : new DispenserBehaviorFilledBucket((ItemBucket)this.getEmptyVessel())));
   }

   public float getChanceOfMeltingWhenFilledWithLava() {
      Material material = this.getVesselMaterial();
      return material == Material.adamantium ? 0.0F : (material == Material.gold ? 0.2F : 0.01F * (Material.mithril.durability / material.durability));
   }

   public void addInformation(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
      if (extended_info && player != null && player.experience >= 100 && (this.contains(Material.water) || this.contains(Material.lava))) {
         info.add((this.contains(Material.water) ? EnumChatFormatting.BLUE : EnumChatFormatting.RED) + Translator.get("item.tooltip.placeBucketAsSource"));
      }

      if (extended_info && this.contains(Material.lava)) {
         int chance_of_breaking = (int)(this.getChanceOfMeltingWhenFilledWithLava() * 100.0F);
         if (chance_of_breaking > 0) {
            info.add("");
            info.add(EnumChatFormatting.DARK_PURPLE + Translator.get("item.tooltip.whenBucketFilled"));
            info.add(EnumChatFormatting.RED + Translator.getFormatted("item.tooltip.chanceOfBucketMelting", chance_of_breaking));
         }
      }

   }
}
