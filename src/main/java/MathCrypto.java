import java.security.SecureRandom;
import java.util.*;
import org.apache.commons.math3.linear.*;

public class MathCrypto {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter plaintext: ");
        String plaintext = scanner.nextLine().toUpperCase().replaceAll("\\s+", "");

        int matrixSize = findLargestSquare(plaintext.length());

        int[][] matrix = MatrixGenerator.generateInvertibleMatrix(matrixSize, 26);
        System.out.println("Generated Key Matrix:");
        printMatrix(matrix);

        String key = generateRandomKey(plaintext.length());
        System.out.println("Generated Random OTP Key: " + key);

        String cipherText = OTPEncryption.performOtpEncryption(plaintext, key);
        System.out.println("Encrypted Text: " + cipherText);

        String ciphertext2 = encrypt(cipherText, matrix);
        System.out.println("Ciphertext: " + ciphertext2);


        String decryptedText = decrypt(ciphertext2, matrix);
        System.out.println("Decrypted Plaintext: " + decryptedText);

        String decryptedText2 = OTPEncryption.performOtpDecryption(decryptedText, key);
        System.out.println("Decrypted Text: " + decryptedText2);


        scanner.close();
    }

    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
    }

    public static int findLargestSquare(int number) {
        return (int) Math.sqrt(number);
    }

    public static String encrypt(String plaintext, int[][] keyMatrix) {
        System.out.println("Plaintext: " + plaintext);
        List<Integer> plaintextNumbers = new ArrayList<>();
        for (char c : plaintext.toCharArray()) {
            plaintextNumbers.add(c - 'A');
        }
        System.out.println("Plaintext Numerical Values: " + plaintextNumbers);

        int matrixSize = keyMatrix.length;
        while (plaintextNumbers.size() % matrixSize != 0) {
            plaintextNumbers.add('X' - 'A'); // Padding with 'X' (23)
        }

        int[] ciphertextNumbers = new int[plaintextNumbers.size()];
        for (int i = 0; i < plaintextNumbers.size(); i += matrixSize) {
            for (int row = 0; row < matrixSize; row++) {
                int sum = 0;
                for (int col = 0; col < matrixSize; col++) {
                    sum += keyMatrix[row][col] * plaintextNumbers.get(i + col);
                }
                ciphertextNumbers[i + row] = ((sum % 26) + 26) % 26; // Apply modulo 26
            }
        }
        System.out.println("Ciphertext Numerical Values: " + Arrays.toString(ciphertextNumbers));

        StringBuilder ciphertext = new StringBuilder();
        for (int num : ciphertextNumbers) {
            ciphertext.append((char) ('A' + num));
        }
        return ciphertext.toString();
    }

    public static String decrypt(String ciphertext, int[][] keyMatrix) {
        int matrixSize = keyMatrix.length;

        if (matrixSize == 2) {
            // Compute the determinant of the 2x2 matrix
            int determinant = (keyMatrix[0][0] * keyMatrix[1][1] - keyMatrix[0][1] * keyMatrix[1][0]) % 26;
            int determinantInverse = modularInverse(determinant, 26);

            if (determinantInverse == -1) {
                throw new IllegalArgumentException("Key matrix is not invertible modulo 26.");
            }

            // For 2x2 matrix inverse
            int[][] inverseMatrix = new int[2][2];
            inverseMatrix[0][0] = (keyMatrix[1][1] * determinantInverse) % 26;
            inverseMatrix[0][1] = (-keyMatrix[0][1] * determinantInverse) % 26;
            inverseMatrix[1][0] = (-keyMatrix[1][0] * determinantInverse) % 26;
            inverseMatrix[1][1] = (keyMatrix[0][0] * determinantInverse) % 26;

            // Apply modulo 26 to keep positive values
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    inverseMatrix[i][j] = (inverseMatrix[i][j] + 26) % 26;
                }
            }

            // Debug: Print the inverse matrix for verification
            System.out.println("Inverse Matrix:");
            printMatrix(inverseMatrix);

            // Decrypt the ciphertext
            List<Integer> ciphertextNumbers = new ArrayList<>();
            for (char c : ciphertext.toCharArray()) {
                ciphertextNumbers.add(c - 'A');
            }

            StringBuilder plaintext = new StringBuilder();
            for (int i = 0; i < ciphertextNumbers.size(); i += matrixSize) {
                for (int row = 0; row < matrixSize; row++) {
                    int sum = 0;
                    for (int col = 0; col < matrixSize; col++) {
                        sum += inverseMatrix[row][col] * ciphertextNumbers.get(i + col);
                    }
                    plaintext.append((char) (((sum % 26) + 26) % 26 + 'A'));
                }
            }

            return plaintext.toString();
        }
        // Compute the determinant modulo 26
        int determinant = MatrixGenerator.calculateDeterminant(keyMatrix, 26);
        int determinantInverse = modularInverse(determinant, 26);

        if (determinantInverse == -1) {
            throw new IllegalArgumentException("Key matrix is not invertible modulo 26.");
        }

        // Compute the adjugate of the key matrix
        int[][] adjugateMatrix = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                adjugateMatrix[i][j] = MatrixGenerator.cofactor(keyMatrix, i, j, 26);
            }
        }

        // Transpose the adjugate matrix to get the inverse matrix
        int[][] inverseMatrix = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                inverseMatrix[j][i] = ((adjugateMatrix[i][j] * determinantInverse) % 26 + 26) % 26;
            }
        }

        // Debug: Print the inverse matrix
        System.out.println("Inverse Matrix:");
        printMatrix(inverseMatrix);

        // Decrypt the ciphertext
        List<Integer> ciphertextNumbers = new ArrayList<>();
        for (char c : ciphertext.toCharArray()) {
            ciphertextNumbers.add(c - 'A');
        }

        StringBuilder plaintext = new StringBuilder();
        for (int i = 0; i < ciphertextNumbers.size(); i += matrixSize) {
            for (int row = 0; row < matrixSize; row++) {
                int sum = 0;
                for (int col = 0; col < matrixSize; col++) {
                    sum += inverseMatrix[row][col] * ciphertextNumbers.get(i + col);
                }
                plaintext.append((char) (((sum % 26) + 26) % 26 + 'A'));
            }
        }

        return plaintext.toString();
    }

    public static int modularInverse(int a, int mod) {
        int t = 0, newT = 1;
        int r = mod, newR = a;

        while (newR != 0) {
            int quotient = r / newR;
            int tempT = t;
            t = newT;
            newT = tempT - quotient * newT;

            int tempR = r;
            r = newR;
            newR = tempR - quotient * newR;
        }

        if (r > 1) return -1; // No modular inverse exists
        if (t < 0) t += mod;

        return t;
    }

    public static int[][] convertToIntMatrix(RealMatrix matrix) {
        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();
        int[][] intMatrix = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                intMatrix[i][j] = (int) matrix.getEntry(i, j);
            }
        }
        return intMatrix;
    }
    public static double[][] convertToDoubleArray(int[][] intArray) {
        int rows = intArray.length;
        int cols = intArray[0].length;
        double[][] doubleArray = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                doubleArray[i][j] = intArray[i][j];
            }
        }

        return doubleArray;
    }
    public static String generateRandomKey(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            key.append(alphabet.charAt(index));
        }

        return key.toString();
    }
}

