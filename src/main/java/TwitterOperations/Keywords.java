/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TwitterOperations;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.TwitterException;

/**
 *
 * @author LEVALLOIS
 */
public class Keywords {

    static Set<String> academicKeywords;

    public static void initialize() {
        BufferedReader br = null;
        academicKeywords = new HashSet();
        try {
            br = new BufferedReader(new FileReader("H:\\data\\datasets\\noms fichier central des theses\\academic keywords.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                academicKeywords.add(line);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SearchAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SearchAPI.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(SearchAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
