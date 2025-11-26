package controller;

import model.sistema.SistemaArchivos;
import model.sistema.Usuario;
import model.sistema.TipoUsuario;
import model.archivos.Directorio;
import model.archivos.Archivo;
import model.procesos.Proceso;
import model.procesos.TipoOperacion;
import model.procesos.SolicitudIO;
import model.planificacion.TipoPlanificacion;
import util.estructuras.ListaEnlazada;
import util.estructuras.Cola;
import util.excepciones.*;

public class ControladorPrincipal {

    private SistemaArchivos sistema;
    private GestorArchivos gestorArchivos;
    private GestorDisco gestorDisco;
    private GestorProcesos gestorProcesos;
    private GestorPersistencia gestorPersistencia;
    private SimuladorIO simuladorIO;
    private GestorBuffer gestorBuffer;
    
    private Cola<ArchivoPendiente> colaArchivosPendientes;
    
    private class ArchivoPendiente {
        Archivo archivo;
        Directorio directorio;
        Proceso proceso;
        
        ArchivoPendiente(Archivo archivo, Directorio directorio, Proceso proceso) {
            this.archivo = archivo;
            this.directorio = directorio;
            this.proceso = proceso;
        }
    }

    public ControladorPrincipal(int tamanioDiscoEnBloques) {
        this.sistema = new SistemaArchivos(tamanioDiscoEnBloques);

        this.gestorDisco = new GestorDisco(sistema.getDisco());
        this.gestorArchivos = new GestorArchivos(sistema.getRaiz(), gestorDisco);
        this.gestorProcesos = new GestorProcesos();
        this.gestorPersistencia = new GestorPersistencia();
        this.simuladorIO = new SimuladorIO(gestorProcesos, gestorDisco);
        
        this.gestorBuffer = new GestorBuffer(20, GestorBuffer.PoliticaReemplazo.FIFO);
        
        this.colaArchivosPendientes = new Cola<>();

        inicializarSistema();
    }
    
    private void inicializarSistema() {
        try {
            Usuario admin = sistema.getUsuarioActual();
            Directorio raiz = sistema.getRaiz();
            
            gestorArchivos.crearDirectorio("documentos", raiz, admin);
            gestorArchivos.crearDirectorio("programas", raiz, admin);
            gestorArchivos.crearDirectorio("sistema", raiz, admin);
            
        } catch (PermisosDenegadosException e) {
            System.err.println("Error al inicializar sistema: " + e.getMessage());
        }
    }
    
