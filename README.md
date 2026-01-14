# Acceso-Datos

- JDK-24
- MySQL (root:root)
- Docker
- Ubuntu version generica

# Requisitos
Ejemplo funcional minimo:
- 1 unica entidad
- Conexion mysql en contenedor Docker en ubuntu
- Persistencia usando JPA
- Operaciones CRUD básicas

# Estructura

```shell
  src/main/java/es/accesodatos/
  ├── Main.java                 # Main (Punto de entrada)
  ├── entity/
  │   └── Usuario.java          # Entidad JPA equivalente a la tabla Usuario
  ├── dao/
  │   └── UsuarioDAO.java       # Operaciones de base de datos
  ├── service/
  │   └── UsuarioService.java   # Logica de negocio
  └── util/
      └── PasswordUtil.java     # Utilidad para la encryptacion de contraseñas

  src/main/resources/META-INF/
  └── persistence.xml           # Configuracion JPA/Hibernate (persistencia)
```

# Otros archivos
 - docker-compose.yml: levanta MySQL en Docker rápidamente 
 - pom.xml: dependencias: hibernate, mysql y bcrypt
 - persistence.xml: para la conexion con la base de datos

# Deployment
(Necesitaras tener Docker ya instalado)

0. Descargar Docker Compose: `sudo apt install docker-compose-plugin`
1. Levantar MySQL en docker:`docker-compose up -d`
2. Ejecutar proyecto: `mvn compile exec:java -Dexec.mainClass="es.accesodatos.Main"` o en IntelliJ
