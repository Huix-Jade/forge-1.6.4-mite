package net.minecraft.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.RandomItemListEntry;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public class EntityRevenant extends EntityZombie {
   public EntityRevenant(World world) {
      super(world);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.followRange, 40.0);
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.25999999046325684);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 7.0);
      this.setEntityAttribute(field_110186_bp, this.rand.nextDouble() * 0.10000000149011612);
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, 30.0);
   }

   public void addRandomWeapon() {
      List items = new ArrayList();
      items.add(new RandomItemListEntry(Item.swordRustedIron, 2));
      if (this.worldObj.getDayOfWorld() >= 10 && !Minecraft.isInTournamentMode()) {
         items.add(new RandomItemListEntry(Item.battleAxeRustedIron, 1));
      }

      if (this.worldObj.getDayOfWorld() >= 20 && !Minecraft.isInTournamentMode()) {
         items.add(new RandomItemListEntry(Item.warHammerRustedIron, 1));
      }

      RandomItemListEntry entry = (RandomItemListEntry)WeightedRandom.getRandomItem(this.rand, (Collection)items);
      this.setHeldItemStack((new ItemStack(entry.item)).randomizeForMob(this, true));
   }

   protected void addRandomEquipment() {
      this.addRandomWeapon();
      this.setBoots((new ItemStack(Item.bootsRustedIron)).randomizeForMob(this, true));
      this.setLeggings((new ItemStack(Item.legsRustedIron)).randomizeForMob(this, true));
      this.setCuirass((new ItemStack(Item.plateRustedIron)).randomizeForMob(this, true));
      this.setHelmet((new ItemStack(Item.helmetRustedIron)).randomizeForMob(this, true));
   }

   public boolean isRevenant() {
      return true;
   }

   public void setVillager(boolean villager, int profession) {
      Minecraft.setErrorMessage("setVillager: why setting villager for revenant?");
      (new Exception()).printStackTrace();
   }

   public boolean isVillager() {
      return false;
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 3;
   }

   public int getMaxSpawnedInChunk() {
      return 1;
   }
}
