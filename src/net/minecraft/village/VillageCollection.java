package net.minecraft.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class VillageCollection extends WorldSavedData {
   private World worldObj;
   private final List villagerPositionsList = new ArrayList();
   private final List newDoors = new ArrayList();
   private final List villageList = new ArrayList();
   private int tickCounter;

   public VillageCollection(String var1) {
      super(var1);
   }

   public VillageCollection(World var1) {
      super("villages");
      this.worldObj = var1;
      this.markDirty();
   }

   public void func_82566_a(World var1) {
      this.worldObj = var1;
      Iterator var2 = this.villageList.iterator();

      while(var2.hasNext()) {
         Village var3 = (Village)var2.next();
         var3.func_82691_a(var1);
      }

   }

   public void addVillagerPosition(int var1, int var2, int var3) {
      if (this.villagerPositionsList.size() <= 64) {
         if (!this.isVillagerPositionPresent(var1, var2, var3)) {
            this.villagerPositionsList.add(new ChunkCoordinates(var1, var2, var3));
         }

      }
   }

   public void tick() {
      ++this.tickCounter;
      Iterator var1 = this.villageList.iterator();

      while(var1.hasNext()) {
         Village var2 = (Village)var1.next();
         var2.tick(this.tickCounter);
      }

      this.removeAnnihilatedVillages();
      this.dropOldestVillagerPosition();
      this.addNewDoorsToVillageOrCreateVillage();
      if (this.tickCounter % 400 == 0) {
         this.markDirty();
      }

   }

   private void removeAnnihilatedVillages() {
      Iterator var1 = this.villageList.iterator();

      while(var1.hasNext()) {
         Village var2 = (Village)var1.next();
         if (var2.isAnnihilated()) {
            var1.remove();
            this.markDirty();
         }
      }

   }

   public List getVillageList() {
      return this.villageList;
   }

   public Village findNearestVillage(int var1, int var2, int var3, int var4) {
      Village var5 = null;
      float var6 = Float.MAX_VALUE;
      Iterator var7 = this.villageList.iterator();

      while(var7.hasNext()) {
         Village var8 = (Village)var7.next();
         float var9 = var8.getCenter().getDistanceSquared(var1, var2, var3);
         if (!(var9 >= var6)) {
            float var10 = (float)(var4 + var8.getVillageRadius());
            if (!(var9 > var10 * var10)) {
               var5 = var8;
               var6 = var9;
            }
         }
      }

      return var5;
   }

   private void dropOldestVillagerPosition() {
      if (!this.villagerPositionsList.isEmpty()) {
         this.addUnassignedWoodenDoorsAroundToNewDoorsList((ChunkCoordinates)this.villagerPositionsList.remove(0));
      }
   }

   private void addNewDoorsToVillageOrCreateVillage() {
      for(int var1 = 0; var1 < this.newDoors.size(); ++var1) {
         VillageDoorInfo var2 = (VillageDoorInfo)this.newDoors.get(var1);
         boolean var3 = false;
         Iterator var4 = this.villageList.iterator();

         while(var4.hasNext()) {
            Village var5 = (Village)var4.next();
            int var6 = (int)var5.getCenter().getDistanceSquared(var2.posX, var2.posY, var2.posZ);
            float var7 = 32F + var5.getVillageRadius();
            if (var6 <= var7 * var7) {
               var5.addVillageDoorInfo(var2);
               var3 = true;
               break;
            }
         }

         if (!var3) {
            Village var8 = new Village(this.worldObj);
            var8.addVillageDoorInfo(var2);
            this.villageList.add(var8);
            this.markDirty();
         }
      }

      this.newDoors.clear();
   }

   private void addUnassignedWoodenDoorsAroundToNewDoorsList(ChunkCoordinates var1) {
      byte var2 = 16;
      byte var3 = 4;
      byte var4 = 16;

      for(int var5 = var1.posX - var2; var5 < var1.posX + var2; ++var5) {
         for(int var6 = var1.posY - var3; var6 < var1.posY + var3; ++var6) {
            for(int var7 = var1.posZ - var4; var7 < var1.posZ + var4; ++var7) {
               if (this.isWoodenDoorAt(var5, var6, var7)) {
                  VillageDoorInfo var8 = this.getVillageDoorAt(var5, var6, var7);
                  if (var8 == null) {
                     this.addDoorToNewListIfAppropriate(var5, var6, var7);
                  } else {
                     var8.lastActivityTimestamp = this.tickCounter;
                  }
               }
            }
         }
      }

   }

   private VillageDoorInfo getVillageDoorAt(int var1, int var2, int var3) {
      Iterator var4 = this.newDoors.iterator();

      VillageDoorInfo var5;
      do {
         if (!var4.hasNext()) {
            var4 = this.villageList.iterator();

            VillageDoorInfo var6;
            do {
               if (!var4.hasNext()) {
                  return null;
               }

               Village var7 = (Village)var4.next();
               var6 = var7.getVillageDoorAt(var1, var2, var3);
            } while(var6 == null);

            return var6;
         }

         var5 = (VillageDoorInfo)var4.next();
      } while(var5.posX != var1 || var5.posZ != var3 || Math.abs(var5.posY - var2) > 1);

      return var5;
   }

   private void addDoorToNewListIfAppropriate(int var1, int var2, int var3) {
      int var4 = ((BlockDoor)Block.doorWood).getDoorOrientation(this.worldObj, var1, var2, var3);
      int var5;
      int var6;
      if (var4 != 0 && var4 != 2) {
         var5 = 0;

         for(var6 = -5; var6 < 0; ++var6) {
            if (this.worldObj.canBlockSeeTheSky(var1, var2, var3 + var6)) {
               --var5;
            }
         }

         for(var6 = 1; var6 <= 5; ++var6) {
            if (this.worldObj.canBlockSeeTheSky(var1, var2, var3 + var6)) {
               ++var5;
            }
         }

         if (var5 != 0) {
            this.newDoors.add(new VillageDoorInfo(var1, var2, var3, 0, var5 > 0 ? -2 : 2, this.tickCounter));
         }
      } else {
         var5 = 0;

         for(var6 = -5; var6 < 0; ++var6) {
            if (this.worldObj.canBlockSeeTheSky(var1 + var6, var2, var3)) {
               --var5;
            }
         }

         for(var6 = 1; var6 <= 5; ++var6) {
            if (this.worldObj.canBlockSeeTheSky(var1 + var6, var2, var3)) {
               ++var5;
            }
         }

         if (var5 != 0) {
            this.newDoors.add(new VillageDoorInfo(var1, var2, var3, var5 > 0 ? -2 : 2, 0, this.tickCounter));
         }
      }

   }

   private boolean isVillagerPositionPresent(int var1, int var2, int var3) {
      Iterator var4 = this.villagerPositionsList.iterator();

      ChunkCoordinates var5;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         var5 = (ChunkCoordinates)var4.next();
      } while(var5.posX != var1 || var5.posY != var2 || var5.posZ != var3);

      return true;
   }

   private boolean isWoodenDoorAt(int var1, int var2, int var3) {
      int var4 = this.worldObj.getBlockId(var1, var2, var3);
      return var4 == Block.doorWood.blockID;
   }

   public void readFromNBT(NBTTagCompound var1) {
      this.tickCounter = var1.getInteger("Tick");
      NBTTagList var2 = var1.getTagList("Villages");

      for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
         NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
         Village var5 = new Village();
         var5.readVillageDataFromNBT(var4);
         this.villageList.add(var5);
      }

   }

   public void writeToNBT(NBTTagCompound var1) {
      var1.setInteger("Tick", this.tickCounter);
      NBTTagList var2 = new NBTTagList("Villages");
      Iterator var3 = this.villageList.iterator();

      while(var3.hasNext()) {
         Village var4 = (Village)var3.next();
         NBTTagCompound var5 = new NBTTagCompound("Village");
         var4.writeVillageDataToNBT(var5);
         var2.appendTag(var5);
      }

      var1.setTag("Villages", var2);
   }
}
