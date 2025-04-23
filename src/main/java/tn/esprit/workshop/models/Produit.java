package tn.esprit.workshop.models;

import javafx.scene.control.Button;

import java.sql.Date;

public class Produit {
    private int id, quantiteStock;
    private String nom, description, image, categorie;
    private double prix;
    private Date dateCreation;

    private Button modifier;

    public Produit() {}

    public Produit(String nom, String description, double prix, int quantiteStock, String image, String categorie, Date dateCreation) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.image = image;
        this.categorie = categorie;
        this.dateCreation = dateCreation;
        this.modifier = new Button("Modifier");
    }

    public Produit(int id, String nom, String description, double prix, int quantiteStock, String image, String categorie, Date dateCreation) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.image = image;
        this.categorie = categorie;
        this.dateCreation = dateCreation;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantiteStock() {
        return quantiteStock;
    }

    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
    public Button getModifier() { return modifier; }
    public void setModifier(Button modifier) { this.modifier = modifier; }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", quantiteStock=" + quantiteStock +
                ", image='" + image + '\'' +
                ", categorie='" + categorie + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}