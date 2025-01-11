import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

public class MathCrypto {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter plaintext: ");
        String plaintext = scanner.nextLine().toUpperCase().replaceAll("\\s+", "");

        int matrixSize = findLargestSquare(plaintext.length());
        int length = plaintext.length();
        System.out.println(matrixSize + " " + length + " " + length % matrixSize);
        ArrayList<ArrayList<Character>> matrix = stringToMatrix(plaintext, matrixSize - 1);

        System.out.println(matrix);
        String key = generateRandomKey(plaintext.length());
        System.out.println("Key: " + key);

        ArrayList<ArrayList<Character>> keyMatrix = keyMatrixGenerator(key, matrixSize - 1);
        System.out.println(keyMatrix);

        String cipher = encryptRowWise(keyMatrix, plaintext);
        System.out.println("Cipher: " + cipher);

        // Decrypt the cipher text
        String decryptedText = decryptRowWise(keyMatrix, cipher);
        System.out.println("Decrypted Text: " + decryptedText);
        
        scanner.close();
    }

    public static int findLargestSquare(int number) {
        int sqrt = (int) Math.sqrt(number);
        return sqrt;
    }

    public static ArrayList<ArrayList<Character>> stringToMatrix(String plainText, int matrixSize) {
        ArrayList<ArrayList<Character>> matrix = new ArrayList<>();

        int rows = (int) Math.ceil((double) plainText.length() / matrixSize);

        for (int i = 0; i < rows; i++) {
            ArrayList<Character> row = new ArrayList<>();
            for (int j = 0; j < matrixSize; j++) {
                int index = i * matrixSize + j;
                if (index < plainText.length()) {
                    row.add(plainText.charAt(index));
                } else {
                    row.add('A');
                }
            }
            matrix.add(row);
        }

        return matrix; 
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

    public static ArrayList<ArrayList<Character>> keyMatrixGenerator(String key, int matrixSize) {
        ArrayList<ArrayList<Character>> keyMatrix = new ArrayList<>();
        int step = key.length() % (matrixSize + 1); 
        step++;
        int index = 0;
        for (int i = 0; i < matrixSize; i++) {
            ArrayList<Character> row = new ArrayList<>();
            for (int j = 0; j < matrixSize; j++) {
                row.add(key.charAt(index));
                index = (index + step) % key.length();
            }
            keyMatrix.add(row);
        }
        return keyMatrix;
    }

    public static String encryptRowWise(ArrayList<ArrayList<Character>> keyMatrix, String plaintext) {
        StringBuilder cipherText = new StringBuilder();
        int keyRows = keyMatrix.size();
        int keyCols = keyMatrix.get(0).size();

        for (int i = 0; i < plaintext.length(); i += keyCols) {
            ArrayList<Integer> plaintextBlock = new ArrayList<>();

            for (int j = 0; j < keyCols; j++) {
                if (i + j < plaintext.length()) {
                    plaintextBlock.add(plaintext.charAt(i + j) - 'A');
                } else {
                    plaintextBlock.add(0);
                }
            }

            for (int row = 0; row < keyRows; row++) {
                int sum = 0;
                for (int col = 0; col < keyCols; col++) {
                    int keyVal = keyMatrix.get(row).get(col) - 'A';
                    int plainVal = plaintextBlock.get(col);
                    sum += keyVal * plainVal;
                }
                cipherText.append((char) ((sum % 26) + 'A'));
            }
        }

        return cipherText.toString();
    }

    // Decrypt using inverse matrix
    public static String decryptRowWise(ArrayList<ArrayList<Character>> keyMatrix, String cipherText) {
        ArrayList<ArrayList<Integer>> inverseKeyMatrix = matrixInverse(keyMatrix);
        StringBuilder plainText = new StringBuilder();
        int keyRows = inverseKeyMatrix.size();
        int keyCols = inverseKeyMatrix.get(0).size();

        for (int i = 0; i < cipherText.length(); i += keyCols) {
            ArrayList<Integer> cipherBlock = new ArrayList<>();

            for (int j = 0; j < keyCols; j++) {
                if (i + j < cipherText.length()) {
                    cipherBlock.add(cipherText.charAt(i + j) - 'A');
                } else {
                    cipherBlock.add(0);
                }
            }

            for (int row = 0; row < keyRows; row++) {
                int sum = 0;
                for (int col = 0; col < keyCols; col++) {
                    int keyVal = inverseKeyMatrix.get(row).get(col);
                    int cipherVal = cipherBlock.get(col);
                    sum += keyVal * cipherVal;
                }
                plainText.append((char) ((sum % 26 + 26) % 26 + 'A')); // Modulo 26 adjustment
            }
        }

        return plainText.toString();
    }
    public static ArrayList<ArrayList<Integer>> matrixInverse(ArrayList<ArrayList<Character>> matrix) {
        return MatrixUtils.inverseMatrixMod26(matrix);
    }    
}
