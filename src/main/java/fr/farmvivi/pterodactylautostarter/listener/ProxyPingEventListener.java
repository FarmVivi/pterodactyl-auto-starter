package fr.farmvivi.pterodactylautostarter.listener;

import fr.farmvivi.pterodactylautostarter.MinecraftServer;
import fr.farmvivi.pterodactylautostarter.PterodactylAutoStarter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

public class ProxyPingEventListener implements Listener {
    private final PterodactylAutoStarter plugin;
    private final Map<String, ServerInfo> forcedHosts;
    private final Favicon offlineFavicon, startingFavicon;

    public ProxyPingEventListener(PterodactylAutoStarter plugin) {
        this.plugin = plugin;
        this.forcedHosts = new TreeMap<>();
        for (Map.Entry<String, String> entry : plugin.getProxy().getConfig().getListeners().iterator().next().getForcedHosts().entrySet()) {
            forcedHosts.put(entry.getKey(), plugin.getProxy().getServerInfo(entry.getValue()));
        }
        Favicon offlineFaviconTemp;
        Favicon startingFaviconTemp;
        try {
            offlineFaviconTemp = Favicon.create(ImageIO.read(plugin.getResourceAsStream("img/offline.png")));
            startingFaviconTemp = Favicon.create(ImageIO.read(plugin.getResourceAsStream("img/starting.png")));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Impossible de lire les favicon !", e);
            offlineFaviconTemp = null;
            startingFaviconTemp = null;
        }
        this.offlineFavicon = offlineFaviconTemp;
        this.startingFavicon = startingFaviconTemp;
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        InetSocketAddress virtualHost = event.getConnection().getVirtualHost();
        if (virtualHost != null) {
            String host = virtualHost.getHostString();
            if (forcedHosts.containsKey(host)) {
                ServerInfo serverInfo = forcedHosts.get(host);
                if (plugin.getServers().containsKey(serverInfo)) {
                    ServerPing response = event.getResponse();
                    MinecraftServer server = plugin.getServers().get(serverInfo);
                    switch (server.getStatus()) {
                        case ONLINE -> {
                            ServerPing serverPing = plugin.getServers().get(serverInfo).getServerPing();
                            response.setFavicon(serverPing.getFaviconObject());
                            response.setDescriptionComponent(serverPing.getDescriptionComponent());
                            response.setPlayers(serverPing.getPlayers());
                        }
                        case OFFLINE -> {
                            response.setPlayers(new ServerPing.Players(0, 0, new ServerPing.PlayerInfo[]{
                                    new ServerPing.PlayerInfo(ChatColor.RED + "Hors-ligne", ""),
                                    new ServerPing.PlayerInfo("", ""),
                                    new ServerPing.PlayerInfo(ChatColor.GRAY + "Connectez-vous", ""),
                                    new ServerPing.PlayerInfo(ChatColor.GRAY + "pour démarrer", ""),
                                    new ServerPing.PlayerInfo(ChatColor.YELLOW + serverInfo.getMotd(), "")
                            }));
                            response.setDescriptionComponent(new TextComponent("Serveur " + ChatColor.YELLOW + serverInfo.getMotd() + ChatColor.RED + " hors-ligne\n" + ChatColor.GRAY + "Connectez-vous pour le démarrer !"));
                            response.setVersion(new ServerPing.Protocol(ChatColor.RED + "Hors-ligne", -1));
                            if (offlineFavicon != null) {
                                response.setFavicon(offlineFavicon);
                            }
                        }
                        case STARTING -> {
                            int nbPlayers = server.getQueue().size();
                            List<ServerPing.PlayerInfo> players = new LinkedList<>();
                            players.add(new ServerPing.PlayerInfo(ChatColor.GOLD + "Démarrage...", ""));
                            players.add(new ServerPing.PlayerInfo("", ""));
                            players.add(new ServerPing.PlayerInfo(ChatColor.GRAY + "Connectez-vous", ""));
                            players.add(new ServerPing.PlayerInfo(ChatColor.GRAY + "pour rejoindre la", ""));
                            players.add(new ServerPing.PlayerInfo(ChatColor.GRAY + "file d'attente de", ""));
                            players.add(new ServerPing.PlayerInfo(ChatColor.YELLOW + serverInfo.getMotd(), ""));
                            if (nbPlayers > 0) {
                                players.add(new ServerPing.PlayerInfo("", ""));
                                players.add(new ServerPing.PlayerInfo(ChatColor.GRAY + "File d'attente :", ""));
                                for (ProxiedPlayer player : server.getQueue()) {
                                    players.add(new ServerPing.PlayerInfo(player.getDisplayName(), player.getUniqueId()));
                                }
                            }
                            response.setPlayers(new ServerPing.Players(-1, server.getQueue().size(), players.toArray(new ServerPing.PlayerInfo[0])));
                            response.setDescriptionComponent(new TextComponent("Serveur " + ChatColor.YELLOW + serverInfo.getMotd() + ChatColor.GOLD + " en démarrage...\n" + ChatColor.GRAY + "Connectez-vous pour rejoindre la file d'attente !"));
                            response.setVersion(new ServerPing.Protocol(ChatColor.GOLD + "Démarrage...", -1));
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
}
