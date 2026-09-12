import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ServidorWeb {
    private static final String URL =
            "jdbc:sqlite:C:/Users/Surface Laptop 2/Downloads/"
            + "sqlite-tools-win-x64-3530400/clubes.db";

    public static void main(String[] args) throws IOException {
        try {
            Class.forName("org.sqlite.JDBC");
            crearTablas();
        } catch (Exception error) {
            System.out.println("No se pudo iniciar SQLite: " + error.getMessage());
            return;
        }

        HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);
        servidor.createContext("/", ServidorWeb::procesarSolicitud);
        servidor.setExecutor(null);
        servidor.start();

        System.out.println("Servidor iniciado correctamente.");
        System.out.println("Abre http://localhost:8080");
    }

    private static void procesarSolicitud(HttpExchange intercambio) throws IOException {
        String ruta = intercambio.getRequestURI().getPath();
        String metodo = intercambio.getRequestMethod();

        try {
            if (ruta.equals("/") && metodo.equals("GET")) {
                mostrarInicio(intercambio);
            } else if (ruta.equals("/datos") && metodo.equals("GET")) {
                mostrarDatos(intercambio);
            } else if (ruta.equals("/agregar-miembro") && metodo.equals("POST")) {
                agregarMiembro(intercambio);
            } else if (ruta.equals("/cambiar-estado") && metodo.equals("POST")) {
                cambiarEstado(intercambio);
            } else if (ruta.equals("/agregar-actividad") && metodo.equals("POST")) {
                agregarActividad(intercambio);
            } else if (ruta.equals("/eliminar-miembro") && metodo.equals("POST")) {
                eliminarMiembro(intercambio);
            } else if (ruta.equals("/eliminar-actividad") && metodo.equals("POST")) {
                eliminarActividad(intercambio);
            } else {
                enviar(intercambio, 404,
                        "<h1>Página no encontrada</h1><a href='/'>Regresar</a>");
            }
        } catch (Exception error) {
            error.printStackTrace();
            enviar(intercambio, 500,
                    "<h1>Error</h1><p>" + escapar(error.getMessage())
                    + "</p><a href='/'>Regresar</a>");
        }
    }

    private static void crearTablas() throws Exception {
        try (Connection conexion = DriverManager.getConnection(URL);
             Statement consulta = conexion.createStatement()) {
            consulta.execute("""
                    CREATE TABLE IF NOT EXISTS estudiantes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL,
                        correo TEXT NOT NULL UNIQUE,
                        carne TEXT NOT NULL UNIQUE,
                        estado TEXT NOT NULL
                    )
                    """);

            consulta.execute("""
                    CREATE TABLE IF NOT EXISTS actividades (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL,
                        fecha_hora TEXT NOT NULL,
                        lugar TEXT NOT NULL,
                        tipo TEXT NOT NULL
                    )
                    """);
        }
    }

    private static void mostrarInicio(HttpExchange intercambio) throws IOException {
        Path archivo = Path.of("html", "index.html");
        if (!Files.exists(archivo)) {
            enviar(intercambio, 404, "<h1>No se encontró html/index.html</h1>");
            return;
        }

        byte[] contenido = Files.readAllBytes(archivo);
        intercambio.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        intercambio.sendResponseHeaders(200, contenido.length);
        intercambio.getResponseBody().write(contenido);
        intercambio.close();
    }

    private static void agregarMiembro(HttpExchange intercambio) throws Exception {
        Map<String, String> datos = leerFormulario(intercambio);
        validar(datos, "nombre", "correo", "carne", "estado");

        String sql = "INSERT INTO estudiantes (nombre, correo, carne, estado) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conexion = DriverManager.getConnection(URL);
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setString(1, datos.get("nombre"));
            consulta.setString(2, datos.get("correo"));
            consulta.setString(3, datos.get("carne"));
            consulta.setString(4, datos.get("estado"));
            consulta.executeUpdate();
        }
        redireccionar(intercambio, "/datos");
    }

    private static void cambiarEstado(HttpExchange intercambio) throws Exception {
        Map<String, String> datos = leerFormulario(intercambio);
        validar(datos, "id", "estado");

        String sql = "UPDATE estudiantes SET estado = ? WHERE id = ?";
        int modificados;
        try (Connection conexion = DriverManager.getConnection(URL);
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setString(1, datos.get("estado"));
            consulta.setInt(2, Integer.parseInt(datos.get("id")));
            modificados = consulta.executeUpdate();
        }

        if (modificados == 0) {
            enviar(intercambio, 404,
                    "<h1>Miembro no encontrado</h1><a href='/'>Regresar</a>");
            return;
        }
        redireccionar(intercambio, "/datos");
    }

    private static void agregarActividad(HttpExchange intercambio) throws Exception {
        Map<String, String> datos = leerFormulario(intercambio);
        validar(datos, "nombre", "fechaHora", "lugar", "tipo");

        String sql = "INSERT INTO actividades (nombre, fecha_hora, lugar, tipo) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conexion = DriverManager.getConnection(URL);
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setString(1, datos.get("nombre"));
            consulta.setString(2, datos.get("fechaHora"));
            consulta.setString(3, datos.get("lugar"));
            consulta.setString(4, datos.get("tipo"));
            consulta.executeUpdate();
        }
        redireccionar(intercambio, "/datos");
    }

    private static void eliminarMiembro(HttpExchange intercambio) throws Exception {
        Map<String, String> datos = leerFormulario(intercambio);
        validar(datos, "id");

        try (Connection conexion = DriverManager.getConnection(URL);
             PreparedStatement consulta = conexion.prepareStatement(
                     "DELETE FROM estudiantes WHERE id = ?")) {
            consulta.setInt(1, Integer.parseInt(datos.get("id")));
            consulta.executeUpdate();
        }
        redireccionar(intercambio, "/datos");
    }

    private static void eliminarActividad(HttpExchange intercambio) throws Exception {
        Map<String, String> datos = leerFormulario(intercambio);
        validar(datos, "id");

        try (Connection conexion = DriverManager.getConnection(URL);
             PreparedStatement consulta = conexion.prepareStatement(
                     "DELETE FROM actividades WHERE id = ?")) {
            consulta.setInt(1, Integer.parseInt(datos.get("id")));
            consulta.executeUpdate();
        }
        redireccionar(intercambio, "/datos");
    }

    private static void mostrarDatos(HttpExchange intercambio) throws Exception {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width,initial-scale=1'>")
                .append("<title>Datos del club</title><style>")
                .append("body{font-family:Arial;max-width:1100px;margin:30px auto;padding:20px;background:#f3f5f9}")
                .append("section{background:white;padding:20px;margin:20px 0;border-radius:10px}")
                .append("table{width:100%;border-collapse:collapse}th,td{padding:10px;border:1px solid #ccc;text-align:left}")
                .append("th{background:#234578;color:white}tr:nth-child(even){background:#f3f5f9}")
                .append("a{display:inline-block;padding:10px 16px;background:#3766b1;color:white;text-decoration:none;border-radius:6px}")
                .append("form{margin:0}.eliminar{padding:8px 12px;border:0;border-radius:6px;background:#b42318;color:white;cursor:pointer}")
                .append(".eliminar:hover{background:#8f1c13}")
                .append("</style></head><body><h1>Información registrada</h1><a href='/'>Regresar</a>");

        try (Connection conexion = DriverManager.getConnection(URL)) {
            tablaMiembros(html, conexion);
            tablaActividades(html, conexion);
        }

        html.append("</body></html>");
        enviar(intercambio, 200, html.toString());
    }

    private static void tablaMiembros(StringBuilder html, Connection conexion) throws Exception {
        html.append("<section><h2>Miembros</h2><table><tr>")
                .append("<th>ID</th><th>Nombre</th><th>Correo</th><th>Carné</th><th>Estado</th><th>Acciones</th></tr>");
        try (Statement consulta = conexion.createStatement();
             ResultSet resultado = consulta.executeQuery(
                     "SELECT id,nombre,correo,carne,estado FROM estudiantes ORDER BY id")) {
            boolean hayDatos = false;
            while (resultado.next()) {
                hayDatos = true;
                html.append("<tr><td>").append(resultado.getInt("id"))
                        .append("</td><td>").append(escapar(resultado.getString("nombre")))
                        .append("</td><td>").append(escapar(resultado.getString("correo")))
                        .append("</td><td>").append(escapar(resultado.getString("carne")))
                        .append("</td><td>").append(escapar(resultado.getString("estado")))
                        .append("</td><td><form action='/eliminar-miembro' method='post'>")
                        .append("<input type='hidden' name='id' value='")
                        .append(resultado.getInt("id"))
                        .append("'><button class='eliminar' type='submit'>Eliminar usuario</button>")
                        .append("</form></td></tr>");
            }
            if (!hayDatos) {
                html.append("<tr><td colspan='6'>No hay miembros registrados.</td></tr>");
            }
        }
        html.append("</table></section>");
    }

    private static void tablaActividades(StringBuilder html, Connection conexion) throws Exception {
        html.append("<section><h2>Actividades</h2><table><tr>")
                .append("<th>ID</th><th>Nombre</th><th>Fecha y hora</th><th>Lugar</th><th>Tipo</th><th>Acciones</th></tr>");
        try (Statement consulta = conexion.createStatement();
             ResultSet resultado = consulta.executeQuery(
                     "SELECT id,nombre,fecha_hora,lugar,tipo FROM actividades ORDER BY id")) {
            boolean hayDatos = false;
            while (resultado.next()) {
                hayDatos = true;
                html.append("<tr><td>").append(resultado.getInt("id"))
                        .append("</td><td>").append(escapar(resultado.getString("nombre")))
                        .append("</td><td>").append(escapar(resultado.getString("fecha_hora")))
                        .append("</td><td>").append(escapar(resultado.getString("lugar")))
                        .append("</td><td>").append(escapar(resultado.getString("tipo")))
                        .append("</td><td><form action='/eliminar-actividad' method='post'>")
                        .append("<input type='hidden' name='id' value='")
                        .append(resultado.getInt("id"))
                        .append("'><button class='eliminar' type='submit'>Eliminar actividad</button>")
                        .append("</form></td></tr>");
            }
            if (!hayDatos) {
                html.append("<tr><td colspan='6'>No hay actividades registradas.</td></tr>");
            }
        }
        html.append("</table></section>");
    }

    private static Map<String, String> leerFormulario(HttpExchange intercambio) throws IOException {
        String cuerpo = new String(intercambio.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> datos = new HashMap<>();
        if (cuerpo.isBlank()) {
            return datos;
        }

        for (String campo : cuerpo.split("&")) {
            String[] partes = campo.split("=", 2);
            String nombre = URLDecoder.decode(partes[0], StandardCharsets.UTF_8);
            String valor = partes.length == 2
                    ? URLDecoder.decode(partes[1], StandardCharsets.UTF_8) : "";
            datos.put(nombre, valor.trim());
        }
        return datos;
    }

    private static void validar(Map<String, String> datos, String... campos) {
        for (String campo : campos) {
            if (!datos.containsKey(campo) || datos.get(campo).isBlank()) {
                throw new IllegalArgumentException("Falta el campo: " + campo);
            }
        }
    }

    private static void redireccionar(HttpExchange intercambio, String destino) throws IOException {
        intercambio.getResponseHeaders().set("Location", destino);
        intercambio.sendResponseHeaders(303, -1);
        intercambio.close();
    }

    private static void enviar(HttpExchange intercambio, int codigo, String contenido)
            throws IOException {
        byte[] respuesta = contenido.getBytes(StandardCharsets.UTF_8);
        intercambio.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        intercambio.sendResponseHeaders(codigo, respuesta.length);
        intercambio.getResponseBody().write(respuesta);
        intercambio.close();
    }

    private static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
