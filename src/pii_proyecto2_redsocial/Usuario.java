   /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pii_proyecto2_redsocial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.ImageIcon;

/**
 *
 * @author emyca
 */
public class Usuario implements Serializable{
    static enum Genero{
        FEMENINO, MASCULINO;
    }
    
    static enum EstadoCuenta{
        ACTIVO, INACTIVO;
    }
    
    static enum TipoCuenta{
        PUBLICA, PRIVADA;
    }
   
    protected String user;
    protected String nombre;
    protected String contra;
    protected String bio;
    protected Genero genero;
    protected EstadoCuenta estado;
    protected TipoCuenta tipoCuenta;
    protected Date fecha;
    protected ImageIcon fotoPerfil;
    protected int edad;
    private ArrayList<String> followersList;
    private ArrayList<String> followingList;
    
    public Usuario(String user, String nombre, String contra, String genero, String estado, String tipoCuenta, Date fecha, ImageIcon fotoPerfil, int edad) {
        this.user = user;
        this.nombre = nombre;
        this.contra = contra;
        this.genero = (genero.equals(Genero.MASCULINO.name()))? Genero.MASCULINO : Genero.FEMENINO;
        this.estado = (estado.equals(EstadoCuenta.ACTIVO.name()))? EstadoCuenta.ACTIVO : EstadoCuenta.INACTIVO;
        this.tipoCuenta = (tipoCuenta.equals(TipoCuenta.PUBLICA.name()))? TipoCuenta.PUBLICA : TipoCuenta.PRIVADA;
        this.fecha = fecha;
        this.fotoPerfil = fotoPerfil;
        this.edad = edad;
        this.bio="";
        //borrar despues
        this.followersList = new ArrayList<>();
        this.followingList = new ArrayList<>();
    }
    
    public String getUser(){
        return user;
    }
    
    public void setUser(String user){
        this.user=user;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = (genero.equals(Genero.MASCULINO.name()))? Genero.MASCULINO : Genero.FEMENINO;
    }

    public EstadoCuenta getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = (estado.equals(EstadoCuenta.ACTIVO.name()))? EstadoCuenta.ACTIVO : EstadoCuenta.INACTIVO;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = (tipoCuenta.equals(TipoCuenta.PUBLICA.name()))? TipoCuenta.PUBLICA : TipoCuenta.PRIVADA;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public ImageIcon getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(ImageIcon fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public ArrayList<String> getFollowersList() {
        if (followersList == null) {
            followersList = new ArrayList<>();
        }
        return followersList;
    }

    
    public ArrayList<String> getFollowingList() {
        if (followingList == null) {
            followingList = new ArrayList<>();
        }
        return followingList;
    }
    
    public void agregarSeguidor(String usernameSeguidor) {
        if (!followersList.contains(usernameSeguidor)) {
            followersList.add(usernameSeguidor);
        }
    }

    public void seguirA(String usernameA_Seguir) {
        if (!followingList.contains(usernameA_Seguir)) {
            followingList.add(usernameA_Seguir);
        }
    }

}
