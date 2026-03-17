/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pii_proyecto2_redsocial;

/**
 *
 * @author emyca
 */
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Mensaje implements Serializable {
    protected static final long serialVersionUID = 1L;
    protected Usuario emisor;
    protected Usuario receptor;
    protected String contenido;
    protected LocalDateTime fechaHora;
    protected boolean leido;

    public Mensaje(Usuario emisor, Usuario receptor, String contenido) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.contenido = contenido;
        this.fechaHora = LocalDateTime.now();
        this.leido = false;
    }

    // Método abstracto para que cada tipo de mensaje valide su contenido
    public abstract void validarContenido();
    
    // Getters necesarios para la GUI
    public abstract String getContenido(); 

    public Usuario getEmisor() { return emisor; }
    public Usuario getReceptor() { return receptor; }
    
    public String getHoraFormateada() { 
        return fechaHora.format(DateTimeFormatter.ofPattern("HH:mm")); 
    }
    
    public boolean isLeido() { return leido; }
    public void marcarLeido() { this.leido = true; }

    // Útil para la lógica de burbujas: ¿el emisor soy yo?
    public boolean esMio(String miUsername) {
        return emisor.getUser().equals(miUsername);
    }
}