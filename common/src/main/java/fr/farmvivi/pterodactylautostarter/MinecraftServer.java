package fr.farmvivi.pterodactylautostarter;

import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import fr.farmvivi.pterodactylautostarter.common.CommonPlayer;
import fr.farmvivi.pterodactylautostarter.common.CommonServer;
import fr.farmvivi.pterodactylautostarter.common.ping.CommonServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class MinecraftServer {
    private final PterodactylAutoStarter plugin;

    private final CommonServer server;
    private final ClientServer pterodactylServer;
    private final LinkedList<CommonPlayer> queue = new LinkedList<>();
    private MinecraftServerStatus status = MinecraftServerStatus.OFFLINE;
    private CommonServerPing serverPing;
    private long lastBusyTime = System.currentTimeMillis();

    public MinecraftServer(PterodactylAutoStarter plugin, CommonServer server, ClientServer pterodactylServer) {
        this.plugin = plugin;
        this.server = server;
        this.pterodactylServer = pterodactylServer;

        // Schedule the server status check
        this.plugin.getProxy().schedule(plugin.getPlugin(), () -> server.ping((result) -> {
            long curMillis = System.currentTimeMillis();

            //pterodactylServer.retrieveUtilization().execute().getState()

            // Refresh status
            if (serverPing == null && result != null) {
                status = MinecraftServerStatus.ONLINE;
                plugin.getLogger().info(Component.text(server.getName()).color(NamedTextColor.YELLOW).append(Component.text(" en ligne.").color(NamedTextColor.GREEN)));
            } else if (serverPing != null && result == null && !status.equals(MinecraftServerStatus.STARTING) || status.equals(MinecraftServerStatus.STARTING) && lastBusyTime < curMillis) {
                status = MinecraftServerStatus.OFFLINE;
                plugin.getLogger().info(Component.text(server.getName()).color(NamedTextColor.YELLOW).append(Component.text(" hors-ligne.").color(NamedTextColor.RED)));
                if (!queue.isEmpty()) {
                    Iterator<CommonPlayer> iterator = queue.iterator();
                    while (iterator.hasNext()) {
                        CommonPlayer player = iterator.next();
                        plugin.getLogger().warning("Impossible de téléporter " + player.getUsername() + " sur " + server.getName());
                        player.sendMessage(Component.text("Téléportation sur le serveur ").color(NamedTextColor.RED).append(Component.text(server.getDisplayName()).color(NamedTextColor.YELLOW)).append(Component.text(" impossible !").color(NamedTextColor.RED)));
                        iterator.remove();
                    }
                }
            }

            serverPing = result;

            if (status.equals(MinecraftServerStatus.ONLINE)) {
                if (!queue.isEmpty()) {
                    Iterator<CommonPlayer> iterator = queue.iterator();
                    while (iterator.hasNext()) {
                        CommonPlayer player = iterator.next();
                        if (player.getServer().equals(server)) {
                            iterator.remove();
                            continue;
                        }
                        plugin.getLogger().info("Téléportation de " + player.getUsername() + " sur " + server.getName());
                        player.sendMessage(Component.text("Téléportation sur le serveur ").color(NamedTextColor.GREEN).append(Component.text(server.getDisplayName()).color(NamedTextColor.YELLOW)).append(Component.text("...").color(NamedTextColor.GREEN)));
                        player.connectToServer(server);
                    }
                }

                if (result != null && result.getOnlinePlayers() > 0 && curMillis > lastBusyTime) {
                    lastBusyTime = curMillis;
                } else if (curMillis - lastBusyTime > 5 * 60 * 1000) {
                    stop();
                }
            }
        }), 0, 15, TimeUnit.SECONDS);
    }

    public void start() {
        this.plugin.getLogger().info("Démarrage de " + server.getName() + "...");
        this.pterodactylServer.start().executeAsync();
        this.lastBusyTime = System.currentTimeMillis() + 15 * 60 * 1000;
        this.status = MinecraftServerStatus.STARTING;
    }

    public void stop() {
        this.plugin.getLogger().info("Arrêt de " + server.getName() + "...");
        this.pterodactylServer.stop().executeAsync();
        this.lastBusyTime = System.currentTimeMillis();
    }

    public CommonServer getServer() {
        return server;
    }

    public LinkedList<CommonPlayer> getQueue() {
        return queue;
    }

    public MinecraftServerStatus getStatus() {
        return status;
    }

    public CommonServerPing getServerPing() {
        return serverPing;
    }
}
