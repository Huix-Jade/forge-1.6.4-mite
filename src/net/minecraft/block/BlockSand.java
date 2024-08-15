package net.minecraft.block;

import net.minecraft.block.material.Material;

public final class BlockSand extends BlockFalling {
   public BlockSand(int id) {
      super(id, Material.sand, (new BlockConstants()).setNeverConnectsWithFence().setUseNewSandPhysics());
   }

   public String getMetadataNotes() {
      return "Cactus kill count is kept in the sand beneath it, with a maximum value of 7";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata <= BlockCactus.getKillCountBits();
   }
}
