package net.minecraft.client.resources;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

class GuiScreenTemporaryResourcePackSelectSelectionList extends GuiSlot {
   private final ResourcePackRepository field_110511_b;
   private ResourceLocation field_110513_h;
   final GuiScreenTemporaryResourcePackSelect field_110512_a;

   public GuiScreenTemporaryResourcePackSelectSelectionList(GuiScreenTemporaryResourcePackSelect par1GuiScreenTemporaryResourcePackSelect, ResourcePackRepository par2ResourcePackRepository) {
      super(GuiScreenTemporaryResourcePackSelect.func_110344_a(par1GuiScreenTemporaryResourcePackSelect), par1GuiScreenTemporaryResourcePackSelect.width, par1GuiScreenTemporaryResourcePackSelect.height, 32, par1GuiScreenTemporaryResourcePackSelect.height - 55 + 4, 36);
      this.field_110512_a = par1GuiScreenTemporaryResourcePackSelect;
      this.field_110511_b = par2ResourcePackRepository;
      par2ResourcePackRepository.updateRepositoryEntriesAll();
   }

   protected int getSize() {
      return 1 + this.field_110511_b.getRepositoryEntriesAll().size();
   }

   protected void elementClicked(int par1, boolean par2) {
   }

   protected boolean isSelected(int par1) {
      return false;
   }

   protected int getContentHeight() {
      return this.getSize() * 36;
   }

   protected void drawBackground() {
      this.field_110512_a.drawDefaultBackground();
   }

   protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5Tessellator) {
      TextureManager var6 = GuiScreenTemporaryResourcePackSelect.func_110340_f(this.field_110512_a).getTextureManager();
      if (par1 == 0) {
         try {
            ResourcePack var12 = this.field_110511_b.rprDefaultResourcePack;
            PackMetadataSection var13 = (PackMetadataSection)var12.getPackMetadata(this.field_110511_b.rprMetadataSerializer, "pack");
            if (this.field_110513_h == null) {
               this.field_110513_h = var6.getDynamicTextureLocation("texturepackicon", new DynamicTexture(var12.getPackImage()));
            }

            var6.bindTexture(this.field_110513_h);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            par5Tessellator.startDrawingQuads();
            par5Tessellator.setColorOpaque_I(16777215);
            par5Tessellator.addVertexWithUV((double)par2, (double)(par3 + par4), 0.0, 0.0, 1.0);
            par5Tessellator.addVertexWithUV((double)(par2 + 32), (double)(par3 + par4), 0.0, 1.0, 1.0);
            par5Tessellator.addVertexWithUV((double)(par2 + 32), (double)par3, 0.0, 1.0, 0.0);
            par5Tessellator.addVertexWithUV((double)par2, (double)par3, 0.0, 0.0, 0.0);
            par5Tessellator.draw();
            this.field_110512_a.drawString(GuiScreenTemporaryResourcePackSelect.func_130017_g(this.field_110512_a), "Default", par2 + 32 + 2, par3 + 1, 16777215);
            this.field_110512_a.drawString(GuiScreenTemporaryResourcePackSelect.func_130016_h(this.field_110512_a), var13.getPackDescription(), par2 + 32 + 2, par3 + 12 + 10, 8421504);
         } catch (IOException var11) {
         }
      } else {
         ResourcePackRepositoryEntry var7 = (ResourcePackRepositoryEntry)this.field_110511_b.getRepositoryEntriesAll().get(par1 - 1);
         var7.bindTexturePackIcon(var6);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         par5Tessellator.startDrawingQuads();
         par5Tessellator.setColorOpaque_I(16777215);
         par5Tessellator.addVertexWithUV((double)par2, (double)(par3 + par4), 0.0, 0.0, 1.0);
         par5Tessellator.addVertexWithUV((double)(par2 + 32), (double)(par3 + par4), 0.0, 1.0, 1.0);
         par5Tessellator.addVertexWithUV((double)(par2 + 32), (double)par3, 0.0, 1.0, 0.0);
         par5Tessellator.addVertexWithUV((double)par2, (double)par3, 0.0, 0.0, 0.0);
         par5Tessellator.draw();
         String var8 = var7.getResourcePackName();
         if (var8.length() > 32) {
            var8 = var8.substring(0, 32).trim() + "...";
         }

         this.field_110512_a.drawString(GuiScreenTemporaryResourcePackSelect.func_110337_i(this.field_110512_a), var8, par2 + 32 + 2, par3 + 1, 16777215);
         List var9 = GuiScreenTemporaryResourcePackSelect.func_110335_j(this.field_110512_a).listFormattedStringToWidth(var7.getTexturePackDescription(), 183);

         for(int var10 = 0; var10 < 2 && var10 < var9.size(); ++var10) {
            this.field_110512_a.drawString(GuiScreenTemporaryResourcePackSelect.func_110338_k(this.field_110512_a), (String)var9.get(var10), par2 + 32 + 2, par3 + 12 + 10 * var10, 8421504);
         }
      }

   }

   static ResourcePackRepository func_110510_a(GuiScreenTemporaryResourcePackSelectSelectionList par0GuiScreenTemporaryResourcePackSelectSelectionList) {
      return par0GuiScreenTemporaryResourcePackSelectSelectionList.field_110511_b;
   }
}
