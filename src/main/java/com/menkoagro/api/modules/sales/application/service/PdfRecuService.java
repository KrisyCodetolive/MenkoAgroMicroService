package com.menkoagro.api.modules.sales.application.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.menkoagro.api.modules.sales.domain.entity.LigneVente;
import com.menkoagro.api.modules.sales.domain.entity.Recu;
import com.menkoagro.api.modules.sales.domain.entity.Vente;
import com.menkoagro.api.shared.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class PdfRecuService {

    @Value("${app.pdf.directory:./receipts}")
    private String pdfDirectory;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FILE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public Recu genererRecu(Vente vente) {
        String numeroRecu = construireNumeroRecu(vente);
        String filePath = pdfDirectory + File.separator + numeroRecu + ".pdf";

        try {
            Files.createDirectories(Paths.get(pdfDirectory));

            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdfDoc = new PdfDocument(writer);
                 Document document = new Document(pdfDoc)) {

                ajouterEnTete(document, numeroRecu, vente);
                ajouterTableLignes(document, vente);
                ajouterTotal(document, vente);
                ajouterPiedDePage(document);
            }

        } catch (Exception e) {
            // Supprime le fichier partiel en cas d'erreur
            try { Files.deleteIfExists(Paths.get(filePath)); } catch (Exception ignored) {}
            log.error("Erreur génération PDF vente {} : {}", vente.getId(), e.getMessage());
            throw new BusinessException("Erreur lors de la génération du reçu PDF : " + e.getMessage());
        }

        return Recu.builder()
                .vente(vente)
                .numeroRecu(numeroRecu)
                .cheminFichier(filePath)
                .build();
    }

    // ─── Sections PDF ────────────────────────────────────────────────────────────

    private void ajouterEnTete(Document doc, String numeroRecu, Vente vente) {
        doc.add(new Paragraph("MENKO AGRO")
                .setFontSize(22)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        doc.add(new Paragraph("REÇU DE VENTE")
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER));

        doc.add(new Paragraph(" "));

        doc.add(new Paragraph("Numéro : " + numeroRecu).setFontSize(10));
        doc.add(new Paragraph("Date   : " + vente.getDateVente().format(DATE_FMT)).setFontSize(10));
        doc.add(new Paragraph("Client : " + vente.getClient().getNom()).setFontSize(10));

        if (vente.getClient().getTelephone() != null) {
            doc.add(new Paragraph("Tél.   : " + vente.getClient().getTelephone()).setFontSize(10));
        }
        if (vente.getClient().getEmail() != null) {
            doc.add(new Paragraph("Email  : " + vente.getClient().getEmail()).setFontSize(10));
        }

        doc.add(new Paragraph(" "));
    }

    private void ajouterTableLignes(Document doc, Vente vente) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{40, 15, 22, 23}))
                .useAllAvailableWidth();

        // En-têtes
        table.addHeaderCell(cellEntete("Produit / Conditionnement"));
        table.addHeaderCell(cellEntete("Quantité").setTextAlignment(TextAlignment.RIGHT));
        table.addHeaderCell(cellEntete("Prix unitaire").setTextAlignment(TextAlignment.RIGHT));
        table.addHeaderCell(cellEntete("Sous-total").setTextAlignment(TextAlignment.RIGHT));

        // Lignes
        for (LigneVente ligne : vente.getLignes()) {
            String libelle = ligne.getProduit().getNom()
                    + "\n(" + ligne.getConditionnement().getLibelle() + ")";

            table.addCell(new Cell().add(new Paragraph(libelle).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(ligne.getQuantite().toPlainString())
                    .setFontSize(9).setTextAlignment(TextAlignment.RIGHT)));
            table.addCell(new Cell().add(new Paragraph(formatMontant(ligne.getPrixUnitaire()))
                    .setFontSize(9).setTextAlignment(TextAlignment.RIGHT)));
            table.addCell(new Cell().add(new Paragraph(formatMontant(ligne.getSousTotal()))
                    .setFontSize(9).setTextAlignment(TextAlignment.RIGHT)));
        }

        doc.add(table);
    }

    private void ajouterTotal(Document doc, Vente vente) {
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("TOTAL : " + formatMontant(vente.getMontantTotal()))
                .setBold()
                .setFontSize(14)
                .setTextAlignment(TextAlignment.RIGHT));
        doc.add(new Paragraph(" "));
    }

    private void ajouterPiedDePage(Document doc) {
        doc.add(new Paragraph("─".repeat(60))
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Merci pour votre confiance ! — Menko Agro")
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private Cell cellEntete(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontSize(9))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
    }

    private String construireNumeroRecu(Vente vente) {
        String date = LocalDate.now().format(DATE_FILE_FMT);
        String shortId = vente.getId().toString().substring(0, 8).toUpperCase();
        return "REC-" + date + "-" + shortId;
    }

    private String formatMontant(java.math.BigDecimal montant) {
        return String.format("%,.0f FCFA", montant);
    }
}
