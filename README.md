# Prueba Desarrollador - Impuestos 📊

Aplicación de consola en Java para la gestión de datos tributarios desde un archivo plano (`DATOS.TXT`) hacia una base de datos PostgreSQL.

✅ Desarrollado como prueba técnica para **Jaime Torres S.A**.

---

## 🛠️ Tecnologías utilizadas

- Java 21
- Maven
- PostgreSQL
- JDBC
- Apache POI (para exportar a Excel)
- IntelliJ IDEA

---

## 🚀 Funcionalidades

1. Cargar archivo plano `DATOS.TXT` a la base de datos
2. Consultar por fecha (ej. 12/05/2023)
3. Filtrar y sumar registros por tipo de horario (`A` / `N`)
4. Buscar por campo único `sticker`
5. Exportar datos a archivo `.xls`

---

## 🗃️ Estructura del proyecto

```
src/
 ├─ main/java/
 │   ├─ App.java
 │   ├─ db/DBConnection.java
 │   ├─ model/Impuesto.java
 │   └─ service/ImpuestoService.java
 └─ main/resources/
     └─ DATOS.TXT
```

---

## 🧑‍💻 Cómo ejecutar

1. Clona el repositorio:
```bash
git clone https://github.com/tu-usuario/prueba-desarrollador-impuestos.git
```

2. Crea una base de datos PostgreSQL llamada `PRUEBA01`

3. Ejecuta este SQL:
```sql
CREATE TABLE IF NOT EXISTS public.impuestos (
  sticker BIGINT PRIMARY KEY,
  fecha_movimiento DATE NOT NULL,
  fecha_recaudo DATE NOT NULL,
  tipo_horario VARCHAR(1),
  nro_id NUMERIC(15,0),
  nro_form NUMERIC,
  valor NUMERIC(15,0)
);
```

4. Configura tu conexión en `DBConnection.java`:
```java
private static final String URL = "jdbc:postgresql://localhost:5432/PRUEBA01";
private static final String USER = "tu_usuario";
private static final String PASSWORD = "tu_contraseña";
```

5. Corre `App.java` y sigue el menú interactivo.

---

## 📤 Exportación

Los datos se exportan en formato `.xls` usando Apache POI con la opción del menú.

---

## 📬 Contacto

> Desarrollado con cariño por [@tu-usuario](https://github.com/tu-usuario) para Jaime Torres S.A.
