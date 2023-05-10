package fr.farmvivi.pterodactylautostarter;

import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class MinecraftServer {
    private final PterodactylAutoStarter plugin;

    private final ServerInfo serverInfo;
    private final ClientServer clientServer;
    private final LinkedList<ProxiedPlayer> queue = new LinkedList<>();
    private MinecraftServerStatus status = MinecraftServerStatus.OFFLINE;
    private ServerPing serverPing;
    private long lastBusyTime = System.currentTimeMillis();

    public MinecraftServer(PterodactylAutoStarter plugin, ServerInfo serverInfo, ClientServer clientServer) {
        this.plugin = plugin;
        this.serverInfo = serverInfo;
        this.clientServer = clientServer;
        plugin.getProxy().getScheduler().schedule(plugin, () -> serverInfo.ping((result, error) -> {
            long curMillis = System.currentTimeMillis();

            // Refresh status
            if (serverPing == null && result != null) {
                status = MinecraftServerStatus.ONLINE;
                plugin.getLogger().info(ChatColor.YELLOW + serverInfo.getName() + ChatColor.GREEN + " en ligne.");
            } else if (serverPing != null && result == null && !status.equals(MinecraftServerStatus.STARTING) || status.equals(MinecraftServerStatus.STARTING) && lastBusyTime < curMillis) {
                status = MinecraftServerStatus.OFFLINE;
                plugin.getLogger().info(ChatColor.YELLOW + serverInfo.getName() + ChatColor.RED + " hors-ligne.");
                if (!queue.isEmpty()) {
                    Iterator<ProxiedPlayer> iterator = queue.iterator();
                    while (iterator.hasNext()) {
                        ProxiedPlayer player = iterator.next();
                        plugin.getLogger().warning("Impossible de téléporter " + player.getName() + " sur " + serverInfo.getName());
                        player.sendMessage(new TextComponent(ChatColor.RED + "Téléportation sur le serveur " + ChatColor.YELLOW + serverInfo.getMotd() + ChatColor.RED + " impossible !"));
                        iterator.remove();
                    }
                }
            }

            serverPing = result;

            if (status.equals(MinecraftServerStatus.ONLINE)) {
                if (!queue.isEmpty()) {
                    Iterator<ProxiedPlayer> iterator = queue.iterator();
                    while (iterator.hasNext()) {
                        ProxiedPlayer player = iterator.next();
                        if (player.getServer().getInfo().equals(serverInfo)) {
                            iterator.remove();
                            continue;
                        }
                        plugin.getLogger().info("Téléportation de " + player.getName() + " sur " + serverInfo.getName());
                        player.sendMessage(new TextComponent(ChatColor.GREEN + "Téléportation sur le serveur " + ChatColor.YELLOW + serverInfo.getMotd() + ChatColor.GREEN + "..."));
                        player.connect(serverInfo);
                    }
                }

                if (result.getPlayers().getOnline() > 0 && curMillis > lastBusyTime) {
                    lastBusyTime = curMillis;
                } else if (curMillis - lastBusyTime > 5 * 60 * 1000) {
                    stop();
                }
            }
        }), 0, 15, TimeUnit.SECONDS);
    }

    public void start() {
        plugin.getLogger().info("Démarrage de " + serverInfo.getName() + "...");
        clientServer.start().executeAsync();
        lastBusyTime = System.currentTimeMillis() + 15 * 60 * 1000;
        status = MinecraftServerStatus.STARTING;
    }

    public void stop() {
        plugin.getLogger().info("Arrêt de " + serverInfo.getName() + "...");
        clientServer.stop().executeAsync();
        lastBusyTime = System.currentTimeMillis();
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public ClientServer getClientServer() {
        return clientServer;
    }

    public LinkedList<ProxiedPlayer> getQueue() {
        return queue;
    }

    public MinecraftServerStatus getStatus() {
        return status;
    }

    public ServerPing getServerPing() {
        return serverPing;
    }
}
