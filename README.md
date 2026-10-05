# Entregable 2 — Servidor‑Cliente con API REST (Spring Boot + MongoDB Atlas)

En este entregable **el servidor NO devuelve páginas**: expone una **API REST** que responde **JSON**.
Cualquier cliente la puede consumir: **Postman**, la página `index.html` incluida (JavaScript con `fetch`), una app móvil, Angular, React...

- Puerto: **http://localhost:8081**
- **Página de aterrizaje:** **http://localhost:8081/** (explica la API, muestra totales en vivo y tiene una consola para probar endpoints)
- **Cliente web:** **http://localhost:8081/cliente.html** — consume la API con `fetch()`: panel de inicio, CRUD de las 5 colecciones y un inspector que muestra cada petición HTTP (método, URL, código y JSON)
- Colección de Postman: `postman/Club-API-REST.postman_collection.json` (Postman → Import)

```
src/main/java/com/futbol
├── ClubApiApplication.java
├── model/        Asociacion, Club (maestra), Competicion, Entrenador, Jugador
├── repository/   5 interfaces MongoRepository
├── controller/   5 @RestController  (/api/...)
├── dto/          ClubRequest  (JSON con los ids para crear un club)
├── service/      RelacionService
└── config/       CorsConfig, ManejadorErrores, DatosDemo
src/main/resources
├── application.properties         (AQUÍ va la cadena de MongoDB Atlas)
└── static/
    ├── index.html                 (página de aterrizaje)
    ├── cliente.html               (cliente que consume la API)
    ├── css/app.css, vendor/       (diseño, Bootstrap, iconos y fuentes locales)
    └── Club-API-REST.postman_collection.json
```

## Endpoints
| Método | URL | Descripción |
|---|---|---|
| GET | `/api/clubes` | Lista clubes con sus relaciones ya resueltas |
| GET | `/api/clubes/{id}` | Un club |
| POST | `/api/clubes` | Crea club (ver JSON abajo) → **201** |
| PUT | `/api/clubes/{id}` | Actualiza club |
| DELETE | `/api/clubes/{id}` | Elimina club → **204** |
| GET | `/api/clubes/{id}/jugadores` | Jugadores del club (1‑N) |
| POST / DELETE | `/api/clubes/{id}/jugadores/{jugadorId}` | Agrega / quita un jugador |
| GET, POST | `/api/entrenadores`, `/api/jugadores`, `/api/asociaciones`, `/api/competiciones` | Listar / crear |
| GET, PUT, DELETE | `/api/<recurso>/{id}` | Buscar / actualizar / eliminar |

Crear un club (primero cree los demás y copie sus `id`):
```json
POST http://localhost:8081/api/clubes
{
  "nombre": "Atlético Ejemplo",
  "entrenadorId": "66fa1c...",
  "asociacionId": "66fa1c...",
  "jugadoresIds": ["66fa1c...", "66fa1c..."],
  "competicionesIds": ["66fa1c..."]
}
```
Crear una competición:
```json
POST http://localhost:8081/api/competiciones
{ "nombre": "Copa Colombia 2026", "montoPremio": 1500000000, "fechaInicio": "2026-03-01", "fechaFin": "2026-11-30" }
```
Códigos: `200` OK · `201` creado · `204` eliminado · `400` datos malos · `404` no existe · `409` conflicto (ej: borrar un entrenador que está en un club).

## Modelo: 5 entidades y sus relaciones (todas en la clase maestra `Club`)

| Relación | En JPA (SQL) | En MongoDB (este proyecto) | Ejemplo |
|---|---|---|---|
| Uno a Uno | `@OneToOne` | `@DocumentReference private Entrenador entrenador;` | Un club tiene un entrenador |
| Uno a Muchos | `@OneToMany` + `@JoinColumn` | `@DocumentReference private List<Jugador> jugadores;` | Un club tiene muchos jugadores |
| Muchos a Uno | `@ManyToOne` | `@DocumentReference private Asociacion asociacion;` | Muchos clubes pertenecen a la FCF |
| Muchos a Muchos | `@ManyToMany` | `@DocumentReference private List<Competicion> competiciones;` | Clubes ↔ competiciones |

- `@Entity @Table(name="clubes")` de JPA se reemplaza por `@Document(collection = "clubes")`.
- **Eager / Lazy:** `@DocumentReference` carga de inmediato (EAGER). Para carga perezosa: `@DocumentReference(lazy = true)`.
- **Restricción "foreign key":** Mongo no la tiene, por eso `RelacionService` impide borrar un entrenador/jugador/asociación/competición que esté usado en un club, y valida que un entrenador (1‑1) o un jugador (1‑N) no esté en dos clubes.
- **Orden para registrar datos:** primero asociaciones, entrenadores, jugadores y competiciones; **al final** el club (la maestra necesita que existan los otros).

