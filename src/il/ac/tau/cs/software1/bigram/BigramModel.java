package il.ac.tau.cs.software1.bigram;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;


public class BigramModel {
    public static final int MAX_VOCABULARY_SIZE = 14500;
    public static final String VOC_FILE_SUFFIX = ".voc";
    public static final String COUNTS_FILE_SUFFIX = ".counts";
    public static final String SOME_NUM = "some_num";
    public static final int ELEMENT_NOT_FOUND = -1;

    String[] mVocabulary;
    int[][] mBigramCounts;

    // DO NOT CHANGE THIS !!!
    public void initModel(String fileName) throws IOException {
        mVocabulary = buildVocabularyIndex(fileName);
        mBigramCounts = buildCountsArray(fileName, mVocabulary);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods - already implemented for you. Use them!
    ///////////////////////////////////////////////////////////////////////////

    /*
     * Converts a single token (a whitespace-free piece of text from the file)
     * into the form in which it is stored in the vocabulary:
     *   - a token with at least one English letter  -> the token in lowercase
     *   - a token made only of digits (an integer)  -> SOME_NUM
     *   - anything else is not a legal word         -> null
     *
     * @pre: token != null
     */
    public static String normalizeWord(String token) {
        boolean hasLetter = false;
        boolean allDigits = !token.isEmpty();
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                hasLetter = true;
            }
            if (c < '0' || c > '9') {
                allDigits = false;
            }
        }
        if (hasLetter) {
            return token.toLowerCase(Locale.ROOT);
        }
        if (allDigits) {
            return SOME_NUM;
        }
        return null;
    }

    /*
     * @pre: vocabulary != null
     * @post: $ret = the index of word in vocabulary, or ELEMENT_NOT_FOUND if
     * word is null or does not appear in it
     */
    public static int findIndex(String[] vocabulary, String word) {
        if (word == null) {
            return ELEMENT_NOT_FOUND;
        }
        for (int i = 0; i < vocabulary.length; i++) {
            if (word.equals(vocabulary[i])) {
                return i;
            }
        }
        return ELEMENT_NOT_FOUND;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Questions 1 - 5: implement these
    ///////////////////////////////////////////////////////////////////////////

    /*
     * @pre: fileName is a legal path to an existing, readable text file
     * @post: mVocabulary = prev(mVocabulary)
     * @post: mBigramCounts = prev(mBigramCounts)
     * @post: $ret is a new array holding the different legal words of the file, in their
     *        normalized form (see normalizeWord), ordered by first appearance
     * @post: $ret contains no duplicates and no null cells
     * @post: $ret.length = min(number of different legal words in the file, MAX_VOCABULARY_SIZE)
     */
    public String[] buildVocabularyIndex(String fileName) throws IOException { // Question 1
        String[] newVocabulary = new String[MAX_VOCABULARY_SIZE];
        File fromFile = new File(fileName);
        BufferedReader bf = new BufferedReader(new FileReader(fromFile));
        String line;
        int i = 0;
        while ((i < MAX_VOCABULARY_SIZE) && (line = bf.readLine()) != null) {
            String[] tokens = line.trim().split("\\s+");
            for (String token : tokens) {
                if (i < MAX_VOCABULARY_SIZE) {
                    String word = normalizeWord(token);
                    if (word != null && !containsWord(newVocabulary, word)) {
                        newVocabulary[i] = word;
                        i++;
                    }
                } else {
                    break;
                }
            }
        }
        String[] result = new String[i];
        for (int j = 0; j < i; j++) {
            result[j] = newVocabulary[j];
        }
        bf.close();
        return result;
    }

    /*
     * @pre: vocabulary != null
     * @pre: the words in vocabulary are stored contiguously from index 0, and every
     *       cell after the last word is null (no null "gaps" between words)
     * @post: $ret = true if word appears in vocabulary, false otherwise
     */
    private static boolean containsWord(String[] vocabulary, String word) {
        for (int i = 0; i < vocabulary.length; i++) {
            if (vocabulary[i] == null) {
                return false;
            }
            if (vocabulary[i].equals(word)) {
                return true;
            }
        }
        return false;
    }

    /*
     * @pre: fileName is a legal path to an existing, readable text file
     * @pre: vocabulary != null, contains no null cells and no duplicates
     * @post: mVocabulary = prev(mVocabulary)
     * @post: mBigramCounts = prev(mBigramCounts)
     * @post: $ret is a new vocabulary.length x vocabulary.length array, where $ret[x][y] =
     *        the number of times vocabulary[y] appeared immediately after vocabulary[x]
     *        in the same line of the file (a token that is not in vocabulary breaks the pair)
     */
    public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException { // Question 2
        int[][] counts = new int[vocabulary.length][vocabulary.length];
        File fromFile = new File(fileName);
        BufferedReader bf = new BufferedReader(new FileReader(fromFile));
        String line, curr, after;
        int curr_index, after_index;
        while( (line = bf.readLine()) != null){
            String[] tokens = line.trim().split("\\s+");
            curr = normalizeWord(tokens[0]);
            curr_index = findIndex(vocabulary, curr);
            for (int i = 1; i < tokens.length; i++) {
                after = normalizeWord(tokens[i]);
                after_index = findIndex(vocabulary, after);
                if (!(curr_index == ELEMENT_NOT_FOUND || after_index == ELEMENT_NOT_FOUND)){
                    counts[curr_index][after_index]++;
                }
                curr_index=after_index;
            }
        }       
        bf.close();
        return counts;
    }

    /*
     * @pre: the method initModel was called (the language model is initialized)
     * @pre: fileName is a legal file path
     * @pre: fileName has no suffix (the suffixes are added by this method)
     * @post: mVocabulary = prev(mVocabulary)
     * @post: mBigramCounts = prev(mBigramCounts)
     * @post: the file fileName + VOC_FILE_SUFFIX holds the line "<n> words" (n = mVocabulary.length),
     *        followed by one line "<index>,<word>" per vocabulary word, in increasing index order
     * @post: the file fileName + COUNTS_FILE_SUFFIX holds one line "<i>,<j>:<count>" for every
     *        pair with mBigramCounts[i][j] != 0, ordered by i and then by j
     * @post: every line in both files ends with System.lineSeparator(), including the last one
     *        (an existing file with the same name is overwritten)
     */
    public void saveModel(String fileName) throws IOException { // Question 3
        File newVoc = new File(fileName+VOC_FILE_SUFFIX);
        File newCounts = new File(fileName+COUNTS_FILE_SUFFIX);
        BufferedWriter bw_voc = new BufferedWriter(new FileWriter(newVoc));
        BufferedWriter bw_counts = new BufferedWriter(new FileWriter(newCounts));
        //creating .voc
        bw_voc.write(mVocabulary.length + " words" + System.lineSeparator());
        int i = 0;
        for(String word: mVocabulary){
            bw_voc.write(i + "," + word + System.lineSeparator());
            i++;
        }
        //creating .counts
        for (int j = 0; j < mBigramCounts.length; j++) {
            for (int k=0; k<mBigramCounts[j].length;k++){
                int curr_count = mBigramCounts[j][k];
                if (curr_count!=0){
                    bw_counts.write(j+","+k+":"+curr_count+System.lineSeparator());
                }
            }         
        }
        bw_counts.close();
        bw_voc.close();
    }

    /*
     * @pre: fileName is a legal file path
     * @pre: fileName has no suffix (the suffixes are added by this method)
     * @pre: the files fileName + VOC_FILE_SUFFIX and fileName + COUNTS_FILE_SUFFIX exist and are
     *       in the format written by saveModel (not necessarily written by this class)
     * @post: mVocabulary is a new array, where mVocabulary[i] = the word in the line "i,<word>"
     *        of the .voc file (the word is everything after the first comma)
     * @post: mBigramCounts is a new mVocabulary.length x mVocabulary.length array, where
     *        mBigramCounts[i][j] = count for every line "i,j:count" of the .counts file,
     *        and 0 for every pair that does not appear there
     * @post: the arrays that mVocabulary and mBigramCounts referred to before the call
     *        are not modified
     */
    public void loadModel(String fileName) throws IOException { // Question 4
        File mVoc_src = new File(fileName+VOC_FILE_SUFFIX);
        File bigram_src = new File(fileName+COUNTS_FILE_SUFFIX);
        //create new mVocabulary
        BufferedReader mv_src = new BufferedReader(new FileReader(mVoc_src));
        String firstLine = mv_src.readLine();
        int size =Integer.parseInt(firstLine.split(" ")[0]);
        String[] m1Voc = new String[size];
        String line_voc;
         while( (line_voc = mv_src.readLine()) != null){
            String[] next_word = line_voc.split("," , 2);
            m1Voc[Integer.parseInt(next_word[0])] = next_word[1];
        }
        mv_src.close();
        //create new mBigramCounts
        BufferedReader bg_src = new BufferedReader(new FileReader(bigram_src));
        int[][] mBigram1 = new int[size][size];
        String line_bigram;
        while( (line_bigram = bg_src.readLine()) != null){
            String[] info = line_bigram.split("[,:]");
            mBigram1[Integer.parseInt(info[0])][Integer.parseInt(info[1])] = Integer.parseInt(info[2]);
        }
        bg_src.close();
        //finished
        mVocabulary = m1Voc;
        mBigramCounts = mBigram1;   
    }

    /*
     * @pre: sentence is in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @pre: each two words in the sentence are separated with a single space
     * @post: if sentence is probable, according to the model, $ret = true, else, $ret = false
     */
    public boolean isLegalSentence(String sentence) { // Question 5
        // TODO: implement
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Queries on the model - already implemented for you.
    // They start working once Questions 1 - 2 are done.
    ///////////////////////////////////////////////////////////////////////////

    /*
     * @pre: word is in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
     */
    public int getWordIndex(String word) {
        return findIndex(mVocabulary, word);
    }

    /*
     * @pre: word1, word2 are in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
     * exist in the vocabulary, $ret = 0
     */
    public int getBigramCount(String word1, String word2) {
        int index1 = getWordIndex(word1);
        int index2 = getWordIndex(word2);
        if (index1 == ELEMENT_NOT_FOUND || index2 == ELEMENT_NOT_FOUND) {
            return 0;
        }
        return mBigramCounts[index1][index2];
    }

    /*
     * @pre: word is in lowercase, and is in mVocabulary
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = the word with the lowest vocabulary index that appears most frequently after word
     * (if a bigram starting with word was never seen, $ret will be null)
     */
    public String getMostFrequentProceeding(String word) {
        int[] row = mBigramCounts[getWordIndex(word)];
        int best = ELEMENT_NOT_FOUND;
        for (int i = 0; i < row.length; i++) {
            if (row[i] > 0 && (best == ELEMENT_NOT_FOUND || row[i] > row[best])) {
                best = i;
            }
        }
        return best == ELEMENT_NOT_FOUND ? null : mVocabulary[best];
    }

    /*
     * @pre: arr1.length = arr2.length
     * @post: if arr1 or arr2 are only filled with zeros, $ret = -1, otherwise calculates CosineSim
     */
    public static double calcCosineSim(int[] arr1, int[] arr2) {
        double dot = 0;
        double norm1 = 0;
        double norm2 = 0;
        for (int i = 0; i < arr1.length; i++) {
            dot += (double) arr1[i] * arr2[i];
            norm1 += (double) arr1[i] * arr1[i];
            norm2 += (double) arr2[i] * arr2[i];
        }
        if (norm1 == 0 || norm2 == 0) {
            return -1;
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /*
     * @pre: word is in vocabulary
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w)
     * among all the other words in vocabulary (the lowest index wins a tie)
     */
    public String getClosestWord(String word) {
        int wordIndex = getWordIndex(word);
        int best = ELEMENT_NOT_FOUND;
        double bestSim = 0;
        for (int i = 0; i < mVocabulary.length; i++) {
            if (i == wordIndex) {
                continue;
            }
            double sim = calcCosineSim(mBigramCounts[wordIndex], mBigramCounts[i]);
            if (best == ELEMENT_NOT_FOUND || sim > bestSim) {
                best = i;
                bestSim = sim;
            }
        }
        // A vocabulary with a single word: the closest word is the word itself
        return best == ELEMENT_NOT_FOUND ? word : mVocabulary[best];
    }
}
