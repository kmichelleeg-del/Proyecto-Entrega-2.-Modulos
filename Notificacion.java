import java.time.LocalDateTime;

public class Notificacion {

    private int id;
    private String titulo;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private TipoNotificacion tipo;
    private boolean leida;

    public Notificacion(int id, String titulo, String mensaje, LocalDateTime fechaEnvio, TipoNotificacion tipo, boolean leida) {

        this.id = id;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio;
        this.tipo = tipo;
        this.leida = leida;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacion tipo) {
        this.tipo = tipo;
    }

    public boolean getLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public boolean enviar() {
        return true;
    }

    public void marcarLeida() {
        leida = true;
    }
}