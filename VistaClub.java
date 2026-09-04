import java.util.Scanner;

public class VistaClub {

    private Scanner teclado;

    public VistaClub() {
        teclado = new Scanner(System.in);
    }

    public int mostrarMenu() {
        System.out.println();
        System.out.println("==============================");
        System.out.println("     GESTIÓN DE CLUBES");
        System.out.println("==============================");
        System.out.println("1. Mostrar miembros");
        System.out.println("2. Crear actividad");
        System.out.println("3. Mostrar actividades");
        System.out.println("4. Registrar asistencia");
        System.out.println("5. Evaluar inactividad");
        System.out.println("6. Notificar actividad");
        System.out.println("7. Confirmar continuidad");
        System.out.println("8. Configurar máximo de faltas");
        System.out.println("9. Mostrar notificaciones");
        System.out.println("0. Salir");

        return leerEntero("Seleccione una opción: ");
    }

    public int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(teclado.nextLine());

            } catch (NumberFormatException error) {
                System.out.println("Debe ingresar un número.");
            }
        }
    }

    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return teclado.nextLine();
    }

    public boolean leerSiNo(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (s/n): ");
            String respuesta = teclado.nextLine();

            if (respuesta.equalsIgnoreCase("s")) {
                return true;
            }

            if (respuesta.equalsIgnoreCase("n")) {
                return false;
            }

            System.out.println("Escriba s o n.");
        }
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    @Override
    public String toString() {
        return "Vista del sistema de clubes";
    }
}