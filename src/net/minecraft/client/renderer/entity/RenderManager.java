package net.minecraft.client.renderer.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelGelatinousCube;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlackWidowSpider;
import net.minecraft.client.renderer.RenderDemonSpider;
import net.minecraft.client.renderer.RenderDireWolf;
import net.minecraft.client.renderer.RenderEarthElemental;
import net.minecraft.client.renderer.RenderFireElemental;
import net.minecraft.client.renderer.RenderGelatinousCube;
import net.minecraft.client.renderer.RenderGhoul;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHellhound;
import net.minecraft.client.renderer.RenderInfernalCreeper;
import net.minecraft.client.renderer.RenderInvisibleStalker;
import net.minecraft.client.renderer.RenderPhaseSpider;
import net.minecraft.client.renderer.RenderShadow;
import net.minecraft.client.renderer.RenderWight;
import net.minecraft.client.renderer.RenderWoodSpider;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAncientBoneLord;
import net.minecraft.entity.EntityBlackWidowSpider;
import net.minecraft.entity.EntityBlob;
import net.minecraft.entity.EntityBoneLord;
import net.minecraft.entity.EntityBrick;
import net.minecraft.entity.EntityClayGolem;
import net.minecraft.entity.EntityCopperspine;
import net.minecraft.entity.EntityDemonSpider;
import net.minecraft.entity.EntityDireWolf;
import net.minecraft.entity.EntityEarthElemental;
import net.minecraft.entity.EntityFireElemental;
import net.minecraft.entity.EntityGelatinousSphere;
import net.minecraft.entity.EntityGhoul;
import net.minecraft.entity.EntityGiantVampireBat;
import net.minecraft.entity.EntityHellhound;
import net.minecraft.entity.EntityHoarySilverfish;
import net.minecraft.entity.EntityInfernalCreeper;
import net.minecraft.entity.EntityInvisibleStalker;
import net.minecraft.entity.EntityJelly;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLongdead;
import net.minecraft.entity.EntityLongdeadGuardian;
import net.minecraft.entity.EntityNetherspawn;
import net.minecraft.entity.EntityNightwing;
import net.minecraft.entity.EntityOoze;
import net.minecraft.entity.EntityPhaseSpider;
import net.minecraft.entity.EntityPudding;
import net.minecraft.entity.EntityRevenant;
import net.minecraft.entity.EntityShadow;
import net.minecraft.entity.EntityVampireBat;
import net.minecraft.entity.EntityWeb;
import net.minecraft.entity.EntityWight;
import net.minecraft.entity.EntityWoodSpider;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

public final class RenderManager {
	public Map entityRenderMap = new HashMap();
	public static RenderManager instance = new RenderManager();
	private FontRenderer fontRenderer;
	public static double renderPosX;
	public static double renderPosY;
	public static double renderPosZ;
	public TextureManager renderEngine;
	public ItemRenderer itemRenderer;
	public World worldObj;
	public EntityLivingBase livingPlayer;
	public EntityLivingBase field_96451_i;
	public float playerViewY;
	public float playerViewX;
	public GameSettings options;
	public double viewerPosX;
	public double viewerPosY;
	public double viewerPosZ;
	public static boolean field_85095_o;

