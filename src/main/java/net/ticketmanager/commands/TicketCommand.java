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

    public TicketCommand(TicketManagerPlugin plugin) {
        this.plugin = plugin;
        this.ticketManager = plugin.getTicketManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "Use /ticket help to see the available commands.");
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            showHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("ticketmanager.reload")) {
                plugin.reloadPluginConfig();
                // Send the reload success message
                sender.sendMessage(plugin.getMessage("reload_success"));
            } else {
                sender.sendMessage(plugin.getMessage("no_permission"));
            }
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("only_players"));
            return true;
        }

        Player player = (Player) sender;

        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /ticket create <message>");
                    return true;
                }
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    messageBuilder.append(args[i]).append(" ");
                }
                String message = messageBuilder.toString().trim();
                ticketManager.createTicket(player.getName(), message, player.getLocation());
                player.sendMessage(plugin.getMessage("ticket_created"));
                break;

            case "list":
                ticketManager.listTickets(player);
                break;

            case "assign":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /ticket assign <id> <player>");
                    return true;
                }
                try {
                    int id = Integer.parseInt(args[1]);
                    String assignee = args[2];
                    boolean success = ticketManager.assignTicket(id, assignee);
                    if (success) {
                        player.sendMessage(plugin.getMessage("ticket_assigned"));
                    } else {
                        player.sendMessage(plugin.getMessage("ticket_not_found").replace("{id}", String.valueOf(id)));
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getMessage("invalid_ticket_id"));
                }
                break;

            case "close":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /ticket close <id>");
                    return true;
                }
                try {
                    int id = Integer.parseInt(args[1]);
                    boolean success = ticketManager.closeTicket(id);
                    if (success) {
                        player.sendMessage(plugin.getMessage("ticket_closed").replace("{id}", String.valueOf(id)));
                    } else {
                        player.sendMessage(plugin.getMessage("ticket_not_found").replace("{id}", String.valueOf(id)));
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getMessage("invalid_ticket_id"));
                }
                break;

            default:
                player.sendMessage(ChatColor.RED + "Unknown subcommand. Use /ticket help for a list of commands.");
        }

        return true;
    }

    private void showHelp(CommandSender sender) {
        // Hardcoded help messages
        sender.sendMessage(ChatColor.GOLD + "TicketManager Help");
        sender.sendMessage(ChatColor.YELLOW + "/ticket create <message>" + ChatColor.WHITE + " - Creates a new ticket.");
        sender.sendMessage(ChatColor.YELLOW + "/ticket list" + ChatColor.WHITE + " - Lists all open tickets.");
        sender.sendMessage(ChatColor.YELLOW + "/ticket assign <id> <player>" + ChatColor.WHITE + " - Assigns a ticket to a player.");
        sender.sendMessage(ChatColor.YELLOW + "/ticket close <id>" + ChatColor.WHITE + " - Closes a ticket.");
        sender.sendMessage(ChatColor.YELLOW + "/ticket reload" + ChatColor.WHITE + " - Reloads the configuration and tickets.");
    }
}
