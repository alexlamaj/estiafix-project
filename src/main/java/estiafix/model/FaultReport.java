package estiafix.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class FaultReport {

    private String id;
    private final String reporter;
    private final String building;
    private final int room;
    private final Category category;
    private final String description;
    private boolean urgent;
    private boolean accessWithoutMe;
    private LocalTime accessFrom;
    private LocalTime accessTo;
    private String phone;
    private Status status = Status.SUBMITTED;
    private final LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private String technicianNote = "";
    private final Set<String> followers = new HashSet<>();

    public FaultReport(String reporter, String building, int room, Category category, String description, LocalDateTime createdAt) {
        this.reporter = reporter;
        this.building = building;
        this.room = room;
        this.category = category;
        this.description = description;
        this.createdAt = createdAt;
        this.lastUpdated = createdAt;
    }

    // GETTERS //
    public String getId() {
        return id;
    }

    public String getReporter() {
        return reporter;
    }

    public String getBuilding() {
        return building;
    }

    public int getRoom() {
        return room;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public boolean isAccessWithoutMe() {
        return accessWithoutMe;
    }

    public LocalTime getAccessFrom() {
        return accessFrom;
    }

    public LocalTime getAccessTo() {
        return accessTo;
    }

    public String getPhone() {
        return phone;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getTechnicianNote() {
        return technicianNote;
    }

    public Set<String> getFollowers() {
        return followers;
    }

    // SETTERS //
    public void setId(String id) {
        this.id = id;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public void setAccessWithoutMe(boolean accessWithoutMe) {
        this.accessWithoutMe = accessWithoutMe;
    }

    public void setAccessFrom(LocalTime accessFrom) {
        this.accessFrom = accessFrom;
    }

    public void setAccessTo(LocalTime accessTo) {
        this.accessTo = accessTo;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTechnicianNote(String technicianNote) {
        this.technicianNote = technicianNote;
    }

    // HELPERS //
    public String getCode() {
        return "#" + id;
    }

    public String getLocation() {
        return building + " - " + room;
    }

    public boolean isOpen() {
        return status.isOpen();
    }

    public boolean belongsTo(String username) {
        return reporter.equals(username) || followers.contains(username);
    }

    public void follow(String username) {
        followers.add(username);
    }

    public void changeStatus(Status newStatus, LocalDateTime when) {
        this.status = newStatus;
        this.lastUpdated = when;
    }

    public void updateNote(String note, LocalDateTime when) {
        this.technicianNote = note;
        this.lastUpdated = when;
    }

}
