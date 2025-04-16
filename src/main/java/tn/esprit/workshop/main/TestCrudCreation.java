package tn.esprit.workshop.main;

import java.time.LocalDateTime;
import java.util.List;

import tn.esprit.workshop.entities.Creation;
import tn.esprit.workshop.services.CreationService;
import tn.esprit.workshop.services.UtilisateurService;

public class TestCrudCreation {

    public static void main(String[] args) {
        try {
            CreationService creationService = new CreationService();
            UtilisateurService utilisateurService = new UtilisateurService();
            
            // Get current test user
            int currentUserId = utilisateurService.getCurrentUser().getId();

            System.out.println("========== CREATION CRUD TESTING ==========");

            // Create first creation
            System.out.println("\n----- Creating First Creation -----");
            Creation creation1 = new Creation();
            creation1.setUtilisateurId(currentUserId);
            creation1.setTitre("Artwork Title 1");
            creation1.setDescription("This is a beautiful piece of artwork created for testing.");
            creation1.setImage("artwork1.jpg");
            creation1.setCategorie("Art"); // Changed to valid category from VALID_CATEGORIES array
            creation1.setDatePublic(LocalDateTime.now());
            creation1.setStatut("public"); // Changed to valid status from VALID_STATUSES array
            creation1.setNbLike(0);
            
            boolean added1 = creationService.add(creation1);
            if (added1) {
                System.out.println("Creation 1 created with ID: " + creation1.getId());
            } else {
                System.out.println("Failed to create Creation 1");
            }

            // Create second creation
            System.out.println("\n----- Creating Second Creation -----");
            Creation creation2 = new Creation();
            creation2.setUtilisateurId(currentUserId);
            creation2.setTitre("Artwork Title 2");
            creation2.setDescription("Another artwork created for testing purposes.");
            creation2.setImage("artwork2.jpg");
            creation2.setCategorie("Artisanat"); // Changed to valid category from VALID_CATEGORIES array
            creation2.setDatePublic(LocalDateTime.now());
            creation2.setStatut("public"); // Changed to valid status from VALID_STATUSES array
            creation2.setNbLike(0);
            
            boolean added2 = creationService.add(creation2);
            if (added2) {
                System.out.println("Creation 2 created with ID: " + creation2.getId());
            } else {
                System.out.println("Failed to create Creation 2");
            }

            // Verify all creations were added
            System.out.println("\n----- All Creations After Creation -----");
            List<Creation> allCreations = creationService.getAll();
            System.out.println("Total creations: " + allCreations.size());
            for (Creation creation : allCreations) {
                System.out.println("Creation: " + creation.getId() + " - " + creation.getTitre() + " - " + creation.getCategorie());
            }

            // Update first creation
            System.out.println("\n----- Updating First Creation -----");
            creation1.setTitre("Updated Artwork Title");
            creation1.setDescription("This description has been updated for testing.");
            creation1.setCategorie("Design"); // Changed to valid category from VALID_CATEGORIES array
            
            boolean updated = creationService.update(creation1);
            if (updated) {
                System.out.println("Creation 1 updated successfully");
            } else {
                System.out.println("Failed to update Creation 1");
            }

            // Increment likes for the first creation
            System.out.println("\n----- Incrementing Likes for First Creation -----");
            boolean likesIncremented = creationService.incrementLike(creation1.getId());
            if (likesIncremented) {
                System.out.println("Likes incremented for Creation 1");
            } else {
                System.out.println("Failed to increment likes for Creation 1");
            }

            // Get creations by current user
            System.out.println("\n----- Creations by Current User -----");
            List<Creation> userCreations = creationService.getByUserId(currentUserId);
            System.out.println("User has " + userCreations.size() + " creations:");
            for (Creation creation : userCreations) {
                System.out.println("Creation: " + creation.getId() + " - " + creation.getTitre() + " - " + creation.getCategorie());
            }

            // Get creations by category
            System.out.println("\n----- Creations by Category 'Design' -----");
            List<Creation> categoryCreations = creationService.getByCategorie("Design");
            System.out.println("Found " + categoryCreations.size() + " creations in category:");
            for (Creation creation : categoryCreations) {
                System.out.println("Creation: " + creation.getId() + " - " + creation.getTitre());
            }

            // Delete second creation
            System.out.println("\n----- Deleting Second Creation -----");
            boolean deleted = creationService.delete(creation2.getId());
            if (deleted) {
                System.out.println("Creation 2 deleted successfully");
            } else {
                System.out.println("Failed to delete Creation 2");
            }

            // Final verification
            System.out.println("\n----- Final Creations List -----");
            List<Creation> finalCreations = creationService.getAll();
            System.out.println("Total creations: " + finalCreations.size());
            for (Creation creation : finalCreations) {
                System.out.println("Creation: " + creation.getId() + " - " + creation.getTitre() + " - " + creation.getCategorie());
            }

            // Verify creation1 details
            Creation updatedCreation1 = creationService.getById(creation1.getId());
            if (updatedCreation1 != null) {
                System.out.println("\nCreation 1 details after update:");
                System.out.println("Title: " + updatedCreation1.getTitre());
                System.out.println("Description: " + updatedCreation1.getDescription());
                System.out.println("Category: " + updatedCreation1.getCategorie());
                System.out.println("Likes: " + updatedCreation1.getNbLike());
            }

            // Verify creation2 no longer exists
            Creation deletedCreation2 = creationService.getById(creation2.getId());
            if (deletedCreation2 == null) {
                System.out.println("\nCreation 2 was successfully deleted");
            } else {
                System.out.println("\nCreation 2 was not deleted correctly");
            }

            System.out.println("\n========== CREATION CRUD TESTING COMPLETED ==========");

        } catch (Exception e) {
            System.err.println("Error occurred during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}