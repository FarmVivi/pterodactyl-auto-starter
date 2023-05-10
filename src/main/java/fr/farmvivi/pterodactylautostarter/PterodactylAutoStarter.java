package fr.farmvivi.pterodactylautostarter;

import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import fr.farmvivi.pterodactylautostarter.listener.PlayerDisconnectEventListener;
import fr.farmvivi.pterodactylautostarter.listener.ProxyPingEventListener;
import fr.farmvivi.pterodactylautostarter.listener.ServerConnectEventListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class PterodactylAutoStarter extends Plugin {
    private static PterodactylAutoStarter plugin;

    public static PterodactylAutoStarter getPlugin() {
        return plugin;
    }

    private final HashMap<ServerInfo, MinecraftServer> servers = new HashMap<>();
    private Configuration config;
    private PteroClient api;

    @Override
    public void onEnable() {
        try {
            plugin = this;
            getLogger().info("Running on " + ChatColor.DARK_AQUA + getProxy().getName());
            loadConfig();

            api = PteroBuilder.createClient(config.getString("pterodactyl.url"), config.getString("pterodactyl.token"));

            for (ServerInfo serverInfo : this.getProxy().getServersCopy().values()) {
                if (config.contains("servers." + serverInfo.getName())) {
                    servers.put(serverInfo, new MinecraftServer(
                            this,
                            serverInfo,
                            api.retrieveServerByIdentifier(config.getString("servers." + serverInfo.getName() + ".id")).execute()
                    ));
                    getLogger().info("Added " + serverInfo.getName() + " pterodactyl managed server");
                }
            }

            this.getProxy().getPluginManager().registerListener(this, new ProxyPingEventListener(this));
            this.getProxy().getPluginManager().registerListener(this, new ServerConnectEventListener(this));
            this.getProxy().getPluginManager().registerListener(this, new PlayerDisconnectEventListener(this));

            getLogger().info("Plugin enabled.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadConfig() throws IOException {
        // Create plugin config folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getLogger().info("Created config folder: " + getDataFolder().mkdir());
        }

        File configFile = new File(getDataFolder(), "config.yml");

        // Copy default config if it doesn't exist
        if (!configFile.exists()) {
            FileOutputStream outputStream = new FileOutputStream(configFile); // Throws IOException
            InputStream in = getResourceAsStream("config.yml"); // This file must exist in the jar resources folder
            in.transferTo(outputStream); // Throws IOException
        }

        // Load config
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
    }

    public HashMap<ServerInfo, MinecraftServer> getServers() {
        return servers;
    }

    public Configuration getConfig() {
        return config;
    }

    public PteroClient getApi() {
        return api;
    }
}
