package tn.esprit.workshop.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Commentaire {
    private int id;
    private int creationId;
    private Integer utilisateurId;  // Can be null in the database
    private String contenu;
    private LocalDateTime dateComment;
    private String etat;
    
    // Constants for validation
    private static final int CONTENU_MIN_LENGTH = 2;
    private static final int CONTENU_MAX_LENGTH = 500;
    private static final String[] VALID_STATES = {"actif", "signalé", "inactif"};
    
    // Default constructor
    public Commentaire() {
    }
    
    // Full constructor
    public Commentaire(int id, int creationId, Integer utilisateurId, String contenu, 
                      LocalDateTime dateComment, String etat) {
        this.id = id;
        this.creationId = creationId;
        this.utilisateurId = utilisateurId;
        this.contenu = contenu;
        this.dateComment = dateComment;
        this.etat = etat;
    }
    
    // Constructor without ID for new records
    public Commentaire(int creationId, Integer utilisateurId, String contenu, 
                      LocalDateTime dateComment, String etat) {
        this.creationId = creationId;
        this.utilisateurId = utilisateurId;
        this.contenu = contenu;
        this.dateComment = dateComment;
        this.etat = etat;
    }
    
    // Validation method to check all fields
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        
        // Validate creationId
        if (creationId <= 0) {
            errors.add("L'ID de création doit être un nombre positif");
        }
        
        // Validate contenu
        if (contenu == null || contenu.trim().isEmpty()) {
            errors.add("Le contenu du commentaire ne peut pas être vide");
        } else if (contenu.length() < CONTENU_MIN_LENGTH) {
            errors.add("Le commentaire doit contenir au moins " + CONTENU_MIN_LENGTH + " caractères");
        } else if (contenu.length() > CONTENU_MAX_LENGTH) {
            errors.add("Le commentaire ne peut pas dépasser " + CONTENU_MAX_LENGTH + " caractères");
        }
        
        // Validate dateComment
        if (dateComment == null) {
            errors.add("La date du commentaire ne peut pas être vide");
        } else if (dateComment.isAfter(LocalDateTime.now())) {
            errors.add("La date du commentaire ne peut pas être dans le futur");
        }
        
        // Validate etat
        if (etat == null || etat.trim().isEmpty()) {
            errors.add("L'état du commentaire ne peut pas être vide");
        } else {
            boolean validState = false;
            for (String validEtat : VALID_STATES) {
                if (validEtat.equalsIgnoreCase(etat.trim())) {
                    validState = true;
                    break;
                }
            }
            if (!validState) {
                errors.add("État invalide. Les états valides sont: " + String.join(", ", VALID_STATES));
            }
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

    public int getCreationId() {
        return creationId;
    }

    public void setCreationId(int creationId) {
        this.creationId = creationId;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateComment() {
        return dateComment;
    }

    public void setDateComment(LocalDateTime dateComment) {
        this.dateComment = dateComment;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
    
    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", creationId=" + creationId +
                ", utilisateurId=" + utilisateurId +
                ", contenu='" + contenu + '\'' +
                ", dateComment=" + dateComment +
                ", etat='" + etat + '\'' +
                '}';
    }
}