package util.estructuras;

public class Pila<T> {
    private Nodo<T> tope;
    private int tamanio;
    
    public Pila() {
        this.tope = null;
        this.tamanio = 0;
    }

    public boolean estaVacia() {
        return tope == null;
    }
 
    public int getTamanio() {
        return tamanio;
    }
    
    public void apilar(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        nuevoNodo.setSiguiente(tope);
        tope = nuevoNodo;
        tamanio++;
    }
    

    public T desapilar() {
        if (estaVacia()) {
            return null;
        }
        
        T dato = tope.getDato();
        tope = tope.getSiguiente();
        tamanio--;
        return dato;
    }
    
 
    public T verTope() {
        if (estaVacia()) {
            return null;
        }
        return tope.getDato();
    }
    
    public boolean contiene(T dato) {
        Nodo<T> actual = tope;
        
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        
        return false;
    }
  
    public void limpiar() {
        tope = null;
        tamanio = 0;
    }
    
    @Override
    public String toString() {
        if (estaVacia()) {
            return "Pila[]";
        }
        
        StringBuilder sb = new StringBuilder("Pila[");
        Nodo<T> actual = tope;
        
        while (actual != null) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) {
                sb.append(" | ");
            }
            actual = actual.getSiguiente();
        }
        
        sb.append("]");
        return sb.toString();
    }
}