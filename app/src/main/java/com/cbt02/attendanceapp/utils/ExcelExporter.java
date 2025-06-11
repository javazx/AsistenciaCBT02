package com.cbt02.attendanceapp.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.cbt02.attendanceapp.data.DatabaseHelper;
import com.cbt02.attendanceapp.models.AttendanceRecord;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExcelExporter {

    public static void exportToExcel(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        List<AttendanceRecord> records = dbHelper.getAttendanceForExport();

        if (records.isEmpty()) {
            Toast.makeText(context, "No hay registros de asistencia para exportar.", Toast.LENGTH_SHORT).show();
            return;
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Asistencias CBT02");

        // Crear la fila de encabezado
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Nombre Estudiante", "Grado", "Grupo", "Fecha", "Hora Entrada", "Hora Salida"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Llenar los datos
        int rowNum = 1;
        for (AttendanceRecord record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getStudentName());
            row.createCell(1).setCellValue(record.getGrade());
            row.createCell(2).setCellValue(record.getGroup());
            row.createCell(3).setCellValue(record.getDate());
            row.createCell(4).setCellValue(record.getEntryTime());
            row.createCell(5).setCellValue(record.getExitTime());
        }

        // --- LÓGICA DE GUARDADO ACTUALIZADA ---
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Asistencia_CBT02_" + timeStamp + ".xlsx";

        try {
            // Para Android 10 (API 29) y superior, usamos MediaStore
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                if (uri != null) {
                    try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                        workbook.write(outputStream);
                    }
                    Toast.makeText(context, "Exportado con éxito a Descargas", Toast.LENGTH_LONG).show();
                }
            } else {
                // Para versiones anteriores a Android 10, usamos el método antiguo
                // Esto requiere el permiso de escritura que ya solicitamos en MainActivity
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(path, fileName);
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                Toast.makeText(context, "Exportado a " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al exportar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}