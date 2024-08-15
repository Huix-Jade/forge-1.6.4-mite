package net.minecraft.entity.monster;

import net.minecraft.entity.EntityLivingData;

class EntityZombieGroupData implements EntityLivingData {
   public boolean field_142048_a;
   public boolean field_142046_b;
   // $FF: synthetic field
   final EntityZombie field_142047_c;

   private EntityZombieGroupData(EntityZombie var1, boolean var2, boolean var3) {
      this.field_142047_c = var1;
      this.field_142048_a = false;
      this.field_142046_b = false;
      this.field_142048_a = var2;
      this.field_142046_b = var3;
   }

   // $FF: synthetic method
   EntityZombieGroupData(EntityZombie var1, boolean var2, boolean var3, EntityZombieINNER1 var4) {
      this(var1, var2, var3);
   }
}
