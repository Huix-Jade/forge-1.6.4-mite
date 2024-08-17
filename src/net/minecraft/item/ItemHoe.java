package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFace;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class ItemHoe extends ItemTool {
   protected ItemHoe(int par1, Material material) {
      super(par1, material);
      this.addMaterialsEffectiveAgainst(new Material[]{Material.cake, Material.craftedSnow, Material.grass, Material.dirt, Material.sand, Material.snow});
      this.addBlocksEffectiveAgainst(new Block[]{Block.carrot, Block.potato, Block.onions});
   }

   public String getToolType() {
      return "hoe";
   }

   public float getBaseHarvestEfficiency(Block block) {
      return super.getBaseHarvestEfficiency(block) * 0.5F;
   }

   public float getBaseDamageVsEntity() {
      return 1.0F;
   }

   public boolean canBlock() {
      return false;
   }

   public int getNumComponentsForDurability() {
      return 2;
   }

   public float getBaseDecayRateForBreakingBlock(Block block) {
      return 2.0F;
   }

   public float getBaseDecayRateForAttackingEntity(ItemStack item_stack) {
      return 2.0F;
   }

   public static boolean tryTillSoil(World world, int x, int y, int z, EnumFace face, EntityPlayer player, ItemStack item_stack) {
      if (!player.canPlayerEdit(x, y, z, item_stack)) {
         return false;
      } else if (face.isBottom()) {
         return false;
      } else if (!world.isAirBlock(x, y + 1, z) && world.getBlockWithRefreshedBounds(x, y + 1, z).getBlockBoundsMinY(Minecraft.getThreadIndex()) <= 0.0) {
         return false;
      } else {
         Block block = world.getBlock(x, y, z);
         if (block != Block.grass && block != Block.dirt) {
            return false;
         } else {
            UseHoeEvent event = new UseHoeEvent(player, player.itemInUse, world, x, y, z);
            if (MinecraftForge.EVENT_BUS.post(event))
            {
               return false;
            }

            if (event.getResult() == Event.Result.ALLOW)
            {
               player.tryDamageHeldItem(DamageSource.generic, 50);
               return true;
            }

            if (player.onClient()) {
               player.swingArm();
               Minecraft.theMinecraft.playerController.setUseButtonDelayOverride(200);
            } else {
               world.playSoundAtBlock(x, y, z, Block.tilledField.stepSound.getStepSound(), (Block.tilledField.stepSound.getVolume() + 1.0F) / 2.0F, Block.tilledField.stepSound.getPitch() * 0.8F);
               player.tryDamageHeldItem(DamageSource.generic, 50);
               player.addHungerServerSide(world.getBlockHardness(x, y, z) / 2.0F * EnchantmentHelper.getEnduranceModifier(player));
               world.setBlock(x, y, z, Block.tilledField.blockID);
               if (Math.random() < (double)EnchantmentHelper.getEnchantmentLevelFraction(Enchantment.fertility, item_stack)) {
                  BlockFarmland.setFertilized(world, x, y, z, true);
               }
            }

            return true;
         }
      }
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, true);
      if (rc != null && rc.isBlock()) {
         return rc.face_hit.isTop() ? tryTillSoil(rc.world, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, rc.face_hit, player, player.getHeldItemStack()) : false;
      } else {
         return false;
      }
   }

   public boolean onBlockDestroyed(BlockBreakInfo info) {
      if (!info.world.isRemote) {
         Block block = Block.getBlock(info.block_id);
         if (block instanceof BlockCrops && this.isEffectiveAgainstBlock(block, info.getMetadata())) {
            BlockCrops crops = (BlockCrops)block;
            if (!crops.isDead() && crops.isMature(info.getMetadata()) && Math.random() < (double)EnchantmentHelper.getEnchantmentLevelFraction(Enchantment.fertility, info.getHarvesterItemStack())) {
               BlockFarmland.setFertilized(info.world, info.x, info.y - 1, info.z, true);
            }
         }
      }

      return super.onBlockDestroyed(info);
   }

   public Class[] getSimilarClasses() {
      return new Class[]{ItemMattock.class, ItemShovel.class};
   }
}
