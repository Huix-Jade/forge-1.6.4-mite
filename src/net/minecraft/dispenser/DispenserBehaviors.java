package net.minecraft.dispenser;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;

public class DispenserBehaviors {
   public static void registerDispenserBehaviours() {
      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item != null) {
            IBehaviorDispenseItem dispenser_behavior = item.getDispenserBehavior();
            if (dispenser_behavior != null) {
               BlockDispenser.dispenseBehaviorRegistry.putObject(item, dispenser_behavior);
            }
         }
      }

      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.egg, new DispenserBehaviorEgg());
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.snowball, new DispenserBehaviorSnowball());
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.expBottle, new DispenserBehaviorExperience());
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.potion, new DispenserBehaviorPotion());
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.monsterPlacer, new DispenserBehaviorMobEgg());
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.firework, new DispenserBehaviorFireworks());
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.fireballCharge, new DispenserBehaviorFireball());
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.boat, new DispenserBehaviorBoat());
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.flintAndSteel, new DispenserBehaviorFire());
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.dyePowder, new DispenserBehaviorDye());
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.itemsList[Block.tnt.blockID], new DispenserBehaviorTNT());
   }
}
