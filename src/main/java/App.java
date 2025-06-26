import service.ImpuestoService;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ImpuestoService service = new ImpuestoService();

        int opcion;
        do {
            System.out.println("\n===== MENÚ IMPUESTOS =====");
            System.out.println("1. Cargar archivo DATOS.TXT");
            System.out.println("2. Consultar por fecha");
            System.out.println("3. Consultar cantidad y suma por tipo de horario (A / N)");
            System.out.println("4. Buscar registro por sticker");
            System.out.println("5. Exportar todos los datos a archivo Excel (.xls)");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // limpiar buffer

            switch (opcion) {
                case 1 -> service.cargarArchivo("src/main/resources/datos.TXT");
                case 2 -> {
                    System.out.print("Ingresa la fecha a consultar en formato (AAAA-MM-DD): ");
                    String fecha = scanner.nextLine();
                    service.consultarPorFecha(fecha);
                }
                case 3 -> {
                    System.out.print("¿Qué tipo de horario deseas consultar? (A/N): ");
                    String tipo = scanner.nextLine().toUpperCase();
                    service.consultaPorTipoHorario(tipo);
                }
                case 4 -> {
                    System.out.print("Ingresa el número de sticker: ");
                    long sticker = scanner.nextLong();
                    service.buscarPorSticker(sticker);
                }
                case 5 -> service.exportarExcel();
                case 0 -> System.out.println("👋 Adiós.");
                default -> System.out.println("❌ Opción no válida.");
            }
        } while (opcion != 0);
    }
}
