package fr.farmvivi.pterodactylautostarter.common.listener;

import fr.farmvivi.pterodactylautostarter.common.event.PlayerDisconnectEvent;
import fr.farmvivi.pterodactylautostarter.common.event.ProxyPingEvent;
import fr.farmvivi.pterodactylautostarter.common.event.ServerConnectEvent;

public interface EventListener {
    void onPlayerDisconnect(PlayerDisconnectEvent event);

    void onProxyPing(ProxyPingEvent event);

    void onServerConnect(ServerConnectEvent event);
}
