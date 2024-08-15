package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCubic;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public class EntityMagmaCube extends EntityCubic {
   private int ticks_until_next_fizz_sound;

   public EntityMagmaCube(World par1World) {
      super(par1World);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.20000000298023224);
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      return this.worldObj.difficultySetting > 0 && this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox);
   }

   public float getNaturalDefense(DamageSource damage_source) {
      return damage_source.bypassesMundaneArmor() ? 0.0F : (float)this.getSize() * 2.0F;
   }

   public boolean isImmuneTo(DamageSource damage_source) {
      if (!damage_source.isWater() && !damage_source.isSnowball()) {
         ItemStack item_stack = damage_source.getItemAttackedWith();
         if (item_stack != null && item_stack.getItem() instanceof ItemTool && item_stack.getItemAsTool().isEffectiveAgainstBlock(Block.stone, 0)) {
            return false;
         } else {
            return !damage_source.isExplosion();
         }
      } else {
         return false;
      }
   }

   public int getBrightnessForRender(float par1) {
      return 15728880;
   }

   public float getBrightness(float par1) {
      return 1.0F;
   }

   public EnumParticle getSquishParticle() {
      return EnumParticle.flame;
   }

   public EntityCubic createInstance() {
      return new EntityMagmaCube(this.worldObj);
   }

   protected int getDropItemId() {
      return Item.magmaCream.itemID;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      int item_id = this.getDropItemId();
      if (item_id > 0 && this.getSize() > 1) {
         int num_drops = this.rand.nextInt(4 + damage_source.getLootingModifier()) - 2;

         for(int i = 0; i < num_drops; ++i) {
            this.dropItem(item_id, 1);
         }
      }

   }

   public boolean isBurning() {
      return false;
   }

   public int getJumpDelay(Entity target) {
      return target == null ? this.rand.nextInt(81) + 40 : 20;
   }

   protected void alterSquishAmount() {
      this.squishAmount *= 0.9F;
   }

   protected void jump() {
      this.motionY = (double)(0.42F + (float)this.getSize() * 0.1F);
      this.isAirBorne = true;
   }

   protected void fall(float par1) {
   }

   public int getAttackStrengthMultiplierForType() {
      return 2;
   }

   protected String getHurtSound() {
      return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
   }

   protected String getDeathSound() {
      return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
   }

   public String getJumpSound() {
      return this.getSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
   }

   public boolean handleLavaMovement() {
      return false;
   }

   protected boolean makesSoundOnLand() {
      return true;
   }

   public void onLivingUpdate() {
      if (this.worldObj.isRemote && this.isWet()) {
         this.spawnSteamParticles(this.inWater ? 10 : 1);
      }

      if (!this.worldObj.isRemote && this.isWet() && --this.ticks_until_next_fizz_sound <= 0) {
         this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
         this.ticks_until_next_fizz_sound = this.rand.nextInt(7) + 2;
         if (this.rand.nextInt(this.inWater ? 4 : 16) == 0) {
            this.attackEntityFrom(new Damage(DamageSource.water, 1.0F));
         }
      }

      super.onLivingUpdate();
   }

   public boolean isRepelledByCollisionWithPlayer() {
      return this.getSize() > 1;
   }

   public boolean slowsPlayerOnContact() {
      return false;
   }

   public int getExperienceValue() {
      return this.getSize() * 3;
   }

   public boolean isEntityBiologicallyAlive() {
      return false;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }

   public void onCollidedWithBlock(World world, Block block, int x, int y, int z) {
      if (this.onServer() && block.doRenderBoundsIntersectWith(world, x, y, z, this.boundingBox)) {
         if (block instanceof BlockTorch) {
            world.destroyBlock((new BlockBreakInfo(world, x, y, z)).setCollidedWith(this), true);
            return;
         }

         if (this.instantlyBurnsBlock(block)) {
            world.destroyBlockWithoutDroppingItem(x, y, z, EnumBlockFX.smoke_and_steam);
            return;
         }

         if (block.blockMaterial.isFreezing()) {
            if (world.tryToMeltBlock(x, y, z)) {
               world.blockFX(EnumBlockFX.steam, x, y, z);
            }

            this.attackEntityFrom(new Damage(DamageSource.absolute, 1.0F));
            return;
         }
      }

      super.onCollidedWithBlock(world, block, x, y, z);
   }

   private boolean instantlyBurnsBlock(Block block) {
      if (block == Block.tallGrass) {
         return true;
      } else {
         Material material = block.blockMaterial;
         return material == Material.plants || material == Material.web;
      }
   }

   public boolean attacksAnimals() {
      return false;
   }

   public boolean attacksVillagers() {
      return false;
   }
}
