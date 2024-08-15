package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.StringHelper;

public class Packet100OpenWindow extends Packet {
   public int windowId;
   public int inventoryType;
   public String windowTitle;
   public int slotsCount;
   public int x;
   public int y;
   public int z;
   public boolean has_set_coords;
   public static final int TYPE_CHEST_BLOCK = 0;
   public static final int TYPE_WORKBENCH = 1;
   public static final int TYPE_FURNACE = 2;
   public static final int TYPE_DISPENSER = 3;
   public static final int TYPE_ENCHANTMENT_TABLE = 4;
   public static final int TYPE_BREWING_STAND = 5;
   public static final int TYPE_MERCHANT = 6;
   public static final int TYPE_BEACON = 7;
   public static final int TYPE_ANVIL = 8;
   public static final int TYPE_HOPPER = 9;
   public static final int TYPE_DROPPER = 10;
   public static final int TYPE_HORSE = 11;
   public static final int TYPE_CHEST_MINECART = 12;
   public static final int TYPE_HOPPER_MINECART = 13;
   public boolean useProvidedWindowTitle;
   public int field_111008_f;

   public Packet100OpenWindow() {
   }

   public Packet100OpenWindow(int par1, int par2, String par3Str, int par4, boolean par5) {
      if (par3Str == null) {
         par3Str = "";
      }

      this.windowId = par1;
      this.inventoryType = par2;
      this.windowTitle = par3Str;
      this.slotsCount = par4;
      this.useProvidedWindowTitle = par5;
   }

   public Packet100OpenWindow(int par1, int par2, String par3Str, int par4, boolean par5, int entity_id) {
      this(par1, par2, par3Str, par4, par5);
      this.field_111008_f = entity_id;
   }

   public Packet100OpenWindow setCoords(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.has_set_coords = true;
      return this;
   }

   public Packet100OpenWindow setCoords(TileEntity tile_entity) {
      return this.setCoords(tile_entity.xCoord, tile_entity.yCoord, tile_entity.zCoord);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleOpenWindow(this);
   }

   public boolean hasCoords() {
      return this.inventoryType == 0 || this.inventoryType == 1 || this.inventoryType == 2 || this.inventoryType == 3 || this.inventoryType == 4 || this.inventoryType == 5 || this.inventoryType == 7 || this.inventoryType == 8 || this.inventoryType == 9 || this.inventoryType == 10;
   }

   public boolean hasTileEntity() {
      return this.inventoryType == 1 ? false : this.hasCoords();
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.windowId = par1DataInput.readByte() & 255;
      this.inventoryType = par1DataInput.readByte() & 255;
      this.windowTitle = readString(par1DataInput, 32);
      this.slotsCount = par1DataInput.readByte() & 255;
      this.useProvidedWindowTitle = par1DataInput.readBoolean();
      if (this.inventoryType == 11) {
         this.field_111008_f = par1DataInput.readInt();
      }

      if (this.hasCoords()) {
         this.x = par1DataInput.readInt();
         this.y = par1DataInput.readInt();
         this.z = par1DataInput.readInt();
      }

   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeByte(this.windowId & 255);
      par1DataOutput.writeByte(this.inventoryType & 255);
      writeString(this.windowTitle, par1DataOutput);
      par1DataOutput.writeByte(this.slotsCount & 255);
      par1DataOutput.writeBoolean(this.useProvidedWindowTitle);
      if (this.inventoryType == 11) {
         par1DataOutput.writeInt(this.field_111008_f);
      }

      if (this.hasCoords()) {
         if (!this.has_set_coords) {
            Minecraft.setErrorMessage("Packet100OpenWindow: coords not set for type " + this.inventoryType);
         }

         par1DataOutput.writeInt(this.x);
         par1DataOutput.writeInt(this.y);
         par1DataOutput.writeInt(this.z);
      }

   }

   public int getPacketSize() {
      int bytes = 2 + Packet.getPacketSizeOfString(this.windowTitle) + 2;
      if (this.inventoryType == 11) {
         bytes += 4;
      }

      if (this.hasCoords()) {
         bytes += 12;
      }

      return bytes;
   }

   private Entity getEntityByID(EntityPlayer player, int id) {
      return (Entity)(id == player.entityId ? player : player.worldObj.getEntityByID(id));
   }

