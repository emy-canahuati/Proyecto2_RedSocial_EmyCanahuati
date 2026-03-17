/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pii_proyecto2_redsocial;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import pii_proyecto2_redsocial.Usuario.TipoCuenta;

/**
 *
 * @author emyca
 */
public class GUI extends JFrame {

    private JPanel panelPrincipal;
    private JPanel panelContenido;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private Logica logica = new Logica();
    private JLabel lblErrorLogin = null;
    private JButton btnActivar = null;

    private ImageIcon fotoPerfil;
    private JPanel sidebarPanel;

    private boolean fotoFueCambiada = false;

    // Paneles principales
    private JPanel panelChatActual;      // El área de las burbujas (Derecha)
    private JPanel contenedorTarjetas;   // El área de los amigos (Izquierda)
    private JTextField txtMensaje;       // Campo de texto para escribir
    private String usuarioChatActivo = ""; // Para saber a quién le enviamos el mensaje
    private JLabel lblNombreChat;
    private boolean chatSeleccionado = false;
    private JButton btnEnviar;
    private String tipoMensaje;

    public GUI() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panelRegistrar();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                // Solo cerrar sesión si hay alguien logueado
                try {
                    if (logica.getUsuario(0) != null) {
                        logica.cerrarSesion();
                    }
                } catch (Exception ex) {
                    // No hay sesión activa, ignorar
                }
                System.exit(0);
            }
        });

        panelRegistrar();
    }

    private JPanel crearContenedorConFondo() {
        JPanel panelFondo = new JPanel(null);
        panelFondo.setBounds(0, 0, screenSize.width, screenSize.height);

        //cuadro blanco del centro
        panelPrincipal = new JPanel(null);
        panelPrincipal.setBackground(Color.WHITE);
        panelPrincipal.setBounds((screenSize.width - 1366) / 2, (screenSize.height - 786) / 2, 1366, 786);

        // imagen de Fondo
        JLabel fondo = new JLabel();
        ImageIcon imgFondo = new ImageIcon("src/Imagenes/fondo1.jpg");
        Image imgSized = imgFondo.getImage().getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
        fondo.setIcon(new ImageIcon(imgSized));
        fondo.setBounds(0, 0, screenSize.width, screenSize.height);

        panelFondo.add(panelPrincipal);
        panelFondo.add(fondo);

        return panelFondo; // Retornamos el panel que contiene al cuadro blanco
    }

    public void panelRegistrar() {
        getContentPane().removeAll();
        JPanel contenedor = crearContenedorConFondo();
        JPanel panel = (JPanel) contenedor.getComponent(0);

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel panelIzquierdo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 1. Cargamos la imagen
                ImageIcon imgRegistrar = new ImageIcon("src/Imagenes/fondoRegistrar.png");

                // 2. La dibujamos para que llene exactamente el tamaño de este panel (2/3 del centro)
                if (imgRegistrar.getImage() != null) {
                    g.drawImage(imgRegistrar.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fondo de respaldo si no carga la imagen
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        panelContenido = new JPanel(null);
        panelContenido.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Iniciar sesión en Instagram");
        lblTitulo.setForeground(Color.BLACK);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 23));
        lblTitulo.setBounds(50, 60, 300, 30);

        JLabel lblUsuario = new JLabel("Nombre de usuario");
        lblUsuario.setFont(new Font("SansSerif", Font.BOLD, 19));
        lblUsuario.setForeground(Color.BLACK);
        lblUsuario.setBounds(50, 120, 300, 20);

        JTextField txtUsuario = crearCampoEstilizado("");
        txtUsuario.setFont(new Font("SansSerif", Font.BOLD, 19));
        txtUsuario.setBounds(50, 145, 350, 40);

        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 19));
        lblPass.setForeground(Color.BLACK);
        lblPass.setBounds(50, 200, 300, 20);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(new Font("SansSerif", Font.BOLD, 19));
        txtPass.setBackground(Color.WHITE);
        txtPass.setForeground(Color.BLACK);
        txtPass.setCaretColor(Color.WHITE);
        txtPass.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        txtPass.setBounds(50, 225, 350, 40);

        JButton btnLogin = new JButton("Iniciar sesión");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 19));
        btnLogin.setBackground(new Color(0, 149, 246));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBorderPainted(false);
        btnLogin.setBounds(50, 290, 350, 40);

        JButton btnCrear = new JButton("Crear cuenta nueva");
        btnCrear.setFont(new Font("SansSerif", Font.BOLD, 19));
        btnCrear.setBackground(Color.WHITE);
        btnCrear.setForeground(new Color(0, 149, 246));
        btnCrear.setBorder(BorderFactory.createLineBorder(new Color(0, 149, 246)));
        btnCrear.setBounds(50, 340, 350, 40);

        // Variable de clase para evitar duplicados
        // Dentro de btnLogin.addActionListener:
        btnLogin.addActionListener(e -> {
            // 1. Obtener datos
            String usuario = txtUsuario.getText().trim();
            String pass = new String(txtPass.getPassword());

            // 2. Validar con la lógica
            if (logica.Login(usuario, pass)) {
                if (logica.getUsuario(0).getEstado().name().equals("INACTIVO")) {
                    if (lblErrorLogin == null) {
                        lblErrorLogin = new JLabel("¿Seguro que desea activar la cuenta de nuevo?");
                        lblErrorLogin.setFont(new Font("SansSerif", Font.PLAIN, 14));
                        lblErrorLogin.setForeground(Color.BLUE);
                        lblErrorLogin.setBounds(50, 380, 350, 20);
                        panelContenido.add(lblErrorLogin);

                        btnActivar = new JButton("Activar");
                        btnActivar.setFont(new Font("SansSerif", Font.PLAIN, 14));
                        btnActivar.setBounds(410, 380, 300, 20);
                        btnActivar.setBackground(Color.white);
                        btnActivar.setForeground(Color.blue);
                        panelContenido.add(btnActivar);

                    }
                }
                try {
                    logica.conectarSocket();
                } catch (IOException ex) {
                    System.out.println("Servidor no disponible: " + ex.getMessage());
                    // La app sigue funcionando sin mensajes en tiempo real
                }
                panelFeed();
            } else {
                // 3. Manejo de error visual
                String mensaje = logica.estaActivoEnOtraVentana(usuario) ? "Este usuario ya tiene una sesion activa." : "Usuario o contraseña incorrectos.";
                if (lblErrorLogin == null) {
                    lblErrorLogin = new JLabel(mensaje);
                    lblErrorLogin.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    lblErrorLogin.setForeground(Color.RED);
                    lblErrorLogin.setBounds(50, 380, 350, 20);
                    panelContenido.add(lblErrorLogin);
                } else {
                    lblErrorLogin.setText(mensaje);
                }

                // Forzar al panel a mostrar el nuevo label
                panelContenido.revalidate();
                panelContenido.repaint();

                // Limpiar el campo de contraseña para reintentar
                txtPass.setText("");
                txtPass.requestFocus();
            }
        });
        btnCrear.addActionListener(e -> panelCrear());

        panelContenido.add(lblTitulo);
        panelContenido.add(lblUsuario);
        panelContenido.add(txtUsuario);
        panelContenido.add(lblPass);
        panelContenido.add(txtPass);
        panelContenido.add(btnLogin);
        panelContenido.add(btnCrear);
        panelContenido.revalidate();
        panelContenido.repaint();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.weightx = 0.66;
        panel.add(panelIzquierdo, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.33;
        panel.add(panelContenido, gbc);

        add(contenedor);

        revalidate();
        repaint();
    }

    private JTextField crearCampoEstilizado(String placeholder) {
        JTextField campo = new JTextField(placeholder);
        campo.setBackground(Color.WHITE);
        campo.setForeground(Color.BLACK);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return campo;
    }

    public void panelCrear() {
        getContentPane().removeAll();
        JPanel contenedor = crearContenedorConFondo();
        JPanel panel = (JPanel) contenedor.getComponent(0);
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Dimensiones y Fuentes Unificadas
        Dimension dimCampo = new Dimension(450, 40);
        Font fuente19 = new Font("SansSerif", Font.BOLD, 19);
        int espacioEntreSecciones = 20;

        panel.add(Box.createVerticalGlue());

        // 1. TÍTULO
        JLabel lblTitulo = new JLabel("Empieza a usar Instagram");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitulo.setForeground(Color.BLACK);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(30));

        JLabel lblSubtitulo = new JLabel("Crea una nueva cuenta");
        lblSubtitulo.setFont(new Font("SansSerif", Font.BOLD, 19));
        lblSubtitulo.setForeground(Color.BLACK);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblSubtitulo);
        panel.add(Box.createVerticalStrut(30));

        // --- NOMBRE COMPLETO ---
        panel.add(crearFilaContenedora("Nombre Completo:", fuente19, dimCampo));
        JTextField txtNombre = crearCampoEstilizado("");
        txtNombre.setFont(fuente19);
        txtNombre.setMaximumSize(dimCampo);
        txtNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(txtNombre);
        panel.add(Box.createVerticalStrut(espacioEntreSecciones));

        // --- USUARIO ---
        panel.add(crearFilaContenedora("Nombre de Usuario:", fuente19, dimCampo));
        JTextField txtUser = crearCampoEstilizado("");
        txtUser.setFont(fuente19);
        txtUser.setMaximumSize(dimCampo);
        txtUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(txtUser);
        panel.add(Box.createVerticalStrut(espacioEntreSecciones));

        // --- FILA DE EDAD Y GÉNERO ---
        JPanel filaInfo = new JPanel();
        filaInfo.setLayout(new BoxLayout(filaInfo, BoxLayout.X_AXIS));
        filaInfo.setOpaque(false);
        filaInfo.setMaximumSize(dimCampo);

        // Columna Edad
        JPanel colEdad = new JPanel();
        colEdad.setLayout(new BoxLayout(colEdad, BoxLayout.Y_AXIS));
        colEdad.setOpaque(false);
        JLabel lblEdad = new JLabel("Edad:");
        lblEdad.setFont(fuente19);
        lblEdad.setForeground(Color.BLACK);
        lblEdad.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField txtEdad = crearCampoEstilizado("");
        txtEdad.setFont(fuente19);
        txtEdad.setMaximumSize(new Dimension(215, 40));
        txtEdad.setAlignmentX(Component.LEFT_ALIGNMENT);
        colEdad.add(lblEdad);
        colEdad.add(Box.createVerticalStrut(5));
        colEdad.add(txtEdad);

        // Columna Género
        JPanel colGen = new JPanel();
        colGen.setLayout(new BoxLayout(colGen, BoxLayout.Y_AXIS));
        colGen.setOpaque(false);
        JLabel lblGen = new JLabel("Género:");
        lblGen.setFont(fuente19);
        lblGen.setForeground(Color.BLACK);
        lblGen.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComboBox<String> cbGen = new JComboBox<>(new String[]{"M", "F"});
        cbGen.setBackground(Color.WHITE);
        cbGen.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        cbGen.setForeground(Color.BLACK);
        cbGen.setFont(fuente19);
        cbGen.setMaximumSize(new Dimension(215, 40));
        cbGen.setAlignmentX(Component.LEFT_ALIGNMENT);
        colGen.add(lblGen);
        colGen.add(Box.createVerticalStrut(5));
        colGen.add(cbGen);

        filaInfo.add(colEdad);
        filaInfo.add(Box.createHorizontalStrut(20));
        filaInfo.add(colGen);
        panel.add(filaInfo);
        panel.add(Box.createVerticalStrut(espacioEntreSecciones));

        // --- CONTRASEÑA ---
        // --- CONTRASEÑA ---
        panel.add(crearFilaContenedora("Contraseña:", fuente19, dimCampo));
        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(fuente19);
        txtPass.setBackground(Color.WHITE);
        txtPass.setForeground(Color.BLACK);
        txtPass.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        txtPass.setMaximumSize(dimCampo);
        txtPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(txtPass);

