//package net.minecraft.util;
//
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.item.ItemStack;
//
//public class EntityDamageSourceIndirect extends EntityDamageSource {
//   private Entity indirectEntity;
//
//   public EntityDamageSourceIndirect(String var1, Entity var2, Entity var3) {
//      super(var1, var2);
//      this.indirectEntity = var3;
//   }
//
//   public Entity getSourceOfDamage() {
//      return this.p;
//   }
//
//   public Entity getEntity() {
//      return this.indirectEntity;
//   }
//
//   public ChatMessageComponent getDeathMessage(EntityLivingBase var1) {
//      String var2 = this.indirectEntity == null ? this.p.getTranslatedEntityName() : this.indirectEntity.getTranslatedEntityName();
//      ItemStack var3 = this.indirectEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.indirectEntity).aZ() : null;
//      String var4 = "death.attack." + this.damageType;
//      String var5 = var4 + ".item";
//      return var3 != null && var3.hasDisplayName() && StatCollector.func_94522_b(var5) ? ChatMessageComponent.createFromTranslationWithSubstitutions(var5, var1.getTranslatedEntityName(), var2, var3.getDisplayName()) : ChatMessageComponent.createFromTranslationWithSubstitutions(var4, var1.getTranslatedEntityName(), var2);
//   }
//}