    public Archivo crearArchivoConProceso(String nombre, Directorio directorio, int tamanioEnBloques) 
            throws EspacioInsuficienteException, PermisosDenegadosException {
        
        if (gestorProcesos.getProcesoEnEjecucion() != null) {
            gestorProcesos.terminarProcesoActual();
        }
        
        Usuario usuario = sistema.getUsuarioActual();
        
        if (!usuario.esAdministrador()) {
            throw new PermisosDenegadosException(
                "Solo los administradores pueden crear archivos"
            );
        }
        
        if (directorio.existeHijo(nombre)) {
            throw new IllegalArgumentException(
                "Ya existe un archivo o directorio con ese nombre"
            );
        }
        
        if (!gestorDisco.hayEspacioDisponible(tamanioEnBloques)) {
            throw new EspacioInsuficienteException(
                "No hay suficiente espacio para crear el archivo. Se necesitan " + 
                tamanioEnBloques + " bloques pero solo hay " + 
                gestorDisco.getBloquesLibres() + " disponibles."
            );
        }
        
        Archivo archivo = new Archivo(nombre, directorio, tamanioEnBloques);
        archivo.setPropietario(usuario);
        
        gestorDisco.asignarBloquesAArchivo(archivo);
        
        Proceso proceso = gestorProcesos.crearProceso(
            "Crear_" + nombre,
            TipoOperacion.CREAR,
            usuario,
            nombre
        );
        
        archivo.setProcesoCreador(proceso.getNombre());
        
        gestorDisco.registrarArchivoEnTabla(archivo);
        
        ListaEnlazada<Integer> bloquesAsignados = archivo.getBloquesAsignados();
        for (int i = 0; i < bloquesAsignados.getTamanio(); i++) {
            Integer numeroBloque = bloquesAsignados.obtener(i);
            if (numeroBloque != null) {
                SolicitudIO solicitud = new SolicitudIO(
                    proceso.getPid(),
                    TipoOperacion.CREAR,
                    numeroBloque,
                    nombre
                );
                gestorProcesos.getColaSolicitudesIO().encolar(solicitud);
            }
        }
        
        gestorProcesos.ejecutarSiguienteProceso();
        
        int posicionCabezal = gestorDisco.getDisco().getPosicionCabezal();
        ListaEnlazada<SolicitudIO> solicitudesOrdenadas = 
            gestorProcesos.procesarSolicitudesIO(posicionCabezal);
        
        StringBuilder bloquesIO = new StringBuilder();
        int movimientoTotal = 0;
        int posicionActual = posicionCabezal;
        
        for (int i = 0; i < solicitudesOrdenadas.getTamanio(); i++) {
            SolicitudIO solicitud = solicitudesOrdenadas.obtener(i);
            if (solicitud != null) {
                int bloqueDestino = solicitud.getBloqueDestino();
                
                int movimiento = Math.abs(bloqueDestino - posicionActual);
                movimientoTotal += movimiento;
                posicionActual = bloqueDestino;
                
                if (bloquesIO.length() > 0) {
                    bloquesIO.append(" → ");
                }
                bloquesIO.append(bloqueDestino);
                
                gestorDisco.getDisco().setPosicionCabezal(bloqueDestino);
            }
        }
        
        proceso.setBloquesIO(bloquesIO.toString());
        proceso.setMovimientoCabezal(movimientoTotal);
        
        directorio.agregarHijo(archivo);
        
        gestorProcesos.terminarProceso(proceso);
        
        return archivo;
    }
    
    public void eliminarArchivoConProceso(Archivo archivo) throws PermisosDenegadosException {
        Usuario usuario = sistema.getUsuarioActual();
        
        if (!usuario.esAdministrador()) {
            throw new PermisosDenegadosException(
                "Solo los administradores pueden eliminar archivos"
            );
        }
        
        Proceso proceso = gestorProcesos.crearProceso(
            "Eliminar_" + archivo.getNombre(),
            TipoOperacion.ELIMINAR,
            usuario,
            archivo.getNombre()
        );
        
        ListaEnlazada<model.disco.Bloque> bloques = archivo.getBloques();
        for (int i = 0; i < bloques.getTamanio(); i++) {
            model.disco.Bloque bloque = bloques.obtener(i);
            if (bloque != null) {
                SolicitudIO solicitud = new SolicitudIO(
                    proceso.getPid(),
                    TipoOperacion.ELIMINAR,
                    bloque.getNumeroBloque(),
                    archivo.getNombre()
                );
                gestorProcesos.getColaSolicitudesIO().encolar(solicitud);
            }
        }
        
        gestorProcesos.ejecutarSiguienteProceso();
        
        int posicionCabezal = gestorDisco.getDisco().getPosicionCabezal();
        ListaEnlazada<SolicitudIO> solicitudesOrdenadas = 
            gestorProcesos.procesarSolicitudesIO(posicionCabezal);
        
        StringBuilder bloquesIO = new StringBuilder();
        int movimientoTotal = 0;
        int posicionActual = posicionCabezal;
        
        for (int i = 0; i < solicitudesOrdenadas.getTamanio(); i++) {
            SolicitudIO solicitud = solicitudesOrdenadas.obtener(i);
            if (solicitud != null) {
                int bloqueDestino = solicitud.getBloqueDestino();
                
                int movimiento = Math.abs(bloqueDestino - posicionActual);
                movimientoTotal += movimiento;
                posicionActual = bloqueDestino;
                
                if (bloquesIO.length() > 0) {
                    bloquesIO.append(" → ");
                }
                bloquesIO.append(bloqueDestino);
                
                gestorDisco.getDisco().setPosicionCabezal(bloqueDestino);
            }
        }
        
        proceso.setBloquesIO(bloquesIO.toString());
        proceso.setMovimientoCabezal(movimientoTotal);
        
        gestorArchivos.eliminarArchivo(archivo, usuario);
        
        gestorProcesos.terminarProceso(proceso);
    }
    
