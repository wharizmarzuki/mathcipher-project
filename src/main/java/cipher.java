import java.security.SecureRandom;
import java.util.*;

public class cipher {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input Plaintext
        System.out.print("Enter plaintext: ");
        String plaintext = scanner.nextLine().toUpperCase().replaceAll("\\s+", "");
        System.out.println("Processed plaintext (spaces removed): " + plaintext);

        // Generate a random key matching plaintext length
        String key = generateRandomKey(plaintext.length());
        System.out.println("Generated Random OTP Key: " + key);

        // Matrix Formation
        int matrixSize = calculateMatrixSize(plaintext.length());
        System.out.println("Matrix size: " + matrixSize + "x" + matrixSize);

        ArrayList<ArrayList<Character>> plainTextMatrix = createMatrix(plaintext, matrixSize);
        ArrayList<ArrayList<Character>> keyMatrix = createPatternMatrix(matrixSize, plaintext);

        System.out.println("Plaintext Matrix:");
        print2DMatrix(plainTextMatrix);

        System.out.println("Key Matrix:");
        print2DMatrix(keyMatrix);

        ArrayList<ArrayList<Character>> result = multiplyMatrices(plainTextMatrix, keyMatrix, plainTextMatrix.size(),
                matrixSize);
        System.out.println("Matrix Multiplication Result:");
        print2DMatrix(result);

        convertToHexAndBinary(result);

        ArrayList<ArrayList<Character>> result2 = performOtpEncryption(result, key);
        String cipherText = matrixToTexString(result2);
        System.out.println("Encrypted Text:" + cipherText);

        System.out.println("Decryption\nKey: " + key);
        ArrayList<ArrayList<Character>> decryptedMatrix = performOtpDecryption(cipherText, key, plainTextMatrix.size(),
                matrixSize);
        print2DMatrix(decryptedMatrix);

