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
import net.reallifegames.glm.api.server.WsCommandRegistrar;
import net.reallifegames.glm.api.server.WsServerCommand;
import org.java_websocket.WebSocket;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Maintains control over all web socket server commands.
 *
 * @author Tyler Bucher
 */
public abstract class CommandRegistrar implements WsCommandRegistrar {

    /**
     * The map of commands to command handler objects.
     */
    @Nonnull
    private final ConcurrentMap<String, WsServerCommand> commandMap;

    /**
     * The map of commands to command handler objects.
     */
    @Nonnull
    private final ConcurrentMap<InetSocketAddress, Map<String, Long>> callMap;

    /**
     * Creates a new gl server command registrar.
     */
    CommandRegistrar() {
        this.commandMap = new ConcurrentHashMap<>();
        this.callMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean registerCommand(@Nonnull final String commandName, @Nonnull final WsServerCommand commandHandler) {
        if (commandMap.containsKey(commandName)) {
            return false;
        }
        commandMap.put(commandName, commandHandler);
        return true;
    }

    @Override
    public void handleCommand(@Nonnull final WebSocket connection, @Nonnull final JsonNode commandNode) {
        // Create command var
        String command;
        // Check to see if command lies at 'cmd' or 'command'
        // If at neither then send error to client
        if (commandNode.get("cmd") == null) {
            if (commandNode.get("command") == null) {
                connection.send("{\"error\": \"No command node found\"}");
                return;
            } else {
                command = commandNode.get("command").asText();
            }
        } else {
            command = commandNode.get("cmd").asText();
        }
        // Check if handler exists
        if (!commandMap.containsKey(command)) {
            connection.send("{\"error\": \"Unknown command\"}");
            return;
        }
        // Check if command mapping exists
        callMap.computeIfAbsent(connection.getRemoteSocketAddress(), k->new HashMap<>());
        // Avoid double map lookup.
        final WsServerCommand wsServerCommand = commandMap.get(command);
        // punish client for not respecting settings
        if (wsServerCommand.getInterval() > 0) {
            // Setup vars for checking.
            final long currentTime = System.currentTimeMillis();
            final long pastTime = callMap.get(connection.getRemoteSocketAddress()).get(command);
            if (currentTime - pastTime < wsServerCommand.getInterval()) {
                punishClient(connection, commandNode);
            }
        }
        // Update client call times
        callMap.get(connection.getRemoteSocketAddress()).put(command, System.currentTimeMillis());
        // Get command handler
        wsServerCommand.handle(connection, commandNode);
    }

    /**
     * Push a client for calling a command to often.
     *
     * @param connection  the {@link WebSocket} instance this event is occurring on.
     * @param commandNode the {@link JsonNode} for the command and parameters.
     */
    protected abstract void punishClient(@Nonnull final WebSocket connection, @Nonnull final JsonNode commandNode);
}
