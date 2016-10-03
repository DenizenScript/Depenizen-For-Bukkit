package com.morphanone.depenizenbungee;

import com.morphanone.depenizenbungee.packets.ServerPacketOutEvent;
import com.morphanone.depenizenbungee.sockets.ClientConnection;
import com.morphanone.depenizenbungee.sockets.SocketServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager implements Listener {

    private static long nextEventId;
    private static final Map<Long, Map<String, String>> eventDeterminations = new HashMap<Long, Map<String, String>>();
    private static final Map<String, List<ClientConnection>> eventSubscriptions = new HashMap<String, List<ClientConnection>>();

    public EventManager() {
        ProxyServer.getInstance().getPluginManager().registerListener(Depenizen.getCurrentInstance(), this);
    }

    public static void subscribe(String event, ClientConnection client) {
        if (!eventSubscriptions.containsKey(event))
            eventSubscriptions.put(event, new ArrayList<ClientConnection>());
        eventSubscriptions.get(event).add(client);
    }

    public static void unsubscribe(String event, ClientConnection client) {
        if (eventSubscriptions.containsKey(event)) {
            eventSubscriptions.get(event).remove(client);
        }
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        if (!isSubscribedTo("ProxyPing")) {
            return;
        }
        Map<String, String> context = new HashMap<String, String>();
        PendingConnection connection = event.getConnection();
        context.put("address", connection.getAddress().toString());
        context.put("version", String.valueOf(connection.getVersion()));
        ServerPing ping = event.getResponse();
        ServerPing.Players players = ping.getPlayers();
        context.put("num_players", String.valueOf(players.getOnline()));
        context.put("max_players", String.valueOf(players.getMax()));
        context.put("motd", ping.getDescription());
        Map<String, String> determinations = sendEventPacket(true, "ProxyPing", context);
        if (determinations != null) {
            players.setOnline(Integer.valueOf(determinations.get("num_players")));
            players.setMax(Integer.valueOf(determinations.get("max_players")));
            ping.setDescription(determinations.get("motd"));
            ping.getVersion().setName(determinations.get("version"));
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        if (!isSubscribedTo("PostLogin")) {
            return;
        }
        Map<String, String> context = new HashMap<String, String>();
        ProxiedPlayer player = event.getPlayer();
        context.put("uuid", player.getUniqueId().toString());
        context.put("name", player.getName());
        sendEventPacket(false, "PostLogin", context);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        if (!isSubscribedTo("PlayerDisconnect")) {
            return;
        }
        Map<String, String> context = new HashMap<String, String>();
        ProxiedPlayer player = event.getPlayer();
        context.put("uuid", player.getUniqueId().toString());
        context.put("name", player.getName());
        sendEventPacket(false, "PlayerDisconnect", context);
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        if (!isSubscribedTo("ServerSwitch")) {
            return;
        }
        Map<String, String> context = new HashMap<String, String>();
        ProxiedPlayer player = event.getPlayer();
        context.put("uuid", player.getUniqueId().toString());
        context.put("name", player.getName());
        context.put("server", player.getServer().getInfo().getName());
        sendEventPacket(false, "ServerSwitch", context);
    }

    private static boolean isSubscribedTo(String name) {
        return eventSubscriptions.containsKey(name) && !eventSubscriptions.get(name).isEmpty();
    }

    private static Map<String, String> sendEventPacket(boolean getResponse, String name, Map<String, String> context) {
        long id = nextEventId;
        nextEventId++;
        ServerPacketOutEvent packet = new ServerPacketOutEvent(getResponse, id, name, context);
        SocketServer socketServer = Depenizen.getCurrentInstance().getSocketServer();
        if (socketServer != null) {
            for (ClientConnection client : eventSubscriptions.get(name)) {
                client.send(packet);
            }
            if (getResponse) {
                waitForResponse(id);
                return eventDeterminations.get(id);
            }
        }
        return null;
    }

    private static void waitForResponse(long id) {
        try {
            synchronized (eventDeterminations) {
                while (!eventDeterminations.containsKey(id)) {
                    eventDeterminations.wait();
                }
            }
        } catch (Exception e) {
            dB.echoError(e);
        }
    }

    public static void respond(long id, Map<String, String> determinations) {
        synchronized (eventDeterminations) {
            eventDeterminations.put(id, determinations);
            eventDeterminations.notify();
        }
    }
}
