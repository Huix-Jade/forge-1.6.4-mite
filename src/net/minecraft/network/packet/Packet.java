package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CompressionResult;
import net.minecraft.logging.ILogAgent;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DecompressionResult;
import net.minecraft.util.EnumQuality;
import net.minecraft.util.IntHashMap;

public abstract class Packet {
   public static IntHashMap packetIdToClassMap = new IntHashMap();
   private static Map packetClassToIdMap = new HashMap();
   private static Set clientPacketIdList = new HashSet();
   private static Set serverPacketIdList = new HashSet();
   protected ILogAgent field_98193_m;
   public final long creationTimeMillis = MinecraftServer.getSystemTimeMillis();
   public static long receivedID;
   public static long receivedSize;
   public static long sentID;
   public static long sentSize;
   public boolean isChunkDataPacket;
   private boolean has_been_added_to_tcp_send_queue;

   static void addIdClassMapping(int id, boolean isClient, boolean isServer, Class par3Class) {
      if (packetIdToClassMap.containsItem(id)) {
         throw new IllegalArgumentException("Duplicate packet id:" + id);
      } else if (packetClassToIdMap.containsKey(par3Class)) {
         throw new IllegalArgumentException("Duplicate packet class:" + par3Class);
      } else {
         packetIdToClassMap.addKey(id, par3Class);
         packetClassToIdMap.put(par3Class, id);
         if (isClient) {
            clientPacketIdList.add(id);
         }

         if (isServer) {
            serverPacketIdList.add(id);
         }

      }
   }

   public static Packet getNewPacket(ILogAgent par0ILogAgent, int par1) {
      try {
         Class var2 = (Class)packetIdToClassMap.lookup(par1);
         return var2 == null ? null : (Packet)var2.newInstance();
      } catch (Exception var3) {
         var3.printStackTrace();
         if (par0ILogAgent == null) {
            Minecraft.setErrorMessage("getNewPacket: was not able to create an instance of packet with id " + par1);
         } else {
            par0ILogAgent.logSevere("Skipping packet with id " + par1);
         }

         return null;
      }
   }

   public static void writeByteArray(DataOutput par0DataOutput, byte[] par1ArrayOfByte) throws IOException {
      par0DataOutput.writeShort(par1ArrayOfByte.length);
      par0DataOutput.write(par1ArrayOfByte);
   }

   public static byte[] readBytesFromStream(DataInput par0DataInput) throws IOException {
      short var1 = par0DataInput.readShort();
      if (var1 < 0) {
         throw new IOException("Key was smaller than nothing!  Weird key!");
      } else {
         byte[] var2 = new byte[var1];
         par0DataInput.readFully(var2);
         return var2;
      }
   }

   public final int getPacketId() {
      return (Integer)packetClassToIdMap.get(this.getClass());
   }

   public static Packet readPacket(ILogAgent par0ILogAgent, DataInput par1DataInput, boolean par2, Socket par3Socket) throws IOException {
      Packet var5 = null;
      int var6 = par3Socket.getSoTimeout();
      int var9 = -1;

      try {
         var9 = par1DataInput.readUnsignedByte();
         if (par2 && !serverPacketIdList.contains(var9) || !par2 && !clientPacketIdList.contains(var9)) {
            throw new IOException("Bad packet id " + var9);
         }

         var5 = getNewPacket(par0ILogAgent, var9);
         if (var5 == null) {
            throw new IOException("Bad packet id " + var9);
         }

         var5.field_98193_m = par0ILogAgent;
         if (var5 instanceof Packet254ServerPing) {
            par3Socket.setSoTimeout(1500);
         }

         var5.readPacketData(par1DataInput);
         ++receivedID;
         receivedSize += (long)var5.getPacketSize();
      } catch (EOFException var8) {
         par0ILogAgent.logSevere("Reached end of stream for " + par3Socket.getInetAddress() + ", packet id=" + var9);
         return null;
      }

      PacketCount.countPacket(var9, (long)var5.getPacketSize());
      par3Socket.setSoTimeout(var6);
      return var5;
   }

   public static void writePacket(Packet par0Packet, DataOutput par1DataOutput) throws IOException {
      par1DataOutput.write(par0Packet.getPacketId());
      par0Packet.writePacketData(par1DataOutput);
      ++sentID;
      sentSize += (long)par0Packet.getPacketSize();
   }

