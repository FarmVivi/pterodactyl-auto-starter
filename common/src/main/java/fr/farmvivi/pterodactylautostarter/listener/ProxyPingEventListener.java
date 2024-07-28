package fr.farmvivi.pterodactylautostarter.listener;

import fr.farmvivi.pterodactylautostarter.MinecraftServer;
import fr.farmvivi.pterodactylautostarter.PterodactylAutoStarter;
import fr.farmvivi.pterodactylautostarter.common.CommonPlayer;
import fr.farmvivi.pterodactylautostarter.common.CommonServer;
import fr.farmvivi.pterodactylautostarter.common.event.ProxyPingEvent;
import fr.farmvivi.pterodactylautostarter.common.listener.EventAdapter;
import fr.farmvivi.pterodactylautostarter.common.ping.CommonFavicon;
import fr.farmvivi.pterodactylautostarter.common.ping.CommonServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

public class ProxyPingEventListener extends EventAdapter {
    private final PterodactylAutoStarter plugin;
    private final Map<String, CommonServer> forcedHosts;
    private final CommonFavicon offlineFavicon, startingFavicon;

    public ProxyPingEventListener(PterodactylAutoStarter plugin) {
        this.plugin = plugin;
        this.forcedHosts = new TreeMap<>();
        for (Map.Entry<String, String> entry : plugin.getProxy().getForcedHosts().entrySet()) {
            forcedHosts.put(entry.getKey(), plugin.getProxy().getServer(entry.getValue()));
        }
        CommonFavicon offlineFaviconTemp;
        CommonFavicon startingFaviconTemp;
        try {
            offlineFaviconTemp = plugin.getProxy().createFavicon(ImageIO.read(plugin.getResourceAsStream("img/offline.png")));
            startingFaviconTemp = plugin.getProxy().createFavicon(ImageIO.read(plugin.getResourceAsStream("img/starting.png")));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Impossible de lire les favicon !", e);
            offlineFaviconTemp = null;
            startingFaviconTemp = null;
        }
        this.offlineFavicon = offlineFaviconTemp;
        this.startingFavicon = startingFaviconTemp;
    }

    @Override
    public void onProxyPing(ProxyPingEvent event) {
        String host = event.getHost();
        if (host == null || host.isEmpty()) {
            return;
        }
        if (forcedHosts.containsKey(host)) {
            CommonServer commonServer = forcedHosts.get(host);
            if (plugin.getServers().containsKey(commonServer)) {
                CommonServerPing response = event.getResponse();
                MinecraftServer server = plugin.getServers().get(commonServer);
                switch (server.getStatus()) {
                    case ONLINE -> {
                        // Do not override the response if the server is online
//                        CommonServerPing serverPing = plugin.getServers().get(commonServer).getServerPing();
//                        response.setFavicon(serverPing.getFavicon());
//                        response.setDescriptionComponent(serverPing.getDescriptionComponent());
//                        response.setPlayers(serverPing.getPlayers());
                        return;
                    }
                    case OFFLINE -> {
                        List<Component> players = new LinkedList<>();
                        players.add(Component.text("Hors-ligne").color(NamedTextColor.RED));
                        players.add(Component.text(""));
                        players.add(Component.text("Connectez-vous").color(NamedTextColor.GRAY));
                        players.add(Component.text("pour démarrer").color(NamedTextColor.GRAY));
                        players.add(Component.text(commonServer.getDisplayName()).color(NamedTextColor.YELLOW));
                        response.setMaxPlayers(0);
                        response.setOnlinePlayers(0);
                        response.setSamplePlayers(players);
                        response.setDescriptionComponent(Component.text("Serveur ").append(Component.text(commonServer.getDisplayName()).color(NamedTextColor.YELLOW)).append(Component.text(" hors-ligne").color(NamedTextColor.RED)).appendNewline().append(Component.text("Connectez-vous pour le démarrer !").color(NamedTextColor.GRAY)));
                        response.setProtocolVersion(-1);
                        response.setProtocolName(Component.text("Hors-ligne").color(NamedTextColor.RED));
                        if (offlineFavicon != null) {
                            response.setFavicon(offlineFavicon);
                        }
                    }
                    case STARTING -> {
                        int nbPlayers = server.getQueue().size();
                        List<Component> players = new LinkedList<>();
                        players.add(Component.text("Démarrage...").color(NamedTextColor.GOLD));
                        players.add(Component.text(""));
                        players.add(Component.text("Connectez-vous").color(NamedTextColor.GRAY));
                        players.add(Component.text("pour rejoindre la").color(NamedTextColor.GRAY));
                        players.add(Component.text("file d'attente de").color(NamedTextColor.GRAY));
                        players.add(Component.text(commonServer.getDisplayName()).color(NamedTextColor.YELLOW));
                        if (nbPlayers > 0) {
                            players.add(Component.text(""));

                            players.add(Component.text("File d'attente :").color(NamedTextColor.GRAY));
                            for (CommonPlayer player : server.getQueue()) {
                                players.add(Component.text(player.getDisplayName()).color(NamedTextColor.GRAY));
                            }
                        }
                        response.setMaxPlayers(-1);
                        response.setOnlinePlayers(nbPlayers);
                        response.setSamplePlayers(players);
                        response.setDescriptionComponent(Component.text("Serveur ").append(Component.text(commonServer.getDisplayName()).color(NamedTextColor.YELLOW)).append(Component.text(" en démarrage...").color(NamedTextColor.GOLD)).appendNewline().append(Component.text("Connectez-vous pour rejoindre la file d'attente !").color(NamedTextColor.GRAY)));
                        response.setProtocolVersion(-1);
                        response.setProtocolName(Component.text("Démarrage...").color(NamedTextColor.GOLD));
                        if (startingFavicon != null) {
                            response.setFavicon(startingFavicon);
                        }
                    }
                }
                event.setResponse(response);
            }
        }
    }
}
