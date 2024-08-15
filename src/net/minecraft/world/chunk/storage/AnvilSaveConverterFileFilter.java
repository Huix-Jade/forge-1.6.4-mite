package net.minecraft.world.chunk.storage;

import java.io.File;
import java.io.FilenameFilter;

class AnvilSaveConverterFileFilter implements FilenameFilter {
   // $FF: synthetic field
   final AnvilSaveConverter parent;

   AnvilSaveConverterFileFilter(AnvilSaveConverter var1) {
      this.parent = var1;
   }

   public boolean accept(File var1, String var2) {
      return var2.endsWith(".mcr");
   }
}
