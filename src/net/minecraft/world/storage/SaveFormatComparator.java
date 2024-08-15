package net.minecraft.world.storage;

import net.minecraft.world.EnumGameType;

public class SaveFormatComparator implements Comparable {
   private final String fileName;
   private final String displayName;
   private final long lastTimePlayed;
   private final long sizeOnDisk;
   private final boolean requiresConversion;
   private final EnumGameType theEnumGameType;
   private final boolean hardcore;
   private final boolean are_skills_enabled;
   private final boolean cheatsEnabled;
   public final boolean passed_validation;
   public final String failed_validation_reason;

   public SaveFormatComparator(String par1Str, String par2Str, long par3, long par5, EnumGameType par7EnumGameType, boolean par8, boolean par9, boolean par10, boolean are_skills_enabled, boolean passed_validation, String failed_validation_reason) {
      this.fileName = par1Str;
      this.displayName = par2Str;
      this.lastTimePlayed = par3;
      this.sizeOnDisk = par5;
      this.theEnumGameType = par7EnumGameType;
      this.requiresConversion = par8;
      this.hardcore = par9;
      this.cheatsEnabled = par10;
      this.are_skills_enabled = are_skills_enabled;
      this.passed_validation = passed_validation;
      this.failed_validation_reason = failed_validation_reason;
   }

   public String getFileName() {
      return this.fileName;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public boolean requiresConversion() {
      return this.requiresConversion;
   }

   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   public int compareTo(SaveFormatComparator par1SaveFormatComparator) {
      return this.lastTimePlayed < par1SaveFormatComparator.lastTimePlayed ? 1 : (this.lastTimePlayed > par1SaveFormatComparator.lastTimePlayed ? -1 : this.fileName.compareTo(par1SaveFormatComparator.fileName));
   }

   public EnumGameType getEnumGameType() {
      return this.theEnumGameType;
   }

   public boolean isHardcoreModeEnabled() {
      return this.hardcore;
   }

   public boolean areSkillsEnabled() {
      return this.are_skills_enabled;
   }

   public boolean getCheatsEnabled() {
      return this.cheatsEnabled;
   }

   public int compareTo(Object par1Obj) {
      return this.compareTo((SaveFormatComparator)par1Obj);
   }
}
