package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class EntityClayGolem extends EntityEarthElemental {
   public EntityClayGolem(World world) {
      super(world);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 6.0);
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, 30.0);
   }

   public float getNaturalDefense() {
      return this.isHardenedClay() ? 2.0F : 0.0F;
   }

   public boolean isValidBlock(Block block) {
      return block == Block.blockClay;
   }

   public void setTypeForBlock(Block block, boolean heated) {
      this.setType(heated ? CLAY_HARDENED : CLAY_NORMAL);
   }

   public boolean isMagma() {
      return false;
   }
}
