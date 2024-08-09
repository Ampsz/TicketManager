package net.ticketmanager.commands;

import net.md_5.bungee.api.ChatColor;
import net.ticketmanager.TicketManager;
import net.ticketmanager.TicketManagerPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TicketCommand implements CommandExecutor {

    private final TicketManagerPlugin plugin;
    private final TicketManager ticketManager;

    private String PrefixMain = org.bukkit.ChatColor.GREEN + "VG" + org.bukkit.ChatColor.DARK_GREEN + org.bukkit.ChatColor.BOLD + " > " + org.bukkit.ChatColor.AQUA;

    public TicketCommand(TicketManagerPlugin plugin) {
        this.plugin = plugin;
        this.ticketManager = plugin.getTicketManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(PrefixMain + ChatColor.GREEN + "Use /ticket help to see the available commands.");
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            showHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("ticketmanager.reload")) {
                plugin.reloadPluginConfig();
                sender.sendMessage(PrefixMain + ChatColor.GREEN + "TicketManager configuration reloaded successfully.");
            } else {
                sender.sendMessage(PrefixMain + ChatColor.RED + "You cannot do that!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("prune")) {
            handlePruneCommand(sender, args);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage(PrefixMain + ChatColor.RED + "Usage: /ticket create <message>");
                    return true;
                }
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    messageBuilder.append(args[i]).append(" ");
                }
                String message = messageBuilder.toString().trim();
                ticketManager.createTicket(player.getName(), message, player.getLocation());
                player.sendMessage(PrefixMain + ChatColor.GREEN + "Your ticket has been created successfully!");
                break;

            case "list":
                ticketManager.listTickets(player);
                break;

            case "assign":
                if (args.length < 3) {
                    player.sendMessage(PrefixMain + ChatColor.RED + "Usage: /ticket assign <id> <player>");
                    return true;
                }
                try {
                    int id = Integer.parseInt(args[1]);
                    String assignee = args[2];
                    boolean success = ticketManager.assignTicket(id, assignee);
                    if (success) {
                        player.sendMessage(PrefixMain + ChatColor.GREEN + "Ticket has been successfully assigned.");
                    } else {
                        player.sendMessage(PrefixMain + ChatColor.RED + "Ticket with ID " + id + " could not be found.");
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(PrefixMain + ChatColor.RED + "Invalid ticket ID.");
                }
                break;

            case "close":
                if (args.length < 2) {
                    player.sendMessage(PrefixMain + ChatColor.RED + "Usage: /ticket close <id>");
                    return true;
                }
                try {
                    int id = Integer.parseInt(args[1]);
                    boolean success = ticketManager.closeTicket(id);
                    if (success) {
                        player.sendMessage(PrefixMain + ChatColor.GREEN + "Ticket with ID " + id + " has been successfully closed.");
                    } else {
                        player.sendMessage(PrefixMain + ChatColor.RED + "Ticket with ID " + id + " could not be found.");
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(PrefixMain + ChatColor.RED + "Invalid ticket ID.");
                }
                break;

            default:
                player.sendMessage(PrefixMain + ChatColor.RED + "Unknown subcommand. Use /ticket help for a list of commands.");
        }

        return true;
    }

    private void handlePruneCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ticketmanager.prune")) {
            sender.sendMessage(PrefixMain + ChatColor.RED + "You cannot do that!");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(PrefixMain + ChatColor.RED + "Usage: /ticket prune <days>");
            return;
        }

        try {
            int days = Integer.parseInt(args[1]);
            int prunedCount = ticketManager.pruneTickets(days);
            sender.sendMessage(PrefixMain + ChatColor.GREEN + "Successfully pruned " + prunedCount + " tickets.");
        } catch (NumberFormatException e) {
            sender.sendMessage(PrefixMain + ChatColor.RED + "Invalid number of days.");
        }
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "-- TicketManager Help --");
        sender.sendMessage(ChatColor.YELLOW + "/ticket create <message>" + ChatColor.WHITE + " - Creates a new ticket.");
        sender.sendMessage(ChatColor.YELLOW + "/ticket list" + ChatColor.WHITE + " - Lists all open tickets.");
        sender.sendMessage(ChatColor.YELLOW + "/ticket assign <id> <player>" + ChatColor.WHITE + " - Assigns a ticket to a player.");
        sender.sendMessage(ChatColor.YELLOW + "/ticket close <id>" + ChatColor.WHITE + " - Closes a ticket.");
        sender.sendMessage(ChatColor.YELLOW + "/ticket reload" + ChatColor.WHITE + " - Reloads the plugin configuration.");
        sender.sendMessage(ChatColor.YELLOW + "/ticket prune <days>" + ChatColor.WHITE + " - Prunes tickets older than the specified number of days.");
    }
}
