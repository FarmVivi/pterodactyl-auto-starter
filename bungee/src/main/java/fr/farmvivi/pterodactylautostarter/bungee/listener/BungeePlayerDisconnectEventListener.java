package fr.farmvivi.pterodactylautostarter.bungee.listener;

import fr.farmvivi.pterodactylautostarter.common.CommonPlayer;
import fr.farmvivi.pterodactylautostarter.common.CommonProxy;
import fr.farmvivi.pterodactylautostarter.common.event.PlayerDisconnectEvent;
import fr.farmvivi.pterodactylautostarter.common.listener.EventListener;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Collection;

public class BungeePlayerDisconnectEventListener implements Listener {
    private final CommonProxy proxy;
    private final Collection<EventListener> eventListeners;

    public BungeePlayerDisconnectEventListener(CommonProxy proxy, Collection<EventListener> eventListeners) {
        this.proxy = proxy;
        this.eventListeners = eventListeners;
    }

    @EventHandler
    public void onPlayerDisconnect(net.md_5.bungee.api.event.PlayerDisconnectEvent event) {
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
