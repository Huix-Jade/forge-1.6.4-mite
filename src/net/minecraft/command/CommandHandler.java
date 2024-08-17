package net.minecraft.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cpw.mods.fml.common.network.Player;
import net.minecraft.block.BitHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivestock;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityStatsDump;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemReferencedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumCommand;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringHelper;
import net.minecraft.world.CaveNetworkGenerator;
import net.minecraft.world.CaveNetworkStub;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;

public class CommandHandler implements ICommandManager {
   private final Map commandMap = new HashMap();
   private final Set commandSet = new HashSet();
   private String[] privileged_users = new String[]{"Vuce", "Roninpawn", "ShadowKnight1234"};
   public static boolean spawning_disabled = false;

   private boolean isUserPrivileged(EntityPlayer player) {
      if (Minecraft.inDevMode()) {
         return true;
      } else if (player != null && player.username != null) {
         if (player.isZevimrgvInTournament()) {
            return true;
         } else {
            for(int i = 0; i < this.privileged_users.length; ++i) {
               if (player.username.equalsIgnoreCase(this.privileged_users[i])) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public int executeCommand(ICommandSender par1ICommandSender, String par2Str, boolean permission_override) {
      par2Str = par2Str.trim();
      if (par2Str.startsWith("/")) {
         par2Str = par2Str.substring(1);
      }

      MinecraftServer mc_server = MinecraftServer.getServer();
      WorldServer world = (WorldServer)par1ICommandSender.getEntityWorld();
      EntityPlayerMP player = (EntityPlayerMP)world.getPlayerEntityByName(par1ICommandSender.getCommandSenderName());
      EnumCommand command = EnumCommand.get(par2Str);
      if (command == EnumCommand.version) {
         par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("You are playing MITE 1.6.4 R196" + (Minecraft.inDevMode() ? EnumChatFormatting.RED + " DEV" : "")).setColor(EnumChatFormatting.YELLOW));
         return 1;
      } else {
         WorldInfo info;
         if (command == EnumCommand.versions) {
            info = par1ICommandSender.getEntityWorld().worldInfo;
            if (info.getEarliestMITEReleaseRunIn() == info.getLatestMITEReleaseRunIn()) {
               par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("This world has been played in MITE R" + info.getEarliestMITEReleaseRunIn() + " only").setColor(EnumChatFormatting.YELLOW));
            } else {
               par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("This world has been played in MITE releases R" + info.getEarliestMITEReleaseRunIn() + " to R" + info.getLatestMITEReleaseRunIn()).setColor(EnumChatFormatting.YELLOW));
            }

            return 1;
         } else if (command == EnumCommand.villages) {
            info = par1ICommandSender.getEntityWorld().worldInfo;
            if (info.getVillageConditions() < WorldInfo.getVillagePrerequisites()) {
               par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Villages can generate after the following conditions are met:").setColor(EnumChatFormatting.YELLOW));
               if (!BitHelper.isBitSet(info.getVillageConditions(), 16)) {
                  par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("- Craft an iron pickaxe or war hammer (or better)").setColor(EnumChatFormatting.YELLOW));
               }
            } else if (mc_server.worldServers[0].getDayOfWorld() < 60) {
               par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Villages can generate at day 60").setColor(EnumChatFormatting.YELLOW));
            } else {
               par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Villages can generate").setColor(EnumChatFormatting.YELLOW));
            }

            return 1;
         } else {
            int x;
            int dx;
            if (command == EnumCommand.tournament) {
               if (DedicatedServer.isTournament()) {
                  par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(DedicatedServer.getTournamentObjective()).setColor(EnumChatFormatting.YELLOW));
                  return 1;
               }
            } else {
               if (command == EnumCommand.day) {
                  par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("It is day " + mc_server.worldServers[0].getDayOfWorld() + " of this world").setColor(EnumChatFormatting.YELLOW));
                  return 1;
               }

               if (command == EnumCommand.mem) {
                  par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("The server is using " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L + "MB of memory (" + Runtime.getRuntime().totalMemory() / 1024L / 1024L + "MB is allocated)").setColor(EnumChatFormatting.YELLOW));
                  return 1;
               }

               int i1;
               if (command == EnumCommand.load) {
                  i1 = (int)(mc_server.getLoadOnServer() * 100.0F);
                  if (i1 < 0) {
                     par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("The load on the server is still being calculated").setColor(EnumChatFormatting.YELLOW));
                  } else {
                     par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("The server is at " + i1 + "% of its processing limit").setColor(EnumChatFormatting.YELLOW));
                  }

