package net.ticketmanager.GUI;

import net.ticketmanager.Ticket;
import net.ticketmanager.TicketManagerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class TicketGUI implements Listener {

    private final TicketManagerPlugin plugin;

    public TicketGUI(TicketManagerPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openTicketGUI(Player staffMember, List<Ticket> tickets) {
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.GRAY + "Ticket Management");

        for (int i = 0; i < tickets.size() && i < 54; i++) {
            Ticket ticket = tickets.get(i);

            ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();

            if (skullMeta != null) {
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(ticket.getCreator()));
                skullMeta.setDisplayName(ChatColor.YELLOW + "Ticket #" + ticket.getId());

                // Reuse the formatting logic from /ticket list
                List<String> lore = formatTicketDetails(ticket);
                skullMeta.setLore(lore);
                skullItem.setItemMeta(skullMeta);
            }

            inventory.setItem(i, skullItem);
        }

        staffMember.openInventory(inventory);
    }

    // Handle inventory click events
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Ticket Management")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD) {
                SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
                if (skullMeta != null) {
                    String ticketId = ChatColor.stripColor(skullMeta.getDisplayName()).replace("Ticket #", "");
                    player.sendMessage(ChatColor.GREEN + "You clicked on Ticket #" + ticketId);
                    // Add more actions here as needed
                }
            }
        }
    }

    private List<String> formatTicketDetails(Ticket ticket) {
        List<String> details = new ArrayList<>();
        details.add(ChatColor.GREEN + "Submitted By: " + ChatColor.YELLOW + ticket.getCreator());
        details.add(ChatColor.GREEN + "Date: " + ChatColor.YELLOW + ticket.getSubmissionDate().toString());
        details.add(ChatColor.GREEN + "Status: " + ChatColor.YELLOW + ticket.getStatus());
        details.add(ChatColor.GREEN + "Message: " + ChatColor.YELLOW + ticket.getMessage());

        return details;
    }
}