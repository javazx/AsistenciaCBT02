package com.cbt02.attendanceapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.cbt02.attendanceapp.models.AttendanceRecord;
import com.cbt02.attendanceapp.models.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona toda la interacción con la base de datos SQLite de la aplicación.
 * Incluye la creación de tablas y las operaciones CRUD para estudiantes y asistencias.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CBT02_Attendance.db";

    private static final String SQL_CREATE_STUDENTS =
            "CREATE TABLE " + DatabaseContract.StudentEntry.TABLE_NAME + " (" +
                    DatabaseContract.StudentEntry._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.StudentEntry.COLUMN_NAME_FULL_NAME + " TEXT," +
                    DatabaseContract.StudentEntry.COLUMN_NAME_GRADE + " INTEGER," +
                    DatabaseContract.StudentEntry.COLUMN_NAME_GROUP + " TEXT)";

    private static final String SQL_CREATE_ATTENDANCE =
            "CREATE TABLE " + DatabaseContract.AttendanceEntry.TABLE_NAME + " (" +
                    DatabaseContract.AttendanceEntry._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.AttendanceEntry.COLUMN_NAME_STUDENT_ID + " INTEGER," +
                    DatabaseContract.AttendanceEntry.COLUMN_NAME_DATE + " TEXT," +
                    DatabaseContract.AttendanceEntry.COLUMN_NAME_ENTRY_TIME + " TEXT," +
                    DatabaseContract.AttendanceEntry.COLUMN_NAME_EXIT_TIME + " TEXT," +
                    "FOREIGN KEY(" + DatabaseContract.AttendanceEntry.COLUMN_NAME_STUDENT_ID + ") REFERENCES " +
                    DatabaseContract.StudentEntry.TABLE_NAME + "(" + DatabaseContract.StudentEntry._ID + "))";

    private static final String SQL_DELETE_STUDENTS = "DROP TABLE IF EXISTS " + DatabaseContract.StudentEntry.TABLE_NAME;
    private static final String SQL_DELETE_ATTENDANCE = "DROP TABLE IF EXISTS " + DatabaseContract.AttendanceEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STUDENTS);
        db.execSQL(SQL_CREATE_ATTENDANCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_STUDENTS);
        db.execSQL(SQL_DELETE_ATTENDANCE);
        onCreate(db);
    }

    // --- Métodos CRUD para Estudiantes ---

    /**
     * Agrega un nuevo estudiante a la base de datos.
     * @param student El objeto Student a agregar.
     * @return el ID del nuevo estudiante insertado.
     */
    public long addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.StudentEntry.COLUMN_NAME_FULL_NAME, student.getFullName());
        values.put(DatabaseContract.StudentEntry.COLUMN_NAME_GRADE, student.getGrade());
        values.put(DatabaseContract.StudentEntry.COLUMN_NAME_GROUP, student.getGroup());
        long id = db.insert(DatabaseContract.StudentEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    /**
     * Obtiene un estudiante específico por su ID.
     * @param id El ID del estudiante a buscar.
     * @return el objeto Student si se encuentra, de lo contrario null.
     */
    public Student getStudentById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.StudentEntry.TABLE_NAME, null,
                DatabaseContract.StudentEntry._ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        Student student = null;
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry._ID);
            int nameIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry.COLUMN_NAME_FULL_NAME);
            int gradeIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry.COLUMN_NAME_GRADE);
            int groupIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry.COLUMN_NAME_GROUP);

            if (idIndex != -1 && nameIndex != -1 && gradeIndex != -1 && groupIndex != -1) {
                student = new Student(
                        cursor.getLong(idIndex),
                        cursor.getString(nameIndex),
                        cursor.getInt(gradeIndex),
                        cursor.getString(groupIndex)
                );
            }
            cursor.close();
        }
        db.close();
        return student;
    }


    /**
     * Devuelve una lista con todos los estudiantes.
     * @return Lista de todos los objetos Student.
     */
    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseContract.StudentEntry.TABLE_NAME + " ORDER BY " + DatabaseContract.StudentEntry.COLUMN_NAME_FULL_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry._ID);
                int nameIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry.COLUMN_NAME_FULL_NAME);
                int gradeIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry.COLUMN_NAME_GRADE);
                int groupIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry.COLUMN_NAME_GROUP);

                if (idIndex != -1 && nameIndex != -1 && gradeIndex != -1 && groupIndex != -1) {
                    Student student = new Student(
                            cursor.getLong(idIndex),
                            cursor.getString(nameIndex),
                            cursor.getInt(gradeIndex),
                            cursor.getString(groupIndex)
                    );
                    studentList.add(student);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return studentList;
    }

    /**
     * Devuelve una lista de estudiantes filtrados por grado y grupo.
     * @param grade El grado a filtrar.
     * @param group El grupo a filtrar.
     * @return Lista de estudiantes que coinciden con el filtro.
     */
    public List<Student> getStudentsByGradeAndGroup(int grade, String group) {
        List<Student> studentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = DatabaseContract.StudentEntry.COLUMN_NAME_GRADE + " = ? AND " + DatabaseContract.StudentEntry.COLUMN_NAME_GROUP + " = ?";
        String[] selectionArgs = { String.valueOf(grade), group };

        Cursor cursor = db.query(
                DatabaseContract.StudentEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, DatabaseContract.StudentEntry.COLUMN_NAME_FULL_NAME);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry._ID);
                int nameIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry.COLUMN_NAME_FULL_NAME);
                int gradeIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry.COLUMN_NAME_GRADE);
                int groupIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry.COLUMN_NAME_GROUP);

                if (idIndex != -1 && nameIndex != -1 && gradeIndex != -1 && groupIndex != -1) {
                    Student student = new Student(
                            cursor.getLong(idIndex),
                            cursor.getString(nameIndex),
                            cursor.getInt(gradeIndex),
                            cursor.getString(groupIndex)
                    );
                    studentList.add(student);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return studentList;
    }

    /**
     * Actualiza los datos de un estudiante existente.
     * @param student El objeto Student con los datos actualizados.
     * @return el número de filas afectadas (debería ser 1).
     */
    public int updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.StudentEntry.COLUMN_NAME_FULL_NAME, student.getFullName());
        values.put(DatabaseContract.StudentEntry.COLUMN_NAME_GRADE, student.getGrade());
        values.put(DatabaseContract.StudentEntry.COLUMN_NAME_GROUP, student.getGroup());

        int result = db.update(DatabaseContract.StudentEntry.TABLE_NAME, values,
                DatabaseContract.StudentEntry._ID + " = ?",
                new String[]{String.valueOf(student.getId())});
        db.close();
        return result;
    }

    /**
     * Elimina un estudiante y todos sus registros de asistencia asociados.
     * @param id El ID del estudiante a eliminar.
     */
    public void deleteStudent(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Primero, eliminamos los registros de asistencia para mantener la integridad de la base de datos.
        db.delete(DatabaseContract.AttendanceEntry.TABLE_NAME,
                DatabaseContract.AttendanceEntry.COLUMN_NAME_STUDENT_ID + " = ?",
                new String[]{String.valueOf(id)});
        // Después, eliminamos al estudiante.
        db.delete(DatabaseContract.StudentEntry.TABLE_NAME,
                DatabaseContract.StudentEntry._ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }


    // --- Métodos para Asistencia ---

    /**
     * Agrega o actualiza un registro de asistencia para un estudiante en una fecha específica.
     * @param studentId El ID del estudiante.
     * @param date La fecha del registro (YYYY-MM-DD).
     * @param entryTime La hora de entrada (HH:mm) o null si no se modifica.
     * @param exitTime La hora de salida (HH:mm) o null si no se modifica.
     */
    public void addOrUpdateAttendance(long studentId, String date, String entryTime, String exitTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "SELECT * FROM " + DatabaseContract.AttendanceEntry.TABLE_NAME +
                " WHERE " + DatabaseContract.AttendanceEntry.COLUMN_NAME_STUDENT_ID + " = ? AND " +
                DatabaseContract.AttendanceEntry.COLUMN_NAME_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), date});

        if (cursor.moveToFirst()) { // Si existe, actualiza
            int idIndex = cursor.getColumnIndex(DatabaseContract.AttendanceEntry._ID);
            if (idIndex != -1) {
                long attendanceId = cursor.getLong(idIndex);
                if (entryTime != null) values.put(DatabaseContract.AttendanceEntry.COLUMN_NAME_ENTRY_TIME, entryTime);
                if (exitTime != null) values.put(DatabaseContract.AttendanceEntry.COLUMN_NAME_EXIT_TIME, exitTime);
                db.update(DatabaseContract.AttendanceEntry.TABLE_NAME, values, DatabaseContract.AttendanceEntry._ID + " = ?", new String[]{String.valueOf(attendanceId)});
            }
        } else { // Si no existe, crea uno nuevo
            values.put(DatabaseContract.AttendanceEntry.COLUMN_NAME_STUDENT_ID, studentId);
            values.put(DatabaseContract.AttendanceEntry.COLUMN_NAME_DATE, date);
            if (entryTime != null) values.put(DatabaseContract.AttendanceEntry.COLUMN_NAME_ENTRY_TIME, entryTime);
            if (exitTime != null) values.put(DatabaseContract.AttendanceEntry.COLUMN_NAME_EXIT_TIME, exitTime);
            db.insert(DatabaseContract.AttendanceEntry.TABLE_NAME, null, values);
        }
        cursor.close();
        db.close();
    }

    /**
     * Obtiene el registro de asistencia para un estudiante en una fecha específica.
     * @param studentId El ID del estudiante.
     * @param date La fecha a consultar.
     * @return Un objeto AttendanceRecord con los datos, o null si no hay registro.
     */
    public AttendanceRecord getAttendanceForStudentOnDate(long studentId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseContract.AttendanceEntry.TABLE_NAME +
                " WHERE " + DatabaseContract.AttendanceEntry.COLUMN_NAME_STUDENT_ID + " = ? AND " +
                DatabaseContract.AttendanceEntry.COLUMN_NAME_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), date});

        AttendanceRecord record = null;
        if (cursor.moveToFirst()) {
            int entryIndex = cursor.getColumnIndex(DatabaseContract.AttendanceEntry.COLUMN_NAME_ENTRY_TIME);
            int exitIndex = cursor.getColumnIndex(DatabaseContract.AttendanceEntry.COLUMN_NAME_EXIT_TIME);

            if (entryIndex != -1 && exitIndex != -1) {
                record = new AttendanceRecord(null, 0, null, date,
                        cursor.getString(entryIndex),
                        cursor.getString(exitIndex));
            }
        }
        cursor.close();
        db.close();
        return record;
    }

    /**
     * Obtiene todos los registros de asistencia combinados con los datos del estudiante para exportar.
     * @return Una lista de objetos AttendanceRecord listos para la exportación.
     */
    public List<AttendanceRecord> getAttendanceForExport() {
        List<AttendanceRecord> records = new ArrayList<>();
        String selectQuery = "SELECT s." + DatabaseContract.StudentEntry.COLUMN_NAME_FULL_NAME + ", s." +
                DatabaseContract.StudentEntry.COLUMN_NAME_GRADE + ", s." + DatabaseContract.StudentEntry.COLUMN_NAME_GROUP +
                ", a." + DatabaseContract.AttendanceEntry.COLUMN_NAME_DATE + ", a." +
                DatabaseContract.AttendanceEntry.COLUMN_NAME_ENTRY_TIME + ", a." + DatabaseContract.AttendanceEntry.COLUMN_NAME_EXIT_TIME +
                " FROM " + DatabaseContract.AttendanceEntry.TABLE_NAME + " a" +
                " INNER JOIN " + DatabaseContract.StudentEntry.TABLE_NAME + " s ON a." +
                DatabaseContract.AttendanceEntry.COLUMN_NAME_STUDENT_ID + " = s." + DatabaseContract.StudentEntry._ID +
                " ORDER BY a." + DatabaseContract.AttendanceEntry.COLUMN_NAME_DATE + " DESC, s." + DatabaseContract.StudentEntry.COLUMN_NAME_FULL_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int nameIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry.COLUMN_NAME_FULL_NAME);
                int gradeIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry.COLUMN_NAME_GRADE);
                int groupIndex = cursor.getColumnIndex(DatabaseContract.StudentEntry.COLUMN_NAME_GROUP);
                int dateIndex = cursor.getColumnIndex(DatabaseContract.AttendanceEntry.COLUMN_NAME_DATE);
                int entryIndex = cursor.getColumnIndex(DatabaseContract.AttendanceEntry.COLUMN_NAME_ENTRY_TIME);
                int exitIndex = cursor.getColumnIndex(DatabaseContract.AttendanceEntry.COLUMN_NAME_EXIT_TIME);

                if (nameIndex != -1 && gradeIndex != -1 && groupIndex != -1 && dateIndex != -1 && entryIndex != -1 && exitIndex != -1) {
                    AttendanceRecord record = new AttendanceRecord(
                            cursor.getString(nameIndex),
                            cursor.getInt(gradeIndex),
                            cursor.getString(groupIndex),
                            cursor.getString(dateIndex),
                            cursor.getString(entryIndex),
                            cursor.getString(exitIndex)
                    );
                    records.add(record);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }
}