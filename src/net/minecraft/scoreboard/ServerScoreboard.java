package net.minecraft.scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet206SetObjective;
import net.minecraft.network.packet.Packet207SetScore;
import net.minecraft.network.packet.Packet208SetDisplayObjective;
import net.minecraft.network.packet.Packet209SetPlayerTeam;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard {
   private final MinecraftServer field_96555_a;
   private final Set field_96553_b = new HashSet();
   private ScoreboardSaveData field_96554_c;

   public ServerScoreboard(MinecraftServer var1) {
      this.field_96555_a = var1;
   }

   public void func_96536_a(Score var1) {
      super.func_96536_a(var1);
      if (this.field_96553_b.contains(var1.func_96645_d())) {
         this.field_96555_a.getConfigurationManager().sendPacketToAllPlayers(new Packet207SetScore(var1, 0));
      }

      this.func_96551_b();
   }

   public void func_96516_a(String var1) {
      super.func_96516_a(var1);
      this.field_96555_a.getConfigurationManager().sendPacketToAllPlayers(new Packet207SetScore(var1));
      this.func_96551_b();
   }

   public void func_96530_a(int var1, ScoreObjective var2) {
      ScoreObjective var3 = this.func_96539_a(var1);
      super.func_96530_a(var1, var2);
      if (var3 != var2 && var3 != null) {
         if (this.func_96552_h(var3) > 0) {
            this.field_96555_a.getConfigurationManager().sendPacketToAllPlayers(new Packet208SetDisplayObjective(var1, var2));
         } else {
            this.func_96546_g(var3);
         }
      }

      if (var2 != null) {
         if (this.field_96553_b.contains(var2)) {
            this.field_96555_a.getConfigurationManager().sendPacketToAllPlayers(new Packet208SetDisplayObjective(var1, var2));
         } else {
            this.func_96549_e(var2);
         }
      }

      this.func_96551_b();
   }

   public void addPlayerToTeam(String var1, ScorePlayerTeam var2) {
      super.addPlayerToTeam(var1, var2);
      this.field_96555_a.getConfigurationManager().sendPacketToAllPlayers(new Packet209SetPlayerTeam(var2, Arrays.asList(var1), 3));
      this.func_96551_b();
   }

   public void removePlayerFromTeam(String var1, ScorePlayerTeam var2) {
      super.removePlayerFromTeam(var1, var2);
      this.field_96555_a.getConfigurationManager().sendPacketToAllPlayers(new Packet209SetPlayerTeam(var2, Arrays.asList(var1), 4));
      this.func_96551_b();
   }

   public void func_96522_a(ScoreObjective var1) {
      super.func_96522_a(var1);
      this.func_96551_b();
   }

   public void func_96532_b(ScoreObjective var1) {
      super.func_96532_b(var1);
      if (this.field_96553_b.contains(var1)) {
         this.field_96555_a.getConfigurationManager().sendPacketToAllPlayers(new Packet206SetObjective(var1, 2));
      }

      this.func_96551_b();
   }

   public void func_96533_c(ScoreObjective var1) {
      super.func_96533_c(var1);
      if (this.field_96553_b.contains(var1)) {
         this.func_96546_g(var1);
      }

      this.func_96551_b();
   }

   public void func_96523_a(ScorePlayerTeam var1) {
      super.func_96523_a(var1);
      this.field_96555_a.getConfigurationManager().sendPacketToAllPlayers(new Packet209SetPlayerTeam(var1, 0));
      this.func_96551_b();
   }

   public void func_96538_b(ScorePlayerTeam var1) {
      super.func_96538_b(var1);
      this.field_96555_a.getConfigurationManager().sendPacketToAllPlayers(new Packet209SetPlayerTeam(var1, 2));
      this.func_96551_b();
   }

   public void func_96513_c(ScorePlayerTeam var1) {
      super.func_96513_c(var1);
      this.field_96555_a.getConfigurationManager().sendPacketToAllPlayers(new Packet209SetPlayerTeam(var1, 1));
      this.func_96551_b();
   }

   public void func_96547_a(ScoreboardSaveData var1) {
      this.field_96554_c = var1;
   }

   protected void func_96551_b() {
      if (this.field_96554_c != null) {
         this.field_96554_c.markDirty();
      }

   }

   public List func_96550_d(ScoreObjective var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new Packet206SetObjective(var1, 0));

      for(int var3 = 0; var3 < 3; ++var3) {
         if (this.func_96539_a(var3) == var1) {
            var2.add(new Packet208SetDisplayObjective(var3, var1));
         }
      }

      Iterator var5 = this.func_96534_i(var1).iterator();

      while(var5.hasNext()) {
         Score var4 = (Score)var5.next();
         var2.add(new Packet207SetScore(var4, 0));
      }

      return var2;
   }

   public void func_96549_e(ScoreObjective var1) {
      List var2 = this.func_96550_d(var1);
      Iterator var3 = this.field_96555_a.getConfigurationManager().playerEntityList.iterator();

      while(var3.hasNext()) {
         EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
         Iterator var5 = var2.iterator();

         while(var5.hasNext()) {
            Packet var6 = (Packet)var5.next();
            var4.playerNetServerHandler.sendPacketToPlayer(var6);
         }
      }

      this.field_96553_b.add(var1);
   }

   public List func_96548_f(ScoreObjective var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new Packet206SetObjective(var1, 1));

      for(int var3 = 0; var3 < 3; ++var3) {
         if (this.func_96539_a(var3) == var1) {
            var2.add(new Packet208SetDisplayObjective(var3, var1));
         }
      }

      return var2;
   }

   public void func_96546_g(ScoreObjective var1) {
      List var2 = this.func_96548_f(var1);
      Iterator var3 = this.field_96555_a.getConfigurationManager().playerEntityList.iterator();

      while(var3.hasNext()) {
         EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
         Iterator var5 = var2.iterator();

         while(var5.hasNext()) {
            Packet var6 = (Packet)var5.next();
            var4.playerNetServerHandler.sendPacketToPlayer(var6);
         }
      }

      this.field_96553_b.remove(var1);
   }

   public int func_96552_h(ScoreObjective var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < 3; ++var3) {
         if (this.func_96539_a(var3) == var1) {
            ++var2;
         }
      }

      return var2;
   }
}
