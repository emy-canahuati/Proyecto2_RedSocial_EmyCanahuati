/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pii_proyecto2_redsocial;

import java.util.Date;
import javax.swing.ImageIcon;

/**
 *
 * @author emyca
 */
public abstract class Publicacion {
    protected String autor;
    protected Date fecha;
    protected String contenido;
    protected String hashtags;
    protected String menciones;
    protected ImageIcon imagen;
    
    abstract void mostrar();
}
