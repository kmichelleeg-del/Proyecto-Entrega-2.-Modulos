public class Main {

    public static void main(String[] args) {

        Club club = new Club(
                1,
                "Voces del Valle",
                "Club universitario de coro",
                "Viernes de 14:00 a 16:00",
                "Salón de música",
                "Arte y cultura"
        );

        Coordinador coordinador = new Coordinador(
                1,
                "Nataly",
                "nataly@uvg.edu.gt",
                "250001",
                "Presidenta"
        );

        Estudiante estudiante1 = new Estudiante(
                2,
                "Adrián Garzaro",
                "adrian@uvg.edu.gt",
                "260001",
                EstadoMiembro.ACTIVO
        );

        Estudiante estudiante2 = new Estudiante(
                3,
                "Antonio Monroy",
                "antonio@uvg.edu.gt",
                "260002",
                EstadoMiembro.ACTIVO
        );

        Estudiante estudiante3 = new Estudiante(
                4,
                "Sofía",
                "sofia@uvg.edu.gt",
                "260003",
                EstadoMiembro.ACTIVO
        );

        VistaClub vista = new VistaClub();

        ControladorClub controlador =
                new ControladorClub(
                        club,
                        coordinador,
                        vista
                );

        controlador.agregarEstudiante(estudiante1);
        controlador.agregarEstudiante(estudiante2);
        controlador.agregarEstudiante(estudiante3);

        controlador.iniciar();
    }
}