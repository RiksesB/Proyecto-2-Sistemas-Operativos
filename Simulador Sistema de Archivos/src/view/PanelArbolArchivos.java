package view;

import controller.ControladorPrincipal;
import model.archivos.Archivo;
import model.archivos.Directorio;
import model.archivos.NodoArbol;
import util.estructuras.ListaEnlazada;
import util.excepciones.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;


public class PanelArbolArchivos extends JPanel {
    
    private ControladorPrincipal controlador;
    private JTree tree;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private JTextArea txtInfo;
    private JButton btnCrearArchivo;
    private JButton btnCrearDirectorio;
    private JButton btnEliminar;
    private JButton btnRenombrar;
    
  
    public PanelArbolArchivos(ControladorPrincipal controlador) {
        this.controlador = controlador;
        inicializarComponentes();
        configurarEventos();
        construirArbol();
    }
    

    private void inicializarComponentes() {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(43, 43, 43));
        

        rootNode = new DefaultMutableTreeNode("root");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setBackground(new Color(43, 43, 43));
        tree.setForeground(Color.WHITE);
        tree.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JScrollPane scrollTree = new JScrollPane(tree);
        scrollTree.setBorder(BorderFactory.createEmptyBorder());
        add(scrollTree, BorderLayout.CENTER);
        

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelBotones.setBackground(new Color(50, 50, 50));
        
        btnCrearArchivo = new JButton("📄 Archivo");
        btnCrearArchivo.setFocusPainted(false);
        btnCrearArchivo.setToolTipText("Crear nuevo archivo");
        panelBotones.add(btnCrearArchivo);
        
        btnCrearDirectorio = new JButton("📁 Directorio");
        btnCrearDirectorio.setFocusPainted(false);
        btnCrearDirectorio.setToolTipText("Crear nuevo directorio");
        panelBotones.add(btnCrearDirectorio);
        
        btnRenombrar = new JButton("✏️ Renombrar");
        btnRenombrar.setFocusPainted(false);
        btnRenombrar.setToolTipText("Renombrar elemento");
        panelBotones.add(btnRenombrar);
        
        btnEliminar = new JButton("🗑️ Eliminar");
        btnEliminar.setFocusPainted(false);
        btnEliminar.setToolTipText("Eliminar elemento");
        panelBotones.add(btnEliminar);
        
        add(panelBotones, BorderLayout.NORTH);

