package fr.farmvivi.pterodactylautostarter;

import com.mattmalec.pterodactyl4j.UtilizationState;
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

        scheduleServerStatusCheck();
    }

    private void scheduleServerStatusCheck() {
        this.plugin.getProxy().schedule(plugin.getPlugin(), this::checkServerStatus, 0, 15, TimeUnit.SECONDS);
    }

    private synchronized void checkServerStatus() {
        server.ping((result) -> {
            long curMillis = System.currentTimeMillis();
            updateServerStatus(result, curMillis);
            handleQueueAndShutdown(result, curMillis);
        });
    }

    private void updateServerStatus(CommonServerPing result, long curMillis) {
        if (serverPing == null && result != null) {
            if (pterodactylServer.retrieveUtilization().execute().getState() == UtilizationState.STARTING) {
                return;
            }
            status = MinecraftServerStatus.ONLINE;
            logServerStatus("en ligne", NamedTextColor.GREEN);
        } else if (shouldSetOffline(result, curMillis)) {
            status = MinecraftServerStatus.OFFLINE;
            logServerStatus("hors-ligne", NamedTextColor.RED);
            clearQueue();
        }
        serverPing = result;
    }

    private boolean shouldSetOffline(CommonServerPing result, long curMillis) {
        return (serverPing != null && result == null && !status.equals(MinecraftServerStatus.STARTING)) ||
                (status.equals(MinecraftServerStatus.STARTING) && lastBusyTime < curMillis);
    }

    private void logServerStatus(String statusMessage, NamedTextColor color) {
        plugin.getLogger().info(Component.text(server.getName()).color(NamedTextColor.YELLOW)
                .append(Component.text(" " + statusMessage).color(color)));
    }

    private void clearQueue() {
        if (!queue.isEmpty()) {
            Iterator<CommonPlayer> iterator = queue.iterator();
            while (iterator.hasNext()) {
                CommonPlayer player = iterator.next();
                plugin.getLogger().warning("Impossible de téléporter " + player.getUsername() + " sur " + server.getName());
                player.sendMessage(Component.text("Téléportation sur le serveur ").color(NamedTextColor.RED)
                        .append(Component.text(server.getDisplayName()).color(NamedTextColor.YELLOW))
                        .append(Component.text(" impossible !").color(NamedTextColor.RED)));
                iterator.remove();
            }
        }
    }

    private void handleQueueAndShutdown(CommonServerPing result, long curMillis) {
        if (status.equals(MinecraftServerStatus.ONLINE)) {
            handleQueue();
            updateLastBusyTime(result, curMillis);
            if (curMillis - lastBusyTime > 5 * 60 * 1000) {
                stop();
            }
        }
    }

    private void handleQueue() {
        if (!queue.isEmpty()) {
            Iterator<CommonPlayer> iterator = queue.iterator();
            while (iterator.hasNext()) {
                CommonPlayer player = iterator.next();
                if (player.getServer().equals(server)) {
                    iterator.remove();
                    continue;
                }
                teleportPlayer(player);
            }
        }
    }

    private void teleportPlayer(CommonPlayer player) {
        plugin.getLogger().info("Téléportation de " + player.getUsername() + " sur " + server.getName());
        player.sendMessage(Component.text("Téléportation sur le serveur ").color(NamedTextColor.GREEN)
                .append(Component.text(server.getDisplayName()).color(NamedTextColor.YELLOW))
                .append(Component.text("...").color(NamedTextColor.GREEN)));
        player.connectToServer(server);
    }

    private void updateLastBusyTime(CommonServerPing result, long curMillis) {
        if (result != null && result.getOnlinePlayers() > 0 && curMillis > lastBusyTime) {
            lastBusyTime = curMillis;
        }
    }

    public synchronized void start() {
        if (!status.equals(MinecraftServerStatus.OFFLINE)) {
            return;
        }
        this.plugin.getLogger().info("Démarrage de " + server.getName() + "...");
        this.pterodactylServer.start().executeAsync();
        this.lastBusyTime = System.currentTimeMillis() + 15 * 60 * 1000;
        this.status = MinecraftServerStatus.STARTING;
    }

    public synchronized void stop() {
        if (!status.equals(MinecraftServerStatus.ONLINE)) {
            return;
        }
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
