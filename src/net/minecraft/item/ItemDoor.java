package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.world.World;

public class ItemDoor extends Item {
   private Material door_material;

   public ItemDoor(int par1, Material par2Material) {
      super(par1, par2Material, "doors/" + par2Material.name);
      this.door_material = par2Material;
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   public Block getBlock() {
      if (this.door_material == Material.wood) {
         return Block.doorWood;
      } else if (this.door_material == Material.copper) {
         return Block.doorCopper;
      } else if (this.door_material == Material.silver) {
         return Block.doorSilver;
      } else if (this.door_material == Material.gold) {
         return Block.doorGold;
      } else if (this.door_material == Material.iron) {
         return Block.doorIron;
      } else if (this.door_material == Material.mithril) {
         return Block.doorMithril;
      } else if (this.door_material == Material.adamantium) {
         return Block.doorAdamantium;
      } else {
         return this.door_material == Material.ancient_metal ? Block.doorAncientMetal : null;
      }
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, true);
      return rc != null && rc.isBlock() ? player.tryPlaceHeldItemAsBlock(rc, this.getBlock()) : false;
   }

   public static void placeDoorBlock(World par0World, int par1, int par2, int par3, int par4, Block par5Block) {
      byte var6 = 0;
      byte var7 = 0;
      if (par4 == 0) {
         var7 = 1;
      }

      if (par4 == 1) {
         var6 = -1;
      }

      if (par4 == 2) {
         var7 = -1;
      }

      if (par4 == 3) {
         var6 = 1;
      }

      int var8 = (par0World.isBlockNormalCube(par1 - var6, par2, par3 - var7) ? 1 : 0) + (par0World.isBlockNormalCube(par1 - var6, par2 + 1, par3 - var7) ? 1 : 0);
      int var9 = (par0World.isBlockNormalCube(par1 + var6, par2, par3 + var7) ? 1 : 0) + (par0World.isBlockNormalCube(par1 + var6, par2 + 1, par3 + var7) ? 1 : 0);
      boolean var10 = par0World.getBlockId(par1 - var6, par2, par3 - var7) == par5Block.blockID || par0World.getBlockId(par1 - var6, par2 + 1, par3 - var7) == par5Block.blockID;
      boolean var11 = par0World.getBlockId(par1 + var6, par2, par3 + var7) == par5Block.blockID || par0World.getBlockId(par1 + var6, par2 + 1, par3 + var7) == par5Block.blockID;
      boolean var12 = false;
      if (var10 && !var11) {
         var12 = true;
      } else if (var9 > var8) {
         var12 = true;
      }

      par0World.setBlock(par1, par2, par3, par5Block.blockID, par4, 2);
      par0World.setBlock(par1, par2 + 1, par3, par5Block.blockID, 8 | (var12 ? 1 : 0), 2);
      par0World.notifyBlocksOfNeighborChange(par1, par2, par3, par5Block.blockID);
      par0World.notifyBlocksOfNeighborChange(par1, par2 + 1, par3, par5Block.blockID);
   }
}