                  return 1;
               }

               int z;
               if (command == EnumCommand.chunks) {
                  i1 = 0;
                  WorldServer[] world_servers = mc_server.worldServers;

                  for(z = 0; z < world_servers.length; ++z) {
                     if (world_servers[z] != null) {
                        i1 += world_servers[z].activeChunkSet.size();
                     }
                  }

                  par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("There are " + i1 + " chunks loaded").setColor(EnumChatFormatting.YELLOW));
                  return 1;
               }

               if (command == EnumCommand.commands) {
                  player.sendPacket(new Packet85SimpleSignal(EnumSignal.list_commands));
                  return 1;
               }

               StringBuffer sb;
               if (command == EnumCommand.skills) {
                  if (world.areSkillsEnabled()) {
                     sb = new StringBuffer("Available skills are: ");
                     sb.append(Skill.getSkillsString(-1, false, ", "));
                     par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()).setColor(EnumChatFormatting.YELLOW));
                     par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Type /skill <skill> for more information").setColor(EnumChatFormatting.GRAY));
                  } else {
                     par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Skills are not enabled").setColor(EnumChatFormatting.RED));
                  }

                  return 1;
               }

               String param;
               Skill skill;
               if (par2Str.startsWith("skill ")) {
                  if (world.areSkillsEnabled()) {
                     param = par2Str.substring(6);
                     skill = Skill.getByLocalizedName(param, false);
                     if (skill == null) {
                        skill = Skill.getByLocalizedName(param, true);
                     }

                     if (skill == null) {
                        par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid skill name \"" + param + "\"").setColor(EnumChatFormatting.RED));
                     } else {
                        par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(skill.getLocalizedDescription()).setColor(EnumChatFormatting.YELLOW));
                        if (player != null) {
                           if (player.hasSkill(skill)) {
                              par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Type /abandon <skill> to forget a skill").setColor(EnumChatFormatting.GRAY));
                           } else {
                              par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Type /learn <skill> to gain a skill").setColor(EnumChatFormatting.GRAY));
                           }
                        }
                     }
                  } else {
                     par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Skills are not enabled").setColor(EnumChatFormatting.RED));
                  }

                  return 1;
               }

               if (Minecraft.inDevMode() && par2Str.equals("skills on")) {
                  par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(world.areSkillsEnabled() ? "Skills are already enabled" : "Skills are now enabled").setColor(EnumChatFormatting.YELLOW));
                  if (!world.worldInfo.areSkillsEnabled()) {
                     world.worldInfo.setSkillsEnabled(true);
                     player.sendPacket((new Packet85SimpleSignal(EnumSignal.skills)).setBoolean(true));
                  }

                  return 1;
               }

               if (Minecraft.inDevMode() && par2Str.equals("skills off")) {
                  par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(world.areSkillsEnabled() ? "Skills are now disabled" : "Skills are already disabled").setColor(EnumChatFormatting.YELLOW));
                  if (world.worldInfo.areSkillsEnabled()) {
                     world.worldInfo.setSkillsEnabled(false);
                     player.sendPacket((new Packet85SimpleSignal(EnumSignal.skills)).setBoolean(false));
                  }

                  return 1;
               }

               if ("mute".equals(par2Str)) {
                  player.sendPacket(new Packet85SimpleSignal(EnumSignal.toggle_mute));
                  return 1;
               }

               int y;
               if ((player == null || Minecraft.inDevMode()) && par2Str.equals("time")) {
                  sb = new StringBuffer("Time Progressing? ");

                  WorldServer world_server;
                  for(y = 0; y < mc_server.worldServers.length; ++y) {
                     if (y > 0) {
                        sb.append(", ");
                     }

                     world_server = mc_server.worldServers[y];
                     sb.append(world_server.getDimensionName());
                     sb.append("=");
                     sb.append(StringHelper.capitalize(StringHelper.yesOrNo(world_server.shouldTimeProgress())));
                  }

                  par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()).setColor(EnumChatFormatting.YELLOW));
                  sb = new StringBuffer("Random Block Ticks? ");

                  for(y = 0; y < mc_server.worldServers.length; ++y) {
                     if (y > 0) {
                        sb.append(", ");
                     }

                     world_server = mc_server.worldServers[y];
                     sb.append(world_server.getDimensionName());
                     sb.append("=");
                     sb.append(StringHelper.capitalize(StringHelper.yesOrNo(world_server.shouldRandomBlockTicksBePerformed())));
                  }

                  par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()).setColor(EnumChatFormatting.YELLOW));
                  sb = new StringBuffer("Time Forwarding? ");

                  for(y = 0; y < mc_server.worldServers.length; ++y) {
                     if (y > 0) {
                        sb.append(", ");
                     }

                     world_server = mc_server.worldServers[y];
                     sb.append(world_server.getDimensionName());
                     sb.append("=");
                     sb.append(StringHelper.capitalize(StringHelper.yesOrNo(!world_server.shouldTimeForwardingBeSkipped())));
                  }

                  par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()).setColor(EnumChatFormatting.YELLOW));
                  sb = new StringBuffer("Total World Time: ");

                  for(y = 0; y < mc_server.worldServers.length; ++y) {
                     if (y > 0) {
                        sb.append(", ");
                     }

                     world_server = mc_server.worldServers[y];
                     sb.append(world_server.getDimensionName());
                     sb.append("=");
                     sb.append(world_server.getTotalWorldTime());
                  }

                  par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()).setColor(EnumChatFormatting.YELLOW));
                  return 1;
               }

               if ((player == null || Minecraft.inDevMode()) && par2Str.equals("village")) {
                  par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Village Conditions: " + mc_server.getVillageConditions()).setColor(EnumChatFormatting.YELLOW));
                  return 1;
               }

               if (player == null) {
                  if (command != EnumCommand.version && Minecraft.inDevMode() && par2Str.equals("hour")) {
                     mc_server.addTotalTimeForAllWorlds(1000);
                     par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("The time is now " + world.getHourOfDayAMPM()).setColor(EnumChatFormatting.YELLOW));
                     return 1;
                  }
               } else {
                  if (command == EnumCommand.xp) {
                     par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("You have " + player.experience + " experience").setColor(EnumChatFormatting.YELLOW));
                     return 1;
                  }

                  if (command == EnumCommand.syncpos) {
                     player.sendPacket((new Packet85SimpleSignal(EnumSignal.sync_pos)).setExactPosition(player.posX, player.getEyePosY(), player.posZ));
                     return 1;
                  }

                  if (par2Str.equals("pushout")) {
                     if (!player.isNearToBlock(Block.bed, 2, 2) && !Minecraft.inDevMode()) {
                        par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("This command only works if you are near a bed").setColor(EnumChatFormatting.YELLOW));
                     } else {
                        player.try_push_out_of_blocks = true;
                     }

                     return 1;
                  }

                  if (command == EnumCommand.ground) {
                     double pos_y = player.posY;
                     z = (int)pos_y;
                     double pos_y_fraction = pos_y - (double)z;
                     if (pos_y_fraction > 0.8999999761581421) {
                        ++z;
                     }

                     if (!world.isAirOrPassableBlock(player.getBlockPosX(), z - 1, player.getBlockPosZ(), true)) {
                        player.setPositionAndUpdate(player.posX, (double)z, player.posZ);
                     }

                     return 1;
                  }

                  if (command == EnumCommand.stats) {
                     player.sendPacket(EntityStatsDump.generatePacketFor(player));
                     return 1;
                  }

                  if (world.areSkillsEnabled() && par2Str.startsWith("learn ")) {
                     param = par2Str.substring(6);
                     skill = Skill.getByLocalizedName(param, false);
                     if (skill == null) {
                        skill = Skill.getByLocalizedName(param, true);
                     }

                     if (skill == null) {
                        par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid skill name \"" + param + "\"").setColor(EnumChatFormatting.RED));
                        return 1;
                     }

                     if (player.hasSkill(skill)) {
                        par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("You are already " + StringHelper.aOrAn(skill.getLocalizedName(true))).setColor(EnumChatFormatting.YELLOW));
                        return 1;
                     }

                     z = player.getNumSkills();
                     i1 = 5 * (z + 1);
                     dx = EntityPlayer.getExperienceRequired(i1);
                     if (player.experience < dx) {
                        par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("You must reach level " + i1 + " before learning " + (z == 0 ? "a" : (z == 1 ? "a second" : (z == 2 ? "a third" : (z == 3 ? "a fourth" : (z == 4 ? "a fifth" : "another"))))) + " profession").setColor(EnumChatFormatting.YELLOW));
                        return 1;
                     }

                     if (player.last_skill_learned_on_day >= world.getDayOfWorld()) {
                        par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("You can only learn one skill per day").setColor(EnumChatFormatting.YELLOW));
                        return 1;
                     }

                     player.addSkill(skill);
                     player.addExperience(-dx);
                     par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("You are now " + (player.hasSkills() ? StringHelper.aOrAn(player.getSkillsString(true)) : StatCollector.translateToLocal("skill.none").toLowerCase())).setColor(EnumChatFormatting.YELLOW));
                     player.last_skill_learned_on_day = world.getDayOfWorld();
                     return 1;
                  }

                  if (world.areSkillsEnabled() && par2Str.startsWith("abandon ")) {
                     param = par2Str.substring(8);
                     skill = Skill.getByLocalizedName(param, false);
                     if (skill == null) {
                        skill = Skill.getByLocalizedName(param, true);
                     }

                     if (skill == null) {
                        par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid skill name \"" + param + "\"").setColor(EnumChatFormatting.RED));
                     } else if (player.hasSkill(skill)) {
                        player.removeSkill(skill);
                        world.playSoundAtEntity(player, "imported.random.level_drain");
                        par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("You are now " + (player.hasSkills() ? StringHelper.aOrAn(player.getSkillsString(true)) : StatCollector.translateToLocal("skill.none").toLowerCase())).setColor(EnumChatFormatting.YELLOW));
                     } else {
                        par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("You don't have that skill").setColor(EnumChatFormatting.YELLOW));
                     }

                     return 1;
                  }

                  EntityLivestock livestock;
                  RaycastCollision rc;
                  if (par2Str.equals("hunger")) {
                     rc = player.getSelectedObject(1.0F, false);
                     if (rc != null && rc.isEntity() && rc.getEntityHit() instanceof EntityLivestock) {
                        livestock = (EntityLivestock)rc.getEntityHit();
                        livestock.setFood(0.0F);
                        return 1;
                     }

                     par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Your metabolic hunger rate is x" + player.getWetnessAndMalnourishmentHungerMultiplier()).setColor(EnumChatFormatting.YELLOW));
                     return 1;
                  }

                  if (this.isUserPrivileged(player)) {
                     if (par2Str.startsWith("decoy ")) {
                        param = par2Str.substring(6).replaceAll(" ", "");
                        Entity entity = EntityList.getEntityInstanceByNameCaseInsensitive(param, world);
                        if (entity instanceof EntityLiving) {
                           world.spawnDecoy(entity.getClass(), player);
                        } else {
                           par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid entity name \"" + par2Str.substring(6) + "\"").setColor(EnumChatFormatting.RED));
                        }

                        return 1;
                     }

                     List loadedEntities;
                     if (par2Str.equals("decoy")) {
                        loadedEntities = world.getEntitiesWithinAABB(EntityLiving.class, player.boundingBox.expand(2.0, 2.0, 2.0));

                        for(y = 0; y < loadedEntities.size(); ++y) {
                           EntityLiving entity_living = (EntityLiving)loadedEntities.get(y);
                           if (entity_living.isDecoy()) {
                              entity_living.setDead();
                           }
                        }

                        return 1;
                     }

                     if (par2Str.equals("grace")) {
                        if (DedicatedServer.disconnection_penalty_enabled) {
                           DedicatedServer.disconnection_penalty_enabled = false;
                           par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Disconnection penalties are now disabled").setColor(EnumChatFormatting.YELLOW));
                        } else {
                           DedicatedServer.disconnection_penalty_enabled = true;
                           par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Disconnection penalties are now enabled").setColor(EnumChatFormatting.YELLOW));
                        }

                        return 1;
                     }

                     if (Minecraft.inDevMode() || player.isZevimrgvInTournament()) {
                        if (par2Str.startsWith("level ")) {
                           i1 = MathHelper.clamp_int(Integer.valueOf(par2Str.substring(6)), -40, EntityPlayer.getHighestPossibleLevel());
                           y = EntityPlayerMP.getExperienceRequired(i1);
                           player.addExperience(y - player.experience);
                           return 1;
                        }

                        if (par2Str.equals("heal")) {
                           player.setHealth(player.getMaxHealth());
                           return 1;
                        }

                        if (par2Str.startsWith("health ")) {
                           i1 = Integer.valueOf(par2Str.substring(7));
                           player.setHealth((float)i1);
                           return 1;
                        }

                        if (par2Str.equals("fill")) {
                           player.foodStats.addSatiation(20);
                           player.foodStats.addNutrition(20);
                           return 1;
                        }

                        if (par2Str.equals("nourish")) {
                           player.setProtein(160000);
                           player.setEssentialFats(160000);
                           player.setPhytonutrients(160000);
                           return 1;
                        }

                        if (par2Str.equals("starve")) {
                           player.foodStats.setSatiation(0, false);
                           player.foodStats.setNutrition(0, false);
                           return 1;
                        }

                        if (par2Str.startsWith("satiation ")) {
                           i1 = Integer.valueOf(par2Str.substring(10));
                           player.foodStats.setSatiation(i1, true);
                           return 1;
                        }

                        if (par2Str.startsWith("nutrition ")) {
                           i1 = Integer.valueOf(par2Str.substring(10));
                           player.foodStats.setNutrition(i1, true);
                           return 1;
                        }

                        if (par2Str.equals("night vision")) {
                           player.sendPacket(new Packet85SimpleSignal(EnumSignal.toggle_night_vision_override));
                           return 1;
                        }

                        if (par2Str.equals("test")) {
                           player.in_test_mode = !player.in_test_mode;
                           par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Test mode toggled " + (player.in_test_mode ? "on" : "off")).setColor(EnumChatFormatting.YELLOW));
                           return 1;
                        }

                        if (par2Str.equals("bolt")) {
                           if (world.isThundering(true) && world.canLightningStrikeAt(player.getBlockPosX(), player.getBlockPosY(), player.getBlockPosZ())) {
                              world.addWeatherEffect(new EntityLightningBolt(world, player.posX, player.posY, player.posZ));
                           } else {
                              par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Lightning cannot strike your position").setColor(EnumChatFormatting.YELLOW));
                           }

                           return 1;
                        }

                        if (par2Str.equals("damage armor")) {
                           player.inventory.tryDamageArmor(DamageSource.generic, 10.0F, (EntityDamageResult)null);
                           return 1;
                        }

                        if (par2Str.startsWith("xp ")) {
                           i1 = Integer.valueOf(par2Str.substring(3));
                           player.addExperience(i1 - player.experience);
                           return 1;
                        }

                        if (par2Str.startsWith("day ")) {
                           i1 = (Integer.valueOf(par2Str.substring(4)) - 1) * 24000;
                           world.setTotalWorldTime((long)i1, true);
                           return 1;
                        }

                        if (par2Str.equals("end")) {
                           if (world.provider.dimensionId != 1) {
                              player.travelToDimension(1);
                           }

                           return 1;
                        }

                        if (par2Str.startsWith("metadata ")) {
                           i1 = Integer.valueOf(par2Str.substring(9));
                           RaycastCollision selectedObject = player.getSelectedObject(1.0F, true);
                           if (selectedObject != null && selectedObject.isBlock()) {
                              if (selectedObject.getNeighborOfBlockHitMaterial().isLiquid()) {
                                 world.setBlockMetadataWithNotify(selectedObject.neighbor_block_x, selectedObject.neighbor_block_y, selectedObject.neighbor_block_z, i1, 3);
                              } else {
                                 world.setBlockMetadataWithNotify(selectedObject.block_hit_x, selectedObject.block_hit_y, selectedObject.block_hit_z, i1, 3);
                              }
                           }

                           return 1;
                        }

                        ItemStack held_item_stack;
                        if (par2Str.equals("damage item")) {
                           held_item_stack = player.getHeldItemStack();
                           if (held_item_stack != null) {
                              held_item_stack.applyRandomItemStackDamageForChest();
                           }

                           return 1;
                        }

                        if (par2Str.startsWith("damage item ")) {
                           i1 = Integer.valueOf(par2Str.substring(12));
                           player.tryDamageHeldItem(DamageSource.generic, i1);
                           return 1;
                        }

                        if (par2Str.equals("repair item")) {
                           held_item_stack = player.getHeldItemStack();
                           if (held_item_stack != null && held_item_stack.isItemDamaged()) {
                              held_item_stack.setItemDamage(0);
                           }

                           return 1;
                        }

                        if (par2Str.equals("thirst")) {
                           rc = player.getSelectedObject(1.0F, false);
                           if (rc != null && rc.isEntity() && rc.getEntityHit() instanceof EntityLivestock) {
                              livestock = (EntityLivestock)rc.getEntityHit();
                              livestock.setWater(0.0F);
                           }

                           return 1;
                        }

                        if (par2Str.equals("grow")) {
                           loadedEntities = world.getEntitiesWithinAABB(EntityAgeable.class, player.boundingBox.expand(4.0, 2.0, 4.0));

                           for(y = 0; y < loadedEntities.size(); ++y) {
                              EntityAgeable entity = (EntityAgeable)loadedEntities.get(y);
                              if (entity.isChild()) {
                                 entity.setGrowingAge(0);
                              }
                           }

                           return 1;
                        }

                        if (par2Str.equals("tame")) {
                           rc = player.getSelectedObject(1.0F, false);
                           if (rc != null && rc.isEntity() && rc.getEntityHit() instanceof EntityHorse) {
                              EntityHorse horse = (EntityHorse)rc.getEntityHit();
                              horse.setTamedBy(player);
                           }

                           return 1;
                        }

                        if (par2Str.equals("clear books")) {
                           player.referenced_books_read.clear();
                           return 1;
                        }

                        int dy;
                        int dx_;
                        int dz;
                        if (par2Str.startsWith("clear ")) {
                           i1 = player.getBlockPosX();
                           y = player.getBlockPosY();
                           z = player.getBlockPosZ();
                           boolean include_permanent_blocks;
                           if (par2Str.endsWith("!")) {
                              par2Str = StringHelper.stripTrailing("!", par2Str);
                              include_permanent_blocks = true;
                           } else {
                              include_permanent_blocks = false;
                           }

                           dx_ = Integer.valueOf(par2Str.substring(6));

                           for(dy = -dx_; dy <= dx_; ++dy) {
                              for(dz = -dx_; dz <= dx_; ++dz) {
                                 for(dx_ = -dx_; dx_ <= dx_; ++dx_) {
                                    if (include_permanent_blocks) {
                                       world.setBlockToAir(i1 + dy, y + dz, z + dx_);
                                    } else {
                                       Block block = world.getBlock(i1 + dy, y + dz, z + dx_);
                                       if (block != Block.bedrock && block != Block.mantleOrCore) {
                                          world.setBlockToAir(i1 + dy, y + dz, z + dx_);
                                       }
                                    }
                                 }
                              }
                           }

                           return 1;
                        }

                        byte radius;
                        if (par2Str.startsWith("timber")) {
                           i1 = player.getBlockPosX();
                           y = player.getBlockPosY();
                           z = player.getBlockPosZ();
                           radius = 10;

                           for(dx_ = -radius; dx_ <= radius; ++dx_) {
                              for(dy = -radius; dy <= radius; ++dy) {
                                 for(dz = -radius; dz <= radius; ++dz) {
                                    Block block = world.getBlock(i1 + dx_, y + dy, z + dz);
                                    if (block == Block.wood || block == Block.leaves || block == Block.vine) {
                                       world.setBlockToAir(i1 + dx_, y + dy, z + dz);
                                    }
                                 }
                              }
                           }

                           return 1;
                        }

                        if (par2Str.startsWith("fill ")) {
                           i1 = player.getBlockPosX();
                           y = player.getBlockPosY();
                           z = player.getBlockPosZ();
                           i1 = Integer.valueOf(par2Str.substring(5));

                           for(dx_ = -i1; dx_ <= i1; ++dx_) {
                              for(dy = 1; dy <= i1; ++dy) {
                                 for(dz = -i1; dz <= i1; ++dz) {
                                    world.setBlock(i1 + dx_, y + dy, z + dz, Block.woodSingleSlab.blockID, 8, 3);
                                 }
                              }
                           }

                           return 1;
                        }

                        if (par2Str.equals("mobs")) {
                           par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("There are " + world.countEntities(IMob.class) + " mobs loaded").setColor(EnumChatFormatting.YELLOW));
                           return 1;
                        }

                        if (par2Str.equals("peaceful")) {
                           player.getWorldServer().decreased_hostile_mob_spawning_counter = 4000;
                           return 1;
                        }

                        if (par2Str.equals("hostile")) {
                           player.getWorldServer().increased_hostile_mob_spawning_counter = 2000;
                           return 1;
                        }

                        if (par2Str.equals("malnourish")) {
                           player.setProtein(0);
                           player.setEssentialFats(0);
                           player.setPhytonutrients(0);
                           return 1;
                        }

                        if (par2Str.startsWith("protein ")) {
                           player.setProtein(Integer.valueOf(par2Str.substring(8)));
                           return 1;
                        }

                        if (par2Str.startsWith("essential fats ")) {
                           player.setEssentialFats(Integer.valueOf(par2Str.substring(15)));
                           return 1;
                        }

                        if (par2Str.startsWith("phytonutrients ")) {
                           player.setPhytonutrients(Integer.valueOf(par2Str.substring(15)));
                           return 1;
                        }

                        if (par2Str.startsWith("insulin ")) {
                           player.setInsulinResistance(Integer.valueOf(par2Str.substring(8)));
                           return 1;
                        }

                        if (par2Str.equals("slaughter")) {
                           loadedEntities = world.loadedEntityList;
                           y = 0;
                           z = 0;
                           i1 = 0;

                           for(dx_ = 0; dx_ < loadedEntities.size(); ++dx_) {
                              Entity entity = (Entity)loadedEntities.get(dx_);
                              Class c = entity.getClass();
                              if (EntityAnimal.class.isAssignableFrom(c) || EntityWaterMob.class.isAssignableFrom(c) || EntityAmbientCreature.class.isAssignableFrom(c)) {
                                 if (entity instanceof EntityHorse) {
                                    EntityHorse entity_horse = (EntityHorse)entity;
                                    if (entity_horse.isHorseSaddled()) {
                                       continue;
                                    }
                                 }

                                 entity.setDead();
                                 if (EntityAnimal.class.isAssignableFrom(c)) {
                                    ++y;
                                 } else if (EntityWaterMob.class.isAssignableFrom(c)) {
                                    ++z;
                                 } else if (EntityAmbientCreature.class.isAssignableFrom(c)) {
                                    ++i1;
                                 }
                              }
                           }

                           par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(y + " animals were slaughtered, " + z + " squids and " + i1 + " bats").setColor(EnumChatFormatting.YELLOW));
                           return 1;
                        }

                        if (par2Str.equals("spawning")) {
                           par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Random mob spawning is now " + (spawning_disabled ? "enabled" : "disabled")).setColor(EnumChatFormatting.YELLOW));
                           spawning_disabled = !spawning_disabled;
                           return 1;
                        }

                        Entity entity;
                        if (par2Str.equals("killall")) {
                           loadedEntities = world.loadedEntityList;
                           y = 0;

                           for(z = 0; z < loadedEntities.size(); ++z) {
                              entity = (Entity)loadedEntities.get(z);
                              if (EntityLiving.class.isAssignableFrom(entity.getClass())) {
                                 entity.setDead();
                                 ++y;
                              }
                           }

                           par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(y + " living entities were killed").setColor(EnumChatFormatting.YELLOW));
                           return 1;
                        }

                        if (par2Str.equals("killmobs")) {
                           loadedEntities = world.loadedEntityList;
                           y = 0;

                           for(z = 0; z < loadedEntities.size(); ++z) {
                              entity = (Entity)loadedEntities.get(z);
                              if (IMob.class.isAssignableFrom(entity.getClass())) {
                                 if (entity instanceof EntityWitch) {
                                    world.removeCursesForWitch((EntityWitch)entity);
                                 }

                                 entity.setDead();
                                 ++y;
                              }
                           }

                           par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(y + " living entities were killed").setColor(EnumChatFormatting.YELLOW));
                           return 1;
                        }

                        if (par2Str.equals("nomobs")) {
                           if (!spawning_disabled) {
                              this.executeCommand(par1ICommandSender, "spawning", permission_override);
                           }

                           this.executeCommand(par1ICommandSender, "killmobs", permission_override);
                           return 1;
                        }

                        if (par2Str.equals("recall") && player.worldObj.provider.dimensionId == 0) {
                           info = world.getWorldInfo();
                           player.travelInsideDimension((double)((float)info.getSpawnX() + 0.5F), (double)((float)info.getSpawnY() + 0.1F), (double)((float)info.getSpawnZ() + 0.5F));
                           return 1;
                        }

                        if (par2Str.equals("corrupt")) {
                           player.getChunkFromPosition().invalidate_checksum = true;
                           player.getChunkFromPosition().isModified = true;
                           return 1;
                        }

                        if (par2Str.equals("crash")) {
                           Object o = new Object();
                           boolean b = true;
                           if (b) {
                              o = null;
                           }

                           System.out.println(o.toString());
                           return 1;
                        }

                        if (par2Str.startsWith("achievement ")) {
                           boolean recursive = par2Str.endsWith("!");
                           if (recursive) {
                              par2Str = StringHelper.left(par2Str, par2Str.length() - 1);
                           }

                           y = Integer.valueOf(par2Str.substring(12));
                           Achievement achievement = AchievementList.getAchievementForId(y);
                           if (achievement == null) {
                              par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid achievement id \"" + par2Str.substring(12) + "\"").setColor(EnumChatFormatting.RED));
                              return 1;
                           }

                           if (recursive) {
                              ArrayList achievements;
                              for(achievements = new ArrayList(); achievement != null; achievement = achievement.parentAchievement) {
                                 achievements.add(achievement);
                              }

                              for(dx_ = achievements.size() - 1; dx_ >= 0; --dx_) {
                                 player.triggerAchievement((Achievement)achievements.get(dx_));
                              }
                           }

                           player.triggerAchievement(achievement);
                           return 1;
                        }

                        if (par2Str.equals("books")) {
                           for(i1 = 1; i1 <= 9; ++i1) {
                              ItemStack book = ItemReferencedBook.generateBook(i1);
                              if (!world.worldInfo.hasSignatureBeenAdded(book.getSignature())) {
                                 world.worldInfo.addSignature(book.getSignature());
                                 player.inventory.addItemStackToInventoryOrDropIt(book);
                              }
                           }

                           return 1;
                        }

                        if (par2Str.equals("layer air")) {
                           i1 = player.getBlockPosX();
                           y = player.getFootBlockPosY() - 1;
                           z = player.getBlockPosZ();
                           radius = 64;

                           for(dx_ = -radius; dx_ <= radius; ++dx_) {
                              for(dy = -radius; dy <= radius; ++dy) {
                                 world.setBlock(i1 + dx_, y, z + dy, 0, 0, 2);
                              }
                           }

                           world.setBlock(i1, y, z, Block.obsidian.blockID);
                           return 1;
                        }

                        if (par2Str.startsWith("layer ")) {
                           param = par2Str.substring(6);
                           Object block;
                           if (param.isEmpty()) {
                              block = null;
                           } else if (StringHelper.startsWithDigit(param)) {
                              block = Block.getBlock(Integer.valueOf(param));
                           } else if (param.equalsIgnoreCase("grass")) {
                              block = Block.grass;
                           } else if (param.equalsIgnoreCase("tall grass")) {
                              block = Block.tallGrass;
                           } else if (!param.equalsIgnoreCase("slab") && !param.equalsIgnoreCase("wooden slab")) {
                              if (param.equalsIgnoreCase("stone slab")) {
                                 block = Block.stoneSingleSlab;
                              } else if (param.equalsIgnoreCase("stairs")) {
                                 block = Block.stairsWoodOak;
                              } else {
                                 block = Block.getBlock(param);
                              }
                           } else {
                              block = Block.woodSingleSlab;
                           }

                           if (block == null) {
                              par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid block id \"" + param + "\"").setColor(EnumChatFormatting.RED));
                           } else {
                              byte metadata;
                              if (block == Block.tallGrass) {
                                 metadata = 1;
                              } else {
                                 metadata = 0;
                              }

                              i1 = player.getBlockPosX();
                              dx_ = player.getFootBlockPosY() - 1;
                              dy = player.getBlockPosZ();
                              int range = 64;

                              for(dx_ = -range; dx_ <= range; ++dx_) {
                                 for(int i = -range; i <= range; ++i) {
                                    if (((Block)block).canBePlacedAt(world, i1 + dx_, dx_, dy + i, metadata)) {
                                       world.setBlock(i1 + dx_, dx_, dy + i, ((Block)block).blockID, metadata, 2);
                                    }
                                 }
                              }

                              world.setBlock(i1, dx_, dy, Block.obsidian.blockID);
                           }

                           return 1;
                        }

                        if (par2Str.startsWith("layermd ")) {
                           param = par2Str.substring(8);
                           y = Integer.valueOf(param);
                           z = player.getBlockPosX();
                           i1 = player.getFootBlockPosY() - 1;
                           dx_ = player.getBlockPosZ();
                           int range = 64;

                           for(dz = -range; dz <= range; ++dz) {
                              for(dx_ = -range; dx_ <= range; ++dx_) {
                                 world.setBlockMetadataWithNotify(z + dz, i1, dx_ + dx_, y, 2);
                              }
                           }

                           return 1;
                        }

                         switch (par2Str) {
                             case "noliquid" -> {
                                 i1 = player.getBlockPosX();
                                 y = player.getBlockPosZ();
                                 z = player.getFootBlockPosY();
                                 radius = 64;

                                 for (dx_ = -radius; dx_ <= radius; ++dx_) {
                                     for (dy = -radius; dy <= radius; ++dy) {
                                         for (dz = -radius; dz <= radius; ++dz) {
                                             if (world.getBlockMaterial(i1 + dx_, z + dz, y + dy).isLiquid()) {
                                                 world.setBlock(i1 + dx_, z + dz, y + dy, Block.stone.blockID, 0, 2);
                                             }
                                         }
                                     }
                                 }

                                 return 1;
                             }
                             case "spawn animals" -> {
                                 i1 = world.getAnimalSpawner().trySpawningPeacefulMobs(world, EnumCreatureType.animal);
                                 par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(i1 + " animals were spawned").setColor(EnumChatFormatting.YELLOW));
                                 return 1;
                             }
                             case "spawn squids" -> {
                                 i1 = world.getAnimalSpawner().trySpawningPeacefulMobs(world, EnumCreatureType.aquatic);
                                 par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(i1 + " squids were spawned").setColor(EnumChatFormatting.YELLOW));
                                 return 1;
                             }
                             case "spawn bats" -> {
                                 i1 = world.getAnimalSpawner().trySpawningPeacefulMobs(world, EnumCreatureType.ambient);
                                 par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(i1 + " bats were spawned").setColor(EnumChatFormatting.YELLOW));
                                 return 1;
                             }
                             case "spawn mobs" -> {
                                 i1 = world.getAnimalSpawner().trySpawningHostileMobs(world, false);
                                 i1 += world.getAnimalSpawner().trySpawningHostileMobs(world, true);
                                 par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(i1 + " mobs were spawned").setColor(EnumChatFormatting.YELLOW));
                                 return 1;
                             }
                             case "bb" -> {
                                 Entity.apply_MITE_bb_limits_checking = !Entity.apply_MITE_bb_limits_checking;
                                 par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("MITE BB limits checking is now " + (Entity.apply_MITE_bb_limits_checking ? "enabled" : "disabled")).setColor(EnumChatFormatting.YELLOW));
                                 return 1;
                             }
                             case "tile entities" -> {
                                 TileEntity.printTileEntitiesList("Loaded Entities on Server", world.loadedTileEntityList);
                                 player.sendPacket(new Packet85SimpleSignal(EnumSignal.loaded_tile_entities));
                                 return 1;
                             }
                             case "weather" -> {
                                 i1 = world.getDayOfWorld();
                                 world.generateWeatherReport(i1, i1 + 31);
                                 return 1;
                             }
                         }

                         long seed;
                        CaveNetworkGenerator cg;
                         switch (par2Str) {
                             case "cavern" -> {
                                 seed = world.rand.nextLong();
                                 Debug.println("Using seed " + seed);
                                 cg = new CaveNetworkGenerator(new CaveNetworkStub(0, 0, 64, 48, 64, seed, world.rand.nextInt(2) == 0, true, true));
                                 cg.apply(world, player.getBlockPosX(), player.getFootBlockPosY(), player.getBlockPosZ());
                                 return 1;
                             }
                             case "cavern+" -> {
                                 seed = world.rand.nextLong();
//                           seed = 6160391524653987290L;
                                 Debug.println("Using seed " + seed);
                                 cg = new CaveNetworkGenerator(new CaveNetworkStub(0, 0, 64, 48, 64, seed, true, true, true));
                                 cg.apply(world, player.getBlockPosX(), player.getFootBlockPosY(), player.getBlockPosZ());
                                 return 1;
                             }
                             case "cavern!" -> {
                                 CaveNetworkGenerator caveNetworkGenerator = new CaveNetworkGenerator(new CaveNetworkStub(-14, 29, 64, 48, 64, 2617667064333438329L, true, true, true));
                                 caveNetworkGenerator.apply(world, player.getBlockPosX(), player.getFootBlockPosY(), player.getBlockPosZ());
                                 return 1;
                             }
                             case "drill" -> {
                                 for (i1 = 10; i1 < player.getFootBlockPosY(); ++i1) {
                                     world.setBlockToAir(player.getBlockPosX(), i1, player.getBlockPosZ());
                                 }

                                 player.setPositionAndUpdate((double) player.getBlockPosX() + 0.5, 10.5, (double) player.getBlockPosZ() + 0.5);
                                 i1 = player.getBlockPosX();
                                 y = player.getBlockPosY();
                                 z = player.getBlockPosZ();
                                 radius = 5;

                                 for (dx_ = -radius; dx_ <= radius; ++dx_) {
                                     for (dy = -radius; dy <= radius; ++dy) {
                                         for (dz = -radius; dz <= radius; ++dz) {
                                             world.setBlockToAir(i1 + dx_, y + dy, z + dz);
                                         }
                                     }
                                 }

                                 player.inventory.addItemStackToInventoryOrDropIt(new ItemStack(Item.flintAndSteel));

                                 for (dx_ = 0; dx_ < 4; ++dx_) {
                                     for (dy = 0; dy < 5; ++dy) {
                                         if (dx_ == 0 || dx_ == 3 || dy == 0 || dy == 4) {
                                             world.setBlock(i1 - 1 + dx_, y - 5 + dy, z - 5, Block.obsidian.blockID);
                                         }
                                     }
                                 }

                                 return 1;
                             }
                             case "teleport" -> {
                                 player.setPosition(-2404.5, 53.0, -613.5);
                                 return 1;
                             }
                         }

                         if (player.isZevimrgvInTournament() && par2Str.equals("see")) {
                           player.sendPacket(new Packet85SimpleSignal(EnumSignal.see));
                           return 1;
                        }
                     }
                  }
               }
            }

            String[] var3 = par2Str.split(" ");
            String var4 = var3[0];
            var3 = dropFirstString(var3);
            ICommand icommand = (ICommand)this.commandMap.get(var4);
            x = this.getUsernameIndex(icommand, var3);
            dx = 0;
            boolean permission_always_denied = icommand instanceof CommandTime || icommand instanceof CommandGameMode || icommand instanceof CommandDifficulty
                    || icommand instanceof CommandDefaultGameMode || icommand instanceof CommandToggleDownfall || icommand instanceof CommandWeather
                    || icommand instanceof CommandXP || icommand instanceof CommandEffect || icommand instanceof CommandEnchant || icommand instanceof CommandGameRule
                    || icommand instanceof CommandClearInventory || icommand instanceof CommandGive;

             if (Minecraft.inDevMode()) {
               permission_always_denied = false;
            }

            try {
               if (icommand == null) {
                  throw new CommandNotFoundException();
               }

               if ((permission_override || icommand.canCommandSenderUseCommand(par1ICommandSender)) && !permission_always_denied) {
                  CommandEvent event = new CommandEvent(icommand, par1ICommandSender, var3);
                  if (MinecraftForge.EVENT_BUS.post(event))
                  {
                     if (event.exception != null)
                     {
                        throw event.exception;
                     }
                     return 1;
                  }

                  if (x > -1) {
                     EntityPlayerMP[] var8 = PlayerSelector.matchPlayers(par1ICommandSender, var3[x]);
                     String var9 = var3[x];
                     EntityPlayerMP[] var10 = var8;
                     int var11 = var8.length;

                     for(int var12 = 0; var12 < var11; ++var12) {
                        EntityPlayerMP var13 = var10[var12];
                        var3[x] = var13.getEntityName();

                        try {
                           icommand.processCommand(par1ICommandSender, var3);
                           ++dx;
                        } catch (CommandException var21) {
                           CommandException var15 = var21;
                           par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions(var15.getMessage(), var15.getErrorOjbects()).setColor(EnumChatFormatting.RED));
                        }
                     }

                     var3[x] = var9;
                  } else {
                     icommand.processCommand(par1ICommandSender, var3);
                     ++dx;
                  }
               } else {
                  par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.generic.permission").setColor(EnumChatFormatting.RED));
               }
            } catch (WrongUsageException var22) {
               par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.generic.usage", ChatMessageComponent.createFromTranslationWithSubstitutions(var22.getMessage(), var22.getErrorOjbects())).setColor(EnumChatFormatting.RED));
            } catch (CommandException var23) {
               par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions(var23.getMessage(), var23.getErrorOjbects()).setColor(EnumChatFormatting.RED));
            } catch (Throwable var24) {
               Throwable var18 = var24;
               par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.generic.exception").setColor(EnumChatFormatting.RED));
               var18.printStackTrace();
            }

            return dx;
         }
      }
   }

   public ICommand registerCommand(ICommand par1ICommand) {
      List var2 = par1ICommand.getCommandAliases();
      this.commandMap.put(par1ICommand.getCommandName(), par1ICommand);
      this.commandSet.add(par1ICommand);
      if (var2 != null) {
         Iterator var3 = var2.iterator();

         while(true) {
            String var4;
            ICommand var5;
            do {
               if (!var3.hasNext()) {
                  return par1ICommand;
               }

               var4 = (String)var3.next();
               var5 = (ICommand)this.commandMap.get(var4);
            } while(var5 != null && var5.getCommandName().equals(var4));

            this.commandMap.put(var4, par1ICommand);
         }
      } else {
         return par1ICommand;
      }
   }

   private static String[] dropFirstString(String[] par0ArrayOfStr) {
      String[] var1 = new String[par0ArrayOfStr.length - 1];

      for(int var2 = 1; var2 < par0ArrayOfStr.length; ++var2) {
         var1[var2 - 1] = par0ArrayOfStr[var2];
      }

      return var1;
   }

   public List getPossibleCommands(ICommandSender par1ICommandSender, String par2Str) {
      String[] var3 = par2Str.split(" ", -1);
      String var4 = var3[0];
      if (var3.length == 1) {
         ArrayList var8 = new ArrayList();
         Iterator var6 = this.commandMap.entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry var7 = (Map.Entry)var6.next();
            if (CommandBase.doesStringStartWith(var4, (String)var7.getKey()) && ((ICommand)var7.getValue()).canCommandSenderUseCommand(par1ICommandSender)) {
               var8.add(var7.getKey());
            }
         }

         return var8;
      } else {
         if (var3.length > 1) {
            ICommand var5 = (ICommand)this.commandMap.get(var4);
            if (var5 != null) {
               return var5.addTabCompletionOptions(par1ICommandSender, dropFirstString(var3));
            }
         }

         return null;
      }
   }

   public List getPossibleCommands(ICommandSender par1ICommandSender) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.commandSet.iterator();

      while(var3.hasNext()) {
         ICommand var4 = (ICommand)var3.next();
         if (var4.canCommandSenderUseCommand(par1ICommandSender)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public Map getCommands() {
      return this.commandMap;
   }

   private int getUsernameIndex(ICommand par1ICommand, String[] par2ArrayOfStr) {
      if (par1ICommand == null) {
         return -1;
      } else {
         for(int var3 = 0; var3 < par2ArrayOfStr.length; ++var3) {
            if (par1ICommand.isUsernameIndex(par2ArrayOfStr, var3) && PlayerSelector.matchesMultiplePlayers(par2ArrayOfStr[var3])) {
               return var3;
            }
         }

         return -1;
      }
   }
}
