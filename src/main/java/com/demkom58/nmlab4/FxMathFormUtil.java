package com.demkom58.nmlab4;

import javafx.scene.control.TextField;
import org.mariuszgromada.math.mxparser.Expression;

public final class FxMathFormUtil {

    public static int getInt(TextField input) {
        String text = input.getText();
        if (text.isBlank())
            return Integer.parseInt(input.getPromptText());

        var expression = new Expression(text);
        if (!expression.checkSyntax())
            return Integer.MIN_VALUE;

        return (int) expression.calculate();
    }

    public static double getDouble(TextField input) {
        String text = input.getText();
        if (text.isBlank())
            return Double.parseDouble(input.getPromptText());

        var expression = new Expression(text);
        if (!expression.checkSyntax())
            return Double.MIN_VALUE;

        return expression.calculate();
    }

    public static float getFloat(TextField input) {
        String text = input.getText();
        if (text.isBlank())
            return Float.parseFloat(input.getPromptText());

        var expression = new Expression(text);
        if (!expression.checkSyntax())
            return Float.MIN_VALUE;

        return (float) expression.calculate();
    }

}
