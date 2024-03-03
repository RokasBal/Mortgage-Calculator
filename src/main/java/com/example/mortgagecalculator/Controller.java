package com.example.mortgagecalculator;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.text.Font;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class Controller implements Initializable {


    /**
     * Choice boxes
     */
    @FXML
    private ChoiceBox<String> loanType;
    @FXML
    private ChoiceBox<String> displaySelection;

    @FXML
    private TextField loanAmount;
    @FXML
    private TextField interestRate;
    @FXML
    private TextField loanTermYear;
    @FXML
    private TextField loanTermMonth;

    @FXML
    private Slider filterStart;
    @FXML
    private Slider filterEnd;

    @FXML
    private DatePicker delayStartDate;
    @FXML
    private DatePicker delayEndDate;

    @FXML
    private Button saveToFileButton;

    @FXML
    private LineChart annuityGraph;
    @FXML
    private LineChart linearGraph;
    @FXML
    private TableView monthlyTable;

    /**
     * Declaring available loan types to choose from.
     */
    private String[] loanTypes = {"Annuity", "Linear"};
    private String[] displayTypes = {"Graph", "Payment table"};

    public void getLoanTypeSelection(ActionEvent event) {
        String selectedGraph = loanType.getValue();
        System.out.println("Selected loan type: " + selectedGraph);
    }

    public void getDisplaySelection(ActionEvent event) {
        String selectedDisplay = displaySelection.getValue();
        System.out.println("Selected display: " + selectedDisplay);
    }

    public void getLoanAmountInput(ActionEvent event) {
        float loanAmountInput = Float.parseFloat(loanAmount.getText());
        System.out.println("Loan amount: " + loanAmountInput);
    }

    public void getInterestRateInput(ActionEvent event) {
        float interestRateInput = Float.parseFloat(interestRate.getText());
        System.out.println("Interest rate: " + interestRateInput);
    }

    public void getLoanTermYearInput(ActionEvent event) {
        int loanTermYearInput = Integer.parseInt(loanTermYear.getText());
        System.out.println("Loan term years: " + loanTermYearInput);
    }

    public void getLoanTermMonthInput(ActionEvent event) {
        int loanTermMonthInput = Integer.parseInt(loanTermMonth.getText());
        System.out.println("Loan term months: " + loanTermMonthInput);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Adding graph types to the choice box.
        loanType.getItems().addAll(loanTypes);
        loanType.setOnAction(this::getLoanTypeSelection);
        displaySelection.getItems().addAll(displayTypes);
        displaySelection.setOnAction(this::getDisplaySelection);

//        Font.loadFont(getClass().getResourceAsStream("resources/com.example.mortgagecalculator/Satoshi-Variable.ttf"), 14);

        // Setting up filter to only allow float values to be entered in the text field.
        loanAmount.setTextFormatter(new TextFormatter<>(new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                if (change.isContentChange()) {

                    if (change.getControlNewText().matches("([0-9]*\\.?[0-9]*)?")) {
                        return change;
                    }
                    return null;
                }
                return change;
            }
        }));
        loanAmount.setOnAction(this::getLoanAmountInput);

        interestRate.setTextFormatter(new TextFormatter<>(new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                if (change.isContentChange()) {

                    if (change.getControlNewText().matches("([0-9]*\\.?[0-9]*)?")) {
                        return change;
                    }
                    return null;
                }
                return change;
            }
        }));
        interestRate.setOnAction(this::getInterestRateInput);

        loanTermYear.setTextFormatter(new TextFormatter<>(new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                if (change.isContentChange()) {

                    if (change.getControlNewText().matches("([0-9]*)?")) {
                        return change;
                    }
                    return null;
                }
                return change;
            }
        }));
        loanTermYear.setOnAction(this::getLoanTermYearInput);
        loanTermMonth.setTextFormatter(new TextFormatter<>(new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                if (change.isContentChange()) {

                    if (change.getControlNewText().matches("([0-9]*)?")) {
                        return change;
                    }
                    return null;
                }
                return change;
            }
        }));
        loanTermMonth.setOnAction(this::getLoanTermMonthInput);
    }
}