package controller;

import model.archivos.Archivo;
import model.archivos.Directorio;
import model.archivos.NodoArbol;
import model.archivos.TipoPermiso;
import model.sistema.Usuario;
import util.estructuras.ListaEnlazada;
import util.excepciones.ArchivoNoEncontradoException;
import util.excepciones.EspacioInsuficienteException;
import util.excepciones.PermisosDenegadosException;


public class GestorArchivos {
    
    private Directorio raiz;
    private GestorDisco gestorDisco;
    

    public GestorArchivos(Directorio raiz, GestorDisco gestorDisco) {
        this.raiz = raiz;
        this.gestorDisco = gestorDisco;
    }
    

    public Archivo crearArchivo(String nombre, Directorio directorioDestino, 
                                int tamanioEnBloques, Usuario usuario) 
            throws EspacioInsuficienteException, PermisosDenegadosException {
        

        if (!usuario.esAdministrador()) {
            throw new PermisosDenegadosException(
                "Solo los administradores pueden crear archivos"
            );
        }
        

        if (directorioDestino.existeHijo(nombre)) {
            throw new IllegalArgumentException(
                "Ya existe un archivo o directorio con ese nombre"
            );
        }
        

        if (!gestorDisco.hayEspacioDisponible(tamanioEnBloques)) {
            throw new EspacioInsuficienteException(
                "No hay suficiente espacio para crear el archivo"
            );
        }
        

        Archivo nuevoArchivo = new Archivo(nombre, directorioDestino, tamanioEnBloques);
        nuevoArchivo.setPropietario(usuario);
        nuevoArchivo.setPermiso(TipoPermiso.PUBLICO);

        gestorDisco.asignarBloquesAArchivo(nuevoArchivo);
        

        directorioDestino.agregarHijo(nuevoArchivo);
        
        return nuevoArchivo;
    }

    public Directorio crearDirectorio(String nombre, Directorio directorioDestino, Usuario usuario) 
            throws PermisosDenegadosException {
        
        // Verificar permisos
        if (!usuario.esAdministrador()) {
            throw new PermisosDenegadosException(
                "Solo los administradores pueden crear directorios"
            );
        }
        
        // Verificar que no exista
        if (directorioDestino.existeHijo(nombre)) {
            throw new IllegalArgumentException(
                "Ya existe un archivo o directorio con ese nombre"
            );
        }
        
        // Crear el directorio
        Directorio nuevoDirectorio = new Directorio(nombre, directorioDestino);
        nuevoDirectorio.setPropietario(usuario);
        nuevoDirectorio.setPermiso(TipoPermiso.PUBLICO);
        
        // Agregar al directorio padre
        directorioDestino.agregarHijo(nuevoDirectorio);
        
        return nuevoDirectorio;
    }
    

    public String leerArchivo(Archivo archivo, Usuario usuario) 
            throws PermisosDenegadosException {
        
        // Verificar permisos de lectura
        if (!puedeAcceder(archivo, usuario)) {
            throw new PermisosDenegadosException(
                "No tiene permisos para acceder a este archivo"
            );
        }
        
        return archivo.obtenerInformacion();
    }

    public void actualizarNombreArchivo(Archivo archivo, String nuevoNombre, Usuario usuario) 
            throws PermisosDenegadosException {
        
        // Solo administradores pueden modificar
        if (!usuario.esAdministrador()) {
            throw new PermisosDenegadosException(
                "Solo los administradores pueden modificar archivos"
            );
        }
        
        // Verificar que no exista otro archivo con ese nombre en el mismo directorio
        Directorio padre = archivo.getPadre();
        if (padre != null && padre.existeHijo(nuevoNombre)) {
            throw new IllegalArgumentException(
                "Ya existe un archivo con ese nombre en este directorio"
            );
        }
        
        archivo.setNombre(nuevoNombre);
    }
    

    public void actualizarNombreDirectorio(Directorio directorio, String nuevoNombre, Usuario usuario) 
            throws PermisosDenegadosException {
        
        if (!usuario.esAdministrador()) {
            throw new PermisosDenegadosException(
                "Solo los administradores pueden modificar directorios"
            );
        }
        
        Directorio padre = directorio.getPadre();
        if (padre != null && padre.existeHijo(nuevoNombre)) {
            throw new IllegalArgumentException(
                "Ya existe un directorio con ese nombre"
            );
        }
        
        directorio.setNombre(nuevoNombre);
    }
    

    public void eliminarArchivo(Archivo archivo, Usuario usuario) 
            throws PermisosDenegadosException {
        
        if (!usuario.esAdministrador()) {
            throw new PermisosDenegadosException(
                "Solo los administradores pueden eliminar archivos"
            );
        }
        
        // Liberar bloques del disco
        gestorDisco.liberarBloquesDeArchivo(archivo);
        
        // Eliminar del directorio padre
        Directorio padre = archivo.getPadre();
        if (padre != null) {
            padre.eliminarHijo(archivo);
        }
    }
    