	private RenderManager() {
		this.entityRenderMap.put(EntityCaveSpider.class, new RenderCaveSpider());
		this.entityRenderMap.put(EntitySpider.class, new RenderSpider());
		this.entityRenderMap.put(EntityPig.class, new RenderPig(new ModelPig(), new ModelPig(0.5F), 0.7F));
		this.entityRenderMap.put(EntitySheep.class, new RenderSheep(new ModelSheep2(), new ModelSheep1(), 0.7F));
		this.entityRenderMap.put(EntityCow.class, new RenderCow(new ModelCow(), 0.7F));
		this.entityRenderMap.put(EntityMooshroom.class, new RenderMooshroom(new ModelCow(), 0.7F));
		this.entityRenderMap.put(EntityWolf.class, new RenderWolf(new ModelWolf(), new ModelWolf(), 0.5F));
		this.entityRenderMap.put(EntityChicken.class, new RenderChicken(new ModelChicken(), 0.3F));
		this.entityRenderMap.put(EntityOcelot.class, new RenderOcelot(new ModelOcelot(), 0.4F));
		this.entityRenderMap.put(EntitySilverfish.class, new RenderSilverfish());
		this.entityRenderMap.put(EntityCreeper.class, new RenderCreeper());
		this.entityRenderMap.put(EntityEnderman.class, new RenderEnderman());
		this.entityRenderMap.put(EntitySnowman.class, new RenderSnowMan());
		this.entityRenderMap.put(EntitySkeleton.class, new RenderSkeleton());
		this.entityRenderMap.put(EntityWitch.class, new RenderWitch());
		this.entityRenderMap.put(EntityBlaze.class, new RenderBlaze());
		this.entityRenderMap.put(EntityZombie.class, new RenderZombie());
		this.entityRenderMap.put(EntityGhoul.class, new RenderGhoul());
		this.entityRenderMap.put(EntityWight.class, new RenderWight());
		this.entityRenderMap.put(EntityInvisibleStalker.class, new RenderInvisibleStalker());
		this.entityRenderMap.put(EntityDemonSpider.class, new RenderDemonSpider());
		this.entityRenderMap.put(EntityHellhound.class, new RenderHellhound(new ModelWolf(), new ModelWolf(), 0.5F));
		this.entityRenderMap.put(EntityDireWolf.class, new RenderDireWolf(new ModelWolf(), new ModelWolf(), 0.5F, 1.1F));
		this.entityRenderMap.put(EntityWoodSpider.class, new RenderWoodSpider(0.6F));
		this.entityRenderMap.put(EntityInfernalCreeper.class, new RenderInfernalCreeper());
		this.entityRenderMap.put(EntityShadow.class, new RenderShadow());
		this.entityRenderMap.put(EntityFireElemental.class, new RenderFireElemental());
		this.entityRenderMap.put(EntityBlackWidowSpider.class, new RenderBlackWidowSpider(0.6F));
		this.entityRenderMap.put(EntityRevenant.class, new RenderZombie());
		this.entityRenderMap.put(EntityEarthElemental.class, new RenderEarthElemental());
		this.entityRenderMap.put(EntityJelly.class, new RenderGelatinousCube(new ModelGelatinousCube(16), new ModelGelatinousCube(0), 0.25F));
		this.entityRenderMap.put(EntityBlob.class, new RenderGelatinousCube(new ModelGelatinousCube(16), new ModelGelatinousCube(0), 0.25F));
		this.entityRenderMap.put(EntityOoze.class, new RenderGelatinousCube(new ModelGelatinousCube(16), new ModelGelatinousCube(0), 0.25F));
		this.entityRenderMap.put(EntityPudding.class, new RenderGelatinousCube(new ModelGelatinousCube(16), new ModelGelatinousCube(0), 0.25F));
		this.entityRenderMap.put(EntityVampireBat.class, new RenderBat());
		this.entityRenderMap.put(EntityGiantVampireBat.class, new RenderBat());
		this.entityRenderMap.put(EntityLongdead.class, new RenderSkeleton());
		this.entityRenderMap.put(EntityLongdeadGuardian.class, new RenderSkeleton());
		this.entityRenderMap.put(EntityNightwing.class, new RenderBat());
		this.entityRenderMap.put(EntityNetherspawn.class, new RenderSilverfish());
		this.entityRenderMap.put(EntityCopperspine.class, new RenderSilverfish());
		this.entityRenderMap.put(EntityHoarySilverfish.class, new RenderSilverfish());
		this.entityRenderMap.put(EntityClayGolem.class, new RenderEarthElemental());
		this.entityRenderMap.put(EntityBoneLord.class, new RenderSkeleton());
		this.entityRenderMap.put(EntityAncientBoneLord.class, new RenderSkeleton());
		this.entityRenderMap.put(EntityPhaseSpider.class, new RenderPhaseSpider());
		this.entityRenderMap.put(EntitySlime.class, new RenderGelatinousCube(new ModelGelatinousCube(16), new ModelGelatinousCube(0), 0.25F));
		this.entityRenderMap.put(EntityMagmaCube.class, new RenderMagmaCube());
		this.entityRenderMap.put(EntityPlayer.class, new RenderPlayer());
		this.entityRenderMap.put(EntityGiantZombie.class, new RenderGiantZombie(new ModelZombie(), 0.5F, 6.0F));
		this.entityRenderMap.put(EntityGhast.class, new RenderGhast());
		this.entityRenderMap.put(EntitySquid.class, new RenderSquid(new ModelSquid(), 0.7F));
		this.entityRenderMap.put(EntityVillager.class, new RenderVillager());
		this.entityRenderMap.put(EntityIronGolem.class, new RenderIronGolem());
		this.entityRenderMap.put(EntityBat.class, new RenderBat());
		this.entityRenderMap.put(EntityDragon.class, new RenderDragon());
		this.entityRenderMap.put(EntityEnderCrystal.class, new RenderEnderCrystal());
		this.entityRenderMap.put(EntityWither.class, new RenderWither());
		this.entityRenderMap.put(Entity.class, new RenderEntity());
		this.entityRenderMap.put(EntityPainting.class, new RenderPainting());
		this.entityRenderMap.put(EntityItemFrame.class, new RenderItemFrame());
		this.entityRenderMap.put(EntityLeashKnot.class, new RenderLeashKnot());
		this.entityRenderMap.put(EntityArrow.class, new RenderArrow());
		this.entityRenderMap.put(EntitySnowball.class, new RenderSnowball(Item.snowball));
		this.entityRenderMap.put(EntityEnderPearl.class, new RenderSnowball(Item.enderPearl));
		this.entityRenderMap.put(EntityEnderEye.class, new RenderSnowball(Item.eyeOfEnder));
		this.entityRenderMap.put(EntityEgg.class, new RenderSnowball(Item.egg));
		this.entityRenderMap.put(EntityBrick.class, new RenderSnowball((Item)null));
		this.entityRenderMap.put(EntityGelatinousSphere.class, new RenderSnowball(Item.slimeBall));
		this.entityRenderMap.put(EntityWeb.class, new RenderSnowball(Item.thrownWeb));
		this.entityRenderMap.put(EntityPotion.class, new RenderSnowball(Item.potion, 16384));
		this.entityRenderMap.put(EntityExpBottle.class, new RenderSnowball(Item.expBottle));
		this.entityRenderMap.put(EntityFireworkRocket.class, new RenderSnowball(Item.firework));
		this.entityRenderMap.put(EntityLargeFireball.class, new RenderFireball(2.0F));
		this.entityRenderMap.put(EntitySmallFireball.class, new RenderFireball(0.5F));
		this.entityRenderMap.put(EntityWitherSkull.class, new RenderWitherSkull());
		this.entityRenderMap.put(EntityItem.class, new RenderItem());
		this.entityRenderMap.put(EntityXPOrb.class, new RenderXPOrb());
		this.entityRenderMap.put(EntityTNTPrimed.class, new RenderTNTPrimed());
		this.entityRenderMap.put(EntityFallingSand.class, new RenderFallingSand());
		this.entityRenderMap.put(EntityMinecartTNT.class, new RenderTntMinecart());
		this.entityRenderMap.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner());
		this.entityRenderMap.put(EntityMinecart.class, new RenderMinecart());
		this.entityRenderMap.put(EntityBoat.class, new RenderBoat());
		this.entityRenderMap.put(EntityFishHook.class, new RenderFish());
		this.entityRenderMap.put(EntityHorse.class, new RenderHorse(new ModelHorse(), 0.75F));
		this.entityRenderMap.put(EntityLightningBolt.class, new RenderLightningBolt());
		Iterator var1 = this.entityRenderMap.values().iterator();

