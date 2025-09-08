// Save this as PolynomialSolver.java

import com.google.gson.*;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class PolynomialSolver {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            jsonBuilder.append(line);
        }
        String jsonString = jsonBuilder.toString();

        JsonObject rootObj = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonObject keys = rootObj.getAsJsonObject("keys");
        int n = keys.get("n").getAsInt();
        int k = keys.get("k").getAsInt();

        if (k < 3) {
            System.out.println("Need at least 3 points to solve quadratic polynomial");
            return;
        }

        List<Integer> xList = new ArrayList<>();
        List<Double> yList = new ArrayList<>();

        int count = 0;
        for (Map.Entry<String, JsonElement> entry : rootObj.entrySet()) {
            if (entry.getKey().equals("keys")) continue;
            if (count == 3) break;

            int x = Integer.parseInt(entry.getKey());
            JsonObject valObj = entry.getValue().getAsJsonObject();
            int base = Integer.parseInt(valObj.get("base").getAsString());
            String valueStr = valObj.get("value").getAsString();

            BigInteger yBig = new BigInteger(valueStr, base);
            double y = yBig.doubleValue();

            xList.add(x);
            yList.add(y);

            count++;
        }

        double[][] A = new double[3][3];
        double[] Y = new double[3];

        for (int i = 0; i < 3; i++) {
            double x = xList.get(i);
            A[i][0] = x * x;
            A[i][1] = x;
            A[i][2] = 1;
            Y[i] = yList.get(i);
        }

        double[] coef = gaussianSolve(A, Y);

        System.out.printf("Constant term c = %.6f\n", coef[2]);
    }

    public static double[] gaussianSolve(double[][] A, double[] b) {
        int n = b.length;

        for (int p = 0; p < n; p++) {
            int max = p;
            for (int i = p + 1; i < n; i++) {
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }

            double[] tempRow = A[p];
            A[p] = A[max];
            A[max] = tempRow;

            double tempVal = b[p];
            b[p] = b[max];
            b[max] = tempVal;

            if (Math.abs(A[p][p]) < 1e-15) {
                throw new RuntimeException("Matrix is singular or near singular");
            }

            for (int i = p + 1; i < n; i++) {
                double alpha = A[i][p] / A[p][p];
                b[i] -= alpha * b[p];
                for (int j = p; j < n; j++) {
                    A[i][j] -= alpha * A[p][j];
                }
            }
        }

        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = b[i];
            for (int j = i + 1; j < n; j++) {
                sum -= A[i][j] * x[j];
            }
            x[i] = sum / A[i][i];
        }
        return x;
    }
}
