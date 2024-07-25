package fr.farmvivi.pterodactylautostarter.bungee.listener;

import fr.farmvivi.pterodactylautostarter.bungee.BungeeServer;
import fr.farmvivi.pterodactylautostarter.common.CommonPlayer;
import fr.farmvivi.pterodactylautostarter.common.CommonProxy;
import fr.farmvivi.pterodactylautostarter.common.CommonServer;
import fr.farmvivi.pterodactylautostarter.common.event.ServerConnectEvent;
import fr.farmvivi.pterodactylautostarter.common.listener.EventListener;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Collection;

public class BungeeServerConnectEventListener implements Listener {
    private final CommonProxy proxy;
    private final Collection<EventListener> eventListeners;

    public BungeeServerConnectEventListener(CommonProxy proxy, Collection<EventListener> eventListeners) {
        this.proxy = proxy;
        this.eventListeners = eventListeners;
    }

    @EventHandler
    public void onServerConnect(net.md_5.bungee.api.event.ServerConnectEvent event) {
        // Get the player
        CommonPlayer player = proxy.getPlayer(event.getPlayer().getUniqueId());

        // Get the target server
        CommonServer targetServer = proxy.getServer(event.getTarget().getName());

        // Construct the event
        ServerConnectEvent serverConnectEvent = new ServerConnectEvent(player, targetServer);

        // Call the event
        for (EventListener eventListener : eventListeners) {
            eventListener.onServerConnect(serverConnectEvent);
        }

        // Post call
        serverConnectEvent.postCall();

        // Check if the event is cancelled
        if (serverConnectEvent.isCancelled()) {
            // Cancel the connection
            event.setCancelled(true);
        } else {
            // Retrieve target server
            CommonServer target = serverConnectEvent.getTarget();

            // Get the BungeeServer
            BungeeServer bungeeServer = (BungeeServer) target;

            // Set the target server
            event.setTarget(bungeeServer.getServerInfo());
        }
    }
}
