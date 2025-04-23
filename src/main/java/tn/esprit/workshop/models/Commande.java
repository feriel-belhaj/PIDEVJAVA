package tn.esprit.workshop.models;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Commande {
    private int id;
    private double prix;
    private Date dateCmd;
    private String statut;
    private List<CommandeProduit> produits;

    public Commande() {
        this.produits = new ArrayList<>();
    }

    public Commande(double prix, Date dateCmd, String statut) {
        this.prix = prix;
        this.dateCmd = dateCmd;
        this.statut = statut;
    }

    public Commande(int id, double prix, Date dateCmd, String statut) {
        this.id = id;
        this.prix = prix;
        this.dateCmd = dateCmd;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Date getDateCmd() {
        return dateCmd;
    }

    public void setDateCmd(Date dateCmd) {
        this.dateCmd = dateCmd;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public List<CommandeProduit> getProduits() {
        return produits;
    }

    public void setProduits(List<CommandeProduit> produits) {
        this.produits = produits;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", prix=" + prix +
                ", dateCmd=" + dateCmd +
                ", statut='" + statut + '\'' +
                ", produits=" + produits +
                '}';
    }
}
