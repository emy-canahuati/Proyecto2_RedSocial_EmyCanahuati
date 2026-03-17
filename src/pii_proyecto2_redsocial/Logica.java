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

    public Logica() {
        try {
            carpetaRaiz = new File("src/INSTA_RAIZ/");
            carpetaRaiz.mkdir();

            carpetaStickers = new File("src/INSTA_RAIZ/stickers_globales");
            carpetaStickers.mkdir();

            usuarios = new ArrayList<>();

            File archivoUsuarios = new File("src/INSTA_RAIZ/users.ins");

            leerUsuarios(archivoUsuarios);
            cargarPublicacionesUsuarios();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
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

    // Copia la imagen al folder del usuario y retorna la nueva ruta
    public String guardarImagenUsuario(String rutaOriginal, String nombreArchivo) {
        if (rutaOriginal == null || rutaOriginal.isEmpty()) {
            return null;
        }

        try {
            File origen = new File(rutaOriginal);
            if (!origen.exists()) {
                return rutaOriginal; // Si no existe, retorna la ruta original
            }
            // Obtener extensión del archivo
            String extension = rutaOriginal.substring(rutaOriginal.lastIndexOf('.'));
            String nombreFinal = nombreArchivo + extension;

            // Destino: carpeta imagenes del usuario logueado
            File destino = new File(getPath(getUsuarioUser(0)) + "/imagenes/" + nombreFinal);

            // Copiar el archivo
            java.nio.file.Files.copy(
                    origen.toPath(),
                    destino.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

            return destino.getPath(); // Retorna la nueva ruta local

        } catch (IOException e) {
            System.out.println("Error copiando imagen: " + e.getMessage());
            return rutaOriginal; // Si falla, usa la original
        }
    }

    // Leer el mapa completo del archivo
    private HashMap<String, ArrayList<Mensaje>> leerMapaChats(String pathArchivo) {
        File archivo = new File(pathArchivo);
        if (archivo.exists() && archivo.length() > 0) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
                return (HashMap<String, ArrayList<Mensaje>>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error leyendo chats: " + e.getMessage());
            }
        }
        return new HashMap<>(); // Si no existe, retorna mapa vacío
    }

// Guardar el mapa completo en el archivo
    private void guardarMapaChats(String pathArchivo, HashMap<String, ArrayList<Mensaje>> mapa) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathArchivo))) {
            out.writeObject(mapa);
        } catch (IOException e) {
            System.out.println("Error guardando chats: " + e.getMessage());
        }
    }

// Cargar mensajes del chat con un usuario específico
    private void leerChats(Usuario user) {
        String path = getPath(getUsuarioUser(0)) + "/inbox.ins";
        HashMap<String, ArrayList<Mensaje>> mapa = leerMapaChats(path);
        // Si existe conversación con ese user la carga, si no, lista vacía
        this.mensajes = mapa.getOrDefault(user.getUser(), new ArrayList<>());
    }

