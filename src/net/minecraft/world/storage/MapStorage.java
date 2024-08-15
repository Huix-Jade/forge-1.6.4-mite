package net.minecraft.world.storage;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.util.EnumSignal;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;

public class MapStorage {
   private ISaveHandler saveHandler;
   private Map loadedDataMap = new HashMap();
   private List loadedDataList = new ArrayList();
   private Map idCounts = new HashMap();

   public MapStorage(ISaveHandler par1ISaveHandler) {
      this.saveHandler = par1ISaveHandler;
      this.loadIdCounts();
   }

   public WorldSavedData loadData(Class par1Class, String par2Str) {
      WorldSavedData var3 = (WorldSavedData)this.loadedDataMap.get(par2Str);
      if (var3 != null) {
         return var3;
      } else {
         if (this.saveHandler != null) {
            try {
               File var4 = this.saveHandler.getMapFileFromName(par2Str);
               if (var4 != null && var4.exists()) {
                  try {
                     var3 = (WorldSavedData)par1Class.getConstructor(String.class).newInstance(par2Str);
                  } catch (Exception var7) {
                     throw new RuntimeException("Failed to instantiate " + par1Class.toString(), var7);
                  }

                  FileInputStream var5 = new FileInputStream(var4);
                  NBTTagCompound var6 = CompressedStreamTools.readCompressed(var5);
                  var5.close();
                  var3.readFromNBT(var6.getCompoundTag("data"));
               }
            } catch (Exception var8) {
               var8.printStackTrace();
            }
         }

         if (var3 != null) {
            this.loadedDataMap.put(par2Str, var3);
            this.loadedDataList.add(var3);
         }

         return var3;
      }
   }

   public void setData(String par1Str, WorldSavedData par2WorldSavedData) {
      if (par2WorldSavedData == null) {
         throw new RuntimeException("Can't set null data");
      } else {
         if (this.loadedDataMap.containsKey(par1Str)) {
            this.loadedDataList.remove(this.loadedDataMap.remove(par1Str));
         }

         this.loadedDataMap.put(par1Str, par2WorldSavedData);
         this.loadedDataList.add(par2WorldSavedData);
      }
   }

   public void saveAllData() {
      for(int var1 = 0; var1 < this.loadedDataList.size(); ++var1) {
         WorldSavedData var2 = (WorldSavedData)this.loadedDataList.get(var1);
         if (var2.isDirty()) {
            this.saveData(var2);
            var2.setDirty(false);
         }
      }

   }

   private void saveData(WorldSavedData par1WorldSavedData) {
      if (this.saveHandler != null) {
         try {
            File var2 = this.saveHandler.getMapFileFromName(par1WorldSavedData.mapName);
            if (var2 != null) {
               NBTTagCompound var3 = new NBTTagCompound();
               par1WorldSavedData.writeToNBT(var3);
               NBTTagCompound var4 = new NBTTagCompound();
               var4.setCompoundTag("data", var3);
               FileOutputStream var5 = new FileOutputStream(var2);
               CompressedStreamTools.writeCompressed(var4, var5);
               var5.close();
            }
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

   }

   private void loadIdCounts() {
      try {
         this.idCounts.clear();
         if (this.saveHandler == null) {
            return;
         }

         File var1 = this.saveHandler.getMapFileFromName("idcounts");
         if (var1 != null && var1.exists()) {
            DataInputStream var2 = new DataInputStream(new FileInputStream(var1));
            NBTTagCompound var3 = CompressedStreamTools.read((DataInput)var2);
            var2.close();
            Iterator var4 = var3.getTags().iterator();

            while(var4.hasNext()) {
               NBTBase var5 = (NBTBase)var4.next();
               if (var5 instanceof NBTTagShort) {
                  NBTTagShort var6 = (NBTTagShort)var5;
                  String var7 = var6.getName();
                  short var8 = var6.data;
                  this.idCounts.put(var7, var8);
               }
            }
         }
      } catch (Exception var9) {
         var9.printStackTrace();
      }

   }

   public int peekUniqueDataId(String prefix) {
      Short var2 = (Short)this.idCounts.get(prefix);
      return var2 == null ? 0 : Short.valueOf((short)(var2 + 1));
   }

   public void setUniqueDataId(World world, String prefix, short value) {
      this.idCounts.put(prefix, value);
      if (world instanceof WorldServer && "map".equals(prefix)) {
         world.getAsWorldServer().sendPacketToAllPlayersInAllDimensions((new Packet85SimpleSignal(EnumSignal.last_issued_map_id)).setShort(value));
      }

      if (Minecraft.inDevMode() && world instanceof WorldServer && !"map".equals(prefix)) {
         Minecraft.setErrorMessage("setUniqueDataId: prefix of \"" + prefix + "\" used, did you want to propagate it to client?");
      }

      if (this.saveHandler != null) {
         try {
            File var3 = this.saveHandler.getMapFileFromName("idcounts");
            if (var3 != null) {
               NBTTagCompound var4 = new NBTTagCompound();
               Iterator var5 = this.idCounts.keySet().iterator();

               while(var5.hasNext()) {
                  String var6 = (String)var5.next();
                  short var7 = (Short)this.idCounts.get(var6);
                  var4.setShort(var6, var7);
               }

               DataOutputStream var9 = new DataOutputStream(new FileOutputStream(var3));
               CompressedStreamTools.write(var4, (DataOutput)var9);
               var9.close();
            }
         } catch (Exception var9) {
            Exception var8 = var9;
            var8.printStackTrace();
         }

      }
   }

   public int getUniqueDataId(World world, String par1Str) {
      Short var2 = (Short)this.idCounts.get(par1Str);
      if (var2 == null) {
         var2 = Short.valueOf((short)0);
      } else {
         var2 = (short)(var2 + 1);
      }

      this.setUniqueDataId(world, par1Str, var2);
      return var2;
   }
}
