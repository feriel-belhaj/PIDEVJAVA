package tn.esprit.workshop.services;

import org.springframework.stereotype.Service;
import tn.esprit.workshop.models.Formation;
import tn.esprit.workshop.models.User;
import tn.esprit.workshop.models.Utilisateur;
import tn.esprit.workshop.models.PredictionData;
import java.util.ArrayList;
import java.util.List;

@Service
public class PredictionService {
    
    public class PredictionResult {
        private double score;
        private List<String> recommendations;
        private List<String> risques;
        
        public PredictionResult(double score, List<String> recommendations, List<String> risques) {
            this.score = score;
            this.recommendations = recommendations;
            this.risques = risques;
        }
        
        // Getters
        public double getScore() { return score; }
        public List<String> getRecommendations() { return recommendations; }
        public List<String> getRisques() { return risques; }
    }
    
    public PredictionResult predictSuccessRate(Formation formation, User apprenant) {
        double score = 0.0;
        
        // Calculer le score de distance (exemple simple)
        double distanceScore = calculateDistanceScore(apprenant.getAdresse(), formation.getEmplacement());
        
        // Calculer le score de niveau (par défaut "Débutant" si non spécifié)
        String niveauApprenant = "Débutant"; // Niveau par défaut
        double niveauScore = calculateNiveauScore(niveauApprenant, formation.getNiveau());
        
        // Calculer le score final
        score = (distanceScore * 0.4) + (niveauScore * 0.6);
        
        return new PredictionResult(
            score,
            generateRecommendations(score),
            calculateRisques(score)
        );
    }
    
    private double calculateDistanceScore(String adresseApprenant, String lieuFormation) {
        // Simplification : si même ville = 1.0, sinon 0.5
        if (adresseApprenant != null && lieuFormation != null &&
            adresseApprenant.toLowerCase().contains(lieuFormation.toLowerCase())) {
            return 1.0;
        }
        return 0.5;
    }
    
    private double calculateNiveauScore(String niveauApprenant, String niveauFormation) {
        // Simplification : si même niveau = 1.0, si niveau inférieur = 0.5, si niveau supérieur = 0.3
        if (niveauApprenant.equals(niveauFormation)) {
            return 1.0;
        } else if (niveauApprenant.equals("Débutant") && niveauFormation.equals("Intermédiaire")) {
            return 0.5;
        } else if (niveauApprenant.equals("Intermédiaire") && niveauFormation.equals("Avancé")) {
            return 0.5;
        }
        return 0.3;
    }
    
    private List<String> generateRecommendations(double score) {
        List<String> recommendations = new ArrayList<>();
        
        if (score < 0.5) {
            recommendations.add("Cette formation pourrait être trop avancée pour vous");
            recommendations.add("Nous vous recommandons de commencer par une formation de niveau inférieur");
        } else if (score < 0.8) {
            recommendations.add("Vous avez un bon potentiel pour cette formation");
            recommendations.add("Prévoyez du temps supplémentaire pour les révisions");
        } else {
            recommendations.add("Cette formation correspond parfaitement à votre profil");
            recommendations.add("Vous devriez exceller dans cette formation");
        }
        
        return recommendations;
    }
    
    private List<String> calculateRisques(double score) {
        List<String> risques = new ArrayList<>();
        
        if (score < 0.5) {
            risques.add("Risque élevé de difficulté à suivre");
            risques.add("Possibilité de décrochage");
        } else if (score < 0.8) {
            risques.add("Quelques difficultés possibles");
            risques.add("Pourrait nécessiter un soutien supplémentaire");
        } else {
            risques.add("Risques minimes");
        }
        
        return risques;
    }
} 