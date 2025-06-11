package com.cbt02.attendanceapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.cbt02.attendanceapp.adapters.AttendanceAdapter;
import com.cbt02.attendanceapp.data.DatabaseHelper;
import com.cbt02.attendanceapp.databinding.ActivityTakeAttendanceBinding;
import com.cbt02.attendanceapp.models.Student;
import java.util.List;

public class TakeAttendanceActivity extends AppCompatActivity {

    private ActivityTakeAttendanceBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTakeAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        setupSpinners();

        binding.btnLoadStudents.setOnClickListener(v -> loadStudentsForAttendance());
        binding.recyclerViewAttendance.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSpinners() {
        // Para una app real, estos valores vendrían de la BD o de una configuración
        Integer[] grades = {1, 2, 3, 4, 5, 6};
        String[] groups = {"A", "B", "C", "D", "E"};

        ArrayAdapter<Integer> gradeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grades);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGrade.setAdapter(gradeAdapter);

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGroup.setAdapter(groupAdapter);
    }

    private void loadStudentsForAttendance() {
        Integer selectedGrade = (Integer) binding.spinnerGrade.getSelectedItem();
        String selectedGroup = (String) binding.spinnerGroup.getSelectedItem();

        if (selectedGrade == null || selectedGroup == null) {
            Toast.makeText(this, "Selecciona grado y grupo", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Student> students = dbHelper.getStudentsByGradeAndGroup(selectedGrade, selectedGroup);
        if (students.isEmpty()) {
            Toast.makeText(this, "No hay estudiantes en este grupo", Toast.LENGTH_SHORT).show();
        }

        AttendanceAdapter adapter = new AttendanceAdapter(students, this);
        binding.recyclerViewAttendance.setAdapter(adapter);
    }
}