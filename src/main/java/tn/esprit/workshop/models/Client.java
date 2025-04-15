package tn.esprit.workshop.models;

import java.time.LocalDateTime;

public class Client extends User{
    public Client() {
    }

    public Client(String nom, String prenom, String email, String adresse, String password, String image, String role, String sexe, String telephone, LocalDateTime date_inscription) {
        super(nom, prenom, email, adresse, password, image, role, sexe, telephone, date_inscription);
    }
}
