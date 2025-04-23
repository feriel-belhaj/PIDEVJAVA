package tn.esprit.workshop.services;

import java.sql.SQLException;
import java.util.List;

public interface CRUDCandidature<C> {

    void insert(C c) throws SQLException;
    void update(C c)  throws SQLException;
    void delete(C c) throws SQLException;
    List<C> showAll() throws SQLException;

}
