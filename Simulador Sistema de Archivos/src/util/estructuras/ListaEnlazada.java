package util.estructuras;

public class ListaEnlazada<T> {
    private Nodo<T> cabeza;
    private int tamanio;
   
    public ListaEnlazada() {
        this.cabeza = null;
        this.tamanio = 0;
    }
    
    public boolean estaVacia() {
        return cabeza == null;
    }
    
    public int getTamanio() {
        return tamanio;
    }
    
    public void agregarAlInicio(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        nuevoNodo.setSiguiente(cabeza);
        cabeza = nuevoNodo;
        tamanio++;
    }
    
    public void agregarAlFinal(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        
        if (estaVacia()) {
            cabeza = nuevoNodo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }
        tamanio++;
    }
    
    public boolean agregarEnPosicion(T dato, int posicion) {
        if (posicion < 0 || posicion > tamanio) {
            return false;
        }
        
        if (posicion == 0) {
            agregarAlInicio(dato);
            return true;
        }
        
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        Nodo<T> actual = cabeza;
        
        for (int i = 0; i < posicion - 1; i++) {
            actual = actual.getSiguiente();
        }
        
        nuevoNodo.setSiguiente(actual.getSiguiente());
        actual.setSiguiente(nuevoNodo);
        tamanio++;
        return true;
    }
    
    public T eliminarPrimero() {
        if (estaVacia()) {
            return null;
        }
        
        T dato = cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        tamanio--;
        return dato;
    }
    
    public T eliminarUltimo() {
        if (estaVacia()) {
            return null;
        }
        
        if (cabeza.getSiguiente() == null) {
            T dato = cabeza.getDato();
            cabeza = null;
            tamanio--;
            return dato;
        }
        
        Nodo<T> actual = cabeza;
        while (actual.getSiguiente().getSiguiente() != null) {
            actual = actual.getSiguiente();
        }
        
        T dato = actual.getSiguiente().getDato();
        actual.setSiguiente(null);
        tamanio--;
        return dato;
    }
    
    public T eliminarEnPosicion(int posicion) {
        if (posicion < 0 || posicion >= tamanio || estaVacia()) {
            return null;
        }
        
        if (posicion == 0) {
            return eliminarPrimero();
        }
        
        Nodo<T> actual = cabeza;
        for (int i = 0; i < posicion - 1; i++) {
            actual = actual.getSiguiente();
        }
        
        T dato = actual.getSiguiente().getDato();
        actual.setSiguiente(actual.getSiguiente().getSiguiente());
        tamanio--;
        return dato;
    }
    
    public boolean eliminar(T dato) {
        if (estaVacia()) {
            return false;
        }
        
        if (cabeza.getDato().equals(dato)) {
            eliminarPrimero();
            return true;
        }
        
        Nodo<T> actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getDato().equals(dato)) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamanio--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        
        return false;
    }
    
    public boolean contiene(T dato) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }
    
    public T obtener(int posicion) {
        if (posicion < 0 || posicion >= tamanio || estaVacia()) {
            return null;
        }
        
        Nodo<T> actual = cabeza;
        for (int i = 0; i < posicion; i++) {
            actual = actual.getSiguiente();
        }
        
        return actual.getDato();
    }
    
    public T obtenerPrimero() {
        if (estaVacia()) {
            return null;
        }
        return cabeza.getDato();
    }
    
    public T obtenerUltimo() {
        if (estaVacia()) {
            return null;
        }
        
        Nodo<T> actual = cabeza;
        while (actual.getSiguiente() != null) {
            actual = actual.getSiguiente();
        }
        
        return actual.getDato();
    }
    
    public int buscarPosicion(T dato) {
        Nodo<T> actual = cabeza;
        int posicion = 0;
        
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                return posicion;
            }
            actual = actual.getSiguiente();
            posicion++;
        }
        
        return -1;
    }
    
    public void limpiar() {
        cabeza = null;
        tamanio = 0;
    }
    
    public Nodo<T> getCabeza() {
        return cabeza;
    }
    
    public Object[] toArray() {
        Object[] array = new Object[tamanio];
        Nodo<T> actual = cabeza;
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
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder("[");
        Nodo<T> actual = cabeza;
        
        while (actual != null) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) {
                sb.append(", ");
            }
            actual = actual.getSiguiente();
        }
        
        sb.append("]");
        return sb.toString();
    }
}