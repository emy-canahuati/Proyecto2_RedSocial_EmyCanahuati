/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pii_proyecto2_redsocial;

import java.awt.Image;
import java.util.Date;
import javax.swing.ImageIcon;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 *
 * @author emyca
 */
public class Publicacion {
    private static final long serialVersionUID = 1L;
    
    private String autor;
    private Date fechaHora;
    private String contenido;
    private ArrayList <String> hashtags;
    private ArrayList <String> menciones;
    private ArrayList <String> comentarios;
    private ArrayList <String> likes = new ArrayList<>();
    private String rutaImagen;
    private String formato;
    private String tipoMultimedia;
    
    public Publicacion(String autor, String contenido, String rutaImagen, String formato) {
        this.autor = autor;
        this.contenido = contenido;
        this.rutaImagen = rutaImagen;
        this.formato = formato;
        this.tipoMultimedia = extraerTipo(rutaImagen);
        
        this.fechaHora = new Date();
        this.hashtags = new ArrayList<>();
        this.menciones = new ArrayList<>();
        this.comentarios = new ArrayList<>();
        this.likes = new ArrayList<>();
        
        extraerMetadatos();
    }
        
    private void extraerMetadatos() {
        if (this.contenido != null) {
            String[] palabras = this.contenido.split(" ");
            for (String palabra : palabras) {
                if (palabra.startsWith("#") && palabra.length() > 1) 
                    hashtags.add(palabra.substring(1));
                if (palabra.startsWith("@") && palabra.length() > 1) 
                    menciones.add(palabra.substring(1));
            }
        }
    }
    
    private String extraerTipo(String ruta) {
        if (ruta == null || !ruta.contains(".")) {
            return "Texto";
        }
        return ruta.substring(ruta.lastIndexOf(".") + 1).toLowerCase();
    }

    public String getTipoMultimedia() { return tipoMultimedia; }
    public String getAutor() { return autor; }
    public String getContenido() { return contenido; }
    public String getRutaImagen() { return rutaImagen; }
    public String getFormato() { return formato; }
    public ArrayList<String> getHashtags() { return hashtags; }
    public ArrayList<String> getMenciones() { return menciones; }
    public ArrayList<String> getComentarios() { return comentarios; }
    
    public String getFechaFormateada() {
        return new SimpleDateFormat("dd/MM/yy").format(fechaHora);
    }
    
    public String getHoraFormateada() {
        return new SimpleDateFormat("HH:mm").format(fechaHora);
    }

    public void addComentario(String comment) {
        if (comment != null) comentarios.add(comment);
    }
    
    public void pushLike(String user){
        if(likes.contains(user))
            likes.remove(user);
        else
            likes.add(user);
    }
    
    public int getCantLikes() { 
        return likes.size(); 
    }
    
    public boolean tieneLikeDe(String username) { 
        return likes.contains(username); 
    }
}
