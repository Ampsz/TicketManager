package net.ticketmanager;

import net.md_5.bungee.api.ChatColor;
import net.ticketmanager.commands.TicketCommand;
import net.ticketmanager.database.MySQL;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TicketManagerPlugin extends JavaPlugin {

    private MySQL mySQL;
    private TicketManager ticketManager;
    private boolean useDatabase = true;
    private File messagesFile;
    private YamlConfiguration messagesConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);

        loadMessagesConfig();

        FileConfiguration config = getConfig();

        mySQL = new MySQL();

        try {
            mySQL.connect(
                    config.getString("database.host"),
                    config.getInt("database.port"),
                    config.getString("database.name"),
                    config.getString("database.user"),
                    config.getString("database.password")
            );
            getLogger().info("TicketManager - Successfully connected to the database");

            // Initialize the database (create tables if they don't exist)
            initializeDatabase();

            // Initialize ticket manager and load tickets
            ticketManager = new TicketManager(this, mySQL, useDatabase);  // Pass `this` as the plugin instance

            // Register commands
            this.getCommand("ticket").setExecutor(new TicketCommand(this));

        } catch (SQLException e) {
            e.printStackTrace();
            getLogger().severe("TicketManager - Could not connect to the database. Falling back to local storage.");
            useDatabase = false;
        }
    }

    @Override
    public void onDisable() {
        if (useDatabase) {
            try {
                mySQL.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        loadMessagesConfig();
        ticketManager.loadTickets();  // Reload tickets if necessary
    }

    private void loadMessagesConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void initializeDatabase() {
        try {
            Connection connection = mySQL.getConnection();
            try (Statement statement = connection.createStatement()) {

                // Create tickets table if it doesn't exist
                String sql = "CREATE TABLE IF NOT EXISTS tickets (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "creator VARCHAR(255), " +
                        "message TEXT, " +
                        "assignee VARCHAR(255), " +
                        "status VARCHAR(50), " +
                        "world VARCHAR(255), " +
                        "x DOUBLE, " +
                        "y DOUBLE, " +
                        "z DOUBLE, " +
                        "yaw FLOAT, " +
                        "pitch FLOAT)";

                statement.executeUpdate(sql);
                getLogger().info("TicketManager - Database initialized successfully");

            } catch (SQLException e) {
                e.printStackTrace();
                getLogger().severe("TicketManager - Failed to initialize the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            getLogger().severe("TicketManager - Failed to establish database connection for initialization.");
        }
    }

    public String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(key, "&cMessage not found: " + key));
    }

    public YamlConfiguration getMessagesConfig() {
        return messagesConfig;
    }
}
