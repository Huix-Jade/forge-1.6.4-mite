package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockMushroomCap extends Block {
   private static final String[] field_94429_a = new String[]{"skin_brown", "skin_red"};
   private final int mushroomType;
   private Icon[] iconArray;
   private Icon field_94426_cO;
   private Icon field_94427_cP;

   public BlockMushroomCap(int par1, Material par2Material, int par3) {
      super(par1, par2Material, new BlockConstants());
      this.mushroomType = par3;
      this.setCushioning(0.4F);
   }

   public String getMetadataNotes() {
      return "All bits used for texturing permutations";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 11 || metadata == 14;
   }

   public Icon getIcon(int par1, int par2) {
      return par2 == 10 && par1 > 1 ? this.field_94426_cO : (par2 >= 1 && par2 <= 9 && par1 == 1 ? this.iconArray[this.mushroomType] : (par2 >= 1 && par2 <= 3 && par1 == 2 ? this.iconArray[this.mushroomType] : (par2 >= 7 && par2 <= 9 && par1 == 3 ? this.iconArray[this.mushroomType] : ((par2 == 1 || par2 == 4 || par2 == 7) && par1 == 4 ? this.iconArray[this.mushroomType] : ((par2 == 3 || par2 == 6 || par2 == 9) && par1 == 5 ? this.iconArray[this.mushroomType] : (par2 == 14 ? this.iconArray[this.mushroomType] : (par2 == 15 ? this.field_94426_cO : this.field_94427_cP)))))));
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Block.mushroomBrown.blockID + this.mushroomType;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.iconArray = new Icon[field_94429_a.length];

      for(int var2 = 0; var2 < this.iconArray.length; ++var2) {
         this.iconArray[var2] = par1IconRegister.registerIcon(this.getTextureName() + "_" + field_94429_a[var2]);
      }

      this.field_94427_cP = par1IconRegister.registerIcon(this.getTextureName() + "_" + "inside");
      this.field_94426_cO = par1IconRegister.registerIcon(this.getTextureName() + "_" + "skin_stem");
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      int quantity = info.world.rand.nextInt(10) - 7;
      if (info.wasExploded()) {
         --quantity;
      }

      return this.dropBlockAsEntityItem(info, Block.mushroomBrown.blockID + this.mushroomType, 0, quantity, 1.0F);
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this == Block.mushroomCapBrown ? "brown, giant" : (this == Block.mushroomCapRed ? "red, giant" : super.getNameDisambiguationForReferenceFile(metadata));
   }
}
