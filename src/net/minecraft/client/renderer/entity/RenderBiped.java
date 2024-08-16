package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;

public abstract class RenderBiped extends RenderLiving {
	protected ModelBiped modelBipedMain;
	protected float field_77070_b;
	protected ModelBiped field_82423_g;
	protected ModelBiped field_82425_h;
	public static final Map field_110859_k = Maps.newHashMap();

	public RenderBiped(ModelBiped par1ModelBiped, float par2) {
		this(par1ModelBiped, par2, 1.0F);
	}

	public RenderBiped(ModelBiped par1ModelBiped, float par2, float par3) {
		super(par1ModelBiped, par2);
		this.modelBipedMain = par1ModelBiped;
		this.field_77070_b = par3;
		this.func_82421_b();
	}

	protected void func_82421_b() {
		this.field_82423_g = new ModelBiped(1.0F);
		this.field_82425_h = new ModelBiped(0.5F);
	}

	@Deprecated //Use the more sensitve version getArmorResource below
	public static ResourceLocation func_110857_a(ItemArmor par0ItemArmor, int par1) {
		return func_110858_a(par0ItemArmor, par1, (String)null);
	}

	@Deprecated //Use the more sensitve version getArmorResource below
	public static ResourceLocation func_110858_a(ItemArmor par0ItemArmor, int par1, String par2Str) {
		String var3 = String.format("textures/models/armor/%s_layer_%d%s.png", par0ItemArmor.getTextureFilenamePrefix(),
				par1 == 2 ? 2 : 1, par2Str == null ? "" : String.format("_%s", par2Str));
		ResourceLocation var4 = (ResourceLocation)field_110859_k.get(var3);
		if (var4 == null) {
			var4 = new ResourceLocation(var3);
			field_110859_k.put(var3, var4);
		}

		return var4;
	}

	/**
	 * More generic ForgeHook version of the above function, it allows for Items to have more control over what texture they provide.
	 *
	 * @param entity Entity wearing the armor
	 * @param stack ItemStack for the armor
	 * @param slot Slot ID that the item is in
	 * @param type Subtype, can be null or "overlay"
	 * @return ResourceLocation pointing at the armor's texture
	 */
	public static ResourceLocation getArmorResource(Entity entity, ItemStack stack, int slot, String type)
	{
		ItemArmor item = (ItemArmor)stack.getItem();
		String s1 = String.format("textures/models/armor/%s_layer_%d%s.png",
				item.getTextureFilenamePrefix(), (slot == 2 ? 2 : 1), type == null ? "" : String.format("_%s", type));

		s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
		ResourceLocation resourcelocation = (ResourceLocation)field_110859_k.get(s1);

		if (resourcelocation == null)
		{
			resourcelocation = new ResourceLocation(s1);
			field_110859_k.put(s1, resourcelocation);
		}

		return resourcelocation;
	}

	protected int func_130006_a(EntityLiving par1EntityLiving, int par2, float par3) {
		ItemStack var4 = par1EntityLiving.func_130225_q(3 - par2);
		if (var4 != null) {
			Item var5 = var4.getItem();
			if (var5 instanceof ItemArmor var6) {
				this.bindTexture(getArmorResource(par1EntityLiving, var4, par2, null));
				ModelBiped var7 = par2 == 2 ? this.field_82425_h : this.field_82423_g;
				var7.bipedHead.showModel = par2 == 0;
				var7.bipedHeadwear.showModel = par2 == 0;
				var7.bipedBody.showModel = par2 == 1 || par2 == 2;
				var7.bipedRightArm.showModel = par2 == 1;
				var7.bipedLeftArm.showModel = par2 == 1;
				var7.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
				var7.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
				var7 = ForgeHooksClient.getArmorModel(par1EntityLiving, var4, par2, var7);
				this.setRenderPassModel(var7);
				var7.onGround = this.mainModel.onGround;
				var7.isRiding = this.mainModel.isRiding;
				var7.isChild = this.mainModel.isChild;
				float var8 = 1.0F;
				//Move out of if to allow for more then just CLOTH to have color
				int var9 = var6.getColor(var4);
				if (var9 != -1) {
					float var10 = (float)(var9 >> 16 & 255) / 255.0F;
					float var11 = (float)(var9 >> 8 & 255) / 255.0F;
					float var12 = (float)(var9 & 255) / 255.0F;
					GL11.glColor3f(var8 * var10, var8 * var11, var8 * var12);
					if (var4.isItemEnchanted()) {
						return 31;
					}

					return 16;
				}

				GL11.glColor3f(var8, var8, var8);
				if (var4.isItemEnchanted()) {
					return 15;
				}

				return 1;
			}
		}

		return -1;
	}

