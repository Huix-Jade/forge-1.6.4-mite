package net.minecraft.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.security.MessageDigest;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class CopyFileFromURL extends Thread {
   private final String url_string;
   private final String destination_path;
   private final boolean compare_md5s_first;
   private int connection_timeout_ms;
   private int read_timeout_ms;

   public CopyFileFromURL(String url_string, String destination_path, int connection_timeout_ms, int read_timeout_ms, boolean compare_md5s_first) {
      this.url_string = url_string;
      this.destination_path = destination_path;
      this.compare_md5s_first = compare_md5s_first;
      this.connection_timeout_ms = connection_timeout_ms;
      this.read_timeout_ms = read_timeout_ms;
   }

   public static boolean isFileDataIdentical(File file1, File file2) {
      if (!file1.exists() && !file2.exists()) {
         return true;
      } else if (file1.exists() && file2.exists()) {
         if (file1.length() != file2.length()) {
            return false;
         } else {
            try {
               byte[] bytes1 = Files.readAllBytes(file1.toPath());
               byte[] bytes2 = Files.readAllBytes(file2.toPath());
               if (bytes1.length != bytes2.length) {
                  return false;
               } else {
                  for(int i = 0; i < bytes1.length; ++i) {
                     if (bytes1[i] != bytes2[i]) {
                        return false;
                     }
                  }

                  return true;
               }
            } catch (IOException var5) {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public static boolean overwriteFileIfNotSameAsTemp(File file, File temp_file) {
      if (!temp_file.exists()) {
         Minecraft.setErrorMessage("overwriteFileIfNotSameAsTemp: temp_file does not exist");
         (new Exception()).printStackTrace();
         return false;
      } else {
         if (file.exists()) {
            if (isFileDataIdentical(file, temp_file)) {
               FileUtils.deleteQuietly(temp_file);
               return false;
            }

            FileUtils.deleteQuietly(file);
            if (file.exists()) {
               FileUtils.deleteQuietly(temp_file);
               return false;
            }
         }

         if (temp_file.renameTo(file)) {
            return true;
         } else {
            try {
               FileUtils.moveFile(temp_file, file);
               return true;
            } catch (IOException var3) {
               IOException e = var3;
               e.printStackTrace();
               FileUtils.deleteQuietly(temp_file);
               return false;
            }
         }
      }
   }

   public static String getMD5(File file) {
      if (file != null && file.exists()) {
         try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = IOUtils.toByteArray(fis);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes, 0, bytes.length);
            String md5_string = (new BigInteger(1, md.digest())).toString(16);
            fis.close();
            return md5_string;
         } catch (Exception var5) {
            Exception e = var5;
            e.printStackTrace();
         }
      }

      return null;
   }

   public static boolean areMD5sTheSame(String local_file_path, String remote_file_url) {
      File file = new File(local_file_path);
      if (file.exists()) {
         String md5_string = null;
         if (remote_file_url.startsWith("http://")) {
            md5_string = HttpUtil.performGetRequest(FilenameUtils.getPath(remote_file_url) + "md5.php?f=" + FilenameUtils.getName(remote_file_url), 2000, 2000);
         } else if (remote_file_url.startsWith("file:/")) {
            md5_string = getMD5(new File(remote_file_url.substring("file:/".length())));
         }

         if (md5_string != null && md5_string.equals(getMD5(file))) {
            return true;
         }
      }

      return false;
   }

   public void run() {
      if (this.compare_md5s_first && areMD5sTheSame(this.destination_path, this.url_string)) {
         if (Minecraft.inDevMode()) {
            System.out.println("Skipping download of " + this.url_string + " (identical MD5)");
         }

      } else {
         String temp_path = this.destination_path + ".temp";
         if (copyFileFromURL(this.url_string, temp_path, this.connection_timeout_ms, this.read_timeout_ms)) {
            overwriteFileIfNotSameAsTemp(new File(this.destination_path), new File(temp_path));
         }

      }
   }

   public static boolean copyFileFromURL(String url_string, String destination_path, int connection_timeout_ms, int read_timeout_ms) {
      try {
         URLConnection c = (new URL(url_string)).openConnection();
         c.setConnectTimeout(connection_timeout_ms);
         c.setReadTimeout(read_timeout_ms);
         ReadableByteChannel rbc = Channels.newChannel(c.getInputStream());
         FileOutputStream fos = new FileOutputStream(destination_path);
         fos.getChannel().transferFrom(rbc, 0L, Long.MAX_VALUE);
         fos.close();
         if (Minecraft.inDevMode()) {
            System.out.println("Successfully downloaded " + url_string);
         }

         return true;
      } catch (Exception var7) {
         Exception e = var7;
         if (Minecraft.inDevMode()) {
            System.out.println("copyFileFromURL(" + url_string + "): " + e.toString());
         }

         return false;
      }
   }
}
