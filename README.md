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

# Preguntas dificiles posibles

Sobre ORM (Object-Relational Mapping)

1. ¿Qué es un ORM y por qué lo usáis?

    Un ORM mapea objetos Java a tablas de base de datos, evitando escribir SQL manual. Permite trabajar con objetos en lugar de registros.

2. ¿Qué problema resuelve el ORM respecto a JDBC puro?

    Elimina código repetitivo, gestiona conexiones, mapea resultados automáticamente, y abstrae las diferencias entre bases de datos.

3. ¿Qué es el "impedance mismatch" o desajuste de impedancia?

   Es la diferencia entre el modelo orientado a objetos (herencia, polimorfismo) y el modelo relacional (tablas, filas). El ORM lo resuelve.

  ---
Sobre JPA

4. ¿Qué es JPA y qué relación tiene con Hibernate?

    JPA es una especificación (estándar), Hibernate es una implementación de esa especificación (JPA + EXTRAS).
    JPA define las anotaciones y la API, Hibernate las ejecuta.

5. ¿Qué es el persistence.xml y qué se configura ahí?

    Define la unidad de persistencia en nuestro caso `acceso-datos-pu`, el proveedor (Hibernate), la conexión a MySQL,
   y propiedades como hbm2ddl.auto=update. (En general sirve para la configuracion de persistencia con la base de datos)

6. ¿Qué significa transaction-type="RESOURCE_LOCAL"?

   Que las transacciones se gestionan manualmente con begin(), commit(), rollback(). La alternativa es JTA para servidores de aplicaciones.

7. ¿Qué hace hibernate.hbm2ddl.auto=update?

   Actualiza automáticamente el esquema de la BD según las entidades. Crea tablas si no existen, añade columnas nuevas.
   Algo peligroso en producción (para producción se recomienda `none` porque no hace comprobaciones ni altera).

  ---
Sobre Entidades

8. ¿Qué anotaciones usáis en la entidad Usuario y qué hacen?

- `@Entity`: Marca la clase como entidad JPA
- `@Table(name="usuarios")`: Define el nombre de la tabla
- `@Id`: Marca la clave primaria
- `@GeneratedValue(strategy=IDENTITY)`: Auto-incremento en el id
- `@Column(nullable=false, unique=true)`: Restricciones de columna

9. ¿Por qué el id es Long y no long (primitivo)?

   Porque `Long` puede ser null, lo que indica que la entidad aún no se ha persistido. Un primitivo (`int` o `long`) siempre tiene valor (0).

10. ¿Qué es una entidad en JPA?

    Una clase Java que representa una tabla en la BD. Cada instancia (Ej: `String nombre`) es una fila.

  ---
Sobre EntityManager

11. ¿Qué es el EntityManager y para qué sirve?

    Es el objeto principal de JPA para gestionar entidades. Permite persistir, buscar, actualizar y eliminar objetos.

12. ¿Cuál es la diferencia entre persist() y merge()?

- `persist()`: Para entidades nuevas (sin ID). Las hace persistentes. (Creamos algo nuevo)
- `merge()`: Para entidades desacopladas. Copia el estado a una entidad gestionada. (Actualizamos o leemos algo que ya existe)

13. ¿Qué hace em.find(Usuario.class, id)?

    Busca una entidad por su clave primaria. Devuelve null si no existe.

14. ¿Por qué hay que hacer begin() y commit() en las operaciones de escritura pero no en las de lectura?

    Las operaciones de escritura (INSERT, UPDATE, DELETE) modifican datos y necesitan transacción. Las lecturas (SELECT) no modifican nada.

  ---
Sobre JPQL

15. ¿Qué es JPQL y en qué se diferencia de SQL?

    JPQL es un lenguaje de consultas orientado a objetos. Consulta entidades (no tablas) y atributos (no columnas). Es independiente de la BD.

16. Explicad esta consulta: `SELECT u FROM Usuario u WHERE u.email = :email`

    Selecciona objetos Usuario donde el atributo email coincida con el parámetro `:email`. u es un alias para Usuario.

