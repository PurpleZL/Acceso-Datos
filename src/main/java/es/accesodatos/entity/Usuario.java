package es.accesodatos.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Obligatorio y UNICO
    private String email;

    @Column(nullable = false) // Obligatorio
    private String nombre;

    @Column(nullable = false) // Obligatorio
    private String password; // Almacena el hash BCrypt

    private Integer edad; // Opcional

    //Constructor vacio
    public Usuario() {
    }

    //Constructor lleno
    public Usuario(String email, String nombre, String password, Integer edad) {
        this.email = email;
        this.nombre = nombre;
        this.password = password;
        this.edad = edad;
    }

    // Getters/Setters normales
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    @Override //Fijaros de que no devolvemos la contraseña
    public String toString() {
        return "Usuario{id=" + id + ", email='" + email + "', nombre='" + nombre + "', edad=" + edad + "}";
    }
}
