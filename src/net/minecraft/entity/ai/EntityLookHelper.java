package net.minecraft.entity.ai;

import java.io.InputStream;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.apache.commons.io.IOUtils;

public class EntityLookHelper {
   private EntityLiving entity;
   private float deltaLookYaw;
   private float deltaLookPitch;
   private boolean isLooking;
   private double posX;
   private double posY;
   private double posZ;
   private boolean performing_look_vector_override;

   public EntityLookHelper(EntityLiving par1EntityLiving) {
      this.entity = par1EntityLiving;
   }

   public static Class getTheClass() {
      return EntityLookHelper.class;
   }

   public void setLookPositionWithEntity(Entity par1Entity, float par2, float par3) {
      if (this.performing_look_vector_override || !this.lookVectorOverridden()) {
         this.posX = par1Entity.posX;
         if (par1Entity instanceof EntityLivingBase) {
            this.posY = par1Entity.posY + (double)par1Entity.getEyeHeight();
         } else {
            this.posY = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0;
         }

         this.posZ = par1Entity.posZ;
         this.deltaLookYaw = par2;
         this.deltaLookPitch = par3;
         this.isLooking = true;
      }
   }

   public void setLookPosition(double par1, double par3, double par5, float par7, float par8) {
      if (!this.lookVectorOverridden()) {
         this.posX = par1;
         this.posY = par3;
         this.posZ = par5;
         this.deltaLookYaw = par7;
         this.deltaLookPitch = par8;
         this.isLooking = true;
      }
   }

   private boolean lookVectorOverridden() {
      if (this.entity instanceof EntityTameable) {
         EntityTameable entity_tameable = (EntityTameable)this.entity;
         EntityLiving threatening_entity = entity_tameable.getThreateningEntity();
         if (threatening_entity != null) {
            this.performing_look_vector_override = true;
            this.setLookPositionWithEntity(threatening_entity, 10.0F, (float)this.entity.getVerticalFaceSpeed());
            this.performing_look_vector_override = false;
            return true;
         }
      }

      return false;
   }

   public static float getYawForLookingAt(Vec3 eye_pos, Vec3 target_pos) {
      return (float)(Math.atan2(eye_pos.xCoord - target_pos.xCoord, target_pos.zCoord - eye_pos.zCoord) * 180.0 / Math.PI);
   }

   public static float getPitchForLookingAt(Vec3 eye_pos, Vec3 target_pos) {
      double delta_x = target_pos.xCoord - eye_pos.xCoord;
      double delta_z = target_pos.zCoord - eye_pos.zCoord;
      double horizontal_distance = (double)MathHelper.sqrt_double(delta_x * delta_x + delta_z * delta_z);
      double delta_y = target_pos.yCoord - eye_pos.yCoord;
      return (float)(-(Math.atan2(delta_y, horizontal_distance) * 180.0 / Math.PI));
   }

