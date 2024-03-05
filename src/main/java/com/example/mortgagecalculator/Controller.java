package com.example.mortgagecalculator;

import Utility.Calculations;
import Utility.UserInput;
import Utility.tableData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import org.controlsfx.control.action.Action;

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

    public int delayYearStartInput;
    public int delayYearEndInput;
    public int delayMonthStartInput;
    public int delayMonthEndInput;

    private double monthlyPayment;

    /**
     * Choice boxes
     */
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
    private Slider filterStart;
    @FXML
    private Slider filterEnd;

    @FXML
    private DatePicker delayStartDate;
    @FXML
    private DatePicker delayEndDate;

    @FXML
    private Button saveToFileButton;
    @FXML Button generateData;


    @FXML
    private TableView<tableData> monthlyTable;
    @FXML
    private TableColumn<tableData, Integer> monthCol;
    @FXML
    private TableColumn<tableData, Float> monthlyPaymentCol;
    @FXML
    private TableColumn<tableData, Float> balanceLeftCol;
    @FXML
    private TableColumn<tableData, Float> interestCol;

    private ObservableList<tableData> dataList = FXCollections.observableArrayList();

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
        fillData();
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

        generateData.setOnAction(this::displayTableAndGraph);

        // Adding graph types to the choice box.
        loanType.getItems().addAll(loanTypes);
        loanType.setOnAction(this::getLoanTypeSelection);

        // Setting up filter to only allow float values to be entered in the text field.
        formatToFloat(loanAmount);
        loanAmount.setOnAction(this::getLoanAmountInput);

        delayYearStart.setOnAction(this::getDelayYearStart);
        delayMonthStart.setOnAction(this::getDelayMonthStart);
        delayYearEnd.setOnAction(this::getDelayYearEnd);
        delayMonthEnd.setOnAction(this::getDelayMonthEnd);

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

    private void getDelayMonthEnd(ActionEvent actionEvent) {
        delayMonthEndInput = Integer.parseInt(delayMonthEnd.getText());
        System.out.println("Delay month end: " + delayMonthEndInput);
    }

    private void getDelayYearEnd(ActionEvent actionEvent) {
        delayYearEndInput = Integer.parseInt(delayYearEnd.getText());
        System.out.println("Delay year end: " + delayYearEndInput);
    }

    private void getDelayMonthStart(ActionEvent actionEvent) {
        delayMonthStartInput = Integer.parseInt(delayMonthStart.getText());
        System.out.println("Delay month start: " + delayMonthStartInput);
    }

    private void getDelayYearStart(ActionEvent actionEvent) {
        delayYearStartInput = Integer.parseInt(delayYearStart.getText());
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
        balanceLeftCol.setCellValueFactory(new PropertyValueFactory<>("remainingBalance"));
        monthlyTable.setItems(dataList);
    }
    public void fillData() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        linearSeries.getData().clear();
        annuitySeries.getData().clear();
        dataList.removeAll(dataList);
        annuityGraph.getData().add(annuitySeries);
        linearGraph.getData().add(linearSeries);
        String monthlyPaymentRoundedString;

        int term = Calculations.monthsToRepay();
        remainingBalance = loanAmountInput;
        double monthlyInterestRate = interestRateInput / 1200;

        if(selectedGraph.equals("Annuity")) {
            monthlyPayment = (loanAmountInput * monthlyInterestRate) / (1 - pow(1 + monthlyInterestRate, -term));
        } else {
            monthlyPayment = (loanAmountInput / term) + (loanAmountInput * monthlyInterestRate);
        }

        for (int i = 0; i < term; i++) {
            double interestPayment = remainingBalance * monthlyInterestRate;
            double principalPayment;

            if(selectedGraph.equals("Linear")) {
                principalPayment = loanAmountInput / term;
            } else {
                principalPayment = monthlyPayment - interestPayment;
            }

            remainingBalance -= principalPayment;

            if(selectedGraph.equals("Linear")) {
                monthlyPaymentRoundedString = decimalFormat.format(principalPayment + interestPayment);
            } else monthlyPaymentRoundedString = decimalFormat.format(monthlyPayment);

            String interestPaymentRoundedString = decimalFormat.format(interestPayment);
            float monthlyPaymentRounded = Float.parseFloat(monthlyPaymentRoundedString);
            float interestPaymentRounded = Float.parseFloat(interestPaymentRoundedString);

            String remainingBalanceRoundedString = decimalFormat.format(remainingBalance);
            float remainingBalanceRounded = Float.parseFloat(remainingBalanceRoundedString);
            if(remainingBalance < 0.01) remainingBalance = 0;

//            System.out.println("Month: " + (i + 1) + " Payment: " + monthlyPayment + " Remaining balance: " + remainingBalance);

            tableData newData = new tableData(i + 1, monthlyPaymentRounded, interestPaymentRounded, remainingBalanceRounded);
            dataList.add(newData);
            monthlyTable.setItems(dataList);
            monthlyTable.refresh();

            annuitySeries.getData().add(new XYChart.Data<>(i, monthlyPaymentRounded));
            linearSeries.getData().add(new XYChart.Data<>(i, monthlyPaymentRounded));
        }

        annuityGraph.getData().add(annuitySeries);
        linearGraph.getData().add(linearSeries);
    }
}