   public static int getPacketSizeOfString(String string) {
      return 2 + string.getBytes().length;
   }

   public static void writeString(String par0Str, DataOutput par1DataOutput) throws IOException {
      if (par0Str.length() > 32767) {
         throw new IOException("String too big");
      } else {
         par1DataOutput.writeShort(par0Str.length());
         par1DataOutput.writeChars(par0Str);
      }
   }

   public static String readString(DataInput par0DataInput, int par1) throws IOException {
      short var2 = par0DataInput.readShort();
      if (var2 > par1) {
         throw new IOException("Received string length longer than maximum allowed (" + var2 + " > " + par1 + ")");
      } else if (var2 < 0) {
         throw new IOException("Received string length is less than zero! Weird string!");
      } else {
         StringBuilder var3 = new StringBuilder();

         for(int var4 = 0; var4 < var2; ++var4) {
            var3.append(par0DataInput.readChar());
         }

         return var3.toString();
      }
   }

   public abstract void readPacketData(DataInput var1) throws IOException;

   public abstract void writePacketData(DataOutput var1) throws IOException;

   public abstract void processPacket(NetHandler var1);

   public abstract int getPacketSize();

   public boolean isRealPacket() {
      return false;
   }

   public boolean containsSameEntityIDAs(Packet par1Packet) {
      return false;
   }

   public boolean canProcessAsync() {
      return false;
   }

   public String toString() {
      String var1 = this.getClass().getSimpleName();
      return var1;
   }

   public static ItemStack readItemStack(DataInput par0DataInput) throws IOException {
      ItemStack var1 = null;
      short var2 = par0DataInput.readShort();
      if (var2 >= 0) {
         byte var3 = par0DataInput.readByte();
         short var4 = par0DataInput.readShort();
         var1 = new ItemStack(var2, var3, var4);
         int quality_ordinal = par0DataInput.readByte();
         if (quality_ordinal >= 0) {
            var1.setQuality(EnumQuality.values()[quality_ordinal]);
         }

         var1.stackTagCompound = readNBTTagCompound(par0DataInput);
         if (var1.isItemStackDamageable()) {
            var1.setItemDamage(readItemStackDamage(var1, par0DataInput));
         }
      }

      return var1;
   }

   public static void writeItemStack(ItemStack par0ItemStack, DataOutput par1DataOutput) throws IOException {
      if (par0ItemStack == null) {
         par1DataOutput.writeShort(-1);
      } else {
         par1DataOutput.writeShort(par0ItemStack.itemID);
         par1DataOutput.writeByte(par0ItemStack.stackSize);
         par1DataOutput.writeShort(par0ItemStack.getItemSubtype());
         par1DataOutput.writeByte(par0ItemStack.getQuality() == null ? -1 : par0ItemStack.getQuality().ordinal());
         NBTTagCompound var2 = null;
         if (par0ItemStack.getItem().isDamageable() || par0ItemStack.getItem().getShareTag()) {
            var2 = par0ItemStack.stackTagCompound;
         }

         writeNBTTagCompound(var2, par1DataOutput);
         if (par0ItemStack.isItemStackDamageable()) {
            writeItemStackDamage(par0ItemStack, par1DataOutput);
         }
      }

   }

   public static int getPacketSizeOfItemStack(ItemStack item_stack) {
      if (item_stack == null) {
         return 2;
      } else {
         return !item_stack.isItemStackDamageable() ? 6 : 6 + getPacketSizeOfItemStackDamage(item_stack.getMaxDamage());
      }
   }

   private static int getPacketSizeOfItemStackDamage(int max_damage) {
      return max_damage <= 127 ? 1 : (max_damage <= 32767 ? 2 : 4);
   }

