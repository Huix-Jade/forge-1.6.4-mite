package net.minecraft.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

public interface Hopper extends IInventory {
   World getWorldObj();

   double getXPos();

   double getYPos();

   double getZPos();

   int getX();

   int getY();

   int getZ();
}
