package net.minecraft.world;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;

public enum EnumGameType {
   NOT_SET(-1, ""),
   SURVIVAL(0, "survival"),
   CREATIVE(1, "creative"),
   ADVENTURE(2, "adventure");

   public int id;
   String name;

   private EnumGameType(int par3, String par4Str) {
      this.id = par3;
      this.name = par4Str;
   }

   public int getID() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public void configurePlayerCapabilities(PlayerCapabilities par1PlayerCapabilities) {
      if (this.isCreative()) {
         par1PlayerCapabilities.allowFlying = true;
         par1PlayerCapabilities.isCreativeMode = true;
         par1PlayerCapabilities.disableDamage = true;
      } else {
         par1PlayerCapabilities.allowFlying = false;
         par1PlayerCapabilities.isCreativeMode = false;
         par1PlayerCapabilities.disableDamage = false;
         par1PlayerCapabilities.isFlying = false;
      }

      par1PlayerCapabilities.allowEdit = !this.isAdventure();
   }

   public boolean isAdventure() {
      return false;
   }

   public boolean isCreative() {
      return this == CREATIVE && Minecraft.inDevMode();
   }

   public boolean isSurvivalOrAdventure() {
      if (!Minecraft.inDevMode()) {
         return true;
      } else {
         return this == SURVIVAL || this == ADVENTURE;
      }
   }

   public static EnumGameType getByID(int par0) {
      if (!Minecraft.inDevMode()) {
         return SURVIVAL;
      } else {
         EnumGameType[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            EnumGameType var4 = var1[var3];
            if (var4.id == par0) {
               return var4;
            }
         }

         return SURVIVAL;
      }
   }

   public static EnumGameType getByName(String par0Str) {
      if (!Minecraft.inDevMode()) {
         return SURVIVAL;
      } else {
         EnumGameType[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            EnumGameType var4 = var1[var3];
            if (var4.name.equals(par0Str)) {
               return var4;
            }
         }

         return SURVIVAL;
      }
   }
}
