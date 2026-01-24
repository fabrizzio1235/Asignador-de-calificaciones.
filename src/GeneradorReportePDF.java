
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.FileOutputStream;

public class GeneradorReportePDF {
    public void generarPDF() {
        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream("Reporte_De_Calificaciones.pdf"));
            doc.open();
            //tit
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.black);
            //par
            Paragraph titulo = new Paragraph("Reporte de Calificaciones", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            doc.add(new Paragraph("\nAsignado por..."));
            //tab
            PdfPTable tabla = new PdfPTable(3);

            tabla.addCell("Matrícula");
            tabla.addCell("Nombre");
            tabla.addCell("Calificacion");

            doc.add(tabla);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            doc.close();
        }
    }
}