package tn.esprit.workshop.services;

import tn.esprit.workshop.models.User;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements CRUD<User>{
    private Connection cnx;

    public ServiceUser(){
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void insert(User user) throws SQLException {
        String req = "INSERT INTO `user`(`nom`, `prenom`, `age`) " +
                "VALUES ('"+user.getNom()+"','"+user.getPrenom()+"','"+user.getAge()+"')";

        Statement st = cnx.createStatement();

        st.executeUpdate(req);

        System.out.println("Insertion OK!");
    }

    public void insert1(User user) throws SQLException {
        String req = "INSERT INTO `user`(`nom`, `prenom`, `age`) " +
                "VALUES (?, ?, ?)";

        PreparedStatement ps = cnx.prepareStatement(req);

        ps.setString(1, user.getNom());
        ps.setString(2, user.getPrenom());
        ps.setInt(3, user.getAge());

        ps.executeUpdate();

        System.out.println("Insertion OK!");

    }

    @Override
    public void update(User user) throws SQLException {

    }

    @Override
    public void delete(User user) throws SQLException {

    }

    @Override
    public List<User> showAll() throws SQLException {
        List<User> temp = new ArrayList<>();

        String req = "SELECT * FROM user";

        Statement st = cnx.createStatement();

        ResultSet rs = st.executeQuery(req);

        while (rs.next()){

            User u = new User();

            u.setId(rs.getInt(1));
            u.setNom(rs.getString("Nom"));
            u.setPrenom(rs.getString(3));
            u.setAge(rs.getInt("age"));

            temp.add(u);
        }


        return temp;
    }
}
