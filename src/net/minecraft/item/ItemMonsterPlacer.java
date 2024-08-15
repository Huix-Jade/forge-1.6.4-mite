package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityEarthElemental;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;

public class ItemMonsterPlacer extends Item {
   private Icon theIcon;

   public ItemMonsterPlacer(int par1) {
      super(par1, Material.iron, "spawn_egg");
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public String getItemDisplayName(ItemStack par1ItemStack) {
      String var2 = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
      if (par1ItemStack == null) {
         return var2;
      } else {
         String var3 = EntityList.getStringFromID(par1ItemStack.getItemSubtype());
         if (var3 != null) {
            var2 = var2 + " " + StatCollector.translateToLocal("entity." + var3 + ".name");
         }

         return var2;
      }
   }

   public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
      EntityEggInfo var3 = (EntityEggInfo)EntityList.entityEggs.get(par1ItemStack.getItemSubtype());
      return var3 != null ? (par2 == 0 ? var3.primaryColor : var3.secondaryColor) : 16777215;
   }

   public boolean requiresMultipleRenderPasses() {
      return true;
   }

   public Icon getIconFromSubtypeForRenderPass(int par1, int par2) {
      return par2 > 0 ? this.theIcon : super.getIconFromSubtypeForRenderPass(par1, par2);
   }

   public boolean tryEntityInteraction(Entity entity, EntityPlayer player, ItemStack item_stack) {
      if (entity instanceof EntityAgeable) {
         Class entity_class = EntityList.getClassFromID(item_stack.getItemSubtype());
         if (entity_class != null && entity_class.isAssignableFrom(entity.getClass())) {
            if (player.onClient()) {
               return true;
            } else {
               EntityAgeable entity_ageable = (EntityAgeable)entity;
               EntityAgeable newborn = entity_ageable.createChild(entity_ageable);
               if (newborn == null) {
                  return false;
               } else {
                  newborn.setGrowingAgeToNewborn();
                  newborn.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, 0.0F, 0.0F);
                  entity.worldObj.spawnEntityInWorld(newborn);
                  if (item_stack.hasDisplayName()) {
                     newborn.setCustomNameTag(item_stack.getDisplayName());
                  }

                  if (!player.inCreativeMode()) {
                     player.convertOneOfHeldItem((ItemStack)null);
                  }

                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, true);
      if (rc != null && rc.isBlock()) {
         if (player.onClient()) {
            player.swingArm();
         } else {
            float offset_y;
            if (rc.face_hit.isTop()) {
               if (!(rc.getBlockHit() instanceof BlockFence) && !(rc.getBlockHit() instanceof BlockWall)) {
                  if (rc.getBlockHit() instanceof BlockFarmland) {
                     offset_y = 0.0625F;
                  } else {
                     rc.getBlockHit().setBlockBoundsBasedOnStateAndNeighbors(rc.world, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z);
                     offset_y = (float)rc.getBlockHit().getBlockBoundsMaxY(Minecraft.getThreadIndex()) - 1.0F;
                  }
               } else {
                  offset_y = 0.5F;
               }
            } else {
               offset_y = 0.0F;
            }

            ItemStack item_stack = player.getHeldItemStack();
            if (rc.getBlockHit().isLiquid() && rc.face_hit.isTop() && EntityList.getClassFromID(item_stack.getItemSubtype()) == EntityEarthElemental.class) {
               --rc.neighbor_block_y;
            }

            Entity entity = spawnCreature(rc.world, item_stack.getItemSubtype(), (double)((float)rc.neighbor_block_x + 0.5F), (double)((float)rc.neighbor_block_y + offset_y), (double)((float)rc.neighbor_block_z + 0.5F), Minecraft.inDevMode() && !ctrl_is_down, rc.face_hit);
            if (entity != null) {
               if (entity instanceof EntityLivingBase && item_stack.hasDisplayName()) {
                  ((EntityLiving)entity).setCustomNameTag(item_stack.getDisplayName());
               }

               if (!player.inCreativeMode()) {
                  player.convertOneOfHeldItem((ItemStack)null);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static Entity spawnCreature(World par0World, int par1, double par2, double par4, double par6, boolean check_location, EnumFace face_hit) {
      if (!Minecraft.inDevMode()) {
         check_location = false;
      }

      if (!EntityList.entityEggs.containsKey(par1)) {
         return null;
      } else {
         Entity var8 = null;

         for(int var9 = 0; var9 < 1; ++var9) {
            var8 = EntityList.createEntityByID(par1, par0World);
            if (var8 != null && var8 instanceof EntityLivingBase) {
               if (face_hit == EnumFace.BOTTOM && var8.height > 1.0F) {
                  par4 -= (double)(var8.height - 1.0F);
               }

               EntityLiving var10 = (EntityLiving)var8;
               var8.setLocationAndAngles(par2, par4, par6, MathHelper.wrapAngleTo180_float(par0World.rand.nextFloat() * 360.0F), 0.0F);
               if (var10 instanceof EntityVillager) {
                  check_location = false;
               }

               if (check_location) {
                  double[] resulting_y_pos = new double[1];
                  if (!SpawnerAnimals.canCreatureTypeSpawnAtLocation(var10.getCreatureType(), par0World, var10.getBlockPosX(), var10.getBlockPosY(), var10.getBlockPosZ(), false, resulting_y_pos)) {
                     if (Minecraft.inDevMode()) {
                        System.out.println("spawnCreature: canCreatureTypeSpawnAtLocation() returned false for " + var10.getBlockPosString());
                     }

                     return null;
                  }

                  if (!var10.getCanSpawnHere(false)) {
                     if (Minecraft.inDevMode()) {
                        System.out.println("spawnCreature: getCanSpawnHere(false) returned false for " + var10.getBlockPosString());
                     }

                     return null;
                  }

                  if (!var10.getCanSpawnHere(true)) {
                     if (Minecraft.inDevMode()) {
                        System.out.println("spawnCreature: getCanSpawnHere() returned false for " + var10.getBlockPosString() + " due to invalid light level (BLV=" + par0World.getBlockLightValue(var10.getBlockPosX(), var10.getBlockPosY(), var10.getBlockPosZ()) + ")");
                     }

                     return null;
                  }
               }

               var10.rotationYawHead = var10.rotationYaw;
               var10.renderYawOffset = var10.rotationYaw;
               var10.onSpawnWithEgg((EntityLivingData)null);
               par0World.spawnEntityInWorld(var8);
               var10.makeLivingSound();
               if (Minecraft.inDevMode()) {
                  System.out.println("Spawning " + var10.getEntityName() + " from egg, UUID=" + var10.getUniqueID());
               }
            }
         }

         return var8;
      }
   }

   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
      Iterator var4 = EntityList.entityEggs.values().iterator();

      while(var4.hasNext()) {
         EntityEggInfo var5 = (EntityEggInfo)var4.next();
         par3List.add(new ItemStack(par1, 1, var5.spawnedID));
      }

   }

   public void registerIcons(IconRegister par1IconRegister) {
      super.registerIcons(par1IconRegister);
      this.theIcon = par1IconRegister.registerIcon(this.getIconString() + "_overlay");
   }
}
