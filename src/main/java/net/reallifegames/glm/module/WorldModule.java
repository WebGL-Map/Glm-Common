/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Tyler Bucher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.reallifegames.glm.module;

import net.reallifegames.glm.GzipGlmChunk;
import net.reallifegames.glm.api.GlmChunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helps with getting info from server and transforming it into data to be sent to the client.
 *
 * @author Tyler Bucher
 */
public class WorldModule {

    /**
     * The cache of {@link GlmChunk}s. So we do not need to poll the server every time.
     */
    @Nonnull
    protected static final ConcurrentHashMap<String, HashMap<String, GlmChunk>> cache = new ConcurrentHashMap<>();

    /**
     * Attempts to build a {@link GlmChunk} from the following information. Bypasses config checks and directly
     * inserts the chunk into the cache.
     *
     * @param worldId         the id of the world for the chunk.
     * @param x               the x position.
     * @param z               the z position.
     * @param generationTime  the time of creation.
     * @param chunkData       the data for the chunk.
     * @param chunkHeightData the height data for the chunk.
     * @return the newly created {@link GlmChunk}.
     */
    @Nonnull
    public static GlmChunk buildFromParametersUnsafe(@Nonnull final String worldId, int x, int z, long generationTime,
                                                     @Nonnull final String chunkData, @Nonnull final String chunkHeightData) {
        final String chunkId = getChunkCacheId(x, 0, z);
        final GlmChunk glChunk = new GzipGlmChunk(generationTime, chunkData, chunkHeightData);
        cache.get(worldId).put(chunkId, glChunk);
        return glChunk;
    }

    /**
     * Attempts to build a {@link GlmChunk} from the following information.
     *
     * @param worldId              the id of the world for the chunk.
     * @param x                    the x position.
     * @param z                    the z position.
     * @param generationTime       the time of creation.
     * @param chunkData            the data for the chunk.
     * @param chunkHeightData      the height data for the chunk.
     * @param isCacheLimited       is the cache size limited.
     * @param maximumChunksInCache if the cache size is limited what is the size.
     * @return the newly created {@link GlmChunk}.
     */
    @Nonnull
    public static GlmChunk buildFromParameters(@Nonnull final String worldId, int x, int z, long generationTime,
                                               @Nonnull final String chunkData, @Nonnull final String chunkHeightData,
                                               final boolean isCacheLimited, final int maximumChunksInCache) {
        final String chunkId = getChunkCacheId(x, 0, z);
        final GlmChunk glChunk = new GzipGlmChunk(generationTime, chunkData, chunkHeightData);
        if (isCacheLimited) {
            // Check if there is room in the cache
            if (cache.get(worldId).size() < maximumChunksInCache) {
                cache.get(worldId).put(chunkId, glChunk);
            }
        } else {
            cache.get(worldId).put(chunkId, glChunk);
        }
        return glChunk;
    }

    /**
     * Gets a chunk id from the supplied information.
     *
     * @param x the x position of a chunk.
     * @param y the y position of a chunk.
     * @param z the z position of a chunk.
     * @return the id of the chunk position.
     */
    @Nonnull
    public static String getChunkCacheId(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }

    /**
     * Returns the status of a chunk in cache.
     *
     * @param worldUuid the string version of the worlds uuid.
     * @param x         the x position of the chunk.
     * @param y         the y position of the chunk.
     * @param z         the z position of the chunk.
     * @return true if the world and the chunk are in the cache false otherwise.
     */
    public static boolean chunkInCache(@Nonnull final String worldUuid, int x, int y, int z) {
        final HashMap<String, GlmChunk> worldCache = cache.get(worldUuid);
        return worldCache != null && worldCache.containsKey(getChunkCacheId(x, y, z));
    }

    /**
     * Gets the {@link GlmChunk} from the cache.
     *
     * @param worldUuid the string version of the worlds uuid.
     * @param x         the x position of the chunk.
     * @param y         the y position of the chunk.
     * @param z         the z position of the chunk.
     * @return the {@link GlmChunk} from the cache if it is present or null if missing.
     */
    @Nullable
    public static GlmChunk getCacheChunk(@Nonnull final String worldUuid, int x, int y, int z) {
        final HashMap<String, GlmChunk> worldCache = cache.get(worldUuid);
        return worldCache == null ? null : worldCache.get(getChunkCacheId(x, y, z));
    }

    /**
     * Checks to see if the cache is less than the provided value.
     *
     * @param worldId the id of the the world to check.
     * @param max     the maximum size of the cache.
     * @return true if the cache is less than the provided value.
     */
    public static boolean isRoomInCache(@Nonnull final String worldId, int max) {
        cache.computeIfAbsent(worldId, k->new HashMap<>());
        return cache.get(worldId).size() < max;
    }

    /**
     * @param worldId the id of the the world to check.
     * @return the size of the cache for a world.
     */
    public static int getCacheSize(@Nonnull final String worldId) {
        final HashMap<String, GlmChunk> worldCache = cache.get(worldId);
        return worldCache == null ? 0 : worldCache.size();
    }

    /**
     * @return the total entry's for the entire cache.
     */
    public static int getTotalCacheSize() {
        int size = 0;
        for (Map.Entry<String, HashMap<String, GlmChunk>> stringHashMapEntry : cache.entrySet()) {
            size += stringHashMapEntry.getValue().size();
        }
        return size;
    }

    /**
     * Remove a range of chunks from the cache.
     *
     * @param worldId the id of the the world to check.
     * @param x1      the top left x coordinate.
     * @param z1      the top left z coordinate.
     * @param x2      the bottom right x coordinate.
     * @param z2      the bottom right z coordinate.
     */
    public static void purgeCache(@Nonnull final String worldId, int x1, int z1, int x2, int z2) {
        HashMap<String, GlmChunk> worldCache;
        for (int i = x1; i < x2; i++) {
            for (int j = z1; j < z2; j++) {
                worldCache = cache.get(worldId);
                if (worldCache != null) {
                    worldCache.remove(getChunkCacheId(i, 0, j));
                }
            }
        }
    }
}
