//package net.minecraft.item;
//
//import net.minecraft.block.Block;
//
//public enum EnumToolMaterial {
//   WOOD(0, 59, 2.0F, 0.0F, 15),
//   STONE(1, 131, 4.0F, 1.0F, 5),
//   IRON(2, 250, 6.0F, 2.0F, 14),
//   EMERALD(3, 1561, 8.0F, 3.0F, 10),
//   GOLD(0, 32, 12.0F, 0.0F, 22);
//
//   private final int harvestLevel;
//   private final int maxUses;
//   private final float efficiencyOnProperMaterial;
//   private final float damageVsEntity;
//   private final int enchantability;
//
//   private EnumToolMaterial(int var3, int var4, float var5, float var6, int var7) {
//      this.harvestLevel = var3;
//      this.maxUses = var4;
//      this.efficiencyOnProperMaterial = var5;
//      this.damageVsEntity = var6;
//      this.enchantability = var7;
//   }
//
//   public int getMaxUses() {
//      return this.maxUses;
//   }
//
//   public float getEfficiencyOnProperMaterial() {
//      return this.efficiencyOnProperMaterial;
//   }
//
//   public float getDamageVsEntity() {
//      return this.damageVsEntity;
//   }
//
//   public int getHarvestLevel() {
//      return this.harvestLevel;
//   }
//
//   public int getEnchantability() {
//      return this.enchantability;
//   }
//
//   public int getToolCraftingMaterial() {
//      if (this == WOOD) {
//         return Block.C.blockID;
//      } else if (this == STONE) {
//         return Block.cobblestone.blockID;
//      } else if (this == GOLD) {
//         return Item.ingotGold.itemID;
//      } else if (this == IRON) {
//         return Item.ingotIron.itemID;
//      } else {
//         return this == EMERALD ? Item.diamond.itemID : 0;
//      }
//   }
//}
