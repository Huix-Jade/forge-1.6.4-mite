package net.minecraft.entity;

import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.util.Damage;
import net.minecraft.world.World;

public interface IEntityMultiPart {
   World func_82194_d();

   EntityDamageResult attackEntityFromPart(EntityDragonPart var1, Damage var2);
}
