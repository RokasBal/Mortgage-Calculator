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

//    public static void fillTable() {
//        monthsToRepay();
//        totalLoanAmount();
//        float payment = 0;
//        float interestRate = 0;
//        float remainingBalance = 0;
//        if (Controller.selectedGraph == "Annuity") {
//            payment = (float) (loanAmount * (Controller.interestRateInput / 12) / (1 - Math.pow(1 + Controller.interestRateInput / 12, -loanTerm)));
//            interestRate = Controller.interestRateInput;
//            remainingBalance = loanAmount - payment;
//        } else {
//            for (int i = 0; i < loanTerm; i++) {
//            }
//        }
//        ObservableList<tableData> data = FXCollections.observableArrayList(
//                new tableData(payment, interestRate, remainingBalance));
//        Controller.monthlyTable.setItems(data);
//    }

}
