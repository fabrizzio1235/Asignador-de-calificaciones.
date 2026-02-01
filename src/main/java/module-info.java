module uady.ucan.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.github.librepdf.openpdf;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;

    // Abrimos y exportamos el paquete donde están los archivos .java
    opens uady.ucan.proyecto to javafx.fxml;
    exports uady.ucan.proyecto;
}