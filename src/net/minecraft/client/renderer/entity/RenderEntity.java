package net.minecraft.client.renderer.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEntity extends Render {
	public void doRender(Entity entity, double d, double e, double f, float g, float h) {
		GL11.glPushMatrix();
		renderOffsetAABB(entity.boundingBox, d - entity.lastTickPosX, e - entity.lastTickPosY, f - entity.lastTickPosZ);
		GL11.glPopMatrix();
	}

	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}
