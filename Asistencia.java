import java.time.LocalDateTime;

public class Asistencia {

    private int id;
    private LocalDateTime fechaRegistro;
    private boolean presente;
    private String observacion;

    public Asistencia(int id, LocalDateTime fechaRegistro, boolean presente, String observacion) {

        this.id = id;
        this.fechaRegistro = fechaRegistro;
        this.presente = presente;
        this.observacion = observacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public boolean getPresente() {
        return presente;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void registrar() {
    }

    public void modificar() {
    }
}