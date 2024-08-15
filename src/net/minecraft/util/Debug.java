package net.minecraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

public class Debug {
   public static boolean is_active;
   public static String general_info;
   public static String general_info_client;
   public static String general_info_server;
   public static int general_counter;
   public static String biome_info;
   public static String selected_object_info;
   public static String equipped_item_info;
   public static boolean flag;
   private static long t;
   public static boolean timer_enabled = true;
   public static Object object;

   public static void println(String text) {
      if (Minecraft.inDevMode()) {
         System.out.println(text);
      }

   }

   public static void setErrorMessage(String text) {
      setErrorMessage(text, false);
   }

   public static void setErrorMessage(String text, boolean print_stack_trace) {
      if (Minecraft.inDevMode()) {
         Minecraft.setErrorMessage(text);
         if (print_stack_trace) {
            printStackTrace();
         }
      }

   }

   public static void printStackTrace() {
      if (Minecraft.inDevMode()) {
         (new Exception()).printStackTrace();
      }

   }

   public static void startTiming() {
      if (timer_enabled) {
         t = System.nanoTime();
      }

   }

   public static long stopTiming() {
      if (timer_enabled) {
         long elapsed_microseconds = (System.nanoTime() - t) / 1000L;
         System.out.println(elapsed_microseconds);
         return elapsed_microseconds;
      } else {
         return 0L;
      }
   }

   public static long stopTiming(String text) {
      if (timer_enabled) {
         long elapsed_microseconds = (System.nanoTime() - t) / 1000L;
         System.out.println(elapsed_microseconds + ": " + text);
         return elapsed_microseconds;
      } else {
         return 0L;
      }
   }

   public static long stopTiming(String text, int minimum_delay_microseconds) {
      if (timer_enabled) {
         long elapsed_microseconds = (System.nanoTime() - t) / 1000L;
         if (elapsed_microseconds >= (long)minimum_delay_microseconds) {
            System.out.println(elapsed_microseconds + ": " + text);
         }

         return elapsed_microseconds;
      } else {
         return 0L;
      }
   }

   public static int getBlockInQuestionX() {
      return -32;
   }

   public static int getBlockInQuestionY() {
      return 67;
   }

   public static int getBlockInQuestionZ() {
      return -936;
   }

   public static boolean isBlockInQuestion(int x, int y, int z) {
      return x == getBlockInQuestionX() && y == getBlockInQuestionY() && z == getBlockInQuestionZ();
   }

   public static boolean isBlockInQuestion(Chunk chunk, int local_x, int y, int local_z) {
      return isBlockInQuestion(chunk.xPosition * 16 + local_x, y, chunk.zPosition * 16 + local_z);
   }

   public static boolean isChunkInQuestion(Chunk chunk) {
      return chunk.xPosition == getBlockInQuestionX() >> 4 && chunk.zPosition == getBlockInQuestionZ() >> 4;
   }
}
