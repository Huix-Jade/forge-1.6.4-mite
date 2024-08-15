package net.minecraft.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemFirework extends Item {
   public ItemFirework(int par1) {
      super(par1, new Material[]{Material.gunpowder, Material.blaze, Material.coal}, "fireworks");
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, false);
      if (rc != null && rc.isBlock()) {
         if (player.onClient()) {
            player.swingArm();
         } else {
            World world = player.getWorld();
            if (!world.spawnEntityInWorld(new EntityFireworkRocket(world, (double)((float)rc.block_hit_x + rc.block_hit_offset_x), (double)((float)rc.block_hit_y + rc.block_hit_offset_y), (double)((float)rc.block_hit_z + rc.block_hit_offset_z), player.getHeldItemStack()))) {
               return false;
            }

            if (!player.inCreativeMode()) {
               player.convertOneOfHeldItem((ItemStack)null);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4, Slot slot) {
      if (par1ItemStack.hasTagCompound()) {
         NBTTagCompound var5 = par1ItemStack.getTagCompound().getCompoundTag("Fireworks");
         if (var5 != null) {
            if (var5.hasKey("Flight")) {
               par3List.add(StatCollector.translateToLocal("item.fireworks.flight") + " " + var5.getByte("Flight"));
            }

            NBTTagList var6 = var5.getTagList("Explosions");
            if (var6 != null && var6.tagCount() > 0) {
               for(int var7 = 0; var7 < var6.tagCount(); ++var7) {
                  NBTTagCompound var8 = (NBTTagCompound)var6.tagAt(var7);
                  ArrayList var9 = new ArrayList();
                  ItemFireworkCharge.func_92107_a(var8, var9);
                  if (var9.size() > 0) {
                     for(int var10 = 1; var10 < var9.size(); ++var10) {
                        var9.set(var10, "  " + (String)var9.get(var10));
                     }

                     par3List.addAll(var9);
                  }
               }
            }
         }
      }

   }
}
