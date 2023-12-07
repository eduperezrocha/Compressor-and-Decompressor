/*
 *
 * Description: 
 * The compression program takes a text file as input, compresses it into a binary
 *  coded file with a .zzz extension, and logs the process in a .zzz.log file, detailing the compression 
 * ratio, time taken, dictionary entries, and rehash count. The decompression program reverses this process, 
 * taking a .zzz file as input and restoring it to its original text format, with a log file recording the 
 * decompression details. Both programs are expected to be robust, user-friendly, and capable of handling 
 * incorrect file name entries. The implementation involves the use of chained and ideal hashing 
 * techniques for the creation of dictionaries in the compression and decompression stages, respectively. 
 * 
 * 
 * Date: December 5th 2023
 * Authors: Eduardo Perez Rocha & Jack Neumann
 * 
 */

import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;


class Compress {
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s");

        if (args.length == 0) {
            System.out.println("No file provided.");
            return; 
        }

        String outputFilePath = args[0] + ".zzz";
        int dictSize = 256;
        int size = 0;
        KWhashMap<String, Integer> dictionary = new KWhashMap<>();

        Logger logger = Logger.getLogger("MyLog");  
        FileHandler fh;

        String filePath = args[0];

        try (FileOutputStream fos = new FileOutputStream(outputFilePath);
            DataOutputStream dos = new DataOutputStream(fos)) {

            // This block configure the logger with handler and formatter  
            fh = new FileHandler(outputFilePath + ".log");  
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);  

            String initial_size = Calculate_Size(filePath);
            logger.info("Compression of " + args[0] + "\n");
            logger.info("Compressed from ");
            logger.info("" + initial_size + " bytes to ");


            long startTime = System.nanoTime();
            String p = "";
            File file = new File(filePath);
            Scanner input = new Scanner(file);

            // Initialize dictionary with ASCII values
            for (int i = 0; i < 256; i++) {
                String ch = String.valueOf((char) i);
                dictionary.put(ch, i);
                size++;
            }
            
            while (input.hasNextLine()) {
                String inputStr = input.nextLine();

                for (char c : inputStr.toCharArray()) {
                    String pc = p + c;
                    if (dictionary.containsKey(pc)) {
                        p = pc;
                    } else {
                        if (dictionary.get(p) != null) {
                            dos.writeInt(dictionary.get(p)); // Write the code for 'p'
                        }
                        dictionary.put(pc, dictSize++); // Add 'pc' to the dictionary
                        size += 1;
                        p = "" + c; // Reset 'p' to the current character
                    }
                }

                // Handle end-of-line - depending on your requirements, you may need to handle this
                // For example, you might want to add a special symbol for end-of-line
                if (!p.isEmpty()) {
                    if (dictionary.get(p) != null) {
                            dos.writeInt(dictionary.get(p)); // Write the code for 'p'
                        }
                    p = ""; // Reset 'p' at the end of each line
                }
            }

            // Handle the last sequence if there is any
            if (!p.equals("")) {
                if (dictionary.get(p) != null) {
                            dos.writeInt(dictionary.get(p)); // Write the code for 'p'
                        }
            }

            long endTime = System.nanoTime();
            String finalSize = Calculate_Size(outputFilePath);
            logger.info("" + finalSize + " bytes \n");
            long totalTime = endTime - startTime;
            String formattedTime = String.format("%.5f seconds", totalTime / 1_000_000_000.0);
            logger.info("Compression took " + formattedTime + "\n");
            logger.info("Data written to " + outputFilePath + "\n");
            logger.info("The dictionary contains " + size + " total entries \n");
            logger.info("The table has rehashed " + dictionary.getRehashCount() + " times \n");
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        
    }
    public static String Calculate_Size(String filePath) {
        String size_str = "";
        long size_long = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Path path = Paths.get(filePath);
            size_long = Files.size(path);
            size_str = formatDataSize(size_long);
            return size_str;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size_str;
    }

    public static String formatDataSize(long bytes) {
        String formattedSize;
        if (bytes >= 1_099_511_627_776L) {  // At least a terabyte
            formattedSize = String.format("%.2f terabytes", bytes / 1_099_511_627_776.0);
        } else if (bytes >= 1_073_741_824) {  // At least a gigabyte
            formattedSize = String.format("%.2f gigabytes", bytes / 1_073_741_824.0);
        } else if (bytes >= 1_048_576) {  // At least a megabyte
            formattedSize = String.format("%.2f megabytes", bytes / 1_048_576.0);
        } else if (bytes >= 1024) {  // At least a kilobyte
            formattedSize = String.format("%.2f kilobytes", bytes / 1024.0);
        } else {
            formattedSize = bytes + " bytes";  // Less than a kilobyte
        }
        return formattedSize;
    }
    
}
