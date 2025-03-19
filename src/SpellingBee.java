import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Spelling Bee
 *
 * This program accepts an input of letters. It prints to an output file
 * all English words that can be generated from those letters.
 *
 * For example: if the user inputs the letters "doggo" the program will generate:
 * do
 * dog
 * doggo
 * go
 * god
 * gog
 * gogo
 * goo
 * good
 *
 * It utilizes recursion to generate the strings, mergesort to sort them, and
 * binary search to find them in a dictionary.
 *
 * @author Zach Blick, Angelica Chou
 *
 * Written on March 5, 2023 for CS2 @ Menlo School
 *
 * DO NOT MODIFY MAIN OR ANY OF THE METHOD HEADERS.
 */
public class SpellingBee {

    private String letters;
    private ArrayList<String> words;
    public static final int DICTIONARY_SIZE = 143091;
    public static final String[] DICTIONARY = new String[DICTIONARY_SIZE];
    private Scanner s = new Scanner (System.in);

    public SpellingBee(String letters) {
        this.letters = letters;
        words = new ArrayList<String>();
    }

    // Generate all permutations of the word input
    public void generate() {
        generateHelper("", letters);
    }

    // Recursive method that helps generate()
    public void generateHelper(String newWord, String letters) {
        // Separates string into two parts, like the tree in the example
        String left;
        String right;
        // When new word is generated, add it to the arrayList
        if (!(newWord.isEmpty())) {
            words.add(newWord);
        }
        // Base case, when the word has no more letters to change
        if (letters.isEmpty()) {
            return;
        }
        //Split into two "array", like the tree in the example
        for (int i = 0; i < letters.length(); i++) {
            left = newWord + letters.charAt(i);
            right = letters.substring(0, i) + letters.substring(i + 1);
            generateHelper(left, right);
        }
    }

    public void sort() {
        // Reassign words to what when it is sorted
        words = mergeSort(this.words);
    }
    public ArrayList<String> merge(ArrayList<String> right, ArrayList<String> left) {
        // Make a arraylist with all the terms combined
        ArrayList <String> combined = new ArrayList <String>();
        int rightIndex = 0;
        int leftIndex = 0;

        // Keep comparing values when both arrayLists have not run out
        while (leftIndex < left.size() && rightIndex < right.size()) {
            // If right string is a smaller value tha left string, swap them
            if (right.get(rightIndex).compareTo(left.get(leftIndex)) <= 0) {
                // Add new sorted values into new combined arrayList
                combined.add(right.get(rightIndex));
                rightIndex++;
            }
            else {
                // If left string is still bigger, keep it in its place and add it to combined arrayList
                combined.add(left.get(leftIndex));
                leftIndex++;
            }
        }
        // If the right arrayList runs out, keep comparing values when the left arrayList doesn't
        while (leftIndex < left.size()) {
            combined.add(left.get(leftIndex));
            leftIndex++;
        }
        // If the left arrayList runs out, keep comparing values when the right arrayList doesn't
        while (rightIndex < right.size()) {
            combined.add(right.get(rightIndex));
            rightIndex++;
        }
        return combined;
    }

    public ArrayList<String> mergeSort(ArrayList<String> words) {
        // Base case, if there are no other words to divide
        if (words.size() <= 1) {
            return words;
        }
        // Get the middle
        int mid = words.size() / 2;
        ArrayList<String> right = new ArrayList<String>();
        ArrayList<String> left = new ArrayList<String>();
        // Divide values of arrayLists into two different arrayLists
        for (int i = 0; i < mid; i++) {
            left.add(words.get(i));
        }
        for (int i = mid; i < words.size(); i++) {
            right.add(words.get(i));
        }
        // Recursive step
        right = mergeSort(right);
        left = mergeSort(left);
        return merge(right, left);
    }

    // Removes duplicates from the sorted list.
    public void removeDuplicates() {
        int i = 0;
        while (i < words.size() - 1) {
            String word = words.get(i);
            if (word.equals(words.get(i + 1))) {
                words.remove(i + 1);
            }
            else {
                i++;
            }
        }
    }

    // Using recursive method to check if permutated word is a real word in dictionary
    public void checkWords() {
        for (int i = 0; i < this.words.size(); i++) {
            if (!binarySearch(DICTIONARY, words.get(i))) {
                this.words.remove(i);
                i--;
            }
        }
    }

    // Recursive function to search through dictionary
    public boolean binarySearch(String[] dictionary, String lookingFor) {
        // Parameters of what is being searched
        int low = 0;
        int high = dictionary.length - 1;

        while (low <= high) {
            // Gets the middle of array
            int mid = low + (high - low) / 2;
            // If the word is in the dictionary
            if (lookingFor.compareTo(dictionary[mid]) == 0) {
                return true;
            }
            // If the word being looked for has a lesser value than the dictionary value
            else if (lookingFor.compareTo(dictionary[mid]) < 0) {
                // Looking in the "left" array
                high = mid - 1;
            }
            else {
                // Looking in the "right" array when value searched is higher
                low = mid + 1;
            }
        }
        return false;
    }

    // Prints all valid words to wordList.txt
    public void printWords() throws IOException {
        File wordFile = new File("Resources/wordList.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(wordFile, false));
        for (String word : words) {
            writer.append(word);
            System.out.println(word);
            writer.newLine();
        }
        writer.close();
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    public SpellingBee getBee() {
        return this;
    }

    public static void loadDictionary() {
        Scanner s;
        File dictionaryFile = new File("Resources/dictionary.txt");
        try {
            s = new Scanner(dictionaryFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open dictionary file.");
            return;
        }
        int i = 0;
        while(s.hasNextLine()) {
            DICTIONARY[i++] = s.nextLine();
        }
    }

    public static void main(String[] args) {
        // Prompt for letters until given only letters.
        Scanner s = new Scanner(System.in);
        String letters;
        do {
            System.out.print("Enter your letters: ");
            letters = s.nextLine();
        }
        while (!letters.matches("[a-zA-Z]+"));

        // Load the dictionary
        SpellingBee.loadDictionary();

        // Generate and print all valid words from those letters.
        SpellingBee sb = new SpellingBee(letters);
        sb.generate();
        sb.sort();
        sb.removeDuplicates();
        sb.checkWords();
        try {
            sb.printWords();
        } catch (IOException e) {
            System.out.println("Could not write to output file.");
        }
        s.close();
    }
}
