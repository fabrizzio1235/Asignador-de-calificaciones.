public class Alumno {
    private final String matricula;
    private final String apellido1;
    private final String apellido2;
    private final String nombres;
    private int calificacion = -1; //algún alumno que sacó 0, puede quedar como default y contar como calif ingresada

    public Alumno(String matricula, String apellido1, String apellido2, String nombres) {
        this.matricula = matricula;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.nombres = nombres;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getAppellido1() {
        return apellido1;
    }

    public String getAppellido2() {
        return apellido2;
    }

    public String getNombres() {
        return nombres;
    }

    void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public int getCalificacion() {
        return calificacion;
    }

    @Override
    public String toString() {
        return getMatricula() + ',' + getAppellido1() + ',' + getAppellido2() + ',' + getNombres() + ",Diseño de Software," + getCalificacion();
    }

}
