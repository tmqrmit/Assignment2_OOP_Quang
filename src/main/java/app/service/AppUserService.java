package app.service;

import app.model.AppUser;
import app.model.enums.Role;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

public class AppUserService {

    private final EntityManager entityManager;

    /**
     * Constructor for AppUserService.
     * Allows injecting an EntityManager directly for user management.
     *
     * @param entityManager the EntityManager used for AppUser management
     */
    public AppUserService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Save a new AppUser entity.
     *
     * Ensures that no duplicate username or personId exists before saving.
     *
     * @param appUser the AppUser to be saved
     * @throws IllegalArgumentException if a user with the same username or personId already exists
     */
    @Transactional
    public void saveUser(AppUser appUser) {
        EntityTransaction transaction = entityManager.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }

        TypedQuery<AppUser> usernameQuery = entityManager.createQuery(
                "SELECT u FROM AppUser u WHERE u.username = :username", AppUser.class);
        usernameQuery.setParameter("username", appUser.getUsername());

        TypedQuery<AppUser> personIdQuery = entityManager.createQuery(
                "SELECT u FROM AppUser u WHERE u.personId = :personId", AppUser.class);
        personIdQuery.setParameter("personId", appUser.getPersonId());

        if (!usernameQuery.getResultList().isEmpty()) {
            throw new IllegalArgumentException("A user with the given username already exists.");
        }
        if (!personIdQuery.getResultList().isEmpty()) {
            throw new IllegalArgumentException("A user with the given personId already exists.");
        }

        entityManager.persist(appUser);
        transaction.commit();
    }

    /**
     * Find an AppUser entity by its personId.
     *
     * @param personId the personId of the AppUser to retrieve
     * @return the AppUser, or null if not found
     */
    public AppUser findByPersonId(String personId) {
        TypedQuery<AppUser> query = entityManager.createQuery(
                "SELECT u FROM AppUser u WHERE u.personId = :personId", AppUser.class);
        query.setParameter("personId", personId);

        List<AppUser> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Retrieve all AppUser entities.
     *
     * @return a list of all AppUsers
     */
    public List<AppUser> findAllUsers() {
        TypedQuery<AppUser> query = entityManager.createQuery("SELECT u FROM AppUser u", AppUser.class);
        return query.getResultList();
    }

    /**
     * Update an existing AppUser entity.
     *
     * Ensures updated username or personId does not conflict with existing records.
     *
     * @param appUser the AppUser to update
     * @return the updated AppUser entity
     * @throws IllegalArgumentException if updated username or personId already exists in another record
     */
    @Transactional
    public AppUser updateUser(AppUser appUser) {
        AppUser existingUser = entityManager.find(AppUser.class, appUser.getPersonId());
        if (existingUser == null) {
            throw new IllegalArgumentException("User with ID " + appUser.getPersonId() + " does not exist.");
        }

        // Check for username conflicts
        TypedQuery<AppUser> usernameQuery = entityManager.createQuery(
                "SELECT u FROM AppUser u WHERE u.username = :username AND u.id <> :id", AppUser.class);
        usernameQuery.setParameter("username", appUser.getUsername());
        usernameQuery.setParameter("id", appUser.getPersonId());
        if (!usernameQuery.getResultList().isEmpty()) {
            throw new IllegalArgumentException("Username already in use by another user.");
        }

        // Check for personId conflicts
        TypedQuery<AppUser> personIdQuery = entityManager.createQuery(
                "SELECT u FROM AppUser u WHERE u.personId = :personId AND u.id <> :id", AppUser.class);
        personIdQuery.setParameter("personId", appUser.getPersonId());
        personIdQuery.setParameter("id", appUser.getPersonId());
        if (!personIdQuery.getResultList().isEmpty()) {
            throw new IllegalArgumentException("Person ID already in use by another user.");
        }

        existingUser.setPersonId(appUser.getPersonId());
        existingUser.setUsername(appUser.getUsername());
        existingUser.setPassword(appUser.getPassword());
        existingUser.setRole(appUser.getRole());
        return entityManager.merge(existingUser);
    }

    /**
     * Remove an AppUser entity by username.
     *
     * @param username the username of the AppUser to delete
     */
    @Transactional
    public void deleteUserByUsername(String username) {
        TypedQuery<AppUser> query = entityManager.createQuery(
                "SELECT u FROM AppUser u WHERE u.username = :username", AppUser.class);
        query.setParameter("username", username);

        List<AppUser> results = query.getResultList();
        if (results.isEmpty()) {
            throw new IllegalArgumentException("No user found with username: " + username);
        }

        AppUser userToDelete = results.get(0);
        entityManager.remove(userToDelete);
    }

    /**
     * Find an AppUser entity by its username.
     *
     * @param username the username of the AppUser to retrieve
     * @return the AppUser, or null if not found
     */
    public AppUser findByUsername(String username) {
        TypedQuery<AppUser> query = entityManager.createQuery(
                "SELECT u FROM AppUser u WHERE u.username = :username", AppUser.class);
        query.setParameter("username", username);

        List<AppUser> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }


    /**
     * Retrieve all users by their role.
     *
     * @param role the role of users to retrieve
     * @return a list of users with the specified role
     */
    public List<AppUser> findUsersByRole(Role role) {
        TypedQuery<AppUser> query = entityManager.createQuery(
                "SELECT u FROM AppUser u WHERE u.role = :role", AppUser.class);
        query.setParameter("role", role);

        return query.getResultList();
    }
}