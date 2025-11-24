package view;

import controller.ControladorPrincipal;
import model.procesos.EstadoProceso;
import model.procesos.Proceso;
import util.estructuras.ListaEnlazada;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * muestra los procesos del sistema
 */
public class PanelProcesos extends JPanel {
    
    private ControladorPrincipal controlador;
    private JTable tableProcesos;
    private DefaultTableModel tableModel;
    private String[] columnas = {"PID", "Nombre", "Estado", "Operación", "Archivo", "Usuario"};
    private JButton btnEjecutar;
    private JButton btnTerminar;
    private JButton btnLimpiar;
    private JLabel lblInfo;
    private Timer timer;
    

    public PanelProcesos(ControladorPrincipal controlador) {
        this.controlador = controlador;
        inicializarComponentes();
        configurarEventos();
        actualizarTabla();
        iniciarActualizacionAutomatica();
    }
    

    private void inicializarComponentes() {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(43, 43, 43));
        

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelSuperior.setBackground(new Color(50, 50, 50));
        
        lblInfo = new JLabel("Procesos: 0");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 11));
        panelSuperior.add(lblInfo);
        
        add(panelSuperior, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableProcesos = new JTable(tableModel);
        tableProcesos.setBackground(new Color(50, 50, 50));
        tableProcesos.setForeground(Color.WHITE);
        tableProcesos.setFont(new Font("Arial", Font.PLAIN, 11));
        tableProcesos.setRowHeight(22);
        tableProcesos.setGridColor(new Color(80, 80, 80));
        tableProcesos.setSelectionBackground(new Color(75, 110, 175));
        
        // Header
        tableProcesos.getTableHeader().setBackground(new Color(60, 63, 65));
        tableProcesos.getTableHeader().setForeground(Color.WHITE);
        tableProcesos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        
        JScrollPane scroll = new JScrollPane(tableProcesos);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelBotones.setBackground(new Color(50, 50, 50));
        
        btnEjecutar = new JButton("▶️ Ejecutar Siguiente");
        btnEjecutar.setFocusPainted(false);
        btnEjecutar.setToolTipText("Ejecuta el siguiente proceso en la cola");
        panelBotones.add(btnEjecutar);
        
        btnTerminar = new JButton("⏹️ Terminar");
        btnTerminar.setFocusPainted(false);
        btnTerminar.setToolTipText("Termina el proceso seleccionado");
        panelBotones.add(btnTerminar);
        
        btnLimpiar = new JButton("🗑️ Limpiar Terminados");
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setToolTipText("Elimina los procesos terminados");
        panelBotones.add(btnLimpiar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    

    private void configurarEventos() {

        btnEjecutar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarSiguienteProceso();
            }
        });
        

        btnTerminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminarProcesoSeleccionado();
            }
        });
        

        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarProcesosTerminados();
            }
        });
        

        tableProcesos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    mostrarDetallesProceso();
                }
            }
        });
    }
    

    private void iniciarActualizacionAutomatica() {
        timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarTabla();
            }
        });
        timer.start();
    }
    

    private void actualizarTabla() {
        tableModel.setRowCount(0); // Limpiar
        
        ListaEnlazada<Proceso> procesos = controlador.getGestorProcesos().getListaProcesos();
        
        for (int i = 0; i < procesos.getTamanio(); i++) {
            Proceso proceso = procesos.obtener(i);
            if (proceso != null) {
                Object[] fila = {
                    proceso.getPid(),
                    proceso.getNombre(),
                    proceso.getEstado(),
                    proceso.getOperacion(),
                    proceso.getArchivoObjetivo(),
                    proceso.getPropietario().getNombre()
                };
                tableModel.addRow(fila);

                int row = tableModel.getRowCount() - 1;
                colorearFila(row, proceso.getEstado());
            }
        }
        
        actualizarInfo();
    }
    

    private void colorearFila(int row, EstadoProceso estado) {

    }
    

    private void actualizarInfo() {
        int total = controlador.getGestorProcesos().getCantidadProcesos();
        int listos = controlador.getGestorProcesos().getColaListos().getTamanio();
        int bloqueados = controlador.getGestorProcesos()
            .obtenerProcesosPorEstado(EstadoProceso.BLOQUEADO).getTamanio();
        int terminados = controlador.getGestorProcesos()
            .obtenerProcesosPorEstado(EstadoProceso.TERMINADO).getTamanio();
        
        lblInfo.setText(String.format(
            "Total: %d  |  Listos: %d  |  Bloqueados: %d  |  Terminados: %d",
            total, listos, bloqueados, terminados
        ));
    }
    
  
    private void ejecutarSiguienteProceso() {
        Proceso proceso = controlador.getGestorProcesos().ejecutarSiguienteProceso();
        
        if (proceso != null) {
            actualizarTabla();
            

            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controlador.getGestorProcesos().terminarProcesoActual();
                    actualizarTabla();
                    notificarCambio();
                }
            });
            timer.setRepeats(false);
            timer.start();
            
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No hay procesos listos para ejecutar",
                "Información",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    

    private void terminarProcesoSeleccionado() {
        int selectedRow = tableProcesos.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Seleccione un proceso",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        int pid = (int) tableModel.getValueAt(selectedRow, 0);
        Proceso proceso = controlador.getGestorProcesos().buscarProcesoPorPID(pid);
        
        if (proceso != null) {
            int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de terminar el proceso " + proceso.getNombre() + "?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (opcion == JOptionPane.YES_OPTION) {
                controlador.getGestorProcesos().terminarProceso(proceso);
                actualizarTabla();
                
                JOptionPane.showMessageDialog(
                    this,
                    "Proceso terminado",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }
    

    private void limpiarProcesosTerminados() {
        int terminados = controlador.getGestorProcesos()
            .obtenerProcesosPorEstado(EstadoProceso.TERMINADO).getTamanio();
        
        if (terminados == 0) {
            JOptionPane.showMessageDialog(
                this,
                "No hay procesos terminados para limpiar",
                "Información",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        controlador.getGestorProcesos().limpiarProcesosTerminados();
        actualizarTabla();
        
        JOptionPane.showMessageDialog(
            this,
            "Se eliminaron " + terminados + " procesos terminados",
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
 
    private void mostrarDetallesProceso() {
        int selectedRow = tableProcesos.getSelectedRow();
        
        if (selectedRow == -1) {
            return;
        }
        
        int pid = (int) tableModel.getValueAt(selectedRow, 0);
        Proceso proceso = controlador.getGestorProcesos().buscarProcesoPorPID(pid);
        
        if (proceso != null) {
            JTextArea textArea = new JTextArea(proceso.obtenerInformacion());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setBackground(new Color(43, 43, 43));
            textArea.setForeground(Color.WHITE);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 250));
            
            JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Detalles del Proceso - PID " + pid,
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
        actualizarTabla();
    }
    

    public void detener() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }
}
