// Save this as PolynomialSolver.java

import com.google.gson.*;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class PolynomialSolverExact {
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
        int k = keys.get("k").getAsInt();

        if (k < 3) {
            System.out.println("Need at least 3 points to solve quadratic polynomial");
            return;
        }

        List<BigInteger> xList = new ArrayList<>();
        List<BigInteger> yList = new ArrayList<>();

        int count = 0;
        for (Map.Entry<String, JsonElement> entry : rootObj.entrySet()) {
            if (entry.getKey().equals("keys")) continue;
            if (count == 3) break;
            BigInteger x = new BigInteger(entry.getKey());
            JsonObject valObj = entry.getValue().getAsJsonObject();
            int base = Integer.parseInt(valObj.get("base").getAsString());
            String valueStr = valObj.get("value").getAsString();
            BigInteger yBig = new BigInteger(valueStr, base);
            xList.add(x);
            yList.add(yBig);
            count++;
        }

        BigInteger[][] A = new BigInteger[3][3];
        BigInteger[] Y = new BigInteger[3];
        for (int i = 0; i < 3; i++) {
            BigInteger x = xList.get(i);
            A[i][0] = x.multiply(x);
            A[i][1] = x;
            A[i][2] = BigInteger.ONE;
            Y[i] = yList.get(i);
        }

        BigInteger detA = determinant3x3(A);
        if (detA.equals(BigInteger.ZERO)) throw new RuntimeException("Matrix is singular");

        BigInteger[][] A_a = replaceColumn(A, Y, 0);
        BigInteger[][] A_b = replaceColumn(A, Y, 1);
        BigInteger[][] A_c = replaceColumn(A, Y, 2);

        BigInteger detA_a = determinant3x3(A_a);
        BigInteger detA_b = determinant3x3(A_b);
        BigInteger detA_c = determinant3x3(A_c);

        BigInteger c = detA_c.divide(detA);
        System.out.println("Constant term c = " + c);
    }

    public static BigInteger determinant3x3(BigInteger[][] M) {
        return M[0][0].multiply(M[1][1]).multiply(M[2][2])
                .add(M[0][1].multiply(M[1][2]).multiply(M[2][0]))
                .add(M[0][2].multiply(M[1][0]).multiply(M[2][1]))
                .subtract(M[0][2].multiply(M[1][1]).multiply(M[2][0]))
                .subtract(M[0][0].multiply(M[1][2]).multiply(M[2][1]))
                .subtract(M[0][1].multiply(M[1][0]).multiply(M[2][2]));
    }

    public static BigInteger[][] replaceColumn(BigInteger[][] M, BigInteger[] Y, int col) {
        BigInteger[][] result = new BigInteger[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = (j == col) ? Y[i] : M[i][j];
            }
        }
        return result;
    }
}
