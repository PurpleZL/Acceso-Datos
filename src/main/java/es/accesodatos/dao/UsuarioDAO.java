package es.accesodatos.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import es.accesodatos.entity.Usuario;
import java.util.*;

/**
 * DAO para operaciones CRUD (Create, Read, Update, Delete)
 */
public class UsuarioDAO {

    private static final String PERSISTENCE_UNIT = "acceso-datos-pu"; // debe coincidir con persistence.xml
    private final EntityManagerFactory emf;

    public UsuarioDAO() {
        this.emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }

    // CREATE
    public Usuario crear(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
            return usuario;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // READ - Buscar por ID
    public Optional<Usuario> buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Usuario usuario = em.find(Usuario.class, id);
            return Optional.ofNullable(usuario);
        } finally {
            em.close();
        }
    }

    // READ - Buscar por email - Personalizado
    public Optional<Usuario> buscarPorEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class);
            query.setParameter("email", email);
            List<Usuario> resultados = query.getResultList();
            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
        } finally {
            em.close();
        }
    }

    // READ - Listar todos - Perosnalizado
    public List<Usuario> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u", Usuario.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // UPDATE
    public Usuario actualizar(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Usuario usuarioActualizado = em.merge(usuario);
            em.getTransaction().commit();
            return usuarioActualizado;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // DELETE
    public boolean eliminar(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario != null) {
                em.remove(usuario);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Cerrar EntityManagerFactory
    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
