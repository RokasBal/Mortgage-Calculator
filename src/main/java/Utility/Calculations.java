package Utility;

import com.example.mortgagecalculator.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static Utility.TableData.*;

public class Calculations {

    public static int loanTerm;
    public static float loanAmount;

    Controller controller;

    public Calculations(Controller controller) {
        this.controller = controller;
    }

    public int monthsToRepay() {
        loanTerm = controller.loanTermYearInput * 12 + controller.loanTermMonthInput;
        System.out.println("Term in Calculations: " + loanTerm + " loanTermYearInput: " + controller.loanTermYearInput + " loanTermMonthInput: " + controller.loanTermMonthInput);
        return loanTerm;
    }

    public float totalLoanAmount() {
        loanAmount = controller.loanAmountInput + controller.loanAmountInput * controller.interestRateInput * loanTerm;
        return loanAmount;
    }

}
