package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelArachnid;
import net.minecraft.client.renderer.RenderArachnid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCaveSpider;
import org.lwjgl.opengl.GL11;

public class RenderCaveSpider extends RenderArachnid {
	public RenderCaveSpider() {
		super(new ModelArachnid(), new ModelArachnid(), 1.0F);
		this.shadowSize *= 0.7F;
	}

	protected void scaleSpider(EntityCaveSpider par1EntityCaveSpider, float par2) {
		GL11.glScalef(0.7F, 0.7F, 0.7F);
	}

	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
		this.scaleSpider((EntityCaveSpider)par1EntityLivingBase, par2);
	}

	public String getSubtypeName() {
		return "cave_spider";
	}
}
