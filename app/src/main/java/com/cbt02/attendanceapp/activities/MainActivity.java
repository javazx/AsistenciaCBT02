package com.cbt02.attendanceapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cbt02.attendanceapp.databinding.ActivityMainBinding;
import com.cbt02.attendanceapp.utils.ExcelExporter;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnManageStudents.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentManagementActivity.class));
        });

        binding.btnTakeAttendance.setOnClickListener(v -> {
            startActivity(new Intent(this, TakeAttendanceActivity.class));
        });

        binding.btnExportToExcel.setOnClickListener(v -> {
            checkAndRequestStoragePermission();
        });
    }

    private void checkAndRequestStoragePermission() {
        // En Android 10 (Q) y superior, no necesitamos permiso de escritura para MediaStore.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            exportData();
            return;
        }

        // Para Android 9 (P) y inferior, sí necesitamos el permiso de escritura.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Si el permiso ya está concedido, exportamos.
            exportData();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Si el usuario ya lo denegó una vez, le mostramos una explicación.
            new AlertDialog.Builder(this)
                    .setTitle("Permiso Necesario")
                    .setMessage("Se necesita permiso para guardar el archivo de Excel en su dispositivo.")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        requestPermission();
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            // Si es la primera vez que se pide, o si el usuario marcó "No volver a preguntar".
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el usuario concede el permiso desde el diálogo.
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                exportData();
            } else {
                // Si el usuario deniega el permiso.
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                // Verificamos si marcó "No volver a preguntar".
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showSettingsDialog();
                }
            }
        }
    }

    /**
     * Muestra un diálogo que guía al usuario a la configuración de la app
     * para que pueda habilitar el permiso manualmente.
     */
    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permiso Denegado Permanentemente")
                .setMessage("Ha denegado el permiso de almacenamiento de forma permanente. Para exportar a Excel, debe ir a la configuración de la aplicación y habilitarlo manualmente.")
                .setPositiveButton("Ir a Configuración", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    private void exportData() {
        Toast.makeText(this, "Iniciando exportación...", Toast.LENGTH_SHORT).show();
        ExcelExporter.exportToExcel(this);
    }
}