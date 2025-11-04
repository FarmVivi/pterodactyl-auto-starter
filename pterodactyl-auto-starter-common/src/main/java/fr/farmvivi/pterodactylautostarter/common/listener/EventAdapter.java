package fr.farmvivi.pterodactylautostarter.common.listener;

import fr.farmvivi.pterodactylautostarter.common.event.PlayerDisconnectEvent;
import fr.farmvivi.pterodactylautostarter.common.event.ProxyPingEvent;
import fr.farmvivi.pterodactylautostarter.common.event.ServerConnectEvent;

public class EventAdapter implements EventListener {
    @Override
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
    }

    @Override
    public void onProxyPing(ProxyPingEvent event) {
    }

    @Override
    public void onServerConnect(ServerConnectEvent event) {
    }
}
