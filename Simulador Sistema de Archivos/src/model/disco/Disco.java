package model.disco;

import model.archivos.Archivo;
import util.estructuras.ListaEnlazada;
import util.excepciones.EspacioInsuficienteException;
import java.util.Random;

public class Disco {
    private Bloque[] bloques;
    private int tamanioTotal;
    private int bloquesOcupados;
    private int posicionCabezal; 
    private Random random; 
    
    public Disco(int tamanioTotal) {
        this.tamanioTotal = tamanioTotal;
        this.bloques = new Bloque[tamanioTotal];
        this.bloquesOcupados = 0;
        this.posicionCabezal = 0;
        this.random = new Random();
        
        for (int i = 0; i < tamanioTotal; i++) {
            bloques[i] = new Bloque(i);
        }
    }
    
    public ListaEnlazada<Bloque> asignarBloques(Archivo archivo, int cantidadBloques) 
            throws EspacioInsuficienteException {
        
        if (getBloquesLibres() < cantidadBloques) {
            throw new EspacioInsuficienteException(
                "No hay suficiente espacio. Necesitas " + cantidadBloques + 
                " bloques pero solo hay " + getBloquesLibres() + " disponibles."
            );
        }
        
        ListaEnlazada<Bloque> bloquesAsignados = new ListaEnlazada<>();
        ListaEnlazada<Integer> bloquesLibres = new ListaEnlazada<>();

        for (int i = 0; i < tamanioTotal; i++) {
            if (bloques[i].estaLibre()) {
                bloquesLibres.agregarAlFinal(i);
            }
        }
        
        Bloque bloqueAnterior = null;
        
        for (int i = 0; i < cantidadBloques; i++) {
            int indiceAleatorio = random.nextInt(bloquesLibres.getTamanio());
            Integer numeroBloque = bloquesLibres.obtener(indiceAleatorio);
            
            if (numeroBloque != null) {
                Bloque bloque = bloques[numeroBloque];
                bloque.ocupar(archivo);
                bloquesAsignados.agregarAlFinal(bloque);

                if (bloqueAnterior != null) {
                    bloqueAnterior.setSiguiente(bloque);
                }
                
                bloqueAnterior = bloque;
                bloquesOcupados++;
                
                bloquesLibres.eliminarEnPosicion(indiceAleatorio);
            }
        }
        
        return bloquesAsignados;
    }
    
 
    public void liberarBloques(Archivo archivo) {
        ListaEnlazada<Bloque> bloquesArchivo = archivo.getBloques();
        
        for (int i = 0; i < bloquesArchivo.getTamanio(); i++) {
            Bloque bloque = bloquesArchivo.obtener(i);
            if (bloque != null && bloque.estaOcupado()) {
                bloque.liberar();
                bloquesOcupados--;
            }
        }
    }
    
 
    public Bloque obtenerBloque(int numeroBloque) {
        if (numeroBloque >= 0 && numeroBloque < tamanioTotal) {
            return bloques[numeroBloque];
        }
        return null;
    }
    

    public int getBloquesLibres() {
        return tamanioTotal - bloquesOcupados;
    }
    

    public double getPorcentajeUso() {
        return (double) bloquesOcupados / tamanioTotal * 100;
    }
    
   
    public boolean hayEspacioDisponible(int cantidadBloques) {
        return getBloquesLibres() >= cantidadBloques;
    }
    

    public ListaEnlazada<Bloque> obtenerBloquesOcupados() {
        ListaEnlazada<Bloque> ocupados = new ListaEnlazada<>();
        for (int i = 0; i < tamanioTotal; i++) {
            if (bloques[i].estaOcupado()) {
                ocupados.agregarAlFinal(bloques[i]);
            }
        }
        return ocupados;
    }

    public void desfragmentar() {

        ListaEnlazada<Bloque> ocupados = obtenerBloquesOcupados();

        for (int i = 0; i < tamanioTotal; i++) {
            if (bloques[i].estaOcupado()) {
                bloques[i].liberar();
            }
        }
        
        // Reasignar en orden
        int posicion = 0;
        for (int i = 0; i < ocupados.getTamanio(); i++) {
            Bloque bloqueOriginal = ocupados.obtener(i);
            if (bloqueOriginal != null) {
                bloques[posicion].ocupar(bloqueOriginal.getArchivoPropietario());
                bloques[posicion].setDatos(bloqueOriginal.getDatos());
                posicion++;
            }
        }
    }
    
    // Getters y Setters
    public Bloque[] getBloques() {
        return bloques;
    }
    
    public int getTamanioTotal() {
        return tamanioTotal;
    }
    
    public int getBloquesOcupados() {
        return bloquesOcupados;
    }
    
    public int getPosicionCabezal() {
        return posicionCabezal;
    }
    
    public void setPosicionCabezal(int posicionCabezal) {
        if (posicionCabezal >= 0 && posicionCabezal < tamanioTotal) {
            this.posicionCabezal = posicionCabezal;
        }
    }
    
    public String obtenerEstadisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADÍSTICAS DEL DISCO ===\n");
        sb.append("Tamaño total: ").append(tamanioTotal).append(" bloques\n");
        sb.append("Bloques ocupados: ").append(bloquesOcupados).append("\n");
        sb.append("Bloques libres: ").append(getBloquesLibres()).append("\n");
        sb.append("Porcentaje de uso: ").append(String.format("%.2f", getPorcentajeUso())).append("%\n");
        sb.append("Posición del cabezal: ").append(posicionCabezal).append("\n");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "Disco{" +
                "bloques=" + bloquesOcupados + "/" + tamanioTotal +
                ", uso=" + String.format("%.1f", getPorcentajeUso()) + "%" +
                '}';
    }
}