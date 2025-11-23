package util.estructuras;

public class Cola<T> {
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;
    
    public Cola() {
        this.frente = null;
        this.fin = null;
        this.tamanio = 0;
    }
    

    public boolean estaVacia() {
        return frente == null;
    }

    public int getTamanio() {
        return tamanio;
    }
    
    public void encolar(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        
        if (estaVacia()) {
            frente = nuevoNodo;
            fin = nuevoNodo;
        } else {
            fin.setSiguiente(nuevoNodo);
            fin = nuevoNodo;
        }
        tamanio++;
    }
    
    public T desencolar() {
        if (estaVacia()) {
            return null;
        }
        
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        
        if (frente == null) {
            fin = null;
        }
        
        tamanio--;
        return dato;
    }
    
    public T verFrente() {
        if (estaVacia()) {
            return null;
        }
        return frente.getDato();
    }
    
    public T verFin() {
        if (estaVacia()) {
            return null;
        }
        return fin.getDato();
    }
    
    public boolean contiene(T dato) {
        Nodo<T> actual = frente;
        
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        
        return false;
    }
    
    public void limpiar() {
        frente = null;
        fin = null;
        tamanio = 0;
    }
    
    public Nodo<T> getFrente() {
        return frente;
    }
    
    public Object[] toArray() {
        Object[] array = new Object[tamanio];
        Nodo<T> actual = frente;
        int i = 0;
        
        while (actual != null) {
            array[i++] = actual.getDato();
            actual = actual.getSiguiente();
        }
        
        return array;
    }
    
    @Override
    public String toString() {
        if (estaVacia()) {
            return "Cola[]";
        }
        
        StringBuilder sb = new StringBuilder("Cola[");
        Nodo<T> actual = frente;
        
        while (actual != null) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) {
                sb.append(" <- ");
            }
            actual = actual.getSiguiente();
        }
        
        sb.append("]");
        return sb.toString();
    }
}