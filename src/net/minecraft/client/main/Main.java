package net.minecraft.client.main;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet0KeepAlive;
import net.minecraft.network.packet.Packet13PlayerLookMove;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet254ServerPing;
import net.minecraft.network.packet.Packet255KickDisconnect;
import net.minecraft.network.packet.Packet2ClientProtocol;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet4UpdateTime;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.util.Session;
import net.minecraft.util.StringHelper;

public class Main {
   private static final String[] option_ds;
   private static final String[] option_packet52;
   private static final String[] option_ntf;
   private static final String[] option_drum2;
   private static final String[] option_ndp;
   public static boolean is_MITE_DS;
   public static boolean no_time_forwarding;
   public static boolean disable_render_update_method_2;
   public static boolean no_downtime_processing;
   public static final String MITE_DS_username = "Dedicated_Server";
   public static final Class[] packets_that_MITE_DS_client_player_can_send_or_receive;

   private static void printOption(String[] option) {
      System.out.println("OPTION SPECIFIED: " + option[0] + " (" + option[1] + ")");
   }

   public static void main(String[] par0ArrayOfStr) {
      if (Minecraft.inDevMode()) {
         System.out.println("Command line arguments: " + StringHelper.implode(par0ArrayOfStr, " "));
      }

      System.setProperty("java.net.preferIPv4Stack", "true");
      OptionParser var1 = new OptionParser();
      var1.allowsUnrecognizedOptions();
      var1.accepts("demo");
      var1.accepts("fullscreen");
      var1.accepts(option_ds[0]);
      var1.accepts(option_packet52[0]);
      var1.accepts(option_ntf[0]);
      var1.accepts(option_drum2[0]);
      var1.accepts(option_ndp[0]);
      ArgumentAcceptingOptionSpec var2 = var1.accepts("server").withRequiredArg();
      ArgumentAcceptingOptionSpec var3 = var1.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, new Integer[0]);
      ArgumentAcceptingOptionSpec var4 = var1.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
      ArgumentAcceptingOptionSpec var5 = var1.accepts("assetsDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var6 = var1.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var7 = var1.accepts("proxyHost").withRequiredArg();
      ArgumentAcceptingOptionSpec var8 = var1.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
      ArgumentAcceptingOptionSpec var9 = var1.accepts("proxyUser").withRequiredArg();
      ArgumentAcceptingOptionSpec var10 = var1.accepts("proxyPass").withRequiredArg();
      ArgumentAcceptingOptionSpec var11 = var1.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L, new String[0]);
      ArgumentAcceptingOptionSpec var12 = var1.accepts("session").withRequiredArg();
      ArgumentAcceptingOptionSpec var13 = var1.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var14 = var1.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      ArgumentAcceptingOptionSpec var15 = var1.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      ArgumentAcceptingOptionSpec access_token_arg = var1.accepts("accessToken").withRequiredArg();
      ArgumentAcceptingOptionSpec uuid_arg = var1.accepts("uuid").withRequiredArg();
      NonOptionArgumentSpec var16 = var1.nonOptions();
      OptionSet var17 = var1.parse(par0ArrayOfStr);
      List var18 = var17.valuesOf(var16);
      String var19 = (String)var17.valueOf(var7);
      Proxy var20 = Proxy.NO_PROXY;
      if (var19 != null) {
         try {
            var20 = new Proxy(Type.SOCKS, new InetSocketAddress(var19, (Integer)var17.valueOf(var8)));
         } catch (Exception var37) {
         }
      }

      String var21 = (String)var17.valueOf(var9);
      String var22 = (String)var17.valueOf(var10);
      if (!var20.equals(Proxy.NO_PROXY) && func_110121_a(var21) && func_110121_a(var22)) {
         Authenticator.setDefault(new MainProxyAuthenticator(var21, var22));
      }

