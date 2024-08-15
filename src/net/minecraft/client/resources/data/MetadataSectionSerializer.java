package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializer;

public interface MetadataSectionSerializer extends JsonDeserializer {
   String getSectionName();
}
