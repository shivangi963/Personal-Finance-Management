

import java.util.List;
import java.util.Map;

public class Export {
	 private TransactionView transactionView;
	    
	    public Export(TransactionView transactionView) {
	        this.transactionView = transactionView;
	    }
	    
	    public List<Transaction> getTransactionsForExport(String period) throws Exception {
	        List<Transaction> transactions = transactionView.getTransactionsForExport(period);
	        System.out.println("Exporting " + transactions.size() + " transactions for period: " + period);
	        return transactions;
	    }
	    
	    public Map<String, Double> getSummaryForExport(String period) throws Exception {
	        Map<String, Double> summary = transactionView.getSummaryForExport(period);
	        System.out.println("Exporting summary with " + summary.size() + " categories for period: " + period);
	        summary.forEach((k, v) -> System.out.println(k + ": " + v));
	        return summary;
	    }
	}

