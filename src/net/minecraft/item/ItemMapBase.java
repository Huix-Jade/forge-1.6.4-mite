package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;

public class ItemMapBase extends Item {
   protected ItemMapBase(int id, String texture) {
      super(id, Material.paper, texture);
      this.setCraftingDifficultyAsComponent(100.0F);
   }

   public boolean isMap() {
      return true;
   }

   public Packet createMapDataPacket(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
      return null;
   }
}
