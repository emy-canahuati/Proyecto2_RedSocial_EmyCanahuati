/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pii_proyecto2_redsocial;

import java.io.*;
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
    private int usuarioLogged;
    private File carpetaRaiz;
    private File carpetaStickers;
    private RandomAccessFile users;
    
    public Logica(){
        try{
        carpetaRaiz = new File("scr/INSTA_RAIZ/");
        crearCarpetaRaiz();
        carpetaStickers = new File("scr/INSTA_RAIZ/stickers_globales");
        crearCarpetaSticker();
        users=new RandomAccessFile("scr/INSTA_RAIZ/users.ins","rw");
        usuarios= new ArrayList<>();//que extraiga los usuarios de los archivos
        }catch(IOException e){
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
        return "scr/INSTA_RAIZ/"+user.toUpperCase();
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
                if (usuarios.get(indexUsuario).getEstado().equals("INACTIVO"))
                    return Login(nombre_login, contraseña_login, indexUsuario+1);

                if (usuarios.get(indexUsuario).getNombre().equals(nombre_login) && usuarios.get(indexUsuario).getContra().equals(contraseña_login)){
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
    
    public ArrayList<Integer> verificarDatos(String nombre, String user, String contra, String edad) {
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
        return errores;
    }

    public ArrayList<Integer> setDatos(String nombre, String user, String contra, String genero, String edad, ImageIcon fotoPerfil, String tipoCuenta) {
        ArrayList<Integer> errores = verificarDatos(nombre, user, contra, edad);

        if (errores.isEmpty()) {
            try{
                Date fecha = Calendar.getInstance().getTime();
                Usuario nuevoUser=null;
                if (tipoCuenta.equals("PUBLICA"))
                    new UsuarioPublico(user, nombre, contra, genero, "ACTIVO", tipoCuenta, fecha, fotoPerfil, Integer.parseInt(edad));
                else
                    new UsuarioPrivado(user, nombre, contra, genero, "ACTIVO", tipoCuenta, fecha, fotoPerfil, Integer.parseInt(edad));
                usuarios.add(nuevoUser);
                crearCarpetasUser(user);
                errores.add(1);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
             // Código de éxito
        }
        return errores;
    }
}
