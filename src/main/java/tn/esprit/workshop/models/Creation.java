package tn.esprit.workshop.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Creation {
    private int id;
    private int utilisateurId;
    private String titre;
    private String description;
    private String image;
    private String categorie;
    private LocalDateTime datePublic;
    private String statut;
    private int nbLike;
    
    // Constants for validation
    private static final int TITRE_MIN_LENGTH = 3;
    private static final int TITRE_MAX_LENGTH = 100;
    private static final int DESCRIPTION_MIN_LENGTH = 10;
    private static final int DESCRIPTION_MAX_LENGTH = 1000;
    private static final String[] VALID_CATEGORIES = {"Art", "Design", "Musique", "Photographie", "Vidéo", "Artisanat", "Littérature", "Autre", 
                                                     "Painting", "Digital Art", "Sculpture", "Photography", "Other"};
    private static final String[] VALID_STATUSES = {"public", "privé", "archivé", "actif", "inactif", "signalé"};
    
    // Default constructor
    public Creation() {
    }
    
    // Full constructor
    public Creation(int id, int utilisateurId, String titre, String description, 
                   String image, String categorie, LocalDateTime datePublic, 
                   String statut, int nbLike) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.titre = titre;
        this.description = description;
        this.image = image;
        this.categorie = categorie;
        this.datePublic = datePublic;
        this.statut = statut;
        this.nbLike = nbLike;
    }
    
    // Constructor without ID for new records
    public Creation(int utilisateurId, String titre, String description, 
                   String image, String categorie, LocalDateTime datePublic, 
                   String statut, int nbLike) {
        this.utilisateurId = utilisateurId;
        this.titre = titre;
        this.description = description;
        this.image = image;
        this.categorie = categorie;
        this.datePublic = datePublic;
        this.statut = statut;
        this.nbLike = nbLike;
    }
    
    // Validation method to check all fields
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        
        // Validate utilisateurId
        if (utilisateurId <= 0) {
            errors.add("L'ID utilisateur doit être un nombre positif");
        }
        
        // Validate titre
        if (titre == null || titre.trim().isEmpty()) {
            errors.add("Le titre ne peut pas être vide");
        } else if (titre.length() < TITRE_MIN_LENGTH) {
            errors.add("Le titre doit contenir au moins " + TITRE_MIN_LENGTH + " caractères");
        } else if (titre.length() > TITRE_MAX_LENGTH) {
            errors.add("Le titre ne peut pas dépasser " + TITRE_MAX_LENGTH + " caractères");
        }
        
        // Validate description
        if (description == null || description.trim().isEmpty()) {
            errors.add("La description ne peut pas être vide");
        } else if (description.length() < DESCRIPTION_MIN_LENGTH) {
            errors.add("La description doit contenir au moins " + DESCRIPTION_MIN_LENGTH + " caractères");
        } else if (description.length() > DESCRIPTION_MAX_LENGTH) {
            errors.add("La description ne peut pas dépasser " + DESCRIPTION_MAX_LENGTH + " caractères");
        }
        
        // Validate categorie
        if (categorie == null || categorie.trim().isEmpty()) {
            errors.add("La catégorie ne peut pas être vide");
        } else {
            boolean validCategory = false;
            for (String validCat : VALID_CATEGORIES) {
                if (validCat.equalsIgnoreCase(categorie.trim())) {
                    validCategory = true;
                    break;
                }
            }
            if (!validCategory) {
                errors.add("Catégorie invalide. Les catégories valides sont: " + String.join(", ", VALID_CATEGORIES));
            }
        }
        
        // Validate datePublic
        if (datePublic == null) {
            errors.add("La date de publication ne peut pas être vide");
        } else if (datePublic.isAfter(LocalDateTime.now())) {
            errors.add("La date de publication ne peut pas être dans le futur");
        }
        
        // Validate statut
        if (statut == null || statut.trim().isEmpty()) {
            errors.add("Le statut ne peut pas être vide");
        } else {
            boolean validStatus = false;
            for (String validStat : VALID_STATUSES) {
                if (validStat.equalsIgnoreCase(statut.trim())) {
                    validStatus = true;
                    break;
                }
            }
            if (!validStatus) {
                errors.add("Statut invalide. Les statuts valides sont: " + String.join(", ", VALID_STATUSES));
            }
        }
        
        // Validate nbLike
        if (nbLike < 0) {
            errors.add("Le nombre de likes ne peut pas être négatif");
        }
        
        return errors;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public LocalDateTime getDatePublic() {
        return datePublic;
    }

    public void setDatePublic(LocalDateTime datePublic) {
        this.datePublic = datePublic;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getNbLike() {
        return nbLike;
    }

    public void setNbLike(int nbLike) {
        this.nbLike = nbLike;
    }
    
    @Override
    public String toString() {
        return "Creation{" +
                "id=" + id +
                ", utilisateurId=" + utilisateurId +
                ", titre='" + titre + '\'' +
                ", categorie='" + categorie + '\'' +
                ", datePublic=" + datePublic +
                ", statut='" + statut + '\'' +
                ", nbLike=" + nbLike +
                '}';
    }
}