// Mensaje de validación (Debajo del campo)
        JLabel lblVal = new JLabel("Mín. 8 caracteres, 1 Símbolo, 1 Mayúscula");
        lblVal.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblVal.setForeground(Color.GRAY);
        lblVal.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblVal);

        txtPass.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                // CORRECCIÓN: Convertir char[] a String de forma segura para validar
                String p = new String(txtPass.getPassword());

                // Lógica de validación:
                // 1. Longitud >= 8
                // 2. Contiene al menos una mayúscula (!p.equals(p.toLowerCase()))
                // 3. Contiene al menos un símbolo (Regex: .*[!@#$%^&*()].*)
                boolean largoOk = p.length() >= 8;
                boolean mayusOk = !p.equals(p.toLowerCase()) && !p.equals("");
                boolean symbolOk = p.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

                if (largoOk && mayusOk && symbolOk) {
                    lblVal.setText("Contraseña segura");
                    lblVal.setForeground(new Color(0, 150, 0)); // Verde oscuro para legibilidad
                } else {
                    lblVal.setText("Mín. 8 caracteres, 1 Símbolo, 1 Mayúscula");
                    lblVal.setForeground(new Color(200, 0, 0)); // Rojo si no cumple
                }

                // Si el campo está vacío, volver al color gris original
                if (p.isEmpty()) {
                    lblVal.setForeground(Color.GRAY);
                }
            }
        });
        panel.add(Box.createVerticalStrut(espacioEntreSecciones));

        panel.add(crearFilaContenedora("Tipo de cuenta:", fuente19, dimCampo));
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Publica", "Privada"});
        cbTipo.setFont(fuente19);
        cbTipo.setBackground(Color.WHITE);
        cbTipo.setForeground(Color.BLACK);
        cbTipo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        cbTipo.setMaximumSize(dimCampo);
        cbTipo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(cbTipo);

        panel.add(Box.createVerticalStrut(30));
        JButton btnFoto = new JButton("Seleccionar Foto de Perfil");
        btnFoto.setFont(fuente19);
        btnFoto.setMaximumSize(dimCampo);
        btnFoto.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnFoto.addActionListener(e -> {
            String rutaImagen = abrirExploradorArchivos();
            if (rutaImagen != null) {
                this.fotoPerfil = new ImageIcon(rutaImagen, rutaImagen); // descripción = ruta
                btnFoto.setText("Foto Seleccionada");
                btnFoto.setForeground(Color.BLACK);
            }
        });

        panel.add(btnFoto);

        panel.add(Box.createVerticalStrut(15));
        JButton btnReg = new JButton("Registrarse");
        btnReg.setBackground(new Color(0, 149, 246));
        btnReg.setForeground(Color.WHITE);
        btnReg.setFont(fuente19);
        btnReg.setMaximumSize(dimCampo); // Mismo ancho que btnFoto
        btnReg.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnReg.addActionListener(e -> {
            String password = new String(txtPass.getPassword());
            ArrayList<Integer> opciones = logica.setDatos(txtNombre.getText(), txtUser.getText(), password,
                    String.valueOf(cbGen.getSelectedItem()), txtEdad.getText(),
                    fotoPerfil, String.valueOf(cbTipo.getSelectedItem()).toUpperCase());

            String errorVacio = "No puede dejar este campo vacio";

            if (opciones.contains(1)) {
                JOptionPane.showMessageDialog(null, "Cuenta creada");
                return;
            }

            // Procesar cada error encontrado
            if (opciones.contains(2)) {
                aplicarErrorCampo(txtNombre, errorVacio);
            }
            if (opciones.contains(3)) {
                aplicarErrorCampo(txtUser, errorVacio);
            }
            if (opciones.contains(7)) {
                aplicarErrorCampo(txtUser, "Este usuario ya existe");
            }
            if (opciones.contains(8)) {
                txtPass.setEchoChar((char) 0);
                aplicarErrorCampo(txtPass, "Contraseña invalida");
                txtPass.setEchoChar('•');
            }
            if (opciones.contains(4)) {
                aplicarErrorCampo(txtEdad, errorVacio);
            }
            if (opciones.contains(5)) {
                aplicarErrorCampo(txtEdad, "Edad invalida");
            }
            if (opciones.contains(6)) {
                txtPass.setEchoChar((char) 0); // Quita los puntos para leer el error
                txtPass.setText(errorVacio);
                txtPass.setForeground(Color.RED);

                txtPass.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (new String(txtPass.getPassword()).equals(errorVacio)) {
                            txtPass.setText("");
                            txtPass.setEchoChar('•'); // Vuelve a poner los puntos de seguridad
                            txtPass.setForeground(Color.BLACK);
                        }
                    }
                });
            }

        });

        panel.add(btnReg);

        panel.add(Box.createVerticalStrut(15));

        JButton btnYaTengo = new JButton("¿Ya tienes una cuenta? Inicia sesión");
        btnYaTengo.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnYaTengo.setForeground(new Color(0, 149, 246));
        btnYaTengo.setContentAreaFilled(false);
        btnYaTengo.setBorderPainted(false);
        btnYaTengo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnYaTengo.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnYaTengo.addActionListener(e -> panelRegistrar());

        panel.add(btnYaTengo);

        panel.add(Box.createVerticalGlue());
        add(contenedor);
        revalidate();
        repaint();
    }

    private void aplicarErrorCampo(JTextField campo, String mensaje) {
        campo.setText(mensaje);
        campo.setForeground(Color.RED);
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(mensaje)) {
                    campo.setText("");
                    campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    campo.setForeground(Color.BLACK);
                }
            }
        });
    }

    private String abrirExploradorArchivos() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Seleccionar Foto de Perfil");

        // Filtro para que solo acepte imágenes
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Imágenes (JPG, PNG, GIF)", "jpg", "png", "gif");
        selector.setFileFilter(filtro);

        int resultado = selector.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            return selector.getSelectedFile().getPath();
        }
        return null; // El usuario canceló la selección
    }

    private JPanel crearFilaContenedora(String texto, Font font, Dimension dim) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setMaximumSize(dim); // Esto centra el panel de la label respecto al campo
        JLabel l = new JLabel(texto);
        l.setFont(font);
        l.setForeground(Color.BLACK);
        p.add(l);
        return p;
    }

    private JPanel crearColumnaInterna(String texto, Font font, int ancho) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        col.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(texto);
        l.setFont(font);
        l.setForeground(Color.BLACK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        col.add(l);
        col.add(Box.createVerticalStrut(5));
        return col;
    }

    public void panelFeed() {
        getContentPane().removeAll();
        JPanel contenedor = crearContenedorConFondo();
        JPanel panel = (JPanel) contenedor.getComponent(0);
        panel.setLayout(new BorderLayout());

        // --- BARRA LATERAL (Izquierda) ---
        construirSidebar();

        // --- ÁREA CENTRAL (Feed) ---
        JPanel feedArea = new JPanel();
        feedArea.setLayout(new BoxLayout(feedArea, BoxLayout.Y_AXIS));
        feedArea.setBackground(new Color(250, 250, 250));

        try {
            ArrayList<Publicacion> miFeed = logica.getFeed();

            // 3. Dibujar las tarjetas
            for (Publicacion p : miFeed) {
                JPanel postCard = crearTarjetaPost(p); // El método que definimos antes

                JPanel centralizer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
                centralizer.setOpaque(false);
                centralizer.add(postCard);
                feedArea.add(centralizer);
            }

            // Mensaje si el feed está vacío
            if (miFeed.isEmpty()) {
                JLabel vacio = new JLabel("No hay publicaciones nuevas. ¡Sigue a más personas!");
                vacio.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
                vacio.setAlignmentX(Component.CENTER_ALIGNMENT);
                feedArea.add(vacio);
            }

        } catch (IOException e) {
            System.out.println("Error al cargar el feed: " + e.getMessage());
        }
        // --- CONFIGURACIÓN DEL SCROLL ---
        JScrollPane scroll = new JScrollPane(feedArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(25); // Movimiento suave
        scroll.setBorder(null);

        // Unimos todo al contenedor principal
        panel.add(sidebarPanel, BorderLayout.WEST);
        panel.add(scroll, BorderLayout.CENTER);

        add(contenedor);
        revalidate();
        repaint();
    }

    // --- MÉTODO UNIVERSAL PARA EL SIDEBAR (Para no repetir código) ---
    private void construirSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(220, 786));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        String[] menu = {"🏠 Inicio", "🔍 Buscar", "✉️ Mensajes", "❤️ Notificaciones", "➕ Crear", "👤 Perfil", "≡ Configuración"};
        sidebarPanel.add(Box.createVerticalStrut(30));

        for (String opcion : menu) {
            JButton btnMenu = new JButton(opcion);
            btnMenu.setFont(new Font("SansSerif", Font.PLAIN, 16));
            btnMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnMenu.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 10));
            btnMenu.setContentAreaFilled(false);
            btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnMenu.addActionListener(e -> {
                if (opcion.contains("Inicio")) {
                    panelFeed();
                }
                if (opcion.contains("Buscar")) {
                    panelBuscar();
                }
                if (opcion.contains("Perfil")) {
                    panelPerfil();
                }
                if (opcion.contains("Configuración")) {
                    panelConfiguracion();
                }
                if (opcion.contains("Crear")) {
                    panelCrearPublicacion();
                }
                if (opcion.contains("Mensajes")) {
                    panelMensajes();
                }
                // Agregar los demás según necesites
            });
            sidebarPanel.add(btnMenu);
        }
    }

    private JPanel crearTarjetaPost(Publicacion publi) {
        JPanel post = new JPanel(new BorderLayout());
        post.setBackground(Color.WHITE);
        post.setBorder(BorderFactory.createLineBorder(new Color(219, 219, 219)));
        int ancho = 600;

        // --- HEADER --- (Igual al anterior)
        JLabel userHeader = new JLabel("  " + publi.getAutor());
        userHeader.setPreferredSize(new Dimension(ancho, 40));
        userHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        post.add(userHeader, BorderLayout.NORTH);

        // --- IMAGEN --- (Igual al anterior)
        if (publi.getRutaImagen() != null && !publi.getRutaImagen().isEmpty()) {
            JLabel lblImg = new JLabel();
            try {
                ImageIcon icon = new ImageIcon(publi.getRutaImagen());
                Image img = icon.getImage().getScaledInstance(ancho, 500, Image.SCALE_SMOOTH);
                lblImg.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                lblImg.setText("Error imagen");
            }
            post.add(lblImg, BorderLayout.CENTER);
        }

        // --- FOOTER DINÁMICO ---
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de Botones (Like y Comentario)
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelAcciones.setOpaque(false);

        JButton btnLike = new JButton(publi.tieneLikeDe(logica.getUsuarioUser(0)) ? "❤️" : "🤍");
        btnLike.setContentAreaFilled(false);
        btnLike.setBorder(null);
        btnLike.setFont(new Font("Serif", Font.PLAIN, 24));

        JButton btnComentar = new JButton("💬");
        btnComentar.setContentAreaFilled(false);
        btnComentar.setBorder(null);
        btnComentar.setFont(new Font("Serif", Font.PLAIN, 24));

        panelAcciones.add(btnLike);
        panelAcciones.add(btnComentar);

        // Contador de Likes
        JLabel lblLikes = new JLabel(publi.getCantLikes() + " Me gusta");
        lblLikes.setFont(new Font("SansSerif", Font.BOLD, 13));

        // Texto de la publicación
        JLabel lblCaption = new JLabel("<html><b>" + publi.getAutor() + "</b> " + publi.getContenido() + "</html>");

        // Área de comentarios
        JPanel panelComentarios = new JPanel();
        panelComentarios.setLayout(new BoxLayout(panelComentarios, BoxLayout.Y_AXIS));
        panelComentarios.setOpaque(false);

        for (String c : publi.getComentarios()) {
            JLabel l = new JLabel("<html>" + c + "</html>");
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            panelComentarios.add(l);
        }

        // --- EVENTOS ---
        // Acción de Like
        btnLike.addActionListener(e -> {
            publi.pushLike(logica.getUsuarioUser(0));
            // Guardar cambio en archivo (debes implementar esto en logica)
            logica.actualizarPublicacion(publi);
            btnLike.setText(publi.tieneLikeDe(logica.getUsuarioUser(0)) ? "❤️" : "🤍");
            lblLikes.setText(publi.getCantLikes() + " Me gusta");
        });

        // Acción de Comentar
        btnComentar.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Escribe un comentario:");
            if (input != null && !input.trim().isEmpty()) {
                String nuevoComentario = "<b>" + logica.getUsuarioUser(0) + "</b> " + input;
                publi.addComentario(nuevoComentario);
                logica.actualizarPublicacion(publi);

                // Refresco instantáneo de la lista de comentarios
                JLabel l = new JLabel("<html>" + nuevoComentario + "</html>");
                panelComentarios.add(l);
                panelComentarios.revalidate();
                panelComentarios.repaint();
            }
        });

        footer.add(panelAcciones);
        footer.add(lblLikes);
        footer.add(Box.createVerticalStrut(5));
        footer.add(lblCaption);
        footer.add(Box.createVerticalStrut(10));
        footer.add(panelComentarios);

        post.add(footer, BorderLayout.SOUTH);
        post.setMaximumSize(new Dimension(ancho, post.getPreferredSize().height));

        return post;
    }

    //perfiles
    public void panelPerfil() {
        getContentPane().removeAll();
        JPanel contenedor = crearContenedorConFondo();
        JPanel panel = (JPanel) contenedor.getComponent(0);
        panel.setLayout(new BorderLayout());

        // --- 1. BARRA LATERAL (Sidebar compacta para dar espacio al grid) ---
        construirSidebar();
        if (sidebarPanel != null) {
            sidebarPanel.setPreferredSize(new Dimension(220, 786)); // Sidebar más delgada
        }

        // --- 2. ÁREA DE CONTENIDO ---
        JPanel profileContent = new JPanel();
        profileContent.setLayout(new BoxLayout(profileContent, BoxLayout.Y_AXIS));
        profileContent.setBackground(Color.WHITE);

        // Encabezado con nueva jerarquía
        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(935, 450)); // Aumentamos alto para el botón abajo
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));
        header.setOpaque(false);

        // Foto de Perfil (320x320)
        JPanel fotoCircular = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ImageIcon foto = logica.getUsuarioFoto(0);
                Shape circulo = new java.awt.geom.Ellipse2D.Float(0, 0, getWidth(), getHeight());
                g2.setClip(circulo);
                if (foto != null) {
                    g2.drawImage(foto.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setColor(new Color(239, 239, 239));
                    g2.fill(circulo);
                }
                g2.setClip(null);
                g2.setColor(new Color(219, 219, 219));
                g2.draw(circulo);
            }
        };
        fotoCircular.setBounds(40, 40, 320, 320); // Medida exacta 320x320
        header.add(fotoCircular);

        int textoX = 390;

        // 1. Nombre de Usuario (Top)
        JLabel lblUser = new JLabel(logica.getUsuarioUser(0));
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblUser.setBounds(textoX, 50, 300, 30);
        header.add(lblUser);

        // 2. Nombre Real
        JLabel lblNombre = new JLabel(logica.getUsuarioNombre(0));
        lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblNombre.setForeground(Color.GRAY);
        lblNombre.setBounds(textoX, 80, 300, 25);
        header.add(lblNombre);

        // 3. Estadísticas
        int followers = logica.getUsuarioFollowers(0);
        int following = logica.getUsuarioFollowing(0);
        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        panelStats.setOpaque(false);
        panelStats.setBounds(textoX, 115, 500, 30);

        JLabel lblPosts = new JLabel("<html><b>0</b> publicaciones</html>");
        lblPosts.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JButton btnFollowers = new JButton("<html><b>" + followers + "</b> seguidores</html>");
        btnFollowers.setContentAreaFilled(false);
        btnFollowers.setBorderPainted(false);
        btnFollowers.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnFollowers.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFollowers.addActionListener(e -> mostrarListaUsuarios(
                btnFollowers, "Seguidores", logica.getListaNombresFollowers(0)
        ));

        JButton btnFollowing = new JButton("<html><b>" + following + "</b> seguidos</html>");
        btnFollowing.setContentAreaFilled(false);
        btnFollowing.setBorderPainted(false);
        btnFollowing.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnFollowing.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFollowing.addActionListener(e -> mostrarListaUsuarios(
                btnFollowing, "Seguidos", logica.getListaNombresFollowing(0)
        ));

        panelStats.add(lblPosts);
        panelStats.add(btnFollowers);
        panelStats.add(btnFollowing);
        header.add(panelStats);

        // 4. Descripción / Biografía
        JTextArea txtBio = new JTextArea(logica.getUsuarioBio(0));
        txtBio.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtBio.setBounds(textoX, 155, 450, 80);
        txtBio.setLineWrap(true);
        txtBio.setWrapStyleWord(true);
        txtBio.setOpaque(false);
        txtBio.setEditable(false);
        header.add(txtBio);

        // 5. BOTÓN EDITAR PERFIL (Ahora debajo de la descripción)
        JButton btnConfig = new JButton("Editar Perfil");
        btnConfig.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnConfig.setBounds(textoX, 245, 150, 32); // Posicionado en Y=245
        btnConfig.setBackground(new Color(239, 239, 239));
        btnConfig.setBorder(BorderFactory.createLineBorder(new Color(219, 219, 219)));
        btnConfig.setFocusPainted(false);
        btnConfig.addActionListener(e -> panelConfiguracion());
        header.add(btnConfig);

        profileContent.add(header);
        profileContent.add(new JSeparator());

        // Grid de 4 columnas (Miniaturas 300x300)
        JPanel gridContainer = new JPanel(new GridLayout(0, 4, 10, 10));
        gridContainer.setBackground(Color.WHITE);
        gridContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int i = 0; i < 12; i++) {
            JPanel postThumb = new JPanel();
            postThumb.setPreferredSize(new Dimension(300, 300)); // Medida exacta 300x300
            postThumb.setBackground(new Color(250, 250, 250));
            postThumb.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
            gridContainer.add(postThumb);
        }
        profileContent.add(gridContainer);

        JScrollPane scroll = new JScrollPane(profileContent);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(sidebarPanel, BorderLayout.WEST);
        panel.add(scroll, BorderLayout.CENTER);

        add(contenedor);
        revalidate();
        repaint();
    }

    public void panelPerfilPersona() {
        getContentPane().removeAll();
        JPanel contenedor = crearContenedorConFondo();
        JPanel panel = (JPanel) contenedor.getComponent(0);
        panel.setLayout(new BorderLayout());

        // Llamada a la sidebar angosta
        construirSidebar();

        JButton btnVolver = new JButton("← Volver");
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnVolver.setContentAreaFilled(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setForeground(new Color(0, 149, 246));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> panelBuscar());
        // Verificación en consola
        imprimirSeguidoresConsola();

        JPanel profileContent = new JPanel();
        profileContent.setLayout(new BoxLayout(profileContent, BoxLayout.Y_AXIS));
        profileContent.setBackground(Color.WHITE);

        // ENCABEZADO (Ajustado a 450 de alto para que quepan botones abajo)
        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(935, 450));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));
        header.setOpaque(false);

        JPanel fotoCircular = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ImageIcon foto = logica.getUsuarioFoto(1);
                Shape circulo = new java.awt.geom.Ellipse2D.Float(0, 0, getWidth(), getHeight());
                g2.setClip(circulo);
                if (foto != null) {
                    g2.drawImage(foto.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setColor(new Color(239, 239, 239));
                    g2.fill(circulo);
                }
                g2.setClip(null);
                g2.setColor(new Color(219, 219, 219));
                g2.draw(circulo);
            }
        };
        fotoCircular.setBounds(40, 40, 320, 320); // Alineación a la izquierda
        header.add(fotoCircular);

        int textoX = 390; // Nueva coordenada X para textos

        // 1. Username (Arriba)
        JLabel lblUser = new JLabel(logica.getUsuarioUser(1));
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblUser.setBounds(textoX, 50, 300, 30);
        header.add(lblUser);

        // 2. Nombre Real (Debajo del user)
        JLabel lblNombre = new JLabel(logica.getUsuarioNombre(1));
        lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblNombre.setForeground(Color.GRAY);
        lblNombre.setBounds(textoX, 80, 300, 25);
        header.add(lblNombre);

        // 3. Stats
        int followers = logica.getUsuarioFollowers(1);
        int following = logica.getUsuarioFollowing(1);
        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        panelStats.setOpaque(false);
        panelStats.setBounds(textoX, 115, 500, 30);

        JLabel lblPosts = new JLabel("<html><b>0</b> posts</html>");
        lblPosts.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JButton btnFollowers = new JButton("<html><b>" + followers + "</b> seguidores</html>");
        btnFollowers.setContentAreaFilled(false);
        btnFollowers.setBorderPainted(false);
        btnFollowers.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnFollowers.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFollowers.addActionListener(e -> mostrarListaUsuarios(
                btnFollowers, "Seguidores", logica.getListaNombresFollowers(1)
        ));

        JButton btnFollowing = new JButton("<html><b>" + following + "</b> seguidos</html>");
        btnFollowing.setContentAreaFilled(false);
        btnFollowing.setBorderPainted(false);
        btnFollowing.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnFollowing.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFollowing.addActionListener(e -> mostrarListaUsuarios(
                btnFollowing, "Seguidos", logica.getListaNombresFollowing(1)
        ));

        panelStats.add(lblPosts);
        panelStats.add(btnFollowers);
        panelStats.add(btnFollowing);
        header.add(panelStats);

        // 4. Bio
        JTextArea txtBio = new JTextArea(logica.getUsuarioBio(1));
        txtBio.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtBio.setBounds(textoX, 150, 450, 80);
        txtBio.setLineWrap(true);
        txtBio.setWrapStyleWord(true);
        txtBio.setOpaque(false);
        txtBio.setEditable(false);
        header.add(txtBio);

        // 5. Botones (Debajo de la Bio)
        // 5. Botones (Debajo de la Bio)
        try {
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            panelBotones.setOpaque(false);
            panelBotones.setBounds(textoX - 5, 240, 450, 40);

            JButton btnPrincipal = new JButton();
            btnPrincipal.setFont(new Font("SansSerif", Font.BOLD, 13));
            btnPrincipal.setPreferredSize(new Dimension(140, 32));
            btnPrincipal.setFocusPainted(false);
            btnPrincipal.setBorderPainted(false);
            btnPrincipal.setOpaque(true); // Necesario para ver el background en Mac/Linux

            // --- LOGICA DE ACTUALIZACIÓN ---
            btnPrincipal.addActionListener(e -> {
                try {
                    // Ejecuta la lógica de seguir/dejar de seguir en los archivos
                    logica.addFollowing(logica.getUsuario(1));

                    // REFRESCAMOS EL TEXTO Y COLOR INMEDIATAMENTE
                    refrescarBotonSeguir(btnPrincipal);

                } catch (IOException ex) {
                    System.out.println("Error en la persistencia: " + ex.getMessage());
                }
            });

            // Estado inicial al cargar el perfil
            refrescarBotonSeguir(btnPrincipal);

            panelBotones.add(btnPrincipal);

            // Botón de Mensaje (Solo si no es privada o si ya lo sigues)
            if (!logica.getUsuarioTipo(1).equals("Privada") || logica.isUsuarioLoggedFollower(logica.getUsuario(1)) == 1) {
                JButton btnMensaje = new JButton("Mensaje");
                btnMensaje.setPreferredSize(new Dimension(100, 32));
                btnMensaje.setBackground(new Color(239, 239, 239));
                btnMensaje.setFocusPainted(false);
                btnMensaje.setBorderPainted(false);
                btnMensaje.setOpaque(true);
                panelBotones.add(btnMensaje);
            }

            header.add(panelBotones);
        } catch (IOException ex) {
            System.out.println("Error en relación de seguimiento");
        }

        JPanel barraVolver = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        barraVolver.setOpaque(false);
        barraVolver.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        barraVolver.add(btnVolver);
        profileContent.add(barraVolver);
        profileContent.add(header);
        profileContent.add(new JSeparator());

        // --- GRID DE 4 COLUMNAS ---
        JPanel gridContainer = new JPanel();
        gridContainer.setBackground(Color.WHITE);
        try {
            if (logica.getUsuarioTipo(1).equals("Publica") || logica.isUsuarioLoggedFollower(logica.getUsuario(1)) == 1) {
                gridContainer.setLayout(new GridLayout(0, 4, 10, 10));
                gridContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                for (int i = 0; i < 12; i++) {
                    JPanel postThumb = new JPanel();
                    postThumb.setPreferredSize(new Dimension(300, 300));
                    postThumb.setBackground(new Color(250, 250, 250));
                    postThumb.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
                    gridContainer.add(postThumb);
                }
            } else {
                gridContainer.setLayout(new GridBagLayout());
                gridContainer.setPreferredSize(new Dimension(935, 300));
                gridContainer.add(new JLabel("<html><center>🔒<br><b>Esta cuenta es privada</b></center></html>"));
            }
        } catch (IOException ex) {
            System.getLogger(GUI.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        profileContent.add(gridContainer);
        JScrollPane scroll = new JScrollPane(profileContent);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(sidebarPanel, BorderLayout.WEST);
        panel.add(scroll, BorderLayout.CENTER);

        add(contenedor);
        revalidate();
        repaint();
    }

    private void mostrarListaUsuarios(JButton origen, String titulo, String listaNombres) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(new Color(219, 219, 219)));

        // Título no clicable
        JMenuItem lblTitulo = new JMenuItem(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitulo.setEnabled(false);
        lblTitulo.setForeground(Color.BLACK);
        popup.add(lblTitulo);
        popup.addSeparator();

        if (listaNombres == null || listaNombres.equals("Nadie")) {
            JMenuItem vacio = new JMenuItem("Nadie aún");
            vacio.setEnabled(false);
            popup.add(vacio);
        } else {
            String[] nombres = listaNombres.split(", ");
            for (String nombre : nombres) {
                if (nombre.isBlank()) {
                    continue;
                }

                JMenuItem item = new JMenuItem(nombre);
                item.setFont(new Font("SansSerif", Font.PLAIN, 13));
                item.setPreferredSize(new Dimension(200, 35));
                item.setCursor(new Cursor(Cursor.HAND_CURSOR));

                item.addActionListener(e -> {
                    // Al hacer clic en un nombre, ir a su perfil
                    logica.setUsuarioSelec(nombre.trim());
                    panelPerfilPersona();
                });
                popup.add(item);
            }
        }

        // Mostrar debajo del botón que se clickeó
        popup.show(origen, 0, origen.getHeight());
    }

    private void refrescarBotonSeguir(JButton btn) {
        try {
            if (logica.getUsuario(1) == null) {
                return;
            }

            int relacion = logica.isUsuarioLoggedFollower(logica.getUsuario(1));

            if (relacion == 3 || relacion == 4) { // No se sigue o se eliminó
                btn.setText("Seguir");
                btn.setBackground(new Color(0, 149, 246)); // Azul
                btn.setForeground(Color.WHITE);
            } else { // Siguiendo (1) o Pendiente (2)
                btn.setText(relacion == 1 ? "Siguiendo" : "Pendiente");
                btn.setBackground(new Color(239, 239, 239)); // Gris
                btn.setForeground(Color.BLACK);
            }

            // Forzar al componente a repintarse
            btn.revalidate();
            btn.repaint();

        } catch (IOException ex) {
            btn.setText("Seguir");
            System.out.println("Error al refrescar estado del botón");
            btn.revalidate();
            btn.repaint();
        }
    }

    //configuracion
    private void panelConfiguracion() {
        getContentPane().removeAll();

        JPanel contenedorBase = crearContenedorConFondo();
        JPanel panelBlanco = (JPanel) contenedorBase.getComponent(0);
        panelBlanco.setLayout(new BorderLayout());

        this.fotoPerfil = null; // Reiniciar al entrar al panel
        this.fotoFueCambiada = false;
        Font fuente19 = new Font("SansSerif", Font.BOLD, 19);
        Font fuentePlain19 = new Font("SansSerif", Font.PLAIN, 19);
        int espacioEntreSecciones = 20;
        Dimension dimCampo = new Dimension(450, 40);

        construirSidebar();

        // --- PANEL DE CONTENIDO ---
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel lblTitulo = new JLabel("Configuración de Perfil");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenido.add(lblTitulo);
        contenido.add(Box.createVerticalStrut(30));

        // Nombre y Usuario
        contenido.add(crearFilaContenedora("Nombre Completo:", fuente19, dimCampo));
        JTextField txtNombre = crearCampoEstilizado(logica.getUsuarioNombre(0));
        txtNombre.setFont(fuentePlain19);
        txtNombre.setMaximumSize(dimCampo);
        contenido.add(txtNombre);
        contenido.add(Box.createVerticalStrut(espacioEntreSecciones));

        contenido.add(crearFilaContenedora("Nombre de Usuario:", fuente19, dimCampo));
        JTextField txtUser = crearCampoEstilizado(logica.getUsuarioUser(0));
        txtUser.setFont(fuentePlain19);
        txtUser.setMaximumSize(dimCampo);
        contenido.add(txtUser);
        contenido.add(Box.createVerticalStrut(espacioEntreSecciones));

        // Fila Edad y Género
        JPanel filaInfo = new JPanel();
        filaInfo.setLayout(new BoxLayout(filaInfo, BoxLayout.X_AXIS));
        filaInfo.setOpaque(false);
        filaInfo.setMaximumSize(dimCampo);

        JPanel colEdad = new JPanel();
        colEdad.setLayout(new BoxLayout(colEdad, BoxLayout.Y_AXIS));
        colEdad.setOpaque(false);
        JLabel lblEdad = new JLabel("Edad:");
        lblEdad.setFont(fuente19);
        lblEdad.setForeground(Color.BLACK);
        lblEdad.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField txtEdad = crearCampoEstilizado(logica.getUsuarioEdad(0));
        txtEdad.setFont(fuentePlain19);
        txtEdad.setMaximumSize(new Dimension(215, 40));
        txtEdad.setAlignmentX(Component.LEFT_ALIGNMENT);
        colEdad.add(lblEdad);
        colEdad.add(Box.createVerticalStrut(5));
        colEdad.add(txtEdad);

        JPanel colGen = new JPanel();
        colGen.setLayout(new BoxLayout(colGen, BoxLayout.Y_AXIS));
        colGen.setOpaque(false);
        JLabel lblGen = new JLabel("Género:");
        lblGen.setFont(fuente19);
        lblGen.setForeground(Color.BLACK);
        lblGen.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> cbGen = new JComboBox<>(new String[]{"M", "F"});
        cbGen.setSelectedItem(logica.getUsuarioGenero(0));
        cbGen.setBackground(Color.WHITE);
        cbGen.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        cbGen.setForeground(Color.BLACK);
        cbGen.setFont(fuente19);
        cbGen.setMaximumSize(new Dimension(215, 40));
        cbGen.setAlignmentX(Component.LEFT_ALIGNMENT);

        colGen.add(lblGen);
        colGen.add(Box.createVerticalStrut(5));
        colGen.add(cbGen);

        filaInfo.add(colEdad);
        filaInfo.add(Box.createHorizontalStrut(20));
        filaInfo.add(colGen);
        contenido.add(filaInfo);
        contenido.add(Box.createVerticalStrut(espacioEntreSecciones));

        // Contraseña
        contenido.add(crearFilaContenedora("Contraseña:", fuente19, dimCampo));
        JPasswordField txtPass = new JPasswordField(logica.getUsuarioContra(0));
        txtPass.setFont(fuentePlain19);
        txtPass.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        txtPass.setMaximumSize(dimCampo);
        contenido.add(txtPass);

        JLabel lblVal = new JLabel("Mín. 8 caracteres, 1 Símbolo, 1 Mayúscula");
        lblVal.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblVal.setForeground(Color.GRAY);
        lblVal.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenido.add(lblVal);
        contenido.add(Box.createVerticalStrut(espacioEntreSecciones));

        // Tipo de Cuenta
        contenido.add(crearFilaContenedora("Tipo de cuenta:", fuente19, dimCampo));
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Publica", "Privada"});
        cbTipo.setSelectedItem(logica.getUsuarioTipo(0));
        cbTipo.setFont(fuentePlain19);
        cbTipo.setBackground(Color.WHITE);
        cbTipo.setMaximumSize(dimCampo);
        contenido.add(cbTipo);
        contenido.add(Box.createVerticalStrut(espacioEntreSecciones));

        // --- DESCRIPCIÓN (Agregado correctamente al panel) ---
        contenido.add(crearFilaContenedora("Descripción:", fuente19, dimCampo));
        JTextArea txtBio = new JTextArea(logica.getUsuarioBio(0));
        txtBio.setFont(fuentePlain19);
        txtBio.setLineWrap(true);
        txtBio.setWrapStyleWord(true);

        JScrollPane scrollBio = new JScrollPane(txtBio);
        scrollBio.setMaximumSize(new Dimension(450, 100)); // Altura fija para que no tape nada
        scrollBio.setPreferredSize(new Dimension(450, 100));
        scrollBio.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        contenido.add(scrollBio);
        contenido.add(Box.createVerticalStrut(25));

        // Foto
        JButton btnFoto = new JButton("Cambiar Foto de Perfil");
        btnFoto.setFont(fuente19);
        btnFoto.setMaximumSize(dimCampo);
        btnFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnFoto.addActionListener(e -> {
            String ruta = abrirExploradorArchivos();
            if (ruta != null) {
                this.fotoPerfil = new ImageIcon(ruta, ruta); // ✅ descripción = ruta
                this.fotoFueCambiada = true;
                btnFoto.setText("Foto Seleccionada");
            }
        });
        contenido.add(btnFoto);
        contenido.add(Box.createVerticalStrut(15));

        // --- BOTONES DE ACCIÓN ---
        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(new Color(0, 149, 246));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(fuente19);
        btnGuardar.setMaximumSize(dimCampo);
        btnGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGuardar.addActionListener(e -> {
            ImageIcon fotoEnviar = (fotoFueCambiada) ? this.fotoPerfil : null;
            ArrayList<Integer> opciones = logica.modificarDatos(txtNombre.getText(), txtUser.getText(), new String(txtPass.getPassword()),
                    String.valueOf(cbGen.getSelectedItem()), txtEdad.getText(), fotoEnviar, String.valueOf(cbTipo.getSelectedItem()).toUpperCase(), txtBio.getText());
            if (opciones.contains(8)) {
                aplicarErrorCampo(txtUser, "Este usuario ya existe");
            }
            if (opciones.contains(9)) {
                aplicarErrorCampo(txtEdad, "Edad no válida");
            }
            if (!opciones.contains(8) && !opciones.contains(9)) {
                fotoFueCambiada = false;
            }
        });
        contenido.add(btnGuardar);
        contenido.add(Box.createVerticalStrut(15));

        JButton btnLogout = new JButton("🚪 Cerrar Sesión");
        estilizarBotonAccion(btnLogout, dimCampo, Color.DARK_GRAY);
        btnLogout.addActionListener(e -> {
            logica.cerrarSesion();
            panelRegistrar();
        });
        contenido.add(btnLogout);
        contenido.add(Box.createVerticalStrut(10));

        JButton btnEliminar = new JButton("❌ Desactivar Cuenta");
        estilizarBotonAccion(btnEliminar, dimCampo, new Color(200, 0, 0));
        btnEliminar.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "¿Seguro?", "Alerta", 0) == 0) {
                logica.getUsuario(0).setEstado("INACTIVA");
                logica.cerrarSesion();
                panelRegistrar();
            }
        });
        contenido.add(btnEliminar);
        contenido.add(Box.createVerticalGlue());

        // --- ENSAMBLAJE ---
        JScrollPane scrollPrincipal = new JScrollPane(contenido);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(16); // Scroll más suave

        panelBlanco.add(sidebarPanel, BorderLayout.WEST);
        panelBlanco.add(scrollPrincipal, BorderLayout.CENTER);

        add(contenedorBase);
        revalidate();
        repaint();
    }

    private void estilizarBotonAccion(JButton btn, Dimension dim, Color color) {
        btn.setMaximumSize(dim);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setForeground(color);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void panelBuscar() {
        getContentPane().removeAll();

        JPanel contenedorBase = crearContenedorConFondo();
        JPanel panelBlanco = (JPanel) contenedorBase.getComponent(0);
        panelBlanco.setLayout(new BorderLayout());

        construirSidebar();

        // 2. Crear Contenido de Búsqueda
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.WHITE);
        // Ajustamos el margen para que coincida con la estética de Instagram
        contenido.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel lblTitulo = new JLabel("Buscar");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineación forzada
        contenido.add(lblTitulo);
        contenido.add(Box.createVerticalStrut(20));

        // Barra de búsqueda
        JTextField txtBuscar = new JTextField();
        txtBuscar.setMaximumSize(new Dimension(800, 45));
        txtBuscar.setPreferredSize(new Dimension(800, 45));
        txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtBuscar.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineación forzada
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(219, 219, 219), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        contenido.add(txtBuscar);
        contenido.add(Box.createVerticalStrut(25)); // Espacio antes de resultados

        // Panel donde caen los resultados
        JPanel panelResultados = new JPanel();
        panelResultados.setLayout(new BoxLayout(panelResultados, BoxLayout.Y_AXIS));
        panelResultados.setBackground(Color.WHITE);
        panelResultados.setAlignmentX(Component.LEFT_ALIGNMENT); // Clave para las tarjetas

        // Scroll que contiene los resultados
        JScrollPane scroll = new JScrollPane(panelResultados);
        scroll.setBorder(null);
        scroll.setBackground(Color.WHITE);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT); // Clave para que el scroll no se mueva
        contenido.add(scroll);

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String texto = txtBuscar.getText().trim();
                panelResultados.removeAll();
                try {
                    if (!texto.isEmpty()) {
                        if (texto.startsWith("#") || texto.startsWith("@")) {
                            int tipoBusqueda = texto.startsWith("#") ? 2 : 3;
                            ArrayList<Publicacion> resultados = logica.buscar(tipoBusqueda, texto);

                            for (Publicacion publi : resultados) {
                                JPanel cardPost = crearTarjetaPost(publi);
                                cardPost.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinear tarjeta
                                panelResultados.add(cardPost);
                                panelResultados.add(Box.createVerticalStrut(25));

                            }
                        } else {
                            ArrayList<Usuario> resultados = logica.buscar(1, texto);
                            for (Usuario user : resultados) {
                                JPanel cardUser = crearTarjetaUsuario(
                                        user.getNombre(), user.getUser(), String.valueOf(user.getEdad()),
                                        user.getTipoCuenta().name(), user.getEstado().name(),
                                        user.getFecha().toString(), user.getGenero().name(), user.getFotoPerfil()
                                );
                                cardUser.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinear tarjeta
                                panelResultados.add(cardUser);
                                panelResultados.add(Box.createVerticalStrut(10));
                            }
                        }
                    }
                } catch (IOException excepcion) {
                    System.out.println(excepcion.getMessage());
                }

                panelResultados.revalidate();
                panelResultados.repaint();
            }
        });

        panelBlanco.add(sidebarPanel, BorderLayout.WEST);
        panelBlanco.add(contenido, BorderLayout.CENTER);

        add(contenedorBase);
        revalidate();
        repaint();
    }

