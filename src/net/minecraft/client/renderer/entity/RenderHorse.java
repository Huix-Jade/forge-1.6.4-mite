package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderHorse extends RenderLiving {
	private static final Map field_110852_a = Maps.newHashMap();
	public static final int body_texture_horse = 0;
	public static final int body_texture_donkey = 1;
	public static final int body_texture_mule = 2;
	public static final int body_texture_zombie = 3;
	public static final int body_texture_skeleton = 4;

	public RenderHorse(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/horse/horse_white");
		this.setTexture(1, "textures/entity/horse/donkey");
		this.setTexture(2, "textures/entity/horse/mule");
		this.setTexture(3, "textures/entity/horse/horse_zombie");
		this.setTexture(4, "textures/entity/horse/horse_skeleton");
	}

	protected void func_110847_a(EntityHorse par1EntityHorse, float par2) {
		float var3 = 1.0F;
		int var4 = par1EntityHorse.getHorseType();
		if (var4 == 1) {
			var3 *= 0.87F;
		} else if (var4 == 2) {
			var3 *= 0.92F;
		}

		GL11.glScalef(var3, var3, var3);
		super.preRenderCallback(par1EntityHorse, par2);
	}

	protected void func_110846_a(EntityHorse par1EntityHorse, float par2, float par3, float par4, float par5, float par6, float par7) {
		if (par1EntityHorse.isInvisible()) {
			this.mainModel.setRotationAngles(par2, par3, par4, par5, par6, par7, par1EntityHorse);
		} else {
			this.bindEntityTexture(par1EntityHorse);
			this.mainModel.render(par1EntityHorse, par2, par3, par4, par5, par6, par7);
		}

	}

	protected ResourceLocation func_110849_a(EntityHorse par1EntityHorse) {
		if (!par1EntityHorse.func_110239_cn()) {
			switch (par1EntityHorse.getHorseType()) {
				case 0:
				default:
					return this.textures[0];
				case 1:
					return this.textures[1];
				case 2:
					return this.textures[2];
				case 3:
					return this.textures[3];
				case 4:
					return this.textures[4];
			}
		} else {
			return this.func_110848_b(par1EntityHorse);
		}
	}

	private ResourceLocation func_110848_b(EntityHorse par1EntityHorse) {
		String var2 = par1EntityHorse.getHorseTexture();
		ResourceLocation var3 = (ResourceLocation)field_110852_a.get(var2);
		if (var3 == null) {
			var3 = new ResourceLocation(var2, false);
			Minecraft.getMinecraft().getTextureManager().loadTexture(var3, new LayeredTexture(par1EntityHorse.getVariantTexturePaths()));
			field_110852_a.put(var2, var3);
		}

		return var3;
	}

	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
		this.func_110847_a((EntityHorse)par1EntityLivingBase, par2);
	}

	protected void renderModel(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4, float par5, float par6, float par7) {
		this.func_110846_a((EntityHorse)par1EntityLivingBase, par2, par3, par4, par5, par6, par7);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.func_110849_a((EntityHorse)par1Entity);
	}
}
