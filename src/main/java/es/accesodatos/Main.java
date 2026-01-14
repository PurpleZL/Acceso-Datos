package es.accesodatos;

import es.accesodatos.entity.Usuario;
import es.accesodatos.service.UsuarioService;
import java.util.*;

/**
 * menu interactivo para gestionar usuarios
 */
public class Main {

    private static final UsuarioService service = new UsuarioService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero("[!] Selecciona una opcion: ");

            switch (opcion) {
                case 1 -> registrarUsuario();
                case 2 -> listarUsuarios();
                case 3 -> buscarPorId();
                case 4 -> buscarPorEmail();
                case 5 -> actualizarUsuario();
                case 6 -> cambiarPassword();
                case 7 -> eliminarUsuario();
                case 8 -> autenticar();
                case 0 -> System.out.println("[!] Hasta luego"); //salida
                default -> System.out.println("[X] Opcion no valida");
            }

            if (opcion != 0) {
                System.out.println("\n[~] Presiona cualquier tecla para continuar");
                scanner.nextLine();
            }

        } while (opcion != 0);

        service.cerrar();
    }

    /**
     * Menu simple
     */
    private static void mostrarMenu() {
        System.out.println("++++++++++++++++++++++++++++++++++++++");
        System.out.println("          GESTION DE USUARIOS         ");
        System.out.println("++++++++++++++++++++++++++++++++++++++");
        System.out.println("|  1. Registrar usuario              |");
        System.out.println("|  2. Listar todos los usuarios      |");
        System.out.println("|  3. Buscar por ID                  |");
        System.out.println("|  4. Buscar por email               |");
        System.out.println("|  5. Actualizar usuario             |");
        System.out.println("|  6. Cambiar contraseña             |");
        System.out.println("|  7. Eliminar usuario               |");
        System.out.println("|  8. Autenticar (login simple)      |");
        System.out.println("|  0. Salir                          |");
        System.out.println("++++++++++++++++++++++++++++++++++++++");
    }

    /**
     * Metodos de interaccion con el usuario, luego pasara a Usuario Service para la logica y luego a Usuario DAO
     * Main (interaccion) -> Service (logica de negocio) -> DAO (ejecucion en bd)
     */

    private static void registrarUsuario() {
        System.out.println("\n--- REGISTRAR USUARIO ---");
        String email = leerTexto("[+] Email: ");
        String nombre = leerTexto("[+] Nombre: ");
        String password = leerTexto("[+] Password: ");
        Integer edad = leerEntero("[+] Edad: ");

        try {
            Usuario usuario = service.registrar(email, nombre, password, edad);
            System.out.println("[=] Usuario registrado: " + usuario);
        } catch (Exception e) {
            System.out.println("[!] Error al registrar: " + e.getMessage());
        }
    }

    private static void listarUsuarios() {
        System.out.println("\n--- LISTA DE USUARIOS ---");
        List<Usuario> usuarios = service.listarTodos();

        if (usuarios.isEmpty()) {
            System.out.println("[?] No hay usuarios registrados");
        } else {
            usuarios.forEach(System.out::println);
        }
    }

    private static void buscarPorId() {
        System.out.println("\n--- BUSCAR POR ID ---");
        Long id = leerLong("[+] ID del usuario: ");

        Optional<Usuario> usuario = service.buscarPorId(id);
        usuario.ifPresentOrElse(
                u -> System.out.println("[=] Encontrado: " + u),
                () -> System.out.println("[!] Usuario no encontrado")
        );
    }

    private static void buscarPorEmail() {
        System.out.println("\n--- BUSCAR POR EMAIL ---");
        String email = leerTexto("[+] Email: ");

        Optional<Usuario> usuario = service.buscarPorEmail(email);
        usuario.ifPresentOrElse(
                u -> System.out.println("[=] Encontrado: " + u),
                () -> System.out.println("Usuario no encontrado")
        );
    }

    private static void actualizarUsuario() {
        System.out.println("\n--- ACTUALIZAR USUARIO ---");
        Long id = leerLong("[+] ID del usuario a actualizar: ");

        Optional<Usuario> usuarioOpt = service.buscarPorId(id);

        if (usuarioOpt.isEmpty()) {
            System.out.println("[!] Usuario no encontrado");
            return;
        }

        Usuario usuario = usuarioOpt.get();
        System.out.println("[?] Usuario actual: " + usuario);

        String nuevoNombre = leerTexto("[+] Nuevo nombre (ENTER para mantener): ");
        String nuevoEmail = leerTexto("[+] Nuevo email (ENTER para mantener): ");
        String edadStr = leerTexto("[+] Nueva edad (ENTER para mantener): ");

        if (!nuevoNombre.isEmpty()) {
            usuario.setNombre(nuevoNombre);
        }
        if (!nuevoEmail.isEmpty()) {
            usuario.setEmail(nuevoEmail);
        }
        if (!edadStr.isEmpty()) {
            usuario.setEdad(Integer.parseInt(edadStr));
        }

        try {
            Usuario actualizado = service.actualizar(usuario);
            System.out.println("[=] Usuario actualizado: " + actualizado);
        } catch (Exception e) {
            System.out.println("[!] Error al actualizar: " + e.getMessage());
        }
    }

    private static void cambiarPassword() {
        System.out.println("\n--- CAMBIAR PASSWORD ---");
        Long id = leerLong("[+] ID del usuario: ");
        String nuevaPassword = leerTexto("[+] Nueva password: ");

        try {
            service.cambiarPassword(id, nuevaPassword);
            System.out.println("[=] Password cambiada correctamente");
        } catch (Exception e) {
            System.out.println("[!] Error: " + e.getMessage());
        }
    }

    private static void eliminarUsuario() {
        System.out.println("\n--- ELIMINAR USUARIO ---");
        Long id = leerLong("[+] ID del usuario a eliminar: ");

        String confirmacion = leerTexto("[?] ¿Estas seguro? (s/n): ");

        if (confirmacion.equalsIgnoreCase("s")) {
            boolean eliminado = service.eliminar(id);
            System.out.println(eliminado ? "[=] Usuario eliminado" : "[?] Usuario no encontrado");
        } else {
            System.out.println("[!] Operacion cancelada");
        }
    }

    private static void autenticar() {
        System.out.println("\n--- AUTENTICACION ---");
        String email = leerTexto("[+] Email: ");
        String password = leerTexto("[+] Password: ");

        Optional<Usuario> usuario = service.autenticar(email, password);

        if (usuario.isPresent()) {
            System.out.println("[OK] LOGIN EXITOSO");
            System.out.println("[OK] Bienvenido, " + usuario.get().getNombre());
        } else {
            System.out.println("[X] LOGIN FALLIDO - Credenciales incorrectas");
        }
    }

    /**
     * Metodos auxiliares para el input de usuarios, evita repeticion y mejora limpieza
     */

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        while (!scanner.hasNextInt()) {
            System.out.print("[+] Ingresa un numero valido: ");
            scanner.next();
        }
        int valor = scanner.nextInt();
        scanner.nextLine(); //buffer
        return valor;
    }

    private static Long leerLong(String mensaje) {
        System.out.print(mensaje);
        while (!scanner.hasNextLong()) {
            System.out.print("[+] Ingresa un numero valido: ");
            scanner.next();
        }
        Long valor = scanner.nextLong();
        scanner.nextLine(); //buffer
        return valor;
    }
}
