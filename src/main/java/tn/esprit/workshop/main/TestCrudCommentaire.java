package tn.esprit.workshop.main;

import java.time.LocalDateTime;
import java.util.List;

import tn.esprit.workshop.entities.Commentaire;
import tn.esprit.workshop.entities.Creation;
import tn.esprit.workshop.services.CommentaireService;
import tn.esprit.workshop.services.CreationService;
import tn.esprit.workshop.services.UtilisateurService;

public class TestCrudCommentaire {

    public static void main(String[] args) {
        try {
            CommentaireService commentaireService = new CommentaireService();
            CreationService creationService = new CreationService();
            UtilisateurService utilisateurService = new UtilisateurService();
            
            // Get current test user
            int currentUserId = utilisateurService.getCurrentUser().getId();
            
            // Get a valid creation to comment on
            List<Creation> creations = creationService.getAll();
            if (creations.isEmpty()) {
                System.out.println("No creations found to comment on. Please create some creations first.");
                return;
            }
            int creationId = creations.get(0).getId();

            System.out.println("========== COMMENTAIRE CRUD TESTING ==========");

            // Create first comment
            System.out.println("\n----- Creating First Comment -----");
            Commentaire comment1 = new Commentaire();
            comment1.setCreationId(creationId);
            comment1.setUtilisateurId(currentUserId);
            comment1.setContenu("This is a test comment for validation. Great work on this creation!");
            comment1.setDateComment(LocalDateTime.now());
            comment1.setEtat("actif"); // Using valid state from VALID_STATES array
            
            boolean added1 = commentaireService.add(comment1);
            if (added1) {
                System.out.println("Comment 1 created with ID: " + comment1.getId());
            } else {
                System.out.println("Failed to create Comment 1");
            }

            // Create second comment (anonymous)
            System.out.println("\n----- Creating Second Anonymous Comment -----");
            Commentaire comment2 = new Commentaire();
            comment2.setCreationId(creationId);
            comment2.setUtilisateurId(null); // Anonymous comment
            comment2.setContenu("An anonymous comment testing the validation system. Looking great!");
            comment2.setDateComment(LocalDateTime.now());
            comment2.setEtat("actif"); // Using valid state from VALID_STATES array
            
            boolean added2 = commentaireService.add(comment2);
            if (added2) {
                System.out.println("Anonymous Comment 2 created with ID: " + comment2.getId());
            } else {
                System.out.println("Failed to create Anonymous Comment 2");
            }

            // Verify all comments for this creation
            System.out.println("\n----- All Comments for Creation ID: " + creationId + " -----");
            List<Commentaire> creationComments = commentaireService.getByCreationId(creationId);
            System.out.println("Total comments: " + creationComments.size());
            for (Commentaire comment : creationComments) {
                String username = comment.getUtilisateurId() == null ? "Anonymous" : 
                                 "User " + comment.getUtilisateurId();
                System.out.println("Comment: " + comment.getId() + " - " + username + " - " + comment.getContenu());
            }

            // Update first comment
            System.out.println("\n----- Updating First Comment -----");
            comment1.setContenu("This comment has been updated for testing validation.");
            comment1.setEtat("signalé"); // Changed to another valid state
            
            boolean updated = commentaireService.update(comment1);
            if (updated) {
                System.out.println("Comment 1 updated successfully");
            } else {
                System.out.println("Failed to update Comment 1");
            }

            // Test validation with invalid data
            System.out.println("\n----- Testing Invalid Comment Data -----");
            Commentaire invalidComment = new Commentaire();
            invalidComment.setCreationId(creationId);
            invalidComment.setUtilisateurId(currentUserId);
            invalidComment.setContenu(""); // Empty content should fail validation
            invalidComment.setDateComment(LocalDateTime.now().plusDays(1)); // Future date should fail
            invalidComment.setEtat("unknown"); // Invalid state should fail
            
            boolean addedInvalid = commentaireService.add(invalidComment);
            if (addedInvalid) {
                System.out.println("Invalid comment was incorrectly added with ID: " + invalidComment.getId());
            } else {
                System.out.println("Invalid comment was correctly rejected");
            }

            // Update comment status
            System.out.println("\n----- Updating Comment Status -----");
            boolean statusUpdated = commentaireService.updateStatus(comment1.getId(), "inactif");
            if (statusUpdated) {
                System.out.println("Comment 1 status updated to 'inactif'");
            } else {
                System.out.println("Failed to update Comment 1 status");
            }

            // Delete second comment
            System.out.println("\n----- Deleting Second Comment -----");
            boolean deleted = commentaireService.delete(comment2.getId());
            if (deleted) {
                System.out.println("Comment 2 deleted successfully");
            } else {
                System.out.println("Failed to delete Comment 2");
            }

            // Final verification
            System.out.println("\n----- Final Comments List -----");
            List<Commentaire> finalComments = commentaireService.getByCreationId(creationId);
            System.out.println("Total comments for creation: " + finalComments.size());
            for (Commentaire comment : finalComments) {
                String username = comment.getUtilisateurId() == null ? "Anonymous" : 
                                 "User " + comment.getUtilisateurId();
                System.out.println("Comment: " + comment.getId() + " - " + username + " - " + comment.getContenu() + " - " + comment.getEtat());
            }

            // Verify comment2 no longer exists
            Commentaire deletedComment2 = commentaireService.getById(comment2.getId());
            if (deletedComment2 == null) {
                System.out.println("\nComment 2 was successfully deleted");
            } else {
                System.out.println("\nComment 2 was not deleted correctly");
            }

            System.out.println("\n========== COMMENTAIRE CRUD TESTING COMPLETED ==========");

        } catch (Exception e) {
            System.err.println("Error occurred during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}