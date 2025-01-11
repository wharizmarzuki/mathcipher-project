import java.util.Random;

public class EchelonForm {

    // Check if two numbers are coprime
    public static boolean isCoprime(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a == 1;
    }

    // Generate an invertible matrix for mod 26
    public static double[][] generateInvertibleMatrix(int size) {
        Random random = new Random();
        double[][] matrix;

        while (true) {
            matrix = new double[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = random.nextInt(26); // Numbers between 0-25
                }
            }

            // Calculate determinant (mod 26)
            double[][] matrixCopy = new double[size][size];
            for (int i = 0; i < size; i++) {
                matrixCopy[i] = matrix[i].clone();
            }

            int determinant = (int) Math.round(determinantByRowReduction(matrixCopy)) % 26;
            if (determinant < 0) determinant += 26;

            // Check if determinant is coprime with 26
            if (isCoprime(determinant, 26)) {
                System.out.println("Generated Invertible Matrix with Determinant: " + determinant);
                break;
            }
        }

        return matrix;
    }

    // Find the determinant using row reduction
    public static double determinantByRowReduction(double[][] matrix) {
        int n = matrix.length;
        double det = 1; // Initialize determinant as 1
        int swaps = 0;  // Track number of row swaps

        for (int i = 0; i < n; i++) {
            // Find the pivot row
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(matrix[k][i]) > Math.abs(matrix[maxRow][i])) {
                    maxRow = k;
                }
            }

            // Swap rows if needed
            if (i != maxRow) {
                double[] temp = matrix[i];
                matrix[i] = matrix[maxRow];
                matrix[maxRow] = temp;
                swaps++;
            }

            // If pivot element is zero, determinant is zero
            if (matrix[i][i] == 0) {
                return 0;
            }

            // Perform row reduction
            for (int j = i + 1; j < n; j++) {
                double factor = matrix[j][i] / matrix[i][i];
                for (int k = i; k < n; k++) {
                    matrix[j][k] -= factor * matrix[i][k];
                }
            }
        }

        // Calculate determinant from diagonal elements
        for (int i = 0; i < n; i++) {
            det *= matrix[i][i];
        }

        // Adjust sign based on number of swaps
        if (swaps % 2 != 0) {
            det = -det;
        }

        det = det % 26;
        if (det < 0) {
            det = 26 + det;
        }
        return det;
    }

    // Find the minor of a matrix
    public static double[][] getMinor(double[][] matrix, int row, int col) {
        int n = matrix.length;
        double[][] minor = new double[n - 1][n - 1];
        int minorRow = 0, minorCol;

        for (int i = 0; i < n; i++) {
            if (i == row) continue;
            minorCol = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) continue;
                minor[minorRow][minorCol] = matrix[i][j];
                minorCol++;
            }
            minorRow++;
        }

        return minor;
    }

    // Calculate the adjoint of a matrix
    public static double[][] adjoint(double[][] matrix) {
        int n = matrix.length;
        double[][] adjoint = new double[n][n];

        if (n == 1) {
            adjoint[0][0] = 1;
            return adjoint;
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Get minor and calculate determinant
                double[][] minor = getMinor(matrix, i, j);
                double minorDet = determinantByRowReduction(minor);

                // Calculate cofactor and transpose (i, j) -> (j, i)
                adjoint[j][i] = Math.pow(-1, i + j) * minorDet;

                // Modulo adjustment
                adjoint[j][i] = (adjoint[j][i] % 26 + 26) % 26;
            }
        }

        return adjoint;
    }

    public static int modInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1; // No modular inverse exists
    }

    public static double[][] inverse(double[][] adjoint, double det) {
        int mod = 26;
        int detInverse = modInverse((int) det, mod);

        double[][] inverse = new double[adjoint.length][adjoint[0].length];
        for (int i = 0; i < adjoint.length; i++) {
            for (int j = 0; j < adjoint[0].length; j++) {
                inverse[i][j] = (detInverse * adjoint[i][j]) % mod;
                if (inverse[i][j] < 0) {
                    inverse[i][j] += mod; // Ensure non-negative result
                }
            }
        }
        return inverse;
    }

    // Print the matrix
    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double value : row) {
                System.out.printf("%d\t",(int) value);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int size = 3; // 4x4 matrix

        // Generate an invertible matrix
        double[][] matrix = generateInvertibleMatrix(size);

        System.out.println("Original Random Matrix:");
        printMatrix(matrix);

        // Calculate determinant
        double determinant = determinantByRowReduction(matrix);
        System.out.printf("\nDeterminant: %.0f\n", determinant);

        // Calculate adjoint
        double[][] adj = adjoint(matrix);
        System.out.println("\nAdjoint of the Matrix:");
        printMatrix(adj);

        // Calculate inverse
        double[][] inv = inverse(adj, determinant);
        System.out.println("\nInverse of the Matrix:");
        printMatrix(inv);
    }
}
