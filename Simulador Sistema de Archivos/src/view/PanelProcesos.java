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

public class PanelProcesos extends JPanel {
    
    private ControladorPrincipal controlador;
    private JTable tableProcesos;
    private DefaultTableModel tableModel;
    private String[] columnas = {"PID", "Nombre", "Estado", "Operación", "Archivo", "Bloques I/O", "Mov. Cabezal", "Usuario"};
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
        panelSuperior.setBackground(new Color(45, 45, 45));
        
        lblInfo = new JLabel("Procesos: 0");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Consolas", Font.PLAIN, 11));
        panelSuperior.add(lblInfo);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de procesos
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableProcesos = new JTable(tableModel);
        tableProcesos.setBackground(new Color(60, 60, 60)); 
        tableProcesos.setForeground(Color.WHITE); 
        tableProcesos.setFont(new Font("Consolas", Font.PLAIN, 12));
        tableProcesos.setRowHeight(25);
        tableProcesos.setGridColor(new Color(80, 80, 80));
        tableProcesos.setSelectionBackground(new Color(70, 130, 180)); 
        tableProcesos.setSelectionForeground(Color.WHITE);
        
        javax.swing.table.DefaultTableCellRenderer cellRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(new Color(60, 60, 60));
                    c.setForeground(Color.WHITE);
                    
                    if (column == 2 && value != null) {
                        String estado = value.toString();
                        switch (estado) {
                            case "NUEVO":
                                c.setForeground(new Color(100, 200, 255)); // Azul claro
                                break;
                            case "LISTO":
                                c.setForeground(new Color(255, 255, 100)); // Amarillo
                                break;
                            case "EJECUTANDO":
                                c.setForeground(new Color(100, 255, 100)); // Verde
                                break;
                            case "TERMINADO":
                                c.setForeground(new Color(180, 180, 180)); // Gris claro
                                break;
                        }
                    }
                } else {
                    c.setBackground(new Color(70, 130, 180));
                    c.setForeground(Color.WHITE);
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };
        
        for (int i = 0; i < tableProcesos.getColumnCount(); i++) {
            tableProcesos.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        
        tableProcesos.getTableHeader().setBackground(new Color(50, 50, 50));
        tableProcesos.getTableHeader().setForeground(Color.BLACK); // Texto negro para header blanco
        tableProcesos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableProcesos.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        tableProcesos.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scroll = new JScrollPane(tableProcesos);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(60, 60, 60));
        add(scroll, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelBotones.setBackground(new Color(45, 45, 45));
        
        btnLimpiar = new JButton("🧹 Limpiar Terminados");
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBackground(new Color(100, 100, 100));
        btnLimpiar.setForeground(Color.BLACK); // Texto negro
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setToolTipText("Elimina los procesos terminados");
        panelBotones.add(btnLimpiar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void configurarEventos() {
        // Botón limpiar
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
        timer = new Timer(500, new ActionListener() {
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
                String bloquesIO = proceso.getBloquesIO();
                if (bloquesIO == null || bloquesIO.isEmpty()) {
                    bloquesIO = "-";
                }
                
             
                String movCabezal = String.valueOf(proceso.getMovimientoCabezal());
                
                Object[] fila = {
                    proceso.getPid(),
                    proceso.getNombre(),
                    proceso.getEstado(),
                    proceso.getOperacion(),
                    proceso.getArchivoObjetivo(),
                    bloquesIO,
                    movCabezal,
                    proceso.getPropietario().getNombre()
                };
                tableModel.addRow(fila);
            }
        }
        
        actualizarInfo();
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
    

    public void actualizar() {
        actualizarTabla();
    }
    

    public void detener() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }
}