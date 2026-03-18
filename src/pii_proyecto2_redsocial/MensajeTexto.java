package pii_proyecto2_redsocial;

public class MensajeTexto extends Mensaje {
    
    public MensajeTexto(Usuario emisor, Usuario receptor, String contenido) {
        super(emisor, receptor, contenido);
        validarContenido(); 
    }
    
    @Override
    public void validarContenido() { 
        if (contenido != null && contenido.length() > 300) {
            this.contenido = contenido.substring(0, 300); 
        }
    }
    
    @Override
    public String getContenido() {
        return contenido;
    }
}