/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pii_proyecto2_redsocial;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.ImageIcon;

/**
 *
 * @author emyca
 */
public class Logica {
    private ArrayList<Usuario> usuarios;
    private Usuario extraerUsers;
    private int usuarioLogged;
    private int usuarioSelec;
    private File carpetaRaiz;
    private File carpetaStickers;
    private RandomAccessFile users;
    
    public Logica() {
        try {
            carpetaRaiz = new File("src/INSTA_RAIZ/");
            crearCarpetaRaiz();

            carpetaStickers = new File("src/INSTA_RAIZ/stickers_globales");
            crearCarpetaSticker();

            usuarios = new ArrayList<>();

            File archivo = new File("src/INSTA_RAIZ/users.ins");

            leerUsuarios(archivo);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void leerUsuarios(File archivo) throws IOException, ClassNotFoundException {
            if (archivo.exists() && archivo.length() > 0) {

                ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo));

                usuarios = (ArrayList<Usuario>) in.readObject();

                in.close();
            }
    }
    
    private void guardarUsuarios() {
    try {
        ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("src/INSTA_RAIZ/users.ins"));
        out.writeObject(usuarios);
        out.close();
    } catch (IOException e) {
        System.out.println(e.getMessage());
    }
}
    
    private void crearCarpetaRaiz(){
        if(carpetaRaiz.exists())
            return;
        else
           carpetaRaiz.mkdir();
    }
    
    private void crearCarpetaSticker(){
        if(carpetaStickers.exists())
            return;
        else
           carpetaStickers.mkdir();
    }
    
    private String getPath(String user){
        return "src/INSTA_RAIZ/"+user.toUpperCase();
    }
    
    private void crearCarpetasUser (String user) throws IOException{
        File carpetaUser = new File(getPath(user));
        carpetaUser.mkdir();
        
        File carpetaImg = new File(getPath(user)+"/imagenes");
        carpetaImg.mkdir();
        
        File carpetaFolders = new File(getPath(user)+"/folders_personales");
        carpetaFolders.mkdir();
     
        File carpetaStickers = new File(getPath(user)+"/stickers_personales");
        carpetaStickers.mkdir();        
    }
    
    private RandomAccessFile getFileFollowers(String user) throws IOException{
        return new RandomAccessFile(getPath(user)+"/followers.ins", "rw");
    }
    
    private RandomAccessFile getFileFollowing(String user) throws IOException{
        return new RandomAccessFile(getPath(user)+"/following.ins", "rw");
    }
    
    private RandomAccessFile getFileInsta(String user) throws IOException{
        return new RandomAccessFile(getPath(user)+"/insta.ins", "rw");
    }
    
    private RandomAccessFile getFileInbox(String user) throws IOException{
        return new RandomAccessFile(getPath(user)+"/inbox.ins", "rw");
    }
    
    private RandomAccessFile getFileStickers(String user) throws IOException{
        return new RandomAccessFile(getPath(user)+"/stickers.ins", "rw");
    }
    
    private boolean Login(String nombre_login, String contraseña_login, int indexUsuario){
        if (indexUsuario<usuarios.size()){
            if(usuarios.get(indexUsuario)!=null){
                if (usuarios.get(indexUsuario).getEstado() == Usuario.EstadoCuenta.INACTIVO)
                    return Login(nombre_login, contraseña_login, indexUsuario+1);

                if (usuarios.get(indexUsuario).getUser().equals(nombre_login) && usuarios.get(indexUsuario).getContra().equals(contraseña_login)){
                    this.usuarioLogged=indexUsuario;
                    return true;
                }else{
                    return Login(nombre_login, contraseña_login, indexUsuario+1);
                }
            } 
        }else{
            return false;
        }
        return false;
    }
    
    public boolean Login(String nombre_login, String contraseña_login){
        return Login(nombre_login, contraseña_login, 0);
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
        
        if(usuarios!=null){
            for (Usuario usuar : usuarios) {
                if(usuar!=null){
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
            try{
                Date fecha = Calendar.getInstance().getTime();
                Usuario nuevoUser=null;
                if (tipoCuenta.equals("PUBLICA"))
                    nuevoUser=new UsuarioPublico(user, nombre, contra, genero, "ACTIVO", tipoCuenta, fecha, fotoPerfil, Integer.parseInt(edad));
                else
                    nuevoUser=new UsuarioPrivado(user, nombre, contra, genero, "ACTIVO", tipoCuenta, fecha, fotoPerfil, Integer.parseInt(edad));
                usuarios.add(nuevoUser);
                guardarUsuarios();
                crearCarpetasUser(user);
                initFilesUser(user);
                errores.add(1);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
             // Código de éxito
        }
        return errores;
    }
    
    public ArrayList<Integer> modificarDatos(String nombre, String user, String contra, String genero, String edad, ImageIcon fotoPerfil, String tipoCuenta, String bio) {
        ArrayList<Integer> modificaciones = verificarNuevosDatos(nombre, user, contra, edad, bio, genero, tipoCuenta);
        
        if(modificaciones.contains(1)){
            usuarios.get(usuarioLogged).setNombre(nombre);
        }
        
        if(modificaciones.contains(2)){
            File carpetaAntigua = new File(getPath(usuarios.get(usuarioLogged).getUser()));
            File carpetaNueva = new File(getPath(user));
            carpetaAntigua.renameTo(carpetaNueva);
            
            usuarios.get(usuarioLogged).setUser(user);
        }
        
        if(modificaciones.contains(3)){
            usuarios.get(usuarioLogged).setContra(contra);
        }
        
        if(modificaciones.contains(4)){
            usuarios.get(usuarioLogged).setEdad(Integer.parseInt(edad));
        }
        
        if(modificaciones.contains(5)){
            usuarios.get(usuarioLogged).setBio(bio);
        }
        
        if(modificaciones.contains(6)){
            usuarios.get(usuarioLogged).setGenero(genero);
        }
        
        if(fotoPerfil!=null){
            usuarios.get(usuarioLogged).setFotoPerfil(fotoPerfil);
        }
        
        if(modificaciones.contains(7)){
            usuarios.get(usuarioLogged).setTipoCuenta(tipoCuenta);
            Usuario userModificado=null;
            if (tipoCuenta.equals("PUBLICA"))
                    userModificado=new UsuarioPublico(usuarios.get(usuarioLogged).getUser(), usuarios.get(usuarioLogged).getNombre(), usuarios.get(usuarioLogged).getContra(), usuarios.get(usuarioLogged).getGenero().name(), usuarios.get(usuarioLogged).getEstado().name(), usuarios.get(usuarioLogged).getTipoCuenta().name(), usuarios.get(usuarioLogged).getFecha(), usuarios.get(usuarioLogged).getFotoPerfil(), usuarios.get(usuarioLogged).getEdad());
                else
                    userModificado=new UsuarioPrivado(usuarios.get(usuarioLogged).getUser(), usuarios.get(usuarioLogged).getNombre(), usuarios.get(usuarioLogged).getContra(), usuarios.get(usuarioLogged).getGenero().name(), usuarios.get(usuarioLogged).getEstado().name(), usuarios.get(usuarioLogged).getTipoCuenta().name(), usuarios.get(usuarioLogged).getFecha(), usuarios.get(usuarioLogged).getFotoPerfil(), usuarios.get(usuarioLogged).getEdad());         
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
            if(usuarios!=null){
                for (Usuario usuar : usuarios) {
                    if(usuar!=null){
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
        if (!edad.equals(usuarios.get(usuarioLogged).getEdad()) && !edad.isBlank()) {
            try {
                if (Integer.parseInt(edad) > 0) {
                    modificacion.add(4);
                }
            } catch (NumberFormatException e) {
                modificacion.add(9); // Edad no es un número
            }
        }
        
        if(!bio.equals(usuarios.get(usuarioLogged).getBio()) && !bio.isBlank()){
            modificacion.add(5);
        }
        
        if(!genero.equals(usuarios.get(usuarioLogged).getGenero().name())){
            modificacion.add(6);
        }
        
        if(!tipoCuenta.equals(usuarios.get(usuarioLogged).getTipoCuenta().name())){
            modificacion.add(7);
        }
        return modificacion;
    }
    
    private void initFilesUser(String user) throws IOException{
        RandomAccessFile followers = getFileFollowers(user);
        RandomAccessFile following = getFileFollowing(user);
        
        if(followers.length()==0){
            followers.writeInt(0);
            followers.close();
        }
        if(following.length()==0){
            following.writeInt(0);
            following.close();
        }    
    }
    
    public String getUsuario(int indicador){
        return usuarios.get((indicador==0)? usuarioLogged:usuarioSelec).getUser();
    }
    
    public String getUsuarioNombre(int indicador){
        return usuarios.get((indicador==0)? usuarioLogged: usuarioSelec).getNombre();
    }
    
    public String getUsuarioLoggedEdad(){
        return String.valueOf(usuarios.get(usuarioLogged).getEdad());
    }
    
    public String getUsuarioBio(int indicador){
        return usuarios.get((indicador==0)? usuarioLogged:usuarioSelec).getBio();
    }
    
    public String getUsuarioLoggedFecha(){
        SimpleDateFormat formato = new SimpleDateFormat ("dd/MM/yy");
        return formato.format(usuarios.get(usuarioLogged).getFecha());
    }
    
    public String getUsuarioLoggedGenero(){
        if(usuarios.get(usuarioLogged).getGenero().name().equals("MASCULINO")){
            return "M";
        }
        return "F";
    }
    
    public String getUsuarioLoggedContra(){
        return usuarios.get(usuarioLogged).getContra();
    }
    
    public String getUsuarioTipo(int indicador){
        if(usuarios.get((indicador==0)? usuarioLogged:usuarioSelec).getTipoCuenta().name().equals("PRIVADA")){
            return "Privada";
        }
        return "Publica";
    }
    
    public int getUsuarioFollowers(int indicador) {
    try (RandomAccessFile followers = getFileFollowers(getUsuario(indicador))) {
        if (followers.length() < 4) return 0;
        followers.seek(0);
        return followers.readInt();
    } catch (IOException e) {
        System.out.println("Error al leer followers: " + e.getMessage());
        return 0;
    }
}

    public int getUsuarioFollowing(int indicador) {
        try (RandomAccessFile following = getFileFollowing(getUsuario(indicador))) {
            if (following.length() < 4) return 0;
            following.seek(0);
            return following.readInt();
        } catch (IOException e) {
            System.out.println("Error al leer following: " + e.getMessage());
            return 0;
        }
    }
    
    public ImageIcon getUsuarioFoto(int indicador){
        return usuarios.get((indicador==0)? usuarioLogged:usuarioSelec).getFotoPerfil();
    }
    
    public void getUsuarioSelec(String user){
        if(!usuarios.isEmpty()){
            for(int indexUsuario=0; indexUsuario<usuarios.size();indexUsuario++){
                if(usuarios.get(indexUsuario).getUser().equals(user))
                    usuarioSelec=indexUsuario;
            }
        }   
    }
    
}
