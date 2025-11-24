package view;

import controller.ControladorPrincipal;
import controller.SimuladorIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * visualiza el simulador de I/O paso a paso
 */
public class PanelSimulador extends JPanel {

    private ControladorPrincipal controlador;
    private JTextArea textInfo;
    private JProgressBar progressBar;
    private JButton btnAvanzar;
    private JButton btnPausar;
    private JButton btnCompletar;
    private JButton btnReiniciar;
    private JSlider sliderVelocidad;
    private JLabel lblEstado;
    private JLabel lblCabezal;
    private Timer timerAutoAvance;
    private boolean modoAutomatico;

    public PanelSimulador(ControladorPrincipal controlador) {
        this.controlador = controlador;
        this.modoAutomatico = false;
        inicializarComponentes();
        configurarEventos();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(43, 43, 43));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

   
        JPanel panelEstado = new JPanel(new GridLayout(2, 1, 5, 5));
        panelEstado.setBackground(new Color(50, 50, 50));
        panelEstado.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Estado de la Simulación",
            0, 0, new Font("Arial", Font.BOLD, 12), Color.WHITE
        ));

        lblEstado = new JLabel("Estado: Esperando...");
        lblEstado.setForeground(Color.WHITE);
        lblEstado.setFont(new Font("Monospaced", Font.PLAIN, 12));

        lblCabezal = new JLabel("Posición Cabezal: 0");
        lblCabezal.setForeground(Color.CYAN);
        lblCabezal.setFont(new Font("Monospaced", Font.PLAIN, 12));

        panelEstado.add(lblEstado);
        panelEstado.add(lblCabezal);

        add(panelEstado, BorderLayout.NORTH);

     
        textInfo = new JTextArea(12, 40);
        textInfo.setEditable(false);
        textInfo.setBackground(new Color(30, 30, 30));
        textInfo.setForeground(Color.GREEN);
        textInfo.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textInfo.setText("Simulador de I/O - Esperando operación...\n");

        JScrollPane scroll = new JScrollPane(textInfo);
        scroll.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(scroll, BorderLayout.CENTER);

        
        JPanel panelControles = new JPanel(new BorderLayout(5, 5));
        panelControles.setBackground(new Color(50, 50, 50));

     
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("0%");
        progressBar.setForeground(new Color(100, 200, 100));
        panelControles.add(progressBar, BorderLayout.NORTH);

      
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelBotones.setBackground(new Color(50, 50, 50));

        btnAvanzar = new JButton("▶ Avanzar Ciclo");
        btnAvanzar.setFocusPainted(false);
        btnAvanzar.setToolTipText("Avanza un ciclo de la simulación");
        panelBotones.add(btnAvanzar);

        btnPausar = new JButton("⏸ Auto");
        btnPausar.setFocusPainted(false);
        btnPausar.setToolTipText("Ejecuta automáticamente");
        panelBotones.add(btnPausar);

        btnCompletar = new JButton("⏭ Completar");
        btnCompletar.setFocusPainted(false);
        btnCompletar.setToolTipText("Completa toda la simulación");
        panelBotones.add(btnCompletar);

        btnReiniciar = new JButton("🔄 Reiniciar");
        btnReiniciar.setFocusPainted(false);
        btnReiniciar.setToolTipText("Reinicia el simulador");
        panelBotones.add(btnReiniciar);

        panelControles.add(panelBotones, BorderLayout.CENTER);

    
        JPanel panelVelocidad = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelVelocidad.setBackground(new Color(50, 50, 50));

        JLabel lblVelocidad = new JLabel("Velocidad:");
        lblVelocidad.setForeground(Color.WHITE);
        panelVelocidad.add(lblVelocidad);

        sliderVelocidad = new JSlider(JSlider.HORIZONTAL, 50, 2000, 500);
        sliderVelocidad.setMajorTickSpacing(500);
        sliderVelocidad.setPaintTicks(true);
        sliderVelocidad.setPreferredSize(new Dimension(200, 40));
        sliderVelocidad.setBackground(new Color(50, 50, 50));
        sliderVelocidad.setForeground(Color.WHITE);
        panelVelocidad.add(sliderVelocidad);

        JLabel lblMs = new JLabel("ms");
        lblMs.setForeground(Color.LIGHT_GRAY);
        panelVelocidad.add(lblMs);

        panelControles.add(panelVelocidad, BorderLayout.SOUTH);

        add(panelControles, BorderLayout.SOUTH);

       
        timerAutoAvance = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modoAutomatico) {
                    avanzarCiclo();
                }
            }
        });
    }

    private void configurarEventos() {
  
        btnAvanzar.addActionListener(e -> avanzarCiclo());

      
        btnPausar.addActionListener(e -> {
            modoAutomatico = !modoAutomatico;
            if (modoAutomatico) {
                btnPausar.setText("⏸ Pausar");
                timerAutoAvance.start();
            } else {
                btnPausar.setText("▶ Auto");
                timerAutoAvance.stop();
            }
        });

   
        btnCompletar.addActionListener(e -> {
            modoAutomatico = false;
            timerAutoAvance.stop();
            btnPausar.setText("▶ Auto");

            model.archivos.Archivo archivo = controlador.getSimuladorIO().getArchivoActual();
            model.archivos.Directorio directorio = controlador.getSimuladorIO().getDirectorioActual();

            if (archivo != null && directorio != null) {
                try {
                  
                    controlador.completarSimulacion(archivo, directorio);

                 
                    Window window = SwingUtilities.getWindowAncestor(this);
                    if (window instanceof VentanaPrincipal) {
                        ((VentanaPrincipal) window).actualizarTodo();
                    }

                    JOptionPane.showMessageDialog(
                        this,
                        "Archivo '" + archivo.getNombre() + "' creado exitosamente\n" +
                        "Tamaño: " + archivo.getTamanioEnBloques() + " bloques",
                        "Simulación Completada",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (util.excepciones.EspacioInsuficienteException ex) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Error: " + ex.getMessage(),
                        "Error al completar",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
         
                while (controlador.avanzarSimulacion()) {
                    actualizar();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            actualizar();
        });

     
        btnReiniciar.addActionListener(e -> {
            modoAutomatico = false;
            timerAutoAvance.stop();
            btnPausar.setText("▶ Auto");
            controlador.getSimuladorIO().reiniciar();
            actualizar();
        });

    
        sliderVelocidad.addChangeListener(e -> {
            int velocidad = sliderVelocidad.getValue();
            controlador.getSimuladorIO().setVelocidadSimulacion(velocidad);
            timerAutoAvance.setDelay(velocidad);
        });
    }

  
    private void avanzarCiclo() {
        boolean continua = controlador.avanzarSimulacion();
        actualizar();

        if (!continua && modoAutomatico) {
            modoAutomatico = false;
            timerAutoAvance.stop();
            btnPausar.setText("▶ Auto");
        }
    }


    public void actualizar() {
        SimuladorIO simulador = controlador.getSimuladorIO();

   
        lblEstado.setText("Estado: " + simulador.getEstadoActual().getDescripcion());
        lblCabezal.setText(String.format("Posición Cabezal: %d → %d",
            simulador.getPosicionCabezalActual(),
            simulador.getPosicionCabezalDestino()));

        int progreso = (int) (simulador.obtenerProgreso() * 100);
        progressBar.setValue(progreso);
        progressBar.setString(progreso + "%");

      
        textInfo.setText(simulador.obtenerInformacionCiclo());

        if (simulador.getProcesoActual() != null) {
            textInfo.append("\n");
            textInfo.append(controlador.getGestorProcesos().obtenerEstadisticas());
        }
    }

    public void detener() {
        if (timerAutoAvance != null && timerAutoAvance.isRunning()) {
            timerAutoAvance.stop();
        }
        modoAutomatico = false;
    }
}
