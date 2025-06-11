package com.cbt02.attendanceapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cbt02.attendanceapp.R;
import com.cbt02.attendanceapp.models.Student;
import java.util.List;

/**
 * Adaptador para mostrar la lista de estudiantes en un RecyclerView.
 * Incluye la lógica para detectar una pulsación larga en un elemento.
 */
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private OnItemLongClickListener longClickListener; // Listener para la pulsación larga

    /**
     * Interfaz para definir el listener de la pulsación larga.
     * La Activity implementará esta interfaz para recibir el evento.
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    /**
     * Método para que la Activity pueda establecer el listener.
     * @param listener La implementación del listener desde la Activity.
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    /**
     * Constructor del adaptador.
     * @param studentList La lista de estudiantes a mostrar.
     */
    public StudentAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.tvStudentName.setText(student.getFullName());
        holder.tvGradeGroup.setText("Grado: " + student.getGrade() + ", Grupo: " + student.getGroup());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    /**
     * ViewHolder que representa cada fila (item) de la lista de estudiantes.
     */
    class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvGradeGroup;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvGradeGroup = itemView.findViewById(R.id.tvGradeGroup);

            // Se configura el listener para la pulsación larga sobre toda la vista del item.
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    int position = getAdapterPosition();
                    // Asegurarse de que la posición es válida antes de invocar al listener
                    if (position != RecyclerView.NO_POSITION) {
                        longClickListener.onItemLongClick(v, position);
                    }
                }
                // Se devuelve 'true' para indicar que el evento ha sido consumido
                // y no se deben procesar otros eventos (como el click normal).
                return true;
            });
        }
    }
}