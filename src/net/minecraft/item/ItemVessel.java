package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityEarthElemental;
import net.minecraft.entity.EntityFireElemental;
import net.minecraft.entity.EntityLivestock;
import net.minecraft.entity.EntityNetherspawn;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;

public abstract class ItemVessel extends Item {
   private Material vessel_material;
   private Material contents;
   private int standard_volume;

   public ItemVessel(int id, Material vessel_material, Material contents_material, int standard_volume, int max_stack_size_empty, int max_stack_size_full, String texture) {
      super(id, vessel_material, texture);
      this.vessel_material = vessel_material;
      if (contents_material == null) {
         this.setMaxStackSize(max_stack_size_empty);
      } else {
         this.contents = contents_material;
         this.addMaterial(new Material[]{contents_material});
         this.setContainerItem(this.getEmptyVessel());
         this.setMaxStackSize(max_stack_size_full);
      }

      this.standard_volume = standard_volume;
   }

   public Material getVesselMaterial() {
      return this.vessel_material;
   }

   public int getStandardVolume() {
      return this.standard_volume;
   }

   public boolean isEmpty() {
      return this.getContents() == null;
   }

   public Material getContents() {
      return this.contents;
   }

   public boolean contains(Material material) {
      return this.getContents() == material;
   }

   public boolean isEatable(int item_subtype) {
      return this.getContents() != null && this.getContents().isEdible();
   }

   public boolean isDrinkable(int item_subtype) {
      return this.getContents() != null && this.getContents().isDrinkable();
   }

   public ItemVessel setAnimalProduct() {
      super.setAnimalProduct();
      return this;
   }

   public ItemVessel setPlantProduct() {
      super.setPlantProduct();
      return this;
   }

   public boolean canContentsDouseFire() {
      return this.contents != null && this.contents.canDouseFire();
   }

   public final ItemVessel getEmptyVessel() {
      return this.getPeerForContents((Material)null);
   }

   public abstract ItemVessel getPeerForContents(Material var1);

   public abstract ItemVessel getPeerForVesselMaterial(Material var1);

   public Item getItemProducedOnItemUseFinish() {
      return this.getContainerItem();
   }

   public ItemStack getItemProducedWhenDestroyed(ItemStack item_stack, DamageSource damage_source) {
      return this.vessel_material.isHarmedBy(damage_source) ? null : new ItemStack(this.getContainerItem(), item_stack.stackSize);
   }

   public int getSimilarityToItem(Item item) {
      if (item instanceof ItemVessel) {
         ItemVessel vessel = (ItemVessel)item;
         if (vessel.getEmptyVessel() == this.getEmptyVessel()) {
            return 1;
         }
      }

      return super.getSimilarityToItem(item);
   }

   public boolean tryEntityInteraction(Entity entity, EntityPlayer player, ItemStack item_stack) {
      if (entity instanceof EntityLivestock) {
         if (this.isEmpty()) {
            if (entity instanceof EntityCow) {
               EntityCow cow = (EntityCow)entity;
               if (cow.getMilk() >= this.standard_volume * 25) {
                  if (player.onServer()) {
                     cow.setMilk(cow.getMilk() - this.standard_volume * 25);
                     if (!player.inCreativeMode()) {
                        player.convertOneOfHeldItem(new ItemStack(this.getPeerForContents(Material.milk)));
                     }
                  }

                  return true;
               }
            }
         } else if (this.contains(Material.water)) {
            EntityLivestock livestock = (EntityLivestock)entity;
            if (livestock.isThirsty()) {
               if (player.onServer()) {
                  livestock.addWater(0.25F * (float)this.standard_volume);
                  if (!player.inCreativeMode()) {
                     player.convertOneOfHeldItem(new ItemStack(this.getEmptyVessel()));
                  }
               }

               return true;
            }
         }
      } else if (this.canContentsDouseFire()) {
         if (entity instanceof EntityFireElemental) {
            if (player.onServer()) {
               entity.attackEntityFrom(new Damage(DamageSource.water, this instanceof ItemBucket ? 20.0F : 5.0F));
               entity.causeQuenchEffect();
               if (!player.inCreativeMode()) {
                  player.convertOneOfHeldItem(new ItemStack(this.getEmptyVessel()));
               }
            }

            return true;
         }

         if (entity instanceof EntityNetherspawn) {
            if (player.onServer()) {
               entity.attackEntityFrom(new Damage(DamageSource.water, this instanceof ItemBucket ? 8.0F : 2.0F));
               entity.causeQuenchEffect();
               if (!player.inCreativeMode()) {
                  player.convertOneOfHeldItem(new ItemStack(this.getEmptyVessel()));
               }
            }

            return true;
         }

         if (entity instanceof EntityEarthElemental) {
            EntityEarthElemental elemental = (EntityEarthElemental)entity;
            if (elemental.isMagma()) {
               if (player.onServer()) {
                  elemental.convertToNormal(true);
                  if (!player.inCreativeMode()) {
                     player.convertOneOfHeldItem(new ItemStack(this.getEmptyVessel()));
                  }
               }

               return true;
            }
         }
      }

      return super.tryEntityInteraction(entity, player, item_stack);
   }
}
