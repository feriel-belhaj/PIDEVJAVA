package tn.esprit.workshop.services;

import tn.esprit.workshop.models.Don;

import java.util.List;

public interface IDonService {
    // CRUD operations
    void ajouter(Don don);
    void modifier(Don don);
    void supprimer(int id);
    Don getById(int id);
    List<Don> getAll();
    
    // Additional specific operations
    List<Don> getByEvenementId(int evenementId);
    List<Don> getByUserId(int userId);
    double getTotalDonationsByEvenementId(int evenementId);
} 