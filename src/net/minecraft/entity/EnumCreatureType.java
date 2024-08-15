package net.minecraft.entity;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;

public enum EnumCreatureType {
   monster(IMob.class, 50, Material.air),
   animal(EntityAnimal.class, 10, Material.air),
   ambient(EntityAmbientCreature.class, 5, Material.air),
   aquatic(EntityWaterMob.class, 5, Material.water);

   private final Class creatureClass;
   private final int maxNumberOfCreature;
   private final Material creatureMaterial;

   private EnumCreatureType(Class par3Class, int par4, Material par5Material) {
      this.creatureClass = par3Class;
      this.maxNumberOfCreature = par4;
      this.creatureMaterial = par5Material;
   }

   public Class getCreatureClass() {
      return this.creatureClass;
   }

   public int getMaxNumberOfCreature() {
      return this.maxNumberOfCreature;
   }

   public Material getCreatureMaterial() {
      return this.creatureMaterial;
   }

   public static EnumCreatureType getCreatureType(EntityLiving entity_living) {
      if (entity_living instanceof EntityAmbientCreature) {
         return ambient;
      } else if (entity_living instanceof EntityWaterMob) {
         return aquatic;
      } else if (entity_living instanceof IMob) {
         return monster;
      } else if (entity_living instanceof EntityAnimal) {
         return animal;
      } else {
         Minecraft.setErrorMessage("getCreatureType: unknown creature type for " + entity_living);
         return null;
      }
   }
}
