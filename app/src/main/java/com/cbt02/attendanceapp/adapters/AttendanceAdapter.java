package com.cbt02.attendanceapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cbt02.attendanceapp.R;
import com.cbt02.attendanceapp.data.DatabaseHelper;
import com.cbt02.attendanceapp.models.AttendanceRecord;
import com.cbt02.attendanceapp.models.Student;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador para la lista de toma de asistencia.
 * Muestra a los estudiantes y permite registrar su hora de entrada y salida.
 */
public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private List<Student> studentList;
    private Context context;
    private DatabaseHelper dbHelper;

    public AttendanceAdapter(List<Student> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.tvStudentName.setText(student.getFullName());

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Carga el estado de asistencia actual al mostrar la fila
        updateAttendanceStatus(holder, student.getId(), currentDate);

        holder.btnEntry.setOnClickListener(v -> {
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            dbHelper.addOrUpdateAttendance(student.getId(), currentDate, currentTime, null);
            Toast.makeText(context, "Entrada registrada para " + student.getFullName(), Toast.LENGTH_SHORT).show();
            // Actualiza la UI de inmediato
            updateAttendanceStatus(holder, student.getId(), currentDate);
        });

        holder.btnExit.setOnClickListener(v -> {
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            dbHelper.addOrUpdateAttendance(student.getId(), currentDate, null, currentTime);
            Toast.makeText(context, "Salida registrada para " + student.getFullName(), Toast.LENGTH_SHORT).show();
            // Actualiza la UI de inmediato
            updateAttendanceStatus(holder, student.getId(), currentDate);
        });
    }

    /**
     * Consulta la base de datos para obtener la asistencia del día y actualiza los TextViews y botones.
     * @param holder El ViewHolder de la fila actual.
     * @param studentId El ID del estudiante.
     * @param date La fecha actual.
     */
    private void updateAttendanceStatus(AttendanceViewHolder holder, long studentId, String date) {
        AttendanceRecord record = dbHelper.getAttendanceForStudentOnDate(studentId, date);

        // Resetea el estado por defecto
        holder.tvEntryTime.setText("--:--");
        holder.btnEntry.setEnabled(true);
        holder.tvExitTime.setText("--:--");
        holder.btnExit.setEnabled(true);

        if (record != null) {
            String entryTime = record.getEntryTime();
            String exitTime = record.getExitTime();

            if (entryTime != null && !entryTime.isEmpty()) {
                holder.tvEntryTime.setText(entryTime);
                holder.btnEntry.setEnabled(false); // Deshabilita el botón si ya hay registro
            }

            if (exitTime != null && !exitTime.isEmpty()) {
                holder.tvExitTime.setText(exitTime);
                holder.btnExit.setEnabled(false); // Deshabilita el botón si ya hay registro
            }
        }
    }


    @Override
    public int getItemCount() {
        return studentList.size();
    }

    /**
     * ViewHolder que contiene las vistas para cada fila de la lista de asistencia.
     */
    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvEntryTime, tvExitTime;
        Button btnEntry, btnExit;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentNameAttendance);
            btnEntry = itemView.findViewById(R.id.btnEntry);
            btnExit = itemView.findViewById(R.id.btnExit);
            tvEntryTime = itemView.findViewById(R.id.tvEntryTime);
            tvExitTime = itemView.findViewById(R.id.tvExitTime);
        }
    }
}