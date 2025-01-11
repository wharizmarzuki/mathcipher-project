import java.util.ArrayList;

public class MatrixUtils {

    // Compute the determinant of a 2x2 matrix mod 26
    public static int determinantMod26(int a, int b, int c, int d) {
        return ((a * d - b * c) % 26 + 26) % 26;
    }

    // Find the modular multiplicative inverse of a number mod 26
    public static int modInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        throw new ArithmeticException("No modular inverse exists");
    }

    // Invert a 2x2 matrix mod 26
    public static ArrayList<ArrayList<Integer>> inverseMatrixMod26(ArrayList<ArrayList<Character>> matrix) {
        int a = matrix.get(0).get(0) - 'A';
        int b = matrix.get(0).get(1) - 'A';
        int c = matrix.get(1).get(0) - 'A';
        int d = matrix.get(1).get(1) - 'A';

        int det = determinantMod26(a, b, c, d);

        if (gcd(det, 26) != 1) {
            throw new ArithmeticException("Matrix is not invertible mod 26");
        }

        int detInv = modInverse(det, 26);

        ArrayList<ArrayList<Integer>> inverse = new ArrayList<>();
        ArrayList<Integer> row1 = new ArrayList<>();
        ArrayList<Integer> row2 = new ArrayList<>();

        row1.add((detInv * d % 26 + 26) % 26); // Swap d and a, adjust sign
        row1.add((detInv * (-b + 26) % 26 + 26) % 26);
        row2.add((detInv * (-c + 26) % 26 + 26) % 26);
        row2.add((detInv * a % 26 + 26) % 26);

        inverse.add(row1);
        inverse.add(row2);

        return inverse;
    }

    // Calculate GCD (Greatest Common Divisor)
    public static int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }

    public static void main(String[] args) {
        ArrayList<ArrayList<Character>> keyMatrix = new ArrayList<>();
        ArrayList<Character> row1 = new ArrayList<>();
        ArrayList<Character> row2 = new ArrayList<>();

        row1.add('A'); // Example 2x2 matrix
        row1.add('B');
        row2.add('C');
        row2.add('D');

        keyMatrix.add(row1);
        keyMatrix.add(row2);

        try {
            ArrayList<ArrayList<Integer>> inverseMatrix = inverseMatrixMod26(keyMatrix);
            System.out.println("Inverse Matrix Mod 26:");
            for (ArrayList<Integer> row : inverseMatrix) {
                System.out.println(row);
            }
        } catch (ArithmeticException e) {
            System.out.println(e.getMessage());
        }
    }
}
