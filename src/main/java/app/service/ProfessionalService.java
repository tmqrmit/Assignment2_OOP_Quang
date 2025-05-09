package app.service;

import app.model.Academic;
import app.model.Professional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.transaction.Transactional;

import java.util.List;

@Transactional
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
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Professional existing = findByPersonId(professional.getPersonId());
            if (existing != null) {
                throw new IllegalArgumentException("A professional with personId " + professional.getPersonId() + " already exists.");
            }

            entityManager.persist(professional);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
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
     *
     */
    public void updateProfessional(Professional professional) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(professional);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /**
     * Deletes a Professional entity by its personId.
     *
     * @param  professional to delete
     */
    public void removeProfessional(Professional professional) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            if (professional != null) {
                entityManager.remove(professional);
            }
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}