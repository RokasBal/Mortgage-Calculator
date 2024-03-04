package Utility;

import com.example.mortgagecalculator.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static Utility.tableData.*;

public class Calculations {

    public static int loanTerm;
    public static float loanAmount;
    public static int monthsToRepay() {
        loanTerm = Controller.loanTermYearInput * 12 + Controller.loanTermMonthInput;
        return loanTerm;
    }

    public static float totalLoanAmount() {
        loanAmount = Controller.loanAmountInput + Controller.loanAmountInput * Controller.interestRateInput * loanTerm;
        return loanAmount;
    }

}
