package net.minecraft.scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public class ServerCommandScoreboard extends CommandBase {
   public String getCommandName() {
      return "scoreboard";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.scoreboard.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      if (var2.length >= 1) {
         if (var2[0].equalsIgnoreCase("objectives")) {
            if (var2.length == 1) {
               throw new WrongUsageException("commands.scoreboard.objectives.usage", new Object[0]);
            }

            if (var2[1].equalsIgnoreCase("list")) {
               this.getObjectivesList(var1);
            } else if (var2[1].equalsIgnoreCase("add")) {
               if (var2.length < 4) {
                  throw new WrongUsageException("commands.scoreboard.objectives.add.usage", new Object[0]);
               }

               this.addObjective(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("remove")) {
               if (var2.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.objectives.remove.usage", new Object[0]);
               }

               this.removeObjective(var1, var2[2]);
            } else {
               if (!var2[1].equalsIgnoreCase("setdisplay")) {
                  throw new WrongUsageException("commands.scoreboard.objectives.usage", new Object[0]);
               }

               if (var2.length != 3 && var2.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.objectives.setdisplay.usage", new Object[0]);
               }

               this.setObjectivesDisplay(var1, var2, 2);
            }

            return;
         }

         if (var2[0].equalsIgnoreCase("players")) {
            if (var2.length == 1) {
               throw new WrongUsageException("commands.scoreboard.players.usage", new Object[0]);
            }

            if (var2[1].equalsIgnoreCase("list")) {
               if (var2.length > 3) {
                  throw new WrongUsageException("commands.scoreboard.players.list.usage", new Object[0]);
               }

               this.listPlayers(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("add")) {
               if (var2.length != 5) {
                  throw new WrongUsageException("commands.scoreboard.players.add.usage", new Object[0]);
               }

               this.setPlayerScore(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("remove")) {
               if (var2.length != 5) {
                  throw new WrongUsageException("commands.scoreboard.players.remove.usage", new Object[0]);
               }

               this.setPlayerScore(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("set")) {
               if (var2.length != 5) {
                  throw new WrongUsageException("commands.scoreboard.players.set.usage", new Object[0]);
               }

               this.setPlayerScore(var1, var2, 2);
            } else {
               if (!var2[1].equalsIgnoreCase("reset")) {
                  throw new WrongUsageException("commands.scoreboard.players.usage", new Object[0]);
               }

               if (var2.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.players.reset.usage", new Object[0]);
               }

               this.resetPlayerScore(var1, var2, 2);
            }

            return;
         }

         if (var2[0].equalsIgnoreCase("teams")) {
            if (var2.length == 1) {
               throw new WrongUsageException("commands.scoreboard.teams.usage", new Object[0]);
            }

            if (var2[1].equalsIgnoreCase("list")) {
               if (var2.length > 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.list.usage", new Object[0]);
               }

               this.getTeamList(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("add")) {
               if (var2.length < 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.add.usage", new Object[0]);
               }

               this.addTeam(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("remove")) {
               if (var2.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.remove.usage", new Object[0]);
               }

               this.removeTeam(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("empty")) {
               if (var2.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.empty.usage", new Object[0]);
               }

               this.emptyTeam(var1, var2, 2);
            } else if (!var2[1].equalsIgnoreCase("join")) {
               if (var2[1].equalsIgnoreCase("leave")) {
                  if (var2.length < 3 && !(var1 instanceof EntityPlayer)) {
                     throw new WrongUsageException("commands.scoreboard.teams.leave.usage", new Object[0]);
                  }

                  this.leaveTeam(var1, var2, 2);
               } else {
                  if (!var2[1].equalsIgnoreCase("option")) {
                     throw new WrongUsageException("commands.scoreboard.teams.usage", new Object[0]);
                  }

                  if (var2.length != 4 && var2.length != 5) {
                     throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
                  }

                  this.setTeamOption(var1, var2, 2);
               }
            } else {
               if (var2.length < 4 && (var2.length != 3 || !(var1 instanceof EntityPlayer))) {
                  throw new WrongUsageException("commands.scoreboard.teams.join.usage", new Object[0]);
               }

               this.joinTeam(var1, var2, 2);
            }

            return;
         }
      }

      throw new WrongUsageException("commands.scoreboard.usage", new Object[0]);
   }

   protected Scoreboard getScoreboardFromWorldServer() {
      return MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
   }

   protected ScoreObjective getScoreObjective(String var1, boolean var2) {
      Scoreboard var3 = this.getScoreboardFromWorldServer();
      ScoreObjective var4 = var3.getObjective(var1);
      if (var4 == null) {
         throw new CommandException("commands.scoreboard.objectiveNotFound", new Object[]{var1});
      } else if (var2 && var4.getCriteria().isReadOnly()) {
         throw new CommandException("commands.scoreboard.objectiveReadOnly", new Object[]{var1});
      } else {
         return var4;
      }
   }

   protected ScorePlayerTeam getTeam(String var1) {
      Scoreboard var2 = this.getScoreboardFromWorldServer();
      ScorePlayerTeam var3 = var2.func_96508_e(var1);
      if (var3 == null) {
         throw new CommandException("commands.scoreboard.teamNotFound", new Object[]{var1});
      } else {
         return var3;
      }
   }

   protected void addObjective(ICommandSender var1, String[] var2, int var3) {
      String var4 = var2[var3++];
      String var5 = var2[var3++];
      Scoreboard var6 = this.getScoreboardFromWorldServer();
      ScoreObjectiveCriteria var7 = (ScoreObjectiveCriteria)ScoreObjectiveCriteria.field_96643_a.get(var5);
      if (var7 == null) {
         String[] var9 = (String[])ScoreObjectiveCriteria.field_96643_a.keySet().toArray(new String[0]);
         throw new WrongUsageException("commands.scoreboard.objectives.add.wrongType", new Object[]{joinNiceString(var9)});
      } else if (var6.getObjective(var4) != null) {
         throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", new Object[]{var4});
      } else if (var4.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.objectives.add.tooLong", new Object[]{var4, 16});
      } else if (var4.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.objectives.add.usage", new Object[0]);
      } else {
         if (var2.length > var3) {
            String var8 = func_82360_a(var1, var2, var3);
            if (var8.length() > 32) {
               throw new SyntaxErrorException("commands.scoreboard.objectives.add.displayTooLong", new Object[]{var8, 32});
            }

            if (var8.length() > 0) {
               var6.func_96535_a(var4, var7).setDisplayName(var8);
            } else {
               var6.func_96535_a(var4, var7);
            }
         } else {
            var6.func_96535_a(var4, var7);
         }

         notifyAdmins(var1, "commands.scoreboard.objectives.add.success", new Object[]{var4});
      }
   }

   protected void addTeam(ICommandSender var1, String[] var2, int var3) {
      String var4 = var2[var3++];
      Scoreboard var5 = this.getScoreboardFromWorldServer();
      if (var5.func_96508_e(var4) != null) {
         throw new CommandException("commands.scoreboard.teams.add.alreadyExists", new Object[]{var4});
      } else if (var4.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.teams.add.tooLong", new Object[]{var4, 16});
      } else if (var4.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.teams.add.usage", new Object[0]);
      } else {
         if (var2.length > var3) {
            String var6 = func_82360_a(var1, var2, var3);
            if (var6.length() > 32) {
               throw new SyntaxErrorException("commands.scoreboard.teams.add.displayTooLong", new Object[]{var6, 32});
            }

            if (var6.length() > 0) {
               var5.createTeam(var4).setTeamName(var6);
            } else {
               var5.createTeam(var4);
            }
         } else {
            var5.createTeam(var4);
         }

         notifyAdmins(var1, "commands.scoreboard.teams.add.success", new Object[]{var4});
      }
   }

   protected void setTeamOption(ICommandSender var1, String[] var2, int var3) {
      ScorePlayerTeam var4 = this.getTeam(var2[var3++]);
      String var5 = var2[var3++].toLowerCase();
      if (!var5.equalsIgnoreCase("color") && !var5.equalsIgnoreCase("friendlyfire") && !var5.equalsIgnoreCase("seeFriendlyInvisibles")) {
         throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
      } else if (var2.length == 4) {
         if (var5.equalsIgnoreCase("color")) {
            throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{var5, func_96333_a(EnumChatFormatting.func_96296_a(true, false))});
         } else if (!var5.equalsIgnoreCase("friendlyfire") && !var5.equalsIgnoreCase("seeFriendlyInvisibles")) {
            throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
         } else {
            throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{var5, func_96333_a(Arrays.asList("true", "false"))});
         }
      } else {
         String var6 = var2[var3++];
         if (var5.equalsIgnoreCase("color")) {
            EnumChatFormatting var7 = EnumChatFormatting.func_96300_b(var6);
            if (var6 == null) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{var5, func_96333_a(EnumChatFormatting.func_96296_a(true, false))});
            }

            var4.setNamePrefix(var7.toString());
            var4.setNameSuffix(EnumChatFormatting.RESET.toString());
         } else if (var5.equalsIgnoreCase("friendlyfire")) {
            if (!var6.equalsIgnoreCase("true") && !var6.equalsIgnoreCase("false")) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{var5, func_96333_a(Arrays.asList("true", "false"))});
            }

            var4.setAllowFriendlyFire(var6.equalsIgnoreCase("true"));
         } else if (var5.equalsIgnoreCase("seeFriendlyInvisibles")) {
            if (!var6.equalsIgnoreCase("true") && !var6.equalsIgnoreCase("false")) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{var5, func_96333_a(Arrays.asList("true", "false"))});
            }

            var4.setSeeFriendlyInvisiblesEnabled(var6.equalsIgnoreCase("true"));
         }

         notifyAdmins(var1, "commands.scoreboard.teams.option.success", new Object[]{var5, var4.func_96661_b(), var6});
      }
   }

   protected void removeTeam(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.getScoreboardFromWorldServer();
      ScorePlayerTeam var5 = this.getTeam(var2[var3++]);
      var4.func_96511_d(var5);
      notifyAdmins(var1, "commands.scoreboard.teams.remove.success", new Object[]{var5.func_96661_b()});
   }

   protected void getTeamList(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.getScoreboardFromWorldServer();
      if (var2.length > var3) {
         ScorePlayerTeam var5 = this.getTeam(var2[var3++]);
         Collection var6 = var5.getMembershipCollection();
         if (var6.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.player.empty", new Object[]{var5.func_96661_b()});
         }

         var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.scoreboard.teams.list.player.count", var6.size(), var5.func_96661_b()).setColor(EnumChatFormatting.DARK_GREEN));
         var1.sendChatToPlayer(ChatMessageComponent.createFromText(joinNiceString(var6.toArray())));
      } else {
         Collection var8 = var4.func_96525_g();
         if (var8.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.empty", new Object[0]);
         }

         var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.scoreboard.teams.list.count", var8.size()).setColor(EnumChatFormatting.DARK_GREEN));
         Iterator var9 = var8.iterator();

         while(var9.hasNext()) {
            ScorePlayerTeam var7 = (ScorePlayerTeam)var9.next();
            var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.scoreboard.teams.list.entry", var7.func_96661_b(), var7.func_96669_c(), var7.getMembershipCollection().size()));
         }
      }

   }

   protected void joinTeam(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.getScoreboardFromWorldServer();
      ScorePlayerTeam var5 = var4.func_96508_e(var2[var3++]);
      HashSet var6 = new HashSet();
      String var7;
      if (var1 instanceof EntityPlayer && var3 == var2.length) {
         var7 = getCommandSenderAsPlayer(var1).getEntityName();
         var4.addPlayerToTeam(var7, var5);
         var6.add(var7);
      } else {
         while(var3 < var2.length) {
            var7 = func_96332_d(var1, var2[var3++]);
            var4.addPlayerToTeam(var7, var5);
            var6.add(var7);
         }
      }

      if (!var6.isEmpty()) {
         notifyAdmins(var1, "commands.scoreboard.teams.join.success", new Object[]{var6.size(), var5.func_96661_b(), joinNiceString(var6.toArray(new String[0]))});
      }

   }

   protected void leaveTeam(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.getScoreboardFromWorldServer();
      HashSet var5 = new HashSet();
      HashSet var6 = new HashSet();
      String var7;
      if (var1 instanceof EntityPlayer && var3 == var2.length) {
         var7 = getCommandSenderAsPlayer(var1).getEntityName();
         if (var4.removePlayerFromTeams(var7)) {
            var5.add(var7);
         } else {
            var6.add(var7);
         }
      } else {
         while(var3 < var2.length) {
            var7 = func_96332_d(var1, var2[var3++]);
            if (var4.removePlayerFromTeams(var7)) {
               var5.add(var7);
            } else {
               var6.add(var7);
            }
         }
      }

      if (!var5.isEmpty()) {
         notifyAdmins(var1, "commands.scoreboard.teams.leave.success", new Object[]{var5.size(), joinNiceString(var5.toArray(new String[0]))});
      }

      if (!var6.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.leave.failure", new Object[]{var6.size(), joinNiceString(var6.toArray(new String[0]))});
      }
   }

   protected void emptyTeam(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.getScoreboardFromWorldServer();
      ScorePlayerTeam var5 = this.getTeam(var2[var3++]);
      ArrayList var6 = new ArrayList(var5.getMembershipCollection());
      if (var6.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", new Object[]{var5.func_96661_b()});
      } else {
         Iterator var7 = var6.iterator();

         while(var7.hasNext()) {
            String var8 = (String)var7.next();
            var4.removePlayerFromTeam(var8, var5);
         }

         notifyAdmins(var1, "commands.scoreboard.teams.empty.success", new Object[]{var6.size(), var5.func_96661_b()});
      }
   }

   protected void removeObjective(ICommandSender var1, String var2) {
      Scoreboard var3 = this.getScoreboardFromWorldServer();
      ScoreObjective var4 = this.getScoreObjective(var2, false);
      var3.func_96519_k(var4);
      notifyAdmins(var1, "commands.scoreboard.objectives.remove.success", new Object[]{var2});
   }

   protected void getObjectivesList(ICommandSender var1) {
      Scoreboard var2 = this.getScoreboardFromWorldServer();
      Collection var3 = var2.getScoreObjectives();
      if (var3.size() <= 0) {
         throw new CommandException("commands.scoreboard.objectives.list.empty", new Object[0]);
      } else {
         var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.scoreboard.objectives.list.count", var3.size()).setColor(EnumChatFormatting.DARK_GREEN));
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            ScoreObjective var5 = (ScoreObjective)var4.next();
            var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.scoreboard.objectives.list.entry", var5.getName(), var5.getDisplayName(), var5.getCriteria().func_96636_a()));
         }

      }
   }

   protected void setObjectivesDisplay(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.getScoreboardFromWorldServer();
      String var5 = var2[var3++];
      int var6 = Scoreboard.getObjectiveDisplaySlotNumber(var5);
      ScoreObjective var7 = null;
      if (var2.length == 4) {
         var7 = this.getScoreObjective(var2[var3++], false);
      }

      if (var6 < 0) {
         throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", new Object[]{var5});
      } else {
         var4.func_96530_a(var6, var7);
         if (var7 != null) {
            notifyAdmins(var1, "commands.scoreboard.objectives.setdisplay.successSet", new Object[]{Scoreboard.getObjectiveDisplaySlot(var6), var7.getName()});
         } else {
            notifyAdmins(var1, "commands.scoreboard.objectives.setdisplay.successCleared", new Object[]{Scoreboard.getObjectiveDisplaySlot(var6)});
         }

      }
   }

   protected void listPlayers(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.getScoreboardFromWorldServer();
      if (var2.length > var3) {
         String var5 = func_96332_d(var1, var2[var3++]);
         Map var6 = var4.func_96510_d(var5);
         if (var6.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.player.empty", new Object[]{var5});
         }

         var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.scoreboard.players.list.player.count", var6.size(), var5).setColor(EnumChatFormatting.DARK_GREEN));
         Iterator var7 = var6.values().iterator();

         while(var7.hasNext()) {
            Score var8 = (Score)var7.next();
            var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.scoreboard.players.list.player.entry", var8.getScorePoints(), var8.func_96645_d().getDisplayName(), var8.func_96645_d().getName()));
         }
      } else {
         Collection var9 = var4.getObjectiveNames();
         if (var9.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.empty", new Object[0]);
         }

         var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.scoreboard.players.list.count", var9.size()).setColor(EnumChatFormatting.DARK_GREEN));
         var1.sendChatToPlayer(ChatMessageComponent.createFromText(joinNiceString(var9.toArray())));
      }

   }

   protected void setPlayerScore(ICommandSender var1, String[] var2, int var3) {
      String var4 = var2[var3 - 1];
      String var5 = func_96332_d(var1, var2[var3++]);
      ScoreObjective var6 = this.getScoreObjective(var2[var3++], true);
      int var7 = var4.equalsIgnoreCase("set") ? parseInt(var1, var2[var3++]) : parseIntWithMin(var1, var2[var3++], 1);
      Scoreboard var8 = this.getScoreboardFromWorldServer();
      Score var9 = var8.func_96529_a(var5, var6);
      if (var4.equalsIgnoreCase("set")) {
         var9.func_96647_c(var7);
      } else if (var4.equalsIgnoreCase("add")) {
         var9.func_96649_a(var7);
      } else {
         var9.func_96646_b(var7);
      }

      notifyAdmins(var1, "commands.scoreboard.players.set.success", new Object[]{var6.getName(), var5, var9.getScorePoints()});
   }

   protected void resetPlayerScore(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.getScoreboardFromWorldServer();
      String var5 = func_96332_d(var1, var2[var3++]);
      var4.func_96515_c(var5);
      notifyAdmins(var1, "commands.scoreboard.players.reset.success", new Object[]{var5});
   }

   public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
      if (var2.length == 1) {
         return getListOfStringsMatchingLastWord(var2, new String[]{"objectives", "players", "teams"});
      } else {
         if (var2[0].equalsIgnoreCase("objectives")) {
            if (var2.length == 2) {
               return getListOfStringsMatchingLastWord(var2, new String[]{"list", "add", "remove", "setdisplay"});
            }

            if (var2[1].equalsIgnoreCase("add")) {
               if (var2.length == 4) {
                  return getListOfStringsFromIterableMatchingLastWord(var2, ScoreObjectiveCriteria.field_96643_a.keySet());
               }
            } else if (var2[1].equalsIgnoreCase("remove")) {
               if (var2.length == 3) {
                  return getListOfStringsFromIterableMatchingLastWord(var2, this.getScoreObjectivesList(false));
               }
            } else if (var2[1].equalsIgnoreCase("setdisplay")) {
               if (var2.length == 3) {
                  return getListOfStringsMatchingLastWord(var2, new String[]{"list", "sidebar", "belowName"});
               }

               if (var2.length == 4) {
                  return getListOfStringsFromIterableMatchingLastWord(var2, this.getScoreObjectivesList(false));
               }
            }
         } else if (var2[0].equalsIgnoreCase("players")) {
            if (var2.length == 2) {
               return getListOfStringsMatchingLastWord(var2, new String[]{"set", "add", "remove", "reset", "list"});
            }

            if (!var2[1].equalsIgnoreCase("set") && !var2[1].equalsIgnoreCase("add") && !var2[1].equalsIgnoreCase("remove")) {
               if ((var2[1].equalsIgnoreCase("reset") || var2[1].equalsIgnoreCase("list")) && var2.length == 3) {
                  return getListOfStringsFromIterableMatchingLastWord(var2, this.getScoreboardFromWorldServer().getObjectiveNames());
               }
            } else {
               if (var2.length == 3) {
                  return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
               }

               if (var2.length == 4) {
                  return getListOfStringsFromIterableMatchingLastWord(var2, this.getScoreObjectivesList(true));
               }
            }
         } else if (var2[0].equalsIgnoreCase("teams")) {
            if (var2.length == 2) {
               return getListOfStringsMatchingLastWord(var2, new String[]{"add", "remove", "join", "leave", "empty", "list", "option"});
            }

            if (var2[1].equalsIgnoreCase("join")) {
               if (var2.length == 3) {
                  return getListOfStringsFromIterableMatchingLastWord(var2, this.getScoreboardFromWorldServer().func_96531_f());
               }

               if (var2.length >= 4) {
                  return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
               }
            } else {
               if (var2[1].equalsIgnoreCase("leave")) {
                  return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
               }

               if (!var2[1].equalsIgnoreCase("empty") && !var2[1].equalsIgnoreCase("list") && !var2[1].equalsIgnoreCase("remove")) {
                  if (var2[1].equalsIgnoreCase("option")) {
                     if (var2.length == 3) {
                        return getListOfStringsFromIterableMatchingLastWord(var2, this.getScoreboardFromWorldServer().func_96531_f());
                     }

                     if (var2.length == 4) {
                        return getListOfStringsMatchingLastWord(var2, new String[]{"color", "friendlyfire", "seeFriendlyInvisibles"});
                     }

                     if (var2.length == 5) {
                        if (var2[3].equalsIgnoreCase("color")) {
                           return getListOfStringsFromIterableMatchingLastWord(var2, EnumChatFormatting.func_96296_a(true, false));
                        }

                        if (var2[3].equalsIgnoreCase("friendlyfire") || var2[3].equalsIgnoreCase("seeFriendlyInvisibles")) {
                           return getListOfStringsMatchingLastWord(var2, new String[]{"true", "false"});
                        }
                     }
                  }
               } else if (var2.length == 3) {
                  return getListOfStringsFromIterableMatchingLastWord(var2, this.getScoreboardFromWorldServer().func_96531_f());
               }
            }
         }

         return null;
      }
   }

   protected List getScoreObjectivesList(boolean var1) {
      Collection var2 = this.getScoreboardFromWorldServer().getScoreObjectives();
      ArrayList var3 = new ArrayList();
      Iterator var4 = var2.iterator();

      while(true) {
         ScoreObjective var5;
         do {
            if (!var4.hasNext()) {
               return var3;
            }

            var5 = (ScoreObjective)var4.next();
         } while(var1 && var5.getCriteria().isReadOnly());

         var3.add(var5.getName());
      }
   }

   public boolean isUsernameIndex(String[] var1, int var2) {
      if (var1[0].equalsIgnoreCase("players")) {
         return var2 == 2;
      } else if (!var1[0].equalsIgnoreCase("teams")) {
         return false;
      } else {
         return var2 == 2 || var2 == 3;
      }
   }
}
