public class Coordinador extends Usuario {

    private String cargo;

    public Coordinador(int id, String nombre, String correo, String carne,
                       String cargo) {

        super(id, nombre, correo, carne);
        this.cargo = cargo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void registrarAsistencia() {
    }

    public void cambiarEstado(Membresia miembro) {
    }

    public Actividad crearActividad() {
        return null;
    }
}