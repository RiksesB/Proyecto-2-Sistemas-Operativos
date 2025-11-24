package controller;

import model.archivos.Archivo;
import model.disco.Bloque;
import model.disco.Disco;
import model.disco.TablaAsignacion;
import util.estructuras.ListaEnlazada;
import util.excepciones.EspacioInsuficienteException;


public class GestorDisco {
    
    private Disco disco;
    private TablaAsignacion tablaAsignacion;
    

    public GestorDisco(int tamanioDiscoEnBloques) {
        this.disco = new Disco(tamanioDiscoEnBloques);
        this.tablaAsignacion = new TablaAsignacion();
    }
    

    public GestorDisco(Disco disco) {
        this.disco = disco;
        this.tablaAsignacion = new TablaAsignacion();
    }
    

    public boolean asignarBloquesAArchivo(Archivo archivo) throws EspacioInsuficienteException {
        int cantidadBloques = archivo.getTamanioEnBloques();
        

        if (!disco.hayEspacioDisponible(cantidadBloques)) {
            throw new EspacioInsuficienteException(
                "No hay suficiente espacio en el disco. Se necesitan " + cantidadBloques + 
                " bloques pero solo hay " + disco.getBloquesLibres() + " disponibles."
            );
        }
        

        ListaEnlazada<Bloque> bloquesAsignados = disco.asignarBloques(archivo, cantidadBloques);
        

        for (int i = 0; i < bloquesAsignados.getTamanio(); i++) {
            Bloque bloque = bloquesAsignados.obtener(i);
            if (bloque != null) {
                archivo.agregarBloque(bloque);
            }
        }
        

        return archivo.estaCompleto();
    }
    

    public void liberarBloquesDeArchivo(Archivo archivo) {

        tablaAsignacion.eliminarArchivo(archivo.getNombre());
        

        disco.liberarBloques(archivo);
        

        archivo.liberarBloques();
    }
    

    public void registrarArchivoEnTabla(Archivo archivo) {
        if (archivo.estaCompleto()) {
            tablaAsignacion.agregarArchivo(archivo);
        }
    }
    

    public boolean hayEspacioDisponible(int cantidadBloques) {
        return disco.hayEspacioDisponible(cantidadBloques);
    }
    
 
    public String obtenerInformacionDisco() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== INFORMACIÓN DEL DISCO ===\n");
        sb.append("Bloques totales: ").append(disco.getTamanioTotal()).append("\n");
        sb.append("Bloques ocupados: ").append(disco.getBloquesOcupados()).append("\n");
        sb.append("Bloques libres: ").append(disco.getBloquesLibres()).append("\n");
        sb.append("Porcentaje de uso: ").append(String.format("%.2f%%", disco.getPorcentajeUso())).append("\n");
        sb.append("Archivos almacenados: ").append(tablaAsignacion.getCantidadArchivos()).append("\n");
        return sb.toString();
    }
    

    public boolean[] obtenerMapaBloques() {
        boolean[] mapa = new boolean[disco.getTamanioTotal()];
        Bloque[] bloques = disco.getBloques();
        
        for (int i = 0; i < bloques.length; i++) {
            mapa[i] = bloques[i].estaOcupado();
        }
        
        return mapa;
    }
    

    public Archivo obtenerPropietarioBloque(int numeroBloque) {
        Bloque bloque = disco.obtenerBloque(numeroBloque);
        if (bloque != null && bloque.estaOcupado()) {
            return bloque.getArchivoPropietario();
        }
        return null;
    }
    
    /**
     * Desfragmenta el disco
     */
    public void desfragmentar() {
        disco.desfragmentar();

    }
    
    // Getters
    public Disco getDisco() {
        return disco;
    }
    
    public TablaAsignacion getTablaAsignacion() {
        return tablaAsignacion;
    }
    
    public int getBloquesLibres() {
        return disco.getBloquesLibres();
    }
    
    public int getBloquesOcupados() {
        return disco.getBloquesOcupados();
    }
    
    public int getTamanioTotal() {
        return disco.getTamanioTotal();
    }
    
    public double getPorcentajeUso() {
        return disco.getPorcentajeUso();
    }
    
    @Override
    public String toString() {
        return "GestorDisco{" +
                "bloques=" + disco.getBloquesOcupados() + "/" + disco.getTamanioTotal() +
                ", archivos=" + tablaAsignacion.getCantidadArchivos() +
                '}';
    }
}
