/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TwitterOperations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                bw = new BufferedWriter(new FileWriter("H:\\data\\datasets\\noms fichier central des theses\\docteus sur twitter.txt", true));
                System.out.println("found a PhD on Twitter: @" + user.getScreenName() + ", " + user.getDescription());
                bw.write(line);
                bw.write("| @");
                bw.write(user.getScreenName());
                bw.write("|");
                bw.write(user.getDescription());
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

    private User isOnTwitter(String line) throws TwitterException {
        ResponseList<User> users = twitter.searchUsers(line, 1);
        for (User user : users) {
            String description = user.getDescription();
            for (String word : description.split(" ")) {
                if (Keywords.academicKeywords.contains(word.toLowerCase())) {
                    return user;
                }
            }
        }
        return null;
    }

}
