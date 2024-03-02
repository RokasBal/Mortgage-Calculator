package com.example.mortgagecalculator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    /**
     * ComboBox utilized for graph selection drop down menu.
     */
    @FXML
    private ChoiceBox<String> graphSelectionBox;

    /**
     * Declaring available graph types to choose from.
     */
    private String[] graphTypes = {"Annuity", "Line", "Monthly percentage"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Test Message");
        graphSelectionBox.getItems().addAll(graphTypes);
    }
}