        txtInfo = new JTextArea(5, 20);
        txtInfo.setEditable(false);
        txtInfo.setBackground(new Color(50, 50, 50));
        txtInfo.setForeground(Color.WHITE);
        txtInfo.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        scrollInfo.setPreferredSize(new Dimension(0, 120));
        scrollInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Información",
            0, 0, null, Color.WHITE
        ));
        add(scrollInfo, BorderLayout.SOUTH);
    }
    

    private void configurarEventos() {

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = 
                (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            
            if (selectedNode != null && selectedNode.getUserObject() instanceof NodoArbol) {
                NodoArbol nodo = (NodoArbol) selectedNode.getUserObject();
                mostrarInformacion(nodo);
            }
        });
        

        btnCrearArchivo.addActionListener(e -> crearArchivo());
        

        btnCrearDirectorio.addActionListener(e -> crearDirectorio());
        

        btnRenombrar.addActionListener(e -> renombrar());
        

        btnEliminar.addActionListener(e -> eliminar());
        

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode node = 
                        (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    
                    if (node != null && node.getUserObject() instanceof Archivo) {
                        Archivo archivo = (Archivo) node.getUserObject();
                        mostrarDetallesArchivo(archivo);
                    }
                }
            }
        });
    }
    

    private void construirArbol() {
        rootNode.removeAllChildren();
        Directorio raiz = controlador.getSistema().getRaiz();
        agregarNodosRecursivo(rootNode, raiz);
        treeModel.reload();
        tree.expandRow(0);
    }
    

    private void agregarNodosRecursivo(DefaultMutableTreeNode parentNode, Directorio directorio) {
        ListaEnlazada<NodoArbol> hijos = directorio.getHijos();
        
        for (int i = 0; i < hijos.getTamanio(); i++) {
            NodoArbol hijo = hijos.obtener(i);
            if (hijo != null) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(hijo);
                parentNode.add(childNode);
                
                if (hijo.esDirectorio()) {
                    agregarNodosRecursivo(childNode, (Directorio) hijo);
                }
            }
        }
    }
    

    private void crearArchivo() {
        DefaultMutableTreeNode selectedNode = 
            (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        
        if (selectedNode == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un directorio donde crear el archivo",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Object obj = selectedNode.getUserObject();
        Directorio directorioDestino;
        
        if (obj instanceof Directorio) {
            directorioDestino = (Directorio) obj;
        } else if (obj instanceof Archivo) {
            directorioDestino = ((Archivo) obj).getPadre();
        } else {
            directorioDestino = controlador.getSistema().getRaiz();
        }
        
        DialogoCrearArchivo dialogo = new DialogoCrearArchivo(
            (Frame) SwingUtilities.getWindowAncestor(this),
            controlador,
            directorioDestino
        );
        dialogo.setVisible(true);
        
        if (dialogo.isCreado()) {
            actualizar();
            notificarCambio();
        }
    }
    
  
    private void crearDirectorio() {
        DefaultMutableTreeNode selectedNode = 
            (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        
        Directorio directorioDestino;
        
        if (selectedNode != null && selectedNode.getUserObject() instanceof Directorio) {
            directorioDestino = (Directorio) selectedNode.getUserObject();
        } else {
            directorioDestino = controlador.getSistema().getRaiz();
        }
        
        String nombre = JOptionPane.showInputDialog(
            this,
            "Nombre del directorio:",
            "Crear Directorio",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (nombre != null && !nombre.trim().isEmpty()) {
            try {
                controlador.getGestorArchivos().crearDirectorio(
                    nombre.trim(),
                    directorioDestino,
                    controlador.getSistema().getUsuarioActual()
                );
                
                actualizar();
                notificarCambio();
                
                JOptionPane.showMessageDialog(
                    this,
                    "Directorio creado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
            } catch (PermisosDenegadosException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error de Permisos",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error al crear directorio: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void renombrar() {
        DefaultMutableTreeNode selectedNode = 
            (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        
        if (selectedNode == null || !(selectedNode.getUserObject() instanceof NodoArbol)) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un archivo o directorio",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        NodoArbol nodo = (NodoArbol) selectedNode.getUserObject();
        String nuevoNombre = JOptionPane.showInputDialog(
            this,
            "Nuevo nombre:",
            nodo.getNombre()
        );
        
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            try {
                if (nodo.esDirectorio()) {
                    controlador.getGestorArchivos().actualizarNombreDirectorio(
                        (Directorio) nodo,
                        nuevoNombre.trim(),
                        controlador.getSistema().getUsuarioActual()
                    );
                } else {
                    controlador.getGestorArchivos().actualizarNombreArchivo(
                        (Archivo) nodo,
                        nuevoNombre.trim(),
                        controlador.getSistema().getUsuarioActual()
                    );
                }
                
                actualizar();
                notificarCambio();
                
                JOptionPane.showMessageDialog(
                    this,
                    "Renombrado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
            } catch (PermisosDenegadosException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error de Permisos",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    

    private void eliminar() {
        DefaultMutableTreeNode selectedNode = 
            (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        
        if (selectedNode == null || !(selectedNode.getUserObject() instanceof NodoArbol)) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un archivo o directorio",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        NodoArbol nodo = (NodoArbol) selectedNode.getUserObject();
        
        int opcion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar " + nodo.getNombre() + "?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                if (nodo.esDirectorio()) {
                    controlador.getGestorArchivos().eliminarDirectorio(
                        (Directorio) nodo,
                        controlador.getSistema().getUsuarioActual()
                    );
                } else {
                    controlador.eliminarArchivoConProceso((Archivo) nodo);
                }
                
                actualizar();
                notificarCambio();
                
                JOptionPane.showMessageDialog(
                    this,
                    "Eliminado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
            } catch (PermisosDenegadosException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error de Permisos",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    

    private void mostrarInformacion(NodoArbol nodo) {
        if (nodo.esDirectorio()) {
            txtInfo.setText(((Directorio) nodo).obtenerInformacion());
        } else {
            txtInfo.setText(((Archivo) nodo).obtenerInformacion());
        }
    }
    
 
    private void mostrarDetallesArchivo(Archivo archivo) {
        try {
    
            String info = controlador.leerArchivoConProceso(archivo);
            
            JTextArea textArea = new JTextArea(info);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setBackground(new Color(43, 43, 43));
            textArea.setForeground(Color.WHITE);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Detalles: " + archivo.getNombre(),
                JOptionPane.INFORMATION_MESSAGE
            );
        
            notificarCambio();
            
        } catch (PermisosDenegadosException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Error de Permisos",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
      
            JTextArea textArea = new JTextArea(archivo.obtenerInformacion());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setBackground(new Color(43, 43, 43));
            textArea.setForeground(Color.WHITE);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Detalles: " + archivo.getNombre(),
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    

    private void notificarCambio() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof VentanaPrincipal) {
            ((VentanaPrincipal) window).actualizarTodo();
        }
    }

    public void actualizar() {
        construirArbol();
    }
}
