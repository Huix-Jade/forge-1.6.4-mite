package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.item.Item;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityNetherspawn extends EntitySilverfish {
   private int ticks_until_next_fizz_sound;

   public EntityNetherspawn(World world) {
      super(world);
      this.getNavigator().setAvoidsWater(true);
   }

   public void onLivingUpdate() {
      if (this.onClient()) {
         if (this.isWet()) {
            this.spawnSteamParticles(this.inWater ? 10 : 1);
         }

         this.worldObj.spawnParticle(EnumParticle.largesmoke, this.posX + (this.rand.nextDouble() - 0.5) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5) * (double)this.width, 0.0, 0.0, 0.0);
      } else if (this.isWet() && --this.ticks_until_next_fizz_sound <= 0) {
         this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
         this.ticks_until_next_fizz_sound = this.rand.nextInt(7) + 2;
         if (this.rand.nextInt(this.inWater ? 1 : 4) == 0) {
            this.attackEntityFrom(new Damage(DamageSource.water, 1.0F));
         }
      }

      super.onLivingUpdate();
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public int getFragParticle() {
      return Item.fragsNetherspawn.itemID;
   }

   public boolean shouldExplodeBlock(Explosion explosion, World world, int x, int y, int z, int block_id, float force) {
      return block_id != Block.netherrack.blockID && block_id != Block.oreNetherQuartz.blockID && block_id != Block.oreGold.blockID ? super.shouldExplodeBlock(explosion, world, x, y, z, block_id, force) : false;
   }

   public boolean canTakeDamageFromPlayerThrownSnowballs() {
      return true;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result != null && result.entityWasDestroyed() && !this.isWet() && !this.isInsideOfMaterial(Material.lava) && !damage.isSnowball() && damage.getSource() != DamageSource.water) {
         this.entityFX(EnumEntityFX.frags);
         this.worldObj.createExplosion(this, this.posX, this.posY + (double)(this.height / 4.0F), this.posZ, 1.0F, 1.0F, true);
         this.setDead();
      }

      return result;
   }
}
