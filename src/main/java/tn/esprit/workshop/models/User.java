package tn.esprit.workshop.models;

import java.time.LocalDateTime;

public class User {

    private int id;
    private String nom, prenom,email,password,adresse,image,role,sexe,telephone;
    private LocalDateTime date_inscription;

    public User() {}

    public User(String nom, String prenom, String email, String adresse, String password, String image, String role, String sexe, String telephone, LocalDateTime date_inscription) {

        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adresse = adresse;
        this.password = password;
        this.image = image;
        this.role = role;
        this.sexe = sexe;
        this.telephone = telephone;
        this.date_inscription = date_inscription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDateTime getDate_inscription() {
        return date_inscription;
    }

    public void setDate_inscription(LocalDateTime date_inscription) {
        this.date_inscription = date_inscription;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", adresse='" + adresse + '\'' +
                ", image='" + image + '\'' +
                ", role='" + role + '\'' +
                ", sexe='" + sexe + '\'' +
                ", telephone='" + telephone + '\'' +
                ", date_inscription=" + date_inscription +
                '}';
    }
}
