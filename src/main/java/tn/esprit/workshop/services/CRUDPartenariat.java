package tn.esprit.workshop.services;

import java.sql.SQLException;
import java.util.List;

public interface CRUDPartenariat<P> {

    void insert(P p) throws SQLException;
    void update(P p)  throws SQLException;
    void delete(P p) throws SQLException;
    List<P> showAll() throws SQLException;

}