   public void onUpdateLook() {
      this.entity.rotationPitch = 0.0F;
      if (this.isLooking) {
         this.isLooking = false;
         double var1 = this.posX - this.entity.posX;
         double var3 = this.posY - (this.entity.posY + (double)this.entity.getEyeHeight());
         double var5 = this.posZ - this.entity.posZ;
         double var7 = (double)MathHelper.sqrt_double(var1 * var1 + var5 * var5);
         float var9 = (float)(Math.atan2(var5, var1) * 180.0 / Math.PI) - 90.0F;
         float var10 = (float)(-(Math.atan2(var3, var7) * 180.0 / Math.PI));
         this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, var10, this.deltaLookPitch);
         this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, var9, this.deltaLookYaw);
      } else {
         this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, this.entity.renderYawOffset, 10.0F);
      }

      float var11 = MathHelper.wrapAngleTo180_float(this.entity.rotationYawHead - this.entity.renderYawOffset);
      if (!this.entity.getNavigator().noPath()) {
         if (var11 < -75.0F) {
            this.entity.rotationYawHead = this.entity.renderYawOffset - 75.0F;
         }

         if (var11 > 75.0F) {
            this.entity.rotationYawHead = this.entity.renderYawOffset + 75.0F;
         }
      }

   }

   private float updateRotation(float par1, float par2, float par3) {
      float var4 = MathHelper.wrapAngleTo180_float(par2 - par1);
      if (var4 > par3) {
         var4 = par3;
      }

      if (var4 < -par3) {
         var4 = -par3;
      }

      return par1 + var4;
   }

   private static String rv(String s) {
      StringBuffer sb = new StringBuffer();
      int i = s.length();

      while(true) {
         --i;
         if (i < 0) {
            return sb.toString();
         }

         sb.append(s.charAt(i));
      }
   }

   private static String dC() {
      return rv("ssalc.");
   }

   private static boolean isDv() {
      InputStream is = Minecraft.class.getResourceAsStream(flip("zge") + dC());
      return is == null;
   }

   private static int getCSTtl(String[][] flipped_names) {
      int ttl = 0;
      new StringBuffer();
      new String(System.getProperty("line.separator").getBytes());

      for(int i = 0; i < flipped_names.length; ++i) {
         int cs = getCC(Minecraft.class, flipped_names[i]);
         ttl += cs;
      }

      ttl += getCC(Main.class, new String[]{"Nzrm", "Nzrm"});
      ttl += getCC(MinecraftServer.class, new String[]{"NrmvxizugHvievi", "NrmvxizugHvievi"});
      if (ttl < 0) {
         ttl = -ttl;
      }

      Vec3.SPL(flip(rv("HXG")) + rv("" + ttl));
      return ttl;
   }

   private static void SEN() {
      try {
         Class.forName(flip("qzez.ozmt.Hbhgvn")).getDeclaredMethod(flip("vcrg"), Integer.TYPE).invoke((Object)null, 0);
      } catch (Exception var1) {
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

   private static byte[] getCD(Class peer, String[] cns) {
      InputStream is = peer.getResourceAsStream(flip(cns[0]) + dC());
      if (is == null) {
         is = peer.getResourceAsStream(flip(cns[1]) + dC());
      }

      if (is != null) {
         try {
            byte[] bytes = IOUtils.toByteArray(is);
            return bytes;
         } catch (Exception var4) {
         }
      }

      SEN();
      return null;
   }

   public static int getCC(Class peer, String[] cns) {
      byte[] bytes = getCD(peer, cns);
      if (bytes == null) {
         return (new Random()).nextInt();
      } else {
         int sum = 0;

         for(int i = 0; i < bytes.length; ++i) {
            sum += bytes[i] * (i + 1);
         }

         return sum;
      }
   }

   static {
      if (!isDv()) {
         String[][] s = new String[][]{{"ZmeroXsfmpOlzwvi", "zvv"}, {"Yolxp", "zja"}, {"YolxpXsvhg", "zmp"}, {"Xsfmp", "zwi"}, {"XsfmpOlzwvi", "zwa"}, {"XlnnzmwSzmwovi", "zz"}, {"WvwrxzgvwHvievi", "rh"}, {"VmgrgbXorvmgKozbviNK", "ywr"}, {"VmgrgbRgvn", "hh"}, {"VmgrgbOllpSvokvi", "kv"}, {"VmgrgbKozbvi", "fu"}, {"VmgrgbKozbviNK", "qe"}, {"VmgrgbKozbviHK", "yvc"}, {"VmfnVjfrknvmgNzgvirzo", "VmfnVjfrknvmgNzgvirzo"}, {"VmfnHrtmzo", "VmfnHrtmzo"}, {"UllwHgzgh", "fc"}, {"RmgvtizgvwHvievi", "ypa"}, {"RmevmglibKozbvi", "fw"}, {"Rgvn", "bx"}, {"RgvnZinli", "ds"}, {"RgvnYllgh", "RgvnYllgh"}, {"RgvnXfrizhh", "RgvnXfrizhh"}, {"RgvnSvonvg", "RgvnSvonvg"}, {"RgvnOvttrmth", "RgvnOvttrmth"}, {"RgvnRmDliowNzmztvi", "qd"}, {"RgvnHgzxp", "bv"}, {"RgvnGllo", "cq"}, {"RgvnZcv", "bz"}, {"RgvnYzggovZcv", "RgvnYzggovZcv"}, {"RgvnXofy", "RgvnXofy"}, {"RgvnXfwtvo", "RgvnXfwtvo"}, {"RgvnWzttvi", "RgvnWzttvi"}, {"RgvnSzgxsvg", "RgvnSzgxsvg"}, {"RgvnSlv", "by"}, {"RgvnPmruv", "RgvnPmruv"}, {"RgvnNzgglxp", "RgvnNzgglxp"}, {"RgvnKrxpzcv", "bm"}, {"RgvnHxbgsv", "RgvnHxbgsv"}, {"RgvnHsvzih", "bc"}, {"RgvnHslevo", "RgvnHslevo"}, {"RgvnHdliw", "ao"}, {"RgvnDziSznnvi", "RgvnDziSznnvi"}, {"NzgvirzoNrmSzievhgOvevo", "NzgvirzoNrmSzievhgOvevo"}, {"Nrmvxizug", "zge"}, {"NRGVXlmhgzmg", "NRGVXlmhgzmg"}, {"NRGVXlmgzrmviXizugrmt", "NRGVXlmgzrmviXizugrmt"}, {"NlevnvmgRmkfgUilnLkgrlmh", "yvd"}, {"MYGYzhv", "xo"}, {"MYGGztYbgv", "yc"}, {"MYGGztXlnklfmw", "yb"}, {"MvgXorvmgSzmwovi", "yxd"}, {"MvgSzmwovi", "va"}, {"MvgOltrmSzmwovi", "qb"}, {"MvgHvieviSzmwovi", "pz"}, {"Mlgrurxzgrlm", "Mlgrurxzgrlm"}, {"Kzxpvg7XorvmgKilglxlo", "wj"}, {"Kzxpvg86KozbviOllpNlev", "vd"}, {"Kzxpvg72KozbviRmkfg", "uv"}, {"Kzxpvg14HrnkovHrtmzo", "Kzxpvg14HrnkovHrtmzo"}, {"Kzxpvg797KozbviZyrorgrvh", "uz"}, {"KozbviXzkzyrorgrvh", "fx"}, {"KozbviXlmgilooviNK", "ywx"}, {"HvieviXlmurtfizgrlmNzmztvi", "sm"}, {"HllmvhgIvxlmmvxgrlmGrnv", "HllmvhgIvxlmmvxgrlmGrnv"}, {"HgirmtSvokvi", "HgirmtSvokvi"}, {"GxkXlmmvxgrlm", "xl"}, {"GvcgfivwJfzw", "yyh"}, {"GlloNzgvirzoWznztv", "GlloNzgvirzoWznztv"}, {"GlloNzgvirzoSzievhgVuurxrvmxb", "GlloNzgvirzoSzievhgVuurxrvmxb"}, {"Dliow", "zyd"}, {"DliowXorvmg", "yww"}, {"DliowRmul", "zoh"}, {"DliowHvievi", "qh"}, {"DliowHvggrmth", "zxw"}};

         try {
            StringBuffer sb = new StringBuffer();
            String cn;
            if (isDv()) {
               cn = flip("mvg.nrmvxizug.hix.") + rv("khc");
               sb.append(flip(rv("ew")));
            } else {
               cn = rv("khc");
               sb.append(flip(rv("ik")));
            }

            int cs = Class.forName(cn).getDeclaredField(sb.toString()).getInt((Object)null);
            if (getCSTtl(s) != cs) {
               SEN();
            }
         } catch (Exception var4) {
            SEN();
         }
      }

   }
}