   private static void writeItemStackDamage(ItemStack item_stack, DataOutput dos) throws IOException {
      int bytes = getPacketSizeOfItemStackDamage(item_stack.getMaxDamage());
      int damage = item_stack.getItemDamage();
      int min_allowed_damage = bytes == 1 ? -128 : (bytes == 2 ? Short.MIN_VALUE : Integer.MIN_VALUE);
      int max_allowed_damage = bytes == 1 ? 127 : (bytes == 2 ? 32767 : Integer.MAX_VALUE);
      if (damage < min_allowed_damage) {
         Minecraft.setErrorMessage("writeItemStack: damage value of " + damage + " is out of range for " + item_stack);
         damage = min_allowed_damage;
      } else if (damage > max_allowed_damage) {
         Minecraft.setErrorMessage("writeItemStack: damage value of " + damage + " is out of range for " + item_stack);
         damage = max_allowed_damage;
      }

      if (bytes == 1) {
         dos.writeByte(damage);
      } else if (bytes == 2) {
         dos.writeShort(damage);
      } else {
         dos.writeInt(damage);
      }

   }

   private static int readItemStackDamage(ItemStack item_stack, DataInput dis) throws IOException {
      int bytes = getPacketSizeOfItemStackDamage(item_stack.getMaxDamage());
      if (bytes == 1) {
         return dis.readByte();
      } else {
         return bytes == 2 ? dis.readShort() : dis.readInt();
      }
   }

   public static NBTTagCompound readNBTTagCompound(DataInput par0DataInput) throws IOException {
      short var1 = par0DataInput.readShort();
      if (var1 < 0) {
         return null;
      } else {
         byte[] var2 = new byte[var1];
         par0DataInput.readFully(var2);
         return CompressedStreamTools.decompress(var2);
      }
   }

   protected static void writeNBTTagCompound(NBTTagCompound par0NBTTagCompound, DataOutput par1DataOutput) throws IOException {
      if (par0NBTTagCompound == null) {
         par1DataOutput.writeShort(-1);
      } else {
         byte[] var2 = CompressedStreamTools.compress(par0NBTTagCompound);
         par1DataOutput.writeShort((short)var2.length);
         par1DataOutput.write(var2);
      }

   }

   public static final CompressionResult tryCompress(byte[] input, int compression_level, Packet packet) {
      return tryCompress(input, 0, input.length, compression_level, packet);
   }

   public static final CompressionResult tryCompress(byte[] input, int offset, int length, int compression_level, Packet packet) {
      if (offset >= input.length) {
         Minecraft.setErrorMessage("tryCompress: offset was >= input.length");
         offset = input.length - 1;
      }

      if (length > input.length - offset) {
         Minecraft.setErrorMessage("tryCompress: length was greater than input.length - offset");
         length = input.length - offset;
      }

      if (compression_level != -1 && (compression_level < 0 || compression_level > 9)) {
         Minecraft.setErrorMessage("tryCompress: invalid compression_level " + compression_level);
         compression_level = -1;
      }

      if (compression_level == 0 && offset == 0 && length == input.length) {
         return new CompressionResult(input, input.length, false);
      } else {
         Deflater deflater = new Deflater(compression_level);

         CompressionResult var9;
         try {
            int size_of_input = length - offset;
            deflater.setInput(input, offset, length);
            deflater.finish();
            byte[] output = new byte[size_of_input];
            int size_of_output = deflater.deflate(output);
            if (size_of_output > 0 && size_of_output < size_of_input) {
               var9 = new CompressionResult(output, size_of_output, true);
               return var9;
            }

            if (offset == 0 && length == input.length) {
               var9 = new CompressionResult(input, size_of_input, false);
               return var9;
            }

            System.arraycopy(input, offset, output, 0, length);
            var9 = new CompressionResult(output, length, false);
         } finally {
            deflater.end();
         }

         return var9;
      }
   }

   public static final DecompressionResult decompress(byte[] input, int expected_output_size, Packet packet) {
      return decompress(input, 0, input.length, expected_output_size, packet);
   }

