package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import org.lwjgl.opengl.GL11;

public abstract class RenderLiving extends RendererLivingEntity {
	public RenderLiving(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
		this.setTextures();
	}

	protected abstract void setTextures();

	protected boolean func_130007_b(EntityLiving par1EntityLiving) {
		return super.func_110813_b(par1EntityLiving) && (par1EntityLiving.getAlwaysRenderNameTagForRender() || par1EntityLiving.hasCustomNameTag() && par1EntityLiving == this.renderManager.field_96451_i);
	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		if (par1EntityLiving instanceof EntityCreeper) {
			par4 -= 0.11999999731779099;
		}

		super.doRenderLiving(par1EntityLiving, par2, par4, par6, par8, par9);
		this.func_110827_b(par1EntityLiving, par2, par4, par6, par8, par9);
	}

	private double func_110828_a(double par1, double par3, double par5) {
		return par1 + (par3 - par1) * par5;
	}

	protected void func_110827_b(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		Entity var10 = par1EntityLiving.getLeashedToEntity();
		if (var10 != null) {
			par4 -= (1.6 - (double)par1EntityLiving.height) * 0.5;
			boolean is_child = par1EntityLiving.isChild();
			float height_adjustment;
			if (par1EntityLiving instanceof EntityChicken) {
				height_adjustment = is_child ? -0.45F : -0.35F;
			} else if (par1EntityLiving instanceof EntitySheep) {
				height_adjustment = is_child ? -0.3F : -0.1F;
			} else if (par1EntityLiving instanceof EntityPig) {
				height_adjustment = is_child ? -0.4F : -0.3F;
			} else if (par1EntityLiving instanceof EntityCow) {
				height_adjustment = is_child ? -0.25F : -0.0F;
			} else if (par1EntityLiving instanceof EntityWolf) {
				height_adjustment = is_child ? -0.45F : -0.2F;
			} else if (par1EntityLiving instanceof EntityOcelot) {
				height_adjustment = is_child ? -0.55F : -0.45F;
			} else {
				height_adjustment = 0.0F;
			}

			Tessellator var11 = Tessellator.instance;
			double var12 = this.func_110828_a((double)var10.prevRotationYaw, (double)var10.rotationYaw, (double)(par9 * 0.5F)) * 0.01745329238474369;
			double var14 = this.func_110828_a((double)var10.prevRotationPitch, (double)var10.rotationPitch, (double)(par9 * 0.5F)) * 0.01745329238474369;
			double var16 = Math.cos(var12);
			double var18 = Math.sin(var12);
			double var20 = Math.sin(var14);
			if (var10 instanceof EntityHanging) {
				var16 = 0.0;
				var18 = 0.0;
				var20 = -1.0;
			}

			double var22 = Math.cos(var14);
			double var24 = this.func_110828_a(var10.prevPosX, var10.posX, (double)par9) - var16 * 0.7 - var18 * 0.5 * var22;
			double var26 = this.func_110828_a(var10.prevPosY + (double)var10.getEyeHeight() * 0.7, var10.posY + (double)var10.getEyeHeight() * 0.7, (double)par9) - var20 * 0.5 - 0.25;
			double var28 = this.func_110828_a(var10.prevPosZ, var10.posZ, (double)par9) - var18 * 0.7 + var16 * 0.5 * var22;
			double var30 = this.func_110828_a((double)par1EntityLiving.prevRenderYawOffset, (double)par1EntityLiving.renderYawOffset, (double)par9) * 0.01745329238474369 + 1.5707963267948966;
			var16 = Math.cos(var30) * (double)par1EntityLiving.width * 0.4;
			var18 = Math.sin(var30) * (double)par1EntityLiving.width * 0.4;
			double var32 = this.func_110828_a(par1EntityLiving.prevPosX, par1EntityLiving.posX, (double)par9) + var16;
			double var34 = this.func_110828_a(par1EntityLiving.prevPosY, par1EntityLiving.posY, (double)par9);
			double var36 = this.func_110828_a(par1EntityLiving.prevPosZ, par1EntityLiving.posZ, (double)par9) + var18;
			par2 += var16;
			par6 += var18;
			double var38 = (double)((float)(var24 - var32));
			double var40 = (double)((float)(var26 - var34));
			double var42 = (double)((float)(var28 - var36));
			GL11.glDisable(3553);
			GL11.glDisable(2896);
			GL11.glDisable(2884);
			boolean var44 = true;
			double var45 = 0.025;
			var11.startDrawing(5);

			int var47;
			float var48;
			float height_adjustment_fraction;
			double y_animal;
			for(var47 = 0; var47 <= 24; ++var47) {
				if (var47 % 2 == 0) {
					var11.setColorRGBA_F(0.5F, 0.4F, 0.3F, 1.0F);
				} else {
					var11.setColorRGBA_F(0.35F, 0.28F, 0.21000001F, 1.0F);
				}

				var48 = (float)var47 / 24.0F;
				height_adjustment_fraction = 1.0F - var48;
				y_animal = (double)(height_adjustment * height_adjustment_fraction) + par4 + var40 * (double)(var48 * var48 + var48) * 0.5 + (double)((24.0F - (float)var47) / 18.0F + 0.125F);
				var11.addVertex(par2 + var38 * (double)var48 + 0.0, y_animal, par6 + var42 * (double)var48);
				var11.addVertex(par2 + var38 * (double)var48 + 0.025, y_animal + 0.025, par6 + var42 * (double)var48);
			}

			var11.draw();
			var11.startDrawing(5);

			for(var47 = 0; var47 <= 24; ++var47) {
				if (var47 % 2 == 0) {
					var11.setColorRGBA_F(0.5F, 0.4F, 0.3F, 1.0F);
				} else {
					var11.setColorRGBA_F(0.35F, 0.28F, 0.21000001F, 1.0F);
				}

				var48 = (float)var47 / 24.0F;
				height_adjustment_fraction = 1.0F - var48;
				y_animal = (double)(height_adjustment * height_adjustment_fraction) + par4 + var40 * (double)(var48 * var48 + var48) * 0.5 + (double)((24.0F - (float)var47) / 18.0F + 0.125F);
				var11.addVertex(par2 + var38 * (double)var48 + 0.0, y_animal + 0.025, par6 + var42 * (double)var48);
				var11.addVertex(par2 + var38 * (double)var48 + 0.025, y_animal, par6 + var42 * (double)var48 + 0.025);
			}

			var11.draw();
			GL11.glEnable(2896);
			GL11.glEnable(3553);
			GL11.glEnable(2884);
		}

	}

	protected boolean func_110813_b(EntityLivingBase par1EntityLivingBase) {
		return this.func_130007_b((EntityLiving)par1EntityLivingBase);
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
		this.doRenderLiving((EntityLiving)par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.doRenderLiving((EntityLiving)par1Entity, par2, par4, par6, par8, par9);
	}
}
