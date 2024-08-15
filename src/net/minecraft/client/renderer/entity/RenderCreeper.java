package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderCreeper extends RenderLiving {
	public static final int body_texture = 0;
	public static final int armored_texture = 1;
	protected float scale = EntityCreeper.getScale();
	private ModelBase creeperModel = new ModelCreeper(2.0F);

	public RenderCreeper() {
		super(new ModelCreeper(), EntityCreeper.getScale() * 0.5F);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/creeper/" + this.getSubtypeName());
		this.setTexture(1, "textures/entity/creeper/creeper_armor");
	}

	protected void updateCreeperScale(EntityCreeper par1EntityCreeper, float par2) {
		float var3 = par1EntityCreeper.getCreeperFlashIntensity(par2);
		float var4 = 1.0F + MathHelper.sin(var3 * 100.0F) * var3 * 0.01F;
		if (var3 < 0.0F) {
			var3 = 0.0F;
		}

		if (var3 > 1.0F) {
			var3 = 1.0F;
		}

		var3 *= var3;
		var3 *= var3;
		float var5 = (1.0F + var3 * 0.4F) * var4;
		float var6 = (1.0F + var3 * 0.1F) / var4;
		GL11.glScalef(var5 * this.scale, var6 * this.scale, var5 * this.scale);
	}

	protected int updateCreeperColorMultiplier(EntityCreeper par1EntityCreeper, float par2, float par3) {
		float var4 = par1EntityCreeper.getCreeperFlashIntensity(par3);
		if ((int)(var4 * 10.0F) % 2 == 0) {
			return 0;
		} else {
			int var5 = (int)(var4 * 0.2F * 255.0F);
			if (var5 < 0) {
				var5 = 0;
			}

			if (var5 > 255) {
				var5 = 255;
			}

			short var6 = 255;
			short var7 = 255;
			short var8 = 255;
			return var5 << 24 | var6 << 16 | var7 << 8 | var8;
		}
	}

	protected int renderCreeperPassModel(EntityCreeper par1EntityCreeper, int par2, float par3) {
		if (par1EntityCreeper.getPowered()) {
			if (par1EntityCreeper.isInvisible()) {
				GL11.glDepthMask(false);
			} else {
				GL11.glDepthMask(true);
			}

			if (par2 == 1) {
				float var4 = (float)par1EntityCreeper.ticksExisted + par3;
				this.bindTexture(this.textures[1]);
				GL11.glMatrixMode(5890);
				GL11.glLoadIdentity();
				float var5 = var4 * 0.01F;
				float var6 = var4 * 0.01F;
				GL11.glTranslatef(var5, var6, 0.0F);
				this.setRenderPassModel(this.creeperModel);
				GL11.glMatrixMode(5888);
				GL11.glEnable(3042);
				float var7 = 0.5F;
				GL11.glColor4f(var7, var7, var7, 1.0F);
				GL11.glDisable(2896);
				GL11.glBlendFunc(1, 1);
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

	protected int func_77061_b(EntityCreeper par1EntityCreeper, int par2, float par3) {
		return -1;
	}

	protected ResourceLocation getCreeperTextures(EntityCreeper par1EntityCreeper) {
		return this.textures[0];
	}

	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
		this.updateCreeperScale((EntityCreeper)par1EntityLivingBase, par2);
	}

	protected int getColorMultiplier(EntityLivingBase par1EntityLivingBase, float par2, float par3) {
		return this.updateCreeperColorMultiplier((EntityCreeper)par1EntityLivingBase, par2, par3);
	}

	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return this.renderCreeperPassModel((EntityCreeper)par1EntityLivingBase, par2, par3);
	}

	protected int inheritRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return this.func_77061_b((EntityCreeper)par1EntityLivingBase, par2, par3);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.getCreeperTextures((EntityCreeper)par1Entity);
	}

	public String getSubtypeName() {
		return "creeper";
	}
}