class MatrixGenerator {

    public static int[][] generateInvertibleMatrix(int size, int mod) {
        Random random = new Random();
        int[][] matrix = new int[size][size];

        while (true) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = random.nextInt(mod);
                }
            }

            int determinant = calculateDeterminant(matrix, mod);

            if (determinant != 0 && gcd(determinant, mod) == 1) {
                return matrix;
            }
        }
    }

    public static int calculateDeterminant(int[][] matrix, int mod) {
        int size = matrix.length;
        if (size == 2) {
            int determinant = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
            return ((determinant % mod) + mod) % mod;
        }

        int determinant = 0;
        for (int col = 0; col < size; col++) {
            determinant += matrix[0][col] * cofactor(matrix, 0, col, mod);
            determinant %= mod;
        }
        return (determinant + mod) % mod;
    }

    public static int cofactor(int[][] matrix, int row, int col, int mod) {
        int[][] minor = getMinor(matrix, row, col);
        int determinant = calculateDeterminant(minor, mod);
        int cofactor = (int) Math.pow(-1, row + col) * determinant;
        return ((cofactor % mod) + mod) % mod;
    }

    public static int[][] getMinor(int[][] matrix, int row, int col) {
        int size = matrix.length;
        int[][] minor = new int[size - 1][size - 1];
        int r = 0, c = 0;

        for (int i = 0; i < size; i++) {
            if (i == row) continue;
            c = 0;
            for (int j = 0; j < size; j++) {
                if (j == col) continue;
                minor[r][c++] = matrix[i][j];
            }
            r++;
        }
        return minor;
    }

    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}

