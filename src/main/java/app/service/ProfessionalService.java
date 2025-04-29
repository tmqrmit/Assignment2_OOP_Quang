package app.service;

import app.model.Professional;

import jakarta.persistence.EntityManager;
import java.util.List;

public class ProfessionalService {

    private final EntityManager entityManager;

    /**
     * Constructor for ProfessionalService.
     * Injects an EntityManager to handle persistence.
     *
     * @param entityManager the EntityManager instance
     */
    public ProfessionalService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Find a Professional entity by its personId.
     *
     * @param personId the personId of the Professional
     * @return the Professional object, or null if not found
     */
    public Professional findByPersonId(String personId) {
        try {
            return entityManager.createQuery(
                    "SELECT p FROM Professional p WHERE p.personId = :personId", Professional.class
            ).setParameter("personId", personId).getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null; // Return null when no result is found
        }
    }

    /**
     * Saves a new Professional entity to the database.
     *
     * @param professional the Professional to save
     */
    public void saveProfessional(Professional professional) {
        entityManager.persist(professional);
    }

    /**
     * Retrieves all Professional entities from the database.
     *
     * @return a list of all Professional objects
     */
    public List<Professional> findAll() {
        return entityManager.createQuery("SELECT p FROM Professional p", Professional.class).getResultList();
    }

    /**
     * Updates an existing Professional entity in the database.
     *
     * @param professional the Professional with updated data
     * @return the updated Professional object
     */
    public Professional updateProfessional(Professional professional) {
        return entityManager.merge(professional);
    }

    /**
     * Deletes a Professional entity by its personId.
     *
     * @param personId the personId of the Professional to delete
     */
    public void deleteById(String personId) {
        Professional professional = findByPersonId(personId);
        if (professional != null) {
            entityManager.remove(professional);
        }
    }
}