   public static final DecompressionResult decompress(byte[] input, int offset, int length, int expected_output_size, Packet packet) {
      if (offset >= input.length) {
         Minecraft.setErrorMessage("decompress: offset was >= input.length");
         offset = input.length - 1;
      }

      if (length > input.length - offset) {
         Minecraft.setErrorMessage("decompress: length was greater than input.length - offset");
         length = input.length - offset;
      }

      if (length >= expected_output_size) {
         Minecraft.setErrorMessage("decompress: length >= expected_output_size?");
      }

      Inflater inflater = new Inflater();

      byte[] output;
      try {
         inflater.setInput(input, offset, length);
         output = new byte[expected_output_size];
         int output_size = inflater.inflate(output);
         if (output_size == expected_output_size) {
            DecompressionResult var8 = new DecompressionResult(output, output_size, true);
            return var8;
         }

         Minecraft.setErrorMessage("decompress: size_of_bytes_uncompressed discrepency, " + output_size + " vs " + expected_output_size);
      } catch (DataFormatException var12) {
         Minecraft.setErrorMessage("decompress: Bad compressed data format");
      } finally {
         inflater.end();
      }

      if (offset == 0 && length == input.length) {
         return new DecompressionResult(input, input.length, false);
      } else {
         output = new byte[length];
         System.arraycopy(input, offset, output, 0, length);
         return new DecompressionResult(output, length, false);
      }
   }

   public void compressPayload() {
   }

   public final boolean hasBeenAddedToTcpSendQueue() {
      return this.has_been_added_to_tcp_send_queue;
   }

   public final void setHasBeenAddedToTcpSendQueue() {
      this.has_been_added_to_tcp_send_queue = true;
   }

