package tn.esprit.workshop.services;

import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;

public interface CRUD<T> {

    void insert(T t) throws SQLException;
    void update(T t)  throws SQLException;
    void delete(T t) throws SQLException;
    ObservableList<T> showAll() throws SQLException;

}
