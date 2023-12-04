 /*
 *
 * Description: 
 * This program creates a dictionary with an entry of name and phone number.
 * It can save the file in multi-line format, remove an entry, search or even display 
 * all entries that were added. These all can be done with an easy to use MENU.
 * 
 * Date: December 5th 2023
 * Authors: Eduardo Perez Rocha
 * 
 */
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Decompress {
    public static void main(String[] args) {
        String fileName = args[0];
        String extension = getFileExtension(fileName);
        
        if (!extension.equals("zzz")){
            System.out.println("FILE not VALID, please try again");
            System.exit(0);
        }
        decompress(fileName);
        // You might want to use 'extension' and 'fileName' here for further processing
    }

    private static void decompress(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             DataInputStream dis = new DataInputStream(fis)) {

            KWhashMap<Integer, String> dictionary = new KWhashMap<>();
            int dictSize = 256;
            for (int i = 0; i < dictSize; i++) {
                dictionary.put(i, String.valueOf((char) i));
            }

            int q = dis.readInt();  // Read the first code
            String result = dictionary.get(q);  // Output the first character
            System.out.print(result);

            while (dis.available() > 0) {
                int p = dis.readInt();
                String textP;
                if (dictionary.containsKey(p)) {
                    textP = dictionary.get(p);
                } else {
                    // Special case: p is not in the dictionary
                    textP = dictionary.get(q) + dictionary.get(q).charAt(0);
                }
                System.out.print(textP);

                // Update the dictionary
                dictionary.put(dictSize++, dictionary.get(q) + textP.charAt(0));
                q = p;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileName() {
        System.out.print("Please enter the name of the file: ");
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }

    public static void readBinaryFile(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             DataInputStream dis = new DataInputStream(fis)) {

            // Example: Read integers from the file until the end
            while (dis.available() > 0) {
                int data = dis.readInt();
                System.out.println("Read integer: " + data);
                // Process the data as needed
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot > 0) { // Ensure dot is not the first character
            return fileName.substring(lastIndexOfDot + 1);
        } else {
            return null; // No extension found
        }
    }
}
