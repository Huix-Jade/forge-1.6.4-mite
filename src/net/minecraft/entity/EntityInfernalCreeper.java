package net.minecraft.entity;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityInfernalCreeper extends EntityCreeper {
   public EntityInfernalCreeper(World world) {
      super(world);
      this.setSize(this.width * getScale(), this.height * getScale());
      this.explosionRadius *= 2.0F;
   }

   public float getNaturalDefense(DamageSource damage_source) {
      return super.getNaturalDefense(damage_source) + (damage_source.bypassesMundaneArmor() ? 0.0F : 2.0F);
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      int num_drops = this.rand.nextInt(4);
      if (num_drops == 0) {
         num_drops = this.rand.nextInt(3);
      }

      int fortune = damage_source.getLootingModifier();
      if (fortune > 0) {
         num_drops += this.rand.nextInt(fortune + 1);
      }

      if (num_drops > 0 && !recently_hit_by_player) {
         num_drops -= this.rand.nextInt(num_drops + 1);
      }

      for(int i = 0; i < num_drops; ++i) {
         this.dropItem(this.getDropItemId(), 1);
      }

   }

   public int getFragParticle() {
      return Item.fragsInfernalCreeper.itemID;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 3;
   }
}
