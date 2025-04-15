package tn.esprit.workshop.entities;

import java.time.LocalDateTime;

public class FormationReservee {
    private int id;
    private String status;
    private int formationId;
    private int utilisateurId;
    private String nom;
    private String prenom;
    private LocalDateTime dateReservation;

    public FormationReservee() {
    }

    public FormationReservee(int id, String status, int formationId, int utilisateurId, 
                           String nom, String prenom, LocalDateTime dateReservation) {
        this.id = id;
        this.status = status;
        this.formationId = formationId;
        this.utilisateurId = utilisateurId;
        this.nom = nom;
        this.prenom = prenom;
        this.dateReservation = dateReservation;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFormationId() {
        return formationId;
    }

    public void setFormationId(int formationId) {
        this.formationId = formationId;
    }

    public int getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }
} 