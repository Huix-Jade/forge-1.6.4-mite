package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;

public class RenderMinecartMobSpawner extends RenderMinecart {
	protected void func_98192_a(EntityMinecartMobSpawner entityMinecartMobSpawner, float f, Block block, int i) {
		super.renderBlockInMinecart(entityMinecartMobSpawner, f, block, i);
		if (block == Block.mobSpawner) {
			TileEntityMobSpawnerRenderer.func_98144_a(entityMinecartMobSpawner.func_98039_d(), entityMinecartMobSpawner.posX, entityMinecartMobSpawner.posY, entityMinecartMobSpawner.posZ, f);
		}

	}
}
