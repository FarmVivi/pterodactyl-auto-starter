package fr.farmvivi.pterodactylautostarter.bungee.listener;

import fr.farmvivi.pterodactylautostarter.bungee.ping.BungeeServerPing;
import fr.farmvivi.pterodactylautostarter.common.event.ProxyPingEvent;
import fr.farmvivi.pterodactylautostarter.common.listener.EventListener;
import fr.farmvivi.pterodactylautostarter.common.ping.CommonServerPing;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Collection;

public class BungeeProxyPingEventListener implements Listener {
    private final Collection<EventListener> eventListeners;

    public BungeeProxyPingEventListener(Collection<EventListener> eventListeners) {
        this.eventListeners = eventListeners;
    }

    @EventHandler
    public void onProxyPing(net.md_5.bungee.api.event.ProxyPingEvent event) {
        // Get the host
        String host = event.getConnection().getVirtualHost().getHostString();

        // Construct the event
        ProxyPingEvent proxyPingEvent = new ProxyPingEvent(host, new BungeeServerPing(event.getResponse()));

        // Call the event
        for (EventListener eventListener : eventListeners) {
            eventListener.onProxyPing(proxyPingEvent);
        }

        // Post call
        proxyPingEvent.postCall();

        // Retrieve ping
        CommonServerPing response = proxyPingEvent.getResponse();

        if (response instanceof BungeeServerPing bungeeServerPing) {
            // Set the ping
            event.setResponse(bungeeServerPing.toBungeeServerPing());
        }
    }
}