      int var23 = (Integer)var17.valueOf(var14);
      int var24 = (Integer)var17.valueOf(var15);
      boolean var25 = var17.has("fullscreen");
      boolean var26 = var17.has("demo");
      String var27 = (String)var17.valueOf(var13);
      File var28 = (File)var17.valueOf(var4);
      File var29 = var17.has(var5) ? (File)var17.valueOf(var5) : new File(var28, "assets/");
      File var30 = var17.has(var6) ? (File)var17.valueOf(var6) : new File(var28, "resourcepacks/");
      String session = (String)var12.value(var17);
      if (session == null || !session.equals("-") && !session.contains("token:")) {
         if (Minecraft.inDevMode()) {
            System.out.println("Missing argument: session");
         }

         String access_token = (String)access_token_arg.value(var17);
         String uuid = (String)uuid_arg.value(var17);
         if (access_token != null && !access_token.isEmpty() && uuid != null && !uuid.isEmpty()) {
            session = "token:" + access_token + ":" + uuid;
         }

         if (Minecraft.inDevMode()) {
            System.out.println("Attempting to construct \"session\" argument. Result: session=" + (session == null ? session : "\"" + session + "\""));
         }
      }

      if (is_MITE_DS = var17.has(option_ds[0])) {
         printOption(option_ds);
      }

      if (no_time_forwarding = var17.has(option_ntf[0])) {
         printOption(option_ntf);
      }

      if (disable_render_update_method_2 = var17.has(option_drum2[0])) {
         printOption(option_drum2);
      }

      if (no_downtime_processing = var17.has(option_ndp[0])) {
         printOption(option_ndp);
      }

      TextureManager.preloadTextures();
      Session var31;
      if (is_MITE_DS) {
         var31 = new Session("Dedicated_Server", session);
         var25 = false;
      } else {
         var31 = new Session((String)var11.value(var17), session);
      }

      Minecraft var32 = new Minecraft(var31, var23, var24, var25, var26, var28, var29, var30, var20, var27);
      String var33 = (String)var17.valueOf(var2);
      if (var33 != null) {
         var32.setServer(var33, (Integer)var17.valueOf(var3));
      }

      Runtime.getRuntime().addShutdownHook(new MainShutdownHook());
      if (!var18.isEmpty()) {
         System.out.println("Completely ignored arguments: " + var18);
      }

      Thread.currentThread().setName("Minecraft main thread");
      Minecraft.client_thread = Thread.currentThread();
      var32.run();
   }

   private static boolean func_110121_a(String par0Str) {
      return par0Str != null && !par0Str.isEmpty();
   }

   public static boolean isPacketThatMITEDSClientPlayerCanSendOrReceive(Packet packet) {
      Class packet_class = packet.getClass();

      for(int i = 0; i < packets_that_MITE_DS_client_player_can_send_or_receive.length; ++i) {
         if (packets_that_MITE_DS_client_player_can_send_or_receive[i] == packet_class) {
            return true;
         }
      }

      return false;
   }

   public static boolean isPacketIgnored(NetHandler net_handler, Packet packet) {
      if (net_handler instanceof NetClientHandler) {
         if (is_MITE_DS) {
            return !isPacketThatMITEDSClientPlayerCanSendOrReceive(packet);
         }
      } else if (net_handler instanceof NetServerHandler) {
         if (((NetServerHandler)net_handler).playerEntity.isGhost()) {
            return !isPacketThatMITEDSClientPlayerCanSendOrReceive(packet);
         }
      } else if (net_handler instanceof NetLoginHandler) {
      }

      return false;
   }

   static {
      TextureManager.unloadTextures();
      option_ds = new String[]{"ds", "MITE Dedicated Server mode enabled"};
      option_packet52 = new String[]{"packet52", "regular Packet52 chunk updating will be used"};
      option_ntf = new String[]{"ntf", "no time forwarding"};
      option_drum2 = new String[]{"drum2", "renderUpdateMethod2 disabled"};
      option_ndp = new String[]{"ndp", "no downtime processing"};
      packets_that_MITE_DS_client_player_can_send_or_receive = new Class[]{Packet0KeepAlive.class, Packet1Login.class, Packet2ClientProtocol.class, Packet3Chat.class, Packet4UpdateTime.class, Packet13PlayerLookMove.class, Packet85SimpleSignal.class, Packet254ServerPing.class, Packet255KickDisconnect.class};
      Entity.resetEntityIds();
   }
}
