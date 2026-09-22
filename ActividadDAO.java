import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActividadDAO {

    private static final String URL =
            "jdbc:sqlite:clubes.db";

    public void agregar(Actividad actividad)
            throws SQLException {

        String sql =
                "INSERT INTO actividades "
                + "(nombre, fecha_hora, lugar, tipo) "
                + "VALUES (?, ?, ?, ?)";

        try (
            Connection conexion =
                    DriverManager.getConnection(URL);

            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setString(
                    1,
                    actividad.getNombre()
            );

            consulta.setString(
                    2,
                    actividad.getFechaHora().toString()
            );

            consulta.setString(
                    3,
                    actividad.getLugar()
            );

            consulta.setString(
                    4,
                    actividad.getTipo()
            );

            consulta.executeUpdate();
        }
    }

    public List<Actividad> listar()
            throws SQLException {

        List<Actividad> actividades =
                new ArrayList<>();

        String sql =
                "SELECT id, nombre, fecha_hora, lugar, tipo "
                + "FROM actividades ORDER BY id";

        try (
            Connection conexion =
                    DriverManager.getConnection(URL);

            Statement consulta =
                    conexion.createStatement();

            ResultSet resultado =
                    consulta.executeQuery(sql)
        ) {
            while (resultado.next()) {
                Actividad actividad =
                        new Actividad(
                                resultado.getInt("id"),
                                resultado.getString("nombre"),
                                LocalDateTime.parse(
                                        resultado.getString(
                                                "fecha_hora"
                                        )
                                ),
                                resultado.getString("lugar"),
                                resultado.getString("tipo")
                        );

                actividades.add(actividad);
            }
        }

        return actividades;
    }

    public boolean eliminar(int actividadId)
            throws SQLException {

        String sql =
                "DELETE FROM actividades WHERE id = ?";

        try (
            Connection conexion =
                    DriverManager.getConnection(URL);

            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setInt(1, actividadId);

            return consulta.executeUpdate() > 0;
        }
    }
}
