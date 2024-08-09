package net.ticketmanager;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import net.ticketmanager.database.MySQL;
import net.ticketmanager.commands.TicketCommand;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.logging.Level;

public class TicketManagerPlugin extends JavaPlugin {

    private MySQL mySQL;
    private TicketManager ticketManager;
    private File messagesFile;
    private YamlConfiguration messagesConfig;

    @Override
    public void onEnable() {
        // Load and save the default config.yml
        saveDefaultConfig();

        // Load messages.yml or create it if it doesn't exist
        loadMessagesConfig();

        // Initialize database connection
        setupDatabase();

        // Initialize the ticket manager
        ticketManager = new TicketManager(this, mySQL, getConfig().getBoolean("settings.use_database"));

        // Register the /ticket command
        this.getCommand("ticket").setExecutor(new TicketCommand(this));
    }

    private void loadMessagesConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false); // Creates messages.yml from plugin's resources folder
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void setupDatabase() {
        FileConfiguration config = getConfig();
        mySQL = new MySQL();  // No arguments needed
        try {
            mySQL.connect(
                    config.getString("database.host"),
                    config.getInt("database.port"),
                    config.getString("database.name"),
                    config.getString("database.user"),
                    config.getString("database.password")
            );
            getLogger().info("Successfully connected to the database.");
        } catch (Exception e) {
            getLogger().severe("Failed to connect to the database. Falling back to local storage.");
            getConfig().set("settings.use_database", false);
        }
    }

    @Override
    public void onDisable() {
        if (mySQL != null) {
            try {
                mySQL.disconnect();
            } catch (SQLException e) {
                getLogger().severe("Failed to disconnect from the database: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public TicketManager getTicketManager() {
        return ticketManager;
    }

    public String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(key, "&cMessage not found: " + key));
    }

    public void reloadPluginConfig() {
        reloadConfig();
        loadMessagesConfig(); // Reload messages.yml
        ticketManager.loadTickets(); // Reload tickets if necessary
    }

    public void logAction(String action) {
        if (getConfig().getBoolean("logging.log_to_console", true)) {
            getLogger().info(action);
        }

        if (getConfig().getBoolean("logging.log_to_file", false)) {
            try {
                File logFile = new File(getDataFolder(), getConfig().getString("logging.log_file", "logs/ticketmanager.log"));
                if (!logFile.exists()) {
                    logFile.getParentFile().mkdirs();
                    logFile.createNewFile();
                }
                try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
                    out.println(action);
                }
            } catch (IOException e) {
                getLogger().severe("Failed to log action to file: " + e.getMessage());
            }
        }
    }
}
