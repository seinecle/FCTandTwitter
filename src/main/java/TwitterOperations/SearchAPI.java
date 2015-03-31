/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TwitterOperations;

import Utils.NGramFinder;
import Utils.StatusCleaner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/*
 Copyright 2008-2013 Clement Levallois
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net


 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2013 Clement Levallois. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s): Clement Levallois

 */
public class SearchAPI extends Thread {

    String searchString;
    int countTweets = 0;
    long timeLastTweet;
    Twitter twitter;

    public SearchAPI() {
    }

    @Override
    public void run() {
        final MyOwnTwitterFactory factory = new MyOwnTwitterFactory();
        twitter = factory.createOneTwitterInstance();

        Keywords.initialize();

        BufferedReader br = null;
        BufferedWriter bw = null;
        try {

            bw = new BufferedWriter(new FileWriter("H:\\data\\datasets\\noms fichier central des theses\\docteus sur twitter.txt"));

            File fileDir = new File("H:\\data\\datasets\\noms fichier central des theses\\noms extraits du fichier central des theses.txt");

            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));

            String line;
            int counter = 0;
            long timer = System.currentTimeMillis();
            while ((line = br.readLine()) != null) {
                System.out.println(counter++);
                User user = isOnTwitter(line);
                if (counter % 170 == 0) {
                    System.out.println("entering sleeping mode for rate limits");
                    long timeBeforeFifteenMinutesWindowEnd = (15 * 60 * 1000) - (System.currentTimeMillis() - timer);
                    Thread.sleep(timeBeforeFifteenMinutesWindowEnd);
                    timer = System.currentTimeMillis();
                }
                if (user == null) {
                    continue;
                }
                bw = new BufferedWriter(new FileWriter("H:\\data\\datasets\\noms fichier central des theses\\docteurs sur twitter.txt", true));
                System.out.println("found a PhD on Twitter: @" + user.getScreenName() + ", " + user.getDescription());
                bw.write(line);
                bw.write("| @");
                bw.write(user.getScreenName());
                bw.write("|");
                //removing end of line characters and tabs from the description:
                String description = user.getDescription().replaceAll("[\r|\n|\t]", " ");
                bw.write(description);
                bw.newLine();
                bw.close();
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SearchAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | TwitterException ex) {
            Logger.getLogger(SearchAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            System.out.println("the sleeping thread got interrupted");
            Logger.getLogger(SearchAPI.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(SearchAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private User isOnTwitter(String nameFromFCTFile) throws TwitterException {

        /*
         * Steps of this function:
         * 1. We search for the FCT name on Twitter
         * 2. We loop through all the Twitter profiles returned by this search
         * 3. If the name of the Twitter user is indeed the one from the doctor in the FCT records (many operations on text cleaning here)
         * 4 and if the description of this Twitter user mentions a terms indicating a scientfific activity,
         *   then we have a match: this doctor is on Twitter.
         */
        ResponseList<User> users = twitter.searchUsers(nameFromFCTFile, 1);

        for (User user : users) {
            String nameFCT = StringUtils.stripAccents(nameFromFCTFile).toLowerCase();
            String nameTwitter = StringUtils.stripAccents(user.getName()).toLowerCase();

            //case of married women who appears in the FCT file with their maiden names. Example: Barbara Moreno Garcia
            // -> can be Barbara Moreno or Barbara Garcia
            // -> we will consider both cases
            String[] componentsFCTName = nameFCT.split(" ");
            List<String> variationsNamesFCT = new ArrayList();
            variationsNamesFCT.add(nameFCT);
            if (componentsFCTName.length > 2) {
                variationsNamesFCT.add(componentsFCTName[0] + " " + componentsFCTName[0]);
                variationsNamesFCT.add(componentsFCTName[0] + " " + componentsFCTName[2]);
            }

            for (String possibleNamesFCT : variationsNamesFCT) {

                //many users write their Twitter name as "Last Name, First Name"
                String nameTwitterLastNameFirst = null;
                String[] componentsTwitterName = nameTwitter.split(" ");
                if (componentsTwitterName.length > 1) {
                    nameTwitterLastNameFirst = nameTwitter.substring(0, nameTwitter.lastIndexOf(" ")) + " " + componentsTwitterName[componentsTwitterName.length - 1];
                }

                //we make a list of the name variations in the names of Twitter users, and we will then test for each of them whether they match the FCT name
                List<String> variationsTwitterName = new ArrayList();
                variationsTwitterName.add(nameTwitter);
                if (nameTwitterLastNameFirst != null) {
                    variationsTwitterName.add(nameTwitterLastNameFirst);
                }

                boolean matchDetected = false;
                for (String variationTwitterName : variationsTwitterName) {
                    // if there is a difference of two characters or less between the name in FCT and the user found on Twitter, we have a match
                    if (StringUtils.getLevenshteinDistance(possibleNamesFCT, variationTwitterName) <= 2) {
                        matchDetected = true;
                        break;
                    }
                }

                //if no match found, we leave this method
                if (!matchDetected) {
                    return null;
                }

                //but if a match was found, we will check further is there is any clue of a scientific activity in their profile description
                //removing punctuation signs so that INSA/CNRS becomes INSA CNRS
                String description = user.getDescription();
                description = StatusCleaner.removePunctuationSigns(description);

                //removing end of line characters and tabs from the description:
                description = description.replaceAll("[\r|\n|\t]", " ");

                //transforming the description of the Tiwtter user into a set of one word and two words expressions
                //to be able to find 2 words expressions like "post doc")
                NGramFinder nGramFinder = new NGramFinder(description);
                Set<String> nGramsInDescription = nGramFinder.runIt(2);

                //if one of the words of the description of the Twitter user matches one from our list of academic words, that's a match!
                for (String word : nGramsInDescription) {
                    if (Keywords.academicKeywords.contains(word.toLowerCase())) {
                        return user;
                    }
                }
            }
        }
        return null;
    }

}
