package controller;

import util.estructuras.ListaEnlazada;

public class GestorBuffer {
    
    public enum PoliticaReemplazo {
        FIFO("First In First Out - El primero que entra es el primero en salir"),
        LRU("Least Recently Used - El menos recientemente usado"),
        LFU("Least Frequently Used - El menos frecuentemente usado");
        
        private final String descripcion;
        
        PoliticaReemplazo(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    private static class EntradaBuffer {
        int numeroBloque;
        String datos;
        long ultimoAcceso;
        int frecuenciaAcceso;
        
        public EntradaBuffer(int numeroBloque, String datos) {
            this.numeroBloque = numeroBloque;
            this.datos = datos;
            this.ultimoAcceso = System.currentTimeMillis();
            this.frecuenciaAcceso = 1;
        }
        
        public void actualizarAcceso() {
            this.ultimoAcceso = System.currentTimeMillis();
            this.frecuenciaAcceso++;
        }
    }
    
    private ListaEnlazada<EntradaBuffer> buffer;
    private int tamanioMaximo;
    private PoliticaReemplazo politica;
    
    private int hits;
    private int misses;
    private int reemplazos;
    
    public GestorBuffer(int tamanioMaximo, PoliticaReemplazo politica) {
        this.buffer = new ListaEnlazada<>();
        this.tamanioMaximo = tamanioMaximo;
        this.politica = politica;
        this.hits = 0;
        this.misses = 0;
        this.reemplazos = 0;
    }
    
    public String buscarBloque(int numeroBloque) {
        for (int i = 0; i < buffer.getTamanio(); i++) {
            EntradaBuffer entrada = buffer.obtener(i);
            if (entrada != null && entrada.numeroBloque == numeroBloque) {
                hits++;
                entrada.actualizarAcceso();
                return entrada.datos;
            }
        }
        misses++;
        return null;
    }
    
    public void agregarBloque(int numeroBloque, String datos) {
        for (int i = 0; i < buffer.getTamanio(); i++) {
            EntradaBuffer entrada = buffer.obtener(i);
            if (entrada != null && entrada.numeroBloque == numeroBloque) {
                entrada.datos = datos;
                entrada.actualizarAcceso();
                return;
            }
        }
        
        if (buffer.getTamanio() >= tamanioMaximo) {
            aplicarPoliticaReemplazo();
            reemplazos++;
        }
        
        buffer.agregarAlFinal(new EntradaBuffer(numeroBloque, datos));
    }
    
    private void aplicarPoliticaReemplazo() {
        if (buffer.estaVacia()) {
            return;
        }
        
        switch (politica) {
            case FIFO:
                buffer.eliminarPrimero();
                break;
                
            case LRU:
                int indiceMenosReciente = 0;
                long tiempoMasAntiguo = Long.MAX_VALUE;
                
                for (int i = 0; i < buffer.getTamanio(); i++) {
                    EntradaBuffer entrada = buffer.obtener(i);
                    if (entrada != null && entrada.ultimoAcceso < tiempoMasAntiguo) {
                        tiempoMasAntiguo = entrada.ultimoAcceso;
                        indiceMenosReciente = i;
                    }
                }
                buffer.eliminarEnPosicion(indiceMenosReciente);
                break;
                
            case LFU:
                int indiceMenosFrecuente = 0;
                int menorFrecuencia = Integer.MAX_VALUE;
                
                for (int i = 0; i < buffer.getTamanio(); i++) {
                    EntradaBuffer entrada = buffer.obtener(i);
                    if (entrada != null && entrada.frecuenciaAcceso < menorFrecuencia) {
                        menorFrecuencia = entrada.frecuenciaAcceso;
                        indiceMenosFrecuente = i;
                    }
                }
                buffer.eliminarEnPosicion(indiceMenosFrecuente);
                break;
        }
    }
    
    public boolean eliminarBloque(int numeroBloque) {
        for (int i = 0; i < buffer.getTamanio(); i++) {
            EntradaBuffer entrada = buffer.obtener(i);
            if (entrada != null && entrada.numeroBloque == numeroBloque) {
                buffer.eliminarEnPosicion(i);
                return true;
            }
        }
        return false;
    }
    
    public void limpiarBuffer() {
        buffer.limpiar();
    }
    
    public void cambiarPolitica(PoliticaReemplazo nuevaPolitica) {
        this.politica = nuevaPolitica;
    }
    
    public void reiniciarEstadisticas() {
        this.hits = 0;
        this.misses = 0;
        this.reemplazos = 0;
    }
    
    public double getTasaHit() {
        int total = hits + misses;
        return total == 0 ? 0 : (double) hits / total * 100;
    }
    
    public double getTasaMiss() {
        int total = hits + misses;
        return total == 0 ? 0 : (double) misses / total * 100;
    }
    
    public int getEspacioDisponible() {
        return tamanioMaximo - buffer.getTamanio();
    }
    
    public String obtenerEstadisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== BUFFER DE ALMACENAMIENTO ===\n");
        sb.append("Capacidad: ").append(buffer.getTamanio()).append("/").append(tamanioMaximo).append("\n");
        sb.append("Espacio Disponible: ").append(getEspacioDisponible()).append("\n");
        sb.append("Política: ").append(politica).append("\n");
        sb.append("  ").append(politica.getDescripcion()).append("\n");
        sb.append("\n--- Estadísticas de Acceso ---\n");
        sb.append("Hits (Aciertos): ").append(hits).append("\n");
        sb.append("Misses (Fallos): ").append(misses).append("\n");
        sb.append("Tasa de Hit: ").append(String.format("%.2f", getTasaHit())).append("%\n");
        sb.append("Tasa de Miss: ").append(String.format("%.2f", getTasaMiss())).append("%\n");
        sb.append("Reemplazos: ").append(reemplazos).append("\n");
        
        int totalAccesos = hits + misses;
        if (totalAccesos > 0) {
            sb.append("Total de Accesos: ").append(totalAccesos).append("\n");
        }
        
        return sb.toString();
    }
    
    public String obtenerBloquesEnBuffer() {
        if (buffer.estaVacia()) {
            return "Buffer vacío";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Bloques en Buffer: [");
        for (int i = 0; i < buffer.getTamanio(); i++) {
            EntradaBuffer entrada = buffer.obtener(i);
            if (entrada != null) {
                sb.append(entrada.numeroBloque);
                if (i < buffer.getTamanio() - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    public int getHits() {
        return hits;
    }
    
    public int getMisses() {
        return misses;
    }
    
    public int getReemplazos() {
        return reemplazos;
    }
    
    public int getTamanioMaximo() {
        return tamanioMaximo;
    }
    
    public int getTamanioActual() {
        return buffer.getTamanio();
    }
    
    public PoliticaReemplazo getPolitica() {
        return politica;
    }
    
    public ListaEnlazada<EntradaBuffer> getBuffer() {
        return buffer;
    }
    
    public String obtenerInfoEntrada(int indice) {
        if (indice >= 0 && indice < buffer.getTamanio()) {
            EntradaBuffer entrada = buffer.obtener(indice);
            if (entrada != null) {
                return String.format("Bloque %d | Accesos: %d", 
                    entrada.numeroBloque, entrada.frecuenciaAcceso);
            }
        }
        return "";
    }
    
    @Override
    public String toString() {
        return String.format("Buffer[%d/%d, Política: %s, Hits: %d, Misses: %d]", 
            buffer.getTamanio(), tamanioMaximo, politica, hits, misses);
    }
}
