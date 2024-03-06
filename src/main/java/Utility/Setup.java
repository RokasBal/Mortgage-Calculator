package Utility;

import com.example.mortgagecalculator.Controller;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class Setup extends Controller {
    public void initializeSliders(Slider filterStart, Slider filterEnd) {
        filterStart.setMin(1);
        filterStart.setValue(1);
        filterEnd.setMin(1);
    }

    public void formatToFloat(TextField textField) {
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

    public void formatToInt(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(new UnaryOperator<TextFormatter.Change>() {
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
    }

    public void calculatePostpone() {
//        Controller.totalDelay = (delayYearEndInput - delayYearStartInput) * 12 + (delayMonthEndInput - delayMonthStartInput);
//        delayStartMonth = delayYearStartInput * 12 + delayMonthStartInput;
//        delayEndMonth = delayYearEndInput * 12 + delayMonthEndInput;
    }
}
