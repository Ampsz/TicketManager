package net.ticketmanager;

import net.ticketmanager.database.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TicketManager {

    private final TicketManagerPlugin plugin;
    private final MySQL mySQL;
    private final List<Ticket> tickets = new ArrayList<>();
    private int nextId = 1;
    private final boolean useDatabase;

    public TicketManager(TicketManagerPlugin plugin, MySQL mySQL, boolean useDatabase) {
        this.plugin = plugin;
        this.mySQL = mySQL;
        this.useDatabase = useDatabase;
        loadTickets();
    }

    public void loadTickets() {
        if (useDatabase) {
            loadTicketsFromDatabase();
        } else {
            loadTicketsFromLocalStorage();
        }
    }

    private void loadTicketsFromDatabase() {
        try (Connection connection = mySQL.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM tickets")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String creator = resultSet.getString("creator");
                String message = resultSet.getString("message");
                String assignee = resultSet.getString("assignee");
                String status = resultSet.getString("status");
                String world = resultSet.getString("world");
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                float yaw = resultSet.getFloat("yaw");
                float pitch = resultSet.getFloat("pitch");

                Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                tickets.add(new Ticket(id, creator, message, assignee, status, location));
                nextId = Math.max(nextId, id + 1);
            }
            Bukkit.getLogger().info("TicketManager - Tickets loaded from database");
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("TicketManager - Failed to load tickets from the database");
        }
    }

    private void loadTicketsFromLocalStorage() {
        // Load tickets from local storage (not implemented in this snippet)
    }

    public void createTicket(String creator, String message, Location location) {
        Ticket ticket = new Ticket(nextId++, creator, message, "None", "Open", location);
        tickets.add(ticket);
        if (useDatabase) {
            saveTicketToDatabase(ticket);
        } else {
            saveTicketToLocalStorage(ticket);
        }
    }

    private void saveTicketToDatabase(Ticket ticket) {
        try (Connection connection = mySQL.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO tickets (id, creator, message, assignee, status, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            statement.setInt(1, ticket.getId());
            statement.setString(2, ticket.getCreator());
            statement.setString(3, ticket.getMessage());
            statement.setString(4, ticket.getAssignee());
            statement.setString(5, ticket.getStatus());
            statement.setString(6, ticket.getLocation().getWorld().getName());
            statement.setDouble(7, ticket.getLocation().getX());
            statement.setDouble(8, ticket.getLocation().getY());
            statement.setDouble(9, ticket.getLocation().getZ());
            statement.setFloat(10, ticket.getLocation().getYaw());
            statement.setFloat(11, ticket.getLocation().getPitch());
            statement.executeUpdate();
            Bukkit.getLogger().info("TicketManager - Ticket saved to database");
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("TicketManager - Failed to save ticket to the database: " + e.getMessage());
        }
    }

    private void saveTicketToLocalStorage(Ticket ticket) {
        // Save ticket to local storage (not implemented in this snippet)
    }

    public void listTickets(CommandSender sender) {
        if (tickets.isEmpty()) {
            sender.sendMessage(plugin.getMessage("no_tickets"));
            return;
        }
        sender.sendMessage(plugin.getMessage("list_tickets_header"));
        for (Ticket ticket : tickets) {
            if (!ticket.getStatus().equalsIgnoreCase("Closed")) {
                String location = String.format("world %.0f %.0f %.0f",
                        (double) Math.round(ticket.getLocation().getX()),
                        (double) Math.round(ticket.getLocation().getY()),
                        (double) Math.round(ticket.getLocation().getZ()));

                String header = plugin.getMessage("ticket_layout.header");
                String creator = plugin.getMessage("ticket_layout.creator")
                        .replace("{id}", String.valueOf(ticket.getId()))
                        .replace("{creator}", ticket.getCreator());
                String assignedTo = plugin.getMessage("ticket_layout.assigned_to")
                        .replace("{assignee}", ticket.getAssignee().equals("None") ? "None" : ticket.getAssignee());
                String priority = plugin.getMessage("ticket_layout.priority")
                        .replace("{priority}", "NORMAL");
                String status = plugin.getMessage("ticket_layout.status")
                        .replace("{status}", ticket.getStatus())
                        .replace("{message}", ticket.getMessage());
                String loc = plugin.getMessage("ticket_layout.location")
                        .replace("{location}", location);
                String footer = plugin.getMessage("ticket_layout.footer");

                sender.sendMessage(header);
                sender.sendMessage(creator + "  " + assignedTo);
                sender.sendMessage(priority + "  " + status);
                sender.sendMessage(loc);
                sender.sendMessage(footer);
            }
        }
    }


    public boolean assignTicket(int id, String assignee) {
        Ticket ticket = getTicketById(id);
        if (ticket != null && !ticket.getStatus().equalsIgnoreCase("Closed")) {
            ticket.setAssignee(assignee);
            if (useDatabase) {
                updateTicketInDatabase(ticket);
            } else {
                saveTicketToLocalStorage(ticket);
            }
            return true;
        }
        return false;
    }

    public boolean closeTicket(int id) {
        Ticket ticket = getTicketById(id);
        if (ticket == null) {
            Bukkit.getConsoleSender().sendMessage(plugin.getMessage("ticket_not_found").replace("{id}", String.valueOf(id)));
            return false;
        }

        ticket.setStatus("Closed");
        tickets.remove(ticket); // Remove the closed ticket from the list

        if (useDatabase) {
            updateTicketInDatabase(ticket);
        } else {
            saveTicketToLocalStorage(ticket);
        }

        Bukkit.getConsoleSender().sendMessage(plugin.getMessage("ticket_closed").replace("{id}", String.valueOf(ticket.getId())));

        Player creator = Bukkit.getPlayer(ticket.getCreator());
        if (creator != null) {
            creator.sendMessage(plugin.getMessage("ticket_closed_creator").replace("{id}", String.valueOf(ticket.getId())));
        }

        // Update the GUI after closing the ticket
        plugin.getTicketGUI().updateTicketGUIForAllStaff();

        return true;
    }

    private void updateTicketInDatabase(Ticket ticket) {
        try (Connection connection = mySQL.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE tickets SET assignee = ?, status = ?, world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE id = ?")) {
            statement.setString(1, ticket.getAssignee());
            statement.setString(2, ticket.getStatus());
            statement.setString(3, ticket.getLocation().getWorld().getName());
            statement.setDouble(4, ticket.getLocation().getX());
            statement.setDouble(5, ticket.getLocation().getY());
            statement.setDouble(6, ticket.getLocation().getZ());
            statement.setFloat(7, ticket.getLocation().getYaw());
            statement.setFloat(8, ticket.getLocation().getPitch());
            statement.setInt(9, ticket.getId());
            statement.executeUpdate();
            Bukkit.getLogger().info("TicketManager - Ticket ID " + ticket.getId() + " updated in database with status " + ticket.getStatus());
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("TicketManager - Failed to update ticket in the database: " + e.getMessage());
        }
    }

    public int pruneTickets(int days) {
        int prunedCount = 0;
        if (mySQL != null) {
            try (Connection connection = mySQL.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM tickets")) {
                prunedCount = statement.executeUpdate();
                plugin.getLogger().info("Pruned " + prunedCount + " tickets from the database.");
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to prune tickets: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return prunedCount;
    }

    private Ticket getTicketById(int id) {
        for (Ticket ticket : tickets) {
            if (ticket.getId() == id) {
                return ticket;
            }
        }
        return null;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
