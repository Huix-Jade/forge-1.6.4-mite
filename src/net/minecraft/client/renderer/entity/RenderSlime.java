//package net.minecraft.client.renderer.entity;
//
//import net.minecraft.client.model.ModelBase;
//import net.minecraft.entity.monster.EntitySlime;
//import net.minecraft.util.ResourceLocation;
//import org.lwjgl.opengl.GL11;
//
//public class RenderSlime extends RenderLiving {
//	private static final ResourceLocation slimeTextures = new ResourceLocation("textures/entity/slime/slime.png");
//	private ModelBase scaleAmount;
//
//	public RenderSlime(ModelBase modelBase, ModelBase modelBase2, float f) {
//		super(modelBase, f);
//		this.scaleAmount = modelBase2;
//	}
//
//	protected int shouldSlimeRenderPass(EntitySlime entitySlime, int i, float f) {
//		if (entitySlime.isInvisible()) {
//			return 0;
//		} else if (i == 0) {
//			this.setRenderPassModel(this.scaleAmount);
//			GL11.glEnable(2977);
//			GL11.glEnable(3042);
//			GL11.glBlendFunc(770, 771);
//			return 1;
//		} else {
//			if (i == 1) {
//				GL11.glDisable(3042);
//				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//			}
//
//			return -1;
//		}
//	}
//
//	protected void scaleSlime(EntitySlime entitySlime, float f) {
//		float var3 = (float)entitySlime.bR();
//		float var4 = (entitySlime.j + (entitySlime.i - entitySlime.j) * f) / (var3 * 0.5F + 1.0F);
//		float var5 = 1.0F / (var4 + 1.0F);
//		GL11.glScalef(var5 * var3, 1.0F / var5 * var3, var5 * var3);
//	}
//
//	protected ResourceLocation getSlimeTextures(EntitySlime entitySlime) {
//		return slimeTextures;
//	}
//}
