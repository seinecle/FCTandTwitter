/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

public class StatusCleaner {

    public static String clean(String status) {
        if (status == null) {
            return "";
        }
        status = status.replace("...", " ");
        status = status.replace(",", " ");
        status = status.replace("..", " ");
//            System.out.println(status);
        status = status.replaceAll("http[^ ]*", " ");
//        status = status.replaceAll("\".*\"", " ");
        status = status.replaceAll("http.*[\r|\n]*", " ");
        status = status.replaceAll(" +", " ");

        return status;
    }

    public static String removePunctuationSigns(String string) {
        if (string == null) {
            return "";
        }
        string = string.replace("'s", " ");
        string = string.replace("’s", " ");
        string = string.replace("l'", " ");
        string = string.replace("l’", " ");
        String punctuation = "!?.@'’`+<>\"«»:-+,|$;_/~&()[]{}#=*";
        char[] chars = punctuation.toCharArray();
        for (char currChar : chars) {
            string = string.replace(String.valueOf(currChar), " ");
        }
        return string.trim();
    }

}
