public class Estudiante extends Usuario {

    private EstadoMiembro estado;

    public Estudiante(int id, String nombre, String correo, String carne, EstadoMiembro estado) {
        super(id, nombre, correo, carne);
        this.estado = estado;
    }

    public EstadoMiembro getEstado() {
        return estado;
    }

    public void setEstado(EstadoMiembro estado) {
        this.estado = estado;
    }

    public void unirseClub(Club club) {
        estado = EstadoMiembro.ACTIVO;
    }

    public void salirClub(Club club) {
        estado = EstadoMiembro.RETIRADO;
    }

    public void confirmarContinuidad() {
        estado = EstadoMiembro.PENDIENTE_CONFIRMACION;
    }
}