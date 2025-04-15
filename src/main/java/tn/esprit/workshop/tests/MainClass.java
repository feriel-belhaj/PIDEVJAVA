package tn.esprit.workshop.tests;

import tn.esprit.workshop.models.Candidature;
import tn.esprit.workshop.models.Partenariat;
import tn.esprit.workshop.services.ServiceCandidature;
import tn.esprit.workshop.services.ServicePartenariat;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        try {
            // Initialiser les services
            ServicePartenariat servicePartenariat = new ServicePartenariat();
            ServiceCandidature serviceCandidature = new ServiceCandidature();
            Scanner scanner = new Scanner(System.in);

            // 1. Créer un partenariat de test
            System.out.println("\n=== Création d'un partenariat de test ===");
            Date dateDebut = new Date();
            Date dateFin = new Date(dateDebut.getTime() + (30L * 24 * 60 * 60 * 1000)); // 30 jours plus tard
            Partenariat partenariat = new Partenariat(
                    dateDebut,
                    dateFin,
                    "feriel",
                    "Description du partenariat test",
                    "Partenariat Test",
                    "Type Test",
                    "image_test111.jpg"
            );
            servicePartenariat.insert(partenariat);
            System.out.println("Partenariat créé avec l'ID: " + partenariat.getId());

            // 2. Créer une candidature
            System.out.println("\n=== Création d'une candidature ===");
            Candidature candidature = new Candidature(
                    new Date(),
                    partenariat,
                    "feriel",
                    "portfolio_test.pdf",
                    "Lettre de motivation test",
                    "Collaboration Test",
                    85.5,
                    90.0,
                    "Analyse des résultats test"
            );
            serviceCandidature.insert(candidature);
            System.out.println("Candidature créée avec l'ID: " + candidature.getId());

            // 3. Afficher toutes les candidatures
            System.out.println("\n=== Liste de toutes les candidatures ===");
            List<Candidature> candidatures = serviceCandidature.showAll();
            for (Candidature c : candidatures) {
                System.out.println(c);
            }

            // 4. Afficher tous les partenariats
            System.out.println("\n=== Liste de tous les partenariats ===");
            List<Partenariat> partenariats = servicePartenariat.showAll();
            for (Partenariat p : partenariats) {
                System.out.println(p);
            }

            // 5. Sélectionner un partenariat et modifier
            System.out.print("\nEntrez l'ID du partenariat à modifier: ");
            int partenariatId = scanner.nextInt();
            Partenariat partenariatAModifier = servicePartenariat.findById(partenariatId);
            if (partenariatAModifier != null) {
                System.out.println("Partenariat trouvé: " + partenariatAModifier);
                // Modifier tous les attributs du partenariat
                Date nouvelleDateDebut = new Date();
                Date nouvelleDateFin = new Date(nouvelleDateDebut.getTime() + (60L * 24 * 60 * 60 * 1000)); // 60 jours plus tard
                partenariatAModifier.setDateDebut(nouvelleDateDebut);
                partenariatAModifier.setDateFin(nouvelleDateFin);
                partenariatAModifier.setStatut("En cours");
                partenariatAModifier.setDescription("Nouvelle description du partenariat");
                partenariatAModifier.setNom("Nouveau nom du partenariat");
                partenariatAModifier.setType("Nouveau type de partenariat");
                partenariatAModifier.setImage("nouvelle_image.jpg");
                servicePartenariat.update(partenariatAModifier);
                System.out.println("Partenariat mis à jour: " + partenariatAModifier);
            } else {
                System.out.println("Partenariat non trouvé.");
            }

            // 6. Sélectionner une candidature et modifier
            System.out.print("\nEntrez l'ID de la candidature à modifier: ");
            int candidatureId = scanner.nextInt();
            Candidature candidatureAModifier = serviceCandidature.findById(candidatureId);
            if (candidatureAModifier != null) {
                System.out.println("Candidature trouvée: " + candidatureAModifier);
                // Modifier tous les attributs de la candidature
                candidatureAModifier.setDatePostulation(new Date());
                candidatureAModifier.setCv("nouveau_cv.pdf");
                candidatureAModifier.setPortfolio("ferfer");
                candidatureAModifier.setMotivation("Nouvelle lettre de motivation");
                candidatureAModifier.setTypeCollab("Nouveau type de collaboration");
                candidatureAModifier.setScoreNlp(98.5);
                candidatureAModifier.setScoreArtistique(97.0);
                candidatureAModifier.setAnalysisResult("Nouvelle analyse des résultats");
                serviceCandidature.update(candidatureAModifier);
                System.out.println("Candidature mise à jour: " + candidatureAModifier);
            } else {
                System.out.println("Candidature non trouvée.");
            }

            // 7. Sélectionner un partenariat et supprimer
            System.out.print("\n33 ");
            partenariatId = scanner.nextInt();
            Partenariat partenariatASupprimer = servicePartenariat.findById(partenariatId);
            if (partenariatASupprimer != null) {
                servicePartenariat.delete(partenariatASupprimer);
                System.out.println("Partenariat supprimé.");
            } else {
                System.out.println("Partenariat non trouvé.");
            }

            // 8. Sélectionner une candidature et supprimer
            System.out.print("\n 63 ");
            candidatureId = scanner.nextInt();
            Candidature candidatureASupprimer = serviceCandidature.findById(candidatureId);
            if (candidatureASupprimer != null) {
                serviceCandidature.delete(candidatureASupprimer);
                System.out.println("Candidature supprimée.");
            } else {
                System.out.println("Candidature non trouvée.");
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