    public String leerArchivoConProceso(Archivo archivo) throws PermisosDenegadosException {
        Usuario usuario = sistema.getUsuarioActual();
        
        Proceso proceso = gestorProcesos.crearProceso(
            "Leer_" + archivo.getNombre(),
            TipoOperacion.LEER,
            usuario,
            archivo.getNombre()
        );
        
        ListaEnlazada<model.disco.Bloque> bloques = archivo.getBloques();
        for (int i = 0; i < bloques.getTamanio(); i++) {
            model.disco.Bloque bloque = bloques.obtener(i);
            if (bloque != null) {
                SolicitudIO solicitud = new SolicitudIO(
                    proceso.getPid(),
                    TipoOperacion.LEER,
                    bloque.getNumeroBloque(),
                    archivo.getNombre()
                );
                gestorProcesos.getColaSolicitudesIO().encolar(solicitud);
            }
        }
        
        gestorProcesos.ejecutarSiguienteProceso();
        
        int posicionCabezal = gestorDisco.getDisco().getPosicionCabezal();
        ListaEnlazada<SolicitudIO> solicitudesOrdenadas = 
            gestorProcesos.procesarSolicitudesIO(posicionCabezal);
        
        StringBuilder bloquesIO = new StringBuilder();
        int movimientoTotal = 0;
        int posicionActual = posicionCabezal;
        
        for (int i = 0; i < solicitudesOrdenadas.getTamanio(); i++) {
            SolicitudIO solicitud = solicitudesOrdenadas.obtener(i);
            if (solicitud != null) {
                int bloqueDestino = solicitud.getBloqueDestino();
                
                int movimiento = Math.abs(bloqueDestino - posicionActual);
                movimientoTotal += movimiento;
                posicionActual = bloqueDestino;
                
                if (bloquesIO.length() > 0) {
                    bloquesIO.append(" → ");
                }
                bloquesIO.append(bloqueDestino);
                
                gestorDisco.getDisco().setPosicionCabezal(bloqueDestino);
            }
        }
        
        proceso.setBloquesIO(bloquesIO.toString());
        proceso.setMovimientoCabezal(movimientoTotal);
        
        String resultado = gestorArchivos.leerArchivo(archivo, usuario);
        
        gestorProcesos.terminarProceso(proceso);
        
        return resultado;
    }
    
