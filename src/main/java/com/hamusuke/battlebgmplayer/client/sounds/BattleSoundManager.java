package com.hamusuke.battlebgmplayer.client.sounds;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hamusuke.battlebgmplayer.client.sound.BattleSound;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;

@SideOnly(Side.CLIENT)
public final class BattleSoundManager {
    private static final String PATH = "battlesounds.json";
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LogManager.getLogger();
    private final File config;
    private ResourceLocation defaultSound = SoundHandler.MISSING_SOUND.getSoundLocation();
    private final Int2ObjectMap<ResourceLocation> byWorld = Int2ObjectMaps.emptyMap();
    private final Map<ResourceLocation, ResourceLocation> byBiome = Maps.newHashMap();
    private final Map<ResourceLocation, ResourceLocation> byEntity = Maps.newHashMap();
    private int previousWorld;
    private ResourceLocation previousBiome;
    private ResourceLocation previousEntity;
    private boolean isNotCurrentSoundDefault;

    public BattleSoundManager(Path config) {
        this.config = config.resolve(PATH).toFile();
    }

    public BattleSound choose(@Nullable BattleSound current, EntityLiving mob, EntityPlayerSP localPlayer) {
        BattleSound chosen = this.choose(current, mob, localPlayer.world.getBiome(localPlayer.getPosition()), localPlayer.world);
        return current != null && current.getSoundLocation().equals(chosen.getSoundLocation()) ? current : chosen;
    }

    private BattleSound choose(@Nullable BattleSound current, EntityLiving mob, Biome biome, World level) {
        ResourceLocation entityName = EntityRegistry.getEntry(mob.getClass()).getRegistryName();
        ResourceLocation biomeName = biome.getRegistryName();
        int worldId = level.provider.getDimension();

        ResourceLocation resourceLocation = this.byEntity.get(entityName);
        ResourceLocation resourceLocation1 = this.byBiome.get(biomeName);
        ResourceLocation resourceLocation2 = this.byWorld.get(worldId);

        if (resourceLocation != null) {
            this.isNotCurrentSoundDefault = true;
            if (current != null && entityName.equals(this.previousEntity)) {
                return current;
            }

            this.previousEntity = entityName;
            this.previousBiome = biomeName;
            this.previousWorld = worldId;
            return new BattleSound(resourceLocation);
        } else if (resourceLocation1 != null) {
            this.isNotCurrentSoundDefault = true;
            if (current != null && biomeName.equals(this.previousBiome)) {
                return current;
            }

            this.previousEntity = entityName;
            this.previousBiome = biomeName;
            this.previousWorld = worldId;
            return new BattleSound(resourceLocation1);
        } else if (resourceLocation2 != null) {
            this.isNotCurrentSoundDefault = true;
            if (current != null && worldId == this.previousWorld) {
                return current;
            }

            this.previousEntity = entityName;
            this.previousBiome = biomeName;
            this.previousWorld = worldId;
            return new BattleSound(resourceLocation2);
        } else {
            this.previousEntity = entityName;
            this.previousBiome = biomeName;
            this.previousWorld = worldId;

            if (!this.isNotCurrentSoundDefault && current != null) {
                return current;
            }

            this.isNotCurrentSoundDefault = false;
            return new BattleSound(this.defaultSound);
        }
    }

    private void clearAll() {
        this.defaultSound = SoundHandler.MISSING_SOUND.getSoundLocation();
        this.byWorld.clear();
        this.byBiome.clear();
        this.byEntity.clear();
        this.previousWorld = 0;
        this.previousBiome = null;
        this.previousEntity = null;
        this.isNotCurrentSoundDefault = false;
    }

    public synchronized void load() {
        this.clearAll();

        if (this.config.exists()) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(this.config))) {
                JsonObject jsonObject = GSON.fromJson(inputStreamReader, JsonObject.class);
                if (jsonObject.has("defaultSound")) {
                    this.defaultSound = new ResourceLocation(jsonObject.get("defaultSound").getAsString());
                }
                if (jsonObject.has("byWorld")) {
                    jsonObject.getAsJsonObject("byWorld").entrySet().forEach(entry -> this.byWorld.put(Integer.parseInt(entry.getKey()), new ResourceLocation(entry.getValue().getAsString())));
                }
                forEachIfHasKey(jsonObject, "byBiome", this.byBiome::put);
                forEachIfHasKey(jsonObject, "byEntity", this.byEntity::put);
            } catch (Exception e) {
                LOGGER.warn("Error occurred while loading config from " + PATH, e);
            }
        }
    }

    private static void forEachIfHasKey(JsonObject jsonObject, String key, BiConsumer<ResourceLocation, ResourceLocation> consumer) {
        if (jsonObject.has(key)) {
            jsonObject.getAsJsonObject(key).entrySet().forEach(entry -> consumer.accept(new ResourceLocation(entry.getKey()), new ResourceLocation(entry.getValue().getAsString())));
        }
    }

    public synchronized void save() {
        try (JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(new FileOutputStream(this.config)))) {
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.name("defaultSound").value(this.defaultSound.toString());

            jsonWriter.name("byWorld").beginObject();
            for (Map.Entry<Integer, ResourceLocation> entry : this.byWorld.entrySet()) {
                jsonWriter.name(entry.getKey().toString()).value(entry.getValue().toString());
            }
            jsonWriter.endObject();

            jsonWriter.name("byBiome").beginObject();
            for (Map.Entry<ResourceLocation, ResourceLocation> entry : this.byBiome.entrySet()) {
                jsonWriter.name(entry.getKey().toString()).value(entry.getValue().toString());
            }
            jsonWriter.endObject();

            jsonWriter.name("byEntity").beginObject();
            for (Map.Entry<ResourceLocation, ResourceLocation> entry : this.byEntity.entrySet()) {
                jsonWriter.name(entry.getKey().toString()).value(entry.getValue().toString());
            }
            jsonWriter.endObject();

            jsonWriter.endObject();
            jsonWriter.flush();
        } catch (Exception e) {
            LOGGER.warn("Error occurred while saving config to " + PATH, e);
        }
    }

    @Override
    public String toString() {
        return "BattleSoundManager{" +
                "defaultSound=" + this.defaultSound +
                ", byWorld=" + this.byWorld +
                ", byBiome=" + this.byBiome +
                ", byEntity=" + this.byEntity +
                '}';
    }
}
