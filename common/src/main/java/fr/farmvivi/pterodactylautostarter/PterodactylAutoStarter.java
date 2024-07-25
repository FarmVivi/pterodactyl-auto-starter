package fr.farmvivi.pterodactylautostarter;

import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import fr.farmvivi.pterodactylautostarter.common.CommonPlugin;
import fr.farmvivi.pterodactylautostarter.common.CommonProxy;
import fr.farmvivi.pterodactylautostarter.common.CommonServer;
import fr.farmvivi.pterodactylautostarter.listener.PlayerDisconnectEventListener;
import fr.farmvivi.pterodactylautostarter.listener.ProxyPingEventListener;
import fr.farmvivi.pterodactylautostarter.listener.ServerConnectEventListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

public final class PterodactylAutoStarter {
    public static final String NAME = "PterodactylAutoStarter";
    public static final String VERSION;
    public static final boolean PRODUCTION;
    private static PterodactylAutoStarter instance;

    static {
        Properties properties = new Properties();
        try {
            properties.load(PterodactylAutoStarter.class.getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            System.out.println("ERROR: Cannot read properties file ! Using default values");
            System.exit(1);
        }

        VERSION = properties.getProperty("version");
        PRODUCTION = !PterodactylAutoStarter.VERSION.contains("-SNAPSHOT");
    }

    private final CommonPlugin plugin;
    private final LoggerProxy logger;
    private final HashMap<CommonServer, MinecraftServer> servers = new HashMap<>();
    private CommonProxy proxy;
    private Configuration config;
    private PteroClient api;

    public PterodactylAutoStarter(CommonPlugin plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = new LoggerProxy(logger);
    }

    public static PterodactylAutoStarter getInstance() {
        return instance;
    }

    public static void setPlugin(CommonPlugin plugin, Logger logger) {
        PterodactylAutoStarter.instance = new PterodactylAutoStarter(plugin, logger);
    }

    public void enable(CommonProxy proxy) {
        try {
            this.getLogger().info(Component.text(NAME).color(NamedTextColor.DARK_AQUA)
                    .append(Component.text(" v" + VERSION, NamedTextColor.YELLOW))
                    .append(Component.text(" running on "))
                    .append(Component.text(proxy.getName(), NamedTextColor.DARK_AQUA))
                    .append(Component.text(" v" + proxy.getVersion(), NamedTextColor.YELLOW))
                    .append(Component.text("..."))
            );

            this.proxy = proxy;

            // Load config
            this.loadConfig();

            this.api = PteroBuilder.createClient(config.getString("pterodactyl.url"), config.getString("pterodactyl.token"));

            // Initialize servers
            this.initServers();

            // Register listeners
            this.getLogger().info("Registering event listeners...");
            this.getPlugin().addEventListener(new ServerConnectEventListener(this));
            this.getPlugin().addEventListener(new ProxyPingEventListener(this));
            this.getPlugin().addEventListener(new PlayerDisconnectEventListener(this));
            this.getLogger().info("Event listeners registered.");

            this.getLogger().info("Plugin enabled.");
        } catch (Exception ex) {
            this.getLogger().log(SEVERE, "An error occurred while enabling the plugin.", ex);
        }
    }

    public void disable() {
        try {
            this.getLogger().info("Disabling plugin...");

            // Cancel tasks
            this.proxy.cancel(plugin);

            this.getLogger().info("Plugin disabled.");
        } catch (Exception ex) {
            this.getLogger().log(SEVERE, "An error occurred while disabling the plugin.", ex);
        }
    }

    private void loadConfig() throws IOException {
        this.getLogger().info("Loading config...");

        File dataFolder = this.plugin.getDataFolder();

        // Create plugin config folder if it doesn't exist
        if (!dataFolder.exists()) {
            this.getLogger().info("Creating config folder...");
            dataFolder.mkdirs();
            this.getLogger().info("Config folder created.");
        }

        File configFile = new File(dataFolder, "config.yml");

        // Copy default config if it doesn't exist
        if (!configFile.exists()) {
            FileOutputStream outputStream = new FileOutputStream(configFile); // Throws IOException
            InputStream in = this.getResourceAsStream("config.yml"); // This file must exist in the jar resources folder
            in.transferTo(outputStream); // Throws IOException
        }

        // Load config
        this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

        this.getLogger().info("Config loaded.");
    }

    private void initServers() {
        this.getLogger().info("Initializing servers...");

        for (CommonServer server : proxy.getServers()) {
            if (config.contains("servers." + server.getName())) {
                // Retrieve server pterodactyl identifier from config file
                String pterodactylServerId = config.getString("servers." + server.getName() + ".id");

                // Registering the server
                this.getLogger().info("Registering " + server.getName() + " server with pterodactyl identifier " + pterodactylServerId);

                // Retrieve pterodactyl server
                ClientServer pterodactylServer = this.api.retrieveServerByIdentifier(pterodactylServerId).execute();

                // Add server to the servers list
                servers.put(server, new MinecraftServer(this, server, pterodactylServer));

                // Server registered
                this.getLogger().info("Registered " + server.getName() + " server with pterodactyl identifier " + pterodactylServerId);
            }
        }

        this.getLogger().info("Servers initialized.");
    }

    public InputStream getResourceAsStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }

    public CommonPlugin getPlugin() {
        return plugin;
    }

    public HashMap<CommonServer, MinecraftServer> getServers() {
        return servers;
    }

    public LoggerProxy getLogger() {
        return logger;
    }

    public CommonProxy getProxy() {
        return proxy;
    }

    public Configuration getConfig() {
        return config;
    }

    public PteroClient getApi() {
        return api;
    }
}
