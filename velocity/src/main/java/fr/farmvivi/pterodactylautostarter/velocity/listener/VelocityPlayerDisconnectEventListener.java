package fr.farmvivi.pterodactylautostarter.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import fr.farmvivi.pterodactylautostarter.common.CommonPlayer;
import fr.farmvivi.pterodactylautostarter.common.CommonProxy;
import fr.farmvivi.pterodactylautostarter.common.event.PlayerDisconnectEvent;
import fr.farmvivi.pterodactylautostarter.common.listener.EventListener;

import java.util.Collection;

public class VelocityPlayerDisconnectEventListener {
    private final CommonProxy proxy;
    private final Collection<EventListener> eventListeners;

    public VelocityPlayerDisconnectEventListener(CommonProxy proxy, Collection<EventListener> eventListeners) {
        this.proxy = proxy;
        this.eventListeners = eventListeners;
    }

    @Subscribe
    public void onPlayerDisconnect(com.velocitypowered.api.event.connection.DisconnectEvent event) {
        // Get the player
        CommonPlayer player = proxy.getPlayer(event.getPlayer().getUniqueId());

        // Construct the event
        PlayerDisconnectEvent playerDisconnectEvent = new PlayerDisconnectEvent(player);

        // Call the event
        for (EventListener eventListener : eventListeners) {
            eventListener.onPlayerDisconnect(playerDisconnectEvent);
        }

        // Post call
        playerDisconnectEvent.postCall();
    }
}
