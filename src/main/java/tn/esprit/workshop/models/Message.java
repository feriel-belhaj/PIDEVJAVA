package tn.esprit.workshop.models;

public class Message {
    private String date;
    private String name;
    private String email;
    private String message;
    private boolean isRead;

    public Message(String date, String name, String email, String message) {
        this.date = date;
        this.name = name;
        this.email = email;
        this.message = message;
        this.isRead = false; // Par défaut, non lu
    }

    public Message(String date, String name, String email, String message, boolean isRead) {
        this.date = date;
        this.name = name;
        this.email = email;
        this.message = message;
        this.isRead = isRead;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getTime() {
        if (date != null && date.length() >= 16) {
            return date.substring(11, 16); // HH:mm
        }
        return "";
    }

    @Override
    public String toString() {
        return name + " (" + getTime() + ")";
    }
}