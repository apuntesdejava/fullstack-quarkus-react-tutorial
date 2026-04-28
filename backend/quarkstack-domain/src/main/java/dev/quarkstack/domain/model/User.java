package dev.quarkstack.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Usuario del sistema.
 * Entidad del dominio — Java puro, sin anotaciones de framework.
 */
public class User {

    private final UserId id;
    private String name;
    private String email;
    private final Instant createdAt;

    public User(UserId id, String name, String email, Instant createdAt) {
        this.id        = Objects.requireNonNull(id, "id requerido");
        this.name      = validateName(name);
        this.email     = validateEmail(email);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt requerido");
    }

    /** Factory method para crear un nuevo usuario */
    public static User create(String name, String email) {
        return new User(UserId.generate(), name, email, Instant.now());
    }

    public void updateName(String name) {
        this.name = validateName(name);
    }

    // --- Reglas de negocio del dominio ---

    private static String validateName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío");
        if (name.length() > 100)
            throw new IllegalArgumentException("El nombre no puede superar los 100 caracteres");
        return name.trim();
    }

    private static String validateEmail(String email) {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("El email no puede estar vacío");
        if (!email.contains("@"))
            throw new IllegalArgumentException("Email inválido: " + email);
        return email.trim().toLowerCase();
    }

    // --- Getters ---
    public UserId id()        { return id; }
    public String name()      { return name; }
    public String email()     { return email; }
    public Instant createdAt(){ return createdAt; }
}