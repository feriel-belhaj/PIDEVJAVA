package tn.esprit.workshop.models;
import java.util.Date;
public class Vente {
    private Date dateCommande;
    private int quantiteVendue;

    public Vente(Date dateCommande, int quantiteVendue) {
        this.dateCommande = dateCommande;
        this.quantiteVendue = quantiteVendue;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public int getQuantiteVendue() {
        return quantiteVendue;
    }
}
