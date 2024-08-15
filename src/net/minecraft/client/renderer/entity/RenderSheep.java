package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSheep extends RenderLiving {
	public static final int body_texture = 0;
	public static final int fur_texture = 1;
	public static final int body_texture_sick = 2;

	public RenderSheep(ModelBase par1ModelBase, ModelBase par2ModelBase, float par3) {
		super(par1ModelBase, par3);
		this.setRenderPassModel(par2ModelBase);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/sheep/sheep");
		this.setTexture(1, "textures/entity/sheep/sheep_fur");
		this.setTexture(2, "textures/entity/sheep/sick");
	}

	protected int setWoolColorAndRender(EntitySheep par1EntitySheep, int par2, float par3) {
		if (par2 == 0 && !par1EntitySheep.getSheared()) {
			this.bindTexture(this.textures[1]);
			float var4 = 1.0F;
			int var5 = par1EntitySheep.getFleeceColor();
			GL11.glColor3f(var4 * EntitySheep.fleeceColorTable[var5][0], var4 * EntitySheep.fleeceColorTable[var5][1], var4 * EntitySheep.fleeceColorTable[var5][2]);
			return 1;
		} else {
			return -1;
		}
	}

	protected ResourceLocation func_110883_a(EntitySheep par1EntitySheep) {
		return par1EntitySheep.isWell() ? this.textures[0] : this.textures[2];
	}

	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return this.setWoolColorAndRender((EntitySheep)par1EntityLivingBase, par2, par3);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.func_110883_a((EntitySheep)par1Entity);
	}
}
