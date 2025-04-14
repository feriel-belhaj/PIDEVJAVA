package tn.esprit.workshop.models;

import java.time.LocalDateTime;

public class User {
    private int id;
    private int age;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String adresse;
    private String telephone;
    private LocalDateTime date_inscription;
    private String image;
    private String role;
    private String sexe;
    
    // Default constructor
    public User() {
    }
    
    // Simple constructor with basic info
    public User(int age, String nom, String prenom) {
        this.age = age;
        this.nom = nom;
        this.prenom = prenom;
    }
    
    // Constructor without ID (for new users)
    public User(String nom, String prenom, String email, String password, String adresse, 
               String telephone, LocalDateTime date_inscription, String image, String role, String sexe, int age) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.adresse = adresse;
        this.telephone = telephone;
        this.date_inscription = date_inscription;
        this.image = image;
        this.role = role;
        this.sexe = sexe;
        this.age = age;
    }
    
    // Full constructor
    public User(int id, String nom, String prenom, String email, String password, String adresse, 
               String telephone, LocalDateTime date_inscription, String image, String role, String sexe, int age) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.adresse = adresse;
        this.telephone = telephone;
        this.date_inscription = date_inscription;
        this.image = image;
        this.role = role;
        this.sexe = sexe;
        this.age = age;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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
    
    @Override
    public String toString() {
        return prenom + " " + nom + ", " + age + " ans";
    }
}
