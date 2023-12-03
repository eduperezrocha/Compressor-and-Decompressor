
/* Compression:
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

//COMPRESS
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class Compress {
    public static void main(String[] args) {
        int dictSize = 256;
        String test = "aaabaa";
        KWhashMap<String, Integer> dictionary = new KWhashMap<>();

        // Initialize dictionary with ASCII values
        for (char ch : test.toCharArray()) {
            String strChar = String.valueOf(ch);
            if (!dictionary.containsKey(strChar)) {
                dictionary.put(strChar, (int) ch);
            }
        }

        String p = "";
        String filePath = "output.zzz";

        try (FileOutputStream fos = new FileOutputStream(filePath);
             DataOutputStream dos = new DataOutputStream(fos)) {

            for (char c : test.toCharArray()) {
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

            System.out.println("Data written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
