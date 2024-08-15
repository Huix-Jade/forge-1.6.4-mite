package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatList;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldAchievement;
import net.minecraft.world.WorldType;

public class Packet1Login extends Packet {
   public int clientEntityId;
   public WorldType terrainType;
   public boolean hardcoreMode;
   public EnumGameType gameType;
   public int dimension;
   public byte difficultySetting;
   public byte worldHeight;
   public byte maxPlayers;
   public byte village_conditions;
   public short earliest_MITE_release_run_in;
   public short latest_MITE_release_run_in;
   public boolean are_skills_enabled;
   public HashMap achievements;
   public long world_creation_time;
   public long total_world_time;

   public Packet1Login() {
   }

   public Packet1Login(int par1, WorldType par2WorldType, EnumGameType par3EnumGameType, boolean par4, int par5, int par6, int par7, int par8, byte village_conditions, HashMap achievements, int earliest_MITE_release_run_in, int latest_MITE_release_run_in, boolean are_skills_enabled, long world_creation_time, long total_world_time) {
      this.clientEntityId = par1;
      this.terrainType = par2WorldType;
      this.dimension = par5;
      this.difficultySetting = (byte)par6;
      this.gameType = par3EnumGameType;
      this.worldHeight = (byte)par7;
      this.maxPlayers = (byte)par8;
      this.hardcoreMode = par4;
      this.village_conditions = village_conditions;
      this.achievements = (HashMap)achievements.clone();
      this.earliest_MITE_release_run_in = (short)earliest_MITE_release_run_in;
      this.latest_MITE_release_run_in = (short)latest_MITE_release_run_in;
      this.are_skills_enabled = are_skills_enabled;
      this.world_creation_time = world_creation_time;
      this.total_world_time = total_world_time;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.clientEntityId = par1DataInput.readInt();
      String var2 = readString(par1DataInput, 16);
      this.terrainType = WorldType.parseWorldType(var2);
      if (this.terrainType == null) {
         this.terrainType = WorldType.DEFAULT;
      }

      byte var3 = par1DataInput.readByte();
      this.hardcoreMode = (var3 & 8) == 8;
      int var4 = var3 & -9;
      this.gameType = EnumGameType.getByID(var4);
      this.dimension = par1DataInput.readByte();
      this.difficultySetting = par1DataInput.readByte();
      this.worldHeight = par1DataInput.readByte();
      this.maxPlayers = par1DataInput.readByte();
      this.village_conditions = par1DataInput.readByte();
      int num_achievements = par1DataInput.readByte();
      this.achievements = new HashMap();

      for(int i = 0; i < num_achievements; ++i) {
         Achievement achievement = (Achievement)StatList.getStat(par1DataInput.readInt());
         String username = readString(par1DataInput, 16);
         int day = par1DataInput.readInt();
         this.achievements.put(achievement, new WorldAchievement(achievement, username, day));
      }

      this.earliest_MITE_release_run_in = par1DataInput.readShort();
      this.latest_MITE_release_run_in = par1DataInput.readShort();
      this.are_skills_enabled = par1DataInput.readBoolean();
      this.world_creation_time = par1DataInput.readLong();
      this.total_world_time = par1DataInput.readLong();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.clientEntityId);
      writeString(this.terrainType == null ? "" : this.terrainType.getWorldTypeName(), par1DataOutput);
      int var2 = this.gameType.getID();
      if (this.hardcoreMode) {
         var2 |= 8;
      }

      par1DataOutput.writeByte(var2);
      par1DataOutput.writeByte(this.dimension);
      par1DataOutput.writeByte(this.difficultySetting);
      par1DataOutput.writeByte(this.worldHeight);
      par1DataOutput.writeByte(this.maxPlayers);
      par1DataOutput.writeByte(this.village_conditions);
      par1DataOutput.writeByte(this.achievements.size());
      Iterator i = this.achievements.entrySet().iterator();

      while(i.hasNext()) {
         Map.Entry entry = (Map.Entry)i.next();
         WorldAchievement wa = (WorldAchievement)entry.getValue();
         par1DataOutput.writeInt(wa.achievement.statId);
         writeString(wa.username, par1DataOutput);
         par1DataOutput.writeInt(wa.day);
      }

      par1DataOutput.writeShort(this.earliest_MITE_release_run_in);
      par1DataOutput.writeShort(this.latest_MITE_release_run_in);
      par1DataOutput.writeBoolean(this.are_skills_enabled);
      par1DataOutput.writeLong(this.world_creation_time);
      par1DataOutput.writeLong(this.total_world_time);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleLogin(this);
   }

   public int getPacketSize() {
      int var1 = 0;
      if (this.terrainType != null) {
         var1 = this.terrainType.getWorldTypeName().length();
      }

      int num_achievement_bytes = 1;

      WorldAchievement wa;
      for(Iterator i = this.achievements.entrySet().iterator(); i.hasNext(); num_achievement_bytes += 4 + wa.username.length() * 2 + 4) {
         Map.Entry entry = (Map.Entry)i.next();
         wa = (WorldAchievement)entry.getValue();
      }

      return 6 + 2 * var1 + 4 + 4 + 1 + 1 + 1 + 1 + 4 + 1 + num_achievement_bytes + 16;
   }
}
