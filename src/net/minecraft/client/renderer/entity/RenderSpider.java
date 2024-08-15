package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelArachnid;
import net.minecraft.client.renderer.RenderArachnid;

public class RenderSpider extends RenderArachnid {
	public RenderSpider() {
		super(new ModelArachnid(), new ModelArachnid(), 1.0F);
	}

	public String getSubtypeName() {
		return "spider";
	}
}