    public void renombrarArchivoConProceso(Archivo archivo, String nuevoNombre) 
            throws PermisosDenegadosException {
        Usuario usuario = sistema.getUsuarioActual();
        String nombreAnterior = archivo.getNombre();
        
        Proceso proceso = gestorProcesos.crearProceso(
            "Renombrar_" + nombreAnterior + "_a_" + nuevoNombre,
            TipoOperacion.RENOMBRAR,
            usuario,
            nombreAnterior
        );
        
        ListaEnlazada<model.disco.Bloque> bloques = archivo.getBloques();
        for (int i = 0; i < bloques.getTamanio(); i++) {
            model.disco.Bloque bloque = bloques.obtener(i);
            if (bloque != null) {
                SolicitudIO solicitud = new SolicitudIO(
                    proceso.getPid(),
                    TipoOperacion.RENOMBRAR,
                    bloque.getNumeroBloque(),
                    nombreAnterior
                );
                gestorProcesos.getColaSolicitudesIO().encolar(solicitud);
            }
        }
        
        gestorProcesos.ejecutarSiguienteProceso();
        
        int posicionCabezal = gestorDisco.getDisco().getPosicionCabezal();
        ListaEnlazada<SolicitudIO> solicitudesOrdenadas = 
            gestorProcesos.procesarSolicitudesIO(posicionCabezal);
        
        StringBuilder bloquesIO = new StringBuilder();
        int movimientoTotal = 0;
        int posicionActual = posicionCabezal;
        
        for (int i = 0; i < solicitudesOrdenadas.getTamanio(); i++) {
            SolicitudIO solicitud = solicitudesOrdenadas.obtener(i);
            if (solicitud != null) {
                int bloqueDestino = solicitud.getBloqueDestino();
                
                int movimiento = Math.abs(bloqueDestino - posicionActual);
                movimientoTotal += movimiento;
                posicionActual = bloqueDestino;
                
                if (bloquesIO.length() > 0) {
                    bloquesIO.append(" → ");
                }
                bloquesIO.append(bloqueDestino);
                
                gestorDisco.getDisco().setPosicionCabezal(bloqueDestino);
            }
        }
        
        proceso.setBloquesIO(bloquesIO.toString());
        proceso.setMovimientoCabezal(movimientoTotal);
        
        gestorDisco.getTablaAsignacion().eliminarArchivo(nombreAnterior);
        
        gestorArchivos.actualizarNombreArchivo(archivo, nuevoNombre, usuario);
        
        gestorDisco.registrarArchivoEnTabla(archivo);
        
        gestorProcesos.terminarProceso(proceso);
    }
    
    public void renombrarDirectorioConProceso(Directorio directorio, String nuevoNombre) 
            throws PermisosDenegadosException {
        Usuario usuario = sistema.getUsuarioActual();
        String nombreAnterior = directorio.getNombre();
        
        gestorProcesos.crearProceso(
            "Renombrar_DIR_" + nombreAnterior + "_a_" + nuevoNombre,
            TipoOperacion.RENOMBRAR,
            usuario,
            nombreAnterior
        );
        
        gestorArchivos.actualizarNombreDirectorio(directorio, nuevoNombre, usuario);
        
        Proceso proceso = gestorProcesos.getProcesoEnEjecucion();
        if (proceso != null) {
            gestorProcesos.terminarProceso(proceso);
        }
    }
    
    public void cambiarUsuario(String nombreUsuario, TipoUsuario tipo) {
        Usuario nuevoUsuario = buscarUsuario(nombreUsuario);
        
        if (nuevoUsuario == null) {
            nuevoUsuario = new Usuario(nombreUsuario, tipo);
            sistema.agregarUsuario(nuevoUsuario);
        }
        
        sistema.cambiarUsuario(nuevoUsuario);
    }
    
    private Usuario buscarUsuario(String nombre) {
        for (int i = 0; i < sistema.getUsuarios().getTamanio(); i++) {
            Usuario usuario = sistema.getUsuarios().obtener(i);
            if (usuario != null && usuario.getNombre().equals(nombre)) {
                return usuario;
            }
        }
        return null;
    }
    
    public void cambiarAlgoritmoPlanificacion(TipoPlanificacion tipo) {
        gestorProcesos.cambiarAlgoritmoPlanificacion(tipo);
    }
    
    public boolean guardarSistema() {
        return gestorPersistencia.guardarSistema(sistema);
    }
    
    public boolean cargarSistema() {
        SistemaArchivos sistemaCargado = gestorPersistencia.cargarSistema(
            sistema.getDisco().getTamanioTotal()
        );
        
        if (sistemaCargado != null) {
            this.sistema = sistemaCargado;
            this.gestorDisco = new GestorDisco(sistema.getDisco());
            this.gestorArchivos = new GestorArchivos(sistema.getRaiz(), gestorDisco);
            return true;
        }
        
        return false;
    }
    
