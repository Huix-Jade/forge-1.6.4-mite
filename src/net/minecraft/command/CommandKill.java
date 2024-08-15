package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;

public class CommandKill extends CommandBase {
   public String getCommandName() {
      return "kill";
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }

   public String getCommandUsage(ICommandSender par1ICommandSender) {
      return "commands.kill.usage";
   }

   public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      EntityPlayerMP var3 = getCommandSenderAsPlayer(par1ICommandSender);
      if (var3.isGhost()) {
         par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Dedicated_Server cannot use this command"));
      } else {
         var3.attackEntityFrom(new Damage(DamageSource.absolute, 3276.0F));
         par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.kill.success"));
      }
   }
}
