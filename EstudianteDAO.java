import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {

    private static final String URL =
            "jdbc:sqlite:clubes.db";

    public void agregar(Estudiante estudiante)
            throws SQLException {

        String sql =
                "INSERT INTO estudiantes "
                + "(nombre, correo, carne, estado) "
                + "VALUES (?, ?, ?, ?)";

        try (
            Connection conexion =
                    DriverManager.getConnection(URL);

            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setString(1, estudiante.getNombre());
            consulta.setString(2, estudiante.getCorreo());
            consulta.setString(3, estudiante.getCarne());
            consulta.setString(
                    4,
                    estudiante.getEstado().name()
            );

            consulta.executeUpdate();
        }
    }

    public List<Estudiante> listar()
            throws SQLException {

        List<Estudiante> estudiantes =
                new ArrayList<>();

        String sql =
                "SELECT id, nombre, correo, carne, estado "
                + "FROM estudiantes ORDER BY id";

        try (
            Connection conexion =
                    DriverManager.getConnection(URL);

            Statement consulta =
                    conexion.createStatement();

            ResultSet resultado =
                    consulta.executeQuery(sql)
        ) {
            while (resultado.next()) {
                Estudiante estudiante =
                        new Estudiante(
                                resultado.getInt("id"),
                                resultado.getString("nombre"),
                                resultado.getString("correo"),
                                resultado.getString("carne"),
                                EstadoMiembro.valueOf(
                                        resultado.getString(
                                                "estado"
                                        )
                                )
                        );

                estudiantes.add(estudiante);
            }
        }

        return estudiantes;
    }

    public boolean cambiarEstado(
            int estudianteId,
            EstadoMiembro nuevoEstado
    ) throws SQLException {

        String sql =
                "UPDATE estudiantes "
                + "SET estado = ? "
                + "WHERE id = ?";

        try (
            Connection conexion =
                    DriverManager.getConnection(URL);

            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setString(
                    1,
                    nuevoEstado.name()
            );

            consulta.setInt(
                    2,
                    estudianteId
            );

            return consulta.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int estudianteId)
            throws SQLException {

        String sql =
                "DELETE FROM estudiantes WHERE id = ?";

        try (
            Connection conexion =
                    DriverManager.getConnection(URL);

            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setInt(1, estudianteId);

            return consulta.executeUpdate() > 0;
        }
    }
}