    public String obtenerEstadisticasCompletas() {
        StringBuilder sb = new StringBuilder();
        sb.append(sistema.obtenerEstadisticas()).append("\n");
        sb.append(gestorDisco.obtenerInformacionDisco()).append("\n");
        sb.append(gestorProcesos.obtenerEstadisticas()).append("\n");
        sb.append("\n=== ALGORITMO DE PLANIFICACIÓN ACTUAL ===\n");
        sb.append(gestorProcesos.getGestorPlanificacion().getNombreAlgoritmoActual()).append("\n");
        sb.append(gestorProcesos.getGestorPlanificacion().getDescripcionAlgoritmoActual()).append("\n");
        return sb.toString();
    }
    
    public SistemaArchivos getSistema() {
        return sistema;
    }
    
    public GestorArchivos getGestorArchivos() {
        return gestorArchivos;
    }
    
    public GestorDisco getGestorDisco() {
        return gestorDisco;
    }
    
    public GestorProcesos getGestorProcesos() {
        return gestorProcesos;
    }
    
    public GestorPersistencia getGestorPersistencia() {
        return gestorPersistencia;
    }

    public SimuladorIO getSimuladorIO() {
        return simuladorIO;
    }

    public GestorBuffer getGestorBuffer() {
        return gestorBuffer;
    }
    
    public void cambiarPoliticaBuffer(GestorBuffer.PoliticaReemplazo politica) {
        gestorBuffer.cambiarPolitica(politica);
    }
    
    public String leerBloqueConBuffer(int numeroBloque) {
        String datos = gestorBuffer.buscarBloque(numeroBloque);
        
        if (datos != null) {
            return datos;
        }
        
        model.disco.Bloque bloque = gestorDisco.getDisco().obtenerBloque(numeroBloque);
        if (bloque != null && bloque.estaOcupado()) {
            datos = bloque.getDatos();
            gestorBuffer.agregarBloque(numeroBloque, datos);
            return datos;
        }
        
        return null;
    }
    
    public void escribirBloqueConBuffer(int numeroBloque, String datos) {
        model.disco.Bloque bloque = gestorDisco.getDisco().obtenerBloque(numeroBloque);
        if (bloque != null) {
            bloque.setDatos(datos);
            gestorBuffer.agregarBloque(numeroBloque, datos);
        }
    }

    public Archivo crearArchivoConSimulacion(String nombre, Directorio directorio, int tamanioEnBloques)
            throws EspacioInsuficienteException, PermisosDenegadosException {

        Usuario usuario = sistema.getUsuarioActual();

        if (!usuario.esAdministrador()) {
            throw new PermisosDenegadosException(
                "Solo los administradores pueden crear archivos"
            );
        }

        if (directorio.existeHijo(nombre)) {
            throw new IllegalArgumentException(
                "Ya existe un archivo o directorio con ese nombre"
            );
        }

        if (!gestorDisco.hayEspacioDisponible(tamanioEnBloques)) {
            throw new EspacioInsuficienteException(
                "No hay suficiente espacio para crear el archivo"
            );
        }

        Archivo archivo = new Archivo(nombre, directorio, tamanioEnBloques);
        archivo.setPropietario(usuario);

        gestorDisco.asignarBloquesAArchivo(archivo);

        Proceso proceso = gestorProcesos.crearProceso(
            "Crear_" + nombre,
            TipoOperacion.CREAR,
            usuario,
            nombre
        );
        
        archivo.setProcesoCreador(proceso.getNombre());
        
        gestorDisco.registrarArchivoEnTabla(archivo);

        ListaEnlazada<Integer> bloquesAsignados = archivo.getBloquesAsignados();
        for (int i = 0; i < bloquesAsignados.getTamanio(); i++) {
            Integer numeroBloque = bloquesAsignados.obtener(i);
            if (numeroBloque != null) {
                SolicitudIO solicitud = new SolicitudIO(
                    proceso.getPid(),
                    TipoOperacion.CREAR,
                    numeroBloque,
                    nombre
                );
                gestorProcesos.getColaSolicitudesIO().encolar(solicitud);
            }
        }

        colaArchivosPendientes.encolar(new ArchivoPendiente(archivo, directorio, proceso));

        simuladorIO.setProcesoActual(proceso);
        simuladorIO.iniciarCreacionArchivo(archivo, directorio, tamanioEnBloques);

        gestorProcesos.ejecutarSiguienteProceso();

        int posicionCabezal = gestorDisco.getDisco().getPosicionCabezal();
        ListaEnlazada<SolicitudIO> solicitudesOrdenadas =
            gestorProcesos.procesarSolicitudesIO(posicionCabezal);

        int movimientoTotal = 0;
        int posicionActual = posicionCabezal;
        
        for (int i = 0; i < solicitudesOrdenadas.getTamanio(); i++) {
            SolicitudIO solicitud = solicitudesOrdenadas.obtener(i);
            if (solicitud != null) {
                int bloqueDestino = solicitud.getBloqueDestino();
                int movimiento = Math.abs(bloqueDestino - posicionActual);
                movimientoTotal += movimiento;
                posicionActual = bloqueDestino;
            }
        }
        
        proceso.setMovimientoCabezal(movimientoTotal);

        simuladorIO.setSolicitudesPendientes(solicitudesOrdenadas);

        return archivo;
    }