		while(var1.hasNext()) {
			Render var2 = (Render)var1.next();
			var2.setRenderManager(this);
		}

	}

	public Render getEntityClassRenderObject(Class par1Class) {
		Render var2 = (Render)this.entityRenderMap.get(par1Class);
		if (var2 == null && par1Class != Entity.class) {
			var2 = this.getEntityClassRenderObject(par1Class.getSuperclass());
			this.entityRenderMap.put(par1Class, var2);
		}

		return var2;
	}

	public Render getEntityRenderObject(Entity par1Entity) {
		return this.getEntityClassRenderObject(par1Entity.getClass());
	}

	public void cacheActiveRenderInfo(World par1World, TextureManager par2TextureManager, FontRenderer par3FontRenderer, EntityLivingBase par4EntityLivingBase, EntityLivingBase par5EntityLivingBase, GameSettings par6GameSettings, float par7) {
		this.worldObj = par1World;
		this.renderEngine = par2TextureManager;
		this.options = par6GameSettings;
		this.livingPlayer = par4EntityLivingBase;
		this.field_96451_i = par5EntityLivingBase;
		this.fontRenderer = par3FontRenderer;

		int x = MathHelper.floor_double(par4EntityLivingBase.posX);
		int y = MathHelper.floor_double(par4EntityLivingBase.posY);
		int z = MathHelper.floor_double(par4EntityLivingBase.posZ);
		Block block = Block.blocksList[par1World.getBlockId(x, y, z)];

		if (block != null && block.isBed(par1World, x, y, z, par4EntityLivingBase)) {
			int k = block.getBedDirection(par1World, x, y, z);
			this.playerViewY = (float)(k * 90 + 180);
			this.playerViewX = 0.0F;
		} else {
			this.playerViewY = par4EntityLivingBase.prevRotationYaw + (par4EntityLivingBase.rotationYaw - par4EntityLivingBase.prevRotationYaw) * par7;
			this.playerViewX = par4EntityLivingBase.prevRotationPitch + (par4EntityLivingBase.rotationPitch - par4EntityLivingBase.prevRotationPitch) * par7;
		}

		if (par6GameSettings.thirdPersonView == 2) {
			this.playerViewY += 180.0F;
		}

		this.viewerPosX = par4EntityLivingBase.lastTickPosX + (par4EntityLivingBase.posX - par4EntityLivingBase.lastTickPosX) * (double)par7;
		this.viewerPosY = par4EntityLivingBase.lastTickPosY + (par4EntityLivingBase.posY - par4EntityLivingBase.lastTickPosY) * (double)par7;
		this.viewerPosZ = par4EntityLivingBase.lastTickPosZ + (par4EntityLivingBase.posZ - par4EntityLivingBase.lastTickPosZ) * (double)par7;
	}

	public void renderEntity(Entity par1Entity, float par2) {
		World world = par1Entity.worldObj;
		if (par1Entity instanceof EntityItem) {
			if (par1Entity.ticksExisted < 1) {
				return;
			}

			if (par1Entity.ticksExisted < 5 && world.doBlockCollisionBoundsIntersectWithBB(par1Entity.getBlockPosX(), par1Entity.getBlockPosY(), par1Entity.getBlockPosZ(), par1Entity.boundingBox)) {
				return;
			}
		}

		Chunk chunk = world.getChunkFromBlockCoords(par1Entity.getBlockPosX(), par1Entity.getBlockPosZ());
		if (chunk != null && !chunk.isEmpty()) {
			int x = par1Entity.getBlockPosX();
			int y = par1Entity.getBlockPosY();
			int z = par1Entity.getBlockPosZ();
			RenderGlobal render_global = Minecraft.theMinecraft.renderGlobal;
			WorldRenderer world_renderer = null;
			if (par1Entity.index_of_last_applicable_world_renderer < render_global.worldRenderers.length) {
				world_renderer = render_global.worldRenderers[par1Entity.index_of_last_applicable_world_renderer];
				if (world_renderer == null || !world_renderer.isRenderingCoords(x, y, z)) {
					world_renderer = null;
				}
			}

			if (world_renderer == null) {
				world_renderer = Minecraft.theMinecraft.renderGlobal.getWorldRendererFor(x, y, z, par1Entity);
			}

			if (world_renderer != null && world_renderer.isInitialized) {
				if (par1Entity.ticksExisted == 0) {
					par1Entity.lastTickPosX = par1Entity.posX;
					par1Entity.lastTickPosY = par1Entity.posY;
					par1Entity.lastTickPosZ = par1Entity.posZ;
				}

				double var3 = par1Entity.lastTickPosX + (par1Entity.posX - par1Entity.lastTickPosX) * (double)par2;
				double var5 = par1Entity.lastTickPosY + (par1Entity.posY - par1Entity.lastTickPosY) * (double)par2;
				double var7 = par1Entity.lastTickPosZ + (par1Entity.posZ - par1Entity.lastTickPosZ) * (double)par2;
				float var9 = par1Entity.prevRotationYaw + (par1Entity.rotationYaw - par1Entity.prevRotationYaw) * par2;
				int var10 = par1Entity.getBrightnessForRender(par2);
				if (par1Entity.isBurning()) {
					var10 = 15728880;
				}

				int var11 = var10 % 65536;
				int var12 = var10 / 65536;
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var11 / 1.0F, (float)var12 / 1.0F);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.renderEntityWithPosYaw(par1Entity, var3 - renderPosX, var5 - renderPosY, var7 - renderPosZ, var9, par2);
			}
		}
	}

	public void renderEntityWithPosYaw(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		Render var10 = null;

		try {
			var10 = this.getEntityRenderObject(par1Entity);
			if (var10 != null && this.renderEngine != null) {
				if (field_85095_o && !par1Entity.isInvisible()) {
					try {
						this.func_85094_b(par1Entity, par2, par4, par6, par8, par9);
					} catch (Throwable var17) {
						throw new ReportedException(CrashReport.makeCrashReport(var17, "Rendering entity hitbox in world"));
					}
				}

				try {
					var10.doRender(par1Entity, par2, par4, par6, par8, par9);
				} catch (Throwable var16) {
					throw new ReportedException(CrashReport.makeCrashReport(var16, "Rendering entity in world"));
				}

				try {
					var10.doRenderShadowAndFire(par1Entity, par2, par4, par6, par8, par9);
				} catch (Throwable var15) {
					throw new ReportedException(CrashReport.makeCrashReport(var15, "Post-rendering entity in world"));
				}
			}

		} catch (Throwable var18) {
			CrashReport var12 = CrashReport.makeCrashReport(var18, "Rendering entity in world");
			CrashReportCategory var13 = var12.makeCategory("Entity being rendered");
			par1Entity.addEntityCrashInfo(var13);
			CrashReportCategory var14 = var12.makeCategory("Renderer details");
			var14.addCrashSection("Assigned renderer", var10);
			var14.addCrashSection("Location", CrashReportCategory.func_85074_a(par2, par4, par6));
			var14.addCrashSection("Rotation", par8);
			var14.addCrashSection("Delta", par9);
			throw new ReportedException(var12);
		}
	}

	private void func_85094_b(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		if (par1Entity instanceof EntityClientPlayerMP) {
			par4 -= (double)par1Entity.yOffset;
		}

		GL11.glDepthMask(false);
		GL11.glDisable(3553);
		GL11.glDisable(2896);
		GL11.glDisable(2884);
		GL11.glDisable(3042);
		GL11.glPushMatrix();
		Tessellator var10 = Tessellator.instance;
		var10.startDrawingQuads();
		var10.setColorRGBA(255, 255, 255, 32);
		double var11 = (double)(-par1Entity.width / 2.0F);
		double var13 = (double)(-par1Entity.width / 2.0F);
		double var15 = (double)(par1Entity.width / 2.0F);
		double var17 = (double)(-par1Entity.width / 2.0F);
		double var19 = (double)(-par1Entity.width / 2.0F);
		double var21 = (double)(par1Entity.width / 2.0F);
		double var23 = (double)(par1Entity.width / 2.0F);
		double var25 = (double)(par1Entity.width / 2.0F);
		double var27 = (double)par1Entity.height;
		var10.addVertex(par2 + var11, par4 + var27, par6 + var13);
		var10.addVertex(par2 + var11, par4, par6 + var13);
		var10.addVertex(par2 + var15, par4, par6 + var17);
		var10.addVertex(par2 + var15, par4 + var27, par6 + var17);
		var10.addVertex(par2 + var23, par4 + var27, par6 + var25);
		var10.addVertex(par2 + var23, par4, par6 + var25);
		var10.addVertex(par2 + var19, par4, par6 + var21);
		var10.addVertex(par2 + var19, par4 + var27, par6 + var21);
		var10.addVertex(par2 + var15, par4 + var27, par6 + var17);
		var10.addVertex(par2 + var15, par4, par6 + var17);
		var10.addVertex(par2 + var23, par4, par6 + var25);
		var10.addVertex(par2 + var23, par4 + var27, par6 + var25);
		var10.addVertex(par2 + var19, par4 + var27, par6 + var21);
		var10.addVertex(par2 + var19, par4, par6 + var21);
		var10.addVertex(par2 + var11, par4, par6 + var13);
		var10.addVertex(par2 + var11, par4 + var27, par6 + var13);
		var10.draw();
		GL11.glPopMatrix();
		GL11.glEnable(3553);
		GL11.glEnable(2896);
		GL11.glEnable(2884);
		GL11.glDisable(3042);
		GL11.glDepthMask(true);
	}

	public void set(World par1World) {
		this.worldObj = par1World;
	}

	public double getDistanceToCamera(double par1, double par3, double par5) {
		double var7 = par1 - this.viewerPosX;
		double var9 = par3 - this.viewerPosY;
		double var11 = par5 - this.viewerPosZ;
		return var7 * var7 + var9 * var9 + var11 * var11;
	}

	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}

	public void updateIcons(IconRegister par1IconRegister) {
		Iterator var2 = this.entityRenderMap.values().iterator();

		while(var2.hasNext()) {
			Render var3 = (Render)var2.next();
			var3.updateIcons(par1IconRegister);
		}

	}
}