17. ¿Qué es un TypedQuery y por qué lo usáis?

    Es una consulta tipada (escrita manualmente) que devuelve objetos del tipo especificado (Usuario), evitando casting. Más seguro que Query genérico.

18. ¿Cuál es la diferencia entre getSingleResult() y getResultList()?

- `getSingleResult()`: Espera exactamente UN resultado. Lanza excepción si hay 0 o más de 1.
- `getResultList()`: Devuelve una lista (puede estar vacía).

  ---
Sobre el ciclo de vida de entidades

19. ¿Cuáles son los estados de una entidad en JPA?

- New/Transient: Objeto creado pero no persistido
- Managed: Gestionado por EntityManager (normalmente despues de persistir o usar `persist()`)
- Detached: Fue gestionado pero ya no (EntityManager cerrado)
- Removed: Marcado para eliminar

20. ¿Cuándo una entidad pasa de "new" a "managed"?

    Cuando se llama a `persist()` dentro de una transacción.

  ---
Sobre vuestro código específico

21. ¿Por qué usáis el patrón DAO?

    Para separar la lógica de acceso a datos de la lógica de negocio. Facilita mantenimiento, testing y cambio de BD.

22. ¿Por qué devolvéis Optional en buscarPorId() y buscarPorEmail()?

    Para manejar de forma elegante el caso de que no exista el usuario, evitando `null` y `NullPointerException`, excepciones que es mejor evitar.

23. ¿Por qué encriptáis las contraseñas con BCrypt?

    Por seguridad. Si alguien accede a la BD, no puede ver las contraseñas reales. BCrypt usa salt y es resistente a ataques de fuerza bruta.

24. ¿Qué pasa si el email ya existe al registrar un usuario?

    El servicio valida unicidad antes de persistir y lanza una excepción.

  ---
Preguntas trampa / Avanzadas

25. ¿Usáis relaciones JPA en vuestro proyecto?

    No, solo tenemos una entidad Usuario sin relaciones (OneToMany, ManyToOne, etc.).
    Es decir, no unimos mas tablas porque solo tenemos 1, la de "usuarios"

26. ¿Qué tipos de relaciones existen en JPA? (aunque no las uséis)

    @OneToOne, @OneToMany, @ManyToOne, @ManyToMany
    Es concepto importante aunque no lo tengamos.

27. ¿Qué es el Lazy Loading vs Eager Loading?

- Lazy: Carga las relaciones solo cuando se acceden
- Eager: Carga todo inmediatamente
  Es concepto importante aunque no lo tengamos.

28. ¿Qué pasaría si quitáis @GeneratedValue del id?

    Tendriamos que asignar el ID manualmente antes de persistir.

  ---
Top 10 preguntas MÁS PROBABLES

```
┌─────┬──────────────────────────────────────────────────────────┐
│  #  │                         Pregunta                         │
├─────┼──────────────────────────────────────────────────────────┤
│ 1   │ ¿Qué es JPA y qué relación tiene con Hibernate?          │
├─────┼──────────────────────────────────────────────────────────┤
│ 2   │ ¿Qué anotaciones usáis y para qué sirven?                │
├─────┼──────────────────────────────────────────────────────────┤
│ 3   │ ¿Qué es el EntityManager?                                │
├─────┼──────────────────────────────────────────────────────────┤
│ 4   │ Diferencia entre persist() y merge()                     │
├─────┼──────────────────────────────────────────────────────────┤
│ 5   │ ¿Qué es JPQL?                                            │
├─────┼──────────────────────────────────────────────────────────┤
│ 6   │ ¿Qué hace hbm2ddl.auto=update?                           │
├─────┼──────────────────────────────────────────────────────────┤
│ 7   │ ¿Qué es un ORM?                                          │
├─────┼──────────────────────────────────────────────────────────┤
│ 8   │ Estados de una entidad (new, managed, detached, removed) │
├─────┼──────────────────────────────────────────────────────────┤
│ 9   │ ¿Por qué usáis el patrón DAO?                            │
├─────┼──────────────────────────────────────────────────────────┤
│ 10  │ ¿Qué tipos de relaciones existen en JPA?                 │
└─────┴──────────────────────────────────────────────────────────┘
```