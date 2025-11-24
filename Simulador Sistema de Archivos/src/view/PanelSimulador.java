package view;

import controller.ControladorPrincipal;
import controller.SimuladorIO;
import javax.swing.*;
import java.awt.*;

public class PanelSimulador extends JPanel implements SimuladorIO.ObservadorSimulacion {
    
    private ControladorPrincipal controlador;
    private SimuladorIO simulador;
    
    private JTextArea areaEstado;
    private JTextArea areaLog;
    private JButton btnAvanzar;
    private JButton btnAuto;
    private JButton btnCompletar;
    private JButton btnReiniciar;
    private JSlider sliderVelocidad;
    private JLabel lblVelocidad;
    private JProgressBar barraProgreso;
    
    public PanelSimulador(ControladorPrincipal controlador) {
        this.controlador = controlador;
        this.simulador = controlador.getSimuladorIO();
        this.simulador.agregarObservador(this);
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            "⚙️ Simulador de I/O",
            0, 0, new Font("Segoe UI", Font.BOLD, 13), Color.WHITE
        ));
        setBackground(new Color(45, 45, 45));
        
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        JPanel panelEstado = crearPanelEstado();
        add(panelEstado, BorderLayout.NORTH);
        
        JPanel panelLog = crearPanelLog();
        add(panelLog, BorderLayout.CENTER);
        
        JPanel panelControl = crearPanelControl();
        add(panelControl, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(45, 45, 45));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            "Estado de la Simulación",
            0, 0, new Font("Segoe UI", Font.BOLD, 11), Color.WHITE
        ));
        
        areaEstado = new JTextArea(3, 40);
        areaEstado.setEditable(false);
        areaEstado.setBackground(new Color(30, 30, 30));
        areaEstado.setForeground(new Color(0, 255, 0));
        areaEstado.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaEstado.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setStringPainted(true);
        barraProgreso.setForeground(new Color(76, 175, 80));
        barraProgreso.setBackground(new Color(60, 60, 60));
        barraProgreso.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        panel.add(new JScrollPane(areaEstado), BorderLayout.CENTER);
        panel.add(barraProgreso, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelLog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 45, 45));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            "Log de Operaciones",
            0, 0, new Font("Segoe UI", Font.BOLD, 11), Color.WHITE
        ));
        
        areaLog = new JTextArea(8, 40);
        areaLog.setEditable(false);
        areaLog.setBackground(new Color(30, 30, 30));
        areaLog.setForeground(new Color(0, 200, 255));
        areaLog.setFont(new Font("Consolas", Font.PLAIN, 10));
        areaLog.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(areaLog);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelControl() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(45, 45, 45));
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelBotones.setBackground(new Color(45, 45, 45));
        
        btnAvanzar = new JButton("▶ Avanzar");
        btnAvanzar.setBackground(new Color(33, 150, 243));
        btnAvanzar.setForeground(Color.WHITE);
        btnAvanzar.setFocusPainted(false);
        btnAvanzar.setBorderPainted(false);
        btnAvanzar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAvanzar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAvanzar.addActionListener(e -> avanzarCiclo());
        
        btnAuto = new JButton("⏯ Auto");
        btnAuto.setBackground(new Color(76, 175, 80));
        btnAuto.setForeground(Color.WHITE);
        btnAuto.setFocusPainted(false);
        btnAuto.setBorderPainted(false);
        btnAuto.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAuto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAuto.addActionListener(e -> toggleAuto());
        
        btnCompletar = new JButton("⏭ Completar");
        btnCompletar.setBackground(new Color(255, 152, 0));
        btnCompletar.setForeground(Color.WHITE);
        btnCompletar.setFocusPainted(false);
        btnCompletar.setBorderPainted(false);
        btnCompletar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnCompletar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCompletar.addActionListener(e -> completar());
        
        btnReiniciar = new JButton("🔄 Reiniciar");
        btnReiniciar.setBackground(new Color(244, 67, 54));
        btnReiniciar.setForeground(Color.WHITE);
        btnReiniciar.setFocusPainted(false);
        btnReiniciar.setBorderPainted(false);
        btnReiniciar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnReiniciar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReiniciar.addActionListener(e -> reiniciar());
        
        panelBotones.add(btnAvanzar);
        panelBotones.add(btnAuto);
        panelBotones.add(btnCompletar);
        panelBotones.add(btnReiniciar);
        
        JPanel panelVelocidad = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelVelocidad.setBackground(new Color(45, 45, 45));
        
        lblVelocidad = new JLabel("Velocidad: 500 ms");
        lblVelocidad.setForeground(Color.WHITE);
        lblVelocidad.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        sliderVelocidad = new JSlider(100, 2000, 500);
        sliderVelocidad.setInverted(true);
        sliderVelocidad.setMajorTickSpacing(500);
        sliderVelocidad.setMinorTickSpacing(100);
        sliderVelocidad.setPaintTicks(true);
        sliderVelocidad.setPaintLabels(false);
        sliderVelocidad.setBackground(new Color(45, 45, 45));
        sliderVelocidad.setForeground(Color.WHITE);
        sliderVelocidad.addChangeListener(e -> {
            int valor = sliderVelocidad.getValue();
            simulador.setVelocidadSimulacion(valor);
            lblVelocidad.setText("Velocidad: " + valor + " ms");
        });
        
        panelVelocidad.add(lblVelocidad);
        panelVelocidad.add(sliderVelocidad);
        
        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(panelVelocidad, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void avanzarCiclo() {
        boolean continuar = simulador.avanzarCiclo();
        if (!continuar) {
            agregarLog("✓ Simulación completada");
        }
    }
    
    private void toggleAuto() {
        if (simulador.isAutoActivo()) {
            simulador.detenerAuto();
            btnAuto.setText("⏯ Auto");
            btnAuto.setBackground(new Color(76, 175, 80));
            agregarLog("⏸ Modo automático pausado");
        } else {
            simulador.iniciarAuto();
            btnAuto.setText("⏸ Pausar");
            btnAuto.setBackground(new Color(255, 193, 7));
            agregarLog("⏯ Modo automático activado");
        }
    }
    
    private void completar() {
        try {
            simulador.completar();
            
            if (simulador.getArchivoActual() != null && simulador.getDirectorioActual() != null) {
                controlador.completarSimulacion(
                    simulador.getArchivoActual(),
                    simulador.getDirectorioActual()
                );
                
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window instanceof VentanaPrincipal) {
                    ((VentanaPrincipal) window).actualizarTodo();
                }
            }
            
            btnAuto.setText("⏯ Auto");
            btnAuto.setBackground(new Color(76, 175, 80));
            agregarLog("⏭ Simulación completada instantáneamente");
        } catch (Exception ex) {
            agregarLog("❌ Error al completar: " + ex.getMessage());
        }
    }
    
    private void reiniciar() {
        simulador.reiniciar();
        btnAuto.setText("⏯ Auto");
        btnAuto.setBackground(new Color(76, 175, 80));
        areaLog.setText("");
        agregarLog("🔄 Simulación reiniciada");
    }
    
    @Override
    public void onSimulacionActualizada() {
        SwingUtilities.invokeLater(() -> {
            actualizarEstado();
            actualizarProgreso();
        });
    }
    
    private void actualizarEstado() {
        StringBuilder estado = new StringBuilder();
        
        SimuladorIO.EstadoSimulacion estadoActual = simulador.getEstadoActual();
        
        if (estadoActual != SimuladorIO.EstadoSimulacion.ESPERANDO && 
            estadoActual != SimuladorIO.EstadoSimulacion.COMPLETADO) {
            
            estado.append("Estado: ").append(estadoActual.getDescripcion()).append("\n");
            estado.append("Posición Cabezal: ").append(simulador.getPosicionCabezalActual());
            
            if (estadoActual == SimuladorIO.EstadoSimulacion.MOVIENDO_CABEZAL) {
                estado.append(" → ").append(simulador.getPosicionCabezalDestino());
            }
            
            estado.append("\n");
            
            int indiceActual = simulador.getIndiceSolicitudActual();
            estado.append("Progreso: ").append(indiceActual).append(" operaciones procesadas");
            
        } else if (estadoActual == SimuladorIO.EstadoSimulacion.COMPLETADO) {
            estado.append("Estado: ✓ Operación completada\n");
            estado.append("Posición Final Cabezal: ").append(simulador.getPosicionCabezalActual());
        } else {
            estado.append("Estado: Esperando próxima operación\n");
            estado.append("Posición Cabezal: ").append(simulador.getPosicionCabezalActual());
        }
        
        areaEstado.setText(estado.toString());
    }
    
    private void actualizarProgreso() {
        double progreso = simulador.obtenerProgreso();
        int progresoInt = (int)(progreso * 100);
        barraProgreso.setValue(progresoInt);
        barraProgreso.setString(progresoInt + "%");
        
        SimuladorIO.EstadoSimulacion estadoActual = simulador.getEstadoActual();
        if (estadoActual == SimuladorIO.EstadoSimulacion.ASIGNANDO_BLOQUE) {
            int bloqueActual = simulador.getPosicionCabezalActual();
            agregarLog(String.format("→ Bloque %d procesado", bloqueActual));
        }
    }
    
    private void agregarLog(String mensaje) {
        areaLog.append(mensaje + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }
    
    public void actualizar() {
        onSimulacionActualizada();
    }
    
    public void limpiar() {
        areaEstado.setText("");
        areaLog.setText("");
        barraProgreso.setValue(0);
        btnAuto.setText("⏯ Auto");
        btnAuto.setBackground(new Color(76, 175, 80));
    }
    
    public void detener() {
        simulador.detenerAuto();
        btnAuto.setText("⏯ Auto");
        btnAuto.setBackground(new Color(76, 175, 80));
    }
}
