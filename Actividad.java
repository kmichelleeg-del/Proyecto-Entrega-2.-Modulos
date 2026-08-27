import java.time.LocalDateTime;

public class Actividad {

    private int id;
    private String nombre;
    private LocalDateTime fechaHora;
    private String lugar;
    private String tipo;

    public Actividad(int id, String nombre, LocalDateTime fechaHora, String lugar, String tipo) {

        this.id = id;
        this.nombre = nombre;
        this.fechaHora = fechaHora;
        this.lugar = lugar;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void iniciar() {
    }

    public void finalizar() {
    }
}