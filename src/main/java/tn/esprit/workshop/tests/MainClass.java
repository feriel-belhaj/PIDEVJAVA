package tn.esprit.workshop.tests;

import tn.esprit.workshop.models.User;
import tn.esprit.workshop.services.ServiceUser;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.SQLException;

public class MainClass {
    public static void main(String[] args) {
        MyDbConnexion c1 = MyDbConnexion.getInstance();

        ServiceUser us = new ServiceUser();

        User u1 = new User(22, "Allem", "Anas");

        try {
            //us.insert(u1);
            System.out.println(us.showAll());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }
}
