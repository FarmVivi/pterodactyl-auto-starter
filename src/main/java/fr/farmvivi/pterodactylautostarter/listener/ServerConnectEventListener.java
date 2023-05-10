package fr.farmvivi.pterodactylautostarter.listener;

import fr.farmvivi.pterodactylautostarter.MinecraftServer;
import fr.farmvivi.pterodactylautostarter.MinecraftServerStatus;
import fr.farmvivi.pterodactylautostarter.PterodactylAutoStarter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerConnectEventListener implements Listener {
    private final PterodactylAutoStarter plugin;
    private final ServerInfo limboServer;

    public ServerConnectEventListener(PterodactylAutoStarter plugin) {
        this.plugin = plugin;
        this.limboServer = plugin.getProxy().getServerInfo(plugin.getConfig().getString("queue.server"));
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ServerInfo serverInfo = event.getTarget();
        if (plugin.getServers().containsKey(serverInfo)) {
            ProxiedPlayer player = event.getPlayer();
            MinecraftServer server = plugin.getServers().get(serverInfo);
            MinecraftServerStatus serverStatus = server.getStatus();
            if (serverStatus.equals(MinecraftServerStatus.OFFLINE) || serverStatus.equals(MinecraftServerStatus.STARTING)) {
                if (event.getPlayer().getServer() == null) {
                    event.setTarget(limboServer);
                } else {
                    event.setCancelled(true);
                }
                server.getQueue().add(player);
                if (serverStatus.equals(MinecraftServerStatus.OFFLINE)) {
                    server.start();
                }
                player.sendMessage(new TextComponent(ChatColor.YELLOW + server.getServerInfo().getMotd() + ChatColor.GOLD + " en cours de démarrage...\n" + ChatColor.GRAY + "Vous avez rejoint la file d'attente pour rejoindre ce serveur une fois démarré."));
            }
        }
    }
}
