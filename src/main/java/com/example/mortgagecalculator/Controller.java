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
    private ObservableList<TableData> newList = FXCollections.observableArrayList();

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
        calculate();
    }

    public void calculate() {
        int counter = 1;
        int term = loanTermYearInput * 12 + loanTermMonthInput;
        double remainingBalance = loanAmountInput;
        double monthlyInterestRate = interestRateInput / 12 / 100;
        double monthlyPayment;
        int postponeStart = delayYearStartInput * 12 + delayMonthStartInput;
        int postponeEnd = delayYearEndInput * 12 + delayMonthEndInput;
        double totalToPay = 0;
        int totalInterest = 0;
        dataList.clear();

        if (selectedGraph.equals("Annuity")) {
            System.out.println("CALCULATING ANNUITY MORTGAGE");

            monthlyPayment = (loanAmountInput * monthlyInterestRate) / (1 - Math.pow((1 + monthlyInterestRate), -term));
            if (Double.isNaN(monthlyPayment)) {
                monthlyPayment = loanAmountInput / term;
            }

            counter = 1;
            remainingBalance = monthlyPayment * term;
            double principal = loanAmountInput;
            double percent;

            for (int i = 1; i <= term; i++) {
                remainingBalance -= monthlyPayment;

                if (i >= postponeStart && i < postponeEnd && postponeEnd < term) {
                    monthlyPayment = 0;
                    counter++;
                }

                if (postponeEnd == i && i > 1 && postponeEnd < term) {
                    remainingBalance += remainingBalance * counter * monthlyInterestRate;
                    monthlyPayment = remainingBalance / (term - i);
                    principal += principal * monthlyInterestRate;
                }

                if (principal <= 0) principal = 0;
                if (remainingBalance < 1) remainingBalance = 0;
                percent = principal * monthlyInterestRate;
                principal -= percent;

                double monthlyPaymentRounded = ModifyInput.roundInput(monthlyPayment);
                double interestPaymentRounded = ModifyInput.roundInput(percent);
                double remainingBalanceRounded = ModifyInput.roundInput(remainingBalance);

                totalInterest += interestPaymentRounded;

                TableData newData = new TableData(i, (float) monthlyPaymentRounded, (float) interestPaymentRounded, totalInterest, (float) remainingBalanceRounded);
                dataList.add(newData);

                annuitySeries.getData().add(new XYChart.Data<>(i, monthlyPaymentRounded));
            }
            applyFilter();
        } else {
            System.out.println("CALCULATING LINEAR MORTGAGE");

            double monthlyReduction = loanAmountInput / term;
            for (int i = 1; i <= term; i++) {
                monthlyPayment = monthlyReduction + monthlyInterestRate * remainingBalance;
                if (i >= postponeStart && i < postponeEnd && postponeEnd < term) {
                    monthlyPayment = 0;
                    counter++;
                } else if (postponeEnd == i && i > 1 && postponeEnd < term) {
                    remainingBalance += remainingBalance * counter * monthlyInterestRate;
                    monthlyReduction = remainingBalance / term;
                    monthlyPayment = monthlyReduction + monthlyInterestRate * remainingBalance;
                } else {
                    remainingBalance -= monthlyReduction;
                }

                if (remainingBalance < 1) remainingBalance = 0;
                totalToPay += monthlyPayment;
            }

            double percentPay = totalToPay;
            monthlyReduction = loanAmountInput / term;
            remainingBalance = loanAmountInput;
            counter = 1;
            double percent;

            for (int i = 1; i <= term; i++) {
                monthlyPayment = monthlyReduction + monthlyInterestRate * remainingBalance;
                if (i >= postponeStart && i < postponeEnd && postponeEnd < term) {
                    monthlyPayment = 0;
                    counter++;
                    percent = remainingBalance * monthlyInterestRate;
                } else if (postponeEnd == i && i > 1 && postponeEnd < term) {
                    remainingBalance += remainingBalance * counter * monthlyInterestRate;
                    monthlyReduction = remainingBalance / term;
                    monthlyPayment = monthlyReduction + monthlyInterestRate * remainingBalance;
                    percent = remainingBalance * monthlyInterestRate;
                } else {
                    percent = remainingBalance * monthlyInterestRate;
                    remainingBalance -= monthlyReduction;
                }

                totalToPay -= monthlyPayment;
                if (totalToPay < 1) totalToPay = 0;

                double monthlyPaymentRounded = ModifyInput.roundInput(monthlyPayment);
                double interestPaymentRounded = ModifyInput.roundInput(percent);
                double remainingBalanceRounded = ModifyInput.roundInput(totalToPay);

                totalInterest += interestPaymentRounded;

                TableData newData = new TableData(i, (float) monthlyPaymentRounded, (float) interestPaymentRounded, totalInterest, (float) remainingBalanceRounded);
                dataList.add(newData);
                linearSeries.getData().add(new XYChart.Data<>(i, monthlyPaymentRounded));
            }
        }

        applyFilter();
    }

    public void applyFilter() {
        System.out.println("Applying filter");
        cleanUpFromLastIteration();
        newList.clear();

        // Add the rows that pass the filter to the table
        for (TableData row : dataList) {
            int month = row.getMonth();

            if (month >= (int) filterStart.getValue() && month <= (int) filterEnd.getValue()) {
                annuitySeries.getData().add(new XYChart.Data<>(month, row.getMonthlyPayment()));
                linearSeries.getData().add(new XYChart.Data<>(month, row.getMonthlyPayment()));
                newList.add(row);
                monthlyTable.setItems(newList);
                monthlyTable.refresh();
            }
        }
        annuityGraph.getData().add(annuitySeries);
        linearGraph.getData().add(linearSeries);
    }

    private void cleanUpFromLastIteration() {
        linearSeries.getData().clear();
        annuitySeries.getData().clear();
//        annuityGraph.getData().add(annuitySeries);
//        linearGraph.getData().add(linearSeries);
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