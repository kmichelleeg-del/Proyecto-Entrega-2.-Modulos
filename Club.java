import java.util.ArrayList;

public class Club {

    private int id;
    private String nombre;
    private String descripcion;
    private String horario;
    private String ubicacion;
    private String categoria;

    private ArrayList<Membresia> miembros;

    public Club(
            int id,
            String nombre,
            String descripcion,
            String horario,
            String ubicacion,
            String categoria
    ) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.horario = horario;
        this.ubicacion = ubicacion;
        this.categoria = categoria;

        miembros = new ArrayList<Membresia>();
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public ArrayList<Membresia> getMiembros() {
        return miembros;
    }

    public void setMiembros(
            ArrayList<Membresia> miembros
    ) {
        this.miembros = miembros;
    }

    public void agregarMiembro(Membresia membresia) {
        miembros.add(membresia);
    }

    public void publicarAviso() {
        System.out.println(
                "El club " + nombre + " publicó un nuevo aviso."
        );
    }

    public ArrayList<Membresia> miembrosActivos() {
        ArrayList<Membresia> activos =
                new ArrayList<Membresia>();

        for (Membresia membresia : miembros) {
            if (membresia.getEstado()
                    == EstadoMiembro.ACTIVO) {

                activos.add(membresia);
            }
        }

        return activos;
    }

    @Override
    public String toString() {
        return "Club{"
                + "id=" + id
                + ", nombre='" + nombre + '\''
                + ", descripcion='" + descripcion + '\''
                + ", horario='" + horario + '\''
                + ", ubicacion='" + ubicacion + '\''
                + ", categoria='" + categoria + '\''
                + ", cantidadMiembros=" + miembros.size()
                + '}';
    }
}