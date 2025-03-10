package com.library.book_storage_service.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.library.book_storage_service.models.enums.Role;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;
    @Column(nullable = false, unique = true)
    @JsonProperty("username")
    private String username;
    @Column(nullable = false)
    @JsonProperty("password")
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonProperty("role")
    private Role role;

    public User(Long id, String username, String password,Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    public User() {
    }

    public void setRole(Role role) {
        this.role = role;
    }
    public Role getRole() {
        return role;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

}
