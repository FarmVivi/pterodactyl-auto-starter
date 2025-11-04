package fr.farmvivi.pterodactylautostarter.bungee;

import fr.farmvivi.pterodactylautostarter.bungee.ping.BungeeFavicon;
import fr.farmvivi.pterodactylautostarter.common.CommonPlayer;
import fr.farmvivi.pterodactylautostarter.common.CommonPlugin;
import fr.farmvivi.pterodactylautostarter.common.CommonProxy;
import fr.farmvivi.pterodactylautostarter.common.CommonServer;
import fr.farmvivi.pterodactylautostarter.common.event.PlayerDisconnectEvent;
import fr.farmvivi.pterodactylautostarter.common.event.ProxyPingEvent;
import fr.farmvivi.pterodactylautostarter.common.event.ServerConnectEvent;
import fr.farmvivi.pterodactylautostarter.common.listener.EventListener;
import fr.farmvivi.pterodactylautostarter.common.ping.CommonFavicon;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BungeeProxy implements CommonProxy, EventListener {
    private final ProxyServer proxyServer;

    private final Map<String, BungeeServer> serversCache = new HashMap<>();
    private final Map<UUID, BungeePlayer> playersCache = new HashMap<>();

    public BungeeProxy(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public String getName() {
        return proxyServer.getName();
    }

    @Override
    public String getVersion() {
        return proxyServer.getVersion();
    }

    @Override
    public CommonServer getServer(String name) {
        // Try to get the server from the cache
        BungeeServer bungeeServer = serversCache.get(name);
        // If the server is not in the cache
        if (bungeeServer == null) {
            // Try to retrieve the server data from BungeeCord
            ServerInfo server = proxyServer.getServerInfo(name);
            // If the server exists
            if (server != null) {
                // Create the server
                bungeeServer = new BungeeServer(server);
                // Cache the server
                serversCache.put(name, bungeeServer);
            }
        }

        // Return the server
        return bungeeServer;
    }

    @Override
    public CommonServer[] getServers() {
        // Retrieve all the servers data from BungeeCord
        Map<String, ServerInfo> servers = proxyServer.getServersCopy();
        // For each server
        for (ServerInfo server : servers.values()) {
            // Retrieve the server name
            String name = server.getName();
            // Cache the server if it is not already cached
            serversCache.computeIfAbsent(name, k -> new BungeeServer(server));
        }

        // Return the servers
        return serversCache.values().toArray(new BungeeServer[0]);
    }

    @Override
    public CommonPlayer getPlayer(UUID uuid) {
        // Try to get the player from the cache
        BungeePlayer bungeePlayer = playersCache.get(uuid);
        // If the player is not in the cache
        if (bungeePlayer == null) {
            // Try to retrieve the player data from BungeeCord
            ProxiedPlayer player = proxyServer.getPlayer(uuid);
            // If the player exists
            if (player != null) {
                // Create the player
                bungeePlayer = new BungeePlayer(this, player);
                // Cache the player
                playersCache.put(uuid, bungeePlayer);
            }
        }

        // Return the player
        return bungeePlayer;
    }

    @Override
    public CommonPlayer getPlayer(String name) {
        // Try to retrieve the player data from BungeeCord
        ProxiedPlayer player = proxyServer.getPlayer(name);
        // If the player exists
        if (player != null) {
            // Retrieve the player UUID
            UUID uuid = player.getUniqueId();

            // Return the player by its UUID
            return getPlayer(uuid);
        }

        // Return null if the player does not exist
        return null;
    }

    public CommonPlayer getPlayer(ProxiedPlayer player) {
        // Retrieve the player UUID
        UUID uuid = player.getUniqueId();
        // Cache the player if it is not already cached
        playersCache.computeIfAbsent(uuid, k -> new BungeePlayer(this, player));

        // Return the player
        return playersCache.get(uuid);
    }

    @Override
    public CommonPlayer[] getPlayers() {
        // Retrieve all the players data from BungeeCord
        for (ProxiedPlayer player : proxyServer.getPlayers()) {
            // Retrieve the player UUID
            UUID uuid = player.getUniqueId();
            // Cache the player if it is not already cached
            playersCache.computeIfAbsent(uuid, k -> new BungeePlayer(this, player));
        }

        // Return the players
        return playersCache.values().toArray(new BungeePlayer[0]);
    }

    @Override
    public void runAsync(CommonPlugin owner, Runnable task) {
        if (owner instanceof BungeePlugin bungeeOwner) {
            // Run the task asynchronously
            proxyServer.getScheduler().runAsync(bungeeOwner, task);
        }
    }

    @Override
    public void schedule(CommonPlugin owner, Runnable task, long delay, TimeUnit unit) {
        if (owner instanceof BungeePlugin bungeeOwner) {
            // Schedule the task
            proxyServer.getScheduler().schedule(bungeeOwner, task, delay, unit);
        }
    }

    @Override
    public void schedule(CommonPlugin owner, Runnable task, long delay, long period, TimeUnit unit) {
        if (owner instanceof BungeePlugin bungeeOwner) {
            // Schedule the task
            proxyServer.getScheduler().schedule(bungeeOwner, task, delay, period, unit);
        }
    }

    @Override
    public void cancel(CommonPlugin owner) {
        if (owner instanceof BungeePlugin bungeeOwner) {
            // Cancel the tasks of the plugin owner
            proxyServer.getScheduler().cancel(bungeeOwner);
        }
    }

    @Override
    public CommonFavicon createFavicon(BufferedImage image) {
        return new BungeeFavicon(Favicon.create(image));
    }

    @Override
    public Map<String, String> getForcedHosts() {
        return proxyServer.getConfig().getListeners().iterator().next().getForcedHosts();
    }

    @Override
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        // Retrieve the player
        CommonPlayer player = event.getPlayer();

        // If player is not null
        if (player != null) {
            // Remove the player from the cache
            playersCache.remove(player.getUniqueId());
        }
    }

    @Override
    public void onProxyPing(ProxyPingEvent event) {
        // Do nothing
    }

    @Override
    public void onServerConnect(ServerConnectEvent event) {
        // Do nothing
    }
}
