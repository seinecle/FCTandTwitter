/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author C. Levallois adapted from
 * http://stackoverflow.com/questions/3656762/n-gram-generation-from-a-sentence
 */
public class NGramFinder {

    private Set<String> freqSetN;
    private String[] words;
    private Set<String> nGrams;
    private String stringOriginal;

    public NGramFinder(String string) {
        this.stringOriginal = string;
    }

    public Set<String> runIt(int maxgram) {

        String string = stringOriginal;

        nGrams = new HashSet();
        nGrams.addAll(run(string, maxgram));

        return nGrams;

    }

    private Set<String> ngrams(int n, String str) {

        Set<String> setToReturn = new HashSet();
        words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++) {
            setToReturn.add(concat(words, i, i + n, n));
        }

        return setToReturn;

    }

    private String concat(String[] words, int start, int end, int ngram) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            sb.append(i > start ? " " : "").append(words[i]);
        }
        return sb.toString();
    }

    private Set<String> run(String toBeParsed, int nGram) {
        freqSetN = new HashSet();

        for (int n = 1; n <= nGram; n++) {
            freqSetN.addAll(ngrams(n, toBeParsed));
        }
        //System.out.println(freqList.get(i));
        return freqSetN;
    }
}