    public void eliminarDirectorio(Directorio directorio, Usuario usuario) 
            throws PermisosDenegadosException {
        
        if (!usuario.esAdministrador()) {
            throw new PermisosDenegadosException(
                "Solo los administradores pueden eliminar directorios"
            );
        }
        
        // Eliminar recursivamente todos los hijos
        ListaEnlazada<NodoArbol> hijos = directorio.getHijos();
        while (!hijos.estaVacia()) {
            NodoArbol hijo = hijos.obtenerPrimero();
            
            if (hijo.esDirectorio()) {
                eliminarDirectorio((Directorio) hijo, usuario);
            } else {
                eliminarArchivo((Archivo) hijo, usuario);
            }
        }
        
        // Eliminar del directorio padre
        Directorio padre = directorio.getPadre();
        if (padre != null) {
            padre.eliminarHijo(directorio);
        }
    }

    public Archivo buscarArchivo(String nombre) throws ArchivoNoEncontradoException {
        Archivo resultado = buscarArchivoRecursivo(raiz, nombre);
        if (resultado == null) {
            throw new ArchivoNoEncontradoException("No se encontró el archivo: " + nombre);
        }
        return resultado;
    }
    

    private Archivo buscarArchivoRecursivo(Directorio directorio, String nombre) {
        ListaEnlazada<NodoArbol> hijos = directorio.getHijos();
        
        for (int i = 0; i < hijos.getTamanio(); i++) {
            NodoArbol hijo = hijos.obtener(i);
            if (hijo != null) {
                if (!hijo.esDirectorio() && hijo.getNombre().equals(nombre)) {
                    return (Archivo) hijo;
                } else if (hijo.esDirectorio()) {
                    Archivo resultado = buscarArchivoRecursivo((Directorio) hijo, nombre);
                    if (resultado != null) {
                        return resultado;
                    }
                }
            }
        }
        
        return null;
    }
    

    public Directorio buscarDirectorio(String nombre) throws ArchivoNoEncontradoException {
        Directorio resultado = buscarDirectorioRecursivo(raiz, nombre);
        if (resultado == null) {
            throw new ArchivoNoEncontradoException("No se encontró el directorio: " + nombre);
        }
        return resultado;
    }
    
 
    private Directorio buscarDirectorioRecursivo(Directorio directorio, String nombre) {
        if (directorio.getNombre().equals(nombre)) {
            return directorio;
        }
        
        ListaEnlazada<NodoArbol> hijos = directorio.getHijos();
        for (int i = 0; i < hijos.getTamanio(); i++) {
            NodoArbol hijo = hijos.obtener(i);
            if (hijo != null && hijo.esDirectorio()) {
                Directorio resultado = buscarDirectorioRecursivo((Directorio) hijo, nombre);
                if (resultado != null) {
                    return resultado;
                }
            }
        }
        
        return null;
    }
    

    public ListaEnlazada<Archivo> obtenerTodosLosArchivos() {
        ListaEnlazada<Archivo> archivos = new ListaEnlazada<>();
        obtenerArchivosRecursivo(raiz, archivos);
        return archivos;
    }
    
 
    private void obtenerArchivosRecursivo(Directorio directorio, ListaEnlazada<Archivo> archivos) {
        ListaEnlazada<NodoArbol> hijos = directorio.getHijos();
        
        for (int i = 0; i < hijos.getTamanio(); i++) {
            NodoArbol hijo = hijos.obtener(i);
            if (hijo != null) {
                if (!hijo.esDirectorio()) {
                    archivos.agregarAlFinal((Archivo) hijo);
                } else {
                    obtenerArchivosRecursivo((Directorio) hijo, archivos);
                }
            }
        }
    }
    

    private boolean puedeAcceder(Archivo archivo, Usuario usuario) {
        // Los administradores siempre pueden acceder
        if (usuario.esAdministrador()) {
            return true;
        }
        
        // Si el archivo es público, todos pueden leer
        if (archivo.getPermiso() == TipoPermiso.PUBLICO) {
            return true;
        }
        
        // Si es el propietario
        if (archivo.getPropietario() != null && 
            archivo.getPropietario().equals(usuario)) {
            return true;
        }
        
        return false;
    }
    

    public void cambiarPermisos(Archivo archivo, TipoPermiso nuevoPermiso, Usuario usuario) 
            throws PermisosDenegadosException {
        
        if (!usuario.esAdministrador()) {
            throw new PermisosDenegadosException(
                "Solo los administradores pueden cambiar permisos"
            );
        }
        
        archivo.setPermiso(nuevoPermiso);
    }
    

    public Directorio getRaiz() {
        return raiz;
    }
    
    public GestorDisco getGestorDisco() {
        return gestorDisco;
    }
    
    @Override
    public String toString() {
        return "GestorArchivos{archivos=" + obtenerTodosLosArchivos().getTamanio() + "}";
    }
}