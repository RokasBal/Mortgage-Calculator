package Utility;

import com.example.mortgagecalculator.Controller;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

public class UserInput extends Controller {
//    public boolean tableDrawn = false;

    public UserInput() {

    }
    public void handleLoanAmountInput(ActionEvent event, TextField loanAmountField) {
        loanAmountInput = Float.parseFloat(loanAmount.getText());
        // Process the loan amount input value
        System.out.println("Loan Amount: " + loanAmountInput);

        if (super.getTableDrawn()) {
            super.fillData();
        }
    }
}
