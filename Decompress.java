 /*
 * 
 * Description: This program decompresses the data made by the compressor with the 
 * extension .zzz. It then creates an ouput file with all the data gotten.
 * Authors: Eduardo Perez Rocha
 * 
 */
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class Decompress {
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s");
        String fileName = args[0];
        String extension = getFileExtension(fileName);

        if (!extension.equals("zzz")){
            System.out.println("FILE not VALID, please try again");
            System.exit(0);
        }
        decompress(fileName);
    }

    private static void decompress(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             DataInputStream dis = new DataInputStream(fis)) {
            
            long startTime = System.nanoTime();
            Logger logger = Logger.getLogger("MyLog");  
            FileHandler fh;

            fh = new FileHandler(filePath + ".txt.log");  
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);
            
            logger.info("Decompression for file " + filePath + "\n");
            logger.info("Decompression took \n");
            
            KWhashMap<Integer, String> dictionary = new KWhashMap<>();
            int dictSize = 256;
            for (int i = 0; i < dictSize; i++) {
                dictionary.put(i, String.valueOf((char) i));
            }
            
            int q = dis.readInt();  // Read the first code
            String result = dictionary.get(q);  // Output the first character
            System.out.print(result);

            int dotIndex = filePath.lastIndexOf('.');
            if (dotIndex > 0) {
                // Extract the substring before the last dot
                filePath = filePath.substring(0, dotIndex);
            }

            
            try (FileWriter new_file = new FileWriter(filePath)) {
                while (dis.available() > 0) {
                    int p = dis.readInt();
                    String textP;
                    if (dictionary.containsKey(p)) {
                        textP = dictionary.get(p);
                    } else {
                        // Special case: p is not in the dictionary
                        textP = dictionary.get(q) + dictionary.get(q).charAt(0);
                    }

                    new_file.write(textP);

                    // Update the dictionary
                    dictionary.put(dictSize++, dictionary.get(q) + textP.charAt(0));
                    q = p;
                }
            }

            long endTime = System.nanoTime();
            long totalTime = endTime - startTime;
            String formattedTime = String.format("%.5f seconds", totalTime / 1_000_000_000.0);
            logger.info("Decompression took " + formattedTime + "\n");
            logger.info("The table was doubled " + dictionary.getRehashCount() + " times \n");

        } catch (IOException e) {
            e.printStackTrace();
        }
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
