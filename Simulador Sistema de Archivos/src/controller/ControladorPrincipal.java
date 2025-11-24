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
import util.excepciones.*;

/**
 * coordina todos los gestores del sistema
 */
public class ControladorPrincipal {

    private SistemaArchivos sistema;
    private GestorArchivos gestorArchivos;
    private GestorDisco gestorDisco;
    private GestorProcesos gestorProcesos;
    private GestorPersistencia gestorPersistencia;
    private SimuladorIO simuladorIO;


    public ControladorPrincipal(int tamanioDiscoEnBloques) {

        this.sistema = new SistemaArchivos(tamanioDiscoEnBloques);

      
        this.gestorDisco = new GestorDisco(sistema.getDisco());
        this.gestorArchivos = new GestorArchivos(sistema.getRaiz(), gestorDisco);
        this.gestorProcesos = new GestorProcesos();
        this.gestorPersistencia = new GestorPersistencia();
        this.simuladorIO = new SimuladorIO(gestorProcesos, gestorDisco);

        inicializarSistema();
    }
    
 
    private void inicializarSistema() {
        try {
            Usuario admin = sistema.getUsuarioActual();
            Directorio raiz = sistema.getRaiz();
            
            // Crear algunos directorios por defecto
            gestorArchivos.crearDirectorio("documentos", raiz, admin);
            gestorArchivos.crearDirectorio("programas", raiz, admin);
            gestorArchivos.crearDirectorio("sistema", raiz, admin);
            
        } catch (PermisosDenegadosException e) {
            System.err.println("Error al inicializar sistema: " + e.getMessage());
        }
    }

    public Archivo crearArchivoConProceso(String nombre, Directorio directorio, int tamanioEnBloques) 
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
                "No hay suficiente espacio para crear el archivo. Se necesitan " + 
                tamanioEnBloques + " bloques pero solo hay " + 
                gestorDisco.getBloquesLibres() + " disponibles."
            );
        }
        
      
        Archivo archivo = new Archivo(nombre, directorio, tamanioEnBloques);
        archivo.setPropietario(usuario);
        
       
        Proceso proceso = gestorProcesos.crearProceso(
            "Crear_" + nombre,
            TipoOperacion.CREAR,
            usuario,
            nombre
        );
        
      
        for (int i = 0; i < tamanioEnBloques; i++) {
            
            int bloqueDestino = buscarBloqueSiguienteLibre(i);
            
            SolicitudIO solicitud = new SolicitudIO(
                proceso.getPid(),
                TipoOperacion.CREAR,
                bloqueDestino,
                nombre
            );
            
            gestorProcesos.getColaSolicitudesIO().encolar(solicitud);
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
        

        gestorDisco.asignarBloquesAArchivo(archivo);
     
        directorio.agregarHijo(archivo);
 
        gestorProcesos.terminarProcesoActual();
        
      
        gestorProcesos.limpiarProcesosTerminados();
        
        return archivo;
    }
    
 
    private int buscarBloqueSiguienteLibre(int desde) {
        int total = gestorDisco.getTamanioTotal();
        for (int i = desde; i < total; i++) {
            if (gestorDisco.getDisco().obtenerBloque(i) != null && 
                gestorDisco.getDisco().obtenerBloque(i).estaLibre()) {
                return i;
            }
        }
  
        for (int i = 0; i < desde; i++) {
            if (gestorDisco.getDisco().obtenerBloque(i) != null && 
                gestorDisco.getDisco().obtenerBloque(i).estaLibre()) {
                return i;
            }
        }
        return 0; 
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
        
     
        ListaEnlazada<SolicitudIO> solicitudesOrdenadas = 
            gestorProcesos.procesarSolicitudesIO(gestorDisco.getDisco().getPosicionCabezal());
        
      
        for (int i = 0; i < solicitudesOrdenadas.getTamanio(); i++) {
            SolicitudIO solicitud = solicitudesOrdenadas.obtener(i);
            if (solicitud != null) {
                gestorDisco.getDisco().setPosicionCabezal(solicitud.getBloqueDestino());
            }
        }
        
    
        gestorArchivos.eliminarArchivo(archivo, usuario);
 
        gestorProcesos.terminarProcesoActual();
        gestorProcesos.limpiarProcesosTerminados();
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
        ListaEnlazada<SolicitudIO> solicitudesOrdenadas = 
            gestorProcesos.procesarSolicitudesIO(gestorDisco.getDisco().getPosicionCabezal());

        String resultado = gestorArchivos.leerArchivo(archivo, usuario);
        
   
        gestorProcesos.terminarProcesoActual();
        gestorProcesos.limpiarProcesosTerminados();
        
        return resultado;
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

     
        Proceso proceso = gestorProcesos.crearProceso(
            "Crear_" + nombre,
            TipoOperacion.CREAR,
            usuario,
            nombre
        );

        for (int i = 0; i < tamanioEnBloques; i++) {
            int bloqueDestino = buscarBloqueSiguienteLibre(i);

            SolicitudIO solicitud = new SolicitudIO(
                proceso.getPid(),
                TipoOperacion.CREAR,
                bloqueDestino,
                nombre
            );

            gestorProcesos.getColaSolicitudesIO().encolar(solicitud);
        }


        simuladorIO.setProcesoActual(proceso);
        simuladorIO.iniciarCreacionArchivo(archivo, directorio, tamanioEnBloques);

    
        gestorProcesos.ejecutarSiguienteProceso();

     
        int posicionCabezal = gestorDisco.getDisco().getPosicionCabezal();
        ListaEnlazada<SolicitudIO> solicitudesOrdenadas =
            gestorProcesos.procesarSolicitudesIO(posicionCabezal);

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

     
        gestorDisco.asignarBloquesAArchivo(archivo);
        directorio.agregarHijo(archivo);
        gestorProcesos.terminarProcesoActual();
        gestorProcesos.limpiarProcesosTerminados();
        simuladorIO.reiniciar();
    }

    @Override
    public String toString() {
        return "ControladorPrincipal{" + sistema + "}";
    }
}