	protected void func_130013_c(EntityLiving par1EntityLiving, int par2, float par3) {
		ItemStack var4 = par1EntityLiving.func_130225_q(3 - par2);
		if (var4 != null) {
			Item var5 = var4.getItem();
			if (var5 instanceof ItemArmor) {
				this.bindTexture(getArmorResource(par1EntityLiving, var4, par2, "overlay"));
				float var6 = 1.0F;
				GL11.glColor3f(var6, var6, var6);
			}
		}

	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		float var10 = 1.0F;
		GL11.glColor3f(var10, var10, var10);
		ItemStack var11 = par1EntityLiving.getHeldItemStack();
		this.func_82420_a(par1EntityLiving, var11);
		double var12 = par4 - (double)par1EntityLiving.yOffset;
		if (par1EntityLiving.isSneaking()) {
			var12 -= 0.125;
		}

		super.doRenderLiving(par1EntityLiving, par2, var12, par6, par8, par9);
		this.field_82423_g.aimedBow = this.field_82425_h.aimedBow = this.modelBipedMain.aimedBow = false;
		this.field_82423_g.isSneak = this.field_82425_h.isSneak = this.modelBipedMain.isSneak = false;
		this.field_82423_g.heldItemRight = this.field_82425_h.heldItemRight = this.modelBipedMain.heldItemRight = 0;
	}

	protected ResourceLocation func_110856_a(EntityLiving par1EntityLiving) {
		return null;
	}

	protected void func_82420_a(EntityLiving par1EntityLiving, ItemStack par2ItemStack) {
		this.field_82423_g.heldItemRight = this.field_82425_h.heldItemRight = this.modelBipedMain.heldItemRight = par2ItemStack != null ? 1 : 0;
		this.field_82423_g.isSneak = this.field_82425_h.isSneak = this.modelBipedMain.isSneak = par1EntityLiving.isSneaking();
	}

	protected void func_130005_c(EntityLiving par1EntityLiving, float par2) {
		float var3 = 1.0F;
		GL11.glColor3f(var3, var3, var3);
		super.renderEquippedItems(par1EntityLiving, par2);
		ItemStack var4 = par1EntityLiving.getHeldItemStack();
		ItemStack itemStack = par1EntityLiving.func_130225_q(3);
		float var6;
		if (itemStack != null) {
			GL11.glPushMatrix();
			this.modelBipedMain.bipedHead.postRender(0.0625F);
			IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemStack, EQUIPPED);
			boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemStack, BLOCK_3D));

			if (itemStack.getItem() instanceof ItemBlock)
			{
				if (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[itemStack.itemID].getRenderType()))
				{
					var6 = 0.625F;
					GL11.glTranslatef(0.0F, -0.25F, 0.0F);
					GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
					GL11.glScalef(var6, -var6, -var6);
				}

				this.renderManager.itemRenderer.renderItem(par1EntityLiving, itemStack, 0);
			} else if (itemStack.getItem().itemID == Item.skull.itemID) {
				var6 = 1.0625F;
				GL11.glScalef(var6, -var6, -var6);
				String var7 = "";
				if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("SkullOwner")) {
					var7 = itemStack.getTagCompound().getString("SkullOwner");
				}

				TileEntitySkullRenderer.skullRenderer.func_82393_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, itemStack.getItemSubtype(), var7);
			}

			GL11.glPopMatrix();
		}

		if (var4 != null) {
			GL11.glPushMatrix();
			if (this.mainModel.isChild) {
				var6 = 0.5F;
				GL11.glTranslatef(0.0F, 0.625F, 0.0F);
				GL11.glRotatef(-20.0F, -1.0F, 0.0F, 0.0F);
				GL11.glScalef(var6, var6, var6);
			}

			this.modelBipedMain.bipedRightArm.postRender(0.0625F);
			GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
			IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(var4, EQUIPPED);
			boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, var4, BLOCK_3D));

			if (var4.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[var4.itemID].getRenderType())))
			{
				var6 = 0.5F;
				GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
				var6 *= 0.75F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(-var6, -var6, var6);
			} else if (var4.getItem() instanceof ItemBow) {
				var6 = 0.625F;
				GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
				GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(var6, -var6, var6);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else if (Item.itemsList[var4.itemID].isFull3D()) {
				var6 = 0.625F;
				if (Item.itemsList[var4.itemID].shouldRotateAroundWhenRendering()) {
					GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
					GL11.glTranslatef(0.0F, -0.125F, 0.0F);
				}

				this.func_82422_c();
				GL11.glScalef(var6, -var6, var6);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else {
				var6 = 0.375F;
				GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
				GL11.glScalef(var6, var6, var6);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			this.renderManager.itemRenderer.renderItem(par1EntityLiving, var4, 0);
			if (var4.getItem().requiresMultipleRenderPasses()) {
				for (int x = 1; x < var4.getItem().getRenderPasses(var4.getItemDamage()); x++)
				{
					this.renderManager.itemRenderer.renderItem(par1EntityLiving, var4, x);
				}
			}

			GL11.glPopMatrix();
		}

	}

	protected void func_82422_c() {
		GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
	}

	protected void func_82408_c(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		this.func_130013_c((EntityLiving)par1EntityLivingBase, par2, par3);
	}

	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return this.func_130006_a((EntityLiving)par1EntityLivingBase, par2, par3);
	}

	protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
		this.func_130005_c((EntityLiving)par1EntityLivingBase, par2);
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
		this.doRenderLiving((EntityLiving)par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.func_110856_a((EntityLiving)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.doRenderLiving((EntityLiving)par1Entity, par2, par4, par6, par8, par9);
	}
}
