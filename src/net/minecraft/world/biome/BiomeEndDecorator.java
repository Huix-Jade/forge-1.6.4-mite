package net.minecraft.world.biome;

import net.minecraft.block.Block;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.world.gen.feature.WorldGenSpikes;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeEndDecorator extends BiomeDecorator {
   protected WorldGenerator spikeGen;

   public BiomeEndDecorator(BiomeGenBase var1) {
      super(var1);
      this.spikeGen = new WorldGenSpikes(Block.whiteStone.blockID);
   }

   protected void decorate() {
      this.generateOres();
      if (this.randomGenerator.nextInt(5) == 0) {
         int var1 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         int var2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         int var3 = this.currentWorld.getTopSolidOrLiquidBlock(var1, var2);
         this.spikeGen.generate(this.currentWorld, this.randomGenerator, var1, var3, var2);
      }

      if (this.chunk_X == 0 && this.chunk_Z == 0) {
         EntityDragon var4 = new EntityDragon(this.currentWorld);
         var4.setLocationAndAngles(0.0, 128.0, 0.0, this.randomGenerator.nextFloat() * 360.0F, 0.0F);
         this.currentWorld.spawnEntityInWorld(var4);
      }

   }
}
