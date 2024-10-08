package net.minecraft.network.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeDummyContainer;

public class Packet52MultiBlockChange extends Packet {
   public int xPosition;
   public int zPosition;
   public byte[] metadataArray;
   public int size;
   private static byte[] field_73449_e = new byte[0];

   public Packet52MultiBlockChange() {
      this.isChunkDataPacket = true;
   }

   public Packet52MultiBlockChange(int par1, int par2, short[] par3ArrayOfShort, int par4, World par5World) {
      Minecraft.setErrorMessage("Packet52 created?");
      this.isChunkDataPacket = true;
      this.xPosition = par1;
      this.zPosition = par2;
      this.size = par4;
      int var6 = 4 * par4;
      Chunk var7 = par5World.getChunkFromChunkCoords(par1, par2);

      try {
         if (par4 >= ForgeDummyContainer.clumpingThreshold) {

            if (field_73449_e.length < var6) {
               field_73449_e = new byte[var6];
            }
         } else {
            ByteArrayOutputStream var8 = new ByteArrayOutputStream(var6);
            DataOutputStream var9 = new DataOutputStream(var8);

            for(int var10 = 0; var10 < par4; ++var10) {
               int var11 = par3ArrayOfShort[var10] >> 12 & 15;
               int var12 = par3ArrayOfShort[var10] >> 8 & 15;
               int var13 = par3ArrayOfShort[var10] & 255;
               var9.writeShort(par3ArrayOfShort[var10]);
               var9.writeShort((short)((var7.getBlockID(var11, var13, var12) & 4095) << 4 | var7.getBlockMetadata(var11, var13, var12) & 15));
            }

            this.metadataArray = var8.toByteArray();
            if (this.metadataArray.length != var6) {
               throw new RuntimeException("Expected length " + var6 + " doesn't match received length " + this.metadataArray.length);
            }
         }
      } catch (IOException var14) {
         this.field_98193_m.logSevereException("Couldn't create chunk packet", var14);
         this.metadataArray = null;
      }

   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.xPosition = par1DataInput.readInt();
      this.zPosition = par1DataInput.readInt();
      this.size = par1DataInput.readShort() & '\uffff';
      int var2 = par1DataInput.readInt();
      if (var2 > 0) {
         this.metadataArray = new byte[var2];
         par1DataInput.readFully(this.metadataArray);
      }

   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.xPosition);
      par1DataOutput.writeInt(this.zPosition);
      par1DataOutput.writeShort((short)this.size);
      if (this.metadataArray != null) {
         par1DataOutput.writeInt(this.metadataArray.length);
         par1DataOutput.write(this.metadataArray);
      } else {
         par1DataOutput.writeInt(0);
      }

   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleMultiBlockChange(this);
   }

   public int getPacketSize() {
      return 10 + this.size * 4;
   }
}
