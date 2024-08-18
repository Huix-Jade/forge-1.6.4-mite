package net.minecraft.client;

import cpw.mods.fml.common.FMLCommonHandler;

public class ClientBrandRetriever {
   public static String getClientModName() {
      return FMLCommonHandler.instance().getModName();
   }
}
