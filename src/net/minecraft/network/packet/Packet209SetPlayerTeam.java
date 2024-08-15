package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class Packet209SetPlayerTeam extends Packet {
   public String teamName = "";
   public String teamDisplayName = "";
   public String teamPrefix = "";
   public String teamSuffix = "";
   public Collection playerNames = new ArrayList();
   public int mode;
   public int friendlyFire;

   public Packet209SetPlayerTeam() {
   }

   public Packet209SetPlayerTeam(ScorePlayerTeam var1, int var2) {
      this.teamName = var1.func_96661_b();
      this.mode = var2;
      if (var2 == 0 || var2 == 2) {
         this.teamDisplayName = var1.func_96669_c();
         this.teamPrefix = var1.getColorPrefix();
         this.teamSuffix = var1.getColorSuffix();
         this.friendlyFire = var1.func_98299_i();
      }

      if (var2 == 0) {
         this.playerNames.addAll(var1.getMembershipCollection());
      }

   }

   public Packet209SetPlayerTeam(ScorePlayerTeam var1, Collection var2, int var3) {
      if (var3 != 3 && var3 != 4) {
         throw new IllegalArgumentException("Method must be join or leave for player constructor");
      } else if (var2 != null && !var2.isEmpty()) {
         this.mode = var3;
         this.teamName = var1.func_96661_b();
         this.playerNames.addAll(var2);
      } else {
         throw new IllegalArgumentException("Players cannot be null/empty");
      }
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.teamName = readString(var1, 16);
      this.mode = var1.readByte();
      if (this.mode == 0 || this.mode == 2) {
         this.teamDisplayName = readString(var1, 32);
         this.teamPrefix = readString(var1, 16);
         this.teamSuffix = readString(var1, 16);
         this.friendlyFire = var1.readByte();
      }

      if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
         short var2 = var1.readShort();

         for(int var3 = 0; var3 < var2; ++var3) {
            this.playerNames.add(readString(var1, 16));
         }
      }

   }

   public void writePacketData(DataOutput var1) throws IOException {
      writeString(this.teamName, var1);
      var1.writeByte(this.mode);
      if (this.mode == 0 || this.mode == 2) {
         writeString(this.teamDisplayName, var1);
         writeString(this.teamPrefix, var1);
         writeString(this.teamSuffix, var1);
         var1.writeByte(this.friendlyFire);
      }

      if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
         var1.writeShort(this.playerNames.size());
         Iterator var2 = this.playerNames.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            writeString(var3, var1);
         }
      }

   }

   public void processPacket(NetHandler var1) {
      var1.handleSetPlayerTeam(this);
   }

   public int getPacketSize() {
      return 3 + this.teamName.length();
   }
}
