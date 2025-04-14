package tn.esprit.workshop.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tn.esprit.workshop.models.User;
import tn.esprit.workshop.services.CRUD;
import tn.esprit.workshop.services.ServiceUser;

import java.sql.SQLException;

public class Personne {

    @FXML
    private TextField tfAge;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfPrenom;
    @FXML
    private Label lbShow;
    CRUD<User> u = new ServiceUser();
    User user = new User();


    @FXML
    void ajouterPersonne(ActionEvent event) {
        user.setNom(tfNom.getText());
        user.setPrenom(tfPrenom.getText());
        user.setAge(Integer.parseInt(tfAge.getText()));
        try {
            u.insert(user);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    @FXML
    void afficherUser(ActionEvent event) {
        try {
           String s = u.showAll().toString();
            lbShow.setText(s);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
