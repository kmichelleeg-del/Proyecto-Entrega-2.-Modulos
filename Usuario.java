public class Usuario {

    private int id;
    private String nombre;
    private String correo;
    private String carne;

    public Usuario(int id, String nombre, String correo, String carne) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.carne = carne;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCarne() {
        return carne;
    }

    public void setCarne(String carne) {
        this.carne = carne;
    }

    public boolean iniciarSesion() {
        return true;
    }

    public void cerrarSesion() {
    }
}