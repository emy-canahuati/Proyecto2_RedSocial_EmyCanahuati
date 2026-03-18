/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pii_proyecto2_redsocial;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

/**
 *
 * @author emyca
 */
public class Logica {

    private ArrayList<Usuario> usuarios;
    private ArrayList<Publicacion> publicaciones;
    private ArrayList<Mensaje> mensajes;
    private int usuarioLogged;
    private int usuarioSelec;
    private File carpetaRaiz;
    private File carpetaStickers;
    private static final String pathSesiones = "src/INSTA_RAIZ/sesiones_activas.ins";

    private Socket socket;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
    private ChatListener chatListener;
    private String chatActivoActual = "";
    private final Object lockChats = new Object();

    public Logica() {
        try {
            carpetaRaiz = new File("src/INSTA_RAIZ/");
            carpetaRaiz.mkdir();

            carpetaStickers = new File("src/INSTA_RAIZ/stickers_globales");
            carpetaStickers.mkdir();

            usuarios = new ArrayList<>();
            mensajes = new ArrayList<>();

            File archivoUsuarios = new File("src/INSTA_RAIZ/users.ins");

            leerUsuarios(archivoUsuarios);
            cargarPublicacionesUsuarios();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

public void crearCuentasDefault() {
    String[] usersDefault = {"luna_estrella", "carlos_dev", "maria_foto"};
    for (String u : usersDefault) {
        for (Usuario existente : usuarios) {
            if (existente.getUser().equals(u)) return;
        }
    }

    try {
        Date fecha = Calendar.getInstance().getTime();
        ImageIcon img1= new ImageIcon("src/Imagenes/marvel.jpg");
        ImageIcon img2= new ImageIcon("src/Imagenes/jorgue.jpg");
        ImageIcon img3= new ImageIcon("src/Imagenes/game.png");
        
        UsuarioPublico u1 = new UsuarioPublico(
            "marvel", "Marvel Entertainment", "Hola123!",
            "MASCULINO", "ACTIVO", "PUBLICA", fecha, img1, 24
        );
        usuarios.add(u1);
        crearCarpetasUser("marvel");

        UsuarioPublico u2 = new UsuarioPublico(
            "jorgue_her", "Jorge Rivera-Herrans", "Legendary123!",
            "MASCULINO", "ACTIVO", "PUBLICA", fecha, img2, 28
        );
        usuarios.add(u2);
        crearCarpetasUser("jorgue_her");

        UsuarioPublico u3 = new UsuarioPublico(
            "game_theory", "The Game Theorists", "IamBatman123!",
            "FEMENINO", "ACTIVO", "PUBLICA", fecha, img3, 22
        );
        usuarios.add(u3);
        crearCarpetasUser("game_theory");

        guardarUsuarios();

    } catch (IOException e) {
        System.out.println("Error creando cuentas default: " + e.getMessage());
    }
}

public void crearPublicacionesDefault() {
    HashMap<String, String[][]> publicacionesPorUser = new HashMap<>();

    publicacionesPorUser.put("marvel", new String[][] {
        {"Protegiendo el universo, un planeta a la vez.\n #GuardiansOfTheGalaxy #Marvel #RocketRaccoon #Groot", "Cuadrada","src/Imagenes/img1.jpeg"},
        {"Siempre listo.\nCaptainAmerica #Avengers #MarvelStudios #OnYourLeft", "Cuadrada","src/Imagenes/img2.jpeg"},
        {"El rey de Wakanda, siempre en nuestros corazones.#WakandaForever #BlackPanther #Marvel #Legacy","Vertical",  "src/Imagenes/img3.jpeg"}
    });

    publicacionesPorUser.put("jorgue_her", new String[][] {
        {"Nuevo proyecto terminado #programacion","Cuadrada",  "src/Imagenes/img4.jpg"},
        {"El café es vida #developer #codigo", "Cuadrada",  "src/Imagenes/img5.jpg"},
        {"Capturando momentos 📸 #fotografia","Cuadrad","src/Imagenes/img6.jpg"}
    });

    publicacionesPorUser.put("game_theory", new String[][] {
        {"¿Pesaba Mario 100 kilos en SM64? La física revela la verdad. #GameTheory #Mario #Nintendo","Vertical",  "src/Imagenes/img7.jpeg"},
        {"FNAF: ¿Y si miramos toda la línea de tiempo al revés? #FNAF #GameTheory #Lore", "Horizontal",  "src/Imagenes/img8.jpg"},
        {"Sobrevivirías un apocalipsis zombie en Minecraft? La ciencia responde. #GameTheory #Minecraft", "Cuadrada",  "src/Imagenes/img9.jpg"}
    });

    int loggedBackup = usuarioLogged;

    for (Map.Entry<String, String[][]> entry : publicacionesPorUser.entrySet()) {
        String username = entry.getKey();
        String[][] publis = entry.getValue();

        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getUser().equals(username)) {
                usuarioLogged = i;
                break;
            }
        }

        for (String[] publiData : publis) {
            String contenido  = publiData[0];
            String forma      = publiData[1];
            String rutaImagen = publiData[2];

            try { Thread.sleep(10); } catch (InterruptedException ignored) {}

            try {
                String rutaFinal = rutaImagen;
                File imgFile = new File(rutaImagen);
                if (imgFile.exists()) {
                    String nombreImg = "post_" + System.currentTimeMillis();
                    rutaFinal = guardarImagenUsuario(rutaImagen, nombreImg);
                }

                Publicacion publi = new Publicacion(username, contenido, rutaFinal, forma);
                publicaciones.add(publi);
                addPublicacionFileUser(username, publi);
                System.out.println("Publicación creada para " + username);
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    usuarioLogged = loggedBackup;
    System.out.println("publicaciones default creadas.");
}

    private void leerUsuarios(File archivoUsuarios) throws IOException, ClassNotFoundException {
        if (archivoUsuarios.exists() && archivoUsuarios.length() > 0) {

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivoUsuarios));

            usuarios = (ArrayList<Usuario>) in.readObject();

            in.close();
        }
    }

    private void guardarUsuarios() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("src/INSTA_RAIZ/users.ins"));
            out.writeObject(usuarios);
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String guardarImagenUsuario(String rutaOriginal, String nombreArchivo) {
        if (rutaOriginal == null || rutaOriginal.isEmpty()) {
            return null;
        }
        try {
            File origen = new File(rutaOriginal);
            if (!origen.exists()) {
                return rutaOriginal;
            }

            String extension = rutaOriginal.substring(rutaOriginal.lastIndexOf('.'));
            extension = extension.toLowerCase(); // .JPG → .jpg

            String nombreLimpio = nombreArchivo.replaceAll("[^a-zA-Z0-9_\\-]", "_");

            File destino = new File(getPath(getUsuarioUser(0))
                    + "/folders_personales/imagenes/" + nombreLimpio + extension);

            destino.getParentFile().mkdirs();

            java.nio.file.Files.copy(
                    origen.toPath(),
                    destino.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            return destino.getPath();
        } catch (IOException e) {
            System.out.println("Error copiando imagen: " + e.getMessage());
            return rutaOriginal; 
        }
    }

    private HashMap<String, ArrayList<Mensaje>> leerMapaChats(String pathArchivo) {
        synchronized (lockChats) { 
            File archivo = new File(pathArchivo);
            if (archivo.exists() && archivo.length() > 0) {
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
                    return (HashMap<String, ArrayList<Mensaje>>) in.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error leyendo chats: " + e.getMessage());
                }
            }
            return new HashMap<>();
        }
    }

