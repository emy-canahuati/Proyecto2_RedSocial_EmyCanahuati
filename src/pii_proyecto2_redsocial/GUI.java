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

    private ImageIcon fotoPerfil;
    private Component sidebar;

    public GUI() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

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
            String pass = new String(txtPass.getPassword()); // Corrección de Password
            if (logica.Login(txtUsuario.getText(), pass)) {
                panelFeed();
            } else {
                if (lblErrorLogin == null) {
                    lblErrorLogin = new JLabel("La información es incorrecta.");
                    lblErrorLogin.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    lblErrorLogin.setForeground(Color.RED);
                    lblErrorLogin.setBounds(50, 380, 350, 20);
                    panelContenido.add(lblErrorLogin);
                }
                lblErrorLogin.setText("Nombre de usuario o contraseña incorrectos.");
                panelContenido.repaint();
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
                this.fotoPerfil = new ImageIcon(rutaImagen);
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
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(280, screenSize.height));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        sidebar.add(Box.createVerticalStrut(30));

        String[] menu = {"🏠 Inicio", "🔍 Buscar", "✉️ Mensajes", "❤️ Notificaciones", "➕ Crear", "👤 Perfil", "≡ Configuración"};

        for (String opcion : menu) {
            JButton btnMenu = new JButton(opcion);
            btnMenu.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btnMenu.setForeground(Color.BLACK);
            btnMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnMenu.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 10));
            btnMenu.setContentAreaFilled(false);
            btnMenu.setBorderPainted(false);
            btnMenu.setFocusPainted(false);
            btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnMenu.addActionListener(e -> {
                if (opcion.contains("Inicio")) {
                    panelFeed();
                }
                if (opcion.contains("Perfil")) {
                    panelPerfil();
                }
                if (opcion.contains("Mensajes")) {
                    panelMensajes();
                }
                if (opcion.contains("Crear")) {
                }
                if (opcion.contains("Buscar")) {
                    panelBuscar();
                }
                if (opcion.contains("Configuración")) {
                    panelConfiguracion();
                }
                if (opcion.contains("Notificaciones")) {
                }
            });

            sidebar.add(btnMenu);
        }

        // --- ÁREA CENTRAL (Feed) ---
        JPanel feedArea = new JPanel();
        feedArea.setLayout(new BoxLayout(feedArea, BoxLayout.Y_AXIS));
        feedArea.setBackground(new Color(250, 250, 250));

        // Agregamos varios posts para que el scroll sea necesario
        for (int i = 0; i < 8; i++) {
            // Usamos tu método de ejemplo para crear el diseño del post
            JPanel post = crearPublicacionEjemplo(600, 600);

            // Eventos del mouse para interactividad
            post.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    JOptionPane.showMessageDialog(null, "Abriendo publicación detallada...");
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    post.setBorder(BorderFactory.createLineBorder(new Color(0, 149, 246), 2));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    post.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                }
            });

            // Contenedor para centrar el post horizontalmente
            JPanel centralizer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
            centralizer.setOpaque(false);
            centralizer.add(post);
            feedArea.add(centralizer);
        }

        // --- CONFIGURACIÓN DEL SCROLL ---
        JScrollPane scroll = new JScrollPane(feedArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(25); // Movimiento suave
        scroll.setBorder(null);

        // Unimos todo al contenedor principal
        panel.add(sidebar, BorderLayout.WEST);
        panel.add(scroll, BorderLayout.CENTER);

        add(contenedor);
        revalidate();
        repaint();
    }

    
    private JPanel crearPublicacionEjemplo(int ancho, int alto) {
        JPanel post = new JPanel(new BorderLayout());
        post.setBackground(Color.WHITE);
        post.setMaximumSize(new Dimension(ancho, alto + 100)); // +100 para header/footer
        post.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel userHeader = new JLabel("usuario");
        userHeader.setPreferredSize(new Dimension(ancho, 50));
        userHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        post.add(userHeader, BorderLayout.NORTH);

        // Imagen de Publicación
        JPanel imagenPost = new JPanel();
        imagenPost.setBackground(Color.DARK_GRAY);
        imagenPost.setPreferredSize(new Dimension(ancho, alto));
        post.add(imagenPost, BorderLayout.CENTER);

        // Footer (Likes/Comentarios)
        JLabel footer = new JLabel("  Ver los 146 comentarios...");
        footer.setPreferredSize(new Dimension(ancho, 50));
        post.add(footer, BorderLayout.SOUTH);

        return post;
    }

    
    public void panelPerfil() {
        getContentPane().removeAll();
        JPanel contenedor = crearContenedorConFondo();
        JPanel panel = (JPanel) contenedor.getComponent(0);
        panel.setLayout(new BorderLayout());

        // --- BARRA LATERAL (Reutilizamos la del feed) ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(280, screenSize.height));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        String[] menu = {"🏠 Inicio", "🔍 Buscar", "✉️ Mensajes", "❤️ Notificaciones", "➕ Crear", "👤 Perfil", "≡ Configuración"};
        sidebar.add(Box.createVerticalStrut(30));
        for (String opcion : menu) {
            JButton btnMenu= new JButton(opcion);
            btnMenu.setFont(new Font("SansSerif", opcion.contains("Perfil") ? Font.BOLD : Font.PLAIN, 18));
            btnMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnMenu.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 10));
            btnMenu.setContentAreaFilled(false);
            btnMenu.setBorderPainted(false);
            btnMenu.setFocusPainted(false);
            btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            btnMenu.addActionListener(e -> {
                if (opcion.contains("Inicio")) {
                    panelFeed();
                }
                if (opcion.contains("Mensajes")) {
                    panelMensajes();
                }
                if (opcion.contains("Crear")) {
                }
                if (opcion.contains("Buscar")) {
                    panelBuscar();
                }
                if (opcion.contains("Configuración")) {
                    panelConfiguracion();
                }
                if (opcion.contains("Notificaciones")) {
                }
            });
            
            sidebar.add(btnMenu);
          
        }

        // --- ÁREA DE CONTENIDO (Perfil) ---
        JPanel profileContent = new JPanel();
        profileContent.setLayout(new BoxLayout(profileContent, BoxLayout.Y_AXIS));
        profileContent.setBackground(Color.WHITE);

        // 1. ENCABEZADO DE PERFIL (Foto y Datos)
        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(935, 300));
        header.setMaximumSize(new Dimension(935, 300));
        header.setOpaque(false);

        JPanel fotoCircular = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                fotoPerfil = logica.getUsuarioFoto(0);
                Shape circulo = new java.awt.geom.Ellipse2D.Float(0, 0, getWidth(), getHeight());
                g2.setClip(circulo);
                if (fotoPerfil != null) {
                    g2.drawImage(fotoPerfil.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.fill(circulo);
                }
            }
        };
        fotoCircular.setBounds(100, 40, 150, 150);
        header.add(fotoCircular);

        // Nombre de Usuario y Botones
        JLabel lblUser = new JLabel(logica.getUsuario(0));
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lblUser.setBounds(300, 45, 200, 30);
        header.add(lblUser);
        
        JLabel lblNombre = new JLabel(logica.getUsuarioNombre(0));
        lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblNombre.setBounds(300, 65, 200, 30);
        header.add(lblNombre);

        JButton btnConfig = new JButton("Editar Perfil");
        btnConfig.setBounds(480, 47, 120, 30);
        btnConfig.addActionListener(e -> panelConfiguracion());
        header.add(btnConfig);

        int followers, following;
        followers = logica.getUsuarioFollowers(0);
        following = logica.getUsuarioFollowing(0);

        JLabel lblStats = new JLabel("<html><b>0</b> publicaciones &nbsp;&nbsp; <b>" + followers + "</b> seguidores &nbsp;&nbsp; <b>" + following + "</b> seguidos</html>");
        lblStats.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblStats.setBounds(300, 95, 500, 30);
        header.add(lblStats);

        JLabel lblBio = new JLabel("<html><br>Descripción:</html>");
        lblBio.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblBio.setBounds(300, 135, 400, 60);
        header.add(lblBio);
        
        JTextArea txtBio = new JTextArea(logica.getUsuarioBio(0));
        txtBio.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtBio.setBounds(300, 185, 400, 60);
        txtBio.setEditable(false);
        header.add(txtBio);

        profileContent.add(header);

        profileContent.add(new JSeparator());

        JPanel gridContainer = new JPanel(new GridLayout(0, 3, 20, 20)); // 3 columnas
        gridContainer.setBackground(Color.WHITE);
        gridContainer.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        // Generar cuadros de posts (cuadrados)
        for (int i = 0; i < 9; i++) {
            JPanel postThumb = new JPanel();
            postThumb.setPreferredSize(new Dimension(290, 290));
            postThumb.setBackground(Color.BLACK); // Simula la imagen
            postThumb.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            gridContainer.add(postThumb);
        }

        profileContent.add(gridContainer);

        // Scroll
        JScrollPane scroll = new JScrollPane(profileContent);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);

        panel.add(sidebar, BorderLayout.WEST);
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

        // --- BARRA LATERAL (Reutilizamos la del feed) ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(280, screenSize.height));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        String[] menu = {"🏠 Inicio", "🔍 Buscar", "✉️ Mensajes", "❤️ Notificaciones", "➕ Crear", "👤 Perfil", "≡ Configuración"};
        sidebar.add(Box.createVerticalStrut(30));
        for (String opcion : menu) {
            JButton btnMenu= new JButton(opcion);
            btnMenu.setFont(new Font("SansSerif",Font.PLAIN, 18));
            btnMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnMenu.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 10));
            btnMenu.setContentAreaFilled(false);
            btnMenu.setBorderPainted(false);
            btnMenu.setFocusPainted(false);
            btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            btnMenu.addActionListener(e -> {
                if (opcion.contains("Inicio")) {
                    panelFeed();
                }
                if (opcion.contains("Mensajes")) {
                    panelMensajes();
                }
                if (opcion.contains("Perfil")){
                    panelPerfil();
                }
                if (opcion.contains("Crear")) {
                }
                if (opcion.contains("Buscar")) {
                    panelBuscar();
                }
                if (opcion.contains("Configuración")) {
                    panelConfiguracion();
                }
                if (opcion.contains("Notificaciones")) {
                }
            });

            sidebar.add(btnMenu);

        }

        // --- ÁREA DE CONTENIDO (Perfil) ---
        JPanel profileContent = new JPanel();
        profileContent.setLayout(new BoxLayout(profileContent, BoxLayout.Y_AXIS));
        profileContent.setBackground(Color.WHITE);

        // 1. ENCABEZADO DE PERFIL (Foto y Datos)
        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(935, 300));
        header.setMaximumSize(new Dimension(935, 300));
        header.setOpaque(false);

        JPanel fotoCircular = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                fotoPerfil = logica.getUsuarioFoto(1);
                Shape circulo = new java.awt.geom.Ellipse2D.Float(0, 0, getWidth(), getHeight());
                g2.setClip(circulo);
                if (fotoPerfil != null) {
                    g2.drawImage(fotoPerfil.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.fill(circulo);
                }
            }
        };
        fotoCircular.setBounds(100, 40, 150, 150);
        header.add(fotoCircular);

        // Nombre de Usuario y Botones
        JLabel lblUser = new JLabel(logica.getUsuario(1));
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lblUser.setBounds(300, 45, 200, 30);
        header.add(lblUser);

        JLabel lblNombre = new JLabel(logica.getUsuarioNombre(1));
        lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblNombre.setBounds(300, 65, 200, 30);
        header.add(lblNombre);

        JButton btnConfig = new JButton("Seguir");
        btnConfig.setBounds(480, 47, 120, 30);
        btnConfig.addActionListener(e -> panelConfiguracion());
        header.add(btnConfig);

        int followers, following;
        followers = logica.getUsuarioFollowers(1);
        following = logica.getUsuarioFollowing(1);

        JLabel lblStats = new JLabel("<html><b>0</b> publicaciones &nbsp;&nbsp; <b>" + followers + "</b> seguidores &nbsp;&nbsp; <b>" + following + "</b> seguidos</html>");
        lblStats.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblStats.setBounds(300, 95, 500, 30);
        header.add(lblStats);

        JLabel lblBio = new JLabel("<html><br>Descripción:</html>");
        lblBio.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblBio.setBounds(300, 135, 400, 60);
        header.add(lblBio);

        JTextArea txtBio = new JTextArea(logica.getUsuarioBio(1));
        txtBio.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtBio.setBounds(300, 185, 400, 60);
        txtBio.setEditable(false);
        header.add(txtBio);

        profileContent.add(header);

        profileContent.add(new JSeparator());

        JPanel gridContainer = null;
        if (logica.getUsuarioTipo(1).equals(TipoCuenta.PUBLICA)) {
            generarCuadricula(gridContainer);
        } else {
            gridContainer.setLayout(new FlowLayout());
            JLabel fotoPrivado = new JLabel();
            fotoPrivado.setSize(30, 30);
            ImageIcon imagen = new ImageIcon("scr/Imagenes/fotoPrivado.png");
            Image imgSized = imagen.getImage().getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
            fotoPrivado.setIcon(new ImageIcon(imgSized));
            fotoPrivado.setAlignmentX(CENTER_ALIGNMENT);
            gridContainer.add(fotoPrivado);
        }

        profileContent.add(gridContainer);

        // Scroll
        JScrollPane scroll = new JScrollPane(profileContent);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);

        panel.add(sidebar, BorderLayout.WEST);
        panel.add(scroll, BorderLayout.CENTER);

        add(contenedor);
        revalidate();
        repaint();
    }

    private void panelMensajes() {
    }

    private void generarCuadricula(JPanel gridContainer) {
        gridContainer = new JPanel(new GridLayout(0, 3, 20, 20)); // 3 columnas
        gridContainer.setBackground(Color.WHITE);
        gridContainer.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        // Generar cuadros de posts (cuadrados)
        for (int i = 0; i < 9; i++) {
            JPanel postThumb = new JPanel();
            postThumb.setPreferredSize(new Dimension(290, 290));
            postThumb.setBackground(Color.BLACK); // Simula la imagen
            postThumb.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            gridContainer.add(postThumb);
        }
    }

    private void panelConfiguracion() {
        getContentPane().removeAll();

        JPanel contenedorBase = crearContenedorConFondo();
        JPanel panelBlanco = (JPanel) contenedorBase.getComponent(0);
        panelBlanco.setLayout(new BorderLayout());

        Font fuente19 = new Font("SansSerif", Font.BOLD, 19);
        Font fuentePlain19 = new Font("SansSerif", Font.PLAIN, 19);
        int espacioEntreSecciones = 20;
        Dimension dimCampo = new Dimension(450, 40);

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(280, 786));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        String[] menu = {"🏠 Inicio", "🔍 Buscar", "✉️ Mensajes", "❤️ Notificaciones", "➕ Crear", "👤 Perfil", "≡ Configuración"};
        sidebar.add(Box.createVerticalStrut(30));
        for (String opcion : menu) {
            JButton btnMenu = new JButton(opcion);
            btnMenu.setFont((opcion.contains("Configuración")) ? new Font("SansSerif", Font.BOLD, 18) : new Font("SansSerif", Font.PLAIN, 18));
            btnMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnMenu.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 10));
            btnMenu.setContentAreaFilled(false);
            btnMenu.setBorderPainted(false);
            btnMenu.setFocusPainted(false);
            btnMenu.setOpaque(false);
            btnMenu.setBackground(Color.WHITE);
            btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnMenu.addActionListener(e -> {
                if (opcion.contains("Inicio")) {
                    panelFeed();
                }
                if (opcion.contains("Mensajes")) {
                    panelMensajes();
                }
                if (opcion.contains("Crear")) {
                }
                if (opcion.contains("Buscar")) {
                    panelBuscar();
                }
                if (opcion.contains("Notificaciones")) {
                }
                if (opcion.contains("Perfil")) {
                    panelPerfil();
                }
            });
            sidebar.add(btnMenu);
        }

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
        JTextField txtUser = crearCampoEstilizado(logica.getUsuario(0));
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
        JTextField txtEdad = crearCampoEstilizado(logica.getUsuarioLoggedEdad());
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
        cbGen.setSelectedItem(logica.getUsuarioLoggedGenero());
        cbGen.setBackground(Color.WHITE);
        cbGen.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        cbGen.setForeground(Color.BLACK);
        cbGen.setFont(fuente19);
        cbGen.setSelectedItem(logica.getUsuarioLoggedGenero());
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
        JPasswordField txtPass = new JPasswordField(logica.getUsuarioLoggedContra());
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
                this.fotoPerfil = new ImageIcon(ruta);
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
            ArrayList<Integer> opciones = logica.modificarDatos(txtNombre.getText(), txtUser.getText(), new String(txtPass.getPassword()),
                    String.valueOf(cbGen.getSelectedItem()), txtEdad.getText(), fotoPerfil, String.valueOf(cbTipo.getSelectedItem()).toUpperCase(), txtBio.getText());
            if (opciones.contains(8)) {
                aplicarErrorCampo(txtUser, "Este usuario ya existe");
            }
            if (opciones.contains(9)) {
                aplicarErrorCampo(txtEdad, "Edad no válida");
            }
        });
        contenido.add(btnGuardar);
        contenido.add(Box.createVerticalStrut(15));

        JButton btnSwitch = new JButton("🔄 Cambiar de Cuenta");
        estilizarBotonAccion(btnSwitch, dimCampo, new Color(70, 70, 70));
        btnSwitch.addActionListener(e -> panelRegistrar());
        contenido.add(btnSwitch);
        contenido.add(Box.createVerticalStrut(10));

        JButton btnLogout = new JButton("🚪 Cerrar Sesión");
        estilizarBotonAccion(btnLogout, dimCampo, Color.DARK_GRAY);
        btnLogout.addActionListener(e -> panelRegistrar());
        contenido.add(btnLogout);
        contenido.add(Box.createVerticalStrut(10));

        JButton btnEliminar = new JButton("❌ Desactivar Cuenta");
        estilizarBotonAccion(btnEliminar, dimCampo, new Color(200, 0, 0));
        btnEliminar.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "¿Seguro?", "Alerta", 0) == 0) {
                panelRegistrar();
            }
        });
        contenido.add(btnEliminar);
        contenido.add(Box.createVerticalGlue());

        // --- ENSAMBLAJE ---
        JScrollPane scrollPrincipal = new JScrollPane(contenido);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(16); // Scroll más suave

        panelBlanco.add(sidebar, BorderLayout.WEST);
        panelBlanco.add(scrollPrincipal, BorderLayout.CENTER);

        add(contenedorBase);
        revalidate();
        repaint();
    }
