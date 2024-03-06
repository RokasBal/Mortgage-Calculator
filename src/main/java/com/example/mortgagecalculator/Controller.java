package com.example.mortgagecalculator;

import Utility.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Math.pow;

/**
 * Controller class, handling the user input and visualizing data.
 */
public class Controller implements Initializable {
    public static String selectedGraph;
    private boolean tableDrawn = false;
    public static int loanTermYearInput;
    public static int loanTermMonthInput;
    public static float loanAmountInput;
    public static float interestRateInput;
    private int startDate;
    private int endDate;

    public int totalDelay;
    public int delayStartMonth;
    public int delayEndMonth;

    public int delayYearStartInput = 0;
    public int delayYearEndInput = 0;
    public int delayMonthStartInput = 0;
    public int delayMonthEndInput = 0;
    private double monthlyPayment;
    @FXML
    private ChoiceBox<String> loanType;
    @FXML
    private ChoiceBox<String> displaySelection;

    @FXML
    protected TextField loanAmount;
    @FXML
    private TextField interestRate;
    @FXML
    private TextField loanTermYear;
    @FXML
    private TextField loanTermMonth;
    @FXML
    private TextField delayYearStart;
    @FXML
    private TextField delayMonthStart;
    @FXML
    private TextField delayYearEnd;
    @FXML
    private TextField delayMonthEnd;

    @FXML
    protected Slider filterStart;
    @FXML
    protected Slider filterEnd;

    @FXML
    private Button saveToFileButton;
    @FXML
    private Button generateData;

    @FXML
    private TableView<TableData> monthlyTable;
    @FXML
    private TableColumn<TableData, Integer> monthCol;
    @FXML
    private TableColumn<TableData, Float> monthlyPaymentCol;
    @FXML
    private TableColumn<TableData, Float> balanceLeftCol;
    @FXML
    private TableColumn<TableData, Float> interestCol;
    @FXML
    private TableColumn<TableData, Float> totalInterestCol;

    private ObservableList<TableData> dataList = FXCollections.observableArrayList();

    @FXML
    private LineChart<Number, Number> annuityGraph;
    @FXML
    private LineChart<Number, Number> linearGraph;
    private final XYChart.Series<Number, Number> linearSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> annuitySeries = new XYChart.Series<>();

    /**
     * Declaring available loan types to choose from.
     */
    private String[] loanTypes = {"Annuity", "Linear"};
    double remainingBalance;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up the collumns of the table
        initializeTable();

