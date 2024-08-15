package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityMinecartTNT extends EntityMinecart {
   private int minecartTNTFuse = -1;

   public EntityMinecartTNT(World par1World) {
      super(par1World);
   }

   public EntityMinecartTNT(World par1World, double par2, double par4, double par6) {
      super(par1World, par2, par4, par6);
   }

   public int getMinecartType() {
      return 3;
   }

   public Block getDefaultDisplayTile() {
      return Block.tnt;
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.minecartTNTFuse > 0) {
         --this.minecartTNTFuse;
         this.worldObj.spawnParticle(EnumParticle.smoke, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
      } else if (this.minecartTNTFuse == 0) {
         this.explodeCart(this.motionX * this.motionX + this.motionZ * this.motionZ);
      }

      if (this.isCollidedHorizontally) {
         double var1 = this.motionX * this.motionX + this.motionZ * this.motionZ;
         if (var1 >= 0.009999999776482582) {
            this.explodeCart(var1);
         }
      }

      if (this.isBurning()) {
         this.ignite();
      }

   }

   public void killMinecart(DamageSource par1DamageSource) {
      super.killMinecart(par1DamageSource);
      double var2 = this.motionX * this.motionX + this.motionZ * this.motionZ;
      if (!par1DamageSource.isExplosion()) {
         this.dropItemStack(new ItemStack(Block.tnt, 1), 0.0F);
      }

      if (par1DamageSource.hasFireAspect() || par1DamageSource.isExplosion() || var2 >= 0.009999999776482582) {
         this.explodeCart(var2);
      }

   }

   protected void explodeCart(double par1) {
      if (!this.worldObj.isRemote) {
         double var3 = Math.sqrt(par1);
         if (var3 > 5.0) {
            var3 = 5.0;
         }

         float explosion_size = (float)(4.0 + this.rand.nextDouble() * 1.5 * var3);
         this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, explosion_size, explosion_size, true);
         this.setDead();
      }

   }

   protected void fall(float par1) {
      if (par1 >= 3.0F) {
         float var2 = par1 / 10.0F;
         this.explodeCart((double)(var2 * var2));
      }

      super.fall(par1);
   }

   public void onActivatorRailPass(int par1, int par2, int par3, boolean par4) {
      if (par4 && this.minecartTNTFuse < 0) {
         this.ignite();
      }

   }

   public void handleHealthUpdate(EnumEntityState par1) {
      if (par1 == EnumEntityState.tnt_ignite_or_eating_grass) {
         this.ignite();
      } else {
         super.handleHealthUpdate(par1);
      }

   }

   public void ignite() {
      if (this.minecartTNTFuse < 0) {
         this.minecartTNTFuse = 80;
         if (!this.worldObj.isRemote) {
            this.worldObj.setEntityState(this, EnumEntityState.tnt_ignite_or_eating_grass);
            this.worldObj.playSoundAtEntity(this, "random.fuse", 1.0F, 1.0F);
         }

      }
   }

   public int func_94104_d() {
      return this.minecartTNTFuse;
   }

   public boolean isIgnited() {
      return this.minecartTNTFuse > -1;
   }

   public boolean shouldExplodeBlock(Explosion par1Explosion, World par2World, int par3, int par4, int par5, int par6, float par7) {
      return !this.isIgnited() || !BlockRailBase.isRailBlock(par6) && !BlockRailBase.isRailBlockAt(par2World, par3, par4 + 1, par5) ? super.shouldExplodeBlock(par1Explosion, par2World, par3, par4, par5, par6, par7) : false;
   }

   protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      if (par1NBTTagCompound.hasKey("TNTFuse")) {
         this.minecartTNTFuse = par1NBTTagCompound.getInteger("TNTFuse");
      }

   }

   protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("TNTFuse", this.minecartTNTFuse);
   }

   public Item getModelItem() {
      return Item.minecartTnt;
   }

   public void spentTickInFire() {
      this.ignite();
   }
}
