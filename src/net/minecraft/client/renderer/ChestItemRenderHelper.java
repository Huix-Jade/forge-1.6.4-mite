package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.EnumChestType;

public class ChestItemRenderHelper {
   public static ChestItemRenderHelper instance = new ChestItemRenderHelper();
   private TileEntityChest theChest;
   private TileEntityChest field_142033_c;
   private TileEntityChest copperChest;
   private TileEntityChest silverChest;
   private TileEntityChest goldChest;
   private TileEntityChest ironChest;
   private TileEntityChest mithrilChest;
   private TileEntityChest adamantiumChest;
   private TileEntityChest ancientMetalChest;
   private TileEntityEnderChest theEnderChest;

   public ChestItemRenderHelper() {
      this.theChest = new TileEntityChest(EnumChestType.normal, Block.chest);
      this.field_142033_c = new TileEntityChest(EnumChestType.trapped, Block.chestTrapped);
      this.copperChest = new TileEntityChest(EnumChestType.strongbox, Block.chestCopper);
      this.silverChest = new TileEntityChest(EnumChestType.strongbox, Block.chestSilver);
      this.goldChest = new TileEntityChest(EnumChestType.strongbox, Block.chestGold);
      this.ironChest = new TileEntityChest(EnumChestType.strongbox, Block.chestIron);
      this.mithrilChest = new TileEntityChest(EnumChestType.strongbox, Block.chestMithril);
      this.adamantiumChest = new TileEntityChest(EnumChestType.strongbox, Block.chestAdamantium);
      this.ancientMetalChest = new TileEntityChest(EnumChestType.strongbox, Block.chestAncientMetal);
      this.theEnderChest = new TileEntityEnderChest();
   }

   public void renderChest(Block par1Block, int par2, float par3) {
      if (par1Block.blockID == Block.enderChest.blockID) {
         TileEntityRenderer.instance.renderTileEntityAt(this.theEnderChest, 0.0, 0.0, 0.0, 0.0F);
      } else if (par1Block.blockID == Block.chestTrapped.blockID) {
         TileEntityRenderer.instance.renderTileEntityAt(this.field_142033_c, 0.0, 0.0, 0.0, 0.0F);
      } else if (par1Block.blockID == Block.chestCopper.blockID) {
         TileEntityRenderer.instance.renderTileEntityAt(this.copperChest, 0.0, 0.0, 0.0, 0.0F);
      } else if (par1Block.blockID == Block.chestSilver.blockID) {
         TileEntityRenderer.instance.renderTileEntityAt(this.silverChest, 0.0, 0.0, 0.0, 0.0F);
      } else if (par1Block.blockID == Block.chestGold.blockID) {
         TileEntityRenderer.instance.renderTileEntityAt(this.goldChest, 0.0, 0.0, 0.0, 0.0F);
      } else if (par1Block.blockID == Block.chestIron.blockID) {
         TileEntityRenderer.instance.renderTileEntityAt(this.ironChest, 0.0, 0.0, 0.0, 0.0F);
      } else if (par1Block.blockID == Block.chestMithril.blockID) {
         TileEntityRenderer.instance.renderTileEntityAt(this.mithrilChest, 0.0, 0.0, 0.0, 0.0F);
      } else if (par1Block.blockID == Block.chestAdamantium.blockID) {
         TileEntityRenderer.instance.renderTileEntityAt(this.adamantiumChest, 0.0, 0.0, 0.0, 0.0F);
      } else if (par1Block.blockID == Block.chestAncientMetal.blockID) {
         TileEntityRenderer.instance.renderTileEntityAt(this.ancientMetalChest, 0.0, 0.0, 0.0, 0.0F);
      } else {
         TileEntityRenderer.instance.renderTileEntityAt(this.theChest, 0.0, 0.0, 0.0, 0.0F);
      }

   }
}
