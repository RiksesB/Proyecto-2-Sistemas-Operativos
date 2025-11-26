# Simulador Virtual de Sistema de Archivos

## Descripción del Proyecto

Este proyecto consiste en el desarrollo de un simulador de sistema de archivos avanzado que permite comprender y aplicar conceptos fundamentales de sistemas operativos como la gestión de archivos y directorios, la asignación de bloques de almacenamiento, la administración de permisos, la fragmentación del espacio en disco, y la gestión de operaciones de entrada/salida mediante procesos de usuario.

El simulador está desarrollado completamente en Java utilizando NetBeans, con una interfaz gráfica intuitiva que representa visualmente la estructura jerárquica de directorios y archivos mediante un JTree, la distribución de bloques en un disco simulado, una tabla de asignación de archivos, y un sistema completo de gestión de procesos que realizan operaciones de E/S.

## Características Principales

### Sistema de Archivos
- **Estructura jerárquica:** Visualización de directorios y archivos en forma de árbol (JTree)
- **Operaciones CRUD completas:**
  - Crear archivos y directorios con tamaños personalizados
  - Leer y visualizar información detallada
  - Actualizar nombres de archivos y directorios
  - Eliminar archivos liberando bloques, y directorios eliminando recursivamente todo su contenido
- **Sistema de permisos:** Control de acceso basado en tipo de usuario y permisos (lectura, escritura, ejecución)

### Simulación del Disco
- **Asignación encadenada de bloques:** Cada archivo se representa como una lista enlazada de bloques en el disco
- **Visualización en tiempo real:** Representación gráfica del disco mostrando bloques ocupados y libres con diferentes colores según el archivo que los ocupa
- **Gestión inteligente del espacio:** Validación automática de espacio disponible antes de crear archivos
- **Desfragmentación:** Capacidad de reorganizar bloques para optimizar el uso del disco

### Planificación de Disco
Se implementaron seis algoritmos diferentes de planificación para gestionar las solicitudes de E/S:

1. **FIFO (First In First Out):** Las solicitudes se atienden en el orden en que llegan
2. **SSTF (Shortest Seek Time First):** Se atiende primero la solicitud más cercana al cabezal actual
3. **SCAN (Algoritmo del Ascensor):** El cabezal se mueve en una dirección atendiendo solicitudes hasta llegar al final, luego cambia de dirección
4. **C-SCAN (Circular SCAN):** Similar a SCAN pero al llegar al final regresa al inicio sin atender solicitudes en el camino de retorno
5. **LOOK:** Versión mejorada de SCAN que solo llega hasta la última solicitud en cada dirección
6. **C-LOOK:** Versión circular de LOOK que retorna al inicio después de atender la última solicitud

### Modos de Usuario
El sistema opera en dos modos diferentes:

- **Modo Administrador:**
  - Realizar todas las operaciones sin restricciones
  - Crear, modificar y eliminar cualquier archivo o directorio
  - Gestionar todos los procesos del sistema
  - Cambiar políticas de planificación del disco
  - Acceso completo a información del disco y estadísticas

- **Modo Usuario:**
  - Solo lectura de archivos propios o públicos
  - Crear procesos para operaciones de E/S sobre archivos propios
  - Sin capacidad de modificar archivos del sistema o acceder a información de otros usuarios

### Gestión de Procesos
- **Estados de proceso:** Nuevo, Listo, Ejecutando, Bloqueado, Terminado
- **Cola de procesos:** Visualización del estado actual de todos los procesos
- **Solicitudes de E/S:** Cada operación CRUD genera solicitudes que son procesadas según la política de planificación activa
- **Simulador visual:** Permite ejecutar paso a paso o automáticamente las operaciones de E/S con visualización del movimiento del cabezal del disco

### Almacenamiento Intermedio (Buffer)
Sistema opcional de caché implementado con:
- **Capacidad configurable:** Buffer de memoria para almacenar temporalmente bloques frecuentemente accedidos
- **Políticas de reemplazo:**
  - FIFO (First In First Out)
  - LRU (Least Recently Used)
  - LFU (Least Frequently Used)
- **Estadísticas de rendimiento:** Visualización de hits, misses y tasa de aciertos del buffer

### Tabla de Asignación
Tabla visual que muestra en tiempo real:
- Nombre de cada archivo
- Cantidad de bloques asignados
- Dirección del primer bloque
- Propietario del archivo
- Proceso que creó el archivo
- Color distintivo para identificación visual en el disco

