package fr.farmvivi.pterodactylautostarter.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import fr.farmvivi.pterodactylautostarter.common.CommonPlayer;
import fr.farmvivi.pterodactylautostarter.common.CommonServer;
import fr.farmvivi.pterodactylautostarter.common.event.ServerConnectEvent;
import fr.farmvivi.pterodactylautostarter.common.listener.EventListener;
import fr.farmvivi.pterodactylautostarter.velocity.VelocityProxy;
import fr.farmvivi.pterodactylautostarter.velocity.VelocityServer;

import java.util.Collection;

public class VelocityServerConnectEventListener {
    private final VelocityProxy velocityProxy;
    private final Collection<EventListener> eventListeners;

    public VelocityServerConnectEventListener(VelocityProxy velocityProxy, Collection<EventListener> eventListeners) {
        this.velocityProxy = velocityProxy;
        this.eventListeners = eventListeners;
    }

    @Subscribe
    public void onServerConnect(com.velocitypowered.api.event.player.ServerPreConnectEvent event) {
        // Get the player
        CommonPlayer player = velocityProxy.getPlayer(event.getPlayer());

        // Get the target server
        CommonServer targetServer = velocityProxy.getServer(event.getOriginalServer().getServerInfo().getName());

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
            event.setResult(ServerPreConnectEvent.ServerResult.denied());
        } else {
            // Retrieve target server
            CommonServer target = serverConnectEvent.getTarget();

            // Get the VelocityServer
            VelocityServer velocityServer = (VelocityServer) target;

            // Set the target server
            event.setResult(ServerPreConnectEvent.ServerResult.allowed(velocityServer.getServer()));
        }
    }
}
