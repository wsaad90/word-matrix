package wordmatrix;

import java.util.*;
import java.io.*;

/**
 * Program that allows the user to build a matrix and then fill in the values.
 * Each row and column must be a registered word in the dictionary. The program
 * checks if each row and column is a valid word in the dictionary. Be sure to
 * change variable "filePath" to be the correct file path of the
 * myDictionary.txt file.
 *
 * @author Waleed Saad
 */
public class WordMatrix {

    static List<String> dictionary = new ArrayList<>(); //list containing all dictionary words

    public static void main(String[] args) {
        char[][] grid = buildFoundation();                                  //building an empty matrix
        constructDictionary("resources/myDictionary.txt", grid.length);  //contructs the list of words
        fillMatrix(grid, grid.length);                                //filling the matrix with values
        System.out.println("\nValid Matrix: " + checkMatrix(grid));            //check if valid matrix
        printMatrix(grid);                                                  //neatly prints the matrix
    }

    /**
     * Method that fills the matrix. Makes sure the word is a valid 
     * dictionary word and is the same length as the matrix.
     *
     * @param grid - our matrix
     * @param gridSize - the size of the matrix
     * @return - a filled matrix of user entered values
     */
    public static char[][] fillMatrix(char[][] grid, int gridSize) {
        int loopControl = 0;
        do {
            char[] wordSplit = dictionary.get((int) (Math.random() * dictionary.size())).toCharArray();
            for (int i = 0; i < wordSplit.length; i++) {
                grid[loopControl][i] = wordSplit[i];
            }
            loopControl++;
        } while (loopControl != gridSize);
        
        if (checkMatrix(grid)) {
            return grid;
        } else {
            return fillMatrix(grid, gridSize);
        }
    }

    /**
     * Checks if the associated word is in the dictionary (list of words).
     *
     * @param key - the word we are checking for
     * @return - true if exists, false if doesn't exist
     */
    public static boolean checkWord(String key) {
        for (int i = 0; i < dictionary.size(); i++) {      //traverse dictionary
            if (dictionary.get(i).equals(key)) {           //if input is in dictionary
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the matrix is valid. A valid matrix is when each row and column
     * is a registered word in the dictionary.
     *
     * @param grid - the matrix we're checking
     * @return - the validity of the matrix
     */
    public static boolean checkMatrix(char[][] grid) {
        //flags
        boolean rowCheck = false;
        boolean colCheck = false;

        //puts every row of chars into a word array
        StringBuilder rows = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            rows.append(grid[i]);
            rows.append(",");
        }
        String[] rowsForChecking = rows.toString().split(",");

        //puts every column of chars into a word array
        StringBuilder cols = new StringBuilder();
        for (int i = 0; i < rowsForChecking.length; i++) {
            for (int j = 0; j < rowsForChecking.length; j++) {
                cols.append(rowsForChecking[j].charAt(i));
            }
            cols.append(",");
        }
        String[] colsForChecking = cols.toString().split(",");

        //printing contents of rows & columns for displaying words
        System.out.println("");
        for (int i = 0; i < grid.length; i++) {
            System.out.println("ROW " + (i + 1) + ": " + rowsForChecking[i]);
            System.out.println("COL " + (i + 1) + ": " + colsForChecking[i]);
        }

        //checking if all rows are registered words in the dictionary
        for (int i = 0; i < rowsForChecking.length; i++) {
            for (int j = 0; j < dictionary.size(); j++) {
                if (checkWord(rowsForChecking[i])) {
                    rowCheck = true;
                } else {
                    rowCheck = false;
                }
            }
        }

        //checking if all columns are registered words in the dictionary
        outer:
        for (int i = 0; i < colsForChecking.length; i++) {
            for (int j = 0; j < dictionary.size(); j++) {
                if (checkWord(colsForChecking[i])) {
                    colCheck = true;
                } else {
                    colCheck = false;
                }
            }
        }

        //checking for duplicates in the matrix
        if (checkForDupes(rowsForChecking, colsForChecking)) {
            rowCheck = false;
            colCheck = false;
        }

        return rowCheck && colCheck == true;
    }

    /**
     * Checks for duplicate words between two String arrays.
     *
     * @param rows - row words of the char matrix
     * @param cols - col words of the char matrix
     * @return true if duplicates found, false if not
     */
    public static boolean checkForDupes(String[] rows, String[] cols) {
        //checking for duplicates rows by cols
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < cols.length; j++) {
                if (rows[i].equals(cols[j])) {
                    System.out.println("\nDuplicate word detected.");
                    return true;
                }
            }
        }
        //checking for duplicates rows by rows
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows.length; j++) {
                if (rows[i].equals(rows[j])) {
                    if (i != j) {
                        System.out.println("\nDuplicate word detected.");
                        return true;
                    }
                }
            }
        }
        //checking for duplicates cols by cols
        for (int i = 0; i < cols.length; i++) {
            for (int j = 0; j < cols.length; j++) {
                if (cols[i].equals(cols[j])) {
                    if (i != j) {
                        System.out.println("\nDuplicate word detected.");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Takes a text file and puts every word in to a List using spaces as
     * delimiter. Only adds words that are of a specific size decided by the
     * user.
     *
     * @param path - file path of the dictionary text file
     * @param gridSize - the NxN size of the matrix
     */
    public static void constructDictionary(String path, int gridSize) {
        //grabbing the dictionary file from the resources directory
        ClassLoader classLoader = new WordMatrix().getClass().getClassLoader();
        File file = new File(classLoader.getResource(path).getFile());

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                //adds every single word from File -> List given specified word size
                for (String token : tokens) {
                    if (token.length() == gridSize) {
                        dictionary.add(token);
                    }
                }
            }
        } catch (FileNotFoundException ex) { //file path is more than likely incorrect
            System.out.println("404: FILE NOT FOUND. Check the filePath variable for correct file path.");
            System.exit(0);
        } catch (IOException ex) {           //bad user input
            System.out.println("400: BAD REQUEST. There was in I/O Exception.");
            System.exit(0);
        }
    }

    /**
     * Builds the physical matrix without filling in values. Checks for and
     * even, limited in size matrix.
     *
     * @return - the foundation of the matrix without any values
     */
    public static char[][] buildFoundation() {
        Scanner intInput = new Scanner(System.in);
        int size;
        char[][] grid;
        do {                //keeping matrix between 3x3 - 5x5 for simplicity
            System.out.print("Enter size of matrix (between 3 - 5): ");
            size = intInput.nextInt();
            grid = new char[size][size]; //lets keep an even matrix shall we?
        } while (size < 3 || size > 5);
        return grid;
    }

    /**
     * Neatly prints out the matrix with the values filled.
     *
     * @param grid - matrix we're printing
     */
    public static void printMatrix(char[][] grid) {
        System.out.println("");
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println("");
        }
    }
}
