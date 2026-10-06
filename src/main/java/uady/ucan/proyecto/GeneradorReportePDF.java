package uady.ucan.proyecto;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import static uady.ucan.proyecto.Alerta.setAlert;

public class GeneradorReportePDF extends CambioDeMenu {

    //Extract Method - Nuevo
    //Usa alumnoActual en vez de alumnitos.get(i) repetido
    //nombreFinal en vez de pdfNombre
    private Paragraph crearTitulo() {
        Font fontTit = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.black);
        Paragraph tit = new Paragraph("Reporte de Calificaciones", fontTit);
        tit.setAlignment(Element.ALIGN_CENTER);
        return tit;
    }

    private Paragraph crearSubtitulo() {
        Font fontsub = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.black);
        Paragraph sub = new Paragraph("Diseño de Software", fontsub);
        sub.setAlignment(Element.ALIGN_CENTER);
        return sub;
    }

    private PdfPTable crearTabla(ArrayList<Alumno> alumnitos) {
        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidths(new float[] {1f, 3f, 1f});

        tabla.addCell("Matrícula");
        tabla.addCell("Nombre");
        tabla.addCell("Calificación");
        for (int i = 0; i < alumnitos.size(); i++) {
            Alumno alumnoActual = alumnitos.get(i);
            tabla.addCell(alumnoActual.getMatricula());
            tabla.addCell(alumnoActual.getAppellido1()+" "+alumnoActual.getAppellido2()+" "+alumnoActual.getNombres());
            if(alumnoActual.getCalificacion()>=0){
                tabla.addCell(String.valueOf(alumnoActual.getCalificacion()));
            } else {
                tabla.addCell("S/C");
            }
        }
        return tabla;
    }
    @FXML
    public void generarPdf(String pdfNombre) {
        String nombreFinal = (pdfNombre == null || pdfNombre.trim().isEmpty()) ? "Reporte_Calificaciones" : pdfNombre;
        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream("output/"+nombreFinal+".pdf"));
            doc.open();

            doc.add(crearTitulo());
            doc.add(crearSubtitulo());
            doc.add(new Paragraph(" ")); doc.add(new Paragraph(" ")); //saltos de linea en el documento

            doc.add(crearTabla(repositorioAlumnos.getAlumnos()));
            setAlert(Alert.AlertType.CONFIRMATION, "Reporte PDF creado con exito en: " + new File("output/"+nombreFinal+".pdf").getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            doc.close();
        }
    }

}