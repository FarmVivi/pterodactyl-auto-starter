package fr.farmvivi.pterodactylautostarter.listener;

import fr.farmvivi.pterodactylautostarter.MinecraftServer;
import fr.farmvivi.pterodactylautostarter.PterodactylAutoStarter;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectEventListener implements Listener {
    private final PterodactylAutoStarter plugin;

    public PlayerDisconnectEventListener(PterodactylAutoStarter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        for (MinecraftServer server : plugin.getServers().values()) {
            server.getQueue().removeIf(proxiedPlayer -> proxiedPlayer.equals(event.getPlayer()));
        }
    }
}
