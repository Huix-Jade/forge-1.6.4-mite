package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityBrick;
import net.minecraft.entity.EntityGelatinousSphere;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSnowball extends Render {
	private Item field_94151_a;
	private int field_94150_f;

	public RenderSnowball(Item par1Item, int par2) {
		this.field_94151_a = par1Item;
		this.field_94150_f = par2;
	}

	public RenderSnowball(Item par1Item) {
		this(par1Item, 0);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		if (par1Entity instanceof EntityBrick) {
			this.field_94151_a = ((EntityBrick)par1Entity).getModelItem();
		} else if (par1Entity instanceof EntityGelatinousSphere) {
			this.field_94150_f = ((EntityGelatinousSphere)par1Entity).getModelSubtype();
		}

		Icon var10 = this.field_94151_a.getIconFromSubtype(this.field_94150_f);
		if (var10 != null) {
			GL11.glPushMatrix();
			GL11.glTranslatef((float)par2, (float)par4, (float)par6);
			GL11.glEnable(32826);
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			this.bindEntityTexture(par1Entity);
			Tessellator var11 = Tessellator.instance;
			if (var10 == ItemPotion.func_94589_d("bottle_splash")) {
				int var12 = PotionHelper.func_77915_a(((EntityPotion)par1Entity).getPotionType(), false);
				float var13 = (float)(var12 >> 16 & 255) / 255.0F;
				float var14 = (float)(var12 >> 8 & 255) / 255.0F;
				float var15 = (float)(var12 & 255) / 255.0F;
				GL11.glColor3f(var13, var14, var15);
				GL11.glPushMatrix();
				this.func_77026_a(var11, ItemPotion.func_94589_d("overlay"));
				GL11.glPopMatrix();
				GL11.glColor3f(1.0F, 1.0F, 1.0F);
			}

			this.func_77026_a(var11, var10);
			GL11.glDisable(32826);
			GL11.glPopMatrix();
		}

	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return TextureMap.locationItemsTexture;
	}

	private void func_77026_a(Tessellator par1Tessellator, Icon par2Icon) {
		float var3 = par2Icon.getMinU();
		float var4 = par2Icon.getMaxU();
		float var5 = par2Icon.getMinV();
		float var6 = par2Icon.getMaxV();
		float var7 = 1.0F;
		float var8 = 0.5F;
		float var9 = 0.25F;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		par1Tessellator.startDrawingQuads();
		par1Tessellator.setNormal(0.0F, 1.0F, 0.0F);
		par1Tessellator.addVertexWithUV((double)(0.0F - var8), (double)(0.0F - var9), 0.0, (double)var3, (double)var6);
		par1Tessellator.addVertexWithUV((double)(var7 - var8), (double)(0.0F - var9), 0.0, (double)var4, (double)var6);
		par1Tessellator.addVertexWithUV((double)(var7 - var8), (double)(var7 - var9), 0.0, (double)var4, (double)var5);
		par1Tessellator.addVertexWithUV((double)(0.0F - var8), (double)(var7 - var9), 0.0, (double)var3, (double)var5);
		par1Tessellator.draw();
	}
}
