package model.disco;

import model.archivos.Archivo;
import util.estructuras.ListaEnlazada;

public class TablaAsignacion {
    
    public static class EntradaTabla {
        private String nombreArchivo;
        private int cantidadBloques;
        private int primerBloque;
        private String propietario;
        private String colorHex;
        private String procesoCreador;
        
        public EntradaTabla(String nombreArchivo, int cantidadBloques, 
                           int primerBloque, String propietario, String colorHex, String procesoCreador) {
            this.nombreArchivo = nombreArchivo;
            this.cantidadBloques = cantidadBloques;
            this.primerBloque = primerBloque;
            this.propietario = propietario;
            this.colorHex = colorHex;
            this.procesoCreador = procesoCreador;
        }
        
        // Getters
        public String getNombreArchivo() {
            return nombreArchivo;
        }
        
        public int getCantidadBloques() {
            return cantidadBloques;
        }
        
        public int getPrimerBloque() {
            return primerBloque;
        }
        
        public String getPropietario() {
            return propietario;
        }
        
        public String getColorHex() {
            return colorHex;
        }
        
        public String getProcesoCreador() {
            return procesoCreador;
        }
        
        @Override
        public String toString() {
            return nombreArchivo + " | " + cantidadBloques + " bloques | Inicio: #" + primerBloque;
        }
    }
    
    private ListaEnlazada<EntradaTabla> entradas;
 
    public TablaAsignacion() {
        this.entradas = new ListaEnlazada<>();
    }
 
    public void agregarArchivo(Archivo archivo) {
        if (archivo.getPrimerBloque() != null) {
            String propietario = archivo.getPropietario() != null ? 
                               archivo.getPropietario().getNombre() : "Sistema";
            
            String colorHex = String.format("#%02x%02x%02x", 
                archivo.getColor().getRed(),
                archivo.getColor().getGreen(),
                archivo.getColor().getBlue()
            );
            
            String procesoCreador = archivo.getProcesoCreador() != null ? 
                                   archivo.getProcesoCreador() : "N/A";
            
            EntradaTabla entrada = new EntradaTabla(
                archivo.getNombre(),
                archivo.getTamanioEnBloques(),
                archivo.getPrimerBloque().getNumeroBloque(),
                propietario,
                colorHex,
                procesoCreador
            );
            
            entradas.agregarAlFinal(entrada);
        }
    }
    
    public boolean eliminarArchivo(String nombreArchivo) {
        for (int i = 0; i < entradas.getTamanio(); i++) {
            EntradaTabla entrada = entradas.obtener(i);
            if (entrada != null && entrada.getNombreArchivo().equals(nombreArchivo)) {
                entradas.eliminarEnPosicion(i);
                return true;
            }
        }
        return false;
    }

    public EntradaTabla buscarArchivo(String nombreArchivo) {
        for (int i = 0; i < entradas.getTamanio(); i++) {
            EntradaTabla entrada = entradas.obtener(i);
            if (entrada != null && entrada.getNombreArchivo().equals(nombreArchivo)) {
                return entrada;
            }
        }
        return null;
    }
    
 
    public ListaEnlazada<EntradaTabla> getEntradas() {
        return entradas;
    }
    
  
    public int getCantidadArchivos() {
        return entradas.getTamanio();
    }
    

    public void limpiar() {
        entradas.limpiar();
    }
    
 
    public String obtenerTablaFormateada() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TABLA DE ASIGNACIÓN DE ARCHIVOS ===\n");
        sb.append(String.format("%-20s %-10s %-15s %-15s\n", 
            "Archivo", "Bloques", "Primer Bloque", "Propietario"));
        sb.append("-".repeat(60)).append("\n");
        
        for (int i = 0; i < entradas.getTamanio(); i++) {
            EntradaTabla entrada = entradas.obtener(i);
            if (entrada != null) {
                sb.append(String.format("%-20s %-10d %-15d %-15s\n",
                    entrada.getNombreArchivo(),
                    entrada.getCantidadBloques(),
                    entrada.getPrimerBloque(),
                    entrada.getPropietario()
                ));
            }
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "TablaAsignacion{archivos=" + entradas.getTamanio() + "}";
    }
}