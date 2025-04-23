package tn.esprit.workshop.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Partenariat {

    private int id;
    private Date dateDebut;
    private Date dateFin;
    private String statut;
    private String description;
    private String nom;
    private String type;
    private String image;

    // Liste des candidatures liées à ce partenariat (One-to-Many)
    private List<Candidature> candidatures = new ArrayList<>();

    public Partenariat() {}

    public Partenariat(Date dateDebut, Date dateFin, String statut, String description,
                       String nom, String type, String image) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.description = description;
        this.nom = nom;
        this.type = type;
        this.image = image;
    }

    public Partenariat(int id, Date dateDebut, Date dateFin, String statut, String description,
                       String nom, String type, String image) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.description = description;
        this.nom = nom;
        this.type = type;
        this.image = image;
    }

    // ✅ Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Candidature> getCandidatures() {
        return candidatures;
    }

    public void setCandidatures(List<Candidature> candidatures) {
        this.candidatures = candidatures;
    }

    // Méthode utilitaire pour ajouter une candidature
    public void addCandidature(Candidature candidature) {
        if (candidatures == null) {
            candidatures = new ArrayList<>();
        }
        candidatures.add(candidature);
        candidature.setPartenariat(this);
    }

    // Méthode utilitaire pour supprimer une candidature
    public void removeCandidature(Candidature candidature) {
        if (candidatures != null) {
            candidatures.remove(candidature);
            candidature.setPartenariat(null);
        }
    }

    @Override
    public String toString() {
        return "Partenariat{" +
                "id=" + id +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statut='" + statut + '\'' +
                ", description='" + description + '\'' +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", image='" + image + '\'' +
                ", candidaturesCount=" + (candidatures != null ? candidatures.size() : 0) +
                '}';
    }
}
