import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EstudianteDAO {

    private static final String URL =
        "jdbc:sqlite:C:/Users/Surface Laptop 2/Downloads/sqlite-tools-win-x64-3530400/clubes.db";

    public boolean cambiarEstado(
            int estudianteId,
            EstadoMiembro nuevoEstado
    ) {
        String sql = "UPDATE estudiantes "
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

            int filasModificadas =
                consulta.executeUpdate();

            return filasModificadas > 0;

        } catch (SQLException error) {
            System.out.println(
                "Error al modificar el estado: "
                + error.getMessage()
            );

            return false;
        }
    }
}