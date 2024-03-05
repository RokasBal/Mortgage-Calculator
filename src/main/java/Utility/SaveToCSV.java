package Utility;

import javafx.collections.ObservableList;

import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

/**
 * Class utilized for exporting data to a CSV file
 */
public class SaveToCSV {
    public static void saveToFile(ObservableList<TableData> dataList, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Writing header
            String[] header = {"Month", "Monthly payment", "Monthly interest", "Remaining balance"}; // Replace with your column names
            writer.writeNext(header);

            // Writing data
            for (TableData data : dataList) {
                String[] row = {String.valueOf(data.getMonth()), String.valueOf(data.getMonthlyPayment()), String.valueOf(data.getInterestPayment()), String.valueOf(data.getRemainingBalance())};
                writer.writeNext(row);
            }

            System.out.println("CSV file written successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}