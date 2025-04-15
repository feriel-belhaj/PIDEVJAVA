package tn.esprit.workshop.entities;

import java.time.LocalDate;

public class Certificat {
    private int id;
    private int formationId;
    private String nom;
    private String prenom;
    private LocalDate dateobt;
    private String niveau;
    private String nomorganisme;
    private String formationTitre;
    private Formation formation;

    public Certificat() {
    }

    public Certificat(int formationId, String nom, String prenom, LocalDate dateobt, 
                     String niveau, String nomorganisme) {
        this.formationId = formationId;
        this.nom = nom;
        this.prenom = prenom;
        this.dateobt = dateobt;
        this.niveau = niveau;
        this.nomorganisme = nomorganisme;
    }

    // Getters
    public int getId() { return id; }
    public int getFormationId() { return formationId; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public LocalDate getDateobt() { return dateobt; }
    public String getNiveau() { return niveau; }
    public String getNomorganisme() { return nomorganisme; }
    public String getFormationTitre() { return formationTitre; }
    public Formation getFormation() { return formation; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setFormationId(int formationId) { this.formationId = formationId; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setDateobt(LocalDate dateobt) { this.dateobt = dateobt; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public void setNomorganisme(String nomorganisme) { this.nomorganisme = nomorganisme; }
    public void setFormationTitre(String formationTitre) { this.formationTitre = formationTitre; }
    public void setFormation(Formation formation) { this.formation = formation; }

    @Override
    public String toString() {
        return "Certificat{" +
                "id=" + id +
                ", formationId=" + formationId +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", dateobt=" + dateobt +
                ", niveau='" + niveau + '\'' +
                ", nomorganisme='" + nomorganisme + '\'' +
                '}';
    }
} 