        Setup setup = new Setup();
        setup.initializeSliders(filterStart, filterEnd);
        filterStart.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                startDate = (int) filterStart.getValue();
                if((int) filterStart.getValue() >= (int) filterEnd.getValue()) filterEnd.setValue(filterStart.getValue());
                fillData();
            }
        });
        filterEnd.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                endDate = (int) filterEnd.getValue();
                if((int) filterEnd.getValue() <= (int) filterStart.getValue()) filterStart.setValue(filterEnd.getValue());
                fillData();
            }
        });

        // Adding graph types to the choice box and assigning functionality.
        loanType.getItems().addAll(loanTypes);
        loanType.setOnAction(this::getLoanTypeSelection);

        // Setting up button functionality.
        saveToFileButton.setOnAction(this::saveToFilePressed);
        generateData.setOnAction(this::displayTableAndGraph);

        // Setting up filter to only allow float values to be entered in the text field.
        setup.formatToFloat(loanAmount);
        loanAmount.setOnAction(this::getLoanAmountInput);

        // Setting up functionality for filter sliders.
        delayYearStart.setOnAction(this::getDelayYearStart);
        delayMonthStart.setOnAction(this::getDelayMonthStart);
        delayYearEnd.setOnAction(this::getDelayYearEnd);
        delayMonthEnd.setOnAction(this::getDelayMonthEnd);

        // Formatting interest rate text field to only allow float values and assigning it functionality.
        setup.formatToFloat(interestRate);
        interestRate.setOnAction(this::getInterestRateInput);

        // Formatting loan term text fields to only allow int values and assigning them functionality.
        setup.formatToInt(loanTermYear);
        loanTermYear.setOnAction(this::getLoanTermYearInput);
        setup.formatToInt(loanTermMonth);
        loanTermMonth.setOnAction(this::getLoanTermMonthInput);
    }

    /**
     * Primary method for this program, calculating both graph types and displaying them within the table and graph.
     */
    public void fillData() {
        float totalInterest = 0;

        // Declaring that the visualization has been rendered at least once, allowing other functions to call this method.
        tableDrawn = true;
        // Clearing the graph and table data to avoid overlap.
        cleanUpFromLastIteration();

        // Getting payment delay information.
        Setup setup = new Setup();
        totalDelay = (delayYearEndInput - delayYearStartInput) * 12 + (delayMonthEndInput - delayMonthStartInput);
        delayStartMonth = delayYearStartInput * 12 + delayMonthStartInput;
        delayEndMonth = delayYearEndInput * 12 + delayMonthEndInput;
        System.out.println("Total delay: " + totalDelay + ", Delay start month: " + delayStartMonth + ", Delay end month: " + delayEndMonth);

        // Calculating the total length of time over which the mortgage needs to be repaid in months.
        int term = Calculations.monthsToRepay();
        // Setting the starting value of remainingBalance to the total loan amount.
        remainingBalance = loanAmountInput;
        // Calculating the monthly interest rate from the provided yearly interest rate.
        double monthlyInterestRate = interestRateInput / 1200;

        // Calculating the monthly payment for annuity or linear mortgage, based on the selected mortgage type.
        if(selectedGraph.equals("Annuity")) {
            monthlyPayment = (loanAmountInput * monthlyInterestRate) / (1 - pow(1 + monthlyInterestRate, -(term - totalDelay)));
        } else {
            monthlyPayment = (loanAmountInput / term) + (loanAmountInput * monthlyInterestRate);
        }

        // Loop, going through each month of the mortgage term, calculating the monthly payment, interest payment and remaining balance.
        for (int i = (int) filterStart.getValue(); i <= (int) (filterEnd.getValue()); i++) {
            // Calculating the monthly interest in currency.
            double interestPayment = remainingBalance * monthlyInterestRate;
            double principalPayment = 0;

            float interestPaymentRounded;
            float remainingBalanceRounded;
            float monthlyPaymentRounded;

            if(filterStart.getValue() > 0) {
                for(int x = 0; x < i; x++) {
                    System.out.println("Remaining balance: " + remainingBalance + "i: " + i);
                    remainingBalance -= principalPayment;
                }
            }

            // Checking if current month is inside a postpone period.
            if(i >= delayStartMonth && i < delayEndMonth) {
                interestPayment = 0;
                monthlyPaymentRounded = 0;
            } else {
                if(selectedGraph.equals("Linear")) {
                    principalPayment = loanAmountInput / (term - totalDelay);
                } else {
                    principalPayment = monthlyPayment;
                }

                if(selectedGraph.equals("Linear")) {
                    monthlyPaymentRounded = ModifyInput.roundInput(principalPayment + interestPayment);
                } else monthlyPaymentRounded = ModifyInput.roundInput(monthlyPayment);

                // Checking if on the last month, the remaining balance is lower than the monthly payment calculated.
//                if(remainingBalance < monthlyPayment) {
//                    monthlyPaymentRounded = ModifyInput.roundInput(remainingBalance + interestPayment);
//                    remainingBalance = 0;
//                } else {
//                    remainingBalance -= principalPayment;
//                }
                remainingBalance -= principalPayment;

                System.out.println("Monthly payment: " + monthlyPaymentRounded + ", Remaining Balance: " + remainingBalance);
            }

            if(remainingBalance < 1) remainingBalance = 0;

            interestPaymentRounded = ModifyInput.roundInput(interestPayment);
            remainingBalanceRounded = ModifyInput.roundInput(remainingBalance);

            totalInterest += interestPaymentRounded;
            float totalInterestRounded = ModifyInput.roundInput(totalInterest);

            TableData newData = new TableData(i, monthlyPaymentRounded, interestPaymentRounded, totalInterestRounded, remainingBalanceRounded);
            dataList.add(newData);
            monthlyTable.setItems(dataList);
            monthlyTable.refresh();

            annuitySeries.getData().add(new XYChart.Data<>(i, monthlyPaymentRounded));
            linearSeries.getData().add(new XYChart.Data<>(i, monthlyPaymentRounded));
        }

        // Adding data collected in the loop to the graphs.
        annuityGraph.getData().add(annuitySeries);
        linearGraph.getData().add(linearSeries);
    }

    private void cleanUpFromLastIteration() {
        linearSeries.getData().clear();
        annuitySeries.getData().clear();
        dataList.removeAll(dataList);
        annuityGraph.getData().add(annuitySeries);
        linearGraph.getData().add(linearSeries);
    }

    public void getLoanTypeSelection(ActionEvent event) {
        selectedGraph = loanType.getValue();
        if(tableDrawn) fillData();
    }

    public void getLoanAmountInput(ActionEvent event) {
        loanAmountInput = Float.parseFloat(loanAmount.getText());
        if(tableDrawn) fillData();
        System.out.println("Loan amount: " + loanAmountInput);
    }

    public void getInterestRateInput(ActionEvent event) {
        interestRateInput = Float.parseFloat(interestRate.getText());
        if(tableDrawn) fillData();
        System.out.println("Interest rate: " + interestRateInput);
    }

    public void getLoanTermYearInput(ActionEvent event) {
        loanTermYearInput = Integer.parseInt(loanTermYear.getText());
        filterStart.setMax(loanTermYearInput * 12 + loanTermMonthInput);
        filterEnd.setMax(loanTermYearInput * 12 + loanTermMonthInput);
        filterEnd.setValue(loanTermYearInput * 12 + loanTermMonthInput);
        if(tableDrawn) fillData();
        System.out.println("Loan term years: " + loanTermYearInput);
    }

    public void getLoanTermMonthInput(ActionEvent event) {
        loanTermMonthInput = Integer.parseInt(loanTermMonth.getText());
        filterStart.setMax(loanTermYearInput * 12 + loanTermMonthInput);
        filterEnd.setMax(loanTermYearInput * 12 + loanTermMonthInput);
        filterEnd.setValue(loanTermYearInput * 12 + loanTermMonthInput);
        if(tableDrawn) fillData();
        System.out.println("Loan term months: " + loanTermMonthInput);
    }
    protected boolean getTableDrawn() {
        return tableDrawn;
    }

    private void saveToFilePressed(ActionEvent actionEvent) {
        SaveToCSV.saveToFile(dataList, "src/output/loanData.csv");
    }

    private void getDelayMonthEnd(ActionEvent actionEvent) {
        delayMonthEndInput = Integer.parseInt(delayMonthEnd.getText());
        if(tableDrawn) fillData();
        System.out.println("Delay month end: " + delayMonthEndInput);
    }

    private void getDelayYearEnd(ActionEvent actionEvent) {
        delayYearEndInput = Integer.parseInt(delayYearEnd.getText());
        if(tableDrawn) fillData();
        System.out.println("Delay year end: " + delayYearEndInput);
    }

    private void getDelayMonthStart(ActionEvent actionEvent) {
        delayMonthStartInput = Integer.parseInt(delayMonthStart.getText());
        if(tableDrawn) fillData();
        System.out.println("Delay month start: " + delayMonthStartInput);
    }

    private void getDelayYearStart(ActionEvent actionEvent) {
        delayYearStartInput = Integer.parseInt(delayYearStart.getText());
        if(tableDrawn) fillData();
        System.out.println("Delay year start: " + delayYearStartInput);
    }

    private void displayTableAndGraph(ActionEvent actionEvent) {
        fillData();
        monthlyTable.setVisible(true);
        if(selectedGraph.equals("Annuity")) {
            annuityGraph.setVisible(true);
            linearGraph.setVisible(false);
        } else if(selectedGraph.equals("Linear")) {
            annuityGraph.setVisible(false);
            linearGraph.setVisible(true);
        }
    }

    private void initializeTable() {
        monthCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        monthlyPaymentCol.setCellValueFactory(new PropertyValueFactory<>("monthlyPayment"));
        interestCol.setCellValueFactory(new PropertyValueFactory<>("interestPayment"));
        totalInterestCol.setCellValueFactory(new PropertyValueFactory<>("totalInterest"));
        balanceLeftCol.setCellValueFactory(new PropertyValueFactory<>("remainingBalance"));
        monthlyTable.setItems(dataList);
    }
}