package tn.esprit.workshop.models;

import javax.persistence.*;

@Entity
public class PredictionData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Formation formation;
    
    @ManyToOne
    private User apprenant;
    
    private Double distanceLieuFormation;
    private Boolean disponibiliteHoraire;
    private Integer niveauInitial; // 1-5
    private Integer anneesExperience;
    private Double scoreCompatibilite;
    
    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public User getApprenant() {
        return apprenant;
    }

    public void setApprenant(User apprenant) {
        this.apprenant = apprenant;
    }

    public Double getDistanceLieuFormation() {
        return distanceLieuFormation;
    }

    public void setDistanceLieuFormation(Double distanceLieuFormation) {
        this.distanceLieuFormation = distanceLieuFormation;
    }

    public Boolean getDisponibiliteHoraire() {
        return disponibiliteHoraire;
    }

    public void setDisponibiliteHoraire(Boolean disponibiliteHoraire) {
        this.disponibiliteHoraire = disponibiliteHoraire;
    }

    public Integer getNiveauInitial() {
        return niveauInitial;
    }

    public void setNiveauInitial(Integer niveauInitial) {
        this.niveauInitial = niveauInitial;
    }

    public Integer getAnneesExperience() {
        return anneesExperience;
    }

    public void setAnneesExperience(Integer anneesExperience) {
        this.anneesExperience = anneesExperience;
    }

    public Double getScoreCompatibilite() {
        return scoreCompatibilite;
    }

    public void setScoreCompatibilite(Double scoreCompatibilite) {
        this.scoreCompatibilite = scoreCompatibilite;
    }
} 