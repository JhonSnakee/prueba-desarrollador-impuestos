# 🧾 Sistema de Gestión de Impuestos

Aplicación de consola desarrollada en **Java 21** con **Maven** que permite cargar, consultar y exportar registros de impuestos desde un archivo de texto plano hacia una base de datos **PostgreSQL**, con opción de exportación a **Excel**.

---

## 📋 Tabla de Contenidos

- [Tecnologías](#-tecnologías)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Requisitos Previos](#-requisitos-previos)
- [Configuración de Base de Datos](#-configuración-de-base-de-datos)
- [Configuración de Conexión](#-configuración-de-conexión)
- [Formato del Archivo de Datos](#-formato-del-archivo-de-datos)
- [Compilación y Ejecución](#-compilación-y-ejecución)
- [Funcionalidades](#-funcionalidades)

---

## 🛠 Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Maven | 3.x |
| PostgreSQL JDBC Driver | 42.7.2 |
| Apache POI (Excel) | 5.2.5 |
| Log4j2 | 2.20.0 |

---

## 📁 Estructura del Proyecto

```
prueba-desarrollador-impuestos/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── App.java                    # Punto de entrada / Menú principal
│   │   │   ├── db/
│   │   │   │   └── DBConnection.java       # Conexión a PostgreSQL
│   │   │   ├── model/
│   │   │   │   └── Impuesto.java           # Modelo de datos
│   │   │   └── service/
│   │   │       └── ImpuestoService.java    # Lógica de negocio
│   │   └── resources/
│   │       ├── datos.TXT                   # Archivo de datos de entrada
│   │       └── log4j2.xml                  # Configuración de logs
│   └── test/
│       └── java/
│           └── org/example/
│               └── AppTest.java
```

---

## ✅ Requisitos Previos

- **JDK 21** o superior instalado
- **Maven 3.x** instalado
- **PostgreSQL** instalado y en ejecución (puerto `5432`)
- Base de datos y tabla creadas (ver sección siguiente)

---

## 🗄 Configuración de Base de Datos

Ejecuta el siguiente script en tu instancia de PostgreSQL para crear la base de datos y la tabla requerida:

```sql
-- Crear la base de datos
CREATE DATABASE prueba01;

-- Conectarse a la base de datos
\c prueba01

-- Crear la tabla de impuestos
CREATE TABLE impuestos (
    sticker          BIGINT PRIMARY KEY,
    fecha_movimiento DATE   NOT NULL,
    fecha_recaudo    DATE   NOT NULL,
    tipo_horario     VARCHAR(1) NOT NULL,
    nro_id           BIGINT NOT NULL,
    nro_form         BIGINT NOT NULL,
    valor            BIGINT NOT NULL
);
```

---

## ⚙️ Configuración de Conexión

Los parámetros de conexión se encuentran en el archivo `src/main/java/db/DBConnection.java`:

```java
private static final String URL      = "jdbc:postgresql://localhost:5432/prueba01";
private static final String USER     = "jhon";
private static final String PASSWORD = "admin";
```

Modifica estos valores según tu entorno antes de compilar.

---

## 📄 Formato del Archivo de Datos

El archivo `datos.TXT` (ubicado en `src/main/resources/`) contiene registros separados por comas con el siguiente formato:

```
sticker,fechaMovimiento,fechaRecaudo,tipoHorario,nroId,nroForm,valor
```

**Ejemplo:**
```
1000020628634,20250311,20250311,N,8600029644,4910052408623,3000000
```

| Campo | Descripción | Formato |
|---|---|---|
| `sticker` | Identificador único del registro | Numérico |
| `fechaMovimiento` | Fecha del movimiento | `AAAAMMDD` |
| `fechaRecaudo` | Fecha del recaudo | `AAAAMMDD` |
| `tipoHorario` | Tipo de horario | `A` (Ampliado) / `N` (Normal) |
| `nroId` | Número de identificación del contribuyente | Numérico |
| `nroForm` | Número de formulario | Numérico |
| `valor` | Valor del impuesto en pesos | Numérico |

---

## 🚀 Compilación y Ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/prueba-desarrollador-impuestos.git
cd prueba-desarrollador-impuestos
```

### 2. Compilar el proyecto

```bash
mvn clean package
```

### 3. Ejecutar la aplicación

```bash
mvn exec:java -Dexec.mainClass="App"
```

---

## 🎯 Funcionalidades

Al ejecutar la aplicación se despliega el siguiente menú interactivo:

```
===== MENÚ IMPUESTOS =====
1. Cargar archivo DATOS.TXT
2. Consultar por fecha
3. Consultar cantidad y suma por tipo de horario (A / N)
4. Buscar registro por sticker
5. Exportar todos los datos a archivo Excel (.xls)
0. Salir
```

### Opción 1 — Cargar archivo DATOS.TXT
Lee el archivo `src/main/resources/datos.TXT` e inserta los registros en la base de datos PostgreSQL. Los registros duplicados (mismo `sticker`) se ignoran automáticamente.

### Opción 2 — Consultar por fecha
Permite buscar todos los registros cuya `fecha_movimiento` coincida con la fecha ingresada en formato `AAAA-MM-DD`.

### Opción 3 — Consultar por tipo de horario
Muestra la **cantidad total de registros** y la **suma de valores** para un tipo de horario específico:
- `A` → Horario Ampliado
- `N` → Horario Normal

### Opción 4 — Buscar por sticker
Muestra el detalle completo de un registro a partir de su número de sticker. Ofrece la opción de exportar ese registro individual a un archivo Excel (`sticker_<número>.xls`).

### Opción 5 — Exportar todos los datos a Excel
Exporta la totalidad de los registros almacenados en la base de datos al archivo `impuestos_exportados.xls` en el directorio raíz del proyecto.

---

## 👤 Autor

> Desarrollado con cariño por [@JhonSnakee](https://github.com/JhonSnakee)

