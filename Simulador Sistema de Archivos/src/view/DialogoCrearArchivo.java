
package view;

import controller.ControladorPrincipal;
import model.archivos.Directorio;
import model.archivos.Archivo;
import util.excepciones.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *  crear un nuevo archivo
 */
public class DialogoCrearArchivo extends JDialog {
    
    private ControladorPrincipal controlador;
    private Directorio directorioDestino;
    private boolean creado = false;
    
    private JTextField txtNombre;
    private JSpinner spinnerTamanio;
    private JCheckBox chkUsarSimulador;
    private JButton btnCrear;
    private JButton btnCancelar;
    private JLabel lblInfo;
    

    public DialogoCrearArchivo(Frame parent, ControladorPrincipal controlador, 
                              Directorio directorioDestino) {
        super(parent, "Crear Nuevo Archivo", true);
        this.controlador = controlador;
        this.directorioDestino = directorioDestino;
        
        inicializarComponentes();
        configurarEventos();
        configurarDialogo();
    }
    

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(43, 43, 43));
        

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(new Color(43, 43, 43));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblNombre = new JLabel("Nombre del archivo:");
        lblNombre.setForeground(Color.WHITE);
        panelFormulario.add(lblNombre, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtNombre = new JTextField(20);
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 12));
        panelFormulario.add(txtNombre, gbc);
        

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblTamanio = new JLabel("Tamaño (bloques):");
        lblTamanio.setForeground(Color.WHITE);
        panelFormulario.add(lblTamanio, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
        spinnerTamanio = new JSpinner(spinnerModel);
        spinnerTamanio.setFont(new Font("Arial", Font.PLAIN, 12));
        panelFormulario.add(spinnerTamanio, gbc);


        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        chkUsarSimulador = new JCheckBox("Usar simulador de I/O (paso a paso)");
        chkUsarSimulador.setForeground(Color.CYAN);
        chkUsarSimulador.setBackground(new Color(43, 43, 43));
        chkUsarSimulador.setFont(new Font("Arial", Font.BOLD, 11));
        chkUsarSimulador.setSelected(true); // Por defecto activado
        chkUsarSimulador.setToolTipText("Permite visualizar el proceso de creación paso a paso");
        panelFormulario.add(chkUsarSimulador, gbc);


        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel lblDestino = new JLabel("Directorio: " + directorioDestino.obtenerRutaCompleta());
        lblDestino.setForeground(Color.LIGHT_GRAY);
        lblDestino.setFont(new Font("Arial", Font.ITALIC, 11));
        panelFormulario.add(lblDestino, gbc);
        
        add(panelFormulario, BorderLayout.CENTER);
        

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.setBackground(new Color(50, 50, 50));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        
        int libres = controlador.getGestorDisco().getBloquesLibres();
        lblInfo = new JLabel("Bloques disponibles: " + libres);
        lblInfo.setForeground(Color.YELLOW);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 11));
        panelInfo.add(lblInfo);
        
        add(panelInfo, BorderLayout.NORTH);
        

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBotones.setBackground(new Color(43, 43, 43));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        btnCrear = new JButton("✓ Crear");
        btnCrear.setPreferredSize(new Dimension(100, 30));
        btnCrear.setFocusPainted(false);
        panelBotones.add(btnCrear);
        
        btnCancelar = new JButton("✗ Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 30));
        btnCancelar.setFocusPainted(false);
        panelBotones.add(btnCancelar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    

    private void configurarEventos() {

        btnCrear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearArchivo();
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        

        txtNombre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearArchivo();
            }
        });
        

        spinnerTamanio.addChangeListener(e -> {
            int tamanio = (int) spinnerTamanio.getValue();
            int libres = controlador.getGestorDisco().getBloquesLibres();
            
            if (tamanio > libres) {
                lblInfo.setText("⚠️ No hay suficiente espacio (disponibles: " + libres + ")");
                lblInfo.setForeground(Color.RED);
                btnCrear.setEnabled(false);
            } else {
                lblInfo.setText("Bloques disponibles: " + libres);
                lblInfo.setForeground(Color.YELLOW);
                btnCrear.setEnabled(true);
            }
        });
        

        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            },
            escape,
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    

    private void configurarDialogo() {
        setSize(450, 250);
        setResizable(false);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                txtNombre.requestFocusInWindow();
            }
        });
    }
    

    private void crearArchivo() {
        String nombre = txtNombre.getText().trim();
        

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Ingrese un nombre para el archivo",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            txtNombre.requestFocusInWindow();
            return;
        }
        

        if (!nombre.matches("[a-zA-Z0-9_\\-\\.]+")) {
            JOptionPane.showMessageDialog(
                this,
                "El nombre solo puede contener letras, números, guiones y puntos",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            txtNombre.requestFocusInWindow();
            return;
        }


        if (directorioDestino.existeHijo(nombre)) {
            JOptionPane.showMessageDialog(
                this,
                "Ya existe un archivo o directorio con el nombre '" + nombre + "' en este directorio",
                "Error - Archivo Duplicado",
                JOptionPane.ERROR_MESSAGE
            );
            txtNombre.requestFocusInWindow();
            return;
        }

        int tamanio = (int) spinnerTamanio.getValue();

        if (!controlador.getGestorDisco().hayEspacioDisponible(tamanio)) {
            JOptionPane.showMessageDialog(
                this,
                "No hay suficiente espacio en el disco",
                "Error de Espacio",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnCrear.setEnabled(false);
            btnCancelar.setEnabled(false);

            Archivo archivo;

            if (chkUsarSimulador.isSelected()) {
                archivo = controlador.crearArchivoConSimulacion(
                    nombre,
                    directorioDestino,
                    tamanio
                );

                creado = false; 
                dispose();

                JOptionPane.showMessageDialog(
                    getParent(),
                    "Simulación iniciada.\n\n" +
                    "Use los controles del Simulador de I/O en la parte inferior\n" +
                    "para avanzar paso a paso o ejecutar automáticamente.\n\n" +
                    "Presione 'Completar' cuando termine de visualizar.",
                    "Simulador Activado",
                    JOptionPane.INFORMATION_MESSAGE
                );



            } else {
                archivo = controlador.crearArchivoConProceso(
                    nombre,
                    directorioDestino,
                    tamanio
                );

                creado = true;

                JOptionPane.showMessageDialog(
                    this,
                    "Archivo '" + nombre + "' creado exitosamente\n" +
                    "Tamaño: " + tamanio + " bloques\n" +
                    "Primer bloque: #" + (archivo.getPrimerBloque() != null ?
                        archivo.getPrimerBloque().getNumeroBloque() : "N/A"),
                    "Archivo Creado",
                    JOptionPane.INFORMATION_MESSAGE
                );

                dispose();
            }
            
        } catch (EspacioInsuficienteException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Error de Espacio",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (PermisosDenegadosException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Error de Permisos",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error al crear el archivo: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            System.err.println("Error inesperado al crear archivo: " + ex.getClass().getName());
        } finally {
            setCursor(Cursor.getDefaultCursor());
            btnCrear.setEnabled(true);
            btnCancelar.setEnabled(true);
        }
    }
    

    public boolean isCreado() {
        return creado;
    }
}