package com.demkom58.nmlab4;

import com.demkom58.divine.gui.GuiController;
import com.demkom58.divine.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.mariuszgromada.math.mxparser.Function;

import java.util.StringJoiner;

public class MainController extends GuiController {
    @FXML
    private TextField stepsInput;

    @FXML
    private TextField x0Input;
    @FXML
    private TextField y0Input;

    @FXML
    private TextField fromAInput;
    @FXML
    private TextField toBInput;

    @FXML
    private TextField functionInput;

    private int steps;

    private double x0;
    private double y0;

    private double start;
    private double end;

    private Function function;

    @Override
    public void init() {
        super.init();
        read();
    }

    /**
     * Метод Эйлера-Коши
     */
    @FXML
    public void euler(MouseEvent event) {
        try {
            check();
        } catch (Exception e) {
            AlertUtil.showErrorMessage(e);
            return;
        }

        final double[][] iterations = new double[steps + 1][2];

        iterations[0][0] = this.x0;
        iterations[0][1] = this.y0;

        final double h = (end - start) / steps;

        for (int i = 0; i < steps; i++) {
            final double[] itr = iterations[i + 1]; // current iteration
            final double[] pItr = iterations[i]; // previous iteration

            itr[0] = pItr[0] + h;

            final double prevF = function.calculate(pItr[0], pItr[1]);
            itr[1] = pItr[1] + h / 2d * (
                    prevF + function.calculate(itr[0], pItr[1] + h * prevF)
            );
        }

        showResult("Ейлера", iterations, steps, h);
    }

    @FXML
    public void rungeKutta(MouseEvent event) {
        try {
            check();
        } catch (Exception e) {
            AlertUtil.showErrorMessage(e);
            return;
        }

        final double[][] iterations = new double[steps + 1][2];

        iterations[0][0] = this.x0;
        iterations[0][1] = this.y0;

        final double h = (end - start) / steps;

        for (int i = 0; i < steps; i++) {
            final double[] itr = iterations[i + 1]; // current iteration
            final double[] pItr = iterations[i]; // previous iteration

            itr[0] = pItr[0] + h;

            double k1 = function.calculate(pItr[0], pItr[1]);
            double k2 = function.calculate(pItr[0] + h / 2d, pItr[1] + (h * k1) / 2d);
            double k3 = function.calculate(pItr[0] + h / 2d, pItr[1] + (h * k2) / 2d);
            double k4 = function.calculate(pItr[0] + h, pItr[1] + (h * k3));

            itr[1] = pItr[1] + h * (k1 + (2 * k2) + (2 * k3) + k4) / 6;
        }

        showResult("Рунге-Кутта", iterations, steps, h);
    }

    @FXML
    public void adams(MouseEvent event) {
        try {
            check();
        } catch (Exception e) {
            AlertUtil.showErrorMessage(e);
            return;
        }

        final double[][] iterations = new double[steps + 1][2];

        final double h = (end - start) / steps;

        // manual values filling, for loop work
        iterations[0][0] = start;
        iterations[0][1] = end;

        for (int i = 1; i < 3; i++) {
            double[] itr = iterations[i];
            double[] pItr = iterations[i - 1];

            itr[0] = h * i;
            itr[1] = pItr[1] + h * function.calculate(pItr);
        }

        for (int i = 2; i < steps; i++) {
            final double[] itr = iterations[i + 1]; // current iteration
            final double[] pItr = iterations[i]; // previous iteration

            double t = start + i * h;
            itr[0] = t + h;

            final double k1 = 23 * function.calculate(t, pItr[1]);
            final double k2 = 16 * function.calculate(t - h, iterations[i - 1][1]);
            final double k3 = 5 * function.calculate(t - 2 * h, iterations[i - 2][1]);
            itr[1] = pItr[1] + (h / 12d) * (k1 - k2 + k3);
        }

        showResult("Адамса", iterations, steps, h);
    }


    @FXML
    public void onChanged(KeyEvent event) {
        read();
    }

    private void showResult(String method, double[][] result, int steps, double step) {
        var joiner = new StringJoiner(System.lineSeparator());
        joiner.add("#\tx\t\t\ty");

        for (int i = 0; i < result.length; i++) {
            var values = result[i];
            joiner.add(String.format("%d\t%.7f\t%.7f", i, values[0], values[1]));
        }

        AlertUtil.showInfoMessage(
                "Метод " + method,
                "Кроків: " + steps + " (h = " + step + ")"
                        + "\n" + joiner.toString()
        );

        read();
    }

    private void check() throws IllegalStateException {
        if (!function.checkSyntax())
            throw new IllegalStateException("Перевірте введену функцію.\n" + function.getErrorMessage());
    }

    private void read() {
        String functionText = functionInput.getText().replace(" ", "");
        if (functionText.isBlank()) {
            final String promptText = functionInput.getPromptText().replace(" ", "");
            functionText = promptText.substring(promptText.indexOf("f(x,y)="));
        }

        if (functionText.isEmpty() || !functionText.startsWith("f(x,y)="))
            return;

        function = new Function(functionText);

        steps = FxMathFormUtil.getInt(stepsInput);

        x0 = FxMathFormUtil.getDouble(x0Input);
        y0 = FxMathFormUtil.getDouble(y0Input);

        start = FxMathFormUtil.getDouble(fromAInput);
        end = FxMathFormUtil.getDouble(toBInput);
    }
}
