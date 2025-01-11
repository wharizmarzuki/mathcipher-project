import java.util.*;

/**
 * Playfair Cipher Implementation
 */
public class playfaircipher {

    public static void main(String[] args) {
        String key, plaintext, ciphertext = "", decyrpttext = "";

        // Using try-with-resources to automatically close the scanner
        try (Scanner scanner = new Scanner(System.in)) {

            // Input the key and plaintext
            System.out.print("Input Key: ");
            key = scanner.nextLine().toLowerCase();

            System.out.print("Input Plaintext: ");
            plaintext = scanner.nextLine().toLowerCase();

            // Step 1: Process the key and remove duplicates
            StringBuilder uniqueKey = new StringBuilder();
            Set<Character> seen = new HashSet<>();
            boolean asteriskAdded = false;

            // Process key characters, remove duplicates and handle 'i' and 'j'
            for (char c : key.toCharArray()) {
                if (!seen.contains(c)) {
                    if (c != ' ') {
                        if (c != 'i' && c != 'j') {
                            seen.add(c);
                            uniqueKey.append(c);
                        } else if (!asteriskAdded) {
                            // Use '*' to replace both 'i' and 'j'
                            seen.add('*');
                            uniqueKey.append('*');
                            asteriskAdded = true;
                        }
                    }
                }
            }

            // Step 2: Add remaining characters of the alphabet (excluding 'j')
            String alphabet = "abcdefgh*klmnopqrstuvwxyz"; // Using '*' for 'i' and 'j'
            for (char c : alphabet.toCharArray()) {
                if (!seen.contains(c)) {
                    uniqueKey.append(c);
                }
            }

            // Step 3: Fill the 5x5 table with the unique characters from the key and
            // alphabet
            List<List<Character>> cipherTable = new ArrayList<>();
            int index = 0;
            for (int row = 0; row < 5; row++) {
                List<Character> currentRow = new ArrayList<>();
                for (int col = 0; col < 5; col++) {
                    currentRow.add(uniqueKey.charAt(index++));
                }
                cipherTable.add(currentRow);
            }

            // Step 4: Print the 5x5 cipher table
            System.out.println("5x5 Cipher Table:");
            for (List<Character> row : cipherTable) {
                for (char c : row) {
                    System.out.print(c + " ");
                }
                System.out.println();
            }

            // Step 5: Split the plaintext into digraphs (2-character chunks)
            List<String> digraphs = new ArrayList<>();
            plaintext = plaintext.replace(" ", ""); // Remove spaces from the plaintext

            for (int i = 0; i < plaintext.length(); i += 2) {
                String digraph = "";
                char first = plaintext.charAt(i);

                // Replace 'i' or 'j' with '*'
                if (first == 'i' || first == 'j')
                    first = '*';

                char second;
                if (i + 1 < plaintext.length()) {
                    second = plaintext.charAt(i + 1);
                    if (second == 'i' || second == 'j')
                        second = '*';

                    // Handle repeating characters
                    if (first == second) {
                        digraph = "" + first + 'z';
                        i--; // Reprocess the second character
                    } else {
                        digraph = "" + first + second;
                    }
                } else {
                    // If it's the last single character, pad with 'z'
                    digraph = "" + first + 'z';
                }

                digraphs.add(digraph);
            }

            // Step 6: Print the digraphs
            System.out.println("\nPlaintext Split into Digraphs:");
            for (String digraph : digraphs) {
                System.out.println(digraph);
            }

            // Step 7: Encrypt the digraphs
            for (String digraph : digraphs) {
                // Compare first and second character in each digraph (pair)
                char firstChar = digraph.charAt(0);
                char secondChar = digraph.charAt(1);

                int[] firstCharLocation = findCharLocation(cipherTable, firstChar);
                int[] secondCharLocation = findCharLocation(cipherTable, secondChar);

                System.out.println("Current Pair: \t" + firstChar + secondChar);

                // Check for null values (character not found in the table)
                if (firstCharLocation == null || secondCharLocation == null) {
                    System.out.println("Error: Character not found in the cipher table");
                    return; // Exit if any character is not found
                }

                if (firstCharLocation[0] == secondCharLocation[0]) { // Same row
                    // Shift both characters to the right in the same row
                    firstChar = cipherTable.get(firstCharLocation[0]).get((firstCharLocation[1] + 1) % 5); // Next
                                                                                                           // column,
                                                                                                           // wrap
                                                                                                           // around
                    secondChar = cipherTable.get(secondCharLocation[0]).get((secondCharLocation[1] + 1) % 5); // Next
                                                                                                              // column,
                                                                                                              // wrap
                                                                                                              // around
                    System.out.println("Same Row: \t" + firstChar + secondChar + "\n");
                } else if (firstCharLocation[1] == secondCharLocation[1]) { // Same column
                    // Shift both characters down the column
                    firstChar = cipherTable.get((firstCharLocation[0] + 1) % 5).get(firstCharLocation[1]); // Next row,
                                                                                                           // wrap
                                                                                                           // around
                    secondChar = cipherTable.get((secondCharLocation[0] + 1) % 5).get(secondCharLocation[1]); // Next
                                                                                                              // row,
                                                                                                              // wrap
                                                                                                              // around
                    System.out.println("Same Column: \t" + firstChar + secondChar + "\n");
                } else { // Different row and column (rectangle case)
                    // Apply the rectangle rule: swap columns
                    firstChar = cipherTable.get(firstCharLocation[0]).get(secondCharLocation[1]); // Same row as
                                                                                                  // firstChar, same
                                                                                                  // column as
                                                                                                  // secondChar
                    secondChar = cipherTable.get(secondCharLocation[0]).get(firstCharLocation[1]); // Same row as
                                                                                                   // secondChar, same
                                                                                                   // column as
                                                                                                   // firstChar
                    System.out.println("Overlay Case: \t" + firstChar + secondChar + "\n");
                }
                ciphertext += firstChar;
                ciphertext += secondChar;
            }
            System.out.println("Ciphertext: " + ciphertext);
            // Step 5: Split the plaintext into digraphs (2-character chunks)
            List<String> encdigraphs = new ArrayList<>();
            for (int i = 0; i < ciphertext.length(); i += 2) {
                String encdigraph;

                // Handle case where last character is single and append 'z'
                if (i + 1 >= ciphertext.length()) {
                    encdigraph = ciphertext.substring(i) + "z";
                } else {
                    char first = ciphertext.charAt(i);
                    char second = ciphertext.charAt(i + 1);
                    // Replace 'i' or 'j' with '*'
                    if (first == 'i' || first == 'j') {
                        first = '*';
                    }
                    if (second == 'i' || second == 'j') {
                        second = '*';
                    }
                    // If both characters are the same, add 'z' to the second character
                    if (first == second) {
                        encdigraph = "" + first + 'z';
                        i--; // Reprocess the current second character
                    } else {
                        encdigraph = "" + first + second;
                    }
                }
                encdigraphs.add(encdigraph);
            }

            // Step 6: Print the digraphs
            System.out.println("\nCippher text Split into Digraphs:");
            for (String encdigraph : encdigraphs) {
                System.out.println(encdigraph);
            }

            // Step 7: Encrypt the digraphs
            for (String encdigraph : encdigraphs) {
                // Compare first and second character in each digraph (pair)
                char firstChar = encdigraph.charAt(0);
                char secondChar = encdigraph.charAt(1);

                int[] firstCharLocation = findCharLocation(cipherTable, firstChar);
                int[] secondCharLocation = findCharLocation(cipherTable, secondChar);

                System.out.println("Current Pair: \t" + firstChar + secondChar);

                // Check for null values (character not found in the table)
                if (firstCharLocation == null || secondCharLocation == null) {
                    System.out.println("Error: Character not found in the cipher table");
                    return; // Exit if any character is not found
                }

                if (firstCharLocation[0] == secondCharLocation[0]) { // Same row
                    // Shift both characters to the right in the same row
                    firstChar = cipherTable.get(firstCharLocation[0]).get((firstCharLocation[1] - 1 + 5) % 5); // Next
                                                                                                               // column,
                                                                                                               // wrap
                                                                                                               // around
                    secondChar = cipherTable.get(secondCharLocation[0]).get((secondCharLocation[1] - 1 + 5) % 5); // Next
                                                                                                                  // column,
                                                                                                                  // wrap
                                                                                                                  // around
                    System.out.println("Same Row: \t" + firstChar + secondChar + "\n");
                } else if (firstCharLocation[1] == secondCharLocation[1]) { // Same column
                    // Shift both characters down the column
                    firstChar = cipherTable.get((firstCharLocation[0] - 1 + 5) % 5).get(firstCharLocation[1]); // Next
                                                                                                               // row,
                                                                                                               // wrap
                                                                                                               // around
                    secondChar = cipherTable.get((secondCharLocation[0] - 1 + 5) % 5).get(secondCharLocation[1]); // Next
                                                                                                                  // row,
                                                                                                                  // wrap
                                                                                                                  // around
                    System.out.println("Same Column: \t" + firstChar + secondChar + "\n");
                } else { // Different row and column (rectangle case)
                    // Apply the rectangle rule: swap columns
                    firstChar = cipherTable.get(firstCharLocation[0]).get(secondCharLocation[1]); // Same row as
                                                                                                  // firstChar, same
                                                                                                  // column as
                                                                                                  // secondChar
                    secondChar = cipherTable.get(secondCharLocation[0]).get(firstCharLocation[1]); // Same row as
                                                                                                   // secondChar, same
                                                                                                   // column as
                                                                                                   // firstChar
                    System.out.println("Overlay Case: \t" + firstChar + secondChar + "\n");
                }
                decyrpttext += firstChar;
                decyrpttext += secondChar;
            }
            System.out.println("Decrypt: " + decyrpttext);
        }
    }

    public static int[] findCharLocation(List<List<Character>> cipherTable, char target) {
        for (int row = 0; row < cipherTable.size(); row++) {
            for (int col = 0; col < cipherTable.get(row).size(); col++) {
                if (cipherTable.get(row).get(col) == target) {
                    return new int[] { row, col };
                }
            }
        }
        return null; // Return null if character not found
    }
}
