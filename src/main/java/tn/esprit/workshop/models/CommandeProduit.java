package tn.esprit.workshop.models;

public class CommandeProduit {
    private Produit produit;
    private int quantite;

    public CommandeProduit(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "CommandeProduit{" +
                "produit=" + produit +
                ", quantite=" + quantite +
                '}';
    }
}
