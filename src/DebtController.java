import java.sql.SQLException;
import java.util.List;

public class DebtController {
    private DebtModel model;
    private DebtView view;

    public DebtController(DebtModel model, DebtView view) {
        this.model = model;
        this.view = view;
        loadData();
    }

    public void loadData() {
        try {
            List<DebtModel.Debt> debts = model.getDebts();
            List<DebtModel.Debt> credits = model.getCredits();
            double totalDebt = model.getTotalDebt();
            double totalCredit = model.getTotalCredit();
            double netPosition = model.getNetPosition();

            view.renderDebts(debts, credits, totalDebt, totalCredit, netPosition);
        } catch (SQLException e) {
            view.showError("Failed to load debt data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addDebt(String name, double totalAmount, String dueDate, double interestRate) throws SQLException {
        if (model.addDebt(name, totalAmount, dueDate, interestRate)) {
		    loadData(); 
		    
		} else {
		    view.showError("Failed to add debt.");
		}
    }

    public void addCredit(String name, double totalAmount, String dueDate, double interestRate) throws SQLException {
        if (model.addCredit(name, totalAmount, dueDate, interestRate)) {
		    loadData();
		    
		} else {
		    view.showError("Failed to add credit.");
		}
    }

    public void recordDebtPayment(int debtId, double paymentAmount) throws SQLException{
        if (model.updateDebtPayment(debtId, paymentAmount)) {
		    loadData(); // Refresh the view
		  
		} else {
		    view.showError("Failed to record payment.");
		}
    }

    public void recordCreditPayment(int creditId, double paymentAmount) throws SQLException {
        if (model.updateCreditPayment(creditId, paymentAmount)) {
		    loadData(); // Refresh the view
		 
		} else {
		    view.showError("Failed to record payment.");
		}
    }

    public void deleteDebt(int debtId) {
        try {
            if (model.deleteDebt(debtId)) {
                loadData(); 
 
            } else {
                view.showError("Failed to delete debt.");
            }
        } catch (SQLException e) {
            view.showError("Error deleting debt: " + e.getMessage());
        }
    }

    public void deleteCredit(int creditId) {
        try {
            if (model.deleteCredit(creditId)) {
                loadData(); 
              
            } else {
                view.showError("Failed to delete credit.");
            }
        } catch (SQLException e) {
            view.showError("Error deleting credit: " + e.getMessage());
        }
    }
}