package net.minecraft.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInfernalCreeper;
import net.minecraft.entity.EntityLivestock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityNetherspawn;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Explosion {
   public boolean isFlaming;
   public boolean isSmoking = true;
   private int field_77289_h = 16;
   private Random explosionRNG = new Random();
   private World worldObj;
   public double explosionX;
   public double explosionY;
   public double explosionZ;
   public Entity exploder;
   public float explosion_size_vs_blocks;
   public float explosion_size_vs_living_entities;
   public List affectedBlockPositions = new ArrayList();
   private Map field_77288_k = new HashMap();

   public Explosion(World world, Entity exploder, double posX, double posY, double posZ, float explosion_size_vs_blocks, float explosion_size_vs_living_entities) {
      this.worldObj = world;
      this.exploder = exploder;
      this.explosionX = posX;
      this.explosionY = posY;
      this.explosionZ = posZ;
      this.explosion_size_vs_blocks = explosion_size_vs_blocks;
      this.explosion_size_vs_living_entities = explosion_size_vs_living_entities;
   }

   private void doPreExplosionA() {
      for(int var3 = 0; var3 < this.field_77289_h; ++var3) {
         for(int var4 = 0; var4 < this.field_77289_h; ++var4) {
            for(int var5 = 0; var5 < this.field_77289_h; ++var5) {
               if (var3 == 0 || var3 == this.field_77289_h - 1 || var4 == 0 || var4 == this.field_77289_h - 1 || var5 == 0 || var5 == this.field_77289_h - 1) {
                  double var6 = (double)((float)var3 / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                  double var8 = (double)((float)var4 / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                  double var10 = (double)((float)var5 / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                  double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
                  var6 /= var12;
                  var8 /= var12;
                  var10 /= var12;
                  float var14 = this.explosion_size_vs_blocks * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                  var14 *= 2.0F;
                  var14 *= 1.25F;
                  double var15 = this.explosionX;
                  double var17 = this.explosionY;
                  double var19 = this.explosionZ;

                  for(float var21 = 0.3F; var14 > 0.0F; var14 -= var21 * 0.75F) {
                     int var22 = MathHelper.floor_double(var15);
                     int var23 = MathHelper.floor_double(var17);
                     int var24 = MathHelper.floor_double(var19);
                     int var25 = this.worldObj.getBlockId(var22, var23, var24);
                     if (var25 > 0) {
                        Block var26 = Block.blocksList[var25];
                        float var27 = var26.getExplosionResistance(this);
                        if (!(var27 < 0.0F) && !(var27 > 0.1F)) {
                           var14 -= (var27 + 0.3F) * var21;
                        } else {
                           var14 = 0.0F;
                        }
                     }

                     if (var14 > 0.0F && (this.exploder == null || this.exploder.shouldExplodeBlock(this, this.worldObj, var22, var23, var24, var25, var14))) {
                        if (var25 > 0) {
                           Block.blocksList[var25].dropBlockAsEntityItem((new BlockBreakInfo(this.worldObj, var22, var23, var24)).setExploded(this));
                        }

                        this.worldObj.setBlockToAir(var22, var23, var24);
                     } else if (var14 >= 0.0F && this.explosionY > (double)var23 && this.worldObj.getBlock(var22, var23, var24) == Block.mycelium) {
                        this.worldObj.setBlock(var22, var23, var24, Block.dirt.blockID);
                        Block.dirt.onUnderminedByPlayer(this.worldObj, (EntityPlayer)null, var22, var23, var24);
                     }

                     var15 += var6 * (double)var21;
                     var17 += var8 * (double)var21;
                     var19 += var10 * (double)var21;
                  }
               }
            }
         }
      }

   }

   public void doExplosionA() {
      this.doPreExplosionA();
      if (this.exploder instanceof EntityInfernalCreeper || this.exploder instanceof EntityNetherspawn) {
         this.isFlaming = true;
      }

      HashSet var2 = new HashSet();

      int var3;
      int var4;
      int var5;
      double var15;
      double var17;
      double var19;
      double var12;
      for(var3 = 0; var3 < this.field_77289_h; ++var3) {
         for(var4 = 0; var4 < this.field_77289_h; ++var4) {
            for(var5 = 0; var5 < this.field_77289_h; ++var5) {
               if (var3 == 0 || var3 == this.field_77289_h - 1 || var4 == 0 || var4 == this.field_77289_h - 1 || var5 == 0 || var5 == this.field_77289_h - 1) {
                  double var6 = (double)((float)var3 / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                  double var8 = (double)((float)var4 / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                  double var10 = (double)((float)var5 / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                  var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
                  var6 /= var12;
                  var8 /= var12;
                  var10 /= var12;
                  float var14 = this.explosion_size_vs_blocks * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                  var15 = this.explosionX;
                  var17 = this.explosionY;
                  var19 = this.explosionZ;

                  for(float var21 = 0.3F; var14 > 0.0F; var14 -= var21 * 0.75F) {
                     int var22 = MathHelper.floor_double(var15);
                     int var23 = MathHelper.floor_double(var17);
                     int var24 = MathHelper.floor_double(var19);
                     int var25 = this.worldObj.getBlockId(var22, var23, var24);
                     if (var25 > 0) {
                        Block var26 = Block.blocksList[var25];
                        float var27 = var26.getExplosionResistance(this);
                        if (var27 > 0.8F && this.exploder instanceof EntityWitherSkull) {
                           EntityWitherSkull wither_skull = (EntityWitherSkull)this.exploder;
                           if (wither_skull.isInvulnerable() && var26 != Block.bedrock && var26 != Block.endPortal && var26 != Block.endPortalFrame && var26 != Block.mantleOrCore) {
                              var27 = 0.8F;
                           }
                        }

                        if (var27 < 0.0F) {
                           var14 = 0.0F;
                        } else {
                           var14 -= (var27 + 0.3F) * var21;
                        }
                     }

                     if (var14 > 0.0F && (this.exploder == null || this.exploder.shouldExplodeBlock(this, this.worldObj, var22, var23, var24, var25, var14))) {
                        var2.add(new ChunkPosition(var22, var23, var24));
                     }

                     var15 += var6 * (double)var21;
                     var17 += var8 * (double)var21;
                     var19 += var10 * (double)var21;
                  }
               }
            }
         }
      }

      float effective_explosion_size_vs_entities = this.explosion_size_vs_living_entities * 4.0F;
      this.affectedBlockPositions.addAll(var2);
      var3 = MathHelper.floor_double(this.explosionX - (double)effective_explosion_size_vs_entities - 1.0);
      var4 = MathHelper.floor_double(this.explosionX + (double)effective_explosion_size_vs_entities + 1.0);
      var5 = MathHelper.floor_double(this.explosionY - (double)effective_explosion_size_vs_entities - 1.0);
      int var29 = MathHelper.floor_double(this.explosionY + (double)effective_explosion_size_vs_entities + 1.0);
      int var7 = MathHelper.floor_double(this.explosionZ - (double)effective_explosion_size_vs_entities - 1.0);
      int var30 = MathHelper.floor_double(this.explosionZ + (double)effective_explosion_size_vs_entities + 1.0);
      List var9 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getAABBPool().getAABB((double)var3, (double)var5, (double)var7, (double)var4, (double)var29, (double)var30));
      Vec3 var31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.explosionX, this.explosionY, this.explosionZ);

      double var13;
      double var34;
      double var33;
      double var35;
      for(int var11 = 0; var11 < var9.size(); ++var11) {
         Entity var32 = (Entity)var9.get(var11);
         if (!var32.handleExplosion(this)) {
            var13 = var32.getDistance(this.explosionX, this.explosionY, this.explosionZ) / (double)effective_explosion_size_vs_entities;
            if (var13 <= 1.0) {
               var15 = var32.posX - this.explosionX;
               var17 = var32.posY + (double)var32.getEyeHeight() - this.explosionY;
               var19 = var32.posZ - this.explosionZ;
               var34 = (double)MathHelper.sqrt_double(var15 * var15 + var17 * var17 + var19 * var19);
               if (var34 != 0.0) {
                  var15 /= var34;
                  var17 /= var34;
                  var19 /= var34;
                  var33 = (double)this.worldObj.getBlockDensity(var31, var32.boundingBox);
                  var35 = (1.0 - var13) * var33;
                  float damage = effective_explosion_size_vs_entities * (float)var35 * 4.0F;
                  damage *= 0.6666667F;
                  if (damage >= 0.5F) {
                     var32.attackEntityFrom(new Damage(DamageSource.setExplosionSource(this), damage));
                  }

                  double var36 = EnchantmentProtection.func_92092_a(var32, var35);
                  if (!(var32 instanceof EntityItem)) {
                     var32.motionX += var15 * var36;
                     var32.motionY += var17 * var36;
                     var32.motionZ += var19 * var36;
                  }

                  if (var32 instanceof EntityPlayer) {
                     this.field_77288_k.put((EntityPlayer)var32, this.worldObj.getWorldVec3Pool().getVecFromPool(var15 * var35, var17 * var35, var19 * var35));
                  }
               }
            }
         }
      }

      var12 = this.explosionX - ((double)effective_explosion_size_vs_entities - 1.0) * 4.0;
      var13 = this.explosionX + ((double)effective_explosion_size_vs_entities + 1.0) * 4.0;
      var34 = this.explosionY - ((double)effective_explosion_size_vs_entities - 1.0) * 4.0;
      var33 = this.explosionY + ((double)effective_explosion_size_vs_entities + 1.0) * 4.0;
      var35 = this.explosionZ - ((double)effective_explosion_size_vs_entities - 1.0) * 4.0;
      double maxZ = this.explosionZ + ((double)effective_explosion_size_vs_entities + 1.0) * 4.0;
      List nearby_livestock = this.worldObj.getEntitiesWithinAABB(EntityLivestock.class, AxisAlignedBB.getAABBPool().getAABB(var12, var34, var35, var13, var33, maxZ));
      Iterator i = nearby_livestock.iterator();

      while(i.hasNext()) {
         EntityLivestock livestock = (EntityLivestock)i.next();
         if (!livestock.isDead) {
            livestock.spooked_until = this.worldObj.getTotalWorldTime() + 400L + (long)this.worldObj.rand.nextInt(400);
         }
      }

   }

   public void doExplosionB(boolean par1) {
      this.worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
      if (this.explosion_size_vs_blocks >= 2.0F && this.isSmoking) {
         this.worldObj.spawnParticle(EnumParticle.hugeexplosion, this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
      } else {
         this.worldObj.spawnParticle(EnumParticle.largeexplode, this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
      }

      Iterator var2;
      ChunkPosition var3;
      int var4;
      int var5;
      int var6;
      int var7;
      if (this.isSmoking) {
         var2 = this.affectedBlockPositions.iterator();

         while(var2.hasNext()) {
            var3 = (ChunkPosition)var2.next();
            var4 = var3.x;
            var5 = var3.y;
            var6 = var3.z;
            var7 = this.worldObj.getBlockId(var4, var5, var6);
            if (par1) {
               double var8 = (double)((float)var4 + this.worldObj.rand.nextFloat());
               double var10 = (double)((float)var5 + this.worldObj.rand.nextFloat());
               double var12 = (double)((float)var6 + this.worldObj.rand.nextFloat());
               double var14 = var8 - this.explosionX;
               double var16 = var10 - this.explosionY;
               double var18 = var12 - this.explosionZ;
               double var20 = (double)MathHelper.sqrt_double(var14 * var14 + var16 * var16 + var18 * var18);
               var14 /= var20;
               var16 /= var20;
               var18 /= var20;
               double var22 = 0.5 / (var20 / (double)this.explosion_size_vs_blocks + 0.1);
               var22 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
               var14 *= var22;
               var16 *= var22;
               var18 *= var22;
               this.worldObj.spawnParticle(EnumParticle.explode, (var8 + this.explosionX * 1.0) / 2.0, (var10 + this.explosionY * 1.0) / 2.0, (var12 + this.explosionZ * 1.0) / 2.0, var14, var16, var18);
               this.worldObj.spawnParticle(EnumParticle.smoke, var8, var10, var12, var14, var16, var18);
               this.worldObj.setBlock(var4, var5, var6, 0, 0, 2);
            }

            if (var7 > 0 && !this.worldObj.isRemote) {
               Block var25 = Block.blocksList[var7];
               BlockBreakInfo info = (new BlockBreakInfo(this.worldObj, var4, var5, var6)).setExploded(this);
               var25.onBlockAboutToBeBroken(info);
               if (var25.canDropFromExplosion(this)) {
                  var25.dropBlockAsEntityItem(info);
               }

               this.worldObj.setBlock(var4, var5, var6, 0, 0, 3);
               var25.onBlockDestroyedByExplosion(this.worldObj, var4, var5, var6, this);
            }
         }
      }

      if (this.isFlaming) {
         var2 = this.affectedBlockPositions.iterator();

         while(var2.hasNext()) {
            var3 = (ChunkPosition)var2.next();
            var4 = var3.x;
            var5 = var3.y;
            var6 = var3.z;
            var7 = this.worldObj.getBlockId(var4, var5, var6);
            int var24 = this.worldObj.getBlockId(var4, var5 - 1, var6);
            if (var7 == 0 && Block.opaqueCubeLookup[var24] && this.explosionRNG.nextInt(3) == 0) {
               this.worldObj.setBlock(var4, var5, var6, Block.fire.blockID);
            }
         }
      }

   }

   public Map func_77277_b() {
      return this.field_77288_k;
   }

   public EntityLivingBase getExplosivePlacedBy() {
      return this.exploder == null ? null : (this.exploder instanceof EntityTNTPrimed ? ((EntityTNTPrimed)this.exploder).getTntPlacedBy() : (this.exploder instanceof EntityLivingBase ? (EntityLivingBase)this.exploder : null));
   }
}
