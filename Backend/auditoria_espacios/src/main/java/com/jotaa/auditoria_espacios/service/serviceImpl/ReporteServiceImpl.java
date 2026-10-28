package com.jotaa.auditoria_espacios.service.serviceImpl;

import com.jotaa.auditoria_espacios.service.InspeccionService;
import com.jotaa.auditoria_espacios.service.ReporteService;
import com.lowagie.text.pdf.PdfPTable;
import org.springframework.mail.javamail.JavaMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.ByteArrayOutputStream;


@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final JavaMailSender mailSender;
    private final InspeccionService service;

    @Override
    public byte[] generarPdfInspeccion(Long inpeccionId) {
        var insp = service.obtener(inpeccionId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titulo = new Paragraph("Reporte de Inspeccion - " + insp.espacioNombre(), fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Tipo: " + insp.tipo()));
        document.add(new Paragraph("Fecha: " + insp.fecha()));
        document.add(new Paragraph("Inspector: " + insp.inspectorNombre()));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        table.addCell("Puesto");
        table.addCell("Resultado");
        table.addCell("Novedades / Hallazgos");

        insp.detalles().forEach(d -> {
            table.addCell(d.puestoCodigo());
            table.addCell(d.resultado().toString());
            String hallazgoStr = d.hallazgos().isEmpty() ? "Sin novedades" :
                    d.hallazgos().stream()
                            .map(h -> h.tipoNombre() + " (" + (h.gravedad() != null ? h.gravedad() : "N/A") + ")")
                            .reduce("", (a, b) -> a + "; " + b);
            table.addCell(hallazgoStr);
        });

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    @Override
    public void enviarReportePorCorreo(Long inspeccionId, String correoDestino) {
        byte[] pdfBytes = generarPdfInspeccion(inspeccionId);
        var insp = service.obtener(inspeccionId);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("jmantillavillalba208@gmail.com");
            helper.setTo(correoDestino);
            helper.setSubject("Reporte de Inspección: " + insp.espacioNombre() + " (" + insp.fecha() + ")");
            helper.setText("Adjunto encontrarás el reporte detallado correspondiente a la inspección del espacio " + insp.espacioNombre() + ".");

            String nombreArchivo = String.format("Reporte_Inspeccion_%d.pdf", inspeccionId);
            helper.addAttachment(nombreArchivo, new ByteArrayResource(pdfBytes));

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage());
        }
    }


}