// --- MÉTODO PARA CREAR LAS TARJETAS ---
    private JPanel crearTarjetaUsuario(String nombre, String user, String edad, String tipo, String estado, String fecha, String genero, ImageIcon foto) {
        JPanel tarjeta = new JPanel(new BorderLayout(15, 0));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        tarjeta.setMaximumSize(new Dimension(800, 140));
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Foto de Perfil (Circular o escalada)
        JLabel lblFoto = new JLabel();
        if (foto != null) {
            Image img = foto.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            lblFoto.setIcon(new ImageIcon(img));
        } else {
            // Placeholder si no hay foto
            lblFoto.setOpaque(true);
            lblFoto.setBackground(new Color(240, 240, 240));
            lblFoto.setPreferredSize(new Dimension(80, 80));
            lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
            lblFoto.setText("👤");
        }

        // Información del Usuario
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);

        JLabel lblNombreUser = new JLabel(nombre + " (@" + user + ")");
        lblNombreUser.setFont(new Font("SansSerif", Font.BOLD, 18));

        // Detalle multilineal (Edad, Tipo, Estado, etc.)
        String detalles = String.format("<html><div style='color: gray;'>%s | %s | %s<br>Estado: %s | Creado: %s</div></html>",
                genero, edad + " años", tipo, estado, fecha);
        JLabel lblDetalles = new JLabel(detalles);
        lblDetalles.setFont(new Font("SansSerif", Font.PLAIN, 14));

        info.add(lblNombreUser);
        info.add(lblDetalles);

        tarjeta.add(lblFoto, BorderLayout.WEST);
        tarjeta.add(info, BorderLayout.CENTER);

        // Evento Click para ir al perfil
        tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                // Aquí llamas a tu método para ver el perfil, pasando el username
                logica.setUsuarioSelec(user);
                panelPerfilPersona();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                tarjeta.setBackground(new Color(250, 250, 250));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                tarjeta.setBackground(Color.WHITE);
            }
        });

        return tarjeta;

    }

    //mensajes
    public void panelMensajes() {
        getContentPane().removeAll();
        JPanel contenedor = crearContenedorConFondo();
        JPanel panelPrincipal = (JPanel) contenedor.getComponent(0);
        panelPrincipal.setLayout(new BorderLayout());

        construirSidebar(); // La sidebar de iconos (Inicio, Buscar, etc.)

        // --- PANEL IZQUIERDO: LISTA DE CHATS (Más angosto) ---
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setPreferredSize(new Dimension(350, 0)); // Tamaño fijo para la lista
        panelIzquierdo.setBackground(Color.WHITE);
        panelIzquierdo.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(219, 219, 219)));

        // Header del panel izquierdo
        JPanel headerMensajes = new JPanel(new BorderLayout());
        headerMensajes.setBackground(Color.WHITE);
        headerMensajes.setPreferredSize(new Dimension(350, 60));
        JLabel lblMiUser = new JLabel("  " + logica.getUsuarioUser(0));
        lblMiUser.setFont(new Font("SansSerif", Font.BOLD, 18));

        JButton btnNuevoMsj = new JButton("+ ");
        btnNuevoMsj.setFont(new Font("SansSerif", Font.PLAIN, 24));
        btnNuevoMsj.setContentAreaFilled(false);
        btnNuevoMsj.setBorder(null);
        btnNuevoMsj.addActionListener(e -> mostrarMenuAmigos(btnNuevoMsj));

        headerMensajes.add(lblMiUser, BorderLayout.WEST);
        headerMensajes.add(btnNuevoMsj, BorderLayout.EAST);
        panelIzquierdo.add(headerMensajes, BorderLayout.NORTH);

        // Contenedor de tarjetas de usuarios
        contenedorTarjetas = new JPanel();
        contenedorTarjetas.setLayout(new BoxLayout(contenedorTarjetas, BoxLayout.Y_AXIS));
        contenedorTarjetas.setBackground(Color.WHITE);
        JScrollPane scrollTarjetas = new JScrollPane(contenedorTarjetas);
        scrollTarjetas.setBorder(null);
        panelIzquierdo.add(scrollTarjetas, BorderLayout.CENTER);

        // --- PANEL DERECHO: ÁREA DE CHAT (Mucho más grande) ---
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBackground(Color.WHITE);

        JPanel headerChatActual = new JPanel(new BorderLayout());
        headerChatActual.setBackground(Color.WHITE);
        headerChatActual.setPreferredSize(new Dimension(0, 60));
        headerChatActual.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(219, 219, 219)));

        lblNombreChat = new JLabel("  Selecciona un chat"); // Cambiará en iniciarChatCon
        lblNombreChat.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerChatActual.add(lblNombreChat, BorderLayout.WEST);
        panelDerecho.add(headerChatActual, BorderLayout.NORTH);

