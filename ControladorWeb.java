import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ControladorWeb {

    private final EstudianteDAO estudianteDAO;
    private final ActividadDAO actividadDAO;

    public ControladorWeb() {
        estudianteDAO = new EstudianteDAO();
        actividadDAO = new ActividadDAO();
    }

    public void agregarMiembro(
            String nombre,
            String correo,
            String carne,
            String estado
    ) throws SQLException {

        EstadoMiembro estadoMiembro =
                EstadoMiembro.valueOf(estado);

        Estudiante estudiante =
                new Estudiante(
                        0,
                        nombre,
                        correo,
                        carne,
                        estadoMiembro
                );

        estudianteDAO.agregar(estudiante);
    }

    public List<Estudiante> obtenerMiembros()
            throws SQLException {

        return estudianteDAO.listar();
    }

    public boolean cambiarEstado(
            int estudianteId,
            String estado
    ) throws SQLException {

        EstadoMiembro nuevoEstado =
                EstadoMiembro.valueOf(estado);

        return estudianteDAO.cambiarEstado(
                estudianteId,
                nuevoEstado
        );
    }

    public boolean eliminarMiembro(int estudianteId)
            throws SQLException {

        return estudianteDAO.eliminar(estudianteId);
    }

    public void agregarActividad(
            String nombre,
            String fechaHora,
            String lugar,
            String tipo
    ) throws SQLException {

        Actividad actividad =
                new Actividad(
                        0,
                        nombre,
                        LocalDateTime.parse(fechaHora),
                        lugar,
                        tipo
                );

        actividadDAO.agregar(actividad);
    }

    public List<Actividad> obtenerActividades()
            throws SQLException {

        return actividadDAO.listar();
    }

    public boolean eliminarActividad(int actividadId)
            throws SQLException {

        return actividadDAO.eliminar(actividadId);
    }
}