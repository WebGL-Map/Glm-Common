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
package net.reallifegames.glm;

import net.reallifegames.glm.api.GlmChunk;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

/**
 * A compressed chunk representation for the client side WebGl map.
 *
 * @author Tyler Bucher
 */
public class GzipGlmChunk implements GlmChunk {

    /**
     * Static indices for a two dimensional plane.
     */
    @Nonnull
    public static final String TWO_DIMENSIONAL_INDICES = GzipGlmChunk.twoDimensionalIndexGenerator();

    /**
     * The id / type of this glm chunk.
     */
    @Nonnull
    public final String id;

    /**
     * The time in milliseconds when this snapshot was taken.
     */
    public final long chunkGenerationTime;

    /**
     * The chunk data which the client will render. This data should be compressed using Gzip.
     */
    @Nonnull
    public final String blockData;

    /**
     * The chunk height data which the client can use for positioning. This data should be compressed using Gzip.
     */
    @Nonnull
    public final String blockHeightData;

    /**
     * The block biome data which the client can use for rendering.
     */
    public final String blockBiomeData;

    /**
     * The block index data which the client can use for positioning.
     */
    @Nonnull
    public final String blockIndices;

    /**
     * Creates a chunk representation for the client side gl map.
     *
     * @param id                  the id / type of this glm chunk.
     * @param chunkGenerationTime the time in milliseconds when this snapshot was taken.
     * @param blockData           the chunk data which the client will render.
     * @param blockHeightData     the height data of the chunk.
     * @param blockBiomeData      the block biome data which the client can use for rendering.
     * @param blockIndices        the block index data which the client can use for positioning.
     */
    public GzipGlmChunk(@Nonnull final String id, final long chunkGenerationTime, @Nonnull final String blockData,
                        @Nonnull final String blockHeightData, @Nonnull final String blockBiomeData,
                        @Nonnull final String blockIndices) {
        this.id = id;
        this.chunkGenerationTime = chunkGenerationTime;
        this.blockData = blockData;
        this.blockHeightData = blockHeightData;
        this.blockBiomeData = blockBiomeData;
        this.blockIndices = blockIndices;
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getChunkGenerationTime() {
        return chunkGenerationTime;
    }

    @Nonnull
    @Override
    public String getBlockData() {
        return blockData;
    }

    @Nonnull
    @Override
    public String getBlockHeightData() {
        return blockHeightData;
    }

    @Nonnull
    @Override
    public String getBlockIndices() {
        return blockIndices;
    }

    @Nonnull
    @Override
    public String getBlockBiomeData() {
        return blockBiomeData;
    }

    /**
     * @return generated indices for a two dimensional plane.
     */
    @Nonnull
    private static String twoDimensionalIndexGenerator() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            builder.append(i).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    /**
     * Compress a string down to save memory. Uses gzip.
     *
     * @param original the string to process.
     * @return the newly compressed string.
     */
    @Nonnull
    public static String compressString(@Nonnull final String original) {
        // Don't compress empty string
        if (original.length() == 0) {
            return original;
        }
        // Attempt to compress string
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream(); final GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(original.getBytes());
            gzip.close();
            // Convert to base64 string
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            return original;
        }
    }

    /**
     * Attempts to stringify and compress height data. Uses gzip.
     *
     * @param heights the heights to stringify and compress.
     * @return the new height encoded string.
     */
    @Nonnull
    public static String compressByteArray(@Nonnull final byte[] heights) {
        // Don't compress empty string
        if (heights.length == 0) {
            return "";
        }
        // Attempt to compress string
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream(); final GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(heights);
            gzip.close();
            // Convert to base64 string
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            return new String(heights);
        }
    }
}
