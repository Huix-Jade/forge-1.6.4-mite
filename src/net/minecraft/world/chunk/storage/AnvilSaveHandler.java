package net.minecraft.world.chunk.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldProviderUnderworld;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;

public class AnvilSaveHandler extends SaveHandler {
   public AnvilSaveHandler(File par1File, String par2Str, boolean par3) {
      super(par1File, par2Str, par3);
   }

   public IChunkLoader getChunkLoader(WorldProvider par1WorldProvider) {
      File var2 = this.getWorldDirectory();
      File var3;
      if (par1WorldProvider instanceof WorldProviderUnderworld) {
         var3 = new File(var2, "DIM-2");
         var3.mkdirs();
         return new AnvilChunkLoader(var3);
      } else if (par1WorldProvider.getSaveFolder() != null) {
         var3 = new File(var2, par1WorldProvider.getSaveFolder());
         return new AnvilChunkLoader(var3);
      } else {
         return new AnvilChunkLoader(var2);
      }
   }

   public void saveWorldInfoWithPlayer(WorldInfo par1WorldInfo, NBTTagCompound par2NBTTagCompound) {
      par1WorldInfo.setSaveVersion(19133);
      super.saveWorldInfoWithPlayer(par1WorldInfo, par2NBTTagCompound);
   }

   public void flush() {
      ThreadedFileIOBase.waitForFinish();
      RegionFileCache.clearRegionFileReferences();
   }
}
