import java.util.List;
import java.util.Map;

public class ExportController {
    private final Export model;
    
    public ExportController(Export model) {
        this.model = model;
    }
    
    public void exportTransactions(String period, String filePath) throws Exception {
        List<Transaction> transactions = model.getTransactionsForExport(period);
        if (transactions.isEmpty()) {
            throw new Exception("No transactions found for selected period");
        }
        PDFGenerator.generateTransactionPDF(filePath, transactions, period);
    }
    
    public void exportSummary(String period, String filePath) throws Exception {
        Map<String, Double> summary = model.getSummaryForExport(period);
        if (summary.isEmpty()) {
            throw new Exception("No summary data found for selected period");
        }
        PDFGenerator.generateSummaryPDF(filePath, summary, period);
    }
}