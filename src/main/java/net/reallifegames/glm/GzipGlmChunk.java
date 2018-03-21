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
     * The time in milliseconds when this snapshot was taken.
     */
    public final long chunkGenerationTime;

    /**
     * The chunk data which the client will render. This data should be compressed using Gzip.
     */
    @Nonnull
    public final String chunkData;

    /**
     * The chunk height data which the client can use for positioning. This data should be compressed using Gzip.
     */
    @Nonnull
    public final String chunkHeightData;

    /**
     * Creates a chunk representation for the client side gl map.
     *
     * @param chunkGenerationTime the time in milliseconds when this snapshot was taken.
     * @param chunkData           the chunk data which the client will render.
     * @param chunkHeightData     the height data of the chunk.
     */
    public GzipGlmChunk(final long chunkGenerationTime, @Nonnull final String chunkData, @Nonnull final String chunkHeightData) {
        this.chunkGenerationTime = chunkGenerationTime;
        this.chunkData = chunkData;
        this.chunkHeightData = chunkHeightData;
    }

    @Override
    public long getChunkGenerationTime() {
        return chunkGenerationTime;
    }

    @Nonnull
    @Override
    public String getChunkData() {
        return chunkData;
    }

    @Nonnull
    @Override
    public String getChunkHeightData() {
        return chunkHeightData;
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
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); GZIPOutputStream gzip = new GZIPOutputStream(out)) {
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
    public static String compressHeightData(@Nonnull final byte[] heights) {
        // Don't compress empty string
        if (heights.length == 0) {
            return "";
        }
        // Attempt to compress string
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(heights);
            gzip.close();
            // Convert to base64 string
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            return new String(heights);
        }
    }
}