## Paso a paso: conexión con MongoDB Atlas

### 1. Crear la cuenta y el clúster gratis
1. Entre a **https://www.mongodb.com/cloud/atlas/register** y regístrese (puede usar su cuenta de Google).
2. Cuando pregunte, cree un **Project** (ej: `Futbol`).
3. Clic en **Create** / **Build a Database** → escoja el plan **M0 / Free**.
4. Proveedor: AWS, región la más cercana (ej: `us-east-1`). Nombre del clúster: `Cluster0`. Clic en **Create Deployment**.

### 2. Crear el usuario de la base de datos
1. Atlas abre la ventana **Connect to Cluster0** (o vaya a **Security → Database Access → Add New Database User**).
2. Método: **Password**. Usuario, por ejemplo `estudiante`, y una contraseña **sin caracteres especiales** (solo letras y números, ej: `Futbol2026`).
   *Si la contraseña tiene `@ : / ? # %` hay que codificarla en la URL y es la causa n.º 1 de errores.*
3. Rol: **Read and write to any database** (el que sale por defecto `atlasAdmin` también sirve). Guarde.

### 3. Permitir su IP (Network Access)
1. Vaya a **Security → Network Access → Add IP Address**.
2. Para clase lo más fácil es **Allow Access from Anywhere** (`0.0.0.0/0`). Confirme.
   *Si no hace esto, la app se queda esperando y sale un error de "timeout" al conectar.*

### 4. Copiar la cadena de conexión
1. Vaya a **Database → Clusters → Connect → Drivers**.
2. Driver: **Java**, versión la más reciente.
3. Copie la cadena, se ve así:
   ```
   mongodb+srv://estudiante:<db_password>@cluster0.ab1cd.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0
   ```

### 5. Pegarla en el proyecto
Abra `src/main/resources/application.properties` y reemplace la línea `spring.data.mongodb.uri` así:
```properties
spring.data.mongodb.uri=mongodb+srv://estudiante:Futbol2026@cluster0.ab1cd.mongodb.net/agenda?retryWrites=true&w=majority&appName=Cluster0
spring.data.mongodb.database=agenda
```
- Cambie `<db_password>` por su contraseña (**sin** los signos `< >`).
- Después de `.mongodb.net/` escriba el nombre de la base: **`agenda`** (Atlas la crea sola al guardar el primer dato).
- Haga lo mismo en los **dos** proyectos (ambos usan la misma base, así lo que crea en uno se ve en el otro).

### 6. Ejecutar y comprobar
1. Abra el proyecto en IntelliJ IDEA / VS Code (con *Extension Pack for Java* + *Spring Boot Extension Pack*) / NetBeans / STS: **Open → carpeta del proyecto (donde está `pom.xml`)**. Espere que Maven descargue las dependencias.
2. Requisito: **Java 17 o superior** (JDK).
3. Ejecute la clase principal (`ClubMvcApplication` o `ClubApiApplication`) o en consola: `mvn spring-boot:run`.
4. En la consola debe salir `Cargando datos de ejemplo en MongoDB Atlas...` y `Tomcat started on port ...`.
5. En Atlas: **Database → Browse Collections** → verá la base `agenda` con las colecciones
   `asociaciones`, `clubes`, `competiciones`, `entrenadores`, `jugadores`.
   Abra un documento de `clubes`: verá que `entrenador`, `asociacion`, `jugadores` y `competiciones`
   guardan **solo los ObjectId** (igual que una llave foránea). Eso es `@DocumentReference`.

### Errores comunes
| Error en consola | Causa / solución |
|---|---|
| `Timed out after 30000 ms while waiting ...` | Su IP no está en **Network Access**. Agregue `0.0.0.0/0`. |
| `Exception authenticating ... bad auth : authentication failed` | Usuario o contraseña mal escritos, o dejó los `< >`. |
| `UnknownHostException` / `Failed looking up SRV record` | Sin internet o red que bloquea DNS (a veces pasa en la red de la universidad: pruebe con datos del celular). |
| `USUARIO:CONTRASENA@cluster0.xxxxx` en el error | No reemplazó la cadena de ejemplo en `application.properties`. |
| `Port 8080 was already in use` | Ya tiene la app corriendo; deténgala o cambie `server.port`. |
