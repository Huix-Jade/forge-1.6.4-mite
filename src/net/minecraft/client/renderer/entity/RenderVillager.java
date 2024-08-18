package net.minecraft.client.renderer.entity;

import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderVillager extends RenderLiving {
	public static final int body_texture = 0;
	public static final int body_texture_farmer = 1;
	public static final int body_texture_librarian = 2;
	public static final int body_texture_priest = 3;
	public static final int body_texture_smith = 4;
	public static final int body_texture_butcher = 5;
	protected ModelVillager villagerModel;

	public RenderVillager() {
		super(new ModelVillager(0.0F), 0.5F);
		this.villagerModel = (ModelVillager)this.mainModel;
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/villager/villager");
		this.setTexture(1, "textures/entity/villager/farmer");
		this.setTexture(2, "textures/entity/villager/librarian");
		this.setTexture(3, "textures/entity/villager/priest");
		this.setTexture(4, "textures/entity/villager/smith");
		this.setTexture(5, "textures/entity/villager/butcher");
	}

	protected int shouldVillagerRenderPass(EntityVillager par1EntityVillager, int par2, float par3) {
		return -1;
	}

	public void renderVillager(EntityVillager par1EntityVillager, double par2, double par4, double par6, float par8, float par9) {
		super.doRenderLiving((EntityLiving)par1EntityVillager, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation func_110902_a(EntityVillager par1EntityVillager) {
		switch (par1EntityVillager.getProfession()) {
			case 0:
				return this.textures[1];
			case 1:
				return this.textures[2];
			case 2:
				return this.textures[3];
			case 3:
				return this.textures[4];
			case 4:
				return this.textures[5];
			default:
				return this.textures[0];
		}
	}

	protected void renderVillagerEquipedItems(EntityVillager par1EntityVillager, float par2) {
		super.renderEquippedItems(par1EntityVillager, par2);
	}

	protected void preRenderVillager(EntityVillager par1EntityVillager, float par2) {
		float var3 = 0.9375F;
		if (par1EntityVillager.getGrowingAge() < 0) {
			var3 = (float)((double)var3 * 0.5);
			this.shadowSize = 0.25F;
		} else {
			this.shadowSize = 0.5F;
		}

		GL11.glScalef(var3, var3, var3);
	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		this.renderVillager((EntityVillager)par1EntityLiving, par2, par4, par6, par8, par9);
	}

	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
		this.preRenderVillager((EntityVillager)par1EntityLivingBase, par2);
	}

	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return this.shouldVillagerRenderPass((EntityVillager)par1EntityLivingBase, par2, par3);
	}

	protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
		this.renderVillagerEquipedItems((EntityVillager)par1EntityLivingBase, par2);
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
		this.renderVillager((EntityVillager)par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.func_110902_a((EntityVillager)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.renderVillager((EntityVillager)par1Entity, par2, par4, par6, par8, par9);
	}
}