// Guardar mensajes actuales sin perder los demás chats
    private void guardarChats(Usuario user) {
        String pathLogged = getPath(getUsuarioUser(0)) + "/inbox.ins";
        String pathOther = getPath(user.getUser()) + "/inbox.ins";

        // --- Guardar en inbox del usuario logueado ---
        HashMap<String, ArrayList<Mensaje>> mapaLogged = leerMapaChats(pathLogged);
        mapaLogged.put(user.getUser(), this.mensajes);   // reemplaza solo ese chat
        guardarMapaChats(pathLogged, mapaLogged);

        // --- Guardar en inbox del otro usuario ---
        HashMap<String, ArrayList<Mensaje>> mapaOther = leerMapaChats(pathOther);
        mapaOther.put(getUsuarioUser(0), this.mensajes); // clave = usuario logueado
        guardarMapaChats(pathOther, mapaOther);
    }

    public void setChatListener(ChatListener listener) {
        this.chatListener = listener;
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

// ── Enviar mensaje ────────────────────────────────────────────
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
                mensajes.add(mensaje);
                guardarChats(mensaje.getEmisor());

                if (chatListener != null) {
                    SwingUtilities.invokeLater(() -> chatListener.onMensajeRecibido(mensaje));
                }
            }
        } catch (SocketException | EOFException e) {
            if (chatListener != null) {
                SwingUtilities.invokeLater(() -> chatListener.onDesconectado());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    private String getPath(String user) {
        return "src/INSTA_RAIZ/" + user.toUpperCase();
    }

    private void crearCarpetasUser(String user) throws IOException {
        File carpetaUser = new File(getPath(user));
        carpetaUser.mkdir();

        File carpetaImg = new File(getPath(user) + "/imagenes");
        carpetaImg.mkdir();

        File carpetaFolders = new File(getPath(user) + "/folders_personales");
        carpetaFolders.mkdir();

        File carpetaStickers = new File(getPath(user) + "/stickers_personales");
        carpetaStickers.mkdir();

        new File(getPath(user) + "/followers.ins").createNewFile();
        new File(getPath(user) + "/following.ins").createNewFile();
        new File(getPath(user) + "/inbox.ins").createNewFile();
        new File(getPath(user) + "/likes.ins").createNewFile();
    }

    private RandomAccessFile getFileFollowers(String user) throws IOException {
        return new RandomAccessFile(getPath(user) + "/followers.ins", "rw");
    }

    private RandomAccessFile getFileFollowing(String user) throws IOException {
        return new RandomAccessFile(getPath(user) + "/following.ins", "rw");
    }

    private RandomAccessFile getFileInsta(String user) throws IOException {
        return new RandomAccessFile(getPath(user) + "/insta.ins", "rw");
    }

    private RandomAccessFile getFileInbox(String user) throws IOException {
        return new RandomAccessFile(getPath(user) + "/inbox.ins", "rw");
    }

    private RandomAccessFile getFileStickers(String user) throws IOException {
        return new RandomAccessFile(getPath(user) + "/stickers.ins", "rw");
    }

    private RandomAccessFile getFileLikes(String user) throws IOException {
        return new RandomAccessFile(getPath(user) + "/likes.ins", "rw");
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
                    return true; // ya está activo
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo sesiones: " + e.getMessage());
        }
        return false;
    }

// Escribir el username en el archivo de sesiones
    private void registrarSesion(String username) {
        try {
            File archivo = new File(pathSesiones);
            // Leer sesiones existentes
            ArrayList<String> sesiones = leerSesiones();
            if (!sesiones.contains(username.toLowerCase())) {
                sesiones.add(username.toLowerCase());
            }
            // Reescribir el archivo
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

// Eliminar el username del archivo al cerrar sesión
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

            // Marcar usuario como inactivo
            usuarios.get(usuarioLogged).setEstado("INACTIVO");
            guardarUsuarios();

        } catch (IOException e) {
            System.out.println("Error cerrando sesión: " + e.getMessage());
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

    private boolean Login(String nombre_login, String contraseña_login, int indexUsuario) {
        if (indexUsuario < usuarios.size()) {
            if (usuarios.get(indexUsuario) != null) {
                if (usuarios.get(indexUsuario).getUser().equals(nombre_login) && usuarios.get(indexUsuario).getContra().equals(contraseña_login)) {
                    this.usuarioLogged = indexUsuario;
                    usuarios.get(usuarioLogged).setEstado("ACTIVO");
                    return true;
                } else {
                    return Login(nombre_login, contraseña_login, indexUsuario + 1);
                }
            }
        } else {
            return false;
        }
        return false;
    }

    public boolean Login(String nombre_login, String contraseña_login) {
        boolean resultado = Login(nombre_login, contraseña_login, 0);
        if (resultado) {
            if (estaActivoEnOtraVentana(nombre_login)) {
                usuarioLogged = 0;
                return false; // ← tratarlo como login fallido
            }
            registrarSesion(nombre_login);
        }
        return resultado;
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
                errores.add(5); // Edad no es un número
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

        if (errores.isEmpty()) {
            try {
                Date fecha = Calendar.getInstance().getTime();
                Usuario nuevoUser = null;
                if (tipoCuenta.equals("PUBLICA")) {
                    nuevoUser = new UsuarioPublico(user, nombre, contra, genero, "ACTIVO", tipoCuenta, fecha, fotoPerfil, Integer.parseInt(edad));
                } else {
                    nuevoUser = new UsuarioPrivado(user, nombre, contra, genero, "ACTIVO", tipoCuenta, fecha, fotoPerfil, Integer.parseInt(edad));
                }
                usuarios.add(nuevoUser);
                crearCarpetasUser(user);

                if (fotoPerfil != null) {
                    String rutaOriginal = fotoPerfil.getDescription();
                    if (rutaOriginal != null && !rutaOriginal.isEmpty()) {
                        // Copiar manualmente sin depender de usuarioLogged
                        File origen = new File(rutaOriginal);
                        if (origen.exists()) {
                            String extension = rutaOriginal.substring(rutaOriginal.lastIndexOf('.'));
                            File destino = new File(getPath(user) + "/imagenes/perfil" + extension);
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
            // Código de éxito
        }
        return errores;
    }

    public ArrayList<Integer> modificarDatos(String nombre, String user, String contra, String genero, String edad, ImageIcon fotoPerfil, String tipoCuenta, String bio) {
        ArrayList<Integer> modificaciones = verificarNuevosDatos(nombre, user, contra, edad, bio, genero, tipoCuenta);

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
            usuarios.get(usuarioLogged).setGenero(genero);
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
                modificacion.add(9); // Edad no es un número
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
        String miUser = getUsuarioUser(0); // Guardamos tu usuario para comparar rápido

        switch (indicador) {
            case 1: // Búsqueda de Usuarios
                for (Usuario user : usuarios) {
                    String username = user.getUser().toLowerCase();
                    String nombreReal = user.getNombre().toLowerCase();
                    String query = buscado.toLowerCase();

                    // Regla: (Coincide usuario O Coincide nombre) Y NO soy yo
                    if ((username.contains(query) || nombreReal.contains(query)) && !user.getUser().equals(miUser) && user.getEstado().name().equals("ACTIVO")) {
                        matches.add(user);
                    }
                }
                break;

            case 2: // Búsqueda por Hashtag
                buscado = buscado.replace("#", "");
                for (Publicacion publi : publicaciones) {
                    // Regla: NO es mi publicación Y tiene el hashtag Y (Es pública O la sigo)
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

            case 3: // Búsqueda por Mención
                buscado = buscado.replace("@", "");
                for (Publicacion publi : publicaciones) {
                    // Regla: NO es mi publicación Y me mencionaron Y (Es pública O la sigo)
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
                // Nombre único basado en timestamp para evitar sobreescribir
                String nombreImg = "post_" + System.currentTimeMillis();
                rutaFinal = guardarImagenUsuario(rutaImagen, nombreImg);
            }

            Publicacion publi = new Publicacion(getUsuarioUser(0), contenido, rutaImagen, contenido);

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

    public int isFollowing(Usuario user) throws IOException {
        try (RandomAccessFile following = getFileFollowing(getUsuarioUser(0))) {
            following.seek(0);
            while (following.getFilePointer() < following.length()) {
                String nombre = following.readUTF();
                long posicion = following.getFilePointer();
                int estado = following.readInt();
                if (nombre.equals(user.getUser())) {
                    following.seek(posicion);
                    return estado;
                }
            }
            return 4;
        }
    }

    public int isUsuarioLoggedFollower(Usuario user) throws IOException {
        File archivo = new File(getPath(user.getUser()) + "/followers.ins");
        if (!archivo.exists()) {
            return 4;
        }

        try (RandomAccessFile followers = getFileFollowers(user.getUser())) {
            followers.seek(0);
            while (followers.getFilePointer() < followers.length()) {
                String nombre = followers.readUTF();
                long posicion = followers.getFilePointer();
                int estado = followers.readInt();
                if (nombre.equals(getUsuarioUser(0))) {
                    followers.seek(posicion);
                    return estado;
                }
            }
            return 4;
        }
    }

    public void addFollowing(Usuario user) throws IOException {
        int indicador = isFollowing(user);
        if (indicador == 1 || indicador == 2) {
            removeFollowing(user);
            removeFollower();
            return;
        } else {
            RandomAccessFile following = getFileFollowing(getUsuarioUser(0));
            if (indicador == 4) {
                following.seek(following.length());
                following.writeUTF(user.getUser());
            }
            following.writeInt(isPublica(user) ? 1 : 2);

            int indicador2 = isUsuarioLoggedFollower(user);
            RandomAccessFile followers = getFileFollowers(user.getUser());
            if (indicador2 == 4) {
                followers.seek(followers.length());
                followers.writeUTF(getUsuarioUser(0));
            }
            followers.writeInt(isPublica(user) ? 1 : 2);
        }
    }

    public void addFollower(Usuario user) throws IOException {
        int indicador = isFollower(user);
        if (indicador == 2) {
            RandomAccessFile followers = getFileFollowers(getUsuarioUser(0));
            followers.writeInt(1);
        }
    }

    public void removeFollowing(Usuario user) throws IOException {
        int indicador = isFollowing(user);
        if (indicador == 1 || indicador == 2) {
            RandomAccessFile following = getFileFollowing(getUsuarioUser(0));
            following.writeInt(3);
        }
    }

    public void removeFollower() throws IOException {
        int indicador = isUsuarioLoggedFollower(getUsuario(1));
        if (indicador == 1 || indicador == 2) {
            RandomAccessFile followers = getFileFollowers(getUsuarioUser(1));
            followers.writeInt(3);
        }
    }

    public String getListaNombresFollowers(int indicador) {
        // Obtenemos el usuario objetivo según el índice (en tu caso, el 1 para otros perfiles)
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
                if (estado == 1) // solo activos
                {
                    seguidores.add(nombre);
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo followers: " + e.getMessage());
        }

        return seguidores.isEmpty() ? "Nadie" : String.join(", ", seguidores);
    }

// Método para obtener la lista de nombres de seguidos como un String formateado
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
                if (estado == 1) // solo activos
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
        // Recorremos de atrás hacia adelante para ver lo más reciente primero
        for (int i = publicaciones.size() - 1; i >= 0; i--) {
            Publicacion publi = publicaciones.get(i);
            if (!publi.getAutor().equals(getUsuarioUser(0))) {
                Usuario autor = getAutorPublicacion(publi.getAutor());
                if (isPublica(autor) || isUsuarioLoggedFollower(autor) == 1) {
                    publiFeed.add(publi);
                }
            }
        }
        return publiFeed;
    }

    public void actualizarPublicacion(Publicacion pActualizada) {
        // 1. Actualizar en la lista de memoria
        for (int i = 0; i < publicaciones.size(); i++) {
            if (publicaciones.get(i).getFechaFormateada().equals(pActualizada.getFechaFormateada())
                    && publicaciones.get(i).getAutor().equals(pActualizada.getAutor())) {
                publicaciones.set(i, pActualizada);
                break;
            }
        }

        // 2. Reescribir el archivo .ins del autor con la lista completa de sus posts actualizada
        reescribirArchivoUsuario(pActualizada.getAutor());
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

}
