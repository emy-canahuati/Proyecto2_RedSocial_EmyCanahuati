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
import java.util.Date;

public class Notificacion implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Tipo {
        SOLICITUD, SEGUIDOR, MENSAJE
    }

    private Tipo tipo;
    private String deQuienUser; 
    private String mensaje;
    private Date fecha;
    private boolean vista;

    public Notificacion(Tipo tipo, String deQuienUser, String mensaje) {
        this.tipo = tipo;
        this.deQuienUser = deQuienUser;
        this.mensaje = mensaje;
        this.fecha = new Date();
        this.vista = false;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getDeQuienUser() {
        return deQuienUser;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Date getFecha() {
        return fecha;
    }

    public boolean isVista() {
        return vista;
    }

    public void marcarVista() {
        this.vista = true;
    }
}
