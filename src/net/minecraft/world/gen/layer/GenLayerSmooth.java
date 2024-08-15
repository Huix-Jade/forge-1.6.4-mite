package net.minecraft.world.gen.layer;

public class GenLayerSmooth extends GenLayer {
   public GenLayerSmooth(long par1, GenLayer par3GenLayer) {
      super(par1);
      super.parent = par3GenLayer;
   }

   public int[] getInts(int par1, int par2, int par3, int par4, int z) {
      int var5 = par1 - 1;
      int var6 = par2 - 1;
      int var7 = par3 + 2;
      int var8 = par4 + 2;
      int[] var9 = this.parent.getInts(var5, var6, var7, var8, z);
      int[] var10 = IntCache.getIntCache(par3 * par4);

      for(int var11 = 0; var11 < par4; ++var11) {
         for(int var12 = 0; var12 < par3; ++var12) {
            int var13 = var9[var12 + 0 + (var11 + 1) * var7];
            int var14 = var9[var12 + 2 + (var11 + 1) * var7];
            int var15 = var9[var12 + 1 + (var11 + 0) * var7];
            int var16 = var9[var12 + 1 + (var11 + 2) * var7];
            int var17 = var9[var12 + 1 + (var11 + 1) * var7];
            if (var13 == var14 && var15 == var16) {
               long par1_1 = (long)(var12 + par1);
               long par3_1 = (long)(var11 + par2);
               this.chunkSeed = this.worldGenSeed;
               this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
               this.chunkSeed += par1_1;
               this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
               this.chunkSeed += par3_1;
               this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
               this.chunkSeed += par1_1;
               this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
               this.chunkSeed += par3_1;
               var17 = (this.chunkSeed >> 24 & 1L) == 0L ? var13 : var15;
               this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
               this.chunkSeed += this.worldGenSeed;
            } else {
               if (var13 == var14) {
                  var17 = var13;
               }

               if (var15 == var16) {
                  var17 = var15;
               }
            }

            var10[var12 + var11 * par3] = var17;
         }
      }

      return var10;
   }
}