    public boolean avanzarSimulacion() {
        return simuladorIO.avanzarCiclo();
    }

    public void completarSimulacion(Archivo archivo, Directorio directorio)
            throws EspacioInsuficienteException {
        while (simuladorIO.getEstadoActual() != SimuladorIO.EstadoSimulacion.COMPLETADO) {
            simuladorIO.avanzarCiclo();
        }

        while (!colaArchivosPendientes.estaVacia()) {
            ArchivoPendiente pendiente = colaArchivosPendientes.desencolar();
            
            int posicionCabezal = gestorDisco.getDisco().getPosicionCabezal();
            
            ListaEnlazada<Integer> bloquesAsignados = pendiente.archivo.getBloquesAsignados();
            
            for (int i = 0; i < bloquesAsignados.getTamanio(); i++) {
                Integer numeroBloque = bloquesAsignados.obtener(i);
                if (numeroBloque != null) {
                    SolicitudIO solicitud = new SolicitudIO(
                        pendiente.proceso.getPid(),
                        TipoOperacion.CREAR,
                        numeroBloque,
                        pendiente.archivo.getNombre()
                    );
                    gestorProcesos.getColaSolicitudesIO().encolar(solicitud);
                }
            }
            
            ListaEnlazada<SolicitudIO> solicitudesOrdenadas = 
                gestorProcesos.procesarSolicitudesIO(posicionCabezal);
            
            StringBuilder bloquesIO = new StringBuilder();
            int movimientoTotal = 0;
            int posicionActual = posicionCabezal;
            
            for (int i = 0; i < solicitudesOrdenadas.getTamanio(); i++) {
                SolicitudIO solicitud = solicitudesOrdenadas.obtener(i);
                if (solicitud != null) {
                    int bloqueDestino = solicitud.getBloqueDestino();
                    
                    if (bloquesIO.length() > 0) {
                        bloquesIO.append(" → ");
                    }
                    bloquesIO.append(bloqueDestino);
                    
                    int movimiento = Math.abs(bloqueDestino - posicionActual);
                    movimientoTotal += movimiento;
                    posicionActual = bloqueDestino;
                    
                    gestorDisco.getDisco().setPosicionCabezal(bloqueDestino);
                    String datos = "Datos del archivo " + pendiente.archivo.getNombre();
                    gestorBuffer.agregarBloque(bloqueDestino, datos);
                }
            }
            
            pendiente.proceso.setBloquesIO(bloquesIO.toString());
            pendiente.proceso.setMovimientoCabezal(movimientoTotal);
            
            pendiente.directorio.agregarHijo(pendiente.archivo);
        }
        
        gestorProcesos.terminarProcesoActual();
        
        simuladorIO.reiniciar();
    }

