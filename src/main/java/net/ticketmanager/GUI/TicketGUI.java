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
import org.bukkit.event.inventory.InventoryDragEvent;
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

                List<String> lore = formatTicketDetails(ticket);
                skullMeta.setLore(lore);
                skullItem.setItemMeta(skullMeta);
            }

            inventory.setItem(i, skullItem);
        }

        staffMember.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the clicked inventory is the Ticket Management GUI
        if (event.getView().getTitle().equals(ChatColor.GRAY + "Ticket Management")) {
            event.setCancelled(true);  // Prevent any item movement

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD) {
                SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
                if (skullMeta != null) {
                    String ticketId = ChatColor.stripColor(skullMeta.getDisplayName()).replace("Ticket #", "");
                    player.sendMessage(ChatColor.GREEN + "You clicked on Ticket #" + ticketId);
                }
            }
        }
    }

    // Prevent dragging items in the GUI
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GRAY + "Ticket Management")) {
            event.setCancelled(true);
        }
    }

    public void updateTicketGUIForAllStaff() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("ticketmanager.staff")) {
                if (player.getOpenInventory().getTitle().equals(ChatColor.GRAY + "Ticket Management")) {
                    List<Ticket> tickets = plugin.getTicketManager().getTickets();
                    openTicketGUI(player, tickets);
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
