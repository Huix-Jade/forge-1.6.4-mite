package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSkeleton extends RenderBiped {
	public static final int body_texture = 0;
	public static final int body_texture_wither = 1;
	public static final int texture_longdead = 2;
	public static final int texture_longdead_guardian = 3;
	public static final int texture_bone_lord = 4;

	public RenderSkeleton() {
		super(new ModelSkeleton(), 0.5F);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/skeleton/skeleton");
		this.setTexture(1, "textures/entity/skeleton/wither_skeleton");
		this.setTexture(2, "textures/entity/skeleton/longdead");
		this.setTexture(3, "textures/entity/skeleton/longdead_guardian");
		this.setTexture(4, "textures/entity/skeleton/bone_lord");
	}

	protected void scaleSkeleton(EntitySkeleton par1EntitySkeleton, float par2) {
		if (par1EntitySkeleton.getSkeletonType() == 1) {
			GL11.glScalef(1.2F, 1.2F, 1.2F);
		}

	}

	protected void func_82422_c() {
		GL11.glTranslatef(0.09375F, 0.1875F, 0.0F);
	}

	protected ResourceLocation func_110860_a(EntitySkeleton par1EntitySkeleton) {
		if (par1EntitySkeleton.isLongdead()) {
			return this.textures[par1EntitySkeleton.isLongdeadGuardian() ? 3 : 2];
		} else if (par1EntitySkeleton.isBoneLord()) {
			return this.textures[par1EntitySkeleton.isAncientBoneLord() ? 3 : 4];
		} else {
			return par1EntitySkeleton.getSkeletonType() == 1 ? this.textures[1] : this.textures[0];
		}
	}

	protected ResourceLocation func_110856_a(EntityLiving par1EntityLiving) {
		return this.func_110860_a((EntitySkeleton)par1EntityLiving);
	}

	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
		this.scaleSkeleton((EntitySkeleton)par1EntityLivingBase, par2);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.func_110860_a((EntitySkeleton)par1Entity);
	}
}
