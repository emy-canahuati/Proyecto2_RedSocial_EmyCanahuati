/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pii_proyecto2_redsocial;

import javax.swing.ImageIcon;

/**
 *
 * @author emyca
 */
public class MensajeSticker extends Mensaje {
    
    public MensajeSticker(Usuario emisor, Usuario receptor, String rutaSticker) {
        super(emisor, receptor, rutaSticker);
        validarContenido();
    }

    @Override
    public void validarContenido() {
        if (this.contenido == null || this.contenido.isEmpty()) {
            this.contenido = "default_sticker.png"; 
        }
    }

    @Override
    public String getContenido() {
        return contenido; 
    }
}
