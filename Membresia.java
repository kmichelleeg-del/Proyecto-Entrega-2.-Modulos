import java.util.Date;

public class Membresia {

    private Date fechaIngreso;
    private EstadoMiembro estado;
    private int totalAsistencias;
    private Date ultimaAsistencia;

    public Membresia(Date fechaIngreso, EstadoMiembro estado, int totalAsistencias, Date ultimaAsistencia) {

        this.fechaIngreso = fechaIngreso;
        this.estado = estado;
        this.totalAsistencias = totalAsistencias;
        this.ultimaAsistencia = ultimaAsistencia;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public EstadoMiembro getEstado() {
        return estado;
    }

    public void setEstado(EstadoMiembro estado) {
        this.estado = estado;
    }

    public int getTotalAsistencias() {
        return totalAsistencias;
    }

    public void setTotalAsistencias(int totalAsistencias) {
        this.totalAsistencias = totalAsistencias;
    }

    public Date getUltimaAsistencia() {
        return ultimaAsistencia;
    }

    public void setUltimaAsistencia(Date ultimaAsistencia) {
        this.ultimaAsistencia = ultimaAsistencia;
    }

    public void actualizarEstado() {
    }

    public void solicitarContinuidad() {
    }
}