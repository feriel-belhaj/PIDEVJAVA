package tn.esprit.workshop.models;

import java.time.LocalDateTime;

public class Evenement {
    private int id;
    private String titre;
    private String description;
    private LocalDateTime startdate;
    private LocalDateTime enddate;
    private String localisation;
    private double goalamount;
    private double collectedamount;
    private String status;
    private String imageurl;
    private LocalDateTime createdat;
    private String categorie;
    private int createur_id;
    
    // Default constructor
    public Evenement() {
    }
    
    // Constructor without ID (for new events)
    public Evenement(String titre, String description, LocalDateTime startdate, LocalDateTime enddate, 
                    String localisation, double goalamount, double collectedamount, String status, 
                    String imageurl, String categorie, int createur_id) {
        this.titre = titre;
        this.description = description;
        this.startdate = startdate;
        this.enddate = enddate;
        this.localisation = localisation;
        this.goalamount = goalamount;
        this.collectedamount = collectedamount;
        this.status = status;
        this.imageurl = imageurl;
        this.createdat = LocalDateTime.now();
        this.categorie = categorie;
        this.createur_id = createur_id;
    }
    
    // Full constructor
    public Evenement(int id, String titre, String description, LocalDateTime startdate, LocalDateTime enddate, 
                    String localisation, double goalamount, double collectedamount, String status, 
                    String imageurl, LocalDateTime createdat, String categorie, int createur_id) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.startdate = startdate;
        this.enddate = enddate;
        this.localisation = localisation;
        this.goalamount = goalamount;
        this.collectedamount = collectedamount;
        this.status = status;
        this.imageurl = imageurl;
        this.createdat = createdat;
        this.categorie = categorie;
        this.createur_id = createur_id;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartdate() {
        return startdate;
    }

    public void setStartdate(LocalDateTime startdate) {
        this.startdate = startdate;
    }

    public LocalDateTime getEnddate() {
        return enddate;
    }

    public void setEnddate(LocalDateTime enddate) {
        this.enddate = enddate;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public double getGoalamount() {
        return goalamount;
    }

    public void setGoalamount(double goalamount) {
        this.goalamount = goalamount;
    }

    public double getCollectedamount() {
        return collectedamount;
    }

    public void setCollectedamount(double collectedamount) {
        this.collectedamount = collectedamount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public int getCreateur_id() {
        return createur_id;
    }

    public void setCreateur_id(int createur_id) {
        this.createur_id = createur_id;
    }
    
    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", startdate=" + startdate +
                ", enddate=" + enddate +
                ", localisation='" + localisation + '\'' +
                ", goalamount=" + goalamount +
                ", collectedamount=" + collectedamount +
                ", status='" + status + '\'' +
                ", imageurl='" + imageurl + '\'' +
                ", createdat=" + createdat +
                ", categorie='" + categorie + '\'' +
                ", createur_id=" + createur_id +
                '}';
    }
} 