class AdjugateMatrix {

    public static RealMatrix adjugate(RealMatrix matrix) {
        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();

        RealMatrix cofactorMatrix = MatrixUtils.createRealMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                RealMatrix minor = getMinor(matrix, i, j);
                double cofactor = Math.pow(-1, i + j) * determinant(minor);
                cofactorMatrix.setEntry(i, j, cofactor);
            }
        }
        return cofactorMatrix.transpose();
    }

    private static RealMatrix getMinor(RealMatrix matrix, int row, int col) {
        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();
        RealMatrix minor = MatrixUtils.createRealMatrix(rows - 1, cols - 1);

        int r = 0;
        for (int i = 0; i < rows; i++) {
            if (i == row) continue;
            int c = 0;
            for (int j = 0; j < cols; j++) {
                if (j == col) continue;
                minor.setEntry(r, c, matrix.getEntry(i, j));
                c++;
            }
            r++;
        }
        return minor;
    }

    private static double determinant(RealMatrix matrix) {
        return new LUDecomposition(matrix).getDeterminant();
    }
}
class OTPEncryption {

    // Perform OTP Encryption on a string
    public static String performOtpEncryption(String text, String key) {
        // Ensure uppercase input
        text = text.toUpperCase();
        key = key.toUpperCase();

        // Adjust key length to match the text length
        String adjustedKey = key.length() < text.length()
                ? key.repeat((text.length() / key.length()) + 1).substring(0, text.length())
                : key.substring(0, text.length());

        return stringEncryption(text, adjustedKey);
    }

    // Perform OTP Decryption on a string
    public static String performOtpDecryption(String cipherText, String key) {
        // Ensure uppercase input
        cipherText = cipherText.toUpperCase();
        key = key.toUpperCase();

        // Adjust key length to match the ciphertext length
        String adjustedKey = key.length() < cipherText.length()
                ? key.repeat((cipherText.length() / key.length()) + 1).substring(0, cipherText.length())
                : key.substring(0, cipherText.length());

        return stringDecryption(cipherText, adjustedKey);
    }

    // OTP Encryption for a string (each character shifted by the corresponding key character)
    public static String stringEncryption(String text, String key) {
        StringBuilder cipherText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            int cipher = text.charAt(i) - 'A' + key.charAt(i) - 'A';
            cipherText.append((char) ((cipher % 26) + 'A'));
        }
        return cipherText.toString();
    }

    // OTP Decryption for a string (reverse the encryption process)
    public static String stringDecryption(String cipherText, String key) {
        StringBuilder plainText = new StringBuilder();
        for (int i = 0; i < cipherText.length(); i++) {
            int cipherChar = cipherText.charAt(i) - 'A';
            int keyChar = key.charAt(i) - 'A';
            int plainChar = (cipherChar - keyChar + 26) % 26; // Ensure non-negative result
            plainText.append((char) (plainChar + 'A'));
        }
        return plainText.toString();
    }
}

