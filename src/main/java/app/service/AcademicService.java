package app.service;

import app.model.Academic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.transaction.Transactional;

import java.util.List;

@Transactional
public class AcademicService {

    private final EntityManager entityManager;

    /**
     * Constructor for AcademicService.
     * Inject an EntityManager directly into the service.
     *
     * @param entityManager the EntityManager used for persistence operations
     */
    public AcademicService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Save an Academic entity to the database.
     *
     * Ensures that no duplicate `personId` exists before saving.
     *
     * @param academic the Academic to be saved
     * @throws IllegalArgumentException if an Academic with the same `personId` already exists
     */
    public void saveAcademic(Academic academic) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Academic existingAcademic = findByPersonId(academic.getPersonId());
            if (existingAcademic != null) {
                throw new IllegalArgumentException("An academic with personId " + academic.getPersonId() + " already exists.");
            }

            entityManager.persist(academic);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /**
     * Find an Academic entity by its personId.
     *
     * @param personId the personId of the Academic
     * @return the Academic, or null if not found
     */
    public Academic findByPersonId(String personId) {
        try {
            return entityManager.createQuery(
                    "SELECT a FROM Academic a WHERE a.personId = :personId", Academic.class
            ).setParameter("personId", personId).getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null; // Return null when no result is found
        }
    }

    /**
     * Retrieve all Academic entities from the database.
     *
     * @return a list of all Academic entities
     */
    public List<Academic> findAll() {
        return entityManager.createQuery("SELECT a FROM Academic a", Academic.class).getResultList();
    }

    /**
     * Find Academics filtered by their expertise.
     *
     * @param expertise the expertise of the Academics
     * @return a list of Academic entities with the given expertise
     */
    public List<Academic> findByExpertise(String expertise) {
        return entityManager.createQuery(
                "SELECT a FROM Academic a WHERE a.expertise = :expertise", Academic.class
        ).setParameter("expertise", expertise).getResultList();
    }

    /**
     * Delete an Academic entity by its personID.
     *
     * @param academic to remove
     */
    public void removeAcademic(Academic academic) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Academic existingAcademic = findByPersonId(academic.getPersonId());
            if (existingAcademic != null) {
                // Ensure the entity is managed before removal
                Academic managedAcademic = entityManager.contains(academic) ? academic : entityManager.merge(academic);
                entityManager.remove(managedAcademic);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /**
     * Update an existing Academic entity in the database.
     *
     *
     *
     *
     * @param academic the Academic with updated data
     * @return the updated Academic
     * @throws IllegalArgumentException if the updated `personId` already exists in another record
     */
    public Academic updateAcademic(Academic academic) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = false;

        try {
            // Start transaction only if none is active
            if (!transaction.isActive()) {
                transaction.begin();
                isNewTransaction = true;
            }

            // Merge changes
            Academic updatedAcademic = entityManager.merge(academic);

            // Commit only if this method started the transaction
            if (isNewTransaction) {
                transaction.commit();
            }

            return updatedAcademic;

        } catch (Exception e) {
            // Rollback only if this method started the transaction
            if (isNewTransaction && transaction.isActive()) {
                transaction.rollback();
            }
            throw e; // Re-throw for caller handling
        }
    }
}