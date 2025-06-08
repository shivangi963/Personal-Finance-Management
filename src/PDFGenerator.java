

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

public class PDFGenerator {
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10);
    
    public static void generateTransactionPDF(String filePath, List<Transaction> transactions, String period) 
            throws DocumentException, FileNotFoundException {
        if (transactions == null || transactions.isEmpty()) {
            throw new DocumentException("No transactions to export");
        }

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            addTitle(document, "Transaction Report - " + period);
            
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addTransactionTableHeader(table);
            
            for (Transaction t : transactions) {
                addTransactionRow(table, t);
            }
            
            document.add(table);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }

    public static void generateSummaryPDF(String filePath, Map<String, Double> summaryData, String period) 
            throws DocumentException, FileNotFoundException {
        if (summaryData == null || summaryData.isEmpty()) {
            throw new DocumentException("No summary data to export");
        }

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            addTitle(document, "Financial Summary - " + period);
            
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            addSummaryTableHeader(table);
            
            for (Map.Entry<String, Double> entry : summaryData.entrySet()) {
                addSummaryRow(table, entry.getKey(), entry.getValue());
            }
            
            document.add(table);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }
    private static void addTitle(Document document, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, TITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(20);
        document.add(p);
    }
    
    private static void addTransactionTableHeader(PdfPTable table) {
        String[] headers = {"Date", "Description", "Category", "Type", "Amount"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new BaseColor(220, 220, 220));
            table.addCell(cell);
        }
    }
    
    private static void addTransactionRow(PdfPTable table, Transaction t) {
        table.addCell(createCell(t.getDate().toString(), NORMAL_FONT));
        table.addCell(createCell(t.getDescription(), NORMAL_FONT));
        table.addCell(createCell(t.getCategory(), NORMAL_FONT));
        table.addCell(createCell(t.getType(), NORMAL_FONT));
        table.addCell(createCell(NumberFormat.getCurrencyInstance().format(t.getAmount()), NORMAL_FONT));
    }
    
    private static void addSummaryTableHeader(PdfPTable table) {
        PdfPCell cell1 = new PdfPCell(new Phrase("Category", HEADER_FONT));
        PdfPCell cell2 = new PdfPCell(new Phrase("Amount", HEADER_FONT));
        cell1.setBackgroundColor(new BaseColor(220, 220, 220));
        cell2.setBackgroundColor(new BaseColor(220, 220, 220));
        table.addCell(cell1);
        table.addCell(cell2);
    }
    
    private static void addSummaryRow(PdfPTable table, String category, double amount) {
        table.addCell(createCell(category, NORMAL_FONT));
        table.addCell(createCell(NumberFormat.getCurrencyInstance().format(amount), NORMAL_FONT));
    }
    
    private static PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }
}