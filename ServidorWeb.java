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
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServidorWeb {

    private static final ControladorWeb controlador =
            new ControladorWeb();

    private static final String URL =
            "jdbc:sqlite:clubes.db";

    public static void main(String[] args)
            throws IOException {

        try {
            Class.forName("org.sqlite.JDBC");
            crearTablas();

        } catch (Exception error) {
            System.out.println(
                    "No se pudo iniciar SQLite: "
                    + error.getMessage()
            );
            return;
        }

        HttpServer servidor =
                HttpServer.create(
                        new InetSocketAddress(8080),
                        0
                );

        servidor.createContext(
                "/",
                ServidorWeb::procesarSolicitud
        );

        servidor.setExecutor(null);
        servidor.start();

        System.out.println(
                "Servidor iniciado correctamente."
        );

        System.out.println(
                "Abre http://localhost:8080"
        );
    }

    private static void procesarSolicitud(
            HttpExchange intercambio
    ) throws IOException {

        String ruta =
                intercambio.getRequestURI().getPath();

        String metodo =
                intercambio.getRequestMethod();

        try {
            if (
                    ruta.equals("/")
                    && metodo.equals("GET")
            ) {
                mostrarInicio(intercambio);

            } else if (
                    ruta.equals("/uvglog.png")
                    && metodo.equals("GET")
            ) {
                mostrarLogo(intercambio);

            } else if (
                    ruta.equals("/datos")
                    && metodo.equals("GET")
            ) {
                mostrarDatos(intercambio);

            } else if (
                    ruta.equals("/agregar-miembro")
                    && metodo.equals("POST")
            ) {
                agregarMiembro(intercambio);

            } else if (
                    ruta.equals("/cambiar-estado")
                    && metodo.equals("POST")
            ) {
                cambiarEstado(intercambio);

            } else if (
                    ruta.equals("/agregar-actividad")
                    && metodo.equals("POST")
            ) {
                agregarActividad(intercambio);

            } else if (
                    ruta.equals("/eliminar-miembro")
                    && metodo.equals("POST")
            ) {
                eliminarMiembro(intercambio);

            } else if (
                    ruta.equals("/eliminar-actividad")
                    && metodo.equals("POST")
            ) {
                eliminarActividad(intercambio);

            } else {
                enviar(
                        intercambio,
                        404,
                        "<h1>Página no encontrada</h1>"
                        + "<a href='/'>Regresar</a>"
                );
            }

        } catch (Exception error) {
            error.printStackTrace();

            enviar(
                    intercambio,
                    500,
                    "<h1>Error</h1>"
                    + "<p>"
                    + escapar(error.getMessage())
                    + "</p>"
                    + "<a href='/'>Regresar</a>"
            );
        }
    }

    private static void crearTablas()
            throws Exception {

        try (
            Connection conexion =
                    DriverManager.getConnection(URL);

            Statement consulta =
                    conexion.createStatement()
        ) {
            consulta.execute(
                    """
                    CREATE TABLE IF NOT EXISTS estudiantes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL,
                        correo TEXT NOT NULL UNIQUE,
                        carne TEXT NOT NULL UNIQUE,
                        estado TEXT NOT NULL
                    )
                    """
            );

            consulta.execute(
                    """
                    CREATE TABLE IF NOT EXISTS actividades (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL,
                        fecha_hora TEXT NOT NULL,
                        lugar TEXT NOT NULL,
                        tipo TEXT NOT NULL
                    )
                    """
            );
        }
    }

    private static void mostrarInicio(
            HttpExchange intercambio
    ) throws IOException {

        Path archivo =
                Path.of("html", "index.html");

        if (!Files.exists(archivo)) {
            enviar(
                    intercambio,
                    404,
                    "<h1>No se encontró "
                    + "html/index.html</h1>"
            );
            return;
        }

        byte[] contenido =
                Files.readAllBytes(archivo);

        intercambio.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8"
        );

        intercambio.sendResponseHeaders(
                200,
                contenido.length
        );

        intercambio.getResponseBody().write(
                contenido
        );

        intercambio.close();
    }

    private static void mostrarLogo(
            HttpExchange intercambio
    ) throws IOException {

        Path archivo =
                Path.of("html", "uvglog.png");

        if (!Files.exists(archivo)) {
            intercambio.sendResponseHeaders(
                    404,
                    -1
            );

            intercambio.close();
            return;
        }

        byte[] contenido =
                Files.readAllBytes(archivo);

        intercambio.getResponseHeaders().set(
                "Content-Type",
                "image/png"
        );

        intercambio.sendResponseHeaders(
                200,
                contenido.length
        );

        intercambio.getResponseBody().write(
                contenido
        );

        intercambio.close();
    }

    private static void agregarMiembro(
            HttpExchange intercambio
    ) throws Exception {

        Map<String, String> datos =
                leerFormulario(intercambio);

        validar(
                datos,
                "nombre",
                "correo",
                "carne",
                "estado"
        );

        controlador.agregarMiembro(
                datos.get("nombre"),
                datos.get("correo"),
                datos.get("carne"),
                datos.get("estado")
        );

        redireccionar(
                intercambio,
                "/datos"
        );
    }

    private static void cambiarEstado(
            HttpExchange intercambio
    ) throws Exception {

        Map<String, String> datos =
                leerFormulario(intercambio);

        validar(
                datos,
                "id",
                "estado"
        );

        int id =
                Integer.parseInt(
                        datos.get("id")
                );

        boolean modificado =
                controlador.cambiarEstado(
                        id,
                        datos.get("estado")
                );

        if (!modificado) {
            enviar(
                    intercambio,
                    404,
                    "<h1>Miembro no encontrado</h1>"
                    + "<a href='/'>Regresar</a>"
            );
            return;
        }

        redireccionar(
                intercambio,
                "/datos"
        );
    }

    private static void agregarActividad(
            HttpExchange intercambio
    ) throws Exception {

        Map<String, String> datos =
                leerFormulario(intercambio);

        validar(
                datos,
                "nombre",
                "fechaHora",
                "lugar",
                "tipo"
        );

        controlador.agregarActividad(
                datos.get("nombre"),
                datos.get("fechaHora"),
                datos.get("lugar"),
                datos.get("tipo")
        );

        redireccionar(
                intercambio,
                "/datos"
        );
    }

    private static void eliminarMiembro(
            HttpExchange intercambio
    ) throws Exception {

        Map<String, String> datos =
                leerFormulario(intercambio);

        validar(
                datos,
                "id"
        );

        int id =
                Integer.parseInt(
                        datos.get("id")
                );

        controlador.eliminarMiembro(id);

        redireccionar(
                intercambio,
                "/datos"
        );
    }

    private static void eliminarActividad(
            HttpExchange intercambio
    ) throws Exception {

        Map<String, String> datos =
                leerFormulario(intercambio);

        validar(
                datos,
                "id"
        );

        int id =
                Integer.parseInt(
                        datos.get("id")
                );

        controlador.eliminarActividad(id);

        redireccionar(
                intercambio,
                "/datos"
        );
    }

    private static void mostrarDatos(
            HttpExchange intercambio
    ) throws Exception {

        List<Estudiante> estudiantes =
                controlador.obtenerMiembros();

        List<Actividad> actividades =
                controlador.obtenerActividades();

        StringBuilder html =
                new StringBuilder();

        html.append(
                """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1">
                    <title>Datos del club</title>
                    <style>
                        :root {
                            --verde: #009444;
                            --dorado: #d79b27;
                            --gris: #5d7079;
                            --fondo: #eef1f2;
                        }

                        * {
                            box-sizing: border-box;
                        }

                        body {
                            margin: 0;
                            font-family: Arial, sans-serif;
                            background: var(--fondo);
                            color: #263238;
                        }

                        header {
                            position: relative;
                            min-height: 145px;
                            padding: 35px 20px;
                            color: white;
                            text-align: center;
                            background: var(--verde);
                            border-bottom: 7px solid var(--dorado);
                        }

                        .logo {
                            position: absolute;
                            top: 15px;
                            left: 20px;
                            width: 105px;
                            height: 105px;
                            object-fit: contain;
                        }

                        main {
                            max-width: 1100px;
                            margin: 30px auto;
                            padding: 0 20px;
                        }

                        section {
                            overflow-x: auto;
                            margin: 20px 0;
                            padding: 20px;
                            background: white;
                            border-radius: 10px;
                            border-top: 5px solid var(--dorado);
                        }

                        table {
                            width: 100%;
                            border-collapse: collapse;
                        }

                        th,
                        td {
                            padding: 10px;
                            text-align: left;
                            border: 1px solid #ccc;
                        }

                        th {
                            color: white;
                            background: var(--verde);
                        }

                        tr:nth-child(even) {
                            background: #f4f6f6;
                        }

                        h2 {
                            color: var(--gris);
                        }

                        .regresar {
                            display: inline-block;
                            padding: 10px 16px;
                            color: white;
                            text-decoration: none;
                            background: var(--verde);
                            border-radius: 6px;
                        }

                        form {
                            margin: 0;
                        }

                        .eliminar {
                            padding: 8px 12px;
                            color: #202a2e;
                            font-weight: bold;
                            background: var(--dorado);
                            border: 0;
                            border-radius: 6px;
                            cursor: pointer;
                        }

                        .eliminar:hover {
                            color: white;
                            background: var(--gris);
                        }

                        @media (max-width: 650px) {
                            header {
                                padding-top: 135px;
                            }

                            .logo {
                                left: 50%;
                                transform: translateX(-50%);
                            }
                        }
                    </style>
                </head>
                <body>
                    <header>
                        <img class="logo"
                             src="/uvglog.png"
                             alt="Logo UVG">
                        <h1>Información registrada</h1>
                    </header>

                    <main>
                        <a class="regresar" href="/">
                            Regresar al inicio
                        </a>
                """
        );

        agregarTablaMiembros(
                html,
                estudiantes
        );

        agregarTablaActividades(
                html,
                actividades
        );

        html.append(
                """
                    </main>
                </body>
                </html>
                """
        );

        enviar(
                intercambio,
                200,
                html.toString()
        );
    }

    private static void agregarTablaMiembros(
            StringBuilder html,
            List<Estudiante> estudiantes
    ) {
        html.append(
                """
                <section>
                    <h2>Miembros</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Correo</th>
                                <th>Carné</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                """
        );

        if (estudiantes.isEmpty()) {
            html.append(
                    """
                    <tr>
                        <td colspan="6">
                            No hay miembros registrados.
                        </td>
                    </tr>
                    """
            );

        } else {
            for (Estudiante estudiante : estudiantes) {
                html.append("<tr>");

                html.append("<td>")
                        .append(estudiante.getId())
                        .append("</td>");

                html.append("<td>")
                        .append(
                                escapar(
                                        estudiante.getNombre()
                                )
                        )
                        .append("</td>");

                html.append("<td>")
                        .append(
                                escapar(
                                        estudiante.getCorreo()
                                )
                        )
                        .append("</td>");

                html.append("<td>")
                        .append(
                                escapar(
                                        estudiante.getCarne()
                                )
                        )
                        .append("</td>");

                html.append("<td>")
                        .append(
                                escapar(
                                        estudiante
                                                .getEstado()
                                                .name()
                                )
                        )
                        .append("</td>");

                html.append(
                        "<td>"
                        + "<form action='/eliminar-miembro' "
                        + "method='post'>"
                        + "<input type='hidden' "
                        + "name='id' value='"
                );

                html.append(
                        estudiante.getId()
                );

                html.append(
                        "'>"
                        + "<button class='eliminar' "
                        + "type='submit'>"
                        + "Eliminar usuario"
                        + "</button>"
                        + "</form>"
                        + "</td>"
                        + "</tr>"
                );
            }
        }

        html.append(
                """
                        </tbody>
                    </table>
                </section>
                """
        );
    }

    private static void agregarTablaActividades(
            StringBuilder html,
            List<Actividad> actividades
    ) {
        html.append(
                """
                <section>
                    <h2>Actividades</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Fecha y hora</th>
                                <th>Lugar</th>
                                <th>Tipo</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                """
        );

        if (actividades.isEmpty()) {
            html.append(
                    """
                    <tr>
                        <td colspan="6">
                            No hay actividades registradas.
                        </td>
                    </tr>
                    """
            );

        } else {
            for (Actividad actividad : actividades) {
                html.append("<tr>");

                html.append("<td>")
                        .append(actividad.getId())
                        .append("</td>");

                html.append("<td>")
                        .append(
                                escapar(
                                        actividad.getNombre()
                                )
                        )
                        .append("</td>");

                html.append("<td>")
                        .append(
                                escapar(
                                        actividad
                                                .getFechaHora()
                                                .toString()
                                )
                        )
                        .append("</td>");

                html.append("<td>")
                        .append(
                                escapar(
                                        actividad.getLugar()
                                )
                        )
                        .append("</td>");

                html.append("<td>")
                        .append(
                                escapar(
                                        actividad.getTipo()
                                )
                        )
                        .append("</td>");

                html.append(
                        "<td>"
                        + "<form action='/eliminar-actividad' "
                        + "method='post'>"
                        + "<input type='hidden' "
                        + "name='id' value='"
                );

                html.append(
                        actividad.getId()
                );

                html.append(
                        "'>"
                        + "<button class='eliminar' "
                        + "type='submit'>"
                        + "Eliminar actividad"
                        + "</button>"
                        + "</form>"
                        + "</td>"
                        + "</tr>"
                );
            }
        }

        html.append(
                """
                        </tbody>
                    </table>
                </section>
                """
        );
    }

    private static Map<String, String> leerFormulario(
            HttpExchange intercambio
    ) throws IOException {

        String cuerpo =
                new String(
                        intercambio
                                .getRequestBody()
                                .readAllBytes(),
                        StandardCharsets.UTF_8
                );

        Map<String, String> datos =
                new HashMap<>();

        if (cuerpo.isBlank()) {
            return datos;
        }

        for (String campo : cuerpo.split("&")) {
            String[] partes =
                    campo.split("=", 2);

            String nombre =
                    URLDecoder.decode(
                            partes[0],
                            StandardCharsets.UTF_8
                    );

            String valor =
                    partes.length == 2
                    ? URLDecoder.decode(
                            partes[1],
                            StandardCharsets.UTF_8
                    )
                    : "";

            datos.put(
                    nombre,
                    valor.trim()
            );
        }

        return datos;
    }

    private static void validar(
            Map<String, String> datos,
            String... campos
    ) {
        for (String campo : campos) {
            if (
                    !datos.containsKey(campo)
                    || datos.get(campo).isBlank()
            ) {
                throw new IllegalArgumentException(
                        "Falta el campo: " + campo
                );
            }
        }
    }

    private static void redireccionar(
            HttpExchange intercambio,
            String destino
    ) throws IOException {

        intercambio
                .getResponseHeaders()
                .set("Location", destino);

        intercambio.sendResponseHeaders(
                303,
                -1
        );

        intercambio.close();
    }

    private static void enviar(
            HttpExchange intercambio,
            int codigo,
            String contenido
    ) throws IOException {

        byte[] respuesta =
                contenido.getBytes(
                        StandardCharsets.UTF_8
                );

        intercambio.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8"
        );

        intercambio.sendResponseHeaders(
                codigo,
                respuesta.length
        );

        intercambio
                .getResponseBody()
                .write(respuesta);

        intercambio.close();
    }

    private static String escapar(String texto) {
        if (texto == null) {
            return "";
        }

        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}