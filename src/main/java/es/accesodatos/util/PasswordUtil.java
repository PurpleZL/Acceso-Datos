package es.accesodatos.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para encriptar y verificar contraseñas usando BCrypt.
 */
public class PasswordUtil {

    /**
     * Encripta una contraseña en texto plano
     * @param passTexto contraseña sin encriptar
     * @return hash BCrypt de la contraseña
     */
    public static String encrypt(String passTexto) {
        return BCrypt.hashpw(passTexto, BCrypt.gensalt()); // esta es la funcion que encrypta y te devuleve el hash
    }

    /**
     * Verifica si una contraseña coincide con un hash.
     * Es decir para ver si la contraseña que pone el usuario es la misma que la de la base de datos, compara.
     * @param passTexto contraseña sin encriptar
     * @param passHashed hash BCrypt almacenado
     * @return true si coinciden false si no
     */
    public static boolean verify(String passTexto, String passHashed) {
        return BCrypt.checkpw(passTexto, passHashed); // esta es la funcion que compara contraseñas ¿hash1 == hash2?
    }
}
