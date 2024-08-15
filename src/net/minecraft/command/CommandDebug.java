package net.minecraft.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.profiler.ProfilerResult;
import net.minecraft.server.MinecraftServer;

public class CommandDebug extends CommandBase {
   private long startTime;
   private int startTicks;

   public String getCommandName() {
      return "debug";
   }

   public int getRequiredPermissionLevel() {
      return 3;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.debug.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      if (var2.length == 1) {
         if (var2[0].equals("start")) {
            notifyAdmins(var1, "commands.debug.start", new Object[0]);
            MinecraftServer.getServer().enableProfiling();
            this.startTime = MinecraftServer.getSystemTimeMillis();
            this.startTicks = MinecraftServer.getServer().getTickCounter();
            return;
         }

         if (var2[0].equals("stop")) {
            if (!MinecraftServer.getServer().theProfiler.profilingEnabled) {
               throw new CommandException("commands.debug.notStarted", new Object[0]);
            }

            long var3 = MinecraftServer.getSystemTimeMillis();
            int var5 = MinecraftServer.getServer().getTickCounter();
            long var6 = var3 - this.startTime;
            int var8 = var5 - this.startTicks;
            this.saveProfilerResults(var6, var8);
            MinecraftServer.getServer().theProfiler.profilingEnabled = false;
            notifyAdmins(var1, "commands.debug.stop", new Object[]{(float)var6 / 1000.0F, var8});
            return;
         }
      }

      throw new WrongUsageException("commands.debug.usage", new Object[0]);
   }

   private void saveProfilerResults(long var1, int var3) {
      File var4 = new File(MinecraftServer.getServer().getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
      var4.getParentFile().mkdirs();

      try {
         FileWriter var5 = new FileWriter(var4);
         var5.write(this.getProfilerResults(var1, var3));
         var5.close();
      } catch (Throwable var6) {
         MinecraftServer.getServer().getLogAgent().logSevereException("Could not save profiler results to " + var4, var6);
      }

   }

   private String getProfilerResults(long var1, int var3) {
      StringBuilder var4 = new StringBuilder();
      var4.append("---- Minecraft Profiler Results ----\n");
      var4.append("// ");
      var4.append(getWittyComment());
      var4.append("\n\n");
      var4.append("Time span: ").append(var1).append(" ms\n");
      var4.append("Tick span: ").append(var3).append(" ticks\n");
      var4.append("// This is approximately ").append(String.format("%.2f", (float)var3 / ((float)var1 / 1000.0F))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
      var4.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.getProfileDump(0, "root", var4);
      var4.append("--- END PROFILE DUMP ---\n\n");
      return var4.toString();
   }

   private void getProfileDump(int var1, String var2, StringBuilder var3) {
      List var4 = MinecraftServer.getServer().theProfiler.getProfilingData(var2);
      if (var4 != null && var4.size() >= 3) {
         for(int var5 = 1; var5 < var4.size(); ++var5) {
            ProfilerResult var6 = (ProfilerResult)var4.get(var5);
            var3.append(String.format("[%02d] ", var1));

            for(int var7 = 0; var7 < var1; ++var7) {
               var3.append(" ");
            }

            var3.append(var6.field_76331_c);
            var3.append(" - ");
            var3.append(String.format("%.2f", var6.field_76332_a));
            var3.append("%/");
            var3.append(String.format("%.2f", var6.field_76330_b));
            var3.append("%\n");
            if (!var6.field_76331_c.equals("unspecified")) {
               try {
                  this.getProfileDump(var1 + 1, var2 + "." + var6.field_76331_c, var3);
               } catch (Exception var8) {
                  var3.append("[[ EXCEPTION " + var8 + " ]]");
               }
            }
         }

      }
   }

   private static String getWittyComment() {
      String[] var0 = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

      try {
         return var0[(int)(System.nanoTime() % (long)var0.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, new String[]{"start", "stop"}) : null;
   }
}