    private void guardarMapaChats(String pathArchivo, HashMap<String, ArrayList<Mensaje>> mapa) {
        synchronized (lockChats) { // ✅
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathArchivo))) {
                out.writeObject(mapa);
            } catch (IOException e) {
                System.out.println("Error guardando chats: " + e.getMessage());
            }
        }
    }

    private void leerChats(Usuario user) {
        String path = getPath(getUsuarioUser(0)) + "/inbox.ins";
        HashMap<String, ArrayList<Mensaje>> mapa = leerMapaChats(path);
        this.mensajes = mapa.getOrDefault(user.getUser(), new ArrayList<>());
    }

    private void guardarChats(Usuario user) {
        synchronized (lockChats) { 
            String pathLogged = getPath(getUsuarioUser(0)) + "/inbox.ins";
            String pathOther = getPath(user.getUser()) + "/inbox.ins";

            HashMap<String, ArrayList<Mensaje>> mapaLogged = leerMapaChats(pathLogged);
            mapaLogged.put(user.getUser(), this.mensajes);
            guardarMapaChats(pathLogged, mapaLogged);

            HashMap<String, ArrayList<Mensaje>> mapaOther = leerMapaChats(pathOther);
            mapaOther.put(getUsuarioUser(0), this.mensajes);
            guardarMapaChats(pathOther, mapaOther);
        }
    }

    public void setChatListener(ChatListener listener) {
        this.chatListener = listener;
    }

    public void setChatActivo(String username) {
        this.chatActivoActual = username;
    }

    public ArrayList<Mensaje> getMensajes() {
        return mensajes;
    }

    public void abrirChat(String userDestino) {
        setUsuarioSelec(userDestino);
        leerChats(getUsuario(1));
    }

    public void conectarSocket() throws IOException {
        socket = new Socket("localhost", 5000);
        socketOut = new ObjectOutputStream(socket.getOutputStream());
        socketIn = new ObjectInputStream(socket.getInputStream());

        socketOut.writeObject(getUsuarioUser(0));
        socketOut.flush();

        new Thread(this::escucharMensajes).start();
    }

    public ArrayList<String> getUsuariosConChat() {
        String path = getPath(getUsuarioUser(0)) + "/inbox.ins";
        HashMap<String, ArrayList<Mensaje>> mapa = leerMapaChats(path);
        return new ArrayList<>(mapa.keySet());
    }

    public ArrayList<String> getContactosDisponibles() {
        ArrayList<String> contactos = new ArrayList<>();
        String miUser = getUsuarioUser(0);

        File archivoFollowing = new File(getPath(miUser) + "/following.ins");
        if (archivoFollowing.exists()) {
            try (RandomAccessFile following = getFileFollowing(miUser)) {
                following.seek(0);
                while (following.getFilePointer() < following.length()) {
                    String nombre = following.readUTF();
                    int estado = following.readInt();
                    if (estado == 1 && !contactos.contains(nombre)) {
                        contactos.add(nombre);
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        File archivoFollowers = new File(getPath(miUser) + "/followers.ins");
        if (archivoFollowers.exists()) {
            try (RandomAccessFile followers = getFileFollowers(miUser)) {
                followers.seek(0);
                while (followers.getFilePointer() < followers.length()) {
                    String nombre = followers.readUTF();
                    int estado = followers.readInt();
                    if (estado == 1 && !contactos.contains(nombre)) {
                        Usuario u = getAutorPublicacion(nombre);
                        if (u == null) {
                            for (Usuario usr : usuarios) {
                                if (usr.getUser().equals(nombre)) {
                                    u = usr;
                                    break;
                                }
                            }
                        }
                        if (u != null && isPublica(u)) {
                            contactos.add(nombre);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        return contactos;
    }

    public void enviarMensaje(int indicador, String contenido) throws IOException {
        Mensaje mensaje;
        if (indicador == 0) {
            mensaje = new MensajeTexto(getUsuario(0), getUsuario(1), contenido);
        } else {
            mensaje = new MensajeSticker(getUsuario(0), getUsuario(1), contenido);
        }
        mensajes.add(mensaje);
        guardarChats(getUsuario(1));

        socketOut.writeObject(mensaje);
        socketOut.flush();
    }

private void escucharMensajes() {
    try {
        while (true) {
            Mensaje mensaje = (Mensaje) socketIn.readObject();
            
            if (mensaje instanceof MensajeTexto 
                    && "@@LEIDO@@".equals(mensaje.getContenido())) {
                if (mensajes != null) {
                    for (Mensaje m : mensajes) {
                        if (m.esMio(getUsuarioUser(0)) && !m.isLeido()) {
                            m.marcarLeido();
                        }
                    }
                }
                if (chatListener != null) {
                    SwingUtilities.invokeLater(() -> chatListener.onMensajeRecibido(mensaje));
                }
                continue;
            }
            
            if (mensajes == null) mensajes = new ArrayList<>();
            mensajes.add(mensaje);
            
            if (chatListener != null) {
                SwingUtilities.invokeLater(() -> chatListener.onMensajeRecibido(mensaje));
            }
            
            new Thread(() -> {
                guardarChats(mensaje.getEmisor());
                if (!mensaje.getEmisor().getUser().equals(chatActivoActual)) {
                    agregarNotificacion(getUsuarioUser(0), new Notificacion(
                        Notificacion.Tipo.MENSAJE,
                        mensaje.getEmisor().getUser(),
                        mensaje.getEmisor().getUser() + " te envió un mensaje."
                    ));
                }
            }).start();
        }
    } catch (SocketException | EOFException e) {
        if (chatListener != null)
            SwingUtilities.invokeLater(() -> chatListener.onDesconectado());
    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
    }
}
public void notificarMensajesLeidos(String paraQuien) throws IOException {
    if (socketOut == null) return;
    MensajeTexto ack = new MensajeTexto(getUsuario(0), getUsuario(1), "@@LEIDO@@");
    socketOut.writeObject(ack);
    socketOut.flush();
}

    public void marcarMensajesLeidosEnMemoria(String deQuien) {
        if (mensajes == null) {
            return;
        }
        for (Mensaje m : mensajes) {
            if (m.getEmisor().getUser().equals(deQuien) && !m.isLeido()) {
                m.marcarLeido();
            }
        }
    }

    public void marcarMensajesLeidos(String deQuien) {
        marcarMensajesLeidosEnMemoria(deQuien);
        if (usuarioSelec >= 0 && usuarioSelec < usuarios.size()) {
            guardarChats(getUsuario(1));
        }
    }

    public void eliminarConversacion(String userDestino) {
        String pathLogged = getPath(getUsuarioUser(0)) + "/inbox.ins";
        HashMap<String, ArrayList<Mensaje>> mapaLogged = leerMapaChats(pathLogged);
        mapaLogged.remove(userDestino);
        guardarMapaChats(pathLogged, mapaLogged);

        this.mensajes = new ArrayList<>();
    }

    private void cargarPublicacionesUsuarios() {
        publicaciones = new ArrayList<>();
        for (Usuario user : usuarios) {
            try {
                File archivo = new File(getPath(user.getUser()) + "/insta.ins");
                if (archivo.exists() && archivo.length() > 0) {
                    ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo));
                    while (true) {
                        try {
                            Publicacion p = (Publicacion) in.readObject();
                            publicaciones.add(p);
                        } catch (EOFException e) {
                            break;
                        }
                    }
                    in.close();
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void recargarPublicaciones() {
        cargarPublicacionesUsuarios();
    }

    private String getPath(String user) {
        return "src/INSTA_RAIZ/" + user.toUpperCase();
    }

    private void crearCarpetasUser(String user) throws IOException {
        File carpetaUser = new File(getPath(user));
        carpetaUser.mkdir();

        File carpetaFolders = new File(getPath(user) + "/folders_personales");
        carpetaFolders.mkdir();

        File carpetaImg = new File(getPath(user) + "/folders_personales/imagenes");
        carpetaImg.mkdir();

        File carpetaStickers = new File(getPath(user) + "/stickers_personales");
        carpetaStickers.mkdir();

        new File(getPath(user) + "/followers.ins").createNewFile();
        new File(getPath(user) + "/following.ins").createNewFile();
        new File(getPath(user) + "/inbox.ins").createNewFile();
        new File(getPath(user) + "/folders_personales/notificaciones.ins").createNewFile();
    }

    private RandomAccessFile getFileFollowers(String user) throws IOException {
        File archivo = new File(getPath(user) + "/followers.ins");
        archivo.getParentFile().mkdirs();
        if (!archivo.exists()) {
            archivo.createNewFile();
        }
        return new RandomAccessFile(archivo, "rw");
    }

    private RandomAccessFile getFileFollowing(String user) throws IOException {
        File archivo = new File(getPath(user) + "/following.ins");
        archivo.getParentFile().mkdirs();
        if (!archivo.exists()) {
            archivo.createNewFile();
        }
        return new RandomAccessFile(archivo, "rw");
    }

    private String getPathStickersGlobales() {
        return "src/INSTA_RAIZ/stickers_globales/";
    }

    private String getPathStickersPersonales(String user) {
        return getPath(user) + "/stickers_personales/";
    }

    public ArrayList<String> getStickersGlobales() {
        String[] nombres = {
            "stickerFeliz.png", "stickerTriste.png", "stickerCorazon.png",
            "stickerRisa.png", "stickerAplauso.png"
        };
        ArrayList<String> rutas = new ArrayList<>();
        for (String nombre : nombres) {
            File f = new File(getPathStickersGlobales() + nombre);
            if (f.exists()) {
                rutas.add(f.getPath());
            }
        }
        return rutas;
    }

    public ArrayList<String> getStickersPersonales() {
        ArrayList<String> rutas = new ArrayList<>();
        File archivo = new File(getPath(getUsuarioUser(0)) + "/stickers.ins");
        if (!archivo.exists() || archivo.length() == 0) {
            return rutas;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            rutas = (ArrayList<String>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error leyendo stickers: " + e.getMessage());
        }
        return rutas;
    }

    public String guardarStickerPersonal(String rutaOriginal) {
        try {
            File origen = new File(rutaOriginal);
            if (!origen.exists()) {
                return null;
            }

            String extension = rutaOriginal.substring(rutaOriginal.lastIndexOf('.'));
            String nombre = "sticker_" + System.currentTimeMillis() + extension;
            File destino = new File(getPathStickersPersonales(getUsuarioUser(0)) + nombre);

            java.nio.file.Files.copy(origen.toPath(), destino.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            ArrayList<String> lista = getStickersPersonales();
            lista.add(destino.getPath());

            try (ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(getPath(getUsuarioUser(0)) + "/stickers.ins"))) {
                out.writeObject(lista);
            }

            return destino.getPath();
        } catch (IOException e) {
            System.out.println("Error guardando sticker: " + e.getMessage());
            return null;
        }
    }

    public boolean estaActivoEnOtraVentana(String username) {
        File archivo = new File(pathSesiones);
        if (!archivo.exists() || archivo.length() == 0) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().equalsIgnoreCase(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo sesiones: " + e.getMessage());
        }
        return false;
    }

    private void registrarSesion(String username) {
        try {
            File archivo = new File(pathSesiones);
            ArrayList<String> sesiones = leerSesiones();
            if (!sesiones.contains(username.toLowerCase())) {
                sesiones.add(username.toLowerCase());
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, false))) {
                for (String s : sesiones) {
                    writer.write(s);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error registrando sesión: " + e.getMessage());
        }
    }

    public void cerrarSesion() {
        try {
            String username = getUsuarioUser(0).toLowerCase();
            ArrayList<String> sesiones = leerSesiones();
            sesiones.remove(username);

            File archivo = new File(pathSesiones);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, false))) {
                for (String s : sesiones) {
                    writer.write(s);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            System.out.println("Error cerrando sesión: " + e.getMessage());
        }
    }

    public void desactivarYCerrarSesion() {
        try {
            usuarios.get(usuarioLogged).setEstado("INACTIVO");
            guardarUsuarios();

            String username = getUsuarioUser(0).toLowerCase();
            ArrayList<String> sesiones = leerSesiones();
            sesiones.remove(username);

            File archivo = new File(pathSesiones);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, false))) {
                for (String s : sesiones) {
                    writer.write(s);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private ArrayList<String> leerSesiones() {
        ArrayList<String> sesiones = new ArrayList<>();
        File archivo = new File(pathSesiones);
        if (!archivo.exists() || archivo.length() == 0) {
            return sesiones;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.isBlank()) {
                    sesiones.add(linea.trim().toLowerCase());
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo sesiones: " + e.getMessage());
        }
        return sesiones;
    }

    public int LoginConEstado(String nombre_login, String contraseña_login) {
        for (Usuario user : usuarios) {
            if (user != null && user.getUser().equals(nombre_login)
                    && user.getContra().equals(contraseña_login)) {
                if (estaActivoEnOtraVentana(nombre_login)) {
                    return 0;
                }

                usuarioLogged = usuarios.indexOf(user);

                if (user.getEstado() == Usuario.EstadoCuenta.INACTIVO) {
                    return 2;
                }

                user.setEstado("ACTIVO");
                registrarSesion(nombre_login);
                return 1;
            }
        }
        return 0;
    }

    public void reactivarCuenta() {
        usuarios.get(usuarioLogged).setEstado("ACTIVO");
        registrarSesion(getUsuarioUser(0));
        guardarUsuarios();
    }

    public boolean Login(String nombre_login, String contraseña_login) {
        return LoginConEstado(nombre_login, contraseña_login) == 1;
    }

    private ArrayList<Integer> verificarDatos(String nombre, String user, String contra, String edad) {
        ArrayList<Integer> errores = new ArrayList<>();

        if (nombre.isBlank()) {
            errores.add(2);
        }
        if (user.isBlank()) {
            errores.add(3);
        }
        if (edad.isBlank()) {
            errores.add(4);
        } else {
            try {
                if (Integer.parseInt(edad) < 1) {
                    errores.add(5);
                }
            } catch (NumberFormatException e) {
                errores.add(5);
            }
        }
        if (contra.isBlank()) {
            errores.add(6);
        }

        if (usuarios != null) {
            for (Usuario usuar : usuarios) {
                if (usuar != null) {
                    if (user.equals(usuar.getUser())) {
                        errores.add(7);
                        break;
                    }
                }
            }
        }

        boolean largoOk = contra.length() >= 8;
        boolean mayusOk = !contra.equals(contra.toLowerCase()) && !contra.equals("");
        boolean symbolOk = contra.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        if (!largoOk || !mayusOk || !symbolOk) {
            errores.add(8);
        }
        return errores;
    }

    public ArrayList<Integer> setDatos(String nombre, String user, String contra, String genero, String edad, ImageIcon fotoPerfil, String tipoCuenta) {
        ArrayList<Integer> errores = verificarDatos(nombre, user, contra, edad);

        String generoFinal = genero.equals("M") ? "MASCULINO" : "FEMENINO";
        if (errores.isEmpty()) {
            try {
                Date fecha = Calendar.getInstance().getTime();
                Usuario nuevoUser = null;
                if (tipoCuenta.equals("PUBLICA")) {
                    nuevoUser = new UsuarioPublico(user, nombre, contra, generoFinal, "ACTIVO", tipoCuenta, fecha, fotoPerfil, Integer.parseInt(edad));
                } else {
                    nuevoUser = new UsuarioPrivado(user, nombre, contra, generoFinal, "ACTIVO", tipoCuenta, fecha, fotoPerfil, Integer.parseInt(edad));
                }
                usuarios.add(nuevoUser);
                crearCarpetasUser(user);

                if (fotoPerfil != null) {
                    String rutaOriginal = fotoPerfil.getDescription();
                    if (rutaOriginal != null && !rutaOriginal.isEmpty()) {
                        File origen = new File(rutaOriginal);
                        if (origen.exists()) {
                            String extension = rutaOriginal.substring(rutaOriginal.lastIndexOf('.')).toLowerCase();
                            File destino = new File(getPath(user) + "/folders_personales/imagenes/perfil" + extension);
                            destino.getParentFile().mkdirs();
                            java.nio.file.Files.copy(origen.toPath(), destino.toPath(),
                                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            nuevoUser.setFotoPerfil(new ImageIcon(destino.getPath(), destino.getPath()));
                        }
                    }
                }

                guardarUsuarios();
                errores.add(1);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return errores;
    }

    public ArrayList<Integer> modificarDatos(String nombre, String user, String contra, String genero, String edad, ImageIcon fotoPerfil, String tipoCuenta, String bio) {
        ArrayList<Integer> modificaciones = verificarNuevosDatos(nombre, user, contra, edad, bio, genero, tipoCuenta);

        String generoFinal = genero.equals("M") ? "MASCULINO" : "FEMENINO";
        if (modificaciones.contains(1)) {
            usuarios.get(usuarioLogged).setNombre(nombre);
        }

        if (modificaciones.contains(2)) {
            File carpetaAntigua = new File(getPath(usuarios.get(usuarioLogged).getUser()));
            File carpetaNueva = new File(getPath(user));
            carpetaAntigua.renameTo(carpetaNueva);

            usuarios.get(usuarioLogged).setUser(user);
        }

        if (modificaciones.contains(3)) {
            usuarios.get(usuarioLogged).setContra(contra);
        }

        if (modificaciones.contains(4)) {
            usuarios.get(usuarioLogged).setEdad(Integer.parseInt(edad));
        }

        if (modificaciones.contains(5)) {
            usuarios.get(usuarioLogged).setBio(bio);
        }

        if (modificaciones.contains(6)) {
            usuarios.get(usuarioLogged).setGenero(generoFinal);
        }

        if (fotoPerfil != null) {
            String rutaOriginal = fotoPerfil.getDescription();
            if (rutaOriginal != null && !rutaOriginal.isEmpty()) {
                String nuevaRuta = guardarImagenUsuario(rutaOriginal, "perfil");
                usuarios.get(usuarioLogged).setFotoPerfil(new ImageIcon(nuevaRuta, nuevaRuta));
            } else {
                usuarios.get(usuarioLogged).setFotoPerfil(fotoPerfil);
            }
        }

        if (modificaciones.contains(7)) {
            usuarios.get(usuarioLogged).setTipoCuenta(tipoCuenta);
            ImageIcon fotoActual = usuarios.get(usuarioLogged).getFotoPerfil();
            Usuario userModificado = null;
            if (tipoCuenta.equals("PUBLICA")) {
                userModificado = new UsuarioPublico(usuarios.get(usuarioLogged).getUser(), usuarios.get(usuarioLogged).getNombre(), usuarios.get(usuarioLogged).getContra(), usuarios.get(usuarioLogged).getGenero().name(), usuarios.get(usuarioLogged).getEstado().name(), usuarios.get(usuarioLogged).getTipoCuenta().name(), usuarios.get(usuarioLogged).getFecha(), fotoActual, usuarios.get(usuarioLogged).getEdad());
            } else {
                userModificado = new UsuarioPrivado(usuarios.get(usuarioLogged).getUser(), usuarios.get(usuarioLogged).getNombre(), usuarios.get(usuarioLogged).getContra(), usuarios.get(usuarioLogged).getGenero().name(), usuarios.get(usuarioLogged).getEstado().name(), usuarios.get(usuarioLogged).getTipoCuenta().name(), usuarios.get(usuarioLogged).getFecha(), fotoActual, usuarios.get(usuarioLogged).getEdad());
            }
            usuarios.set(usuarioLogged, userModificado);
        }

        guardarUsuarios();
        return modificaciones;
    }

    private ArrayList<Integer> verificarNuevosDatos(String nombre, String user, String contra, String edad, String bio, String genero, String tipoCuenta) {
        ArrayList<Integer> modificacion = new ArrayList<>();

        if (!nombre.equals(usuarios.get(usuarioLogged).getNombre()) && !nombre.isBlank()) {
            modificacion.add(1);
        }
        if (!user.equals(usuarios.get(usuarioLogged).getUser()) && !user.isBlank()) {
            if (usuarios != null) {
                for (Usuario usuar : usuarios) {
                    if (usuar != null) {
                        if (user.equals(usuar.getUser())) {
                            modificacion.add(8);
                            break;
                        }
                    }
                }
                modificacion.add(2);
            }
        }
        if (!contra.equals(usuarios.get(usuarioLogged).getContra()) && !contra.isBlank()) {
            boolean largoOk = contra.length() >= 8;
            boolean mayusOk = !contra.equals(contra.toLowerCase()) && !contra.equals("");
            boolean symbolOk = contra.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

            if (largoOk && mayusOk && symbolOk) {
                modificacion.add(3);
            }
        }
        if (!edad.equals(String.valueOf(usuarios.get(usuarioLogged).getEdad())) && !edad.isBlank()) {
            try {
                if (Integer.parseInt(edad) > 0) {
                    modificacion.add(4);
                }
            } catch (NumberFormatException e) {
                modificacion.add(9); 
            }
        }

        if (!bio.equals(usuarios.get(usuarioLogged).getBio()) && !bio.isBlank()) {
            modificacion.add(5);
        }

        if (!genero.equals(usuarios.get(usuarioLogged).getGenero().name())) {
            modificacion.add(6);
        }

        if (!tipoCuenta.equals(usuarios.get(usuarioLogged).getTipoCuenta().name())) {
            modificacion.add(7);
        }
        return modificacion;
    }

    public Usuario getUsuario(int indicador) {
        return usuarios.get((indicador == 0) ? usuarioLogged : usuarioSelec);
    }

    public String getUsuarioUser(int indicador) {
        return getUsuario(indicador).getUser();
    }

    public String getUsuarioNombre(int indicador) {
        return getUsuario(indicador).getNombre();
    }

    public String getUsuarioEdad(int indicador) {
        return String.valueOf(getUsuario(indicador).getEdad());
    }

    public String getUsuarioBio(int indicador) {
        return getUsuario(indicador).getBio();
    }

    public String getUsuarioFecha(int indicador) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy");
        return formato.format(getUsuario(indicador).getFecha());
    }

    public String getUsuarioGenero(int indicador) {
        if (getUsuario(indicador).getGenero().name().equals("MASCULINO")) {
            return "M";
        }
        return "F";
    }

    public String getUsuarioContra(int indicador) {
        return getUsuario(indicador).getContra();
    }

    public String getUsuarioTipo(int indicador) {
        if (getUsuario(indicador).getTipoCuenta().name().equals("PRIVADA")) {
            return "Privada";
        }
        return "Publica";
    }

    public int getUsuarioFollowers(int indicador) {
        File archivo = new File(getPath(getUsuarioUser(indicador)) + "/followers.ins");
        if (!archivo.exists()) {
            return 0;
        }

        try (RandomAccessFile followers = getFileFollowers(getUsuarioUser(indicador))) {
            followers.seek(0);
            int contador = 0;
            while (followers.getFilePointer() < followers.length()) {
                followers.readUTF();
                if (followers.readInt() == 1) {
                    contador++;
                }
            }
            return contador;
        } catch (IOException e) {
            System.out.println("Error al leer followers: " + e.getMessage());
            return 0;
        }
    }

    public int getUsuarioFollowing(int indicador) {
        File archivo = new File(getPath(getUsuarioUser(indicador)) + "/following.ins");
        if (!archivo.exists()) {
            return 0;
        }

        try (RandomAccessFile following = getFileFollowing(getUsuarioUser(indicador))) {
            following.seek(0);
            int contador = 0;
            while (following.getFilePointer() < following.length()) {
                following.readUTF();
                if (following.readInt() == 1) {
                    contador++;
                }
            }
            return contador;
        } catch (IOException e) {
            System.out.println("Error al leer following: " + e.getMessage());
            return 0;
        }
    }

    public ImageIcon getUsuarioFoto(int indicador) {
        return usuarios.get((indicador == 0) ? usuarioLogged : usuarioSelec).getFotoPerfil();
    }

    public void getUsuarioSelec(String user) {
        if (!usuarios.isEmpty()) {
            for (int indexUsuario = 0; indexUsuario < usuarios.size(); indexUsuario++) {
                if (usuarios.get(indexUsuario).getUser().equals(user)) {
                    usuarioSelec = indexUsuario;
                }
            }
        }
    }

    public ArrayList buscar(int indicador, String buscado) throws IOException {
        ArrayList matches = new ArrayList<>();
        String miUser = getUsuarioUser(0);

        switch (indicador) {
            case 1:
                for (Usuario user : usuarios) {
                    String username = user.getUser().toLowerCase();
                    String nombreReal = user.getNombre().toLowerCase();
                    String query = buscado.toLowerCase();

                    if ((username.contains(query) || nombreReal.contains(query)) && !user.getUser().equals(miUser) && user.getEstado().name().equals("ACTIVO")) {
                        matches.add(user);
                    }
                }
                break;

            case 2: 
                buscado = buscado.replace("#", "");
                for (Publicacion publi : publicaciones) {
                    if (!publi.getAutor().equals(miUser)) {
                        if (publi.getHashtags().contains(buscado)) {
                            Usuario autor = getAutorPublicacion(publi.getAutor());
                            if (isPublica(autor) || isUsuarioLoggedFollower(autor) == 1) {
                                if (autor.getEstado().name().equals("ACTIVO")) {
                                    matches.add(publi);
                                }
                            }
                        }
                    }
                }
                break;

            case 3: 
                buscado = buscado.replace("@", "");
                for (Publicacion publi : publicaciones) {
                    if (!publi.getAutor().equals(miUser)) {
                        if (publi.getMenciones().contains(buscado)) {
                            Usuario autor = getAutorPublicacion(publi.getAutor());
                            if (isPublica(autor) || isUsuarioLoggedFollower(autor) == 1) {
                                if (autor.getEstado().name().equals("ACTIVO")) {
                                    matches.add(publi);
                                }
                            }
                        }
                    }
                }
                break;
        }
        return matches;
    }

    public void setUsuarioSelec(String user) {
        for (int indexUsuario = 0; indexUsuario < usuarios.size(); indexUsuario++) {
            if (usuarios.get(indexUsuario).getUser().equals(user)) {
                usuarioSelec = indexUsuario;
                return;
            }
        }
    }

    public void addPublicacionUser(String contenido, String forma, String rutaImagen) {
        try {
            String rutaFinal = rutaImagen;
            if (rutaImagen != null && !rutaImagen.isEmpty()) {
                String nombreImg = "post_" + System.currentTimeMillis();
                rutaFinal = guardarImagenUsuario(rutaImagen, nombreImg);
            }

            Publicacion publi = new Publicacion(getUsuarioUser(0), contenido, rutaFinal, forma);

            publicaciones.add(publi);
            addPublicacionFileUser(getUsuarioUser(0), publi);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private void addPublicacionFileUser(String user, Publicacion publi) throws IOException {
        File archivo = new File(getPath(user) + "/insta.ins");
        boolean append = archivo.exists() && archivo.length() > 0;

        ObjectOutputStream out;
        if (append) {
            out = new ObjectOutputStream(new FileOutputStream(archivo, true)) {
                protected void writeStreamHeader() throws IOException {
                    reset();
                }
            };
        } else {
            out = new ObjectOutputStream(new FileOutputStream(archivo));
        }
        out.writeObject(publi);
        out.close();
    }

    public Usuario getAutorPublicacion(String autor) {
        for (Usuario users : usuarios) {
            if (users.getUser().equals(autor)) {
                return users;
            }
        }
        return null;
    }

    public boolean isPublica(Usuario user) {
        return (user.getTipoCuenta().name().equals("PUBLICA"));
    }

    public int isFollower(Usuario user) throws IOException {
        try (RandomAccessFile followers = getFileFollowers(getUsuarioUser(0))) {
            followers.seek(0);
            while (followers.getFilePointer() < followers.length()) {
                String nombre = followers.readUTF();
                int estado = followers.readInt();
                if (nombre.equals(user.getUser())) {
                    return estado;
                }
            }
            return 4;
        }
    }

public int isFollowing(String user) throws IOException {
    File archivo = new File(getPath(getUsuarioUser(0)) + "/following.ins");
    if (!archivo.exists() || archivo.length() == 0) return 4;

    try (RandomAccessFile following = getFileFollowing(getUsuarioUser(0))) {
        following.seek(0);
        while (following.getFilePointer() < following.length()) {
            try {
                String nombre = following.readUTF();
                int estado = following.readInt();
                if (nombre != null && nombre.equals(user)) return estado;
            } catch (EOFException e) { break; }
        }
        return 4;
    }
}

public int isUsuarioLoggedFollower(Usuario user) throws IOException {
    if (user == null) return 4;
    File archivo = new File(getPath(user.getUser()) + "/followers.ins");
    if (!archivo.exists() || archivo.length() == 0) return 4;

    try (RandomAccessFile followers = getFileFollowers(user.getUser())) {
        followers.seek(0);
        while (followers.getFilePointer() < followers.length()) {
            try {
                String nombre = followers.readUTF();
                int estado = followers.readInt();
                if (nombre != null && nombre.equals(getUsuarioUser(0))) return estado;
            } catch (EOFException e) { break; }
        }
        return 4;
    }
}

    public void addFollowing(Usuario user) throws IOException {
        int indicador = isFollowing(user.getUser());

        if (indicador == 1 || indicador == 2) {
            removeFollowing(user);
            removeFollower();
            return;
        }

        if (indicador == 3) {
            try (RandomAccessFile following = getFileFollowing(getUsuarioUser(0))) {
                following.seek(0);
                while (following.getFilePointer() < following.length()) {
                    following.readUTF();
                    long posEstado = following.getFilePointer();
                    int estado = following.readInt();
                    if (estado == 3) {
                        following.seek(posEstado);
                        following.writeInt(isPublica(user) ? 1 : 2);
                        break;
                    }
                }
            }

            int indicador2 = isUsuarioLoggedFollower(user);
            try (RandomAccessFile followers = getFileFollowers(user.getUser())) {
                if (indicador2 == 3) {
                    followers.seek(0);
                    while (followers.getFilePointer() < followers.length()) {
                        followers.readUTF();
                        long posEstado = followers.getFilePointer();
                        int estado = followers.readInt();
                        if (estado == 3) {
                            followers.seek(posEstado);
                            followers.writeInt(isPublica(user) ? 1 : 2);
                            break;
                        }
                    }
                } else if (indicador2 == 4) {
                    followers.seek(followers.length());
                    followers.writeUTF(getUsuarioUser(0));
                    followers.writeInt(isPublica(user) ? 1 : 2);
                }
            }
        } else {
            try (RandomAccessFile following = getFileFollowing(getUsuarioUser(0))) {
                following.seek(following.length());
                following.writeUTF(user.getUser());
                following.writeInt(isPublica(user) ? 1 : 2);
            }

            int indicador2 = isUsuarioLoggedFollower(user);
            try (RandomAccessFile followers = getFileFollowers(user.getUser())) {
                if (indicador2 == 4) {
                    followers.seek(followers.length());
                    followers.writeUTF(getUsuarioUser(0));
                } else {
                    followers.seek(0);
                    while (followers.getFilePointer() < followers.length()) {
                        followers.readUTF();
                        long posEstado = followers.getFilePointer();
                        followers.readInt();
                        followers.seek(posEstado);
                        followers.writeInt(isPublica(user) ? 1 : 2);
                        break;
                    }
                    return;
                }
                followers.writeInt(isPublica(user) ? 1 : 2);
            }
        }

        agregarNotificacion(user.getUser(), new Notificacion(
                isPublica(user) ? Notificacion.Tipo.SEGUIDOR : Notificacion.Tipo.SOLICITUD,
                getUsuarioUser(0),
                isPublica(user)
                ? getUsuarioUser(0) + " empezó a seguirte."
                : getUsuarioUser(0) + " quiere seguirte."
        ));
    }

    public void addFollower(Usuario user) throws IOException {
        int indicador = isFollower(user);
        if (indicador == 2) {
            RandomAccessFile followers = getFileFollowers(getUsuarioUser(0));
            followers.writeInt(1);
        }
    }

public void removeFollowing(Usuario user) throws IOException {
    if (user == null) return;
    try (RandomAccessFile following = getFileFollowing(getUsuarioUser(0))) {
        following.seek(0);
        while (following.getFilePointer() < following.length()) {
            try {
                String nombre = following.readUTF();
                long posEstado = following.getFilePointer();
                int estado = following.readInt();
                if (nombre.equals(user.getUser()) && (estado == 1 || estado == 2)) {
                    following.seek(posEstado);
                    following.writeInt(3);
                    break;
                }
            } catch (EOFException e) { break; }
        }
    }
}

public void removeFollower() throws IOException {
    Usuario selec = getUsuario(1);
    if (selec == null) return;
    try (RandomAccessFile followers = getFileFollowers(getUsuarioUser(1))) {
        followers.seek(0);
        while (followers.getFilePointer() < followers.length()) {
            try {
                String nombre = followers.readUTF();
                long posEstado = followers.getFilePointer();
                int estado = followers.readInt();
                if (nombre.equals(getUsuarioUser(0)) && (estado == 1 || estado == 2)) {
                    followers.seek(posEstado);
                    followers.writeInt(3);
                    break;
                }
            } catch (EOFException e) { break; }
        }
    }
}

    public String getListaNombresFollowers(int indicador) {
        Usuario user = getUsuario(indicador);
        File archivo = new File(getPath(user.getUser()) + "/followers.ins");
        if (!archivo.exists() || archivo.length() == 0) {
            return "Nadie";
        }

        ArrayList<String> seguidores = new ArrayList<>();
        try (RandomAccessFile followers = getFileFollowers(user.getUser())) {
            followers.seek(0);
            while (followers.getFilePointer() < followers.length()) {
                String nombre = followers.readUTF();
                int estado = followers.readInt();
                if (estado == 1) 
                {
                    seguidores.add(nombre);
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo followers: " + e.getMessage());
        }

        return seguidores.isEmpty() ? "Nadie" : String.join(", ", seguidores);
    }

    public String getListaNombresFollowing(int indicador) {
        Usuario user = getUsuario(indicador);

        File archivo = new File(getPath(user.getUser()) + "/following.ins");
        if (!archivo.exists() || archivo.length() == 0) {
            return "Nadie";
        }

        ArrayList<String> seguidos = new ArrayList<>();
        try (RandomAccessFile following = getFileFollowing(user.getUser())) {
            following.seek(0);
            while (following.getFilePointer() < following.length()) {
                String nombre = following.readUTF();
                int estado = following.readInt();
                if (estado == 1) 
                {
                    seguidos.add(nombre);
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo following: " + e.getMessage());
        }

        return seguidos.isEmpty() ? "Nadie" : String.join(", ", seguidos);
    }

    public ArrayList<Publicacion> getFeed() throws IOException {
        ArrayList<Publicacion> publiFeed = new ArrayList<>();

        for (int i = publicaciones.size() - 1; i >= 0; i--) {
            Publicacion publi = publicaciones.get(i);
            Usuario autor = getAutorPublicacion(publi.getAutor());

            if (autor == null || autor.getEstado() == Usuario.EstadoCuenta.INACTIVO) {
                continue;
            }

            if (publi.getAutor().equals(getUsuarioUser(0))) {
                publiFeed.add(publi);
                continue;
            }

            if (isPublica(autor) || isUsuarioLoggedFollower(autor) == 1) {
                publiFeed.add(publi);
            }
        }

        publiFeed.sort((a, b) -> b.getFechaHora().compareTo(a.getFechaHora()));

        return publiFeed;
    }

    public void actualizarPublicacion(Publicacion pActualizada) {
        for (int i = 0; i < publicaciones.size(); i++) {
            Publicacion p = publicaciones.get(i);
            if (p.getAutor().equals(pActualizada.getAutor())
                    && p.getFechaHora().equals(pActualizada.getFechaHora())) {
                publicaciones.set(i, pActualizada);
                reescribirArchivoUsuario(pActualizada.getAutor());
                return; 
            }
        }

    }

    private void reescribirArchivoUsuario(String username) {
        File archivo = new File(getPath(username) + "/insta.ins");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(archivo))) {
            for (Publicacion p : publicaciones) {
                if (p.getAutor().equals(username)) {
                    out.writeObject(p);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al actualizar archivo: " + e.getMessage());
        }
    }

    private ArrayList<Publicacion> getPubliPerfilDesordenadas(Usuario user) {
        ArrayList<Publicacion> publiUsuario = new ArrayList<>();
        for (Publicacion publis : publicaciones) {
            if (publis.getAutor().equals(user.getUser())) {
                publiUsuario.add(publis);
            }
        }
        return publiUsuario;
    }

    public ArrayList<Publicacion> getPubliPerfil(Usuario user) {
        ArrayList<Publicacion> lista = getPubliPerfilDesordenadas(user);
        if (lista.isEmpty()) {
            return lista;
        }

        for (int i = 0; i < lista.size() - 1; i++) {
            for (int j = 0; j < lista.size() - 1 - i; j++) {
                if (lista.get(j).getFechaHora().before(lista.get(j + 1).getFechaHora())) {
                    Publicacion tmp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, tmp);
                }
            }
        }
        return lista;
    }

    private String getPathNotificaciones(String user) {
        return getPath(user) + "/folders_personales/notificaciones.ins";
    }

    private void guardarNotificaciones(String user, ArrayList<Notificacion> lista) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(getPathNotificaciones(user)))) {
            out.writeObject(lista);
        } catch (IOException e) {
            System.out.println("Error guardando notificaciones: " + e.getMessage());
        }
    }

    public ArrayList<Notificacion> leerNotificaciones(String user) {
        File archivo = new File(getPathNotificaciones(user));
        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (ArrayList<Notificacion>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public void agregarNotificacion(String userDestino, Notificacion notif) {
        ArrayList<Notificacion> lista = leerNotificaciones(userDestino);
        lista.add(notif);
        guardarNotificaciones(userDestino, lista);
    }

    public void eliminarNotificacion(String user, int index) {
        ArrayList<Notificacion> lista = leerNotificaciones(user);
        if (index >= 0 && index < lista.size()) {
            lista.remove(index);
            guardarNotificaciones(user, lista);
        }
    }

    public void marcarNotificacionesVistas() {
        ArrayList<Notificacion> lista = leerNotificaciones(getUsuarioUser(0));
        for (Notificacion n : lista) {
            n.marcarVista();
        }
        guardarNotificaciones(getUsuarioUser(0), lista);
    }

    public boolean hayNotificacionesSinVer() {
        for (Notificacion n : leerNotificaciones(getUsuarioUser(0))) {
            if (!n.isVista()) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getSolicitudesPendientes() {
        ArrayList<String> pendientes = new ArrayList<>();
        File archivo = new File(getPath(getUsuarioUser(0)) + "/followers.ins");
        if (!archivo.exists()) {
            return pendientes;
        }
        try (RandomAccessFile followers = getFileFollowers(getUsuarioUser(0))) {
            followers.seek(0);
            while (followers.getFilePointer() < followers.length()) {
                String nombre = followers.readUTF();
                int estado = followers.readInt();
                if (estado == 2) {
                    pendientes.add(nombre);
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo solicitudes: " + e.getMessage());
        }
        return pendientes;
    }

    public void confirmarSolicitud(String solicitante) throws IOException {
        try (RandomAccessFile followers = getFileFollowers(getUsuarioUser(0))) {
            followers.seek(0);
            while (followers.getFilePointer() < followers.length()) {
                String nombre = followers.readUTF();
                long posEstado = followers.getFilePointer();
                int estado = followers.readInt();
                if (nombre.equals(solicitante) && estado == 2) {
                    followers.seek(posEstado);
                    followers.writeInt(1); 
                    break;
                }
            }
        }

        try (RandomAccessFile following = getFileFollowing(solicitante)) {
            following.seek(0);
            while (following.getFilePointer() < following.length()) {
                String nombre = following.readUTF();
                long posEstado = following.getFilePointer();
                int estado = following.readInt();
                if (nombre.equals(getUsuarioUser(0)) && estado == 2) {
                    following.seek(posEstado);
                    following.writeInt(1); 
                    break;
                }
            }
        }
    }

    public void eliminarSolicitud(String solicitante) throws IOException {
        try (RandomAccessFile followers = getFileFollowers(getUsuarioUser(0))) {
            followers.seek(0);
            while (followers.getFilePointer() < followers.length()) {
                String nombre = followers.readUTF();
                long posEstado = followers.getFilePointer();
                int estado = followers.readInt();
                if (nombre.equals(solicitante) && estado == 2) {
                    followers.seek(posEstado);
                    followers.writeInt(3);
                    break;
                }
            }
        }

        try (RandomAccessFile following = getFileFollowing(solicitante)) {
            following.seek(0);
            while (following.getFilePointer() < following.length()) {
                String nombre = following.readUTF();
                long posEstado = following.getFilePointer();
                int estado = following.readInt();
                if (nombre.equals(getUsuarioUser(0)) && estado == 2) {
                    following.seek(posEstado);
                    following.writeInt(3);
                    break;
                }
            }
        }
    }
    
    public void hacerQueSeSignanEntreSi() {
    String[] usernames = {"marvel", "jorgue_her", "game_theory"};
    
    for (String u : usernames) {
        boolean existe = false;
        for (Usuario usr : usuarios) {
            if (usr.getUser().equals(u)) { existe = true; break; }
        }
        if (!existe) return;
    }
    
    try {
        File archivoFollowing = new File(getPath("marvel") + "/following.ins");
        if (archivoFollowing.exists() && archivoFollowing.length() > 0) {
            return;
        }
    } catch (Exception e) {  }

    for (String userA : usernames) {
        for (String userB : usernames) {
            if (userA.equals(userB)) continue; 

            try {
                try (RandomAccessFile following = getFileFollowing(userA)) {
                    following.seek(following.length());
                    following.writeUTF(userB);
                    following.writeInt(1); 
                }

                try (RandomAccessFile followers = getFileFollowers(userB)) {
                    followers.seek(followers.length());
                    followers.writeUTF(userA);
                    followers.writeInt(1); 
                }

                System.out.println(userA + " ahora sigue a " + userB);

            } catch (IOException e) {
                System.out.println("Error creando relación " + userA + " → " + userB + ": " + e.getMessage());
            }
        }
    }
    System.out.println("✅ Todas las cuentas default se siguen entre sí.");
}

}
