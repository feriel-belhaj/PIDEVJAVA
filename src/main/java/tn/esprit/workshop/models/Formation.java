package tn.esprit.workshop.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Formation {
    private int id;
    private String titre;
    private String description;
    private LocalDate dateDeb;
    private LocalDate dateFin;
    private String niveau;
    private float prix;
    private String emplacement;
    private int nbPlace;
    private int nbParticipant;
    private String organisateur;
    private String duree;
    private String image;
    private List<Certificat> certificats;

    // Constructeur par défaut
    public Formation() {
        this.certificats = new ArrayList<>();
    }

    // Constructeur avec paramètres
    public Formation(String titre, String description, LocalDate dateDeb, LocalDate dateFin, 
                    String niveau, float prix, String emplacement, int nbPlace, 
                    int nbParticipant, String organisateur, String duree, String image) {
        this.titre = titre;
        this.description = description;
        this.dateDeb = dateDeb;
        this.dateFin = dateFin;
        this.niveau = niveau;
        this.prix = prix;
        this.emplacement = emplacement;
        this.nbPlace = nbPlace;
        this.nbParticipant = nbParticipant;
        this.organisateur = organisateur;
        this.duree = duree;
        this.image = image;
        this.certificats = new ArrayList<>();
    }

    // Getters
    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public LocalDate getDateDeb() { return dateDeb; }
    public LocalDate getDateFin() { return dateFin; }
    public String getNiveau() { return niveau; }
    public float getPrix() { return prix; }
    public String getEmplacement() { return emplacement; }
    public int getNbPlace() { return nbPlace; }
    public int getNbParticipant() { return nbParticipant; }
    public String getOrganisateur() { return organisateur; }
    public String getDuree() { return duree; }
    public String getImage() { return image; }
    public List<Certificat> getCertificats() { return certificats; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setDateDeb(LocalDate dateDeb) { this.dateDeb = dateDeb; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public void setPrix(float prix) { this.prix = prix; }
    public void setEmplacement(String emplacement) { this.emplacement = emplacement; }
    public void setNbPlace(int nbPlace) { this.nbPlace = nbPlace; }
    public void setNbParticipant(int nbParticipant) { this.nbParticipant = nbParticipant; }
    public void setOrganisateur(String organisateur) { this.organisateur = organisateur; }
    public void setDuree(String duree) { this.duree = duree; }
    public void setImage(String image) { this.image = image; }
    public void setCertificats(List<Certificat> certificats) { this.certificats = certificats; }

    // Méthodes pour gérer les certificats
    public void addCertificat(Certificat certificat) {
        this.certificats.add(certificat);
    }

    public void removeCertificat(Certificat certificat) {
        this.certificats.remove(certificat);
    }

    @Override
    public String toString() {
        return "Formation{" +
                "titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", dateDeb=" + dateDeb +
                ", dateFin=" + dateFin +
                ", niveau='" + niveau + '\'' +
                ", prix=" + prix +
                ", emplacement='" + emplacement + '\'' +
                ", nbPlace=" + nbPlace +
                ", nbParticipant=" + nbParticipant +
                ", organisateur='" + organisateur + '\'' +
                ", duree='" + duree + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}