package net.minecraft.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.RandomItemListEntry;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public class EntityAncientBoneLord extends EntityBoneLord {
   public EntityAncientBoneLord(World world) {
      super(world);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.followRange, 40.0);
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.27000001072883606);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 8.0);
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, 24.0);
   }

   public void addRandomWeapon() {
      List items = new ArrayList();
      items.add(new RandomItemListEntry(Item.swordAncientMetal, 2));
      if (!Minecraft.isInTournamentMode()) {
         items.add(new RandomItemListEntry(Item.battleAxeAncientMetal, 1));
         items.add(new RandomItemListEntry(Item.warHammerAncientMetal, 1));
      }

      RandomItemListEntry entry = (RandomItemListEntry)WeightedRandom.getRandomItem(this.rand, (Collection)items);
      this.setHeldItemStack((new ItemStack(entry.item)).randomizeForMob(this, true));
   }

   protected void addRandomEquipment() {
      this.addRandomWeapon();
      this.setBoots((new ItemStack(Item.bootsAncientMetal)).randomizeForMob(this, true));
      this.setLeggings((new ItemStack(Item.legsAncientMetal)).randomizeForMob(this, true));
      this.setCuirass((new ItemStack(Item.plateAncientMetal)).randomizeForMob(this, true));
      this.setHelmet((new ItemStack(Item.helmetAncientMetal)).randomizeForMob(this, true));
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 2;
   }
}