        // ArrayList<ArrayList<Character>> decryptedMatrix2 =
        // decryptMatrix(decryptedMatrix, keyMatrix);
        // print2DMatrix(decryptedMatrix2);
    }

    // Random Key Generator
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

    public static int calculateMatrixSize(int length) {
        if (length < 9)
            return 1;
        if (length < 16)
            return 2;
        if (length < 25)
            return 3;

        int cyclePosition = (length - 25) % 28;
        if (cyclePosition < 4)
            return 1;
        if (cyclePosition < 9)
            return 2;
        if (cyclePosition < 16)
            return 3;
        if (cyclePosition < 25)
            return 4;

        return 4;
    }

    public static ArrayList<ArrayList<Character>> createMatrix(String text, int size) {
        ArrayList<ArrayList<Character>> matrix = new ArrayList<>();
        for (int i = 0; i < text.length(); i += size) {
            ArrayList<Character> row = new ArrayList<>();
            for (int j = 0; j < size && (i + j) < text.length(); j++) {
                row.add(text.charAt(i + j));
            }
            while (row.size() < size)
                row.add('A'); // Fill with 'A' if needed
            matrix.add(row);
        }
        return matrix;
    }

    public static ArrayList<ArrayList<Character>> createPatternMatrix(int size, String plaintext) {
        ArrayList<ArrayList<Character>> matrix = new ArrayList<>();
        int step = size;
        int index = 0;

        for (int i = 0; i < size; i++) {
            ArrayList<Character> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (index < plaintext.length()) {
                    row.add(plaintext.charAt(index));
                    index += step;
                } else {
                    row.add('-');
                }
            }
            matrix.add(row);
        }
        return matrix;
    }

    public static void print2DMatrix(ArrayList<ArrayList<Character>> matrix) {
        for (ArrayList<Character> row : matrix) {
            for (Character ch : row) {
                System.out.print(ch + " ");
            }
            System.out.println();
        }
    }

    public static String matrixToTexString(ArrayList<ArrayList<Character>> matrix) {
        StringBuilder sb = new StringBuilder();
        for (ArrayList<Character> row : matrix) {
            for (Character ch : row) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static ArrayList<ArrayList<Character>> multiplyMatrices( ArrayList<ArrayList<Character>> plaintextMatrix, ArrayList<ArrayList<Character>> keyMatrix, int plaintextRows, int keyColumns) { 
        ArrayList<ArrayList<Character>> resultMatrix = new ArrayList<>();
        for (int i = 0; i < plaintextRows; i++) { 
            ArrayList<Character> resultRow = new ArrayList<>();
            for (int j = 0; j < keyColumns; j++) { int sum = 0;
                for (int k = 0; k < keyMatrix.size(); k++) {
                    sum += (plaintextMatrix.get(i).get(k) - 'A') * (keyMatrix.get(k).get(j) - 'A');
                } 
                char resultChar = (char) ((sum % 26 + 26) % 26 + 'A');
                resultRow.add(resultChar);
            }
            resultMatrix.add(resultRow);
        } return resultMatrix;
    }
    
    public static ArrayList<ArrayList<Character>> convertToHexAndBinary(ArrayList<ArrayList<Character>> resultMatrix) {
        for (ArrayList<Character> row : resultMatrix) {
            for (Character ch : row) {
                int asciiValue = (int) ch;
                String hexValue = Integer.toHexString(asciiValue);
                String binaryValue = String.format("%8s", Integer.toBinaryString(asciiValue)).replace(' ', '0');
                // System.out.println("Character: " + ch + " | Hex: " + hexValue + " | Binary: "
                // + binaryValue);
            }
        }
        return resultMatrix;
    }

    public static ArrayList<ArrayList<Character>> performOtpEncryption(
            ArrayList<ArrayList<Character>> resultMatrix, String key) {

        ArrayList<ArrayList<Character>> otpResult = new ArrayList<>();

        for (ArrayList<Character> row : resultMatrix) {
            StringBuilder rowText = new StringBuilder();
            for (Character ch : row) {
                rowText.append(ch);
            }

            String adjustedKey = key.length() < rowText.length()
                    ? key.repeat((rowText.length() / key.length()) + 1).substring(0, rowText.length())
                    : key.substring(0, rowText.length());

            String encryptedRow = stringEncryption(rowText.toString().toUpperCase(),
                    adjustedKey.toUpperCase());

            ArrayList<Character> encryptedCharRow = new ArrayList<>();
            for (char ch : encryptedRow.toCharArray()) {
                encryptedCharRow.add(ch);
            }

            otpResult.add(encryptedCharRow);
        }

        return otpResult;
    }

    public static String stringEncryption(String text, String key) {
        StringBuilder cipherText = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            int cipher = text.charAt(i) - 'A' + key.charAt(i) - 'A';
            cipherText.append((char) ((cipher % 26) + 'A'));
        }
        return cipherText.toString();
    }

    public static ArrayList<ArrayList<Character>> performOtpDecryption(String cipherText, String key, int rows,
            int cols) {
        // Ensure uppercase input
        cipherText = cipherText.toUpperCase();
        key = key.toUpperCase();

        ArrayList<ArrayList<Character>> decryptedMatrix = new ArrayList<>();
        int index = 0;

        // Process row by row to match OTP encryption logic
        for (int i = 0; i < rows; i++) {
            StringBuilder rowCipherText = new StringBuilder();
            for (int j = 0; j < cols; j++) {
                if (index < cipherText.length()) {
                    rowCipherText.append(cipherText.charAt(index++));
                }
            }

            // Adjust the key for the current row
            String adjustedKey = key.length() < rowCipherText.length()
                    ? key.repeat((rowCipherText.length() / key.length()) + 1).substring(0, rowCipherText.length())
                    : key.substring(0, rowCipherText.length());

            // Decrypt the current row
            String decryptedRow = stringDecryption(rowCipherText.toString(), adjustedKey);

            // Add to matrix
            ArrayList<Character> row = new ArrayList<>();
            for (char ch : decryptedRow.toCharArray()) {
                row.add(ch);
            }
            decryptedMatrix.add(row);
        }

        return decryptedMatrix;
    }

    // OTP Decryption for each row
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
