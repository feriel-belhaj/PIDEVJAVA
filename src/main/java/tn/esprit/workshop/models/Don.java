package tn.esprit.workshop.models;

import java.time.LocalDateTime;

public class Don {
    private int id;
    private int evenement_id;
    private LocalDateTime donationdate;
    private double amount;
    private String paymentref;
    private String message;
    private int createur_id;
    private int user_id;
    
    // Default constructor
    public Don() {
    }
    
    // Constructor without ID (for new donations)
    public Don(int evenement_id, LocalDateTime donationdate, double amount, String paymentref, 
              String message, int createur_id, int user_id) {
        this.evenement_id = evenement_id;
        this.donationdate = donationdate;
        this.amount = amount;
        this.paymentref = paymentref;
        this.message = message;
        this.createur_id = createur_id;
        this.user_id = user_id;
    }
    
    // Full constructor
    public Don(int id, int evenement_id, LocalDateTime donationdate, double amount, String paymentref, 
              String message, int createur_id, int user_id) {
        this.id = id;
        this.evenement_id = evenement_id;
        this.donationdate = donationdate;
        this.amount = amount;
        this.paymentref = paymentref;
        this.message = message;
        this.createur_id = createur_id;
        this.user_id = user_id;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEvenement_id() {
        return evenement_id;
    }

    public void setEvenement_id(int evenement_id) {
        this.evenement_id = evenement_id;
    }

    public LocalDateTime getDonationdate() {
        return donationdate;
    }

    public void setDonationdate(LocalDateTime donationdate) {
        this.donationdate = donationdate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentref() {
        return paymentref;
    }

    public void setPaymentref(String paymentref) {
        this.paymentref = paymentref;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCreateur_id() {
        return createur_id;
    }

    public void setCreateur_id(int createur_id) {
        this.createur_id = createur_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    
    @Override
    public String toString() {
        return "Don{" +
                "id=" + id +
                ", evenement_id=" + evenement_id +
                ", donationdate=" + donationdate +
                ", amount=" + amount +
                ", paymentref='" + paymentref + '\'' +
                ", message='" + message + '\'' +
                ", createur_id=" + createur_id +
                ", user_id=" + user_id +
                '}';
    }
} 