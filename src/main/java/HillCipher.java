import java.util.*;

public class HillCipher {

    // Convert a character to its corresponding number (A = 0, B = 1, ..., Z = 25)
    public static int charToNum(char c) {
        return c - 'a';
    }

    // Convert a number to its corresponding character
    public static char numToChar(int n) {
        return (char) (n + 'a');
    }

    // Matrix multiplication (mod 26)
    public static int[] multiplyMatrix(int[][] matrix, int[] vector) {
        int size = matrix.length;
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = 0;
            for (int j = 0; j < size; j++) {
                result[i] = (result[i] + matrix[i][j] * vector[j]) % 26;
            }
        }
        return result; // Should return a 1D array
    }

    // Matrix inverse modulo 26 (only works for invertible matrices)
    public static int[][] inverseMatrix(int[][] matrix, int mod) {
        int size = matrix.length;
        int[][] augmented = new int[size][size * 2];
    
        // Create an augmented matrix [matrix | identity]
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                augmented[i][j] = matrix[i][j];
                augmented[i][j + size] = (i == j) ? 1 : 0;
            }
        }
    
        // Perform row reduction
        for (int i = 0; i < size; i++) {
            int pivotRow = -1;
            for (int j = i; j < size; j++) {
                if (augmented[j][i] != 0) {
                    pivotRow = j;
                    break;
                }
            }
    
            if (pivotRow == -1) {
                throw new IllegalArgumentException("Matrix is not invertible");
            }
    
            // Swap rows
            int[] temp = augmented[i];
            augmented[i] = augmented[pivotRow];
            augmented[pivotRow] = temp;
    
            // Normalize the pivot row
            int inversePivot = modInverse(augmented[i][i], mod);
            for (int j = 0; j < 2 * size; j++) {
                augmented[i][j] = (augmented[i][j] * inversePivot) % mod;
            }
    
            // Eliminate other rows
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    int factor = augmented[j][i];
                    for (int k = 0; k < 2 * size; k++) {
                        augmented[j][k] = (augmented[j][k] - factor * augmented[i][k]) % mod;
                        if (augmented[j][k] < 0) {
                            augmented[j][k] += mod;
                        }
                    }
                }
            }
        }
    
        // Extract the inverse matrix
        int[][] inverse = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                inverse[i][j] = augmented[i][j + size];
            }
        }
        return inverse;
    }
    
    // Modulo inverse
    public static int modInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1; // No modular inverse exists
    }

    // Encrypt plaintext
    public static String encrypt(String plaintext, int[][] matrix) {
        StringBuilder ciphertext = new StringBuilder();
        plaintext = plaintext.replaceAll("[^a-z]", ""); // Remove non-alphabetic characters
        int len = plaintext.length();
        // Pad plaintext if necessary
        if (len % matrix.length != 0) {
            int paddingLength = matrix.length - (len % matrix.length);
            for (int i = 0; i < paddingLength; i++) {
                plaintext += "x"; // Add 'x' as padding
            }
        }

        // Process in blocks of size 3 (for 3x3 matrix)
        for (int i = 0; i < plaintext.length(); i += matrix.length) {
            int[] block = new int[matrix.length];
            for (int j = 0; j < matrix.length; j++) {
                block[j] = charToNum(plaintext.charAt(i + j));
            }

            // Now using a 1D array result from multiplyMatrix
            int[] encryptedBlock = multiplyMatrix(matrix, block);

            for (int num : encryptedBlock) {
                ciphertext.append(numToChar(num));
            }
        }
        return ciphertext.toString();
    }

    // Decrypt ciphertext
    public static String decrypt(String ciphertext, int[][] matrix) {
        StringBuilder plaintext = new StringBuilder();
        int[][] inverse = inverseMatrix(matrix, 26);

        // Process in blocks of size 3 (for 3x3 matrix)
        for (int i = 0; i < ciphertext.length(); i += matrix.length) {
            int[] block = new int[matrix.length];
            for (int j = 0; j < matrix.length; j++) {
                block[j] = charToNum(ciphertext.charAt(i + j));
            }

            // Now using a 1D array result from multiplyMatrix
            int[] decryptedBlock = multiplyMatrix(inverse, block);

            for (int num : decryptedBlock) {
                plaintext.append(numToChar(num));
            }
        }

        // Remove padding
        return plaintext.toString().replaceAll("x+$", "");
    }
    public static String removePadding(String decryptedText) {
        return decryptedText.replaceAll("x+$", ""); // Remove trailing 'x' padding
    }

    public static void main(String[] args) {
        String plaintext = "harizisveryhandsome";

        // 3x3 matrix for encryption (example matrix)
        int[][] matrix = {
                { 18, 14, 11 },
                { 22, 25, 24 },
                { 13, 25, 4 }
        };

        // Encrypt the plaintext
        String ciphertext = encrypt(plaintext, matrix);
        System.out.println("Ciphertext: " + ciphertext);

        // Decrypt the ciphertext
        String decryptedText = decrypt(ciphertext, matrix);
        System.out.println("Decrypted Text: " + decryptedText);
    }
}
