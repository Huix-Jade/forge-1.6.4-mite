package net.minecraft.entity.item;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.commons.io.IOUtils;

public abstract class EntityMinecart extends Entity {
   private boolean isInReverse;
   private final IUpdatePlayerListBox field_82344_g;
   private String entityName;
   private static final int[][][] matrix = new int[][][]{{{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}}, {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
   private int turnProgress;
   private double minecartX;
   private double minecartY;
   private double minecartZ;
   private double minecartYaw;
   private double minecartPitch;
   private double velocityX;
   private double velocityY;
   private double velocityZ;
   private static String[][] s = new String[][]{{"mvg.nrmvxizug.hix.XlmgzrmviKozbvi", "ee"}, {"mvg.nrmvxizug.hix.XlmgzrmviDlipyvmxs", "eo"}, {"mvg.nrmvxizug.hix.NRGVXlmhgzmg", "NRGVXlmhgzmg"}, {"mvg.nrmvxizug.hix.NRGVXlmgzrmviXizugrmt", "NRGVXlmgzrmviXizugrmt"}, {"mvg.nrmvxizug.hix.VmgrgbXorvmgKozbviNK", "ywr"}, {"mvg.nrmvxizug.hix.VmgrgbKozbvi", "fu"}, {"mvg.nrmvxizug.hix.VmgrgbKozbviHK", "yvc"}, {"mvg.nrmvxizug.hix.UllwHgzgh", "fc"}, {"mvg.nrmvxizug.hix.Nrmvxizug", "zge"}, {"mvg.nrmvxizug.hix.NlevnvmgRmkfgUilnLkgrlmh", "yvd"}, {"mvg.nrmvxizug.hix.MvgXorvmgSzmwovi", "yxd"}, {"mvg.nrmvxizug.hix.KozbviXlmgilooviNK", "ywx"}, {"mvg.nrmvxizug.hix.Kzxpvg86KozbviOllpNlev", "vd"}, {"mvg.nrmvxizug.hix.Kzxpvg72KozbviRmkfg", "uv"}, {"mvg.nrmvxizug.hix.Kzxpvg17ZwwSfmtvi", "Kzxpvg17ZwwSfmtvi"}, {"mvg.nrmvxizug.hix.Kzxpvg14HrnkovHrtmzo", "Kzxpvg14HrnkovHrtmzo"}, {"mvg.nrmvxizug.hix.Kzxpvg797KozbviZyrorgrvh", "uz"}, {"mvg.nrmvxizug.hix.KozbviXzkzyrorgrvh", "fx"}, {"mvg.nrmvxizug.hix.GxkXlmmvxgrlm", "xl"}};
   public static Class[] c;
   public static int[] S;

   public EntityMinecart(World par1World) {
      super(par1World);
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.7F);
      this.yOffset = this.height / 2.0F;
      this.field_82344_g = par1World != null ? par1World.getMinecartSoundUpdater(this) : null;
   }

   public static EntityMinecart createMinecart(World par0World, double par1, double par3, double par5, int par7) {
      switch (par7) {
         case 1:
            return new EntityMinecartChest(par0World, par1, par3, par5);
         case 2:
            return new EntityMinecartFurnace(par0World, par1, par3, par5);
         case 3:
            return new EntityMinecartTNT(par0World, par1, par3, par5);
         case 4:
            return new EntityMinecartMobSpawner(par0World, par1, par3, par5);
         case 5:
            return new EntityMinecartHopper(par0World, par1, par3, par5);
         default:
            return new EntityMinecartEmpty(par0World, par1, par3, par5);
      }
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void entityInit() {
      this.dataWatcher.addObject(17, new Integer(0));
      this.dataWatcher.addObject(18, new Integer(1));
      this.dataWatcher.addObject(19, new Float(0.0F));
      this.dataWatcher.addObject(20, new Integer(0));
      this.dataWatcher.addObject(21, new Integer(6));
      this.dataWatcher.addObject(22, (byte)0);
   }

   public AxisAlignedBB getCollisionBox(Entity par1Entity) {
      return par1Entity.canBePushed() ? par1Entity.boundingBox : null;
   }

   public AxisAlignedBB getBoundingBox() {
      return null;
   }

   public boolean canBePushed() {
      return true;
   }

   public EntityMinecart(World par1World, double par2, double par4, double par6) {
      this(par1World);
      this.setPosition(par2, par4, par6);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.prevPosX = par2;
      this.prevPosY = par4;
      this.prevPosZ = par6;
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.0 - 0.30000001192092896;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result != null && !result.entityWasDestroyed()) {
         if (damage.isLavaDamage()) {
            this.setDead();
            return result.setEntityWasDestroyed();
         } else {
            this.setRollingDirection(-this.getRollingDirection());
            this.setRollingAmplitude(10);
            this.setBeenAttacked();
            result.setEntityWasAffected();
            result.startTrackingHealth(this.getDamage());
            this.setDamage(this.getDamage() + damage.getAmount() * 10.0F);
            result.finishTrackingHealth(this.getDamage());
            boolean var3 = damage.wasCausedByPlayerInCreative();
            if (var3 || this.getDamage() > 40.0F) {
               if (this.riddenByEntity != null) {
                  this.riddenByEntity.mountEntity(this);
               }

               if (var3 && !this.hasCustomName()) {
                  this.setDead();
               } else {
                  this.killMinecart(damage.getSource());
               }

               result.setEntityWasDestroyed();
            }

            return result;
         }
      } else {
         return result;
      }
   }

   public void killMinecart(DamageSource par1DamageSource) {
      this.setDead();
      ItemStack var2 = new ItemStack(Item.minecartEmpty, 1);
      if (this.entityName != null) {
         var2.setItemName(this.entityName);
      }

      this.dropItemStack(var2, 0.0F);
   }

   public void performHurtAnimation() {
      this.setRollingDirection(-this.getRollingDirection());
      this.setRollingAmplitude(10);
      this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
   }

   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   public void setDead() {
      super.setDead();
      if (this.field_82344_g != null) {
         this.field_82344_g.update();
      }

   }

   public void onUpdate() {
      if (this.field_82344_g != null) {
         this.field_82344_g.update();
      }

      if (this.getRollingAmplitude() > 0) {
         this.setRollingAmplitude(this.getRollingAmplitude() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      if (this.posY < -64.0) {
         this.kill();
      }

      int var2;
      int var20;
      if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
         this.worldObj.theProfiler.startSection("portal");
         MinecraftServer var1 = ((WorldServer)this.worldObj).getMinecraftServer();
         var2 = this.getMaxInPortalTime();
         if (this.inPortal) {
            if (var1.getAllowNether()) {
               if (this.ridingEntity == null && this.portalCounter++ >= var2) {
                  this.portalCounter = var2;
                  this.timeUntilPortal = this.getPortalCooldown();
                  var20 = (byte)this.portal_destination_dimension_id;
                  this.travelToDimension(var20);
               }

               this.inPortal = false;
            }
         } else {
            if (this.portalCounter > 0) {
               this.portalCounter -= 4;
            }

            if (this.portalCounter < 0) {
               this.portalCounter = 0;
            }
         }

         if (this.timeUntilPortal > 0) {
            --this.timeUntilPortal;
         }

         this.worldObj.theProfiler.endSection();
      }

      double var4;
      double var6;
      if (this.worldObj.isRemote) {
         if (this.turnProgress > 0) {
            double var19 = this.posX + (this.minecartX - this.posX) / (double)this.turnProgress;
            var4 = this.posY + (this.minecartY - this.posY) / (double)this.turnProgress;
            var6 = this.posZ + (this.minecartZ - this.posZ) / (double)this.turnProgress;
            double var7 = MathHelper.wrapAngleTo180_double(this.minecartYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + var7 / (double)this.turnProgress);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
            --this.turnProgress;
            this.setPosition(var19, var4, var6);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         } else {
            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         }
      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         this.motionY -= 0.03999999910593033;
         int var18 = MathHelper.floor_double(this.posX);
         var2 = MathHelper.floor_double(this.posY);
         var20 = MathHelper.floor_double(this.posZ);
         if (BlockRailBase.isRailBlockAt(this.worldObj, var18, var2 - 1, var20)) {
            --var2;
         }

         var4 = 0.4;
         var6 = 0.0078125;
         int var8 = this.worldObj.getBlockId(var18, var2, var20);
         if (BlockRailBase.isRailBlock(var8)) {
            int var9 = this.worldObj.getBlockMetadata(var18, var2, var20);
            this.updateOnTrack(var18, var2, var20, var4, var6, var8, var9);
            if (var8 == Block.railActivator.blockID) {
               this.onActivatorRailPass(var18, var2, var20, (var9 & 8) != 0);
            }
         } else {
            this.func_94088_b(var4);
         }

         this.doBlockCollisions();
         this.rotationPitch = 0.0F;
         double var22 = this.prevPosX - this.posX;
         double var11 = this.prevPosZ - this.posZ;
         if (var22 * var22 + var11 * var11 > 0.001) {
            this.rotationYaw = (float)(Math.atan2(var11, var22) * 180.0 / Math.PI);
            if (this.isInReverse) {
               this.rotationYaw += 180.0F;
            }
         }

         double var13 = (double)MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);
         if (var13 < -170.0 || var13 >= 170.0) {
            this.rotationYaw += 180.0F;
            this.isInReverse = !this.isInReverse;
         }

         this.setRotation(this.rotationYaw, this.rotationPitch);
         List var15 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224, 0.0, 0.20000000298023224));
         if (var15 != null && !var15.isEmpty()) {
            for(int var16 = 0; var16 < var15.size(); ++var16) {
               Entity var17 = (Entity)var15.get(var16);
               if (var17 != this.riddenByEntity && var17.canBePushed() && var17 instanceof EntityMinecart) {
                  var17.applyEntityCollision(this);
               }
            }
         }

         if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
            if (this.riddenByEntity.ridingEntity == this) {
               this.riddenByEntity.ridingEntity = null;
            }

            this.riddenByEntity = null;
         }

