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
package net.reallifegames.glm.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.reallifegames.glm.api.server.WsCommandRegistrar;
import net.reallifegames.glm.api.server.WsServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * The web socket server which handles communications with web socket clients.
 *
 * @author Tyler Bucher
 */
public abstract class GlmServer extends WebSocketServer implements WsServer {

    /**
     * Handles all incoming commands.
     */
    @Nonnull
    private final WsCommandRegistrar registrar;

    /**
     * The current number if connected clients.
     */
    protected int currentConnections;

    /**
     * Creates a new Gl server.
     *
     * @param address            the address to bind to.
     * @param wsCommandRegistrar the handler for all incoming commands.
     */
    public GlmServer(@Nonnull final InetSocketAddress address, @Nonnull final WsCommandRegistrar wsCommandRegistrar) {
        super(address);
        registrar = wsCommandRegistrar;
    }

    @Override
    public void onOpen(@Nonnull final WebSocket conn, @Nonnull final ClientHandshake handshake) {
        currentConnections++;
    }

    @Override
    public void onClose(@Nonnull final WebSocket conn, int code, @Nonnull final String reason, boolean remote) {
        currentConnections--;
    }

    @Override
    public void onMessage(@Nonnull final WebSocket conn, @Nonnull final String message) {
        // Attempt to parse json
        JsonNode node = null;
        try {
            node = new ObjectMapper().readTree(message);
        } catch (IOException e) {
            conn.send("{\"error\": \"Invalid data format\"}");
        }
        // Handle json object
        if (node != null) {
            registrar.handleCommand(conn, node);
        } else {
            conn.send("{\"error\": \"Invalid data format\"}");
        }
    }

    @Override
    public int getCurrentConnections() {
        return currentConnections;
    }

    /**
     * @return the handler for all incoming commands.
     */
    @Nonnull
    public WsCommandRegistrar getRegistrar() {
        return registrar;
    }
}
