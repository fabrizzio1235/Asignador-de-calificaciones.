package uady.ucan.proyecto;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.scene.control.Alert;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class GeneradorReportePDF extends CambioDeMenu{
    public void generarPdf(String pdfNombre) {
        if(pdfNombre == null || pdfNombre.trim().isEmpty()){pdfNombre ="Reporte_Calificaciones";}
        Document doc = new Document();
        ArrayList<Alumno> alumnitos = getAlumnos();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream("output/"+pdfNombre+".pdf"));
            doc.open();
            //tit
            Font fontTit = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.black);
            Paragraph tit = new Paragraph("Reporte de Calificaciones", fontTit);
            tit.setAlignment(Element.ALIGN_CENTER);
            //sub (materia)
            Font fontsub = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.black);
            Paragraph sub = new Paragraph("Diseño de Software", fontsub);
            sub.setAlignment(Element.ALIGN_CENTER);

            doc.add(tit);
            doc.add(sub);
            doc.add(new Paragraph(" ")); doc.add(new Paragraph(" ")); //saltos de linea en el documento

            //tab
            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidths(new float[] {1f, 3f, 1f});

            tabla.addCell("Matrícula");
            tabla.addCell("Nombre");
            tabla.addCell("Calificación");
            for (int i = 0; i < alumnitos.size(); i++) {
                tabla.addCell(alumnitos.get(i).getMatricula());
                tabla.addCell(alumnitos.get(i).getAppellido1()+" "+alumnitos.get(i).getAppellido2()+" "+alumnitos.get(i).getNombres());
                if(alumnitos.get(i).getCalificacion()>=0){
                    tabla.addCell(String.valueOf(alumnitos.get(i).getCalificacion()));
                } else {
                    tabla.addCell("S/C");
                }
            }
            doc.add(tabla);
            setAlert(Alert.AlertType.CONFIRMATION, "Reporte PDF creado con exito en: " + new File("output/Reporte_De_Calificaciones.pdf").getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            doc.close();
        }
    }
}