    public void actualizarArchivoConProceso(Archivo archivo, String nuevoContenido) 
            throws PermisosDenegadosException {
        
        Usuario usuario = sistema.getUsuarioActual();
        
        if (!usuario.esAdministrador()) {
            throw new PermisosDenegadosException(
                "Solo los administradores pueden actualizar archivos"
            );
        }
        
        Proceso proceso = gestorProcesos.crearProceso(
            "Actualizar_" + archivo.getNombre(),
            TipoOperacion.ACTUALIZAR,
            usuario,
            archivo.getNombre()
        );
        
        ListaEnlazada<model.disco.Bloque> bloques = archivo.getBloques();
        for (int i = 0; i < bloques.getTamanio(); i++) {
            model.disco.Bloque bloque = bloques.obtener(i);
            if (bloque != null) {
                SolicitudIO solicitud = new SolicitudIO(
                    proceso.getPid(),
                    TipoOperacion.ACTUALIZAR,
                    bloque.getNumeroBloque(),
                    archivo.getNombre()
                );
                gestorProcesos.getColaSolicitudesIO().encolar(solicitud);
            }
        }
        
        gestorProcesos.ejecutarSiguienteProceso();
        
        int posicionCabezal = gestorDisco.getDisco().getPosicionCabezal();
        ListaEnlazada<SolicitudIO> solicitudesOrdenadas = 
            gestorProcesos.procesarSolicitudesIO(posicionCabezal);
        
        for (int i = 0; i < solicitudesOrdenadas.getTamanio(); i++) {
            SolicitudIO solicitud = solicitudesOrdenadas.obtener(i);
            if (solicitud != null) {
                gestorDisco.getDisco().setPosicionCabezal(solicitud.getBloqueDestino());
            }
        }
        
        archivo.setContenido(nuevoContenido);
        archivo.actualizarFechaModificacion();
        
        gestorProcesos.terminarProcesoActual();
    }

    public void actualizarArchivoConSimulacion(Archivo archivo, String nuevoContenido) 
            throws PermisosDenegadosException {
        
        Usuario usuario = sistema.getUsuarioActual();
        
        if (!usuario.esAdministrador()) {
            throw new PermisosDenegadosException(
                "Solo los administradores pueden actualizar archivos"
            );
        }
        
        Proceso proceso = gestorProcesos.crearProceso(
            "Actualizar_" + archivo.getNombre(),
            TipoOperacion.ACTUALIZAR,
            usuario,
            archivo.getNombre()
        );
        
        ListaEnlazada<model.disco.Bloque> bloques = archivo.getBloques();
        for (int i = 0; i < bloques.getTamanio(); i++) {
            model.disco.Bloque bloque = bloques.obtener(i);
            if (bloque != null) {
                SolicitudIO solicitud = new SolicitudIO(
                    proceso.getPid(),
                    TipoOperacion.ACTUALIZAR,
                    bloque.getNumeroBloque(),
                    archivo.getNombre()
                );
                gestorProcesos.getColaSolicitudesIO().encolar(solicitud);
            }
        }
        
        simuladorIO.setProcesoActual(proceso);
        simuladorIO.iniciarActualizacionArchivo(archivo, nuevoContenido);
        
        gestorProcesos.ejecutarSiguienteProceso();
        
        int posicionCabezal = gestorDisco.getDisco().getPosicionCabezal();
        ListaEnlazada<SolicitudIO> solicitudesOrdenadas = 
            gestorProcesos.procesarSolicitudesIO(posicionCabezal);
        
        simuladorIO.setSolicitudesPendientes(solicitudesOrdenadas);
    }

    public void completarActualizacion(Archivo archivo, String nuevoContenido) {
        while (simuladorIO.getEstadoActual() != SimuladorIO.EstadoSimulacion.COMPLETADO) {
            simuladorIO.avanzarCiclo();
        }
        
        archivo.setContenido(nuevoContenido);
        archivo.actualizarFechaModificacion();
        gestorProcesos.terminarProcesoActual();
        
        simuladorIO.reiniciar();
    }

    @Override
    public String toString() {
        return "ControladorPrincipal{" + sistema + "}";
    }
}