// Método auxiliar para no repetir código de botones

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

    // --- SIDEBAR (Reutilizado) ---
    // ... (El código del sidebar que ya tienes se mantiene igual)

    // --- CONTENIDO PRINCIPAL ---
    JPanel contenido = new JPanel();
    contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
    contenido.setBackground(Color.WHITE);
    contenido.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

    // Título
    JLabel lblTitulo = new JLabel("Buscar");
    lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
    lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
    contenido.add(lblTitulo);
    contenido.add(Box.createVerticalStrut(20));

    // Barra de Búsqueda
    JPanel panelBusqueda = new JPanel(new BorderLayout(10, 0));
    panelBusqueda.setBackground(Color.WHITE);
    panelBusqueda.setMaximumSize(new Dimension(800, 45));
    panelBusqueda.setAlignmentX(Component.LEFT_ALIGNMENT);

    JTextField txtBuscar = new JTextField(" Buscar usuarios...");
    txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 16));
    txtBuscar.setForeground(Color.GRAY);
    txtBuscar.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(219, 219, 219), 1, true),
        BorderFactory.createEmptyBorder(5, 10, 5, 10)
    ));

    // Panel donde aparecerán los resultados
    JPanel panelResultados = new JPanel();
    panelResultados.setLayout(new BoxLayout(panelResultados, BoxLayout.Y_AXIS));
    panelResultados.setBackground(Color.WHITE);

    txtBuscar.addActionListener(e -> {
        String query = txtBuscar.getText().trim();
        panelResultados.removeAll();
        
        // Simulación de búsqueda (Aquí conectarías con tu lógica.buscarUsuarios(query))
        // Ejemplo de uso del método de tarjetas:
        // for(Usuario u : resultados) { 
        //    panelResultados.add(crearTarjetaUsuario(u));
        //    panelResultados.add(Box.createVerticalStrut(10));
        // }
        
        panelResultados.revalidate();
        panelResultados.repaint();
    });

    panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
    contenido.add(panelBusqueda);
    contenido.add(Box.createVerticalStrut(30));

    // Scroll para los resultados
    JScrollPane scrollResultados = new JScrollPane(panelResultados);
    scrollResultados.setBorder(null);
    scrollResultados.getVerticalScrollBar().setUnitIncrement(16);
    scrollResultados.getViewport().setBackground(Color.WHITE);
    contenido.add(scrollResultados);

    panelBlanco.add(sidebar, BorderLayout.WEST);
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
}
