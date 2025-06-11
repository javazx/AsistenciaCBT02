package com.cbt02.attendanceapp.data;

import android.provider.BaseColumns;

public final class DatabaseContract {
    private DatabaseContract() {}

    // Tabla de Estudiantes
    public static class StudentEntry implements BaseColumns {
        public static final String TABLE_NAME = "students";
        public static final String COLUMN_NAME_FULL_NAME = "full_name";
        public static final String COLUMN_NAME_GRADE = "grade";
        public static final String COLUMN_NAME_GROUP = "group_name";
    }

    // Tabla de Asistencias
    public static class AttendanceEntry implements BaseColumns {
        public static final String TABLE_NAME = "attendance";
        public static final String COLUMN_NAME_STUDENT_ID = "student_id";
        public static final String COLUMN_NAME_DATE = "date"; // Formato YYYY-MM-DD
        public static final String COLUMN_NAME_ENTRY_TIME = "entry_time"; // Formato HH:mm
        public static final String COLUMN_NAME_EXIT_TIME = "exit_time"; // Formato HH:mm
    }
}