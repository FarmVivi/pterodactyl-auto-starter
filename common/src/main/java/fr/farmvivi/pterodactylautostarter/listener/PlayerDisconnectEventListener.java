package fr.farmvivi.pterodactylautostarter.listener;

import fr.farmvivi.pterodactylautostarter.MinecraftServer;
import fr.farmvivi.pterodactylautostarter.PterodactylAutoStarter;
import fr.farmvivi.pterodactylautostarter.common.CommonPlayer;
import fr.farmvivi.pterodactylautostarter.common.event.PlayerDisconnectEvent;
import fr.farmvivi.pterodactylautostarter.common.listener.EventAdapter;

public class PlayerDisconnectEventListener extends EventAdapter {
    private final PterodactylAutoStarter plugin;

    public PlayerDisconnectEventListener(PterodactylAutoStarter plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        CommonPlayer player = event.getPlayer();
        if (player == null) {
            return;
        }
        for (MinecraftServer server : plugin.getServers().values()) {
            server.getQueue().removeIf(proxiedPlayer -> proxiedPlayer.equals(player));
        }
    }
}
