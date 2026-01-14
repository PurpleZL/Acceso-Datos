package es.accesodatos.service;

import es.accesodatos.dao.UsuarioDAO;
import es.accesodatos.entity.Usuario;
import es.accesodatos.util.PasswordUtil;
import java.util.*;

/**
 * Logica de negocio
 */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Registra un nuevo usuario encriptando su contraseña
     */
    public Usuario registrar(String email, String nombre, String passwordPlano, Integer edad) {
        // Validaciones
        validarCamposObligatorios(email, nombre, passwordPlano);
        validarFormatoEmail(email);
        validarEmailUnico(email, null); // null porque es nuevo usuario

        // [!] OJO: Encriptar contraseña antes de guardar
        String passwordEncriptado = PasswordUtil.encrypt(passwordPlano);
        Usuario usuario = new Usuario(email, nombre, passwordEncriptado, edad);
        return usuarioDAO.crear(usuario);
    }

    /**
     * Autentica un usuario verificando su contraseña
     * @return Optional con el usuario si las credenciales son correctas
     */
    public Optional<Usuario> autenticar(String email, String passwordPlano) {
        // 1. Primero vemos si existe su correo
        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // 2. Si existe el correo, miramos a ver si coinciden las contraseñas
            if (PasswordUtil.verify(passwordPlano, usuario.getPassword())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    /**
     * Busca un usuario por su ID.
     */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioDAO.buscarPorId(id);
    }

    /**
     * Busca un usuario por su email.
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioDAO.buscarPorEmail(email);
    }

    /**
     * Lista todos los usuarios.
     */
    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    /**
     * Actualiza los datos de un usuario (sin cambiar contraseña).
     */
    public Usuario actualizar(Usuario usuario) {
        // Validaciones
        if (usuario == null || usuario.getId() == null) {
            throw new RuntimeException("[!] El usuario o su ID no pueden ser nulos");
        }
        validarCamposObligatorios(usuario.getEmail(), usuario.getNombre(), usuario.getPassword());
        validarFormatoEmail(usuario.getEmail());
        validarEmailUnico(usuario.getEmail(), usuario.getId()); // Pasamos el ID para excluirlo de la búsqueda

        return usuarioDAO.actualizar(usuario);
    }

    /**
     * Cambia la contraseña de un usuario.
     */
    public Usuario cambiarPassword(Long id, String nuevaPasswordPlana) {
        // Validación de contraseña
        if (nuevaPasswordPlana == null || nuevaPasswordPlana.trim().isEmpty()) {
            throw new RuntimeException("[!] La contraseña no puede estar vacía");
        }

        // 1. Primero buscamos a ver si existe el usuario
        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorId(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // 2. Si existe le ponemos la nueva contraseña
            usuario.setPassword(PasswordUtil.encrypt(nuevaPasswordPlana));
            return usuarioDAO.actualizar(usuario);
        }
        throw new RuntimeException("[!] Usuario no encontrado con ID: " + id);
    }

    /**
     * Elimina un usuario por su ID.
     */
    public boolean eliminar(Long id) {
        return usuarioDAO.eliminar(id);
    }

    /**
     * Cierra los recursos del servicio.
     */
    public void cerrar() {
        usuarioDAO.cerrar();
    }

    /**
     * Metodos de validacion (hibernate.validation es mejor y mas recomendado pero manualmente seria asi)
     */

    /**
     * Valida que los campos obligatorios no sean nulos o vacíos
     */
    private void validarCamposObligatorios(String email, String nombre, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("[!] El email es obligatorio");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new RuntimeException("[!] El nombre es obligatorio");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("[!] La contraseña es obligatoria");
        }
    }

    /**
     * Valida que el email tenga un formato válido usando regex simple
     */
    private void validarFormatoEmail(String email) {
        // he buscado el regex y este es el basico para email: "algo + '@' + algo + '.' + algo"
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(regex)) {
            throw new RuntimeException("[!] El formato del email no es correcto");
        }
    }

    /**
     * Valida que el email no esté duplicado en la base de datos
     * @param email Email a validar
     * @param idUsuarioActual ID del usuario actual (null si es un nuevo registro)
     */
    private void validarEmailUnico(String email, Long idUsuarioActual) {
        Optional<Usuario> usuarioExistente = usuarioDAO.buscarPorEmail(email);

        if (usuarioExistente.isPresent()) {
            // Si es un nuevo registro o el email pertenece a otro usuario
            if (idUsuarioActual == null || !usuarioExistente.get().getId().equals(idUsuarioActual)) {
                throw new RuntimeException("[!] El email '" + email + "' ya esta registrado");
            }
        }
    }
}
