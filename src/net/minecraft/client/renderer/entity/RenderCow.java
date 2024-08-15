package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;

public class RenderCow extends RenderLiving {
	public static final int body_texture = 0;
	public static final int body_texture_sick = 1;

	public RenderCow(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/cow/cow");
		this.setTexture(1, "textures/entity/cow/sick");
	}

	protected ResourceLocation getCowTextures(EntityCow par1EntityCow) {
		return par1EntityCow.isWell() ? this.textures[0] : this.textures[1];
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.getCowTextures((EntityCow)par1Entity);
	}
}