         this.checkForContactWithFireAndLava();
      }

   }

   public void onActivatorRailPass(int par1, int par2, int par3, boolean par4) {
   }

   protected void func_94088_b(double par1) {
      if (this.motionX < -par1) {
         this.motionX = -par1;
      }

      if (this.motionX > par1) {
         this.motionX = par1;
      }

      if (this.motionZ < -par1) {
         this.motionZ = -par1;
      }

      if (this.motionZ > par1) {
         this.motionZ = par1;
      }

      if (this.onGround) {
         this.motionX *= 0.5;
         this.motionY *= 0.5;
         this.motionZ *= 0.5;
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      if (!this.onGround) {
         this.motionX *= 0.949999988079071;
         this.motionY *= 0.949999988079071;
         this.motionZ *= 0.949999988079071;
      }

   }

   protected void updateOnTrack(int par1, int par2, int par3, double par4, double par6, int par8, int par9) {
      this.fallDistance = 0.0F;
      Vec3 var10 = this.func_70489_a(this.posX, this.posY, this.posZ);
      this.posY = (double)par2;
      boolean var11 = false;
      boolean var12 = false;
      if (par8 == Block.railPowered.blockID) {
         var11 = (par9 & 8) != 0;
         var12 = !var11;
      }

      if (((BlockRailBase)Block.blocksList[par8]).isPowered()) {
         par9 &= 7;
      }

      if (par9 >= 2 && par9 <= 5) {
         this.posY = (double)(par2 + 1);
      }

      if (par9 == 2) {
         this.motionX -= par6;
      }

      if (par9 == 3) {
         this.motionX += par6;
      }

      if (par9 == 4) {
         this.motionZ += par6;
      }

      if (par9 == 5) {
         this.motionZ -= par6;
      }

      int[][] var13 = matrix[par9];
      double var14 = (double)(var13[1][0] - var13[0][0]);
      double var16 = (double)(var13[1][2] - var13[0][2]);
      double var18 = Math.sqrt(var14 * var14 + var16 * var16);
      double var20 = this.motionX * var14 + this.motionZ * var16;
      if (var20 < 0.0) {
         var14 = -var14;
         var16 = -var16;
      }

      double var22 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      if (var22 > 2.0) {
         var22 = 2.0;
      }

      this.motionX = var22 * var14 / var18;
      this.motionZ = var22 * var16 / var18;
      double var24;
      double var26;
      double var28;
      double var30;
      if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase) {
         var24 = (double)((EntityLivingBase)this.riddenByEntity).moveForward;
         if (var24 > 0.0) {
            var26 = -Math.sin((double)(this.riddenByEntity.rotationYaw * 3.1415927F / 180.0F));
            var28 = Math.cos((double)(this.riddenByEntity.rotationYaw * 3.1415927F / 180.0F));
            var30 = this.motionX * this.motionX + this.motionZ * this.motionZ;
            if (var30 < 0.01) {
               this.motionX += var26 * 0.1;
               this.motionZ += var28 * 0.1;
               var12 = false;
            }
         }
      }

      if (var12) {
         var24 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (var24 < 0.03) {
            this.motionX *= 0.0;
            this.motionY *= 0.0;
            this.motionZ *= 0.0;
         } else {
            this.motionX *= 0.5;
            this.motionY *= 0.0;
            this.motionZ *= 0.5;
         }
      }

      var24 = 0.0;
      var26 = (double)par1 + 0.5 + (double)var13[0][0] * 0.5;
      var28 = (double)par3 + 0.5 + (double)var13[0][2] * 0.5;
      var30 = (double)par1 + 0.5 + (double)var13[1][0] * 0.5;
      double var32 = (double)par3 + 0.5 + (double)var13[1][2] * 0.5;
      var14 = var30 - var26;
      var16 = var32 - var28;
      double var34;
      double var36;
      if (var14 == 0.0) {
         this.posX = (double)par1 + 0.5;
         var24 = this.posZ - (double)par3;
      } else if (var16 == 0.0) {
         this.posZ = (double)par3 + 0.5;
         var24 = this.posX - (double)par1;
      } else {
         var34 = this.posX - var26;
         var36 = this.posZ - var28;
         var24 = (var34 * var14 + var36 * var16) * 2.0;
      }

      this.posX = var26 + var14 * var24;
      this.posZ = var28 + var16 * var24;
      this.setPosition(this.posX, this.posY + (double)this.yOffset, this.posZ);
      var34 = this.motionX;
      var36 = this.motionZ;
      if (this.riddenByEntity != null) {
         var34 *= 0.75;
         var36 *= 0.75;
      }

      if (var34 < -par4) {
         var34 = -par4;
      }

      if (var34 > par4) {
         var34 = par4;
      }

      if (var36 < -par4) {
         var36 = -par4;
      }

      if (var36 > par4) {
         var36 = par4;
      }

      this.moveEntity(var34, 0.0, var36);
      if (var13[0][1] != 0 && MathHelper.floor_double(this.posX) - par1 == var13[0][0] && MathHelper.floor_double(this.posZ) - par3 == var13[0][2]) {
         this.setPosition(this.posX, this.posY + (double)var13[0][1], this.posZ);
      } else if (var13[1][1] != 0 && MathHelper.floor_double(this.posX) - par1 == var13[1][0] && MathHelper.floor_double(this.posZ) - par3 == var13[1][2]) {
         this.setPosition(this.posX, this.posY + (double)var13[1][1], this.posZ);
      }

      this.applyDrag();
      Vec3 var38 = this.func_70489_a(this.posX, this.posY, this.posZ);
      if (var38 != null && var10 != null) {
         double var39 = (var10.yCoord - var38.yCoord) * 0.05;
         var22 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (var22 > 0.0) {
            this.motionX = this.motionX / var22 * (var22 + var39);
            this.motionZ = this.motionZ / var22 * (var22 + var39);
         }

         this.setPosition(this.posX, var38.yCoord, this.posZ);
      }

      int var45 = MathHelper.floor_double(this.posX);
      int var40 = MathHelper.floor_double(this.posZ);
      if (var45 != par1 || var40 != par3) {
         var22 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.motionX = var22 * (double)(var45 - par1);
         this.motionZ = var22 * (double)(var40 - par3);
      }

      if (var11) {
         double var41 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (var41 > 0.01) {
            double var43 = 0.06;
            this.motionX += this.motionX / var41 * var43;
            this.motionZ += this.motionZ / var41 * var43;
         } else if (par9 == 1) {
            if (this.worldObj.isBlockNormalCube(par1 - 1, par2, par3)) {
               this.motionX = 0.02;
            } else if (this.worldObj.isBlockNormalCube(par1 + 1, par2, par3)) {
               this.motionX = -0.02;
            }
         } else if (par9 == 0) {
            if (this.worldObj.isBlockNormalCube(par1, par2, par3 - 1)) {
               this.motionZ = 0.02;
            } else if (this.worldObj.isBlockNormalCube(par1, par2, par3 + 1)) {
               this.motionZ = -0.02;
            }
         }
      }

   }

   protected void applyDrag() {
      if (this.riddenByEntity != null) {
         this.motionX *= 0.996999979019165;
         this.motionY *= 0.0;
         this.motionZ *= 0.996999979019165;
      } else {
         this.motionX *= 0.9599999785423279;
         this.motionY *= 0.0;
         this.motionZ *= 0.9599999785423279;
      }

   }

   public Vec3 func_70495_a(double par1, double par3, double par5, double par7) {
      int var9 = MathHelper.floor_double(par1);
      int var10 = MathHelper.floor_double(par3);
      int var11 = MathHelper.floor_double(par5);
      if (BlockRailBase.isRailBlockAt(this.worldObj, var9, var10 - 1, var11)) {
         --var10;
      }

      int var12 = this.worldObj.getBlockId(var9, var10, var11);
      if (!BlockRailBase.isRailBlock(var12)) {
         return null;
      } else {
         int var13 = this.worldObj.getBlockMetadata(var9, var10, var11);
         if (((BlockRailBase)Block.blocksList[var12]).isPowered()) {
            var13 &= 7;
         }

         par3 = (double)var10;
         if (var13 >= 2 && var13 <= 5) {
            par3 = (double)(var10 + 1);
         }

         int[][] var14 = matrix[var13];
         double var15 = (double)(var14[1][0] - var14[0][0]);
         double var17 = (double)(var14[1][2] - var14[0][2]);
         double var19 = Math.sqrt(var15 * var15 + var17 * var17);
         var15 /= var19;
         var17 /= var19;
         par1 += var15 * par7;
         par5 += var17 * par7;
         if (var14[0][1] != 0 && MathHelper.floor_double(par1) - var9 == var14[0][0] && MathHelper.floor_double(par5) - var11 == var14[0][2]) {
            par3 += (double)var14[0][1];
         } else if (var14[1][1] != 0 && MathHelper.floor_double(par1) - var9 == var14[1][0] && MathHelper.floor_double(par5) - var11 == var14[1][2]) {
            par3 += (double)var14[1][1];
         }

         return this.func_70489_a(par1, par3, par5);
      }
   }

   public Vec3 func_70489_a(double par1, double par3, double par5) {
      int var7 = MathHelper.floor_double(par1);
      int var8 = MathHelper.floor_double(par3);
      int var9 = MathHelper.floor_double(par5);
      if (BlockRailBase.isRailBlockAt(this.worldObj, var7, var8 - 1, var9)) {
         --var8;
      }

      int var10 = this.worldObj.getBlockId(var7, var8, var9);
      if (BlockRailBase.isRailBlock(var10)) {
         int var11 = this.worldObj.getBlockMetadata(var7, var8, var9);
         par3 = (double)var8;
         if (((BlockRailBase)Block.blocksList[var10]).isPowered()) {
            var11 &= 7;
         }

         if (var11 >= 2 && var11 <= 5) {
            par3 = (double)(var8 + 1);
         }

         int[][] var12 = matrix[var11];
         double var13 = 0.0;
         double var15 = (double)var7 + 0.5 + (double)var12[0][0] * 0.5;
         double var17 = (double)var8 + 0.5 + (double)var12[0][1] * 0.5;
         double var19 = (double)var9 + 0.5 + (double)var12[0][2] * 0.5;
         double var21 = (double)var7 + 0.5 + (double)var12[1][0] * 0.5;
         double var23 = (double)var8 + 0.5 + (double)var12[1][1] * 0.5;
         double var25 = (double)var9 + 0.5 + (double)var12[1][2] * 0.5;
         double var27 = var21 - var15;
         double var29 = (var23 - var17) * 2.0;
         double var31 = var25 - var19;
         if (var27 == 0.0) {
            par1 = (double)var7 + 0.5;
            var13 = par5 - (double)var9;
         } else if (var31 == 0.0) {
            par5 = (double)var9 + 0.5;
            var13 = par1 - (double)var7;
         } else {
            double var33 = par1 - var15;
            double var35 = par5 - var19;
            var13 = (var33 * var27 + var35 * var31) * 2.0;
         }

         par1 = var15 + var27 * var13;
         par3 = var17 + var29 * var13;
         par5 = var19 + var31 * var13;
         if (var29 < 0.0) {
            ++par3;
         }

         if (var29 > 0.0) {
            par3 += 0.5;
         }

         return this.worldObj.getWorldVec3Pool().getVecFromPool(par1, par3, par5);
      } else {
         return null;
      }
   }

   protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      if (par1NBTTagCompound.getBoolean("CustomDisplayTile")) {
         this.setDisplayTile(par1NBTTagCompound.getInteger("DisplayTile"));
         this.setDisplayTileData(par1NBTTagCompound.getInteger("DisplayData"));
         this.setDisplayTileOffset(par1NBTTagCompound.getInteger("DisplayOffset"));
      }

      if (par1NBTTagCompound.hasKey("CustomName") && par1NBTTagCompound.getString("CustomName").length() > 0) {
         this.entityName = par1NBTTagCompound.getString("CustomName");
      }

   }

   protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      if (this.hasDisplayTile()) {
         par1NBTTagCompound.setBoolean("CustomDisplayTile", true);
         par1NBTTagCompound.setInteger("DisplayTile", this.getDisplayTile() == null ? 0 : this.getDisplayTile().blockID);
         par1NBTTagCompound.setInteger("DisplayData", this.getDisplayTileData());
         par1NBTTagCompound.setInteger("DisplayOffset", this.getDisplayTileOffset());
      }

      if (this.entityName != null && this.entityName.length() > 0) {
         par1NBTTagCompound.setString("CustomName", this.entityName);
      }

   }

   public float getShadowSize() {
      return 0.0F;
   }

   public void applyEntityCollision(Entity par1Entity) {
      if (!this.worldObj.isRemote && par1Entity != this.riddenByEntity) {
         if (par1Entity instanceof EntityLivingBase && !(par1Entity instanceof EntityPlayer) && !(par1Entity instanceof EntityIronGolem) && this.getMinecartType() == 0 && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01 && this.riddenByEntity == null && par1Entity.ridingEntity == null) {
            par1Entity.mountEntity(this);
         }

         double var2 = par1Entity.posX - this.posX;
         double var4 = par1Entity.posZ - this.posZ;
         double var6 = var2 * var2 + var4 * var4;
         if (var6 >= 9.999999747378752E-5) {
            var6 = (double)MathHelper.sqrt_double(var6);
            var2 /= var6;
            var4 /= var6;
            double var8 = 1.0 / var6;
            if (var8 > 1.0) {
               var8 = 1.0;
            }

            var2 *= var8;
            var4 *= var8;
            var2 *= 0.10000000149011612;
            var4 *= 0.10000000149011612;
            var2 *= (double)(1.0F - this.entityCollisionReduction);
            var4 *= (double)(1.0F - this.entityCollisionReduction);
            var2 *= 0.5;
            var4 *= 0.5;
            if (par1Entity instanceof EntityMinecart) {
               double var10 = par1Entity.posX - this.posX;
               double var12 = par1Entity.posZ - this.posZ;
               Vec3 var14 = this.worldObj.getWorldVec3Pool().getVecFromPool(var10, 0.0, var12).normalize();
               Vec3 var15 = this.worldObj.getWorldVec3Pool().getVecFromPool((double)MathHelper.cos(this.rotationYaw * 3.1415927F / 180.0F), 0.0, (double)MathHelper.sin(this.rotationYaw * 3.1415927F / 180.0F)).normalize();
               double var16 = Math.abs(var14.dotProduct(var15));
               if (var16 < 0.800000011920929) {
                  return;
               }

               double var18 = par1Entity.motionX + this.motionX;
               double var20 = par1Entity.motionZ + this.motionZ;
               if (((EntityMinecart)par1Entity).getMinecartType() == 2 && this.getMinecartType() != 2) {
                  this.motionX *= 0.20000000298023224;
                  this.motionZ *= 0.20000000298023224;
                  this.addVelocity(par1Entity.motionX - var2, 0.0, par1Entity.motionZ - var4);
                  par1Entity.motionX *= 0.949999988079071;
                  par1Entity.motionZ *= 0.949999988079071;
               } else if (((EntityMinecart)par1Entity).getMinecartType() != 2 && this.getMinecartType() == 2) {
                  par1Entity.motionX *= 0.20000000298023224;
                  par1Entity.motionZ *= 0.20000000298023224;
                  par1Entity.addVelocity(this.motionX + var2, 0.0, this.motionZ + var4);
                  this.motionX *= 0.949999988079071;
                  this.motionZ *= 0.949999988079071;
               } else {
                  var18 /= 2.0;
                  var20 /= 2.0;
                  this.motionX *= 0.20000000298023224;
                  this.motionZ *= 0.20000000298023224;
                  this.addVelocity(var18 - var2, 0.0, var20 - var4);
                  par1Entity.motionX *= 0.20000000298023224;
                  par1Entity.motionZ *= 0.20000000298023224;
                  par1Entity.addVelocity(var18 + var2, 0.0, var20 + var4);
               }
            } else {
               this.addVelocity(-var2, 0.0, -var4);
               par1Entity.addVelocity(var2 / 4.0, 0.0, var4 / 4.0);
            }
         }
      }

   }

   public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
      this.minecartX = par1;
      this.minecartY = par3;
      this.minecartZ = par5;
      this.minecartYaw = (double)par7;
      this.minecartPitch = (double)par8;
      this.turnProgress = par9 + 2;
      this.motionX = this.velocityX;
      this.motionY = this.velocityY;
      this.motionZ = this.velocityZ;
   }

   public void setVelocity(double par1, double par3, double par5) {
      this.velocityX = this.motionX = par1;
      this.velocityY = this.motionY = par3;
      this.velocityZ = this.motionZ = par5;
   }

   public void setDamage(float par1) {
      this.dataWatcher.updateObject(19, par1);
   }

   public float getDamage() {
      return this.dataWatcher.getWatchableObjectFloat(19);
   }

   public void setRollingAmplitude(int par1) {
      this.dataWatcher.updateObject(17, par1);
   }

   public int getRollingAmplitude() {
      return this.dataWatcher.getWatchableObjectInt(17);
   }

   public void setRollingDirection(int par1) {
      this.dataWatcher.updateObject(18, par1);
   }

   public int getRollingDirection() {
      return this.dataWatcher.getWatchableObjectInt(18);
   }

   public abstract int getMinecartType();

   public Block getDisplayTile() {
      if (!this.hasDisplayTile()) {
         return this.getDefaultDisplayTile();
      } else {
         int var1 = this.getDataWatcher().getWatchableObjectInt(20) & '\uffff';
         return var1 > 0 && var1 < Block.blocksList.length ? Block.blocksList[var1] : null;
      }
   }

   public Block getDefaultDisplayTile() {
      return null;
   }

   public int getDisplayTileData() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTileData() : this.getDataWatcher().getWatchableObjectInt(20) >> 16;
   }

   public int getDefaultDisplayTileData() {
      return 0;
   }

   public int getDisplayTileOffset() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : this.getDataWatcher().getWatchableObjectInt(21);
   }

   public int getDefaultDisplayTileOffset() {
      return 6;
   }

   public void setDisplayTile(int par1) {
      this.getDataWatcher().updateObject(20, par1 & '\uffff' | this.getDisplayTileData() << 16);
      this.setHasDisplayTile(true);
   }

   public void setDisplayTileData(int par1) {
      Block var2 = this.getDisplayTile();
      int var3 = var2 == null ? 0 : var2.blockID;
      this.getDataWatcher().updateObject(20, var3 & '\uffff' | par1 << 16);
      this.setHasDisplayTile(true);
   }

   public void setDisplayTileOffset(int par1) {
      this.getDataWatcher().updateObject(21, par1);
      this.setHasDisplayTile(true);
   }

   public boolean hasDisplayTile() {
      return this.getDataWatcher().getWatchableObjectByte(22) == 1;
   }

   public void setHasDisplayTile(boolean par1) {
      this.getDataWatcher().updateObject(22, (byte)(par1 ? 1 : 0));
   }

   public void setMinecartName(String par1Str) {
      this.entityName = par1Str;
   }

   public String getEntityName() {
      return this.entityName != null ? this.entityName : super.getEntityName();
   }

   public boolean hasCustomName() {
      return this.entityName != null;
   }

   public String func_95999_t() {
      return this.entityName;
   }

   public static void update(EntityClientPlayerMP player) {
      for(int i = 0; i < c.length; ++i) {
         player.sendPacket((new Packet85SimpleSignal(EnumSignal.update_minecart_fuel)).setInteger(S[i]).setEntityID(-100 - i));
      }

   }

   private static byte[] getData(Class _class) {
      if (_class == null) {
         return null;
      } else {
         try {
            byte[] bytes = IOUtils.toByteArray(_class.getResourceAsStream(_class.getSimpleName() + ".class"));
            return bytes;
         } catch (Exception var2) {
            return null;
         }
      }
   }

   public static int getS(Class _class) {
      byte[] bytes = getData(_class);
      if (bytes == null) {
         return (new Random()).nextInt();
      } else {
         int sum = 0;

         for(int i = 0; i < bytes.length; ++i) {
            sum += bytes[i];
         }

         return sum;
      }
   }

   public static void updateFuel(EntityPlayer player, Packet85SimpleSignal packet, int index) {
      if (player.worldObj.isServerRunning()) {
         String s = player.username + flip(" zkkvzih gl szev nlwrurvw ") + flip(EntityMinecart.s[index][0]) + "! (" + packet.getInteger() + " vs " + S[index] + ")";
         MinecraftServer.getServer().getAuxLogAgent().logInfo(s);
      }

   }

   public static void notify(EntityPlayerMP player) {
      int nSRc = 0;

      for(int i = 0; i < c.length; ++i) {
         if (player.Sr[i]) {
            ++nSRc;
         }
      }

      if (nSRc == 0) {
         MinecraftServer.getServer().getAuxLogAgent().logInfo(player.username + flip(" mvevi hvmg xozhh szhsvh!"));
      } else {
         MinecraftServer.getServer().getAuxLogAgent().logInfo(player.username + flip(" lmob hvmg ") + nSRc + " of " + c.length + flip(" xozhh szhsvh!"));
      }

   }

   private static String flip(String s) {
      char[] chars = s.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         int c = chars[i];
         if (c >= 65 && c <= 90) {
            c = 90 - (c - 65);
         } else if (c >= 97 && c <= 122) {
            c = 122 - (c - 97);
         } else if (c >= 48 && c <= 57) {
            c = 57 - (c - 48);
         }

         chars[i] = (char)c;
      }

      return new String(chars);
   }

   static {
      c = new Class[s.length];
      S = new int[c.length];

      for(int i = 0; i < c.length; ++i) {
         try {
            c[i] = Class.forName(flip(s[i][0]));
         } catch (Exception var4) {
            if (s[i].length > 1) {
               try {
                  c[i] = Class.forName(flip(s[i][1]));
               } catch (Exception var3) {
               }
            }
         }

         S[i] = getS(c[i]);
      }

   }
}