### Persistencia de Datos
- **Guardado automático:** El sistema puede guardar su estado completo en un archivo de texto
- **Carga de sesiones:** Permite recuperar el estado del sistema en ejecuciones futuras
- **Formato de almacenamiento:** Archivo de texto plano con toda la información del disco, usuarios y estructura de archivos

## Requisitos del Sistema

- **Java:** Versión 21 o superior
- **IDE:** NetBeans (recomendado para desarrollo)
- **Sistema Operativo:** Windows, Linux o macOS
- **Memoria RAM:** Mínimo 512 MB disponibles

## Instalación y Ejecución

### Opción 1: Desde NetBeans
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/RiksesB/Proyecto-2-Sistemas-Operativos.git
   ```

2. Abrir NetBeans y seleccionar "Abrir Proyecto"

3. Navegar hasta la carpeta `Simulador Sistema de Archivos`

4. Hacer clic derecho en el proyecto y seleccionar "Ejecutar" o presionar F6

### Opción 2: Desde línea de comandos
1. Clonar el repositorio

2. Navegar al directorio del proyecto:
   ```bash
   cd "Proyecto-2-Sistemas-Operativos/Simulador Sistema de Archivos"
   ```

3. Compilar el proyecto:
   ```bash
   javac -d build/classes -sourcepath src src/main/Main.java
   ```

4. Ejecutar la aplicación:
   ```bash
   java -cp build/classes main.Main
   ```

## Uso del Simulador

### Inicio Rápido

1. **Cambiar modo de usuario:** Utilizar el selector en la barra superior para cambiar entre "Administrador" y "Usuario"

2. **Crear archivos:**
   - Seleccionar un directorio en el árbol
   - Hacer clic en el botón "📄 Archivo"
   - Especificar nombre y tamaño en bloques
   - El sistema asignará bloques automáticamente

3. **Crear directorios:**
   - Seleccionar un directorio padre
   - Hacer clic en "📁 Directorio"
   - Ingresar el nombre

4. **Cambiar algoritmo de planificación:**
   - Usar el selector "Planificación" en la barra superior
   - Elegir entre FIFO, SSTF, SCAN, C-SCAN, LOOK o C-LOOK

5. **Ver estadísticas:** Hacer clic en "📊 Estadísticas" para ver información detallada del sistema

6. **Guardar y cargar:**
   - "💾 Guardar" para almacenar el estado actual
   - "📂 Cargar" para recuperar una sesión anterior

### Simulador de I/O

El panel inferior muestra el simulador de operaciones de entrada/salida:

- **Avanzar:** Ejecutar un paso de la simulación
- **Auto:** Ejecutar automáticamente toda la simulación
- **Completar:** Saltar al final de la simulación
- **Reiniciar:** Volver al estado inicial
- **Velocidad:** Ajustar la velocidad de la simulación automática

## Estructura del Proyecto

```
Simulador Sistema de Archivos/
├── src/
│   ├── main/
│   │   └── Main.java                 # Punto de entrada de la aplicación
│   ├── controller/
│   │   ├── ControladorPrincipal.java # Controlador principal del sistema
│   │   ├── GestorArchivos.java       # Gestión de archivos y directorios
│   │   ├── GestorBuffer.java         # Gestión del buffer de memoria
│   │   ├── GestorDisco.java          # Gestión del disco virtual
│   │   ├── GestorPersistencia.java   # Guardado y carga del sistema
│   │   ├── GestorProcesos.java       # Gestión de procesos
│   │   └── SimuladorIO.java          # Simulación de operaciones I/O
│   ├── model/
│   │   ├── archivos/
│   │   │   ├── Archivo.java          # Modelo de archivo
│   │   │   ├── Directorio.java       # Modelo de directorio
│   │   │   ├── NodoArbol.java        # Nodo genérico del árbol
│   │   │   └── TipoPermiso.java      # Enumeración de permisos
│   │   ├── disco/
│   │   │   ├── Bloque.java           # Modelo de bloque de disco
│   │   │   ├── Disco.java            # Modelo del disco virtual
│   │   │   ├── EstadoBloque.java     # Estados de un bloque
│   │   │   └── TablaAsignacion.java  # Tabla de asignación de archivos
│   │   ├── planificacion/
│   │   │   ├── GestorPlanificacion.java
│   │   │   ├── PlanificadorFIFO.java
│   │   │   ├── PlanificadorSSTF.java
│   │   │   ├── PlanificadorSCAN.java
│   │   │   ├── PlanificadorCSCAN.java
│   │   │   ├── PlanificadorLOOK.java
│   │   │   ├── PlanificadorCLOOK.java
│   │   │   └── TipoPlanificacion.java
│   │   ├── procesos/
│   │   │   ├── Proceso.java          # Modelo de proceso
│   │   │   ├── EstadoProceso.java    # Estados de un proceso
│   │   │   ├── SolicitudIO.java      # Solicitud de I/O
│   │   │   └── TipoOperacion.java    # Tipos de operaciones
│   │   └── sistema/
│   │       ├── SistemaArchivos.java  # Sistema de archivos principal
│   │       ├── Usuario.java          # Modelo de usuario
│   │       └── TipoUsuario.java      # Tipos de usuario
│   ├── view/
│   │   ├── VentanaPrincipal.java     # Ventana principal
│   │   ├── PanelArbolArchivos.java   # Panel del árbol de archivos
│   │   ├── PanelDisco.java           # Panel de visualización del disco
│   │   ├── PanelTablaAsignacion.java # Panel de tabla de asignación
│   │   ├── PanelProcesos.java        # Panel de gestión de procesos
│   │   ├── PanelSimulador.java       # Panel del simulador I/O
│   │   ├── PanelBuffer.java          # Panel del buffer
│   │   └── DialogoCrearArchivo.java  # Diálogo para crear archivos
│   └── util/
│       ├── estructuras/
│       │   ├── ListaEnlazada.java    # Lista enlazada genérica
│       │   ├── Cola.java             # Cola genérica
│       │   ├── Pila.java             # Pila genérica
│       │   └── Nodo.java             # Nodo genérico
│       └── excepciones/
│           ├── ArchivoNoEncontradoException.java
│           ├── EspacioInsuficienteException.java
│           └── PermisosDenegadosException.java
├── build/                            # Archivos compilados
└── nbproject/                        # Configuración de NetBeans
```

## Estructuras de Datos Implementadas

El proyecto **no utiliza** las estructuras de datos estándar de Java (`ArrayList`, `LinkedList`, `Queue`, etc.). En su lugar, implementa desde cero las siguientes estructuras genéricas:

- **ListaEnlazada<T>:** Lista enlazada simple con métodos de inserción, eliminación, búsqueda y ordenamiento
- **Cola<T>:** Implementación de cola FIFO con operaciones de encolar y desencolar
- **Pila<T>:** Implementación de pila LIFO con operaciones push y pop
- **Nodo<T>:** Nodo genérico utilizado por todas las estructuras enlazadas

Todas estas estructuras son genéricas y reutilizables en diferentes contextos del sistema.

## Conceptos de Sistemas Operativos Aplicados

- **Asignación encadenada de bloques:** Cada archivo mantiene una lista enlazada de bloques no necesariamente contiguos
- **Tabla de asignación de archivos (FAT):** Registro de todos los archivos y sus bloques asignados
- **Algoritmos de planificación de disco:** Optimización del movimiento del cabezal del disco
- **Estados de procesos:** Modelado del ciclo de vida de un proceso (nuevo, listo, ejecutando, bloqueado, terminado)
- **Solicitudes de E/S:** Cola de operaciones pendientes sobre el disco
- **Buffer de memoria:** Caché para reducir accesos al disco
- **Sistema de permisos:** Control de acceso basado en usuarios y permisos
- **Fragmentación del disco:** Visualización de cómo los bloques se distribuyen en el espacio de almacenamiento

## Decisiones de Diseño

### Asignación de Bloques
Se eligió la **asignación encadenada** en lugar de asignación contigua o indexada porque permite:
- Mejor aprovechamiento del espacio sin necesidad de bloques contiguos
- Crecimiento dinámico de archivos sin relocación
- Implementación directa mediante listas enlazadas

### Planificación de Disco
Se implementaron seis algoritmos diferentes para permitir la comparación de rendimiento y movimiento del cabezal en diferentes escenarios:
- FIFO como línea base simple
- SSTF para minimizar tiempos de búsqueda
- SCAN/C-SCAN para evitar inanición
- LOOK/C-LOOK como optimizaciones de SCAN

### Interfaz Gráfica
Se diseñó una interfaz dividida en múltiples paneles para:
- Separación clara de responsabilidades
- Visualización simultánea de diferentes aspectos del sistema
- Actualización en tiempo real de todos los componentes
- Experiencia de usuario intuitiva y profesional

## Autores

Proyecto desarrollado para la materia de Sistemas Operativos - Universidad Metropolitana

**Trimestre:** 2425-2  
**Preparadores:** Sofia León y Marielena Ginez

## Licencia

Este proyecto es de uso académico para la Universidad Metropolitana.

---

**Nota:** Este simulador es una herramienta educativa diseñada para comprender los conceptos fundamentales de sistemas de archivos y no representa una implementación de sistema de archivos real para uso en producción.
