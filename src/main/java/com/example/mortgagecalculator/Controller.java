package com.example.mortgagecalculator;

import Utility.Calculations;
import Utility.tableData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static java.lang.Math.pow;

public class Controller implements Initializable {

    public static String selectedGraph;

    public static int loanTermYearInput;
    public static int loanTermMonthInput;
    public static float loanAmountInput;
    public static float interestRateInput;

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
    public LineChart annuityGraph;
    @FXML
    public LineChart linearGraph;
    @FXML
    private TableView<tableData> monthlyTable;
//    @FXML
//    public static TableColumn<tableData, Integer> monthCol = new TableColumn<>("Month");
//    @FXML
//    public static TableColumn<tableData, Float> monthlyPaymentCol = new TableColumn<>("Monthly payment");
//    @FXML
//    public static TableColumn<tableData, Float> balanceLeftCol = new TableColumn<>("Balance left");
    @FXML
    private TableColumn<tableData, Integer> monthCol;
    @FXML
    private TableColumn<tableData, Float> monthlyPaymentCol;
    @FXML
    private TableColumn<tableData, Float> balanceLeftCol;

    private ObservableList<tableData> dataList = FXCollections.observableArrayList();

    /**
     * Declaring available loan types to choose from.
     */
    private String[] loanTypes = {"Annuity", "Linear"};
    private String[] displayTypes = {"Graph", "Payment table"};
    double remainingBalance;

    private void refreshGraphs() {
        if(selectedGraph.equals("Annuity")) {
            annuityGraph.setVisible(true);
            linearGraph.setVisible(false);
            monthlyTable.setVisible(false);
        } else if(selectedGraph.equals("Linear")) {
            annuityGraph.setVisible(false);
            linearGraph.setVisible(true);
            monthlyTable.setVisible(false);
        }
    }

    public void getLoanTypeSelection(ActionEvent event) {
        selectedGraph = loanType.getValue();
    }

    public void getDisplaySelection(ActionEvent event) {
        String selectedDisplay = displaySelection.getValue();
        if(selectedDisplay.equals("Graph")) {
            if(selectedGraph.equals("Annuity")) {
                annuityGraph.setVisible(true);
                linearGraph.setVisible(false);
                monthlyTable.setVisible(false);
            } else {
                annuityGraph.setVisible(false);
                linearGraph.setVisible(true);
                monthlyTable.setVisible(false);
            }
        } else {
            monthlyTable.setVisible(true);
            annuityGraph.setVisible(false);
            linearGraph.setVisible(false);
        }
        fillTable();
        System.out.println("AA");
    }

    public void getLoanAmountInput(ActionEvent event) {
        loanAmountInput = Float.parseFloat(loanAmount.getText());
        System.out.println("Loan amount: " + loanAmountInput);
    }

    public void getInterestRateInput(ActionEvent event) {
        interestRateInput = Float.parseFloat(interestRate.getText());
        System.out.println("Interest rate: " + interestRateInput);
    }

    public void getLoanTermYearInput(ActionEvent event) {
        loanTermYearInput = Integer.parseInt(loanTermYear.getText());
        System.out.println("Loan term years: " + loanTermYearInput);
    }

    public void getLoanTermMonthInput(ActionEvent event) {
        loanTermMonthInput = Integer.parseInt(loanTermMonth.getText());
        System.out.println("Loan term months: " + loanTermMonthInput);
    }

    private void formatToFloat(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(new UnaryOperator<TextFormatter.Change>() {
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
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTable();

        // Adding graph types to the choice box.
        loanType.getItems().addAll(loanTypes);
        loanType.setOnAction(this::getLoanTypeSelection);
//        displaySelection.getItems().addAll(displayTypes);
//        displaySelection.setOnAction(this::getDisplaySelection);

//        Font.loadFont(getClass().getResourceAsStream("resources/com.example.mortgagecalculator/Satoshi-Variable.ttf"), 14);

        // Setting up filter to only allow float values to be entered in the text field.
        formatToFloat(loanAmount);
        loanAmount.setOnAction(this::getLoanAmountInput);

        formatToFloat(interestRate);
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


    private void initializeTable() {
        monthCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        monthlyPaymentCol.setCellValueFactory(new PropertyValueFactory<>("monthlyPayment"));
        balanceLeftCol.setCellValueFactory(new PropertyValueFactory<>("remainingBalance"));
        monthlyTable.setItems(dataList);
    }
    public void fillTable() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        int term = Calculations.monthsToRepay();
        float loan = Calculations.totalLoanAmount();
        String balance = decimalFormat.format(loanAmountInput);
        remainingBalance = Float.parseFloat(balance);
        double monthlyInterestRate = interestRateInput / 1200;
        double monthlyPayment = (loanAmountInput * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, -term));

        for (int i = 0; i < term; i++) {
            double interestPayment = remainingBalance * monthlyInterestRate;
            double balanceLeft = monthlyPayment - interestPayment;

            String monthlyPaymentRoundedString = decimalFormat.format(monthlyPayment);
            String balanceLeftRoundedString = decimalFormat.format(balanceLeft);
            float monthlyPaymentRounded = Float.parseFloat(monthlyPaymentRoundedString);
            float balanceLeftRounded = Float.parseFloat(balanceLeftRoundedString);

            remainingBalance -= balanceLeftRounded;
            if(remainingBalance < 1) remainingBalance = 0;

            System.out.println("Month: " + (i + 1) + " Payment: " + monthlyPayment + " Remaining balance: " + remainingBalance);

            tableData newData = new tableData(i + 1, (float) monthlyPaymentRounded, (float) remainingBalance);
            dataList.add(newData);
            monthlyTable.setItems(dataList);
            monthlyTable.refresh();
        }
    }
}