package app.model;

import app.model.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "app_user") // Explicitly specify the table name to avoid conflicts
public class AppUser {

    @Column(nullable = false, unique = true)
    private String personId;

    @Id
    @Column(nullable = false, unique = true) // Ensure `username` is unique and non-null
    private String username;

    @Column(nullable = false) // Ensure `password` is non-null
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // Ensure `role` is non-null
    private Role role;

    // Default constructor
    public AppUser() {}

    // Constructor without ID (for creating a new user)
    public AppUser(String personId, String username, String password, Role role) {
        this.personId = personId;
        this.username = username;
        this.password = password;
        this.role = role;
    }


    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                ", personId='" + personId + '\'' +
                ", username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}