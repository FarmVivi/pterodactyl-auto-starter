package fr.farmvivi.pterodactylautostarter.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.farmvivi.pterodactylautostarter.PterodactylAutoStarter;
import fr.farmvivi.pterodactylautostarter.common.CommonPlugin;
import fr.farmvivi.pterodactylautostarter.common.listener.EventListener;
import fr.farmvivi.pterodactylautostarter.velocity.listener.VelocityPlayerDisconnectEventListener;
import fr.farmvivi.pterodactylautostarter.velocity.listener.VelocityProxyPingEventListener;
import fr.farmvivi.pterodactylautostarter.velocity.listener.VelocityServerConnectEventListener;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

public class VelocityPlugin implements CommonPlugin {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private final Collection<EventListener> eventListeners = new LinkedList<>();

    @Inject
    public VelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        // Setting the plugin
        PterodactylAutoStarter.setPlugin(this, logger);

        // Building the VelocityProxy
        VelocityProxy proxy = new VelocityProxy(server);

        // Registering the proxy as an event listener
        eventListeners.add(proxy);

        // Enabling the PterodactylAutoStarter
        PterodactylAutoStarter.getInstance().enable(proxy);

        // Registering the event listeners
        this.logger.info("Registering Velocity event listeners...");
        this.server.getEventManager().register(this, new VelocityServerConnectEventListener(proxy, eventListeners));
        this.server.getEventManager().register(this, new VelocityProxyPingEventListener(logger, proxy, eventListeners));
        this.server.getEventManager().register(this, new VelocityPlayerDisconnectEventListener(proxy, eventListeners));
        this.logger.info("Velocity Event listeners registered.");
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        PterodactylAutoStarter.getInstance().disable();
    }

    @Override
    public File getDataFolder() {
        return dataDirectory.toFile();
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
