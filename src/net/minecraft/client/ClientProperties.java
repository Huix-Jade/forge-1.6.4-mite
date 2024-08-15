package net.minecraft.client;

import java.io.File;
import net.minecraft.client.renderer.RenderingScheme;
import net.minecraft.logging.ILogAgent;
import net.minecraft.server.dedicated.PropertyManager;

public class ClientProperties {
   private static ClientProperties instance;
   private int rendering_scheme;
   private boolean opportunity_chunk_rendering;
   private int forced_chunk_rendering_distance;
   private String public_servers_update_url;
   private static final int DEFAULT_RENDERING_SCHEME = 1;
   private static final boolean DEFAULT_OPPORTUNITY_CHUNK_RENDERING = true;
   private static final int DEFAULT_FORCED_CHUNK_RENDERING_DISTANCE = 1;
   private static final String DEFAULT_PUBLIC_SERVERS_UPDATE_URL = "http://minecraft-is-too-easy.com/public_servers/public_servers.txt";

   public ClientProperties(String filepath, ILogAgent log_agent) {
      this.readPropertiesFromFile(filepath, log_agent);
      if (instance != null) {
         Minecraft.setErrorMessage("ClientProperties: instance already exists");
         (new Exception()).printStackTrace();
      }

      instance = this;
   }

   private void readPropertiesFromFile(String filepath, ILogAgent log_agent) {
      File file = new File(filepath);
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (Exception var5) {
            log_agent.logSevere("Unable to create " + filepath + " file!");
         }
      }

      log_agent.logInfo("Loading " + filepath);
      PropertyManager settings = new PropertyManager(file, log_agent, "Minecraft client properties");
      RenderingScheme.setCurrent(this.rendering_scheme = settings.getIntProperty("rendering-scheme", 1));
      this.opportunity_chunk_rendering = false;
      this.forced_chunk_rendering_distance = 1;
      this.public_servers_update_url = settings.getProperty("public-servers-update-url", "http://minecraft-is-too-easy.com/public_servers/public_servers.txt");
      settings.saveProperties();
   }

   public static boolean isOpportunityChunkRenderingEnabled() {
      return instance.opportunity_chunk_rendering;
   }

   public static int getForcedChunkRenderingDistance() {
      return instance.forced_chunk_rendering_distance;
   }

   public static String getPublicServersUpdateURL() {
      return instance.public_servers_update_url;
   }
}
