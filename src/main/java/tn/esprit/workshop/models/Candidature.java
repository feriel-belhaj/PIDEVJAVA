package tn.esprit.workshop.models;

import java.util.Date;

public class Candidature {

    private int id;
    private Date datePostulation;
    private String cv;
    private String portfolio;
    private String motivation;
    private String typeCollab;
    private double scoreNlp;
    private double scoreArtistique;
    private String analysisResult;
    private Partenariat partenariat;

    public Candidature() {
        // Scores à 0.0 par défaut
        this.scoreNlp = 0.0;
        this.scoreArtistique = 0.0;
    }

    public Candidature(Date datePostulation, String cv, String portfolio, String motivation, String typeCollab, Partenariat partenariat) {
        this.datePostulation = datePostulation;
        this.cv = cv;
        this.portfolio = portfolio;
        this.motivation = motivation;
        this.typeCollab = typeCollab;
        this.partenariat = partenariat;
        this.scoreNlp = 0.0;
        this.scoreArtistique = 0.0;
    }
    
    // Nouveau constructeur pour correspondre à celui utilisé dans MainClass.java
    public Candidature(Date datePostulation, Partenariat partenariat, String cv, String portfolio, 
                      String motivation, String typeCollab, double scoreNlp, 
                      double scoreArtistique, String analysisResult) {
        this.datePostulation = datePostulation;
        this.partenariat = partenariat;
        this.cv = cv;
        this.portfolio = portfolio;
        this.motivation = motivation;
        this.typeCollab = typeCollab;
        this.scoreNlp = scoreNlp;
        this.scoreArtistique = scoreArtistique;
        this.analysisResult = analysisResult;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDatePostulation() {
        return datePostulation;
    }

    public void setDatePostulation(Date datePostulation) {
        this.datePostulation = datePostulation;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getTypeCollab() {
        return typeCollab;
    }

    public void setTypeCollab(String typeCollab) {
        this.typeCollab = typeCollab;
    }

    public double getScoreNlp() {
        return scoreNlp;
    }

    public void setScoreNlp(double scoreNlp) {
        this.scoreNlp = scoreNlp;
    }

    public double getScoreArtistique() {
        return scoreArtistique;
    }

    public void setScoreArtistique(double scoreArtistique) {
        this.scoreArtistique = scoreArtistique;
    }

    public String getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(String analysisResult) {
        this.analysisResult = analysisResult;
    }

    public Partenariat getPartenariat() {
        return partenariat;
    }

    public void setPartenariat(Partenariat partenariat) {
        this.partenariat = partenariat;
    }

    @Override
    public String toString() {
        return "Candidature{" +
                "id=" + id +
                ", datePostulation=" + datePostulation +
                ", cv='" + cv + '\'' +
                ", portfolio='" + portfolio + '\'' +
                ", motivation='" + motivation + '\'' +
                ", typeCollab='" + typeCollab + '\'' +
                ", scoreNlp=" + scoreNlp +
                ", scoreArtistique=" + scoreArtistique +
                ", analysisResult='" + analysisResult + '\'' +
                ", partenariat=" + (partenariat != null ? partenariat.getId() : "null") +
                '}';
    }
}
