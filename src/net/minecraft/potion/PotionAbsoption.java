package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;

public class PotionAbsoption extends Potion {
   protected PotionAbsoption(int var1, boolean var2, int var3) {
      super(var1, var2, var3);
   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase var1, BaseAttributeMap var2, int var3) {
      var1.setAbsorptionAmount(var1.getAbsorptionAmount() - (float)(4 * (var3 + 1)));
      super.removeAttributesModifiersFromEntity(var1, var2, var3);
   }

   public void applyAttributesModifiersToEntity(EntityLivingBase var1, BaseAttributeMap var2, int var3) {
      var1.setAbsorptionAmount(var1.getAbsorptionAmount() + (float)(4 * (var3 + 1)));
      super.applyAttributesModifiersToEntity(var1, var2, var3);
   }
}
