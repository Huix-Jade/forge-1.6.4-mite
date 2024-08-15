package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumBlockBreakReason;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class BlockBreakInfo {
   public World world;
   public int x;
   public int y;
   public int z;
   public int block_id;
   public Block block;
   public TileEntity tile_entity;
   public EnumBlockBreakReason reason;
   private int metadata;
   public int neighbor_block_id;
   public Block crushing_block;
   public BlockFluid flooding_block;
   public Entity responsible_entity;
   public ItemStack responsible_item_stack;
   public Explosion explosion;
   private boolean was_silk_harvested;
   public int damage;
   public int drop_x;
   public int drop_y;
   public int drop_z;
   private AxisAlignedBB drop_bounds;
   private static AxisAlignedBB drop_bounds_default = AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).scale(0.5);

   public float chance = 1.0F;

   public BlockBreakInfo(World world, int x, int y, int z) {
      if (world.isRemote) {
         Minecraft.setErrorMessage("BlockBreakInfo: Why creating this object on client?", true);
         (new Exception()).printStackTrace();
      }

      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
      this.block_id = world.getBlockId(x, y, z);
      this.block = Block.blocksList[this.block_id];
      this.metadata = world.getBlockMetadata(x, y, z);
      this.tile_entity = world.getBlockTileEntity(x, y, z);
      this.drop_x = x;
      this.drop_y = y;
      this.drop_z = z;
      if (this.block == null) {
         this.drop_bounds = drop_bounds_default;
      } else {
         this.drop_bounds = this.block.getSelectedBoundingBoxFromPool(world, x, y, z).translate((double)(-x), (double)(-y), (double)(-z)).scale(0.5);
      }

   }

   public BlockBreakInfo(int block_id, int metadata) {
      this.block_id = block_id;
      this.block = Block.getBlock(block_id);
      this.metadata = metadata;
   }


   public BlockBreakInfo setMetadata(int metadata) {
      this.metadata = metadata;
      return this;
   }

   public BlockBreakInfo setBlock(Block block, int metadata) {
      this.block_id = block.blockID;
      this.block = block;
      this.metadata = metadata;
      return this;
   }

   public BlockBreakInfo setBlock(Block block, int metadata, int flags) {
      this.setBlock(block, metadata);
      this.world.setBlock(this.x, this.y, this.z, block.blockID, metadata, flags);
      return this;
   }

   public BlockBreakInfo setWasNotLegal() {
      this.reason = EnumBlockBreakReason.was_not_legal;
      return this;
   }

   public BlockBreakInfo setNeighborChanged(int neighbor_block_id) {
      this.reason = EnumBlockBreakReason.neighbor_changed;
      this.neighbor_block_id = neighbor_block_id;
      return this;
   }

   public BlockBreakInfo setReplaced() {
      this.reason = EnumBlockBreakReason.replaced;
      return this;
   }

   public BlockBreakInfo setFlooded(BlockFluid flooding_block) {
      this.reason = EnumBlockBreakReason.flooded;
      this.flooding_block = flooding_block;
      return this;
   }

   public BlockBreakInfo setHarvestedBy(EntityLivingBase harvesting_entity) {
      this.reason = EnumBlockBreakReason.harvested;
      this.responsible_entity = harvesting_entity;
      this.responsible_item_stack = harvesting_entity.getHeldItemStack();
      if (this.block == null) {
         Minecraft.setErrorMessage("setHarvestedBy: block is null (" + harvesting_entity.getEntityName() + ")");
         Debug.printStackTrace();
         return this;
      } else {
         this.was_silk_harvested = harvesting_entity instanceof EntityLivingBase && harvesting_entity.getAsEntityLivingBase().canSilkHarvestBlock(this.block, this.metadata);
         return this;
      }
   }

   public BlockBreakInfo setPickedBy(EntityLivingBase picking_entity) {
      this.reason = EnumBlockBreakReason.picked;
      this.responsible_entity = picking_entity;
      if (this.block == null) {
         Minecraft.setErrorMessage("setPickedBy: block is null (" + picking_entity.getEntityName() + ")");
         return this;
      } else {
         return this;
      }
   }

   public BlockBreakInfo setTrampledBy(Entity trampling_entity) {
      this.reason = EnumBlockBreakReason.trampled;
      this.responsible_entity = trampling_entity;
      return this;
   }

   public BlockBreakInfo setExploded(Explosion explosion) {
      this.reason = EnumBlockBreakReason.exploded;
      this.explosion = explosion;
      return this;
   }

   public BlockBreakInfo setDroppedSelf() {
      this.reason = EnumBlockBreakReason.self_dropped;
      return this;
   }

   public BlockBreakInfo setCrushed(Block crushing_block) {
      this.reason = EnumBlockBreakReason.crushed;
      this.crushing_block = crushing_block;
      return this;
   }

   public BlockBreakInfo setEatenBy(Entity entity) {
      this.reason = EnumBlockBreakReason.eaten;
      this.responsible_entity = entity;
      return this;
   }

   public BlockBreakInfo setCollidedWith(Entity entity) {
      this.reason = EnumBlockBreakReason.collided_with_entity;
      this.responsible_entity = entity;
      return this;
   }

   public BlockBreakInfo setSilverfish(Entity entity) {
      this.reason = EnumBlockBreakReason.silverfish;
      this.responsible_entity = entity;
      return this;
   }

   public BlockBreakInfo setDrought() {
      this.reason = EnumBlockBreakReason.drought;
      return this;
   }

   public BlockBreakInfo setSnowedUpon() {
      this.reason = EnumBlockBreakReason.snowfall;
      return this;
   }

   public BlockBreakInfo setDestroyedBy(Entity entity) {
      this.reason = EnumBlockBreakReason.destroyed;
      this.responsible_entity = entity;
      return this;
   }

   public BlockBreakInfo setWindfall() {
      this.reason = EnumBlockBreakReason.windfall;
      return this;
   }

   public BlockBreakInfo setOther() {
      this.reason = EnumBlockBreakReason.other;
      return this;
   }

   public int getMetadata() {
      return this.metadata;
   }

   public boolean wasNotLegal() {
      return this.reason == EnumBlockBreakReason.was_not_legal;
   }

   public boolean wasReplaced() {
      return this.reason == EnumBlockBreakReason.replaced;
   }

   public boolean wasFlooded() {
      return this.reason == EnumBlockBreakReason.flooded;
   }

   public boolean wasHarvested() {
      return this.reason == EnumBlockBreakReason.harvested;
   }

   public boolean wasHarvestedByPlayer() {
      return this.reason == EnumBlockBreakReason.harvested && this.responsible_entity instanceof EntityPlayer;
   }

   public boolean wasPicked() {
      return this.reason == EnumBlockBreakReason.picked;
   }

   public boolean wasPickedByPlayer() {
      return this.wasPicked() && this.responsible_entity instanceof EntityPlayer;
   }

   public boolean wasSilkHarvested() {
      return this.was_silk_harvested;
   }

   public boolean wasTrampled() {
      return this.reason == EnumBlockBreakReason.trampled;
   }

   public boolean wasExploded() {
      return this.reason == EnumBlockBreakReason.exploded;
   }

   public boolean wasSelfDropped() {
      return this.reason == EnumBlockBreakReason.self_dropped;
   }

   public boolean wasCrushed() {
      return this.reason == EnumBlockBreakReason.crushed;
   }

   public boolean wasEaten() {
      return this.reason == EnumBlockBreakReason.eaten;
   }

   public boolean wasCollidedWithEntity() {
      return this.reason == EnumBlockBreakReason.collided_with_entity;
   }

   public boolean wasSilverfish() {
      return this.reason == EnumBlockBreakReason.silverfish;
   }

   public boolean wasDrought() {
      return this.reason == EnumBlockBreakReason.drought;
   }

   public boolean wasSnowedUpon() {
      return this.reason == EnumBlockBreakReason.snowfall;
   }

   public boolean wasWindfall() {
      return this.reason == EnumBlockBreakReason.windfall;
   }

   public EntityLivingBase getHarvester() {
      return this.wasHarvested() ? (EntityLivingBase)this.responsible_entity : null;
   }

   public ItemStack getHarvesterItemStack() {
      return this.getHarvester() == null ? null : this.getHarvester().getHeldItemStack();
   }

   public Item getHarvesterItem() {
      return this.getHarvesterItemStack() == null ? null : this.getHarvesterItemStack().getItem();
   }

   public int getHarvesterFortune() {
      return EnchantmentHelper.getFortuneModifier(this.getHarvester());
   }

   public EntityPlayer getResponsiblePlayer() {
      return this.responsible_entity instanceof EntityPlayer ? (EntityPlayer)this.responsible_entity : null;
   }

   public boolean isResponsiblePlayerInCreativeMode() {
      return this.getResponsiblePlayer() != null && this.getResponsiblePlayer().capabilities.isCreativeMode;
   }

   public float getBlockHardness() {
      return this.block.getBlockHardness(this.metadata);
   }

   public EnumBlockBreakReason getReason() {
      return this.reason;
   }

   public BiomeGenBase getBiome() {
      return this.world.getBiomeGenForCoords(this.x, this.z);
   }

   public BlockBreakInfo setDamage(int damage) {
      this.damage = damage;
      return this;
   }

   public void playSoundEffectAtBlock(String sound, float volume, float pitch) {
      if (this.world.isRemote) {
         Minecraft.setErrorMessage("playSoundEffect: why calling this function on client?");
      }

      this.world.playSoundEffect((double)((float)this.x + 0.5F), (double)((float)this.y + 0.5F), (double)((float)this.z + 0.5F), sound, volume, pitch);
   }

   public int dropBlockAsItself(boolean set_block_to_air) {
      int num_drops = this.block.dropBlockAsItself(this);
      if (set_block_to_air) {
         if (this.world.getBlock(this.x, this.y, this.z) != this.block) {
            Minecraft.setErrorMessage("dropBlockAsItself: Block mismatch");
         }

         this.world.setBlockToAir(this.x, this.y, this.z);
      }

      return num_drops;
   }

   public int dropBlockAsEntityItem(boolean set_block_to_air) {
      int num_drops = this.block.dropBlockAsEntityItem(this);
      if (set_block_to_air) {
         if (this.world.getBlock(this.x, this.y, this.z) != this.block) {
            Minecraft.setErrorMessage("dropBlockAsItself: Block mismatch");
         }

         this.world.setBlockToAir(this.x, this.y, this.z);
      }

      return num_drops;
   }

   public BlockBreakInfo setDropCoords(int x, int y, int z) {
      this.drop_x = x;
      this.drop_y = y;
      this.drop_z = z;
      return this;
   }

   public EntityItem createEntityItem(ItemStack item_stack) {
      Random random = this.world.rand;
      double pos_x = (double)this.x + (double)random.nextFloat() * (this.drop_bounds.maxX - this.drop_bounds.minX) + this.drop_bounds.minX;
      double pos_y = (double)this.y + (double)random.nextFloat() * (this.drop_bounds.maxY - this.drop_bounds.minY) + this.drop_bounds.minY;
      double pos_z = (double)this.z + (double)random.nextFloat() * (this.drop_bounds.maxZ - this.drop_bounds.minZ) + this.drop_bounds.minZ;
      return new EntityItem(this.world, pos_x, pos_y, pos_z, item_stack);
   }

   public Chunk getChunkIfItExists() {
      return this.world.getChunkFromBlockCoordsIfItExists(this.x, this.z);
   }
}
