package net.minecraft.crash;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Callable;

class CallableSuspiciousClasses implements Callable {
   // $FF: synthetic field
   final CrashReport theCrashReport;

   CallableSuspiciousClasses(CrashReport var1) {
      this.theCrashReport = var1;
   }

   public String callSuspiciousClasses() throws SecurityException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
      StringBuilder var1 = new StringBuilder();
      Field var2 = ClassLoader.class.getDeclaredField("classes");
      var2.setAccessible(true);
      ArrayList var3 = new ArrayList((Vector)var2.get(CrashReport.class.getClassLoader()));
      boolean var4 = true;
      boolean var5 = !CrashReport.class.getCanonicalName().equals("net.minecraft.CrashReport");
      HashMap var6 = new HashMap();
      String var7 = "";
      Collections.sort(var3, new ComparatorClassSorter(this));
      Iterator var8 = var3.iterator();

      while(true) {
         while(true) {
            Class var9;
            while(true) {
               String var10;
               do {
                  do {
                     do {
                        do {
                           do {
                              do {
                                 do {
                                    do {
                                       do {
                                          do {
                                             do {
                                                do {
                                                   if (!var8.hasNext()) {
                                                      if (var4) {
                                                         var1.append("No suspicious classes found.");
                                                      } else {
                                                         var1.append("]");
                                                      }

                                                      return var1.toString();
                                                   }

                                                   var9 = (Class)var8.next();
                                                } while(var9 == null);

                                                var10 = var9.getCanonicalName();
                                             } while(var10 == null);
                                          } while(var10.startsWith("org.lwjgl."));
                                       } while(var10.startsWith("paulscode."));
                                    } while(var10.startsWith("org.bouncycastle."));
                                 } while(var10.startsWith("argo."));
                              } while(var10.startsWith("com.jcraft."));
                           } while(var10.startsWith("com.fasterxml."));
                        } while(var10.startsWith("com.google."));
                     } while(var10.startsWith("joptsimple."));
                  } while(var10.startsWith("org.apache."));
               } while(var10.equals("util.GLX"));

               if (var5) {
                  if (var10.length() > 3 && !var10.equals("net.minecraft.client.main.Main") && !var10.equals("net.minecraft.client.Minecraft") && !var10.equals("net.minecraft.client.ClientBrandRetriever") && !var10.equals("net.minecraft.server.MinecraftServer")) {
                     break;
                  }
               } else {
                  if (var10.startsWith("net.minecraft")) {
                     continue;
                  }
                  break;
               }
            }

            Package var11 = var9.getPackage();
            String var12 = var11 == null ? "" : var11.getName();
            if (var6.containsKey(var12)) {
               int var13 = (Integer)var6.get(var12);
               var6.put(var12, var13 + 1);
               if (var13 == 3) {
                  if (!var4) {
                     var1.append(", ");
                  }

                  var1.append("...");
                  var4 = false;
                  continue;
               }

               if (var13 > 3) {
                  continue;
               }
            } else {
               var6.put(var12, 1);
            }

            if (!var7.equals(var12) && var7.length() > 0) {
               var1.append("], ");
            }

            if (!var4 && var7.equals(var12)) {
               var1.append(", ");
            }

            if (!var7.equals(var12)) {
               var1.append("[");
               var1.append(var12);
               var1.append(".");
            }

            var1.append(var9.getSimpleName());
            var7 = var12;
            var4 = false;
         }
      }
   }

   // $FF: synthetic method
   public Object call() {
      return "FML and Forge are installed";
   }
}
