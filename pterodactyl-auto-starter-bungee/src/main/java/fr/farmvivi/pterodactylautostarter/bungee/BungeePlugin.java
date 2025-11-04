package fr.farmvivi.pterodactylautostarter.bungee;

import fr.farmvivi.pterodactylautostarter.PterodactylAutoStarter;
import fr.farmvivi.pterodactylautostarter.bungee.listener.BungeePlayerDisconnectEventListener;
import fr.farmvivi.pterodactylautostarter.bungee.listener.BungeeProxyPingEventListener;
import fr.farmvivi.pterodactylautostarter.bungee.listener.BungeeServerConnectEventListener;
import fr.farmvivi.pterodactylautostarter.common.CommonPlugin;
import fr.farmvivi.pterodactylautostarter.common.listener.EventListener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Collection;
import java.util.LinkedList;

public class BungeePlugin extends Plugin implements CommonPlugin {
    private final Collection<EventListener> eventListeners = new LinkedList<>();

    @Override
    public void onEnable() {
        // Setting the plugin
        PterodactylAutoStarter.setPlugin(this, this.getLogger());

        // Building the BungeeProxy
        BungeeProxy proxy = new BungeeProxy(this.getProxy());

        // Registering the proxy as an event listener
        eventListeners.add(proxy);

        // Enabling the PterodactylAutoStarter
        PterodactylAutoStarter.getInstance().enable(proxy);

        // Registering the event listeners
        this.getLogger().info("Registering BungeeCord event listeners...");
        this.getProxy().getPluginManager().registerListener(this, new BungeeServerConnectEventListener(proxy, eventListeners));
        this.getProxy().getPluginManager().registerListener(this, new BungeeProxyPingEventListener(proxy, eventListeners));
        this.getProxy().getPluginManager().registerListener(this, new BungeePlayerDisconnectEventListener(proxy, eventListeners));
        this.getLogger().info("BungeeCord Event listeners registered.");
    }

    @Override
    public void onDisable() {
        PterodactylAutoStarter.getInstance().disable();
    }

    @Override
    public void addEventListener(EventListener eventListener) {
        this.eventListeners.add(eventListener);
    }

    @Override
    public void removeEventListener(EventListener eventListener) {
        this.eventListeners.remove(eventListener);
    }
}
