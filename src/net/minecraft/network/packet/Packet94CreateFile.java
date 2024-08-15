package net.minecraft.network.packet;

import java.awt.Desktop;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketComponentBytes;

public class Packet94CreateFile extends Packet {
   public static final int CONTEXT_ENTITY_STATS_DUMP = 1;
   private String filepath;
   private PacketComponentBytes content;
   private byte context;
   private boolean open_in_editor;

   public Packet94CreateFile() {
   }

   public Packet94CreateFile(String filepath, byte[] content) {
      this(filepath, content, -1);
   }

   public Packet94CreateFile(String filepath, byte[] content, int compression_level) {
      this.filepath = filepath;
      this.content = new PacketComponentBytes(content, compression_level, this);
   }

   public void compressPayload() {
      this.content.compress();
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.filepath = readString(par1DataInput, 255);
      this.content = new PacketComponentBytes(this);
      this.content.readData(par1DataInput);
      this.context = par1DataInput.readByte();
      this.open_in_editor = par1DataInput.readBoolean();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      writeString(this.filepath, par1DataOutput);
      this.content.writeData(par1DataOutput);
      par1DataOutput.writeByte(this.context);
      par1DataOutput.writeBoolean(this.open_in_editor);
   }

   public void processPacket(NetHandler net_handler) {
      net_handler.handleCreateFile(this);
   }

   public int getPacketSize() {
      return Packet.getPacketSizeOfString(this.filepath) + this.content.getSize() + 1 + 1;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean canProcessAsync() {
      return true;
   }

   public Packet94CreateFile setOptions(int context, boolean open_in_editor) {
      this.context = (byte)context;
      this.open_in_editor = open_in_editor;
      return this;
   }

   public int getContext() {
      return this.context;
   }

   public String getFilepath() {
      return this.filepath;
   }

   public boolean writeFile() {
      File file = new File(this.filepath);
      file.getParentFile().mkdirs();

      try {
         FileWriter fw = new FileWriter(file);
         fw.write(this.content.getBytesAsString());
         fw.close();
         if (this.open_in_editor && Minecraft.theMinecraft != null && !Minecraft.theMinecraft.isFullScreen()) {
            try {
               if (Desktop.isDesktopSupported()) {
                  Desktop.getDesktop().edit(file);
               }
            } catch (Exception var4) {
               Exception e = var4;
               if (Minecraft.inDevMode()) {
                  e.printStackTrace();
               }
            }
         }

         return true;
      } catch (Exception var5) {
         Exception e = var5;
         System.out.println("Writing file to \"" + this.filepath + "\" [failed]\n");
         if (Minecraft.inDevMode()) {
            e.printStackTrace();
         }

         return false;
      }
   }
}
