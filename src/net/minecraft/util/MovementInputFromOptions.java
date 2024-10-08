package net.minecraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput {
   private GameSettings gameSettings;

   public MovementInputFromOptions(GameSettings par1GameSettings) {
      this.gameSettings = par1GameSettings;
   }

   public void updatePlayerMoveState() {
      this.moveStrafe = 0.0F;
      this.moveForward = 0.0F;
      if (!Minecraft.theMinecraft.thePlayer.isGhost()) {
         if (this.gameSettings.keyBindForward.pressed) {
            ++this.moveForward;
         }

         if (this.gameSettings.keyBindBack.pressed) {
            --this.moveForward;
         }

         if (this.gameSettings.keyBindLeft.pressed) {
            ++this.moveStrafe;
         }

         if (this.gameSettings.keyBindRight.pressed) {
            --this.moveStrafe;
         }

         this.jump = this.gameSettings.keyBindJump.pressed;
         this.sneak = this.gameSettings.keyBindSneak.pressed;
         if (this.sneak) {
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3);
            this.moveForward = (float)((double)this.moveForward * 0.3);
         }

      }
   }
}
