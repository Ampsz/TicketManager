package net.ticketmanager;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Date;

public class Ticket {

    private final int id;
    private final String creator;
    private final String message;
    private Date submissionDate;
    private String assignee;
    private String status;
    private final String world;
    private final double x, y, z;
    private final float yaw, pitch;

    public Ticket(int id, String creator, String message, String assignee, String status, Location location) {
        this.id = id;
        this.creator = creator;
        this.message = message;
        this.assignee = assignee;
        this.status = status;
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.submissionDate = new Date();
    }

    // Getters for the new fields
    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    // Other existing methods
    public int getId() {
        return id;
    }

    public String getCreator() {
        return creator;
    }

    public String getMessage() {
        return message;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
}
