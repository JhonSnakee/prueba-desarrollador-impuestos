package service;

import db.DBConnection;
import model.Impuesto;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ImpuestoService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public void cargarArchivo(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo));
             Connection conn = DBConnection.getConnection()) {

            String linea;
            int count = 0;

            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(",");
                if (campos.length != 7) continue;

                long sticker = Long.parseLong(campos[0]);
                LocalDate fechaMovimiento = LocalDate.parse(campos[1], formatter);
                LocalDate fechaRecaudo = LocalDate.parse(campos[2], formatter);
                String tipoHorario = campos[3];
                long nroId = Long.parseLong(campos[4]);
                long nroForm = Long.parseLong(campos[5]);
                long valor = Long.parseLong(campos[6]);

                Impuesto impuesto = new Impuesto(sticker, fechaMovimiento, fechaRecaudo, tipoHorario, nroId, nroForm, valor);
                insertarEnBD(conn, impuesto);
                count++;
            }

            System.out.println("✅ Registros cargados: " + count);

        } catch (Exception e) {
            System.out.println("❌ Ocurrió un error al cargar el documento: " + e.getMessage());
        }
    }

    private void insertarEnBD(Connection conn, Impuesto imp) throws SQLException {
        String sql = "INSERT INTO impuestos (sticker, fecha_movimiento, fecha_recaudo, tipo_horario, nro_id, nro_form, valor) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (sticker) DO NOTHING";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, imp.getSticker());
            stmt.setDate(2, java.sql.Date.valueOf(imp.getFechaMovimiento()));
            stmt.setDate(3, java.sql.Date.valueOf(imp.getFechaRecaudo()));
            stmt.setString(4, imp.getTipoHorario());
            stmt.setLong(5, imp.getNroId());
            stmt.setLong(6, imp.getNroForm());
            stmt.setLong(7, imp.getValor());
            stmt.executeUpdate();
        }
    }

    public void consultarPorFecha(String fecha) {
        String sql = "SELECT fecha_movimiento, sticker, nro_id, nro_form, valor FROM impuestos WHERE fecha_movimiento = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(fecha));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.printf("%-15s %-15s %-20s %-15s %-10s%n", "Fecha", "Sticker", "Nro ID", "Formulario", "Valor");
                while (rs.next()) {
                    System.out.printf("%-15s %-15d %-20d %-15d %-10d%n",
                            rs.getDate(1), rs.getLong(2), rs.getLong(3), rs.getLong(4), rs.getLong(5));
                }
            } else {
                System.out.println("❌ No se encontraron registros para la fecha ingresada.");
            }
        } catch (Exception e) {
            System.out.println("❌ Ocurrió un error al generar la búsqueda. Por favor, verifique los datos ingresados.");
        }
    }

    public void consultaPorTipoHorario(String tipo) {
        String sql = "SELECT COUNT(*), SUM(valor) FROM impuestos WHERE tipo_horario = ?";

        if (!tipo.equals("N") && !tipo.equals("A")) {
            System.out.println("❌ Ha ingresado una opción no válida.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int cantidad = rs.getInt(1);
                long suma = rs.getLong(2);

                String titulo = String.format("Consulta consolidada por Cantidad y Valor de los registros Horario: %s", tipo);
                String linea = "----------------------------------------------------------------------------------------";
                String encabezado = String.format("%-45s | %-45s", "Cantidad de registros cargados al sistema", "Suma total de los valores cargados al sistema");
                String valores = String.format("%-45d | %-45d", cantidad, suma);

                System.out.println("\n" + titulo);
                System.out.println(linea);
                System.out.println(encabezado);
                System.out.println(valores);
            } else {
                System.out.println("❌ No se encontraron registros para el tipo de horario seleccionado.");
            }

        } catch (Exception e) {
            System.out.println("❌ Ocurrió un error al generar la búsqueda. Por favor, verifique los datos ingresados.");
        }
    }

    public void buscarPorSticker(long sticker) {
        String sql = "SELECT * FROM impuestos WHERE sticker = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, sticker);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("\nInformación del Sticker: " + sticker);
                System.out.println("-------------------------------------------------------------------------------------------");
                System.out.printf("%-20s %-20s %-20s %-25s %-25s %-15s%n",
                        "Fecha de movimiento", "Tipo de horario", "Fecha de recaudo",
                        "Número de identificación", "Número de formulario", "Valor");
                System.out.printf("%-20s %-20s %-20s %-25d %-25d %-15d%n",
                        rs.getDate("fecha_movimiento"), rs.getString("tipo_horario"), rs.getDate("fecha_recaudo"),
                        rs.getLong("nro_id"), rs.getLong("nro_form"), rs.getLong("valor"));

                Scanner scanner = new Scanner(System.in);
                System.out.print("\n¿Deseas exportar este registro a Excel? (S/N): ");
                String respuesta = scanner.nextLine().trim().toUpperCase();
                if (respuesta.equals("S")) {
                    exportarStickerExcel(sticker);
                }

            } else {
                System.out.println("❌ No se encontró el sticker.");
            }
        } catch (Exception e) {
            System.out.println("❌ Ocurrió un error al generar la búsqueda. Por favor, verifique los datos ingresados.");
        }
    }

    public void exportarStickerExcel(long sticker) {
        String sql = "SELECT * FROM impuestos WHERE sticker = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             Workbook workbook = new HSSFWorkbook()) {

            stmt.setLong(1, sticker);
            ResultSet rs = stmt.executeQuery();

            Sheet sheet = workbook.createSheet("Impuesto");
            Row header = sheet.createRow(0);
            String[] columnas = {"Sticker", "Fecha Movimiento", "Fecha Recaudo", "Tipo Horario",
                    "Nro ID", "Nro Formulario", "Valor"};
            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            int fila = 1;
            while (rs.next()) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(rs.getLong("sticker"));
                row.createCell(1).setCellValue(rs.getDate("fecha_movimiento").toString());
                row.createCell(2).setCellValue(rs.getDate("fecha_recaudo").toString());
                row.createCell(3).setCellValue(rs.getString("tipo_horario"));
                row.createCell(4).setCellValue(rs.getLong("nro_id"));
                row.createCell(5).setCellValue(rs.getLong("nro_form"));
                row.createCell(6).setCellValue(rs.getLong("valor"));
            }

            String nombreArchivo = "sticker_" + sticker + ".xls";
            try (FileOutputStream fileOut = new FileOutputStream(nombreArchivo)) {
                workbook.write(fileOut);
                System.out.println("✅ Archivo exportado: " + nombreArchivo);
            }

        } catch (Exception e) {
            System.out.println("❌ Ocurrió un error al generar el documento: " + e.getMessage());
        }
    }

    public void exportarExcel() {
        String nombreArchivo = "impuestos_exportados.xls";
        String sql = "SELECT * FROM impuestos";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();
             Workbook workbook = new HSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Impuestos");
            Row header = sheet.createRow(0);
            String[] columnas = {"Sticker", "Fecha Movimiento", "Fecha Recaudo", "Tipo Horario",
                    "Nro ID", "Nro Formulario", "Valor"};
            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            int fila = 1;
            while (rs.next()) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(rs.getLong("sticker"));
                row.createCell(1).setCellValue(rs.getDate("fecha_movimiento").toString());
                row.createCell(2).setCellValue(rs.getDate("fecha_recaudo").toString());
                row.createCell(3).setCellValue(rs.getString("tipo_horario"));
                row.createCell(4).setCellValue(rs.getLong("nro_id"));
                row.createCell(5).setCellValue(rs.getLong("nro_form"));
                row.createCell(6).setCellValue(rs.getLong("valor"));
            }

            try (FileOutputStream fileOut = new FileOutputStream(nombreArchivo)) {
                workbook.write(fileOut);
                System.out.println("✅ Archivo exportado: " + nombreArchivo);
            }

        } catch (Exception e) {
            System.out.println("❌ Ocurrió un error al generar el documento: " + e.getMessage());
        }
    }
}