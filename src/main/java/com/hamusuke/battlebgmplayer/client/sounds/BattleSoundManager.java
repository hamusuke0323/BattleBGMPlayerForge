package com.hamusuke.battlebgmplayer.client.sounds;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hamusuke.battlebgmplayer.client.sound.BattleSound;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

@SideOnly(Side.CLIENT)
public final class BattleSoundManager {
    private static final String PATH = "battlesounds.json";
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LogManager.getLogger();
    private final File config;
    private ResourceLocation defaultSound = SoundHandler.MISSING_SOUND.getSoundLocation();
    private final Int2ObjectMap<ResourceLocation> byWorld = new Int2ObjectArrayMap<>();
    private final Map<ResourceLocation, ResourceLocation> byBiome = Maps.newHashMap();
    private final Map<ResourceLocation, ResourceLocation> byEntity = Maps.newHashMap();
    private int previousWorld;
    private ResourceLocation previousBiome;
    private ResourceLocation previousEntity;
    private boolean isNotCurrentSoundDefault;

    public BattleSoundManager(Path config) {
        this.config = config.resolve(PATH).toFile();
    }

    private static void forEachIfHasKey(JsonObject jsonObject, String key, BiConsumer<String, String> consumer) {
        if (jsonObject.has(key)) {
            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.getAsJsonObject(key).entrySet();
            ProgressManager.ProgressBar bar = ProgressManager.push(key, entrySet.size(), true);
            entrySet.forEach(entry -> {
                String k = entry.getKey();
                String v = entry.getValue().getAsString();
                bar.step("\"" + k + "\"" + ": " + "\"" + v + "\"");
                consumer.accept(k, v);
            });
            autoPopProgressBar(bar);
        }
    }

    public static void autoPopProgressBar(ProgressManager.ProgressBar bar) {
        for (int i = bar.getStep(); i < bar.getSteps(); i++) {
            bar.step("Skip");
        }
        ProgressManager.pop(bar);
    }

    private void substitute(boolean isNotCurrentSoundDefault, ResourceLocation previousEntity, ResourceLocation previousBiome, int previousWorld) {
        this.isNotCurrentSoundDefault = isNotCurrentSoundDefault;
        this.previousEntity = previousEntity;
        this.previousBiome = previousBiome;
        this.previousWorld = previousWorld;
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

    public BattleSound choose(@Nullable ISound previous, @Nullable BattleSound current, EntityLiving mob, EntityPlayerSP localPlayer) {
        BattleSound chosen = this.choose(previous, current, mob, localPlayer.world.getBiome(localPlayer.getPosition()), localPlayer.world);
        return current != null && current.getSoundLocation().equals(chosen.getSoundLocation()) ? current : chosen;
    }

    private BattleSound choose(@Nullable ISound previous, @Nullable BattleSound current, EntityLiving mob, Biome biome, World level) {
        ResourceLocation entityName = EntityRegistry.getEntry(mob.getClass()).getRegistryName();
        ResourceLocation biomeName = biome.getRegistryName();
        int worldId = level.provider.getDimension();

        ResourceLocation resourceLocation = this.byEntity.get(entityName);
        ResourceLocation resourceLocation1 = this.byBiome.get(biomeName);
        ResourceLocation resourceLocation2 = this.byWorld.get(worldId);

        if (resourceLocation != null) {
            if (current != null && entityName != null && entityName.equals(this.previousEntity)) {
                return current;
            }

            this.substitute(true, entityName, biomeName, worldId);
            return new BattleSound(resourceLocation, previous);
        } else if (resourceLocation1 != null) {
            if (current != null && biomeName != null && biomeName.equals(this.previousBiome)) {
                return current;
            }

            this.substitute(true, entityName, biomeName, worldId);
            return new BattleSound(resourceLocation1, previous);
        } else if (resourceLocation2 != null) {
            if (current != null && worldId == this.previousWorld) {
                return current;
            }

            this.substitute(true, entityName, biomeName, worldId);
            return new BattleSound(resourceLocation2, previous);
        } else {
            if (!this.isNotCurrentSoundDefault && current != null) {
                return current;
            }

            this.substitute(false, entityName, biomeName, worldId);
            return new BattleSound(this.defaultSound, previous);
        }
    }

    public synchronized void load() {
        this.clearAll();

        ProgressManager.ProgressBar bar = ProgressManager.push("Registering conditions", 4, true);
        if (this.config.exists()) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(this.config))) {
                JsonObject jsonObject = GSON.fromJson(inputStreamReader, JsonObject.class);
                if (jsonObject.has("defaultSound")) {
                    String s = jsonObject.get("defaultSound").getAsString();
                    bar.step("default sound: " + s);
                    this.defaultSound = new ResourceLocation(s);
                }
                bar.step("byWorld");
                forEachIfHasKey(jsonObject, "byWorld", (s, s2) -> this.byWorld.put(MathHelper.getInt(s, 0), new ResourceLocation(s2)));
                bar.step("byBiome");
                forEachIfHasKey(jsonObject, "byBiome", (s, s2) -> this.byBiome.put(new ResourceLocation(s), new ResourceLocation(s2)));
                bar.step("byEntity");
                forEachIfHasKey(jsonObject, "byEntity", (s, s2) -> this.byEntity.put(new ResourceLocation(s), new ResourceLocation(s2)));
            } catch (Exception e) {
                LOGGER.warn("Error occurred while loading config from " + PATH, e);
            }
        }
        autoPopProgressBar(bar);
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
