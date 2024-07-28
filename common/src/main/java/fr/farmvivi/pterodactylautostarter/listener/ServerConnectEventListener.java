package fr.farmvivi.pterodactylautostarter.listener;

import fr.farmvivi.pterodactylautostarter.MinecraftServer;
import fr.farmvivi.pterodactylautostarter.MinecraftServerStatus;
import fr.farmvivi.pterodactylautostarter.PterodactylAutoStarter;
import fr.farmvivi.pterodactylautostarter.common.CommonPlayer;
import fr.farmvivi.pterodactylautostarter.common.CommonServer;
import fr.farmvivi.pterodactylautostarter.common.event.ServerConnectEvent;
import fr.farmvivi.pterodactylautostarter.common.listener.EventAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ServerConnectEventListener extends EventAdapter {
    private final PterodactylAutoStarter plugin;
    private final CommonServer limboServer;

    public ServerConnectEventListener(PterodactylAutoStarter plugin) {
        this.plugin = plugin;
        this.limboServer = plugin.getProxy().getServer(plugin.getConfig().getString("queue.server"));
    }

    @Override
    public void onServerConnect(ServerConnectEvent event) {
        CommonServer target = event.getTarget();
        CommonPlayer player = event.getPlayer();

        if (player == null) {
            return;
        }

        if (plugin.getServers().containsKey(target)) {
            MinecraftServer server = plugin.getServers().get(target);
            MinecraftServerStatus serverStatus = server.getStatus();
            if (serverStatus.equals(MinecraftServerStatus.OFFLINE) || serverStatus.equals(MinecraftServerStatus.STARTING)) {
                if (player.getServer() == null) {
                    event.setTarget(limboServer);
                } else {
                    event.setCancelled(true);
                }
                server.getQueue().add(player);
                if (serverStatus.equals(MinecraftServerStatus.OFFLINE)) {
                    server.start();
                }

                player.sendMessage(Component.text(server.getServer().getDisplayName()).color(NamedTextColor.YELLOW)
                        .append(Component.text(" en cours de démarrage...").color(NamedTextColor.GOLD))
                        .appendNewline()
                        .append(Component.text("Vous avez rejoint la file d'attente pour rejoindre ce serveur une fois démarré.").color(NamedTextColor.GRAY))
                );
            }
        }
    }
}
