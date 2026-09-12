import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PruebaSQLite {

    private static final String URL =
        "jdbc:sqlite:C:/Users/Surface Laptop 2/Downloads/sqlite-tools-win-x64-3530400/clubes.db";

    public static void main(String[] args) {

        try (
            Connection conexion =
                DriverManager.getConnection(URL);

            Statement consulta =
                conexion.createStatement();

            ResultSet resultado =
                consulta.executeQuery(
                    "SELECT id, nombre, estado "
                    + "FROM estudiantes"
                )
        ) {
            System.out.println(
                "Conexión realizada correctamente."
            );

            while (resultado.next()) {
                System.out.println(
                    resultado.getInt("id")
                    + " | "
                    + resultado.getString("nombre")
                    + " | "
                    + resultado.getString("estado")
                );
            }

        } catch (Exception error) {
            System.out.println(
                "ERROR: " + error.getMessage()
            );

            error.printStackTrace();
        }
    }
}