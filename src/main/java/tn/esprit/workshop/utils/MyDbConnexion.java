package tn.esprit.workshop.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDbConnexion {

    //1ST STEP : Creer une variable static de meme type que la class
    private static MyDbConnexion instance;

    private static final String USER = "root";
    private static final String PWD = "";
    private static final String URL = "jdbc:mysql://localhost:3306/artizina8";

    private Connection cnx;

    //2ND STEP : Rendre le constructeur en mode private
    private MyDbConnexion(){
        try {
            cnx = DriverManager.getConnection(URL, USER, PWD);
            System.out.println("Connexion Etablie!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    //3RD STEP : Creer une methode qui retourne l'instance
    public static MyDbConnexion getInstance(){
        if (instance == null)
            instance = new MyDbConnexion();
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }
}
