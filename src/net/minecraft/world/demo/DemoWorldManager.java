package net.minecraft.world.demo;

import net.minecraft.item.ItemInWorldManager;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumFace;
import net.minecraft.world.World;

public class DemoWorldManager extends ItemInWorldManager {
   private boolean field_73105_c;
   private boolean demoTimeExpired;
   private int field_73104_e;
   private int field_73102_f;

   public DemoWorldManager(World par1World) {
      super(par1World);
   }

   private void sendDemoReminder() {
      if (this.field_73104_e > 100) {
         this.thisPlayerMP.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("demo.reminder"));
         this.field_73104_e = 0;
      }

   }

   public void onBlockClicked(int x, int y, int z, EnumFace face) {
      if (this.demoTimeExpired) {
         this.sendDemoReminder();
      } else {
         super.onBlockClicked(x, y, z, face);
      }

   }

   public boolean tryHarvestBlock(int par1, int par2, int par3) {
      return this.demoTimeExpired ? false : super.tryHarvestBlock(par1, par2, par3);
   }
}