// 2. Área de burbujas
        panelChatActual = new JPanel();
        panelChatActual.setLayout(new BoxLayout(panelChatActual, BoxLayout.Y_AXIS));
        panelChatActual.setBackground(Color.WHITE);
        JScrollPane scrollChat = new JScrollPane(panelChatActual);
        scrollChat.setBorder(null);
        panelDerecho.add(scrollChat, BorderLayout.CENTER);

// 3. Barra de entrada más grande
        JPanel barraEntrada = new JPanel(new BorderLayout(15, 0));
        barraEntrada.setBackground(Color.WHITE);
        barraEntrada.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Más margen

        txtMensaje = new JTextField("Escribe un mensaje...");
        txtMensaje.setPreferredSize(new Dimension(0, 45)); // Más alto
        txtMensaje.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtMensaje.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtMensaje.getText().equals("Escribe un mensaje...")) {
                    txtMensaje.setText("");
                    txtMensaje.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtMensaje.getText().isEmpty()) {
                    txtMensaje.setForeground(Color.GRAY);
                    txtMensaje.setText("Escribe un mensaje...");
                }
            }
        });

        btnEnviar = new JButton("Enviar ");
        btnEnviar.setForeground(new Color(0, 149, 246));
        btnEnviar.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnEnviar.setContentAreaFilled(false);
        btnEnviar.setBorder(null);
        btnEnviar.addActionListener(e -> enviarNuevoMensaje());

        barraEntrada.add(txtMensaje, BorderLayout.CENTER);
        barraEntrada.add(btnEnviar, BorderLayout.EAST);
        panelDerecho.add(barraEntrada, BorderLayout.SOUTH);

        mostrarPantallaInicioChat();

        // --- ENSAMBLE FINAL ---
        panelPrincipal.add(sidebarPanel, BorderLayout.WEST); // Sidebar de iconos

        // Usamos un panel central para contener la lista y el chat juntos
        JPanel centroContenedor = new JPanel(new BorderLayout());
        centroContenedor.add(panelIzquierdo, BorderLayout.WEST);
        centroContenedor.add(panelDerecho, BorderLayout.CENTER); // El chat ocupa el resto

        panelPrincipal.add(centroContenedor, BorderLayout.CENTER);

        add(contenedor);
        revalidate();
        repaint();
    }

    private void mostrarPantallaInicioChat() {
        chatSeleccionado = false;
        panelChatActual.removeAll();
        panelChatActual.setLayout(new GridBagLayout()); // Para centrar el contenido

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);

        JLabel icono = new JLabel("📩", SwingConstants.CENTER); // Puedes usar un ImageIcon aquí
        icono.setFont(new Font("SansSerif", Font.PLAIN, 50));

        JLabel titulo = new JLabel("Tus mensajes", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel sub = new JLabel("Envía fotos y mensajes privados a un amigo.", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(Color.GRAY);

        centro.add(icono);
        centro.add(Box.createVerticalStrut(10));
        centro.add(titulo);
        centro.add(sub);

        panelChatActual.add(centro);

        // Bloquear entrada
        txtMensaje.setEnabled(false);
        btnEnviar.setEnabled(false);

        panelChatActual.revalidate();
        panelChatActual.repaint();
    }

    private void añadirBurbuja(Mensaje msj, boolean esMio) {
        // Contenedor alineado a la izquierda o derecha
        JPanel fila = new JPanel(new FlowLayout(esMio ? FlowLayout.RIGHT : FlowLayout.LEFT));
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setOpaque(false);
        columna.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        // El globo con el texto
        JPanel globo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(esMio ? new Color(0, 149, 246) : new Color(239, 239, 239));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            }
        };
        globo.setOpaque(false);
        globo.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel lblTexto = new JLabel();
        if (msj instanceof MensajeTexto) {
            lblTexto.setText("<html><p style='width:180px; margin:0;'>" + msj.getContenido() + "</p></html>");
        } else {
            ImageIcon imag = new ImageIcon(msj.getContenido());
            Image imgEscalada = imag.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblTexto.setIcon(new ImageIcon(imgEscalada));
        }
        lblTexto.setForeground(esMio ? Color.WHITE : Color.BLACK);
        lblTexto.setFont(new Font("SansSerif", Font.PLAIN, 13));
        globo.add(lblTexto, BorderLayout.CENTER);
        globo.setAlignmentX(esMio ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        // Fila inferior: hora + estado (solo en mensajes propios)
        JPanel filaInfo = new JPanel(new FlowLayout(esMio ? FlowLayout.RIGHT : FlowLayout.LEFT, 4, 0));
        filaInfo.setOpaque(false);
        filaInfo.setAlignmentX(esMio ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        // Hora
        JLabel lblHora = new JLabel(msj.getHoraFormateada());
        lblHora.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblHora.setForeground(Color.GRAY);
        filaInfo.add(lblHora);

        // Estado leído (solo en mensajes propios)
        if (esMio) {
            String estadoTexto = msj.isLeido() ? "✓✓" : "✓";
            JLabel lblEstado = new JLabel(estadoTexto);
            lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 10));
            lblEstado.setForeground(msj.isLeido() ? new Color(0, 149, 246) : Color.GRAY);
            filaInfo.add(lblEstado);
        }

        columna.add(globo);
        columna.add(Box.createVerticalStrut(2));
        columna.add(filaInfo);

        fila.add(columna);
        panelChatActual.add(fila);
        panelChatActual.revalidate();

        // Auto-scroll al final
        SwingUtilities.invokeLater(() -> {
            Container parent = panelChatActual.getParent();
            if (parent instanceof JViewport) {
                JScrollPane sp = (JScrollPane) parent.getParent();
                JScrollBar bar = sp.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
            }
        });
    }

    private void enviarNuevoMensaje() {
        String texto = txtMensaje.getText().trim();
        String placeholder = "Escribe un mensaje...";

        if (chatSeleccionado && !texto.isEmpty() && !texto.equals(placeholder)) {
            try {
                logica.enviarMensaje((tipoMensaje.equals("Texto") ? 0 : 1), texto); // lógica crea el Mensaje, guarda y envía
                // Mostrar burbuja del mensaje recién enviado
                ArrayList<Mensaje> msgs = logica.getMensajes();
                añadirBurbuja(msgs.get(msgs.size() - 1), true);
                txtMensaje.setText("");
            } catch (IOException ex) {
                System.out.println("Error al enviar: " + ex.getMessage());
            }
        }
    }

    private void mostrarMenuAmigos(JButton botonOrigen) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(219, 219, 219)));

        try {
            String[] seguidos = logica.getListaNombresFollowing(0).split(", ");

            if (seguidos.length == 0 || seguidos[0].equals("Nadie")) {
                JMenuItem vacio = new JMenuItem("No sigues a nadie");
                vacio.setEnabled(false);
                menu.add(vacio);
            } else {
                for (String nombre : seguidos) {
                    JMenuItem item = new JMenuItem(nombre);
                    item.setPreferredSize(new Dimension(180, 35));
                    item.addActionListener(e -> {
                        añadirTarjetaMensaje(nombre);
                        iniciarChatCon(nombre);
                    });
                    menu.add(item);
                }
            }

            // Mostrar desplazado a la izquierda si es necesario
            menu.show(botonOrigen, -150, botonOrigen.getHeight());

        } catch (Exception e) {
            System.out.println("Error al cargar seguidos: " + e.getMessage());
        }
    }

    private void iniciarChatCon(String usernameAmigo) {
        this.chatSeleccionado = true;
        txtMensaje.setEnabled(true);
        btnEnviar.setEnabled(true);
        this.usuarioChatActivo = usernameAmigo;
        lblNombreChat.setText("  " + usernameAmigo);

        // Abrir chat en lógica (carga historial del archivo)
        logica.abrirChat(usernameAmigo);

        // Registrar listener para mensajes entrantes en tiempo real
        logica.setChatListener(new ChatListener() {
            @Override
            public void onMensajeRecibido(Mensaje mensaje) {
                // Solo mostrar si el mensaje es del chat que tenemos abierto
                if (mensaje.getEmisor().getUser().equals(usuarioChatActivo)) {
                    añadirBurbuja(mensaje, false);
                }
            }

            @Override
            public void onDesconectado() {
                lblNombreChat.setText("  " + usernameAmigo + " (desconectado)");
                txtMensaje.setEnabled(false);
                btnEnviar.setEnabled(false);
            }
        });

        // Limpiar y cargar historial en la UI
        panelChatActual.removeAll();
        panelChatActual.setLayout(new BoxLayout(panelChatActual, BoxLayout.Y_AXIS));

        for (Mensaje m : logica.getMensajes()) {
            boolean esMio = m.esMio(logica.getUsuarioUser(0));
            añadirBurbuja(m, esMio);
        }

        panelChatActual.revalidate();
        panelChatActual.repaint();
    }

    private void añadirTarjetaMensaje(String username) {
        // 1. Evitar duplicados: Si ya existe una tarjeta para este usuario, no la creamos de nuevo
        for (Component c : contenedorTarjetas.getComponents()) {
            if (c instanceof JPanel) {
                JLabel lbl = (JLabel) ((JPanel) c).getClientProperty("userLabel");
                if (lbl != null && lbl.getText().equals(username)) {
                    return; // Ya existe la tarjeta
                }
            }
        }

        // 2. Crear el panel de la tarjeta
        JPanel tarjeta = new JPanel(new BorderLayout(15, 0));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setMaximumSize(new Dimension(350, 70));
        tarjeta.setPreferredSize(new Dimension(350, 70));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Guardar una propiedad para identificarla después
        tarjeta.putClientProperty("userLabel", new JLabel(username));

        // 3. Foto de perfil (puedes adaptarlo para que use logica.getUsuarioFoto si lo deseas)
        JPanel fotoMini = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 230));
                g2.fillOval(0, 0, 45, 45); // Círculo gris de placeholder
            }
        };
        fotoMini.setPreferredSize(new Dimension(45, 45));
        fotoMini.setOpaque(false);

        // 4. Nombre y último mensaje (opcional)
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);

        JLabel lblNombre = new JLabel(username);
        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel lblEstado = new JLabel("Haz clic para chatear");
        lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblEstado.setForeground(Color.GRAY);

        info.add(lblNombre);
        info.add(lblEstado);

        tarjeta.add(fotoMini, BorderLayout.WEST);
        tarjeta.add(info, BorderLayout.CENTER);

        // 5. EVENTO: Al hacer clic, cargar ese chat
        tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                // Cambiar color para indicar selección
                resetearColoresTarjetas();
                tarjeta.setBackground(new Color(245, 245, 245));

                // Cargar el chat en el panel derecho
                iniciarChatCon(username);
            }
        });

        // 6. Añadir al contenedor y refrescar
        contenedorTarjetas.add(tarjeta);
        contenedorTarjetas.revalidate();
        contenedorTarjetas.repaint();
    }

