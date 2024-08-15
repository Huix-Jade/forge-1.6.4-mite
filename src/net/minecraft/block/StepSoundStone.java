package net.minecraft.block;

final class StepSoundStone extends StepSound {
   StepSoundStone(String var1, float var2, float var3) {
      super(var1, var2, var3);
   }

   public String getBreakSound() {
      return "random.glass";
   }

   public String getPlaceSound() {
      return "step.stone";
   }
}
