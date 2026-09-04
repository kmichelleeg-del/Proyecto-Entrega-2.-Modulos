import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;

public class ControladorClub {

    private Club club;
    private Coordinador coordinador;
    private VistaClub vista;

    private ArrayList<Estudiante> estudiantes;
    private ArrayList<Membresia> membresias;
    private ArrayList<Actividad> actividades;
    private ArrayList<Asistencia> asistencias;
    private ArrayList<Notificacion> notificaciones;
    private ArrayList<Integer> faltas;

    private ReglaInactividad regla;

    private int siguienteActividad;
    private int siguienteAsistencia;
    private int siguienteNotificacion;

    public ControladorClub(
            Club club,
            Coordinador coordinador,
            VistaClub vista
    ) {
        this.club = club;
        this.coordinador = coordinador;
        this.vista = vista;

        estudiantes = new ArrayList<Estudiante>();
        membresias = new ArrayList<Membresia>();
        actividades = new ArrayList<Actividad>();
        asistencias = new ArrayList<Asistencia>();
        notificaciones = new ArrayList<Notificacion>();
        faltas = new ArrayList<Integer>();

        regla = new ReglaInactividad(3, 30, 7);

        siguienteActividad = 1;
        siguienteAsistencia = 1;
        siguienteNotificacion = 1;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Coordinador getCoordinador() {
        return coordinador;
    }

    public void setCoordinador(Coordinador coordinador) {
        this.coordinador = coordinador;
    }

    public VistaClub getVista() {
        return vista;
    }

    public void setVista(VistaClub vista) {
        this.vista = vista;
    }

    public ArrayList<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public ArrayList<Membresia> getMembresias() {
        return membresias;
    }

    public ArrayList<Actividad> getActividades() {
        return actividades;
    }

    public ArrayList<Asistencia> getAsistencias() {
        return asistencias;
    }

    public ArrayList<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    public ReglaInactividad getRegla() {
        return regla;
    }

    public void setRegla(ReglaInactividad regla) {
        this.regla = regla;
    }

    public void agregarEstudiante(Estudiante estudiante) {
        estudiantes.add(estudiante);

        Membresia membresia = new Membresia(
                new Date(),
                EstadoMiembro.ACTIVO,
                0,
                null
        );

        membresias.add(membresia);
        faltas.add(0);
    }

    public void iniciar() {
        int opcion;

        do {
            opcion = vista.mostrarMenu();

            switch (opcion) {
                case 1:
                    mostrarMiembros();
                    break;

                case 2:
                    crearActividad();
                    break;

                case 3:
                    mostrarActividades();
                    break;

                case 4:
                    registrarAsistencia();
                    break;

                case 5:
                    evaluarInactividad();
                    break;

                case 6:
                    notificarActividad();
                    break;

                case 7:
                    confirmarContinuidad();
                    break;

                case 8:
                    configurarRegla();
                    break;

                case 9:
                    mostrarNotificaciones();
                    break;

                case 0:
                    vista.mostrarMensaje("Programa finalizado.");
                    break;

                default:
                    vista.mostrarMensaje("Opción inválida.");
                    break;
            }

        } while (opcion != 0);
    }

    public void mostrarMiembros() {
        System.out.println("\n--- MIEMBROS DEL CLUB ---");

        if (estudiantes.isEmpty()) {
            vista.mostrarMensaje("No hay miembros registrados.");
            return;
        }

        for (int i = 0; i < estudiantes.size(); i++) {
            Estudiante estudiante = estudiantes.get(i);
            Membresia membresia = membresias.get(i);

            System.out.println(
                    "ID: " + estudiante.getId()
                    + " | Nombre: " + estudiante.getNombre()
                    + " | Carné: " + estudiante.getCarne()
                    + " | Estado: " + membresia.getEstado()
                    + " | Asistencias: "
                    + membresia.getTotalAsistencias()
                    + " | Faltas: " + faltas.get(i)
            );
        }
    }

    public void crearActividad() {
        String nombre =
                vista.leerTexto("Nombre de la actividad: ");

        String fechaTexto =
                vista.leerTexto(
                        "Fecha y hora (dd/MM/yyyy HH:mm): "
                );

        String lugar =
                vista.leerTexto("Lugar: ");

        String tipo =
                vista.leerTexto("Tipo de actividad: ");

        try {
            DateTimeFormatter formato =
                    DateTimeFormatter.ofPattern(
                            "dd/MM/yyyy HH:mm"
                    );

            LocalDateTime fechaHora =
                    LocalDateTime.parse(fechaTexto, formato);

            Actividad actividad = new Actividad(
                    siguienteActividad,
                    nombre,
                    fechaHora,
                    lugar,
                    tipo
            );

            actividades.add(actividad);
            siguienteActividad++;

            vista.mostrarMensaje(
                    "Actividad creada correctamente."
            );

        } catch (DateTimeParseException error) {
            vista.mostrarMensaje(
                    "Formato incorrecto. Ejemplo: 15/09/2026 14:30"
            );
        }
    }

    public void mostrarActividades() {
        System.out.println("\n--- ACTIVIDADES ---");

        if (actividades.isEmpty()) {
            vista.mostrarMensaje(
                    "No hay actividades registradas."
            );
            return;
        }

        for (Actividad actividad : actividades) {
            System.out.println(
                    "ID: " + actividad.getId()
                    + " | Nombre: " + actividad.getNombre()
                    + " | Fecha: " + actividad.getFechaHora()
                    + " | Lugar: " + actividad.getLugar()
                    + " | Tipo: " + actividad.getTipo()
            );
        }
    }

    public void registrarAsistencia() {
        if (actividades.isEmpty()) {
            vista.mostrarMensaje(
                    "Primero debe crear una actividad."
            );
            return;
        }

        mostrarActividades();

        int idActividad =
                vista.leerEntero("ID de la actividad: ");

        Actividad actividad =
                buscarActividad(idActividad);

        if (actividad == null) {
            vista.mostrarMensaje(
                    "La actividad no existe."
            );
            return;
        }

        mostrarMiembros();

        int idEstudiante =
                vista.leerEntero("ID del estudiante: ");

        int posicion =
                buscarPosicionEstudiante(idEstudiante);

        if (posicion == -1) {
            vista.mostrarMensaje(
                    "El estudiante no existe."
            );
            return;
        }

        Membresia membresia =
                membresias.get(posicion);

        if (membresia.getEstado()
                == EstadoMiembro.RETIRADO) {

            vista.mostrarMensaje(
                    "El estudiante está retirado."
            );
            return;
        }

        boolean presente =
                vista.leerSiNo(
                        "¿El estudiante estuvo presente?"
                );

        String observacion =
                vista.leerTexto("Observación: ");

        Asistencia asistencia = new Asistencia(
                siguienteAsistencia,
                LocalDateTime.now(),
                presente,
                observacion
        );

        asistencias.add(asistencia);
        siguienteAsistencia++;

        if (presente) {
            int total =
                    membresia.getTotalAsistencias();

            membresia.setTotalAsistencias(total + 1);
            membresia.setUltimaAsistencia(new Date());
            membresia.setEstado(EstadoMiembro.ACTIVO);

            estudiantes.get(posicion).setEstado(
                    EstadoMiembro.ACTIVO
            );

            vista.mostrarMensaje(
                    "Asistencia registrada correctamente."
            );

        } else {
            int totalFaltas = faltas.get(posicion);
            faltas.set(posicion, totalFaltas + 1);

            vista.mostrarMensaje(
                    "Falta registrada correctamente."
            );
        }
    }

    public void evaluarInactividad() {
        int cantidadInactivos = 0;

        for (int i = 0; i < estudiantes.size(); i++) {
            Membresia membresia = membresias.get(i);

            if (membresia.getEstado()
                    == EstadoMiembro.ACTIVO
                    && faltas.get(i)
                    >= regla.getMaximoFaltas()) {

                membresia.setEstado(
                        EstadoMiembro.PENDIENTE_CONFIRMACION
                );

                estudiantes.get(i).setEstado(
                        EstadoMiembro.PENDIENTE_CONFIRMACION
                );

                crearNotificacion(
                        "Confirmación de continuidad",
                        "El estudiante "
                        + estudiantes.get(i).getNombre()
                        + " debe confirmar si desea continuar.",
                        TipoNotificacion.INACTIVIDAD
                );

                cantidadInactivos++;
            }
        }

        vista.mostrarMensaje(
                "Evaluación finalizada. Miembros pendientes: "
                + cantidadInactivos
        );
    }

    public void notificarActividad() {
        if (actividades.isEmpty()) {
            vista.mostrarMensaje(
                    "No hay actividades registradas."
            );
            return;
        }

        mostrarActividades();

        int idActividad =
                vista.leerEntero("ID de la actividad: ");

        Actividad actividad =
                buscarActividad(idActividad);

        if (actividad == null) {
            vista.mostrarMensaje(
                    "La actividad no existe."
            );
            return;
        }

        int cantidadEnviada = 0;

        for (int i = 0; i < estudiantes.size(); i++) {
            Membresia membresia = membresias.get(i);

            /*
             * Las notificaciones de actividades solo se
             * envían a miembros activos.
             */
            if (membresia.getEstado()
                    == EstadoMiembro.ACTIVO) {

                crearNotificacion(
                        "Nueva actividad",
                        "Para "
                        + estudiantes.get(i).getNombre()
                        + ": " + actividad.getNombre()
                        + ", fecha "
                        + actividad.getFechaHora()
                        + ", lugar "
                        + actividad.getLugar(),
                        TipoNotificacion.ACTIVIDAD
                );

                cantidadEnviada++;
            }
        }

        vista.mostrarMensaje(
                "Notificación enviada a "
                + cantidadEnviada
                + " miembros activos."
        );
    }

    public void confirmarContinuidad() {
        mostrarMiembros();

        int idEstudiante =
                vista.leerEntero("ID del estudiante: ");

        int posicion =
                buscarPosicionEstudiante(idEstudiante);

        if (posicion == -1) {
            vista.mostrarMensaje(
                    "El estudiante no existe."
            );
            return;
        }

        Membresia membresia =
                membresias.get(posicion);

        if (membresia.getEstado()
                != EstadoMiembro.PENDIENTE_CONFIRMACION) {

            vista.mostrarMensaje(
                    "El estudiante no tiene una confirmación pendiente."
            );
            return;
        }

        boolean deseaContinuar =
                vista.leerSiNo(
                        "¿Desea continuar en el club?"
                );

        if (deseaContinuar) {
            membresia.setEstado(EstadoMiembro.ACTIVO);

            estudiantes.get(posicion).setEstado(
                    EstadoMiembro.ACTIVO
            );

            faltas.set(posicion, 0);

            crearNotificacion(
                    "Continuidad confirmada",
                    estudiantes.get(posicion).getNombre()
                    + " continúa como miembro activo.",
                    TipoNotificacion.CONFIRMACION
            );

            vista.mostrarMensaje(
                    "El estudiante continúa activo."
            );

        } else {
            membresia.setEstado(EstadoMiembro.RETIRADO);

            estudiantes.get(posicion).setEstado(
                    EstadoMiembro.RETIRADO
            );

            crearNotificacion(
                    "Retiro del club",
                    estudiantes.get(posicion).getNombre()
                    + " fue retirado del club.",
                    TipoNotificacion.CONFIRMACION
            );

            vista.mostrarMensaje(
                    "El estudiante fue retirado."
            );
        }
    }

    public void configurarRegla() {
        int maximoFaltas =
                vista.leerEntero(
                        "Nuevo máximo de faltas: "
                );

        int diasSinParticipar =
                vista.leerEntero(
                        "Máximo de días sin participar: "
                );

        int plazoConfirmacion =
                vista.leerEntero(
                        "Días para confirmar continuidad: "
                );

        if (maximoFaltas <= 0
                || diasSinParticipar <= 0
                || plazoConfirmacion <= 0) {

            vista.mostrarMensaje(
                    "Los valores deben ser mayores que cero."
            );
            return;
        }

        regla.setMaximoFaltas(maximoFaltas);
        regla.setDiasSinParticipar(diasSinParticipar);
        regla.setPlazoConfirmacion(plazoConfirmacion);

        vista.mostrarMensaje(
                "Regla actualizada correctamente."
        );
    }

    public void mostrarNotificaciones() {
        System.out.println("\n--- NOTIFICACIONES ---");

        if (notificaciones.isEmpty()) {
            vista.mostrarMensaje(
                    "No hay notificaciones registradas."
            );
            return;
        }

        for (Notificacion notificacion : notificaciones) {
            System.out.println(
                    "ID: " + notificacion.getId()
                    + " | Título: "
                    + notificacion.getTitulo()
                    + " | Mensaje: "
                    + notificacion.getMensaje()
                    + " | Tipo: "
                    + notificacion.getTipo()
                    + " | Leída: "
                    + notificacion.getLeida()
            );
        }
    }

    private void crearNotificacion(
            String titulo,
            String mensaje,
            TipoNotificacion tipo
    ) {
        Notificacion notificacion =
                new Notificacion(
                        siguienteNotificacion,
                        titulo,
                        mensaje,
                        LocalDateTime.now(),
                        tipo,
                        false
                );

        notificacion.enviar();
        notificaciones.add(notificacion);
        siguienteNotificacion++;
    }

    private int buscarPosicionEstudiante(int id) {
        for (int i = 0; i < estudiantes.size(); i++) {
            if (estudiantes.get(i).getId() == id) {
                return i;
            }
        }

        return -1;
    }

    private Actividad buscarActividad(int id) {
        for (Actividad actividad : actividades) {
            if (actividad.getId() == id) {
                return actividad;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "ControladorClub{"
                + "club=" + club.getNombre()
                + ", coordinador="
                + coordinador.getNombre()
                + ", miembros="
                + estudiantes.size()
                + '}';
    }
}