// Método auxiliar para limpiar la selección visual
    private void resetearColoresTarjetas() {
        for (Component c : contenedorTarjetas.getComponents()) {
            c.setBackground(Color.WHITE);
        }
    }

    //publicaciones
    private void panelCrearPublicacion() {
        getContentPane().removeAll();
        JPanel contenedorBase = crearContenedorConFondo();
        JPanel panelBlanco = (JPanel) contenedorBase.getComponent(0);
        panelBlanco.setLayout(new BorderLayout());

        construirSidebar();

        // --- PANEL DE CREACIÓN INTEGRADO (Ocupa todo el centro) ---
        JPanel mainCrear = new JPanel(new BorderLayout());
        mainCrear.setBackground(Color.WHITE);

        // 1. HEADER (Barra superior delgada)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        JLabel lblTitulo = new JLabel("    Crear nueva publicación");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));

        JButton btnCompartir = new JButton("Compartir  ");
        btnCompartir.setForeground(new Color(0, 149, 246));
        btnCompartir.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnCompartir.setContentAreaFilled(false);
        btnCompartir.setBorderPainted(false);
        btnCompartir.setCursor(new Cursor(Cursor.HAND_CURSOR));

        header.add(lblTitulo, BorderLayout.WEST);
        header.add(btnCompartir, BorderLayout.EAST);

        // 2. CUERPO (Dividido en 2 columnas)
        JPanel cuerpo = new JPanel(new GridLayout(1, 2));
        cuerpo.setBackground(Color.WHITE);

        // IZQUIERDA: Zona de imagen
        JPanel panelMedia = new JPanel(new GridBagLayout());
        panelMedia.setBackground(new Color(250, 250, 250));
        panelMedia.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));

        JLabel lblInstruccion = new JLabel("<html><center>🖼️<br><br>Arrastra las fotos aquí</center></html>");
        lblInstruccion.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JButton btnSeleccionar = new JButton("Seleccionar de la computadora");
        btnSeleccionar.setBackground(new Color(0, 149, 246));
        btnSeleccionar.setForeground(Color.WHITE);
        btnSeleccionar.setFocusPainted(false);
        btnSeleccionar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelMedia.add(lblInstruccion, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 0, 0);
        panelMedia.add(btnSeleccionar, gbc);

        // DERECHA: Detalles
        JPanel panelDetalles = new JPanel();
        panelDetalles.setLayout(new BoxLayout(panelDetalles, BoxLayout.Y_AXIS));
        panelDetalles.setBackground(Color.WHITE);
        // Aumentamos un poco el margen izquierdo (30) para que respire
        panelDetalles.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 25));

        // Usuario
        JLabel lblUser = new JLabel("👤  " + logica.getUsuarioUser(0));
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalles.add(lblUser);
        panelDetalles.add(Box.createVerticalStrut(20));

        // TextArea con efecto Placeholder
        String placeholder = "Escribe un pie de foto o mensaje...";
        JTextArea areaTexto = new JTextArea(placeholder);
        areaTexto.setForeground(Color.GRAY); // Color de placeholder
        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        areaTexto.setFont(new Font("SansSerif", Font.PLAIN, 15));
        areaTexto.setBorder(null);
        areaTexto.setAlignmentX(Component.LEFT_ALIGNMENT);

        areaTexto.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (areaTexto.getText().equals(placeholder)) {
                    areaTexto.setText("");
                    areaTexto.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (areaTexto.getText().isEmpty()) {
                    areaTexto.setForeground(Color.GRAY);
                    areaTexto.setText(placeholder);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); // Que ocupe el ancho disponible
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalles.add(scroll);

        JLabel lblContador = new JLabel("0 / 220");
        lblContador.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblContador.setForeground(Color.LIGHT_GRAY);
        lblContador.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalles.add(lblContador);

        panelDetalles.add(Box.createVerticalStrut(30));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalles.add(sep);
        panelDetalles.add(Box.createVerticalStrut(20));

        // COMBO BOX ESTILIZADO
        JLabel lblForma = new JLabel("Formato visual:");
        lblForma.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblForma.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalles.add(lblForma);
        panelDetalles.add(Box.createVerticalStrut(10));

        String[] formas = {"Cuadrada", "Vertical", "Horizontal"};
        JComboBox<String> comboForma = new JComboBox<>(formas);
        comboForma.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comboForma.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboForma.setBackground(Color.WHITE);
        comboForma.setFont(new Font("SansSerif", Font.PLAIN, 14));
        // Quitar el borde feo por defecto
        comboForma.setBorder(BorderFactory.createLineBorder(new Color(219, 219, 219)));
        panelDetalles.add(comboForma);

        // --- LÓGICA DE EVENTOS ---
        final String[] rutaImg = {""};
        btnSeleccionar.addActionListener(e -> {
            String r = abrirExploradorArchivos();
            if (r != null) {
                rutaImg[0] = r;
                btnSeleccionar.setText("Imagen lista ✅");
            }
        });

        areaTexto.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                if (areaTexto.getText().length() >= 220) {
                    e.consume();
                }
                if (areaTexto.getText().endsWith("#")) {

                }
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (!areaTexto.getText().equals(placeholder)) {
                    lblContador.setText(areaTexto.getText().length() + " / 220");
                }
            }
        });

        btnCompartir.addActionListener(e -> {
            String contenido = areaTexto.getText().equals(placeholder) ? "" : areaTexto.getText();
            String forma = (String) comboForma.getSelectedItem();
            if (rutaImg[0].isEmpty()) {
                JLabel aviso = new JLabel("Debe seleccionar una foto");
                aviso.setForeground(Color.red);
                panelDetalles.add(aviso);
                panelDetalles.add(Box.createVerticalStrut(20));
            } else {
                logica.addPublicacionUser(contenido, forma, rutaImg[0]);
            }
            JOptionPane.showMessageDialog(this, "¡Publicado!");//borrar
            panelFeed();
        });

        // ENSAMBLAJE
        cuerpo.add(panelMedia);
        cuerpo.add(panelDetalles);
        mainCrear.add(header, BorderLayout.NORTH);
        mainCrear.add(cuerpo, BorderLayout.CENTER);

        panelBlanco.add(sidebarPanel, BorderLayout.WEST);
        panelBlanco.add(mainCrear, BorderLayout.CENTER);

        add(contenedorBase);
        revalidate();
        repaint();
    }

    public void panelNotificaciones() {
        getContentPane().removeAll();
        JPanel contenedor = crearContenedorConFondo();
        JPanel panel = (JPanel) contenedor.getComponent(0);
        panel.setLayout(new BorderLayout());

        construirSidebar();

        // --- ÁREA CENTRAL ---
        JPanel areaNotif = new JPanel();
        areaNotif.setLayout(new BoxLayout(areaNotif, BoxLayout.Y_AXIS));
        areaNotif.setBackground(Color.WHITE);
        areaNotif.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel lblSeccionSoli = new JLabel("Solicitudes de amistad");
        lblSeccionSoli.setFont(new Font("SansSerif", Font.BOLD, 16));
        areaNotif.add(lblSeccionSoli);
        areaNotif.add(Box.createVerticalStrut(10));

        JLabel titulo = new JLabel("Notificaciones");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        areaNotif.add(titulo);
        areaNotif.add(Box.createVerticalStrut(20));

        // --- CARGAR NOTIFICACIONES ---
        // Aquí podrías tener una lista de objetos "Notificacion" en tu lógica
        // Por ahora, simularemos los dos tipos que pediste:
        // Ejemplo 1: Mensaje Nuevo
        areaNotif.add(crearTarjetaNotificacion("Mensaje", "emy_ca te envió un mensaje", "💬"));
        areaNotif.add(Box.createVerticalStrut(10));

        // Ejemplo 2: Solicitud de Amistad
        areaNotif.add(crearTarjetaSolicitud("usuario", "foto"));
        areaNotif.add(Box.createVerticalStrut(10));

        // Scroll
        JScrollPane scroll = new JScrollPane(areaNotif);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(15);

        panel.add(sidebarPanel, BorderLayout.WEST);
        panel.add(scroll, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel crearTarjetaNotificacion(String tipo, String texto, String icono) {
        JPanel tarjeta = new JPanel(new BorderLayout(15, 0));
        tarjeta.setBackground(new Color(250, 250, 250));
        tarjeta.setMaximumSize(new Dimension(800, 70));
        tarjeta.setPreferredSize(new Dimension(800, 70));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Icono visual
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        tarjeta.add(lblIcono, BorderLayout.WEST);

        // Texto descriptivo
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tarjeta.add(lblTexto, BorderLayout.CENTER);

        // Eventos de Mouse (Simulando el botón)
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                panelMensajes();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                tarjeta.setBackground(new Color(240, 240, 240));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                tarjeta.setBackground(new Color(250, 250, 250));
            }
        });

        return tarjeta;
    }

    private JPanel crearTarjetaSolicitud(String nombreUsuario, String fotoRuta) {
        JPanel tarjeta = new JPanel(new BorderLayout(15, 0));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setMaximumSize(new Dimension(850, 80));
        tarjeta.setPreferredSize(new Dimension(850, 80));
        tarjeta.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // --- LADO IZQUIERDO: Foto y Texto ---
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        infoPanel.setOpaque(false);

        // Foto de perfil circular (simulada con un JLabel)
        JLabel lblFoto = new JLabel();
        lblFoto.setPreferredSize(new Dimension(50, 50));
        try {
            ImageIcon icon = new ImageIcon(fotoRuta);
            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            lblFoto.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblFoto.setOpaque(true);
            lblFoto.setBackground(Color.LIGHT_GRAY);
        }

        JLabel lblTexto = new JLabel("<html><b>" + nombreUsuario + "</b> quiere seguirte.</html>");
        lblTexto.setFont(new Font("SansSerif", Font.PLAIN, 14));

        infoPanel.add(lblFoto);
        infoPanel.add(lblTexto);

        // --- LADO DERECHO: Botones de Acción ---
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        botonesPanel.setOpaque(false);

        JButton btnConfirmar = new JButton("Confirmar");
        btnConfirmar.setBackground(new Color(0, 149, 246)); // Azul Instagram
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnConfirmar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(239, 239, 239)); // Gris claro
        btnEliminar.setForeground(Color.BLACK);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnEliminar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // --- EVENTOS ---
        btnConfirmar.addActionListener(e -> {
            // Aquí llamas a tu lógica para aceptar: logica.aceptarSolicitud(nombreUsuario);
            JOptionPane.showMessageDialog(this, "Ahora sigues a " + nombreUsuario);
            tarjeta.setVisible(false); // Desaparece al procesar
        });

        btnEliminar.addActionListener(e -> {
            // Aquí llamas a tu lógica para borrar: logica.eliminarSolicitud(nombreUsuario);
            tarjeta.setVisible(false);
        });

        botonesPanel.add(btnConfirmar);
        botonesPanel.add(btnEliminar);

        tarjeta.add(infoPanel, BorderLayout.WEST);
        tarjeta.add(botonesPanel, BorderLayout.EAST);

        return tarjeta;
    }

    private void imprimirSeguidoresConsola() {
        System.out.println("=== VERIFICACIÓN DE PERFIL VISUALIZADO ===");
        System.out.println("Usuario: " + logica.getUsuarioUser(1));
        // Asumiendo que logica tiene métodos para obtener las listas de nombres
        System.out.println("Seguidores: " + logica.getListaNombresFollowers(1));
        System.out.println("Siguiendo: " + logica.getListaNombresFollowing(1));
        System.out.println("==========================================");
    }
}
