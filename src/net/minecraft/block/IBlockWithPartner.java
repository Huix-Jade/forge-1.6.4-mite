package net.minecraft.block;

import net.minecraft.world.World;

public interface IBlockWithPartner {
   int getPartnerOffsetX(int var1);

   int getPartnerOffsetY(int var1);

   int getPartnerOffsetZ(int var1);

   boolean requiresPartner(int var1);

   boolean isPartner(int var1, Block var2, int var3);

   boolean isPartnerPresent(World var1, int var2, int var3, int var4);

   boolean partnerDropsAsItem(int var1);
}