   public void handleOpenWindow(EntityClientPlayerMP player) {
      WorldClient world = player.worldObj.getAsWorldClient();
      TileEntity tile_entity = world.getBlockTileEntity(this.x, this.y, this.z);
      if (this.hasTileEntity() && tile_entity == null) {
         Minecraft.setErrorMessage("handleOpenWindow: no tile entity found at " + StringHelper.getCoordsAsString(this.x, this.y, this.z));
      }

      if (this.inventoryType == 0) {
         player.displayGUIChest(this.x, this.y, this.z, new InventoryBasic(this.windowTitle, this.useProvidedWindowTitle, this.slotsCount));
         player.openContainer.windowId = this.windowId;
      } else if (this.inventoryType == 1) {
         player.displayGUIWorkbench(this.x, this.y, this.z);
         player.openContainer.windowId = this.windowId;
      } else if (this.inventoryType == 2) {
         TileEntityFurnace var4 = (TileEntityFurnace)tile_entity;
         if (this.useProvidedWindowTitle) {
            var4.setCustomInvName(this.windowTitle);
         }

         player.displayGUIFurnace(var4);
         player.openContainer.windowId = this.windowId;
      } else if (this.inventoryType == 3) {
         TileEntityDispenser var7 = (TileEntityDispenser)tile_entity;
         if (this.useProvidedWindowTitle) {
            var7.setCustomInvName(this.windowTitle);
         }

         player.displayGUIDispenser(var7);
         player.openContainer.windowId = this.windowId;
      } else if (this.inventoryType == 4) {
         player.displayGUIEnchantment(this.x, this.y, this.z, this.useProvidedWindowTitle ? this.windowTitle : null);
         player.openContainer.windowId = this.windowId;
      } else if (this.inventoryType == 5) {
         TileEntityBrewingStand var5 = (TileEntityBrewingStand)tile_entity;
         if (this.useProvidedWindowTitle) {
            var5.setCustomInvName(this.windowTitle);
         }

         player.displayGUIBrewingStand(var5);
         player.openContainer.windowId = this.windowId;
      } else if (this.inventoryType == 6) {
         player.displayGUIMerchant(new NpcMerchant(player), this.useProvidedWindowTitle ? this.windowTitle : null);
         player.openContainer.windowId = this.windowId;
      } else if (this.inventoryType == 7) {
         TileEntityBeacon var8 = (TileEntityBeacon)tile_entity;
         player.displayGUIBeacon(var8);
         if (this.useProvidedWindowTitle) {
            var8.setCustomInvName(this.windowTitle);
         }

         player.openContainer.windowId = this.windowId;
      } else if (this.inventoryType == 8) {
         tile_entity.setCustomInvName(this.windowTitle);
         player.displayGUIAnvil(this.x, this.y, this.z);
         player.openContainer.windowId = this.windowId;
      } else {
         TileEntityHopper var3;
         if (this.inventoryType == 9) {
            var3 = (TileEntityHopper)tile_entity;
            if (this.useProvidedWindowTitle) {
               var3.setCustomInvName(this.windowTitle);
            }

            player.displayGUIHopper(var3);
            player.openContainer.windowId = this.windowId;
         } else if (this.inventoryType == 10) {
            TileEntityDropper var6 = (TileEntityDropper)tile_entity;
            if (this.useProvidedWindowTitle) {
               var6.setCustomInvName(this.windowTitle);
            }

            player.displayGUIDispenser(var6);
            player.openContainer.windowId = this.windowId;
         } else if (this.inventoryType == 11) {
            Entity var9 = this.getEntityByID(player, this.field_111008_f);
            if (var9 != null && var9 instanceof EntityHorse) {
               player.displayGUIHorse((EntityHorse)var9, new AnimalChest(this.windowTitle, this.useProvidedWindowTitle, this.slotsCount));
               player.openContainer.windowId = this.windowId;
            }
         } else if (this.inventoryType == 12) {
            player.displayGUIChestForMinecart(new InventoryBasic(this.windowTitle, this.useProvidedWindowTitle, this.slotsCount));
            player.openContainer.windowId = this.windowId;
         } else if (this.inventoryType == 13) {
            var3 = new TileEntityHopper();
            if (this.useProvidedWindowTitle) {
               var3.setCustomInvName(this.windowTitle);
            }

            player.displayGUIHopper(var3);
            player.openContainer.windowId = this.windowId;
         } else {
            Minecraft.setErrorMessage("handleOpenWindow: type not handled " + this.inventoryType);
         }
      }

   }
}
