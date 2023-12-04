
/* 
 *
 * Compression:
 * 1. Initialize the dict for all characters that may occur in the next file
 * 2. loop
 * 2.1 Find the longest prefix "p" of the uncoded part of the input file that is in the dict.
 * 2.2 Output the code
 * 2.3 If there is a next charc in the input file then p + c is assigned the next
 * code, insert the pair into the dict
 * 
 * Decompression:
 * 1. Initiliaze the dict as in compression
 * 2. output the text corresponding to the first code
 * 3. for all other codes p in the coded file do:
 *      assume q is the code beforep
 *      if p is in the dict
 *          extract text (p) from the dict
 *          output text(p)
 *          insert (next code, text (q), F(text(q) into the dict
 *      else
 *          output text(q F(text(q)))
 *          insert(p, text(q) FC(text(q)))
 *              into the dict
 */

/*
 *
 * Description: 
 * This program compresses an input file using Hash Maps and an algorithm to reduce
 * the size and format.
 * 
 * 
 * Date: December 5th 2023
 * Authors: Eduardo Perez Rocha
 * 
 */

import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


class Compress {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No file provided.");
            return; // Exit 
        }
        String outputFilePath = args[0] + ".zzz";
        int dictSize = 256;
        String test = "aaabaa";
        KWhashMap<String, Integer> dictionary = new KWhashMap<>();

        String filePath = args[0];
   
        long initial_size = Calculate_Size(filePath);
        System.out.println("Compression of " + args[0]);
        System.out.print("Compressed from ");
        System.out.print("" + initial_size + " bytes to ");

        try (FileOutputStream fos = new FileOutputStream(outputFilePath);
             DataOutputStream dos = new DataOutputStream(fos)) {
            long startTime = System.nanoTime();
            String p = "";
            File file = new File(filePath);
            Scanner input = new Scanner(file);
            String Input_str = ""; // Declare the variable once

            while (input.hasNextLine()) {
                Input_str = input.nextLine();
            }
            // Initialize dictionary with ASCII values
            for (char ch : Input_str.toCharArray()) {
                String strChar = String.valueOf(ch);
                if (!dictionary.containsKey(strChar)) {
                    dictionary.put(strChar, (int) ch);
                }
            }
           
            for (char c : Input_str.toCharArray()) {
                String pc = p + c;
                if (dictionary.containsKey(pc)) {
                    p = pc;
                } else {
                    dos.writeInt(dictionary.get(p)); // Write the code for 'p'
                    dictionary.put(pc, dictSize++); // Add 'pc' to the dictionary
                    p = "" + c; // Reset 'p' to the current character
                }
            }

            // Handle the last sequence
            if (!p.equals("")) {
                dos.writeInt(dictionary.get(p));
            }

            long endTime   = System.nanoTime();
            long final_size = Calculate_Size(outputFilePath);
            System.out.print("" + final_size + " bytes \n");
            long totalTime = endTime - startTime;
            String formattedTime;
            formattedTime = String.format("%.5f seconds", totalTime / 1_000_000_000.0);
            System.out.println("Compression took " + formattedTime);
            System.out.println("Data written to " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static long Calculate_Size(String filePath) {
        long size = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Path path = Paths.get(filePath);
            size = Files.size(path);
            return size;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }
}
