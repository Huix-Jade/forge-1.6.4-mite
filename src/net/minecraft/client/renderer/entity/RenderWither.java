package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelWither;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWither extends RenderLiving {
	public static final int body_texture = 0;
	public static final int body_texture_invulnerable = 1;
	private int field_82419_a;

	public RenderWither() {
		super(new ModelWither(), 1.0F);
		this.field_82419_a = ((ModelWither)this.mainModel).func_82903_a();
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/wither/wither");
		this.setTexture(1, "textures/entity/wither/wither_invulnerable");
	}

	public void func_82418_a(EntityWither par1EntityWither, double par2, double par4, double par6, float par8, float par9) {
		BossStatus.setBossStatus(par1EntityWither, true);
		int var10 = ((ModelWither)this.mainModel).func_82903_a();
		if (var10 != this.field_82419_a) {
			this.field_82419_a = var10;
			this.mainModel = new ModelWither();
		}

		super.doRenderLiving((EntityLiving)par1EntityWither, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation func_110911_a(EntityWither par1EntityWither) {
		int var2 = par1EntityWither.func_82212_n();
		return var2 <= 0 || var2 <= 80 && var2 / 5 % 2 == 1 ? this.textures[0] : this.textures[1];
	}

	protected void func_82415_a(EntityWither par1EntityWither, float par2) {
		int var3 = par1EntityWither.func_82212_n();
		if (var3 > 0) {
			float var4 = 2.0F - ((float)var3 - par2) / 220.0F * 0.5F;
			GL11.glScalef(var4, var4, var4);
		} else {
			GL11.glScalef(2.0F, 2.0F, 2.0F);
		}

	}

	protected int func_82417_a(EntityWither par1EntityWither, int par2, float par3) {
		if (par1EntityWither.isArmored()) {
			if (par1EntityWither.isInvisible()) {
				GL11.glDepthMask(false);
			} else {
				GL11.glDepthMask(true);
			}

			if (par2 == 1) {
				float var4 = (float)par1EntityWither.ticksExisted + par3;
				this.bindTexture(this.textures[1]);
				GL11.glMatrixMode(5890);
				GL11.glLoadIdentity();
				float var5 = MathHelper.cos(var4 * 0.02F) * 3.0F;
				float var6 = var4 * 0.01F;
				GL11.glTranslatef(var5, var6, 0.0F);
				this.setRenderPassModel(this.mainModel);
				GL11.glMatrixMode(5888);
				GL11.glEnable(3042);
				float var7 = 0.5F;
				GL11.glColor4f(var7, var7, var7, 1.0F);
				GL11.glDisable(2896);
				GL11.glBlendFunc(1, 1);
				GL11.glTranslatef(0.0F, -0.01F, 0.0F);
				GL11.glScalef(1.1F, 1.1F, 1.1F);
				return 1;
			}

			if (par2 == 2) {
				GL11.glMatrixMode(5890);
				GL11.glLoadIdentity();
				GL11.glMatrixMode(5888);
				GL11.glEnable(2896);
				GL11.glDisable(3042);
			}
		}

		return -1;
	}

	protected int func_82416_b(EntityWither par1EntityWither, int par2, float par3) {
		return -1;
	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		this.func_82418_a((EntityWither)par1EntityLiving, par2, par4, par6, par8, par9);
	}

	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
		this.func_82415_a((EntityWither)par1EntityLivingBase, par2);
	}

	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return this.func_82417_a((EntityWither)par1EntityLivingBase, par2, par3);
	}

	protected int inheritRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return this.func_82416_b((EntityWither)par1EntityLivingBase, par2, par3);
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
		this.func_82418_a((EntityWither)par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.func_110911_a((EntityWither)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.func_82418_a((EntityWither)par1Entity, par2, par4, par6, par8, par9);
	}
}
