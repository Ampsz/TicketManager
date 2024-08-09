package net.ticketmanager;

import net.md_5.bungee.api.ChatColor;
import net.ticketmanager.GUI.TicketGUI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.ticketmanager.database.MySQL;
import net.ticketmanager.commands.TicketCommand;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TicketManagerPlugin extends JavaPlugin {

    private MySQL mySQL;
    private TicketManager ticketManager;
    private TicketGUI ticketGUI;
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

        // Initialize the TicketGUI
        ticketGUI = new TicketGUI(this);

        // Register the /ticket command
        this.getCommand("ticket").setExecutor(new TicketCommand(this));

        // Register the /ticketgui command
        this.getCommand("ticketgui").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player && sender.hasPermission("ticketmanager.gui")) {
                Player player = (Player) sender;
                List<Ticket> tickets = ticketManager.getTickets();
                ticketGUI.openTicketGUI(player, tickets);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return false;
            }
        });
    }

    private void loadMessagesConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void setupDatabase() {
        FileConfiguration config = getConfig();

        // Initialize MySQL with connection details
        mySQL = new MySQL(
                config.getString("database.host"),
                config.getInt("database.port"),
                config.getString("database.name"),
                config.getString("database.user"),
                config.getString("database.password")
        );

        try {
            mySQL.connect();
            getLogger().info("Successfully connected to the database.");
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to the database: " + e.getMessage());
            e.printStackTrace();
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

    // Add this method to return the TicketGUI instance
    public TicketGUI getTicketGUI() {
        return ticketGUI;
    }

    public String getMessage(String key) {
        String message = messagesConfig.getString(key);
        if (message == null) {
            Bukkit.getLogger().severe("Message not found for key: " + key);
            return ChatColor.translateAlternateColorCodes('&', "&cMessage not found: " + key);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void reloadPluginConfig() {
        reloadConfig();
        loadMessagesConfig();
        ticketManager.loadTickets();
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
