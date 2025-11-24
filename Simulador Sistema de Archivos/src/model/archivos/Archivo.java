package model.archivos;

import model.disco.Bloque;
import util.estructuras.ListaEnlazada;
import java.awt.Color;

public class Archivo extends NodoArbol {
    private int tamanioEnBloques;
    private Bloque primerBloque;
    private ListaEnlazada<Bloque> bloques;
    private String contenido;
    private Color color; // visualizacion en interfaz
    private String procesoCreador; //proceso q creo el archivo
    

    public Archivo(String nombre, Directorio padre, int tamanioEnBloques) {
        super(nombre, padre);
        this.tamanioEnBloques = tamanioEnBloques;
        this.bloques = new ListaEnlazada<>();
        this.contenido = "";
        this.color = generarColorAleatorio();
    }
    
//color aleatorio
    private Color generarColorAleatorio() {
        int r = (int)(Math.random() * 200) + 55;
        int g = (int)(Math.random() * 200) + 55;
        int b = (int)(Math.random() * 200) + 55;
        return new Color(r, g, b);
    }
    
    @Override
    public boolean esDirectorio() {
        return false;
    }
    
    @Override
    public int obtenerTamanio() {
        return tamanioEnBloques;
    }
    
    public void agregarBloque(Bloque bloque) {
        if (bloques.estaVacia()) {
            primerBloque = bloque;
        }
        bloques.agregarAlFinal(bloque);
    }
    
    public int getCantidadBloquesAsignados() {
        return bloques.getTamanio();
    }
    
    public boolean estaCompleto() {
        return bloques.getTamanio() == tamanioEnBloques;
    }
  
    public ListaEnlazada<Integer> getBloquesAsignados() {
        ListaEnlazada<Integer> numeroBloques = new ListaEnlazada<>();
        for (int i = 0; i < bloques.getTamanio(); i++) {
            Bloque bloque = bloques.obtener(i);
            if (bloque != null) {
                numeroBloques.agregarAlFinal(bloque.getNumeroBloque());
            }
        }
        return numeroBloques;
    }
    
    public void liberarBloques() {
        while (!bloques.estaVacia()) {
            Bloque bloque = bloques.eliminarPrimero();
            if (bloque != null) {
                bloque.liberar();
            }
        }
        primerBloque = null;
    }
    
    public int getTamanioEnBloques() {
        return tamanioEnBloques;
    }
    
    public void setTamanioEnBloques(int tamanioEnBloques) {
        this.tamanioEnBloques = tamanioEnBloques;
    }
    
    public Bloque getPrimerBloque() {
        return primerBloque;
    }
    
    public void setPrimerBloque(Bloque primerBloque) {
        this.primerBloque = primerBloque;
    }
    
    public ListaEnlazada<Bloque> getBloques() {
        return bloques;
    }
    
    public String getContenido() {
        return contenido;
    }
    
    public void setContenido(String contenido) {
        this.contenido = contenido;
        actualizarFechaModificacion();
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public String getProcesoCreador() {
        return procesoCreador;
    }
    
    public void setProcesoCreador(String procesoCreador) {
        this.procesoCreador = procesoCreador;
    }
    
    public String obtenerInformacion() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre: ").append(nombre).append("\n");
        sb.append("Tipo: Archivo\n");
        sb.append("Tamaño: ").append(tamanioEnBloques).append(" bloques\n");
        sb.append("Bloques asignados: ").append(bloques.getTamanio()).append("/").append(tamanioEnBloques).append("\n");
        if (primerBloque != null) {
            sb.append("Primer bloque: #").append(primerBloque.getNumeroBloque()).append("\n");
        }
        sb.append("Propietario: ").append(propietario != null ? propietario.getNombre() : "Sistema").append("\n");
        sb.append("Permisos: ").append(permiso).append("\n");
        sb.append("Creación: ").append(formatearFecha(fechaCreacion)).append("\n");
        sb.append("Modificación: ").append(formatearFecha(fechaModificacion)).append("\n");
        sb.append("Ruta: ").append(obtenerRutaCompleta()).append("\n");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return nombre + " (" + tamanioEnBloques + " bloques)";
    }
}