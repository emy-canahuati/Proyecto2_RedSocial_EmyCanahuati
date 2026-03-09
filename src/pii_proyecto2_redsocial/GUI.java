/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pii_proyecto2_redsocial;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author emyca
 */
public class GUI extends JFrame{
    private JPanel panelPrincipal;
    private JPanel panelContenido; 
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private Logica logica = new Logica();
    private JLabel lblErrorLogin = null;

    
    private ImageIcon fotoPerfil;

    
    public GUI(){
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
                lblVal.setText("✓ Contraseña segura");
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
                btnFoto.setText("Foto Seleccionada ✓");
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
        if (opciones.contains(4)) {
            aplicarErrorCampo(txtEdad, errorVacio);
        }
        if (opciones.contains(5)) {
            aplicarErrorCampo(txtEdad, "Debe ingresar una edad valida");
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
    JPanel mainContainer = new JPanel(new BorderLayout());

    // --- BARRA LATERAL (Izquierda) ---
    JPanel sidebar = new JPanel();
    sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
    sidebar.setBackground(Color.WHITE);
    sidebar.setPreferredSize(new Dimension(280, screenSize.height));
    sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

    // Logo y Opciones
    sidebar.add(Box.createVerticalStrut(30));
    String[] menu = {"🏠 Inicio", "🔍 Buscar", "🧭 Explorar", "🎬 Reels", "✉️ Mensajes", "❤️ Notificaciones", "➕ Crear"};
    for (String opt : menu) {
        JLabel lbl = new JLabel(opt);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lbl.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 10));
        sidebar.add(lbl);
    }

    // --- ÁREA CENTRAL (Feed) ---
    JPanel feedArea = new JPanel();
    feedArea.setLayout(new BoxLayout(feedArea, BoxLayout.Y_AXIS));
    feedArea.setBackground(new Color(250, 250, 250));

    // Contenedor de Post centrado
    JPanel centralizer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
    centralizer.setOpaque(false);
    
    // Ejemplo de Post (600x600 px)
    JPanel post = new JPanel(new BorderLayout());
    post.setPreferredSize(new Dimension(600, 750)); // Alto extra para info
    post.setBackground(Color.WHITE);
    post.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

    // Header del Post
    JLabel userLabel = new JLabel("  usuario_instagram");
    userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
    userLabel.setPreferredSize(new Dimension(600, 50));
    post.add(userLabel, BorderLayout.NORTH);

    // Espacio para la imagen
    JPanel imagePlaceholder = new JPanel();
    imagePlaceholder.setBackground(Color.BLACK); // Simula la foto cargada
    post.add(imagePlaceholder, BorderLayout.CENTER);

    centralizer.add(post);
    feedArea.add(centralizer);

    JScrollPane scroll = new JScrollPane(feedArea);
    scroll.getVerticalScrollBar().setUnitIncrement(20);
    scroll.setBorder(null);

    mainContainer.add(sidebar, BorderLayout.WEST);
    mainContainer.add(scroll, BorderLayout.CENTER);

    add(mainContainer);
    revalidate();
    repaint();
}

    /**
     * Crea un panel que simula una publicación con las dimensiones solicitadas
     */
    private JPanel crearPublicacionEjemplo(int ancho, int alto) {
        JPanel post = new JPanel(new BorderLayout());
        post.setBackground(Color.WHITE);
        post.setMaximumSize(new Dimension(ancho, alto + 100)); // +100 para header/footer
        post.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Header (Usuario)
        JLabel userHeader = new JLabel("  usuario_ejemplo");
        userHeader.setPreferredSize(new Dimension(ancho, 50));
        userHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        post.add(userHeader, BorderLayout.NORTH);

        // Imagen de Publicación
        JPanel imagenPost = new JPanel();
        imagenPost.setBackground(Color.DARK_GRAY);
        imagenPost.setPreferredSize(new Dimension(ancho, alto));
        // Aquí iría el g.drawImage de la publicación real
        post.add(imagenPost, BorderLayout.CENTER);

        // Footer (Likes/Comentarios)
        JLabel footer = new JLabel("  Ver los 146 comentarios...");
        footer.setPreferredSize(new Dimension(ancho, 50));
        post.add(footer, BorderLayout.SOUTH);

        return post;
    }

    
}
