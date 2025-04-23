package tn.esprit.workshop.services;

import tn.esprit.workshop.models.Evenement;

import java.util.List;

public interface IEvenementService {
    // CRUD operations
    void ajouter(Evenement evenement);
    void modifier(Evenement evenement);
    void supprimer(int id);
    Evenement getById(int id);
    List<Evenement> getAll();
    
    // Additional specific operations
    List<Evenement> getByStatus(String status);
    List<Evenement> getByCategorie(String categorie);
    List<Evenement> getByCreateurId(int createurId);
    void updateCollectedAmount(int evenementId, double newAmount);
} 