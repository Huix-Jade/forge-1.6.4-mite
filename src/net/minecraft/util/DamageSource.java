package net.minecraft.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityBrick;
import net.minecraft.entity.EntityGelatinousSphere;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class DamageSource {
   public static DamageSource inFire = (new DamageSource("inFire")).setFireDamage();
   public static DamageSource onFire = (new DamageSource("onFire")).setFireDamage();
   public static DamageSource lava = (new DamageSource("lava")).setLavaDamage();
   public static DamageSource inWall = (new DamageSource("inWall")).setAbsolute();
   public static DamageSource drown = (new DamageSource("drown")).setUnblockable();
   public static DamageSource reverse_drown = (new DamageSource("reverse_drown")).setUnblockable();
   public static DamageSource water = (new DamageSource("drown")).setUnblockable();
   public static DamageSource starve = (new DamageSource("starve")).setUnblockable();
   public static DamageSource cactus = new DamageSource("cactus");
   public static DamageSource fall = (new DamageSource("fall")).setDamageBypassesMundaneArmor();
   public static DamageSource outOfWorld = (new DamageSource("outOfWorld")).setAbsolute().setDamageAllowedInCreativeMode();
   public static DamageSource generic = (new DamageSource("generic")).setDamageBypassesMundaneArmor();
   public static DamageSource magic = (new DamageSource("magic")).setDamageBypassesMundaneArmor().setMagicAspect();
   public static DamageSource wither = (new DamageSource("wither")).setDamageBypassesMundaneArmor();
   public static DamageSource anvil = new DamageSource("anvil");
   public static DamageSource fallingBlock = new DamageSource("fallingBlock");
   public static DamageSource poison = (new DamageSource("poison")).setUnblockable();
   public static DamageSource divine_lightning = (new DamageSource("divine_lightning")).setAbsolute();
   public static DamageSource absolute = (new DamageSource("absolute")).setAbsolute();
   public static DamageSource sunlight = new DamageSource("sunlight");
   public static DamageSource pepsin = new DamageSource("pepsin");
   public static DamageSource acid = new DamageSource("acid");
   public static DamageSource melt = new DamageSource("melt");
   public static DamageSource pig_nibble = new DamageSource("pigNibble");
   private boolean bypasses_mundane_armor;
   private boolean is_unblockable;
   private boolean is_absolute;
   private boolean isDamageAllowedInCreativeMode;
   private float hungerDamage = 0.3F;
   private boolean fireDamage;
   private boolean has_fire_aspect;
   private boolean is_lava_damage;
   private boolean has_magic_aspect;
   private boolean has_silver_aspect;
   private boolean explosion;
   public String damageType;
   public int block_x;
   public int block_y;
   public int block_z;
   public int block_metadata;
   public boolean is_hand_damage;

   public static DamageSource causeMobDamage(EntityLivingBase par0EntityLivingBase) {
      return new EntityDamageSource("mob", par0EntityLivingBase);
   }

   public static DamageSource causePlayerDamage(EntityPlayer par0EntityPlayer) {
      return new EntityDamageSource("player", par0EntityPlayer);
   }

   public static DamageSource causeArrowDamage(EntityArrow entity_arrow, Entity entity_shooter) {
      return new EntityDamageSource("arrow", entity_arrow, entity_shooter);
   }

   public static DamageSource causeFireballDamage(EntityFireball par0EntityFireball, Entity par1Entity) {
      return par1Entity == null ? (new EntityDamageSource("onFire", par0EntityFireball, par0EntityFireball)).setFireDamage() : (new EntityDamageSource("fireball", par0EntityFireball, par1Entity)).setFireDamage();
   }

   public static DamageSource causeThrownDamage(Entity entity_projectile, Entity entity_shooter) {
      return new EntityDamageSource("thrown", entity_projectile, entity_shooter);
   }

   public static DamageSource causeIndirectMagicDamage(Entity par0Entity, Entity par1Entity) {
      return (new EntityDamageSource("indirectMagic", par0Entity, par1Entity)).setDamageBypassesMundaneArmor().setMagicAspect();
   }

   public static DamageSource causeThornsDamage(Entity par0Entity) {
      return (new EntityDamageSource("thorns", par0Entity)).setMagicAspect();
   }

   public static DamageSource setExplosionSource(Explosion par0Explosion) {
      return par0Explosion != null && par0Explosion.getExplosivePlacedBy() != null ? (new EntityDamageSource("explosion.player", par0Explosion.getExplosivePlacedBy())).setExplosion() : (new DamageSource("explosion")).setExplosion();
   }

   public boolean isProjectile() {
      return false;
   }

   public boolean isIndirect() {
      return false;
   }

   public boolean isMelee() {
      return !this.isProjectile() && !this.isIndirect();
   }

   public boolean isExplosion() {
      return this.explosion;
   }

   public DamageSource setExplosion() {
      this.explosion = true;
      return this;
   }

   public boolean bypassesMundaneArmor() {
      return this.bypasses_mundane_armor || this.is_unblockable || this.is_absolute;
   }

   public boolean isUnblockable() {
      return this.is_unblockable || this.is_absolute;
   }

   public boolean isAbsolute() {
      return this.is_absolute;
   }

   public float getHungerDamage() {
      return this.hungerDamage;
   }

   public boolean canHarmInCreative() {
      return this.isDamageAllowedInCreativeMode;
   }

   public DamageSource(String par1Str) {
      this.damageType = par1Str;
   }

   public Entity getImmediateEntity() {
      return null;
   }

   public Entity getResponsibleEntity() {
      return null;
   }

   protected DamageSource setDamageBypassesMundaneArmor() {
      this.bypasses_mundane_armor = true;
      this.hungerDamage = 0.0F;
      return this;
   }

   protected DamageSource setUnblockable() {
      this.is_unblockable = true;
      return this.setDamageBypassesMundaneArmor();
   }

   protected DamageSource setAbsolute() {
      this.is_absolute = true;
      return this.setUnblockable();
   }

   protected DamageSource setDamageAllowedInCreativeMode() {
      this.isDamageAllowedInCreativeMode = true;
      return this;
   }

   protected DamageSource setFireDamage() {
      this.fireDamage = true;
      this.setFireAspect();
      this.setDamageBypassesMundaneArmor();
      return this;
   }

   protected DamageSource setFireAspect() {
      return this.setFireAspect(true);
   }

   public DamageSource setFireAspect(boolean has_fire_aspect) {
      this.has_fire_aspect = has_fire_aspect;
      return this;
   }

   public DamageSource setLavaDamage() {
      this.is_lava_damage = true;
      this.setDamageBypassesMundaneArmor();
      return this;
   }

   public boolean isLavaDamage() {
      return this.is_lava_damage;
   }

   public DamageSource setHandDamage() {
      this.is_hand_damage = true;
      this.setUnblockable();
      return this;
   }

   public ChatMessageComponent getDeathMessage(EntityLivingBase par1EntityLivingBase) {
      EntityLivingBase var2 = par1EntityLivingBase.func_94060_bK();
      String var3 = "death.attack." + this.damageType;
      String var4 = var3 + ".player";
      return var2 != null && StatCollector.func_94522_b(var4) ? ChatMessageComponent.createFromTranslationWithSubstitutions(var4, par1EntityLivingBase.getTranslatedEntityName(), var2.getTranslatedEntityName()) : ChatMessageComponent.createFromTranslationWithSubstitutions(var3, par1EntityLivingBase.getTranslatedEntityName());
   }

   public boolean isFireDamage() {
      return this.fireDamage;
   }

   public boolean hasFireAspect() {
      return this.has_fire_aspect || this.isFireDamage();
   }

   public String getDamageType() {
      return this.damageType;
   }

   public boolean hasMagicAspect() {
      return this.has_magic_aspect;
   }

   public boolean hasSilverAspect() {
      return this.has_silver_aspect;
   }

   public DamageSource setMagicAspect() {
      this.has_magic_aspect = true;
      return this;
   }

   public DamageSource setSilverAspect() {
      this.has_silver_aspect = true;
      return this;
   }

   public ItemStack getItemAttackedWith() {
      Entity immediate_entity = this.getImmediateEntity();
      if (immediate_entity != null) {
         if (immediate_entity instanceof EntityThrowable) {
            EntityThrowable entity_throwable = (EntityThrowable)immediate_entity;
            return entity_throwable.getItemStack();
         }

         if (immediate_entity instanceof EntityArrow) {
            EntityArrow entity_arrow = (EntityArrow)immediate_entity;
            return new ItemStack(entity_arrow.item_arrow);
         }

         if (immediate_entity instanceof EntityLivingBase) {
            EntityLivingBase entity_living_base = (EntityLivingBase)immediate_entity;
            return entity_living_base.getHeldItemStack();
         }
      }

      return null;
   }

   public DamageSource setBlock(World world, int x, int y, int z) {
      this.block_x = x;
      this.block_y = y;
      this.block_z = z;
      this.block_metadata = world.getBlockMetadata(x, y, z);
      return this;
   }

   public String toString() {
      return this.damageType;
   }

   public boolean wasCausedByPlayer() {
      return this.getResponsibleEntity() instanceof EntityPlayer;
   }

   public boolean wasCausedByPlayerInCreative() {
      Entity entity = this.getResponsibleEntity();
      return entity instanceof EntityPlayer && ((EntityPlayer)entity).inCreativeMode();
   }

   public boolean isFireballFromPlayer() {
      return "fireball".equals(this.getDamageType()) && this.wasCausedByPlayer();
   }

   public static boolean wasCausedByPlayer(DamageSource damage_source) {
      return damage_source != null && damage_source.wasCausedByPlayer();
   }

   public static boolean isArrowDamage(DamageSource damage_source) {
      return damage_source != null && damage_source.getImmediateEntity() instanceof EntityArrow;
   }

   public boolean isAnvil() {
      return this == anvil;
   }

   public boolean isFallingBlock() {
      return this == fallingBlock;
   }

   public boolean isDrowning() {
      return this == drown;
   }

   public boolean isStarving() {
      return this == starve;
   }

   public boolean isSnowball() {
      return this.isProjectile() && this.getImmediateEntity() instanceof EntitySnowball;
   }

   public boolean isPlayerThrownSnowball() {
      return this.isSnowball() && this.wasCausedByPlayer();
   }

   public boolean isSunlight() {
      return this == sunlight;
   }

   public boolean isArrowDamage() {
      return this.isProjectile() && this.getImmediateEntity() instanceof EntityArrow;
   }

   public boolean isArrowFromPlayer() {
      return this.isArrowDamage() && this.wasCausedByPlayer();
   }

   public boolean isCactus() {
      return this == cactus;
   }

   public boolean isWater() {
      return this == water;
   }

   public boolean isEggDamage() {
      return this.isProjectile() && this.getImmediateEntity() instanceof EntityEgg;
   }

   public boolean isBrickDamage() {
      return this.isProjectile() && this.getImmediateEntity() instanceof EntityBrick;
   }

   public boolean isPepsinDamage() {
      return this == pepsin;
   }

   public boolean isAcidDamage() {
      return this == acid;
   }

   public boolean isFallDamage() {
      return this == fall;
   }

   public boolean isPoison() {
      return this == poison;
   }

   public boolean isGelatinousSphereDamage() {
      return this.isProjectile() && this.getImmediateEntity() instanceof EntityGelatinousSphere;
   }

   public int getLootingModifier() {
      return this.getResponsibleEntity() instanceof EntityLivingBase ? EnchantmentHelper.getLootingModifier(this.getResponsibleEntity().getAsEntityLivingBase()) : 0;
   }

   public int getButcheringModifier() {
      return this.getResponsibleEntity() instanceof EntityLivingBase ? EnchantmentHelper.getButcheringModifier(this.getResponsibleEntity().getAsEntityLivingBase()) : 0;
   }
}
