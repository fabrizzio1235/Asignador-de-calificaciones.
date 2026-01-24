
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class GeneradorReportePDF {
    public void generarPDF(ArrayList<Alumno> alumnitos) {
        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream("Reporte_De_Calificaciones.pdf"));
            doc.open();
            //tit
            Font fontTit = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.black);
            Paragraph tit = new Paragraph("Reporte de Calificaciones", fontTit);
            tit.setAlignment(Element.ALIGN_CENTER);
            //sub (materia)
            Font fontsub = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.black);
            Paragraph sub = new Paragraph("Diseño de software", fontsub);
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
                if(alumnitos.get(i).getCalificacion()>0){
                    tabla.addCell(String.valueOf(alumnitos.get(i).getCalificacion()));
                } else {
                    tabla.addCell("S/C");
                }
            }
            doc.add(tabla);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            doc.close();
        }
    }
}