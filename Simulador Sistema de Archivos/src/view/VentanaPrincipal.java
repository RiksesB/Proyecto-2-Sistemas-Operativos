package view;

import controller.ControladorPrincipal;
import model.sistema.TipoUsuario;
import model.planificacion.TipoPlanificacion;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ventana principal 
 */
public class VentanaPrincipal extends JFrame {
    
    private ControladorPrincipal controlador;
    
  
    private PanelArbolArchivos panelArbol;
    private PanelDisco panelDisco;
    private PanelTablaAsignacion panelTabla;
    private PanelProcesos panelProcesos;
    private PanelSimulador panelSimulador;

   
    private JLabel lblUsuarioActual;
    private JComboBox<String> cmbModoUsuario;
    private JComboBox<TipoPlanificacion> cmbPlanificacion;
    private JButton btnEstadisticas;
    private JButton btnGuardar;
    private JButton btnCargar;
    
  
    public VentanaPrincipal(int tamanioDiscoEnBloques) {
        this.controlador = new ControladorPrincipal(tamanioDiscoEnBloques);
        
        inicializarComponentes();
        configurarVentana();
        configurarEventos();
    }
    
   
    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

       
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);

     
        JPanel panelCentral = crearPanelCentral();

        panelSimulador = new PanelSimulador(controlador);
        JPanel contenedorSimulador = crearPanelConTitulo("🔄 Simulador de I/O", panelSimulador);

      
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelCentral, contenedorSimulador);
        splitPane.setDividerLocation(600); // Posición inicial del divisor
        splitPane.setResizeWeight(0.7); // 70% arriba, 30% abajo al redimensionar
        splitPane.setOneTouchExpandable(true); // Botones para colapsar rápidamente
        splitPane.setDividerSize(8);

        add(splitPane, BorderLayout.CENTER);

        
        JPanel panelInferior = crearPanelInferior();
        add(panelInferior, BorderLayout.SOUTH);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(new Color(60, 63, 65));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
      
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setForeground(Color.WHITE);
        panel.add(lblUsuario);
        
        lblUsuarioActual = new JLabel(controlador.getSistema().getUsuarioActual().getNombre());
        lblUsuarioActual.setForeground(Color.YELLOW);
        lblUsuarioActual.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblUsuarioActual);
        
        panel.add(Box.createHorizontalStrut(20));
        
       
        JLabel lblModo = new JLabel("Modo:");
        lblModo.setForeground(Color.WHITE);
        panel.add(lblModo);
        
        cmbModoUsuario = new JComboBox<>(new String[]{"Administrador", "Usuario"});
        cmbModoUsuario.setPreferredSize(new Dimension(120, 25));
        panel.add(cmbModoUsuario);
        
        panel.add(Box.createHorizontalStrut(20));
        
        
        JLabel lblPlanif = new JLabel("Planificación:");
        lblPlanif.setForeground(Color.WHITE);
        panel.add(lblPlanif);
        
        cmbPlanificacion = new JComboBox<>(TipoPlanificacion.values());
        cmbPlanificacion.setPreferredSize(new Dimension(150, 25));
        panel.add(cmbPlanificacion);
        
        panel.add(Box.createHorizontalStrut(20));
        
      
        btnEstadisticas = new JButton("📊 Estadísticas");
        btnEstadisticas.setFocusPainted(false);
        panel.add(btnEstadisticas);
        
        btnGuardar = new JButton("💾 Guardar");
        btnGuardar.setFocusPainted(false);
        panel.add(btnGuardar);
        
        btnCargar = new JButton("📂 Cargar");
        btnCargar.setFocusPainted(false);
        panel.add(btnCargar);
        
        return panel;
    }
    
  
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(43, 43, 43));
        
      
        panelArbol = new PanelArbolArchivos(controlador);
        JPanel contenedorArbol = crearPanelConTitulo("📁 Sistema de Archivos", panelArbol);
        panel.add(contenedorArbol);
        
      
        panelDisco = new PanelDisco(controlador);
        JPanel contenedorDisco = crearPanelConTitulo("💾 Disco Virtual", panelDisco);
        panel.add(contenedorDisco);
        
      
        panelTabla = new PanelTablaAsignacion(controlador);
        JPanel contenedorTabla = crearPanelConTitulo("📋 Tabla de Asignación", panelTabla);
        panel.add(contenedorTabla);
        
       
        panelProcesos = new PanelProcesos(controlador);
        JPanel contenedorProcesos = crearPanelConTitulo("⚙️ Procesos", panelProcesos);
        panel.add(contenedorProcesos);
        
        return panel;
    }
    
 
    private JPanel crearPanelConTitulo(String titulo, JPanel contenido) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(60, 63, 65));
        panel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));
        
       
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        lblTitulo.setBackground(new Color(50, 50, 50));
        lblTitulo.setOpaque(true);
        panel.add(lblTitulo, BorderLayout.NORTH);
        
     
        panel.add(contenido, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBackground(new Color(60, 63, 65));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
       
        JLabel lblInfo = new JLabel("Sistema de Archivos Virtual v1.0");
        lblInfo.setForeground(Color.LIGHT_GRAY);
        panel.add(lblInfo);
        
        return panel;
    }
    
  
    private void configurarVentana() {
        setTitle("Simulador de Sistema de Archivos");
        setSize(1400, 900);
        setMinimumSize(new Dimension(1200, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
       
        try {
          
        } catch (Exception e) {
         
        }
    }
    

    private void configurarEventos() {
        // Cambio de modo de usuario
        cmbModoUsuario.addActionListener(e -> {
            String modo = (String) cmbModoUsuario.getSelectedItem();
            TipoUsuario tipo = modo.equals("Administrador") ? 
                              TipoUsuario.ADMINISTRADOR : TipoUsuario.USUARIO;
            
            String nombreUsuario = JOptionPane.showInputDialog(
                this,
                "Ingrese el nombre del usuario:",
                "Cambiar Usuario",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (nombreUsuario != null && !nombreUsuario.trim().isEmpty()) {
                controlador.cambiarUsuario(nombreUsuario.trim(), tipo);
                lblUsuarioActual.setText(nombreUsuario.trim());
                actualizarTodo();
                
                JOptionPane.showMessageDialog(
                    this,
                    "Usuario cambiado a: " + nombreUsuario + " (" + tipo + ")",
                    "Usuario Cambiado",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        
       
        cmbPlanificacion.addActionListener(e -> {
            TipoPlanificacion tipo = (TipoPlanificacion) cmbPlanificacion.getSelectedItem();
            if (tipo != null) {
                controlador.cambiarAlgoritmoPlanificacion(tipo);
                JOptionPane.showMessageDialog(
                    this,
                    "Algoritmo cambiado a: " + tipo.getDescripcion(),
                    "Planificación Actualizada",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        
      
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());
        
        
        btnGuardar.addActionListener(e -> guardarSistema());
        
       
        btnCargar.addActionListener(e -> cargarSistema());
        
    
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                    VentanaPrincipal.this,
                    "¿Desea guardar antes de salir?",
                    "Guardar Sistema",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (opcion == JOptionPane.YES_OPTION) {
                    if (guardarSistema()) {
                        System.exit(0);
                    }
                } else if (opcion == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            
            }
        });
    }
    
   
    private void mostrarEstadisticas() {
        String stats = controlador.obtenerEstadisticasCompletas();
        
        JTextArea textArea = new JTextArea(stats);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(43, 43, 43));
        textArea.setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Estadísticas del Sistema",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
 
    private boolean guardarSistema() {
        boolean exito = controlador.guardarSistema();
        
        if (exito) {
            JOptionPane.showMessageDialog(
                this,
                "Sistema guardado exitosamente",
                "Guardado",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Error al guardar el sistema",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
        
        return exito;
    }
    
  
    private void cargarSistema() {
        int opcion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro? Se perderán los cambios no guardados",
            "Cargar Sistema",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (opcion == JOptionPane.YES_OPTION) {
            boolean exito = controlador.cargarSistema();
            
            if (exito) {
                actualizarTodo();
                JOptionPane.showMessageDialog(
                    this,
                    "Sistema cargado exitosamente",
                    "Cargado",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar el sistema",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
 
    public void actualizarTodo() {
        panelArbol.actualizar();
        panelDisco.actualizar();
        panelTabla.actualizar();
        panelProcesos.actualizar();
        panelSimulador.actualizar();

        lblUsuarioActual.setText(controlador.getSistema().getUsuarioActual().getNombre());
    }
    
 
    public ControladorPrincipal getControlador() {
        return controlador;
    }
}