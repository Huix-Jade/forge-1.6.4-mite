package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderingScheme;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public abstract class Render {
	private static final ResourceLocation shadowTextures = new ResourceLocation("textures/misc/shadow.png");
	protected RenderManager renderManager;
	protected RenderBlocks renderBlocks = new RenderBlocks();
	protected float shadowSize;
	protected float shadowOpaque = 1.0F;
	double[] x = new double[4];
	double[] y = new double[4];
	double[] z = new double[4];
	double[] u = new double[4];
	double[] v = new double[4];
	float[] r = new float[4];
	float[] g = new float[4];
	float[] b = new float[4];
	int[] brightness = new int[4];
	protected boolean use_glowing_texture;
	protected ResourceLocation[] textures = new ResourceLocation[16];
	protected ResourceLocation[] textures_glowing = new ResourceLocation[16];

	protected void setTexture(int index, String path) {
		this.setTexture(index, path, path);
	}

	protected void setTexture(int index, String path, String glow_path) {
		if (this.textures[index] != null) {
			Minecraft.setErrorMessage("setTexture: texture [" + index + "] has already been set for " + this);
		} else {
			ResourceLocation texture = new ResourceLocation(path + ".png");
			this.textures[index] = texture;
			ResourceLocation texture_glowing = new ResourceLocation(glow_path + "_glow.png", false);
			if (Minecraft.MITE_resource_pack != null && Minecraft.MITE_resource_pack.resourceExists(texture_glowing)) {
				this.textures_glowing[index] = texture_glowing;
			}

		}
	}

	public abstract void doRender(Entity entity, double d, double e, double f, float g, float h);

	protected abstract ResourceLocation getEntityTexture(Entity entity);

	protected void bindEntityTexture(Entity par1Entity) {
		this.bindTexture(this.getEntityTexture(par1Entity));
	}

	protected void bindTexture(ResourceLocation par1ResourceLocation) {
		this.renderManager.renderEngine.bindTexture(par1ResourceLocation);
	}

	private void renderEntityOnFire(Entity par1Entity, double par2, double par4, double par6, float par8) {
		GL11.glDisable(2896);
		Icon var9 = Block.fire.getFireIcon(0);
		Icon var10 = Block.fire.getFireIcon(1);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)par2, (float)par4, (float)par6);
		float var11 = par1Entity.width * 1.4F;
		GL11.glScalef(var11, var11, var11);
		Tessellator var12 = Tessellator.instance;
		float var13 = 0.5F;
		float var14 = 0.0F;
		float var15 = par1Entity.height / var11;
		float var16 = (float)(par1Entity.posY - par1Entity.boundingBox.minY);
		GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(0.0F, 0.0F, -0.3F + (float)((int)var15) * 0.02F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float var17 = 0.0F;
		int var18 = 0;
		var12.startDrawingQuads();

		while(var15 > 0.0F) {
			Icon var19 = var18 % 2 == 0 ? var9 : var10;
			this.bindTexture(TextureMap.locationBlocksTexture);
			float var20 = var19.getMinU();
			float var21 = var19.getMinV();
			float var22 = var19.getMaxU();
			float var23 = var19.getMaxV();
			if (var18 / 2 % 2 == 0) {
				float var24 = var22;
				var22 = var20;
				var20 = var24;
			}

			var12.addVertexWithUV((double)(var13 - var14), (double)(0.0F - var16), (double)var17, (double)var22, (double)var23);
			var12.addVertexWithUV((double)(-var13 - var14), (double)(0.0F - var16), (double)var17, (double)var20, (double)var23);
			var12.addVertexWithUV((double)(-var13 - var14), (double)(1.4F - var16), (double)var17, (double)var20, (double)var21);
			var12.addVertexWithUV((double)(var13 - var14), (double)(1.4F - var16), (double)var17, (double)var22, (double)var21);
			var15 -= 0.45F;
			var16 -= 0.45F;
			var13 *= 0.9F;
			var17 += 0.03F;
			++var18;
		}

		var12.draw();
		GL11.glPopMatrix();
		GL11.glEnable(2896);
	}

	private void renderShadow(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		if (!par1Entity.disable_shadow) {
			par4 -= (double)par1Entity.yOffset;
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			this.renderManager.renderEngine.bindTexture(shadowTextures);
			World var10 = this.getWorldFromRenderManager();
			GL11.glDepthMask(false);
			float var11 = this.shadowSize;
			if (par1Entity instanceof EntityLiving) {
				EntityLiving var12 = (EntityLiving)par1Entity;
				var11 *= var12.getRenderSizeModifier();
				if (var12.isChild()) {
					var11 *= 0.5F;
				}
			}

			double var35 = par1Entity.lastTickPosX + (par1Entity.posX - par1Entity.lastTickPosX) * (double)par9;
			double var14 = par1Entity.lastTickPosY + (par1Entity.posY - par1Entity.lastTickPosY) * (double)par9 + (double)par1Entity.getShadowSize();
			var14 -= (double)par1Entity.yOffset;
			double var16 = par1Entity.lastTickPosZ + (par1Entity.posZ - par1Entity.lastTickPosZ) * (double)par9;
			int var18 = MathHelper.floor_double(var35 - (double)var11);
			int var19 = MathHelper.floor_double(var35 + (double)var11);
			int var22 = MathHelper.floor_double(var16 - (double)var11);
			int var23 = MathHelper.floor_double(var16 + (double)var11);
			double var24 = par2 - var35;
			double var26 = par4 - var14;
			double var28 = par6 - var16;
			Tessellator var30 = Tessellator.instance;
			var30.startDrawingQuads();
			float shadow_size = par1Entity.getShadowSize();
			float object_opacity = this.renderManager.getEntityRenderObject(par1Entity).getModelOpacity(par1Entity);
			GL11.glAlphaFunc(516, 0.001F);

			for(int x = var18; x <= var19; ++x) {
				for(int z = var22; z <= var23; ++z) {
					this.renderShadowOnBlockMITE(par2, par4 + (double)shadow_size, par6, x, z, par8, var11, var24, var26 + (double)shadow_size, var28, object_opacity, par1Entity);
				}
			}

			var30.draw();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(3042);
			GL11.glDepthMask(true);
			GL11.glAlphaFunc(516, 0.1F);
		}
	}

	private World getWorldFromRenderManager() {
		return this.renderManager.worldObj;
	}

	private void renderShadowOnBlock(Block par1Block, double par2, double par4, double par6, int par8, int par9, int par10, float par11, float par12, double par13, double par15, double par17, float opacity_of_object, Entity entity) {
		Minecraft.setErrorMessage("renderShadowOnBlock: This function is no longer in service");
		GL11.glAlphaFunc(516, 0.01F);
		Tessellator var19 = Tessellator.instance;
		if (par1Block.renderAsNormalBlock()) {
			double entity_min_y = entity.isEntityLivingBase() ? entity.getAsEntityLivingBase().getFootPosY() : entity.posY;
			double block_top_y = this.getWorldFromRenderManager().getBlockRenderTopY(par8, par9, par10);
			double var20 = (double)par11 * (1.0 - Math.abs(entity_min_y - block_top_y) * 0.75) * 0.25 * (double)this.getWorldFromRenderManager().getLightBrightness(par8, par9, par10);
			if (var20 >= 0.0) {
				if (var20 > 1.0) {
					var20 = 1.0;
				}

				var19.setColorRGBA_F(1.0F, 1.0F, 1.0F, (float)var20 * opacity_of_object);
				int index = Minecraft.getThreadIndex();
				double var22 = (double)par8 + par1Block.getBlockBoundsMinX(index) + par13;
				double var24 = (double)par8 + par1Block.getBlockBoundsMaxX(index) + par13;
				double var28 = (double)par10 + par1Block.getBlockBoundsMinZ(index) + par17;
				double var30 = (double)par10 + par1Block.getBlockBoundsMaxZ(index) + par17;
				float var32 = (float)((par2 - var22) / 2.0 / (double)par12 + 0.5);
				float var33 = (float)((par2 - var24) / 2.0 / (double)par12 + 0.5);
				float var34 = (float)((par6 - var28) / 2.0 / (double)par12 + 0.5);
				float var35 = (float)((par6 - var30) / 2.0 / (double)par12 + 0.5);
				double var26 = this.getWorldFromRenderManager().getBlockRenderTopY(par8, par9, par10) + par15 + 5.000000237487257E-4;
				if (RenderingScheme.current == 0) {
					var19.addVertexWithUV(var22, var26, var28, (double)var32, (double)var34);
					var19.addVertexWithUV(var22, var26, var30, (double)var32, (double)var35);
					var19.addVertexWithUV(var24, var26, var30, (double)var33, (double)var35);
					var19.addVertexWithUV(var24, var26, var28, (double)var33, (double)var34);
				} else {
					this.x[0] = var22;
					this.y[0] = var26;
					this.z[0] = var28;
					this.u[0] = (double)var32;
					this.v[0] = (double)var34;
					this.x[1] = var22;
					this.y[1] = var26;
					this.z[1] = var30;
					this.u[1] = (double)var32;
					this.v[1] = (double)var35;
					this.x[2] = var24;
					this.y[2] = var26;
					this.z[2] = var30;
					this.u[2] = (double)var33;
					this.v[2] = (double)var35;
					this.x[3] = var24;
					this.y[3] = var26;
					this.z[3] = var28;
					this.u[3] = (double)var33;
					this.v[3] = (double)var34;
					var19.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
				}
			}
		}

	}

	private float applyShadowAttenuationForHeightDifference(float input_alpha, Entity entity, double entity_min_y, double block_top_y) {
		double attenuation;
		if (entity_min_y < block_top_y) {
			attenuation = (block_top_y - entity_min_y) / (entity.getCenterPoint().yCoord - entity_min_y);
		} else {
			attenuation = (entity_min_y - block_top_y) * 0.75;
		}

		return input_alpha * (1.0F - MathHelper.clamp_float((float)attenuation, 0.0F, 1.0F));
	}

	private void renderShadowOnBlockMITE(double par2, double par4, double par6, int block_x, int block_z, float par11, float par12, double par13, double par15, double par17, float opacity_of_object, Entity entity) {
		World world = this.getWorldFromRenderManager();
		int block_y = MathHelper.floor_double(entity.getCenterPoint().yCoord);
		double entity_min_y = entity.isEntityLivingBase() ? entity.getAsEntityLivingBase().getFootPosY() : entity.posY;
		int min_y = MathHelper.floor_double(entity_min_y) - 2;

		do {
			Block block = world.getBlock(block_x, block_y, block_z);
			int metadata;
			if (block != null && block.canSupportEntityShadow(metadata = world.getBlockMetadata(block_x, block_y, block_z))) {
				if (block == null) {
					return;
				}

				if (world.getBlockLightValue(block_x, block_y + 1, block_z) < 4) {
					return;
				}

				Tessellator var19 = Tessellator.instance;
				double block_top_y = world.getBlockRenderTopY(block_x, block_y, block_z);
				if (block instanceof BlockStairs && !block.isTopFlatAndSolid(metadata)) {
					this.renderShadowOnTopOfStairs(par2, par4, par6, block_x, block_y, block_z, par11, par12, par13, par15, par17, opacity_of_object, entity_min_y, entity);
					block_top_y -= 0.5;
				}

				double var20 = (double)this.applyShadowAttenuationForHeightDifference(par11, entity, entity_min_y, block_top_y) * 0.25 * (double)world.getLightBrightness(block_x, block_y + 1, block_z);
				if (var20 >= 0.0) {
					if (var20 > 1.0) {
						var20 = 1.0;
					}

					var19.setColorRGBA_F(1.0F, 1.0F, 1.0F, (float)var20 * opacity_of_object);
					int index = Minecraft.getThreadIndex();
					double var22 = (double)block_x + block.getBlockBoundsMinX(index) + par13;
					double var24 = (double)block_x + block.getBlockBoundsMaxX(index) + par13;
					double var26 = block_top_y + par15 + 5.000000237487257E-4;
					double var28 = (double)block_z + block.getBlockBoundsMinZ(index) + par17;
					double var30 = (double)block_z + block.getBlockBoundsMaxZ(index) + par17;
					if (block instanceof BlockStairs) {
						var22 = (double)block_x + par13;
						var24 = (double)((float)block_x + 1.0F) + par13;
						var28 = (double)block_z + par17;
						var30 = (double)((float)block_z + 1.0F) + par17;
					}

					float var32 = (float)((par2 - var22) / 2.0 / (double)par12 + 0.5);
					float var33 = (float)((par2 - var24) / 2.0 / (double)par12 + 0.5);
					float var34 = (float)((par6 - var28) / 2.0 / (double)par12 + 0.5);
					float var35 = (float)((par6 - var30) / 2.0 / (double)par12 + 0.5);
					if (RenderingScheme.current == 0) {
						var19.addVertexWithUV(var22, var26, var28, (double)var32, (double)var34);
						var19.addVertexWithUV(var22, var26, var30, (double)var32, (double)var35);
						var19.addVertexWithUV(var24, var26, var30, (double)var33, (double)var35);
						var19.addVertexWithUV(var24, var26, var28, (double)var33, (double)var34);
					} else {
						this.x[0] = var22;
						this.y[0] = var26;
						this.z[0] = var28;
						this.u[0] = (double)var32;
						this.v[0] = (double)var34;
						this.x[1] = var22;
						this.y[1] = var26;
						this.z[1] = var30;
						this.u[1] = (double)var32;
						this.v[1] = (double)var35;
						this.x[2] = var24;
						this.y[2] = var26;
						this.z[2] = var30;
						this.u[2] = (double)var33;
						this.v[2] = (double)var35;
						this.x[3] = var24;
						this.y[3] = var26;
						this.z[3] = var28;
						this.u[3] = (double)var33;
						this.v[3] = (double)var34;
						var19.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
					}
				}

				return;
			}

			--block_y;
		} while(block_y >= min_y);

	}

	private void renderShadowOnTopOfStairs(double par2, double par4, double par6, int block_x, int block_y, int block_z, float par11, float par12, double par13, double par15, double par17, float opacity_of_object, double entity_min_y, Entity entity) {
		World world = this.getWorldFromRenderManager();
		BlockStairs block = (BlockStairs)world.getBlock(block_x, block_y, block_z);
		world.getBlockMetadata(block_x, block_y, block_z);
		Tessellator var19 = Tessellator.instance;
		double block_top_y = world.getBlockRenderTopY(block_x, block_y, block_z);
		double var20 = (double)this.applyShadowAttenuationForHeightDifference(par11, entity, entity_min_y, block_top_y) * 0.25 * (double)world.getLightBrightness(block_x, block_y + 1, block_z);
		if (var20 >= 0.0) {
			if (var20 > 1.0) {
				var20 = 1.0;
			}

			var19.setColorRGBA_F(1.0F, 1.0F, 1.0F, (float)var20 * opacity_of_object);
			int index = Minecraft.getThreadIndex();
			double var26 = block_top_y + par15 + 5.000000237487257E-4;

			for(int i = 0; i < 2; ++i) {
				if (i == 0) {
					if (!block.func_82542_g(world, block_x, block_y, block_z)) {
						i = 1;
					}
				} else if (!block.func_82544_h(world, block_x, block_y, block_z)) {
					break;
				}

				double var22 = (double)block_x + block.getBlockBoundsMinX(index) + par13;
				double var24 = (double)block_x + block.getBlockBoundsMaxX(index) + par13;
				double var28 = (double)block_z + block.getBlockBoundsMinZ(index) + par17;
				double var30 = (double)block_z + block.getBlockBoundsMaxZ(index) + par17;
				float var32 = (float)((par2 - var22) / 2.0 / (double)par12 + 0.5);
				float var33 = (float)((par2 - var24) / 2.0 / (double)par12 + 0.5);
				float var34 = (float)((par6 - var28) / 2.0 / (double)par12 + 0.5);
				float var35 = (float)((par6 - var30) / 2.0 / (double)par12 + 0.5);
				if (RenderingScheme.current == 0) {
					var19.addVertexWithUV(var22, var26, var28, (double)var32, (double)var34);
					var19.addVertexWithUV(var22, var26, var30, (double)var32, (double)var35);
					var19.addVertexWithUV(var24, var26, var30, (double)var33, (double)var35);
					var19.addVertexWithUV(var24, var26, var28, (double)var33, (double)var34);
				} else {
					this.x[0] = var22;
					this.y[0] = var26;
					this.z[0] = var28;
					this.u[0] = (double)var32;
					this.v[0] = (double)var34;
					this.x[1] = var22;
					this.y[1] = var26;
					this.z[1] = var30;
					this.u[1] = (double)var32;
					this.v[1] = (double)var35;
					this.x[2] = var24;
					this.y[2] = var26;
					this.z[2] = var30;
					this.u[2] = (double)var33;
					this.v[2] = (double)var35;
					this.x[3] = var24;
					this.y[3] = var26;
					this.z[3] = var28;
					this.u[3] = (double)var33;
					this.v[3] = (double)var34;
					var19.add4VerticesWithUV(this.x, this.y, this.z, this.u, this.v);
				}
			}
		}

	}

	public static void renderOffsetAABB(AxisAlignedBB par0AxisAlignedBB, double par1, double par3, double par5) {
		GL11.glDisable(3553);
		Tessellator var7 = Tessellator.instance;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		var7.startDrawingQuads();
		var7.setTranslation(par1, par3, par5);
		var7.setNormal(0.0F, 0.0F, -1.0F);
		var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
		var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
		var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
		var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
		var7.setNormal(0.0F, 0.0F, 1.0F);
		var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
		var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
		var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
		var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
		var7.setNormal(0.0F, -1.0F, 0.0F);
		var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
		var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
		var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
		var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
		var7.setNormal(0.0F, 1.0F, 0.0F);
		var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
		var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
		var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
		var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
		var7.setNormal(-1.0F, 0.0F, 0.0F);
		var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
		var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
		var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
		var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
		var7.setNormal(1.0F, 0.0F, 0.0F);
		var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
		var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
		var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
		var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
		var7.setTranslation(0.0, 0.0, 0.0);
		var7.draw();
		GL11.glEnable(3553);
	}

	public static void renderAABB(AxisAlignedBB par0AxisAlignedBB) {
		Tessellator var1 = Tessellator.instance;
		var1.startDrawingQuads();
		var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
		var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
		var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
		var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
		var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
		var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
		var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
		var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
		var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
		var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
		var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
		var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
		var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
		var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
		var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
		var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
		var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
		var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
		var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
		var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
		var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
		var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
		var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
		var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
		var1.draw();
	}

	public void setRenderManager(RenderManager par1RenderManager) {
		this.renderManager = par1RenderManager;
	}

	public void doRenderShadowAndFire(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		int death_time = par1Entity instanceof EntityLivingBase ? ((EntityLivingBase)par1Entity).deathTime : 0;
		if (this.renderManager.options.isFancyGraphicsEnabled() && this.shadowSize > 0.0F && !par1Entity.isInvisible() && death_time < 10) {
			double var10 = this.renderManager.getDistanceToCamera(par1Entity.posX, par1Entity.posY, par1Entity.posZ);
			float var12 = (float)((1.0 - var10 / 256.0) * (double)this.shadowOpaque);
			if (var12 > 0.0F) {
				this.renderShadow(par1Entity, par2, par4, par6, var12, par9);
			}
		}

		if (par1Entity.canRenderOnFire() && death_time < 5) {
			this.renderEntityOnFire(par1Entity, par2, par4, par6, par9);
		}

	}

	public FontRenderer getFontRendererFromRenderManager() {
		return this.renderManager.getFontRenderer();
	}

	public void updateIcons(IconRegister par1IconRegister) {
	}

	public float getModelOpacity(Entity entity) {
		return 1.0F;
	}

	public ResourceLocation getGlowingTextureCounterpart(ResourceLocation texture) {
		for(int i = 0; i < this.textures.length; ++i) {
			if (this.textures[i] == texture) {
				return this.textures_glowing[i];
			}
		}

		return null;
	}
}
