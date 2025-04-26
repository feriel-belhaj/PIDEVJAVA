package tn.esprit.workshop.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
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
        String req = "INSERT INTO `utilisateur`(`nom`, `prenom`, `email`, `adresse`, `password`, `image`, `role`, `sexe`, `telephone`, `date_inscription`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

        PreparedStatement ps = cnx.prepareStatement(req);

        ps.setString(1, user.getNom());
        ps.setString(2, user.getPrenom());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getAdresse());
        ps.setString(5, user.getPassword());
        ps.setString(6, user.getImage());
        ps.setString(7, user.getRole());
        ps.setString(8, user.getSexe());
        ps.setString(9, user.getTelephone());
        ps.setTimestamp(10, Timestamp.valueOf(user.getDate_inscription()));

        ps.executeUpdate();

        System.out.println("Insertion OK!");
    }

//    public void insert1(User user) throws SQLException {
//        String req = "INSERT INTO `user`(`nom`, `prenom`, `age`) " +
//                "VALUES (?, ?, ?)";
//
//        PreparedStatement ps = cnx.prepareStatement(req);
//
//        ps.setString(1, user.getNom());
//        ps.setString(2, user.getPrenom());
//        ps.setInt(3, user.getAge());
//
//        ps.executeUpdate();
//
//        System.out.println("Insertion OK!");
//
//    }

    @Override
    public void update(User user) throws SQLException {
        String req = "UPDATE utilisateur SET " +
                "nom = ?, prenom = ?, email = ?, adresse = ?, password = ?, image = ?, role = ?, sexe = ?, telephone = ?, date_inscription = ? " +
                "WHERE id = ?";

        PreparedStatement ps = cnx.prepareStatement(req);

        ps.setString(1, user.getNom());
        ps.setString(2, user.getPrenom());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getAdresse());
        ps.setString(5, user.getPassword());
        ps.setString(6, user.getImage());
        ps.setString(7, user.getRole());
        ps.setString(8, user.getSexe());
        ps.setString(9, user.getTelephone());
        ps.setTimestamp(10, Timestamp.valueOf(user.getDate_inscription()));
        ps.setInt(11, user.getId());
        System.out.println(user.getId());

        // Exécuter la mise à jour
        ps.executeUpdate();

        System.out.println("Mise à jour effectuée avec succès!");
    }

    @Override
    public void delete(User user) throws SQLException {
        String req = "DELETE FROM utilisateur WHERE id = ?";

        PreparedStatement ps = cnx.prepareStatement(req);

        ps.setInt(1, user.getId());

        ps.executeUpdate();

    }

    @Override
    public ObservableList<User> showAll()  {
        ObservableList<User> temp = FXCollections.observableArrayList();

        String req = "SELECT * FROM utilisateur";
        try {
            Statement st = cnx.createStatement();

            ResultSet rs = st.executeQuery(req);

            while (rs.next()) {

                User u = new User();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setAdresse(rs.getString("adresse"));
                u.setPassword(rs.getString("password"));
                u.setImage(rs.getString("image"));
                u.setRole(rs.getString("role"));
                u.setSexe(rs.getString("sexe"));
                u.setTelephone(rs.getString("telephone"));
                u.setDate_inscription(rs.getTimestamp("date_inscription").toLocalDateTime());

                temp.add(u);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }


        return temp;
    }
    public User loginUser(String username, String password) throws SQLException {
        String req = "SELECT * FROM utilisateur WHERE email = ? AND password = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        User u = new User();
        if (rs.next()){
            u.setId(rs.getInt("id"));
            u.setNom(rs.getString("nom"));
            u.setPrenom(rs.getString("prenom"));
            u.setEmail(rs.getString("email"));
            u.setAdresse(rs.getString("adresse"));
            u.setPassword(rs.getString("password"));
            u.setImage(rs.getString("image"));
            u.setRole(rs.getString("role"));
            u.setSexe(rs.getString("sexe"));
            u.setTelephone(rs.getString("telephone"));
            u.setDate_inscription(rs.getTimestamp("date_inscription").toLocalDateTime());
        }
        else
            u=null;
        rs.close();
        ps.close();

        return u;
    }

    public User findByEmail(String email) throws SQLException {
        String req = "SELECT * FROM utilisateur WHERE email = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setNom(rs.getString("nom"));
            user.setPrenom(rs.getString("prenom"));
            user.setEmail(rs.getString("email"));
            user.setAdresse(rs.getString("adresse"));
            user.setPassword(rs.getString("password"));
            user.setImage(rs.getString("image"));
            user.setRole(rs.getString("role"));
            user.setSexe(rs.getString("sexe"));
            user.setTelephone(rs.getString("telephone"));
            user.setDate_inscription(rs.getTimestamp("date_inscription").toLocalDateTime());
            return user;
        }

        return null;
    }
    private ResultSet result;

    public int homeTotalUsers() {

        String sql = "SELECT COUNT(id) FROM utilisateur";

        int countData = 0;
        try {

            PreparedStatement ps = cnx.prepareStatement(sql);
            result = ps.executeQuery();

            while (result.next()) {
                countData = result.getInt("COUNT(id)");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return countData;

    }

    public int homeTotalWomen() {

        String sql = "SELECT COUNT(id) FROM utilisateur where sexe = ?";
        int countData = 0;
        try {

            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, "femelle");
            result = ps.executeQuery();

            while (result.next()) {
                countData = result.getInt("COUNT(id)");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return countData;

    }
    public int homeTotalMen() {

        String sql = "SELECT COUNT(id) FROM utilisateur where sexe = ?";
        int countData = 0;
        try {

            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, "male");
            result = ps.executeQuery();

            while (result.next()) {
                countData = result.getInt("COUNT(id)");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return countData;

    }
    public XYChart.Series<String, Number> homeChart() {
        XYChart.Series<String, Number> chart = new XYChart.Series<>();

        String sql = "SELECT date_inscription, COUNT(id) FROM utilisateur GROUP BY date_inscription ORDER BY TIMESTAMP(date_inscription) ASC LIMIT 7";

        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            result = ps.executeQuery();

            while (result.next()) {
                chart.getData().add(new XYChart.Data<>(result.getString(1), result.getInt(2)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return chart;
    }

    public User getUserByImage(String imageName) throws SQLException {
        String req = "SELECT * FROM utilisateur WHERE image = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, imageName);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setNom(rs.getString("nom"));
            user.setPrenom(rs.getString("prenom"));
            user.setEmail(rs.getString("email"));
            user.setAdresse(rs.getString("adresse"));
            user.setPassword(rs.getString("password"));
            user.setImage(rs.getString("image"));
            user.setRole(rs.getString("role"));
            user.setSexe(rs.getString("sexe"));
            user.setTelephone(rs.getString("telephone"));
            user.setDate_inscription(rs.getTimestamp("date_inscription").toLocalDateTime());
            return user;
        }

        return null;
    }

}
