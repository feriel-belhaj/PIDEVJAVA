package tn.esprit.workshop.tests;

import tn.esprit.workshop.models.User;
import tn.esprit.workshop.services.ServiceUser;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class MainClass {
    public static void main(String[] args) {
        MyDbConnexion c1 = MyDbConnexion.getInstance();

        ServiceUser us = new ServiceUser();

        User u1 = new User(

                "hh",
                "Anas",
                "anas.allem@example.com",
                "Tunis",
                "password123",
                "image.jpg",
                "client",
                "Homme",
                "12345678",
                LocalDateTime.now()
        );

        try {
            // Insertion de l'utilisateur
           us.insert(u1);

            System.out.println(us.showAll());

        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        }
        User userToUpdate = new User(

                "Allem",
                "Anas",
                "anas.allem@example.com",
                "Tunis",
                "newpassword123",
                "new_image.jpg",
                "client",
                "Homme",
                "98765432",
                LocalDateTime.now()
        );

//        try {
//            us.update(userToUpdate);
//            System.out.println("Utilisateur mis à jour avec succès!");
//        } catch (SQLException e) {
//            System.err.println("Erreur de mise à jour : " + e.getMessage());
//        }
        User userToDelete = new User();
        userToDelete.setId(24);

        try {
            us.delete(userToDelete);
            System.out.println("Utilisateur supprimé avec succès!");
        } catch (SQLException e) {
            System.err.println("Erreur de suppression : " + e.getMessage());
        }

    }
}
