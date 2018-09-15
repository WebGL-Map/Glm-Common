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

import javax.annotation.Nonnull;

/**
 * Holds glm chunk type constants.
 *
 * @author Tyler Bucher.
 */
public enum GlmChunkTypes {

    /**
     * The glm type for a two dimensional chunk representation.
     */
    TWO_DIMENSIONAL_GZIP("two_dimensional_gzip"),

    /**
     * The glm type for a three dimensional chunk representation.
     */
    THREE_DIMENSIONAL_GZIP("three_dimensional_gzip");

    /**
     * The id / type of the glm chunk.
     */
    private final String type;

    /**
     * @param type the id / type of the glm chunk.
     */
    GlmChunkTypes(@Nonnull final String type) {
        this.type = type;
    }

    /**
     * @return the id / type of the glm chunk.
     */
    public String getType() {
        return type;
    }
}
