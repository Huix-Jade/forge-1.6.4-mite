package net.minecraft.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import net.minecraft.client.ClientProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.util.CopyFileFromURL;
import net.minecraft.util.KeyedValuesString;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

class GetPublicServers extends Thread {
   public void run() {
      File dir = new File("MITE/public_servers");
      if (!dir.exists()) {
         dir.mkdir();
      }

      String url_string = ClientProperties.getPublicServersUpdateURL();
      if (url_string != null && !url_string.isEmpty()) {
         String filepath = "MITE/public_servers/public_servers.txt";
         String temp_filepath = filepath + ".temp";
         if (!CopyFileFromURL.areMD5sTheSame(filepath, url_string)) {
            CopyFileFromURL.copyFileFromURL(url_string, temp_filepath, 2000, 2000);
         } else if (Minecraft.inDevMode()) {
            System.out.println("Remote copy of public_servers.txt has same MD5 as local copy, skipping download");
         }

         File temp_file = new File(temp_filepath);
         File file = new File(filepath);
         String image_url;
         if (temp_file.exists()) {
            boolean valid_format = true;

            try {
               BufferedReader br = new BufferedReader(new FileReader(new File(temp_filepath)));

               while((image_url = br.readLine()) != null) {
                  image_url = image_url.trim();
                  if (!image_url.isEmpty() && !image_url.startsWith("#") && !image_url.startsWith("server_address")) {
                     valid_format = false;
                     break;
                  }
               }

               br.close();
            } catch (Exception var13) {
               valid_format = false;
            }

            if (valid_format) {
               CopyFileFromURL.overwriteFileIfNotSameAsTemp(file, temp_file);
            } else {
               FileUtils.deleteQuietly(temp_file);
               if (Minecraft.inDevMode()) {
                  System.out.println("Downloaded copy of public_servers.txt has unexpected format, skipping update");
               }
            }
         }

         if (file.exists()) {
            try {
               BufferedReader br = new BufferedReader(new FileReader(new File(filepath)));

               String line;
               while((line = br.readLine()) != null) {
                  line = line.trim();
                  if (!line.isEmpty() && !line.startsWith("#")) {
                     image_url = (new KeyedValuesString(line)).getValue("image_url", true);
                     if (image_url != null) {
                        String filename = FilenameUtils.getName(image_url);
                        CopyFileFromURL thread = new CopyFileFromURL(image_url, "MITE/public_servers/" + filename, 2000, 2000, true);
                        thread.setDaemon(true);
                        thread.setName("CopyFileFromURL: " + filename);
                        thread.start();
                     }
                  }
               }

               br.close();
            } catch (Exception var12) {
            }
         }

      } else {
         if (Minecraft.inDevMode()) {
            System.out.println("Skipping update of public_servers.txt");
         }

      }
   }
}
