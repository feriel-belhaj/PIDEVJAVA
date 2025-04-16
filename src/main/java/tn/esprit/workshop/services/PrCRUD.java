package tn.esprit.workshop.services;

import java.sql.SQLException;
import java.util.List;

public interface PrCRUD<T> {

    void insert(T t) throws SQLException;
    void update(T t)  throws SQLException;
    void delete(T t) throws SQLException;
    List<T> showAll() throws SQLException;

}
