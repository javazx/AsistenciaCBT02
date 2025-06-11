package com.cbt02.attendanceapp.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.cbt02.attendanceapp.data.DatabaseHelper;
import com.cbt02.attendanceapp.databinding.ActivityAddEditStudentBinding;
import com.cbt02.attendanceapp.models.Student;

public class AddEditStudentActivity extends AppCompatActivity {

    private ActivityAddEditStudentBinding binding;
    private DatabaseHelper dbHelper;
    private boolean isEditMode = false;
    private long studentId;
    private Student currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        // Comprobar si se pasó un ID para modo de edición
        if (getIntent().hasExtra("STUDENT_ID")) {
            studentId = getIntent().getLongExtra("STUDENT_ID", -1);
            if (studentId != -1) {
                isEditMode = true;
                loadStudentData();
                setTitle("Modificar Estudiante");
            }
        } else {
            setTitle("Agregar Estudiante");
        }

        binding.btnSaveStudent.setOnClickListener(v -> saveStudent());
    }

    private void loadStudentData() {
        currentStudent = dbHelper.getStudentById(studentId);
        if (currentStudent != null) {
            binding.etFullName.setText(currentStudent.getFullName());
            binding.etGrade.setText(String.valueOf(currentStudent.getGrade()));
            binding.etGroup.setText(currentStudent.getGroup());
        }
    }

    private void saveStudent() {
        String fullName = binding.etFullName.getText().toString().trim();
        String gradeStr = binding.etGrade.getText().toString().trim();
        String group = binding.etGroup.getText().toString().trim().toUpperCase();

        if (fullName.isEmpty() || gradeStr.isEmpty() || group.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int grade = Integer.parseInt(gradeStr);

        if (isEditMode) {
            // Modo Modificar
            Student updatedStudent = new Student(studentId, fullName, grade, group);
            int result = dbHelper.updateStudent(updatedStudent);
            if (result > 0) {
                Toast.makeText(this, "Estudiante actualizado con éxito", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Modo Agregar
            Student newStudent = new Student(0, fullName, grade, group);
            long id = dbHelper.addStudent(newStudent);
            if (id != -1) {
                Toast.makeText(this, "Estudiante guardado con éxito", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        }
    }
}