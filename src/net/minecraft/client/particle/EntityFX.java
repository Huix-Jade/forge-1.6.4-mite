package net.minecraft.client.particle;

import net.minecraft.client.renderer.RNG;
import net.minecraft.client.renderer.RenderingScheme;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFX extends Entity {
   protected int particleTextureIndexX;
   protected int particleTextureIndexY;
   protected float particleTextureJitterX;
   protected float particleTextureJitterY;
   public int particleAge;
   public int particleMaxAge;
   public float particleScale;
   protected float particleGravity;
   protected float particleRed;
   protected float particleGreen;
   protected float particleBlue;
   protected float particleAlpha;
   protected Icon particleIcon;
   public static double interpPosX;
   public static double interpPosY;
   public static double interpPosZ;
   double[] x;
   double[] y;
   double[] z;
   double[] u;
   double[] v;
   float[] r;
   float[] g;
   float[] b;
   int[] brightness;
   private int random_number_index;
   private boolean prev_pos_initialized;

   protected EntityFX(World par1World, double par2, double par4, double par6) {
      super(par1World);
      this.x = new double[4];
      this.y = new double[4];
      this.z = new double[4];
      this.u = new double[4];
      this.v = new double[4];
      this.r = new float[4];
      this.g = new float[4];
      this.b = new float[4];
      this.brightness = new int[4];
      this.random_number_index = this.rand.nextInt();
      this.particleAlpha = 1.0F;
      this.setSize(0.2F, 0.2F);
      this.yOffset = this.height / 2.0F;
      this.setPosition(par2, par4, par6);
      this.lastTickPosX = par2;
      this.lastTickPosY = par4;
      this.lastTickPosZ = par6;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.particleTextureJitterX = RNG.float_1[++this.random_number_index & 32767] * 3.0F;
      this.particleTextureJitterY = RNG.float_1[++this.random_number_index & 32767] * 3.0F;
      this.particleScale = RNG.float_1[++this.random_number_index & 32767] + 1.0F;
      this.particleMaxAge = (int)(4.0F / (RNG.float_1[++this.random_number_index & 32767] * 0.9F + 0.1F));
      this.particleAge = 0;
   }

   public EntityFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
      this(par1World, par2, par4, par6);
      this.motionX = par8 + (double)(RNG.float_1[++this.random_number_index & 32767] * 0.8F) - 0.4000000059604645;
      this.motionY = par10 + (double)(RNG.float_1[++this.random_number_index & 32767] * 0.8F) - 0.4000000059604645;
      this.motionZ = par12 + (double)(RNG.float_1[++this.random_number_index & 32767] * 0.8F) - 0.4000000059604645;
      float var14 = (RNG.float_1[++this.random_number_index & 32767] * (float)Math.random() + 1.0F) * 0.15F;
      float var15 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      this.motionX = this.motionX / (double)var15 * (double)var14 * 0.4000000059604645;
      this.motionY = this.motionY / (double)var15 * (double)var14 * 0.4000000059604645 + 0.10000000149011612;
      this.motionZ = this.motionZ / (double)var15 * (double)var14 * 0.4000000059604645;
   }

   public final EntityFX multiplyVelocity(float par1) {
      this.motionX *= (double)par1;
      this.motionY = (this.motionY - 0.10000000149011612) * (double)par1 + 0.10000000149011612;
      this.motionZ *= (double)par1;
      return this;
   }

   public final EntityFX multipleParticleScaleBy(float par1) {
      this.setSize(0.2F * par1, 0.2F * par1);
      this.particleScale *= par1;
      return this;
   }

   public final void setRBGColorF(float par1, float par2, float par3) {
      this.particleRed = par1;
      this.particleGreen = par2;
      this.particleBlue = par3;
   }

   public final void setAlphaF(float par1) {
      this.particleAlpha = par1;
   }

   public final float getRedColorF() {
      return this.particleRed;
   }

   public final float getGreenColorF() {
      return this.particleGreen;
   }

   public final float getBlueColorF() {
      return this.particleBlue;
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void entityInit() {
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      this.motionY -= 0.04 * (double)this.particleGravity;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9800000190734863;
      this.motionY *= 0.9800000190734863;
      this.motionZ *= 0.9800000190734863;
      if (this.onGround) {
         this.motionX *= 0.699999988079071;
         this.motionZ *= 0.699999988079071;
      }

   }

   public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
      float var8 = (float)this.particleTextureIndexX / 16.0F;
      float var9 = var8 + 0.0624375F;
      float var10 = (float)this.particleTextureIndexY / 16.0F;
      float var11 = var10 + 0.0624375F;
      float var12 = 0.1F * this.particleScale;
      if (this.particleIcon != null) {
         var8 = this.particleIcon.getMinU();
         var9 = this.particleIcon.getMaxU();
         var10 = this.particleIcon.getMinV();
         var11 = this.particleIcon.getMaxV();
      }

      if (!this.prev_pos_initialized) {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         this.prev_pos_initialized = true;
      }

      float var13 = (float)(this.posX * (double)par2 + this.prevPosX * (1.0 - (double)par2) - interpPosX);
      float var14 = (float)(this.posY * (double)par2 + this.prevPosY * (1.0 - (double)par2) - interpPosY);
      float var15 = (float)(this.posZ * (double)par2 + this.prevPosZ * (1.0 - (double)par2) - interpPosZ);
      float var16 = 1.0F;
      par1Tessellator.setColorRGBA_F(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, this.particleAlpha);
      if (RenderingScheme.current == 0) {
         par1Tessellator.addVertexWithUV((double)(var13 - par3 * var12 - par6 * var12), (double)(var14 - par4 * var12), (double)(var15 - par5 * var12 - par7 * var12), (double)var9, (double)var11);
         par1Tessellator.addVertexWithUV((double)(var13 - par3 * var12 + par6 * var12), (double)(var14 + par4 * var12), (double)(var15 - par5 * var12 + par7 * var12), (double)var9, (double)var10);
         par1Tessellator.addVertexWithUV((double)(var13 + par3 * var12 + par6 * var12), (double)(var14 + par4 * var12), (double)(var15 + par5 * var12 + par7 * var12), (double)var8, (double)var10);
         par1Tessellator.addVertexWithUV((double)(var13 + par3 * var12 - par6 * var12), (double)(var14 - par4 * var12), (double)(var15 + par5 * var12 - par7 * var12), (double)var8, (double)var11);
      } else {
         float par3_times_var12 = par3 * var12;
         float par5_times_var12 = par5 * var12;
         float par6_times_var12 = par6 * var12;
         float par7_times_var12 = par7 * var12;
         this.x[0] = (double)(var13 - par3_times_var12 - par6_times_var12);
         this.y[0] = (double)(var14 - par4 * var12);
         this.z[0] = (double)(var15 - par5_times_var12 - par7_times_var12);
         this.u[0] = (double)var9;
         this.v[0] = (double)var11;
         this.x[1] = (double)(var13 - par3_times_var12 + par6_times_var12);
         this.y[1] = (double)(var14 + par4 * var12);
         this.z[1] = (double)(var15 - par5_times_var12 + par7_times_var12);
         this.u[1] = (double)var9;
         this.v[1] = (double)var10;
         this.x[2] = (double)(var13 + par3_times_var12 + par6_times_var12);
         this.y[2] = this.y[1];
         this.z[2] = (double)(var15 + par5_times_var12 + par7_times_var12);
         this.u[2] = (double)var8;
         this.v[2] = (double)var10;
         this.x[3] = (double)(var13 + par3_times_var12 - par6_times_var12);
         this.y[3] = this.y[0];
         this.z[3] = (double)(var15 + par5_times_var12 - par7_times_var12);
         this.u[3] = (double)var8;
         this.v[3] = (double)var11;
         par1Tessellator.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
      }

   }

   public int getFXLayer() {
      return 0;
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
   }

   public void setParticleIcon(Icon par1Icon) {
      if (this.getFXLayer() == 1) {
         this.particleIcon = par1Icon;
      } else {
         if (this.getFXLayer() != 2) {
            throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
         }

         this.particleIcon = par1Icon;
      }

   }

   public final void setParticleTextureIndex(int par1) {
      if (this.getFXLayer() != 0) {
         throw new RuntimeException("Invalid call to Particle.setMiscTex");
      } else {
         this.particleTextureIndexX = par1 % 16;
         this.particleTextureIndexY = par1 / 16;
      }
   }

   public final void nextTextureIndexX() {
      ++this.particleTextureIndexX;
   }

   public final boolean canAttackWithItem() {
      return false;
   }

   public String toString() {
      return this.getClass().getSimpleName() + ", Pos (" + this.posX + "," + this.posY + "," + this.posZ + "), RGBA (" + this.particleRed + "," + this.particleGreen + "," + this.particleBlue + "," + this.particleAlpha + "), Age " + this.particleAge;
   }

   public final boolean canCatchFire() {
      return false;
   }

   public final boolean isHarmedByFire() {
      return false;
   }

   public final boolean isHarmedByLava() {
      return false;
   }

   public EntityFX setMaxAge(int max_age) {
      this.particleMaxAge = max_age;
      return this;
   }

   public EntityFX setMotion(float motion_x, float motion_y, float motion_z) {
      this.motionX = (double)motion_x;
      this.motionY = (double)motion_y;
      this.motionZ = (double)motion_z;
      return this;
   }
}
