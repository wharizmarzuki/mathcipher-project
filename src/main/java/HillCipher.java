import org.apache.commons.math3.linear.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HillCipher {
    public static void main(String[] args) {
        // Key matrix (must be invertible modulo 26)
        int[][] keyMatrix = {
                {2, 15},
                {11, 10}
        };

        // Plaintext
        String plaintext = "wanhariz nie boss";

        // Encrypt the plaintext
        String ciphertext = encrypt(plaintext, keyMatrix);
        System.out.println("Ciphertext: " + ciphertext);

        // Decrypt the ciphertext
        String decryptedText = decrypt(ciphertext, keyMatrix);
        System.out.println("Decrypted Text: " + decryptedText);
    }

    // Function to encrypt plaintext using Hill Cipher
    public static String encrypt(String plaintext, int[][] keyMatrix) {
        System.out.println("Plaintext: " + plaintext);
        List<Integer> plaintextNumbers = new ArrayList<>();
        for (char c : plaintext.toUpperCase().toCharArray()) {
            if (Character.isLetter(c)) {
                plaintextNumbers.add(c - 'A');
            }
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
        System.out.println("Ciphertext: " + ciphertext);
        RealMatrix inverseKeyMatrix = calculateModInverse(keyMatrix, 26);
        if (inverseKeyMatrix == null) {
            throw new IllegalArgumentException("Key matrix is not invertible modulo 26.");
        }

        System.out.println("Inverse Key Matrix: ");
        for (int i = 0; i < inverseKeyMatrix.getRowDimension(); i++) {
            System.out.println(Arrays.toString(inverseKeyMatrix.getRow(i)));
        }

        List<Integer> ciphertextNumbers = new ArrayList<>();
        for (char c : ciphertext.toUpperCase().toCharArray()) {
            if (Character.isLetter(c)) {
                ciphertextNumbers.add(c - 'A');
            }
        }

        int matrixSize = keyMatrix.length;
        int[] plaintextNumbers = new int[ciphertextNumbers.size()];
        for (int i = 0; i < ciphertextNumbers.size(); i += matrixSize) {
            for (int row = 0; row < matrixSize; row++) {
                int sum = 0;
                for (int col = 0; col < matrixSize; col++) {
                    sum += (int) Math.round(inverseKeyMatrix.getEntry(row, col)) * ciphertextNumbers.get(i + col);
                }
                plaintextNumbers[i + row] = ((sum % 26) + 26) % 26;
            }
        }
        System.out.println("Decrypted Numerical Values: " + Arrays.toString(plaintextNumbers));

        StringBuilder plaintext = new StringBuilder();
        for (int num : plaintextNumbers) {
            plaintext.append((char) ('A' + num));
        }

        // Remove padding (if any)
        while (plaintext.length() > 0 && plaintext.charAt(plaintext.length() - 1) == 'X') {
            plaintext.setLength(plaintext.length() - 1);
        }

        return plaintext.toString();
    }

    // Function to calculate the modular inverse of a matrix
    private static RealMatrix calculateModInverse(int[][] matrix, int mod) {
        int size = matrix.length;
        RealMatrix realMatrix = new Array2DRowRealMatrix(size, size);

        // Copy the input matrix to the RealMatrix
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                realMatrix.setEntry(i, j, matrix[i][j]);
            }
        }

        // Calculate determinant
        double determinant = new LUDecomposition(realMatrix).getDeterminant();
        determinant = ((determinant % mod) + mod) % mod;

        // Find modular inverse of determinant
        int determinantInverse = -1;
        for (int i = 1; i < mod; i++) {
            if ((determinant * i) % mod == 1) {
                determinantInverse = i;
                break;
            }
        }
        if (determinantInverse == -1) {
            throw new IllegalArgumentException("Determinant has no modular inverse.");
        }

        // Calculate adjugate matrix
        RealMatrix adjugateMatrix = new LUDecomposition(realMatrix).getSolver().getInverse().scalarMultiply(determinant);

        // Convert adjugate to integer modulo matrix
        RealMatrix modInverse = new Array2DRowRealMatrix(size, size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double value = determinantInverse * adjugateMatrix.getEntry(i, j);
                modInverse.setEntry(i, j, ((int) Math.round(value) % mod + mod) % mod);
            }
        }

        return modInverse;
    }
}
