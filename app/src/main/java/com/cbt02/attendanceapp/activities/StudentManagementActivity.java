package com.cbt02.attendanceapp.activities;

// Importa lo necesario
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.cbt02.attendanceapp.adapters.StudentAdapter;
import com.cbt02.attendanceapp.data.DatabaseHelper;
import com.cbt02.attendanceapp.databinding.ActivityStudentManagementBinding;
import com.cbt02.attendanceapp.models.Student;
import java.util.ArrayList;
import java.util.List;

public class StudentManagementActivity extends AppCompatActivity {

    private ActivityStudentManagementBinding binding;
    private DatabaseHelper dbHelper;
    private StudentAdapter adapter;
    private List<Student> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Gestión de Estudiantes");

        dbHelper = new DatabaseHelper(this);
        binding.recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));

        binding.fabAddStudent.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditStudentActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
    }

    private void loadStudents() {
        studentList.clear();
        studentList.addAll(dbHelper.getAllStudents());
        if (adapter == null) {
            adapter = new StudentAdapter(studentList);
            setupAdapterListeners(); // Configuramos los listeners aquí
            binding.recyclerViewStudents.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void setupAdapterListeners() {
        adapter.setOnItemLongClickListener((view, position) -> {
            Student selectedStudent = studentList.get(position);
            showOptionsDialog(selectedStudent, position);
        });
    }

    private void showOptionsDialog(final Student student, final int position) {
        CharSequence[] options = {"Modificar", "Eliminar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opciones para " + student.getFullName());
        builder.setItems(options, (dialog, item) -> {
            if (item == 0) { // Modificar
                Intent intent = new Intent(StudentManagementActivity.this, AddEditStudentActivity.class);
                intent.putExtra("STUDENT_ID", student.getId());
                startActivity(intent);
            } else if (item == 1) { // Eliminar
                showDeleteConfirmationDialog(student, position);
            }
        });
        builder.show();
    }

    private void showDeleteConfirmationDialog(final Student student, final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar a " + student.getFullName() + "? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dbHelper.deleteStudent(student.getId());
                    studentList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, studentList.size());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}