   static {
      addIdClassMapping(0, true, true, Packet0KeepAlive.class);
      addIdClassMapping(1, true, true, Packet1Login.class);
      addIdClassMapping(2, false, true, Packet2ClientProtocol.class);
      addIdClassMapping(3, true, true, Packet3Chat.class);
      addIdClassMapping(4, true, false, Packet4UpdateTime.class);
      addIdClassMapping(5, true, true, Packet5PlayerInventory.class);
      addIdClassMapping(6, true, false, Packet6SpawnPosition.class);
      addIdClassMapping(8, true, false, Packet8UpdateHealth.class);
      addIdClassMapping(9, true, true, Packet9Respawn.class);
      addIdClassMapping(10, true, true, Packet10Flying.class);
      addIdClassMapping(11, true, true, Packet11PlayerPosition.class);
      addIdClassMapping(12, true, true, Packet12PlayerLook.class);
      addIdClassMapping(13, true, true, Packet13PlayerLookMove.class);
      addIdClassMapping(14, false, true, Packet14BlockDig.class);
      addIdClassMapping(15, false, true, Packet15Place.class);
      addIdClassMapping(16, true, true, Packet16BlockItemSwitch.class);
      addIdClassMapping(17, true, false, Packet17Sleep.class);
      addIdClassMapping(18, true, true, Packet18Animation.class);
      addIdClassMapping(19, false, true, Packet19EntityAction.class);
      addIdClassMapping(20, true, false, Packet20NamedEntitySpawn.class);
      addIdClassMapping(22, true, false, Packet22Collect.class);
      addIdClassMapping(23, true, false, Packet23VehicleSpawn.class);
      addIdClassMapping(24, true, false, Packet24MobSpawn.class);
      addIdClassMapping(25, true, false, Packet25EntityPainting.class);
      addIdClassMapping(26, true, false, Packet26EntityExpOrb.class);
      addIdClassMapping(27, false, true, Packet27PlayerInput.class);
      addIdClassMapping(28, true, false, Packet28EntityVelocity.class);
      addIdClassMapping(29, true, false, Packet29DestroyEntity.class);
      addIdClassMapping(30, true, false, Packet30Entity.class);
      addIdClassMapping(31, true, false, Packet31RelEntityMove.class);
      addIdClassMapping(32, true, false, Packet32EntityLook.class);
      addIdClassMapping(33, true, false, Packet33RelEntityMoveLook.class);
      addIdClassMapping(34, true, true, Packet34EntityTeleport.class);
      addIdClassMapping(35, true, false, Packet35EntityHeadRotation.class);
      addIdClassMapping(38, true, false, Packet38EntityStatus.class);
      addIdClassMapping(39, true, false, Packet39AttachEntity.class);
      addIdClassMapping(40, true, false, Packet40EntityMetadata.class);
      addIdClassMapping(41, true, false, Packet41EntityEffect.class);
      addIdClassMapping(42, true, false, Packet42RemoveEntityEffect.class);
      addIdClassMapping(43, true, false, Packet43Experience.class);
      addIdClassMapping(44, true, false, Packet44UpdateAttributes.class);
      addIdClassMapping(51, true, false, Packet51MapChunk.class);
      addIdClassMapping(52, true, false, Packet52MultiBlockChange.class);
      addIdClassMapping(53, true, false, Packet53BlockChange.class);
      addIdClassMapping(54, true, false, Packet54PlayNoteBlock.class);
      addIdClassMapping(55, true, true, Packet55BlockDestroy.class);
      addIdClassMapping(56, true, false, Packet56MapChunks.class);
      addIdClassMapping(60, true, false, Packet60Explosion.class);
      addIdClassMapping(61, true, false, Packet61DoorChange.class);
      addIdClassMapping(62, true, false, Packet62LevelSound.class);
      addIdClassMapping(70, true, false, Packet70GameEvent.class);
      addIdClassMapping(71, true, false, Packet71Weather.class);
      addIdClassMapping(80, true, false, Packet80LongDistanceSound.class);
      addIdClassMapping(81, false, true, Packet81RightClick.class);
      addIdClassMapping(82, false, true, Packet82AddHunger.class);
      addIdClassMapping(83, true, false, Packet83EntityTeleportCompact.class);
      addIdClassMapping(84, true, false, Packet84EntityStateWithData.class);
      addIdClassMapping(85, true, true, Packet85SimpleSignal.class);
      addIdClassMapping(86, true, false, Packet86EntityTeleportWithMotion.class);
      addIdClassMapping(87, false, true, Packet87SetDespawnCounters.class);
      addIdClassMapping(88, true, false, Packet88UpdateStrongboxOwner.class);
      addIdClassMapping(89, false, true, Packet89PlaySoundOnServerAtEntity.class);
      addIdClassMapping(90, false, true, Packet90BroadcastToAssociatedPlayers.class);
      addIdClassMapping(91, true, false, Packet91PlayerStat.class);
      addIdClassMapping(92, true, false, Packet92UpdateTimeSmall.class);
      addIdClassMapping(93, true, false, Packet93WorldAchievement.class);
      addIdClassMapping(94, true, false, Packet94CreateFile.class);
      addIdClassMapping(97, true, false, Packet97MultiBlockChange.class);
      addIdClassMapping(100, true, false, Packet100OpenWindow.class);
      addIdClassMapping(101, true, true, Packet101CloseWindow.class);
      addIdClassMapping(102, false, true, Packet102WindowClick.class);
      addIdClassMapping(103, true, false, Packet103SetSlot.class);
      addIdClassMapping(104, true, false, Packet104WindowItems.class);
      addIdClassMapping(105, true, false, Packet105UpdateProgressbar.class);
      addIdClassMapping(106, true, true, Packet106Transaction.class);
      addIdClassMapping(107, true, true, Packet107CreativeSetSlot.class);
      addIdClassMapping(108, false, true, Packet108EnchantItem.class);
      addIdClassMapping(130, true, true, Packet130UpdateSign.class);
      addIdClassMapping(131, true, true, Packet131MapData.class);
      addIdClassMapping(132, true, false, Packet132TileEntityData.class);
      addIdClassMapping(133, true, false, Packet133TileEditorOpen.class);
      addIdClassMapping(200, true, false, Packet200Statistic.class);
      addIdClassMapping(201, true, false, Packet201PlayerInfo.class);
      addIdClassMapping(202, true, true, Packet202PlayerAbilities.class);
      addIdClassMapping(203, true, true, Packet203AutoComplete.class);
      addIdClassMapping(204, false, true, Packet204ClientInfo.class);
      addIdClassMapping(205, false, true, Packet205ClientCommand.class);
      addIdClassMapping(206, true, false, Packet206SetObjective.class);
      addIdClassMapping(207, true, false, Packet207SetScore.class);
      addIdClassMapping(208, true, false, Packet208SetDisplayObjective.class);
      addIdClassMapping(209, true, false, Packet209SetPlayerTeam.class);
      addIdClassMapping(250, true, true, Packet250CustomPayload.class);
      addIdClassMapping(252, true, true, Packet252SharedKey.class);
      addIdClassMapping(253, true, false, Packet253ServerAuthData.class);
      addIdClassMapping(254, false, true, Packet254ServerPing.class);
      addIdClassMapping(255, true, true, Packet255KickDisconnect.class);
   }
}
